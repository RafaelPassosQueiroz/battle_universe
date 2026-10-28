//Falas e Caixas
package com.example.projetomarvel;
import static com.example.projetomarvel.WorldValues.*;
/** Native Java rules generated from the authoritative Fraturas campaign. No WebView or JavaScript runtime. */
final class
WorldRules {final Scope global;WorldRules(java.util.Map<String,Object> data){global=new Scope(null);global.vars.putAll(data);installBuiltins(global);install_slotIdentity();install_storyIdentity();install_combatantAllowed();install_heroById();install_defaultDraft();install_characterFromDraft();install_npcCharacter();install_clueCount();install_fullAllies();install_trust();install_gesture();install_collectClue();install_refreshProgress();install_recordChoice();install_isVisible();install_visibleNPCs();install_encounterAllowed();install_completeEncounter();install_MISSIONS();install_objective();install_targetLocation();install_resolveEnding();install_endingSummary();install_breachCast();install_line();install_talk();install_opt();install_choice();install_done();install_remember();install_names();install_protagonist();install_gesturePrompt();install_repeat();install_dpDialogue();install_dialogueFor();install_narrativeDialogueFor();install_blockedText();install_narrativeScene();install_encounterScript();install_objectDialogue();install_OUTLINE();install_shade();install_drawSprite();install_NPC_PHASE();install_PAL();install_draw_tile();install_draw_prop();install_draw_object();}
Object run(String name,Object...args){return call(global.get(name),args);}
private void install_slotIdentity(){global.let("slotIdentity",fn(global,(s1,a2)->{
s1.let("slot",argument(a2,0));
if(always()) throw returned(or(()->get(obj(kv("defender","iron"),kv("leader","captain")),s1.get("slot")),()->s1.get("slot")));
}));
}
private void install_storyIdentity(){global.let("storyIdentity",fn(global,(s3,a4)->{
s3.let("npc",argument(a4,0));
s3.let("character",argument(a4,1));
s3.let("hero",(truth(get(s3.get("npc"),"slot"))?call(s3.get("slotIdentity"),args(get(s3.get("npc"),"slot"))):get(s3.get("npc"),"hero")));
s3.let("variant",and(()->and(()->unary("!",get(s3.get("npc"),"provisional")),()->method(s3.get("COMBAT_HEROES"),"includes",args(s3.get("hero")))),()->or(()->binary("===",s3.get("hero"),get(s3.get("character"),"id")),()->get(s3.get("npc"),"extra"))));
s3.let("reality",(truth(s3.get("variant"))?(truth(binary("===",get(s3.get("npc"),"id"),"wolverine"))?"Terra-Esmaecida":binary("+","Terra-Eco · ",get(s3.get("npc"),"loc"))):null));
if(always()) throw returned(obj(spread(s3.get("npc")),kv("hero",s3.get("hero")),kv("variant",s3.get("variant")),kv("reality",s3.get("reality")),kv("name",binary("+",or(()->get(method(s3.get("NPCS"),"find",args(fn(s3,(s5,a6)->{
s5.let("n",argument(a6,0));
throw returned(binary("===",get(s5.get("n"),"id"),get(s5.get("npc"),"id")));
}))),"name"),()->get(s3.get("npc"),"name")),(truth(s3.get("variant"))?" · de outra realidade":"")))));
}));
}
private void install_combatantAllowed(){global.let("combatantAllowed",fn(global,(s7,a8)->{
s7.let("id",argument(a8,0));
s7.let("npc",method(s7.get("NPCS"),"find",args(fn(s7,(s9,a10)->{
s9.let("n",argument(a10,0));
throw returned(binary("===",get(s9.get("n"),"id"),s9.get("id")));
}))));
if(truth(or(()->or(()->unary("!",s7.get("npc")),()->get(s7.get("npc"),"provisional")),()->method(s7.get("NARRATIVE_ONLY"),"includes",args(s7.get("id")))))){
if(always()) throw returned(false);
}
if(always()) throw returned(unary("!",unary("!",or(()->and(()->method(s7.get("ENCOUNTERS"),"some",args(fn(s7,(s11,a12)->{
s11.let("e",argument(a12,0));
throw returned(binary("===",get(s11.get("e"),"id"),s11.get("id")));
}))),()->or(()->method(s7.get("COMBAT_HEROES"),"includes",args(get(s7.get("npc"),"hero"))),()->method(arr("doombot","agent"),"includes",args(get(s7.get("npc"),"enemy"))))),()->and(()->get(s7.get("npc"),"extra"),()->method(s7.get("COMBAT_HEROES"),"includes",args(get(s7.get("npc"),"hero"))))))));
}));
}
private void install_heroById(){global.let("heroById",fn(global,(s13,a14)->{
s13.let("id",argument(a14,0));
if(always()) throw returned(method(s13.get("HEROES"),"find",args(fn(s13,(s15,a16)->{
s15.let("h",argument(a16,0));
throw returned(binary("===",get(s15.get("h"),"id"),s15.get("id")));
}))));
}));
}
private void install_defaultDraft(){global.let("defaultDraft",fn(global,(s17,a18)->{
if(always()) throw returned(obj(kv("type","custom"),kv("heroId","captain"),kv("name",""),kv("style",0),kv("build",1),kv("skin",1),kv("hair",0),kv("color",1),kv("power",0)));
}));
}
private void install_characterFromDraft(){global.let("characterFromDraft",fn(global,(s19,a20)->{
s19.let("draft",argument(a20,0));
if(truth(binary("===",get(s19.get("draft"),"type"),"marvel"))){
if(always()) throw returned(obj(spread(call(s19.get("heroById"),args(get(s19.get("draft"),"heroId")))),kv("type","marvel")));
}
if(always()) throw returned(obj(kv("id","custom"),kv("kind","custom"),kv("type","custom"),kv("name",method(get(s19.get("draft"),"name"),"trim",args())),kv("body",get(get(s19.get("COLORS"),get(s19.get("draft"),"color")),1)),kv("legs","#344956"),kv("accent",get(get(s19.get("COLORS"),get(s19.get("draft"),"color")),1)),kv("skin",get(arr("#ebbd91","#be8a63","#75523e"),get(s19.get("draft"),"skin"))),kv("power",get(get(s19.get("POWERS"),get(s19.get("draft"),"power")),"name")),kv("appearance",obj(kv("style",get(s19.get("draft"),"style")),kv("build",get(s19.get("draft"),"build")),kv("skin",get(s19.get("draft"),"skin")),kv("hair",get(s19.get("draft"),"hair")))),kv("colorName",get(get(s19.get("COLORS"),get(s19.get("draft"),"color")),0))));
}));
}
private void install_npcCharacter(){global.let("npcCharacter",fn(global,(s21,a22)->{
s21.let("npc",argument(a22,0));
if(truth(get(s21.get("npc"),"hero"))){
if(always()) throw returned(obj(spread(call(s21.get("heroById"),args(get(s21.get("npc"),"hero")))),kv("id",(truth(get(s21.get("npc"),"provisional"))?get(s21.get("npc"),"id"):get(s21.get("npc"),"hero"))),kv("name",get(s21.get("npc"),"name"))));
}
if(truth(method(arr("venom","goblin","killmonger","hela","magneto","ultron"),"includes",args(get(s21.get("npc"),"kind"))))){
if(always()) throw returned(obj(kv("id",get(s21.get("npc"),"id")),kv("kind",get(s21.get("npc"),"kind")),kv("name",get(s21.get("npc"),"name")),kv("body",get(s21.get("npc"),"body")),kv("legs",get(s21.get("npc"),"legs")),kv("accent",get(s21.get("npc"),"accent")),kv("skin",get(s21.get("npc"),"skin"))));
}
if(truth(binary("===",get(s21.get("npc"),"kind"),"loki"))){
if(always()) throw returned(obj(kv("id",get(s21.get("npc"),"id")),kv("kind","loki"),kv("name",get(s21.get("npc"),"name")),kv("body","#477250"),kv("legs","#37483e"),kv("accent","#ddbb63"),kv("skin","#deac7c")));
}
if(always()) throw returned(obj(kv("id",get(s21.get("npc"),"id")),kv("kind","resident"),kv("name",get(s21.get("npc"),"name")),kv("body",or(()->get(s21.get("npc"),"body"),()->"#71869b")),kv("legs","#485459"),kv("accent","#cdbc87"),kv("skin",or(()->get(s21.get("npc"),"skin"),()->"#bd8e69")),kv("hairColor",or(()->get(s21.get("npc"),"hairColor"),()->"#524044"))));
}));
}
private void install_clueCount(){global.let("clueCount",fn(global,(s23,a24)->{
s23.let("s",argument(a24,0));
throw returned(get(method(method(s23.get("Object"),"keys",args(s23.get("CLUES"))),"filter",args(fn(s23,(s25,a26)->{
s25.let("id",argument(a26,0));
throw returned(and(()->binary("!==",s25.get("id"),"FV6a"),()->get(get(s25.get("s"),"flags"),s25.get("id"))));
}))),"length"));
}));
}
private void install_fullAllies(){global.let("fullAllies",fn(global,(s27,a28)->{
s27.let("s",argument(a28,0));
throw returned(method(s27.get("ALLIES"),"filter",args(fn(s27,(s29,a30)->{
s29.let("id",argument(a30,0));
throw returned(binary("===",get(get(s29.get("s"),"relationships"),s29.get("id")),2));
}))));
}));
}
private void install_trust(){global.let("trust",fn(global,(s31,a32)->{
s31.let("s",argument(a32,0));
s31.let("id",argument(a32,1));
s31.let("amount",coalesce(argument(a32,2),()->1));
discard(put(get(s31.get("s"),"relationships"),s31.get("id"),method(s31.get("Math"),"max",args(0,method(s31.get("Math"),"min",args(2,binary("+",or(()->get(get(s31.get("s"),"relationships"),s31.get("id")),()->0),s31.get("amount"))))))));
}));
}
private void install_gesture(){global.let("gesture",fn(global,(s33,a34)->{
s33.let("s",argument(a34,0));
s33.let("id",argument(a34,1));
if(truth(get(get(s33.get("s"),"flags"),binary("+","G-",s33.get("id"))))){
if(always()) throw returned(false);
}
discard(put(get(s33.get("s"),"flags"),binary("+","G-",s33.get("id")),true));
discard(call(s33.get("trust"),args(s33.get("s"),s33.get("id"))));
discard(call(s33.get("addNote"),args(s33.get("s"),binary("+","G-",s33.get("id")),binary("+","Gesto · ",or(()->get(method(s33.get("NPCS"),"find",args(fn(s33,(s35,a36)->{
s35.let("n",argument(a36,0));
throw returned(binary("===",get(s35.get("n"),"id"),s35.get("id")));
}))),"name"),()->s33.get("id"))),binary("+",binary("+","Uma ação concreta completou sua promessa. Confiança ",get(get(s33.get("s"),"relationships"),s33.get("id"))),"/2."))));
if(always()) throw returned(true);
}));
}
private void install_collectClue(){global.let("collectClue",fn(global,(s37,a38)->{
s37.let("s",argument(a38,0));
s37.let("id",argument(a38,1));
if(truth(get(get(s37.get("s"),"flags"),s37.get("id")))){
if(always()) throw returned(false);
}
discard(put(get(s37.get("s"),"flags"),s37.get("id"),true));
discard(call(s37.get("addNote"),args(s37.get("s"),s37.get("id"),spread(get(s37.get("CLUES"),s37.get("id"))))));
discard(call(s37.get("refreshProgress"),args(s37.get("s"))));
if(always()) throw returned(true);
}));
}
private void install_refreshProgress(){global.let("refreshProgress",fn(global,(s39,a40)->{
s39.let("s",argument(a40,0));
s39.let("f",get(s39.get("s"),"flags"));
if(truth(binary("!==",get(s39.get("s"),"storyRevision"),2))){
{ final Scope s41=new Scope(s39);
s41.let("converted",obj(kv("venom","stationClear"),kv("goblin","oscClear"),kv("hawkeye","spider"),kv("killmonger","mineClear"),kv("hela","heart"),kv("ultron","armoryClear")));
for(Object v43:list(s41.get("NARRATIVE_ONLY"))){final Scope s42=new Scope(s41);s42.let("id",v43);
try{{ final Scope s44=new Scope(s42);
if(truth(binary("===",get(get(s44.get("s"),"encounters"),s44.get("id")),"completed"))){
{ final Scope s45=new Scope(s44);
discard(put(s45.get("f"),s45.get("id"),true));
if(truth(get(s45.get("converted"),s45.get("id")))){
discard(put(s45.get("f"),get(s45.get("converted"),s45.get("id")),true));
}
if(truth(binary("===",s45.get("id"),"antman"))){
{ final Scope s46=new Scope(s45);
discard(put(s46.get("f"),"stabilizer",true));
discard(put(s46.get("f"),"S11",true));
}
}
}
}
discard(remove(get(s44.get("s"),"encounters"),s44.get("id")));
discard(put(s44.get("s"),"notes",method(get(s44.get("s"),"notes"),"filter",args(fn(s44,(s47,a48)->{
s47.let("n",argument(a48,0));
throw returned(binary("!==",get(s47.get("n"),"id"),binary("+","encounter-",s47.get("id"))));
})))));
}
}catch(Continue c){}catch(Break b){break;}}
discard(put(s41.get("s"),"storyRevision",2));
}
}
discard(put(s39.get("f"),"M01",unary("!",unary("!",and(()->get(s39.get("f"),"rosa"),()->get(s39.get("f"),"marcus"))))));
discard(put(s39.get("f"),"marketOpen",unary("!",unary("!",or(()->get(s39.get("f"),"spider"),()->get(s39.get("f"),"widow"))))));
discard(put(s39.get("f"),"M08",unary("!",unary("!",and(()->get(s39.get("f"),"spider"),()->get(s39.get("f"),"widow"))))));
discard(put(s39.get("f"),"asgardOpen",unary("!",unary("!",and(()->get(s39.get("f"),"sif"),()->get(s39.get("f"),"frigga"))))));
discard(put(s39.get("f"),"wingsOpen",binary(">=",get(method(arr(get(s39.get("f"),"gamora"),get(s39.get("f"),"magneto"),get(s39.get("f"),"wolverine")),"filter",args(s39.get("Boolean"))),"length"),2)));
discard(put(s39.get("f"),"pymOpen",and(()->method(method(s39.get("Array"),"from",args(obj(kv("length",8)),fn(s39,(s49,a50)->{
s49.let("_",argument(a50,0));
s49.let("i",argument(a50,1));
throw returned(get(s49.get("f"),binary("+","FV",binary("+",s49.get("i"),1))));
}))),"every",args(s39.get("Boolean"))),()->binary(">=",or(()->get(s39.get("f"),"rocketVisits"),()->0),2))));
discard(put(s39.get("f"),"R1",binary(">=",get(method(arr("FV1","FV2","FV3","FV4","FV5"),"filter",args(fn(s39,(s51,a52)->{
s51.let("id",argument(a52,0));
throw returned(get(s51.get("f"),s51.get("id")));
}))),"length"),3)));
discard(put(s39.get("f"),"R2",binary(">=",get(method(arr("FV6","FV7","FV8"),"filter",args(fn(s39,(s53,a54)->{
s53.let("id",argument(a54,0));
throw returned(get(s53.get("f"),s53.get("id")));
}))),"length"),2)));
discard(put(s39.get("f"),"R3",unary("!",unary("!",and(()->get(s39.get("f"),"FV9"),()->get(s39.get("f"),"FV10"))))));
discard(put(s39.get("f"),"R4",unary("!",unary("!",get(s39.get("f"),"FV11")))));
discard(put(s39.get("f"),"R5",unary("!",unary("!",get(s39.get("f"),"FV12")))));
discard(put(s39.get("f"),"R6",unary("!",unary("!",and(()->and(()->and(()->and(()->get(s39.get("f"),"R1"),()->get(s39.get("f"),"R2")),()->get(s39.get("f"),"R3")),()->get(s39.get("f"),"R4")),()->get(s39.get("f"),"R5"))))));
s39.let("missions",obj(kv("M02",get(s39.get("f"),"fragment")),kv("M03",get(s39.get("f"),"stationClear")),kv("M04",get(s39.get("f"),"defender")),kv("M05",and(()->get(s39.get("f"),"oscClear"),()->get(s39.get("f"),"smallfragment"))),kv("M06",get(s39.get("f"),"strange")),kv("M07",get(s39.get("f"),"panther")),kv("M09",get(s39.get("f"),"goldCard")),kv("M10",get(s39.get("f"),"mineClear")),kv("M11",get(s39.get("f"),"hulk")),kv("M12",get(s39.get("f"),"loki")),kv("M13",get(s39.get("f"),"marvel")),kv("M14",get(s39.get("f"),"asgardOpen")),kv("M15",get(s39.get("f"),"cryptKey")),kv("M16",get(s39.get("f"),"heart")),kv("M17",get(s39.get("f"),"odinAwake")),kv("M18",get(s39.get("f"),"wingsOpen")),kv("M19",get(s39.get("f"),"leader")),kv("M20",get(s39.get("f"),"breach")),kv("M21",get(s39.get("f"),"armoryClear")),kv("M22",get(s39.get("f"),"scarlet")),kv("M23",get(s39.get("f"),"destinyChosen")),kv("M24",get(s39.get("f"),"solved"))));
discard(method(s39.get("Object"),"assign",args(s39.get("f"),s39.get("missions"))));
for(Object v56:list(method(s39.get("Object"),"entries",args(s39.get("missions"))))){final Scope s55=new Scope(s39);Object v57=v56;
s55.let("id",get(v57,0));
s55.let("done",get(v57,1));
try{if(truth(s55.get("done"))){
discard(call(s55.get("addNote"),args(s55.get("s"),s55.get("id"),binary("+",s55.get("id")," · concluída"),"O dossiê registra este passo da campanha Fraturas.")));
}
}catch(Continue c){}catch(Break b){break;}}
}));
}
private void install_recordChoice(){global.let("recordChoice",fn(global,(s58,a59)->{
s58.let("s",argument(a59,0));
s58.let("id",argument(a59,1));
s58.let("index",argument(a59,2));
s58.let("option",argument(a59,3));
if(truth(method(s58.get("Object"),"hasOwn",args(get(s58.get("s"),"choices"),s58.get("id"))))){
if(always()) throw returned(false);
}
discard(put(get(s58.get("s"),"choices"),s58.get("id"),s58.get("index")));
if(truth(get(s58.get("option"),"tone"))){
{ final Scope s60=new Scope(s58);
discard(put(get(s60.get("s"),"flags"),get(s60.get("option"),"tone"),binary("+",or(()->get(get(s60.get("s"),"flags"),get(s60.get("option"),"tone")),()->0),1)));
if(truth(and(()->get(s60.get("option"),"ally"),()->binary("===",get(s60.get("AFFINITIES"),get(s60.get("option"),"ally")),get(s60.get("option"),"tone"))))){
discard(call(s60.get("trust"),args(s60.get("s"),get(s60.get("option"),"ally"))));
}
}
}
if(truth(get(s58.get("option"),"relation"))){
discard(call(s58.get("trust"),args(s58.get("s"),spread(get(s58.get("option"),"relation")))));
}
if(truth(get(s58.get("option"),"flag"))){
discard(put(get(s58.get("s"),"flags"),get(s58.get("option"),"flag"),true));
}
if(truth(get(s58.get("option"),"note"))){
discard(call(s58.get("addNote"),args(s58.get("s"),spread(get(s58.get("option"),"note")))));
}
discard(method(s58.get("option"),"effect",args()));
if(truth(get(s58.get("option"),"retry"))){
discard(remove(get(s58.get("s"),"choices"),s58.get("id")));
}
discard(call(s58.get("refreshProgress"),args(s58.get("s"))));
if(always()) throw returned(true);
}));
}
private void install_isVisible(){global.let("isVisible",fn(global,(s61,a62)->{
s61.let("entity",argument(a62,0));
s61.let("s",argument(a62,1));
if(always()) throw returned(and(()->and(()->and(()->and(()->unary("!",unary("!",s61.get("s"))),()->or(()->unary("!",get(s61.get("entity"),"requires")),()->get(get(s61.get("s"),"flags"),get(s61.get("entity"),"requires")))),()->or(()->unary("!",get(s61.get("entity"),"when")),()->method(get(s61.get("entity"),"when"),"every",args(fn(s61,(s63,a64)->{
s63.let("id",argument(a64,0));
throw returned(get(get(s63.get("s"),"flags"),s63.get("id")));
}))))),()->or(()->unary("!",get(s61.get("entity"),"extra")),()->unary("!",method(arr(get(get(s61.get("s"),"character"),"id"),call(s61.get("slotIdentity"),args("defender",get(s61.get("s"),"character"))),call(s61.get("slotIdentity"),args("leader",get(s61.get("s"),"character")))),"includes",args(get(s61.get("entity"),"hero")))))),()->or(()->unary("!",get(s61.get("entity"),"hideAfter")),()->unary("!",get(get(s61.get("s"),"flags"),get(s61.get("entity"),"hideAfter"))))));
}));
}
private void install_visibleNPCs(){global.let("visibleNPCs",fn(global,(s65,a66)->{
s65.let("s",argument(a66,0));
if(always()) throw returned(method(s65.get("NPCS"),"filter",args(fn(s65,(s67,a68)->{
s67.let("n",argument(a68,0));
throw returned(call(s67.get("isVisible"),args(s67.get("n"),s67.get("s"))));
}))));
}));
}
private void install_encounterAllowed(){global.let("encounterAllowed",fn(global,(s69,a70)->{
s69.let("s",argument(a70,0));
s69.let("id",argument(a70,1));
if(truth(unary("!",call(s69.get("combatantAllowed"),args(s69.get("id")))))){
if(always()) throw returned(false);
}
s69.let("f",get(s69.get("s"),"flags"));
s69.let("req",obj(kv("station-guard",and(()->get(s69.get("f"),"fragment"),()->get(s69.get("f"),"venom"))),kv("defender",get(s69.get("f"),"stationClear")),kv("osc-guards",and(()->get(s69.get("f"),"defender"),()->get(s69.get("f"),"goblin"))),kv("strange",get(s69.get("f"),"smallfragment")),kv("panther",get(s69.get("f"),"strange")),kv("spider",get(s69.get("f"),"panther")),kv("widow",get(s69.get("f"),"panther")),kv("mine-guard",and(()->and(()->get(s69.get("f"),"goldCard"),()->get(s69.get("f"),"M08")),()->get(s69.get("f"),"killmonger"))),kv("hulk",and(()->get(s69.get("f"),"mineClear"),()->get(s69.get("f"),"banner"))),kv("marvel",get(s69.get("f"),"loki")),kv("thor",get(s69.get("f"),"heart")),kv("wolverine",get(s69.get("f"),"odinAwake")),kv("leader",and(()->get(s69.get("f"),"vision"),()->get(s69.get("f"),"wolverine"))),kv("armory-guard",and(()->get(s69.get("f"),"breach"),()->get(s69.get("f"),"ultron"))),kv("scarlet",get(s69.get("f"),"armoryClear")),kv("doom",get(s69.get("f"),"scarlet")),kv("thanos",get(s69.get("f"),"destinyChosen"))));
if(always()) throw returned((truth(method(s69.get("id"),"startsWith",args("extra-")))?unary("!",unary("!",get(s69.get("f"),"odinAwake"))):unary("!",unary("!",get(s69.get("req"),s69.get("id"))))));
}));
}
private void install_completeEncounter(){global.let("completeEncounter",fn(global,(s71,a72)->{
s71.let("s",argument(a72,0));
s71.let("id",argument(a72,1));
if(truth(unary("!",call(s71.get("encounterAllowed"),args(s71.get("s"),s71.get("id")))))){
if(always()) throw error(construct("Error",args("Encontre primeiro a condição narrativa deste confronto.")));
}
if(truth(binary("===",get(get(s71.get("s"),"encounters"),s71.get("id")),"completed"))){
if(always()) throw returned(false);
}
discard(put(get(s71.get("s"),"encounters"),s71.get("id"),"completed"));
discard(put(get(s71.get("s"),"flags"),s71.get("id"),true));
s71.let("grants",obj(kv("strange","ring"),kv("station-guard","stationClear"),kv("osc-guards","oscClear"),kv("mine-guard","mineClear"),kv("armory-guard","armoryClear")));
if(truth(get(s71.get("grants"),s71.get("id")))){
discard(put(get(s71.get("s"),"flags"),get(s71.get("grants"),s71.get("id")),true));
}
if(truth(method(s71.get("id"),"startsWith",args("extra-")))){
discard(put(get(s71.get("s"),"flags"),"S12",true));
}
if(truth(binary("===",s71.get("id"),"thanos"))){
{ final Scope s73=new Scope(s71);
discard(put(get(s73.get("s"),"flags"),"solved",true));
discard(put(s73.get("s"),"ending",call(s73.get("resolveEnding"),args(s73.get("s")))));
discard(call(s73.get("rememberEnding"),args(s73.get("s"))));
}
}
discard(call(s71.get("refreshProgress"),args(s71.get("s"))));
s71.let("e",method(s71.get("ENCOUNTERS"),"find",args(fn(s71,(s74,a75)->{
s74.let("e",argument(a75,0));
throw returned(binary("===",get(s74.get("e"),"id"),s74.get("id")));
}))));
discard(call(s71.get("addNote"),args(s71.get("s"),binary("+","encounter-",s71.get("id")),(truth(s71.get("e"))?or(()->get(s71.get("e"),"mark"),()->binary("+","Encontro ",get(s71.get("e"),"number"))):"Prova opcional"),or(()->get(s71.get("e"),"context"),()->"Um duelo de preparação foi registrado, sem implementar combate."))));
if(always()) throw returned(true);
}));
}
private void install_MISSIONS(){global.let("MISSIONS",arr(arr("M01","Ouça Rosa e Marcus.","rosa"),arr("M02","Investigue a runa e toque o Fragmento no Central Park.","fragment"),arr("M03","Ouça Venom e desative o Doombot que amplifica a Fissura no metrô.","venom"),arr("M04","Suba à Torre Stark e aceite o teste do defensor.","defender"),arr("M05","Investigue o contrato com o Duende, detenha o agente e recolha o fragmento menor.","goblin"),arr("M06","Leve o fragmento ao teste de Strange no Sanctum.","strange"),arr("M07","Use o portal do Sanctum. Pantera espera na fronteira.","panther"),arr("M08","Desminta a ordem falsa com Homem-Aranha e Viúva em qualquer ordem.","spider"),arr("M09","Visite Shuri e peça o Cartão Dourado.","shuri"),arr("M10","Ouça o Eco de Killmonger e interrompa o Doombot de extração.","killmonger"),arr("M11","Ouça Banner, acalme Hulk e use o Portal Harmônico.","banner"),arr("M12","Negocie com Loki e alinhe a ponte partida, sem entregar o Fragmento.","loki"),arr("M13","Abra a quarentena com Capitã Marvel.","marvel"),arr("M14","Converse com Sif e Frigga sobre o sono de Odin.","sif"),arr("M15","Procure Eir na Biblioteca e a Chave da Cripta.","eir"),arr("M16","Negocie com Hela a liberação do Coração do Bifrost.","hela"),arr("M17","Prove-se a Thor, depois volte a Odin.","thor"),arr("M18","Explore duas das três alas: Cantina, Refugiados e Terra-Esmaecida.","rocket"),arr("M19","Descubra o teste na Terra-Esmaecida e investigue o impostor com Visão antes do duelo do Capitão.","vision"),arr("M20","Atravesse o portal da Torre e reúna os aliados na Muralha.","breach"),arr("M21","Investigue o registro de Ultron e desative o Doombot de Convergência.","ultron"),arr("M22","Liberte Wanda do colar de Domínio.","scarlet"),arr("M23","Enfrente Destino e decida se ele lutará ao seu lado.","doom"),arr("M24","Enfrente Thanos. Use o Fragmento para reverter o Núcleo.","thanos")));
}
private void install_objective(){global.let("objective",fn(global,(s76,a77)->{
s76.let("s",argument(a77,0));
discard(call(s76.get("refreshProgress"),args(s76.get("s"))));
s76.let("m",method(s76.get("MISSIONS"),"find",args(fn(s76,(s78,a79)->{
Object v80=argument(a79,0);
s78.let("id",get(v80,0));
throw returned(unary("!",get(get(s78.get("s"),"flags"),s78.get("id"))));
}))));
if(truth(unary("!",s76.get("m")))){
if(always()) throw returned(obj(kv("text","Seu universo sobreviveu. Volte à bodega para o epílogo e reveja seus aliados."),kv("target","dp9")));
}
Object v81=s76.get("m");
s76.let("text",get(v81,1));
s76.let("target",get(v81,2));
if(truth(and(()->binary("===",s76.get("target"),"rosa"),()->get(get(s76.get("s"),"flags"),"rosa")))){
discard(s76.set("target","marcus"));
}
if(truth(and(()->binary("===",s76.get("target"),"venom"),()->get(get(s76.get("s"),"flags"),"venom")))){
discard(s76.set("target","station-guard"));
}
if(truth(and(()->binary("===",s76.get("target"),"goblin"),()->get(get(s76.get("s"),"flags"),"goblin")))){
discard(s76.set("target",(truth(get(get(s76.get("s"),"flags"),"oscClear"))?"smallfragment":"osc-guards")));
}
if(truth(and(()->binary("===",s76.get("target"),"spider"),()->get(get(s76.get("s"),"flags"),"spider")))){
discard(s76.set("target","widow"));
}
if(truth(and(()->binary("===",s76.get("target"),"killmonger"),()->get(get(s76.get("s"),"flags"),"killmonger")))){
discard(s76.set("target","mine-guard"));
}
if(truth(and(()->binary("===",s76.get("target"),"ultron"),()->get(get(s76.get("s"),"flags"),"ultron")))){
discard(s76.set("target","armory-guard"));
}
if(truth(and(()->binary("===",s76.get("target"),"sif"),()->get(get(s76.get("s"),"flags"),"sif")))){
discard(s76.set("target","frigga"));
}
if(truth(and(()->binary("===",s76.get("target"),"thor"),()->get(get(s76.get("s"),"flags"),"thor")))){
discard(s76.set("target","odin"));
}
if(truth(binary("===",s76.get("target"),"vision"))){
{ final Scope s82=new Scope(s76);
if(truth(unary("!",get(get(s82.get("s"),"flags"),"wolverine")))){
discard(s82.set("target","wolverine"));
}
else{
if(truth(get(get(s82.get("s"),"flags"),"vision"))){
discard(s82.set("target","leader"));
}
}
}
}
if(always()) throw returned(obj(kv("text",binary("+",binary("+",get(s76.get("m"),0)," · "),s76.get("text"))),kv("target",s76.get("target"))));
}));
}
private void install_targetLocation(){global.let("targetLocation",fn(global,(s83,a84)->{
s83.let("s",argument(a84,0));
s83.let("id",get(call(s83.get("objective"),args(s83.get("s"))),"target"));
if(always()) throw returned(or(()->method(arr(spread(s83.get("NPCS")),spread(s83.get("OBJECTS"))),"find",args(fn(s83,(s85,a86)->{
s85.let("n",argument(a86,0));
throw returned(binary("===",get(s85.get("n"),"id"),s85.get("id")));
}))),()->null));
}));
}
private void install_resolveEnding(){global.let("resolveEnding",fn(global,(s87,a88)->{
s87.let("s",argument(a88,0));
s87.let("ap",get(call(s87.get("fullAllies"),args(s87.get("s"))),"length"));
s87.let("fv",call(s87.get("clueCount"),args(s87.get("s"))));
if(always()) throw returned((truth(and(()->and(()->binary(">=",s87.get("ap"),10),()->binary("===",get(get(s87.get("s"),"choices"),"destiny"),0)),()->binary(">=",s87.get("fv"),10)))?"restored":(truth(binary(">=",s87.get("ap"),5))?"stitched":"patched")));
}));
}
private void install_endingSummary(){global.let("endingSummary",fn(global,(s89,a90)->{
s89.let("s",argument(a90,0));
s89.let("kind",or(()->get(s89.get("s"),"ending"),()->call(s89.get("resolveEnding"),args(s89.get("s")))));
s89.let("ap",call(s89.get("fullAllies"),args(s89.get("s"))));
s89.let("text",get(obj(kv("restored","Os mundos se separam com cicatrizes mínimas. Wakanda recupera seu vibranium, o Bifrost fica inteiro e a Encruzilhada permanece um território neutro. Destino, desarmado, retorna à Latveria morta levando um fragmento da Terra-Esmaecida: reconstruir sem fundir."),kv("stitched",binary("+","Os universos sobrevivem, com pequenas Fissuras em Nova York. Wakanda e Asgard reconstruirão seus Pilares. ",(truth(binary("===",get(get(s89.get("s"),"choices"),"destiny"),0))?"Destino escolhe o exílio.":"Destino desaparece entre os espelhos."))),kv("patched","Seu universo foi salvo. Wakanda perde parte do vibranium, Asgard perde o Coração e a Encruzilhada fecha. Destino desaparece na queda do Núcleo. Na bodega, Rosa deixa um café esperando por você.")),s89.get("kind")));
if(always()) throw returned(obj(kv("kind",s89.get("kind")),kv("title",get(obj(kv("restored","Tecido Restaurado"),kv("stitched","Tecido Costurado"),kv("patched","Tecido Remendado")),s89.get("kind"))),kv("text",s89.get("text")),kv("epilogue",binary("+",binary("+",binary("+",binary("+","Thanos foi selado no Núcleo em colapso. O Fragmento virou pó azul. ",(truth(get(s89.get("ap"),"length"))?binary("+",binary("+","Aliados Plenos: ",method(method(s89.get("ap"),"map",args(fn(s89,(s91,a92)->{
s91.let("id",argument(a92,0));
throw returned(or(()->get(method(s91.get("NPCS"),"find",args(fn(s91,(s93,a94)->{
s93.let("n",argument(a94,0));
throw returned(binary("===",get(s93.get("n"),"id"),s93.get("id")));
}))),"name"),()->or(()->get(obj(kv("iron","Stark"),kv("captain","Capitão América")),s91.get("id")),()->s91.get("id"))));
}))),"join",args(", "))),"."):"Os outros heróis respeitam a vitória, mas a distância permanece."))," "),(truth(binary(">=",or(()->get(get(s89.get("s"),"relationships"),"alvarez"),()->0),1))?"Alvarez encerra o caso como “terremoto”. ":"")),"Marcus reencontra a bicicleta. O sol se põe em Nova York."))));
}));
}
private void install_breachCast(){global.let("breachCast",fn(global,(s95,a96)->{
s95.let("s",argument(a96,0));
s95.let("base",arr("spider","iron","strange","panther","hulk","thor","widow","captain"));
if(always()) throw returned(method(method(method(arr(spread(construct("Set",args(arr(spread(call(s95.get("fullAllies"),args(s95.get("s")))),spread(s95.get("base"))))))),"filter",args(fn(s95,(s101,a102)->{
s101.let("id",argument(a102,0));
throw returned(method(s101.get("COMBAT_HEROES"),"includes",args(s101.get("id"))));
}))),"slice",args(0,8)),"map",args(fn(s95,(s97,a98)->{
s97.let("id",argument(a98,0));
s97.let("actor",method(s97.get("NPCS"),"find",args(fn(s97,(s99,a100)->{
s99.let("n",argument(a100,0));
throw returned(and(()->and(()->unary("!",get(s99.get("n"),"provisional")),()->unary("!",get(s99.get("n"),"extra"))),()->binary("===",get(s99.get("n"),"hero"),s99.get("id"))));
}))));
if(always()) throw returned(obj(kv("id",s97.get("id")),kv("full",binary("===",get(get(s97.get("s"),"relationships"),s97.get("id")),2)),spread((truth(s97.get("actor"))?obj(kv("name",get(call(s97.get("storyIdentity"),args(s97.get("actor"),get(s97.get("s"),"character"))),"name"))):obj())),kv("variant",binary("===",s97.get("id"),get(get(s97.get("s"),"character"),"id")))));
}))));
}));
}
private void install_line(){global.let("line",fn(global,(s103,a104)->{
s103.let("text",argument(a104,0));
throw returned(obj(kv("text",s103.get("text"))));
}));
}
private void install_talk(){global.let("talk",fn(global,(s105,a106)->{
s105.let("text",argument(a106,0));
s105.let("onFinish",argument(a106,1));
throw returned(obj(kv("lines",arr(call(s105.get("line"),args(s105.get("text"))))),kv("onFinish",s105.get("onFinish"))));
}));
}
private void install_opt(){global.let("opt",fn(global,(s107,a108)->{
s107.let("label",argument(a108,0));
s107.let("response",argument(a108,1));
s107.let("tone",argument(a108,2));
s107.let("ally",argument(a108,3));
s107.let("effect",argument(a108,4));
throw returned(obj(kv("label",s107.get("label")),kv("response",s107.get("response")),kv("tone",s107.get("tone")),kv("ally",s107.get("ally")),kv("effect",s107.get("effect"))));
}));
}
private void install_choice(){global.let("choice",fn(global,(s109,a110)->{
s109.let("id",argument(a110,0));
s109.let("text",argument(a110,1));
s109.let("a",argument(a110,2));
s109.let("b",argument(a110,3));
throw returned(obj(kv("id",s109.get("id")),kv("text",s109.get("text")),kv("options",arr(s109.get("a"),s109.get("b")))));
}));
}
private void install_done(){global.let("done",fn(global,(s111,a112)->{
s111.let("s",argument(a112,0));
s111.let("flag",argument(a112,1));
throw returned(fn(s111,(s113,a114)->{
discard(put(get(s113.get("s"),"flags"),s113.get("flag"),true));
discard(call(s113.get("refreshProgress"),args(s113.get("s"))));
}));
}));
}
private void install_remember(){global.let("remember",fn(global,(s115,a116)->{
s115.let("s",argument(a116,0));
s115.let("id",argument(a116,1));
s115.let("title",argument(a116,2));
s115.let("text",argument(a116,3));
throw returned(call(s115.get("addNote"),args(s115.get("s"),s115.get("id"),s115.get("title"),s115.get("text"))));
}));
}
private void install_names(){global.let("names",obj(kv("iron","Stark"),kv("captain","Capitão"),kv("spider","Aranha"),kv("strange","Estranho"),kv("panther","Pantera"),kv("marvel","Capitã"),kv("scarlet","Wanda")));
}
private void install_protagonist(){global.let("protagonist",fn(global,(s117,a118)->{
s117.let("s",argument(a118,0));
s117.let("lines",argument(a118,1));
throw returned(or(()->or(()->get(s117.get("lines"),get(get(s117.get("s"),"character"),"id")),()->get(s117.get("lines"),"custom")),()->""));
}));
}
private void install_gesturePrompt(){global.let("gesturePrompt",fn(global,(s119,a120)->{
s119.let("s",argument(a120,0));
s119.let("id",argument(a120,1));
s119.let("text",argument(a120,2));
s119.let("label",argument(a120,3));
s119.let("require",coalesce(argument(a120,4),()->true));
s119.let("effect",coalesce(argument(a120,5),()->null));
if(truth(or(()->get(get(s119.get("s"),"flags"),binary("+","G-",s119.get("id"))),()->unary("!",s119.get("require"))))){
if(always()) throw returned(null);
}
if(always()) throw returned(obj(kv("lines",arr(call(s119.get("choice"),args(binary("+","gesture-",s119.get("id")),s119.get("text"),call(s119.get("opt"),args(s119.get("label"),"Sua ação foi reconhecida. Uma promessa virou confiança.",null,null,fn(s119,(s121,a122)->{
discard(call(s121.get("gesture"),args(s121.get("s"),s121.get("id"))));
discard(call(s121.get("effect"),args()));
}))),obj(spread(call(s119.get("opt"),args("Agora preciso seguir a investigação.","A oportunidade continua aqui, caso você volte."))),kv("retry",true))))))));
}));
}
private void install_repeat(){global.let("repeat",fn(global,(s123,a124)->{
s123.let("n",argument(a124,0));
s123.let("s",argument(a124,1));
s123.let("f",get(s123.get("s"),"flags"));
s123.let("id",get(s123.get("n"),"id"));
s123.let("ally",(truth(binary("===",s123.get("id"),"defender"))?call(s123.get("slotIdentity"),args("defender",get(s123.get("s"),"character"))):(truth(binary("===",s123.get("id"),"leader"))?call(s123.get("slotIdentity"),args("leader",get(s123.get("s"),"character"))):s123.get("id"))));
s123.let("opportunities",obj(kv("venom",arr("Rosa preparou comida para Eddie.","Entregar a comida.",get(s123.get("f"),"food"))),kv("defender",arr("Uma encomenda de Marcus ficou na Fissura.",(truth(get(s123.get("f"),"drone"))?"Devolver o drone perdido.":"Entregar a conta de vibranium."),and(()->get(s123.get("f"),"marcus"),()->or(()->get(s123.get("f"),"drone"),()->get(s123.get("f"),"bead"))))),kv("strange",arr("O livro atravessado ainda está com você. Wong conhece seu dono.","Devolver o livro de Marcus.",and(()->get(s123.get("f"),"book"),()->get(s123.get("f"),"marcus")))),kv("hawkeye",arr("Uma flecha ficou perdida no mercado.","Devolver a flecha.",get(s123.get("f"),"arrow"))),kv("loki",arr("O pedágio simbólico ainda pode ser pago.","Entregar a moeda nórdica.",get(s123.get("f"),"coin"))),kv("marvel",arr("Heimdall aguarda um livro da Biblioteca.","Devolver o livro a Heimdall.",get(s123.get("f"),"libraryBook"))),kv("starlord",arr("O cartaz ainda anuncia sua recompensa falsa.","Rasgar o cartaz e explicar o plano.",true)),kv("gamora",arr("Gamora guarda a gravação, mas continua desconfiada.","Oferecer ouvi-la sem armas.",true)),kv("magneto",arr("Os refugiados precisam de mantimentos.","Entregar os suprimentos de Oru.",get(s123.get("f"),"supplies"))),kv("vision",arr("Que prova separa o portador de sua cópia?","Cruzar FV3 com FV7: o sinal segue o Fragmento, não o rosto.",and(()->get(s123.get("f"),"FV3"),()->get(s123.get("f"),"FV7")))),kv("leader",arr("A figura de casa confiou um escudo emprestado a você.","Entregar o escudo à resistência.",get(s123.get("f"),"borrowedShield"))),kv("scarlet",arr("Visão pode reconhecer Wanda além do Domínio.","Pedir a análise remota de Visão.",binary("===",get(get(s123.get("s"),"relationships"),"vision"),2)))));
s123.let("g",get(s123.get("opportunities"),s123.get("id")));
if(truth(s123.get("g"))){
{ final Scope s125=new Scope(s123);
s125.let("script",call(s125.get("gesturePrompt"),args(s125.get("s"),s125.get("ally"),spread(s125.get("g")))));
if(truth(s125.get("script"))){
{ final Scope s126=new Scope(s125);
discard(put(get(get(s126.get("script"),"lines"),0),"id",binary("+",get(get(get(s126.get("script"),"lines"),0),"id"),binary("+","-",s126.get("id")))));
if(truth(binary("===",s126.get("id"),"starlord"))){
discard(put(get(get(get(get(s126.get("script"),"lines"),0),"options"),0),"effect",fn(s126,(s127,a128)->{
discard(call(s127.get("gesture"),args(s127.get("s"),s127.get("ally"))));
discard(put(s127.get("f"),"S08",true));
})));
}
if(always()) throw returned(s126.get("script"));
}
}
}
}
s123.let("after",obj(kv("spider","A ordem era falsa, mas o perigo era real. Vou manter o mercado seguro enquanto você segue as pistas de Shuri."),kv("venom","Estou ouvindo menos vozes. Ainda não é silêncio, mas já é um começo."),kv("defender","O sinal acompanha você. O consultor da Oscorp pode ter preparado tudo."),kv("goblin","O homem de metal prometeu que eu controlaria os blecautes. Nunca me deu seu nome."),kv("strange","Prefiro lutar ao seu lado. Mas, se o Tecido pedir, lutarei contra você de novo."),kv("panther","Wakanda reconhece quem luta com honra."),kv("hawkeye","Se aquele Fury era falso, eu estou em dívida."),kv("widow","Eu odeio quando estou errada. Mas gosto de saber."),kv("killmonger","Eu só queria um lugar onde minhas decisões fossem minhas."),kv("hulk","Hulk lembra."),kv("loki","Fui sincero uma vez. Detestei a sensação."),kv("marvel","Se eu estiver errada, vou me desculpar com força."),kv("hela","Se Asgard cair, eu serei a última rainha. Prefiro não ser."),kv("thor","Meu martelo reconhece você. Odin despertou: volte ao Salão antes de cruzar o Bifrost."),kv("starlord","Ok, eu fui o idiota. Sou um idiota que te deve uma."),kv("gamora","Vou retirar os habitantes da rota da Conta. Você já sabe quem precisa deter."),kv("magneto","Se for traição, eu serei o primeiro a lhe dizer."),kv("wolverine","Eu perdi tudo. Não vou perder de novo."),kv("vision","Estimo 78% de confiança. Vou aumentar o número."),kv("leader","Vamos. A passagem para Latveria está aberta."),kv("ultron","O registro encerra: o circuito de Domínio alimenta o Núcleo. O emissor permanece desligado."),kv("scarlet","Eu vi o fim de todos os mundos. Vamos impedir."),kv("antman","Desculpa. Eu diminuí demais minha confiança."),kv("thanos","O Titã está selado no Núcleo. Não há mais uma voz a contar mundos.")));
if(truth(and(()->get(s123.get("f"),"solved"),()->get(s123.get("after"),s123.get("id"))))){
if(always()) throw returned(call(s123.get("talk"),args(binary("+",(truth(binary("===",get(get(s123.get("s"),"relationships"),s123.get("ally")),2))?"Você veio por nós. Agora estamos com você. ":(truth(binary("===",get(get(s123.get("s"),"relationships"),s123.get("ally")),1))?"Eu reconheço o que você fez. ":"Seu mundo sobreviveu. Ainda estamos nos conhecendo. ")),get(s123.get("after"),s123.get("id"))))));
}
if(always()) throw returned(call(s123.get("talk"),args(or(()->get(s123.get("after"),s123.get("id")),()->"Este encontro já deixou sua marca na ilha."))));
}));
}
private void install_dpDialogue(){global.let("dpDialogue",fn(global,(s129,a130)->{
s129.let("n",argument(a130,0));
s129.let("s",argument(a130,1));
s129.let("f",get(s129.get("s"),"flags"));
s129.let("i",get(s129.get("n"),"appearance"));
s129.let("jokes",obj(kv(1,"Churros! Quatro cadeiras reservadas e ninguém pagou o couvert. Eu estaria preocupado com o céu."),kv(2,"Eu prefiro o fim do Metrô. Pelo menos tem uma saída sinalizada. Pegou meus churros no parque?"),kv(3,"Sou uma Dora Milaje muito convincente. Não olhe para os sapatos. Hulk é um diapasão verde."),kv(4,"Isso aí vibra? Eu também. Só que o meu plano odontológico não cobre vibranium."),kv(5,"Estou pescando o arco-íris. Se fisgar um deus, devolvo. Já tenho problemas suficientes."),kv(6,"Isso é javali ou piada? A Cripta cheira a mofo e a destino."),kv(7,"À direita, a Cantina. À esquerda, o Setor. Ao fundo, o fim do mundo. Visitas guiadas sem reembolso."),kv(8,"Se eu te contasse que eu já vi isso... você acreditaria?"),kv(9,"Obrigado por jogar. Joga de novo?")));
s129.let("text",get(s129.get("jokes"),s129.get("i")));
if(truth(get(s129.get("s"),"eco"))){
discard(s129.set("text",binary("+",s129.get("text"),binary("+",binary("+",binary("+",binary("+"," Eu lembro: antes você escolheu ",(truth(binary(">=",get(get(s129.get("s"),"eco"),"H"),get(get(s129.get("s"),"eco"),"C")))?"ouvir e falar a verdade":"medir cada risco")),". "),(truth(get(get(get(s129.get("s"),"eco"),"allies"),"length"))?binary("+",binary("+","Confiaram em você: ",method(method(get(get(s129.get("s"),"eco"),"allies"),"map",args(fn(s129,(s131,a132)->{
s131.let("id",argument(a132,0));
throw returned(or(()->get(s131.get("names"),s131.get("id")),()->s131.get("id")));
}))),"join",args(", "))),"."):"Você terminou sem Aliados Plenos."))," Desta vez, a decisão é sua."))));
}
if(truth(binary("===",s129.get("i"),9))){
{ final Scope s133=new Scope(s129);
s133.let("end",call(s133.get("endingSummary"),args(s133.get("s"))));
discard(s133.set("text",binary("+",s133.get("text"),binary("+",binary("+",binary("+"," ",get(s133.get("end"),"title")),". "),(truth(binary("===",get(s133.get("end"),"kind"),"patched"))?"Dá para fazer melhor. Eu vi.":"As escolhas deixaram cicatrizes diferentes.")))));
if(truth(and(()->and(()->get(s133.get("s"),"eco"),()->binary("===",call(s133.get("clueCount"),args(s133.get("s"))),12)),()->method(arr(1,2,3),"every",args(fn(s133,(s134,a135)->{
s134.let("k",argument(a135,0));
throw returned(get(s134.get("f"),binary("+","DP_hint",s134.get("k"))));
})))))){
if(always()) throw returned(obj(kv("lines",arr(call(s133.get("line"),args(s133.get("text"))),call(s133.get("line"),args("O Quarto Muro · Quatro cadeiras eram mundos. A quinta não é um vilão escondido: é um assento vazio. Eu fico fora do Tecido; por isso lembro de cada Fissura, inclusive as que você chamou de outra partida.")))),kv("onFinish",call(s133.get("done"),args(s133.get("s"),"fourthWall")))));
}
if(always()) throw returned(call(s133.get("talk"),args(s133.get("text"))));
}
}
s129.let("hint",get(obj(kv(3,arr(1,unary("!",unary("!",get(s129.get("f"),"FV8"))),"Relacionar o diapasão aos registros de Banner.","O Núcleo usa a frequência de Hulk para sintonizar um Pilar.")),kv(6,arr(2,unary("!",unary("!",and(()->get(s129.get("f"),"FV9"),()->get(s129.get("f"),"FV10")))),"Relacionar a Cripta à Conta do Titã.","A palavra Metade faz parte de uma contagem de habitantes, não de tesouros.")),kv(8,arr(3,unary("!",unary("!",and(()->get(s129.get("f"),"FV12"),()->get(s129.get("f"),"FV10")))),"Relacionar o mundo morto ao primeiro teste.","A Terra-Esmaecida foi uma tentativa anterior. Deadpool guarda a memória que o Tecido apagou."))),s129.get("i")));
if(truth(and(()->s129.get("hint"),()->unary("!",get(s129.get("f"),binary("+","DP_hint",get(s129.get("hint"),0))))))){
if(always()) throw returned(obj(kv("lines",arr(call(s129.get("line"),args(s129.get("text"))),call(s129.get("choice"),args(binary("+","dp-link-",get(s129.get("hint"),0)),"O que essas palavras significam?",obj(spread(call(s129.get("opt"),args(get(s129.get("hint"),2),(truth(get(s129.get("hint"),1))?get(s129.get("hint"),3):"Ainda faltam provas no dossiê. Volte com as pistas para relacionar a frase."),null,null,fn(s129,(s136,a137)->{
if(truth(get(s136.get("hint"),1))){
{ final Scope s138=new Scope(s136);
discard(put(s138.get("f"),binary("+","DP_hint",get(s138.get("hint"),0)),true));
discard(call(s138.get("remember"),args(s138.get("s"),binary("+","DP_hint",get(s138.get("hint"),0)),"Pista de Deadpool relacionada",get(s138.get("hint"),3))));
}
}
})))),kv("retry",unary("!",get(s129.get("hint"),1)))),obj(spread(call(s129.get("opt"),args("Pode ser só uma piada.","Pode. Eu nunca disse que era uma boa."))),kv("retry",true))))))));
}
if(always()) throw returned(call(s129.get("talk"),args(s129.get("text"),fn(s129,(s139,a140)->{
discard(put(s139.get("f"),binary("+","dpSeen",s139.get("i")),true));
if(truth(and(()->binary("===",s139.get("i"),2),()->get(s139.get("f"),"dpSeen1")))){
{ final Scope s141=new Scope(s139);
discard(put(s141.get("f"),"S02",true));
discard(call(s141.get("remember"),args(s141.get("s"),"S02","Churros Misteriosos","O rastro de Deadpool repete quatro cadeiras. Quem está reunindo seus ocupantes?")));
}
}
}))));
}));
}
private void install_dialogueFor(){global.let("dialogueFor",fn(global,(s142,a143)->{
s142.let("n",argument(a143,0));
s142.let("s",argument(a143,1));
s142.let("identity",call(s142.get("storyIdentity"),args(s142.get("n"),get(s142.get("s"),"character"))));
s142.let("script",call(s142.get("narrativeDialogueFor"),args(s142.get("identity"),s142.get("s"))));
if(truth(get(s142.get("identity"),"variant"))){
discard(method(get(s142.get("script"),"lines"),"unshift",args(call(s142.get("line"),args(binary("+",binary("+",binary("+",get(s142.get("identity"),"name")," vem de "),get(s142.get("identity"),"reality")),". “Meu mundo tem outra história. Não sou você: as Fissuras nos trouxeram ao mesmo lugar.”"))))));
}
if(always()) throw returned(s142.get("script"));
}));
}
private void install_narrativeDialogueFor(){global.let("narrativeDialogueFor",fn(global,(s144,a145)->{
s144.let("n",argument(a145,0));
s144.let("s",argument(a145,1));
s144.let("f",get(s144.get("s"),"flags"));
s144.let("id",get(s144.get("n"),"id"));
if(truth(method(s144.get("id"),"startsWith",args("dp")))){
if(always()) throw returned(call(s144.get("dpDialogue"),args(s144.get("n"),s144.get("s"))));
}
if(truth(and(()->binary("===",s144.get("id"),"doom"),()->get(s144.get("f"),"solved")))){
if(always()) throw returned(call(s144.get("talk"),args((truth(binary("===",get(s144.get("s"),"ending"),"restored"))?"Destino está desarmado. “Voltarei ao meu mundo morto. Tentarei reconstruir sem tomar outro.”":(truth(and(()->binary("===",get(s144.get("s"),"ending"),"stitched"),()->get(s144.get("f"),"Destino_A")))?"O espelho mostra uma Latveria vazia. Destino escolheu o exílio.":"A máscara desapareceu no Núcleo. O espelho já não responde.")))));
}
if(truth(and(()->and(()->binary("===",s144.get("id"),"doom"),()->get(s144.get("f"),"doom")),()->unary("!",get(s144.get("f"),"destinyChosen"))))){
if(always()) throw returned(obj(kv("lines",arr(call(s144.get("line"),args("Destino cai de joelhos: “Eu queria minha mãe de volta.” Thanos atravessa os espelhos: “Você me disse que seria um rei.” — “Eu disse que seria um mundo.”")),call(s144.get("choice"),args("destiny","Thanos toma o Núcleo. Destino ainda pode se levantar.",call(s144.get("opt"),args("Preciso de você. A culpa será julgada depois.","Destino se levanta. Seu raio abrirá uma brecha contra Thanos.","H",null,fn(s144,(s146,a147)->{
discard(put(s146.get("f"),"destinyChosen",true));
discard(put(s146.get("f"),"Destino_A",true));
}))),call(s144.get("opt"),args("Fique fora. Eu vou resolver isto.","Destino é lançado ao canto do Núcleo. Você seguirá sem sua ajuda.","C",null,fn(s144,(s148,a149)->{
discard(put(s148.get("f"),"destinyChosen",true));
discard(put(s148.get("f"),"Destino_B",true));
})))))))));
}
if(truth(or(()->binary("===",get(get(s144.get("s"),"encounters"),s144.get("id")),"completed"),()->and(()->method(s144.get("NARRATIVE_ONLY"),"includes",args(s144.get("id"))),()->get(s144.get("f"),s144.get("id")))))){
if(always()) throw returned(call(s144.get("repeat"),args(s144.get("n"),s144.get("s"))));
}
if(truth(method(s144.get("NARRATIVE_ONLY"),"includes",args(s144.get("id"))))){
if(always()) throw returned(call(s144.get("narrativeScene"),args(s144.get("n"),s144.get("s"))));
}
if(truth(method(s144.get("id"),"startsWith",args("extra-")))){
{ final Scope s150=new Scope(s144);
if(truth(unary("!",call(s150.get("encounterAllowed"),args(s150.get("s"),s150.get("id")))))){
if(always()) throw returned(call(s150.get("talk"),args("A prova opcional estará disponível depois de abrir o Bifrost da Encruzilhada.")));
}
if(truth(get(get(s150.get("s"),"encounters"),s150.get("id")))){
if(always()) throw returned(call(s150.get("talk"),args("A prova foi concluída. No próximo mundo, espero que lutemos do mesmo lado.")));
}
if(always()) throw returned(obj(kv("lines",arr(call(s150.get("choice"),args(binary("+","trial-",s150.get("id")),"S-12 · Uma prova opcional, fora da guerra dos Pilares.",call(s150.get("opt"),args("Aceito a prova de preparação.","Vamos testar nossa confiança, sem colocar habitantes em risco.","H",get(s150.get("n"),"hero"),fn(s150,(s151,a152)->{
discard(put(s151.get("f"),binary("+","trial-",s151.get("id")),true));
}))),call(s150.get("opt"),args("Quero ouvir seu relato primeiro.","O Doombot copia rostos, mas nunca as escolhas de quem os usa.","C",get(s150.get("n"),"hero"))))))),kv("onFinish",fn(s150,(s153,a154)->{
})),kv("encounter",fn(s150,(s155,a156)->{
if(always()) throw returned((truth(get(s155.get("f"),binary("+","trial-",s155.get("id"))))?s155.get("id"):null));
}))));
}
}
{Object v157=s144.get("id");boolean m158=false;try{if(m158||equal(v157,"rosa")){m158=true;if(truth(get(s144.get("f"),"solved"))){
if(always()) throw returned(call(s144.get("talk"),args(binary("+","Café? Sua cadeira continua aqui. ",(truth(binary("===",get(s144.get("s"),"ending"),"patched"))?"Nem tudo voltou ao lugar. Mas você voltou.":"Os vizinhos voltaram a chamar a rua pelo mesmo nome.")))));
}
if(truth(get(s144.get("f"),"rosa"))){
if(always()) throw returned(obj(kv("lines",arr(call(s144.get("choice"),args("recipe","Marcus ainda procura sua bicicleta. Eddie, no Metrô, talvez precise de algo mais simples que uma arma.",call(s144.get("opt"),args("Prepare comida. Eu a levo para Eddie.","Rosa embrulha a receita de família. “Ninguém pensa direito com fome.”","H",null,fn(s144,(s159,a160)->{
discard(put(s159.get("f"),"food",true));
discard(put(s159.get("f"),"S03",true));
}))),call(s144.get("opt"),args("Hoje preciso dos registros das quedas.","Rosa aponta o Central Park. Um homem de vermelho vendia churros quando tudo começou.","C",null,fn(s144,(s161,a162)->{
discard(put(s161.get("f"),"rosaClue",true));
})))))))));
}
if(always()) throw returned(obj(kv("lines",arr(call(s144.get("line"),args("O chão cheira queimado. Não é o metrô: é o céu. Um homem de vermelho vendia churros bem na hora do tremor.")),call(s144.get("line"),args(call(s144.get("protagonist"),args(s144.get("s"),obj(kv("spider","May pediu que eu olhasse por quem ficou embaixo. Vou começar ouvindo o bairro."),kv("iron","Pepper disse que consertar não é o mesmo que controlar. Hoje eu preciso ouvir."),kv("captain","Sharon conhece esse bairro. Se ele está com medo, eu fico."),kv("custom",binary("+",get(get(s144.get("s"),"character"),"name"),", sua casa também está sob este céu."))))))))),kv("onFinish",call(s144.get("done"),args(s144.get("s"),"rosa")))));
}if(m158||equal(v157,"marcus")){m158=true;if(truth(get(s144.get("f"),"solved"))){
if(always()) throw returned(call(s144.get("talk"),args("Minha bicicleta voltou a ser só uma bicicleta. Hoje entreguei café, não coisas de outro mundo. Rosa deixou um para você.",fn(s144,(s163,a164)->{
discard(put(s163.get("f"),"bikeReturned",true));
discard(put(s163.get("f"),"bike",true));
}))));
}
if(truth(and(()->get(s144.get("f"),"bike"),()->unary("!",get(s144.get("f"),"bikeReturned"))))){
if(always()) throw returned(call(s144.get("talk"),args("Minha bicicleta! Ela caiu de uma janela que não existe. Agora posso entregar as encomendas de novo.",fn(s144,(s165,a166)->{
discard(put(s165.get("f"),"bikeReturned",true));
discard(call(s165.get("remember"),args(s165.get("s"),"S01-bike","Bicicleta devolvida","Marcus voltou a trabalhar. Ele procura uma moeda nórdica, uma conta de vibranium e um livro atravessado.")));
}))));
}
if(truth(get(s144.get("f"),"marcus"))){
if(always()) throw returned(call(s144.get("talk"),args((truth(and(()->and(()->get(s144.get("f"),"G-loki"),()->get(s144.get("f"),"G-strange")),()->or(()->get(s144.get("f"),"G-iron"),()->get(s144.get("f"),"G-spider"))))?"Encomendas entregues! Este Mapa Dimensional é seu. Você devolveu cada objeto ao lugar certo.":"S-01 · Procure a moeda na bodega, a conta no Metrô e o livro na Oscorp. Leve a conta ao defensor, o livro a Strange e a moeda a Loki."),fn(s144,(s167,a168)->{
if(truth(and(()->and(()->get(s167.get("f"),"G-loki"),()->get(s167.get("f"),"G-strange")),()->or(()->get(s167.get("f"),"G-iron"),()->get(s167.get("f"),"G-spider"))))){
{ final Scope s169=new Scope(s167);
discard(put(s169.get("f"),"dimensionalMap",true));
discard(put(s169.get("f"),"S01",true));
}
}
}))));
}
if(always()) throw returned(obj(kv("lines",arr(call(s144.get("line"),args("Minha bicicleta ficou no ar. Depois caiu no parque, junto com coisas que eu nunca entreguei.")),call(s144.get("choice"),args("D01","Marcus aponta para o cordão policial.",call(s144.get("opt"),args("Eu trago sua bicicleta. Ninguém fica para trás.","Ele respira. “Obrigado. Foi perto da plataforma de manutenção do parque.”","H",null,fn(s144,(s170,a171)->{
discard(put(s170.get("f"),"bikePromise",true));
}))),call(s144.get("opt"),args("Preciso saber exatamente onde tudo caiu.","Marcus desenha o caminho: bodega, parque, estação 8th Ave.","C",null,fn(s144,(s172,a173)->{
discard(put(s172.get("f"),"routeKnown",true));
}))))))),kv("onFinish",call(s144.get("done"),args(s144.get("s"),"marcus")))));
}if(m158||equal(v157,"home")){m158=true;if(always()) throw returned(obj(kv("lines",arr(call(s144.get("line"),args(call(s144.get("protagonist"),args(s144.get("s"),obj(kv("spider","Tia May: “Se o céu cair, não carregue tudo sozinho. Trouxe seu equipamento.”"),kv("iron","Pepper: “Não confunda responsabilidade com fazer tudo sozinho. FRIDAY está pronta.”"),kv("captain","Sharon: “Um plano funciona melhor quando você volta para contar. Leve este escudo emprestado.”"),kv("custom","Alguém da sua casa deixou um bilhete: “Volte. Sua história ainda tem gente esperando.”")))))),call(s144.get("choice"),args("home-tool","Um escudo emprestado pode proteger a resistência.",call(s144.get("opt"),args("Vou levá-lo e devolvê-lo a quem precisar.","O escudo é guardado com cuidado.","H",null,fn(s144,(s174,a175)->{
discard(put(s174.get("f"),"borrowedShield",true));
}))),call(s144.get("opt"),args("Prefiro manter o equipamento aqui, por segurança.","Você sai com o aviso, sem levar o escudo.","C"))))))));
}if(m158||equal(v157,"alvarez")){m158=true;if(truth(get(s144.get("f"),"solved"))){
if(always()) throw returned(call(s144.get("talk"),args((truth(binary(">=",get(get(s144.get("s"),"relationships"),"alvarez"),1))?"Arquivei como terremoto. Eu sei que não foi. Você também. Obrigado por confiar antes de ter todas as respostas.":"O caso está encerrado. Você seguiu por sua conta; eu vou seguir pela minha."))));
}
if(truth(get(s144.get("f"),"fragment"))){
if(always()) throw returned(call(s144.get("talk"),args((truth(get(s144.get("f"),"runeFirst"))?"Você examinou a runa antes de tocar. Guardei o metal: veio de Latveria. Posso abrir um atalho da estação.":"Desde que tocou aquilo, as quedas parecem seguir você. Não gosto disso, mas vou registrar os fatos."),fn(s144,(s176,a177)->{
if(truth(binary(">=",get(get(s176.get("s"),"relationships"),"alvarez"),1))){
discard(put(s176.get("f"),"policeShortcut",true));
}
}))));
}
if(always()) throw returned(obj(kv("lines",arr(call(s144.get("choice"),args("alvarez","Não atravesse o cordão. Preciso entender os objetos antes de perder mais um no depósito.",call(s144.get("opt"),args("Vou cooperar. Mostre o que caiu primeiro.","Alvarez permite examinar a runa e registra seu nome.","H",null,fn(s144,(s178,a179)->{
throw returned(call(s178.get("trust"),args(s178.get("s"),"alvarez")));
}))),call(s144.get("opt"),args("O tempo importa mais que o cordão. Vou investigar.","Ele recua, contrariado. Você ganha passagem, sem a confiança do detetive.","C"))))))));
}if(m158||equal(v157,"friday")){m158=true;if(always()) throw returned(call(s144.get("talk"),args("A energia não aponta para um endereço. Ela acompanha o portador. O objeto escolheu você — isso não prova que você tenha escolhido o desastre.")));
}if(m158||equal(v157,"wong")){m158=true;if(always()) throw returned(call(s144.get("talk"),args(binary("+",(truth(get(s144.get("s"),"eco"))?binary("+",binary("+","Hoje o chá é ",(truth(binary("===",get(get(get(s144.get("s"),"eco"),"choices"),"D04"),0))?"de jasmim, como quando você quis reparar":"preto, como quando você pediu provas")),". Eu também lembro. "):(truth(binary("===",get(get(s144.get("s"),"choices"),"D04"),0))?"Jasmim para quem aceita reparar. ":(truth(binary("===",get(get(s144.get("s"),"choices"),"D04"),1))?"Chá preto para quem exige provas. ":"O chá é quente; o fim do mundo pode esperar um gole. "))),(truth(get(s144.get("f"),"strange"))?"Leve o Anel de Passagem ao portal, no extremo sudeste deste pátio.":"O espelho exige um teste, não uma confissão. Strange espera à frente.")),fn(s144,(s180,a181)->{
discard(put(s180.get("f"),"tea",true));
if(truth(get(s180.get("f"),"strange"))){
discard(put(s180.get("f"),"ring",true));
}
}))));
}if(m158||equal(v157,"okoye")){m158=true;if(always()) throw returned(call(s144.get("talk"),args((truth(get(s144.get("f"),"panther"))?"Sua luta respeitou a fronteira. Birnin Zana está aberta.":"Não confunda hospitalidade com ausência de limites. O rei ouvirá sua intenção."))));
}if(m158||equal(v157,"ayo")){m158=true;if(always()) throw returned(call(s144.get("talk"),args("O rasgo nasceu quando você chegou. Eu vi. E não consigo deixar de olhar para ele.")));
}if(m158||equal(v157,"mbaku")){m158=true;if(always()) throw returned(call(s144.get("talk"),args("Quatro cadeiras sustentavam uma mesa. Um rei levou todas para sua casa e se espantou quando ninguém mais pôde sentar. Não ria: é uma fábula, não uma previsão.")));
}if(m158||equal(v157,"nakia")){m158=true;if(always()) throw returned(call(s144.get("talk"),args("Esta flor encontra raízes mesmo quando o chão não reconhece seu nome. Leve uma flor e sementes: o mercado, Shuri e alguém sem casa podem precisar delas.",fn(s144,(s182,a183)->{
discard(put(s182.get("f"),"flower",true));
discard(put(s182.get("f"),"seeds",true));
}))));
}if(m158||equal(v157,"child")){m158=true;if(always()) throw returned(obj(kv("lines",arr(call(s144.get("choice"),args("market-child","Nuru perdeu a mãe junto à Fissura. A rota rápida passa longe daqui.",call(s144.get("opt"),args("Fico com você até Nakia chegar.","Nakia reúne a família. Natasha observa a sua escolha.","H",null,fn(s144,(s184,a185)->{
discard(call(s184.get("gesture"),args(s184.get("s"),"widow")));
discard(put(s184.get("f"),"childSafe",true));
}))),call(s144.get("opt"),args("Vou avisar a guarda e seguir pela rota rápida.","A guarda acolhe Nuru. Você segue sem esperar.","C"))))))));
}if(m158||equal(v157,"shuri")){m158=true;if(truth(unary("!",get(s144.get("f"),"marketOpen")))){
if(always()) throw returned(call(s144.get("talk"),args("Preciso que Homem-Aranha ou Viúva abandone as ordens de Fury antes de abrir a mina.")));
}
if(always()) throw returned(obj(kv("lines",arr(call(s144.get("line"),args("LAT-ETN. Não é Fury. É uma cabeça de Doombot com o rosto de Fury. O vibranium canta, só que alguém mudou a música.")),spread((truth(and(()->get(s144.get("f"),"flower"),()->unary("!",get(s144.get("f"),"G-panther"))))?arr(call(s144.get("choice"),args("flower-shuri","As flores de Nakia reagem à frequência.",call(s144.get("opt"),args("Entregar a flor antes de descer à mina.","Shuri estabiliza o jardim do laboratório. Pantera recebe sua mensagem.","H",null,fn(s144,(s186,a187)->{
discard(call(s186.get("gesture"),args(s186.get("s"),"panther")));
discard(put(s186.get("f"),"flowerGiven",true));
}))),call(s144.get("opt"),args("Guardar a flor para quem perdeu seu mundo.","Shuri usa os sensores. As sementes continuam com você.","C"))))):arr())),call(s144.get("line"),args("Leve o Cartão Dourado. A mina está aberta; mas desminta Homem-Aranha e Viúva antes de enfrentar quem controla o túnel.")))),kv("onFinish",call(s144.get("done"),args(s144.get("s"),"goldCard")))));
}if(m158||equal(v157,"banner")){m158=true;if(always()) throw returned(obj(kv("lines",arr(call(s144.get("line"),args("O som é um grito contínuo. Se eu cair, ele sai. Escute: DÓ, MI, SOL. Uma lembrança pode ser mais forte que a frequência.")),call(s144.get("choice"),args("D06","Bruce tenta manter as mãos firmes.",call(s144.get("opt"),args("Eu fico. Você não precisa segurar isso sozinho.","Bruce canta a nota baixa. Hulk ainda pode lembrar.","H","hulk",fn(s144,(s188,a189)->{
discard(put(s188.get("f"),"musicLesson",true));
}))),call(s144.get("opt"),args("Diga como desligar o amplificador.","Bruce aponta seus registros. A frequência alimenta um Núcleo em outro mundo.","C",null,fn(s144,(s190,a191)->{
throw returned(call(s190.get("collectClue"),args(s190.get("s"),"FV8")));
}))))))),kv("onFinish",call(s144.get("done"),args(s144.get("s"),"banner")))));
}if(m158||equal(v157,"heimdall")){m158=true;if(truth(and(()->get(s144.get("f"),"libraryBook"),()->unary("!",get(s144.get("f"),"G-marvel"))))){
if(always()) throw returned(call(s144.get("gesturePrompt"),args(s144.get("s"),"marvel","Eir devolveu o livro emprestado ao Observatório.","Devolver a Heimdall.",true,fn(s144,(s192,a193)->{
discard(put(s192.get("f"),"S06",true));
}))));
}
if(always()) throw returned(call(s144.get("talk"),args((truth(get(s144.get("f"),"marvel"))?"Minha visão volta aos caminhos. Odin dorme desde que o Coração desapareceu. Sif e Frigga podem guiá-lo.":"Vejo os mundos, mas não vejo você. Não atravessará a quarentena sem Carol."))));
}if(m158||equal(v157,"sif")){m158=true;if(always()) throw returned(call(s144.get("talk"),args("Odin dorme desde o silêncio das runas. Fale com Frigga no vestíbulo do Salão, a oeste. A Biblioteca guarda a chave da Cripta.",call(s144.get("done"),args(s144.get("s"),"sif")))));
}if(m158||equal(v157,"volstagg")){m158=true;if(always()) throw returned(obj(kv("lines",arr(call(s144.get("choice"),args("volstagg","Uma mesa grande demais para um único mundo. Javali?",call(s144.get("opt"),args("Sentar e ouvir a história da Cripta.","“Hela guardou o que escondemos. Quatro cadeiras vazias. Uma palavra na pedra.”","H",null,fn(s144,(s194,a195)->{
discard(put(s194.get("f"),"S07",true));
}))),call(s144.get("opt"),args("Perguntar por uma rota segura.","“Sif, Frigga, Eir. Nessa ordem — e leve comida.”","C"))))))));
}if(m158||equal(v157,"frigga")){m158=true;if(always()) throw returned(call(s144.get("talk"),args((truth(get(s144.get("f"),"eir"))?"Eir confiou a chave. Meu recado é este: não confunda a voz que conta mundos com a voz que quer salvá-los. No teto, o robô traz o selo de Latveria.":"Há um Doombot no teto do Salão. Ele já observava Odin antes de você chegar. Eir guarda o caminho para a Cripta."),fn(s144,(s196,a197)->{
discard(put(s196.get("f"),"frigga",true));
discard(call(s196.get("refreshProgress"),args(s196.get("s"))));
if(truth(get(s196.get("f"),"eir"))){
discard(put(s196.get("f"),"friggaMessage",true));
}
}))));
}if(m158||equal(v157,"eir")){m158=true;if(always()) throw returned(call(s144.get("talk"),args("Silêncio. O texto está aqui: “O Titã contou os mundos.” Leve a Chave da Cripta e devolva este livro ao Observatório. A cripta mostra mais a quem lê antes de entrar.",fn(s144,(s198,a199)->{
discard(put(s198.get("f"),"eir",true));
discard(put(s198.get("f"),"cryptKey",true));
discard(put(s198.get("f"),"libraryBook",true));
discard(call(s198.get("refreshProgress"),args(s198.get("s"))));
}))));
}if(m158||equal(v157,"odin")){m158=true;if(truth(unary("!",get(s144.get("f"),"thor")))){
if(always()) throw returned(call(s144.get("talk"),args("O rei permanece adormecido. Quatro runas cercam o trono, mas nenhuma emite luz.")));
}
if(always()) throw returned(call(s144.get("talk"),args((truth(get(s144.get("f"),"R3"))?"O Titã contou mundos para escolher o mais populoso. Ele chama isso de misericórdia cósmica. A Conta não salvará ninguém. O Bifrost para a Encruzilhada está aberto.":"Há um Titã por trás da reunião dos mundos. Sua “misericórdia” é uma ameaça. Procure os refugiados da Encruzilhada; o Bifrost está aberto."),call(s144.get("done"),args(s144.get("s"),"odinAwake")))));
}if(m158||equal(v157,"rocket")){m158=true;if(always()) throw returned(call(s144.get("talk"),args((truth(binary(">=",or(()->get(s144.get("f"),"rocketVisits"),()->0),1))?"Já voltou? Se juntou os oito primeiros registros, a Estação Pym abre a noroeste da Cantina. O pequeno conhece máquinas que não deveriam existir.":"Três alas. Recompensas na Cantina, refugiados ao sul, um mundo morto ainda mais ao sul. Explore duas antes de subir à Torre."),fn(s144,(s200,a201)->{
discard(put(s200.get("f"),"rocketVisits",binary("+",or(()->get(s200.get("f"),"rocketVisits"),()->0),1)));
discard(call(s200.get("refreshProgress"),args(s200.get("s"))));
}))));
}if(m158||equal(v157,"groot")){m158=true;if(always()) throw returned(call(s144.get("talk"),args((truth(get(s144.get("s"),"eco"))?"“Eu sou Groot.” Rocket traduz: “Uma árvore reconhece quem já voltou de um fim.” Na primeira visita eu achei que ele estava falando de água.":"“Eu sou Groot.” Rocket: “Ele quer saber se você pretende devolver os mundos às raízes deles.”"),fn(s144,(s202,a203)->{
if(truth(get(s202.get("s"),"eco"))){
discard(put(s202.get("f"),"grootEco",true));
}
}))));
}if(m158||equal(v157,"oru")){m158=true;if(always()) throw returned(obj(kv("lines",arr(call(s144.get("choice"),args("oru","Tenho suprimentos, e histórias que custam uma moeda de outro mundo.",call(s144.get("opt"),args("Os suprimentos são para os refugiados.","Oru entrega uma caixa. “Pague contando que eles sobreviveram.”","H",null,fn(s144,(s204,a205)->{
discard(put(s204.get("f"),"supplies",true));
discard(put(s204.get("f"),"S10",true));
}))),call(s144.get("opt"),args("Quero uma pista sobre a Estação Pym.","“Rocket fala duas vezes antes de entregar um segredo. Oito registros abrem a porta.”","C",null,fn(s144,(s206,a207)->{
discard(put(s206.get("f"),"pymRumor",true));
})))))))));
}if(m158||equal(v157,"vigia")){m158=true;if(always()) throw returned(call(s144.get("talk"),args("Rota estável: concluir quaisquer duas alas libera a Torre. A terceira continua disponível. A Estação Pym exige FV1 a FV8 e duas conversas com Rocket.")));
}if(m158||equal(v157,"tessel")){m158=true;if(always()) throw returned(call(s144.get("talk"),args((truth(get(s144.get("f"),"gamora"))?"A gravação da Gamora ainda está perto da mesa. Não deixe a Conta virar só um rumor.":"O cartaz é falso. A bebida é verdadeira. Star-Lord confundiu as duas coisas quando pediu o pagamento."))));
}if(m158||equal(v157,"refugee")){m158=true;if(always()) throw returned(call(s144.get("talk"),args((truth(get(s144.get("f"),"solved"))?"Minha filha perguntou se podemos voltar a chamar o chão de casa. Eu disse que sim.":"O céu que conhecíamos já não existe. Magneto acha que esse cristal pode abrir uma porta. Não conte que eu chorei."))));
}if(m158||equal(v157,"breach")){m158=true;{ final Scope s208=new Scope(s144);
s208.let("cast",call(s208.get("breachCast"),args(s208.get("s"))));
if(always()) throw returned(obj(kv("lines",arr(call(s208.get("line"),args(binary("+",binary("+","A muralha cede. ",method(method(s208.get("cast"),"map",args(fn(s208,(s209,a210)->{
s209.let("p",argument(a210,0));
throw returned(binary("+",or(()->or(()->or(()->get(s209.get("p"),"name"),()->get(s209.get("names"),get(s209.get("p"),"id"))),()->get(method(s209.get("NPCS"),"find",args(fn(s209,(s211,a212)->{
s211.let("n",argument(a212,0));
throw returned(binary("===",get(s211.get("n"),"id"),get(s211.get("p"),"id")));
}))),"name")),()->get(s209.get("p"),"id")),(truth(get(s209.get("p"),"full"))?" está ao seu lado":" aguarda em silhueta")));
}))),"join",args("; "))),"."))),call(s208.get("line"),args("Homem-Aranha protege a retaguarda. Stark rompe o metal; Strange abre a brecha; Pantera guarda o flanco. Hulk segura a porta, Viúva identifica as ordens falsas, o Capitão cobre a passagem e Thor chama o trovão. Os demais aliados orientam a evacuação ou enviam informações; não participam do combate.")))),kv("onFinish",call(s208.get("done"),args(s208.get("s"),"breach")))));
}
}if(m158||equal(v157,"boris")){m158=true;if(always()) throw returned(call(s144.get("talk"),args((truth(get(s144.get("f"),"FV12"))?"Cynthia. Esse era o nome dela. Ele não está construindo um trono: está tentando recuperar uma mãe. O que perdeu não lhe dá direito de nos perder também.":"Servi um país. Agora sirvo seu reflexo. A cidade obedece porque já não lembra como discordar."))));
}if(m158||equal(v157,"cynthia")){m158=true;if(always()) throw returned(obj(kv("lines",arr(call(s144.get("choice"),args("cynthia-truth","O Eco reconhece o selo da Terra-Esmaecida. “O que Victor fez?”",call(s144.get("opt"),args("Outro mundo morreu no primeiro teste. Você merece saber.","“Então ele ainda não aprendeu a perder. Não deixe que me traga de volta assim.”","H")),call(s144.get("opt"),args("Ele está tentando salvar Latveria. Vou impedir o sacrifício.","“Uma mãe não pede mundos em troca de um filho.”","C"))))))));
}}catch(Break b){}}
if(truth(method(s144.get("ENCOUNTERS"),"some",args(fn(s144,(s213,a214)->{
s213.let("e",argument(a214,0));
throw returned(binary("===",get(s213.get("e"),"id"),s213.get("id")));
}))))){
{ final Scope s215=new Scope(s144);
if(truth(unary("!",call(s215.get("encounterAllowed"),args(s215.get("s"),s215.get("id")))))){
if(always()) throw returned(call(s215.get("talk"),args(binary("+","Ainda falta um passo da investigação. ",call(s215.get("blockedText"),args(s215.get("id"),s215.get("s")))))));
}
if(always()) throw returned(call(s215.get("encounterScript"),args(s215.get("n"),s215.get("s"))));
}
}
if(always()) throw returned(call(s144.get("talk"),args("Esta parte do Tecido guarda uma história que ainda não foi revelada.")));
}));
}
private void install_blockedText(){global.let("blockedText",fn(global,(s216,a217)->{
s216.let("id",argument(a217,0));
s216.let("s",argument(a217,1));
if(always()) throw returned(or(()->get(obj(kv("strange","Recupere o fragmento menor após deter o agente que vigia as antenas."),kv("killmonger","Desminta Homem-Aranha e Viúva, e peça o Cartão Dourado a Shuri."),kv("hulk","Ouça Bruce Banner antes do confronto."),kv("leader","Logan guarda a prova do primeiro teste. Confronte-o e investigue o impostor com Visão."),kv("vision","Complete duas das três alas."),kv("thanos","Destino caiu, mas você ainda precisa decidir se aceita sua ajuda."),kv("antman","Rocket e os oito primeiros registros guardam o acesso.")),s216.get("id")),()->"Siga o objetivo indicado no dossiê."));
}));
}
private void install_narrativeScene(){global.let("narrativeScene",fn(global,(s218,a219)->{
s218.let("n",argument(a219,0));
s218.let("s",argument(a219,1));
s218.let("f",get(s218.get("s"),"flags"));
s218.let("id",get(s218.get("n"),"id"));
s218.let("required",obj(kv("venom",get(s218.get("f"),"fragment")),kv("goblin",get(s218.get("f"),"defender")),kv("hawkeye",get(s218.get("f"),"panther")),kv("killmonger",and(()->get(s218.get("f"),"goldCard"),()->get(s218.get("f"),"M08"))),kv("loki",get(s218.get("f"),"hulk")),kv("hela",get(s218.get("f"),"cryptKey")),kv("starlord",get(s218.get("f"),"odinAwake")),kv("gamora",get(s218.get("f"),"starlord")),kv("magneto",get(s218.get("f"),"odinAwake")),kv("vision",get(s218.get("f"),"wingsOpen")),kv("ultron",get(s218.get("f"),"breach")),kv("antman",get(s218.get("f"),"pymOpen"))));
if(truth(unary("!",get(s218.get("required"),s218.get("id"))))){
if(always()) throw returned(call(s218.get("talk"),args(binary("+","Ainda falta um passo da investigação. ",call(s218.get("blockedText"),args(s218.get("id"),s218.get("s")))))));
}
s218.let("d",fn(s218,(s220,a221)->{
s220.let("code",argument(a221,0));
s220.let("text",argument(a221,1));
s220.let("a",argument(a221,2));
s220.let("ar",argument(a221,3));
s220.let("b",argument(a221,4));
s220.let("br",argument(a221,5));
throw returned(call(s220.get("choice"),args(s220.get("code"),s220.get("text"),call(s220.get("opt"),args(s220.get("a"),s220.get("ar"),"H",s220.get("id"))),call(s220.get("opt"),args(s220.get("b"),s220.get("br"),"C",s220.get("id"))))));
}));
s218.let("scenes",obj(kv("venom",arr(call(s218.get("line"),args("Eddie: “Quatro. Quatro vozes e um homem de metal. A máquina na plataforma faz a Fissura gritar.”")),call(s218.get("d"),args("D02","O simbionte pede ajuda; a patrulha de Destino bloqueia a saída.","Eddie, eu vou ouvir. Não precisa ferir ninguém.","Ele registra as quatro vozes. A pista fica junto aos trilhos.","Vou medir a frequência e desligar a máquina.","Eddie aponta o Doombot. O sinal vem dele, não do simbionte.")),call(s218.get("line"),args("Venom fica com os passageiros. O obstáculo de combate é o Doombot de Patrulha, a leste.")))),kv("goblin",arr(call(s218.get("line"),args("Duende: “O consultor prometeu que eu controlaria a Fissura. Agora o agente dele quer apagar o contrato.”")),call(s218.get("d"),args("goblin-intent","O acordo V. von D. está sobre a mesa. Um capanga guarda a antena.","Você foi usado. Mostre quem assinou.","Ele entrega o rastro do contrato e aponta o homem de Latveria.","Vou rastrear a alimentação antes de retirar o fragmento.","O Duende revela o circuito. O capanga tentará destruir as provas.")),call(s218.get("line"),args("O Duende recua da operação. Detenha o agente, então recolha o fragmento menor.")))),kv("hawkeye",arr(call(s218.get("line"),args("Clint abaixa o arco: “Fury ordenou a captura. Só que ninguém viu Fury chegar.”")),call(s218.get("d"),args("hawkeye-order","A ordem falsa pode ser investigada sem ferir Clint.","Os moradores precisam de nós. Vamos conferir juntos.","Clint entrega a cópia da mensagem e mantém as ruas livres.","Confira a assinatura antes de obedecer.","Clint encontra um eco metálico. Aranha e Natasha ainda precisam verificar o portador.")),call(s218.get("line"),args("Homem-Aranha e Viúva fazem a contenção no mercado; Clint ajuda a reunir a evidência.")))),kv("killmonger",arr(call(s218.get("line"),args("O Eco encara os vagões: “No meu mundo eu venci. O Monarca prometeu que esse vibranium me devolveria uma casa.”")),call(s218.get("d"),args("killmonger-intent","Os Doombots retiram o Vibranium Mãe, sem cumprir o acordo.","Ele usa sua perda para roubar este povo.","O Eco entrega os registros de extração. Não quer outro mundo vazio.","Compare o contrato com os vagões: você não controla nada.","O selo do Monarca está em cada carga. O Eco revela o túnel B.")),call(s218.get("line"),args("Killmonger desliga o alarme. O Doombot de Extração continua protegendo o saque; enfrente a máquina, não o Eco.")))),kv("loki",arr(call(s218.get("line"),args("Pedágio, querido. Moedas, segredos ou um pouco de dignidade.")),spread((truth(and(()->and(()->get(s218.get("f"),"FV3"),()->get(s218.get("f"),"FV7")),()->get(s218.get("f"),"FV4")))?arr(call(s218.get("line"),args("Os registros convergem: “O Monarca é Victor von Doom.” Loki para de sorrir."))):arr())),call(s218.get("choice"),args("D07","A ponte está desalinhada. Loki conhece a ordem das runas.",call(s218.get("opt"),args("Tenho medo. Ainda assim, vou ajudar a reconstruí-la.","Você alinha as runas enquanto Loki sustenta a ponte. “O Titã se chama Thanos.”","H","loki")),call(s218.get("opt"),args((truth(get(s218.get("f"),"coin"))?"Pagar o pedágio simbólico e exigir a rota segura.":"Negociar uma rota sem entregar o Fragmento."),"Loki aceita. As runas se alinham; o preço é levar adiante o nome Thanos.","C","loki",fn(s218,(s222,a223)->{
if(truth(get(s222.get("f"),"coin"))){
discard(call(s222.get("gesture"),args(s222.get("s"),"loki")));
}
}))))),call(s218.get("line"),args("O caminho até o Observatório está reparado. Não houve duelo: o perigo era a ponte.")))),kv("hela",arr(call(s218.get("line"),args("Eu cuidei do que vocês esconderam. O Coração está preso a um mecanismo que rouba sua luz.")),call(s218.get("d"),args("hela-heart","Hela pode abrir as correntes se você assumir o risco de levar o Coração.","Asgard precisa dele. Não vou negar sua ferida.","Hela abre a cripta e acompanha a retirada pelas runas.","Isole o mecanismo. Vou impedir que o Núcleo receba mais luz.","Você corta o circuito indicado por Hela. As correntes cedem.")),call(s218.get("line"),args("Hela entrega o Coração do Bifrost e permanece na Cripta. Thor exigirá verificar sua integridade.")))),kv("starlord",arr(call(s218.get("line"),args("Sabe quanto estão pagando por você? Muito. Tessel acabou de dizer que o cartaz é falso.")),call(s218.get("d"),args("starlord-bounty","A recompensa usa o mesmo selo do contrato da Oscorp.","Olhe os habitantes antes de olhar o preço.","Peter desiste da captura e aponta a mesa de Gamora.","Compare as assinaturas. Estão tentando separar nossos aliados.","Star-Lord encontra o selo de Latveria na impressão. Ninguém aqui precisa lutar.")),call(s218.get("line"),args("A investigação da Cantina continua com Gamora e a gravação do Titã.")))),kv("gamora",arr(call(s218.get("line"),args("Esse cristal estava nas medições dele. Eu guardei uma gravação, não uma ordem para capturar você.")),call(s218.get("d"),args("D10","Gamora precisa decidir se compartilha a prova da Conta.","Mostre a prova. Eu vou ouvir.","Ela conta sobre os habitantes que Thanos transformou em números.","Guarde as armas. Vamos comparar os registros.","Os registros confirmam o plano. Gamora disponibiliza a gravação.")),call(s218.get("line"),args("Thanos quer fundir os mundos para reunir o maior número de habitantes antes da Conta. Gamora ajudará na evacuação.")))),kv("magneto",arr(call(s218.get("line"),args("O cristal parece a última saída deste povo. Não vou entregá-lo ao Monarca, nem tomar sua decisão pela força.")),call(s218.get("d"),args("D11","Ada e sua filha esperam uma rota segura.","Usá-lo sem controle pode matar alguns. Dá-lo ao Monarca mata todos.","Magneto aceita proteger a evacuação enquanto você procura o Núcleo.","Vamos mapear outra rota antes de fechar essa porta.","Vocês verificam o mapa dos refugiados. A rota da Torre pode trazer ajuda.")),call(s218.get("line"),args("A barricada abre para passagem e suprimentos. A esperança não depende de derrotar seu guardião.")))),kv("vision",arr(call(s218.get("line"),args("Uma cópia do seu rosto tentou desligar os sensores há dois minutos. O emissor ficou gravado.")),call(s218.get("d"),args("D13","Visão compara a gravação do impostor com o Fragmento verdadeiro.","Um Doombot usou meu rosto. Vou mostrar meu percurso.","As marcas da viagem contradizem o horário da cópia. Visão identifica o selo de Destino.","Faça uma pergunta que somente eu possa responder.","Sua resposta sobre o Fragmento diverge do roteiro gravado no Doombot.")),call(s218.get("line"),args("O impostor era uma máquina de Destino. Visão informa que Wanda foi capturada e fica organizando comunicações; o Capitão fará o duelo de preparação.")))),kv("ultron",arr(call(s218.get("line"),args("Um registro antigo de Ultron ocupa o emissor: “Destino esqueceu de me incluir como variável. A fusão destruiria até as máquinas.”")),call(s218.get("d"),args("armory-record","O arquivo revela o circuito que drena Wanda.","Copiar os registros para que ela possa ser libertada.","Você identifica o colar de Domínio e o Laboratório dos Pilares.","Localizar o bloqueio antes de cortar a alimentação.","O arquivo aponta um Doombot de Convergência protegendo o circuito.")),call(s218.get("line"),args("Ultron não está no Salão. O registro termina; a máquina de Destino, a leste, impede a passagem.")))),kv("antman",arr(call(s218.get("line"),args("Rocket enviou os oito registros? Então podemos comparar o sinal da cópia sem qualquer duelo.")),call(s218.get("d"),args("pym-intent","Pym calibra o Estabilizador para isolar o colar de Wanda.","Quero ajudá-la sem deixar ninguém para trás.","Ele ajusta a frequência e confia o aparelho a você.","Confira meu rastro nos oito registros.","As leituras confirmam sua identidade. O Estabilizador está pronto.")),call(s218.get("line"),args("S-11 · Investigação concluída. O Estabilizador Pym acompanha você; Homem-Formiga permanece no laboratório."))))));
if(always()) throw returned(obj(kv("lines",get(s218.get("scenes"),s218.get("id"))),kv("onFinish",fn(s218,(s224,a225)->{
discard(put(s224.get("f"),s224.get("id"),true));
if(truth(binary("===",s224.get("id"),"hela"))){
discard(put(s224.get("f"),"heart",true));
}
if(truth(binary("===",s224.get("id"),"loki"))){
discard(put(s224.get("f"),"bridgeFixed",true));
}
if(truth(binary("===",s224.get("id"),"antman"))){
{ final Scope s226=new Scope(s224);
discard(put(s226.get("f"),"stabilizer",true));
discard(put(s226.get("f"),"S11",true));
}
}
discard(call(s224.get("remember"),args(s224.get("s"),binary("+","scene-",s224.get("id")),binary("+","Investigação · ",get(s224.get("n"),"name")),"Encontro resolvido por diálogo e evidências, sem batalha.")));
discard(call(s224.get("refreshProgress"),args(s224.get("s"))));
}))));
}));
}
private void install_encounterScript(){global.let("encounterScript",fn(global,(s227,a228)->{
s227.let("n",argument(a228,0));
s227.let("s",argument(a228,1));
s227.let("f",get(s227.get("s"),"flags"));
s227.let("id",get(s227.get("n"),"id"));
s227.let("ally",(truth(binary("===",s227.get("id"),"defender"))?call(s227.get("slotIdentity"),args(s227.get("id"),get(s227.get("s"),"character"))):(truth(binary("===",s227.get("id"),"leader"))?call(s227.get("slotIdentity"),args(s227.get("id"),get(s227.get("s"),"character"))):s227.get("id"))));
s227.let("d",fn(s227,(s229,a230)->{
s229.let("code",argument(a230,0));
s229.let("text",argument(a230,1));
s229.let("a",argument(a230,2));
s229.let("ar",argument(a230,3));
s229.let("b",argument(a230,4));
s229.let("br",argument(a230,5));
throw returned(call(s229.get("choice"),args(s229.get("code"),s229.get("text"),call(s229.get("opt"),args(s229.get("a"),s229.get("ar"),"H",s229.get("ally"))),call(s229.get("opt"),args(s229.get("b"),s229.get("br"),"C",s229.get("ally"))))));
}));
s227.let("lines",or(()->get(obj(kv("station-guard",arr(call(s227.get("line"),args("O Doombot captura as vozes dos Pilares para localizar o Fragmento. Os passageiros não conseguem atravessar a plataforma.")),call(s227.get("line"),args("Venom revelou a fonte. Desativar a patrulha interromperá o sinal e abrirá a Torre Stark.")))),kv("osc-guards",arr(call(s227.get("line"),args("O agente de Latveria recebeu ordens de destruir o contrato e recolher o fragmento menor.")),call(s227.get("d"),args("osc-agent","O Duende revelou o circuito das antenas.","Vou preservar as provas e impedir outro desaparecimento.","O agente bloqueia a antena. Você protege o contrato.","Primeiro corto a rota de fuga do agente.","Ele arma a contenção sobre o fragmento.")))),kv("spider",arr(call(s227.get("line"),args((truth(get(s227.get("f"),"widow"))?"Natasha identificou a ordem falsa. Preciso garantir que o Fragmento não vai atingir os moradores.":"Clint encontrou uma falha, mas a ordem de Fury ainda manda conter o portador."))),call(s227.get("d"),args("spider-order","Aranha guarda o acesso ao mercado enquanto a Fissura cresce.","Fico por quem está embaixo. Vamos testar o risco sem envolver moradores.","Aranha escolhe um espaço vazio para o teste de contenção.","Compare a transmissão; depois teste o que o Fragmento faz.","A assinatura é falsa. O teste verificará a segurança da passagem.")))),kv("mine-guard",arr(call(s227.get("line"),args("O Doombot de Extração mantém os vagões carregando o Pilar II. O Eco desligou o alarme, mas não controla a máquina.")),call(s227.get("line"),args("Interrompa a escolta para impedir que o Monarca drene o Vibranium Mãe.")))),kv("armory-guard",arr(call(s227.get("line"),args("O Doombot de Convergência redireciona energia ao colar de Wanda. O registro identificou seu circuito.")),call(s227.get("line"),args("Desative o guardião para alcançar o Laboratório dos Pilares antes que o Núcleo consuma sua prisioneira.")))),kv("defender",arr(call(s227.get("line"),args((truth(binary("===",s227.get("ally"),"iron"))?"FRIDAY escaneia o Fragmento. Stark: “Seu bolso emite um sinal que eu nunca vi. E eu vi tudo.”":"Aranha: “Esse cristal está causando blecautes no bairro. Preciso ter certeza de que você consegue segurá-lo.”"))),call(s227.get("d"),args("D03","O defensor prepara um teste armado.","Ele me escolheu. Não sei por quê, mas não vou fugir.","Honesto. Isso ainda não é uma prova.","Teste o que precisar. Só me avise antes de disparar.","Um plano e limites. Agora podemos testar.")))),kv("strange",arr(call(s227.get("line"),args("Wong serve chá. Strange: “A Fissura trouxe você, ou você trouxe a Fissura?”")),spread((truth(get(s227.get("s"),"eco"))?arr(call(s227.get("line"),args(binary("+",binary("+","Eu já te vi escolher ",(truth(binary("===",get(get(get(s227.get("s"),"eco"),"choices"),"D04"),0))?"reparar sem fugir":"exigir provas")),". Hoje vai decidir diferente?")))):arr())),call(s227.get("d"),args("D04","O espelho pode testar sua identidade.","Se causei isso, quero reparar. Não vou fugir.","O espelho reconhece alguém capaz de responder.","Preciso de provas antes de aceitar a culpa.","Então o espelho será nossa prova.")),call(s227.get("line"),args(call(s227.get("protagonist"),args(s227.get("s"),obj(kv("spider","Uma máscara esconde meu rosto, não a responsabilidade por quem está embaixo."),kv("iron","Eu construo respostas. Hoje posso estar carregando a pergunta."),kv("captain","Um teste não muda o motivo pelo qual eu fico."),kv("custom","Minha aparência não explica de onde veio este objeto.")))))))),kv("panther",arr(call(s227.get("line"),args("Quem cruza minha fronteira traz uma intenção. A sua trouxe um rasgo no céu.")),call(s227.get("d"),args("D05","Okoye observa o desafio ritual.","Trouxe um problema. Também posso ajudar a reparar.","Então prove sua honra na fronteira.","Posso provar sem lutar. Vamos medir a Fissura.","Sem o desafio, não. A fronteira exige responsabilidade.")))),kv("widow",arr(call(s227.get("line"),args((truth(get(s227.get("f"),"spider"))?"Aranha me mostrou a falha. Ainda vou verificar você pessoalmente.":"Você corre bem demais para ser inocente. O Fury da transmissão disse seu nome."))),call(s227.get("d"),args("widow-order","Natasha corta sua rota no mercado.","Vou ficar. Os moradores não devem correr por minha causa.","Ela mede a distância, sem baixar a guarda.","Rastreie a transmissão. Não é a voz real de Fury.","Se eu estiver errada, quero provas.")))),kv("hulk",arr(call(s227.get("line"),args("HULK NÃO QUER OUVIR! O vibranium responde ao grito de Bruce.")),call(s227.get("line"),args("O confronto interromperá a frequência. As três notas de Bruce continuam no dossiê.")))),kv("marvel",arr(call(s227.get("line"),args("O que você carrega pode contaminar Asgard. Heimdall nem consegue vê-lo.")),call(s227.get("d"),args("D08","A quarentena fecha o Observatório.","Deixe-me mostrar o que descobri.","Carol precisa ver se você suporta o risco.","Aceito vigilância e restrições enquanto atravesso.","Você entende prevenção. Agora prove que a contenção funciona.")))),kv("thor",arr(call(s227.get("line"),args("Você segura o coração do meu povo. Explique-se. Rápido.")),call(s227.get("choice"),args("D09","Hela liberou as correntes; Thor vê o Coração em suas mãos.",call(s227.get("opt"),args("Hela me confiou o Coração. Tome-o antes de exigir.","Thor reconhece o gesto, mas precisa provar que a Fissura não o corrompeu.","H","thor",fn(s227,(s231,a232)->{
throw returned(call(s231.get("gesture"),args(s231.get("s"),"thor")));
}))),call(s227.get("opt"),args("Se eu soltar agora, podemos perder todos os mundos. Venha conferir.","O martelo se ergue. “Então eu mesmo conferirei.”","C","thor")))),call(s227.get("line"),args(call(s227.get("protagonist"),args(s227.get("s"),obj(kv("spider","Não vim roubar um coração. Vim impedir que parem todos eles."),kv("iron","Um núcleo seguro exige teste. Eu entendo. Só não temos tempo infinito."),kv("captain","Eu devolveria meu escudo por esse povo. O Coração também."),kv("custom","Seu reino é uma das casas que ainda podemos salvar.")))))))),kv("wolverine",arr(call(s227.get("line"),args((truth(get(s227.get("f"),"FV11"))?"Você já sabe da Conta. Logan reconhece a gravação: “Ele está fazendo de novo. Cheira igual ao último dia.”":"Cheira igual. Igual ao dia em que meu mundo acabou."))),call(s227.get("line"),args("Logan bloqueia a passagem: “Esse Fragmento tem o cheiro do último dia. Não vou deixar que você repita o que fizeram à Terra-Esmaecida sem ter certeza de quem é.”")),call(s227.get("d"),args("D12","O parque não tem mais vozes de crianças.","Sinto muito. Não vou deixar que se repita.","Logan olha o chão pela primeira vez.","Mostre as provas. Preciso saber quem fez isso.","O selo está no poste. Mas primeiro atravesse a lembrança comigo.")))),kv("leader",arr(call(s227.get("line"),args("Destino aparece em holograma: “Entregue o Fragmento. Seu universo permanecerá.” Uma silhueta roxa apaga o resto da Terra-Esmaecida.")),call(s227.get("line"),args((truth(binary("===",s227.get("ally"),"captain"))?"Eu confio em você. Por isso preciso ver até onde você vai. Se passa por mim, passa por ele.":"Preciso de você inteiro. Não vou deixar que atravesse Latveria sem cobertura."))),call(s227.get("d"),args("D14","A resistência quer garantir que você não partirá sozinho.","Mesmo sem cobertura, vou continuar por quem ficou.","O líder mede sua convicção com um duelo.","Dê uma hora para um plano. Podemos reduzir as perdas.","Ele abre os mapas, mas precisa testar sua resposta ao risco.")),call(s227.get("line"),args(call(s227.get("protagonist"),args(s227.get("s"),obj(kv("spider","Sempre existe alguém embaixo. Não posso fechar o céu só sobre mim."),kv("iron","Se vou consertar o Núcleo, preciso de gente capaz de me parar."),kv("captain","Um plano só vale se não deixar ninguém fora dele."),kv("custom","Meu universo não é o único lugar com gente esperando voltar.")))))))),kv("scarlet",arr(call(s227.get("line"),args("Eu... não... consigo parar.")),call(s227.get("d"),args("D16","O colar rúnico obriga Wanda a defender os Pilares.","Vou quebrar o colar. Você não é uma arma.","Wanda reconhece uma voz além do Domínio.","Mostre o ponto de controle. Vou cortar a corrente.","O colar responde ao cálculo, apertando mais uma vez.")),spread((truth(get(s227.get("f"),"stabilizer"))?arr(call(s227.get("line"),args("O Estabilizador Pym isola o colar. A cena de libertação é mais curta, sem alterar o combate deste Web."))):(truth(binary("===",get(get(s227.get("s"),"relationships"),"vision"),2))?arr(call(s227.get("line"),args("Visão envia sua voz e a análise do colar por um canal remoto. Wanda reconhece a lembrança; ele permanece junto aos refugiados, sem participar da batalha."))):arr()))))),kv("doom",arr(call(s227.get("line"),args("O Tecido devora mundos. Eu construí um Núcleo que reúne os quatro Pilares: Terra-Primária, Vibranium Mãe, Coração do Bifrost e Sol Negro.")),call(s227.get("line"),args("Minha Latveria morreu numa incursão. Meu povo. Cynthia. Tudo. Se fundir os mundos, posso trazê-los de volta.")),spread((truth(get(s227.get("f"),"R6"))?arr(call(s227.get("line"),args("Você acusa com fatos: o contrato da Oscorp, o Pilar II, a Conta e o selo na Terra-Esmaecida. A primeira tentativa matou um universo inteiro."))):arr(call(s227.get("line"),args("Há um mundo morto por trás de sua promessa. Você não aceitará que outro pague por ela."))))),call(s227.get("d"),args("D17","Destino segura o último Pilar.","Sua dor não lhe dá direito de repetir o fim.","A culpa atravessa a máscara. O orgulho continua de pé.","Seu cálculo falhou antes. Não vou permitir outra tentativa.","Ele responde que agora tem energia de um Titã.")),call(s227.get("line"),args(call(s227.get("protagonist"),args(s227.get("s"),obj(kv("spider","Você chamou isso de salvar. Eu vi quem ficou embaixo."),kv("iron","Eu sei como é querer consertar tudo. Não aceito transformar gente em peças."),kv("captain","Perder uma casa não transforma as outras em território seu."),kv("custom",binary("+",get(get(s227.get("s"),"character"),"name")," não entregará um mundo vivo para ressuscitar um mundo morto."))))))))),kv("thanos",arr(call(s227.get("line"),args("Destino achou que usava minha energia. Eu estava reunindo habitantes em um único mundo. Agora vou concluir A Conta.")),call(s227.get("line"),args(call(s227.get("protagonist"),args(s227.get("s"),obj(kv("spider","Mundos não são números. Nem as pessoas que você contou."),kv("iron","Você calculou o mundo mais populoso. Eu vou desfazer sua equação."),kv("captain","Ninguém aqui será metade de nada."),kv("custom","Meu universo não é um número. Nenhum deles é.")))))),call(s227.get("line"),args((truth(get(s227.get("f"),"Destino_A"))?"Destino dispara um raio e abre uma brecha. Esse apoio é entregue ao sistema de batalha futuro como evento de cena.":"Você enfrenta o Titã sem a ajuda de Destino."))),call(s227.get("line"),args("No momento decisivo, o Fragmento pulsa. Você o insere para REVERTER, não completar o Núcleo. O ponto de cena a 30% de vida está preparado para a futura integração."))))),s227.get("id")),()->arr(call(s227.get("line"),args("Um confronto guarda o próximo passo da investigação.")))));
s227.let("marco",get(method(s227.get("ENCOUNTERS"),"find",args(fn(s227,(s233,a234)->{
s233.let("e",argument(a234,0));
throw returned(binary("===",get(s233.get("e"),"id"),s233.get("id")));
}))),"mark"));
s227.let("echoes",obj(kv("strange",obj(kv("spider","O espelho reconhece quem estava atrás da máscara antes da Fissura."),kv("iron","Até uma armadura precisa reconhecer o homem dentro dela."),kv("captain","O espelho pode copiar um escudo. Ainda não aprendeu a copiar uma causa."))),kv("hulk",obj(kv("spider","Outra vez, alguém precisa ouvir a pessoa dentro do grito."),kv("iron","Nem todo amplificador precisa de mais energia. Alguns precisam de companhia."),kv("captain","Bruce continua aqui. Não o deixarei sozinho de novo."))),kv("thor",obj(kv("spider","Uma cidade cabe em quem decide carregá-la. Um coração também."),kv("iron","Aprendi que devolver também pode ser uma forma de consertar."),kv("captain","Uma promessa pode pesar mais que um martelo."))),kv("leader",obj(kv("spider","Na outra vez, eu também quis atravessar sozinho. Agora conheço o caminho de volta."),kv("iron","A resistência me lembra de quem paga pelo meu improviso."),kv("captain","Continuar não exige abandonar quem veio comigo."))),kv("doom",obj(kv("spider","Sua mãe não pediria o fim de quem ficou embaixo."),kv("iron","Eu reconheço essa conta impossível. Não a pagarei com outros mundos."),kv("captain","Eu perdi meu tempo. Não tomarei o tempo dos outros para recuperá-lo.")))));
if(truth(and(()->and(()->get(s227.get("s"),"eco"),()->s227.get("marco")),()->binary("!==",s227.get("marco"),"FINAL")))){
discard(method(s227.get("lines"),"push",args(call(s227.get("line"),args(binary("+","Eco · ",call(s227.get("protagonist"),args(s227.get("s"),obj(spread(get(s227.get("echoes"),s227.get("id"))),kv("custom",binary("+",binary("+",binary("+",binary("+","O Tecido reconhece ",get(get(s227.get("s"),"character"),"name"))," em "),s227.get("marco")),", mas a próxima escolha ainda está aberta.")))))))))));
}
discard(method(s227.get("lines"),"push",args(call(s227.get("line"),args(binary("+","Este encontro leva a um confronto: ",get(method(s227.get("ENCOUNTERS"),"find",args(fn(s227,(s235,a236)->{
s235.let("e",argument(a236,0));
throw returned(binary("===",get(s235.get("e"),"id"),s235.get("id")));
}))),"context")))))));
if(always()) throw returned(obj(kv("lines",s227.get("lines")),kv("encounter",s227.get("id"))));
}));
}
private void install_objectDialogue(){global.let("objectDialogue",fn(global,(s237,a238)->{
s237.let("o",argument(a238,0));
s237.let("s",argument(a238,1));
s237.let("f",get(s237.get("s"),"flags"));
s237.let("id",get(s237.get("o"),"id"));
if(truth(get(s237.get("o"),"travel"))){
{ final Scope s239=new Scope(s237);
s239.let("destination",call(s239.get("locationById"),args(get(s239.get("o"),"travel"))));
if(always()) throw returned(obj(kv("lines",arr(call(s239.get("line"),args(binary("+",binary("+","A passagem está estável. ",get(s239.get("destination"),"name"))," espera do outro lado."))))),kv("travel",get(s239.get("o"),"travel"))));
}
}
if(truth(method(s237.get("id"),"startsWith",args("sign-")))){
{ final Scope s240=new Scope(s237);
s240.let("r",call(s240.get("locationById"),args(get(s240.get("o"),"loc"))));
if(always()) throw returned(call(s240.get("talk"),args(binary("+",get(s240.get("r"),"subtitle")," · Consulte o mapa e o dossiê para caminhos e pistas."))));
}
}
if(truth(get(s237.get("CLUES"),s237.get("id")))){
if(always()) throw returned(call(s237.get("talk"),args(binary("+",get(get(s237.get("CLUES"),s237.get("id")),1),(truth(and(()->binary("===",s237.get("id"),"FV9"),()->get(s237.get("s"),"eco")))?" Você lembra de ter visto este texto antes.":"")),fn(s237,(s241,a242)->{
discard(call(s241.get("collectClue"),args(s241.get("s"),s241.get("id"))));
if(truth(and(()->binary("===",s241.get("id"),"FV1"),()->unary("!",get(s241.get("f"),"fragment"))))){
discard(put(s241.get("f"),"runeFirst",true));
}
}))));
}
s237.let("pickup",get(obj(kv("drone",arr("Drone recuperado","O drone de Marcus está sem energia. O defensor tecnológico pode recuperá-lo.")),kv("coin",arr("Moeda nórdica","Uma moeda que não pertence a Nova York. Marcus pode indicar a quem entregar.")),kv("bead",arr("Conta de vibranium","Uma conta entre trilhos antigos. Pode ser entregue ao defensor tecnológico.")),kv("book",arr("Livro atravessado","As páginas chegaram antes da capa. Wong conhece a biblioteca de origem.")),kv("bike",arr("Bicicleta recuperada","A bicicleta deixou de flutuar. Devolva a Marcus.")),kv("arrow",arr("Flecha perdida","O dono está no mercado. Uma pequena devolução pode mudar a confiança.")),kv("supplies",arr("Suprimentos do Setor","Leve esta caixa aos refugiados de Magneto."))),s237.get("id")));
if(truth(s237.get("pickup"))){
if(always()) throw returned(call(s237.get("talk"),args(get(s237.get("pickup"),1),fn(s237,(s243,a244)->{
discard(put(s243.get("f"),s243.get("id"),true));
discard(call(s243.get("remember"),args(s243.get("s"),binary("+","item-",s243.get("id")),spread(s243.get("pickup")))));
}))));
}
if(truth(binary("===",s237.get("id"),"fragment"))){
if(always()) throw returned(call(s237.get("talk"),args(binary("+",binary("+","O cristal responde à sua mão. Quatro visões: Nova York, uma raiz azul, uma ponte de luz e um sol negro. As cidades tentam ocupar o mesmo céu. ",call(s237.get("protagonist"),args(s237.get("s"),obj(kv("spider","Ele escolheu quem olhou para quem estava embaixo."),kv("iron","Ele escolheu quem não para de consertar o que quebrou."),kv("captain","Ele escolheu quem nunca desistiu de uma causa."),kv("custom","Ele escolheu uma pessoa capaz de levar a pergunta adiante."))))),(truth(get(s237.get("f"),"runeFirst"))?" A runa de Latveria estava aqui antes do cristal.":" A origem permanece um mistério.")),fn(s237,(s245,a246)->{
discard(put(s245.get("f"),"fragment",true));
discard(call(s245.get("remember"),args(s245.get("s"),"fragment","Fragmento Âncora","O cristal escolheu o protagonista. Cada deslocamento parece abrir uma nova Fissura.")));
discard(call(s245.get("refreshProgress"),args(s245.get("s"))));
}))));
}
if(truth(binary("===",s237.get("id"),"smallfragment"))){
if(always()) throw returned(call(s237.get("talk"),args("O fragmento menor foi retirado das antenas. O espelho no vidro mostra uma máscara de metal: o Monarca.",call(s237.get("done"),args(s237.get("s"),"smallfragment")))));
}
if(truth(binary("===",s237.get("id"),"equipment"))){
if(always()) throw returned(call(s237.get("talk"),args(call(s237.get("protagonist"),args(s237.get("s"),obj(kv("spider","A máscara está remendada. May deixou uma nova linha no bolso."),kv("iron","Pepper separou uma peça reparada da armadura. “Volte para a próxima manutenção.”"),kv("captain","O equipamento está pronto. Sharon deixou um mapa dobrado sob o escudo."),kv("custom","Sua roupa, sua cor e seu poder. A aventura começa com alguém que pertence a este mundo.")))),call(s237.get("done"),args(s237.get("s"),"equipment")))));
}
if(truth(binary("===",s237.get("id"),"newspaper"))){
if(always()) throw returned(call(s237.get("talk"),args("TREMORES · Objetos desapareceram do depósito da polícia. Moradores relatam o mesmo cheiro de ozônio em bairros diferentes.")));
}
if(truth(binary("===",s237.get("id"),"old-armor"))){
if(always()) throw returned(call(s237.get("talk"),args((truth(binary("===",get(get(s237.get("s"),"character"),"id"),"iron"))?"Eu já fui menos elegante. FRIDAY: “E igualmente incapaz de descartar peças antigas.”":"O traje tem marcas de reparos sobre reparos. Stark guardou a lembrança de uma falha que conseguiu consertar."))));
}
if(truth(binary("===",s237.get("id"),"consultant-mirror"))){
if(always()) throw returned(call(s237.get("talk"),args("Por dois segundos, o espelho não reflete você: um capuz verde, uma máscara metálica. A imagem do Monarca desaparece antes que você possa chamá-lo pelo nome.",call(s237.get("done"),args(s237.get("s"),"monarchGlimpse")))));
}
if(truth(binary("===",s237.get("id"),"transmission"))){
if(always()) throw returned(call(s237.get("talk"),args((truth(get(s237.get("f"),"M08"))?"As duas mensagens vêm do mesmo emissor metálico. Fury foi imitado.":"Compare as mensagens depois de desmentir Gavião e Viúva."),fn(s237,(s247,a248)->{
if(truth(get(s247.get("f"),"M08"))){
{ final Scope s249=new Scope(s247);
discard(call(s249.get("collectClue"),args(s249.get("s"),"FV6a")));
discard(put(s249.get("f"),"S04",true));
}
}
}))));
}
if(truth(binary("===",s237.get("id"),"flower-market"))){
if(always()) throw returned(call(s237.get("talk"),args((truth(get(s237.get("f"),"flower"))?"As pétalas de Nakia acalmam o rasgo. Pantera saberá que você protegeu o mercado.":"A pequena Fissura responde a frequências vivas. Nakia pode conhecer algo que ajude."),fn(s237,(s250,a251)->{
if(truth(get(s250.get("f"),"flower"))){
discard(put(s250.get("f"),"marketFlower",true));
}
}))));
}
if(truth(binary("===",s237.get("id"),"pillar-mark"))){
if(always()) throw returned(call(s237.get("talk"),args("Muito antes do Monarca, Wakanda gravou quatro círculos no vibranium. O Pilar II já tinha um nome.")));
}
if(truth(binary("===",s237.get("id"),"rhythm"))){
if(always()) throw returned(obj(kv("lines",arr(call(s237.get("line"),args("Três cristais esperam a lembrança de Bruce. DÓ, MI, SOL.")),call(s237.get("choice"),args("rhythm-1","Qual é a primeira nota?",call(s237.get("opt"),args("DÓ.","O primeiro cristal responde.",null,null,fn(s237,(s252,a253)->{
discard(put(s252.get("f"),"rhythm1",true));
}))),obj(spread(call(s237.get("opt"),args("SOL.","A nota se apaga."))),kv("retry",true)))),spread((truth(get(s237.get("f"),"rhythm1"))?arr(call(s237.get("choice"),args("rhythm-2","Depois de DÓ, vem...",call(s237.get("opt"),args("MI.","O segundo cristal se acende.",null,null,fn(s237,(s254,a255)->{
discard(put(s254.get("f"),"rhythm2",true));
}))),obj(spread(call(s237.get("opt"),args("RÉ.","A luz não acompanha."))),kv("retry",true))))):arr())),spread((truth(get(s237.get("f"),"rhythm2"))?arr(call(s237.get("choice"),args("rhythm-3","A última nota é...",call(s237.get("opt"),args("SOL.","Bruce reconhece a sequência. “Hulk lembra.”",null,null,fn(s237,(s256,a257)->{
discard(call(s256.get("gesture"),args(s256.get("s"),"hulk")));
discard(put(s256.get("f"),"S05",true));
}))),obj(spread(call(s237.get("opt"),args("DÓ.","A sequência não fecha."))),kv("retry",true))))):arr()))))));
}
if(truth(binary("===",s237.get("id"),"mural"))){
if(always()) throw returned(call(s237.get("talk"),args((truth(get(s237.get("f"),"FV9"))?"O mural extra mostra mundos contados por um Titã, enquanto quatro colunas sustentam a mesa.":"Cinzas cobrem o mural. Talvez a Biblioteca explique sua inscrição."))));
}
if(truth(binary("===",s237.get("id"),"poster"))){
if(always()) throw returned(call(s237.get("talk"),args("O selo do cartaz não é de nenhuma autoridade. Rasgue-o diante de Star-Lord depois do encontro.")));
}
if(truth(binary("===",s237.get("id"),"plant"))){
if(always()) throw returned(or(()->call(s237.get("gesturePrompt"),args(s237.get("s"),"wolverine","O parque da Terra-Esmaecida espera uma raiz viva.","Plantar as sementes de Nakia.",unary("!",unary("!",get(s237.get("f"),"seeds"))),fn(s237,(s258,a259)->{
discard(put(s258.get("f"),"S09",true));
}))),()->call(s237.get("talk"),args((truth(get(s237.get("f"),"G-wolverine"))?"Uma flor resiste onde as crianças brincavam.":"Nakia conhece flores capazes de encontrar raízes em mundos feridos.")))));
}
if(truth(binary("===",s237.get("id"),"nucleus"))){
if(always()) throw returned(call(s237.get("talk"),args((truth(get(s237.get("f"),"solved"))?"Quatro Pilares respiram separados. O pó azul do Fragmento não responde a nenhuma ordem.":"As colunas foram drenadas. Destino planejou a fusão; Thanos planejou o número de mortos."))));
}
if(always()) throw returned(call(s237.get("talk"),args("O objeto guarda uma lembrança deste lugar.")));
}));
}
private void install_OUTLINE(){global.let("OUTLINE","#172d32");
}
private void install_shade(){global.let("shade",fn(global,(s260,a261)->{
s260.let("hex",argument(a261,0));
s260.let("amount",argument(a261,1));
s260.let("n",call(s260.get("parseInt"),args(method(s260.get("hex"),"slice",args(1)),16)));
s260.let("c",fn(s260,(s262,a263)->{
s262.let("v",argument(a263,0));
throw returned(method(s262.get("Math"),"max",args(0,method(s262.get("Math"),"min",args(255,method(s262.get("Math"),"round",args(binary("*",s262.get("v"),s262.get("amount")))))))));
}));
if(always()) throw returned(("rgb("+str(call(s260.get("c"),args(binary(">>",s260.get("n"),16))))+","+str(call(s260.get("c"),args(binary("&",binary(">>",s260.get("n"),8),255))))+","+str(call(s260.get("c"),args(binary("&",s260.get("n"),255))))+")"));
}));
}
private void install_drawSprite(){global.let("drawSprite",fn(global,(s264,a265)->{
s264.let("ctx",argument(a265,0));
s264.let("character",argument(a265,1));
s264.let("x",argument(a265,2));
s264.let("y",argument(a265,3));
s264.let("direction",coalesce(argument(a265,4),()->"down"));
s264.let("time",coalesce(argument(a265,5),()->0));
s264.let("walking",coalesce(argument(a265,6),()->false));
s264.let("scale",coalesce(argument(a265,7),()->1));
s264.let("c",s264.get("character"));
s264.let("k",or(()->get(s264.get("c"),"kind"),()->"custom"));
s264.let("a",or(()->get(s264.get("c"),"appearance"),()->obj()));
discard(method(s264.get("ctx"),"save",args()));
discard(method(s264.get("ctx"),"translate",args(method(s264.get("Math"),"round",args(s264.get("x"))),method(s264.get("Math"),"round",args(s264.get("y"))))));
discard(method(s264.get("ctx"),"scale",args(s264.get("scale"),s264.get("scale"))));
discard(method(s264.get("ctx"),"translate",args(unary("-",14),unary("-",31))));
s264.let("r",fn(s264,(s266,a267)->{
s266.let("x",argument(a267,0));
s266.let("y",argument(a267,1));
s266.let("w",argument(a267,2));
s266.let("h",argument(a267,3));
s266.let("col",argument(a267,4));
discard(put(s266.get("ctx"),"fillStyle",s266.get("col")));
discard(method(s266.get("ctx"),"fillRect",args(method(s266.get("Math"),"round",args(s266.get("x"))),method(s266.get("Math"),"round",args(s266.get("y"))),s266.get("w"),s266.get("h"))));
}));
s264.let("box",fn(s264,(s268,a269)->{
s268.let("x",argument(a269,0));
s268.let("y",argument(a269,1));
s268.let("w",argument(a269,2));
s268.let("h",argument(a269,3));
s268.let("col",argument(a269,4));
discard(call(s268.get("r"),args(binary("-",s268.get("x"),1),binary("-",s268.get("y"),1),binary("+",s268.get("w"),2),binary("+",s268.get("h"),2),s268.get("OUTLINE"))));
discard(call(s268.get("r"),args(s268.get("x"),s268.get("y"),s268.get("w"),s268.get("h"),s268.get("col"))));
}));
s264.let("poly",fn(s264,(s270,a271)->{
s270.let("pts",argument(a271,0));
s270.let("col",argument(a271,1));
discard(put(s270.get("ctx"),"fillStyle",s270.get("col")));
discard(method(s270.get("ctx"),"beginPath",args()));
discard(method(s270.get("pts"),"forEach",args(fn(s270,(s272,a273)->{
Object v274=argument(a273,0);
s272.let("px",get(v274,0));
s272.let("py",get(v274,1));
s272.let("i",argument(a273,1));
throw returned((truth(s272.get("i"))?method(s272.get("ctx"),"lineTo",args(s272.get("px"),s272.get("py"))):method(s272.get("ctx"),"moveTo",args(s272.get("px"),s272.get("py")))));
}))));
discard(method(s270.get("ctx"),"closePath",args()));
discard(method(s270.get("ctx"),"fill",args()));
}));
s264.let("phase",(truth(s264.get("walking"))?binary("%",method(s264.get("Math"),"floor",args(binary("*",s264.get("time"),9))),4):0));
s264.let("step",(truth(s264.get("walking"))?get(arr(0,1,0,unary("-",1)),s264.get("phase")):0));
s264.let("breath",(truth(s264.get("walking"))?0:(truth(binary(">",method(s264.get("Math"),"sin",args(binary("*",s264.get("time"),2.3))),0.85))?unary("-",1):0)));
s264.let("side",or(()->binary("===",s264.get("direction"),"left"),()->binary("===",s264.get("direction"),"right")));
s264.let("back",binary("===",s264.get("direction"),"up"));
s264.let("wide",(truth(binary("===",s264.get("k"),"hulk"))?3:(truth(or(()->binary("===",s264.get("k"),"thanos"),()->binary("===",s264.get("k"),"venom")))?2:(truth(or(()->binary("===",s264.get("k"),"killmonger"),()->binary("===",s264.get("k"),"ultron")))?1:(truth(binary("===",s264.get("k"),"custom"))?(truth(binary("===",get(s264.get("a"),"build"),2))?2:(truth(binary("===",get(s264.get("a"),"build"),0))?unary("-",1):0)):0)))));
discard(call(s264.get("r"),args(binary("-",5,s264.get("wide")),29,binary("+",18,binary("*",s264.get("wide"),2)),2,"#183b3655")));
if(truth(and(()->s264.get("side"),()->binary("===",s264.get("direction"),"left")))){
{ final Scope s275=new Scope(s264);
discard(method(s275.get("ctx"),"translate",args(28,0)));
discard(method(s275.get("ctx"),"scale",args(unary("-",1),1)));
}
}
s264.let("cape",method(arr("thor","strange","scarlet","doom","loki","hela","magneto"),"includes",args(s264.get("k"))));
if(truth(s264.get("cape"))){
{ final Scope s276=new Scope(s264);
s276.let("color",(truth(binary("===",s276.get("k"),"doom"))?"#2c5843":(truth(binary("===",s276.get("k"),"loki"))?"#264a3a":(truth(binary("===",s276.get("k"),"hela"))?"#172b28":(truth(binary("===",s276.get("k"),"magneto"))?"#632f5d":"#8e3844")))));
discard(call(s276.get("poly"),args(arr(arr(6,12),arr(21,12),arr(23,binary("-",28,s276.get("step"))),arr(6,binary("+",28,s276.get("step"))),arr(4,23)),s276.get("color"))));
discard(call(s276.get("r"),args(6,15,3,12,call(s276.get("shade"),args(s276.get("color"),0.8)))));
}
}
s264.let("leg",or(()->get(s264.get("c"),"legs"),()->"#455c62"));
discard(call(s264.get("box"),args(binary("-",9,s264.get("wide")),binary("+",23,s264.get("step")),binary("+",5,s264.get("wide")),binary("-",6,s264.get("step")),s264.get("leg"))));
discard(call(s264.get("box"),args(15,binary("-",23,s264.get("step")),binary("+",5,s264.get("wide")),binary("+",6,s264.get("step")),s264.get("leg"))));
discard(call(s264.get("r"),args(binary("-",8,s264.get("wide")),binary("+",28,s264.get("step")),binary("+",7,s264.get("wide")),2,(truth(binary("===",s264.get("k"),"hulk"))?get(s264.get("c"),"skin"):(truth(or(()->or(()->binary("===",s264.get("k"),"marvel"),()->binary("===",s264.get("k"),"iron")),()->binary("===",s264.get("k"),"captain")))?"#9e3e42":s264.get("OUTLINE"))))));
discard(call(s264.get("r"),args(15,binary("-",28,s264.get("step")),binary("+",7,s264.get("wide")),2,(truth(binary("===",s264.get("k"),"hulk"))?get(s264.get("c"),"skin"):(truth(or(()->or(()->binary("===",s264.get("k"),"marvel"),()->binary("===",s264.get("k"),"iron")),()->binary("===",s264.get("k"),"captain")))?"#9e3e42":s264.get("OUTLINE"))))));
discard(method(s264.get("ctx"),"translate",args(0,s264.get("breath"))));
discard(call(s264.get("box"),args(binary("-",7,s264.get("wide")),13,binary("+",14,binary("*",s264.get("wide"),2)),11,get(s264.get("c"),"body"))));
discard(call(s264.get("r"),args(binary("-",7,s264.get("wide")),14,3,9,call(s264.get("shade"),args(get(s264.get("c"),"body"),0.77)))));
discard(call(s264.get("r"),args(binary("-",9,s264.get("wide")),13,binary("+",11,s264.get("wide")),2,call(s264.get("shade"),args(get(s264.get("c"),"body"),1.13)))));
s264.let("arms",(truth(binary("===",s264.get("k"),"hulk"))?get(s264.get("c"),"skin"):(truth(method(arr("thor","wolverine"),"includes",args(s264.get("k"))))?get(s264.get("c"),"skin"):get(s264.get("c"),"body"))));
discard(call(s264.get("box"),args(binary("-",4,s264.get("wide")),binary("-",14,s264.get("step")),binary("+",4,s264.get("wide")),9,s264.get("arms"))));
discard(call(s264.get("box"),args(binary("+",21,s264.get("wide")),binary("+",14,s264.get("step")),binary("+",4,s264.get("wide")),9,s264.get("arms"))));
discard(call(s264.get("r"),args(binary("-",4,s264.get("wide")),binary("-",21,s264.get("step")),binary("+",4,s264.get("wide")),3,(truth(binary("===",s264.get("k"),"iron"))?get(s264.get("c"),"accent"):get(s264.get("c"),"skin")))));
discard(call(s264.get("r"),args(binary("+",21,s264.get("wide")),binary("+",21,s264.get("step")),binary("+",4,s264.get("wide")),3,(truth(binary("===",s264.get("k"),"iron"))?get(s264.get("c"),"accent"):get(s264.get("c"),"skin")))));
discard(call(s264.get("r"),args(binary("-",8,s264.get("wide")),22,binary("+",13,binary("*",s264.get("wide"),2)),2,(truth(method(arr("strange","widow","scarlet"),"includes",args(s264.get("k"))))?"#b94449":(truth(binary("===",s264.get("k"),"thanos"))?"#c4a253":call(s264.get("shade"),args(get(s264.get("c"),"body"),0.69)))))));
if(truth(and(()->binary("===",s264.get("k"),"custom"),()->binary("===",get(s264.get("a"),"style"),1)))){
{ final Scope s277=new Scope(s264);
discard(call(s277.get("r"),args(binary("-",7,s277.get("wide")),18,2,4,s277.get("OUTLINE"))));
discard(call(s277.get("r"),args(binary("+",20,s277.get("wide")),18,1,4,s277.get("OUTLINE"))));
}
}
s264.let("head",get(s264.get("c"),"skin"));
s264.let("hair",or(()->get(s264.get("c"),"hairColor"),()->"#382f30"));
if(truth(method(arr("spider","deadpool","venom","killmonger"),"includes",args(s264.get("k"))))){
discard(s264.set("head",get(s264.get("c"),"body")));
}
if(truth(binary("===",s264.get("k"),"ultron"))){
discard(s264.set("head",get(s264.get("c"),"skin")));
}
if(truth(binary("===",s264.get("k"),"captain"))){
discard(s264.set("head","#3b5d92"));
}
if(truth(binary("===",s264.get("k"),"panther"))){
discard(s264.set("head","#2d303d"));
}
discard(call(s264.get("box"),args(binary("-",8,s264.get("wide")),4,binary("+",12,binary("*",s264.get("wide"),2)),10,s264.get("head"))));
discard(call(s264.get("r"),args(binary("-",9,s264.get("wide")),4,binary("+",10,binary("*",s264.get("wide"),2)),2,call(s264.get("shade"),args(s264.get("head"),1.15)))));
discard(call(s264.get("r"),args(binary("-",8,s264.get("wide")),11,2,3,call(s264.get("shade"),args(s264.get("head"),0.84)))));
s264.let("hairKinds",arr("custom","resident","strange","widow","scarlet","marvel","thor","hulk"));
if(truth(method(s264.get("hairKinds"),"includes",args(s264.get("k"))))){
{ final Scope s278=new Scope(s264);
discard(s278.set("hair",(truth(binary("===",s278.get("k"),"widow"))?"#974e3d":(truth(binary("===",s278.get("k"),"scarlet"))?"#67333b":(truth(method(arr("marvel","thor"),"includes",args(s278.get("k"))))?"#dabc68":(truth(binary("===",s278.get("k"),"hulk"))?"#294338":s278.get("hair")))))));
s278.let("shaved",and(()->binary("===",s278.get("k"),"custom"),()->binary("===",get(s278.get("a"),"hair"),2)));
if(truth(unary("!",s278.get("shaved")))){
{ final Scope s279=new Scope(s278);
discard(call(s279.get("r"),args(binary("-",8,s279.get("wide")),3,binary("+",12,binary("*",s279.get("wide"),2)),4,s279.get("hair"))));
discard(call(s279.get("r"),args(binary("-",7,s279.get("wide")),5,3,5,s279.get("hair"))));
discard(call(s279.get("r"),args(binary("-",10,s279.get("wide")),3,binary("+",10,s279.get("wide")),1,call(s279.get("shade"),args(s279.get("hair"),1.3)))));
}
}
if(truth(or(()->method(arr("widow","scarlet","marvel"),"includes",args(s278.get("k"))),()->and(()->binary("===",s278.get("k"),"custom"),()->binary("===",get(s278.get("a"),"hair"),1))))){
{ final Scope s280=new Scope(s278);
discard(call(s280.get("r"),args(binary("-",7,s280.get("wide")),6,3,11,s280.get("hair"))));
discard(call(s280.get("r"),args(binary("+",19,s280.get("wide")),6,3,9,s280.get("hair"))));
}
}
if(truth(s278.get("back"))){
{ final Scope s281=new Scope(s278);
discard(call(s281.get("r"),args(binary("-",8,s281.get("wide")),7,binary("+",12,binary("*",s281.get("wide"),2)),7,(truth(s281.get("shaved"))?call(s281.get("shade"),args(s281.get("head"),0.85)):s281.get("hair")))));
}
}
}
}
s264.let("blink",and(()->unary("!",s264.get("walking")),()->binary("===",binary("%",method(s264.get("Math"),"floor",args(binary("*",s264.get("time"),1.3))),6),5)));
if(truth(unary("!",s264.get("back")))){
{ final Scope s282=new Scope(s264);
if(truth(s282.get("side"))){
{ final Scope s283=new Scope(s282);
discard(call(s283.get("r"),args(binary("+",18,s283.get("wide")),8,2,(truth(s283.get("blink"))?1:2),s283.get("OUTLINE"))));
discard(call(s283.get("r"),args(binary("+",20,s283.get("wide")),10,2,2,s283.get("head"))));
}
}
else{
{ final Scope s284=new Scope(s282);
discard(call(s284.get("r"),args(binary("-",10,s284.get("wide")),9,2,(truth(s284.get("blink"))?1:2),s284.get("OUTLINE"))));
discard(call(s284.get("r"),args(binary("+",16,s284.get("wide")),9,2,(truth(s284.get("blink"))?1:2),s284.get("OUTLINE"))));
discard(call(s284.get("r"),args(13,12,3,1,call(s284.get("shade"),args(s284.get("head"),0.66)))));
}
}
}
}
if(truth(binary("===",s264.get("k"),"spider"))){
{ final Scope s285=new Scope(s264);
discard(call(s285.get("r"),args(8,7,12,1,"#802c39")));
discard(call(s285.get("r"),args(13,4,1,9,"#802c39")));
discard(call(s285.get("r"),args(9,16,11,1,"#802c39")));
discard(call(s285.get("r"),args(13,15,1,6,"#802c39")));
if(truth(unary("!",s285.get("back")))){
{ final Scope s286=new Scope(s285);
discard(call(s286.get("r"),args((truth(s286.get("side"))?17:9),8,(truth(s286.get("side"))?3:4),3,"#f6ecd5")));
if(truth(unary("!",s286.get("side")))){
discard(call(s286.get("r"),args(16,8,3,3,"#f6ecd5")));
}
discard(call(s286.get("r"),args(12,18,4,2,s286.get("OUTLINE"))));
discard(call(s286.get("r"),args(11,17,1,4,s286.get("OUTLINE"))));
discard(call(s286.get("r"),args(16,17,1,4,s286.get("OUTLINE"))));
}
}
discard(call(s285.get("r"),args(4,binary("-",18,s285.get("step")),4,4,"#3563a5")));
discard(call(s285.get("r"),args(21,binary("+",18,s285.get("step")),4,4,"#3563a5")));
}
}
else{
if(truth(binary("===",s264.get("k"),"iron"))){
{ final Scope s287=new Scope(s264);
discard(call(s287.get("r"),args(9,5,10,7,"#dfb653")));
discard(call(s287.get("r"),args(11,12,6,1,"#deb65a")));
if(truth(unary("!",s287.get("back")))){
{ final Scope s288=new Scope(s287);
discard(call(s288.get("r"),args((truth(s288.get("side"))?17:9),8,(truth(s288.get("side"))?3:4),1,"#a5f1ee")));
if(truth(unary("!",s288.get("side")))){
discard(call(s288.get("r"),args(16,8,3,1,"#a5f1ee")));
}
discard(call(s288.get("r"),args(12,17,4,3,"#b6f8ed")));
discard(call(s288.get("r"),args(13,18,2,1,"#fffde2")));
}
}
else{
discard(call(s287.get("r"),args(10,6,8,2,"#d07a3b")));
}
discard(call(s287.get("r"),args(7,14,3,5,"#d6a551")));
discard(call(s287.get("r"),args(18,14,3,5,"#d6a551")));
}
}
else{
if(truth(binary("===",s264.get("k"),"captain"))){
{ final Scope s289=new Scope(s264);
if(truth(and(()->unary("!",s289.get("back")),()->unary("!",s289.get("side"))))){
{ final Scope s290=new Scope(s289);
discard(call(s290.get("r"),args(12,5,1,3,"#f5edd5")));
discard(call(s290.get("r"),args(15,5,1,3,"#f5edd5")));
discard(call(s290.get("r"),args(13,4,2,1,"#f5edd5")));
discard(call(s290.get("r"),args(13,6,2,1,"#f5edd5")));
discard(call(s290.get("r"),args(11,11,7,3,get(s290.get("c"),"skin"))));
}
}
if(truth(unary("!",s289.get("back")))){
{ final Scope s291=new Scope(s289);
discard(call(s291.get("poly"),args(arr(arr(14,15),arr(15,18),arr(18,18),arr(16,20),arr(16,22),arr(14,20),arr(11,22),arr(12,19),arr(10,18),arr(13,18)),"#f2e9cf")));
discard(call(s291.get("r"),args(9,21,2,3,"#c6494c")));
discard(call(s291.get("r"),args(13,21,2,3,"#ead8ba")));
discard(call(s291.get("r"),args(17,21,2,3,"#c6494c")));
}
}
discard(call(s289.get("box"),args((truth(s289.get("side"))?4:2),binary("-",18,s289.get("step")),7,9,"#b84242")));
discard(call(s289.get("r"),args((truth(s289.get("side"))?5:3),binary("-",19,s289.get("step")),5,6,"#ebe2c8")));
discard(call(s289.get("r"),args((truth(s289.get("side"))?6:4),binary("-",20,s289.get("step")),3,4,"#466899")));
discard(call(s289.get("r"),args((truth(s289.get("side"))?7:5),binary("-",21,s289.get("step")),1,2,"#f2eed6")));
}
}
else{
if(truth(binary("===",s264.get("k"),"wolverine"))){
{ final Scope s292=new Scope(s264);
discard(call(s292.get("poly"),args(arr(arr(7,7),arr(6,0),arr(11,4),arr(17,4),arr(22,0),arr(21,8)),"#273040")));
discard(call(s292.get("r"),args(8,8,3,2,"#f7f2db")));
discard(call(s292.get("r"),args(17,8,3,2,"#f7f2db")));
if(truth(s292.get("back"))){
discard(call(s292.get("r"),args(9,8,10,4,"#c8a33d")));
}
discard(call(s292.get("poly"),args(arr(arr(8,15),arr(12,16),arr(8,18)),"#293c53")));
discard(call(s292.get("poly"),args(arr(arr(20,15),arr(16,16),arr(20,18)),"#293c53")));
discard(call(s292.get("r"),args(22,binary("+",22,s292.get("step")),6,1,"#dce8e1")));
discard(call(s292.get("r"),args(23,binary("+",24,s292.get("step")),5,1,"#dce8e1")));
discard(call(s292.get("r"),args(22,binary("+",26,s292.get("step")),6,1,"#dce8e1")));
}
}
else{
if(truth(binary("===",s264.get("k"),"panther"))){
{ final Scope s293=new Scope(s264);
discard(call(s293.get("poly"),args(arr(arr(8,5),arr(8,1),arr(11,4)),"#353441")));
discard(call(s293.get("poly"),args(arr(arr(17,4),arr(21,1),arr(20,6)),"#353441")));
if(truth(unary("!",s293.get("back")))){
{ final Scope s294=new Scope(s293);
discard(call(s294.get("r"),args((truth(s294.get("side"))?18:10),8,3,1,"#dedbed")));
if(truth(unary("!",s294.get("side")))){
discard(call(s294.get("r"),args(16,8,3,1,"#dedbed")));
}
}
}
{final Scope s295=new Scope(s293);s295.let("i",0);
while(truth(binary("<",s295.get("i"),4))){try{discard(call(s295.get("r"),args(binary("+",9,binary("*",s295.get("i"),3)),binary("+",15,method(s295.get("Math"),"abs",args(binary("-",s295.get("i"),1)))),1,2,"#b4a4d3")));
}catch(Continue c){}catch(Break b){break;}discard(update(s295,"i",1,true));}}
discard(call(s293.get("r"),args(24,binary("+",22,s293.get("step")),3,1,"#a896d6")));
discard(call(s293.get("r"),args(24,binary("+",24,s293.get("step")),3,1,"#a896d6")));
}
}
else{
if(truth(binary("===",s264.get("k"),"thor"))){
{ final Scope s296=new Scope(s264);
discard(call(s296.get("r"),args(8,4,12,3,"#9caeb3")));
discard(call(s296.get("r"),args(10,6,8,1,"#d3d8cc")));
discard(call(s296.get("poly"),args(arr(arr(6,5),arr(3,0),arr(9,3)),"#b8c6c5")));
discard(call(s296.get("poly"),args(arr(arr(19,3),arr(25,0),arr(22,6)),"#b8c6c5")));
discard(call(s296.get("r"),args(10,16,2,2,"#c5cfd0")));
discard(call(s296.get("r"),args(17,16,2,2,"#c5cfd0")));
discard(call(s296.get("r"),args(10,20,2,2,"#c5cfd0")));
discard(call(s296.get("r"),args(17,20,2,2,"#c5cfd0")));
discard(call(s296.get("r"),args(24,binary("+",16,s296.get("step")),1,8,"#a88a4d")));
discard(call(s296.get("box"),args(22,binary("+",15,s296.get("step")),5,4,"#b7c8ce")));
}
}
else{
if(truth(binary("===",s264.get("k"),"hulk"))){
{ final Scope s297=new Scope(s264);
discard(call(s297.get("r"),args(9,17,15,1,"#3c854e")));
discard(call(s297.get("r"),args(16,16,1,6,"#3c854e")));
discard(call(s297.get("r"),args(10,20,12,1,"#438954")));
discard(call(s297.get("r"),args(8,22,16,3,"#81639f")));
if(truth(unary("!",s297.get("back")))){
discard(call(s297.get("r"),args(11,12,7,1,"#e1ddb0")));
}
}
}
else{
if(truth(binary("===",s264.get("k"),"strange"))){
{ final Scope s298=new Scope(s264);
discard(call(s298.get("r"),args(8,5,2,3,"#bfc7bb")));
discard(call(s298.get("r"),args(19,5,2,3,"#bfc7bb")));
if(truth(unary("!",s298.get("back")))){
{ final Scope s299=new Scope(s298);
discard(call(s299.get("r"),args(12,12,5,1,"#463534")));
discard(call(s299.get("r"),args(13,17,3,3,"#dbbb63")));
discard(call(s299.get("r"),args(14,18,1,1,"#6cad72")));
}
}
discard(call(s298.get("r"),args(7,13,3,3,"#b54345")));
discard(call(s298.get("r"),args(19,13,3,3,"#b54345")));
}
}
else{
if(truth(binary("===",s264.get("k"),"widow"))){
{ final Scope s300=new Scope(s264);
if(truth(unary("!",s300.get("back")))){
{ final Scope s301=new Scope(s300);
discard(call(s301.get("r"),args(12,17,5,1,"#bf4553")));
discard(call(s301.get("r"),args(13,18,3,1,"#bf4553")));
discard(call(s301.get("r"),args(12,19,5,1,"#bf4553")));
}
}
discard(call(s300.get("r"),args(4,binary("-",20,s300.get("step")),4,2,"#80bdd3")));
discard(call(s300.get("r"),args(21,binary("+",20,s300.get("step")),4,2,"#80bdd3")));
discard(call(s300.get("r"),args(25,binary("+",17,s300.get("step")),1,6,"#789cac")));
}
}
else{
if(truth(binary("===",s264.get("k"),"scarlet"))){
{ final Scope s302=new Scope(s264);
discard(call(s302.get("poly"),args(arr(arr(8,5),arr(8,1),arr(13,4),arr(19,1),arr(20,6)),"#b64661")));
if(truth(unary("!",s302.get("back")))){
{ final Scope s303=new Scope(s302);
discard(call(s303.get("poly"),args(arr(arr(14,16),arr(17,19),arr(14,22),arr(11,19)),"#ed7c91")));
discard(call(s303.get("poly"),args(arr(arr(14,18),arr(15,19),arr(14,20),arr(13,19)),get(s303.get("c"),"body"))));
}
}
discard(call(s302.get("r"),args(3,binary("-",21,s302.get("step")),5,1,"#fb92a1")));
discard(call(s302.get("r"),args(2,binary("-",20,s302.get("step")),1,3,"#d75978")));
}
}
else{
if(truth(binary("===",s264.get("k"),"marvel"))){
{ final Scope s304=new Scope(s264);
discard(call(s304.get("r"),args(8,14,12,2,"#c04b4c")));
if(truth(unary("!",s304.get("back")))){
{ final Scope s305=new Scope(s304);
discard(call(s305.get("r"),args(13,15,2,6,"#e5c771")));
discard(call(s305.get("r"),args(10,17,8,2,"#e5c771")));
discard(call(s305.get("r"),args(12,16,4,4,"#e5c771")));
}
}
}
}
else{
if(truth(binary("===",s264.get("k"),"doom"))){
{ final Scope s306=new Scope(s264);
discard(call(s306.get("poly"),args(arr(arr(6,13),arr(6,6),arr(8,2),arr(14,0),arr(20,2),arr(23,7),arr(23,13)),"#2c6345")));
discard(call(s306.get("box"),args((truth(s306.get("side"))?13:9),5,(truth(s306.get("side"))?7:10),8,"#aebbb4")));
discard(call(s306.get("r"),args((truth(s306.get("side"))?14:10),5,(truth(s306.get("side"))?5:8),2,"#d2dbce")));
if(truth(unary("!",s306.get("back")))){
{ final Scope s307=new Scope(s306);
discard(call(s307.get("r"),args((truth(s307.get("side"))?17:10),8,(truth(s307.get("side"))?2:3),1,"#304b46")));
if(truth(unary("!",s307.get("side")))){
discard(call(s307.get("r"),args(16,8,2,1,"#304b46")));
}
discard(call(s307.get("r"),args((truth(s307.get("side"))?16:12),11,(truth(s307.get("side"))?3:5),1,"#526760")));
discard(call(s307.get("r"),args((truth(s307.get("side"))?20:14),8,1,3,"#849a92")));
}
}
else{
discard(call(s306.get("r"),args(9,6,10,7,"#3c7950")));
}
discard(call(s306.get("r"),args(8,13,2,2,"#dabb66")));
discard(call(s306.get("r"),args(19,13,2,2,"#dabb66")));
discard(call(s306.get("r"),args(13,17,4,4,"#85bc91")));
discard(call(s306.get("r"),args(14,18,2,2,get(s306.get("c"),"body"))));
}
}
else{
if(truth(binary("===",s264.get("k"),"thanos"))){
{ final Scope s308=new Scope(s264);
discard(call(s308.get("r"),args(7,4,17,3,"#ceb260")));
discard(call(s308.get("r"),args(14,2,3,3,"#d4b666")));
discard(call(s308.get("r"),args(5,14,20,2,"#d9b65e")));
discard(call(s308.get("r"),args(13,14,3,10,"#c7a753")));
discard(call(s308.get("r"),args(7,22,17,2,"#d2b257")));
if(truth(unary("!",s308.get("back")))){
{final Scope s309=new Scope(s308);s309.let("i",0);
while(truth(binary("<",s309.get("i"),4))){try{discard(call(s309.get("r"),args(binary("+",11,binary("*",s309.get("i"),2)),12,1,2,"#5f4c78")));
}catch(Continue c){}catch(Break b){break;}discard(update(s309,"i",1,true));}}
}
discard(call(s308.get("box"),args(2,binary("-",20,s308.get("step")),6,6,"#d4b961")));
discard(method(arr("#64c280","#8a68c5","#659dd4","#d45859"),"forEach",args(fn(s308,(s310,a311)->{
s310.let("col",argument(a311,0));
s310.let("i",argument(a311,1));
throw returned(call(s310.get("r"),args(binary("+",3,binary("*",binary("%",s310.get("i"),2),2)),binary("-",binary("+",21,binary("*",method(s310.get("Math"),"floor",args(binary("/",s310.get("i"),2))),2)),s310.get("step")),1,1,s310.get("col"))));
}))));
}
}
else{
if(truth(binary("===",s264.get("k"),"deadpool"))){
{ final Scope s312=new Scope(s264);
discard(call(s312.get("r"),args(9,7,4,5,"#242e37")));
discard(call(s312.get("r"),args(16,7,4,5,"#242e37")));
if(truth(unary("!",s312.get("back")))){
{ final Scope s313=new Scope(s312);
discard(call(s313.get("r"),args((truth(s313.get("side"))?18:10),8,2,1,"#eef2dd")));
if(truth(unary("!",s313.get("side")))){
discard(call(s313.get("r"),args(17,8,2,1,"#eef2dd")));
}
}
}
discard(call(s312.get("r"),args(9,13,2,10,"#28353c")));
discard(call(s312.get("r"),args(18,13,2,10,"#28353c")));
if(truth(s312.get("back"))){
{ final Scope s314=new Scope(s312);
discard(call(s314.get("r"),args(9,10,2,14,"#afaca1")));
discard(call(s314.get("r"),args(18,10,2,14,"#afaca1")));
}
}
}
}
else{
if(truth(binary("===",s264.get("k"),"venom"))){
{ final Scope s315=new Scope(s264);
discard(call(s315.get("r"),args(binary("-",8,s315.get("wide")),4,binary("+",12,binary("*",s315.get("wide"),2)),3,"#17202e")));
if(truth(unary("!",s315.get("back")))){
{ final Scope s316=new Scope(s315);
discard(call(s316.get("poly"),args((truth(s316.get("side"))?arr(arr(17,7),arr(23,8),arr(20,11),arr(17,10)):arr(arr(8,7),arr(14,8),arr(12,11),arr(9,10))),"#f5f3e9")));
if(truth(unary("!",s316.get("side")))){
discard(call(s316.get("poly"),args(arr(arr(15,8),arr(22,7),arr(20,10),arr(17,11)),"#f5f3e9")));
}
discard(call(s316.get("r"),args(11,12,9,1,"#e7e5dc")));
discard(call(s316.get("r"),args(15,13,2,2,"#a94754")));
}
}
discard(call(s315.get("r"),args(12,15,5,5,"#f2f1e8")));
discard(call(s315.get("r"),args(14,18,2,4,"#f2f1e8")));
{final Scope s317=new Scope(s315);s317.let("i",0);
while(truth(binary("<",s317.get("i"),3))){try{{ final Scope s318=new Scope(s317);
discard(call(s318.get("r"),args(binary("-",8,s318.get("i")),binary("+",16,binary("*",s318.get("i"),2)),5,1,"#f2f1e8")));
discard(call(s318.get("r"),args(binary("+",17,s318.get("i")),binary("+",16,binary("*",s318.get("i"),2)),5,1,"#f2f1e8")));
}
}catch(Continue c){}catch(Break b){break;}discard(update(s317,"i",1,true));}}
if(truth(s315.get("back"))){
{ final Scope s319=new Scope(s315);
discard(call(s319.get("r"),args(11,17,7,3,"#f2f1e8")));
discard(call(s319.get("r"),args(7,18,5,1,"#f2f1e8")));
discard(call(s319.get("r"),args(18,18,5,1,"#f2f1e8")));
}
}
discard(call(s315.get("r"),args(binary("-",3,s315.get("wide")),binary("-",22,s315.get("step")),3,2,"#eef0e7")));
discard(call(s315.get("r"),args(binary("+",23,s315.get("wide")),binary("+",22,s315.get("step")),3,2,"#eef0e7")));
}
}
else{
if(truth(binary("===",s264.get("k"),"goblin"))){
{ final Scope s320=new Scope(s264);
discard(call(s320.get("poly"),args(arr(arr(7,5),arr(5,1),arr(11,4),arr(18,4),arr(24,1),arr(21,7)),"#5b3174")));
discard(call(s320.get("r"),args(9,4,10,3,"#794590")));
discard(call(s320.get("poly"),args(arr(arr(8,8),arr(5,7),arr(9,10)),"#79bf53")));
discard(call(s320.get("poly"),args(arr(arr(20,8),arr(23,7),arr(19,10)),"#79bf53")));
if(truth(unary("!",s320.get("back")))){
{ final Scope s321=new Scope(s320);
discard(call(s321.get("r"),args((truth(s321.get("side"))?18:10),9,3,1,"#e4cc7a")));
if(truth(unary("!",s321.get("side")))){
discard(call(s321.get("r"),args(16,9,3,1,"#e4cc7a")));
}
discard(call(s321.get("r"),args(12,12,5,1,"#335c3c")));
discard(call(s321.get("r"),args(13,13,1,1,"#f1e7c5")));
discard(call(s321.get("r"),args(16,13,1,1,"#f1e7c5")));
}
}
discard(call(s320.get("r"),args(11,16,7,2,"#97c25d")));
discard(call(s320.get("r"),args(13,18,3,4,"#d8b978")));
discard(call(s320.get("r"),args(4,binary("-",21,s320.get("step")),4,3,"#e58a39")));
discard(call(s320.get("r"),args(5,binary("-",20,s320.get("step")),2,1,"#f8c664")));
discard(call(s320.get("r"),args(4,29,21,1,"#7c8892")));
discard(call(s320.get("r"),args(11,28,8,1,"#b2a6a1")));
}
}
else{
if(truth(binary("===",s264.get("k"),"killmonger"))){
{ final Scope s322=new Scope(s264);
discard(call(s322.get("r"),args(binary("-",8,s322.get("wide")),4,binary("+",12,binary("*",s322.get("wide"),2)),3,"#2a2930")));
discard(call(s322.get("r"),args(10,3,8,2,"#4d3931")));
if(truth(unary("!",s322.get("back")))){
{ final Scope s323=new Scope(s322);
discard(call(s323.get("r"),args((truth(s323.get("side"))?18:10),8,3,1,"#d5b15e")));
if(truth(unary("!",s323.get("side")))){
discard(call(s323.get("r"),args(16,8,3,1,"#d5b15e")));
}
discard(call(s323.get("r"),args(11,12,7,1,"#cba551")));
discard(call(s323.get("r"),args(12,13,1,1,"#e0c57c")));
discard(call(s323.get("r"),args(16,13,1,1,"#e0c57c")));
}
}
discard(call(s322.get("poly"),args(arr(arr(8,15),arr(14,17),arr(20,15),arr(18,19),arr(14,21),arr(10,19)),"#caa34d")));
discard(call(s322.get("r"),args(13,16,2,4,"#34333b")));
for(Object v325:list(arr(arr(8,18),arr(20,18),arr(10,21),arr(18,21)))){final Scope s324=new Scope(s322);Object v326=v325;
s324.let("px",get(v326,0));
s324.let("py",get(v326,1));
try{discard(call(s324.get("r"),args(s324.get("px"),s324.get("py"),2,1,"#d4b25e")));
}catch(Continue c){}catch(Break b){break;}}
discard(call(s322.get("r"),args(binary("+",23,s322.get("wide")),binary("+",21,s322.get("step")),3,1,"#d9bd77")));
discard(call(s322.get("r"),args(binary("+",23,s322.get("wide")),binary("+",23,s322.get("step")),3,1,"#d9bd77")));
}
}
else{
if(truth(binary("===",s264.get("k"),"hela"))){
{ final Scope s327=new Scope(s264);
discard(call(s327.get("poly"),args(arr(arr(8,7),arr(6,1),arr(3,0),arr(6,5),arr(9,3),arr(12,6),arr(14,0),arr(16,6),arr(19,3),arr(22,5),arr(25,0),arr(22,1),arr(20,7)),"#172923")));
discard(call(s327.get("r"),args(9,5,10,3,"#254936")));
if(truth(unary("!",s327.get("back")))){
{ final Scope s328=new Scope(s327);
discard(call(s328.get("r"),args((truth(s328.get("side"))?18:10),9,3,1,"#7dbd7d")));
if(truth(unary("!",s328.get("side")))){
discard(call(s328.get("r"),args(16,9,3,1,"#7dbd7d")));
}
discard(call(s328.get("r"),args(12,12,5,1,"#294533")));
}
}
discard(call(s327.get("r"),args(12,15,4,9,"#4fa069")));
discard(call(s327.get("r"),args(9,17,2,6,"#3d7651")));
discard(call(s327.get("r"),args(18,17,2,6,"#3d7651")));
discard(call(s327.get("r"),args(4,binary("-",21,s327.get("step")),4,2,"#60b374")));
discard(call(s327.get("r"),args(21,binary("+",21,s327.get("step")),4,2,"#60b374")));
}
}
else{
if(truth(binary("===",s264.get("k"),"magneto"))){
{ final Scope s329=new Scope(s264);
discard(call(s329.get("r"),args(8,3,12,4,"#7b3c62")));
discard(call(s329.get("r"),args(8,5,3,7,"#723657")));
discard(call(s329.get("r"),args(18,5,3,7,"#723657")));
discard(call(s329.get("r"),args(11,5,7,2,"#a75b7a")));
if(truth(unary("!",s329.get("back")))){
{ final Scope s330=new Scope(s329);
discard(call(s330.get("r"),args((truth(s330.get("side"))?18:11),9,2,1,"#332d39")));
if(truth(unary("!",s330.get("side")))){
discard(call(s330.get("r"),args(16,9,2,1,"#332d39")));
}
discard(call(s330.get("r"),args(12,12,5,1,"#70434b")));
}
}
discard(call(s329.get("r"),args(11,15,7,2,"#b4758c")));
discard(call(s329.get("r"),args(13,17,3,6,"#73394f")));
discard(call(s329.get("r"),args(9,22,11,2,"#b7a4a6")));
discard(call(s329.get("r"),args(3,binary("-",21,s329.get("step")),5,2,"#ca79a0")));
discard(call(s329.get("r"),args(21,binary("+",21,s329.get("step")),5,2,"#ca79a0")));
}
}
else{
if(truth(binary("===",s264.get("k"),"ultron"))){
{ final Scope s331=new Scope(s264);
discard(call(s331.get("poly"),args(arr(arr(8,6),arr(10,3),arr(19,3),arr(21,6),arr(20,12),arr(18,14),arr(10,14),arr(8,12)),"#9ca9ac")));
discard(call(s331.get("r"),args(10,4,9,2,"#c2c9c5")));
if(truth(unary("!",s331.get("back")))){
{ final Scope s332=new Scope(s331);
discard(call(s332.get("r"),args((truth(s332.get("side"))?18:10),9,3,1,"#e74c4c")));
if(truth(unary("!",s332.get("side")))){
discard(call(s332.get("r"),args(16,9,3,1,"#e74c4c")));
}
discard(call(s332.get("r"),args(12,12,6,1,"#303b43")));
discard(call(s332.get("r"),args(13,13,4,1,"#bdc4c2")));
}
}
discard(call(s331.get("r"),args(8,15,3,7,"#9ba9ac")));
discard(call(s331.get("r"),args(18,15,3,7,"#9ba9ac")));
discard(call(s331.get("r"),args(12,17,5,4,"#c8494c")));
discard(call(s331.get("r"),args(13,18,3,2,"#f0706c")));
discard(call(s331.get("r"),args(9,22,11,1,"#495961")));
discard(call(s331.get("r"),args(4,binary("-",20,s331.get("step")),4,2,"#aab7ba")));
discard(call(s331.get("r"),args(21,binary("+",20,s331.get("step")),4,2,"#aab7ba")));
}
}
else{
if(truth(binary("===",s264.get("k"),"loki"))){
{ final Scope s333=new Scope(s264);
discard(call(s333.get("r"),args(9,4,10,2,"#d8bb66")));
discard(call(s333.get("poly"),args(arr(arr(8,5),arr(5,1),arr(5,0),arr(10,3)),"#dec778")));
discard(call(s333.get("poly"),args(arr(arr(18,3),arr(23,0),arr(23,2),arr(20,6)),"#dec778")));
discard(call(s333.get("r"),args(13,15,2,8,"#c8af67")));
}
}
else{
if(truth(binary("===",s264.get("k"),"custom"))){
{ final Scope s334=new Scope(s264);
s334.let("p",get(s334.get("c"),"power"));
if(truth(binary("===",s334.get("p"),"Ninja"))){
{ final Scope s335=new Scope(s334);
discard(call(s335.get("r"),args(8,7,12,4,call(s335.get("shade"),args(get(s335.get("c"),"body"),0.46)))));
if(truth(unary("!",s335.get("back")))){
discard(call(s335.get("r"),args((truth(s335.get("side"))?17:10),8,(truth(s335.get("side"))?3:8),1,"#e8dcbb")));
}
discard(call(s335.get("r"),args(25,binary("+",16,s335.get("step")),1,10,"#d4dfd4")));
}
}
else{
if(truth(binary("===",s334.get("p"),"Gelo"))){
{ final Scope s336=new Scope(s334);
discard(call(s336.get("r"),args(13,16,2,5,"#c9eff0")));
discard(call(s336.get("r"),args(11,18,6,1,"#c9eff0")));
discard(call(s336.get("r"),args(12,17,4,3,"#b0dede")));
}
}
else{
if(truth(binary("===",s334.get("p"),"Velocidade"))){
{ final Scope s337=new Scope(s334);
discard(call(s337.get("poly"),args(arr(arr(15,15),arr(12,18),arr(15,18),arr(12,22),arr(17,17),arr(14,17)),"#f5e5ad")));
}
}
else{
if(truth(binary("===",s334.get("p"),"Super Força"))){
{ final Scope s338=new Scope(s334);
discard(call(s338.get("r"),args(binary("-",5,s338.get("wide")),18,binary("+",4,s338.get("wide")),4,call(s338.get("shade"),args(get(s338.get("c"),"body"),0.6)))));
discard(call(s338.get("r"),args(binary("+",21,s338.get("wide")),18,binary("+",4,s338.get("wide")),4,call(s338.get("shade"),args(get(s338.get("c"),"body"),0.6)))));
}
}
else{
if(truth(binary("===",s334.get("p"),"Telecinese"))){
{ final Scope s339=new Scope(s334);
discard(call(s339.get("r"),args(12,16,5,1,"#e2deef")));
discard(call(s339.get("r"),args(11,17,1,3,"#e2deef")));
discard(call(s339.get("r"),args(17,17,1,3,"#e2deef")));
discard(call(s339.get("r"),args(12,20,5,1,"#e2deef")));
}
}
else{
{ final Scope s340=new Scope(s334);
discard(call(s340.get("r"),args(13,17,3,3,"#efe9c3")));
discard(call(s340.get("r"),args(14,18,1,1,get(s340.get("c"),"body"))));
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
discard(method(s264.get("ctx"),"restore",args()));
}));
}
private void install_NPC_PHASE(){global.let("NPC_PHASE",fn(global,(s341,a342)->{
s341.let("id",argument(a342,0));
if(always()) throw returned(binary("/",method(arr(spread(s341.get("id"))),"reduce",args(fn(s341,(s343,a344)->{
s343.let("n",argument(a344,0));
s343.let("c",argument(a344,1));
throw returned(binary("+",s343.get("n"),method(s343.get("c"),"charCodeAt",args(0))));
}),0)),7));
}));
}
private void install_PAL(){global.let("PAL",obj(kv(0,"#223952"),kv(1,"#e0bd7f"),kv(2,"#75b55c"),kv(3,"#cbb993"),kv(4,"#586c85"),kv(5,"#8677c1"),kv(6,"#808e83"),kv(7,"#35947e"),kv(8,"#d4bb77"),kv(9,"#83716d"),kv(10,"#77767f"),kv(11,"#354d49"),kv(12,"#39536c")));
}
private void install_draw_tile(){global.let("draw_tile",fn(global,(s345,a346)->{
s345.let("ctx",argument(a346,0));
s345.let("tx",argument(a346,1));
s345.let("ty",argument(a346,2));
s345.let("x",argument(a346,3));
s345.let("y",argument(a346,4));
s345.let("t",method(get(s345.get("this"),"map"),"at",args(s345.get("tx"),s345.get("ty"))));
s345.let("n",call(s345.get("noise"),args(s345.get("tx"),s345.get("ty"),3)));
s345.let("r",fn(s345,(s347,a348)->{
s347.let("a",argument(a348,0));
s347.let("b",argument(a348,1));
s347.let("w",argument(a348,2));
s347.let("h",argument(a348,3));
s347.let("c",argument(a348,4));
discard(put(s347.get("ctx"),"fillStyle",s347.get("c")));
discard(method(s347.get("ctx"),"fillRect",args(binary("+",s347.get("x"),s347.get("a")),binary("+",s347.get("y"),s347.get("b")),s347.get("w"),s347.get("h"))));
}));
discard(call(s345.get("r"),args(0,0,32,32,get(s345.get("PAL"),s345.get("t")))));
s345.let("realm",get(method(get(s345.get("this"),"map"),"region",args(binary("*",s345.get("tx"),32),binary("*",s345.get("ty"),32))),"realm"));
if(truth(method(arr(9,10,11,12),"includes",args(s345.get("t"))))){
{ final Scope s349=new Scope(s345);
s349.let("dark",(truth(binary("===",s349.get("t"),9))?"#514959":(truth(binary("===",s349.get("t"),10))?"#66636c":"#233834")));
s349.let("light",(truth(binary("===",s349.get("t"),9))?"#b39a7e":(truth(binary("===",s349.get("t"),10))?"#918e94":"#718b7d")));
discard(call(s349.get("r"),args(0,30,32,2,s349.get("dark"))));
discard(call(s349.get("r"),args(30,0,2,32,s349.get("dark"))));
discard(call(s349.get("r"),args(3,3,26,1,s349.get("light"))));
if(truth(binary("===",s349.get("t"),9))){
{ final Scope s350=new Scope(s349);
discard(call(s350.get("r"),args(4,8,22,1,"#ab8270")));
discard(call(s350.get("r"),args(4,20,22,1,"#695563")));
if(truth(binary(">",s350.get("n"),0.85))){
discard(call(s350.get("r"),args(15,10,2,10,"#e8ba69")));
}
}
}
if(truth(and(()->binary("===",s349.get("t"),10),()->binary(">",s349.get("n"),0.6)))){
{ final Scope s351=new Scope(s349);
discard(call(s351.get("r"),args(8,11,12,1,s351.get("dark"))));
discard(call(s351.get("r"),args(19,11,1,13,s351.get("dark"))));
discard(call(s351.get("r"),args(7,26,3,2,s351.get("light"))));
}
}
if(truth(and(()->binary("===",s349.get("t"),11),()->binary(">",s349.get("n"),0.7)))){
{ final Scope s352=new Scope(s349);
discard(call(s352.get("r"),args(4,6,10,1,"#599e73")));
discard(call(s352.get("r"),args(13,6,1,10,"#599e73")));
discard(call(s352.get("r"),args(20,25,5,2,"#994f59")));
}
}
if(truth(binary("===",s349.get("t"),12))){
{ final Scope s353=new Scope(s349);
discard(call(s353.get("r"),args(3,3,26,1,"#527797")));
discard(call(s353.get("r"),args(0,30,32,2,"#283e59")));
if(truth(binary(">",s353.get("n"),0.7))){
{ final Scope s354=new Scope(s353);
discard(call(s354.get("r"),args(8,15,4,2,"#92d0ea")));
discard(call(s354.get("r"),args(13,16,6,2,"#7163c2")));
}
}
}
}
if(always()) throw returned(null);
}
}
if(truth(binary("===",s345.get("t"),get(s345.get("TILE"),"WATER")))){
{ final Scope s355=new Scope(s345);
discard(call(s355.get("r"),args(0,0,32,32,(truth(binary(">",s355.get("n"),0.5))?"#346079":"#305b73"))));
discard(call(s355.get("r"),args(binary("+",2,method(s355.get("Math"),"floor",args(binary("*",s355.get("n"),16)))),12,8,1,"#48768a")));
discard(call(s355.get("r"),args(18,25,7,1,"#49768b")));
if(always()) throw returned(null);
}
}
if(truth(method(arr(get(s345.get("TILE"),"GRASS"),get(s345.get("TILE"),"WAKANDA"),get(s345.get("TILE"),"RUINS"),get(s345.get("TILE"),"ASGARD")),"includes",args(s345.get("t"))))){
{ final Scope s356=new Scope(s345);
s356.let("dark",(truth(binary("===",s356.get("t"),7))?"#226956":(truth(binary("===",s356.get("t"),8))?"#b69953":(truth(binary("===",s356.get("t"),6))?"#707e76":"#66986e"))));
s356.let("light",(truth(binary("===",s356.get("t"),7))?"#64d5a1":(truth(binary("===",s356.get("t"),8))?"#f2dc9c":"#8eb489")));
{final Scope s357=new Scope(s356);s357.let("i",0);
while(truth(binary("<",s357.get("i"),5))){try{{ final Scope s358=new Scope(s357);
s358.let("px",method(s358.get("Math"),"floor",args(binary("*",call(s358.get("noise"),args(s358.get("tx"),s358.get("ty"),binary("+",s358.get("i"),20))),27))));
s358.let("py",method(s358.get("Math"),"floor",args(binary("*",call(s358.get("noise"),args(s358.get("tx"),s358.get("ty"),binary("+",s358.get("i"),30))),28))));
discard(call(s358.get("r"),args(s358.get("px"),s358.get("py"),3,1,s358.get("dark"))));
discard(call(s358.get("r"),args(binary("+",s358.get("px"),1),binary("-",s358.get("py"),1),1,1,s358.get("light"))));
}
}catch(Continue c){}catch(Break b){break;}discard(update(s357,"i",1,true));}}
if(truth(binary(">",s356.get("n"),0.94))){
{ final Scope s359=new Scope(s356);
discard(call(s359.get("r"),args(14,17,2,3,(truth(binary("===",s359.get("t"),7))?"#aa85b8":"#e8dbae"))));
discard(call(s359.get("r"),args(17,16,2,3,(truth(binary("===",s359.get("t"),7))?"#c19aca":"#edc891"))));
discard(call(s359.get("r"),args(15,21,1,3,s359.get("dark"))));
}
}
if(truth(and(()->binary("===",s356.get("t"),6),()->binary(">",s356.get("n"),0.8)))){
{ final Scope s360=new Scope(s356);
discard(call(s360.get("r"),args(4,6,14,9,"#95a098")));
discard(call(s360.get("r"),args(5,7,12,7,"#a8afa0")));
discard(call(s360.get("r"),args(5,9,12,1,"#949f97")));
}
}
}
}
else{
if(truth(binary("===",s345.get("t"),get(s345.get("TILE"),"PATH")))){
{ final Scope s361=new Scope(s345);
s361.let("region",s361.get("realm"));
if(truth(binary("===",s361.get("region"),"ny"))){
{ final Scope s362=new Scope(s361);
discard(call(s362.get("r"),args(0,0,32,32,"#384c67")));
discard(call(s362.get("r"),args(0,30,32,2,"#8195ad")));
discard(call(s362.get("r"),args(31,0,1,32,"#627f9e")));
if(truth(binary("===",binary("%",s362.get("tx"),4),0))){
discard(call(s362.get("r"),args(7,15,16,2,"#e5bf67")));
}
}
}
else{
if(truth(binary("===",s361.get("region"),"asgard"))){
{ final Scope s363=new Scope(s361);
discard(call(s363.get("r"),args(0,0,32,32,"#e4c47c")));
discard(call(s363.get("r"),args(0,30,32,2,"#a18a56")));
discard(call(s363.get("r"),args(15,0,1,30,"#b59c69")));
discard(call(s363.get("r"),args(3,4,10,1,"#fff0bb")));
}
}
else{
if(truth(binary("===",s361.get("region"),"wakanda"))){
{ final Scope s364=new Scope(s361);
discard(call(s364.get("r"),args(0,0,32,32,"#b89568")));
discard(call(s364.get("r"),args(0,28,32,3,"#5d5691")));
discard(call(s364.get("r"),args(2,2,2,24,"#62d4cd")));
}
}
else{
if(truth(binary("===",s361.get("region"),"latveria"))){
{ final Scope s365=new Scope(s361);
discard(call(s365.get("r"),args(0,0,32,32,"#4e6360")));
discard(call(s365.get("r"),args(0,30,32,2,"#263b36")));
discard(call(s365.get("r"),args(15,0,1,30,"#9baba3")));
discard(call(s365.get("r"),args(2,3,3,3,"#67b686")));
}
}
else{
if(truth(binary("===",s361.get("region"),"faded"))){
{ final Scope s366=new Scope(s361);
discard(call(s366.get("r"),args(0,0,32,32,"#87828a")));
discard(call(s366.get("r"),args(0,30,32,2,"#66616b")));
discard(call(s366.get("r"),args(7,11,12,1,"#706a76")));
}
}
else{
if(truth(binary("===",s361.get("region"),"crossroads"))){
{ final Scope s367=new Scope(s361);
discard(call(s367.get("r"),args(0,0,32,32,"#a17d68")));
discard(call(s367.get("r"),args(0,10,32,1,"#67525b")));
discard(call(s367.get("r"),args(0,22,32,1,"#67525b")));
discard(call(s367.get("r"),args(3,1,1,30,"#d1ae80")));
}
}
else{
{ final Scope s368=new Scope(s361);
discard(call(s368.get("r"),args(4,6,10,2,"#dbcaa4")));
discard(call(s368.get("r"),args(21,23,5,1,"#b5a881")));
discard(call(s368.get("r"),args(9,26,2,2,"#b5a881")));
}
}
}
}
}
}
}
for(Object v370:list(arr(arr(unary("-",1),0,0,0,2,32),arr(1,0,30,0,2,32),arr(0,unary("-",1),0,0,32,2),arr(0,1,0,30,32,2)))){final Scope s369=new Scope(s361);Object v371=v370;
s369.let("dx",get(v371,0));
s369.let("dy",get(v371,1));
s369.let("a",get(v371,2));
s369.let("b",get(v371,3));
s369.let("w",get(v371,4));
s369.let("h",get(v371,5));
try{if(truth(binary("!==",method(get(s369.get("this"),"map"),"at",args(binary("+",s369.get("tx"),s369.get("dx")),binary("+",s369.get("ty"),s369.get("dy")))),get(s369.get("TILE"),"PATH")))){
discard(call(s369.get("r"),args(s369.get("a"),s369.get("b"),s369.get("w"),s369.get("h"),(truth(binary("===",s369.get("region"),"ny"))?"#6e857e":"#9eaf89"))));
}
}catch(Continue c){}catch(Break b){break;}}
}
}
else{
if(truth(binary("===",s345.get("t"),get(s345.get("TILE"),"CITY")))){
{ final Scope s372=new Scope(s345);
discard(call(s372.get("r"),args(0,31,32,1,"#354b64")));
discard(call(s372.get("r"),args(31,0,1,32,"#354b64")));
discard(call(s372.get("r"),args(2,2,28,1,"#8995a5")));
if(truth(binary(">",s372.get("n"),0.7))){
{ final Scope s373=new Scope(s372);
discard(call(s373.get("r"),args(13,17,9,2,"#6c96b0")));
discard(call(s373.get("r"),args(19,18,1,5,"#dcb784")));
}
}
}
}
else{
if(truth(binary("===",s345.get("t"),get(s345.get("TILE"),"BRIDGE")))){
{ final Scope s374=new Scope(s345);
s374.let("bif",binary("===",s374.get("realm"),"bifrost"));
discard(call(s374.get("r"),args(0,0,32,32,(truth(s374.get("bif"))?"#807cc2":"#aa9674"))));
discard(call(s374.get("r"),args(0,29,32,2,(truth(s374.get("bif"))?"#504c7c":"#826b51"))));
discard(call(s374.get("r"),args(0,0,32,1,(truth(s374.get("bif"))?"#ecdeff":"#c5b28b"))));
if(truth(s374.get("bif"))){
{ final Scope s375=new Scope(s374);
s375.let("cols",arr("#f07c9f","#f2c867","#62d4ae","#70b3ee","#bc8bed"));
discard(call(s375.get("r"),args(0,8,32,6,get(s375.get("cols"),binary("%",s375.get("tx"),5)))));
discard(call(s375.get("r"),args(0,21,32,4,get(s375.get("cols"),binary("%",binary("+",s375.get("tx"),2),5)))));
}
}
else{
discard(call(s374.get("r"),args(7,1,1,28,"#907d5c")));
}
if(truth(binary("!==",method(get(s374.get("this"),"map"),"at",args(binary("-",s374.get("tx"),1),s374.get("ty"))),5))){
discard(call(s374.get("r"),args(0,0,3,32,(truth(s374.get("bif"))?"#ead194":"#715d47"))));
}
if(truth(binary("!==",method(get(s374.get("this"),"map"),"at",args(binary("+",s374.get("tx"),1),s374.get("ty"))),5))){
discard(call(s374.get("r"),args(29,0,3,32,(truth(s374.get("bif"))?"#ead194":"#715d47"))));
}
}
}
else{
if(truth(binary("===",s345.get("t"),get(s345.get("TILE"),"SAND")))){
{ final Scope s376=new Scope(s345);
discard(call(s376.get("r"),args(4,11,2,1,"#b5b186")));
discard(call(s376.get("r"),args(23,22,3,1,"#b9b287")));
if(truth(binary("===",method(get(s376.get("this"),"map"),"at",args(s376.get("tx"),binary("+",s376.get("ty"),1))),0))){
{ final Scope s377=new Scope(s376);
discard(call(s377.get("r"),args(0,23,32,9,"#86b8a4")));
discard(call(s377.get("r"),args(0,25,32,2,"#c7debd")));
discard(call(s377.get("r"),args(0,30,32,2,"#5f999f")));
}
}
}
}
}
}
}
}
}));}
private void install_draw_prop(){global.let("draw_prop",fn(global,(s378,a379)->{
s378.let("ctx",argument(a379,0));
s378.let("p",argument(a379,1));
s378.let("time",argument(a379,2));
s378.let("r",fn(s378,(s380,a381)->{
s380.let("x",argument(a381,0));
s380.let("y",argument(a381,1));
s380.let("w",argument(a381,2));
s380.let("h",argument(a381,3));
s380.let("c",argument(a381,4));
discard(put(s380.get("ctx"),"fillStyle",s380.get("c")));
discard(method(s380.get("ctx"),"fillRect",args(method(s380.get("Math"),"round",args(s380.get("x"))),method(s380.get("Math"),"round",args(s380.get("y"))),s380.get("w"),s380.get("h"))));
}));
Object v382=s378.get("p");
s378.let("x",get(v382,"x"));
s378.let("y",get(v382,"y"));
s378.let("w",get(v382,"w"));
s378.let("h",get(v382,"h"));
s378.let("kind",get(v382,"kind"));
if(truth(binary("===",s378.get("kind"),"police-tape"))){
{ final Scope s383=new Scope(s378);
discard(call(s383.get("r"),args(s383.get("x"),s383.get("y"),s383.get("w"),4,"#e3ba5b")));
{final Scope s384=new Scope(s383);s384.let("i",0);
while(truth(binary("<",s384.get("i"),s384.get("w")))){try{discard(call(s384.get("r"),args(binary("+",s384.get("x"),s384.get("i")),s384.get("y"),6,4,"#30415d")));
}catch(Continue c){}catch(Break b){break;}discard(s384.set("i",binary("+",s384.get("i"),15)));}}
discard(call(s383.get("r"),args(s383.get("x"),binary("-",s383.get("y"),3),3,20,"#657f96")));
discard(call(s383.get("r"),args(binary("-",binary("+",s383.get("x"),s383.get("w")),3),binary("-",s383.get("y"),3),3,20,"#657f96")));
if(always()) throw returned(null);
}
}
if(truth(binary("===",s378.get("kind"),"swing"))){
{ final Scope s385=new Scope(s378);
discard(call(s385.get("r"),args(s385.get("x"),binary("-",s385.get("y"),48),5,49,"#53515e")));
discard(call(s385.get("r"),args(binary("+",s385.get("x"),60),binary("-",s385.get("y"),48),5,49,"#53515e")));
discard(call(s385.get("r"),args(s385.get("x"),binary("-",s385.get("y"),49),65,5,"#938792")));
discard(call(s385.get("r"),args(binary("+",s385.get("x"),20),binary("-",s385.get("y"),45),2,33,"#afa4ad")));
discard(call(s385.get("r"),args(binary("+",s385.get("x"),42),binary("-",s385.get("y"),45),2,33,"#afa4ad")));
discard(call(s385.get("r"),args(binary("+",s385.get("x"),17),binary("-",s385.get("y"),12),30,4,"#605561")));
if(always()) throw returned(null);
}
}
if(truth(binary("===",s378.get("kind"),"train"))){
{ final Scope s386=new Scope(s378);
discard(call(s386.get("r"),args(s386.get("x"),binary("-",s386.get("y"),53),s386.get("w"),54,"#33445b")));
discard(call(s386.get("r"),args(binary("+",s386.get("x"),4),binary("-",s386.get("y"),48),binary("-",s386.get("w"),8),40,"#678391")));
discard(call(s386.get("r"),args(binary("+",s386.get("x"),5),binary("-",s386.get("y"),15),binary("-",s386.get("w"),10),3,"#c2a258")));
{final Scope s387=new Scope(s386);s387.let("i",15);
while(truth(binary("<",s387.get("i"),binary("-",s387.get("w"),20)))){try{{ final Scope s388=new Scope(s387);
discard(call(s388.get("r"),args(binary("+",s388.get("x"),s388.get("i")),binary("-",s388.get("y"),42),31,20,"#263a50")));
discard(call(s388.get("r"),args(binary("+",binary("+",s388.get("x"),s388.get("i")),3),binary("-",s388.get("y"),40),25,5,"#89b6c7")));
}
}catch(Continue c){}catch(Break b){break;}discard(s387.set("i",binary("+",s387.get("i"),47)));}}
discard(call(s386.get("r"),args(binary("+",s386.get("x"),8),binary("-",s386.get("y"),5),binary("-",s386.get("w"),16),5,"#202e45")));
discard(put(s386.get("ctx"),"font","12px monospace"));
discard(put(s386.get("ctx"),"fillStyle","#d9d4b2"));
discard(method(s386.get("ctx"),"fillText",args("4",binary("+",s386.get("x"),20),binary("-",s386.get("y"),20))));
if(always()) throw returned(null);
}
}
if(truth(binary("===",s378.get("kind"),"bamboo"))){
{ final Scope s389=new Scope(s378);
{final Scope s390=new Scope(s389);s390.let("k",0);
while(truth(binary("<",s390.get("k"),3))){try{{ final Scope s391=new Scope(s390);
discard(call(s391.get("r"),args(binary("+",binary("-",s391.get("x"),12),binary("*",s391.get("k"),10)),binary("+",binary("-",s391.get("y"),52),binary("*",s391.get("k"),5)),4,binary("-",53,binary("*",s391.get("k"),5)),"#3d98a7")));
{final Scope s392=new Scope(s391);s392.let("j",0);
while(truth(binary("<",s392.get("j"),5))){try{discard(call(s392.get("r"),args(binary("+",binary("-",s392.get("x"),12),binary("*",s392.get("k"),10)),binary("+",binary("-",s392.get("y"),47),binary("*",s392.get("j"),9)),4,2,"#b1cf78")));
}catch(Continue c){}catch(Break b){break;}discard(update(s392,"j",1,true));}}
discard(call(s391.get("r"),args(binary("+",binary("-",s391.get("x"),19),binary("*",s391.get("k"),10)),binary("+",binary("-",s391.get("y"),44),binary("*",s391.get("k"),6)),16,3,"#59b5a0")));
discard(call(s391.get("r"),args(binary("+",binary("-",s391.get("x"),10),binary("*",s391.get("k"),10)),binary("+",binary("-",s391.get("y"),36),binary("*",s391.get("k"),4)),13,3,"#6ed6b3")));
}
}catch(Continue c){}catch(Break b){break;}discard(update(s390,"k",1,true));}}
if(always()) throw returned(null);
}
}
if(truth(and(()->method(arr("mirror","dead-tree","lantern","steam","rail","star","aurora","rune","fissure","orb","pillar"),"includes",args(s378.get("kind"))),()->unary("!",and(()->binary("===",s378.get("kind"),"mirror"),()->binary(">",s378.get("w"),40)))))){
{ final Scope s393=new Scope(s378);
if(truth(binary("===",s393.get("kind"),"mirror"))){
{ final Scope s394=new Scope(s393);
discard(call(s394.get("r"),args(binary("-",s394.get("x"),8),binary("-",s394.get("y"),42),16,42,"#809b8e")));
discard(call(s394.get("r"),args(binary("-",s394.get("x"),5),binary("-",s394.get("y"),39),10,35,"#213a38")));
discard(call(s394.get("r"),args(binary("-",s394.get("x"),4),binary("-",s394.get("y"),35),2,20,"#8eda9e")));
discard(call(s394.get("r"),args(binary("+",s394.get("x"),4),binary("-",s394.get("y"),19),2,8,"#bc7581")));
}
}
if(truth(binary("===",s393.get("kind"),"dead-tree"))){
{ final Scope s395=new Scope(s393);
discard(call(s395.get("r"),args(binary("-",s395.get("x"),3),binary("-",s395.get("y"),40),6,42,"#4f4b58")));
discard(call(s395.get("r"),args(binary("-",s395.get("x"),13),binary("-",s395.get("y"),33),14,4,"#605a67")));
discard(call(s395.get("r"),args(binary("+",s395.get("x"),3),binary("-",s395.get("y"),27),13,4,"#605a67")));
discard(call(s395.get("r"),args(binary("-",s395.get("x"),13),binary("-",s395.get("y"),44),3,13,"#605a67")));
discard(call(s395.get("r"),args(binary("+",s395.get("x"),13),binary("-",s395.get("y"),37),3,13,"#605a67")));
}
}
if(truth(binary("===",s393.get("kind"),"lantern"))){
{ final Scope s396=new Scope(s393);
discard(call(s396.get("r"),args(binary("-",s396.get("x"),2),binary("-",s396.get("y"),35),4,37,"#4a4354")));
discard(call(s396.get("r"),args(binary("-",s396.get("x"),7),binary("-",s396.get("y"),39),14,12,"#e6b956")));
discard(call(s396.get("r"),args(binary("-",s396.get("x"),4),binary("-",s396.get("y"),37),8,8,"#ffdda0")));
discard(call(s396.get("r"),args(binary("-",s396.get("x"),12),binary("-",s396.get("y"),21),24,3,"#ecbd6133")));
}
}
if(truth(binary("===",s393.get("kind"),"steam"))){
{final Scope s397=new Scope(s393);s397.let("k",0);
while(truth(binary("<",s397.get("k"),4))){try{discard(call(s397.get("r"),args(binary("+",s397.get("x"),binary("*",method(s397.get("Math"),"sin",args(binary("+",s397.get("time"),s397.get("k")))),3)),binary("-",binary("-",binary("-",s397.get("y"),6),binary("*",s397.get("k"),5)),binary("%",binary("*",s397.get("time"),4),8)),binary("-",9,s397.get("k")),3,"#d3d7df66")));
}catch(Continue c){}catch(Break b){break;}discard(update(s397,"k",1,true));}}
}
if(truth(binary("===",s393.get("kind"),"rail"))){
{ final Scope s398=new Scope(s393);
discard(call(s398.get("r"),args(s398.get("x"),s398.get("y"),3,32,"#bac8d2")));
discard(call(s398.get("r"),args(binary("+",s398.get("x"),22),s398.get("y"),3,32,"#bac8d2")));
{final Scope s399=new Scope(s398);s399.let("k",3);
while(truth(binary("<",s399.get("k"),32))){try{discard(call(s399.get("r"),args(binary("-",s399.get("x"),3),binary("+",s399.get("y"),s399.get("k")),31,3,"#614d52")));
}catch(Continue c){}catch(Break b){break;}discard(s399.set("k",binary("+",s399.get("k"),8)));}}
}
}
if(truth(binary("===",s393.get("kind"),"star"))){
{ final Scope s400=new Scope(s393);
discard(call(s400.get("r"),args(s400.get("x"),s400.get("y"),1,5,"#fff0d5")));
discard(call(s400.get("r"),args(binary("-",s400.get("x"),2),binary("+",s400.get("y"),2),5,1,"#a1cffa")));
}
}
if(truth(binary("===",s393.get("kind"),"aurora"))){
{ final Scope s401=new Scope(s393);
{final Scope s402=new Scope(s401);s402.let("k",0);
while(truth(binary("<",s402.get("k"),16))){try{discard(call(s402.get("r"),args(binary("+",s402.get("x"),binary("/",binary("*",s402.get("k"),s402.get("w")),16)),binary("+",s402.get("y"),binary("*",method(s402.get("Math"),"sin",args(binary("+",binary("*",s402.get("k"),0.6),binary("*",s402.get("time"),0.2)))),12)),binary("/",s402.get("w"),16),7,get(arr("#8de1d055","#9696e955","#dca3f755"),binary("%",s402.get("k"),3)))));
}catch(Continue c){}catch(Break b){break;}discard(update(s402,"k",1,true));}}
}
}
if(truth(binary("===",s393.get("kind"),"rune"))){
{ final Scope s403=new Scope(s393);
discard(call(s403.get("r"),args(binary("-",s403.get("x"),14),binary("-",s403.get("y"),14),28,2,"#82d19b")));
discard(call(s403.get("r"),args(binary("-",s403.get("x"),14),binary("+",s403.get("y"),14),28,2,"#82d19b")));
discard(call(s403.get("r"),args(binary("-",s403.get("x"),14),binary("-",s403.get("y"),14),2,28,"#82d19b")));
discard(call(s403.get("r"),args(binary("+",s403.get("x"),12),binary("-",s403.get("y"),14),2,28,"#82d19b")));
discard(call(s403.get("r"),args(binary("-",s403.get("x"),3),binary("-",s403.get("y"),4),6,8,"#c3d9be")));
}
}
if(truth(binary("===",s393.get("kind"),"fissure"))){
{ final Scope s404=new Scope(s393);
{final Scope s405=new Scope(s404);s405.let("k",0);
while(truth(binary("<",s405.get("k"),10))){try{{ final Scope s406=new Scope(s405);
discard(call(s406.get("r"),args(binary("+",s406.get("x"),binary("*",method(s406.get("Math"),"sin",args(binary("+",s406.get("k"),binary("*",s406.get("time"),0.2)))),5)),binary("-",s406.get("y"),binary("*",s406.get("k"),5)),5,7,"#61c4f3")));
discard(call(s406.get("r"),args(binary("+",binary("+",s406.get("x"),binary("*",method(s406.get("Math"),"sin",args(binary("+",s406.get("k"),binary("*",s406.get("time"),0.2)))),5)),2),binary("-",s406.get("y"),binary("*",s406.get("k"),5)),1,5,"#c6edff")));
}
}catch(Continue c){}catch(Break b){break;}discard(update(s405,"k",1,true));}}
discard(call(s404.get("r"),args(binary("-",s404.get("x"),19),binary("+",s404.get("y"),2),38,2,"#45779b")));
}
}
if(truth(binary("===",s393.get("kind"),"orb"))){
{ final Scope s407=new Scope(s393);
discard(call(s407.get("r"),args(binary("-",s407.get("x"),28),binary("-",s407.get("y"),67),56,52,"#112c2a")));
discard(call(s407.get("r"),args(binary("-",s407.get("x"),23),binary("-",s407.get("y"),74),46,67,"#15332f")));
discard(call(s407.get("r"),args(binary("-",s407.get("x"),15),binary("-",s407.get("y"),79),30,78,"#193c32")));
discard(call(s407.get("r"),args(binary("-",s407.get("x"),22),binary("-",s407.get("y"),61),3,38,"#78d196")));
discard(call(s407.get("r"),args(binary("+",s407.get("x"),18),binary("-",s407.get("y"),46),4,29,"#aa759d")));
discard(call(s407.get("r"),args(binary("-",s407.get("x"),3),binary("-",s407.get("y"),45),6,7,"#d3e5c3")));
{final Scope s408=new Scope(s407);s408.let("i",0);
while(truth(binary("<",s408.get("i"),4))){try{discard(call(s408.get("r"),args(binary("+",binary("-",s408.get("x"),36),binary("*",s408.get("i"),24)),binary("+",s408.get("y"),binary("*",method(s408.get("Math"),"sin",args(binary("+",s408.get("time"),s408.get("i")))),4)),5,2,"#bce4a5")));
}catch(Continue c){}catch(Break b){break;}discard(update(s408,"i",1,true));}}
}
}
if(truth(binary("===",s393.get("kind"),"pillar"))){
{ final Scope s409=new Scope(s393);
discard(call(s409.get("r"),args(binary("-",s409.get("x"),8),binary("-",s409.get("y"),48),16,50,"#648174")));
discard(call(s409.get("r"),args(binary("-",s409.get("x"),4),binary("-",s409.get("y"),44),4,40,"#afd5b8")));
discard(call(s409.get("r"),args(binary("+",s409.get("x"),5),binary("-",s409.get("y"),42),3,36,"#314d46")));
discard(call(s409.get("r"),args(binary("-",s409.get("x"),12),binary("-",s409.get("y"),51),24,5,"#90b49e")));
discard(call(s409.get("r"),args(binary("-",s409.get("x"),12),binary("-",s409.get("y"),4),24,7,"#354f44")));
}
}
if(always()) throw returned(null);
}
}
if(truth(method(arr("castle","mirror","nucleus","wall","tent","station","cantina","osc"),"includes",args(s378.get("kind"))))){
{ final Scope s410=new Scope(s378);
s410.let("lat",method(arr("castle","mirror","nucleus","wall"),"includes",args(s410.get("kind"))));
s410.let("base",(truth(s410.get("lat"))?"#354d46":(truth(binary("===",s410.get("kind"),"tent"))?"#b79870":(truth(binary("===",s410.get("kind"),"osc"))?"#536c7a":"#826858"))));
s410.let("edge",(truth(s410.get("lat"))?"#7fa28f":"#dbb486"));
discard(call(s410.get("r"),args(binary("-",s410.get("x"),3),binary("-",s410.get("y"),30),binary("+",s410.get("w"),6),binary("+",s410.get("h"),30),"#253437")));
discard(call(s410.get("r"),args(s410.get("x"),binary("-",s410.get("y"),27),s410.get("w"),binary("+",s410.get("h"),25),s410.get("base"))));
discard(call(s410.get("r"),args(binary("+",s410.get("x"),5),binary("-",s410.get("y"),23),binary("-",s410.get("w"),10),binary("-",s410.get("h"),28),s410.get("edge"))));
discard(call(s410.get("r"),args(binary("+",s410.get("x"),10),binary("-",s410.get("y"),19),binary("-",s410.get("w"),20),binary("-",s410.get("h"),37),s410.get("base"))));
if(truth(s410.get("lat"))){
{ final Scope s411=new Scope(s410);
{final Scope s412=new Scope(s411);s412.let("i",8);
while(truth(binary("<",s412.get("i"),s412.get("w")))){try{{ final Scope s413=new Scope(s412);
discard(call(s413.get("r"),args(binary("+",s413.get("x"),s413.get("i")),binary("-",s413.get("y"),40),15,18,"#658373")));
discard(call(s413.get("r"),args(binary("+",binary("+",s413.get("x"),s413.get("i")),3),binary("-",s413.get("y"),35),3,12,"#a9c4b0")));
discard(call(s413.get("r"),args(binary("+",s413.get("x"),s413.get("i")),binary("-",binary("+",s413.get("y"),s413.get("h")),60),16,28,"#1d3533")));
discard(call(s413.get("r"),args(binary("+",binary("+",s413.get("x"),s413.get("i")),5),binary("-",binary("+",s413.get("y"),s413.get("h")),54),3,18,"#7dda91")));
}
}catch(Continue c){}catch(Break b){break;}discard(s412.set("i",binary("+",s412.get("i"),27)));}}
discard(call(s411.get("r"),args(binary("+",s411.get("x"),6),binary("-",binary("+",s411.get("y"),s411.get("h")),24),binary("-",s411.get("w"),12),6,"#8a4557")));
}
}
else{
{final Scope s414=new Scope(s410);s414.let("i",15);
while(truth(binary("<",s414.get("i"),binary("-",s414.get("w"),10)))){try{{ final Scope s415=new Scope(s414);
discard(call(s415.get("r"),args(binary("+",s415.get("x"),s415.get("i")),binary("-",binary("+",s415.get("y"),s415.get("h")),60),19,26,"#385562")));
discard(call(s415.get("r"),args(binary("+",binary("+",s415.get("x"),s415.get("i")),2),binary("-",binary("+",s415.get("y"),s415.get("h")),57),15,20,(truth(binary("===",s415.get("kind"),"osc"))?"#88bdce":"#ddaa70"))));
discard(call(s415.get("r"),args(binary("+",binary("+",s415.get("x"),s415.get("i")),7),binary("-",binary("+",s415.get("y"),s415.get("h")),57),2,20,"#51465d")));
}
}catch(Continue c){}catch(Break b){break;}discard(s414.set("i",binary("+",s414.get("i"),35)));}}
}
discard(call(s410.get("r"),args(binary("-",binary("+",s410.get("x"),binary("/",s410.get("w"),2)),12),binary("-",binary("+",s410.get("y"),s410.get("h")),25),24,25,"#203a3a")));
discard(call(s410.get("r"),args(binary("-",binary("+",s410.get("x"),binary("/",s410.get("w"),2)),9),binary("-",binary("+",s410.get("y"),s410.get("h")),22),18,22,(truth(s410.get("lat"))?"#599676":"#8ac3cf"))));
if(truth(get(s410.get("p"),"label"))){
{ final Scope s416=new Scope(s410);
discard(put(s416.get("ctx"),"font","7px monospace"));
discard(put(s416.get("ctx"),"textAlign","center"));
discard(put(s416.get("ctx"),"fillStyle","#fff0c5"));
discard(method(s416.get("ctx"),"fillText",args(get(s416.get("p"),"label"),binary("+",s416.get("x"),binary("/",s416.get("w"),2)),binary("-",binary("+",s416.get("y"),s416.get("h")),28))));
}
}
if(always()) throw returned(null);
}
}
if(truth(binary("===",s378.get("kind"),"tree"))){
{ final Scope s417=new Scope(s378);
s417.let("palette",(truth(binary("===",get(s417.get("p"),"region"),7))?arr("#1b5c52","#2a8b67","#55b87a","#94d277"):(truth(binary("===",get(s417.get("p"),"region"),6))?arr("#364e54","#506e69","#699378","#8aaa84"):arr("#624342","#b17046","#d59353","#eab75a"))));
discard(call(s417.get("r"),args(binary("-",s417.get("x"),14),binary("-",s417.get("y"),5),30,9,"#254a3d38")));
discard(call(s417.get("r"),args(binary("-",s417.get("x"),4),binary("-",s417.get("y"),19),9,21,"#645746")));
discard(call(s417.get("r"),args(binary("-",s417.get("x"),2),binary("-",s417.get("y"),16),3,16,"#8a754f")));
discard(call(s417.get("r"),args(binary("-",s417.get("x"),18),binary("-",s417.get("y"),37),37,20,get(s417.get("palette"),0))));
discard(call(s417.get("r"),args(binary("-",s417.get("x"),14),binary("-",s417.get("y"),49),29,16,get(s417.get("palette"),0))));
discard(call(s417.get("r"),args(binary("-",s417.get("x"),9),binary("-",s417.get("y"),55),19,8,get(s417.get("palette"),0))));
discard(call(s417.get("r"),args(binary("-",s417.get("x"),16),binary("-",s417.get("y"),40),33,18,get(s417.get("palette"),1))));
discard(call(s417.get("r"),args(binary("-",s417.get("x"),12),binary("-",s417.get("y"),49),25,14,get(s417.get("palette"),1))));
discard(call(s417.get("r"),args(binary("-",s417.get("x"),7),binary("-",s417.get("y"),53),15,5,get(s417.get("palette"),2))));
discard(call(s417.get("r"),args(binary("-",s417.get("x"),12),binary("-",s417.get("y"),44),22,8,get(s417.get("palette"),2))));
discard(call(s417.get("r"),args(binary("-",s417.get("x"),15),binary("-",s417.get("y"),35),13,5,get(s417.get("palette"),2))));
discard(call(s417.get("r"),args(binary("-",s417.get("x"),9),binary("-",s417.get("y"),43),9,2,get(s417.get("palette"),3))));
discard(call(s417.get("r"),args(binary("+",s417.get("x"),4),binary("-",s417.get("y"),34),8,2,get(s417.get("palette"),3))));
discard(call(s417.get("r"),args(binary("-",s417.get("x"),5),binary("-",s417.get("y"),27),9,2,get(s417.get("palette"),0))));
if(truth(binary("===",get(s417.get("p"),"region"),7))){
{ final Scope s418=new Scope(s417);
discard(call(s418.get("r"),args(binary("+",s418.get("x"),11),binary("-",s418.get("y"),37),2,4,"#beb671")));
discard(call(s418.get("r"),args(binary("-",s418.get("x"),9),binary("-",s418.get("y"),25),2,3,"#ac99bd")));
}
}
if(always()) throw returned(null);
}
}
if(truth(binary("===",s378.get("kind"),"rock"))){
{ final Scope s419=new Scope(s378);
discard(call(s419.get("r"),args(binary("-",s419.get("x"),12),binary("-",s419.get("y"),10),23,12,"#4e6c68")));
discard(call(s419.get("r"),args(binary("-",s419.get("x"),8),binary("-",s419.get("y"),15),16,5,"#7f9390")));
discard(call(s419.get("r"),args(binary("-",s419.get("x"),9),binary("-",s419.get("y"),9),17,7,"#8c9f95")));
discard(call(s419.get("r"),args(binary("-",s419.get("x"),5),binary("-",s419.get("y"),13),10,2,"#adba9f")));
if(always()) throw returned(null);
}
}
if(truth(binary("===",s378.get("kind"),"crystal"))){
{ final Scope s420=new Scope(s378);
discard(call(s420.get("r"),args(binary("-",s420.get("x"),4),binary("-",s420.get("y"),29),8,25,"#66c7eb")));
discard(call(s420.get("r"),args(binary("-",s420.get("x"),7),binary("-",s420.get("y"),22),3,17,"#7366c1")));
discard(call(s420.get("r"),args(binary("+",s420.get("x"),4),binary("-",s420.get("y"),19),5,14,"#397db3")));
discard(call(s420.get("r"),args(binary("-",s420.get("x"),2),binary("-",s420.get("y"),26),2,19,"#b7f0fb")));
if(always()) throw returned(null);
}
}
if(truth(binary("===",s378.get("kind"),"lamp"))){
{ final Scope s421=new Scope(s378);
discard(call(s421.get("r"),args(binary("-",s421.get("x"),2),binary("-",s421.get("y"),34),4,34,"#435557")));
discard(call(s421.get("r"),args(binary("-",s421.get("x"),5),binary("-",s421.get("y"),37),11,6,"#354c51")));
discard(call(s421.get("r"),args(binary("-",s421.get("x"),3),binary("-",s421.get("y"),35),7,3,"#e6cf8e")));
discard(call(s421.get("r"),args(binary("-",s421.get("x"),4),binary("-",s421.get("y"),1),9,3,"#546960")));
if(always()) throw returned(null);
}
}
if(truth(binary("===",s378.get("kind"),"column"))){
{ final Scope s422=new Scope(s378);
discard(call(s422.get("r"),args(binary("-",s422.get("x"),6),binary("-",s422.get("y"),32),12,31,"#d5c58f")));
discard(call(s422.get("r"),args(binary("-",s422.get("x"),3),binary("-",s422.get("y"),29),3,26,"#f0dfaf")));
discard(call(s422.get("r"),args(binary("+",s422.get("x"),3),binary("-",s422.get("y"),29),3,26,"#ab9b67")));
discard(call(s422.get("r"),args(binary("-",s422.get("x"),8),binary("-",s422.get("y"),33),16,4,"#e7d39e")));
discard(call(s422.get("r"),args(binary("-",s422.get("x"),8),binary("-",s422.get("y"),3),16,4,"#ad9e73")));
if(always()) throw returned(null);
}
}
if(truth(binary("===",s378.get("kind"),"boat"))){
{ final Scope s423=new Scope(s378);
discard(call(s423.get("r"),args(s423.get("x"),s423.get("y"),s423.get("w"),12,"#704e41")));
discard(call(s423.get("r"),args(binary("+",s423.get("x"),8),binary("-",s423.get("y"),7),binary("-",s423.get("w"),16),7,"#a6815a")));
discard(call(s423.get("r"),args(binary("+",s423.get("x"),12),binary("-",s423.get("y"),6),binary("-",s423.get("w"),24),2,"#d2ba89")));
discard(call(s423.get("r"),args(binary("+",s423.get("x"),15),binary("-",s423.get("y"),26),24,20,"#c4c3a5")));
discard(call(s423.get("r"),args(binary("+",s423.get("x"),15),binary("-",s423.get("y"),26),24,5,"#bd564d")));
discard(call(s423.get("r"),args(binary("+",s423.get("x"),20),binary("-",s423.get("y"),18),14,7,"#546c76")));
discard(call(s423.get("r"),args(binary("+",s423.get("x"),5),binary("+",s423.get("y"),5),58,1,"#cba36e")));
if(always()) throw returned(null);
}
}
if(truth(binary("===",s378.get("kind"),"fountain"))){
{ final Scope s424=new Scope(s378);
discard(call(s424.get("r"),args(binary("-",s424.get("x"),13),binary("-",s424.get("y"),3),26,7,"#8fa49b")));
discard(call(s424.get("r"),args(binary("-",s424.get("x"),11),binary("-",s424.get("y"),9),22,9,"#d1c9a7")));
discard(call(s424.get("r"),args(binary("-",s424.get("x"),9),binary("-",s424.get("y"),8),18,5,"#73aabb")));
discard(call(s424.get("r"),args(binary("-",s424.get("x"),3),binary("-",s424.get("y"),25),6,18,"#d6d4ba")));
discard(call(s424.get("r"),args(binary("-",s424.get("x"),6),binary("-",s424.get("y"),25),12,4,"#e3dcba")));
discard(call(s424.get("r"),args(binary("-",s424.get("x"),1),binary("+",binary("-",s424.get("y"),29),method(s424.get("Math"),"floor",args(method(s424.get("Math"),"sin",args(binary("*",s424.get("time"),2)))))),2,5,"#a8e0d4")));
if(always()) throw returned(null);
}
}
if(truth(binary("===",s378.get("kind"),"bench"))){
{ final Scope s425=new Scope(s378);
discard(call(s425.get("r"),args(binary("-",s425.get("x"),18),binary("-",s425.get("y"),17),36,5,"#ae8a59")));
discard(call(s425.get("r"),args(binary("-",s425.get("x"),18),binary("-",s425.get("y"),10),36,5,"#bb9865")));
discard(call(s425.get("r"),args(binary("-",s425.get("x"),18),binary("-",s425.get("y"),5),36,3,"#705b44")));
discard(call(s425.get("r"),args(binary("-",s425.get("x"),14),binary("-",s425.get("y"),4),3,7,"#394f4c")));
discard(call(s425.get("r"),args(binary("+",s425.get("x"),12),binary("-",s425.get("y"),4),3,7,"#394f4c")));
if(always()) throw returned(null);
}
}
if(truth(binary("===",s378.get("kind"),"bollard"))){
{ final Scope s426=new Scope(s378);
discard(call(s426.get("r"),args(binary("-",s426.get("x"),4),binary("-",s426.get("y"),12),8,13,"#4d625c")));
discard(call(s426.get("r"),args(binary("-",s426.get("x"),5),binary("-",s426.get("y"),13),10,4,"#a5b0a3")));
discard(call(s426.get("r"),args(binary("-",s426.get("x"),3),binary("-",s426.get("y"),6),6,2,"#ceb96a")));
if(always()) throw returned(null);
}
}
if(truth(binary("===",s378.get("kind"),"car"))){
{ final Scope s427=new Scope(s378);
s427.let("col",get(arr("#a84d46","#627d9b","#bcb273"),get(s427.get("p"),"variant")));
discard(call(s427.get("r"),args(binary("-",s427.get("x"),12),binary("-",s427.get("y"),26),24,33,"#30474b")));
discard(call(s427.get("r"),args(binary("-",s427.get("x"),10),binary("-",s427.get("y"),29),20,39,s427.get("col"))));
discard(call(s427.get("r"),args(binary("-",s427.get("x"),9),binary("-",s427.get("y"),17),18,14,"#395363")));
discard(call(s427.get("r"),args(binary("-",s427.get("x"),7),binary("-",s427.get("y"),16),14,5,"#8bb8bd")));
discard(call(s427.get("r"),args(binary("-",s427.get("x"),8),binary("-",s427.get("y"),4),16,3,"#7ca3a8")));
discard(call(s427.get("r"),args(binary("-",s427.get("x"),9),binary("+",s427.get("y"),6),4,2,"#ded8a4")));
discard(call(s427.get("r"),args(binary("+",s427.get("x"),5),binary("+",s427.get("y"),6),4,2,"#ded8a4")));
discard(call(s427.get("r"),args(binary("-",s427.get("x"),11),binary("-",s427.get("y"),23),2,7,"#1f3638")));
discard(call(s427.get("r"),args(binary("+",s427.get("x"),10),binary("-",s427.get("y"),23),2,7,"#1f3638")));
if(always()) throw returned(null);
}
}
discard(call(s378.get("r"),args(binary("+",s378.get("x"),8),binary("+",s378.get("y"),8),s378.get("w"),s378.get("h"),"#24473d45")));
discard(call(s378.get("r"),args(binary("-",s378.get("x"),2),binary("-",s378.get("y"),26),binary("+",s378.get("w"),4),binary("+",s378.get("h"),27),"#304d4b")));
if(truth(method(arr("wakanda","asgard","palace"),"includes",args(s378.get("kind"))))){
{ final Scope s428=new Scope(s378);
s428.let("gold",binary("!==",s428.get("kind"),"wakanda"));
s428.let("base",(truth(s428.get("gold"))?"#a99a69":"#386964"));
s428.let("light",(truth(s428.get("gold"))?"#e1ce97":"#7ab4a4"));
s428.let("trim",(truth(s428.get("gold"))?"#f2dfa8":"#b2a0d2"));
discard(call(s428.get("r"),args(s428.get("x"),binary("-",s428.get("y"),25),s428.get("w"),binary("+",s428.get("h"),23),s428.get("base"))));
discard(call(s428.get("r"),args(binary("+",s428.get("x"),5),binary("-",s428.get("y"),30),binary("-",s428.get("w"),10),binary("-",s428.get("h"),20),s428.get("light"))));
discard(call(s428.get("r"),args(binary("+",s428.get("x"),13),binary("-",s428.get("y"),38),binary("-",s428.get("w"),26),13,s428.get("base"))));
discard(call(s428.get("r"),args(binary("+",s428.get("x"),20),binary("-",s428.get("y"),44),binary("-",s428.get("w"),40),10,s428.get("light"))));
{final Scope s429=new Scope(s428);s429.let("i",12);
while(truth(binary("<",s429.get("i"),binary("-",s429.get("w"),9)))){try{{ final Scope s430=new Scope(s429);
discard(call(s430.get("r"),args(binary("+",s430.get("x"),s430.get("i")),binary("-",binary("+",s430.get("y"),s430.get("h")),53),11,34,(truth(s430.get("gold"))?"#6b807e":"#264e51"))));
discard(call(s430.get("r"),args(binary("+",binary("+",s430.get("x"),s430.get("i")),2),binary("-",binary("+",s430.get("y"),s430.get("h")),51),3,29,s430.get("trim"))));
discard(call(s430.get("r"),args(binary("+",s430.get("x"),s430.get("i")),binary("-",s430.get("y"),20),3,method(s430.get("Math"),"max",args(5,binary("-",s430.get("h"),50))),s430.get("trim"))));
}
}catch(Continue c){}catch(Break b){break;}discard(s429.set("i",binary("+",s429.get("i"),22)));}}
discard(call(s428.get("r"),args(binary("+",s428.get("x"),3),binary("-",binary("+",s428.get("y"),s428.get("h")),19),binary("-",s428.get("w"),6),5,s428.get("light"))));
discard(call(s428.get("r"),args(binary("-",binary("+",s428.get("x"),binary("/",s428.get("w"),2)),11),binary("-",binary("+",s428.get("y"),s428.get("h")),23),22,24,"#253f45")));
discard(call(s428.get("r"),args(binary("-",binary("+",s428.get("x"),binary("/",s428.get("w"),2)),8),binary("-",binary("+",s428.get("y"),s428.get("h")),20),16,20,(truth(s428.get("gold"))?"#7d9ba0":"#7667a0"))));
if(truth(binary("===",s428.get("kind"),"wakanda"))){
{ final Scope s431=new Scope(s428);
discard(call(s431.get("r"),args(binary("+",s431.get("x"),18),binary("-",s431.get("y"),32),binary("-",s431.get("w"),36),5,"#c0b469")));
discard(call(s431.get("r"),args(binary("-",binary("+",s431.get("x"),binary("/",s431.get("w"),2)),7),binary("-",s431.get("y"),47),14,6,"#cfb575")));
}
}
if(truth(binary("===",s428.get("kind"),"palace"))){
{ final Scope s432=new Scope(s428);
discard(call(s432.get("r"),args(binary("-",s432.get("x"),8),binary("-",s432.get("y"),59),20,binary("+",s432.get("h"),58),"#bcac7c")));
discard(call(s432.get("r"),args(binary("-",binary("+",s432.get("x"),s432.get("w")),12),binary("-",s432.get("y"),59),20,binary("+",s432.get("h"),58),"#bcac7c")));
discard(call(s432.get("r"),args(binary("-",s432.get("x"),4),binary("-",s432.get("y"),71),12,12,"#e5d295")));
discard(call(s432.get("r"),args(binary("-",binary("+",s432.get("x"),s432.get("w")),8),binary("-",s432.get("y"),71),12,12,"#e5d295")));
discard(call(s432.get("r"),args(binary("+",s432.get("x"),6),binary("-",s432.get("y"),25),binary("-",s432.get("w"),12),5,"#f0da9e")));
}
}
}
}
else{
{ final Scope s433=new Scope(s378);
s433.let("roof",(truth(binary("===",s433.get("kind"),"stark"))?"#526777":(truth(binary("===",s433.get("kind"),"sanctum"))?"#695667":(truth(binary("===",s433.get("kind"),"archive"))?"#546b66":(truth(binary(">",call(s433.get("noise"),args(s433.get("x"),s433.get("y"))),0.5))?"#aa6652":"#6c8090")))));
discard(call(s433.get("r"),args(s433.get("x"),binary("-",s433.get("y"),25),s433.get("w"),binary("+",s433.get("h"),24),"#bfc2a3")));
discard(call(s433.get("r"),args(binary("+",s433.get("x"),3),binary("-",s433.get("y"),28),binary("-",s433.get("w"),6),method(s433.get("Math"),"max",args(20,binary("-",s433.get("h"),51))),s433.get("roof"))));
discard(call(s433.get("r"),args(binary("+",s433.get("x"),6),binary("-",s433.get("y"),25),binary("-",s433.get("w"),12),4,"#d1c5a1")));
discard(call(s433.get("r"),args(binary("+",s433.get("x"),7),binary("+",s433.get("y"),5),binary("-",s433.get("w"),14),2,"#414f5855")));
{final Scope s434=new Scope(s433);s434.let("j",8);
while(truth(binary("<",s434.get("j"),binary("-",s434.get("h"),54)))){try{{ final Scope s435=new Scope(s434);
discard(call(s435.get("r"),args(binary("+",s435.get("x"),8),binary("+",s435.get("y"),s435.get("j")),binary("-",s435.get("w"),16),1,"#283e4940")));
{final Scope s436=new Scope(s435);s436.let("i",16);
while(truth(binary("<",s436.get("i"),binary("-",s436.get("w"),14)))){try{discard(call(s436.get("r"),args(binary("+",s436.get("x"),s436.get("i")),binary("+",binary("+",s436.get("y"),s436.get("j")),1),1,18,"#283e4930")));
}catch(Continue c){}catch(Break b){break;}discard(s436.set("i",binary("+",s436.get("i"),28)));}}
}
}catch(Continue c){}catch(Break b){break;}discard(s434.set("j",binary("+",s434.get("j"),20)));}}
{final Scope s437=new Scope(s433);s437.let("i",13);
while(truth(binary("<",s437.get("i"),binary("-",s437.get("w"),16)))){try{{ final Scope s438=new Scope(s437);
discard(call(s438.get("r"),args(binary("+",s438.get("x"),s438.get("i")),binary("-",binary("+",s438.get("y"),s438.get("h")),48),18,21,"#3d5964")));
discard(call(s438.get("r"),args(binary("+",binary("+",s438.get("x"),s438.get("i")),2),binary("-",binary("+",s438.get("y"),s438.get("h")),46),14,17,"#8eb2b9")));
discard(call(s438.get("r"),args(binary("+",binary("+",s438.get("x"),s438.get("i")),9),binary("-",binary("+",s438.get("y"),s438.get("h")),46),1,17,"#d4d0b5")));
discard(call(s438.get("r"),args(binary("+",binary("+",s438.get("x"),s438.get("i")),2),binary("-",binary("+",s438.get("y"),s438.get("h")),37),14,1,"#d4d0b5")));
}
}catch(Continue c){}catch(Break b){break;}discard(s437.set("i",binary("+",s437.get("i"),30)));}}
discard(call(s433.get("r"),args(binary("-",binary("+",s433.get("x"),binary("/",s433.get("w"),2)),10),binary("-",binary("+",s433.get("y"),s433.get("h")),23),20,23,"#425761")));
discard(call(s433.get("r"),args(binary("-",binary("+",s433.get("x"),binary("/",s433.get("w"),2)),7),binary("-",binary("+",s433.get("y"),s433.get("h")),20),14,20,"#92b2b3")));
discard(call(s433.get("r"),args(binary("-",binary("+",s433.get("x"),binary("/",s433.get("w"),2)),10),binary("-",binary("+",s433.get("y"),s433.get("h")),26),20,4,"#a0624e")));
discard(call(s433.get("r"),args(binary("+",s433.get("x"),13),binary("-",s433.get("y"),16),22,14,"#a5b4ae")));
discard(call(s433.get("r"),args(binary("+",s433.get("x"),16),binary("-",s433.get("y"),14),16,9,"#647778")));
discard(call(s433.get("r"),args(binary("-",binary("+",s433.get("x"),s433.get("w")),34),binary("-",s433.get("y"),19),19,16,"#7d9993")));
discard(call(s433.get("r"),args(binary("-",binary("+",s433.get("x"),s433.get("w")),32),binary("-",s433.get("y"),17),15,2,"#b8c1ae")));
if(truth(binary("===",s433.get("kind"),"stark"))){
{ final Scope s439=new Scope(s433);
discard(call(s439.get("r"),args(binary("-",binary("+",s439.get("x"),binary("/",s439.get("w"),2)),21),binary("-",s439.get("y"),35),42,12,"#334c58")));
discard(put(s439.get("ctx"),"fillStyle","#b0e0da"));
discard(put(s439.get("ctx"),"font","bold 8px monospace"));
discard(put(s439.get("ctx"),"textAlign","center"));
discard(method(s439.get("ctx"),"fillText",args("STARK",binary("+",s439.get("x"),binary("/",s439.get("w"),2)),binary("-",s439.get("y"),26))));
}
}
if(truth(binary("===",s433.get("kind"),"sanctum"))){
{ final Scope s440=new Scope(s433);
discard(call(s440.get("r"),args(binary("-",binary("+",s440.get("x"),binary("/",s440.get("w"),2)),13),binary("-",s440.get("y"),35),26,23,"#b4a482")));
discard(call(s440.get("r"),args(binary("-",binary("+",s440.get("x"),binary("/",s440.get("w"),2)),9),binary("-",s440.get("y"),32),18,17,"#536376")));
discard(call(s440.get("r"),args(binary("-",binary("+",s440.get("x"),binary("/",s440.get("w"),2)),1),binary("-",s440.get("y"),32),2,17,"#d6c28c")));
discard(call(s440.get("r"),args(binary("-",binary("+",s440.get("x"),binary("/",s440.get("w"),2)),9),binary("-",s440.get("y"),24),18,2,"#d6c28c")));
}
}
if(truth(binary("===",s433.get("kind"),"building"))){
{ final Scope s441=new Scope(s433);
{final Scope s442=new Scope(s441);s442.let("j",8);
while(truth(binary("<",s442.get("j"),binary("-",s442.get("h"),30)))){try{{ final Scope s443=new Scope(s442);
discard(call(s443.get("r"),args(binary("+",s443.get("x"),10),binary("+",s443.get("y"),s443.get("j")),25,2,"#513e49")));
discard(call(s443.get("r"),args(binary("+",s443.get("x"),11),binary("+",s443.get("y"),s443.get("j")),2,12,"#594551")));
discard(call(s443.get("r"),args(binary("+",s443.get("x"),31),binary("+",s443.get("y"),s443.get("j")),2,12,"#594551")));
discard(call(s443.get("r"),args(binary("+",s443.get("x"),16),binary("+",binary("+",s443.get("y"),s443.get("j")),4),15,1,"#8d7680")));
}
}catch(Continue c){}catch(Break b){break;}discard(s442.set("j",binary("+",s442.get("j"),13)));}}
discard(call(s441.get("r"),args(binary("+",s441.get("x"),3),binary("-",binary("+",s441.get("y"),s441.get("h")),28),binary("-",s441.get("w"),6),5,"#cb6656")));
}
}
if(truth(binary("===",s433.get("kind"),"ruin"))){
{ final Scope s444=new Scope(s433);
discard(call(s444.get("r"),args(s444.get("x"),binary("-",s444.get("y"),25),s444.get("w"),binary("+",s444.get("h"),25),"#707f73")));
{final Scope s445=new Scope(s444);s445.let("i",0);
while(truth(binary("<",s445.get("i"),s445.get("w")))){try{{ final Scope s446=new Scope(s445);
discard(call(s446.get("r"),args(binary("+",s446.get("x"),s446.get("i")),binary("-",s446.get("y"),29),17,8,"#a6ae94")));
discard(call(s446.get("r"),args(binary("+",binary("+",s446.get("x"),s446.get("i")),3),binary("-",s446.get("y"),5),7,method(s446.get("Math"),"max",args(5,binary("-",s446.get("h"),5))),"#899881")));
}
}catch(Continue c){}catch(Break b){break;}discard(s445.set("i",binary("+",s445.get("i"),25)));}}
if(always()) throw returned(null);
}
}
}
}
if(truth(and(()->get(s378.get("p"),"label"),()->binary("!==",s378.get("kind"),"stark")))){
{ final Scope s447=new Scope(s378);
s447.let("ly",binary("-",binary("+",s447.get("y"),s447.get("h")),14));
discard(put(s447.get("ctx"),"font","6px monospace"));
discard(put(s447.get("ctx"),"textAlign","center"));
s447.let("tw",get(method(s447.get("ctx"),"measureText",args(get(s447.get("p"),"label"))),"width"));
discard(call(s447.get("r"),args(binary("-",binary("-",binary("+",s447.get("x"),binary("/",s447.get("w"),2)),binary("/",s447.get("tw"),2)),5),binary("-",s447.get("ly"),7),binary("+",s447.get("tw"),10),11,"#314b46")));
discard(put(s447.get("ctx"),"fillStyle","#ece0b4"));
discard(method(s447.get("ctx"),"fillText",args(get(s447.get("p"),"label"),binary("+",s447.get("x"),binary("/",s447.get("w"),2)),binary("+",s447.get("ly"),1))));
}
}
}));}
private void install_draw_object(){global.let("draw_object",fn(global,(s448,a449)->{
s448.let("ctx",argument(a449,0));
s448.let("o",argument(a449,1));
s448.let("time",argument(a449,2));
s448.let("session",argument(a449,3));
s448.let("x",method(s448.get("Math"),"round",args(get(s448.get("o"),"px"))));
s448.let("y",method(s448.get("Math"),"round",args(get(s448.get("o"),"py"))));
discard(put(s448.get("ctx"),"fillStyle","#294e4240"));
discard(method(s448.get("ctx"),"fillRect",args(binary("-",s448.get("x"),9),binary("-",s448.get("y"),2),18,5)));
if(truth(get(s448.get("o"),"travel"))){
{ final Scope s450=new Scope(s448);
discard(put(s450.get("ctx"),"fillStyle","#3b596a"));
discard(method(s450.get("ctx"),"fillRect",args(binary("-",s450.get("x"),15),binary("-",s450.get("y"),35),5,35)));
discard(method(s450.get("ctx"),"fillRect",args(binary("+",s450.get("x"),10),binary("-",s450.get("y"),35),5,35)));
discard(method(s450.get("ctx"),"fillRect",args(binary("-",s450.get("x"),15),binary("-",s450.get("y"),39),30,5)));
discard(put(s450.get("ctx"),"fillStyle","#977cea"));
discard(method(s450.get("ctx"),"fillRect",args(binary("-",s450.get("x"),10),binary("-",s450.get("y"),34),20,32)));
discard(put(s450.get("ctx"),"fillStyle","#a5e4ee"));
discard(method(s450.get("ctx"),"fillRect",args(binary("-",s450.get("x"),7),binary("-",s450.get("y"),30),14,24)));
discard(put(s450.get("ctx"),"fillStyle","#e1eef5"));
discard(method(s450.get("ctx"),"fillRect",args(binary("-",s450.get("x"),5),binary("+",binary("-",s450.get("y"),28),method(s450.get("Math"),"floor",args(binary("*",method(s450.get("Math"),"sin",args(binary("*",s450.get("time"),2))),3)))),2,17)));
if(always()) throw returned(null);
}
}
if(truth(or(()->binary("===",get(s448.get("o"),"kind"),"sign"),()->binary("===",get(s448.get("o"),"kind"),"door")))){
{ final Scope s451=new Scope(s448);
discard(put(s451.get("ctx"),"fillStyle","#71624a"));
discard(method(s451.get("ctx"),"fillRect",args(binary("-",s451.get("x"),2),binary("-",s451.get("y"),14),4,16)));
discard(put(s451.get("ctx"),"fillStyle","#bba877"));
discard(method(s451.get("ctx"),"fillRect",args(binary("-",s451.get("x"),11),binary("-",s451.get("y"),23),22,13)));
discard(put(s451.get("ctx"),"fillStyle","#6e6851"));
discard(method(s451.get("ctx"),"fillRect",args(binary("-",s451.get("x"),8),binary("-",s451.get("y"),20),15,1)));
discard(method(s451.get("ctx"),"fillRect",args(binary("-",s451.get("x"),8),binary("-",s451.get("y"),16),12,1)));
}
}
else{
{ final Scope s452=new Scope(s448);
discard(put(s452.get("ctx"),"fillStyle",(truth(binary("===",get(s452.get("o"),"id"),"ore"))?"#a18fcc":(truth(binary("===",get(s452.get("o"),"id"),"anchor"))?"#8abcab":"#aec7bb"))));
discard(method(s452.get("ctx"),"fillRect",args(binary("-",s452.get("x"),5),binary("-",s452.get("y"),17),10,15)));
discard(put(s452.get("ctx"),"fillStyle","#e5e2b1"));
discard(method(s452.get("ctx"),"fillRect",args(binary("-",s452.get("x"),2),binary("-",s452.get("y"),20),4,6)));
discard(method(s452.get("ctx"),"fillRect",args(binary("-",s452.get("x"),6),binary("-",s452.get("y"),2),13,3)));
if(truth(binary("===",binary("%",method(s452.get("Math"),"floor",args(binary("*",s452.get("time"),2))),3),0))){
{ final Scope s453=new Scope(s452);
discard(put(s453.get("ctx"),"fillStyle","#efe6ae"));
discard(method(s453.get("ctx"),"fillRect",args(binary("+",s453.get("x"),9),binary("-",s453.get("y"),20),1,4)));
discard(method(s453.get("ctx"),"fillRect",args(binary("+",s453.get("x"),8),binary("-",s453.get("y"),19),3,1)));
}
}
}
}
}));}}
