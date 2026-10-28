//Regras e calculadora
package com.example.projetomarvel;

import java.util.ArrayList;
import java.util.Random;

/** Simulation for the existing fixed arena. No Android dependencies: combat can be checked deterministically. */
final class BattleEngine {
    static final float WIDTH=844, EDGE=55;
    static final float[] DURATIONS={.28f,.40f,.50f};
    static final float[] COOLDOWNS={.38f,.70f,1.25f};
    enum Skill { SPIDER, IRON, WOLVERINE, CAPTAIN, THOR, HULK, PANTHER, STRANGE,
        WIDOW, SCARLET, MARVEL, DOOM, THANOS, ENERGY, ICE, SPEED, STRENGTH, NINJA, TELEKINESIS, ALLY, GENERIC }
    enum Form { BEAM, STRIKE, BURST, WAVE, SLASH, PORTAL, SMOKE, TIME, VOLLEY, THOR_SPARK, GREEN_RAY }

    static final class Fighter {
        final String name;
        final Skill skill;
        final boolean custom;
        float x,y,vy,hp,maxHp,cooldown,stun,hitTime,attackTime,attackDuration,botMove,walkTime,guardTime;
        float specialCooldown=15,specialTime,specialDuration,specialStartX,specialEndX,specialStartY;
        float specialTargetX,specialTargetY,slow,frozen,distortion,quietTime,shieldClock=7;
        float objectClock=15,alert,hoverTime,slideVelocity,exhaustion,iceClock,allyDecision;
        float guardHold,guardLock,noJump,electricTime,airDashTime,airDashVelocity,teleMoveTime,teleMoveTargetX;
        int attack=-1,dir=1,specialStage,defendedAttacks,allyWave,energyHits,lastMoveDirection,pantherDirection;
        boolean attackResolved,guard,moving,knockedOut,specialActive,forceShield,armedMelee;
        boolean rewindUsed,redTriggered,secondAllyQueued,airDashUsed,dusted;
        Fighter ally,master;
        Snapshot rewind;
        Fighter(String name,String power,boolean custom,float x){this(name,power,custom,x,false);}
        Fighter(String name,String power,boolean custom,float x,boolean generic){
            this.name=name;this.custom=custom;this.skill=generic?Skill.GENERIC:custom?customSkill(power):heroSkill(name);
            this.x=x;maxHp=skill==Skill.THANOS?130:skill==Skill.ALLY?20:120;hp=maxHp;
            if(skill==Skill.THANOS)specialCooldown=20;
            if(skill==Skill.STRANGE||skill==Skill.NINJA||skill==Skill.GENERIC)specialCooldown=10;
        }
        Fighter copy(){
            Fighter f=new Fighter(name,powerName(skill),custom,x,skill==Skill.GENERIC);
            f.copyState(this);return f;
        }
        void copyState(Fighter f){
            x=f.x;y=f.y;vy=f.vy;hp=f.hp;maxHp=f.maxHp;cooldown=f.cooldown;stun=f.stun;
            hitTime=f.hitTime;attackTime=f.attackTime;attackDuration=f.attackDuration;botMove=f.botMove;
            walkTime=f.walkTime;guardTime=f.guardTime;specialCooldown=f.specialCooldown;
            specialTime=f.specialTime;specialDuration=f.specialDuration;specialStartX=f.specialStartX;
            specialEndX=f.specialEndX;specialStartY=f.specialStartY;specialTargetX=f.specialTargetX;
            specialTargetY=f.specialTargetY;slow=f.slow;frozen=f.frozen;distortion=f.distortion;
            quietTime=f.quietTime;shieldClock=f.shieldClock;objectClock=f.objectClock;alert=f.alert;
            hoverTime=f.hoverTime;slideVelocity=f.slideVelocity;exhaustion=f.exhaustion;iceClock=f.iceClock;
            guardHold=f.guardHold;guardLock=f.guardLock;noJump=f.noJump;electricTime=f.electricTime;
            airDashTime=f.airDashTime;airDashVelocity=f.airDashVelocity;
            teleMoveTime=f.teleMoveTime;teleMoveTargetX=f.teleMoveTargetX;
            allyDecision=f.allyDecision;attack=f.attack;dir=f.dir;specialStage=f.specialStage;
            defendedAttacks=f.defendedAttacks;allyWave=f.allyWave;energyHits=f.energyHits;
            lastMoveDirection=f.lastMoveDirection;pantherDirection=f.pantherDirection;
            attackResolved=f.attackResolved;
            guard=f.guard;moving=f.moving;knockedOut=f.knockedOut;specialActive=f.specialActive;
            forceShield=f.forceShield;armedMelee=f.armedMelee;redTriggered=f.redTriggered;
            secondAllyQueued=f.secondAllyQueued;airDashUsed=f.airDashUsed;dusted=f.dusted;
            ally=f.ally==null?null:f.ally.copy();
            if(ally!=null)ally.master=this;
        }
    }

    static final class Projectile {
        final Fighter owner;
        float x,y,vx,vy,damage,life=2,maxDistance=1000,startX,radius=13;
        int direction;
        boolean slow,freeze,returning,hitOut,hitBack,boomerang,thorHammer,special,unblockable;
        String style;
        Projectile(Fighter f,float x,float y,float vx,float vy,float damage,String style){
            owner=f;this.x=x;this.y=y;this.vx=vx;this.vy=vy;this.damage=damage;
            this.style=style;startX=x;direction=vx<0?-1:vx>0?1:f.dir;
        }
        Projectile copy(){Projectile p=new Projectile(owner,x,y,vx,vy,damage,style);
            p.life=life;p.maxDistance=maxDistance;p.startX=startX;p.radius=radius;
            p.direction=direction;p.slow=slow;p.freeze=freeze;p.returning=returning;
            p.hitOut=hitOut;p.hitBack=hitBack;p.boomerang=boomerang;p.thorHammer=thorHammer;
            p.special=special;p.unblockable=unblockable;return p;}
    }

