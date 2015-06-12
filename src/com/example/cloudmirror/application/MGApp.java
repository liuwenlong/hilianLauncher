package com.example.cloudmirror.application;

import com.baidu.mapapi.SDKInitializer;
import com.car.brand.db.AppInitUtils;
import com.example.cloudmirror.api.ApiClient;
import com.example.cloudmirror.api.MyVolley;
import com.example.cloudmirror.utils.DBmanager;
import com.example.cloudmirror.utils.QuickShPref;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import android.app.Application;
import android.app.Notification;
import android.content.Context;

public class MGApp extends Application {

	public static MGApp pThis;

	@Override
	public void onCreate() {
		super.onCreate();

		init();

	}

	private void init() {
		pThis = this;

		// 初始化网络请求库volley
		MyVolley.init(this);

		// 初始化请求头的appkey
		ApiClient.initAppKey(this);

		
		initImageLoader(this);

		
		//初始化
		AppInitUtils.initApplication(this);

		QuickShPref.init(this);
		DBmanager.init(this);
		SDKInitializer.initialize(this);
	}

	public static void initImageLoader(Context context) {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
											  .threadPriority(Thread.NORM_PRIORITY - 2)
											  .denyCacheImageMultipleSizesInMemory()
											  .diskCacheFileNameGenerator(new Md5FileNameGenerator())
											  .tasksProcessingOrder(QueueProcessingType.LIFO)
											  .build();

		ImageLoader.getInstance().init(config);
	}


	// FIXME 再整个App生命周期中， 维持数据的链接，是不是不太好？
	@Override
	public void onTerminate() {
		super.onTerminate();

	}

}
