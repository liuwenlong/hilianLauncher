package com.baidu.android.domain;

import java.util.List;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.util.Log;

import com.baidu.android.domain.DomainMap.MapObject;

public class DomainJoke extends Domain{
	public static String NAME = "joke";
	
	public static class MapObject{
		public String appname;
		
	}
	
	public MapObject Object;
	
	public void setObject(MapObject arg){
		Object = arg;
	}
	@Override
	public String doAction(Context context) {
		// TODO Auto-generated method stub

			if(intent.equalsIgnoreCase("play") || intent.equalsIgnoreCase("uninstall")){
				return getJoke(context);
			}
		return null;
	}
	private String  getJoke(Context context) {

        return "朝鲜：大哥，我要做核试验了。\r\n中国：好的，什么时候？\r\n朝：10. 　\r\n中：10？10什么？10天还是10小时？　\r\n朝：9，8,7，6。。。　\r\n中：你大爷的！";
    }
	
	private void startActivity(Context context,ResolveInfo info){
        String pkg = info.activityInfo.packageName;
        String cls = info.activityInfo.name;
         
        ComponentName componet = new ComponentName(pkg, cls);
         
        Intent i = new Intent();
        i.setComponent(componet);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);		
	}
	
	private void deleteApp(Context context,ResolveInfo info){
		String pkg = info.activityInfo.packageName;
		Uri packageURI = Uri.parse("package:"+pkg);
		Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);           
		context.startActivity(uninstallIntent);  
	}
}
