package com.nti56.xmisa.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.nti56.xmisa.bean.AssignBean;
import com.nti56.xmisa.bean.ExamineBean;
import com.nti56.xmisa.bean.ExamineDetailBean;
import com.nti56.xmisa.bean.InPutBean;
import com.nti56.xmisa.bean.InPutDetailBean;
import com.nti56.xmisa.bean.InPutThirdBean;
import com.nti56.xmisa.bean.InventoryBean;
import com.nti56.xmisa.bean.InventoryDetailBean;
import com.nti56.xmisa.bean.OutPutBean;
import com.nti56.xmisa.bean.OutPutDetailBean;
import com.nti56.xmisa.bean.OutPutThirdBean;
import com.nti56.xmisa.bean.SurveyThirdBean;
import com.nti56.xmisa.bean.PatrolBean;
import com.nti56.xmisa.bean.PatrolDetailBean;
import com.nti56.xmisa.bean.PestsBean;
import com.nti56.xmisa.bean.PestsDetailBean;
import com.nti56.xmisa.bean.SurveyBean;
import com.nti56.xmisa.bean.SurveyDetailBean;
import com.nti56.xmisa.bean.FumigateBean;
import com.nti56.xmisa.bean.LossRecordBean;
import com.nti56.xmisa.bean.ReceiptBean;
import com.nti56.xmisa.bean.ReceiptDetailBean;
import com.nti56.xmisa.bean.PostResult;
import com.nti56.xmisa.bean.FumigateDetailBean;

public class HttpUtil {

	private static HttpUtil httputil = null;
	private String TAG = "HttpUtil";

	public static HttpUtil getInstance() {
		if (httputil == null) {
			synchronized (HttpUtil.class) {
				if (httputil == null) {
					httputil = new HttpUtil();
				}
			}
		}
		return httputil;
	}

	public void updateURL() {
		httputil = new HttpUtil();
	}

	// private static boolean TEST = true;
	public PostResult sendXML(String xmlString) {
		MyLog.e(TAG, "sendXML", "xmlString = " + xmlString);
		PostResult result = new PostResult();// 返回值
		// if (TEST) {
		// String function2 = "";
		// try {
		// Element root_xml2 =
		// DocumentHelper.parseText(xmlString).getRootElement();
		// function2 = root_xml2.element("Function").getText();
		// MyLog.e(TAG, "sendXML", "function2 = " + function2);
		// } catch (DocumentException e) {
		// e.printStackTrace();
		// }
		// return returnMyXml(result, function2);
		// }
		HttpResponse response = null;
		HttpParams httpParameters = new BasicHttpParams();
		// HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
		// HttpConnectionParams.setSoTimeout(httpParameters, 5000);
		HttpClient client = new DefaultHttpClient(httpParameters);
		xmlString = "<?xml version='1.0' encoding='UTF-8'?>" + xmlString;
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("xmldata", xmlString));
		HttpPost post = new HttpPost(Content.getServerUrl());

