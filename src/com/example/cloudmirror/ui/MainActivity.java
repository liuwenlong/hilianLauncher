package com.example.cloudmirror.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONException;
import org.json.JSONObject;
import com.mapgoo.carlife.main.R;
import com.alibaba.fastjson.JSON;
import com.android.volley.Response.Listener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviParaOption;
import com.baidu.mapapi.utils.route.BaiduMapRoutePlan;
import com.baidu.mapapi.utils.route.RouteParaOption;
import com.example.cloudmirror.api.ApiClient;
import com.example.cloudmirror.api.GlobalNetErrorHandler;
import com.example.cloudmirror.api.LatlngFactory;
import com.example.cloudmirror.bean.CarHomeBean;
import com.example.cloudmirror.bean.CarHomeBean.AdvBean;
import com.example.cloudmirror.bean.Weather;
import com.example.cloudmirror.service.DataSyncService;
import com.example.cloudmirror.ui.activity.GasStationActivity;
import com.example.cloudmirror.ui.widget.AsyncImageView;
import com.example.cloudmirror.ui.widget.FlipperIndicatorDotView;
import com.example.cloudmirror.ui.widget.RoundedRectangleBitmapDisplayer;
import com.example.cloudmirror.utils.MyLog;
import com.example.cloudmirror.utils.QuickShPref;
import com.example.cloudmirror.utils.StringUtils;
import de.greenrobot.event.EventBus;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import android.graphics.Rect;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends BaseActivity{
	public static String KEY_ACTION = "android.intent.action.CUSTOM_KEY_EVENT";
	public static String GPS_KEY_FLAG = "GPS_KEY"; 
	public static String DVR_KEY_FLAG = "DVR_KEY"; 
	public static String NOR_KEY_FLAG = "NORMAL_KEY"; 
	public static String SIL_KEY_FLAG = "SILENT_KEY"; 
	
	public static String ACTION_HOEM_VISIABLE_CHANGE = "action.home.visiable.change";
	
	
	public final static int LOCK_OUT_TIME = -1;//1*60*1000;
	private ViewPager mViewPager;
	private int mSelectPage;
	private int mPageScrollStateChanged;
	private int mPageScrollOffsetX;
	private FlipperIndicatorDotView mIndicatorrView ;
	private Camera mCamera;  
    private boolean mPreviewRunning = false; 
    private LinearLayout home_adv_view;
    private final static boolean  IS_CAMREA_OPEN = false; 
    Handler mHandler = new Handler();
    
    int[] pageIndex = new int[]{R.drawable.home_page_index_icon_1,R.drawable.home_page_index_icon_2};
	@Override
	protected void setContentView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_main_3);
		EventBus.getDefault().register(this);
	}
	
	@Override
	protected void initData(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
	}
	
	@Override
	protected void initViews() {
		// TODO Auto-generated method stub
		//initShortcutMenu();
		initPageView();
	}
	
	private void initPageView(){
		mViewPager = (ViewPager)findViewById(R.id.vPager);
		ArrayList<View> pageViews = new ArrayList<View>();
		pageViews.add(View.inflate(this, R.layout.new_home_page_1, null));
		pageViews.add(View.inflate(this, R.layout.new_home_page_2, null));
		initShortcutMenuPage1(pageViews.get(0));
		initShortcutMenuPage2(pageViews.get(1));
		MyViewPagerAdapter adapter = new MyViewPagerAdapter(pageViews);
		mViewPager.setAdapter(adapter);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener(){
			@Override
			public void onPageScrollStateChanged(int arg0) {
				//MyLog.D("----->onPageScrollStateChanged:arg0="+arg0);
				mPageScrollStateChanged = arg0;
				if(arg0 == 1){
					mHandler.postDelayed(new Runnable() {
						@Override
						public void run() {
							if(mPageScrollStateChanged == 1){
								if(mPageScrollOffsetX > 0){
									sendBroadcast(new Intent(ACTION_HOEM_VISIABLE_CHANGE).putExtra("visiable", false));
								}else{
									mHandler.postDelayed(this, 20);
								}
							}
						}
					}, 1);
				}else if(arg0 == 0){
					if(mSelectPage == 0)
						sendBroadcast(new Intent(ACTION_HOEM_VISIABLE_CHANGE).putExtra("visiable", true));
				}
			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				//MyLog.D("----->onPageScrolled:arg0="+arg0+",arg1="+arg1+",arg2="+arg2);
				mPageScrollOffsetX = arg2;
			}
			@Override
			public void onPageSelected(int arg0) {
				//MyLog.D("----->onPageSelected:arg0="+arg0);
				mSelectPage = arg0;
				((ImageView)findViewById(R.id.home_page_index)).setImageResource(pageIndex[arg0]);
				if(arg0 == 0){
					sendBroadcast(new Intent(ACTION_HOEM_VISIABLE_CHANGE).putExtra("visiable", true));
				}else{
					sendBroadcast(new Intent(ACTION_HOEM_VISIABLE_CHANGE).putExtra("visiable", false));
				}
			}
		});
	}
	
	
	@Override
	protected void handleData() {
		// TODO Auto-generated method stub
		 refreshHome();
		 updateLockTime();
		 //locationInit();
		 startService(new Intent(this,DataSyncService.class));
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

			case R.id.home_icon_blue_btn:
				startActivity(this,"com.concox.bluetooth","com.concox.bluetooth.MainActivity",null); 
				//startToCarRecord(RECORD_ACTION, RECORD_MODE_HIDE);
				break;
			case R.id.home_icon_wifi_btn:
				startActivity(this,"com.android.settings","com.android.settings.Settings$TetherSettingsActivity",null); 
				break;
			case R.id.home_icon_music_btn:
				startActivity(this,"com.sds.ttpod.hd", "com.sds.ttpod.hd.app.EntryActivity", "音乐");
				break;
			case R.id.home_icon_navi_btn:
				startActivity(this,"com.baidu.BaiduMap", "com.baidu.baidumaps.WelcomeScreen", null);
				break;
			default:
				break;
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
        	MyLog.D("pack="+pack+"  ,name="+name+"  ,label="+label);
        }
    }
    
	public static boolean startActivity(Context context,String pkg,String cls,String name){
		boolean ret;
		try{
			Intent intent = new Intent().setClassName(pkg, cls);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			context.startActivity(intent);
			ret =  true;
		}catch(Exception e){
			if(!StringUtils.isEmpty(name))
				Toast.makeText(context, name, Toast.LENGTH_SHORT).show();
			ret =  false;
		}
		MyLog.D("startActivity "+pkg+","+ret);
		return ret;
	}
	private void startActivity(String pkg,String cls,String name){
		MainActivity.startActivity(this, pkg, cls, name);
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
		if(result.equalsIgnoreCase("NetWorkConnect")){
			getCarHome();
			//getWeather(mBDLocation);
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		onUsrActivity();
		registerReceiver(TimeTickRcv, new IntentFilter(Intent.ACTION_TIME_TICK));
		QuickShPref.getInstance().putValueObject(QuickShPref.ISRUNNING, (int)1);
		
		if(mSelectPage == 0)
			mHandler.postDelayed(mShowRecoderRectRun, 800);
		
		mHandler.postDelayed(mGetRecoderRectRun, 2000);
		MyLog.D("主界面onResume");
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mHandler.removeCallbacks(mShowRecoderRectRun);
		unregisterReceiver(TimeTickRcv);
		QuickShPref.getInstance().putValueObject(QuickShPref.ISRUNNING, (int)0);
		sendBroadcast(new Intent(ACTION_HOEM_VISIABLE_CHANGE).putExtra("visiable", false));
		MyLog.D("主界面onPause");
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
		MyLog.D("onWindowFocusChanged hasFocus="+hasFocus);
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
	    	if(para.getStartPoint() == null){
	    		if(mLocLatLng!=null){
	    			para.startPoint(mLocLatLng);
	    		}else{
	    			mToast.toastMsg("定位失败,无法启动导航");
	    			return;
	    		}
	    	}
		       BaiduMapRoutePlan.openBaiduMapDrivingRoute(para, this);
		} catch (Exception e) {
		        e.printStackTrace();
		}
	}
	public void onEventMainThread(NaviParaOption para) {
	    try {
	    	if(para.getStartPoint() == null){
	    		if(mLocLatLng!=null){
	    			para.startPoint(mLocLatLng);
	    		}else{
	    			mToast.toastMsg("定位失败,无法启动导航");
	    			return;
	    		}
	    	}
		    BaiduMapNavigation.openBaiduMapNavi(para, this);
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
		if(LOCK_OUT_TIME>0){
			mHandler.removeCallbacks(showLockRun);
			mHandler.postDelayed(showLockRun, LOCK_OUT_TIME);
		}
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
       ((TextView)findViewById(R.id.lock_date)).setText(lockdate.format(date)+"  "+getWeek());   
//       ((TextView)findViewById(R.id.home_time_txt)).setText(locktime.format(date));
//       ((TextView)findViewById(R.id.home_date_txt)).setText(lockdate.format(date)+"  "+getWeek());   
	}
	private String getWeek(){
		Calendar calendar = Calendar.getInstance();  
	    calendar.setTime(new Date()); 
	    int dayIndex = calendar.get(Calendar.DAY_OF_WEEK);
		String str[] = getResources().getStringArray(R.array.week_string);
		return str[dayIndex];
	}
	BroadcastReceiver TimeTickRcv = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent){
            // TODO Auto-generated method stub  
        	updateLockTime();
        }
    };

	private int resShortcutId[]={
			R.id.function_item_1,R.id.function_item_2,R.id.function_item_3 ,
			R.id.function_item_4,R.id.function_item_5,R.id.function_item_6
			,R.id.function_item_7,R.id.function_item_8
	};
	
	private void initShortcutMenuPage1(View v){
		for(int i=0;i<resShortcutId.length;i++){
			View item = v.findViewById(resShortcutId[i]);
			item.setVisibility(View.INVISIBLE);
		}
		int i=0;
		shortcutDHUA.setResShortcutId(v,resShortcutId[i]);i++;
		shortcutDHANG.setResShortcutId(v,resShortcutId[i]);i++;
		shortcutYYUE.setResShortcutId(v,resShortcutId[i]);i++;
		shortcutXCJLY.setResShortcutId(v,resShortcutId[i]);i++;
		shortcutDT.setResShortcutId(v,resShortcutId[i]);i++;
		shortcutDZG.setResShortcutId(v,resShortcutId[i]);i++;
		shortcutJYZ.setResShortcutId(v,resShortcutId[i]);i++;
		shortcutFM.setResShortcutId(v,resShortcutId[i]);i++;
	}
	
	private void initShortcutMenuPage2(View v){
		for(int i=0;i<resShortcutId.length;i++){
			View item = v.findViewById(resShortcutId[i]);
			item.setVisibility(View.INVISIBLE);
		}
		int i=0;
		
		shortcutXCZS.setResShortcutId(v,resShortcutId[i]);i++;
		shortcutWZCX.setResShortcutId(v,resShortcutId[i]);i++;
		shortcutSP.setResShortcutId(v,resShortcutId[i]);i++;
		shortcutSZ.setResShortcutId(v,resShortcutId[i]);i++;
		shortcutGY.setResShortcutId(v,resShortcutId[i]);i++;
//		shortcutGP.setResShortcutId(v,resShortcutId[i]);i++;
//		shortcutYYZS.setResShortcutId(v,resShortcutId[i]);i++;
//		shortcutGD.setResShortcutId(v,resShortcutId[i]);i++;
	}
	public abstract class ShortcutItem{
		int resShortcutIcon;
		int resShortcutName;
		int resShortcutId;
		View parentView;
		
		ShortcutItem(int icon,int name){
			resShortcutIcon = icon;
			resShortcutName = name;
		}
		Handler mShortcutHandler = new Handler();
		public void setResShortcutId(View v,int resShortcutId){
			View item = v.findViewById(resShortcutId);
			((ImageView)item.findViewById(R.id.home_more_item_icon)).setImageResource(resShortcutIcon);
			((TextView)item.findViewById(R.id.home_more_item_name)).setText(resShortcutName);
			item.setVisibility(View.VISIBLE);
			item.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
						doAction();
				}
			});
		}
		protected abstract void doAction();
	}
	ShortcutItem  shortcutXCJLY = new ShortcutItem(R.drawable.new_home_icon_jly,R.string.home_item_xcjly){
		@Override
		protected void doAction() {
			startActivity(mContext, "com.hilan.carrecorder", "com.hilan.carrecorder.activity.MainActivity", null);
		}
	};
	ShortcutItem  shortcutGP = new ShortcutItem(R.drawable.home_more_gupiao,R.string.home_more_gupiao){
		@Override
		protected void doAction() {
			sendBroadcast(new Intent("action.start.recorder"));
			//sendBroadcast(new Intent("com.hilan.car.exit"));
		}
	};
	ShortcutItem  shortcutDHANG = new ShortcutItem(R.drawable.new_home_icon_dhang,R.string.home_item_dhang){
		@Override
		protected void doAction() {
			//startActivity(mContext, "com.baidu.navi.hd", "com.baidu.navi.NaviActivity", null);
			startActivity(mContext,"com.baidu.BaiduMap", "com.baidu.baidumaps.WelcomeScreen", null);
		}
	};
	ShortcutItem  shortcutYYUE = new ShortcutItem(R.drawable.new_home_icon_yy,R.string.home_item_yy){
		@Override
		protected void doAction() {
			//startActivity(mContext, "com.android.music", "com.android.music.MusicBrowserActivity", null);
			startActivity(mContext, "com.mapgoo.music", "com.mapgoo.music.MusicActivity", null);
		}
	};
	ShortcutItem  shortcutYYZS = new ShortcutItem(R.drawable.home_item_yyzs,R.string.home_item_yyzs){
		@Override
		protected void doAction() {
			//startActivity(new Intent(mContext, VoliceRecActivity.class));
			//startActivity(new Intent(mContext, NaviAdrSelectActivity.class));
		}
	};
	ShortcutItem  shortcutDZG = new ShortcutItem(R.drawable.new_home_icon_dzg,R.string.home_item_dzg){
		@Override
		protected void doAction() {
			//startActivity(mContext, "com.edog", "com.edog.activity.SplashActivity", null);
			startActivity(mContext, "com.mapgoo.dzgmg", "com.mapgoo.dzgmg.MainActivity", null);
		}
	};
	ShortcutItem  shortcutDHUA = new ShortcutItem(R.drawable.new_home_icon_dhua,R.string.home_item_dhua){
		@Override
		protected void doAction() {
			startActivity(mContext, "com.concox.bluetooth", "com.concox.bluetooth.MainActivity", null);
		}
	};
	ShortcutItem  shortcutGD = new ShortcutItem(R.drawable.home_item_gd,R.string.home_item_gd){
		@Override
		protected void doAction() {
			startActivity(new Intent(mContext,HomeMoreActivity.class));
			//sendBroadcast(new Intent("action.start.recorder"));
		}
	};
	ShortcutItem  shortcutXCZS = new ShortcutItem(R.drawable.new_home_icon_xczs,R.string.home_more_xczs){
		@Override
		protected void doAction(){
			if(startActivity(mContext,"com.mapgoo.diruite", "com.example.cloudmirror.ui.MainActivity", null) == false)
			 startActivity(mContext,"com.mapgoo.rcx.core", "com.example.cloudmirror.ui.RcxCoreActivity", getString(R.string.home_more_zhushou));
		}
	};
	ShortcutItem  shortcutWZCX = new ShortcutItem(R.drawable.new_home_icon_wzcx,R.string.home_more_wzcx){
		@Override
		protected void doAction(){
			Intent intent = new Intent().setClassName("com.mapgoo.diruite", "com.example.cloudmirror.ui.MainActivity");
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			intent.putExtra("starttag", "weizhang");
			try {
				mContext.startActivity(intent);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	};
	ShortcutItem  shortcutJYZ = new ShortcutItem(R.drawable.new_home_icon_jyz,R.string.home_more_jyz){
		@Override
		protected void doAction(){
			startActivity(new Intent(mContext, GasStationActivity.class));
		}
	};
	ShortcutItem  shortcutSZ = new ShortcutItem(R.drawable.new_home_icon_sz,R.string.home_more_sz){
		@Override
		protected void doAction(){
			startActivity("com.android.settings","com.android.settings.Settings",null);
		}
	};
	ShortcutItem  shortcutGY = new ShortcutItem(R.drawable.new_home_icon_gy,R.string.home_more_gy){
		@Override
		protected void doAction(){
			startActivity(new Intent(mContext, AboutActivity.class));
		}
	};
	ShortcutItem  shortcutSP = new ShortcutItem(R.drawable.new_home_icon_sp,R.string.home_more_sp){
		@Override
		protected void doAction(){
			startActivity("com.mediatek.videoplayer","com.mediatek.videoplayer.MovieListActivity",null);
		}
	};
	ShortcutItem  shortcutDT = new ShortcutItem(R.drawable.new_home_icon_dt,R.string.home_more_dt){
		@Override
		protected void doAction(){
			startActivity("com.itings.myradio","com.itings.myradio.kaolafm.home.FlashActivity",null);
		}
	};
	ShortcutItem  shortcutFM = new ShortcutItem(R.drawable.new_home_icon_fm,R.string.home_more_fm){
		@Override
		protected void doAction(){
			startActivity("com.hilan.fm82","com.hilan.fm.activity.MainActivity",null);
		}
	};
	private void getRectForRecorder(){
		if(QuickShPref.getInstance().getInt(QuickShPref.WIDTH)<=0){
			View p = findViewById(R.id.function_item_4);
			ImageView r = (ImageView)p.findViewById(R.id.home_more_item_icon);
			Rect rect = new Rect();
			
			r.getGlobalVisibleRect(rect);	

			MyLog.D("top="+rect.top +",left="+rect.left+",width="+rect.width()+",height="+rect.height());
			
			int t = rect.top;
			int l = rect.left;
			int w = rect.width();
			int h = rect.height();
			int d = w - h;
			w = h;
			l = l + d/2;
			
			QuickShPref.getInstance().putValueObject(QuickShPref.TOP, t);
			QuickShPref.getInstance().putValueObject(QuickShPref.LEFT, l);
			QuickShPref.getInstance().putValueObject(QuickShPref.WIDTH, w);
			QuickShPref.getInstance().putValueObject(QuickShPref.HEIGHT, h);
		}
	}
	
	Runnable mGetRecoderRectRun = new Runnable(){
		@Override
		public void run() {
			getRectForRecorder();
		}
	};
	Runnable mShowRecoderRectRun = new Runnable(){
		@Override
		public void run() {
			sendBroadcast(new Intent(ACTION_HOEM_VISIABLE_CHANGE).putExtra("visiable", true));
		}
	};
	
    LocationClient mLocClient;
    BDLocation mBDLocation;
    LatLng mLocLatLng;
    MyLocationListenner myListener= new MyLocationListenner();
    private void locationInit(){
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		//option.setOpenGps(true);// 打开gps
		option.setLocationMode(LocationMode.Hight_Accuracy);
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(30000);
		option.setIsNeedAddress(true);
		mLocClient.setLocOption(option);
		mLocClient.start(); 
    }
    
	public class MyLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)	{ return;}
			if(location.getLatitude() != Double.MIN_VALUE){
				//mLocClient.stop();
				mBDLocation = location;
				mLocLatLng = LatlngFactory.CreatefromDouble(location.getLatitude(), location.getLongitude());
				if(!mGetWeatherSuccess){
					//getWeather(location);
					mGetWeatherSuccess = true;
				}
				MyLog.D("主界面定位 lat="+location.getLatitude()+",lon="+location.getLongitude());
			}
		}
		public void onReceivePoi(BDLocation poiLocation) {}
	}
	
	private boolean mGetWeatherSuccess;
	public void getWeather(BDLocation location){
		if(location!=null)
			ApiClient.getWeather(String.valueOf(location.getLongitude()), String.valueOf(location.getLatitude()), null, new Listener<JSONObject>() {
				@Override
				public void onResponse(JSONObject response) {
					// TODO Auto-generated method stub
					MyLog.D(""+response.toString());
					try {
						Weather weather = JSON.parseObject(response.getString("result"), Weather.class);
						((TextView)findViewById(R.id.home_weather_loaction)).setText(weather.getCity());
						((TextView)findViewById(R.id.home_weather_temple)).setText(weather.getTemperature());
						((TextView)findViewById(R.id.home_weather_txt)).setText(weather.getWeather());
						((AsyncImageView)findViewById(R.id.iv_weather_icon)).setImage(weather.getWeather_icon_url(), R.drawable.weather_default, new RoundedRectangleBitmapDisplayer(0));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}, GlobalNetErrorHandler.getInstance(mContext, null, null));
	}
	
	public void getMusic(String str){
		ApiClient.getMusic(str,null,new Listener<String>() {
			@Override
			public void onResponse(String response) {
				// TODO Auto-generated method stub
				MyLog.D(""+response.toString());
				getMusicAdr(response);
			}
		}, GlobalNetErrorHandler.getInstance(mContext, null, null));
	}
	private void getMusicAdr(String response){
		Pattern pattern = Pattern
				.compile(".+<url><encode><.\\[CDATA\\[(.*)\\]\\]></encode><decode><.\\[CDATA\\[(.*mid.*)\\]\\]></decode><type>.</type><lrcid>.</lrcid><flag>.</flag></url>.+");// *MG2000CH(.),23.124,nskdh#
		Matcher matcher = pattern.matcher(response);
		if(matcher.find()){
			startBrowser(matcher.group(1)+matcher.group(2));
			MyLog.D("mTakePerson:"+matcher.group(1)+matcher.group(2));
		}else{
			MyLog.D("not match:"+response);
		}
	}
	private void startBrowser(String url) {
			Intent it = new Intent(Intent.ACTION_VIEW);
			Uri uri = Uri.parse(url);
			it.setDataAndType(uri, "audio/*");
			startActivity(it);
	}
}
