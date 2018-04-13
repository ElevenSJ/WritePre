package com.easier.writepre.param;

public class SearchWordParams extends BaseBodyParams {

	private String words;
	
	/**
	 * 1草书、2行书、3楷书、4隶书、5篆书
	 */
	private int type;
	
	public SearchWordParams(String words, int type) {
		this.words = words;
		this.type = type;
	}
	
	public SearchWordParams() {
	}
	
	@Override
	public String getProNo() {
		return "2020";
	}

	public String getWords() {
		return words;
	}

	public void setWords(String words) {
		this.words = words;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Override
	public String getUrl() {
		return "/writePieWeb/login";
	}
	
}
