package com.graduation.common.other;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;

public class CustomPopMenu extends PopupWindow {

	private Context context;
	private LayoutInflater layoutInflater;
	public View allView;
	private int resId;

	public CustomPopMenu(Context context, int resourceId) {
		super(context);
		this.context = context;
		this.resId = resourceId;
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		allView = layoutInflater.inflate(resId, null);
		allView.setFocusable(true);
		allView.setFocusableInTouchMode(true);
		allView.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if (isShowing()) {
					dismiss();
					return true;
				}
				return false;
			}

		});
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		setContentView(allView);
		setWidth(dm.widthPixels/2);
		setHeight(LayoutParams.WRAP_CONTENT);
		setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		setTouchable(true);
		setFocusable(true);
		setOutsideTouchable(true);
	}

	public View getView()
	{
		return allView;
	}
}
