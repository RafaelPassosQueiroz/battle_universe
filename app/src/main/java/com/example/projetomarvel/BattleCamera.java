package com.example.projetomarvel;

/** The approved web camera in a 1100-unit viewport over the shared world. */
final class BattleCamera {
    double x=1300,zoom=1.05,y,shake,clock;
    private double lastA,lastB;
    void reset(BattleEngine.Fighter a,BattleEngine.Fighter b){x=(a.x+b.x)/2;zoom=1.05;y=shake=0;lastA=a.x;lastB=b.x;}
    void update(double dt,BattleEngine.Fighter a,BattleEngine.Fighter b){
        if(dt<=0)return;clock+=dt;
        double left=Math.min(a.x,b.x)-105,right=Math.max(a.x,b.x)+105;
        double targetZoom=clamp(1100/(right-left+150),.40,1.18);
        zoom=approach(zoom,targetZoom,targetZoom<zoom?5.8:2,dt);
        double half=550/zoom,lead=clamp(((a.x-lastA)+(b.x-lastB))/dt*.12,-36,36);
        lastA=a.x;lastB=b.x;
        double target=(left+right)/2+lead,maxLeft=left+half-32/zoom,minRight=right-half+32/zoom;
        if(minRight<=maxLeft)target=clamp(target,minRight,maxLeft);
        target=clamp(target,Math.min(half,BattleEngine.WIDTH/2),Math.max(BattleEngine.WIDTH/2,BattleEngine.WIDTH-half));
        x=approach(x,target,3.8,dt);x=clamp(x,Math.min(half,BattleEngine.WIDTH/2),Math.max(BattleEngine.WIDTH/2,BattleEngine.WIDTH-half));
        y=approach(y,clamp(Math.max(a.y,b.y)-115,0,90)*.25,2,dt);shake=Math.max(0,shake-dt*15);
    }
    void impact(double amount){shake=Math.min(2.6,amount);}
    void apply(VectorCanvas c){c.translate(550+Math.sin(clock*91)*shake,426+Math.sin(clock*117)*shake*.45);c.scale(zoom,zoom);c.translate(-x,y);}
    void apply(android.graphics.Canvas c){c.translate((float)(550+Math.sin(clock*91)*shake),(float)(426+Math.sin(clock*117)*shake*.45));c.scale((float)zoom,(float)zoom);c.translate((float)-x,(float)y);}
    static double approach(double a,double b,double rate,double dt){return a+(b-a)*(1-Math.exp(-rate*dt));}
    static double clamp(double v,double a,double b){return Math.max(a,Math.min(b,v));}
}
