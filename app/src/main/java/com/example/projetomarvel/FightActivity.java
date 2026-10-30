//lógica e animação da luta

package com.example.projetomarvel;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import com.example.projetomarvel.BattleEngine.Fighter;
import com.example.projetomarvel.BattleEngine.Projectile;

/** Native landscape arena. World dimensions and timings follow the HTML fight loop. */
public class FightActivity extends Activity {
    private static final float WORLD_W = 1100f;
    private static final float WORLD_H = 620f;
    private static final float GROUND_Y = 426f;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private BattleEngine battle;
    private ArrayList<Projectile> projectiles;
    private Button specialButton;
    private Button guardButton;
    private String lastSpecialState="";
    private final Button[] attackButtons = new Button[3];
    private Fighter playerFighter, botFighter;
    private FrameLayout stageFrame;
    private Arena arena;
    private final BattleCamera camera=new BattleCamera();
    private final AsgardScene scene=new AsgardScene();
    private BattleHud nativeHud;
    private BattleVfx nativeEffects;
    private final java.util.WeakHashMap<Fighter,FighterVisual> visualStates=new java.util.WeakHashMap<>();
    private float visualClock,accumulator,hitStop,countdownRemaining,noteRemaining;
    private String note="";
    private int blocks,visualRevision;
    private FrameLayout pauseOverlay;
    private boolean manualPaused,soundOn,lastSkillActive;
    private android.media.ToneGenerator tone;

