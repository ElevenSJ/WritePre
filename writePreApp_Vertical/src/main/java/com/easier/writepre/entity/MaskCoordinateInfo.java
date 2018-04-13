package com.easier.writepre.entity;

import java.io.Serializable;

/**
 * 蒙版结构体
 * 
 * @author zhoulu
 * 
 */
public class MaskCoordinateInfo implements Serializable {
	private int index;//序号第几个蒙版
	private int topLeftX;//左上角x轴坐标
	private int topLeftY;//左上角y轴坐标
	private int bgRes;//资源id

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getBgRes() {
		return bgRes;
	}

	public void setBgRes(int bgRes) {
		this.bgRes = bgRes;
	}

	public int getTopLeftX() {
		return topLeftX;
	}

	public void setTopLeftX(int topLeftX) {
		this.topLeftX = topLeftX;
	}

	public int getTopLeftY() {
		return topLeftY;
	}

	public void setTopLeftY(int topLeftY) {
		this.topLeftY = topLeftY;
	}
}