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
    // Drive Input magic numbers
    public static final double[] DRIVE_INPUT_MAGIC_NUMBERS =
    { 1.0, -1.0 };

    //Secondary Motor Ports
    //The direction right means on the right if you're looking from the backside of the robot. Ditto with left.
    public static final int INTAKE_TALON_PORT = 20;
    public static final int SHOOTER_PORT_TALON_PORT = 31;
    public static final int SHOOTER_STARBOARD_TALON_PORT = 30;
    public static final int[] SHOOTER_MOTOR_PORTS =
    {INTAKE_TALON_PORT, SHOOTER_PORT_TALON_PORT, SHOOTER_STARBOARD_TALON_PORT};
    
    /**
     * Shooter Stuff
     */
    public static final double[] SHOOTER_MAGIC_NUMBERS = {-1.0, 1.0, -1.0};
    public static final double SHOOTER_INTAKE_FORWARD = 1.0;
    public static final double SHOOTER_INTAKE_BACKWARDS = -0.7;
    public static final double SHOOTER_INTAKE_PREP = -0.2;
    public static final double SHOOTER_INTAKE_LAUNCH = 1.0;
    public static final double SHOOTER_INTAKE_OSC_FORWARD = 1.0;
    public static final double SHOOTER_INTAKE_OSC_BACKWARDS = 0.0;
    public static double SHOOTER_MOTOR_SPEED = 6400.0;
    public static final double SHOOTER_GAIN = 0.00015;
    public static final double PORT_I_GAIN = 0.0000003;
    public static final double STARBOARD_I_GAIN = 0.0000003;
    
    // Glide gain
    public static final double[][] DRIVE_GLIDE_GAIN =
    {
        {0.0015, 0.003},
{0.008, 0.008}};
    // Drive Output magic numbers - for getting everything spinning the correct direction
    public static final double[] DRIVE_OUTPUT_MAGIC_NUMBERS =
    { -1.0, -1.0, 1.0, 1.0 };
    
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
     * Pneumatics stuff
     */
    public static final int HIGHSIDE = 1;
    public static final int LOWSIDE = 0;
    
    /**
    * IO stuff
    */
    // Joystick raw axes
    public static final int JOYSTICK_Y_AXIS = 1;
    public static final int JOYSTICK_X_AXIS = 0;
    
    // Joystick inputs
    public static final int DRIVE_ARCADE_Y = 0;
    public static final int DRIVE_ARCADE_TURN = 1;
    public static final int SECONDARY = 2;
  
    
    /**
    * Logger stuff
    */
    public static enum LOGGED_CLASSES
    { SEMAPHORE, DRIVE, GROUNDTRUTH, SHOOTER, PNEUMATICS }
    
    //Buttons
    
    	//Primary
    
    // Drive Front Side changing
    public static final int DRIVE_FRONTSIDE_BACK = 2;
    public static final int DRIVE_FRONTSIDE_FRONT = 3;
    
    	//Seconday
    //Buttons for shooting
    public static final int SHOOTER_INTAKE_ON = 6;
    public static final int SHOOTER_INTAKE_OFF = 7;
    public static final int SHOOTER_INTAKE_OSC = 8;
    public static final int SHOOTER_PREP = 3;
    public static final int SHOOTER_LAUNCH = 1;
    public static final int SHOOTER_DISABLE_LAUNCH = 2;
    public static final int[] SHOOTER_INPUTS =
    {SHOOTER_INTAKE_ON, SHOOTER_INTAKE_OFF, SHOOTER_PREP, SHOOTER_LAUNCH, SHOOTER_DISABLE_LAUNCH, SHOOTER_INTAKE_OSC};


}