package com.nti56.xmisa.fragment.store;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import com.nti56.xmisa.bean.AdapterBean;
import com.nti56.xmisa.bean.LossRecordBean;
import com.nti56.xmisa.bean.ReceiptBean;
import com.nti56.xmisa.bean.ReceiptDetailBean;
import com.nti56.xmisa.adapter.MyListView.OnRefreshListener;

import com.nti56.xmisa.MainActivity;
import com.nti56.xmisa.R;
import com.nti56.xmisa.adapter.MyListViewAdapter;
import com.nti56.xmisa.adapter.MyListViewAdapter.ListViewAdapterCallBack;
import com.nti56.xmisa.adapter.MySQLiteHelper;
import com.nti56.xmisa.adapter.MyListView;
import com.nti56.xmisa.bean.PostResult;
import com.nti56.xmisa.dialog.RoadLostDialog;
import com.nti56.xmisa.dialog.RoadLostDialog.mLostDialogListener;
import com.nti56.xmisa.util.Content;
import com.nti56.xmisa.util.HttpUtil;
import com.nti56.xmisa.util.MyLog;
import com.nti56.xmisa.util.MyToast;

import android.app.Fragment;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ReceiveFragment extends Fragment implements OnClickListener, OnLayoutChangeListener {

	private static MyHandler mHandler;

	private MainActivity mContext;

	private HttpUtil http;
	private MyToast mToast;

	private SQLiteDatabase sqlite;
	private MyListViewAdapter listViewAdapter1, listViewAdapter2;

	private int mCurDetailPos = -1;

	private WeakReference<View> mRootView;
	private mRecFragmentListener mListener;

	private RelativeLayout layout1;
	private LinearLayout layout2, layout2Info;
	private MyListView mListView1, mListView2;
	private TextView mOrderID, mPCNum, mType, mCarNum, mStateNC, mProvider;
	private Button mDetail_back, mDetail_commit, mDetail_save;

	private String NCState;

	private String mCurTaskID;
	private String DB_TABLE = "receive";
	private String DETAIL_TABLE = "receive_detail";
	private String THIRD_TABLE = "receive_loss";
	private String TAG = "ReceiveFragment";

	private ArrayList<AdapterBean> mBeanList1 = new ArrayList<AdapterBean>();
	private ArrayList<AdapterBean> mBeanList2 = new ArrayList<AdapterBean>();

//	private String sql_creat1 = "CREATE TABLE if not exists receive(_taskid VARCHAR PRIMARY KEY, _orderid VARCHAR, _type VARCHAR,"
//			+ " _pcnum VARCHAR, _plannum INT, _statenc VARCHAR, _carnum VARCHAR, _provider VARCHAR)";
	
	private String sql_creat1 = "CREATE TABLE if not exists receive(_taskid VARCHAR PRIMARY KEY, _date VARCHAR, _orderid VARCHAR, "
			+ " _type VARCHAR, _plannum VARCHAR, _pcnum VARCHAR, _building VARCHAR, _carnum VARCHAR,"
			+ "_statenc VARCHAR, _provider VARCHAR)";
	
	private String sql_creat2 = "CREATE  TABLE if not exists receive_detail(_taskid VARCHAR, _detailid VARCHAR PRIMARY KEY,"
			+ " _goodsid VARCHAR, _goodsname VARCHAR, _spec FLOAT, _plannum INT, _factnum INT, _planweight FLOAT,"
			+ " _lossweight FLOAT, _factweight FLOAT, _finish VARCHAR)";

	private String sql_creat3 = "CREATE  TABLE if not exists receive_loss(_taskid VARCHAR, _detailid VARCHAR, _lossid VARCHAR PRIMARY KEY,"
			+ " _lossweight FLOAT, _spec FLOAT, _rate FLOAT, _type VARCHAR, _barcode VARCHAR, _mark VARCHAR)";

	private RoadLostDialog roadLostDialog;

	public interface mRecFragmentListener {
		public void hideMenu(boolean hide);

		public void btnCancel();
	}

	public ReceiveFragment(mRecFragmentListener mListener) {
		// TODO Auto-generated constructor stub
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
		MyLog.d(TAG, "onCreateView");
		if (mRootView == null || mRootView.get() == null) {
			View v = inflater.inflate(R.layout.fragment_1_receipt, container, false);
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
		boolean isDialogLayout = (roadLostDialog != null && roadLostDialog.isShowing());
		if (oldBottom != 0 && bottom != 0 && (oldBottom - bottom > 200)) {
			Content.InputSoftOpen = true;
		} else if (oldBottom != 0 && bottom != 0 && (bottom - oldBottom > 200)) {
			Content.InputSoftOpen = false;
		} else {
			return;
		}
		Message msg = mHandler.obtainMessage(Content.INPUT_SOFT_ACTION);
		msg.obj = isDialogLayout;
		mHandler.sendMessageDelayed(msg, 100);
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		MyLog.d(TAG, "onHiddenChanged", "hidden  = " + hidden);
		if (!hidden && mRootView != null) {
			if (layout2.getVisibility() == View.VISIBLE) {
				mContext.setMyBackModel(Content.BACK_MODEL_REC_DETAIL);
			} else {
				mContext.setMyBackModel(Content.BACK_MODEL_NONE);
			}
		}
		super.onHiddenChanged(hidden);
	}

	@Override
	public void onDestroy() {
		MyLog.d(TAG, "onDestroy");
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		if (Content.InputSoftOpen) {
			return;
		}
		switch (v.getId()) {
		case R.id.btn_rec_detail_back:// 返回
			ShowMainTable();
			break;
		case R.id.btn_rec_detail_save: // 保存
			if(NCState.equals(Content.HTTP_UPLOAD_TRUE)){
				mToast.show("已反馈任务不能修改！");
			} else {
				SaveDetailData();
				mToast.show("成功保存至本地！");
			}
			break;

		case R.id.btn_rec_detail_commit: // 反馈
			SaveDetailData();
			CommitData_Send();
			break;
		default:
			break;
		}
	}

	private void BackPressed() {
		if (layout2.getVisibility() == View.VISIBLE) {
			ShowMainTable();
		}
	}

	private void CommitData_Send() {
		String Action = "UPDATE";
		String TaskXML = "", DetailXML = "", ThirdXML = "";
		String DelDetailXML = "", DelThirdXML = "";
		String Detail_Start = "";

		Cursor cursor = sqlite.query(DB_TABLE, null, "_taskid = ?", new String[] { mCurTaskID }, null, null, null);
		if (cursor != null && cursor.moveToFirst()) {
			TaskXML = "<Task TaskId=\"" + cursor.getString(0) + "\" TaskNumber=\"" + cursor.getString(2) + "\" typename=\""
					+ cursor.getString(3) + "\" total=\"" + cursor.getString(4) + "\" computerCode=\"" + cursor.getString(5)
					+ "\" carNum=\"" + cursor.getString(7) + "\" companyName=\"" + cursor.getString(9) + "\" > ";
			cursor.close();
		}
		cursor = null;

		Cursor cursor2 = sqlite.query(DETAIL_TABLE, null, "_taskid = ?", new String[] { mCurTaskID }, null, null, null);
		if (cursor2 != null && cursor2.moveToFirst()) {
			do {
				Detail_Start = "<TaskDetail taskId=\"" + mCurTaskID + "\" detailId=\"" + cursor2.getString(1) + "\" itemcode=\""
						+ cursor2.getString(2) + "\" goodsName=\"" + cursor2.getString(3) + "\" spec=\"" + cursor2.getString(4)
						+ "\" receivable=\"" + cursor2.getString(5) + "\" numberFact=\"" + cursor2.getString(6) + "\" weight=\""
						+ cursor2.getString(7) + "\" weightLoss=\"" + cursor2.getString(8) + "\" weightFact=\"" + cursor2.getString(9)
						+ "\" > ";// 明细表xml上部分
				ThirdXML = "";
				DelThirdXML = "";
				String DetailID = cursor2.getString(1);

				Cursor cursor3 = sqlite.query(THIRD_TABLE, null, "_detailid = ?", new String[] { DetailID }, null, null, null);
				if (cursor3 != null && cursor3.moveToFirst()) {
					do {
						String LossWeightType = Content.getLossWeightType(cursor3.getString(6));
						if (cursor3.getString(8).equals(Content.DB_NEED_DELETE)) {
							DelThirdXML += "<LossRecord  TaskLossId=\"" + cursor3.getString(2) + "\" weightLoss=\""
									+ cursor3.getString(3) + "\" spec=\"" + cursor3.getString(4) + "\" rate=\"" + cursor3.getString(5)
									+ "\" LossWeightType=\"" + LossWeightType + "\" Code=\"" + cursor3.getString(7) + "\" /> ";// 需删除的子子表xml
						} else {
							ThirdXML += "<LossRecord  TaskLossId=\"" + cursor3.getString(2) + "\" weightLoss=\""
									+ cursor3.getString(3) + "\" spec=\"" + cursor3.getString(4) + "\" rate=\"" + cursor3.getString(5)
									+ "\" LossWeightType=\"" + LossWeightType + "\" Code=\"" + cursor3.getString(7) + "\" /> ";// 需删除的子子表xml
						}
					} while (cursor3.moveToNext());
					cursor3.close();
				}
				cursor3 = null;

				DetailXML += Detail_Start + ThirdXML + " </TaskDetail> ";// 明细表XML。
				if (!DelThirdXML.equals("")) {
					DelDetailXML += Detail_Start + DelThirdXML + " </TaskDetail> ";// 需删除的明细表XML。
				}
			} while (cursor2.moveToNext());
			cursor2.close();
		}
		cursor2 = null;

		final String DeleteXML = (DelDetailXML == "") ? null
				: ("<Request><Function>receiptBackNC</Function><Action>DELETEDETAIL</Action><Parms>" + TaskXML + DelDetailXML + "</Task></Parms></Request>");
		final String UpdateXML = "<Request><Function>receiptBackNC</Function><Action>" + Action + "</Action><Parms>" + TaskXML
				+ DetailXML + "</Task></Parms></Request>";
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
				mHandler.obtainMessage(Content.SEND_XML_REC_COMMIT, result).sendToTarget();
			}
		}).start();
	}

	private void CommitData_Rev(PostResult result) {// TODO,1反馈成功需判断图片是否需要上传，2反馈成功需删除Content.DB_NEED_DELETE
		if (result.isResult()) {
			ContentValues values = new ContentValues();
			values.put("_statenc", Content.HTTP_UPLOAD_TRUE);
			sqlite.update(DB_TABLE, values, "_taskid = ?", new String[] { mCurTaskID });
			sqlite.delete(THIRD_TABLE, "_taskid = ? and _mark = ?", new String[] { mCurTaskID, Content.DB_NEED_DELETE });
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

			mOrderID = (TextView) view.findViewById(R.id.tv_rec_detail_taskid);
			mType = (TextView) view.findViewById(R.id.tv_rec_detail_type);
			mPCNum = (TextView) view.findViewById(R.id.tv_rec_detail_pcnum);
			mProvider = (TextView) view.findViewById(R.id.tv_rec_detail_provider);
			mCarNum = (TextView) view.findViewById(R.id.tv_rec_detail_carnum);
			mStateNC = (TextView) view.findViewById(R.id.tv_rec_detail_statenc);

			mDetail_save = (Button) view.findViewById(R.id.btn_rec_detail_save);
			mDetail_back = (Button) view.findViewById(R.id.btn_rec_detail_back);
			mDetail_commit = (Button) view.findViewById(R.id.btn_rec_detail_commit);

			layout1 = (RelativeLayout) view.findViewById(R.id.layout_receive);
			layout2 = (LinearLayout) view.findViewById(R.id.layout_receive_detail);

			layout2Info = (LinearLayout) view.findViewById(R.id.layout_rec_detail_info);

			mDetail_save.setOnClickListener(this);
			mDetail_back.setOnClickListener(this);
			mDetail_commit.setOnClickListener(this);

			mListView1 = (MyListView) view.findViewById(R.id.list_receive);
			mListView2 = (MyListView) view.findViewById(R.id.list_rec_detail);

			mListView1.setOnRefreshListener(refreshListener, Content.LIST_RECEIVE);
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
				bean.setType(cursor.getString(3));
				bean.setPlanNum(cursor.getString(4));
				bean.setPCNum(cursor.getString(5));
				bean.setBuilding(cursor.getString(6));
				bean.setCarNum(cursor.getString(7));
				bean.setNCState(cursor.getString(8));
				bean.setProvider(cursor.getString(9));
				mBeanList1.add(bean);
			} while (cursor.moveToNext());
			cursor.close();
		}
		cursor = null;
		listViewAdapter1 = new MyListViewAdapter(mContext, R.layout.list_receive, mBeanList1, Content.LIST_RECEIVE, null);
	}

	private void InitListViewAdapter2() {
		sqlite.execSQL(sql_creat2);
		listViewAdapter2 = new MyListViewAdapter(mContext, R.layout.list_receive_detail, mBeanList2, Content.LIST_RECEIVE_DETAIL,
				listViewAdapterCallBack2);
		mListView2.setAdapter(listViewAdapter2);
	}

	private void InitLostDialog() {
		// DialogIsShow = false;
		roadLostDialog = new RoadLostDialog(mContext, R.style.dialog, mDialogListener);
		Window dialogWindow = roadLostDialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		// dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);
		lp.x = 40;// 设置x坐标
		lp.y = 26;// 设置y坐标
		dialogWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		dialogWindow.setAttributes(lp);
		roadLostDialog.setCanceledOnTouchOutside(false);
	}

	private void LoadData_Send() {
		final String xmlString = "<Request><Function>receiptTask</Function><Parms><Username>" + Content.USER_SYS_CODE
				+ "</Username></Parms></Request>";
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (http == null) {
					http = HttpUtil.getInstance();
				}
				PostResult result = http.sendXML(xmlString);
				if (result != null) {
					mHandler.obtainMessage(Content.SEND_XML_REC_LOAD, result).sendToTarget();
				}
			}
		}).start();

	}

	private void LoadData_Rev(PostResult result) {
		if (result.isResult()) {
			DeleteTable(DB_TABLE, sql_creat1);
			DeleteTable(DETAIL_TABLE, sql_creat2);
			DeleteTable(THIRD_TABLE, sql_creat3);
			UpdateSQLiteData(result.getReceiptBeanList());
			UpDateListView1();
		} else {
			Toast.makeText(mContext, result.getMessage(), Toast.LENGTH_SHORT).show();
		}
		mListView1.onRefreshComplete(result.isResult(), Content.LIST_RECEIVE);

	}

	private void SaveDetailData() {
		ContentValues values = new ContentValues();
		for (int i = 0; i < mBeanList2.size(); i++) {
			values.clear();
			String DetailID = mBeanList2.get(i).getTaskID();
			String FactNum = mBeanList2.get(i).getFactNum();
			String FactWeight = mBeanList2.get(i).getFactWeight();
			String LockState = mBeanList2.get(i).getLockState();
			values.put("_factnum", FactNum);
			values.put("_factweight", FactWeight);
			values.put("_finish", LockState);
			sqlite.update(DETAIL_TABLE, values, "_detailid = ?", new String[] { DetailID });
		}
	}

	private void ShowInputSoft(boolean isDialogLayout) {
		if (isDialogLayout) {
			roadLostDialog.showInputSoft();
		} else {
			if (mListener != null && Content.InputSoftOpen) {
				mListener.hideMenu(true);
				listViewAdapter2.setInputOpen(true);
				layout2Info.setVisibility(View.GONE);
			} else if (mListener != null) {
				mListener.hideMenu(false);
				listViewAdapter2.setInputOpen(false);
				layout2Info.setVisibility(View.VISIBLE);
			}
		}
	}

	private void ShowLostDialog(String[] condition) {
		if (roadLostDialog == null) {
			InitLostDialog();
		}
		roadLostDialog.setTitle(condition);
		roadLostDialog.show();
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
		if (listViewAdapter2 == null) {
			InitListViewAdapter2();
		}
		if (pos > 0) {
			AdapterBean bean = mBeanList1.get(pos - 1);
			mCurTaskID = bean.getTaskID();
			mOrderID.setText(bean.getOrderID());
			mType.setText(bean.getType());
			mPCNum.setText(bean.getPCNum());
			NCState = bean.getNCState();
			mStateNC.setText(bean.getNCState());
			mCarNum.setText(bean.getCarNum());
			mProvider.setText(bean.getProvider());
		}
		UpDateListView2();
		mContext.setMyBackModel(Content.BACK_MODEL_REC_DETAIL);
		if (layout1.getVisibility() == View.VISIBLE) {
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
				bean.setPlanNum(cursor.getString(4));
				bean.setPCNum(cursor.getString(5));
				bean.setBuilding(cursor.getString(6));
				bean.setCarNum(cursor.getString(7));
				bean.setNCState(cursor.getString(8));
				bean.setProvider(cursor.getString(9));
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
				bean.setGoodsID(cursor.getString(2));
				bean.setGoodsName(cursor.getString(3));
				bean.setSpec(cursor.getString(4));
				bean.setPlanNum(cursor.getString(5));
				bean.setFactNum(cursor.getString(6));
				bean.setPlanWeight(cursor.getString(7));
				bean.setLossWeight(cursor.getString(8));
				bean.setFactWeight(cursor.getString(9));
				bean.setLockState(cursor.getString(10));
				mBeanList2.add(bean);
			} while (cursor.moveToNext());
			cursor.close();
		}
		cursor = null;
		listViewAdapter2.setNCState(NCState.equals(Content.HTTP_UPLOAD_TRUE));
		listViewAdapter2.notifyDataSetChanged();
	}

	private void UpdateSQLiteData(List<ReceiptBean> mBeanList) {
		sqlite.beginTransaction();
		ContentValues cv = new ContentValues();
		for (int i = 0; i < mBeanList.size(); i++) {
			cv.clear();
			cv.put("_taskid", mBeanList.get(i).getTaskId());
			cv.put("_date", mBeanList.get(i).getDate());
			cv.put("_orderid", mBeanList.get(i).getOrderId());
			cv.put("_type", mBeanList.get(i).getType());
			cv.put("_plannum", mBeanList.get(i).getPlanNum());
			cv.put("_pcnum", mBeanList.get(i).getPCNum());
			cv.put("_building", mBeanList.get(i).getBuilding());
			cv.put("_carnum", mBeanList.get(i).getCarNum());
			cv.put("_provider", mBeanList.get(i).getProvider());
			if (mBeanList.get(i).getStateNC().equals("0")) {
				cv.put("_statenc", Content.HTTP_UPLOAD_FALSE);
			} else {
				cv.put("_statenc", Content.HTTP_UPLOAD_TRUE);
			}
			sqlite.insert(DB_TABLE, null, cv);
			List<ReceiptDetailBean> mDetailBeanList = mBeanList.get(i).getDetailBeanList();
			if (mDetailBeanList != null && mDetailBeanList.size() > 0) {
				for (int j = 0; j < mDetailBeanList.size(); j++) {
					cv.clear();
					cv.put("_taskid", mDetailBeanList.get(j).getTaskId());
					cv.put("_detailid", mDetailBeanList.get(j).getDetailId());
					cv.put("_goodsid", mDetailBeanList.get(j).getGoodsID());
					cv.put("_goodsname", mDetailBeanList.get(j).getGoodsName());
					cv.put("_spec", mDetailBeanList.get(j).getSpec());
					cv.put("_plannum", mDetailBeanList.get(j).getPlanNum());
					cv.put("_factnum", mDetailBeanList.get(j).getFactNum());
					cv.put("_planweight", mDetailBeanList.get(j).getPlanWeight());
					cv.put("_lossweight", mDetailBeanList.get(j).getLossWeight());
					cv.put("_factweight", mDetailBeanList.get(j).getFactWeight());
					cv.put("_finish", "0");
					sqlite.insert(DETAIL_TABLE, null, cv);
					List<LossRecordBean> mThirdBeanList = mDetailBeanList.get(j).getThirdBeanList();
					if (mThirdBeanList != null && mThirdBeanList.size() > 0) {
						for (int k = 0; k < mThirdBeanList.size(); k++) {
							cv.clear();
							cv.put("_taskid", mThirdBeanList.get(k).getTaskId());
							cv.put("_detailid", mThirdBeanList.get(k).getDetailId());
							cv.put("_lossid", mThirdBeanList.get(k).getLossTaskID());
							if (mThirdBeanList.get(k).getLossType().equals("1")) {
								cv.put("_type", "湿损");
							} else if (mThirdBeanList.get(k).getLossType().equals("2")) {
								cv.put("_type", "丢失");
							} else {
								cv.put("_type", "无");
							}
							cv.put("_type", mThirdBeanList.get(k).getLossType());
							cv.put("_spec", mThirdBeanList.get(k).getSpec());
							cv.put("_rate", mThirdBeanList.get(k).getRate());
							cv.put("_lossweight", mThirdBeanList.get(k).getLossWeight());
							cv.put("_barcode", mThirdBeanList.get(k).getBarCode());
							cv.put("_mark", mThirdBeanList.get(k).getMark());
							sqlite.insert(THIRD_TABLE, null, cv);
						}
					}
				}
			}
		}
		sqlite.setTransactionSuccessful();
		sqlite.endTransaction();
	}

	static class MyHandler extends Handler {
		WeakReference<ReceiveFragment> mFragment;

		MyHandler(ReceiveFragment receiveFragment) {
			mFragment = new WeakReference<ReceiveFragment>(receiveFragment);
		}

		@Override
		public void handleMessage(Message msg) {
			ReceiveFragment theFragment = mFragment.get();
			if (theFragment == null) {
				return;
			}
			switch (msg.what) {
			case Content.SEND_XML_REC_LOAD:
				theFragment.LoadData_Rev((PostResult) msg.obj);
				break;

			case Content.SEND_XML_REC_COMMIT:
				theFragment.CommitData_Rev((PostResult) msg.obj);
				break;

			case Content.BACK_PRESSED:
				theFragment.BackPressed();
				break;

			case Content.INPUT_SOFT_ACTION:
				theFragment.ShowInputSoft((Boolean) msg.obj);
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
		public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
			ShowDetailTable(pos);
		}
	};

	private ListViewAdapterCallBack listViewAdapterCallBack2 = new ListViewAdapterCallBack() {

		@Override
		public void onItemClick(View v, int pos, AdapterBean bean) {
			if (layout2Info.getVisibility() != View.GONE) {// 输入法展开时屏蔽点击事件
				mCurDetailPos = pos;
				MyLog.d(TAG, "mLV2CallBack.onItemClick", "mCurDetailPos = " + mCurDetailPos);
				String[] detailInfo = new String[] { mCurTaskID, bean.getTaskID(), NCState, bean.getGoodsID(), bean.getGoodsName(),
						bean.getSpec(), bean.getPlanNum(), bean.getFactNum(), bean.getPlanWeight(), bean.getFactWeight() };
				ShowLostDialog(detailInfo);
			}
		}

		@Override
		public void onItemDelete(View v, int mPos) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onItemUpdate(View v, int mPos, String newValue1) {
			// TODO Auto-generated method stub

		}

	};

	public static void onBackPressed() {
		mHandler.obtainMessage(Content.BACK_PRESSED).sendToTarget();
	}

	private mLostDialogListener mDialogListener = new mLostDialogListener() {

		@Override
		public void DialogDismiss(double lossWeight) {// 重新获取途损重量计算完成重量，不改变完成数量
			MyLog.d(TAG, "mDialogListener.DialogDismiss", "mCurDetailPos = " + mCurDetailPos);
			AdapterBean bean = mBeanList2.get(mCurDetailPos);
			bean.setLossWeight(lossWeight + "");
			listViewAdapter2.notifyDataSetChanged();
			mCurDetailPos = -1;
		}
	};

}
