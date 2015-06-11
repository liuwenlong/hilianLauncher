package com.example.cloudmirror.ui;

import com.mapgoo.eagle.R;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeMoreActivity extends BaseActivity {

	
	@Override
	protected void setContentView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_home_more);
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
			,R.id.function_item_7,R.id.function_item_8,R.id.function_item_9
			};
	private void initShortcutMenu(){
		for(int i=0;i<resShortcutId.length;i++){
			View item = findViewById(resShortcutId[i]);
			item.setVisibility(View.INVISIBLE);
		}
		int i=0;
		shortcutGP.setResShortcutId(resShortcutId[i]);i++;
		shortcutFM.setResShortcutId(resShortcutId[i]);i++;
		shortcutJY.setResShortcutId(resShortcutId[i]);i++;
		shortcutZS.setResShortcutId(resShortcutId[i]);i++;
		shortcutLL.setResShortcutId(resShortcutId[i]);i++;
		shortcutST.setResShortcutId(resShortcutId[i]);i++;
		shortcutAB.setResShortcutId(resShortcutId[i]);i++;
		
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
				}
			});
		}
		protected abstract void doAction();
	}
	
	ShortcutItem  shortcutGP = new ShortcutItem(R.drawable.home_more_gupiao,R.string.home_more_gupiao){
		@Override
		protected void doAction() {
		}
	};
	ShortcutItem  shortcutFM = new ShortcutItem(R.drawable.home_more_fm,R.string.home_more_fm){
		@Override
		protected void doAction() {
		}
	};
	ShortcutItem  shortcutJY = new ShortcutItem(R.drawable.home_more_jiayou,R.string.home_more_jiayou){
		@Override
		protected void doAction() {
		}
	};
	ShortcutItem  shortcutZS = new ShortcutItem(R.drawable.home_more_zhushou,R.string.home_more_zhushou){
		@Override
		protected void doAction() {
		}
	};
	ShortcutItem  shortcutLL = new ShortcutItem(R.drawable.home_more_liuliang,R.string.home_more_liuliang){
		@Override
		protected void doAction() {
		}
	};
	ShortcutItem  shortcutST = new ShortcutItem(R.drawable.home_more_settings,R.string.home_more_settings){
		@Override
		protected void doAction() {
		}
	};
	ShortcutItem  shortcutAB = new ShortcutItem(R.drawable.home_more_about,R.string.home_more_about){
		@Override
		protected void doAction() {
		}
	};
}
