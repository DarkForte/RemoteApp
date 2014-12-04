#include "ServerSocket.h"
#include "SocketException.h"
#include "ztb.h"
#include <string>
#include <iostream>
#include <unistd.h>
#include <pthread.h>
#include <queue>
#include <string.h>
#include <stdio.h>
#include <math.h>
#include <stdlib.h>

#define LOCK(m) pthread_mutex_lock(&m)
#define TRYLOCK(m) pthread_mutex_trylock(&m)
// Trylock return 0 if successfully got a lock, 
// or describe the lock with other integers
#define UNLOCK(m) pthread_mutex_unlock(&m)
using namespace std;

pthread_t cin_thread, calc_thread;
pthread_mutex_t input_lock, output_lock;

queue <double> input_q;
queue <double> output_q;

ServerSocket app_socket;

const int GOT_LOCK = 0;
const double PI = acos(-1.0);
int trash;

static void *netInput(void *dat)
{
	double a,b;
	while(true)
	{
		string x;
		app_socket>>x;
		cout<<x<<endl;
		/*a = atof(x.c_str());
		cout<<"a: "<<a<<endl;
		
		app_socket>>x;
		cout<<x<<endl;
		b = atof(x.c_str());
		cout<<"b: "<<b<<endl;
		
		LOCK(input_lock); 
		cout<<"pushed into input_q!"<<endl;
		input_q.push(a);
		input_q.push(b);
		UNLOCK(input_lock);*/
	}
}


static void *calc(void *dat)
{
	ztb_play();
	
	/*freopen("data_for_sb.txt", "r", stdin);
	while(true)
	{
		LOCK(output_lock);
		double now1, now2, now3;
		int tmp = scanf("%lf %lf %lf", &now1, &now2, &now3);
		if(tmp<3)
		{
			UNLOCK(output_lock);
			continue;
		}
		
		cout<<"cin: "<<now1<<" "<<now2<<" "<<now3<<endl;
		output_q.push(now1);
		output_q.push(now2);
		output_q.push(now3);
		
		while(true)
		{
			double x,y;

			scanf("%lf %lf", &x, &y);
			output_q.push(x);
			output_q.push(y);
			if(x<-1e4 && y<-1e4)
				break;
		}
		UNLOCK(output_lock);
		
		usleep(1000000);//1s
	}*/
	
	/*double ans;
	while(true)
	{
	    LOCK(input_lock)
		
		if(!input_q.empty())
		{
			cout<<"input_q not empty!"<<endl;
			double a = input_q.front();
			input_q.pop();
			double b = input_q.front();
			input_q.pop();
			ans = a+b;
			
			LOCK(output_lock);
			cout<<"pushing to output q!"<<endl;
			output_q.push(ans);
			UNLOCK(output_lock);
		}
			
		UNLOCK(input_lock);	
	}*/
	
	/*int i=20;
	LOCK(mutex1);
	while(--i)
	{
	  cout<<"This is thread2"<<endl;
	  usleep(300000);
	}
	UNLOCK(mutex1);
	pthread_exit(NULL);*/

}

int main ( int argc, int argv[] )
{
	pthread_mutex_init(&input_lock, NULL);
	pthread_mutex_init(&output_lock, NULL);
	cout << "running....\n";

	try
	{
		// Create the socket
		ServerSocket server ( 12121 );
		cout<<"ServerSocket created."<<endl;

		while ( true )
		{
			cout<<"Accepting..."<<endl;
			server.accept ( app_socket );
			cout<<"connection detected!"<<endl;
			
			pthread_create(&cin_thread, NULL, netInput, NULL);
			pthread_create(&calc_thread, NULL, calc, NULL);
			
			while ( true )
			{
				LOCK(output_lock);
				if( !output_q.empty())
				{
					double now = output_q.front();
					output_q.pop();
					cout<<now<<endl;
					
					char buffer[100]={};
					sprintf(buffer, "%lf\n", now);
					string now_str(buffer);
					app_socket<<now_str;
				}
				UNLOCK(output_lock);
			}
		}
	}
	catch ( SocketException& e )
	{
		cout << "Exception was caught:" << e.description() << "\nExiting.\n";
	}

	return 0;
}


