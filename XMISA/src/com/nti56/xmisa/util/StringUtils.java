package com.nti56.xmisa.util;


public class StringUtils {

	public final static String qualified = "合格";
	public final static String disqualified = "不合格";

	public final static String none = "无";
	public final static String slight = "轻微";
	public final static String severe = "严重";

	public final static String exist = "有";
	
	/*
	 * 反馈状态
	 */
	public static class IsUpLoad{
		public final static String YES = "已反馈";
		public final static String NO = "未反馈";
	}
	
	
	/*
	 * 撤销状态
	 */
	public static class IsCancel{
		public final static String FINISH = "0";//已撤销
		public final static String WAIT = "1";//待撤销
		public final static String NORMAL = "2";//正常
	}
	
	/*
	 * 任务状态
	 */
	public static class TaskStatu{
		public final static String WAIT = "0";//待执行
		public final static String DOING = "1";//执行中
		public final static String FINISH = "2";//已完成
	}
	
	/*
	 * 任务状态
	 */
	public static class isOK{
		public final static String no = "0";//不合格
		public final static String yes = "1";//合格
	}
	
	/**
	 * 任务类型
	 * @author wyl
	 *
	 */
	public static class TaskType{
		public final static String DEF = "0";//与NC保持一致
	}
	
	public static boolean isNull(String s){
		return s==null||"".equals(s);
	}
	
	public static String notNull(String s){
		if(s==null) return "";
		else return s; 
	}

}
