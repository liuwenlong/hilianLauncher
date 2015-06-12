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

public class DomainApp extends Domain{
	public static String NAME = "app";
	
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
		if(Object.appname != null){
			if(intent.equalsIgnoreCase("open") || intent.equalsIgnoreCase("uninstall")){
				return openApp(context, Object.appname);
			}else{
				Intent intent = new Intent(); 
				intent.setAction(Intent.ACTION_WEB_SEARCH); 
				intent.putExtra(SearchManager.QUERY,String.format("%s",Object.appname));
				context.startActivity(intent); 
				return "正在搜索"+Object.appname;
			}
		}
		
		return null;
	}
	private String  openApp(Context context,String appname) {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        
        List<ResolveInfo> mApps = context.getPackageManager().queryIntentActivities(mainIntent, 0);
        
        for(ResolveInfo app:mApps){
        	String name = app.activityInfo.name;
        	String pack = app.activityInfo.packageName;
        	String label = app.loadLabel(context.getPackageManager()).toString();
        	Log.d("TAG", "name="+name+",pack="+pack+",label="+label+",appname="+appname);
        	if(label != null){
	        	if(label.equalsIgnoreCase(appname) || label.contains(appname) || appname.contains(label)){
	        		if(intent.equalsIgnoreCase("open")){
	        			startActivity(context, app);
	        			return "正在打开"+label;
	        		}else if(intent.equalsIgnoreCase("uninstall")){
	        			deleteApp(context, app);
	        			return "正在删除"+label;
	        		}
	        		break;
	        	}
        	}
        }
        return "没有找到"+appname;
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
