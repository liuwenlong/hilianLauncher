package com.example.cloudmirror.ui;

import java.util.ArrayList;
import java.util.List;
import com.mapgoo.diruite.R;
import com.example.cloudmirror.service.DataSyncService;
import com.example.cloudmirror.ui.activity.CarBrandUpdateActivity;
import com.example.cloudmirror.ui.activity.GetInvadationCodeActivity;
import com.example.cloudmirror.ui.widget.AsyncImageView;
import com.example.cloudmirror.ui.widget.FlipperIndicatorDotView;
import com.example.cloudmirror.ui.widget.RoundedRectangleBitmapDisplayer;
import com.example.cloudmirror.ui.widget.SimpleDialogBuilder;
import com.example.cloudmirror.utils.MyLog;
import com.example.cloudmirror.utils.StringUtils;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;


public class MainActivity extends BaseActivity {

	private ViewPager mViewPager;
	private FlipperIndicatorDotView mIndicatorrView ;
	@Override
	protected void setContentView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_main);
		mContext = MainActivity.this;
		
		
	}

	@Override
	protected void initData(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void initViews() {
		// TODO Auto-generated method stub
		mViewPager = (ViewPager)findViewById(R.id.vPager);
		mIndicatorrView = (FlipperIndicatorDotView)findViewById(R.id.vPager_Indicator);
	}

	@Override
	protected void handleData() {
		// TODO Auto-generated method stub
		 loadImages();
		 getPhoneNum();
		 startService(new Intent(this, DataSyncService.class));
	}
	
	private void loadImages(){
		ArrayList<View> pageViews = new ArrayList<View>();
		for (int i = 0; i < 1; i++) {
			View adView = mInflater.inflate(R.layout.home_ad_info, null); 
			AsyncImageView adImgView = (AsyncImageView) adView.findViewById(R.id.ad_img);
			adImgView.setImage("http://imgsrc.baidu.com/forum/pic/item/d4d50c381f30e924b696bed34c086e061c95f77b.jpg", R.drawable.loading_bg, new RoundedRectangleBitmapDisplayer(0));
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
        public void destroyItem(ViewGroup container, int position, Object object)   {     
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
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}  

	String mTelNum,mTelImei;
	private void getPhoneNum(){
		TelephonyManager deviceinfo = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		mTelNum = deviceinfo.getLine1Number();
		mTelImei = deviceinfo.getDeviceId();
		
		MyLog.D("mTelNum="+mTelNum+",mTelImei="+mTelImei);
		if(StringUtils.isEmpty(mTelNum)){
			showRegister();
			showCarInfo();
		}
	}
	
	public void showRegister(){
		TextView alertTextView = (TextView) View.inflate(this,R.layout.layout_alert_dialog_view, null);
		alertTextView.setText("亲，您还没有注册哦！");
		
		new SimpleDialogBuilder(this).setContentView(alertTextView)
		.setNegativeButton("取消",new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				})
				.setPositiveButton("去注册", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						startActivity(new Intent(mContext, GetInvadationCodeActivity.class));
					}
				}).create().show();		
	}
	public void showCarInfo(){
		TextView alertTextView = (TextView) View.inflate(this,R.layout.layout_alert_dialog_view, null);
		alertTextView.setText("亲，您还没有完善资料！");
		
		new SimpleDialogBuilder(this).setContentView(alertTextView)
		.setNegativeButton("取消",new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				})
				.setPositiveButton("去完善", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						startActivity(new Intent(mContext, CarBrandUpdateActivity.class));
					}
				}).create().show();		
	}
	
	public void msgClick(View v){
		startActivity(new Intent(mContext, MsgListActivity.class));
	}
	
}
