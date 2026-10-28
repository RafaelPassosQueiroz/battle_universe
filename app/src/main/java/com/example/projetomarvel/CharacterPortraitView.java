//retartos menores personagens
package com.example.projetomarvel;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

/** Cropped bust from the same vector character rig used in combat. */
final class CharacterPortraitView extends View {
    private final String hero;
    private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
    CharacterPortraitView(Context context,String hero){super(context);this.hero=hero;}
    @Override protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        float side=Math.min(getWidth(),getHeight());
        float cx=getWidth()/2f,cy=getHeight()/2f;
        paint.setColor(0xff0b0b14);
        canvas.drawCircle(cx,cy,side*.49f,paint);
        Path clip=new Path();
        clip.addCircle(cx,cy,side*.46f,Path.Direction.CW);
        canvas.save();
        canvas.clipPath(clip);
        FighterArtView.draw(canvas,cx,cy+side*.95f,side/114f,hero,false);
        canvas.restore();
        paint.setColor(0x88ffffff);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(Math.max(1,side*.025f));
        canvas.drawCircle(cx,cy,side*.48f,paint);
        paint.setStyle(Paint.Style.FILL);
    }
}
