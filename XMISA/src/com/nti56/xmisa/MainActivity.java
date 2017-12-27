package com.nti56.xmisa;

import java.io.File;
import java.util.List;

import cn.trinea.android.common.util.ShellUtils;

import com.nti56.xmisa.adapter.MySQLiteHelper;
import com.nti56.xmisa.dialog.RemindDialog;
import com.nti56.xmisa.dialog.RemindDialog.mRemindDialogListener;
import com.nti56.xmisa.fragment.CheckFragment;
import com.nti56.xmisa.fragment.MaintainFragment;
import com.nti56.xmisa.fragment.RFIDFragment;
import com.nti56.xmisa.fragment.SettingFragment;
import com.nti56.xmisa.fragment.StoreFragment;
import com.nti56.xmisa.fragment.check.AssignFragment;
import com.nti56.xmisa.fragment.check.ExamineFragment;
import com.nti56.xmisa.fragment.maintain.FumigateFragment;
import com.nti56.xmisa.fragment.maintain.PatrolFragment;
import com.nti56.xmisa.fragment.maintain.PestsFragment;
import com.nti56.xmisa.fragment.maintain.SurveyFragment;
import com.nti56.xmisa.fragment.store.InPutFragment;
import com.nti56.xmisa.fragment.store.InventoryFragment;
import com.nti56.xmisa.fragment.store.OutPutFragment;
import com.nti56.xmisa.fragment.store.ReceiveFragment;
import com.nti56.xmisa.util.Content;
import com.reader.base.ReaderBase;
import com.reader.helper.InventoryBuffer;
import com.reader.helper.ReaderHelper;
import com.reader.helper.ReaderSetting;
import com.reader.helper.InventoryBuffer.InventoryTagMap;
import com.vanch.uhf.serialport.SerialPort;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {

	private Button btnStore, btnCheck, btnMaintain, btnRFID, btnSetting, lastButton;
	private Fragment storeFragment, checkFragment, maintainFragment, rfidFragment, settingFragment;
	private FragmentManager fgManager;
	private Fragment mCurFg;

	// private int backCount, homeCount;
	private RemindDialog exitDialog;
	private int BackMode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.e("NTI", "MainActivity.........onCreate()");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		fgManager = getFragmentManager();
		MySQLiteHelper.setSQLiteHelper(this, Content.DB_NAME, Content.DB_VERSION);
		init();
		// this.getWindow().setFlags(Content.FLAG_HOMEKEY_DISPATCHED,
		// Content.FLAG_HOMEKEY_DISPATCHED);
	}

	@Override
	public void onClick(View v) {
		if (Content.InputSoftOpen) {
			return;
		}
		switch (v.getId()) {
		case R.id.button_store:
			if (lastButton != btnStore) {
				lastButton.setSelected(false);
				lastButton = btnStore;
				btnStore.setSelected(true);
				switchFrament(mCurFg, storeFragment);
			}
			break;
		case R.id.button_check:
			if (lastButton != btnCheck) {
				lastButton.setSelected(false);
				lastButton = btnCheck;
				btnCheck.setSelected(true);
				switchFrament(mCurFg, checkFragment);
			}
			 break;
		case R.id.button_maintain:
			if (lastButton != btnMaintain) {
				lastButton.setSelected(false);
				lastButton = btnMaintain;
				btnMaintain.setSelected(true);
				switchFrament(mCurFg, maintainFragment);
			}
			break;
		case R.id.button_rfid:
			if (lastButton != btnRFID) {
				lastButton.setSelected(false);
				lastButton = btnRFID;
				btnRFID.setSelected(true);
				switchFrament(mCurFg, rfidFragment);
			}
			break;
		case R.id.button_setting:
			if (lastButton != btnSetting) {
				lastButton.setSelected(false);
				lastButton = btnSetting;
				btnSetting.setSelected(true);
				switchFrament(mCurFg, settingFragment);
			}
			break;
		default:
			return;

		}
		hideInputSoft(v);
	}

	private void hideInputSoft(View v) {
		// 隐藏输入法窗口
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}

	public void setMyBackModel(int backModel) {
		this.BackMode = backModel;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		// case KeyEvent.KEYCODE_HOME:
		// BackMode = KeyEvent.KEYCODE_HOME;
		// ShowExitDialog();
		// return true;

		case KeyEvent.KEYCODE_BACK:
			BackPressed();
			return true;

		default:
			return super.onKeyDown(keyCode, event);
		}
	}

	private void BackPressed() {
		switch (BackMode) {
		case Content.BACK_MODEL_REC_DETAIL:
			ReceiveFragment.onBackPressed();
			BackMode = 0;
			break;
		case Content.BACK_MODEL_IPT_DETAIL:
			InPutFragment.onBackPressed();
			BackMode = 0;
			break;
		case Content.BACK_MODEL_IPT_THIRD:
			InPutFragment.onBackPressed();
			BackMode = Content.BACK_MODEL_IPT_DETAIL;
			break;
		case Content.BACK_MODEL_OPT_DETAIL:
			OutPutFragment.onBackPressed();
			BackMode = 0;
			break;
		case Content.BACK_MODEL_OPT_THIRD:
			OutPutFragment.onBackPressed();
			BackMode = Content.BACK_MODEL_OPT_DETAIL;
			break;
		case Content.BACK_MODEL_IVT_DETAIL:
			InventoryFragment.onBackPressed();
			BackMode = 0;
			break;
		case Content.BACK_MODEL_ASG_DETAIL:
			AssignFragment.onBackPressed();
			BackMode = 0;
			break;
		case Content.BACK_MODEL_EXM_DETAIL:
			ExamineFragment.onBackPressed();
			BackMode = 0;
			break;
		case Content.BACK_MODEL_SUR_DETAIL:
			SurveyFragment.onBackPressed();
			BackMode = 0;
			break;
		case Content.BACK_MODEL_SUR_THIRD:
			SurveyFragment.onBackPressed();
			BackMode = Content.BACK_MODEL_SUR_DETAIL;
			break;
		case Content.BACK_MODEL_FMG_DETAIL:
			FumigateFragment.onBackPressed();
			BackMode = 0;
			break;
		case Content.BACK_MODEL_PTS_DETAIL:
			PestsFragment.onBackPressed();
			BackMode = 0;
			break;
		case Content.BACK_MODEL_PTL_DETAIL:
			PatrolFragment.onBackPressed();
			BackMode = 0;
			break;
		default:
			ShowExitDialog();
			break;
		}
	}

	private mRemindDialogListener mExitCallBack = new mRemindDialogListener() {
		@Override
		public void btnSure() {
			HideExitDialog();
			finish();
		}

		// @Override
		// public void btnCancel() {
		// HideExitDialog();
		// }
	};

	private void InitExitDialog() {
		exitDialog = new RemindDialog(this, R.style.dialog, Content.MODEL_EXIT, mExitCallBack);
		Window dialogWindow = exitDialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		// dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);
		// lp.x = 0;// 设置x坐标
		// lp.y = 0;// 设置y坐标
		// lp.alpha = 1f; // 透明度
		dialogWindow.setAttributes(lp);
		exitDialog.setCanceledOnTouchOutside(true);
	}

	private void ShowExitDialog() {
		if (exitDialog == null) {
			InitExitDialog();
		}
		exitDialog.show();
	}

	private void HideExitDialog() {
		if (exitDialog != null) {
			exitDialog.dismiss();
		}
	}

	public void init() {
		btnStore = (Button) findViewById(R.id.button_store);
		btnCheck = (Button) findViewById(R.id.button_check);
		btnMaintain = (Button) findViewById(R.id.button_maintain);
		btnRFID = (Button) findViewById(R.id.button_rfid);
		btnSetting = (Button) findViewById(R.id.button_setting);

		btnStore.setOnClickListener(this);
		btnCheck.setOnClickListener(this);
		btnMaintain.setOnClickListener(this);
		btnRFID.setOnClickListener(this);
		btnSetting.setOnClickListener(this);

		storeFragment = new StoreFragment();
		checkFragment = new CheckFragment();
		maintainFragment = new MaintainFragment();
		rfidFragment = new RFIDFragment();
		settingFragment = new SettingFragment();

		switchFrament(maintainFragment, maintainFragment);

		btnMaintain.setSelected(true);
		lastButton = btnMaintain;
		// switchFrament(storeFragment, storeFragment);
		//
		// btnStore.setSelected(true);
		// lastButton = btnStore;
	}

	public void switchFrament(Fragment from, Fragment to) {
		if (mCurFg != to) {
			mCurFg = to;
			FragmentTransaction transaction = fgManager.beginTransaction();
			transaction.hide(from);
			if (!to.isAdded()) {
				transaction.add(R.id.fragmentRoot, to);
			}
			transaction.show(to).commit();
		}
	}

	private ReaderHelper mReaderHelper;
	private ReaderBase mReader;
	private ReaderSetting m_curReaderSetting;
	private InventoryBuffer m_curInventoryBuffer;
	private LocalBroadcastManager lbm;

	private void init_RFID_Device() {
		try {
			if (!GPIO_PowerON) {
				String commnandList = "echo 1 >/sys/devices/platform/gpio_test/uart1power";
				ShellUtils.execCommand(commnandList, true);
				GPIO_PowerON = true;
			}
			ReaderHelper.setContext(this);
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

		lbm = LocalBroadcastManager.getInstance(this);

		IntentFilter itent = new IntentFilter();

		// itent.addAction(ReaderHelper.BROADCAST_REFRESH_INVENTORY_REAL);
		// itent.addAction(ReaderHelper.BROADCAST_REFRESH_READER_SETTING);
		// itent.addAction(ReaderHelper.BROADCAST_REFRESH_FAST_SWITCH);
		// itent.addAction(ReaderHelper.BROADCAST_REFRESH_OPERATE_TAG);
		// itent.addAction(ReaderHelper.BROADCAST_REFRESH_ISO18000_6B);
		// itent.addAction(ReaderHelper.BROADCAST_REFRESH_INVENTORY);
		itent.addAction(ReaderHelper.BROADCAST_WRITE_LOG);
		itent.addAction(ReaderHelper.BROADCAST_WRITE_DATA);
		itent.addAction(ReaderHelper.BROADCAST_ON_LOST_CONNECT);

		lbm.registerReceiver(mRecv, itent);
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

	private void RFID_Stop() {
		Log.e("LXM", "MainActivity.........scanRFID....Stop");
		Log.e("NTI", "MainActivity.........scanRFID....Stop");
		// refreshText();
		if(mReaderHelper != null){
			mReaderHelper.setInventoryFlag(false);
			m_curInventoryBuffer.bLoopInventoryReal = false;
			// mLoopHandler.removeCallbacks(mLoopRunnable);
			mHandler.removeCallbacks(mRefreshRunnable);
		}
		// refreshList();

	}

	private void RFID_Start() {
		Log.e("LXM", "MainActivity.........scanRFID....Start");
		m_curInventoryBuffer.clearInventoryPar();
		m_curInventoryBuffer.lAntenna.add((byte) 0x00);
		m_curInventoryBuffer.lAntenna.add((byte) 0x01);
		m_curInventoryBuffer.lAntenna.add((byte) 0x02);
		m_curInventoryBuffer.lAntenna.add((byte) 0x03);

		if (m_curInventoryBuffer.lAntenna.size() <= 0) {
			// 至少选中一个天线
			sendRFIDResult("扫描出错", false);
			return;
		}
		m_curInventoryBuffer.btRepeat = 0;

		m_curInventoryBuffer.btRepeat = 1;

		if ((m_curInventoryBuffer.btRepeat & 0xFF) <= 0) {
			// 重复次数至少为1
			sendRFIDResult("扫描出错", false);
			return;
		}
		m_curInventoryBuffer.bLoopInventoryReal = true;
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

		mReader.setWorkAntenna(m_curReaderSetting.btReadId, btWorkAntenna);

		m_curReaderSetting.btWorkAntenna = btWorkAntenna;
		count = 0;
		// mLoopHandler.removeCallbacks(mLoopRunnable);
		// mLoopHandler.postDelayed(mLoopRunnable, 2000);
		mHandler.removeCallbacks(mRefreshRunnable);
		mHandler.postDelayed(mRefreshRunnable, 2000);

	}

	// private Handler mLoopHandler = new Handler();
	// private Runnable mLoopRunnable = new Runnable() {
	// public void run() {
	//
	// if (m_curInventoryBuffer.bLoopInventoryReal) {
	//
	// byte btWorkAntenna =
	// m_curInventoryBuffer.lAntenna.get(m_curInventoryBuffer.nIndexAntenna);
	// if (btWorkAntenna < 0)
	// btWorkAntenna = 0;
	//
	// mReader.setWorkAntenna(m_curReaderSetting.btReadId, btWorkAntenna);
	// mLoopHandler.postDelayed(this, 2000);
	// }
	// }
	// };
	int count = 0;
	private Handler mHandler = new Handler();
	private Runnable mRefreshRunnable = new Runnable() {
		public void run() {
			if (m_curInventoryBuffer.bLoopInventoryReal) {
				List<InventoryTagMap> listMap = m_curInventoryBuffer.lsTagList;
				for (int i = 0; i < listMap.size(); i++) {
					InventoryTagMap map = listMap.get(i);
					String result = HexASCII_To_String(map.strEPC);
					Log.e("LXM", "position : " + i + " , EPC : " + HexASCII_To_String(map.strEPC));
					if (mResultStart == null) {
						sendRFIDResult(HexASCII_To_String(map.strEPC), true);
					} else if (result.trim().substring(0, 2).equalsIgnoreCase(mResultStart)) {
						sendRFIDResult(HexASCII_To_String(map.strEPC), true);
						RFID_Stop();
						return;
					}
				}
				if (mResultStart != null && count > 9) {
					sendRFIDResult("未扫描到结果", false);
					RFID_Stop();
					count = 0;
				} else {
					mHandler.postDelayed(this, 1000);
					count++;
				}
			}
		}

	};
	private boolean GPIO_PowerON = false;

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
				mEPC += " ";
			}
		}
		return mEPC;
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		Log.e("NTI", "MainActivity.........onStart()");
		init_RFID_Device();
		MySQLiteHelper.initSQLiteDatabase();
		super.onStart();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		Log.e("NTI", "MainActivity.........onRestart()");
		super.onRestart();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		Log.e("NTI", "MainActivity.........onResume()");
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		Log.e("NTI", "MainActivity.........onPause()");
		super.onPause();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		Log.e("NTI", "MainActivity.........onStop()");
		if (m_curInventoryBuffer != null && m_curInventoryBuffer.bLoopInventoryReal) {
			RFID_Stop();
		}
		String commnandList = "echo 0 >/sys/devices/platform/gpio_test/uart1power";
		ShellUtils.execCommand(commnandList, true);
		GPIO_PowerON = false;
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Log.e("NTI", "MainActivity.........onDestroy()");
		MySQLiteHelper.closeSQLiteDatabase();
		targetFragment = null;
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Log.e("NTI", "MainActivity.........onBackPressed()");
		finish();
		super.onBackPressed();
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		Log.e("NTI", "MainActivity.........finish()");
		super.finish();
	}

	private Fragment targetFragment = null;
	private String mResultStart;

	public void scanRFID(Fragment mFragment, String ResultStart) {
		Log.e("NTI", "MainActivity.........scanRFID....");
		if (!m_curInventoryBuffer.bLoopInventoryReal) {
			targetFragment = mFragment;
			mResultStart = ResultStart;
			RFID_Start();
		}
	}

	private void sendRFIDResult(String result, boolean Right) {
		if (targetFragment instanceof PatrolFragment) {
			((PatrolFragment) targetFragment).setRFIDResult(result, Right);
		}
		if (targetFragment instanceof PestsFragment) {
			((PestsFragment) targetFragment).setRFIDResult(result, Right);
		}
		if (targetFragment instanceof RFIDFragment) {
			((RFIDFragment) targetFragment).setRFIDResult(result);
			return;
		}
		targetFragment = null;
	}

	public void startRFID(RFIDFragment mFragment) {
		if (!m_curInventoryBuffer.bLoopInventoryReal) {
			targetFragment = mFragment;
			mResultStart = null;
			RFID_Start();
		}
	}

	public void stopRFID() {
		targetFragment = null;
		RFID_Stop();
	}

}
