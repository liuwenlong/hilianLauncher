package com.baidu.android.domain;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import com.alibaba.fastjson.JSON;
import com.baidu.android.domain.DomainApp;
import com.baidu.android.domain.DomainCalender;
import com.baidu.android.domain.DomainMap;
import com.baidu.android.domain.DomainWeather;
import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.speechsynthesizer.SpeechSynthesizer;
import com.example.cloudmirror.utils.StringUtils;
import com.mapgoo.volice.api.VoliceSpeeh;
import com.mapgoo.volice.api.VoliceSpeeh.OnSpeechChangeListener;
import com.mapgoo.volice.ui.WebViewActivity;

public abstract class ResultAnasy implements OnSpeechChangeListener {
	public static class AnasyItem{
		public String content;
		public int type;
		public JSONObject data;
		public Runnable mDoRun;
		public boolean needFinsh;
	}
	
	public ArrayList<AnasyItem> mAnasyList = new ArrayList<AnasyItem>();
	
    public static final int ITEM_VIEW_TYPE_TEXT = 0;
    public static final int ITEM_VIEW_TYPE_HTML = 1;
    
    private int invaliteCount;
    
    private Activity mContext;
    private VoliceSpeeh mVoliceSpeeh;
    public ResultAnasy(Activity context){
    	mContext = context;
    	mVoliceSpeeh = new VoliceSpeeh(context,this);
    }
    
    public void addSpeak(String answer){
		AnasyItem anasy = new AnasyItem();
		anasy.content = answer;
		anasy.type = 0;
		mAnasyList.add(anasy);
    }
    public void addAnswer(String answer,Runnable r,boolean needFinish){
		AnasyItem anasy = new AnasyItem();
		anasy.content = answer;
		anasy.type = 1;
		anasy.mDoRun =  r;
		anasy.needFinsh = needFinish;
		mAnasyList.add(anasy);
		mVoliceSpeeh.startSpeaker(answer);
		mLastAnasyItem = anasy;
    }
    public void addAnswer(String answer,Runnable r){
    	addAnswer(answer, r, false);
    }    
    
    public void  anasyJSON(JSONObject data,boolean visiable){
    	Log.i("TAG", "enter anasyJSON="+data.toString());
    	if(visiable || (data!=null&&data.has("item"))){
    		
    		try {
    			JSONArray itemlist = data.getJSONArray("item");
    			addSpeak(itemlist.get(0).toString());
    			
    			CustomDomain customDomain = new CustomDomain(mContext);
    			if(customDomain.isCustomDomain(itemlist.get(0).toString())){
    				addAnswer(customDomain.answer, customDomain.doActionRunnable,true);
    				return;
    			}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	String answer = null;
    	Domain dom = null;
    	if(data!=null && data.has("json_res")){
    		try {
    			String temp_str = data.optString("json_res");
				JSONObject res = new JSONObject(temp_str);
				JSONArray array = res.getJSONArray("results");
				
				Log.d("TAG", "enter json_res");
				
				if(array!=null && array.length()>0){
					JSONObject item = array.getJSONObject(0);
					
			    	if(getItemResultType(item) == ITEM_VIEW_TYPE_HTML){
//			    		 Intent intent = new Intent(mContext, WebViewActivity.class);
//				         intent.putExtra("xml", item.toString());
//				         mContext.startActivity(intent);
			    	}else{
			    		String domain = item.getString("domain");
			    		Log.d("TAG", "enter domain="+domain);
			    		
			    		if(DomainMap.NAME.equalsIgnoreCase(domain)){
			    			DomainMap map = JSON.parseObject(item.toString(), DomainMap.class);
			    			answer = map.doAction(mContext);
			    			dom = map;
			    		}else if(DomainApp.NAME.equalsIgnoreCase(domain)){
			    			DomainApp domainApp = JSON.parseObject(item.toString(), DomainApp.class);
			    			answer = domainApp.doAction(mContext);
			    			dom = domainApp;
			    		}else if(DomainWeather.NAME.equalsIgnoreCase(domain)){
			    			DomainWeather domainWeather = JSON.parseObject(item.toString(), DomainWeather.class);
			    			answer = domainWeather.doAction(mContext);
			    			dom = domainWeather;
			    		}else if(DomainCalender.NAME.equalsIgnoreCase(domain)){
			    			DomainCalender domainCalender = JSON.parseObject(item.toString(), DomainCalender.class);
			    			answer = domainCalender.doAction(mContext);
			    			dom = domainCalender;
			    		}else if(DomainTelephone.NAME.equalsIgnoreCase(domain)){
			    			DomainTelephone domainTelephone = JSON.parseObject(item.toString(), DomainTelephone.class);
			    			answer = domainTelephone.doAction(mContext);
			    			dom = domainTelephone;
			    		}else if(DomainJoke.NAME.equalsIgnoreCase(domain)){
			    			DomainJoke domainJoke = JSON.parseObject(item.toString(), DomainJoke.class);
			    			answer = domainJoke.doAction(mContext);
			    			dom = domainJoke;
			    		}else if(DomainNavIns.NAME.equalsIgnoreCase(domain)){
			    			DomainNavIns domainNavIns = JSON.parseObject(item.toString(), DomainNavIns.class);
			    			answer = domainNavIns.doAction(mContext);
			    			dom = domainNavIns;
			    		}
			    	}
				}else{

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}	
    	}else{
    		Log.d("TAG", "not find json_res");
    	}
		if(dom==null || dom.doActionRunnable == null){
			addInputEorror(answer);
		}else{
			addAnswer(answer, dom.doActionRunnable,true);
		}
    }
    
    public void addInputEorror(String answer){
		if(answer == null){
			answer="您的指令有误,请重试";
			invaliteCount++;
		}
		if(invaliteCount < 3)
			addAnswer(answer, reTryRunnable);
		else
			addAnswer("您的指令有误,下次见", finishRunnable);  	
    }
    
    public int getItemResultType(JSONObject data) {
        int type = ITEM_VIEW_TYPE_TEXT;
        if ("search".equals(data.opt("commandtype"))) {
            type = ITEM_VIEW_TYPE_HTML;
        } else {
            type = ITEM_VIEW_TYPE_TEXT;
        }
        return type;
    }
    AnasyItem mLastAnasyItem;
	@Override
	public void OnSpeechChangeListener(SpeechSynthesizer sp, int what,Object arg) {
		// TODO Auto-generated method stub
		switch (what) {
		case 1:
			if(mLastAnasyItem != null && mLastAnasyItem.mDoRun != null)
				new Handler().post(mLastAnasyItem.mDoRun);
			if(mLastAnasyItem.needFinsh){
				mContext.finish();
				mContext.overridePendingTransition(0, 0);
			}
			break;

		default:
			break;
		}
	}
	
	Runnable reTryRunnable = new Runnable(){
		@Override
		public void run() {
			// TODO Auto-generated method stub
			reTry();
		}
	};
	Runnable finishRunnable = new Runnable(){
		@Override
		public void run() {
			// TODO Auto-generated method stub
			mContext.finish();
			mContext.overridePendingTransition(0, 0);
		}
	};
	
	abstract public void reTry();

}
