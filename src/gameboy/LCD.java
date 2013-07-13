package gameboy;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author iskuri
 */
public class LCD {

	/*
	 * Screen resolution: 160x144
	 *
	 */
	private byte[][] screen = new byte[160][144];
	private DrawSpace drawSpace;

	private int[] busData;

	private boolean activeScreen = false;
	private int tileMapStart = 0x9800;
	private boolean windowDisplay = false;
	private int bgTileDataStart = 0x8800;
	private int bgTileMapStart = 0x9800;
	private int spriteY = 8;
	private int spriteX = 8;
	private boolean enableSprites = false;
	private boolean displayBackground = false;

	int scrollY;
	int scrollX;
	int x;
	int y;

	private int ly;
	private int lyc;

	private int lcdc = 0;

	/*
	FF40 - LCDC - LCD Control (R/W)
		Bit 7 - LCD Display Enable             (0=Off, 1=On)
		Bit 6 - Window Tile Map Display Select (0=9800-9BFF, 1=9C00-9FFF)
		Bit 5 - Window Display Enable          (0=Off, 1=On)
		Bit 4 - BG & Window Tile Data Select   (0=8800-97FF, 1=8000-8FFF)
		Bit 3 - BG Tile Map Display Select     (0=9800-9BFF, 1=9C00-9FFF)
		Bit 2 - OBJ (Sprite) Size              (0=8x8, 1=8x16)
		Bit 1 - OBJ (Sprite) Display Enable    (0=Off, 1=On)
		Bit 0 - BG Display (for CGB see below) (0=Off, 1=On)
	 */

	/*
	FF41 - STAT - LCDC Status (R/W)
	  Bit 6 - LYC=LY Coincidence Interrupt (1=Enable) (Read/Write)
	  Bit 5 - Mode 2 OAM Interrupt         (1=Enable) (Read/Write)
	  Bit 4 - Mode 1 V-Blank Interrupt     (1=Enable) (Read/Write)
	  Bit 3 - Mode 0 H-Blank Interrupt     (1=Enable) (Read/Write)
	  Bit 2 - Coincidence Flag  (0:LYC<>LY, 1:LYC=LY) (Read Only)
	  Bit 1-0 - Mode Flag       (Mode 0-3, see below) (Read Only)
		    0: During H-Blank
		    1: During V-Blank
		    2: During Searching OAM-RAM
		    3: During Transfering Data to LCD Driver
	 */

	/*
	 * FF40 - LCDC - LCD Control (R/W)
		Bit 7 - LCD Display Enable             (0=Off, 1=On)
		Bit 6 - Window Tile Map Display Select (0=9800-9BFF, 1=9C00-9FFF)
		Bit 5 - Window Display Enable          (0=Off, 1=On)
		Bit 4 - BG & Window Tile Data Select   (0=8800-97FF, 1=8000-8FFF)
		Bit 3 - BG Tile Map Display Select     (0=9800-9BFF, 1=9C00-9FFF)
		Bit 2 - OBJ (Sprite) Size              (0=8x8, 1=8x16)
		Bit 1 - OBJ (Sprite) Display Enable    (0=Off, 1=On)
		Bit 0 - BG Display (for CGB see below) (0=Off, 1=On)
	 */

	public LCD() {

		createDrawspace();
	}

	private void clearScreen() {
		screen = new byte[160][144];
	}

