# Makefile for the socket programming example
#

simple_server_objects = ServerSocket.o Socket.o SimpleServer.o


all : simple_server

simple_server: $(simple_server_objects)
	g++ -o simple_server $(simple_server_objects) -lpthread

Socket: Socket.cpp
ServerSocket: ServerSocket.cpp
simple_server_main: SimpleServer.cpp


clean:
	rm -f *.o simple_server