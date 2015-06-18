package com.example.cloudmirror.bean;

import java.sql.SQLException;
import android.util.Log;

import com.example.cloudmirror.utils.DatabaseHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;

public class ContactInfo {
	@DatabaseField(generatedId = true)
	public int id;
	@DatabaseField
	public String name;
	@DatabaseField
	public String telPhone;
	@DatabaseField
	public String remark;
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
