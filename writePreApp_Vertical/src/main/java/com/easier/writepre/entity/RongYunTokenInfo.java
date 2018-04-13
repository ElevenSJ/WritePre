package com.easier.writepre.entity;

import java.io.Serializable;

/**
 * 融云Token结构体
 * 
 * @author zhoulu
 * 
 */
public class RongYunTokenInfo implements Serializable {
	private String token = "";

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}