//Reator Splash
package com.example.projetomarvel;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

/** Native cyan HUD/Arc-reactor artwork used by the startup screen. */
public final class ArcReactorView extends View {
    private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
    private ObjectAnimator spin,pulse,pulseY; private AnimatorSet pulseSet;
    public ArcReactorView(Context context,AttributeSet attrs){super(context,attrs);setLayerType(View.LAYER_TYPE_HARDWARE,null);}
    @Override protected void onDraw(Canvas canvas){super.onDraw(canvas);float cx=getWidth()/2f,cy=getHeight()/2f,r=Math.min(cx,cy)*.82f;canvas.save();canvas.translate(cx,cy);paint.setStyle(Paint.Style.STROKE);paint.setStrokeWidth(2f);paint.setColor(0xff19e6ff);paint.setAlpha(185);canvas.drawCircle(0,0,r,paint);paint.setStrokeWidth(1f);paint.setAlpha(95);canvas.drawCircle(0,0,r*.82f,paint);paint.setColor(0xff4fbaff);paint.setAlpha(200);paint.setStrokeWidth(3f);paint.setStrokeCap(Paint.Cap.SQUARE);for(int i=0;i<12;i++){canvas.save();canvas.rotate(i*30f);canvas.drawLine(0,-r*.68f,0,-r*.92f,paint);canvas.restore();}paint.setStyle(Paint.Style.STROKE);paint.setStrokeWidth(2f);paint.setAlpha(225);canvas.drawArc(-r*.58f,-r*.58f,r*.58f,r*.58f,25,115,false,paint);canvas.drawArc(-r*.58f,-r*.58f,r*.58f,r*.58f,205,115,false,paint);paint.setStyle(Paint.Style.FILL);paint.setShader(new RadialGradient(0,0,r*.38f,new int[]{0xffe7ffff,0xff45efff,0xff0876c8,0x000876c8},null,Shader.TileMode.CLAMP));canvas.drawCircle(0,0,r*.38f,paint);paint.setShader(null);paint.setStyle(Paint.Style.STROKE);paint.setStrokeWidth(1.5f);paint.setAlpha(230);canvas.drawCircle(0,0,r*.43f,paint);canvas.restore();}
    public void start(){if(spin!=null)return;spin=ObjectAnimator.ofFloat(this,View.ROTATION,0f,360f);spin.setDuration(7200);spin.setRepeatCount(ObjectAnimator.INFINITE);spin.start();pulse=ObjectAnimator.ofFloat(this,View.SCALE_X,1f,1.07f);pulseY=ObjectAnimator.ofFloat(this,View.SCALE_Y,1f,1.07f);pulse.setDuration(900);pulseY.setDuration(900);pulse.setRepeatCount(ObjectAnimator.INFINITE);pulseY.setRepeatCount(ObjectAnimator.INFINITE);pulse.setRepeatMode(ObjectAnimator.REVERSE);pulseY.setRepeatMode(ObjectAnimator.REVERSE);pulseSet=new AnimatorSet();pulseSet.playTogether(pulse,pulseY);pulseSet.start();}
    @Override protected void onDetachedFromWindow(){if(spin!=null)spin.cancel();if(pulseSet!=null)pulseSet.cancel();spin=null;pulse=null;pulseY=null;pulseSet=null;super.onDetachedFromWindow();}
}
