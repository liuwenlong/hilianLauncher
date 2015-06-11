package com.example.cloudmirror.ui.widget;

import com.mapgoo.eagle.R;
import com.example.cloudmirror.utils.StringUtils;

import android.content.Context;
import android.content.DialogInterface;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SimpleDialogBuilder {

	public Context mContext;

	public CharSequence mTitle; // 对话框标题
	public String mContent; // 对话框内容
	public String mPositiveBtnTxt;
	public String mNegtiveBtnTxt;
	public int mPositiveButtonBgResId = 0;
	public int mNegtiveButtonBgResId = 0;
	private boolean mIsCancelable = true;

	public TextView tv_dialog_title;
	public TextView tv_dialog_content;
	public TextView tv_btn_positive;
	public TextView tv_btn_negtive;
	public RelativeLayout rl_dialog_content;
	public View mContentView; // 对话框中间加载的其他布局界面

	public LinearLayout ll_btn_wrapper; // 按钮组容器，控制按钮组是否可见

	/* 按钮监听 */
	public DialogInterface.OnClickListener mPositiveBtnClickListener;
	public DialogInterface.OnClickListener mNegtiveBtnClickListener;

	public SimpleDialogBuilder(Context context) {
		mContext = context;
	}

	/* 设置对话框信息 */
	public SimpleDialogBuilder setMessage(String message) {
		mContent = message;
		return this;
	}

	/**
	 * Set the Dialog message from resource
	 * 
	 * @param title
	 * @return
	 */
	public SimpleDialogBuilder setMessage(int message) {
		mContent = (String) mContext.getText(message);
		return this;
	}

	/**
	 * Set the Dialog title from resource
	 * 
	 * @param title
	 * @return
	 */
	public SimpleDialogBuilder setTitle(int title) {
		mTitle = (String) mContext.getText(title);
		return this;
	}

	/**
	 * Set the Dialog title from String
	 * 
	 * @param title
	 * @return
	 */
	public SimpleDialogBuilder setTitle(String title) {
		mTitle = title;
		return this;
	}
	
	/**
	 * Set the Dialog if it is Cancelable
	 * 
	 * @param title
	 * @return
	 */
	public SimpleDialogBuilder setCancelable(boolean b) {
		mIsCancelable = b;

		return this;
	}


	/**
	 * 设置对话框界面
	 * 
	 * @param v
	 *            View
	 * @return
	 */
	public SimpleDialogBuilder setContentView(View v) {
		mContentView = v;
		return this;
	}

	/**
	 * Set the positive button resource and it's listener
	 * 
	 * @param confirm_btnText
	 * @return
	 */
	public SimpleDialogBuilder setPositiveButton(int confirm_btnText, DialogInterface.OnClickListener listener) {
		mPositiveBtnTxt = (String) mContext.getText(confirm_btnText);
		mPositiveBtnClickListener = listener;
		return this;
	}

	public SimpleDialogBuilder setPositiveButtonBg(int bgResId) {
		mPositiveButtonBgResId = bgResId;
		
		return this;
	}
	
	public SimpleDialogBuilder setNegtiveButtonBg(int bgResId) {
		mNegtiveButtonBgResId = bgResId;

		return this;
	}

	/**
	 * Set the positive button and it's listener
	 * 
	 * @param confirm_btnText
	 * @return
	 */
	public SimpleDialogBuilder setPositiveButton(String confirm_btnText, DialogInterface.OnClickListener listener) {
		mPositiveBtnTxt = confirm_btnText;

		mPositiveBtnClickListener = listener;

		return this;
	}

	/**
	 * Set the negative button resource and it's listener
	 * 
	 * @param confirm_btnText
	 * @return
	 */
	public SimpleDialogBuilder setNegativeButton(int cancel_btnText, DialogInterface.OnClickListener listener) {
		mNegtiveBtnTxt = (String) mContext.getText(cancel_btnText);
		mNegtiveBtnClickListener = listener;
		return this;
	}

	/**
	 * Set the negative button and it's listener
	 * 
	 * @param confirm_btnText
	 * @return
	 */
	public SimpleDialogBuilder setNegativeButton(String cancel_btnText, DialogInterface.OnClickListener listener) {
		mNegtiveBtnTxt = cancel_btnText;
		mNegtiveBtnClickListener = listener;
		return this;
	}

	public SimpleDialog create() {
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// instantiate the dialog with the custom Theme
		final SimpleDialog dialog = new SimpleDialog(mContext, R.style.DialogTheme);
		dialog.mBuilder = this;

		View layout = inflater.inflate(R.layout.layout_dialog_generic, null);
		tv_dialog_title = (TextView) layout.findViewById(R.id.tv_dialog_title);
		tv_btn_positive = (TextView) layout.findViewById(R.id.tv_btn_positive);
		tv_btn_negtive = (TextView) layout.findViewById(R.id.tv_btn_negtive);
		tv_dialog_content = (TextView) layout.findViewById(R.id.tv_dialog_content);
		rl_dialog_content = (RelativeLayout) layout.findViewById(R.id.rl_dialog_content);
		ll_btn_wrapper = (LinearLayout) layout.findViewById(R.id.ll_btn_wrapper);

		dialog.addContentView(layout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

		// set the dialog title
		if (StringUtils.isEmpty(mTitle))
			tv_dialog_title.setVisibility(View.GONE);
		else {
			tv_dialog_title.setText(mTitle);
			tv_dialog_title.setVisibility(View.VISIBLE);
		}

		// set the confirm button
		if (mPositiveBtnTxt != null) {
			tv_btn_positive.setText(mPositiveBtnTxt);
			if (mPositiveButtonBgResId != 0)
				tv_btn_positive.setBackgroundResource(mPositiveButtonBgResId);
			else {
				if(mNegtiveBtnTxt == null)
					tv_btn_positive.setBackgroundResource(R.drawable.selector_generic_dialog_single_btn_bg);
			}

			if (mPositiveBtnClickListener != null) {
				tv_btn_positive.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						mPositiveBtnClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
					}
				});
			}
		} else {
			// if no confirm button just set the visibility to GONE
			tv_btn_positive.setVisibility(View.GONE);
		}
		// set the cancel button
		if (mNegtiveBtnTxt != null) {
			tv_btn_negtive.setText(mNegtiveBtnTxt);
			
			if (mNegtiveButtonBgResId != 0)
				tv_btn_negtive.setBackgroundResource(mNegtiveButtonBgResId);
			else {
				if(mPositiveBtnTxt == null)
					tv_btn_negtive.setBackgroundResource(R.drawable.selector_generic_dialog_single_btn_bg);
			}
			
			
			if (mNegtiveBtnClickListener != null) {
				tv_btn_negtive.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						mNegtiveBtnClickListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
					}
				});
			}
		} else {
			// if no confirm button just set the visibility to GONE
			tv_btn_negtive.setVisibility(View.GONE);
		}
		// set the content message
		if (mContent != null) {
			tv_dialog_content.setText(mContent);
			// TextView可滚动
			tv_dialog_content.setMovementMethod(new ScrollingMovementMethod());

		} else if (mContentView != null) {
			// if no message set add the contentView to the dialog body
			rl_dialog_content.removeAllViews();
			rl_dialog_content.addView(mContentView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		}
		
		dialog.setCancelable(mIsCancelable);

		dialog.isFromBuilder = true;

		dialog.setContentView(layout);
		return dialog;
	}



}
