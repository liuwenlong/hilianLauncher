package com.example.cloudmirror.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.cloudmirror.utils.MyLog;
import com.mapgoo.eagle.R;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.IBinder;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class FxService extends Service implements Callback {
	public static final String ACTION_START_RECORD = "action.start.record";
	public static final String ACTION_RECORD_COMPLETE = "action.record.complete";
	public static final int ACTION_RECORD_DUR = 10*1000;
	
	private Camera mCamera;
	private String mRecordPath = null;
	private boolean mPreviewRunning = true;
	MediaRecorder mediarecorder;
	SurfaceHolder mSurfaceHolder;
	// 定义浮动窗口布局
	LinearLayout mFloatLayout;
	WindowManager.LayoutParams wmParams;
	// 创建浮动窗口设置布局参数的对象
	WindowManager mWindowManager;

	Button mFloatView;

	private static final String TAG = "FxService";

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		MyLog.D("FxService oncreat");
		createFloatView();
		registerReceiver(mReceiver, new IntentFilter(ACTION_START_RECORD));
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	private void createFloatView() {
		wmParams = new WindowManager.LayoutParams();
		// 获取的是WindowManagerImpl.CompatModeWrapper
		mWindowManager = (WindowManager) getApplication().getSystemService(
				getApplication().WINDOW_SERVICE);
		Log.i(TAG, "mWindowManager--->" + mWindowManager);
		// 设置window type
		wmParams.type = LayoutParams.TYPE_PHONE;
		// 设置图片格式，效果为背景透明
		wmParams.format = PixelFormat.RGBA_8888;
		// 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
		wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE|LayoutParams.FLAG_NOT_TOUCHABLE;
		// 调整悬浮窗显示的停靠位置为左侧置顶
		wmParams.gravity = Gravity.LEFT | Gravity.TOP;
		// 以屏幕左上角为原点，设置x、y初始值，相对于gravity
		wmParams.x = 0;
		wmParams.y = 0;

		// 设置悬浮窗口长宽数据
		wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

		/*
		 * // 设置悬浮窗口长宽数据 wmParams.width = 200; wmParams.height = 80;
		 */

		LayoutInflater inflater = LayoutInflater.from(getApplication());
		// 获取浮动窗口视图所在布局
		mFloatLayout = (LinearLayout) inflater.inflate(R.layout.activity_camera_test, null);
		// 添加mFloatLayout
		
		mWindowManager.addView(mFloatLayout, wmParams);
		// 浮动窗口按钮
//		mFloatView = (Button) mFloatLayout.findViewById(R.id.test_camera_btn);
//
//		mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
//				View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
//				.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//		Log.i(TAG, "Width/2--->" + mFloatView.getMeasuredWidth() / 2);
//		Log.i(TAG, "Height/2--->" + mFloatView.getMeasuredHeight() / 2);
//		// 设置监听浮动窗口的触摸移动
//		mFloatView.setOnTouchListener(new OnTouchListener() {
//
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				// TODO Auto-generated method stub
//				// getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
//				wmParams.x = (int) event.getRawX()
//						- mFloatView.getMeasuredWidth() / 2;
//				Log.i(TAG, "RawX" + event.getRawX());
//				Log.i(TAG, "X" + event.getX());
//				// 减25为状态栏的高度
//				wmParams.y = (int) event.getRawY()
//						- mFloatView.getMeasuredHeight() / 2 - 25;
//				Log.i(TAG, "RawY" + event.getRawY());
//				Log.i(TAG, "Y" + event.getY());
//				// 刷新
//				mWindowManager.updateViewLayout(mFloatLayout, wmParams);
//				return false; // 此处必须返回false，否则OnClickListener获取不到监听
//			}
//		});
//
//		mFloatView.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Toast.makeText(FxService.this, "onClick", Toast.LENGTH_SHORT)
//						.show();
//			}
//		});

		iniCamera();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mFloatLayout != null) {
			// 移除悬浮窗口
			mWindowManager.removeView(mFloatLayout);
		}
		unregisterReceiver(mReceiver);
	}
	SurfaceView mSurfaceView;
	private void iniCamera(){
		mSurfaceView = (SurfaceView) mFloatLayout.findViewById(R.id.surfaceview);
		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.addCallback(this);
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		//mHandler.postDelayed(mStopRecordRun, 3000);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		if (mCamera == null)
			return;
		// Camera.Parameters p = mCamera.getParameters();
		// p.setPreviewSize(width, height);
		// p.set("rotation", 90);
		// mCamera.setParameters(p);
		try {
			mCamera.setPreviewDisplay(holder);
		} catch (IOException e) {
			e.printStackTrace();
		}

		mCamera.startPreview();
		mPreviewRunning = true;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		if (mCamera == null&&holder==null) {
			try {
				mCamera = Camera.open();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		mFloatLayout.findViewById(R.id.lay).setVisibility(View.INVISIBLE);
		MyLog.D("Fxservice surfaceCreated");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		if (mCamera == null)
			return;
		mCamera.stopPreview();
		mPreviewRunning = false;
		mCamera.release();
		mCamera = null;
	}

	public void startPreview() {
		surfaceCreated(null);
		surfaceChanged(mSurfaceHolder, 0, 0, 0);
	}

	/**
	 * 开始录像
	 */
	public void startRecord() {
		startPreview();
		if(mCamera == null)
			return;
		mediarecorder = new MediaRecorder();// 创建mediarecorder对象

		// 解锁camera
		mCamera.unlock();
		mediarecorder.setCamera(mCamera);

		// 设置录制视频源为Camera(相机)
		mediarecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
		mediarecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

		// 设置录制文件质量，格式，分辨率之类，这个全部包括了
		mediarecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_LOW));
		mediarecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
		
		// 设置视频文件输出的路径
		mRecordPath = getVideoPath();
		mediarecorder.setOutputFile(mRecordPath);
		try {
			// 准备录制
			mediarecorder.prepare();
			// 开始录制
			mediarecorder.start();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MyLog.D("开始录像成功");
	}
	
	private String getVideoPath(){
	    SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
	    String t=format.format(new Date());
	    return "/sdcard/"+t+".3gp";
	}
	
	/**
	 * 停止录制
	 */
	public void stopRecord() {
		if (mediarecorder != null) {
			// 停止录制
			mediarecorder.stop();
			// 释放资源
			mediarecorder.release();
			mediarecorder = null;
			surfaceDestroyed(mSurfaceHolder);
			startService(new Intent(getBaseContext(), DataSyncService.class).putExtra(DataSyncService.COMMAND, DataSyncService.COMMAND_START));
			mSurfaceView.setVisibility(View.INVISIBLE);
		}
	}
	
	Handler mHandler = new Handler();
	Runnable mStopRecordRun = new Runnable(){
		@Override
		public void run() {
			// TODO Auto-generated method stub
			stopRecord();
			MyLog.D("录像时间到了:"+mRecordPath);
		}
	};
	Runnable mSartRecordRun = new Runnable(){
		@Override
		public void run() {
			// TODO Auto-generated method stub
			startRecord();
		}
	};
	BroadcastReceiver mReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent){
			// TODO Auto-generated method stub
			startService(new Intent(getBaseContext(), DataSyncService.class).putExtra(DataSyncService.COMMAND, DataSyncService.COMMAND_STOP));
			mHandler.postDelayed(mSartRecordRun, 500);
			mHandler.postDelayed(mStopRecordRun, ACTION_RECORD_DUR);
		}
	};
}
