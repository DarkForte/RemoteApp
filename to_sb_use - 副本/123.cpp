#include<iostream>
#include<opencv2/opencv.hpp>

using namespace std;
using namespace cv;

int hehe()
{
	Mat src(Size(600,600),CV_8UC1,Scalar(0));
	Mat dst;
	namedWindow("src",1);
	namedWindow("dst",1);
	unsigned char *p;
	for(int i=100;i<120;i++)
	{
		p=src.ptr<unsigned char>(i);
		for(int j=100;j<200;j++)
		{
			p[j]=100;
			
		}
	}


	Canny( src, dst, 50, 200, 3 );
	//src.copyTo(dst);
	vector<Vec4i> lines;
	HoughLinesP( dst, lines, 1, CV_PI/180, 80, 25, 20 );
	for( size_t i = 0; i < lines.size(); i++ )
	{
			line(dst, Point(lines[i][0], lines[i][1]),Point(lines[i][2], lines[i][3]), Scalar(50), 2,8 );
	}
	imshow( "src",src);
	imshow( "dst",dst);
	while(1)
	{
		int keyval=waitKey(100);
		if(keyval==' ')break;
	}
		return 0;
}