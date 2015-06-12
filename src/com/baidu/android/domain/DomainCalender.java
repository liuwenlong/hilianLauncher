package com.baidu.android.domain;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.util.Log;

import com.baidu.android.domain.DomainMap.MapObject;

public class DomainCalender extends Domain{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4127773390911598081L;
	public static String NAME = "calendar";
	
	public static class MapObject{
		public String Answer;
		
	}
	
	public MapObject Object;
	
	public void setObject(MapObject arg){
		Object = arg;
	}
	@Override
	public String doAction(Context context) {
		// TODO Auto-generated method stub
		if(Object.Answer != null){
			//ShowAnswer(Object.Answer, context);
		}
		return Object.Answer;
	}

	public void ShowAnswer(String answer,Context context){
		 new AlertDialog.Builder(context).setMessage(answer).setPositiveButton("确定",new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
			
		}).show();
	}
	
}
