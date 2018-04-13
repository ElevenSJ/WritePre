package com.easier.writepre.entity;

import java.util.List;

public class ChildCategoryInfo {

	public List<CategoryInfo> getChild() {
		return child;
	}

	public void setChild(List<CategoryInfo> child) {
		this.child = child;
	}

	private List<CategoryInfo> child;

}
