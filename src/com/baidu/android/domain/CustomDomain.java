package com.baidu.android.domain;

import java.util.ArrayList;

import com.baidu.location.an;

import android.content.Context;

public class CustomDomain extends Domain{
	ResultAnasy mResultAnasy;
	String answer;
	int  type;
	boolean needFinish;
	class Custom{
		public String key;
		public int value;
		public Custom(){}
		public Custom(String key,int value){
			this.key = key;
			this.value = value;
		}
	}
	
	public CustomDomain(ResultAnasy c){
		mResultAnasy = c;
	}
	ArrayList<Custom> mCustomList = new ArrayList<Custom>(){{
		add(new Custom("退出语音",1));
		add(new Custom("关闭语音",1));
		add(new Custom("退出",1));
		add(new Custom("导航",2));
	}};
	
	public boolean isCustomDomain(String key){
		for(Custom item:mCustomList){
			if(key.equals(item.key)){
				startCustom(item);
				return true;
			}
		}
		return false;
	}
	
	public void startCustom(Custom item){
		switch (item.value) {
		case 1:
			answer = "是";
			needFinish = true;
			break;
		case 2:
			answer = "请问您要导航到什么地方？";
			type = item.value;
			doActionRunnable = mResultAnasy.reTryRunnable;
			break;
		default:
			type = 0;
			break;
		}
	}
	
	
	@Override
	protected String doAction(Context context) {
		// TODO Auto-generated method stub
		
		return null;
	}
}
