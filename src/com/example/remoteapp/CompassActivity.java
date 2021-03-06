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

import com.zero.clientsocketmanager.ClientSocketManager;

import android.app.Activity;  
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.hardware.Sensor;  
import android.hardware.SensorEvent;  
import android.hardware.SensorEventListener;  
import android.hardware.SensorManager;  
import android.os.Bundle;  
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
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

/**
 * @author Zero
 *
 */

public class CompassActivity extends Activity implements SensorEventListener
{  
	MapImageView imageView;  
	DrawImageView drawView;
	TextView text;
	TextView now;
	TextView cood;
	TextView ori;
	TextView calcResult;
	Button forwardBtn;
	Button backwardBtn;
	Button leftBtn;
	Button rightBtn;
	Button clearBtn;
	Button cleanBtn;
	Button reserveBtn;
	Button res_okBtn;
	Button res_cancelBtn;
	Button returnBtn;
	Button reset_mapBtn;
	Button timeBtn;
	Button show_infoBtn;
	Button resetBtn;
	
	SensorManager mSensorManager;  
	GestureDetector gesture_detector;
	
	String send_message;
	float originalDegree;
	boolean direction_lock;
	int detectTimes;
	float degree;
    
    RemoteApp the_app;
    Handler handler;
    ClientSocketManager socket_manager;
    
    PointType draw_point;
    List<Segment> segments;
    PointType car_point;
    
    PointType MAP_ORIGIN;
        
    PointType sum_move = new PointType(0,0);
    
    Bitmap origin_bmp;
    
    boolean reserve_mode;
    
    List<Button> default_buttons;
    
    ConvexHull convex_hull;
    
    /**
     * Network Threads
     */
    
    class GetThread implements Runnable //get the information from the server
    {
		@Override
		public void run() 
		{
			try 
			{
				BufferedReader in = new BufferedReader
						(new InputStreamReader(socket_manager.getSocket().getInputStream()));
				while(true)
				{
					String input = in.readLine();
					if(input != null)
					{
						if(input.startsWith("Map"))
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
						}
						else if(input.startsWith("Self"))
						{
							String input_string[] = new String[10];
							input_string = input.split(" ");
							double x = Double.parseDouble(input_string[1]);
							double y = Double.parseDouble(input_string[2]);
							car_point = new PointType(x,y);
						}
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
				return;
			}
			catch (NullPointerException e)
			{
				//System.out.println("NetThreadIOException");
				Message msg = new Message();
				msg.obj = "net error";
				CompassActivity.this.handler.sendMessage(msg);
				return;
			}
		}
    }

