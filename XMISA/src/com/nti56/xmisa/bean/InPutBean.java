package com.nti56.xmisa.bean;

import java.util.List;

public class InPutBean {

	private String taskId;// 任务号
	private String date;// 日期
	private String orderId;// 入库单号
	private String type;// 任务类型
	private int planNum;// 计划数量
	private String pcNum;// 微机编号
	private String building;// 楼栋
	private String carNum;// 车牌号
	private String stateNC;// 反馈状态
	private String conveyId;// 调运单号

	// private String isCancel;// 撤销状态
	// private String name;// 货物名称
	// public String spec;// 规格
	// private Double weightAble;// 应收重量
	// private Double numberFact;// 实收件数
	// private Double weightFact1;// 实收重量

	private List<InPutDetailBean> detailBeanList;// 明细表

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

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getPlanNum() {
		return planNum;
	}

	public void setPlanNum(int planNum) {
		this.planNum = planNum;
	}

	public String getPCNum() {
		return pcNum;
	}

	public void setPCNum(String pcNum) {
		this.pcNum = pcNum;
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

	public String getStateNC() {
		return stateNC;
	}

	public void setStateNC(String stateNC) {
		this.stateNC = stateNC;
	}

	public String getConveyId() {
		return conveyId;
	}

	public void setConveyId(String conveyId) {
		this.conveyId = conveyId;
	}

	public List<InPutDetailBean> getDetailBeanList() {
		return detailBeanList;
	}

	public void setDetailBeanList(List<InPutDetailBean> DetailBeanList) {
		this.detailBeanList = DetailBeanList;
	}

	// public String getIsCancel() {
	// return isCancel;
	// }
	//
	// public void setIsCancel(String isCancel) {
	// this.isCancel = isCancel;
	// }
	//
	// public String getName() {
	// return name;
	// }
	//
	// public void setName(String name) {
	// this.name = name;
	// }
	//
	// public String getSpec() {
	// return spec;
	// }
	//
	// public void setSpec(String spec) {
	// this.spec = spec;
	// }
	//
	// public Double getWeightAble() {
	// return weightAble;
	// }
	//
	// public void setWeightAble(Double weightAble) {
	// this.weightAble = weightAble;
	// }
	//
	// public Double getNumberFact() {
	// return numberFact;
	// }
	//
	// public void setNumberFact(Double numberFact) {
	// this.numberFact = numberFact;
	// }

}
