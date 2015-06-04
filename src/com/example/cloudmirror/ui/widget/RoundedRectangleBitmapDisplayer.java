package com.example.cloudmirror.ui.widget;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

public final class RoundedRectangleBitmapDisplayer implements BitmapDisplayer {
	private final float width;
	private final float height;

	public RoundedRectangleBitmapDisplayer(float radius) {

		this.width = radius;
		this.height = radius;
	}

	@Override
	public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {

		if (!(imageAware instanceof ImageViewAware))
			throw new IllegalArgumentException("ImageAware should wrap ImageView. ImageViewAware is expected.");

		imageAware.setImageBitmap(new RoundedRectangleBitmapDecorator(this.width, this.height).getDecoratedBitmap(bitmap));
	}
}