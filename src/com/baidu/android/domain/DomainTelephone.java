package com.baidu.android.domain;

import java.util.List;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;

import com.baidu.android.domain.DomainMap.MapObject;
import com.example.cloudmirror.bean.ContactDBPref;
import com.example.cloudmirror.ui.MainActivity;
import com.example.cloudmirror.utils.StringUtils;
import com.mapgoo.volice.ui.VoliceRecActivity;

public class DomainTelephone extends Domain{
	public static String NAME = "telephone";
	
	public static class TelObject{
		public String name;
		public String number;
	}
	
	public TelObject Object;
	
	public void setObject(TelObject arg){
		Object = arg;
	}
	@Override
	public String doAction(Context context) {
		// TODO Auto-generated method stub
		if(intent != null && intent.equals("call")){
				return getTelNum(context,Object.name);
		}
		return null;
	}

	private String getTelNum(final Context context,final String name){
		String calName = name;
		String number = ContactDBPref.getInstance().getTelPhone(name);
		
		if(StringUtils.isEmpty(number) && !StringUtils.isEmpty(Object.number)){
			number = Object.number;
			calName = Object.number;
		}else if(StringUtils.isEmpty(name) && StringUtils.isEmpty(Object.number)){
			return null;
		}
		final String telphone = number;
		
		if(StringUtils.isEmpty(telphone)){
			return "没有找到"+name;
		}else{
				doActionRunnable = new Runnable() {
					@Override
					public void run() {
			     		MainActivity.callPhoneNum(context, telphone);
					}
				};
				return "您是否要打电话给"+calName+",请说是或者不是";
		}
     	
	}
	
}
