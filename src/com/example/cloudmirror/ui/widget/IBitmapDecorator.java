package com.example.cloudmirror.ui.widget;

import android.graphics.Bitmap;

public abstract interface IBitmapDecorator {
	public abstract Bitmap getDecoratedBitmap(Bitmap paramBitmap);
}