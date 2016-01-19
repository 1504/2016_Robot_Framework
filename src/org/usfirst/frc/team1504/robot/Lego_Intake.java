package org.usfirst.frc.team1504.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import org.usfirst.frc.team1504.robot.Update_Semaphore.Updatable;

public class Lego_Intake implements Updatable{
	
	DoubleSolenoid hinge;
	Solenoid leftArm, rightArm, plunger;
	CANTalon leftRoller, rightRoller;
	
	boolean hingestartingPos = true; //startingPos true if arm starts lower to pickup
	boolean shootButtonState, pickupButtonState;
	//leftarmState, rightarmState, plungerState, 
	
	
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
	
	public Lego_Intake()
	{
		leftRoller.enableLimitSwitch(false, false); //hm
	}
	
	public void pickUp()
	{	
		if(!hingestartingPos)
		{
			//hinge.set(-.75); //arbitrary - lower into the right position
			hinge.set(DoubleSolenoid.Value.kOff); //off = pickup position
			hingestartingPos = true;
		}
		hingestartingPos = true;
		leftArm.set(true);
		rightArm.set(true);
		
		leftRoller.set(1); //spin to pick up ball
		rightRoller.set(1);
		
		leftRoller.enableLimitSwitch(true, false); //forward, reverse
		leftArm.set(false);
		rightArm.set(false); //close
		leftRoller.set(0); // turn off
		rightRoller.set(0);	
		
	}
	
	public void shoot()
	{
		if(hingestartingPos)
		{
			hinge.set(DoubleSolenoid.Value.kForward); //off = pickup position
			hingestartingPos = false;
		}
		//hinge.set(DoubleSolenoid.Value.kForward); //bring up the arm for shooting
		hingestartingPos = false;
	
		leftRoller.enableLimitSwitch(true, false); //make sure ball is in the robot
		leftArm.set(false);
		rightArm.set(false); //open arms
		
		leftRoller.set(-.75);
		leftRoller.set(-.75); //negative for spinning for shooting
		plunger.set(true); //shooooot
		
		leftRoller.enableLimitSwitch(false, true); //check for when ball has left
		leftRoller.set(0);
		leftRoller.set(0); //turn off rollers
		
		plunger.set(false); //reverse the shooter
		
		leftArm.set(true); //open arms back up for pickup ready-ness
		rightArm.set(true);
	}
	
	public void semaphore_update()
	{
		if(IO.joystickSecondary.getRawButton(2))
		{
			pickUp();
			pickupButtonState = true;
		}
		pickupButtonState = false;
		
		if(IO.joystickSecondary.getRawButton(3))
		{
			shoot();
			shootButtonState = true;
		}
		shootButtonState = false;

	}
	
	public void dump()
	{
		//solenoids.get (use the ordinal too)
		byte[] data = new byte[8];
		
	}
}

