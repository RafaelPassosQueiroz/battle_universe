package com.example.projetomarvel;
import android.graphics.Canvas;
import java.util.HashMap;
import static com.example.projetomarvel.VectorCanvas.clamp;
/** Native transcription of the final V3 rig, costume paths, pivots and palette. */
final class NativeFighterArt {
    private static final VectorCanvas.Art A=new VectorCanvas.Art();
    static final class Body { static final double scale=1; static final double width=1; static final double shoulderX=19; static final double shoulderY=-127; static final double upperArm=29; static final double forearm=28; static final double armWidth=16; static final double cuffWidth=18; static final double hipX=11; static final double hipY=-72; static final double thigh=31; static final double shin=32; static final double legWidth=17; static final double torsoX=-23; static final double torsoY=-137; static final double torsoWidth=46; static final double torsoHeight=67; static final double headX=-20; static final double headY=-190; static final double headPivotY=-161; static final double headWidth=40; static final double headHeight=46; }
    static final class Palette {
        String accent,armShadow,boot,cape,deep,energy,eye,face,glove,gold,hair,main,shadow,skin;
        boolean bareArms,shortHair;
        Palette copy(){Palette p=new Palette();p.accent=accent;p.armShadow=armShadow;p.boot=boot;p.cape=cape;p.deep=deep;p.energy=energy;p.eye=eye;p.face=face;p.glove=glove;p.gold=gold;p.hair=hair;p.main=main;p.shadow=shadow;p.skin=skin; p.bareArms=bareArms;p.shortHair=shortHair;return p;}
    }
    static final HashMap<String,Palette> PALETTES=new HashMap<>();
    static {         { Palette p=new Palette();p.main="#315f99";p.shadow="#23436f";p.deep="#142b4e";p.accent="#c34755";p.gold="#eee5ca";p.eye="#f3f7ed";p.energy="#a8d9ff";PALETTES.put("captain",p); }
        { Palette p=new Palette();p.main="#c33f4c";p.shadow="#8d2d41";p.deep="#54263b";p.accent="#e5b957";p.gold="#e5b957";p.eye="#c0faff";p.energy="#8be9ff";PALETTES.put("iron",p); }
        { Palette p=new Palette();p.main="#d43249";p.shadow="#9c2840";p.deep="#243b73";p.accent="#2a58a1";p.glove="#d43249";p.boot="#ce3046";p.eye="#f5fbff";p.energy="#e6f6ff";PALETTES.put("spider",p); }
        { Palette p=new Palette();p.main="#e4bb36";p.shadow="#bf8b2e";p.deep="#1e3154";p.accent="#294e85";p.glove="#294e85";p.boot="#294e85";p.face="#dbad83";p.skin="#dbad83";p.armShadow="#b9866b";p.bareArms=true;p.eye="#fff8dd";p.energy="#fff0a1";PALETTES.put("wolverine",p); }
        { Palette p=new Palette();p.main="#34465d";p.shadow="#23344b";p.deep="#172a41";p.accent="#b9c9d3";p.face="#deb28e";p.skin="#deb28e";p.armShadow="#b68973";p.bareArms=true;p.hair="#dfb86a";p.cape="#b63849";p.eye="#25374b";p.energy="#a8ddff";PALETTES.put("thor",p); }
        { Palette p=new Palette();p.main="#65b57d";p.shadow="#40935f";p.deep="#28523e";p.accent="#7851a0";p.glove="#65b57d";p.boot="#65b57d";p.face="#65b57d";p.hair="#284b38";p.eye="#f5f4ce";p.energy="#9cebb1";PALETTES.put("hulk",p); }
        { Palette p=new Palette();p.main="#2b304d";p.shadow="#1c233c";p.deep="#141d34";p.accent="#b7a2dc";p.eye="#f4edff";p.energy="#c2a5ff";PALETTES.put("panther",p); }
        { Palette p=new Palette();p.main="#3c5b89";p.shadow="#2a4168";p.deep="#202f49";p.accent="#dab96c";p.face="#dcb28f";p.glove="#dcb28f";p.hair="#262c3a";p.cape="#b33a48";p.eye="#343240";p.energy="#ffbe64";PALETTES.put("strange",p); }
        { Palette p=new Palette();p.main="#28374c";p.shadow="#1a273c";p.deep="#111e32";p.accent="#c74658";p.face="#e2b291";p.hair="#b14e3f";p.eye="#30364a";p.energy="#77d4ff";PALETTES.put("widow",p); }
        { Palette p=new Palette();p.main="#b13e5b";p.shadow="#7a2d49";p.deep="#51263e";p.accent="#f17b9d";p.face="#e0b08d";p.glove="#e984a6";p.hair="#71333e";p.cape="#8c2a48";p.eye="#6d2549";p.energy="#ff6286";PALETTES.put("scarlet",p); }
        { Palette p=new Palette();p.main="#31518a";p.shadow="#243c6a";p.deep="#192e53";p.accent="#e0bd62";p.face="#dfb18d";p.glove="#c8394f";p.boot="#c8394f";p.hair="#e4be68";p.eye="#303c54";p.energy="#ffe7a0";PALETTES.put("marvel",p); }
        { Palette p=new Palette();p.main="#39835a";p.shadow="#286448";p.deep="#214b3c";p.accent="#c7d2c4";p.face="#aebfb9";p.glove="#9bb5a9";p.boot="#708f82";p.cape="#2e714c";p.eye="#e4ffe4";p.energy="#89f4ad";PALETTES.put("doom",p); }
        { Palette p=new Palette();p.main="#354d62";p.shadow="#253b50";p.deep="#514369";p.accent="#d8b560";p.glove="#d8b560";p.boot="#d8b560";p.face="#a57cae";p.eye="#d3f5ff";p.energy="#d6aaff";PALETTES.put("thanos",p); } }
    private static void panel(VectorCanvas c,double x,double y,double w,double h,double r,String color){panel(c,x,y,w,h,r,color,"#14243a");}
    private static void panel(VectorCanvas c,double x,double y,double w,double h,double r,String color,String outline){A.round(c,x,y,w,h,r,color);if(outline!=null){c.strokeStyle=outline;c.lineWidth=1.9;c.stroke();}}

