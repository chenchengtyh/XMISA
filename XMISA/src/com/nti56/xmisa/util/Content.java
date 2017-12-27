package com.nti56.xmisa.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.util.Log;

public class Content {


	public static boolean OffLine = true;
	public static boolean InputSoftOpen = false;

	public static int FTP_PORT = 21;
	public static String FTP_IP = "172.16.20.87";
	public static String FTP_USER = "anonymous";
	public static String FTP_PASSWORD = "hehe";
	public static final String FTP_IMAGE_DIRECTORY = "/XMISA/Images";

	public static String getFtpUrl() {
		return "ftp://" + FTP_IP + ":" + FTP_PORT + FTP_IMAGE_DIRECTORY + "/";
	}

	public static String USER_NAME = "张三";
	public static String USER_ACCOUNT = "123456";
	public static String USER_SYS_CODE = "767565";
	public static String BUILDINGS = "";
	public static String IP = "172.16.20.87:8080";

	public static String getServerUrl(){
		return "http://" + IP + "/AppWebService.asmx/ReceiveDataSWMS";
	}

	public static final String DB_NAME = "xmisa_sql.db";
	public static final String DEFAULT_DRUG = "磷化铝";

//	public static String IP_DEFAULT = "192.168.44.171:8082";
	 public static final String IP_DEFAULT = "172.16.20.87:8080"; // 默认IP地址


	public static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000; // Home键需要自己定义标志

	public static int IMAGE_RATIO = 64;

	public static final int MODEL_EXIT = 1;
	public static final int MODEL_DELETE = 2;

	public static final int DB_VERSION = 1;
	public static final String DB_DATA_TEMP = "temp";// temp
														// 用来区分是INSERT还是UPDATE操作
	public static final String DB_DATA_LOCAL = "local";// local
	public static final String DB_DATA_SERVER = "server";// server
	public static final String DB_NEED_DELETE = "delete";// delete

	public static final int SEND_XML_LOGIN = 1001;
	public static final int SEND_XML_REC_LOAD = 1111;
	public static final int SEND_XML_REC_DELETE = 1112;
	public static final int SEND_XML_REC_COMMIT = 1113;
	public static final int SEND_XML_IPT_LOAD = 1121;
	public static final int SEND_XML_IPT_DELETE = 1122;
	public static final int SEND_XML_IPT_COMMIT = 1123;
	public static final int SEND_XML_OPT_LOAD = 1131;
	public static final int SEND_XML_OPT_DELETE = 1132;
	public static final int SEND_XML_OPT_COMMIT = 1133;
	public static final int SEND_XML_IVT_LOAD = 1141;
	public static final int SEND_XML_IVT_DELETE = 1142;
	public static final int SEND_XML_IVT_COMMIT = 1143;
	public static final int SEND_XML_ASG_LOAD = 1211;
	public static final int SEND_XML_ASG_DELETE = 1212;
	public static final int SEND_XML_ASG_COMMIT = 1213;
	public static final int SEND_XML_EXM_LOAD = 1221;
	public static final int SEND_XML_EXM_DELETE = 1222;
	public static final int SEND_XML_EXM_COMMIT = 1223;
	public static final int SEND_XML_SUR_LOAD = 1311;
	public static final int SEND_XML_SUR_DELETE = 1312;
	public static final int SEND_XML_SUR_COMMIT = 1313;
	public static final int SEND_XML_FMG_LOAD = 1321;
	public static final int SEND_XML_FMG_DELETE = 1322;
	public static final int SEND_XML_FMG_COMMIT = 1323;
	public static final int SEND_XML_PTS_LOAD = 1331;
	public static final int SEND_XML_PTS_DELETE = 1332;
	public static final int SEND_XML_PTS_COMMIT = 1333;
	public static final int SEND_XML_PTL_LOAD = 1341;
	public static final int SEND_XML_PTL_DELETE = 1342;
	public static final int SEND_XML_PTL_COMMIT = 1343;

	// public static final int SEND_XML_RESULT4 = 1001;
	// public static final int SEND_XML_RESULT5 = 1001;

	public static final int BACK_PRESSED = 1002;
	public static final int INPUT_SOFT_ACTION = 1003;
	public static final int JUDGE_EDITTEXT_FOCUS = 1004;
	public static final int HANDLER_TOAST = 1005;

