package org.usfirst.frc.team1504.robot;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.DriverStation;
public class Vision
{
	static NetworkTable table;
	boolean updating;
	static boolean centered;
	static DriverStation ds;
	
	public Vision()
	{
		double[] inputs = new double[2];
		
		double[] height;
		double[] width;	
		int currentWidthIndex;
		
		double[] xCenter;
		double[] yCenter;
		
		double[] defaultValue = new double[1];
	}
	
	public static boolean visionUpdate()
	{
		if(IO.visionInputs() || ds.isAutonomous())
		{
			return true;
		}
		else
			return false;
	}
	
	public static double[] offset()
	{
		double[] inputs = new double[2];
		
		double[] height;
		double[] width;	
		int currentWidthIndex;
		
		double[] xCenter;
		double[] yCenter;
		
		double[] defaultValue = new double[1];
		defaultValue[0] = 0;
		
		height = table.getNumberArray("height", defaultValue);
		width = table.getNumberArray("width", defaultValue);
		
		if(height != defaultValue) //if there is something found
		{	
			if(width[1] != defaultValue[0]) //multiple contours detected
			{
				if(width[0] > width[1])
					currentWidthIndex = 0;//width[0]; //make sure we are centered to the right contour
				else
					currentWidthIndex = 1;//width[1];
			}
			currentWidthIndex = 0;
			xCenter = table.getNumberArray("centerX", defaultValue);
			yCenter = table.getNumberArray("centerY", defaultValue);
			
			if(xCenter[currentWidthIndex] < 380) //400-20; 20 is threshold
			{
				inputs[0] = 0;
				inputs[1] = .5;//turn right
			}
			
			if(xCenter[currentWidthIndex] > 420)
			{
				inputs[0] = 0;
				inputs[1] = -.5; //turn left
			}
			
			if(yCenter[currentWidthIndex] < 280) //300-20
			{
				inputs[0] = .5;
				inputs[1] = 0; 
			}
			
			if(yCenter[currentWidthIndex] > 320)
			{
				inputs[0] = -.5;
				inputs[1] = 0; 
			}
					
			if(Math.abs(400 - xCenter[currentWidthIndex]) <= 20 && Math.abs(300 - yCenter[currentWidthIndex]) <= 20) //TODO: threshold values
			{
				centered = true;
			}
			
			if(centered)
			{				
				if(height[0] < 35)// || area[0] > 3300)
				{
					inputs[0] = .5; //forward/backward is 0
					inputs[1] = 0;
				}
				
				if(height[0] > 65)// || area[0] > 3300)
				{
					inputs[0] = -.5;
					inputs[1] = 0;
				}
			}	
		}
		return inputs;
	}
}