package com.example.remoteapp;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;

import java.util.ArrayList;
import java.util.List;

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

public class SuperImageView extends ImageViewTouch
{
	Paint paint;
	Bitmap bmp=BitmapFactory.decodeResource(this.getContext().getResources(), 
			R.drawable.pic2048).copy(Bitmap.Config.ARGB_8888, true);
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
		toDraw = new ArrayList<Segment>(list);
		return;
	}
	
	/*public void setPointListWithMatrix(List<Segment> list, Matrix matrix)
	{
		toDraw = new ArrayList<Segment>();
		int i;
		for(i=0; i<list.size();i++)
		{
			Segment seg_now = new Segment(list.get(0));
			//Log.d("segment", "before: " + list.get(0));
			seg_now = seg_now.transWithMatrix(matrix);
			//Log.d("segment", "after: " + list.get(0));
			toDraw.add(seg_now);
		}
		
		return;
	}*/
	
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
		
		Paint paint = new Paint();
        
        //Draw touchPoint
        paint.setColor(Color.RED);
        if(finget_point != null)
        	canvas.drawCircle( (float)finget_point.x, (float)finget_point.y, 10, paint);
           
        return;
	}

	public void drawMap(Bitmap base_bmp, Bitmap draw_bmp)
	{
		Bitmap now_bmp = Bitmap.createBitmap(base_bmp);
		Canvas canvas = new Canvas(now_bmp);
		Paint paint = new Paint();
		
		//Draw map
        paint. setColor(Color.BLUE);
        paint. setStrokeWidth(10);
        int i;
        for(i=0; i<toDraw.size();i++)
        {
        	Segment now = toDraw.get(i);
        	
        	PointType tmp_start = now.s;
        	//tmp_start = tmp_start.centerXYToTopLeftXY(MAP_ORIGIN);
        	PointType tmp_end = now.e;
        	//tmp_end = tmp_end.centerXYToTopLeftXY(MAP_ORIGIN);
        	
        	canvas.drawLine((float)tmp_start.x, (float)tmp_start.y, 
        			(float)tmp_end.x, (float)tmp_end.y, paint);
        }
        
        //Draw draw_bmp
        if(draw_bmp != null)
        	canvas.drawBitmap(draw_bmp, 0, 0, paint);
        
        Matrix matrix = getDisplayMatrix();
        setImageBitmap(now_bmp, matrix, ZOOM_INVALID, ZOOM_INVALID);     
	}
}
