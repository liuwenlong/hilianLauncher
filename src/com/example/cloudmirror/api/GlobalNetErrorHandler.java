package com.example.cloudmirror.api;

import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.mapgoo.diruite.R;
import com.example.cloudmirror.bean.User;
import com.example.cloudmirror.ui.widget.MyToast;
import com.example.cloudmirror.utils.StringUtils;


/**
 * 概述: 自定义Error处理、主要处理：拦截401(Authorization Error)错误， 重获token的机制
 * 
 * @author yao
 * @version 1.0
 * @created 2014年11月25日
 */
public class GlobalNetErrorHandler implements ErrorListener {

	private static GlobalNetErrorHandler mInstance = null;

	private GlobalNetErrorHandler() {
	}

	/**
	 * 概述: 单例、全局唯一
	 * 
	 * @auther yao
	 * @return
	 */
	public static GlobalNetErrorHandler getInstance(Context context, User curUser, Dialog progressDialog) {
		mContext = context;
		mProgressDialog = progressDialog;
		mCurUser = curUser;
		mGlobalNetErrorCallback = null;
		if (mInstance == null) {

			synchronized (GlobalNetErrorHandler.class) {
				if (mInstance == null)
					mInstance = new GlobalNetErrorHandler();
			}
		}

		return mInstance;
	}
	public static interface GlobalNetErrorCallback{
		public void OnNetErrorCallback(VolleyError error);
	}
	private static GlobalNetErrorCallback mGlobalNetErrorCallback;
	public static GlobalNetErrorHandler getInstance(Context context, User curUser, Dialog progressDialog,GlobalNetErrorCallback callback) {
		getInstance(context, curUser, progressDialog);
		mGlobalNetErrorCallback = callback;
		return mInstance;
	}
	
	private static User mCurUser;
	private static Dialog mProgressDialog;
	private static Context mContext;

	@Override
	public void onErrorResponse(VolleyError error) {

		if (mProgressDialog != null && mProgressDialog.isShowing())
			mProgressDialog.dismiss();

		VolleyErrorHelper.handleError(error, mContext, mCurUser); // 拦截/处理error信息
		
		
	}

	public static class VolleyErrorHelper {

		public static void handleError(VolleyError error, Context context, User curUser) {
			if (mProgressDialog != null)
				mProgressDialog.dismiss();

			String errorMsg = getMessage(error, context, curUser);

			if (!StringUtils.isEmpty(errorMsg))
				MyToast.getInstance(mContext).toastMsg(errorMsg);
			else if(mGlobalNetErrorCallback != null)
				mGlobalNetErrorCallback.OnNetErrorCallback(error);
				
		}

		/**
		 * Returns appropriate message which is to be displayed to the user
		 * against the specified error object.
		 * 
		 * @param error
		 * @param context
		 * @return
		 */
		public static String getMessage(VolleyError error, Context context, User curUser) {
			if (error instanceof TimeoutError) {
				Log.d("getMessage", " TimeoutError:" + error.toString());
				return context.getResources().getString(R.string.network_timeout_req_again);
			} else if (isServerProblem(error)) {
				Log.d("getMessage", " isServerProblem:" + error.toString());
				return handleServerError(error, context, curUser);
			} else if (isNetworkProblem(error)) {

				// -------------------------------------------------------------------------------
				// 某些系统版本，不会直接返回401 而是返回下面一段字符串，这里做了统一处理
				String str = error.toString();
				if (error instanceof NoConnectionError) {
					if (str!=null && str.contains("No authentication challenges found")) {
						// responseStatusCode = 401;
						kickYouOfflineNotifier(mContext, mCurUser, "1");
						return null;
					}
				}
				// -------------------------------------------------------------------------------

				Log.d("getMessage", " isNetworkProblem:" + error.toString());
				return context.getResources().getString(R.string.bad_network);
			}

			return context.getResources().getString(R.string.bad_network);
		}

		/**
		 * Determines whether the error is related to network
		 * 
		 * @param error
		 * @return
		 */
		private static boolean isNetworkProblem(VolleyError error) {
			return (error instanceof NetworkError) || (error instanceof NoConnectionError);
		}

