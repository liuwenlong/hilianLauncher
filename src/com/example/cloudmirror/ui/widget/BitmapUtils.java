package com.example.cloudmirror.ui.widget;

import java.io.FileDescriptor;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public final class BitmapUtils {
	
	private static int inSampleSize;
	private static int outWidth;
	private static int outHeight;
	public static Bitmap createImageThumbnail(String filePath, int reqWidth, int reqHeight) {
		
		Bitmap bitmap = null;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, opts);
		
		outWidth = opts.outWidth;
		outHeight = opts.outHeight;
		
//		inSampleSize = computeSampleSize(opts, -1, reqWidth * reqHeight);
		
		inSampleSize = computeSampleSize2(opts, reqWidth, reqHeight);
		
		opts.inSampleSize = inSampleSize;
		
		opts.inJustDecodeBounds = false;
		try {
			bitmap = BitmapFactory.decodeFile(filePath, opts);
		} catch (OutOfMemoryError e) {
			Log.e("OOM", "OutOfMemoryError");
		}
		
		// 裁剪
//		bitmap = cropBitmapThumbnail(bitmap, reqWidth, reqHeight, outWidth/inSampleSize, outHeight/inSampleSize);
		
		return bitmap;
	}
	
	public static Bitmap createImageThumbnail(Bitmap filePath, int reqWidth, int reqHeight) {

		Bitmap bitmap = null;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		
		opts.outHeight = filePath.getHeight();
		opts.outWidth = filePath.getWidth();

		inSampleSize = computeSampleSize2(opts, reqWidth, reqHeight);

		opts.inSampleSize = inSampleSize;

		opts.inJustDecodeBounds = false;
		try {
//			bitmap = BitmapFactory.decodeFile(filePath, opts);
		} catch (OutOfMemoryError e) {
			Log.e("OOM", "OutOfMemoryError");
		}

		// 裁剪
		// bitmap = cropBitmapThumbnail(bitmap, reqWidth, reqHeight,
		// outWidth/inSampleSize, outHeight/inSampleSize);

		return bitmap;
	}
	
	public static int computeSampleSize2(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		
		int sampleSize = 0;

		int widthRatio = (int) Math.ceil(options.outWidth / reqWidth);// 获取宽度的压缩比率
		int heightRatio = (int) Math.ceil(options.outHeight / reqHeight);// 获取高度的压缩比率
		
		if (widthRatio > 1 || heightRatio > 1) {	// 只要其中一个的比率大于1,说明需要压缩
			if (widthRatio >= heightRatio) {		// 取options.inSampleSize为宽高比率中的最小值
				sampleSize = heightRatio;
			} else {
				sampleSize = widthRatio;
			}
		}
		
		
		return sampleSize;

	}

	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

		int inSampleSize = 1;

		if ((reqWidth < 0) || (reqHeight < 0))
			return inSampleSize;

		int width = options.outWidth;
		int height = options.outHeight;

		if (width > reqWidth || height > reqHeight) {
			inSampleSize = Math.round(height / reqHeight);

			int widthRatio = Math.round(width / reqWidth);

			if (inSampleSize >= widthRatio)
				inSampleSize = widthRatio;

			float f1 = width * height;
			float f2 = 2 * (reqHeight * reqWidth);

			while (f1 / (float) (inSampleSize * inSampleSize) > f1)
				inSampleSize++;
		}

		return inSampleSize;
	}

	public static Bitmap createBitmap(int reqWidth, int reqHeight, Bitmap.Config config, int paramInt3) {
		while (true) {
			if (paramInt3 > 2)
				return null;

			try {
				Bitmap localBitmap = Bitmap.createBitmap(reqWidth, reqHeight, config);
				return localBitmap;
			} catch (OutOfMemoryError localOutOfMemoryError) {
				System.gc();

				paramInt3++;
			} catch (IllegalArgumentException localIllegalArgumentException) {

				return null;
			}
		}
	}

	public static Bitmap createBitmap(Resources res, int resId, BitmapFactory.Options options, int paramInt2) {
		while (true) {
			if (paramInt2 > 2)
				return null;

			try {
				Bitmap localBitmap = BitmapFactory.decodeResource(res, resId, options);
				return localBitmap;
			} catch (OutOfMemoryError localOutOfMemoryError) {
				System.gc();

				paramInt2++;
			} catch (IllegalArgumentException localIllegalArgumentException) {

				return null;
			}
		}
	}

	public static Bitmap createBitmap(Bitmap bitmap, int reqWidth, int reqHeight) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float f1 = width / reqWidth;
		float f2 = height / reqHeight;

		if (f1 < f2) {
			Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, reqWidth, Math.round(height / f1), true);
			return Bitmap.createBitmap(scaledBitmap, 0, Math.round((scaledBitmap.getHeight() - reqHeight) / 2.0F), reqWidth, reqHeight);
		}
		Bitmap localBitmap1 = Bitmap.createScaledBitmap(bitmap, Math.round(width / f2), reqHeight, true);
		return Bitmap.createBitmap(localBitmap1, Math.round((localBitmap1.getWidth() - reqWidth) / 2.0F), 0, reqWidth, reqHeight);
	}

	public static Bitmap createBitmap(FileDescriptor paramFileDescriptor, BitmapFactory.Options options, int paramInt) {
		while (true) {
			if (paramInt > 2)
				return null;
			try {
				Bitmap localBitmap = BitmapFactory.decodeFileDescriptor(paramFileDescriptor, null, options);

				return localBitmap;
			} catch (OutOfMemoryError localOutOfMemoryError) {
				System.gc();

				paramInt++;
			} catch (IllegalArgumentException localIllegalArgumentException) {
				return null;
			}
		}

	}

	public static Bitmap createBitmap(String paramString, BitmapFactory.Options options, int paramInt) {
		while (true) {
			if (paramInt > 2)
				return null;
			try {
				Bitmap localBitmap = BitmapFactory.decodeFile(paramString, options);
				return localBitmap;
			} catch (OutOfMemoryError localOutOfMemoryError) {
				System.gc();

				paramInt++;
			} catch (IllegalArgumentException localIllegalArgumentException) {

				return null;
			}
		}

	}
}