package com.nti56.xmisa.fragment;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

import com.nti56.xmisa.LoginActivity;
import com.nti56.xmisa.MainActivity;
import com.nti56.xmisa.MyApplication;
import com.nti56.xmisa.R;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SettingFragment extends Fragment implements OnClickListener {

	private Button btnLogout, btnChangeIP, btnChangeKey;
	private String TAG = "NTI";
	protected WeakReference<View> mRootView;
	private EditText newIP;
	private Editor editor;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (mRootView == null || mRootView.get() == null) {
			View v = inflater.inflate(R.layout.fragment_setting, container, false);
			mRootView = new WeakReference<View>(v);
		} else {
			ViewGroup parent = (ViewGroup) mRootView.get().getParent();
			if (parent != null) {
				parent.removeView(mRootView.get());
			}
		}
		View view = mRootView.get();

		newIP = (EditText) view.findViewById(R.id.edit_test);
		btnChangeIP = (Button) view.findViewById(R.id.button_change_ip);
		btnChangeKey = (Button) view.findViewById(R.id.button_change_key);
		btnLogout = (Button) view.findViewById(R.id.button_logout);

		btnChangeIP.setOnClickListener(this);
		btnChangeKey.setOnClickListener(this);
		btnLogout.setOnClickListener(this);

		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_change_ip:

			if (MyApplication.mySharedPreferences == null) {
				MyApplication.mySharedPreferences = this.getActivity().getSharedPreferences("mysharepreferences",
						Activity.MODE_PRIVATE);
			}
			editor = MyApplication.mySharedPreferences.edit();
			editor.putString("cur_ip", newIP.getText().toString());
			editor.commit();
			break;

		case R.id.button_change_key:

			break;

		case R.id.button_logout:

			Intent intent = new Intent();
			intent.setClass(this.getActivity(), LoginActivity.class);
			startActivity(intent);
			this.getActivity().finish();
			break;

		default:
			break;
		}
	}

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		Log.e(TAG, "onDetach()...........Setting");
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
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.e(TAG, "onAttach()...........Setting");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e(TAG, "onCreate()...........Setting");
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.e(TAG, "onActivityCreated()...........Setting");
	}

	@Override
	public void onStart() {
		super.onStart();
		Log.e(TAG, "onStart()...........Setting");
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.e(TAG, "onResume()...........Setting");
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.e(TAG, "onPause()...........Setting");
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.e(TAG, "onStop()...........Setting");
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.e(TAG, "onDestroyView()...........Setting");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.e(TAG, "onDestroy()...........Setting");
	}

}
