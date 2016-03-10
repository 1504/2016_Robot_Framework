package org.usfirst.frc.team1504.robot;

public class IO
{

	private static Latch_Joystick _drive_y = new Latch_Joystick(Map.DRIVE_ARCADE_Y);
	private static Latch_Joystick _drive_w = new Latch_Joystick(Map.DRIVE_ARCADE_TURN);
	
	private static Latch_Joystick _secondary = new Latch_Joystick(Map.SECONDARY);

	public static final long ROBOT_START_TIME = System.currentTimeMillis();

	/**
	 * Drive stuff
	 */

	/**
	 * Handle getting joystick values
	 * 
	 */
	public static double[] tank_input()
	{
		double[] inputs = new double[2];

		inputs[0] = Map.DRIVE_INPUT_MAGIC_NUMBERS[0]
				* Math.pow(Utils.deadzone(_drive_y.getRawAxis(Map.JOYSTICK_Y_AXIS)), 2)
				* Math.signum(_drive_y.getRawAxis(Map.JOYSTICK_Y_AXIS));// forward/backward
																		// motion

		inputs[1] = Map.DRIVE_INPUT_MAGIC_NUMBERS[1]
				* Math.pow(Utils.deadzone(_drive_w.getRawAxis(Map.JOYSTICK_X_AXIS)), 2)
				* Math.signum(_drive_w.getRawAxis(Map.JOYSTICK_X_AXIS)) * .6;// turning
																					// left/right;
		return inputs;
	}
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
	public static boolean launch()
	{
		return _secondary.getRawButtonLatch(Map.SHOOTER_LAUNCH);
	}
	public static boolean disable_launch()
	{
		return _secondary.getRawButtonLatch(Map.SHOOTER_DISABLE_LAUNCH);
	}
}
