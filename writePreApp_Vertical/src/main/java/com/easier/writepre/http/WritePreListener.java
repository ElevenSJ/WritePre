package com.easier.writepre.http;

public interface WritePreListener<T> {
	public void onResponse(T response);
	public void onResponse(String tag,T response);
}
