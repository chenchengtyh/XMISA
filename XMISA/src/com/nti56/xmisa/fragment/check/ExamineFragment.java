package com.nti56.xmisa.fragment.check;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;

import com.nti56.xmisa.bean.AdapterBean;
import com.nti56.xmisa.bean.ExamineBean;
import com.nti56.xmisa.bean.ExamineDetailBean;
import com.nti56.xmisa.adapter.MyListView.OnRefreshListener;

import com.nti56.xmisa.R;
import com.nti56.xmisa.MainActivity;
import com.nti56.xmisa.adapter.MyListViewAdapter;
import com.nti56.xmisa.adapter.MySQLiteHelper;
import com.nti56.xmisa.adapter.MyListView;
import com.nti56.xmisa.bean.PostResult;
import com.nti56.xmisa.util.Content;
import com.nti56.xmisa.util.HttpUtil;
import com.nti56.xmisa.util.MyToast;

import android.app.Fragment;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ExamineFragment extends Fragment implements OnClickListener, OnLayoutChangeListener {

	private static MyHandler mHandler;

	private MainActivity mContext;

	private HttpUtil http;
	private MyToast mToast;

	private SQLiteDatabase sqlite;
	private MyListViewAdapter listViewAdapter1, listViewAdapter2;

	int mDetailClickPos = -1;

	private WeakReference<View> mRootView;
	private mExmFragmentListener mListener;

	private RelativeLayout layout1;
	private LinearLayout layout2, layoutInfo;

	private MyListView mListView1, mListView2;
	private TextView mDate, mOrderID, mType, mBuilding, mTotalNum, mGoodsID, mGoodsName, mCheckNum;
	private Button mBtn_back, mBtn_commit;

	private String NCState;
	private String mCurTaskID;
	private String DB_TABLE = "examine";
	private String DETAIL_TABLE = "examine_detail";

	private ArrayList<AdapterBean> mBeanList1 = new ArrayList<AdapterBean>();
	private ArrayList<AdapterBean> mBeanList2 = new ArrayList<AdapterBean>();

	private String sql_creat1 = "CREATE TABLE if not exists examine(_taskid VARCHAR PRIMARY KEY, _date VARCHAR, "
			+ "_orderid VARCHAR, _type VARCHAR, _goodsid VARCHAR, _goodsname VARCHAR, _building VARCHAR, _carnum VARCHAR, "
			+ "_checkline VARCHAR, _statenc VARCHAR, _totalnum VARCHAR, _checknum VARCHAR)";

	private String sql_creat2 = "CREATE TABLE if not exists examine_detail(_taskid VARCHAR, _detailid VARCHAR PRIMARY KEY, "
			+ "_barcode VARCHAR, _rfidcode VARCHAR, _factweight VARCHAR, _weight VARCHAR, _factwater VARCHAR, _water VARCHAR, "
			+ "_appearance VARCHAR, _mould VARCHAR, _odor VARCHAR, _impurity VARCHAR, _mothy VARCHAR, _waterdev VARCHAR, "
			+ "_densitydev VARCHAR, _colligate VARCHAR, _date VARCHAR)";

	public interface mExmFragmentListener {
		public void hideMenu(boolean hide);

		public void btnCancel();
	}

	public ExamineFragment(mExmFragmentListener mListener) {
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
		Log.e("NTI", "ExamineFragment........onCreateView()");
		if (mRootView == null || mRootView.get() == null) {
			View v = inflater.inflate(R.layout.fragment_2_examine, container, false);
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
	public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight,
			int oldBottom) {
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
		Log.e("NTI", "ExamineFragment........onHiddenChanged():" + hidden);
		if (!hidden && mRootView != null) {
			if (layout2.getVisibility() == View.VISIBLE) {
				mContext.setMyBackModel(Content.BACK_MODEL_EXM_DETAIL);
			} else {
				mContext.setMyBackModel(Content.BACK_MODEL_NONE);
			}
		}
		super.onHiddenChanged(hidden);
	}

	@Override
	public void onDestroy() {
		Log.e("NTI", "ExamineFragment........onDestroy()");
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		if (Content.InputSoftOpen) {
			return;
		}
		switch (v.getId()) {
		case R.id.btn_exm_detail_back:// 返回
			ShowMainTable();
			break;

		case R.id.btn_exm_detail_commit: { // 反馈
			CommitData_Send();
		}
			break;

		default:
			break;
		}
	}

	private void CommitData_Send() {
		String Action = "UPDATE";
		String TaskXML = "", DetailXML = "";
		Cursor cursor = sqlite.query(DB_TABLE, null, "_taskid = ?", new String[] { mCurTaskID }, null, null, null);
		if (cursor != null && cursor.moveToFirst()) {
			TaskXML = "<Task TaskId=\"" + cursor.getString(0) + "\" TaskNumber=\"" + cursor.getString(2) + "\" > ";
			cursor.close();
		}
		cursor = null;
		Cursor cursor2 = sqlite.query(DETAIL_TABLE, null, "_taskid = ?", new String[] { mCurTaskID }, null, null, null);
		if (cursor2 != null && cursor2.moveToFirst()) {
			do {
				if (cursor2.isLast()) {
					DetailXML = "<TaskDetail DetailId=\"" + cursor2.getString(1) + "\" > " + DetailXML + "</TaskDetail>";
				} else {
					DetailXML += "<TaskChild ChildId=\"" + cursor2.getString(1) + "\" /> ";
				}
			} while (cursor2.moveToNext());
			cursor2.close();
		}
		cursor2 = null;
		final String UpdateXML = "<Request><Function>CheckComfirmNC</Function><Action>" + Action + "</Action><Parms>" + TaskXML
				+ DetailXML + "</Task></Parms></Request>";
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (http == null) {
					http = HttpUtil.getInstance();
				}
				PostResult result = http.sendXML(UpdateXML);
				mHandler.obtainMessage(Content.SEND_XML_EXM_COMMIT, result).sendToTarget();
			}
		}).start();
	}

	private void CommitData_Rev(PostResult result) {
		if (result.isResult()) {
			ContentValues values = new ContentValues();
			values.put("_statenc", Content.HTTP_UPLOAD_TRUE);
//			NCState = Content.HTTP_ASSIGN_TRUE;
			sqlite.update(DB_TABLE, values, "_taskid = ?", new String[] { mCurTaskID });
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

			mDate = (TextView) view.findViewById(R.id.tv_exm_detail_date);
			mOrderID = (TextView) view.findViewById(R.id.tv_exm_detail_orderid);
			mType = (TextView) view.findViewById(R.id.tv_exm_detail_type);
			mBuilding = (TextView) view.findViewById(R.id.tv_exm_detail_building);
			mTotalNum = (TextView) view.findViewById(R.id.tv_exm_detail_totalnum);
			mCheckNum = (TextView) view.findViewById(R.id.tv_exm_detail_checknum);
			mGoodsID = (TextView) view.findViewById(R.id.tv_exm_detail_goodsid);
			mGoodsName = (TextView) view.findViewById(R.id.tv_exm_detail_goodsname);

			mBtn_back = (Button) view.findViewById(R.id.btn_exm_detail_back);
			mBtn_commit = (Button) view.findViewById(R.id.btn_exm_detail_commit);

			layout1 = (RelativeLayout) view.findViewById(R.id.layout_examine);
			layout2 = (LinearLayout) view.findViewById(R.id.layout_exm_detail);
			layoutInfo = (LinearLayout) view.findViewById(R.id.layout_exm_detail_info);

			mBtn_back.setOnClickListener(this);
			mBtn_commit.setOnClickListener(this);

			mListView1 = (MyListView) view.findViewById(R.id.list_examine);
			mListView2 = (MyListView) view.findViewById(R.id.list_exm_detail);

			mListView1.setOnRefreshListener(refreshListener, Content.LIST_EXAMINE);
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
				bean.setGoodsID(cursor.getString(4));
				bean.setGoodsName(cursor.getString(5));
				bean.setBuilding(cursor.getString(6));
				bean.setCarNum(cursor.getString(7));
				bean.setCheckLine(cursor.getString(8));
				bean.setNCState(cursor.getString(9));
				bean.setTotalNum(cursor.getString(10));
				bean.setCheckNum(cursor.getString(11));
				mBeanList1.add(bean);
			} while (cursor.moveToNext());
			cursor.close();
		}
		cursor = null;
		listViewAdapter1 = new MyListViewAdapter(mContext, R.layout.list_examine, mBeanList1, Content.LIST_EXAMINE, null);
	}

	private void InitListViewAdapter2() {
		sqlite.execSQL(sql_creat2);
		listViewAdapter2 = new MyListViewAdapter(mContext, R.layout.list_exm_detail, mBeanList2, Content.LIST_EXAMINE_DETAIL, null);
		mListView2.setAdapter(listViewAdapter2);
	}

	private void LoadData_Send() {
		final String xmlString = "<Request><Function>CheckComfirm</Function><Parms><Username>" + Content.USER_SYS_CODE
				+ "</Username></Parms></Request>";
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (http == null) {
					http = HttpUtil.getInstance();
				}
				PostResult result = http.sendXML(xmlString);
				if (result != null) {
					mHandler.obtainMessage(Content.SEND_XML_EXM_LOAD, result).sendToTarget();
				}
			}
		}).start();
	}

	private void LoadData_Rev(PostResult result) {
		if (result.isResult()) {
			DeleteTable(DB_TABLE, sql_creat1);
			DeleteTable(DETAIL_TABLE, sql_creat2);
			UpdateSQLiteData(result.getExamineBeanList());
			UpDateListView1();
		} else {
			mToast.show(result.getMessage());
		}
		mListView1.onRefreshComplete(result.isResult(), Content.LIST_EXAMINE);

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
			mDate.setText(bean.getDate());
			mOrderID.setText(bean.getOrderID());
			mType.setText(bean.getType());
			mBuilding.setText(bean.getBuilding());
			mTotalNum.setText(bean.getTotalNum());
			mCheckNum.setText(bean.getCheckNum());
			mGoodsID.setText(bean.getGoodsID());
			mGoodsName.setText(bean.getGoodsName());
			NCState = bean.getNCState();
//			mStateNC.setText(NCState);
		}
		UpDateListView2();
		mContext.setMyBackModel(Content.BACK_MODEL_EXM_DETAIL);
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
				bean.setGoodsID(cursor.getString(4));
				bean.setGoodsName(cursor.getString(5));
				bean.setBuilding(cursor.getString(6));
				bean.setCarNum(cursor.getString(7));
				bean.setCheckLine(cursor.getString(8));
				bean.setNCState(cursor.getString(9));
				bean.setTotalNum(cursor.getString(10));
				bean.setCheckNum(cursor.getString(11));
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
				bean.setBarCode(cursor.getString(2));
				bean.setRFIDCode(cursor.getString(3));
				bean.setFactWeight(cursor.getString(4));
				bean.setWeight(cursor.getString(5));
				bean.setFactWater(cursor.getString(6));
				bean.setWater(cursor.getString(7));
				bean.setAppearance(cursor.getString(8));
				bean.setMould(cursor.getString(9));
				bean.setOdor(cursor.getString(10));
				bean.setImpurity(cursor.getString(11));
				bean.setMothy(cursor.getString(12));
				bean.setWaterDev(cursor.getString(13));
				bean.setDensityDev(cursor.getString(14));
				bean.setColligate(cursor.getString(15));
				bean.setDate(cursor.getString(16));
				mBeanList2.add(bean);
			} while (cursor.moveToNext());
			cursor.close();
		}
		cursor = null;
