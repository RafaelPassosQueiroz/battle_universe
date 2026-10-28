//Proporções e regras de jogo
package com.example.projetomarvel;

import java.util.*;
import java.util.function.Supplier;

/** Values and closures used by the native, compiled campaign rules. */
final class WorldValues {
 interface NativeObject {Object get(String name);Object put(String name,Object value);Object method(String name,Object[] args);}
 interface Body {void run(Scope scope,Object[] args);}
 interface Fn {Object call(Object[] args);}
 static final class Scope {
  final Scope parent;final Map<String,Object> vars=new LinkedHashMap<>();
  Scope(Scope parent){this.parent=parent;}
  Object get(String name){return vars.containsKey(name)?vars.get(name):parent==null?null:parent.get(name);}
  Object let(String name,Object value){vars.put(name,value);return value;}
  Object set(String name,Object value){if(vars.containsKey(name)||parent==null)return let(name,value);return parent.set(name,value);}
 }
 static final class Return extends RuntimeException {final Object value;Return(Object v){super(null,null,false,false);value=v;}}
 static final class Break extends RuntimeException {Break(){super(null,null,false,false);}}
 static final class Continue extends RuntimeException {Continue(){super(null,null,false,false);}}
 static final class Spread {final Object value;Spread(Object v){value=v;}}
 static final class Pair {final String key;final Object value;Pair(Object k,Object v){key=str(k);value=v;}}
 static Fn fn(Scope parent,Body body){return a->{try{body.run(new Scope(parent),a);return null;}catch(Return r){return r.value;}};}
 static Object call(Object f,Object[] a){return f instanceof Fn?((Fn)f).call(a):null;}
 static Return returned(Object v){return new Return(v);}
 static RuntimeException error(Object v){return v instanceof RuntimeException?(RuntimeException)v:new IllegalStateException(str(v));}
 static boolean always(){return true;}static void discard(Object v){}
 static Object argument(Object[] a,int i){return i<a.length?a[i]:null;}
 static Object coalesce(Object v,Supplier<Object> fallback){return v==null?fallback.get():v;}
 static Spread spread(Object v){return new Spread(v);}static Pair kv(Object k,Object v){return new Pair(k,v);}
 static Object[] args(Object...vs){return arr(vs).toArray();}
 static List<Object> arr(Object...vs){List<Object> r=new ArrayList<>();for(Object v:vs)if(v instanceof Spread)r.addAll(list(((Spread)v).value));else r.add(v);return r;}
 @SuppressWarnings("unchecked") static Map<String,Object> obj(Object...vs){Map<String,Object> r=new LinkedHashMap<>();for(Object v:vs)if(v instanceof Pair){Pair p=(Pair)v;r.put(p.key,p.value);}else if(v instanceof Spread&&((Spread)v).value instanceof Map)r.putAll((Map<String,Object>)((Spread)v).value);return r;}
 @SuppressWarnings("unchecked") static List<Object> list(Object v){if(v instanceof List)return (List<Object>)v;if(v instanceof Collection)return new ArrayList<>((Collection<Object>)v);if(v instanceof String){List<Object> a=new ArrayList<>();for(char c:((String)v).toCharArray())a.add(String.valueOf(c));return a;}return new ArrayList<>();}
 @SuppressWarnings("unchecked") static Map<String,Object> map(Object v){return v instanceof Map?(Map<String,Object>)v:new LinkedHashMap<>();}
 static double number(Object v){if(v==null)return 0;if(v instanceof Number)return ((Number)v).doubleValue();if(v instanceof Boolean)return (Boolean)v?1:0;try{return Double.parseDouble(str(v));}catch(Exception e){return Double.NaN;}}
 static int integer(Object v){return (int)number(v);}
 static String str(Object v){if(v==null)return "";if(v instanceof String)return (String)v;if(v instanceof Number){double n=number(v);return n==(long)n?Long.toString((long)n):Double.toString(n);}return String.valueOf(v);}
 static boolean truth(Object v){return v!=null&&(!(v instanceof Boolean)||(Boolean)v)&&(!(v instanceof Number)||(number(v)!=0&&!Double.isNaN(number(v))))&&(!(v instanceof String)||!((String)v).isEmpty());}
 static boolean equal(Object a,Object b){if(a instanceof Number&&b instanceof Number)return number(a)==number(b);return Objects.equals(a,b);}
 static Object and(Supplier<Object>a,Supplier<Object>b){Object v=a.get();return truth(v)?b.get():v;}
 static Object or(Supplier<Object>a,Supplier<Object>b){Object v=a.get();return truth(v)?v:b.get();}
 static Object get(Object o,Object k){if(o instanceof NativeObject)return ((NativeObject)o).get(str(k));if(o instanceof Map)return ((Map<?,?>)o).get(str(k));if(o instanceof List){List<?> a=(List<?>)o;if("length".equals(str(k)))return a.size();int i=integer(k);return i>=0&&i<a.size()?a.get(i):null;}if(o instanceof String){String s=(String)o;if("length".equals(str(k)))return s.length();int i=integer(k);return i>=0&&i<s.length()?s.substring(i,i+1):null;}return null;}
 @SuppressWarnings("unchecked") static Object put(Object o,Object k,Object v){if(o instanceof NativeObject)return ((NativeObject)o).put(str(k),v);if(o instanceof Map)((Map<String,Object>)o).put(str(k),v);else if(o instanceof List){List<Object>a=(List<Object>)o;int i=integer(k);while(a.size()<=i)a.add(null);a.set(i,v);}return v;}
 static Object remove(Object o,Object k){if(o instanceof Map)((Map<?,?>)o).remove(str(k));return true;}
 static Object update(Scope s,String k,int d,boolean post){Object old=s.get(k);Object n=number(old)+d;s.set(k,n);return post?old:n;}
 static Object unary(String op,Object a){switch(op){case "!":return !truth(a);case "-":return -number(a);case "+":return number(a);default:throw new IllegalStateException(op);}}
 static Object binary(String op,Object a,Object b){switch(op){case "+":return a instanceof String||b instanceof String?str(a)+str(b):number(a)+number(b);case "-":return number(a)-number(b);case "*":return number(a)*number(b);case "/":return number(a)/number(b);case "%":return number(a)%number(b);case "===":case "==":return equal(a,b);case "!==":case "!=":return !equal(a,b);case "<":return number(a)<number(b);case ">":return number(a)>number(b);case "<=":return number(a)<=number(b);case ">=":return number(a)>=number(b);case ">>":return integer(a)>>integer(b);case ">>>":return integer(a)>>>integer(b);case "&":return integer(a)&integer(b);case "|":return integer(a)|integer(b);default:throw new IllegalStateException(op);}}
 static Object construct(String name,Object[]a){if("Set".equals(name))return new LinkedHashSet<>(list(argument(a,0)));if("Error".equals(name))return new IllegalStateException(str(argument(a,0)));throw new IllegalStateException(name);}
 static Object method(Object o,Object key,Object[] a){String k=str(key);if(o instanceof NativeObject)return ((NativeObject)o).method(k,a);
  if(o instanceof Map){Object f=((Map<?,?>)o).get(k);return f instanceof Fn?call(f,a):null;}
  if(o instanceof String){String s=(String)o;String v=str(argument(a,0));switch(k){case "trim":return s.trim();case "startsWith":return s.startsWith(v);case "includes":return s.contains(v);case "slice":return s.substring(Math.max(0,integer(argument(a,0))),a.length>1?Math.min(s.length(),integer(a[1])):s.length());case "charCodeAt":return (int)s.charAt(integer(a[0]));}}
  if(o instanceof List){List<Object> l=list(o);Object f=argument(a,0);switch(k){
   case "includes":return l.stream().anyMatch(x->equal(x,f));
   case "push":l.addAll(Arrays.asList(a));return l.size();
   case "unshift":l.addAll(0,Arrays.asList(a));return l.size();
   case "join":{StringJoiner j=new StringJoiner(a.length==0?",":str(a[0]));for(Object v:l)j.add(str(v));return j.toString();}
   case "slice":{int from=Math.max(0,integer(f)),to=a.length>1?Math.min(l.size(),integer(a[1])):l.size();return new ArrayList<>(l.subList(Math.min(from,to),to));}
   case "find":for(int i=0;i<l.size();i++)if(truth(call(f,args(l.get(i),i,l))))return l.get(i);return null;
   case "some":for(int i=0;i<l.size();i++)if(truth(call(f,args(l.get(i),i,l))))return true;return false;
   case "every":for(int i=0;i<l.size();i++)if(!truth(call(f,args(l.get(i),i,l))))return false;return true;
   case "map":{List<Object>r=new ArrayList<>();for(int i=0;i<l.size();i++)r.add(call(f,args(l.get(i),i,l)));return r;}
   case "filter":{List<Object>r=new ArrayList<>();for(int i=0;i<l.size();i++)if(truth(call(f,args(l.get(i),i,l))))r.add(l.get(i));return r;}
   case "forEach":for(int i=0;i<l.size();i++)call(f,args(l.get(i),i,l));return null;
   case "reduce":{Object acc=a.length>1?a[1]:l.get(0);for(int i=a.length>1?0:1;i<l.size();i++)acc=call(f,args(acc,l.get(i),i,l));return acc;}
  }}
  if(o==null)return null;throw new IllegalStateException("Unsupported native operation "+k+" on "+o.getClass());
 }
 static void installBuiltins(Scope g){
  g.let("Boolean",(Fn)a->truth(argument(a,0)));
  g.let("Math",obj(kv("max",(Fn)a->{double v=-Double.MAX_VALUE;for(Object x:a)v=Math.max(v,number(x));return v;}),kv("min",(Fn)a->{double v=Double.MAX_VALUE;for(Object x:a)v=Math.min(v,number(x));return v;}),kv("round",(Fn)a->(double)Math.round(number(a[0]))),kv("floor",(Fn)a->Math.floor(number(a[0]))),kv("abs",(Fn)a->Math.abs(number(a[0]))),kv("sin",(Fn)a->Math.sin(number(a[0])))));
  g.let("Object",obj(kv("keys",(Fn)a->new ArrayList<>(map(a[0]).keySet())),kv("entries",(Fn)a->{List<Object>r=new ArrayList<>();map(a[0]).forEach((k,v)->r.add(arr(k,v)));return r;}),kv("assign",(Fn)a->{Map<String,Object>m=map(a[0]);for(int i=1;i<a.length;i++)m.putAll(map(a[i]));return m;}),kv("hasOwn",(Fn)a->map(a[0]).containsKey(str(a[1])))));
  g.let("Array",obj(kv("from",(Fn)a->{int n=integer(get(a[0],"length"));List<Object>r=new ArrayList<>();for(int i=0;i<n;i++)r.add(call(a[1],args(null,i)));return r;})));
  g.let("parseInt",(Fn)a->Long.parseLong(str(a[0]),a.length>1?integer(a[1]):10));
  g.let("noise",(Fn)a->{int x=integer(a[0]),y=integer(a[1]),seed=a.length>2?integer(a[2]):0;int n=(x+seed*31)*374761393+(y+seed*7)*668265263;n=(n^(n>>>13))*1274126177;return ((n^(n>>>16))&0xffffffffL)/4294967295d;});
  g.let("addNote",(Fn)a->{Map<String,Object>s=map(a[0]);List<Object>notes=list(s.get("notes"));if(notes.stream().anyMatch(v->equal(get(v,"id"),a[1])))return false;notes.add(obj(kv("id",a[1]),kv("title",a[2]),kv("text",a[3])));return true;});
  g.let("rememberEnding",(Fn)a->null);
 }
}
