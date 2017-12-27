package com.nti56.xmisa.bean;

import java.util.List;

public class ReceiptDetailBean {

	private String taskId;// 主表任务号
	private String detailId;// 任务明细id
	private String goodsID;// 货物编码
	private String goodsName;// 货物名称
	private double spec;// 规格

	private int planNum;// 计划件数
	private int factNum;// 完成件数
	private Double planWeight;// 计划重量
	private Double factWeight;// 完成重量

	private String ban;// 收货楼栋
	private Double weightLoss;// 途损重量
	private Double weightLossDamp;// 湿损重量
	private String note;// 备注
	private double lossWeight;
	private List<LossRecordBean> lossBeanList;// 途损明细表

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setDetailId(String detailId) {
		this.detailId = detailId;
	}

	public String getDetailId() {
		return detailId;
	}

	public void setGoodsID(String goodsID) {
		this.goodsID = goodsID;
	}

	public String getGoodsID() {
		return goodsID;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setSpec(double spec) {
		this.spec = spec;
	}

	public double getSpec() {
		return spec;
	}

	public void setPlanNum(int planNum) {
		this.planNum = planNum;

	}

	public int getPlanNum() {
		return planNum;
	}

	public void setFactNum(int factNum) {
		this.factNum = factNum;

	}

	public int getFactNum() {
		return factNum;
	}

	public void setPlanWeight(Double planWeight) {
		this.planWeight = planWeight;
	}

	public Double getPlanWeight() {
		return planWeight;
	}

	public void setFactWeight(Double factWeight) {
		this.factWeight = factWeight;
	}

	public Double getFactWeight() {
		return factWeight;
	}

	public String getBan() {
		return ban;
	}

	public void setBan(String ban) {
		this.ban = ban;
	}

	public Double getWeightLoss() {
		return weightLoss;
	}

	public void setWeightLoss(Double weightLoss) {
		this.weightLoss = weightLoss;
	}

	public Double getWeightLossDamp() {
		return weightLossDamp;
	}

	public void setWeightLossDamp(Double weightLossDamp) {
		this.weightLossDamp = weightLossDamp;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public List<LossRecordBean> getThirdBeanList() {
		return lossBeanList;
	}

	public void setThirdBeanList(List<LossRecordBean> lossBeanList) {
		this.lossBeanList = lossBeanList;
	}

	public void setLossWeight(double totalLoss) {
		this.lossWeight = totalLoss;
	}

	public double getLossWeight() {
		return lossWeight;
	}

}