    private static void limb(VectorCanvas c,double length,double width,String color,String shadow) {

    
    panel(c,-width/2,-3,width,length+7,5,color);
    A.round(c,width*.15,1,width*.25,length,2,shadow);
    A.line(c,new double[][]{{-width*.25,3},{-width*.25,10}},"#ffffff20",1.4);
  
    }

    private static void shield(VectorCanvas c,double t) {

    c.save(); c.rotate(-.12);
    A.ellipse(c,0,0,24,27,"#c64b59","#14243a");
    A.ellipse(c,0,0,18.5,21.5,"#eee9d9");
    A.ellipse(c,0,0,13,16,"#315e96");
    A.star(c,0,0,12,"#f6f1df");
    A.line(c,new double[][]{{-15,-15},{-5,-21}},"#ffffff6b",2);
    c.restore();
  
    }

    private static void hand(VectorCanvas c,Palette palette,String id,boolean front,FighterVisual f) {

    String glove=id.equals("captain")?palette.accent:(palette.glove!=null?palette.glove:palette.main);
    panel(c,-9,-3,18,18,6,glove);
    A.line(c,new double[][]{{-5,2},{5,2}},id.equals("captain")?"#e884852e":"#e4b35f77",1.3);
    if(id.equals("iron")){
      A.ellipse(c,1,5,4,4,palette.energy,"#376171");
      A.ellipse(c,1,5,2,2,"#efffff");
    }
    if(id.equals("captain")&&front&&!f.shieldAway){c.translate(5,2);shield(c,f.clock);}
    if(id.equals("wolverine")||id.equals("panther")){
      for(int i=-1;i<=1;i++)A.line(c,new double[][]{{5,3+i*4},{id.equals("wolverine")?31:16,-2+i*4}},id.equals("wolverine")?"#ecf3f7":palette.accent,id.equals("wolverine")?2:1.4);
    }
    if(id.equals("thor")&&front&&!f.hammerAway){
      panel(c,-2,-16,5,33,1,"#a9814f");
      panel(c,-16,-34,30,20,4,"#bfccd6");
      A.line(c,new double[][]{{-12,-29},{9,-29}},"#f6efd9",2);
    }
    if(id.equals("widow")){
      A.line(c,new double[][]{{-6,-1},{6,-1}},"#8bd8f4",2);
      if(f.armed)A.ring(c,0,4,12,palette.energy,f.clock);
      if(front&&f.attack==2)A.path(c,new double[][]{{-2,-3},{21,-3},{23,3},{6,5},{3,15},{-3,14}},"#172638","#a2b6bf",1);
    }
    if((id.equals("strange")||id.equals("scarlet"))&&front){
      c.save();c.globalAlpha*=f.attack>=0||f.specialActive?1:.45;
      A.ring(c,1,4,id.equals("strange")?16:11,palette.energy,f.clock*2);
      if(id.equals("scarlet"))A.ellipse(c,1,4,4,4,"#ffc4d8");c.restore();
    }
    if(id.equals("doom")&&(f.attack==2||f.specialActive))A.ring(c,1,4,11,palette.energy,f.clock);
    if(id.equals("thanos")&&front){
      panel(c,-9,-4,18,21,5,palette.accent);
      String[] gems={"#86edba","#ef6e89","#88c8ff","#ffe779","#b594f5","#f5a564"};
      for(int i=0;i<gems.length;i++)A.ellipse(c,-5+(i%3)*5,-1+Math.floor(i/3)*6,1.7,2,gems[i]);
    }
  
    }

