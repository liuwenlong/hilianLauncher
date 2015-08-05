package com.example.cloudmirror.ui;

import com.example.cloudmirror.ui.MainActivity.ShortcutItem;
import com.example.cloudmirror.ui.activity.GasStationActivity;
import com.mapgoo.carlife.main.R;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeMoreActivity extends BaseActivity {

	
	@Override
	protected void setContentView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_home_more_2);
	}

	@Override
	protected void initData(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void initViews() {
		// TODO Auto-generated method stub
		initShortcutMenu();
	}

	@Override
	protected void handleData() {

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.item_back_click:
			case R.id.home_more_close:
				finish();
				break;
	
			default:
				break;
		}
	}
	
	private int resShortcutId[]={
			R.id.function_item_1,R.id.function_item_2,R.id.function_item_3 ,
			R.id.function_item_4,R.id.function_item_5,R.id.function_item_6
			,R.id.function_item_7,R.id.function_item_8
	};
	private void initShortcutMenu(){
		for(int i=0;i<resShortcutId.length;i++){
			View item = findViewById(resShortcutId[i]);
			item.setVisibility(View.INVISIBLE);
		}
		int i=0;
		shortcutXCZS.setResShortcutId(resShortcutId[i]);i++;
		shortcutWZCX.setResShortcutId(resShortcutId[i]);i++;
		shortcutJYZ.setResShortcutId(resShortcutId[i]);i++;
		shortcutSP.setResShortcutId(resShortcutId[i]);i++;
		shortcutDT.setResShortcutId(resShortcutId[i]);i++;
		shortcutSZ.setResShortcutId(resShortcutId[i]);i++;
		shortcutGY.setResShortcutId(resShortcutId[i]);i++;
		shortcutFH.setResShortcutId(resShortcutId[i]);i++;
	}
	public abstract class ShortcutItem{
		int resShortcutIcon;
		int resShortcutName;
		int resShortcutId;
		View parentView;
		
		ShortcutItem(int icon,int name){
			resShortcutIcon = icon;
			resShortcutName = name;
		}
		Handler mShortcutHandler = new Handler();
		public void setResShortcutId(int resShortcutId){
			View item = findViewById(resShortcutId);
			((ImageView)item.findViewById(R.id.home_more_item_icon)).setImageResource(resShortcutIcon);
			((TextView)item.findViewById(R.id.home_more_item_name)).setText(resShortcutName);
			item.setVisibility(View.VISIBLE);
			item.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
						doAction();
						//finish();
				}
			});
		}
		protected abstract void doAction();
	}
	ShortcutItem  shortcutXCZS = new ShortcutItem(R.drawable.home_item_xczs,R.string.home_more_xczs){
		@Override
		protected void doAction(){
			startActivity("com.mapgoo.diruite", "com.example.cloudmirror.ui.MainActivity", null);
		}
	};
	ShortcutItem  shortcutWZCX = new ShortcutItem(R.drawable.home_item_wzcx,R.string.home_more_wzcx){
		@Override
		protected void doAction(){
			Intent intent = new Intent().setClassName("com.mapgoo.diruite", "com.example.cloudmirror.ui.MainActivity");
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			intent.putExtra("starttag", "weizhang");
			try {
				mContext.startActivity(intent);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	};
	ShortcutItem  shortcutJYZ = new ShortcutItem(R.drawable.home_item_jyz,R.string.home_more_jyz){
		@Override
		protected void doAction(){
			startActivity(new Intent(mContext, GasStationActivity.class));
		}
	};
	ShortcutItem  shortcutSZ = new ShortcutItem(R.drawable.home_item_sz,R.string.home_more_sz){
		@Override
		protected void doAction(){
			startActivity("com.android.settings","com.android.settings.Settings",null);
		}
	};
	ShortcutItem  shortcutGY = new ShortcutItem(R.drawable.home_item_gy,R.string.home_more_gy){
		@Override
		protected void doAction(){
			startActivity(new Intent(mContext, AboutActivity.class));
		}
	};
	ShortcutItem  shortcutSP = new ShortcutItem(R.drawable.home_item_sp,R.string.home_more_sp){
		@Override
		protected void doAction(){
			startActivity("com.mediatek.videoplayer","com.mediatek.videoplayer.MovieListActivity",null);
		}
	};
	ShortcutItem  shortcutDT = new ShortcutItem(R.drawable.home_item_dt,R.string.home_more_dt){
		@Override
		protected void doAction(){
			startActivity("com.hilan.fm","com.hilan.fm.activity.MainActivity",null);
		}
	};
	ShortcutItem  shortcutFH = new ShortcutItem(R.drawable.home_item_fh,R.string.home_more_fh){
		@Override
		protected void doAction(){
			finish();
		}
	};
	private void startActivity(String pkg,String cls,String name){
		MainActivity.startActivity(this, pkg, cls, name);
	}
}
