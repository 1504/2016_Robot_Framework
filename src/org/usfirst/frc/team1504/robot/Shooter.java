package org.usfirst.frc.team1504.robot;

import java.nio.ByteBuffer;

import org.usfirst.frc.team1504.robot.Update_Semaphore.Updatable;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice; 
import edu.wpi.first.wpilibj.DriverStation;

public class Shooter implements Updatable
{
	private static class Shooter_Task implements Runnable
	{

		private Shooter _task;

		Shooter_Task(Shooter d)
		{
			_task = d;
		}

		public void run()
		{
			_task.shootTask();
		}
	}

	private class Motor_Setter implements Runnable
	{
		public void run()
		{
			while(true)
			{
				setMotors();
			}
		}
	}
	private static Shooter instance = new Shooter();

	private Thread _task_thread;
	private Thread _dump_thread;
	private Thread _pid_thread;
	private volatile boolean _thread_alive = true;

	/**
	 * Returns an instance of the shooter; for use in other classes.
	 * 
	 */
	public static Shooter getInstance()
	{
		return Shooter.instance;
	}
	protected Shooter()
	{
		_task_thread = new Thread(new Shooter_Task(this), "1504_Shooter");
		_task_thread.setPriority((Thread.NORM_PRIORITY + Thread.MAX_PRIORITY) / 2);
		_task_thread.start();

		_pid_thread = new Thread(new Motor_Setter());
		_pid_thread.setPriority((Thread.NORM_PRIORITY + Thread.MAX_PRIORITY) / 2);
		_pid_thread.start();
		
		Update_Semaphore.getInstance().register(this);

		ShootInit();

		System.out.println("Gunner, standing by.");
	}
	public void release()
	{
		_thread_alive = false;
	}

	private DriverStation _ds = DriverStation.getInstance();
	private Logger _logger = Logger.getInstance();
	private CANTalon[] _motors;
	private boolean[] _shooter_input;// 0: Intake On, 1: Intake Off, 2: Prep, 3: Launch, 4: Disable Launch

	private boolean _intake_on = false;
	private boolean _prep_on = false;	
	
	private double[] _motor_values;
	private int _prep_counter = 0;

	private static enum STATE {Default, IntakeOn, IntakeReverse, Prep, Launch, DisableLaunch};
	private STATE _mode = STATE.Default;
	
	private volatile int _loops_since_last_dump = 0;
	
