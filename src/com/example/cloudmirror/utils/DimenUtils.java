package com.example.cloudmirror.utils;

import android.content.Context;

/**
 * 概述：各种尺寸转换
 * 
 * @author yqw
 * @since 2014-3-25
 * @version 1.0
 */
public class DimenUtils {

	public static int sp2px(Context context, float spValue) {
		float fontScale = context.getResources().getDisplayMetrics().scaledDensity;

		return (int) (spValue * fontScale + 0.5f);
	}

	public static int px2sp(Context context, float pxValue) {
		float fontScale = context.getResources().getDisplayMetrics().scaledDensity;

		return (int) (pxValue / fontScale + 0.5f);
	}

	public static int dip2px(Context context, int dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;

		return (int) (dipValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;

		return (int) (pxValue / scale + 0.5f);
	}
}