    private static void arm(VectorCanvas c,Palette palette,String id,double x,double y,double upper,double elbow,boolean front,FighterVisual f) {

    c.save();c.translate(x,y);c.rotate(upper);
    limb(c,Body.upperArm,Body.armWidth,palette.bareArms?palette.skin:palette.main,palette.bareArms?palette.armShadow:palette.shadow);
    
    c.translate(0,Body.upperArm);c.rotate(elbow);
    limb(c,Body.forearm,Body.cuffWidth,palette.bareArms?palette.skin:palette.main,palette.bareArms?palette.armShadow:palette.shadow);
    if(id.equals("iron")){
      panel(c,-6,5,12,17,3,palette.accent,null);
      A.line(c,new double[][]{{-4,9},{4,9}},"#fff2c64d",1.3);
    }else if(id.equals("captain")){
      panel(c,-8,12,16,15,3,palette.accent,null);
      A.line(c,new double[][]{{-6,13},{6,13}},"#e77e823d",1.3);
    }else if(id.equals("widow")){
      panel(c,-8,15,16,10,2,palette.deep,null);A.line(c,new double[][]{{-6,19},{6,19}},palette.energy,2);
    }else if(id.equals("wolverine")||id.equals("marvel")){
      panel(c,-8,12,16,15,3,palette.glove,null);
    }else if(id.equals("panther")){
      A.line(c,new double[][]{{-6,17},{6,17}},palette.accent,1.5);
    }else if(id.equals("thanos")){
      panel(c,-8,13,16,14,3,palette.accent,null);
    }
    c.translate(0,Body.forearm);hand(c,palette,id,front,f);c.restore();
  
    }

