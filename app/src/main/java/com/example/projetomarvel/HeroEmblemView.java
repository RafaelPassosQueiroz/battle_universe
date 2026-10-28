//Emblemas heróis (não utilzado)
package com.example.projetomarvel;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

/** Scalable native emblem art used by the featured cards and fighter picker. */
final class HeroEmblemView extends View {
    private final Paint p=new Paint(3);
    private final int hero;
    private final int[] inks={0xff8fb6ff,0xffffd86b,0xffffe45c,0xffb7caff,0xffc6d6ff,0xff9af0a8,0xffc9a9ff,0xff8fd3ff,0xffffca6d};
    HeroEmblemView(Context context,int hero){super(context);this.hero=hero;setLayerType(View.LAYER_TYPE_SOFTWARE,null);}
    private void ring(Canvas c,float x,float y,float r){p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(2);c.drawCircle(x,y,r,p);}
    private void line(Canvas c,float a,float b,float x,float y){c.drawLine(a,b,x,y,p);}
    @Override protected void onDraw(Canvas canvas){super.onDraw(canvas);int save=canvas.save();float scale=Math.min(getWidth(),getHeight())/100f;canvas.translate((getWidth()-100*scale)/2f,(getHeight()-100*scale)/2f);canvas.scale(scale,scale);p.setColor(inks[hero]);p.setAlpha(220);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(2.5f);p.setStrokeCap(Paint.Cap.ROUND);p.setStrokeJoin(Paint.Join.ROUND);
        if(hero==0){ring(canvas,50,50,47);ring(canvas,50,50,31);ring(canvas,50,50,15);for(int i=0;i<12;i++){double a=i*Math.PI/6;line(canvas,50,50,50+(float)Math.cos(a)*47,50+(float)Math.sin(a)*47);}for(int i=0;i<4;i++){float r=15+i*8;canvas.drawArc(50-r,50-r,50+r,50+r,0,360,false,p);}}
        else if(hero==1){ring(canvas,50,50,47);ring(canvas,50,50,32);Path t=new Path();t.moveTo(50,20);t.lineTo(78,72);t.lineTo(22,72);t.close();canvas.drawPath(t,p);ring(canvas,50,58,10);}
        else if(hero==2){p.setStrokeWidth(5);for(int i=0;i<3;i++)line(canvas,22+i*20,8,43+i*20,92);}
        else if(hero==3){ring(canvas,50,50,46);ring(canvas,50,50,31);ring(canvas,50,50,17);star(canvas,50,50,15,7,5);}
        else if(hero==4){Path bolt=new Path();bolt.moveTo(60,5);bolt.lineTo(30,54);bolt.lineTo(50,54);bolt.lineTo(39,96);bolt.lineTo(74,42);bolt.lineTo(53,42);bolt.close();canvas.drawPath(bolt,p);}
        else if(hero==5){Path h=new Path();h.moveTo(50,6);h.lineTo(88,28);h.lineTo(88,72);h.lineTo(50,94);h.lineTo(12,72);h.lineTo(12,28);h.close();canvas.drawPath(h,p);p.setStyle(Paint.Style.FILL);p.setTextSize(50);p.setTypeface(android.graphics.Typeface.SERIF);canvas.drawText("γ",35,68,p);}
        else if(hero==6){canvas.drawOval(31,51,69,82,p);canvas.drawOval(16,33,29,52,p);canvas.drawOval(34,18,47,38,p);canvas.drawOval(53,18,66,38,p);canvas.drawOval(72,33,85,52,p);}
        else if(hero==7){ring(canvas,50,50,47);p.setStyle(Paint.Style.FILL);p.setTypeface(android.graphics.Typeface.create("sans-serif-condensed",android.graphics.Typeface.BOLD));p.setTextSize(63);canvas.drawText("4",32,72,p);}
        else {ring(canvas,50,50,46);ring(canvas,50,50,34);Path tri=new Path();tri.moveTo(50,16);tri.lineTo(81,72);tri.lineTo(19,72);tri.close();canvas.drawPath(tri,p);ring(canvas,50,58,10);}
        canvas.restoreToCount(save);
    }
    private void star(Canvas c,float x,float y,float out,float in,int points){Path path=new Path();for(int i=0;i<points*2;i++){double angle=-Math.PI/2+i*Math.PI/points;float r=i%2==0?out:in;float px=x+(float)Math.cos(angle)*r,py=y+(float)Math.sin(angle)*r;if(i==0)path.moveTo(px,py);else path.lineTo(px,py);}path.close();c.drawPath(path,p);}
}