    static final class Effect {
        final Fighter owner;
        final Form form;
        float x,y,range,damage,delay,duration,age,guardDamage=-1;
        boolean resolved,special;
        int direction;
        Effect(Fighter f,Form form,float x,float y,float range,float damage,float delay,float duration){
            owner=f;this.form=form;this.x=x;this.y=y;this.range=range;this.damage=damage;
            this.delay=delay;this.duration=duration;direction=f.dir;
        }
        Effect copy(){Effect e=new Effect(owner,form,x,y,range,damage,delay,duration);
            e.age=age;e.guardDamage=guardDamage;e.resolved=resolved;e.direction=direction;
            e.special=special;return e;}
    }
    static final class IcePatch {
        final Fighter owner;float x,life=5;
        IcePatch(Fighter owner,float x){this.owner=owner;this.x=x;}
        IcePatch copy(){IcePatch p=new IcePatch(owner,x);p.life=life;return p;}
    }
    static final class Hit {
        final Fighter target,attacker;final float damage;final boolean guarded,blocked;
        Hit(Fighter t,Fighter a,float damage,boolean guarded,boolean blocked){
            target=t;attacker=a;this.damage=damage;this.guarded=guarded;this.blocked=blocked;
        }
    }
    static final class Snapshot {
        Fighter player,bot;float time;
        final ArrayList<Projectile> shots=new ArrayList<>();
        final ArrayList<Effect> effects=new ArrayList<>();
        final ArrayList<IcePatch> ice=new ArrayList<>();
    }

