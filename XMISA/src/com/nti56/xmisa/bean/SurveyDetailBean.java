package com.nti56.xmisa.bean;

import java.util.List;

public class SurveyDetailBean {

	private String taskId;// 主任务id
	private String detailId;// 任务明细id
	private String trayName;// 货位名称
	private String goodsID;// 货物编码
	private String goodsName;// 货物名称
	private String trayNum;// 货位总量
	private String checkNum;// 检测数量
	private String colligate;// 综合评定
	private String appearance;// 外观
	private String color;// 颜色
	private String waterFell;// 手感水分
	private String mould;// 霉变
	private String oilTrace;// 油印
	private String mothy;// 虫蛀
	private String odor;// 异味
	private String impurity;// 杂物

	private List<SurveyThirdBean> thirdBeanList;

	public List<SurveyThirdBean> getThirdBeanList() {
		return thirdBeanList;
	}

	public void setThirdBeanList(List<SurveyThirdBean> thirdBeanList) {
		this.thirdBeanList = thirdBeanList;
	}

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

	public String getTrayName() {
		return trayName;
	}

	public void setTrayName(String trayName) {
		this.trayName = trayName;
	}

	public String getGoodsID() {
		return goodsID;
	}

	public void setGoodsID(String goodsID) {
		this.goodsID = goodsID;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public String getColligate() {
		return colligate;
	}

	public void setColligate(String colligate) {
		this.colligate = colligate;
	}

	public String getTrayNum() {
		return trayNum;
	}

	public void setTrayNum(String trayNum) {
		this.trayNum = trayNum;
	}

	public String getCheckNum() {
		return checkNum;
	}

	public void setCheckNum(String checkNum) {
		this.checkNum = checkNum;
	}

	public String getAppearance() {
		return appearance;
	}

	public void setAppearance(String appearance) {
		this.appearance = appearance;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getWaterFell() {
		return waterFell;
	}

	public void setWaterFell(String waterFell) {
		this.waterFell = waterFell;
	}

	public String getMould() {
		return mould;
	}

	public void setMould(String mould) {
		this.mould = mould;
	}

	public String getOilTrace() {
		return oilTrace;
	}

	public void setOilTrace(String oilTrace) {
		this.oilTrace = oilTrace;
	}

	public String getMothy() {
		return mothy;
	}

	public void setMothy(String mothy) {
		this.mothy = mothy;
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

}
