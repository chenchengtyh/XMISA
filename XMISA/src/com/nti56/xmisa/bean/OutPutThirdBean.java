package com.nti56.xmisa.bean;

public class OutPutThirdBean {

	private String taskId;// 主表id
	private String detailId;// 明细id
	private String thirdId;// 子子表id
	private String carId;// 抱车编号
	private String doorId;// 门禁编号
	private String barCode;// 一维条码
	private String outTime;// 出库时间

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

	public void setThirdId(String thirdId) {
		this.thirdId = thirdId;
	}

	public String getThirdId() {
		return thirdId;
	}

	public void setCarId(String carId) {
		this.carId = carId;
	}

	public String getCarId() {
		return carId;
	}

	public void setDoorId(String doorId) {
		this.doorId = doorId;
	}

	public String getDoorId() {
		return doorId;
	}

	public void setBarCode(String barCode) {
		this.barCode = barCode;
	}

	public String getBarCode() {
		return barCode;
	}

	public void setOutTime(String outTime) {
		this.outTime = outTime;
	}

	public String getOutTime() {
		return outTime;
	}

}
