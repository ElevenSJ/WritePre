package com.easier.writepre.entity;

import com.easier.writepre.app.WritePreApp;
import com.easier.writepre.utils.Utils;

import java.util.Map;

public class SocialPropEntity {
	private String _id = "";

	private String sys_prop = "";

	private Map<String, String> imgsrv_pre;

	private Map<String, String> imgsrv_pre_https;

	private String oss_endpoint = "";

	private String oss_bucket = "";

	private String oss_root = "";

	private String app_socail_server = "";

	private String app_socail_server_https = "";

	private String app_school_server = "";

	private String app_school_server_https = "";

	private String share_baseurl = "";

	private String circle_num_limit = "";

	private String city_url = "";

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getSys_prop() {
		return sys_prop;
	}

	public void setSys_prop(String sys_prop) {
		this.sys_prop = sys_prop;
	}

	public String getOss_endpoint() {
		return oss_endpoint;
	}

	public void setOss_endpoint(String oss_endpoint) {
		this.oss_endpoint = oss_endpoint;
	}

	public String getOss_bucket() {
		return oss_bucket;
	}

	public void setOss_bucket(String oss_bucket) {
		this.oss_bucket = oss_bucket;
	}

	public String getOss_root() {
		return oss_root;
	}

	public void setOss_root(String oss_root) {
		this.oss_root = oss_root;
	}

	public String getApp_socail_server() {
//		if (Utils.getCurrentCode(WritePreApp.getApp())<21){
//			//版本号21以下使用http协议
//			return app_socail_server;
//		}else{
			//版本号21及以上使用https协议
			return app_socail_server_https;
//		}
	}

	public void setApp_socail_server(String app_socail_server) {
		this.app_socail_server = app_socail_server;
	}

	public String getShare_baseurl() {
		return share_baseurl.replaceAll("98","88");
	}

	public void setShare_baseurl(String share_baseurl) {
		this.share_baseurl = share_baseurl;
	}

	public String getCircle_num_limit() {
		return circle_num_limit;
	}

	public void setCircle_num_limit(String circle_num_limit) {
		this.circle_num_limit = circle_num_limit;
	}

	public String getCity_url() {
		return city_url;
	}

	public void setCity_url(String city_url) {
		this.city_url = city_url;
	}

	public Map<String, String> getImgsrv_pre() {
//		if (Utils.getCurrentCode(WritePreApp.getApp())<21){
			//版本号21以下使用http协议
//			return imgsrv_pre;
//		}else{
//			//版本号21及以上使用https协议
			return imgsrv_pre_https;
//		}
	}

	public void setImgsrv_pre(Map<String, String> imgsrv_pre) {
		this.imgsrv_pre = imgsrv_pre;
	}

	public String getApp_socail_server_https() {
		return app_socail_server_https;
	}

	public void setApp_socail_server_https(String app_socail_server_https) {
		this.app_socail_server_https = app_socail_server_https;
	}

	public Map<String, String> getImgsrv_pre_https() {
		return imgsrv_pre_https;
	}

	public void setImgsrv_pre_https(Map<String, String> imgsrv_pre_https) {
		this.imgsrv_pre_https = imgsrv_pre_https;
	}

	public String getApp_school_server() {
//		return app_school_server;
		return app_school_server_https;
	}

	public void setApp_school_server(String app_school_server) {
		this.app_school_server = app_school_server;
	}

	public String getApp_school_server_https() {
		return app_school_server_https;
	}

	public void setApp_school_server_https(String app_school_server_https) {
		this.app_school_server_https = app_school_server_https;
	}
}
