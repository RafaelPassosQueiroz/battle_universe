package com.example.projetomarvel;

import android.graphics.*;
import java.util.ArrayList;
import java.util.HashMap;

/** Native Canvas vocabulary for the approved vector art. No HTML or script runtime. */
final class VectorCanvas {
    final Canvas canvas;
    final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
    final Path path=new Path();
    Object fillStyle="#000000",strokeStyle="#000000";
    double lineWidth=1,globalAlpha=1;
    String lineJoin="round",lineCap="round",font="bold 14px Arial",textAlign="left";
    private final ArrayList<State> stack=new ArrayList<>();
    private static final HashMap<String,Integer> colors=new HashMap<>();
    private static final class State {
        Object fill,stroke;double width,alpha;String font,align;
        State(VectorCanvas c){fill=c.fillStyle;stroke=c.strokeStyle;width=c.lineWidth;alpha=c.globalAlpha;font=c.font;align=c.textAlign;}
    }
    VectorCanvas(Canvas canvas){this.canvas=canvas;paint.setStrokeJoin(Paint.Join.ROUND);paint.setStrokeCap(Paint.Cap.ROUND);}
    void save(){canvas.save();stack.add(new State(this));}
    void restore(){canvas.restore();State s=stack.remove(stack.size()-1);fillStyle=s.fill;strokeStyle=s.stroke;lineWidth=s.width;globalAlpha=s.alpha;font=s.font;textAlign=s.align;}
    void translate(double x,double y){canvas.translate((float)x,(float)y);}
    void scale(double x,double y){canvas.scale((float)x,(float)y);}
    void rotate(double radians){canvas.rotate((float)Math.toDegrees(radians));}
    void beginPath(){path.reset();}
    void moveTo(double x,double y){path.moveTo((float)x,(float)y);}
    void lineTo(double x,double y){path.lineTo((float)x,(float)y);}
    void closePath(){path.close();}
    void bezierCurveTo(double x1,double y1,double x2,double y2,double x,double y){path.cubicTo((float)x1,(float)y1,(float)x2,(float)y2,(float)x,(float)y);}
    void quadraticCurveTo(double x1,double y1,double x,double y){path.quadTo((float)x1,(float)y1,(float)x,(float)y);}
    void roundRect(double x,double y,double w,double h,double r){path.addRoundRect((float)x,(float)y,(float)(x+w),(float)(y+h),(float)r,(float)r,Path.Direction.CW);}
    void arc(double x,double y,double radius,double start,double end){path.addArc((float)(x-radius),(float)(y-radius),(float)(x+radius),(float)(y+radius),(float)Math.toDegrees(start),(float)Math.toDegrees(end-start));}
    void clip(){canvas.clipPath(path);}
    void fill(){prepare(fillStyle,false);canvas.drawPath(path,paint);}
    void stroke(){prepare(strokeStyle,true);canvas.drawPath(path,paint);}
    void fillRect(double x,double y,double w,double h){prepare(fillStyle,false);canvas.drawRect((float)x,(float)y,(float)(x+w),(float)(y+h),paint);}
    void fillText(String text,double x,double y){prepare(fillStyle,false);paint.setTypeface(Typeface.DEFAULT_BOLD);java.util.regex.Matcher m=java.util.regex.Pattern.compile("([0-9.]+)px").matcher(font);paint.setTextSize(m.find()?Float.parseFloat(m.group(1)):14);paint.setTextAlign("center".equals(textAlign)?Paint.Align.CENTER:"right".equals(textAlign)?Paint.Align.RIGHT:Paint.Align.LEFT);canvas.drawText(text,(float)x,(float)y,paint);}
    private void prepare(Object style,boolean stroke){
        paint.setStyle(stroke?Paint.Style.STROKE:Paint.Style.FILL);paint.setStrokeWidth((float)lineWidth);paint.setShader(null);paint.setAlpha(255);
        if(style instanceof Gradient){paint.setShader(((Gradient)style).shader());paint.setColor(Color.WHITE);paint.setAlpha((int)(255*clamp(globalAlpha,0,1)));}
        else{int color=style instanceof Integer?(Integer)style:color(String.valueOf(style));paint.setColor(color);paint.setAlpha((int)(Color.alpha(color)*clamp(globalAlpha,0,1)));}
    }
    static int color(String value){
        Integer found=colors.get(value);if(found!=null)return found;
        int result;
        if(value.startsWith("rgba(")){String[] parts=value.substring(5,value.length()-1).split(",");result=Color.argb((int)(Double.parseDouble(parts[3])*255),Integer.parseInt(parts[0]),Integer.parseInt(parts[1]),Integer.parseInt(parts[2]));}
        else if(value.length()==4||value.length()==5){int red=Character.digit(value.charAt(1),16)*17,green=Character.digit(value.charAt(2),16)*17,blue=Character.digit(value.charAt(3),16)*17,alpha=value.length()==5?Character.digit(value.charAt(4),16)*17:255;result=Color.argb(alpha,red,green,blue);}
        else if(value.length()==9){long hex=Long.parseLong(value.substring(1),16);result=(int)((hex>>>8)|((hex&255)<<24));}
        else result=Color.parseColor(value);
        // Dynamic environmental alpha strings must not grow the cache every frame.
        if(!value.startsWith("rgba("))colors.put(value,result);return result;
    }
    static double clamp(double x,double a,double b){return Math.max(a,Math.min(b,x));}
    static final class Gradient {
        final boolean radial;final double x,y,xx,yy,r;
        final ArrayList<Integer>colors=new ArrayList<>();final ArrayList<Float>stops=new ArrayList<>();
        Gradient(boolean radial,double x,double y,double xx,double yy,double r){this.radial=radial;this.x=x;this.y=y;this.xx=xx;this.yy=yy;this.r=r;}
        void addColorStop(double stop,String color){stops.add((float)stop);colors.add(VectorCanvas.color(color));}
        Shader shader(){int[]c=new int[colors.size()];float[]s=new float[c.length];for(int i=0;i<c.length;i++){c[i]=colors.get(i);s[i]=stops.get(i);}return radial?new RadialGradient((float)x,(float)y,(float)Math.max(.01,r),c,s,Shader.TileMode.CLAMP):new LinearGradient((float)x,(float)y,(float)xx,(float)yy,c,s,Shader.TileMode.CLAMP);}
    }
    Gradient createLinearGradient(double x,double y,double xx,double yy){return new Gradient(false,x,y,xx,yy,0);}
    Gradient createRadialGradient(double x,double y,double inner,double xx,double yy,double radius){return new Gradient(true,xx,yy,0,0,radius);}
    static final class Art {
        static void path(VectorCanvas c,double[][]points,Object fill){path(c,points,fill,"#101725",1.6);}
        static void path(VectorCanvas c,double[][]points,Object fill,Object stroke){path(c,points,fill,stroke,1.6);}
        static void path(VectorCanvas c,double[][]points,Object fill,Object stroke,double width){c.beginPath();for(int i=0;i<points.length;i++){if(i==0)c.moveTo(points[i][0],points[i][1]);else c.lineTo(points[i][0],points[i][1]);}c.closePath();if(fill!=null){c.fillStyle=fill;c.fill();}if(stroke!=null){c.strokeStyle=stroke;c.lineWidth=width;c.stroke();}}
        static void line(VectorCanvas c,double[][]points,Object color,double width){c.beginPath();for(int i=0;i<points.length;i++){if(i==0)c.moveTo(points[i][0],points[i][1]);else c.lineTo(points[i][0],points[i][1]);}c.strokeStyle=color;c.lineWidth=width;c.stroke();}
        static void line(VectorCanvas c,double[][]points,Object color){line(c,points,color,2);}
        static void round(VectorCanvas c,double x,double y,double w,double h,double r,Object fill){round(c,x,y,w,h,r,fill,null);}
        static void round(VectorCanvas c,double x,double y,double w,double h,double r,Object fill,Object stroke){c.beginPath();c.roundRect(x,y,w,h,r);if(fill!=null){c.fillStyle=fill;c.fill();}if(stroke!=null){c.strokeStyle=stroke;c.lineWidth=1.2;c.stroke();}}
        static void ellipse(VectorCanvas c,double x,double y,double rx,double ry,Object fill){ellipse(c,x,y,rx,ry,fill,null);}
        static void ellipse(VectorCanvas c,double x,double y,double rx,double ry,Object fill,Object stroke){c.beginPath();c.path.addOval((float)(x-rx),(float)(y-ry),(float)(x+rx),(float)(y+ry),Path.Direction.CW);if(fill!=null){c.fillStyle=fill;c.fill();}if(stroke!=null){c.strokeStyle=stroke;c.lineWidth=1.3;c.stroke();}}
        static void star(VectorCanvas c,double x,double y,double r,Object color){star(c,x,y,r,color,5,.43);}
        static void star(VectorCanvas c,double x,double y,double r,Object color,int n,double inner){double[][]v=new double[n*2][2];for(int i=0;i<v.length;i++){double a=i*Math.PI/n-Math.PI/2,rr=i%2==1?r*inner:r;v[i]=new double[]{x+Math.cos(a)*rr,y+Math.sin(a)*rr};}path(c,v,color,null);}
        static void ring(VectorCanvas c,double x,double y,double r,Object color,double t){c.save();c.translate(x,y);c.rotate(t);c.strokeStyle=color;c.lineWidth=1.7;for(int i=0;i<2;i++){c.beginPath();c.arc(0,0,r-i*5,0,Math.PI*2);c.stroke();}double[][]p=new double[6][2];for(int i=0;i<6;i++){double a=i*Math.PI/3;p[i]=new double[]{Math.cos(a)*r*.75,Math.sin(a)*r*.75};}path(c,p,null,color,1);c.restore();}
        static Gradient gradient(VectorCanvas c,double x,double y,double w,double h,String a,String b){Gradient g=c.createLinearGradient(x,y,x+w,y+h);g.addColorStop(0,a);g.addColorStop(1,b);return g;}
    }
}