    private static void leg(VectorCanvas c,Palette palette,String id,double x,double y,double upper,double knee,double foot) {

    c.save();c.translate(x,y);c.rotate(upper);
    String pants=id.equals("spider")?palette.accent:id.equals("hulk")?palette.accent:palette.main;
    limb(c,Body.thigh,Body.legWidth,pants,id.equals("hulk")?"#584178":palette.shadow);
    c.translate(0,Body.thigh);c.rotate(knee);
    limb(c,Body.shin,Body.legWidth,palette.main,palette.shadow);
    
    String boot=id.equals("captain")?palette.accent:(palette.boot!=null?palette.boot:palette.main);
    panel(c,-9,11,18,23,5,boot);
    if(id.equals("iron"))panel(c,-5,15,10,13,2,palette.accent,null);
    c.translate(0,Body.shin);c.rotate(foot);
    panel(c,-9,-3,26,10,4,boot);
    A.line(c,new double[][]{{-6,5},{14,5}},palette.deep,1.8);
    c.restore();
  
    }

    private static void torso(VectorCanvas c,Palette palette,String id) {

    
    panel(c,Body.torsoX,Body.torsoY,Body.torsoWidth,Body.torsoHeight,9,palette.main);
    A.round(c,13,-132,7,57,3,palette.shadow);
    panel(c,-18,-145,36,15,5,palette.main);
    A.line(c,new double[][]{{-14,-140},{14,-140}},palette.shadow,2);
    
    panel(c,-27,-137,17,17,5,palette.main);
    panel(c,10,-137,17,17,5,palette.main);
    if(id.equals("captain")){
      A.star(c,0,-115,13,"#f3ecd8");
      panel(c,-19,-96,38,22,3,"#eee6d3",null);
      for(double x:new double[]{-13,0,13})A.round(c,x-3.5,-96,7,22,0,palette.accent);
      panel(c,-21,-81,42,9,2,palette.shadow,null);
      panel(c,-5,-82,10,10,2,"#e1c88a",null);
    }else if(id.equals("iron")){
      
      A.path(c,new double[][]{{-17,-130},{-5,-130},{-5,-122},{-17,-122}},palette.accent,null);
      A.path(c,new double[][]{{5,-130},{17,-130},{17,-122},{5,-122}},palette.accent,null);
      A.ellipse(c,0,-114,11,11,"#4fc4da","#273d57");
      A.ellipse(c,0,-114,7.5,7.5,"#c5faff");
      A.ellipse(c,-1,-116,3,3,"#f2ffff");
      panel(c,-15,-96,30,16,3,palette.shadow,null);
      A.line(c,new double[][]{{-11,-91},{11,-91}},"#e5b95799",1.4);
      panel(c,-19,-79,38,7,2,palette.main,null);
    }else if(id.equals("spider")){
      for(double side:new double[]{-1,1})panel(c,side<0?-22:13,-124,9,46,2,palette.accent,null);
      for(double y:new double[]{-132,-115,-98})A.line(c,new double[][]{{-16,y},{0,y+4},{16,y}},"#8c233c",1);
      A.ellipse(c,0,-115,4,7,"#17253b");
      for(int i=0;i<4;i++)for(double side:new double[]{-1,1})A.line(c,new double[][]{{side*2,-119+i*3},{side*10,-125+i*6},{side*13,-125+i*7}},"#17253b",1.5);
    }else if(id.equals("wolverine")){
      for(double side:new double[]{-1,1})for(double y:new double[]{-128,-108})A.path(c,new double[][]{{side*22,y},{side*8,y+7},{side*22,y+13}},palette.deep,null);
      panel(c,-21,-82,42,10,2,palette.accent,null);A.ellipse(c,0,-77,5,5,"#c3444a");
    }else if(id.equals("thor")){
      for(double y:new double[]{-126,-107,-88})for(double x:new double[]{-12,12})A.ellipse(c,x,y,6.5,6.5,palette.accent,palette.deep);
      panel(c,-21,-79,42,7,2,"#d7b967",null);
    }else if(id.equals("hulk")){
      
      panel(c,-23,-88,46,18,2,palette.accent,null);
      A.path(c,new double[][]{{-23,-71},{-16,-66},{-10,-71},{-2,-67},{6,-71},{15,-67},{23,-71}},palette.accent,null);
    }else if(id.equals("panther")){
      for(int i=-2;i<=2;i++)A.path(c,new double[][]{{i*7,-138},{i*6,-127-Math.abs(i)*2},{i*7+3,-138}},palette.accent,null);
      for(double side:new double[]{-1,1})A.line(c,new double[][]{{side*18,-122},{side*10,-108},{side*13,-87}},"#806baf",1.2);
      A.path(c,new double[][]{{-6,-115},{0,-122},{6,-115},{0,-108}},null,"#9c80c2",1);
    }else if(id.equals("strange")){
      A.path(c,new double[][]{{-20,-134},{0,-105},{20,-134},{13,-110},{-13,-110}},"#5475a1",null);
      A.line(c,new double[][]{{-13,-141},{-3,-121},{11,-141}},palette.accent,1.4);
      A.ellipse(c,0,-119,8,9,palette.accent,palette.deep);A.ellipse(c,0,-119,3.5,4,"#83db9c");
      panel(c,-21,-86,42,10,2,"#9e3b4a",null);
    }else if(id.equals("widow")){
      A.line(c,new double[][]{{0,-134},{0,-94}},"#91b1c252",1);
      A.path(c,new double[][]{{-8,-117},{8,-117},{-8,-103},{8,-103}},null,palette.accent,2.3);
      panel(c,-21,-84,42,9,2,palette.accent,null);panel(c,-5,-85,10,11,2,palette.deep,null);
    }else if(id.equals("scarlet")){
      A.path(c,new double[][]{{0,-135},{14,-115},{0,-92},{-14,-115}},null,palette.accent,2.8);
      A.path(c,new double[][]{{0,-126},{7,-115},{0,-103},{-7,-115}},"#86314b",null);
      panel(c,-21,-83,42,8,2,palette.deep,null);
    }else if(id.equals("marvel")){
      A.path(c,new double[][]{{-23,-136},{0,-121},{23,-136},{23,-127},{0,-112},{-23,-127}},palette.accent,null);
      A.star(c,0,-120,13,"#f5d680",8,.43);
      panel(c,-21,-84,42,9,2,"#c8394f",null);
      for(double x:new double[]{-27,10})panel(c,x,-137,17,8,2,"#c8394f",null);
    }else if(id.equals("doom")){
      A.path(c,new double[][]{{0,-129},{13,-121},{13,-105},{0,-97},{-13,-105},{-13,-121}},null,"#9ac9a5",1.6);
      A.line(c,new double[][]{{-7,-112},{7,-112}},"#a2d9b5",1.5);
      panel(c,-21,-84,42,10,2,"#7b9686",null);panel(c,-5,-85,10,12,2,"#d8bd69",null);
    }else if(id.equals("thanos")){
      A.path(c,new double[][]{{-24,-137},{-13,-133},{-8,-107},{8,-107},{13,-133},{24,-137},{20,-122},{15,-119},{11,-91},{-11,-91},{-15,-119},{-20,-122}},palette.accent,null);
      panel(c,-22,-83,44,11,2,palette.accent,null);
    }
  
    }

