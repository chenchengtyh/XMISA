package com.nti56.xmisa.adapter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

public class MyHorizontalScrollView extends HorizontalScrollView {

	private int mMode;
	private final static int NONE = 0;
	private final static int VERTICAL = 5;// 垂直移动
	private final static int HORIZONTAL = 10;// 水平移动

	private int startX, startY;
	private final static int RATIO = 3;// 实际的padding的距离与界面上偏移距离的比例

//	private GestureDetector mGestureDetector;

	public MyHorizontalScrollView(Context context) {
		super(context);
//		mGestureDetector = new GestureDetector(context, new YScrollDetector());
	}

	public MyHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
//		mGestureDetector = new GestureDetector(context, new YScrollDetector());
	}

	public MyHorizontalScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
//		mGestureDetector = new GestureDetector(context, new YScrollDetector());
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mMode = NONE;
			startX = (int) ev.getX();// 手指按下时记录当前位置
			startY = (int) ev.getY();// 手指按下时记录当前位置
			break;

		case MotionEvent.ACTION_MOVE:
			if (mMode == NONE) {
				int DifValueX = ((int) ev.getX() - startX) / RATIO;// Y轴移动距离
				int DifValueY = ((int) ev.getY() - startY) / RATIO;// Y轴移动距离
				if (Math.abs(DifValueX) > 2 || Math.abs(DifValueY) > 2) {
					if (Math.abs(DifValueX) > Math.abs(DifValueY)) {
						mMode = HORIZONTAL;// 水平模式
					} else {
						mMode = VERTICAL;// 垂直模式
					}
				}
			}
			break;

		case MotionEvent.ACTION_UP:
			mMode = NONE;
			break;

		default:
			break;
		}
		if(mMode == VERTICAL){
			return false;
		}
		return super.onInterceptTouchEvent(ev);
//		return super.onInterceptTouchEvent(ev) && mGestureDetector.onTouchEvent(ev);
	}

//	class YScrollDetector extends SimpleOnGestureListener {
//
//		@Override
//		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//			return super.onScroll(e1, e2, distanceX, distanceY);
//		}
//
//	}

}