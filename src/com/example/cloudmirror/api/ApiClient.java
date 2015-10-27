package com.example.cloudmirror.api;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.example.cloudmirror.application.MGApp;
import com.example.cloudmirror.utils.DBmanager;
import com.example.cloudmirror.utils.MyLog;
import com.example.cloudmirror.utils.QuickShPref;

/**
 * 概述: API客户端接口：用于访问网络数据 <br>
 * 
 * TIPS 暂时以JSON为请求体的格式数据提交
 * 
 * @author yao
 * @version 1.0
 * @created 2014年11月8日
 */
@SuppressLint("SimpleDateFormat")
public class ApiClient {

	private static Listener<JSONObject> mListener;
	private static ErrorListener mErrorListener;
	private static onReqStartListener mOnStartListener;

	/**
	 * 概述: 每个activity请求网络之前必须要先设置Listeners <br>
	 * 用于每个请求成功或者失败的回调 <br>
	 * 
	 * @auther yao
	 * @param listener
	 * @param errorListener
	 */
	public static void setListeners(onReqStartListener onStartListener, Listener<JSONObject> listener, ErrorListener errorListener) {
		mOnStartListener = onStartListener;
		mListener = listener;
		mErrorListener = errorListener;
	}

	/**
	 * 概述: TIPS， 若token失效，需调用此方法，重新设置token
	 * 
	 * @auther yao
	 * @param newToken
	 */
	public static void setToken(String newToken) {
		RequestUtils.setToken(newToken);
	}
	
	/**
	 * 概述: get token
	 *
	 * @auther yao
	 * @return
	 */
	public static String getToken() {
		return RequestUtils.getToken();
	}

	public static void initAppKey(Context context) {
		RequestUtils.setAppKey(RequestUtils.getAppKey(context));
	}
	public static String getAppKey() {
		return RequestUtils.getAppKey();
	}
	/**
	 * 概述: 请求开始的回调监听器
	 * 
	 * @author yao
	 * @version 1.0
	 * @created 2014年11月8日
	 */
	public interface onReqStartListener {
		/**
		 * 概述: 请求开始的回调方法
		 * 
		 * @auther yao
		 * @param reqCode
		 *            请求的reqCode
		 */
		public void onReqStart();
	}
	
	/**
	 * 概述：将键值对转换为XML
	 * 
	 * @author yqw
	 * @since 2014年4月17日
	 * 
	 * @param params
	 * @return
	 */
	private static String map2XML(HashMap<String, Object> params) {
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");

		sb.append("<xml>");
		Set<String> keys = params.keySet();
		for (String key : keys) {
			sb.append("<" + key + ">" + params.get(key) + "</" + key + ">");
		}
		sb.append("</xml>");

		return sb.toString();
	}

	/**
	 * 概述: GET请求
	 * 
	 * @auther yao
	 * @param reqParams
	 *            请求参数
	 */
	private static void _GET(String url, Map<String, String> headerParams, Map<String, String> reqParams) {
		if (mListener != null && mErrorListener != null) {
			if (mOnStartListener != null)
				mOnStartListener.onReqStart(); // 请求开始的回调

			MyVolley.addToRequestQueue(RequestUtils.getJsonObjectRequest(Method.GET, url, headerParams, reqParams, null, mListener,
					mErrorListener));
		}
	}

	private static void _GET_WITH_LISTENERS(String url, Map<String, String> headerParams, Map<String, String> reqParams,
			onReqStartListener reqStartListener, Listener<JSONObject> responseListener, ErrorListener errorListener) {

		if (responseListener != null && errorListener != null) {
			if (reqStartListener != null) // 这个包容性太棒了，不用监听的话，我可以直接传null了
				reqStartListener.onReqStart(); // 请求开始的回调

			MyVolley.addToRequestQueue(RequestUtils.getJsonObjectRequest(Method.GET, url, headerParams, reqParams, null, responseListener,
					errorListener));
		}
	}

