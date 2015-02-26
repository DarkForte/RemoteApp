#include "ServerSocket.h"
#include "SocketException.h"
#include <string>
#include <iostream>
using namespace std;

int main ( int argc, int argv[] )
{
  cout << "running....\n";
   
  try
    {
      // Create the socket
      ServerSocket server ( 12121 );

      while ( true )
	{

	  ServerSocket new_sock;
	  server.accept ( new_sock );
	  cout<<"connection detected!"<<endl;

	  try
	    {
	      while ( true )
		{
		  string data;
		  new_sock >> data;
		  cout << data;
		}
	    }
	  catch ( SocketException& ) {}

	}
    }
  catch ( SocketException& e )
    {
      cout << "Exception was caught:" << e.description() << "\nExiting.\n";
    }

  return 0;
}
