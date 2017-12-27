package com.nti56.xmisa.adapter;

import java.util.ArrayList;

import com.nti56.xmisa.R;
import com.nti56.xmisa.bean.AdapterBean;
import com.nti56.xmisa.util.Content;
import com.nti56.xmisa.util.MyLog;
import com.nti56.xmisa.util.StringUtils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

public class MyListViewAdapter extends BaseAdapter {

	private View mFocusView;
	private Context mContext;
	private ArrayList<AdapterBean> mBeanList;
	private ListViewAdapterCallBack mCallback;
	private int mMode, mLayoutId, mFocusPos = -1, mFocusNum;//TODO
	private boolean mLock = false;
	private boolean mNCState = false;
	private boolean mFocusChange = false;
	private String TAG = "MyListViewAdapter";

	public MyListViewAdapter(Context mContext, int layoutId, ArrayList<AdapterBean> beanlist, int mode,
			ListViewAdapterCallBack callback) {
		this.mContext = mContext;
		this.mLayoutId = layoutId;
		this.mBeanList = beanlist;
		this.mMode = mode;
		this.mCallback = callback;
	}

	public interface ListViewAdapterCallBack {
		public void onItemClick(View v, int mPos, AdapterBean bean);

		// public void onItemLongClick(View v, String taskID);

		public void onItemDelete(View v, int mPos);

		public void onItemUpdate(View v, int mPos, String newValue1);
		// public void onCheckBoxChanged(String taskID, boolean isChecked);
		//
		// public void onEditTextChanged(String taskID, int pos, String
		// oldValue, String newValue);
		//

	}

	@Override
	public int getCount() {
		return mBeanList.size();
	}

