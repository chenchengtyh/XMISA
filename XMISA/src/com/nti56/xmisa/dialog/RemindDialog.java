package com.nti56.xmisa.dialog;

import com.nti56.xmisa.R;
import com.nti56.xmisa.util.Content;

import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class RemindDialog extends Dialog implements OnClickListener {

	private mRemindDialogListener mListener;

	public interface mRemindDialogListener {
		public void btnSure();
//		public void btnCancel();
	}

	private Button btnSure, btnCancel;
	private int mModel;
	private TextView RemindTitle;
	private Context mContext;

	public RemindDialog(Context context, int theme, int Model, mRemindDialogListener Listener) {
		super(context, theme);
		this.mModel = Model;
		this.mContext = context;
		this.mListener = Listener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_remind);
		btnSure = (Button) findViewById(R.id.btn_remind_sure);
		btnCancel = (Button) findViewById(R.id.btn_remind_cancel);
		RemindTitle = (TextView) findViewById(R.id.tv_remind_title);

		btnSure.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
//		this.getWindow().setFlags(Content.FLAG_HOMEKEY_DISPATCHED, Content.FLAG_HOMEKEY_DISPATCHED);
	}

	@Override
	protected void onStart() {
		switch (mModel) {
		case Content.MODEL_EXIT:
			RemindTitle.setText(mContext.getString(R.string.remind_exit));
			break;

		case Content.MODEL_DELETE:
			RemindTitle.setText(mContext.getString(R.string.remind_delete));
			break;

		default:
			break;
		}
		super.onStart();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_remind_sure:
			mListener.btnSure();
			break;

		case R.id.btn_remind_cancel:
//			mListener.btnCancel();
			dismiss();
			break;

		default:
			break;
		}
	}

//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		switch (keyCode) {
//		case KeyEvent.KEYCODE_HOME:
////			mListener.btnCancel();
//			dismiss();
//			return true;
//		default:
//			return super.onKeyDown(keyCode, event);
//		}
//	}

}
