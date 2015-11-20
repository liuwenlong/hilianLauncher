package com.example.cloudmirror.ui.activity;



import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviParaOption;
import com.baidu.mapapi.overlayutil.OverlayManager;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.mapapi.utils.route.BaiduMapRoutePlan;
import com.example.cloudmirror.application.MGApp;
import com.example.cloudmirror.ui.BaseActivity;
import com.example.cloudmirror.ui.widget.FlipperIndicatorDotView;
import com.example.cloudmirror.utils.StringUtils;
import com.mapgoo.carlife.main.R;

import de.greenrobot.event.EventBus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by arche on 15-6-17.
 */
public class GasStationActivity extends BaseActivity implements OnGetPoiSearchResultListener {
	private String ACTION_KEY_HOME = "action.custom.key.home";
	private static final String TAG = "GasStationActivity";
	private ViewPager vPager;
	private MGApp app;
	private LatLng myLocation;
	public static ArrayList<HashMap<String, Object>> arrayList;
	public MapView mMapView;
	public BaiduMap mBaiduMap;
	public View myAdressposView; // POI位置view
	int[] mark;
	private PoiSearch mPoiSearch = null;

	private ArrayList<View> viewList;
	private List<PoiInfo> list;
	private FlipperIndicatorDotView mIndicatorrView;

