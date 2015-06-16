package com.baidu.android.domain;

import java.io.Serializable;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;

public abstract class Domain implements Serializable{

	private static final long serialVersionUID = -6442539860789014944L;

	public String Domain;
	public String intent;
	public Runnable doActionRunnable;
	public int state;
	
	public void setDomain(String arg){
		Domain = arg;
	}
	public void setIntent(String arg){
		intent = arg;
	}
	protected abstract String doAction(Context context);
	
	public void search(Context context ,String value){
		Intent intent = new Intent(); 
		intent.setAction(Intent.ACTION_WEB_SEARCH); 
		intent.putExtra(SearchManager.QUERY,String.format("%s",value));
		context.startActivity(intent); 
	}
}
