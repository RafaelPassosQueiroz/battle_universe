//Centraliza fontes
package com.example.projetomarvel;

import android.content.Context;
import android.graphics.Typeface;
import java.util.HashMap;

/** The four OFL font families imported from the reference HTML. */
final class Typography {
    private static final HashMap<String,Typeface> CACHE=new HashMap<>();
    private static Typeface font(Context c,String name){synchronized(CACHE){Typeface f=CACHE.get(name);if(f==null){f=Typeface.createFromAsset(c.getAssets(),"fonts/"+name+".ttf");CACHE.put(name,f);}return f;}}
    static Typeface bebas(Context c){return font(c,"BebasNeue-Regular");}
    static Typeface barlow(Context c){return font(c,"Barlow-Regular");}
    static Typeface barlowMedium(Context c){return font(c,"Barlow-Medium");}
    static Typeface barlowSemi(Context c){return font(c,"Barlow-SemiBold");}
    static Typeface barlowBold(Context c){return font(c,"Barlow-Bold");}
    static Typeface rajdhani(Context c){return font(c,"Rajdhani-Medium");}
    static Typeface rajdhaniSemi(Context c){return font(c,"Rajdhani-SemiBold");}
    static Typeface rajdhaniBold(Context c){return font(c,"Rajdhani-Bold");}
    static Typeface cinzel(Context c){return font(c,"Cinzel");}
}
