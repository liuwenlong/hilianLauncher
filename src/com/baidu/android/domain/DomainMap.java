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
		if(intent.equals("route")){
			return startNavi(context);
		}else if(intent.equals("poi")){
			return startNearby(context);
		}else if(intent.equals("nearby")){
			return startNearby(context);
		}else{
			return null;
		}
	}
	private String startNavi(final Context context){
		final String start;
		if(Object.start == null){
			start = VoliceRecActivity.mBDLocation.getAddrStr();
		}else{
			start = Object.start;
		}
		
		if(Object.arrival == null)
			return null;
		
		doActionRunnable = new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
			    RouteParaOption para = new RouteParaOption()
			    	.startName(start)
			        .endName(VoliceRecActivity.mBDLocation.getCity()+Object.arrival);
			    try {
			       BaiduMapRoutePlan.openBaiduMapDrivingRoute(para, context);
				} catch (Exception e) {
			        e.printStackTrace();
			    }
			}
		};
	    
	    //return "正在导航到"+VoliceRecActivity.mBDLocation.getCity()+Object.arrival;
	    return "是";
	}

	private String startNearby(Context context){
		
		if(Object.keywords!=null){
			Object.start = Object.keywords;
			startNavi(context);
			return "是";
		}else
			return null;
	}
}
