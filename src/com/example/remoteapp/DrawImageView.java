package com.example.remoteapp;

import java.util.ArrayList;
import java.util.List;

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
	PointType start_point = new PointType();
	PointType now_point = new PointType();
	Paint paint = new Paint();
	float current_degree=0;
	Matrix matrix;
	
	List<PointType> points;
	List<PointType> now_points;
	
	private void init()
	{
		saved_bmp = Bitmap.createBitmap(2048, 1536,
				Bitmap.Config.ARGB_8888);
		now_bmp = Bitmap.createBitmap(saved_bmp);
		points = new ArrayList<PointType>();
		now_points = new ArrayList<PointType>();
	}

	public DrawImageView(Context context) 
	{
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}

	public DrawImageView(Context context, AttributeSet attrs, int defStyle) 
	{
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init();
	}

	public DrawImageView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) 
	{
		// TODO Auto-generated method stub
		Canvas canvas = new Canvas(now_bmp);
		paint.setColor(Color.GRAY);
		paint.setStrokeWidth(20);
		
		Matrix m_invert = new Matrix();
		matrix.invert(m_invert);

		int action_type = e.getAction();
		if(action_type == MotionEvent.ACTION_DOWN)
		{
			start_point.set(e.getX(), e.getY());
			start_point = start_point.transWithMatrix(m_invert);
			now_points.add(start_point);
		}
		else if(action_type == MotionEvent.ACTION_MOVE)
		{
			now_point.set(e.getX(), e.getY());
			now_point = now_point.transWithMatrix(m_invert);
			now_points.add(now_point);
			
			canvas.drawLine((float)start_point.x, (float)start_point.y, 
					(float)now_point.x, (float)now_point.y, paint);
			
			start_point.set(now_point.x, now_point.y);
			
		}
		
		this.setImageBitmap(now_bmp);
		return true;
	}

	public void save()
	{
		saved_bmp = Bitmap.createBitmap(now_bmp);
		points.addAll(now_points);
		
		/*int i;
		for(i=0;i<points.size();i++)
			Log.d("Points", ((PointType)points.get(i)).toString());*/
		
		now_points.clear();
	}
	
	public void loadWithMatrix(Matrix matrix)
	{
		now_bmp = Bitmap.createBitmap(saved_bmp);
		this.setImageBitmap(now_bmp);
		this.matrix = matrix;
		this.setImageMatrix(matrix);
	}
	
	public void cancel()
	{
		now_points.clear();
	}
	
	public void setPoints(List<PointType> p)
	{
		points = p;
	}
	
}
