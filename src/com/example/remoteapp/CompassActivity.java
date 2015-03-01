package com.example.remoteapp;  
   
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;  
import android.content.res.Resources;
import android.hardware.Sensor;  
import android.hardware.SensorEvent;  
import android.hardware.SensorEventListener;  
import android.hardware.SensorManager;  
import android.os.Bundle;  
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;  
import android.widget.TextView;
import android.widget.Toast;


public class CompassActivity extends Activity implements SensorEventListener
{  
	SuperImageView imageView;  
	TextView text;
	TextView now;
	TextView cood;
	TextView ori;
	TextView calcResult;
	Button reconnectBtn;
	Button forwardBtn;
	Button backwardBtn;
	Button leftBtn;
	Button rightBtn;
	Button sweepBtn;
	Button cleanBtn;
	SensorManager mSensorManager;  
	
	String send_message;
	//////Sensor
	private float currentDegree=0f;  
	float originalDegree = 0;
	boolean lock=false;
	int detectTimes=0;
    
    RemoteApp the_app;
    Handler handler;
    
    LoginThread loginThread;
    NetThread netThread;
    
    PointType finger_point;
    //List<PointType> mapPoints;
    List<Segment> segments;
    
    PointType MAP_ORIGIN;
    
    final double UNKNOWN = -1e4;
    
    PointType sum_move = new PointType(0,0);
    //double sum_rotate = 0;
    
    //in order to place the picture in a proper place
    
    //double init_rotate = Math.atan(4.0/3.0);
    PointType picMove = new PointType(-100,0);
    
    class NetThread implements Runnable //get the information from the server
    {
    	//double dx, dy, rotate;
		@Override
		public void run() 
		{
			try 
			{
				BufferedReader in = new BufferedReader(new InputStreamReader(the_app.socket.getInputStream()));
				while(true)
				{
					String input = in.readLine();
					//System.out.println("1: "+input);

					if(input != null)
					{
				    	int n; 
						input = input.substring(4);
						n = Integer.parseInt(input);
						
						segments.clear();
						
						int i;
						for(i=1; i<=n; i++)
						{
					    	double point_data[] = new double[10];
					    	input = in.readLine();
					    	//System.out.println("2: "+input);
					    	
					    	String point_data_string[] = new String[10];
					    	point_data_string = input.split(" ");
					    	int j;
					    	for(j=0; j<=4;j++)
					    	{
					    		point_data[j] = Double.parseDouble(point_data_string[j]);
					    	}
					    	
					    	
					    	PointType s = new PointType(point_data[0], point_data[1]);
					    	PointType e = new PointType(point_data[2], point_data[3]);
					    	
					    	segments.add(new Segment(s,e));
					    	
						}
						
						//
						/*//System.out.println(input);
						
						dx = Double.parseDouble(input);
						input = in.readLine();
						dy = Double.parseDouble(input);
						input = in.readLine();
						rotate = Double.parseDouble(input);
						
						//System.out.println("dx: "+dx + " dy: "+dy + " rotate: "+rotate);
						//Modify start

						int i;
						
						PointType move_vec = new PointType(dx, dy);
						sum_rotate += rotate;
						move_vec = move_vec.spin(sum_rotate);
						sum_move.x += move_vec.x;
						sum_move.y += move_vec.y;
						
						//System.out.println("sum_move: "+sum_move);
						//System.out.println("sum_rotate: "+sum_rotate);
						//continue to read in

						while(true)
						{
							double x,y;
							input= in.readLine();
						//	System.out.println("input: "+input);
							x = Double.parseDouble(input);
							
							input= in.readLine();
						//	System.out.println("input: "+input);
							y = Double.parseDouble(input);
							
						//	System.out.println("x: "+x + " y: "+y);

							if(x<-1e4 && y< -1e4)
								break;

							PointType new_point = new PointType(x,y);
							
							//modify new point
							new_point = new_point.spin(sum_rotate);
							new_point.move(sum_move.x, sum_move.y);
							
							//in order to place the pic in a proper place
							new_point = new_point.spin(-init_rotate);
							new_point.x += picMove.x;
							new_point.y += picMove.y;
							
							mapPoints.add(new_point);
						}
						//System.out.println("ans: "+ans);

						imageView.setPointList(mapPoints);*/

						Message msg = new Message();
						msg.obj = "redraw";
						CompassActivity.this.handler.sendMessage(msg);
					}
				} 
			}
			catch (IOException e) 
			{
				System.out.println("NetThreadIOException");
				Message msg = new Message();
				msg.obj = "net error";
				CompassActivity.this.handler.sendMessage(msg);
			}
			catch (NullPointerException e)
			{
				//System.out.println("NetThreadIOException");
				Message msg = new Message();
				msg.obj = "net error";
				CompassActivity.this.handler.sendMessage(msg);
			}
		}
    }

