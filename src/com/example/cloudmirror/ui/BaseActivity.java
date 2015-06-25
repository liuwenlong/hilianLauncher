package com.example.cloudmirror.ui;

import java.util.ArrayList;
import java.util.List;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.cloudmirror.api.ApiClient;
import com.example.cloudmirror.bean.User;
import com.example.cloudmirror.ui.widget.MGProgressDialog;
import com.example.cloudmirror.ui.widget.MyToast;
import com.example.cloudmirror.utils.DimenUtils;
import com.example.cloudmirror.utils.StringUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.social.UMSocialService;
import de.greenrobot.event.EventBus;

/**
 * 概述: 组织一个强大的基类，好让子类更方便的使用父类提供的方法，而不用重复写太多代码<br>
 * ps: 咱得用面向对象思想干点实事啊<br>
 * <br>
 * 1、细分其生命周期，形成固定化规范方法流程<br>
 * 2、引入自定义Activity栈管理<br>
 * 3、默认实现OnClickListener接口，子类必须实现OnClickListener中的方法<br>
 * <br>
 * 后续有什么通用的方法可以抽取到这里...<br>
 * 
 * @orignalAuthor yao
 * @createTime 2014年10月29日 下午3:57:41
 * 
 * @improvedAuther yao
 * @modifyTime 2014年10月29日
 */
public abstract class BaseActivity extends Activity implements OnClickListener {

	protected LayoutInflater mInflater;
	protected MyToast mToast;
	protected Context mContext;
	
	protected MGProgressDialog mProgressDialog;

	protected int mReqCode = -1; // 请求码，标示当前是哪个请求

	/**
	 * 概述: 只需写初始化数据和初始化View控件之后的处理即可
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mInflater = getLayoutInflater();
		mContext = this;
		mProgressDialog = new MGProgressDialog(mContext);

		getRestoredData(savedInstanceState);

		setContentView();

		_init(savedInstanceState);

		initViews();

		handleData();
		

	}

	@SuppressWarnings("unchecked")
	private void getRestoredData(Bundle savedInstanceState) {

		// 保存
		if (savedInstanceState != null) {
			mCurUser = (User) savedInstanceState.getSerializable("curUser");
			
			// retrieve value that we stored before crash
			if(!StringUtils.isEmpty(savedInstanceState.getString("token")))
				ApiClient.setToken(savedInstanceState.getString("token"));
		} else {

		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putSerializable("curUser", mCurUser);

		if(!StringUtils.isEmpty(ApiClient.getToken()))
			outState.putString("token", ApiClient.getToken());
			
		super.onSaveInstanceState(outState);
	}

	private void _init(Bundle savedInstanceState) {
		mToast = MyToast.getInstance(mContext);
		mToast.setGravity(Gravity.TOP, 0, DimenUtils.dip2px(this, 48));
		
		initData(savedInstanceState);
	}

	/**
	 * 概述: 设置ContextView
	 * 
	 * @auther yao
	 */
	protected abstract void setContentView();

	/**
	 * 概述: 初始化当前页面所需要的数据，例如前一个页面传过来的数据，
	 * 
	 * @auther yao
	 * @param savedInstanceState
	 *            内存存储数据的变量 从onCreate传过来
	 */
	protected abstract void initData(Bundle savedInstanceState);

	/**
	 * 概述: 初始化当前页面的View控件
	 * 
	 * @auther yao
	 */
	protected abstract void initViews();

	/**
	 * 概述: 加载数据
	 * 
	 * @auther yao
	 */
	protected abstract void handleData();

