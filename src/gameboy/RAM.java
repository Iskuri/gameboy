/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gameboy;

import java.util.Random;

/**
 *
 * @author iskuri
 */
public class RAM {

	// ram 32k because it's the max it can be
	private int[] mem = new int[32 * 1024];

	public RAM() {

		for(int i = 0 ; i < mem.length ; i++) {
			mem[i] = (int) (Math.random() * ( 255 ));
		}
	}

	public void writeMem(int pos, int val) {
		mem[pos] = val;
	}

	public int readMem(int pos) {
		return mem[pos];
	}


}
