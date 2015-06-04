package com.example.cloudmirror.receiver;

import com.example.cloudmirror.service.DataSyncService;
import com.example.cloudmirror.ui.MainActivity;
import com.example.cloudmirror.utils.MyLog;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver{
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		MyLog.D("onReceive-----"+arg1.getAction());
		arg0.startService(new Intent(arg0, DataSyncService.class));
//		Intent intent = new Intent();
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		intent.setClass(arg0, MainActivity.class);
//		arg0.startActivity(intent);
	}
	
}