	private View listShowView;
	private View mapShowView;
	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_gas_station);
		//SDKInitializer.initialize(this);
		//BaiduMapRoutePlan.finish(this);
	}

	@Override
	protected void initData(Bundle savedInstanceState) {
		// double lat =
		// Double.parseDouble(QuickShPref.getInstance().getString(QuickShPref.LAT)==null?"0":QuickShPref.getInstance().getString(QuickShPref.LAT));
		// double lng =
		// Double.parseDouble(QuickShPref.getInstance().getString(QuickShPref.LON)==null?"0":QuickShPref.getInstance().getString(QuickShPref.LON));
		// double lat = 11.22;
		// double lng = 113.910293;
		// myLocation = new LatLng(lat,lng);
		locationInit();
		app = (MGApp) getApplication();
		initMark();
		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(this);
		initMapView();

		// startSearchNearby();
		registRecvs();
	}
	private void registRecvs(){
		IntentFilter filter = new IntentFilter();
//		filter.addAction(ACTION_KEY_BACK);
		filter.addAction(ACTION_KEY_HOME);
		registerReceiver(KeycodeRecv, filter);
	}
	BroadcastReceiver KeycodeRecv = new  BroadcastReceiver(){
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			String action = arg1.getAction();
			if(ACTION_KEY_HOME.equals(action)){
				finish();
			}
		}
	};
	LocationClient mLocClient;
	MyLocationListenner myListener = new MyLocationListenner();
	ProgressDialog progressDialog;

	private void locationInit() {
		progressDialog = new ProgressDialog(mContext);
		progressDialog.setMessage("搜索中...");
		progressDialog.show();
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		// option.setOpenGps(true);// 打开gps
		option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		option.setIsNeedAddress(true);
		mLocClient.setLocOption(option);

		mLocClient.start();
	}

	public class MyLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null) {
				return;
			}
			if (location.getLatitude() != Double.MIN_VALUE) {
				myLocation = new LatLng(location.getLatitude(), location.getLongitude());
				mLocClient.stop();
				myHandler.post(mRunStartSearch);
			}
		}
	}
	
	Runnable mRunStartSearch = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			startSearchNearby();
		}
	};

	private void startSearchNearby() {
		String keywords = getIntent().getStringExtra("keywords");
		if (StringUtils.isEmpty(keywords)) {
			keywords = "加油站";
		}
		
		if (myLocation != null) {
			boolean result = mPoiSearch.searchNearby(
					new PoiNearbySearchOption().keyword(keywords).location(myLocation).radius(5000).pageCapacity(9));
			if (result) {

			} else {
				//Toast.makeText(mContext, "无法启动搜索", Toast.LENGTH_SHORT).show();
				findViewById(R.id.vPager_view).setVisibility(View.GONE);
				findViewById(R.id.errorTv).setVisibility(View.VISIBLE);
				myHandler.postDelayed(mRunStartSearch, 1000);
				return;
			}
		} else {
			findViewById(R.id.errorTv).setVisibility(View.VISIBLE);
			findViewById(R.id.vPager_view).setVisibility(View.GONE);
			Toast.makeText(mContext, "定位失败,无法启动搜索", Toast.LENGTH_SHORT).show();
			return;
		}
	}

	@Override
	protected void initViews() {
		mIndicatorrView = (FlipperIndicatorDotView) findViewById(R.id.vPager_Indicator);
		listShowView = findViewById(R.id.vPager_view);
		mapShowView = findViewById(R.id.map_view);
	}

	private void initMapView() {
		mMapView = (MapView) findViewById(R.id.mMapView);
		mBaiduMap = mMapView.getMap();
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
		mBaiduMap.setMapStatus(msu);
		mMapView.showZoomControls(false);
		mLiOverlayManager = new LiOverlayManager(mBaiduMap);
		mBaiduMap.setOnMarkerClickListener(mLiOverlayManager);
	}

	// POI气泡
	TextView mpop_name;
	TextView mpop_address;
	TextView mpop_phone;
	View btn_pop_rout;

	private void initMyPoiView() {
		myAdressposView = LayoutInflater.from(this).inflate(R.layout.explore_popview, null);
		mpop_name = (TextView) myAdressposView.findViewById(R.id.tx_tishi);
		mpop_address = (TextView) myAdressposView.findViewById(R.id.tv_location_info);
		mpop_phone = (TextView) myAdressposView.findViewById(R.id.tv_phone);
		btn_pop_rout = myAdressposView.findViewById(R.id.btn_pop_rout);

		btn_pop_rout.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				LatLng pt2 = (LatLng) v.getTag();
				startNavi(mContext, myLocation, pt2);
			}
		});
		myAdressposView.findViewById(R.id.map_bubbleImage2).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mBaiduMap.hideInfoWindow();
			}
		});
	}

	InfoWindow window;

	private void showInfowindows(HashMap<String, Object> map) {
		LatLng latlng = (LatLng) map.get("location");

		if (myAdressposView == null)
			initMyPoiView();

		mpop_name.setText(map.get("name").toString());
		mpop_address.setText(map.get("address").toString());
		mpop_phone.setText(map.get("phoneNum").toString());
		btn_pop_rout.setTag(latlng);
		myAdressposView.setLayoutParams(
				new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		window = new InfoWindow(myAdressposView, latlng, -47);
		mBaiduMap.showInfoWindow(window);
	}

	private void initMark() {
		mark = new int[] { R.drawable.icon_marka, R.drawable.icon_markb, R.drawable.icon_markc, R.drawable.icon_markd,
				R.drawable.icon_marke, R.drawable.icon_markf, R.drawable.icon_markg, R.drawable.icon_markh,
				R.drawable.icon_marki };
	}

	public static void startNavi(final Context context, LatLng pt1, LatLng pt2) {
		// 构建 导航参数
		try {
			NaviParaOption para = new NaviParaOption();
			para = para.startPoint(pt1);
			para = para.startName("从这里开始");
			para = para.endPoint(pt2);
			para = para.endName("到这里结束");
			//BaiduMapNavigation.openBaiduMapNavi(para, context);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	Intent intent = new Intent("com.mapgoo.start.navi");
        intent.putExtra("lat", pt2.latitude);//参数类型double
        intent.putExtra("lon", pt2.longitude);//参数类型double
        context.sendBroadcast(intent);
//		EventBus.getDefault().post(para);
	}

	public static double roundResult(Double v, int scale) {
		if (scale < 0) {
			return 0;
		}
		BigDecimal b = null == v ? new BigDecimal("0.0") : new BigDecimal(Double.toString(v));
		BigDecimal one = new BigDecimal("1");
		return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	LiOverlayManager mLiOverlayManager;

	public class LiOverlayManager extends OverlayManager {
		private List<OverlayOptions> optionsList = new ArrayList<OverlayOptions>();

		public LiOverlayManager(BaiduMap baiduMap) {
			super(baiduMap);
		}

		@Override
		public List<OverlayOptions> getOverlayOptions() {
			return optionsList;
		}

		@Override
		public boolean onMarkerClick(Marker marker) {
			int index = marker.getZIndex();
			Log.d("onMarkerClick", "index = " + index);
			showInfowindows(arrayList.get(index));
			return true;
		}

		public void setData(List<OverlayOptions> optionsList) {
			this.optionsList = optionsList;
		}

		public void addData(OverlayOptions overlay) {
			this.optionsList.add(overlay);
		}

		public void clear() {
			this.optionsList.clear();
		}

		@Override
		public boolean onPolylineClick(Polyline arg0) {
			// TODO Auto-generated method stub
			return false;
		}
	}

	private Handler myHandler = new Handler();

	private void initShowMark() {
		//try {

			int count = 0;
			if (arrayList != null && arrayList.size() > 0) {
				count = arrayList.size();
				mLiOverlayManager.removeFromMap();
				mLiOverlayManager.clear();
				for (int i = 0; i < count; i++) {
					BitmapDescriptor bdA = BitmapDescriptorFactory.fromResource(mark[i]);
					OverlayOptions ooA = new MarkerOptions().position((LatLng) arrayList.get(i).get("location"))
							.icon(bdA).zIndex(i);
					mLiOverlayManager.addData(ooA);
				}
				mLiOverlayManager.addToMap();

				myHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						mLiOverlayManager.zoomToSpan();
					}
				}, 400);
				addPoiToMapPageView((ArrayList<PoiInfo>) list);

			} else {
				return;
			}
//		} catch (Exception e) {
//			// TODO: handle exception
//			Log.i("aaaa", e.toString());
//			return;
//		}
	}

	@Override
	protected void handleData() {

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.gas_stition_left_memu:
		case R.id.gas_stition_left_memu_l:
			finish();
			break;
		case R.id.gas_stition_right_menu:

			if (listShowView.getVisibility() == View.VISIBLE) {
				listShowView.setVisibility(View.GONE);
				mapShowView.setVisibility(View.VISIBLE);
				// mMapView.setVisibility(View.VISIBLE);
				((TextView) findViewById(R.id.gas_stition_right_menu)).setText("列表");
				initShowMark();
			} else {
				listShowView.setVisibility(View.VISIBLE);
				mapShowView.setVisibility(View.GONE);
				// mMapView.setVisibility(View.GONE);
				((TextView) findViewById(R.id.gas_stition_right_menu)).setText("地图");
			}
			break;
		default:
			break;
		}
	}

	public class PoiAdapter extends PagerAdapter {
		private ArrayList<View> mListViews = new ArrayList<View>();

		public PoiAdapter(ArrayList<View> viewList) {
			this.mListViews = viewList;// 构造方法，参数是我们的页卡，这样比较方便。
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(mListViews.get(position));// 删除页卡
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) { // 这个方法用来实例化页卡
			container.addView(mListViews.get(position), 0);// 添加页卡
			return mListViews.get(position);
		}

		@Override
		public int getCount() {
			return mListViews.size();// 返回页卡的数量
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;// 官方提示这样写
		}
	}

	int gas_stations[] = { R.id.gas_stition_station1, R.id.gas_stition_station2, R.id.gas_stition_station3 };
	View poiView;

	private void addPoiToPageView(final ArrayList<PoiInfo> list) {

		viewList = new ArrayList<View>();
		if (list.size() > 0) {
			poiView = mInflater.inflate(R.layout.view_page_item, null);
			for (int i = 0; i < list.size() && i < 9; i++) {
				LinearLayout stationLayout = (LinearLayout) poiView.findViewById(gas_stations[i % 3]);
				LinearLayout stationView = (LinearLayout) mInflater.inflate(R.layout.gas_station_item, null);

				((TextView) stationView.findViewById(R.id.gas_stition_station_name))
						.setText(i + 1 + "." + list.get(i).name);
				((TextView) stationView.findViewById(R.id.gas_stition_station_address)).setText(list.get(i).address);
				double distance = roundResult(DistanceUtil.getDistance(myLocation, list.get(i).location), 1);
				((TextView) stationView.findViewById(R.id.gas_stition_station_distance)).setText(distance + "米");
				final int j = i;
				stationView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						
						startNavi(mContext, myLocation, list.get(j).location);
					}
				});
				stationView.setLayoutParams(
						new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
				stationLayout.addView(stationView);
				if ((i + 1) % 3 == 0 || i == list.size() - 1) {
					viewList.add(poiView);
					poiView = mInflater.inflate(R.layout.view_page_item, null);
				}
			}
		}

	}
	
	private ArrayList<View> mapViewList;

	private void addPoiToMapPageView(final ArrayList<PoiInfo> list) {

		mapViewList = new ArrayList<View>();
		if (list.size() > 0) {
			for (int i = 0; i < list.size() && i < 9; i++) {
				View poiView = mInflater.inflate(R.layout.layout_poi_map_view_page, null);
		
				((TextView) poiView.findViewById(R.id.tv_index)).setText((i+1)+"");
				((TextView) poiView.findViewById(R.id.tv_name)).setText(list.get(i).name);
				((TextView) poiView.findViewById(R.id.tv_adress)).setText(list.get(i).address);
				final int j = i;
				poiView.findViewById(R.id.tv_go).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						startNavi(mContext, myLocation, list.get(j).location);
					}
				});

				poiView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
				mapViewList.add(poiView);
				Log.i("aaaa", mapViewList.size()+"");
				
			}
			PoiAdapter poiAdapter = new PoiAdapter(mapViewList);
			((ViewPager) mapShowView.findViewById(R.id.map_view_vPager)).setAdapter(poiAdapter);
			((ViewPager) mapShowView.findViewById(R.id.map_view_vPager)).setOnPageChangeListener(new OnPageChangeListener() {
				
				@Override
				public void onPageSelected(int arg0) {
					// TODO Auto-generated method stub
					moveToLocation(list.get(arg0).location);
				}
				
				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onPageScrollStateChanged(int arg0) {
					// TODO Auto-generated method stub
					
				}
			});
		}

	}

	private void moveToLocation(LatLng latlng){
		if(latlng!=null){
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(latlng);
			mBaiduMap.animateMapStatus(u);		
		}
	}
	private PoiAdapter poiAdapter;

	@Override
	public void onGetPoiResult(PoiResult poiResult) {
		if (progressDialog.isShowing())
			progressDialog.hide();
		try {

			if (poiResult != null) {
				list = poiResult.getAllPoi();
				int TotalPageNum = poiResult.getTotalPageNum();
				int TotalPoiNum = poiResult.getTotalPoiNum();
				Log.d("onGetPoiResult", "TotalPageNum=" + TotalPageNum + ",TotalPoiNum=" + TotalPoiNum);
				TotalPageNum = TotalPoiNum / 10 + 1;
				arrayList = new ArrayList<HashMap<String, Object>>();
				if (list != null && list.size() > 0) {
					findViewById(R.id.errorTv).setVisibility(View.GONE);
					for (PoiInfo info : list) {
						HashMap<String, Object> map = new HashMap<String, Object>();
						map.put("name", info.name);
						map.put("address", info.address);
						map.put("phoneNum", info.phoneNum);
						map.put("location", info.location);
						map.put("howlong", DistanceUtil.getDistance(myLocation, info.location));
						map.put("uid", info.uid);
						map.put("lat", info.location.latitude);
						map.put("lon", info.location.longitude);

						Log.i(TAG, info.name);
						arrayList.add(map);
					}
					if (arrayList.size() > 0) {
						addPoiToPageView((ArrayList<PoiInfo>) list);
						poiAdapter = new PoiAdapter(viewList);
						((ViewPager) findViewById(R.id.vPager)).setAdapter(poiAdapter);
						mIndicatorrView.setCount(viewList.size());
						mIndicatorrView.setSeletion(0);
						((ViewPager) findViewById(R.id.vPager))
								.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
									@Override
									public void onPageScrolled(int position, float positionOffset,
											int positionOffsetPixels) {
									}

									@Override
									public void onPageSelected(int position) {
										mIndicatorrView.setSeletion(position);
									}

									@Override
									public void onPageScrollStateChanged(int state) {
									}
								});

					} else {
						Toast.makeText(this, "很抱歉！没有找到相关信息", Toast.LENGTH_LONG).show();
					}
				} else {
					findViewById(R.id.vPager_view).setVisibility(View.GONE);
					findViewById(R.id.errorTv).setVisibility(View.VISIBLE);
					return;
				}
			} else {
				findViewById(R.id.errorTv).setVisibility(View.VISIBLE);
				return;
			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.i(TAG, e.toString());
			return;
		}
	}

	@Override
	public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(KeycodeRecv);
		myHandler.removeCallbacks(mRunStartSearch);
	}
}
