package com.example.remoteapp;

import android.graphics.Matrix;

public class Segment 
{
	public PointType s,e;
	Segment(){}
	Segment(PointType _s, PointType _e)
	{
		s=_s;
		e=_e;
	}
	
	Segment(Segment seg)
	{
		s=seg.s;
		e=seg.e;
	}
	
	public Segment transWithMatrix(Matrix matrix)
	{
		Segment ret = new Segment();
		ret.s = s.transWithMatrix(matrix);
		ret.e = e.transWithMatrix(matrix);
		return ret;
	}
	@Override
	public String toString() 
	{
		// TODO Auto-generated method stub
		return "start: "+s + " end: "+e;
	}
	
	
}
