package com.nti56.xmisa;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nti56.xmisa.adapter.MyListView;
import com.nti56.xmisa.adapter.MyListViewAdapter;
import com.nti56.xmisa.adapter.MyListViewAdapter.ListViewAdapterCallBack;
import com.nti56.xmisa.bean.AdapterBean;
import com.nti56.xmisa.bean.PostResult;
import com.nti56.xmisa.util.Content;
import com.nti56.xmisa.util.HttpUtil;
import com.nti56.xmisa.util.MyLog;
import com.nti56.xmisa.util.MyToast;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;

public class LoginActivity extends Activity implements OnClickListener {

	private float startX, startY;
	private int mMode;
	private int state;
	private final static int NONE = 0;
	private final static int VERTICAL = 5;// 垂直移动
	private final static int HORIZONTAL = 10;// 水平移动
	// private final static int HORIZONTAL_LEFT = 10;// 水平移动
	// private final static int HORIZONTAL_RIGHT = 15;// 水平移动

	private final static int CLOSE = 0;// 滑动失败，恢复原样
	private final static int CHANGEING = 1; // 滑动中
	private final static int CHANGED = 2;// 滑动成功，改变布局

	private final static float RATIO = 1.1f;// 实际的padding的距离与界面上偏移距离的比例
	private final static int ContentWidth = 380;// 有效滑动距离

	private EditText inputUser, inputKey, serverIP, serverPort;
	private CheckBox keepKey, autoLogin;
	private Button showUsers, btnEnter, btnSure, btnBack;
	private int backCount, homeCount;
//	private Editor editor;
	private JSONObject dataJson;
	private JSONArray arrayJson;
	private String cur_user, cur_key, user_list;
	private boolean keepKey_state, autoLogin_state;
	private HttpUtil http;
	private MyToast mToast;
	private MyHandler mHandler;
	private String TAG = "LoginActivity";
	private MyListView mListView;
	private MyListViewAdapter mListViewAdapter;
	private ArrayList<AdapterBean> mBeanList = new ArrayList<AdapterBean>();
	private RelativeLayout mLayout1, mLayout2;
	private ProgressDialog proDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		MyLog.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		inputUser = (EditText) findViewById(R.id.et_login_user);
		inputKey = (EditText) findViewById(R.id.et_login_key);
		serverIP = (EditText) findViewById(R.id.et_login_server_ip);
		serverPort = (EditText) findViewById(R.id.et_login_server_port);

		keepKey = (CheckBox) findViewById(R.id.checkbox_keepkey);
		autoLogin = (CheckBox) findViewById(R.id.checkbox_autologin);

		showUsers = (Button) findViewById(R.id.btn_login_users);
		btnEnter = (Button) findViewById(R.id.btn_login_enter);
		btnSure = (Button) findViewById(R.id.btn_login_sure);
		btnBack = (Button) findViewById(R.id.btn_login_back);

		mListView = (MyListView) findViewById(R.id.lv_user);

		mLayout1 = (RelativeLayout) findViewById(R.id.layout_login_input);
		mLayout2 = (RelativeLayout) findViewById(R.id.layout_login_setting);

		showUsers.setOnClickListener(this);
		btnEnter.setOnClickListener(this);
		btnSure.setOnClickListener(this);
		btnBack.setOnClickListener(this);

		mHandler = new MyHandler(this);

