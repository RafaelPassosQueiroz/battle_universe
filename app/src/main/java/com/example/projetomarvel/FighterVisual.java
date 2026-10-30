package com.example.projetomarvel;

/** Presentation state only; never participates in hits, cooldowns or AI decisions. */
final class FighterVisual {
    String id,power="Energia";int dir=1,attack=-1,build=1,style;
    double distortion,electric;
    double vx,y,vy,walk,age,duration=.28,hit,land,koAge,specialTime,specialDuration,clock,visualTime,alpha=1;
    boolean shield,guard,ko,victory,specialActive,armed,shieldAway,hammerAway,dusted,custom,robot;
    NativeFighterArt.Palette palette;
    FighterPose pose;
    private double lastX,lastY,lastTime=-1;
    FighterVisual(String hero){id=BattleRoster.id(hero);}
    void configure(int color,int skin,int build,int hair,int style,String power){
        this.custom=true;this.build=build;this.style=style;this.power=power==null?"Energia":power;id="custom";
        palette=NativeFighterArt.PALETTES.get("strange").copy();
        int tint=color==-1?0xff468dff:color;
        palette.main=hex(tint);palette.shadow=shade(tint,.69);palette.deep=shade(tint,.39);
        palette.accent=style==1?"#e0c18a":style==2?"#accbe0":"#d2dce1";
        palette.face=skin==0?"#f2c39d":skin==2?"#835038":"#bc805e";
        palette.hair=hair==2?null:"#263241";palette.shortHair=hair==0;
        palette.cape=style==2?shade(tint,.60):null;palette.eye="#28354a";
        palette.glove=palette.main;palette.boot=palette.shadow;palette.energy="Gelo".equals(this.power)?"#a4eaff":hex(tint);
    }
    void generic(String kind){
        custom=true;id="custom";robot="doombot".equals(kind);build=1;
        palette=NativeFighterArt.PALETTES.get("doom").copy();palette.cape=null;palette.hair=null;
        palette.main=robot?"#568875":"#45665b";palette.shadow=robot?"#355e50":"#2b4843";
        palette.deep="#203b38";palette.accent=robot?"#98b7a7":"#78978a";
        palette.face=robot?"#a6bbb3":"#a47d5b";palette.eye=robot?"#b5ffe0":"#213530";palette.glove=palette.shadow;palette.boot="#233d38";
    }
    void sync(BattleEngine.Fighter f,double t){
        double dt=lastTime<0?0:Math.max(0,Math.min(.05,t-lastTime));
        vx=dt>0?(f.x-lastX)/dt:0;
        if(dt>0&&f.y==0&&lastY>0)land=.16;else land=Math.max(0,land-dt);
        if(f.knockedOut)koAge+=dt;else koAge=0;
        y=f.y;vy=f.vy;dir=f.dir;walk=f.walkTime*7;attack=f.attack;age=f.attackTime;duration=f.attackDuration;
        hit=f.hitTime;guard=f.guard;ko=f.knockedOut;specialActive=f.specialActive&&!(f.custom&&f.skill==BattleEngine.Skill.STRENGTH);shield=f.forceShield;distortion=f.distortion;electric=f.electricTime;
        specialTime=f.specialTime;specialDuration=f.specialDuration;armed=f.armedMelee;dusted=f.dusted;clock=t;
        lastX=f.x;lastY=f.y;lastTime=t;
    }
    void preview(int pose,double phase,double t){
        clock=t;
        switch(pose){
            case FighterArtView.POSE_WALK:vx=220;walk=phase*7;break;
            case FighterArtView.POSE_JUMP:y=70;vy=120;break;
            case FighterArtView.POSE_PUNCH:attack=0;duration=.28;age=phase*duration;break;
            case FighterArtView.POSE_KICK:attack=1;duration=.4;age=phase*duration;break;
            case FighterArtView.POSE_BLAST:attack=2;duration=.5;age=phase*duration;break;
            case FighterArtView.POSE_HIT:hit=.18;break;
            case FighterArtView.POSE_GUARD:guard=true;break;
            case FighterArtView.POSE_KO:ko=true;koAge=Math.max(.65,phase);break;
            case FighterArtView.POSE_VICTORY:victory=true;break;
            case FighterArtView.POSE_CLASH:attack=2;duration=.5;age=.22;break;
            default:break;
        }
    }
    double width(){return custom?(build==0?.93:build==2?1.16:1)*(style==1?.97:1):id.equals("hulk")?1.24:id.equals("thanos")?1.15:id.equals("wolverine")?1.04:1;}
    double height(){return custom?(build==2?1.025:1):id.equals("hulk")?1.16:id.equals("thanos")?1.10:id.equals("wolverine")?.97:1;}
    double agility(){return id.equals("captain")||id.equals("iron")||custom?1:BattleRoster.find(id).agility;}
    private static String hex(int color){return String.format(java.util.Locale.ROOT,"#%06x",color&0xffffff);}
    private static String shade(int color,double f){return hex(android.graphics.Color.rgb((int)(android.graphics.Color.red(color)*f),(int)(android.graphics.Color.green(color)*f),(int)(android.graphics.Color.blue(color)*f)));}
}
