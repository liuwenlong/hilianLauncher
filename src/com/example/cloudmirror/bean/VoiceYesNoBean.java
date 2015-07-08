package com.example.cloudmirror.bean;

import java.io.Serializable;
import java.util.ArrayList;

import com.alibaba.fastjson.JSON;

public class VoiceYesNoBean implements Serializable{
	public static final String TAG = "VoiceYesNoBean";
	public String version;
	public ArrayList<String> yes;
	public ArrayList<String> no;
	public ArrayList<String> pwd;
	
	public static VoiceYesNoBean getFromJson(String json){
		return JSON.parseObject(json, VoiceYesNoBean.class);
	}
	
}
