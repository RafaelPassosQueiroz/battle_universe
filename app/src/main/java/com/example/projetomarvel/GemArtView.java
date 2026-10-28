//Recebendo as jóias
package com.example.projetomarvel;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

/** Three-facet Infinity Gem drawn natively for the Quiz tray and reveal. */
final class GemArtView extends View {
    private final int color;
    private final Paint p=new Paint(3);
    GemArtView(Context c,int color){super(c);this.color=color;setLayerType(View.LAYER_TYPE_SOFTWARE,null);}
    private Path poly(float... xy){Path v=new Path();v.moveTo(xy[0],xy[1]);for(int i=2;i<xy.length;i+=2)v.lineTo(xy[i],xy[i+1]);v.close();return v;}
    @Override protected void onDraw(Canvas c){int save=c.save();float side=Math.min(getWidth(),getHeight())*.82f;c.translate((getWidth()-side)/2f,(getHeight()-side)/2f);c.scale(side/100f,side/100f);p.setColor(color);p.setShadowLayer(13,0,0,color);c.drawPath(poly(50,1,94,25,94,76,50,99,6,76,6,25),p);p.clearShadowLayer();p.setColor(blend(color,Color.WHITE,.47f));c.drawPath(poly(50,1,94,25,50,50,6,25),p);p.setColor(color);c.drawPath(poly(6,25,50,50,50,99,6,76),p);p.setColor(blend(color,Color.BLACK,.26f));c.drawPath(poly(94,25,94,76,50,99,50,50),p);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(3);p.setColor(0xffdfeaff);c.drawPath(poly(50,1,94,25,94,76,50,99,6,76,6,25),p);p.setStyle(Paint.Style.FILL);c.restoreToCount(save);}
    private int blend(int a,int b,float amount){return Color.rgb((int)(Color.red(a)*(1-amount)+Color.red(b)*amount),(int)(Color.green(a)*(1-amount)+Color.green(b)*amount),(int)(Color.blue(a)*(1-amount)+Color.blue(b)*amount));}
}
