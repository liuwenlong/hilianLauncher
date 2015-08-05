package com.example.cloudmirror.widget;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import android.os.Handler;
import android.os.Message;

import com.example.cloudmirror.utils.DBmanager;
import com.example.cloudmirror.utils.DBmanager.DbItem;
import com.example.cloudmirror.utils.MyLog;

import de.greenrobot.event.EventBus;
public class NetWork {
	private final String Server_ip = "183.62.138.9";
	private final int Server_port = 2233;
	
	private Socket mSocket;
	private InputStream mSocketReader;
	private OutputStream mSocketWriter;
	private DBmanager mDBmanager;
	private Handler mHandler;
	private Thread mMainThread;
	public final  static int ReConnectTime = 30*1000;
	
	private boolean NetWorkConnect = false;
	
	public NetWork(){
		mDBmanager = DBmanager.getInase();
	}
	public NetWork(Handler h){
		mHandler = h;
		mDBmanager = DBmanager.getInase();
	}
	public void start(){
		mMainThread =new Thread(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				mainRun();
			}
			
		};
		mMainThread.start();
	}
	
	public void mainRun(){
		reStartSocket();
		while(true){
			DbItem item = mDBmanager.getLastContent();
			
			if(item != null){
				
				if(sendMsg(item.content)){
					mDBmanager.deleteItem(item.id);
					msleep(200);
				}else{
					msleep(ReConnectTime);
					reStartSocket();
				}
				
			}else{
				MyLog.D("没有数据,等待30秒");
				msleep(ReConnectTime*10);
			}
		
		}
	}
	

	
	public boolean sendMsg(String msg){
		boolean ret = false;

		try {
			if(mSocketWriter!=null){
				MyLog.D("尝试发送数据");
				mSocketWriter.write(msg.getBytes());
				MyLog.I("发送数据成功:"+msg);
				ret = true;
			}else{
				MyLog.E("尝试发送数据失败,mSocketWriter为空");
			}
		} catch (IOException e) {
			e.printStackTrace();	
			MyLog.E("发送数据失败,IOException");
		}
		
		return ret;
	}
	
	public void reStartSocket(){
		MyLog.W("Socket重连");
		closeNet();
		openNet();
	}
	
	public void closeNet(){
		try {
				if (mSocketReader != null) {
					mSocketReader.close();
					mSocketReader = null;
				}
				if (mSocketWriter != null) {
					mSocketWriter.close();
					mSocketWriter = null;
				}
				if (mSocket != null) {
					mSocket.close();
					mSocket = null;
				}
		} catch (IOException e) {
				e.printStackTrace();
		}
		MyLog.W("Socket关闭");
	}
	
	public void openNet(){
		try {
			mSocket = new Socket(Server_ip, Server_port);
			mSocket.setKeepAlive(true);
			mSocket.setTcpNoDelay(true);
			//mSocket.setSoTimeout(5000);// 设置超时时间(毫秒)
			//startRead();
			mSocketReader = mSocket.getInputStream();
			mSocketWriter = mSocket.getOutputStream();	
			MyLog.D("socket连接成功");
		} catch (Exception e) {
			e.printStackTrace();
			MyLog.D("创建Socket连接失败");
			closeNet();
		}
	}
	private void startRead(){
		new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				while(mSocket!=null&&mSocket.isConnected()){
					try {
						InputStream in = mSocket.getInputStream();
						byte[] buffer = new byte[30];
						in.read(buffer);	
						
						if(buffer[0]!=0){
							sendMessage(new String(buffer));
						}else{
							msleep(10*1000);
						}
					}catch(IOException e){
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}.start();
	}
	private void sendMessage(String msg){
		Message message = new Message();
		message.what = 1;
		message.obj = msg;
		mHandler.sendMessage(message);
		MyLog.D("sendMessage msg="+msg);
	}
	public boolean isNetWorkConnect(){
		if(mSocketWriter == null){
			if(NetWorkConnect)
				EventBus.getDefault().post("NetWorkDisConnect");
			NetWorkConnect = false;
			return false;
		}else{
			if(NetWorkConnect == false)
				EventBus.getDefault().post("NetWorkConnect");
			NetWorkConnect = true;
			return true;
		}
	}
	public void msleep(int dur){
		
		try {
			Thread.sleep(dur);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
