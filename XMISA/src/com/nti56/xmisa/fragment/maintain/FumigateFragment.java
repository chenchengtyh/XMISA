package com.nti56.xmisa.fragment.maintain;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.nti56.xmisa.bean.AdapterBean;
import com.nti56.xmisa.bean.FumigateBean;
import com.nti56.xmisa.bean.FumigateDetailBean;
import com.nti56.xmisa.adapter.MyListView.OnRefreshListener;

import com.nti56.xmisa.R;
import com.nti56.xmisa.ImageActivity;
import com.nti56.xmisa.MainActivity;
import com.nti56.xmisa.adapter.MyListViewAdapter;
import com.nti56.xmisa.adapter.MyListViewAdapter.ListViewAdapterCallBack;
import com.nti56.xmisa.adapter.MySQLiteHelper;
import com.nti56.xmisa.adapter.MyListView;
import com.nti56.xmisa.bean.PostResult;
import com.nti56.xmisa.util.Content;
import com.nti56.xmisa.util.FTPUtil;
import com.nti56.xmisa.util.HttpUtil;
import com.nti56.xmisa.util.MyLog;
import com.nti56.xmisa.util.MyToast;
import com.nti56.xmisa.util.FTPUtil.UploadProgressListener;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLayoutChangeListener;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FumigateFragment extends Fragment implements OnClickListener, OnLayoutChangeListener, OnFocusChangeListener {

	private static MyHandler mHandler;

	private MainActivity mContext;
	// 怀川 怀远 冰心 怀宽 自如 逸才
	//山 川 海 正 德 仁 义 忠 孝 礼 信 远 宽 广 智 志 卓 善 坚
	private FTPUtil mFTP;
	private HttpUtil http;
	private MyToast mToast;
	// 630 57.9% 116/64 93 14 2 48
	private File mediaStorageDir;
	private SQLiteDatabase sqlite;
	private MyListViewAdapter listViewAdapter1, listViewAdapter2, listViewAdapter2_1, listViewAdapter2_2;

	private int mDetailClickPos = -1, mRoomChangePos = -1;

	private WeakReference<View> mRootView;
	private mFmgFragmentListener mListener;

	private RelativeLayout layout1, layout2;
	private LinearLayout layoutInfo, mLayoutRoomSelect;
	private MyListView mListView1, mListView2;
	private TextView mOrderID, mMethod, mBuilding, mRoom, mStateNC, mMode2, mBtnHideRoomSelect;
	private Button mBtn_save, mBtn_back, mBtn_commit, mBtn_scan, mBtn_Room1, mBtn_Room2, mBtn_Room3, mBtn_Room4, mBtn_Room5, mBtn_Room6,
			mBtn_Room7, mBtn_Room8;
	private EditText mDrug, mDosingDate, mAirStart, mAirEnd, mUnit, mVolume, mDosage, mNote, mFocusEditText;

	private String NCState;
	private String mCurTaskID;
	private String TAG = "FumigateFragment";
	private String DB_TABLE = "fumigate";
	private String DETAIL_TABLE = "fumigate_detail";

	private ArrayList<String> finalImageName = new ArrayList<String>();

	private ArrayList<AdapterBean> mBeanList1 = new ArrayList<AdapterBean>();
	private ArrayList<AdapterBean> mBeanList2 = new ArrayList<AdapterBean>();

	private String sql_creat1 = "CREATE TABLE if not exists fumigate(_taskid VARCHAR PRIMARY KEY, _date VARCHAR, _orderid VARCHAR, "
			+ "_building VARCHAR, _method VARCHAR, _dosingdate VARCHAR, _airstart VARCHAR, _airend VARCHAR, _statenc VARCHAR, "
			+ "_person VARCHAR, _room VARCHAR, _drug VARCHAR, _unit VARCHAR, _volume VARCHAR, _dosage VARCHAR, _note VARCHAR)";

	private String sql_creat2 = "CREATE TABLE if not exists fumigate_detail(_taskid VARCHAR, _detailid VARCHAR PRIMARY KEY, _trayid VARCHAR,"
			+ " _trayname VARCHAR, _location VARCHAR, _concentration6 VARCHAR, _concentration12 VARCHAR, _concentration24 VARCHAR,"
			+ " _concentration48 VARCHAR, _concentration72 VARCHAR, _concentration96 VARCHAR, _concentration144 VARCHAR, _concentration216 VARCHAR,"
			+ " _picture VARCHAR, _picturever VARCHAR)";

	public interface mFmgFragmentListener {
		public void hideMenu(boolean hide);

		public void btnCancel();
	}

	public FumigateFragment(mFmgFragmentListener mListener) {
		this.mListener = mListener;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Content.ACTIVITY_REQUEST_CODE_IMAGE) {
			if (resultCode == Activity.RESULT_OK) {
				String mImageName = data.getStringExtra("newImageName");
				AdapterBean bean = mBeanList2.get(mDetailClickPos);
				bean.setPicture(mImageName);
				listViewAdapter2.notifyDataSetChanged();
			} else if (resultCode == Activity.RESULT_CANCELED) {

			}
			mDetailClickPos = -1;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		mContext = (MainActivity) getActivity();
		sqlite = MySQLiteHelper.getInstance();
		http = HttpUtil.getInstance();
		mHandler = new MyHandler(this);
		mToast = MyToast.getInstance();
		InitListViewAdapter1();
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.e("NTI", "FumigateFragment........onCreateView()");
		if (mRootView == null || mRootView.get() == null) {
			View v = inflater.inflate(R.layout.fragment_3_fumigate, container, false);
			mRootView = new WeakReference<View>(v);
		} else {
			ViewGroup parent = (ViewGroup) mRootView.get().getParent();
			if (parent != null) {
				parent.removeView(mRootView.get());
			}
		}
		View view = mRootView.get();
		InitLayout(view);
		return view;
	}

	@Override
	public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
		if (oldBottom != 0 && bottom != 0 && (oldBottom - bottom > 200)) {
			Content.InputSoftOpen = true;
		} else if (oldBottom != 0 && bottom != 0 && (bottom - oldBottom > 200)) {
			Content.InputSoftOpen = false;
		} else {
			return;
		}
		Message msg = mHandler.obtainMessage(Content.INPUT_SOFT_ACTION);
		mHandler.sendMessageDelayed(msg, 100);
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		Log.e("NTI", "FumigateFragment........onHiddenChanged():" + hidden);
		if (!hidden && mRootView != null) {
			if (layout2.getVisibility() == View.VISIBLE) {
				mContext.setMyBackModel(Content.BACK_MODEL_FMG_DETAIL);
			} else {
				mContext.setMyBackModel(Content.BACK_MODEL_NONE);
			}
		}
		super.onHiddenChanged(hidden);
	}

	@Override
	public void onDestroy() {
		Log.e("NTI", "FumigateFragment........onDestroy()");
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		if (Content.InputSoftOpen) {
			return;
		}
		switch (v.getId()) {
		case R.id.btn_fmg_detail_back:// 返回
			ShowMainTable();
			break;

		case R.id.btn_fmg_detail_save:// 保存
			SaveDetailData();
			break;

		case R.id.btn_fmg_detail_commit: { // 反馈
			SaveDetailData();
			CommitData_Send();
		}
			break;

		case R.id.btn_fmg_detail_scan:
			// mContext.scanRFID(this, "XZ");
			break;

		case R.id.tv_hide_roomselect:
			mLayoutRoomSelect.setVisibility(View.GONE);
			mBtnHideRoomSelect.setVisibility(View.GONE);
			break;

		case R.id.btn_fmg_room_1: {
			AdapterBean bean = mBeanList2.get(mRoomChangePos);
			bean.setTrayID("1901");
			bean.setTrayName("DFXZ01");
			listViewAdapter2.notifyDataSetChanged();
			mLayoutRoomSelect.setVisibility(View.GONE);
			mBtnHideRoomSelect.setVisibility(View.GONE);
			mRoomChangePos = -1;
		}
			break;

		case R.id.btn_fmg_room_2: {
			AdapterBean bean = mBeanList2.get(mRoomChangePos);
			bean.setTrayID("1902");
			bean.setTrayName("DFXZ02");
			listViewAdapter2.notifyDataSetChanged();
			mLayoutRoomSelect.setVisibility(View.GONE);
			mBtnHideRoomSelect.setVisibility(View.GONE);
			mRoomChangePos = -1;
		}
			break;

		case R.id.btn_fmg_room_3: {
			AdapterBean bean = mBeanList2.get(mRoomChangePos);
			bean.setTrayID("1903");
			bean.setTrayName("DFXZ03");
			listViewAdapter2.notifyDataSetChanged();
			mLayoutRoomSelect.setVisibility(View.GONE);
			mBtnHideRoomSelect.setVisibility(View.GONE);
			mRoomChangePos = -1;
		}
			break;

		case R.id.btn_fmg_room_4: {
			AdapterBean bean = mBeanList2.get(mRoomChangePos);
			bean.setTrayID("1904");
			bean.setTrayName("DFXZ04");
			listViewAdapter2.notifyDataSetChanged();
			mLayoutRoomSelect.setVisibility(View.GONE);
			mBtnHideRoomSelect.setVisibility(View.GONE);
			mRoomChangePos = -1;
		}
			break;

		case R.id.btn_fmg_room_5: {
			AdapterBean bean = mBeanList2.get(mRoomChangePos);
			bean.setTrayID("1905");
			bean.setTrayName("DFXZ05");
			listViewAdapter2.notifyDataSetChanged();
			mLayoutRoomSelect.setVisibility(View.GONE);
			mBtnHideRoomSelect.setVisibility(View.GONE);
			mRoomChangePos = -1;
		}
			break;

		case R.id.btn_fmg_room_6: {
			AdapterBean bean = mBeanList2.get(mRoomChangePos);
			bean.setTrayID("1906");
			bean.setTrayName("DFXZ06");
			listViewAdapter2.notifyDataSetChanged();
			mLayoutRoomSelect.setVisibility(View.GONE);
			mBtnHideRoomSelect.setVisibility(View.GONE);
			mRoomChangePos = -1;
		}
			break;

		case R.id.btn_fmg_room_7: {
			AdapterBean bean = mBeanList2.get(mRoomChangePos);
			bean.setTrayID("1907");
			bean.setTrayName("DFXZ07");
			listViewAdapter2.notifyDataSetChanged();
			mLayoutRoomSelect.setVisibility(View.GONE);
			mBtnHideRoomSelect.setVisibility(View.GONE);
			mRoomChangePos = -1;
		}
			break;

		case R.id.btn_fmg_room_8: {
			AdapterBean bean = mBeanList2.get(mRoomChangePos);
			bean.setTrayID("1908");
			bean.setTrayName("DFXZ08");
			listViewAdapter2.notifyDataSetChanged();
			mLayoutRoomSelect.setVisibility(View.GONE);
			mBtnHideRoomSelect.setVisibility(View.GONE);
			mRoomChangePos = -1;
		}
			break;

		default:
			break;
		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		switch (v.getId()) {
		case R.id.et_fmg_detail_drug:
			mFocusEditText = hasFocus ? mDrug : null;
			break;

		case R.id.et_fmg_detail_dosingdate:
			mFocusEditText = hasFocus ? mDosingDate : null;
			break;

		case R.id.et_fmg_detail_air_start:
			mFocusEditText = hasFocus ? mAirStart : null;
			break;

		case R.id.et_fmg_detail_air_end:
			mFocusEditText = hasFocus ? mAirEnd : null;
			break;

		case R.id.et_fmg_detail_dosingunit:
			mFocusEditText = hasFocus ? mUnit : null;
			break;

		case R.id.et_fmg_detail_volume:
			mFocusEditText = hasFocus ? mVolume : null;
			break;

		case R.id.et_fmg_detail_dosage:
			mFocusEditText = hasFocus ? mDosage : null;
			break;

		case R.id.et_fmg_detail_note:
			mFocusEditText = hasFocus ? mNote : null;
			break;

		default:
			return;
		}
		if (!hasFocus && Content.InputSoftOpen) {
			Message msg = mHandler.obtainMessage(Content.JUDGE_EDITTEXT_FOCUS);
			mHandler.sendMessageDelayed(msg, 50);
		}
	}

	private void CommitData_Send() {
		String Action = "UPDATE";
		String ImageUrl1, ImageUrl2, ImageUrl3, ImageUrl4;
		String TaskXML = "", DetailXML = "";
		Cursor cursor = sqlite.query(DB_TABLE, null, "_taskid = ?", new String[] { mCurTaskID }, null, null, null);
		if (cursor != null && cursor.moveToFirst()) {
			TaskXML = "<Task taskid=\"" + cursor.getString(0) + "\" billdate=\"" + cursor.getString(1) + "\" tasknumber=\""
					+ cursor.getString(2) + "\" ban=\"" + cursor.getString(3) + "\" methodcode=\"" + cursor.getString(4)
					+ "\" dosingdate=\"" + cursor.getString(5) + "\" divergencedate=\"" + cursor.getString(6) + "\" acceptancedate=\""
					+ cursor.getString(7) + "\" dispatcher=\"" + Content.USER_SYS_CODE + "\" fumigation=\"" + cursor.getString(10)
					+ "\" agent=\"" + cursor.getString(11) + "\" company=\"" + cursor.getString(12) + "\" volume=\"" + cursor.getString(13)
					+ "\" dosage=\"" + cursor.getString(14) + "\" note=\"" + cursor.getString(15) + "\" > ";
			cursor.close();
		}
		cursor = null;

		finalImageName.clear();
		Cursor cursor2 = sqlite.query(DETAIL_TABLE, null, "_taskid = ?", new String[] { mCurTaskID }, null, null, null);
		if (cursor2 != null && cursor2.moveToFirst()) {
			do {
				String[] imageName = cursor2.getString(13).split("/");
				ImageUrl1 = (imageName[0].length() == 38) ? (Content.getFtpUrl() + imageName[0] + ".jpg") : " ";
				ImageUrl2 = (imageName[1].length() == 38) ? (Content.getFtpUrl() + imageName[1] + ".jpg") : " ";
				ImageUrl3 = (imageName[2].length() == 38) ? (Content.getFtpUrl() + imageName[2] + ".jpg") : " ";
				ImageUrl4 = (imageName[3].length() == 38) ? (Content.getFtpUrl() + imageName[3] + ".jpg") : " ";

				for (int i = 0; i < imageName.length; i++) {
					finalImageName.add(imageName[i]);
				}
				DetailXML += "<TaskDetail taskId=\"" + cursor2.getString(0) + "\" detailId=\"" + cursor2.getString(1)
						+ "\" locationcode=\"" + cursor2.getString(2) 
						+ "\" locationname=\"" + cursor2.getString(3) + "\" position=\"" + cursor2.getString(4) + "\" hsulubility6=\""
						+ cursor2.getString(5) + "\" hsulubility12=\"" + cursor2.getString(6) + "\" hsulubility24=\""
						+ cursor2.getString(7) + "\" hsulubility48=\"" + cursor2.getString(8) + "\" hsulubility72=\""
						+ cursor2.getString(9) + "\" hsulubility96=\"" + cursor2.getString(10) + "\" hsulubility144=\""
						+ cursor2.getString(11) + "\" hsulubility216=\"" + cursor2.getString(12) + "\" imageurl1=\"" + ImageUrl1
						+ "\" imageurl2=\"" + ImageUrl2 + "\" imageurl3=\"" + ImageUrl3 + "\" imageurl4=\"" + ImageUrl4 + "\" /> ";
			} while (cursor2.moveToNext());
			cursor2.close();
		}
		cursor2 = null;
		final String UpdateXML = "<Request><Function>fumigateNC</Function><Action>" + Action + "</Action><Parms>" + TaskXML + DetailXML
				+ "</Task></Parms></Request>";
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (http == null) {
					http = HttpUtil.getInstance();
				}
				PostResult result = http.sendXML(UpdateXML);
				mHandler.obtainMessage(Content.SEND_XML_FMG_COMMIT, result).sendToTarget();
			}
		}).start();
	}

	private void CommitData_Rev(PostResult result) {// TODO,1反馈成功需判断图片是否需要上传，2反馈成功需删除Content.DB_NEED_DELETE
		if (result.isResult()) {
			new Thread(upLoadImageFiles).start();
			ContentValues values = new ContentValues();
			values.put("_statenc", Content.HTTP_UPLOAD_TRUE);
			sqlite.update(DB_TABLE, values, "_taskid = ?", new String[] { mCurTaskID });
			NCState = Content.HTTP_UPLOAD_TRUE;
			mStateNC.setText(NCState);
			UpDateListView2();
		}
		mToast.show(result.getMessage());
	}

	private void DeleteTable(String TABLE, String sql_creat) {
		try {
			sqlite.delete(TABLE, null, null);
		} catch (Exception e) {
			sqlite.execSQL(sql_creat);
		}
	}

	private File GetMediaFile(String imageName) {
		if (mediaStorageDir == null) {
			mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "XMISA_image");
		}
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				return null;
			}
		}
		File mediaFile;
		mediaFile = new File(mediaStorageDir.getPath() + File.separator + imageName + ".jpg");
		return mediaFile;
	}

	private void InitLayout(View view) {
		if (mListView1 == null) {
			view.addOnLayoutChangeListener(this);

			mOrderID = (TextView) view.findViewById(R.id.tv_fmg_detail_orderid);
			mMethod = (TextView) view.findViewById(R.id.tv_fmg_detail_method);
			mBuilding = (TextView) view.findViewById(R.id.tv_fmg_detail_building);
			mRoom = (TextView) view.findViewById(R.id.tv_fmg_detail_room);
			mStateNC = (TextView) view.findViewById(R.id.tv_fmg_detail_statenc);

			mLayoutRoomSelect = (LinearLayout) view.findViewById(R.id.layout_fmg_roomselect);
			mBtnHideRoomSelect = (TextView) view.findViewById(R.id.tv_hide_roomselect);

			mDrug = (EditText) view.findViewById(R.id.et_fmg_detail_drug);
			mDosingDate = (EditText) view.findViewById(R.id.et_fmg_detail_dosingdate);
			mAirStart = (EditText) view.findViewById(R.id.et_fmg_detail_air_start);
			mAirEnd = (EditText) view.findViewById(R.id.et_fmg_detail_air_end);

			mUnit = (EditText) view.findViewById(R.id.et_fmg_detail_dosingunit);
			mVolume = (EditText) view.findViewById(R.id.et_fmg_detail_volume);
			mDosage = (EditText) view.findViewById(R.id.et_fmg_detail_dosage);
			mNote = (EditText) view.findViewById(R.id.et_fmg_detail_note);

			mMode2 = (TextView) view.findViewById(R.id.tv_fmg_detail_mode2);

			mBtn_back = (Button) view.findViewById(R.id.btn_fmg_detail_back);
			mBtn_save = (Button) view.findViewById(R.id.btn_fmg_detail_save);
			mBtn_commit = (Button) view.findViewById(R.id.btn_fmg_detail_commit);
			mBtn_scan = (Button) view.findViewById(R.id.btn_fmg_detail_scan);
			mBtn_Room1 = (Button) view.findViewById(R.id.btn_fmg_room_1);
			mBtn_Room2 = (Button) view.findViewById(R.id.btn_fmg_room_2);
			mBtn_Room3 = (Button) view.findViewById(R.id.btn_fmg_room_3);
			mBtn_Room4 = (Button) view.findViewById(R.id.btn_fmg_room_4);
			mBtn_Room5 = (Button) view.findViewById(R.id.btn_fmg_room_5);
			mBtn_Room6 = (Button) view.findViewById(R.id.btn_fmg_room_6);
			mBtn_Room7 = (Button) view.findViewById(R.id.btn_fmg_room_7);
			mBtn_Room8 = (Button) view.findViewById(R.id.btn_fmg_room_8);

			layout1 = (RelativeLayout) view.findViewById(R.id.layout_fumigate);
			layout2 = (RelativeLayout) view.findViewById(R.id.layout_fmg_detail);
			layoutInfo = (LinearLayout) view.findViewById(R.id.layout_fmg_detail_info);

			mBtn_save.setOnClickListener(this);
			mBtn_back.setOnClickListener(this);
			mBtn_scan.setOnClickListener(this);
			mBtn_commit.setOnClickListener(this);
			mBtn_Room1.setOnClickListener(this);
			mBtn_Room2.setOnClickListener(this);
			mBtn_Room3.setOnClickListener(this);
			mBtn_Room4.setOnClickListener(this);
			mBtn_Room5.setOnClickListener(this);
			mBtn_Room6.setOnClickListener(this);
			mBtn_Room7.setOnClickListener(this);
			mBtn_Room8.setOnClickListener(this);
			mBtnHideRoomSelect.setOnClickListener(this);

			mDrug.setOnFocusChangeListener(this);
			mDosingDate.setOnFocusChangeListener(this);
			mAirStart.setOnFocusChangeListener(this);
			mAirEnd.setOnFocusChangeListener(this);
			mUnit.setOnFocusChangeListener(this);
			mVolume.setOnFocusChangeListener(this);
			mDosage.setOnFocusChangeListener(this);
			mNote.setOnFocusChangeListener(this);

			mListView1 = (MyListView) view.findViewById(R.id.list_fumigate);
			mListView2 = (MyListView) view.findViewById(R.id.list_fmg_detail);

			mListView1.setOnRefreshListener(refreshListener, Content.LIST_FUMIGATE);
			mListView1.setAdapter(listViewAdapter1);
			mListView1.setOnItemClickListener(itemClicklistener1);
			mContext.setMyBackModel(Content.BACK_MODEL_NONE);
		}
	}

	private void InitListViewAdapter1() {
		sqlite.execSQL(sql_creat1);
		Cursor cursor = sqlite.query(DB_TABLE, null, null, null, null, null, null);
		if (cursor != null && cursor.moveToFirst()) {
			do {
				AdapterBean bean = new AdapterBean();
				bean.setTaskID(cursor.getString(0));
				bean.setDate(cursor.getString(1));
				bean.setOrderID(cursor.getString(2));
				bean.setBuilding(cursor.getString(3));
				bean.setMethod(cursor.getString(4));
				bean.setDosingDate(cursor.getString(5));
				bean.setAirStart(cursor.getString(6));
				bean.setAirEnd(cursor.getString(7));
				bean.setNCState(cursor.getString(8));
				bean.setPerson(cursor.getString(9));
				bean.setRoom(cursor.getString(10));
				bean.setDrug(cursor.getString(11));
				bean.setUnit(cursor.getString(12));
				bean.setVolume(cursor.getString(13));
				bean.setDosage(cursor.getString(14));
				bean.setNote(cursor.getString(15));
				mBeanList1.add(bean);
			} while (cursor.moveToNext());
			cursor.close();
		}
		cursor = null;
		listViewAdapter1 = new MyListViewAdapter(mContext, R.layout.list_fumigate, mBeanList1, Content.LIST_FUMIGATE, null);
	}

	private void InitListViewAdapter2() {
		sqlite.execSQL(sql_creat2);
		listViewAdapter2_1 = new MyListViewAdapter(mContext, R.layout.list_fmg_detail_1, mBeanList2, Content.LIST_FUMIGATE_DETAIL,
				listViewAdapterCallBack2);
		listViewAdapter2_2 = new MyListViewAdapter(mContext, R.layout.list_fmg_detail_2, mBeanList2, Content.LIST_FUMIGATE_DETAIL,
				listViewAdapterCallBack2);
	}

	private void JudgeEditTextFocus() {
		if (mFocusEditText == null && Content.InputSoftOpen && mListener != null) {
			mListener.hideMenu(true);
			if (listViewAdapter2 != null) {
				listViewAdapter2.setInputOpen(true);
			}
			layoutInfo.setVisibility(View.GONE);
		}
	}

	private void LoadData_Send() {
		final String xmlString = "<Request><Function>fumigate</Function><Parms><Username>" + Content.USER_SYS_CODE
				+ "</Username></Parms></Request>";
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (http == null) {
					http = HttpUtil.getInstance();
				}
				PostResult result = http.sendXML(xmlString);
				if (result != null) {
					mHandler.obtainMessage(Content.SEND_XML_FMG_LOAD, result).sendToTarget();
				}
			}
		}).start();

	}

	private void LoadData_Rev(PostResult result) {
		if (result.isResult()) {
			DeleteTable(DB_TABLE, sql_creat1);
			DeleteTable(DETAIL_TABLE, sql_creat2);
			UpdateSQLiteData(result.getFumigateBeanList());
			UpDateListView1();
		} else {
			mToast.show(result.getMessage());
		}
		mListView1.onRefreshComplete(result.isResult(), Content.LIST_FUMIGATE);

	}

	private void SaveDetailData() {
		ContentValues values = new ContentValues();

		values.put("_drug", mDrug.getText().toString());
		values.put("_dosingdate", mDosingDate.getText().toString());
		values.put("_airstart", mAirStart.getText().toString());
		values.put("_airend", mAirEnd.getText().toString());
		values.put("_unit", mUnit.getText().toString());
		values.put("_volume", mVolume.getText().toString());
		values.put("_dosage", mDosage.getText().toString());
		values.put("_note", mNote.getText().toString());
		sqlite.update(DB_TABLE, values, "_taskid = ?", new String[] { mCurTaskID });

		for (int i = 0; i < mBeanList2.size(); i++) {
			values.clear();

			String DetailID = mBeanList2.get(i).getTaskID();
			String TrayID = mBeanList2.get(i).getTrayID();
			String TrayName = mBeanList2.get(i).getTrayName();
			String Concentration6 = mBeanList2.get(i).getConcentration6();
			String Concentration12 = mBeanList2.get(i).getConcentration12();
			String Concentration24 = mBeanList2.get(i).getConcentration24();
			String Concentration48 = mBeanList2.get(i).getConcentration48();
			String Concentration72 = mBeanList2.get(i).getConcentration72();
			String Concentration96 = mBeanList2.get(i).getConcentration96();
			String Concentration144 = mBeanList2.get(i).getConcentration144();
			String Concentration216 = mBeanList2.get(i).getConcentration216();
			String Picture = mBeanList2.get(i).getPicture();

			values.put("_trayid", TrayID);
			values.put("_trayname", TrayName);
			values.put("_concentration6", Concentration6);
			values.put("_concentration12", Concentration12);
			values.put("_concentration24", Concentration24);
			values.put("_concentration48", Concentration48);
			values.put("_concentration72", Concentration72);
			values.put("_concentration96", Concentration96);
			values.put("_concentration144", Concentration144);
			values.put("_concentration216", Concentration216);
			values.put("_picture", Picture);

			sqlite.update(DETAIL_TABLE, values, "_detailid = ?", new String[] { DetailID });
		}
		mToast.show("保存成功！");
	}

	private void ShowInputSoft() {
		if (mFocusEditText != null) {
			if (!Content.InputSoftOpen) {
				mFocusEditText.clearFocus();
				mFocusEditText = null;
			}
		} else if (mListener != null && Content.InputSoftOpen) {
			mListener.hideMenu(true);
			listViewAdapter2.setInputOpen(true);
			layoutInfo.setVisibility(View.GONE);
		} else if (mListener != null) {
			mListener.hideMenu(false);
			listViewAdapter2.setInputOpen(false);
			layoutInfo.setVisibility(View.VISIBLE);
		}
	}

	private void ShowMainTable() {
		UpDateListView1();
		mContext.setMyBackModel(Content.BACK_MODEL_NONE);
		if (layout1.getVisibility() == View.GONE) {
			mLayoutRoomSelect.setVisibility(View.GONE);
			mBtnHideRoomSelect.setVisibility(View.GONE);
			layout1.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_layout_right_show));
			layout2.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_layout_right_hide));
			layout1.setVisibility(View.VISIBLE);
			layout2.setVisibility(View.GONE);
		}
	}

	private void ShowDetailTable(int pos) {// TODO
		if (listViewAdapter2 == null) {
			InitListViewAdapter2();
		}
		AdapterBean bean = mBeanList1.get(pos - 1);
		String method = bean.getMethod();
		if (method == null) {
			mToast.show("熏蒸方法不符！");
			return;
		} else if (method.equals("YH001") || method.equals("熏蒸室熏蒸")) {// 熏蒸室熏蒸
			if (listViewAdapter2 != listViewAdapter2_2) {
				listViewAdapter2 = listViewAdapter2_2;
				mListView2.setAdapter(listViewAdapter2);
			}
			mMode2.setText("熏蒸室");
			mMode2.setVisibility(View.VISIBLE);
		} else if (method.equals("YH002") || method.equals("在库熏蒸杀虫(栋杀)")) {// 在库熏蒸杀虫(栋杀)
			if (listViewAdapter2 != listViewAdapter2_1) {
				listViewAdapter2 = listViewAdapter2_1;
				mListView2.setAdapter(listViewAdapter2);
			}
			mMode2.setVisibility(View.GONE);
		} else if (method.equals("YH007") || method.equals("在库熏蒸杀虫(层杀)")) {// 在库熏蒸杀虫(层杀)
			if (listViewAdapter2 != listViewAdapter2_1) {
				listViewAdapter2 = listViewAdapter2_1;
				mListView2.setAdapter(listViewAdapter2);
			}
			mMode2.setVisibility(View.GONE);
		} else if (method.equals("YH008") || method.equals("在库熏蒸杀虫(垛杀)")) {// 在库熏蒸杀虫(垛杀)
			if (listViewAdapter2 != listViewAdapter2_2) {
				listViewAdapter2 = listViewAdapter2_2;
				mListView2.setAdapter(listViewAdapter2);
			}
			mMode2.setText("货位名称");
			mMode2.setVisibility(View.VISIBLE);
		} else {
			mToast.show("熏蒸方法不符！");
			return;
		}
		mCurTaskID = bean.getTaskID();
		mOrderID.setText(bean.getOrderID());
		mMethod.setText(bean.getMethod());
		mBuilding.setText(bean.getBuilding());
		mRoom.setText(bean.getRoom());
		mDrug.setText(bean.getDrug());
		mDosingDate.setText(bean.getDosingDate());
		mAirStart.setText(bean.getAirStart());
		mAirEnd.setText(bean.getAirEnd());
		mUnit.setText(bean.getUnit());
		mVolume.setText(bean.getVolume());
		mDosage.setText(bean.getDosage());
		mNote.setText(bean.getNote());

		NCState = bean.getNCState();
		mStateNC.setText(bean.getNCState());

		UpDateListView2();
		mContext.setMyBackModel(Content.BACK_MODEL_FMG_DETAIL);
		if (layout2.getVisibility() == View.GONE) {
			layout1.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_layout_left_hide));
			layout2.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_layout_left_show));
			layout1.setVisibility(View.GONE);
			layout2.setVisibility(View.VISIBLE);
		}
	}

	private void UpDateListView1() {
		mCurTaskID = "";
		mBeanList1.clear();
		Cursor cursor = sqlite.query(DB_TABLE, null, null, null, null, null, null);
		if (cursor != null && cursor.moveToFirst()) {
			do {
				AdapterBean bean = new AdapterBean();
				bean.setTaskID(cursor.getString(0));
				bean.setDate(cursor.getString(1));
				bean.setOrderID(cursor.getString(2));
				bean.setBuilding(cursor.getString(3));
				bean.setMethod(cursor.getString(4));
				bean.setDosingDate(cursor.getString(5));
				bean.setAirStart(cursor.getString(6));
				bean.setAirEnd(cursor.getString(7));
				bean.setNCState(cursor.getString(8));
				bean.setPerson(cursor.getString(9));
				bean.setRoom(cursor.getString(10));
				bean.setDrug(cursor.getString(11));
				bean.setUnit(cursor.getString(12));
				bean.setVolume(cursor.getString(13));
				bean.setDosage(cursor.getString(14));
				bean.setNote(cursor.getString(15));
				mBeanList1.add(bean);
			} while (cursor.moveToNext());
			cursor.close();
		}
		cursor = null;
		listViewAdapter1.notifyDataSetChanged();
	}

	private void UpDateListView2() {
		mBeanList2.clear();
		Cursor cursor = sqlite.query(DETAIL_TABLE, null, "_taskid = ?", new String[] { mCurTaskID }, null, null, null);
		if (cursor != null && cursor.moveToFirst()) {
			do {
				AdapterBean bean = new AdapterBean();
				bean.setPTaskID(cursor.getString(0));
				bean.setTaskID(cursor.getString(1));
				bean.setTrayID(cursor.getString(2));
				bean.setTrayName(cursor.getString(3));
				bean.setLocation(cursor.getString(4));
				bean.setConcentration6(cursor.getString(5));
				bean.setConcentration12(cursor.getString(6));
				bean.setConcentration24(cursor.getString(7));
				bean.setConcentration48(cursor.getString(8));
				bean.setConcentration72(cursor.getString(9));
				bean.setConcentration96(cursor.getString(10));
				bean.setConcentration144(cursor.getString(11));
				bean.setConcentration216(cursor.getString(12));
				bean.setPicture(cursor.getString(13));
				bean.setPictureVersion(cursor.getString(14));
				mBeanList2.add(bean);
			} while (cursor.moveToNext());
			cursor.close();
		}
		cursor = null;
		listViewAdapter2.setNCState(NCState.equals(Content.HTTP_UPLOAD_TRUE));
		listViewAdapter2.notifyDataSetChanged();
	}

	private void UpdateSQLiteData(List<FumigateBean> mTaskBeans) {
		sqlite.beginTransaction();
		ContentValues cv = new ContentValues();
		for (int i = 0; i < mTaskBeans.size(); i++) {
			cv.clear();
			cv.put("_taskid", mTaskBeans.get(i).getTaskId());
			cv.put("_date", mTaskBeans.get(i).getDate());
			cv.put("_orderid", mTaskBeans.get(i).getOrderId());
			cv.put("_building", mTaskBeans.get(i).getBuilding());
			cv.put("_method", mTaskBeans.get(i).getMethod());
			cv.put("_dosingdate", mTaskBeans.get(i).getDosingDate());
			cv.put("_airstart", mTaskBeans.get(i).getAirStart());
			cv.put("_airend", mTaskBeans.get(i).getAirEnd());
			cv.put("_person", mTaskBeans.get(i).getPerson());
			cv.put("_room", mTaskBeans.get(i).getRoom());
			cv.put("_drug", mTaskBeans.get(i).getDrug());
			cv.put("_unit", mTaskBeans.get(i).getUnit());
			cv.put("_volume", mTaskBeans.get(i).getVolume());
			cv.put("_dosage", mTaskBeans.get(i).getDosage());
			cv.put("_note", mTaskBeans.get(i).getNote());
			if (mTaskBeans.get(i).getStateNC().equals("0")) {
				cv.put("_statenc", Content.HTTP_UPLOAD_FALSE);
			} else {
				cv.put("_statenc", Content.HTTP_UPLOAD_TRUE);
			}

			sqlite.insert(DB_TABLE, null, cv);
			List<FumigateDetailBean> details = mTaskBeans.get(i).getDetailBeanList();
			if (details != null && details.size() > 0) {
				for (int j = 0; j < details.size(); j++) {
					cv.clear();
					cv.put("_taskid", details.get(j).getTaskId());
					cv.put("_detailid", details.get(j).getDetailId());
					cv.put("_trayid", details.get(j).getTrayID());
					cv.put("_trayname", details.get(j).getTrayName());
					// cv.put("_floor", details.get(j).getFloor());
					cv.put("_location", details.get(j).getLocation());
					cv.put("_concentration6", details.get(j).getConcentration6());
					cv.put("_concentration12", details.get(j).getConcentration12());
					cv.put("_concentration24", details.get(j).getConcentration24());
					cv.put("_concentration48", details.get(j).getConcentration48());
					cv.put("_concentration72", details.get(j).getConcentration72());
					cv.put("_concentration96", details.get(j).getConcentration96());
					cv.put("_concentration144", details.get(j).getConcentration144());
					cv.put("_concentration216", details.get(j).getConcentration216());
					cv.put("_picture", details.get(j).getPicture());
					cv.put("_picturever", details.get(j).getPictureVersion());
					sqlite.insert(DETAIL_TABLE, null, cv);
				}
			}
		}
		sqlite.setTransactionSuccessful();
		sqlite.endTransaction();
	}

	private static class MyHandler extends Handler {
		WeakReference<FumigateFragment> mFragment;

		MyHandler(FumigateFragment fumigateFragment) {
			mFragment = new WeakReference<FumigateFragment>(fumigateFragment);
		}

		@Override
		public void handleMessage(Message msg) {
			FumigateFragment theFragment = mFragment.get();
			if (theFragment == null) {
				return;
			}
			switch (msg.what) {
			case Content.SEND_XML_FMG_LOAD:
				theFragment.LoadData_Rev((PostResult) msg.obj);
				break;

			case Content.SEND_XML_FMG_COMMIT:
				theFragment.CommitData_Rev((PostResult) msg.obj);
				break;

			case Content.BACK_PRESSED:
				theFragment.ShowMainTable();
				break;

			case Content.INPUT_SOFT_ACTION:
				theFragment.ShowInputSoft();
				break;

			case Content.JUDGE_EDITTEXT_FOCUS:
				theFragment.JudgeEditTextFocus();
				break;

			default:
				break;
			}
		}
	}

	private OnRefreshListener refreshListener = new OnRefreshListener() {

		@Override
		public void onRefresh() {
			LoadData_Send();
		}

	};

	private OnItemClickListener itemClicklistener1 = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
			ShowDetailTable(pos);
		}

	};

	private ListViewAdapterCallBack listViewAdapterCallBack2 = new ListViewAdapterCallBack() {

		@Override
		public void onItemClick(View v, int pos, AdapterBean bean) {
			if (bean != null) {
				mDetailClickPos = pos;
				String[] imageNames = bean.getPicture().split("/");
				Intent intent = new Intent();
				intent.setClass(mContext, ImageActivity.class);
				intent.putExtra("taskId", bean.getTaskID());
				intent.putExtra("NCState", NCState);
				intent.putExtra("imageName1", imageNames.length < 1 ? " " : imageNames[0]);
				intent.putExtra("imageName2", imageNames.length < 2 ? " " : imageNames[1]);
				intent.putExtra("imageName3", imageNames.length < 3 ? " " : imageNames[2]);
				intent.putExtra("imageName4", imageNames.length < 4 ? " " : imageNames[3]);
				intent.putExtra("imageVersion", bean.getPictureVersion());
				startActivityForResult(intent, Content.ACTIVITY_REQUEST_CODE_IMAGE);
				// 点击反馈之后，反馈成功则删除旧文件
			} else if (!Content.InputSoftOpen && mMode2.getText().equals("熏蒸室")) {
				int[] location = new int[2];
				v.getLocationInWindow(location);
				int X = location[0];
				int Y = location[1];

				mLayoutRoomSelect.getLocationInWindow(location);
				Y = Y - location[1];
				mLayoutRoomSelect.setX(X + 81);
				mLayoutRoomSelect.setY(Y + mLayoutRoomSelect.getY());

				mRoomChangePos = pos;
				mLayoutRoomSelect.setVisibility(View.VISIBLE);
				mBtnHideRoomSelect.setVisibility(View.VISIBLE);
			}
		}

		@Override
		public void onItemDelete(View v, int mPos) {

		}

		@Override
		public void onItemUpdate(View v, int mPos, String newValue1) {

		}

	};

	private Runnable upLoadImageFiles = new Runnable() {
		@Override
		public void run() {
			LinkedList<File> fileList = new LinkedList<File>();
			for (int i = 0; i < finalImageName.size(); i++) {
				File file = GetMediaFile(finalImageName.get(i));
				if (file != null && file.exists()) {
					fileList.add(file);
				}
			}
			finalImageName.clear();
			if (fileList.size() > 0) {
				try {
					mFTP = new FTPUtil(5, 5, 5);
					mFTP.uploadMultiFile(fileList, Content.FTP_IMAGE_DIRECTORY, mUpLoadListener);
					mToast.show(Content.FTP_IMAGE_UPLOAD_SUCCESSS);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	};

	private UploadProgressListener mUpLoadListener = new UploadProgressListener() {
		@Override
		public void onUploadProgress(String currentStep, long uploadSize, File file) {
			if (currentStep.equals(Content.FTP_UPLOAD_SUCCESS)) {
				Log.e("LXM", "-----shanchuan--successful");
			} else if (currentStep.equals(Content.FTP_UPLOAD_LOADING)) {
				long fize = file.length();
				float num = (float) uploadSize / (float) fize;
				int result = (int) (num * 100);
				Log.e("LXM", "-----shangchuan---" + result + "%");
			} else if (currentStep.equals(Content.FTP_CONNECT_FAIL)) {

				mToast.show(Content.FTP_CONNECT_FAIL);
			}
		}
	};

	public static void onBackPressed() {
		mHandler.obtainMessage(Content.BACK_PRESSED).sendToTarget();
	}

	public void setRFIDResult(String result, boolean Right) {
		String ResultMsg = "";
		if (!Right) {
			ResultMsg = result;
		} else if (!result.substring(2, 4).equals(mBuilding.getText().toString())) {
			ResultMsg = "扫描结果：" + result + ", 楼栋错误";
		} else if (!judgeFloor(result.substring(4, 6))) {
			ResultMsg = "扫描结果：" + result + ", 楼层错误";
		} else {
			String Cardnum = result.substring(6);
			int pos = -1;
			for (int i = 0; i < mBeanList2.size(); i++) {
				if (mBeanList2.get(i).getCardNum().equals(Cardnum)) {
					pos = i;
					break;
				}
			}
			if (pos != -1) {
				mListView2.setSelection(pos);
			} else {
				// NewDetailTask(Cardnum);
			}
			return;
		}
		mToast.show(ResultMsg);
	}

	private boolean judgeFloor(String Floor) {
		int floor = 0;
		try {
			floor = Integer.parseInt(Floor);
		} catch (NumberFormatException e) {
		}
		if (floor < 1 && floor > 5) {
			return false;
		} else {
			return true;
		}
	}

}
