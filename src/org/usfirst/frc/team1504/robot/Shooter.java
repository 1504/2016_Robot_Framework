package org.usfirst.frc.team1504.robot;

import org.usfirst.frc.team1504.robot.Update_Semaphore.Updatable;

import edu.wpi.first.wpilibj.CANTalon;
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

	private static Shooter instance = new Shooter();
	
	private Thread _task_thread;
	private volatile boolean _thread_alive = true;
	
	public static Shooter getInstance()
	{
		return Shooter.instance;
	}
	
	protected Shooter()
	{
		_task_thread = new Thread(new Shooter_Task(this), "1504_Shooter");
		_task_thread.setPriority((Thread.NORM_PRIORITY + Thread.MAX_PRIORITY)/2);
		_task_thread.start();
		
		Update_Semaphore.getInstance().register(this);
		
		ShootInit();
		
		System.out.println("Red shooter, standing by.");
	}
	
	public void release()
	{
		_thread_alive = false;
	}
	
	private DriverStation _ds = DriverStation.getInstance();
	private Logger _logger = Logger.getInstance();
	private CANTalon _intake_motor;
	private CANTalon _shooter_left_motor;
	private CANTalon _shooter_right_motor;
	private boolean[] _shooter_input;// 0: Intake On, 1: Intake Off, 2: Prep, 3: Launch
	private boolean _prep_on;
	
	private void ShootInit()
	{
		_intake_motor = new CANTalon(Map.INTAKE_TALON_PORT);
		_shooter_left_motor = new CANTalon(Map.SHOOTER_LEFT_TALON_PORT);
		_shooter_right_motor = new CANTalon(Map.SHOOTER_RIGHT_TALON_PORT);
		_shooter_input = new boolean[Map.SHOOTER_INPUTS.length];
	}
	
	public void semaphore_update()
	{
		_shooter_input[0] = IO.intake_on();
		_shooter_input[1] = IO.intake_off();
		_shooter_input[2] = IO.prep();
		_shooter_input[3] = IO.launch();
	}
	
	//TODO: TEST AND FIND OUT REAL VALUES FOR MOTORS
	
	private void intake()
	{
		boolean intake_on = false;
		
		if (_shooter_input[0] || intake_on)
		{
			intake_on = true;
			_intake_motor.set(0.7);
		}
		if (_shooter_input[1] || !intake_on)
		{
			intake_on = false;
			_intake_motor.set(0.0);
		}
	}
	
	private void prep()
	{
		
		if (_shooter_input[2] || _prep_on)
		{
			_prep_on = true;
			_intake_motor.set(-0.3);
			
			try {
			    Thread.sleep(250); //A quarter of a second.
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
			
			_intake_motor.set(0.0);
			_shooter_left_motor.set(1.0);
			_shooter_right_motor.set(1.0);
		}
	}
	
	private void launch()
	{
		
		if (_shooter_input[3])
		{
			_intake_motor.set(1.0);
			try {
			    Thread.sleep(333); //Almost a third of a second
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
			_prep_on = false;
			_shooter_left_motor.set(0);
			_shooter_right_motor.set(0);
			_intake_motor.set(0);
		}
	}
	
	private void shootTask()
	{
		//stuff will happen
		while(_thread_alive)
		{
			//BEEP BOOP I AM ROBORT
		}
	}
}
