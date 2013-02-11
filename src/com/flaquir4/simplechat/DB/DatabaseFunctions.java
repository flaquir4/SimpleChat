/**
 * 
 */
package com.flaquir4.simplechat.DB;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author Borja
 *
 */
public class DatabaseFunctions extends SQLiteOpenHelper {
	//database params
	private static int DatabaseVersion = 1;
	private static String DatabaseName = "simplechat";
	private String DatabaseTableName ="friends";
	//table params
	private String ColumnName = "name";
	private String ColumnRegId= "regid";



	public DatabaseFunctions(Context context) {
		super(context, DatabaseName, null, DatabaseVersion);
	}


	@Override
	public void onCreate(SQLiteDatabase db) {
		String CreateTable = "create table "+DatabaseTableName+" ( "
				+ColumnName+" varchar(50) primary key, "
				+ColumnRegId+" TEXT UNIQUE NOT NULL );"	;
		db.execSQL(CreateTable);
	}


	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		db.execSQL("DROP TABLE IF EXISTS "+DatabaseTableName);
		onCreate(db);
	}


	public void addFriend(String name, String regId){
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(ColumnName, name);
		values.put(ColumnRegId, regId);

		db.insert(DatabaseTableName, null, values);
		db.close();
	}

	public ArrayList<HashMap<String,String>> getChats(){
		ArrayList<HashMap<String,String>> result = new ArrayList<HashMap<String,String>>();

		String query = "Select * from "+ DatabaseTableName;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query,null);
		cursor.moveToFirst();

		while(!cursor.isAfterLast()){
			HashMap<String,String> aux = new HashMap<String,String>();
			aux.put(ColumnName, cursor.getString(0));
			aux.put(ColumnRegId, cursor.getString(1));
			result.add(aux);
			cursor.moveToNext();
		}
		
		cursor.close();
		db.close();

		return result;
	}

}
