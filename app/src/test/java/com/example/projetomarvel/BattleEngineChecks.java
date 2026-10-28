package com.example.projetomarvel;

import com.example.projetomarvel.BattleEngine.Fighter;
import com.example.projetomarvel.BattleEngine.Projectile;
import com.example.projetomarvel.BattleEngine.Skill;

/** Behavioral checks for damage, dodge windows, status lifetimes, side changes and ally interception. */
public final class BattleEngineChecks {
    static int checks;
    static final String[] HEROES={"Homem-Aranha","Homem de Ferro","Wolverine","Capitão América","Thor",
            "Hulk","Pantera Negra","Doutor Estranho","Viúva Negra","Feiticeira Escarlate","Capitã Marvel","Dr. Doom","Thanos"};
    static BattleEngine engine(String name){return new BattleEngine(name,"Homem de Ferro","Energia",false,1,-1,false,42);}
    static BattleEngine custom(String power){return new BattleEngine("Teste","Homem de Ferro",power,true,1,-1,false,42);}
    static void ok(boolean value,String message){checks++;if(!value)throw new AssertionError(message);}
    static void eq(float value,float expected,String message){ok(Math.abs(value-expected)<.02f,message+": "+value+" != "+expected);}
    static void advance(BattleEngine e,float seconds){for(int i=0;i<Math.ceil(seconds/.01f);i++)e.step(.01f,0,false,false);}
    static void close(BattleEngine e,boolean right){e.player.x=right?450:300;e.bot.x=right?350:400;e.face();}
    static void ready(BattleEngine e){e.player.specialCooldown=0;e.player.cooldown=0;e.player.stun=0;}
    static void basics(){
        for(String hero:HEROES)for(boolean right:new boolean[]{false,true})for(int slot=0;slot<2;slot++){
            BattleEngine e=engine(hero);close(e,right);
            eq(e.player.maxHp,hero.equals("Thanos")?130:120,hero+" life");
            int expected=(slot==0?8:10)+(hero.equals("Thor")||hero.contains("Doom")||hero.equals("Thanos")?1:0);
            ok(e.attack(e.player,slot),hero+" starts melee");advance(e,.55f);
            eq(e.bot.hp,120-expected,hero+" melee on "+(right?"right":"left"));
            eq(e.player.dir,right?-1:1,hero+" facing");
            ok(e.bot.x>=BattleEngine.EDGE&&e.bot.x<=BattleEngine.WIDTH-BattleEngine.EDGE,"knockback in arena");
        }
        BattleEngine e=engine("Homem de Ferro");close(e,false);e.guard(e.bot,true);e.bot.guardTime=2;
        e.attack(e.player,0);advance(e,.5f);eq(e.bot.hp,118,"existing quarter melee defense");
        e=engine("Homem de Ferro");e.player.x=400;e.bot.x=500;e.jump(e.player);
        for(int i=0;i<55;i++)e.step(.01f,1,false,false);
        ok(e.player.x>e.bot.x,"jump crosses opponent");eq(e.player.dir,-1,"automatic facing after crossing");
        e.player.y=0;e.player.vy=0;e.player.stun=0;e.player.cooldown=0;
        e.player.x=500;e.bot.x=400;e.face();e.attack(e.player,0);advance(e,.5f);
        eq(e.bot.hp,112,"melee follows new facing");
        e=engine("Dr. Doom");e.player.x=750;e.bot.x=700;e.face();
        for(int i=0;i<100;i++)e.step(.01f,1,false,false);
        eq(e.player.x,789,"movement reaches useful arena edge behind controls");
    }
    static void common(){
        for(String hero:HEROES)for(boolean right:new boolean[]{false,true}){
            BattleEngine e=engine(hero);close(e,right);e.attack(e.player,2);advance(e,.85f);
            float expected=hero.contains("Aranha")?5:hero.equals("Wolverine")?4:hero.equals("Hulk")?5:
                    hero.equals("Viúva Negra")||hero.equals("Capitã Marvel")?10:
                    hero.equals("Thor")||hero.contains("Doom")||hero.equals("Thanos")?9:8;
            eq(e.bot.hp,120-expected,hero+" common both directions");
            if(hero.contains("Aranha"))ok(e.bot.slow>0,"web slow persists");
        }
        BattleEngine e=engine("Dr. Doom");e.player.x=500;e.bot.x=300;e.player.y=100;e.face();
        e.attack(e.player,2);advance(e,.7f);eq(e.bot.hp,111,"Doom aerial diagonal reaches grounded left opponent");
        e=engine("Thor");e.player.x=100;e.bot.x=700;e.face();e.attack(e.player,2);advance(e,2);
        eq(e.bot.hp,120,"Mjolnir has half-arena range");
        e=engine("Hulk");e.projectiles.add(new Projectile(e.bot,700,78,-500,0,8,"common"));
        e.attack(e.player,2);advance(e,.3f);ok(e.projectiles.isEmpty(),"Thunderclap destroys distant projectile");
        e=engine("Homem-Aranha");e.player.x=400;e.bot.x=700;e.face();
        Projectile shot=new Projectile(e.bot,500,78,-100,0,8,"common");e.projectiles.add(shot);e.step(.05f,0,false,false);
        eq(shot.x,496,"spider sense reduces speed 20 percent nearby");ok(e.player.alert>0,"spider visual alert state");
        shot.x=650;e.step(.05f,0,false,false);eq(shot.x,645,"distant projectile retains speed");
    }
    static void specialRules(){
        for(String hero:HEROES)for(boolean right:new boolean[]{false,true}){
            BattleEngine e=engine(hero);close(e,right);
            ok(!e.special(e.player),hero+" initially unavailable");
            advance(e,15.05f);ok(e.canSpecial(e.player),hero+" available after initial 15 seconds");
            ok(e.special(e.player),hero+" starts special");
            e.hurt(e.player,e.bot,8,false,false,-1,e.bot.dir);
            ok(e.player.specialActive,hero+" special survives damage");
            advance(e,6);ok(!e.player.specialActive,hero+" completes execution");
            ok(e.player.specialCooldown>0,hero+" cooldown remains");
            ok(!e.over,hero+" special is bounded damage");
        }
        BattleEngine e=engine("Homem-Aranha");close(e,false);ready(e);e.special(e.player);
        e.hurt(e.player,e.bot,50,true,true,-1,e.bot.dir);eq(e.player.hp,120,"sling immunity");
        advance(e,1.5f);ok(e.player.x>e.bot.x,"sling changes side");eq(e.bot.hp,110,"automatic sling kick");
        eq(e.player.y,0,"sling lands");
        e=engine("Wolverine");close(e,false);ready(e);e.special(e.player);advance(e,1);
        eq(e.bot.hp,100,"two berserker dashes, 10 each");
        e.player.hp=60;advance(e,5.2f);eq(e.player.hp,65,"healing after five quiet seconds");
        advance(e,5.1f);eq(e.player.hp,70,"repeat healing");
        e=engine("Capitão América");close(e,false);ready(e);e.special(e.player);
        e.hurt(e.player,e.bot,25,true,true,-1,-1);eq(e.player.hp,120,"absolute defense immunity without guard");
        advance(e,4.95f);ok(e.player.specialActive,"captain effect lasts five seconds");
        advance(e,.1f);ok(!e.player.specialActive,"captain effect ends");
        ok(e.player.specialCooldown>14.8f,"15 recharge starts after effect");advance(e,14.8f);
        ok(!e.canSpecial(e.player),"not ready before full 20 seconds");advance(e,.3f);
        ok(e.canSpecial(e.player),"ready after effect plus recharge");
        e=engine("Hulk");close(e,false);ready(e);e.special(e.player);advance(e,1.8f);
        eq(e.bot.hp,100,"meteor smash direct damage");
        e=engine("Hulk");close(e,false);e.player.hp=19;ready(e);e.special(e.player);advance(e,1.8f);
        eq(e.bot.hp,95,"Hulk fury boosts explicit special only when below 20");
        eq(e.basicDamage(e.player,0),13,"Hulk fury punch modifier");
        e=engine("Hulk");close(e,false);ready(e);e.special(e.player);advance(e,1.35f);
        e.jump(e.bot);advance(e,.35f);eq(e.bot.hp,120,"timed jump avoids smash");
        e=engine("Pantera Negra");close(e,false);e.guard(e.player,true);
        for(int i=0;i<5;i++){e.player.stun=0;e.hurt(e.player,e.bot,8,true,false,-1,-1);}
        eq(e.player.defendedAttacks,5,"counts defended attacks");close(e,false);ready(e);e.special(e.player);advance(e,.5f);
        eq(e.bot.hp,110,"vibranium damage equals twice defense count");eq(e.player.defendedAttacks,0,"vibranium consumes stored energy");
        e=engine("Viúva Negra");close(e,false);ready(e);e.special(e.player);advance(e,.5f);
        e.attack(e.player,0);advance(e,.14f);ok(e.bot.stun>1.9f,"next physical attack stuns two seconds");
        ok(!e.player.armedMelee,"stun is consumed once");
        e=engine("Feiticeira Escarlate");ready(e);e.special(e.player);
        ok(!e.jump(e.bot),"distortion prevents jumping");e.guard(e.bot,true);ok(!e.guarding(e.bot),"distortion prevents guarding");
        advance(e,5.05f);eq(e.bot.distortion,0,"distortion expires");ok(e.player.specialCooldown>14.8f,"Scarlet starts 15s after effect");
        e=engine("Thanos");float x=e.player.x;e.hurt(e.player,e.bot,8,false,false,-1,-1);eq(e.player.x,x,"Thanos does not recoil");
        ready(e);e.special(e.player);advance(e,1.25f);ok(e.projectiles.size()>=3,"five meteors enter arena in sequence");
        e=engine("Dr. Doom");advance(e,10.05f);ok(e.player.forceShield,"Doom shield every ten seconds");
        e.hurt(e.player,e.bot,30,true,true,-1,-1);eq(e.player.hp,120,"Doom shield absorbs first hit");
        ok(!e.player.forceShield,"shield disappears");e.hurt(e.player,e.bot,8,false,false,-1,-1);eq(e.player.hp,112,"following hit damages");
    }
    static void beamsAndStrikes(){
        BattleEngine e=engine("Homem de Ferro");close(e,false);ready(e);e.special(e.player);
        advance(e,.49f);eq(e.bot.hp,120,"Unibeam warning lasts half second");advance(e,.1f);
        eq(e.bot.hp,95,"Unibeam damage 25");ok(e.player.exhaustion>2.8f,"Unibeam exhaustion even after hit");
        e=engine("Homem de Ferro");close(e,false);e.guard(e.bot,true);e.bot.guardTime=3;ready(e);e.special(e.player);advance(e,.6f);
        eq(e.bot.hp,115,"guarded Unibeam damage five");
        e=engine("Homem de Ferro");close(e,false);ready(e);e.special(e.player);advance(e,.28f);e.jump(e.bot);advance(e,.4f);
        eq(e.bot.hp,120,"jump avoids grounded Unibeam");ok(e.player.exhaustion>0,"miss still exhausts Iron Man");
        e=engine("Homem de Ferro");close(e,false);e.player.y=110;ready(e);e.special(e.player);advance(e,.7f);
        eq(e.bot.hp,120,"grounded enemy avoids aerial Unibeam");advance(e,.5f);eq(e.player.y,0,"Iron lands exhausted");
        e=engine("Homem de Ferro");e.jump(e.player);
        for(int i=0;i<100;i++)e.step(.01f,0,true,false);ok(e.player.y>0,"held jump extends air time");
        advance(e,1.8f);eq(e.player.y,0,"hover is bounded and releases");
        for(String hero:new String[]{"Thor","Dr. Doom"})for(boolean right:new boolean[]{false,true}){
            e=engine(hero);close(e,right);ready(e);e.special(e.player);advance(e,.3f);
            e.bot.x+=right?-130:130;advance(e,.8f);eq(e.bot.hp,120,hero+" marked strike can be dodged");
            e=engine(hero);close(e,right);ready(e);e.special(e.player);advance(e,1.2f);
            eq(e.bot.hp,hero.equals("Thor")?107:100,hero+" strike specified damage");
        }
    }
    static void rewind(){
        BattleEngine e=engine("Doutor Estranho");close(e,false);e.player.hp=25;e.bot.hp=91;
        e.hurt(e.player,e.bot,2,false,false,-1,-1);ok(e.player.rewind!=null,"20 percent snapshot recorded");
        e.bot.hp=50;e.hurt(e.player,e.bot,80,true,true,-1,-1);
        eq(e.player.hp,24,"restores Strange at twenty percent");eq(e.bot.hp,91,"enemy exact snapshot HP");
        ok(e.player.rewindUsed&&!e.over,"first defeat rewinds");
        e.hurt(e.player,e.bot,80,true,true,-1,-1);ok(e.over&&!e.victory,"second defeat ends fight");
        e=engine("Doutor Estranho");e.player.y=30;ready(e);ok(!e.special(e.player),"portal is ground only");
        e.player.y=0;close(e,false);e.bot.x=350;e.face();e.special(e.player);advance(e,.25f);
        eq(e.bot.hp,110,"portal touches nearby enemy");ok(e.player.x>700,"only Strange teleports opposite");
        ok(e.bot.x<400,"portal leaves enemy in its location");
    }
    static void scarletAllies(){
        for(boolean right:new boolean[]{false,true}){
            BattleEngine e=engine("Feiticeira Escarlate");close(e,right);e.player.hp=60;e.step(.01f,0,false,false);
            Fighter first=e.player.ally;ok(first!=null,"first ally spawned at yellow");eq(first.hp,20,"ally HP exactly twenty");
            ok((first.x-e.player.x)*e.player.dir>0,"ally stays ahead, same side");
            float main=e.player.hp;
            e.hurt(first,e.bot,8,false,false,-1,e.bot.dir);ok(!e.over,"hitting ally does not finish battle");
            e.player.hp=30;e.step(.01f,0,false,false);eq(first.hp,20,"red restores current ally");
            ok(e.player.ally==first&&e.player.secondAllyQueued,"second ally waits for first death");
            Projectile shot=new Projectile(e.bot,first.x,78,0,0,5,"common");e.projectiles.add(shot);
            e.step(.01f,0,false,false);eq(e.player.hp,30,"projectile hits ally without hitting Scarlet");eq(first.hp,15,"ally intercepts damage");
            e.hurt(first,e.bot,200,false,false,-1,e.bot.dir);e.step(.01f,0,false,false);
            ok(e.player.ally!=first&&e.player.allyWave==2,"second spawned only after first death");eq(e.player.ally.hp,20,"second ally twenty HP");
            e.hurt(e.player.ally,e.bot,200,false,false,-1,e.bot.dir);advance(e,1);
            ok(e.player.ally.hp==0,"no third ally");ok(e.player.hp<=main,"Scarlet life bounded");
        }
    }
    static void customs(){
        for(String power:new String[]{"Energia","Gelo","Velocidade","Super Força","Ninja","Telecinese"}){
            BattleEngine e=custom(power);close(e,false);eq(e.player.hp,120,"custom life "+power);
            ready(e);ok(e.special(e.player),"custom special "+power);advance(e,1);
            ok(!e.player.specialActive&&!e.over,"custom ability completes and is bounded "+power);
        }
        BattleEngine e=custom("Ninja");eq(e.basicDamage(e.player,0),7,"Ninja punch reduced one");
        eq(e.basicDamage(e.player,1),10,"Ninja kick preserved");e.player.x=250;e.bot.x=395;e.face();
        e.attack(e.player,0);advance(e,.4f);eq(e.bot.hp,113,"Ninja sword has increased punch range");
        e=custom("Super Força");eq(e.basicDamage(e.player,0),11,"strength punch plus three");
        eq(e.basicDamage(e.player,1),13,"strength kick plus three");eq(e.commonDamage(e.player),11,"strength common plus three");
        close(e,false);ready(e);e.special(e.player);advance(e,.9f);eq(e.bot.hp,102,"strength special 15 plus three");
        e=custom("Gelo");close(e,false);ready(e);e.special(e.player);advance(e,.5f);
        ok(e.bot.frozen>2.8f,"ice freezes three seconds");eq(e.bot.hp,120,"freeze special no direct damage");
        ok(!e.attack(e.bot,0)&&!e.jump(e.bot),"frozen fighter cannot act");
        e.hurt(e.bot,e.player,9,false,false,-1,1);eq(e.bot.hp,115.5f,"frozen takes exactly half damage");
        advance(e,3.1f);eq(e.bot.frozen,0,"freeze expires");
        e=custom("Gelo");e.ice.add(new BattleEngine.IcePatch(e.player,e.bot.x));
        e.bot.botMove=1;e.step(.05f,0,false,false);e.bot.botMove=0;float before=e.bot.x;e.step(.05f,0,false,false);
        ok(e.bot.x>before,"ice creates sliding inertia");advance(e,5.1f);
        ok(e.ice.stream().noneMatch(p->Math.abs(p.x-624)<1),"ice patch expires five seconds after departure");
        e=custom("Telecinese");close(e,false);e.attack(e.player,2);advance(e,.6f);
        eq(e.bot.hp,107,"telekinetic base plus five");ok(e.bot.x>780,"telekinetic push to opposite edge");
        e=custom("Telecinese");advance(e,15.05f);ok(e.projectiles.size()==2,"one object from each edge every fifteen seconds");
    }
    static void simulations(){
        for(String hero:HEROES)for(int difficulty=0;difficulty<3;difficulty++){
            BattleEngine e=new BattleEngine("Homem-Aranha",hero,"Energia",false,difficulty,-1,false,87+difficulty);
            for(int i=0;i<9000&&!e.over;i++){
                if(i%20==0)e.attack(e.player,Math.abs(e.player.x-e.bot.x)>145?2:i%40==0?0:1);
                if(i%91==0)e.jump(e.player);
                if(e.canSpecial(e.player))e.special(e.player);
                e.step(.016f,i%220<120?1:-1,i%130<50,true);
                ok(Float.isFinite(e.player.x)&&Float.isFinite(e.bot.x),"simulation finite "+hero);
                ok(e.player.hp>=0&&e.player.hp<=120&&e.bot.hp>=0&&e.bot.hp<=e.bot.maxHp,"simulation HP bounds");
                ok(e.player.x>=55&&e.player.x<=789&&e.bot.x>=55&&e.bot.x<=789,"simulation arena bounds");
                if(e.bot.ally!=null&&e.bot.ally.hp>0){
                    ok(e.bot.ally.hp<=20,"simulation ally HP bounds");
                    ok((e.bot.ally.x-e.bot.x)*e.bot.dir>=0,"simulation ally stays same side");
                    ok((e.bot.ally.x-e.player.x)*e.bot.dir<=0,"ally does not cross opponent ahead of Scarlet");
                }
            }
        }
    }
    static void interactions(){
        // Run the same basic attacks with the hero as BOT, including Doom on the right.
        for(String hero:HEROES)for(boolean right:new boolean[]{true,false})for(int slot=0;slot<3;slot++){
            BattleEngine e=new BattleEngine("Homem de Ferro",hero,"Energia",false,1,-1,false,17);
            e.bot.x=right?400:300;e.player.x=right?300:400;e.face();
            ok(e.attack(e.bot,slot),hero+" BOT attack starts");advance(e,.9f);
            float expected=slot==0?8:slot==1?10:8;
            if(hero.equals("Thor")||hero.contains("Doom")||hero.equals("Thanos"))expected++;
            if(slot==2){if(hero.contains("Aranha"))expected=5;if(hero.equals("Wolverine"))expected=4;
                if(hero.equals("Hulk"))expected=5;if(hero.equals("Viúva Negra")||hero.equals("Capitã Marvel"))expected=10;}
            eq(e.player.hp,120-expected,hero+" BOT animation direction matches hit direction");
            eq(e.bot.dir,right?-1:1,hero+" BOT facing");
        }
        for(String hero:HEROES){
            BattleEngine e=new BattleEngine("Homem de Ferro",hero,"Energia",false,1,2,false,19);
            e.bot.specialCooldown=0;e.bot.x=450;e.player.x=350;e.face();
            ok(e.special(e.bot),hero+" story BOT special starts");
            e.hurt(e.bot,e.player,8,false,false,-1,1);ok(e.bot.specialActive,hero+" BOT special not interrupted");
            advance(e,2);ok(e.bot.specialCooldown>0,hero+" BOT recharge");
        }
        BattleEngine e=engine("Viúva Negra");close(e,false);e.guard(e.bot,true);e.bot.guardTime=3;
        e.attack(e.player,2);advance(e,.9f);eq(e.bot.hp,118,"held guard reduces both Widow bullets");
        e=new BattleEngine("Capitão América","Homem de Ferro","Energia",false,1,-1,false,19);
        close(e,false);e.guard(e.player,true);e.attack(e.bot,2);advance(e,.7f);
        eq(e.player.hp,120,"Vibranium shield absorbs projectile chip while guarding");
        e.guard(e.player,false);e.bot.cooldown=0;e.attack(e.bot,2);advance(e,.8f);
        eq(e.player.hp,112,"Captain unguarded receives projectile damage");
        e=engine("Homem-Aranha");close(e,false);e.guard(e.bot,true);e.bot.guardTime=3;
        ready(e);e.special(e.player);advance(e,1.6f);eq(e.bot.hp,117,"sling kick is defendable");
        e=engine("Hulk");close(e,false);e.guard(e.bot,true);e.bot.guardTime=3;
        ready(e);e.special(e.player);advance(e,1.8f);eq(e.bot.hp,115,"smash traditional defense works");
        e=engine("Pantera Negra");close(e,false);e.player.defendedAttacks=8;e.bot.y=110;
        ready(e);e.special(e.player);advance(e,.25f);eq(e.bot.hp,104,"jump cannot avoid vibranium burst");
        e=engine("Capitã Marvel");close(e,false);ready(e);e.special(e.player);advance(e,1);
        eq(e.bot.hp,105,"Supernova fifteen damage");
        e=engine("Capitã Marvel");e.attack(e.player,2);eq(e.player.attackDuration,.5f,"Marvel common remains normal speed");
        e=engine("Capitã Marvel");e.attack(e.player,0);ok(e.player.attackDuration<.28f,"Marvel physical speed improved");
        e=engine("Doutor Estranho");close(e,false);e.player.hp=25;e.bot.hp=83;
        e.projectiles.add(new Projectile(e.bot,e.player.x,78,0,0,30,"common"));e.step(.01f,0,false,false);
        eq(e.player.hp,24,"fatal projectile crossing twenty percent rewinds");
        eq(e.bot.hp,83,"fatal projectile preserves enemy snapshot");
        advance(e,.5f);eq(e.player.hp,24,"consumed projectile does not strike again after rewind");
        e=custom("Gelo");close(e,false);e.bot.guardTime=3;e.guard(e.bot,true);ready(e);e.special(e.player);advance(e,.7f);
        eq(e.bot.frozen,0,"defense can stop freeze shot");
        e=engine("Feiticeira Escarlate");close(e,false);e.player.hp=55;e.step(.01f,0,false,false);
        e.hurt(e.player.ally,e.bot,100,false,false,-1,-1);advance(e,.1f);
        ok(e.player.ally.hp==0,"no replacement before red threshold");e.player.hp=29;e.step(.01f,0,false,false);
        eq(e.player.ally.hp,20,"second wave spawns when first already dead at red");
        for(String hero:HEROES){
            e=engine(hero);e.player.y=80;e.player.vy=0;ready(e);
            ok(e.special(e.player)==!hero.equals("Doutor Estranho"),hero+" special air rule");
        }
        for(boolean right:new boolean[]{false,true}){
            e=engine("Homem-Aranha");e.bot.x=right?55:789;e.player.x=right?155:689;e.face();
            ready(e);e.special(e.player);advance(e,1.5f);
            ok(right?e.player.x<e.bot.x:e.player.x>e.bot.x,"sling still changes sides at arena wall");
            eq(e.bot.hp,110,"wall sling kick is synchronized with new direction");
        }
    }
    public static void main(String[] args){
        basics();common();specialRules();beamsAndStrikes();rewind();scarletAllies();customs();interactions();simulations();
        System.out.println("PASS: "+checks+" behavioral assertions, 13 heroes on both sides, six custom powers and AI simulations.");
    }
}
