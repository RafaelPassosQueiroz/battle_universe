package com.example.projetomarvel;
/** Approved roster labels and abilities. Rules remain in BattleEngine. */
final class BattleRoster {
 static final class Entry {
  final String id,name,civilian,type,normal,special,passive;final double agility;
  Entry(String id,String name,String civilian,String type,String normal,String special,String passive,double agility){this.id=id;this.name=name;this.civilian=civilian;this.type=type;this.normal=normal;this.special=special;this.passive=passive;this.agility=agility;}
 }
 static final Entry[] ALL={
new Entry("spider","Homem-Aranha","Peter Parker","ACROBACIA","Teia","Estilingue","Sentido Aranha: projéteis próximos ficam mais lentos. A teia reduz a velocidade do alvo por 1,5 s.",1.18),
new Entry("iron","Homem de Ferro","Tony Stark","TECNOLOGIA","Repulsor","Unibeam","Propulsão: segure o pulo para planar brevemente. O Unibeam causa 35 de dano, seguido de 3 s de exaustão.",.94),
new Entry("wolverine","Wolverine","Logan","BERSERKER","Corte cruzado","Berserker","Regeneração: recupera 5 HP a cada 2 s sem atacar, defender ou receber dano. Berserker: duas investidas de 10.",1.08),
new Entry("captain","Capitão América","Steve Rogers","SUPER SOLDADO","Escudo","Defesa absoluta","O escudo atinge na ida e na volta. Defesa absoluta concede invulnerabilidade por 5 s; o escudo bloqueia poderes ao defender.",1),
new Entry("thor","Thor","Thor Odinson","DEUS DO TROVÃO","Mjölnir","Trovão","Mjölnir causa 8 de dano e um raio de 2. Pode executar um dash aéreo por salto ao pressionar uma direção.",.92),
new Entry("hulk","Hulk","Bruce Banner","FORÇA BRUTA","Thunderclap","Salto esmagador","Thunderclap anula projéteis comuns. Com 30 HP ou menos, a Fúria acrescenta 5 de dano aos golpes.",.8),
new Entry("panther","Pantera Negra","T’Challa","VIBRANIUM","Investida felina","Explosão cinética","Movimento 22% mais rápido. Cada golpe defendido carrega vibranium; o especial libera 6 de dano por carga (máximo 10).",1.22),
new Entry("strange","Doutor Estranho","Stephen Strange","ARTES MÍSTICAS","Rajada mística","Portal","Portal causa 10 de dano próximo e troca seu lado da arena. Viagem no Tempo restaura uma vez o estado registrado ao chegar a 20% de vida.",.95),
new Entry("widow","Viúva Negra","Natasha Romanoff","ESPIONAGEM","Dois tiros","Abate silencioso","Dois tiros de 3 de dano. Abate silencioso eletrifica o próximo golpe físico e atordoa o alvo por 2 s. Recuperação ágil.",1.15),
new Entry("scarlet","Feiticeira Escarlate","Wanda Maximoff","MAGIA DO CAOS","Esfera do caos","Distorção","Distorção impede pulo e defesa do alvo por 5 s. Ao atingir 30 HP, invoca uma projeção do caos com 20 HP.",1.02),
new Entry("marvel","Capitã Marvel","Carol Danvers","ENERGIA CÓSMICA","Feixe de luz","Supernova","Combate aprimorado: golpes físicos mais rápidos. Feixe de luz causa 7 de dano; Supernova avança com um impacto de 15.",1.18),
new Entry("doom","Doutor Destino","Victor von Doom","CIÊNCIA & FEITIÇARIA","Raios verdes","Sentença","Cinco raios verdes de 3 de dano. Sentença marca um laser de 20 de dano. Um escudo automático absorve um golpe a cada 7 s.",.9),
new Entry("thanos","Thanos","O Titã Louco","TITÃ","Energia cósmica","Estalo","130 HP e resistência a empurrões. O Estalo encerra a luta após 1,5 s de preparação; acertá-lo interrompe a habilidade.",.82),
};
 static Entry at(int index){return ALL[Math.max(0,Math.min(ALL.length-1,index))];}
 static String id(String name){if(name==null)return "custom";if(name.toLowerCase(java.util.Locale.ROOT).contains("doombot"))return "doombot";if(name.contains("Projeção"))return "scarlet";for(Entry e:ALL){if(name.equals(e.name))return e.id;}switch(BattleEngine.heroSkill(name)){case SPIDER:return "spider";case IRON:return "iron";case WOLVERINE:return "wolverine";case CAPTAIN:return "captain";case THOR:return "thor";case HULK:return "hulk";case PANTHER:return "panther";case STRANGE:return "strange";case WIDOW:return "widow";case SCARLET:return "scarlet";case MARVEL:return "marvel";case DOOM:return "doom";case THANOS:return "thanos";default:return "custom";}}
 static Entry find(String id){for(Entry e:ALL)if(e.id.equals(id))return e;return ALL[1];}
}
