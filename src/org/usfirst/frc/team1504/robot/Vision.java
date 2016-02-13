package org.usfirst.frc.team1504.robot;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class Vision
{
	private NetworkTable table;

	private static final Vision _instance = new Vision();
	double[] inputs = new double[2];
	double pos = 0;
	public static Vision getInstance()
	{
		return _instance;
	}
	
	protected Vision()
	{		
		System.out.println("Vision is watching");
		
		table = NetworkTable.getTable("GRIP/contours");
	}
	
	public boolean getAimed(double center)
	{
		if(Math.abs(center) > Map.VISION_DEADZONE)
			return true;
		return false;
	}
	
	public double predictPosition(double speed)
	{
		//double delay = speed > .3 ? .6 : .8;

		pos = speed * Map.VISION_DELAY_TIME * Map.VISION_CORRECTION_DELAY_TIME;	
		return pos;
	}
	
	public double[] offset()
	{
		double[] height;
		double[] width;	
		double[] area;	
		int index;
		
		double[] xCenter;
		double[] yCenter;
		
		double[] defaultValue = {0.0};
		//defaultValue[0] = 0;
		
		height = table.getNumberArray("height", defaultValue);
		width = table.getNumberArray("width", defaultValue);
			
		//System.out.println("height is   " + height[0]);
		
		double scaledPosition_x = 0; 
		double scaledPosition_y = 0; 

		if(height[0] != 0) //if there is something found
		{	
			System.out.println("entered if loop  " + height[0]);
			int widest = 0;
			for(int i = 0; i < height.length - 1; i++)
			{
				if(width[i] > width[i+1])
					widest = i;//width[0]; //make sure we are centered to the correct contour
			}
			
			index = widest;
			
			xCenter = table.getNumberArray("centerX", defaultValue);
			yCenter = table.getNumberArray("centerY", defaultValue);
			
			scaledPosition_x = (2*(xCenter[index]/Map.VISION_WIDTH)) - 1 + predictPosition(inputs[0]);
			System.out.println("scaled x is   " + scaledPosition_x);
			scaledPosition_y = (-2*(yCenter[index]/Map.VISION_HEIGHT)) - 1 + predictPosition(inputs[1]);
			System.out.println("scaled y is   " + scaledPosition_y);
						
			scaledPosition_x = (67*(xCenter[index]/Map.VISION_WIDTH) - 33.5);// + predictPosition(inputs[0]);

			if(getAimed(scaledPosition_x))  {
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
			}
		}
		//inputs[0] = 0;
		//inputs[1] = 0;
		return inputs;
	}
}