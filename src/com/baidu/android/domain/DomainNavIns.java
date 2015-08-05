package com.baidu.android.domain;

import java.net.URISyntaxException;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviParaOption;
import com.baidu.mapapi.utils.route.BaiduMapRoutePlan;
import com.baidu.mapapi.utils.route.RouteParaOption;
import com.baidu.mapapi.utils.route.RouteParaOption.EBusStrategyType;
import com.example.cloudmirror.utils.MyLog;
import com.mapgoo.carlife.main.R;
import com.mapgoo.volice.ui.NaviAdrSelectActivity;
import com.mapgoo.volice.ui.VoliceRecActivity;

import de.greenrobot.event.EventBus;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

public class DomainNavIns extends Domain{
	public static String NAME = "navigate_instruction";
	
	public static class MapObject{
		public String arrival;
		public String start;
		public String drive_sort;
		public String route_type;
	}
	public MapObject Object;

	public DomainNavIns(){
		Object = new MapObject();
	}
	
	public void setObject(MapObject arg){
		Object = arg;
	}
	@Override
	public String doAction(Context context){
		return	startNavi(context);
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
				try {
//					RouteParaOption para = new RouteParaOption()
//					.startName(start)
//					.startPoint(VoliceRecActivity.getLocLatLng())
//					.endName(Object.arrival);
//					
//					EventBus.getDefault().post(para);
					context.startActivity(new Intent(context, NaviAdrSelectActivity.class).putExtra("keywords", Object.arrival));
				} catch (Exception e) {
			        e.printStackTrace();
			    }
			}
		};

	    return "您是否要导航到"+Object.arrival+context.getString(R.string.do_ask);
	}
}
