package com.graduation.activity;

import java.io.InputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.graduation.R;
import com.graduation.common.other.ViewPagerAdapter;

public class GuideActivity extends Activity implements OnClickListener,
		OnPageChangeListener {

	private Button buttonStart;
	private ViewPager viewPager;
	private View viewStart;
	private LayoutInflater mLi;
	private ViewPagerAdapter viewPagerAdapter;
	private ArrayList<View> views;
	private Context context;
	private static final int[] pics = { R.drawable.new_guid_01,
			R.drawable.new_guid_02, R.drawable.new_guid_03 };
	private ImageView[] points;
	private int currentIndex;
	private Bitmap bmp[];
	private Bitmap bmpStart;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);
		context = this;
		mLi = LayoutInflater.from(this);
		viewStart = mLi.inflate(R.layout.start_page, null);
		ImageView iv = (ImageView) viewStart.findViewById(R.id.imageView_start);
		InputStream is = viewStart.getResources().openRawResource(
				R.drawable.new_guid_start);
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		bmpStart = BitmapFactory.decodeStream(is, null, opt);
		iv.setImageBitmap(bmpStart);
		buttonStart = (Button) viewStart.findViewById(R.id.startButton);
		buttonStart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
				Editor eidEditor=sp.edit();
				eidEditor.putBoolean("FirstUse", false);
				eidEditor.commit();
				
				
				Intent intent = new Intent(GuideActivity.this,
						LoginActivity.class);
				intent.putExtra("LastActivity", "GuideActivity");
				startActivity(intent);
				GuideActivity.this.finish();
				if (!bmpStart.isRecycled()) {
					bmpStart.recycle();
				}
				if (bmp.length > 0) {
					for (int i = 0; i < bmp.length; i++) {
						if (!bmp[i].isRecycled())
							bmp[i].recycle();
					}
					System.gc();
				}
			}
		});

		initView();
		initData();

	}

	private void initData() {
		// TODO Auto-generated method stub
		LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		// LayoutInflater mLi = LayoutInflater.from(this);
		// View view1 = mLi.inflate(R.layout.guide_view01, null);
		// startBt = (Button) view6.findViewById(R.id.startBtn);
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		bmp = new Bitmap[pics.length];
		for (int i = 0; i < pics.length; i++) {
			ImageView iv = new ImageView(context);
			iv.setLayoutParams(mParams);
			// 不使用此方法，可能会导致内存溢出
			// iv.setImageResource(pics[i]);
			InputStream is = this.getResources().openRawResource(pics[i]);
			bmp[i] = BitmapFactory.decodeStream(is, null, opt);
			iv.setImageBitmap(bmp[i]);
			iv.setScaleType(ImageView.ScaleType.FIT_XY);
			views.add(iv);
		}

		views.add(viewStart);
		viewPager.setAdapter(viewPagerAdapter);
		// 设置监听
		viewPager.setOnPageChangeListener(this);

		// 初始化导航点
		initPoint();
	}

	private void initPoint() {
		// TODO Auto-generated method stub
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearL);
		points = new ImageView[pics.length + 1];
		for (int i = 0; i <= pics.length; i++) {
			points[i] = (ImageView) linearLayout.getChildAt(i);
			points[i].setEnabled(true);
			// points[i].setOnClickListener(this);
			points[i].setTag(i);
		}
		currentIndex = 0;
		points[currentIndex].setEnabled(false);
	}

	private void initView() {
		// TODO Auto-generated method stub
		views = new ArrayList<View>();
		viewPager = (ViewPager) this.findViewById(R.id.guidPages);
		viewPagerAdapter = new ViewPagerAdapter(views);

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub
		setCurDot(arg0);
	}

	private void setCurDot(int position) {
		// TODO Auto-generated method stub
		if (position < 0 || position > pics.length || currentIndex == position)
			return;
		points[position].setEnabled(false);
		points[currentIndex].setEnabled(true);
		currentIndex = position;

	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		int position = which;
		setCurView(position);
		setCurDot(position);
	}

	private void setCurView(int position) {
		// TODO Auto-generated method stub
		if (position < 0 || position >= pics.length)
			return;
		viewPager.setCurrentItem(position);
	}


}
