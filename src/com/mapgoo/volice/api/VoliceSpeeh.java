package com.mapgoo.volice.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Environment;

import com.baidu.speechsynthesizer.SpeechSynthesizer;
import com.baidu.speechsynthesizer.SpeechSynthesizerListener;
import com.baidu.speechsynthesizer.publicutility.DataInfoUtils;
import com.baidu.speechsynthesizer.publicutility.SpeechError;
import com.baidu.speechsynthesizer.publicutility.SpeechLogger;
import com.mapgoo.eagle.R;

public class VoliceSpeeh implements SpeechSynthesizerListener {
	private Activity mActivity;
	private SpeechSynthesizer speechSynthesizer;
	private OnSpeechChangeListener mOnSpeechChangeListener;
	public static interface OnSpeechChangeListener{
		public void OnSpeechChangeListener(SpeechSynthesizer sp,int what,Object arg);
	}
	
    /** 指定license路径，需要保证该路径的可读写权限 */
    private static final String LICENCE_FILE_NAME = Environment.getExternalStorageDirectory()
            + "/tts/baidu_tts_licence.dat";
    public VoliceSpeeh(Activity a,OnSpeechChangeListener l){
    	mActivity = a;
    	mOnSpeechChangeListener = l;
    	init();
    }
    public void init(){
        // 部分版本不需要BDSpeechDecoder_V1
        try {
            System.loadLibrary("BDSpeechDecoder_V1");
        } catch (UnsatisfiedLinkError e) {
            SpeechLogger.logD("load BDSpeechDecoder_V1 failed, ignore");
        }
        System.loadLibrary("bd_etts");
	    System.loadLibrary("bds");
        
        if (!new File(LICENCE_FILE_NAME).getParentFile().exists()) {
            new File(LICENCE_FILE_NAME).getParentFile().mkdirs();
        }
        // 复制license到指定路径
        InputStream licenseInputStream = mActivity.getResources().openRawResource(R.raw.temp_license_2015_06_12);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(LICENCE_FILE_NAME);
            byte[] buffer = new byte[1024];
            int size = 0;
            while ((size = licenseInputStream.read(buffer, 0, 1024)) >= 0) {
                SpeechLogger.logD("size written: " + size);
                fos.write(buffer, 0, size);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                licenseInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        speechSynthesizer =
                SpeechSynthesizer.newInstance(SpeechSynthesizer.SYNTHESIZER_AUTO, mActivity,"holder", this);
        // 请替换为开放平台上申请的apikey和secretkey
        speechSynthesizer.setApiKey("8tu3QOsGNqfOxDS2nu8wDiOT", "UykqHwI1OPskgVMymH5YXHfjGFASrYrY");
        // 设置授权文件路径
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_LICENCE_FILE, LICENCE_FILE_NAME);
        // TTS所需的资源文件，可以放在任意可读目录，可以任意改名
        String ttsTextModelFilePath = mActivity.getApplicationInfo().dataDir + "/lib/libbd_etts_text.dat.so";
        String ttsSpeechModelFilePath = mActivity.getApplicationInfo().dataDir + "/lib/libbd_etts_speech_female.dat.so";
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, ttsTextModelFilePath);
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, ttsSpeechModelFilePath);
        DataInfoUtils.verifyDataFile(ttsTextModelFilePath);
        DataInfoUtils.getDataFileParam(ttsTextModelFilePath, DataInfoUtils.TTS_DATA_PARAM_DATE);
        DataInfoUtils.getDataFileParam(ttsTextModelFilePath, DataInfoUtils.TTS_DATA_PARAM_SPEAKER);
        DataInfoUtils.getDataFileParam(ttsTextModelFilePath, DataInfoUtils.TTS_DATA_PARAM_GENDER);
        DataInfoUtils.getDataFileParam(ttsTextModelFilePath, DataInfoUtils.TTS_DATA_PARAM_CATEGORY);
        DataInfoUtils.getDataFileParam(ttsTextModelFilePath, DataInfoUtils.TTS_DATA_PARAM_LANGUAGE);
        speechSynthesizer.initEngine();
        mActivity. setVolumeControlStream(AudioManager.STREAM_MUSIC);  	
    }
	@Override
	public void onBufferProgressChanged(SpeechSynthesizer arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onCancel(SpeechSynthesizer arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onError(SpeechSynthesizer arg0, SpeechError arg1) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onNewDataArrive(SpeechSynthesizer arg0, byte[] arg1,
			boolean arg2) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onSpeechFinish(SpeechSynthesizer arg0) {
		// TODO Auto-generated method stub
		mOnSpeechChangeListener.OnSpeechChangeListener(arg0, 1, null);
	}
	@Override
	public void onSpeechPause(SpeechSynthesizer arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onSpeechProgressChanged(SpeechSynthesizer arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onSpeechResume(SpeechSynthesizer arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onSpeechStart(SpeechSynthesizer arg0) {
		// TODO Auto-generated method stub
		mOnSpeechChangeListener.OnSpeechChangeListener(arg0, 0, null);
	}
	@Override
	public void onStartWorking(SpeechSynthesizer arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onSynthesizeFinish(SpeechSynthesizer arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void startSpeaker(String str){
		 speechSynthesizer.speak(str);
	}
}
