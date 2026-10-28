package com.example.projetomarvel;
import org.junit.Test;
import static org.junit.Assert.*;
public class WorldGenericBattleTest {
 BattleEngine arena(){return new BattleEngine("Homem de Ferro","Doombot de Patrulha","Energia",false,1,-1,false,42,true);}
 void advance(BattleEngine b,float seconds){for(int i=0;i<Math.ceil(seconds/.01f);i++)b.step(.01f,0,false,false);}
 @Test public void dashStopsAtOpponentWithoutDamageOrPush(){
  BattleEngine b=arena();b.player.x=180;b.bot.x=650;b.bot.specialCooldown=0;
  assertEquals(BattleEngine.Skill.GENERIC,b.bot.skill);assertTrue(b.special(b.bot));
  advance(b,.5f);assertEquals(245,b.bot.x,.1f);assertEquals(180,b.player.x,0);
  assertEquals(120,b.player.hp,0);assertEquals(120,b.bot.hp,0);
  assertTrue(b.projectiles.isEmpty());assertTrue(b.effects.isEmpty());assertTrue(b.hits.isEmpty());
  assertEquals(9.5f,b.bot.specialCooldown,.04f);assertFalse(b.special(b.bot));
 }
 @Test public void dashStopsAtWallWithAirborneOpponent(){
  BattleEngine b=arena();b.player.x=BattleEngine.EDGE;b.player.y=130;b.player.vy=800;
  b.bot.x=650;b.bot.specialCooldown=0;assertTrue(b.special(b.bot));advance(b,.45f);
  assertTrue(b.bot.x>=BattleEngine.EDGE);assertEquals(120,b.player.hp,0);assertEquals(BattleEngine.EDGE,b.player.x,0);
 }
 @Test public void dashTracksApproachingOpponentWithoutPushing(){
  BattleEngine b=arena();b.player.x=180;b.bot.x=650;b.bot.specialCooldown=0;b.special(b.bot);
  advance(b,.1f);b.player.x=400;advance(b,.4f);
  assertEquals(400,b.player.x,0);assertTrue(b.bot.x>=465);assertEquals(120,b.player.hp,0);
 }
 @Test public void genericProjectileDealsEightAndOriginalDoomRemainsDoom(){
  BattleEngine b=arena();b.player.x=340;b.bot.x=550;b.attack(b.bot,2);advance(b,1);
  assertEquals(112,b.player.hp,0);assertEquals(8,b.commonDamage(b.bot));
  BattleEngine normal=new BattleEngine("Homem-Aranha","Dr. Doom","Energia",false,1,-1,false,42);
  assertEquals(BattleEngine.Skill.DOOM,normal.bot.skill);assertEquals(15,normal.bot.specialCooldown,0);
 }
}
