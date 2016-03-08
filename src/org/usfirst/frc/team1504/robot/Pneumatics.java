package org.usfirst.frc.team1504.robot;

import org.usfirst.frc.team1504.robot.Update_Semaphore.Updatable;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Pneumatics implements Updatable
{

	public volatile double _pressure_high;// these are characters for space
										// efficiency
	public volatile double _pressure_low;
	public volatile float _current;
	public volatile boolean _pressure_switch;
	public volatile boolean _is_pressure_low;

	private static final Pneumatics instance = new Pneumatics();

	private Logger _log = Logger.getInstance();

	private Compressor _compressor;
	public AnalogInput _high_pressure_in;
	public AnalogInput _low_pressure_in;

	protected Pneumatics()
	{
		_compressor = new Compressor();
		_high_pressure_in = new AnalogInput(Map.HIGHSIDE);
		_low_pressure_in = new AnalogInput(Map.LOWSIDE);

		Update_Semaphore.getInstance().register(this);

		System.out.println("Pressure leader, standing by.");
	}
	public static Pneumatics getInstance()
	{
		return instance;
	}
	private double volt_to_pressure(double v)
	{
		return (double)((250 * v / 4096) - 25);
	}
	private void updateVals()
	{
		_pressure_high = volt_to_pressure(_high_pressure_in.getAverageVoltage());
		_pressure_low = volt_to_pressure(_low_pressure_in.getAverageVoltage());
		_current = _compressor.getCompressorCurrent();
		_pressure_switch = _compressor.getPressureSwitchValue();

	}
	private void updateDash()
	{
		SmartDashboard.putNumber("highside pressure", _pressure_high);
		SmartDashboard.putNumber("lowside pressure", _pressure_low);
		SmartDashboard.putNumber("compressor current", _current);
	}
	private void dump()
	{
		byte[] data =
		{ (byte) _pressure_high, (byte) _pressure_low, Utils.double_to_byte(_current),
				(byte) ((_pressure_switch ? 2 : 0) + (_is_pressure_low ? 1 : 0)) };
				

//		_log.log(Map.LOGGED_CLASSES.PNEUMATICS, data);
	}
	public void semaphore_update()
	{
		updateVals();
		updateDash();
		dump();
	}
}
