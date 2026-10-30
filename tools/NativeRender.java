package com.example.projetomarvel;
import com.android.layoutlib.bridge.Bridge;
import com.android.ide.common.rendering.api.ILayoutLog;
import android.graphics.*;
import java.nio.file.*;import java.util.*;
public class NativeRender {
 static final class CombatScenariosDummy {static final String[] HEROES={"Homem-Aranha","Homem de Ferro","Wolverine","Capitão América","Thor","Hulk","Pantera Negra","Doutor Estranho","Viúva Negra","Feiticeira Escarlate","Capitã Marvel","Dr. Doom","Thanos"};}
 static void save(Bitmap b,String path)throws Exception{int w=b.getWidth(),h=b.getHeight();int[]px=new int[w*h];b.getPixels(px,0,w,0,0,w,h);java.awt.image.BufferedImage im=new java.awt.image.BufferedImage(w,h,java.awt.image.BufferedImage.TYPE_INT_ARGB);im.setRGB(0,0,w,h,px,0,w);javax.imageio.ImageIO.write(im,"png",new java.io.File(path));}
 public static void main(String[]args)throws Exception{
  String data=args.length>0?args[0]:"C:/Program Files/Android/Android Studio/plugins/design-tools/resources/layoutlib/data";
  Map<String,String>props=new HashMap<>();props.put("ro.build.version.sdk","35");props.put("ro.build.version.release","15");props.put("ro.product.cpu.abilist","x86_64");
  Bridge bridge=new Bridge();boolean ok=bridge.init(props,new java.io.File(data+"/fonts"),data+"/win/lib64",data+"/icu/icudt78l.dat",data+"/hyphen-data",new String[]{},new HashMap<>(),new ILayoutLog(){public void error(String tag,String message,Throwable t,Object cookie,Object data){System.err.println(message);if(t!=null)t.printStackTrace();}});
  if(!ok)throw new IllegalStateException("Native bridge not initialized");
  Bitmap bitmap=Bitmap.createBitmap(1280,720,Bitmap.Config.ARGB_8888);Canvas c=new Canvas(bitmap);c.drawColor(0xff102637);
  for(int i=0;i<BattleRoster.ALL.length;i++){FighterVisual f=new FighterVisual(BattleRoster.at(i).name);double x=100+(i%7)*180,y=i<7?320:650;NativeFighterArt.draw(c,f,x,y,1.25,1);}
  int[]pixels=new int[1280*720];bitmap.getPixels(pixels,0,1280,0,0,1280,720);java.awt.image.BufferedImage image=new java.awt.image.BufferedImage(1280,720,java.awt.image.BufferedImage.TYPE_INT_ARGB);image.setRGB(0,0,1280,720,pixels,0,1280);javax.imageio.ImageIO.write(image,"png",new java.io.File("build/visual-reference/native-roster.png"));System.out.println("Native Android Canvas rendered all 13 fighters.");
  for(String hero:CombatScenariosDummy.HEROES)for(int pose=0;pose<=10;pose++)for(boolean mirror:new boolean[]{false,true}){FighterArtView.drawAnimated(c,400,500,1,hero,mirror,-1,pose,.45f);}
  Bitmap roster=Bitmap.createBitmap(1200,420,Bitmap.Config.ARGB_8888);Canvas rc=new Canvas(roster);rc.drawColor(0xff102637);
  String[] powers={"Energia","Gelo","Velocidade","Super Força","Ninja","Telecinese"};
  for(int i=0;i<6;i++){FighterVisual f=new FighterVisual("Herói");f.configure(0xff468dff,i%3,i%3,i%3,i%3,powers[i]);f.preview(FighterArtView.POSE_BLAST,.45,1);NativeFighterArt.draw(rc,f,90+i*165,350,1.3,1);}
  FighterVisual robot=new FighterVisual("Doombot");robot.generic("doombot");NativeFighterArt.draw(rc,robot,1100,350,1.3,1);save(roster,"build/visual-reference/native-customs-doombot.png");
  Bitmap fight=Bitmap.createBitmap(1335,600,Bitmap.Config.ARGB_8888);Canvas fc=new Canvas(fight);fc.drawColor(0xff142e46);
  BattleEngine e=new BattleEngine("Capitão América","Homem de Ferro","Energia",false,1,-1,false,1);BattleCamera camera=new BattleCamera();camera.reset(e.player,e.bot);
  fc.save();float scale=1335f/1100;fc.translate(0,(600-620*scale)*.58f);fc.scale(scale,scale);AsgardScene scene=new AsgardScene();VectorCanvas v=new VectorCanvas(fc);scene.background(v,camera,1);scene.world(v,camera,1);fc.save();camera.apply(fc);NativeFighterArt.draw(fc,new FighterVisual("Capitão América"),e.player.x,0,.72,1);FighterVisual iron=new FighterVisual("Homem de Ferro");iron.dir=-1;NativeFighterArt.draw(fc,iron,e.bot.x,0,.72,1);fc.restore();fc.restore();
  android.graphics.Typeface heading=android.graphics.Typeface.createFromFile("app/src/main/assets/fonts/BebasNeue-Regular.ttf"),body=android.graphics.Typeface.createFromFile("app/src/main/assets/fonts/Barlow-Regular.ttf");
  new BattleHud(heading,body).draw(fc,1335,600,e,camera,"Capitão América","Homem de Ferro","",.016);save(fight,"build/visual-reference/native-asgard.png");
  Bitmap fx=Bitmap.createBitmap(1335,600,Bitmap.Config.ARGB_8888);Canvas fxCanvas=new Canvas(fx);fxCanvas.drawColor(0xff142e46);fxCanvas.save();fxCanvas.scale(1335f/2600,1);for(String name:CombatScenariosDummy.HEROES){BattleEngine b=new BattleEngine(name,"Homem de Ferro","Energia",false,1,-1,false,1);b.player.specialCooldown=0;b.special(b.player);for(int i=0;i<60;i++)b.step(.01f,0,false,false);BattleVfx.abilities(fxCanvas,b,426,1,0xff468dff);for(BattleEngine.Projectile shot:b.projectiles)BattleVfx.projectile(fxCanvas,shot,426,1,0xff468dff);}fxCanvas.restore();save(fx,"build/visual-reference/native-powers-smoke.png");
  System.out.println("Native render OK: 286 action/direction samples, six custom powers, Doom Bot, Asgard and all hero ability forms.");bridge.dispose();
 }
}
