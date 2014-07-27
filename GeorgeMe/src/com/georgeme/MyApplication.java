package com.georgeme;

import com.georgeme.DaoMaster.DevOpenHelper;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class MyApplication extends Application {
	
	static DaoSession mDaoSession;
	
	@Override
	public void onCreate() {
		super.onCreate();
		initDB(this); 
		MyVolley.init(getApplicationContext());
//		prepareSampleData();
		mDaoSession.getUserDao().deleteAll();
		
	}
	
	
	
	
	private void prepareSampleData() {
		mDaoSession.getUserDao().deleteAll();
		String[] name = new String[] { "Nevin Chen",
				"Rex Chen ", "Uegene Wang" };

		String[] location = new String[] { " 大同區","內湖區", "中和區" };
		String[] pic = new String[] { "nevin", "rex", "uegene" };
		 
		for (int i=0;i<name.length;i++){
			User u = new User();
			u.setLantitude("25.0"+i);
			u.setLongitude("124.1"+i);
			u.setName(name[i]);
			u.setPicture("file://sdcard/User/"+pic[i]+".jpg");
			mDaoSession.getUserDao().insert(u);
		}
		

		
	}
	public static DaoSession getDBSession(Context conext){
		
		if (mDaoSession==null){
			initDB(conext);
		}
		return mDaoSession;
	}
	
	private static void initDB(Context conext){
		DevOpenHelper helper = new DaoMaster.DevOpenHelper(conext, "phind-db",
				null);
		SQLiteDatabase db = helper.getWritableDatabase();
		DaoMaster mDaoMaster = new DaoMaster(db);
		mDaoSession = mDaoMaster.newSession();
	}
}