    class MonitorThread implements Runnable
    {
    	boolean logged_in = false;
		@Override
		public void run() 
		{
			// TODO Auto-generated method stub
			while(logged_in == false)
			{
				logged_in = socket_manager.loginBlocked();
				if(logged_in==false)
				{
					try 
					{
						Thread.sleep(5000);
					} catch (InterruptedException e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			new Thread(new GetThread()).start();
		}
    }
    
    /**
     * Functions
     */
    private void initSensor()
    {
    	originalDegree = 0;
    	direction_lock=false;
    	detectTimes=0;
    	degree=0;
    }
    
    private void init()
    {
    	the_app = (RemoteApp) getApplicationContext();
    	MAP_ORIGIN = new PointType(the_app.WIDTH/2, the_app.HEIGHT/2);
	    
	    imageView = (MapImageView)findViewById(R.id.picView); 
	    imageView.setDoubleTapEnabled(false);
	    drawView = (DrawImageView)findViewById(R.id.drawViewID);
	    //cood = (TextView)findViewById(R.id.coodID);
	    
	    default_buttons = new ArrayList<Button>();
	    
	    forwardBtn = (Button)findViewById(R.id.forwardID);
	    backwardBtn = (Button)findViewById(R.id.backwardID);
	    leftBtn = (Button)findViewById(R.id.leftID);
	    rightBtn = (Button)findViewById(R.id.rightID);
	    clearBtn = (Button)findViewById(R.id.clearID);
	    cleanBtn = (Button)findViewById(R.id.cleanID);
	    reserveBtn = (Button)findViewById(R.id.reserveID);
	    returnBtn = (Button)findViewById(R.id.returnID);
	    reset_mapBtn = (Button)findViewById(R.id.reset_mapID);
	    timeBtn = (Button)findViewById(R.id.timeBtn);
	    show_infoBtn = (Button)findViewById(R.id.showinfoBtn);
	    resetBtn = (Button)findViewById(R.id.resetBtn);
	    
	    default_buttons.add(forwardBtn);
	    default_buttons.add(backwardBtn);
	    default_buttons.add(leftBtn);
	    default_buttons.add(rightBtn);
	    default_buttons.add(clearBtn);
	    default_buttons.add(cleanBtn);
	    default_buttons.add(reserveBtn);
	    default_buttons.add(returnBtn);
	    default_buttons.add(reset_mapBtn);
	    default_buttons.add(timeBtn);
	    default_buttons.add(show_infoBtn);
	    default_buttons.add(resetBtn);
	    
	    res_okBtn = (Button)findViewById(R.id.res_okID);
	    res_cancelBtn = (Button)findViewById(R.id.res_cancelID);
	    
	    mSensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);  
	    gesture_detector = new GestureDetector(this, new GestureListener());
	    initSensor();
	    
	    segments = new ArrayList<Segment>();
	    car_point = new PointType(1024, 768);
	    
	    origin_bmp=BitmapFactory.decodeResource(this.getBaseContext().getResources(), 
	    		R.drawable.pic2048).copy(Bitmap.Config.ARGB_8888, true);
	    imageView.setPointList(segments);
	    imageView.setCarPoint(car_point);
	    imageView.drawMap(origin_bmp);
	    
	    reserve_mode = false;
	    return;
    }
    
	private void shutReserveMode()
	{
		drawView.setVisibility(View.GONE);
		res_okBtn.setVisibility(View.GONE);
		res_cancelBtn.setVisibility(View.GONE);
		
		int i;
		for(i=0;i<default_buttons.size();i++)
		{
			Button now = (Button)default_buttons.get(i);
			now.setVisibility(View.VISIBLE);
		}
		
		reserve_mode = false;
		mSensorManager.registerListener(this,mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_GAME);  
	}

	private void startMonitorThread()
	{
		MonitorThread mt = new MonitorThread();
		new Thread(mt).start();
	}
	
    @Override 
	protected void onCreate(Bundle savedInstanceState) 
	{  
	    super.onCreate(savedInstanceState);  
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.activity_compass);  
	    
	    init();
	    
	    handler = new Handler()
		{
			public void handleMessage(Message msg)
			{
				String words = (String)msg.obj;
				if( words.equals("ok") )
				{
					Toast.makeText(CompassActivity.this, "来自服务器的问候：登陆成功！！欢迎使用~",Toast.LENGTH_LONG).show();
				}
				else if(words.equals("did not login") )
				{
					Toast.makeText(CompassActivity.this,"ip错了或者是主机没有开", Toast.LENGTH_SHORT).show();
				}
				else if(words.equals("net error"))
				{
					Toast.makeText(CompassActivity.this,"网络连接断了", Toast.LENGTH_SHORT).show();
					startMonitorThread();
				}
				else if(words.equals("redraw"))
				{
					imageView.setPointList(segments);
					imageView.setCarPoint(car_point);
					imageView.drawMap(origin_bmp);
				}
			}
		};
		
		socket_manager = new ClientSocketManager(the_app.ip, the_app.port, handler);
		startMonitorThread();
	    
		imageView.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View arg0, MotionEvent e) 
			{
				// TODO Auto-generated method stub
				return gesture_detector.onTouchEvent(e);
			}
			
		});
		
