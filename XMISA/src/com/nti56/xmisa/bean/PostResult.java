package com.nti56.xmisa.bean;

import java.util.ArrayList;
import java.util.List;

public class PostResult {

	private boolean result;
	private String message;

	private List<ReceiptBean> receiptBeanList;
	private List<SurveyBean> surveyBeanList;
	private List<FumigateBean> fumigateBeanList;
	private List<PestsBean> pestsBeanList;
	private List<PatrolBean> patrolBeanList;
	private List<InPutBean> inPutBeanList;
	private List<OutPutBean> outPutBeanList;
	private List<AssignBean> assignBeanList;
	private List<ExamineBean> examineBeanList;
	private List<InventoryBean> inventoryBeanList;

//	private String User1;
//	private String Building1;
//	private List<Object> msglst1;
//	private List<InventoryBean> iventoryBeans;
//	private List<AssignBean> qaBeans;

	public PostResult() {
		result = false;
		message = "";
		receiptBeanList = new ArrayList<ReceiptBean>();
		surveyBeanList = new ArrayList<SurveyBean>();
		fumigateBeanList = new ArrayList<FumigateBean>();
		pestsBeanList = new ArrayList<PestsBean>();
		patrolBeanList = new ArrayList<PatrolBean>();
		inPutBeanList = new ArrayList<InPutBean>();

//		msglst = new ArrayList<Object>();
//		iventoryBeans = new ArrayList<InventoryBean>();
//		qaBeans = new ArrayList<AssignBean>();
	}

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<ReceiptBean> getReceiptBeanList() {
		return receiptBeanList;
	}

	public void setReceiptBeanList(List<ReceiptBean> receiptBeanList) {
		this.receiptBeanList = receiptBeanList;
	}

	public List<InPutBean> getInPutBeanList() {
		return inPutBeanList;
	}

	public void setInPutBeanList(List<InPutBean> inPutBeanList) {
		this.inPutBeanList = inPutBeanList;
	}

	public List<OutPutBean> getOutPutBeanList() {
		return outPutBeanList;
	}

	public void setOutPutBeanList(List<OutPutBean> outPutBeanList) {
		this.outPutBeanList = outPutBeanList;
	}

	public List<SurveyBean> getSurveyBeanList() {
		return surveyBeanList;
	}

	public void setSurveyBeanList(List<SurveyBean> surveyBeanList) {
		this.surveyBeanList = surveyBeanList;
	}

	public List<FumigateBean> getFumigateBeanList() {
		return fumigateBeanList;
	}

	public void setFumigateBeanList(List<FumigateBean> fumigateBeanList) {
		this.fumigateBeanList = fumigateBeanList;
	}

	public List<PestsBean> getPestsBeanList() {
		return pestsBeanList;
	}

	public void setPestsBeanList(List<PestsBean> pestsBeanList) {
		this.pestsBeanList = pestsBeanList;
	}

	public List<PatrolBean> getPatrolBeanList() {
		return patrolBeanList;
	}

	public void setPatrolBeanList(List<PatrolBean> patrolBeanList) {
		this.patrolBeanList = patrolBeanList;
	}

	public List<AssignBean> getAssignBeanList() {
		return assignBeanList;
	}

	public void setAssignBeanList(List<AssignBean> assignBeanList) {
		this.assignBeanList = assignBeanList;
	}

	public List<ExamineBean> getExamineBeanList() {
		return examineBeanList;
	}

	public void setExamineBeanList(List<ExamineBean> examineBeanList) {
		this.examineBeanList = examineBeanList;
	}

	public List<InventoryBean> getInventoryBeanList() {
		return inventoryBeanList;
	}

	public void setInventoryBeanList(List<InventoryBean> inventoryBeanList) {
		this.inventoryBeanList = inventoryBeanList;
	}

	// TODO
//	public String getUser() {
//		return User;
//	}
//
//	public void setUser(String user) {
//		this.User = user;
//	}
//
//	public String getBuilding() {
//		return Building;
//	}
//
//	public void setBuilding(String building) {
//		this.Building = building;
//	}
//
//	public List<Object> getMsglst() {
//		return msglst;
//	}
//
//	public void setMsglst(List<Object> msglst) {
//		this.msglst = msglst;
//	}
//
//	public List<InventoryBean> getIventoryBeans() {
//		return iventoryBeans;
//	}
//
//	public void setIventoryBeans(List<InventoryBean> iventoryBeans) {
//		this.iventoryBeans = iventoryBeans;
//	}
//
//	public List<AssignBean> getQaBeans() {
//		return qaBeans;
//	}
//
//	public void setQaBeans(List<AssignBean> qaBeans) {
//		this.qaBeans = qaBeans;
//	}
//
//	public List<OutBoundBean> getOutBoundBeans() {
//		return outBoundBeans;
//	}
//
//	public void setOutBoundBeans(List<OutBoundBean> outBoundBeans) {
//		this.outBoundBeans = outBoundBeans;
//	}

}
