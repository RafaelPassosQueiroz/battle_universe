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
    private static final float WORLD_W = 844f;
    private static final float WORLD_H = 380f;
    private static final float GROUND_Y = 270f;

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
        stageFrame.addView(status, statusLp);
        Button exit = action("Sair", 0xff100c18);
        exit.setTextSize(12);
        GradientDrawable exitBg = new GradientDrawable();
        exitBg.setColor(0xb8100c18);
        exitBg.setCornerRadius(dp(12));
        exitBg.setStroke(dp(1), 0x88ffffff);
        exit.setBackground(exitBg);
        FrameLayout.LayoutParams exitLp = new FrameLayout.LayoutParams(
                dp(58), dp(32), Gravity.TOP | Gravity.RIGHT);
        exitLp.setMargins(0, dp(72), dp(12), 0);
        stageFrame.addView(exit, exitLp);
        exit.setOnClickListener(v -> returnToCaller(false));

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
                if (active && !over && !paused) step(dt);
                else if (!paused) {
                    effectTime = Math.max(0, effectTime - dt);
                    damageTime = Math.max(0, damageTime - dt);
                    dustTime = Math.max(0, dustTime - dt);
                    if (playerFighter != null)
                        playerFighter.hitTime = Math.max(0, playerFighter.hitTime - dt);
                    if (botFighter != null)
                        botFighter.hitTime = Math.max(0, botFighter.hitTime - dt);
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
        updateHealth();updateAttackButtons();arena.invalidate();
    }

    private void countdown(int n) {
        if(over)return;
        if(n>0){status.setText(String.valueOf(n));handler.postDelayed(()->countdown(n-1),700);}
        else{status.setText("LUTE!");handler.postDelayed(()->{
            if(!over){active=true;previousFrame=0;status.setText("Em combate");}
        },650);}
    }

    private void step(float dt) {
        effectTime=Math.max(0,effectTime-dt);damageTime=Math.max(0,damageTime-dt);
        if(jumpRequested){battle.jump(playerFighter);jumpRequested=false;}
        battle.step(dt,(forwardHeld?1:0)-(backwardHeld?1:0),jumpHeld,true);
        for(BattleEngine.Hit hit:battle.hits){
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
        battle.hits.clear();updateHealth();
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
        status.setText("Fim da luta");
        if (storyBattle < 0 && !worldMode) {
            android.content.SharedPreferences record = getSharedPreferences("battle_record", 0);
            String key = victory ? "wins" : "losses";
            record.edit().putInt(key, record.getInt(key, 0) + 1).apply();
        }
        handler.postDelayed(() -> showResult(victory), 1100);
    }

    private void showResult(boolean victory) {
        if (isFinishing()) return;
        FrameLayout overlay = new FrameLayout(this);
        overlay.setBackground(new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{0xf5090712,0xe21e111b,0xc3422618}));
        LinearLayout composition = new LinearLayout(this);
        composition.setGravity(Gravity.CENTER_VERTICAL);
        composition.setPadding(dp(26),dp(15),dp(22),dp(15));
        overlay.addView(composition,new FrameLayout.LayoutParams(-1,-1));
        LinearLayout copy = new LinearLayout(this);
        copy.setOrientation(LinearLayout.VERTICAL);
        copy.setGravity(Gravity.CENTER_VERTICAL);
        composition.addView(copy,new LinearLayout.LayoutParams(0,-1,1.45f));
        TextView title = label(victory ? "VITÓRIA" : "DERROTA", 66,
                victory ? 0xffffca4d : 0xffff526c);
        title.setTypeface(Typeface.create(Typography.bebas(this), Typeface.ITALIC));
        title.setShadowLayer(dp(12),0,dp(4),victory?0x99ff8718:0x99ef264a);
        copy.addView(title);
        TextView description=label(victory ? player+" venceu "+bot+"."
                : bot+" venceu "+player+".",19,0xffffffff);
        copy.addView(description);
        LinearLayout stats=new LinearLayout(this);
        stats.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams statsLp=new LinearLayout.LayoutParams(-1,-2);
        statsLp.topMargin=dp(12);
        statsLp.bottomMargin=dp(20);
        copy.addView(stats,statsLp);
        TextView hp=label(Math.round(playerFighter.hp)+" HP",29,0xffffffff);
        hp.setTypeface(Typography.bebas(this));
        stats.addView(hp);
        TextView hpLabel=label("  vida restante      ",13,0xffd4c8c2);
        stats.addView(hpLabel);
        TextView count=label(String.valueOf(hits),29,0xffffffff);
        count.setTypeface(Typography.bebas(this));
        stats.addView(count);
        stats.addView(label("  golpes certeiros",13,0xffd4c8c2));
        LinearLayout row = new LinearLayout(this);
        Button retry = action(victory ? "Lutar novamente" : "Tentar novamente", 0xffa64627);
        Button back = action(worldMode?"Voltar ao mundo":storyBattle >= 0
                ? (victory ? "Continuar história" : "Voltar à história")
                : "Voltar às lutas", 0xff34303a);
        GradientDrawable orange=new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{0xffffaa20,0xffff711e});
        orange.setCornerRadius(dp(16));
        retry.setBackground(orange);
        retry.setTextColor(0xff241106);
        LinearLayout.LayoutParams retryLp=new LinearLayout.LayoutParams(0,dp(54),1);
        row.addView(retry,retryLp);
        GradientDrawable outline=new GradientDrawable();
        outline.setColor(0x55252027);
        outline.setCornerRadius(dp(16));
        outline.setStroke(dp(1),0x99ffffff);
        back.setBackground(outline);
        LinearLayout.LayoutParams backLp=new LinearLayout.LayoutParams(0,dp(54),1);
        backLp.leftMargin=dp(10);
        row.addView(back,backLp);
        copy.addView(row,new LinearLayout.LayoutParams(-1,-2));
        FighterArtView figure=(storyBattle>=0||worldMode&&storySkin>=0)
                ?new FighterArtView(this,player,false,storyColor,storySkin,storyBuild,storyHair,storyStyle)
                :new FighterArtView(this,player,false);
        if(storySkin>=0)figure.setPower(storyPower);
        figure.setPose(victory?FighterArtView.POSE_VICTORY:FighterArtView.POSE_KO);
        composition.addView(figure,new LinearLayout.LayoutParams(0,-1,.8f));
        FrameLayout screen=(FrameLayout)findViewById(android.R.id.content);
        screen.addView(overlay,new FrameLayout.LayoutParams(-1,-1));
        retry.setOnClickListener(v -> {
            screen.removeView(overlay);
            resetFight();
            countdown(3);
        });
        back.setOnClickListener(v -> returnToCaller(victory));
    }

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
        else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        finish();
    }

    @Override public void onBackPressed() { returnToCaller(false); }
    @Override protected void onResume() {
        super.onResume();
        paused = false;
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
        super.onDestroy();
    }

    private final class Arena extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final int[] skyline = {125, 164, 102, 141, 85, 154, 113, 177, 107, 148, 120, 169};
        Arena() { super(FightActivity.this); }

        @Override protected void onDraw(Canvas actual) {
            super.onDraw(actual);
            actual.save();
            float scale=Math.min(getWidth()/WORLD_W,getHeight()/WORLD_H);
            actual.translate((getWidth()-WORLD_W*scale)/2f,
                    (getHeight()-WORLD_H*scale)/2f);
            actual.scale(scale,scale);
            if(battle!=null&&battle.shake>0)
                actual.translate((float)Math.sin(battle.time*120)*2.8f*battle.shake/.18f,0);
            drawBackdrop(actual);
            CombatEffects.drawWorld(actual,battle,GROUND_Y,storyColor,paint);
            drawProjectiles(actual);
            drawFighter(actual, playerFighter, player,
                    storyBattle >= 0 || worldMode&&storySkin>=0 ? storyColor : -1);
            drawFighter(actual, botFighter, bot, -1);
            for(Fighter owner:new Fighter[]{playerFighter,botFighter})if(owner.ally!=null&&owner.ally.hp>0)
                CombatEffects.drawAlly(actual,owner.ally,GROUND_Y,paint);
            drawImpact(actual);
            actual.restore();
        }

        private void drawBackdrop(Canvas canvas) {
            int[] colors = storyBattle == 2
                    ? new int[]{0xff101a22, 0xff254b3c, 0xff07110d}
                    : storyBattle >= 0
                    ? new int[]{0xff101a36, 0xff5b3a67, 0xffc78598}
                    : new int[]{0xff1a1030, 0xff6b2942, 0xfff0a03c};
            paint.setShader(new LinearGradient(0, 0, 0, WORLD_H, colors,
                    null, Shader.TileMode.CLAMP));
            canvas.drawRect(0, 0, WORLD_W, WORLD_H, paint);
            int glow=storyBattle==2?0x6679ed91:0xb7ffa353;
            paint.setShader(new RadialGradient(WORLD_W*.5f, GROUND_Y+18f, 190,
                    glow, 0x00ffa353, Shader.TileMode.CLAMP));
            canvas.drawRect(0, 45, WORLD_W, GROUND_Y, paint);
            paint.setShader(null);
            paint.setColor(storyBattle==2?0x553b7050:0x664a2447);
            for(int i=0;i<7;i++){
                float cx=55+i*129,cy=103+(i%3)*27;
                canvas.drawRoundRect(cx-34,cy,cx+44,cy+7,7,7,paint);
                canvas.drawRoundRect(cx-6,cy-5,cx+20,cy+4,6,6,paint);
            }
            float width = WORLD_W / skyline.length;
            paint.setColor(storyBattle==2?0xff183126:0xff2a1638);
            for (int i = 0; i < skyline.length; i++) {
                canvas.drawRect(i * width, GROUND_Y - skyline[i] * .63f,
                        (i + 1) * width + 1, GROUND_Y, paint);
            }
            paint.setColor(storyBattle==2?0xff0b2118:0xff180e24);
            for (int i = 0; i < skyline.length; i += 2) {
                canvas.drawRect(i * width + width * .35f,
                        GROUND_Y - skyline[(i + 3) % skyline.length] * .43f,
                        (i + 1) * width + width * .65f, GROUND_Y, paint);
            }
            Path tower = new Path();
            tower.moveTo(615, GROUND_Y);
            tower.lineTo(615, 168);
            tower.lineTo(630, 168);
            tower.lineTo(650, 79);
            tower.lineTo(670, 168);
            tower.lineTo(685, 168);
            tower.lineTo(685, GROUND_Y);
            tower.close();
            canvas.drawPath(tower, paint);
            paint.setShader(new LinearGradient(0,GROUND_Y,0,WORLD_H,
                    storyBattle==2?0xff263a2a:0xff352238,
                    storyBattle==2?0xff08130d:0xff110b1b,Shader.TileMode.CLAMP));
            canvas.drawRect(0,GROUND_Y,WORLD_W,WORLD_H,paint);
            paint.setShader(null);
            paint.setColor(storyBattle==2?0x667dba80:0x665d4059);
            paint.setStrokeWidth(1);
            for(int y=(int)GROUND_Y+20;y<WORLD_H;y+=25)
                canvas.drawLine(0,y,WORLD_W,y,paint);
            for(int row=0;row<4;row++)for(int col=0;col<12;col++){
                float sx=col*76+(row%2)*31,sy=GROUND_Y+9+row*27;
                canvas.drawRect(sx,sy,sx+23+(col%3)*7,sy+3,paint);
            }
            paint.setColor(storyBattle == 2 ? 0xff7bfd8a : 0xffffac21);
            canvas.drawRect(0, GROUND_Y, WORLD_W, GROUND_Y+3, paint);
        }

        private void drawFighter(Canvas canvas, Fighter fighter, String hero,
                                 int overridePrimary) {
            boolean mirrored=fighter.dir<0;
            boolean scripted=fighter.specialActive&&fighter.skill!=BattleEngine.Skill.CAPTAIN
                    &&fighter.skill!=BattleEngine.Skill.SCARLET
                    &&fighter.skill!=BattleEngine.Skill.STRENGTH;
            int pose=fighter.knockedOut?FighterArtView.POSE_KO:
                    scripted?CombatEffects.specialPose(fighter):
                    fighter.frozen>0?FighterArtView.POSE_GUARD:
                    fighter.hitTime>0?FighterArtView.POSE_HIT:
                    fighter.attack>=0?fighter.attack==0?FighterArtView.POSE_PUNCH:
                    fighter.attack==1?FighterArtView.POSE_KICK:FighterArtView.POSE_BLAST:
                    battle.guarding(fighter)||(fighter.specialActive&&fighter.skill==BattleEngine.Skill.CAPTAIN)?FighterArtView.POSE_GUARD:
                    fighter.y>0?FighterArtView.POSE_JUMP:
                    fighter.moving?FighterArtView.POSE_WALK:FighterArtView.POSE_IDLE;
            float phase=fighter.attack>=0?fighter.attackTime/fighter.attackDuration:
                    fighter.specialActive?Math.min(1,fighter.specialTime/Math.min(1,fighter.specialDuration)):fighter.walkTime;
            int dustAlpha=fighter.dusted?Math.round(255*Math.max(0,Math.min(1,(dustTime-.15f)/.95f))):255;
            int layer=canvas.saveLayerAlpha(0,0,WORLD_W,WORLD_H,
                    fighter.specialActive&&fighter.skill==BattleEngine.Skill.NINJA?100:
                            fighter.specialActive&&fighter.skill==BattleEngine.Skill.MARVEL?90:dustAlpha);
            float artScale=fighter.custom?.72f:FighterArtView.visualScale(hero);
            if(fighter.skill==BattleEngine.Skill.GENERIC)WorldEnemyArt.draw(canvas,fighter.x,GROUND_Y-fighter.y,mirrored,worldGenericKind,pose,phase);
            else if(fighter.custom)FighterArtView.drawPowered(canvas,fighter.x,GROUND_Y-fighter.y,
                    artScale,hero,mirrored,overridePrimary,pose,phase,
                    storySkin,storyBuild,storyHair,storyStyle,storyPower);
            else FighterArtView.drawAnimated(canvas,fighter.x,GROUND_Y-fighter.y,
                    artScale,hero,mirrored,overridePrimary,pose,phase);
            canvas.restoreToCount(layer);
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
            CombatEffects.drawStatus(canvas,battle,fighter,GROUND_Y,specialColor(hero),paint);
        }

        private void drawProjectiles(Canvas canvas) {
            if(projectiles==null)return;
            for(Projectile projectile:projectiles){
                if(!projectile.style.equals("common"))CombatEffects.drawProjectile(canvas,projectile,GROUND_Y,paint);
                else if(projectile.owner.custom)AttackEffects.drawCustom(canvas,projectile.x,GROUND_Y-projectile.y,
                        projectile.direction,storyPower,storyColor,paint);
                else AttackEffects.draw(canvas,projectile.x,GROUND_Y-projectile.y,
                        projectile.direction,projectile.owner.name,paint);
            }
        }

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
                        125 - damageY - progress * 28f, paint);
                paint.setAlpha(255);
            }
        }
    }
}
