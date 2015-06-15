package com.example.cloudmirror.ui.widget;

import java.util.Random;

import com.example.cloudmirror.utils.MyLog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class VoliceInView extends View{
	private Paint mPaint; 
	Random mRandom;
	public VoliceInView(Context context){
		super(context);
	}
	public VoliceInView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	public VoliceInView(Context context, AttributeSet attrs,int l) {
		super(context, attrs,l);
		// TODO Auto-generated constructor stub
	}
	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		if(mPaint == null){
			mPaint = new Paint();
			 mPaint.setColor(0xff215b80);
			 mPaint.setStyle(Style.FILL);	
			 mRandom = new Random();
		}
		//MyLog.D("w="+getWidth()+"h="+getHeight());
		int width = getWidth();
		int height = getHeight();
		int count = 70;
		int sRectFill = 9;
		int sRectStoke = 4;
		int swidth = sRectFill+sRectStoke;
		int cw = width/count;
		int cws = cw*sRectStoke/(sRectFill+sRectStoke);
		int cwf = cw*sRectFill/(sRectFill+sRectStoke);
		int sheight = getHeight()*8/10;
		int sleft = sRectStoke;
		for(int i=0;i<count;i++){
			int h = mRandom.nextInt(sheight)+height-sheight;
			canvas.drawRect(new RectF(sleft+i*cw, h, sleft+cwf+i*cw, height), mPaint);
		}
	}
	
	public void startAnim(){
		removeCallbacks(runnable);
		post(runnable);
	}
	
	public void stopAnim(){
		removeCallbacks(runnable);
	}
	Runnable runnable = new Runnable(){
		@Override
		public void run() {
			// TODO Auto-generated method stub
			invalidate();
			postDelayed(this, 50);
		}
	};
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		int width;
		int height ;

		if (widthMode == MeasureSpec.EXACTLY)
		{
			width = widthSize;
		} else
		{
			width = widthSize;
		}

		if (heightMode == MeasureSpec.EXACTLY)
		{
			height = heightSize;
		} else
		{
			height = heightSize;
		}
		
		

		setMeasuredDimension(width, height);
	}

}
