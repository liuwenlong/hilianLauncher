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
import android.util.Log;
import com.alibaba.fastjson.JSON;
import com.baidu.android.domain.DomainApp;
import com.baidu.android.domain.DomainCalender;
import com.baidu.android.domain.DomainMap;
import com.baidu.android.domain.DomainWeather;
import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.example.cloudmirror.utils.StringUtils;
import com.mapgoo.volice.api.VoliceSpeeh;
import com.mapgoo.volice.ui.WebViewActivity;

public class ResultAnasy {
	public static class AnasyItem{
		public String content;
		public int type;
		public JSONObject data;
	}
	
	public ArrayList<AnasyItem> mAnasyList = new ArrayList<AnasyItem>();
	
    public static final int ITEM_VIEW_TYPE_TEXT = 0;
    public static final int ITEM_VIEW_TYPE_HTML = 1;
    
    private Activity mContext;
    private VoliceSpeeh mVoliceSpeeh;
    public ResultAnasy(Activity context){
    	mContext = context;
    	mVoliceSpeeh = new VoliceSpeeh(context);
    }
    
    public void addAnswer(String answer,int type){
		AnasyItem anasy = new AnasyItem();
		anasy.content = answer;
		anasy.type = type;
		mAnasyList.add(anasy);
		if(type==1){
			mVoliceSpeeh.startSpeaker(answer);
		}
    }
    
    
    
    public void  anasyJSON(JSONObject data,boolean visiable){
    	Log.i("TAG", "enter anasyJSON="+data.toString());
    	if(visiable || data.has("item")){
    		
    		try {
    			JSONArray itemlist = data.getJSONArray("item");
    			addAnswer(itemlist.get(0).toString(), 0);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	String answer = null;
    	if(data.has("json_res")){
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
			    		}else if(DomainApp.NAME.equalsIgnoreCase(domain)){
			    			DomainApp domainApp = JSON.parseObject(item.toString(), DomainApp.class);
			    			answer = domainApp.doAction(mContext);
			    		}else if(DomainWeather.NAME.equalsIgnoreCase(domain)){
			    			DomainWeather domainWeather = JSON.parseObject(item.toString(), DomainWeather.class);
			    			answer = domainWeather.doAction(mContext);
			    		}else if(DomainCalender.NAME.equalsIgnoreCase(domain)){
			    			DomainCalender domainCalender = JSON.parseObject(item.toString(), DomainCalender.class);
			    			answer = domainCalender.doAction(mContext);
			    		}else if(DomainTelephone.NAME.equalsIgnoreCase(domain)){
			    			DomainTelephone domainTelephone = JSON.parseObject(item.toString(), DomainTelephone.class);
			    			answer = domainTelephone.doAction(mContext);
			    		}else if(DomainJoke.NAME.equalsIgnoreCase(domain)){
			    			DomainJoke domainJoke = JSON.parseObject(item.toString(), DomainJoke.class);
			    			answer = domainJoke.doAction(mContext);
			    		}else if(DomainNavIns.NAME.equalsIgnoreCase(domain)){
			    			DomainNavIns domainNavIns = JSON.parseObject(item.toString(), DomainNavIns.class);
			    			answer = domainNavIns.doAction(mContext);
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
		if(StringUtils.isEmpty(answer))
			answer="无效指令";
		addAnswer(answer, 1);
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

//    public static void startNavi(final Context context,String start,String end){
//		NaviPara para = new NaviPara();
//		para.startPoint = null;
//		para.startName = start;
//		para.endPoint = null;
//		para.endName = end;
//
//		try {
//			BaiduMapNavigation.openBaiduMapNavi(para, context);
//		} catch (BaiduMapAppNotSupportNaviException e) {
//			e.printStackTrace();
//			AlertDialog.Builder builder = new AlertDialog.Builder(context);
//			builder.setMessage("您尚未安装百度地图app或app版本过低，点击确认安装？");
//			builder.setTitle("提示");
//			builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					dialog.dismiss();
//					BaiduMapNavigation.getLatestBaiduMapApp(context);
//				}
//			});
//
//			builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					dialog.dismiss();
//				}
//			});
//
//			builder.create().show();
//		}
//    }
    
    
    
}
