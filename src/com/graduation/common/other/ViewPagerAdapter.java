package com.graduation.common.other;

import java.util.ArrayList;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

public class ViewPagerAdapter extends PagerAdapter {

	private ArrayList<View> views;
	public ViewPagerAdapter(ArrayList<View> views)
	{
		this.views=views;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(views!=null)
			return views.size();
		else
			return 0;
		
	}
	@Override
	public Object instantiateItem(View view,int Position)
	{
		((ViewPager)view).addView(views.get(Position),0);
		return views.get(Position);
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0==arg1;
	}
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// TODO Auto-generated method stub
		((ViewPager)container).removeView(views.get(position));
	}

}
