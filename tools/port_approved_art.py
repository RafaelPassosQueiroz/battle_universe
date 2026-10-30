"""Development-only vector transcription. Emits Java, never ships a JS runtime."""
from pathlib import Path
import re,json,sys
web=Path(sys.argv[1]) if len(sys.argv)>1 else Path(r'C:\Users\rafaelqueiroz-ieg\OneDrive - Instituto J&F\Área de Trabalho\Projetos_Rafa\marvel\version3')
dest=Path(__file__).resolve().parents[1]/'app/src/main/java/com/example/projetomarvel'
source=(web/'js/duel-art.js').read_text(encoding='utf-8')

def body(src,marker):
    start=src.index(marker);brace=src.index('{',start);depth=1;i=brace+1
    quote=None
    while depth:
        ch=src[i]
        if quote:
            if ch==quote and src[i-1]!='\\':quote=None
        elif ch in "'\"":quote=ch
        elif ch=='{':depth+=1
        elif ch=='}':depth-=1
        i+=1
    return src[brace+1:i-1]

def convert(s):
    s=re.sub(r'//[^\n]*','',s)
    s=re.sub(r"'([^'\n]*)'",lambda m:json.dumps(m.group(1)),s)
    s=re.sub(r'\bid(!==|===)"([^"]+)"',lambda m:('!' if m.group(1)=='!==' else '')+'id.equals("'+m.group(2)+'")',s)
    s=re.sub(r'for\(const (\w+) of (\[[^\[\]]+\])\)',lambda m:'for(double '+m.group(1)+':new double[]{'+m.group(2)[1:-1]+'})',s)
    s=re.sub(r'\[\[([^;]*?)\]\]',lambda m:'new double[][]{{'+re.sub(r'\],\s*\[','},{',m.group(1))+'}}',s)
    s=s.replace('body.','Body.')
    s=s.replace('f.attack?.kind==="power"','f.attack==2').replace('f.attack||f.specialActive','f.attack>=0||f.specialActive').replace('(f.clock||0)','f.clock').replace('f.clock||0','f.clock')
    s=s.replace('palette.glove||palette.main','(palette.glove!=null?palette.glove:palette.main)').replace('palette.boot||palette.main','(palette.boot!=null?palette.boot:palette.main)').replace('palette.face||palette.main','(palette.face!=null?palette.face:palette.main)').replace('palette.face?palette.face:palette.shadow','palette.face!=null?palette.face:palette.shadow')
    s=s.replace('if(palette.hair&&','if(palette.hair!=null&&')
    s=s.replace('if(palette.hair!=null&& !', 'if(palette.hair!=null&& !')
    s=s.replace('if(palette.hair!=null&&!id.equals("hulk")&&!id.equals("strange"))', 'if(palette.hair!=null&&!palette.shortHair&&!id.equals("hulk")&&!id.equals("strange"))')
    s=s.replace('if(id.equals("strange")){\n        A.path', 'if(id.equals("strange")||palette.shortHair){\n        A.path')
    s=s.replace('const glove=','String glove=').replace('const pants=','String pants=').replace('const boot=','String boot=').replace('const sway=','double sway=')
    s=s.replace('const gems=[','String[] gems={').replace('"#f5a564"];','"#f5a564"};')
    s=re.sub(r'gems.forEach\(\(color,i\)=>A.ellipse\(c,([^;]+)\)\);',lambda m:'for(int i=0;i<gems.length;i++)A.ellipse(c,'+m.group(1).replace('color','gems[i]')+');',s)
    s=s.replace('for(let ','for(int ').replace('for (let ','for (int ')
    s=s.replace('const ','double ').replace('let ','double ')
    s=re.sub(r"(?<=\))(?=\})", ";",s)
    return s

paletteblock=body(source,'const palettes =')
paletteRows=re.findall(r'(\w+):\s*\{([^{}]+)\}',paletteblock)
fields=set()
paletteinit=[]
for id,values in paletteRows:
    vals=re.findall(r"(\w+):('(?:[^']*)'|true|false)",values)
    fields.update(k for k,v in vals if k!='bareArms')
    assignments=[]
    for k,v in vals:assignments.append('p.'+k+'='+('true' if v=='true' else 'false' if v=='false' else json.dumps(v[1:-1]))+';')
    paletteinit.append('        { Palette p=new Palette();'+''.join(assignments)+'PALETTES.put("'+id+'",p); }')
