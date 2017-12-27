package com.nti56.xmisa.bean;

import java.util.List;

public class SurveyBean {

	private String taskId;// 任务号
	private String date;// 单据日期
	private String orderId;// 普查单号
	private String building;// 楼栋
	private String floor;// 仓库楼层
	private String person;// 操作人
	private String stateNC;// 反馈状态

	private String detailNum;// 明细数量
	private List<SurveyDetailBean> detailBeanList;// 任务明细

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

	public String getFloor() {
		return floor;
	}

	public void setFloor(String floor) {
		this.floor = floor;
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

	public void setStateNC(String stateNC) {
		this.stateNC = stateNC;
	}

	public String getDetailNum() {
		return detailNum;
	}

	public void setDetailNum(int detailNum) {
		this.detailNum = detailNum + "";
	}

	public List<SurveyDetailBean> getDetailBeanList() {
		return detailBeanList;
	}

	public void setDetailBeanList(List<SurveyDetailBean> detailBeanList) {
		this.detailBeanList = detailBeanList;
	}

}
