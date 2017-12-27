package com.nti56.xmisa.bean;

import java.util.List;

public class FumigateBean {

	private String taskId;
	private String date;// 单据日期
	private String orderId;// 熏蒸单号
	private String building;// 楼栋
	private String room;// 熏蒸室
	private String method;// 熏蒸方法
	private String dosingdate;// 用药日期
	private String person;// 质检人员
	private String stateNC;// 任务状态
	private String drug;// 熏蒸药剂
	private String airStart;// 通风散毒日期
	private String airEnd;// 通风结束日期
	private String volume;// 熏蒸体积
	private String dosage;// 用药量
	private String unit;// 用药单位
	private String note;// 备注

	private List<FumigateDetailBean> detailBeanList;// 任务明细

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getBuilding() {
		return building;
	}

	public void setBuilding(String building) {
		this.building = building;
	}

	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getDosingDate() {
		return dosingdate;
	}

	public void setDosingDate(String dosingdate) {
		this.dosingdate = dosingdate;
	}

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public String getStateNC() {
		return stateNC;
	}

	public void setStateNC(String state) {
		this.stateNC = state;
	}

	public String getDrug() {
		return drug;
	}

	public void setDrug(String drug) {
		this.drug = drug;
	}

	public String getAirStart() {
		return airStart;
	}

	public void setAirStart(String airStart) {
		this.airStart = airStart;
	}

	public String getAirEnd() {
		return airEnd;
	}

	public void setAirEnd(String airEnd) {
		this.airEnd = airEnd;
	}

	public List<FumigateDetailBean> getDetailBeanList() {
		return detailBeanList;
	}

	public void setDetailBeanList(List<FumigateDetailBean> detailBeanList) {
		this.detailBeanList = detailBeanList;
	}

	public String getVolume() {
		return volume;
	}

	public void setVolume(String volume) {
		this.volume = volume;
	}

	public String getDosage() {
		return dosage;
	}

	public void setDosage(String dosage) {
		this.dosage = dosage;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

}
