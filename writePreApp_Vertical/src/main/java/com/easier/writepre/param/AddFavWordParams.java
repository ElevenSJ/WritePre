package com.easier.writepre.param;

import java.util.ArrayList;
import java.util.List;

public class AddFavWordParams extends BaseBodyParams {

	private int direction;

	private String words_txt;

	private List<AddFavID> words;

	public AddFavWordParams(boolean isV, List<String> strs) {
		direction = isV ? 1 : 0;
		words = new ArrayList<AddFavWordParams.AddFavID>();
		for (String item : strs) {
			words.add(new AddFavID(item));
		}
		words_txt = "";
	}

	@Override
	public String getProNo() {
		return "2016";
	}

	class AddFavID {

		private String words_id;

		public AddFavID(String words_id) {
			this.words_id = words_id;
		}
	}
}
