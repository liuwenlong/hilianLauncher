package com.example.cloudmirror.ui.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

/**
 * 概述: 自定义Dialog
 * 
 * @orignalAuthor yao
 * @createTime 2014年11月3日 下午2:08:49
 * 
 * @improvedAuther yao
 * @modifyTime 2014年11月3日
 */
public class SimpleDialog extends Dialog {

	public SimpleDialogBuilder mBuilder;

	public SimpleDialog(Context context) {
		super(context);
	}

	public SimpleDialog(Context context, int theme) {
		super(context, theme);
	}

	@Override
	public void setTitle(CharSequence title) {
		mBuilder.mTitle = title;

		if (mBuilder.tv_dialog_title != null)
			mBuilder.tv_dialog_title.setText(title);
	}

	@Override
	public void setTitle(int titleId) {
		setTitle(mBuilder.mContext.getText(titleId));
	}

	// 设置按钮组是否可见
	public SimpleDialog setBtnGroupVisibility(int visibility) {
		mBuilder.ll_btn_wrapper.setVisibility(visibility);

		return this;
	}

	@Override
	public void setContentView(int layoutResID) {

		if (isFromBuilder) {
			super.setContentView(layoutResID);

			isFromBuilder = false;
		} else {

//			View contextView = null;
//
//			if (mBuilder.rl_dialog_content.findViewWithTag(layoutResID) != null) {
//				contextView = mBuilder.rl_dialog_content.findViewWithTag(layoutResID);
//			} else {
//				contextView = ((Activity) mBuilder.mContext).getLayoutInflater().inflate(layoutResID, null);
//
//				contextView.setTag(layoutResID);
//			}

			setContentView(((Activity) mBuilder.mContext).getLayoutInflater().inflate(layoutResID, null));
		}
	}

	public boolean isFromBuilder = false;

	@Override
	public void setContentView(View view) {

		if (view == null)
			return;

		if (isFromBuilder) {
			super.setContentView(view);

			isFromBuilder = false;
		} else {

//			if (view.getParent() != null) // 如果父控件存在 则切换父控件的引用
//				mBuilder.rl_dialog_content = (RelativeLayout) view.getParent();

			mBuilder.rl_dialog_content.removeAllViews();
			mBuilder.rl_dialog_content.addView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		}
	}

}
