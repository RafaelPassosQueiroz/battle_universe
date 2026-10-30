package com.example.projetomarvel;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.widget.*;
import java.util.Locale;

/** Portrait-independent native pause/result cards using the approved web palette. */
final class FightPanels {
    private final Activity host;final FrameLayout overlay;final LinearLayout panel;
    private int dp(float n){return Math.round(n*host.getResources().getDisplayMetrics().density);}
    private FightPanels(Activity h){
        host=h;overlay=new FrameLayout(h);overlay.setClickable(true);overlay.setBackgroundColor(0xcd060c18);overlay.setElevation(dp(40));
        ScrollView scroll=new ScrollView(h);scroll.setFillViewport(false);scroll.setClipToPadding(false);
        int width=Math.min(dp(520),(int)(h.getResources().getDisplayMetrics().widthPixels*.70));
        FrameLayout.LayoutParams lp=new FrameLayout.LayoutParams(width,-2,Gravity.CENTER);overlay.addView(scroll,lp);
        overlay.addOnLayoutChangeListener((v,l,t,r,b,ol,ot,or,ob)->{int maximum=Math.max(dp(150),b-t-dp(24));if(scroll.getMeasuredHeight()>maximum){FrameLayout.LayoutParams sp=(FrameLayout.LayoutParams)scroll.getLayoutParams();sp.height=maximum;scroll.setLayoutParams(sp);}});
        panel=new LinearLayout(h);panel.setOrientation(1);panel.setGravity(Gravity.CENTER_HORIZONTAL);panel.setPadding(dp(23),dp(17),dp(23),dp(17));
        GradientDrawable bg=new GradientDrawable(GradientDrawable.Orientation.TL_BR,new int[]{0xff2f4b5b,0xff142b40});bg.setCornerRadius(dp(10));bg.setStroke(dp(1),0x66e3c784);panel.setBackground(bg);scroll.addView(panel);
    }
    private TextView text(String value,int size,int color,boolean heading){TextView t=new TextView(host);t.setText(value);t.setTextSize(size);t.setTextColor(color);t.setTypeface(heading?Typography.bebas(host):Typography.barlow(host));t.setGravity(Gravity.CENTER);t.setIncludeFontPadding(false);return t;}
    private void title(String kicker,String title){TextView k=text(kicker,8,0xffedcf8c,false);panel.addView(k);TextView t=text(title,52,0xfff3cb89,true);LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.setMargins(0,dp(10),0,dp(9));panel.addView(t,lp);}
    private Button button(String value,boolean primary,Runnable click){Button b=new Button(host);b.setText(value);b.setAllCaps(false);b.setTypeface(Typography.bebas(host));b.setTextSize(17);b.setTextColor(primary?0xff192a37:0xffe8edf0);b.setPadding(dp(10),0,dp(10),0);b.setMinHeight(0);b.setMinWidth(0);GradientDrawable bg=new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,primary?new int[]{0xffe8bf72,0xfff5da99}:new int[]{0x14ffffff,0x0cffffff});bg.setCornerRadius(dp(5));bg.setStroke(dp(1),primary?0xffffe4a0:0x44ffffff);b.setBackground(bg);b.setOnClickListener(v->click.run());return b;}
    private void pair(String a,boolean goldA,Runnable ca,String b,Runnable cb){LinearLayout row=new LinearLayout(host);LinearLayout.LayoutParams rp=new LinearLayout.LayoutParams(-1,dp(43));rp.topMargin=dp(10);panel.addView(row,rp);LinearLayout.LayoutParams first=new LinearLayout.LayoutParams(0,-1,1);first.rightMargin=dp(8);row.addView(button(a,goldA,ca),first);row.addView(button(b,false,cb),new LinearLayout.LayoutParams(0,-1,1));}
    private void link(String label,Runnable click){Button b=button(label,false,click);b.setTextSize(12);b.setTypeface(Typography.barlow(host));b.setTextColor(0xffb5c9d2);b.setBackgroundColor(Color.TRANSPARENT);LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,dp(34));lp.topMargin=dp(4);panel.addView(b,lp);}
    static FrameLayout pause(Activity h,Runnable resume,Runnable restart,Runnable selection,Runnable exit){FightPanels p=new FightPanels(h);p.title("RESPIRA. REPOSICIONA.","PAUSA");p.pair("CONTINUAR  →",true,resume,"RECOMEÇAR LUTA",restart);if(selection!=null)p.link("Escolher personagens",selection);p.link("Voltar",exit);TextView hint=p.text("Reposicione, defenda e use seu poder quando houver uma abertura.",10,0xffa9c0cd,false);p.panel.addView(hint);return p.overlay;}
    static FrameLayout result(Activity h,boolean win,String winner,float seconds,float hp,int hits,int blocks,Runnable retry,Runnable selection,String back,Runnable exit){
        FightPanels p=new FightPanels(h);p.title(win?"O CONFRONTO É SEU":"CADA CONFRONTO ENSINA",win?"VITÓRIA":"DERROTA");
        p.panel.addView(p.text(winner+" venceu o confronto em "+(int)seconds+" segundos.",11,0xffb5c9d2,false));
        LinearLayout stats=new LinearLayout(h);LinearLayout.LayoutParams sp=new LinearLayout.LayoutParams(-1,-2);sp.setMargins(0,p.dp(10),0,p.dp(2));p.panel.addView(stats,sp);
        String[]values={String.valueOf((int)Math.ceil(hp)),String.valueOf(hits),String.valueOf(blocks)},labels={"HP RESTANTES","ACERTOS","DEFESAS"};
        for(int i=0;i<3;i++){LinearLayout col=new LinearLayout(h);col.setOrientation(1);col.addView(p.text(values[i],27,0xfff4f1df,true));col.addView(p.text(labels[i],8,0xff9fb7c6,false));stats.addView(col,new LinearLayout.LayoutParams(0,-2,1));}
        if(selection!=null){p.pair("REVANCHE  ↻",true,retry,"ESCOLHER PERSONAGENS",selection);p.link(back,exit);}
        else p.pair("REVANCHE  ↻",true,retry,back,exit);
        return p.overlay;
    }
}
