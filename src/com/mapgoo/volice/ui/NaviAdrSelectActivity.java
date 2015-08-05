package com.mapgoo.volice.ui;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.android.domain.ResultAnasy;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.PoiInfo.POITYPE;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiBoundSearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionResult.SuggestionInfo;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.speechsynthesizer.SpeechSynthesizer;
import com.baidu.voicerecognition.android.VoiceRecognitionClient;
import com.baidu.voicerecognition.android.VoiceRecognitionClient.VoiceClientStatusChangeListener;
import com.baidu.voicerecognition.android.VoiceRecognitionConfig;
import com.example.cloudmirror.service.DataSyncService;
import com.example.cloudmirror.ui.MainActivity.MyLocationListenner;
import com.example.cloudmirror.ui.activity.GasStationActivity;
import com.example.cloudmirror.utils.MyLog;
import com.example.cloudmirror.utils.StringUtils;
import com.mapgoo.carlife.main.R;
import com.mapgoo.volice.api.Config;
import com.mapgoo.volice.api.Constants;
import com.mapgoo.volice.api.VoliceSpeeh;
import com.mapgoo.volice.api.VoliceSpeeh.OnSpeechChangeListener;

import de.greenrobot.event.EventBus;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class NaviAdrSelectActivity extends Activity implements
		VoiceClientStatusChangeListener, OnGetPoiSearchResultListener {
	private Handler mHandler = new Handler();
	private ListView mListView;
	private PoiSearch mPoiSearch;
	private Context mContext;
	private PoiListAdapter mPoiListAdapter;
	ProgressDialog mProgressDialog;
	
	private boolean isRecognition = false;
	private static final int POWER_UPDATE_INTERVAL = 100;
	ResultAnasy mResultAnasy;
	private VoiceRecognitionClient mASREngine;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_navi_select);
		locationInit();
		initSpeaker();
		initView();
		EventBus.getDefault().register(this);
	}

	private void initSpeaker() {
		mResultAnasy = new ResultAnasy(this) {
			@Override
			public void reTry() {
				// TODO Auto-generated method stub
				startVolice();
			}
		};
		// mResultAnasy.addAnswer("正在搜索",null);
		requestFocus();
	}

	private void initView() {
		mListView = (ListView) findViewById(R.id.msglist);
		mPoiListAdapter = new PoiListAdapter();
		mListView.setAdapter(mPoiListAdapter);
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setMessage("正在定位...");
		mProgressDialog.show();
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Integer arg = arg2 + 1;
				EventBus.getDefault().post(arg);
			}
		});
	}

	private void startVolice() {
		mResultAnasy.setSelectMode();
		if (mASREngine == null) {
			mASREngine = VoiceRecognitionClient.getInstance(this);
			mASREngine.setTokenApis(Constants.API_KEY, Constants.SECRET_KEY);
		}
		VoiceRecognitionConfig config = new VoiceRecognitionConfig();
		int prop = Config.CURRENT_PROP;
		// 输入法暂不支持语义解析
		if (prop == VoiceRecognitionConfig.PROP_INPUT) {
			prop = VoiceRecognitionConfig.PROP_ASSISTANT;
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
		int code = mASREngine.startVoiceRecognition(this, config);
		if (code != VoiceRecognitionClient.START_WORK_RESULT_WORKING){
			Toast.makeText(this, getString(R.string.error_start, code),Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onClientStatusChange(int status, Object obj) {
		// TODO Auto-generated method stub
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
			showNetVoiceDialog();
			break;
		// 语音识别完成，显示obj中的结果
		case VoiceRecognitionClient.CLIENT_STATUS_FINISH:
			MyLog.D("语音识别完成");
			dismissNetVoiceDialog();
			noSpeakCount = 0;
			isRecognition = false;
			if (obj != null && obj instanceof List) {
				List results = (List) obj;
				if (results.size() > 0) {
					String temp_str = results.get(0).toString();
					MyLog.D(temp_str);
					showResourceViewer(temp_str);
				}
			} else {
				MyLog.D("语音识别完成obj = null");
				// mResultAnasy.addInputEorror(null);
			}
			onVoiceRecognitionStop();
			break;
		// 用户取消
		case VoiceRecognitionClient.CLIENT_STATUS_USER_CANCELED:
			MyLog.D("用户取消语音");
			isRecognition = false;
			onVoiceRecognitionStop();
			dismissNetVoiceDialog();
			break;
		default:
			break;
		}
	}

	@Override
	public void onError(int errorType, int errorCode) {
		// TODO Auto-generated method stub
		isRecognition = false;
		MyLog.D("语音 onError:errorCode=" + errorCode);
		mResultAnasy.addAnswer("网络错误,请重试", new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				startVolice();
			}
		});
		onVoiceRecognitionStop();
		dismissNetVoiceDialog();
	}

	private void showNetVoiceDialog() {
		mProgressDialog.setMessage("正在识别...");
		mProgressDialog.show();
	}

	private void dismissNetVoiceDialog() {
		if (mProgressDialog != null && mProgressDialog.isShowing())
			mProgressDialog.dismiss();
	}

	@Override
	public void onNetworkStatusChange(int arg0, Object arg1) {
		// TODO Auto-generated method stub

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
		}
	}

	private long maxVolume;
	private int noSpeakCount;
	private Runnable mUpdateVolume = new Runnable() {
		public void run() {
			if (isRecognition) {
				long vol = mASREngine.getCurrentDBLevelMeter();
				mHandler.removeCallbacks(mUpdateVolume);
				mHandler.postDelayed(mUpdateVolume, POWER_UPDATE_INTERVAL);
				if (vol > maxVolume)
					maxVolume = vol;
				// MyLog.D("maxVolume="+maxVolume);
			} else {
				maxVolume = 0;
			}
		}
	};
	private final long MinNoSpeakCheckVolume = 30;
	private final long TimeNoSpeakCheckVolume = 10000;
	private final long MaxNoSpeakNum = 3;

	private Runnable mNoSpeakCheck = new Runnable() {
		public void run() {
			if (isRecognition) {
				MyLog.D("maxVolume=" + maxVolume);
				if (maxVolume < MinNoSpeakCheckVolume) {
					mASREngine.stopVoiceRecognition();
					noSpeakCount++;
					if (noSpeakCount >= MaxNoSpeakNum) {
						mResultAnasy.addAnswer("您没有说话,下次见", new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								finish();
								overridePendingTransition(0, 0);
							}
						});
					} else {
						mResultAnasy.addAnswer("您没有说话,请重试", new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								startVolice();
							}
						});
					}
				}
			}
		}
	};

	public void onVoiceRecognitionStart() {
		mHandler.removeCallbacksAndMessages(this);
		maxVolume = 0;
		mHandler.post(mUpdateVolume);
		mHandler.postDelayed(mNoSpeakCheck, TimeNoSpeakCheckVolume);
	}

	public void onVoiceRecognitionStop() {
		mHandler.removeCallbacksAndMessages(this);
	}

	LocationClient mLocClient;
	MyLocationListenner myListener = new MyLocationListenner();

	private void locationInit() {
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);
		option.setCoorType("bd09ll");
		option.setScanSpan(10000);
		option.setIsNeedAddress(true);
		mLocClient.setLocOption(option);
		mLocClient.start();
		MyLog.D("start location");
	}

	LatLng mLatLng;
	BDLocation mBDLocation;
	public class MyLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null) {
				return;
			}
			if (location.getLatitude() != Double.MIN_VALUE) {
				mLatLng = new LatLng(location.getLatitude(),
						location.getLongitude());
				mLocClient.stop();
				mBDLocation = location;
				MyLog.D("定位成功");
				startSearchNearby(location);
			}
		}
		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	private int mCurrentPageNum = 0;
	
	public void prevPageClick(View v){
		mNeedVoice = false;
		prevPage();
	}
	public boolean prevPage(){
		if(mCurrentPageNum>0){
			mCurrentPageNum--;
			startSearchNearby(mBDLocation);
			return true;
		}else{
			return false;
		}
	}
	public void nextPageClick(View v){
		mNeedVoice = false;
		nextPage();
	}

	public  boolean nextPage(){
		if(TotalPageNum>0 && mCurrentPageNum<TotalPageNum-1){
			mCurrentPageNum++;
			startSearchNearby(mBDLocation);
			return true;
		}else{
			return false;
		}
	}
	private void startSearchNearby(BDLocation location) {
		String keywords = getIntent().getStringExtra("keywords");
		if (StringUtils.isEmpty(keywords)) {
			keywords = "天安门";
		}
		if (mPoiSearch == null) {
			mPoiSearch = PoiSearch.newInstance();
			mPoiSearch.setOnGetPoiSearchResultListener(this);
		}
		mLocClient.stop();
		if (location != null) {
			boolean result = mPoiSearch
							.searchNearby(new PoiNearbySearchOption()
							.keyword(keywords)
							.radius(20000*1000)
							.location(new LatLng(location.getLatitude(), location.getLongitude()))
							.pageNum(mCurrentPageNum).pageCapacity(5));
//			LatLngBounds bounds = new LatLngBounds.Builder()  
//		    .include(new LatLng(20.0f, 88.0f))
//		    .include(new LatLng(60.0f,130.0f))
//		    .build();
//			boolean result = mPoiSearch
//					.searchInBound(new PoiBoundSearchOption()
//					.bound(bounds)
//					.keyword(keywords)
//					.pageCapacity(5)
//					.pageNum(mCurrentPageNum));
			
			if (result) {
				MyLog.D("启动搜索成功");
				mProgressDialog.setMessage("正在搜索...");
				if (!mProgressDialog.isShowing())
					mProgressDialog.show();
			} else {
				Toast.makeText(this, "无法启动搜索", Toast.LENGTH_SHORT).show();
				return;
			}
		} else {
			Toast.makeText(this, "定位失败,无法启动搜索", Toast.LENGTH_SHORT).show();
			return;
		}
	}

	@Override
	public void onGetPoiDetailResult(PoiDetailResult arg0) {
		// TODO Auto-generated method stub

	}

	int TotalPageNum, TotalPoiNum, CurrentPageNum, CurrentPageCapacity;
	boolean mNeedVoice = true;
	boolean mFirstSearch = true;
	@Override
	public void onGetPoiResult(PoiResult arg0) {
		// TODO Auto-generated method stub
		MyLog.D("搜索到结果");

		TotalPageNum = arg0.getTotalPageNum();
		TotalPoiNum = arg0.getTotalPoiNum();
		CurrentPageNum = arg0.getCurrentPageNum();
		CurrentPageCapacity = arg0.getCurrentPageCapacity();
		
		if(arg0.getAllPoi() == null || arg0.getAllPoi().size() == 0){
			Toast.makeText(this, "没有找到地点", Toast.LENGTH_SHORT).show();
			return;
		}
		
		mPoiListAdapter.setPoiList(arg0.getAllPoi());
		
		if(mNeedVoice){
			mResultAnasy.addAnswer("请选择", new Runnable(){
				@Override
				public void run() {
					MyLog.D("请选择 runnable");
					startVolice();
				}
			});
		}
		
		mProgressDialog.dismiss();
		
		if(mFirstSearch){
			startService(new Intent(this, DataSyncService.class).putExtra(
					DataSyncService.COMMAND, DataSyncService.COMMAND_STOP));
			mFirstSearch = false;
		}
		((TextView)findViewById(R.id.page_num)).setText((CurrentPageNum+1)+"/"+TotalPageNum);
	}

	class PoiListAdapter extends BaseAdapter {
		List<PoiInfo> mPoiInfoList;

		public PoiListAdapter() {
			mPoiInfoList = new ArrayList<PoiInfo>();
		}

		public void setPoiList(List<PoiInfo> list) {
			mPoiInfoList = list;
			notifyDataSetChanged();
		}

		public List<PoiInfo> getPoiList() {
			return mPoiInfoList;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mPoiInfoList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (view == null) {
				view = View.inflate(mContext, R.layout.navi_list_item, null);
			}
			PoiInfo poi = mPoiInfoList.get(position);

			if (poi.type.equals(POITYPE.BUS_STATION)) {
				poi.name = poi.name + "(公交站)";
			} else if (poi.type.equals(POITYPE.SUBWAY_STATION)) {
				poi.name = poi.name + "(地铁站)";
			}
			
			((TextView) view.findViewById(R.id.navi_item_name))
					.setText((position+1)+"."+poi.name);
			((TextView) view.findViewById(R.id.navi_item_detail))
					.setText(poi.address);
			double distance = GasStationActivity.roundResult(DistanceUtil.getDistance(new LatLng(mBDLocation.getLatitude(), mBDLocation.getLongitude()),poi.location),1);
			((TextView) view.findViewById(R.id.navi_item_distance))
			.setText("("+distance+"米"+")");
			return view;
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		EventBus.getDefault().unregister(this);
		
		if (mASREngine != null) {
			mASREngine.stopVoiceRecognition();
		}
		
		VoiceRecognitionClient.releaseInstance();
		
		if (mResultAnasy != null) {
			mResultAnasy.stopSpeaker();
		}
		
		mAm.abandonAudioFocus(mOnAudioFocusChangeListener);
		
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				startService(new Intent(mContext, DataSyncService.class)
						.putExtra(DataSyncService.COMMAND,
								DataSyncService.COMMAND_START));
			}
		}, 1000);
	}

	private AudioManager mAm;
	private OnAudioFocusChangeListener mOnAudioFocusChangeListener = new OnAudioFocusChangeListener() {
		@Override
		public void onAudioFocusChange(int focusChange) {
			MyLog.D("OnAudioFocusChangeListener:" + focusChange);
		}
	};

	private void requestFocus() {
		if (mAm == null)
			mAm = (AudioManager) getApplicationContext().getSystemService(
					Context.AUDIO_SERVICE);

		int result = mAm.requestAudioFocus(mOnAudioFocusChangeListener,
				AudioManager.STREAM_MUSIC,
				AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
		if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
			MyLog.I("requestAudioFocus successfully.");
		} else {
			MyLog.D("requestAudioFocus successfully.");
		}
	}

	public void onEventMainThread(Integer result) {
		int select = result;
		if (result!=0 && Math.abs(result) <= mPoiListAdapter.getCount()) {
			if(result < 0){
				select = mPoiListAdapter.getCount() + result;
			}else{
				select = select - 1;
			}
			PoiInfo poi = mPoiListAdapter.getPoiList().get(select);
			GasStationActivity.startNavi(mContext, mLatLng, poi.location);
			MyLog.D("onEventMainThread poi.location="+poi.name);
			finish();
		} else {
			mResultAnasy.addAnswer("没有第" + result + "个,请重试", new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					startVolice();
				}
			});
		}
	}
	public void onEventMainThread(String result) {
		MyLog.D("onEventMainThread:"+result);
		if(result.equals("next")){
			mNeedVoice = true;
			if(!nextPage()){
				mResultAnasy.addAnswer("已经是最后一页,请重试", new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						startVolice();
					}
				});
			}
		}else if(result.equals("back")){
			mNeedVoice = true;
			if(!prevPage()){
				mResultAnasy.addAnswer("已经是第一页,请重试", new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						startVolice();
					}
				});				
			}
		}
	}
	SuggestionSearch mSuggestionSearch;
	private void startSearch(){
		mSuggestionSearch = SuggestionSearch.newInstance();
		mSuggestionSearch.setOnGetSuggestionResultListener(listener);
		mSuggestionSearch.requestSuggestion(new SuggestionSearchOption()
		.city("深圳")
		.keyword("天安门"));
	}
	OnGetSuggestionResultListener listener = new OnGetSuggestionResultListener() {  
	    public void onGetSuggestionResult(SuggestionResult res) {  
	    	MyLog.D("onGetSuggestionResult");
	        if (res == null || res.getAllSuggestions() == null) {
	            return;  
	            //未找到相关结果  
	        }
	        List<SuggestionInfo> list = res.getAllSuggestions();
	        for(SuggestionInfo info:list){
	        	MyLog.D("address:"+info.district);
	        }
	    //获取在线建议检索结果
	    }  
	};
	
}
