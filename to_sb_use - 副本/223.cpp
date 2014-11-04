#include"hehe.h"
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
#pragma comment(linker, "/STACK:32000000,32000000")
using namespace std;
using namespace cv;



const double Hd=20.0;
const double eps = 1e-8;
const double PI = acos(-1.0);
int sgn(double x)
{
	if(fabs(x) < eps)return 0;
	if(x < 0)return -1;
	else return 1;
}
//*Á½µã¼ä¾àÀë
double Dist(myPoint a,myPoint b)
{
return sqrt((a-b)*(a-b));
}


node_for_E eqa[1001];

const int MAPN=800,MAPM=800;
const double ST_X=300.0,ST_Y=300.0;
int n_dis,n,pre_n,n_E;
dis_node dist[1001],pre_dist[1001];
myPoint dir0;
myPoint dir1;
myPoint st;
myPoint pmap[1001],big_pmap[10001];
myPoint pmap2[1001];
myLine pline[1001],pline2[1001];


myPoint pre_pmap[1001];
Mat dmap,line_map,pre_dmap,dmap2;
Mat big_dmap;
Mat dmap_with_line,dmap_with_line2;



bool get_dis()
{
	int i,j;

	n_dis=0;

	while(1)
	{
		n_dis++;
		dist[n_dis].a=-1.0;
		scanf("%lf %lf",&dist[n_dis].a,&dist[n_dis].d);
		if(dist[n_dis].a<-0.5)
		{
			n_dis--;
			break;
		}
	}

	if(n_dis<10)return false;

	return true;
}

void init()
{
	n=0;
	st.x=ST_X;
	st.y=ST_Y;

	dir0.x=3.5;dir0.y=1.2;

	dmap=Mat(Size(MAPN,MAPM),CV_8UC1,Scalar(0));
	namedWindow("dmap",1);
	dmap2=Mat(Size(MAPN,MAPM),CV_8UC1,Scalar(0));
	namedWindow("dmap2",1);
	pre_dmap=Mat(Size(MAPN,MAPM),CV_8UC1,Scalar(0));
	namedWindow("pre_dmap",1);
	line_map=Mat(Size(MAPN,MAPM),CV_8UC1,Scalar(0));
	namedWindow("line_map",1);

	big_dmap=Mat(Size(MAPN,MAPM),CV_8UC1,Scalar(0));
	namedWindow("big_dmap",1);

	dmap_with_line=Mat(Size(MAPN,MAPM),CV_8UC1,Scalar(0));
	namedWindow("dmap_with_line",1);
	dmap_with_line2=Mat(Size(MAPN,MAPM),CV_8UC1,Scalar(0));
	namedWindow("dmap_with_line2",1);
	return ;
}
void copy_pre_pmap_dist()
{
	int i,j;
	for(i=1;i<=n_dis;i++)
	{
		pre_pmap[i]=pmap[i];
		pre_dist[i]=dist[i];
	}
	return ;
}
void calc_dmap()
{
	int tmp_map[MAPN+10][MAPM+10];
	int i,j;
	double xx,yy;
	unsigned char *p;

	memset(tmp_map,0,sizeof(tmp_map));
	for(i=1;i<=n_dis;i++)
	{
		pmap[i].x=st.x+dist[i].d*cos(dist[i].a);
		pmap[i].y=st.y+dist[i].d*sin(dist[i].a);


		xx=st.x+dist[i].d*cos(dist[i].a);
		yy=st.y+dist[i].d*sin(dist[i].a);
		if(xx>-eps && xx<eps+MAPN && yy>-eps && yy<eps+MAPM)
		{
			tmp_map[(int)xx][(int)yy]=255;
		}
	}
	for(int i=0;i<MAPN;i++)
	{
		p=dmap.ptr<unsigned char>(i);
		for(int j=0;j<MAPM;j++)
		{
			if(i==((int)(st.x+eps)) && j==((int)(st.y+eps)))
			{
				p[j]=255;
			}
			else
			{
				p[j]=tmp_map[i][j];
			}
		}
	}

	return ;
}
void calc_line_map()
{
	int i,j;
	vector<Vec4i> lines;
	HoughLinesP( dmap, lines, 1, CV_PI/180, 1, 100, 20 );
	unsigned char *p;
	for(int i=0;i<MAPN;i++)
	{
		p=line_map.ptr<unsigned char>(i);
		for(int j=0;j<MAPM;j++)
		{
			p[j]=0;
		}
	}

	for(i = 0; i < lines.size(); i++ )
	{
			line(line_map, Point(lines[i][0], lines[i][1]),Point(lines[i][2], lines[i][3]), Scalar(255), 1,4 );
	}

	return ;
}
void show_dmap()
{
	imshow( "dmap",dmap);

	return ;
}

