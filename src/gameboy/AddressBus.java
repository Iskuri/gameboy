/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gameboy;

/**
 *
 * @author iskuri
 */
public class AddressBus {

	public LCD lcd;
	public Joypad joypad;
	public LinkCable linkCable;

	int[] bus;

	/*
	 * Addresses:
	 *
	 * Joypad Input: 0xFF00
	 *
	 */

	// NOTE -- CHECK ON BIT ORDER!!

	public AddressBus() {

		// lcd object
		lcd = new LCD();

		// joypad object
		joypad = new Joypad();

		// link cable object - not necessary for now
		linkCable = new LinkCable();

		bus = new int[(int)Math.pow(2,16)];
	}

	public void updateHardware() throws Exception {

		bus = lcd.update(bus);
		bus = joypad.update(bus);

	}

	public int readBus(int pos) {
		return bus[pos];
	}

	public void writeBus(int pos, int val) {
		bus[pos] = val;
	}
}
