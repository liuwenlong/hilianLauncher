package com.mapgoo.volice.ui;

import java.io.Serializable;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.baidu.android.domain.ResultAnasy;
import com.baidu.android.domain.ResultAnasy.AnasyItem;
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
import com.baidu.mapapi.utils.route.RouteParaOption.EBusStrategyType;
import com.baidu.voicerecognition.android.DataUploader;
import com.baidu.voicerecognition.android.VoiceRecognitionClient;
import com.baidu.voicerecognition.android.VoiceRecognitionConfig;
import com.baidu.voicerecognition.android.VoiceRecognitionClient.VoiceClientStatusChangeListener;
import com.example.cloudmirror.ui.widget.VoliceInView;
import com.example.cloudmirror.utils.MyLog;
import com.example.cloudmirror.utils.QuickShPref;
import com.mapgoo.eagle.R;
import com.mapgoo.volice.api.Config;
import com.mapgoo.volice.api.Constants;
import com.mapgoo.volice.api.VoliceSpeeh;

import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class VoliceRecActivity extends ActionBarActivity {
	ResultAnasy mResultAnasy;
    private VoiceRecognitionClient mASREngine;
    private Handler mHandler = new Handler();
    private ListAdapter mListAdapter;
    private ListView mListView;
    /** 正在识别中 */
    private boolean isRecognition = false;
    /** 音量更新间隔 */
    private static final int POWER_UPDATE_INTERVAL = 100;
    /** 识别回调接口 */
    private MyVoiceRecogListener mListener = new MyVoiceRecogListener();
    VoliceSpeeh mVoliceSpeeh;
    VoliceInView voliceInView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_volice_recogin);
		initVolice();
		uploadContacts();
		locationInit();
		mListAdapter = new ListAdapter(getBaseContext(), mResultAnasy.mAnasyList);
		mListView = (ListView)findViewById(R.id.list);
		mListView.setAdapter(mListAdapter);
		voliceInView = (VoliceInView)findViewById(R.id.voliceInView);
		WifiManager wm = (WifiManager)getSystemService(Context.WIFI_SERVICE); 
		String m_szWLANMAC = wm.getConnectionInfo().getMacAddress();
		MyLog.D("MAC="+m_szWLANMAC);
	}
	
	private void initVolice(){
	       mASREngine = VoiceRecognitionClient.getInstance(this);
	       mASREngine.setTokenApis(Constants.API_KEY, Constants.SECRET_KEY);
	       mResultAnasy = new ResultAnasy(this){
				@Override
				public void reTry() {
					// TODO Auto-generated method stub
					startSpeak(null);
				}
	       };
	       mResultAnasy.addAnswer(getString(R.string.volice_start), new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				startVolice();
			}
		});
	}
	private void startVolice(){
        VoiceRecognitionConfig config = new VoiceRecognitionConfig();
        int prop = Config.CURRENT_PROP;
        // 输入法暂不支持语义解析
        if (prop == VoiceRecognitionConfig.PROP_INPUT) {
            prop = VoiceRecognitionConfig.PROP_SEARCH;
        }
        config.setProp(prop);
        config.setLanguage(Config.getCurrentLanguage());
        config.enableNLU();
        config.enableContacts();
        config.enableVoicePower(Config.SHOW_VOL); // 音量反馈。
        if (Config.PLAY_START_SOUND) {
            config.enableBeginSoundEffect(R.raw.bdspeech_recognition_start); // 设置识别开始提示音
        }
        if (Config.PLAY_END_SOUND) {
            config.enableEndSoundEffect(R.raw.bdspeech_speech_end); // 设置识别结束提示音
        }
        config.setSampleRate(VoiceRecognitionConfig.SAMPLE_RATE_8K); // 设置采样率
        // 下面发起识别
        int code = mASREngine.startVoiceRecognition(mListener, config);
        if (code != VoiceRecognitionClient.START_WORK_RESULT_WORKING) {
            Toast.makeText(this, getString(R.string.error_start, code),Toast.LENGTH_LONG).show();
        }
        
	}
    /**
     * 将语义结果中的资源单独展示
     * 
     * @param result
     */
    private void showResourceViewer(String result) {
       
        if (!TextUtils.isEmpty(result)) {

                JSONObject temp_json = null;
				try {
					temp_json = new JSONObject(result);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                 mResultAnasy.anasyJSON(temp_json, true);
                 mListAdapter.notifyDataSetChanged();
                 mListView.setSelection(mListAdapter .getCount()-1);
        }
    }
    /**
     * 重写用于处理语音识别回调的监听器
     */
    class MyVoiceRecogListener implements VoiceClientStatusChangeListener {

        @Override
        public void onClientStatusChange(int status, Object obj) {
            switch (status) {
            	// 语音识别实际开始，这是真正开始识别的时间点，需在界面提示用户说话。
                case VoiceRecognitionClient.CLIENT_STATUS_START_RECORDING:
                    isRecognition = true;
                    onVoiceRecognitionStart();
                    MyLog.D("语音识别实际开始");
                    break;
                 // 检测到语音起点    
                case VoiceRecognitionClient.CLIENT_STATUS_SPEECH_START: 
                    break;
                // 已经检测到语音终点，等待网络返回
                case VoiceRecognitionClient.CLIENT_STATUS_SPEECH_END:
                	MyLog.D("已经检测到语音终点，等待网络返回");
                	 onVoiceRecognitionStop();
                    break;
                // 语音识别完成，显示obj中的结果
                case VoiceRecognitionClient.CLIENT_STATUS_FINISH:
                	MyLog.D("语音识别完成");
                	noSpeakCount = 0;
                    isRecognition = false;
                    if (obj != null && obj instanceof List) {
                        List results = (List) obj;
                        if (results.size() > 0) {
                            String temp_str = results.get(0).toString();
                            MyLog.D(temp_str);
                            showResourceViewer(temp_str);
                        }
                    }else{
                    	MyLog.D("语音识别完成obj = null");
                    	mResultAnasy.addInputEorror(null);
                    }
                    onVoiceRecognitionStop();
                    break;
                // 用户取消
                case VoiceRecognitionClient.CLIENT_STATUS_USER_CANCELED:
                	MyLog.D("用户取消语音");
                    isRecognition = false;
                    onVoiceRecognitionStop();
                    break;
                default:
                    break;
            	}
        	}
            @Override
            public void onError(int errorType, int errorCode) {
                isRecognition = false;
                MyLog.D("语音 onError:errorCode="+errorCode);
                mResultAnasy.addAnswer("网络错误,请重试", new Runnable(){
					@Override
					public void run() {
						// TODO Auto-generated method stub
						startSpeak(null);
					}
                });
                mListAdapter.notifyDataSetChanged();
                onVoiceRecognitionStop();
            }

            @Override
            public void onNetworkStatusChange(int status, Object obj) {
                // 这里不做任何操作不影响简单识别
            }
        }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VoiceRecognitionClient.releaseInstance(); 
        if(mASREngine!=null){
        	mASREngine.stopVoiceRecognition();
        }
        BaiduMapRoutePlan.finish(this);
    }
    
    public void onClick(View v){
    	switch (v.getId()) {
			case R.id.volice_speeker_btn:
				startSpeak(v);
				break;
			case R.id.volice_tip_btn:
				if(findViewById(R.id.volice_tip_text).getVisibility()==View.VISIBLE){
					findViewById(R.id.volice_tip_text).setVisibility(View.GONE);
				}else{
					findViewById(R.id.volice_tip_text).setVisibility(View.VISIBLE);
				}
				break;
			default:
				break;
		}
    }
    
	public void startSpeak(View v){
		BaiduMapRoutePlan.finish(this);
		if(isRecognition){
			mASREngine.speakFinish();
		}else{
			startVolice();
		}
	}
	
    /**
     * 上传通讯录
     * */
    private void uploadContacts(){
    	boolean upload = QuickShPref.getInstance().getBoolean(QuickShPref.UPLOAD_CONTACTS);
    	if(upload){
    		
    	}else{
	    	DataUploader dataUploader = new DataUploader(this);
	    	dataUploader.setApiKey(Constants.API_KEY, Constants.SECRET_KEY);
	    	
	    	//String jsonString = "[{\"name\":\"蔡毓宏\", \"frequency\":1}, {\"name\":\"林新汝\", \"frequency\":2}, {\"name\":\"文胜\", \"frequency\":3}]";
	    	String jsonString = getContact();
	    	MyLog.D(jsonString);
	    	try{
	    		if(jsonString != null){
	    			dataUploader.uploadContactsData(jsonString.getBytes("utf-8"));
	    			QuickShPref.getInstance().putValueObject(QuickShPref.UPLOAD_CONTACTS, true);
	    		}
	    	}catch (Exception e){
	    		e.printStackTrace();
	    	}
    	}
    }
    
    public static class DataUploaderBean extends Object implements Serializable{
    	public String name;
    	public int frequency;
    	public DataUploaderBean(){}
    	public DataUploaderBean(String name,int p){
    		this.name = name;
    		frequency = p;
    	}
    }
    private String getContact(){
    	ArrayList<DataUploaderBean> list = new ArrayList<DataUploaderBean>();
    	String[] PHONES_PROJECTION = new String[] {
    	       Phone.DISPLAY_NAME, Phone.NUMBER, Phone.CONTACT_ID }; 
    	int count = 1;
    	Cursor phone = getContentResolver().query(Phone.CONTENT_URI, PHONES_PROJECTION, null, null, null);
    	if(phone!=null){
	    	for(int i=0;i<phone.getCount();i++){
	    		String name = getCursorString(phone, Phone.DISPLAY_NAME, i);
	    		DataUploaderBean item = new DataUploaderBean(name,count);
	    		list.add(item);
	    		count++;
	    	}
    	}
    	
    	Map<String, Object> reqBodyParams = new HashMap<String, Object>();
    	reqBodyParams.put("DataUploader", list);
    	com.alibaba.fastjson.JSONObject object = new com.alibaba.fastjson.JSONObject(reqBodyParams);

		return object.getJSONArray("DataUploader").toString();
    }
    
    public static  String getCursorString(Cursor cur,String key,int pos){
    	cur.moveToPosition(pos);
    	int index = cur.getColumnIndex(key);
    	return cur.getString(index);
    }
    
    class ListAdapter extends BaseAdapter{
    	ArrayList<AnasyItem> mAnasyList = new ArrayList<AnasyItem>();
    	Context mContext;
    	ListAdapter(Context c,ArrayList<AnasyItem> list){
    		mContext = c;
    		mAnasyList = list;
    	}
    	
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if(mAnasyList != null)
				return mAnasyList.size();
			return 0;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mAnasyList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if(convertView == null)
				convertView = View.inflate(mContext, R.layout.volice_talke_msg, null);
			AnasyItem item = mAnasyList.get(position);
			if(item.type == 0){
				convertView.findViewById(R.id.volice_right).setVisibility(View.VISIBLE);
				convertView.findViewById(R.id.volice_left).setVisibility(View.INVISIBLE);
				((TextView)convertView.findViewById(R.id.volice_right)).setText(item.content);
			}else{
				convertView.findViewById(R.id.volice_left).setVisibility(View.VISIBLE);
				convertView.findViewById(R.id.volice_right).setVisibility(View.INVISIBLE);
				((TextView)convertView.findViewById(R.id.volice_left)).setText(item.content);
			}
			
			return convertView;
		}
    }
    
    LocationClient mLocClient;
    MyLocationListenner myListener= new MyLocationListenner();
    private void locationInit(){
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		//option.setOpenGps(true);// 打开gps
		option.setLocationMode(LocationMode.Hight_Accuracy);
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(10000);
		option.setIsNeedAddress(true);
		mLocClient.setLocOption(option);

		mLocClient.start(); 
    }
	public class MyLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)	{ return;}
			if(location.getLatitude() != Double.MIN_VALUE)
				mBDLocation = location;
			MyLog.D("lat="+location.getLatitude()+",lon="+location.getLongitude()+",adr="+mBDLocation.getAddrStr());
		}
		public void onReceivePoi(BDLocation poiLocation) {}
	}
	
	public static LatLng getLocLatLng(){
		if(mBDLocation!=null){
			return new LatLng(mBDLocation.getLatitude(), mBDLocation.getLongitude());
		}
		return null;
	}
	
	public static BDLocation mBDLocation=null;
	
	private long maxVolume;
	private int noSpeakCount;
    private Runnable mUpdateVolume = new Runnable() {
        public void run() {
            if (isRecognition) {
                long vol = mASREngine.getCurrentDBLevelMeter();
                mHandler.removeCallbacks(mUpdateVolume);
                mHandler.postDelayed(mUpdateVolume, POWER_UPDATE_INTERVAL);
                if(vol>maxVolume)
                	maxVolume = vol;
                //MyLog.D("maxVolume="+maxVolume);
            }else{
            	maxVolume = 0;
            }
        }
    };
    private final long MinNoSpeakCheckVolume = 30;
    private final long TimeNoSpeakCheckVolume = 5000;
    private final long MaxNoSpeakNum = 3;   
    
    private Runnable mNoSpeakCheck = new Runnable() {
        public void run() {
            if (isRecognition) {
            	 MyLog.D("maxVolume="+maxVolume);
            	 if(maxVolume<MinNoSpeakCheckVolume){
            		 mASREngine.stopVoiceRecognition();
            		 noSpeakCount ++;
            		 if(noSpeakCount>=MaxNoSpeakNum){
            			 mResultAnasy.addAnswer("您没有说话,下次见", new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								finish();
								overridePendingTransition(0, 0);
							}
						});
            		 }else{
            			 mResultAnasy.addAnswer("您没有说话,请重试", new Runnable(){
							@Override
							public void run() {
								// TODO Auto-generated method stub
								startSpeak(null);
							}
            			 });
            		 }
            		 mListAdapter.notifyDataSetChanged();
            	 }
            }
        }
    };
    
    public void onVoiceRecognitionStart(){
    	mHandler.removeCallbacksAndMessages(this);
    	maxVolume = 0;
    	mHandler.post(mUpdateVolume);
    	mHandler.postDelayed(mNoSpeakCheck, TimeNoSpeakCheckVolume);
    	voliceInView.startAnim();
    }
    public void onVoiceRecognitionStop(){
    	mHandler.removeCallbacksAndMessages(this);
    	voliceInView.stopAnim();
    }
}
