//Deenho splash
package com.example.projetomarvel;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;

/** A two-second shield/repulsor clash, recreated with the app's own animated character rig. */
public final class SplashCinematicView extends View {
    private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
    private long start;
    public SplashCinematicView(Context context){super(context);}
    public SplashCinematicView(Context context,AttributeSet attrs){super(context,attrs);}
    @Override protected void onAttachedToWindow(){super.onAttachedToWindow();start=SystemClock.uptimeMillis();}
    @Override protected void onDraw(Canvas c){
        super.onDraw(c);float w=getWidth(),h=getHeight();if(w==0||h==0)return;
        float t=Math.min(1.85f,(SystemClock.uptimeMillis()-start)/1000f);
        paint.setShader(new LinearGradient(0,0,w,h,new int[]{0xff142e46,0xff1d394c,0xff122638},
                null,Shader.TileMode.CLAMP));c.drawRect(0,0,w,h,paint);
        glow(c,w*.16f,h*.48f,w*.65f,0x664b9dba,w,h);
        glow(c,w*.84f,h*.48f,w*.65f,0x667e7149,w,h);
        float scale=Math.min(w/225f,h*.58f/200f),feet=h*.79f;
        float left=ease(segment(t,0,.52f)),right=ease(segment(t,.12f,.65f));
        hero(c,w*(.20f-.06f*(1-left)),feet,scale,"Capitão América",false,left);
        hero(c,w*(.815f+.06f*(1-right)),feet,scale,"Homem de Ferro",true,right);
        float clash=ease(segment(t,.72f,1.0f));
        if(clash>0){
            float x=w*.52f,y=feet-scale*116;
            glow(c,x,y,w*.43f,(int)(clash*100)<<24|0x00ffb747,w,h);
            paint.setShader(null);paint.setColor(0xaab8eeff);paint.setStrokeWidth(7*clash);
            float hand=w*.815f-scale*65;
            c.drawLine(Math.max(x+3,hand),y,x,y,paint);
            paint.setColor(0xffffd072);paint.setStrokeWidth(2);
            float spread=12+30*ease(segment(t,.85f,1.30f));
            for(int i=0;i<15;i++){
                double a=i*2.399;float dx=(float)Math.cos(a),dy=(float)Math.sin(a);
                float length=spread*(.65f+(i%4)*.22f);
                paint.setAlpha((int)(clash*(150+i%3*30)));
                c.drawLine(x+dx*11,y+dy*11,x+dx*length,y+dy*length,paint);
            }
            paint.setAlpha(255);paint.setColor(0xffffe4a5);c.drawCircle(x,y,10*clash,paint);
            paint.setColor(0xfff6fcff);c.drawCircle(x,y,5*clash,paint);
        }
        paint.setShader(null);paint.setAlpha(255);
        if(t<1.85f)postInvalidateOnAnimation();
    }
    private void glow(Canvas c,float x,float y,float radius,int color,float w,float h){
        paint.setShader(new RadialGradient(x,y,radius,color,color&0x00ffffff,Shader.TileMode.CLAMP));
        c.drawRect(0,0,w,h,paint);paint.setShader(null);
    }
    private void hero(Canvas c,float x,float feet,float scale,String hero,boolean mirror,float alpha){
        int layer=c.saveLayerAlpha(0,0,getWidth(),getHeight(),(int)(alpha*255));
        FighterArtView.drawAnimated(c,x,feet,scale,hero,mirror,-1,FighterArtView.POSE_CLASH,.5f);
        c.restoreToCount(layer);
    }
    private static float segment(float t,float from,float to){return Math.max(0,Math.min(1,(t-from)/(to-from)));}
    private static float ease(float v){return v*v*(3-2*v);}
}