    private TextView playerHealth, botHealth, status;
    private ProgressBar playerBar, botBar;
    private String player, bot, storyPower="Energia";
    private String worldEncounter,worldBotLabel,worldGenericKind;
    private boolean worldMode,worldGeneric;
    private int storyBattle = -1, storyColor = 0xffe94d65, botMax = 120, hits;
    private int storySkin=-1,storyBuild=1,storyHair=0,storyStyle=0,difficulty=1;
    private boolean storyTech, backwardHeld, forwardHeld, jumpRequested, jumpHeld, over, active, paused;
    private long previousFrame;
    private float effectTime, effectX, effectY, damageTime, damageX, damageY, dustTime;
    private int effectColor, damageValue;
    private boolean effectSpecial, defendedHit;
    private Runnable ticker;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        getWindow().setStatusBarColor(0xff100c13);
        getWindow().setNavigationBarColor(0xff100c13);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        player = getIntent().getStringExtra("player");
        bot = getIntent().getStringExtra("bot");
        if (player == null) player = "Homem-Aranha";
        if (bot == null) bot = "Homem de Ferro";
        worldEncounter=getIntent().getStringExtra("world_encounter");
        worldMode=worldEncounter!=null;worldGeneric=getIntent().getBooleanExtra("world_bot_generic",false);
        worldBotLabel=getIntent().getStringExtra("world_bot_label");worldGenericKind=getIntent().getStringExtra("world_generic_kind");
        storyBattle = getIntent().getIntExtra("story_battle", -1);
        storyColor = getIntent().getIntExtra("story_color", storyColor);
        storySkin = getIntent().getIntExtra("story_skin", -1);
        storyBuild = getIntent().getIntExtra("story_build", 1);
        storyHair = getIntent().getIntExtra("story_hair", 0);
        storyStyle = getIntent().getIntExtra("story_style", 0);
        storyPower = getIntent().getStringExtra("story_power");
        if(storyPower==null)storyPower="Energia";
        difficulty = Math.max(0,Math.min(2,getIntent().getIntExtra("difficulty",1)));
        storyTech = getIntent().getBooleanExtra("story_choice_tech", false);
        botMax = bot.contains("Thanos") ? 130 : 120;
        build();
        resetFight();
        countdown(3);
    }

    private int dp(float value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private TextView label(String value, int size, int color) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(color);
        view.setTypeface(Typography.barlow(this));
        return view;
    }

    private void build() {
        setContentView(R.layout.activity_fight);
        LinearLayout hud = findViewById(R.id.fight_hud);
        LinearLayout controls = findViewById(R.id.fight_controls);
        stageFrame = findViewById(R.id.fight_stage);

        hud.addView(healthColumn(player, true), new LinearLayout.LayoutParams(0, -1, 1));
        TextView vs = label("VS", 31, 0xffffffff);
        vs.setTypeface(Typeface.create(Typography.bebas(this), Typeface.ITALIC));
        vs.setGravity(Gravity.CENTER);
        vs.setShadowLayer(dp(5), 0, dp(2), 0xffff511d);
        hud.addView(vs, new LinearLayout.LayoutParams(dp(66), -1));
        hud.addView(healthColumn(worldMode&&worldBotLabel!=null?worldBotLabel:bot, false), new LinearLayout.LayoutParams(0, -1, 1));

        hud.setVisibility(View.GONE);
        nativeHud=new BattleHud(this);nativeEffects=new BattleVfx(this);
        arena = new Arena();
        stageFrame.addView(arena, new FrameLayout.LayoutParams(-1, -1));
        status = label("3", 14, 0xffffffff);
        status.setGravity(Gravity.CENTER);
        GradientDrawable statusBg = new GradientDrawable();
        statusBg.setColor(0xcc100b1b);
        statusBg.setCornerRadius(dp(18));
        statusBg.setStroke(dp(1), 0x77ffffff);
        status.setBackground(statusBg);
        FrameLayout.LayoutParams statusLp = new FrameLayout.LayoutParams(
                dp(118), dp(31), Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        statusLp.topMargin = dp(68);
        status.setVisibility(View.INVISIBLE);
        stageFrame.addView(status, statusLp);
        FrameLayout fightSurface=findViewById(R.id.fight_root);
        fightSurface.setOnApplyWindowInsetsListener((v,insets)->{
            int left=insets.getSystemWindowInsetLeft(),top=insets.getSystemWindowInsetTop(),right=insets.getSystemWindowInsetRight(),bottom=insets.getSystemWindowInsetBottom();
            if(android.os.Build.VERSION.SDK_INT>=28&&insets.getDisplayCutout()!=null){android.view.DisplayCutout cut=insets.getDisplayCutout();left=Math.max(left,cut.getSafeInsetLeft());top=Math.max(top,cut.getSafeInsetTop());right=Math.max(right,cut.getSafeInsetRight());bottom=Math.max(bottom,cut.getSafeInsetBottom());}
            v.setPadding(left,top,right,bottom);return insets;
        });
        fightSurface.requestApplyInsets();
        Button sound=toolbarButton("♪","Ativar som",86);
        sound.setOnClickListener(v->{soundOn=!soundOn;sound.setText(soundOn?"♫":"♪");sound.setContentDescription(soundOn?"Desativar som":"Ativar som");if(soundOn){if(tone==null)tone=new android.media.ToneGenerator(android.media.AudioManager.STREAM_MUSIC,25);tone.startTone(android.media.ToneGenerator.TONE_PROP_BEEP,60);}});
        Button fullscreen=toolbarButton("⛶","Tela cheia",49);
        fullscreen.setOnClickListener(v->{int flags=getWindow().getDecorView().getSystemUiVisibility();boolean full=(flags&View.SYSTEM_UI_FLAG_FULLSCREEN)!=0;getWindow().getDecorView().setSystemUiVisibility(full?View.SYSTEM_UI_FLAG_VISIBLE:View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);});
        Button pause=toolbarButton("Ⅱ","Pausar luta",12);pause.setOnClickListener(v->showPause());

        FrameLayout movement = new FrameLayout(this);
        movement.setTranslationX(dp(35));
        movement.setTranslationY(dp(6));
        controls.addView(movement, new LinearLayout.LayoutParams(dp(164), dp(149)));
        Button jump = hold("⌃\nPulo", 0xff394153,
                () -> { jumpRequested = true; jumpHeld = true; }, () -> jumpHeld = false);
        FrameLayout.LayoutParams jumpLp = new FrameLayout.LayoutParams(dp(75),dp(65),
                Gravity.TOP|Gravity.CENTER_HORIZONTAL);
        movement.addView(jump,jumpLp);
        Button backward = hold("❮\nTrás", 0xff394153,
                () -> {backwardHeld = true;if(active)battle.airDash(playerFighter,-1);},
                () -> backwardHeld = false);
        FrameLayout.LayoutParams backMoveLp = new FrameLayout.LayoutParams(dp(75),dp(65),
                Gravity.BOTTOM|Gravity.LEFT);
        movement.addView(backward,backMoveLp);
        Button forward = hold("❯\nFrente", 0xff394153,
                () -> {forwardHeld = true;if(active)battle.airDash(playerFighter,1);},
                () -> forwardHeld = false);
        FrameLayout.LayoutParams frontMoveLp = new FrameLayout.LayoutParams(dp(75),dp(65),
                Gravity.BOTTOM|Gravity.RIGHT);
        movement.addView(forward,frontMoveLp);

        controls.addView(new View(this),new LinearLayout.LayoutParams(0,1,1));
        FrameLayout attacks = new FrameLayout(this);
        attacks.setTranslationX(-dp(8));
        attacks.setAlpha(0.70f); // Transparência de 40% de opacidade
        controls.addView(attacks, new LinearLayout.LayoutParams(dp(164), dp(149)));
        Button punch=attackButton(0,"✊\nSoco",0xffffa23a);
        FrameLayout.LayoutParams punchLp=new FrameLayout.LayoutParams(dp(65),dp(65),
                Gravity.TOP|Gravity.CENTER_HORIZONTAL);
        attacks.addView(punch,punchLp);
        Button defense=hold("⬡\nDefesa",0xff23546f,
                ()->battle.guard(playerFighter,true),()->battle.guard(playerFighter,false));
        guardButton=defense;
        FrameLayout.LayoutParams defenseLp=new FrameLayout.LayoutParams(dp(65),dp(65),
                Gravity.LEFT|Gravity.CENTER_VERTICAL);
        attacks.addView(defense,defenseLp);
        Button power=attackButton(2,"✦\nPoder",0xffffd23f);
        FrameLayout.LayoutParams powerLp=new FrameLayout.LayoutParams(dp(65),dp(65),
                Gravity.RIGHT|Gravity.CENTER_VERTICAL);
        attacks.addView(power,powerLp);
        Button kick=attackButton(1,"⚑\nChute",0xffef4052);
        FrameLayout.LayoutParams kickLp=new FrameLayout.LayoutParams(dp(65),dp(65),
                Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL);
        attacks.addView(kick,kickLp);

        specialButton=action("✺\n15s",0xff7931a5);
        specialButton.setTextSize(11);
        specialButton.setContentDescription("Especial: aguarde 15 segundos");
        FrameLayout fightRoot=findViewById(R.id.fight_root);
        fightRoot.addView(specialButton,new FrameLayout.LayoutParams(dp(65),dp(65)));
        // Align with Power, above the unchanged action diamond, as in the supplied reference.
        fightRoot.addOnLayoutChangeListener((v,l,t,r,b,ol,ot,or,ob)->fightRoot.post(()->{
            int[] rootPos=new int[2],powerPos=new int[2];
            fightRoot.getLocationOnScreen(rootPos);power.getLocationOnScreen(powerPos);
            specialButton.setX(powerPos[0]-rootPos[0]);
            specialButton.setY(Math.max(dp(112),powerPos[1]-rootPos[1]-dp(110)));
        }));
        specialButton.setOnClickListener(v->{
            if(active&&battle.special(playerFighter))specialButton.animate().scaleX(.9f).scaleY(.9f)
                    .setDuration(70).withEndAction(()->specialButton.animate().scaleX(1).scaleY(1)
                            .setDuration(90).start()).start();
        });

        ticker = new Runnable() {
            @Override public void run() {
                long now = System.nanoTime();
                float dt = previousFrame == 0 ? 0 : Math.min((now - previousFrame) / 1_000_000_000f, .05f);
                previousFrame = now;
                if(!paused){
                    visualClock+=dt;nativeEffects.update(dt);effectTime=Math.max(0,effectTime-dt);damageTime=Math.max(0,damageTime-dt);dustTime=Math.max(0,dustTime-dt);noteRemaining=Math.max(0,noteRemaining-dt);
                    if(countdownRemaining>0){countdownRemaining=Math.max(0,countdownRemaining-dt);status.setText(countdownRemaining>.7f?String.valueOf(Math.max(1,(int)Math.ceil((countdownRemaining-.7f)/.7f))):"LUTEM!");if(countdownRemaining==0&&!over){active=true;status.setText("Em combate");}}
                    else if(active&&!over){
                        hitStop=Math.max(0,hitStop-dt);
                        if(hitStop==0){accumulator+=dt;int steps=0;while(accumulator>=1f/120&&steps++<8){step(1f/120);accumulator-=1f/120;if(over||hitStop>0){accumulator=0;break;}}}
                    }
                    if(battle!=null)camera.update(dt,battle.player,battle.bot);
                }
                arena.invalidate();
                updateAttackButtons();
                handler.postDelayed(this, 16);
            }
        };
        handler.post(ticker);
    }

    private LinearLayout healthColumn(String name, boolean isPlayer) {
        LinearLayout column = new LinearLayout(this);
        column.setOrientation(LinearLayout.VERTICAL);
        LinearLayout top = new LinearLayout(this);
        top.setGravity(Gravity.CENTER_VERTICAL);
        TextView role = label(isPlayer ? "JOGADOR" : "BOT", 11,
                isPlayer ? 0xff1c1207 : 0xffffffff);
        role.setTypeface(Typeface.DEFAULT_BOLD);
        role.setGravity(Gravity.CENTER);
        GradientDrawable badge = new GradientDrawable();
        badge.setColor(isPlayer ? 0xffffad20 : 0xffed2738);
        badge.setCornerRadius(dp(6));
        role.setBackground(badge);
        LinearLayout.LayoutParams roleLp = new LinearLayout.LayoutParams(
                dp(isPlayer ? 78 : 45), dp(24));
        if (isPlayer) {
            roleLp.rightMargin = dp(7);
            top.addView(role, roleLp);
        }
        TextView title = label(name.toUpperCase(), 21, 0xffffffff);
        title.setTypeface(Typography.bebas(this));
        title.setSingleLine(true);
        title.setEllipsize(android.text.TextUtils.TruncateAt.END);
        top.addView(title, new LinearLayout.LayoutParams(0, -2, 1));
        if (!isPlayer) {
            roleLp.leftMargin = dp(6);
            top.addView(role, roleLp);
        }
        int maximum=isPlayer?(player.contains("Thanos")?130:120):botMax;
        TextView number = label(String.valueOf(maximum), 16, 0xffece9ff);
        number.setTypeface(Typeface.DEFAULT_BOLD);
        top.addView(number);
        if (isPlayer) playerHealth = number; else botHealth = number;
        column.addView(top, new LinearLayout.LayoutParams(-1, dp(29)));
        ProgressBar bar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        bar.setMax(maximum);
        bar.setProgress(maximum);
        bar.setProgressTintList(ColorStateList.valueOf(0xff45dc87));
        bar.setProgressBackgroundTintList(ColorStateList.valueOf(0xff30253b));
        column.addView(bar, new LinearLayout.LayoutParams(-1, dp(17)));
        if (isPlayer) playerBar = bar; else botBar = bar;
        return column;
    }

    private Button action(String value, int color) {
        Button button = new Button(this);
        button.setText(value);
        button.setTextSize(14);
        button.setTextColor(0xffffffff);
        button.setAllCaps(false);
        button.setTypeface(Typography.barlowBold(this));
        button.setPadding(0, 0, 0, 0);
        button.setMinWidth(0);
        button.setMinHeight(0);
        GradientDrawable background = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM, new int[]{color, 0xff292c39});
        background.setCornerRadius(dp(20));
        background.setStroke(dp(1), 0x55ffffff);
        button.setBackground(background);
        return button;
    }

    private Button hold(String title, int color, Runnable down, Runnable up) {
        Button button = action(title, color);
        button.setTextSize(11);
        GradientDrawable circle = new GradientDrawable();
        circle.setShape(GradientDrawable.OVAL);
        circle.setColor((color & 0x00ffffff) | 0xc9000000);
        circle.setStroke(dp(1.5f), 0xaad8e8ff);
        button.setBackground(circle);
        button.setOnTouchListener((view, event) -> {
            int eventType = event.getActionMasked();
            if (eventType == MotionEvent.ACTION_DOWN) {
                view.setScaleX(.94f);
                view.setScaleY(.94f);
                down.run();
                return true;
            }
            if (eventType == MotionEvent.ACTION_UP || eventType == MotionEvent.ACTION_CANCEL) {
                view.setScaleX(1);
                view.setScaleY(1);
                up.run();
                return true;
            }
            return true;
        });
        return button;
    }

    private Button attackButton(int slot, String title, int color) {
        Button button = action(title, color);
        button.setTextSize(11);
        button.setTextColor(slot == 2 ? 0xff231608 : 0xffffffff);
        GradientDrawable background = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{color, slot == 0 ? 0xffff8419 : slot == 1 ? 0xffe92848 : 0xffffbe0b});
        background.setShape(GradientDrawable.OVAL);
        background.setStroke(dp(2), 0x66ffffff);
        button.setBackground(background);
        button.setOnClickListener(v -> {
            if (startAttack(playerFighter, slot)) {
                button.animate().scaleX(.88f).scaleY(.88f).setDuration(70)
                        .withEndAction(() -> button.animate().scaleX(1).scaleY(1)
                                .setDuration(90).start()).start();
            }
        });
        attackButtons[slot] = button;
        return button;
    }

    private void resetFight() {
        battle=new BattleEngine(player,bot,storyPower,(storyBattle>=0||worldMode)&&storySkin>=0,
                difficulty,storyBattle,storyTech,System.nanoTime(),worldMode&&worldGeneric);
        playerFighter=battle.player;botFighter=battle.bot;projectiles=battle.projectiles;
        backwardHeld=forwardHeld=jumpRequested=jumpHeld=over=active=false;
        hits=0;effectTime=damageTime=dustTime=0;previousFrame=0;lastSpecialState="";
        paused=manualPaused=false;visualClock=accumulator=hitStop=countdownRemaining=noteRemaining=0;blocks=0;visualRevision=0;lastSkillActive=false;note="";visualStates.clear();nativeEffects.clear();camera.reset(playerFighter,botFighter);nativeHud.reset();
        updateHealth();updateAttackButtons();arena.invalidate();
    }

    private void countdown(int n) {if(over)return;countdownRemaining=n*.7f+.7f;active=false;status.setText(String.valueOf(n));}

    private void step(float dt) {
        if(jumpRequested){battle.jump(playerFighter);jumpRequested=false;}
        battle.step(dt,(forwardHeld?1:0)-(backwardHeld?1:0),jumpHeld,true);
        if(battle.revision!=visualRevision){visualRevision=battle.revision;visualStates.clear();nativeEffects.clear();hitStop=0;note="VIAGEM NO TEMPO";noteRemaining=1.5f;camera.impact(.6);}
        for(BattleEngine.Hit hit:battle.hits){
            nativeEffects.hit(hit,storyColor);
            if(hit.damage>0){hitStop=Math.max(hitStop,hit.damage>=10?.045f:.032f);camera.impact(hit.damage>=10?1.7:.9);}
            if(hit.guarded&&hit.target==playerFighter)blocks++;
            if(soundOn&&tone!=null)tone.startTone(android.media.ToneGenerator.TONE_PROP_BEEP,35);
            effectX=damageX=hit.target.x;effectY=damageY=hit.target.y;
            effectColor=specialColor(hit.attacker==playerFighter?player:bot);
            effectTime=.42f;damageTime=hit.damage>0||hit.guarded||hit.blocked?.8f:0;
            damageValue=Math.round(hit.damage);
            effectSpecial=true;defendedHit=hit.guarded||hit.blocked;
            if((hit.attacker==playerFighter||hit.attacker.master==playerFighter)&&hit.damage>0)hits++;
            status.setText(hit.blocked?"PROTEGIDO":hit.guarded?"DEFESA":
                    playerFighter.frozen>0?"CONGELADO":Math.min(playerFighter.hp,botFighter.hp)<=30?
                    "Vida crítica":"Em combate");
        }
        battle.hits.clear();
        if(playerFighter.specialActive&&!lastSkillActive){nativeEffects.burst(playerFighter.x,playerFighter.y+100,BattleVfx.energy(playerFighter,storyColor),14,70);note=playerFighter.custom?storyPower.toUpperCase(java.util.Locale.ROOT):BattleRoster.find(BattleRoster.id(player)).special.toUpperCase(java.util.Locale.ROOT);noteRemaining=1;}
        lastSkillActive=playerFighter.specialActive;
        updateHealth();
        if(battle.over)finishFight(battle.victory);
    }

    private boolean startAttack(Fighter fighter,int slot){return active&&!over&&battle.attack(fighter,slot);}

    private void updateHealth() {
        playerHealth.setText(String.valueOf(Math.round(playerFighter.hp)));
        botHealth.setText(String.valueOf(Math.round(botFighter.hp)));
        playerBar.setMax(Math.round(playerFighter.maxHp));botBar.setMax(Math.round(botFighter.maxHp));
        playerBar.setProgress(Math.round(playerFighter.hp));botBar.setProgress(Math.round(botFighter.hp));
        playerBar.setProgressTintList(ColorStateList.valueOf(healthColor(playerFighter.hp/playerFighter.maxHp*100)));
        botBar.setProgressTintList(ColorStateList.valueOf(healthColor(botFighter.hp/botFighter.maxHp*100)));
        String accessibility=player+", "+Math.round(playerFighter.hp)+" de "+Math.round(playerFighter.maxHp)+" de vida. Adversário: "+(worldBotLabel!=null?worldBotLabel:bot)+", "+Math.round(botFighter.hp)+" de "+Math.round(botFighter.maxHp)+" de vida.";
        if(!accessibility.contentEquals(arena.getContentDescription()==null?"":arena.getContentDescription()))arena.setContentDescription(accessibility);
    }

    private int healthColor(float percent){return percent<=25?0xfff3475a:percent<=50?0xffffc632:0xff45dc87;}

    private void updateAttackButtons() {
        if(playerFighter==null)return;
        boolean ready=active&&!over&&battle.canAct(playerFighter)&&playerFighter.cooldown<=0&&playerFighter.attack<0;
        for(Button button:attackButtons)if(button!=null)button.setAlpha(ready?1:.48f);
        if(guardButton!=null){
            boolean locked=playerFighter.guardLock>0;
            String guardText=locked?"⬡\n"+(int)Math.ceil(playerFighter.guardLock)+"s":"⬡\nDefesa";
            if(!guardText.contentEquals(guardButton.getText()))guardButton.setText(guardText);
            guardButton.setAlpha(locked?.48f:1);
        }
        boolean available=active&&!over&&battle.canSpecial(playerFighter);
        boolean executing=playerFighter.specialActive;
        String text=executing?"✺\nATIVO":playerFighter.specialCooldown>0?
                "✺\n"+(int)Math.ceil(playerFighter.specialCooldown)+"s":
                playerFighter.skill==BattleEngine.Skill.STRANGE&&playerFighter.y>0?"✺\nNO CHÃO":"✺\nEspecial";
        String state=text+":"+available+":"+executing;
        if(state.equals(lastSpecialState))return;
        lastSpecialState=state;
        specialButton.setText(text);specialButton.setEnabled(available);
        specialButton.setAlpha(available||executing?1:.6f);
        GradientDrawable circle=new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{executing?0xffb654df:available?0xff9234bf:0xff4c315e,0xff382244});
        circle.setShape(GradientDrawable.OVAL);circle.setStroke(dp(1.5f),available?0xffffde92:0xffbd94cb);
        specialButton.setBackground(circle);
        specialButton.setContentDescription("Especial: "+(executing?"em execução":available?"disponível":
                "aguarde "+(int)Math.ceil(playerFighter.specialCooldown)+" segundos"));
    }

    private void finishFight(boolean victory) {
        if (over) return;
        active = false;
        over = true;
        playerFighter.knockedOut = !victory;
        botFighter.knockedOut = victory;
        if(playerFighter.dusted||botFighter.dusted)dustTime=1.1f;
        note=playerFighter.dusted||botFighter.dusted?"O ESTALO":"K.O.";noteRemaining=.9f;
        Fighter defeated=victory?botFighter:playerFighter;
        nativeEffects.burst(defeated.x,defeated.y+90,BattleVfx.energy(defeated,storyColor),30,115);
        status.setText("Fim da luta");
        if (storyBattle < 0 && !worldMode) {
            android.content.SharedPreferences record = getSharedPreferences("battle_record", 0);
            String key = victory ? "wins" : "losses";
            record.edit().putInt(key, record.getInt(key, 0) + 1).apply();
        }
        handler.postDelayed(() -> showResult(victory), 1100);
    }

    private void showResult(boolean victory) {
        if(isFinishing())return;
        FrameLayout screen=findViewById(R.id.fight_root);
        final FrameLayout[] card=new FrameLayout[1];
        Runnable retry=()->{screen.removeView(card[0]);resetFight();countdown(3);};
        String back=worldMode?"VOLTAR AO MUNDO":storyBattle>=0?(victory?"CONTINUAR HISTÓRIA":"VOLTAR À HISTÓRIA"):"VOLTAR ÀS LUTAS";
        card[0]=FightPanels.result(this,victory,victory?player:worldMode&&worldBotLabel!=null?worldBotLabel:bot,battle.time,playerFighter.hp,hits,blocks,retry,storyBattle<0&&!worldMode?this::returnToSelection:null,back,()->returnToCaller(victory));
        screen.addView(card[0],new FrameLayout.LayoutParams(-1,-1));
    }

    private Button toolbarButton(String glyph,String description,int right){
        Button b=action(glyph,0xff173247);b.setTextSize(16);b.setContentDescription(description);
        GradientDrawable bg=new GradientDrawable();bg.setColor(0xc9173247);bg.setCornerRadius(dp(4));bg.setStroke(dp(1),0x55b5bb96);b.setBackground(bg);
        FrameLayout.LayoutParams lp=new FrameLayout.LayoutParams(dp(28),dp(28),Gravity.TOP|Gravity.RIGHT);lp.setMargins(0,dp(72),dp(right),0);stageFrame.addView(b,lp);return b;
    }
    private void clearHeld(){backwardHeld=forwardHeld=jumpHeld=jumpRequested=false;if(playerFighter!=null)battle.guard(playerFighter,false);}
    private void resumeFight(){if(pauseOverlay!=null){((FrameLayout)findViewById(R.id.fight_root)).removeView(pauseOverlay);pauseOverlay=null;}manualPaused=paused=false;previousFrame=0;clearHeld();}
    private void showPause(){
        if(over||manualPaused)return;manualPaused=paused=true;clearHeld();
        FrameLayout root=findViewById(R.id.fight_root);
        pauseOverlay=FightPanels.pause(this,this::resumeFight,()->{resumeFight();resetFight();countdown(3);},storyBattle<0&&!worldMode?this::returnToSelection:null,()->returnToCaller(false));
        root.addView(pauseOverlay,new FrameLayout.LayoutParams(-1,-1));
    }
    private void returnToSelection(){Intent result=new Intent();result.putExtra("battle_select_again",true);setResult(RESULT_OK,result);finish();}



    private String specialMove(String hero) {
        if (hero.contains("Aranha")) return "Teia";
        if (hero.contains("Ferro")) return "Energia";
        if (hero.contains("Wolverine") || hero.contains("Pantera")) return "Garras";
        if (hero.contains("Capitão América")) return "Escudo";
        if (hero.equals("Thor")) return "Raio";
        if (hero.equals("Hulk")) return "Impacto";
        if (hero.contains("Escarlate")) return "Magia";
        if (hero.contains("Estranho")) return "Magia";
        if (hero.contains("Viúva")) return "Energia";
        if (hero.contains("Marvel")) return "Energia";
        if (hero.contains("Doom")) return "Energia";
        return "Rajada";
    }

    private int specialColor(String hero){
        return (storyBattle>=0||worldMode) && storySkin>=0 && hero.equals(player)
                ? storyColor : AttackEffects.color(hero);
    }

    private void returnToCaller(boolean victory) {
        if (storyBattle >= 0) {
            Intent result = new Intent();
            result.putExtra("story_battle", storyBattle);
            result.putExtra("story_victory", victory);
            setResult(RESULT_OK, result);
        }
        if(worldMode){Intent result=new Intent();result.putExtra("world_encounter",worldEncounter);
            result.putExtra("world_victory",victory);setResult(RESULT_OK,result);}
        // The parent Activity restores its own portrait or landscape orientation.
        finish();
    }

    @Override public void onBackPressed() {if(over)returnToCaller(battle.victory);else if(manualPaused)resumeFight();else showPause();}
    @Override protected void onResume() {
        super.onResume();
        paused = manualPaused;
        previousFrame = 0;
    }
    @Override protected void onPause() {
        super.onPause();
        paused = true;
        previousFrame = 0;
        backwardHeld = forwardHeld = jumpHeld = false;
        if (playerFighter != null) playerFighter.guard = false;
    }
    @Override protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        if(tone!=null)tone.release();
        super.onDestroy();
    }

    private final class Arena extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final int[] skyline = {125, 164, 102, 141, 85, 154, 113, 177, 107, 148, 120, 169};
        Arena() { super(FightActivity.this); }

        @Override protected void onDraw(Canvas actual) {
            super.onDraw(actual);if(battle==null)return;
            actual.drawColor(0xff142e46);actual.save();
            float scale=getWidth()/WORLD_W;
            actual.translate(0,(getHeight()-WORLD_H*scale)*.58f);actual.scale(scale,scale);
            VectorCanvas c=new VectorCanvas(actual);scene.background(c,camera,visualClock);scene.world(c,camera,visualClock);
            actual.save();camera.apply(actual);actual.translate(0,-GROUND_Y);
            BattleVfx.abilities(actual,battle,GROUND_Y,visualClock,storyColor);drawProjectiles(actual);
            drawFighter(actual,playerFighter,player,storySkin>=0?storyColor:-1);
            drawFighter(actual,botFighter,bot,-1);
            for(Fighter owner:new Fighter[]{playerFighter,botFighter})if(owner.ally!=null&&owner.ally.hp>0){
                drawFighter(actual,owner.ally,owner.ally.name,owner.custom?storyColor:-1);
                paint.setColor(0xff282035);actual.drawRoundRect(owner.ally.x-17,GROUND_Y-owner.ally.y-157,owner.ally.x+17,GROUND_Y-owner.ally.y-154,1,1,paint);
                paint.setColor(0xffef6f96);actual.drawRect(owner.ally.x-17,GROUND_Y-owner.ally.y-157,owner.ally.x-17+34*owner.ally.hp/owner.ally.maxHp,GROUND_Y-owner.ally.y-154,paint);
            }
            nativeEffects.draw(actual,GROUND_Y);actual.restore();actual.restore();
            String announcement=countdownRemaining>0?status.getText().toString():noteRemaining>0?note:"";
            nativeHud.draw(actual,getWidth(),getHeight(),battle,camera,player,worldMode&&worldBotLabel!=null?worldBotLabel:bot,announcement,.016);
        }

        private void drawFighter(Canvas canvas, Fighter fighter, String hero,
                                 int overridePrimary) {
            FighterVisual visual=visualStates.get(fighter);
            if(visual==null){
                visual=new FighterVisual(hero);
                if(fighter.custom||fighter.master!=null&&fighter.master.custom)visual.configure(storyColor,storySkin,storyBuild,storyHair,storyStyle,storyPower);
                else if(fighter.skill==BattleEngine.Skill.GENERIC)visual.generic(worldGenericKind);
                else if(visual.id.equals("doombot"))visual.generic("doombot");
                visualStates.put(fighter,visual);
            }
            visual.sync(fighter,visualClock);visual.victory=over&&!fighter.knockedOut;
            visual.alpha=fighter.master!=null?.55:fighter.specialActive&&fighter.skill==BattleEngine.Skill.NINJA?.4:1;
            visual.shieldAway=false;visual.hammerAway=false;
            for(Projectile shot:projectiles)if(shot.owner==fighter){if(shot.boomerang)visual.shieldAway=true;if(shot.thorHammer)visual.hammerAway=true;}
            NativeFighterArt.draw(canvas,visual,fighter.x,GROUND_Y-fighter.y,.72,visualClock);
            if(fighter.dusted&&dustTime>0){
                float spread=1-dustTime/1.1f;
                paint.setColor(0xffc9b594);paint.setAlpha(Math.round(dustTime/1.1f*210));
                for(int i=0;i<18;i++){
                    float dx=((i*37)%71-35)*spread;
                    float dy=((i*19)%130)-120-spread*45;
                    canvas.drawCircle(fighter.x+dx,GROUND_Y+dy,2+i%3,paint);
                }
                paint.setAlpha(255);
            }
            BattleVfx.status(canvas,fighter,GROUND_Y,visualClock,storyColor);
        }

        private void drawProjectiles(Canvas canvas) {if(projectiles!=null)for(Projectile shot:projectiles)BattleVfx.projectile(canvas,shot,GROUND_Y,visualClock,storyColor);}

        private void drawImpact(Canvas canvas) {
            if (effectTime > 0) {
                float progress = 1f - effectTime / .42f;
                float x = effectX, y = GROUND_Y-88-effectY;
                paint.setColor(effectColor);
                paint.setAlpha((int) (220 * (1f - progress)));
                paint.setStrokeWidth(effectSpecial ? 5f : 3f);
                paint.setStrokeCap(Paint.Cap.ROUND);
                for (int i = 0; i < 8; i++) {
                    double angle = i * Math.PI / 4d;
                    float inner = 8 + progress * 8;
                    float outer = 23 + progress * 23;
                    canvas.drawLine(x + (float) Math.cos(angle) * inner,
                            y + (float) Math.sin(angle) * inner,
                            x + (float) Math.cos(angle) * outer,
                            y + (float) Math.sin(angle) * outer, paint);
                }
                paint.setAlpha(255);
            }
            if (damageTime > 0) {
                float progress = 1f - damageTime / .8f;
                paint.setColor(defendedHit ? 0xff8cefff : 0xffffe36a);
                paint.setAlpha((int) (255 * (1f - progress)));
                paint.setTypeface(Typography.bebas(FightActivity.this));
                paint.setTextSize(29);
                String text = defendedHit ? "DEFESA" : "-" + damageValue;
                canvas.drawText(text, damageX - paint.measureText(text) / 2f,
                        GROUND_Y-145 - damageY - progress * 28f, paint);
                paint.setAlpha(255);
            }
        }
    }
}