    private static void head(VectorCanvas c,Palette palette,String id,FighterPose p,double t) {

    c.save();c.translate(0,Body.headPivotY);c.rotate(p.head);
    if(palette.hair!=null&&!palette.shortHair&&!id.equals("hulk")&&!id.equals("strange")){
      double sway=Math.sin(t*2.1)*2+p.cape*.10;
      A.path(c,new double[][]{{-19,-25},{-26,-12},{-25,20},{-17+sway,35},{-4+sway,28},{3,4},{16,-14},{8,-29}},palette.hair,palette.deep,1.3);
    }
    if(id.equals("doom"))A.path(c,new double[][]{{-27,18},{-27,-9},{-18,-33},{0,-42},{21,-31},{28,-6},{26,21}},palette.cape,palette.deep,1.7);
    
    panel(c,Body.headX,Body.headY-Body.headPivotY,Body.headWidth,Body.headHeight,11,(palette.face!=null?palette.face:palette.main));
    A.round(c,13,-23,5,30,3,palette.face!=null?palette.face:palette.shadow);
    if(id.equals("captain")){
      
      panel(c,-12,7,24,9,4,"#d8ad87",null);
      c.fillStyle="#f3efd9";c.font="bold 14px Arial";c.textAlign="center";c.fillText("A",0,-12);c.textAlign="left";
      A.path(c,new double[][]{{-13,-6},{-3,-3},{-5,2},{-12,1}},palette.eye,null);
      A.path(c,new double[][]{{4,-3},{15,-6},{14,1},{6,2}},palette.eye,null);
      A.line(c,new double[][]{{-13,-22},{-8,-24}},"#9fc2e866",1.5);
    }else if(id.equals("iron")){
      panel(c,-14,-22,28,32,7,palette.accent,null);
      A.path(c,new double[][]{{-14,-20},{-5,-20},{-5,-15},{5,-15},{5,-20},{14,-20},{12,-25},{-12,-25}},palette.main,null);
      A.path(c,new double[][]{{-11,-7},{-2,-5},{-2,-1},{-10,-2}},palette.eye,null);
      A.path(c,new double[][]{{3,-5},{12,-7},{11,-2},{3,-1}},palette.eye,null);
      A.line(c,new double[][]{{-17,-19},{-17,1}},"#ee8a7b44",1.4);
    }else{
      if(id.equals("strange")||palette.shortHair){
        A.path(c,new double[][]{{-20,-14},{-20,-25},{-9,-31},{8,-30},{20,-22},{20,-14},{10,-18},{1,-16},{-10,-20},{-17,-13}},palette.hair,palette.deep,1.1);
      }else if(palette.hair!=null&&!id.equals("hulk")){
        A.path(c,new double[][]{{-20,-9},{-20,-27},{-6,-34},{10,-30},{20,-17},{6,-20},{-5,-12},{-15,-4}},palette.hair,palette.deep,1.1);
      }
      if(id.equals("spider")){
        for(double y:new double[]{-20,-8,5})A.line(c,new double[][]{{-18,y},{0,y+5},{18,y}},"#89223a",1);
        A.line(c,new double[][]{{0,-27},{0,14}},"#89223a",1);
        A.path(c,new double[][]{{-15,-11},{-3,-6},{-6,6},{-12,3}},palette.eye,palette.deep,1.5);
        A.path(c,new double[][]{{4,-6},{17,-12},{15,3},{7,6}},palette.eye,palette.deep,1.5);
      }else{
        A.path(c,new double[][]{{-13,-6},{-3,-3},{-5,2},{-12,1}},palette.eye,null);
        A.path(c,new double[][]{{4,-3},{15,-6},{14,1},{6,2}},palette.eye,null);
      }
      if(id.equals("wolverine")){
        A.path(c,new double[][]{{-19,5},{-24,-45},{-6,-27},{5,-27},{24,-45},{19,5},{8,-7},{0,-3},{-8,-7}},palette.deep,palette.deep,1);
        A.path(c,new double[][]{{-14,-9},{-3,-5},{-6,1},{-12,0}},palette.eye,null);
        A.path(c,new double[][]{{4,-5},{16,-10},{14,0},{6,1}},palette.eye,null);
        A.path(c,new double[][]{{-5,-26},{5,-26},{0,-14}},palette.main,null);
      }
      if(id.equals("panther")){
        A.path(c,new double[][]{{-19,-16},{-20,-42},{-6,-28}},palette.main,palette.deep,1.6);
        A.path(c,new double[][]{{6,-28},{20,-42},{19,-15}},palette.main,palette.deep,1.6);
        A.line(c,new double[][]{{0,-23},{0,-13}},"#9d87c6",1.3);
        A.line(c,new double[][]{{-13,7},{-7,12},{0,14},{7,12},{13,7}},"#8c78b3",1);
      }
      if(id.equals("thor")){
        panel(c,-21,-32,42,16,5,"#a9bcc9");A.line(c,new double[][]{{-17,-17},{17,-17}},"#eed18d",2);
        for(double side:new double[]{-1,1})A.path(c,new double[][]{{side*16,-26},{side*30,-39},{side*27,-19},{side*18,-11}},"#d2dce0",palette.deep,1.1);
      }
      if(id.equals("strange")||palette.shortHair){
        A.path(c,new double[][]{{-19,-17},{-16,-21},{-14,-12},{-17,-8}},"#d4d7d2",null);
        A.path(c,new double[][]{{14,-20},{18,-16},{17,-8},{14,-12}},"#d4d7d2",null);
      }
      if(id.equals("scarlet"))A.path(c,new double[][]{{-20,-13},{-21,-43},{-4,-27},{0,-24},{18,-43},{20,-13},{7,-21},{-5,-21}},"#c04465",palette.deep,1.2);
      if(id.equals("hulk"))A.path(c,new double[][]{{-20,-17},{-21,-27},{-9,-32},{10,-30},{20,-23},{20,-17},{11,-21},{5,-18},{-6,-22}},palette.hair,null);
      if(id.equals("doom")){
        panel(c,-16,-24,32,36,6,palette.face,null);
        A.line(c,new double[][]{{-11,-7},{-3,-7}},"#244c3c",2);A.line(c,new double[][]{{4,-7},{12,-7}},"#244c3c",2);
        for(double x:new double[]{-12,12})A.ellipse(c,x,7,1.2,1.2,"#60867c");
      }
      if(id.equals("thanos")){
        A.path(c,new double[][]{{-20,-15},{-19,-28},{14,-28},{21,-15},{14,-17},{10,-22},{-13,-22},{-15,-15}},palette.accent,palette.deep,1);
        for(double x:new double[]{-7,0,7})A.line(c,new double[][]{{x,9},{x,14}},"#765886",1);
      }
    }
    c.restore();
  
    }

