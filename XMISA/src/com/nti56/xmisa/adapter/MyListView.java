package com.nti56.xmisa.adapter;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nti56.xmisa.MyApplication;
import com.nti56.xmisa.R;
import com.nti56.xmisa.util.Content;
import com.nti56.xmisa.util.MyLog;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

public class MyListView extends ListView implements OnScrollListener {

	private Context mContext;

	private int state;
	private final static int CLOSE = 0;
	private final static int OPENING = 1; // 下拉过程中
	private final static int OPEN = 2;// 下拉完成
	private final static int LOADING = 3;// 正在刷新的状态值

	private int mMode;
	private final static int NONE = 0;
	private final static int VERTICAL = 5;// 垂直移动
	private final static int HORIZONTAL = 10;// 水平移动

	private final static int RATIO = 3;// 实际的padding的距离与界面上偏移距离的比例

	private int headerContentHeight;// 定义头部下拉刷新的布局的高度

	private int startX, startY;
	private LayoutInflater inflater;

	// ListView头部下拉刷新的布局
	private LinearLayout headerView;
	private TextView lvHeaderTipsTv;
	private TextView lvHeaderLastUpdatedTv;
	private ImageView lvHeaderArrowIv;
	private ProgressBar lvHeaderProgressBar;

	private RotateAnimation animation;
	private RotateAnimation reverseAnimation;

	private boolean isBack;
	public boolean isOnMeasure;
	private boolean Refreshable = false;

	private int mListCode;
//	private Editor editor;

	private String TAG = "MyListView";

	private OnRefreshListener refreshListener;

	public interface OnRefreshListener {
		public void onRefresh();
	}

	public MyListView(Context context) {
		super(context);
		mContext = context;
	}

