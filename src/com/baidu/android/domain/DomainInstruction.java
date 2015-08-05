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
import com.example.cloudmirror.ui.MainActivity;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;

import de.greenrobot.event.EventBus;

public class DomainInstruction extends Domain{
	public static String NAME = "instruction";
	
	public static class MapObject{
		public String option;
	}
	
	public MapObject Object;
	
	public void setObject(MapObject arg){
		Object = arg;
	}
	@Override
	public String doAction(Context context) {
		// TODO Auto-generated method stub
		if(Object.option != null){
			if(intent.equalsIgnoreCase("select")){
				int select;
				try{
					select = (int)Integer.parseInt(Object.option);
					EventBus.getDefault().post(select);
					return Object.option;
				}catch(NumberFormatException e){
					
				}
				return null;
			}else{				
				return null;
			}
		}else if(intent.equalsIgnoreCase("next")){
			EventBus.getDefault().post(intent);
			return intent;
		}else if(intent.equalsIgnoreCase("back")){
			EventBus.getDefault().post(intent);
			return intent;
		}
		
		return null;
	}

}
