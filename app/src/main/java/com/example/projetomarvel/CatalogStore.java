//Guarda os personagens carregados
package com.example.projetomarvel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

/** SQLite cache for the complete, incrementally synchronized Comic Vine catalog. */
public final class CatalogStore extends SQLiteOpenHelper {
    public CatalogStore(Context context){super(context,"marvel_catalog.db",null,2);}
    @Override public void onCreate(SQLiteDatabase db){db.execSQL("CREATE TABLE characters (id INTEGER PRIMARY KEY, name TEXT, real_name TEXT, deck TEXT, image TEXT, publisher TEXT, description TEXT, powers TEXT)");}
    @Override public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){db.execSQL("DROP TABLE IF EXISTS characters");onCreate(db);}
    public synchronized void save(List<MainActivity.Hero> heroes){SQLiteDatabase db=getWritableDatabase();db.beginTransaction();try{for(MainActivity.Hero h:heroes){ContentValues v=new ContentValues();v.put("id",h.id);v.put("name",h.name);v.put("real_name",h.real);v.put("deck",h.deck);v.put("image",h.image);v.put("publisher",h.publisher);v.put("description",h.description);v.put("powers",h.powers);db.insertWithOnConflict("characters",null,v,SQLiteDatabase.CONFLICT_REPLACE);}db.setTransactionSuccessful();}finally{db.endTransaction();}}
    public synchronized void updateDetails(int id,String description,String powers){ContentValues values=new ContentValues();values.put("description",description);values.put("powers",powers);getWritableDatabase().update("characters",values,"id=?",new String[]{String.valueOf(id)});}
    public synchronized ArrayList<MainActivity.Hero> load(){ArrayList<MainActivity.Hero> out=new ArrayList<>();try(Cursor c=getReadableDatabase().rawQuery("SELECT id,name,real_name,deck,image,publisher,description,powers FROM characters",null)){while(c.moveToNext()){MainActivity.Hero h=new MainActivity.Hero(c.getInt(0),c.getString(1),c.getString(2),c.getString(3),c.getString(4),c.getString(5));h.description=c.getString(6);h.powers=c.getString(7);out.add(h);}}return out;}
}