		http = HttpUtil.getInstance();
		GetSharedPreferences();
		MyToast.setInstance(getApplicationContext());
		mToast = MyToast.getInstance();
	}

	@Override
	protected void onStart() {
		MyLog.d(TAG, "onStart");
		backCount = 0;
		homeCount = 0;
		this.getWindow().setFlags(Content.FLAG_HOMEKEY_DISPATCHED, Content.FLAG_HOMEKEY_DISPATCHED);
		super.onStart();
	}

	@Override
	public void onClick(View v) {
		backCount = 0;
		homeCount = 0;
		switch (v.getId()) {
		case R.id.btn_login_users:
			if (mListView.getVisibility() != View.VISIBLE) {
				ShowUserList();
			} else {
				HideUserList();
			}
			break;

		case R.id.btn_login_enter:
			if (proDialog == null) {
				proDialog = new ProgressDialog(this);
			}
			proDialog.setTitle("登录中..");
			proDialog.setMessage("正在登录..请稍后....");
			proDialog.setCancelable(false);
			proDialog.setCanceledOnTouchOutside(false);
			proDialog.show();
			HideUserList();
			LoginSystem_Send();
			break;
		case R.id.btn_login_sure: {
			String ip = serverIP.getText().toString();
			String port = serverPort.getText().toString();
			if (ip == null || port == null || ip.split("\\.").length != 4 || port.length() < 2) {
				mToast.show("参数错误，请重新输入！");
			} else {
				Content.IP = ip + ":" + port;
				mToast.show("设置成功！当前服务器IP地址为：" + Content.IP);
				MyApplication.editor.putString("cur_ip", Content.IP);
				MyApplication.editor.commit();
			}
		}
			break;

		case R.id.btn_login_back: {
			mLayout2.setAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_layout_right_hide));
			mLayout2.setVisibility(View.GONE);
			mLayout1.setVisibility(View.VISIBLE);
			mLayout1.setPadding(0, 0, 0, 0);
			mLayout1.setAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_menu_location));
		}
			break;
		default:
			break;
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mMode = NONE;
			startX = event.getX();// 手指按下时记录当前位置
			startY = event.getY();// 手指按下时记录当前位置
			break;

		case MotionEvent.ACTION_MOVE:
			int DifValueX = (int) ((event.getX() - startX) / RATIO);// X轴移动距离
			int DifValueY = (int) ((event.getY() - startY) / RATIO);// Y轴移动距离
			if (mMode == NONE && (Math.abs(DifValueX) > 4 || Math.abs(DifValueY) > 4)) {
				if (Math.abs(DifValueX) > Math.abs(DifValueY)) {
					mMode = HORIZONTAL;// 水平模式
				} else {
					mMode = VERTICAL;// 垂直模式
				}
			}
			if (mMode == HORIZONTAL) {// 当未正在刷新时
				if (DifValueX >= ContentWidth || DifValueX <= -ContentWidth) {// 移动完成
					state = CHANGED;
				} else {
					state = CHANGEING;
				}
				mLayout1.setPadding(DifValueX, 0, 0, 0);
				mLayout2.setPadding(DifValueX, 0, 0, 0);
			}
			break;

		case MotionEvent.ACTION_UP:
			if (state == CHANGED) {
				ChangeViewByState();
				state = CLOSE;
			} else {
				state = CLOSE;
				ChangeViewByState();
			}

			break;

		default:
			break;
		}
		return super.onTouchEvent(event);
	}

	private void ChangeViewByState() {
		switch (state) {
		case CLOSE:// 滑动失败，恢复原样
			mLayout1.setPadding(0, 0, 0, 0);
			mLayout2.setPadding(0, 0, 0, 0);
			break;

		case CHANGED:
			if (mLayout1.getVisibility() == View.VISIBLE) {// 隐藏登录输入框，显示高级设置界面
				mLayout1.setAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_menu_up));
				mLayout1.setVisibility(View.GONE);
				mLayout2.setVisibility(View.VISIBLE);
				mLayout2.setPadding(0, 0, 0, 0);
				String[] server = Content.IP.split(":");
				if (server == null || server.length != 2) {
					serverIP.setText("IP地址为空！");
					serverPort.setText("端口号为空！");
				} else {
					serverIP.setText(server[0]);
					serverPort.setText(server[1]);
				}
				mLayout2.setAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_menu_location));
			} else {// 隐藏高级设置界面，显示登录输入框
				mLayout2.setAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_menu_up));
				mLayout2.setVisibility(View.GONE);
				mLayout1.setVisibility(View.VISIBLE);
				mLayout1.setPadding(0, 0, 0, 0);
				mLayout1.setAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_menu_location));
			}
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_HOME:
			if (homeCount > 0) {
				this.getWindow().clearFlags(Content.FLAG_HOMEKEY_DISPATCHED);
			}
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_HOME:
			if (homeCount == 0) {
				homeCount++;
				mToast.show("再按一次Home键退出应用。");
				return true;
			}
			break;

		case KeyEvent.KEYCODE_BACK:
			if (backCount == 0) {
				backCount++;
				mToast.show("再按一次返回键退出应用。");
				return true;
			}
			break;

		case KeyEvent.KEYCODE_SEARCH:
			return true;

		case KeyEvent.KEYCODE_MENU:
			return true;
		default:
			break;
		}
		return super.onKeyUp(keyCode, event);
	}

	private void InitListViewAdapter() {
		mListViewAdapter = new MyListViewAdapter(this, R.layout.list_login, mBeanList, Content.LIST_LOGIN, listViewAdapterCallBack);
		mListView.setAdapter(mListViewAdapter);
		mListView.setOnItemClickListener(itemClicklistener);
	}

	private void GetSharedPreferences() {
		if (MyApplication.mySharedPreferences == null) {
			MyApplication.mySharedPreferences = getSharedPreferences("mysharepreferences", Activity.MODE_PRIVATE);
		}
		MyApplication.editor = MyApplication.mySharedPreferences.edit();

		Content.IP = MyApplication.mySharedPreferences.getString("cur_ip", Content.IP_DEFAULT);
		cur_user = MyApplication.mySharedPreferences.getString("cur_user", "");
		cur_key = MyApplication.mySharedPreferences.getString("cur_key", "");

		MyLog.d(TAG, "getSharedPreferences", "cur_user = " + cur_user + ", cur_key = " + cur_key);

		user_list = MyApplication.mySharedPreferences.getString("users", "{ \"List\" : [{\"user\" : \"\",\"key\" : \"\"}]}");
		keepKey_state = MyApplication.mySharedPreferences.getBoolean("keep_key", true);
		autoLogin_state = MyApplication.mySharedPreferences.getBoolean("auto_login", true);
		inputUser.setText(cur_user);
		inputKey.setText(cur_key);
		keepKey.setChecked(keepKey_state);
		autoLogin.setChecked(autoLogin_state);
		try {
			dataJson = new JSONObject(user_list);
			arrayJson = dataJson.getJSONArray("List");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private void UpDateJSON(String user, String key, int pos) {
		if (pos < 0) {// 增改
			JSONObject tempJson = null;
			String str = "{\"user\" : \"" + user + "\",\"key\" : \"" + key + "\"}";
			if (arrayJson.length() == 0) {
				try {
					arrayJson.put(0, new JSONObject(str));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				for (int i = 0; i < arrayJson.length(); i++) {
					tempJson = arrayJson.optJSONObject(i);
					try {
						if (user.equals(tempJson.getString("user"))) {// 修改
							arrayJson.put(i, new JSONObject(str));
							break;
						} else if (i == arrayJson.length() - 1) {// 增加
							arrayJson.put(++i, new JSONObject(str));
							break;
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}

				}
			}
			MyApplication.editor.putString("cur_user", user);
			MyApplication.editor.putString("cur_key", key);
			MyApplication.editor.putBoolean("keep_key", keepKey.isChecked());
			MyApplication.editor.putBoolean("auto_login", autoLogin.isChecked());
		} else {// 删除对应记录（ 此时Mode为要操作的list的ID）
			if (pos < arrayJson.length()) {
				try {
					Field valuesField = JSONArray.class.getDeclaredField("values");
					valuesField.setAccessible(true);
					List<Object> values = (List<Object>) valuesField.get(arrayJson);
					values.remove(pos);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		MyApplication.editor.putString("users", dataJson.toString());
		MyApplication.editor.commit();
	}

	private void ShowUserList() {
		if (mListViewAdapter == null) {
			InitListViewAdapter();
		}
		if (mListView.getVisibility() != View.VISIBLE) {
			showUsers.setBackgroundResource(R.drawable.indicator_up);
			mBeanList.clear();
			JSONObject tempJson = null;
			MyLog.d(TAG, "ShowUserList", "arrayJson.length() = " + arrayJson.length());
			for (int i = 0; i < arrayJson.length(); i++) {
				try {
					tempJson = arrayJson.optJSONObject(i);
					AdapterBean bean = new AdapterBean();
					bean.setUser(tempJson.getString("user"));
					bean.setKey(tempJson.getString("key"));
					mBeanList.add(bean);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			mListViewAdapter.notifyDataSetChanged();
			mListView.setVisibility(View.VISIBLE);
		}
	}

	private void HideUserList() {
		if (mListView.getVisibility() != View.GONE) {
			showUsers.setBackgroundResource(R.drawable.indicator_down);
			mBeanList.clear();
			mListViewAdapter.notifyDataSetChanged();
			mListView.setVisibility(View.GONE);
		}
	}

	private void LoginSystem_Send() {
		String user = inputUser.getText().toString();
		String key = inputKey.getText().toString();

		final String xmlString = "<Request><Function>login</Function><Parms><Username>" + user + "</Username><Password>" + key
				+ "</Password></Parms></Request>";

		if (!keepKey.isChecked()) {
			key = "";
		}
		UpDateJSON(user, key, -1);
		Content.USER_ACCOUNT = user;
		// TODO 此处应另起线层进行耗时操作！！！！
		new Thread(new Runnable() {
			@Override
			public void run() {
				PostResult result = http.sendXML(xmlString);
				mHandler.obtainMessage(Content.SEND_XML_LOGIN, result).sendToTarget();
			}
		}).start();
	}

	private void LoginSystem_Rev(PostResult result) {
		if (proDialog != null) {
			proDialog.dismiss();
		}
		if (result.isResult()) {
			Intent intent = new Intent();
			intent.setClass(this, MainActivity.class);
			startActivity(intent);
			finish();
		} else {
			mToast.show(result.getMessage());
		}

	}

	static class MyHandler extends Handler {
		WeakReference<LoginActivity> mActivity;

		MyHandler(LoginActivity activity) {
			mActivity = new WeakReference<LoginActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			LoginActivity theActivity = mActivity.get();
			if (theActivity == null) {
				return;
			}
			switch (msg.what) {
			case Content.SEND_XML_LOGIN:
				theActivity.LoginSystem_Rev((PostResult) msg.obj);
				break;

			default:
				break;
			}
		}
	}

	private OnItemClickListener itemClicklistener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			inputUser.setText(mBeanList.get(position).getUser());
			inputKey.setText(mBeanList.get(position).getKey());
			mListView.setVisibility(View.GONE);
		}
	};

	private ListViewAdapterCallBack listViewAdapterCallBack = new ListViewAdapterCallBack() {

		@Override
		public void onItemUpdate(View v, int mPos, String newValue1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onItemDelete(View v, int mPos) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onItemClick(View v, int mPos, AdapterBean bean) {
			mBeanList.remove(mPos);
			UpDateJSON("", "", mPos);
			mListViewAdapter.notifyDataSetChanged();
		}
	};

}
