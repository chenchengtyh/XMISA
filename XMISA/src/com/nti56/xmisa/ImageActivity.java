package com.nti56.xmisa;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.LinkedList;

import com.nti56.xmisa.util.Content;
import com.nti56.xmisa.util.FTPUtil;
import com.nti56.xmisa.util.FTPUtil.UploadProgressListener;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class ImageActivity extends Activity implements OnClickListener, OnLongClickListener {

	private File mediaStorageDir = null;
	private ImageView ImageView1, ImageView2, ImageView3, ImageView4, ImageBigView, SelectedView;
	private Button ButtonCamera, ButtonPhoto, ButtonSaveBack, ButtonSaveUpload;

	private String mTaskID, mNCState;
	private String mImageName1, mImageName2, mImageName3, mImageName4;// 传过来的图片名称
	private String mNewImageName1, mNewImageName2, mNewImageName3, mNewImageName4;// 新建图片时的名称
	private Bitmap mBitmap;
	private int mSelectID = 0;
	String[] finalName = new String[] { " ", " ", " ", " " };
	private String[] imageVersions = new String[] { " ", " ", " ", " " };
	private int mHashCode;
	private int mActivityResult;
	private FTPUtil mFTP;
	private static final int HIGHT_BIG = 486;
	private static final int HIGHT_SMALL = 72;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.e("NTI", "ImageActivity.........onCreate()");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image);

		ImageView1 = (ImageView) findViewById(R.id.iv_photo_s_1);
		ImageView2 = (ImageView) findViewById(R.id.iv_photo_s_2);
		ImageView3 = (ImageView) findViewById(R.id.iv_photo_s_3);
		ImageView4 = (ImageView) findViewById(R.id.iv_photo_s_4);
		ImageBigView = (ImageView) findViewById(R.id.iv_image_photo);

		ButtonCamera = (Button) findViewById(R.id.btn_take_camera);
		ButtonPhoto = (Button) findViewById(R.id.btn_take_photo);
		ButtonSaveBack = (Button) findViewById(R.id.btn_save_back);
		ButtonSaveUpload = (Button) findViewById(R.id.btn_save_upload);

		ButtonCamera.setOnClickListener(this);
		ButtonPhoto.setOnClickListener(this);
		ButtonSaveBack.setOnClickListener(this);
		ButtonSaveUpload.setOnClickListener(this);

		ImageView1.setOnClickListener(this);
		ImageView2.setOnClickListener(this);
		ImageView3.setOnClickListener(this);
		ImageView4.setOnClickListener(this);

		ImageView1.setOnLongClickListener(this);
		ImageView2.setOnLongClickListener(this);
		ImageView3.setOnLongClickListener(this);
		ImageView4.setOnLongClickListener(this);
		mHandler = new MyHandler(this);
	}

	private static MyHandler mHandler;

	@Override
	protected void onStart() {
		Log.e("NTI", "ImageActivity.........onStart()");
		super.onStart();

		Intent intent = getIntent();
		if (intent.hashCode() != mHashCode) {
			mHashCode = intent.hashCode();
			mTaskID = intent.getStringExtra("taskId");
			mNCState = intent.getStringExtra("NCState");
			mImageName1 = intent.getStringExtra("imageName1");
			mImageName2 = intent.getStringExtra("imageName2");
			mImageName3 = intent.getStringExtra("imageName3");
			mImageName4 = intent.getStringExtra("imageName4");
			String imageVersion = intent.getStringExtra("imageVersion");

			if (imageVersion != null) {
				imageVersions = imageVersion.split("/");
			}

			mNewImageName1 = getNewImageName(1);
			mNewImageName2 = getNewImageName(2);
			mNewImageName3 = getNewImageName(3);
			mNewImageName4 = getNewImageName(4);

			finalName[0] = mImageName1;
			finalName[1] = mImageName2;
			finalName[2] = mImageName3;
			finalName[3] = mImageName4;

			mSelectID = 1;
			ImageView1.setSelected(true);
			SelectedView = ImageView1;

			ShowImage(ImageView1, mImageName1);
			ShowImage(ImageView2, mImageName2);
			ShowImage(ImageView3, mImageName3);
			ShowImage(ImageView4, mImageName4);
			// TODO 起一个线程，清掉所有过时图片。
		}
	}

	private void ShowImage(ImageView imageView, String imageName) {
		if (imageName != null && !imageName.equals(" ")) {
			File file = getOutputMediaFile(imageName);
			if (file != null && file.exists()) {// 图片存在，则显示
				if (imageView == SelectedView) {
					setBitmapByPath(HIGHT_BIG, file.getPath());
					ImageBigView.setImageBitmap(mBitmap);
				}
				setBitmapByPath(HIGHT_SMALL, file.getPath());
				imageView.setImageBitmap(mBitmap);
			} else {
				if (imageView == SelectedView) {
					ImageBigView.setImageResource(R.drawable.src_864x486);
				}
				imageView.setImageResource(R.drawable.src_126x70);
			}
		}
	}

	private void ShowBigImage(String imageName) {// 单击缩略图时显示对应大图
		if (imageName != null && !imageName.equals("")) {
			File file = getOutputMediaFile(imageName);
			if (file != null && file.exists()) {// 图片存在，则显示
				setBitmapByPath(HIGHT_BIG, file.getPath());
				ImageBigView.setImageBitmap(mBitmap);

			} else {
				ImageBigView.setImageResource(R.drawable.src_864x486);
			}
		}
	}

	private void ShowNewImage(String imageName) {// 拍照返回时刷新新获得的图片
		File file = getOutputMediaFile(imageName);
		if (file != null && file.exists()) {
			setBitmapByPath(HIGHT_BIG, file.getPath());
			ImageBigView.setImageBitmap(mBitmap);
			setBitmapByPath(HIGHT_SMALL, file.getPath());
			SelectedView.setImageBitmap(mBitmap);
		}
	}

	private String getNewImageName(int num) {// 设置下一张图片的编号
		String newImageName;
		String mVersion;
		try {
			int version = Integer.parseInt(imageVersions[num - 1]) + 1;
			mVersion = (version > 99) ? "_01" : ((version > 9) ? "_" + version : "_0" + version);
		} catch (Exception e1) {
			mVersion = "_01";
		}
		newImageName = mTaskID + "_0" + num + mVersion;
		return newImageName;
	}

	private void setBitmapByPath(int goalHight, String Path) {
		Options options = new Options();
		options.inJustDecodeBounds = true;
		mBitmap = BitmapFactory.decodeFile(Path, options);
		int scale = (int) options.outHeight / goalHight;
		if (scale <= 0)
			scale = 1;
		options.inSampleSize = scale;
		options.inJustDecodeBounds = false;
		mBitmap = BitmapFactory.decodeFile(Path, options);
	}

	private Bitmap AdjustBitmap(Bitmap bitmap, int degree) {// A B 缩放效果均不理想。
		int newHight = 0;
		int newWidth = 0;
		int translate = 0;
		if (degree == 0 || degree == 180) {
			newWidth = bitmap.getWidth();
			newHight = bitmap.getHeight();
			translate = 0;
		} else if (degree == 90 || degree == 270) {
			newWidth = bitmap.getHeight();
			newHight = bitmap.getWidth();
			translate = (newHight - newWidth) / 2;
		} else {
			return bitmap;
		}
		// A float scale = 1;
		// A if (newWidth > 1024) {
		// A scale = (float) 1024 / newWidth;
		// A newWidth = 1024;
		// A newHight = 576;
		// A }

		Matrix matrix = new Matrix();
		matrix.setRotate(degree, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
		matrix.postTranslate(-translate, translate);
		// A matrix.postScale(scale, scale);

		Paint paint = new Paint();
		paint.setAntiAlias(true);

		Bitmap newBitmap = Bitmap.createBitmap(newWidth, newHight, bitmap.getConfig());// Bitmap.Config.ARGB_8888
		Canvas canvas = new Canvas(newBitmap);
		canvas.drawBitmap(bitmap, matrix, paint);
		// B Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0,
		// bitmap.getWidth(), bitmap.getHeight(), matrix, true);

		bitmap.recycle();
		return newBitmap;
	}

	private void SaveBitmap(String imageName) {
		File file = getOutputMediaFile(imageName);
		if (file != null && file.exists()) {
			Options options = new Options();
			options.inSampleSize = 2;// 缩小一半
			Bitmap bitmap = BitmapFactory.decodeFile(file.getPath(), options);
			if (bitmap == null) {
				return;
			}
			int degree = (options.outHeight > options.outWidth) ? 270 : 0;// 顺时针旋转角度
			bitmap = AdjustBitmap(bitmap, degree);
			try {
				FileOutputStream fos = new FileOutputStream(file);
				bitmap.compress(CompressFormat.JPEG, 60, fos);
				fos.flush();
				fos.close();
			} catch (Exception e) {
			}
			bitmap.recycle();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// super.onActivityResult(requestCode, resultCode, data);
		Log.e("NTI", "ImageActivity.........onActivityResult()");
		if (requestCode == Content.ACTIVITY_REQUEST_CODE_CAMERA) {
			if (resultCode == RESULT_OK) {
				setFinalName();
				String mSeclet = getNewImageName_Select();
				SaveBitmap(mSeclet);// 调整图片并保存
				ShowNewImage(mSeclet);// 刷新图片
			} else if (resultCode == RESULT_CANCELED) {
				// User cancelled the image capture
			} else {
				// TODO Image capture failed, advise user
			}
		}
	}

	private String getNewImageName_Select() {
		switch (mSelectID) {
		case 1:
			return mNewImageName1;
		case 2:
			return mNewImageName2;
		case 3:
			return mNewImageName3;
		case 4:
			return mNewImageName4;
		default:
			return "";
		}
	}

	private void setFinalName() {
		switch (mSelectID) {
		case 1:
			mImageName1 = mNewImageName1;
			finalName[0] = mNewImageName1;
			break;
		case 2:
			mImageName2 = mNewImageName2;
			finalName[1] = mNewImageName2;
			break;
		case 3:
			mImageName3 = mNewImageName3;
			finalName[2] = mNewImageName3;
			break;
		case 4:
			mImageName4 = mNewImageName4;
			finalName[3] = mNewImageName4;
			break;
		default:
			break;
		}
	}

	private Uri getOutputMediaFileUri(String imageName) {
		File file = getOutputMediaFile(imageName);
		if (file != null) {
			return Uri.fromFile(file);
		}
		return null;
	}

	private File getOutputMediaFile(String imageName) {
		if (mediaStorageDir == null) {
			mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
					"XMISA_image");
		}
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.e("NTI", "failed to create directory");
				return null;
			}
		}
		File mediaFile;
		mediaFile = new File(mediaStorageDir.getPath() + File.separator + imageName + ".jpg");
		return mediaFile;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_photo_s_1:
			if (!v.isSelected()) {
				mSelectID = 1;
				ShowBigImage(mImageName1);
				changeSelected(ImageView1);
			}
			return;

		case R.id.iv_photo_s_2:
			if (!v.isSelected()) {
				mSelectID = 2;
				ShowBigImage(mImageName2);
				changeSelected(ImageView2);
			}
			return;

		case R.id.iv_photo_s_3:
			if (!v.isSelected()) {
				mSelectID = 3;
				ShowBigImage(mImageName3);
				changeSelected(ImageView3);
			}
			return;

		case R.id.iv_photo_s_4:
			if (!v.isSelected()) {
				mSelectID = 4;
				ShowBigImage(mImageName4);
				changeSelected(ImageView4);
			}
			return;
		}
		if (mNCState.equals(Content.HTTP_UPLOAD_TRUE)) {
			Toast.makeText(getApplication(), "已反馈数据不支持此操作。", Toast.LENGTH_SHORT).show();
			return;
		}
		switch (v.getId()) {
		case R.id.btn_take_camera:
			if (mNCState.equals(Content.HTTP_UPLOAD_TRUE)) {
				Toast.makeText(getApplication(), "已反馈数据不支持此操作。", Toast.LENGTH_SHORT).show();
				return;
			}
			if (SelectedView != null) {
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				String mSeclet = getNewImageName_Select();
				Uri fileUri = getOutputMediaFileUri(mSeclet);

				Log.e("NTI", "ImageActivity.........fileUri.getPath() = " + fileUri.getPath());
				intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
				startActivityForResult(intent, Content.ACTIVITY_REQUEST_CODE_CAMERA);
			}
			break;

		case R.id.btn_take_photo:

			break;

		case R.id.btn_save_upload:
			mActivityResult = RESULT_OK;
			if (!FTP_Lock) {
				new Thread(upLoadImageFiles).start();
			} else {
				Toast.makeText(getApplication(), "正在上传中，请稍后...", Toast.LENGTH_SHORT).show();
			}
			// finish();
			break;

		case R.id.btn_save_back:
			mActivityResult = RESULT_OK;
			finish();
			break;
		default:
			return;
		}
	}

	private boolean FTP_Lock;
	Runnable upLoadImageFiles = new Runnable() {

		public void run() {
			FTP_Lock = true;
			Log.e("LXM", "-----uploadMultiFile--1");
			LinkedList<File> fileList = new LinkedList<File>();
			for (int i = 0; i < finalName.length; i++) {
				File file = getOutputMediaFile(finalName[i]);
				if (file != null && file.exists()) {
					fileList.add(file);
				}
			}
			if (fileList.size() < 1) {
				FTP_Lock = false;
				return;
			}
			try {
				// 文件上传
				mFTP = new FTPUtil(5, 5, 5);
				mFTP.uploadMultiFile(fileList, Content.FTP_IMAGE_DIRECTORY, mUpLoadListener);
				mHandler.obtainMessage(Content.HANDLER_TOAST, Content.FTP_IMAGE_UPLOAD_SUCCESSS).sendToTarget();
			} catch (IOException e) {
				e.printStackTrace();
			}

			FTP_Lock = false;
		}
	};

	public void Toast(String ToastText) {
		Toast.makeText(this, ToastText, Toast.LENGTH_SHORT).show();
	}

	private UploadProgressListener mUpLoadListener = new UploadProgressListener() {
		@Override
		public void onUploadProgress(String currentStep, long uploadSize, File file) {
			// TODO Auto-generated method stub
			Log.e("LXM", currentStep);
			if (currentStep.equals(Content.FTP_UPLOAD_SUCCESS)) {
				Log.e("LXM", "-----shanchuan--successful");
			} else if (currentStep.equals(Content.FTP_UPLOAD_LOADING)) {
				long fize = file.length();
				float num = (float) uploadSize / (float) fize;
				int result = (int) (num * 100);
				Log.e("LXM", "-----shangchuan---" + result + "%");
			} else if (currentStep.equals(Content.FTP_CONNECT_FAIL)) {
				mHandler.obtainMessage(Content.HANDLER_TOAST, Content.FTP_CONNECT_FAIL).sendToTarget();
			}
		}
	};

	static class MyHandler extends Handler {
		WeakReference<ImageActivity> mActivity;

		MyHandler(ImageActivity imageActivity) {
			mActivity = new WeakReference<ImageActivity>(imageActivity);
		}

		@Override
		public void handleMessage(Message msg) {
			ImageActivity theActivity = mActivity.get();
			if (theActivity == null) {
				return;
			}
			switch (msg.what) {
			case Content.HANDLER_TOAST:
				theActivity.Toast((String) msg.obj);
				break;
			default:
				break;
			}
		}
	}

	@Override
	public boolean onLongClick(View v) {
		if (mNCState.equals(Content.HTTP_UPLOAD_TRUE)) {
			Toast.makeText(getApplication(), "已反馈数据不支持此操作。", Toast.LENGTH_SHORT).show();
			return true;
		}
		switch (v.getId()) {

		case R.id.iv_photo_s_1:
			ImageView1.setImageResource(R.drawable.src_126x70);
			if (v.isSelected()) {
				ImageBigView.setImageResource(R.drawable.src_864x486);
			}
			mImageName1 = " ";
			finalName[0] = " ";
			break;

		case R.id.iv_photo_s_2:
			ImageView2.setImageResource(R.drawable.src_126x70);
			if (v.isSelected()) {
				ImageBigView.setImageResource(R.drawable.src_864x486);
			}
			mImageName2 = " ";
			finalName[1] = " ";
			break;

		case R.id.iv_photo_s_3:
			ImageView3.setImageResource(R.drawable.src_126x70);
			if (v.isSelected()) {
				ImageBigView.setImageResource(R.drawable.src_864x486);
			}
			mImageName3 = " ";
			finalName[2] = " ";
			break;

		case R.id.iv_photo_s_4:
			ImageView4.setImageResource(R.drawable.src_126x70);
			if (v.isSelected()) {
				ImageBigView.setImageResource(R.drawable.src_864x486);
			}
			mImageName4 = " ";
			finalName[3] = " ";
			break;

		default:
			break;
		}
		return true;
	}

	private void changeSelected(ImageView imageView) {
		if (SelectedView != null) {
			SelectedView.setSelected(false);
			SelectedView.setBackgroundResource(R.color.white);
		}
		imageView.setSelected(true);
		imageView.setBackgroundResource(R.color.yellow);
		SelectedView = imageView;
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		Log.e("NTI", "ImageActivity.........finish()");
		Intent intent = new Intent();
		intent.putExtra("newImageName", finalName[0] + "/" + finalName[1] + "/" + finalName[2] + "/" + finalName[3]);
		setResult(mActivityResult, intent);
		Log.e("NTI", "ImageActivity.........mActivityResult = " + mActivityResult);
		super.finish();
	}

	@Override
	protected void onDestroy() {
		Log.e("NTI", "ImageActivity.........onDestroy()");
		if (mBitmap != null) {
			mBitmap.recycle();
		}
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		Log.e("NTI", "ImageActivity.........onBackPressed()");
		// mActivityResult = RESULT_CANCELED;
		// finish();
		super.onBackPressed();
	}

}