bodyconst=re.search(r'const body = Object.freeze\(\{(.*?)\}\)',source,re.S).group(1)
bodyfields=re.findall(r'(\w+):\s*([\d.\-]+)',bodyconst)
headers='''package com.example.projetomarvel;
import android.graphics.Canvas;
import java.util.HashMap;
import static com.example.projetomarvel.VectorCanvas.clamp;
/** Native transcription of the final V3 rig, costume paths, pivots and palette. */
final class NativeFighterArt {
    private static final VectorCanvas.Art A=new VectorCanvas.Art();
    static final class Body { BODY_FIELDS }
    static final class Palette {
        String PALETTE_FIELDS;
        boolean bareArms,shortHair;
        Palette copy(){Palette p=new Palette();COPY_FIELDS p.bareArms=bareArms;p.shortHair=shortHair;return p;}
    }
    static final HashMap<String,Palette> PALETTES=new HashMap<>();
    static { PALETTE_INIT }
    private static void panel(VectorCanvas c,double x,double y,double w,double h,double r,String color){panel(c,x,y,w,h,r,color,"#14243a");}
    private static void panel(VectorCanvas c,double x,double y,double w,double h,double r,String color,String outline){A.round(c,x,y,w,h,r,color);if(outline!=null){c.strokeStyle=outline;c.lineWidth=1.9;c.stroke();}}
'''.replace('BODY_FIELDS',' '.join('static final double '+k+'='+v+';' for k,v in bodyfields)).replace('PALETTE_FIELDS',','.join(sorted(fields))).replace('COPY_FIELDS',''.join('p.'+k+'='+k+';' for k in sorted(fields))).replace('PALETTE_INIT','\n'.join(paletteinit))
sigs={
'limb':'VectorCanvas c,double length,double width,String color,String shadow',
'shield':'VectorCanvas c,double t',
'hand':'VectorCanvas c,Palette palette,String id,boolean front,FighterVisual f',
'arm':'VectorCanvas c,Palette palette,String id,double x,double y,double upper,double elbow,boolean front,FighterVisual f',
'leg':'VectorCanvas c,Palette palette,String id,double x,double y,double upper,double knee,double foot',
'torso':'VectorCanvas c,Palette palette,String id',
'head':'VectorCanvas c,Palette palette,String id,FighterPose p,double t',
'cape':'VectorCanvas c,Palette palette,FighterPose p,double t'}
out=headers
for name,sig in sigs.items():out+='\n    private static void '+name+'('+sig+') {\n'+convert(body(source,'function '+name+'('))+'\n    }\n'
out+='''
    static void draw(Canvas actual,FighterVisual f,double x,double y,double scale,double t){
        VectorCanvas c=new VectorCanvas(actual);String id=f.id;f.clock=t;
        Palette palette=f.palette!=null?f.palette:PALETTES.get(id);if(palette==null)return;
        FighterPose target=FighterPose.sample(f,t);
        double dt=clamp(t-f.visualTime,0,.05);f.visualTime=t;
        if(f.pose==null)f.pose=target;else f.pose.approach(target,dt);
        FighterPose p=f.pose;
        double wide=f.width(),tall=f.height();
        A.ellipse(c,x,y+3,30*scale*wide,5.5*scale,"#06162566");
        c.save();c.translate(x,y);c.scale(scale*f.dir*wide,scale*tall);c.globalAlpha=f.alpha;
        if(f.dusted)c.globalAlpha*=1-smooth(f.koAge/1.3);
        c.translate(0,p.bob);c.rotate(p.turn);c.translate(0,-74);c.rotate(p.lean);c.scale(1,p.stretch);c.translate(0,74);
        if(palette.cape!=null)cape(c,palette,p,t);
        c.save();c.globalAlpha*=.86;
        arm(c,palette,id,-Body.shoulderX,Body.shoulderY,p.ba,p.be,false,f);
        leg(c,palette,id,-Body.hipX,Body.hipY,p.bl,p.bk,0);c.restore();
        leg(c,palette,id,Body.hipX,Body.hipY,p.fl,p.fk,p.foot);
        torso(c,palette,id);
        if(f.custom)customCostume(c,f,palette);
        head(c,palette,id,p,t);
        if(f.robot){c.save();c.translate(0,Body.headPivotY);c.rotate(p.head);A.line(c,new double[][]{{-11,7},{11,7}},palette.deep,1.5);A.line(c,new double[][]{{-8,11},{8,11}},palette.shadow,1.4);for(double side:new double[]{-1,1})A.ellipse(c,side*14,10,1.2,1.2,palette.deep);c.restore();}
        arm(c,palette,id,Body.shoulderX,Body.shoulderY,p.fa,p.fe,true,f);
        if(palette.cape!=null){A.ellipse(c,-17,-134,3.5,3.5,id.equals("doom")?"#dfbf68":palette.accent);A.ellipse(c,17,-134,3.5,3.5,id.equals("doom")?"#dfbf68":palette.accent);}
        if(f.hit>.12){c.globalAlpha=.16;A.round(c,-19,-134,38,61,8,"#fff2ce");}
        c.restore();
        if(f.shield)A.ring(c,x,y-90*scale,66*scale,palette.energy+"88",t*.5);
        if(f.distortion>0){c.save();c.globalAlpha=.6;A.ring(c,x,y-90*scale,49*scale,"#f66294",t*2);c.restore();}
        if(f.electric>0)A.line(c,new double[][]{{x-18*scale,y-137*scale},{x+8*scale,y-110*scale},{x-5*scale,y-97*scale},{x+23*scale,y-65*scale}},"#b7f3ff",1.5);
        if(f.guard){c.save();c.globalAlpha=.48;A.line(c,new double[][]{{x+f.dir*38*scale,y-135*scale},{x+f.dir*47*scale,y-97*scale},{x+f.dir*36*scale,y-60*scale}},palette.energy,2*scale);c.restore();}
        if(id.equals("iron")&&f.y>3&&!f.ko){double length=9+Math.sin(t*33)*3+(f.vy>0?6:0);for(double side:new double[]{-1,1})A.path(c,new double[][]{{x+side*11*scale-3*scale,y+2*scale},{x+side*11*scale+4*scale,y+2*scale},{x+side*11*scale,y+length*scale}},"#9beaff99",null);}
    }
    private static void customCostume(VectorCanvas c,FighterVisual f,Palette p){
        A.round(c,-21,-84,42,9,2,p.accent);A.round(c,-5,-85,10,11,2,p.deep);
        if(f.robot){A.round(c,-14,-125,28,25,4,p.deep);A.ellipse(c,0,-113,6,6,p.energy);A.line(c,new double[][]{{-9,-106},{9,-106}},p.accent,1.4);}
        else{A.path(c,new double[][]{{0,-129},{11,-117},{0,-104},{-11,-117}},null,p.accent,2);}
        if(f.power.equals("Ninja")){A.line(c,new double[][]{{-17,-137},{-29,-77}},"#d0dde8",4);A.line(c,new double[][]{{-22,-132},{-34,-139}},p.accent,4);}
    }
    static double smooth(double t){t=clamp(t,0,1);return t*t*(3-2*t);}
}
'''
(dest/'NativeFighterArt.java').write_text(out,encoding='utf-8')

