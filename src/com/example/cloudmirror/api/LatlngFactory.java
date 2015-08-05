package com.example.cloudmirror.api;

import android.graphics.Point;

import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.CoordinateConverter.CoordType;

public class LatlngFactory {
	public static LatLng CreatefromString(String lat,String lng){
		double dlat,dlng;
		try{
			dlat = Double.parseDouble(lat);
			dlng = Double.parseDouble(lng);
			return new LatLng(dlat, dlng);
		}catch(Exception e){
			return null;
		}
	}
	
	public static LatLng CreatefromDouble(double lat,double lng){
		try{
			return new LatLng(lat, lng);
		}catch(Exception e){
			return null;
		}
	}
	

	
	public static boolean isLatlngVisiable(MapView map,LatLng lat){
		boolean ret = false;
		if(map != null && map.getMap()!= null && map.getMap().getProjection()!=null){
			Point p =  map.getMap().getProjection().toScreenLocation(lat);
			if(p.x > 0 && p.y > 20 && p.x < map.getWidth() && p.y < map.getHeight())
				ret = true;
		}
		return ret;
		
	}
}