void show_line_map()
{
	imshow("line_map",line_map);

	return ;
}
/*
void get_fea_point()
{
	int i,j;
	//-- Step 1: Detect the keypoints using SURF Detector
	int minHessian = 400;

	SurfFeatureDetector detector(minHessian);

	//vector<KeyPoint> keypoints_object, keypoints_scene;

	//detector.detect( dmap, keypoints_object );
	//detector.detect( big_dmap, keypoints_scene );

  //-- Step 2: Calculate descriptors (feature vectors)
  SurfDescriptorExtractor extractor;

  Mat descriptors_object, descriptors_scene;

  extractor.compute( dmap, keypoints_object, descriptors_object );
  extractor.compute( big_dmap, keypoints_scene, descriptors_scene );

  //-- Step 3: Matching descriptor vectors using FLANN matcher
  FlannBasedMatcher matcher;
  vector< DMatch > matches;
  matcher.match( descriptors_object, descriptors_scene, matches );

  double max_dist = 0; double min_dist = 100;

  //-- Quick calculation of max and min distances between keypoints
  for( int i = 0; i < descriptors_object.rows; i++ )
  { double dist = matches[i].distance;
    if( dist < min_dist ) min_dist = dist;
    if( dist > max_dist ) max_dist = dist;
  }

  printf("-- Max dist : %f \n", max_dist );
  printf("-- Min dist : %f \n", min_dist );

  //-- Draw only "good" matches (i.e. whose distance is less than 3*min_dist )
  vector< DMatch > good_matches;

  for( int i = 0; i < descriptors_object.rows; i++ )
  { if( matches[i].distance < 3*min_dist )
     { good_matches.push_back( matches[i]); }
  }

  Mat img_matches;
  drawMatches( dmap, keypoints_object, big_dmap, keypoints_scene,
               good_matches, img_matches, Scalar::all(-1), Scalar::all(-1),
               vector<char>(), DrawMatchesFlags::NOT_DRAW_SINGLE_POINTS );

	return ;
}
*/
void move_big_map(double dx,double dy)
{
	int i,j,k;

	//st.x+=dx;
	//st.y+=dy;
	for(i=1;i<=n;i++)
	{
		big_pmap[i].x+=dx;
		big_pmap[i].y+=dy;
	}
	return ;
}
void rotate_big_map(double a)
{
	int i,j;
	myPoint temp;

	for(i=1;i<=n;i++)
	{
		temp.x=big_pmap[i].x-st.x;
		temp.y=big_pmap[i].y-st.y;
		temp.transXY(a);

		big_pmap[i].x=temp.x+st.x;
		big_pmap[i].y=temp.y+st.y;
	}
	return ;
}
void copymap_to_bigmap()
{
	int i,j;
	for(i=1;i<=n_dis;i++)
	{
		for(j=1;j<=n;j++)
		{
			if((big_pmap[j].x-pmap[i].x)*(big_pmap[j].x-pmap[i].x)+(big_pmap[j].y-pmap[i].y)*(big_pmap[j].y-pmap[i].y)<4.0)
				break;
		}

		if(j>n)
		{
			n++;
			big_pmap[n].x=pmap[i].x;
			big_pmap[n].y=pmap[i].y;

			cout<<big_pmap[n].x-st.x<<' '<<big_pmap[n].y-st.y<<endl;
		}
	}

	cout<<-1e8<<' '<<-1e8<<endl;
	return ;
}
void show_big_map()
{
	int tmp_map[MAPN+10][MAPM+10];
	int i,j,k;
	memset(tmp_map,0,sizeof(tmp_map));
	for(i=1;i<=n;i++)
	{
		if(big_pmap[i].x>-eps && big_pmap[i].x<((double)MAPN)-eps && big_pmap[i].y>-eps && big_pmap[i].y<((double)MAPM)-eps)
			tmp_map[(int)(big_pmap[i].x+eps)][(int)(big_pmap[i].y+eps)]=255;
	}

	unsigned char *p;
	for(int i=0;i<MAPN;i++)
	{
		p=big_dmap.ptr<unsigned char>(i);
		for(int j=0;j<MAPM;j++)
		{
			p[j]=tmp_map[i][j];
		}
	}

	imshow("big_dmap",big_dmap);

	return ;
}
void show_pre_dmap()
{
	int tmp_map[MAPN+10][MAPM+10];
	int i,j,k;
	memset(tmp_map,0,sizeof(tmp_map));
	for(i=1;i<=n_dis;i++)
	{
		if(pre_pmap[i].x>-eps && pre_pmap[i].x<((double)MAPN)-eps && pre_pmap[i].y>-eps && pre_pmap[i].y<((double)MAPM)-eps)
			tmp_map[(int)(pre_pmap[i].x+eps)][(int)(pre_pmap[i].y+eps)]=255;
	}

	unsigned char *p;
	for(int i=0;i<MAPN;i++)
	{
		p=pre_dmap.ptr<unsigned char>(i);
		for(int j=0;j<MAPM;j++)
		{
			p[j]=tmp_map[i][j];
		}
	}

	imshow("pre_dmap",pre_dmap);

	return ;
}
void show_dmap2()
{
	int tmp_map[MAPN+10][MAPM+10];
	int i,j,k;
	memset(tmp_map,0,sizeof(tmp_map));
	for(i=1;i<=pre_n;i++)
	{
		if(pmap2[i].x>-eps && pmap2[i].x<((double)MAPN)-eps && pmap2[i].y>-eps && pmap2[i].y<((double)MAPM)-eps)
			tmp_map[(int)(pmap2[i].x+eps)][(int)(pmap2[i].y+eps)]=255;
	}

	unsigned char *p;
	for(int i=0;i<MAPN;i++)
	{
		p=dmap2.ptr<unsigned char>(i);
		for(int j=0;j<MAPM;j++)
		{
			p[j]=tmp_map[i][j];
		}
	}

	imshow("dmap2",dmap2);

	return ;
}
void reset_pre_pmap()
{
	int i,j;
	bool flag[1001];
	double far_dis[2001];
	double new_sin;
	double new_cos;
	Point st2;
	st2.x=st.x+dir0.x;
	st2.y=st.y+dir0.y;
	dis_node dist2[1001];

	for(i=1;i<=n_dis;i++)
	{
		dist2[i].d=sqrt((pre_pmap[i].x-st2.x)*(pre_pmap[i].x-st2.x)+(pre_pmap[i].y-st2.y)*(pre_pmap[i].y-st2.y));
		new_sin=(pre_pmap[i].y-st2.y)/dist2[i].d;
		new_cos=(pre_pmap[i].x-st2.x)/dist2[i].d;

		if(new_sin>-eps)
		{
			dist2[i].a=acos(new_cos);
		}
		else
		{
			dist2[i].a=2*PI-acos(new_cos);
		}
	}

	memset(flag,0,sizeof(flag));


	for(i=0;i<=800;i++)
	{
		far_dis[i]=1e6;
	}
	//for(i=1;i<=n_dis;i++)
	//{
	//	far_dis[i*2-1]=1.1*dist[i].d;
	//	far_dis[i*2]=1.1*dist[i].d;
	//}

	int da;
	double pre_a=-1.0;
	for(i=1;i<=n_dis;i++)
	{
		if(dist2[i].a<pre_a || (pre_a<PI/90.0 && dist2[i].a>2*PI-PI/90.0))
		{
			continue;
		}

		da=floor(dist2[i].a/(0.5*PI/180.0)+eps);

		if(far_dis[da]<dist2[i].d)
		{
			continue;
		}

		da=ceil(dist2[i].a/(0.5*PI/180.0)+eps);

		if(far_dis[da]<dist2[i].d)
		{
			continue;
		}

		flag[i]=1;
		da=floor(dist2[i].a/(0.5*PI/180.0)+eps);
		far_dis[da]=min(far_dis[da],1.1*dist2[i].d);
		da=ceil(dist2[i].a/(0.5*PI/180.0)+eps);
		far_dis[da]=min(far_dis[da],1.1*dist2[i].d);
	}

	pre_n=0;
	for(i=1;i<=n_dis;i++)
	{
		if(flag[i]==1)
		{
			pre_n++;
			//pre_pmap[pre_n]=pre_pmap[i];
			pmap2[pre_n].x=pre_pmap[i].x;
			pmap2[pre_n].y=pre_pmap[i].y;
		}
	}

	return ;
}
void delete_unreliable_line(myPoint *poi,int n,myLine *line)
{
	int i,j;
	double s1=0,s2=0;
	double _b,_e;
	/*
	_b=_e=0.0;
	for(i=1;i<=n;i++)
	{
		_e+=line[i].e;
		_b+=abs(line[i].a-atan(poi[i].y/poi[i].x));
    }
	_e/=((double)n);
	_b/=((double)n);


	s1=s2=0.0;
	for(i=1;i<=n;i++)
	{
		s1+=(line[i].e-_e)*(line[i].e-_e);
		s2+=(abs(line[i].a-atan(poi[i].y/poi[i].x))-_b)*(abs(line[i].a-atan(poi[i].y/poi[i].x))-_b);
	}
	s1/=((double)n);
	s2/=((double)n);
	s1=sqrt(s1);
	s2=sqrt(s2);
	*/
	for(i=1;i<=n;i++)
	{
		if(line[i].e>2000.0 || abs(line[i].a-atan((poi[i].y-st.y)/(poi[i].x-st.x)))>PI/2.0)
		{
			line[i].flag=0;
		}
	}


	return ;
}
bool check_is_angle(int now,int dat,int n,myPoint *poi)
{
	int i,j;
	int ss,tt;

	if(now-dat<1)
	{
		ss=now-dat+n;
	}
	else
	{
		ss=now-dat;
	}

	if(now+dat>n)
	{
		tt=now+dat-n;
	}
	else
	{
		tt=now+dat;
	}

	myPoint p1,p2;
	myPoint s(0.0,0.0);
	p1=myPoint(poi[now].x-poi[ss].x,poi[now].y-poi[ss].y);
	p2=myPoint(poi[tt].x-poi[now].x,poi[tt].y-poi[now].y);

	double u;
	u=p1*p2/Dist(p1,s)/Dist(p2,s);

	if(acos(u)>PI/18.0)
	{
		return true;
	}


	return false;
}
void get_line_for_every_single_node(myPoint *poi,int n,myLine *line)
{
	int i,j;
	myPoint tmp[41];
	myPoint temp;
	myPoint n1,n2;
	double _x,_y;
	double sx2,sy2,sxy;
	//double ss.tt;
	int tail;

	for(i=1;i<=n;i++)
	{
		tail=1;
		for(j=i-10;j<=i+10;j++)
		{
			if(j<1)
			{
				tmp[tail]=poi[j+n];
			}
			else if(j>n)
			{
				tmp[tail]=poi[j-n];
			}
			else
			{
				tmp[tail]=poi[j];
			}

			tail++;
		}


		_x=_y=0.0;
		for(j=1;j<=tail-1;j++)
		{
			_x+=(tmp[j].x-st.x);
			_y+=(tmp[j].y-st.y);
		}
		_x/=((double)(tail-1));
		_y/=((double)(tail-1));

		sx2=sy2=sxy=0.0;
		for(j=1;j<=tail-1;j++)
		{
			sx2+=(tmp[j].x-st.x-_x)*(tmp[j].x-st.x-_x);
			sy2+=(tmp[j].y-st.y-_y)*(tmp[j].y-st.y-_y);
			sxy+=(tmp[j].x-st.x-_x)*(tmp[j].y-st.y-_y);
		}


		line[i].a=0.5*atan(-2*sxy/(sy2-sx2));
		temp.x=tmp[tail-1].x-tmp[1].x;
		temp.y=tmp[tail-1].y-tmp[1].y;

		n1.x=cos(line[i].a);
		n1.y=sin(line[i].a);
		if(line[i].a>0.0)
		{
			n2.x=cos(line[i].a-PI/2.0);
			n2.y=sin(line[i].a-PI/2.0);
		}
		else
		{
			n2.x=cos(line[i].a+PI/2.0);
			n2.y=sin(line[i].a+PI/2.0);
		}

		if(abs(n1*temp)>abs(n2*temp))
		{
			if(line[i].a>0.0)
			{
				line[i].a-=PI/2.0;
			}
			else
			{
				line[i].a+=PI/2.0;
			}
		}


		line[i].p=_x*cos(line[i].a)+_y*sin(line[i].a);
		line[i].e=0.5*(sx2+sy2-sqrt(4*sxy+(sy2-sx2)*(sy2-sx2)));
		line[i].flag=1;

		//if(i>=200 && i<=400)
		//cout<<line[i].e<<endl;



		if(check_is_angle(i,10,n,poi)==true)
		{
			line[i].flag=0;
		}

	}

	delete_unreliable_line(poi,n,line);


	return ;
}
void show_with_line(myPoint *poi,int n,myLine *lines)
{
	
	int tmp_map[MAPN+10][MAPM+10];
	int i,j,k;
	memset(tmp_map,0,sizeof(tmp_map));
	for(i=1;i<=n;i++)
	{
		if(poi[i].x>-eps && poi[i].x<((double)MAPN)-eps && poi[i].y>-eps && poi[i].y<((double)MAPM)-eps)
			tmp_map[(int)(poi[i].x+eps)][(int)(poi[i].y+eps)]=255;
	}

	unsigned char *p;
	for(int i=0;i<MAPN;i++)
	{
		p=dmap_with_line.ptr<unsigned char>(i);
		for(int j=0;j<MAPM;j++)
		{
			p[j]=tmp_map[i][j];
		}
	}

	for(i = 1; i <= n; i+=2 )
	{
		//cout<<lines[i].e<<endl;
		//cout<<poi[i].x<<' '<<poi[i].y<<endl;
		//Point(((int)(poi[i].x+poi[i].y*tan(lines[i].a)))
		if(lines[i].flag==1)
			line(dmap_with_line, Point(((int)poi[i].y),((int)poi[i].x)),Point(0.0,((int)(poi[i].x+poi[i].y*tan(lines[i].a)))), Scalar(255), 1,4 );
	}
	imshow("dmap_with_line",dmap_with_line);

	return ;
}
void show_with_line2(myPoint *poi,int n,myLine *lines)
{
	
	int tmp_map[MAPN+10][MAPM+10];
	int i,j,k;
	memset(tmp_map,0,sizeof(tmp_map));
	for(i=1;i<=n;i++)
	{
		if(poi[i].x>-eps && poi[i].x<((double)MAPN)-eps && poi[i].y>-eps && poi[i].y<((double)MAPM)-eps)
			tmp_map[(int)(poi[i].x+eps)][(int)(poi[i].y+eps)]=255;
	}

	unsigned char *p;
	for(int i=0;i<MAPN;i++)
	{
		p=dmap_with_line2.ptr<unsigned char>(i);
		for(int j=0;j<MAPM;j++)
		{
			p[j]=tmp_map[i][j];
		}
	}

	for(i = 1; i <= n; i+=2 )
	{
		//cout<<lines[i].e<<endl;
		//cout<<poi[i].x<<' '<<poi[i].y<<endl;
		//Point(((int)(poi[i].x+poi[i].y*tan(lines[i].a)))
		if(lines[i].flag==1)
			line(dmap_with_line2, Point(((int)poi[i].y),((int)poi[i].x)),Point(0.0,((int)(poi[i].x+poi[i].y*tan(lines[i].a)))), Scalar(255), 1,4 );
	}
	imshow("dmap_with_line2",dmap_with_line2);

	return ;
}


