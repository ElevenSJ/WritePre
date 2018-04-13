package com.easier.writepre.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 课时
 * 
 * @author chenhong
 * 
 */
public class ClassHour implements Serializable {

	private static final long serialVersionUID = 1L;
	
	
	private String index="";
	private String title="";
	private String memo="";
	private List<WordInfo> words_ref = new ArrayList<WordInfo>();
	

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public List<WordInfo> getWords_ref() {
		return words_ref;
	}

	public void setWords_ref(List<WordInfo> words_ref) {
		this.words_ref.clear();
		this.words_ref.addAll(words_ref);
	}

}
