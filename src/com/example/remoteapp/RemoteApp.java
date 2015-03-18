package com.example.remoteapp;
import java.net.Socket;

import android.app.Application;

public class RemoteApp extends Application
{
	public String ip="192.168.1.233";
	public int port=12121;
	public Socket socket = null;
	public final int WIDTH=2048, HEIGHT=1536;
}