# Shared labels and ability copy come from the same approved roster.
chars=(web/'js/characters.js').read_text(encoding='utf-8')
rows=re.findall(r'\{id:.*?\}',chars)
roster=[]
for row in rows:
    entries=dict((k,v[1:-1] if v[0] in "'\"" else v) for k,v in re.findall(r'''(\w+):('(?:[^']*)'|"(?:[^"]*)"|[\d.]+)''',row))
    roster.append(entries)
meta='''package com.example.projetomarvel;
/** Approved roster labels and abilities. Rules remain in BattleEngine. */
final class BattleRoster {
 static final class Entry {
  final String id,name,civilian,type,normal,special,passive;final double agility;
  Entry(String id,String name,String civilian,String type,String normal,String special,String passive,double agility){this.id=id;this.name=name;this.civilian=civilian;this.type=type;this.normal=normal;this.special=special;this.passive=passive;this.agility=agility;}
 }
 static final Entry[] ALL={
'''
for row in roster:
    meta+='new Entry('+','.join(json.dumps(row[k],ensure_ascii=False) for k in ['id','name','civilian','type','normal','special','passive'])+','+row['agility']+'),\n'
meta+='''};
 static Entry at(int index){return ALL[Math.max(0,Math.min(ALL.length-1,index))];}
 static String id(String name){if(name==null)return "custom";if(name.toLowerCase(java.util.Locale.ROOT).contains("doombot"))return "doombot";if(name.contains("Projeção"))return "scarlet";for(Entry e:ALL){if(name.equals(e.name))return e.id;}switch(BattleEngine.heroSkill(name)){case SPIDER:return "spider";case IRON:return "iron";case WOLVERINE:return "wolverine";case CAPTAIN:return "captain";case THOR:return "thor";case HULK:return "hulk";case PANTHER:return "panther";case STRANGE:return "strange";case WIDOW:return "widow";case SCARLET:return "scarlet";case MARVEL:return "marvel";case DOOM:return "doom";case THANOS:return "thanos";default:return "custom";}}
 static Entry find(String id){for(Entry e:ALL)if(e.id.equals(id))return e;return ALL[1];}
}
'''
(dest/'BattleRoster.java').write_text(meta,encoding='utf-8')
print('Transcribed final V3 costume geometry, colors and 13 roster entries to native Java.')

