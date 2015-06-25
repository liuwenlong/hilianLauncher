package com.baidu.android.domain;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.android.volley.Response.Listener;
import com.baidu.speechsynthesizer.SpeechSynthesizer;
import com.example.cloudmirror.api.ApiClient;
import com.example.cloudmirror.api.GlobalNetErrorHandler;
import com.example.cloudmirror.bean.VoiceYesNoBean;
import com.example.cloudmirror.ui.activity.GasStationActivity;
import com.example.cloudmirror.utils.MyLog;
import com.example.cloudmirror.utils.QuickShPref;
import com.example.cloudmirror.utils.StringUtils;
import com.mapgoo.volice.api.VoliceSpeeh;
import com.mapgoo.volice.api.VoliceSpeeh.OnSpeechChangeListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public abstract class ResultAnasy implements OnSpeechChangeListener {
	public static class AnasyItem{
		public String content;
		public int type;
		public JSONObject data;
		public Runnable mDoRun;
		public boolean needFinsh;
		public int state;
	}
	public int mLastType = 0;
	public Runnable mLastRunnanle;
	
	public ArrayList<AnasyItem> mAnasyList = new ArrayList<AnasyItem>();
	
    public static final int ITEM_VIEW_TYPE_TEXT = 0;
    public static final int ITEM_VIEW_TYPE_HTML = 1;
    
    private int invaliteCount;
    
    private Activity mActivity;
    private VoliceSpeeh mVoliceSpeeh;
    public ResultAnasy(Activity context){
    	mActivity = context;
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
		if(mVoliceSpeeh!=null)
			mVoliceSpeeh.startSpeaker(answer);
		mLastAnasyItem = anasy;
    }
    
    public void addASKAnswer(String answer,Runnable r,boolean needFinish){
    	mLastType = 1;
    	mLastRunnanle = r;
    	addAnswer(answer, reTryRunnable, needFinish);
    }
    public void addAnasyItem(String answer,AnasyItem anasy,boolean needFinish){
    	anasy.needFinsh = true;
    	anasy.content = answer;
    	if(answer != null){
    		mAnasyList.add(anasy);
    		mVoliceSpeeh.startSpeaker(answer);
    	}
		mLastAnasyItem = anasy;
    }
    public void addAnswer(String answer,Runnable r){
    	addAnswer(answer, r, false);
    }
    
    public void  anasyJSON(JSONObject data,boolean visiable){
    	Log.i("TAG", "enter anasyJSON="+data.toString());
    	String answer = null;
    	Domain dom = null;
    	String content = null;
    	if(visiable || (data!=null&&data.has("item"))){
    		
    		try {
    			JSONArray itemlist = data.getJSONArray("item");
    			content = itemlist.get(0).toString();
    			addSpeak(content);
    			switch(mLastType){
    				case 1:
    					if(isOK(itemlist)){
    						mLastType = 0;
    						mLastAnasyItem.mDoRun = mLastRunnanle;
    						new Handler().postDelayed(mLastAnasyItem.mDoRun,300);
    						new Handler().postDelayed(new Runnable() {
								
								@Override
								public void run() {
									// TODO Auto-generated method stub
									finishActivity();
								}
							},500);
    						
    					}else if(isNo(itemlist)){
    						mLastType = 0;
    						addAnswer("您需要什么帮助", reTryRunnable);
    					}else{
    						postVoiceContent(data.toString());
    						addAnswer("没有听清，请说是或者不是", reTryRunnable);
    					}
    					return;
	    			case 2:
	    				mLastType = 0;
	    				DomainNavIns domainNavIns = new DomainNavIns();
	    				domainNavIns.Object.arrival = itemlist.get(0).toString();
	    				domainNavIns.Object.arrival = domainNavIns.Object.arrival.replace("导航到", "");
	    				domainNavIns.Object.arrival = domainNavIns.Object.arrival.replace("导航", "");
	    				domainNavIns.Object.arrival = domainNavIns.Object.arrival.replace("到", "");
	    				domainNavIns.Object.arrival = domainNavIns.Object.arrival.replace("去", "");
	    				answer = domainNavIns.doAction(mActivity);
	    				dom = domainNavIns;    				
	    				break;
    			}
    			CustomDomain customDomain = new CustomDomain(this);
    			if(customDomain.isCustomDomain(itemlist)){
    				mLastType = customDomain.type;
    				addAnswer(customDomain.answer, customDomain.doActionRunnable,customDomain.needFinish);
    				return;
    			}
    			
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	if(dom == null)
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
			    			answer = map.doAction(mActivity);
			    			dom = map;
			    		}else if(DomainApp.NAME.equalsIgnoreCase(domain)){
			    			DomainApp domainApp = JSON.parseObject(item.toString(), DomainApp.class);
			    			answer = domainApp.doAction(mActivity);
			    			dom = domainApp;
			    		}else if(DomainWeather.NAME.equalsIgnoreCase(domain)){
			    			DomainWeather domainWeather = JSON.parseObject(item.toString(), DomainWeather.class);
			    			answer = domainWeather.doAction(mActivity);
			    			dom = domainWeather;
			    		}else if(DomainCalender.NAME.equalsIgnoreCase(domain)){
			    			DomainCalender domainCalender = JSON.parseObject(item.toString(), DomainCalender.class);
			    			answer = domainCalender.doAction(mActivity);
			    			dom = domainCalender;
			    		}else if(DomainTelephone.NAME.equalsIgnoreCase(domain)){
			    			DomainTelephone domainTelephone = JSON.parseObject(item.toString(), DomainTelephone.class);
			    			answer = domainTelephone.doAction(mActivity);
			    			dom = domainTelephone;
			    		}else if(DomainJoke.NAME.equalsIgnoreCase(domain)){
			    			DomainJoke domainJoke = JSON.parseObject(item.toString(), DomainJoke.class);
			    			answer = domainJoke.doAction(mActivity);
			    			dom = domainJoke;
			    		}else if(DomainNavIns.NAME.equalsIgnoreCase(domain)){
			    			DomainNavIns domainNavIns = JSON.parseObject(item.toString(), DomainNavIns.class);
			    			answer = domainNavIns.doAction(mActivity);
			    			dom = domainNavIns;
			    		}
			    	}
				}else{

				}
			}catch(JSONException e){
				e.printStackTrace();
			}
    	}else{
    		Log.d("TAG", "not find json_res");
    	}
		if(dom==null || dom.doActionRunnable == null){
			MyLog.D("语音识别完成 doActionRunnable = null");
			addInputEorror(answer);
			postVoiceContent(data.toString());
		}else{
			addASKAnswer(answer, dom.doActionRunnable,false);
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
			if(mLastAnasyItem != null && mLastAnasyItem.mDoRun != null){
				new Handler().post(mLastAnasyItem.mDoRun);
			}
			if(mLastAnasyItem.needFinsh){
				finishActivity();
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
	Runnable searchGasStation = new Runnable() {
		@Override
		public void run() {
			Intent intent = new Intent();
			intent.setClass(mActivity, GasStationActivity.class);
			mActivity.startActivity(intent);
			mActivity.finish();
		}
	};
	Runnable finishRunnable = new Runnable(){
		@Override
		public void run() {
			// TODO Auto-generated method stub
			mActivity.finish();
			mActivity.overridePendingTransition(0, 0);
		}
	};
	
	abstract public void reTry();

	public boolean isOK(JSONArray list){
		ArrayList<String> okList = new ArrayList<String>(){{add("是");add("对");add("是的");}};
		VoiceYesNoBean volice = VoiceYesNoBean.getFromJson(QuickShPref.getInstance().getString(VoiceYesNoBean.TAG));
		if(volice!=null && volice.yes!=null && volice.yes.size()>0){
			okList = volice.yes;
		}
		return checkStringList(list,okList);
	}
	public boolean isNo(JSONArray list){
		ArrayList<String> noList = new ArrayList<String>(){{add("不是");add("不对");add("否");}};
		VoiceYesNoBean volice = VoiceYesNoBean.getFromJson(QuickShPref.getInstance().getString(VoiceYesNoBean.TAG));
		if(volice!=null && volice.no!=null && volice.no.size()>0){
			noList = volice.no;
		}
		return checkStringList(list,noList);
	}
	public static boolean checkStringList(JSONArray list,ArrayList<String> array){
		for(int i=0;i<list.length();i++){
			String str = null;
			try {
				str = list.get(i).toString();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(!StringUtils.isEmpty(str)){
				for(String item:array){
					if(item.equals(str))
						return true;
				}
			}
		}
		
		return false;		
	}
	private void finishActivity(){
		if(mActivity != null){
			mActivity.finish();
			mActivity.overridePendingTransition(0, 0);
		}
	}
	
    public void postVoiceContent(String content){
    	ApiClient.postVoiceContent(content, null, new Listener<JSONObject>(){
			@Override
			public void onResponse(JSONObject response) {
				MyLog.D("onResponse:"+response.toString());
			}
    	}, GlobalNetErrorHandler.getInstance(mActivity, null, null));
    }
}