	public static final int BACK_MODEL_NONE = 0;
	public static final int BACK_MODEL_REC_DETAIL = 112;
	public static final int BACK_MODEL_IPT_DETAIL = 122;
	public static final int BACK_MODEL_IPT_THIRD = 123;
	public static final int BACK_MODEL_OPT_DETAIL = 132;
	public static final int BACK_MODEL_OPT_THIRD = 133;
	public static final int BACK_MODEL_IVT_DETAIL = 142;
	public static final int BACK_MODEL_ASG_DETAIL = 212;
	public static final int BACK_MODEL_EXM_DETAIL = 222;
	public static final int BACK_MODEL_SUR_DETAIL = 312;
	public static final int BACK_MODEL_SUR_THIRD = 313;
	public static final int BACK_MODEL_FMG_DETAIL = 322;
	public static final int BACK_MODEL_PTS_DETAIL = 332;
	public static final int BACK_MODEL_PTL_DETAIL = 342;

	public static final int LIST_LOGIN = 888;
	public static final int LIST_RECEIVE = 111;
	public static final int LIST_RECEIVE_DETAIL = 112;
	public static final int LIST_RECEIVE_LOSS = 113;
	public static final int LIST_INPUT = 121;
	public static final int LIST_INPUT_DETAIL = 122;
	public static final int LIST_INPUT_THIRD = 123;
	public static final int LIST_OUTPUT = 131;
	public static final int LIST_OUTPUT_DETAIL = 132;
	public static final int LIST_OUTPUT_THIRD = 133;
	public static final int LIST_INVENTORY = 141;
	public static final int LIST_INVENTORY_DETAIL = 142;
	public static final int LIST_ASSIGN = 211;
	public static final int LIST_EXAMINE = 221;
	public static final int LIST_EXAMINE_DETAIL = 222;
	public static final int LIST_SURVEY = 311;
	public static final int LIST_SURVEY_DETAIL = 312;
	public static final int LIST_SURVEY_THIRD = 313;
	public static final int LIST_FUMIGATE = 321;
	public static final int LIST_FUMIGATE_DETAIL = 322;
	public static final int LIST_PESTS = 331;
	public static final int LIST_PESTS_DETAIL = 332;
	public static final int LIST_PATROL = 341;
	public static final int LIST_PATROL_DETAIL = 342;

	public static final int ACTIVITY_REQUEST_CODE_CAMERA = 100;
	public static final int ACTIVITY_REQUEST_CODE_IMAGE = 101;

	public static final String HTTP_UPLOAD_TRUE = "已反馈";
	public static final String HTTP_UPLOAD_FALSE = "未反馈";

	public static final String HTTP_ASSIGN_TRUE = "已分配";
	public static final String HTTP_ASSIGN_FALSE = "未分配";

	public static final String DETAIL_AVERAGE_VALUE = "平均值";
	
	public static final String FTP_IMAGE_UPLOAD_SUCCESSS = "图片全部上传成功";

	public static final String FTP_CONNECT_SUCCESSS = "ftp连接成功";
	public static final String FTP_CONNECT_FAIL = "FTP服务连接失败";
	public static final String FTP_DISCONNECT_SUCCESS = "ftp断开连接";
	public static final String FTP_FILE_NOTEXISTS = "ftp上文件不存在";

	public static final String FTP_UPLOAD_SUCCESS = "ftp文件上传成功";
	public static final String FTP_UPLOAD_FAIL = "ftp文件上传失败";
	public static final String FTP_UPLOAD_LOADING = "ftp文件正在上传";

	public static final String FTP_DOWN_LOADING = "ftp文件正在下载";
	public static final String FTP_DOWN_SUCCESS = "ftp文件下载成功";
	public static final String FTP_DOWN_FAIL = "ftp文件下载失败";

	public static final String FTP_DELETEFILE_SUCCESS = "ftp文件删除成功";
	public static final String FTP_DELETEFILE_FAIL = "ftp文件删除失败";

	public static String filterNumber(String number) {
		number = number.replaceAll("[^(0-9)]", "");
		return number;
	}

	public static String getBuilding(String building) {
		building = building.replaceAll("[^(0-9)]", "");
		building = (building.length() == 1) ? ("0" + building) : building;
		return building;
	}

	public static int parseInt(String string) {
		int value = 0;
		try {
			value = Integer.parseInt(string);
		} catch (Exception e) {
			value = 0;
		}
		return value;
	}

