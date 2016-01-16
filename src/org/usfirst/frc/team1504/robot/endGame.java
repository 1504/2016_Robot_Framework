package org.usfirst.frc.team1504.robot;

import java.nio.ByteBuffer;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import org.usfirst.frc.team1504.robot.Update_Semaphore.Updatable;

public class endGame implements Updatable{
	
	DoubleSolenoid lift; 
	DoubleSolenoid mast;
	DoubleSolenoid hook;
	DriverStation ds;
	endGame EndGame;
	boolean override;
	int overrideButtonState, liftButtonState;
	long time;
	
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
			time = System.currentTimeMillis();
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
			liftButtonState = 1; //1 = true, 0 = false
			lift();
		}
		liftButtonState = 0; //false (0) whether or not condition executed
		
		while(IO.joystickSecondary.getRawButton(1))
		{
			if(IO.joystickSecondary.getRawButton(0)) //override
			{
				overrideButtonState = 1;
				override = true;
				lift();
			}
		}
		overrideButtonState = 0; //should be false (0) whether or not while loop executed
		override = false;
		
	}
	
	public void dump()
	{
		//time
		byte[] data = new byte[8];
		ByteBuffer.wrap(data).putLong(System.currentTimeMillis() - time);
		ByteBuffer.wrap(data).putInt(liftButtonState);
		ByteBuffer.wrap(data).putInt(overrideButtonState);
		ByteBuffer.wrap(data).putInt(mast.get().ordinal());
		

		//data[0] = System.currentTimeMillis() - time;
		Logger.getInstance().log(Map.LOGGED_CLASSES.ENDGAME, data);
	}
	
}

