/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gameboy;

/**
 *
 * @author iskuri
 */
public class Z80CPU {

	/*
	 * General Memory Map
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

	/*
	 * Clock speed is 4MHz - make sure to implement this one day
	 */

	private int stackPointer = 0;
	private int programCounter = 0;

	private int registerA;
	private int registerB;
	private int registerC;
	private int registerD;
	private int registerE;
	private int registerF;
	private int registerH;
	private int registerL;

	private int registerIX;
	private int registerIY;

	// F Flags
	private static int fZero = 7;
	private static int fAddSub = 6;
	private static int fHalfCarry = 5;
	private static int fCarry = 4;

	// 16 bit address bus
	private int[] addressBus = new int[2^16];

	private int progressCounter = 0;
	
	boolean runDebug = false;

	public Z80CPU() {

	}

	private void updateFFlags(int flagValue) {

		if(flagValue == 0) {
			f(setBit(fZero, f()));
//			echo("The byte was zero");
		} else {
			f(resetBit(fZero, f()));
//			echo("The byte was not zero");
		}

	}

	private int reverseEndian(int int0, int int1) {

		// re-enable if reverse endian is wrong
		int sixteenBit = (int0 | int1 << 8);
//		int sixteenBit = (int1 | int0 << 8);

		return sixteenBit;
	}

	private void setInterrupt(boolean setting) {

	}

	private void a(int val) {

		registerA = val & 0xFF;
	}

	private int a() {

		return registerA & 0xff;
	}

	private int b() {

		return registerB & 0xff;
	}

	private void b(int val) {

		registerB = val & 0xFF;
	}

	private int c() {
		return registerC & 0xff;
	}

	private void c(int val) {
		registerC = val & 0xff;
	}

	private int d() {
		return registerD & 0xff;
	}

	private void d(int val) {
	    
		registerD = val & 0xFF;
	}

	private int e() {
		return registerE & 0xff;
	}

	private void e(int val) {
		registerE = val & 0xFF;
	}

	private int f() {
		return registerF & 0xff;
	}

	private void f(int val) {
		registerF = val & 0xFF;
	}

	private int h() {
		return registerH & 0xff;
	}

	private void h(int val) {
		registerH = val & 0xFF;
	}

	private int l() {
		return registerL & 0xff;
	}

	private void l(int val) {
		registerL = val & 0xFF;
	}

	// make sure this is in the right fricking order
	private int af() {
		return (f() | a() << 8);
	}

	private void af(int val) {

		int aVal = val & 0xFF00;
		int fVal = val & 0xFF;

		a(aVal >> 8);
		f(fVal);
	}

	private int de() {
		return (e() | d() << 8);
	}

	private void de(int val) {

//		echo("Setting val to 0x"+Integer.toHexString(val));
		
		int dVal = val & 0xFF00;
		int eVal = val & 0xFF;

		d(dVal >> 8);
		e(eVal);
	}

	private int bc() {

		return (c() | b() << 8);
	}

	private void bc(int val) {

		int bVal = val & 0xFF00;
		int cVal = val & 0xFF;

		b(bVal >> 8);
		c(cVal);
	}

	private int hl() {
		return (l() | h() << 8);
	}

	private void hl(int val) {

		int hVal = val & 0xFF00;
		int lVal = val & 0xFF;

		h(hVal >> 8);
		l(lVal);
	}

	private void sp(int pos) {
		stackPointer = pos & 0xffff;
	}

	private int sp() {
		return stackPointer;
	}

	private void writeBit(int pos, int val) throws Exception {
		Gameboy.memory.writeMem(pos, val);
	}

	private int setBit(int bit, int val) {

		int shift = 1 << bit;

		return val | shift;
	}

	private int resetBit(int bit, int val) {

		int shift = val & ~(1 << bit);

		return shift;
	}

	private void testBit(int bit, int val) throws Exception {


		if(((1 << bit) & val) == 0) {
			f(setBit(fZero, f()));
		} else {
			f(resetBit(fZero, f()));
		}

	}

	private boolean checkBit(int bit, int value) {

		return ((1 << bit) & value) != 0;
	}

	private boolean zero() {

		return ((1 << fZero) & f()) != 0;
	}

