package com.nti56.xmisa.bean;

import java.util.List;

/*
 * 收获任务
 */
public class ReceiptBean {

	private String taskId;// 任务号
	private String date;// 日期
	private String orderId;// 入库单号
	private String type;// 任务类型
	private int planNum;// 计划数量
	private String pcNum;// 微机编号
	private String building;// 楼栋
	private String carNum;// 车牌号
	private String stateNC;// 反馈状态
	private String provider;// 加工商

	private List<ReceiptDetailBean> detailBeanList;// 任务明细

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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getPlanNum() {
		return planNum;
	}

	public void setPlanNum(int planNum) {
		this.planNum = planNum;
	}

	public String getPCNum() {
		return pcNum;
	}

	public void setPCNum(String PCNum) {
		this.pcNum = PCNum;
	}

	public String getBuilding() {
		return building;
	}

	public void setBuilding(String building) {
		this.building = building;
	}

	public String getCarNum() {
		return carNum;
	}

	public void setCarNum(String carNum) {
		this.carNum = carNum;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String companyName) {
		this.provider = companyName;
	}

	public String getStateNC() {
		return stateNC;
	}

	public void setStateNC(String state) {
		this.stateNC = state;
	}

	public List<ReceiptDetailBean> getDetailBeanList() {
		return detailBeanList;
	}

	public void setDetailBeanList(List<ReceiptDetailBean> detailBeanList) {
		this.detailBeanList = detailBeanList;
	}

	// TODO

}