	private void createDrawspace() {
            
            
		drawSpace = new DrawSpace();

		// create JFrames and the like for the pseudo-LCD
		JFrame frame = new JFrame();
		JPanel panel = new JPanel();
		panel.add(drawSpace);

		frame.add(panel);
		frame.setSize(800, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.setLocation(100,100);
		frame.setFocusable(true);
		frame.pack();
	}

	// 0xFF40
	private void lcdControl() {

		int val = busData[0xff40];

		if(getBit(7,val)) {
			activeScreen = true;
		} else {
			activeScreen = false;
		}

		// select tile maps
		if(getBit(6,val)) {
			tileMapStart = 0x9C00;
		} else {
			tileMapStart = 0x9800;
		}

		// window display enable
		windowDisplay = getBit(5,val);

		// set bg tile data
		if(getBit(4,val)) {
			bgTileDataStart = 0x8000;
		} else {
			bgTileDataStart = 0x8800;
		}

		// tile map display select
		if(getBit(3,val)) {
			bgTileMapStart = 0x9c00;
		} else {
			bgTileMapStart = 0x9800;
		}

		// set sprite scale
		if(getBit(2,val)) {
			spriteY = 16;
		} else {
			spriteY = 8;
		}

		// enable sprites
		enableSprites = getBit(1,val);

		// bg display
		displayBackground = getBit(0,val);

	}

	private int[] getGradients(int val) {

		val = val & 0xff;
		int[] gradients = new int[4];

		gradients[0] = (val & 0xC0) >> 6;
		gradients[1] = (val & 0x30) >> 4;
		gradients[2] = (val & 0xC) >> 2;
		gradients[3] = (val & 0x3);

		return gradients;
	}

	private void drawBackground() throws Exception {

		int xVal = 0;
		int yVal = 0;

		for(int i = bgTileDataStart ; i < (bgTileDataStart + 0x3ff) ; i++) {

			int val = Gameboy.memory.readMem(i);

			int[] gradients = getGradients(val);

			for(int grad : gradients) {
//				drawPX(xVal - scrollX, yVal - scrollY, grad);
				drawPX(xVal, yVal, grad);
			}

			xVal++;

			if(xVal >= 32) {
				xVal = 0;
				yVal++;
			}

		}

	}

	private void drawPX(int x, int y, int val) {

		if(x >= screen.length || x < 0 || y >= screen[x].length || y < 0) {
			
		    
		} else {
//			System.out.println(x+": "+y+" : "+val);
			screen[x][y] = (byte)val;
		}

	}

	private void drawScreen() throws Exception {

		clearScreen();

		if(displayBackground) {
			drawBackground();
		}

		drawSpace.setScreen(screen);

		drawSpace.repaint();

	}

	// 0xff41
	private void lcdStatus() {

		int val = busData[0xff41];

//		System.out.println("Looking at lcd status using val: 0x"+Integer.toHexString(val));

		// LYC=LY Coincidence Interrupt
		if(getBit(6,val)) {

		} else {

		}

		if(getBit(5,val)) {

		} else {

		}

		if(getBit(4,val)) {

		} else {

		}

		if(getBit(3,val)) {

		} else {

		}

		if(getBit(2,val)) {

		} else {

		}
	}

	// 0xff42
	private void scrollY() {

		int val = busData[0xff42];

		if(scrollY != val) {
			System.out.println("Setting y scroll to: "+val);
		}

		scrollY = val;

	}

	// 0xff43
	private void scrollX() {

		int val = busData[0xff43];

		if(scrollX != val) {
			System.out.println("Setting x scroll to: "+val);
		}

		scrollX = val;
	}

	// 0xff44 - r
	private void lcdcY() {

//		lcdc = lcdc % 153 + 3;
		lcdc = 0x90;
		
		busData[0xff44] = lcdc;
	}

	//0xff45 - LY Compare
	private void lyCompare() {

		int val = busData[0xff45];

	}

	// 0xff4a - Window Y position
	private void windowY() {

		if(y != busData[0xff4a]) {
			System.out.println("Window Y "+busData[0xff4a]);
		}

		y = busData[0xff4a];

	}

	// 0xff4b - Window X position
	private void windowX() {

		if(x != busData[0xff4b]) {
			System.out.println("Window X "+busData[0xff4b]);
		}

		x = busData[0xff4b];

	}

	private boolean getBit(int bit, int val) {

		return ((1 << bit) & val) != 0;
	}

	private int setBit(int bit, int val) {

		int shift = 1 << bit;

		return val | shift;
	}

	private int resetBit(int bit, int val) {

		int shift = val & ~(1 << bit);

		return shift;
	}

	// 0xff41
	private void checkInterrupts() {

		
		int modeVal = 0;

		// ly = lyc interrupt
		if(ly == lyc) {
			busData[0xff41] = setBit(6,busData[0xff41]);
			busData[0xff41] = setBit(2,busData[0xff41]);
		} else {
			busData[0xff41] = resetBit(6,busData[0xff41]);
			busData[0xff41] = resetBit(2,busData[0xff41]);
		}

		// OAM Interrupt

		// V-Blank interrupt

		// H-Blank interrupt

	}

	public int[] update(int[] bus) throws Exception {

		busData = bus;

		lcdControl();
		lcdStatus();
		scrollY();
		scrollX();
		lcdcY();
		lyCompare();
		windowY();
		windowX();

		checkInterrupts();

		drawScreen();

		return busData;
	}

	private class DrawSpace extends JPanel {

		byte[][] screen = new byte[160][144];
		private int[] black = new int[] {39,44,21};
		private int[] white = new int[] {96,109,1};

		public DrawSpace() {
			setPreferredSize(new Dimension(160*2,144*2));
		}

		public void setScreen(byte[][] screenData) {
			screen = screenData;
		}

		private Color getColor(int gradient) {

			float grad = (float) gradient;

			grad = grad / 4;

			int[] color = new int[3];

			for(int i = 0 ; i < black.length ; i++) {

				color[i] = (int) (((float)black[i] * grad) + ((float)white[i] * (1-grad)));
			}

			return new Color(color[0],color[1],color[2]);
		}

		@Override
		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D)g;

			// fill rect stuff, replace with drawing when greyscale is fixed
			// xpos, ypos, width, height - set up

			// "black" - 39, 44, 21
			// "white" - 96,109,1
			// do gradiants between the two

			for(int x = 0 ; x < screen.length ; x++) {

				for(int y = 0 ; y < screen[x].length ; y++) {

					g2d.setColor(getColor(screen[x][y]));
					g2d.fillRect(x*2, y*2, 2, 2);
				}
			}

		}


	}

}