double get_angle(double x,double y)
{
	int i,j;
	double a;
	double nsin,ncos;
	double c=sqrt(x*x+y*y);
	nsin=asin(y/c);
	ncos=acos(x/c);

	//cout<<x<<' '<<y<<' '<<nsin<<' '<<ncos<<endl;

	if(nsin>-eps)
	{
		return ncos;
	}
	else
	{
		return 2*PI-ncos;
	}
}

void do_match_pro(myPoint *poi1,int n1,myLine *lines1,myPoint *poi2,int n2,myLine *lines2,double w)
{
	int i,j;

	double n1x,n1y,n2x,n2y;
	int s1,s2;
	int aim;
	double a1,a2,pre_a2;
	double x1,y1,x2,y2;
	myPoint temp;
	//s1=1;
	s2=n2-20;

	n_E=0;


	x2=poi2[s2].x-st.x;
	y2=poi2[s2].y-st.y;
	a2=get_angle(x2,y2);
	for(s1=1;s1<=n1;s1++)
	{
		x1=poi1[s1].x-st.x;
		y1=poi1[s1].y-st.y;

		a1=get_angle(x1,y1);
		a1=(a1+w);
		if(a1>2*PI)
		{
			a1-=2*PI;
		}
		if(a1<0)
		{
			a1+=2*PI;
		}


		//if(s1<=100)
		//	cout<<a1<<endl;
		//    cout<<s1<<' '<<s2<<' '<<a1<<' '<<a2<<"!!!"<<endl;
		while((a2<a1 || (a1<PI/4.0 && a2>PI/4.0*7.0)) && !(a2<PI/4.0 && a1>PI/4.0*7.0))
		{
			s2++;
			if(s2>n2)
			{
				s2-=n2;
			}
			x2=poi2[s2].x-st.x;
			y2=poi2[s2].y-st.y;
			pre_a2=a2;
			a2=get_angle(x2,y2);
	    }

		//if(s1<=100)
		//{
		//	cout<<pre_a2<<' '<<a2<<endl;
		//}

		if(pre_a2>PI/4.0*7.0 && a2<PI/4.0)
		{
			if(a1>PI/4.0*7.0)
			    a2+=PI*2;
			else
			    pre_a2-=PI*2;
		}

		if(abs(a2-a1)<abs(a1-pre_a2))
		{
			aim=s2;
		}
		else
		{
			aim=s2-1;
			if(aim<1)
			{
				aim+=n2;
			}
		}

		//temp=poi1[s1];
		//temp.x-=st.y;
		//temp.y-=st.y;
		//temp.transXY(w);

		//cout<<temp.x<<' '<<temp.y<<endl<<poi2[aim].x-st.x<<' '<<poi2[aim].y-st.y<<endl;
		//system("pause");
		//cout<<s1<<' '<<aim<<' '<<n1<<' '<<n2<<endl;
		//cout<<poi1[s1].x<<' '<<poi1[s1].y<<' '<<lines1[s1].p<<' '<<lines1[s1].a<<endl;
		//cout<<poi2[aim].x<<' '<<poi2[aim].y<<' '<<lines2[aim].p<<' '<<lines2[aim].a<<endl;
		//cout<<poi2[aim].x-poi1[s1].x<<' '<<poi2[aim].y-poi1[s1].y<<endl;
		//if(s1<=100)
			//cout<<"/////"<<endl;

		if(abs(lines2[aim].a-lines1[s1].a)<PI/10.0 && Dist(poi2[aim],poi1[s1])<Hd && lines2[aim].flag==1 && lines1[s1].flag==1)
		{
			
			//cout<<lines1[s1].a<<' '<<lines2[aim].a<<"!!"<<endl;
			n_E++;

			n1x=cos(lines1[s1].a);
			n1y=sin(lines1[s1].a);

			n2x=cos(lines2[aim].a);
			n2y=sin(lines2[aim].a);

			eqa[n_E].cx=cos(w)*n1x-sin(w)*n1y+n2x;
			eqa[n_E].cy=sin(w)*n1x+cos(w)*n1y+n2y;

			eqa[n_E].d=eqa[n_E].cx*(poi2[aim].x-st.x-cos(w)*(poi1[s1].x-st.x)+sin(w)*(poi1[s1].y-st.y))+eqa[n_E].cy*(poi2[aim].y-st.y-sin(w)*(poi1[s1].x-st.x)-cos(w)*(poi1[s1].y-st.y));
		}

	}


	return ;
}
////////////////////////////////////////////////////////////

