package com.example.projetomarvel;
import java.util.ArrayList;
/** Native path-for-path Bifrost scene, with the approved parallax coefficients. */
final class AsgardScene {
 private static final VectorCanvas.Art A=new VectorCanvas.Art();
 private static final class Tower {double x,h,w;int layer;boolean crown;Tower(double x,int layer,double h,double w,boolean crown){this.x=x;this.layer=layer;this.h=h;this.w=w;this.crown=crown;}}
 private static final class Star {double x,y,r,phase;Star(double x,double y,double r,double phase){this.x=x;this.y=y;this.r=r;this.phase=phase;}}
 private final ArrayList<Tower> towers=new ArrayList<>();private final ArrayList<Star> stars=new ArrayList<>();private long seed=20261029;
 private double random(){seed=(seed*1664525+1013904223)&0xffffffffL;return seed/4294967296.0;}
 AsgardScene(){for(int layer=0;layer<2;layer++)for(double x=-1100;x<5800;x+=105+random()*90)towers.add(new Tower(x,layer,48+random()*100,22+random()*28,random()>.48));for(int i=0;i<35;i++)stars.add(new Star(random()*1600,55+random()*180,.5+random()*.6,random()*6));}

 void background(VectorCanvas c,BattleCamera cam,double t){

      VectorCanvas.Gradient sky = c.createLinearGradient(0, 0, 0, 460);
      sky.addColorStop(0, "#142e46");
      sky.addColorStop(.45, "#476275");
      sky.addColorStop(.79, "#8a8e89");
      sky.addColorStop(1, "#bbb092");
      c.fillStyle = sky; c.fillRect(0, 0, 1100, 620);

      VectorCanvas.Gradient halo = c.createRadialGradient(600, 227, 20, 600, 227, 340);
      halo.addColorStop(0, "#f3d89b28"); halo.addColorStop(1, "#eed5a000");
      c.fillStyle = halo; c.fillRect(170, 0, 860, 470);
      A.ellipse(c, 887 - cam.x * .015, 119, 47, 47, "#d6dedb16");
      A.ellipse(c, 876 - cam.x * .015, 109, 47, 47, "#3e5b6d");
      for (Star star:stars) {
        A.ellipse(c, star.x - cam.x * .022, star.y, star.r, star.r,
          "rgba(235,227,197," + (.10 + Math.sin(t * .65 + star.phase) * .035) + ")");
      }

      
      for (int i = 0; i < 4; i++) {
        double y = 122 + i * 50, drift = Math.sin(t * .06 + i) * 24;
        c.beginPath(); c.moveTo(-80, y);
        c.bezierCurveTo(180 + drift, y - 38, 480 + drift, y + 41, 1180, y - 30);
        c.strokeStyle = i % 2 != 0 ? "#d6ccb80b" : "#162e4110";
        c.lineWidth = 25 + i * 9; c.stroke();
      }

      double z = .75 + cam.zoom * .25;
      c.save(); c.translate(550 - cam.x * .15 * z, 356); c.scale(z, z);
      A.path(c, new double[][]{{-1300, 28},{-1300, -8},{-1020, -30},{-740, -14},{-430, -37},{-100, -18},{220, -40},{540, -16},{910, -35},{1300, -8},{5300, -15},{5300, 160},{-1300, 160}}, "#365465", null);
      for (Tower tower:towers) if (tower.layer == 0 && Math.abs(550+z*(tower.x-cam.x*.15))<750) {
        spire(c, tower.x, -12, tower.w * .68, tower.h * .78, "#7c8278", "#536d74", tower.crown);
      }
      for(double x:new double[]{-210, 560, 1100, 1810}) {
        VectorCanvas.Gradient mist = c.createLinearGradient(x, 0, x, 170);
        mist.addColorStop(0, "#b7d9d128"); mist.addColorStop(1, "#aecfc900");
        c.fillStyle = mist; c.fillRect(x, 0, 13, 170);
        A.line(c, new double[][]{{x + 4, (t * 16 + x) % 80},{x + 4, 110}}, "#d2e6dc13", 1);
      }
      c.restore();

      c.save(); c.translate(550 - cam.x * .38 * z, 387); c.scale(z, z);
      for (Tower tower:towers) if (tower.layer == 1 && Math.abs(550+z*(tower.x-cam.x*.38))<750) {
        spire(c, tower.x, -7, tower.w, tower.h, "#968566", "#5f6d6b", tower.crown);
      }
      palace(c, 525, -8);
      for (int x = -900; x < 5000; x += 175) {
        A.path(c, new double[][]{{x, -27},{x + 160, -27},{x + 170, 0},{x - 8, 0}}, "#717966", null);
        A.line(c, new double[][]{{x, -28},{x + 158, -28}}, "#c3ac7944", 2);
        for (int i = 0; i < 7; i++) arch(c, x + 9 + i * 21, -23, 12, 22, "#435c63", "#b09b6b55");
      }
      c.restore();

      c.save(); c.translate(550 - cam.x * .70 * z, 406); c.scale(z, z);
      A.line(c, new double[][]{{-1500, -3},{5500, -3}}, "#c8ac714d", 5);
      for (int x = -1400; x < 5500; x += 154) {
        A.line(c, new double[][]{{x, -4},{x + 9, -22},{x + 17, -4}}, "#405663", 5);
        A.line(c, new double[][]{{x + 8, -21},{x + 8, -29}}, "#b59b6b66", 2);
      }
      c.restore();

      VectorCanvas.Gradient haze = c.createLinearGradient(0, 290, 0, 431);
      haze.addColorStop(0, "#c8cbb600"); haze.addColorStop(1, "#d8c89a1c");
      c.fillStyle = haze; c.fillRect(0, 290, 1100, 141);
    
 }

