package org.usfirst.frc.team1504.robot;

import org.usfirst.frc.team1504.robot.Update_Semaphore.Updatable;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid;

public class Lego_Intake implements Updatable{
	
	DoubleSolenoid hinge;
	Solenoid leftArm, rightArm, shooter;
	CANTalon leftRoller, rightRoller;
	
	public enum ACTION_STATES {READY, PICKUP_IN, PICKUP_OUT, FIRE, RELOAD};
	byte [] actionState = new byte[5];
	public enum MOTION_STATES {FIRING, PICKUP, CLEAR};
	
	ACTION_STATES _astate = ACTION_STATES.READY;
	MOTION_STATES _mstate = MOTION_STATES.PICKUP;
	
	private class intakeTask implements Runnable
	{
		Lego_Intake legoIntake;
		intakeTask intakeTask;
		
		public intakeTask(Lego_Intake intake)
		{
			intake = legoIntake;
		}
		
		public void run()
		{
			intakeTask.run();
		}
	}
	
	private class shooterTask implements Runnable
	{
		shooterTask shooterTask;
		public void run()
		{
			setShooterActionState(_astate);
			shooterTask.run();
		}
	}
	
	private class motionTask implements Runnable
	{
		motionTask motionTask;
		public void run()
		{
			setMotionState(_mstate);
			motionTask.run();
		}
	}
	
	public Lego_Intake()
	{
		leftRoller.enableLimitSwitch(true, false);
	}
	
	public void setShooterActionState(ACTION_STATES astate)
	{
		switch(astate) {
		case FIRE:
			if(_astate == ACTION_STATES.READY && _mstate == MOTION_STATES.FIRING) 
			{
				leftArm.set(true);
				rightArm.set(true); //open arms
				try{
					Thread.sleep(500);
				}
				catch(InterruptedException e){}
				
				shooter.set(true); //shoot
				_astate = astate;
				
				try{
					Thread.sleep(300);
				}
				catch(InterruptedException e){}
				
				//reload
				shooter.set(false);
			
				try{
					Thread.sleep(500);
				}
				catch(InterruptedException e){}
				
				leftArm.set(false); //close arms back up for normal pickup position
				rightArm.set(false);
				 //reverse the shooter for reloading
				//hinge.set(DoubleSolenoid.Value.kReverse); - this is a motion - //kOff will be clear position
			}
		}
		}
	
	public void setActionState(ACTION_STATES astate)
	{
		switch(astate) {
		case READY:
			leftRoller.set(0);
			rightRoller.set(0);
			_astate = astate;
			break;
		
		case PICKUP_IN:
			if(_astate == ACTION_STATES.READY && _mstate == MOTION_STATES.PICKUP) 
			{	
				leftRoller.set(1); //spin to pick up ball
				rightRoller.set(1);
				_astate = astate;
			}
		case PICKUP_OUT:
			if(_astate == ACTION_STATES.READY && _mstate == MOTION_STATES.PICKUP) 
			{
				leftRoller.set(-1); //spin to spit out ball
				rightRoller.set(-1);
				_astate = astate;
			}
		}		
	}
	
	public void setMotionState(MOTION_STATES mstate)
	{
		//todo - possibly add variable to keep motion from changing during actions
		switch(mstate)
		{
		case PICKUP:
			hinge.set(DoubleSolenoid.Value.kReverse); 
			_mstate = mstate;
		case CLEAR:
			hinge.set(DoubleSolenoid.Value.kOff); 
			_mstate = mstate; 
		case FIRING:
			hinge.set(DoubleSolenoid.Value.kForward); 
			_mstate = mstate;
		}
	}
	
	public void semaphore_update()
	{
		//_mstate = IO.setJoystickMotionState();
		setActionState(IO.setJoystickActionState());
		//setMotionState(_mstate);
		setMotionState(IO.setJoystickMotionState());	
	}
	
	public void dump()
	{
		//solenoids.get (use the ordinal too)
		byte[] data = new byte[8];
		data[0] = Utils.byteLog(hinge);
		data[1] = Utils.byteLogSingle(leftArm);
		data[2] = Utils.byteLogSingle(rightArm);
		data[3] = Utils.double_to_byte(rightRoller.get());
		data[4] = Utils.double_to_byte(leftRoller.get());
		data[5] = Utils.actionStates(_astate);
		data[6] = Utils.motionStates(_mstate);
		
		Logger.getInstance().log(Map.LOGGED_CLASSES.LEGO_INTAKE, data);		
	}
}

