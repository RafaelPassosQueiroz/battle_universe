package com.example.projetomarvel;
import static com.example.projetomarvel.VectorCanvas.clamp;

/** The web V3 pose equations, sampled from the Android simulation's action ages. */
final class FighterPose {
    double lean=-.025,bob,hip,head=.02,fa=-.55,fe=-1.1,ba=.14,be=-1.15,fl=-.18,fk=.18,bl=.14,bk=.1,foot,stretch=1,turn,cape;
    private static double smooth(double t){t=clamp(t,0,1);return t*t*(3-2*t);}
    private static double lerp(double a,double b,double t){return a+(b-a)*t;}
    static FighterPose sample(FighterVisual f,double t){
        String id=f.id;boolean move=Math.abs(f.vx)>10,back=f.vx*f.dir<0,attacking=f.attack>=0;
        double phase=(f.walk!=0?f.walk:t*7)*f.agility(),gait=Math.sin(phase),breath=Math.sin(t*(id.equals("hulk")?2:2.8));
        FighterPose p=new FighterPose();p.bob=breath*.8;p.cape=Math.sin(t*2.2)*5;
        if(id.equals("hulk")){p.fa=-.25;p.fe=-.32;p.ba=.25;p.be=-.3;p.lean=-.08;p.head=.08;}
        if(id.equals("spider")){p.lean=-.12;p.bob+=3;p.fl=-.35;p.fk=.55;p.bl=.28;p.bk=.3;p.fa=-.85;p.fe=-.8;}
        if(id.equals("scarlet")||id.equals("strange")){p.fa=-.8;p.fe=-1.1;p.ba=.1;p.be=-1.4;p.bob+=Math.sin(t*2.2)*1.5;}
        if(move&&f.y<1&&!attacking&&!f.specialActive&&!f.guard){
            double amount=back?.37:id.equals("hulk")?.46:Math.abs(f.vx)>250?.65:.55;
            p.fl=gait*amount-.1;p.bl=-gait*amount+.1;p.fk=Math.max(0,-gait)*.9+.08;p.bk=Math.max(0,gait)*.9+.08;
            p.fa=-gait*.45-.12;p.ba=gait*.45+.1;p.fe=-.5;p.be=-.5;
            p.bob=-Math.abs(Math.sin(phase))*(id.equals("hulk")?3.5:2.4);p.lean=back?.06:Math.abs(f.vx)>250?-.18:-.1;p.cape+=Math.min(1,Math.abs(f.vx)/220)*13;
        }
        if(f.y>1){p.lean=-.09;p.fl=-.65;p.fk=1;p.bl=.45;p.bk=.7;p.fa=-1.7;p.fe=-.5;p.ba=.6;p.be=-1.1;p.stretch=f.vy>0?1.025:.99;p.cape+=15;if(f.vy<0){p.fl=-.4;p.fk=.45;p.bl=.25;p.bk=.25;p.fa=-.7;}}
        if(f.land>0){double a=f.land/.16;p.bob+=7*a;p.fl-=.2*a;p.fk+=.65*a;p.bk+=.65*a;p.stretch=1-.06*a;}
        if(f.guard){p.lean=.12;p.bob+=3;p.fa=-1.2;p.fe=-1.4;p.ba=-.6;p.be=-1.6;p.fl=-.3;p.fk=.4;p.bl=.3;p.bk=.3;p.head=.1;}
        if(attacking){
            double u=clamp(f.age/Math.max(.01,f.duration),0,1),wind=Math.sin(Math.PI*clamp(u/.25,0,1)),hit=u<.45?smooth((u-.14)/.31):1-smooth((u-.45)/.55);
            if(f.attack==0){
                p.fa=lerp(.4,-1.65,hit);p.fe=lerp(-1.7,.13,hit);p.ba=-.4;p.be=-1.5;p.lean=-.16*hit+.07*wind;p.hip=-.10*hit;p.bob=-2*hit;p.head=.08*hit;p.fl=-.25*hit-.1;p.fk=.22;p.bl=.3*hit;
                if(id.equals("thor")){p.fa=lerp(-2.9,-1.25,hit);p.fe=-.25;}if(id.equals("wolverine")){p.fa=lerp(-2,-.8,hit);p.fe=-.6*hit;}
            }else if(f.attack==1){
                p.fl=lerp(-.8,-1.5,hit);p.fk=lerp(1.9,.05,hit);p.foot=-.12;p.bl=.1;p.bk=.15;p.lean=.30*hit;p.hip=.07*hit;p.fa=.4*hit;p.fe=-1.1;p.ba=-1.1;p.be=-1;p.bob=-3*hit;
                if(id.equals("spider")){p.lean=.5*hit;p.bl+=.3*hit;p.ba=-1.8;}
            }else{
                p.fa=lerp(-.7,-1.25,hit);p.fe=lerp(-1.9,.05,hit);p.ba=-.3;p.be=-1.2;p.lean=-.11*hit;p.head=-.04;p.fl=-.3;p.bl=.25;
                if(id.equals("hulk")){p.fa=-1.5*hit;p.ba=-1.3*hit;p.fe=-.7;p.be=-.6;p.lean=-.2*hit;}
                if(id.equals("scarlet")||id.equals("strange")){p.fa=-1.4*hit-.5;p.fe=-.55;p.ba=-1.1*hit;p.be=-.8;p.cape+=hit*10;}
            }
        }
        if(f.specialActive){
            double a=Math.sin(clamp(f.specialTime/(f.specialDuration>0?f.specialDuration:.8),0,1)*Math.PI);
            p.fa=-1.5;p.fe=-.1;p.ba=-.8;p.be=-1.2;p.lean=-.1;p.cape+=15;
            if(id.equals("widow")){p.bob=12*a;p.lean=-.35;p.fa=-1.3;p.fe=-.9;p.fl=-.7;p.fk=.85;}
            if(id.equals("scarlet")||id.equals("strange")){p.fa=-2;p.fe=.4;p.ba=-1.8;p.be=-.4;p.bob=-3*a;p.head=-.1;}
            if(id.equals("hulk")){p.fa=-2.8;p.ba=-2.5;p.fe=-.5;p.be=-.5;p.fl=-.6;p.fk=.8;p.lean=-.2;}
            if(id.equals("thanos")){p.fa=-2.5;p.fe=-.9;p.ba=.1;p.be=-.4;p.lean=.05;}
            if(id.equals("captain")){p.fa=-1.3;p.fe=-.9;p.ba=-1;p.be=-1.1;p.lean=.1;}
            if(id.equals("wolverine")){p.fa=-1.6;p.fe=-.1;p.ba=-1.7;p.be=-.2;p.lean=-.3;}
            if(id.equals("spider")){p.fl=-1.3;p.fk=.2;p.bl=.7;p.bk=1;p.lean=.3;p.fa=-2.3;p.fe=-.7;}
        }
        if(f.hit>0){double a=Math.sin(clamp(f.hit/.23,0,1)*Math.PI);p.lean=.28*a;p.head=.2*a;p.fa=-.25;p.fe=-1.7;p.ba=.5;p.bob+=2*a;p.bl=.4;p.fk=.35;}
        if(f.ko){double a=smooth(f.koAge/.65);p.turn=-1.48*a;p.bob=10*a;p.lean=.3*a;p.fl=-.3;p.fk=.8;p.bl=.6;p.bk=.3;p.fa=.8;p.fe=-.4;p.ba=-.6;p.be=-.6;}
        if(f.victory){p.fa=-2.9;p.fe=-.6;p.ba=.2;p.be=-.5;p.lean=-.05;p.head=-.08;}
        // The V2 expressive pass, followed by the approved V3 body constraints.
        p.cape*=1.22;p.head+=Math.sin(t*2.1)*.015;
        if(!attacking&&!f.specialActive&&!f.guard&&!f.ko&&f.hit<=0){
            if(move&&f.y==0){p.fl*=1.16;p.bl*=1.16;p.fk*=1.18;p.bk*=1.18;p.fa*=1.14;p.ba*=1.14;p.lean*=1.3;p.bob*=1.2;p.stretch=1-.035*Math.abs(Math.sin(f.walk*f.agility()));}
            else if(f.y==0){p.bob*=1.35;p.fa+=Math.sin(t*2.8)*.055;p.be+=Math.sin(t*2.8+.7)*.045;p.hip=Math.sin(t*1.8)*.025;}
        }
        if(f.y>1&&!f.specialActive){p.fl*=1.17;p.fk*=1.12;p.bl*=1.12;p.ba+=.16;p.head-=.055;p.stretch=f.vy>50?1.075:f.vy<0?.97:1.015;}
        if(f.land>0){double a=clamp(f.land/.16,0,1);p.stretch=1-.12*a;p.bob+=2*a;p.fa+=.16*a;}
        if(f.guard){p.lean*=1.3;p.fl-=.06;p.bk+=.08;p.head+=.06;p.stretch=.975;}
        if(attacking){double u=clamp(f.age/Math.max(.01,f.duration),0,1),impact=u<.45?smooth((u-.14)/.31):1-smooth((u-.45)/.55),wind=Math.sin(Math.PI*clamp(u/.25,0,1));
            if(f.attack==0){p.lean-=.08*impact;p.lean+=.04*wind;p.hip-=.10*impact;p.ba+=.15*impact;p.fa-=.10*impact;p.fe+=.08*impact;p.head+=.04*wind;p.stretch=1+.035*impact;}
            else if(f.attack==1){p.fl-=.13*impact;p.fk-=.06*impact;p.lean+=.12*impact;p.ba-=.20*impact;p.fa+=.16*impact;p.hip+=.10*impact;p.stretch=1+.045*impact;if(id.equals("spider")){p.bl+=.20*impact;p.lean+=.08*impact;}}
        }
        if(f.hit>0){double a=Math.sin(clamp(f.hit/.23,0,1)*Math.PI);p.lean+=.15*a;p.head+=.08*a;p.fa+=.18*a;p.stretch=1-.055*a;}
        if(f.victory){p.lean-=.04;p.head-=.05;p.fe-=.10;}
        p.lean=clamp(p.lean*.46,-.20,.24);p.hip=0;p.bob*=.65;p.head=clamp(p.head*.35,-.07,.08);p.stretch=clamp(p.stretch,.95,1.045);
        if(!attacking&&!f.specialActive&&!f.guard&&f.hit<=0&&!f.ko){if(move&&f.y<=0){p.fl*=.78;p.bl*=.78;p.fk*=.85;p.bk*=.85;}else if(f.y<=0){p.fl=-.10;p.fk=.12;p.bl=.10;p.bk=.10;}}
        return p;
    }
    void approach(FighterPose p,double dt){
        double k=1-Math.exp(-38*dt),h=1-Math.exp(-24*dt);
        lean=lerp(lean,p.lean,k);bob=lerp(bob,p.bob,k);hip=lerp(hip,p.hip,k);head=lerp(head,p.head,h);
        fa=lerp(fa,p.fa,k);fe=lerp(fe,p.fe,k);ba=lerp(ba,p.ba,k);be=lerp(be,p.be,k);fl=lerp(fl,p.fl,k);fk=lerp(fk,p.fk,k);bl=lerp(bl,p.bl,k);bk=lerp(bk,p.bk,k);foot=lerp(foot,p.foot,k);stretch=lerp(stretch,p.stretch,k);turn=lerp(turn,p.turn,k);cape=lerp(cape,p.cape,k);
    }
}
