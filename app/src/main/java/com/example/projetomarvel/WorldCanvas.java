package com.example.projetomarvel;
import android.graphics.*;import java.util.*;import static com.example.projetomarvel.WorldValues.*;
/** Pixel drawing adapter for compiled native art commands. */
final class WorldCanvas implements NativeObject {
 final Canvas canvas;final Paint paint=new Paint();final Path path=new Path();
 final Map<String,Object>styles=obj(kv("fillStyle","#ffffff"),kv("font","10px monospace"),kv("textAlign","left"),kv("globalAlpha",1));
 final ArrayDeque<Map<String,Object>>stack=new ArrayDeque<>();
 WorldCanvas(Canvas c){canvas=c;paint.setAntiAlias(false);paint.setFilterBitmap(false);}
 public Object get(String k){return styles.get(k);}public Object put(String k,Object v){styles.put(k,v);return v;}
 static int color(String s){try{if(s.startsWith("rgb(")){String[]p=s.substring(4,s.length()-1).split(",");return Color.rgb(Integer.parseInt(p[0]),Integer.parseInt(p[1]),Integer.parseInt(p[2]));}if(s.length()==9&&s.startsWith("#"))return (int)(Long.parseLong(s.substring(7,9),16)<<24|Long.parseLong(s.substring(1,7),16));return Color.parseColor(s);}catch(Exception e){return Color.WHITE;}}
 void ink(){int c=color(str(styles.get("fillStyle")));paint.setColor(c);paint.setAlpha(Math.round(Color.alpha(c)*(float)number(styles.get("globalAlpha"))));paint.setStyle(Paint.Style.FILL);}
 float n(Object[]a,int i){return (float)number(a[i]);}
 void text(){String f=str(styles.get("font"));float size=10;java.util.regex.Matcher m=java.util.regex.Pattern.compile("([0-9.]+)px").matcher(f);if(m.find())size=Float.parseFloat(m.group(1));paint.setTextSize(size);paint.setTypeface(Typeface.create(f.contains("monospace")?"monospace":"serif",f.contains("bold")?Typeface.BOLD:Typeface.NORMAL));paint.setTextAlign("center".equals(styles.get("textAlign"))?Paint.Align.CENTER:"right".equals(styles.get("textAlign"))?Paint.Align.RIGHT:Paint.Align.LEFT);}
 public Object method(String k,Object[]a){switch(k){case "save":canvas.save();stack.push(new LinkedHashMap<>(styles));break;case "restore":canvas.restore();if(!stack.isEmpty()){styles.clear();styles.putAll(stack.pop());}break;case "translate":canvas.translate(n(a,0),n(a,1));break;case "scale":canvas.scale(n(a,0),n(a,1));break;case "fillRect":ink();canvas.drawRect(n(a,0),n(a,1),n(a,0)+n(a,2),n(a,1)+n(a,3),paint);break;case "beginPath":path.reset();break;case "moveTo":path.moveTo(n(a,0),n(a,1));break;case "lineTo":path.lineTo(n(a,0),n(a,1));break;case "closePath":path.close();break;case "fill":ink();canvas.drawPath(path,paint);break;case "fillText":ink();text();canvas.drawText(str(a[0]),n(a,1),n(a,2),paint);break;case "measureText":text();return obj(kv("width",paint.measureText(str(a[0]))));default:throw new IllegalStateException("Drawing operation "+k);}return null;}
}