		/**
		 * Determines whether the error is related to server
		 * 
		 * @param error
		 * @return
		 */
		private static boolean isServerProblem(VolleyError error) {
			return (error instanceof ServerError) || (error instanceof AuthFailureError);
		}

		/**
		 * Handles the server error, tries to determine whether to show a stock
		 * message or to show a message retrieved from the server.
		 * 
		 * @param err
		 * @param context
		 * @return
		 */
		private static String handleServerError(VolleyError error, final Context context, final User curUser) {
			NetworkResponse response = error.networkResponse;
			
			if (response != null) {
				switch (response.statusCode) {
				// case 404:
				// case 422:
				case 401: // 401 重获token
					kickYouOfflineNotifier(context, curUser, response.statusText);
					return null;

				default:
					return context.getResources().getString(R.string.bad_network);
				}
			}

			return context.getResources().getString(R.string.bad_network);
		}
	}

	private static boolean isAlertDialogShown = false;
	private static boolean isTokenTimeoutAlertDialogShown = false;
	// 被踢下线的通知
	private static void kickYouOfflineNotifier(final Context context, final User curUser, String statusText) {
		Log.d("401 msg:", statusText);

//		if (statusText.contains("1")) { // 账号在别处登录，被踢下线
//			if (!isAlertDialogShown) {
//
//				TextView alertTextView = (TextView) ((Activity) context).getLayoutInflater().inflate(R.layout.layout_alert_dialog_view,
//						null);
//				alertTextView.setText(R.string.single_login_dialog_alert_txt);
//				alertTextView.setGravity(Gravity.LEFT | Gravity.CENTER);
//
//				new SimpleDialogBuilder(context).setTitle(R.string.single_login_dialog_title).setContentView(alertTextView)
//						.setPositiveButton(R.string.single_login_dialog_right_btn, new OnClickListener() {
//
//							@Override
//							public void onClick(DialogInterface dialog, int which) { // 重新登录-跳回主页-刷新数据
//								dialog.dismiss();
//								isAlertDialogShown = false;
//
//								// 重新登录-并进入主界面-刷新数据
//								reLoginAndgetNewToken(context, curUser);
//							}
//						}).setNegativeButton(R.string.single_login_dialog_left_btn, new OnClickListener() {
//
//							@Override
//							public void onClick(DialogInterface dialog, int which) { // 退出-注销-到登录界面-去掉密码
//								dialog.dismiss();
//								isAlertDialogShown = false;
//
//								// 注销并回到登录界面
//								logoutAndStartLoginActivity(context, curUser);
//							}
//						}).setCancelable(false).create().show();
//
//				isAlertDialogShown = true;
//			}
//		} else if (statusText.contains("2")) { // token失效
//			if (!isTokenTimeoutAlertDialogShown) {
//
//				TextView alertTextView = (TextView) ((Activity) context).getLayoutInflater().inflate(R.layout.layout_alert_dialog_view,
//						null);
//				alertTextView.setText(R.string.login_token_info_time_out_alert);
//				alertTextView.setGravity(Gravity.LEFT | Gravity.CENTER);
//
//				new SimpleDialogBuilder(context).setTitle(R.string.prompt).setContentView(alertTextView)
//						.setPositiveButton(R.string.confirm, new OnClickListener() {
//
//							@Override
//							public void onClick(DialogInterface dialog, int which) { // 重新登录-跳回主页-刷新数据
//								dialog.dismiss();
//								isTokenTimeoutAlertDialogShown = false;
//
//								// 退出到登录界面
//								exitAndStartLoginActivity(context, curUser);
//								
//								// 回到登录界面
//							}
//						}).setCancelable(false).create().show();
//
//				isTokenTimeoutAlertDialogShown = true;
//			}
//		}
	}
	
	protected static void exitAndStartLoginActivity(Context context, User curUser) {
//		Intent intent = new Intent();
//		intent.setClass(mContext, LoginActivity.class);
//		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//
//		mCurUser = null;
//		BaseActivity.mCurUser = null; // 重置
//
//		if (!JPushInterface.isPushStopped(mContext))
//			JPushInterface.stopPush(mContext);
//
//		// 清除上一次自动登录信息
//		LoadPref.getInstance().beginTransaction().setLastFromLogout(true).commit();
//
//		// 结束自己
//		context.startActivity(intent);
	}