	public MyListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		isOnMeasure = true;
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	public void onLayout(boolean changed, int l, int t, int r, int b) {
		isOnMeasure = false;
		super.onLayout(changed, l, t, r, b);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (Refreshable) {
			switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				MyLog.e(TAG, "onTouchEvent", "MotionEvent.ACTION_DOWN");
				mMode = NONE;
				startX = (int) ev.getX();// 手指按下时记录当前位置
				startY = (int) ev.getY();// 手指按下时记录当前位置
				break;

			case MotionEvent.ACTION_MOVE:
				MyLog.d(TAG, "onTouchEvent", "MotionEvent.ACTION_MOVE");
				int DifValueX = ((int) ev.getX() - startX) / RATIO;// Y轴移动距离
				int DifValueY = ((int) ev.getY() - startY) / RATIO;// Y轴移动距离
				if (mMode == NONE && (Math.abs(DifValueX) > 2 || Math.abs(DifValueY) > 2)) {
					if (Math.abs(DifValueX) > Math.abs(DifValueY)) {
						mMode = HORIZONTAL;// 水平模式
					} else {
						mMode = VERTICAL;// 垂直模式
					}
				}
				if (mMode == VERTICAL && state != LOADING) {// 当未正在刷新时
					if (DifValueY <= 0) {// 关闭
						if (state != CLOSE) {
							state = CLOSE;
							isBack = false;
							changeHeaderViewByState();
						}
					} else if (DifValueY < headerContentHeight && state != OPENING) {// 正在下拉
						state = OPENING;
						changeHeaderViewByState();
					} else if (DifValueY >= headerContentHeight && state != OPEN) {// 下拉完成
						state = OPEN;
						isBack = true;
						changeHeaderViewByState();
					}

					if (state != CLOSE) {// 动态改变界面高度
						headerView.setPadding(0, DifValueY - headerContentHeight, 0, 0);
					}
				}

				break;

			case MotionEvent.ACTION_UP:
				MyLog.e(TAG, "onTouchEvent", "MotionEvent.ACTION_UP");
				if (state == OPEN) {
					state = LOADING;
					changeHeaderViewByState();
					onLvRefresh();
				} else if (state == OPENING) {
					state = CLOSE;
					changeHeaderViewByState();
				}

				break;

			default:
				break;
			}
		}
		return super.onTouchEvent(ev);
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		if (Refreshable) {
			initHeaderView();
			UpdateLastTime();//TODO  此处要从MyApplication.mySharedPreferences获得上次刷新时间写入
		}
		super.setAdapter(adapter);
	}

	private void InitSharedPreferences(){
		if (MyApplication.mySharedPreferences == null) {
			MyApplication.mySharedPreferences = mContext.getSharedPreferences("mysharepreferences", Activity.MODE_PRIVATE);
		}
		if(MyApplication.editor == null){
			MyApplication.editor = MyApplication.mySharedPreferences.edit();
		}
	}

	private void UpdateLastTime(){
		InitSharedPreferences();
		String lastUpdate= "";
		switch (mListCode) {
		case Content.LIST_RECEIVE:
			lastUpdate = MyApplication.mySharedPreferences.getString("rec_update", "");
			break;

		case Content.LIST_INPUT:
			lastUpdate = MyApplication.mySharedPreferences.getString("ipt_update", "");
			break;

		case Content.LIST_OUTPUT:
			lastUpdate = MyApplication.mySharedPreferences.getString("opt_update", "");
			break;

		case Content.LIST_INVENTORY:
			lastUpdate = MyApplication.mySharedPreferences.getString("ivt_update", "");
			break;

		case Content.LIST_ASSIGN:
			lastUpdate = MyApplication.mySharedPreferences.getString("asg_update", "");
			break;

		case Content.LIST_EXAMINE:
			lastUpdate = MyApplication.mySharedPreferences.getString("exm_update", "");
			break;

		case Content.LIST_SURVEY:
			lastUpdate = MyApplication.mySharedPreferences.getString("sur_update", "");
			break;

		case Content.LIST_FUMIGATE:
			lastUpdate = MyApplication.mySharedPreferences.getString("fmg_update", "");
			break;

		case Content.LIST_PESTS:
			lastUpdate = MyApplication.mySharedPreferences.getString("pts_update", "");
			break;

		case Content.LIST_PATROL:
			lastUpdate = MyApplication.mySharedPreferences.getString("ptl_update", "");
			break;

		default:
			break;
		}
		lvHeaderLastUpdatedTv.setText("上次更新时间：" + lastUpdate);
	}
	
	public void setOnRefreshListener(OnRefreshListener refreshListener, int listCode) {
		this.Refreshable = true;
		this.refreshListener = refreshListener;
		mListCode = listCode;
	}

	public void onRefreshComplete(boolean Success, int listCode) {
		state = CLOSE;
		if(Success){//TODO 此处要将时间写入 MyApplication.mySharedPreferences
			DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
			String updateTime = format.format(new Date());
			lvHeaderLastUpdatedTv.setText("上次更新时间：" + updateTime);
			InitSharedPreferences();
			switch (listCode) {
			case Content.LIST_RECEIVE:
				MyApplication.editor.putString("rec_update", updateTime);
				break;

			case Content.LIST_INPUT:
				MyApplication.editor.putString("ipt_update", updateTime);
				break;

			case Content.LIST_OUTPUT:
				MyApplication.editor.putString("opt_update", updateTime);
				break;

			case Content.LIST_INVENTORY:
				MyApplication.editor.putString("ivt_update", updateTime);
				break;

			case Content.LIST_ASSIGN:
				MyApplication.editor.putString("asg_update", updateTime);
				break;

			case Content.LIST_EXAMINE:
				MyApplication.editor.putString("exm_update", updateTime);
				break;

			case Content.LIST_SURVEY:
				MyApplication.editor.putString("sur_update", updateTime);
				break;

			case Content.LIST_FUMIGATE:
				MyApplication.editor.putString("fmg_update", updateTime);
				break;

			case Content.LIST_PESTS:
				MyApplication.editor.putString("pts_update", updateTime);
				break;

			case Content.LIST_PATROL:
				MyApplication.editor.putString("ptl_update", updateTime);
				break;

			default:
				break;
			}
			MyApplication.editor.commit();
		}
		changeHeaderViewByState();
	}

	@Override
	public Drawable getDivider() {
		return super.getDivider();
	}

	@Override
	public int getDividerHeight() {
		return super.getDividerHeight();
	}

	private void initHeaderView() {
		MyLog.e(TAG, "initHeaderView");
		setCacheColorHint(mContext.getResources().getColor(R.color.yellow));
		inflater = LayoutInflater.from(mContext);
		headerView = (LinearLayout) inflater.inflate(R.layout.listview_header, null);
		lvHeaderTipsTv = (TextView) headerView.findViewById(R.id.lvHeaderTipsTv);
		lvHeaderLastUpdatedTv = (TextView) headerView.findViewById(R.id.lvHeaderLastUpdatedTv);

		lvHeaderArrowIv = (ImageView) headerView.findViewById(R.id.lvHeaderArrowIv);
		// 设置下拉刷新图标的最小高度和宽度
		lvHeaderArrowIv.setMinimumWidth(70);
		lvHeaderArrowIv.setMinimumHeight(50);

		lvHeaderProgressBar = (ProgressBar) headerView.findViewById(R.id.lvHeaderProgressBar);
		measureView(headerView);
		headerContentHeight = headerView.getMeasuredHeight();

		// 设置内边距，正好距离顶部为一个负的整个布局的高度，正好把头部隐藏
		headerView.setPadding(0, -1 * headerContentHeight, 0, 0);
		// 头部存在时会有间隙，去掉此间隙
		setY(0 - getDividerHeight());
		// 重绘一下
		headerView.invalidate();
		// 将下拉刷新的布局加入ListView的顶部
		addHeaderView(headerView, null, true);//第三参数设置为false会导致headerView与itemView的分割线为透明
		setOnScrollListener(this);
		// 设置旋转动画事件
		animation = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(250);
		animation.setFillAfter(true);

		reverseAnimation = new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(200);
		reverseAnimation.setFillAfter(true);
	}

	// 当状态改变时候，调用该方法，以更新界面
	private void changeHeaderViewByState() {
		switch (state) {
		case CLOSE:
			headerView.setPadding(0, 0 - headerContentHeight, 0, 0);
			lvHeaderTipsTv.setText("下拉刷新");
			lvHeaderProgressBar.setVisibility(View.GONE);
			lvHeaderArrowIv.setVisibility(View.VISIBLE);
			lvHeaderArrowIv.clearAnimation();
			lvHeaderArrowIv.setImageResource(R.drawable.arrow);
			break;

		case OPENING:
			lvHeaderTipsTv.setText("下拉刷新");
			if (isBack) {// 是由OPEN状态转变来的
				isBack = false;
				lvHeaderArrowIv.clearAnimation();
				lvHeaderArrowIv.startAnimation(reverseAnimation);
			}
			break;

		case OPEN:
			lvHeaderTipsTv.setText("松开刷新");
			lvHeaderProgressBar.setVisibility(View.GONE);
			lvHeaderArrowIv.clearAnimation();// 清除动画
			lvHeaderArrowIv.startAnimation(animation);// 开始动画效果
			break;

		case LOADING:
			headerView.setPadding(0, 0, 0, 0);
			lvHeaderTipsTv.setText("正在刷新...");
			lvHeaderProgressBar.setVisibility(View.VISIBLE);
			lvHeaderArrowIv.clearAnimation();
			lvHeaderArrowIv.setVisibility(View.GONE);
			break;

		}
	}

	// 此方法直接照搬自网络上的一个下拉刷新的demo，此处是“估计”headView的width以及height
	private void measureView(View child) {
		ViewGroup.LayoutParams params = child.getLayoutParams();
		if (params == null) {
			params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, params.width);
		int lpHeight = params.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	private void onLvRefresh() {
		if (refreshListener != null) {
			refreshListener.onRefresh();
		}
	}

	public boolean isOnMeasure() {
		return isOnMeasure;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
	}

}
