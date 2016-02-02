package org.usfirst.frc.team1504.robot;

import edu.wpi.first.wpilibj.Joystick;

public class IO {

	private static Latch_Joystick _drive_y = new Latch_Joystick(Map.DRIVE_ARCADE_Y);
	private static Latch_Joystick _drive_w = new Latch_Joystick(Map.DRIVE_ARCADE_TURN);
	
	private static Latch_Joystick _secondary = new Latch_Joystick(Map.SECONDARY);

	public static final long ROBOT_START_TIME = System.currentTimeMillis();
<<<<<<< Updated upstream
=======
	
	public static Joystick joystickSecondary = new Joystick(2); //secondary joystick - port tbd
>>>>>>> Stashed changes

	/**
	 * Drive stuff
	 */

	/**
	 * Handle getting joystick values
	 * 
	 * @return
	 */
<<<<<<< Updated upstream

	public static double[] tank_input()
	{
=======
	public void endGameInputs()
	{
		
	}

	public static double[] tank_input() {
>>>>>>> Stashed changes
		double[] inputs = new double[2];
		// TODO: Make sure the RIGHT SIDE is the one multiplied by -1.

		inputs[0] = Map.DRIVE_INPUT_MAGIC_NUMBERS[0]
				* Math.pow(Utils.deadzone(_drive_y.getRawAxis(Map.JOYSTICK_Y_AXIS)), 2)
				* Math.signum(_drive_y.getRawAxis(Map.JOYSTICK_Y_AXIS));// forward/backward
																		// motion
<<<<<<< Updated upstream

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
=======
		inputs[1] = Map.DRIVE_INPUT_MAGIC_NUMBERS[1]
				* Math.pow(Utils.deadzone(_drive_w.getRawAxis(Map.JOYSTICK_X_AXIS)), 2)
				* Math.signum(_drive_w.getRawAxis(Map.JOYSTICK_X_AXIS)) * -1;// turning
																				// left/right;

>>>>>>> Stashed changes
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

<<<<<<< Updated upstream
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
=======
	public static boolean front_side() {
		if (_drive_w.getRawButtonLatch(Map.DRIVE_FRONTSIDE_BACK)) {
			return true;
		}
		return false;
>>>>>>> Stashed changes
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
