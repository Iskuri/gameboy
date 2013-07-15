/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gameboy;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * @author iskuri
 */
public class Memory {

	/*
		General Memory Map
		  0000-3FFF   16KB ROM Bank 00     (in cartridge, fixed at bank 00)
		  4000-7FFF   16KB ROM Bank 01..NN (in cartridge, switchable bank number)
		  8000-9FFF   8KB Video RAM (VRAM) (switchable bank 0-1 in CGB Mode)
		  A000-BFFF   8KB External RAM     (in cartridge, switchable bank, if any)
		  C000-CFFF   4KB Work RAM Bank 0 (WRAM)
		  D000-DFFF   4KB Work RAM Bank 1 (WRAM)  (switchable bank 1-7 in CGB Mode)
		  E000-FDFF   Same as C000-DDFF (ECHO)    (typically not used)
		  FE00-FE9F   Sprite Attribute Table (OAM)
		  FEA0-FEFF   Not Usable
		  FF00-FF7F   I/O Ports
		  FF80-FFFE   High RAM (HRAM)
		  FFFF        Interrupt Enable Register
	 */

	public AddressBus addressBus;
	public ROMHandler rom;
	public RAM ram;
	public RAM exram;
	public RAM workRam;
	public RAM vram;
	public RAM spriteTable;
	public RAM hram;


	public Memory() throws FileNotFoundException, IOException {

		// address bus object
		addressBus = new AddressBus();

		// get the game's ROM
		rom = new ROMHandler("Pokemon_Blue.gb");

		// print out the game's name from the ROM
		rom.printName();

		// load in the bios, for funsies ofc - it's probably illegal to play gameboy games without seeing that logo first ;)
		rom.loadBIOS();

		// initialize ram
		ram = new RAM();
		vram = new RAM();
		exram = new RAM();
		workRam = new RAM();
		spriteTable = new RAM();
		hram = new RAM();

	}

	public void writeMem(int pos, int val) throws Exception {

//		System.out.println("Writing: 0x"+Integer.toHexString(val)+" to 0x"+Integer.toHexString(pos));

		val = val & 0xff;

		if(pos < 0x4000) { // 16K ROM

			throw new Exception("You can't write to the ROM");

		} else if(pos < 0x8000) { // 16K ROM

			throw new Exception("You can't write to the ROM");

		} else if(pos < 0xA000) { // 8K VRAM

			vram.writeMem(pos - 0x8000, val);

		} else if(pos < 0xC000) { // 8K EXTERNAL RAM

//			System.out.println("writing to exram");

			exram.writeMem(pos - 0x0A000, val);

		} else if(pos < 0xD000) { // 4K WORK RAM

//			System.out.println("writing to work ram");

			workRam.writeMem(pos - 0xC000, val);

		} else if(pos < 0xE000) { // 4K WORK RAM 2

			throw new Exception("Find out what to put here");

		} else if(pos < 0xFE00) { // 4K WORK RAM 1 AGAIN

			throw new Exception("Don't touch this for gods sake");

		} else if(pos < 0xFEA0) { // Sprite Attribute Table

//			System.out.println("writing to sprite table");

			spriteTable.writeMem(pos, val);

		} else if(pos < 0xFF00) { // Not usable
			throw new Exception("Access addressssed illegal memory location");
		} else if(pos < 0xFF80) { // I/O Ports - This means AddressBus BOY

			addressBus.writeBus(pos, val);
			addressBus.updateHardware();

		} else if(pos < 0xFFFF) { // High RAM - Don't know what this is for, so throw an exception

			hram.writeMem(pos - 0xFF80, val);

		} else if(pos == 0xFFFF) {
			throw new Exception("This is to do with interrupts, you should not be here");
		} else {
			throw new Exception("You broke something, good luck finding it");
		}

	}

	public int readMem(int pos) throws Exception {

//		System.out.println("Reading from memory position: 0x"+Integer.toHexString(pos));
		
		if(pos < 0x4000) { // 16K ROM

			return rom.getByte(pos);

		} else if(pos < 0x8000) { // 16K ROM -- ALLOWS CHANGES FOR MEMORY BANK - MAKE WORK IF ASKED FOR

			throw new Exception("Implement rom bank");

		} else if(pos < 0xA000) { // 8K VRAM

			return vram.readMem(pos - 0x8000);

		} else if(pos < 0xC000) { // 8K EXTERNAL RAM

			return exram.readMem(pos - 0xA000);

		} else if(pos < 0xD000) { // 4K WORK RAM

			return workRam.readMem(pos - 0xC000);

		} else if(pos < 0xE000) { // 4K WORK RAM 2 -- switchable bank

			throw new Exception("Implement work RAM 2 and switchable banks");

		} else if(pos < 0xFE00) { // 4K WORK RAM 1 AGAIN

			return workRam.readMem(pos - 0xe00);

		} else if(pos < 0xFEA0) { // Sprite Attribute Table

			return spriteTable.readMem(pos);

		} else if(pos < 0xFF00) { // Not usable
			throw new Exception("Accessed illegal memory location");
		} else if(pos < 0xFF80) { // I/O Ports - This means AddressBus BOY

			addressBus.updateHardware();
			return addressBus.readBus(pos);

		} else if(pos < 0xFFFF) {
			return hram.readMem(pos - 0xFF80);

		} else if(pos == 0xFFFF) {
			throw new Exception("Make enabling interrupts work");
		} else {
			throw new Exception("You broke something, good luck finding it");
		}
	}


}
