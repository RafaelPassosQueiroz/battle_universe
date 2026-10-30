"""Read-only scope checks against the pre-layout branch revision."""
from pathlib import Path
import subprocess,re,xml.etree.ElementTree as ET
root=Path(__file__).resolve().parents[1]
base='efd18021884f5021b7c67dd075b0ca30457c4dd3'
java='app/src/main/java/com/example/projetomarvel/'
def old(file):return subprocess.check_output(['git','show',base+':'+file],cwd=root).decode('utf-8').replace('\r\n','\n')
def new(file):return (root/file).read_text(encoding='utf-8')
for file in ['StoryMode.java','HomeReference.java','WorldArt.java','WorldCanvas.java','WorldMap.java','WorldRules.java','WorldJson.java','WorldValues.java','WorldBattleBridge.java','QuizGenerator.java','GeminiClient.java','ComicVineClient.java']:
    assert old(java+file)==new(java+file),'Protected file changed: '+file
for file in ['app/src/main/AndroidManifest.xml','app/src/main/res/layout/activity_main.xml']:
    assert old(file)==new(file),'Protected navigation/orientation changed: '+file
def replace_method(source,marker,value):
    start=source.index(marker);i=source.index('{',start)+1;depth=1;quote=None;line=False;comment=False
    while depth:
        ch=source[i];n=source[i+1:i+2]
        if line:
            if ch=='\n':line=False
        elif comment:
            if ch=='*' and n=='/':comment=False;i+=1
        elif quote:
            if ch==quote and source[i-1]!='\\':quote=None
        elif ch=='/' and n=='/':line=True;i+=1
        elif ch=='/' and n=='*':comment=True;i+=1
        elif ch in "'\"":quote=ch
        elif ch=='{':depth+=1
        elif ch=='}':depth-=1
        i+=1
    return source[:start]+value+source[i:]
a,b=old(java+'MainActivity.java'),new(java+'MainActivity.java')
for marker in ['    void fights()','    void pickScreen()','    void preFight()','    @Override protected void onActivityResult(']:
    a=replace_method(a,marker,'/* battle-specific change */');b=replace_method(b,marker,'/* battle-specific change */')
assert a==b,'Home/Jarvis/Quiz/five-tab navigation changed outside allowed methods'
a,b=old(java+'FightActivity.java'),new(java+'FightActivity.java')
def controls(source):return source[source.index('        FrameLayout movement ='):source.index('        ticker = new Runnable()')]
assert controls(a)==controls(b),'Existing mobile controls changed'
before=ET.fromstring(old('app/src/main/res/layout/activity_fight.xml'));after=ET.fromstring(new('app/src/main/res/layout/activity_fight.xml'))
ns='{http://schemas.android.com/apk/res/android}'
before=next(e for e in before if e.get(ns+'id')=='@+id/fight_controls');after=next(e for e in after if e.get(ns+'id')=='@+id/fight_controls')
assert before.attrib==after.attrib,'Control container geometry changed'
a=old(java+'BattleEngine.java').replace('existing fixed arena','shared 2,600-unit scrolling arena').replace('static final float WIDTH=844, EDGE=55;','static final float WIDTH=2600, EDGE=55;\n    static final float RETURNING_POWER_RANGE=844f/3f-48;').replace('custom,220);bot=new Fighter(botName,null,false,624,genericBot)','custom,1100);bot=new Fighter(botName,null,false,1500,genericBot)').replace('p.maxDistance=WIDTH/3-48','p.maxDistance=RETURNING_POWER_RANGE')
assert a==new(java+'BattleEngine.java'),'Combat rules changed beyond world dimensions and spawn points'
a=old(java+'VisitWorldActivity.java').replace('SpriteView icon=new SpriteView(h,"down");option.addView(icon,new LinearLayout.LayoutParams(d(30),d(34)));','FighterArtView icon=new FighterArtView(VisitWorldActivity.this,str(get(h,"name")),false);icon.setAnimated(false);option.addView(icon,new LinearLayout.LayoutParams(d(50),d(58)));').replace('LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,d(72),1);lp.setMargins(0,0,d(5),d(6));line.addView(option,lp);','LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,d(92),1);lp.setMargins(0,0,d(5),d(6));line.addView(option,lp);')
assert a==new(java+'VisitWorldActivity.java'),'Pixel exploration or encounter presentation changed'
assert 'COMO JOGAR' not in new(java+'BattleScreens.java')
assert 'characterLockedBy' not in new(java+'BattleScreens.java'),'All thirteen must be available in standalone fights'
for file in ['NativeFighterArt.java','FighterArtView.java','BattleScreens.java','AsgardScene.java','BattleVfx.java']:
    s=new(java+file)
    assert 'WebView' not in s and 'drawBitmap' not in s
print('PASS: original control block/container, combat rules, Home/Jarvis/Quiz, five tabs, Story and pixel exploration preserved; no WebView or bitmap fighters.')
