package com.example.cloudmirror.bean;

import java.sql.SQLException;
import android.util.Log;

import com.example.cloudmirror.utils.DatabaseHelper;
import com.j256.ormlite.dao.Dao;

public class ContactInfo {
	public String name;
	public String telPhone;
	private static Dao<ContactInfo, String> msgDao = null;

	/**
	 * Returns the Database Access Object (DAO) for our SimpleData class. It
	 * will create it or just give the cached value.
	 */
	public static Dao<ContactInfo, String> getDao(DatabaseHelper mDatabaseHelper) {

		if (mDatabaseHelper == null)
			return null;

		if (msgDao == null) {
			try {
				msgDao = mDatabaseHelper.getDao(ContactInfo.class);
			} catch (SQLException e) {
				Log.e(DatabaseHelper.class.getName(), "Can't userDao", e);
				throw new RuntimeException(e);
			}
		}

		return msgDao;
	}	
}
