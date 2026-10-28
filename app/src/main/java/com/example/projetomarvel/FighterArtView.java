//desenho e as poses dos lutadores
package com.example.projetomarvel;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

/** Shared side-facing character art for selection, story, versus and the arena. */
public final class FighterArtView extends View {
    public static final int POSE_IDLE=0,POSE_WALK=1,POSE_JUMP=2,POSE_PUNCH=3,
            POSE_KICK=4,POSE_BLAST=5,POSE_HIT=6,POSE_GUARD=7,POSE_KO=8,
            POSE_VICTORY=9,POSE_CLASH=10;
    private static final int SPIDER=0,IRON=1,WOLVERINE=2,CAPTAIN=3,THOR=4,
            HULK=5,PANTHER=6,STRANGE=7,WIDOW=8,SCARLET=9,MARVEL=10,
            DOOM=11,THANOS=12,CUSTOM=13;
    private final String hero;
    private final boolean mirrored;
    private final int overridePrimary,skin,build,hair,style;
    private int displayPose=POSE_IDLE;
    private String displayPower;

    public FighterArtView(Context context,String hero,boolean mirrored){
        this(context,hero,mirrored,-1,-1,1,0,0);
    }
    public FighterArtView(Context context,String hero,boolean mirrored,int color){
        this(context,hero,mirrored,color,-1,1,0,0);
    }
    public FighterArtView(Context context,String hero,boolean mirrored,int color,
                          int skin,int build,int hair){
        this(context,hero,mirrored,color,skin,build,hair,0);
    }
    public FighterArtView(Context context,String hero,boolean mirrored,int color,
                          int skin,int build,int hair,int style){
        super(context);
        this.hero=hero==null?"Seu herói":hero;
        this.mirrored=mirrored;
        this.overridePrimary=color;
        this.skin=skin;
        this.build=build;
        this.hair=hair;
        this.style=style;
    }

