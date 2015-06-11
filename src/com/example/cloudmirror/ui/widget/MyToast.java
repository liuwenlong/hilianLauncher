package com.example.cloudmirror.ui.widget;

import com.mapgoo.eagle.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 概述：封装的 自定义风格的Toast 工具类
 * 
 * @author yao
 * @version 1.0
 * @created 2014年8月14日
 */
public class MyToast {

	private static Toast mToast;
	private static View toastView;
	private TextView mToastTextView;
	private LayoutInflater mLayoutInflater;

	private MyToast(Context context) {
		mToast = new Toast(context);
		mLayoutInflater = ((Activity) context).getLayoutInflater();

		toastView = mLayoutInflater.inflate(R.layout.toast_text_widget, null);
		mToastTextView = (TextView) toastView.findViewById(R.id.toasttext);

		mToast.setView(toastView);
		mToast.setDuration(Toast.LENGTH_SHORT);
	}

	private static MyToast mInstance = null;

	public static MyToast getInstance(Context context) {

		if (mInstance == null) {

			synchronized (MyToast.class) {
				if (mInstance == null)
					mInstance = new MyToast(context);
			}
		} else {
			resetToastView();
		}

		return mInstance;
	}

	private static void resetToastView() {
		mToast.setView(toastView);
	}

	public void setGravity(int gravity, int xOffset, int yOffset) {
		mToast.setGravity(gravity, xOffset, yOffset);
	}

	public void setView(View view) {
		mToast.setView(view);
	}

	public void toastMsg(int msgId) {
		mToastTextView.setText(msgId);
		mToast.setDuration(Toast.LENGTH_SHORT);
		show();
	}

	public void toastMsg(String msg) {
		mToastTextView.setText(msg);
		mToast.setDuration(Toast.LENGTH_SHORT);
		show();
	}
	
	public void toastMsg(CharSequence msg) {
		mToastTextView.setText(msg);
		mToast.setDuration(Toast.LENGTH_SHORT);
		show();
	}

	public void toastMsg(String msg, int duration) {
		mToast.setDuration(duration);
		toastMsg(msg);
	}

	public void toastMsg(int msgId, int duration) {
		mToast.setDuration(duration);
		toastMsg(msgId);
	}

	public void show() {
		mToast.show();
	}

	public void cancel() {
		mToast.cancel();
	}
}