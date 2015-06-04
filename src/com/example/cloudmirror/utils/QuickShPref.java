package com.example.cloudmirror.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class QuickShPref {
	public final static String IEMI = "imei";
	public final static String LAT = "lat";
	public final static String LON = "lon";

	private static QuickShPref mInstace;
	
	private SharedPreferences sSharedPreferences;
	private Editor sEditor;

	private QuickShPref(Context c){
		sSharedPreferences = c.getSharedPreferences(c.getPackageName(),Context.MODE_PRIVATE);
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
