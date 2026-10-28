//passagem da exploração para a arena
package com.example.projetomarvel;
import android.content.*;import java.util.*;import static com.example.projetomarvel.WorldValues.*;
/** Stable battle request: existing fighters and combat rules remain owned by FightActivity. */
final class WorldBattleBridge {
 static Intent intent(Context context,Map<String,Object>session,Map<String,Object>npc,WorldRules rules){
  Map<String,Object>hero=map(session.get("character")),opponent=map(rules.run("npcCharacter",npc));boolean custom="custom".equals(hero.get("id"));if(!custom&&!list(rules.global.get("COMBAT_HEROES")).contains(hero.get("id")))throw new IllegalArgumentException("Este personagem participa apenas como NPC.");if(!truth(rules.run("combatantAllowed",npc.get("id"))))throw new IllegalArgumentException("NPC sem batalha narrativa.");Intent i=new Intent(context,FightActivity.class);
  i.putExtra("world_encounter",str(npc.get("id")));i.putExtra("player",str(hero.get("name")));String bot=str(opponent.get("name"));if("doom".equals(npc.get("hero")))bot="Dr. Doom"+(truth(npc.get("variant"))?" · de outra realidade":"");if("Thor".equals(get(rules.run("heroById",npc.get("hero")),"name")))bot="Thor";if("Hulk".equals(get(rules.run("heroById",npc.get("hero")),"name")))bot="Hulk";i.putExtra("bot",bot);i.putExtra("world_bot_label",str(npc.get("name")));i.putExtra("world_bot_generic",npc.get("enemy")!=null);i.putExtra("world_generic_kind",str(npc.get("enemy")));i.putExtra("difficulty",1);i.putExtra("world_custom",custom);
  if(custom){Map<String,Object>a=map(hero.get("appearance"));i.putExtra("story_color",WorldCanvas.color(str(hero.get("body"))));i.putExtra("story_skin",integer(a.get("skin")));i.putExtra("story_build",integer(a.get("build")));i.putExtra("story_hair",integer(a.get("hair")));i.putExtra("story_style",integer(a.get("style")));i.putExtra("story_power",str(hero.get("power")));}
  return i;
 }
}
