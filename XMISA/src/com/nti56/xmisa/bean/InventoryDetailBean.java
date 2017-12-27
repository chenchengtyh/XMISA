package com.nti56.xmisa.bean;

public class InventoryDetailBean {

	private String TaskId;// 主任务id
	private String DetailId;// 任务明细id
	private String TrayID;// 货位编码
	private String GoodsID;// 货物编码
//	private String GoodsName;// 货物名称
	private String StockNum;// 库存数量
	private String StockWeight;// 库存重量
	private String IvtNum;// 盘点数量
	private String IvtWeight;// 盘点重量
	private String Rate;// 换算率
	private String Date;// 盘点时间
	private String Mark;// 标记 本地/服务器/待删除

	// private String Floor;// 楼层
	// private String Picture;// 图片
	// private String PictureVersion;// 图片版本
	// private String note;// 备注
	// private String mMark = "";// 标记 临时/本地/服务器/待删除

	public String getTaskId() {
		return TaskId;
	}

	public void setTaskId(String taskId) {
		this.TaskId = taskId;
	}

	public String getDetailId() {
		return DetailId;
	}

	public void setDetailId(String detailId) {
		this.DetailId = detailId;
	}

	public String getTrayID() {
		return TrayID;
	}

	public void setTrayID(String trayID) {
		this.TrayID = trayID;
	}

	public String getGoodsID() {
		return GoodsID;
	}

	public void setGoodsID(String goodsId) {
		this.GoodsID = goodsId;
	}

//	public String getGoodsName() {
//		return GoodsName;
//	}

//	public void setGoodsName(String goodsName) {
//		this.GoodsName = goodsName;
//	}

	public String getStockNum() {
		return StockNum;
	}

	public void setStockNum(String stockNum) {
		this.StockNum = stockNum;
	}

	public String getStockWeight() {
		return StockWeight;
	}

	public void setStockWeight(String stockWeight) {
		this.StockWeight = stockWeight;
	}

	public String getIvtNum() {
		return IvtNum;
	}

	public void setIvtNum(String ivtNum) {
		this.IvtNum = ivtNum;
	}

	public String getIvtWeight() {
		return IvtWeight;
	}

	public void setIvtWeight(String ivtWeight) {
		this.IvtWeight = ivtWeight;
	}

	public String getRate() {
		return Rate;
	}

	public void setRate(String rate) {
		this.Rate = rate;
	}

	public String getDate() {
		return Date;
	}

	public void setDate(String date) {
		this.Date = date;
	}

	public void setMark(String mark) {
		this.Mark = mark;
	}

	public String getMark() {
		return Mark;
	}

	// public String getFloor() {
	// return Floor;
	// }
	//
	// public void setFloor(String floor) {
	// this.Floor = floor;
	// }
	//
	// public String getPicture() {
	// return Picture;
	// }
	//
	// public void setPicture(String picture) {
	// this.Picture = picture;
	// }
	//
	// public String getPictureVersion() {
	// return PictureVersion;
	// }
	//
	// public void setPictureVersion(String pictureVersion) {
	// this.PictureVersion = pictureVersion;
	// }
	//
	//
	// public String getNote() {
	// return note;
	// }
	//
	// public void setNote(String note) {
	// this.note = note;
	// }
	//
	// public void setMark(String Mark) {
	// if (mMark != null) {
	// this.mMark = Mark;
	// }
	// }
	//
	// public String getMark() {
	// return mMark;
	// }

}
