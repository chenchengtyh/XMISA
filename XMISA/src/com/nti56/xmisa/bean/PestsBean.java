package com.nti56.xmisa.bean;

import java.util.List;

public class PestsBean {

	private String taskId;// 任务号
	private String OrderId;// 虫情单号
	private String Building;// 楼栋
	private String StateNC;// 反馈状态
	private String date;// 采集时间
	private String Person;// 巡查人员
	private String Note;// 备注
	private String Mark = "";// 标记 临时/本地/服务器/待删除

	private List<PestsDetailBean> detailBeanList;// 任务明细

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getOrderId() {
		return OrderId;
	}

	public void setOrderId(String orderId) {
		this.OrderId = orderId;
	}

	public String getBuilding() {
		return Building;
	}

	public void setBuilding(String building) {
		this.Building = building;
	}

	public String getStateNC() {
		return StateNC;
	}

	public void setStateNC(String stateNC) {
		this.StateNC = stateNC;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getPerson() {
		return Person;
	}

	public void setPerson(String person) {
		this.Person = person;
	}

	public String getNote() {
		return Note;
	}

	public void setNote(String note) {
		this.Note = note;
	}

	public String getMark() {
		return Mark;
	}

	public void setMark(String mark) {
		this.Mark = mark;
	}

	public List<PestsDetailBean> getDetailBeanList() {
		return detailBeanList;
	}

	public void setDetailBeanList(List<PestsDetailBean> detailBeanList) {
		this.detailBeanList = detailBeanList;
	}

}