    private static void cape(VectorCanvas c,Palette palette,FighterPose p,double t) {

    c.save();c.translate(-9,-137);double sway=p.cape*.55+Math.sin(t*1.8)*2;
    c.beginPath();c.moveTo(-15,-2);c.bezierCurveTo(-34-sway,28,-31-sway,86,-49-sway,131);
    c.quadraticCurveTo(-12,120,17,31);c.lineTo(19,0);c.closePath();
    c.fillStyle=palette.cape;c.fill();c.strokeStyle=palette.deep;c.lineWidth=1.7;c.stroke();
    A.line(c,new double[][]{{-14,6},{-21-sway*.5,59},{-36-sway*.7,117}},"#ffffff17",2);c.restore();
  
    }

    static void draw(Canvas actual,FighterVisual f,double x,double y,double scale,double t){
        VectorCanvas c=new VectorCanvas(actual);String id=f.id;f.clock=t;
        Palette palette=f.palette!=null?f.palette:PALETTES.get(id);if(palette==null)return;
        FighterPose target=FighterPose.sample(f,t);
        double dt=clamp(t-f.visualTime,0,.05);f.visualTime=t;
        if(f.pose==null)f.pose=target;else f.pose.approach(target,dt);
        FighterPose p=f.pose;
        double wide=f.width(),tall=f.height();
        A.ellipse(c,x,y+3,30*scale*wide,5.5*scale,"#06162566");
        c.save();c.translate(x,y);c.scale(scale*f.dir*wide,scale*tall);c.globalAlpha=f.alpha;
        if(f.dusted)c.globalAlpha*=1-smooth(f.koAge/1.3);
        c.translate(0,p.bob);c.rotate(p.turn);c.translate(0,-74);c.rotate(p.lean);c.scale(1,p.stretch);c.translate(0,74);
        if(palette.cape!=null)cape(c,palette,p,t);
        c.save();c.globalAlpha*=.86;
        arm(c,palette,id,-Body.shoulderX,Body.shoulderY,p.ba,p.be,false,f);
        leg(c,palette,id,-Body.hipX,Body.hipY,p.bl,p.bk,0);c.restore();
        leg(c,palette,id,Body.hipX,Body.hipY,p.fl,p.fk,p.foot);
        torso(c,palette,id);
        if(f.custom)customCostume(c,f,palette);
        head(c,palette,id,p,t);
        if(f.robot){c.save();c.translate(0,Body.headPivotY);c.rotate(p.head);A.line(c,new double[][]{{-11,7},{11,7}},palette.deep,1.5);A.line(c,new double[][]{{-8,11},{8,11}},palette.shadow,1.4);for(double side:new double[]{-1,1})A.ellipse(c,side*14,10,1.2,1.2,palette.deep);c.restore();}
        arm(c,palette,id,Body.shoulderX,Body.shoulderY,p.fa,p.fe,true,f);
        if(palette.cape!=null){A.ellipse(c,-17,-134,3.5,3.5,id.equals("doom")?"#dfbf68":palette.accent);A.ellipse(c,17,-134,3.5,3.5,id.equals("doom")?"#dfbf68":palette.accent);}
        if(f.hit>.12){c.globalAlpha=.16;A.round(c,-19,-134,38,61,8,"#fff2ce");}
        c.restore();
        if(f.shield)A.ring(c,x,y-90*scale,66*scale,palette.energy+"88",t*.5);
        if(f.distortion>0){c.save();c.globalAlpha=.6;A.ring(c,x,y-90*scale,49*scale,"#f66294",t*2);c.restore();}
        if(f.electric>0)A.line(c,new double[][]{{x-18*scale,y-137*scale},{x+8*scale,y-110*scale},{x-5*scale,y-97*scale},{x+23*scale,y-65*scale}},"#b7f3ff",1.5);
        if(f.guard){c.save();c.globalAlpha=.48;A.line(c,new double[][]{{x+f.dir*38*scale,y-135*scale},{x+f.dir*47*scale,y-97*scale},{x+f.dir*36*scale,y-60*scale}},palette.energy,2*scale);c.restore();}
        if(id.equals("iron")&&f.y>3&&!f.ko){double length=9+Math.sin(t*33)*3+(f.vy>0?6:0);for(double side:new double[]{-1,1})A.path(c,new double[][]{{x+side*11*scale-3*scale,y+2*scale},{x+side*11*scale+4*scale,y+2*scale},{x+side*11*scale,y+length*scale}},"#9beaff99",null);}
    }
    private static void customCostume(VectorCanvas c,FighterVisual f,Palette p){
        A.round(c,-21,-84,42,9,2,p.accent);A.round(c,-5,-85,10,11,2,p.deep);
        if(f.robot){A.round(c,-14,-125,28,25,4,p.deep);A.ellipse(c,0,-113,6,6,p.energy);A.line(c,new double[][]{{-9,-106},{9,-106}},p.accent,1.4);}
        else{A.path(c,new double[][]{{0,-129},{11,-117},{0,-104},{-11,-117}},null,p.accent,2);}
        if(f.power.equals("Ninja")){A.line(c,new double[][]{{-17,-137},{-29,-77}},"#d0dde8",4);A.line(c,new double[][]{{-22,-132},{-34,-139}},p.accent,4);}
    }
    static double smooth(double t){t=clamp(t,0,1);return t*t*(3-2*t);}
}
