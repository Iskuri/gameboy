/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gameboy;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 *
 * @author iskuri
 */
public class Joypad {

	// Sawted

	int statusAddress = 0xFF00;

	boolean upPressed = false;
	boolean downPressed = false;
	boolean leftPressed = false;
	boolean rightPressed = false;
	boolean APressed = false;
	boolean BPressed = false;
	boolean startPressed = false;
	boolean selectPressed = false;

	int[] busData;

	UserController keyController = new UserController();

	// Bus Byte: 0xFF00

	/*
	 *
		Bit 7 - Not used
		Bit 6 - Not used
		Bit 5 - P15 Select Button Keys      (0=Select)
		Bit 4 - P14 Select Direction Keys   (0=Select)
		Bit 3 - P13 Input Down  or Start    (0=Pressed) (Read Only)
		Bit 2 - P12 Input Up    or Select   (0=Pressed) (Read Only)
		Bit 1 - P11 Input Left  or Button B (0=Pressed) (Read Only)
		Bit 0 - P10 Input Right or Button A (0=Pressed) (Read Only)
	 */
	public Joypad() {

	}

	// implement the updatestatus button returns

	public int[] update(int[] bus) {

		busData = bus;

		// Implement this with the restructure

//		if((Gameboy.addressBus.bus[statusAddress] & 0x20) == 0x20) {
//
//			// button keys
//			setButtonKeys();
//
//
//		} else if((Gameboy.addressBus.bus[statusAddress] & 0x10) == 0x10) {
//
//			// direction keys
//			setDirectionKeys();
//
//
//		} else {
//
//			// nathing
//
//		}


		return busData;
	}

	private void setButtonKeys() {

//		Gameboy.addressBus.bus[statusAddress] = Gameboy.addressBus.bus[statusAddress] & 0x20;

	}

	private void setDirectionKeys() {

//		Gameboy.addressBus.bus[statusAddress] = Gameboy.addressBus.bus[statusAddress] & 0x10;

		if(downPressed) {

		} else {

		}

		if(upPressed) {

		} else {

		}

		if(leftPressed) {

		} else {

		}

		if(rightPressed) {

		} else {

		}
	}

	public void clearStatus() {
//		Gameboy.addressBus.bus[statusAddress] = Gameboy.addressBus.bus[statusAddress] ^ Gameboy.addressBus.bus[statusAddress];
	}

	private class UserController implements KeyListener {

		public UserController() {

		}

		@Override
		public void keyTyped(KeyEvent ke) {
//			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void keyPressed(KeyEvent ke) {
//			throw new UnsupportedOperationException("Not supported yet.");

		}

		@Override
		public void keyReleased(KeyEvent ke) {
//			throw new UnsupportedOperationException("Not supported yet.");
		}

	}
}
