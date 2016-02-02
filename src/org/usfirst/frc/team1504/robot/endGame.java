package org.usfirst.frc.team1504.robot;

import java.nio.ByteBuffer;

import org.usfirst.frc.team1504.robot.Update_Semaphore.Updatable;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;

public class endGame implements Updatable{
	
	DoubleSolenoid lift; 
	DoubleSolenoid mast;
	DoubleSolenoid hook;
	DriverStation ds;
	endGame EndGame;
	boolean override;
	int overrideButtonState, liftButtonState; //values of 0 or 1 to record button states
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
		/*if(IO.joystickSecondary.getRawButton(0))
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
		}*/
		
		int state = IO.endGameInputs();
		if(state == 1)
		{
			liftButtonState = 1; //1 = true, 0 = false
			lift();
			liftButtonState = 0;
		}
		
		if(state == 2)
		{
			overrideButtonState = 1;
			override = true;
			lift();
			overrideButtonState = 0;
			override = false;
		}
		
		
		
	}
	
	public void dump()
	{
		//time
		byte[] data = new byte[8];
		//byte mastByte = 0; = data[0]
		
		/*if(mast.get() == DoubleSolenoid.Value.kOff)
		{
			mastByte += 4;
		} //TODO finish this using the method in utils*/
		
		data[0] = Utils.byteLog(mast);
		data[1] = Utils.byteLog(lift); 
		data[2] = Utils.byteLog(hook);
		data[3] = (byte)overrideButtonState;
		data[4] = (byte)liftButtonState;

		//data[0] = System.currentTimeMillis() - time;
		Logger.getInstance().log(Map.LOGGED_CLASSES.ENDGAME, data);
	}
	
}

