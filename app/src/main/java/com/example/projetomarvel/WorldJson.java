package com.example.projetomarvel;
import java.util.*;
/** JSON data only: maps, characters, and current exploration session. */
final class WorldJson {
 static Object parse(String text){return new Reader(text).value();}
 static String write(Object v){StringBuilder b=new StringBuilder();append(b,v);return b.toString();}
 static void append(StringBuilder b,Object v){
  if(v==null){b.append("null");return;}if(v instanceof Boolean||v instanceof Number){b.append(v);return;}
  if(v instanceof Map){b.append('{');boolean first=true;for(Map.Entry<?,?>e:((Map<?,?>)v).entrySet()){if(e.getValue() instanceof WorldValues.Fn)continue;if(!first)b.append(',');first=false;append(b,String.valueOf(e.getKey()));b.append(':');append(b,e.getValue());}b.append('}');return;}
  if(v instanceof Collection){b.append('[');boolean first=true;for(Object x:(Collection<?>)v){if(!first)b.append(',');first=false;append(b,x);}b.append(']');return;}
  String s=String.valueOf(v);b.append('"');for(char c:s.toCharArray())switch(c){case '"':b.append("\\\"");break;case '\\':b.append("\\\\");break;case '\n':b.append("\\n");break;case '\r':b.append("\\r");break;case '\t':b.append("\\t");break;default:if(c<32)b.append(String.format("\\u%04x",(int)c));else b.append(c);}b.append('"');
 }
 static final class Reader {final String s;int i;Reader(String s){this.s=s;}
  void skip(){while(i<s.length()&&Character.isWhitespace(s.charAt(i)))i++;}
  char take(){return s.charAt(i++);}Object value(){skip();char c=s.charAt(i);if(c=='{'){i++;Map<String,Object>m=new LinkedHashMap<>();skip();if(s.charAt(i)=='}'){i++;return m;}do{skip();String k=string();skip();i++;m.put(k,value());skip();}while(take()==',');return m;}if(c=='['){i++;List<Object>a=new ArrayList<>();skip();if(s.charAt(i)==']'){i++;return a;}do{a.add(value());skip();}while(take()==',');return a;}if(c=='"')return string();if(s.startsWith("true",i)){i+=4;return true;}if(s.startsWith("false",i)){i+=5;return false;}if(s.startsWith("null",i)){i+=4;return null;}int p=i;while(i<s.length()&&"0123456789-+.eE".indexOf(s.charAt(i))>=0)i++;return Double.valueOf(s.substring(p,i));}
  String string(){if(take()!='"')throw new IllegalArgumentException("Invalid JSON");StringBuilder b=new StringBuilder();while(i<s.length()){char c=take();if(c=='"')break;if(c=='\\'){c=take();switch(c){case 'n':c='\n';break;case 'r':c='\r';break;case 't':c='\t';break;case 'b':c='\b';break;case 'f':c='\f';break;case 'u':c=(char)Integer.parseInt(s.substring(i,i+4),16);i+=4;break;}}b.append(c);}return b.toString();}
 }
}
