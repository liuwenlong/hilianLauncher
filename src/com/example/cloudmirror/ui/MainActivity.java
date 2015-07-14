package com.example.cloudmirror.ui;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import com.mapgoo.eagle.R;
import com.mapgoo.volice.ui.VoliceRecActivity;
import com.android.volley.Response.Listener;
import com.baidu.mapapi.utils.route.BaiduMapRoutePlan;
import com.baidu.mapapi.utils.route.RouteParaOption;
import com.example.cloudmirror.api.ApiClient;
import com.example.cloudmirror.api.GlobalNetErrorHandler;
import com.example.cloudmirror.api.ApiClient.onReqStartListener;
import com.example.cloudmirror.bean.CarHomeBean;
import com.example.cloudmirror.bean.VoiceYesNoBean;
import com.example.cloudmirror.bean.CarHomeBean.AdvBean;
import com.example.cloudmirror.service.DataSyncService;
import com.example.cloudmirror.service.FxService;
import com.example.cloudmirror.ui.activity.CarBrandUpdateActivity;
import com.example.cloudmirror.ui.activity.GetInvadationCodeActivity;
import com.example.cloudmirror.ui.widget.AsyncImageView;
import com.example.cloudmirror.ui.widget.FlipperIndicatorDotView;
import com.example.cloudmirror.ui.widget.RoundedRectangleBitmapDisplayer;
import com.example.cloudmirror.ui.widget.SimpleDialogBuilder;
import com.example.cloudmirror.utils.MyLog;
import com.example.cloudmirror.utils.QuickShPref;
import com.example.cloudmirror.utils.StringUtils;

import de.greenrobot.event.EventBus;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;


public class MainActivity extends BaseActivity implements Callback {
	public static String KEY_ACTION = "android.intent.action.CUSTOM_KEY_EVENT";
	public static String GPS_KEY_FLAG = "GPS_KEY"; 
	public static String DVR_KEY_FLAG = "DVR_KEY"; 
	public static String NOR_KEY_FLAG = "NORMAL_KEY"; 
	public static String SIL_KEY_FLAG = "SILENT_KEY"; 
	