	private int read8(int pos) throws Exception {
		return Gameboy.memory.readMem(pos);
	}

	private int read16(int pos) throws Exception {

		// Work out which is right, very significant
		return Gameboy.memory.readMem(pos) | (Gameboy.memory.readMem(pos + 1) << 8);
	}

	private void write16(int pos, int val) throws Exception {

		int val1 = val & 0xff;
		int val2 = (val & 0xff00) >> 8;

		Gameboy.memory.writeMem(pos, val1 & 0xff);
		Gameboy.memory.writeMem(pos + 1, val2 & 0xff);
	}

	private void write8(int pos, int val) throws Exception {
		Gameboy.memory.writeMem(pos, val & 0xff);
	}

	// calls a subroutine
	private int call(int point) throws Exception {
		return run(point);
	}

	private void push(int val) throws Exception {
		write16(sp()-1, val);
		sp(sp() - 2);

	}

	private int pop() throws Exception {

		sp(sp() + 2);
		int val = read16(sp()-1);

		return val;
	}

	// keep an eye on this
	private int rl(int val) {

		val = val << 1;

		int carry = (val & 0x100) >> 8;

		val = (val & 0xff) | carry;

		return val;
	}

	private int signify(int val) {

		if(((1 << 7) & val) == 0) {
			return val;
		} else {
			return (val ^ 0xff) * -1;

		}

	}

	private void echo(String string) {

		System.out.println(string);

	}

