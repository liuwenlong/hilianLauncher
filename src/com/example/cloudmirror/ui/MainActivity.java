package com.example.cloudmirror.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.mapgoo.eagle.R;
import com.mapgoo.volice.ui.VoliceRecActivity;
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
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
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
import android.widget.TextView;
import android.widget.ImageView.ScaleType;


public class MainActivity extends BaseActivity implements Callback {

	private ViewPager mViewPager;
	private FlipperIndicatorDotView mIndicatorrView ;
	private Camera mCamera;  
    private boolean mPreviewRunning = true; 
    
	@Override
	protected void setContentView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_main);
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
		//iniCamera();
	}

	@Override
	protected void handleData() {
		// TODO Auto-generated method stub
		 loadImages();
	}
	
	private void iniCamera(){
		SurfaceView mSurfaceView;
		mSurfaceView = (SurfaceView) findViewById(R.id.surface_camera); 
		SurfaceHolder mSurfaceHolder = mSurfaceView.getHolder(); 
		mSurfaceHolder.addCallback(this); 
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK && mTipViewIsShow){
			dismissTipView();
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
				break;
			case R.id.violation_tip_img:
				dismissTipView();
				break;
			case R.id.home_click_volice:
				startActivity(new Intent(mContext, VoliceRecActivity.class));
				break;
			default:
				break;
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
		if(mCamera == null)
			return;
		if (mPreviewRunning){
            mCamera.stopPreview();  
        }  
        Camera.Parameters p = mCamera.getParameters();  
        //p.setPreviewSize(width, height);  
        p.set("rotation", 90);  
       //mCamera.setParameters(p);  
        try{  
            mCamera.setPreviewDisplay(holder);  
        } catch (IOException e){
            e.printStackTrace();  
        }  
  
        mCamera.startPreview();  
        mPreviewRunning = true;  		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		mCamera = Camera.open(); 
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		if(mCamera == null)
			return;
		mCamera.stopPreview();  
        mPreviewRunning = false;  
        mCamera.release();		
	}
}
