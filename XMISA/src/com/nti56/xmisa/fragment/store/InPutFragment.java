package com.nti56.xmisa.fragment.store;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import com.nti56.xmisa.bean.AdapterBean;
import com.nti56.xmisa.bean.InPutBean;
import com.nti56.xmisa.bean.InPutDetailBean;
import com.nti56.xmisa.bean.InPutThirdBean;
import com.nti56.xmisa.adapter.MyListView.OnRefreshListener;

import com.nti56.xmisa.R;
import com.nti56.xmisa.MainActivity;
import com.nti56.xmisa.adapter.MyListViewAdapter;
import com.nti56.xmisa.adapter.MyListViewAdapter.ListViewAdapterCallBack;
import com.nti56.xmisa.adapter.MySQLiteHelper;
import com.nti56.xmisa.adapter.MyListView;
import com.nti56.xmisa.bean.PostResult;
import com.nti56.xmisa.util.Content;
import com.nti56.xmisa.util.HttpUtil;
import com.nti56.xmisa.util.MyLog;
import com.nti56.xmisa.util.MyToast;
import com.nti56.xmisa.util.FTPUtil.UploadProgressListener;
import com.nti56.xmisa.util.StringUtils;

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

public class InPutFragment extends Fragment implements OnClickListener, OnLayoutChangeListener {

	private static MyHandler mHandler;

	private MainActivity mContext;

	private HttpUtil http;
	private MyToast mToast;

	private SQLiteDatabase sqlite;
	private MyListViewAdapter listViewAdapter1, listViewAdapter2, listViewAdapter3;

	private WeakReference<View> mRootView;
	private mIptFragmentListener mListener;

	private RelativeLayout layout1;
	private LinearLayout layout2, layout3, layout2Info, layout3Info;
	private MyListView mListView1, mListView2, mListView3;
	private TextView mDate, mOrderID, mOrderID3, mPCNum, mBuilding, mConveyID, mType, mCarNum, mStateNC, mTrayName, mGoodsName,
			mStateNC3;
	private Button mDetail_back, mDetail_commit, mDetail_save, mThird_back;

	private String NCState;
	private String TAG = "InPutFragment";
	private String mCurTaskID, mCurDetailTaskID;
	private String DB_TABLE = "input";
	private String DETAIL_TABLE = "input_detail";
	private String THIRD_TABLE = "input_third";

	private ArrayList<AdapterBean> mBeanList1 = new ArrayList<AdapterBean>();
	private ArrayList<AdapterBean> mBeanList2 = new ArrayList<AdapterBean>();
	private ArrayList<AdapterBean> mBeanList3 = new ArrayList<AdapterBean>();
	// private ArrayList<AdapterBean> mThirdDeleteList = new
	// ArrayList<AdapterBean>();

	private String sql_creat1 = "CREATE TABLE if not exists input(_taskid VARCHAR PRIMARY KEY, _date VARCHAR, _orderid VARCHAR, "
			+ " _type VARCHAR, _plannum VARCHAR, _pcnum VARCHAR, _building VARCHAR, _carnum VARCHAR,"
			+ "_statenc VARCHAR, _conveyid VARCHAR)";

	private String sql_creat2 = "CREATE TABLE if not exists input_detail(_taskid VARCHAR, _detailid VARCHAR PRIMARY KEY,"
			+ " _trayname VARCHAR, _goodsid VARCHAR, _goodsname VARCHAR, _spec VARCHAR, _plannum VARCHAR, _factnum VARCHAR, "
			+ "_planweight VARCHAR, _factweight VARCHAR)";

	private String sql_creat3 = "CREATE TABLE if not exists input_third(_taskid VARCHAR, _detailid VARCHAR, _thirdid VARCHAR PRIMARY KEY,"
			+ " _carid VARCHAR, _doorid VARCHAR, _barcode VARCHAR, _intime VARCHAR)";

	public interface mIptFragmentListener {
		public void hideMenu(boolean hide);

		public void btnCancel();
	}

	public InPutFragment(mIptFragmentListener mListener) {
		this.mListener = mListener;
	}

