package com.concox.bluetooth.contentprovider;

import com.example.cloudmirror.bean.ContactDBPref;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class TelContentProvider extends ContentProvider{
	public static boolean isBlueToothConnect;
	private String TAG = "TelContentProvider"; 
	
	private final static String AUTHORITY = "com.concox.bluetooth.contentprovider.TelContentProvider";
	private final static String BLUETOOTH_CONNECT = "isconnect";  
    private final static String PERSON_PATH = "person";
    
    private final static int CONNECT = 1;  
    private final static int PERSON = 2;  
    
	 private final static UriMatcher sMatcher = new UriMatcher(UriMatcher.NO_MATCH);  
	    static{
	        //UriMatcher类的一个方法  
	        sMatcher.addURI(AUTHORITY, BLUETOOTH_CONNECT, CONNECT);  
	        sMatcher.addURI(AUTHORITY, PERSON_PATH, PERSON);  
	    }
	    
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * 用来返回蓝牙连接状态
	 */
	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		if(uri!=null){
			String str = uri.toString();
			int index = str.lastIndexOf('/');
			if(index>0){
				String name = str.substring(index);
				String number = ContactDBPref.getInstance().getTelPhone(name);
				
				return number;
			}
		}
		return null;
	}
	
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		return null;
	}

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		return true;
	}
	
	/*
	 * 返回所有联系人
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		Log.d(TAG, "uri="+uri.toString());
		Cursor cur = null;
		switch(sMatcher.match(uri)){
			case PERSON:				//返回所有联系人

				break;	
		}
		return cur;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/*
	 * 判断蓝牙是否连接，方法还没有完成
	 * 返回disconnect,connecting,connected
	 */
	public String isBlueToothConnect(){
		if(isBlueToothConnect)
			return "connect";
		else
			return "disconnect";
	}
}
