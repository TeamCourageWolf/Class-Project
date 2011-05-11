package com.courage_wolf;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import android.widget.Toast;

public class DataBaseHelper{
	private static final String DATABASE_NAME = "courage_wolf_data.db";
	private static final int DATABASE_VERSION = 1;
	private static final String TABLE_NAME = "CourageWolfPlayerData";

	private SimpleDateFormat dateFormat;
	private static String oldDate;

	private Context context;
	private SQLiteDatabase db;
		
	private SQLiteStatement insertStmt;
	private static final String INSERT = "INSERT INTO "+TABLE_NAME+"(uid,pass,energy,cash,rep,lastlog,lastcash) VALUES('?','?',?,?,?,'?','?')";

	private static class OpenHelper extends SQLiteOpenHelper{
		OpenHelper(Context context){
			super(context, DATABASE_NAME, null, DATABASE_VERSION);			
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			Log.w("Dbase Create", "Creating database.");
			db.execSQL("CREATE TABLE "+TABLE_NAME+"(uid TEXT PRIMARY KEY, pass TEXT, energy INTEGER, cash REAL, rep INTEGER, lastlog TEXT, lastcash TEXT)");
			db.execSQL("INSERT INTO "+TABLE_NAME+"(uid,pass,energy,cash,rep,lastlog,lastcash) VALUES('A','B',30,25.0,100,'"+oldDate+"','"+oldDate+"')");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			Log.w("Dbase Upgrade", "Upgrading database, this will drop tables and recreate.");
			db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
			onCreate(db);
		}
	}

	public DataBaseHelper(Context context, SimpleDateFormat dateFormat){
		this.context = context;
		this.dateFormat = dateFormat;
		oldDate = this.dateFormat.format(new Date());
		OpenHelper openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();
		this.insertStmt = this.db.compileStatement(INSERT);		
	}

	public long insert(String uid, String pass, int energy, double cash, int rep){
		long result = -2;
		try{
			Date now = new Date();
			this.insertStmt.bindString(1, uid);
			this.insertStmt.bindString(2, pass);
			this.insertStmt.bindString(3, Integer.toString(energy));
			this.insertStmt.bindString(4, Double.toString(cash));
			this.insertStmt.bindString(5, Integer.toString(rep));			
			this.insertStmt.bindString(6, dateFormat.format(now));
			this.insertStmt.bindString(7, dateFormat.format(now));
			Toast.makeText(this.context, "Inserting Values...", Toast.LENGTH_LONG).show();
			result = this.insertStmt.executeInsert();
			Toast.makeText(this.context, "Insert Done", Toast.LENGTH_LONG).show();
		}
		catch(Exception ex){
			Toast.makeText(this.context, "Fatal Error in Insert: "+ex.getMessage()+"\nType: "+ex.toString(), Toast.LENGTH_LONG).show();
		}
		return result;
	}

	public int update(String uid, int energy, double cash, int rep, Date lastcash){
		int result = -1;
		try{
			ContentValues updates = new ContentValues();
			updates.put("energy", energy);
			updates.put("cash", cash);
			updates.put("rep", rep);
			updates.put("lastlog", this.dateFormat.format(new Date()));
			updates.put("lastcash", this.dateFormat.format(lastcash));
			Toast.makeText(this.context, "Updating Values...", Toast.LENGTH_LONG).show();
			result = this.db.update(TABLE_NAME, updates, "uid='"+uid+"'", null);
			Toast.makeText(this.context, "Update Done", Toast.LENGTH_LONG).show();
		}
		catch(Exception ex){
			Toast.makeText(this.context, "Fatal Error in Update: "+ex.getMessage()+"\nType: "+ex.toString(), Toast.LENGTH_LONG).show();
		}
		return result;
	}

	public void deleteAll(){
		try{
			this.db.delete(TABLE_NAME, null, null);
		}
		catch(Exception ex){
			Toast.makeText(this.context, "Fatal Error in Delete All: "+ex.getMessage()+"\nType: "+ex.toString(), Toast.LENGTH_LONG).show();
		}
	}

	public UserData getValues(String uid){
		UserData result = new UserData();
		String query = "SELECT energy,cash,rep,lastlog,lastcash FROM "+TABLE_NAME+" WHERE uid='"+uid+"'";
		Cursor cursor = null;
		try{
			cursor = this.db.rawQuery(query, null);
			if(cursor!=null){
				if(cursor.moveToFirst()){
					result.setEnergy(cursor.getInt(cursor.getColumnIndex("energy")));
					result.setCash(cursor.getDouble(cursor.getColumnIndex("cash")));
					result.setRep(cursor.getInt(cursor.getColumnIndex("rep")));
					result.setLastPlayed(dateFormat.parse(cursor.getString(cursor.getColumnIndex("lastlog"))));
					result.setLastCash(dateFormat.parse(cursor.getString(cursor.getColumnIndex("lastcash"))));
					result.containsData = true;
				}
			}
		}
		catch (Exception ex){
			Toast.makeText(this.context, "Fatal Error in Get Values: "+ex.getMessage()+"\nType: "+ex.toString(), Toast.LENGTH_LONG).show();
		}
		return result;
	}

	public short checkPassWord(String uid, String pass){

		String query = "SELECT pass FROM "+TABLE_NAME+" WHERE uid='"+uid+"'";
		Cursor cursor = null;
		try
		{
			cursor = this.db.rawQuery(query, null);
			if(cursor!=null){
				if(cursor.moveToFirst()){
					String passv = cursor.getString(cursor.getColumnIndex("pass"));
					if(passv.compareTo(pass)==0)
						return 1;//Correct Password
					else
						return 0;//Wrong Password					
				}
				return -1;//No Such User
			}
		}
		catch (Exception ex)
		{
			Toast.makeText(this.context, "Fatal Error in Check Password:"+ex.getMessage()+"\nType: "+ex.toString(), Toast.LENGTH_LONG).show();
		}

		return -2;//Fatal Error
	}

	public void close(){
		this.db.close();
	}
}
