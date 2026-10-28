//Gemini quiz / Jarvis
package com.example.projetomarvel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

/** The existing screens call this on a background executor. No credentials or responses are logged. */
final class GeminiClient {
    private static final String ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.8-flash:generateContent";
    static final class Turn {
        final String role, text;
        Turn(String role, String text) { this.role = role; this.text = text; }
    }

    boolean configured() { return !BuildConfig.GEMINI_API_KEY.trim().isEmpty(); }

    String generate(String instruction, List<Turn> turns, JSONObject responseFormat) throws Exception {
        if (!configured()) throw new IOException("Gemini unavailable");
        JSONObject request = new JSONObject();
        request.put("systemInstruction", new JSONObject().put("parts",
                new JSONArray().put(new JSONObject().put("text", instruction))));
        JSONArray contents = new JSONArray();
        for (Turn turn : turns) {
            if (!"user".equals(turn.role) && !"model".equals(turn.role)) continue;
            contents.put(new JSONObject().put("role", turn.role).put("parts",
                    new JSONArray().put(new JSONObject().put("text", turn.text))));
        }
        request.put("contents", contents);
        JSONObject config = new JSONObject().put("temperature", responseFormat == null ? 0.55 : 0.8);
        if (responseFormat != null) config.put("responseFormat", responseFormat);
        request.put("generationConfig", config);

        HttpURLConnection connection = (HttpURLConnection) new URL(ENDPOINT).openConnection();
        try {
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            connection.setRequestProperty("x-goog-api-key", BuildConfig.GEMINI_API_KEY);
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(20000);
            connection.setDoOutput(true);
            byte[] payload = request.toString().getBytes(StandardCharsets.UTF_8);
            try (OutputStream output = connection.getOutputStream()) { output.write(payload); }
            if (connection.getResponseCode() < 200 || connection.getResponseCode() >= 300)
                throw new IOException("Gemini unavailable");
            JSONObject reply;
            try (InputStream input = connection.getInputStream()) {
                reply = new JSONObject(readLimited(input));
            }
            JSONArray candidates = reply.optJSONArray("candidates");
            if (candidates == null || candidates.length() == 0) throw new IOException("Empty Gemini response");
            JSONObject content = candidates.getJSONObject(0).optJSONObject("content");
            JSONArray parts = content == null ? null : content.optJSONArray("parts");
            if (parts == null) throw new IOException("Empty Gemini response");
            StringBuilder text = new StringBuilder();
            for (int i = 0; i < parts.length(); i++) text.append(parts.getJSONObject(i).optString("text", ""));
            String result = text.toString().trim();
            if (result.isEmpty()) throw new IOException("Empty Gemini response");
            return result;
        } finally {
            connection.disconnect();
        }
    }

    private static String readLimited(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int count;
        while ((count = input.read(buffer)) != -1) {
            if (output.size() + count > 256_000) throw new IOException("Gemini response too large");
            output.write(buffer, 0, count);
        }
        return output.toString(StandardCharsets.UTF_8.name());
    }
}