	// @Override
	// public void onActivityResult(int requestCode, int resultCode, Intent
	// data) {
	// if (requestCode == Content.ACTIVITY_REQUEST_CODE_IMAGE) {
	// if (resultCode == Activity.RESULT_OK) {
	// String mImageName = data.getStringExtra("newImageName");
	// AdapterBean bean = mBeanList3.get(mThirdClickPos);
	// bean.setPicture(mImageName);
	// listViewAdapter3.notifyDataSetChanged();
	// } else if (resultCode == Activity.RESULT_CANCELED) {
	//
	// }
	// mThirdClickPos = -1;
	// }
	// }

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
		Log.e("NTI", "InPutFragment........onCreateView()");
		if (mRootView == null || mRootView.get() == null) {
			View v = inflater.inflate(R.layout.fragment_1_input, container, false);
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
		Log.e("NTI", "InPutFragment........onHiddenChanged():" + hidden);
		if (!hidden && mRootView != null) {
			if (layout3.getVisibility() == View.VISIBLE) {
				mContext.setMyBackModel(Content.BACK_MODEL_IPT_THIRD);
			} else if (layout2.getVisibility() == View.VISIBLE) {
				mContext.setMyBackModel(Content.BACK_MODEL_IPT_DETAIL);
			} else {
				mContext.setMyBackModel(Content.BACK_MODEL_NONE);
			}
		}
		super.onHiddenChanged(hidden);
	}

	@Override
	public void onDestroy() {
		Log.e("NTI", "InPutFragment........onDestroy()");
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		if (Content.InputSoftOpen) {
			return;
		}
		switch (v.getId()) {
		case R.id.btn_ipt_detail_back:// 返回
			ShowMainTable();
			break;

		case R.id.btn_ipt_third_back:// 返回
			ShowDetailTable(-1);
			break;

		case R.id.btn_ipt_detail_save:// 保存
			SaveDetailData();
			break;

		// case R.id.btn_ipt_third_addlist:// 新建
		// NewThirdTask();
		// break;

		case R.id.btn_ipt_detail_commit: { // 反馈
			SaveDetailData();
			CommitData_Send();
		}
			break;

		default:
			break;
		}
	}

	private void BackPressed() {
		if (layout3.getVisibility() == View.VISIBLE) {
			ShowDetailTable(-1);
		} else if (layout2.getVisibility() == View.VISIBLE) {
			ShowMainTable();
		}
	}

