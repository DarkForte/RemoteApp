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
	PointType touchPoint;
	PointType MAP_ORIGIN;
	
	List<PointType> toDraw;
	
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
		touchPoint = p;
		return;
	}
	
	public void setPointList(List<PointType> list)
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
        int i;
        for(i=0; i<toDraw.size();i++)
        {
        	PointType now = toDraw.get(i);
        	PointType tmp = new PointType(now.x, now.y);
        	tmp = tmp.XYtoView(MAP_ORIGIN);
        	canvas.drawCircle((float)tmp.x, (float)tmp.y, 2, paint);
        }
        
        //Draw touchPoint
        
        paint.setColor(Color.RED);
        if(touchPoint != null)
        	canvas.drawCircle( (float)touchPoint.x, (float)touchPoint.y, 10, paint);
        
        //imageView.setImageBitmap(newb);
        return;
	}


}
