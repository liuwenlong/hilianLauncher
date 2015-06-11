package com.example.cloudmirror.ui.widget;

import com.mapgoo.eagle.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
/**
 * 概述：自定义圆环View， 圆环进度条View，可用于打分，动态打分等视图
 * 
 * @author yao
 * 
 * @created 2014年7月11日
 */
public class RingProgressBar extends View {

	private Paint paint;

	private int roundColor; // 圆环的颜色

	private int roundProgressColor; // 圆环进度的颜色

	private int textColor; // 中间进度百分比的字符串的颜色

	private float textSize; // 中间进度百分比的字符串的字体

	private float roundWidth; // 圆环的宽度

	private int max; // 最大进度

	private int progress; // 当前进度

	private boolean textIsDisplayable; // 是否显示中间的进度

	private int style; // 进度的风格，实心或者空心

	// 正切圆
	private RectF oval; // 用于定义的圆弧的形状和大小的界限

	public static final int STROKE = 0;
	public static final int FILL = 1;
	
	public void setRoundColor(int roundColor) {
		this.roundColor = roundColor;
	}
	
	public void setRoundProgressColor(int roundProgressColor) {
		this.roundProgressColor = roundProgressColor;
	}
	

	public RingProgressBar(Context context) {
		this(context, null);
	}

	public RingProgressBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public RingProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		// init...
		paint = new Paint();
		oval = new RectF();

		// get custome property
		TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.RingProgressBar);

		// 获取自定义属性和默认值
		roundColor = mTypedArray.getColor(R.styleable.RingProgressBar_roundColor, Color.RED);
		roundProgressColor = mTypedArray.getColor(R.styleable.RingProgressBar_roundProgressColor, Color.GREEN);
		textColor = mTypedArray.getColor(R.styleable.RingProgressBar_textColor, Color.GREEN);
		textSize = mTypedArray.getDimension(R.styleable.RingProgressBar_textSize, 15);
		roundWidth = mTypedArray.getDimension(R.styleable.RingProgressBar_roundWidth, 5);
		max = mTypedArray.getInteger(R.styleable.RingProgressBar_maxVal, 365);
		progress = mTypedArray.getInteger(R.styleable.RingProgressBar_progressVal, 365);
		textIsDisplayable = mTypedArray.getBoolean(R.styleable.RingProgressBar_textIsDisplayable, true);
		style = mTypedArray.getInt(R.styleable.RingProgressBar_style, 0);

		mTypedArray.recycle();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// 画最外层的大圆环
		int center = getWidth() / 2; // 获取圆心的x坐标
		int radius = (int) (center - roundWidth / 2); // 圆环的半径

		paint.setColor(roundColor); // 设置圆环的颜色
		paint.setStyle(Paint.Style.STROKE); // 设置空心
		paint.setStrokeWidth(roundWidth); // 设置圆环的宽度
		paint.setAntiAlias(true); // 消除锯齿
		canvas.drawCircle(center, center, radius, paint); // 画出圆环

		// 画文字进度
		paint.setStrokeWidth(0);
		paint.setColor(textColor);
		paint.setTextSize(textSize);
		paint.setTypeface(Typeface.DEFAULT_BOLD); // 设置字体

//		int percent = (int) (((float) progress / (float) max) * 100); // 中间的进度百分比，先转换成float在进行除法运算，不然都为0
//		float textWidth = paint.measureText(String.valueOf(percent)); // 测量字体宽度，我们需要根据字体的宽度设置在圆环中间

//		if (textIsDisplayable && percent != 0 && style == STROKE) {
//			canvas.drawText(String.valueOf(percent), center - textWidth / 2, center + textSize / 2, paint); // 画出进度百分比
//		}

		// 画圆弧 ，画圆环的进度
		// 设置进度是实心还是空心
		paint.setStrokeWidth(roundWidth); // 设置圆环的宽度
		paint.setColor(roundProgressColor); // 设置进度的颜色

		oval.set(center - radius, center - radius, center + radius, center + radius);

		switch (style) {
		case STROKE: {
			paint.setStyle(Paint.Style.STROKE);
			canvas.drawArc(oval, -90, 360 * progress / max, false, paint); // 根据进度画圆弧
			break;
		}
		case FILL: {
			paint.setStyle(Paint.Style.FILL_AND_STROKE);
			if (progress != 0)
				canvas.drawArc(oval, -90, 360 * progress / max, true, paint); // 根据进度画圆弧
			break;
		}
		}

	}

	public synchronized int getMax() {
		return max;
	}

	/**
	 * 设置进度的最大值
	 * 
	 * @param max
	 */
	public synchronized void setMax(int max) {
		if (max < 0) {
			throw new IllegalArgumentException("max not less than 0");
		}
		this.max = max;
	}

	/**
	 * 获取进度.需要同步
	 * 
	 * @return
	 */
	public synchronized int getProgress() {
		return progress;
	}

	/**
	 * 设置进度，此为线程安全控件，由于考虑多线的问题，需要同步 刷新界面调用postInvalidate()能在非UI线程刷新
	 * 
	 * @param progress
	 */
	public synchronized void setProgress(int progress) {
		if (progress < 0) {
			throw new IllegalArgumentException("progress not less than 0");
		}
		if (progress > max) {
			progress = max;
		}
		if (progress <= max) {
			this.progress = progress;
			postInvalidate();
		}

	}

	public int getCricleColor() {
		return roundColor;
	}

	public void setCricleColor(int cricleColor) {
		this.roundColor = cricleColor;
	}

	public int getCricleProgressColor() {
		return roundProgressColor;
	}

	public void setCricleProgressColor(int cricleProgressColor) {
		this.roundProgressColor = cricleProgressColor;
	}

	public int getTextColor() {
		return textColor;
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}

	public float getTextSize() {
		return textSize;
	}

	public void setTextSize(float textSize) {
		this.textSize = textSize;
	}

	public float getRoundWidth() {
		return roundWidth;
	}

	public void setRoundWidth(float roundWidth) {
		this.roundWidth = roundWidth;
	}
}