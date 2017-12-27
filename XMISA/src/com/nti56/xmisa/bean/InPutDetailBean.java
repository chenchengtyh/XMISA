package com.nti56.xmisa.bean;

import java.util.List;

public class InPutDetailBean {

	private String taskId;// 主表id
	private String detailId;// 明细id
	private String trayName;// 货位名称
	private String goodsID;// 货物编码
	private String goodsName;// 货物名称
	private double spec;// 规格

	private int planNum;// 计划件数
	private int factNum;// 完成件数
	private double planWeight;// 计划重量
	private double factWeight;// 完成重量

	// private String ban;//收货楼栋
	// private Double weightLoss;//途损重量
	// private Double weightLossDamp;//湿损重量
	// private String note;//备注
	// private ArrayList<LossRecordBean> lossBeans;
	// private double lossWeight;

	private List<InPutThirdBean> thirdBeanList;

	public List<InPutThirdBean> getThirdBeanList() {
		return thirdBeanList;
	}

	public void setThirdBeanList(List<InPutThirdBean> thirdBeanList) {
		this.thirdBeanList = thirdBeanList;
	}

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

	public String getTrayName() {
		return trayName;
	}

	public void setTrayName(String trayName) {
		this.trayName = trayName;
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

}
