package com.nti56.xmisa.fragment;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.List;

import cn.trinea.android.common.util.ShellUtils;

import com.nti56.xmisa.MainActivity;
import com.nti56.xmisa.R;
import com.reader.base.ERROR;
import com.reader.base.ReaderBase;
import com.reader.helper.InventoryBuffer;
import com.reader.helper.ReaderHelper;
import com.reader.helper.ReaderSetting;
import com.reader.helper.InventoryBuffer.InventoryTagMap;
import com.vanch.uhf.serialport.SerialPort;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class RFIDFragment extends Fragment implements OnClickListener {

	private Button BtnRFID_Start, BtnRFID_Stop;
	protected WeakReference<View> mRootView;
	private ReaderHelper mReaderHelper;
	private LocalBroadcastManager lbm;
	private MainActivity mContext;
	private ReaderBase mReader;
	private TextView mResult;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (mRootView == null || mRootView.get() == null) {
			View v = inflater.inflate(R.layout.fragment_rfid, container, false);
			mRootView = new WeakReference<View>(v);
		} else {
			ViewGroup parent = (ViewGroup) mRootView.get().getParent();
			if (parent != null) {
				parent.removeView(mRootView.get());
			}
		}
		View view = mRootView.get();

		mResult = (TextView) view.findViewById(R.id.tv_rfid_result);
		
		BtnRFID_Start = (Button) view.findViewById(R.id.btn_rfid_start);
		BtnRFID_Stop = (Button) view.findViewById(R.id.btn_rfid_stop);
		BtnRFID_Start.setOnClickListener(this);
		BtnRFID_Stop.setOnClickListener(this);
		return view;
	}

	private final BroadcastReceiver mRecv = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ReaderHelper.BROADCAST_WRITE_LOG)) {
				// mLogList.writeLog((String) intent.getStringExtra("log"),
				// intent.getIntExtra("type", ERROR.SUCCESS));
			} else if (intent.getAction().equals(ReaderHelper.BROADCAST_WRITE_DATA)) {
				// mMonitor.writeMonitor((String) intent.getStringExtra("log"),
				// intent.getIntExtra("type", ERROR.SUCCESS));
			} else if (intent.getAction().equals(ReaderHelper.BROADCAST_ON_LOST_CONNECT)) {
				// mLogList.writeLog("串口异常断开", ERROR.FAIL);
			} else if (intent.getAction().equals(ReaderHelper.BROADCAST_REFRESH_FAST_SWITCH)) {

			} else if (intent.getAction().equals(ReaderHelper.BROADCAST_REFRESH_INVENTORY)) {

			} else if (intent.getAction().equals(ReaderHelper.BROADCAST_REFRESH_INVENTORY_REAL)) {

			} else if (intent.getAction().equals(ReaderHelper.BROADCAST_REFRESH_ISO18000_6B)) {

			} else if (intent.getAction().equals(ReaderHelper.BROADCAST_REFRESH_OPERATE_TAG)) {

			} else if (intent.getAction().equals(ReaderHelper.BROADCAST_REFRESH_READER_SETTING)) {

			}
		}
	};

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		Log.e("NTI", "onDetach()...........RFID");
		super.onDetach();
		try {
			Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
			childFragmentManager.setAccessible(true);
			childFragmentManager.set(this, null);

		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onDestroy() {
		mContext = null;
		super.onDestroy();
	}


	/* (non-Javadoc)
	 * @see android.app.Fragment#onStop()
	 */
	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		mContext = (MainActivity) getActivity();
		// init_RFID_Test();
		super.onCreate(savedInstanceState);
	}

	private void init_RFID_Test() {
		try {
			String commnandList = "echo 1 >/sys/devices/platform/gpio_test/uart1power";
			ShellUtils.execCommand(commnandList, true);
			ReaderHelper.setContext(mContext);
			SerialPort mSerialPort = new SerialPort(new File("/dev/ttyMT0"), 115200, 0);
			mReaderHelper = ReaderHelper.getDefaultHelper();
			mReaderHelper.setReader(mSerialPort.getInputStream(), mSerialPort.getOutputStream());
			mReader = mReaderHelper.getReader();
		} catch (SecurityException e) {
			Log.e("LXM", "该串口号没有读写权限");
		} catch (Exception e) {
			Log.e("LXM", "打开失败，未知错误");
		}

		m_curReaderSetting = mReaderHelper.getCurReaderSetting();
		m_curInventoryBuffer = mReaderHelper.getCurInventoryBuffer();

		lbm = LocalBroadcastManager.getInstance(mContext);

		IntentFilter itent = new IntentFilter();
		// itent.addAction(ReaderHelper.BROADCAST_REFRESH_FAST_SWITCH);
		// itent.addAction(ReaderHelper.BROADCAST_REFRESH_INVENTORY);
		// itent.addAction(ReaderHelper.BROADCAST_REFRESH_INVENTORY_REAL);
		// itent.addAction(ReaderHelper.BROADCAST_REFRESH_ISO18000_6B);
		// itent.addAction(ReaderHelper.BROADCAST_REFRESH_OPERATE_TAG);
		// itent.addAction(ReaderHelper.BROADCAST_REFRESH_READER_SETTING);
		itent.addAction(ReaderHelper.BROADCAST_WRITE_LOG);
		itent.addAction(ReaderHelper.BROADCAST_WRITE_DATA);
		itent.addAction(ReaderHelper.BROADCAST_ON_LOST_CONNECT);

		lbm.registerReceiver(mRecv, itent);
	}

	private static InventoryBuffer m_curInventoryBuffer;
	private static ReaderSetting m_curReaderSetting;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_rfid_start:
			mContext.startRFID(this);
			mResult.setText("正在扫描中。。。");
			// RFID_Start();
			break;

		case R.id.btn_rfid_stop:
			stopRFID();
			// RFID_Stop();
			break;

		default:
			break;
		}
	}

	private void RFID_Stop() {

		// refreshText();
		mReaderHelper.setInventoryFlag(false);
		m_curInventoryBuffer.bLoopInventoryReal = false;

		BtnRFID_Stop.setEnabled(false);
		BtnRFID_Start.setEnabled(true);

		mLoopHandler.removeCallbacks(mLoopRunnable);
		mHandler.removeCallbacks(mRefreshRunnable);
		// refreshList();

	}

	private void RFID_Start() {

		m_curInventoryBuffer.clearInventoryPar();
		m_curInventoryBuffer.lAntenna.add((byte) 0x00);
		m_curInventoryBuffer.lAntenna.add((byte) 0x01);
		m_curInventoryBuffer.lAntenna.add((byte) 0x02);
		m_curInventoryBuffer.lAntenna.add((byte) 0x03);

		if (m_curInventoryBuffer.lAntenna.size() <= 0) {
			// 至少选中一个天线
			return;
		}
		m_curInventoryBuffer.bLoopInventoryReal = true;
		m_curInventoryBuffer.btRepeat = 0;

		m_curInventoryBuffer.btRepeat = 1;

		if ((m_curInventoryBuffer.btRepeat & 0xFF) <= 0) {
			// 重复次数至少为1
			return;
		}
		/*
		 * 自定义Session参数 m_curInventoryBuffer.bLoopCustomizedSession = true;
		 * m_curInventoryBuffer.btSession = (byte) (mPos1 & 0xFF); //0-3
		 * m_curInventoryBuffer.btTarget = (byte) (mPos2 & 0xFF); //0-1
		 */
		m_curInventoryBuffer.bLoopCustomizedSession = false;

		m_curInventoryBuffer.clearInventoryRealResult();
		mReaderHelper.setInventoryFlag(true);

		mReaderHelper.clearInventoryTotal();

		// refreshText();

		byte btWorkAntenna = m_curInventoryBuffer.lAntenna.get(m_curInventoryBuffer.nIndexAntenna);
		if (btWorkAntenna < 0)
			btWorkAntenna = 0;

		if (mReader != null) {
			Log.e("LXM", "RFID_Start().......mReader != null");
		}
		if (m_curReaderSetting == null) {
			Log.e("LXM", "RFID_Start().......m_curReaderSetting == null");
		}
		Log.e("LXM", "RFID_Start().......m_curReaderSetting.btReadId = " + m_curReaderSetting.btReadId);
		Log.e("LXM", "RFID_Start().......btWorkAntenna = " + btWorkAntenna);
		// if(m_curReaderSetting.btReadId == null){
		// Log.e("LXM",
		// "RFID_Start().......m_curReaderSetting.btReadId == null");
		// }
		// if(btWorkAntenna == null){
		// Log.e("LXM", "RFID_Start().......mReader == null");
		// }
		mReader.setWorkAntenna(m_curReaderSetting.btReadId, btWorkAntenna);

		m_curReaderSetting.btWorkAntenna = btWorkAntenna;

		BtnRFID_Stop.setEnabled(true);
		BtnRFID_Start.setEnabled(false);

		mLoopHandler.removeCallbacks(mLoopRunnable);
		mLoopHandler.postDelayed(mLoopRunnable, 2000);
		mHandler.removeCallbacks(mRefreshRunnable);
		mHandler.postDelayed(mRefreshRunnable, 2000);

	}

	private Handler mLoopHandler = new Handler();
	private Runnable mLoopRunnable = new Runnable() {
		public void run() {

			if (m_curInventoryBuffer.bLoopInventoryReal) {

				byte btWorkAntenna = m_curInventoryBuffer.lAntenna.get(m_curInventoryBuffer.nIndexAntenna);
				if (btWorkAntenna < 0)
					btWorkAntenna = 0;

				mReader.setWorkAntenna(m_curReaderSetting.btReadId, btWorkAntenna);
				mLoopHandler.postDelayed(this, 2000);
			}
		}
	};

	private Handler mHandler = new Handler();
	private Runnable mRefreshRunnable = new Runnable() {
		public void run() {
			if (m_curInventoryBuffer.bLoopInventoryReal) {
				// refreshList();

				List<InventoryTagMap> listMap = m_curInventoryBuffer.lsTagList;
				for (int i = 0; i < listMap.size(); i++) {
					InventoryTagMap map = listMap.get(i);
					Log.e("LXM", "position : " + i + " , EPC : " + HexASCII_To_String(map.strEPC));
					RFID_Stop();
					return;
				}

				// listItemView.mIdText.setText(String.valueOf(position));
				// String mEPC = HexASCII_To_String(map.strEPC);
				// listItemView.mEpcText.setText(mEPC);
				// listItemView.mPcText.setText(map.strPC);
				// listItemView.mTimesText.setText(String.valueOf(map.nReadCount));
				// try {
				// listItemView.mRssiText.setText((Integer.parseInt(map.strRSSI)
				// - 129) + "dBm");
				// } catch (Exception e) {
				// listItemView.mRssiText.setText("");
				// }
				// listItemView.mFreqText.setText(map.strFreq);

				mHandler.postDelayed(this, 2000);
			}
		}
	};

	private String HexASCII_To_String(String strEPC) {
		String[] EPC = strEPC.trim().split(" ");
		int temp;
		char c;
		String mEPC = "";
		for (int i = 0; i < EPC.length; i++) {
			temp = Integer.parseInt(EPC[i], 16);
			if (temp > 32 && temp < 127) {
				c = (char) temp;
				mEPC += c;
			} else {
				mEPC += "0";
			}
		}
		return mEPC;
	}

	public void setRFIDResult(String result) {
		Log.e("LXM", "RFIDFragment-------setRFIDResult()  result = " + result);
		String value = mResult.getText().toString();
		if (!value.contains(result)) {
			value = value.replace("正在扫描中。。。", result + ";\n正在扫描中。。。");
			mResult.setText(value);
		}
	}
	
	private void stopRFID() {
		mContext.stopRFID();
		String value = mResult.getText().toString();
		value = value.replace("正在扫描中。。。", "扫描结束！");
		mResult.setText(value);
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		if(hidden){
			stopRFID();
		}
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
	}
	
	
}
