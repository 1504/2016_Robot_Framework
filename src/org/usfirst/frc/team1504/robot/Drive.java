package org.usfirst.frc.team1504.robot;

import java.nio.ByteBuffer;

import org.usfirst.frc.team1504.robot.Update_Semaphore.Updatable;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DriverStation;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class Drive implements Updatable
{

	private static class DriveTask implements Runnable
	{

		private Drive _d;

		DriveTask(Drive d)
		{
			_d = d;
		}

		public void run()
		{
			_d.fastTask();
		}
	}

	private static final Drive instance = new Drive();

	private Thread _task_thread;
	private Thread _dump_thread;
	private volatile boolean _thread_alive = true;

	private Compressor _c;
	
	/**
	 * Gets an instance of the Drive
	 *
	 * @return The Drive.
	 */
	public static Drive getInstance()
	{
		return Drive.instance;
	}
	protected Drive()
	{
		_task_thread = new Thread(new DriveTask(this), "1504_Drive");
		_task_thread.setPriority((Thread.NORM_PRIORITY + Thread.MAX_PRIORITY) / 2);
		_task_thread.start();

		Update_Semaphore.getInstance().register(this);

		DriveInit();

		System.out.println("Drive Leader, standing by.");
	}
	public void release()
	{
		_thread_alive = false;
	}

	/**
	 * 
	 * User code here
	 * 
	 */

	private DriverStation _ds = DriverStation.getInstance();
	private Logger _logger = Logger.getInstance();
	private Pneumatics _p = Pneumatics.getInstance();
	
	private volatile boolean _new_data = false;
	private volatile double[] _input =	{ 0.0, 0.0 };// TWO due to ARCADE DRIVE.
	private volatile double _frontside_scalar = 1.0;
	private DriveGlide _glide = new DriveGlide();
	private Groundtruth _groundtruth = Groundtruth.getInstance();

	
	private CANTalon[] _motors = new CANTalon[Map.DRIVE_MOTOR_PORTS.length];

	private volatile int _loops_since_last_dump = 0;

	/**
	 * Set up everything that will be needed for the drive class
	 */
	private void DriveInit()
	{
		// Set up the drive motors
		for (int i = 0; i < Map.DRIVE_MOTOR_PORTS.length; i++)
		{
			_motors[i] = new CANTalon(Map.DRIVE_MOTOR_PORTS[i]);
		}
		
		_c = new Compressor();
	}
	/**
	 * Method called when there is new data from the Driver Station.
	 * 
	 * @see org.usfirst.frc.team1504.robot.Update_Semaphore
	 */
	public void semaphore_update()
	{
		// Get new values from the map
		// Do all configurating first (front, etc.)
		drive_inputs(IO.tank_input());
		// so "_new_data = true" at the VERY END OF EVERYTHING
	}
	/**
	 * Put data into the processing queue. Usable from both the semaphore and
	 * autonomous methods.
	 */
	public void drive_inputs(double forward, double turn)
	{
		double[] inputs =
		{ forward, turn };
		drive_inputs(inputs);
	}
	public void drive_inputs(double[] input)
	{
		if (_new_data)
			return;

		_input = input;
		_new_data = true;
	}
	/**
	 * Programmatically switch the direction the robot goes when the stick gets
	 * pushed; due to tank, can only switch between forward and backwards.
	 */
	private double[] front_side(double[] input)
	{
		input[0] *= _frontside_scalar;
		return input;
	}
	private void frontsideAngle(double d)
	{
		_frontside_scalar = d;
	}
	/**
	 * Detented controller correction methods (and helper methods)
	 */
	private double[] detents(double[] input)
	{

		double theta = Math.atan2(input[0], input[1]);

		double dx = correct_x(theta) * Utils.distance(input[1], input[0]) * 0.25;
		double dy = correct_y(theta) * Utils.distance(input[1], input[0]) * 0.25;

		double[] detented = new double[3];

		detented[0] = input[0] + dy; // y
		detented[1] = input[1] + dx; // x
//		detented[2] = input[2];// angular

		return detented;
	}
	private double correct_x(double theta)
	{
		return -Math.sin(theta) * (-Math.sin(8 * theta) - 0.25 * Math.sin(4 * theta));
	}
	private double correct_y(double theta)
	{
		return Math.cos(theta) * (-Math.sin(8 * theta) - 0.25 * Math.sin(4 * theta));
	}
	/**
	 * Ground truth sensor corrections
	 * 
	 * @param input
	 *            - Joystick input to correct towards
	 * @return
	 */
	private double[] groundtruth_correction(double[] input)
	{
		double[] normal_input = input;
		double[] output = input;
		double[] speeds = _groundtruth.getSpeed();

		// Normalize the inputs and actual speeds
		if (groundtruth_normalize(speeds) == 0)
			return input;
		groundtruth_normalize(normal_input);

		// Apply P(ID) correction factor to the joystick values
		// TODO: Determine gain constant and add to the Map
		for (int i = 0; i < 2; i++)
			output[i] += (normal_input[i] - speeds[i]) * -0.01;

		return output;
	}
	/**
	 * Normalization function for arrays to normalize full scale to +- 1 <br>
	 * Note: THIS FUNCTION OPERATES ON THE REFERENCE INPUT ARRAY AND WILL CHANGE
	 * IT!
	 * 
	 * @param input
	 *            - The array to normalize
	 * @return Maximum value in the array
	 */
	private double groundtruth_normalize(double[] input)
	{
		double max = 0;
		for (int i = 0; i < input.length; i++)
			max = Math.max(Math.abs(input[1]), max);

		if (max == 0)
			return 0;

		max = max == 0 ? 1 : max;
		for (int i = 0; i < input.length; i++)
			input[i] /= max;

		return max;
	}
	/**
	 * Convert the Forward/Backward and Turn values into 4 motor outputs
	 * 
	 * @param input
	 *            - Double array containing Forward and Turn values
	 * @param output
	 *            - Double array containing motor output values
	 */
	private double[] outputCompute(double[] input)
	{
		double[] output = new double[4];

		double theta = Math.atan2(input[0], input[1]);
		double offset = (theta % (Math.PI/4.0)) - Math.floor((theta/(Math.PI/4)%2)*(Math.PI/4));
		double scalar = Math.cos(offset)/Math.cos(offset - 45 + 90 * (offset < 0 ? 1.0 : 0.0));

		output[0] = output[1] = (scalar/Math.sqrt(2.0)) * (input[0] + input[1]);
		output[2] = output[3] = (scalar/Math.sqrt(2.0)) * (input[0] - input[1]);

		return output;
	}
	/**
	 * Output values to motors. Input: array of motor values to output in
	 * Map.DRIVE_MOTOR order.
	 */
	private void motorOutput(double[] values)
	{
		for (int i = 0; i < _motors.length; i++)
		{
			// There are no Sync Groups for CANTalons. Apparently.
			_motors[i].set(values[i] * Map.DRIVE_OUTPUT_MAGIC_NUMBERS[i]);
		}
	}
	/**
	 * Dump class for logging
	 */
	private void dump()
	{
		byte[] output = new byte[12 + 4 + 4];

		int loops_since_last_dump = _loops_since_last_dump;
		// _loops_since_last_dump = 0;

		// Dump motor set point, current, and voltage
		for (int i = 0; i < Map.DRIVE_MOTOR.values().length; i++)
		{
			output[i] = Utils.double_to_byte(_motors[i].get()); // Returns as
																// 11-bit,
																// downconvert
																// to 8
			output[i + 1] = (byte) _motors[i].getOutputCurrent();
			output[i + 2] = (byte) (_motors[i].getBusVoltage() * 10);
			// From CANTalon class: Bus voltage * throttle = output voltage
		}
		ByteBuffer.wrap(output, 12, 4).putInt(loops_since_last_dump);
		ByteBuffer.wrap(output, 16, 4).putInt((int) (System.currentTimeMillis() - IO.ROBOT_START_TIME));

		if (_logger != null)
		{
			if (_logger.log(Map.LOGGED_CLASSES.DRIVE, output))
				_loops_since_last_dump -= loops_since_last_dump;
		}
	}
	/**
	 * Update motors as fast as possible, but only compute all the joystick stuff when there's new data
	 */
	private void fastTask()
	{
		// Damn you, Java, and your lack of local static variables!
		double[] input;
		boolean dump = false;
		
		while(_thread_alive)
		{
			input = _input;
			
			if(_ds.isEnabled())
			{
				// Process new joystick data - only when new data happens
				if(_new_data)
				{	
					if(_ds.isOperatorControl())
					{
						// Switch front side, if needed
						double frontside_scalar = IO.front_side();
						if (frontside_scalar != 0.0)
						{
						frontsideAngle(frontside_scalar);							
						}
						
						// Detents
						input = detents(input);
						
						// Frontside
						input = front_side(input);
						
						// Orbit point
						//input = orbit_point(input);
						// Glide
						input = _glide.gain_adjust(input);
						// Osc
						
						// Save corrected input for fast loop
						_input = input;
					}
					
					_new_data = false;
					dump = true;
				}
				
				// Ground speed offset
				input = groundtruth_correction(input);
				// Output to motors - as fast as this loop will go
				motorOutput(outputCompute(input));
				
				_loops_since_last_dump++;
				
				// Log on new data, after the first computation
				if(dump)
				{
					// Dump in a separate thread, so we can loop as fast as possible
					if(_dump_thread == null || !_dump_thread.isAlive())
					{
						_dump_thread = new Thread(new Runnable() {
							public void run() {
								dump();
							}
						});
						_dump_thread.start();
					}
					dump = false;
				}
			}
		}
	}
}
