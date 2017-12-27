package com.nti56.xmisa.bean;

public class ExamineDetailBean {

	private String taskId;// 主键ID
	private String detailId;// 明细ID
	private String barCode;// 复烤一维条码
	private String rfidCode;// RFID条码
	private String factWeight;// 净重
	private String weight;// 重量判定
	private String factWater;// 水分
	private String water;// 水分判定
	private String appearance;// 外观
	private String mould;// 霉变
	private String odor;// 异味
	private String impurity;// 杂物
	private String mothy;// 虫蛀
	private String waterDev;// 水分偏差
	private String densityDev;// 密度偏差
	private String colligate;// 综合评定
	private String date;// 检测日期

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

	public String getBarCode() {
		return barCode;
	}

	public void setBarCode(String barCode) {
		this.barCode = barCode;
	}

	public String getRFIDCode() {
		return rfidCode;
	}

	public void setRFIDCode(String rfidCode) {
		this.rfidCode = rfidCode;
	}

	public String getFactWeight() {
		return factWeight;
	}

	public void setFactWeight(String factWeight) {
		this.factWeight = factWeight;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public String getFactWater() {
		return factWater;
	}

	public void setFactWater(String factWater) {
		this.factWater = factWater;
	}

	public String getWater() {
		return water;
	}

	public void setWater(String water) {
		this.water = water;
	}

	public String getAppearance() {
		return appearance;
	}

	public void setAppearance(String appearance) {
		this.appearance = appearance;
	}

	public String getMould() {
		return mould;
	}

	public void setMould(String mould) {
		this.mould = mould;
	}

	public String getOdor() {
		return odor;
	}

	public void setOdor(String odor) {
		this.odor = odor;
	}

	public String getImpurity() {
		return impurity;
	}

	public void setImpurity(String impurity) {
		this.impurity = impurity;
	}

	public String getMothy() {
		return mothy;
	}

	public void setMothy(String mothy) {
		this.mothy = mothy;
	}

	public String getWaterDev() {
		return waterDev;
	}

	public void setWaterDev(String waterDev) {
		this.waterDev = waterDev;
	}

	public String getDensityDev() {
		return densityDev;
	}

	public void setDensityDev(String densityDev) {
		this.densityDev = densityDev;
	}

	public String getColligate() {
		return colligate;
	}

	public void setColligate(String colligate) {
		this.colligate = colligate;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

}
