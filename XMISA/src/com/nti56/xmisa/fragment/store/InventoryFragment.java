package com.nti56.xmisa.fragment.store;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.nti56.xmisa.MainActivity;
import com.nti56.xmisa.R;
import com.nti56.xmisa.adapter.MyListView;
import com.nti56.xmisa.adapter.MyListViewAdapter;
import com.nti56.xmisa.adapter.MySQLiteHelper;
import com.nti56.xmisa.adapter.MyListView.OnRefreshListener;
import com.nti56.xmisa.adapter.MyListViewAdapter.ListViewAdapterCallBack;
import com.nti56.xmisa.bean.AdapterBean;
import com.nti56.xmisa.bean.InventoryBean;
import com.nti56.xmisa.bean.InventoryDetailBean;
import com.nti56.xmisa.bean.PostResult;
import com.nti56.xmisa.util.Content;
import com.nti56.xmisa.util.HttpUtil;
import com.nti56.xmisa.util.MyToast;
import com.nti56.xmisa.util.MyLog;

import android.app.Fragment;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class InventoryFragment extends Fragment implements OnClickListener, OnLayoutChangeListener {

	private static MyHandler mHandler;

	private MainActivity mContext;

	private HttpUtil http;
	private MyToast mToast;

	private SQLiteDatabase sqlite;
	private MyListViewAdapter listViewAdapter1, listViewAdapter2;

	private WeakReference<View> mRootView;
	private mIvtFragmentListener mListener;

	private EditText mFocusEditText;
	private HorizontalScrollView layout1;
	private LinearLayout layout2, layoutInfo;
	private MyListView mListView1, mListView2;
	private TextView mDate, mOrderID, mBuilding, mStateNC;
	private Button mBtn_save, mBtn_back, mBtn_commit, mBtn_addDetail;

	private String NCState;
	private String mCurTaskID;
	private String TAG = "InventoryFragment";
	private String DB_TABLE = "inventory";
	private String DETAIL_TABLE = "inventory_detail";

	private ArrayList<AdapterBean> mBeanList1 = new ArrayList<AdapterBean>();
	private ArrayList<AdapterBean> mBeanList2 = new ArrayList<AdapterBean>();
	private ArrayList<AdapterBean> mDetailDeleteList = new ArrayList<AdapterBean>();

	private String sql_creat1 = "CREATE TABLE if not exists inventory(_taskid VARCHAR PRIMARY KEY, _date VARCHAR,"
			+ " _orderid VARCHAR, _type VARCHAR, _building VARCHAR, _traytotle VARCHAR, _traydone VARCHAR,"
			+ " _ivtnum VARCHAR, _ivtdone VARCHAR, _ivtwait VARCHAR, _statenc VARCHAR)";

	private String sql_creat2 = "CREATE TABLE if not exists inventory_detail(_taskid VARCHAR, _detailid VARCHAR PRIMARY KEY,"
			+ " _trayid VARCHAR, _goodsid VARCHAR, _stocknum VARCHAR, _stockweight VARCHAR, _ivtnum VARCHAR, _ivtweight VARCHAR,"
			+ " _rate VARCHAR, _date VARCHAR, _mark VARCHAR)";

	public interface mIvtFragmentListener {
		public void hideMenu(boolean hide);

		public void btnCancel();
	}

	public InventoryFragment(mIvtFragmentListener mListener) {
		this.mListener = mListener;
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
		MyLog.e(TAG, "InventoryFragment........onCreateView()");
		if (mRootView == null || mRootView.get() == null) {
			View v = inflater.inflate(R.layout.fragment_1_inventory, container, false);
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
		MyLog.e(TAG, "InventoryFragment........onHiddenChanged():" + hidden);
		if (!hidden && mRootView != null) {
			if (layout2.getVisibility() == View.VISIBLE) {
				mContext.setMyBackModel(Content.BACK_MODEL_IVT_DETAIL);
			} else {
				mContext.setMyBackModel(Content.BACK_MODEL_NONE);
			}
		}
		super.onHiddenChanged(hidden);
	}

	@Override
	public void onDestroy() {
		MyLog.e(TAG, "InventoryFragment........onDestroy()");
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		if (Content.InputSoftOpen) {
			return;
		}
		switch (v.getId()) {
		case R.id.btn_ivt_detail_back:// 返回
			ShowMainTable();
			break;

		case R.id.btn_ivt_detail_save:// 保存
			SaveDetailData();
			break;

		case R.id.btn_ivt_detail_commit: { // 反馈
			SaveDetailData();
			CommitData_Send();
		}
			break;
//
//		case R.id.btn_ivt_detail_scan:
//			mContext.scanRFID(this, "CQ");
//			break;
//
//		case R.id.btn_ivt_detail_floor:
//			if (mFloorRadioGroup.getVisibility() == View.GONE) {
//				ShowFloorRadioGroup();
//			} else {
//				HideFloorRadioGroup();
//			}
//			break;

		case R.id.btn_ivt_detail_addlist:
			// 新增子表记录
			if (NCState.equals(Content.HTTP_UPLOAD_FALSE)) {
				NewDetailTask("");
			}
			break;

		default:
			break;
		}
	}

	private void CommitData_Send() {
		String TaskXML = "", DetailXML = "", DelDetailXML = "";
		Cursor cursor = sqlite.query(DB_TABLE, null, "_taskid = ?", new String[] { mCurTaskID }, null, null, null);
		if (cursor != null && cursor.moveToFirst()) {
			TaskXML = "<Task taskId=\"" + cursor.getString(0) + "\" createtime=\"" + cursor.getString(1) + "\" tasknumber=\""
					+ cursor.getString(2) + "\" tasktype=\"" + cursor.getString(3) + "\" ban=\"" + cursor.getString(4) + "\" ttlloc=\""
					+ cursor.getString(5) + "\" TtlLoccounted=\"" + cursor.getString(6) + "\" totalqty=\"" + cursor.getString(7)
					+ "\" finishNum=\"" + cursor.getString(8) + "\" waitNum=\"" + cursor.getString(9) + "\" > ";
			cursor.close();
		}
		cursor = null;

		Cursor cursor2 = sqlite.query(DETAIL_TABLE, null, "_taskid = ?", new String[] { mCurTaskID }, null, null, null);
		if (cursor2 != null && cursor2.moveToFirst()) {
			do {

				if (cursor2.getString(10).equals(Content.DB_NEED_DELETE)) {
					DelDetailXML += "<TaskDetail TaskId=\"" + cursor2.getString(0) + "\" detailId=\"" + cursor2.getString(1)
							+ "\" locationcode=\"" + cursor2.getString(2) + "\" itemcode=\"" + cursor2.getString(3)
							+ "\" qty=\"" + cursor2.getString(4) + "\" weight=\"" + cursor2.getString(5)
							+ "\" qty2=\"" + cursor2.getString(6) + "\" weight2=\"" + cursor2.getString(7)
							+ "\" rate=\"" + cursor2.getString(8) + "\" CountDate=\"" + cursor2.getString(9) + "\" /> ";
				} else {
					DetailXML += "<TaskDetail TaskId=\"" + cursor2.getString(0) + "\" detailId=\"" + cursor2.getString(1)
							+ "\" locationcode=\"" + cursor2.getString(2) + "\" itemcode=\"" + cursor2.getString(3)
							+ "\" qty=\"" + cursor2.getString(4) + "\" weight=\"" + cursor2.getString(5)
							+ "\" qty2=\"" + cursor2.getString(6) + "\" weight2=\"" + cursor2.getString(7)
							+ "\" rate=\"" + cursor2.getString(8) + "\" CountDate=\"" + cursor2.getString(9) + "\" /> ";
				}
			} while (cursor2.moveToNext());
			cursor2.close();
		}
		cursor2 = null;
		final String DeleteXML = (DelDetailXML == "") ? null
				: ("<Request><Function>inventoryBackNC</Function><Action>DELETEDETAIL</Action><Parms>" + TaskXML + DelDetailXML + "</Task></Parms></Request>");
		final String UpdateXML = "<Request><Function>cqjcBack</Function><Action>UPDATE</Action><Parms>" + TaskXML + DetailXML
				+ "</Task></Parms></Request>";
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (http == null) {
					http = HttpUtil.getInstance();
				}
				PostResult result = null;
				if (DeleteXML != null) {
					result = http.sendXML(DeleteXML);
				}
				if (result == null || result.isResult()) {
					result = http.sendXML(UpdateXML);
				}
				mHandler.obtainMessage(Content.SEND_XML_IVT_COMMIT, result).sendToTarget();
			}
		}).start();
	}

	private void CommitData_Rev(PostResult result) {// TODO,反馈成功需删除Content.DB_NEED_DELETE
		if (result.isResult()) {
			ContentValues values = new ContentValues();
			values.put("_statenc", Content.HTTP_UPLOAD_TRUE);
			values.put("_mark", Content.DB_DATA_SERVER);
			sqlite.update(DB_TABLE, values, "_taskid = ?", new String[] { mCurTaskID });
			sqlite.delete(DETAIL_TABLE, "_taskid = ? and _mark = ?", new String[] { mCurTaskID, Content.DB_NEED_DELETE });
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

	private void InitLayout(View view) {
		if (mListView1 == null) {
			view.addOnLayoutChangeListener(this);

			mDate = (TextView) view.findViewById(R.id.tv_ivt_detail_date);
			mOrderID = (TextView) view.findViewById(R.id.tv_ivt_detail_orderid);
			mStateNC = (TextView) view.findViewById(R.id.tv_ivt_detail_statenc);
			mBuilding = (TextView) view.findViewById(R.id.tv_ivt_detail_building);


			mBtn_save = (Button) view.findViewById(R.id.btn_ivt_detail_save);
			mBtn_back = (Button) view.findViewById(R.id.btn_ivt_detail_back);
			mBtn_commit = (Button) view.findViewById(R.id.btn_ivt_detail_commit);
			mBtn_addDetail = (Button) view.findViewById(R.id.btn_ivt_detail_addlist);

			layout1 = (HorizontalScrollView) view.findViewById(R.id.layout_inventory);
			layout2 = (LinearLayout) view.findViewById(R.id.layout_ivt_detail);
			layoutInfo = (LinearLayout) view.findViewById(R.id.layout_ivt_detail_info);

			mBtn_save.setOnClickListener(this);
			mBtn_back.setOnClickListener(this);
			mBtn_commit.setOnClickListener(this);
			mBtn_addDetail.setOnClickListener(this);

			mListView1 = (MyListView) view.findViewById(R.id.list_inventory);
			mListView2 = (MyListView) view.findViewById(R.id.list_ivt_detail);

			mListView1.setOnRefreshListener(refreshListener, Content.LIST_INVENTORY);
			mListView1.setAdapter(listViewAdapter1);
			mListView1.setOnItemClickListener(itemClicklistener1);
			mContext.setMyBackModel(Content.BACK_MODEL_NONE);
		}
	}
//	private String sql_creat1 = "CREATE TABLE if not exists inventory(_taskid VARCHAR PRIMARY KEY, _date VARCHAR,"
//	+ " _orderid VARCHAR, _type VARCHAR, _building VARCHAR, _ivtstate VARCHAR, _traytotle VARCHAR,"
//	+ " _traydone VARCHAR, _ivtnum VARCHAR, _ivtdone VARCHAR, _ivtwait VARCHAR, _statenc VARCHAR)";
//
//private String sql_creat2 = "CREATE TABLE if not exists inventory_detail(_taskid VARCHAR, _detailid VARCHAR PRIMARY KEY,"
//	+ " _trayname VARCHAR, _goodsid VARCHAR, _stocknum VARCHAR, _stockweight VARCHAR, _ivtnum VARCHAR, _ivtweight VARCHAR,"
//	+ " _rate VARCHAR, _ivttime VARCHAR)";

	private void InitListViewAdapter1() {
		sqlite.execSQL(sql_creat1);
		Cursor cursor = sqlite.query(DB_TABLE, null, null, null, null, null, null);
		if (cursor != null && cursor.moveToFirst()) {
			do {
				AdapterBean bean = new AdapterBean();
				bean.setTaskID(cursor.getString(0));
				bean.setDate(cursor.getString(1));
				bean.setOrderID(cursor.getString(2));
				bean.setType(cursor.getString(3));
				bean.setBuilding(cursor.getString(4));
				bean.setTrayTotle(cursor.getString(5));
				bean.setTrayDone(cursor.getString(6));
				bean.setIvtNum(cursor.getString(7));
				bean.setIvtDone(cursor.getString(8));
				bean.setIvtWait(cursor.getString(9));
				bean.setNCState(cursor.getString(10));
				mBeanList1.add(bean);
			} while (cursor.moveToNext());
			cursor.close();
		}
		cursor = null;
		listViewAdapter1 = new MyListViewAdapter(mContext, R.layout.list_inventory, mBeanList1, Content.LIST_INVENTORY, null);
	}

	private void InitListViewAdapter2() {
		sqlite.execSQL(sql_creat2);
		listViewAdapter2 = new MyListViewAdapter(mContext, R.layout.list_ivt_detail, mBeanList2, Content.LIST_INVENTORY_DETAIL,
				listViewAdapterCallBack2);
		mListView2.setAdapter(listViewAdapter2);
	}

	private void LoadData_Send() {
		final String xmlString = "<Request><Function>inventoryTask</Function><Parms><Username>" + Content.USER_SYS_CODE
				+ "</Username></Parms></Request>";
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (http == null) {
					http = HttpUtil.getInstance();
				}
				PostResult result = http.sendXML(xmlString);
				if (result != null) {
					mHandler.obtainMessage(Content.SEND_XML_IVT_LOAD, result).sendToTarget();
				}
			}
		}).start();

	}

	private void LoadData_Rev(PostResult result) {
		if (result.isResult()) {
			DeleteTable(DB_TABLE, sql_creat1);
			DeleteTable(DETAIL_TABLE, sql_creat2);
			UpdateSQLiteData(result.getInventoryBeanList());
			UpDateListView1();
		} else {
			mToast.show(result.getMessage());
		}
		mListView1.onRefreshComplete(result.isResult(), Content.LIST_INVENTORY);

	}

	private void NewDetailTask(String cardnum) {
		AdapterBean bean = new AdapterBean();
		bean.setPTaskID(mCurTaskID);
		String guid = UUID.randomUUID().toString().replace("-", "");
		bean.setTaskID(guid);
		bean.setTrayID("");
		bean.setGoodsID("");
//		bean.setGoodsName("");
		bean.setStockNum("0");
		bean.setStockWeight("0");
		bean.setIvtNum("0");
		bean.setIvtWeight("0");
		bean.setRate("200");
		bean.setDate("");
		bean.setMark(Content.DB_DATA_TEMP);
		mBeanList2.add(bean);
		listViewAdapter2.notifyDataSetChanged();
	}

	private void SaveDetailData() {
		ContentValues values = new ContentValues();

		for (int i = 0; i < mBeanList2.size(); i++) {
			values.clear();
			String TaskID = mBeanList2.get(i).getPTaskID();
			String DetailID = mBeanList2.get(i).getTaskID();
			String TrayID = mBeanList2.get(i).getTrayID();
			String GoodsID = mBeanList2.get(i).getGoodsID();
			String StockNum = mBeanList2.get(i).getStockNum();
			String StockWeight = mBeanList2.get(i).getStockWeight();
			String IvtNum = mBeanList2.get(i).getIvtNum();
			String IvtWeight = mBeanList2.get(i).getIvtWeight();
			String Rate = mBeanList2.get(i).getRate();
			String Date = mBeanList2.get(i).getDate();
			String Mark = mBeanList2.get(i).getMark();

			values.put("_trayid", TrayID);
			values.put("_goodsid", GoodsID);
			values.put("_stocknum", StockNum);
			values.put("_stockweight", StockWeight);
			values.put("_ivtnum", IvtNum);
			values.put("_ivtweight", IvtWeight);
			values.put("_rate", Rate);
			values.put("_date", Date);
			values.put("_mark", Mark);

			if (Mark.equals(Content.DB_DATA_TEMP)) {
				values.put("_taskid", TaskID);
				values.put("_detailid", DetailID);
				values.put("_mark", Content.DB_DATA_LOCAL);
				sqlite.insert(DETAIL_TABLE, null, values);
			} else {
				sqlite.update(DETAIL_TABLE, values, "_detailid = ?", new String[] { DetailID });
			}
		}
		for (int i = 0; i < mDetailDeleteList.size(); i++) {
			String DetailTaskID = mDetailDeleteList.get(i).getTaskID();
			if (mDetailDeleteList.get(i).getMark().equals(Content.DB_DATA_LOCAL)) {
				sqlite.delete(DETAIL_TABLE, "_detailid = ?", new String[] { DetailTaskID });
			} else {
				values.put("_mark", Content.DB_NEED_DELETE);
				sqlite.update(DETAIL_TABLE, values, "_detailid = ?", new String[] { DetailTaskID });
				values.clear();
			}
		}
		mDetailDeleteList.clear();
		mToast.show("保存成功！");
		UpDateListView2();
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
//		UpDateListView1();
//		mDetailDeleteList.clear();
		mContext.setMyBackModel(Content.BACK_MODEL_NONE);
		if (layout1.getVisibility() == View.GONE) {
			layout1.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_layout_right_show));
			layout2.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_layout_right_hide));
			layout1.setVisibility(View.VISIBLE);
			layout2.setVisibility(View.GONE);
		}
	}

	private void ShowDetailTable(int pos) {
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
		mContext.setMyBackModel(Content.BACK_MODEL_IVT_DETAIL);
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
				bean.setType(cursor.getString(3));
				bean.setBuilding(cursor.getString(4));
				bean.setTrayTotle(cursor.getString(5));
				bean.setTrayDone(cursor.getString(6));
				bean.setIvtNum(cursor.getString(7));
				bean.setIvtDone(cursor.getString(8));
				bean.setIvtWait(cursor.getString(9));
				bean.setNCState(cursor.getString(10));
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
				if (!cursor.getString(10).equals(Content.DB_NEED_DELETE)) {
					AdapterBean bean = new AdapterBean();
					bean.setPTaskID(cursor.getString(0));
					bean.setTaskID(cursor.getString(1));
					bean.setTrayID(cursor.getString(2));
					bean.setGoodsID(cursor.getString(3));
//					bean.setGoodsName(cursor.getString(4));
					bean.setStockNum(cursor.getString(4));
					bean.setStockWeight(cursor.getString(5));
					bean.setIvtNum(cursor.getString(6));
					bean.setIvtWeight(cursor.getString(7));
					bean.setRate(cursor.getString(8));
					bean.setDate(cursor.getString(9));
					bean.setMark(cursor.getString(10));
					mBeanList2.add(bean);
				}
			} while (cursor.moveToNext());
			cursor.close();
		}
		cursor = null;
		listViewAdapter2.setNCState(NCState.equals(Content.HTTP_UPLOAD_TRUE));
		listViewAdapter2.notifyDataSetChanged();
		mListView2.setSelection(0);
	}

	private void UpdateSQLiteData(List<InventoryBean> mTaskBeans) {
		sqlite.beginTransaction();
		ContentValues cv = new ContentValues();
		MyLog.e(TAG, "mTaskBeans.size = " + mTaskBeans.size());
		for (int i = 0; i < mTaskBeans.size(); i++) {
			cv.clear();
			cv.put("_taskid", mTaskBeans.get(i).getTaskId());
			cv.put("_date", mTaskBeans.get(i).getDate());
			cv.put("_orderid", mTaskBeans.get(i).getOrderId());
			cv.put("_type", mTaskBeans.get(i).getType());
			cv.put("_building", mTaskBeans.get(i).getBuilding());
			cv.put("_traytotle", mTaskBeans.get(i).getTrayTotle());
			cv.put("_traydone", mTaskBeans.get(i).getTrayDone());
			cv.put("_ivtnum", mTaskBeans.get(i).getIvtNum());
			cv.put("_ivtdone", mTaskBeans.get(i).getIvtDone());
			cv.put("_ivtwait", mTaskBeans.get(i).getIvtWait());
			cv.put("_statenc", mTaskBeans.get(i).getStateNC());
			sqlite.insert(DB_TABLE, null, cv);
			List<InventoryDetailBean> details = mTaskBeans.get(i).getDetailBeanList();
			if (details != null && details.size() > 0) {
				for (int j = 0; j < details.size(); j++) {
					cv.clear();
					cv.put("_taskid", details.get(j).getTaskId());
					cv.put("_detailid", details.get(j).getDetailId());
					cv.put("_trayid", details.get(j).getTrayID());
					cv.put("_goodsid", details.get(j).getGoodsID());
//					cv.put("_goodsname", details.get(j).getGoodsName());
					cv.put("_stocknum", details.get(j).getStockNum());
					cv.put("_stockweight", details.get(j).getStockWeight());
					cv.put("_ivtnum", details.get(j).getIvtNum());
					cv.put("_ivtweight", details.get(j).getIvtWeight());
					cv.put("_rate", details.get(j).getRate());
					cv.put("_date", details.get(j).getDate());
					cv.put("_mark", details.get(j).getMark());
					sqlite.insert(DETAIL_TABLE, null, cv);
				}
			}
		}
		sqlite.setTransactionSuccessful();
		sqlite.endTransaction();
	}

	private static class MyHandler extends Handler {
		WeakReference<InventoryFragment> mFragment;

		MyHandler(InventoryFragment inventoryFragment) {
			mFragment = new WeakReference<InventoryFragment>(inventoryFragment);
		}

		@Override
		public void handleMessage(Message msg) {
			InventoryFragment theFragment = mFragment.get();
			if (theFragment == null) {
				return;
			}
			switch (msg.what) {
			case Content.SEND_XML_IVT_LOAD:
				theFragment.LoadData_Rev((PostResult) msg.obj);
				break;

			case Content.SEND_XML_IVT_COMMIT:
				theFragment.CommitData_Rev((PostResult) msg.obj);
				break;

			case Content.BACK_PRESSED:
				theFragment.ShowMainTable();
				break;

			case Content.INPUT_SOFT_ACTION:
				theFragment.ShowInputSoft();
				break;

//			case Content.JUDGE_EDITTEXT_FOCUS:
//				theFragment.JudgeEditTextFocus();
//				break;

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

		}

		@Override
		public void onItemDelete(View v, int mPos) {
			MyLog.e(TAG, "onItemDelete","---------onItemDelete().mPos = " + mPos);
			AdapterBean removeBean = mBeanList2.get(mPos);
			mDetailDeleteList.add(removeBean);
			mBeanList2.remove(mPos);
			listViewAdapter2.notifyDataSetChanged();
		}

		@Override
		public void onItemUpdate(View v, int mPos, String newValue1) {

		}

	};

	public static void onBackPressed() {
		mHandler.obtainMessage(Content.BACK_PRESSED).sendToTarget();
	}

}
