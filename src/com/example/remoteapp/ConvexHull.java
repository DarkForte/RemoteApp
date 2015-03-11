package com.example.remoteapp;

import java.util.Arrays;
import java.util.Comparator;

public class ConvexHull
{
	/*
	 *  求凸包，Graham算法
	 *  点的编号0~n-1
	 *  返回凸包结果Stack[0~top-1]为凸包的编号
	 */
	final double EPS = 1e-8;
	PointType list[];
	public int Stack[],top;
	
	public ConvexHull(PointType points[])
	{
		top=0;
		list = points;
		Stack = new int[list.length];
	}
	
	public int getNum()
	{
		return top;
	}
	
	public PointType getPoint(int i)
	{
		return list[Stack[i]];
	}
	
	class Compare implements Comparator<PointType>
	{
		@Override
		public int compare(PointType p1, PointType p2) 
		{
			// TODO Auto-generated method stub
			PointType t1 = p1.sub(list[0]);
			PointType t2 = p2.sub(list[0]);
			
			double tmp = t1.cross(t2);
			if(sgn(tmp) > 0)
				return -1;
			else if(sgn(tmp) == 0 && sgn(p1.dist(list[0]) - p2.dist(list[0])) <= 0)
				return -1;
			else 
				return 1;
		}
	}
	
	private int sgn(double x)
	{
		if(Math.abs(x) < EPS)
			return 0;
		if(x>0)
			return 1;
		return -1;
	}
		
	void swap(PointType x, PointType y)
	{
		PointType t = new PointType(x);
		x.x = y.x;
		x.y = y.y;
		y.x = t.x;
		y.y = t.y;
	}
	
	private boolean check(int i)
	{
		if(top<=1)
			return false;
		PointType p1 = list[Stack[top-1]].sub(list[Stack[top-2]]);
		PointType p2 = list[i].sub(list[Stack[top-2]]);
		if(sgn(p1.cross(p2)) <= 0)
			return true;
		return false;
	}
	
	public void Graham(int n)
	{
		PointType p0;
		int k = 0;
		p0 = new PointType(list[0]);
		//找最下边的一个点
		for(int i = 1;i < n;i++)
		{
			if( (p0.y > list[i].y) || (p0.y == list[i].y && p0.x > list[i].x) )
			{
				p0 = new PointType(list[i]);
				k = i;
			}
		}
		swap(list[k],list[0]);
		Arrays.sort(list, new Compare());
		if(n == 1)
		{
			top = 1;
			Stack[0] = 0;
			return;
		}
		if(n == 2)
		{
			top = 2;
			Stack[0] = 0;
			Stack[1] = 1;
			return ;
		}
		Stack[0] = 0;
		Stack[1] = 1;
		top = 2;
		for(int i = 2;i < n;i++)
		{
			while(check(i)) 
				top--;
			Stack[top++] = i;
		}
	}
	
}