	public static String Keep2Decimal(String string) {
		try {
			DecimalFormat format = new DecimalFormat("0.00");
			string = format.format(new BigDecimal(string));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return string;
	}

	public static String DoubleToString(double value) {
		String Value = value + "";
		if (Value.endsWith(".0")) {
			Value = Value.replace(".0", "");
		}
		return Value;
	}

	public static double parseDouble(String string) {
		double value = 0;
		try {
			value = Double.parseDouble(string);
		} catch (Exception e) {
			value = 0;
		}
		return value;
	}

	public static String getDate_Now() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
		Date curDate = new Date(System.currentTimeMillis());
		return formatter.format(curDate);
	}

	public static String getDate(String dateTime) {
		if (dateTime == null) {
			return null;
		}
		String[] temp = null;
		temp = dateTime.split("/");
		if (temp.length != 3) {
			temp = dateTime.split("-");
		}
		if (temp.length != 3) {
			return dateTime;
		}
		String Date = "";
		try {
			if (temp[1].length() == 1) {
				temp[1] = "0" + temp[1];
			}
			temp[2] = temp[2].split(" ")[0];
			if (temp[2].length() == 1) {
				temp[2] = "0" + temp[2];
			}
			Date = temp[0] + "/" + temp[1] + "/" + temp[2];
		} catch (Exception e) {
			return dateTime;
		}
		return Date;
	}

	public static String getMeridiem_Now() {
		int hour = 0;
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("HH", Locale.getDefault());
			Date curDate = new Date(System.currentTimeMillis());
			hour = Integer.parseInt(formatter.format(curDate));
		} catch (NumberFormatException e) {
		}
		Log.e("LXM", "Hour = >" + hour + "<");
		if (hour > 11 && hour < 24) {
			return "1";//下午
		}
		return "0";//上午
	}

	public static String getMeridiem(String dateTime) {
		String[] temp = null;
		temp = dateTime.split(":");
		if (temp.length == 2 || temp.length == 3) {
			int hour = 0;
			try {
				String Hour = temp[0].split(" ")[1];
				hour = Integer.parseInt(Hour);
			} catch (NumberFormatException e) {
			}
			if (hour > 11 && hour < 24) {
				return "1";//下午
			}

		}
		return "0";//上午
	}

	public static String getLossWeightType(String string) {
		if (string.equals("丢失")) {
			return "2";
		} else if (string.equals("湿损")) {
			return "1";
		} else {
			return "0";
		}
	}
	//
	// S+本身 W经验，满级93.30W经验
	// S 本身5W经验，满级48.12W经验 拿x2的卡，如果出现2倍，需要含有21.56W经验
	// A+本身2W经验，满级31.44W经验 拿x2的卡，如果出现2倍，需要含有14.72W经验
	// A 本身1W经验，满级14.56W经验 拿x2的卡，如果出现2倍，需要含有6.78W经验

	// A  【爱德华的印象】〖阿波罗的印象〗【吉妮的神灯ⅡⅢ 】

	// A+ 【奶爸】【关羽】【明美】【可儿】【元大佬】【狼蛛】
	// A+ 【厨师高进】【圣诞大小姐】【歌星可儿】【巡逻队狼蛛】【海岸女神荷绾绾】
	// A+ 〖新生小诺的印象ⅠⅡⅢ〗【吉妮的印象】〖吉妮的神灯ⅠⅡⅢ 〗〖黑暗吉妮的印象ⅠⅡⅢ〗

	// S  〖优拉〗〖菲奥娜〗【安然】〖娜娜〗【托雷斯】【杰西卡】〖菲奥娜〗【潘多拉】〖爱德华〗【卢克】【亨利】【莱娅】【安娜】【蓝冰】【印第安公主】〖阿拉丁〗【亚瑟】【安妮】【新生小诺】〖吉妮〗
	// S  〖女神优拉〗〖熊猫娜娜〗〖新娘菲奥娜〗〖摄影家爱德华〗【骑士亨利】【中国小姐安然】【飞行员托雷斯】〖摇滚乐阿波罗〗
	// S  〖吉妮的印象〗

	// S+ 〖优拉〗〖娜娜〗〖菲奥娜〗〖亚瑟〗【安妮】【爱德华】〖亨利〗〖安然〗〖托雷斯〗〖吉妮〗
}
