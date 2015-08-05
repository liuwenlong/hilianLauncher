package com.example.cloudmirror.bean;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import com.baidu.mapapi.model.LatLng;
import com.baidu.speechsynthesizer.SpeechSynthesizer;
import com.example.cloudmirror.api.LatlngFactory;
import com.example.cloudmirror.ui.activity.GasStationActivity;
import com.example.cloudmirror.utils.Base64Util;
import com.example.cloudmirror.utils.MyLog;
import com.example.cloudmirror.utils.StringUtils;
import com.mapgoo.volice.api.VoliceSpeeh;
import com.mapgoo.volice.api.VoliceSpeeh.OnSpeechChangeListener;

import android.content.Context;

public class TakePerson implements Serializable{
	public String mLat;
	public String mLng;
	public String msg;
	VoliceSpeeh mVoliceSpeeh;
	Context mContext;
	public TakePerson(Context context){
		mContext = context;
		mVoliceSpeeh = new VoliceSpeeh(context, new OnSpeechChangeListener(){
			@Override
			public void OnSpeechChange(SpeechSynthesizer sp, int what,Object arg) {
				// TODO Auto-generated method stub
				MyLog.D("OnSpeechChange startNavi");
				if(mLat!=null){
					MyLog.D("OnSpeechChange startNavi");
					GasStationActivity.startNavi(mContext, null,LatlngFactory.CreatefromString(mLat, mLng));
				}
			}
		});
	}
	
	public void  startSpeakAndNavi(){
		if(!StringUtils.isEmpty(msg)){
			MyLog.D("startSpeakAndNavi msg");
			mVoliceSpeeh.startSpeaker(msg);
		}else if(mLat!=null){
			MyLog.D("startSpeakAndNavi startNavi");
			GasStationActivity.startNavi(mContext, null,LatlngFactory.CreatefromString(mLat, mLng));
		}
	}
	public void setMsg(String buf){
	   try {
		   msg = new String(Base64Util.decode(buf), "GBK");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			msg = null;
		}
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return String.format("%s,%s,%s",mLat,mLng,msg);
	}
	
}
