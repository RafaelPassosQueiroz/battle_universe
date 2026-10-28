//a aparência dos poderes comuns
package com.example.projetomarvel;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Shader;

/** Common-power art. The battle simulation owns direction and collision. */
final class AttackEffects {
    private AttackEffects() {}

    static void drawCustom(Canvas canvas,float x,float y,int direction,
                           String power,int color,Paint paint){
        canvas.save();canvas.translate(x,y);canvas.scale(direction,1);
        paint.setStyle(Paint.Style.FILL);
        int energyColor="Gelo".equals(power)?0xffa4eaff:color;
        paint.setShader(new RadialGradient(0,0,25,
                new int[]{0xeeffffff,energyColor,energyColor&0x00ffffff},null,Shader.TileMode.CLAMP));
        canvas.drawCircle(0,0,25,paint);paint.setShader(null);
        paint.setColor(0xffffffff);paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);paint.setStrokeWidth(3);
        String move=power==null?"Energia":power;
        if(move.equals("Ninja")){
            paint.setColor(0xffdce7ee);paint.setStyle(Paint.Style.FILL);
            Path shuriken=new Path();
            for(int i=0;i<8;i++){double a=i*Math.PI/4;float r=i%2==0?19:6;
                float sx=(float)Math.cos(a)*r,sy=(float)Math.sin(a)*r;
                if(i==0)shuriken.moveTo(sx,sy);else shuriken.lineTo(sx,sy);}
            shuriken.close();canvas.drawPath(shuriken,paint);
            paint.setColor(0xff40515e);canvas.drawCircle(0,0,3,paint);
        }else if(move.equals("Gelo")){
            paint.setColor(0xffc3f0ff);
            for(int i=0;i<3;i++){double a=i*Math.PI/3;
                canvas.drawLine(-(float)Math.cos(a)*18,-(float)Math.sin(a)*18,
                        (float)Math.cos(a)*18,(float)Math.sin(a)*18,paint);}
        }else if(move.equals("Superforça")||move.equals("Super Força")){
            for(int radius=11;radius<=29;radius+=9)
                canvas.drawArc(-radius,-radius,radius,radius,-60,120,false,paint);
        }else if(move.equals("Velocidade")){
            for(int i=-1;i<=1;i++)canvas.drawLine(-28,i*10,-3,i*5,paint);
            canvas.drawArc(-12,-12,14,12,-65,135,false,paint);
        }else if(move.equals("Telecinese")){
            canvas.drawCircle(0,0,17,paint);
            canvas.drawArc(-26,-26,26,26,25,235,false,paint);
        }else if(move.equals("Elemental")){
            for(int i=-1;i<=1;i++){
                Path petal=new Path();petal.moveTo(-15,i*8);
                petal.quadTo(0,i*12-15,23,i*8);canvas.drawPath(petal,paint);
            }
        }else if(move.equals("Tecnologia")){
            Path hex=new Path();
            for(int i=0;i<6;i++){
                double angle=i*Math.PI/3;float px=(float)Math.cos(angle)*18;
                float py=(float)Math.sin(angle)*18;
                if(i==0)hex.moveTo(px,py);else hex.lineTo(px,py);
            }
            hex.close();canvas.drawPath(hex,paint);canvas.drawCircle(0,0,6,paint);
        }else{
            canvas.drawCircle(0,0,13,paint);
            for(int i=0;i<6;i++){
                double angle=i*Math.PI/3;
                float dx=(float)Math.cos(angle),dy=(float)Math.sin(angle);
                canvas.drawLine(dx*17,dy*17,dx*28,dy*28,paint);
            }
        }
        paint.setStyle(Paint.Style.FILL);canvas.restore();
    }

    static int color(String hero) {
        if (hero.contains("Ferro") || hero.contains("Viúva") || hero.contains("Thanos"))
            return 0xff39bfff;
        if (hero.contains("Hulk") || hero.contains("Doom")) return 0xff67e887;
        if (hero.contains("Escarlate")) return 0xffff435f;
        if (hero.contains("Pantera")) return 0xffae84ff;
        if (hero.contains("Aranha")) return 0xffe8f5ff;
        if (hero.contains("Wolverine")) return 0xffffd448;
        if (hero.equals("Thor")) return 0xffb7dcff;
        return 0xffffd65a;
    }

    static void draw(Canvas canvas,float x,float y,int direction,String hero,Paint paint) {
        canvas.save();
        canvas.translate(x,y);
        canvas.scale(direction,1);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        if(hero.contains("Viúva")){
            paint.setStyle(Paint.Style.FILL);paint.setColor(0xfff2e7c4);
            canvas.drawRoundRect(-12,-3,12,3,3,3,paint);
            paint.setColor(0x885ea9dd);canvas.drawRect(-27,-2,-10,2,paint);
        }else if(hero.contains("Aranha")) {
            paint.setColor(0xfff2faff);
            paint.setStrokeWidth(2.5f);
            canvas.drawCircle(0,0,14,paint);
            for(int line=0;line<8;line++){
                double angle=line*Math.PI/4;
                float dx=(float)Math.cos(angle),dy=(float)Math.sin(angle);
                canvas.drawLine(dx*5,dy*5,dx*23,dy*23,paint);
                canvas.drawArc(-19,-19,19,19,line*45+13,24,false,paint);
            }
        } else if(hero.contains("Wolverine")||hero.contains("Pantera")) {
            paint.setColor(color(hero));
            paint.setStrokeWidth(4);
            for(int line=-1;line<=1;line++){
                Path claw=new Path();
                claw.moveTo(-17,line*8+5);
                claw.quadTo(2,line*8-9,23,line*8-4);
                canvas.drawPath(claw,paint);
            }
        } else if(hero.contains("Capitão América")) {
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(0xffdd2a3c);canvas.drawCircle(0,0,22,paint);
            paint.setColor(0xffeaf5ff);canvas.drawCircle(0,0,16,paint);
            paint.setColor(0xff2254a8);canvas.drawCircle(0,0,10,paint);
            paint.setColor(0xffffffff);
            Path star=new Path();
            for(int i=0;i<10;i++){
                double angle=-Math.PI/2+i*Math.PI/5;
                float radius=i%2==0?8:3.5f;
                float sx=(float)Math.cos(angle)*radius,sy=(float)Math.sin(angle)*radius;
                if(i==0)star.moveTo(sx,sy);else star.lineTo(sx,sy);
            }
            star.close();canvas.drawPath(star,paint);
        } else if(hero.equals("Thor")) {
            paint.setColor(0xffbce7ff);paint.setStrokeWidth(3);
            canvas.drawLine(-18,14,4,-10,paint);
            canvas.drawLine(-13,-16,-2,-6,paint);
            canvas.drawLine(-2,-6,-10,3,paint);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(0xffb9c7d7);canvas.drawRoundRect(-2,-22,23,-5,4,4,paint);
            paint.setColor(0xff465774);canvas.drawRect(2,-20,20,-17,paint);
            paint.setColor(0xff6c4d2e);canvas.drawRect(7,-5,12,19,paint);
        } else if(hero.equals("Hulk")) {
            paint.setColor(0xff8fffaa);paint.setStrokeWidth(4);
            for(int radius=8;radius<=28;radius+=10)
                canvas.drawArc(-radius,-radius,radius,radius,-65,130,false,paint);
        } else if(hero.contains("Estranho")) {
            paint.setColor(0xffffdc63);paint.setStrokeWidth(2.5f);
            canvas.drawCircle(0,0,21,paint);
            canvas.drawCircle(0,0,15,paint);
            canvas.drawLine(-14,-14,14,14,paint);
            canvas.drawLine(-14,14,14,-14,paint);
            canvas.drawRect(-10,-10,10,10,paint);
        } else {
            int tint=color(hero);
            paint.setStyle(Paint.Style.FILL);
            paint.setShader(new RadialGradient(0,0,25,
                    new int[]{0xffffffff,tint,0x00000000},null,Shader.TileMode.CLAMP));
            canvas.drawCircle(0,0,25,paint);
            paint.setShader(null);
            paint.setColor(tint);
            paint.setStrokeWidth(3);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawArc(-19,-19,19,19,160,115,false,paint);
        }
        paint.setStyle(Paint.Style.FILL);
        canvas.restore();
    }
}
