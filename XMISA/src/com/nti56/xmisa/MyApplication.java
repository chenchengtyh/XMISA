package com.nti56.xmisa;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class MyApplication extends Application {

	public static SharedPreferences mySharedPreferences = null;

	public static Editor editor = null;
}
