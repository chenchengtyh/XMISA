package com.nti56.xmisa.dialog;

import java.util.ArrayList;
import java.util.UUID;

import com.nti56.xmisa.R;
import com.nti56.xmisa.adapter.MyListView;
import com.nti56.xmisa.adapter.MyListViewAdapter;
import com.nti56.xmisa.adapter.MyListViewAdapter.ListViewAdapterCallBack;
import com.nti56.xmisa.adapter.MySQLiteHelper;
import com.nti56.xmisa.bean.AdapterBean;
import com.nti56.xmisa.util.Content;
import com.nti56.xmisa.util.MyLog;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class RoadLostDialog extends Dialog implements OnClickListener {

	private Context mContext;
	private SQLiteDatabase sqlite;

	private MyListView mListView;
	private LinearLayout mLayoutInfo, mLayoutTypeSelect;
	private Button mBtnBack, mBtnSave, mBtnTypeNone, mBtnTypeWet, mBtnTypeLose, mBtnAdd;
	private TextView mTVGoodsID, mTVGoodsName, mTVSpec, mTVPlanNum, mTVFactNum, mTVPlanWeight, mTVFactWeight, mBtnHideTypeSelect;

	private MyListViewAdapter adapter;
	private ArrayList<AdapterBean> mBeanList = new ArrayList<AdapterBean>();
	private ArrayList<AdapterBean> mDeletList = new ArrayList<AdapterBean>();

	private String TAG = "RoadLostDialog";
	private String LOSS_TABLE = "receive_loss";
	private String DETAIL_TABLE = "receive_detail";
	private String mTaskID, mDetailID, mStateNC, mSpec;
	private String sql_creat = "CREATE  TABLE if not exists receive_loss(_taskid VARCHAR, _detailid VARCHAR, _lossid VARCHAR PRIMARY KEY,"
			+ " _lossweight FLOAT, _spec FLOAT, _rate FLOAT, _type VARCHAR, _barcode VARCHAR, _mark VARCHAR)";

	private String[] mCondition = null;

	private double totalLoss;
	private int factnum, mTypeChangePos = -1;

	private mLostDialogListener mListener;
	private RelativeLayout mLayoutLossTable;

	public interface mLostDialogListener {
		public void DialogDismiss(double totalLoss);
	}

	public RoadLostDialog(Context context, int theme, mLostDialogListener listener) {
		super(context, theme);
		this.mContext = context;
		this.mListener = listener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.e("NTI", "DIALOG............onCreate()");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_road_lost);

		mLayoutLossTable = (RelativeLayout) findViewById(R.id.layout_rec_loss_table);

		mLayoutInfo = (LinearLayout) findViewById(R.id.layout_rec_loss_info);
		mLayoutTypeSelect = (LinearLayout) findViewById(R.id.layout_loss_typeselect);
		mBtnHideTypeSelect = (TextView) findViewById(R.id.tv_hide_typeselect);

		mTVGoodsID = (TextView) findViewById(R.id.tv_rec_lost_goodsid);
		mTVGoodsName = (TextView) findViewById(R.id.tv_rec_lost_goodsname);
		mTVSpec = (TextView) findViewById(R.id.tv_rec_lost_spec);
		mTVPlanNum = (TextView) findViewById(R.id.tv_rec_lost_plannum);
		mTVFactNum = (TextView) findViewById(R.id.tv_rec_lost_factnum);
		mTVPlanWeight = (TextView) findViewById(R.id.tv_rec_lost_planweight);
		mTVFactWeight = (TextView) findViewById(R.id.tv_rec_lost_factweight);

		mBtnTypeNone = (Button) findViewById(R.id.btn_lost_type_none);
		mBtnTypeWet = (Button) findViewById(R.id.btn_lost_type_wet);
		mBtnTypeLose = (Button) findViewById(R.id.btn_lost_type_lose);
		mBtnBack = (Button) findViewById(R.id.btn_rec_loss_back);
		mBtnSave = (Button) findViewById(R.id.btn_rec_loss_save);
		mBtnAdd = (Button) findViewById(R.id.btn_rec_loss_addlist);

		mBtnAdd.setOnClickListener(this);
		mBtnBack.setOnClickListener(this);
		mBtnSave.setOnClickListener(this);
		mBtnTypeWet.setOnClickListener(this);
		mBtnTypeNone.setOnClickListener(this);
		mBtnTypeLose.setOnClickListener(this);
		mBtnHideTypeSelect.setOnClickListener(this);

		mListView = (MyListView) findViewById(R.id.list_rec_loss);
		sqlite = MySQLiteHelper.getInstance();
		InitAdapter();
		mListView.setAdapter(adapter);
	}

	@Override
	protected void onStart() {
		mLayoutTypeSelect.setVisibility(View.GONE);
		mBtnHideTypeSelect.setVisibility(View.GONE);

		if (mCondition != null && mCondition.length > 9) {
			mTaskID = mCondition[0];
			mDetailID = mCondition[1];
			mStateNC = mCondition[2];
			mSpec = mCondition[5];
			mTVGoodsID.setText(mCondition[3]);
			mTVGoodsName.setText(mCondition[4]);
			mTVSpec.setText(mSpec);
			mTVPlanNum.setText(mCondition[6]);
			mTVFactNum.setText(mCondition[7]);
			mTVPlanWeight.setText(mCondition[8]);
			mTVFactWeight.setText(mCondition[9]);

			factnum = Content.parseInt(mCondition[7]);
			adapter.setNCState(mStateNC.equals(Content.HTTP_UPLOAD_TRUE));
			UpDateListView();
		}
		super.onStart();
	}

	@Override
	protected void onStop() {
		mListener.DialogDismiss(totalLoss);
		super.onStop();
	}

	private void InitAdapter() {
		sqlite.execSQL(sql_creat);
		Log.e("LXM", "mDetailID = " + ((mDetailID == null) ? "null" : mDetailID));
		// Cursor cursor = sqlite.query(LOSS_TABLE, null, "_detailid=?", new
		// String[] { mDetailID }, null, null, null);
		// if (cursor != null && cursor.moveToFirst()) {
		// do {
		// AdapterBean bean = new AdapterBean();
		// bean.setTaskID(cursor.getString(2));
		// bean.setLossWeight(cursor.getString(3));
		// bean.setSpec(cursor.getString(4));
		// bean.setRate(cursor.getString(5));
		// bean.setType(cursor.getString(6));
		// bean.setBarCode(cursor.getString(7));
		// bean.setMark(cursor.getString(8));
		// mBeanList.add(bean);
		// } while (cursor.moveToNext());
		// cursor.close();
		// cursor = null;
		// }
		adapter = new MyListViewAdapter(mContext, R.layout.list_road_lost, mBeanList, Content.LIST_RECEIVE_LOSS, mAdapterCallBack);
	}

	private void UpDateListView() {
		Cursor cursor = sqlite.query(LOSS_TABLE, null, "_detailid=?", new String[] { mDetailID }, null, null, null);
		mDeletList.clear();
		totalLoss = 0;
		mBeanList.clear();
		if (cursor != null && cursor.moveToFirst()) {
			do {
				if (!cursor.getString(8).equals(Content.DB_NEED_DELETE)) {
					AdapterBean bean = new AdapterBean();
					bean.setTaskID(cursor.getString(2));
					totalLoss += Content.parseDouble(cursor.getString(3));
					bean.setLossWeight(cursor.getString(3));
					bean.setSpec(cursor.getString(4));
					bean.setRate(cursor.getString(5));
					bean.setType(cursor.getString(6));
					bean.setBarCode(cursor.getString(7));
					bean.setMark(cursor.getString(8));
					mBeanList.add(bean);
				}
			} while (cursor.moveToNext());
			cursor.close();
			cursor = null;
		}
		adapter.notifyDataSetChanged();
	}

	private void UpdateFactWeight() {
		double totalLoss = 0;
		for (int i = 0; i < mBeanList.size(); i++) {
			totalLoss += Content.parseDouble(mBeanList.get(i).getLossWeight());
		}
		String FactWeight = "" + (factnum * Content.parseDouble(mSpec) - totalLoss);
		if (FactWeight.endsWith(".0")) {
			FactWeight = FactWeight.replace(".0", "");
		}
		mTVFactWeight.setText(FactWeight);
	}

	public void setTitle(String[] condition) {
		Log.e("NTI", "DIALOG............setTitle()");
		mCondition = condition;
		if (mCondition != null && mCondition.length > 9) {
			mTaskID = mCondition[0];
			mDetailID = mCondition[1];
			mStateNC = mCondition[2];
			mSpec = mCondition[5];
			factnum = Content.parseInt(mCondition[7]);
		}
	}

	public void showInputSoft() {
		final int Height = Content.InputSoftOpen ? 262 : LayoutParams.MATCH_PARENT;
		mLayoutLossTable.post(new Runnable() {
			@Override
			public void run() {
				LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, Height);
				mLayoutLossTable.setLayoutParams(layoutParams);
				UpdateFactWeight();
				adapter.setInputOpen(Height != LayoutParams.MATCH_PARENT);
			}
		});
		mLayoutInfo.setVisibility(Content.InputSoftOpen ? View.GONE : View.VISIBLE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_rec_loss_back:
			cancel();
			break;
		case R.id.btn_rec_loss_save: {
			ContentValues values = new ContentValues();
			totalLoss = 0;
			for (int i = 0; i < mBeanList.size(); i++) {// 第一步，更新&插入LOSS表中数据
				String LossID = mBeanList.get(i).getTaskID();
				String Mark = mBeanList.get(i).getMark();
				totalLoss += Content.parseDouble(mBeanList.get(i).getLossWeight());
				values.put("_lossweight", mBeanList.get(i).getLossWeight());
				values.put("_rate", mBeanList.get(i).getRate());
				values.put("_type", mBeanList.get(i).getType());
				values.put("_barcode", mBeanList.get(i).getBarCode());
				if (Mark.equals(Content.DB_DATA_TEMP)) {
					values.put("_taskid", mTaskID);
					values.put("_detailid", mDetailID);
					values.put("_lossid", LossID);
					values.put("_spec", mBeanList.get(i).getSpec());
					values.put("_mark", Content.DB_DATA_LOCAL);
					sqlite.insert(LOSS_TABLE, null, values);
				} else {
					sqlite.update(LOSS_TABLE, values, "_lossid = ?", new String[] { LossID });
				}
				values.clear();
			}
			for (int i = 0; i < mDeletList.size(); i++) {// 第二步，删除/标记LOSS表中需要移除的数据
				String LossID = mDeletList.get(i).getTaskID();
				if (mDeletList.get(i).getMark().equals(Content.DB_DATA_LOCAL)) {
					sqlite.delete(LOSS_TABLE, "_lossid = ?", new String[] { LossID });
				} else {// 因为如果为-1，数据库中无对应数据，update会失败。所以不用考虑temp的情况
					values.put("_mark", Content.DB_NEED_DELETE);
					sqlite.update(LOSS_TABLE, values, "_lossid = ?", new String[] { LossID });
					values.clear();
				}
			}
			mDeletList.clear();
			// 第三步，将计算的途损总重量更新至收货明细表
			values.put("_lossweight", totalLoss);
			sqlite.update(DETAIL_TABLE, values, "_detailid = ?", new String[] { mDetailID });
			Toast.makeText(mContext, "成功保存至本地！", Toast.LENGTH_SHORT).show();
		}
			break;
		case R.id.btn_rec_loss_addlist: {
			if (!mStateNC.equals(Content.HTTP_UPLOAD_TRUE)) {
				String guid = UUID.randomUUID().toString().replace("-", "");
				AdapterBean bean = new AdapterBean();
				bean.setTaskID(guid);
				bean.setLossWeight("0");
				bean.setSpec(mSpec);
				bean.setRate(mSpec);
				bean.setType("无");
				bean.setBarCode("0");
				bean.setMark(Content.DB_DATA_TEMP);
				mBeanList.add(bean);
				adapter.notifyDataSetChanged();
			}
		}
			break;

		case R.id.tv_hide_typeselect:
			mLayoutTypeSelect.setVisibility(View.GONE);
			mBtnHideTypeSelect.setVisibility(View.GONE);
			break;

		case R.id.btn_lost_type_none: {
			AdapterBean bean = mBeanList.get(mTypeChangePos);
			bean.setType("无");
			adapter.notifyDataSetChanged();
			mLayoutTypeSelect.setVisibility(View.GONE);
			mBtnHideTypeSelect.setVisibility(View.GONE);
			mTypeChangePos = -1;
		}
			break;

		case R.id.btn_lost_type_wet: {
			AdapterBean bean = mBeanList.get(mTypeChangePos);
			bean.setType("湿损");
			adapter.notifyDataSetChanged();
			mLayoutTypeSelect.setVisibility(View.GONE);
			mBtnHideTypeSelect.setVisibility(View.GONE);
			mTypeChangePos = -1;
		}
			break;

		case R.id.btn_lost_type_lose: {
			AdapterBean bean = mBeanList.get(mTypeChangePos);
			bean.setType("丢失");
			adapter.notifyDataSetChanged();
			mLayoutTypeSelect.setVisibility(View.GONE);
			mBtnHideTypeSelect.setVisibility(View.GONE);
			mTypeChangePos = -1;
		}
			break;

		default:
			break;
		}
	}

	private ListViewAdapterCallBack mAdapterCallBack = new ListViewAdapterCallBack() {

		@Override
		public void onItemUpdate(View v, int mPos, String newValue1) {

		}

		@Override
		public void onItemDelete(View v, int mPos) {
			MyLog.e(TAG, "onItemDelete","---------onItemDelete().mPos = " + mPos);
			AdapterBean removeBean = mBeanList.get(mPos);
			mDeletList.add(removeBean);
			mBeanList.remove(mPos);
			UpdateFactWeight();
			adapter.notifyDataSetChanged();
		}

		@Override
		public void onItemClick(View v, int mPos, AdapterBean bean) {
			if (!Content.InputSoftOpen) {
				int[] location = new int[2];
				v.getLocationInWindow(location);
				int x = location[0];
				int y = location[1];
				mLayoutTypeSelect.setX(x);
				mLayoutTypeSelect.setY(y);
				mTypeChangePos = mPos;
				mLayoutTypeSelect.setVisibility(View.VISIBLE);
				mBtnHideTypeSelect.setVisibility(View.VISIBLE);
			}
		}

	};

}
