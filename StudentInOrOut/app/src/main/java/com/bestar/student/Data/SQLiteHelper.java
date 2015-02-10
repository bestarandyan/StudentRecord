package com.bestar.student.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
public class SQLiteHelper extends SQLiteOpenHelper {
	private static final String DATABASENAME = "StudentInOrOutSchool.db";
	private static final int VERSION = 1;
	public SQLiteHelper(Context context) {
		super(context, DATABASENAME, null, VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(PersonBean.tbCreateSql);
		db.execSQL(FamilyBean.tbCreateSql);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

}
