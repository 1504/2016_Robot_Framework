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
	private CANTalon[] motors = new CANTalon[Map.SHOOTER_MOTOR_PORTS.length];
	private boolean[] _shooter_input;// 0: Intake On, 1: Intake Off, 2: Prep, 3: Launch
	private boolean _prep_on;
	
	private volatile int _loops_since_last_dump = 0;
	
	private void ShootInit()
	{
		motors[0] = new CANTalon(Map.INTAKE_TALON_PORT);
		motors[1] = new CANTalon(Map.SHOOTER_LEFT_TALON_PORT);
		motors[2] = new CANTalon(Map.SHOOTER_RIGHT_TALON_PORT);
		
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
			motors[0].set(0.7);
		}
		if (_shooter_input[1] || !intake_on)
		{
			intake_on = false;
			motors[0].set(0.0);
		}
	}
	
	private void prep()
	{
		
		if (_shooter_input[2] || _prep_on)
		{
			_prep_on = true;
			motors[0].set(-0.3);
			
			try {
			    Thread.sleep(250); //A quarter of a second.
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
			
			motors[0].set(0.0);
			motors[1].set(1.0);
			motors[2].set(1.0);
		}
	}
	
	private void launch()
	{
		
		if (_shooter_input[3])
		{
			motors[0].set(1.0);
			try {
			    Thread.sleep(333); //Almost a third of a second
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
			_prep_on = false;
			motors[1].set(0);
			motors[2].set(0);
			motors[0].set(0);
		}
	}
	
	//All motors should have: Bus Voltage, Output Current, and Set Point
	private void dump()
	{
		byte[] output = new byte[12 + 1];
		
		int loops_since_last_dump = _loops_since_last_dump;
		
		for (int i = 0; i < motors.length; i++)
		{
			int j = i * 4;//in order to not have nested loops, array indices get calculated based on i
			output[j] = Utils.double_to_byte(motors[i].get());
			output[j+1] = Utils.double_to_byte(motors[i].getSetpoint());
			output[j+2] = Utils.double_to_byte(motors[i].getBusVoltage());
			output[j+3] = Utils.double_to_byte(motors[i].getOutputCurrent());
		}
		//current state of buttons, crushed into one byte Again, with the button array: 0: Intake On, 1: Intake Off, 2: Prep, 3: Launch
		byte buttons = 0;
			if (_shooter_input[0]) {buttons += 8;}
			if (_shooter_input[1]) {buttons += 4;}
			if (_shooter_input[2]) {buttons += 2;}
			if (_shooter_input[3]) {buttons += 1;}
		
		output[12] = buttons;
		
	}
	
	private void shootTask()
	{
		//stuff will happen
		while(_thread_alive)
		{
			if (_ds.isEnabled() && _ds.isOperatorControl())
			{
				intake();
				prep();
				launch();
			}
		}
	}
}