 void spire(VectorCanvas c,double x,double base,double w,double h,String light,String dark,boolean crown){

      c.save(); c.translate(x, base);
      A.path(c, new double[][]{{-w * .6, 0},{-w * .55, -h * .55},{-w * .32, -h * .72},{-w * .2, -h * .9},{0, -h - 18},{w * .2, -h * .9},{w * .32, -h * .72},{w * .55, -h * .55},{w * .6, 0}}, A.gradient(c, -w, 0, w * 2, 0, dark, light), null);
      A.line(c, new double[][]{{0, -h - 17},{0, -h * .2}}, "#e2cb912f", 1.4);
      for(double side:new double[]{-1, 1}) A.line(c, new double[][]{{side * w * .2, -h * .74},{side * w * .3, -h * .08}}, "#e2cb911c", 1);
      if (crown) {
        A.line(c, new double[][]{{-w * .6, -h * .54},{0, -h * .64},{w * .6, -h * .54}}, "#bca47366", 2);
        arch(c, -w * .17, -h * .47, w * .34, h * .22, "#344f5b", "#c7ad7944");
      }
      c.restore();
    
 }

 void arch(VectorCanvas c,double x,double y,double w,double h,String fill,String stroke){

      c.beginPath(); c.moveTo(x, y + h); c.lineTo(x, y + w * .5);
      c.bezierCurveTo(x, y - w * .15, x + w, y - w * .15, x + w, y + w * .5);
      c.lineTo(x + w, y + h); c.closePath(); c.fillStyle = fill; c.fill();
      if (stroke!=null) { c.strokeStyle = stroke; c.lineWidth = 1; c.stroke(); }
    
 }

 void palace(VectorCanvas c,double x,double y){

      c.save(); c.translate(x, y);
      
      A.path(c, new double[][]{{-235, 0},{-216, -41},{-170, -61},{-121, -69},{0, -105},{121, -69},{170, -61},{216, -41},{235, 0}}, "#7d806a", null);
      double[] heights = new double[]{113, 136, 161, 188, 227, 281, 335, 281, 227, 188, 161, 136, 113};
      for (int i = 0; i < heights.length; i++) {
        double xx=(i-6)*23,h=heights[i],w=i==6?17:13;
        spire(c, xx, -16, w, h, i % 2 != 0 ? "#b6a06f" : "#c2ac79", "#73785f", false);
        A.line(c, new double[][]{{xx - 4, -h + 19},{xx - 4, -31}}, "#e3cd9226", 1);
      }
      for(double side:new double[]{-1, 1}) {
        c.save(); c.scale(side, 1);
        for (int i = 0; i < 4; i++) {
          c.beginPath(); c.moveTo(16 + i * 10, -19);
          c.bezierCurveTo(92 + i * 13, -17, 126 + i * 14, -53 - i * 11, 160 + i * 14, -78 - i * 8);
          c.strokeStyle = i % 2 != 0 ? "#d0b57866" : "#9e926966"; c.lineWidth = 5; c.stroke();
        }
        c.restore();
      }
      A.path(c, new double[][]{{-77, -4},{-64, -77},{-43, -108},{0, -132},{43, -108},{64, -77},{77, -4}},
        A.gradient(c, -70, -110, 140, 70, "#b3a276", "#78856e"), "#ccb67d55", 1.2);
      arch(c, -15, -80, 30, 78, "#435f62", "#d6bf8188");
      
      for(double side:new double[]{-1, 1}) {
        A.line(c, new double[][]{{side * 21, -89},{side * 45, -69},{side * 55, -10}}, "#e0c58b55", 2);
        A.line(c, new double[][]{{side * 10, -104},{side * 32, -94},{side * 49, -77}}, "#6a745657", 2);
      }
      A.ring(c, 0, -108, 8, "#d7c18a55", 0);
      for(double side:new double[]{-1, 1}) for (int i = 0; i < 3; i++) arch(c, side * (29 + i * 15) - 4, -57 + i * 9, 8, 52 - i * 9, "#5b705f", "#d0b98144");
      for (int i = 0; i < 4; i++) A.line(c, new double[][]{{-91 - i * 13, -3 + i * 5},{91 + i * 13, -3 + i * 5}}, "#baa97755", 2);
      c.restore();
    
 }

