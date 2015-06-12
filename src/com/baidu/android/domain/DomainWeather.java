package com.baidu.android.domain;

import java.util.List;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.util.Log;

import com.baidu.android.domain.DomainMap.MapObject;

public class DomainWeather extends Domain{
	public static String NAME = "weather";
	
	public static class MapObject{
		public String region;
		public String _date;
	}
	
	public MapObject Object;
	
	public void setObject(MapObject arg){
		Object = arg;
	}
	@Override
	public String doAction(Context context) {
		// TODO Auto-generated method stub
		String val = "";
		if(Object.region!=null)
			val = val + Object.region;
		if(Object._date!=null)
			val = val + Object._date;		
		val = val+"天气";
		//search(context, val);
		
		return val+"多云";
	}

}