    final Fighter player,bot;
    final ArrayList<Projectile> projectiles=new ArrayList<>();
    final ArrayList<Effect> effects=new ArrayList<>();
    final ArrayList<IcePatch> ice=new ArrayList<>();
    final ArrayList<Hit> hits=new ArrayList<>();
    final Random random;
    int difficulty,storyBattle,revision;
    boolean storyTech,over,victory;
    float time,aiDecision=1,shake;
    BattleEngine(String playerName,String botName,String power,boolean custom,int difficulty,
                 int storyBattle,boolean storyTech,long seed){
        this(playerName,botName,power,custom,difficulty,storyBattle,storyTech,seed,false);
    }
    BattleEngine(String playerName,String botName,String power,boolean custom,int difficulty,
                 int storyBattle,boolean storyTech,long seed,boolean genericBot){
        player=new Fighter(playerName,power,custom,220);bot=new Fighter(botName,null,false,624,genericBot);
        this.difficulty=difficulty;this.storyBattle=storyBattle;this.storyTech=storyTech;
        random=new Random(seed);face();
    }
    static Skill heroSkill(String n){
        if(n.contains("Projeção"))return Skill.ALLY;
        if(n.contains("Aranha"))return Skill.SPIDER;if(n.contains("Ferro"))return Skill.IRON;
        if(n.contains("Wolverine"))return Skill.WOLVERINE;if(n.contains("Capitão América"))return Skill.CAPTAIN;
        if(n.equals("Thor"))return Skill.THOR;if(n.equals("Hulk"))return Skill.HULK;
        if(n.contains("Pantera"))return Skill.PANTHER;if(n.contains("Estranho"))return Skill.STRANGE;
        if(n.contains("Viúva"))return Skill.WIDOW;if(n.contains("Escarlate"))return Skill.SCARLET;
        if(n.contains("Doom"))return Skill.DOOM;if(n.contains("Thanos"))return Skill.THANOS;
        if(n.contains("Marvel"))return Skill.MARVEL;return Skill.ENERGY;
    }
    static Skill customSkill(String p){
        if("Gelo".equals(p))return Skill.ICE;if("Velocidade".equals(p))return Skill.SPEED;
        if("Super Força".equals(p)||"Superforça".equals(p))return Skill.STRENGTH;
        if("Ninja".equals(p))return Skill.NINJA;if("Telecinese".equals(p))return Skill.TELEKINESIS;
        return Skill.ENERGY;
    }
    static String powerName(Skill s){
        switch(s){case ICE:return "Gelo";case SPEED:return "Velocidade";
            case STRENGTH:return "Super Força";case NINJA:return "Ninja";
            case TELEKINESIS:return "Telecinese";default:return "Energia";}
    }
    Fighter opponent(Fighter f){return f==player||f.master==player?bot:player;}
    private Fighter root(Fighter f){return f.master==null?f:f.master;}
    void face(){if(Math.abs(player.x-bot.x)>.1f){player.dir=player.x<bot.x?1:-1;bot.dir=-player.dir;}}
    private boolean busy(Fighter f){return f.specialActive&&f.skill!=Skill.CAPTAIN
            &&f.skill!=Skill.SCARLET&&f.skill!=Skill.STRENGTH;}
    boolean canAct(Fighter f){return !over&&f.hp>0&&f.stun<=0&&f.frozen<=0&&f.exhaustion<=0&&!busy(f);}
    boolean canSpecial(Fighter f){return canAct(f)&&f.attack<0&&f.specialCooldown<=0&&!f.specialActive
            &&(f.skill!=Skill.STRANGE||f.y==0);}
    boolean guarding(Fighter f){return f.guard&&f.guardLock<=0&&f.frozen<=0&&f.distortion<=0&&f.master==null;}
    void guard(Fighter f,boolean held){
        if(!held){f.guard=false;f.guardHold=0;return;}
        f.guard=f.hp>0&&f.guardLock<=0&&f.frozen<=0&&f.distortion<=0;
        if(f.guard)f.attack=-1;
    }
    boolean jump(Fighter f){
        if(!canAct(f)||f.y!=0||f.distortion>0||f.noJump>0)return false;
        f.vy=f.skill==Skill.STRENGTH?710:650;f.hoverTime=0;f.airDashUsed=false;return true;
    }
    boolean airDash(Fighter f,int direction){
        if(f.skill!=Skill.THOR||f.y<=0||f.airDashUsed||direction==0||!canAct(f))return false;
        f.airDashUsed=true;f.airDashTime=.16f;f.airDashVelocity=direction*510;return true;
    }
    float speed(Fighter f){return (f.skill==Skill.PANTHER?1.22f:f.skill==Skill.SPEED?1.42f:
            f.skill==Skill.STRENGTH?.80f:1)*(f.slow>0?.70f:1);}
    float pace(Fighter f){return f.skill==Skill.SPEED?.90f:f.skill==Skill.MARVEL?1.18f:
            f.skill==Skill.WIDOW?.90f:1;}
    int basicDamage(Fighter f,int slot){
        int damage=slot==1?10:8;
        if(f.skill==Skill.DOOM||f.skill==Skill.THANOS)damage++;
        if(f.skill==Skill.STRENGTH)damage+=2+(f.specialActive?2:0);
        if(f.skill==Skill.NINJA&&slot==0)damage--;
        if(f.skill==Skill.HULK&&f.hp<=30)damage+=5;
        return damage;
    }
    int commonDamage(Fighter f){
        int d;
        switch(f.skill){case SPIDER:d=5;break;case WOLVERINE:d=6;break;case HULK:d=6;break;
            case MARVEL:case ICE:d=7;break;case WIDOW:d=3;break;
            case TELEKINESIS:d=2;break;case GENERIC:d=8;break;
            case PANTHER:d=2;break;case THOR:d=8;break;case CAPTAIN:d=4;break;
            case ENERGY:if(f.custom)return 9;
            default:d=basicDamage(f,2);return d;}
        return d+(f.skill==Skill.HULK&&f.hp<=30?5:0);
    }
    int specialDamage(Fighter f,int base){return base+(f.skill==Skill.HULK&&f.hp<=30?5:0)
            +(f.skill==Skill.STRENGTH?2+(f.specialActive?2:0):0);}
    boolean attack(Fighter f,int slot){
        if(slot<0||slot>2||!canAct(f)||f.cooldown>0||f.attack>=0)return false;
        face();f.attack=slot;f.attackTime=0;f.attackResolved=false;f.quietTime=0;f.guard=false;
        float attackPace=f.skill==Skill.MARVEL&&slot==2?1:pace(f);
        f.attackDuration=DURATIONS[slot]/attackPace;
        if(slot==2&&f.skill==Skill.PANTHER)f.attackDuration=.32f;
        f.cooldown=slot==2&&f.skill==Skill.HULK?0:
                COOLDOWNS[slot]/attackPace+(f==bot?(.18f+random.nextFloat()*.28f)/aiRate():0);
        if(slot==2&&f.skill==Skill.PANTHER)f.cooldown*=.5f;
        if(slot==2&&f.skill==Skill.STRENGTH){
            f.attackDuration=.8f;f.specialStartX=f.x;
            Fighter target=opponent(f);
            f.specialEndX=clamp(target.x-f.dir*65,EDGE,WIDTH-EDGE);
        }
        if(slot==2&&f.skill==Skill.SPEED){
            f.specialStartX=f.x;f.specialEndX=clamp(opponent(f).x+f.dir*70,EDGE,WIDTH-EDGE);
        }
        if(slot==2&&f.skill==Skill.PANTHER)
            f.pantherDirection=f.moving&&f.lastMoveDirection==-f.dir?-f.dir:f.dir;
        return true;
    }
    boolean special(Fighter f){
        if(!canSpecial(f))return false;
        face();Fighter target=opponent(f);f.quietTime=0;f.guard=false;
        int firstEffect=effects.size();
        f.specialActive=true;f.specialTime=0;f.specialStage=0;f.specialStartX=f.x;
        f.specialStartY=f.y;f.specialTargetX=target.x;f.specialTargetY=target.y;
        f.specialCooldown=f.skill==Skill.THANOS?20:
                f.skill==Skill.STRANGE||f.skill==Skill.NINJA||f.skill==Skill.GENERIC?10:15;
        f.specialDuration=.75f;
        switch(f.skill){
            case GENERIC:
                f.specialDuration=.4f;f.specialEndX=dashEnd(f,WIDTH);break;
            case SPIDER:
                f.specialDuration=1.35f;f.specialEndX=clamp(target.x+f.dir*90,EDGE,WIDTH-EDGE);break;
            case IRON:
                f.specialDuration=.78f;Effect beam=effect(f,Form.BEAM,f.x,f.y+78,WIDTH,35,.5f,.20f);
                beam.guardDamage=5;break;
            case WOLVERINE:
                f.specialDuration=.90f;f.specialEndX=dashEnd(f,190);break;
            case CAPTAIN:
                f.specialDuration=5;f.specialCooldown=20;break;
            case THOR:effect(f,Form.STRIKE,target.x,0,58,15,.5f,.25f);break;
            case HULK:
                f.specialDuration=.8f;f.specialEndX=clamp(f.x+f.dir*Math.min(180,Math.abs(target.x-f.x)),EDGE,WIDTH-EDGE);break;
            case PANTHER:
                effect(f,Form.BURST,f.x,f.y+70,165,Math.min(10,f.defendedAttacks)*6,.20f,.35f);
                f.defendedAttacks=0;break;
            case STRANGE:
                effect(f,Form.PORTAL,f.x,f.y+60,70,10,0,.4f);
                f.specialEndX=f.x<WIDTH/2?WIDTH-EDGE-35:EDGE+35;break;
            case WIDOW:
                f.armedMelee=true;f.specialDuration=.4f;effect(f,Form.SMOKE,f.x,f.y,55,0,0,.4f);break;
            case SCARLET:
                f.specialDuration=5;f.specialCooldown=20;
                Projectile curse=shoot(f,0,1320,0,"scarlet_special");
                curse.special=true;curse.unblockable=true;break;
            case MARVEL:
                f.specialDuration=.85f;f.specialEndX=clamp(target.x+f.dir*95,EDGE,WIDTH-EDGE);break;
            case DOOM:effect(f,Form.STRIKE,target.x,0,39,20,.80f,.3f);f.specialDuration=1.1f;break;
            case THANOS:f.specialDuration=1.5f;break;
            case ICE:
                effect(f,Form.VOLLEY,f.x,f.y+78,1,0,.35f,.01f);break;
            case SPEED:
                f.specialDuration=.48f;f.specialEndX=dashEnd(f,240);break;
            case STRENGTH:f.specialDuration=5;f.specialCooldown=20;break;
            case NINJA:
                f.specialDuration=.48f;f.specialEndX=f.x<WIDTH/2?WIDTH-EDGE-35:EDGE+35;
                effect(f,Form.SMOKE,f.x,f.y,65,0,0,.48f);break;
            case TELEKINESIS:
                Projectile pull=shoot(f,3,700,0,"tele_special");pull.special=true;break;
            default:
                effect(f,Form.BURST,f.x,f.y+70,155,Math.min(24,f.energyHits*2),.25f,.35f);
                f.energyHits=0;break;
        }
        for(int i=firstEffect;i<effects.size();i++)effects.get(i).special=true;
        return true;
    }
    private float dashEnd(Fighter f,float distance){
        Fighter t=opponent(f);return clamp(f.x+f.dir*Math.min(distance,Math.max(0,Math.abs(t.x-f.x)-65)),EDGE,WIDTH-EDGE);
    }
    private Effect effect(Fighter f,Form form,float x,float y,float range,float damage,float delay,float duration){
        Effect e=new Effect(f,form,x,y,range,damage,delay,duration);effects.add(e);return e;
    }

