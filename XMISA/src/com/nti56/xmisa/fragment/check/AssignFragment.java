package com.nti56.xmisa.fragment.check;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;

import com.nti56.xmisa.bean.AdapterBean;
import com.nti56.xmisa.bean.AssignBean;
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
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLayoutChangeListener;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AssignFragment extends Fragment implements OnClickListener, OnLayoutChangeListener, OnFocusChangeListener {

	private static MyHandler mHandler;

	private MainActivity mContext;

	private HttpUtil http;
	private MyToast mToast;

	private SQLiteDatabase sqlite;
	private MyListViewAdapter listViewAdapter1;

	int mDetailClickPos = -1;

	private WeakReference<View> mRootView;
	private mAsgFragmentListener mListener;

	private RelativeLayout layout1;
	private LinearLayout layout2, layoutInfo;

	private EditText mCheckNum;
	private MyListView mListView1;
	private RadioGroup mRadioGroup;
	private CheckBox mCBWeight, mCBWater, mCBAppearance, mCBMould, mCBOdor, mCBImpurity, mCBMothy;
	private TextView mDate, mOrderID, mType, mBuilding, mTotalNum, mGoodsID, mGoodsName, mCarNum;
	private Button mBtn_save, mBtn_back, mBtn_commit;

	// private String NCState;
	private String mCurTaskID;
	private String DB_TABLE = "assign";

	private ArrayList<AdapterBean> mBeanList1 = new ArrayList<AdapterBean>();

	private String sql_creat1 = "CREATE TABLE if not exists assign(_taskid VARCHAR PRIMARY KEY, _childid VARCHAR, _date VARCHAR, "
			+ "_orderid VARCHAR, _type VARCHAR, _goodsid VARCHAR, _goodsname VARCHAR, _building VARCHAR, _carnum VARCHAR, "
			+ "_checkline VARCHAR, _assignstate VARCHAR, _totalnum VARCHAR, _checknum VARCHAR, _upstreamdetailid VARCHAR, "
			+ "_weight VARCHAR, _water VARCHAR, _appearance VARCHAR, _mould VARCHAR, _odor VARCHAR, _impurity VARCHAR, _mothy VARCHAR)";

	public interface mAsgFragmentListener {
		public void hideMenu(boolean hide);

		public void btnCancel();
	}

	public AssignFragment(mAsgFragmentListener mListener) {
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
		Log.e("NTI", "AssignFragment........onCreateView()");
		if (mRootView == null || mRootView.get() == null) {
			View v = inflater.inflate(R.layout.fragment_2_assign, container, false);
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
		Log.e("NTI", "AssignFragment........onHiddenChanged():" + hidden);
		if (!hidden && mRootView != null) {
			if (layout2.getVisibility() == View.VISIBLE) {
				mContext.setMyBackModel(Content.BACK_MODEL_ASG_DETAIL);
			} else {
				mContext.setMyBackModel(Content.BACK_MODEL_NONE);
			}
		}
		super.onHiddenChanged(hidden);
	}

	@Override
	public void onDestroy() {
		Log.e("NTI", "AssignFragment........onDestroy()");
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		if (Content.InputSoftOpen) {
			return;
		}
		switch (v.getId()) {
		case R.id.btn_asg_detail_back:// 返回
			ShowMainTable();
			break;

		case R.id.btn_asg_detail_save:// 保存
			SaveDetailData();
			break;

		case R.id.btn_asg_detail_commit: { // 反馈
			SaveDetailData();
			CommitData_Send();
		}
			break;

		default:
			break;
		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		// switch (v.getId()) {
		// case R.id.et_asg_detail_drug:
		// mFocusEditText = hasFocus ? mDrug : null;
		// break;
		//
		// case R.id.et_asg_detail_dosingdate:
		// mFocusEditText = hasFocus ? mDosingDate : null;
		// break;
		// case R.id.et_asg_detail_air_start:
		// mFocusEditText = hasFocus ? mAirStart : null;
		// break;
		//
		// case R.id.et_asg_detail_air_end:
		// mFocusEditText = hasFocus ? mAirEnd : null;
		// break;
		//
		// default:
		// return;
		// }
		// if (!hasFocus && Content.InputSoftOpen) {
		// Message msg = mHandler.obtainMessage(Content.JUDGE_EDITTEXT_FOCUS);
		// mHandler.sendMessageDelayed(msg, 50);
		// }
	}

	private void CommitData_Send() {
		String Action = "UPDATE";
		String TaskXML = "";
		Cursor cursor = sqlite.query(DB_TABLE, null, "_taskid = ?", new String[] { mCurTaskID }, null, null, null);
		if (cursor != null && cursor.moveToFirst()) {
			TaskXML = "<Task TaskId=\"" + cursor.getString(0) + "\" DetailID=\"" + cursor.getString(1) + "\" TaskNumber=\""
					+ cursor.getString(3) + "\" Line=\"" + cursor.getString(9) + "\" Samplingqty=\"" + cursor.getString(12)
					+ "\" Upstreamdetailid=\"" + cursor.getString(13) + "\" > <TaskItem " + " Weight=\"" + cursor.getString(14)
					+ "\" Water=\"" + cursor.getString(15) + "\" Appearance=\"" + cursor.getString(16) + "\" Mould=\""
					+ cursor.getString(17) + "\" Odor=\"" + cursor.getString(18) + "\" Varia=\"" + cursor.getString(19)
					+ "\" Moth=\"" + cursor.getString(20) + "\" />";
			cursor.close();
		}
		cursor = null;

		final String UpdateXML = "<Request><Function>CheckAllocateNC</Function><Action>" + Action + "</Action><Parms>" + TaskXML
				+ "</Task></Parms></Request>";
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (http == null) {
					http = HttpUtil.getInstance();
				}
				PostResult result = http.sendXML(UpdateXML);
				mHandler.obtainMessage(Content.SEND_XML_ASG_COMMIT, result).sendToTarget();
			}
		}).start();
	}

	private void CommitData_Rev(PostResult result) {
		if (result.isResult()) {
			ContentValues values = new ContentValues();
			values.put("_assignstate", Content.HTTP_ASSIGN_TRUE);
			// NCState = Content.HTTP_ASSIGN_TRUE;
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

			mDate = (TextView) view.findViewById(R.id.tv_asg_detail_date);
			mOrderID = (TextView) view.findViewById(R.id.tv_asg_detail_orderid);
			mType = (TextView) view.findViewById(R.id.tv_asg_detail_type);
			mBuilding = (TextView) view.findViewById(R.id.tv_asg_detail_building);
			mTotalNum = (TextView) view.findViewById(R.id.tv_asg_detail_totalnum);
			mGoodsID = (TextView) view.findViewById(R.id.tv_asg_detail_goodsid);
			mGoodsName = (TextView) view.findViewById(R.id.tv_asg_detail_goodsname);
			mCarNum = (TextView) view.findViewById(R.id.tv_asg_detail_carnum);

			mCheckNum = (EditText) view.findViewById(R.id.et_asg_detail_check_num);

			mRadioGroup = (RadioGroup) view.findViewById(R.id.radiogroup_assign);

			mCBWeight = (CheckBox) view.findViewById(R.id.cb_asg_detail_weight);
			mCBWater = (CheckBox) view.findViewById(R.id.cb_asg_detail_water);
			mCBAppearance = (CheckBox) view.findViewById(R.id.cb_asg_detail_appearance);
			mCBMould = (CheckBox) view.findViewById(R.id.cb_asg_detail_mould);
			mCBOdor = (CheckBox) view.findViewById(R.id.cb_asg_detail_odor);
			mCBImpurity = (CheckBox) view.findViewById(R.id.cb_asg_detail_impurity);
			mCBMothy = (CheckBox) view.findViewById(R.id.cb_asg_detail_mothy);

			mBtn_save = (Button) view.findViewById(R.id.btn_asg_detail_save);
			mBtn_back = (Button) view.findViewById(R.id.btn_asg_detail_back);
			mBtn_commit = (Button) view.findViewById(R.id.btn_asg_detail_commit);

			layout1 = (RelativeLayout) view.findViewById(R.id.layout_assign);
			layout2 = (LinearLayout) view.findViewById(R.id.layout_asg_detail);
			layoutInfo = (LinearLayout) view.findViewById(R.id.layout_asg_detail_info);

			mBtn_save.setOnClickListener(this);
			mBtn_back.setOnClickListener(this);
			mBtn_commit.setOnClickListener(this);

			mListView1 = (MyListView) view.findViewById(R.id.list_assign);
			mListView1.setOnRefreshListener(refreshListener, Content.LIST_ASSIGN);
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
				bean.setDate(cursor.getString(2));
				bean.setOrderID(cursor.getString(3));
				bean.setType(cursor.getString(4));
				bean.setGoodsID(cursor.getString(5));
				bean.setGoodsName(cursor.getString(6));
				bean.setBuilding(cursor.getString(7));
				bean.setCarNum(cursor.getString(8));
				bean.setCheckLine(cursor.getString(9));
				bean.setAssignState(cursor.getString(10));
				bean.setTotalNum(cursor.getString(11));
				bean.setCheckNum(cursor.getString(12));
				bean.setWeight(cursor.getString(14));
				bean.setWater(cursor.getString(15));
				bean.setAppearance(cursor.getString(16));
				bean.setMould(cursor.getString(17));
				bean.setOdor(cursor.getString(18));
				bean.setImpurity(cursor.getString(19));
				bean.setMothy(cursor.getString(20));
				mBeanList1.add(bean);
			} while (cursor.moveToNext());
			cursor.close();
		}
		cursor = null;
		listViewAdapter1 = new MyListViewAdapter(mContext, R.layout.list_assign, mBeanList1, Content.LIST_ASSIGN, null);
	}

	private void LoadData_Send() {
		final String xmlString = "<Request><Function>CheckAllocate</Function><Parms><Username>" + Content.USER_SYS_CODE
				+ "</Username></Parms></Request>";
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (http == null) {
					http = HttpUtil.getInstance();
				}
				PostResult result = http.sendXML(xmlString);
				if (result != null) {
					mHandler.obtainMessage(Content.SEND_XML_ASG_LOAD, result).sendToTarget();
				}
			}
		}).start();

	}

	private void LoadData_Rev(PostResult result) {
		if (result.isResult()) {
			DeleteTable(DB_TABLE, sql_creat1);
			UpdateSQLiteData(result.getAssignBeanList());
			UpDateListView1();
		} else {
			mToast.show(result.getMessage());
		}
		mListView1.onRefreshComplete(result.isResult(), Content.LIST_ASSIGN);

	}

	private void SaveDetailData() {
		ContentValues values = new ContentValues();
		values.put("_checknum", mCheckNum.getText().toString());
		switch (mRadioGroup.getCheckedRadioButtonId()) {
		case R.id.radio1:
			values.put("_checkline", "1");
			break;
		case R.id.radio2:
			values.put("_checkline", "2");
			break;
		case R.id.radio3:
			values.put("_checkline", "3");
			break;
		}
		values.put("_weight", mCBWeight.isChecked() ? "1" : "0");
		values.put("_water", mCBWater.isChecked() ? "1" : "0");
		values.put("_appearance", mCBAppearance.isChecked() ? "1" : "0");
		values.put("_mould", mCBMould.isChecked() ? "1" : "0");
		values.put("_odor", mCBOdor.isChecked() ? "1" : "0");
		values.put("_impurity", mCBImpurity.isChecked() ? "1" : "0");
		values.put("_mothy", mCBMothy.isChecked() ? "1" : "0");
		sqlite.update(DB_TABLE, values, "_taskid = ?", new String[] { mCurTaskID });
		mToast.show("保存成功！");
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
		AdapterBean bean = mBeanList1.get(pos - 1);
		mCurTaskID = bean.getTaskID();
		mDate.setText(bean.getDate());
		mOrderID.setText(bean.getOrderID());
		mType.setText(bean.getType());
		mBuilding.setText(bean.getBuilding());
		mTotalNum.setText(bean.getTotalNum());
		mGoodsID.setText(bean.getGoodsID());
		mGoodsName.setText(bean.getGoodsName());
		mCarNum.setText(bean.getCarNum());
		mCheckNum.setText(bean.getCheckNum());
		if (bean.getCheckLine() == null) {
			mRadioGroup.check(R.id.radio1);
		} else if (bean.getCheckLine().equals("3")) {
			mRadioGroup.check(R.id.radio3);
		} else if (bean.getCheckLine().equals("2")) {
			mRadioGroup.check(R.id.radio2);
		} else {
			mRadioGroup.check(R.id.radio1);
		}
		mCBWeight.setChecked(!bean.getWeight().equals("0"));
		mCBWater.setChecked(!bean.getWater().equals("0"));
		mCBAppearance.setChecked(!bean.getAppearance().equals("0"));
		mCBMould.setChecked(!bean.getMould().equals("0"));
		mCBOdor.setChecked(!bean.getOdor().equals("0"));
		mCBImpurity.setChecked(!bean.getImpurity().equals("0"));
		mCBMothy.setChecked(!bean.getMothy().equals("0"));

		// NCState = bean.getAssignState();
		mContext.setMyBackModel(Content.BACK_MODEL_ASG_DETAIL);
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
				bean.setDate(cursor.getString(2));
				bean.setOrderID(cursor.getString(3));
				bean.setType(cursor.getString(4));
				bean.setGoodsID(cursor.getString(5));
				bean.setGoodsName(cursor.getString(6));
				bean.setBuilding(cursor.getString(7));
				bean.setCarNum(cursor.getString(8));
				bean.setCheckLine(cursor.getString(9));
				bean.setAssignState(cursor.getString(10));
				bean.setTotalNum(cursor.getString(11));
				bean.setCheckNum(cursor.getString(12));
				bean.setWeight(cursor.getString(14));
				bean.setWater(cursor.getString(15));
				bean.setAppearance(cursor.getString(16));
				bean.setMould(cursor.getString(17));
				bean.setOdor(cursor.getString(18));
				bean.setImpurity(cursor.getString(19));
				bean.setMothy(cursor.getString(20));
				mBeanList1.add(bean);
			} while (cursor.moveToNext());
			cursor.close();
		}
		cursor = null;
		listViewAdapter1.notifyDataSetChanged();
	}

	private void UpdateSQLiteData(List<AssignBean> mTaskBeans) {
		sqlite.beginTransaction();
		ContentValues cv = new ContentValues();
		for (int i = 0; i < mTaskBeans.size(); i++) {
			cv.clear();
			cv.put("_taskid", mTaskBeans.get(i).getTaskId());
			cv.put("_childid", mTaskBeans.get(i).getChildId());
			cv.put("_date", mTaskBeans.get(i).getDate());
			cv.put("_orderid", mTaskBeans.get(i).getOrderId());
			cv.put("_type", mTaskBeans.get(i).getType());
			cv.put("_goodsid", mTaskBeans.get(i).getGoodsId());
			cv.put("_goodsname", mTaskBeans.get(i).getGoodsName());
			cv.put("_building", mTaskBeans.get(i).getBuilding());
			cv.put("_carnum", mTaskBeans.get(i).getCarNum());
			cv.put("_checkline", mTaskBeans.get(i).getCheckLine());
			cv.put("_totalnum", mTaskBeans.get(i).getTotalNum());
			cv.put("_checknum", mTaskBeans.get(i).getCheckNum());
			cv.put("_upstreamdetailid", mTaskBeans.get(i).getUpstreamdetailid());
			cv.put("_weight", mTaskBeans.get(i).getWeight());
			cv.put("_water", mTaskBeans.get(i).getWater());
			cv.put("_appearance", mTaskBeans.get(i).getAppearance());
			cv.put("_mould", mTaskBeans.get(i).getMould());
			cv.put("_odor", mTaskBeans.get(i).getOdor());
			cv.put("_impurity", mTaskBeans.get(i).getImpurity());
			cv.put("_mothy", mTaskBeans.get(i).getMothy());

			if (mTaskBeans.get(i).getAssignState().equals("0")) {
				cv.put("_assignstate", Content.HTTP_ASSIGN_FALSE);
			} else {
				cv.put("_assignstate", Content.HTTP_ASSIGN_TRUE);
			}
			sqlite.insert(DB_TABLE, null, cv);
		}
		sqlite.setTransactionSuccessful();
		sqlite.endTransaction();
	}

	private static class MyHandler extends Handler {
		WeakReference<AssignFragment> mFragment;

		MyHandler(AssignFragment assignFragment) {
			mFragment = new WeakReference<AssignFragment>(assignFragment);
		}

		@Override
		public void handleMessage(Message msg) {
			AssignFragment theFragment = mFragment.get();
			if (theFragment == null) {
				return;
			}
			switch (msg.what) {
			case Content.SEND_XML_ASG_LOAD:
				theFragment.LoadData_Rev((PostResult) msg.obj);
				break;

			case Content.SEND_XML_ASG_COMMIT:
				theFragment.CommitData_Rev((PostResult) msg.obj);
				break;

			case Content.BACK_PRESSED:
				theFragment.ShowMainTable();
				break;

			case Content.INPUT_SOFT_ACTION:
				theFragment.ShowInputSoft();
				break;

			case Content.JUDGE_EDITTEXT_FOCUS:
				// theFragment.JudgeEditTextFocus();
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
