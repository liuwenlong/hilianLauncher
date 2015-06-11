package com.example.cloudmirror.ui.widget;

import com.mapgoo.eagle.R;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

/**
 * 概述: 自定义Dialog
 * 
 * @author yao
 * @version 1.0
 * @created 2014年11月8日
 */
public class MGProgressDialog extends Dialog {

	private View mContentView;
	public MGProgressDialog(Context context) {

		super(context, R.style.loading_dialog);

		mContentView = getLayoutInflater().inflate(R.layout.layout_progress_generic, null);

		setContentView(mContentView);
		getWindow().getAttributes().gravity = Gravity.CENTER;
		setCanceledOnTouchOutside(true);
		setCancelable(true);
	}

	public MGProgressDialog setMessage(String msg) {
		TextView msgTextView = (TextView) mContentView.findViewById(R.id.tip_text_view);
		if (msgTextView != null)
			msgTextView.setText(msg);

		return this;
	}

	public MGProgressDialog setMessage(int msgResId) {
		TextView msgTextView = (TextView) mContentView.findViewById(R.id.tip_text_view);
		if (msgTextView != null)
			msgTextView.setText(msgResId);

		return this;
	}

	public MGProgressDialog setMessage(CharSequence msgCharSeq) {
		TextView msgTextView = (TextView) mContentView.findViewById(R.id.tip_text_view);
		if (msgTextView != null)
			msgTextView.setText(msgCharSeq);

		return this;
	}
}