double xmin,ymin;
double calc_xy(double x,double y)
{
	double e=0;
	int i;
	for(i=1;i<=n_E;i++)
	{
		e+=(eqa[i].cx*x+eqa[i].cy*y-eqa[i].d)*(eqa[i].cx*x+eqa[i].cy*y-eqa[i].d);
    }

	return e;
}

double get_min_E_x(double x1,double x2,double y)
{
	double x3,x4;

	while(x2-x1>1e-2)
	{
		x3=x1+1.0/3.0*(x2-x1);
		x4=x1+2.0/3.0*(x2-x1);

		if(calc_xy(x3,y)<calc_xy(x4,y))
		{
			x2=x4;
		}
		else
		{
			x1=x3;
		}
	}

	xmin=(x1+x2)/2.0;

	return calc_xy((x1+x2)/2.0,y);
}
double get_min_E_y(double y1,double y2,double x)
{
	double y3,y4;
	//cout<<x<<' '<<y1<<' '<<y2<<"YYYYY"<<endl;
	while(y2-y1>1e-2)
	{
		y3=y1+1.0/3.0*(y2-y1);
		y4=y1+2.0/3.0*(y2-y1);

		if(calc_xy(x,y3)<calc_xy(x,y4))
		{
			y2=y4;
		}
		else
		{
			y1=y3;
		}
	}

	ymin=(y1+y2)/2.0;
	return calc_xy(x,(y1+y2)/2.0);
}

