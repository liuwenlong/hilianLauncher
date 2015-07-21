package com.example.cloudmirror.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.android.volley.Response.Listener;
import com.example.cloudmirror.api.ApiClient;
import com.example.cloudmirror.api.GlobalNetErrorHandler;
import com.example.cloudmirror.api.GlobalNetErrorHandler.GlobalNetErrorCallback;
import com.example.cloudmirror.application.MGApp;
import com.example.cloudmirror.ui.MainActivity;
import com.example.cloudmirror.utils.CamParaUtil;
import com.example.cloudmirror.utils.ImageUtils;
import com.example.cloudmirror.utils.MyLog;
import com.example.cloudmirror.widget.ShakeListener;
import com.example.cloudmirror.widget.ShakeListener.OnShakeListener;
import com.mapgoo.eagle.R;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Environment;
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
	public static final String ACTION_START_TAKE_PIC = "action.start.take.pic";
	public static final String ACTION_START_AUDIO = "action.start.audio.recorder";
	public static final String ACTION_RECORD_COMPLETE = "action.record.complete";
	public static final String ACTION_TAKEPIC_COMPLETE = "action.takepic.complete";
	public static final int ACTION_RECORD_DUR = 6 * 1000;
	private Context mContext;
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
	ShakeListener mShakeListener;
	Button mFloatView;

	private static final String TAG = "FxService";

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mContext = this;
		MyLog.D("FxService oncreat");

		createFloatView();

		registerRecv();
		
		regestShake();
	}

	private void regestShake(){
		mShakeListener = new ShakeListener(this);  
        
        mShakeListener.setOnShakeListener(new OnShakeListener() {  
              
            public void onShake() {
            	mShakeListener.stop();
            	 Toast.makeText(mContext, "呵呵，成功了！。\n再试一次吧！", Toast.LENGTH_SHORT).show();  
                new Handler().postDelayed(new Runnable() {
                    @Override  
                    public void run() {
                    	mShakeListener.start();
                    }
                }, 3000);  
            }  
        });  
	}
	
	private void registerRecv() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_START_RECORD);
		filter.addAction(ACTION_START_TAKE_PIC);
		filter.addAction(ACTION_START_AUDIO);
		registerReceiver(mReceiver, filter);
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
		wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE
				| LayoutParams.FLAG_NOT_TOUCHABLE;
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
		// mFloatView = (Button)
		// mFloatLayout.findViewById(R.id.test_camera_btn);
		//
		// mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
		// View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
		// .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
		// Log.i(TAG, "Width/2--->" + mFloatView.getMeasuredWidth() / 2);
		// Log.i(TAG, "Height/2--->" + mFloatView.getMeasuredHeight() / 2);
		// // 设置监听浮动窗口的触摸移动
		// mFloatView.setOnTouchListener(new OnTouchListener() {
		//
		// @Override
		// public boolean onTouch(View v, MotionEvent event) {
		// // TODO Auto-generated method stub
		// // getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
		// wmParams.x = (int) event.getRawX()
		// - mFloatView.getMeasuredWidth() / 2;
		// Log.i(TAG, "RawX" + event.getRawX());
		// Log.i(TAG, "X" + event.getX());
		// // 减25为状态栏的高度
		// wmParams.y = (int) event.getRawY()
		// - mFloatView.getMeasuredHeight() / 2 - 25;
		// Log.i(TAG, "RawY" + event.getRawY());
		// Log.i(TAG, "Y" + event.getY());
		// // 刷新
		// mWindowManager.updateViewLayout(mFloatLayout, wmParams);
		// return false; // 此处必须返回false，否则OnClickListener获取不到监听
		// }
		// });
		//
		// mFloatView.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// Toast.makeText(FxService.this, "onClick", Toast.LENGTH_SHORT)
		// .show();
		// }
		// });

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
		mShakeListener.stop();
	}

	SurfaceView mSurfaceView;

	private void iniCamera() {
		mSurfaceView = (SurfaceView) mFloatLayout
				.findViewById(R.id.surfaceview);
		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.addCallback(this);
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		// mHandler.postDelayed(mStopRecordRun, 3000);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		if (mCamera == null)
			return;
		 Camera.Parameters p = mCamera.getParameters();
//		 p.setPreviewSize(width, height);
//		 p.set("rotation", 90);
		 List<Size> list=  p.getSupportedPictureSizes();
		 int min = 10000;
		 int index = 0;
		 for(int i=0;i<list.size();i++){
			 MyLog.D("i="+i+",width="+list.get(i).width+",height="+list.get(i).height);
			 int w = list.get(i).width;
			 int abs = Math.abs(mPicSize - w);
			 if(abs < min){
				 min = abs;
				 index = i;
			 }
		 }
		 MyLog.D("select index="+index);
		 p.setPictureSize(list.get(index).width, list.get(index).height);
		 
		 mCamera.setParameters(p);
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
		if (mCamera == null && holder == null) {
			try {
				mCamera = Camera.open();
				mSurfaceView.setVisibility(View.VISIBLE);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
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
		mSurfaceView.setVisibility(View.INVISIBLE);
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
		if (mCamera == null){
			sendCameraEorror("相机错误");
			return;
		}
		mediarecorder = new MediaRecorder();// 创建mediarecorder对象

		// 解锁camera
		mCamera.unlock();
		mediarecorder.setCamera(mCamera);

		// 设置录制视频源为Camera(相机)
		mediarecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
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
			// 10秒后停止录制
			mHandler.postDelayed(mStopRecordRun, ACTION_RECORD_DUR);
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			sendCameraEorror("相机错误");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			sendCameraEorror("相机错误");
		}
		MyLog.D("开始录像成功");
	}

	private String getVideoPath() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
		String t = format.format(new Date());
		return Environment.getExternalStorageDirectory().getPath() + "/" + t + ".3gp";
	}
	private String getAudioPath() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
		String t = format.format(new Date());
		return Environment.getExternalStorageDirectory().getPath() + "/" + t
				+ ".amr";
	}
	/**
	 * 停止录制
	 */
	public void stopRecord() {
		if (mediarecorder != null) {
			try {
				// 停止录制
				mediarecorder.stop();
				// 释放资源
				mediarecorder.release();
				mediarecorder = null;				
			}catch(IllegalStateException e){
				e.printStackTrace();
				sendCameraEorror("相机错误");
			}
			surfaceDestroyed(mSurfaceHolder);
			startService(new Intent(getBaseContext(), DataSyncService.class).putExtra(DataSyncService.COMMAND,DataSyncService.COMMAND_START));
		}
	}

	private void takePic(){
		startPreview();
		if(mCamera == null){
			sendCameraEorror("相机错误");
			return;
		}
		Log.i(TAG, "mCamera.takePicture...");
		mHandler.postDelayed(mDoTakePicRun, 500);
	}

	PictureCallback mJpegPictureCallback = new PictureCallback(){
		public void onPictureTaken(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
			Log.i(TAG, "myJpegCallback:onPictureTaken...");
			Bitmap b = null;
			if (null != data) {
				b = BitmapFactory.decodeByteArray(data, 0, data.length);// data是字节数据，将其解析成位图
				mCamera.stopPreview();
			}
			// 保存图片到sdcard
			if (null != b) {
				saveBitmap(b);
				uploadImage(b);
			}else{
				sendCameraEorror("相机错误");
			}
			surfaceDestroyed(mSurfaceHolder);
			startCarRecoder();
			isBusy = false;
		}
	};

	public void saveBitmap(Bitmap bm) {
		Log.e(TAG, "保存图片");
		mRecordPath = getPictruePath();
		File f = new File(mRecordPath);
		if (f.exists()) {
			f.delete();
		}
		try {
			FileOutputStream out = new FileOutputStream(f);
			bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.flush();
			out.close();
			Log.i(TAG, "已经保存");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private String getPictruePath() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
		String t = format.format(new Date());
		return Environment.getExternalStorageDirectory().getPath() + "/" + t + ".jpg";
	}
	
	Handler mHandler = new Handler();
	Runnable mDoTakePicRun = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			mCamera.takePicture(null, null, mJpegPictureCallback);
		}
	};	
	Runnable mTakePicRun = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			takePic();
		}
	};
	Runnable mStopRecordRun = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			stopRecord();
			startCarRecoder();
			MyLog.D("录像时间到了:" + mRecordPath);
			isBusy = false;
			uploadVideo(mRecordPath);
		}
	};
	Runnable mSartRecordRun = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			startRecord();
		}
	};
	Runnable mSartAudioRun = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			initializeAudio();
		}
	};	
	MediaRecorder recorder;
	private void initializeAudio() {
        recorder = new MediaRecorder();// new出MediaRecorder对象  
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);  
        // 设置MediaRecorder的音频源为麦克风  
        recorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);  
        // 设置MediaRecorder录制的音频格式  
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);  
        // 设置MediaRecorder录制音频的编码为amr.  
		mRecordPath = getAudioPath();
        recorder.setOutputFile(mRecordPath);  
        // 设置录制好的音频文件保存路径  
        try {
            recorder.prepare();// 准备录制  
            recorder.start();// 开始录制  
            MyLog.D("--------->开始录制音频 mRecordPath="+mRecordPath);
            mHandler.postDelayed(mStopAudio, 10*1000);
        } catch (IllegalStateException e) {
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }
        
    }
	Runnable mStopAudio = new Runnable(){

		@Override
		public void run() {
			// TODO Auto-generated method stub
			stopAudio();
			startService(new Intent(getBaseContext(), DataSyncService.class).putExtra(DataSyncService.COMMAND,DataSyncService.COMMAND_START));
			isBusy = false;
		}
		
	};
	private void stopAudio(){
		if(recorder!=null){
			 recorder.stop();// 停止刻录  
	         // recorder.reset(); // 重新启动MediaRecorder.  
	         recorder.release(); // 刻录完成一定要释放资源  
	         // recorder = null;  
		}
	}
	
	private void stopCarRecoder() {
		sendBroadcast(new Intent("android.intent.action.concox.carrecorder.quit"));
	}

	private void startCarRecoder() {
		MainActivity.startToCarRecord(this, MainActivity.RECORD_ACTION,MainActivity.RECORD_MODE_HIDE);// 打开行车记录仪
	}

	boolean isBusy = false;
	BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			MyLog.D("onReceive action="+action);
			if(isBusy)
				return;
			isBusy = true;
			mHandler.removeCallbacks(resetBusy);
			mHandler.postDelayed(resetBusy, 25*1000);
			if (ACTION_START_TAKE_PIC.equals(action)) {
				stopCarRecoder();
				int size = intent.getIntExtra("size", 0);
				setPicSize(size);
				mHandler.postDelayed(mTakePicRun, 3 * 1000);
			} else if (ACTION_START_RECORD.equals(action)) {
				setPicSize(1);
				startService(new Intent(getBaseContext(), DataSyncService.class).putExtra(DataSyncService.COMMAND,DataSyncService.COMMAND_STOP));
				stopCarRecoder();
				mHandler.postDelayed(mSartRecordRun, 3 * 1000);
			} else if(ACTION_START_AUDIO.equals(action)){
				startService(new Intent(getBaseContext(), DataSyncService.class).putExtra(DataSyncService.COMMAND,DataSyncService.COMMAND_STOP));
				MyLog.D("FxService unknow action:" + action);
				mHandler.postDelayed(mSartAudioRun, 1 * 1000);
			}
			
		}
	};
	
	Runnable resetBusy = new Runnable() {
		public void run() {
			isBusy = false;
		}
	};
	
	private int mPicSize;
	public void setPicSize(int size){
		switch (size) {
			case 0:mPicSize = 640;break;
			case 1:mPicSize = 320;break;
			case 2:mPicSize = 480;break;
			default:break;
		}
	}
	public void uploadImage(final Bitmap bm){
		new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				
//				Bitmap debp = Bitmap.createScaledBitmap(bm, width, height, false);
//				MyLog.D("uploadImage start");
				
				String base64 = ImageUtils.img2Base64(MGApp.pThis, bm);
				MyLog.D("uploadImage gener base64");
				
				ApiClient.postImage(base64, null, new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
						MyLog.D("onResponse:"+response.toString());
						sendUpLoadComplete(response.toString());
					}
				}, GlobalNetErrorHandler.getInstance(mContext, null, null, new GlobalNetErrorCallback() {
					
					@Override
					public void OnNetErrorCallback(VolleyError error) {
						// TODO Auto-generated method stub
						sendCameraEorror("网络错误");
					}
				}));				
			}
		}.start();
	}
	public void uploadVideo(final String path){
		new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				MyLog.D("upload video start");
				
				String base64 = ImageUtils.file2Base64(MGApp.pThis, path);
				MyLog.D("upload video gener base64");
				
				ApiClient.postVideo(base64, null, new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
						MyLog.D("postVideo onResponse:"+response.toString());
						sendUpLoadComplete(response.toString());
					}
				}, GlobalNetErrorHandler.getInstance(mContext, null, null, new GlobalNetErrorCallback() {
					
					@Override
					public void OnNetErrorCallback(VolleyError error) {
						// TODO Auto-generated method stub
						sendCameraEorror("网络错误");
					}
				}));		
			}
		}.start();	
	}
	
	private void sendUpLoadComplete(String resp){
		sendBroadcast(new Intent("action.upload.complete").putExtra("response", resp));
	}
	private void sendCameraEorror(String error){
		isBusy = false;
		sendBroadcast(new Intent("action.upload.complete").putExtra("response", "url:"+error));
	}
}
