package org.usfirst.frc.team1504.robot;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import java.util.Timer;
import java.util.TimerTask;

public class visionTrack
{
	private static final visionTrack _instance = new visionTrack();
	public ADXRS450_Gyro _gyro = new ADXRS450_Gyro();
	public Timer _timer = new Timer();
	public NetworkTable _table;
	public enum VISION_STATES {AIMED, NOT_AIMED, NO_IMAGE, GET_IMAGE, WAIT_FOR_IMAGE};
	public VISION_STATES _state;
	public double _angle;
	
	protected visionTrack()
	{
		_table = NetworkTable.getTable("GRIP/contours");
		_gyro.calibrate();
		_state = VISION_STATES.WAIT_FOR_IMAGE;
		System.out.println("Vision is watching");
	}
	
	public static visionTrack getInstance()
	{
		return _instance;
	}
	
	public void settleCamera()
	{
		//_state = VISION_STATES.WAIT_FOR_IMAGE;
		_timer.schedule(new TimerTask() { 
			public void run() {
			cameraAim();
		}
		}, 750);
	}
	
	public boolean checkAim()
	{
		return (_angle - _gyro.getAngle() < Map.VISION_DEADZONE);
	}
	
	public void cameraAim()
	{
		double[] height;
		double[] width;	
		double[] xCenter;
		int widest = 0;		
		
		double[] defaultValue = {0.0};
				
		height = _table.getNumberArray("height", defaultValue);
		width = _table.getNumberArray("width", defaultValue);	
		xCenter = _table.getNumberArray("centerX", defaultValue);
		
		System.out.println("height[0] is  " + height[0]);
		
		if(height != defaultValue)
		{	
			System.out.println("entered height condition");
			for(int i = 0; i < height.length - 1; i++)
			{
				if(width[i] > width[i+1])
					widest = i;
			}
			
			_angle = (67*(xCenter[widest]/Map.VISION_WIDTH) - 33.5);
		}
		else
			_state = VISION_STATES.NO_IMAGE;
		
		if(Math.abs(_angle) < Map.VISION_DEADZONE)
			_state = VISION_STATES.AIMED;
		else
		{
			_gyro.reset();
			_state = VISION_STATES.NOT_AIMED;
		}
	}
	
	public double[] updateInputs(boolean first)
	{
		//double [] inputs = new double[2];
		if(first || _state == VISION_STATES.WAIT_FOR_IMAGE)
		{
			settleCamera();
		}
		
		System.out.println("angle is " + _angle);
		System.out.println("gyro angle is " + _gyro.getAngle());

		if(_state == VISION_STATES.NOT_AIMED)
		{
			return new double[] {0.0, -(_angle - _gyro.getAngle()) * Map.VISION_GAIN_ADJUST_X};
		}
		
		if(checkAim())
		{
			_state = VISION_STATES.AIMED;
			return new double[] {0.0, 0.0};
		}
		else
		{
			_state = VISION_STATES.WAIT_FOR_IMAGE;
			//return new double[] {0.0, 0.0};
		}
		
		return new double[] {0.0, 0.0};
	}
}