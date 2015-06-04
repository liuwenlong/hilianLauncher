package com.example.cloudmirror.ui;

import java.util.ArrayList;
import com.mapgoo.diruite.R;
import com.example.cloudmirror.bean.MsgInfo;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;



public class MsgListActivity extends BaseActivity {
	
	private ListView mMsgListview;
	private MsgAdapter mMsgAdapter;
	private ArrayList<MsgInfo> mMsgList = new  ArrayList<MsgInfo>();
	@Override
	protected void setContentView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_msglist);
	}

	@Override
	protected void initData(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void initViews() {
		// TODO Auto-generated method stub
		mMsgListview = (ListView)findViewById(R.id.msglist);
		mMsgAdapter = new MsgAdapter(mContext, mMsgList);
		mMsgListview.setAdapter(mMsgAdapter);
		mMsgListview.setEmptyView(findViewById(R.id.empty_text));
	}

	@Override
	protected void handleData() {
		// TODO Auto-generated method stub
		
//		mMsgList.add(new MsgInfo());
//		mMsgAdapter.notifyDataSetChanged();
	}
	

	public class MsgAdapter extends BaseAdapter{
		ArrayList<MsgInfo> mDataList;	
		Context mContext;
		public MsgAdapter(Context context, ArrayList<MsgInfo> list){
			mDataList = list;
			mContext = context;
		}
		@Override
		public int getCount() {
			return mDataList.size();
		}
		@Override
		public Object getItem(int position) {
			return mDataList.get(position);
		}
		@Override
		public long getItemId(int position) {
			return position;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null)
				convertView = View.inflate(mContext, R.layout.list_item_msg, null);
			inflateView(convertView,mDataList.get(position));
			return convertView;
		}
		
		void inflateView(View view,MsgInfo info){

		}
		
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	
	public void backbtnclick(View v){
		finish();
	}
	
}
