//caixas e menus story mode
package com.example.projetomarvel;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

/** Native XML/Java Story Mode state machine matching the web campaign phases. */
final class StoryMode {
    static final String[] PHASES={"landing","hero_choice","name","appearance","color","power","personality1","personality2","summary","intro1","intro2","dialogue1","choice1","consequence1","pre1","after1","dialogue2","choice2","consequence2","pre2","after2","reveal","prep","pre3","ending"};
    static final String[] COLORS={"Vermelho","Azul","Verde","Roxo","Amarelo","Laranja","Ciano"};
    static final int[] COLOR_VALUES={0xffe94d65,0xff468dff,0xff52d98b,0xffaa75ef,0xfff2d15b,0xffff995a,0xff55dce9};
    static final String[] POWERS={"Energia","Gelo","Velocidade","Super Força","Ninja","Telecinese"};
    static final String[] POWER_DESC={"Rajada + explosão","Congelamento + chão de gelo","Ataques e movimento rápidos","Golpes +3, passo mais lento","Espada, shurikens e fumaça","Empurrar, puxar e objetos"};
    static final String[] ENEMIES={"Wolverine de outra dimensão","Homem-Aranha de outra dimensão","Dr. Doom"};
    static final String[] STAKES={"Um Wolverine de outra dimensão bloqueia a passagem dos sobreviventes.","Um Homem-Aranha de outra dimensão, parecido com o de Tobey Maguire, protege o portal.","Se você cair, Doom apaga seu universo."};
    private final MainActivity h;
    int phase=0,color=1,power=0,wins=0;
    int[] personality={-1,-1},choices={-1,-1};
    int build=1,skin=1,hair=0,style=0;
    String name="", selectedHero="";
    StoryMode(MainActivity host){
        h=host;
        // Remove campaign data saved by older versions; this story is one sitting only.
        h.getSharedPreferences("story_mode",0).edit().clear().apply();
    }
    private int d(float v){return h.dp(v);}
    private int accent(){return phase>=21&&phase<=23?0xff68dc81:selectedHero.isEmpty()?COLOR_VALUES[color]:FighterArtView.accent(selectedHero);}
    int themeColor(){return selectedHero.isEmpty()?COLOR_VALUES[color]:FighterArtView.primary(selectedHero);}
    private String protagonist(){return selectedHero.isEmpty()?name:selectedHero;}
    private String ally(String hero){
        if(!selectedHero.equals(hero))return hero;
        return hero.equals("Homem de Ferro")?"J.A.R.V.I.S.":"Wong";
    }
    private TextView t(String s,float size,int color){return h.text(s,size,color);}
    private void save(){/* Campaign data intentionally stays in memory for one sitting. */}
    private GradientDrawable shape(int a,int b,int radius,int stroke){return h.rounded(new int[]{a,b},radius,stroke,1);}
    private LinearLayout.LayoutParams gap(int height,int bottom){LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,height<0?height:d(height));p.bottomMargin=d(bottom);return p;}
    private void add(String value,float size,int color,int bottom){TextView v=t(value,size,color);v.setLineSpacing(d(2),1f);h.body.addView(v,gap(-2,bottom));}
    private void kicker(String s){TextView v=t(s.toUpperCase(),11,accent());v.setTypeface(Typography.rajdhaniBold(h));v.setLetterSpacing(.16f);h.body.addView(v,gap(-2,4));}
    private void title(String s){TextView v=t(s,42,Color.WHITE);v.setTypeface(Typography.bebas(h));v.setLineSpacing(d(-4),1f);h.body.addView(v,gap(-2,13));}
    private void card(String heading,String copy){LinearLayout c=new LinearLayout(h);c.setOrientation(1);c.setPadding(d(15),d(14),d(15),d(14));c.setBackground(shape(0xff1c304d,0xff151a32,18,accent()));TextView head=t(heading.toUpperCase(),24,Color.WHITE);head.setTypeface(Typography.bebas(h));c.addView(head);c.addView(t(copy,14,0xffd4def0));h.body.addView(c,gap(-2,13));}
    private void narration(String s){TextView v=t(s,13,0xffc8d9ef);v.setPadding(d(13),d(9),d(12),d(9));v.setBackground(shape(0xff17263f,0xff101a30,0,accent()));h.body.addView(v,gap(-2,13));}
    private void art(String caption,boolean figure,boolean doom){
        FrameLayout box=new FrameLayout(h);
        box.setBackground(shape(doom?0xff102e20:0xff172945,
                doom?0xff193527:0xff28172f,24,doom?0xff68dc81:0x6689cfea));
        box.setClipToOutline(true);
        box.addView(new StoryArtView(h,doom),new FrameLayout.LayoutParams(-1,-1));
        if(figure){
            String shown=caption.equals("Dr. Doom")?"Dr. Doom":protagonist();
            FighterArtView avatar=caption.equals("Dr. Doom")||!selectedHero.isEmpty()
                    ?new FighterArtView(h,shown,false)
                    :new FighterArtView(h,shown.isEmpty()?"Seu herói":shown,false,
                            COLOR_VALUES[color],skin,build,hair,style);
            if(selectedHero.isEmpty()&&!caption.equals("Dr. Doom"))avatar.setPower(POWERS[power]);
            FrameLayout.LayoutParams ap=new FrameLayout.LayoutParams(d(150),d(206),
                    Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL);
            ap.bottomMargin=d(8);
            box.addView(avatar,ap);
        }
        TextView cap=t(caption.toUpperCase(),11,Color.WHITE);
        cap.setTypeface(Typeface.DEFAULT_BOLD);
        cap.setPadding(d(9),d(5),d(9),d(5));
        cap.setBackground(shape(0xdd06101b,0xdd06101b,8,0x55ffffff));
        FrameLayout.LayoutParams cl=new FrameLayout.LayoutParams(-2,-2,Gravity.LEFT|Gravity.BOTTOM);
        cl.setMargins(d(13),0,0,d(12));
        box.addView(cap,cl);
        h.body.addView(box,gap(figure?244:220,14));
    }
    private void appearanceRow(String label,String[] values,int chosen,java.util.function.IntConsumer select){
        kicker(label);
        LinearLayout row=new LinearLayout(h);
        for(int i=0;i<values.length;i++){
            final int option=i;
            TextView chip=t(values[i],12,Color.WHITE);
            chip.setGravity(Gravity.CENTER);
            chip.setPadding(d(4),d(7),d(4),d(7));
            chip.setBackground(shape(i==chosen?COLOR_VALUES[color]:0xff1a2d49,
                    i==chosen?COLOR_VALUES[color]:0xff1a2d49,10,
                    i==chosen?Color.WHITE:0xff4c6284));
            LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,-2,1);
            if(i>0)lp.leftMargin=d(6);
            row.addView(chip,lp);
            chip.setOnClickListener(v->{
                int scrollY=h.body.getParent() instanceof android.widget.ScrollView
                        ?((android.widget.ScrollView)h.body.getParent()).getScrollY():0;
                select.accept(option);
                save();render();
                if(h.body.getParent() instanceof android.widget.ScrollView)
                    ((android.widget.ScrollView)h.body.getParent()).post(
                            ()->((android.widget.ScrollView)h.body.getParent()).scrollTo(0,scrollY));
            });
        }
        h.body.addView(row,gap(-2,10));
    }
    private void appearanceControls(){
        appearanceRow("Apresentação",new String[]{"Masculina","Feminina","Livre"},style,value->style=value);
        appearanceRow("Porte",new String[]{"Esbelto","Atlético","Forte"},build,value->build=value);
        appearanceRow("Tom de pele",new String[]{"Claro","Médio","Escuro"},skin,value->skin=value);
        appearanceRow("Cabelo",new String[]{"Curto","Longo","Raspado"},hair,value->hair=value);
    }
    private void dialog(String who,String text,boolean self){LinearLayout row=new LinearLayout(h);row.setGravity(Gravity.TOP);TextView avatar=t(self?"◆":who.substring(0,1),20,0xffabecff);avatar.setGravity(Gravity.CENTER);avatar.setBackground(shape(self?themeColor():0xff995271,0xff172745,13,0x66ffffff));LinearLayout.LayoutParams av=new LinearLayout.LayoutParams(d(42),d(42));if(!self){av.rightMargin=d(10);row.addView(avatar,av);}LinearLayout bubble=new LinearLayout(h);bubble.setOrientation(1);bubble.setPadding(d(12),d(10),d(12),d(10));bubble.setBackground(shape(self?0xff20445a:0xff21324d,self?0xff20445a:0xff21324d,14,0xff435879));TextView speaker=t(who.toUpperCase(),11,0xff90e6f7);speaker.setTypeface(Typography.rajdhaniBold(h));speaker.setLetterSpacing(.08f);bubble.addView(speaker);bubble.addView(t(text,14,Color.WHITE));row.addView(bubble,new LinearLayout.LayoutParams(0,-2,1));if(self){av.leftMargin=d(10);row.addView(avatar,av);}h.body.addView(row,gap(-2,11));}
    private void options(String[] choicesText,String[] subs,int current,java.util.function.IntConsumer select){for(int i=0;i<choicesText.length;i++){final int option=i;LinearLayout v=new LinearLayout(h);v.setOrientation(1);v.setPadding(d(14),d(11),d(14),d(11));v.setBackground(shape(i==current?0xff20536b:0xff1a2b47,i==current?0xff20536b:0xff1a2b47,14,i==current?accent():0xff566c8f));TextView sub=t((char)('A'+i)+" · "+subs[i],11,0xff86e8fa);sub.setTypeface(Typeface.DEFAULT_BOLD);v.addView(sub);v.addView(t(choicesText[i],14,Color.WHITE));h.body.addView(v,gap(-2,9));v.setOnClickListener(view->select.accept(option));}}
    private void header(){LinearLayout row=new LinearLayout(h);row.setGravity(Gravity.CENTER_VERTICAL);if(phase!=0&&phase!=24){TextView back=t("‹",30,Color.WHITE);back.setGravity(Gravity.CENTER);back.setBackground(shape(0xff182239,0xff182239,11,0x66ffffff));row.addView(back,new LinearLayout.LayoutParams(d(34),d(34)));back.setOnClickListener(v->{phase=Math.max(0,phase-1);save();render();});}TextView brand=t("MARVEL UNIVERSE /\nSTORY MODE",11,accent());brand.setTypeface(Typography.rajdhaniBold(h));brand.setLetterSpacing(.13f);LinearLayout.LayoutParams bl=new LinearLayout.LayoutParams(0,-2,1);bl.leftMargin=d(9);row.addView(brand,bl);TextView act=t(phase<9?"ORIGEM":phase<16?"ATO 1 · DESCOBERTA":phase<21?"ATO 2 · CONFLITO":"ATO 3 · DESTINO",10,0xffc5d5e9);act.setPadding(d(8),d(5),d(8),d(5));act.setBackground(shape(0xff122139,0xff122139,20,0xff52637e));row.addView(act);h.body.addView(row,gap(-2,14));LinearLayout progress=new LinearLayout(h);for(int i=0;i<4;i++){int step=phase<9?0:phase<16?1:phase<21?2:3;View seg=new View(h);seg.setBackground(shape(i<=step?accent():0xff2a3d59,i<=step?0xff73e5ff:0xff2a3d59,4,0));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,d(4),1);if(i>0)lp.leftMargin=d(5);progress.addView(seg,lp);}h.body.addView(progress,gap(4,14));}
    private void chapters(){LinearLayout row=new LinearLayout(h);String[] labels={"1. Descoberta","2. Conflito","3. Doom"};for(int i=0;i<3;i++){TextView x=t(labels[i],11,i<wins?0xff7af0af:i==wins?0xffb0f6ff:0xff9cb2c9);x.setGravity(Gravity.CENTER);x.setPadding(d(2),d(8),d(2),d(8));x.setBackground(shape(i==wins?0xff21465f:0xff172945,i==wins?0xff21465f:0xff172945,8,i==wins?0xff68deed:0xff34506e));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,-2,1);if(i>0)lp.leftMargin=d(6);row.addView(x,lp);}h.body.addView(row,gap(-2,12));}
    private void footer(String text,boolean enabled,Runnable action){Button button=new Button(h);button.setText(text);button.setAllCaps(false);button.setTextSize(15);button.setTypeface(Typography.barlowBold(h));button.setTextColor(0xff071b2c);button.setBackground(shape(enabled?accent():0xff4a708c,enabled?accent():0xff3e536c,14,0));button.setEnabled(enabled);button.setTag("screen_action");FrameLayout.LayoutParams bp=new FrameLayout.LayoutParams(-1,d(50),Gravity.BOTTOM);bp.setMargins(d(20),0,d(20),d(106)+h.navigationInset);h.fragmentSurface.addView(button,bp);button.setOnClickListener(v->action.run());}
    private void secondary(String label,Runnable action){Button button=new Button(h);button.setText(label);button.setAllCaps(false);button.setTextSize(14);button.setTypeface(Typography.barlowSemi(h));button.setTextColor(0xffecf7ff);button.setBackground(shape(0xff24344d,0xff24344d,14,0xff59708f));h.body.addView(button,gap(48,10));button.setOnClickListener(v->action.run());}
    private void next(){phase=Math.min(PHASES.length-1,phase+1);save();render();}
    void back(){phase=Math.max(0,phase-1);save();render();}
    private void choice(int slot,int option){choices[slot]=option;save();render();}
    private void personality(int slot,int option){personality[slot]=option;save();render();}
    private void startFight(int number){Intent intent=new Intent(h,FightActivity.class);intent.putExtra("player",protagonist());intent.putExtra("bot",ENEMIES[number]);intent.putExtra("story_battle",number);intent.putExtra("story_color",selectedHero.isEmpty()?COLOR_VALUES[color]:-1);intent.putExtra("story_skin",selectedHero.isEmpty()?skin:-1);intent.putExtra("story_build",build);intent.putExtra("story_hair",hair);intent.putExtra("story_style",style);intent.putExtra("story_power",selectedHero.isEmpty()?POWERS[power]:selectedHero);intent.putExtra("story_choice_rescue",choices[0]==0);intent.putExtra("story_choice_tech",choices[1]==0);h.startActivityForResult(intent,721);}
    void battleResult(int number,boolean victory){if(victory){wins=Math.max(wins,number+1);if(number==2)h.doomUnlocked=true;phase=number==0?15:number==1?20:24;save();render();}}
    private void resetState(int startPhase){phase=startPhase;name="";selectedHero="";color=1;power=0;wins=0;build=1;skin=1;hair=0;style=0;personality=new int[]{-1,-1};choices=new int[]{-1,-1};save();}
    void resetForTabExit(){resetState(0);}
    private void restart(){resetState(1);render();}
    private void chooseProtagonist(){
        kicker("Antes da campanha");
        title("QUEM VAI SALVAR\nSEU UNIVERSO?");
        add("Crie um personagem seu ou jogue a campanha com um herói já disponível.",
                15,0xffc5d4e8,14);
        for(int row=0;row<6;row++){
            LinearLayout line=new LinearLayout(h);
            LinearLayout.LayoutParams lineLp=gap(75,8);
            h.body.addView(line,lineLp);
            for(int column=0;column<2;column++){
                int index=row*2+column;
                String hero=index==0?"":h.roster[index-1];
                boolean active=selectedHero.equals(hero);
                LinearLayout card=new LinearLayout(h);
                card.setGravity(Gravity.CENTER_VERTICAL);
                card.setPadding(d(7),d(5),d(7),d(5));
                card.setBackground(shape(active?0xff284e68:0xff182943,
                        active?0xff203b56:0xff18223a,13,active?accent():0xff52617c));
                LinearLayout.LayoutParams cardLp=new LinearLayout.LayoutParams(0,-1,1);
                if(column==1)cardLp.leftMargin=d(7);
                line.addView(card,cardLp);
                if(index==0){
                    TextView custom=t("◆",27,accent());
                    custom.setGravity(Gravity.CENTER);
                    card.addView(custom,new LinearLayout.LayoutParams(d(42),d(52)));
                }else card.addView(new CharacterPortraitView(h,hero),
                        new LinearLayout.LayoutParams(d(48),d(48)));
                TextView label=t(index==0?"Meu personagem":hero,15,Color.WHITE);
                label.setTypeface(Typography.bebas(h));
                label.setPadding(d(6),0,0,0);
                card.addView(label,new LinearLayout.LayoutParams(0,-2,1));
                card.setOnClickListener(v->{selectedHero=hero;save();render();});
            }
        }
        footer(selectedHero.isEmpty()?"Criar meu personagem":"Jogar com "+selectedHero,
                true,()->{phase=selectedHero.isEmpty()?2:6;save();render();});
    }
    private void preBattle(int battle){
        chapters();
        kicker(battle==2?"BATALHA FINAL":"Batalha "+(battle+1)+" / 3");
        title(battle==2?"O DESTINO DO\nUNIVERSO":"O PORTAL É O\nCAMPO DE BATALHA");
        LinearLayout fighters=new LinearLayout(h);
        fighters.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL);
        FighterArtView me=selectedHero.isEmpty()
                ?new FighterArtView(h,protagonist(),false,COLOR_VALUES[color],skin,build,hair,style)
                :new FighterArtView(h,protagonist(),false);
        if(selectedHero.isEmpty())me.setPower(POWERS[power]);
        FighterArtView enemy=new FighterArtView(h,ENEMIES[battle],true);
        fighters.addView(me,new LinearLayout.LayoutParams(0,d(218),1));
        TextView vs=t("VS",57,Color.WHITE);
        vs.setGravity(Gravity.CENTER);
        vs.setTypeface(Typeface.create(Typography.bebas(h),Typeface.ITALIC));
        vs.setShadowLayer(d(11),0,0,0xffff7c53);
        fighters.addView(vs,new LinearLayout.LayoutParams(d(65),d(160)));
        fighters.addView(enemy,new LinearLayout.LayoutParams(0,d(218),1));
        h.body.addView(fighters,gap(258,9));
        LinearLayout names=new LinearLayout(h);
        TextView myName=t(protagonist().toUpperCase(),25,Color.WHITE);
        TextView enemyName=t(ENEMIES[battle].toUpperCase(),25,Color.WHITE);
        myName.setTypeface(Typography.bebas(h));
        enemyName.setTypeface(Typography.bebas(h));
        enemyName.setGravity(Gravity.RIGHT);
        names.addView(myName,new LinearLayout.LayoutParams(0,-2,1));
        names.addView(enemyName,new LinearLayout.LayoutParams(0,-2,1));
        h.body.addView(names,gap(-2,10));
        narration(STAKES[battle]);
        add(battle==0?"Aprenda os controles: Frente e Trás para se mover, Pulo para escapar, Soco e Chute de perto, Rajada de longe. Segure Defesa para reduzir o dano.":
                        "Combate horizontal · Frente, Trás, Pulo, 3 ataques e Defesa. Use os controles que aprendeu.",
                14,0xffc5d4e8,12);
        footer(battle==2?"LUTAR PELO UNIVERSO":"INICIAR BATALHA",true,
                ()->startFight(battle));
    }
    void render(){h.root.setBackground(h.new StoryBackdrop());h.body.removeAllViews();View old=h.fragmentSurface.findViewWithTag("screen_action");if(old!=null)h.fragmentSurface.removeView(old);h.body.setPadding(d(20),d(33),d(20),d(185)+h.navigationInset);header();String p=PHASES[phase];switch(p){
        case "landing":kicker("Campanha · 3 atos · 3 batalhas");title("SEU UNIVERSO\nESTÁ EM JOGO.");art("Anomalia / realidade 01",false,false);add("Escolha quem vai lutar por seu universo. Descubra por que as realidades estão colidindo e enfrente a ameaça por trás de tudo.",15,0xffc5d4e8,14);chapters();secondary("VISITAR MUNDO",()->h.startActivity(new Intent(h,VisitWorldActivity.class)));if(!name.isEmpty()||!selectedHero.isEmpty())secondary("Recomeçar com outro protagonista",this::restart);footer(name.isEmpty()&&selectedHero.isEmpty()?"Escolher protagonista":"Continuar campanha",true,()->{if(name.isEmpty()&&selectedHero.isEmpty())next();else{phase=wins>=3?24:wins==2?21:wins==1?15:8;save();render();}});break;
        case "hero_choice":chooseProtagonist();break;
        case "name":kicker("01 / identidade");title("COMO O MUNDO VAI\nCHAMAR VOCÊ?");art("Seu herói",true,false);kicker("Nome do herói");EditText input=new EditText(h);input.setSingleLine(true);input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(22)});input.setText(name);input.setTextColor(Color.WHITE);input.setHintTextColor(0xff8da6bf);input.setHint("Digite um nome");input.setPadding(d(15),0,d(15),0);input.setBackground(shape(0xff111e32,0xff111e32,14,0xff607b9d));h.body.addView(input,gap(54,13));add("Esse nome aparecerá nos diálogos e na arena.",15,0xffc5d4e8,12);input.addTextChangedListener(new TextWatcher(){public void beforeTextChanged(CharSequence s,int st,int count,int after){}public void onTextChanged(CharSequence s,int st,int before,int count){name=s.toString();save();View old=h.fragmentSurface.findViewWithTag("screen_action");if(old instanceof Button)((Button)old).setEnabled(!name.trim().isEmpty());}public void afterTextChanged(Editable e){}});footer("Definir Nome",!name.trim().isEmpty(),this::next);break;
        case "appearance":kicker("02 / aparência");title("SEU VISUAL.\nSUA HISTÓRIA.");art(protagonist(),true,false);add("Ajuste a apresentação, o porte, o tom de pele e o cabelo antes de entrar na história.",15,0xffc5d4e8,12);appearanceControls();footer("Definir aparência",true,this::next);break;
        case "color":kicker("03 / assinatura visual");title("UMA COR. UM SÍMBOLO.");art(protagonist(),true,false);kicker("Cor principal");LinearLayout colors=new LinearLayout(h);for(int i=0;i<COLORS.length;i++){final int n=i;TextView swatch=t("",10,Color.WHITE);swatch.setBackground(shape(COLOR_VALUES[i],COLOR_VALUES[i],12,i==color?Color.WHITE:COLOR_VALUES[i]));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,d(43),1);if(i>0)lp.leftMargin=d(6);colors.addView(swatch,lp);swatch.setContentDescription(COLORS[i]);swatch.setOnClickListener(v->{color=n;save();render();});}h.body.addView(colors,gap(-2,13));add(COLORS[color]+" acompanha seu herói até o confronto final.",15,0xffc5d4e8,12);footer("Escolher cor",true,this::next);break;
        case "power":kicker("04 / habilidade");title("QUAL É SEU PODER?");for(int row=0;row<3;row++){LinearLayout line=new LinearLayout(h);for(int col=0;col<2;col++){int i=row*2+col;final int chosen=i;LinearLayout option=new LinearLayout(h);option.setOrientation(1);option.setPadding(d(11),d(10),d(8),d(9));option.setBackground(shape(i==power?0xff22516b:0xff1a2d49,i==power?0xff22516b:0xff1a2d49,14,i==power?0xff81eafa:0xff4c6284));option.addView(t(POWERS[i],14,Color.WHITE));option.addView(t(POWER_DESC[i],11,0xffa8bfd7));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,d(70),1);if(col>0)lp.leftMargin=d(9);line.addView(option,lp);option.setOnClickListener(v->{power=chosen;save();render();});}h.body.addView(line,gap(-2,9));}card(POWERS[power],"Seu poder define a rajada comum, o Especial e uma característica própria de combate. A cor e a aparência escolhidas continuam sendo suas.");footer("Definir poder",true,this::next);break;
        case "personality1":case "personality2":int q=p.equals("personality1")?0:1;kicker(q==0?"05 / personalidade":"06 / personalidade");title(q==0?"O QUE GUIA\nSUAS DECISÕES?":"QUANDO A VERDADE FALHA,\nEM QUEM CONFIA?");add("Sua personalidade influencia algumas falas durante a campanha.",15,0xffc5d4e8,13);art(protagonist(),true,false);options(q==0?new String[]{"Proteger quem está perto","Pensar no futuro de todos"}:new String[]{"Na equipe","No meu próprio julgamento"},q==0?new String[]{"Compromisso humano","Visão estratégica"}:new String[]{"Força coletiva","Autonomia"},personality[q],n->personality(q,n));footer(q==0?"Próxima pergunta":"Continuar",personality[q]>=0,this::next);break;
        case "summary":kicker("Personagem confirmado");title("SEU HERÓI");art(protagonist(),true,false);if(selectedHero.isEmpty())card(protagonist(),"Aparência: "+new String[]{"Masculina","Feminina","Livre"}[style]+"  ·  Cor: "+COLORS[color]+"  ·  Poder: "+POWERS[power]+"  ·  Perfil: "+profile()+"  ·  Porte: "+new String[]{"Esbelto","Atlético","Forte"}[build]+"  ·  Cabelo: "+new String[]{"Curto","Longo","Raspado"}[hair]);else{int heroIndex=java.util.Arrays.asList(h.roster).indexOf(selectedHero);card(protagonist(),"Identidade: "+(heroIndex>=0?h.civilian[heroIndex]:selectedHero)+"  ·  Visual e ataques do modo de batalha.");}add("O universo ainda não conhece você. Isso está prestes a mudar.",15,0xffc5d4e8,13);chapters();footer("INICIAR HISTÓRIA",true,this::next);break;
        case "intro1":kicker("Ato 1 / descoberta");title("O CÉU SE PARTIU.");art("Anomalia / realidade 01",false,false);narration("Nova York, 02:17. Por três segundos, duas cidades ocuparam o mesmo lugar.");add(protagonist()+" vê um trem desaparecer dentro de uma fenda. Quando ele volta, seus passageiros falam de um mundo que nunca existiu aqui.",15,0xffc5d4e8,13);footer("Seguir o sinal",true,this::next);break;
        case "intro2":kicker("Evidência 01 / a fenda");title("UMA CIDADE.\nDUAS REALIDADES.");art("Anomalia / realidade 01",false,false);card("Leitura impossível","O sinal do trem aparece em dois universos ao mesmo tempo. As frequências estão se comprimindo — como se uma realidade precisasse ceder espaço à outra.");add("Um círculo de luz se abre às suas costas.",15,0xffc5d4e8,12);footer("Olhar para trás",true,this::next);break;
        case "dialogue1":kicker("Primeiro contato");title("ALGUÉM SABE MAIS.");dialog(ally("Doutor Estranho"),"Essas fendas são incursões. Dois universos tentam ocupar o mesmo espaço. Só um pode permanecer.",false);dialog(protagonist(),"Então o meu universo pode desaparecer?",true);dialog(ally("Doutor Estranho"),"Sim. E há sobreviventes presos na próxima ruptura. Preciso da sua decisão agora.",false);narration("O portal à frente começa a fechar.");footer("Tomar a decisão",true,this::next);break;
        case "choice1":kicker("Decisão 1 / sem tempo");title("UM PORTAL CAI.\nQUEM VOCÊ SALVA?");add("Habitantes de outra realidade estão presos. O mesmo portal sustenta a âncora que protege seu universo.",15,0xffc5d4e8,12);art("Anomalia / realidade 01",false,false);options(new String[]{"Abrir o portal e tirar as pessoas de lá.","Selar o portal e reforçar a âncora."},new String[]{"Resgatar os habitantes","Proteger seu universo"},choices[0],n->choice(0,n));footer("Confirmar decisão",choices[0]>=0,this::next);break;
        case "consequence1":boolean rescue=choices[0]==0;kicker("Consequência / o portal");title(rescue?"VIDAS SALVAS.\nÂNCORA FRÁGIL.":"ÂNCORA ESTÁVEL.\nVIDAS PERDIDAS.");dialog(ally("Doutor Estranho"),rescue?"Você os trouxe de volta. Confio em você — mas agora a âncora exige defesa extra.":"A âncora está segura. Os que ficaram do outro lado não tiveram a mesma chance.",false);card(rescue?"Aliado conquistado":"Defesa reforçada",rescue?"Estranho lutará ao seu lado à distância. A âncora exigirá mais cuidado no confronto.":"A âncora está protegida para os próximos confrontos. A perda será lembrada pelos heróis.");add("Um Wolverine de outra dimensão atravessa a fenda. Ele não reconhece você — só sua realidade como alvo.",15,0xffc5d4e8,12);footer("Enfrentar Wolverine",true,this::next);break;
        case "after1":chapters();kicker("Ato 2 / conflito");title("WOLVERINE CAIU.\nA GUERRA, NÃO.");art(protagonist(),true,false);narration("As garras dimensionais deixam uma assinatura que revela um mapa: centenas de universos, reduzidos a três.");add(protagonist()+" venceu a primeira batalha. Agora há um padrão: as incursões parecem ser direcionadas.",15,0xffc5d4e8,12);footer("Investigar o mapa",true,this::next);break;
        case "dialogue2":kicker("Dois pontos de vista");title("NEM TODOS\nCONCORDAM.");dialog(ally("Doutor Estranho"),choices[0]==0?"Você salvou aquelas pessoas. Eu confio no seu julgamento.":"A âncora resistiu. Ainda penso nos que ficaram para trás.",false);dialog(ally("Homem de Ferro"),"Alguém construiu uma máquina para escolher quais universos sobrevivem. Posso derrubar o circuito.",false);dialog(ally("Doutor Estranho"),"Se o circuito cair antes de entendermos o feitiço, a colisão acelera. Preciso ver o núcleo.",false);dialog(protagonist(),"Só temos tempo para uma abordagem.",true);footer("Escolher um plano",true,this::next);break;
        case "choice2":kicker("Decisão 2 / plano de ataque");title("TECNOLOGIA OU MAGIA?");add("O núcleo da incursão pode ser interrompido por uma armadura ou aberto por um feitiço. Cada opção muda o confronto que virá.",15,0xffc5d4e8,12);art("Anomalia / realidade 01",false,false);options(new String[]{"Seguir Tony e sobrecarregar o circuito.","Confiar em Estranho e ler o núcleo."},new String[]{"Plano tecnológico","Plano místico"},choices[1],n->choice(1,n));footer("Confirmar plano",choices[1]>=0,this::next);break;
        case "consequence2":boolean tech=choices[1]==0;kicker("Consequência / o núcleo");title(tech?"CIRCUITO PARTIDO.":"A VERDADE NO NÚCLEO.");dialog(ally(tech?"Homem de Ferro":"Doutor Estranho"),tech?"Sobrecarga pronta. A armadura vai absorver parte do dano no confronto final.":"O feitiço revelou uma mente por trás das incursões. Seu escudo ficará vulnerável.",false);card(tech?"Proteção extra":"Vantagem ofensiva",tech?"Na batalha final, os golpes do adversário final causarão menos dano.":"Na batalha final, você conhece a falha do escudo do adversário final.");add("Um Homem-Aranha de outra dimensão bloqueia o caminho para a origem do sinal.",15,0xffc5d4e8,12);footer("Romper o bloqueio",true,this::next);break;
        case "after2":chapters();kicker("Ato 3 / destino");title("O CAMINHO\nESTÁ ABERTO.");art("Latvéria / núcleo de colisão",false,true);narration("O Homem-Aranha de outra dimensão cai. Atrás dele, um observatório rodeado por fragmentos de universos destruídos.");add("A energia da máquina tem uma assinatura. Ela vem da Latvéria.",15,0xffc5d4e8,12);footer("Entrar no observatório",true,this::next);break;
        case "reveal":kicker("AMEAÇA REVELADA");title("DR. DOOM ESCOLHE\nQUEM EXISTE.");art("Dr. Doom",true,true);dialog("Dr. Doom","Realidades fracas desaparecem. A sua será a próxima.",false);add("Doom provocou as incursões para alimentar uma única realidade sob seu controle. Seu universo é o último obstáculo.",15,0xffc5d4e8,12);footer("Preparar o confronto",true,this::next);break;
        case "prep":kicker("Última conversa");title("NÃO HÁ\nOUTRO CAMINHO.");dialog(ally(choices[1]==0?"Homem de Ferro":"Doutor Estranho"),choices[1]==0?"Sua armadura aguenta o primeiro impacto. O resto depende de você.":"O escudo de Doom tem uma falha. Encontre a abertura e ataque.",false);dialog(protagonist(),personality[1]==0?"Não vou lutar sozinho. Vamos salvar nosso universo.":"Eu termino isso. Pelo nosso universo.",true);card("Seu universo está em jogo","Derrote Doom para desligar a máquina. Se cair, tente novamente: a campanha continua até a vitória.");footer("Enfrentar Dr. Doom",true,this::next);break;
        case "ending":kicker("CAMPANHA CONCLUÍDA");title("SEU UNIVERSO\nSOBREVIVEU.");art("Anomalia / realidade 01",false,false);narration("A máquina de Doom se apaga. As realidades se separam. O céu volta a ser um só.");dialog(ally(choices[1]==0?"Homem de Ferro":"Doutor Estranho"),choices[1]==0?"Você derrubou a máquina. Esse universo deve a você um novo amanhã.":"Você enfrentou Doom e devolveu nosso futuro. Seu nome será lembrado.",false);card("O legado de "+protagonist(),(choices[0]==0?"Os habitantes resgatados voltaram para casa e lembrarão quem abriu o portal.":"A âncora resistiu e protegeu seu universo, enquanto a perda do outro lado permanece na memória dos heróis.")+" "+(choices[1]==0?"Tony mantém a armadura como defesa contra novas fendas.":"Estranho guarda o segredo do núcleo para impedir outra incursão."));secondary("Voltar ao início",()->{phase=0;save();render();});footer("Jogar novamente",true,this::restart);break;
        default:if(p.startsWith("pre"))preBattle(Integer.parseInt(p.substring(3))-1);break;
    }}
    private String profile(){return personality[0]==0&&personality[1]==0?"Protetor solidário":personality[0]==1&&personality[1]==1?"Guardião independente":personality[0]==0?"Líder compassivo":"Estrategista leal";}
}







