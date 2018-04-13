package com.easier.writepre.utils;

import com.easier.writepre.entity.JointAttentionInfo;

import java.util.Comparator;

/**
 *
 * @author xiaanming
 *
 */
public class PinyinFriendsComparator implements Comparator<JointAttentionInfo> {

	public int compare(JointAttentionInfo o1, JointAttentionInfo o2) {
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
