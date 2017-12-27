package com.nti56.xmisa.dialog;

import com.nti56.xmisa.R;
import com.nti56.xmisa.util.Content;

import android.view.View;
import android.view.View.OnClickListener;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class BuildingSelectDialog extends Dialog implements OnClickListener {

	private mBuildingSelectDialogListener mListener;

	public interface mBuildingSelectDialogListener {
		public void btnStart(String building);

//		public void btnCancel();
	}

	private Button btnStart, btnCancel;
	private RadioGroup mRadioGroup;
//	private Context mContext;

	public BuildingSelectDialog(Context context, int theme, mBuildingSelectDialogListener mListener) {
		super(context, theme);
//		this.mContext = context;
		this.mListener = mListener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_new_patroltask);
		btnStart = (Button) findViewById(R.id.btn_patrol_dialog_startpatrol);
		btnCancel = (Button) findViewById(R.id.btn_patrol_dialog_cancel);

		mRadioGroup = (RadioGroup) findViewById(R.id.radiogroup_patrol_dialog);
		
		btnStart.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		
//		mRadioGroup.setOnCheckedChangeListener(this);
		String[] Buildings = Content.BUILDINGS.replace(" ", "").split(",");

		for (int i = 0; i < Buildings.length; i++) {
			RadioButton tempButton = (RadioButton) getLayoutInflater().inflate(R.layout.radio_item, null);
//			RadioButton tempButton = new RadioButton(mContext);
			if(i%2 == 1){
				tempButton.setBackgroundResource(R.color.white); // 设置RadioButton的背景图片
//			} else{
//				tempButton.setBackgroundResource(R.color.blue); // 设置RadioButton的背景图片
			}
//			tempButton.setBackgroundResource(R.drawable.xxx); // 设置RadioButton的背景图片
//			tempButton.setButtonDrawable(R.drawable.xxx); // 设置按钮的样式
//			tempButton.setPadding(0, 0, 0, 0); // 设置文字距离按钮四周的距离
//			tempButton.setId(i);

			Buildings[i] = (Buildings[i].length() == 1) ? ("0" + Buildings[i]) : Buildings[i];
			tempButton.setText(Buildings[i]+"栋");
			mRadioGroup.addView(tempButton, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		}

//		this.getWindow().setFlags(Content.FLAG_HOMEKEY_DISPATCHED, Content.FLAG_HOMEKEY_DISPATCHED);
	}

	/* (non-Javadoc)
	 * @see android.app.Dialog#onStart()
	 */
	@Override
	protected void onStart() {
		mRadioGroup.clearCheck();
		super.onStart();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_patrol_dialog_startpatrol:
			RadioButton radioButton = (RadioButton) findViewById(mRadioGroup.getCheckedRadioButtonId());
			if(radioButton != null){
				String building = radioButton.getText().toString().replace("栋", "");
				mListener.btnStart(building);
			}
			break;

		case R.id.btn_patrol_dialog_cancel:
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
//			mListener.btnCancel();
//			return true;
//		default:
//			return super.onKeyDown(keyCode, event);
//		}
//	}

}