	@Override
	public AdapterBean getItem(int position) {
		return mBeanList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void setInputOpen(boolean InputOpen) {
		mFocusChange = InputOpen;
		if (!InputOpen && mFocusView != null) {
			mFocusView.clearFocus();
		}
	}

	public void setNCState(boolean NCState) {
		mNCState = NCState;
	}

	private String countFactWeight(String Value, int mPos) {
		int factnum = Content.parseInt(Value);
		double spec = Content.parseDouble(mBeanList.get(mPos).getSpec());
		double lossweight = Content.parseDouble(mBeanList.get(mPos).getLossWeight());
		Value = "" + (factnum * spec - lossweight);
		if (Value.endsWith(".0")) {
			Value = Value.replace(".0", "");
		}
		return Value;
	}

	private void setExceptionBackGround(TextView mTextView, String value) {
		mTextView.setText(value);
		if (value.equals("异常") || value.equals("不合格") || value.equals("严重") || value.equals("轻微") || value.equals("有")) {
			mTextView.setBackgroundResource(R.color.red_light);
		} else {
			mTextView.setBackgroundResource(R.color.transparent);
		}
	}

	private void setLockBackground(View convertView, boolean locked) {
		if (mMode != Content.LIST_LOGIN) {
			Drawable drawable;
			if (locked) {
				drawable = mContext.getResources().getDrawable(R.drawable.list_item_locked);
			} else {
				drawable = mContext.getResources().getDrawable(R.drawable.list_item_select);
			}
			convertView.setBackground(drawable);
		}
		convertView.setTag(R.id.tag_lock, locked);
	}

	private void judgeFocus(EditText mEditText, int position, int serialNum) {
		mEditText.clearFocus();
		mEditText.setTextIsSelectable(false);
		mEditText.setEnabled(!mLock && !mNCState);
		if (mFocusChange && position == mFocusPos && mFocusNum == serialNum) {
			mFocusChange = false;
			mEditText.setSelection(mEditText.length());
			mEditText.requestFocus();
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {// 加一个标记，当标记与现在不同，就改
		ViewHolder mHolder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(mLayoutId, null);
		}
		if (parent instanceof MyListView) {
			if (((MyListView) parent).isOnMeasure()) {// 此种情况下，listview正在根据高度计算应显示多少个view，因予以过滤
				return convertView;
			}
		}
		MyLog.d(TAG, "getView", "position = " + position);
		if (mBeanList.size() == 0) {
			return convertView;
		}
		mHolder = (ViewHolder) convertView.getTag(R.id.tag_holder);
		if (mHolder == null) {
			mHolder = setViewHolder(convertView);
			setLockBackground(convertView, false);
			convertView.setTag(R.id.tag_holder, mHolder);
		}

		convertView.setTag(position);
		mHolder.mPosition.setText((position + 1) + "");
		fillData(position, convertView, mHolder);
		boolean Locked = (Boolean) convertView.getTag(R.id.tag_lock);
		if (Locked && !mLock && !mNCState) {
			MyLog.d(TAG, "getView", "解锁" + position);
			setLockBackground(convertView, false);
		} else if (!Locked && (mLock || mNCState)) {
			MyLog.d(TAG, "getView", "锁定" + position);
			setLockBackground(convertView, true);
		}
		return convertView;
	}

	private ViewHolder setViewHolder(View convertView) {
		ViewHolder holder = new ViewHolder();
		holder.mPosition = (TextView) convertView.findViewById(R.id.public_id);
		switch (mMode) {
		case Content.LIST_LOGIN:
			holder.mUser = (TextView) convertView.findViewById(R.id.tv_login_user);
			holder.mDelete = (TextView) convertView.findViewById(R.id.tv_login_delete);
			holder.mDelete.setOnClickListener(mItemClickListener);
			break;

		case Content.LIST_RECEIVE:
			holder.mDate = (TextView) convertView.findViewById(R.id.tv_rec_date);
			holder.mOrderID = (TextView) convertView.findViewById(R.id.tv_rec_orderid);
			holder.mType = (TextView) convertView.findViewById(R.id.tv_rec_type);
			holder.mPcNum = (TextView) convertView.findViewById(R.id.tv_rec_pcnum);
			holder.mBuilding = (TextView) convertView.findViewById(R.id.tv_rec_building);
			holder.mPlanNum = (TextView) convertView.findViewById(R.id.tv_rec_plannum);
			holder.mNCState = (TextView) convertView.findViewById(R.id.tv_rec_statenc);
			holder.mCarNum = (TextView) convertView.findViewById(R.id.tv_rec_carnum);
			break;

		case Content.LIST_RECEIVE_DETAIL:
			holder.mGoodsID = (TextView) convertView.findViewById(R.id.tv_rec_detail_goodsId);
			holder.mGoodsName = (TextView) convertView.findViewById(R.id.tv_rec_detail_goodsName);
			holder.mSpec = (TextView) convertView.findViewById(R.id.tv_rec_detail_spec);
			holder.mPlanNum = (TextView) convertView.findViewById(R.id.tv_rec_detail_plannum);
			holder.mFactNum = (EditText) convertView.findViewById(R.id.et_rec_detail_factnum);
			holder.mPlanWeight = (TextView) convertView.findViewById(R.id.tv_rec_detail_planweight);
			holder.mFactWeight = (TextView) convertView.findViewById(R.id.tv_rec_detail_factweight);
			holder.mLock = (CheckBox) convertView.findViewById(R.id.public_lock);

			convertView.setOnClickListener(mItemClickListener);
			holder.mFactNum.setOnLongClickListener(mLongClickListener);
			holder.mFactNum.setOnFocusChangeListener(mFocusChangeListener);
			holder.mLock.setOnCheckedChangeListener(mCheckedChangeListener);
			break;

		case Content.LIST_RECEIVE_LOSS://TODO
			holder.mLossWeight = (EditText) convertView.findViewById(R.id.et_rec_loss_lossweight);
			holder.mSpec = (TextView) convertView.findViewById(R.id.tv_rec_loss_spec);
			holder.mRate = (TextView) convertView.findViewById(R.id.tv_rec_loss_rate);
			holder.mType = (TextView) convertView.findViewById(R.id.tv_rec_loss_type);
			holder.mBarCodeET = (EditText) convertView.findViewById(R.id.et_rec_loss_barcode);

			convertView.setOnLongClickListener(mLongClickListener);
			holder.mType.setOnClickListener(mItemClickListener);
			holder.mBarCodeET.setOnLongClickListener(mLongClickListener);
			holder.mBarCodeET.setOnFocusChangeListener(mFocusChangeListener);
			holder.mLossWeight.setOnLongClickListener(mLongClickListener);
			holder.mLossWeight.setOnFocusChangeListener(mFocusChangeListener);
			break;

		case Content.LIST_INPUT:
			holder.mDate = (TextView) convertView.findViewById(R.id.tv_ipt_date);
			holder.mOrderID = (TextView) convertView.findViewById(R.id.tv_ipt_orderid);
			holder.mType = (TextView) convertView.findViewById(R.id.tv_ipt_type);
			holder.mPlanNum = (TextView) convertView.findViewById(R.id.tv_ipt_plannum);
			holder.mPcNum = (TextView) convertView.findViewById(R.id.tv_ipt_pcnum);
			holder.mBuilding = (TextView) convertView.findViewById(R.id.tv_ipt_building);
			holder.mCarNum = (TextView) convertView.findViewById(R.id.tv_ipt_carnum);
			holder.mNCState = (TextView) convertView.findViewById(R.id.tv_ipt_statenc);
			break;

		case Content.LIST_INPUT_DETAIL:
			holder.mTrayName = (TextView) convertView.findViewById(R.id.tv_ipt_detail_trayname);
			holder.mGoodsID = (TextView) convertView.findViewById(R.id.tv_ipt_detail_goodsid);
			holder.mGoodsName = (TextView) convertView.findViewById(R.id.tv_ipt_detail_goodsname);
			holder.mSpec = (TextView) convertView.findViewById(R.id.tv_ipt_detail_spec);
			holder.mPlanNum = (TextView) convertView.findViewById(R.id.tv_ipt_detail_plannum);
			holder.mFactNum = (EditText) convertView.findViewById(R.id.et_ipt_detail_factnum);
			holder.mPlanWeight = (TextView) convertView.findViewById(R.id.tv_ipt_detail_planweight);
			holder.mFactWeight = (TextView) convertView.findViewById(R.id.tv_ipt_detail_factweight);

			// convertView.setOnClickListener(mItemClickListener);
			holder.mFactNum.setOnLongClickListener(mLongClickListener);
			holder.mFactNum.setOnFocusChangeListener(mFocusChangeListener);
			// holder.mLock.setOnCheckedChangeListener(mCheckedChangeListener);
			convertView.setOnClickListener(mItemClickListener);
			break;

		case Content.LIST_INPUT_THIRD:
			holder.mCarID = (TextView) convertView.findViewById(R.id.tv_ipt_third_carid);
			holder.mDoorID = (TextView) convertView.findViewById(R.id.tv_ipt_third_doorid);
			holder.mBarCode = (TextView) convertView.findViewById(R.id.tv_ipt_third_barcode);
			holder.mInTime = (TextView) convertView.findViewById(R.id.tv_ipt_third_intime);
			break;

		case Content.LIST_OUTPUT:
			holder.mDate = (TextView) convertView.findViewById(R.id.tv_opt_date);
			holder.mOrderID = (TextView) convertView.findViewById(R.id.tv_opt_orderid);
			holder.mType = (TextView) convertView.findViewById(R.id.tv_opt_type);
			holder.mGoalWare = (TextView) convertView.findViewById(R.id.tv_opt_goalware);
			holder.mPlanNum = (TextView) convertView.findViewById(R.id.tv_opt_plannum);
			holder.mBuilding = (TextView) convertView.findViewById(R.id.tv_opt_building);
			holder.mCarNum = (TextView) convertView.findViewById(R.id.tv_opt_carnum);
			holder.mNCState = (TextView) convertView.findViewById(R.id.tv_opt_statenc);
			break;

		case Content.LIST_OUTPUT_DETAIL:
			holder.mTrayName = (TextView) convertView.findViewById(R.id.tv_opt_detail_trayname);
			holder.mGoodsID = (TextView) convertView.findViewById(R.id.tv_opt_detail_goodsid);
			holder.mGoodsName = (TextView) convertView.findViewById(R.id.tv_opt_detail_goodsname);
			holder.mVbomName = (TextView) convertView.findViewById(R.id.tv_opt_detail_vbomname);
			holder.mSpec = (TextView) convertView.findViewById(R.id.tv_opt_detail_spec);
			holder.mPlanNum = (TextView) convertView.findViewById(R.id.tv_opt_detail_plannum);
			holder.mFactNum = (EditText) convertView.findViewById(R.id.et_opt_detail_factnum);
			holder.mPlanWeight = (TextView) convertView.findViewById(R.id.tv_opt_detail_planweight);
			holder.mFactWeight = (TextView) convertView.findViewById(R.id.tv_opt_detail_factweight);

			holder.mFactNum.setOnLongClickListener(mLongClickListener);
			holder.mFactNum.setOnFocusChangeListener(mFocusChangeListener);
			convertView.setOnClickListener(mItemClickListener);
			break;

		case Content.LIST_OUTPUT_THIRD:
			holder.mCarID = (TextView) convertView.findViewById(R.id.tv_opt_third_carid);
			holder.mDoorID = (TextView) convertView.findViewById(R.id.tv_opt_third_doorid);
			holder.mBarCode = (TextView) convertView.findViewById(R.id.tv_opt_third_barcode);
			holder.mOutTime = (TextView) convertView.findViewById(R.id.tv_opt_third_outtime);
			break;

		case Content.LIST_INVENTORY: {
			holder.mDate = (TextView) convertView.findViewById(R.id.tv_ivt_date);
			holder.mOrderID = (TextView) convertView.findViewById(R.id.tv_ivt_orderid);
			holder.mType = (TextView) convertView.findViewById(R.id.tv_ivt_type);
			holder.mBuilding = (TextView) convertView.findViewById(R.id.tv_ivt_building);
			holder.mTrayTotle = (TextView) convertView.findViewById(R.id.tv_ivt_traytotle);
			holder.mTrayDone = (TextView) convertView.findViewById(R.id.tv_ivt_traydone);
			holder.mIvtNum = (TextView) convertView.findViewById(R.id.tv_ivt_num);
			holder.mIvtDone = (TextView) convertView.findViewById(R.id.tv_ivt_done);
			holder.mIvtWait = (TextView) convertView.findViewById(R.id.tv_ivt_wait);
		}
			break;

		case Content.LIST_INVENTORY_DETAIL: {//TODO
			holder.mTrayIDET = (EditText) convertView.findViewById(R.id.et_ivt_detail_trayid);
			holder.mGoodsIDET = (EditText) convertView.findViewById(R.id.et_ivt_detail_goodsid);
			holder.mStockNum = (EditText) convertView.findViewById(R.id.et_ivt_detail_stocknum);
			holder.mStockWeight = (EditText) convertView.findViewById(R.id.et_ivt_detail_stockweight);
			holder.mIvtNumET = (EditText) convertView.findViewById(R.id.et_ivt_detail_ivtnum);
			holder.mIvtWeight = (TextView) convertView.findViewById(R.id.tv_ivt_detail_ivtweight);
			holder.mRateET = (EditText) convertView.findViewById(R.id.et_ivt_detail_rate);
			holder.mDate = (TextView) convertView.findViewById(R.id.tv_ivt_detail_ivttime);

			convertView.setOnLongClickListener(mLongClickListener);
			holder.mTrayIDET.setOnLongClickListener(mLongClickListener);
			holder.mTrayIDET.setOnFocusChangeListener(mFocusChangeListener);

			holder.mGoodsIDET.setOnLongClickListener(mLongClickListener);
			holder.mGoodsIDET.setOnFocusChangeListener(mFocusChangeListener);

			holder.mStockNum.setOnLongClickListener(mLongClickListener);
			holder.mStockNum.setOnFocusChangeListener(mFocusChangeListener);

			holder.mStockWeight.setOnLongClickListener(mLongClickListener);
			holder.mStockWeight.setOnFocusChangeListener(mFocusChangeListener);

			holder.mIvtNumET.setOnLongClickListener(mLongClickListener);
			holder.mIvtNumET.setOnFocusChangeListener(mFocusChangeListener);

			holder.mRateET.setOnLongClickListener(mLongClickListener);
			holder.mRateET.setOnFocusChangeListener(mFocusChangeListener);
		}
			break;

		case Content.LIST_ASSIGN:
			holder.mDate = (TextView) convertView.findViewById(R.id.tv_asg_date);
			holder.mOrderID = (TextView) convertView.findViewById(R.id.tv_asg_orderid);
			holder.mType = (TextView) convertView.findViewById(R.id.tv_asg_type);
			holder.mGoodsName = (TextView) convertView.findViewById(R.id.tv_asg_goodsname);
			holder.mBuilding = (TextView) convertView.findViewById(R.id.tv_asg_building);
			holder.mCarNum = (TextView) convertView.findViewById(R.id.tv_asg_carnum);
			holder.mCheckLine = (TextView) convertView.findViewById(R.id.tv_asg_checkline);
			holder.mAssignState = (TextView) convertView.findViewById(R.id.tv_asg_state);
			break;

		case Content.LIST_EXAMINE:
			holder.mDate = (TextView) convertView.findViewById(R.id.tv_exm_date);
			holder.mOrderID = (TextView) convertView.findViewById(R.id.tv_exm_orderid);
			holder.mType = (TextView) convertView.findViewById(R.id.tv_exm_type);
			holder.mGoodsName = (TextView) convertView.findViewById(R.id.tv_exm_goodsname);
			holder.mBuilding = (TextView) convertView.findViewById(R.id.tv_exm_building);
			holder.mCarNum = (TextView) convertView.findViewById(R.id.tv_exm_carnum);
			holder.mCheckLine = (TextView) convertView.findViewById(R.id.tv_exm_checkline);
			holder.mNCState = (TextView) convertView.findViewById(R.id.tv_exm_statenc);
			break;

		case Content.LIST_EXAMINE_DETAIL:
			holder.mBarCode = (TextView) convertView.findViewById(R.id.tv_exm_detail_barcode);
			holder.mRFIDCode = (TextView) convertView.findViewById(R.id.tv_exm_detail_rfidcode);
			holder.mWeight = (TextView) convertView.findViewById(R.id.tv_exm_detail_weight);
			holder.mWater = (TextView) convertView.findViewById(R.id.tv_exm_detail_water);
			holder.mAppearance = (TextView) convertView.findViewById(R.id.tv_exm_detail_appearance);
			holder.mMould = (TextView) convertView.findViewById(R.id.tv_exm_detail_mould);
			holder.mOdor = (TextView) convertView.findViewById(R.id.tv_exm_detail_odor);
			holder.mImpurity = (TextView) convertView.findViewById(R.id.tv_exm_detail_impurity);
			holder.mMothy = (TextView) convertView.findViewById(R.id.tv_exm_detail_mothy);
			holder.mWaterDev = (TextView) convertView.findViewById(R.id.tv_exm_detail_waterdev);
			holder.mDensityDev = (TextView) convertView.findViewById(R.id.tv_exm_detail_densitydev);
			holder.mColligate = (TextView) convertView.findViewById(R.id.tv_exm_detail_colligate);
			holder.mDate = (TextView) convertView.findViewById(R.id.tv_exm_detail_date);
			break;

		case Content.LIST_PATROL:
			holder.mDate = (TextView) convertView.findViewById(R.id.tv_ptl_date);
			holder.mMeridiem = (TextView) convertView.findViewById(R.id.tv_ptl_meridiem);
			holder.mOrderID = (TextView) convertView.findViewById(R.id.tv_ptl_orderid);
			holder.mBuilding = (TextView) convertView.findViewById(R.id.tv_ptl_building);
			holder.mPerson = (TextView) convertView.findViewById(R.id.tv_ptl_person);
			holder.mNCState = (TextView) convertView.findViewById(R.id.tv_ptl_statenc);
			break;

		case Content.LIST_PATROL_DETAIL:
			holder.mFloor = (TextView) convertView.findViewById(R.id.tv_ptl_detail_floor);
			holder.mPoint = (TextView) convertView.findViewById(R.id.tv_ptl_detail_point);
			holder.mResult = (TextView) convertView.findViewById(R.id.tv_ptl_detail_result);
			holder.mTrouble = (EditText) convertView.findViewById(R.id.et_ptl_detail_trouble);
			holder.mPicture = (TextView) convertView.findViewById(R.id.tv_ptl_detail_picture);
			holder.mSolution = (EditText) convertView.findViewById(R.id.et_ptl_detail_solution);
			holder.mRepairDate = (TextView) convertView.findViewById(R.id.tv_ptl_detail_repairdate);

			holder.mResult.setOnLongClickListener(mLongClickListener);
			holder.mRepairDate.setOnLongClickListener(mLongClickListener);
			holder.mTrouble.setOnLongClickListener(mLongClickListener);
			holder.mTrouble.setOnFocusChangeListener(mFocusChangeListener);
			holder.mSolution.setOnLongClickListener(mLongClickListener);
			holder.mSolution.setOnFocusChangeListener(mFocusChangeListener);
			holder.mPicture.setOnClickListener(mItemClickListener);
			break;

		case Content.LIST_PESTS:
			holder.mDate = (TextView) convertView.findViewById(R.id.tv_pts_date);
			holder.mOrderID = (TextView) convertView.findViewById(R.id.tv_pts_orderid);
			holder.mBuilding = (TextView) convertView.findViewById(R.id.tv_pts_building);
			holder.mNote = (TextView) convertView.findViewById(R.id.tv_pts_note);
			holder.mPerson = (TextView) convertView.findViewById(R.id.tv_pts_person);
			holder.mNCState = (TextView) convertView.findViewById(R.id.tv_pts_statenc);
			break;

		case Content.LIST_PESTS_DETAIL:
			holder.mFloorET = (EditText) convertView.findViewById(R.id.et_pts_detail_floor);
			holder.mCardNum = (EditText) convertView.findViewById(R.id.et_pts_detail_cardnum);
			holder.mSerricorneNum = (EditText) convertView.findViewById(R.id.et_pts_detail_serricorne);
			holder.mElutellaNum = (EditText) convertView.findViewById(R.id.et_pts_detail_elutella);
			holder.mPicture = (TextView) convertView.findViewById(R.id.tv_pts_detail_picture);
			holder.mSolution = (EditText) convertView.findViewById(R.id.et_pts_detail_solution);

			// convertView.setOnLongClickListener(mLongClickListener);
			holder.mFloorET.setOnLongClickListener(mLongClickListener);
			holder.mFloorET.setOnFocusChangeListener(mFocusChangeListener);
			holder.mCardNum.setOnLongClickListener(mLongClickListener);
			holder.mCardNum.setOnFocusChangeListener(mFocusChangeListener);
			holder.mSerricorneNum.setOnLongClickListener(mLongClickListener);
			holder.mSerricorneNum.setOnFocusChangeListener(mFocusChangeListener);
			holder.mElutellaNum.setOnLongClickListener(mLongClickListener);
			holder.mElutellaNum.setOnFocusChangeListener(mFocusChangeListener);
			holder.mSolution.setOnLongClickListener(mLongClickListener);
			holder.mSolution.setOnFocusChangeListener(mFocusChangeListener);
			holder.mPicture.setOnClickListener(mItemClickListener);
			break;

		case Content.LIST_FUMIGATE:
			holder.mDate = (TextView) convertView.findViewById(R.id.tv_fmg_date);
			holder.mOrderID = (TextView) convertView.findViewById(R.id.tv_fmg_orderid);
			holder.mBuilding = (TextView) convertView.findViewById(R.id.tv_fmg_building);
			holder.mMethod = (TextView) convertView.findViewById(R.id.tv_fmg_method);
			holder.mDosingDate = (TextView) convertView.findViewById(R.id.tv_fmg_dosingdate);
			holder.mPerson = (TextView) convertView.findViewById(R.id.tv_fmg_person);
			holder.mNCState = (TextView) convertView.findViewById(R.id.tv_fmg_statenc);
			break;

		case Content.LIST_FUMIGATE_DETAIL:
			holder.mFloor = (TextView) convertView.findViewById(R.id.tv_fmg_detail_floor);
			holder.mLocation = (TextView) convertView.findViewById(R.id.tv_fmg_detail_location);
			holder.mTrayName = (TextView) convertView.findViewById(R.id.tv_fmg_detail_trayname);
			holder.mConcentration6 = (EditText) convertView.findViewById(R.id.et_fmg_detail_concentration6);
			holder.mConcentration12 = (EditText) convertView.findViewById(R.id.et_fmg_detail_concentration12);
			holder.mConcentration24 = (EditText) convertView.findViewById(R.id.et_fmg_detail_concentration24);
			holder.mConcentration48 = (EditText) convertView.findViewById(R.id.et_fmg_detail_concentration48);
			holder.mConcentration72 = (EditText) convertView.findViewById(R.id.et_fmg_detail_concentration72);
			holder.mConcentration96 = (EditText) convertView.findViewById(R.id.et_fmg_detail_concentration96);
			holder.mConcentration144 = (EditText) convertView.findViewById(R.id.et_fmg_detail_concentration144);
			holder.mConcentration216 = (EditText) convertView.findViewById(R.id.et_fmg_detail_concentration216);
			holder.mPicture = (TextView) convertView.findViewById(R.id.tv_fmg_detail_picture);
			holder.mConcentration6.setOnLongClickListener(mLongClickListener);
			holder.mConcentration6.setOnFocusChangeListener(mFocusChangeListener);
			holder.mConcentration12.setOnLongClickListener(mLongClickListener);
			holder.mConcentration12.setOnFocusChangeListener(mFocusChangeListener);
			holder.mConcentration24.setOnLongClickListener(mLongClickListener);
			holder.mConcentration24.setOnFocusChangeListener(mFocusChangeListener);
			holder.mConcentration48.setOnLongClickListener(mLongClickListener);
			holder.mConcentration48.setOnFocusChangeListener(mFocusChangeListener);
			holder.mConcentration72.setOnLongClickListener(mLongClickListener);
			holder.mConcentration72.setOnFocusChangeListener(mFocusChangeListener);
			holder.mConcentration96.setOnLongClickListener(mLongClickListener);
			holder.mConcentration96.setOnFocusChangeListener(mFocusChangeListener);
			holder.mConcentration144.setOnLongClickListener(mLongClickListener);
			holder.mConcentration144.setOnFocusChangeListener(mFocusChangeListener);
			holder.mConcentration216.setOnLongClickListener(mLongClickListener);
			holder.mConcentration216.setOnFocusChangeListener(mFocusChangeListener);
			if(holder.mTrayName != null){				
				holder.mTrayName.setOnClickListener(mItemClickListener);
			}
			holder.mPicture.setOnClickListener(mItemClickListener);
			break;

		case Content.LIST_SURVEY:
			holder.mDate = (TextView) convertView.findViewById(R.id.tv_sur_date);
			holder.mOrderID = (TextView) convertView.findViewById(R.id.tv_sur_orderid);
			holder.mBuilding = (TextView) convertView.findViewById(R.id.tv_sur_building);
			holder.mFloor = (TextView) convertView.findViewById(R.id.tv_sur_floor);
			holder.mDetailNum = (TextView) convertView.findViewById(R.id.tv_sur_detailnum);
			holder.mPerson = (TextView) convertView.findViewById(R.id.tv_sur_person);
			holder.mNCState = (TextView) convertView.findViewById(R.id.tv_sur_statenc);
			break;

		case Content.LIST_SURVEY_DETAIL:
			holder.mTrayName = (TextView) convertView.findViewById(R.id.tv_sur_detail_trayname);
			holder.mGoodsID = (TextView) convertView.findViewById(R.id.tv_sur_detail_goodsid);
			holder.mGoodsName = (TextView) convertView.findViewById(R.id.tv_sur_detail_goodsname);
			holder.mTrayNum = (TextView) convertView.findViewById(R.id.tv_sur_detail_traynum);
			holder.mCheckNum = (TextView) convertView.findViewById(R.id.tv_sur_detail_checknum);
			holder.mColligate = (TextView) convertView.findViewById(R.id.tv_sur_detail_colligate);
			holder.mAppearance = (TextView) convertView.findViewById(R.id.tv_sur_detail_appearance);
			holder.mColor = (TextView) convertView.findViewById(R.id.tv_sur_detail_color);
			holder.mWaterFell = (TextView) convertView.findViewById(R.id.tv_sur_detail_waterfeel);
			holder.mMould = (TextView) convertView.findViewById(R.id.tv_sur_detail_mould);
			holder.mOilTrace = (TextView) convertView.findViewById(R.id.tv_sur_detail_oiltrace);
			holder.mMothy = (TextView) convertView.findViewById(R.id.tv_sur_detail_mothy);
			holder.mOdor = (TextView) convertView.findViewById(R.id.tv_sur_detail_odor);
			holder.mImpurity = (TextView) convertView.findViewById(R.id.tv_sur_detail_impurity);

			convertView.setOnClickListener(mItemClickListener);
			break;

		case Content.LIST_SURVEY_THIRD:
			holder.mBarCodeET = (EditText) convertView.findViewById(R.id.et_sur_third_barcode);
			holder.mColligate = (TextView) convertView.findViewById(R.id.tv_sur_third_colligate);
			holder.mAppearance = (TextView) convertView.findViewById(R.id.tv_sur_third_appearance);
			holder.mColor = (TextView) convertView.findViewById(R.id.tv_sur_third_color);
			holder.mWaterFell = (TextView) convertView.findViewById(R.id.tv_sur_third_waterfeel);
			holder.mMould = (TextView) convertView.findViewById(R.id.tv_sur_third_mould);
			holder.mOilTrace = (TextView) convertView.findViewById(R.id.tv_sur_third_oiltrace);
			holder.mMothy = (TextView) convertView.findViewById(R.id.tv_sur_third_mothy);
			holder.mOdor = (TextView) convertView.findViewById(R.id.tv_sur_third_odor);
			holder.mImpurity = (TextView) convertView.findViewById(R.id.tv_sur_third_impurity);
			holder.mPicture = (TextView) convertView.findViewById(R.id.tv_sur_third_picture);

			convertView.setOnLongClickListener(mLongClickListener);
			holder.mBarCodeET.setOnLongClickListener(mLongClickListener);
			holder.mBarCodeET.setOnFocusChangeListener(mFocusChangeListener);
			holder.mColligate.setOnLongClickListener(mLongClickListener);
			holder.mAppearance.setOnLongClickListener(mLongClickListener);
			holder.mColor.setOnLongClickListener(mLongClickListener);
			holder.mWaterFell.setOnLongClickListener(mLongClickListener);
			holder.mMould.setOnLongClickListener(mLongClickListener);
			holder.mOilTrace.setOnLongClickListener(mLongClickListener);
			holder.mMothy.setOnLongClickListener(mLongClickListener);
			holder.mOdor.setOnLongClickListener(mLongClickListener);
			holder.mImpurity.setOnLongClickListener(mLongClickListener);
			holder.mPicture.setOnClickListener(mItemClickListener);

			break;

		default:
			break;
		}

		return holder;
	}

	private void fillData(int position, View convertView, ViewHolder mHolder) {
		/*TODO 注意：	
		 * 调用judgeFocus(mEditText, position, serialNum)应在mEditText.setText(mString)之前
		 * 因为滑动ListView，一个获得焦点的EditText（此时pos = A）将会失去焦点移出屏幕外,而pos = B的EditText从另一端进入屏幕，
		 * 此时会复用此EditText。调用judgeFocus是为了使此EditText失去焦点（因为从效果上看，已经移除屏幕外，如果不清除焦点，一旦复用，焦点还在），
		 * A失去焦点时会判断此时EditText的值保存至BeanList中。若先赋值再失去焦点，则保存的值为pos = B时的值，逻辑是错误的。
		 */
		switch (mMode) {
		case Content.LIST_LOGIN: {
			mHolder.mUser.setText(mBeanList.get(position).getUser());
			mHolder.mDelete.setTag(position);
		}
			break;

		case Content.LIST_RECEIVE: {
			mHolder.mDate.setText(mBeanList.get(position).getDate());
			mHolder.mOrderID.setText(mBeanList.get(position).getOrderID());
			mHolder.mType.setText(mBeanList.get(position).getType());
			mHolder.mPlanNum.setText(mBeanList.get(position).getPlanNum());
			mHolder.mPcNum.setText(mBeanList.get(position).getPCNum());
			mHolder.mBuilding.setText(mBeanList.get(position).getBuilding());
			mHolder.mCarNum.setText(mBeanList.get(position).getCarNum());
			mHolder.mNCState.setText(mBeanList.get(position).getNCState());
		}
			break;

		case Content.LIST_RECEIVE_DETAIL: {
			mHolder.mGoodsID.setText(mBeanList.get(position).getGoodsID());
			mHolder.mGoodsName.setText(mBeanList.get(position).getGoodsName());
			mHolder.mSpec.setText(mBeanList.get(position).getSpec());
			mHolder.mPlanNum.setText(mBeanList.get(position).getPlanNum());
			mHolder.mPlanWeight.setText(mBeanList.get(position).getPlanWeight());

			String factweight = countFactWeight(mBeanList.get(position).getFactNum(), position);
			mBeanList.get(position).setFactWeight(factweight);
			mHolder.mFactWeight.setText(mBeanList.get(position).getFactWeight());

			mHolder.mLock.setEnabled(!mNCState);
			mHolder.mLock.setTag(position);
			mHolder.mLock.setTag(R.id.target_view, mHolder.mFactNum);
			mHolder.mLock.setTag(R.id.target_second, convertView);
			mHolder.mLock.setText(mBeanList.get(position).getLockState());
			mLock = mBeanList.get(position).getLockState().equals("1");
			mHolder.mLock.setChecked(mLock);

			judgeFocus(mHolder.mFactNum, position, 0);
			mHolder.mFactNum.setText(mBeanList.get(position).getFactNum());
			mHolder.mFactNum.setTag(position);
			mHolder.mFactNum.setTag(R.id.target_view, mHolder.mFactWeight);
		}
			break;

		case Content.LIST_RECEIVE_LOSS: {
			convertView.setLongClickable(!mNCState);
			mHolder.mSpec.setText(mBeanList.get(position).getSpec());
			mHolder.mRate.setText(mBeanList.get(position).getRate());
			mHolder.mType.setText(mBeanList.get(position).getType());
			mHolder.mType.setTag(position);

			judgeFocus(mHolder.mLossWeight, position, 1);
			mHolder.mLossWeight.setText(mBeanList.get(position).getLossWeight());
			mHolder.mLossWeight.setTag(position);
			mHolder.mLossWeight.setTag(R.id.target_view, mHolder.mRate);

			judgeFocus(mHolder.mBarCodeET, position, 2);
			mHolder.mBarCodeET.setText(mBeanList.get(position).getBarCode());
			mHolder.mBarCodeET.setTag(position);
		}
			break;

		case Content.LIST_INPUT: {
			mHolder.mDate.setText(mBeanList.get(position).getDate());
			mHolder.mOrderID.setText(mBeanList.get(position).getOrderID());
			mHolder.mType.setText(mBeanList.get(position).getType());
			mHolder.mPlanNum.setText(mBeanList.get(position).getPlanNum());
			mHolder.mPcNum.setText(mBeanList.get(position).getPCNum());
			mHolder.mBuilding.setText(mBeanList.get(position).getBuilding());
			mHolder.mCarNum.setText(mBeanList.get(position).getCarNum());
			mHolder.mNCState.setText(mBeanList.get(position).getNCState());
		}
			break;

		case Content.LIST_INPUT_DETAIL: {
			mHolder.mTrayName.setText(mBeanList.get(position).getTrayName());
			mHolder.mGoodsID.setText(mBeanList.get(position).getGoodsID());
			mHolder.mGoodsName.setText(mBeanList.get(position).getGoodsName());
			mHolder.mSpec.setText(mBeanList.get(position).getSpec());
			mHolder.mPlanNum.setText(mBeanList.get(position).getPlanNum());
			mHolder.mPlanWeight.setText(mBeanList.get(position).getPlanWeight());

			String factweight = countFactWeight(mBeanList.get(position).getFactNum(), position);
			mBeanList.get(position).setFactWeight(factweight);
			mHolder.mFactWeight.setText(mBeanList.get(position).getFactWeight());

			judgeFocus(mHolder.mFactNum, position, 0);
			mHolder.mFactNum.setText(mBeanList.get(position).getFactNum());
			mHolder.mFactNum.setTag(position);
			mHolder.mFactNum.setTag(R.id.target_view, mHolder.mFactWeight);
		}
			break;

		case Content.LIST_INPUT_THIRD: {
			mHolder.mCarID.setText(mBeanList.get(position).getCarID());
			mHolder.mDoorID.setText(mBeanList.get(position).getDoorID());
			mHolder.mBarCode.setText(mBeanList.get(position).getBarCode());
			mHolder.mInTime.setText(mBeanList.get(position).getInTime());
		}
			break;

		case Content.LIST_OUTPUT: {
			mHolder.mDate.setText(mBeanList.get(position).getDate());
			mHolder.mOrderID.setText(mBeanList.get(position).getOrderID());
			mHolder.mType.setText(mBeanList.get(position).getType());
			mHolder.mGoalWare.setText(mBeanList.get(position).getGoalWare());
			mHolder.mPlanNum.setText(mBeanList.get(position).getPlanNum());
			mHolder.mBuilding.setText(mBeanList.get(position).getBuilding());
			mHolder.mCarNum.setText(mBeanList.get(position).getCarNum());
			mHolder.mNCState.setText(mBeanList.get(position).getNCState());
		}
			break;

		case Content.LIST_OUTPUT_DETAIL: {
			mHolder.mTrayName.setText(mBeanList.get(position).getTrayName());
			mHolder.mGoodsID.setText(mBeanList.get(position).getGoodsID());
			mHolder.mGoodsName.setText(mBeanList.get(position).getGoodsName());
			mHolder.mVbomName.setText(mBeanList.get(position).getVbomName());
			mHolder.mSpec.setText(mBeanList.get(position).getSpec());
			mHolder.mPlanNum.setText(mBeanList.get(position).getPlanNum());
			mHolder.mPlanWeight.setText(mBeanList.get(position).getPlanWeight());

			String factweight = countFactWeight(mBeanList.get(position).getFactNum(), position);
			mBeanList.get(position).setFactWeight(factweight);
			mHolder.mFactWeight.setText(mBeanList.get(position).getFactWeight());

			judgeFocus(mHolder.mFactNum, position, 0);
			mHolder.mFactNum.setText(mBeanList.get(position).getFactNum());
			mHolder.mFactNum.setTag(position);
			mHolder.mFactNum.setTag(R.id.target_view, mHolder.mFactWeight);
		}
			break;

		case Content.LIST_OUTPUT_THIRD: {
			mHolder.mCarID.setText(mBeanList.get(position).getCarID());
			mHolder.mDoorID.setText(mBeanList.get(position).getDoorID());
			mHolder.mBarCode.setText(mBeanList.get(position).getBarCode());
			mHolder.mOutTime.setText(mBeanList.get(position).getOutTime());
		}
			break;

		case Content.LIST_INVENTORY: {
			mHolder.mDate.setText(mBeanList.get(position).getDate());
			mHolder.mOrderID.setText(mBeanList.get(position).getOrderID());
			mHolder.mType.setText(mBeanList.get(position).getType());
			mHolder.mBuilding.setText(mBeanList.get(position).getBuilding());
			mHolder.mTrayTotle.setText(mBeanList.get(position).getTrayTotle());
			mHolder.mTrayDone.setText(mBeanList.get(position).getTrayDone());
			mHolder.mIvtNum.setText(mBeanList.get(position).getIvtNum());
			mHolder.mIvtDone.setText(mBeanList.get(position).getIvtDone());
			mHolder.mIvtWait.setText(mBeanList.get(position).getIvtWait());
		}
			break;

		case Content.LIST_INVENTORY_DETAIL: {
			convertView.setLongClickable(!mNCState);
			mHolder.mIvtWeight.setText(mBeanList.get(position).getIvtWeight());
			mHolder.mDate.setText(mBeanList.get(position).getDate());

			judgeFocus(mHolder.mTrayIDET, position, 1);
			mHolder.mTrayIDET.setText(mBeanList.get(position).getTrayID());
			mHolder.mTrayIDET.setTag(position);

			judgeFocus(mHolder.mGoodsIDET, position, 2);
			mHolder.mGoodsIDET.setText(mBeanList.get(position).getGoodsID());
			mHolder.mGoodsIDET.setTag(position);

			judgeFocus(mHolder.mStockNum, position, 3);
			mHolder.mStockNum.setText(mBeanList.get(position).getStockNum());
			mHolder.mStockNum.setTag(position);

			judgeFocus(mHolder.mStockWeight, position, 4);
			mHolder.mStockWeight.setText(mBeanList.get(position).getStockWeight());
			mHolder.mStockWeight.setTag(position);

			judgeFocus(mHolder.mIvtNumET, position, 5);
			mHolder.mIvtNumET.setText(mBeanList.get(position).getIvtNum());
			mHolder.mIvtNumET.setTag(position);
			mHolder.mIvtNumET.setTag(R.id.target_view, mHolder.mIvtWeight);

			judgeFocus(mHolder.mRateET, position, 6);
			mHolder.mRateET.setText(mBeanList.get(position).getRate());
			mHolder.mRateET.setTag(position);
			mHolder.mRateET.setTag(R.id.target_view, mHolder.mIvtWeight);
		}
			break;

		case Content.LIST_ASSIGN: {
			mHolder.mDate.setText(mBeanList.get(position).getDate());
			mHolder.mOrderID.setText(mBeanList.get(position).getOrderID());
			mHolder.mType.setText(mBeanList.get(position).getType());
			mHolder.mGoodsName.setText(mBeanList.get(position).getGoodsName());
			mHolder.mBuilding.setText(mBeanList.get(position).getBuilding());
			mHolder.mCarNum.setText(mBeanList.get(position).getCarNum());
			mHolder.mCheckLine.setText(mBeanList.get(position).getCheckLine());
			mHolder.mAssignState.setText(mBeanList.get(position).getAssignState());
		}
			break;

		case Content.LIST_EXAMINE: {
			mHolder.mDate.setText(mBeanList.get(position).getDate());
			mHolder.mOrderID.setText(mBeanList.get(position).getOrderID());
			mHolder.mType.setText(mBeanList.get(position).getType());
			mHolder.mGoodsName.setText(mBeanList.get(position).getGoodsName());
			mHolder.mBuilding.setText(mBeanList.get(position).getBuilding());
			mHolder.mCarNum.setText(mBeanList.get(position).getCarNum());
			mHolder.mCheckLine.setText(mBeanList.get(position).getCheckLine());
			mHolder.mNCState.setText(mBeanList.get(position).getNCState());
		}
			break;

		case Content.LIST_EXAMINE_DETAIL: {
			mLock = (position == mBeanList.size() - 1);
			if (mLock) {
				mHolder.mPosition.setText("X");
			}
			mHolder.mBarCode.setText(mBeanList.get(position).getBarCode());
			mHolder.mRFIDCode.setText(mBeanList.get(position).getRFIDCode());

			mHolder.mWeight.setText(Content.Keep2Decimal(mBeanList.get(position).getFactWeight()));// 颜色变化
			if (mBeanList.get(position).getWeight().equals("2")) {
				mHolder.mWeight.setBackgroundResource(R.color.red_light);
			} else {
				mHolder.mWeight.setBackgroundResource(R.color.transparent);
			}

			mHolder.mWater.setText(Content.Keep2Decimal(mBeanList.get(position).getFactWater()));// 颜色变化
			if (mBeanList.get(position).getWater().equals("2")) {
				mHolder.mWater.setBackgroundResource(R.color.red_light);
			} else {
				mHolder.mWater.setBackgroundResource(R.color.transparent);
			}

			setExceptionBackGround(mHolder.mAppearance, mBeanList.get(position).getAppearance());
			setExceptionBackGround(mHolder.mMould, mBeanList.get(position).getMould());
			setExceptionBackGround(mHolder.mOdor, mBeanList.get(position).getOdor());
			setExceptionBackGround(mHolder.mImpurity, mBeanList.get(position).getImpurity());
			setExceptionBackGround(mHolder.mMothy, mBeanList.get(position).getMothy());

			mHolder.mWaterDev.setText(Content.Keep2Decimal(mBeanList.get(position).getWaterDev()));
			mHolder.mDensityDev.setText(Content.Keep2Decimal(mBeanList.get(position).getDensityDev()));

			setExceptionBackGround(mHolder.mColligate, mBeanList.get(position).getColligate());
			mHolder.mDate.setText(mBeanList.get(position).getDate());
		}
			break;

		case Content.LIST_PATROL: {
			mHolder.mDate.setText(mBeanList.get(position).getDate());
			mHolder.mMeridiem.setText(mBeanList.get(position).getMeridiem());
			mHolder.mOrderID.setText(mBeanList.get(position).getOrderID());
			mHolder.mBuilding.setText(mBeanList.get(position).getBuilding());
			mHolder.mPerson.setText(mBeanList.get(position).getPerson());
			mHolder.mNCState.setText(mBeanList.get(position).getNCState());
		}
			break;

		case Content.LIST_PATROL_DETAIL: {
			mHolder.mFloor.setText(mBeanList.get(position).getFloor());
			mHolder.mPoint.setText(mBeanList.get(position).getPoint());

			mHolder.mResult.setText(mBeanList.get(position).getResult());
			mHolder.mResult.setLongClickable(!mNCState);
			mHolder.mResult.setTag(position);

			judgeFocus(mHolder.mTrouble, position, 1);
			mHolder.mTrouble.setText(mBeanList.get(position).getTrouble());
			mHolder.mTrouble.setTag(position);

			String value = (mBeanList.get(position).getPicture().length() > 37) ? "有" : "无";
			mHolder.mPicture.setText(value);
			mHolder.mPicture.setTag(position);

			judgeFocus(mHolder.mSolution, position, 2);
			mHolder.mSolution.setText(mBeanList.get(position).getSolution());
			mHolder.mSolution.setTag(position);

			mHolder.mRepairDate.setText(Content.getDate(mBeanList.get(position).getRepairDate()));
			mHolder.mRepairDate.setLongClickable(!mNCState);
			mHolder.mRepairDate.setTag(position);
		}
			break;

		case Content.LIST_PESTS: {
			mHolder.mDate.setText(mBeanList.get(position).getDate());
			mHolder.mOrderID.setText(mBeanList.get(position).getOrderID());
			mHolder.mBuilding.setText(mBeanList.get(position).getBuilding());
			mHolder.mNote.setText(mBeanList.get(position).getNote());
			mHolder.mPerson.setText(mBeanList.get(position).getPerson());
			mHolder.mNCState.setText(mBeanList.get(position).getNCState());
		}
			break;

		case Content.LIST_PESTS_DETAIL: {
			convertView.setLongClickable(!mNCState);

			judgeFocus(mHolder.mFloorET, position, 1);
			mHolder.mFloorET.setText(mBeanList.get(position).getFloor());
			mHolder.mFloorET.setTag(position);

			judgeFocus(mHolder.mCardNum, position, 2);
			mHolder.mCardNum.setText(mBeanList.get(position).getCardNum());
			mHolder.mCardNum.setTag(position);

			judgeFocus(mHolder.mSerricorneNum, position, 3);
			mHolder.mSerricorneNum.setText(mBeanList.get(position).getSerricorneNum());
			mHolder.mSerricorneNum.setTag(position);

			judgeFocus(mHolder.mElutellaNum, position, 4);
			mHolder.mElutellaNum.setText(mBeanList.get(position).getElutellaNum());
			mHolder.mElutellaNum.setTag(position);

			String value = (mBeanList.get(position).getPicture().length() > 37) ? "有" : "无";
			mHolder.mPicture.setText(value);
			mHolder.mPicture.setTag(position);

			judgeFocus(mHolder.mSolution, position, 5);
			mHolder.mSolution.setText(mBeanList.get(position).getSolution());
			mHolder.mSolution.setTag(position);
		}
			break;

		case Content.LIST_FUMIGATE: {
			mHolder.mDate.setText(mBeanList.get(position).getDate());
			mHolder.mOrderID.setText(mBeanList.get(position).getOrderID());
			mHolder.mBuilding.setText(mBeanList.get(position).getBuilding());
			mHolder.mMethod.setText(mBeanList.get(position).getMethod());
			mHolder.mDosingDate.setText(mBeanList.get(position).getDosingDate());
			mHolder.mPerson.setText(mBeanList.get(position).getPerson());
			mHolder.mNCState.setText(mBeanList.get(position).getNCState());
		}
			break;

		case Content.LIST_FUMIGATE_DETAIL: {
			if (mHolder.mFloor != null) {
				mHolder.mFloor.setText(mBeanList.get(position).getTrayName());
				mHolder.mLocation.setText(mBeanList.get(position).getLocation());
			} else if (mHolder.mTrayName != null) {
				mHolder.mTrayName.setText(mBeanList.get(position).getTrayName());
				mHolder.mTrayName.setTag(position);
			}

			judgeFocus(mHolder.mConcentration6, position, 1);
			mHolder.mConcentration6.setText(mBeanList.get(position).getConcentration6());
			mHolder.mConcentration6.setTag(position);

			judgeFocus(mHolder.mConcentration12, position, 2);
			mHolder.mConcentration12.setText(mBeanList.get(position).getConcentration12());
			mHolder.mConcentration12.setTag(position);

			judgeFocus(mHolder.mConcentration24, position, 3);
			mHolder.mConcentration24.setText(mBeanList.get(position).getConcentration24());
			mHolder.mConcentration24.setTag(position);

			judgeFocus(mHolder.mConcentration48, position, 4);
			mHolder.mConcentration48.setText(mBeanList.get(position).getConcentration48());
			mHolder.mConcentration48.setTag(position);

			judgeFocus(mHolder.mConcentration72, position, 5);
			mHolder.mConcentration72.setText(mBeanList.get(position).getConcentration72());
			mHolder.mConcentration72.setTag(position);

			judgeFocus(mHolder.mConcentration96, position, 6);
			mHolder.mConcentration96.setText(mBeanList.get(position).getConcentration96());
			mHolder.mConcentration96.setTag(position);

			judgeFocus(mHolder.mConcentration144, position, 7);
			mHolder.mConcentration144.setText(mBeanList.get(position).getConcentration144());
			mHolder.mConcentration144.setTag(position);

			judgeFocus(mHolder.mConcentration216, position, 8);
			mHolder.mConcentration216.setText(mBeanList.get(position).getConcentration216());
			mHolder.mConcentration216.setTag(position);

			String value = (mBeanList.get(position).getPicture().length() > 37) ? "有" : "无";
			mHolder.mPicture.setText(value);
			mHolder.mPicture.setTag(position);
		}
			break;

		case Content.LIST_SURVEY: {
			mHolder.mDate.setText(mBeanList.get(position).getDate());
			mHolder.mOrderID.setText(mBeanList.get(position).getOrderID());
			mHolder.mBuilding.setText(mBeanList.get(position).getBuilding());
			mHolder.mFloor.setText(mBeanList.get(position).getFloor());
			mHolder.mDetailNum.setText(mBeanList.get(position).getDetailNum());
			mHolder.mPerson.setText(mBeanList.get(position).getPerson());
			mHolder.mNCState.setText(mBeanList.get(position).getNCState());
		}
			break;

		case Content.LIST_SURVEY_DETAIL: {
			mHolder.mTrayName.setText(mBeanList.get(position).getTrayName());
			mHolder.mGoodsID.setText(mBeanList.get(position).getGoodsID());
			mHolder.mGoodsName.setText(mBeanList.get(position).getGoodsName());
			mHolder.mTrayNum.setText(mBeanList.get(position).getTrayNum());
			mHolder.mCheckNum.setText(mBeanList.get(position).getCheckNum());

			setExceptionBackGround(mHolder.mColligate, mBeanList.get(position).getColligate());
			setExceptionBackGround(mHolder.mAppearance, mBeanList.get(position).getAppearance());
			setExceptionBackGround(mHolder.mColor, mBeanList.get(position).getColor());
			setExceptionBackGround(mHolder.mWaterFell, mBeanList.get(position).getWaterFell());
			setExceptionBackGround(mHolder.mMould, mBeanList.get(position).getMould());
			setExceptionBackGround(mHolder.mOilTrace, mBeanList.get(position).getOilTrace());
			setExceptionBackGround(mHolder.mMothy, mBeanList.get(position).getMothy());
			setExceptionBackGround(mHolder.mOdor, mBeanList.get(position).getOdor());
			setExceptionBackGround(mHolder.mImpurity, mBeanList.get(position).getImpurity());
		}
			break;

		case Content.LIST_SURVEY_THIRD: {
			judgeFocus(mHolder.mBarCodeET, position, 0);
			mHolder.mBarCodeET.setText(mBeanList.get(position).getBarCode());
			mHolder.mBarCodeET.setTag(position);

			setExceptionBackGround(mHolder.mColligate, mBeanList.get(position).getColligate());
			mHolder.mColligate.setLongClickable(!mNCState);
			mHolder.mColligate.setTag(position);

			setExceptionBackGround(mHolder.mAppearance, mBeanList.get(position).getAppearance());
			mHolder.mAppearance.setLongClickable(!mNCState);
			mHolder.mAppearance.setTag(position);
			setExceptionBackGround(mHolder.mColor, mBeanList.get(position).getColor());
			mHolder.mColor.setLongClickable(!mNCState);
			mHolder.mColor.setTag(position);
			setExceptionBackGround(mHolder.mWaterFell, mBeanList.get(position).getWaterFell());
			mHolder.mWaterFell.setLongClickable(!mNCState);
			mHolder.mWaterFell.setTag(position);
			setExceptionBackGround(mHolder.mMould, mBeanList.get(position).getMould());
			mHolder.mMould.setLongClickable(!mNCState);
			mHolder.mMould.setTag(position);
			setExceptionBackGround(mHolder.mOilTrace, mBeanList.get(position).getOilTrace());
			mHolder.mOilTrace.setLongClickable(!mNCState);
			mHolder.mOilTrace.setTag(position);
			setExceptionBackGround(mHolder.mMothy, mBeanList.get(position).getMothy());
			mHolder.mMothy.setLongClickable(!mNCState);
			mHolder.mMothy.setTag(position);
			setExceptionBackGround(mHolder.mOdor, mBeanList.get(position).getOdor());
			mHolder.mOdor.setLongClickable(!mNCState);
			mHolder.mOdor.setTag(position);
			setExceptionBackGround(mHolder.mImpurity, mBeanList.get(position).getImpurity());
			mHolder.mImpurity.setLongClickable(!mNCState);
			mHolder.mImpurity.setTag(position);

			String value = (mBeanList.get(position).getPicture().length() > 37) ? "有" : "无";
			mHolder.mPicture.setText(value);
			mHolder.mPicture.setTag(position);
		}
			break;

		default:
			break;
		}
	}

	private OnClickListener mItemClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.tv_login_delete: {
				int mPos = (Integer) v.getTag();
				mCallback.onItemClick(v, mPos, mBeanList.get(mPos));
			}
				break;

			case R.id.layout_list_rec_detail: {
				int mPos = (Integer) v.getTag();
				mCallback.onItemClick(v, mPos, mBeanList.get(mPos));
			}
				break;

			case R.id.tv_rec_loss_type: {
				int mPos = (Integer) v.getTag();
				mCallback.onItemClick(v, mPos, null);
			}
				break;

			case R.id.layout_list_ipt_detail: {
				int mPos = (Integer) v.getTag();
				mCallback.onItemClick(v, mPos, null);
			}
				break;

			case R.id.layout_list_opt_detail: {
				int mPos = (Integer) v.getTag();
				mCallback.onItemClick(v, mPos, null);
			}
				break;

			case R.id.tv_ptl_detail_picture: {
				int mPos = (Integer) v.getTag();
				mCallback.onItemClick(v, mPos, mBeanList.get(mPos));
			}
				break;

			case R.id.tv_pts_detail_picture: {
				int mPos = (Integer) v.getTag();
				mCallback.onItemClick(v, mPos, mBeanList.get(mPos));
			}
				break;

			case R.id.tv_fmg_detail_trayname: {
				int mPos = (Integer) v.getTag();
				mCallback.onItemClick(v, mPos, null);
			}
				break;

			case R.id.tv_fmg_detail_picture: {
				int mPos = (Integer) v.getTag();
				mCallback.onItemClick(v, mPos, mBeanList.get(mPos));
			}
				break;

			case R.id.layout_list_sur_detail: {
				int mPos = (Integer) v.getTag();
				mCallback.onItemClick(v, mPos, null);
			}
				break;

			case R.id.tv_sur_third_picture: {
				int mPos = (Integer) v.getTag();
				mCallback.onItemClick(v, mPos, mBeanList.get(mPos));
			}
				break;

			default:
				break;
			}
		}
	};

	private OnLongClickListener mLongClickListener = new OnLongClickListener() {

		@Override
		public boolean onLongClick(View v) {
			switch (v.getId()) {
			case R.id.layout_list_rec_loss: {// 长按删除途损明细表中一条记录
				if (mFocusView != null && mFocusView.isFocused()) {//TODO
					MyLog.e(TAG, "LongClick","----mFocusView.isFocused()");
					mFocusView.clearFocus();
				}
				int mPos = (Integer) v.getTag();
				mCallback.onItemDelete(v, mPos);
				break;
			}

			case R.id.tv_ptl_detail_result: {// 长按改变巡查检测结果
				int mPos = (Integer) v.getTag();
				String Value = ((TextView) v).getText().toString();
				Value = Value.equals(StringUtils.disqualified) ? StringUtils.qualified : StringUtils.disqualified;
				setExceptionBackGround((TextView) v, Value);
				mBeanList.get(mPos).setResult(Value);
				break;
			}

			case R.id.tv_ptl_detail_repairdate: {// 长按改变巡查整改完成时间
				int mPos = (Integer) v.getTag();
				String Value = ((TextView) v).getText().toString();
				Value = Value.equals("") ? Content.getDate_Now() : "";
				((TextView) v).setText(Value);
				mBeanList.get(mPos).setRepairDate(Value);
				break;
			}

			case R.id.layout_list_ivt_detail: {// 长按删除盘点明细表中一条记录
				if (mFocusView != null && mFocusView.isFocused()) {
					MyLog.e(TAG, "LongClick","----mFocusView.isFocused()");
					mFocusView.clearFocus();
				}
				int mPos = (Integer) v.getTag();
				mCallback.onItemDelete(v, mPos);
				break;
			}

			case R.id.layout_list_sur_third: {// 长按删除普查子子表中一条记录
				if (mFocusView != null && mFocusView.isFocused()) {
					mFocusView.clearFocus();
				}
				int mPos = (Integer) v.getTag();
				mCallback.onItemDelete(v, mPos);
				break;
			}

			case R.id.tv_sur_third_colligate: {// 长按改变综合判定
				int mPos = (Integer) v.getTag();
				String Value = ((TextView) v).getText().toString();
				Value = Value.equals(StringUtils.disqualified) ? StringUtils.qualified : StringUtils.disqualified;
				setExceptionBackGround((TextView) v, Value);
				mBeanList.get(mPos).setColligate(Value);
				break;
			}

			case R.id.tv_sur_third_appearance: {// 长按改变外观
				int mPos = (Integer) v.getTag();
				String Value = ((TextView) v).getText().toString();
				Value = Value.equals(StringUtils.disqualified) ? StringUtils.qualified : StringUtils.disqualified;
				setExceptionBackGround((TextView) v, Value);
				mBeanList.get(mPos).setAppearance(Value);
				break;
			}

			case R.id.tv_sur_third_color: {// 长按改变颜色
				int mPos = (Integer) v.getTag();
				String Value = ((TextView) v).getText().toString();
				Value = Value.equals(StringUtils.disqualified) ? StringUtils.qualified : StringUtils.disqualified;
				setExceptionBackGround((TextView) v, Value);
				mBeanList.get(mPos).setColor(Value);
				break;
			}

			case R.id.tv_sur_third_waterfeel: {// 长按改变手感水分
				int mPos = (Integer) v.getTag();
				String Value = ((TextView) v).getText().toString();
				Value = Value.equals(StringUtils.disqualified) ? StringUtils.qualified : StringUtils.disqualified;
				setExceptionBackGround((TextView) v, Value);
				mBeanList.get(mPos).setWaterFell(Value);
				break;
			}

			case R.id.tv_sur_third_mould: {// 长按改变霉变
				int mPos = (Integer) v.getTag();
				String Value = ((TextView) v).getText().toString();
				if (Value.equals(StringUtils.none)) {
					Value = StringUtils.slight;
				} else if (Value.equals(StringUtils.slight)) {
					Value = StringUtils.severe;
				} else {
					Value = StringUtils.none;
				}
				setExceptionBackGround((TextView) v, Value);
				mBeanList.get(mPos).setMould(Value);
				break;
			}

			case R.id.tv_sur_third_oiltrace: {// 长按改变油印
				int mPos = (Integer) v.getTag();
				String Value = ((TextView) v).getText().toString();
				if (Value.equals(StringUtils.none)) {
					Value = StringUtils.slight;
				} else if (Value.equals(StringUtils.slight)) {
					Value = StringUtils.severe;
				} else {
					Value = StringUtils.none;
				}
				setExceptionBackGround((TextView) v, Value);
				mBeanList.get(mPos).setOilTrace(Value);
				break;
			}

			case R.id.tv_sur_third_mothy: {// 长按改变虫蛀
				int mPos = (Integer) v.getTag();
				String Value = ((TextView) v).getText().toString();
				if (Value.equals(StringUtils.none)) {
					Value = StringUtils.slight;
				} else if (Value.equals(StringUtils.slight)) {
					Value = StringUtils.severe;
				} else {
					Value = StringUtils.none;
				}
				setExceptionBackGround((TextView) v, Value);
				mBeanList.get(mPos).setMothy(Value);
				break;
			}

			case R.id.tv_sur_third_odor: {// 长按改变异味
				int mPos = (Integer) v.getTag();
				String Value = ((TextView) v).getText().toString();
				Value = Value.equals(StringUtils.exist) ? StringUtils.none : StringUtils.exist;
				setExceptionBackGround((TextView) v, Value);
				mBeanList.get(mPos).setOdor(Value);
				break;
			}

			case R.id.tv_sur_third_impurity: {// 长按改变杂物
				int mPos = (Integer) v.getTag();
				String Value = ((TextView) v).getText().toString();
				Value = Value.equals(StringUtils.exist) ? StringUtils.none : StringUtils.exist;
				setExceptionBackGround((TextView) v, Value);
				mBeanList.get(mPos).setImpurity(Value);
				break;
			}

			default:

				break;
			}
			return false;
		}
	};

	private OnFocusChangeListener mFocusChangeListener = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			// 此处情况特殊，如果回调onItemUpdate方法会notifyDataSetChanged,导致下一个获取焦点的View失去焦点。因此数据应在Adapter内部处理。
			mFocusNum = 0;
			mFocusView = v;
			mFocusPos = (Integer) v.getTag();
