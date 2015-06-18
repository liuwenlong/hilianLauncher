package com.example.cloudmirror.service;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.cloudmirror.utils.MyLog;
import com.example.cloudmirror.utils.QuickShPref;
import com.example.cloudmirror.widget.GetLoaction;
import com.example.cloudmirror.widget.NetWork;
import com.umeng.analytics.MobclickAgent;

import de.greenrobot.event.EventBus;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;

public class DataSyncService extends Service{
	
	public static final String ACTION_LIST_REFRESH = "com.concox.BLUETOOTH_LIST_REFRESH";
	public static final String ACTION_IG = "com.concox.BLUETOOTH_IG";
	public static final String ACTION_IF = "com.concox.BLUETOOTH_IF";
	public static final String ACTION_MD = "com.concox.BLUETOOTH_MD";
	public static final String ACTION_CX = "com.concox.BLUETOOTH_CX";
	public static final String ACTION_CE = "com.concox.BLUETOOTH_CE";
	public static final String ACTION_CO = "com.concox.BLUETOOTH_CO";
	public static final String ACTION_CG = "com.concox.BLUETOOTH_CG";
	public static final String ACTION_CF = "com.concox.BLUETOOTH_CF";
	public static final String ACTION_CW = "com.concox.BLUETOOTH_CW";
	public static final String ACTION_CK = "com.concox.BLUETOOTH_CK";
	public static final String ACTION_CL = "com.concox.BLUETOOTH_CL";
	public static final String ACTION_IK = "com.concox.BLUETOOTH_IK";
	public static final String ACTION_IB = "com.concox.BLUETOOTH_IB";
	public static final String ACTION_MY = "com.concox.BLUETOOTH_MY";
	public static final String ACTION_PC = "com.concox.BLUETOOTH_PC";
	public static final String ACTION_MX = "com.concox.BLUETOOTH_MX";
	public static final String ACTION_PA1 = "com.concox.BLUETOOTH_PA1";
	private static String TAG = "DataSyncService";
	private GetLoaction mGetLoaction = new GetLoaction();
	NetWork mNetWork = new NetWork();
	
    public class LocalBinder extends Binder {
        public DataSyncService getService() {
            return DataSyncService.this;
        }
    }

	@Override
	public void onCreate() {
		super.onCreate();
		 Log.i(TAG, "onCreate");
		// locationInit();
		//openADB();
		//getIMEI();
		//initSingle();
		//mNetWork.start();
		 registReceiver();
	}
	