	/**
	 * 概述: 字符串GET请求
	 *
	 * @auther yao
	 * @param url
	 * @param headerParams
	 * @param reqParams
	 * @param reqStartListener
	 * @param responseListener
	 * @param errorListener
	 */
	private static void _GET_WITH_LISTENERS_REQ_STRING(String url, Map<String, String> headerParams, Map<String, String> reqParams,
			onReqStartListener reqStartListener, Listener<String> responseListener, ErrorListener errorListener) {

		if (responseListener != null && errorListener != null) {
			if (reqStartListener != null) // 这个包容性太棒了，不用监听的话，我可以直接传null了
				reqStartListener.onReqStart(); // 请求开始的回调

			MyVolley.addToRequestQueue(RequestUtils.getStringRequest(Method.GET, url, headerParams, reqParams, responseListener,
					errorListener));
		}
	}

	/**
	 * 概述: POST请求
	 * 
	 * @auther yao
	 * @param reqParams
	 *            POST请求参数/Form参数
	 * @param reqJsonObject
	 *            POST请求(Body) 暂时设置为：Content-Type=application/json
	 */
	private static void _POST(String url, Map<String, String> headerParams, Map<String, String> reqParams, Map<String, Object> reqBodyParams) {
		if (mListener != null && mErrorListener != null) {
			if (mOnStartListener != null)
				mOnStartListener.onReqStart(); // 请求开始的回调

			JSONObject reqJsonObject = null;
			if (reqBodyParams != null)
				reqJsonObject = new JSONObject(reqBodyParams);

			MyVolley.addToRequestQueue(RequestUtils.getJsonObjectRequest(Method.POST, url, headerParams, reqParams,
					reqJsonObject == null ? null : reqJsonObject, mListener, mErrorListener));
		}
	}

	/**
	 * 概述: POST请求
	 * 
	 * @auther yao
	 * @param reqParams
	 *            POST请求参数/Form参数
	 * @param reqJsonObject
	 *            POST请求(Body) 暂时设置为：Content-Type=application/json
	 */
	private static void _POST_AftarOnReqStart(String url, Map<String, String> headerParams, Map<String, String> reqParams,
			Map<String, Object> reqBodyParams) {
		if (mListener != null && mErrorListener != null) {

			JSONObject reqJsonObject = new JSONObject(reqBodyParams);

			MyVolley.addToRequestQueue(RequestUtils.getJsonObjectRequest(Method.POST, url, headerParams, reqParams, reqJsonObject,
					mListener, mErrorListener));
		}
	}

