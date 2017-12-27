package com.nti56.xmisa.fragment.maintain;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import com.nti56.xmisa.bean.AdapterBean;
import com.nti56.xmisa.bean.SurveyBean;
import com.nti56.xmisa.bean.SurveyDetailBean;
import com.nti56.xmisa.bean.SurveyThirdBean;
import com.nti56.xmisa.adapter.MyListView.OnRefreshListener;

import com.nti56.xmisa.ImageActivity;
import com.nti56.xmisa.R;
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
import com.nti56.xmisa.util.StringUtils;
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
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SurveyFragment extends Fragment implements OnClickListener, OnLayoutChangeListener {

	private static MyHandler mHandler;

	private MainActivity mContext;

	private FTPUtil mFTP;
	private HttpUtil http;
	private MyToast mToast;

	private File mediaStorageDir;
	private SQLiteDatabase sqlite;
	private MyListViewAdapter listViewAdapter1, listViewAdapter2, listViewAdapter3;

	private int mThirdClickPos = -1;

	private WeakReference<View> mRootView;
	private mSurFragmentListener mListener;

	private RelativeLayout layout1;
	private LinearLayout layout2, layout3, layout2Info, layout3Info;
	private MyListView mListView1, mListView2, mListView3;
	private TextView mDate, mOrderID, mOrderID3, mBuilding, mTrayName, mGoodsName, mFloor, mStateNC, mStateNC3;
	private Button mDetail_back, mDetail_commit, mDetail_scan, mThird_back, mThird_save, mThird_add;

	private String NCState;
	private String TAG = "SurveyFragment";
	private String mCurTaskID, mCurDetailTaskID;
	private String DB_TABLE = "survey";
	private String DETAIL_TABLE = "survey_detail";
	private String THIRD_TABLE = "survey_third";

	private ArrayList<String> finalImageName = new ArrayList<String>();

	private ArrayList<AdapterBean> mBeanList1 = new ArrayList<AdapterBean>();
	private ArrayList<AdapterBean> mBeanList2 = new ArrayList<AdapterBean>();
	private ArrayList<AdapterBean> mBeanList3 = new ArrayList<AdapterBean>();
	private ArrayList<AdapterBean> mThirdDeleteList = new ArrayList<AdapterBean>();

	private String sql_creat1 = "CREATE TABLE if not exists survey(_taskid VARCHAR PRIMARY KEY, _date VARCHAR, _orderid VARCHAR, "
			+ "_building VARCHAR, _floor VARCHAR, _detailnum VARCHAR, _person VARCHAR, _statenc VARCHAR)";

	private String sql_creat2 = "CREATE TABLE if not exists survey_detail(_taskid VARCHAR, _detailid VARCHAR PRIMARY KEY,"
			+ " _trayname VARCHAR, _goodsid VARCHAR, _goodsname VARCHAR, _traynum VARCHAR, _checknum VARCHAR, _colligate VARCHAR, _appearance VARCHAR, _color VARCHAR,"
			+ " _waterfell VARCHAR, _mould VARCHAR, _oiltrace VARCHAR, _mothy VARCHAR, _odor VARCHAR, _impurity VARCHAR)";

	private String sql_creat3 = "CREATE TABLE if not exists survey_third(_taskid VARCHAR, _detailid VARCHAR, _thirdid VARCHAR PRIMARY KEY,"
			+ " _barcode VARCHAR, _colligate VARCHAR, _appearance VARCHAR, _color VARCHAR, _waterfell VARCHAR, _mould VARCHAR, _oiltrace VARCHAR, _mothy VARCHAR,"
			+ " _odor VARCHAR, _impurity VARCHAR, _picture VARCHAR, _picturever VARCHAR, _mark VARCHAR)";

	public interface mSurFragmentListener {
		public void hideMenu(boolean hide);

		public void btnCancel();
	}

	public SurveyFragment(mSurFragmentListener mListener) {
		this.mListener = mListener;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Content.ACTIVITY_REQUEST_CODE_IMAGE) {
			if (resultCode == Activity.RESULT_OK) {
				String mImageName = data.getStringExtra("newImageName");
				AdapterBean bean = mBeanList3.get(mThirdClickPos);
				bean.setPicture(mImageName);
				listViewAdapter3.notifyDataSetChanged();
			} else if (resultCode == Activity.RESULT_CANCELED) {

			}
			mThirdClickPos = -1;
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
		Log.e("NTI", "SurveyFragment........onCreateView()");
		if (mRootView == null || mRootView.get() == null) {
			View v = inflater.inflate(R.layout.fragment_3_survey, container, false);
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
		Log.e("NTI", "SurveyFragment........onHiddenChanged():" + hidden);
		if (!hidden && mRootView != null) {
			if (layout3.getVisibility() == View.VISIBLE) {
				mContext.setMyBackModel(Content.BACK_MODEL_SUR_THIRD);
			} else if (layout2.getVisibility() == View.VISIBLE) {
				mContext.setMyBackModel(Content.BACK_MODEL_SUR_DETAIL);
			} else {
				mContext.setMyBackModel(Content.BACK_MODEL_NONE);
			}
		}
		super.onHiddenChanged(hidden);
	}

	@Override
	public void onDestroy() {
		Log.e("NTI", "SurveyFragment........onDestroy()");
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		if (Content.InputSoftOpen) {
			return;
		}
		switch (v.getId()) {
		case R.id.btn_sur_detail_back:// 返回
			ShowMainTable();
			break;

		case R.id.btn_sur_third_back:// 返回
			ShowDetailTable(-1);
			break;

		case R.id.btn_sur_third_save:// 保存
			SaveThirdData();
			break;

		case R.id.btn_sur_third_addlist:// 新建
			NewThirdTask();
			break;

		case R.id.btn_sur_detail_commit: { // 反馈
			// SaveDetailData();
			CommitData_Send();
		}
			break;

		case R.id.btn_sur_detail_scan:
			// mContext.scanRFID(this, "XZ");
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
		String ImageUrl1, ImageUrl2, ImageUrl3, ImageUrl4;
		String TaskXML = "", DetailXML = "", ThirdXML = "";
		String DelDetailXML = "", DelThirdXML = "";

		String Detail_Start = "";
		Cursor cursor = sqlite.query(DB_TABLE, null, "_taskid = ?", new String[] { mCurTaskID }, null, null, null);
		if (cursor != null && cursor.moveToFirst()) {
			TaskXML = "<Task TaskId=\"" + cursor.getString(0) + "\" Date=\"" + cursor.getString(1) + "\" TaskNumber=\""
					+ cursor.getString(2) + "\" Ban=\"" + cursor.getString(3) + "\" FloorLay=\"" + cursor.getString(4) + "\" Dispatcher=\""
					+ Content.USER_SYS_CODE + "\" > ";
			cursor.close();
		}
		
		cursor = null;
		Cursor cursor2 = sqlite.query(DETAIL_TABLE, null, "_taskid = ?", new String[] { mCurTaskID }, null, null, null);
		finalImageName.clear();
		if (cursor2 != null && cursor2.moveToFirst()) {
			do {
				Detail_Start = "<TaskDetail DetailId=\"" + cursor2.getString(1) + "\" LocationName=\"" + cursor2.getString(2)
						+ "\" ItemCode=\"" + cursor2.getString(3) + "\" ItemName=\"" + cursor2.getString(4) + "\" colligate=\""
						+ cursor2.getString(7) + "\" appearance=\"" + cursor2.getString(8) + "\" color=\"" + cursor2.getString(9)
						+ "\" wet=\"" + cursor2.getString(10) + "\" mould=\"" + cursor2.getString(11) + "\" oil=\"" + cursor2.getString(12)
						+ "\" cq=\"" + cursor2.getString(13) + "\" odor=\"" + cursor2.getString(14) + "\" varia=\"" + cursor2.getString(15)
						+ "\" > ";// 明细表xml上部分
				ThirdXML = "";
				DelThirdXML = "";
				String DetailID = cursor2.getString(1);
				Cursor cursor3 = sqlite.query(THIRD_TABLE, null, "_detailid = ?", new String[] { DetailID }, null, null, null);

				if (cursor3 != null && cursor3.moveToFirst()) {
					do {
						String[] imageName = cursor3.getString(13).split("/");
						ImageUrl1 = (imageName[0].length() > 37) ? (Content.getFtpUrl() + imageName[0] + ".jpg") : " ";
						ImageUrl2 = (imageName[1].length() > 37) ? (Content.getFtpUrl() + imageName[1] + ".jpg") : " ";
						ImageUrl3 = (imageName[2].length() > 37) ? (Content.getFtpUrl() + imageName[2] + ".jpg") : " ";
						ImageUrl4 = (imageName[3].length() > 37) ? (Content.getFtpUrl() + imageName[3] + ".jpg") : " ";
						for (int i = 0; i < imageName.length; i++) {
							finalImageName.add(imageName[i]);
						}
						if (cursor3.getString(15).equals(Content.DB_NEED_DELETE)) {
							DelThirdXML += "<Result ChildId=\"" + cursor3.getString(2) + "\" Code=\"" + cursor3.getString(3)
									+ "\" colligate=\"" + cursor3.getString(4) + "\" appearance=\"" + cursor3.getString(5)
									+ "\" color=\"" + cursor3.getString(6) + "\" wet=\"" + cursor3.getString(7)
									+ "\" mould=\"" + cursor3.getString(8) + "\" oil=\"" + cursor3.getString(9)
									+ "\" cq=\"" + cursor3.getString(10) + "\" odor=\"" + cursor3.getString(11)
									+ "\" varia=\"" + cursor3.getString(12) + "\" ImageUrl1=\"" + ImageUrl1
									+ "\" ImageUrl2=\"" + ImageUrl2 + "\" ImageUrl3=\"" + ImageUrl3
									+ "\" ImageUrl4=\"" + ImageUrl4 + "\" /> ";// 需删除的子子表xml
						} else {
							ThirdXML += "<Result " + "TaskId=\"" + cursor3.getString(0) + "\" DetailId=\"" + cursor3.getString(1)
									+ "\" ChildId=\"" + cursor3.getString(2) + "\" Code=\"" + cursor3.getString(3)
									+ "\" colligate=\"" + cursor3.getString(4) + "\" appearance=\"" + cursor3.getString(5)
									+ "\" color=\"" + cursor3.getString(6) + "\" wet=\"" + cursor3.getString(7)
									+ "\" mould=\"" + cursor3.getString(8) + "\" oil=\"" + cursor3.getString(9)
									+ "\" cq=\"" + cursor3.getString(10) + "\" odor=\"" + cursor3.getString(11)
									+ "\" varia=\"" + cursor3.getString(12) + "\" ImageUrl1=\"" + ImageUrl1
									+ "\" ImageUrl2=\"" + ImageUrl2 + "\" ImageUrl3=\"" + ImageUrl3
									+ "\" ImageUrl4=\"" + ImageUrl4 + "\" /> ";// 子子表xml
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
				: ("<Request><Function>gcheckNC</Function><Action>DELETEDETAIL</Action><Parms>" + TaskXML + DelDetailXML + "</Task></Parms></Request>");
		final String UpdateXML = "<Request><Function>gcheckNC</Function><Action>" + Action + "</Action><Parms>" + TaskXML + DetailXML
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
				mHandler.obtainMessage(Content.SEND_XML_SUR_COMMIT, result).sendToTarget();
			}
		}).start();
	}

	private void CommitData_Rev(PostResult result) {// TODO,1反馈成功需判断图片是否需要上传，2反馈成功需删除Content.DB_NEED_DELETE
		if (result.isResult()) {
			new Thread(upLoadImageFiles).start();
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

			mDate = (TextView) view.findViewById(R.id.tv_sur_detail_date);
			mOrderID = (TextView) view.findViewById(R.id.tv_sur_detail_orderid);
			mBuilding = (TextView) view.findViewById(R.id.tv_sur_detail_building);
			mFloor = (TextView) view.findViewById(R.id.tv_sur_detail_floor);
			mStateNC = (TextView) view.findViewById(R.id.tv_sur_detail_statenc);

			mOrderID3 = (TextView) view.findViewById(R.id.tv_sur_third_orderid);
			mTrayName = (TextView) view.findViewById(R.id.tv_sur_third_trayname);
			mGoodsName = (TextView) view.findViewById(R.id.tv_sur_third_goodsname);
			mStateNC3 = (TextView) view.findViewById(R.id.tv_sur_third_statenc);

			mDetail_back = (Button) view.findViewById(R.id.btn_sur_detail_back);
			mDetail_scan = (Button) view.findViewById(R.id.btn_sur_detail_scan);
			mDetail_commit = (Button) view.findViewById(R.id.btn_sur_detail_commit);

			mThird_back = (Button) view.findViewById(R.id.btn_sur_third_back);
			mThird_save = (Button) view.findViewById(R.id.btn_sur_third_save);
			mThird_add = (Button) view.findViewById(R.id.btn_sur_third_addlist);

			layout1 = (RelativeLayout) view.findViewById(R.id.layout_survey);
			layout2 = (LinearLayout) view.findViewById(R.id.layout_sur_detail);
			layout3 = (LinearLayout) view.findViewById(R.id.layout_sur_third);
			layout2Info = (LinearLayout) view.findViewById(R.id.layout_sur_detail_info);
			layout3Info = (LinearLayout) view.findViewById(R.id.layout_sur_third_info);

			mDetail_back.setOnClickListener(this);
			mDetail_scan.setOnClickListener(this);
			mDetail_commit.setOnClickListener(this);
			mThird_back.setOnClickListener(this);
			mThird_save.setOnClickListener(this);
			mThird_add.setOnClickListener(this);

			mListView1 = (MyListView) view.findViewById(R.id.list_survey);
			mListView2 = (MyListView) view.findViewById(R.id.list_sur_detail);
			mListView3 = (MyListView) view.findViewById(R.id.list_sur_third);

			mListView1.setOnRefreshListener(refreshListener, Content.LIST_SURVEY);
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
				bean.setFloor(cursor.getString(4));
				bean.setDetailNum(cursor.getString(5));
				bean.setPerson(cursor.getString(6));
				bean.setNCState(cursor.getString(7));
				mBeanList1.add(bean);
			} while (cursor.moveToNext());
			cursor.close();
		}
		cursor = null;
		listViewAdapter1 = new MyListViewAdapter(mContext, R.layout.list_survey, mBeanList1, Content.LIST_SURVEY, null);
	}

	private void InitListViewAdapter2() {
		sqlite.execSQL(sql_creat2);
		listViewAdapter2 = new MyListViewAdapter(mContext, R.layout.list_sur_detail, mBeanList2, Content.LIST_SURVEY_DETAIL,
				listViewAdapterCallBack2);
		mListView2.setAdapter(listViewAdapter2);
	}

	private void InitListViewAdapter3() {
		sqlite.execSQL(sql_creat3);
		listViewAdapter3 = new MyListViewAdapter(mContext, R.layout.list_sur_third, mBeanList3, Content.LIST_SURVEY_THIRD,
				listViewAdapterCallBack3);
		mListView3.setAdapter(listViewAdapter3);
	}

	private void LoadData_Send() {
		final String xmlString = "<Request><Function>gcheck</Function><Parms><Username>" + Content.USER_SYS_CODE
				+ "</Username></Parms></Request>";
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (http == null) {
					http = HttpUtil.getInstance();
				}
				PostResult result = http.sendXML(xmlString);
				if (result != null) {
					mHandler.obtainMessage(Content.SEND_XML_SUR_LOAD, result).sendToTarget();
				}
			}
		}).start();

	}

	private void LoadData_Rev(PostResult result) {
		if (result.isResult()) {
			DeleteTable(DB_TABLE, sql_creat1);
			DeleteTable(DETAIL_TABLE, sql_creat2);
			DeleteTable(THIRD_TABLE, sql_creat3);
			UpdateSQLiteData(result.getSurveyBeanList());
			UpDateListView1();
		} else {
			mToast.show(result.getMessage());
		}
		mListView1.onRefreshComplete(result.isResult(), Content.LIST_SURVEY);

	}

	private void NewThirdTask() {
		AdapterBean bean = new AdapterBean();
		String guid = UUID.randomUUID().toString().replace("-", "");
		bean.setTaskID(guid);
		bean.setBarCode("");
		bean.setColligate(StringUtils.qualified);
		bean.setAppearance(StringUtils.qualified);
		bean.setColor(StringUtils.qualified);
		bean.setWaterFell(StringUtils.qualified);
		bean.setMould(StringUtils.none);
		bean.setOilTrace(StringUtils.none);
		bean.setMothy(StringUtils.none);
		bean.setOdor(StringUtils.none);
		bean.setImpurity(StringUtils.none);
		bean.setPicture(" / / / ");
		bean.setMark(Content.DB_DATA_TEMP);
		mBeanList3.add(bean);
		listViewAdapter3.notifyDataSetChanged();
	}

	private void SaveThirdData() {
		ContentValues values = new ContentValues();

		for (int i = 0; i < mThirdDeleteList.size(); i++) {
			String ThirdTaskID = mThirdDeleteList.get(i).getTaskID();
			if (mThirdDeleteList.get(i).getMark().equals(Content.DB_DATA_LOCAL)) {
				sqlite.delete(THIRD_TABLE, "_thirdid = ?", new String[] { ThirdTaskID });
			} else if (mThirdDeleteList.get(i).getMark().equals(Content.DB_DATA_SERVER)) {
				values.put("_mark", Content.DB_NEED_DELETE);
				sqlite.update(THIRD_TABLE, values, "_thirdid = ?", new String[] { ThirdTaskID });
				values.clear();
			}
		}
		mThirdDeleteList.clear();

		String DetailColligate = "0";
		String DetailAppearance = "0";
		String DetailColor = "0";
		String DetailWaterFell = "0";
		String DetailMould = "0";
		String DetailOilTrace = "0";
		String DetailMothy = "0";
		String DetailOdor = "0";
		String DetailImpurity = "0";
		for (int i = 0; i < mBeanList3.size(); i++) {
			values.clear();
			String ThirdId = mBeanList3.get(i).getTaskID();
			String BarCode = mBeanList3.get(i).getBarCode();
			String Colligate = "0";
			String Appearance = "0";
			String Color = "0";
			String WaterFell = "0";
			String Mould = "0";
			String OilTrace = "0";
			String Mothy = "0";
			String Odor = "0";
			String Impurity = "0";
			if (mBeanList3.get(i).getColligate().equals(StringUtils.disqualified)) {
				Colligate = "1";
				DetailColligate = "1";
			}
			if (mBeanList3.get(i).getAppearance().equals(StringUtils.disqualified)) {
				Appearance = "1";
				DetailAppearance = "1";
			}
			if (mBeanList3.get(i).getColor().equals(StringUtils.disqualified)) {
				Color = "1";
				DetailColor = "1";
			}
			if (mBeanList3.get(i).getWaterFell().equals(StringUtils.disqualified)) {
				WaterFell = "1";
				DetailWaterFell = "1";
			}
			if (mBeanList3.get(i).getMould().equals(StringUtils.slight)) {
				Mould = "1";
				if(!DetailMould.equals("2")){
					DetailMould = "1";
				}
			} else if(mBeanList3.get(i).getMould().equals(StringUtils.severe)){
				Mould = "2";
				DetailMould = "2";
			}
			if (mBeanList3.get(i).getOilTrace().equals(StringUtils.slight)) {
				OilTrace = "1";
				if(!DetailOilTrace.equals("2")){
					DetailOilTrace = "1";
				}
			} else if(mBeanList3.get(i).getOilTrace().equals(StringUtils.severe)){
				OilTrace = "2";
				DetailOilTrace = "2";
			}
			if (mBeanList3.get(i).getMothy().equals(StringUtils.slight)) {
				Mothy = "1";
				if(!DetailMothy.equals("2")){
					DetailMothy = "1";
				}
			} else if(mBeanList3.get(i).getMothy().equals(StringUtils.severe)){
				Mothy = "2";
				DetailMothy = "2";
			}
			if (mBeanList3.get(i).getOdor().equals(StringUtils.exist)) {
				Odor = "1";
				DetailOdor = "1";
			}
			if (mBeanList3.get(i).getImpurity().equals(StringUtils.exist)) {
				Impurity = "1";
				DetailImpurity = "1";
			}

			String Picture = mBeanList3.get(i).getPicture();

			values.put("_barcode", BarCode);
			values.put("_colligate", Colligate);
			values.put("_appearance", Appearance);
			values.put("_color", Color);
			values.put("_waterfell", WaterFell);
			values.put("_mould", Mould);
			values.put("_oiltrace", OilTrace);
			values.put("_mothy", Mothy);
			values.put("_odor", Odor);
			values.put("_impurity", Impurity);
			values.put("_picture", Picture);

			if (mBeanList3.get(i).getMark().equals(Content.DB_DATA_TEMP)) {
				values.put("_taskid", mCurTaskID);
				values.put("_detailid", mCurDetailTaskID);
				values.put("_thirdid", ThirdId);
				values.put("_mark", Content.DB_DATA_LOCAL);
				sqlite.insert(THIRD_TABLE, null, values);
			} else {
				sqlite.update(THIRD_TABLE, values, "_thirdid = ?", new String[] { ThirdId });
			}
		}
		values.clear();
		values.put("_colligate", DetailColligate);
		values.put("_appearance", DetailAppearance);
		values.put("_color", DetailColor);
		values.put("_waterfell", DetailWaterFell);
		values.put("_mould", DetailMould);
		values.put("_oiltrace", DetailOilTrace);
		values.put("_mothy", DetailMothy);
		values.put("_odor", DetailOdor);
		values.put("_impurity", DetailImpurity);
		sqlite.update(DETAIL_TABLE, values, "_detailid = ?", new String[] { mCurDetailTaskID });
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
		if (listViewAdapter2 == null) {
			InitListViewAdapter2();
		}
		if (pos > 0) {
			AdapterBean bean = mBeanList1.get(pos - 1);
			mCurTaskID = bean.getTaskID();
			mDate.setText(bean.getDate());
			mOrderID.setText(bean.getOrderID());
			mBuilding.setText(bean.getBuilding());
			mFloor.setText(bean.getFloor());
			NCState = bean.getNCState();
			mStateNC.setText(bean.getNCState());
		}
		UpDateListView2();
		mContext.setMyBackModel(Content.BACK_MODEL_SUR_DETAIL);
		if (layout1.getVisibility() == View.VISIBLE) {
			layout1.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_layout_left_hide));
			layout2.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_layout_left_show));
			layout1.setVisibility(View.GONE);
			layout2.setVisibility(View.VISIBLE);
		} else if (layout3.getVisibility() == View.VISIBLE) {
			mThirdDeleteList.clear();
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
		mContext.setMyBackModel(Content.BACK_MODEL_SUR_THIRD);
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
				bean.setBuilding(cursor.getString(3));
				bean.setFloor(cursor.getString(4));
				bean.setDetailNum(cursor.getString(5));
				bean.setPerson(cursor.getString(6));
				bean.setNCState(cursor.getString(7));
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
				bean.setTrayNum(cursor.getString(5));
				bean.setCheckNum(cursor.getString(6));
				bean.setColligate(cursor.getString(7));
				bean.setAppearance(cursor.getString(8));
				bean.setColor(cursor.getString(9));
				bean.setWaterFell(cursor.getString(10));
				bean.setMould(cursor.getString(11));
				bean.setOilTrace(cursor.getString(12));
				bean.setMothy(cursor.getString(13));
				bean.setOdor(cursor.getString(14));
				bean.setImpurity(cursor.getString(15));
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
				if (!cursor.getString(15).equals(Content.DB_NEED_DELETE)) {
					AdapterBean bean = new AdapterBean();
					bean.setPPTaskID(cursor.getString(0));
					bean.setPTaskID(cursor.getString(1));
					bean.setTaskID(cursor.getString(2));
					bean.setBarCode(cursor.getString(3));
					bean.setColligate(cursor.getString(4));
					bean.setAppearance(cursor.getString(5));
					bean.setColor(cursor.getString(6));
					bean.setWaterFell(cursor.getString(7));
					bean.setMould(cursor.getString(8));
					bean.setOilTrace(cursor.getString(9));
					bean.setMothy(cursor.getString(10));
					bean.setOdor(cursor.getString(11));
					bean.setImpurity(cursor.getString(12));
					bean.setPicture(cursor.getString(13));
					bean.setPictureVersion(cursor.getString(14));
					bean.setMark(cursor.getString(15));
					mBeanList3.add(bean);
				}
			} while (cursor.moveToNext());
			cursor.close();
		}
		cursor = null;
		listViewAdapter3.setNCState(NCState.equals(StringUtils.IsUpLoad.YES));
		listViewAdapter3.notifyDataSetChanged();
	}

	private void UpdateSQLiteData(List<SurveyBean> mTaskBeanList) {
		sqlite.beginTransaction();
		ContentValues cv = new ContentValues();
		for (int i = 0; i < mTaskBeanList.size(); i++) {
			cv.clear();
			cv.put("_taskid", mTaskBeanList.get(i).getTaskId());
			cv.put("_date", mTaskBeanList.get(i).getDate());
			cv.put("_orderid", mTaskBeanList.get(i).getOrderId());
			cv.put("_building", mTaskBeanList.get(i).getBuilding());
			cv.put("_floor", mTaskBeanList.get(i).getFloor());
			cv.put("_detailnum", mTaskBeanList.get(i).getDetailNum());
			cv.put("_person", mTaskBeanList.get(i).getPerson());
			cv.put("_statenc", mTaskBeanList.get(i).getStateNC());
			sqlite.insert(DB_TABLE, null, cv);
			List<SurveyDetailBean> details = mTaskBeanList.get(i).getDetailBeanList();
			if (details != null && details.size() > 0) {
				for (int j = 0; j < details.size(); j++) {
					cv.clear();
					cv.put("_taskid", details.get(j).getTaskId());
					cv.put("_detailid", details.get(j).getDetailId());
					cv.put("_trayname", details.get(j).getTrayName());
					cv.put("_goodsid", details.get(j).getGoodsID());
					cv.put("_goodsname", details.get(j).getGoodsName());
					cv.put("_traynum", details.get(j).getTrayNum());
					cv.put("_checknum", details.get(j).getCheckNum());
					cv.put("_colligate", details.get(j).getColligate());
					cv.put("_appearance", details.get(j).getAppearance());
					cv.put("_color", details.get(j).getColor());
					cv.put("_waterfell", details.get(j).getWaterFell());
					cv.put("_mould", details.get(j).getMould());
					cv.put("_oiltrace", details.get(j).getOilTrace());
					cv.put("_mothy", details.get(j).getMothy());
					cv.put("_odor", details.get(j).getOdor());
					cv.put("_impurity", details.get(j).getImpurity());
					sqlite.insert(DETAIL_TABLE, null, cv);
					List<SurveyThirdBean> thirds = details.get(j).getThirdBeanList();
					if (thirds != null && thirds.size() > 0) {
						for (int k = 0; k < thirds.size(); k++) {
							cv.clear();
							cv.put("_taskid", thirds.get(k).getTaskId());
							cv.put("_detailid", thirds.get(k).getDetailId());
							cv.put("_thirdid", thirds.get(k).getThirdId());
							cv.put("_barcode", thirds.get(k).getBarCode());
							cv.put("_colligate", thirds.get(k).getColligate());
							cv.put("_appearance", thirds.get(k).getAppearance());
							cv.put("_color", thirds.get(k).getColor());
							cv.put("_waterfell", thirds.get(k).getWaterFell());
							cv.put("_mould", thirds.get(k).getMould());
							cv.put("_oiltrace", thirds.get(k).getOilTrace());
							cv.put("_mothy", thirds.get(k).getMothy());
							cv.put("_odor", thirds.get(k).getOdor());
							cv.put("_impurity", thirds.get(k).getImpurity());
							cv.put("_picture", thirds.get(k).getPicture());
							cv.put("_picturever", thirds.get(k).getPictureVersion());
							cv.put("_mark", thirds.get(k).getMark());
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
		WeakReference<SurveyFragment> mFragment;

		MyHandler(SurveyFragment surveyFragment) {
			mFragment = new WeakReference<SurveyFragment>(surveyFragment);
		}

		@Override
		public void handleMessage(Message msg) {
			SurveyFragment theFragment = mFragment.get();
			if (theFragment == null) {
				return;
			}
			switch (msg.what) {
			case Content.SEND_XML_SUR_LOAD:
				theFragment.LoadData_Rev((PostResult) msg.obj);
				break;

			case Content.SEND_XML_SUR_COMMIT:
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
			ShowThirdTable(pos);
		}

		@Override
		public void onItemDelete(View v, int mPos) {

		}

		@Override
		public void onItemUpdate(View v, int mPos, String newValue1) {

		}

	};

	private ListViewAdapterCallBack listViewAdapterCallBack3 = new ListViewAdapterCallBack() {

		@Override
		public void onItemClick(View v, int pos, AdapterBean bean) {
			mThirdClickPos = pos;
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
		}

		@Override
		public void onItemDelete(View v, int mPos) {
			AdapterBean removeBean = mBeanList3.get(mPos);
			mThirdDeleteList.add(removeBean);
			mBeanList3.remove(mPos);
			listViewAdapter3.notifyDataSetChanged();
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

	public void ShowInputSoft() {
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