	/**
	 * Initializes motors and buttons for usage. Called ONCE.
	 */
	private void ShootInit()
	{
		_motors = new CANTalon[Map.SHOOTER_MOTOR_PORTS.length];
		_motors[0] = new CANTalon(Map.INTAKE_TALON_PORT);

		_motors[1] = new CANTalon(Map.SHOOTER_PORT_TALON_PORT);
//		_motors[1].setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative); // Magnetic Encoder
//		_motors[1].reverseSensor(false); // No sign flips

		_motors[2] = new CANTalon(Map.SHOOTER_STARBOARD_TALON_PORT);
		_motors[2].setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
		_motors[2].reverseSensor(false);

		_shooter_input = new boolean[Map.SHOOTER_INPUTS.length];

		_motor_values = new double[_motors.length];
	}
	/**
	 * Updates the array values to the current values of the buttons.
	 */
	public void semaphore_update()
	{
		_shooter_input[0] = IO.intake_on();
		_shooter_input[1] = IO.intake_off();
		_shooter_input[2] = IO.prep();
		_shooter_input[3] = IO.launch();
		_shooter_input[4] = IO.disable_launch();
	}
	private void change_state()
	{
		if (_shooter_input[1] || !_intake_on)
		{
			_intake_on = false;
			_mode = STATE.Default;
//			System.out.println("default");
		}
		if (_shooter_input[0] || _intake_on)
		{
			_intake_on = true;
			_mode = STATE.IntakeOn;
//			System.out.println("intake on");
		}
		if (_shooter_input[0] && _shooter_input[1])
		{
			_intake_on = false;
			_mode = STATE.IntakeReverse;
//			System.out.println("intake rev");
		}
		if (_shooter_input[2] || _prep_on)
		{
			_prep_on = true;
			_mode = STATE.Prep;
//			System.out.println("prep");
		}
		if (_shooter_input[3])
		{
			_prep_on = false;
			_mode = STATE.Launch;
//			System.out.println("launch");
		}
		if (_shooter_input[4])
		{
			_prep_on = false;
			_mode = STATE.DisableLaunch;
//			System.out.println("disable launch");
		}
	}
	/**
	 * Turns on the motor so that the robot can capture a BOULDER.
	 */
	private void intake()
	{
		if (_mode == STATE.IntakeOn)
		{
			_motor_values[0] = Map.SHOOTER_INTAKE_FORWARD * Map.SHOOTER_MAGIC_NUMBERS[0];
		}
		if (_mode == STATE.Default)
		{
			_motor_values[0] = 0.0;
		}
		if (_mode == STATE.IntakeReverse)
		{
			_motor_values[0] = Map.SHOOTER_INTAKE_BACKWARDS * Map.SHOOTER_MAGIC_NUMBERS[0];
		}
	}
	/**
	 * Prepares the BOULDER for launch into the high goal of the CASTLE TOWER.
	 * It first partially jams the BOULDER between the ramp and the intake motor
	 * such that there is no contact with the two launch motors, and then
	 * proceeds to turn those motors on.
	 */
	private void prep()
	{
		if (_mode == STATE.Prep)
		{
			if (_prep_counter < 3)
			{ 
				try
				{
					if (_prep_counter == 0)
					{
						_motor_values[0] = Map.SHOOTER_INTAKE_PREP * Map.SHOOTER_MAGIC_NUMBERS[0];
					}
					_motor_values[1] = 0.1;
					_motor_values[2] = -0.1;
					
					Thread.sleep(20);
					
					_motor_values[1] = _motor_values[2] = 0.0;
					
					Thread.sleep(200);
					
					if (_motors[2].getEncVelocity() > 0) //_motors[1].getEncVelocity() > 0 && 
					{
						_motor_values[0] = 0.0;
						_prep_counter++;
					}
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			else
			{
			_motor_values[0] = 0.0;
			_motor_values[1] = Map.SHOOTER_MOTOR_SPEED * Map.SHOOTER_MAGIC_NUMBERS[1];
			_motor_values[2] = Map.SHOOTER_MOTOR_SPEED * Map.SHOOTER_MAGIC_NUMBERS[2];
			}
		}
	}
	/**
	 * Launches the BOULDER, but keeps the shooter motors running.
	 */
	private void launch()
	{

		if (_mode == STATE.Launch)
		{
			_motor_values[0] = Map.SHOOTER_INTAKE_LAUNCH * Map.SHOOTER_MAGIC_NUMBERS[0];

			try
			{
				Thread.sleep(500);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			_prep_counter = 0;
			_motor_values[0] = 0.0;
		}
	}
	/**
	 * Turns off the shooter motors.
	 */
	private void disable_launch()
	{
		if (_mode == STATE.DisableLaunch)
		{
			_motor_values[1] = _motor_values[2] = 0.0;
			_mode = STATE.Default;
		}
	}
	private void setMotors()
	{
		_motors[0].set(_motor_values[0]); // No encoder for the intake motor,
											// because it doesn't matter as
											// much.
//		if (_motor_values[1] == Map.SHOOTER_MOTOR_SPEED)
//		{
//			_motors[1].set((_motor_values[1] - _motors[1].getSpeed()) * Map.SHOOTER_GAIN);
//		}else
//		{
//			_motors[1].set(_motor_values[1]);
//		}
		
		if (_motor_values[2] == Map.SHOOTER_MOTOR_SPEED)
		{
			_motors[2].set((_motor_values[2] - _motors[2].getSpeed()) * Map.SHOOTER_GAIN);
		}else
		{
			_motors[2].set(_motor_values[2]);
		}
		
		if (_motor_values[1] != 0)
		{
			_motors[1].set(-1 * Map.SHOOTER_MAGIC_NUMBERS[1] * _motors[2].get());
		}
		else
		{
			_motors[1].set(0.0);
		}

	}
	// All motors should have: Bus Voltage, Output Current, and Set Point
	/**
	 * Creates an array of data to log.
	 */
	private void dump()
	{
		byte[] output = new byte[12 + 1 + 4 + 4];

		int loops_since_last_dump = _loops_since_last_dump;

		for (int i = 0; i < _motors.length; i++)
		{
			int j = i * 4;// in order to not have nested loops, array indices
							// get calculated based on i
			output[j] = Utils.double_to_byte(_motors[i].get());
			output[j + 1] = Utils.double_to_byte(_motors[i].getSetpoint());
			output[j + 2] = Utils.double_to_byte(_motors[i].getBusVoltage());
			output[j + 3] = Utils.double_to_byte(_motors[i].getOutputCurrent());
		}
		// current state of buttons, crushed into one byte , with the
		// button array: 0: Intake On, 1: Intake Off, 2: Prep, 3: Launch
		byte buttons = 0;
		if (_shooter_input[0])
		{
			buttons += 16;
		}
		if (_shooter_input[1])
		{
			buttons += 8;
		}
		if (_shooter_input[2])
		{
			buttons += 4;
		}
		if (_shooter_input[3])
		{
			buttons += 2;
		}
		if (_shooter_input[4])
		{
			buttons += 1;
		}

		output[12] = buttons;

		ByteBuffer.wrap(output, 12, 4).putInt(loops_since_last_dump);
		ByteBuffer.wrap(output, 16, 4).putInt((int) System.currentTimeMillis());

		if (_logger != null)
		{
			if (_logger.log(Map.LOGGED_CLASSES.SHOOTER, output))
				_loops_since_last_dump -= loops_since_last_dump;
		}
	}
	/**
	 * Controls the above methods.
	 */
	private void shootTask()
	{
		boolean logtiem = false;
		while (_thread_alive)
		{
			if (_ds.isEnabled() && _ds.isOperatorControl())
			{
				change_state();
				intake();
				prep();
				launch();
				disable_launch();
				logtiem = true;
			}
			if (logtiem)
			{
//				 Dump is done in its own thread, for speed.
				if (_dump_thread == null || !_dump_thread.isAlive())
				{
					_dump_thread = new Thread(new Runnable()
					{
						public void run()
						{
							dump();
						}
					});
					_dump_thread.start();
				}
				logtiem = false;
			}
		}
	}
}
