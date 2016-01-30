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
	private byte[] _output_array;

	private DigitalInput _a;
	private DigitalInput _b;
	private boolean[] _buttons;

	private AnalogInput _pot;

	private static enum STATE
	{
		Voltage, Position, Obstacle, Wait
	}
	
	private static String[] _positions = {"P  1", "P  2", "P  3", "P  4", "P  5"};
	private static String[] _obstacles = {"LBAR", "PORT", "DRAW", "MOAT", "FRIS", "RAMP", "SALP", "ROCK", "TERR"};

	int pos = 0;
	int obs = 0;
	
	private void DisplayInit()
	{
		_display_board = new I2C(I2C.Port.kMXP, 0x70);

		_output_array = new byte[10];

		_a = new DigitalInput(19);
		_b = new DigitalInput(20);
		_buttons = new boolean[2];

		_pot = new AnalogInput(3);

	}

	private void update_button_values()
	{
		_buttons[0] = _a.get();
		_buttons[1] = _b.get();
	}

	private byte[] output_voltage()
	{
		String voltage = Integer.toString((int) _ds.getBatteryVoltage());
		if (voltage.length() != 2)
		{
			voltage = voltage.substring(0, 2);
		}
		
		byte[] output = new byte[10];

		output[0] = (byte) (0b0000111100001111);
		
		output[2] = CHARS[31][0];// V
		output[3] = CHARS[31][1];// V
		output[4] = (byte) 0b00000000;// blank
		output[5] = (byte) 0b00000000;// blank
		output[6] = CHARS[voltage.charAt(1)][0];// second digit of
														// voltage
		output[7] = CHARS[voltage.charAt(1)][1];// second digit of
														// voltage
		output[8] = CHARS[voltage.charAt(0)][0];// first digit of voltage
		output[9] = CHARS[voltage.charAt(0)][1];// first digit of voltage
		
		return output;

	}

	private byte[] output_pos(String input)
	{
		byte[] output = new byte[10];
		
		output[0] = (byte) (0b0000111100001111);
		
		output[2] = CHARS[input.charAt(3)][0];
		output[3] = CHARS[input.charAt(3)][1];
		
		output[4] = output[5] = output[6] = output[7] = (byte) 0b00000000;
		
		output[8] = CHARS[input.charAt(0) - 55][0];
		output[9] = CHARS[input.charAt(0) - 55][1];
		
		return output;
	}
	
	private byte[] output_obs(String input)
	{
		byte[] output = new byte[10];
		
		output[0] = (byte) (0b0000111100001111);
		
		for (int i = 0; i < 4; i+= 2)
		{
			output[i+2] = CHARS[input.charAt(3-i) - 55][0];
			output[i+3] = CHARS[input.charAt(3-i) - 55][1];
		}
		
		
		return output;
	}

	private void board_task()
	{
		STATE mode = STATE.Voltage;

		update_button_values();
		
		
		if(!_buttons[0] && !_buttons[1])
		{
			mode = STATE.Voltage;
		}
		
		
		if (mode != STATE.Position && _buttons[0])
		{
			mode = STATE.Position; //just display current position on first press
		}
		if (mode == STATE.Position && _buttons[0])
		{
			pos++;
		}
		
		
		if (mode != STATE.Obstacle && _buttons[1])
		{
			mode = STATE.Obstacle; // display current obstacle on first press
		}
		if (mode == STATE.Obstacle && _buttons[1])
		{
			obs++;
		}
		
		
		
		if (mode == STATE.Voltage)
		{
			_output_array = output_voltage();
		}
		
		else if (mode == STATE.Position)
		{
			_output_array = output_pos(_positions[pos]);
		}
		
		else if (mode == STATE.Obstacle)
		{
			_output_array = output_obs(_obstacles[obs]);
		}

		_display_board.writeBulk(_output_array);
		
		
		try
		{
			Thread.sleep(300); //wait a while because people can't read that fast
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	// Thanks @Team 1493
	private static final byte[][] CHARS =
	{
			{ (byte) 0b00111111, (byte) 0b00000000 }, // 0; 0
			{ (byte) 0b00000110, (byte) 0b00000000 }, // 1; 1
			{ (byte) 0b11011011, (byte) 0b00000000 }, // 2; 2
			{ (byte) 0b11001111, (byte) 0b00000000 }, // 3; 3
			{ (byte) 0b11100110, (byte) 0b00000000 }, // 4; 4
			{ (byte) 0b11101101, (byte) 0b00000000 }, // 5; 5
			{ (byte) 0b11111101, (byte) 0b00000000 }, // 6; 6
			{ (byte) 0b00000111, (byte) 0b00000000 }, // 7; 7
			{ (byte) 0b11111111, (byte) 0b00000000 }, // 8; 8
			{ (byte) 0b11101111, (byte) 0b00000000 }, // 9; 9
			{ (byte) 0b11110111, (byte) 0b00000000 }, // A; 10
			{ (byte) 0b10001111, (byte) 0b00010010 }, // B; 11
			{ (byte) 0b00111001, (byte) 0b00000000 }, // C; 12
			{ (byte) 0b00001111, (byte) 0b00010010 }, // D; 13
			{ (byte) 0b11111001, (byte) 0b00000000 }, // E; 14
			{ (byte) 0b11110001, (byte) 0b00000000 }, // F; 15
			{ (byte) 0b10111101, (byte) 0b00000000 }, // G; 16
			{ (byte) 0b11110110, (byte) 0b00000000 }, // H; 17
			{ (byte) 0b00001001, (byte) 0b00010010 }, // I; 18
			{ (byte) 0b00011110, (byte) 0b00000000 }, // J; 19
			{ (byte) 0b01110000, (byte) 0b00001100 }, // K; 20
			{ (byte) 0b00111000, (byte) 0b00000000 }, // L; 21
			{ (byte) 0b00110110, (byte) 0b00000101 }, // M; 22
			{ (byte) 0b00110110, (byte) 0b00001001 }, // N; 23
			{ (byte) 0b00111111, (byte) 0b00000000 }, // O; 24
			{ (byte) 0b11110011, (byte) 0b00000000 }, // P; 25
			{ (byte) 0b00111111, (byte) 0b00001000 }, // Q; 26
			{ (byte) 0b11110011, (byte) 0b00001000 }, // R; 27
			{ (byte) 0b10001101, (byte) 0b00000001 }, // S; 28
			{ (byte) 0b00000001, (byte) 0b00010010 }, // T; 29
			{ (byte) 0b00111110, (byte) 0b00000000 }, // U; 30
			{ (byte) 0b00110000, (byte) 0b00100100 }, // V; 31
			{ (byte) 0b00110110, (byte) 0b00101000 }, // W; 32
			{ (byte) 0b00000000, (byte) 0b00101101 }, // X; 33
			{ (byte) 0b00000000, (byte) 0b00010101 }, // Y; 34
			{ (byte) 0b00001001, (byte) 0b00100100 }, // Z; 35

	};
}
