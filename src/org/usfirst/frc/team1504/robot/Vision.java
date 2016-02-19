package org.usfirst.frc.team1504.robot;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;

public class Vision
{
	private NetworkTable table;
	ADXRS450_Gyro gyro = new ADXRS450_Gyro();
	private static final Vision _instance = new Vision();
	double[] _cinputs = new double[2];
	double[] _ginputs = new double[2];
	
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
		table = NetworkTable.getTable("GRIP/contours");
	}
	
	public boolean getAimed(double center)
	{
		if(Math.abs(center) > Map.VISION_DEADZONE)
			return false;
		return true; //true means robot is aimed
	}
	
	public boolean checkCamera()
	{
		//double [] camera = useCameraData();
		//double [] gyro = aim();
		//if(Math.abs(gyro[1]-camera[1]) > Map.VISION_DEADZONE)
		
		if(Math.abs(_gangle - _cangle) > Map.VISION_DEADZONE) //checking angles.
		{
			//_updateState = true; //recheck camera
			return false; //doesnt match camera
		}
		else
			return true;
	}
	
	public void useCameraData()
	{	
		_updateState = true;
		_aimed = false;
		double[] height;
		double[] width;	
		double[] xCenter;
		
		double scaledPosition_x = 0; 
		int index = 0;
		double center = 0;					
		
		double[] defaultValue = {0.0};
		
		height = table.getNumberArray("height", defaultValue);
		width = table.getNumberArray("width", defaultValue);	
		xCenter = table.getNumberArray("centerX", defaultValue);
		
		System.out.println("height[0] is  " + height[0]);
		
		if(height[0] > 0) //if there is something found
		{	
			_detected = true;
			System.out.println("loop");
			int widest = 0;
			for(int i = 0; i < height.length - 1; i++)
			{
				if(width[i] > width[i+1])
					widest = i; //make sure we are centered to the correct ie widest contour
			}
			index = widest;
		}
		else
			_cinputs[1] = Map.VISION_TURN_SPEED; //turn until see something
		
		center = xCenter[index];

		//scaledPosition_x = (2*(center/Map.VISION_WIDTH) - 1);
		scaledPosition_x = (67*(center/Map.VISION_WIDTH) - 33.5); //angle of target
		
		//if(getAimed(center))
		if(checkCamera())
		{
			_aimed = true;
			System.out.println("aimed inside camera method");
		}
		
		else //not aimed correctly
		{
			_updateState = false;
			gyro.reset();
			System.out.println("adjust with gyro");
		}
		
		_cangle = scaledPosition_x;
		//return _cinputs;
	}
	
	public double predictPosition(double speed)
	{
		pos = speed * Map.VISION_DELAY_TIME * Map.VISION_CORRECTION_DELAY_TIME;	
		return pos;
	}
	
	public double[] aim() //using gyro
	{	
		if(_detected)
		{
			_updateState = false;
			//double [] inputs = new double[2];		
			//double[] target = table.getNumberArray("centerX", defaultValue);
			double angle = 0;
			double scaledAngle = 0;
			
			/*double scaledTarget = (67*(target[index]/Map.VISION_WIDTH) - 33.5); //angle of target
			System.out.println("target angle is   " + scaledTarget);
			
			angle = gyro.getAngle();
			scaledAngle = 67*(gyro.getAngle()/360) - 33.5; //scale angle
			System.out.println("gyro angle is   " + scaledAngle);
			
			double scaledPosition_x = (2*(target[index]/Map.VISION_WIDTH) - 1);*/
			//angle = gyro.getAngle();
			_gangle = gyro.getAngle();

			//scaledAngle = 67*(angle/360) - 33.5; //scale angle
			
			/*if(Math.abs(scaledAngle) - Math.abs(scaledTarget) > Map.VISION_DEADZONE) //turn to where the gyro angle is where the target was
			{
				
				inputs[1] = -scaledPosition_x * Map.VISION_GAIN_ADJUST_X;
			}*/
			/*if(getAimed(scaledAngle))
			{
				_aimed = true;
				time = System.currentTimeMillis();		
			}*/
			if(checkCamera())
			{
				_aimed = true;
				time = System.currentTimeMillis();	
			}
			else
			{
				//_gangle = scaledAngle;// * Map.VISION_GAIN_ADJUST_X;
				_ginputs[1] = -(_cangle - _gangle) * Map.VISION_GAIN_ADJUST_X;
			//_ginputs[0] = scaledAngle;
			}
		}
		else
			_ginputs[1] = Map.VISION_TURN_SPEED;
		
		System.out.println("aimed inside gyro method");
		System.out.println("gyro inputs are " + _ginputs[1]);
		System.out.println("gangle is " + _gangle);


		return _ginputs;
	}
	
	public double[] update()
	{
		double [] inputs = new double[2];
		//double scaledPosition_x = 0; 
		//double scaledPosition_y = 0; 

		//if(height[0] > 0) //if there is something found and its viable
		//{
		
		if(_updateState)
			useCameraData();
		inputs = aim();
		
		System.out.println("inputs are " + inputs[1]);

		
		if(_aimed) 
		{
			if(checkCamera() && (System.currentTimeMillis() - time) >= 1000) //1 sec has passed 
			{
				//_aimed = true;
				inputs[1] = 0;
				System.out.println("robot has successfully aimed!");
				_detected = false; //as in don't keep moving to find it, start new vision loop 
			}
		}
		return inputs;
	}
}