//		listViewAdapter2.setNCState(NCState.equals(Content.HTTP_UPLOAD_TRUE));
		listViewAdapter2.notifyDataSetChanged();
		mListView2.setSelection(0);
	}

	private void UpdateSQLiteData(List<ExamineBean> mBeanList) {
		sqlite.beginTransaction();
		ContentValues cv = new ContentValues();
		for (int i = 0; i < mBeanList.size(); i++) {
			cv.clear();
			cv.put("_taskid", mBeanList.get(i).getTaskId());
			cv.put("_date", mBeanList.get(i).getDate());
			cv.put("_orderid", mBeanList.get(i).getOrderId());
			cv.put("_type", mBeanList.get(i).getType());
			cv.put("_goodsid", mBeanList.get(i).getGoodsId());
			cv.put("_goodsname", mBeanList.get(i).getGoodsName());
			cv.put("_building", mBeanList.get(i).getBuilding());
			cv.put("_carnum", mBeanList.get(i).getCarNum());
			cv.put("_checkline", mBeanList.get(i).getCheckLine());
			cv.put("_totalnum", mBeanList.get(i).getTotalNum());
			cv.put("_checknum", mBeanList.get(i).getCheckNum());;
			
			if (mBeanList.get(i).getNCState().equals("0")) {
				cv.put("_statenc", Content.HTTP_UPLOAD_FALSE);
			} else {
				cv.put("_statenc", Content.HTTP_UPLOAD_TRUE);
			}
			sqlite.insert(DB_TABLE, null, cv);
			List<ExamineDetailBean> mDetailBeanList = mBeanList.get(i).getDetailBeanList();
			if (mDetailBeanList != null && mDetailBeanList.size() > 0) {
				for (int j = 0; j < mDetailBeanList.size(); j++) {
					cv.clear();
					cv.put("_taskid", mDetailBeanList.get(j).getTaskId());
					cv.put("_detailid", mDetailBeanList.get(j).getDetailId());
					cv.put("_barcode", mDetailBeanList.get(j).getBarCode());
					cv.put("_rfidcode", mDetailBeanList.get(j).getRFIDCode());
					cv.put("_factweight", mDetailBeanList.get(j).getFactWeight());
					cv.put("_weight", mDetailBeanList.get(j).getWeight());
					cv.put("_factwater", mDetailBeanList.get(j).getFactWater());
					cv.put("_water", mDetailBeanList.get(j).getWater());
					cv.put("_appearance", mDetailBeanList.get(j).getAppearance());
					cv.put("_mould", mDetailBeanList.get(j).getMould());
					cv.put("_odor", mDetailBeanList.get(j).getOdor());
					cv.put("_impurity", mDetailBeanList.get(j).getImpurity());
					cv.put("_mothy", mDetailBeanList.get(j).getMothy());
					cv.put("_waterdev", mDetailBeanList.get(j).getWaterDev());
					cv.put("_densitydev", mDetailBeanList.get(j).getDensityDev());
					cv.put("_colligate", mDetailBeanList.get(j).getColligate());
					cv.put("_date", mDetailBeanList.get(j).getDate());
					sqlite.insert(DETAIL_TABLE, null, cv);
				}
			}
		}
		sqlite.setTransactionSuccessful();
		sqlite.endTransaction();
	}

	private static class MyHandler extends Handler {
		WeakReference<ExamineFragment> mFragment;

		MyHandler(ExamineFragment examineFragment) {
			mFragment = new WeakReference<ExamineFragment>(examineFragment);
		}

		@Override
		public void handleMessage(Message msg) {
			ExamineFragment theFragment = mFragment.get();
			if (theFragment == null) {
				return;
			}
			switch (msg.what) {
			case Content.SEND_XML_EXM_LOAD:
				theFragment.LoadData_Rev((PostResult) msg.obj);
				break;

			case Content.SEND_XML_EXM_COMMIT:
				theFragment.CommitData_Rev((PostResult) msg.obj);
				break;

			case Content.BACK_PRESSED:
				theFragment.ShowMainTable();
				break;

			case Content.INPUT_SOFT_ACTION:
				theFragment.ShowInputSoft();
				break;

			case Content.JUDGE_EDITTEXT_FOCUS:
//				theFragment.JudgeEditTextFocus();
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


	public void ShowInputSoft() {
		if (mListener != null && Content.InputSoftOpen) {
			mListener.hideMenu(true);
			layoutInfo.setVisibility(View.GONE);
		} else if (mListener != null) {
			mListener.hideMenu(false);
			layoutInfo.setVisibility(View.VISIBLE);
		}
	}

	public static void onBackPressed() {
		mHandler.obtainMessage(Content.BACK_PRESSED).sendToTarget();
	}


}
