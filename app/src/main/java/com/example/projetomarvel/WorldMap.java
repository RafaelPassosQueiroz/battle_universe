//Espaços e anadamneto
package com.example.projetomarvel;
import java.util.*;
import static com.example.projetomarvel.WorldValues.*;
/** Exact Web map data and its collision/camera rules in native Java. */
final class WorldMap implements NativeObject {
 static Object get(Object o,Object k){return WorldValues.get(o,k);}
 final int cols,rows,width,height;final byte[] tiles;
 final Map<String,Object>[]tileRegions;
 final List<Object> regions,npcs,objects,props,solids,gates;
 float cameraX,cameraY;boolean cameraReady;
 WorldMap(Map<String,Object>data){Map<String,Object>m=map(data.get("map"));cols=integer(m.get("cols"));rows=integer(m.get("rows"));width=cols*32;height=rows*32;List<Object>t=list(m.get("tiles"));tiles=new byte[t.size()];for(int i=0;i<tiles.length;i++)tiles[i]=(byte)integer(t.get(i));regions=list(data.get("REGIONS"));npcs=list(data.get("NPCS"));objects=list(data.get("OBJECTS"));props=list(m.get("props"));solids=new ArrayList<>(list(m.get("solids")));gates=list(m.get("gates"));tileRegions=new Map[cols*rows];for(int y=0;y<rows;y++)for(int x=0;x<cols;x++)tileRegions[y*cols+x]=region(x*32,y*32);for(Object o:objects)solids.add(obj(kv("x",number(get(o,"x"))*32+11),kv("y",number(get(o,"y"))*32+15),kv("w",10),kv("h",10)));}
 int at(int x,int y){return x<0||y<0||x>=cols||y>=rows?0:tiles[y*cols+x];}
 Map<String,Object> region(double x,double y){double tx=x/32,ty=y/32;for(int i=regions.size()-1;i>=0;i--){Map<String,Object>r=map(regions.get(i));double rx=number(r.get("x")),ry=number(r.get("y"));if(tx>=rx&&tx<rx+number(r.get("w"))&&ty>=ry&&ty<ry+number(r.get("h")))return r;}double best=Double.MAX_VALUE;Map<String,Object>nearest=map(regions.get(0));for(Object o:regions){Map<String,Object>r=map(o);double dist=Math.hypot(tx-number(r.get("x"))-number(r.get("w"))/2,ty-number(r.get("y"))-number(r.get("h"))/2);if(dist<best){best=dist;nearest=r;}}return nearest;}
 Map<String,Object> byId(String id){for(Object r:regions)if(id.equals(get(r,"id")))return map(r);return null;}
 boolean overlap(Object o,double x,double y,double radius){return x+radius>number(get(o,"x"))&&x-radius<number(get(o,"x"))+number(get(o,"w"))&&y+radius>number(get(o,"y"))&&y-radius<number(get(o,"y"))+number(get(o,"h"));}
 boolean walkable(double x,double y,double radius,Map<String,Object>flags,List<Object>actors,String ignore){for(int dx:new int[]{-1,1})for(int dy:new int[]{-1,1})if(at((int)Math.floor((x+dx*radius)/32),(int)Math.floor((y+dy*radius)/32))==0)return false;for(Object o:solids)if(overlap(o,x,y,radius))return false;for(Object o:gates)if(!truth(flags.get(str(get(o,"flag"))))&&overlap(o,x,y,radius))return false;for(Object n:actors)if(!Objects.equals(ignore,get(n,"id"))&&Math.hypot(number(get(n,"px"))-x,number(get(n,"py"))-y)<radius+9)return false;return true;}
 void move(Map<String,Object>actor,double dx,double dy,Map<String,Object>flags,List<Object>npcs,String ignore,double radius){int steps=Math.max(1,(int)Math.ceil(Math.max(Math.abs(dx),Math.abs(dy))/5));for(int i=0;i<steps;i++){double x=number(actor.get("x")),y=number(actor.get("y"));if(walkable(x+dx/steps,y,radius,flags,npcs,ignore)){x+=dx/steps;actor.put("x",x);}if(walkable(x,y+dy/steps,radius,flags,npcs,ignore))actor.put("y",y+dy/steps);}}
 void camera(Map<String,Object>player,float w,float h,float dt){float tx=Math.max(0,Math.min(width-w,(float)number(player.get("x"))-w/2)),ty=Math.max(0,Math.min(height-h,(float)number(player.get("y"))-h*.52f));float a=cameraReady?1-(float)Math.exp(-dt*9):1;cameraX+=(tx-cameraX)*a;cameraY+=(ty-cameraY)*a;cameraReady=true;}
 public Object get(String k){switch(k){case "cols":return cols;case "rows":return rows;case "width":return width;case "height":return height;default:return null;}}
 public Object put(String k,Object v){return v;}
 public Object method(String k,Object[]a){if(k.equals("at"))return at(integer(a[0]),integer(a[1]));if(k.equals("region"))return region(number(a[0]),number(a[1]));return null;}
}
