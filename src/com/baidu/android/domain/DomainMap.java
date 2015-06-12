package com.baidu.android.domain;

import java.net.URISyntaxException;

import com.baidu.mapapi.utils.route.BaiduMapRoutePlan;
import com.baidu.mapapi.utils.route.RouteParaOption;
import com.baidu.mapapi.utils.route.RouteParaOption.EBusStrategyType;
import com.mapgoo.volice.ui.VoliceRecActivity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DomainMap extends Domain{
	public static String NAME = "map";
	
	public static class MapObject{
		public String arrival;
		public String start;
		public String drive_sort;
		public String route_type;
		public String centre;
		public String keywords;
	}
	public MapObject Object;

	public void setObject(MapObject arg){
		Object = arg;
	}
	@Override
	public String doAction(Context context){
//		if(Object.start == null)
//			Object.start = MainActivity.mBDLocation.getAddrStr();
//			if(Object.start == null)
//				Object.start = "深圳蛇口万维大楼";
//			
//		Intent intent = new Intent(); 
//		intent.setAction(Intent.ACTION_WEB_SEARCH); 
//		intent.putExtra(SearchManager.QUERY,String.format("%s到%s",Object.start,Object.arrival));
//		context.startActivity(intent); 
		if(intent.equals("route")){
			return startNavi(context);
		}else if(intent.equals("poi")){
			return startNearby();
		}else if(intent.equals("nearby")){
			return startNearby();
		}else{
			return null;
		}
	}
	private String startNavi(Context context){
		String start = Object.start;
		if(start == null){
			start = VoliceRecActivity.mBDLocation.getAddrStr();
		}else{
			start = VoliceRecActivity.mBDLocation.getCity()+start;
		}
		if(Object.arrival == null)
			return null;
	    RouteParaOption para = new RouteParaOption()
	    .startName(start)
	        .endName(VoliceRecActivity.mBDLocation.getCity()+Object.arrival);
	    try {
	       BaiduMapRoutePlan.openBaiduMapDrivingRoute(para, context);
		} catch (Exception e) {
	        e.printStackTrace();
	    }
	    return "正在导航到"+VoliceRecActivity.mBDLocation.getCity()+Object.arrival;
	}

	private String startNearby(){
		if(Object.keywords!=null)
			return "正在搜索"+Object.keywords;
		else
			return null;
	}
}