void calc_dx_dy()
{
	int i,j;
	double x1,x2,y1,y2;
	double x3,x4,y3,y4;
	double xmid,ymid;
	double e1,e2;


	x1=dir0.x-2.0;
	x2=dir0.x+2.0;
	y1=dir0.y-2.0;
	y2=dir0.y+2.0;


	

	int io=0;

	while(x2-x1>1e-2 || y2-y1>1e-2)
	{
		//cout<<x1<<' '<<x2<<' '<<y1<<' '<<y2<<endl;
		xmid=(x1+x2)/2.0;
		ymid=(y1+y2)/2.0;

		if(io==0 || io==1)e1=get_min_E_x(x1,x2,ymid);
		if(io==0 || io==2)e2=get_min_E_y(y1,y2,xmid);

		//cout<<e1<<' '<<e2<<"*&^%$"<<endl;
		if(io==1 || (io==0 && e1<e2))
		{
			if(xmin>xmid)
			{
				x1=xmid;
			}
			else
			{
				x2=xmid;
			}

			io=2;
		}
		else
		{
			if(ymin>ymid)
			{
				y1=ymid;
			}
			else
			{
				y2=ymid;
			}

			io=1;
		}
	}

	dir1.x=(x1+x2)/2.0;
	dir1.y=(y1+y2)/2.0;
	return ;
}