		MyLog.e(TAG, "sendXML", "ServerUrl = " + Content.getServerUrl());
		post.addHeader("Content-Type", "application/x-www-form-urlencoded");
		try {
			post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			response = client.execute(post);
		} catch (Exception e) {
			MyLog.e(TAG, "sendXML", "服务器繁忙，请稍候再试！");
			result.setResult(false);
			result.setMessage("服务器繁忙，请稍候再试！");
			return result;
		}
		Document doc = null;
		Element root_xml;
		String function;
		// 解析数据
		try {
			StringBuffer result_buffer = new StringBuffer(EntityUtils.toString(response.getEntity(), HTTP.UTF_8));
			String result_xml = DocumentHelper.parseText(result_buffer.toString()).getRootElement().getText();
			result_xml.replace("&lt;", "<");
			result_xml.replace("&gt;", ">");
			MyLog.e(TAG, "sendXML", "result_xml = " + result_xml);
			doc = DocumentHelper.parseText(result_xml);
			root_xml = doc.getRootElement();
			function = root_xml.element("Function").getText();
		} catch (Exception e) {
			MyLog.e(TAG, "sendXML", "解析数据出错！");
			result.setResult(false);
			result.setMessage("解析数据出错！");
			return result;
		}
		if (function.equals("login")) {// 登录
			result = Login(result, root_xml);

		} else if (function.equals("receiptTask")) {// 收货
			result = Receipt(result, root_xml);
		} else if (function.equals("receiptBackNC")) {// 收货反馈
			result = ReceiptBack(result, root_xml);

		} else if (function.equals("storageTask")) {// 入库
			result = InPut(result, root_xml);
		} else if (function.equals("storageBackNC")) {// 入库反馈
			result = InPutBack(result, root_xml);

		} else if (function.equals("outboundTask")) {// 出库
			result = OutPut(result, root_xml);
		} else if (function.equals("outboundTaskNC")) {// 出库反馈
			result = OutPutBack(result, root_xml);

		} else if (function.equals("inventoryTask")) {// 盘点
			result = Inventory(result, root_xml);
		} else if (function.equals("inventoryBackNC")) {// 盘点反馈
			result = InventoryBack(result, root_xml);

		} else if (function.equals("CheckAllocate")) {// 质检分配
			result = Assign(result, root_xml);
		} else if (function.equals("CheckAllocateNC")) {// 质检分配确认
			result = AssignBack(result, root_xml);

		} else if (function.equals("CheckComfirm")) {// 质检确认
			result = Examine(result, root_xml);
		} else if (function.equals("CheckComfirmNC")) {// 质检确认反馈
			result = ExamineBack(result, root_xml);

		} else if (function.equals("gcheck")) {// 普查
			result = Survey(result, root_xml);
		} else if (function.equals("gcheckNC")) {// 普查反馈
			result = SurveyBack(result, root_xml);

		} else if (function.equals("fumigate")) {// 熏蒸
			result = Fumigate(result, root_xml);
		} else if (function.equals("fumigateNC")) {// 熏蒸反馈
			result = FumigateBack(result, root_xml);

		} else if (function.equals("cqjcTask")) {// 虫情
			result = Pests(result, root_xml);
		} else if (function.equals("cqjcBack")) {// 虫情反馈
			result = PestsBack(result, root_xml);

		} else if (function.equals("ckxc")) {// 巡查
			result = Patrol(result, root_xml);
		} else if (function.equals("ckxcBack")) {// 巡查反馈
			result = PatrolBack(result, root_xml);

		} else if (function.equals("test")) {

		} else if (function.equals("test")) {

		} else {
			MyLog.e(TAG, "sendXML", "服务器返回错误！");
			result.setResult(false);
			result.setMessage("服务器返回错误！");
		}
		return result;

	}

	private PostResult Login(PostResult result, Element root_xml) {
		String Value = root_xml.element("Parms").element("Result").getText();
		int count = 0;
		count = Content.parseInt(Value);
		if (count != 0) {
			result.setResult(true);
			String userName = root_xml.element("Parms").element("Username").getText();
			userName = (userName != null ? userName : "");

			String sysCode = root_xml.element("Parms").element("Sysum").getText();
			sysCode = (sysCode != null ? sysCode : "");

			String building = root_xml.element("Parms").element("Floor").getText();
			building = (building != null ? building : "");

			Content.USER_NAME = userName;
			Content.USER_SYS_CODE = sysCode;
			Content.BUILDINGS = building;
			// result.setUser(usernameString);
		} else {
			result.setResult(false);
			result.setMessage("账号或密码错误！");
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private PostResult Receipt(PostResult result, Element root_xml) {
		List<ReceiptBean> beanlist = new ArrayList<ReceiptBean>();
		List<Element> taskElement = root_xml.element("Parms").elements("Task");
		if (taskElement != null && taskElement.size() > 0) {// 收货总表
			for (int i = 0; i < taskElement.size(); i++) {
				ReceiptBean bean = new ReceiptBean();
				bean.setTaskId(taskElement.get(i).attributeValue("taskId"));
				if (bean.getTaskId() == null || bean.getTaskId().equals("")) {
					continue;
				}
				bean.setDate(taskElement.get(i).attributeValue("date"));
				bean.setOrderId(taskElement.get(i).attributeValue("TaskNumber"));
				bean.setType(taskElement.get(i).attributeValue("typename"));
				bean.setPCNum(taskElement.get(i).attributeValue("computerCode"));
				bean.setPlanNum(Content.parseInt(taskElement.get(i).attributeValue("total")));
				bean.setBuilding(Content.getBuilding(taskElement.get(i).attributeValue("ban")));
				bean.setStateNC(taskElement.get(i).attributeValue("IsUpLoad"));
				bean.setCarNum(taskElement.get(i).attributeValue("carNum"));
				bean.setProvider(taskElement.get(i).attributeValue("companyName"));
				List<Element> detaiElement = taskElement.get(i).elements("TaskDetail");
				if (detaiElement != null && detaiElement.size() > 0) {// 收货子表
					List<ReceiptDetailBean> detailBeanList = new ArrayList<ReceiptDetailBean>();
					for (int j = 0; j < detaiElement.size(); j++) {
						ReceiptDetailBean detailBean = new ReceiptDetailBean();
						detailBean.setTaskId(bean.getTaskId());
						detailBean.setDetailId(detaiElement.get(j).attributeValue("detailId"));
						if (detailBean.getDetailId() == null || detailBean.getDetailId().equals("")) {
							continue;
						}
						detailBean.setGoodsID(detaiElement.get(j).attributeValue("itemcode"));
						detailBean.setGoodsName(detaiElement.get(j).attributeValue("goodsName"));
						detailBean.setSpec(Content.parseDouble(detaiElement.get(j).attributeValue("spec")));
						detailBean.setPlanNum(Content.parseInt(detaiElement.get(j).attributeValue("receivable")));
						detailBean.setFactNum(Content.parseInt(detaiElement.get(j).attributeValue("numberFact")));
						detailBean.setPlanWeight(Content.parseDouble(detaiElement.get(j).attributeValue("weight")));
						double totalLoss = 0;
						List<Element> thirdElement = detaiElement.get(j).elements("LossRecord");
						if (thirdElement != null && thirdElement.size() > 0) {// 途损明细表
							List<LossRecordBean> thirdBeanList = new ArrayList<LossRecordBean>();
							for (int k = 0; k < thirdElement.size(); k++) {
								LossRecordBean thirdBean = new LossRecordBean();
								thirdBean.setTaskId(bean.getTaskId());
								thirdBean.setDetailId(detailBean.getDetailId());
								thirdBean.setLossTaskID(thirdElement.get(k).attributeValue("TaskLossId"));
								if (thirdBean.getLossTaskID() == null || thirdBean.getLossTaskID().equals("")) {
									continue;
								}
								totalLoss += Content.parseDouble(thirdElement.get(k).attributeValue("weightLoss"));
								thirdBean.setLossWeight(Content.parseDouble(thirdElement.get(k).attributeValue("weightLoss")));
								thirdBean.setSpec(detailBean.getSpec());
								thirdBean.setRate(detailBean.getSpec() - thirdBean.getLossWeight());
								thirdBean.setLossType(thirdElement.get(k).attributeValue("LossWeightType"));
								thirdBean.setBarCode(thirdElement.get(k).attributeValue("Code"));
								thirdBean.setMark(Content.DB_DATA_SERVER);
								thirdBeanList.add(thirdBean);
							}
							detailBean.setThirdBeanList(thirdBeanList);
						}
						detailBean.setLossWeight(totalLoss);
						double factweight = detailBean.getFactNum() * detailBean.getSpec() - detailBean.getLossWeight();
						detailBean.setFactWeight((factweight < 0) ? ((double) 0) : factweight);
						detailBeanList.add(detailBean);
					}
					bean.setDetailBeanList(detailBeanList);
				}
				beanlist.add(bean);
			}
		}
		result.setResult(true);
		result.setReceiptBeanList(beanlist);
		return result;
	}

	@SuppressWarnings("unchecked")
	private PostResult InPut(PostResult result, Element root_xml) {
		List<InPutBean> beanList = new ArrayList<InPutBean>();
		List<Element> taskElement = root_xml.element("Parms").elements("Task");
		if (taskElement != null && taskElement.size() > 0) {// 入库总表
			for (int i = 0; i < taskElement.size(); i++) {
				InPutBean bean = new InPutBean();
				bean.setTaskId(taskElement.get(i).attributeValue("taskId"));
				if (bean.getTaskId() == null || bean.getTaskId().equals("")) {
					continue;
				}
				bean.setDate(Content.getDate(taskElement.get(i).attributeValue("date")));
				bean.setOrderId(taskElement.get(i).attributeValue("TaskNumber"));
				bean.setType(taskElement.get(i).attributeValue("typename"));
				bean.setPlanNum(Content.parseInt(taskElement.get(i).attributeValue("ableNum")));
				bean.setPCNum(taskElement.get(i).attributeValue("ComputerCode"));
				bean.setBuilding(Content.getBuilding(taskElement.get(i).attributeValue("floorName")));
				bean.setCarNum(taskElement.get(i).attributeValue("carNum"));
				bean.setStateNC(taskElement.get(i).attributeValue("IsUpLoad"));
				bean.setConveyId(taskElement.get(i).attributeValue("TransportCode"));

				List<Element> detaiElement = taskElement.get(i).elements("TaskDetail");
				if (detaiElement != null && detaiElement.size() > 0) {// 入库子表
					List<InPutDetailBean> detailBeanList = new ArrayList<InPutDetailBean>();
					for (int j = 0; j < detaiElement.size(); j++) {
						InPutDetailBean detailBean = new InPutDetailBean();
						detailBean.setTaskId(bean.getTaskId());
						detailBean.setDetailId(detaiElement.get(j).attributeValue("detailId"));
						if (detailBean.getDetailId() == null || detailBean.getDetailId().equals("")) {
							continue;
						}
						detailBean.setTrayName(detaiElement.get(j).attributeValue("locationname"));
						detailBean.setGoodsID(detaiElement.get(j).attributeValue("itemcode"));
						detailBean.setGoodsName(detaiElement.get(j).attributeValue("itemname"));
						detailBean.setSpec(Content.parseDouble(detaiElement.get(j).attributeValue("spec")));
						detailBean.setPlanNum(Content.parseInt(detaiElement.get(j).attributeValue("ableNum")));
						detailBean.setFactNum(Content.parseInt(detaiElement.get(j).attributeValue("numberFact")));
						detailBean.setPlanWeight(Content.parseDouble(detaiElement.get(j).attributeValue("weightAble")));
						detailBean.setFactWeight(detailBean.getFactNum() * detailBean.getSpec());

						List<Element> thirdElement = detaiElement.get(j).elements("CarRecord");
						if (thirdElement != null && thirdElement.size() > 0) {// 入库子子表
							List<InPutThirdBean> thirdBeanList = new ArrayList<InPutThirdBean>();
							for (int k = 0; k < thirdElement.size(); k++) {
								InPutThirdBean thirdBean = new InPutThirdBean();
								thirdBean.setTaskId(detailBean.getTaskId());
								thirdBean.setDetailId(detailBean.getDetailId());
								thirdBean.setThirdId(thirdElement.get(k).attributeValue("recordId"));
								if (thirdBean.getThirdId() == null || thirdBean.getThirdId().equals("")) {
									continue;
								}
								thirdBean.setCarId(thirdElement.get(k).attributeValue("terminalId"));
								thirdBean.setDoorId(thirdElement.get(k).attributeValue("RfidMachine"));
								thirdBean.setBarCode(thirdElement.get(k).attributeValue("Code"));
								thirdBean.setInTime(thirdElement.get(k).attributeValue("startTime"));
								thirdBeanList.add(thirdBean);
							}
							detailBean.setThirdBeanList(thirdBeanList);
						}
						detailBeanList.add(detailBean);
					}
					bean.setDetailBeanList(detailBeanList);
				}
				beanList.add(bean);
			}
		}
		result.setResult(true);
		result.setInPutBeanList(beanList);
		return result;
	}

	@SuppressWarnings("unchecked")
	private PostResult OutPut(PostResult result, Element root_xml) {
		List<OutPutBean> beanList = new ArrayList<OutPutBean>();
		List<Element> taskElement = root_xml.element("Parms").elements("Task");
		if (taskElement != null && taskElement.size() > 0) {// 出库总表
			for (int i = 0; i < taskElement.size(); i++) {
				OutPutBean bean = new OutPutBean();
				bean.setTaskId(taskElement.get(i).attributeValue("taskId"));
				if (bean.getTaskId() == null || bean.getTaskId().equals("")) {
					continue;
				}
				bean.setDate(Content.getDate(taskElement.get(i).attributeValue("date")));
				bean.setOrderId(taskElement.get(i).attributeValue("TaskNumber"));
				bean.setType(taskElement.get(i).attributeValue("typename"));
				bean.setGoalWare(taskElement.get(i).attributeValue("celevatedwhsname"));
				bean.setPlanNum(Content.parseInt(taskElement.get(i).attributeValue("ableNum")));
				bean.setBuilding(Content.getBuilding(taskElement.get(i).attributeValue("floor")));
				bean.setCarNum(taskElement.get(i).attributeValue("carNum"));
				bean.setStateNC(taskElement.get(i).attributeValue("IsUpLoad"));

				List<Element> detaiElement = taskElement.get(i).elements("TaskDetail");
				if (detaiElement != null && detaiElement.size() > 0) {// 入库子表
					List<OutPutDetailBean> detailBeanList = new ArrayList<OutPutDetailBean>();
					for (int j = 0; j < detaiElement.size(); j++) {
						OutPutDetailBean detailBean = new OutPutDetailBean();
						detailBean.setTaskId(bean.getTaskId());
						detailBean.setDetailId(detaiElement.get(j).attributeValue("detailId"));
						if (detailBean.getDetailId() == null || detailBean.getDetailId().equals("")) {
							continue;
						}
						detailBean.setTrayName(detaiElement.get(j).attributeValue("locationname"));
						detailBean.setGoodsID(detaiElement.get(j).attributeValue("itemcode"));
						detailBean.setGoodsName(detaiElement.get(j).attributeValue("itemname"));
						detailBean.setVbomName(detaiElement.get(j).attributeValue("vbomname"));
						detailBean.setSpec(Content.parseDouble(detaiElement.get(j).attributeValue("spec")));
						detailBean.setPlanNum(Content.parseInt(detaiElement.get(j).attributeValue("ableNum")));
						detailBean.setFactNum(Content.parseInt(detaiElement.get(j).attributeValue("numberFact")));
						detailBean.setPlanWeight(Content.parseDouble(detaiElement.get(j).attributeValue("weightAble")));
						detailBean.setFactWeight(detailBean.getFactNum() * detailBean.getSpec());

						List<Element> thirdElement = detaiElement.get(j).elements("CarRecord");
						if (thirdElement != null && thirdElement.size() > 0) {// 入库子子表
							List<OutPutThirdBean> thirdBeanList = new ArrayList<OutPutThirdBean>();
							for (int k = 0; k < thirdElement.size(); k++) {
								OutPutThirdBean thirdBean = new OutPutThirdBean();
								thirdBean.setTaskId(detailBean.getTaskId());
								thirdBean.setDetailId(detailBean.getDetailId());
								thirdBean.setThirdId(thirdElement.get(k).attributeValue("recordId"));
								if (thirdBean.getThirdId() == null || thirdBean.getThirdId().equals("")) {
									continue;
								}
								thirdBean.setCarId(thirdElement.get(k).attributeValue("terminalId"));
								thirdBean.setDoorId(thirdElement.get(k).attributeValue("RfidMachine"));
								thirdBean.setBarCode(thirdElement.get(k).attributeValue("Code"));
								thirdBean.setOutTime(thirdElement.get(k).attributeValue("startTime"));
								thirdBeanList.add(thirdBean);
							}
							detailBean.setThirdBeanList(thirdBeanList);
						}
						detailBeanList.add(detailBean);
					}
					bean.setDetailBeanList(detailBeanList);
				}
				beanList.add(bean);
			}
		}
		result.setResult(true);
		result.setOutPutBeanList(beanList);
		return result;
	}

	@SuppressWarnings("unchecked")
	private PostResult Inventory(PostResult result, Element root_xml) {
		List<InventoryBean> beanList = new ArrayList<InventoryBean>();
		List<Element> taskElement = root_xml.element("Parms").elements("Task");
		if (taskElement != null && taskElement.size() > 0) {// 盘点总表
			for (int i = 0; i < taskElement.size(); i++) {
				InventoryBean bean = new InventoryBean();
				bean.setTaskId(taskElement.get(i).attributeValue("taskId"));
				if (bean.getTaskId() == null || bean.getTaskId().equals("")) {
					continue;
				}
				bean.setDate(Content.getDate(taskElement.get(i).attributeValue("createtime")));
				bean.setOrderId(taskElement.get(i).attributeValue("tasknumber"));
				bean.setType(taskElement.get(i).attributeValue("tasktype"));
				bean.setBuilding(Content.getBuilding(taskElement.get(i).attributeValue("ban")));
				bean.setTrayTotle(taskElement.get(i).attributeValue("ttlloc"));
				bean.setTrayDone(taskElement.get(i).attributeValue("TtlLoccounted"));
				bean.setIvtNum(taskElement.get(i).attributeValue("totalqty"));
				bean.setIvtDone(taskElement.get(i).attributeValue("finishNum"));
				bean.setIvtWait(taskElement.get(i).attributeValue("waitNum"));
				bean.setStateNC(taskElement.get(i).attributeValue("IsUpLoad"));

				List<Element> detaiElement = taskElement.get(i).elements("TaskDetail");
				if (detaiElement != null && detaiElement.size() > 0) {// 盘点子表
					List<InventoryDetailBean> detailBeanList = new ArrayList<InventoryDetailBean>();
					for (int j = 0; j < detaiElement.size(); j++) {
						InventoryDetailBean detailBean = new InventoryDetailBean();
						detailBean.setTaskId(bean.getTaskId());
						detailBean.setDetailId(detaiElement.get(j).attributeValue("detailId"));
						if (detailBean.getDetailId() == null || detailBean.getDetailId().equals("")) {
							continue;
						}
						detailBean.setTrayID(detaiElement.get(j).attributeValue("locationcode"));
						detailBean.setGoodsID(detaiElement.get(j).attributeValue("itemcode"));
						// detailBean.setGoodsName(detaiElement.get(j).attributeValue("itemname"));
						detailBean.setStockNum(detaiElement.get(j).attributeValue("qty"));
						detailBean.setStockWeight(detaiElement.get(j).attributeValue("weight"));
						detailBean.setIvtNum(detaiElement.get(j).attributeValue("qty2"));
						detailBean.setIvtWeight(detaiElement.get(j).attributeValue("weight2"));
						detailBean.setRate(detaiElement.get(j).attributeValue("rate"));
						detailBean.setDate(Content.getDate(detaiElement.get(j).attributeValue("CountDate")));
						detailBean.setMark(Content.DB_DATA_SERVER);
						detailBeanList.add(detailBean);
					}
					bean.setDetailBeanList(detailBeanList);
				}
				beanList.add(bean);
			}
		}
		result.setResult(true);
		result.setInventoryBeanList(beanList);
		return result;
	}

	@SuppressWarnings("unchecked")
	private PostResult Assign(PostResult result, Element root_xml) {
		List<AssignBean> beanList = new ArrayList<AssignBean>();
		List<Element> taskElement = root_xml.element("Parms").elements("Task");
		if (taskElement != null && taskElement.size() > 0) {// 分配总表
			for (int i = 0; i < taskElement.size(); i++) {
				AssignBean bean = new AssignBean();
				bean.setTaskId(taskElement.get(i).attributeValue("TaskId"));
				if (bean.getTaskId() == null || bean.getTaskId().equals("")) {
					continue;
				}
				bean.setChildId(taskElement.get(i).attributeValue("DetailID"));
				bean.setDate(Content.getDate(taskElement.get(i).attributeValue("Date")));
				bean.setOrderId(taskElement.get(i).attributeValue("TaskNumber"));
				bean.setType(taskElement.get(i).attributeValue("TypeName"));
				bean.setGoodsId(taskElement.get(i).attributeValue("ItemCode"));
				bean.setGoodsName(taskElement.get(i).attributeValue("ItemName"));
				bean.setBuilding(Content.getBuilding(taskElement.get(i).attributeValue("FloorName")));
				bean.setCarNum(taskElement.get(i).attributeValue("CarNum"));
				bean.setCheckLine(taskElement.get(i).attributeValue("Line"));
				bean.setAssignState(taskElement.get(i).attributeValue("Allocate"));
				bean.setTotalNum(taskElement.get(i).attributeValue("Qty"));
				bean.setCheckNum(taskElement.get(i).attributeValue("Samplingqty"));
				bean.setUpstreamdetailid(taskElement.get(i).attributeValue("Upstreamdetailid"));// 什么东东。。
				List<Element> detaiElement = taskElement.get(i).elements("TaskItem");
				if (detaiElement != null && detaiElement.size() > 0) {// 分配子表
					bean.setWeight(detaiElement.get(0).attributeValue("Weight"));
					bean.setWater(detaiElement.get(0).attributeValue("Water"));
					bean.setAppearance(detaiElement.get(0).attributeValue("Appearance"));
					bean.setMould(detaiElement.get(0).attributeValue("Mould"));
					bean.setOdor(detaiElement.get(0).attributeValue("Odor"));
					bean.setImpurity(detaiElement.get(0).attributeValue("Varia"));
					bean.setMothy(detaiElement.get(0).attributeValue("Moth"));
				}
				beanList.add(bean);
			}
		}
		result.setResult(true);
		result.setAssignBeanList(beanList);
		return result;
	}

	@SuppressWarnings("unchecked")
	private PostResult Examine(PostResult result, Element root_xml) {
		List<ExamineBean> beanList = new ArrayList<ExamineBean>();
		List<Element> taskElement = root_xml.element("Parms").elements("Task");
		if (taskElement != null && taskElement.size() > 0) {// 质检确认总表
			for (int i = 0; i < taskElement.size(); i++) {
				ExamineBean bean = new ExamineBean();
				bean.setTaskId(taskElement.get(i).attributeValue("TaskId"));
				if (bean.getTaskId() == null || bean.getTaskId().equals("")) {
					continue;
				}
				bean.setDate(Content.getDate(taskElement.get(i).attributeValue("Date")));
				bean.setOrderId(taskElement.get(i).attributeValue("TaskNumber"));
				bean.setType(taskElement.get(i).attributeValue("TypeName"));
				bean.setGoodsId(taskElement.get(i).attributeValue("ItemCode"));
				bean.setGoodsName(taskElement.get(i).attributeValue("ItemName"));
				bean.setBuilding(Content.getBuilding(taskElement.get(i).attributeValue("FloorName")));
				bean.setCarNum(taskElement.get(i).attributeValue("CarNum"));
				bean.setCheckLine(taskElement.get(i).attributeValue("Line"));
				bean.setNCState(taskElement.get(i).attributeValue("IsUpLoad"));
				bean.setTotalNum(taskElement.get(i).attributeValue("Qty"));
				bean.setCheckNum(taskElement.get(i).attributeValue("Samplingqty"));

				List<Element> detaiElement = taskElement.get(i).elements("TaskDetail");
				if (detaiElement != null && detaiElement.size() > 0) {// 质检确认子表
					List<ExamineDetailBean> detailBeanList = new ArrayList<ExamineDetailBean>();

					List<Element> thirdElement = detaiElement.get(0).elements("TaskChild");
					if (thirdElement != null && thirdElement.size() > 0) {// 质检确认子子表
						for (int j = 0; j < thirdElement.size(); j++) {
							ExamineDetailBean thirdBean = new ExamineDetailBean();
							thirdBean.setTaskId(bean.getTaskId());
							thirdBean.setDetailId(thirdElement.get(j).attributeValue("ChildId"));
							if (thirdBean.getDetailId() == null || thirdBean.getDetailId().equals("")) {
								continue;
							}
							thirdBean.setBarCode(thirdElement.get(j).attributeValue("FKcode"));
							thirdBean.setRFIDCode(thirdElement.get(j).attributeValue("code"));
							thirdBean.setFactWeight(thirdElement.get(j).attributeValue("Weight"));
							thirdBean.setWeight(thirdElement.get(j).attributeValue("nweight"));
							thirdBean.setFactWater(thirdElement.get(j).attributeValue("Water"));
							thirdBean.setWater(thirdElement.get(j).attributeValue("NWater"));
							thirdBean.setAppearance(thirdElement.get(j).attributeValue("Appearance"));
							thirdBean.setMould(thirdElement.get(j).attributeValue("Mould"));
							thirdBean.setOdor(thirdElement.get(j).attributeValue("Odor"));
							thirdBean.setImpurity(thirdElement.get(j).attributeValue("Varia"));
							thirdBean.setMothy(thirdElement.get(j).attributeValue("Moth"));
							thirdBean.setWaterDev(thirdElement.get(j).attributeValue("waterdev"));
							thirdBean.setDensityDev(thirdElement.get(j).attributeValue("Density"));
							thirdBean.setColligate(thirdElement.get(j).attributeValue("Colligate"));
							thirdBean.setDate(Content.getDate(thirdElement.get(j).attributeValue("CheckTime")));
							detailBeanList.add(thirdBean);
						}
					}
					ExamineDetailBean detailBean = new ExamineDetailBean();
					detailBean.setTaskId(bean.getTaskId());
					detailBean.setDetailId(detaiElement.get(0).attributeValue("DetailId"));
					if (detailBean.getDetailId() == null || detailBean.getDetailId().equals("")) {
						continue;
					}
					detailBean.setBarCode(Content.DETAIL_AVERAGE_VALUE);
					detailBean.setRFIDCode(Content.DETAIL_AVERAGE_VALUE);
					detailBean.setFactWeight(detaiElement.get(0).attributeValue("Weight"));
					detailBean.setWeight(detaiElement.get(0).attributeValue("NWeight"));
					detailBean.setFactWater(detaiElement.get(0).attributeValue("Water"));
					detailBean.setWater(detaiElement.get(0).attributeValue("NWater"));
					detailBean.setAppearance(detaiElement.get(0).attributeValue("Appearance"));
					detailBean.setMould(detaiElement.get(0).attributeValue("Mould"));
					detailBean.setOdor(detaiElement.get(0).attributeValue("Odor"));
					detailBean.setImpurity(detaiElement.get(0).attributeValue("varia"));
					detailBean.setMothy(detaiElement.get(0).attributeValue("Moth"));
					detailBean.setWaterDev(detaiElement.get(0).attributeValue("WaterDev"));
					detailBean.setDensityDev(detaiElement.get(0).attributeValue("Density"));
					detailBean.setColligate(detaiElement.get(0).attributeValue("Colligate"));
					detailBean.setDate(Content.getDate(detaiElement.get(0).attributeValue("CheckTime")));
					detailBeanList.add(detailBean);
					bean.setDetailBeanList(detailBeanList);
				}
				beanList.add(bean);
			}
		}
		result.setResult(true);
		result.setExamineBeanList(beanList);
		return result;
	}

	@SuppressWarnings("unchecked")
	private PostResult Survey(PostResult result, Element root_xml) {
		List<SurveyBean> beanList = new ArrayList<SurveyBean>();
		List<Element> taskElement = root_xml.element("Parms").elements("Task");
		if (taskElement != null && taskElement.size() > 0) {// 普查总表
			for (int i = 0; i < taskElement.size(); i++) {
				SurveyBean bean = new SurveyBean();
				bean.setTaskId(taskElement.get(i).attributeValue("TaskId"));
				if (bean.getTaskId() == null || bean.getTaskId().equals("")) {
					continue;
				}
				bean.setDate(Content.getDate(taskElement.get(i).attributeValue("Date")));
				bean.setOrderId(taskElement.get(i).attributeValue("TaskNumber"));
				bean.setBuilding(Content.getBuilding(taskElement.get(i).attributeValue("Ban")));
				bean.setFloor(taskElement.get(i).attributeValue("FloorLay"));
				bean.setPerson(taskElement.get(i).attributeValue("Dispatcher"));
				bean.setStateNC(taskElement.get(i).attributeValue("IsUpload"));

				List<Element> detaiElement = taskElement.get(i).elements("TaskDetail");
				if (detaiElement != null && detaiElement.size() > 0) {// 普查子表
					List<SurveyDetailBean> detailBeanList = new ArrayList<SurveyDetailBean>();
					for (int j = 0; j < detaiElement.size(); j++) {
						SurveyDetailBean detailBean = new SurveyDetailBean();
						detailBean.setTaskId(bean.getTaskId());
						detailBean.setDetailId(detaiElement.get(j).attributeValue("DetailId"));
						if (detailBean.getDetailId() == null || detailBean.getDetailId().equals("")) {
							continue;
						}
						detailBean.setTrayName(detaiElement.get(j).attributeValue("LocationName"));
						detailBean.setGoodsID(detaiElement.get(j).attributeValue("ItemCode"));
						detailBean.setGoodsName(detaiElement.get(j).attributeValue("ItemName"));
						detailBean.setTrayNum(detaiElement.get(j).attributeValue("Qty"));
						detailBean.setCheckNum(detaiElement.get(j).attributeValue("CheckNumber"));
						detailBean.setColligate(detaiElement.get(j).attributeValue("colligate"));
						detailBean.setAppearance(detaiElement.get(j).attributeValue("appearance"));
						detailBean.setColor(detaiElement.get(j).attributeValue("color"));
						detailBean.setWaterFell(detaiElement.get(j).attributeValue("wet"));
						detailBean.setMould(detaiElement.get(j).attributeValue("mould"));
						detailBean.setOilTrace(detaiElement.get(j).attributeValue("oil"));
						detailBean.setMothy(detaiElement.get(j).attributeValue("cq"));
						detailBean.setOdor(detaiElement.get(j).attributeValue("odor"));
						detailBean.setImpurity(detaiElement.get(j).attributeValue("varia"));

						List<Element> thirdElement = detaiElement.get(j).elements("Result");
						if (thirdElement != null && thirdElement.size() > 0) {// 子子表
							List<SurveyThirdBean> thirdBeanList = new ArrayList<SurveyThirdBean>();
							for (int k = 0; k < thirdElement.size(); k++) {
								SurveyThirdBean thirdBean = new SurveyThirdBean();
								thirdBean.setTaskId(bean.getTaskId());
								thirdBean.setDetailId(detailBean.getDetailId());
								thirdBean.setThirdId(thirdElement.get(k).attributeValue("ChildId"));
								if (thirdBean.getThirdId() == null || thirdBean.getThirdId().equals("")) {
									continue;
								}
								thirdBean.setBarCode(thirdElement.get(k).attributeValue("Code"));
								thirdBean.setColligate(thirdElement.get(k).attributeValue("colligate"));
								thirdBean.setAppearance(thirdElement.get(k).attributeValue("appearance"));
								thirdBean.setColor(thirdElement.get(k).attributeValue("color"));
								thirdBean.setWaterFell(thirdElement.get(k).attributeValue("wet"));
								thirdBean.setMould(thirdElement.get(k).attributeValue("mould"));
								thirdBean.setOilTrace(thirdElement.get(k).attributeValue("oil"));
								thirdBean.setMothy(thirdElement.get(k).attributeValue("cq"));
								thirdBean.setOdor(thirdElement.get(k).attributeValue("odor"));
								thirdBean.setImpurity(thirdElement.get(k).attributeValue("varia"));

								String[] imageNames = new String[4];
								imageNames[0] = getImageName(thirdElement.get(k).attributeValue("ImageUrl1"));
								imageNames[1] = getImageName(thirdElement.get(k).attributeValue("ImageUrl2"));
								imageNames[2] = getImageName(thirdElement.get(k).attributeValue("ImageUrl3"));
								imageNames[3] = getImageName(thirdElement.get(k).attributeValue("ImageUrl4"));

								String[] versions = getImageversions(imageNames);
								thirdBean.setPicture(imageNames[0] + "/" + imageNames[1] + "/" + imageNames[2] + "/" + imageNames[3]);
								thirdBean.setPictureVersion(versions[0] + "/" + versions[1] + "/" + versions[2] + "/" + versions[3]);
								thirdBean.setMark(Content.DB_DATA_SERVER);
								thirdBeanList.add(thirdBean);
							}
							detailBean.setThirdBeanList(thirdBeanList);
						}
						detailBeanList.add(detailBean);
					}
					bean.setDetailNum(detaiElement.size());
					bean.setDetailBeanList(detailBeanList);
				}
				beanList.add(bean);
			}
		}
		result.setResult(true);
		result.setSurveyBeanList(beanList);
		return result;
	}

	@SuppressWarnings("unchecked")
	private PostResult Fumigate(PostResult result, Element root_xml) {
		List<FumigateBean> beanlist = new ArrayList<FumigateBean>();
		List<Element> taskElement = root_xml.element("Parms").elements("Task");
		if (taskElement != null && taskElement.size() > 0) {// 熏蒸总表
			for (int i = 0; i < taskElement.size(); i++) {
				FumigateBean bean = new FumigateBean();
				bean.setTaskId(taskElement.get(i).attributeValue("taskid"));
				if (bean.getTaskId() == null || bean.getTaskId().equals("")) {
					continue;
				}
				bean.setDate(Content.getDate(taskElement.get(i).attributeValue("billdate")));// 单据日期
				bean.setOrderId(taskElement.get(i).attributeValue("tasknumber"));// 任务号
				bean.setBuilding(Content.getBuilding(taskElement.get(i).attributeValue("ban")));// 仓库楼栋
				bean.setRoom(taskElement.get(i).attributeValue("fumigation"));// 熏蒸室
				bean.setMethod(taskElement.get(i).attributeValue("methodcode"));// 熏蒸方法
				bean.setDosingDate(Content.getDate(taskElement.get(i).attributeValue("dosingdate")));// 放药日期
				bean.setPerson(taskElement.get(i).attributeValue("dispatcher"));// 记录人
				bean.setStateNC(taskElement.get(i).attributeValue("isupload"));// 反馈状态
				bean.setDrug(taskElement.get(i).attributeValue("agent"));// 熏蒸药物
				bean.setVolume(taskElement.get(i).attributeValue("volume"));// 熏蒸体积
				bean.setDosage(taskElement.get(i).attributeValue("dosage"));// 用药量
				bean.setUnit(taskElement.get(i).attributeValue("company"));// 用药单位
				bean.setNote(taskElement.get(i).attributeValue("note"));// 备注
				// bean.setDrug(Content.DEFAULT_DRUG);
				bean.setAirStart(Content.getDate(taskElement.get(i).attributeValue("divergencedate")));// 通风开始
				bean.setAirEnd(Content.getDate(taskElement.get(i).attributeValue("acceptancedate")));// 通风结束
				List<Element> detaiElement = taskElement.get(i).elements("TaskDetail");
				if (detaiElement != null && detaiElement.size() > 0) {// 熏蒸子表
					List<FumigateDetailBean> detailBeanList = new ArrayList<FumigateDetailBean>();
					for (int j = 0; j < detaiElement.size(); j++) {
						FumigateDetailBean detailBean = new FumigateDetailBean();
						detailBean.setTaskId(bean.getTaskId());
						detailBean.setDetailId(detaiElement.get(j).attributeValue("detailId"));
						if (detailBean.getDetailId() == null || detailBean.getDetailId().equals("")) {
							continue;
						}
						detailBean.setTrayID(detaiElement.get(j).attributeValue("locationcode"));
						detailBean.setTrayName(detaiElement.get(j).attributeValue("locationname"));// 货位&楼层&熏蒸室
						// detailBean.setFloor(detaiElement.get(j).attributeValue("floorlay"));//楼层
						detailBean.setLocation(detaiElement.get(j).attributeValue("position"));// 位置
						detailBean.setConcentration6(detaiElement.get(j).attributeValue("hsulubility6"));// 浓度6
						detailBean.setConcentration12(detaiElement.get(j).attributeValue("hsulubility12"));// 浓度12
						detailBean.setConcentration24(detaiElement.get(j).attributeValue("hsulubility24"));// 浓度24
						detailBean.setConcentration48(detaiElement.get(j).attributeValue("hsulubility48"));// 浓度48
						detailBean.setConcentration72(detaiElement.get(j).attributeValue("hsulubility72"));// 浓度72
						detailBean.setConcentration96(detaiElement.get(j).attributeValue("hsulubility96"));// 浓度96
						detailBean.setConcentration144(detaiElement.get(j).attributeValue("hsulubility144"));// 浓度144
						detailBean.setConcentration216(detaiElement.get(j).attributeValue("hsulubility216"));// 浓度216

						// detailBean.setDateFirst(detaiElement.get(j).attributeValue("datafirst"));
						// detailBean.setConcentrationFirst(detaiElement.get(j).attributeValue("ppmfirst"));
						// detailBean.setDateSecond(detaiElement.get(j).attributeValue("datasecond"));
						// detailBean.setConcentrationSecond(detaiElement.get(j).attributeValue("ppmsecond"));

						String[] imageNames = new String[4];
						imageNames[0] = getImageName(detaiElement.get(j).attributeValue("imageurl1"));
						imageNames[1] = getImageName(detaiElement.get(j).attributeValue("imageurl2"));
						imageNames[2] = getImageName(detaiElement.get(j).attributeValue("imageurl3"));
						imageNames[3] = getImageName(detaiElement.get(j).attributeValue("imageurl4"));

						String[] versions = getImageversions(imageNames);
						detailBean.setPicture(imageNames[0] + "/" + imageNames[1] + "/" + imageNames[2] + "/" + imageNames[3]);
						detailBean.setPictureVersion(versions[0] + "/" + versions[1] + "/" + versions[2] + "/" + versions[3]);
						detailBeanList.add(detailBean);
					}
					bean.setDetailBeanList(detailBeanList);
				}
				beanlist.add(bean);
			}
		}
		result.setResult(true);
		result.setFumigateBeanList(beanlist);
		return result;
	}

	@SuppressWarnings("unchecked")
	private PostResult Pests(PostResult result, Element root_xml) {
		List<PestsBean> beanlist = new ArrayList<PestsBean>();
		List<Element> taskElement = root_xml.element("Parms").elements("Task");
		if (taskElement != null && taskElement.size() > 0) {// 虫情总表
			for (int i = 0; i < taskElement.size(); i++) {
				PestsBean bean = new PestsBean();
				bean.setTaskId(taskElement.get(i).attributeValue("TaskId"));
				if (bean.getTaskId() == null || bean.getTaskId().equals("")) {
					continue;
				}
				bean.setDate(Content.getDate(taskElement.get(i).attributeValue("Date")));
				bean.setOrderId(taskElement.get(i).attributeValue("PestInfoCode"));
				bean.setBuilding(Content.getBuilding(taskElement.get(i).attributeValue("Ban")));
				bean.setPerson(taskElement.get(i).attributeValue("Dispatcher"));
				bean.setStateNC(taskElement.get(i).attributeValue("IsUpLoad"));
				bean.setNote(taskElement.get(i).attributeValue("Note"));
				bean.setMark(Content.DB_DATA_SERVER);
				List<Element> detaiElement = taskElement.get(i).elements("TaskDetail");
				if (detaiElement != null && detaiElement.size() > 0) {// 虫情子表
					List<PestsDetailBean> detailBeanList = new ArrayList<PestsDetailBean>();
					for (int j = 0; j < detaiElement.size(); j++) {
						PestsDetailBean detailBean = new PestsDetailBean();
						detailBean.setTaskId(bean.getTaskId());
						detailBean.setDetailId(detaiElement.get(j).attributeValue("DetailId"));
						if (detailBean.getDetailId() == null || detailBean.getDetailId().equals("")) {
							continue;
						}
						detailBean.setFloor(detaiElement.get(j).attributeValue("FloorLay"));
						detailBean.setCardNum(detaiElement.get(j).attributeValue("Code"));
						detailBean.setSerricorneNum(Content.filterNumber(detaiElement.get(j).attributeValue("Jc_num")));// 烟草甲
						detailBean.setElutellaNum(Content.filterNumber(detaiElement.get(j).attributeValue("Fm_num")));// 烟草粉螟

						String[] imageNames = new String[4];
						imageNames[0] = getImageName(detaiElement.get(j).attributeValue("ImageUrl1"));
						imageNames[1] = getImageName(detaiElement.get(j).attributeValue("ImageUrl2"));
						imageNames[2] = getImageName(detaiElement.get(j).attributeValue("ImageUrl3"));
						imageNames[3] = getImageName(detaiElement.get(j).attributeValue("ImageUrl4"));

						String[] versions = getImageversions(imageNames);
						detailBean.setPicture(imageNames[0] + "/" + imageNames[1] + "/" + imageNames[2] + "/" + imageNames[3]);
						detailBean.setPictureVersion(versions[0] + "/" + versions[1] + "/" + versions[2] + "/" + versions[3]);
						detailBean.setSolution(detaiElement.get(j).attributeValue("Measure"));
						// detailBean.setMark(Content.DB_DATA_SERVER);
						detailBeanList.add(detailBean);
					}
					bean.setDetailBeanList(detailBeanList);
				}
				beanlist.add(bean);
			}
		}
		result.setResult(true);
		result.setPestsBeanList(beanlist);
		return result;
	}

	@SuppressWarnings("unchecked")
	private PostResult Patrol(PostResult result, Element root_xml) {
		List<PatrolBean> beanlist = new ArrayList<PatrolBean>();
		List<Element> taskElement = root_xml.element("Parms").elements("Task");
		if (taskElement != null && taskElement.size() > 0) {// 巡查总表
			for (int i = 0; i < taskElement.size(); i++) {
				PatrolBean bean = new PatrolBean();
				bean.setTaskId(taskElement.get(i).attributeValue("TaskId"));
				if (bean.getTaskId() == null || bean.getTaskId().equals("")) {
					continue;
				}
				bean.setDate(Content.getDate(taskElement.get(i).attributeValue("Date")));
				bean.setMeridiem(taskElement.get(i).attributeValue("isMor"));
				bean.setOrderId(taskElement.get(i).attributeValue("PatrolinfoCode"));
				bean.setBuilding(Content.getBuilding(taskElement.get(i).attributeValue("Ban")));
				bean.setPerson(taskElement.get(i).attributeValue("Dispatcher"));
				bean.setStateNC(taskElement.get(i).attributeValue("IsUpLoad"));
				bean.setMark(Content.DB_DATA_SERVER);
				List<Element> detaiElement = taskElement.get(i).elements("TaskDetail");
				if (detaiElement != null && detaiElement.size() > 0) {// 巡查子表
					List<PatrolDetailBean> detailBeanList = new ArrayList<PatrolDetailBean>();
					for (int j = 0; j < detaiElement.size(); j++) {
						PatrolDetailBean detailBean = new PatrolDetailBean();
						detailBean.setTaskId(bean.getTaskId());
						detailBean.setDetailId(detaiElement.get(j).attributeValue("DetailId"));
						if (detailBean.getDetailId() == null || detailBean.getDetailId().equals("")) {
							continue;
						}
						detailBean.setFloor(detaiElement.get(j).attributeValue("FloorLay"));
						detailBean.setPoint(detaiElement.get(j).attributeValue("Project"));
						detailBean.setResult(detaiElement.get(j).attributeValue("Result"));
						detailBean.setTrouble(detaiElement.get(j).attributeValue("Danger"));

						String[] imageNames = new String[4];
						imageNames[0] = getImageName(detaiElement.get(j).attributeValue("ImageUrl1"));
						imageNames[1] = getImageName(detaiElement.get(j).attributeValue("ImageUrl2"));
						imageNames[2] = getImageName(detaiElement.get(j).attributeValue("ImageUrl3"));
						imageNames[3] = getImageName(detaiElement.get(j).attributeValue("ImageUrl4"));

						String[] versions = getImageversions(imageNames);
						detailBean.setPicture(imageNames[0] + "/" + imageNames[1] + "/" + imageNames[2] + "/" + imageNames[3]);
						detailBean.setPictureVersion(versions[0] + "/" + versions[1] + "/" + versions[2] + "/" + versions[3]);

						detailBean.setSolution(detaiElement.get(j).attributeValue("Rectification"));
						detailBean.setRepairDate(detaiElement.get(j).attributeValue("RepairDate"));
						// detailBean.setMark(Content.DB_DATA_SERVER);
						detailBeanList.add(detailBean);
					}
					bean.setDetailBeanList(detailBeanList);
				}
				beanlist.add(bean);
			}
		}
		result.setResult(true);
		result.setPatrolBeanList(beanlist);
		return result;
	}

	private PostResult ReceiptBack(PostResult result, Element root_xml) {
		String Value = root_xml.element("Parms").element("Result").getText();
		int count = 0;
		try {
			count = Content.parseInt(Value);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		if (count != 0) {
			result.setResult(true);
			result.setMessage("反馈NC成功！");
		} else {
			result.setResult(false);
			result.setMessage("反馈NC失败！" + root_xml.element("Parms").element("Msg").getText());
		}
		return result;
	}

	private PostResult SurveyBack(PostResult result, Element root_xml) {
		String Value = root_xml.element("Parms").element("Result").getText();
		int count = 0;
		try {
			count = Content.parseInt(Value);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		if (count != 0) {
			result.setResult(true);
			result.setMessage("反馈NC成功！");
		} else {
			result.setResult(false);
			result.setMessage("反馈NC失败！" + root_xml.element("Parms").element("Msg").getText());
		}
		return result;
	}

	private PostResult FumigateBack(PostResult result, Element root_xml) {
		String Value = root_xml.element("Parms").element("Result").getText();
		int count = 0;
		try {
			count = Content.parseInt(Value);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		if (count != 0) {
			result.setResult(true);
			result.setMessage("反馈NC成功！");
		} else {
			result.setResult(false);
			result.setMessage("反馈NC失败！" + root_xml.element("Parms").element("Msg").getText());
		}
		return result;
	}

	private PostResult PestsBack(PostResult result, Element root_xml) {
		String Value = root_xml.element("Parms").element("Result").getText();
		int count = 0;
		try {
			count = Content.parseInt(Value);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		if (count != 0) {
			result.setResult(true);
			result.setMessage("反馈NC成功！");
		} else {
			result.setResult(false);
			result.setMessage("反馈NC失败！" + root_xml.element("Parms").element("Msg").getText());
		}
		return result;
	}

	private PostResult PatrolBack(PostResult result, Element root_xml) {
		String Value = root_xml.element("Parms").element("Result").getText();
		int count = 0;
		try {
			count = Content.parseInt(Value);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		if (count != 0) {
			result.setResult(true);
			result.setMessage("反馈NC成功！");
		} else {
			result.setResult(false);
			result.setMessage("反馈NC失败！" + root_xml.element("Parms").element("Msg").getText());
		}
		return result;
	}

	private PostResult ExamineBack(PostResult result, Element root_xml) {
		String Value = root_xml.element("Parms").element("Result").getText();
		int count = 0;
		try {
			count = Content.parseInt(Value);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		if (count != 0) {
			result.setResult(true);
			result.setMessage("反馈NC成功！");
		} else {
			result.setResult(false);
			result.setMessage("反馈NC失败！" + root_xml.element("Parms").element("Msg").getText());
		}
		return result;
	}

	private PostResult AssignBack(PostResult result, Element root_xml) {
		String Value = root_xml.element("Parms").element("Result").getText();
		int count = 0;
		try {
			count = Content.parseInt(Value);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		if (count != 0) {
			result.setResult(true);
			result.setMessage("任务分配成功！");
		} else {
			result.setResult(false);
			result.setMessage("任务分配失败！" + root_xml.element("Parms").element("Msg").getText());
		}
		return result;
	}

	private PostResult OutPutBack(PostResult result, Element root_xml) {
		String Value = root_xml.element("Parms").element("Result").getText();
		int count = 0;
		try {
			count = Content.parseInt(Value);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		if (count != 0) {
			result.setResult(true);
			result.setMessage("反馈NC成功！");
		} else {
			result.setResult(false);
			result.setMessage("反馈NC失败！" + root_xml.element("Parms").element("Msg").getText());
		}
		return result;
	}

	private PostResult InPutBack(PostResult result, Element root_xml) {
		String Value = root_xml.element("Parms").element("Result").getText();
		int count = 0;
		try {
			count = Content.parseInt(Value);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		if (count != 0) {
			result.setResult(true);
			result.setMessage("反馈NC成功！");
		} else {
			result.setResult(false);
			result.setMessage("反馈NC失败！" + root_xml.element("Parms").element("Msg").getText());
		}
		return result;
	}

	private PostResult InventoryBack(PostResult result, Element root_xml) {
		String Value = root_xml.element("Parms").element("Result").getText();
		int count = 0;
		try {
			count = Content.parseInt(Value);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		if (count != 0) {
			result.setResult(true);
			result.setMessage("反馈NC成功！");
		} else {
			result.setResult(false);
			result.setMessage("反馈NC失败！" + root_xml.element("Parms").element("Msg").getText());
		}
		return result;
	}

	private PostResult returnMyXml(PostResult result, String function) {

		Document doc = null;
		String return_xml = "";

		if (function.contains("login")) {
			return_xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?><Request><Function>login</Function>"
					+ "<Parms><Username>陈铮亮</Username><Sysum>123456</Sysum><Floor>01,02,03,04,05,06,07,08,09,10,11,12,13,14,15,16,17,18</Floor><Result>1</Result></Parms></Request>";
			try {
				doc = DocumentHelper.parseText(return_xml);
			} catch (Exception e) {
				result.setResult(false);
				result.setMessage("数据出错！");
				return result;
			}
			result = Login(result, doc.getRootElement());
		} else if (function.contains("CheckComfirm")) {
			return_xml = "<?xml version=\"1.0\" encoding=\"gb2312\"?><Request> <Function>CheckComfirm</Function> <Parms> " +

					"<Task TaskId=\"3A6EA5C2724200CCE053AC101C3ECEDD\" TaskNumber=\"WRHA1604270090\" Date=\"2016/4/27 0:00:00\" CarNum=\"云D76475\" TypeName=\"采购入库质检任务\" FloorName=\"1号楼\" QueNo=\"6\" Line=\"3\" ItemCode=\"110739\" ItemName=\"云南曲靖沾益C3FLHD-2015X片烟\" Qty=\"85\" Weight=\"199.9860\" Samplingqty=\"6\" IsUpLoad=\"1\">"
					+ "	<TaskDetail DetailId=\"3B6EA5C2724300CCE053AC101C3ECEDD\" Weight=\"199.9860\" NWeight=\"1\" Water=\"12.323\" NWater=\"1\" Mould=\"1\" Appearance=\"1\" Odor=\"1\" varia=\"1\" Moth=\"1\" WaterDev=\"0.5090\" Density=\"13.1330\" Colligate=\"1\" CheckTime=\"2016/4/27 14:50:54\">"
					+ "		<TaskChild ChildId=\"1172A048372F00C6E053AC101C3E7D3E\" FKcode=\"01140783\" code=\"\" Weight=\"200.00\" nweight=\"1\" Water=\"12.64\" NWater=\"1\" Mould=\"1\" Appearance=\"2\" Odor=\"1\" Varia=\"1\" Moth=\"1\" waterdev=\"0.60\" Density=\"13.57\" Colligate=\"1\" CheckTime=\"2016/4/27 14:47:34\" />"
					+ "		<TaskChild ChildId=\"2172A048373000C6E053AC101C3E7D3E\" FKcode=\"01140681\" code=\"\" Weight=\"199.60\" nweight=\"1\" Water=\"12.37\" NWater=\"1\" Mould=\"1\" Appearance=\"1\" Odor=\"1\" Varia=\"1\" Moth=\"1\" waterdev=\"0.72\" Density=\"13.69\" Colligate=\"1\" CheckTime=\"2016/4/27 14:48:03\" />"
					+ "		<TaskChild ChildId=\"3172A048373100C6E053AC101C3E7D3E\" FKcode=\"01140696\" code=\"\" Weight=\"200.10\" nweight=\"2\" Water=\"12.18\" NWater=\"1\" Mould=\"1\" Appearance=\"1\" Odor=\"3\" Varia=\"1\" Moth=\"1\" waterdev=\"0.51\" Density=\"12.72\" Colligate=\"1\" CheckTime=\"2016/4/27 14:48:33\" />"
					+ "		<TaskChild ChildId=\"4172A048373200C6E053AC101C3E7D3E\" FKcode=\"01140787\" code=\"\" Weight=\"200.30\" nweight=\"1\" Water=\"12.42\" NWater=\"1\" Mould=\"1\" Appearance=\"1\" Odor=\"1\" Varia=\"1\" Moth=\"1\" waterdev=\"0.33\" Density=\"13.85\" Colligate=\"1\" CheckTime=\"2016/4/27 14:49:02\" />"
					+ "		<TaskChild ChildId=\"5172A048373300C6E053AC101C3E7D3E\" FKcode=\"01140695\" code=\"\" Weight=\"199.80\" nweight=\"1\" Water=\"12.09\" NWater=\"3\" Mould=\"1\" Appearance=\"1\" Odor=\"1\" Varia=\"2\" Moth=\"1\" waterdev=\"0.48\" Density=\"12.28\" Colligate=\"1\" CheckTime=\"2016/4/27 14:49:31\" />"
					+ "		<TaskChild ChildId=\"6172A048373400C6E053AC101C3E7D3E\" FKcode=\"01140702\" code=\"\" Weight=\"200.30\" nweight=\"1\" Water=\"12.41\" NWater=\"1\" Mould=\"1\" Appearance=\"1\" Odor=\"1\" Varia=\"1\" Moth=\"1\" waterdev=\"0.58\" Density=\"13.06\" Colligate=\"1\" CheckTime=\"2016/4/27 14:50:01\" />"
					+ "		<TaskChild ChildId=\"7172A048372E00C6E053AC101C3E7D3E\" FKcode=\"01140734\" code=\"\" Weight=\"199.80\" nweight=\"1\" Water=\"12.15\" NWater=\"1\" Mould=\"1\" Appearance=\"1\" Odor=\"1\" Varia=\"1\" Moth=\"1\" waterdev=\"0.34\" Density=\"12.76\" Colligate=\"1\" CheckTime=\"2016/4/27 14:47:05\" />"
					+ "</TaskDetail></Task>" +

					"</Parms></Request>";
			try {
				doc = DocumentHelper.parseText(return_xml);
			} catch (Exception e) {
				result.setResult(false);
				result.setMessage("数据出错！222");
				return result;
			}
			result = Examine(result, doc.getRootElement());
		} else if (function.contains("storageTask")) {
			MyLog.e(TAG, "sendXML", "function = " + function);
			return_xml = "<?xml version=\"1.0\" encoding=\"gb2312\"?><Request><Function>storageTask</Function><Parms>"
					+ "<Task TaskNumber=\"WRHB1612221995\" taskId=\"6148834e-cd30-4a2c-ba5d-c081e8b58ac8\" isCancel=\"2\" statu=\"0\" carNum=\"闽F66155\" ComputerCode=\"42179722\" TransportCode=\"YLDYD201612210008\" ableNum=\"15\" floorName=\"6号楼\" weightAble=\"750\" numberFact=\"15\" weightFact=\"750\" tasktype=\"RK001\" typename=\"烟叶采购入库任务\" date=\"2016/12/22 0:00:00\" IsUpLoad=\"0\" opername=\"林祥跃\" responname=\"林祥跃\"><TaskDetail taskId=\"6148834e-cd30-4a2c-ba5d-c081e8b58ac8\" TaskNumber=\"WRHB1612221995\" detailId=\"d4275f19-8011-49da-ad0a-473748debaab\" itemname=\"福建C-2016梗\" itemcode=\"100612\" locationcode=\"060523\" locationname=\"DF060523\" ableNum=\"15\" spec=\"50\" weightAble=\"750\" weightFact=\"750\" numberFact=\"15\" statu=\"0\" /></Task>"
					+
					// "<Task TaskNumber=\"WRHB1612221994\"
					// taskId=\"d509b035-c3e6-423e-8e4c-ecb933ef6191\"
					// isCancel=\"2\" statu=\"0\" carNum=\"闽F66155\"
					// ComputerCode=\"42179722\"
					// TransportCode=\"YLDYD201612210007\" ableNum=\"79\"
					// floorName=\"6号楼\" weightAble=\"3950\" numberFact=\"79\"
					// weightFact=\"3950\" tasktype=\"RK001\"
					// typename=\"烟叶采购入库任务\" date=\"2016/12/22 0:00:00\"
					// IsUpLoad=\"0\" opername=\"林祥跃\"
					// responname=\"林祥跃\"><TaskDetail
					// taskId=\"d509b035-c3e6-423e-8e4c-ecb933ef6191\"
					// TaskNumber=\"WRHB1612221994\"
					// detailId=\"498a8aa1-0fcf-41f9-a821-1f509c8b65a7\"
					// itemname=\"福建C-2016梗\" itemcode=\"100612\"
					// locationcode=\"060523\" locationname=\"DF060523\"
					// ableNum=\"79\" spec=\"50\" weightAble=\"3950\"
					// weightFact=\"3950\" numberFact=\"79\" statu=\"0\"
					// /></Task>" +
					// "<Task TaskNumber=\"WRHB1612221993\"
					// taskId=\"6df46e9d-bfa8-4d06-9639-a86d0f47372d\"
					// isCancel=\"2\" statu=\"0\" carNum=\"闽F66155\"
					// ComputerCode=\"42179722\"
					// TransportCode=\"YLDYD201612210006\" ableNum=\"55\"
					// floorName=\"6号楼\" weightAble=\"2750\" numberFact=\"55\"
					// weightFact=\"2750\" tasktype=\"RK001\"
					// typename=\"烟叶采购入库任务\" date=\"2016/12/22 0:00:00\"
					// IsUpLoad=\"0\" opername=\"林祥跃\"
					// responname=\"林祥跃\"><TaskDetail
					// taskId=\"6df46e9d-bfa8-4d06-9639-a86d0f47372d\"
					// TaskNumber=\"WRHB1612221993\"
					// detailId=\"e8403bbb-b3bd-45c2-a964-bc14343644bf\"
					// itemname=\"福建X-2016梗\" itemcode=\"100613\"
					// locationcode=\"060517\" locationname=\"DF060517\"
					// ableNum=\"55\" spec=\"50\" weightAble=\"2750\"
					// weightFact=\"2750\" numberFact=\"55\" statu=\"0\"
					// /></Task>" +
					// "<Task TaskNumber=\"WRHB1612221992\"
					// taskId=\"32108342-cf8c-4476-afda-86db35f07bda\"
					// isCancel=\"2\" statu=\"0\" carNum=\"闽F66155\"
					// ComputerCode=\"42179722\"
					// TransportCode=\"YLDYD201612210005\" ableNum=\"111\"
					// floorName=\"6号楼\" weightAble=\"5550\" numberFact=\"111\"
					// weightFact=\"5550\" tasktype=\"RK001\"
					// typename=\"烟叶采购入库任务\" date=\"2016/12/22 0:00:00\"
					// IsUpLoad=\"0\" opername=\"林祥跃\"
					// responname=\"林祥跃\"><TaskDetail
					// taskId=\"32108342-cf8c-4476-afda-86db35f07bda\"
					// TaskNumber=\"WRHB1612221992\"
					// detailId=\"f8921828-2c8e-44d4-ad47-35689f1744c8\"
					// itemname=\"福建X-2016梗\" itemcode=\"100613\"
					// locationcode=\"060517\" locationname=\"DF060517\"
					// ableNum=\"111\" spec=\"50\" weightAble=\"5550\"
					// weightFact=\"5550\" numberFact=\"111\" statu=\"0\"
					// /></Task>" +
					// "<Task TaskNumber=\"WRHB1612211988\"
					// taskId=\"25af9a70-1e11-441d-83f8-ae9a85c725b0\"
					// isCancel=\"2\" statu=\"0\" carNum=\"云D80030\"
					// ComputerCode=\"42176267；42176269\"
					// TransportCode=\"YLDYD201612190003\" ableNum=\"43\"
					// floorName=\"4号楼\" weightAble=\"8600\" numberFact=\"43\"
					// weightFact=\"8600\" tasktype=\"RK001\"
					// typename=\"烟叶采购入库任务\" date=\"2016/12/21 0:00:00\"
					// IsUpLoad=\"0\" opername=\"黄良辉\"
					// responname=\"黄良辉\"><TaskDetail
					// taskId=\"25af9a70-1e11-441d-83f8-ae9a85c725b0\"
					// TaskNumber=\"WRHB1612211988\"
					// detailId=\"ae84a0fc-3f01-4d96-a9d7-5e10853e2319\"
					// itemname=\"云南普洱景东B2F-2016片烟\" itemcode=\"110763\"
					// locationcode=\"040515\" locationname=\"DF040515\"
					// ableNum=\"43\" spec=\"200\" weightAble=\"8600\"
					// weightFact=\"8600\" numberFact=\"43\" statu=\"0\"
					// /></Task>" +
					// "<Task TaskNumber=\"WRHB1612191929\"
					// taskId=\"dabf7d0e-7161-4841-a7be-c5a3c4ce7198\"
					// isCancel=\"2\" statu=\"0\" carNum=\"闽F65360\"
					// ComputerCode=\"42100001\"
					// TransportCode=\"YLDYD201610100009\" ableNum=\"15\"
					// floorName=\"4号楼\" weightAble=\"3000\" numberFact=\"15\"
					// weightFact=\"3000\" tasktype=\"RK001\"
					// typename=\"烟叶采购入库任务\" date=\"2016/12/19 0:00:00\"
					// IsUpLoad=\"0\" opername=\"黄良辉\"
					// responname=\"黄良辉\"><TaskDetail
					// taskId=\"dabf7d0e-7161-4841-a7be-c5a3c4ce7198\"
					// TaskNumber=\"WRHB1612191929\"
					// detailId=\"371a316b-da89-4a1b-82a5-f752dec97e9c\"
					// itemname=\"福建尤永长X3FX03-2016片烟\" itemcode=\"100616\"
					// locationcode=\"040514\" locationname=\"DF040514\"
					// ableNum=\"15\" spec=\"200\" weightAble=\"3000\"
					// weightFact=\"3000\" numberFact=\"15\" statu=\"0\"
					// /></Task>" +
					// "<Task TaskNumber=\"WRHB1612191927\"
					// taskId=\"9eb74541-9c39-4cf8-8cae-b770cbc5bd6a\"
					// isCancel=\"2\" statu=\"0\" carNum=\"闽F66030\"
					// ComputerCode=\"42097925\"
					// TransportCode=\"YLDYD201610080004\" ableNum=\"124\"
					// floorName=\"4号楼\" weightAble=\"24800\" numberFact=\"124\"
					// weightFact=\"24800\" tasktype=\"RK001\"
					// typename=\"烟叶采购入库任务\" date=\"2016/12/19 0:00:00\"
					// IsUpLoad=\"0\" opername=\"黄良辉\"
					// responname=\"黄良辉\"><TaskDetail
					// taskId=\"9eb74541-9c39-4cf8-8cae-b770cbc5bd6a\"
					// TaskNumber=\"WRHB1612191927\"
					// detailId=\"c820edc8-e55c-4dec-bf40-075664db57ed\"
					// itemname=\"福建尤永长X3FX03-2016片烟\" itemcode=\"100616\"
					// locationcode=\"040514\" locationname=\"DF040514\"
					// ableNum=\"124\" spec=\"200\" weightAble=\"24800\"
					// weightFact=\"24800\" numberFact=\"124\" statu=\"0\"
					// /></Task>" +
					// "<Task TaskNumber=\"WRHB1612051576\"
					// taskId=\"e5d7d4f6-cabe-4c13-9f81-9099ae4f6e85\"
					// isCancel=\"2\" statu=\"0\" carNum=\"\" ComputerCode=\"\"
					// TransportCode=\"\" ableNum=\"50\" floorName=\"18号楼\"
					// weightAble=\"10000\" numberFact=\"0\" weightFact=\"0\"
					// tasktype=\"RK003\" typename=\"货位调整(翻垛)入库任务\"
					// date=\"2016/12/5 0:00:00\" IsUpLoad=\"0\"
					// opername=\"陈珊红\" responname=\"陈珊红\"><TaskDetail
					// taskId=\"e5d7d4f6-cabe-4c13-9f81-9099ae4f6e85\"
					// TaskNumber=\"WRHB1612051576\"
					// detailId=\"11bf07e9-89e6-47a1-a5ae-72296da01854\"
					// itemname=\"贵州遵义铜仁B2F-2015片烟\" itemcode=\"120287\"
					// locationcode=\"180108\" locationname=\"DF180108\"
					// ableNum=\"50\" spec=\"200\" weightAble=\"10000\"
					// weightFact=\"0\" numberFact=\"0\" statu=\"1\" /></Task>"
					// +
					// "<Task TaskNumber=\"WRHB1611221378\"
					// taskId=\"e6ae1aa6-c6e5-44b1-baaa-ab99f55103f8\"
					// isCancel=\"2\" statu=\"0\" carNum=\"\" ComputerCode=\"\"
					// TransportCode=\"\" ableNum=\"6\" floorName=\"11号楼\"
					// weightAble=\"1200\" numberFact=\"6\" weightFact=\"1200\"
					// tasktype=\"RK003\" typename=\"货位调整(翻垛)入库任务\"
					// date=\"2016/11/22 0:00:00\" IsUpLoad=\"0\"
					// opername=\"李旺\" responname=\"李旺\"><TaskDetail
					// taskId=\"e6ae1aa6-c6e5-44b1-baaa-ab99f55103f8\"
					// TaskNumber=\"WRHB1611221378\"
					// detailId=\"a5be4b2d-09c4-43cf-8363-5de400424fd0\"
					// itemname=\"阿根廷FujianSTS-3/2013\" itemcode=\"170134\"
					// locationcode=\"110201\" locationname=\"DF110201\"
					// ableNum=\"6\" spec=\"200\" weightAble=\"1200\"
					// weightFact=\"1200\" numberFact=\"6\" statu=\"0\"
					// /></Task>" +
					// "<Task TaskNumber=\"WRHB1611181356\"
					// taskId=\"5f5f2f66-6708-4a84-9986-713a85c07a96\"
					// isCancel=\"2\" statu=\"0\" carNum=\"\" ComputerCode=\"\"
					// TransportCode=\"\" ableNum=\"0\" floorName=\"15号楼\"
					// weightAble=\"54000\" numberFact=\"0\"
					// weightFact=\"54000\" tasktype=\"RK003\"
					// typename=\"货位调整(翻垛)入库任务\" date=\"2016/11/18 0:00:00\"
					// IsUpLoad=\"0\" opername=\"杨嘉兴\"
					// responname=\"杨嘉兴\"><TaskDetail
					// taskId=\"5f5f2f66-6708-4a84-9986-713a85c07a96\"
					// TaskNumber=\"WRHB1611181356\"
					// detailId=\"458c0dd5-9212-4173-a895-a0aa582e8876\"
					// itemname=\"贵州遵义务川C2F1-2013T片烟\" itemcode=\"120211\"
					// locationcode=\"150513\" locationname=\"DF150513\"
					// ableNum=\"0\" spec=\"200\" weightAble=\"54000\"
					// weightFact=\"54000\" numberFact=\"0\" statu=\"0\"
					// /></Task>" +
					// "<Task TaskNumber=\"WRHB1611031241\"
					// taskId=\"11bdbcc2-ee66-42d0-9905-9b2bf90f8b77\"
					// isCancel=\"2\" statu=\"0\" carNum=\"\" ComputerCode=\"\"
					// TransportCode=\"\" ableNum=\"43\" floorName=\"11号楼\"
					// weightAble=\"8600\" numberFact=\"43\" weightFact=\"8600\"
					// tasktype=\"RK006\" typename=\"微波水分检测入库任务\"
					// date=\"2016/11/3 0:00:00\" IsUpLoad=\"0\" opername=\"李旺\"
					// responname=\"李旺\"><TaskDetail
					// taskId=\"11bdbcc2-ee66-42d0-9905-9b2bf90f8b77\"
					// TaskNumber=\"WRHB1611031241\"
					// detailId=\"d3100e6f-5b05-4a75-bf71-abbfc21e31e7\"
					// itemname=\"津巴布韦OLT-2015片烟\" itemcode=\"170222\"
					// locationcode=\"110317\" locationname=\"DF110317\"
					// ableNum=\"8\" spec=\"200\" weightAble=\"1600\"
					// weightFact=\"1600\" numberFact=\"8\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"11bdbcc2-ee66-42d0-9905-9b2bf90f8b77\"
					// TaskNumber=\"WRHB1611031241\"
					// detailId=\"8c2490cd-2cde-4981-9cf8-09506c091cb7\"
					// itemname=\"津巴布韦OLT-2015片烟\" itemcode=\"170222\"
					// locationcode=\"110309\" locationname=\"DF110309\"
					// ableNum=\"10\" spec=\"200\" weightAble=\"2000\"
					// weightFact=\"2000\" numberFact=\"10\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"11bdbcc2-ee66-42d0-9905-9b2bf90f8b77\"
					// TaskNumber=\"WRHB1611031241\"
					// detailId=\"dd30f2a9-73c4-418c-b227-0063339c7e8d\"
					// itemname=\"津巴布韦OLT-2015片烟\" itemcode=\"170222\"
					// locationcode=\"110307\" locationname=\"DF110307\"
					// ableNum=\"11\" spec=\"200\" weightAble=\"2200\"
					// weightFact=\"2200\" numberFact=\"11\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"11bdbcc2-ee66-42d0-9905-9b2bf90f8b77\"
					// TaskNumber=\"WRHB1611031241\"
					// detailId=\"48033ba8-42fb-4e5f-a02a-91af7f783ede\"
					// itemname=\"津巴布韦OLT-2015片烟\" itemcode=\"170222\"
					// locationcode=\"110305\" locationname=\"DF110305\"
					// ableNum=\"11\" spec=\"200\" weightAble=\"2200\"
					// weightFact=\"2200\" numberFact=\"11\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"11bdbcc2-ee66-42d0-9905-9b2bf90f8b77\"
					// TaskNumber=\"WRHB1611031241\"
					// detailId=\"94720ad1-1f6e-41c9-9b43-d030fe4d3263\"
					// itemname=\"贵州铜仁德江C2F-2013X片烟\" itemcode=\"120230\"
					// locationcode=\"110216\" locationname=\"DF110216\"
					// ableNum=\"3\" spec=\"200\" weightAble=\"600\"
					// weightFact=\"600\" numberFact=\"3\" statu=\"0\"
					// /></Task>" +
					// "<Task TaskNumber=\"WRHB1611031240\"
					// taskId=\"6c5ec156-cb85-4656-bcfe-0363f1c44b7c\"
					// isCancel=\"2\" statu=\"0\" carNum=\"\" ComputerCode=\"\"
					// TransportCode=\"\" ableNum=\"6\" floorName=\"14号楼\"
					// weightAble=\"1200\" numberFact=\"6\" weightFact=\"1200\"
					// tasktype=\"RK006\" typename=\"微波水分检测入库任务\"
					// date=\"2016/11/3 0:00:00\" IsUpLoad=\"0\" opername=\"黄臻\"
					// responname=\"黄臻\"><TaskDetail
					// taskId=\"6c5ec156-cb85-4656-bcfe-0363f1c44b7c\"
					// TaskNumber=\"WRHB1611031240\"
					// detailId=\"618be31e-7cad-47e0-bbd5-9b290887b2bf\"
					// itemname=\"河南漯河临颍B2F-2013片烟\" itemcode=\"130087\"
					// locationcode=\"140103\" locationname=\"DF140103\"
					// ableNum=\"6\" spec=\"200\" weightAble=\"1200\"
					// weightFact=\"1200\" numberFact=\"6\" statu=\"0\"
					// /></Task>" +
					// "<Task TaskNumber=\"WRHB1611031227\"
					// taskId=\"492c947b-37d9-4666-bafb-2473eacac3a8\"
					// isCancel=\"2\" statu=\"0\" carNum=\"\" ComputerCode=\"\"
					// TransportCode=\"\" ableNum=\"7\" floorName=\"7号楼\"
					// weightAble=\"1395\" numberFact=\"7\" weightFact=\"1395\"
					// tasktype=\"RK006\" typename=\"微波水分检测入库任务\"
					// date=\"2016/11/3 0:00:00\" IsUpLoad=\"0\"
					// opername=\"黄吉辉\" responname=\"黄吉辉\"><TaskDetail
					// taskId=\"492c947b-37d9-4666-bafb-2473eacac3a8\"
					// TaskNumber=\"WRHB1611031227\"
					// detailId=\"58a773ec-02a2-4c8d-9dee-b944f6ebce05\"
					// itemname=\"陕西安康旬阳C2F-2013T片烟\" itemcode=\"150069\"
					// locationcode=\"070215\" locationname=\"DF070215\"
					// ableNum=\"1\" spec=\"200\" weightAble=\"195\"
					// weightFact=\"195\" numberFact=\"1\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"492c947b-37d9-4666-bafb-2473eacac3a8\"
					// TaskNumber=\"WRHB1611031227\"
					// detailId=\"2aa9bc64-b84c-4e63-97a4-449d8f145ba7\"
					// itemname=\"陕西安康旬阳C2F-2013T片烟\" itemcode=\"150069\"
					// locationcode=\"070215\" locationname=\"DF070215\"
					// ableNum=\"6\" spec=\"200\" weightAble=\"1200\"
					// weightFact=\"1200\" numberFact=\"6\" statu=\"0\"
					// /></Task>" +
					// "<Task TaskNumber=\"WRHB1610261083\"
					// taskId=\"d58bd33f-c58a-429f-9b56-f792e25bf63b\"
					// isCancel=\"2\" statu=\"0\" carNum=\"\" ComputerCode=\"\"
					// TransportCode=\"\" ableNum=\"104\" floorName=\"11号楼\"
					// weightAble=\"20650\" numberFact=\"104\"
					// weightFact=\"20650\" tasktype=\"RK006\"
					// typename=\"微波水分检测入库任务\" date=\"2016/10/26 0:00:00\"
					// IsUpLoad=\"0\" opername=\"李旺\"
					// responname=\"李旺\"><TaskDetail
					// taskId=\"d58bd33f-c58a-429f-9b56-f792e25bf63b\"
					// TaskNumber=\"WRHB1610261083\"
					// detailId=\"2b0ee092-c5c2-49c9-a9e4-43a97d455b62\"
					// itemname=\"贵州毕节B3F-2014片烟\" itemcode=\"120264\"
					// locationcode=\"110216\" locationname=\"DF110216\"
					// ableNum=\"5\" spec=\"200\" weightAble=\"1000\"
					// weightFact=\"1000\" numberFact=\"5\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"d58bd33f-c58a-429f-9b56-f792e25bf63b\"
					// TaskNumber=\"WRHB1610261083\"
					// detailId=\"84c8bcc6-5655-4d9d-9d82-0b5ca9851721\"
					// itemname=\"津巴布韦L1O/C(天利)-2014片烟\" itemcode=\"170190\"
					// locationcode=\"110405\" locationname=\"DF110405\"
					// ableNum=\"11\" spec=\"200\" weightAble=\"2200\"
					// weightFact=\"2200\" numberFact=\"11\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"d58bd33f-c58a-429f-9b56-f792e25bf63b\"
					// TaskNumber=\"WRHB1610261083\"
					// detailId=\"5f9c5c38-20d5-410c-8584-1e9777d87b99\"
					// itemname=\"津巴布韦L1O/C(天利)-2014片烟\" itemcode=\"170190\"
					// locationcode=\"110417\" locationname=\"DF110417\"
					// ableNum=\"4\" spec=\"200\" weightAble=\"800\"
					// weightFact=\"800\" numberFact=\"4\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"d58bd33f-c58a-429f-9b56-f792e25bf63b\"
					// TaskNumber=\"WRHB1610261083\"
					// detailId=\"c73dce54-d355-4c6e-9285-e26a9ca8287a\"
					// itemname=\"津巴布韦L1O/C(天利)-2014片烟\" itemcode=\"170190\"
					// locationcode=\"110407\" locationname=\"DF110407\"
					// ableNum=\"11\" spec=\"200\" weightAble=\"2200\"
					// weightFact=\"2200\" numberFact=\"11\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"d58bd33f-c58a-429f-9b56-f792e25bf63b\"
					// TaskNumber=\"WRHB1610261083\"
					// detailId=\"66e45ded-8886-4dbc-b6ab-a4ce325038f0\"
					// itemname=\"津巴布韦L1O/C(天利)-2014片烟\" itemcode=\"170190\"
					// locationcode=\"110107\" locationname=\"DF110107\"
					// ableNum=\"1\" spec=\"200\" weightAble=\"200\"
					// weightFact=\"200\" numberFact=\"1\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"d58bd33f-c58a-429f-9b56-f792e25bf63b\"
					// TaskNumber=\"WRHB1610261083\"
					// detailId=\"78dd80d4-dd29-4d5f-a519-7a3b28889c76\"
					// itemname=\"津巴布韦L1O/C(天利)-2014片烟\" itemcode=\"170190\"
					// locationcode=\"110107\" locationname=\"DF110107\"
					// ableNum=\"1\" spec=\"200\" weightAble=\"50\"
					// weightFact=\"50\" numberFact=\"1\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"d58bd33f-c58a-429f-9b56-f792e25bf63b\"
					// TaskNumber=\"WRHB1610261083\"
					// detailId=\"a235f5d9-2fe2-484e-91b9-0b8419ff1df9\"
					// itemname=\"山东潍坊B3F-2014片烟\" itemcode=\"160309\"
					// locationcode=\"110202\" locationname=\"DF110202\"
					// ableNum=\"7\" spec=\"200\" weightAble=\"1400\"
					// weightFact=\"1400\" numberFact=\"7\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"d58bd33f-c58a-429f-9b56-f792e25bf63b\"
					// TaskNumber=\"WRHB1610261083\"
					// detailId=\"a3898235-650a-479c-942f-77ee13556dfb\"
					// itemname=\"江西吉安B2F-2013片烟\" itemcode=\"160244\"
					// locationcode=\"110107\" locationname=\"DF110107\"
					// ableNum=\"1\" spec=\"200\" weightAble=\"200\"
					// weightFact=\"200\" numberFact=\"1\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"d58bd33f-c58a-429f-9b56-f792e25bf63b\"
					// TaskNumber=\"WRHB1610261083\"
					// detailId=\"d6532b56-d584-4c42-ba6b-aeb4dc09c455\"
					// itemname=\"贵州遵义务川C3F-2013X片烟\" itemcode=\"120201\"
					// locationcode=\"110212\" locationname=\"DF110212\"
					// ableNum=\"7\" spec=\"200\" weightAble=\"1400\"
					// weightFact=\"1400\" numberFact=\"7\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"d58bd33f-c58a-429f-9b56-f792e25bf63b\"
					// TaskNumber=\"WRHB1610261083\"
					// detailId=\"f6471cf1-8ced-4c87-b9b9-ac8b0a28a061\"
					// itemname=\"贵州遵义务川C3F-2013X片烟\" itemcode=\"120201\"
					// locationcode=\"110108\" locationname=\"DF110108\"
					// ableNum=\"2\" spec=\"200\" weightAble=\"400\"
					// weightFact=\"400\" numberFact=\"2\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"d58bd33f-c58a-429f-9b56-f792e25bf63b\"
					// TaskNumber=\"WRHB1610261083\"
					// detailId=\"3f31a5a2-71a9-4e2e-aa70-753a6409820e\"
					// itemname=\"陕西安康旬阳C34LF-2013片烟\" itemcode=\"150068\"
					// locationcode=\"110304\" locationname=\"DF110304\"
					// ableNum=\"11\" spec=\"200\" weightAble=\"2200\"
					// weightFact=\"2200\" numberFact=\"11\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"d58bd33f-c58a-429f-9b56-f792e25bf63b\"
					// TaskNumber=\"WRHB1610261083\"
					// detailId=\"dc4ce932-77f3-408a-87e5-c7f84f0b606c\"
					// itemname=\"陕西安康旬阳C3F-2013X片烟\" itemcode=\"150067\"
					// locationcode=\"110306\" locationname=\"DF110306\"
					// ableNum=\"11\" spec=\"200\" weightAble=\"2200\"
					// weightFact=\"2200\" numberFact=\"11\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"d58bd33f-c58a-429f-9b56-f792e25bf63b\"
					// TaskNumber=\"WRHB1610261083\"
					// detailId=\"e4db3b40-77cd-46eb-944b-a7fa30c39dec\"
					// itemname=\"陕西安康旬阳C3F-2013X片烟\" itemcode=\"150067\"
					// locationcode=\"110308\" locationname=\"DF110308\"
					// ableNum=\"3\" spec=\"200\" weightAble=\"600\"
					// weightFact=\"600\" numberFact=\"3\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"d58bd33f-c58a-429f-9b56-f792e25bf63b\"
					// TaskNumber=\"WRHB1610261083\"
					// detailId=\"01040e23-923b-4805-a9cd-a468061b38ee\"
					// itemname=\"贵州遵义务川C3F1-2013X片烟\" itemcode=\"120210\"
					// locationcode=\"110314\" locationname=\"DF110314\"
					// ableNum=\"1\" spec=\"200\" weightAble=\"200\"
					// weightFact=\"200\" numberFact=\"1\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"d58bd33f-c58a-429f-9b56-f792e25bf63b\"
					// TaskNumber=\"WRHB1610261083\"
					// detailId=\"50c96ecd-efd6-4a45-8feb-9c67432a78a2\"
					// itemname=\"云南烟叶公司B2FA-2014片烟\" itemcode=\"110664\"
					// locationcode=\"110208\" locationname=\"DF110208\"
					// ableNum=\"10\" spec=\"200\" weightAble=\"2000\"
					// weightFact=\"2000\" numberFact=\"10\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"d58bd33f-c58a-429f-9b56-f792e25bf63b\"
					// TaskNumber=\"WRHB1610261083\"
					// detailId=\"32eb7bb5-868e-4c54-a45f-298059677ac5\"
					// itemname=\"河南洛阳B2F-2013片烟\" itemcode=\"130103\"
					// locationcode=\"110310\" locationname=\"DF110310\"
					// ableNum=\"1\" spec=\"200\" weightAble=\"200\"
					// weightFact=\"200\" numberFact=\"1\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"d58bd33f-c58a-429f-9b56-f792e25bf63b\"
					// TaskNumber=\"WRHB1610261083\"
					// detailId=\"4c68c794-b87b-4b23-8872-ac21aea1cba1\"
					// itemname=\"贵州铜仁B2FLY-2014片烟\" itemcode=\"120272\"
					// locationcode=\"110214\" locationname=\"DF110214\"
					// ableNum=\"7\" spec=\"200\" weightAble=\"1400\"
					// weightFact=\"1400\" numberFact=\"7\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"d58bd33f-c58a-429f-9b56-f792e25bf63b\"
					// TaskNumber=\"WRHB1610261083\"
					// detailId=\"6e9cfd89-b5f7-4348-8878-5bc0c4d98f11\"
					// itemname=\"云南烟叶公司C4F-2014片烟\" itemcode=\"110660\"
					// locationcode=\"110210\" locationname=\"DF110210\"
					// ableNum=\"10\" spec=\"200\" weightAble=\"2000\"
					// weightFact=\"2000\" numberFact=\"10\" statu=\"0\"
					// /></Task>" +
					// "<Task TaskNumber=\"WRHB1610211051\"
					// taskId=\"0da3c7eb-2a2f-411c-85f9-262111ec3d84\"
					// isCancel=\"2\" statu=\"0\" carNum=\"\" ComputerCode=\"\"
					// TransportCode=\"\" ableNum=\"309\" floorName=\"15号楼\"
					// weightAble=\"61800\" numberFact=\"309\"
					// weightFact=\"61800\" tasktype=\"RK006\"
					// typename=\"微波水分检测入库任务\" date=\"2016/10/21 0:00:00\"
					// IsUpLoad=\"0\" opername=\"杨嘉兴\"
					// responname=\"杨嘉兴\"><TaskDetail
					// taskId=\"0da3c7eb-2a2f-411c-85f9-262111ec3d84\"
					// TaskNumber=\"WRHB1610211051\"
					// detailId=\"794d1636-f9a3-40cc-ac50-f2212ae79ced\"
					// itemname=\"福建南平BX3F-2015片烟\" itemcode=\"100607\"
					// locationcode=\"150415\" locationname=\"DF150415\"
					// ableNum=\"7\" spec=\"200\" weightAble=\"1400\"
					// weightFact=\"1400\" numberFact=\"7\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"0da3c7eb-2a2f-411c-85f9-262111ec3d84\"
					// TaskNumber=\"WRHB1610211051\"
					// detailId=\"85fd2b1e-391b-48ec-bbab-901f2faf83f8\"
					// itemname=\"贵州铜仁X2F-2014片烟\" itemcode=\"120249\"
					// locationcode=\"150503\" locationname=\"DF150503\"
					// ableNum=\"11\" spec=\"200\" weightAble=\"2200\"
					// weightFact=\"2200\" numberFact=\"11\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"0da3c7eb-2a2f-411c-85f9-262111ec3d84\"
					// TaskNumber=\"WRHB1610211051\"
					// detailId=\"5c0e2c61-9f3e-47c7-b22d-fd4699a15b44\"
					// itemname=\"江西吉安C34FL-2014片烟\" itemcode=\"160306\"
					// locationcode=\"150411\" locationname=\"DF150411\"
					// ableNum=\"7\" spec=\"200\" weightAble=\"1400\"
					// weightFact=\"1400\" numberFact=\"7\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"0da3c7eb-2a2f-411c-85f9-262111ec3d84\"
					// TaskNumber=\"WRHB1610211051\"
					// detailId=\"eba18eba-fb8d-4723-9eae-22e814a43573\"
					// itemname=\"江西吉安C34FL-2014片烟\" itemcode=\"160306\"
					// locationcode=\"150407\" locationname=\"DF150407\"
					// ableNum=\"11\" spec=\"200\" weightAble=\"2200\"
					// weightFact=\"2200\" numberFact=\"11\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"0da3c7eb-2a2f-411c-85f9-262111ec3d84\"
					// TaskNumber=\"WRHB1610211051\"
					// detailId=\"c30d85d1-3a5b-4d86-9bd3-9a2429d1e1d3\"
					// itemname=\"江西吉安C34FL-2014片烟\" itemcode=\"160306\"
					// locationcode=\"150413\" locationname=\"DF150413\"
					// ableNum=\"6\" spec=\"200\" weightAble=\"1200\"
					// weightFact=\"1200\" numberFact=\"6\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"0da3c7eb-2a2f-411c-85f9-262111ec3d84\"
					// TaskNumber=\"WRHB1610211051\"
					// detailId=\"faadca17-c578-447d-82cc-3346ecbb8fc9\"
					// itemname=\"陕西安康旬阳C4F-2014片烟\" itemcode=\"150079\"
					// locationcode=\"150416\" locationname=\"DF150416\"
					// ableNum=\"5\" spec=\"200\" weightAble=\"1000\"
					// weightFact=\"1000\" numberFact=\"5\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"0da3c7eb-2a2f-411c-85f9-262111ec3d84\"
					// TaskNumber=\"WRHB1610211051\"
					// detailId=\"8ad1aed6-4b8e-4f3a-8992-643d77cacc76\"
					// itemname=\"陕西安康旬阳C4F-2014片烟\" itemcode=\"150079\"
					// locationcode=\"150402\" locationname=\"DF150402\"
					// ableNum=\"9\" spec=\"200\" weightAble=\"1800\"
					// weightFact=\"1800\" numberFact=\"9\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"0da3c7eb-2a2f-411c-85f9-262111ec3d84\"
					// TaskNumber=\"WRHB1610211051\"
					// detailId=\"a5561702-fdb4-403b-9e44-90980c1fabc3\"
					// itemname=\"陕西安康旬阳C4F-2015片烟\" itemcode=\"150086\"
					// locationcode=\"150502\" locationname=\"DF150502\"
					// ableNum=\"9\" spec=\"200\" weightAble=\"1800\"
					// weightFact=\"1800\" numberFact=\"9\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"0da3c7eb-2a2f-411c-85f9-262111ec3d84\"
					// TaskNumber=\"WRHB1610211051\"
					// detailId=\"a0cbbdf1-f2d7-450a-828f-4a0cf16d491d\"
					// itemname=\"贵州遵义务川C3F-2015M片烟\" itemcode=\"120290\"
					// locationcode=\"150510\" locationname=\"DF150510\"
					// ableNum=\"10\" spec=\"200\" weightAble=\"2000\"
					// weightFact=\"2000\" numberFact=\"10\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"0da3c7eb-2a2f-411c-85f9-262111ec3d84\"
					// TaskNumber=\"WRHB1610211051\"
					// detailId=\"6e00c5cd-383d-4434-aa78-61dbd7d6412f\"
					// itemname=\"河南驻马店确山B34F-2014片烟\" itemcode=\"130114\"
					// locationcode=\"150414\" locationname=\"DF150414\"
					// ableNum=\"6\" spec=\"200\" weightAble=\"1200\"
					// weightFact=\"1200\" numberFact=\"6\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"0da3c7eb-2a2f-411c-85f9-262111ec3d84\"
					// TaskNumber=\"WRHB1610211051\"
					// detailId=\"2de00bf6-0b5b-4bc3-b431-360036139c34\"
					// itemname=\"河南驻马店确山B34F-2014片烟\" itemcode=\"130114\"
					// locationcode=\"150408\" locationname=\"DF150408\"
					// ableNum=\"11\" spec=\"200\" weightAble=\"2200\"
					// weightFact=\"2200\" numberFact=\"11\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"0da3c7eb-2a2f-411c-85f9-262111ec3d84\"
					// TaskNumber=\"WRHB1610211051\"
					// detailId=\"da57157c-4069-4c8f-b1b2-f4876ea7beb0\"
					// itemname=\"贵州遵义务川C3F-2015T片烟\" itemcode=\"120289\"
					// locationcode=\"150504\" locationname=\"DF150504\"
					// ableNum=\"11\" spec=\"200\" weightAble=\"2200\"
					// weightFact=\"2200\" numberFact=\"11\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"0da3c7eb-2a2f-411c-85f9-262111ec3d84\"
					// TaskNumber=\"WRHB1610211051\"
					// detailId=\"92f8acc3-1123-47b9-a055-858d21b272c4\"
					// itemname=\"贵州遵义务川C3F-2015T片烟\" itemcode=\"120289\"
					// locationcode=\"150518\" locationname=\"DF150518\"
					// ableNum=\"5\" spec=\"200\" weightAble=\"1000\"
					// weightFact=\"1000\" numberFact=\"5\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"0da3c7eb-2a2f-411c-85f9-262111ec3d84\"
					// TaskNumber=\"WRHB1610211051\"
					// detailId=\"78787fbf-c20f-4a5d-a65a-21aa4ce1c2b3\"
					// itemname=\"江西赣州B3F-2013片烟\" itemcode=\"160222\"
					// locationcode=\"150517\" locationname=\"DF150517\"
					// ableNum=\"8\" spec=\"200\" weightAble=\"1600\"
					// weightFact=\"1600\" numberFact=\"8\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"0da3c7eb-2a2f-411c-85f9-262111ec3d84\"
					// TaskNumber=\"WRHB1610211051\"
					// detailId=\"fd87f1fe-3033-4173-9f00-e1dc543c4806\"
					// itemname=\"江西赣州B3F-2013片烟\" itemcode=\"160222\"
					// locationcode=\"150515\" locationname=\"DF150515\"
					// ableNum=\"6\" spec=\"200\" weightAble=\"1200\"
					// weightFact=\"1200\" numberFact=\"6\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"0da3c7eb-2a2f-411c-85f9-262111ec3d84\"
					// TaskNumber=\"WRHB1610211051\"
					// detailId=\"8a82e784-7a49-46d3-a219-4401021313c9\"
					// itemname=\"贵州遵义务川C3L-2014片烟\" itemcode=\"120238\"
					// locationcode=\"150404\" locationname=\"DF150404\"
					// ableNum=\"11\" spec=\"200\" weightAble=\"2200\"
					// weightFact=\"2200\" numberFact=\"11\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"0da3c7eb-2a2f-411c-85f9-262111ec3d84\"
					// TaskNumber=\"WRHB1610211051\"
					// detailId=\"3fdad33d-233f-4f77-98e5-7bc61246ae5b\"
					// itemname=\"贵州遵义务川C3L-2014片烟\" itemcode=\"120238\"
					// locationcode=\"150406\" locationname=\"DF150406\"
					// ableNum=\"11\" spec=\"200\" weightAble=\"2200\"
					// weightFact=\"2200\" numberFact=\"11\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"0da3c7eb-2a2f-411c-85f9-262111ec3d84\"
					// TaskNumber=\"WRHB1610211051\"
					// detailId=\"3306089a-6ea3-405f-b9ae-3998454cacad\"
					// itemname=\"贵州遵义务川C2F1-2013T片烟\" itemcode=\"120211\"
					// locationcode=\"150513\" locationname=\"DF150513\"
					// ableNum=\"8\" spec=\"200\" weightAble=\"1600\"
					// weightFact=\"1600\" numberFact=\"8\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"0da3c7eb-2a2f-411c-85f9-262111ec3d84\"
					// TaskNumber=\"WRHB1610211051\"
					// detailId=\"f9abc17c-cdca-4193-b3eb-631f20cd3973\"
					// itemname=\"四川凉山C4F-2013片烟\" itemcode=\"140059\"
					// locationcode=\"150506\" locationname=\"DF150506\"
					// ableNum=\"11\" spec=\"200\" weightAble=\"2200\"
					// weightFact=\"2200\" numberFact=\"11\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"0da3c7eb-2a2f-411c-85f9-262111ec3d84\"
					// TaskNumber=\"WRHB1610211051\"
					// detailId=\"9a7b405d-0db0-469b-96c7-5fbe87181ac4\"
					// itemname=\"四川凉山C4F-2013片烟\" itemcode=\"140059\"
					// locationcode=\"150512\" locationname=\"DF150512\"
					// ableNum=\"7\" spec=\"200\" weightAble=\"1400\"
					// weightFact=\"1400\" numberFact=\"7\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"0da3c7eb-2a2f-411c-85f9-262111ec3d84\"
					// TaskNumber=\"WRHB1610211051\"
					// detailId=\"d43f23e6-3fa1-43de-a7cc-2dc7a8ab9248\"
					// itemname=\"福建三明尤溪C3F-2015M片烟\" itemcode=\"100581\"
					// locationcode=\"150505\" locationname=\"DF150505\"
					// ableNum=\"11\" spec=\"200\" weightAble=\"2200\"
					// weightFact=\"2200\" numberFact=\"11\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"0da3c7eb-2a2f-411c-85f9-262111ec3d84\"
					// TaskNumber=\"WRHB1610211051\"
					// detailId=\"2c3ba3e7-c1ca-4a1f-852c-6b0fda1ae216\"
					// itemname=\"福建三明尤溪C3F-2015M片烟\" itemcode=\"100581\"
					// locationcode=\"150509\" locationname=\"DF150509\"
					// ableNum=\"10\" spec=\"200\" weightAble=\"2000\"
					// weightFact=\"2000\" numberFact=\"10\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"0da3c7eb-2a2f-411c-85f9-262111ec3d84\"
					// TaskNumber=\"WRHB1610211051\"
					// detailId=\"1ddf7224-5151-4ff8-9f79-1ac8dddab019\"
					// itemname=\"福建三明尤溪C3F-2015M片烟\" itemcode=\"100581\"
					// locationcode=\"150507\" locationname=\"DF150507\"
					// ableNum=\"11\" spec=\"200\" weightAble=\"2200\"
					// weightFact=\"2200\" numberFact=\"11\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"0da3c7eb-2a2f-411c-85f9-262111ec3d84\"
					// TaskNumber=\"WRHB1610211051\"
					// detailId=\"d44f0471-fbfe-48c2-b39f-1a118d13d420\"
					// itemname=\"贵州遵义务川C3FLY-2014T片烟\" itemcode=\"120269\"
					// locationcode=\"150417\" locationname=\"DF150417\"
					// ableNum=\"2\" spec=\"200\" weightAble=\"400\"
					// weightFact=\"400\" numberFact=\"2\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"0da3c7eb-2a2f-411c-85f9-262111ec3d84\"
					// TaskNumber=\"WRHB1610211051\"
					// detailId=\"4e3a0256-bfb6-4475-ac59-0c2df3ef64d4\"
					// itemname=\"贵州遵义务川C3FLY-2014T片烟\" itemcode=\"120269\"
					// locationcode=\"150403\" locationname=\"DF150403\"
					// ableNum=\"11\" spec=\"200\" weightAble=\"2200\"
					// weightFact=\"2200\" numberFact=\"11\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"0da3c7eb-2a2f-411c-85f9-262111ec3d84\"
					// TaskNumber=\"WRHB1610211051\"
					// detailId=\"2b1eb641-8710-4e5e-9b6b-5f7cfaa5d9bd\"
					// itemname=\"山东潍坊C3LZB/13-2014片烟\" itemcode=\"160280\"
					// locationcode=\"150416\" locationname=\"DF150416\"
					// ableNum=\"3\" spec=\"200\" weightAble=\"600\"
					// weightFact=\"600\" numberFact=\"3\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"0da3c7eb-2a2f-411c-85f9-262111ec3d84\"
					// TaskNumber=\"WRHB1610211051\"
					// detailId=\"85eb4d59-58dc-4cd4-9728-824452e77f9e\"
					// itemname=\"山东潍坊C3LZB/13-2014片烟\" itemcode=\"160280\"
					// locationcode=\"150401\" locationname=\"DF150401\"
					// ableNum=\"9\" spec=\"200\" weightAble=\"1800\"
					// weightFact=\"1800\" numberFact=\"9\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"0da3c7eb-2a2f-411c-85f9-262111ec3d84\"
					// TaskNumber=\"WRHB1610211051\"
					// detailId=\"9e3f0326-2c87-4072-ad04-27070a76ce5c\"
					// itemname=\"江西赣州C4X2F-2014片烟\" itemcode=\"160285\"
					// locationcode=\"150409\" locationname=\"DF150409\"
					// ableNum=\"10\" spec=\"200\" weightAble=\"2000\"
					// weightFact=\"2000\" numberFact=\"10\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"0da3c7eb-2a2f-411c-85f9-262111ec3d84\"
					// TaskNumber=\"WRHB1610211051\"
					// detailId=\"15d82382-bb83-4b2e-b8a6-d7510229357c\"
					// itemname=\"福建龙岩永定C4F-2014片烟\" itemcode=\"100489\"
					// locationcode=\"150514\" locationname=\"DF150514\"
					// ableNum=\"6\" spec=\"200\" weightAble=\"1200\"
					// weightFact=\"1200\" numberFact=\"6\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"0da3c7eb-2a2f-411c-85f9-262111ec3d84\"
					// TaskNumber=\"WRHB1610211051\"
					// detailId=\"d2eafcd3-80de-4607-a1c9-5fb4506178e2\"
					// itemname=\"福建三明尤溪C3L-2014片烟\" itemcode=\"100517\"
					// locationcode=\"150516\" locationname=\"DF150516\"
					// ableNum=\"8\" spec=\"200\" weightAble=\"1600\"
					// weightFact=\"1600\" numberFact=\"8\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"0da3c7eb-2a2f-411c-85f9-262111ec3d84\"
					// TaskNumber=\"WRHB1610211051\"
					// detailId=\"414cb21d-9c32-46ca-bada-0c93ad58acb8\"
					// itemname=\"湖南永州C3X2F-2014片烟\" itemcode=\"160298\"
					// locationcode=\"150418\" locationname=\"DF150418\"
					// ableNum=\"7\" spec=\"200\" weightAble=\"1400\"
					// weightFact=\"1400\" numberFact=\"7\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"0da3c7eb-2a2f-411c-85f9-262111ec3d84\"
					// TaskNumber=\"WRHB1610211051\"
					// detailId=\"0edeb012-10a9-4443-9b9c-3c2309d8f9e0\"
					// itemname=\"湖南永州C3X2F-2014片烟\" itemcode=\"160298\"
					// locationcode=\"150412\" locationname=\"DF150412\"
					// ableNum=\"7\" spec=\"200\" weightAble=\"1400\"
					// weightFact=\"1400\" numberFact=\"7\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"0da3c7eb-2a2f-411c-85f9-262111ec3d84\"
					// TaskNumber=\"WRHB1610211051\"
					// detailId=\"ce88651f-1990-419a-bd9f-55d7dd3c56bb\"
					// itemname=\"湖南永州C3X2F-2014片烟\" itemcode=\"160298\"
					// locationcode=\"150410\" locationname=\"DF150410\"
					// ableNum=\"11\" spec=\"200\" weightAble=\"2200\"
					// weightFact=\"2200\" numberFact=\"11\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"0da3c7eb-2a2f-411c-85f9-262111ec3d84\"
					// TaskNumber=\"WRHB1610211051\"
					// detailId=\"9d8e48e6-5a56-48cc-a74c-047991263575\"
					// itemname=\"福建龙岩C3F-2014片烟\" itemcode=\"100525\"
					// locationcode=\"150417\" locationname=\"DF150417\"
					// ableNum=\"6\" spec=\"200\" weightAble=\"1200\"
					// weightFact=\"1200\" numberFact=\"6\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"0da3c7eb-2a2f-411c-85f9-262111ec3d84\"
					// TaskNumber=\"WRHB1610211051\"
					// detailId=\"ac9bc03f-48c1-4a5c-9017-c054b6d6dcd2\"
					// itemname=\"福建龙岩C3F-2014片烟\" itemcode=\"100525\"
					// locationcode=\"150405\" locationname=\"DF150405\"
					// ableNum=\"11\" spec=\"200\" weightAble=\"2200\"
					// weightFact=\"2200\" numberFact=\"11\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"0da3c7eb-2a2f-411c-85f9-262111ec3d84\"
					// TaskNumber=\"WRHB1610211051\"
					// detailId=\"f0b4a702-bcb9-4aed-ad3c-40774aed4819\"
					// itemname=\"江西吉赣C34F-2015片烟\" itemcode=\"160351\"
					// locationcode=\"150511\" locationname=\"DF150511\"
					// ableNum=\"7\" spec=\"200\" weightAble=\"1400\"
					// weightFact=\"1400\" numberFact=\"7\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"0da3c7eb-2a2f-411c-85f9-262111ec3d84\"
					// TaskNumber=\"WRHB1610211051\"
					// detailId=\"e8a5bd4a-dee1-4c5c-9276-f2628bbbe290\"
					// itemname=\"江西吉赣C34F-2015片烟\" itemcode=\"160351\"
					// locationcode=\"150501\" locationname=\"DF150501\"
					// ableNum=\"9\" spec=\"200\" weightAble=\"1800\"
					// weightFact=\"1800\" numberFact=\"9\" statu=\"0\"
					// /></Task>" +
					// "<Task TaskNumber=\"WRHB1610211050\"
					// taskId=\"4658cdf0-01e8-405b-a712-6b46a7d6c37f\"
					// isCancel=\"2\" statu=\"0\" carNum=\"\" ComputerCode=\"\"
					// TransportCode=\"\" ableNum=\"335\" floorName=\"15号楼\"
					// weightAble=\"66409\" numberFact=\"335\"
					// weightFact=\"66409\" tasktype=\"RK006\"
					// typename=\"微波水分检测入库任务\" date=\"2016/10/21 0:00:00\"
					// IsUpLoad=\"0\" opername=\"杨嘉兴\"
					// responname=\"杨嘉兴\"><TaskDetail
					// taskId=\"4658cdf0-01e8-405b-a712-6b46a7d6c37f\"
					// TaskNumber=\"WRHB1610211050\"
					// detailId=\"810acf2f-fe93-49b4-85e4-437e5cf48324\"
					// itemname=\"湖北恩施C12P-2010白肋烟片\" itemcode=\"160072\"
					// locationcode=\"150114\" locationname=\"DF150114\"
					// ableNum=\"5\" spec=\"100\" weightAble=\"500\"
					// weightFact=\"500\" numberFact=\"5\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"4658cdf0-01e8-405b-a712-6b46a7d6c37f\"
					// TaskNumber=\"WRHB1610211050\"
					// detailId=\"536ae62c-8fe7-4ba5-b484-1486d91a185a\"
					// itemname=\"四川凉山越西X3F-2012片烟\" itemcode=\"140051\"
					// locationcode=\"150310\" locationname=\"DF150310\"
					// ableNum=\"9\" spec=\"200\" weightAble=\"1800\"
					// weightFact=\"1800\" numberFact=\"9\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"4658cdf0-01e8-405b-a712-6b46a7d6c37f\"
					// TaskNumber=\"WRHB1610211050\"
					// detailId=\"4e035634-d22a-451b-a18e-e4d3654a711c\"
					// itemname=\"四川凉山越西X3F-2012片烟\" itemcode=\"140051\"
					// locationcode=\"150312\" locationname=\"DF150312\"
					// ableNum=\"7\" spec=\"200\" weightAble=\"1400\"
					// weightFact=\"1400\" numberFact=\"7\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"4658cdf0-01e8-405b-a712-6b46a7d6c37f\"
					// TaskNumber=\"WRHB1610211050\"
					// detailId=\"c586d7f5-e191-4e10-9bd4-991a2e318d71\"
					// itemname=\"福建龙岩永定B3C4F-2012FM片烟\" itemcode=\"100318\"
					// locationcode=\"150116\" locationname=\"DF150116\"
					// ableNum=\"1\" spec=\"200\" weightAble=\"200\"
					// weightFact=\"200\" numberFact=\"1\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"4658cdf0-01e8-405b-a712-6b46a7d6c37f\"
					// TaskNumber=\"WRHB1610211050\"
					// detailId=\"b8ebb604-e938-4004-86fc-7b92bd3dfe70\"
					// itemname=\"江西吉安C34X2F-2012片烟\" itemcode=\"160166\"
					// locationcode=\"150213\" locationname=\"DF150213\"
					// ableNum=\"7\" spec=\"200\" weightAble=\"1400\"
					// weightFact=\"1400\" numberFact=\"7\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"4658cdf0-01e8-405b-a712-6b46a7d6c37f\"
					// TaskNumber=\"WRHB1610211050\"
					// detailId=\"c01a3f39-404a-41eb-9686-69cbf0f0f1c6\"
					// itemname=\"江西吉安C34X2F-2012片烟\" itemcode=\"160166\"
					// locationcode=\"150201\" locationname=\"DF150201\"
					// ableNum=\"6\" spec=\"200\" weightAble=\"1200\"
					// weightFact=\"1200\" numberFact=\"6\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"4658cdf0-01e8-405b-a712-6b46a7d6c37f\"
					// TaskNumber=\"WRHB1610211050\"
					// detailId=\"b939e630-142d-4a5f-8555-a8883d8422f4\"
					// itemname=\"云南楚雄南华C2F-2012T片烟\" itemcode=\"110409\"
					// locationcode=\"150112\" locationname=\"DF150112\"
					// ableNum=\"1\" spec=\"200\" weightAble=\"200\"
					// weightFact=\"200\" numberFact=\"1\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"4658cdf0-01e8-405b-a712-6b46a7d6c37f\"
					// TaskNumber=\"WRHB1610211050\"
					// detailId=\"9e7b9f04-6a1e-40bd-9f07-30f6dc1bba2e\"
					// itemname=\"江西吉安BX3F-2013片烟\" itemcode=\"160245\"
					// locationcode=\"150304\" locationname=\"DF150304\"
					// ableNum=\"11\" spec=\"200\" weightAble=\"2200\"
					// weightFact=\"2200\" numberFact=\"11\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"4658cdf0-01e8-405b-a712-6b46a7d6c37f\"
					// TaskNumber=\"WRHB1610211050\"
					// detailId=\"648e07d1-44f1-45e4-bd49-a83b4b8150c7\"
					// itemname=\"江西吉安BX3F-2013片烟\" itemcode=\"160245\"
					// locationcode=\"150302\" locationname=\"DF150302\"
					// ableNum=\"6\" spec=\"200\" weightAble=\"1200\"
					// weightFact=\"1200\" numberFact=\"6\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"4658cdf0-01e8-405b-a712-6b46a7d6c37f\"
					// TaskNumber=\"WRHB1610211050\"
					// detailId=\"ccd4eecd-4023-47ae-9dbf-5d3453cf28ae\"
					// itemname=\"贵州铜仁C4F-2014片烟\" itemcode=\"120248\"
					// locationcode=\"150217\" locationname=\"DF150217\"
					// ableNum=\"8\" spec=\"200\" weightAble=\"1600\"
					// weightFact=\"1600\" numberFact=\"8\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"4658cdf0-01e8-405b-a712-6b46a7d6c37f\"
					// TaskNumber=\"WRHB1610211050\"
					// detailId=\"95a003a4-3cfa-4f03-9047-de1d8ef6a493\"
					// itemname=\"贵州铜仁C4F-2014片烟\" itemcode=\"120248\"
					// locationcode=\"150215\" locationname=\"DF150215\"
					// ableNum=\"8\" spec=\"200\" weightAble=\"1600\"
					// weightFact=\"1600\" numberFact=\"8\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"4658cdf0-01e8-405b-a712-6b46a7d6c37f\"
					// TaskNumber=\"WRHB1610211050\"
					// detailId=\"d90ad064-17af-458c-b61a-4e9bd9c9df24\"
					// itemname=\"贵州铜仁C4F-2014片烟\" itemcode=\"120248\"
					// locationcode=\"150203\" locationname=\"DF150203\"
					// ableNum=\"11\" spec=\"200\" weightAble=\"2200\"
					// weightFact=\"2200\" numberFact=\"11\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"4658cdf0-01e8-405b-a712-6b46a7d6c37f\"
					// TaskNumber=\"WRHB1610211050\"
					// detailId=\"09cb64f7-9b5c-4491-ad36-5991d7766f41\"
					// itemname=\"贵州铜仁C4F-2014片烟\" itemcode=\"120248\"
					// locationcode=\"150205\" locationname=\"DF150205\"
					// ableNum=\"11\" spec=\"200\" weightAble=\"2200\"
					// weightFact=\"2200\" numberFact=\"11\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"4658cdf0-01e8-405b-a712-6b46a7d6c37f\"
					// TaskNumber=\"WRHB1610211050\"
					// detailId=\"046bd9d5-a3c2-4fc9-af17-f90655c99c22\"
					// itemname=\"福建南平B2C3X2F-2014片烟\" itemcode=\"100547\"
					// locationcode=\"150116\" locationname=\"DF150116\"
					// ableNum=\"1\" spec=\"200\" weightAble=\"109\"
					// weightFact=\"109\" numberFact=\"1\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"4658cdf0-01e8-405b-a712-6b46a7d6c37f\"
					// TaskNumber=\"WRHB1610211050\"
					// detailId=\"b14c4b65-a7c6-4a1e-92a4-fbac7e50371d\"
					// itemname=\"福建南平B2C3X2F-2014片烟\" itemcode=\"100547\"
					// locationcode=\"150116\" locationname=\"DF150116\"
					// ableNum=\"1\" spec=\"200\" weightAble=\"200\"
					// weightFact=\"200\" numberFact=\"1\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"4658cdf0-01e8-405b-a712-6b46a7d6c37f\"
					// TaskNumber=\"WRHB1610211050\"
					// detailId=\"df568718-2488-4ffa-89a0-f506e8d494db\"
					// itemname=\"陕西安康旬阳X3F-2014片烟\" itemcode=\"150078\"
					// locationcode=\"150113\" locationname=\"DF150113\"
					// ableNum=\"7\" spec=\"200\" weightAble=\"1400\"
					// weightFact=\"1400\" numberFact=\"7\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"4658cdf0-01e8-405b-a712-6b46a7d6c37f\"
					// TaskNumber=\"WRHB1610211050\"
					// detailId=\"b8b19644-c90c-4bd4-bd21-187ca3edc7c7\"
					// itemname=\"贵州遵义务川C3FLY-2014X片烟\" itemcode=\"120265\"
					// locationcode=\"150211\" locationname=\"DF150211\"
					// ableNum=\"7\" spec=\"200\" weightAble=\"1400\"
					// weightFact=\"1400\" numberFact=\"7\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"4658cdf0-01e8-405b-a712-6b46a7d6c37f\"
					// TaskNumber=\"WRHB1610211050\"
					// detailId=\"f54b42b5-15f3-4b19-8501-4b03151bdc9e\"
					// itemname=\"贵州遵义务川C3FLY-2014X片烟\" itemcode=\"120265\"
					// locationcode=\"150209\" locationname=\"DF150209\"
					// ableNum=\"11\" spec=\"200\" weightAble=\"2200\"
					// weightFact=\"2200\" numberFact=\"11\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"4658cdf0-01e8-405b-a712-6b46a7d6c37f\"
					// TaskNumber=\"WRHB1610211050\"
					// detailId=\"25856b08-1219-42be-85f6-51003b3193d0\"
					// itemname=\"贵州遵义务川C3FLY-2014X片烟\" itemcode=\"120265\"
					// locationcode=\"150207\" locationname=\"DF150207\"
					// ableNum=\"11\" spec=\"200\" weightAble=\"2200\"
					// weightFact=\"2200\" numberFact=\"11\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"4658cdf0-01e8-405b-a712-6b46a7d6c37f\"
					// TaskNumber=\"WRHB1610211050\"
					// detailId=\"7831c0e8-eaf3-459d-9146-7388eda31087\"
					// itemname=\"江西赣州B2F-2014片烟\" itemcode=\"160286\"
					// locationcode=\"150313\" locationname=\"DF150313\"
					// ableNum=\"8\" spec=\"200\" weightAble=\"1600\"
					// weightFact=\"1600\" numberFact=\"8\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"4658cdf0-01e8-405b-a712-6b46a7d6c37f\"
					// TaskNumber=\"WRHB1610211050\"
					// detailId=\"87ce78f3-c782-42d8-803c-99619630fac9\"
					// itemname=\"江西赣州B2F-2014片烟\" itemcode=\"160286\"
					// locationcode=\"150303\" locationname=\"DF150303\"
					// ableNum=\"11\" spec=\"200\" weightAble=\"2200\"
					// weightFact=\"2200\" numberFact=\"11\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"4658cdf0-01e8-405b-a712-6b46a7d6c37f\"
					// TaskNumber=\"WRHB1610211050\"
					// detailId=\"6493e0eb-fea1-4673-9a96-81baaba946af\"
					// itemname=\"江西赣州B2F-2013片烟\" itemcode=\"160220\"
					// locationcode=\"150307\" locationname=\"DF150307\"
					// ableNum=\"9\" spec=\"200\" weightAble=\"1800\"
					// weightFact=\"1800\" numberFact=\"9\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"4658cdf0-01e8-405b-a712-6b46a7d6c37f\"
					// TaskNumber=\"WRHB1610211050\"
					// detailId=\"7cb77e12-2945-4184-8a22-eba16300dee1\"
					// itemname=\"贵州铜仁C3L-2013片烟\" itemcode=\"120189\"
					// locationcode=\"150318\" locationname=\"DF150318\"
					// ableNum=\"6\" spec=\"200\" weightAble=\"1200\"
					// weightFact=\"1200\" numberFact=\"6\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"4658cdf0-01e8-405b-a712-6b46a7d6c37f\"
					// TaskNumber=\"WRHB1610211050\"
					// detailId=\"403323a6-e150-485e-9703-1a118b3aa377\"
					// itemname=\"贵州铜仁C3L-2013片烟\" itemcode=\"120189\"
					// locationcode=\"150316\" locationname=\"DF150316\"
					// ableNum=\"8\" spec=\"200\" weightAble=\"1600\"
					// weightFact=\"1600\" numberFact=\"8\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"4658cdf0-01e8-405b-a712-6b46a7d6c37f\"
					// TaskNumber=\"WRHB1610211050\"
					// detailId=\"3f54571e-4775-41ad-8f78-81b0c491a0f0\"
					// itemname=\"贵州铜仁C3L-2013片烟\" itemcode=\"120189\"
					// locationcode=\"150314\" locationname=\"DF150314\"
					// ableNum=\"7\" spec=\"200\" weightAble=\"1400\"
					// weightFact=\"1400\" numberFact=\"7\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"4658cdf0-01e8-405b-a712-6b46a7d6c37f\"
					// TaskNumber=\"WRHB1610211050\"
					// detailId=\"aecbc9c7-8a66-4c0d-9822-2e3116fb98dc\"
					// itemname=\"贵州黔南长顺X2F-2013片烟\" itemcode=\"120195\"
					// locationcode=\"150210\" locationname=\"DF150210\"
					// ableNum=\"7\" spec=\"200\" weightAble=\"1400\"
					// weightFact=\"1400\" numberFact=\"7\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"4658cdf0-01e8-405b-a712-6b46a7d6c37f\"
					// TaskNumber=\"WRHB1610211050\"
					// detailId=\"2ada2188-cd1b-4c44-a8d6-492f23b90e50\"
					// itemname=\"云南文山麻栗坡C3F-2013片烟\" itemcode=\"110493\"
					// locationcode=\"150206\" locationname=\"DF150206\"
					// ableNum=\"11\" spec=\"200\" weightAble=\"2200\"
					// weightFact=\"2200\" numberFact=\"11\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"4658cdf0-01e8-405b-a712-6b46a7d6c37f\"
					// TaskNumber=\"WRHB1610211050\"
					// detailId=\"792cc341-b909-4582-978e-090c57410612\"
					// itemname=\"云南文山麻栗坡C3F-2013片烟\" itemcode=\"110493\"
					// locationcode=\"150204\" locationname=\"DF150204\"
					// ableNum=\"11\" spec=\"200\" weightAble=\"2200\"
					// weightFact=\"2200\" numberFact=\"11\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"4658cdf0-01e8-405b-a712-6b46a7d6c37f\"
					// TaskNumber=\"WRHB1610211050\"
					// detailId=\"f49d3790-64d4-4467-8a6b-d09c2d73a8fb\"
					// itemname=\"云南文山麻栗坡C3F-2013片烟\" itemcode=\"110493\"
					// locationcode=\"150202\" locationname=\"DF150202\"
					// ableNum=\"8\" spec=\"200\" weightAble=\"1600\"
					// weightFact=\"1600\" numberFact=\"8\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"4658cdf0-01e8-405b-a712-6b46a7d6c37f\"
					// TaskNumber=\"WRHB1610211050\"
					// detailId=\"a9491b87-c876-42b9-b5cd-d575f9082a48\"
					// itemname=\"贵州遵义务川C3F1-2013X片烟\" itemcode=\"120210\"
					// locationcode=\"150315\" locationname=\"DF150315\"
					// ableNum=\"5\" spec=\"200\" weightAble=\"1000\"
					// weightFact=\"1000\" numberFact=\"5\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"4658cdf0-01e8-405b-a712-6b46a7d6c37f\"
					// TaskNumber=\"WRHB1610211050\"
					// detailId=\"8bef2721-5c63-424b-9f03-ec4283496593\"
					// itemname=\"贵州遵义务川C3F1-2013X片烟\" itemcode=\"120210\"
					// locationcode=\"150317\" locationname=\"DF150317\"
					// ableNum=\"8\" spec=\"200\" weightAble=\"1600\"
					// weightFact=\"1600\" numberFact=\"8\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"4658cdf0-01e8-405b-a712-6b46a7d6c37f\"
					// TaskNumber=\"WRHB1610211050\"
					// detailId=\"263e8bfb-0490-48bd-bada-6b59efa5b3e0\"
					// itemname=\"贵州遵义务川C3F1-2013X片烟\" itemcode=\"120210\"
					// locationcode=\"150309\" locationname=\"DF150309\"
					// ableNum=\"9\" spec=\"200\" weightAble=\"1800\"
					// weightFact=\"1800\" numberFact=\"9\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"4658cdf0-01e8-405b-a712-6b46a7d6c37f\"
					// TaskNumber=\"WRHB1610211050\"
					// detailId=\"d72ed0e4-f2f2-4ee5-a991-acbb75f5c89b\"
					// itemname=\"四川凉山会理C3F-2013T片烟\" itemcode=\"140057\"
					// locationcode=\"150214\" locationname=\"DF150214\"
					// ableNum=\"7\" spec=\"200\" weightAble=\"1400\"
					// weightFact=\"1400\" numberFact=\"7\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"4658cdf0-01e8-405b-a712-6b46a7d6c37f\"
					// TaskNumber=\"WRHB1610211050\"
					// detailId=\"ed1f4c4b-9d10-424a-8275-d96aa0ac9304\"
					// itemname=\"四川凉山会理C3F-2013T片烟\" itemcode=\"140057\"
					// locationcode=\"150212\" locationname=\"DF150212\"
					// ableNum=\"7\" spec=\"200\" weightAble=\"1400\"
					// weightFact=\"1400\" numberFact=\"7\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"4658cdf0-01e8-405b-a712-6b46a7d6c37f\"
					// TaskNumber=\"WRHB1610211050\"
					// detailId=\"1643c16d-5c98-42d7-a9bd-cfad8709bd4e\"
					// itemname=\"津巴布韦L1OFT/2013片烟\" itemcode=\"170153\"
					// locationcode=\"150116\" locationname=\"DF150116\"
					// ableNum=\"5\" spec=\"200\" weightAble=\"1000\"
					// weightFact=\"1000\" numberFact=\"5\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"4658cdf0-01e8-405b-a712-6b46a7d6c37f\"
					// TaskNumber=\"WRHB1610211050\"
					// detailId=\"daa40e81-b921-4894-8561-8c7f1dae1921\"
					// itemname=\"江西C4X2FZB-2013片烟\" itemcode=\"160263\"
					// locationcode=\"150305\" locationname=\"DF150305\"
					// ableNum=\"10\" spec=\"200\" weightAble=\"2000\"
					// weightFact=\"2000\" numberFact=\"10\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"4658cdf0-01e8-405b-a712-6b46a7d6c37f\"
					// TaskNumber=\"WRHB1610211050\"
					// detailId=\"3ca40e2f-40a3-4d8a-ae42-3bba2049c9f5\"
					// itemname=\"江西C4X2FZB-2013片烟\" itemcode=\"160263\"
					// locationcode=\"150301\" locationname=\"DF150301\"
					// ableNum=\"9\" spec=\"200\" weightAble=\"1800\"
					// weightFact=\"1800\" numberFact=\"9\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"4658cdf0-01e8-405b-a712-6b46a7d6c37f\"
					// TaskNumber=\"WRHB1610211050\"
					// detailId=\"1c4a6b52-092b-4104-9d38-6d06da04173e\"
					// itemname=\"贵州铜仁C4F-2013片烟\" itemcode=\"120219\"
					// locationcode=\"150218\" locationname=\"DF150218\"
					// ableNum=\"8\" spec=\"200\" weightAble=\"1600\"
					// weightFact=\"1600\" numberFact=\"8\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"4658cdf0-01e8-405b-a712-6b46a7d6c37f\"
					// TaskNumber=\"WRHB1610211050\"
					// detailId=\"48a55265-c6df-46f7-b8af-d681f1afa814\"
					// itemname=\"贵州铜仁C4F-2013片烟\" itemcode=\"120219\"
					// locationcode=\"150216\" locationname=\"DF150216\"
					// ableNum=\"8\" spec=\"200\" weightAble=\"1600\"
					// weightFact=\"1600\" numberFact=\"8\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"4658cdf0-01e8-405b-a712-6b46a7d6c37f\"
					// TaskNumber=\"WRHB1610211050\"
					// detailId=\"d3f13711-f3f6-49f9-b559-fd3f4f65ec03\"
					// itemname=\"贵州铜仁C4F-2013片烟\" itemcode=\"120219\"
					// locationcode=\"150208\" locationname=\"DF150208\"
					// ableNum=\"11\" spec=\"200\" weightAble=\"2200\"
					// weightFact=\"2200\" numberFact=\"11\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"4658cdf0-01e8-405b-a712-6b46a7d6c37f\"
					// TaskNumber=\"WRHB1610211050\"
					// detailId=\"5fcdb17d-3695-4fe9-8b5a-c57c57b99abb\"
					// itemname=\"陕西安康旬阳B2F-2013片烟\" itemcode=\"150071\"
					// locationcode=\"150115\" locationname=\"DF150115\"
					// ableNum=\"8\" spec=\"200\" weightAble=\"1600\"
					// weightFact=\"1600\" numberFact=\"8\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"4658cdf0-01e8-405b-a712-6b46a7d6c37f\"
					// TaskNumber=\"WRHB1610211050\"
					// detailId=\"9e8be931-f7ab-413b-9a24-8a5df81c8660\"
					// itemname=\"福建三明尤溪C3L-2014片烟\" itemcode=\"100517\"
					// locationcode=\"150311\" locationname=\"DF150311\"
					// ableNum=\"4\" spec=\"200\" weightAble=\"800\"
					// weightFact=\"800\" numberFact=\"4\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"4658cdf0-01e8-405b-a712-6b46a7d6c37f\"
					// TaskNumber=\"WRHB1610211050\"
					// detailId=\"8c3ff497-0e7a-4258-b677-bc14ae40bc3f\"
					// itemname=\"山东潍坊C3FZB/13-2014片烟\" itemcode=\"160279\"
					// locationcode=\"150308\" locationname=\"DF150308\"
					// ableNum=\"11\" spec=\"200\" weightAble=\"2200\"
					// weightFact=\"2200\" numberFact=\"11\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"4658cdf0-01e8-405b-a712-6b46a7d6c37f\"
					// TaskNumber=\"WRHB1610211050\"
					// detailId=\"1e4bb745-3d9d-42cb-9d16-cad91b54add2\"
					// itemname=\"山东潍坊C3FZB/13-2014片烟\" itemcode=\"160279\"
					// locationcode=\"150306\" locationname=\"DF150306\"
					// ableNum=\"10\" spec=\"200\" weightAble=\"2000\"
					// weightFact=\"2000\" numberFact=\"10\" statu=\"0\"
					// /></Task>" +
					// "<Task TaskNumber=\"WRHB1610201046\"
					// taskId=\"a78fb7dd-2da4-4d77-954d-e25e34370146\"
					// isCancel=\"2\" statu=\"0\" carNum=\"\" ComputerCode=\"\"
					// TransportCode=\"\" ableNum=\"63\" floorName=\"11号楼\"
					// weightAble=\"12600\" numberFact=\"63\"
					// weightFact=\"12600\" tasktype=\"RK006\"
					// typename=\"微波水分检测入库任务\" date=\"2016/10/20 0:00:00\"
					// IsUpLoad=\"0\" opername=\"李旺\"
					// responname=\"李旺\"><TaskDetail
					// taskId=\"a78fb7dd-2da4-4d77-954d-e25e34370146\"
					// TaskNumber=\"WRHB1610201046\"
					// detailId=\"169c0778-7767-4d8a-b99a-2ca60b9e07f6\"
					// itemname=\"福建南平政和B3F-2015片烟\" itemcode=\"100595\"
					// locationcode=\"110316\" locationname=\"DF110316\"
					// ableNum=\"5\" spec=\"200\" weightAble=\"1000\"
					// weightFact=\"1000\" numberFact=\"5\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"a78fb7dd-2da4-4d77-954d-e25e34370146\"
					// TaskNumber=\"WRHB1610201046\"
					// detailId=\"f14ded22-bd15-4438-859d-8186234d7cf3\"
					// itemname=\"福建南平B2C3X2F-2014片烟\" itemcode=\"100547\"
					// locationcode=\"110316\" locationname=\"DF110316\"
					// ableNum=\"4\" spec=\"200\" weightAble=\"800\"
					// weightFact=\"800\" numberFact=\"4\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"a78fb7dd-2da4-4d77-954d-e25e34370146\"
					// TaskNumber=\"WRHB1610201046\"
					// detailId=\"a11a2b77-98ad-4ea6-9d3c-0bf95dc1c398\"
					// itemname=\"福建南平政和C23F-2014X片烟\" itemcode=\"100554\"
					// locationcode=\"110218\" locationname=\"DF110218\"
					// ableNum=\"7\" spec=\"200\" weightAble=\"1400\"
					// weightFact=\"1400\" numberFact=\"7\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"a78fb7dd-2da4-4d77-954d-e25e34370146\"
					// TaskNumber=\"WRHB1610201046\"
					// detailId=\"9c2d47ee-03bf-4c15-a963-48ca4895f800\"
					// itemname=\"山东潍坊B3F-2014片烟\" itemcode=\"160309\"
					// locationcode=\"110115\" locationname=\"DF110115\"
					// ableNum=\"3\" spec=\"200\" weightAble=\"600\"
					// weightFact=\"600\" numberFact=\"3\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"a78fb7dd-2da4-4d77-954d-e25e34370146\"
					// TaskNumber=\"WRHB1610201046\"
					// detailId=\"4625623e-6cf8-4586-9309-2ce9c3842ecd\"
					// itemname=\"江西吉安B2F-2013片烟\" itemcode=\"160244\"
					// locationcode=\"110116\" locationname=\"DF110116\"
					// ableNum=\"3\" spec=\"200\" weightAble=\"600\"
					// weightFact=\"600\" numberFact=\"3\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"a78fb7dd-2da4-4d77-954d-e25e34370146\"
					// TaskNumber=\"WRHB1610201046\"
					// detailId=\"1872ae2e-bf3e-49fc-b74b-bba84246062b\"
					// itemname=\"贵州遵义务川C3F-2013X片烟\" itemcode=\"120201\"
					// locationcode=\"110312\" locationname=\"DF110312\"
					// ableNum=\"2\" spec=\"200\" weightAble=\"400\"
					// weightFact=\"400\" numberFact=\"2\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"a78fb7dd-2da4-4d77-954d-e25e34370146\"
					// TaskNumber=\"WRHB1610201046\"
					// detailId=\"321d87e8-4251-4e50-9ca1-53afa6e19c70\"
					// itemname=\"陕西安康旬阳C34LF-2013片烟\" itemcode=\"150068\"
					// locationcode=\"110318\" locationname=\"DF110318\"
					// ableNum=\"8\" spec=\"200\" weightAble=\"1600\"
					// weightFact=\"1600\" numberFact=\"8\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"a78fb7dd-2da4-4d77-954d-e25e34370146\"
					// TaskNumber=\"WRHB1610201046\"
					// detailId=\"e842b1df-f953-4242-ac22-92e112296ad8\"
					// itemname=\"陕西安康旬阳C3F-2013X片烟\" itemcode=\"150067\"
					// locationcode=\"110105\" locationname=\"DF110105\"
					// ableNum=\"5\" spec=\"200\" weightAble=\"1000\"
					// weightFact=\"1000\" numberFact=\"5\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"a78fb7dd-2da4-4d77-954d-e25e34370146\"
					// TaskNumber=\"WRHB1610201046\"
					// detailId=\"1c06e41c-1173-46e6-9e33-7621e7c5ecb1\"
					// itemname=\"贵州遵义务川C3F1-2013X片烟\" itemcode=\"120210\"
					// locationcode=\"110308\" locationname=\"DF110308\"
					// ableNum=\"7\" spec=\"200\" weightAble=\"1400\"
					// weightFact=\"1400\" numberFact=\"7\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"a78fb7dd-2da4-4d77-954d-e25e34370146\"
					// TaskNumber=\"WRHB1610201046\"
					// detailId=\"23364f93-8442-41f2-89d2-9bf4a6ba13ed\"
					// itemname=\"山东临沂沂南X3F-2013片烟\" itemcode=\"160256\"
					// locationcode=\"110310\" locationname=\"DF110310\"
					// ableNum=\"6\" spec=\"200\" weightAble=\"1200\"
					// weightFact=\"1200\" numberFact=\"6\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"a78fb7dd-2da4-4d77-954d-e25e34370146\"
					// TaskNumber=\"WRHB1610201046\"
					// detailId=\"29a369c5-94f6-4866-9db0-54123cf6bdce\"
					// itemname=\"河南洛阳B2F-2013片烟\" itemcode=\"130103\"
					// locationcode=\"110105\" locationname=\"DF110105\"
					// ableNum=\"5\" spec=\"200\" weightAble=\"1000\"
					// weightFact=\"1000\" numberFact=\"5\" statu=\"0\"
					// /><TaskDetail
					// taskId=\"a78fb7dd-2da4-4d77-954d-e25e34370146\"
					// TaskNumber=\"WRHB1610201046\"
					// detailId=\"8d555165-8f2e-4908-85dd-e110d77feac9\"
					// itemname=\"福建龙岩永定C2FA-2014M片烟\" itemcode=\"100483\"
					// locationcode=\"110302\" locationname=\"DF110302\"
					// ableNum=\"8\" spec=\"200\" weightAble=\"1600\"
					// weightFact=\"1600\" numberFact=\"8\" statu=\"0\"
					// /></Task>" +
					"</Parms></Request>";
			try {
				doc = DocumentHelper.parseText(return_xml);
			} catch (Exception e) {
				result.setResult(false);
				result.setMessage("数据出错！222");
				return result;
			}
			result = InPut(result, doc.getRootElement());
		} else {
			result.setResult(false);
			result.setMessage("服务器返回错误！");
		}
		return result;
	}

	private String[] getImageversions(String[] imageNames) {
		String[] versions = new String[4];
		String[] nodes;
		for (int i = 0; i < 4; i++) {
			nodes = imageNames[i].split("_");
			if (nodes.length == 3) {
				versions[i] = nodes[2];
			} else {
				versions[i] = " ";
			}
		}
		return versions;
	}

	private String getImageName(String attributeValue) {
		String imageName = null;
		String[] nodes;
		nodes = attributeValue.split("/");
		try {
			imageName = nodes[nodes.length - 1].split("\\.")[0];
		} catch (Exception e) {
		}
		if (imageName == null || imageName.length() < 38) {
			imageName = " ";
		}
		return imageName;
	}

}
