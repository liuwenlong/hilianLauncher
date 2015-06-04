package com.example.cloudmirror.ui.widget;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

public final class RoundedRectangleBitmapDecorator implements IBitmapDecorator {

	private final float roundX;
	private final float roundY;

	public RoundedRectangleBitmapDecorator(float roundX, float roundY) {
		this.roundX = roundX;
		this.roundY = roundY;
	}

	public final Bitmap getDecoratedBitmap(Bitmap paramBitmap) {
		if (paramBitmap == null)
			return null;

		int width;
		int height;

		Bitmap localBitmap;
		do {
			width = paramBitmap.getWidth();
			height = paramBitmap.getHeight();

			localBitmap = BitmapUtils.createBitmap(width, height, Bitmap.Config.ARGB_8888, 1);

		} while (localBitmap == null);

		Canvas localCanvas = new Canvas(localBitmap);

		Paint localPaint = new Paint();
		Rect localRect = new Rect(0, 0, width, height);
		RectF localRectF = new RectF(localRect);

		localPaint.setAntiAlias(true);
		localCanvas.drawARGB(0, 0, 0, 0);
		localCanvas.drawRoundRect(localRectF, this.roundX, this.roundY, localPaint);
		localPaint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		localCanvas.drawBitmap(paramBitmap, localRect, localRectF, localPaint);

		return localBitmap;
	}
}