    private BroadcastReceiver mMyReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			
			// TODO Auto-generated method stub
			if(intent != null){
				MyLog.D("action="+intent.getAction());
				
				//
			}
			isBlueToothConnect();
		}
    	
    };
    
    
	private void getContact(){
		try{
		Cursor phone = getContentResolver().query(Uri.parse("content://com.concox.bluetooth.contentprovider.TelContentProvider/person"), null, null, null, null);
		if(phone!=null){
			phone.moveToFirst();
			int colum = phone.getColumnCount();
			for(int i=0;i<colum;i++){
				
				MyLog.D(phone.getColumnName(i)+"="+phone.getString(i));
				
			}
		}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void isBlueToothConnect(){
		try{
			String connect = getContentResolver().getType(Uri.parse("content://com.concox.bluetooth.contentprovider.TelContentProvider/isconnect"));
			MyLog.D("connect="+connect);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private void registReceiver(){
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_LIST_REFRESH);
		filter.addAction(ACTION_IG);
		filter.addAction(ACTION_IF);
		filter.addAction(ACTION_MD);
		filter.addAction(ACTION_CX);
		filter.addAction(ACTION_CE);
		filter.addAction(ACTION_CO);
		filter.addAction(ACTION_CG);
		filter.addAction(ACTION_CF);
		filter.addAction(ACTION_CW);
		filter.addAction(ACTION_CK);
		filter.addAction(ACTION_CL);
		filter.addAction(ACTION_IK);
		filter.addAction(ACTION_IB);
		filter.addAction(ACTION_MY);
		filter.addAction(ACTION_PC);
		filter.addAction(ACTION_MX);
		filter.addAction(ACTION_PA1);
		registerReceiver(mMyReceiver, filter);
		
	}
	
	private void initSingle(){
		MyPhoneStateListener MyListener   = new MyPhoneStateListener();  
		TelephonyManager  tel = ( TelephonyManager )getSystemService(Context.TELEPHONY_SERVICE);  
        tel.listen(MyListener ,PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        
        mGetLoaction.uploadBattery(0);
	}

    @Override
    public void onDestroy() {
        mLocClient.stop();
        MobclickAgent.onPause(this);
        startService(new Intent(this, DataSyncService.class));
    }
    private final IBinder mBinder = new LocalBinder();
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Received start id " + startId + ": " + intent);
        if(intent!=null && intent.getExtras()!=null){

        }
        return START_STICKY;
    }
	
    LocationClient mLocClient;
    MyLocationListenner myListener= new MyLocationListenner();
    Handler mHandler = new Handler();
    // 定位初始化
    private void locationInit(){
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(30*1000);
		mLocClient.setLocOption(option);
		
		mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mLocClient.start(); 
			}
		}, 0*1000);
		
		Log.d(TAG,"百度定位 初始化成功");
    }
	public class MyLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)	{Log.e(TAG,"百度定位失败"); return;}
			Log.d(TAG,"百度定位成功:"+location.getLongitude()+","+location.getLatitude());
			mGetLoaction.uploadPos(location);
			saveLastPos(location);

		}
		public void onReceivePoi(BDLocation poiLocation) {}
	}
	
	public void saveLastPos(BDLocation location){
		QuickShPref.getInstance().putValueObject(QuickShPref.LAT, (float)(location.getLatitude()));
		QuickShPref.getInstance().putValueObject(QuickShPref.LON, (float)(location.getLongitude()));
		MyLog.D("百度定位地址保存成功");
	}
	public static void openADB(){
		new Thread(){
			@Override
			public void run() {
				super.run();
				
				String url = "http://127.0.0.1/goform/debug?action=adb&item=1";
				try{
					HttpResponse resp = getHttpResponse(url);
					
					if(resp != null)
						Log.e(TAG,"openADB--------resposeCode:"+resp.getStatusLine().getStatusCode());
					else
						Log.e(TAG,"openADB--------resposeCode null");
					
					url = "http://127.0.0.1/goform/CommConfig?cmd=TestSuite&action=gpstest&item=start&subitem=0";
					resp = getHttpResponse(url);
					
					if(resp != null)
						Log.e(TAG,"openGPS--------resposeCode:"+resp.getStatusLine().getStatusCode());
					else
						Log.e(TAG,"openGPS--------resposeCode null");
					
				}catch (Exception e) {
					e.printStackTrace();
					Log.e(TAG,"openADB or GPS--------exception-------------------");
				}
				
			}
		}.start();
	}
	
	public static HttpResponse getHttpResponse(String url) throws ClientProtocolException, IOException{
		HttpGet httpget = new HttpGet(url);
		HttpClient client = new DefaultHttpClient();
		
		HttpResponse resp = client.execute(httpget);
		
		return resp;
	}

	
	public String getIMEI(){
		String imei = QuickShPref.getInstance().getString(QuickShPref.IEMI);
		
		if(imei == null){
			imei =((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).getDeviceId();
			MyLog.D("imei="+imei);
			QuickShPref.getInstance().putValueObject(QuickShPref.IEMI, imei);
			mGetLoaction.mIMEI = imei;
		}

		return imei;
	}
	
	private class MyPhoneStateListener extends PhoneStateListener{  
      /* Get the Signal strength from the provider, each tiome there is an update  从得到的信号强度,每个tiome供应商有更新*/  
      @Override
      public void onSignalStrengthsChanged(SignalStrength signalStrength){
         super.onSignalStrengthsChanged(signalStrength);  
         //MyLog.D("signalStrength.getGsmSignalStrength()="+signalStrength.getGsmSignalStrength());
         mGetLoaction.setGSMsingle(signalStrength.getGsmSignalStrength());
      }
	}
	
	
}
