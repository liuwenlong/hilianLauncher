package com.example.cloudmirror.service;


import com.example.cloudmirror.ui.MainActivity;
import com.example.cloudmirror.utils.MyLog;
import com.example.cloudmirror.utils.QuickShPref;
import com.example.cloudmirror.widget.GetLoaction;
import com.example.cloudmirror.widget.NetWork;
import de.greenrobot.event.EventBus;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

public class DataSyncService extends Service {
	public static final String ACTION_START_SETTINGS = "action.open.recoder.settings";
	public static boolean NOT_NEED_CODE_IN = true;
	private static String TAG = "DataSyncService";
	private final IBinder mBinder = new LocalBinder();
	private Context mContext;
	@Override
	public void onCreate() {
		super.onCreate();
		MyLog.D("DataSyncService onCreate");
		mContext = this;
		getIMEI();
		registerRecv();
	}
	@Override
	public void onDestroy() {
		EventBus.getDefault().unregister(this);
		unregisterReceiver(mReceiver);
	}
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "Received start id " + startId + ": " + intent);
		if (intent != null && intent.getExtras() != null) {
		}
    	startService(new Intent("intent.action.mapgoo.volice.call"));
    	startService(new Intent("intent.action.mapgoo.data.sync"));
		return START_STICKY;
	}

	public String getIMEI() {
		String imei = QuickShPref.getInstance().getString(QuickShPref.IEMI);
		if (imei == null){
			imei = ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).getDeviceId();
			QuickShPref.getInstance().putValueObject(QuickShPref.IEMI, imei);
		}
		MyLog.D("imei=" + imei);
		return imei;
	}
	public class LocalBinder extends Binder {
		public DataSyncService getService() {
			return DataSyncService.this;
		}
	}
	
	private void registerRecv() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_START_SETTINGS);
		registerReceiver(mReceiver, filter);
	}
	BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			MyLog.D("onReceive action="+action);
			if(ACTION_START_SETTINGS.equalsIgnoreCase(action)){
				MyLog.D("startActivity com.hilan.carrecorder");
				MainActivity.startActivity(mContext, "com.hilan.carrecorder", "com.hilan.carrecorder.activity.MainActivity", null);
			}
		}
	};

}
