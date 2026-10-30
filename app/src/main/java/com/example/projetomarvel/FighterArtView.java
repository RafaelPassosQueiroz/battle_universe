package com.example.projetomarvel;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.SystemClock;
import android.view.View;
/** Shared native articulated V3 rig for previews, Splash, Story and battle. */
public final class FighterArtView extends View {
 public static final int POSE_IDLE=0,POSE_WALK=1,POSE_JUMP=2,POSE_PUNCH=3,POSE_KICK=4,POSE_BLAST=5,POSE_HIT=6,POSE_GUARD=7,POSE_KO=8,POSE_VICTORY=9,POSE_CLASH=10;
 private static final int SPIDER=0,IRON=1,WOLVERINE=2,CAPTAIN=3,THOR=4,HULK=5,PANTHER=6,STRANGE=7,WIDOW=8,SCARLET=9,MARVEL=10,DOOM=11,THANOS=12,CUSTOM=13;
 private final String hero;private final boolean mirror;private final int color,skin,build,hair,style;
 private int displayPose;private String power;private boolean animated=true;
 private final long started=SystemClock.uptimeMillis();
 public FighterArtView(Context c,String hero,boolean mirror){this(c,hero,mirror,-1,-1,1,0,0);}
 public FighterArtView(Context c,String hero,boolean mirror,int color){this(c,hero,mirror,color,-1,1,0,0);}
 public FighterArtView(Context c,String hero,boolean mirror,int color,int skin,int build,int hair){this(c,hero,mirror,color,skin,build,hair,0);}
 public FighterArtView(Context c,String hero,boolean mirror,int color,int skin,int build,int hair,int style){super(c);this.hero=hero==null?"Seu herói":hero;this.mirror=mirror;this.color=color;this.skin=skin;this.build=build;this.hair=hair;this.style=style;setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);}
 public void setPose(int pose){displayPose=pose;invalidate();}public void setPower(String power){this.power=power;invalidate();}public void setAnimated(boolean value){animated=value;invalidate();}
 @Override protected void onDraw(Canvas c){super.onDraw(c);double t=animated?(SystemClock.uptimeMillis()-started)/1000.0:0;FighterVisual f=preview(hero,mirror,color,displayPose,displayPose==POSE_IDLE?t:.5,skin,build,hair,style,power);double margin=Math.max(3,Math.min(getWidth(),getHeight())*.04);double scale=Math.min((getWidth()-margin*2)/(displayPose==POSE_KO?215*f.width():120*f.width()),(getHeight()-margin*2)/(205*f.height()));NativeFighterArt.draw(c,f,getWidth()*.5,getHeight()-margin,Math.max(.01,scale),t);if(animated&&isShown())postInvalidateOnAnimation();}
 static FighterVisual preview(String hero,boolean mirror,int color,int pose,double phase,int skin,int build,int hair,int style,String power){FighterVisual f=new FighterVisual(hero);if(skin>=0)f.configure(color,skin,build,hair,style,power);else if(f.id.equals("doombot"))f.generic("doombot");else if(f.id.equals("custom"))f.configure(color,1,build,hair,style,power);f.dir=mirror?-1:1;f.preview(pose,phase,phase);return f;}
 public static void draw(Canvas c,float x,float y,float s,String hero,boolean mirror){draw(c,x,y,s,hero,mirror,-1);}
 public static void draw(Canvas c,float x,float y,float s,String hero,boolean mirror,int color){drawAnimated(c,x,y,s,hero,mirror,color,POSE_IDLE,0);}
 public static void drawAnimated(Canvas c,float x,float y,float s,String hero,boolean mirror,int color,int pose,float phase){drawAnimated(c,x,y,s,hero,mirror,color,pose,phase,-1,1,0,0);}
 public static void drawAnimated(Canvas c,float x,float y,float s,String hero,boolean mirror,int color,int pose,float phase,int skin,int build,int hair){drawAnimated(c,x,y,s,hero,mirror,color,pose,phase,skin,build,hair,0);}
 public static void drawAnimated(Canvas c,float x,float y,float s,String hero,boolean mirror,int color,int pose,float phase,int skin,int build,int hair,int style){drawPowered(c,x,y,s,hero,mirror,color,pose,phase,skin,build,hair,style,null);}
 public static void drawPowered(Canvas c,float x,float y,float s,String hero,boolean mirror,int color,int pose,float phase,int skin,int build,int hair,int style,String power){FighterVisual f=preview(hero,mirror,color,pose,phase,skin,build,hair,style,power);NativeFighterArt.draw(c,f,x,y,s,phase);}
    private static int type(String hero){
        if(hero==null)return CUSTOM;
        if(hero.contains("Aranha"))return SPIDER;
        if(hero.contains("Ferro"))return IRON;
        if(hero.contains("Wolverine"))return WOLVERINE;
        if(hero.contains("Capitão América"))return CAPTAIN;
        if(hero.equals("Thor"))return THOR;
        if(hero.equals("Hulk"))return HULK;
        if(hero.contains("Pantera"))return PANTHER;
        if(hero.contains("Estranho"))return STRANGE;
        if(hero.contains("Viúva"))return WIDOW;
        if(hero.contains("Escarlate"))return SCARLET;
        if(hero.contains("Marvel"))return MARVEL;
        if(hero.contains("Doom"))return DOOM;
        if(hero.contains("Thanos"))return THANOS;
        return CUSTOM;
    }
    private static int shade(int color,float factor){
        return Color.rgb((int)(Color.red(color)*factor),
                (int)(Color.green(color)*factor),(int)(Color.blue(color)*factor));
    }
    private static int skinColor(int skin){
        return skin==0?0xfff2c39d:skin==2?0xff835038:0xffbc805e;
    }
    public static int primary(String name){
        switch(type(name)){
            case SPIDER:return 0xffd82934;
            case IRON:return 0xffc62b35;
            case WOLVERINE:return 0xffedc623;
            case CAPTAIN:return 0xff2b56aa;
            case THOR:return 0xff3767bb;
            case HULK:return 0xff47ac58;
            case PANTHER:return 0xff211d30;
            case STRANGE:return 0xff3d4e8a;
            case WIDOW:return 0xff212632;
            case SCARLET:return 0xffa32d46;
            case MARVEL:return 0xff243f92;
            case DOOM:return 0xff2d7849;
            case THANOS:return 0xff7754a5;
            default:return 0xff435e9a;
        }
    }
    public static int secondary(String name){
        switch(type(name)){
            case SPIDER:return 0xff2056c5;
            case IRON:return 0xffe3b029;
            case WOLVERINE:return 0xff24356d;
            case CAPTAIN:return 0xffb32a38;
            case THOR:return 0xff9da7b7;
            case HULK:return 0xff74459a;
            case PANTHER:return 0xff685798;
            case STRANGE:return 0xffb52c39;
            case WIDOW:return 0xff494d58;
            case SCARLET:return 0xff4e233b;
            case MARVEL:return 0xffc92b3c;
            case DOOM:return 0xff899f97;
            case THANOS:return 0xffc9a03b;
            default:return 0xff2858c5;
        }
    }
    public static int accent(String name){
        switch(type(name)){
            case IRON:return 0xff9ceeff;
            case HULK:return 0xff78e390;
            case SCARLET:return 0xffff6d88;
            case DOOM:return 0xff9fffaa;
            case THANOS:return 0xffffce55;
            default:return 0xff9cc9ff;
        }
    }
    public static float visualScale(String hero){return .72f;}
}
