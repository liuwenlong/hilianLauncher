package com.example.cloudmirror.bean;

import java.io.Serializable;
import java.sql.SQLException;

import android.util.Log;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 概述: 用户模型, 用户GSON实体类转换、ORM的使用
 * 
 * @orignalAuthor yao
 * @createTime 2014年11月6日 下午6:44:26
 * 
 * @improvedAuther yao
 * @modifyTime 2014年11月6日
 */

public class User implements Serializable {

	private static final long serialVersionUID = 103983357602894002L;

	// @DatabaseField(generatedId = true)
	private int _id; // 自增id
	private int userId;
	private String userMobile;

	// 雪豹新加------------
	private String userName;
	private String idCardNum; // 身份证号
	private boolean isAdmin = false; // 是否是管理员
	private String authToken;
	private int loginCount;
	private String loginDate; // TIPS，注意时间的转换
	private String userPwd;
	private String userPwdPlain; // 密码明文
	private int isData; // 资料是否完善
	private String objectId; // 账号下对应的设备列表

	private Boolean isInvite; // 是否为邀请用户

	// 用户登录类型
	private final String userLoginType = "1"; // TODO 兼容老接口，这里暂时写为1
	// private final String userAndPwd = "username=1&pwd=1"; // 兼容老接口，这里暂时写为1

	private String shareurl; // 分享链接
	private String qrcode; // App下载二维码

	private int passwordtip;

	public User() {
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getAuthToken() {
		return authToken;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	public int getLoginCount() {
		return loginCount;
	}

	public void setLoginCount(int loginCount) {
		this.loginCount = loginCount;
	}

	public String getLoginDate() {
		return loginDate;
	}

	public void setLoginDate(String loginDate) {
		this.loginDate = loginDate;
	}

	public String getUserPwd() {
		return userPwd;
	}

	public void setUserPwd(String userPwd) {
		this.userPwd = userPwd;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	public int getIsData() {
		return isData;
	}

	public void setIsData(int isData) {
		this.isData = isData;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getUserMobile() {
		return userMobile;
	}

	public void setUserMobile(String userMobile) {
		this.userMobile = userMobile;
	}

	public Boolean getIsInvite() {
		return isInvite;
	}

	public void setIsInvite(Boolean isInvite) {
		this.isInvite = isInvite;
	}

	public String getIdCardNum() {
		return idCardNum;
	}

	public void setIdCardNum(String idCardNum) {
		this.idCardNum = idCardNum;
	}

	public String getUserPwdPlain() {
		return userPwdPlain;
	}

	public void setUserPwdPlain(String userPwdPlain) {
		this.userPwdPlain = userPwdPlain;
	}

	public int get_id() {
		return _id;
	}

	public String getUserLoginType() {
		return userLoginType;
	}

	// TIPS 老接口兼容-传用户名密码
	public String getUserAndPwd() {
		return "username=" + this.userMobile + "&pwd=" + this.userPwd;
	}

	// TIPS 老接口兼容-传用户名密码
	public String getUserAndPasswd() {
		return "username=" + this.userMobile + "&passwd=" + this.userPwd;
	}

	public String getShareurl() {
		return shareurl;
	}

	public void setShareurl(String shareurl) {
		this.shareurl = shareurl;
	}

	public String getQrcode() {
		return qrcode;
	}

	public void setQrcode(String qrcode) {
		this.qrcode = qrcode;
	}

	public int getPasswordtip() {
		return passwordtip;
	}

	public void setPasswordtip(int passwordtip) {
		this.passwordtip = passwordtip;
	}

}