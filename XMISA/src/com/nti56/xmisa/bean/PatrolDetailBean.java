package com.nti56.xmisa.bean;

public class PatrolDetailBean {

	private String taskId;// 主任务id
	private String detailId;// 任务明细id
	private String floor;// 巡查楼层
	private String point;// 巡查项目
	private String result;// 巡查结果
	private String trouble;// 隐患
	private String picture;// 图片
	private String solution;// 整改措施
	private String repairDate;// 整改完成时间
	private String PictureVersion;// 图片版本
//	private String mMark = "";// 标记 临时/本地/服务器/待删除

//	private String note;// 备注

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

	public String getFloor() {
		return floor;
	}

	public void setFloor(String floor) {
		this.floor = floor;
	}

	public String getPoint() {
		return point;
	}

	public void setPoint(String project) {
		this.point = project;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getTrouble() {
		return trouble;
	}

	public void setTrouble(String trouble) {
		this.trouble = trouble;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public String getSolution() {
		return solution;
	}

	public void setSolution(String solution) {
		this.solution = solution;
	}

	public String getRepairDate() {
		return repairDate;
	}

	public void setRepairDate(String repairDate) {
		this.repairDate = repairDate;
	}

	public String getPictureVersion() {
		return PictureVersion;
	}

	public void setPictureVersion(String pictureVersion) {
		this.PictureVersion = pictureVersion;
	}

//	public void setMark(String Mark) {
//		if (mMark != null) {
//			this.mMark = Mark;
//		}
//	}
//
//	public String getMark() {
//		return mMark;
//	}

	// TODO
	// public String getNote() {
	// return note;
	// }
	//
	// public void setNote(String note) {
	// this.note = note;
	// }

}
