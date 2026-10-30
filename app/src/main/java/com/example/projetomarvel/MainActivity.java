//Arquivo Princiapl
package com.example.projetomarvel;

import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.WindowInsets;
import android.widget.*;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.Drawable;
import android.util.LruCache;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import org.json.*;
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

/** Native Java implementation of the Marvel Universe prototype. */
public class MainActivity extends AppCompatActivity {
    final int BG=Color.rgb(12,13,18), PANEL=Color.rgb(25,28,38), WHITE=Color.WHITE, MUTED=0xffb6b9c6, RED=0xffe62429;
    FrameLayout root; LinearLayout body, nav; FrameLayout content, fragmentSurface; String screen="Home", previousScreen="Home"; int navigationInset=0; Hero selectedHero; final ArrayList<Hero> heroes=new ArrayList<>();
    StoryMode storyMode;
    final String[] roster={"Homem-Aranha","Homem de Ferro","Wolverine","Capitão América","Thor","Hulk","Pantera Negra","Doutor Estranho","Viúva Negra","Feiticeira Escarlate","Capitã Marvel","Dr. Doom","Thanos"};
    int battleDifficulty=1;
    final String[] civilian={"Peter Parker","Tony Stark","Logan","Steve Rogers","Thor Odinson","Bruce Banner","T'Challa","Stephen Strange","Natasha Romanoff","Wanda Maximoff","Carol Danvers","Victor von Doom","Titã Louco"};
    int playerPick=0, botPick=5; boolean choosingBot=false,playerChosen=false,botChosen=false;
    int qIndex=0, correct=0, gems=0, phaseMisses=0, selected=-1; boolean answered=false, quizDone=false;
    // Process-local unlocks survive Activity recreation and independent mode resets.
    static boolean thanosUnlocked=false, doomUnlocked=false;
    final ArrayList<QA> questions=new ArrayList<>();
    final ArrayList<QA> localQuestions=new ArrayList<>();
    final HashSet<String> previousQuizQuestions=new HashSet<>();
    final GeminiClient geminiClient=new GeminiClient();
    final ExecutorService geminiIo=Executors.newSingleThreadExecutor();
    final ExecutorService quizIo=Executors.newSingleThreadExecutor();
    final ArrayList<GeminiClient.Turn> jarvisHistory=new ArrayList<>();
    long jarvisLastDisplayAt=0;
    static final String JARVIS_PERSONA=
            "Você é JARVIS, um sofisticado assistente virtual pessoal inspirado na personalidade de um mordomo tecnológico extremamente inteligente, preciso e educado.\n"
            +"Sua principal função neste aplicativo é responder perguntas sobre o universo Marvel.\n"
            +"Você conhece personagens, heróis, vilões, poderes, histórias, filmes, séries, quadrinhos, equipes, acontecimentos, universos, cronologias, relações, curiosidades, artefatos, locais, guerras, origens, fraquezas e comparações da Marvel.\n"
            +"Responda sobre qualquer assunto relacionado à Marvel, e não apenas Homem-Aranha ou Quarteto Fantástico.\n"
            +"Sua linguagem deve ser extremamente educada, confiante, inteligente, objetiva, elegante, levemente sofisticada e natural, sem parecer um robô genérico, sem exagerar em formalidade e sem gírias.\n"
            +"Prefira respostas curtas e úteis. Quando a pergunta exigir contexto, forneça o suficiente para responder corretamente.\n"
            +"Nunca invente informações deliberadamente. Quando houver incerteza ou versões diferentes de uma história, deixe isso claro.\n"
            +"Não diga que possui acesso a informações que realmente não possui. Não saia do universo Marvel sem necessidade.\n"
            +"Se a pergunta não for relacionada à Marvel, responda educadamente como JARVIS e tente direcionar a conversa novamente para o contexto do aplicativo.\n"
            +"Exemplos de tom: 'Certamente, senhor.' 'Uma excelente pergunta, senhor.' 'Pelo que consta nos registros...' 'Sim, senhor. Essa informação está correta.' Evite repetir essas expressões em todas as respostas.\n"
            +"O objetivo é parecer um assistente pessoal sofisticado, não um personagem que repete bordões. Responda em português brasileiro.";
    boolean quizSessionStarted=false,quizLoading=false;
    int quizGeneration=0;
    final Handler main=new Handler(Looper.getMainLooper());
    CharacterTranslator translator; final HashMap<String,String> translations=new HashMap<>();
    CatalogStore catalogStore; final ExecutorService io=Executors.newSingleThreadExecutor();
    final ExecutorService imageIo=Executors.newFixedThreadPool(3); LruCache<String,android.graphics.Bitmap> imageCache;
    final HashMap<String,ArrayList<ImageView>> pendingImages=new HashMap<>();
    final HashSet<Integer> loadingDetails=new HashSet<>();
    final HashMap<String,Integer> sessionOrder=new HashMap<>();
    RecyclerView heroList; HeroAdapter heroAdapter; TextView homeCount,catalogStatus; String homeQuery=""; ArrayList<Hero> visibleHeroes=new ArrayList<>(); boolean catalogSyncing=true; String catalogError="";
    static class Hero {int id;String name,real,deck,image,publisher,description,powers; Hero(int id,String n,String r,String d,String i,String p){this.id=id;name=n;real=r;deck=d;image=i;publisher=p;description=d;powers="";}}
    static class QA {String q;String[] opts;int answer;String explanation;QA(String q,String[]o,int a){this(q,o,a,"");}QA(String q,String[]o,int a,String explanation){this.q=q;opts=o;answer=a;this.explanation=explanation;}}
    @Override public void onCreate(Bundle b){super.onCreate(b);getWindow().setStatusBarColor(BG);getWindow().setNavigationBarColor(BG);translator=new CharacterTranslator();catalogStore=new CatalogStore(this);int cacheKb=(int)(Runtime.getRuntime().maxMemory()/1024/8);imageCache=new LruCache<String,android.graphics.Bitmap>(Math.max(1024,cacheKb)){@Override protected int sizeOf(String key,android.graphics.Bitmap value){return Math.max(1,value.getByteCount()/1024);}};seedQuestions();storyMode=new StoryMode(this);showSplash();}
    void translateText(String source,java.util.function.Consumer<String> result){if(source==null||source.trim().isEmpty()){result.accept("");return;}String cached=translations.get(source);if(cached!=null){result.accept(cached);return;}translator.translate(source,value->{if(value!=null&&!value.equals(source))translations.put(source,value);result.accept(value==null?source:value);});}
    TextView text(String s,float size,int color){TextView t=new TextView(this);t.setText(s);t.setTextColor(color);t.setTextSize(size);t.setTypeface(Typography.barlow(this));return t;}
    class StarfieldDrawable extends Drawable {final Paint p=new Paint(3);final float[] xy=new float[140];final float[] sizes=new float[70];StarfieldDrawable(){Random r=new Random(420);for(int i=0;i<70;i++){xy[i*2]=r.nextFloat();xy[i*2+1]=r.nextFloat();sizes[i]=1+r.nextFloat()*2;}}@Override public void draw(Canvas c){android.graphics.Rect b=getBounds();float w=b.width(),h=b.height();p.setShader(new RadialGradient(w*.48f,h*.24f,Math.max(w,h)*.85f,new int[]{0xff211746,0xff11102c,0xff07051a},null,Shader.TileMode.CLAMP));c.drawRect(b,p);p.setShader(null);for(int i=0;i<70;i++){p.setColor(i%6==0?0xffe6dcff:0xffa6b9ff);p.setAlpha(90+(i*29%150));c.drawCircle(xy[i*2]*w,xy[i*2+1]*h,sizes[i],p);}}@Override public void setAlpha(int alpha){}@Override public void setColorFilter(android.graphics.ColorFilter f){}@Override public int getOpacity(){return android.graphics.PixelFormat.OPAQUE;}}
    class JarvisBackdrop extends Drawable {final Paint p=new Paint(3);@Override public void draw(Canvas c){android.graphics.Rect b=getBounds();float w=b.width(),h=b.height();p.setShader(new RadialGradient(w*.48f,0,Math.max(w,h)*.85f,new int[]{0xff07344d,0xff031625,0xff020b14},null,Shader.TileMode.CLAMP));c.drawRect(b,p);p.setShader(null);p.setColor(0x1619e6ff);p.setStrokeWidth(dp(1));for(float x=0;x<w;x+=dp(26))c.drawLine(x,0,x,h,p);for(float y=0;y<h;y+=dp(26))c.drawLine(0,y,w,y,p);}@Override public void setAlpha(int alpha){}@Override public void setColorFilter(android.graphics.ColorFilter filter){}@Override public int getOpacity(){return android.graphics.PixelFormat.OPAQUE;}}
    class StoryBackdrop extends Drawable {final Paint p=new Paint(3);@Override public void draw(Canvas c){android.graphics.Rect b=getBounds();float w=b.width(),h=b.height();int accent=storyMode.themeColor();int glow=Color.rgb((Color.red(accent)+32)/2,(Color.green(accent)+44)/2,(Color.blue(accent)+78)/2);p.setShader(new RadialGradient(w*.72f,h*.13f,Math.max(w,h)*.85f,new int[]{glow,0xff11142c,0xff080c19},null,Shader.TileMode.CLAMP));c.drawRect(b,p);p.setShader(null);p.setColor(0x4473a9d2);for(float x=dp(10);x<w;x+=dp(27))for(float y=dp(12);y<h*.85f;y+=dp(29))c.drawCircle(x,y,dp(1),p);}@Override public void setAlpha(int alpha){}@Override public void setColorFilter(android.graphics.ColorFilter f){}@Override public int getOpacity(){return android.graphics.PixelFormat.OPAQUE;}}
    class HomeBackdrop extends Drawable {final Paint p=new Paint(3);@Override public void draw(Canvas c){android.graphics.Rect b=getBounds();float w=b.width(),h=b.height();p.setShader(new RadialGradient(w*.5f,0,Math.max(w,h)*.73f,new int[]{0xff4f1c25,0xff211019,BG},null,Shader.TileMode.CLAMP));c.drawRect(b,p);p.setShader(null);}@Override public void setAlpha(int alpha){}@Override public void setColorFilter(android.graphics.ColorFilter f){}@Override public int getOpacity(){return android.graphics.PixelFormat.OPAQUE;}}
    class BattleBackdrop extends Drawable {final Paint p=new Paint(3);@Override public void draw(Canvas c){android.graphics.Rect b=getBounds();float w=b.width(),h=b.height();p.setShader(new RadialGradient(w*.5f,0,Math.max(w,h)*.68f,new int[]{0xff4e2a16,0xff21131a,0xff0d0a10},null,Shader.TileMode.CLAMP));c.drawRect(b,p);p.setShader(null);}@Override public void setAlpha(int alpha){}@Override public void setColorFilter(android.graphics.ColorFilter f){}@Override public int getOpacity(){return android.graphics.PixelFormat.OPAQUE;}}
    class VersusBackdrop extends Drawable {final Paint p=new Paint(3);@Override public void draw(Canvas c){android.graphics.Rect b=getBounds();float w=b.width(),h=b.height();p.setShader(new android.graphics.LinearGradient(0,0,0,h,0xff802534,0xff201a42,Shader.TileMode.CLAMP));c.drawRect(b,p);p.setShader(new android.graphics.LinearGradient(0,0,0,h,0xff6e1515,0xff432510,Shader.TileMode.CLAMP));android.graphics.Path right=new android.graphics.Path();right.moveTo(w*.66f,0);right.lineTo(w,0);right.lineTo(w,h);right.lineTo(w*.41f,h);right.close();c.drawPath(right,p);p.setShader(null);}@Override public void setAlpha(int alpha){}@Override public void setColorFilter(android.graphics.ColorFilter filter){}@Override public int getOpacity(){return android.graphics.PixelFormat.OPAQUE;}}
    void showSplash(){
        setContentView(R.layout.activity_splash);
        FrameLayout splash=findViewById(R.id.splash_root);
        Runnable enter=()->{
            if(root!=null)return;
            splash.animate().alpha(0f).setDuration(220).withEndAction(()->{
                if(root==null){open("Home");loadCatalog();}
            }).start();
        };
        main.postDelayed(enter,1850);
        splash.setOnClickListener(v->{main.removeCallbacks(enter);enter.run();});
    }
    void open(String tab) {
        if (screen.equals("História") && !tab.equals("História") && storyMode != null)
            storyMode.resetForTabExit();
        screen = tab;
        if (root == null) {
            setContentView(R.layout.activity_main);
            root = findViewById(R.id.main);
            content = findViewById(R.id.screen_container);
            nav = findViewById(R.id.bottom_navigation);
            installNavInsets();
        }
        if (tab.equals("Quiz")) {root.setBackground(new StarfieldDrawable());if(!quizSessionStarted)startQuizGeneration();}
        else if (tab.equals("Jarvis")) root.setBackground(new JarvisBackdrop());
        else if (tab.equals("Lutas") || tab.equals("Pré-luta") || tab.equals("Seleção")) root.setBackground(new BattleBackdrop());
        else if (tab.equals("Home")) root.setBackground(new HomeBackdrop());
        else root.setBackgroundColor(BG);
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.screen_enter, R.anim.screen_exit)
                .replace(R.id.screen_container, ScreenFragment.create(tab)).commitNow();
        fragmentSurface = findViewById(R.id.fragment_surface);
        body = new LinearLayout(this);
        body.setOrientation(1);
        body.setPadding(dp(18), dp(16), dp(18),
                (tab.equals("Pré-luta") || tab.equals("Detalhes")) ? dp(16) : dp(102));
        body.setBackgroundColor(Color.TRANSPARENT);
        if (tab.equals("Home")) {
            LinearLayout page = new LinearLayout(this);
            page.setOrientation(1);
            fragmentSurface.addView(page, new FrameLayout.LayoutParams(-1, -1));
            page.addView(body, new LinearLayout.LayoutParams(-1, -2));
        } else if (tab.equals("Jarvis")) {
            fragmentSurface.addView(body, new FrameLayout.LayoutParams(-1, -1));
        } else if (tab.equals("Detalhes")) {
            LinearLayout page = new LinearLayout(this);
            page.setOrientation(1);
            fragmentSurface.addView(page, new FrameLayout.LayoutParams(-1, -1));
            LinearLayout toolbar = new LinearLayout(this);
            toolbar.setGravity(Gravity.CENTER_VERTICAL);
            WindowInsets windowInsets = root.getRootWindowInsets();
            int statusInset = windowInsets == null ? 0 : windowInsets.getSystemWindowInsetTop();
            if (statusInset == 0) {
                int statusBarId = getResources().getIdentifier("status_bar_height", "dimen", "android");
                if (statusBarId != 0) statusInset = getResources().getDimensionPixelSize(statusBarId);
            }
            toolbar.setPadding(dp(8), dp(2) + statusInset, dp(16), dp(2));
            toolbar.setBackgroundColor(PANEL);
            Button back = button("‹", BG);
            back.setAllCaps(false);
            back.setTextSize(28);
            toolbar.addView(back, new LinearLayout.LayoutParams(dp(52), dp(52)));
            TextView name = text(displayName(selectedHero.name), 20, WHITE);
            name.setTypeface(null, Typeface.BOLD);
            name.setPadding(dp(14), 0, 0, 0);
            toolbar.addView(name);
            back.setOnClickListener(v -> open(previousScreen));
            page.addView(toolbar, new LinearLayout.LayoutParams(-1, dp(56) + statusInset));
            ScrollView scroll = new ScrollView(this);
            scroll.addView(body);
            page.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));
        } else {
            ScrollView scroll = new ScrollView(this);
            scroll.setBackgroundColor(Color.TRANSPARENT);
            scroll.addView(body);
            fragmentSurface.addView(scroll, new FrameLayout.LayoutParams(-1, -1));
        }
        renderTab();
        makeNav();
    }
    void renderTab(){int chrome=screen.equals("Jarvis")?0xff041726:screen.equals("Quiz")?0xff2b1a5a:screen.equals("História")?0xff101932:screen.equals("Pré-luta")?0xff71232d:screen.equals("Seleção")||screen.equals("Lutas")?0xff21131a:BG;getWindow().setStatusBarColor(chrome);getWindow().setNavigationBarColor(0xff0b0d14);body.removeAllViews();View floating=fragmentSurface.findViewWithTag("screen_action");if(floating!=null)fragmentSurface.removeView(floating); if(screen.equals("Home"))home();else if(screen.equals("Jarvis"))jarvis();else if(screen.equals("Quiz"))quiz();else if(screen.equals("Lutas"))fights();else if(screen.equals("Detalhes"))detailScreen();else if(screen.equals("Seleção"))pickScreen();else if(screen.equals("Pré-luta"))preFight();else if(screen.equals("História")){root.setBackground(new StoryBackdrop());storyMode.render();}}
    void title(String s){TextView t=text(s,28,WHITE);t.setTypeface(screen.equals("Quiz")?Typography.cinzel(this):Typography.bebas(this));body.addView(t);}
    void home(){new HomeReference(this).render();}
    void refreshVisibleCatalog(){
        ArrayList<Hero> loaded;
        synchronized(heroes){loaded=new ArrayList<>(heroes);}
        visibleHeroes.clear();
        String query=homeQuery.trim().toLowerCase(Locale.ROOT);
        if(!query.isEmpty()){
            for(Hero hero:loaded){
                String name=displayName(hero.name).toLowerCase(Locale.ROOT);
                String original=hero.name==null?"":hero.name.toLowerCase(Locale.ROOT);
                String real=hero.real==null?"":hero.real.toLowerCase(Locale.ROOT);
                if(name.contains(query)||original.contains(query)||real.contains(query))visibleHeroes.add(hero);
            }
            visibleHeroes.sort((a,b)->displayName(a.name).compareToIgnoreCase(displayName(b.name)));
        }else{
            // Exactly one canonical Comic Vine entry is featured for each of the twelve names.
            String[] names={"spider-man","iron man","captain america","thor","hulk",
                    "black widow","black panther","doctor strange","scarlet witch",
                    "wolverine","deadpool","captain marvel"};
            String[] identities={"peter parker","tony stark","steve rogers","thor odinson",
                    "bruce banner","natasha romanoff","t'challa","stephen strange",
                    "wanda maximoff","logan","wade wilson","carol danvers"};
            HashSet<Integer> featuredIds=new HashSet<>();
            for(int index=0;index<names.length;index++){
                Hero best=null;
                int bestScore=-1;
                for(Hero hero:loaded){
                    String candidate=hero.name==null?"":hero.name.toLowerCase(Locale.ROOT);
                    if(!candidate.equals(names[index])
                            && !candidate.startsWith(names[index]+" ("))continue;
                    int score=(candidate.equals(names[index])?4:0)
                            +((hero.real!=null&&hero.real.toLowerCase(Locale.ROOT)
                            .contains(identities[index]))?2:0);
                    if(score>bestScore){best=hero;bestScore=score;}
                }
                if(best!=null && featuredIds.add(best.id))visibleHeroes.add(best);
            }
            for(Hero hero:loaded)if(!featuredIds.contains(hero.id))visibleHeroes.add(hero);
            visibleHeroes.subList(featuredIds.size(),visibleHeroes.size()).sort((a,b)->
                    Integer.compare(sessionOrder.computeIfAbsent(String.valueOf(a.id),
                                    key->new Random(a.id).nextInt()),
                            sessionOrder.computeIfAbsent(String.valueOf(b.id),
                                    key->new Random(b.id).nextInt())));
        }
        if(heroAdapter!=null)heroAdapter.notifyDataSetChanged();
        if(homeCount!=null)homeCount.setText(query.isEmpty()
                ? loaded.size()+" personagens"
                : visibleHeroes.size()+" encontrados · "+loaded.size()+" carregados");
        if(catalogStatus!=null){
            String message=!catalogError.isEmpty()?catalogError
                    :catalogSyncing?"Sincronizando catálogo Comic Vine… "+loaded.size()+" personagens"
                    :loaded.isEmpty()?"Nenhum personagem Marvel disponível.":"";
            catalogStatus.setText(message);
            catalogStatus.setVisibility(message.isEmpty()?View.GONE:View.VISIBLE);
        }
    }
    String displayName(String n){String s=n.toLowerCase(Locale.ROOT);if(s.contains("spider-man")||s.equals("spider-man"))return "Homem-Aranha";if(s.contains("iron man"))return "Homem de Ferro";if(s.contains("captain america"))return "Capitão América";if(s.contains("black widow"))return "Viúva Negra";if(s.contains("black panther"))return "Pantera Negra";if(s.contains("doctor strange"))return "Doutor Estranho";if(s.contains("scarlet witch"))return "Feiticeira Escarlate";if(s.contains("captain marvel"))return "Capitã Marvel";if(s.contains("wolverine"))return "Wolverine";if(s.contains("deadpool"))return "Deadpool";return n;}
    class HeroAdapter extends RecyclerView.Adapter<HeroAdapter.Holder> {
        class Holder extends RecyclerView.ViewHolder {
            LinearLayout card;
            ImageView image;
            TextView name, real, deck;
            Holder(View item) {
                super(item);
                card = item.findViewById(R.id.hero_card);
                image = item.findViewById(R.id.hero_image);
                name = item.findViewById(R.id.hero_name);
                real = item.findViewById(R.id.hero_real_name);
                deck = item.findViewById(R.id.hero_deck);
            }
        }
        @Override public Holder onCreateViewHolder(android.view.ViewGroup parent, int type) {
            return new Holder(getLayoutInflater().inflate(R.layout.item_hero, parent, false));
        }
        @Override public void onBindViewHolder(Holder holder, int position) {
            Hero hero = visibleHeroes.get(position);
            holder.card.setTag(hero.id);
            holder.name.setTypeface(Typography.bebas(MainActivity.this));
            holder.name.setText(displayName(hero.name));
            holder.real.setText(hero.real == null || hero.real.isEmpty()
                    ? "Identidade não informada" : hero.real);
            String raw = hero.deck == null || hero.deck.isEmpty()
                    ? hero.description == null ? "" : hero.description : hero.deck;
            String source = raw.length() > 150 ? raw.substring(0, 147) + "…" : raw;
            holder.deck.setText(translations.getOrDefault(source, source));
            translateText(source, value -> {
                if (Integer.valueOf(hero.id).equals(holder.card.getTag()))
                    holder.deck.setText(value);
            });
            GradientDrawable background = new GradientDrawable(
                    GradientDrawable.Orientation.TL_BR, heroColors(hero.name));
            background.setCornerRadius(dp(20));
            background.setStroke(dp(1), 0x44ffffff);
            holder.card.setBackground(background);
            holder.image.setImageDrawable(null);
            loadImage(hero.image, holder.image);
            holder.card.setOnClickListener(v -> details(hero));
        }
        @Override public int getItemCount() { return visibleHeroes.size(); }
    }
    int priorityIndex(String n,String[] p){String s=n.toLowerCase(Locale.ROOT);for(int i=0;i<p.length;i++)if(s.contains(p[i]))return i;return -1;}
    int[] heroColors(String n){String s=n.toLowerCase(Locale.ROOT);if(s.contains("spider")||s.contains("aranha"))return new int[]{0xff762532,0xff152d68};if(s.contains("iron")||s.contains("ferro"))return new int[]{0xff511d26,0xff76500f};if(s.contains("hulk"))return new int[]{0xff103a30,0xff443065};if(s.contains("wolverine"))return new int[]{0xff2d2a18,0xff20365c};if(s.contains("captain america")||s.contains("capitão américa"))return new int[]{0xff162b53,0xff65162e};if(s.contains("thor"))return new int[]{0xff20283c,0xff53617d};if(s.contains("pantera"))return new int[]{0xff171322,0xff4c2874};if(s.contains("estranho"))return new int[]{0xff3d1b23,0xff171e4a};if(s.contains("quarteto"))return new int[]{0xff164b97,0xff0e2f66};if(s.contains("marvel"))return new int[]{0xff273260,0xff782539};if(s.contains("viúva")||s.contains("escarlate"))return new int[]{0xff491d2e,0xff25213d};if(s.contains("thanos"))return new int[]{0xff2d243a,0xff604326};return new int[]{0xff202331,0xff41475b};}
    void details(Hero h){selectedHero=h;previousScreen=screen;open("Detalhes");}
    void detailScreen(){
        if(selectedHero==null){open("Home");return;}
        body.setPadding(dp(20),dp(14),dp(20),dp(105)+navigationInset);
        ImageView image=new ImageView(this);
        image.setAdjustViewBounds(true);
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        LinearLayout.LayoutParams imageLp=new LinearLayout.LayoutParams(-1,dp(260));
        imageLp.bottomMargin=dp(15);
        body.addView(image,imageLp);
        loadImage(selectedHero.image,image);
        TextView name=text(displayName(selectedHero.name),30,WHITE);
        name.setTypeface(Typography.bebas(this));
        body.addView(name);
        body.addView(text(selectedHero.real==null||selectedHero.real.isEmpty()
                ?"Nome real não informado":selectedHero.real,17,0xffff6d6d));
        body.addView(text("Editora  •  "+selectedHero.publisher,14,MUTED));
        String powers=selectedHero.powers==null||selectedHero.powers.isEmpty()
                ?"Informação de poderes indisponível na Comic Vine.":selectedHero.powers;
        TextView powerText=section("Poderes",powers);
        if(!powers.startsWith("Informação"))translateText(powers,powerText::setText);
        Hero hero=selectedHero;
        String description=hero.description==null?"":hero.description.trim();
        if(description.isEmpty())description=selectedHero.deck==null?"":selectedHero.deck.trim();
        if(description.isEmpty())description="Descrição indisponível na Comic Vine.";
        TextView value=section("Descrição",description);
        if(!description.startsWith("Descrição indisponível")){
            String initial=description;
            translateText(initial,translated->{if(selectedHero==hero &&
                    (hero.description==null||hero.description.isEmpty()
                            ||initial.equals(hero.description)))value.setText(translated);});
        }
        if((hero.description==null||hero.description.isEmpty())
                && !BuildConfig.COMIC_VINE_API_KEY.isEmpty()
                && loadingDetails.add(hero.id)){
            imageIo.execute(()->{
                try{
                    ComicVineClient.Details details=new ComicVineClient(
                            BuildConfig.COMIC_VINE_API_KEY).fetchDetails(hero.id);
                    hero.description=details.description;
                    hero.powers=details.powers;
                    catalogStore.updateDetails(hero.id,details.description,details.powers);
                    main.post(()->{
                        loadingDetails.remove(hero.id);
                        if(screen.equals("Detalhes")&&selectedHero==hero){
                            if(!details.description.isEmpty())translateText(details.description,value::setText);
                            if(!details.powers.isEmpty())translateText(details.powers,powerText::setText);
                        }
                    });
                }catch(Exception ignored){main.post(()->loadingDetails.remove(hero.id));}
            });
        }
    }
    TextView section(String heading,String content){TextView h=text(heading,19,WHITE);h.setTypeface(null,Typeface.BOLD);h.setPadding(0,20,0,5);body.addView(h);TextView value=text(content,15,MUTED);body.addView(value);return value;}
    void loadImage(String url,ImageView view){
        if(url==null||url.isEmpty())return;
        view.setTag(url);
        android.graphics.Bitmap cached=imageCache.get(url);
        if(cached!=null){view.setImageBitmap(cached);return;}
        ArrayList<ImageView> waiting=pendingImages.get(url);
        if(waiting!=null){waiting.add(view);return;}
        waiting=new ArrayList<>();waiting.add(view);pendingImages.put(url,waiting);
        imageIo.execute(()->{
            android.graphics.Bitmap bitmap=null;
            HttpURLConnection connection=null;
            try{
                connection=(HttpURLConnection)new URL(url).openConnection();
                connection.setConnectTimeout(10000);connection.setReadTimeout(15000);
                try(InputStream in=connection.getInputStream()){
                    bitmap=BitmapFactory.decodeStream(in);
                }
            }catch(Exception ignored){}finally{if(connection!=null)connection.disconnect();}
            final android.graphics.Bitmap result=bitmap;
            main.post(()->{
                ArrayList<ImageView> views=pendingImages.remove(url);
                if(result==null||views==null)return;
                imageCache.put(url,result);
                for(ImageView target:views)
                    if(url.equals(target.getTag()))target.setImageBitmap(result);
            });
        });
    }
    @Override protected void onDestroy(){super.onDestroy();if(isFinishing()){translator.close();io.shutdownNow();imageIo.shutdownNow();}}
    @Override protected void onResume(){super.onResume();if(body!=null&&screen.equals("Lutas"))renderTab();}
    void loadCatalog(){
        String key=BuildConfig.COMIC_VINE_API_KEY;
        io.execute(()->{
            ArrayList<Hero> cached=catalogStore.load();
            HashSet<Integer> seen=new HashSet<>();
            for(Hero h:cached)seen.add(h.id);
            main.post(()->{synchronized(heroes){heroes.clear();heroes.addAll(cached);}if(screen.equals("Home"))refreshVisibleCatalog();});
            if(key.isEmpty()){
                main.post(()->{catalogSyncing=false;catalogError="Catálogo indisponível: configure a chave da Comic Vine.";refreshVisibleCatalog();});
                return;
            }
            long lastSync=getPreferences(0).getLong("catalog_sync_at",0L);
            if(!cached.isEmpty()&&System.currentTimeMillis()-lastSync<24L*60L*60L*1000L){
                main.post(()->{catalogSyncing=false;refreshVisibleCatalog();});
                return;
            }
            int offset=getPreferences(0).getInt("catalog_sync_offset",0);
            ComicVineClient client=new ComicVineClient(key);
            try{
                // Seed the same API backed card component before the long cross-publisher scan.
                if(cached.isEmpty() && offset==0){
                    String[] featuredNames={"Spider-Man","Iron Man","Captain America","Thor",
                            "Hulk","Black Widow","Black Panther","Doctor Strange",
                            "Scarlet Witch","Wolverine","Deadpool","Captain Marvel"};
                    for(String name:featuredNames){
                        try{
                            ComicVineClient.Page featured=client.fetchNamed(name);
                            ArrayList<Hero> additions=new ArrayList<>();
                            for(Hero hero:featured.characters)if(seen.add(hero.id))additions.add(hero);
                            if(!additions.isEmpty()){
                                catalogStore.save(additions);
                                main.post(()->{
                                    synchronized(heroes){heroes.addAll(additions);}
                                    if(screen.equals("Home"))refreshVisibleCatalog();
                                });
                            }
                        }catch(IOException ignored){/* The full scan can still find this character. */}
                    }
                }
                ArrayList<Integer> publisherIds=null;
                try{publisherIds=client.marvelIds();}
                catch(IOException ignored){/* General paging remains available. */}
                boolean targeted=publisherIds!=null&&!publisherIds.isEmpty();
                String mode=targeted?"publisher":"scan";
                if(!mode.equals(getPreferences(0).getString("catalog_sync_mode","")))offset=0;
                while(true){
                    if(targeted&&offset>=publisherIds.size())break;
                    ComicVineClient.Page page=null;
                    IOException lastError=null;
                    int end=targeted?Math.min(offset+ComicVineClient.PAGE_SIZE,
                            publisherIds.size()):0;
                    for(int attempt=0;attempt<4 && page==null;attempt++){
                        try{page=targeted?client.fetchIds(publisherIds,offset,end)
                                :client.fetch(offset);}
                        catch(IOException error){
                            lastError=error;
                            if(attempt<3)Thread.sleep((1L<<attempt)*1500L);
                        }
                    }
                    if(page==null)throw lastError==null?new IOException("Falha de rede"):lastError;
                    ArrayList<Hero> additions=new ArrayList<>();
                    for(Hero hero:page.characters)if(seen.add(hero.id))additions.add(hero);
                    if(!additions.isEmpty())catalogStore.save(additions);
                    int nextOffset=targeted?end:offset+page.returned;
                    getPreferences(0).edit().putInt("catalog_sync_offset",nextOffset)
                            .putString("catalog_sync_mode",mode).apply();
                    main.post(()->{
                        synchronized(heroes){heroes.addAll(additions);}
                        if(screen.equals("Home"))refreshVisibleCatalog();
                    });
                    offset=nextOffset;
                    if(targeted?offset>=publisherIds.size():
                            page.returned==0 || (page.total>0 && offset>=page.total))break;
                }
                getPreferences(0).edit().putLong("catalog_sync_at",System.currentTimeMillis())
                        .putInt("catalog_sync_offset",0).apply();
                main.post(()->{catalogSyncing=false;catalogError="";if(screen.equals("Home"))refreshVisibleCatalog();});
            }catch(Exception error){
                main.post(()->{catalogSyncing=false;catalogError="Sincronização pausada. O catálogo salvo continua disponível.";if(screen.equals("Home"))refreshVisibleCatalog();});
            }
        });
    }
    String plainText(String html){return android.text.Html.fromHtml(html,android.text.Html.FROM_HTML_MODE_COMPACT).toString().trim();}
    String read(InputStream in)throws Exception{BufferedReader r=new BufferedReader(new InputStreamReader(in));StringBuilder b=new StringBuilder();String s;while((s=r.readLine())!=null)b.append(s);return b.toString();}
    void parseHeroes(JSONArray a)throws Exception{HashSet<Integer> seen=new HashSet<>();for(int i=0;i<a.length();i++){JSONObject x=a.getJSONObject(i);Hero h=new Hero(x.optInt("id"),x.optString("name"),x.optString("real"),x.optString("deck"),x.optString("image"),x.optString("publisher"));h.description=x.optString("description",h.deck);h.powers=x.optString("powers","");if(seen.add(h.id))heroes.add(h);}}
    void jarvis(){
        body.setPadding(dp(16),dp(46),dp(16),dp(102)+navigationInset);
        LinearLayout header=new LinearLayout(this);header.setGravity(Gravity.CENTER_VERTICAL);header.setPadding(dp(3),dp(8),dp(3),dp(14));
        FrameLayout orb=new FrameLayout(this);GradientDrawable ring=new GradientDrawable();ring.setShape(GradientDrawable.OVAL);ring.setColor(0x1a19e6ff);ring.setStroke(dp(2),0xff19e6ff);orb.setBackground(ring);ImageView helmet=new ImageView(this);helmet.setImageResource(R.drawable.ic_nav_jarvis);helmet.setColorFilter(0xff19e6ff);FrameLayout.LayoutParams hp=new FrameLayout.LayoutParams(dp(34),dp(34),Gravity.CENTER);orb.addView(helmet,hp);header.addView(orb,new LinearLayout.LayoutParams(dp(58),dp(58)));
        LinearLayout ident=new LinearLayout(this);ident.setOrientation(1);ident.setPadding(dp(12),0,0,0);TextView brand=text("JARVIS",24,0xffeaffff);brand.setTypeface(Typography.rajdhaniBold(this));brand.setLetterSpacing(.16f);ident.addView(brand);TextView status=text("●  Online · sistemas operacionais",13,0xff7fdde9);status.setTypeface(Typography.rajdhani(this));android.text.SpannableString statusColors=new android.text.SpannableString(status.getText());statusColors.setSpan(new android.text.style.ForegroundColorSpan(0xff3cff9e),0,1,0);status.setText(statusColors);ident.addView(status);header.addView(ident,new LinearLayout.LayoutParams(0,-2,1));
        LinearLayout equalizer=new LinearLayout(this);equalizer.setGravity(Gravity.BOTTOM);for(int h:new int[]{8,19,12,23,10}){View bar=new View(this);GradientDrawable cyan=new GradientDrawable();cyan.setColor(0xff19e6ff);cyan.setCornerRadius(dp(2));bar.setBackground(cyan);LinearLayout.LayoutParams bp=new LinearLayout.LayoutParams(dp(3),dp(h));bp.leftMargin=dp(3);equalizer.addView(bar,bp);}header.addView(equalizer,new LinearLayout.LayoutParams(dp(34),dp(26)));
        View divider=new View(this);divider.setBackgroundColor(0x5536c9df);LinearLayout.LayoutParams dpv=new LinearLayout.LayoutParams(-1,dp(1));dpv.bottomMargin=dp(13);body.addView(header);body.addView(divider,dpv);
        ScrollView transcript=new ScrollView(this);transcript.setFillViewport(false);transcript.setVerticalScrollBarEnabled(false);LinearLayout messages=new LinearLayout(this);messages.setOrientation(1);transcript.addView(messages);body.addView(transcript,new LinearLayout.LayoutParams(-1,0,1));addBubble(messages,"Toda a Interface já está preparada para o senhor!",false);
        LinearLayout composer=new LinearLayout(this);composer.setGravity(Gravity.CENTER_VERTICAL);EditText input=new EditText(this);input.setSingleLine(true);input.setTextSize(17);input.setTypeface(Typography.rajdhaniSemi(this));input.setHint("Pergunte ao Jarvis…");input.setHintTextColor(0xff5fa9b6);input.setTextColor(0xffeaffff);input.setPadding(dp(15),0,dp(12),0);GradientDrawable field=new GradientDrawable();field.setColor(0xe603192d);field.setCornerRadius(dp(14));field.setStroke(dp(1),0x8836d7e8);input.setBackground(field);composer.addView(input,new LinearLayout.LayoutParams(0,dp(52),1));Button send=button("➜",0xff0a8fff);send.setTextColor(0xff02202e);send.setTextSize(22);send.setMinWidth(0);send.setPadding(0,0,0,0);GradientDrawable sendBg=new GradientDrawable(GradientDrawable.Orientation.TL_BR,new int[]{0xff19e6ff,0xff0a8fff});sendBg.setCornerRadius(dp(14));send.setBackground(sendBg);LinearLayout.LayoutParams sendLp=new LinearLayout.LayoutParams(dp(52),dp(52));sendLp.leftMargin=dp(9);composer.addView(send,sendLp);body.addView(composer);
        send.setOnClickListener(v->{String q=input.getText().toString().trim();if(q.isEmpty())return;
            Runnable submit=()->{input.setText("");transcript.post(()->transcript.fullScroll(View.FOCUS_DOWN));};
            if(!geminiClient.configured()){sendJarvis(messages,q,false);submit.run();return;}
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Enviar pergunta ao Gemini?")
                    .setMessage("Pergunta: “"+q+"”\n\nEsta pergunta e o contexto recente da conversa serão enviados ao Gemini para gerar a resposta.")
                    .setPositiveButton("Enviar ao Gemini",(dialog,which)->{sendJarvis(messages,q,true);submit.run();})
                    .setNegativeButton("Resposta local",(dialog,which)->{sendJarvis(messages,q,false);submit.run();})
                    .show();
        });
    }
    void addBubble(LinearLayout messages,String content,boolean user){
        LinearLayout row=new LinearLayout(this);row.setGravity(user?Gravity.RIGHT:Gravity.LEFT);row.setOrientation(LinearLayout.HORIZONTAL);LinearLayout.LayoutParams rp=new LinearLayout.LayoutParams(-1,-2);rp.bottomMargin=dp(12);messages.addView(row,rp);
        if(!user){TextView avatar=text("◉",16,0xff19e6ff);avatar.setGravity(Gravity.CENTER);GradientDrawable av=new GradientDrawable();av.setShape(GradientDrawable.OVAL);av.setColor(0xe603192d);av.setStroke(dp(1),0xaa19e6ff);avatar.setBackground(av);LinearLayout.LayoutParams ap=new LinearLayout.LayoutParams(dp(30),dp(30));ap.rightMargin=dp(8);row.addView(avatar,ap);}
        TextView bubble=text(content,16.5f,0xffe6fcff);bubble.setTypeface(Typography.rajdhaniSemi(this));bubble.setMaxWidth((int)(getResources().getDisplayMetrics().widthPixels*.76f));bubble.setLineSpacing(dp(2),1f);bubble.setPadding(dp(13),dp(10),dp(14),dp(10));GradientDrawable bg=new GradientDrawable(GradientDrawable.Orientation.TL_BR,user?new int[]{0xff0a72d8,0xff0c4fa8}:new int[]{0x2421e6ff,0x1e0a64a8});bg.setCornerRadii(user?new float[]{dp(16),dp(16),dp(4),dp(4),dp(16),dp(16),dp(16),dp(16)}:new float[]{dp(4),dp(4),dp(16),dp(16),dp(16),dp(16),dp(16),dp(16)});bg.setStroke(dp(1),user?0x55ffffff:0x6636d7e8);bubble.setBackground(bg);row.addView(bubble,new LinearLayout.LayoutParams(-2,-2));
    }
    void sendJarvis(LinearLayout messages,String query,boolean useGemini){
        addBubble(messages,query,true);
        TextView typing=text("◉  Jarvis está digitando…",14,0xff19e6ff);
        messages.addView(typing);
        String q=query.toLowerCase(Locale.ROOT);
        boolean spider=q.contains("aranha")||q.contains("spider")||q.contains("peter parker");
        boolean fantastic=q.contains("quarteto")||q.contains("fantastic four")
                ||q.contains("reed richards")||q.contains("sue storm")
                ||q.contains("johnny storm")||q.contains("ben grimm");
        long started=android.os.SystemClock.elapsedRealtime();
        geminiIo.execute(()->{
            String answer;
            try{
                if(!useGemini)throw new IllegalStateException("Local response selected");
                ArrayList<GeminiClient.Turn> context=new ArrayList<>(jarvisHistory);
                context.add(new GeminiClient.Turn("user",query));
                answer=geminiClient.generate(JARVIS_PERSONA,context,null);
            }catch(Exception ignored){answer=jarvisFallback(spider,fantastic);}
            if(useGemini){
                jarvisHistory.add(new GeminiClient.Turn("user",query));
                jarvisHistory.add(new GeminiClient.Turn("model",answer));
                while(jarvisHistory.size()>12)jarvisHistory.remove(0);
            }
            String finalAnswer=answer;
            long minimum=spider||fantastic?5000:750;
            long now=android.os.SystemClock.elapsedRealtime();
            long showAt=Math.max(started+minimum,Math.max(now,jarvisLastDisplayAt+100));
            jarvisLastDisplayAt=showAt;
            long delay=showAt-now;
            main.postDelayed(()->{
                if(isFinishing()||isDestroyed())return;
                messages.removeView(typing);
                addBubble(messages,finalAnswer,false);
            },delay);
        });
    }
    String jarvisFallback(boolean spider,boolean fantastic){
        if(spider)return "Homem-Aranha, senhor. Peter Parker é um estudante de Nova York que ganhou força, agilidade e o sentido aranha após uma picada radioativa. Ele equilibra a rotina de jovem com a de herói.\n\nIdentidade: Peter Parker\nOrigem: Nova York\nPoderes: sentido aranha, agilidade e teia.";
        if(fantastic)return "O Quarteto Fantástico ganhou poderes após atravessar uma tempestade de raios cósmicos.\n\nReed Richards — Sr. Fantástico, corpo elástico e mente brilhante.\nSue Storm — Mulher Invisível, invisibilidade e campos de força.\nJohnny Storm — Tocha Humana, controla o fogo e voa.\nBen Grimm — O Coisa, força e resistência de rocha.";
        return "Perdão, senhor, essa pergunta foge do meu banco de dados da Marvel. Posso falar sobre heróis como o Homem-Aranha e o Quarteto Fantástico. Por onde prefere continuar?";
    }
    Button button(String s,int color){Button b=new Button(this);b.setText(s);b.setTextColor(WHITE);b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(color));return b;}
    void startQuizGeneration(){
        quizSessionStarted=true;
        quizGeneration++;
        int generation=quizGeneration;
        questions.clear();
        questions.addAll(localQuestions);
        quizLoading=geminiClient.configured();
        if(!quizLoading)return;
        HashSet<String> previous=new HashSet<>(previousQuizQuestions);
        quizIo.execute(()->{
            ArrayList<QA> generated=null;
            try{
                String prompt=QuizGenerator.prompt(previous);
                String json=geminiClient.generate(
                        "Você é um criador especializado de perguntas factual e cuidadosamente verificadas para um Quiz do universo Marvel. Retorne somente os dados estruturados solicitados.",
                        java.util.Collections.singletonList(new GeminiClient.Turn("user",prompt)),
                        QuizGenerator.responseFormat());
                generated=QuizGenerator.parse(json);
            }catch(Exception ignored){}
            ArrayList<QA> result=generated;
            main.post(()->{
                if(generation!=quizGeneration||isFinishing()||isDestroyed())return;
                if(result!=null){
                    questions.clear();questions.addAll(result);
                    previousQuizQuestions.clear();
                    for(QA item:result)previousQuizQuestions.add(item.q);
                }
                quizLoading=false;
                if(screen.equals("Quiz"))renderTab();
            });
        });
    }
    void quiz(){
        body.setPadding(dp(18),dp(34),dp(18),dp(110)+navigationInset);
        if(quizLoading){
            TextView loading=text("JARVIS está preparando 24 perguntas para esta sessão…",20,0xfff1ecff);
            loading.setTypeface(Typography.cinzel(this));
            body.addView(loading);
            ProgressBar progress=new ProgressBar(this);
            LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(dp(48),dp(48));lp.topMargin=dp(22);body.addView(progress,lp);
            return;
        }
        if(quizDone){quizCompleteScreen();return;}
        QA q=questions.get(qIndex);String[] gemNames={"Espaço","Mente","Realidade","Poder","Tempo","Alma"};int phase=Math.min(qIndex/4,5);
        LinearLayout top=new LinearLayout(this);top.setGravity(Gravity.BOTTOM);LinearLayout heading=new LinearLayout(this);heading.setOrientation(1);TextView title=text("Desafio das Joias",24,0xfff1ecff);title.setTypeface(Typography.cinzel(this));heading.addView(title);TextView number=text("Pergunta "+(qIndex+1),13,0xffb9aee6);heading.addView(number);top.addView(heading,new LinearLayout.LayoutParams(0,-2,1));LinearLayout score=new LinearLayout(this);score.setOrientation(1);score.setGravity(Gravity.RIGHT);TextView scoreValue=text(String.valueOf(correct),29,WHITE);scoreValue.setTypeface(Typography.cinzel(this));score.addView(scoreValue);score.addView(text("acertos",12,0xffb9aee6));top.addView(score);body.addView(top);
        LinearLayout progress=new LinearLayout(this);LinearLayout.LayoutParams progLp=new LinearLayout.LayoutParams(-1,dp(7));progLp.topMargin=dp(10);body.addView(progress,progLp);for(int i=0;i<4;i++){View segment=new View(this);segment.setBackground(rounded(i<qIndex%4?new int[]{0xff8a5cff,0xff5ea0ff}:new int[]{0x555e4a96,0x555e4a96},5,0,0));LinearLayout.LayoutParams sp=new LinearLayout.LayoutParams(0,-1,1);if(i>0)sp.leftMargin=dp(5);progress.addView(segment,sp);}
        LinearLayout progressText=new LinearLayout(this);LinearLayout.LayoutParams ptl=new LinearLayout.LayoutParams(-1,-2);ptl.topMargin=dp(5);ptl.bottomMargin=dp(14);body.addView(progressText,ptl);TextView next=text("Próxima Joia: "+gemNames[phase],12,0xffb9aee6);progressText.addView(next,new LinearLayout.LayoutParams(0,-2,1));progressText.addView(text((qIndex%4)+"/4 · "+phaseMisses+"/1 erro",12,0xffb9aee6));
        LinearLayout questionCard=new LinearLayout(this);questionCard.setOrientation(1);questionCard.setPadding(dp(18),dp(17),dp(18),dp(19));questionCard.setBackground(rounded(new int[]{0xff392471,0xff15144a},20,0x66b4a0ff,1));LinearLayout.LayoutParams qlp=new LinearLayout.LayoutParams(-1,-2);qlp.bottomMargin=dp(12);body.addView(questionCard,qlp);TextView overline=text("Universo Marvel",12,0xffc4b8f2);overline.setTypeface(Typography.barlowBold(this));questionCard.addView(overline);TextView question=text(q.q,21,0xfff1ecff);question.setTypeface(Typography.cinzel(this));LinearLayout.LayoutParams qtp=new LinearLayout.LayoutParams(-1,-2);qtp.topMargin=dp(5);questionCard.addView(question,qtp);
        for(int i=0;i<q.opts.length;i++){final int index=i;boolean isCorrect=answered&&i==q.answer;boolean isWrong=answered&&i==selected&&i!=q.answer;boolean isSelected=!answered&&i==selected;int tint=isCorrect?0xff2ee6a6:isWrong?0xffff4d7a:isSelected?0xffb48cff:0x66b4a0ff;int bg=isCorrect?0xff17483e:isWrong?0xff4a1c3a:isSelected?0xff3f2d75:0xff171443;LinearLayout option=new LinearLayout(this);option.setGravity(Gravity.CENTER_VERTICAL);option.setPadding(dp(13),0,dp(13),0);option.setBackground(rounded(new int[]{bg,bg},14,tint,isSelected?2:1));LinearLayout.LayoutParams olp=new LinearLayout.LayoutParams(-1,dp(53));olp.bottomMargin=dp(9);body.addView(option,olp);TextView letter=text(isCorrect?"✓":isWrong?"×":String.valueOf((char)('A'+i)),13,isCorrect?0xff0a231c:isWrong?0xff2a0512:0xffcfc4ff);letter.setTypeface(Typography.cinzel(this));letter.setGravity(Gravity.CENTER);letter.setBackground(rounded(isCorrect?new int[]{tint,tint}:isWrong?new int[]{tint,tint}:new int[]{0x22110c37,0x22110c37},9,tint,1));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(dp(28),dp(28));lp.rightMargin=dp(12);option.addView(letter,lp);TextView answerText=text(q.opts[i],16,0xfff1ecff);answerText.setTypeface(Typography.barlowBold(this));option.addView(answerText);option.setOnClickListener(v->{if(!answered){selected=index;renderTab();}});}
        String feedbackText=answered?(selected==q.answer?"✓  Correto!":"×  Incorreto. A resposta era "+q.opts[q.answer]+"."):"Escolha uma alternativa para responder.";TextView feedback=text(feedbackText,14,answered?(selected==q.answer?0xffc8ffec:0xffffd0dc):0xffcfc4ff);feedback.setGravity(Gravity.CENTER_VERTICAL);feedback.setPadding(dp(14),0,dp(14),0);feedback.setBackground(rounded(new int[]{0xff191342,0xff191342},12,answered?(selected==q.answer?0xff2ee6a6:0xffff4d7a):0x55b4a0ff,1));LinearLayout.LayoutParams fbp=new LinearLayout.LayoutParams(-1,dp(44));fbp.topMargin=dp(3);fbp.bottomMargin=dp(12);body.addView(feedback,fbp);
        if(answered&&!q.explanation.isEmpty()){TextView explanation=text(q.explanation,13,0xffcfc4ff);LinearLayout.LayoutParams ep=new LinearLayout.LayoutParams(-1,-2);ep.bottomMargin=dp(10);body.addView(explanation,ep);}
        Button action=new Button(this);action.setAllCaps(false);action.setText(answered?"Próxima":"Confirmar resposta");action.setTextSize(16);action.setTypeface(Typography.barlowBold(this));action.setTextColor(0xff1b1036);boolean canProceed=answered||selected>=0;action.setBackground(rounded(canProceed?new int[]{0xffc7a8ff,0xff7fa8ff}:new int[]{0xff69518a,0xff415987},14,0,0));action.setEnabled(canProceed);body.addView(action,new LinearLayout.LayoutParams(-1,dp(50)));action.setOnClickListener(v->{if(!answered){boolean right=selected==q.answer;if(right)correct++;else phaseMisses++;if(phaseMisses>=2){resetQuiz();Toast.makeText(this,"Duas respostas erradas na fase. O desafio recomeçou.",Toast.LENGTH_LONG).show();renderTab();scrollBodyTop();return;}answered=true;renderTab();}else{answered=false;selected=-1;qIndex++;if(qIndex%4==0){gems=qIndex/4;phaseMisses=0;}if(qIndex==24){quizDone=true;thanosUnlocked=true;}renderTab();scrollBodyTop();if(qIndex%4==0){int gem=gems;main.postDelayed(()->showGemUnlock(gem),550);}}});
        LinearLayout trayTitle=new LinearLayout(this);LinearLayout.LayoutParams ttl=new LinearLayout.LayoutParams(-1,-2);ttl.topMargin=dp(16);ttl.bottomMargin=dp(7);body.addView(trayTitle,ttl);trayTitle.addView(text("Joias conquistadas",13,0xffb9aee6),new LinearLayout.LayoutParams(0,-2,1));trayTitle.addView(text(gems+" de 6",13,0xffb9aee6));LinearLayout tray=new LinearLayout(this);tray.setPadding(dp(8),dp(9),dp(8),dp(9));tray.setBackground(rounded(new int[]{0xc4181036,0xc4181036},18,0x55b4a0ff,1));body.addView(tray);String[] shortNames={"Espaço","Mente","Realidade","Poder","Tempo","Alma"};int[] gemColors={0xff3d7bff,0xffffd23f,0xffff3b4d,0xffa05cff,0xff55da7d,0xffff9a38};for(int i=0;i<6;i++){boolean unlocked=i<gems;LinearLayout slot=new LinearLayout(this);slot.setOrientation(1);slot.setGravity(Gravity.CENTER);if(unlocked){GemArtView icon=new GemArtView(this,gemColors[i]);slot.addView(icon,new LinearLayout.LayoutParams(dp(25),dp(25)));}else{ImageView lock=new ImageView(this);lock.setImageResource(R.drawable.ic_gem_lock);LinearLayout.LayoutParams lockLp=new LinearLayout.LayoutParams(dp(22),dp(22));slot.addView(lock,lockLp);}TextView label=text(unlocked?shortNames[i]:"Bloqueada",9,unlocked?0xffe4dcff:0xff8f84c0);label.setGravity(Gravity.CENTER);slot.addView(label);tray.addView(slot,new LinearLayout.LayoutParams(0,dp(56),1));}
    }
    void addGemProgress(int unlocked){LinearLayout row=new LinearLayout(this);row.setGravity(Gravity.CENTER);String[] names={"ESP","MEN","REA","POD","TEM","ALM"};int[] colors={0xff36bfff,0xffffdb47,0xffff4d54,0xffb455ff,0xff55da7d,0xffff9a38};for(int i=0;i<6;i++){LinearLayout gem=new LinearLayout(this);gem.setOrientation(1);gem.setGravity(Gravity.CENTER);TextView icon=text("◆",22,i<unlocked?colors[i]:0xff4a4b5d);icon.setGravity(Gravity.CENTER);gem.addView(icon);TextView label=text(names[i],8,i<unlocked?WHITE:0xff77798a);gem.addView(label);row.addView(gem,new LinearLayout.LayoutParams(0,-2,1));}LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.topMargin=8;lp.bottomMargin=5;body.addView(row,lp);}
    void resetQuiz(){
        qIndex=0;correct=0;gems=0;phaseMisses=0;selected=-1;answered=false;quizDone=false;
        startQuizGeneration();
    }
    void quizCompleteScreen(){
        TextView heading=text("DESAFIO CONCLUÍDO",30,WHITE);
        heading.setTypeface(Typography.cinzel(this));
        heading.setGravity(Gravity.CENTER);
        body.addView(heading);
        TextView score=text(correct+" acertos · 6 Joias do Infinito",16,0xffd5cbff);
        score.setGravity(Gravity.CENTER);
        body.addView(score);
        GauntletView gauntlet=new GauntletView(this,6);
        LinearLayout.LayoutParams glove=new LinearLayout.LayoutParams(-1,dp(260));
        glove.topMargin=dp(26);glove.bottomMargin=dp(18);
        body.addView(gauntlet,glove);
        Button restart=button("REINICIAR",0xff9b8cff);
        restart.setAllCaps(false);
        body.addView(restart,new LinearLayout.LayoutParams(-1,dp(52)));
        restart.setOnClickListener(v->{resetQuiz();renderTab();scrollBodyTop();});
    }
    void showGemUnlock(int number){
        if(!screen.equals("Quiz")||number<1||number>6)return;
        nav.setVisibility(View.GONE);
        FrameLayout overlay=new FrameLayout(this);
        overlay.setTag("gem_overlay");
        overlay.setBackground(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{0xf30f0b38,0xf6070418}));
        LinearLayout stack=new LinearLayout(this);
        stack.setOrientation(1);
        stack.setGravity(Gravity.CENTER);
        FrameLayout.LayoutParams stackLp=new FrameLayout.LayoutParams(-1,-1);
        stackLp.setMargins(dp(22),0,dp(22),0);
        overlay.addView(stack,stackLp);
        int[] colors={0xff3d7bff,0xffffd23f,0xffff3b4d,0xffa05cff,0xff55da7d,0xffff9a38};
        String[] names={"Espaço","Mente","Realidade","Poder","Tempo","Alma"};
        GemArtView gem=new GemArtView(this,colors[number-1]);
        stack.addView(gem,new LinearLayout.LayoutParams(-1,dp(145)));
        gem.setScaleX(.45f);gem.setScaleY(.45f);
        gem.animate().scaleX(1f).scaleY(1f).setDuration(550).start();
        TextView heading=text("NOVA JOIA DO INFINITO\nDESBLOQUEADA!",24,WHITE);
        heading.setTypeface(Typography.cinzel(this));
        heading.setGravity(Gravity.CENTER);
        stack.addView(heading);
        TextView info=text("Joia do "+names[number-1]+" · "+number+" de 6 · "+correct+" acertos",
                14,0xffd5cbff);
        info.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams infoLp=new LinearLayout.LayoutParams(-1,-2);
        infoLp.topMargin=dp(8);
        stack.addView(info,infoLp);
        GauntletView gauntlet=new GauntletView(this,number);
        LinearLayout.LayoutParams glove=new LinearLayout.LayoutParams(dp(168),dp(178));
        glove.gravity=Gravity.CENTER;
        glove.topMargin=dp(22);glove.bottomMargin=dp(20);
        stack.addView(gauntlet,glove);
        Button continueButton=button("Continuar",0xff9b8cff);
        continueButton.setAllCaps(false);
        stack.addView(continueButton,new LinearLayout.LayoutParams(dp(220),dp(50)));
        continueButton.setOnClickListener(v->{fragmentSurface.removeView(overlay);nav.setVisibility(View.VISIBLE);});
        fragmentSurface.addView(overlay,new FrameLayout.LayoutParams(-1,-1));
    }
    void fights(){new BattleScreens(this).home();}
    GradientDrawable rounded(int[] colors,int radius,int strokeColor,int strokeWidth){GradientDrawable d=new GradientDrawable(GradientDrawable.Orientation.TL_BR,colors);d.setCornerRadius(dp(radius));if(strokeWidth>0)d.setStroke(dp(strokeWidth),strokeColor);return d;}
    Button styledAction(String label,boolean enabled){Button action=new Button(this);action.setText(label);action.setAllCaps(false);action.setTextSize(16);action.setTypeface(Typography.barlowBold(this));action.setTextColor(enabled?0xff180c09:0xff251814);action.setBackground(rounded(enabled?new int[]{0xffffaa20,0xffff711e}:new int[]{0xff68431a,0xff81391e},14,0,0));action.setEnabled(enabled);return action;}
    TextView roleBadge(String name,boolean bot){TextView badge=text(name,10,bot?WHITE:0xff201108);badge.setTypeface(Typography.bebas(this));badge.setGravity(Gravity.CENTER);badge.setPadding(dp(7),dp(3),dp(7),dp(3));badge.setBackground(rounded(new int[]{bot?0xffee2738:0xffffaf19,bot?0xffee2738:0xffffaf19},5,0,0));return badge;}
    void scrollBodyTop(){if(body.getParent() instanceof ScrollView){ScrollView scroll=(ScrollView)body.getParent();scroll.post(()->scroll.scrollTo(0,0));}}
    boolean characterLockedByQuiz(int index){return index==12&&!thanosUnlocked;}
    boolean characterLockedByStory(int index){return index==11&&!doomUnlocked;}
    void pickScreen(){new BattleScreens(this).selection();}
    void preFight(){new BattleScreens(this).versus();}
    // Keep the five-tab bar above gesture or three-button system navigation.
    void installNavInsets(){root.setOnApplyWindowInsetsListener((view,insets)->{int systemBottom=insets.getSystemWindowInsetBottom(),delta=systemBottom-navigationInset;navigationInset=systemBottom;FrameLayout.LayoutParams lp=(FrameLayout.LayoutParams)nav.getLayoutParams();int desired=systemBottom+dp(28);if(lp.bottomMargin!=desired){lp.bottomMargin=desired;nav.setLayoutParams(lp);}if(body!=null&&delta!=0)body.setPadding(body.getPaddingLeft(),body.getPaddingTop(),body.getPaddingRight(),body.getPaddingBottom()+delta);if(fragmentSurface!=null&&delta!=0){View action=fragmentSurface.findViewWithTag("screen_action");if(action!=null){FrameLayout.LayoutParams ap=(FrameLayout.LayoutParams)action.getLayoutParams();ap.bottomMargin+=delta;action.setLayoutParams(ap);}}return insets;});root.requestApplyInsets();}
    @Override protected void onActivityResult(int requestCode,int resultCode,Intent data){super.onActivityResult(requestCode,resultCode,data);if(requestCode==722&&resultCode==RESULT_OK&&data!=null&&data.getBooleanExtra("battle_select_again",false)){choosingBot=false;playerChosen=botChosen=true;open("Seleção");return;}if(requestCode==721&&resultCode==RESULT_OK&&data!=null){int battle=data.getIntExtra("story_battle",-1);if(battle>=0){open("História");storyMode.battleResult(battle,data.getBooleanExtra("story_victory",false));}}}
    @Override public void onBackPressed(){if(screen.equals("História")&&storyMode.phase>0){storyMode.back();return;}if(screen.equals("Pré-luta")){screen="Seleção";choosingBot=true;renderTab();makeNav();return;}if(screen.equals("Seleção")){if(choosingBot){choosingBot=false;renderTab();}else open("Lutas");return;}if(screen.equals("Detalhes")){open(previousScreen);return;}if(!screen.equals("Home")){open("Home");return;}super.onBackPressed();}
    int dp(float value){return Math.round(value*getResources().getDisplayMetrics().density);}
    void makeNav(){
        nav.removeAllViews();
        nav.setVisibility(screen.equals("Pré-luta")||screen.equals("Detalhes")?View.GONE:View.VISIBLE);
        GradientDrawable shell=new GradientDrawable();shell.setColor(0xe60d0f16);shell.setCornerRadius(dp(24));shell.setStroke(dp(1),0x26ffffff);nav.setBackground(shell);nav.setPadding(dp(4),0,dp(4),0);
        String[] tabs={"Home","Jarvis","Quiz","História","Lutas"};int[] icons={R.drawable.ic_nav_home,R.drawable.ic_nav_jarvis,R.drawable.ic_nav_quiz,R.drawable.ic_nav_story,R.drawable.ic_nav_fight};
        for(int i=0;i<tabs.length;i++){
            final String tab=tabs[i];boolean active=screen.equals(tab)||(tab.equals("Lutas")&&screen.equals("Seleção"));int color=active?accentFor(tab):0xff7f8699;
            FrameLayout item=new FrameLayout(this);item.setForeground(new android.graphics.drawable.RippleDrawable(android.content.res.ColorStateList.valueOf(0x22ffffff),null,rounded(new int[]{0xffffffff,0xffffffff},22,0,0)));
            LinearLayout stack=new LinearLayout(this);stack.setOrientation(1);stack.setGravity(Gravity.CENTER);FrameLayout.LayoutParams sl=new FrameLayout.LayoutParams(-1,-1);item.addView(stack,sl);
            if(active){View indicator=new View(this);GradientDrawable line=new GradientDrawable();line.setColor(color);line.setCornerRadius(dp(2));indicator.setBackground(line);indicator.setElevation(dp(3));FrameLayout.LayoutParams il=new FrameLayout.LayoutParams(dp(28),dp(2),Gravity.TOP|Gravity.CENTER_HORIZONTAL);item.addView(indicator,il);}
            ImageView icon=new ImageView(this);icon.setImageResource(icons[i]);icon.setColorFilter(color);LinearLayout.LayoutParams ilp=new LinearLayout.LayoutParams(dp(25),dp(25));stack.addView(icon,ilp);if(active)icon.setElevation(dp(4));
            TextView label=text(tab,10.5f,color);label.setTypeface(Typography.barlowSemi(this));LinearLayout.LayoutParams tl=new LinearLayout.LayoutParams(-2,-2);tl.topMargin=dp(1);stack.addView(label,tl);
            nav.addView(item,new LinearLayout.LayoutParams(0,-1,1));item.setOnClickListener(v->open(tab));
        }
    }
    int accentFor(String tab){return tab.equals("Home")?0xffff4a4a:tab.equals("Jarvis")?0xff19e6ff:tab.equals("Quiz")?0xffb48cff:tab.equals("História")?0xff73e5ff:0xffff9a43;}
    void seedQuestions(){
        String[][] levels={
            {"Qual Vingador é famoso por ser um milionário, gênio, playboy e filantropo?","Thor|Tony Stark (Homem de Ferro)|Bruce Banner|Steve Rogers","1"},
            {"Qual é a arma principal que o Thor usa para canalizar seus trovões na maioria dos filmes?","Um machado de duas lâminas|Um martelo mágico chamado Mjolnir|Uma lança de ouro|Um arco e flecha","1"},
            {"O que acontece com o Hulk quando Bruce Banner fica extremamente irritado?","Ele dorme|Ele fica invisível|Ele se transforma em um gigante verde poderoso|Ele voa para o espaço","2"},
            {"Quem é o irmão adotivo e muito trapaceiro do Thor?","Loki|Odin|Heimdall|Thanos","0"},
            {"Qual comida os Vingadores se reúnem para comer em silêncio após derrotarem os alienígenas no final de \"Os Vingadores\" (2012)?","Hambúrguer|Shawarma|Pizza|Cachorro-quente","1"},
            {"Qual é a cor da pele do vilão Thanos?","Verde|Azul|Roxa|Vermelha","2"},
            {"Qual o nome da nave onde o Senhor das Estrelas e os Guardiões da Galáxia viajam no primeiro filme?","A Milano|A Millennium|A Enterprise|A Apollo","0"},
            {"Qual é o disfarce de trabalho diurno de Peter Parker nos quadrinhos e filmes quando ele não está como Homem-Aranha?","Policial|Fotógrafo / Estudante|Cientista do laboratório|Entregador de pizza","1"},
            {"Qual animal inspira o traje e o nome do herói Scott Lang?","Besouro|Formiga|Aranha|Vespa","1"},
            {"O que Thanos precisa juntar em sua manopla para ter poder total sobre o universo?","Anéis Mágicos|As 6 Joias do Infinito|Cristais de Energia|Moedas Sagradas","1"},
            {"Antes de se tornar o Doutor Estranho e aprender magia, qual era a profissão de Stephen Strange?","Piloto de avião|Neurocirurgião|Professor de história|Advogado","1"},
            {"Qual é a fala famosa que a árvore humanoide Groot repete quase o tempo todo?","\"Eu sou o Groot\"|\"Groot ataca\"|\"Nós somos a Galáxia\"|\"Groot é forte\"","0"},
            {"Qual Vingador é conhecido por usar um arco e flechas de alta tecnologia em suas missões?","Homem de Ferro|Gavião Arqueiro|Capitão América|Falcão","1"},
            {"Em \"Guardiões da Galáxia\", qual objeto dos anos 80 o Senhor das Estrelas guarda com carinho para ouvir suas músicas?","Um Walkman (toca-fitas)|Um rádio de pilha grande|Um toca-discos de vinil|Um iPod","0"},
            {"Qual é a cor da Joia do Tempo, que ficava dentro do amuleto do Doutor Estranho?","Azul|Vermelha|Verde|Amarela","2"},
            {"Onde fica escondido o laboratório/oficina onde Tony Stark constrói suas primeiras armaduras em casa?","No sótão|Na garagem/subsolo|No terraço|Na cozinha","1"},
            {"Qual é o nome do melhor amigo do Peter Parker que o ajuda descobrindo coisas no computador?","Ned|Harry|Flash|MJ","0"},
            {"Qual comida de fast-food Tony Stark pede assim que é resgatado no primeiro filme do Homem de Ferro?","Tacos do Taco Bell|Hambúrguer do Burger King|Pizza da Pizza Hut|Frango do KFC","1"},
            {"O que o Homem-Formiga usa em seu traje para conseguir encolher ou gigantecer?","Energia Gama|Partículas Pym|Ray-X|Nanobots","1"},
            {"Qual heroína tem poderes telepáticos, lança energia vermelha e é irmã do Mercúrio?","Viúva Negra|Feiticeira Escarlate (Wanda)|Capitã Marvel|Gamora","1"},
            {"Em \"Vingadores: Ultimato\", qual jogo de videogame o Thor aparece jogando com os amigos no seu refúgio?","Minecraft|Fortnite|FIFA|Call of Duty","1"},
            {"Qual é o nome do computador de Inteligência Artificial que conversa com o Tony Stark no primeiro filme?","J.A.R.V.I.S.|S.I.R.I.|C.O.R.T.A.N.A.|A.L.E.X.A.","0"},
            {"O que Thanos faz com os dedos quando consegue reunir todas as Joias do Infinito para apagar metade das vidas do universo?","Um soco no chão|Um estalo de dedos|Aponta a mão para o céu|Bate as mãos","1"},
            {"Quem é a melhor amiga e parceira de missões de longa data do Gavião Arqueiro na SHIELD?","Wanda Maximoff|Natasha Romanoff (Viúva Negra)|Carol Danvers|Jane Foster","1"}
        };
        for(String[] item:levels)
            questions.add(new QA(item[0],item[1].split("\\|"),Integer.parseInt(item[2])));
        localQuestions.addAll(questions);
    }
}


