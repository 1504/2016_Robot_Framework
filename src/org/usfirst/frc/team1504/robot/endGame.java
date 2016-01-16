package org.usfirst.frc.team1504.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import org.usfirst.frc.team1504.robot.Update_Semaphore.Updatable;

public class endGame implements Updatable{
	
	DoubleSolenoid lift; 
	DoubleSolenoid mast;
	DoubleSolenoid hook;
	DriverStation ds;
	endGame EndGame;
	boolean override = false;
	
	public endGame()
	{
		mast.set(DoubleSolenoid.Value.kOff);
		lift.set(DoubleSolenoid.Value.kOff);
		hook.set(DoubleSolenoid.Value.kOff);
		Update_Semaphore.getInstance().register(this);
	}
	
	public void lift()
	{
		if(ds.getMatchTime() <= 20 || override)
		{
			try {
				Thread.sleep(1000);
			}
			catch(InterruptedException e){}
			
			mast.set(DoubleSolenoid.Value.kForward); //to raise mast
			lift.set(DoubleSolenoid.Value.kForward); //extend piston
			hook.set(DoubleSolenoid.Value.kForward);
			lift.set(DoubleSolenoid.Value.kReverse); //retract = pull robot up
		}
	}
	
	public void semaphore_update()
	{
		if(IO.joystickSecondary.getRawButton(0))
		{
			lift();
		}
		
		while(IO.joystickSecondary.getRawButton(1))
		{
			if(IO.joystickSecondary.getRawButton(0)) //override
			{
				override = true;
				lift();
			}
		}
		override = false;
		
	}
	
	public void dump()
	{
		//time
	}
	
}

