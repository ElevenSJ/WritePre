package com.easier.writepre.entity;

import java.util.List;

public class CitysList {

	private List<CitysBean> citys;
	
	private String inital;

	public String getInital() {
		return inital;
	}

	public void setInital(String inital) {
		this.inital = inital;
	}

	public void setCitys(List<CitysBean> citys) {
		this.citys = citys;
	}

	public List<CitysBean> getCitys() {
		return citys;
	}

	public class CitysBean {

		private String bcode;

		private List<Object> coord;

		private String full_spell;

		private String min_spell;

		private String name;

		private String sname;

		public String getBcode() {
			return bcode;
		}

		public void setBcode(String bcode) {
			this.bcode = bcode;
		}

		public List<Object> getCoord() {
			return coord;
		}

		public void setCoord(List<Object> coord) {
			this.coord = coord;
		}

		public String getFull_spell() {
			return full_spell;
		}

		public void setFull_spell(String full_spell) {
			this.full_spell = full_spell;
		}

		public String getMin_spell() {
			return min_spell;
		}

		public void setMin_spell(String min_spell) {
			this.min_spell = min_spell;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getSname() {
			return sname;
		}

		public void setSname(String sname) {
			this.sname = sname;
		}

	}

}