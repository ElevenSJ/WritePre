package com.easier.writepre.utils;

import com.easier.writepre.entity.CircleInfo;
import com.easier.writepre.entity.CircleMsgInfo;
import com.easier.writepre.entity.JointAttentionInfo;

import java.util.Comparator;

/**
 *
 * @author xiaanming
 *
 */
public class PinyinCircleMsginfoComparator implements Comparator<CircleInfo> {

	public int compare(CircleInfo o1, CircleInfo o2) {
		if (o1.getSortLetters().equals("@")
				|| o2.getSortLetters().equals("#")) {
			return -1;
		} else if (o1.getSortLetters().equals("#")
				|| o2.getSortLetters().equals("@")) {
			return 1;
		} else {
			return o1.getSortLetters().compareTo(o2.getSortLetters());
		}
	}

}