    void step(float dt,int movement,boolean jumpHeld,boolean runAI){
        if(over)return;dt=Math.min(.05f,Math.max(0,dt));time+=dt;shake=down(shake,dt);
        int originalRevision=revision;
        face();tick(player,dt);tick(bot,dt);
        if(runAI)ai(dt);
        // The left/right buttons keep their screen direction during a crossing.
        move(player,movement*220*speed(player),dt);
        move(bot,bot.botMove*130*speed(bot)*aiSpeed(),dt);
        for(Fighter f:new Fighter[]{player,bot}){
            if(f.specialActive)advanceSpecial(f,dt);
            else physics(f,dt,f==player?jumpHeld:f.skill==Skill.IRON&&opponent(f).y<30);
            if(over)return;
            f.x=clamp(f.x,EDGE,WIDTH-EDGE);
        }
        separate();face();
        for(Fighter f:new Fighter[]{player,bot}){
            if(f.attack>=0){
                f.attackTime+=dt;
                if(f.attack==2&&f.skill==Skill.PANTHER&&!f.attackResolved)
                    f.x=clamp(f.x+f.pantherDirection*530*dt,EDGE,WIDTH-EDGE);
                if(f.attack==2&&f.skill==Skill.STRENGTH){
                    float u=Math.min(1,f.attackTime/f.attackDuration);
                    f.x=lerp(f.specialStartX,f.specialEndX,smooth(u));
                    f.y=Math.max(0,(float)Math.sin(Math.PI*u)*195);
                }
                if(f.attack==2&&f.skill==Skill.SPEED){
                    float u=Math.min(1,f.attackTime/f.attackDuration);
                    f.x=lerp(f.specialStartX,f.specialEndX,smooth(u));
                    Fighter target=opponent(f);
                    int travel=f.specialEndX>=f.specialStartX?1:-1;
                    if(!f.attackResolved&&(f.x-target.x)*travel>=-25){
                        f.attackResolved=true;resolveAttack(f,2);
                        if(over||revision!=originalRevision)return;
                    }
                }
                float hitFraction=f.skill==Skill.STRENGTH&&f.attack==2?1:
                        f.skill==Skill.PANTHER?.7f:.45f;
                if(!f.attackResolved&&(f.skill!=Skill.SPEED||f.attack!=2)&&
                        f.attackTime>=f.attackDuration*hitFraction){
                    f.attackResolved=true;resolveAttack(f,f.attack);
                    if(over||revision!=originalRevision)return;
                }
                if(f.attackTime>=f.attackDuration){
                    if(f.skill==Skill.STRENGTH&&f.attack==2){f.y=0;f.vy=0;}
                    f.attack=-1;
                }
            }
            passives(f,dt);allies(f,dt);
        }
        for(int i=0;i<effects.size();i++){
            Effect e=effects.get(i);e.age+=dt;
            if(e.form==Form.BEAM&&!e.resolved){e.direction=e.owner.dir;e.x=e.owner.x;e.y=e.owner.y+78;}
            if(!e.resolved&&e.age>=e.delay){e.resolved=true;resolveEffect(e);
                if(over||revision!=originalRevision)return;}
            if(e.age>=e.delay+e.duration){effects.remove(i--);}
        }
        for(int i=0;i<projectiles.size();i++){
            Projectile p=projectiles.get(i);Fighter target=opponent(p.owner);
            float multiplier=1;
            if(target.skill==Skill.SPIDER&&Math.abs(p.x-target.x)<150&&Math.abs(p.y-(target.y+70))<160){
                multiplier=.55f;target.alert=.20f;
            }
            p.x+=p.vx*dt*multiplier;p.y+=p.vy*dt*multiplier;p.life-=dt;
            Fighter contact=projectileContact(p,target);
            if(contact!=null&&(!p.boomerang&&!p.thorHammer||
                    p.returning?!p.hitBack:!p.hitOut)){
                if(p.style.equals("scarlet_special")){
                    contact.distortion=5;contact.guard=false;
                    p.owner.specialTime=0;p.owner.specialDuration=5;
                    hits.add(new Hit(contact,p.owner,0,false,false));
                }else{
                    boolean shielded=contact.forceShield,wasDefended=guarding(contact);
                    boolean isBox=p.style.equals("object");
                    boolean landed=hurt(contact,p.owner,p.damage,true,p.special,
                            p.owner.skill==Skill.TELEKINESIS?0:-1,p.direction,isBox);
                    if(landed&&p.slow)contact.slow=1.5f;
                    if(landed&&p.freeze){contact.frozen=3;contact.guard=false;contact.attack=-1;}
                    if(!shielded&&(landed||wasDefended)&&p.owner.skill==Skill.ENERGY&&p.owner.custom)
                        p.owner.energyHits=Math.min(12,p.owner.energyHits+1);
                    if(p.owner.skill==Skill.TELEKINESIS&&!isBox&&!shielded&&!wasDefended&&
                            contact.skill!=Skill.THANOS){
                        contact.teleMoveTargetX=p.style.equals("tele_special")?
                                clamp(p.owner.x+p.direction*85,EDGE,WIDTH-EDGE):
                                p.direction>0?WIDTH-EDGE:EDGE;
                        float travel=Math.abs(contact.teleMoveTargetX-contact.x);
                        contact.teleMoveTime=Math.max(.35f,Math.min(.9f,travel/650f));
                    }
                }
                if(p.boomerang){if(p.returning)p.hitBack=true;else p.hitOut=true;}
                else if(p.thorHammer){
                    Effect spark=effect(p.owner,Form.THOR_SPARK,contact.x,0,35,2,0,.25f);
                    spark.guardDamage=1;projectiles.remove(i--);continue;
                }
                else projectiles.remove(i--);
                if(over||revision!=originalRevision)return;
            }
            if(p.boomerang){
                if(!p.returning&&(Math.abs(p.x-p.startX)>=p.maxDistance||p.x<=EDGE||p.x>=WIDTH-EDGE)){
                    p.returning=true;p.vx=-p.vx;p.direction=-p.direction;
                }
                if(p.returning&&(p.x-p.owner.x)*p.direction>=0){projectiles.remove(i--);continue;}
            }else if(p.thorHammer&&(Math.abs(p.x-p.startX)>=p.maxDistance||p.x<=EDGE||p.x>=WIDTH-EDGE)){
                Effect spark=effect(p.owner,Form.THOR_SPARK,clamp(p.x,EDGE,WIDTH-EDGE),0,35,2,0,.25f);
                spark.guardDamage=1;projectiles.remove(i--);continue;
            }
            if(i>=0&&i<projectiles.size()&&projectiles.get(i)==p&&
                    (p.life<=0||p.x<-40||p.x>WIDTH+40||p.y<-15||
                            !p.boomerang&&!p.thorHammer&&Math.abs(p.x-p.startX)>p.maxDistance)){
                projectiles.remove(i--);
            }
        }
        for(int i=0;i<ice.size();i++){ice.get(i).life-=dt;if(ice.get(i).life<=0)ice.remove(i--);}
        separate();face();
    }
    private void tick(Fighter f,float dt){
        f.cooldown=down(f.cooldown,dt);f.stun=down(f.stun,dt);f.hitTime=down(f.hitTime,dt);
        f.guardTime=down(f.guardTime,dt);f.guardLock=down(f.guardLock,dt);
        f.noJump=down(f.noJump,dt);f.electricTime=down(f.electricTime,dt);
        f.specialCooldown=down(f.specialCooldown,dt);
        f.slow=down(f.slow,dt);f.frozen=down(f.frozen,dt);f.distortion=down(f.distortion,dt);
        f.alert=down(f.alert,dt);f.walkTime+=dt;f.quietTime+=dt;
        if(f.exhaustion>0&&f.y==0)f.exhaustion=down(f.exhaustion,dt);
        if(f==bot&&f.guardTime==0)guard(f,false);
        if(f.frozen>0||f.distortion>0)f.guard=false;
        if(f.guard){f.guardHold+=dt;if(f.guardHold>=5){f.guard=false;f.guardHold=0;f.guardLock=3;}}
        else if(f.guardLock<=0)f.guardHold=0;
    }
    private void move(Fighter f,float velocity,float dt){
        if(!canAct(f)||f.attack>=0)velocity=0;
        if(Math.abs(velocity)>5)f.lastMoveDirection=velocity>0?1:-1;
        boolean slippery=false;
        if(f.y==0)for(IcePatch p:ice)if(p.owner!=f&&Math.abs(p.x-f.x)<38){slippery=true;break;}
        if(slippery&&f.frozen<=0&&!busy(f)){
            f.slideVelocity+=(velocity-f.slideVelocity)*Math.min(1,dt*2.6f);
            if(Math.abs(f.slideVelocity)<15&&velocity!=0)f.slideVelocity=velocity*.45f;
            velocity=f.slideVelocity;
        }else f.slideVelocity=velocity;
        if(f.airDashTime>0){velocity+=f.airDashVelocity;f.airDashTime=down(f.airDashTime,dt);}
        if(f.teleMoveTime>0){
            velocity+=(f.teleMoveTargetX-f.x)/Math.max(.01f,f.teleMoveTime);
            f.teleMoveTime=down(f.teleMoveTime,dt);
        }
        f.x+=velocity*dt;f.moving=Math.abs(velocity)>5;
    }
    private void physics(Fighter f,float dt,boolean held){
        float speed=f.skill==Skill.SPEED?1.20f:f.skill==Skill.WIDOW?1.15f:1;
        if(f.y>0||f.vy>0){
            boolean hover=f.skill==Skill.IRON&&held&&f.vy<=70&&f.hoverTime<1.15f&&f.exhaustion<=0&&f.frozen<=0;
            if(hover){f.vy=Math.max(f.vy,-30);f.hoverTime+=dt;}
            f.y+=f.vy*dt*speed;f.vy-=(hover?100:1900)*dt*speed;
            if(f.y<=0){f.y=0;f.vy=0;}
        }
    }
    private void advanceSpecial(Fighter f,float dt){
        f.specialTime+=dt;float t=f.specialTime;Fighter target=opponent(f);
        switch(f.skill){
            case GENERIC:
                float next=lerp(f.specialStartX,f.specialEndX,smooth(Math.min(1,t/f.specialDuration)));
                int toward=f.specialEndX>=f.specialStartX?1:-1;
                if(Math.abs(f.y-target.y)<65&&(target.x-f.x)*toward>0)
                    next=toward>0?Math.min(next,target.x-65):Math.max(next,target.x+65);
                f.x=clamp(next,EDGE,WIDTH-EDGE);physics(f,dt,false);break;
            case IRON:
                if(t<.5f)f.vy=0;else physics(f,dt,false);
                break;
            case SPIDER:
                float u=Math.min(1,t/f.specialDuration);
                f.x=lerp(f.specialStartX,f.specialEndX,smooth(u));
                f.y=Math.max(0,lerp(f.specialStartY,0,u)+(float)Math.sin(Math.PI*u)*145);
                if(t>=1.15f&&f.specialStage==0){f.specialStage=1;
                    int launchDirection=f.specialTargetX>=f.specialStartX?1:-1;
                    if(target.skill!=Skill.THANOS&&
                            (f.specialEndX-target.x)*launchDirection<65&&Math.abs(f.x-target.x)<145
                            &&Math.abs(f.y-target.y)<125){
                        // Reserve a landing lane when the opponent is against the arena wall.
                        target.x=clamp(f.specialEndX-launchDirection*70,EDGE,WIDTH-EDGE);face();
                    }
                    melee(f,15,145,125,true,false);}
                break;
            case WOLVERINE:
                if(t<.30f)f.x=lerp(f.specialStartX,f.specialEndX,smooth(t/.30f));
                if(t>=.30f&&f.specialStage==0){melee(f,10,110,80,true,false);
                    f.specialStage=1;face();f.specialStartX=f.x;f.specialEndX=dashEnd(f,190);}
                if(t>=.45f&&t<.75f)f.x=lerp(f.specialStartX,f.specialEndX,smooth((t-.45f)/.30f));
                if(t>=.75f&&f.specialStage==1){f.specialStage=2;melee(f,10,110,80,true,false);}
                physics(f,dt,false);break;
            case HULK:
                float h=Math.min(1,t/f.specialDuration);
                f.x=lerp(f.specialStartX,f.specialEndX,smooth(h));
                f.y=Math.max(0,lerp(f.specialStartY,0,h)+(float)Math.sin(Math.PI*h)*150);
                if(h>=1&&f.specialStage==0){f.specialStage=1;
                    Effect impact=effect(f,Form.WAVE,f.x,0,165,specialDamage(f,15),0,.45f);
                    impact.special=true;}
                break;
            case MARVEL:
                f.x=lerp(f.specialStartX,f.specialEndX,smooth(Math.min(1,t/.75f)));
                f.y=f.specialStartY>30?Math.max(f.specialStartY,105):0;
                if(f.specialStage==0&&Math.abs(target.x-f.x)<85){f.specialStage=1;
                    melee(f,15,110,150,true,true);}
                break;
            case SPEED:
                f.x=lerp(f.specialStartX,f.specialEndX,smooth(Math.min(1,t/.40f)));
                if(t>=.35f&&f.specialStage==0){f.specialStage=1;melee(f,15,115,90,true,false);}
                physics(f,dt,false);break;
            case STRANGE:case NINJA:
                if(t>=.20f&&f.specialStage==0){f.specialStage=1;f.x=f.specialEndX;face();}
                physics(f,dt,false);break;
            case THANOS:
                if(t>=1.5f&&f.specialStage==0){
                    f.specialStage=1;target.hp=0;target.knockedOut=true;target.dusted=true;
                    hits.add(new Hit(target,f,0,false,false));
                    over=true;victory=root(f)==player;
                }
                physics(f,dt,false);break;
            default:physics(f,dt,false);break;
        }
        if(t>=f.specialDuration){f.specialActive=false;
            if(f.skill==Skill.SPIDER||f.skill==Skill.HULK){f.y=0;f.vy=0;}
            if(f.skill==Skill.CAPTAIN||f.skill==Skill.SCARLET||f.skill==Skill.STRENGTH)
                f.specialCooldown=15;
        }
    }
    private void separate(){
        player.x=clamp(player.x,EDGE,WIDTH-EDGE);bot.x=clamp(bot.x,EDGE,WIDTH-EDGE);
        if(player.attack==2&&player.skill==Skill.SPEED||bot.attack==2&&bot.skill==Skill.SPEED)return;
        if(Math.abs(player.y-bot.y)>=65)return;
        float distance=Math.abs(player.x-bot.x);
        float gap=(livingAlly(player)||livingAlly(bot))?110:62;
        if(distance>=gap)return;
        int direction=distance>.1f?(player.x<bot.x?1:-1):player.dir;
        if(player.skill==Skill.THANOS){
            float desired=player.x+direction*gap;
            bot.x=desired<EDGE||desired>WIDTH-EDGE?clamp(player.x-direction*gap,EDGE,WIDTH-EDGE):desired;
            return;
        }
        if(bot.skill==Skill.THANOS){
            float desired=bot.x-direction*gap;
            player.x=desired<EDGE||desired>WIDTH-EDGE?clamp(bot.x+direction*gap,EDGE,WIDTH-EDGE):desired;
            return;
        }
        float mid=(player.x+bot.x)/2;
        float left=clamp(mid-gap/2,EDGE,WIDTH-EDGE-gap),right=left+gap;
        if(direction>0){player.x=left;bot.x=right;}else{player.x=right;bot.x=left;}
    }
    private void resolveAttack(Fighter f,int slot){
        face();if(slot<2){
            boolean armed=f.armedMelee;f.armedMelee=false;
            Fighter struck=melee(f,basicDamage(f,slot),slot==1?134:f.skill==Skill.NINJA?150:108,70,false,false);
            if(armed&&struck!=null){struck.stun=Math.max(struck.stun,2);
                struck.electricTime=Math.max(struck.electricTime,2);}
            return;
        }
        switch(f.skill){
            case WOLVERINE:melee(f,6,216,70,false,false);effect(f,Form.SLASH,f.x+f.dir*100,f.y+78,100,0,0,.2f);break;
            case PANTHER:
                meleeDirectional(f,2,115,70,false,false,1,f.pantherDirection);break;
            case HULK:
                projectiles.removeIf(p->root(p.owner)!=root(f)&&!p.special);
                effects.removeIf(e->root(e.owner)!=root(f)&&!e.special);
                Fighter interrupted=opponent(f);
                if(Math.abs(interrupted.x-f.x)<175&&interrupted.attack>=0)interrupted.attack=-1;
                shake=.18f;
                melee(f,commonDamage(f),140,70,false,true);
                effect(f,Form.SLASH,f.x+f.dir*60,f.y+78,80,0,0,.3f);break;
            case WIDOW:
                shoot(f,commonDamage(f),880,0,"common");
                effect(f,Form.VOLLEY,f.x,f.y+78,0,commonDamage(f),.14f,.01f);break;
            case MARVEL:
                shoot(f,commonDamage(f),672,0,"marvel_beam");break;
            case STRENGTH:
                f.y=0;f.vy=0;
                effect(f,Form.WAVE,f.x,0,140,0,0,.3f);break;
            case DOOM:
                Fighter doomTarget=opponent(f);
                for(int i=0;i<5;i++)effect(f,Form.GREEN_RAY,
                        clamp(doomTarget.x+(i-2)*43,EDGE,WIDTH-EDGE),330,19,3,
                        .55f+i*.18f,.18f);
                break;
            case SPEED:
                Fighter swiftTarget=opponent(f);
                if(Math.abs(f.x-swiftTarget.x)<92)
                    hurt(swiftTarget,f,2,false,false,0,
                            f.specialEndX>=f.specialStartX?1:-1);
                break;
            default:
                Projectile p=shoot(f,commonDamage(f),f.skill==Skill.SPIDER?690:f.skill==Skill.SPEED?740:
                        f.skill==Skill.ENERGY&&f.custom?772.8f:560,0,"common");
                p.slow=f.skill==Skill.SPIDER;
                if(f.skill==Skill.THOR){p.maxDistance=WIDTH/3-48;p.thorHammer=true;}
                if(f.skill==Skill.CAPTAIN){p.maxDistance=WIDTH/3-48;p.boomerang=true;}
                break;
        }
    }
    private Projectile shoot(Fighter f,float damage,float speed,float vertical,String style){
        Projectile p=new Projectile(f,f.x+f.dir*48,f.y+78,f.dir*speed,vertical,damage,style);
        projectiles.add(p);return p;
    }
    private void resolveEffect(Effect e){
        Fighter f=e.owner,t=opponent(f);
        if(e.form==Form.VOLLEY){
            if(f.skill==Skill.THANOS){
                Projectile p=new Projectile(f,e.x,e.y,0,-260,5,"meteor");
                p.life=2;p.radius=17;p.special=e.special;projectiles.add(p);
            }else if(f.skill==Skill.ICE){
                Projectile p=shoot(f,0,600,0,"freeze");p.freeze=true;
            }else shoot(f,e.damage,f.skill==Skill.WIDOW?880:780,0,
                    f.skill==Skill.MARVEL?"marvel_beam":"common");
            return;
        }
        if(e.form==Form.SMOKE||e.form==Form.TIME||e.form==Form.SLASH)return;
        Fighter contact=t;
        if(livingAlly(t)&&effectTouches(e,t.ally))contact=t.ally;
        if(effectTouches(e,contact)){
            boolean defended=guarding(contact);
            boolean landed=hurt(contact,f,e.damage,true,true,e.guardDamage,e.direction);
            if(e.form==Form.BEAM&&landed&&!defended)contact.stun=Math.max(contact.stun,2);
            if(e.form==Form.WAVE&&f.skill==Skill.HULK&&landed)
                contact.noJump=Math.max(contact.noJump,3);
        }
        if(e.form==Form.BEAM){f.exhaustion=3;f.vy=Math.min(f.vy,-300);}
    }
    private boolean effectTouches(Effect e,Fighter t){
        switch(e.form){
            case BEAM:return (t.x-e.x)*e.direction>=-15&&e.y>=t.y+10&&e.y<=t.y+130;
            case STRIKE:case GREEN_RAY:case THOR_SPARK:return Math.abs(t.x-e.x)<e.range+20;
            case WAVE:return Math.abs(t.x-e.x)<e.range&&t.y<45;
            case BURST:case PORTAL:return Math.abs(t.x-e.x)<e.range&&Math.abs((t.y+70)-e.y)<170;
            default:return false;
        }
    }
    private Fighter melee(Fighter f,float damage,float range,float vertical,boolean special,boolean heavy){
        face();return meleeDirectional(f,damage,range,vertical,special,heavy,-1,f.dir);
    }
    private Fighter meleeDirectional(Fighter f,float damage,float range,float vertical,
                                     boolean special,boolean heavy,float guardOverride,int direction){
        Fighter target=opponent(f),contact=target;
        if(livingAlly(target)&&meleeTouches(f,target.ally,range,vertical,direction))contact=target.ally;
        if(!meleeTouches(f,contact,range,vertical,direction))return null;
        return hurt(contact,f,damage,heavy,special,guardOverride,direction)?contact:null;
    }
    private boolean meleeTouches(Fighter f,Fighter t,float range,float vertical){
        return meleeTouches(f,t,range,vertical,f.dir);
    }
    private boolean meleeTouches(Fighter f,Fighter t,float range,float vertical,int direction){
        float distance=(t.x-f.x)*direction;
        return distance>=-20&&distance<=range&&Math.abs(f.y-t.y)<vertical;
    }
    private Fighter projectileContact(Projectile p,Fighter t){
        if(livingAlly(t)&&projectileTouches(p,t.ally))return t.ally;
        return projectileTouches(p,t)?t:null;
    }
    private boolean projectileTouches(Projectile p,Fighter t){
        return Math.abs(p.x-t.x)<25+p.radius&&p.y>=t.y+10-p.radius&&p.y<=t.y+130+p.radius;
    }