	// 注销并回到登录界面
	protected static void logoutAndStartLoginActivity(Context context, User curUser) {
//		Intent intent = new Intent();
//		intent.setClass(context, LoginActivity.class);
//		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//
//		curUser = null;
//		BaseActivity.mCurCarObj = null;
//		BaseActivity.mCurUser = null; // 重置
//		BaseActivity.mCarObjList.clear();
//		
//		if(!JPushInterface.isPushStopped(mContext))
//			JPushInterface.stopPush(mContext);
//		
//		// 清除上一次自动登录信息
//		LoadPref.getInstance().beginTransaction().setLastFromLogout(true).commit();
//
//		// 结束自己
//		context.startActivity(intent);
	}

	// 重新登录-并进入主界面-刷新数据
	private static void reLoginAndgetNewToken(final Context context, final User curUser) {
//		final MGProgressDialog progressDialog = new MGProgressDialog(context);
//		progressDialog.setCancelable(true);
//		final LoginInfo mLoginInfo = new LoginInfo();
//		mLoginInfo.mobile = curUser.getUserMobile();
//		mLoginInfo.pwd = curUser.getUserPwd();
//		mLoginInfo.devicetoken = ((TelephonyManager) context.getSystemService(Activity.TELEPHONY_SERVICE)).getDeviceId();
//		mLoginInfo.packname = mContext.getPackageName();
//		mLoginInfo.version = VersionUtils.getVersionNameStr();
//		mLoginInfo.platform = "Android";
//		mLoginInfo.remmeberPwd = false;
//		mLoginInfo.mOriginalPwd = null;
//		mLoginInfo.isFromReLogin = true;
//		// 重新获取token
//		ApiClient.loginInternel(curUser.getUserMobile(), curUser.getUserPwd(),
//				((TelephonyManager) context.getSystemService(Activity.TELEPHONY_SERVICE)).getDeviceId(), mContext.getPackageName(),
//				VersionUtils.getVersionNameStr(), "Android", new onReqStartListener() {
//
//					@Override
//					public void onReqStart() {
//
//						if (progressDialog != null && !progressDialog.isShowing()) {
//							progressDialog.setMessage(context.getText(R.string.token_expire_and_reget).toString());
//							if (((Activity) context).isFinishing())
//								progressDialog.show();
//						}
//
//					}
//				}, new Listener<JSONObject>() {
//
//					@Override
//					public void onResponse(JSONObject response) {
//
//						try {
//							if(response.has("error")&& response.getInt("error") == 0){
//								User user = JSON.parseObject(response.getJSONObject("result").toString(), User.class);
//	
//								curUser.setAuthToken(user.getAuthToken());
//								curUser.setLoginDate(user.getLoginDate());
//								curUser.setLoginCount(user.getLoginCount());
//	
//								Dao<User, String> userDaoUser = User.getDao(MGApp.getHelper());
//	
//								// 用户资料入库操作
//								if (userDaoUser.queryForId(curUser.getUserMobile()) != null)
//									// 存在？->更新
//									userDaoUser.update(curUser);
//								else
//									// 不存在？->添加
//									userDaoUser.createIfNotExists(curUser);
//	
//								if (progressDialog != null && progressDialog.isShowing())
//									progressDialog.dismiss();
//	
//								// 更新Token
//								ApiClient.setToken(user.getAuthToken());
//								
//								// 清空密码
//								curUser.setUserPwdPlain("");
//								userDaoUser.update(curUser);
//	
//	//							MyToast.getInstance(context).toastMsg(context.getText(R.string.token_reget_success_and_do_your_stuff_again));
//								
//								Intent intent = new Intent();
//								intent.setClass(context, MainActivity.class);
//								intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//								context.startActivity(intent);
//							}else if(response.getInt("error") == 2001){
//								context.startActivity(new Intent(mContext, NewDevicesVerifyActivity.class).putExtra("mLoginInfo", mLoginInfo));
//							}else{
//								exitAndStartLoginActivity(mContext, curUser);
//							}
//						} catch (SQLException e) {
//							e.printStackTrace();
//						} catch (JSONException e) {
//							e.printStackTrace();
//						}
//						
//					}
//
//				}, GlobalNetErrorHandler.getInstance(context, curUser, null));
	}
}