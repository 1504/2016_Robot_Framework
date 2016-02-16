package org.usfirst.frc.team1504.robot;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.AnalogGyro;

public class Vision
{
	private NetworkTable table;
	AnalogGyro gyro = new AnalogGyro(1);
	private static final Vision _instance = new Vision();
	double[] _cinputs = new double[2];
	double[] _ginputs = new double[2];
	
	double _cangle = 0;
	double _gangle = 0;

	double pos = 0;
	
	boolean _updateState = true; //true = use camera
	boolean _aimed = false;
	
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
		if(Math.abs(_gangle - _cangle) > Map.VISION_DEADZONE) //yes this is right. checking angles.

			return false;
		else
			return true;
	}
	
	public double[] useCameraData()
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
		
		if(height[0] > 0) //if there is something found
		{	
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
			_cinputs[1] = .15; //turn until see something
		
		xCenter[index] = center;
		
		//scaledPosition_x = (2*(center/Map.VISION_WIDTH) - 1);
		scaledPosition_x = (67*(center/Map.VISION_WIDTH) - 33.5); //angle of target
		
		if(getAimed(center))
			_aimed = true;
		else //not aimed correctly
		{
			_updateState = false;
			gyro.reset();
		}
		
		_cangle = scaledPosition_x;
		return _cinputs;
	}
	
	public double predictPosition(double speed)
	{
		pos = speed * Map.VISION_DELAY_TIME * Map.VISION_CORRECTION_DELAY_TIME;	
		return pos;
	}
	
	public double[] aim() //using gyro
	{	
		_updateState = false;
		//double [] inputs = new double[2];		
		//double[] target = table.getNumberArray("centerX", defaultValue);
		double angle;
		double scaledAngle = 0;
		
		/*double scaledTarget = (67*(target[index]/Map.VISION_WIDTH) - 33.5); //angle of target
		System.out.println("target angle is   " + scaledTarget);
		
		angle = gyro.getAngle();
		scaledAngle = 67*(gyro.getAngle()/360) - 33.5; //scale angle
		System.out.println("gyro angle is   " + scaledAngle);
		
		double scaledPosition_x = (2*(target[index]/Map.VISION_WIDTH) - 1);*/
		angle = gyro.getAngle();
		scaledAngle = 67*(angle/360) - 33.5; //scale angle
		
		/*if(Math.abs(scaledAngle) - Math.abs(scaledTarget) > Map.VISION_DEADZONE) //turn to where the gyro angle is where the target was
		{
			
			inputs[1] = -scaledPosition_x * Map.VISION_GAIN_ADJUST_X;
		}*/
		if(getAimed(scaledAngle))
			_aimed = true;
		else
			_gangle = -scaledAngle * Map.VISION_GAIN_ADJUST_X;
		//_ginputs[0] = scaledAngle;
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
		
		if(_aimed) 
		{
			if(checkCamera()) 
			{
				//_aimed = true;
				inputs[1] = 0;
			}
		}
			//if(_aimed)
				//inputs[1] = 0;
			/*if(_updateState)
				_aimed = cameraCheck();

			*/
			
			
			
			//scaledPosition_x = (2*(xCenter[index]/Map.VISION_WIDTH)) - 1 + predictPosition(inputs[0]);
			
			//inputs = aim(index);
			//System.out.println("angle offset");
			/*if(getAimed(scaledPosition_x))  {
				inputs[1] = -scaledPosition_x * Map.VISION_GAIN_ADJUST_X;
			}
			else
				inputs[1] = 0;
			
			//if(getAimed(scaledPosition_y))
			//	inputs[0] = scaledPosition_y * Map.VISION_GAIN_ADJUST_Y;
			//else
				inputs[0] = 0;
			
			if(!getAimed(scaledPosition_y) && !getAimed(scaledPosition_x)){
				inputs[0] = 0;
				inputs[1] = 0;
			}*/
		//inputs[0] = 0;
		//inputs[1] = 0;
		return inputs;
	}
}