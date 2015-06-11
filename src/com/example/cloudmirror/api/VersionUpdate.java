package com.example.cloudmirror.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.example.cloudmirror.utils.CryptoUtils;
import com.example.cloudmirror.utils.QuickShPref;
import com.j256.ormlite.stmt.query.In;
import com.mapgoo.eagle.R;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class VersionUpdate  extends AsyncTask<String, Integer, Boolean>{
Context mContext ;
int status;
private ProgressDialog mProgressDialog;

public VersionUpdate(Context  c){
	mContext = c;
	status = 0;
	
}

@Override
protected void onPreExecute() {
	// TODO Auto-generated method stub
	super.onPreExecute();
	mProgressDialog = ProgressDialog.show(mContext, "温馨提示", "请稍后...", false, true);
	mProgressDialog.setCanceledOnTouchOutside(false);			
}

@Override
protected void onPostExecute(Boolean result) {
	// TODO Auto-generated method stub
	super.onPostExecute(result);
	if(!result){
		mProgressDialog.dismiss();
		Toast.makeText(mContext, "已是最新版本", Toast.LENGTH_SHORT).show();
	}
}

@Override
protected Boolean doInBackground(String... params) {
	
	Bundle bundle = getSoftVersion(params[0]);
	String url = BundleToVersion(bundle);
	if(url == null)
		return false;
	Log.d("url", url);
	publishProgress(-1);
	while(status == 0){
		try {
			Thread.sleep(200);
		} catch (Exception e) {}
	}
	
	if(status>0){
		publishProgress(-2);
		download(url);
		publishProgress(-3);
	}
	return true;
}

@Override
protected void onProgressUpdate(Integer... values) {
	// TODO Auto-generated method stub
	super.onProgressUpdate(values);
	int value = values[0];
	
	if(value  == -1){
			if(mProgressDialog.isShowing())
				mProgressDialog.dismiss();
			new AlertDialog.Builder(mContext).setTitle("发现新版本")
			.setMessage("是否更新？")
			.setNegativeButton("稍后更新", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					status = -1;
				}
			}).setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
	
				@Override
				public void onClick(DialogInterface dialog, int which) {
					status = 1;
				}
			}).show();		
	}else if(value == -2){
		if(mProgressDialog == null){
			mProgressDialog = ProgressDialog.show(mContext, "温馨提示", "正在下载新版...", false, true);
		}else{
			mProgressDialog.setMessage("正在下载新版...");
			if(!mProgressDialog.isShowing())
				mProgressDialog.show();
		}
	}else if(value == -3){
		mProgressDialog.dismiss();
	}else{
		if (mProgressDialog != null)
			mProgressDialog.setMessage("正在下载新版本...(" + values[0] + "%)");		
	}
}

String getString(int id){
	return mContext.getString(id);
}

