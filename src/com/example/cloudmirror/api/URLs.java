package com.example.cloudmirror.api;

import java.io.Serializable;

/**
 * 概述: 接口URL实体类
 * 
 * @author yao
 * @version 1.0
 * @created 2014年11月8日
 */
public class URLs implements Serializable {

	private static final long serialVersionUID = -3371739479972951686L;

	public final static String VERSION_CODE = "0201001";
	
	// 公网/生产服务器
	private final static String SERVER_PRODUCT = "app001.u12580.com";

	// 局域网服务器
	private final static String LOCAL_TEST_SERVER = "192.168.1.69";

	// 内测服务器
	private final static String INTERNEL_TEST_SERVER = "183.62.138.117:9876";

	// 默认局域网服务器
	private static String HOST = LOCAL_TEST_SERVER;

	private static String PRODUCT_NAME = "v4";	// 雪豹业务相关接品
	private static String LOC_SERVICE = "v3";	// 位置服务相关接口 : 位置服务、消息箱、参数设置等
	private static String LOC_SERVICE_4S = "v2";	// 位置服务-4S : 行车宝典、常用电话等H5页面
	
	private final static String TEST_PRODUCT_NAME = "v1";	// 雪豹业务相关接品
	private final static String TEST_LOC_SERVICE = "v3";	// 位置服务相关接口 : 位置服务、消息箱、参数设置等
	private final static String TEST_LOC_SERVICE_4S = "v2";	// 位置服务-4S : 行车宝典、常用电话等H5页面
	private final static String TEST_VOICE_NAME = "v1";	// 雪豹业务相关接品
	
	public static final boolean isInternelTest = false;
	public static final boolean isLocalServer = true;

	static {
		// 更改app当前状态所用的服务器， 分为本地调试、内测、公测， 默认为内测
		if (isInternelTest) {

			if (isLocalServer)
				URLs.HOST = URLs.LOCAL_TEST_SERVER; // 本地测试服务器
			else
				URLs.HOST = URLs.INTERNEL_TEST_SERVER; // 内测域名代理
			
			URLs.PRODUCT_NAME = URLs.TEST_PRODUCT_NAME;
			URLs.LOC_SERVICE = URLs.TEST_LOC_SERVICE;
			URLs.LOC_SERVICE_4S = URLs.TEST_LOC_SERVICE_4S;
		} else {
			URLs.HOST = URLs.SERVER_PRODUCT;
		}
	}

	private final static String HTTP = "http://";
	private final static String HTTPS = "https://";

	private final static String URL_SPLITTER = "/";
	

	
	private final static String API = "api";
	private final static String OPENAPI = "openapi";
	private final static String H5 = "H5";
	
	private final static String PRODUCT_OPENAPI_HOST = "open.u12580.com";	
	private final static String PRODUCT_OPENAPI_PATH = API + URL_SPLITTER +TEST_VOICE_NAME ;	
	private final static String PRODUCT_API_PATH = PRODUCT_NAME + URL_SPLITTER + API;
	private final static String PRODUCT_H5_PATH = PRODUCT_NAME + URL_SPLITTER + H5;
	private final static String TOOLS = LOC_SERVICE + "/tools";
	
	// api地址
	private final static String URL_API_HTTP_HOST = HTTP + HOST + URL_SPLITTER + PRODUCT_API_PATH + URL_SPLITTER;
	// openapi地址
	private final static String URL_OPENAPI_HTTP_HOST = HTTP + PRODUCT_OPENAPI_HOST + URL_SPLITTER + PRODUCT_OPENAPI_PATH + URL_SPLITTER;	
	// H5地址
	private final static String URL_H5_HTTP_HOST = HTTP + HOST + URL_SPLITTER + PRODUCT_H5_PATH + URL_SPLITTER;
	// 工具接口
	private final static String URL_TOOLS_HTTP_HOST = HTTP + HOST + URL_SPLITTER + TOOLS + URL_SPLITTER;
	// 工具接口-生产服务器
	private final static String URL_TOOLS_HTTP_PRODUCT_HOST = HTTP + SERVER_PRODUCT + URL_SPLITTER + TOOLS + URL_SPLITTER;
	// 位置服务
	public final static String URL_LOC_SERVICE_HTTP_HOST = HTTP + HOST + URL_SPLITTER + LOC_SERVICE + URL_SPLITTER;
	// 位置服务4S接口
	public final static String URL_LOC_SERVICE_4S_HTTP_HOST = HTTP + HOST + URL_SPLITTER + LOC_SERVICE_4S + URL_SPLITTER;
		
//	private final static String URL_API_HTTPS_HOST = HTTPS + HOST + URL_SPLITTER + PRODUCT_API_PATH + URL_SPLITTER;

	// 获取短信验证码
	public final static String SMS_VERIFY = URL_API_HTTP_HOST + "smsVerify";
	
	public final static String VOICE_YES_NO = URL_OPENAPI_HTTP_HOST + "voice";
	public final static String CAR_HOME = URL_API_HTTP_HOST + "car_homeinfo/homeinfo";
	
	public final static String IMG_UPLOAD = "http://open.u12580.com/api/v1/image";
	public final static String VIDEO_UPLOAD = "http://open.u12580.com/api/v1/video";
}
