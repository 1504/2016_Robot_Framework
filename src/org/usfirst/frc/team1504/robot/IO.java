package org.usfirst.frc.team1504.robot;

public class IO
{

	
	private static Latch_Joystick _drive_y = new Latch_Joystick(Map.DRIVE_ARCADE_Y);
	private static Latch_Joystick _drive_w = new Latch_Joystick(Map.DRIVE_ARCADE_TURN);
	
	public static final long ROBOT_START_TIME = System.currentTimeMillis();
	
	/**
	 * Drive stuff
	 */
	
	/**
	 * Handle getting joystick values
	 * @return
	 */
	
	
	public static double[] tank_input() {
		double[] inputs = new double[2];
//TODO: Make sure the RIGHT SIDE is the one multiplied by -1.
		
		
		inputs[0] = Map.DRIVE_INPUT_MAGIC_NUMBERS[0] * Math.pow(Utils.deadzone(_drive_y.getRawAxis(Map.JOYSTICK_Y_AXIS)), 2) * Math.signum(_drive_y.getRawAxis(Map.JOYSTICK_Y_AXIS));//forward/backward motion
		inputs[1] = Map.DRIVE_INPUT_MAGIC_NUMBERS[1] * Math.pow(Utils.deadzone(_drive_w.getRawAxis(Map.JOYSTICK_X_AXIS)), 2) * Math.signum(_drive_w.getRawAxis(Map.JOYSTICK_X_AXIS)) * -1;//turning left/right;
		
		return inputs;
	}
	
	
//	public static double[] mecanum_input() {
//		double[] inputs = new double[3];
//
//		inputs[0] = Map.DRIVE_INPUT_MAGIC_NUMBERS[0] * Math.pow(Utils.deadzone(_drive_forwardright.getRawAxis(Map.JOYSTICK_Y_AXIS)), 2) * Math.signum(_drive_forwardright.getRawAxis(Map.JOYSTICK_Y_AXIS));// y
//		inputs[1] = Map.DRIVE_INPUT_MAGIC_NUMBERS[1] * Math.pow(Utils.deadzone(_drive_forwardright.getRawAxis(Map.JOYSTICK_X_AXIS)), 2) * Math.signum(_drive_forwardright.getRawAxis(Map.JOYSTICK_X_AXIS));// x
//		inputs[2] = Map.DRIVE_INPUT_MAGIC_NUMBERS[2] * Math.pow(Utils.deadzone(_drive_rotation.getRawAxis(Map.JOYSTICK_X_AXIS)), 2) * Math.signum(_drive_rotation.getRawAxis(Map.JOYSTICK_X_AXIS));// w
//		
//		//inputs[0] = _drive_forwardright.getRawAxis(Map.JOYSTICK_Y_AXIS);
//		//inputs[1] = _drive_forwardright.getRawAxis(Map.JOYSTICK_X_AXIS);
//		//inputs[2] = _drive_rotation.getRawAxis(Map.JOYSTICK_X_AXIS);
//		
//		return inputs;
//	}
	
	
	public static boolean front_side() {
		if (_drive_w.getRawButtonLatch(Map.DRIVE_FRONTSIDE_BACK)) {
			return true;
		} 
		return false;
	}
}
