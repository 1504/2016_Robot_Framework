package org.usfirst.frc.team1504.robot;
	
import org.usfirst.frc.team1504.robot.Lego_Intake.ACTION_STATES;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Solenoid;

public class Utils
{
	static byte _dslog = 0; //double solenoid
	static byte _slog = 0; //solenoid
	static byte _aStateLog = 0;
	static byte _mStateLog = 0;
	public static byte double_to_byte(double input)
	{
		if(input < 0) {
            return (byte) (input * 128);
        } else {
            return (byte) (input * 127);
        }
	}

	public static double deadzone(double input) {
		if (Math.abs(input) < Map.UTIL_JOYSTICK_DEADZONE)
			return 0.0;
		return (input - Map.UTIL_JOYSTICK_DEADZONE * Math.signum(input)) / (1.0 - Map.UTIL_JOYSTICK_DEADZONE);
	}
	
	public static double distance(double x, double y)
	{
		return Math.sqrt(x * x + y * y);
	}
	
	public static byte byteLog(DoubleSolenoid solenoid)
	{
		//byte log = 0;
		if(solenoid.get().equals(DoubleSolenoid.Value.kOff))
		{
			_dslog += 1; //TODO: set these values
		}
		
		if(solenoid.get().equals(DoubleSolenoid.Value.kForward))
		{
			_dslog += 2;
		}
		
		if(solenoid.get().equals(DoubleSolenoid.Value.kReverse))
		{
			_dslog += 4;
		}
		
		return _dslog;
	}
	
	public static byte byteLogSingle(Solenoid solenoid)
	{
		//byte log = 0;
		if(!solenoid.get())
			_slog += 1;
		
		if(solenoid.get())
			_slog += 2;
		return _slog;
	}
	
	public static byte actionStates(Lego_Intake.ACTION_STATES state)
	{	
		//byte astate = 0;
		switch(state)
		{
		case READY:
			_aStateLog += 1;
		case PICKUP_IN:
			_aStateLog += 2;
		case PICKUP_OUT:
			_aStateLog += 4;
		case FIRE:
			_aStateLog += 8;
		}
		return _aStateLog;
	}
	
	public static byte motionStates(Lego_Intake.MOTION_STATES state)
	{	
		//byte mstate = 0;
		switch(state)
		{
		case PICKUP:
			_mStateLog += 1;
		case CLEAR:
			_mStateLog += 2;
		case FIRING:
			_mStateLog += 4;
		}
		return _mStateLog;
	}
}