scene=(web/'js/scene.js').read_text(encoding='utf-8')
sceneheader='''package com.example.projetomarvel;
import java.util.ArrayList;
/** Native path-for-path Bifrost scene, with the approved parallax coefficients. */
final class AsgardScene {
 private static final VectorCanvas.Art A=new VectorCanvas.Art();
 private static final class Tower {double x,h,w;int layer;boolean crown;Tower(double x,int layer,double h,double w,boolean crown){this.x=x;this.layer=layer;this.h=h;this.w=w;this.crown=crown;}}
 private static final class Star {double x,y,r,phase;Star(double x,double y,double r,double phase){this.x=x;this.y=y;this.r=r;this.phase=phase;}}
 private final ArrayList<Tower> towers=new ArrayList<>();private final ArrayList<Star> stars=new ArrayList<>();private long seed=20261029;
 private double random(){seed=(seed*1664525+1013904223)&0xffffffffL;return seed/4294967296.0;}
 AsgardScene(){for(int layer=0;layer<2;layer++)for(double x=-1100;x<5800;x+=105+random()*90)towers.add(new Tower(x,layer,48+random()*100,22+random()*28,random()>.48));for(int i=0;i<35;i++)stars.add(new Star(random()*1600,55+random()*180,.5+random()*.6,random()*6));}
'''
scenesigs={'background':'VectorCanvas c,BattleCamera cam,double t','spire':'VectorCanvas c,double x,double base,double w,double h,String light,String dark,boolean crown','arch':'VectorCanvas c,double x,double y,double w,double h,String fill,String stroke','palace':'VectorCanvas c,double x,double y','world':'VectorCanvas c,BattleCamera cam,double t','beacon':'VectorCanvas c,double x,double t'}
out=sceneheader
for name,sig in scenesigs.items():
    b=body(scene,'    '+name+'(')
    b=b.replace('for (const star of this.stars)', 'for (Star star:stars)').replace('for (const tower of this.towers)', 'for (Tower tower:towers)')
    b=re.sub(r'for \(const (\w+) of (\[[^\[\]]+\])\)',lambda m:'for(double '+m.group(1)+':new double[]{'+m.group(2)[1:-1]+'})',b)
    b=b.replace('spectrum.forEach((color, i) => {','for(int i=0;i<spectrum.length;i++){String color=spectrum[i];').replace('      });','      }')
    b=b.replace('deck.forEach((p, i) => i ? c.lineTo(...p) : c.moveTo(...p));','for(int i=0;i<deck.length;i++){if(i==0)c.moveTo(deck[i][0],deck[i][1]);else c.lineTo(deck[i][0],deck[i][1]);}')
    b=convert(b).replace('this.','')
    for v in ['sky','halo','mist','haze','voidFill','vignette','g']:
        b=re.sub(r'\bdouble '+v+r'\b', 'VectorCanvas.Gradient '+v,b)
    b=b.replace('double deck =','double[][] deck =')
    b=re.sub(r'double heights = \[([^\]]+)\];',r'double[] heights = new double[]{\1};',b)
    b=re.sub(r'double spectrum = \[([^\]]+)\];',r'String[] spectrum = new String[]{\1};',b)
    b=b.replace('String color=spectrum[i];','String color=spectrum[i];')
    b=b.replace('if (stroke)', 'if (stroke!=null)')
    b=re.sub(r'(\w+) % 2 \?',r'\1 % 2 != 0 ?',b)
    b=b.replace('double xx = (i - 6) * 23, h = heights[i], w = i === 6 ? 17 : 13;', 'double xx=(i-6)*23,h=heights[i],w=i==6?17:13;')
    b=b.replace('i === 6','i == 6').replace('tower.layer ===','tower.layer ==')
    b=b.replace('i % 2 ?','i % 2 != 0 ?')
    b=b.replace('double color', 'String color')
    if name=='background':
        b=b.replace('if (tower.layer == 0)', 'if (tower.layer == 0 && Math.abs(550+z*(tower.x-cam.x*.15))<750)').replace('if (tower.layer == 1)', 'if (tower.layer == 1 && Math.abs(550+z*(tower.x-cam.x*.38))<750)')
    out+='\n void '+name+'('+sig+'){\n'+b+'\n }\n'
out+='}\n'
(dest/'AsgardScene.java').write_text(out,encoding='utf-8')
print('Transcribed Asgard skyline, palace, bridge, animation and parallax to native Java.')
