/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gameboy;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * @author iskuri
 */
public class ROMHandler {

	public byte[] romData;
	private byte[] tempRom = new byte[256];

	public ROMHandler(String filePath) throws FileNotFoundException, IOException {

		// change this to use params or some shit, this is pretty pathetic tbh
		File file = new File(filePath);
		romData = new byte[(int)file.length()];
		DataInputStream inputStream = new DataInputStream((new FileInputStream(file)));

		inputStream.readFully(romData);
		inputStream.close();

	}

	public void loadBIOS() throws IOException {

		overwriteBIOS();

	}

	public int readMem(int pos) {
		return (int) romData[pos];
	}

	private void overwriteBIOS() throws IOException {

		// get the bios
		ROMHandler biosRom = new ROMHandler("bios");

		for(int i = 0 ; i < 256 ; i++) {
			tempRom[i] = romData[i];
			romData[i] = (byte) biosRom.getByte(i);
		}

	}

	private void removeBIOS() {

		for(int i = 0 ; i < 256 ; i++) {
			romData[i] = tempRom[i];
		}

	}

	public byte getByte(int pointer) {

		return romData[pointer];
	}

	public void setByte(int pos, int val) {
		romData[pos] = (byte)val;
	}

	public void printName() {

		String gameName = "";

		for(int i = 0x134 ; i <= 0x143 ; i++) {
			gameName += Character.toString((char)romData[i]);
		}

		System.out.println("Starting game: "+gameName);
	}
}
