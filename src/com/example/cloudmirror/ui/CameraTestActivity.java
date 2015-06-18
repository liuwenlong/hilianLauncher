package com.example.cloudmirror.ui;

import java.io.IOException;

import com.example.cloudmirror.utils.MyLog;
import com.mapgoo.eagle.R;

import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;

public class CameraTestActivity extends BaseActivity implements Callback{
	private Camera mCamera;  
    private boolean mPreviewRunning = true; 
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void setContentView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_camera_test);
	}

	@Override
	protected void initData(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void initViews() {
		// TODO Auto-generated method stub
		iniCamera();
	}

	@Override
	protected void handleData() {
		// TODO Auto-generated method stub
		
	}
	
	private void iniCamera(){
		SurfaceView mSurfaceView;
		mSurfaceView = (SurfaceView) findViewById(R.id.surface_camera); 
		SurfaceHolder mSurfaceHolder = mSurfaceView.getHolder(); 
		mSurfaceHolder.addCallback(this); 
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {
		// TODO Auto-generated method stub
        Camera.Parameters p = mCamera.getParameters();  
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

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		try{
			mCamera = Camera.open(); 
		}catch(Exception e){
			e.printStackTrace();
		}
		
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
