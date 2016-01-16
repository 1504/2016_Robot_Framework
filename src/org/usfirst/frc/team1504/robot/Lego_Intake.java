package org.usfirst.frc.team1504.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Solenoid;

public class Lego_Intake {

	CANTalon hinge;
	Solenoid leftArm, rightArm;

	public Lego_Intake() { //starts for picking up; could change to shooting if we want
		leftArm.set(true); // open and ready for pickup
		rightArm.set(true);
		hinge.set(1); // set more exact later
	}

	public void pickUp() {
		leftArm.set(false); // close grabber arms around boulder
		rightArm.set(false);
	}

	public void shoot() {
		leftArm.set(true); // open to allow shooter to shoot
		rightArm.set(true);

	}
}