//			mFocusPos = mFocusPos;
			switch (v.getId()) {
			case R.id.et_rec_detail_factnum: {
				if (!hasFocus) {// 失去焦点时，mFocusPos为失去焦点的view的position
					String oldValue = mBeanList.get(mFocusPos).getFactNum();
					String newValue = ((EditText) v).getText().toString();
					try {
						newValue = Integer.parseInt(newValue) + "";
					} catch (NumberFormatException e) {
						((EditText) v).setText(oldValue);
						return;
					}

					if (newValue.equals(oldValue)) {
						((EditText) v).setText(oldValue);
					} else {
						String factweight = countFactWeight(newValue, mFocusPos);
						TextView TargetView = (TextView) v.getTag(R.id.target_view);
						TargetView.setText(factweight);
						((EditText) v).setText(newValue);
						mBeanList.get(mFocusPos).setFactNum(newValue);
						mBeanList.get(mFocusPos).setFactWeight(factweight);
					}
					mFocusView = null;
				}
			}
				break;

			case R.id.et_rec_loss_lossweight: {//TODO
				mFocusNum = 1;
				if (!hasFocus) {// 失去焦点时，mFocusPos为失去焦点的view的position
					MyLog.e(TAG, "FocusChange","----mFocusPos = " + mFocusPos +", mBeanList.size() = "+mBeanList.size());
					String oldValue = mBeanList.get(mFocusPos).getLossWeight();
					String LossWeight = ((EditText) v).getText().toString();
					double lossWeight = -1;
					double spec = 0;
					try {
						lossWeight = Double.parseDouble(LossWeight);
					} catch (NumberFormatException e) {
					}
					spec = Content.parseDouble(mBeanList.get(mFocusPos).getSpec());
					if (lossWeight < 0 || lossWeight > spec) {
						((EditText) v).setText(oldValue);
						return;
					}
					LossWeight = Content.DoubleToString(lossWeight);
					String Rate = Content.DoubleToString(spec - lossWeight);
					mBeanList.get(mFocusPos).setLossWeight(LossWeight);
					mBeanList.get(mFocusPos).setRate(Rate);
					TextView TargetView = (TextView) v.getTag(R.id.target_view);
					TargetView.setText(Rate);
					((EditText) v).setText(LossWeight);
					mFocusView = null;
				}
			}
				break;

			case R.id.et_rec_loss_barcode: {
				mFocusNum = 2;
				if (!hasFocus) {// 失去焦点时，mFocusPos为失去焦点的view的position
					String oldValue = mBeanList.get(mFocusPos).getBarCode();
					String BarCode = ((EditText) v).getText().toString();
					if (BarCode.equals(oldValue) || BarCode.equals("")) {
						((EditText) v).setText(oldValue);
					} else {
						mBeanList.get(mFocusPos).setBarCode(BarCode);
					}
					mFocusView = null;
				}
			}
				break;

			case R.id.et_ptl_detail_trouble: {
				mFocusNum = 1;
				if (!hasFocus) {// 失去焦点时，mFocusPos为失去焦点的view的position
					String oldValue = mBeanList.get(mFocusPos).getTrouble();
					String newValue = ((EditText) v).getText().toString();
					if (newValue.equals(oldValue)) {
						((EditText) v).setText(oldValue);
					} else {
						mBeanList.get(mFocusPos).setTrouble(newValue);
					}
					mFocusView = null;
				}
			}
				break;

			case R.id.et_ptl_detail_solution: {
				mFocusNum = 2;
				if (!hasFocus) {// 失去焦点时，mFocusPos为失去焦点的view的position
					String oldValue = mBeanList.get(mFocusPos).getSolution();
					String newValue = ((EditText) v).getText().toString();
					if (newValue.equals(oldValue)) {
						((EditText) v).setText(oldValue);
					} else {
						mBeanList.get(mFocusPos).setSolution(newValue);
					}
					mFocusView = null;
				}
			}
				break;

			case R.id.et_pts_detail_floor: {
				mFocusNum = 1;
				if (!hasFocus) {// 失去焦点时，mFocusPos为失去焦点的view的position
					String oldValue = mBeanList.get(mFocusPos).getFloor();
					String Floor = ((EditText) v).getText().toString();
					int floor = 0;
					try {
						floor = Integer.parseInt(Floor);
					} catch (NumberFormatException e) {
					}
					if (floor < 1 || floor > 5) {
						((EditText) v).setText(oldValue);
						return;
					}
					mBeanList.get(mFocusPos).setFloor(floor + "");
					((EditText) v).setText(floor + "");
					mFocusView = null;
				}
			}
				break;

			case R.id.et_pts_detail_cardnum: {
				mFocusNum = 2;
				if (!hasFocus) {// 失去焦点时，mFocusPos为失去焦点的view的position
					String oldValue = mBeanList.get(mFocusPos).getCardNum();
					String CardNum = ((EditText) v).getText().toString();
					if (CardNum == "" || CardNum.equals(oldValue)) {
						((EditText) v).setText(oldValue);
						return;
					}
					mBeanList.get(mFocusPos).setCardNum(CardNum);
					((EditText) v).setText(CardNum);
					mFocusView = null;
				}
			}
				break;

			case R.id.et_pts_detail_serricorne: {
				mFocusNum = 3;
				if (!hasFocus) {// 失去焦点时，mFocusPos为失去焦点的view的position
					String oldValue = mBeanList.get(mFocusPos).getSerricorneNum();
					String SerricorneNum = ((EditText) v).getText().toString();
					int serricorneNum = -1;
					try {
						serricorneNum = Integer.parseInt(SerricorneNum);
					} catch (NumberFormatException e) {
					}
					if (serricorneNum < 0) {
						((EditText) v).setText(oldValue);
						return;
					}
					mBeanList.get(mFocusPos).setSerricorneNum(serricorneNum + "");
					((EditText) v).setText(serricorneNum + "");
					mFocusView = null;
				}
			}
				break;

			case R.id.et_pts_detail_elutella: {
				mFocusNum = 4;
				if (!hasFocus) {// 失去焦点时，mFocusPos为失去焦点的view的position
					String oldValue = mBeanList.get(mFocusPos).getElutellaNum();
					String ElutellaNum = ((EditText) v).getText().toString();
					int elutellaNum = -1;
					try {
						elutellaNum = Integer.parseInt(ElutellaNum);
					} catch (NumberFormatException e) {
					}
					if (elutellaNum < 0) {
						((EditText) v).setText(oldValue);
						return;
					}
					mBeanList.get(mFocusPos).setElutellaNum(elutellaNum + "");
					((EditText) v).setText(elutellaNum + "");
					mFocusView = null;
				}
			}
				break;

			case R.id.et_pts_detail_solution: {
				mFocusNum = 5;
				if (!hasFocus) {// 失去焦点时，mFocusPos为失去焦点的view的position
					String oldValue = mBeanList.get(mFocusPos).getSolution();
					String Solution = ((EditText) v).getText().toString();
					if (Solution == "" || Solution.equals(oldValue)) {
						((EditText) v).setText(oldValue);
						return;
					}
					mBeanList.get(mFocusPos).setSolution(Solution);
					((EditText) v).setText(Solution);
					mFocusView = null;
				}
			}
				break;

			case R.id.et_ivt_detail_trayid: {//TODO
				mFocusNum = 1;
				if (!hasFocus) {// 失去焦点时，mFocusPos为失去焦点的view的position
					MyLog.e(TAG, "FocusChange","----mFocusPos = " + mFocusPos +", mBeanList.size() = "+mBeanList.size());
					String oldValue = mBeanList.get(mFocusPos).getTrayID();
					String newValue = ((EditText) v).getText().toString();

					if (newValue == "" || newValue.equals(oldValue)) {
						((EditText) v).setText(oldValue);
					} else {
						mBeanList.get(mFocusPos).setTrayID(newValue);
					}
					mFocusView = null;
				}
			}
				break;

			case R.id.et_ivt_detail_goodsid: {
				mFocusNum = 2;
				if (!hasFocus) {// 失去焦点时，mFocusPos为失去焦点的view的position
					String oldValue = mBeanList.get(mFocusPos).getGoodsID();
					String newValue = ((EditText) v).getText().toString();

					if (newValue == "" || newValue.equals(oldValue)) {
						((EditText) v).setText(oldValue);
					} else {
						mBeanList.get(mFocusPos).setGoodsID(newValue);
					}
					mFocusView = null;
				}
			}
				break;

			case R.id.et_ivt_detail_stocknum: {
				mFocusNum = 3;
				if (!hasFocus) {// 失去焦点时，mFocusPos为失去焦点的view的position
					String oldValue = mBeanList.get(mFocusPos).getStockNum();
					String newValue = ((EditText) v).getText().toString();
					try {
						newValue = Integer.parseInt(newValue) + "";
					} catch (NumberFormatException e) {
						((EditText) v).setText(oldValue);
						return;
					}

					if (newValue.equals(oldValue)) {
						((EditText) v).setText(oldValue);
					} else {
						mBeanList.get(mFocusPos).setStockNum(newValue);
					}
					mFocusView = null;
				}
			}
				break;

			case R.id.et_ivt_detail_stockweight: {
				mFocusNum = 4;
				if (!hasFocus) {// 失去焦点时，mFocusPos为失去焦点的view的position
					String oldValue = mBeanList.get(mFocusPos).getStockWeight();
					String newValue = ((EditText) v).getText().toString();
					try {
						newValue = Integer.parseInt(newValue) + "";
					} catch (NumberFormatException e) {
						((EditText) v).setText(oldValue);
						return;
					}

					if (newValue.equals(oldValue)) {
						((EditText) v).setText(oldValue);
					} else {
						mBeanList.get(mFocusPos).setStockWeight(newValue);
					}
					mFocusView = null;
				}
			}
				break;

			case R.id.et_ivt_detail_ivtnum: {
				mFocusNum = 5;
				if (!hasFocus) {// 失去焦点时，mFocusPos为失去焦点的view的position
					String oldValue = mBeanList.get(mFocusPos).getIvtNum();
					String newValue = ((EditText) v).getText().toString();
					try {
						newValue = Integer.parseInt(newValue) + "";
					} catch (NumberFormatException e) {
						((EditText) v).setText(oldValue);
						return;
					}

					if (newValue.equals(oldValue)) {
						((EditText) v).setText(oldValue);
					} else {
						TextView TargetView = (TextView) v.getTag(R.id.target_view);
						int ivtweight = 0;
						try {
							ivtweight = Integer.parseInt(newValue) * Integer.parseInt(mBeanList.get(mFocusPos).getRate());
						} catch (NumberFormatException e) {
							e.printStackTrace();
						}
						TargetView.setText(ivtweight + "");
						mBeanList.get(mFocusPos).setIvtNum(newValue);
						mBeanList.get(mFocusPos).setIvtWeight(ivtweight + "");
					}
					mFocusView = null;
				}
			}
				break;

			case R.id.et_ivt_detail_rate: {// 换算率
				mFocusNum = 6;
				if (!hasFocus) {// 失去焦点时，mFocusPos为失去焦点的view的position
					String oldValue = mBeanList.get(mFocusPos).getRate();
					String newValue = ((EditText) v).getText().toString();
					try {
						newValue = Integer.parseInt(newValue) + "";
					} catch (NumberFormatException e) {
						((EditText) v).setText(oldValue);
						return;
					}

					if (newValue.equals(oldValue)) {
						((EditText) v).setText(oldValue);
					} else {
						TextView TargetView = (TextView) v.getTag(R.id.target_view);
						int ivtweight = 0;
						try {
							ivtweight = Integer.parseInt(newValue) * Integer.parseInt(mBeanList.get(mFocusPos).getIvtNum());
						} catch (NumberFormatException e) {
							e.printStackTrace();
						}
						TargetView.setText(ivtweight + "");
						mBeanList.get(mFocusPos).setRate(newValue);
						mBeanList.get(mFocusPos).setIvtWeight(ivtweight + "");
					}
					mFocusView = null;
				}
			}
				break;

			case R.id.et_ipt_detail_factnum: {
				if (!hasFocus) {// 失去焦点时，mFocusPos为失去焦点的view的position
					String oldValue = mBeanList.get(mFocusPos).getFactNum();
					String newValue = ((EditText) v).getText().toString();
					try {
						newValue = Integer.parseInt(newValue) + "";
					} catch (NumberFormatException e) {
						((EditText) v).setText(oldValue);
						return;
					}

					if (newValue.equals(oldValue)) {
						((EditText) v).setText(oldValue);
					} else {
						String factweight = countFactWeight(newValue, mFocusPos);
						TextView TargetView = (TextView) v.getTag(R.id.target_view);
						TargetView.setText(factweight);
						((EditText) v).setText(newValue);
						mBeanList.get(mFocusPos).setFactNum(newValue);
						mBeanList.get(mFocusPos).setFactWeight(factweight);
					}
					mFocusView = null;
				}
			}
				break;

			case R.id.et_opt_detail_factnum: {
				if (!hasFocus) {// 失去焦点时，mFocusPos为失去焦点的view的position
					String oldValue = mBeanList.get(mFocusPos).getFactNum();
					String newValue = ((EditText) v).getText().toString();
					try {
						newValue = Integer.parseInt(newValue) + "";
					} catch (NumberFormatException e) {
						((EditText) v).setText(oldValue);
						return;
					}

					if (newValue.equals(oldValue)) {
						((EditText) v).setText(oldValue);
					} else {
						String factweight = countFactWeight(newValue, mFocusPos);
						TextView TargetView = (TextView) v.getTag(R.id.target_view);
						TargetView.setText(factweight);
						((EditText) v).setText(newValue);
						mBeanList.get(mFocusPos).setFactNum(newValue);
						mBeanList.get(mFocusPos).setFactWeight(factweight);
					}
					mFocusView = null;
				}
			}
				break;

			case R.id.et_fmg_detail_concentration6: {
				mFocusNum = 1;
				if (!hasFocus) {// 失去焦点时，mFocusPos为失去焦点的view的position
					String oldValue = mBeanList.get(mFocusPos).getConcentration6();
					String Concentration = ((EditText) v).getText().toString();
					if (Concentration == "" || Concentration.equals(oldValue)) {
						((EditText) v).setText(oldValue);
						return;
					}
					mBeanList.get(mFocusPos).setConcentration6(Concentration);
					((EditText) v).setText(Concentration);
					mFocusView = null;
				}
			}
			break;

			case R.id.et_fmg_detail_concentration12: {
				mFocusNum = 2;
				if (!hasFocus) {// 失去焦点时，mFocusPos为失去焦点的view的position
					String oldValue = mBeanList.get(mFocusPos).getConcentration12();
					String Concentration = ((EditText) v).getText().toString();
					if (Concentration == "" || Concentration.equals(oldValue)) {
						((EditText) v).setText(oldValue);
						return;
					}
					mBeanList.get(mFocusPos).setConcentration12(Concentration);
					((EditText) v).setText(Concentration);
					mFocusView = null;
				}
			}
			break;

			case R.id.et_fmg_detail_concentration24: {
				mFocusNum = 3;
				if (!hasFocus) {// 失去焦点时，mFocusPos为失去焦点的view的position
					String oldValue = mBeanList.get(mFocusPos).getConcentration24();
					String Concentration = ((EditText) v).getText().toString();
					if (Concentration == "" || Concentration.equals(oldValue)) {
						((EditText) v).setText(oldValue);
						return;
					}
					mBeanList.get(mFocusPos).setConcentration24(Concentration);
					((EditText) v).setText(Concentration);
					mFocusView = null;
				}
			}
			break;

			case R.id.et_fmg_detail_concentration48: {
				mFocusNum = 4;
				if (!hasFocus) {// 失去焦点时，mFocusPos为失去焦点的view的position
					String oldValue = mBeanList.get(mFocusPos).getConcentration48();
					String Concentration = ((EditText) v).getText().toString();
					if (Concentration == "" || Concentration.equals(oldValue)) {
						((EditText) v).setText(oldValue);
						return;
					}
					mBeanList.get(mFocusPos).setConcentration48(Concentration);
					((EditText) v).setText(Concentration);
					mFocusView = null;
				}
			}
			break;

			case R.id.et_fmg_detail_concentration72: {
				mFocusNum = 5;
				if (!hasFocus) {// 失去焦点时，mFocusPos为失去焦点的view的position
					String oldValue = mBeanList.get(mFocusPos).getConcentration72();
					String Concentration = ((EditText) v).getText().toString();
					if (Concentration == "" || Concentration.equals(oldValue)) {
						((EditText) v).setText(oldValue);
						return;
					}
					mBeanList.get(mFocusPos).setConcentration72(Concentration);
					((EditText) v).setText(Concentration);
					mFocusView = null;
				}
			}
			break;

			case R.id.et_fmg_detail_concentration96: {
				mFocusNum = 6;
				if (!hasFocus) {// 失去焦点时，mFocusPos为失去焦点的view的position
					String oldValue = mBeanList.get(mFocusPos).getConcentration96();
					String Concentration = ((EditText) v).getText().toString();
					if (Concentration == "" || Concentration.equals(oldValue)) {
						((EditText) v).setText(oldValue);
						return;
					}
					mBeanList.get(mFocusPos).setConcentration96(Concentration);
					((EditText) v).setText(Concentration);
					mFocusView = null;
				}
			}
			break;

			case R.id.et_fmg_detail_concentration144: {
				mFocusNum = 7;
				if (!hasFocus) {// 失去焦点时，mFocusPos为失去焦点的view的position
					String oldValue = mBeanList.get(mFocusPos).getConcentration144();
					String Concentration = ((EditText) v).getText().toString();
					if (Concentration == "" || Concentration.equals(oldValue)) {
						((EditText) v).setText(oldValue);
						return;
					}
					mBeanList.get(mFocusPos).setConcentration144(Concentration);
					((EditText) v).setText(Concentration);
					mFocusView = null;
				}
			}
			break;

			case R.id.et_fmg_detail_concentration216: {
				mFocusNum = 8;
				if (!hasFocus) {// 失去焦点时，mFocusPos为失去焦点的view的position
					String oldValue = mBeanList.get(mFocusPos).getConcentration216();
					String Concentration = ((EditText) v).getText().toString();
					if (Concentration == "" || Concentration.equals(oldValue)) {
						((EditText) v).setText(oldValue);
						return;
					}
					mBeanList.get(mFocusPos).setConcentration216(Concentration);
					((EditText) v).setText(Concentration);
					mFocusView = null;
				}
			}
			break;

			case R.id.et_sur_third_barcode: {
				if (!hasFocus) {
					String oldValue = mBeanList.get(mFocusPos).getBarCode();
					String BarCode = ((EditText) v).getText().toString();
					if (BarCode == "" || BarCode.equals(oldValue)) {
						((EditText) v).setText(oldValue);
						return;
					}
					mBeanList.get(mFocusPos).setBarCode(BarCode);
					((EditText) v).setText(BarCode);
					mFocusView = null;
				}
			}
				break;

			default:
				break;
			}

		}
	};

	private OnCheckedChangeListener mCheckedChangeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton v, boolean isChecked) {
			switch (v.getId()) {
			case R.id.public_lock: {
				int mPos = (Integer) v.getTag();
				View convertView = (View) v.getTag(R.id.target_second);
				String LockState = isChecked ? "1" : "0";
				v.setText(LockState);
				mBeanList.get(mPos).setLockState(LockState);
				setLockBackground(convertView, isChecked);
				EditText TargetView = (EditText) v.getTag(R.id.target_view);
				TargetView.setEnabled(!isChecked);
			}
				break;

			default:
				break;
			}
		}
	};

	public class ViewHolder {

		public EditText mConcentration216;
		public EditText mConcentration144;
		public EditText mConcentration96;
		public EditText mConcentration72;
		public EditText mConcentration48;
		public EditText mConcentration24;
		public EditText mConcentration12;
		public EditText mConcentration6;
		public TextView mLocation;
		public TextView mVbomName;
		public TextView mGoalWare;
		public TextView mInTime;
		public TextView mOutTime;
		public TextView mDoorID;
		public TextView mCarID;
		public EditText mRateET;
		public EditText mIvtNumET;
		public TextView mIvtWeight;
		public EditText mStockWeight;
		public EditText mStockNum;
		public EditText mGoodsIDET;
		public EditText mTrayIDET;
		public TextView mIvtWait;
		public TextView mIvtDone;
		public TextView mIvtNum;
		public TextView mTrayDone;
		public TextView mTrayTotle;
		public TextView mCheckNum;
		public TextView mTrayNum;
		public TextView mDetailNum;
		public TextView mNote;
		public TextView mUser;
		public TextView mDelete;

		public TextView mTaskID;
		public TextView mType;
		public TextView mPcNum;
		public TextView mPlanNum;
		public TextView mNCState;
		public TextView mCarNum;

		public TextView mGoodsID;
		public TextView mGoodsName;
		public TextView mSpec;
		public TextView mPlanWeight;
		public TextView mFactWeight;
		public TextView mRate;

		public TextView mDate;
		public TextView mMeridiem;
		public TextView mOrderID;
		public TextView mBuilding;
		public TextView mPerson;

		public TextView mFloor;
		public TextView mPoint;
		public TextView mResult;
		public TextView mPicture;
		public TextView mRepairDate;

		public TextView mRoom;
		public TextView mMethod;
		public TextView mDosingDate;
		public TextView mProgress;

		public TextView mTrayID;
		public TextView mTrayName;

		public TextView mColligate;
		public TextView mAppearance;
		public TextView mColor;
		public TextView mWaterFell;
		public TextView mMould;
		public TextView mOilTrace;
		public TextView mMothy;
		public TextView mOdor;
		public TextView mImpurity;
		public TextView mPosition;

		public TextView mCheckLine;
		public TextView mAssignState;

		public TextView mBarCode;
		public TextView mRFIDCode;
		public TextView mWeight;
		public TextView mWater;
		public TextView mWaterDev;
		public TextView mDensityDev;

		public TextView mTextView;

		public CheckBox mLock;
		public CheckBox mCheckBox;

		public EditText mLossWeight;
		public EditText mFactNum;
		public EditText mBarCodeET;
		public EditText mTrouble;
		public EditText mSolution;
		public EditText mFloorET;
		public EditText mCardNum;
		public EditText mSerricorneNum;
		public EditText mElutellaNum;
		public EditText mDateFirst;
		public EditText mCCTTFirst;
		public EditText mDateSecond;
		public EditText mCCTTSecond;
		public EditText mEditText;
		public EditText mEditText1;
		public EditText mEditText2;
	}

	// private Filter _filter;
	//
	// @Override
	// public Filter getFilter() {
	// if (_filter == null) {
	// _filter = new SearchFilter();
	// }
	// return _filter;
	// }
	// 内部类：数据过滤器
	// class SearchFilter extends Filter {
	//
	// @Override
	// protected FilterResults performFiltering(CharSequence constraint) {
	// // 定义过滤规则
	// FilterResults filterResults = new FilterResults();
	//
	// if (mOldBeanList == null) {// 保存原始数据
	// mOldBeanList = new ArrayList<AdapterBean>(mBeanList);
	// } else {// 更新原始数据
	//
	// }
	//
	// // 如果搜索框内容为空，就恢复原始数据
	// if (TextUtils.isEmpty(constraint)) {
	// filterResults.values = mOldBeanList;
	// filterResults.count = mOldBeanList.size();
	// } else {
	// // 否则过滤出新数据
	// String filterString = constraint.toString().trim();// 过滤首尾空白，小写过滤
	// ArrayList<AdapterBean> newValues = new ArrayList<AdapterBean>();
	//
	// for (AdapterBean vo : mOldBeanList) {
	// if (vo.getFloor().contains(filterString)) {
	// newValues.add(vo);
	// }
	// filterResults.values = newValues;
	// filterResults.count = newValues.size();
	// }
	// }
	// return filterResults;
	// }
	//
	// @Override
	// protected void publishResults(CharSequence constraint, FilterResults
	// results) {
	// mBeanList = (ArrayList<AdapterBean>) results.values;// 更新适配器的数据
	// if (results.count >= 0) {
	// notifyDataSetChanged();// 通知数据发生了改变
	// } else {
	// notifyDataSetInvalidated();// 通知数据失效
	// }
	// }
	//
	// }
}
