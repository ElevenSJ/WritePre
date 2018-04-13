package com.easier.writepre.jni;

public class JniManager {

	static {
		System.loadLibrary("writepre");
	}

	public static native String base64Encode(String data);

	public static native String base64Decode(String data);

//	public static native String EncodeAES(String data);
//
//	public static native String DecodeAES(String data);
	
	public static native String getAESCode();

	public static native JniResult getYouKuUploadInfo(String url, String client_id, String token, String title,
			String tags, String fileName, String file_md5, String file_size);

}
