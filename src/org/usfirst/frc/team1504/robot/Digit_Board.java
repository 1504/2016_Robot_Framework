package org.usfirst.frc.team1504.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalInput;

/**
 * 
 *                              ./+sydmmNNNNNmmdys+/.                              
                        `/sdNNNNNNNNNNNNNNNNNNNNNNNds/`                         
                     .+hmhyssyhNNNNNNNNNNNNNNNNNNNNNNNNd+.                      
                   /hmo-        :sNNNNNNNNmdhyhhmNNNNNNNNNh/                    
                `+dNh` .s+dh`     .dNmms:`       `-+dNNNNNmmd+`                 
               /dmmh   -s/dd`      `mo`              :dmmmmmmmd/                
             `ymmmm/     ``         ::s:+d.           :mmmmmmmmmy.              
            -hddddd+                :hdoyM+           -ddddddddddd-             
``         :dddddddh.              -d++ys/           .ydddddddddddd:            
 /so/-    -hhhhhhhhhy:         `...:::-.....`     `:ohhhhhhhhhhhhhhh-           
  -yddmhs/yyyyyyyyyyyys/.     `-o----------//+++osyhyhyyyyyyyyyyyyhyy`          
   `sdmmNhyyyyyyyyyyyyyyys+:--.`yd:------.     `.--:+osyyyyyyyyyyyyyy+      `-/-
     /dmmssssssssssss+:.`       /MN:----.              `-:+sssssssssss..:+sdNd- 
      .hhoooooooo/-`            .ms `--`                    .:+oooooooshddmm+   
        +ooooo/-                `/`  ``                        `-+ooooohdds`    
        +ooo:`                   `                                ./ooohy-      
        /o:`                                                        .+o:        
        :.                THIS CODE WAS WRITTEN BY                                            .         
                    FRC TEAM 1504: THE DESPARATE PENGUINS                                                      
                          PUT IN A GOOD WORD FOR US.                                                    
                                                                                
          .-----..``                                                            
         `-----------.`                                                         
          --------------`                                  `.----.`             
          `---------------.                             `.---------             
           `----------------.                         `.-----------             
             .---------------.`                     `.------------.             
              `.---------------`                   `-------------.              
                `.--------------                  .--------------               
                   `.-----------`                .-------------.                
                      `..------.                .-------------.                 
                           ```                 `------------.`                  
                                               `----------.`                    
                                               `--------.`                      
                                                `....``                         
 * 
 * 
 *This code is a free-to-use Java library for FRC Teams using the MXP Digit Board with their roboRIO.
 *The code uses a set of enumerations called STATE. The STATE enumeration should contain all different sets of information being written to the board.
 *Once the STATE is determined, you must find the corresponding/desired information, and give it to the writeDigits class as a string.
 *Currently, the code displays the battery voltage by default, and if the potentiometer is rotated, displays a value from 0.0-10.0, snapped to the nearest half.
 *Finally, this code is written to run in its own thread. Use the start and stop
 */



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
	private boolean _run = false;

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

		System.out.println("MXP Board Initialization Successful.");
	}
	public void start()
	{
		if(_run)
			return;
		_run = true;
		_task_thread = new Thread(new Board_Task(this));
		_task_thread.start();
	}
	public void stop()
	{
		_run = false;
	}

	private DriverStation _ds = DriverStation.getInstance();

	private I2C _display_board;
	private DigitalInput _a;
	private DigitalInput _b;
	private static final int A_MASK = 0b0000000000000001;
	private static final int B_MASK = 0b0000000000000010;
	private volatile int _input_mask, _input_mask_rising, _input_mask_rising_last;


	private static long _timeout = 2500;

	private AnalogInput _potentiometer;
	private boolean _should_disp_potentiometer = false;
	private double _delay = 0.0;
	private double _last_delay = 0.0;

	/**	 * 
	 *Currently, the only information being displayed on the digit board is the voltage of the battery, and 
	 *if the potentiometer is rotated, a value between 0 and 10, snapped to the nearest half.
	 *Update this enumeration with the different sets of information you plan on sending to the digit board. 
	 *View an example at: https://github.com/1504/2016_Robot_Framework.
	 */
	private static enum STATE
	{
		Voltage, Potentiometer
	}

	private void DisplayInit()
	{
		_display_board = new I2C(I2C.Port.kMXP, 0x70);

		_a = new DigitalInput(19);
		_b = new DigitalInput(20);
		_potentiometer = new AnalogInput(7);
		
	}
	
	
	/**
	 * Function for updating buttons, and its helper functions.
	 */
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
	
	
	/**
	 * Checks the current value of the potentiometer, and maps the value to the nearest half between 0.0 and 10.0
	 */
	public void getPotentiometer()
	{
		double val = (double) _potentiometer.getAverageValue();//integer between 3 - 400
		_delay = Math.min((val/400), 10.0); //number between 0 and 10
		_delay = (Math.round(_delay * 2.0)) / 2.0;
		_delay = 10.0 - _delay;
		
		if (_delay != _last_delay)
		{
			_should_disp_potentiometer = true;
		}
		else
		{
			_should_disp_potentiometer = false;
		}
		
		_last_delay = _delay;
	}
	
	
	/**
	 * Computes the byte array to display the current voltage.
	 * @return The array of bytes to write.
	 */
	private String output_voltage()
	{
		double voltage = _ds.getBatteryVoltage();
		String voltage_string = Double.toString(voltage);
		
		if (voltage_string.length() != 3)
		{
			voltage_string = voltage_string.substring(0, 3);
			if (voltage < 10.0)
			{
				voltage_string = voltage_string.substring(0, 2);
				voltage_string = " " + voltage_string;
			}
		}
		voltage_string += "V";
		

		return voltage_string;
	}
	
	
	/**
	 * Outputs the delay computed in getPotentiometer(), indicating how long the robot will wait at the beginning of autonomous
	 * @param d - the delay
	 * @return the string to write.
	 */
	private String output_potentiometer(double d)
	{
		String delay = Double.toString(d);
		
		return delay;
	}
	
	public void writeDigits(String output)
	{
		output += "    "; // Cheap and easy way to clear and prevent index out of bounds errors
		
		byte[] output_buffer = new byte[10];
		output_buffer[0] = (byte)(0b0000111100001111);
		
		int offset = 0;
		
		for(int i = 0; i < 4; i++)
		{
			char letter = output.charAt(i + offset);

			while(/*letter < 32 ||*/ letter == '.')
			{
				if(letter == '.')
				{
					if(i != 0)
						output_buffer[(4-i)*2+3] |= (byte)0b01000000;
				}
				
				offset++;
				letter = output.charAt(i + offset);
			}
			output_buffer[(3-i)*2+2] = CHARS[letter-32][0];
			output_buffer[(3-i)*2+3] = CHARS[letter-32][1];
		}
		
		_display_board.writeBulk(output_buffer);
	}
	
	
	/**
	 * The controller function, doing logic to decide what to display based on the inputs it is getting. This is the function being used by the thread.
	 */
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

		while (_run)
		{	
			update();
			getPotentiometer();

			if ((System.currentTimeMillis() - refresh) > _timeout)
			{
				mode = STATE.Voltage;
			}
			
			boolean update_refresh = true;
			
			if (getAOnRisingEdge())
			{
				/*
				 * What happens when button A is pressed.
				 */
			}
			else if (getBOnRisingEdge())
			{
				/*
				 * What happens when button B is pressed.
				 */
			}
			else if (_should_disp_potentiometer)
			{
				/*
				 * What happens if the potentiometer has been turned.
				 */
			}
			else
			{
				update_refresh = false;
			}
			
			if (update_refresh)
			{
				refresh = System.currentTimeMillis();
			}
			
			//This block of if/else statements is for calling the writeBulk function, determining what information will be written to the board.
			if (mode == STATE.Voltage)
			{
				writeDigits(output_voltage());
			}
			else if (mode == STATE.Potentiometer)
			{
				writeDigits(output_potentiometer(_delay));
			}
			else
			{
				writeDigits(output_voltage());
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
			{(byte)0b00000000, (byte)0b00000000}, //   
			{(byte)0b00000110, (byte)0b00000000}, // ! 
			{(byte)0b00100000, (byte)0b00000010}, // " 
			{(byte)0b11001110, (byte)0b00010010}, // # 
			{(byte)0b11101101, (byte)0b00010010}, // $ 
			{(byte)0b00100100, (byte)0b00100100}, // % 
			{(byte)0b01011101, (byte)0b00001011}, // & 
			{(byte)0b00000000, (byte)0b00000100}, // ' 
			{(byte)0b00000000, (byte)0b00001100}, // ( 
			{(byte)0b00000000, (byte)0b00100001}, // ) 
			{(byte)0b11000000, (byte)0b00111111}, // * 
			{(byte)0b11000000, (byte)0b00010010}, // + 
			{(byte)0b00000000, (byte)0b00100000}, // , 
			{(byte)0b11000000, (byte)0b00000000}, // - 
			{(byte)0b00000000, (byte)0b00000000}, // . 
			{(byte)0b00000000, (byte)0b00100100}, // / 
			{(byte)0b00111111, (byte)0b00100100}, // 0 
			{(byte)0b00000110, (byte)0b00000000}, // 1 
			{(byte)0b11011011, (byte)0b00000000}, // 2 
			{(byte)0b10001111, (byte)0b00000000}, // 3 
			{(byte)0b11100110, (byte)0b00000000}, // 4 
			{(byte)0b01101001, (byte)0b00001000}, // 5 
			{(byte)0b11111101, (byte)0b00000000}, // 6 
			{(byte)0b00000111, (byte)0b00000000}, // 7 
			{(byte)0b11111111, (byte)0b00000000}, // 8 
			{(byte)0b11101111, (byte)0b00000000}, // 9 
			{(byte)0b00000000, (byte)0b00010010}, // : 
			{(byte)0b00000000, (byte)0b00100010}, // ; 
			{(byte)0b00000000, (byte)0b00001100}, // < 
			{(byte)0b11001000, (byte)0b00000000}, // = 
			{(byte)0b00000000, (byte)0b00100001}, // > 
			{(byte)0b10000011, (byte)0b00010000}, // ? 
			{(byte)0b10111011, (byte)0b00000010}, // @ 
			{(byte)0b11110111, (byte)0b00000000}, // A 
			{(byte)0b10001111, (byte)0b00010010}, // B 
			{(byte)0b00111001, (byte)0b00000000}, // C 
			{(byte)0b00001111, (byte)0b00010010}, // D 
			{(byte)0b11111001, (byte)0b00000000}, // E 
			{(byte)0b01110001, (byte)0b00000000}, // F 
			{(byte)0b10111101, (byte)0b00000000}, // G 
			{(byte)0b11110110, (byte)0b00000000}, // H 
			{(byte)0b00000000, (byte)0b00010010}, // I 
			{(byte)0b00011110, (byte)0b00000000}, // J 
			{(byte)0b01110000, (byte)0b00001100}, // K 
			{(byte)0b00111000, (byte)0b00000000}, // L 
			{(byte)0b00110110, (byte)0b00000101}, // M 
			{(byte)0b00110110, (byte)0b00001001}, // N 
			{(byte)0b00111111, (byte)0b00000000}, // O 
			{(byte)0b11110011, (byte)0b00000000}, // P 
			{(byte)0b00111111, (byte)0b00001000}, // Q 
			{(byte)0b11110011, (byte)0b00001000}, // R 
			{(byte)0b11101101, (byte)0b00000000}, // S 
			{(byte)0b00000001, (byte)0b00010010}, // T 
			{(byte)0b00111110, (byte)0b00000000}, // U 
			{(byte)0b00110000, (byte)0b00100100}, // V 
			{(byte)0b00110110, (byte)0b00101000}, // W 
			{(byte)0b00000000, (byte)0b00101101}, // X 
			{(byte)0b00000000, (byte)0b00010101}, // Y 
			{(byte)0b00001001, (byte)0b00100100}, // Z 
			{(byte)0b00111001, (byte)0b00000000}, // [ 
			{(byte)0b00000000, (byte)0b00001001}, // \ 
			{(byte)0b00001111, (byte)0b00000000}, // ] 
			{(byte)0b00000011, (byte)0b00100100}, // ^ 
			{(byte)0b00001000, (byte)0b00000000}, // _ 
			{(byte)0b00000000, (byte)0b00000001}, // ` 
			{(byte)0b01011000, (byte)0b00010000}, // a 
			{(byte)0b01111000, (byte)0b00001000}, // b 
			{(byte)0b11011000, (byte)0b00000000}, // c 
			{(byte)0b10001110, (byte)0b00100000}, // d 
			{(byte)0b01011000, (byte)0b00100000}, // e 
			{(byte)0b01110001, (byte)0b00000000}, // f 
			{(byte)0b10001110, (byte)0b00000100}, // g 
			{(byte)0b01110000, (byte)0b00010000}, // h 
			{(byte)0b00000000, (byte)0b00010000}, // i 
			{(byte)0b00001110, (byte)0b00000000}, // j 
			{(byte)0b00000000, (byte)0b00011110}, // k 
			{(byte)0b00110000, (byte)0b00000000}, // l 
			{(byte)0b11010100, (byte)0b00010000}, // m 
			{(byte)0b01010000, (byte)0b00010000}, // n 
			{(byte)0b11011100, (byte)0b00000000}, // o 
			{(byte)0b01110000, (byte)0b00000001}, // p 
			{(byte)0b10000110, (byte)0b00000100}, // q 
			{(byte)0b01010000, (byte)0b00000000}, // r 
			{(byte)0b10001000, (byte)0b00001000}, // s 
			{(byte)0b01111000, (byte)0b00000000}, // t 
			{(byte)0b00011100, (byte)0b00000000}, // u 
			{(byte)0b00000100, (byte)0b00001000}, // v 
			{(byte)0b00010100, (byte)0b00101000}, // w 
			{(byte)0b11000000, (byte)0b00101000}, // x 
			{(byte)0b00001100, (byte)0b00001000}, // y 
			{(byte)0b01001000, (byte)0b00100000}, // z 
			{(byte)0b01001001, (byte)0b00100001}, // { 
			{(byte)0b00000000, (byte)0b00010010}, // | 
			{(byte)0b10001001, (byte)0b00001100}, // } 
			{(byte)0b00100000, (byte)0b00000101}, // ~ 
			{(byte)0b11111111, (byte)0b00111111}  // DEL 
		};
}
