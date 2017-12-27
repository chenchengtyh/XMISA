package com.nti56.xmisa.util;

import android.content.Context;
import android.widget.Toast;

public class MyToast {

	private static MyToast mInstance;
	private Context mContext;
	private Toast mToast;

	public static void setInstance(Context context) {
		synchronized (MyToast.class) {
			if (mInstance == null) {
				mInstance = new MyToast(context);
			}
		}
	}

	public static MyToast getInstance() {
		return mInstance;
	}

	public MyToast(Context context) {
		// super(context);
		mContext = context;
	}

	public void show(String ToastText) {
		if (mToast == null) {
			mToast = Toast.makeText(mContext, ToastText, Toast.LENGTH_SHORT);
		} else {
			mToast.setText(ToastText);
			mToast.setDuration(Toast.LENGTH_SHORT);
		}
		mToast.show();
	}

	public void show(String ToastText, int Duration) {
		if (mToast == null) {
			mToast = Toast.makeText(mContext, ToastText, Duration);
		} else {
			mToast.setText(ToastText);
			mToast.setDuration(Duration);
		}
		mToast.show();
	}

}
