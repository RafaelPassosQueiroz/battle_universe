package com.example.projetomarvel;
import org.junit.Test;
import static org.junit.Assert.*;
public class BattleCameraTest {
 private BattleEngine fight(){return new BattleEngine("Capitão América","Homem de Ferro","Energia",false,1,-1,false,1);}
 @Test public void followsTravelWithoutCrossingWorldEdges(){BattleEngine b=fight();BattleCamera c=new BattleCamera();c.reset(b.player,b.bot);for(int i=0;i<600;i++){b.player.x=Math.max(55,1100-i*2);b.bot.x=b.player.x+180;c.update(1d/60,b.player,b.bot);double half=550/c.zoom;assertTrue(c.x>=Math.min(half,1300)-.01);assertTrue(c.x<=Math.max(1300,2600-half)+.01);}assertTrue(c.x<900);}
 @Test public void zoomsSmoothlyToIncludeDistantOpponents(){BattleEngine b=fight();BattleCamera c=new BattleCamera();c.reset(b.player,b.bot);b.player.x=55;b.bot.x=2545;double before=c.zoom;c.update(1d/60,b.player,b.bot);assertTrue(c.zoom<before&&c.zoom>.4);for(int i=0;i<300;i++)c.update(1d/60,b.player,b.bot);assertEquals(.4,c.zoom,.001);assertTrue(550+(b.player.x-c.x)*c.zoom>=25);assertTrue(550+(b.bot.x-c.x)*c.zoom<=1075);}
 @Test public void approachesAgainAtCloseRange(){BattleEngine b=fight();BattleCamera c=new BattleCamera();c.reset(b.player,b.bot);b.player.x=1200;b.bot.x=1300;for(int i=0;i<300;i++)c.update(1d/60,b.player,b.bot);assertEquals(1.18,c.zoom,.001);assertTrue(c.x>1100&&c.x<1400);}
}
