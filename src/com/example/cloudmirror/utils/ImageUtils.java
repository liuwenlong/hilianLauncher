package com.example.cloudmirror.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import com.mapgoo.carlife.main.R;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;


public class ImageUtils {

	private static String imgType;
	private static CompressFormat compressFormat = CompressFormat.PNG;
	private static int screenWidth;
	private static int screenHeight;

	/**
	 * 概述：获取适当尺寸的bitmap大小，即重新调整尺寸，这样将图片文件以bitmap形式读到内存中不会占用太大内存
	 * 
	 * @author yqw
	 * @since 2014年5月24日
	 * 
	 * @param context
	 * @param imgPath
	 * @return
	 */
	public static Bitmap getResizeBitMap(Context context, String imgPath) {

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;

		BitmapFactory.decodeFile(imgPath, options); // 解析出来的bitmap为null，不必在这里新建对象
		// 获取图片宽高
		int imgHeight = options.outHeight;
		int imgWidth = options.outWidth;

		Log.d("imgHeight", imgHeight + "");
		Log.d("imgWidth", imgWidth + "");

		imgType = options.outMimeType;

		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);

		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;

		options.inSampleSize = 1;

		// 加载一张分辨率跟系统一致的图片需要的内存
		float sysImgMem = screenWidth * screenHeight * 4 / 1024 / 1024;

		// 完全加载一张大分辨率的图片需要的内存
		float imgReqMem = imgHeight * imgWidth * 4 / 1024 / 1024;

		Log.d("sysImgMem", sysImgMem + "");
		Log.d("imgReqMem", imgReqMem + "");

		if (imgReqMem > sysImgMem)
			// options.inSampleSize = calculateInSampleSize(options,
			// screenWidth, screenHeight);
			options.inSampleSize = computeSampleSize(options, -1, screenWidth * screenHeight);

		Log.d("options.inSampleSize", options.inSampleSize + "");

		/* 下面两个字段需要组合使用 */
		options.inPurgeable = true; // 可以被系统回收(清楚)
		options.inInputShareable = true; // 位图是否能够共享一个指向数据源的引用
		options.inJustDecodeBounds = false; // 注意 一定要关闭这个