	public boolean checkConnectionOnDemand() {
		final NetworkInfo info =  ((ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
		if (info == null || info.getState() != State.CONNECTED) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * 概述: 设置ActionBar，如果有就设置，没有就返回， 见内部调用的方法
	 * 
	 * @auther yao
	 * @param titleResId
	 * @param whichStyle
	 * @param leftBtnResId
	 * @param rightBtnResId
	 * @param actionBarBgResId
	 * @param titileBgResId
	 */
	protected void setupActionBar(int titleResId, int whichStyle, int leftBtnResId, int rightBtnResId, int actionBarBgResId,
			int titileBgResId) {
		String title = getText(titleResId).toString();

		setupActionBar(title, whichStyle, leftBtnResId, rightBtnResId, actionBarBgResId, titileBgResId);
	}

	/**
	 * 概述: 设置ActionBar，如果有就设置，没有就返回
	 * 
	 * @auther yao
	 * @param title
	 *            标题
	 * @param whichStyle
	 *            哪种类型的：1、左按钮-中标题； 2、左按钮-中标题-右按钮(按钮图标设置)； 3、左右按钮图标全设置； 4、标题带背景的
	 * @param leftBtnResId
	 *            左按钮图标， -1为不设置
	 * @param rightBtnResId
	 *            右按钮图标， -1为不设置
	 * @param actionBarBgResId
	 *            整个actionbar背景
	 * @param titileBgResId
	 *            标题背景
	 */
	protected void setupActionBar(String title, int whichStyle, int leftBtnResId, int rightBtnResId, int actionBarBgResId, int titileBgResId) {
/*
		ActionBar actionBar = getActionBar();

		if (actionBar == null) {
			Log.e("actionBar", "actionBar is null");
			return;
		}
		actionBar.setDisplayShowHomeEnabled(false); // 不显示Home
		actionBar.setDisplayShowTitleEnabled(false); // 不显示Title
		actionBar.setDisplayUseLogoEnabled(false); // 不显示logo
		actionBar.setDisplayShowCustomEnabled(true);

		if (mInflater == null)
			mInflater = getLayoutInflater();

		View actionBarView = mInflater.inflate(R.layout.layout_actionbar_generic, null);
		rl_ab_title_bg_wrapper = (RelativeLayout) actionBarView.findViewById(R.id.rl_ab_title_bg_wrapper);
		rl_actionbar_root = (RelativeLayout) actionBarView.findViewById(R.id.rl_actionbar_root);
		tv_ab_title = (TextView) actionBarView.findViewById(R.id.tv_ab_title);
		iv_ab_left_btn = (ImageView) actionBarView.findViewById(R.id.iv_ab_left_btn);
		
		rl_ab_right = (RelativeLayout) actionBarView.findViewById(R.id.rl_ab_right);
		iv_ab_right_btn = (ImageView) actionBarView.findViewById(R.id.iv_ab_right_btn);
		tv_ab_right_btn = (TextView) actionBarView.findViewById(R.id.tv_ab_right_btn);

		if (actionBarBgResId != -1)
			rl_actionbar_root.setBackgroundResource(actionBarBgResId);

		if (titileBgResId != -1)
			rl_ab_title_bg_wrapper.setBackgroundResource(titileBgResId);

		if (tv_ab_title != null)
			tv_ab_title.setText(title);

		switch (whichStyle) {
		case 1:

			if (leftBtnResId != -1)
				iv_ab_left_btn.setImageResource(leftBtnResId);
			iv_ab_left_btn.setVisibility(View.VISIBLE);

			iv_ab_right_btn.setVisibility(View.INVISIBLE);
			tv_ab_title.setClickable(false);

			break;

		case 2:

			if (rightBtnResId != -1)
				iv_ab_right_btn.setImageResource(rightBtnResId);
			iv_ab_right_btn.setVisibility(View.VISIBLE);

			if (leftBtnResId != -1)
				iv_ab_left_btn.setImageResource(leftBtnResId);
			iv_ab_left_btn.setVisibility(View.VISIBLE);

			tv_ab_title.setClickable(false);

			break;

		case 3:

			if (leftBtnResId != -1) {
				iv_ab_left_btn.setImageResource(leftBtnResId);
				iv_ab_left_btn.setVisibility(View.VISIBLE);
			}

			if (rightBtnResId != -1) {
				iv_ab_right_btn.setImageResource(rightBtnResId);
				iv_ab_right_btn.setVisibility(View.VISIBLE);
			}

			tv_ab_title.setClickable(false);

			break;
		case 4: // 带arrow down的actionbar

			if (leftBtnResId != -1) {
				iv_ab_left_btn.setImageResource(leftBtnResId);
				iv_ab_left_btn.setVisibility(View.VISIBLE);
			}

			if (rightBtnResId != -1) {
				iv_ab_right_btn.setImageResource(rightBtnResId);
				iv_ab_right_btn.setVisibility(View.VISIBLE);
			}

			tv_ab_title.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.titlebar_arrow_down),
					null);
			tv_ab_title.setClickable(true);

			break;

		default:
			break;
		}

		actionBar.setCustomView(actionBarView, new ActionBar.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		*/
	}


	public static User mCurUser; // 静态，全局公用

	/**
	 * 概述: 获取当前已登录的用户信息
	 * 
	 * @auther yao
	 * @return
	 */
	protected User getCurUser() { // TODO 注意切换用户时会不会影响
		return mCurUser;
	}

	protected void setCurUser(User user) {
		if (user == null)
			return;
		mCurUser = user;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);	// 友盟统计
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this); // 友盟统计
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
