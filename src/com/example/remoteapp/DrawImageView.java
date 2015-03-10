package com.example.remoteapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class DrawImageView extends ImageView 
{
	Bitmap saved_bmp;
	Bitmap now_bmp;
	PointF start_point = new PointF();
	PointF now_point = new PointF();
	Paint paint = new Paint();
	float current_degree=0;
	
	public void init()
	{
		saved_bmp = Bitmap.createBitmap(2048, 1536, Bitmap.Config.ARGB_8888);
		now_bmp = Bitmap.createBitmap(saved_bmp);
	}

	public DrawImageView(Context context) 
	{
		super(context);
		// TODO Auto-generated constructor stub
	}

	public DrawImageView(Context context, AttributeSet attrs, int defStyle) 
	{
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public DrawImageView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) 
	{
		// TODO Auto-generated method stub
		Canvas canvas = new Canvas(now_bmp);
		paint.setColor(Color.GRAY);
		paint.setStrokeWidth(20);

		int action_type = e.getAction();
		if(action_type == MotionEvent.ACTION_DOWN)
		{
			start_point.set(e.getX(), e.getY());
			Log.d("draw", start_point.toString());
		}
		else if(action_type == MotionEvent.ACTION_MOVE)
		{
			now_point.set(e.getX(), e.getY());
			Log.d("draw", "start: "+ start_point.toString());
			Log.d("draw", "now: "+ now_point.toString());
			canvas.drawLine(start_point.x, start_point.y, now_point.x, now_point.y, paint);
			start_point.set(now_point.x, now_point.y);
		}
		
		this.setImageBitmap(now_bmp);
		return true;
	}

	public void save()
	{
		saved_bmp = Bitmap.createBitmap(now_bmp);
	}
	
	public void loadWithMatrix(Matrix matrix)
	{
		Log.d("matrix", matrix.toString());
		now_bmp = Bitmap.createBitmap(saved_bmp);
		this.setImageBitmap(now_bmp);
		this.setImageMatrix(matrix);
	}
}