void calc_dx_dy2()
{
	double i,j;
	int k;
	double sx=dir0.x,sy=dir0.y;
	double nx,ny;
	double temp;
	double dat=0.2;
	double min_dis;

	for(k=1;k<=3;k++)
	{

	min_dis=1e9;
	for(i=sx-10.0*dat;i<sx+10.0*dat;i+=dat)
	{
		for(j=sy-10.0*dat;j<sy+10.0*dat;j+=dat)
		{
			temp=calc_xy(i,j);
			if(temp<min_dis)
			{
				nx=i;ny=j;
				min_dis=temp;
			}
		}
	}

	sx=nx;sy=ny;
	dat/=10.0;

	}

	dir1.x=sx;
	dir1.y=sy;

	return ;
}


int main()
{
	int i,j,k;


	freopen("data_cir_wucha.txt","r",stdin);
	freopen("data_for_sb.txt","w",stdout);

	init();

	get_dis();//input dir0
	calc_dmap();
	show_dmap();

	while(1)
	{
		copy_pre_pmap_dist();
		if(get_dis()==false)continue;//input dir0
		calc_dmap();
		show_dmap();
		//calc_line_map();
		//show_line_map();

		//get_fea_point();
		reset_pre_pmap();
		show_pre_dmap();
		show_dmap2();

		//cout<<n_E<<endl;
		//cout<<n_dis<<' '<<pre_n<<endl;
		//cout<<st.x<<' '<<st.y<<endl;
		///////////////////////////////////////////////////////////////////////////
		get_line_for_every_single_node(pmap,n_dis,pline);
		get_line_for_every_single_node(pmap2,pre_n,pline2);

		show_with_line(pmap,n_dis,pline);
		show_with_line2(pmap2,pre_n,pline2);
		///////////////////////////////////////////////////////////////////////////

		/*
		for(i=1;i<=pre_n;i++)
		{
			if(pline2[i].a<-PI/2.0 || pline2[i].a>PI/2.0)
			{
				cout<<pline2[i].a<<"!!!"<<endl;
			}
		}
		*/

		do_match_pro(pmap,n_dis,pline,pmap2,pre_n,pline2,PI/180.0);

			//for(i=1;i<=n_E;i++)
			//{
			//	cout<<i<<' '<<eqa[i].cx<<' '<<eqa[i].cy<<' '<<eqa[i].d<<endl;
			//}
		/*
		double ii,jj;
		for(ii=-1.0;ii<=1.0;ii+=0.1)
		{
			for(jj=-1.0;jj<=1.0;jj+=0.1)
			{
				cout<<calc_xy(ii,jj)<<' ';
			}
			cout<<endl;
		}
		cout<<"(*&^%$#"<<endl;
		*/
			
		calc_dx_dy2();

		cout<<dir1.x<<' '<<dir1.y<<' '<<PI/180.0<<endl;



		rotate_big_map(-PI/180.0);
		move_big_map(-dir1.x,-dir1.y);
		

		copymap_to_bigmap();

		show_big_map();

		//cout<<st.x<<' '<<st.y<<endl;
		//cout<<dir0.x<<' '<<dir0.y<<endl;

		while(1)
		{
			int keyval=waitKey(100);
			if(keyval==' ')break;
		}
		
	}

	system("pause");
	return 0;
}
