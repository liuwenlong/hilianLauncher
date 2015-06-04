package com.example.cloudmirror.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.mapgoo.diruite.R;
import com.example.cloudmirror.ui.BaseActivity;

/**
 * 获取验证码进行注册
 * @author feng
 * @time 2015-6-3 16:45:45
 *
 */
public class GetInvadationCodeActivity extends BaseActivity {
	
	private Button nextToRegisterBt;//下一步
	

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId() == nextToRegisterBt.getId()){  //进入车辆品牌选择
			startActivity(new Intent(this, CarBrandUpdateActivity.class));
		}
	}

	@Override
	protected void setContentView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_invadation_code);
		
	}

	@Override
	protected void initData(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void initViews() {
		// TODO Auto-generated method stub
		nextToRegisterBt = (Button) findViewById(R.id.nextToRegisterBt);
		nextToRegisterBt.setOnClickListener(this);
	}

	@Override
	protected void handleData() {
		// TODO Auto-generated method stub
		
	}

}
