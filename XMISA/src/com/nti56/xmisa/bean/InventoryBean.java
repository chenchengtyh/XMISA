package com.nti56.xmisa.bean;

import java.util.List;

public class InventoryBean {

	private String Date;// 单据日期
	private String TaskId;// 任务号
	private String OrderId;// 盘点单号
	private String Type;// 业务类型
	private String Building;// 楼栋
//	private String IvtState;// 盘点状态
	private String TrayTotle;// 盘点货位总数
	private String TrayDone;// 已盘点货位数
	private String IvtNum;// 盘点数量
	private String IvtDone;// 已完成数量
	private String IvtWait;// 待完成数量
	private String StateNC;// 反馈状态

//	private String Person;// 巡查人员
//	private String Note;// 备注
//	private String Mark = "";// 标记 临时/本地/服务器/待删除

	private List<InventoryDetailBean> DetailBeanList;// 任务明细

	public String getTaskId() {
		return TaskId;
	}

	public void setTaskId(String taskId) {
		this.TaskId = taskId;
	}

	public String getDate() {
		return Date;
	}

	public void setDate(String date) {
		this.Date = date;
	}

	public String getOrderId() {
		return OrderId;
	}

	public void setOrderId(String orderId) {
		this.OrderId = orderId;
	}

	public String getType() {
		return Type;
	}

	public void setType(String type) {
		this.Type = type;
	}

	public String getBuilding() {
		return Building;
	}

	public void setBuilding(String building) {
		this.Building = building;
	}

	public String getTrayTotle() {
		return TrayTotle;
	}

	public void setTrayTotle(String trayTotle) {
		this.TrayTotle = trayTotle;
	}

	public String getTrayDone() {
		return TrayDone;
	}

	public void setTrayDone(String trayDone) {
		this.TrayDone = trayDone;
	}

	public String getIvtNum() {
		return IvtNum;
	}

	public void setIvtNum(String ivtNum) {
		this.IvtNum = ivtNum;
	}

	public String getIvtDone() {
		return IvtDone;
	}

	public void setIvtDone(String ivtDone) {
		this.IvtDone = ivtDone;
	}

	public String getIvtWait() {
		return IvtWait;
	}

	public void setIvtWait(String ivtWait) {
		this.IvtWait = ivtWait;
	}

	public String getStateNC() {
		return StateNC;
	}

	public void setStateNC(String stateNC) {
		this.StateNC = stateNC;
	}

	public List<InventoryDetailBean> getDetailBeanList() {
		return DetailBeanList;
	}

	public void setDetailBeanList(List<InventoryDetailBean> detailBeanList) {
		this.DetailBeanList = detailBeanList;
	}

//	public String getIvtState() {
//		return IvtState;
//	}
//
//	public void setIvtState(String ivtState) {
//		this.IvtState = ivtState;
//	}
//
//	public String getPerson() {
//		return Person;
//	}
//
//	public void setPerson(String person) {
//		this.Person = person;
//	}
//
//	public String getNote() {
//		return Note;
//	}
//
//	public void setNote(String note) {
//		this.Note = note;
//	}
//
//	public String getMark() {
//		return Mark;
//	}
//
//	public void setMark(String mark) {
//		this.Mark = mark;
//	}

}
