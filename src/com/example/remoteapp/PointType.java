package com.example.remoteapp;


public class PointType
{
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
	
	public PointType screenToView(double little_a)
	{
		PointType ret = new PointType();

		PointType AO = new PointType(-640.0,-400.0);
		PointType AX = new PointType(x-640.0, y-400.0);
		PointType AX2 = AX.spin(-little_a);

		ret.x = AX2.x - AO.x;
		ret.y = AX2.y - AO.y;

		//ori.setText("AX: " + AX.x + " " + AX.y + "\nAX2: " + AX2.x + " " + AX2.y
		// + "\nret: " + ret.x + " "+ret.y);

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
	
	public PointType XYtoView(PointType origin)
	{
		PointType temp = new PointType(x,y);
		PointType ret = new PointType();
		ret.y = -temp.x;
		ret.x = -temp.y;
		
		ret.x+=origin.x;
		ret.y+=origin.y;
		
		return ret;
	}
	
	public String toString()
	{
		return x+" "+y;
	}
}




