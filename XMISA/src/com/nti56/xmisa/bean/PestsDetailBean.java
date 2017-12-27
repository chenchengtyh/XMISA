package com.nti56.xmisa.bean;

public class PestsDetailBean {

	private String taskId;// 主任务id
	private String detailId;// 任务明细id
	private String CardNum;// 虫板编号
	private String SerricorneNum;// 甲虫数
	private String ElutellaNum;// 粉螟数
	private String Floor;// 楼层
	private String Picture;// 图片
	private String PictureVersion;// 图片版本
	private String Solution;// 整改措施
	private String note;// 备注
//	private String mMark = "";// 标记 临时/本地/服务器/待删除

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
		return Floor;
	}

	public void setFloor(String floor) {
		this.Floor = floor;
	}

	public String getCardNum() {
		return CardNum;
	}

	public void setCardNum(String cardNum) {
		this.CardNum = cardNum;
	}

	public String getSerricorneNum() {
		return SerricorneNum;
	}

	public void setSerricorneNum(String serricorneNum) {
		this.SerricorneNum = serricorneNum;
	}

	public String getElutellaNum() {
		return ElutellaNum;
	}

	public void setElutellaNum(String elutellaNum) {
		this.ElutellaNum = elutellaNum;
	}

	public String getPicture() {
		return Picture;
	}

	public void setPicture(String picture) {
		this.Picture = picture;
	}

	public String getPictureVersion() {
		return PictureVersion;
	}

	public void setPictureVersion(String pictureVersion) {
		this.PictureVersion = pictureVersion;
	}

	public String getSolution() {
		return Solution;
	}

	public void setSolution(String solution) {
		this.Solution = solution;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
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

}
