package com.nti56.xmisa.fragment;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

import com.nti56.xmisa.R;
import com.nti56.xmisa.fragment.store.InPutFragment;
import com.nti56.xmisa.fragment.store.InPutFragment.mIptFragmentListener;
import com.nti56.xmisa.fragment.store.InventoryFragment;
import com.nti56.xmisa.fragment.store.InventoryFragment.mIvtFragmentListener;
import com.nti56.xmisa.fragment.store.OutPutFragment;
import com.nti56.xmisa.fragment.store.OutPutFragment.mOptFragmentListener;
import com.nti56.xmisa.fragment.store.ReceiveFragment;
import com.nti56.xmisa.fragment.store.ReceiveFragment.mRecFragmentListener;
import com.nti56.xmisa.util.Content;
import com.nti56.xmisa.util.MyLog;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;

public class StoreFragment extends Fragment implements OnClickListener {

	protected WeakReference<View> mRootView;
	private Button btnReceive, btnPutIn, btnPutOut, btnInventory, btnHide, lastButton;
	private ReceiveFragment recFragment;
	private InPutFragment iptFragment;
	private OutPutFragment optFragment;
	private InventoryFragment ivtFragment;
	private FragmentManager StoreFgManager = null;
	private Fragment mCurFg;
	private LinearLayout layoutMenu, layoutFg;
	private String TAG = "StoreFragment";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (mRootView == null || mRootView.get() == null) {
			View v = inflater.inflate(R.layout.fragment_store, container, false);
			mRootView = new WeakReference<View>(v);
		} else {
			ViewGroup parent = (ViewGroup) mRootView.get().getParent();
			if (parent != null) {
				parent.removeView(mRootView.get());
			}
		}
		View view = mRootView.get();
		init(view);

		return view;
	}

	@Override
	public void onDetach() {
		MyLog.d(TAG, "onDetach");
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

	private void init(View view) {
		if (StoreFgManager == null) {

			StoreFgManager = getChildFragmentManager();

			btnReceive = (Button) view.findViewById(R.id.btn_1_receive);
			btnPutIn = (Button) view.findViewById(R.id.btn_1_input);
			btnPutOut = (Button) view.findViewById(R.id.btn_1_output);
			btnInventory = (Button) view.findViewById(R.id.btn_1_inventory);
			btnHide = (Button) view.findViewById(R.id.btn_1_hide);

			layoutMenu = (LinearLayout) view.findViewById(R.id.layout_1_menu);
			layoutFg = (LinearLayout) view.findViewById(R.id.layout_1_fragmentRoot);

			btnReceive.setOnClickListener(this);
			btnPutIn.setOnClickListener(this);
			btnPutOut.setOnClickListener(this);
			btnInventory.setOnClickListener(this);
			btnHide.setOnClickListener(this);

			recFragment = new ReceiveFragment(mReceiveFgCallBack);
			iptFragment = new InPutFragment(mInPutFgCallBack);
			optFragment = new OutPutFragment(mOutPutFgCallBack);
			ivtFragment = new InventoryFragment(mInventoryFgCallBack);

			switchFrament(null, recFragment);

			btnReceive.setSelected(true);
			lastButton = btnReceive;
		}
	}

	public void switchFrament(Fragment from, Fragment to) {
		if (mCurFg != to) {
			mCurFg = to;
			FragmentTransaction transaction = StoreFgManager.beginTransaction();
			if (from != null) {
				transaction.hide(from);
			}
			if (!to.isAdded()) {
				transaction.add(R.id.layout_1_fragmentRoot, to);
			}
			transaction.show(to).commit();
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_1_hide) {
			if (layoutMenu.getVisibility() == View.GONE) {
				layoutMenu.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_menu_location));
				layoutFg.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_fg_location));
				layoutMenu.setVisibility(View.VISIBLE);
				LastState = View.VISIBLE;
			} else {
				layoutMenu.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_menu_up));
				layoutFg.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_fg_up));
				layoutMenu.setVisibility(View.GONE);
				LastState = View.GONE;
			}
			return;
		}
		if (Content.InputSoftOpen) {
			return;
		}
		switch (v.getId()) {
		case R.id.btn_1_receive:
			if (lastButton != btnReceive) {
				lastButton.setSelected(false);
				btnReceive.setSelected(true);
				lastButton = btnReceive;
				switchFrament(mCurFg, recFragment);
			}
			break;

		case R.id.btn_1_input:
			if (lastButton != btnPutIn) {
				lastButton.setSelected(false);
				btnPutIn.setSelected(true);
				lastButton = btnPutIn;
				switchFrament(mCurFg, iptFragment);
			}
			break;

		case R.id.btn_1_output:
			if (lastButton != btnPutOut) {
				lastButton.setSelected(false);
				btnPutOut.setSelected(true);
				lastButton = btnPutOut;
				switchFrament(mCurFg, optFragment);
			}
			break;

		case R.id.btn_1_inventory:
			if (lastButton != btnInventory) {
				lastButton.setSelected(false);
				btnInventory.setSelected(true);
				lastButton = btnInventory;
				switchFrament(mCurFg, ivtFragment);
			}
			break;

		default:
			break;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	// private boolean InputSoftOpen;
	private int LastState;

	private mIvtFragmentListener mInventoryFgCallBack = new mIvtFragmentListener() {

		@Override
		public void hideMenu(boolean InputSoftOpen) {
			if (InputSoftOpen) {
				btnHide.setVisibility(View.GONE);
				layoutMenu.setVisibility(View.GONE);
			} else {
				btnHide.setVisibility(View.VISIBLE);
				layoutMenu.setVisibility(LastState);
			}
		}

		@Override
		public void btnCancel() {
			// TODO Auto-generated method stub

		}
	};

	private mOptFragmentListener mOutPutFgCallBack = new mOptFragmentListener() {

		@Override
		public void hideMenu(boolean InputSoftOpen) {
			if (InputSoftOpen) {
				btnHide.setVisibility(View.GONE);
				layoutMenu.setVisibility(View.GONE);
			} else {
				btnHide.setVisibility(View.VISIBLE);
				layoutMenu.setVisibility(LastState);
			}
		}

		@Override
		public void btnCancel() {
			// TODO Auto-generated method stub

		}
	};

	private mIptFragmentListener mInPutFgCallBack = new mIptFragmentListener() {

		@Override
		public void hideMenu(boolean InputSoftOpen) {
			if (InputSoftOpen) {
				btnHide.setVisibility(View.GONE);
				layoutMenu.setVisibility(View.GONE);
			} else {
				btnHide.setVisibility(View.VISIBLE);
				layoutMenu.setVisibility(LastState);
			}
		}

		@Override
		public void btnCancel() {
			// TODO Auto-generated method stub

		}
	};

	private mRecFragmentListener mReceiveFgCallBack = new mRecFragmentListener() {

		@Override
		public void hideMenu(boolean InputSoftOpen) {
			if (InputSoftOpen) {
				btnHide.setVisibility(View.GONE);
				layoutMenu.setVisibility(View.GONE);
			} else {
				btnHide.setVisibility(View.VISIBLE);
				layoutMenu.setVisibility(LastState);
			}
		}

		@Override
		public void btnCancel() {
			// TODO Auto-generated method stub

		}
	};

}
