package com.nti56.xmisa.bean;

import java.util.List;

public class ExamineBean {

	private String taskId;// 任务号
	private String date;// 单据日期
	private String orderId;// 单号/任务号
	private String type;// 业务类型、途损类型
	private String goodsId;// 货物编码
	private String goodsName;// 货物名称
	private String building;// 楼栋
	private String carNum;// 车牌号
	private String checkLine;// 质检线
	private String NCState;// 反馈状态
	private String totalNum;// 入库总重量
	private String checkNum;// 抽检数量

	private List<ExamineDetailBean> detailBeanList;// 明细表

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

	public void setOrderId(String orderID) {
		this.orderId = orderID;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(String goodsID) {
		this.goodsId = goodsID;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
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

	public String getCheckLine() {
		return checkLine;
	}

	public void setCheckLine(String checkLine) {
		this.checkLine = checkLine;
	}

	public String getNCState() {
		return NCState;
	}

	public void setNCState(String ncState) {
		this.NCState = ncState;
	}

	public String getTotalNum() {
		return totalNum;
	}

	public void setTotalNum(String totalNum) {
		this.totalNum = totalNum;
	}

	public String getCheckNum() {
		return checkNum;
	}

	public void setCheckNum(String checkNum) {
		this.checkNum = checkNum;
	}

	public List<ExamineDetailBean> getDetailBeanList() {
		return detailBeanList;
	}

	public void setDetailBeanList(List<ExamineDetailBean> detailBeanList) {
		this.detailBeanList = detailBeanList;
	}

	// TODO
	// public String getCheckState() {
	// return checkState;
	// }
	//
	// public void setCheckState(String checkState) {
	// this.checkState = checkState;
	// }

}
