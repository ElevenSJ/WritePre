package com.easier.writepre.entity;

import java.io.Serializable;

/**
 * 范字
 * 
 * @author chenhong
 * 
 */
public class WordInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	public String getC_w_id() {
		return c_w_id;
	}

	public void setC_w_id(String c_w_id) {
		this.c_w_id = c_w_id;
	}

	private String c_w_id;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	private String url;

}
