package com.nti56.xmisa.fragment;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

import com.nti56.xmisa.R;
import com.nti56.xmisa.fragment.check.AssignFragment;
import com.nti56.xmisa.fragment.check.AssignFragment.mAsgFragmentListener;
import com.nti56.xmisa.fragment.check.ExamineFragment;
import com.nti56.xmisa.fragment.check.ExamineFragment.mExmFragmentListener;
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

public class CheckFragment extends Fragment implements OnClickListener {

	protected WeakReference<View> mRootView;
	private Button btnAssign, btnExamine, btnHide, lastButton;
	private AssignFragment asgFragment;
	private ExamineFragment exmFragment;
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
			View v = inflater.inflate(R.layout.fragment_check, container, false);
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
		Log.e("NTI", "onDetach()...........Check");
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

			btnAssign = (Button) view.findViewById(R.id.btn_2_assign);
			btnExamine = (Button) view.findViewById(R.id.btn_2_examine);
			btnHide = (Button) view.findViewById(R.id.btn_2_hide);

			layoutMenu = (LinearLayout) view.findViewById(R.id.layout_2_menu);
			layoutFg = (LinearLayout) view.findViewById(R.id.layout_2_fragmentRoot);

			btnAssign.setOnClickListener(this);
			btnExamine.setOnClickListener(this);
			btnHide.setOnClickListener(this);

			asgFragment = new AssignFragment(mAssignFgCallBack);
			exmFragment = new ExamineFragment(mExamineFgCallBack);

			switchFrament(null, asgFragment);

			btnAssign.setSelected(true);
			lastButton = btnAssign;
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
				transaction.add(R.id.layout_2_fragmentRoot, to);
			}
			transaction.show(to).commit();
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_2_hide) {
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
		case R.id.btn_2_assign:
			if (lastButton != btnAssign) {
				lastButton.setSelected(false);
				btnAssign.setSelected(true);
				lastButton = btnAssign;
				switchFrament(mCurFg, asgFragment);
			}
			break;

		case R.id.btn_2_examine:
			if (lastButton != btnExamine) {
				lastButton.setSelected(false);
				btnExamine.setSelected(true);
				lastButton = btnExamine;
				switchFrament(mCurFg, exmFragment);
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

	private mAsgFragmentListener mAssignFgCallBack = new mAsgFragmentListener() {

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

	private mExmFragmentListener mExamineFgCallBack = new mExmFragmentListener() {

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
