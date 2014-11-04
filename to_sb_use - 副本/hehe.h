#pragma once
#include<iostream>
#include<cmath>
#include<string.h>
#include<stdio.h>
#include<vector>
#include<stdlib.h>
#include<ctime>
#include "opencv2/core/core.hpp"
#include "opencv2/features2d/features2d.hpp"
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/calib3d/calib3d.hpp"
#include "opencv2/nonfree/nonfree.hpp"
#include<opencv2/opencv.hpp>
using namespace std;
using namespace cv;

struct node_for_E{
	double cx,cy,d;
};
struct myLine{
	double p,a;
	double e;
	bool flag;
};
struct dis_node{
	double a,d;
};
struct myPoint
{
	double x,y;
	myPoint(){}
	myPoint(double _x,double _y)
	{
		x = _x;y = _y;
	}
	myPoint operator -(const myPoint &b)const
	{
		return myPoint(x - b.x,y - b.y);
	}
	//叉积
	double operator ^(const myPoint &b)const
	{
		return x*b.y - y*b.x;
	}
	//点积
	double operator *(const myPoint &b)const
	{
		return x*b.x + y*b.y;
	}
	//绕原点旋转角度B（弧度值），后x,y的变化
	void transXY(double B)
	{
		double tx = x,ty = y;
		x = tx*cos(B) - ty*sin(B);
		y = tx*sin(B) + ty*cos(B);
	}
};


double Dist(myPoint a,myPoint b);