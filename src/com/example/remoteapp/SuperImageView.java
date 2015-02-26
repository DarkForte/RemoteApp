package com.example.remoteapp;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.util.AttributeSet;
import android.widget.ImageView;

public class SuperImageView extends ImageView
{
	Paint paint;
	Bitmap bmp=BitmapFactory.decodeResource(this.getContext().getResources(), R.drawable.pic2);
	PointType finget_point;
	PointType MAP_ORIGIN;
	
	List<Segment> toDraw;
	
	public SuperImageView(Context context) 
	{
		super(context);
		// TODO Auto-generated constructor stub
	}

	public SuperImageView(Context context, AttributeSet attrs, int defStyle) 
	{
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public SuperImageView(Context context, AttributeSet attrs) 
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
		toDraw = list;
		return;
	}
	
	public void setMapOrigin(PointType origin)
	{
		MAP_ORIGIN = origin;
		return;
	}
	
	@Override
	protected void onDraw(Canvas canvas) 
	{   
		// TODO Auto-generated method stub
		super.onDraw(canvas);

		canvas.drawBitmap(bmp, 0, 0, null);
		
        Paint paint = new Paint();
        
        //Draw map
        paint. setColor(Color.BLUE);
        paint. setStrokeWidth(3);
        int i;
        for(i=0; i<toDraw.size();i++)
        {
        	Segment now = toDraw.get(i);
        	
        	PointType tmp_start = now.s;
        	tmp_start = tmp_start.XYtoView(MAP_ORIGIN);
        	PointType tmp_end = now.e;
        	tmp_end = tmp_end.XYtoView(MAP_ORIGIN);
        	
        	canvas.drawLine((float)tmp_start.x, (float)tmp_start.y, 
        			(float)tmp_end.x, (float)tmp_end.y, paint);
        }
        
        //Draw touchPoint
        paint.setColor(Color.RED);
        if(finget_point != null)
        	canvas.drawCircle( (float)finget_point.x, (float)finget_point.y, 10, paint);
        
        return;
	}


}
