package com.example.cloudmirror.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class QuickShPref {
	public final static String IEMI = "imei";
	public final static String LAT = "lat";
	public final static String LON = "lon";
	public final static String VOICE = "voice";
	public final static String HAS_ENTER = "hasenter";	
	public final static String UPLOAD_CONTACTS = "UPLOAD_CONTACTS";
	public final static String ISRUNNING = "isrunning";
	public final static String LEFT = "left";
	public final static String TOP = "top";
	public final static String WIDTH = "width";
	public final static String HEIGHT = "height";
	public final static String WEINXIN = "wein_url";
	public final static String VIBRATE_LV = "vibrate_level";
	
	private static QuickShPref mInstace;
	
	private SharedPreferences sSharedPreferences;
	private Editor sEditor;

	private QuickShPref(Context c){
		sSharedPreferences = c.getSharedPreferences(c.getPackageName(),Context.MODE_MULTI_PROCESS|Context.MODE_WORLD_READABLE);
		sEditor = sSharedPreferences.edit();	
	}
	public static void  init(Context c){
		mInstace = new QuickShPref(c);
	}
	public static QuickShPref  getInstance(){
		return mInstace;
	}
	public void putValueObject(String key ,Object obj){
		if(obj instanceof String){
			sEditor.putString(key, (String)obj);
		}else if(obj instanceof Integer){
			sEditor.putInt(key, (Integer)obj);
		}else if(obj instanceof Boolean){
			sEditor.putBoolean(key, (Boolean)obj);
		}else if(obj instanceof Float){
			sEditor.putFloat(key, (Float)obj);
		}else{
			return;
		}
		sEditor.commit();
	}
	
	public String getString(String key){
		return sSharedPreferences.getString(key, null);
	}
	public int getInt(String key){
		return sSharedPreferences.getInt(key, -1);
	}
	public boolean getBoolean(String key){
		return sSharedPreferences.getBoolean(key, false);
	}
	public Float getFloat(String key){
		return sSharedPreferences.getFloat(key, 0);
	}
}
