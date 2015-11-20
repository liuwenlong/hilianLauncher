package com.example.cloudmirror.receiver;

import com.example.cloudmirror.service.DataSyncService;
import com.example.cloudmirror.utils.MyLog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver{
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		MyLog.D("onReceive-----"+arg1.getAction());
		arg0.startService(new Intent(arg0, DataSyncService.class));
	}
	
}
