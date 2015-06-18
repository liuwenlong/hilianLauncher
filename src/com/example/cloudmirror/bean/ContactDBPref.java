package com.example.cloudmirror.bean;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.example.cloudmirror.application.MGApp;
import com.example.cloudmirror.utils.MyLog;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.table.TableUtils;

public class ContactDBPref {
	private static ContactDBPref mMsgDBPref;
	private static Dao<ContactInfo, String> msgDao;
	
	private ContactDBPref() {
		if(msgDao == null){  
			msgDao = ContactInfo.getDao(MGApp.getHelper());
		}
	}
	
	public static ContactDBPref getInstance(){
		if(mMsgDBPref == null){
			mMsgDBPref = new ContactDBPref();   
		}

		return mMsgDBPref;
	}
	
	public boolean isExsitContact(ContactInfo info){
		try{
			long count = msgDao.queryBuilder().where().eq("name", info.name).and().eq("telPhone", info.telPhone).countOf();
			if(count>0){
				return true;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean isCursorSame(Cursor cur){
		ArrayList<ContactInfo> list = getContactListFromCursor(cur);
		int eqCount = 0;
		boolean isSame = false;
		for(ContactInfo info:list){
			if(isExsitContact(info)){
				eqCount++;
				if(eqCount >= 5){
					isSame = true;
					break;
				}
			}
		}
		return isSame;
	}
	
	public ArrayList<ContactInfo> getContactListFromCursor(Cursor cur){
		ArrayList<ContactInfo> list = new ArrayList<ContactInfo>(){};
		if(cur!=null){
			for(int i=0;i<cur.getCount();i++){
				ContactInfo info = new ContactInfo();
				cur.moveToPosition(i);
				info.name = cur.getColumnName(1);
				info.telPhone = cur.getColumnName(2);
				list.add(info);
			}
		}
		return list;
	}
	
	public void updateDbFromCursor(Cursor cur,ArrayList<ContactInfo> list){
		try{
			TableUtils.clearTable(msgDao.getConnectionSource(), ContactInfo.class);
			for(ContactInfo info:list){
				msgDao.createIfNotExists(info);	
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
