package org.usfirst.frc.team1504.robot;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
public class Vision
{
	static NetworkTable table;
	boolean updating;
	static boolean centered;
	
	public Vision()
	{
		
	}
	
	public static boolean getAimed(double center)
	{
		if(Math.abs(center) > Map.VISION_DEADZONE)
			return true;
		return false;
	}
	
	public static double[] offset()
	{
		double[] inputs = new double[2];

		double[] height;
		double[] width;	
		int index;
		
		double[] xCenter;
		double[] yCenter;
		
		double[] defaultValue = new double[1];
		defaultValue[0] = 0;
		
		height = table.getNumberArray("height", defaultValue);
		width = table.getNumberArray("width", defaultValue);
		
		double scaledPosition_x = 0; 
		double scaledPosition_y = 0; 

		if(height != defaultValue) //if there is something found
		{	
			int widest = 0;
			for(int i = 0; i < height.length - 1; i++)
			{
				if(width[i] > width[i+1])
					widest = i;//width[0]; //make sure we are centered to the right contour
			}
			
			index = widest;
			
			/*if(width[1] != defaultValue[0]) //multiple contours detected
			{
				if(width[0] > width[1])
					index = 0;//width[0]; //make sure we are centered to the right contour
				else
					index = 1;//width[1];
			}*/
			
			//index = 0;
			
			xCenter = table.getNumberArray("centerX", defaultValue);
			yCenter = table.getNumberArray("centerY", defaultValue);
			
			scaledPosition_x = 1 - 2*(xCenter[index]/Map.VISION_WIDTH);
			scaledPosition_y = 1 - 2*(yCenter[index]/Map.VISION_HEIGHT);
			
			if(getAimed(scaledPosition_x))
				inputs[1] = scaledPosition_x * Map.VISION_GAIN_ADJUST_X;
			else
				inputs[1] = 0;
			
			if(getAimed(scaledPosition_y))
				inputs[0] = scaledPosition_y * Map.VISION_GAIN_ADJUST_Y;
			else
				inputs[0] = 0;
					
			/*if(xCenter[index] < 380) //400-20; 20 is threshold
			{
				inputs[0] = 0;
				inputs[1] = .5;//turn right
			}
			
			if(xCenter[index] > 420)
			{
				inputs[0] = 0;
				inputs[1] = -.5; //turn left
			}
			
			if(yCenter[index] < 280) //300-20
			{
				inputs[0] = .5;
				inputs[1] = 0; 
			}
			
			if(yCenter[index] > 320)
			{
				inputs[0] = -.5;
				inputs[1] = 0; 
			}
					
			if(Math.abs(400 - xCenter[index]) <= 20 && Math.abs(300 - yCenter[index]) <= 20) //TODO: threshold values
			{
				centered = true;
			}
			
			if(!getAimed(scaledPosition_x))//centered)
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
			}	*/
		}
		return inputs;
	}
}