boolean download(String URL){
	int count;
	boolean ifTrue = false;
	String urlMd5 = CryptoUtils.MD5Encode(URL);
	
	String saveFile = QuickShPref.getInstance().getString(urlMd5);
	File file;
	if(saveFile == null || !new File(saveFile).exists()){
		
		DecimalFormat df = new DecimalFormat("#.#");
		String fileName = getString(R.string.app_name) + "_V" + System.currentTimeMillis() + ".apk";
	
		File tmpFile = new File("//sdcard//" + getString(R.string.app_name) + "//download");
		if (!tmpFile.exists()) {
			tmpFile.mkdirs();	// 注意，是多层文件夹，用mkdirs
		}
		file = new File(tmpFile.getAbsolutePath() + "//" + fileName);
	
		try {
			URL url = new URL(URL);
			try {
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				InputStream is = conn.getInputStream();
	
				int length = conn.getContentLength();
				int countLoadFile = 0;
				int j = 0;
	
				FileOutputStream fos = new FileOutputStream(file);
				byte[] buf = new byte[1024];
				conn.connect();
				count = 0;
				if (conn.getResponseCode() >= 400) {
					Toast.makeText(mContext, "连接超时", Toast.LENGTH_SHORT).show();
				} else {
	
					while (count <= 100) {
						if (is != null) {
							int numRead = is.read(buf);
							if (numRead <= 0) {
								break;
							} else {
								fos.write(buf, 0, numRead);
								countLoadFile += numRead;
								j++;
								if (j == 100) {
									j = 0;
									Message msg = new Message();
									msg.what = 6;
									Bundle bundle = new Bundle();
									int value = (countLoadFile* 100 / length) ;
									bundle.putString("size", df.format(value));
									msg.setData(bundle);
									Log.d("url","download value="+value);
									publishProgress(value);
									//mHandler.sendMessage(msg);
								}
							}
						} else {
							break;
						}
					}
				}
				conn.disconnect();
				fos.close();
				is.close();
				ifTrue = true;
			} catch (IOException e) {
				e.printStackTrace();
				ifTrue = false;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			ifTrue = false;
		} finally {
			ifTrue = true;
		}
		QuickShPref.getInstance().putValueObject(urlMd5,file.getAbsolutePath());
	}else{
		file = new File(saveFile);
		ifTrue = true;
	}
	
	
	if(ifTrue)
		openFile(file);
	
	return ifTrue;
}
// 根据文件，打开apk并安装
public void openFile(File file) {
	Intent intent = new Intent();
	intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	intent.setAction(android.content.Intent.ACTION_VIEW);
	intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
	mContext.startActivity(intent);
}
public String BundleToVersion(Bundle bundle){
	String result;
	String Result = bundle.getString("Result");
	if (Result.startsWith("error:")) {
		result = bundle.getString("Result").substring(6);
		return null;
	}

	if (bundle.getString("Result").equals("0")) {
		result = bundle.getString("Reason");
	} else {
		
		String[] versionInfo = bundle.getStringArray("versionInfo");
		
		if(versionInfo!=null && versionInfo.length>1){
			int cur = getVersionName(mContext);
			int version = 0;
			
			try {
				 version = Integer.parseInt(versionInfo[0].replace(".", ""));
			} catch (Exception e) {}
			
			if(version>cur)
				return versionInfo[1];
		}
	}
	return null;
}
public static int getVersionName(Context context) {
	String pkName = context.getPackageName();
	String versionName = "";
	int version = 0;
	try {
		versionName = context.getPackageManager().getPackageInfo(pkName, 0).versionName;
		version = Integer.parseInt(versionName.replace(".", ""));
	} catch (Exception e) {
		e.printStackTrace();
	}

	return version;
}
/**
 * 获取软件最新版本号
 * 
 * @param softType
 */
public static Bundle getSoftVersion(String softType) {
	String URL = "http://www.mapgoo.net/admin/versionadmin/GetVersion.aspx?fun=1&SoftType=" + softType;
	InputStream inputStream = getResponse(URL);
	Bundle bundle = new Bundle();
	// 解析JSON begin
	try {
		StringBuffer buffer = new StringBuffer();
		if (inputStream != null) {
			BufferedReader bf = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
			String temp;
			while ((temp = bf.readLine()) != null) {
				buffer.append(temp);
			}
			bf.close();

			JSONObject object = (JSONObject) new JSONTokener(buffer.toString()).nextValue();
			// 获取到所有的信息
			JSONArray info = object.getJSONArray("MG");
			// 得到Info下的第一条信息
			JSONObject objectName = (JSONObject) info.opt(0);
			// 判断数据是否为空
			JSONArray state = objectName.getJSONArray("Status");
			// 得到第一行状态信息
			JSONObject oState = (JSONObject) state.opt(0);

			String Result = oState.getString("Result");
			// 是否成功
			bundle.putString("Result", Result);

			// 判断返回结果是否成功，如果不成功则获取失败原因
			if (Result.equals("0")) {
				bundle.putString("Reason", oState.getString("Reason"));
			} else {// 如果成功获取信息，则解析用所有的信息

				// 得到Info下的第二条信息
				JSONObject obj_pageInfo = (JSONObject) info.opt(1);

				JSONArray array_pageInfo = obj_pageInfo.getJSONArray("Info");

				JSONObject obj_pageInfo_child = (JSONObject) array_pageInfo.opt(0);
				String[] versionInfo = new String[obj_pageInfo_child.length()];
				versionInfo[0] = obj_pageInfo_child.getString("Version"); // 版本号
				versionInfo[1] = obj_pageInfo_child.getString("VerUrl"); // apk
				// 安装包的路径
				versionInfo[2] = obj_pageInfo_child.getString("UpdateTime"); // 更新时间
				versionInfo[3] = obj_pageInfo_child.getString("UpContent"); // 新版本的新增功能

				bundle.putStringArray("versionInfo", versionInfo);
			}

		} else {
			bundle.putString("Result", "error:获取数据失败");
		}

	} catch (ClassCastException e) {
		bundle.putString("Result", "error:JSON解析失败");
		e.printStackTrace();
	} catch (IOException e) {

		bundle.putString("Result", "error:网络连接失败");
		e.printStackTrace();
	} catch (JSONException e) {

		bundle.putString("Result", "error:JSON解析失败");
		e.printStackTrace();
	}
	// 解析JSON end

	return bundle;
}

public static InputStream getResponse(String url) {
	try {
		HttpParams httpParameters;
		httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, 30000);
		HttpConnectionParams.setSoTimeout(httpParameters, 60000);
		DefaultHttpClient mClient = new DefaultHttpClient(httpParameters);
		url = url.replace(" ", "%20");
		HttpGet getMethod = new HttpGet(url);
		HttpResponse response = mClient.execute(getMethod);
		StatusLine statusLine = response.getStatusLine();
		
		if (statusLine.getStatusCode() != HttpStatus.SC_OK) {
			getMethod.abort();
			return null;
		}
		
		HttpEntity entry = response.getEntity();
		InputStream mInputStream = entry.getContent();

		return mInputStream;
	} catch (Exception e) {
		e.printStackTrace();
	}
	return null;
}

}
