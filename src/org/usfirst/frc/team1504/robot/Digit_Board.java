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
	private boolean _do_things = false;

	public static Digit_Board getInstance()
	{
		return Digit_Board.instance;
	}

	protected Digit_Board()
	{
		_task_thread = new Thread(new Board_Task(this), "1504_Display_Board");
		_task_thread.setPriority((Thread.NORM_PRIORITY + Thread.MAX_PRIORITY) / 2);

		DisplayInit();
		
		start();

		System.out.println("MXP Leader, standing by.");
	}

	public void start()
	{
		if(_do_things)
			return;
		_do_things = true;
		_task_thread = new Thread(new Board_Task(this));
		_task_thread.start();
	}

	public void stop()
	{
		_do_things = false;
	}

	private DriverStation _ds = DriverStation.getInstance();

	private I2C _display_board;
	private DigitalInput _a;
	private DigitalInput _b;
	//taken from mike
	private static final int A_MASK = 0b0000000000000001;
	private static final int B_MASK = 0b0000000000000010;
	private volatile int _input_mask, _input_mask_rising, _input_mask_rising_last;


	private static long _timeout = 2500;

	private AnalogInput _pot;

	private static enum STATE
	{
		Voltage, Position, Obstacle, Wait
	}

	private static String[] _positions =
	{ "P  1", "P  2", "P  3", "P  4", "P  5" };
	private static String[] _obstacles =
	{ "LBAR", "PORT", "DRAW", "MOAT", "FRIS", "RAMP", "SALP", "ROCK", "TERR"};

	int pos = 0;
	int obs = 0;

	private void DisplayInit()
	{
		_display_board = new I2C(I2C.Port.kMXP, 0x70);

		_a = new DigitalInput(19);
		_b = new DigitalInput(20);
		_pot = new AnalogInput(7);

		
	}
	
	public void update()
	{
		int current_mask = get_input_mask();

		_input_mask |= current_mask;

		_input_mask_rising |= (~_input_mask_rising_last & current_mask);
		_input_mask_rising_last = current_mask;
	}
	
	private int get_input_mask()
	{
		int mask = 0;
		mask |= (getA() ? 1 : 0) << A_MASK;
		mask |= (getB() ? 1 : 0) << B_MASK;
		return mask;
	}
	
	private boolean getRawButtonOnRisingEdge(int button_mask)
	{
		button_mask = button_mask << 1;
		// Compute a clearing mask for the button.
		int clear_mask = 0b1111111111111111 - button_mask;
		// Get the value of the button - 1 or 0
		boolean value = (_input_mask_rising & button_mask) != 0;
		// Mask this and only this button back to 0
		_input_mask_rising &= clear_mask;
		return value;
	}	
	
	private boolean getRawButtonLatch(int button_mask)
	{
		// Compute a clearing mask for the button.
		int clear_mask = 0b1111111111111111 - button_mask;
		// Get the value of the button - 1 or 0
		boolean value = (_input_mask & button_mask) != 0;
		// Mask this and only this button back to 0
		_input_mask &= clear_mask;
		return value;
	}
	
	public boolean getA()
	{
		return(!_a.get());
	}
	
	public boolean getALatch()
	{
		return getRawButtonLatch(A_MASK);
	}
	
	public boolean getAOnRisingEdge()
	{
		return getRawButtonOnRisingEdge(A_MASK);
	}
	
	public boolean getB()
	{
		return(!_b.get());
	}
	
	public boolean getBLatch()
	{
		return getRawButtonLatch(B_MASK);
	}
	
	public boolean getBOnRisingEdge()
	{
		return getRawButtonOnRisingEdge(B_MASK);
	}
	
	public double getPot()
	{
		double val = (double) _pot.getAverageValue();//integer between 3 - 400
		double delay = Math.min((val/400), 10.0); //number between 0 and 10
		return delay;
	}
	
	private byte[] output_voltage()
	{
		String voltage = Double.toString(_ds.getBatteryVoltage());
		if (voltage.length() != 4)
		{
			voltage = voltage.substring(0, 4);
		}

		byte[] output = new byte[10];

		byte second_digit_two = CHARS[voltage.charAt(1) - 48][1];		
		second_digit_two |= (byte)0b01000000;	
				
		output[0] = (byte) (0b0000111100001111);
		
		output[2] = CHARS[31][0];// V
		output[3] = CHARS[31][1];// V
		output[4] = CHARS[voltage.charAt(3) - 48][0];;// third digit
		output[5] = CHARS[voltage.charAt(3) - 48][1];;// third digit
		output[6] = CHARS[voltage.charAt(1) - 48][0];// second digit of voltage
		output[7] = second_digit_two;// second digit of voltage, with decimal point.
		output[8] = CHARS[voltage.charAt(0) - 48][0];// first digit of voltage
		output[9] = CHARS[voltage.charAt(0) - 48][1];// first digit of voltage

		return output;

	}

	private byte[] output_pos(String input)
	{
		byte[] output = new byte[10];

		output[0] = (byte) (0b0000111100001111);

		output[2] = CHARS[input.charAt(3) - 48][0];
		output[3] = CHARS[input.charAt(3) - 48][1];

		output[4] = output[5] = output[6] = output[7] = (byte) 0b00000000;

		output[8] = CHARS[input.charAt(0) - 55][0];
		output[9] = CHARS[input.charAt(0) - 55][1];

		return output;
	}

	private byte[] output_obs(String input)
	{
		byte[] output = new byte[10];

		output[0] = (byte) (0b0000111100001111);

			for (int i = 0; i < input.length(); i++)
			{
				output[(2*i)+2] = CHARS[input.charAt(3-i) - 55][0];
				output[(2*i)+3] = CHARS[input.charAt(3-i) - 55][1];
			}
//			output[2] = CHARS[input.charAt(3) - 55][0];
//			output[3] = CHARS[input.charAt(3) - 55][1];
//			output[4] = CHARS[input.charAt(2) - 55][0];
//			output[5] = CHARS[input.charAt(2) - 55][1];
//			output[6] = CHARS[input.charAt(1) - 55][0];
//			output[7] = CHARS[input.charAt(1) - 55][1];
//			output[8] = CHARS[input.charAt(0) - 55][0];
//			output[9] = CHARS[input.charAt(0) - 55][1];


		return output;
	}

	private void board_task()
	{	
    	byte[] osc = new byte[1];
    	byte[] blink = new byte[1];
    	byte[] bright = new byte[1];
    	osc[0] = (byte)0x21;
    	blink[0] = (byte)0x81;
    	bright[0] = (byte)0xEF;
	
		_display_board.writeBulk(osc);
		_display_board.writeBulk(bright);
		_display_board.writeBulk(blink);
		
		STATE mode = STATE.Voltage;

		long refresh = 0;

		while (_do_things)
		{	
			update();

			if ((System.currentTimeMillis() - refresh) > _timeout)
			{
				mode = STATE.Voltage;
			}
			
			boolean update_refresh = true;
			
			if (getAOnRisingEdge())
			{
				if (mode == STATE.Position)
				{
					pos = (pos + 1)%_positions.length; // just display current position on first press
				}
				mode = STATE.Position;
				
//				System.out.println("pos");
			}
			else if (getBOnRisingEdge())
			{
				if (mode == STATE.Obstacle)
				{
					obs = (obs + 1)%_obstacles.length;
				}
				mode = STATE.Obstacle; // display current obstacle on first press	
			
//				System.out.println("obs");
			}
			else
			{
				update_refresh = false;
			}
			
			if (pos == 0)
			{
				obs = 0;
			}
			if (pos > 0 && obs == 0)
			{
				obs++;
			}
			
			if (update_refresh)
			{
				refresh = System.currentTimeMillis();
			}

			if (mode == STATE.Position)
			{
				_display_board.writeBulk(output_pos(_positions[pos]));
			}

			else if (mode == STATE.Obstacle)
			{
				_display_board.writeBulk(output_obs(_obstacles[obs]));
			}
			else
			{
				_display_board.writeBulk(output_voltage());
			}


			
			try
			{
				Thread.sleep(40); // wait a while because people can't read that
									// fast
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
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
