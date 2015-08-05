package com.example.cloudmirror.ui;

import org.json.JSONException;
import org.json.JSONObject;

import com.alibaba.fastjson.JSON;
import com.android.volley.Response.Listener;
import com.baidu.location.BDLocation;
import com.example.cloudmirror.api.ApiClient;
import com.example.cloudmirror.api.GlobalNetErrorHandler;
import com.example.cloudmirror.api.VersionUpdate;
import com.example.cloudmirror.bean.Weather;
import com.example.cloudmirror.ui.widget.AsyncImageView;
import com.example.cloudmirror.ui.widget.RoundedRectangleBitmapDisplayer;
import com.example.cloudmirror.utils.MyLog;
import com.example.cloudmirror.utils.QuickShPref;
import com.example.cloudmirror.utils.StringUtils;
import com.mapgoo.carlife.main.R;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class AboutActivity extends BaseActivity{
	AsyncImageView mImageView;
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		finish();
	}

	@Override
	protected void setContentView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_about);
	}

	@Override
	protected void initData(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void initViews() {
		// TODO Auto-generated method stub
		((TextView)findViewById(R.id.about_imei)).setText(QuickShPref.getInstance().getString(QuickShPref.IEMI));
		((TextView)findViewById(R.id.about_serialno)).setText(Build.SERIAL);
		((TextView)findViewById(R.id.about_version)).setText(getVersionName(this));
		refreshWeixin();
	}
	
	private void refreshWeixin(){
		MyLog.D("加载微信二维码:"+QuickShPref.getInstance().getString(QuickShPref.WEINXIN));
		((AsyncImageView)findViewById(R.id.weixin_code_img)).setImage(QuickShPref.getInstance().getString(QuickShPref.WEINXIN), 
				R.drawable.code_pic, new RoundedRectangleBitmapDisplayer(0));
	}

	@Override
	protected void handleData() {
		// TODO Auto-generated method stub
		if(StringUtils.isEmpty( QuickShPref.getInstance().getString(QuickShPref.WEINXIN)))
			getWeixin();
	}

	public String getVersionName(Context context) {
		String pkName = context.getPackageName();
		String versionName = "";
		try {
			versionName = context.getPackageManager().getPackageInfo(pkName, 0).versionName;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return versionName;
	}
	public void getWeixin(){
		ApiClient.getWeixinCode(null, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				// TODO Auto-generated method stub
				MyLog.D(""+response.toString());
				try {
					JSONObject obj = response.getJSONObject("result");
					String url = obj.getString("url");
					if(!StringUtils.isEmpty(url)){
						if(!url.startsWith("http")){
							url = "http://"+url;
						}
						QuickShPref.getInstance().putValueObject(QuickShPref.WEINXIN, url);
						refreshWeixin();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, GlobalNetErrorHandler.getInstance(mContext, null, null));
	}
	
	public void updateCheck(View v){
		MyLog.D("updateCheck");
		new VersionUpdate(this).execute("0201001");
	}
}
