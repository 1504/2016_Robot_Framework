package org.usfirst.frc.team1504.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;

public class IO
{
	private static Latch_Joystick _drive_forward = new Latch_Joystick(Map.DRIVE_FORWARDRIGHT_JOYSTICK);
	private static Latch_Joystick _drive_rotation = new Latch_Joystick(Map.DRIVE_ROTATION_JOYSTICK);
	
	private static Latch_Joystick _drive_y = new Latch_Joystick(Map.DRIVE_ARCADE_Y);
	private static Latch_Joystick _drive_w = new Latch_Joystick(Map.DRIVE_ARCADE_TURN);
	
	public static Joystick joystickSecondary = new Joystick(0); //index later
	public static final long ROBOT_START_TIME = System.currentTimeMillis();

	
	public static Joystick joystickSecondary = new Joystick(2); //secondary joystick - port tbd

	static DriverStation ds;
	/**
	 * Drive stuff
	 */



	public void endGameInputs()
	{
		
	}

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
	

	public static double[] drive_input() {
		double[] inputs = new double[2];

		inputs[0] = Map.DRIVE_INPUT_MAGIC_NUMBERS[0] * Math.pow(Utils.deadzone(_drive_forward.getRawAxis(Map.JOYSTICK_Y_AXIS)), 2) * Math.signum(_drive_forward.getRawAxis(Map.JOYSTICK_Y_AXIS));// y
		inputs[1] = Map.DRIVE_INPUT_MAGIC_NUMBERS[1] * Math.pow(Utils.deadzone(_drive_rotation.getRawAxis(Map.JOYSTICK_X_AXIS)), 2) * Math.signum(_drive_rotation.getRawAxis(Map.JOYSTICK_X_AXIS));// w
		
		if(!_drive_rotation.getRawButton(Map.DRIVE_INPUT_TURN_FACTOR_OVERRIDE_BUTTON))
			inputs[1] *= Math.abs(inputs[0]) <= 0.01 ? 0.85 : Math.min((Math.abs(inputs[0]) + .05) / Map.DRIVE_INPUT_TURN_FACTOR, 1);
		
		return inputs;
	}
	
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
					* Math.signum(_drive_w.getRawAxis(Map.JOYSTICK_X_AXIS)) * -1;// turning
																					// left/right;
		} else
		{
			inputs[1] = 0.0;
		}
//TODO: Make sure the RIGHT SIDE is the one multiplied by -1.
		return inputs;
	}

	// public static double[] mecanum_input() {
	// double[] inputs = new double[3];
	//
	// inputs[0] = Map.DRIVE_INPUT_MAGIC_NUMBERS[0] *
	// Math.pow(Utils.deadzone(_drive_forwardright.getRawAxis(Map.JOYSTICK_Y_AXIS)),
	// 2) * Math.signum(_drive_forwardright.getRawAxis(Map.JOYSTICK_Y_AXIS));//
	// y
	// inputs[1] = Map.DRIVE_INPUT_MAGIC_NUMBERS[1] *
	// Math.pow(Utils.deadzone(_drive_forwardright.getRawAxis(Map.JOYSTICK_X_AXIS)),
	// 2) * Math.signum(_drive_forwardright.getRawAxis(Map.JOYSTICK_X_AXIS));//
	// x
	// inputs[2] = Map.DRIVE_INPUT_MAGIC_NUMBERS[2] *
	// Math.pow(Utils.deadzone(_drive_rotation.getRawAxis(Map.JOYSTICK_X_AXIS)),
	// 2) * Math.signum(_drive_rotation.getRawAxis(Map.JOYSTICK_X_AXIS));// w
	//
	// //inputs[0] = _drive_forwardright.getRawAxis(Map.JOYSTICK_Y_AXIS);
	// //inputs[1] = _drive_forwardright.getRawAxis(Map.JOYSTICK_X_AXIS);
	// //inputs[2] = _drive_rotation.getRawAxis(Map.JOYSTICK_X_AXIS);
	//
	// return inputs;
	// }

	public static double front_side()
	{
		if (_drive_w.getRawButtonLatch(Map.DRIVE_FRONTSIDE_BACK))
		{
			return -1.0;
		} else if (_drive_w.getRawButton(Map.DRIVE_FRONTSIDE_FRONT))
		{
			return 1.0;
		} else
		{
			return 0.0;
		}
	}

	/**
	 * Shooter stuff
	 */
	
	public static boolean intake_on()
	{
		return _secondary.getRawButtonLatch(Map.SHOOTER_INTAKE_ON);
	}
	
	public static boolean intake_off()
	{
		return _secondary.getRawButtonLatch(Map.SHOOTER_INTAKE_OFF);
	}
	
	public static boolean prep()
	{
		return _secondary.getRawButtonLatch(Map.SHOOTER_PREP);
	}
	
	public static boolean launch() //gonna do the thing
	{
		return _secondary.getRawButtonLatch(Map.SHOOTER_LAUNCH);
	}
}
