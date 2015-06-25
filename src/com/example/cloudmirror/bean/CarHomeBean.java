package com.example.cloudmirror.bean;

import java.io.Serializable;
import java.util.ArrayList;

import com.alibaba.fastjson.JSON;

public class CarHomeBean implements Serializable{
	public static final String TAG = "CarHomeBean";
	
	public static final int TELTYPE_ZL = 2;
	public static final int TELTYPE_DJ = 5;
	public static final int TELTYPE_BX = 1;
	public static final int TELTYPE_4S = 3;
	public static final int TELTYPE_SOS = 4;
	
	public static class AdvBean{
		public int id;
		public String postdate;
		public String htmlurl;
		public String title;
		public String imgurl;
	}
	public static class TelBean{
		public String tele;
		public int id;
		public int teletype;
		public String name;
	}
	public String imei;
	public ArrayList<AdvBean> advlist;
	public ArrayList<TelBean> telelist;
	
	public void setImei(String arg){
		imei = arg;
	}
	public static CarHomeBean getFromJson(String json){
		return JSON.parseObject(json, CarHomeBean.class);
	}
	public String getTelNum(int type){
		if(telelist!=null&&!telelist.isEmpty()){
			for(TelBean tel:telelist){
				if(tel.teletype == type){
					return tel.tele;
				}
			}
		}
		return null;
	}
}
