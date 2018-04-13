package com.easier.writepre.entity;

import java.io.Serializable;

public class MyListData implements Serializable {
	private int left_image;
	
	private String left_text; 
	
	private int right_image;
	
	private String right_text;
	
	private String center_text;
	
	private String right_button_text;
	
	private int right_button_background;
	
	private int right_button_text_color;
	
	public int getLeft_image() {
		return left_image;
	}

	public void setLeft_image(int left_image) {
		this.left_image = left_image;
	}

	public String getLeft_text() {
		return left_text;
	}

	public void setLeft_text(String left_text) {
		this.left_text = left_text;
	}

	public int getRight_image() {
		return right_image;
	}

	public void setRight_image(int right_image) {
		this.right_image = right_image;
	}

	public String getRight_text() {
		return right_text;
	}

	public void setRight_text(String right_text) {
		this.right_text = right_text;
	}

	public String getCenter_text() {
		return center_text;
	}

	public void setCenter_text(String center_text) {
		this.center_text = center_text;
	}
	
	public String getRight_button_text() {
		return right_button_text;
	}

	public void setRight_button_text(String right_button_text) {
		this.right_button_text = right_button_text;
	}

	public int getRight_button_background() {
		return right_button_background;
	}

	public void setRight_button_background(int right_button_background) {
		this.right_button_background = right_button_background;
	}

	public int getRight_button_text_color() {
		return right_button_text_color;
	}

	public void setRight_button_text_color(int right_button_text_color) {
		this.right_button_text_color = right_button_text_color;
	}

	public MyListData(int left_image, String left_text, int right_image,
			String right_text, String center_text) {
//		super();
		this.left_image = left_image;
		this.left_text = left_text;
		this.right_image = right_image;
		this.right_text = right_text;
		this.center_text = center_text;
	}

	public MyListData(int left_image, String left_text, int right_image,
			String right_text, String center_text, String right_button_text,
			int right_button_background, int right_button_text_color) {
//		super();
		this.left_image = left_image;
		this.left_text = left_text;
		this.right_image = right_image;
		this.right_text = right_text;
		this.center_text = center_text;
		this.right_button_text = right_button_text;
		this.right_button_background = right_button_background;
		this.right_button_text_color = right_button_text_color;
	}
	
	
}
