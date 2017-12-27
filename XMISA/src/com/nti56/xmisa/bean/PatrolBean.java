package com.nti56.xmisa.bean;

import java.util.List;

public class PatrolBean {

	private String taskId;// 任务号
	private String date;// 巡查日期
	private String meridiem;// 上下午区分 ('0':上午;'1':下午) String mor = isMor.endsWith("1")?"下午":"上午";
	private String orderId;// 巡查单号
	private String building;// 楼栋
	private String person;// 巡查人员
	private String stateNC;// 反馈状态
	private List<PatrolDetailBean> detailBeanList;// 任务明细
	private String Mark = "";// 标记 临时/本地/服务器/待删除

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

	public String getMeridiem() {
		return meridiem;
	}

	public void setMeridiem(String isMor) {
		this.meridiem = isMor;
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

	public List<PatrolDetailBean> getDetailBeanList() {
		return detailBeanList;
	}

	public void setDetailBeanList(List<PatrolDetailBean> detailBeanList) {
		this.detailBeanList = detailBeanList;
	}

	public String getMark() {
		return Mark;
	}
	public void setMark(String mark) {
		this.Mark = mark;
	}

//	public String getSysCode() {
//		return SysCode;
//	}
//	public void setSysCode(String sysCode) {
//		this.SysCode = sysCode;
//	}
	
//TODO
	
//	public String getFloor() {
//		return floor;
//	}
//
//	public void setFloor(String floor) {
//		this.floor = floor;
//	}

}