    class LoginThread implements Runnable
	{
		@Override
		public void run() 
		{
			try 
			{
				//System.out.println(the_app.ip + " " + the_app.port);
				the_app.socket = new Socket();
				the_app.socket.connect(new InetSocketAddress(the_app.ip, the_app.port) , 5000);
				
				//open a netthread after logged in
				NetThread getThread = new NetThread();
				new Thread(getThread).start();
				
			} catch (UnknownHostException e) 
			{
				System.out.println("UnknownHostException");
				Message msg = new Message();
				msg.obj = "did not login";
				CompassActivity.this.handler.sendMessage(msg);
			} catch (IOException e) {
				System.out.println("IOException");
				Message msg = new Message();
				msg.obj = "did not login";
				CompassActivity.this.handler.sendMessage(msg);
			}
		}
	}
    
    class SendThread implements Runnable
    {
		@Override
		public void run() 
		{
			PrintWriter cout;
			try 
			{
				cout = new PrintWriter(the_app.socket.getOutputStream(),true);
				cout.println(send_message);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
    	
    }
    
    private void init()
    {
    	the_app = (RemoteApp) getApplicationContext();
    	MAP_ORIGIN = new PointType(the_app.WIDTH/2, the_app.HEIGHT/2);
	    
	    imageView=(SuperImageView)findViewById(R.id.picView);  
	    //text=(TextView)findViewById(R.id.textID);
	    //now=(TextView)findViewById(R.id.nowID);
	    cood = (TextView)findViewById(R.id.coodID);
	   // ori = (TextView)findViewById(R.id.oriID);
	    
	    reconnectBtn = (Button)findViewById(R.id.BtnID);
	    forwardBtn = (Button)findViewById(R.id.forwardID);
	    backwardBtn = (Button)findViewById(R.id.backwardID);
	    leftBtn = (Button)findViewById(R.id.leftID);
	    rightBtn = (Button)findViewById(R.id.rightID);
	    sweepBtn = (Button)findViewById(R.id.sweepID);
	    cleanBtn = (Button)findViewById(R.id.cleanID);
	    
	   // calcResult = (TextView)findViewById(R.id.CalcResultID);
	    mSensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);  
	    
	    segments = new ArrayList<Segment>();
	    
	    imageView.setMapOrigin(MAP_ORIGIN);
	    imageView.setPointList(segments);
	    
	    return;
    }
    
	@Override 
	protected void onCreate(Bundle savedInstanceState) 
	{  
	    super.onCreate(savedInstanceState);  
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.activity_compass);  
	    
	    init();
	    loginThread = new LoginThread();
	    new Thread(loginThread).start();
	    
	    handler = new Handler()
		{
			public void handleMessage(Message msg)
			{
				String words = (String)msg.obj;
				if( words.equals("ok") == true )
				{
					Toast.makeText(CompassActivity.this, "来自服务器的问候：登陆成功！！欢迎使用~",Toast.LENGTH_LONG).show();
					finish();
				}
				else if(words.equals("did not login") )
				{
					Toast.makeText(CompassActivity.this,"ip错了或者是主机没有开", Toast.LENGTH_SHORT).show();
				}
				else if(words.equals("redraw"))
				{
					//debug
					//System.out.println("Will now invalidate!");
					//int i;
					//for(i=0;i<mapPoints.size(); i++)
					//	System.out.println(mapPoints.get(i));
					
					imageView.setPointList(segments);
					imageView.invalidate();
				}
			}
		};
	    
