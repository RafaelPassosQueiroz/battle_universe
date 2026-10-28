//aparência dos inimigos genéricos na arena
package com.example.projetomarvel;
import android.graphics.Canvas;
import android.graphics.Paint;
/** Generic narrative enemies, without borrowing a Marvel character's skin. */
final class WorldEnemyArt {
 static void draw(Canvas c,float x,float feet,boolean mirror,String kind,int pose,float phase){
  Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);c.save();c.translate(x,feet);c.scale(mirror?-.72f:.72f,.72f);
  float step=pose==FighterArtView.POSE_WALK?(float)Math.sin(phase*11)*10:0;
  if(pose==FighterArtView.POSE_KO)c.rotate(-75,0,-38);
  if(pose==FighterArtView.POSE_HIT)c.rotate(-7,0,-70);
  float strike=(float)Math.sin(Math.PI*Math.min(1,Math.max(0,phase)));
  boolean robot="doombot".equals(kind);int body=robot?0xff568875:0xff45665b,trim=robot?0xff98b7a7:0xff273e3e;
  limb(c,p,-16,-64,-22+step,-8,body,21);limb(c,p,16,-64,22-step,-8,body,21);
  box(c,p,-36+step,-12,27,12,0xff182b2c);box(c,p,9-step,-12,27,12,0xff182b2c);
  box(c,p,-30,-135,60,72,body);box(c,p,-25,-76,50,10,0xff233c39);
  float handX=pose==FighterArtView.POSE_PUNCH?50+strike*53:pose==FighterArtView.POSE_BLAST?54+strike*34:pose==FighterArtView.POSE_GUARD?21:43;
  float handY=pose==FighterArtView.POSE_GUARD?-145:-94;
  limb(c,p,-30,-121,-40,-88,body,19);limb(c,p,29,-121,handX,handY,body,19);
  box(c,p,handX-9,handY-9,18,18,trim);box(c,p,-48,-95,17,17,trim);
  box(c,p,-25,-185,50,47,trim);box(c,p,-21,-179,42,34,robot?0xffa6bbb3:0xffa47d5b);
  box(c,p,-25,-187,50,12,robot?body:0xff253c35);box(c,p,2,-169,18,6,robot?0xff83f2bc:0xff213530);
  if(robot){box(c,p,-14,-162,31,4,0xff416159);box(c,p,-10,-151,23,4,0xff426158);box(c,p,-13,-117,26,21,0xff2c4940);box(c,p,-6,-112,12,9,0xff8cebba);}
  else{box(c,p,-15,-125,30,5,0xff78978a);box(c,p,5,-110,13,17,trim);}
  c.restore();
 }
 static void box(Canvas c,Paint p,float x,float y,float w,float h,int color){p.setColor(color);p.setStyle(Paint.Style.FILL);c.drawRect(x,y,x+w,y+h,p);}
 static void limb(Canvas c,Paint p,float x,float y,float xx,float yy,int color,float w){p.setColor(color);p.setStrokeWidth(w);p.setStrokeCap(Paint.Cap.ROUND);c.drawLine(x,y,xx,yy,p);}
}