	public int run(int pointer) throws Exception {

		// add a switch case to process each instruction as it comes

		while(true) {
			
			// for keeping progress on where i am and where the code is going
			progressCounter++;
			
//			if(pointer == 0x8f) {
//				throw new Exception("Got to pointer 0x8f");
//			}
			
			int instruction = Gameboy.memory.readMem(pointer);

			int param1;
			int param2;
			int param3;

			// not always used but may as well stick in the top, they're gonna be used a lot
			param1 = Gameboy.memory.readMem(pointer+1) & 0xff;
			param2 = Gameboy.memory.readMem(pointer+2) & 0xff;
			param3 = Gameboy.memory.readMem(pointer+3) & 0xff;

			switch((int)instruction & 0xFF) {

				case 0x00: // null int - tis a NOP

					// do nothing, it's a nop , write a check in for when this occures
					pointer++;

					break;
				case 0x31: // LD Stack Pointer

					sp(reverseEndian(param1, param2));

					// move by 3 points because the next 2 are instructions
					pointer += 3;

					break;
				case 0xAF: // XOR A function - TESTING

					a(a() ^ a());

					pointer += 1;

					break;

				case 0x21: // LD HL

					// 16 bit
					hl((reverseEndian(param1,param2)));

					pointer += 3;
					break;

				case 0x32: //  LD function (HL -), A - work out if this is right

					writeBit(hl(), a());
					hl(hl() - 1);

					pointer += 1;
					break;
				case 0xcb: // some bit shifting thing

					// time for ANOTHER switch case
					switch(param1) {

						case 0x7c: // BIT 7, H

							testBit(7,h());

							break;

						case 0x11: // RL C - rotate left

							c(rl(c()));

							break;
						default:
							throw new Exception("Unknown subopcode: 0x"+Integer.toHexString(param1)+", please work out what it's for");
					}


					pointer += 2;
					break;

				case 0x20: // JR NZ

					echo("Zero setting is: "+zero());
					
					if(!zero()) {
						
						echo("Jumping from pointer: 0x"+Integer.toHexString(pointer));
						pointer = pointer + signify(param1) + 1;
						echo("Jumping to pointer: 0x"+Integer.toHexString(pointer));
					} else {
						pointer += 2;
					}

					break;

				case 0xe: // LD C, N

					c(param1);

//					echo(progressCounter+" Currently doing LD C at pointer: 0x"+Integer.toHexString(pointer) +" : 0x"+Integer.toHexString(param1));
					
					pointer += 2;

					break;

				case 0x3e: // LD A

					a(param1);

					pointer += 2;

					break;

				case 0xe2: // LD C, A

					write8(c()+0xFF00,a());

					pointer += 1;
					break;
				case 0x0c: // INC C

					c(c() + 1);

					pointer++;
					break;

				case 0x77: // LD (HL), A

					write8(hl(),a());

					pointer++;

					break;

				case 0xfb: // enable interrupts

					// make sure to make this function active at some point
					setInterrupt(true);

					pointer++;
					break;

				case 0xe0: // LD (0xFF + param), A

					write8(0xFF00 + param1, a());

					pointer += 2;
					break;

				case 0x11: // LD DE nn

					de(reverseEndian(param1, param2));

					pointer += 3;

					break;
				case 0x1a: // LD A, (DE)

					a(read8(de()));

					pointer++;

					break;

				case 0xcd: // CALL - Calls a subroutine from the params

					int reverse = reverseEndian(param1,param2);

					call(reverse);

					pointer += 3;

					break;

				case 0x4f: // LD C, A

					c(a());

					pointer++;
					break;

				case 0x06: // LD B, n

					b(param1);

					pointer += 2;
					break;

				case 0xc5: // PUSH BC

					push(bc());

					pointer++;
					break;

				case 0x17: // RL A

					a(rl(a()));

					pointer++;
					break;

				case 0xc1: // POP BC

					bc(pop());

					pointer++;
					break;

				case 0x05: // DEC B

					b(b() - 1);

					updateFFlags(b());

					pointer++;
					break;
				case 0x22: // LD (HL+), A

					write8(hl(),a());
					hl(hl()+1);

					pointer += 2;
					break;

				case 0xc9: // Apparently this is a return!

					return 0;

				case 0x13: // INC DE

					de(de() + 1);

					pointer++;
					break;

				case 0x7b: // LD A, E

					a(e());

					pointer++;
					break;

				case 0xfe: // CP NN

					int flagVal = a() - param1;

					updateFFlags(flagVal);

					pointer += 2;

					break;

				case 0xea: // LD (NN), A

					write8(reverseEndian(param1,param2),a());

					pointer += 3;

					break;

				case 0x3d: // DEC A

					a(a() - 1);

					echo(progressCounter+" Descending A at pointer: 0x"+Integer.toHexString(pointer) +" to 0x"+Integer.toHexString(a()));
					
					updateFFlags(a());

					pointer++;

					break;

				case 0x28: // JR Z

					if(zero()) {
						// work out if that -1 should be there, it doesn't seem right
						pointer = pointer + signify(param1) + 1;

					} else {
						pointer += 2;
					}

					break;

				case 0x0d: // DEC C

					c(c() - 1);

					updateFFlags(c());

//					echo(c());
					
					pointer++;

					break;

				case 0x2e: // LD L N

					echo("Loading into L: 0x"+Integer.toHexString(param1));
					
					l(param1);

					pointer += 2;

					break;

				case 0x18: // JR - Unconditional jump

					echo(progressCounter+" At 0x"+Integer.toHexString(pointer)+" going to: 0x"+Integer.toHexString(pointer + signify(param1) + 1));
					
					pointer = pointer + signify(param1) + 1;

					break;

				case 0xf3: // DI - Disable Interrupts!

					setInterrupt(false);

					pointer++;

					break;

				case 0x67: // LD H, A

					h(a());

					pointer++;

					break;

				case 0x57: // LD D, A

					d(a());

					pointer++;

					break;

				case 0x04: // INC B

					b(b() + 1);

					updateFFlags(b());

					pointer++;

					break;

				case 0x1e: // LD E, N

					e(param1);

					pointer += 2;

					break;

				case 0xf0: // LD A, (FF00 + n)

					a(read8(0xFF00 + param1));

					pointer += 2;

					break;

				case 0x1d: // DEC E

					d(d() - 1);

					updateFFlags(d());

					pointer++;

					break;

				case 0x24: // INC H

					h(h() + 1);

					updateFFlags(h());

					pointer++;
					break;

				case 0x7c: // LD A, H

					a(h());

					updateFFlags(a());

					pointer++;
					break;

				case 0x90: // SUB A, B

					a(a() - b());

					updateFFlags(a());

					pointer++;
					break;

				case 0x15: // DEC D

					d(d() - 1);
					
					updateFFlags(d());

					pointer++;
					break;
				default:

					throw new Exception("Unknown opcode: 0x"+Integer.toHexString(instruction & 0xff)+", please work out what it's for");
			}

		}

	}


}
