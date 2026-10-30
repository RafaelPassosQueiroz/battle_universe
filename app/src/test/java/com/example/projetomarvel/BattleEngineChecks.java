package com.example.projetomarvel;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import com.example.projetomarvel.BattleEngine.*;

/** Regression against the engine on feat/new-layout before the native visual port.
 * The archived expectations predate the 2,600-unit world; coordinates are intentionally
 * checked separately, while damage, statuses, cooldowns and outcomes stay identical.
 */
public final class BattleEngineChecks {
 static int checks;
 static final String[] HEROES=CombatScenarios.HEROES;
 static void ok(boolean value,String message){checks++;if(!value)throw new AssertionError(message);}
 static void eq(float actual,float expected,String message){ok(Math.abs(actual-expected)<.025f,message+": "+actual+" != "+expected);}
 static BattleEngine engine(String name){return CombatScenarios.create(name,null,false,false,true);}
 static BattleEngine custom(String power){return CombatScenarios.create("Teste",power,true,false,true);}
 static void advance(BattleEngine e,float time){CombatScenarios.advance(e,time);}
 static void ready(BattleEngine e){e.player.specialCooldown=0;e.player.cooldown=0;e.player.stun=0;}
 private static final Map<String,String> BASELINE=new HashMap<>();
 static {try(BufferedReader r=new BufferedReader(new InputStreamReader(BattleEngineChecks.class.getResourceAsStream("/combat-baseline.tsv"),StandardCharsets.UTF_8))){String line;while((line=r.readLine())!=null){line=line.replace("\uFEFF","");int tab=line.indexOf('\t');if(tab>0)BASELINE.put(line.substring(0,tab),line.substring(tab+1));}}catch(IOException e){throw new ExceptionInInitializerError(e);}}
 static void parity(String group){Map<String,String> actual=CombatScenarios.group(group);ok(!actual.isEmpty(),"Scenarios exist: "+group);for(Map.Entry<String,String> item:actual.entrySet()){String expected=BASELINE.get(item.getKey());ok(expected!=null,"Original scenario exists "+item.getKey());ok(item.getValue().equals(expected),"Original rules changed "+item.getKey()+"\n"+item.getValue()+"\n"+expected);}}
 static void basics(){
  parity("melee");eq(BattleEngine.WIDTH,2600,"Expanded approved arena");
  BattleEngine e=engine("Homem de Ferro");for(int i=0;i<1500;i++)e.step(.01f,-1,false,false);eq(e.player.x,BattleEngine.EDGE,"Left arena edge");
  e.player.y=120;e.player.vy=500;for(int i=0;i<2500;i++)e.step(.01f,1,false,false);ok(e.player.x<=BattleEngine.WIDTH-BattleEngine.EDGE,"Right edge bounded");
 }
 static void common(){parity("power");eq(BattleEngine.RETURNING_POWER_RANGE,844f/3-48,"Original shield and hammer range preserved");}
 static void specialRules(){
  parity("special");BattleEngine e=engine("Thanos");ready(e);ok(e.special(e.player),"Estalo starts");advance(e,1.4f);ok(!e.over,"Estalo has preparation");e.hurt(e.player,e.bot,8,false,false,-1,-1);ok(!e.player.specialActive&&!e.over,"Damage interrupts Estalo");
  e=engine("Thanos");ready(e);e.special(e.player);advance(e,1.6f);ok(e.over&&e.victory&&e.bot.dusted,"Prepared Estalo wins");
 }
 static void beamsAndStrikes(){
  BattleEngine e=engine("Homem de Ferro");ready(e);e.special(e.player);advance(e,.49f);eq(e.bot.hp,120,"Unibeam warning");advance(e,.12f);eq(e.bot.hp,85,"Approved Unibeam 35 damage");ok(e.player.exhaustion>0,"Unibeam recovery");
  e=engine("Thor");ready(e);e.special(e.player);advance(e,.3f);e.bot.x+=180;advance(e,.6f);eq(e.bot.hp,120,"Marked lightning can be dodged");
 }
 static void rewind(){parity("rewind");}
 static void scarletAllies(){parity("ally");}
 static void customs(){parity("custom");for(String power:CombatScenarios.CUSTOM){BattleEngine e=custom(power);ready(e);ok(e.special(e.player),"Custom special: "+power);advance(e,6);ok(!e.player.specialActive,"Custom action completes: "+power);ok(e.player.x>=BattleEngine.EDGE&&e.player.x<=BattleEngine.WIDTH-BattleEngine.EDGE,"Custom world bounds");}}
 static void interactions(){parity("passive");BattleEngine e=engine("Pantera Negra");e.guard(e.player,true);advance(e,5.05f);ok(!e.player.guard&&e.player.guardLock>2.8,"Guard breaks after five seconds");advance(e,3.1f);eq(e.player.guardLock,0,"Guard recovers after three seconds");}
 static void simulations(){
  for(String hero:HEROES)for(int difficulty=0;difficulty<3;difficulty++){
   BattleEngine e=new BattleEngine("Homem-Aranha",hero,"Energia",false,difficulty,-1,false,87+difficulty);
   for(int i=0;i<6000&&!e.over;i++){
    if(i%20==0)e.attack(e.player,Math.abs(e.player.x-e.bot.x)>145?2:i%40==0?0:1);
    if(i%91==0)e.jump(e.player);if(e.canSpecial(e.player))e.special(e.player);
    e.step(.016f,i%220<120?1:-1,i%130<50,true);
    ok(Float.isFinite(e.player.x)&&Float.isFinite(e.bot.x),"Finite positions "+hero);
    ok(e.player.hp>=0&&e.player.hp<=e.player.maxHp&&e.bot.hp>=0&&e.bot.hp<=e.bot.maxHp,"HP bounds "+hero);
    ok(e.player.x>=BattleEngine.EDGE&&e.player.x<=BattleEngine.WIDTH-BattleEngine.EDGE&&e.bot.x>=BattleEngine.EDGE&&e.bot.x<=BattleEngine.WIDTH-BattleEngine.EDGE,"Arena bounds "+hero);
    if(e.bot.ally!=null&&e.bot.ally.hp>0)ok(e.bot.ally.hp<=20,"Ally HP bounded");
    e.hits.clear();
   }
  }
 }
 public static void main(String[] args){basics();common();specialRules();beamsAndStrikes();rewind();scarletAllies();customs();interactions();simulations();System.out.println("PASS "+checks+" checks, 477 original-rule scenarios and 39 CPU matchups.");}
}