 void world(VectorCanvas c,BattleCamera cam,double t){

      c.save(); cam.apply(c);
      
      VectorCanvas.Gradient voidFill = A.gradient(c, 0, -5, 0, 300, "#42616b", "#142e45");
      c.fillStyle = voidFill; c.fillRect(-1800, -5, 6200, 1500);
      double[][] deck = new double[][]{{-500, -9},{3100, -9},{3360, 118},{-760, 118}};
      A.path(c, deck, A.gradient(c, 0, -9, 0, 127, "#466b74", "#293c55"), "#a99b754d", 1);

      c.save();
      c.beginPath(); for(int i=0;i<deck.length;i++){if(i==0)c.moveTo(deck[i][0],deck[i][1]);else c.lineTo(deck[i][0],deck[i][1]);} c.closePath(); c.clip();
      String[] spectrum = new String[]{"#e774aa", "#e0a475", "#e4ce7b", "#7abf9a", "#72bed4", "#939cdb", "#bc8ed2"};
      for(int i=0;i<spectrum.length;i++){String color=spectrum[i];
        c.globalAlpha = .32; c.fillStyle = color; c.fillRect(-1000, 8 + i * 14, 4800, 13);
        A.line(c, new double[][]{{-1000, 8 + i * 14},{3800, 8 + i * 14}}, color, .8);
      }
      c.globalAlpha = 1;
      for (int x = -600; x < 3200; x += 118) {
        A.path(c, new double[][]{{x, -8},{x + 14, -8},{x + 82, 116},{x + 59, 116}}, "#eadbab09", null);
        A.line(c, new double[][]{{x, -7},{x + 69, 116}}, "#b6d9dd18", 1);
      }
      for (int x = 50; x < 2600; x += 240) {
        A.path(c, new double[][]{{x, 24},{x + 18, 37},{x, 51},{x - 18, 37}}, null, "#e8d5a126", 1.2);
        A.line(c, new double[][]{{x, 29},{x, 46},{x + 7, 40},{x, 36},{x - 7, 40}}, "#d6e5d12b", 1);
      }
      double glint = (t * 96) % 3000 - 200;
      A.line(c, new double[][]{{glint - 90, 12},{glint, 12},{glint + 18, 12}}, "#d5f5ed20", 1.5);
      c.restore();

      A.line(c, new double[][]{{-500, -10},{3100, -10}}, "#c8b57c99", 2.5);
      A.line(c, new double[][]{{-500, -6},{3100, -6}}, "#d3eee855", 1);
      A.path(c, new double[][]{{-760, 119},{3360, 119},{3360, 140},{-760, 140}}, "#3b4b58", "#a99b754d", 1);
      A.line(c, new double[][]{{-760, 120},{3360, 120}}, "#c9b67f80", 3);
      A.line(c, new double[][]{{-760, 129},{3360, 129}}, "#7b88b377", 1);
      for(double x:new double[]{0, 640, 1940, 2600}) beacon(c, x, t);
      for (int i = 0; i < 18; i++) {
        double x = (i * 169 + Math.sin(t * .22 + i) * 18) % 2600;
        double y = 16 + ((t * 8 + i * 17) % 95), opacity = .10 + Math.sin(t * 1.5 + i) * .055;
        A.ellipse(c, x, -y, .7 + (i % 3) * .3, 1.0 + (i % 3) * .3, "rgba(244,228,166," + opacity + ")");
      }
      c.restore();
      VectorCanvas.Gradient vignette = c.createRadialGradient(550, 300, 160, 550, 310, 720);
      vignette.addColorStop(0, "#0a1d3100"); vignette.addColorStop(1, "#0a1d316b");
      c.fillStyle = vignette; c.fillRect(0, 0, 1100, 620);
    
 }

 void beacon(VectorCanvas c,double x,double t){

      c.save(); c.translate(x, -12);
      A.path(c, new double[][]{{-11, 2},{-7, -25},{-3, -34},{0, -48},{3, -34},{7, -25},{11, 2}}, "#7f886d", "#c2ad7955", 1);
      A.line(c, new double[][]{{0, -35},{0, -12}}, "#d9c58a88", 2);
      A.ellipse(c, 0, -33, 2, 4, "#e0f2d1aa");
      VectorCanvas.Gradient g = c.createRadialGradient(0, -32, 1, 0, -32, 20 + Math.sin(t * 1.2 + x) * 2);
      g.addColorStop(0, "#eaf6bf24"); g.addColorStop(1, "#eaf6bf00");
      c.fillStyle = g; c.fillRect(-25, -57, 50, 50); c.restore();
    
 }
}
