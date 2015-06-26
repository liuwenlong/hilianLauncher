package com.baidu.android.domain;

import java.net.URISyntaxException;

import com.baidu.mapapi.utils.route.BaiduMapRoutePlan;
import com.baidu.mapapi.utils.route.RouteParaOption;
import com.baidu.mapapi.utils.route.RouteParaOption.EBusStrategyType;
import com.example.cloudmirror.ui.activity.GasStationActivity;
import com.example.cloudmirror.utils.MyLog;
import com.mapgoo.eagle.R;
import com.mapgoo.volice.ui.VoliceRecActivity;

import de.greenrobot.event.EventBus;
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
			    	.startPoint(VoliceRecActivity.getLocLatLng())
			        .endName(VoliceRecActivity.mBDLocation.getCity()+Object.arrival);
			    EventBus.getDefault().post(para);
			    try {
			       BaiduMapRoutePlan.openBaiduMapDrivingRoute(para, context);
				} catch (Exception e) {
			        e.printStackTrace();
			    }
			}
		};
		return "您是否要导航到"+Object.arrival+context.getString(R.string.do_ask);
	    //return "正在导航到"+VoliceRecActivity.mBDLocation.getCity()+Object.arrival;
	    //return "是";
	}

	private String startNearby(final Context context){
		MyLog.D("----->startNearby:Object.keywords="+Object.keywords);
		if(Object.keywords != null){
			doActionRunnable = new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					context.startActivity(new Intent(context, GasStationActivity.class).putExtra("keywords", Object.keywords));
				}
			};
			return "您是否要搜索附近的"+Object.keywords+context.getString(R.string.do_ask);
		}else
			return null;
	}
}
