package com.easier.writepre.utils;

import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.widget.EditText;

public class ChineseOrEnglishTextWatcher implements TextWatcher {
	/** 要进行文字输入限制的EditText */
	private EditText mFilter;
	/** 限制的字符数量 */
	private int length;
	/** 空格控制：防止有些手机在进行编辑信息时 空位在最前面 */
	private boolean onceLoad = true;

	public ChineseOrEnglishTextWatcher(EditText mFilter, int length) {
		super();
		this.mFilter = mFilter;
		this.length = length;
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		int countSize = 0;
		Editable editable = mFilter.getText();
		String str = editable.toString();
		char[] array = str.toCharArray();
		int i = 0;
		if (onceLoad) {
			mFilter.setSelection(s.length());
			onceLoad = false;
		}
		for (i = 0; i < array.length; i++) {
			if ((char) (byte) array[i] != array[i])/** 判断是否为中文 */
			{
				countSize += 2;
				/** 如果为中文或者中文特殊符号则占两个字节 */
			} else {
				countSize += 1;
				/** 英文则占一个字节 */
			}
			if (countSize > length) {
				/** 达到最大值 */
				int selEndIndex = Selection.getSelectionEnd(editable);
				str = editable.toString();
				String newStr = str.substring(0, i);
				mFilter.setText(newStr);
				editable = mFilter.getText();
				int newLen = editable.length();
				if (selEndIndex > newLen) {
					selEndIndex = editable.length();
				}
				Selection.setSelection(editable, selEndIndex);
				break;
			}
		}

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		String data = s.toString();
	}

	@Override
	public void afterTextChanged(Editable s) {
	}
}
