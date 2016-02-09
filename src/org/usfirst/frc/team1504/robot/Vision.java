package org.usfirst.frc.team1504.robot;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import org.usfirst.frc.team1504.robot.Update_Semaphore.Updatable;

public class Vision implements Updatable
{
	static NetworkTable table;
	
	private static class VisionTask implements Runnable{
		
		private Vision _v;
		
		public VisionTask(Vision v)
		{
			_v = v;
		}
		
		public void run()
		{
			_v.semaphore_update();
		}
	}
	
	private static Vision instance = new Vision();
	
	private Thread _vision_thread;
	
	public static Vision getInstance()
	{
		return instance;
	}
	
	protected Vision()
	{
		_vision_thread = new Thread(new VisionTask(this), "1504-Vision");
		_vision_thread.setPriority(Thread.NORM_PRIORITY + Thread.MAX_PRIORITY / 2);
		_vision_thread.start();
		
		Update_Semaphore.getInstance().register(this);//Vision.getInstance());
		
		System.out.println("Vision is watching");
		
		table = NetworkTable.getTable("GRIP/myContoursReport");
	}
	
	public static boolean getAimed(double center)
	{
		if(Math.abs(center) > Map.VISION_DEADZONE)
			return true;
		return false;
	}
	
	public static double predictPosition(double speed)
	{
		double pos = 0;
		pos = speed * Map.VISION_DELAY_TIME;
		
		return pos;
	}
	
	public static double[] offset()
	{
		double[] inputs = new double[2];

		//double[] height;
		//double[] width;	
		///double[] area;	
		int index;
		
		double[] xCenter;
		double[] yCenter;
		
		double[] defaultValue = new double[1];
		defaultValue[0] = 0;
		
		double[] height = table.getNumberArray("height", defaultValue);
		double[] width = table.getNumberArray("width", defaultValue);
		double[] area = table.getNumberArray("area", defaultValue);

		
		System.out.println("height's length is  " + height.length);
		
		System.out.println("height is   " + height[0]);
		System.out.println("width is   " + width[0]);

		System.out.println("area is   " + area[0]);

		
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
			
			scaledPosition_x = (1 - 2*(xCenter[index]/Map.VISION_WIDTH)) + predictPosition(inputs[0]);
			System.out.println("scaled x is   " + scaledPosition_x);
			scaledPosition_y = (1 - 2*(yCenter[index]/Map.VISION_HEIGHT)) + predictPosition(inputs[1]);
			System.out.println("scaled y is   " + scaledPosition_y);
						
			if(getAimed(scaledPosition_x))  {
				inputs[1] = scaledPosition_x * Map.VISION_GAIN_ADJUST_X;
				System.out.println("inputs have been mapped arrr   ");
			}
			else
				inputs[1] = 0;
			
			if(getAimed(scaledPosition_y))
				inputs[0] = scaledPosition_y * Map.VISION_GAIN_ADJUST_Y;
			else
				inputs[0] = 0;
		}
		return inputs;
	}
	
	public void semaphore_update()
	{
		if(IO.visionUpdate())
		{
			offset();
			System.out.println("semaphore in vision");

		}
	}
}