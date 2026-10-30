package com.example.projetomarvel;
import android.content.Context;
import android.graphics.*;
import java.util.Locale;

/** Screen-space web-equivalent HUD; independent of camera and touch controls. */
final class BattleHud {
    private final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Typeface title,body;
    private float ghostA=1,ghostB=1;
    BattleHud(Context c){this(Typography.bebas(c),Typography.barlow(c));}
    BattleHud(Typeface title,Typeface body){this.title=title;this.body=body;}
    void reset(){ghostA=ghostB=1;}
    void draw(Canvas canvas,int width,int height,BattleEngine b,BattleCamera camera,String player,String bot,String announcement,double dt){
        if(b==null)return;float scale=width/1100f,h=height/scale;canvas.save();canvas.scale(scale,scale);
        p.setShader(new LinearGradient(0,0,0,115,new int[]{0xf2102536,0xbd102536,0x00102536},null,Shader.TileMode.CLAMP));p.setStyle(Paint.Style.FILL);canvas.drawRect(0,0,1100,115,p);p.setShader(null);
        float a=b.player.hp/b.player.maxHp,z=b.bot.hp/b.bot.maxHp;
        ghostA=Math.max(a,(float)BattleCamera.approach(ghostA,a,1.5,dt));ghostB=Math.max(z,(float)BattleCamera.approach(ghostB,z,1.5,dt));
        label(canvas,"JOGADOR",38,27,7,0xffd1caa9,false,false);
        label(canvas,player.toUpperCase(Locale.ROOT),95,30,20,0xfffff6e5,true,false);
        label(canvas,Math.max(0,(int)Math.ceil(b.player.hp))+" / "+(int)b.player.maxHp,488,29,9,0xffc4d5dc,false,true);
        label(canvas,"CPU",1062,27,7,0xffd1caa9,false,true);
        label(canvas,bot.toUpperCase(Locale.ROOT),1025,30,20,0xfffff6e5,true,true);
        label(canvas,Math.max(0,(int)Math.ceil(b.bot.hp))+" / "+(int)b.bot.maxHp,610,29,9,0xffc4d5dc,false,false);
        bar(canvas,38,46,450,a,ghostA,false);bar(canvas,610,46,452,z,ghostB,true);
        label(canvas,"VS",550,37,34,0xfff3d494,true,false,Paint.Align.CENTER);
        label(canvas,String.format(Locale.ROOT,"%02d:%02d",(int)b.time/60,(int)b.time%60),550,57,8,0xffa4b6c4,false,false,Paint.Align.CENTER);
        label(canvas,status(b.player),38,68,7,0xffefc980,false,false);
        label(canvas,status(b.bot),1062,68,7,0xffefc980,false,true);
        p.setColor(0xffecd89c);canvas.drawCircle(40,94,2,p);label(canvas,"ASGARD · PONTE DO ARCO-ÍRIS",49,97,7,0xffd3d9cf,false,false);
        // Same arena overview and focus-window indicator as the web HUD.
        float mapX=474,mapW=152,mapY=h-28;
        label(canvas,"BIFRÖST",550,mapY-13,17,0x99d9cd9c,true,false,Paint.Align.CENTER);
        p.setColor(0x40ffffff);p.setStrokeWidth(1);canvas.drawLine(mapX,mapY,mapX+mapW,mapY,p);
        p.setStyle(Paint.Style.STROKE);p.setColor(0x40ffffff);
        float span=(float)(1100/camera.zoom),left=(float)Math.max(0,camera.x-span/2)/BattleEngine.WIDTH;
        canvas.drawRoundRect(mapX+left*mapW,mapY-4,mapX+Math.min(1,left+span/BattleEngine.WIDTH)*mapW,mapY+3,2,2,p);
        p.setStyle(Paint.Style.FILL);p.setColor(0xffc3e8b7);canvas.drawCircle(mapX+b.player.x/BattleEngine.WIDTH*mapW,mapY-1,2.2f,p);p.setColor(0xffed98b0);canvas.drawCircle(mapX+b.bot.x/BattleEngine.WIDTH*mapW,mapY-1,2.2f,p);
        label(canvas,"ASGARD / 03",550,h-10,6,0xffa7c1c2,false,false,Paint.Align.CENTER);
        if(announcement!=null&&!announcement.isEmpty())label(canvas,announcement,550,h*.42f,announcement.length()>15?34:announcement.length()>3?52:84,0xffffe0a5,true,false,Paint.Align.CENTER);
        canvas.restore();
    }
    private void bar(Canvas c,float x,float y,float w,float hp,float ghost,boolean bot){
        p.setColor(0xff040b14);c.drawRect(x,y,x+w,y+6,p);p.setColor(0xffe1c798);
        c.drawRect(bot?x+w-w*ghost:x,y,bot?x+w:x+w*ghost,y+6,p);
        p.setShader(new LinearGradient(x,y,x+w,y,bot?0xffed98b0:0xff8ad1af,bot?0xffe85e88:0xffc3e8b7,Shader.TileMode.CLAMP));
        c.drawRect(bot?x+w-w*hp:x,y,bot?x+w:x+w*hp,y+6,p);p.setShader(null);
        p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(.7f);p.setColor(0x55e2cf94);c.drawRect(x,y,x+w,y+6,p);p.setStyle(Paint.Style.FILL);
    }
    private void label(Canvas c,String text,float x,float y,float size,int color,boolean heading,boolean right){label(c,text,x,y,size,color,heading,right,right?Paint.Align.RIGHT:Paint.Align.LEFT);}
    private void label(Canvas c,String text,float x,float y,float size,int color,boolean heading,boolean right,Paint.Align align){p.setTypeface(heading?title:body);p.setTextSize(size);p.setTextAlign(align);p.setColor(color);p.setStyle(Paint.Style.FILL);c.drawText(text,x,y,p);p.setTextAlign(Paint.Align.LEFT);}
    static String status(BattleEngine.Fighter f){
        if(f.knockedOut)return "DERROTADO";if(f.electricTime>0)return "ATORDOADO · DESCARGA ELÉTRICA";if(f.frozen>0)return "CONGELADO";if(f.distortion>0)return "DISTORÇÃO · "+(int)Math.ceil(f.distortion)+"s";
        if(f.armedMelee)return "ABATE SILENCIOSO · ARMADO";if(f.guardLock>0)return "DEFESA EM RECUPERAÇÃO · "+(int)Math.ceil(f.guardLock)+"s";if(f.guard)return "DEFENDENDO";
        if(f.specialActive)return f.custom?BattleEngine.powerName(f.skill).toUpperCase(Locale.ROOT):BattleRoster.find(BattleRoster.id(f.name)).special.toUpperCase(Locale.ROOT);
        if(f.exhaustion>0)return "RECUPERANDO ENERGIA";if(f.forceShield)return "ESCUDO AUTOMÁTICO";if(f.skill==BattleEngine.Skill.PANTHER&&f.defendedAttacks>0)return "VIBRANIUM · "+f.defendedAttacks+"/10";
        if(f.skill==BattleEngine.Skill.HULK&&f.hp<=30)return "FÚRIA ATIVA";if(f.slow>0)return "LENTIDÃO · TEIA";if(f.custom&&f.skill==BattleEngine.Skill.ENERGY)return "ENERGIAS · "+f.energyHits+"/12";if(f.ally!=null&&f.ally.hp>0)return "ALIADO · "+(int)Math.ceil(f.ally.hp)+" HP";return "";
    }
}