	public final static int LOCK_OUT_TIME = 1*60*1000;//5*1000;
	private ViewPager mViewPager;
	private FlipperIndicatorDotView mIndicatorrView ;
	private Camera mCamera;  
    private boolean mPreviewRunning = false; 
    private LinearLayout home_adv_view;
    private final static boolean  IS_CAMREA_OPEN = false; 
    Handler mHandler = new Handler();
	@Override
	protected void setContentView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_main);
		EventBus.getDefault().register(this);
		printApp();
		startService(new Intent(mContext, DataSyncService.class).putExtra(DataSyncService.COMMAND, DataSyncService.COMMAND_NONE));
		//startService(new Intent(mContext, FxService.class));
	}
	@Override
	protected void initData(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		//startToCarRecord(RECORD_ACTION, RECORD_MODE_BACK);
		registerReceiver(KeyRcv, new IntentFilter(KEY_ACTION));
	}
	@Override
	protected void initViews() {
		// TODO Auto-generated method stub
		mViewPager = (ViewPager)findViewById(R.id.vPager);
		mIndicatorrView = (FlipperIndicatorDotView)findViewById(R.id.vPager_Indicator);
		home_adv_view = (LinearLayout)findViewById(R.id.home_adv_view);
		if(IS_CAMREA_OPEN){
			findViewById(R.id.surface_camera_btn).setVisibility(View.VISIBLE);
			findViewById(R.id.home_icon_carrecord_btn).setVisibility(View.GONE);			
			iniCamera();	
		}
	}
	@Override
	protected void handleData() {
		// TODO Auto-generated method stub
		 refreshHome();
		 VoliceRecActivity.getVoiceYseNo(this);
	}
	SurfaceView mSurfaceView;
	private void iniCamera(){
		mSurfaceView = (SurfaceView) findViewById(R.id.surface_camera); 
		SurfaceHolder mSurfaceHolder = mSurfaceView.getHolder(); 
		mSurfaceHolder.addCallback(this); 
		//mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}
	CarHomeBean mCarHomeBean;
	private void refreshHome(){
		CarHomeBean home = getCarHomeBean();
		
		if(home!=null && home.advlist!=null && !home.advlist.isEmpty()){
			loadImages(home.advlist);
		}else{
			ArrayList<AdvBean> advlist = new ArrayList<AdvBean>(){private static final long serialVersionUID = 1L;};
			AdvBean adv = new AdvBean();
			adv.imgurl = "";
			advlist.add(adv);
			loadImages(advlist);
		}
	}
	
	private void loadImages(ArrayList<AdvBean> advlist){
		
		ArrayList<View> pageViews = new ArrayList<View>();
		for (int i = 0; i < advlist.size(); i++) {
			View adView = mInflater.inflate(R.layout.home_ad_info, (ViewGroup)null); 
			AsyncImageView adImgView = (AsyncImageView) adView.findViewById(R.id.ad_img);
			adImgView.setImage(advlist.get(i).imgurl, R.drawable.home_adv_def, new RoundedRectangleBitmapDisplayer(0));
			adImgView.setScaleType(ScaleType.FIT_XY);
			pageViews.add(adView);
		}
		MyViewPagerAdapter adapter = new MyViewPagerAdapter(pageViews);
		mViewPager.setAdapter(adapter);
		mIndicatorrView.setCount(pageViews.size());
	}
	
	public class MyViewPagerAdapter extends PagerAdapter{
        private ArrayList<View> mListViews;
        
        public MyViewPagerAdapter(ArrayList<View> mListViews) {
            this.mListViews = mListViews;//构造方法，参数是我们的页卡，这样比较方便。  
        }

        @Override  
        public void destroyItem(ViewGroup container, int position, Object object){
            container.removeView(mListViews.get(position));//删除页卡  
        }
  
        @Override  
        public Object instantiateItem(ViewGroup container, int position) { //这个方法用来实例化页卡
             container.addView(mListViews.get(position), 0);//添加页卡  
             return mListViews.get(position); 
        }
  
        @Override  
        public int getCount() {
            return  mListViews.size();//返回页卡的数量  
        }
          
        @Override  
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0==arg1;//官方提示这样写  
        }
    }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK && mTipViewIsShow){
			dismissTipView();
			return true;
		}
		if(keyCode == KeyEvent.KEYCODE_BACK){
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.home_more:
				startActivity(new Intent(mContext, HomeMoreActivity.class));
				break;
			case R.id.home_call:
				showTipView();
				//sendBroadcast(new Intent("android.intent.action.concox.carrecorder.quit"));
				//sendBroadcast(new Intent(FxService.ACTION_START_RECORD));
				break;
			case R.id.violation_tip_img:
				dismissTipView();
				break;
			case R.id.home_click_volice:
				startActivity(new Intent(mContext, VoliceRecActivity.class));
				break;
			case R.id.home_icon_carrecord_btn:	
			case R.id.surface_camera_btn:
				startToCarRecord(RECORD_ACTION, RECORD_MODE_NORMAL);//打开行车记录仪
				//startToCarRecord(PHOTO_ACTION, RECORD_MODE_NORMAL);//打开行车记录仪
				break;
			case R.id.home_icon_blue_btn:
				startActivity("com.concox.bluetooth","com.concox.bluetooth.MainActivity",null); 
				//startToCarRecord(RECORD_ACTION, RECORD_MODE_HIDE);
				break;
			case R.id.home_icon_wifi_btn:
				startActivity("com.android.settings","com.android.settings.Settings$TetherSettingsActivity",null); 
				break;
			case R.id.home_icon_music_btn:
				if(!startActivity("com.kugou.android", "com.kugou.android.app.splash.SplashActivity", null))
					startActivity("com.sds.ttpod.hd", "com.sds.ttpod.hd.app.EntryActivity", "音乐");
				break;
			case R.id.home_icon_navi_btn:
				startActivity("com.baidu.BaiduMap", "com.baidu.baidumaps.WelcomeScreen", null);
				break;
			case R.id.function_item_1:
			case R.id.function_item_2:
			case R.id.function_item_3:
			case R.id.function_item_4:
			case R.id.function_item_5:
				callPhone(v.getId());
				break;
			default:
				break;
		}
	}

	private void callPhone(int id){
		CarHomeBean home = getCarHomeBean();
		String num = null;
		if(home != null){
			switch (id) {
			case R.id.function_item_1:
				num = home.getTelNum(CarHomeBean.TELTYPE_ZL);
				break;
			case R.id.function_item_2:
				num = home.getTelNum(CarHomeBean.TELTYPE_DJ);
				break;
			case R.id.function_item_3:
				num = home.getTelNum(CarHomeBean.TELTYPE_BX);
				break;
			case R.id.function_item_4:
				num = home.getTelNum(CarHomeBean.TELTYPE_4S);
				break;
			case R.id.function_item_5:
				num = home.getTelNum(CarHomeBean.TELTYPE_SOS);
				break;
			default:
				break;
			}
		}
		if(StringUtils.isEmpty(num)){
			Toast.makeText(mContext, "号码为空", Toast.LENGTH_SHORT).show();
		}else{
			callPhoneNum(mContext, num);
		}
	}
	
	private View mCallView;
	private int resShortcutId[]={R.id.function_item_1,R.id.function_item_2,R.id.function_item_3 ,R.id.function_item_4,R.id.function_item_5};
	private int resShortcutIcon[]={R.drawable.icon_call_zl,R.drawable.icon_call_dj,R.drawable.icon_call_fw ,R.drawable.icon_call_4s,R.drawable.icon_call_sos};
	private int resShortcutName[]={R.string.name_call_zl,R.string.name_call_dj,R.string.name_call_fw ,R.string.name_call_4s,R.string.name_call_sos};
	private void setResShortcutId(int resShortcutId,int resShortcutIcon,int resShortcutName){
		View item = mCallView.findViewById(resShortcutId);
		((ImageView)item.findViewById(R.id.home_more_item_icon)).setImageResource(resShortcutIcon);
		((TextView)item.findViewById(R.id.home_more_item_name)).setText(resShortcutName);
		item.setOnClickListener(this);
	}
	private void initCallView(){
		mCallView = View.inflate(mContext, R.layout.activity_home_call, null);
		for(int i=0;i<resShortcutId.length;i++){
			setResShortcutId(resShortcutId[i],resShortcutIcon[i],resShortcutName[i]);
		}
	}
	
	private boolean mTipViewIsShow = false;
	private void showTipView(){
		if(mCallView == null){
			initCallView();
		    WindowManager.LayoutParams params = new WindowManager.LayoutParams(  
		                WindowManager.LayoutParams.MATCH_PARENT,   
		                WindowManager.LayoutParams.MATCH_PARENT,   
		                WindowManager.LayoutParams.TYPE_APPLICATION ,   
		                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,   
		                PixelFormat.TRANSLUCENT); 

			getWindowManager().addView(mCallView, params);
		}else{
			mCallView.setVisibility(View.VISIBLE);
		}
		
		Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.zoom_enter);
		mCallView.findViewById(R.id.violation_tip_img).startAnimation(animation);
		mTipViewIsShow = true;
	}
	
	private void dismissTipView(){
		
		Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.zoom_exit);
		animation.setAnimationListener( new AnimationListener(){
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				mCallView.setVisibility(View.GONE);
			}
			@Override
			public void onAnimationRepeat(Animation animation) {}

			@Override
			public void onAnimationStart(Animation animation) {}	
		});
		mCallView.findViewById(R.id.violation_tip_img).startAnimation(animation);
		mTipViewIsShow = false;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {
		// TODO Auto-generated method stub
		MyLog.D("surfaceChanged");
		if(mCamera!=null && mPreviewRunning==false){
	        //Camera.Parameters p = mCamera.getParameters();
	        //p.setPreviewSize(width, height);  
	        //p.set("rotation", 90);  
	       //mCamera.setParameters(p);  
	        try{
	            mCamera.setPreviewDisplay(holder);
	        } catch (IOException e){
	            e.printStackTrace();  
	        }
	        mCamera.startPreview();
	        mPreviewRunning = true;  		
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		MyLog.D("surfaceCreated");
		try{
			if(mCamera == null)
				mCamera = Camera.open(); 
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		MyLog.D("surfaceDestroyed");
		if(mCamera == null)
			return;
		mCamera.stopPreview();  
        mPreviewRunning = false;  
       mCamera.release();
       mCamera = null;
	}
	
	public static final String RECORD_MODE = "record_mode";
	public static final String PHOTO_MODE = "photo_mode";
	public static final int RECORD_MODE_NORMAL = 1;//全屏窗口模式
	public static final int RECORD_MODE_BACK = 2;//小窗口模式
	public static final int RECORD_MODE_HIDE = 3;//后台隐藏窗口模式
	public static final String RECORD_ACTION = "record_action";//行车记录
	public static final String PHOTO_ACTION = "photo_action";//拍照
	public static final String CARBACK_ACTION = "carback_action";//倒车后视
	public static final String DVR_PKG = "com.android.concox.carrecorder";//行车记录仪包名
	public static final String DVR_CLS = "com.android.concox.view.MainActivity";//行车记录仪类名
	public void startToCarRecord(String action, int mode) {
    	surfaceDestroyed(null);
		final Intent mIntent = new Intent(Intent.ACTION_MAIN); 
		ComponentName compName = new ComponentName(DVR_PKG, DVR_CLS);
		mIntent.setComponent(compName); 
		mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mIntent.setAction(action);
		Bundle mBundle = new Bundle();
		mBundle.putInt(RECORD_MODE, mode);
		mIntent.putExtras(mBundle);
		try{
			startActivity(mIntent); 
		}catch(Exception e){
			mToast.toastMsg("没有安装该应用");
		}
    }
    private void printApp(){
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);	
        List<ResolveInfo> mApps = getPackageManager().queryIntentActivities(mainIntent, 0);
        for(final ResolveInfo app:mApps){
        	String name = app.activityInfo.name;
        	String pack = app.activityInfo.packageName;
        	String label = app.loadLabel(getPackageManager()).toString();
        	MyLog.D("name="+name+",pack="+pack+",label="+label);
        }
    }
	private boolean startActivity(String pkg,String cls,String name){
		try{
			Intent intent = new Intent().setClassName(pkg, cls);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			return true;
		}catch(Exception e){
			if(!StringUtils.isEmpty(name))
				mToast.toastMsg("没有安装"+name);
			return false;
		}
	}
	public CarHomeBean getCarHomeBean(){
		if(mCarHomeBean == null){
			mCarHomeBean = CarHomeBean.getFromJson(QuickShPref.getInstance().getString(CarHomeBean.TAG));
		}
		return mCarHomeBean;
	}
	public  static boolean callPhoneNum(Context context,String Num){
		MyLog.D("callPhoneNum:"+Num);
		if(isBlueToothConnect(context)){
			try{
				int calltype = 0;
				if(calltype == 0){
					Intent intent = new Intent().setClassName("com.concox.bluetooth","com.concox.bluetooth.MainActivity");
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
					intent.putExtra("PhoneNum", Num);
					context.startActivity(intent);
				}else{
					Intent intentp = new Intent().setClassName("com.concox.bluetooth","com.concox.bluetooth.MainActivity");
					intentp.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intentp.putExtra("PhoneNum", Num);
					context.startActivity(intentp);		
					
					Intent intent = new Intent(DataSyncService.ACTION_CW);
					Bundle bundle = new Bundle();
					bundle.putString("Number", Num);
					intent.putExtras(bundle);
					context.sendBroadcast(intent);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			return true;
		}else{
			Toast.makeText(context, "蓝牙没有连接", Toast.LENGTH_SHORT).show();
			return false;
		}
	}
	public static boolean isBlueToothConnect(Context c){
		try{
			String connect = c.getContentResolver().getType(Uri.parse("content://com.concox.bluetooth.contentprovider.TelContentProvider/isconnect"));
			MyLog.D("connect="+connect);
			if(!StringUtils.isEmpty(connect)){
				if("connect".equals(connect))
					return true;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	public void onEventMainThread(String result) {
		getCarHome();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		EventBus.getDefault().unregister(this);
		unregisterReceiver(KeyRcv);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
		MyLog.D("onWindowFocusChanged:"+hasFocus);
		if(IS_CAMREA_OPEN){
			if(hasFocus){
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						surfaceCreated(null);
						surfaceChanged(mSurfaceView.getHolder(), 0, 0, 0);
					}
				}, 500);
			}else{
				surfaceDestroyed(null);
			}
		}
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		onUsrActivity();
		registerReceiver(TimeTickRcv, new IntentFilter(Intent.ACTION_TIME_TICK));
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		unregisterReceiver(TimeTickRcv);
	}

    private void getCarHome(){
    	String imei = QuickShPref.getInstance().getString(QuickShPref.IEMI);
    	//imei = "862609000000773";
    	ApiClient.getCarHome(imei,null, new Listener<JSONObject>(){
			@Override
			public void onResponse(JSONObject response) {
				MyLog.D("onResponse:"+response.toString());
				try {
					QuickShPref.getInstance().putValueObject(CarHomeBean.TAG, response.getString("result"));
					refreshHome();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
    	}, GlobalNetErrorHandler.getInstance(getBaseContext(), null, null));
    }
    
	public void onEventMainThread(RouteParaOption para) {
	    try {
		       BaiduMapRoutePlan.openBaiduMapDrivingRoute(para, this);
		} catch (Exception e) {
		        e.printStackTrace();
		}
	}
	private void showLockScreen(){
		findViewById(R.id.lock_screen).setVisibility(View.VISIBLE);
		updateLockTime();
	}
	private boolean dismissLockScreen(){
		View lock = findViewById(R.id.lock_screen);
		if(lock.getVisibility() == View.VISIBLE){
			findViewById(R.id.lock_screen).setVisibility(View.GONE);
			return true;
		}else{
			return false;
		}
	}
	Runnable showLockRun = new Runnable(){
		@Override
		public void run() {
			showLockScreen();
		}
	};
	private boolean onUsrActivity(){
		mHandler.removeCallbacks(showLockRun);
		mHandler.postDelayed(showLockRun, LOCK_OUT_TIME);
		return dismissLockScreen();
	}
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev){
		// TODO Auto-generated method stub
		if(onUsrActivity())
			return true;
		return super.dispatchTouchEvent(ev);
	}
	@Override
	public boolean dispatchKeyEvent(KeyEvent event){
		// TODO Auto-generated method stub
		onUsrActivity();
		return super.dispatchKeyEvent(event);
	}
	private void updateLockTime(){
        Date date = new Date();
        SimpleDateFormat locktime = new SimpleDateFormat("HH:mm");
        SimpleDateFormat lockdate = new SimpleDateFormat("MM月dd日");
       ((TextView)findViewById(R.id.lock_time)).setText(locktime.format(date));
       ((TextView)findViewById(R.id.lock_date)).setText(lockdate.format(date));
	}
	BroadcastReceiver TimeTickRcv = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent){
            // TODO Auto-generated method stub  
        	updateLockTime();
        }
    };
	BroadcastReceiver KeyRcv = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent){
            // TODO Auto-generated method stub  
        	String key = intent.getStringExtra("key_flag");
        	MyLog.D("key_flag="+key);
        	if(GPS_KEY_FLAG.equals(key)){
        		startActivity("com.baidu.BaiduMap", "com.baidu.baidumaps.WelcomeScreen", null);
        	}else if(DVR_KEY_FLAG.equals(key)){
        		startToCarRecord(RECORD_ACTION, RECORD_MODE_NORMAL);//打开行车记录仪
        	}else if(NOR_KEY_FLAG.equals(key)){
        		showVoice(R.drawable.voice_open);
        	}else if(SIL_KEY_FLAG.equals(key)){
        		showVoice(R.drawable.voice_close);
        	}
        }
    };  
    
    private void showVoice(int resid){
    	Toast mToast = new Toast(this);
    	ImageView view = new ImageView(this);
    	view.setImageResource(resid);
    	mToast.setView(view);
    	mToast.setDuration(Toast.LENGTH_SHORT);
    	mToast.setGravity(Gravity.CENTER, 0,0);
    	mToast.show();
    }
}
