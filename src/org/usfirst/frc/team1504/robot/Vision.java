package org.usfirst.frc.team1504.robot;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import java.util.Timer;
import java.util.TimerTask;


public class Vision
{
	private NetworkTable table;
	ADXRS450_Gyro gyro = new ADXRS450_Gyro();
	private static final Vision _instance = new Vision();
	private Timer _imageWait;
	double[] _cinputs = new double[2];
	double[] _ginputs = new double[2];
	
	public enum CAMERA_STATES {WAIT_FOR_IMAGE, AIMED, NOT_AIMED, GET_IMAGE, NO_IMAGE};
	CAMERA_STATES _cstate;
	
	double _cangle = 0;
	double _gangle = 0;

	double pos = 0;
	
	boolean _updateState = true; //true = use camera
	boolean _aimed = false;
	boolean _detected = false;
	
	long time = 0;
	
	public static Vision getInstance()
	{
		return _instance;
	}
	
	protected Vision()
	{		
		System.out.println("Vision is watching");
		gyro.calibrate();
		_cstate = CAMERA_STATES.GET_IMAGE;
		table = NetworkTable.getTable("GRIP/contours");
	}
	
	public void settleCamera()
	{
		_cstate = CAMERA_STATES.WAIT_FOR_IMAGE;
		_imageWait = new Timer();	
		
		_imageWait.schedule(new TimerTask() {
			public void run () { 
				useCameraData(); 
			}
		}, 750);
	//useCameraData(); 

	}
	
	public CAMERA_STATES checkCamera()
	{
		CAMERA_STATES state;
		
		/*if(Math.abs(Math.abs(_cangle) - _gangle) > Map.VISION_DEADZONE) //checking angles. //_gangle
		{
			//_updateState = true; //recheck camera
			System.out.println("in checkCamera");
			System.out.println("dif in angles is  " + Math.abs(Math.abs(_cangle) - _gangle));
			return false; //doesnt match camera
		}
		else
			return true;*/
		
		System.out.println("angles are " + (_cangle - gyro.getAngle()));
		if(_cangle - gyro.getAngle() > Map.VISION_DEADZONE)
		{
			state = CAMERA_STATES.NOT_AIMED;
			//return false;
		}
		else
		{
			state = CAMERA_STATES.AIMED;
			//return true; //true means robot is aimed
		}
		return state;
	}
	
	public void useCameraData()
	{
		double[] height;
		double[] width;	
		double[] xCenter;
		int widest = 0;		
		
		double[] defaultValue = {0.0};
				
		height = table.getNumberArray("height", defaultValue);
		width = table.getNumberArray("width", defaultValue);	
		xCenter = table.getNumberArray("centerX", defaultValue);
		
		System.out.println("height[0] is  " + height[0]);
		
		if(height[0] > 0) //if there is something found
		{	
			System.out.println("detected in camera is true");

			System.out.println("loop");
			for(int i = 0; i < height.length - 1; i++)
			{
				if(width[i] > width[i+1])
					widest = i; //make sure we are centered to the correct ie widest contour
			}
		}
		else
		{
			//_cinputs[1] = 0;//Map.VISION_TURN_SPEED; //turn until see something
			_cstate = CAMERA_STATES.NO_IMAGE;
		}
		
	//	center = xCenter[widest];
	//	_cangle = scaledPosition_x;


		//scaledPosition_x = (2*(center/Map.VISION_WIDTH) - 1);
		_cangle = (67*(xCenter[widest]/Map.VISION_WIDTH) - 33.5); //angle of target
		
		//if(getAimed(center))
		if(checkCamera() == CAMERA_STATES.AIMED)
		{
			//_aimed = true;
			_cstate = CAMERA_STATES.AIMED;
			System.out.println("aimed with camera");
		}
		
		else //not aimed correctly
		{
			//_updateState = false;
			gyro.reset();
			_cstate = CAMERA_STATES.NOT_AIMED;
			System.out.println("not aimed - use gyro");
		}
	}

	public double[] aim() //using gyro
	{	
		if(_cstate == CAMERA_STATES.NOT_AIMED)
		{
		//	double angle = 0;
			//double scaledAngle = 0;
			
			//double scaledPosition_x = (2*(target[index]/Map.VISION_WIDTH) - 1);
			_gangle = gyro.getAngle();
			
			/*if(Math.abs(scaledAngle) - Math.abs(scaledTarget) > Map.VISION_DEADZONE) //turn to where the gyro angle is where the target was
			{
				
				inputs[1] = -scaledPosition_x * Map.VISION_GAIN_ADJUST_X;
			}*/
			/*if(getAimed(scaledAngle))
			{
				_aimed = true;
				time = System.currentTimeMillis();		
			}*/
			if(checkCamera() == CAMERA_STATES.AIMED)
				_cstate = CAMERA_STATES.AIMED;
			else
			{
				//_gangle = scaledAngle;// * Map.VISION_GAIN_ADJUST_X;
			    //_ginputs[1] = (Math.abs(_cangle) - Math.abs(_gangle)) * Map.VISION_GAIN_ADJUST_X;
				_ginputs[1] = -(_cangle - gyro.getAngle()) * Map.VISION_GAIN_ADJUST_X;
				if(checkCamera() == CAMERA_STATES.AIMED)
				{
					_cstate = CAMERA_STATES.GET_IMAGE;//NOT_AIMED;
					_ginputs[1] = 0;
				}
			}
		}
		//else
			//return new double[] {0.0,0.0};
		
		System.out.println("aimed inside gyro method");
		System.out.println("gyro inputs are " + _ginputs[1]);
		System.out.println("gangle is " + _gangle);
		
		return _ginputs;
	}
	
	public double[] update(boolean first)
	{
		double [] inputs = new double[2];
		
		if(first || _cstate == CAMERA_STATES.GET_IMAGE)
		{
			settleCamera();
			System.out.println("first or camera_state");
		}
		//inputs = aim();
		
		if(_cstate == CAMERA_STATES.NOT_AIMED)
			inputs[1] = -(_cangle - gyro.getAngle()) * Map.VISION_GAIN_ADJUST_X;
		
		if(checkCamera() == CAMERA_STATES.AIMED)
		{
			_cstate = CAMERA_STATES.AIMED;
			inputs[1] = 0;
		}
		else
		{
			//_gangle = scaledAngle;// * Map.VISION_GAIN_ADJUST_X;
		    //_ginputs[1] = (Math.abs(_cangle) - Math.abs(_gangle)) * Map.VISION_GAIN_ADJUST_X;
			/*inputs[1] = -(_cangle - gyro.getAngle()) * Map.VISION_GAIN_ADJUST_X;
			if(checkCamera() == CAMERA_STATES.AIMED)
			{
				_cstate = CAMERA_STATES.GET_IMAGE;//NOT_AIMED;
				inputs[1] = 0;
			}*/
			
			_cstate = CAMERA_STATES.GET_IMAGE;
		}
		
		System.out.println("inputs are " + inputs[1]);
		
	/*	if(_cstate == CAMERA_STATES.AIMED) 
		{
			if(checkCamera() == CAMERA_STATES.AIMED)
			{
				inputs[1] = 0;
				System.out.println("robot has successfully aimed!");
			}
		}*/
		return inputs;
	}
}