//pré luta
package com.example.projetomarvel;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

/** Compact fighter guide and the existing battle difficulty selection. */
final class BattleHubReference {
    private final MainActivity h;
    BattleHubReference(MainActivity host){h=host;}
    private int d(float n){return h.dp(n);}
    void render(){LinearLayout body=h.body;body.setPadding(d(18),d(34),d(18),d(120)+h.navigationInset);TextView title=h.text("MARVEL BATTLES",47,Color.WHITE);title.setTypeface(Typography.bebas(h));title.setShadowLayer(d(12),0,0,0x99ff8c1e);body.addView(title);TextView sub=h.text("Escolha seu herói, enfrente o BOT e\nvire a tela para lutar.",15.5f,0xffdccbb9);body.addView(sub);
        addAbilityCards(body);
        TextView difficultyTitle=h.text("DIFICULDADE",16,Color.WHITE);
        difficultyTitle.setTypeface(Typography.bebas(h));
        body.addView(difficultyTitle,new LinearLayout.LayoutParams(-1,d(27)));
        LinearLayout difficultyRow=new LinearLayout(h);
        LinearLayout.LayoutParams difficultyLp=new LinearLayout.LayoutParams(-1,d(46));
        difficultyLp.bottomMargin=d(14);
        body.addView(difficultyRow,difficultyLp);
        String[] levels={"FÁCIL","MÉDIO","DIFÍCIL"};
        for(int i=0;i<levels.length;i++){
            final int level=i;
            Button option=new Button(h);
            option.setText(levels[i]);
            option.setAllCaps(false);
            option.setTextSize(13);
            option.setTextColor(i==h.battleDifficulty?0xff26120a:Color.WHITE);
            option.setTypeface(Typography.barlowBold(h));
            option.setBackground(h.rounded(i==h.battleDifficulty?
                    new int[]{0xffffb020,0xffff8120}:new int[]{0xff30252d,0xff30252d},
                    12,i==h.battleDifficulty?0xffffc758:0x88ffffff,1));
            LinearLayout.LayoutParams optionLp=new LinearLayout.LayoutParams(0,-1,1);
            if(i>0)optionLp.leftMargin=d(8);
            difficultyRow.addView(option,optionLp);
            option.setOnClickListener(v->{h.battleDifficulty=level;renderAgain();});
        }
        Button start=h.styledAction("Começar batalha",true);body.addView(start,new LinearLayout.LayoutParams(-1,d(52)));start.setOnClickListener(v->{h.choosingBot=false;h.playerChosen=false;h.botChosen=false;h.screen="Seleção";h.renderTab();h.makeNav();h.scrollBodyTop();});
    }
    private void addAbilityCards(LinearLayout body){
        String[][] abilities={
                {"Teia · lentidão por 1,5 s", "Estilingue · chute de 15", "Sentido Aranha · projéteis lentos"},
                {"Repulsor · energia de 8", "Unibeam · feixe de 35", "Propulsão · plana no ar"},
                {"Corte Cruzado · garras de 6", "Berserker · 2 investidas de 10", "Regeneração · recupera 5 HP"},
                {"Escudo · 4 na ida e na volta", "Invulnerável por 5 s", "Defesa Inquebrável · escudo"},
                {"Mjölnir · 8 + raio de 2", "Trovão · 15 em área", "Dash Aéreo · 1 por salto"},
                {"Thunderclap · 6 e anula tiros", "Salto Esmagador · 15", "Fúria · mais dano com 30 HP"},
                {"Investida Felina · 2", "Explosão · cresce ao defender", "Vibranium · acumula defesas"},
                {"Rajada mística", "Portal · troca de lado", "Viagem no Tempo · revive 1 vez"},
                {"Dois Tiros · 3 cada", "Abate Silencioso · atordoa", "Agilidade · salto e recuperação"},
                {"Esfera do Caos · energia", "Distorção · bloqueia pulo/defesa", "Auxiliar · surge na vida vermelha"},
                {"Feixe de Luz · 7", "Supernova · 15", "Combate Aprimorado · golpes rápidos"},
                {"Raios Verdes · 5 × 3", "Sentença · laser do céu", "Escudo Automático · a cada 7 s"},
                {"Projétil de energia · 9", "Estalo · vitória após preparo", "Unstoppable · não é empurrado"}
        };
        TextView heading=h.text("CONHEÇA OS LUTADORES",20,Color.WHITE);
        heading.setTypeface(Typography.bebas(h));
        body.addView(heading);
        TextView hint=h.text("Deslize para ver poderes, especiais e características.",12,0xffdccbb9);
        LinearLayout.LayoutParams hintLp=new LinearLayout.LayoutParams(-1,-2);
        hintLp.bottomMargin=d(10);
        body.addView(hint,hintLp);
        HorizontalScrollView scroll=new HorizontalScrollView(h);
        scroll.setHorizontalScrollBarEnabled(false);
        LinearLayout row=new LinearLayout(h);
        scroll.addView(row);
        for(int i=0;i<h.roster.length;i++){
            LinearLayout card=new LinearLayout(h);
            card.setOrientation(1);
            card.setPadding(d(12),d(10),d(12),d(10));
            card.setBackground(h.rounded(new int[]{0xff30232b,0xff211a24},14,0x77ff9a43,1));
            LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(d(260),d(196));
            cp.rightMargin=d(9);
            row.addView(card,cp);
            LinearLayout identity=new LinearLayout(h);
            identity.setGravity(Gravity.CENTER_VERTICAL);
            FighterArtView portrait=new FighterArtView(h,h.roster[i],false);
            identity.addView(portrait,new LinearLayout.LayoutParams(d(42),d(58)));
            TextView name=h.text(h.roster[i],18,Color.WHITE);
            name.setTypeface(Typography.bebas(h));
            name.setPadding(d(8),0,0,0);
            identity.addView(name,new LinearLayout.LayoutParams(0,-2,1));
            card.addView(identity);
            String[] labels={"PODER NORMAL", "ESPECIAL", "CARACTERÍSTICA"};
            for(int j=0;j<3;j++){
                TextView item=h.text(labels[j]+"  ·  "+abilities[i][j],12.5f,j==0?0xffffc172:0xffe9e4e5);
                item.setMaxLines(2);
                LinearLayout.LayoutParams ip=new LinearLayout.LayoutParams(-1,d(36));
                card.addView(item,ip);
            }
        }
        LinearLayout.LayoutParams sp=new LinearLayout.LayoutParams(-1,d(198));
        sp.bottomMargin=d(14);
        body.addView(scroll,sp);
    }
    private void renderAgain(){
        android.widget.ScrollView scroll=h.body.getParent() instanceof android.widget.ScrollView
                ?(android.widget.ScrollView)h.body.getParent():null;
        int y=scroll==null?0:scroll.getScrollY();
        h.body.removeAllViews();render();
        if(scroll!=null)scroll.post(()->scroll.scrollTo(0,y));
    }
}
