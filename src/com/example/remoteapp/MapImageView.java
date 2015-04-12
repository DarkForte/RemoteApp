package com.example.remoteapp;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;

import java.util.ArrayList;
import java.util.List;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

public class MapImageView extends ImageViewTouch
{
	Bitmap bmp=BitmapFactory.decodeResource(this.getContext().getResources(), 
			R.drawable.pic2048).copy(Bitmap.Config.ARGB_8888, true);
	PointType finget_point;
	PointType MAP_ORIGIN;
	PointType car_point;
	
	List<Segment> toDraw;
	
	ValueAnimator animator;
	
	public MapImageView(Context context) 
	{
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MapImageView(Context context, AttributeSet attrs, int defStyle) 
	{
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public MapImageView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	public void setTouchPoint(PointType p)
	{
		finget_point = p;
		return;
	}
	
	public void setPointList(List<Segment> list)
	{
		toDraw = new ArrayList<Segment>(list);
		return;
	}
	
	public void setCarPoint(PointType car_point) {
		this.car_point = car_point;
	}

	public void setMapOrigin(PointType origin)
	{
		MAP_ORIGIN = origin;
		return;
	}
	
	public void startAnimator()
	{
		animator = ValueAnimator.ofInt(255,0);
		animator.setDuration(500);
		animator.start();
		invalidate();
	}
	
	@Override
	protected void onDraw(Canvas canvas) 
	{   
		// TODO Auto-generated method stub
		super.onDraw(canvas);
        
		Paint paint = new Paint();
        //Draw touchPoint
        paint.setColor(Color.RED);
        if(finget_point != null)
        {
        	if(animator.isRunning())
            {
            	int now_alpha = (Integer)animator.getAnimatedValue();
            	paint.setAlpha(now_alpha);
            	canvas.drawCircle( (float)finget_point.x, (float)finget_point.y, 10, paint);
            	invalidate();
            }
        }
        
        
        return;
	}

	public void drawMap(Bitmap base_bmp)
	{
		Bitmap now_bmp = Bitmap.createBitmap(base_bmp);
		Canvas canvas = new Canvas(now_bmp);
		Paint paint = new Paint();
		
		//Draw map
        paint.setColor(Color.rgb(100, 100, 255));
        paint.setStrokeWidth(10);
        int i;
        for(i=0; i<toDraw.size();i++)
        {
        	Segment now = toDraw.get(i);
        	
        	PointType tmp_start = now.s;
        	PointType tmp_end = now.e;
        	
        	canvas.drawLine((float)tmp_start.x, (float)tmp_start.y, 
        			(float)tmp_end.x, (float)tmp_end.y, paint);
        }
        
        //Draw car point
        paint.setColor(Color.BLACK);
        canvas.drawCircle((float)car_point.x, (float)car_point.y, 5, paint);
        
        Matrix matrix = getDisplayMatrix();
        setImageBitmap(now_bmp, matrix, ZOOM_INVALID, ZOOM_INVALID);     
	}
}
