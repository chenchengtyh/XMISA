package com.nti56.xmisa.bean;

public class FumigateDetailBean {

	private String taskId;// 主任务id
	private String detailId;// 任务明细id
	private String trayID;// 货位编码
	private String trayName;// 货位名称&楼层&熏蒸室
	private String floor;// 楼层
	private String location;// 位置
	private String concentration6;// 6小时浓度
	private String concentration12;// 12小时浓度
	private String concentration24;// 24小时浓度
	private String concentration48;// 48小时浓度
	private String concentration72;// 72小时浓度
	private String concentration96;// 96小时浓度
	private String concentration144;// 144小时浓度
	private String concentration216;// 216小时浓度
	private String picture;// 图片
	private String pictureVersion;// 图片版本

	// private String trayID;// 货物编码
	// private String dataFirst;// 第一次日期
	// private String concentrationFirst;// 第一次浓度
	// private String dataSecond;// 第二次日期
	// private String concentrationSecond;// 第二次浓度

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getDetailId() {
		return detailId;
	}

	public void setDetailId(String detailId) {
		this.detailId = detailId;
	}

	public String getTrayID() {
		return trayID;
	}

	public void setTrayID(String trayID) {
		this.trayID = trayID;
	}

	public String getTrayName() {
		return trayName;
	}

	public void setTrayName(String trayName) {
		this.trayName = trayName;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public String getPictureVersion() {
		return pictureVersion;
	}

	public void setPictureVersion(String pictureVersion) {
		this.pictureVersion = pictureVersion;
	}

	public String getFloor() {
		return floor;
	}

	public void setFloor(String floor) {
		this.floor = floor;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getConcentration6() {
		return concentration6;
	}

	public void setConcentration6(String concentration6) {
		this.concentration6 = concentration6;
	}

	public String getConcentration12() {
		return concentration12;
	}

	public void setConcentration12(String concentration12) {
		this.concentration12 = concentration12;
	}

	public String getConcentration24() {
		return concentration24;
	}

	public void setConcentration24(String concentration24) {
		this.concentration24 = concentration24;
	}

	public String getConcentration48() {
		return concentration48;
	}

	public void setConcentration48(String concentration48) {
		this.concentration48 = concentration48;
	}

	public String getConcentration72() {
		return concentration72;
	}

	public void setConcentration72(String concentration72) {
		this.concentration72 = concentration72;
	}

	public String getConcentration96() {
		return concentration96;
	}

	public void setConcentration96(String concentration96) {
		this.concentration96 = concentration96;
	}

	public String getConcentration144() {
		return concentration144;
	}

	public void setConcentration144(String concentration144) {
		this.concentration144 = concentration144;
	}

	public String getConcentration216() {
		return concentration216;
	}

	public void setConcentration216(String concentration216) {
		this.concentration216 = concentration216;
	}

	// public String getTrayID() {
	// return trayID;
	// }
	//
	// public void setTrayID(String trayID) {
	// this.trayID = trayID;
	// }

}
