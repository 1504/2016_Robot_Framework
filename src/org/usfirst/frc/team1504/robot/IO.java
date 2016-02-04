package org.usfirst.frc.team1504.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;

public class IO
{	
	private static Latch_Joystick _drive_y = new Latch_Joystick(Map.DRIVE_ARCADE_Y);
	private static Latch_Joystick _drive_w = new Latch_Joystick(Map.DRIVE_ARCADE_TURN);
	
	public static final long ROBOT_START_TIME = System.currentTimeMillis();

	
	public static Latch_Joystick joystickSecondary = new Latch_Joystick(2); //secondary joystick - port tbd

	static DriverStation ds;
	
	public static int endGameInputs()
	{
		int state = 0;
		if(joystickSecondary.getRawButton(Map.ENDGAME_LIFT_BUTTON))
		{
			state = 1;
			//endGame.lift();
		}
		
		while(joystickSecondary.getRawButton(Map.OVERRIDE_LIFT_BUTTON))
		{
			if(joystickSecondary.getRawButton(Map.ENDGAME_LIFT_BUTTON)) //while override held down and lift pressed down
			{
				state = 2;
			}
		}
		
		return state;
	}
	
	public static boolean visionUpdate()
	{
		boolean vision = false;
		if(joystickSecondary.getRawButton(Map.VISION_BUTTON))
		{
			vision = true;
		}
		
		if(vision || ds.isAutonomous())
		{
			return true;
		}
		else
			return false;
	}
	
	/**
	 * Drive stuff
	 */
	public static double[] tank_input() {
		double[] inputs = new double[2];
		// TODO: Make sure the RIGHT SIDE is the one multiplied by -1.

		inputs[0] = Map.DRIVE_INPUT_MAGIC_NUMBERS[0]
				* Math.pow(Utils.deadzone(_drive_y.getRawAxis(Map.JOYSTICK_Y_AXIS)), 2)
				* Math.signum(_drive_y.getRawAxis(Map.JOYSTICK_Y_AXIS));// forward/backward
																		// motion

		if (_drive_w.getRawButton(Map.DRIVE_TURN_TOGGLE))
		{
			inputs[1] = Map.DRIVE_INPUT_MAGIC_NUMBERS[1]
					* Math.pow(Utils.deadzone(_drive_w.getRawAxis(Map.JOYSTICK_X_AXIS)), 2)
					* Math.signum(_drive_w.getRawAxis(Map.JOYSTICK_X_AXIS)) * 0.6;// turning
																					// left/right;
		} else
		{
			inputs[1] = 0.0;
		}
//TODO: Make sure the RIGHT SIDE is the one multiplied by -1.
		return inputs;
	}

	public static double front_side()
	{
		if (_drive_y.getRawButtonLatch(Map.DRIVE_FRONTSIDE_BACK))
		{
			return -1.0;
		} else if (_drive_y.getRawButton(Map.DRIVE_FRONTSIDE_FRONT))
		{
			return 1.0;
		} else
		{
			return 0.0;
		}
	}

	/**
	 * Lego shooter stuff
	 */
	
	public static Lego_Intake.ACTION_STATES setJoystickActionState()
	{
		Lego_Intake.ACTION_STATES state;
		if(joystickSecondary.getRawButton(Map.ACTION_STATE_PICKUP_IN_BUTTON))
		{
			state = Lego_Intake.ACTION_STATES.PICKUP_IN;
		}
		
		else if(joystickSecondary.getRawButton(Map.ACTION_STATE_PICKUP_OUT_BUTTON))
		{
			state = Lego_Intake.ACTION_STATES.PICKUP_OUT;
		}
		
		else if(joystickSecondary.getRawButton(Map.ACTION_STATE_RELOAD_BUTTON))
		{
			state = Lego_Intake.ACTION_STATES.RELOAD;
		}
		
		else if(joystickSecondary.getRawButton(Map.ACTION_STATE_READY_BUTTON))
		{
			state = Lego_Intake.ACTION_STATES.READY;
		}
		
		else if(joystickSecondary.getRawButton(Map.ACTION_STATE_FIRE_BUTTON))
		{
			state = Lego_Intake.ACTION_STATES.FIRE;
		}
		else
			state = null;
		return state;
	}
	
	public static Lego_Intake.MOTION_STATES setJoystickMotionState()
	{
		Lego_Intake.MOTION_STATES state;
		if(joystickSecondary.getRawButton(Map.MOTION_STATE_PICKUP_BUTTON))
		{
			state = Lego_Intake.MOTION_STATES.PICKUP;
		}
		
		else if(joystickSecondary.getRawButton(Map.MOTION_STATE_FIRE_BUTTON))
		{
			state = Lego_Intake.MOTION_STATES.FIRING;
		}
	
		else if(joystickSecondary.getRawButton(Map.MOTION_STATE_CLEAR_BUTTON))
		{
			state = Lego_Intake.MOTION_STATES.CLEAR;
		}
		else
			state = null;
		return state;
	}
	
	/**
	 * Ramp shooter stuff
	 */
	
	public static boolean intake_on()
	{
		return joystickSecondary.getRawButtonLatch(Map.SHOOTER_INTAKE_ON);
	}
	
	public static boolean intake_off()
	{
		return joystickSecondary.getRawButtonLatch(Map.SHOOTER_INTAKE_OFF);
	}
	
	public static boolean prep()
	{
		return joystickSecondary.getRawButtonLatch(Map.SHOOTER_PREP);
	}
	
	public static boolean launch()
	{
		return joystickSecondary.getRawButtonLatch(Map.SHOOTER_LAUNCH);
	}
}