	/**
	 * 概述: POST请求
	 * 
	 * @auther yao
	 * @param reqParams
	 *            POST请求参数/Form参数
	 * @param reqJsonObject
	 *            POST请求(Body) 暂时设置为：Content-Type=application/json
	 */
	private static void _POST_WITH_LISTENERS(String url, Map<String, String> headerParams, Map<String, String> reqParams,
			Map<String, Object> reqBodyParams, onReqStartListener reqStartListener, Listener<JSONObject> responseListener,
			ErrorListener errorListener) {
		if (responseListener != null && errorListener != null) {
			if (reqStartListener != null)
				reqStartListener.onReqStart(); // 请求开始的回调

			JSONObject reqJsonObject = null;

			if (reqBodyParams != null){
				//reqJsonObject = new JSONObject(reqBodyParams);
			com.alibaba.fastjson.JSONObject o = new com.alibaba.fastjson.JSONObject(reqBodyParams);
			//Log.d("o", o.toString());
			
				try {
					reqJsonObject = new JSONObject(o.toString());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			MyVolley.addToRequestQueue(RequestUtils.getJsonObjectRequest(Method.POST, url, headerParams, reqParams,
					reqJsonObject == null ? null : reqJsonObject, responseListener, errorListener));
		}
	}
	
	/**
	 * 概述: 请求短信验证码
	 * 
	 * @auther yao
	 * @param phoneNum
	 *            手机号
	 */
	public static void reqVerifyCode(String phoneNum) {
		Map<String, String> reqParams = new HashMap<String, String>();
		reqParams.put("sim", phoneNum);

		_GET(URLs.SMS_VERIFY, null, reqParams);
	}
	public static Request<JSONObject> getVoiceYesNo(onReqStartListener reqStartListener,Listener<JSONObject> responseListener,ErrorListener errorListener) {
		Map<String, String> reqParams = new HashMap<String, String>();
		//reqParams.put("imei", iemi);
		 _GET_WITH_LISTENERS(URLs.VOICE_YES_NO, null, null, reqStartListener, responseListener, errorListener);
		 return null;
	}
	public static void postVoiceContent(String content,onReqStartListener reqStartListener,Listener<JSONObject> responseListener,ErrorListener errorListener) {
		Map<String, Object> reqBodyParams = new HashMap<String, Object>();
		reqBodyParams.put("imei", QuickShPref.getInstance().getString(QuickShPref.IEMI));
		reqBodyParams.put("appver", VersionUpdate.getVersionString(MGApp.pThis));
		reqBodyParams.put("date", DBmanager.getTime());
		reqBodyParams.put("content",content.replace("\\", ""));
		_POST_WITH_LISTENERS(URLs.VOICE_YES_NO, null, null, reqBodyParams, reqStartListener, responseListener, errorListener);
	}
	public static Request<JSONObject> getCarHome(String imei,onReqStartListener reqStartListener,Listener<JSONObject> responseListener,ErrorListener errorListener) {
		Map<String, String> reqParams = new HashMap<String, String>();
		reqParams.put("imei", imei);
		reqParams.put("ak", getAppKey());
		 _GET_WITH_LISTENERS(URLs.CAR_HOME, null, reqParams, reqStartListener, responseListener, errorListener);
		 return null;
	}
	public static void postImage(String content,String filename,onReqStartListener reqStartListener,Listener<JSONObject> responseListener,ErrorListener errorListener) {
		Map<String, Object> reqBodyParams = new HashMap<String, Object>();	
		reqBodyParams.put("uid",(int)7623);
		reqBodyParams.put("mediaVal", content);
		reqBodyParams.put("filename",filename);
		reqBodyParams.put("mediaType", "image");
		reqBodyParams.put("format", "jpg");
		MyLog.D("postImage:"+reqBodyParams.toString());
		_POST_WITH_LISTENERS(URLs.IMG_UPLOAD, null, null, reqBodyParams, reqStartListener, responseListener, errorListener);
	}
	public static void postVideo(String content,String filename,onReqStartListener reqStartListener,Listener<JSONObject> responseListener,ErrorListener errorListener) {
		Map<String, Object> reqBodyParams = new HashMap<String, Object>();	
		reqBodyParams.put("uid",(int)7623);
		reqBodyParams.put("mediaVal", content);
		reqBodyParams.put("filename", filename);
		reqBodyParams.put("mediaType", "video");
		reqBodyParams.put("format", "mp4");
		MyLog.D("postVideo:"+reqBodyParams.toString());
		_POST_WITH_LISTENERS(URLs.VIDEO_UPLOAD, null, null, reqBodyParams, reqStartListener, responseListener, errorListener);
	}

	public static Request<JSONObject> getWeather(String lon, String lat, onReqStartListener reqStartListener,Listener<JSONObject> responseListener,ErrorListener errorListener) {
		Map<String, String> reqParams = new HashMap<String, String>();
		reqParams.put("lon", lon);
		reqParams.put("lat", lat);
		reqParams.put("future", "1");
		reqParams.put("ak", getToken());
		
		 _GET_WITH_LISTENERS(URLs.OPEN_WEATHER, null, reqParams, reqStartListener, responseListener, errorListener);
		 return null;
	}
	public static Request<JSONObject> getWeixinCode(onReqStartListener reqStartListener,Listener<JSONObject> responseListener,ErrorListener errorListener) {
		Map<String, String> reqParams = new HashMap<String, String>();
		reqParams.put("imei", QuickShPref.getInstance().getString(QuickShPref.IEMI));
		reqParams.put("appkey", getAppKey());
		 _GET_WITH_LISTENERS(URLs.OPEN_WEIXIN, null, reqParams, reqStartListener, responseListener, errorListener);
		 return null;
	}
	public static Request<JSONObject> getMusic(String keywords,onReqStartListener reqStartListener,Listener<String> responseListener,ErrorListener errorListener) {
		Map<String, String> reqParams = new HashMap<String, String>();
		reqParams.put("op", "12");
		reqParams.put("count", "1");
		reqParams.put("title", keywords);
		
		_GET_WITH_LISTENERS_REQ_STRING(URLs.BAIDU_OPEN_MUSIC, null, reqParams, reqStartListener, responseListener, errorListener);
		 return null;
	}
}
