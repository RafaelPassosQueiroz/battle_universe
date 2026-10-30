package com.example.projetomarvel;
import java.util.*;
/** Stable, camera-independent scenarios also executed against the pre-layout engine. */
final class CombatScenarios {
 static final String[] HEROES={"Homem-Aranha","Homem de Ferro","Wolverine","Capitão América","Thor","Hulk","Pantera Negra","Doutor Estranho","Viúva Negra","Feiticeira Escarlate","Capitã Marvel","Dr. Doom","Thanos"};
 static final String[] CUSTOM={"Energia","Gelo","Velocidade","Super Força","Ninja","Telecinese"};
 static BattleEngine create(String hero,String power,boolean custom,boolean bot,boolean left){
  BattleEngine e=new BattleEngine(bot?"Homem de Ferro":hero,bot?hero:"Homem de Ferro",power,custom,1,-1,false,42);
  float center=BattleEngine.WIDTH/2;BattleEngine.Fighter f=bot?e.bot:e.player,t=bot?e.player:e.bot;f.x=center+(left?-50:50);t.x=center+(left?50:-50);e.face();return e;
 }
 static void advance(BattleEngine e,double seconds){for(int i=0;i<(int)Math.ceil(seconds/.01);i++){e.step(.01f,0,false,false);e.hits.clear();}}
 static String state(BattleEngine e){return fighter(e.player)+"|"+fighter(e.bot)+"|"+e.over+"|"+e.victory+"|"+e.projectiles.size()+"|"+e.effects.size();}
 static String fighter(BattleEngine.Fighter f){return String.format(Locale.ROOT,"%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%s,%s,%s,%s,%s,%.2f",f.hp,f.maxHp,f.specialCooldown,f.stun,f.frozen,f.distortion,f.slow,f.guardLock,f.specialActive,f.armedMelee,f.forceShield,f.rewindUsed,f.dusted,f.ally==null?-1:f.ally.hp);}
 static Map<String,String> group(String group){Map<String,String> results=new TreeMap<>();
  if(group.equals("melee")||group.equals("power")||group.equals("special")){
   for(int i=0;i<HEROES.length;i++)for(boolean bot:new boolean[]{false,true})for(boolean left:new boolean[]{false,true}){
    if(group.equals("special")){for(double age:new double[]{.6,1.6,5.2,9}){BattleEngine e=create(HEROES[i],null,false,bot,left);BattleEngine.Fighter f=bot?e.bot:e.player;f.specialCooldown=0;e.special(f);advance(e,age);results.put(group+"/"+i+"/"+bot+"/"+left+"/"+age,state(e));}}
    else for(int slot=group.equals("power")?2:0;slot<=(group.equals("power")?2:1);slot++){BattleEngine e=create(HEROES[i],null,false,bot,left);e.attack(bot?e.bot:e.player,slot);advance(e,1.35);results.put(group+"/"+i+"/"+bot+"/"+left+"/"+slot,state(e));}
   }
  }else if(group.equals("custom")){
   for(int i=0;i<CUSTOM.length;i++)for(boolean left:new boolean[]{true,false}){
    for(int slot=0;slot<3;slot++){BattleEngine e=create("Herói",CUSTOM[i],true,false,left);e.attack(e.player,slot);advance(e,1.35);results.put(group+"/normal/"+i+"/"+left+"/"+slot,state(e));}
    for(double age:new double[]{.6,1.6,5.2,9}){BattleEngine e=create("Herói",CUSTOM[i],true,false,left);e.player.specialCooldown=0;e.special(e.player);advance(e,age);results.put(group+"/special/"+i+"/"+left+"/"+age,state(e));}
   }
  }else if(group.equals("passive")){
   for(String name:new String[]{"Wolverine","Dr. Doom"})for(double age:new double[]{1.9,2.1,4.2,7.2}){BattleEngine e=create(name,null,false,false,true);e.player.hp=60;advance(e,age);results.put(group+"/"+name+"/"+age,state(e));}
   for(String name:HEROES){BattleEngine e=create("Homem de Ferro",null,false,false,true);e=new BattleEngine(name,"Homem de Ferro","Energia",false,1,-1,false,42);e.player.x=BattleEngine.WIDTH/2-50;e.bot.x=BattleEngine.WIDTH/2+50;e.face();e.guard(e.player,true);e.attack(e.bot,2);advance(e,1.35);results.put(group+"/guard/"+name,state(e));}
  }else if(group.equals("rewind")){
   BattleEngine e=create("Doutor Estranho",null,false,false,true);e.player.hp=25;e.bot.hp=83;e.hurt(e.player,e.bot,2,false,false,-1,-1);e.hurt(e.player,e.bot,80,true,true,-1,-1);results.put(group+"/first",state(e));e.hurt(e.player,e.bot,80,true,true,-1,-1);results.put(group+"/second",state(e));
  }else if(group.equals("ally")){
   for(boolean left:new boolean[]{false,true}){BattleEngine e=create("Feiticeira Escarlate",null,false,false,left);e.player.hp=30;e.step(.01f,0,false,false);results.put(group+"/spawn/"+left,state(e));if(e.player.ally!=null)e.hurt(e.player.ally,e.bot,10,false,false,-1,e.bot.dir);results.put(group+"/damage/"+left,state(e));e.player.hp=29;e.step(.01f,0,false,false);results.put(group+"/persist/"+left,state(e));}
  }
  return results;
 }
 public static void main(String[]args)throws Exception{java.io.PrintWriter out=args.length>0?new java.io.PrintWriter(new java.io.OutputStreamWriter(new java.io.FileOutputStream(args[0]),java.nio.charset.StandardCharsets.UTF_8)):new java.io.PrintWriter(System.out);for(String group:new String[]{"melee","power","special","custom","passive","rewind","ally"})for(Map.Entry<String,String>e:group(group).entrySet())out.println(e.getKey()+"\t"+e.getValue());out.flush();if(args.length>0)out.close();}
}
