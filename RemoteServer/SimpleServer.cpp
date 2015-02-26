#include "ServerSocket.h"
#include "SocketException.h"
//#include "ztb.h"
#include <string>
#include <iostream>
#include <unistd.h>
#include <pthread.h>
#include <queue>
#include <string.h>
#include <stdio.h>
#include <math.h>
#include <stdlib.h>
#include <sstream>

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

stringstream converter;

static void *netInput(void *dat)
{
	double a,b;
	while(true)
	{
		string x;
		app_socket>>x;
		cout<<x;
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
	//ztb_play();
	
	cout<<"calcthread entered!"<<endl;
	int n=0;
	int x=0;
	int y=0;
	int dx = 30;
	int dy = 40;
	while(true)
	{
		n++;
		LOCK(output_lock);
		
		output_q.push(n);
		int i,j;
		for(i=1; i<=n; i++)
		{
			output_q.push(x);
			output_q.push(y);
			output_q.push(x+dx);
			output_q.push(y+dy);
			x+=50;
			y+=0;
		}
		
		UNLOCK(output_lock);
		sleep(2);
	}
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
					string buffer;
					int n = output_q.front();
					output_q.pop();
					
					converter.clear();
					converter<<n;
					converter>>buffer;
					string now_str = "Map ";
					now_str += buffer+"\n";
					
					cout<<now_str;
					app_socket<<now_str;
					
					now_str.clear();
					for(int i=1; i<=n; i++)
					{
						for(int j=1; j<=4; j++)
						{
							converter.clear();
							converter<<output_q.front();
							output_q.pop();
							converter>>buffer;
							now_str += buffer +" ";
						}
						now_str += "0\n";
						cout<<now_str;
						app_socket<<now_str;
						now_str.clear();
					}
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


