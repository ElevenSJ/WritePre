package com.easier.writepre.entity;

public class WordFontInfo {

	private String _id;

	private String words;

	private String words2;

	private String author;

	private String era;

	private String sfrom;

	private String memo;

	private String pic_url;

	private String pic_url_min;

	public String get_id() {
		return _id;
	}

	public String getAuthor() {
		return author;
	}

	public String getEra() {
		return era;
	}

	public String getMemo() {
		return memo;
	}

	public String getPic_url() {
		// return Constant.DOWN_URL + pic_url;
		return pic_url;
	}

	public String getPic_url_min() {
		// return Constant.DOWN_URL + pic_url_min;
		return pic_url_min;
	}

	public String getSfrom() {
		return sfrom;
	}

	public String getTxtStr() {
		return words.equals(words2) ? (words + " / " + author + "\n" +  era + " / " + sfrom)
				: (words + " / " + words2 + " / " + author + "\n" + era + " / " + sfrom);
	}

	public String getWords() {
		return words;
	}

	public String getWords2() {
		return words2;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public void setEra(String era) {
		this.era = era;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public void setPic_url(String pic_url) {
		this.pic_url = pic_url;
	}

	public void setPic_url_min(String pic_url_min) {
		this.pic_url_min = pic_url_min;
	}

	public void setSfrom(String sfrom) {
		this.sfrom = sfrom;
	}

	public void setWords(String words) {
		this.words = words;
	}

	public void setWords2(String words2) {
		this.words2 = words2;
	}
}
