package com.nti56.xmisa.adapter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

	private static MySQLiteHelper mSQLiteHelper;
	private static SQLiteDatabase mSqlite;

	public MySQLiteHelper(Context context, String name, int version) {
		super(context, name, null, version);
	}

	public static void setSQLiteHelper(Context context, String name, int version) {
		if (mSQLiteHelper == null) {
			mSQLiteHelper = new MySQLiteHelper(context, name, version);
		}
	}

	public static SQLiteDatabase getInstance() {
		if (mSqlite == null) {
			mSqlite = mSQLiteHelper.getWritableDatabase();
		}
		return mSqlite;
	}

	public static void initSQLiteDatabase() {
		if (mSqlite == null) {
			mSqlite = mSQLiteHelper.getWritableDatabase();
		}
	}

	public static void closeSQLiteDatabase() {

		if (mSqlite != null && mSqlite.isOpen()) {
			mSqlite.close();
		}
		mSqlite = null;
	}

	@Override
	public synchronized void close() {
		// TODO Auto-generated method stub
		Log.e("NTI", "SQLiteHelper........close()");
		super.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {// .db文件不存在时才会走这个oncreat
		Log.e("NTI", "SQLiteHelper........onCreate()");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}
}
