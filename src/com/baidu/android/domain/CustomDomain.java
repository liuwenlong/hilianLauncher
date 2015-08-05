package com.baidu.android.domain;

import android.content.Context;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import com.example.cloudmirror.utils.StringUtils;

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
		add(new Custom("返回",1));
		add(new Custom("返回主界面",1));
		add(new Custom("导航",2));
		add(new Custom("加油站",3));
	}};
	
	public boolean isCustomDomain(JSONArray list){
		if(list != null && list.length()>0){
			for(int i=0;i<list.length();i++){
				String key=null;
				try {
					key = list.get(i).toString();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(!StringUtils.isEmpty(key)){
					for(Custom item:mCustomList){
						if(key.equals(item.key)){
							startCustom(item);
							return true;
						}
					}
				}
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
		case 3:
			answer = "开始搜索加油站";
			doActionRunnable = mResultAnasy.searchGasStation;
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
