package org.usfirst.frc.team1504.robot;

public class Map
{
    /**
    * Utilities
    */
    public static final double UTIL_JOYSTICK_DEADZONE = 0.09;
    /**
    * Drive class things
    */
    // Joystick inputs
    public static final int DRIVE_ARCADE_Y = 0;
    public static final int DRIVE_ARCADE_TURN = 1;
	public static final int DRIVE_FORWARDRIGHT_JOYSTICK = 0;
	public static final int DRIVE_ROTATION_JOYSTICK = 1;
    public static final int SECONDARY = 2;

    
    // Drive Motor enumeration
    public static enum DRIVE_MOTOR
    { FRONT_LEFT, BACK_LEFT, BACK_RIGHT, FRONT_RIGHT }
    
    // Drive Motor ports
    public static final int FRONT_LEFT_TALON_PORT = 10;
    public static final int BACK_LEFT_TALON_PORT = 11;
    public static final int BACK_RIGHT_TALON_PORT = 12;
    public static final int FRONT_RIGHT_TALON_PORT = 13;
    public static final int[] DRIVE_MOTOR_PORTS =
    {
        FRONT_LEFT_TALON_PORT,
        BACK_LEFT_TALON_PORT,
        BACK_RIGHT_TALON_PORT,
        FRONT_RIGHT_TALON_PORT
    };
    // Glide gain
    public static final double[][] DRIVE_GLIDE_GAIN =
    {
        {0.0015, 0.0025, 0.003},
{0.008, 0.008, 0.008}};
    // Drive Output magic numbers - for getting everything spinning the correct direction
    public static final double[] DRIVE_OUTPUT_MAGIC_NUMBERS =
    { -1.0, -1.0, 1.0, 1.0 };
    // Drive Input magic numbers
    public static final double[] DRIVE_INPUT_MAGIC_NUMBERS =
    { 1.0, -1.0, 0.7 };

    
    //Cheesy Drive Button to toggle whether turning is enabled
    
    //Secondary Motor Ports
    //The direction right means on the right if you're looking from the backside of the robot. Ditto with left.
    public static final int INTAKE_TALON_PORT = 20;
    public static final int SHOOTER_LEFT_TALON_PORT = 21;
    public static final int SHOOTER_RIGHT_TALON_PORT = 22;
    public static final int[] SHOOTER_MOTOR_PORTS =
    {INTAKE_TALON_PORT, SHOOTER_LEFT_TALON_PORT, SHOOTER_RIGHT_TALON_PORT};
    
   
    /**
    * Ground truth sensor
    */
    public static final byte GROUNDTRUTH_QUALITY_MINIMUM = 40;
    public static final double GROUNDTRUTH_DISTANCE_PER_COUNT = 1.0;
    public static final double GROUNDTRUTH_TURN_CIRCUMFERENCE = 3.1416 * 1.25;
    public static final int GROUNDTRUTH_SPEED_AVERAGING_SAMPLES = 4;
    // Maximum (empirically determined) speed the robot can go in its three directions.
    public static final double[] GROUNDTRUTH_MAX_SPEEDS =
    {12.0, 5.0, 7.0};
    
    /**
    * IO stuff
    */
    // Joystick raw axes
    public static final int JOYSTICK_Y_AXIS = 1;
    public static final int JOYSTICK_X_AXIS = 0;
    /**
    * Logger stuff
    */
    public static enum LOGGED_CLASSES
    { SEMAPHORE, DRIVE, GROUNDTRUTH, SHOOTER, ENDGAME, LEGO_INTAKE }
	
	//Buttons
    
    //Primary
    public static final int DRIVE_TURN_TOGGLE = 1;
    
    public static final int DRIVE_FRONTSIDE_BACK = 2;
    public static final int DRIVE_FRONTSIDE_FRONT = 3;
    
    //Secondary
	public static final int ACTION_STATE_READY_BUTTON = 1;
	public static final int ACTION_STATE_PICKUP_IN_BUTTON = 2;
	public static final int ACTION_STATE_PICKUP_OUT_BUTTON = 3;
	public static final int ACTION_STATE_RELOAD_BUTTON = 4;
	public static final int ACTION_STATE_FIRE_BUTTON = 5;
	
    public static final int SHOOTER_INTAKE_ON = 3;
    public static final int SHOOTER_INTAKE_OFF = 5;
    public static final int SHOOTER_PREP = 2;
    public static final int SHOOTER_LAUNCH = 1;
    public static final int[] SHOOTER_INPUTS =
    {SHOOTER_INTAKE_ON, SHOOTER_INTAKE_OFF, SHOOTER_PREP, SHOOTER_LAUNCH};
	
	public static final int MOTION_STATE_FIRE_BUTTON = 6;
	public static final int MOTION_STATE_CLEAR_BUTTON = 7;
	public static final int MOTION_STATE_PICKUP_BUTTON = 8;
	
	public static final int ENDGAME_LIFT_BUTTON = 9;
	public static final int OVERRIDE_LIFT_BUTTON = 10;
	public static final int VISION_BUTTON = 11;

	public static final int VISION_WIDTH = 800;
	public static final int VISION_HEIGHT = 600;

	public static final double VISION_DEADZONE = 3;
	public static final double VISION_GAIN_ADJUST_X = .03;//.25;
	public static final double VISION_GAIN_ADJUST_Y = .25;
	public static final double VISION_DELAY_TIME = .65;//.75;
	public static final double VISION_CORRECTION_DELAY_TIME= .8;

	public static final int DRIVE_INPUT_TURN_FACTOR_OVERRIDE_BUTTON = 1;
	public static final double DRIVE_INPUT_TURN_FACTOR = 0.2;
}