		//Touch
	    imageView.setOnTouchListener(new OnTouchListener()
	    {
			@Override
			public boolean onTouch(View v, MotionEvent e) 
			{
				// TODO Auto-generated method stub
		        PointType point = new PointType();
		        point.x=e.getX();
		        point.y=e.getY();
		        //point = point.screenToView(currentDegree/180*Math.PI, MAP_ORIGIN);
		        finger_point = point;
		        
		        //send_message = (point.x-MAP_ORIGIN.x) + " "+ (point.y-MAP_ORIGIN.y);
		        send_message = point.toString();
		        SendThread send_thread = new SendThread();
		        new Thread(send_thread).start();
		        
		        imageView.setTouchPoint(finger_point);
		        imageView.invalidate();
                
                cood.setText("X: "+ e.getX() +" Y: " + e.getY() );
                
				return false;
			}
	    	
	    });
	    
	    reconnectBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0) 
			{
				LoginThread login_thread = new LoginThread();
				new Thread(login_thread).start();
			}
		});
	    
	    class BtnListener implements OnClickListener
	    {

			@Override
			public void onClick(View source) 
			{
				// TODO Auto-generated method stub
				if(source == forwardBtn)
				{
					send_message = "Forward\n";
				}
				else if(source == backwardBtn)
				{
					send_message = "Backward\n";
				}
				else if(source == leftBtn)
				{
					send_message = "Left\n";
				}
				else if(source == rightBtn)
				{
					send_message = "Right\n";
				}
				else if(source == sweepBtn)
				{
					send_message = "Start Clean\n";
				}
				else if(source == cleanBtn)
				{
					send_message = "Clean";
				}
				SendThread send_thread = new SendThread();
				new Thread(send_thread).start();
			}
	    	
	    }
	    
	    forwardBtn.setOnClickListener(new BtnListener());
	    backwardBtn.setOnClickListener(new BtnListener());
	    leftBtn.setOnClickListener(new BtnListener());
	    rightBtn.setOnClickListener(new BtnListener());
	    sweepBtn.setOnClickListener(new BtnListener());
	    cleanBtn.setOnClickListener(new BtnListener());
	}  
	
    @Override 
    protected void onResume() 
    {  
        mSensorManager.registerListener(this,mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_GAME);  
        super.onResume();  
    }  
       
    @Override 
    protected void onPause() 
    {  
        mSensorManager.unregisterListener(this);  
        super.onPause();  
    }  
   
    @Override 
    protected void onStop() 
    {  
        mSensorManager.unregisterListener(this);  
        super.onStop();  
    }  
    
    protected void onDestroy()
    {
    	send_message = "Exit\n";
    	SendThread send_thread = new SendThread();
    	new Thread(send_thread).start();
    	super.onDestroy();
    }
   
    @Override 
    public void onAccuracyChanged(Sensor arg0, int arg1) 
    {  
           
   
    }  
   
    //ImageView Rotate use
    @Override 
    public void onSensorChanged(SensorEvent event) 
    {  
	    
    	int sensortype=event.sensor.getType();  
	    switch(sensortype)
	    {  
		    case Sensor.TYPE_ORIENTATION:  
		        float degree=event.values[0]; 
		        if(lock==false)
		        {
		        	originalDegree = (degree + originalDegree)/2;
		        	detectTimes++;
		        	if(detectTimes == 100)
		        		lock=true;
		        	return;
		        }
		        imageView.setRotation(originalDegree - degree);
		        currentDegree=originalDegree-degree;  
		        break;
		
	    }  
   		
    }
    
    
   
}  