    boolean hurt(Fighter target,Fighter attacker,float raw,boolean heavy,boolean special,float guardOverride,int direction){
        return hurt(target,attacker,raw,heavy,special,guardOverride,direction,false);
    }
    private boolean hurt(Fighter target,Fighter attacker,float raw,boolean heavy,boolean special,
                         float guardOverride,int direction,boolean noKnockback){
        if(over||target.hp<=0)return false;
        if(target.specialActive&&(target.skill==Skill.SPIDER||target.skill==Skill.CAPTAIN)){
            hits.add(new Hit(target,attacker,0,false,true));return false;}
        if(target.forceShield){target.forceShield=false;hits.add(new Hit(target,attacker,0,false,true));return false;}
        boolean defended=guarding(target);float amount=raw;
        if(attacker==bot&&storyBattle==2&&storyTech)amount=Math.max(raw==0?0:1,Math.round(amount*.76f));
        if(target.frozen>0)amount=(float)Math.ceil(amount*.5f);
        if(defended){
            if(target.skill==Skill.PANTHER)target.defendedAttacks=Math.min(10,target.defendedAttacks+1);
            if(target.skill==Skill.CAPTAIN){
                if(heavy||special)amount=0;
                else amount=Math.max(1,Math.round(Math.max(0,amount-2)*.25f));
            }else if(guardOverride>=0)amount=target.frozen>0?
                    (float)Math.ceil(guardOverride*.5f):guardOverride;
            else amount=raw==0?0:Math.max(1,Math.round(amount*.25f));
        }
        target.hp=Math.max(0,target.hp-amount);
        if(amount>0){
            target.quietTime=0;
            if(target.skill==Skill.THANOS&&target.specialActive){
                target.specialActive=false;target.specialTime=0;
            }
            target.stun=Math.max(target.stun,target.skill==Skill.WIDOW?.14f:
                    target.skill==Skill.SPEED?.18f:.24f);
            target.hitTime=.19f;
            if(target.skill!=Skill.THANOS&&!noKnockback){
                target.x=clamp(target.x+direction*16,EDGE,WIDTH-EDGE);followPushedAlly(target);
            }
            target.attack=-1;
        }
        hits.add(new Hit(target,attacker,amount,defended,false));
        if(target.skill==Skill.STRANGE&&!target.rewindUsed&&target.rewind==null&&target.hp<=target.maxHp*.2f){
            target.rewind=snapshot(target);
        }
        if(target.hp<=0){
            if(target.master!=null){target.knockedOut=true;return amount>0;}
            if(target.skill==Skill.STRANGE&&!target.rewindUsed&&target.rewind!=null){restore(target);return amount>0;}
            over=true;victory=root(attacker)==player;target.knockedOut=true;
        }
        return raw==0?!defended:amount>0;
    }
    private Snapshot snapshot(Fighter strange){
        Snapshot s=new Snapshot();s.player=player.copy();s.bot=bot.copy();s.time=time;
        (strange==player?s.player:s.bot).hp=strange.maxHp*.2f;
        for(Projectile p:projectiles)s.shots.add(p.copy());
        for(Effect e:effects)s.effects.add(e.copy());for(IcePatch p:ice)s.ice.add(p.copy());return s;
    }
    private void restore(Fighter strange){
        Snapshot s=strange.rewind;boolean pUsed=player.rewindUsed,bUsed=bot.rewindUsed;
        player.copyState(s.player);bot.copyState(s.bot);player.rewindUsed=pUsed;bot.rewindUsed=bUsed;
        strange.rewindUsed=true;strange.knockedOut=false;time=s.time;
        projectiles.clear();effects.clear();ice.clear();
        for(Projectile p:s.shots)projectiles.add(p.copy());for(Effect e:s.effects)effects.add(e.copy());
        for(IcePatch p:s.ice)ice.add(p.copy());
        effect(strange,Form.TIME,strange.x,strange.y+75,70,0,0,.8f);revision++;face();
    }
    private void passives(Fighter f,float dt){
        if(f.attack>=0||f.specialActive||guarding(f))f.quietTime=0;
        if(f.skill==Skill.WOLVERINE&&f.quietTime>=2){f.hp=Math.min(f.maxHp,f.hp+5);f.quietTime=0;}
        if(f.skill==Skill.DOOM){f.shieldClock-=dt;if(f.shieldClock<=0){f.forceShield=true;f.shieldClock+=7;}}
        if(f.skill==Skill.TELEKINESIS){f.objectClock-=dt;if(f.objectClock<=0){f.objectClock+=15;
            Projectile a=new Projectile(f,0,78,336,0,5,"object"),b=new Projectile(f,WIDTH,78,-336,0,5,"object");
            a.life=b.life=2.5f;projectiles.add(a);projectiles.add(b);}}
        if(f.skill==Skill.ICE&&f.y==0){f.iceClock-=dt;if(f.iceClock<=0){f.iceClock=.20f;
            IcePatch nearby=null;for(IcePatch p:ice)if(p.owner==f&&Math.abs(p.x-f.x)<20){nearby=p;break;}
            if(nearby==null)ice.add(new IcePatch(f,f.x));else nearby.life=5;
        }}
    }
    private boolean livingAlly(Fighter f){return f.ally!=null&&f.ally.hp>0;}
    private void allies(Fighter f,float dt){
        if(f.skill!=Skill.SCARLET)return;
        if(f.allyWave==0&&f.hp<=30){spawnAlly(f);f.allyWave=1;}
        if(!livingAlly(f))return;
        Fighter a=f.ally,target=opponent(f);a.dir=f.dir;
        // An airborne crossing may temporarily leave too little horizontal room for two bodies.
        // Keep the ally on the master's side of the opponent until a full gap opens again.
        a.moving=Math.abs(a.x-(f.x+f.dir*Math.min(55,Math.max(0,Math.abs(target.x-f.x)-35))))>3;
        a.x=clamp(f.x+f.dir*Math.min(55,Math.max(0,Math.abs(target.x-f.x)-35)),EDGE,WIDTH-EDGE);
        a.y=0;tick(a,dt);a.guard=false;a.specialCooldown=15;
        a.allyDecision-=dt;
        if(a.allyDecision<=0){a.allyDecision=.35f;
            if(Math.abs(a.x-target.x)<100&&canAct(a)&&a.attack<0)attack(a,0);}
        if(a.attack>=0){a.attackTime+=dt;
            if(!a.attackResolved&&a.attackTime>=.13f){a.attackResolved=true;melee(a,8,100,75,false,false);}
            if(a.attackTime>=.28f)a.attack=-1;}
    }
    private void spawnAlly(Fighter f){
        f.ally=new Fighter("Projeção do caos",null,false,f.x+f.dir*50);f.ally.master=f;
        f.ally.dir=f.dir;f.ally.hp=f.ally.maxHp=20;
    }
    private void followPushedAlly(Fighter f){
        if(f.master!=null){Fighter master=f.master;
            if((f.x-master.x)*master.dir<40)master.x=clamp(f.x-master.dir*45,EDGE,WIDTH-EDGE);}
    }
    private float aiRate(){return storyBattle==0?1.08f:storyBattle==1?1.4f:storyBattle==2?1.9f:
            difficulty==0?.72f:difficulty==2?1.65f:1;}
    private float aiSpeed(){return storyBattle==0?1.04f:storyBattle==1?1.14f:storyBattle==2?1.3f:
            difficulty==0?.82f:difficulty==2?1.2f:1;}
    private void ai(float dt){
        aiDecision-=dt;if(aiDecision>0)return;
        aiDecision=(.17f+random.nextFloat()*.22f)/aiRate();
        Fighter b=bot,p=player;float distance=Math.abs(b.x-p.x),choice=random.nextFloat();
        boolean hard=difficulty==2||storyBattle>=0,easy=difficulty==0&&storyBattle<0;
        boolean incoming=(p.attack>=0&&distance<160),unblockableIncoming=false;
        for(Projectile shot:projectiles)if(root(shot.owner)==player&&Math.abs(shot.x-b.x)<210){
            incoming=true;if(shot.unblockable)unblockableIncoming=true;
        }
        boolean telegraph=false;
        for(Effect e:effects)if(e.owner==p&&!e.resolved&&effectTouches(e,b))telegraph=true;
        if(distance>150)b.botMove=choice<(hard?.97f:easy?.72f:.88f)?b.dir:0;
        else if(distance<92)b.botMove=choice<(hard?.72f:easy?.42f:.60f)?-b.dir:0;
        else b.botMove=choice<(hard?.55f:easy?.30f:.42f)?b.dir:choice<(hard?.73f:easy?.49f:.67f)?-b.dir:0;
        if(telegraph&&hard){
            boolean vertical=false;for(Effect e:effects)if(e.owner==p&&!e.resolved&&e.form==Form.STRIKE)vertical=true;
            if(vertical)b.botMove=b.x<WIDTH/2?1:-1;else jump(b);
        }else if(unblockableIncoming){b.botMove=b.x<WIDTH/2?-1:1;jump(b);}
        else if(incoming&&random.nextFloat()<(hard?.32f:easy?.08f:.16f))jump(b);
        else if(b.y==0&&random.nextFloat()<(hard?.16f:easy?.07f:.12f))jump(b);
        if(b.skill==Skill.THOR&&b.y>0&&b.botMove!=0&&!b.airDashUsed&&
                random.nextFloat()<(hard?.46f:easy?.18f:.31f))
            airDash(b,b.botMove>0?1:-1);
        if(incoming&&!unblockableIncoming&&b.guardLock<=0&&canAct(b)&&b.attack<0&&
                random.nextFloat()<(hard?.66f:easy?.16f:.32f)){
            guard(b,true);b.guardTime=.32f;}
        if(b.skill==Skill.WOLVERINE&&b.hp<75&&distance>230&&b.quietTime>2){b.botMove=-b.dir;return;}
        if(b.skill==Skill.HULK&&incoming&&canAct(b)&&b.cooldown<=0){attack(b,2);return;}
        if(canSpecial(b)&&random.nextFloat()<(hard?.80f:easy?.20f:.45f)){
            boolean useful=b.skill==Skill.IRON||b.skill==Skill.DOOM||b.skill==Skill.THOR||b.skill==Skill.THANOS
                    ||b.skill==Skill.SCARLET||b.skill==Skill.CAPTAIN||b.skill==Skill.WIDOW||distance<240;
            if(b.skill==Skill.PANTHER)useful=b.defendedAttacks>=3&&distance<175;
            if(useful&&special(b))return;
        }
        if(canAct(b)&&b.cooldown<=0&&b.attack<0){
            if(distance<130&&random.nextFloat()<(hard?.91f:easy?.52f:.72f))attack(b,random.nextFloat()<.6f?0:1);
            else if(distance>150&&random.nextFloat()<(hard?.86f:easy?.40f:.62f))attack(b,2);
        }
    }
    static float clamp(float v,float min,float max){return Math.max(min,Math.min(max,v));}
    private static float down(float n,float dt){return Math.max(0,n-dt);}
    private static float smooth(float t){return t*t*(3-2*t);}
    private static float lerp(float a,float b,float t){return a+(b-a)*t;}
}
