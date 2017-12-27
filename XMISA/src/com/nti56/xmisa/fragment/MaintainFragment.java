package com.nti56.xmisa.fragment;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

import com.nti56.xmisa.R;
import com.nti56.xmisa.fragment.maintain.FumigateFragment.mFmgFragmentListener;
import com.nti56.xmisa.fragment.maintain.PatrolFragment;
import com.nti56.xmisa.fragment.maintain.PatrolFragment.mPtlFragmentListener;
import com.nti56.xmisa.fragment.maintain.PestsFragment.mPtsFragmentListener;
import com.nti56.xmisa.fragment.maintain.SurveyFragment;
import com.nti56.xmisa.fragment.maintain.FumigateFragment;
import com.nti56.xmisa.fragment.maintain.PestsFragment;
import com.nti56.xmisa.fragment.maintain.SurveyFragment.mSurFragmentListener;
import com.nti56.xmisa.util.Content;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;

public class MaintainFragment extends Fragment implements OnClickListener {

	protected WeakReference<View> mRootView;
	private Button btnSurvey, btnFumigate, btnPests, btnPatrol, btnHide, lastButton;
	private SurveyFragment surFragment;
	private FumigateFragment fumFragment;
	private PestsFragment pesFragment;
	private PatrolFragment patFragment;
	private FragmentManager MainTainFgManager = null;
	private Fragment mCurFg;
	private LinearLayout layoutMenu, layoutFg;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (mRootView == null || mRootView.get() == null) {
			View v = inflater.inflate(R.layout.fragment_maintain, container, false);
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
		// TODO Auto-generated method stub
		Log.e("NTI", "onDetach()...........Store");
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
		if (MainTainFgManager == null) {

			MainTainFgManager = getChildFragmentManager();

			btnSurvey = (Button) view.findViewById(R.id.btn_3_survey);
			btnFumigate = (Button) view.findViewById(R.id.btn_3_fumigate);
			btnPests = (Button) view.findViewById(R.id.btn_3_pests);
			btnPatrol = (Button) view.findViewById(R.id.btn_3_patrol);
			btnHide = (Button) view.findViewById(R.id.btn_3_hide);

			layoutMenu = (LinearLayout) view.findViewById(R.id.layout_3_menu);
			layoutFg = (LinearLayout) view.findViewById(R.id.layout_3_fragmentRoot);

			btnSurvey.setOnClickListener(this);
			btnFumigate.setOnClickListener(this);
			btnPests.setOnClickListener(this);
			btnPatrol.setOnClickListener(this);
			btnHide.setOnClickListener(this);

			surFragment = new SurveyFragment(mSurveyFgCallBack);
			fumFragment = new FumigateFragment(mFumigateFgCallBack);
			pesFragment = new PestsFragment(mPestsFgCallBack);
			patFragment = new PatrolFragment(mPatrolFgCallBack);

			switchFrament(null, patFragment);

			btnPatrol.setSelected(true);
			lastButton = btnPatrol;
		}
	}

	public void switchFrament(Fragment from, Fragment to) {
		if (mCurFg != to) {
			mCurFg = to;
			FragmentTransaction transaction = MainTainFgManager.beginTransaction();
			if (from != null) {
				transaction.hide(from);
			}
			if (!to.isAdded()) {
				transaction.add(R.id.layout_3_fragmentRoot, to);
			}
			transaction.show(to).commit();
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_3_hide) {
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
		case R.id.btn_3_survey:
			if (lastButton != btnSurvey) {
				lastButton.setSelected(false);
				btnSurvey.setSelected(true);
				lastButton = btnSurvey;
				switchFrament(mCurFg, surFragment);
			}
			break;

		case R.id.btn_3_fumigate:
			if (lastButton != btnFumigate) {
				lastButton.setSelected(false);
				btnFumigate.setSelected(true);
				lastButton = btnFumigate;
				switchFrament(mCurFg, fumFragment);
			}
			break;

		case R.id.btn_3_pests:
			if (lastButton != btnPests) {
				lastButton.setSelected(false);
				btnPests.setSelected(true);
				lastButton = btnPests;
				switchFrament(mCurFg, pesFragment);
			}
			break;

		case R.id.btn_3_patrol:
			if (lastButton != btnPatrol) {
				lastButton.setSelected(false);
				btnPatrol.setSelected(true);
				lastButton = btnPatrol;
				switchFrament(mCurFg, patFragment);
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

	private int LastState;

	private mSurFragmentListener mSurveyFgCallBack = new mSurFragmentListener() {

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

	private mFmgFragmentListener mFumigateFgCallBack = new mFmgFragmentListener() {

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

	private mPtsFragmentListener mPestsFgCallBack = new mPtsFragmentListener() {

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

	private mPtlFragmentListener mPatrolFgCallBack = new mPtlFragmentListener() {

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
