package com.example.cloudmirror.ui.widget;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class AsyncImageView extends ImageView {
	public AsyncImageView(Context paramContext) {
		super(paramContext);
		//setScaleType(ImageView.ScaleType.CENTER_CROP);
	}

	public AsyncImageView(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		//setScaleType(ImageView.ScaleType.CENTER_CROP);
	}

	public AsyncImageView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
		//setScaleType(ImageView.ScaleType.CENTER_CROP);
	}

	public final void setImage(String url, int imgResId, BitmapDisplayer bitmapDisplayer) {
		DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();

		builder.showImageForEmptyUri(imgResId);
		builder.showImageOnFail(imgResId);
		builder.showImageOnLoading(imgResId);

		builder.cacheInMemory(true);
		builder.cacheOnDisk(true);

		builder.displayer(bitmapDisplayer);

		DisplayImageOptions displayImageOptions = builder.build();

		ImageLoader.getInstance().displayImage(url, this, displayImageOptions);
	}
}