	private void CommitData_Send() {
		String Action = "UPDATE";
		// String ImageUrl1, ImageUrl2, ImageUrl3, ImageUrl4;
		String TaskXML = "", DetailXML = "";
		// String DelDetailXML = "", DelThirdXML = "";

		// String Detail_Start = "";
		Cursor cursor = sqlite.query(DB_TABLE, null, "_taskid = ?", new String[] { mCurTaskID }, null, null, null);
		if (cursor != null && cursor.moveToFirst()) {
			TaskXML = "<Task taskId=\"" + cursor.getString(0) + "\" date=\"" + cursor.getString(1) + "\" TaskNumber=\""
					+ cursor.getString(2) + "\" typename=\"" + cursor.getString(3) + "\" ableNum=\"" + cursor.getString(4)
					+ "\" ComputerCode=\"" + cursor.getString(5) + "\" floorName=\"" + cursor.getString(6) + "\" carNum=\""
					+ cursor.getString(7) + "\" TransportCode=\"" + cursor.getString(9) + "\" > ";
			cursor.close();
		}
		cursor = null;
		Cursor cursor2 = sqlite.query(DETAIL_TABLE, null, "_taskid = ?", new String[] { mCurTaskID }, null, null, null);
		if (cursor2 != null && cursor2.moveToFirst()) {
			do {
				DetailXML = "<TaskDetail detailId=\"" + cursor2.getString(1) + "\" locationname=\"" + cursor2.getString(2)
						+ "\" itemcode=\"" + cursor2.getString(3) + "\" itemname=\"" + cursor2.getString(4) + "\" spec=\""
						+ cursor2.getString(5) + "\" ableNum=\"" + cursor2.getString(6) + "\" numberFact=\"" + cursor2.getString(7)
						+ "\" weightAble=\"" + cursor2.getString(8) + "\" weightFact=\"" + cursor2.getString(9) + "\" /> ";// 明细表XML
			} while (cursor2.moveToNext());
			cursor2.close();
		}
		cursor2 = null;

		final String UpdateXML = "<Request><Function>storageBackNC</Function><Action>" + Action + "</Action><Parms>" + TaskXML
				+ DetailXML + "</Task></Parms></Request>";
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (http == null) {
					http = HttpUtil.getInstance();
				}
				PostResult result = http.sendXML(UpdateXML);
				mHandler.obtainMessage(Content.SEND_XML_IPT_COMMIT, result).sendToTarget();
			}
		}).start();
	}

	private void CommitData_Rev(PostResult result) {// TODO,1反馈成功需判断图片是否需要上传，2反馈成功需删除Content.DB_NEED_DELETE
		if (result.isResult()) {
			// new Thread(upLoadImageFiles).start();
			ContentValues values = new ContentValues();
			values.put("_statenc", Content.HTTP_UPLOAD_TRUE);
			sqlite.update(DB_TABLE, values, "_taskid = ?", new String[] { mCurTaskID });
			// sqlite.delete(THIRD_TABLE, "_taskid = ? and _mark = ?", new
			// String[] { mCurTaskID, Content.DB_NEED_DELETE });
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

	// private File GetMediaFile(String imageName) {
	// if (mediaStorageDir == null) {
	// mediaStorageDir = new
	// File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
	// "XMISA_image");
	// }
	// if (!mediaStorageDir.exists()) {
	// if (!mediaStorageDir.mkdirs()) {
	// return null;
	// }
	// }
	// File mediaFile;
	// mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	// imageName + ".jpg");
	// return mediaFile;
	// }

	private void InitLayout(View view) {
		if (mListView1 == null) {
			view.addOnLayoutChangeListener(this);

			mDate = (TextView) view.findViewById(R.id.tv_ipt_detail_date);
			mOrderID = (TextView) view.findViewById(R.id.tv_ipt_detail_orderid);
			mPCNum = (TextView) view.findViewById(R.id.tv_ipt_detail_pcnum);
			mBuilding = (TextView) view.findViewById(R.id.tv_ipt_detail_building);
			mConveyID = (TextView) view.findViewById(R.id.tv_ipt_detail_conveyid);
			mType = (TextView) view.findViewById(R.id.tv_ipt_detail_type);
			mCarNum = (TextView) view.findViewById(R.id.tv_ipt_detail_carnum);
			mStateNC = (TextView) view.findViewById(R.id.tv_ipt_detail_statenc);

			mOrderID3 = (TextView) view.findViewById(R.id.tv_ipt_third_orderid);
			mTrayName = (TextView) view.findViewById(R.id.tv_ipt_third_trayname);
			mGoodsName = (TextView) view.findViewById(R.id.tv_ipt_third_goodsname);
			mStateNC3 = (TextView) view.findViewById(R.id.tv_ipt_third_statenc);

			mDetail_back = (Button) view.findViewById(R.id.btn_ipt_detail_back);
			mDetail_save = (Button) view.findViewById(R.id.btn_ipt_detail_save);
			mDetail_commit = (Button) view.findViewById(R.id.btn_ipt_detail_commit);

			mThird_back = (Button) view.findViewById(R.id.btn_ipt_third_back);
			// mThird_save = (Button)
			// view.findViewById(R.id.btn_ipt_third_save);
			// mThird_add = (Button)
			// view.findViewById(R.id.btn_ipt_third_addlist);

			layout1 = (RelativeLayout) view.findViewById(R.id.layout_input);
			layout2 = (LinearLayout) view.findViewById(R.id.layout_ipt_detail);
			layout3 = (LinearLayout) view.findViewById(R.id.layout_ipt_third);
			layout2Info = (LinearLayout) view.findViewById(R.id.layout_ipt_detail_info);
			layout3Info = (LinearLayout) view.findViewById(R.id.layout_ipt_third_info);

			mDetail_back.setOnClickListener(this);
			mDetail_save.setOnClickListener(this);
			mDetail_commit.setOnClickListener(this);
			mThird_back.setOnClickListener(this);
			// mThird_save.setOnClickListener(this);
			// mThird_add.setOnClickListener(this);

			mListView1 = (MyListView) view.findViewById(R.id.list_input);
			mListView2 = (MyListView) view.findViewById(R.id.list_ipt_detail);
			mListView3 = (MyListView) view.findViewById(R.id.list_ipt_third);

			mListView1.setOnRefreshListener(refreshListener, Content.LIST_INPUT);
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
				bean.setConveyID(cursor.getString(9));
				mBeanList1.add(bean);
			} while (cursor.moveToNext());
			cursor.close();
		}
		cursor = null;
		listViewAdapter1 = new MyListViewAdapter(mContext, R.layout.list_input, mBeanList1, Content.LIST_INPUT, null);
	}

	private void InitListViewAdapter2() {
		sqlite.execSQL(sql_creat2);
		listViewAdapter2 = new MyListViewAdapter(mContext, R.layout.list_ipt_detail, mBeanList2, Content.LIST_INPUT_DETAIL,
				listViewAdapterCallBack2);
		mListView2.setAdapter(listViewAdapter2);
	}

	private void InitListViewAdapter3() {
		sqlite.execSQL(sql_creat3);
		listViewAdapter3 = new MyListViewAdapter(mContext, R.layout.list_ipt_third, mBeanList3, Content.LIST_INPUT_THIRD, null);
		mListView3.setAdapter(listViewAdapter3);
	}

	private void LoadData_Send() {
		final String xmlString = "<Request><Function>storageTask</Function><Parms><Username>" + Content.USER_SYS_CODE
				+ "</Username></Parms></Request>";
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (http == null) {
					http = HttpUtil.getInstance();
				}
				PostResult result = http.sendXML(xmlString);
				MyLog.e(TAG, "LoadData_Send", "result "+((result == null)?"==":"!=") + "null");
				if (result != null) {
					mHandler.obtainMessage(Content.SEND_XML_IPT_LOAD, result).sendToTarget();
				}
			}
		}).start();
	}

	private void LoadData_Rev(PostResult result) {
		if (result.isResult()) {
			DeleteTable(DB_TABLE, sql_creat1);
			DeleteTable(DETAIL_TABLE, sql_creat2);
			DeleteTable(THIRD_TABLE, sql_creat3);
			UpdateSQLiteData(result.getInPutBeanList());
			UpDateListView1();
		} else {
			mToast.show(result.getMessage());
		}
		mListView1.onRefreshComplete(result.isResult(), Content.LIST_INPUT);

	}

	// private void NewThirdTask() {
	// AdapterBean bean = new AdapterBean();
	// String guid = UUID.randomUUID().toString().replace("-", "");
	// bean.setTaskID(guid);
	// bean.setBarCode("");
	// bean.setAppearance("正常");
	// bean.setMould("正常");
	// bean.setMothy("正常");
	// bean.setOdor("正常");
	// bean.setImpurity("正常");
	// bean.setPicture(" / / / ");
	// bean.setMark(Content.DB_DATA_TEMP);
	// mBeanList3.add(bean);
	// listViewAdapter3.notifyDataSetChanged();
	// }

	private void SaveDetailData() {
		ContentValues values = new ContentValues();
		for (int i = 0; i < mBeanList2.size(); i++) {
			values.clear();
			String DetailID = mBeanList2.get(i).getTaskID();
			String FactNum = mBeanList2.get(i).getFactNum();
			String FactWeight = mBeanList2.get(i).getFactWeight();
			values.put("_factnum", FactNum);
			values.put("_factweight", FactWeight);
			sqlite.update(DETAIL_TABLE, values, "_detailid = ?", new String[] { DetailID });
		}
		mToast.show("保存成功！");
	}

	// for (int i = 0; i < mThirdDeleteList.size(); i++) {
	// String ThirdTaskID = mThirdDeleteList.get(i).getTaskID();
	// if (mThirdDeleteList.get(i).getMark().equals(Content.DB_DATA_LOCAL)) {
	// sqlite.delete(THIRD_TABLE, "_thirdid = ?", new String[] { ThirdTaskID });
	// } else if
	// (mThirdDeleteList.get(i).getMark().equals(Content.DB_DATA_SERVER)) {
	// values.put("_mark", Content.DB_NEED_DELETE);
	// sqlite.update(THIRD_TABLE, values, "_thirdid = ?", new String[] {
	// ThirdTaskID });
	// values.clear();
	// }
	// }
	// mThirdDeleteList.clear();

	// String DetailAppearance = "正常";
	// String DetailMould = "正常";
	// String DetailMothy = "正常";
	// String DetailOdor = "正常";
	// String DetailImpurity = "正常";
	// for (int i = 0; i < mBeanList2.size(); i++) {
	// values.clear();
	// String ThirdId = mBeanList3.get(i).getTaskID();
	// String BarCode = mBeanList3.get(i).getBarCode();
	// String Appearance = mBeanList3.get(i).getAppearance();
	// String Mould = mBeanList3.get(i).getMould();
	// String Mothy = mBeanList3.get(i).getMothy();
	// String Odor = mBeanList3.get(i).getOdor();
	// String Impurity = mBeanList3.get(i).getImpurity();
	// String Picture = mBeanList3.get(i).getPicture();

	// DetailAppearance = (Appearance.equals("异常")) ? Appearance :
	// DetailAppearance;
	// DetailMould = (Mould.equals("异常")) ? Mould : DetailMould;
	// DetailMothy = (Mothy.equals("异常")) ? Mothy : DetailMothy;
	// DetailOdor = (Odor.equals("异常")) ? Odor : DetailOdor;
	// DetailImpurity = (Impurity.equals("异常")) ? Impurity : DetailImpurity;

	// values.put("_barcode", BarCode);
	// values.put("_appearance", Appearance);
	// values.put("_mould", Mould);
	// values.put("_mothy", Mothy);
	// values.put("_odor", Odor);
	// values.put("_impurity", Impurity);
	// values.put("_picture", Picture);

	// if (mBeanList3.get(i).getMark().equals(Content.DB_DATA_TEMP)) {
	// values.put("_taskid", mCurTaskID);
	// values.put("_detailid", mCurDetailTaskID);
	// values.put("_thirdid", ThirdId);
	// values.put("_mark", Content.DB_DATA_LOCAL);
	// sqlite.insert(THIRD_TABLE, null, values);
	// } else {
	// sqlite.update(THIRD_TABLE, values, "_thirdid = ?", new String[] { ThirdId
	// });
	// }
	// }
	// values.clear();
	// values.put("_appearance", DetailAppearance);
	// values.put("_mould", DetailMould);
	// values.put("_mothy", DetailMothy);
	// values.put("_odor", DetailOdor);
	// values.put("_impurity", DetailImpurity);
	// sqlite.update(DETAIL_TABLE, values, "_detailid = ?", new String[] {
	// mCurDetailTaskID });
	// mToast.show("保存成功！");
	// }

	// private void SaveThirdData() {
	// ContentValues values = new ContentValues();
	//
	// for (int i = 0; i < mThirdDeleteList.size(); i++) {
	// String ThirdTaskID = mThirdDeleteList.get(i).getTaskID();
	// if (mThirdDeleteList.get(i).getMark().equals(Content.DB_DATA_LOCAL)) {
	// sqlite.delete(THIRD_TABLE, "_thirdid = ?", new String[] { ThirdTaskID });
	// } else if
	// (mThirdDeleteList.get(i).getMark().equals(Content.DB_DATA_SERVER)) {
	// values.put("_mark", Content.DB_NEED_DELETE);
	// sqlite.update(THIRD_TABLE, values, "_thirdid = ?", new String[] {
	// ThirdTaskID });
	// values.clear();
	// }
	// }
	// mThirdDeleteList.clear();
	//
	// String DetailAppearance = "正常";
	// String DetailMould = "正常";
	// String DetailMothy = "正常";
	// String DetailOdor = "正常";
	// String DetailImpurity = "正常";
	// for (int i = 0; i < mBeanList3.size(); i++) {
	// values.clear();
	// String ThirdId = mBeanList3.get(i).getTaskID();
	// String BarCode = mBeanList3.get(i).getBarCode();
	// String Appearance = mBeanList3.get(i).getAppearance();
	// String Mould = mBeanList3.get(i).getMould();
	// String Mothy = mBeanList3.get(i).getMothy();
	// String Odor = mBeanList3.get(i).getOdor();
	// String Impurity = mBeanList3.get(i).getImpurity();
	// String Picture = mBeanList3.get(i).getPicture();
	//
	// DetailAppearance = (Appearance.equals("异常")) ? Appearance :
	// DetailAppearance;
	// DetailMould = (Mould.equals("异常")) ? Mould : DetailMould;
	// DetailMothy = (Mothy.equals("异常")) ? Mothy : DetailMothy;
	// DetailOdor = (Odor.equals("异常")) ? Odor : DetailOdor;
	// DetailImpurity = (Impurity.equals("异常")) ? Impurity : DetailImpurity;
	//
	// values.put("_barcode", BarCode);
	// values.put("_appearance", Appearance);
	// values.put("_mould", Mould);
	// values.put("_mothy", Mothy);
	// values.put("_odor", Odor);
	// values.put("_impurity", Impurity);
	// values.put("_picture", Picture);
	//
	// if (mBeanList3.get(i).getMark().equals(Content.DB_DATA_TEMP)) {
	// values.put("_taskid", mCurTaskID);
	// values.put("_detailid", mCurDetailTaskID);
	// values.put("_thirdid", ThirdId);
	// values.put("_mark", Content.DB_DATA_LOCAL);
	// sqlite.insert(THIRD_TABLE, null, values);
	// } else {
	// sqlite.update(THIRD_TABLE, values, "_thirdid = ?", new String[] { ThirdId
	// });
	// }
	// }
	// values.clear();
	// values.put("_appearance", DetailAppearance);
	// values.put("_mould", DetailMould);
	// values.put("_mothy", DetailMothy);
	// values.put("_odor", DetailOdor);
	// values.put("_impurity", DetailImpurity);
	// sqlite.update(DETAIL_TABLE, values, "_detailid = ?", new String[] {
	// mCurDetailTaskID });
	// mToast.show("保存成功！");
	// }

	private void ShowInputSoft() {
		if (layout2.getVisibility() == View.VISIBLE) {
			if (mListener != null && Content.InputSoftOpen) {
				mListener.hideMenu(true);
				listViewAdapter2.setInputOpen(true);
				layout2Info.setVisibility(View.GONE);
			} else if (mListener != null) {
				mListener.hideMenu(false);
				listViewAdapter2.setInputOpen(false);
				layout2Info.setVisibility(View.VISIBLE);
			}
		} else if (layout3.getVisibility() == View.VISIBLE) {
			if (mListener != null && Content.InputSoftOpen) {
				mListener.hideMenu(true);
				listViewAdapter3.setInputOpen(true);
				layout3Info.setVisibility(View.GONE);
			} else if (mListener != null) {
				mListener.hideMenu(false);
				listViewAdapter3.setInputOpen(false);
				layout3Info.setVisibility(View.VISIBLE);
			}
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
		if (listViewAdapter2 == null) {
			InitListViewAdapter2();
		}
		if (pos > 0) {
			AdapterBean bean = mBeanList1.get(pos - 1);
			mCurTaskID = bean.getTaskID();
			mDate.setText(bean.getDate());
			mOrderID.setText(bean.getOrderID());
			mPCNum.setText(bean.getPCNum());
			mBuilding.setText(bean.getBuilding());
			mConveyID.setText(bean.getConveyID());
			mType.setText(bean.getType());
			mCarNum.setText(bean.getCarNum());
			NCState = bean.getNCState();
			mStateNC.setText(NCState);
		}
		UpDateListView2();
		mContext.setMyBackModel(Content.BACK_MODEL_IPT_DETAIL);
		if (layout1.getVisibility() == View.VISIBLE) {
			layout1.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_layout_left_hide));
			layout2.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_layout_left_show));
			layout1.setVisibility(View.GONE);
			layout2.setVisibility(View.VISIBLE);
		} else if (layout3.getVisibility() == View.VISIBLE) {
			// mThirdDeleteList.clear();
			layout2.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_layout_right_show));
			layout3.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_layout_right_hide));
			layout2.setVisibility(View.VISIBLE);
			layout3.setVisibility(View.GONE);
		}
	}

	private void ShowThirdTable(int pos) {
		if (listViewAdapter3 == null) {
			InitListViewAdapter3();
		}
		AdapterBean bean = mBeanList2.get(pos);
		mCurDetailTaskID = bean.getTaskID();
		mOrderID3.setText(mOrderID.getText());
		mTrayName.setText(bean.getTrayName());
		mGoodsName.setText(bean.getGoodsName());
		mStateNC3.setText(NCState);
		UpDateListView3();
		mContext.setMyBackModel(Content.BACK_MODEL_IPT_THIRD);
		if (layout2.getVisibility() == View.VISIBLE) {
			layout2.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_layout_left_hide));
			layout3.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_layout_left_show));
			layout2.setVisibility(View.GONE);
			layout3.setVisibility(View.VISIBLE);
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
				bean.setConveyID(cursor.getString(9));
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
				bean.setTrayName(cursor.getString(2));
				bean.setGoodsID(cursor.getString(3));
				bean.setGoodsName(cursor.getString(4));
				bean.setSpec(cursor.getString(5));
				bean.setPlanNum(cursor.getString(6));
				bean.setFactNum(cursor.getString(7));
				bean.setPlanWeight(cursor.getString(8));
				bean.setFactWeight(cursor.getString(9));
				mBeanList2.add(bean);
			} while (cursor.moveToNext());
			cursor.close();
		}
		cursor = null;
		listViewAdapter2.setNCState(NCState.equals(StringUtils.IsUpLoad.YES));
		listViewAdapter2.notifyDataSetChanged();
	}

	private void UpDateListView3() {
		mBeanList3.clear();
		Cursor cursor = sqlite.query(THIRD_TABLE, null, "_detailid = ?", new String[] { mCurDetailTaskID }, null, null, null);
		if (cursor != null && cursor.moveToFirst()) {
			do {
				AdapterBean bean = new AdapterBean();
				bean.setPPTaskID(cursor.getString(0));
				bean.setPTaskID(cursor.getString(1));
				bean.setTaskID(cursor.getString(2));
				bean.setCarID(cursor.getString(3));
				bean.setDoorID(cursor.getString(4));
				bean.setBarCode(cursor.getString(5));
				bean.setInTime(cursor.getString(6));
				mBeanList3.add(bean);
			} while (cursor.moveToNext());
			cursor.close();
		}
		cursor = null;
		listViewAdapter3.setNCState(NCState.equals(StringUtils.IsUpLoad.YES));
		listViewAdapter3.notifyDataSetChanged();
	}

	private void UpdateSQLiteData(List<InPutBean> mBeanList) {
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
			cv.put("_conveyid", mBeanList.get(i).getConveyId());
			if (mBeanList.get(i).getStateNC().equals("0")) {
				cv.put("_statenc", Content.HTTP_UPLOAD_FALSE);
			} else {
				cv.put("_statenc", Content.HTTP_UPLOAD_TRUE);
			}
			sqlite.insert(DB_TABLE, null, cv);
			List<InPutDetailBean> mDetailBeanList = mBeanList.get(i).getDetailBeanList();
			if (mDetailBeanList != null && mDetailBeanList.size() > 0) {
				for (int j = 0; j < mDetailBeanList.size(); j++) {
					cv.clear();
					cv.put("_taskid", mDetailBeanList.get(j).getTaskId());
					cv.put("_detailid", mDetailBeanList.get(j).getDetailId());
					cv.put("_trayname", mDetailBeanList.get(j).getTrayName());
					cv.put("_goodsid", mDetailBeanList.get(j).getGoodsID());
					cv.put("_goodsname", mDetailBeanList.get(j).getGoodsName());
					cv.put("_spec", mDetailBeanList.get(j).getSpec());
					cv.put("_plannum", mDetailBeanList.get(j).getPlanNum());
					cv.put("_factnum", mDetailBeanList.get(j).getFactNum());
					cv.put("_planweight", mDetailBeanList.get(j).getPlanWeight());
					cv.put("_factweight", mDetailBeanList.get(j).getFactWeight());
					sqlite.insert(DETAIL_TABLE, null, cv);
					List<InPutThirdBean> mThirdBeanList = mDetailBeanList.get(j).getThirdBeanList();
					if (mThirdBeanList != null && mThirdBeanList.size() > 0) {
						for (int k = 0; k < mThirdBeanList.size(); k++) {
							cv.clear();
							cv.put("_taskid", mThirdBeanList.get(k).getTaskId());
							cv.put("_detailid", mThirdBeanList.get(k).getDetailId());
							cv.put("_thirdid", mThirdBeanList.get(k).getThirdId());
							cv.put("_carid", mThirdBeanList.get(k).getCarId());
							cv.put("_doorid", mThirdBeanList.get(k).getDoorId());
							cv.put("_barcode", mThirdBeanList.get(k).getBarCode());
							cv.put("_intime", mThirdBeanList.get(k).getInTime());
							sqlite.insert(THIRD_TABLE, null, cv);
						}
					}
				}
			}
		}
		sqlite.setTransactionSuccessful();
		sqlite.endTransaction();
	}

	private static class MyHandler extends Handler {
		WeakReference<InPutFragment> mFragment;

		MyHandler(InPutFragment inputFragment) {
			mFragment = new WeakReference<InPutFragment>(inputFragment);
		}

		@Override
		public void handleMessage(Message msg) {
			InPutFragment theFragment = mFragment.get();
			if (theFragment == null) {
				return;
			}
			switch (msg.what) {
			case Content.SEND_XML_IPT_LOAD:
				theFragment.LoadData_Rev((PostResult) msg.obj);
				break;

			case Content.SEND_XML_IPT_COMMIT:
				theFragment.CommitData_Rev((PostResult) msg.obj);
				break;

			case Content.BACK_PRESSED:
				// theFragment.ShowMainTable();
				theFragment.BackPressed();
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

	private ListViewAdapterCallBack listViewAdapterCallBack2 = new ListViewAdapterCallBack() {

		@Override
		public void onItemClick(View v, int pos, AdapterBean bean) {
			if (layout2Info.getVisibility() != View.GONE) {// 输入法展开时屏蔽点击事件
				ShowThirdTable(pos);
			}
		}

		@Override
		public void onItemDelete(View v, int mPos) {

		}

		@Override
		public void onItemUpdate(View v, int mPos, String newValue1) {

		}

	};

	// private ListViewAdapterCallBack listViewAdapterCallBack3 = new
	// ListViewAdapterCallBack() {
	//
	// @Override
	// public void onItemClick(View v, int pos, AdapterBean bean) {
	// mThirdClickPos = pos;
	// String[] imageNames = bean.getPicture().split("/");
	// Intent intent = new Intent();
	// intent.setClass(mContext, ImageActivity.class);
	// intent.putExtra("taskId", bean.getTaskID());
	// intent.putExtra("NCState", NCState);
	// intent.putExtra("imageName1", imageNames[0]);
	// intent.putExtra("imageName2", imageNames[1]);
	// intent.putExtra("imageName3", imageNames[2]);
	// intent.putExtra("imageName4", imageNames[3]);
	// intent.putExtra("imageVersion", bean.getPictureVersion());
	// startActivityForResult(intent, Content.ACTIVITY_REQUEST_CODE_IMAGE);
	// // 点击反馈之后，反馈成功则删除旧文件
	// }
	//
	// @Override
	// public void onItemDelete(View v, int mPos) {
	// AdapterBean removeBean = mBeanList3.get(mPos);
	// mThirdDeleteList.add(removeBean);
	// mBeanList3.remove(mPos);
	// listViewAdapter3.notifyDataSetChanged();
	// }
	//
	// @Override
	// public void onItemUpdate(View v, int mPos, String newValue1) {
	//
	// }
	//
	// };

	// private Runnable upLoadImageFiles = new Runnable() {
	// @Override
	// public void run() {
	// LinkedList<File> fileList = new LinkedList<File>();
	// for (int i = 0; i < finalImageName.size(); i++) {
	// File file = GetMediaFile(finalImageName.get(i));
	// if (file != null && file.exists()) {
	// fileList.add(file);
	// }
	// }
	// finalImageName.clear();
	// if (fileList.size() > 0) {
	// try {
	// mFTP = new FTPUtil(5, 5, 5);
	// mFTP.uploadMultiFile(fileList, Content.FTP_IMAGE_DIRECTORY,
	// mUpLoadListener);
	// mToast.show(Content.FTP_IMAGE_UPLOAD_SUCCESSS);
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	// }
	// };

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