		// 得到真正的bitmap
		try {
			return BitmapFactory.decodeFile(imgPath, options);
		} catch (OutOfMemoryError OOMErr) { // 捕获OOM Error,
											// 并进行异常处理，防止程序因OOM而Crash
			Log.d("Oops", "程序发生OOM");
			// return defaultBitmap; // 如果OOM，则返回一个默认的Bitmap

			return null;
		}
	}

	/**
	 * 概述：Android源码中给出的动态计算Bitmap的inSampleSize的大小 Using powers of 2 for
	 * inSampleSize values is faster and more efficient for the decoder
	 * saveBitmapAsTmpImgFile
	 * @author yqw
	 * @since 2014年6月16日
	 * @created 2014年6月16日
	 * 
	 * @param options
	 * @param minSideLength
	 * @param maxNumOfPixels
	 * @return
	 */
	public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {

		int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
		int roundedSize;

		if (initialSize <= 8) {

			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}

		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}

		return roundedSize;
	}

	/**
	 * 概述：
	 * 
	 * @author yqw
	 * @since 2014年6月16日
	 * @created 2014年6月16日
	 * 
	 * @param options
	 * @param minSideLength
	 * @param maxNumOfPixels
	 * @return
	 */
	private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {

			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}

		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {

			return 1;
		} else if (minSideLength == -1) {

			return lowerBound;
		} else {

			return upperBound;
		}
	}

	public static String getRealPathFromURI(Uri contentUri, Context context) {
		String res = null;
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
		if (cursor.moveToFirst()) {
			;
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			res = cursor.getString(column_index);
		}
		cursor.close();
		return res;
	}

	// Extended Code Here-----------------------------------------------------

	
	
	public static String saveBitmapAsTmpImgFile(Bitmap bitmap) {
		String tmpImgPath = "";
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) { // sd存在并可写,则存到sdcard中
			String tmpfilePath = Environment.getExternalStorageDirectory() + "/" + mContext.getResources().getString(R.string.app_name)+ "/tmp/"; // file = /sdcard/<appname>/tmp/
			File fileP = new File(tmpfilePath); // 文件路径是否存在
			if (!fileP.exists()) { // 如果不存在就创建
				fileP.mkdirs();
			}
			tmpImgPath = tmpfilePath + CryptoUtils.MD5Encode(String.valueOf(System.currentTimeMillis()));
			File tmpfile = new File(tmpImgPath);
			try {
				FileOutputStream fos = new FileOutputStream(tmpfile);
				if (bitmap.compress(compressFormat, 100, fos)) {
					fos.flush();
					fos.close();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				Toast.makeText(mContext, e + "找不到路径", Toast.LENGTH_SHORT).show();
			} catch (IOException e) {
				e.printStackTrace();
				Toast.makeText(mContext, e + "文件保存失败", Toast.LENGTH_SHORT).show();
			}
		} else { // 没检测到sdcard？ 保存到内部存储控件，然后公开并返回其ContentProvder的URI
		}
		return tmpImgPath;
	}

	public static String saveByteAsTmpImgFile(byte[] bytes) {
		String tmpImgPath = "";
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) { // sd存在并可写,则存到sdcard中
			String tmpfilePath = Environment.getExternalStorageDirectory() + "/" + mContext.getResources().getString(R.string.app_name)+ "/tmpl/"; // file = /sdcard/<appname>/tmp/
			File fileP = new File(tmpfilePath); // 文件路径是否存在
			if (!fileP.exists()) { // 如果不存在就创建
				fileP.mkdirs();
			}
			tmpImgPath = tmpfilePath + CryptoUtils.MD5Encode(String.valueOf(System.currentTimeMillis()));
			File tmpfile = new File(tmpImgPath);
			try {
				FileOutputStream fos = new FileOutputStream(tmpfile);
				fos.write(bytes);
				fos.close();
//				if (bitmap.compress(compressFormat, 100, fos)) {
//					fos.flush();
//					fos.close();
//				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				Toast.makeText(mContext, e + "找不到路径", Toast.LENGTH_SHORT).show();
			} catch (IOException e) {
				e.printStackTrace();
				Toast.makeText(mContext, e + "文件保存失败", Toast.LENGTH_SHORT).show();
			}
		} else { // 没检测到sdcard？ 保存到内部存储控件，然后公开并返回其ContentProvder的URI
		}
		return tmpImgPath;
	}	
	public static String file2Base64(Context c,String filePath) {
		mContext = c;
		return file2Base64(filePath);
	}
	public static String file2Base64(String filePath) {
		String base64Str = "";

		try {
			InputStream is = new FileInputStream(new File(filePath));

			// 将输入流转换为byte数组
			byte[] bytes = inputStream2Byte(is);
			// 将这个输入流以Base64格式编码为String
			base64Str = Base64.encodeToString(bytes, Base64.DEFAULT);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Toast.makeText(mContext, e + " 文件未找不到路径", Toast.LENGTH_SHORT).show();
		}

		return base64Str;
	}
	
	public static Bitmap getBitmapFromBase64String(Context c, String str,ArrayList<String> array){
		mContext = c;
		Bitmap bp = null;
		
		if(str != null){
			byte[] bytes = Base64.decode(str, Base64.DEFAULT);
			String path = saveByteAsTmpImgFile(bytes);
			array.add(path);
			//bp = BitmapFactory.decodeByteArray(bytes, 0, 0);
			bp = BitmapFactory.decodeFile(path);
		}
		
		return bp;
	}

	/**
	 * 概述：将输入流转换为byte数组saveBitmapAsTmpImgFile
	 * 
	 * @author yqw
	 * @since 2014年5月24日
	 * 
	 * @param in
	 * @return
	 */
	private static byte[] inputStream2Byte(InputStream in) {
		if (in == null) {
			return null;
		}
		byte[] buffer = new byte[512];

		ByteArrayOutputStream out = new ByteArrayOutputStream();

		int length = -1;
		try {
			while ((length = in.read(buffer)) != -1) {
				out.write(buffer, 0, length);
			}
			in.close();
			return out.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	private static Context mContext;

	/**
	 * 概述：Bitmap转Base64
	 * 
	 * @author yao
	 * @version 1.0
	 * @created 2014年10月8日
	 * 
	 * @param mContext2
	 * @param mClipedBitmap
	 */
	public static String img2Base64(Context context, Bitmap mClipedBitmap) {
		mContext = context;

		// 压缩
		// mClipedBitmap = bitmapCompress(mClipedBitmap);

		// 压缩过后
		String tmpImgPath = saveBitmapAsTmpImgFile(mClipedBitmap); // 保存压缩过后的临时图片文件

		// bitMapRecycle(mClipedBitmap); // 回收

		// base64对临时文件编码 临时文件考虑以后删除
		String base64Str = file2Base64(tmpImgPath);

		return base64Str;
	}

	// Extended Code Here-----------------------------------------------------
}
