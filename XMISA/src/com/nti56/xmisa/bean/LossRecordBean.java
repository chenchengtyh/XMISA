package com.nti56.xmisa.bean;

public class LossRecordBean {

	private double mRate;// 换算率
	private double mSpec;// 规格
	private double mLossWeight;
	private String mTasklID = "";// 收货主表ID
	private String mDetailID = "";// 收货子表ID
	private String mBarCode = "";// 一维条码
	private String mLossType = "";// 途损类型
	private String mLossTaskID = "";
	private String mMark = "";// 标记 临时/本地/服务器/待删除

	public void setTaskId(String taskId) {
		if (taskId != null) {
			this.mTasklID = taskId;
		}
	}

	public String getTaskId() {
		return mTasklID;
	}

	public void setDetailId(String detailId) {
		if (detailId != null) {
			this.mDetailID = detailId;
		}
	}

	public String getDetailId() {
		return mDetailID;
	}

	public void setLossTaskID(String LossTaskID) {
		if (LossTaskID != null) {
			this.mLossTaskID = LossTaskID;
		}
	}

	public String getLossTaskID() {
		return mLossTaskID;
	}

	public void setLossWeight(double LossWeight) {
		this.mLossWeight = LossWeight;
	}

	public double getLossWeight() {
		return mLossWeight;
	}

	public void setSpec(double Spec) {
		this.mSpec = Spec;
	}

	public double getSpec() {
		return mSpec;
	}

	public void setRate(double Rate) {
		this.mRate = Rate;
	}

	public double getRate() {
		return mRate;
	}

	public void setLossType(String LossType) {
		if (LossType != null) {
			this.mLossType = LossType;
		}
	}

	public String getLossType() {
		return mLossType;
	}

	public void setBarCode(String BarCode) {
		if (BarCode != null) {
			this.mBarCode = BarCode;
		}
	}

	public String getBarCode() {
		return mBarCode;
	}

	public void setMark(String Mark) {
		if (mMark != null) {
			this.mMark = Mark;
		}
	}

	public String getMark() {
		return mMark;
	}

}
