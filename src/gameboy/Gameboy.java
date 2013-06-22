
package gameboy;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * @author iskuri
 */
public class Gameboy {

	public static Z80CPU cpu;
	public static Memory memory;


	/*
	 * ROM Gameboy Logo Header: 0104 - 0133
	 * ROM Tile Header : 0134-0143
	 */

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException, Exception {

		// cpu object - I think it's a z80 or something like that, I am sure I will find out when opcodes don't work
		cpu = new Z80CPU();
		memory = new Memory();

		cpu.run(0);
	}
}
