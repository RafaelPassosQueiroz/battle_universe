//Comic Vin
package com.example.projetomarvel;

import android.text.Html;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/** REST and JSON parsing for one Comic Vine /characters page. No UI state lives here. */
final class ComicVineClient {
    static final int PAGE_SIZE = 100;
    private final String apiKey;

    static final class Page {
        final ArrayList<MainActivity.Hero> characters;
        final int returned;
        final int total;
        Page(ArrayList<MainActivity.Hero> characters, int returned, int total) {
            this.characters = characters;
            this.returned = returned;
            this.total = total;
        }
    }

    static final class Details {
        final String description, powers;
        Details(String description,String powers){this.description=description;this.powers=powers;}
    }

    ComicVineClient(String apiKey) { this.apiKey = apiKey; }

    Page fetch(int offset) throws IOException { return fetchPage(offset, null); }

    Page fetchNamed(String name) throws IOException { return fetchPage(0, name); }

    /** Publisher membership gives a stable finite queue; records still come from /characters. */
    ArrayList<Integer> marvelIds() throws IOException {
        String address="https://comicvine.gamespot.com/api/publisher/4010-31/?api_key="
                +URLEncoder.encode(apiKey,"UTF-8")+"&format=json&field_list=name,characters";
        JSONObject json=request(address);
        if(json.optInt("status_code",1)!=1)throw new IOException("Comic Vine publisher status");
        JSONObject publisher=json.optJSONObject("results");
        JSONArray characters=publisher==null?null:publisher.optJSONArray("characters");
        if(characters==null||characters.length()==0)throw new IOException("Marvel publisher empty");
        ArrayList<Integer> ids=new ArrayList<>();
        HashSet<Integer> seen=new HashSet<>();
        for(int i=0;i<characters.length();i++){
            JSONObject character=characters.optJSONObject(i);
            int id=character==null?0:character.optInt("id");
            if(id>0&&seen.add(id))ids.add(id);
        }
        return ids;
    }

    Page fetchIds(List<Integer> ids,int start,int end) throws IOException {
        StringBuilder filter=new StringBuilder();
        for(int i=start;i<end;i++){
            if(i>start)filter.append('|');
            filter.append(ids.get(i));
        }
        return fetchCharacters("&filter=id:"+URLEncoder.encode(filter.toString(),"UTF-8"));
    }

    Details fetchDetails(int id) throws IOException {
        String address="https://comicvine.gamespot.com/api/character/4005-"+id
                +"/?api_key="+URLEncoder.encode(apiKey,"UTF-8")
                +"&format=json&field_list=description,deck,powers";
        JSONObject json=request(address);
        if(json.optInt("status_code",1)!=1)throw new IOException("Comic Vine detail status");
        JSONObject item=json.optJSONObject("results");
        if(item==null)throw new IOException("Character detail unavailable");
        String description=plain(item.optString("description",""));
        if(description.isEmpty())description=plain(item.optString("deck",""));
        if(description.length()>3000){
            int sentence=description.indexOf('.',2200);
            description=description.substring(0,sentence>0&&sentence<3000?sentence+1:3000);
        }
        JSONArray powersArray=item.optJSONArray("powers");
        ArrayList<String> names=new ArrayList<>();
        if(powersArray!=null)for(int i=0;i<powersArray.length();i++){
            JSONObject power=powersArray.optJSONObject(i);
            if(power!=null&&!power.optString("name","").isEmpty())
                names.add(power.optString("name"));
        }
        return new Details(description,TextUtils.join(" • ",names));
    }

    private Page fetchPage(int offset, String name) throws IOException {
        return fetchCharacters("&offset="+offset+(name==null?"":
                "&filter=name:"+URLEncoder.encode(name,"UTF-8")));
    }

    private Page fetchCharacters(String filter) throws IOException {
        String address="https://comicvine.gamespot.com/api/characters/?api_key="
                +URLEncoder.encode(apiKey,"UTF-8")+"&format=json&limit="+PAGE_SIZE
                +filter+"&field_list=id,name,real_name,deck,image,publisher";
        JSONObject json=request(address);
        if (json.optInt("status_code", 1) != 1)
            throw new IOException("Comic Vine status " + json.optInt("status_code"));
        JSONArray results = json.optJSONArray("results");
        if (results == null) throw new IOException("Página sem resultados da Comic Vine");
        ArrayList<MainActivity.Hero> marvel = new ArrayList<>();
        for (int index = 0; index < results.length(); index++) {
            JSONObject item = results.optJSONObject(index);
            if (item == null) continue;
            JSONObject publisher = item.optJSONObject("publisher");
            if (publisher == null) continue;
            String publisherName = publisher.optString("name", "");
            if (!"Marvel Comics".equalsIgnoreCase(publisherName)
                    && !"Marvel".equalsIgnoreCase(publisherName)) continue;
            int id = item.optInt("id", 0);
            if (id <= 0) continue;
            String deck = plain(item.optString("deck", ""));
            String description = plain(item.optString("description", ""));
            if (deck.isEmpty() && !description.isEmpty())
                deck = description.substring(0, Math.min(description.length(), 220));
            JSONObject image = item.optJSONObject("image");
            String imageUrl = image == null ? "" : image.optString("medium_url",
                    image.optString("original_url", ""));
            MainActivity.Hero hero = new MainActivity.Hero(id,
                    item.optString("name", "Personagem"),
                    item.optString("real_name", ""), deck, imageUrl, publisherName);
            hero.description = description;
            JSONArray powers = item.optJSONArray("powers");
            if (powers != null) {
                ArrayList<String> names = new ArrayList<>();
                for (int p = 0; p < powers.length(); p++) {
                    JSONObject power = powers.optJSONObject(p);
                    if (power != null && !power.optString("name").isEmpty())
                        names.add(power.optString("name"));
                }
                hero.powers = TextUtils.join(" • ", names);
            }
            marvel.add(hero);
        }
        return new Page(marvel, results.length(), json.optInt("number_of_total_results", 0));
    }

    private JSONObject request(String address) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(address).openConnection();
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(25000);
        connection.setRequestProperty("User-Agent",
                "MarvelUniverseAndroid/1.0 (Android; Comic Vine character catalog)");
        connection.setRequestProperty("Accept", "application/json");
        try {
            int code = connection.getResponseCode();
            if (code != HttpURLConnection.HTTP_OK) throw new IOException("Comic Vine HTTP " + code);
            JSONObject json;
            try (InputStream stream = connection.getInputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"))) {
                StringBuilder body = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) body.append(line);
                try { json = new JSONObject(body.toString()); }
                catch (Exception error) { throw new IOException("Resposta inválida da Comic Vine", error); }
            }
            return json;
        } finally {
            connection.disconnect();
        }
    }

    private static String plain(String html) {
        if (html == null || html.isEmpty()) return "";
        return Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT).toString().trim();
    }
}
