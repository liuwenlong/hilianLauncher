package com.example.cloudmirror.utils;

import java.sql.SQLException;
import java.util.List;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.cloudmirror.application.MGApp;
import com.example.cloudmirror.bean.ContactInfo;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	private static final String DATABASE_NAME = "PosOnline.db";
	private static final int DATABASE_VERSION = 3;
	
	public static String mDatabasePath = null;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		mDatabasePath = context.getDatabasePath(DATABASE_NAME).getAbsolutePath();
		MyLog.D("mDatabasePath="+mDatabasePath);
	}
	/**
	 * This is called when the database is first created. Usually you should
	 * call createTable statements here to create the tables that will store
	 * your data.
	 */
	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {

		try {
			TableUtils.createTableIfNotExists(connectionSource, ContactInfo.class);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
			throw new RuntimeException(e);
		}

	}

	/**
	 * This is called when your application is upgraded and it has a higher
	 * version number. This allows you to adjust the various data to match the
	 * new version number.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVer, int newVer) {
		MyLog.D("SQLiteDatabase onUpgrade newVer="+newVer+",oldVer="+oldVer);
		if (newVer != oldVer) {
				updateTable(ContactInfo.getDao(MGApp.getHelper()), ContactInfo.class);
		}
	}

	public static <T> void updateTable(Dao<T, String> dao,Class<T> cls){
		try {
			List<T> list = dao.queryForAll();
			TableUtils.dropTable(dao.getConnectionSource(), cls, true);
			TableUtils.createTableIfNotExists(dao.getConnectionSource(), cls);
			for(T msg:list){
				dao.createIfNotExists(msg);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * Close the database connections and clear any cached DAOs.
	 */
	@Override
	public void close() {
		super.close();
	}

}
