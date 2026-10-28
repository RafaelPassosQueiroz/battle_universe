//Gerador de perguntas
package com.example.projetomarvel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/** Generates only quiz content. Score, misses, gems and unlocks remain in MainActivity. */
final class QuizGenerator {
    private static final String[] LEVELS = {
            "fácil", "fácil/intermediária", "intermediária", "intermediária/difícil", "difícil", "muito difícil"
    };

    static String prompt(Set<String> previous) {
        StringBuilder prompt = new StringBuilder("Gere exatamente 24 perguntas inéditas em português sobre o universo Marvel, em ordem: seis etapas de quatro perguntas cada. "
                + "Dificuldades por etapa: 1 fácil; 2 fácil/intermediária; 3 intermediária; 4 intermediária/difícil; 5 difícil; 6 muito difícil. "
                + "Varie personagens, quadrinhos, MCU, séries, equipes, artefatos, lugares e cronologias. "
                + "Cada questão deve ter quatro alternativas plausíveis e distintas, exatamente uma resposta inequívoca, índice de 0 a 3 e explicação factual curta. "
                + "Não invente fatos, não use ambiguidades entre versões; quando uma versão importar, especifique MCU ou quadrinhos. "
                + "Revise cada resposta antes de retornar. O campo stage deve ser 1 para as primeiras quatro, depois 2, até 6. "
                + "Retorne só o JSON do formato pedido. Níveis: ");
        for (int i = 0; i < LEVELS.length; i++) prompt.append(i + 1).append('=').append(LEVELS[i]).append(';');
        if (!previous.isEmpty()) {
            prompt.append(" Evite repetir estas perguntas da sessão anterior: ");
            int n = 0;
            for (String question : previous) {
                if (n++ == 24) break;
                prompt.append('[').append(question).append("] ");
            }
        }
        return prompt.toString();
    }

    static JSONObject responseFormat() throws Exception {
        JSONObject string = new JSONObject().put("type", "string");
        JSONObject integer = new JSONObject().put("type", "integer");
        JSONObject item = new JSONObject().put("type", "object")
                .put("properties", new JSONObject()
                        .put("stage", integer).put("question", string)
                        .put("options", new JSONObject().put("type", "array").put("items", string))
                        .put("answerIndex", integer).put("explanation", string))
                .put("required", new JSONArray().put("stage").put("question").put("options")
                        .put("answerIndex").put("explanation"));
        JSONObject schema = new JSONObject().put("type", "object")
                .put("properties", new JSONObject().put("questions",
                        new JSONObject().put("type", "array").put("items", item)))
                .put("required", new JSONArray().put("questions"));
        return new JSONObject().put("text", new JSONObject()
                .put("mimeType", "application/json").put("schema", schema));
    }

    static ArrayList<MainActivity.QA> parse(String json) throws Exception {
        JSONArray items = new JSONObject(json).getJSONArray("questions");
        if (items.length() != 24) throw new IllegalArgumentException("Question count");
        Set<String> unique = new HashSet<>();
        ArrayList<MainActivity.QA> result = new ArrayList<>();
        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i);
            if (item.getInt("stage") != i / 4 + 1) throw new IllegalArgumentException("Question stage");
            String question = clean(item.getString("question"), 15, 230);
            if (!unique.add(question.toLowerCase(Locale.ROOT))) throw new IllegalArgumentException("Duplicate question");
            JSONArray options = item.getJSONArray("options");
            if (options.length() != 4) throw new IllegalArgumentException("Option count");
            String[] values = new String[4];
            Set<String> distinct = new HashSet<>();
            for (int j = 0; j < 4; j++) {
                values[j] = clean(options.getString(j), 1, 120);
                if (!distinct.add(values[j].toLowerCase(Locale.ROOT))) throw new IllegalArgumentException("Duplicate option");
            }
            int answer = item.getInt("answerIndex");
            if (answer < 0 || answer >= 4) throw new IllegalArgumentException("Answer index");
            String explanation = clean(item.getString("explanation"), 8, 190);
            result.add(new MainActivity.QA(question, values, answer, explanation));
        }
        return result;
    }

    private static String clean(String text, int min, int max) {
        String value = text.trim().replaceAll("\\s+", " ");
        if (value.length() < min || value.length() > max) throw new IllegalArgumentException("Question content");
        return value;
    }
}
