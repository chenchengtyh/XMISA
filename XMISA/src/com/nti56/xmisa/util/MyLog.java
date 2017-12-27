package com.nti56.xmisa.util;

import android.util.Log;

public class MyLog {

	private static String mTAG = "NTI";

	public static void d(String TAG, String Function) {
		Log.d(mTAG, TAG + "." + Function + "()");
	}

	public static void e(String TAG, String Function) {
		Log.e(mTAG, TAG + "." + Function + "()");
	}

	public static void i(String TAG, String Function) {
		Log.i(mTAG, TAG + "." + Function + "()");
	}

	public static void v(String TAG, String Function) {
		Log.v(mTAG, TAG + "." + Function + "()");
	}

	public static void w(String TAG, String Function) {
		Log.w(mTAG, TAG + "." + Function + "()");
	}

	public static void d(String TAG, String Function, String Massage) {
		Log.d(mTAG, TAG + "." + Function + "(): " + Massage);
	}

	public static void e(String TAG, String Function, String Massage) {
		Log.e(mTAG, TAG + "." + Function + "(): " + Massage);
	}

	public static void i(String TAG, String Function, String Massage) {
		Log.i(mTAG, TAG + "." + Function + "(): " + Massage);
	}

	public static void v(String TAG, String Function, String Massage) {
		Log.v(mTAG, TAG + "." + Function + "(): " + Massage);
	}

	public static void w(String TAG, String Function, String Massage) {
		Log.w(mTAG, TAG + "." + Function + "(): " + Massage);
	}

}
