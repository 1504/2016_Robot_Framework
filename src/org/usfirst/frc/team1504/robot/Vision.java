package org.usfirst.frc.team1504.robot;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.AnalogGyro;

public class Vision
{
	private NetworkTable table;
	AnalogGyro gyro = new AnalogGyro(1);
	private static final Vision _instance = new Vision();
	double[] inputs = new double[2];
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
	
	public double useCameraData()
	{
		_updateState = true;

		double[] height;
		double[] width;	
		double[] xCenter;
		
		double scaledPosition_x = 0; 
		int index = 0;
		double center = 0;					
		
		double[] defaultValue = {0.0};
		
		height = table.getNumberArray("height", defaultValue);
		width = table.getNumberArray("width", defaultValue);	

		if(height[0] > 0) //if there is something found
		{	
			int widest = 0;
			for(int i = 0; i < height.length - 1; i++)
			{
				if(width[i] > width[i+1])
					widest = i; //make sure we are centered to the correct ie widest contour
			}
			index = widest;
		}
		else
			inputs[1] = .15; //turn until see something
		
		xCenter = table.getNumberArray("centerX", defaultValue);
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
		return scaledPosition_x;
	}
	
	public double predictPosition(double speed)
	{
		pos = speed * Map.VISION_DELAY_TIME * Map.VISION_CORRECTION_DELAY_TIME;	
		return pos;
	}
	
	public double[] aim() //using gyro
	{	
		_updateState = false;
		double [] inputs = new double[2];		
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
			inputs[1] = -scaledAngle * Map.VISION_GAIN_ADJUST_X;

		return inputs;
	}
	
	public double[] update()
	{
		
		//double scaledPosition_x = 0; 
		//double scaledPosition_y = 0; 

		//if(height[0] > 0) //if there is something found and its viable
		//{	
			if(_aimed)
				inputs[1] = 0;
			
			if(_updateState)
				useCameraData();
			aim();
			
			
		
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