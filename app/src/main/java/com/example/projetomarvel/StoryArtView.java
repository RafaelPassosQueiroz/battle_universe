// Cria fundo modo história
package com.example.projetomarvel;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.view.View;

/** Portal illustration translated from the CSS circles, glow and shards. */
final class StoryArtView extends View {
    private final Paint p=new Paint(3);
    private boolean doom;
    StoryArtView(Context c,boolean doom){super(c);this.doom=doom;setLayerType(View.LAYER_TYPE_SOFTWARE,null);}
    @Override protected void onDraw(Canvas c){float w=getWidth(),h=getHeight();p.setShader(new LinearGradient(0,0,w,h,doom?0xff112b23:0xff172a46,doom?0xff21472e:0xff241636,Shader.TileMode.CLAMP));c.drawRect(0,0,w,h,p);p.setShader(null);float cx=w*.5f,cy=h*.43f,r=Math.min(w,h)*.39f;p.setShader(new RadialGradient(cx,cy,r*1.35f,doom?new int[]{0x889bffac,0x2230a85e,0}:new int[]{0x8862dfff,0x333d5ccc,0},null,Shader.TileMode.CLAMP));c.drawCircle(cx,cy,r*1.35f,p);p.setShader(null);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(4);p.setColor(doom?0xff85ff99:0xff74e6ff);p.setShadowLayer(15,0,0,doom?0xff4dc565:0xff58d8ff);c.drawCircle(cx,cy,r,p);p.clearShadowLayer();p.setStrokeWidth(2);p.setColor(0xaee8edff);p.setPathEffect(new android.graphics.DashPathEffect(new float[]{5,5},0));c.drawCircle(cx,cy,r*.82f,p);p.setColor(0xffd18acf);c.drawCircle(cx,cy,r*.66f,p);p.setPathEffect(null);p.setStyle(Paint.Style.FILL);p.setColor(0x4474a5ff);android.graphics.Path shard=new android.graphics.Path();shard.moveTo(w*.09f,h*.22f);shard.lineTo(w*.22f,h*.13f);shard.lineTo(w*.27f,h*.44f);shard.lineTo(w*.12f,h*.53f);shard.close();c.drawPath(shard,p);}
}
