package org.usfirst.frc.team1504.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalInput;

public class Digit_Board
{

	private static class Board_Task implements Runnable
	{
		private Digit_Board _b;

		Board_Task(Digit_Board b)
		{
			_b = b;
		}

		public void run()
		{
			_b.board_task();
		}
	}

	private static Digit_Board instance = new Digit_Board();

	private Thread _task_thread;

	public static Digit_Board getInstance()
	{
		return Digit_Board.instance;
	}

	protected Digit_Board()
	{
		_task_thread = new Thread(new Board_Task(this), "1504_Display_Board");
		_task_thread.setPriority((Thread.NORM_PRIORITY + Thread.MAX_PRIORITY) / 2);
		_task_thread.start();

		DisplayInit();

	}

	private DriverStation _ds = DriverStation.getInstance();

	private I2C _display_board;
	private byte[] _voltage;
	private byte[] _osc;
	private byte[] _blink;
	private byte[] _bright;

	private DigitalInput _a;
	private DigitalInput _b;
	private boolean[] _buttons;

	private AnalogInput _pot;
	private double _battery_voltage;
	

	private void DisplayInit()
	{
		_display_board = new I2C(I2C.Port.kMXP, 0x70);

		_voltage = new byte[10];
		
		_osc = new byte[1];
		_osc[0]= (byte)0x21;

		_blink = new byte[1];
		_blink[0] = (byte)0x81;

		_bright = new byte[1];
		_bright[0] = (byte)0xEF;

		
		_a = new DigitalInput(19);
		_b = new DigitalInput(20);
		_buttons = new boolean[2];

		_pot = new AnalogInput(3);
		
	}

	private void set_battery_voltage()
	{
		_battery_voltage = _ds.getBatteryVoltage();

	}
	
	private static final byte[][] CHARS =
	{
    	{(byte)0b00000110, (byte)0b00000000}, //1
    	{(byte)0b11011011, (byte)0b00000000}, //2
    	{(byte)0b11001111, (byte)0b00000000}, //3
    	{(byte)0b11100110, (byte)0b00000000}, //4
    	{(byte)0b11101101, (byte)0b00000000}, //5
    	{(byte)0b11111101, (byte)0b00000000}, //6
    	{(byte)0b00000111, (byte)0b00000000}, //7
    	{(byte)0b11111111, (byte)0b00000000}, //8
    	{(byte)0b11101111, (byte)0b00000000}, //9
    	{(byte)0b00111111, (byte)0b00000000}, //0
    	{(byte)0b11110111, (byte)0b00000000}, //A
    	{(byte)0b10001111, (byte)0b00010010}, //B
    	{(byte)0b00111001, (byte)0b00000000}, //C
    	{(byte)0b00001111, (byte)0b00010010}, //D
    	{(byte)0b11111001, (byte)0b00000000}, //E
    	{(byte)0b11110001, (byte)0b00000000}, //F
    	{(byte)0b10111101, (byte)0b00000000}, //G
    	{(byte)0b11110110, (byte)0b00000000}, //H
    	{(byte)0b00001001, (byte)0b00010010}, //I
    	{(byte)0b00011110, (byte)0b00000000}, //J
    	{(byte)0b01110000, (byte)0b00001100}, //K
    	{(byte)0b00111000, (byte)0b00000000}, //L
    	{(byte)0b00110110, (byte)0b00000101}, //M
    	{(byte)0b00110110, (byte)0b00001001}, //N
    	{(byte)0b00111111, (byte)0b00000000}, //O
    	{(byte)0b11110011, (byte)0b00000000}, //P
    	{(byte)0b00111111, (byte)0b00001000}, //Q
    	{(byte)0b11110011, (byte)0b00001000}, //R
    	{(byte)0b10001101, (byte)0b00000001}, //S
    	{(byte)0b00000001, (byte)0b00010010}, //T
    	{(byte)0b00111110, (byte)0b00000000}, //U
    	{(byte)0b00110000, (byte)0b00100100}, //V
    	{(byte)0b00110110, (byte)0b00101000}, //W
    	{(byte)0b00000000, (byte)0b00101101}, //X
    	{(byte)0b00000000, (byte)0b00010101}, //Y
    	{(byte)0b00001001, (byte)0b00100100}, //Z

	};

	private void update_button_values()
	{
		_buttons[0] = _a.get();
		_buttons[1] = _b.get();
	}

	private void display_voltage()
	{
		
	}
	
	
	private void board_task()
	{

	}
}
