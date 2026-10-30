package com.example.projetomarvel;
import android.content.Context;
import android.graphics.*;
import java.util.*;
import static com.example.projetomarvel.VectorCanvas.Art.*;
import static com.example.projetomarvel.VectorCanvas.clamp;

/** Approved web particles, power silhouettes and trajectories, driven by native rules. */
final class BattleVfx {
    private static final class Particle {double x,y,vx,vy,life,size;String color;}
    private static final class Ring {double x,y,r=3,life=.24;String color;}
    private static final class Label {double x,y,life=.85;String text,color;}
    private final ArrayList<Particle> particles=new ArrayList<>();private final ArrayList<Ring> rings=new ArrayList<>();private final ArrayList<Label> labels=new ArrayList<>();
    private final Random random=new Random();private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);private final Typeface number,body;
    BattleVfx(Context c){this(Typography.bebas(c),Typography.barlowBold(c));}
    BattleVfx(Typeface number,Typeface body){this.number=number;this.body=body;}
    void clear(){particles.clear();rings.clear();labels.clear();}
    static String energy(BattleEngine.Fighter f,int custom){if(f.custom)return f.skill==BattleEngine.Skill.ICE?"#a4eaff":String.format(Locale.ROOT,"#%06x",custom&0xffffff);NativeFighterArt.Palette p=NativeFighterArt.PALETTES.get(BattleRoster.id(f.name));return p==null?"#89f4ad":p.energy;}
    void burst(double x,double y,String color,int count,double force){for(int i=0;i<count&&particles.size()<180;i++){double a=random.nextDouble()*Math.PI*2,s=force*(.3+random.nextDouble());Particle p=new Particle();p.x=x;p.y=y;p.vx=Math.cos(a)*s;p.vy=Math.sin(a)*s;p.life=.2+random.nextDouble()*.35;p.color=color;p.size=1+random.nextDouble()*2.7;particles.add(p);}if(rings.size()<30){Ring r=new Ring();r.x=x;r.y=y;r.color=color;rings.add(r);}}
    void hit(BattleEngine.Hit h,int custom){boolean blocked=h.guarded||h.blocked;burst(h.target.x,h.target.y+95,blocked?"#9ac9eb":energy(h.attacker,custom),blocked?7:12,blocked?60:125);if(labels.size()<20){Label l=new Label();l.x=h.target.x;l.y=h.target.y+125;l.text=h.damage>0?"−"+Math.round(h.damage):blocked?"DEFESA":"BLOQUEADO";l.color=blocked?"#a6daf9":"#ffe3b6";labels.add(l);}}
    void update(double dt){for(Particle p:particles){p.life-=dt;p.x+=p.vx*dt;p.y+=p.vy*dt;p.vy-=210*dt;}particles.removeIf(p->p.life<=0);for(Ring r:rings){r.life-=dt;r.r+=140*dt;}rings.removeIf(r->r.life<=0);for(Label l:labels){l.life-=dt;l.y+=24*dt;}labels.removeIf(l->l.life<=0);}
    void draw(Canvas actual,double ground){VectorCanvas c=new VectorCanvas(actual);c.translate(0,ground);for(Particle p:particles){c.globalAlpha=Math.min(1,p.life/.15);line(c,new double[][]{{p.x,-p.y},{p.x-p.vx*.025,-p.y+p.vy*.025}},p.color,p.size);}for(Ring r:rings){c.globalAlpha=r.life/.24;c.strokeStyle=r.color;c.lineWidth=1.3;c.beginPath();c.path.addOval((float)(r.x-r.r),(float)(-r.y-r.r*.8),(float)(r.x+r.r),(float)(-r.y+r.r*.8),Path.Direction.CW);c.stroke();}c.translate(0,-ground);
        for(Label l:labels){paint.setTypeface(l.text.length()>5?body:number);paint.setTextSize(l.text.length()>5?10:24);paint.setTextAlign(Paint.Align.CENTER);paint.setAlpha((int)(255*Math.min(1,l.life/.2)));paint.setStyle(Paint.Style.STROKE);paint.setStrokeWidth(3);paint.setColor(0xff101725);actual.drawText(l.text,(float)l.x,(float)(ground-l.y),paint);paint.setStyle(Paint.Style.FILL);paint.setColor(VectorCanvas.color(l.color));paint.setAlpha((int)(255*Math.min(1,l.life/.2)));actual.drawText(l.text,(float)l.x,(float)(ground-l.y),paint);}
    }
    static void projectile(Canvas actual,BattleEngine.Projectile p,double ground,double t,int custom){
        if(p.owner.custom&&p.style.equals("common")){AttackEffects.drawCustom(actual,p.x,(float)(ground-p.y),p.direction,BattleEngine.powerName(p.owner.skill),custom,new Paint(Paint.ANTI_ALIAS_FLAG));return;}
        VectorCanvas c=new VectorCanvas(actual);String id=BattleRoster.id(p.owner.name),style=p.style.equals("common")?id:p.style,color=energy(p.owner,custom);
        if(p.boomerang)style="captain";if(p.thorHammer)style="thor";if(style.equals("scarlet_special"))style="distortion";if(style.equals("marvel_beam"))style="marvel";
        c.save();c.translate(p.x,ground-p.y);c.scale(p.direction,1);
        if(style.equals("widow")){line(c,new double[][]{{-24,0},{8,0}},"#f8cf81",2.5);line(c,new double[][]{{-8,0},{7,0}},"#fff5cd",1.2);}
        else if(style.equals("captain")){c.rotate(t*20);ellipse(c,0,0,17,17,"#bd4056","#17243a");ellipse(c,0,0,12,12,"#e5e7e3");ellipse(c,0,0,8,8,"#365f9c");star(c,0,0,7,"#fff");}
        else if(style.equals("thor")){c.rotate(t*15);round(c,-2,-1,4,26,1,"#9c794e");round(c,-14,-12,27,15,3,"#b0c2d0","#4b607c");line(c,new double[][]{{-20,-13},{-12,-4},{-17,2},{-8,9}},"#bce9ff",1.5);}
        else if(style.equals("spider")){line(c,new double[][]{{-32,0},{0,0}},"#e8f6ff99",1);for(int i=0;i<3;i++){c.save();c.rotate(i*Math.PI/3);line(c,new double[][]{{-10,0},{11,0}},"#e6f5ff",1);c.restore();}ring(c,0,0,8,"#c5e9ef",t*9);}
        else if(style.equals("object")){round(c,-13,-11,26,22,4,"#a69cba","#e4dbfb");line(c,new double[][]{{-9,-6},{9,6}},"#e4dbfb",2);}
        else if(style.equals("meteor")){line(c,new double[][]{{0,-40},{0,-9}},"#ff9a3988",15);ellipse(c,0,0,16,16,"#d57842");ellipse(c,-4,-4,7,7,"#ffd373");}
        else if(style.equals("freeze")){for(int i=0;i<3;i++){c.save();c.rotate(i*Math.PI/3);line(c,new double[][]{{-13,0},{13,0}},"#c3f0ff",2);c.restore();}}
        else{
            double r=style.equals("distortion")?18:style.equals("scarlet")?14:style.equals("marvel")?11:10;
            VectorCanvas.Gradient g=c.createRadialGradient(0,0,1,0,0,r*2.4);g.addColorStop(0,"#ffffffcc");g.addColorStop(.22,color+"cc");g.addColorStop(1,color+"00");c.fillStyle=g;c.fillRect(-r*2.4,-r*2.4,r*4.8,r*4.8);
            ellipse(c,0,0,r*.53,r*.53,color);ellipse(c,2,-2,r*.2,r*.2,"#fff6");
            for(int i=0;i<3;i++){c.save();c.rotate(t*(3+i)+i*2);c.strokeStyle=color;c.lineWidth=1.4;c.beginPath();c.path.addArc((float)(-r*(1+i*.2)),(float)(-r*.6),(float)(r*(1+i*.2)),(float)(r*.6),(float)Math.toDegrees(.3),(float)Math.toDegrees(4.5));c.stroke();c.restore();}
            line(c,new double[][]{{-r*3,-3},{-r*1.8,-5},{-r*.4,0}},color+"77",2);
        }c.restore();
    }
    static void status(Canvas actual,BattleEngine.Fighter f,double ground,double t,int custom){
        VectorCanvas c=new VectorCanvas(actual);double feet=ground-f.y;
        if(f.skill==BattleEngine.Skill.THANOS&&f.specialActive)ring(c,f.x+f.dir*20,feet-120,20+f.specialTime*18,"#e3c58e",t*2);
        if(f.frozen>0){round(c,f.x-31,feet-148,62,151,8,"#9bdef52a","#bcecff99");for(int i=0;i<3;i++)line(c,new double[][]{{f.x-22+i*18,feet-126},{f.x-11+i*18,feet-113},{f.x-19+i*18,feet-94}},"#c8f5ff88",1.5);}
        if(f.custom&&f.skill==BattleEngine.Skill.STRENGTH&&f.specialActive){c.globalAlpha=.35;ring(c,f.x,feet-76,38,energy(f,custom),t*1.5);}
    }
    static void abilities(Canvas actual,BattleEngine b,double ground,double t,int custom){
        VectorCanvas c=new VectorCanvas(actual);c.save();c.translate(0,ground);
        for(BattleEngine.IcePatch ice:b.ice){round(c,ice.x-33,-3,66,10,5,"#97e7ff55");line(c,new double[][]{{ice.x-20,3},{ice.x+19,3}},"#7ad9f599",1);}
        for(BattleEngine.Effect e:b.effects){
            String color=energy(e.owner,custom);boolean warning=e.age<e.delay;double u=clamp((e.age-e.delay)/Math.max(.01,e.duration),0,1);
            c.save();c.globalAlpha=warning?.45:1-u*.7;
            switch(e.form){
                case BEAM:double end=e.direction>0?BattleEngine.WIDTH:0;line(c,new double[][]{{e.owner.x,-(e.owner.y+80)},{end,-(e.owner.y+80)}},color,warning?1:24);if(!warning)line(c,new double[][]{{e.owner.x,-(e.owner.y+80)},{end,-(e.owner.y+80)}},"#eaffff",6);break;
                case STRIKE:case GREEN_RAY:case THOR_SPARK:
                    ellipse(c,e.x,0,e.range,5,color+"33",color);
                    if(warning){line(c,new double[][]{{e.x,-360},{e.x,0}},color+"55",1);ring(c,e.x,-290,13,color,t*2);}
                    else{double[][]points=new double[9][2];points[0]=new double[]{e.x,-390};for(int i=1;i<9;i++)points[i]=new double[]{e.x+Math.sin(i*7.3+e.x)*12,-390+i*48};line(c,points,color,e.owner.skill==BattleEngine.Skill.DOOM?17:10);line(c,points,"#edfff5",2.5);}break;
                case WAVE:case BURST:double r=e.range*(.1+u),ry=e.form==BattleEngine.Form.WAVE?r*.17:r*.7;c.strokeStyle=color;c.lineWidth=5*(1-u)+1;c.beginPath();c.path.addOval((float)(e.x-r),(float)(-e.y-ry),(float)(e.x+r),(float)(-e.y+ry),Path.Direction.CW);c.stroke();break;
                case SLASH:for(int i=0;i<3;i++){c.beginPath();c.strokeStyle=color;c.lineWidth=3;c.arc(e.x,-e.y,35+i*9,-1.2+u,.6+u);c.stroke();}break;
                case PORTAL:case TIME:ring(c,e.x,-(e.y!=0?e.y:70),e.form==BattleEngine.Form.TIME?70:46,e.form==BattleEngine.Form.TIME?"#86f6b3":color,t*3);break;
                case SMOKE:for(int i=0;i<7;i++)ellipse(c,e.x+Math.sin(i*13)*32*u,-18-u*40-i*3,8+u*23,12+u*30,"#9cb1ce35");break;
                default:break;
            }c.restore();
        }c.restore();
    }
}
