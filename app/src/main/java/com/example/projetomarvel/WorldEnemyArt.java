package com.example.projetomarvel;
import android.graphics.Canvas;
/** Battle-only narrative enemies use the same articulated native rig. World pixels are separate. */
final class WorldEnemyArt {
 static void draw(Canvas c,float x,float feet,boolean mirror,String kind,int pose,float phase){FighterVisual f=new FighterVisual("Inimigo");f.generic(kind);f.dir=mirror?-1:1;f.preview(pose,phase,phase);NativeFighterArt.draw(c,f,x,feet,.72,phase);}
}
