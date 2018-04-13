package com.easier.writepre.entity;

import java.io.Serializable;

public class MarkNo implements Serializable{
		private int no;
		private String title;
		private String bgcolor;

		public int getNo() {
			return no;
		}

		public void setNo(int no) {
			this.no = no;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getBgcolor() {
			return bgcolor;
		}

		public void setBgcolor(String bgcolor) {
			this.bgcolor = bgcolor;
		}

	}
