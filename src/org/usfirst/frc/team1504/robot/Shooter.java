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
	private CANTalon _intake_motor = new CANTalon(Map.INTAKE_TALON_PORT);
	private CANTalon _shooter_left_motor = new CANTalon(Map.SHOOTER_RIGHT_TALON_PORT);
	private CANTalon _shooter_right_motor = new CANTalon(Map.SHOOTER_LEFT_TALON_PORT);
	
	private void ShootInit()
	{
		
	}
	
	public void semaphore_update()
	{
		
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
