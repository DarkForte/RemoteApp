package com.example.remoteapp;

import android.graphics.Matrix;


public class PointType
{
	//Android screen axis: ¡úx ¡ýy 
	public double x;
	public double y;
	
	public PointType(double x, double y)
	{
		this.x=x;
		this.y=y;
	}
	public PointType()
	{
		x=0;
		y=0;
	}
	
	public PointType spin(double little_a) // spin need to be written like p=p.spin(d)
	{
		PointType ret = new PointType();
		ret.x = x*Math.cos(little_a) - y*Math.sin(little_a);
	    ret.y = x*Math.sin(little_a) + y*Math.cos(little_a);
	    return ret;
	}
	
	public void move(double dx, double dy)
	{
		x+=dx;
		y+=dy;
		return;
	}
	
	public PointType spin(double degree, PointType origin)
	{
		PointType vec = new PointType(x - origin.x, y - origin.y);
		//System.out.println("vec_before spin: "+vec);
		vec = vec.spin(degree);
		//System.out.println("vec: "+vec);
		PointType ret = new PointType(vec.x + origin.x, vec.y + origin.y);
		return ret;
	}
	
	public PointType centerXYToTopLeftXY(PointType origin)
	{
		
		PointType ret = new PointType();
		ret.x = x;
		ret.y = -y;
		
		ret.x+=origin.x;
		ret.y+=origin.y;
		
		return ret;
	}
	
	public PointType transWithMatrix(Matrix matrix)
	{
		float tmp[] = new float[2];
		tmp[0] = (float)x;
		tmp[1] = (float)y;
		matrix.mapPoints(tmp);
		PointType ret = new PointType();
		ret.x = tmp[0];
		ret.y = tmp[1];
		return ret;
	}
	
	public String toString()
	{
		return x+" "+y;
	}
}