    @Override protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        float margin=Math.max(10f,Math.min(getWidth(),getHeight())*.07f);
        float scale=Math.min((getWidth()-margin*2)/(displayPose==POSE_KO?205f:130f),
                (getHeight()-margin*2)/202f);
        drawPowered(canvas,getWidth()/2f,getHeight()-margin,scale,hero,mirrored,
                overridePrimary,displayPose,.5f,skin,build,hair,style,displayPower);
    }
    public void setPose(int pose){displayPose=pose;invalidate();}
    public void setPower(String power){displayPower=power;invalidate();}
    public static void drawPowered(Canvas canvas,float x,float feet,float scale,String hero,
                                   boolean mirror,int color,int pose,float phase,int skin,
                                   int build,int hair,int style,String power){
        drawAnimated(canvas,x,feet,scale,hero,mirror,color,pose,phase,skin,build,hair,style);
        if(skin<0||power==null)return;
        Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
        canvas.save();canvas.translate(x,feet);canvas.scale(scale*(mirror?-1:1),scale);
        float extension=(float)Math.sin(Math.PI*Math.min(1,Math.max(0,phase)));
        if(pose==POSE_KO)canvas.rotate(-72,0,-45);
        else if(pose==POSE_HIT)canvas.rotate(-7,0,-70);
        else if(pose==POSE_PUNCH){canvas.translate(7*extension,0);canvas.rotate(4*extension,0,-75);}
        else if(pose==POSE_KICK)canvas.rotate(-3*extension,0,-75);
        else if(pose==POSE_WALK)canvas.translate(0,-2*Math.abs((float)Math.sin(phase*11)));
        else if(pose==POSE_IDLE)canvas.translate(0,-1.4f*(float)Math.sin(phase*2.8f));
        float handX=45,handY=-98;
        if(pose==POSE_PUNCH){handX+=extension*69;handY-=extension*19;}
        if(pose==POSE_BLAST){handX+=extension*45;handY-=extension*22;}
        if(pose==POSE_GUARD){handX=18;handY=-151;}
        if(pose==POSE_VICTORY){handX=49;handY=-181;}
        if(pose==POSE_HIT){handX=27;handY=-144;}
        if(power.equals("Ninja")){
            p.setColor(shade(color,.42f));canvas.drawRect(-24,-170,24,-150,p);
            p.setColor(0xfff5e6d9);canvas.drawRect(1,-161,19,-156,p);
            p.setColor(0xffc6d7df);p.setStrokeWidth(4);p.setStrokeCap(Paint.Cap.ROUND);
            canvas.drawLine(handX,handY,handX+42,handY-22,p);
            p.setColor(0xffe0bd67);p.setStrokeWidth(3);canvas.drawLine(handX-2,handY-6,handX+6,handY+5,p);
        }else if(power.equals("Gelo")){
            p.setColor(0xffb8efff);p.setStrokeWidth(3);
            for(int i=0;i<3;i++){double a=i*Math.PI/3;
                canvas.drawLine(-(float)Math.cos(a)*12,-110-(float)Math.sin(a)*12,
                        (float)Math.cos(a)*12,-110+(float)Math.sin(a)*12,p);}
            canvas.drawPath(poly(-29,-132,-14,-144,-8,-129),p);
            canvas.drawPath(poly(29,-132,14,-144,8,-129),p);
        }else if(power.equals("Super Força")){
            p.setColor(shade(color,.62f));canvas.drawRoundRect(-36,-140,-18,-120,5,5,p);
            canvas.drawRoundRect(18,-140,36,-120,5,5,p);
        }else if(power.equals("Velocidade")){
            p.setColor(0xffe0f3fa);p.setStrokeWidth(3);
            canvas.drawPath(poly(-9,-126,7,-113,-3,-110,10,-94,-11,-109,0,-113),p);
            canvas.drawLine(-20,-95,-28,-80,p);canvas.drawLine(20,-95,28,-80,p);
        }else if(power.equals("Telecinese")){
            p.setColor(color);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(3);
            canvas.drawCircle(0,-113,13,p);canvas.drawCircle(-39,-87,12,p);
        }else{
            p.setColor(color);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(3);
            canvas.drawCircle(handX,handY,12,p);
        }
        canvas.restore();
    }
    public static void draw(Canvas canvas,float x,float feet,float scale,String hero,boolean mirror){
        draw(canvas,x,feet,scale,hero,mirror,-1);
    }
    public static void draw(Canvas canvas,float x,float feet,float scale,String hero,
                            boolean mirror,int color){
        drawAnimated(canvas,x,feet,scale,hero,mirror,color,POSE_IDLE,0);
    }
    public static void drawAnimated(Canvas canvas,float x,float feet,float scale,String hero,
                                    boolean mirror,int color,int pose,float phase){
        drawAnimated(canvas,x,feet,scale,hero,mirror,color,pose,phase,-1,1,0);
    }
    public static void drawAnimated(Canvas canvas,float x,float feet,float scale,String hero,
                                    boolean mirror,int override,int pose,float phase,
                                    int skin,int build,int hair){
        drawAnimated(canvas,x,feet,scale,hero,mirror,override,pose,phase,
                skin,build,hair,0);
    }
    public static void drawAnimated(Canvas canvas,float x,float feet,float scale,String hero,
                                    boolean mirror,int override,int pose,float phase,
                                    int skin,int build,int hair,int style){
        int type=skin>=0?CUSTOM:type(hero);
        int primary=override==-1?primary(hero):override;
        int secondary=secondary(hero);
        int accent=accent(hero);
        if(hero!=null && hero.contains("outra dimensão")){
            if(type==SPIDER){primary=0xffa8212b;secondary=0xff142c78;accent=0xffebedf2;}
            if(type==WOLVERINE){primary=0xffd79324;secondary=0xff1c253a;accent=0xffe8e4ce;}
        }
        if(type==CUSTOM)secondary=shade(primary,.48f);
        float width=type==HULK?40:type==THANOS?37:type==WIDOW||type==SCARLET?20
                :type==WOLVERINE?30:type==DOOM?31:type==CUSTOM?
                (build==2?33:build==0?23:28)+(style==1?-3:style==2?1:0):27;
        float head=type==HULK||type==THANOS?27:type==WIDOW||type==SCARLET?19:22;
        float arm=type==HULK||type==THANOS?19:type==WIDOW||type==SCARLET?10:14;
        Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
        canvas.save();
        canvas.translate(x,feet);
        // Mirror the whole rig: the frontal Doom mask stays symmetric in either direction.
        canvas.scale(scale*(mirror?-1:1),scale);
        p.setColor(0x77000000);
        canvas.drawOval(-43,-5,43,5,p);
        if(pose==POSE_KO)canvas.rotate(-72,0,-45);
        else if(pose==POSE_HIT)canvas.rotate(-7,0,-70);
        else if(pose==POSE_PUNCH){
            float reach=(float)Math.sin(Math.PI*Math.min(1,phase));
            canvas.translate(7*reach,0);
            canvas.rotate(4*reach,0,-75);
        }
        else if(pose==POSE_KICK)
            canvas.rotate(-3*(float)Math.sin(Math.PI*Math.min(1,phase)),0,-75);
        else if(pose==POSE_WALK)canvas.translate(0,-2*Math.abs((float)Math.sin(phase*11)));
        else if(pose==POSE_IDLE)canvas.translate(0,-1.4f*(float)Math.sin(phase*2.8f));
        else if(pose==POSE_CLASH)canvas.rotate(5,0,-30);

        drawCape(canvas,p,type,primary);
        float walk=pose==POSE_WALK?(float)Math.sin(phase*11)*17:0;
        float backX=-28+walk,frontX=29-walk,backY=-8,frontY=-8;
        if(pose==POSE_JUMP){backX=-41;frontX=37;backY=-36;frontY=-26;}
        if(pose==POSE_KICK){
            float kick=(float)Math.sin(Math.PI*Math.min(1,phase));
            frontX+=kick*70;frontY-=kick*82;
        }
        p.setStrokeCap(Paint.Cap.ROUND);
        p.setStrokeWidth(type==HULK||type==THANOS?19:14);
        p.setColor(type==HULK?0xff74459a:secondary);
        canvas.drawLine(-width*.42f,-70,backX,backY,p);
        canvas.drawLine(width*.42f,-70,frontX,frontY,p);
        p.setColor(type==IRON||type==DOOM?0xff8395a7:0xff101016);
        canvas.drawOval(backX-13,backY-5,backX+12,backY+6,p);
        canvas.drawOval(frontX-12,frontY-5,frontX+13,frontY+6,p);
        if(type==SPIDER||type==CAPTAIN||type==MARVEL){
            p.setColor(primary);
            canvas.drawRect(backX-10,backY-18,backX+9,backY-5,p);
            canvas.drawRect(frontX-9,frontY-18,frontX+10,frontY-5,p);
        }

        Path torso=new Path();
        torso.moveTo(-width,-132);torso.lineTo(width,-132);
        torso.lineTo(width*.78f,-73);torso.lineTo(-width*.78f,-73);torso.close();
        p.setColor(pose==POSE_HIT?0xfff3f5ff:primary);
        canvas.drawPath(torso,p);
        if(type==HULK){
            p.setColor(0xff48b460);
            canvas.drawOval(-37,-143,37,-113,p);
            canvas.drawRect(-31,-129,31,-88,p);
            p.setColor(0xff75419a);canvas.drawRect(-31,-88,31,-70,p);
        }else if(type==IRON||type==DOOM||type==THANOS){
            p.setColor(type==IRON?0xffe5a928:type==DOOM?0xff8f9d9b:0xffc9a03b);
            canvas.drawRoundRect(-width+5,-136,-width+18,-111,4,4,p);
            canvas.drawRoundRect(width-18,-136,width-5,-111,4,4,p);
            p.setColor(shade(primary,.73f));
            canvas.drawRect(-width+6,-93,width-6,-84,p);
        }
        if(type==WIDOW){
            p.setColor(0xffb02831);
            canvas.drawRect(-width+3,-92,width-3,-85,p);
        }
        if(type==WOLVERINE){
            p.setColor(0xff202d54);
            canvas.drawPath(poly(-width,-130,-12,-104,0,-129),p);
            canvas.drawPath(poly(width,-130,12,-104,0,-129),p);
            p.setColor(0xff15131a);
            canvas.drawPath(poly(-width,-124,-9,-113,-width*.75f,-103),p);
            canvas.drawPath(poly(width,-124,9,-113,width*.75f,-103),p);
            canvas.drawPath(poly(-width,-102,-9,-94,-width*.75f,-85),p);
            canvas.drawPath(poly(width,-102,9,-94,width*.75f,-85),p);
        }
        if(type==SPIDER){
            p.setColor(0xff8d1422);p.setStrokeWidth(1.6f);
            for(int i=-2;i<=2;i++)canvas.drawLine(i*8,-130,i*7,-78,p);
            canvas.drawArc(-24,-123,24,-91,5,170,false,p);
            canvas.drawArc(-22,-112,22,-81,5,170,false,p);
        }
        if(type==CAPTAIN){
            p.setColor(0xfff6f4f0);
            for(int i=-1;i<=1;i++)canvas.drawRect(i*12-3,-94,i*12+3,-74,p);
            p.setColor(0xffba2639);canvas.drawRect(-width*.8f,-77,width*.8f,-72,p);
        }
        if(type==THOR){
            p.setColor(0xffdce0e5);
            for(int row=0;row<3;row++)for(int col=-1;col<=1;col+=2)
                canvas.drawCircle(col*11,-123+row*15,5,p);
            p.setColor(0xffb79a32);canvas.drawRect(-width*.83f,-81,width*.83f,-76,p);
        }
        if(type==MARVEL){
            p.setColor(0xffbd253f);
            canvas.drawPath(poly(-width,-132,width,-132,width*.74f,-112,-width*.74f,-112),p);
            canvas.drawRect(-width*.8f,-86,width*.8f,-79,p);
        }
        if(type==SCARLET){
            p.setColor(0xff79203d);
            canvas.drawPath(poly(-width,-132,0,-90,width,-132,width*.78f,-75,-width*.78f,-75),p);
        }
        if(type==PANTHER){
            p.setColor(0xffbda9d5);p.setStrokeWidth(2);
            for(int i=-3;i<=3;i++)canvas.drawLine(i*7,-132,i*6,-122,p);
        }
        if(type==DOOM){
            p.setColor(0xff236b3c);
            canvas.drawPath(poly(-width,-132,width,-132,width+4,-76,-width-4,-76),p);
            p.setColor(0xffd4ad3c);
            canvas.drawCircle(-width+5,-130,5,p);
            canvas.drawCircle(width-5,-130,5,p);
            p.setColor(0xff91a79c);canvas.drawRect(-width*.73f,-82,width*.73f,-75,p);
        }
        if(type==THANOS){
            p.setColor(0xffd2a536);
            canvas.drawPath(poly(-width-4,-132,0,-114,width+4,-132,width*.7f,-122,
                    -width*.7f,-122),p);
            canvas.drawRect(-width*.85f,-82,width*.85f,-76,p);
        }
        if(type==CUSTOM){
            p.setColor(secondary);
            if(style==1){
                canvas.drawPath(poly(-width*.82f,-84,0,-65,width*.82f,-84),p);
                canvas.drawPath(poly(-width,-132,-width-8,-112,-width*.55f,-110),p);
            }else if(style==2){
                canvas.drawPath(poly(-width-10,-132,-width*.45f,-137,-width,-110),p);
                canvas.drawPath(poly(width+10,-132,width*.45f,-137,width,-110),p);
            }else{
                canvas.drawRoundRect(-width+4,-135,-width+14,-117,3,3,p);
                canvas.drawRoundRect(width-14,-135,width-4,-117,3,3,p);
            }
            p.setColor(accent);
            canvas.drawRect(-width*.72f,-82,width*.72f,-77,p);
        }
        drawInsignia(canvas,p,type,accent);

        float backHandX=-37,backHandY=-88,frontHandX=45,frontHandY=-98;
        if(pose==POSE_IDLE){
            backHandY+=1.5f*(float)Math.sin(phase*2.8f);
            frontHandY-=1.5f*(float)Math.sin(phase*2.8f);
        }
        float extension=(float)Math.sin(Math.PI*Math.min(1,Math.max(0,phase)));
        if(pose==POSE_PUNCH){frontHandX+=extension*69;frontHandY-=extension*19;}
        if(pose==POSE_BLAST){
            if(type==HULK){frontHandX=18;backHandX=-18;frontHandY=backHandY=-106;}
            else {frontHandX+=extension*45;frontHandY-=extension*22;
                backHandX+=extension*25;backHandY-=extension*18;}
        }
        if(pose==POSE_GUARD){frontHandX=18;frontHandY=-151;backHandX=-13;backHandY=-150;}
        if(pose==POSE_CLASH){
            frontHandX=type==CAPTAIN?34:57;frontHandY=-122;
            backHandX=type==CAPTAIN?37:-35;backHandY=type==CAPTAIN?-119:-85;
        }
        if(pose==POSE_VICTORY){frontHandX=49;frontHandY=-181;backHandX=-42;backHandY=-161;}
        if(pose==POSE_HIT){frontHandX=27;frontHandY=-144;backHandX=-57;backHandY=-113;}
        p.setStrokeWidth(arm);
        p.setColor(type==HULK?0xff48b460:pose==POSE_HIT?0xffdbe7ff:
                type==IRON?0xffd72c35:type==DOOM?0xff809393:secondary);
        canvas.drawLine(-width+4,-124,backHandX,backHandY,p);
        canvas.drawLine(width-4,-124,frontHandX,frontHandY,p);
        p.setColor(type==HULK?0xff4ebe64:type==THANOS?0xffa876b9:
                type==CUSTOM?skinColor(skin):accent);
        canvas.drawCircle(backHandX,backHandY,arm*.55f,p);
        canvas.drawCircle(frontHandX,frontHandY,arm*.55f,p);
        drawHeldItem(canvas,p,type,frontHandX,frontHandY,backHandX,backHandY,pose);
        drawHead(canvas,p,type,primary,secondary,accent,head,skin,hair,style,hero);
        if(pose==POSE_GUARD){
            p.setColor(type==DOOM?0xff81f49a:accent);
            p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(3);
            canvas.drawArc(-39,-168,43,-71,-76,152,false,p);
            p.setStyle(Paint.Style.FILL);
        }
        canvas.restore();
    }

    private static Path poly(float... points){
        Path path=new Path();path.moveTo(points[0],points[1]);
        for(int i=2;i<points.length;i+=2)path.lineTo(points[i],points[i+1]);
        path.close();return path;
    }
    private static void drawCape(Canvas c,Paint p,int type,int primary){
        int cape=type==THOR?0xffbf263b:type==STRANGE?0xffb9223d:
                type==DOOM?0xff1e743b:type==SCARLET?0xff8c183b:-1;
        if(cape==-1)return;
        Path shape=new Path();shape.moveTo(-18,-139);shape.quadTo(-70,-109,-49,-17);
        shape.lineTo(7,-67);shape.lineTo(22,-133);shape.close();
        p.setColor(cape);c.drawPath(shape,p);
        p.setColor(shade(cape,.7f));
        p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(3);
        c.drawPath(shape,p);p.setStyle(Paint.Style.FILL);
    }
    private static void drawInsignia(Canvas c,Paint p,int type,int accent){
        p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(2.5f);
        if(type==SPIDER){
            p.setColor(0xfff8f9ff);
            c.drawCircle(0,-109,13,p);c.drawCircle(0,-109,7,p);
            for(int i=-2;i<=2;i++)c.drawLine(i*5,-126,i*5,-92,p);
            c.drawLine(-24,-111,24,-111,p);
        }else if(type==IRON){
            p.setColor(0xffc5f9ff);p.setStrokeWidth(5);
            c.drawCircle(0,-108,11,p);
            p.setStrokeWidth(1.5f);c.drawCircle(0,-108,17,p);
        }else if(type==CAPTAIN||type==MARVEL){
            p.setColor(0xffffffff);
            c.drawPath(star(0,-110,13),p);
            if(type==CAPTAIN){c.drawLine(-20,-95,20,-95,p);c.drawLine(-20,-88,20,-88,p);}
        }else if(type==THOR){
            p.setColor(0xffd8ecff);
            c.drawPath(poly(-2,-122,8,-122,1,-109,7,-109,-7,-94,-3,-108,-9,-108),p);
        }else if(type==HULK){
            p.setColor(0xff2f8e47);c.drawArc(-27,-126,27,-92,20,140,false,p);
        }else if(type==PANTHER){
            p.setColor(0xffb0a6dc);
            for(int i=-2;i<=2;i++)c.drawLine(i*8,-130,i*6,-120,p);
        }else if(type==STRANGE){
            p.setColor(0xffffc43b);c.drawCircle(0,-107,14,p);
            c.drawPath(poly(0,-121,12,-107,0,-93,-12,-107),p);
        }else if(type==WIDOW){
            p.setColor(0xffdb353d);
            c.drawPath(poly(-8,-115,8,-115,-8,-103,8,-103),p);
        }else if(type==SCARLET){
            p.setColor(0xffff6280);c.drawPath(poly(0,-125,11,-106,0,-95,-11,-106),p);
        }else if(type==DOOM){
            p.setColor(0xffbed4c0);
            c.drawPath(poly(0,-126,13,-118,13,-101,0,-93,-13,-101,-13,-118),p);
            p.setColor(0xff335b43);c.drawLine(-6,-113,6,-113,p);
            c.drawLine(-6,-106,6,-106,p);
        }else if(type==THANOS){
            p.setColor(0xffffce51);c.drawPath(poly(-12,-120,12,-120,15,-100,-15,-100),p);
        }else {
            p.setColor(accent);c.drawPath(poly(0,-122,12,-108,0,-94,-12,-108),p);
        }
        p.setStyle(Paint.Style.FILL);
    }
    private static Path star(float x,float y,float radius){
        Path star=new Path();
        for(int i=0;i<10;i++){
            double angle=-Math.PI/2+i*Math.PI/5;
            float r=i%2==0?radius:radius*.43f;
            float px=x+(float)Math.cos(angle)*r,py=y+(float)Math.sin(angle)*r;
            if(i==0)star.moveTo(px,py);else star.lineTo(px,py);
        }
        star.close();return star;
    }
    private static void drawHeldItem(Canvas c,Paint p,int type,float fx,float fy,float bx,float by,int pose){
        if(type==WOLVERINE){
            p.setColor(0xffdee9fa);
            p.setStrokeWidth(2);
            for(int i=-1;i<=1;i++)c.drawLine(fx,fy+i*4,fx+26,fy+i*4-8,p);
        }else if(type==CAPTAIN){
            float shieldScale=pose==POSE_CLASH?1.55f:1;
            p.setColor(0xffca2637);c.drawCircle(bx,by,18*shieldScale,p);
            p.setColor(0xffeaf0f3);c.drawCircle(bx,by,13*shieldScale,p);
            p.setColor(0xff254f99);c.drawCircle(bx,by,8*shieldScale,p);
            p.setColor(Color.WHITE);c.drawPath(star(bx,by,7*shieldScale),p);
        }else if(type==THOR){
            p.setColor(0xff9eafc3);c.drawRoundRect(fx+1,fy-21,fx+27,fy-4,3,3,p);
            p.setColor(0xff765130);c.drawRect(fx+11,fy-3,fx+15,fy+17,p);
        }else if(type==WIDOW){
            p.setColor(0xff37b5ec);p.setStrokeWidth(5);
            c.drawLine(fx-3,fy-12,fx+11,fy+11,p);
        }else if(type==THANOS){
            p.setColor(0xffd7aa34);c.drawCircle(fx,fy,12,p);
            int[] gems={0xff40c8ff,0xffffd63c,0xffef3a4b,0xffa35cf5,0xff55d884};
            for(int i=0;i<gems.length;i++){
                p.setColor(gems[i]);
                c.drawCircle(fx-7+(i%3)*6,fy-5+(i/3)*7,2.8f,p);
            }
        }else if(type==DOOM){
            p.setColor(0xff66f28a);
            p.setShadowLayer(8,0,0,0xff5aff83);
            c.drawCircle(fx,fy,11,p);c.drawCircle(bx,by,10,p);
            p.clearShadowLayer();
        }else if(type==STRANGE||type==SCARLET){
            p.setColor(type==STRANGE?0xffffc73e:0xffff4d6d);
            p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(2.5f);
            c.drawCircle(fx+4,fy,15,p);
            p.setStyle(Paint.Style.FILL);
        }
    }
    private static void drawHead(Canvas c,Paint p,int type,int primary,int secondary,
                                 int accent,float radius,int skin,int hair,int style,String hero){
        float cy=-158;
        if(type==DOOM){
            Path hood=new Path();hood.moveTo(-32,-135);hood.lineTo(-31,-168);
            hood.quadTo(-16,-202,0,-194);hood.quadTo(24,-185,31,-167);
            hood.lineTo(32,-135);hood.lineTo(19,-138);hood.lineTo(-19,-138);hood.close();
            p.setColor(0xff1d753d);c.drawPath(hood,p);
            p.setColor(0xffaab9b2);c.drawRect(-19,-176,19,-140,p);
            p.setColor(0xff20302d);c.drawRoundRect(-15,-165,-3,-162,1,1,p);
            c.drawRoundRect(3,-165,15,-162,1,1,p);
            c.drawRoundRect(-13,-151,13,-148,1,1,p);
            p.setColor(0xffd2ddd5);c.drawRect(-15,-143,15,-140,p);
            return;
        }
        if(type==CUSTOM){
            int skinTone=skinColor(skin);
            p.setColor(skinTone);c.drawCircle(0,cy,radius,p);
            p.setColor(0xff1c1c26);
            if(hair==2)c.drawArc(-radius,-radius+cy,radius,radius+cy,180,180,true,p);
            else if(hair==1){
                c.drawArc(-radius,-radius+cy,radius,radius+cy,180,190,true,p);
                c.drawOval(-radius,-151,-radius+11,-116,p);
            }else{
                c.drawArc(-radius,-radius+cy,radius,radius+cy,180,195,true,p);
                if(style==1)c.drawOval(-radius,-158,-radius+9,-134,p);
                if(style==2)c.drawPath(poly(-radius,-175,-10,-187,0,-179,10,-188,
                        radius,-172),p);
            }
            p.setColor(0xff202027);c.drawOval(8,-160,12,-156,p);
            return;
        }
        int base=type==HULK?0xff48b460:type==THANOS?0xff9664a9:
                type==STRANGE||type==WIDOW||type==SCARLET||type==MARVEL?
                        0xffe3a77e:primary;
        p.setColor(0xff11101a);c.drawCircle(0,cy,radius+2,p);
        p.setColor(base);c.drawCircle(0,cy,radius,p);
        if(type==WOLVERINE){
            p.setColor(0xff151627);
            c.drawPath(poly(-23,-153,-31,-190,-12,-178,0,-161),p);
            c.drawPath(poly(6,-162,20,-193,23,-160,12,-149),p);
        }else if(type==PANTHER){
            p.setColor(0xff171321);
            c.drawPath(poly(-19,-169,-16,-191,-4,-177),p);
            c.drawPath(poly(4,-177,16,-191,20,-169),p);
            p.setColor(0xffb1a4e0);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(2);
            c.drawArc(-20,-177,20,-137,200,140,false,p);p.setStyle(Paint.Style.FILL);
        }else if(type==THOR){
            p.setColor(0xffe3cb73);c.drawArc(-22,-182,23,-134,180,220,true,p);
            p.setColor(0xffaebdcc);c.drawPath(poly(-21,-173,-32,-183,-26,-163),p);
            c.drawPath(poly(18,-173,30,-183,26,-163),p);
        }else if(type==IRON){
            p.setColor(0xffe4b432);c.drawRoundRect(-18,-175,18,-142,7,7,p);
            p.setColor(0xffa82330);c.drawRect(-20,-180,20,-173,p);
            p.setColor(0xffc6f9ff);c.drawRoundRect(0,-162,18,-157,2,2,p);
            return;
        }else if(type==CAPTAIN){
            p.setColor(0xffffffff);p.setTextSize(14);p.setFakeBoldText(true);
            c.drawText("A",-7,-171,p);p.setFakeBoldText(false);
            p.setColor(0xfff4d6c0);c.drawOval(7,-156,23,-141,p);
        }else if(type==WIDOW||type==SCARLET||type==MARVEL||type==STRANGE){
            int hairColor=type==WIDOW?0xffa13b32:type==SCARLET?0xff6b2b31:
                    type==MARVEL?0xffe8c85e:0xff20212b;
            p.setColor(hairColor);
            c.drawArc(-22,-181,22,-137,180,220,true,p);
            if(type==WIDOW||type==MARVEL)c.drawOval(-23,-160,-12,-128,p);
            if(type==SCARLET){
                p.setColor(0xffb52946);
                c.drawPath(poly(-20,-172,-10,-192,0,-178,10,-192,21,-171),p);
            }
        }else if(type==HULK){
            p.setColor(0xff273127);c.drawArc(-26,-183,26,-148,180,190,true,p);
        }else if(type==THANOS){
            p.setColor(0xffceab42);c.drawArc(-27,-182,27,-143,195,155,false,p);
            p.setStrokeWidth(4);p.setStyle(Paint.Style.STROKE);
            c.drawArc(-27,-182,27,-143,195,155,false,p);p.setStyle(Paint.Style.FILL);
        }
        if(type==SPIDER){
            p.setColor(hero!=null&&hero.contains("outra dimensão")?0xffc8c5c9:0xff8e1421);
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(hero!=null&&hero.contains("outra dimensão")?2.2f:1.5f);
            for(int i=-2;i<=2;i++)c.drawLine(i*8,-179,i*9,-138,p);
            c.drawArc(-20,-174,20,-144,0,180,false,p);
            c.drawArc(-17,-170,17,-146,0,180,false,p);p.setStyle(Paint.Style.FILL);
        }
        p.setColor(type==SPIDER||type==PANTHER||type==WOLVERINE||type==CAPTAIN
                ?0xffffffff:0xff242126);
        Path eye=poly(1,-162,19,-166,20,-156,6,-151);
        c.drawPath(eye,p);
        if(type==SPIDER||type==WOLVERINE){
            p.setColor(0xff111018);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(3);
            c.drawPath(eye,p);p.setStyle(Paint.Style.FILL);
        }
    }
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
    public static float visualScale(String hero){
        int kind=type(hero);
        return kind==HULK?.83f:kind==THANOS?.80f:
                kind==DOOM?.76f:kind==WIDOW||kind==SCARLET?.69f:.72f;
    }
}
