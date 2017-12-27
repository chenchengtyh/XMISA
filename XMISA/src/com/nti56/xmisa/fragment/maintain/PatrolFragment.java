package com.nti56.xmisa.fragment.maintain;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import com.nti56.xmisa.bean.AdapterBean;
import com.nti56.xmisa.bean.PatrolBean;
import com.nti56.xmisa.bean.PatrolDetailBean;
import com.nti56.xmisa.adapter.MyListView.OnRefreshListener;

import com.nti56.xmisa.R;
import com.nti56.xmisa.ImageActivity;
import com.nti56.xmisa.MainActivity;
import com.nti56.xmisa.adapter.MyListViewAdapter;
import com.nti56.xmisa.adapter.MyListViewAdapter.ListViewAdapterCallBack;
import com.nti56.xmisa.adapter.MySQLiteHelper;
import com.nti56.xmisa.adapter.MyListView;
import com.nti56.xmisa.bean.PostResult;
import com.nti56.xmisa.dialog.BuildingSelectDialog;
import com.nti56.xmisa.dialog.RemindDialog;
import com.nti56.xmisa.dialog.BuildingSelectDialog.mBuildingSelectDialogListener;
import com.nti56.xmisa.dialog.RemindDialog.mRemindDialogListener;
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
import android.view.View.OnLayoutChangeListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PatrolFragment extends Fragment implements OnClickListener, OnLayoutChangeListener {

	private static MyHandler mHandler;

	private MainActivity mContext;

	private FTPUtil mFTP;
	private HttpUtil http;
	private MyToast mToast;

	private RemindDialog remindDialog;
	private BuildingSelectDialog newTaskDialog;

	private File mediaStorageDir;
	private SQLiteDatabase sqlite;
	private MyListViewAdapter listViewAdapter1, listViewAdapter2;

	private int mDetailClickPos = -1;
	private boolean NewTask = false;

	private WeakReference<View> mRootView;
	private mPtlFragmentListener mListener;

	private RelativeLayout layout1;
	private LinearLayout layout2, layoutInfo;
	private MyListView mListView1, mListView2;
	private TextView mDate, mOrderID, mBuilding, mStateNC;
	private Button mBtn_new, mBtn_save, mBtn_back, mBtn_commit, mBtn_scan;

	private String NCState;
	private String mCurTaskID;
	private String mDeleteTask = "";
	private String TAG = "PatrolFragment";
	private String DB_TABLE = "patrol";
	private String DETAIL_TABLE = "patrol_detail";

	private ArrayList<String> finalImageName = new ArrayList<String>();

	private ArrayList<AdapterBean> mBeanList1 = new ArrayList<AdapterBean>();
	private ArrayList<AdapterBean> mBeanList2 = new ArrayList<AdapterBean>();

	private String sql_creat1 = "CREATE TABLE if not exists patrol(_taskid VARCHAR PRIMARY KEY, _date VARCHAR,"
			+ " _meridiem VARCHAR, _orderid VARCHAR, _building VARCHAR, _person VARCHAR, _statenc VARCHAR, _mark VARCHAR)";
	
	private String sql_creat2 = "CREATE TABLE if not exists patrol_detail(_taskid VARCHAR, _detailid VARCHAR PRIMARY KEY,"
			+ " _floor INT, _point VARCHAR, _result INT, _trouble VARCHAR, _picture VARCHAR, _solution VARCHAR,"
			+ " _repairdate VARCHAR, _picturever VARCHAR)";

	public interface mPtlFragmentListener {
		public void hideMenu(boolean hide);

		public void btnCancel();
	}

	public PatrolFragment(mPtlFragmentListener mListener) {
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
		Log.e("NTI", "PatrolFragment........onCreateView()");
		if (mRootView == null || mRootView.get() == null) {
			View v = inflater.inflate(R.layout.fragment_3_patrol, container, false);
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
		Log.e("NTI", "PatrolFragment........onHiddenChanged():" + hidden);
		if (!hidden && mRootView != null) {
			if (layout2.getVisibility() == View.VISIBLE) {
				mContext.setMyBackModel(Content.BACK_MODEL_PTL_DETAIL);
			} else {
				mContext.setMyBackModel(Content.BACK_MODEL_NONE);
			}
		}
		super.onHiddenChanged(hidden);
	}

	@Override
	public void onDestroy() {
		Log.e("NTI", "PatrolFragment........onDestroy()");
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		if (Content.InputSoftOpen) {
			return;
		}
		switch (v.getId()) {
		case R.id.btn_ptl_detail_back:// 返回
			ShowMainTable();
			break;

		case R.id.btn_ptl_detail_save:
			SaveDetailData();
			break;

		case R.id.btn_patrol_add: // 新建
			ShowNewTaskDialog();
			break;

		case R.id.btn_ptl_detail_commit: { // 反馈
			SaveDetailData();
			CommitData_Send();
		}
			break;

		case R.id.btn_ptl_detail_scan:
			mContext.scanRFID(this, "XC");
			break;

		default:
			break;
		}
	}

	private void CommitData_Send() {
		String Action = "UPDATE";
		String ImageUrl1, ImageUrl2, ImageUrl3, ImageUrl4;
		String TaskXML = "", DetailXML = "";
		Cursor cursor = sqlite.query(DB_TABLE, null, "_taskid = ?", new String[] { mCurTaskID }, null, null, null);
		if (cursor != null && cursor.moveToFirst()) {
			TaskXML = "<Task TaskId=\"" + cursor.getString(0) + "\" Date=\"" + cursor.getString(1) + "\" isMor=\""
					+ (cursor.getString(2).equals("上午") ? 0 : 1) + "\" PatrolinfoCode=\"" + cursor.getString(3) + "\" Ban=\""
					+ cursor.getString(4) + "\" Dispatcher=\"" + Content.USER_SYS_CODE + "\" > ";
			if (cursor.getString(7).equals(Content.DB_DATA_LOCAL)) {
				Action = "INSERT";
			}
			cursor.close();
		}
		cursor = null;

		Cursor cursor2 = sqlite.query(DETAIL_TABLE, null, "_taskid = ?", new String[] { mCurTaskID }, null, null, null);
		finalImageName.clear();
		if (cursor2 != null && cursor2.moveToFirst()) {
			do {
				String[] imageName = cursor2.getString(6).split("/");
				ImageUrl1 = (imageName[0].length() > 37) ? (Content.getFtpUrl() + imageName[0] + ".jpg") : " ";
				ImageUrl2 = (imageName[1].length() > 37) ? (Content.getFtpUrl() + imageName[1] + ".jpg") : " ";
				ImageUrl3 = (imageName[2].length() > 37) ? (Content.getFtpUrl() + imageName[2] + ".jpg") : " ";
				ImageUrl4 = (imageName[3].length() > 37) ? (Content.getFtpUrl() + imageName[3] + ".jpg") : " ";
				for (int i = 0; i < imageName.length; i++) {
					finalImageName.add(imageName[i]);
				}
				String Result = (cursor2.getString(4).equals("合格") ? "无" : "有");
				DetailXML += "<TaskDetail TaskId=\"" + cursor2.getString(0) + "\" DetailId=\"" + cursor2.getString(1) + "\" FloorLay=\""
						+ cursor2.getString(2) + "\" Project=\"" + cursor2.getString(3) + "\" Result=\"" + Result + "\" Danger=\""
						+ cursor2.getString(5) + "\" ImageUrl1=\"" + ImageUrl1 + "\" ImageUrl2=\"" + ImageUrl2 + "\" ImageUrl3=\""
						+ ImageUrl3 + "\" ImageUrl4=\"" + ImageUrl4 + "\" Rectification=\"" + cursor2.getString(7) + "\" RepairDate=\""
						+ cursor2.getString(8) + "\" /> ";
			} while (cursor2.moveToNext());
			cursor2.close();
		}
		cursor2 = null;
		final String UpdateXML = "<Request><Function>ckxcBack</Function><Action>" + Action + "</Action><Parms>" + TaskXML + DetailXML
				+ "</Task></Parms></Request>";
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (http == null) {
					http = HttpUtil.getInstance();
				}
				PostResult result = null;
				result = http.sendXML(UpdateXML);
				mHandler.obtainMessage(Content.SEND_XML_PTL_COMMIT, result).sendToTarget();
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

	private void DeleteData() {
		Cursor cursor = sqlite.query(DB_TABLE, null, "_taskid = ?", new String[] { mDeleteTask }, null, null, null);
		if (cursor != null && cursor.moveToFirst()) {
			if (cursor.getString(7).equals(Content.DB_DATA_LOCAL)) {
				sqlite.delete(DB_TABLE, "_taskid = ?", new String[] { mDeleteTask });
				UpDateListView1();
				sqlite.delete(DETAIL_TABLE, "_taskid = ?", new String[] { mDeleteTask });
				mDeleteTask = "";
				mToast.show("删除成功");
			} else {
				DeleteData_Send();
			}
			cursor.close();
		}
		cursor = null;
		remindDialog.dismiss();
	}

	private void DeleteData_Send() {
		String TaskXML = "", DetailXML = "";
		Cursor cursor = sqlite.query(DB_TABLE, null, "_taskid = ?", new String[] { mDeleteTask }, null, null, null);
		if (cursor != null && cursor.moveToFirst()) {
			TaskXML = "<Task TaskId=\"" + cursor.getString(0) + "\" Date=\"" + cursor.getString(1) + "\" isMor=\""
					+ (cursor.getString(2).equals("上午") ? 0 : 1) + "\" PatrolinfoCode=\"" + cursor.getString(3) + "\" Ban=\""
					+ cursor.getString(4) + "\" Dispatcher=\"" + Content.USER_SYS_CODE + "\" > ";
			cursor.close();
		}
		cursor = null;

		Cursor cursor2 = sqlite.query(DETAIL_TABLE, null, "_taskid = ?", new String[] { mDeleteTask }, null, null, null);
		if (cursor2 != null && cursor2.moveToFirst()) {
			do {
				DetailXML += "<TaskDetail TaskId=\"" + cursor2.getString(0) + "\" DetailId=\"" + cursor2.getString(1) + "\" FloorLay=\""
						+ cursor2.getString(2) + "\" Project=\"" + cursor2.getString(3) + "\" Result=\"无\" Danger=\""
						+ cursor2.getString(5) + "\" ImageUrl1=\" \" ImageUrl2=\" \" ImageUrl3=\" \" ImageUrl4=\" \" Rectification=\""
						+ cursor2.getString(7) + "\" RepairDate=\"" + cursor2.getString(8) + "\" /> ";
			} while (cursor2.moveToNext());
			cursor2.close();
		}
		cursor2 = null;
		final String DeleteXML = "<Request><Function>ckxcBack</Function><Action>DELETE</Action><Parms>" + TaskXML + DetailXML
				+ "</Task></Parms></Request>";
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (http == null) {
					http = HttpUtil.getInstance();
				}
				PostResult result = http.sendXML(DeleteXML);
				mHandler.obtainMessage(Content.SEND_XML_PTL_DELETE, result).sendToTarget();
			}
		}).start();
	}

	private void DeleteData_Rev(PostResult result) {
		String msg = (result.isResult()) ? "删除成功" : "删除失败";
		if (result.isResult()) {
			sqlite.delete(DB_TABLE, "_taskid = ?", new String[] { mDeleteTask });
			UpDateListView1();
			sqlite.delete(DETAIL_TABLE, "_taskid = ?", new String[] { mDeleteTask });
		}
		mDeleteTask = "";
		mToast.show(msg);
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

//	private void HideFloorRadioGroup() {
//		if (mFloorRadioGroup.getCheckedRadioButtonId() != -1) {
//			mFloorRadioGroup.clearCheck();
//		}
//		mFloorRadioGroup.setVisibility(View.GONE);
//	}

	private void InitLayout(View view) {
		if (mListView1 == null) {
			view.addOnLayoutChangeListener(this);

			mDate = (TextView) view.findViewById(R.id.tv_ptl_detail_date);
			mOrderID = (TextView) view.findViewById(R.id.tv_ptl_detail_orderid);
			mStateNC = (TextView) view.findViewById(R.id.tv_ptl_detail_statenc);
			mBuilding = (TextView) view.findViewById(R.id.tv_ptl_detail_building);

			mBtn_new = (Button) view.findViewById(R.id.btn_patrol_add);
			mBtn_save = (Button) view.findViewById(R.id.btn_ptl_detail_save);
			mBtn_back = (Button) view.findViewById(R.id.btn_ptl_detail_back);
			mBtn_scan = (Button) view.findViewById(R.id.btn_ptl_detail_scan);
			mBtn_commit = (Button) view.findViewById(R.id.btn_ptl_detail_commit);

			layout1 = (RelativeLayout) view.findViewById(R.id.layout_patrol);
			layout2 = (LinearLayout) view.findViewById(R.id.layout_ptl_detail);
			layoutInfo = (LinearLayout) view.findViewById(R.id.layout_ptl_detail_info);

			mBtn_new.setOnClickListener(this);
			mBtn_save.setOnClickListener(this);
			mBtn_back.setOnClickListener(this);
			mBtn_scan.setOnClickListener(this);
			mBtn_commit.setOnClickListener(this);
			mListView1 = (MyListView) view.findViewById(R.id.list_patrol);
			mListView2 = (MyListView) view.findViewById(R.id.list_ptl_detail);

			mListView1.setOnRefreshListener(refreshListener, Content.LIST_PATROL);
			mListView1.setAdapter(listViewAdapter1);
			mListView1.setOnItemClickListener(itemClicklistener1);
			mListView1.setOnItemLongClickListener(itemLongClicklistener1);
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
				bean.setMeridiem(cursor.getString(2));
				bean.setOrderID(cursor.getString(3));
				bean.setBuilding(cursor.getString(4));
				bean.setPerson(cursor.getString(5));
				bean.setNCState(cursor.getString(6));
				bean.setMark(cursor.getString(7));
				mBeanList1.add(bean);
			} while (cursor.moveToNext());
			cursor.close();
		}
		cursor = null;
		listViewAdapter1 = new MyListViewAdapter(mContext, R.layout.list_patrol, mBeanList1, Content.LIST_PATROL, null);
	}

	private void InitListViewAdapter2() {
		sqlite.execSQL(sql_creat2);
		listViewAdapter2 = new MyListViewAdapter(mContext, R.layout.list_ptl_detail, mBeanList2, Content.LIST_PATROL_DETAIL,
				listViewAdapterCallBack2);
		mListView2.setAdapter(listViewAdapter2);
	}

	private void InitNewTaskDialog() {
		newTaskDialog = new BuildingSelectDialog(mContext, R.style.dialog, newTaskDialogListener);
		Window dialogWindow = newTaskDialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		lp.x = 40;
		lp.y = 26;
		dialogWindow.setAttributes(lp);
		newTaskDialog.setCanceledOnTouchOutside(true);
	}

	private void InitRemindDialog() {
		remindDialog = new RemindDialog(mContext, R.style.dialog, Content.MODEL_DELETE, remindDialogListener);
		Window dialogWindow = remindDialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setAttributes(lp);
		remindDialog.setCanceledOnTouchOutside(true);
	}

	private void LoadData_Send() {
		final String xmlString = "<Request><Function>ckxc</Function><Parms><Username>" + Content.USER_SYS_CODE
				+ "</Username></Parms></Request>";
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (http == null) {
					http = HttpUtil.getInstance();
				}
				PostResult result = http.sendXML(xmlString);
				if (result != null) {
					mHandler.obtainMessage(Content.SEND_XML_PTL_LOAD, result).sendToTarget();
				}
			}
		}).start();

	}

	private void LoadData_Rev(PostResult result) {
		if (result.isResult()) {
			DeleteTable(DB_TABLE, sql_creat1);
			DeleteTable(DETAIL_TABLE, sql_creat2);
			UpdateSQLiteData(result.getPatrolBeanList());
			UpDateListView1();
		} else {
			mToast.show(result.getMessage());
		}
		mListView1.onRefreshComplete(result.isResult(), Content.LIST_PATROL);

	}

	private void NewPatrolTask(String building) {
		NewTask = true;
		int number = 1;
		for (int i = 0; i < mBeanList1.size(); i++) {
			if (mBeanList1.get(i).getOrderID().startsWith("新建巡查")) {
				int maxNum = Content.parseInt(mBeanList1.get(i).getOrderID().substring(6, 9));
				number = (maxNum >= number) ? maxNum + 1 : number;
			}
		}
		String Number = (number < 100) ? ((number < 10) ? "00" + number : "0" + number) : "" + number;
		String patrolid = "新建巡查单号" + Number;
		if (listViewAdapter2 == null) {
			InitListViewAdapter2();
		}
		mCurTaskID = UUID.randomUUID().toString().replace("-", "");
		mDate.setText(Content.getDate_Now());
		mOrderID.setText(patrolid);
		mBuilding.setText(building);
		NCState = Content.HTTP_UPLOAD_FALSE;
		mStateNC.setText(NCState);
		mBeanList2.clear();
		String[] points = new String[] { "仓库无漏水现象", "仓库无异常气味", "卫生清洁", "门窗完好", "烟箱无倾斜无破损" };
		for (int i = 0; i < 25; i++) {
			AdapterBean bean = new AdapterBean();
			bean.setPTaskID(mCurTaskID);
			bean.setTaskID(UUID.randomUUID().toString().replace("-", ""));
			bean.setFloor((i / 5 + 1) + "");
			bean.setPoint(points[i % 5]);
			bean.setResult("合格");
			bean.setTrouble("");
			bean.setPicture(" / / / ");
			bean.setSolution("");
			bean.setPictureVersion("");
			bean.setRepairDate("");
//			bean.setMark(Content.DB_DATA_TEMP);
			mBeanList2.add(bean);
		}
		listViewAdapter2.setNCState(false);
		listViewAdapter2.notifyDataSetChanged();
		mContext.setMyBackModel(Content.BACK_MODEL_PTL_DETAIL);
		newTaskDialog.dismiss();
		if (layout2.getVisibility() == View.GONE) {
			layout1.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_layout_left_hide));
			layout2.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_layout_left_show));
			layout1.setVisibility(View.GONE);
			layout2.setVisibility(View.VISIBLE);
		}
	}

	private void SaveDetailData() {
		ContentValues values = new ContentValues();
		if (NewTask) {
			values.put("_taskid", mCurTaskID);
			values.put("_date", mDate.getText().toString());
			values.put("_meridiem", Content.getMeridiem_Now());
			values.put("_orderid", mOrderID.getText().toString());
			values.put("_building", mBuilding.getText().toString());
			values.put("_person", Content.USER_NAME);
			values.put("_statenc", Content.HTTP_UPLOAD_FALSE);
			values.put("_mark", Content.DB_DATA_LOCAL);
			sqlite.insert(DB_TABLE, null, values);
		}

		for (int i = 0; i < mBeanList2.size(); i++) {
			values.clear();
			String DetailID = mBeanList2.get(i).getTaskID();
			String Result = mBeanList2.get(i).getResult();
			String Trouble = mBeanList2.get(i).getTrouble();
			String Picture = mBeanList2.get(i).getPicture();
			String Solution = mBeanList2.get(i).getSolution();
			String RepairDate = mBeanList2.get(i).getRepairDate();

			values.put("_result", Result);
			values.put("_trouble", Trouble);
			values.put("_picture", Picture);
			values.put("_solution", Solution);
			values.put("_repairdate", RepairDate);
			if (NewTask) {
				String Floor = mBeanList2.get(i).getFloor();
				String Point = mBeanList2.get(i).getPoint();
				values.put("_taskid", mCurTaskID);
				values.put("_detailid", DetailID);
				values.put("_floor", Floor);
				values.put("_point", Point);
				sqlite.insert(DETAIL_TABLE, null, values);
			} else {
				sqlite.update(DETAIL_TABLE, values, "_detailid = ?", new String[] { DetailID });
			}
		}
		NewTask = false;
		mToast.show("保存成功！");
		UpDateListView2();
	}

	private void ShowInputSoft() {
		if (mListener != null && Content.InputSoftOpen) {
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
			layout1.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_layout_right_show));
			layout2.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_layout_right_hide));
			layout1.setVisibility(View.VISIBLE);
			layout2.setVisibility(View.GONE);
		}
	}

	private void ShowDetailTable(int pos) {
		NewTask = false;
		if (listViewAdapter2 == null) {
			InitListViewAdapter2();
		}

		AdapterBean bean = mBeanList1.get(pos - 1);
		mCurTaskID = bean.getTaskID();
		mDate.setText(bean.getDate());
		mOrderID.setText(bean.getOrderID());
		mBuilding.setText(bean.getBuilding());
		NCState = bean.getNCState();
		mStateNC.setText(bean.getNCState());
		UpDateListView2();
		mContext.setMyBackModel(Content.BACK_MODEL_PTL_DETAIL);
		if (layout2.getVisibility() == View.GONE) {
			layout1.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_layout_left_hide));
			layout2.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_layout_left_show));
			layout1.setVisibility(View.GONE);
			layout2.setVisibility(View.VISIBLE);
		}
	}


	private void ShowRemindDialog() {
		if (remindDialog == null) {
			InitRemindDialog();
		}
		remindDialog.show();
	}

	private void ShowNewTaskDialog() {
		if (newTaskDialog == null) {
			InitNewTaskDialog();
		}
		newTaskDialog.show();
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
				bean.setMeridiem(cursor.getString(2));
				bean.setOrderID(cursor.getString(3));
				bean.setBuilding(cursor.getString(4));
				bean.setPerson(cursor.getString(5));
				bean.setNCState(cursor.getString(6));
				bean.setMark(cursor.getString(7));
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
				bean.setFloor(cursor.getString(2));
				bean.setPoint(cursor.getString(3));
				bean.setResult(cursor.getString(4));
				bean.setTrouble(cursor.getString(5));
				bean.setPicture(cursor.getString(6));
				bean.setSolution(cursor.getString(7));
				bean.setRepairDate(cursor.getString(8));
				bean.setPictureVersion(cursor.getString(9));
//				bean.setMark(cursor.getString(10));
				mBeanList2.add(bean);
			} while (cursor.moveToNext());
			cursor.close();
		}
		cursor = null;
		listViewAdapter2.setNCState(NCState.equals(Content.HTTP_UPLOAD_TRUE));
		listViewAdapter2.notifyDataSetChanged();
		mListView2.setSelection(0);
	}

	private void UpdateSQLiteData(List<PatrolBean> mTaskBeans) {
		sqlite.beginTransaction();
		ContentValues cv = new ContentValues();
		for (int i = 0; i < mTaskBeans.size(); i++) {
			cv.clear();
			cv.put("_taskid", mTaskBeans.get(i).getTaskId());
			cv.put("_date", mTaskBeans.get(i).getDate());
			cv.put("_meridiem", mTaskBeans.get(i).getMeridiem());
			cv.put("_orderid", mTaskBeans.get(i).getOrderId());
			cv.put("_building", mTaskBeans.get(i).getBuilding());
			cv.put("_person", mTaskBeans.get(i).getPerson());
			cv.put("_mark", mTaskBeans.get(i).getMark());
			if (mTaskBeans.get(i).getStateNC().equals("0")) {
				cv.put("_statenc", Content.HTTP_UPLOAD_FALSE);
			} else {
				cv.put("_statenc", Content.HTTP_UPLOAD_TRUE);
			}
			sqlite.insert(DB_TABLE, null, cv);
			List<PatrolDetailBean> details = mTaskBeans.get(i).getDetailBeanList();
			if (details != null && details.size() > 0) {
				for (int j = 0; j < details.size(); j++) {
					cv.clear();
					cv.put("_taskid", details.get(j).getTaskId());
					cv.put("_detailid", details.get(j).getDetailId());
					cv.put("_floor", details.get(j).getFloor());
					cv.put("_point", details.get(j).getPoint());
					cv.put("_result", details.get(j).getResult());
					cv.put("_trouble", details.get(j).getTrouble());
					cv.put("_picture", details.get(j).getPicture());
					cv.put("_solution", details.get(j).getSolution());
					cv.put("_repairdate", details.get(j).getRepairDate());
					cv.put("_picturever", details.get(j).getPictureVersion());
//					cv.put("_mark", details.get(i).getMark());
					sqlite.insert(DETAIL_TABLE, null, cv);
				}
			}
		}
		sqlite.setTransactionSuccessful();
		sqlite.endTransaction();
	}

	static class MyHandler extends Handler {
		WeakReference<PatrolFragment> mFragment;

		MyHandler(PatrolFragment patrolFragment) {
			mFragment = new WeakReference<PatrolFragment>(patrolFragment);
		}

		@Override
		public void handleMessage(Message msg) {
			PatrolFragment theFragment = mFragment.get();
			if (theFragment == null) {
				return;
			}
			switch (msg.what) {
			case Content.SEND_XML_PTL_LOAD:
				theFragment.LoadData_Rev((PostResult) msg.obj);
				break;

			case Content.SEND_XML_PTL_DELETE:
				theFragment.DeleteData_Rev((PostResult) msg.obj);
				break;

			case Content.SEND_XML_PTL_COMMIT:
				theFragment.CommitData_Rev((PostResult) msg.obj);
				break;

			case Content.BACK_PRESSED:
				theFragment.ShowMainTable();
				break;

			case Content.INPUT_SOFT_ACTION:
				theFragment.ShowInputSoft();
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

	private mRemindDialogListener remindDialogListener = new mRemindDialogListener() {

		@Override
		public void btnSure() {
			DeleteData();
		}

	};

	private OnItemLongClickListener itemLongClicklistener1 = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View v, int pos, long id) {
			String State = mBeanList1.get(pos - 1).getNCState();
			if (State.equals(Content.HTTP_UPLOAD_FALSE)) {
				mDeleteTask = mBeanList1.get(pos - 1).getTaskID();
				ShowRemindDialog();
			}
			return true;
		}
	};

	private mBuildingSelectDialogListener newTaskDialogListener = new mBuildingSelectDialogListener() {

		@Override
		public void btnStart(String building) {
			NewPatrolTask(building);
		}
	};

	private ListViewAdapterCallBack listViewAdapterCallBack2 = new ListViewAdapterCallBack() {

		@Override
		public void onItemClick(View v, int pos, AdapterBean bean) {
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
			// startActivity(intent);
			startActivityForResult(intent, Content.ACTIVITY_REQUEST_CODE_IMAGE);
			// 点击反馈之后，反馈成功则删除旧文件
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
			Log.e("LXM", currentStep);
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
		int floor = 0;
		if (!Right) {
			ResultMsg = result;
		} else if (!result.substring(2, 4).equals(mBuilding.getText().toString())) {
			Right = false;
			ResultMsg = "扫描结果：" + result + ", 楼栋错误";
		} else {
			try {
				floor = Integer.parseInt(result.substring(4, 6));
			} catch (NumberFormatException e) {
			}
			if (floor < 1 || floor > 5) {
				ResultMsg = "扫描结果：" + result + ", 楼层错误";
				Right = false;
			} else {
				ResultMsg = "扫描结果：" + result;
			}
		}
		if (Right) {
			mListView2.setSelection(floor * 5 - 4);
		}
		mToast.show(ResultMsg);
	}

}
