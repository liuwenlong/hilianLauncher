package com.mapgoo.volice.ui;

import org.json.JSONException;
import org.json.JSONObject;
import com.mapgoo.eagle.R;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class WebViewActivity extends Activity{
	WebView mWebView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.webview_activity);
		
		mWebView = (WebView)findViewById(R.id.webview);
		WebSettings settings = mWebView.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setSupportZoom(false);
		settings.setBuiltInZoomControls(false);
		mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		
		String xml = this.getIntent().getStringExtra("xml");
		String web = null;
		try {
			JSONObject obj = new JSONObject(xml);
			web = obj.getJSONObject("commandcontent").getString("web");
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		mWebView.loadDataWithBaseURL("", web, "text/html", "utf-8", "");
		
		Log.d("TAG", web);
	}

}