	    reserveBtn.setOnClickListener(new OnClickListener()
	    {
			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				reserve_mode = true;
				int i;
				for(i=0; i<default_buttons.size();i++)
				{
					Button now = (Button)default_buttons.get(i);
					now.setVisibility(View.GONE);
				}
				
				res_okBtn.setVisibility(View.VISIBLE);
				res_cancelBtn.setVisibility(View.VISIBLE);
				
				drawView.loadWithMatrix( imageView.getImageViewMatrix() );
				drawView.setRotation(originalDegree - degree);
				drawView.setVisibility(View.VISIBLE);
				
				mSensorManager.unregisterListener(CompassActivity.this);
			}
	    	
	    });
	    
	    res_okBtn.setOnClickListener(new OnClickListener()
	    {
			@Override
			public void onClick(View arg0) 
			{
				final String LOG_CV = "convex";
				// TODO Auto-generated method stub
				drawView.save();
				
				///calc convexHull
				PointType p_array[] = (PointType[])drawView.points.toArray
						(new PointType[drawView.points.size()]);
				convex_hull = new ConvexHull(p_array);
				convex_hull.Graham(p_array.length);
				
				//Log.d(LOG_CV, "Avoid "+convex_hull.getNum());
				socket_manager.sendMessage("Avoid "+convex_hull.getNum()+"\n");
				int i;
				for(i=0; i<convex_hull.getNum();i++)
				{
					//Log.d(LOG_CV, convex_hull.getPoint(i).toString());
					socket_manager.sendMessage(convex_hull.getPoint(i).toString()+"\n");
				}
				drawView.setPoints(convex_hull.getPointsList());
				shutReserveMode();
			}
	    });
	    
	    res_cancelBtn.setOnClickListener(new OnClickListener()
	    {
			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				drawView.cancel();
				shutReserveMode();
			}
	    	
	    });
	    
	    reset_mapBtn.setOnClickListener(new OnClickListener()
	    {
			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				mSensorManager.unregisterListener(CompassActivity.this);
				imageView.setRotation(0);
				imageView.setImageMatrix(new Matrix());
				initSensor();
				mSensorManager.registerListener(CompassActivity.this,mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_GAME);  
			}
	    	
	    });
	    
	    class SendBtnListener implements OnClickListener
	    {
	    	boolean cleaning = false, clearing = false;
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
				else if(source == clearBtn)
				{
					if(clearing == false)
					{
						send_message = "Start Clear\n";
						clearing = true;
						clearBtn.setText("暂停空净");
					}
					else
					{
						send_message = "Stop Clear\n";
						clearing = false;
						clearBtn.setText("开始空净");
					}
				}
				else if(source == cleanBtn)
				{
					if(cleaning == false)
					{
						send_message = "Start Clean\n";
						cleaning = true;
						cleanBtn.setText("暂停清扫");
					}
					else
					{
						send_message = "Pause Clean\n";
						cleaning = false;
						cleanBtn.setText("开始清扫");
					}
				}
				else if(source == returnBtn)
				{
					send_message = "Return\n";
				}
				else if(source == resetBtn)
				{
					send_message = "Reset\n";
				}
				socket_manager.sendMessage(send_message);
			}
	    	
	    }
	    
	    forwardBtn.setOnClickListener(new SendBtnListener());
	    backwardBtn.setOnClickListener(new SendBtnListener());
	    leftBtn.setOnClickListener(new SendBtnListener());
	    rightBtn.setOnClickListener(new SendBtnListener());
	    clearBtn.setOnClickListener(new SendBtnListener());
	    cleanBtn.setOnClickListener(new SendBtnListener());
	    returnBtn.setOnClickListener(new SendBtnListener());
	    resetBtn.setOnClickListener(new SendBtnListener());
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
    	socket_manager.sendMessage("Exit\n");
    	super.onDestroy();
    }
   
    @Override 
    public void onAccuracyChanged(Sensor arg0, int arg1) 
    {  
    }  
   
    class GestureListener extends SimpleOnGestureListener
    {
		@Override
		public boolean onSingleTapUp(MotionEvent e) 
		{
			// TODO Auto-generated method stub
			
			//raw_point is the point relative to the view
	        PointType raw_point = new PointType();
	        raw_point.x=e.getX();
	        raw_point.y=e.getY();
	        Log.d("origin_point", "Origin TouchPoint: "+ raw_point);
	        
	        //point_on_map is the point with real coodinates (need to be changed)
	        PointType point_on_map = new PointType(raw_point.x, raw_point.y);
	        
	        Matrix transform = imageView.getImageViewMatrix();
	        Matrix transform_invert = new Matrix();
	        transform.invert(transform_invert);
            
	        point_on_map = point_on_map.transWithMatrix(transform_invert);
	        
	        socket_manager.sendMessage("Target "+ point_on_map.toString() + "\n");
	        
	       // cood.setText("X: "+ point_on_map.x +" Y: " + point_on_map.y );
	        
	        //draw_point is the point you ask to draw, which is the
	        //same as raw_point. No need to be changed since the
	        //Imageview do not change.
	        draw_point = raw_point;
	        imageView.setTouchPoint(draw_point);
	        imageView.startAnimator();
			return super.onSingleTapUp(e);
		}
    	
    }
    
    /**
     * Sensor use
     */
    @Override 
    public void onSensorChanged(SensorEvent event) 
    {  
    	
    	int sensortype=event.sensor.getType();  
	    switch(sensortype)
	    {  
		    case Sensor.TYPE_ORIENTATION:  
		        degree=event.values[0]; 
		        if(direction_lock==false)
		        {
		        	originalDegree = (degree + originalDegree)/2;
		        	detectTimes++;
		        	if(detectTimes == 100)
		        		direction_lock=true;
		        	return;
		        }
		        imageView.setRotation(originalDegree - degree);
			break;
		
	    }  
   		
    }
    
    
   
}  