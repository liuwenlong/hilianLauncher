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
import com.mapgoo.volice.ui.VoliceRecActivity;

public class DomainTelephone extends Domain{
	public static String NAME = "telephone";
	
	public static class TelObject{
		public String name;
		
	}
	
	public TelObject Object;
	
	public void setObject(TelObject arg){
		Object = arg;
	}
	@Override
	public String doAction(Context context) {
		// TODO Auto-generated method stub
		if(Object.name != null){
			if(intent != null && intent.equals("call")){
				return getTelNum(context,Object.name);
			}
			
		}
		return null;
	}

	private String getTelNum(Context context,String name){
    	String[] PHONES_PROJECTION = new String[] {
     	       Phone.DISPLAY_NAME, Phone.NUMBER, Phone.CONTACT_ID }; 
     	Cursor phone =context.getContentResolver().query(Phone.CONTENT_URI, PHONES_PROJECTION, Phone.DISPLAY_NAME+"='"+name+"'", null, null);
     	
     	if(phone!=null && phone.getCount()>0){
     		String number = VoliceRecActivity.getCursorString(phone, Phone.NUMBER, 0);
     		context.startActivity(new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+number)));
     		return "正在打给"+name;
     	}
     	return "没有找到"+name;
	}
	
}
