package com.graduation.common.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.graduation.R;


/**
 * this class can show a waiting dialog</p>
 * Note: if the dialog dissmiss by cancle key ,it will  call onCancled function
 * </p>  but if the dialog is closed by close function ,it will not call onCancle function.
 * </p> the onCancled function is a callback function
 * @author shenxy
 *
 */
public class WaitDialog {

	private Context context;
	private View view;
	private PopupWindow popupWindow;
	
	
	private boolean isClose=false;

	public interface OnCancleCallback {
		public void onCancled();
	}

	public void setOnCancleListener(final OnCancleCallback onCancleCallback) {
		popupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
				if(!isClose)
				onCancleCallback.onCancled();
			}
		});

	}

	public WaitDialog(Context context, View view) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.view = view;
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		View contentView = LayoutInflater.from(context).inflate(
				R.layout.wait_dialog_layout, null);
		popupWindow = new PopupWindow(contentView, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT, true);
		popupWindow.setTouchable(true);
		popupWindow.setFocusable(true);
		popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

	}

	public boolean isShowing() {
		return popupWindow.isShowing();
	}

	public void show() {
		popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
	}
    public void close()
    {
    	isClose=true;
    	popupWindow.dismiss();
    }

	public void setMessage(String text) {
		TextView tv = (TextView) view.findViewById(R.id.wait_tips);
		tv.setText(text);
	}

}
