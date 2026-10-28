//aparência habilidades
package com.example.projetomarvel;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import com.example.projetomarvel.BattleEngine.Effect;
import com.example.projetomarvel.BattleEngine.Fighter;
import com.example.projetomarvel.BattleEngine.Skill;

/** Readable ability cues drawn in the existing fixed camera, using the combat's actual geometry. */
final class CombatEffects {
    private CombatEffects(){}
    static int specialPose(Fighter f){
        switch(f.skill){
            case SPIDER:return f.specialTime>1.05f?FighterArtView.POSE_KICK:FighterArtView.POSE_JUMP;
            case HULK:case MARVEL:return FighterArtView.POSE_JUMP;
            case CAPTAIN:return FighterArtView.POSE_GUARD;
            case GENERIC:return FighterArtView.POSE_WALK;
            case WOLVERINE:case SPEED:return FighterArtView.POSE_PUNCH;
            case NINJA:return FighterArtView.POSE_GUARD;
            default:return FighterArtView.POSE_BLAST;
        }
    }
    static int color(Fighter f,int customColor){return f.custom?
            f.skill==Skill.ICE?0xffa4eaff:customColor:AttackEffects.color(f.name);}
    static void drawWorld(Canvas c,BattleEngine engine,float ground,int customColor,Paint p){
        if(engine==null)return;
        clean(p);
        for(BattleEngine.IcePatch ice:engine.ice){
            p.setColor(0x5597e7ff);c.drawRoundRect(ice.x-33,ground-3,ice.x+33,ground+7,5,5,p);
            p.setColor(0x997ad9f5);p.setStrokeWidth(1);
            c.drawLine(ice.x-20,ground+3,ice.x+19,ground+3,p);
        }
        for(Effect e:engine.effects){
            int tint=e.form==BattleEngine.Form.TIME?0xff67ed8e:color(e.owner,customColor);
            boolean warning=e.age<e.delay;
            float fade=warning?1:Math.max(0,1-(e.age-e.delay)/Math.max(.01f,e.duration));
            p.setColor(tint);p.setAlpha(warning?110:(int)(210*fade));
            p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(warning?2:5);
            float y=ground-e.y;
            switch(e.form){
                case BEAM:
                    float end=e.direction>0?BattleEngine.WIDTH:0;
                    p.setStrokeWidth(warning?2:22);c.drawLine(e.x,y,end,y,p);
                    if(!warning){p.setColor(0xdde9fbff);p.setStrokeWidth(7);c.drawLine(e.x,y,end,y,p);}
                    break;
                case STRIKE:
                    if(warning){c.drawOval(e.x-e.range,ground-7,e.x+e.range,ground+7,p);
                        c.drawLine(e.x,55,e.x,ground,p);}
                    else if(e.owner.skill==Skill.THOR){
                        Path bolt=new Path();bolt.moveTo(e.x,35);bolt.lineTo(e.x-15,105);
                        bolt.lineTo(e.x+13,145);bolt.lineTo(e.x-10,205);bolt.lineTo(e.x,ground);
                        p.setStrokeWidth(11);c.drawPath(bolt,p);p.setColor(0xffeefaff);p.setStrokeWidth(3);c.drawPath(bolt,p);
                    }else{
                        p.setStrokeWidth(35);c.drawLine(e.x,25,e.x,ground,p);
                        p.setColor(0xdddeffe5);p.setStrokeWidth(9);c.drawLine(e.x,25,e.x,ground,p);
                        p.setStrokeWidth(2);c.drawCircle(e.x,50,19,p);c.drawCircle(e.x,50,27,p);
                    }
                    break;
                case GREEN_RAY:
                    if(warning){
                        p.setColor(0x9967e887);p.setStrokeWidth(2);
                        c.drawLine(e.x,35,e.x,ground,p);
                        c.drawOval(e.x-20,ground-7,e.x+20,ground+7,p);
                    }else{
                        p.setColor(0x8867ff91);p.setStrokeWidth(27);
                        c.drawLine(e.x,28,e.x,ground,p);
                        p.setColor(0xffc8ffd5);p.setStrokeWidth(7);
                        c.drawLine(e.x,28,e.x,ground,p);
                    }
                    break;
                case THOR_SPARK:
                    p.setColor(0xffc5eaff);p.setStrokeWidth(6);
                    Path smallBolt=new Path();smallBolt.moveTo(e.x-6,ground-85);
                    smallBolt.lineTo(e.x+5,ground-56);smallBolt.lineTo(e.x-7,ground-29);
                    smallBolt.lineTo(e.x+3,ground);c.drawPath(smallBolt,p);
                    c.drawOval(e.x-25,ground-8,e.x+25,ground+6,p);
                    break;
                case WAVE:
                    float wave=e.range*Math.max(.1f,Math.min(1,(e.age-e.delay)/e.duration));
                    c.drawOval(e.x-wave,ground-15,e.x+wave,ground+10,p);
                    c.drawOval(e.x-wave*.7f,ground-8,e.x+wave*.7f,ground+5,p);break;
                case BURST:
                    float radius=e.range*Math.max(.12f,Math.min(1,(e.age-e.delay)/e.duration));
                    c.drawCircle(e.x,y,radius,p);c.drawCircle(e.x,y,radius*.6f,p);break;
                case PORTAL:case TIME:
                    c.drawOval(e.x-40,y-60,e.x+40,y+60,p);
                    c.drawOval(e.x-31,y-48,e.x+31,y+48,p);
                    if(e.form==BattleEngine.Form.TIME){c.drawLine(e.x,y,e.x,y-23,p);c.drawLine(e.x,y,e.x+17,y,p);}
                    break;
                case SLASH:
                    c.save();c.translate(e.x,y);c.scale(e.direction,1);
                    for(int i=-1;i<=1;i++)c.drawArc(-35,-25+i*9,35,25+i*9,-65,130,false,p);
                    c.restore();
                    break;
                case SMOKE:
                    p.setStyle(Paint.Style.FILL);p.setAlpha((int)(70*fade));
                    for(int i=0;i<4;i++)c.drawCircle(e.x-30+i*20,ground-e.y-55-i%2*24,28+e.age*22,p);break;
                case VOLLEY:
                    if(e.owner.skill==Skill.THANOS){p.setColor(0x88ffc268);
                        c.drawOval(e.x-20,ground-5,e.x+20,ground+5,p);}
                    break;
            }
            clean(p);
        }
    }
    static void drawStatus(Canvas c,BattleEngine engine,Fighter f,float ground,int tint,Paint p){
        clean(p);float head=ground-f.y-145,feet=ground-f.y;
        if(f.forceShield||(f.specialActive&&f.skill==Skill.CAPTAIN)){
            p.setColor(f.skill==Skill.CAPTAIN?0xbb97dfff:0xbb67ed8e);
            p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(3);
            c.drawOval(f.x-45,feet-155,f.x+45,feet+5,p);clean(p);
        }
        if(f.frozen>0){
            p.setColor(0x668ee9ff);c.drawRoundRect(f.x-38,feet-148,f.x+38,feet+3,9,9,p);
            p.setColor(0xffcdf8ff);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(2);
            c.drawLine(f.x-28,feet-110,f.x+28,feet-35,p);c.drawLine(f.x+26,feet-135,f.x-21,feet-65,p);
            clean(p);smallText(c,p,"GELO "+(int)Math.ceil(f.frozen)+"s",f.x,head-14,0xffd4f6ff);
        }
        if(f.distortion>0)smallText(c,p,"SEM PULO / DEFESA",f.x,head-16,0xffff83a8);
        if(f.noJump>0)smallText(c,p,"SEM PULO "+(int)Math.ceil(f.noJump)+"s",f.x,head-16,0xffffc75d);
        if(f.exhaustion>0)smallText(c,p,"DESNORTEADO",f.x,head-16,0xff9ddfff);
        if(f.slow>0){p.setColor(0xffd5f4ff);p.setStyle(Paint.Style.STROKE);
            c.drawOval(f.x-27,feet-32,f.x+27,feet-4,p);clean(p);}
        if(f.electricTime>0||f.armedMelee&&f.skill==Skill.WIDOW){
            p.setColor(0xff51c6ff);p.setStrokeWidth(3);
            for(int i=0;i<4;i++){
                float x=f.x+(i%2==0?-23:23),y=feet-120+(i/2)*60;
                c.drawLine(x-5,y-10,x+4,y,p);c.drawLine(x+4,y,x-1,y+12,p);
            }
        }
        if(f.skill==Skill.THOR&&f.airDashTime>0){
            p.setColor(0x99c1eaff);p.setStrokeWidth(4);
            c.drawLine(f.x-(f.airDashVelocity>0?1:-1)*48,feet-72,f.x,feet-72,p);
        }
        if(f.skill==Skill.THANOS&&f.specialActive){
            float charge=Math.min(1,f.specialTime/1.5f);
            p.setColor(0xffdfb9ff);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(3);
            c.drawCircle(f.x+f.dir*32,feet-100,12+charge*19,p);
            clean(p);smallText(c,p,"ESTALO "+(int)Math.ceil(1.5f-f.specialTime)+"s",
                    f.x,head-16,0xffe6c5ff);
        }
        if(f.skill==Skill.STRENGTH&&f.specialActive){
            p.setColor(0x88ffca68);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(3);
            c.drawOval(f.x-43,feet-153,f.x+43,feet+4,p);
        }
        if(f.skill==Skill.SPIDER&&f.alert>0){
            p.setColor(0xffe1f6ff);p.setStrokeWidth(2);
            for(int i=-1;i<=1;i++)c.drawLine(f.x+i*18,head-10,f.x+i*24,head-22,p);
        }
        if(f.specialActive&&f.skill==Skill.SPIDER){
            p.setColor(0xcceefbff);p.setStrokeWidth(2);
            c.drawLine(f.specialStartX,52,f.x+f.dir*17,head+20,p);
        }
        if(f.specialActive&&(f.skill==Skill.MARVEL||f.skill==Skill.SPEED||f.skill==Skill.WOLVERINE)){
            p.setColor(tint);p.setAlpha(150);p.setStrokeWidth(f.skill==Skill.MARVEL?12:3);
            for(int i=0;i<3;i++)c.drawLine(f.x-f.dir*(40+i*15),feet-45-i*24,
                    f.x-f.dir*(95+i*12),feet-45-i*24,p);
        }
        if(f.specialActive&&f.skill==Skill.MARVEL){
            p.setStyle(Paint.Style.FILL);p.setAlpha(255);
            p.setShader(new RadialGradient(f.x,feet-72,85,
                    new int[]{0xcce9fbff,0x66ffe47a,0x00ffe47a},null,Shader.TileMode.CLAMP));
            c.drawOval(f.x-52,feet-157,f.x+52,feet+6,p);p.setShader(null);
        }
        if(f.skill==Skill.PANTHER&&f.defendedAttacks>0)
            smallText(c,p,"VIBRANIUM "+f.defendedAttacks,f.x,head-15,0xffc596ff);
        if(f.custom&&f.skill==Skill.ENERGY)
            smallText(c,p,"ENERGIAS "+f.energyHits+"/12",f.x,head-15,tint);
        if(f.skill==Skill.HULK&&f.hp<=30){p.setColor(0x559cff84);p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(4);c.drawOval(f.x-42,feet-155,f.x+42,feet+3,p);}
        if(f.skill==Skill.IRON&&f.y>10){p.setColor(0xaacaf4ff);p.setStrokeWidth(3);
            c.drawLine(f.x-16,feet,f.x-16,feet+15,p);c.drawLine(f.x+16,feet,f.x+16,feet+15,p);}
        clean(p);
    }
    static void drawAlly(Canvas c,Fighter a,float ground,Paint p){
        c.save();c.translate(a.x,ground);c.scale(a.dir,1);clean(p);
        float punch=a.attack>=0?(float)Math.sin(Math.PI*Math.min(1,a.attackTime/.28f))*30:0;
        p.setColor(0xbbeb446d);p.setStrokeWidth(12);p.setStrokeCap(Paint.Cap.ROUND);
        float stride=a.moving?(float)Math.sin(a.walkTime*10)*9:0;
        c.drawLine(-11,-48,-18+stride,-5,p);c.drawLine(11,-48,18-stride,-5,p);
        c.drawLine(-18,-85,-27,-56,p);c.drawLine(18,-85,30+punch,-62,p);
        p.setColor(0xcc9a294e);c.drawRoundRect(-21,-88,21,-40,9,9,p);
        p.setColor(0xffef7597);c.drawCircle(0,-106,16,p);
        p.setColor(0xff3e1635);c.drawRoundRect(4,-109,14,-104,2,2,p);
        p.setColor(0xfff6aec5);c.drawCircle(30+punch,-62,7,p);
        c.restore();clean(p);
        p.setColor(0xff3a172b);c.drawRoundRect(a.x-22,ground-135,a.x+22,ground-130,2,2,p);
        p.setColor(0xfffa7ca6);c.drawRoundRect(a.x-22,ground-135,a.x-22+44*a.hp/20,ground-130,2,2,p);
        smallText(c,p,Math.round(a.hp)+" HP",a.x,ground-143,0xffffb3ca);
    }
    static void drawProjectile(Canvas c,BattleEngine.Projectile shot,float ground,Paint p){
        clean(p);float y=ground-shot.y;
        if(shot.style.equals("meteor")){
            p.setColor(0x88ff9a39);p.setStrokeWidth(15);c.drawLine(shot.x,y-40,shot.x,y-9,p);
            p.setColor(0xffd57842);c.drawCircle(shot.x,y,16,p);p.setColor(0xffffd373);c.drawCircle(shot.x-4,y-4,7,p);
        }else if(shot.style.equals("object")){
            p.setColor(0xffa69cba);c.drawRoundRect(shot.x-13,y-11,shot.x+13,y+11,4,4,p);
            p.setColor(0xffe4dbfb);p.setStrokeWidth(2);c.drawLine(shot.x-9,y-6,shot.x+9,y+6,p);
        }else if(shot.style.equals("scarlet_special")){
            p.setColor(0x99ff163e);c.drawCircle(shot.x,y,25,p);
            p.setColor(0xffff3a5b);c.drawCircle(shot.x,y,15,p);
            p.setColor(0xffffdce5);c.drawCircle(shot.x,y,6,p);
        }else if(shot.style.equals("marvel_beam")){
            p.setColor(0x88ffe066);p.setStrokeWidth(16);
            c.drawLine(shot.x-shot.direction*38,y,shot.x+shot.direction*25,y,p);
            p.setColor(0xfff8f7d9);p.setStrokeWidth(5);
            c.drawLine(shot.x-shot.direction*38,y,shot.x+shot.direction*25,y,p);
        }else if(shot.style.equals("tele_special")){
            p.setColor(0x88b586ff);c.drawCircle(shot.x,y,25,p);
            p.setColor(0xffd8baff);c.drawCircle(shot.x,y,14,p);
            p.setColor(0xffffffff);c.drawCircle(shot.x,y,5,p);
        }else{
            p.setColor(0xffb2f0ff);c.drawCircle(shot.x,y,16,p);p.setColor(0xffedfaff);p.setStrokeWidth(2);
            for(int i=0;i<3;i++){double angle=i*Math.PI/3;c.drawLine(shot.x-(float)Math.cos(angle)*13,
                    y-(float)Math.sin(angle)*13,shot.x+(float)Math.cos(angle)*13,y+(float)Math.sin(angle)*13,p);}
        }
        clean(p);
    }
    private static void smallText(Canvas c,Paint p,String text,float x,float y,int color){
        p.setTypeface(Typeface.create("sans-serif-condensed",Typeface.BOLD));p.setTextSize(11);
        p.setTextAlign(Paint.Align.CENTER);p.setColor(color);c.drawText(text,x,y,p);p.setTextAlign(Paint.Align.LEFT);
    }
    private static void clean(Paint p){p.setShader(null);p.clearShadowLayer();p.setAlpha(255);
        p.setStyle(Paint.Style.FILL);p.setStrokeWidth(2);p.setStrokeCap(Paint.Cap.ROUND);}
}
