package clickMove;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JPanel;

public class Panel extends JPanel implements Runnable, KeyListener,
		MouseListener {

	String playName = "Quamdel";

	static int imgH = 32;

	static int width = 920;
	static int height = 600;

	// # of horizontal and vertical blocks, used for camera control.
	static int horBlocks;
	static int verBlocks;

	static int w1 = 640;
	static int h1 = 448;

	int w2 = 800;
	int h2 = 152;

	int w3 = 280;
	int h3 = 450;

	Thread thread;

	Image image1;
	static Graphics g1;

	Image image2;
	Graphics g2;

	Image image3;
	Graphics g3;

	Random rand;

	// The camera is at the top of the screen
	static boolean yTop = false;
	static boolean xTop = false;
	// Bottom
	static boolean yBot = false;
	static boolean xBot = false;

	// Shtuff
	static int[] dee;
	static int drwX = 0;
	static int drwY = 0;

	// map arraylist
	static ArrayList<int[]> mapWall;
	static ArrayList<int[]> mapGround;

	// ground arraylist.
	static ArrayList<int[]> ground;

	// Vars for gLoop Below
	public int tps = 20;
	public int milps = 1000 / tps;
	long lastTick = 0;
	int sleepTime = 0;
	long lastSec = 0;
	int ticks = 0;
	static long startTime;
	long runTime;
	private long nextTick = 0;
	private boolean running = false;

	// Vars for gLoop Above

	// Class declarations
	Player play;

	// what the player is doing
	static String playDoing;

	// int that keeps track of what to show on the right bar.
	// 1 == inv, 2 == stats
	int display;

	static int topInset = 0;

	static void setTopInset(int i) {
		topInset = i;
	}

	double relativeX;
	double relativeY;

	void getMouseLoc() {
		// Used to figure out mouse location relative to jpanel.
		double mouseX = MouseInfo.getPointerInfo().getLocation().getX();
		double frameX = ClickMove.frame.getLocation().getX();
		double mouseY = MouseInfo.getPointerInfo().getLocation().getY()
				- topInset;
		double frameY = ClickMove.frame.getLocation().getY();
		relativeX = (mouseX - frameX);
		relativeY = (mouseY - frameY);
	}

	public Panel() {
		super();

		setPreferredSize(new Dimension(width, height));
		setFocusable(true);
		requestFocus();

	}

	public void addNotify() {
		super.addNotify();
		if (thread == null) {
			thread = new Thread(this);
			thread.start();
		}
	}

	public void run() {
		image2 = new BufferedImage(w2, h2, BufferedImage.TYPE_INT_RGB);
		g2 = (Graphics2D) image2.getGraphics();
		this.setSize(new Dimension(width, height));

		image3 = new BufferedImage(w3, h3, BufferedImage.TYPE_INT_RGB);
		g3 = (Graphics2D) image3.getGraphics();
		// this.setSize(new Dimension(w3, h3));

		gStart();
	}

	/**
	 * Methods go below here.
	 * 
	 */

	public void gStart() {
		rand = new Random();
		try {
			TextInit.readMap();
		} catch (IOException e) {
		}
		mapWall = new ArrayList<int[]>();
		mapWall = TextInit.getmap1();
		mapGround = new ArrayList<int[]>();
		mapGround = TextInit.getmap2();

		findNodes();

		ImgLoad.imageInit();

		play = new Player();

		addKeyListener(this);
		addMouseListener(this);

		horBlocks = mapWall.get(1).length;
		verBlocks = mapWall.size();

		image1 = new BufferedImage(horBlocks * 32, verBlocks * 32,
				BufferedImage.TYPE_INT_RGB);
		g1 = (Graphics2D) image1.getGraphics();
		// this.setSize(new Dimension(w1, h1));

		// text box init
		textBox = new ArrayList<String>();
		textBox.add("first line");
		textBox.add("second line");
		textBox.add("third line");
		textBox.add("fourth line");
		textBox.add("fifth line");

		playDoing = null;
		display = 1;

		running = true;
		gLoop();
	}

	static int cmbI = 0;

	public void gLoop() {
		while (running) {

			if (tickClick) {
				dealWithMouse();
				tickClick = false;
			}

			getMouseLoc();

			if (sendMsg) {
				// clear the line AL,
				String werds = getStringRepresentation(chatLine);
				chatLine.removeAll(chatLine);
				textBox.add(werds);
				sendMsg = false;
			}

			nodeRegen();

			// Do the think you want the gloop to do below here
			drawMap(g1);

			play.updatePlayer();

			play.drawPlayer(g1);

			Npc.updateNpc(g1);

			MineNode.updateNpc(g1);

			if (cmbI > 0) {
				for (int i = 0; i < dee.length; i++) {
					g1.drawImage(ImgLoad.txtMc[dee[i]], drwX + (32 / 2) - 8
							+ (i * 8), drwY + (32 / 2), null);
				}
				cmbI -= 1;
			}

			cHair();

			dropDownWorks();

			botBar();

			int[] d;
			d = new int[3];
			d[0] = 0;

			// And above here.

			ticks++;
			// Runs once a second and keeps track of ticks;
			// 1000 ms since last output
			if (timer() - lastSec > 1000) {
				if (ticks < 19 || ticks > 21) {
					if (timer() - startTime < 2000) {
						System.out.println("Ticks this second: " + ticks);

						System.out.println("timer(): " + timer());
						System.out.println("nextTick: " + nextTick);
					}
				}

				ticks = 0;
				lastSec = (System.currentTimeMillis() - startTime);
			}

			// Used to protect the game from falling beind.
			if (nextTick < timer()) {
				nextTick = timer() + milps;
			}

			// Limits the ticks per second
			if (timer() - nextTick < 0) {

				sleepTime = (int) (nextTick - timer());

				if (sleepTime > 0) {
					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
					}
				}
				nextTick += milps;
			}
			drwGm();
		}
	}

	public void dealWithMouse() {
		// first check to see if they are clicking on an drop box, if so then
		// just make the click interact with the dropbox and not make the char
		// walk
		drawDrop = false;
		getMouseLoc();
		selectedZone = 0;
		if (clickInfo[0] < w1 && clickInfo[1] < h1) {
			if (clickInfo[2] == 3) {
				// tell it to draw a drop down at the location until it is told
				// not to.

				// if where trying to walk to is an enterable block acording to
				// wall then have the first drop be walk.
				// if it is a door have the second drop be open/close.
				drawDrop = true;
				int tempX = (int) clickInfo[0];
				int tempY = (int) clickInfo[1];
				if (!xTop && !xBot) {
					tempX = (int) clickInfo[0] + (int) (play.xx - (w1 / 2))
							+ 16;
				}
				if (xBot) {
					tempX = (horBlocks * 32) - w1 + clickInfo[0];
				}
				if (!yTop && !yBot) {
					tempY = (int) (clickInfo[1] + (play.yy - (h1 / 2)) + 16);
				}
				if (yBot) {
					tempY = (verBlocks * 32) - h1 + clickInfo[1];
				}

				// run through the unenterable blocks and if the clicked
				// location is one of those then dont have a movement option
				haveMove = true;
				for (int i = 0; i < PathFind.impBlocks.length; i++) {
					if (getMapVar((tempX - (tempX % 32)) / 32,
							(tempY - (tempY % 32)) / 32) == PathFind.impBlocks[i]) {
						haveMove = false;
					}
				}
				isDoor = false;
				if (getMapVar((tempX - (tempX % 32)) / 32,
						(tempY - (tempY % 32)) / 32) == 17
						|| getMapVar((tempX - (tempX % 32)) / 32,
								(tempY - (tempY % 32)) / 32) == 18
						|| getMapVar((tempX - (tempX % 32)) / 32,
								(tempY - (tempY % 32)) / 32) == 19
						|| getMapVar((tempX - (tempX % 32)) / 32,
								(tempY - (tempY % 32)) / 32) == 20
						|| getMapVar((tempX - (tempX % 32)) / 32,
								(tempY - (tempY % 32)) / 32) == 21
						|| getMapVar((tempX - (tempX % 32)) / 32,
								(tempY - (tempY % 32)) / 32) == 22
						|| getMapVar((tempX - (tempX % 32)) / 32,
								(tempY - (tempY % 32)) / 32) == 23
						|| (getMapVar((tempX - (tempX % 32)) / 32,
								(tempY - (tempY % 32)) / 32) == 24)) {
					isDoor = true;
				}
				isOre = false;
				if (getMapVar((tempX - (tempX % 32)) / 32,
						(tempY - (tempY % 32)) / 32) == 100) {
					isOre = true;
				}
				// where it draws
				dropInts = new int[] { (int) tempX, tempY };
				// tempButts[0] = new int[] { (int) relativeX, (int) relativeY,
				// 10, 10, 1, 0 };

			}
			if (clickInfo[2] == 1) {
				boolean inBox = false;
				// if it doesn't hit the drop down menu then make him walk
				int thisx = clickInfo[0];
				int thisy = clickInfo[1];
				// converts the click location to the location on the graphics
				if (!xTop && !xBot) {
					thisx = (int) (clickInfo[0] + (play.xx - (w1 / 2)) + 16);
				}
				if (xBot) {
					thisx = (horBlocks * 32) - w1 + clickInfo[0];
				}
				if (!yTop && !yBot) {
					thisy = (int) (clickInfo[1] + (play.yy - (h1 / 2)) + 16);
				}
				if (yBot) {
					thisy = (verBlocks * 32) - h1 + clickInfo[1];
				}
				System.out.println("ddH: " + dropDownHeight);
				if (thisx < dropInts[0] + dropDownHeight && thisx > dropInts[0]) {
					if (thisy < dropInts[1] + dropDownWidth
							&& thisy > dropInts[1]) {
						System.out.println("in box");
						inBox = true;
					}
				}
				if (inBox) {
					buttons(thisx, thisy);
					dropInts = new int[2];
				} else {
					play.tryWalkDraw(thisx, thisy);
				}
			}
		} else {
			buttons(clickInfo[0], clickInfo[1]);
		}

	}

	public void drawMap(Graphics g) {
		/**
		 * for (int z = 0; z < map.size(); z++) { for (int x = 0; x <
		 * map.get(z).length; x++) { for (int hg = 0; hg < ImgLoad.mapAr.length;
		 * hg++) { if (map.get(z)[x] == hg + 1) { if (hg + 1 >= 3 && hg + 1 <=
		 * 18) { g.drawImage(ImgLoad.mapAr[0], (x * imgH), (z * imgH), null); }
		 * g.drawImage(ImgLoad.mapAr[hg], (x * imgH), (z * imgH), null); } } } }
		 */
		// should find out of xTop and those before it draws.
		// only draws blocks the player can see (culling)
		int playX = (int) ((play.xx - (play.xx % 32)) / 32);
		int playY = (int) ((play.yy - (play.yy % 32)) / 32);
		int horizontal = 12;
		int startHoriz = -10;
		if (xTop) {
			// should be HeightBlocks / 2 +1 (MAYBE?)
			horizontal = 20;
		}
		if (xBot) {
			startHoriz = -19;
		}
		int vertical = 9;
		int startVerti = -7;
		if (yTop) {
			vertical = 14;
		}
		if (yBot) {
			startVerti = -13;
		}
		for (int h = startHoriz; h < horizontal; h++) {
			for (int v = startVerti; v < vertical; v++) {
				try {
					for (int vg = 0; vg < ImgLoad.groundAr.length; vg++) {
						if (mapGround.get(v + playY)[h + playX] == vg + 1) {
							g.drawImage(ImgLoad.groundAr[vg],
									((h + playX) * imgH), ((v + playY) * imgH),
									null);
						}
					}
					for (int vg = 0; vg < ImgLoad.wallAr.length; vg++) {
						if (mapWall.get(v + playY)[h + playX] == vg) {
							g.drawImage(ImgLoad.wallAr[vg],
									((h + playX) * imgH), ((v + playY) * imgH),
									null);
						}
					}
				} catch (Exception ex) {
				}
			}
		}
		// node overlay
		// draw full ore when not 0, else draw depleted node
		for (int i = 0; i < nodeArray.length; i++) {
			if (nodeArray[i][2] != 0) {
				g.drawImage(ImgLoad.wallAr[28], nodeArray[i][0] * 32,
						nodeArray[i][1] * 32, null);
			} else {
				g.drawImage(ImgLoad.wallAr[29], nodeArray[i][0] * 32,
						nodeArray[i][1] * 32, null);
			}
		}
	}

	public static int getMapVar(int x, int y) {
		// System.out.println("lay: " + lan.get(0).length);
		// System.out.println("lan: " + lan.size());
		try {
			return mapWall.get(y)[x];
		} catch (Exception ex) {
			return -1;
		}
	}

	public static void changeMap(int y, int x, int z) {
		mapWall.get(y)[x] = z;
	}

	public static void npCircle() {
		g1.drawOval((int) Npc.xx + 16 - 112, (int) Npc.yy + 16 - 112, 224, 224);
	}

	public void cHair() {
		for (int i = 0; i < 1; i++)
			if (play.tar == Npc.num) {
				g1.drawImage(ImgLoad.small[0], (int) Npc.xx, (int) Npc.yy, null);
			}
	}

	public static void drwNum(int num) {
		// Find middle of char to draw numbers centered around that
		int[] a = converter(Integer.toString(num));

		int b = (a.length * 6) / 2;
		int c = 16 - b;

		for (int i = 0; i < a.length; i++) {
			g1.drawImage(ImgLoad.txtMc[a[i]], (int) Npc.xx + c + (i * 6),
					(int) Npc.yy - 8, null);
		}
	}

	ArrayList<String> textBox;

	public void botBar() {

		/*
		 * Graphics 1 This is text box graphics
		 */

		g2.setColor(Color.CYAN);
		g2.fillRect(0, 0, w1, h1);

		// need to add scroll bar.
		while (textBox.size() > 14) {
			textBox.remove(0);
		}

		String werds = getStringRepresentation(chatLine);
		ArrayList<String> tempChatBar = new ArrayList<String>();
		tempChatBar.add(werds);

		// Draws buttons
		for (int b = 0; b < butts.length; b++) {
			if (butts[b][4] == 0) {
				g1.setColor(Color.LIGHT_GRAY);
				g1.fillRect(butts[b][0], butts[b][1], butts[b][2], butts[b][3]);
			}
			if (butts[b][4] == 2) {
				g2.setColor(Color.LIGHT_GRAY);
				g2.fillRect(butts[b][0], butts[b][1], butts[b][2], butts[b][3]);
			}
		}

		txtBox(g2, 400, 60, 1, 10, 10, textBox, true);
		txtBox(g2, 400, 24, 1, 10, 122, tempChatBar, false);

		/*
		 * Graphics 3
		 */

		if (display == 1) {

			// Inventory drawing
			g3.setColor(Color.GRAY);
			g3.fillRect(10, 10, 100, 100);
			g3.drawImage(ImgLoad.small[1], 0, 0, null);

			// draws items
			for (int i = 0; i < play.inv1.length; i++) {
				if (play.inv1[i][0] != 0) {
					int xVal = 7 + (i % 5 * 31);
					int yVal = 233 + ((((i - (i % 5))) / 5) * 31);
					g3.drawImage(ImgLoad.small[2], xVal, yVal, null);
					// Draws numbers for things.
					if (play.inv1[i][1] > 1) {
						int[] b = converter(Integer.toString(play.inv1[i][1]));
						for (int c = 0; c < b.length; c++) {
							g3.drawImage(ImgLoad.txtMc[b[c]], xVal - 2,
									yVal - 4, null);
						}
					}
				}
			}
		} else if (display == 2) {
			g3.setColor(Color.WHITE);
			g3.fillRect(0, 0, w3, h3);
			// draws levels
			// int[] gi = converter("Mining " + play.miningEp + " " +
			// play.level1 + " lv " + play.miningLvl);
			int[] gi = converter("Mining " + play.miningLvl);
			// Draws st
			for (int i = 0; i < gi.length; i++) {
				g3.drawImage(ImgLoad.txtAr[gi[i]], 16 + (i * 12), 233, null);
			}
		}

		// Draws text
		int[] gi = converter(Integer.toString(play.tar));
		// Draws st
		for (int i = 0; i < gi.length; i++) {
			g3.drawImage(ImgLoad.txtMc[gi[i]], 16 + (i * 6), 16, null);
		}

		gi = converter(Boolean.toString(play.qP));
		// Draws st
		for (int i = 0; i < gi.length; i++) {
			g3.drawImage(ImgLoad.txtMc[gi[i]], 16 + (i * 6), 24, null);
		}

		if (playDoing != null) {
			gi = converter(playDoing);
			// Draws st
			for (int i = 0; i < gi.length; i++) {
				g3.drawImage(ImgLoad.txtMc[gi[i]], 16 + (i * 6), 32, null);
			}
		} else {
			gi = converter("null");
			// Draws st
			for (int i = 0; i < gi.length; i++) {
				g3.drawImage(ImgLoad.txtMc[gi[i]], 16 + (i * 6), 32, null);
			}
		}

		gi = converter(playName);
		// Draws st
		for (int i = 0; i < gi.length; i++) {
			g3.drawImage(ImgLoad.txtMc[gi[i]], 16 + (i * 6), 8, null);
		}

	}

	static int[][] nodeArray;

	// 0 = x;
	// 1 = y;
	// 2 = curResource;
	// 3 = maxResource;
	// 4 = spawnChance;
	// 5 = drop;

	void findNodes() {
		ArrayList<int[]> tempNodes = new ArrayList<int[]>();
		// gets ran once to create
		for (int x = 0; x < mapWall.size(); x++) {
			for (int y = 0; y < mapWall.get(x).length; y++) {
				if (mapWall.get(x)[y] == 100) {
					// save location
					int nodeX = y;
					int nodeY = x;
					int curResources = 1;
					int maxResources = 1;
					// once every 5 seconds.
					int spawnChance = 1;
					// item #1 is the moneybag
					int drop = 1;
					tempNodes.add(new int[] { nodeX, nodeY, curResources,
							maxResources, spawnChance, drop });
				}
			}
		}
		nodeArray = new int[tempNodes.size()][6];
		for (int i = 0; i < nodeArray.length; i++) {
			nodeArray[i] = tempNodes.get(i);
		}

		for (int i = 0; i < nodeArray.length; i++) {
			System.out.println("node " + i + ": (" + nodeArray[i][0] + ", "
					+ nodeArray[i][1] + ", " + nodeArray[i][2] + ", "
					+ nodeArray[i][3] + ", " + nodeArray[i][4] + ", "
					+ nodeArray[i][5] + ")");
		}
	}

	void nodeRegen() {
		for (int i = 0; i < nodeArray.length; i++) {
			if (nodeArray[i][2] == 0) {
				// role dice, if below [i][4] then set cur to max.
				int randNum = rand.nextInt(100);
				if (randNum < nodeArray[i][4]) {
					nodeArray[i][2] = nodeArray[i][3];
					System.out.println("regeneration");
				}
			}
		}
	}

	// Converts ArrayList<Character> to String
	String getStringRepresentation(ArrayList<Character> list) {
		StringBuilder builder = new StringBuilder(list.size());
		for (Character ch : list) {
			builder.append(ch);
		}
		return builder.toString();
	}

	int selectedZone = 0;

	ArrayList<Character> chatLine = new ArrayList<Character>();

	public void chatInput() {
		// make a button
		// when you click that button you select a number.
		// that number corresponds to what location to put the chars you type.
		// put them into a charBar AL<AL<char>>[]
		// display these in the chat button.
	}

	public static void cmbt(int x, int y, int w, int h, int dmg, int deviation) {

		drwX = x;
		drwY = y;
		dee = converter(Integer.toString(dmg));
		for (int i = 0; i < dee.length; i++) {
			g1.drawImage(ImgLoad.txtMc[dee[i]], x + (w / 2), y + (height / 2),
					null);
		}
		cmbI = 20;
	}

	int nesRows;
	boolean ovr = false;

	public void txtBox(Graphics g, int wi, int hi, int font, int xl, int yl,
			String st) {
		int twi = 0, thi = 0;
		if (font == 0) {
			twi = 12;
			thi = 16;
		}
		if (font == 1) {
			twi = 6;
			thi = 8;
		}
		int zz = 0;
		// Draws the outline of the text box
		g.setColor(Color.CYAN);
		g.fillRect(xl, yl, wi, hi);

		int[] gh = converter(st);
		// How many letters can go in a row.
		int aa = (wi - (wi % twi)) / twi;
		// System.out.println("cols: " + aa);
		// finds how many rows there CAN be.
		int ab = (hi - (hi % thi)) / thi;
		// System.out.println("rows: " + ab);

		if (gh.length % aa != 0) {
			nesRows = ((gh.length - (gh.length % aa)) / aa) + 1;
		} else {
			nesRows = (gh.length - (gh.length % aa)) / aa;
		}

		// If it can be drawn in one line
		if (gh.length <= aa) {
			if (font == 0) {
				for (int i = 0; i < gh.length; i++) {
					// g.drawImage(ImgLoad.txtAr[gh[i]], xl + (i * twi), yl,
					// null);
				}
			} else if (font == 1) {
				for (int i = 0; i < gh.length; i++) {
					g.drawImage(ImgLoad.txtMc[gh[i]], xl + (i * twi), yl, null);
				}
			}
		} else {
			// To long for first row
			// Draws the possiable text.

			// Instead of ab it should be rows there NEEDS to be
			for (int ia = 0; ia < nesRows - 1; ia++) {
				// Makes text isnt displayed passed the bottom of the box
				if (!(ia >= ab)) {
					for (int ib = 0; ib < aa; ib++) {
						if (font == 0) {
							// g.drawImage(ImgLoad.txtAr[gh[ib + (ia * aa)]], xl
							// + (ib * twi), yl + (ia * thi), null);
						} else if (font == 1) {
							g.drawImage(ImgLoad.txtMc[gh[ib + (ia * aa)]], xl
									+ (ib * twi), yl + (ia * thi), null);
						}
					}
					zz += 1;
					ovr = false;
				} else {
					ovr = true;
				}
			}

			// Stops the box from drawing past its height
			if (!ovr) {
				// Draws LAST line
				// Number of digits in last line
				int ac = (gh.length - (zz * aa));
				// Limits the last row
				if (ac > aa) {
					ac = aa;
				}
				if (font == 0) {
					for (int i = 0; i < ac; i++) {
						// g.drawImage(ImgLoad.txtAr[gh[i + (zz * aa)]], xl
						// + (i * twi), yl + (zz * thi), null);
					}
				} else if (font == 1) {
					for (int i = 0; i < ac; i++) {
						g.drawImage(ImgLoad.txtMc[gh[i + (zz * aa)]], xl
								+ (i * twi), yl + (zz * thi), null);
					}
				}
			}
		}
	}

	// Draws each String of the ArrayList on a new line, can only have 17, which
	// is defined in botBar method
	public void txtBox(Graphics g, int wi, int hi, int font, int xl, int yl,
			ArrayList<String> st, boolean bkg) {
		if (bkg) {
			// Draws the outline of the text box
			g.setColor(Color.CYAN);
			g.fillRect(xl, yl, wi, hi);
		} else {
		}
		// line the last String was drawn on in the text box, top down.
		int lineDrawnOn = 0;
		// string array of all the words individually
		for (int stl = 0; stl < st.size(); stl++) {
			String[] words = st.get(stl).split("[ ]");

			int twi = 0, thi = 0;
			if (font == 0) {
				twi = 12;
				thi = 16;
			}
			if (font == 1) {
				twi = 6;
				thi = 8;
			}

			// How many letters can go in a row.
			int lettersPerRow = (wi - (wi % twi)) / twi;

			// first figure out how many lines there will be.
			int numLines = 0;
			// ghe is only temporary and is used to keep track of number of
			// words
			int ghe = 0;
			while (ghe < words.length) {
				ghe = repeat(ghe, 0, words, lettersPerRow);
				numLines++;
			}
			int[] figures = new int[numLines];
			for (int i = 0; i < figures.length; i++) {
				if (i == 0) {
					figures[0] = repeat(0, 0, words, lettersPerRow);
				} else {
					figures[i] = repeat(figures[i - 1], 0, words, lettersPerRow);
				}
			}
			for (int i = 0; i < figures.length; i++) {
				for (int ii = 0; ii < i; ii++) {
					figures[i] -= figures[ii];
				}
			}

			int drawPlace = xl;
			int drawnWords = 0;
			// this makes it draw all the lines
			for (int ig = 0; ig < figures.length; ig++) {
				// this draws one line
				for (int ih = 0; ih < figures[ig]; ih++) {
					// draws each word
					// System.out.println("figures[ig]: " + figures[ig]);
					int[] d = converter(words[drawnWords]);
					// System.out.println("words[drawnWords]: "+words[drawnWords]);
					for (int i = 0; i < d.length; i++) {
						// draws each letter
						g.drawImage(ImgLoad.txtMc[d[i]], drawPlace, yl
								+ (ig * thi) + (lineDrawnOn * thi), null);
						drawPlace += twi;
					}
					if (drawPlace - xl + twi < wi) {
						// draws spaces if there is room
						g.drawImage(ImgLoad.txtMc[26], drawPlace, yl
								+ (ig * thi) + (lineDrawnOn * thi), null);
						drawPlace += twi;
					}
					drawnWords++;

				}
				drawPlace = xl;
			}
			lineDrawnOn += figures.length;
		}
	}

	// returns number of words that can fit in that row.
	int repeat(int a, int b, String[] words, int lettersPerRow) {
		// cant be words[a], because this doesnt take into account the other
		// words before it.
		if (a >= words.length) {
			// System.out.println("end of the array");
			return a;
		}
		// if the next word can fit
		if (b + words[a].length() <= lettersPerRow) {
			// if there is room after the next word for a space.
			if (b + words[a].length() + 1 <= lettersPerRow) {
				b += words[a].length() + 1;
			} else {
				b += words[a].length();
			}
			a++;
			return repeat(a, b, words, lettersPerRow);
		} else {
			// it is to long to fit
			// a == number of words that can fit in that row
			return a;
			// after it returns a it should check if a is >= words.length
			// if not then add a to the first of int[]
			// then run repeat again for the next line.
		}

	}

	// / Make an array to hold all the information for the button locs.
	// [x][0] = x
	// [x][1] = y
	// [x][2] = w
	// [x][3] = h
	// [x][4] = graphics ##
	// [x][5] = what it does
	int[][] butts = { { 10, 122, 400, 24, 2, 0 } };
	int[][] tempButts = { { 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0 } };

	public void buttons(int x, int y) {
		boolean buttPressed = false;
		// have mouse click relative to graphics
		// need to get mouse loc relative to jpanel
		// subtract playX +
		System.out.println("ButtonCheck: (" + x + ", " + y + ")");
		int bx = (int) (x);
		int by = (int) (y);
		if (!xTop) {
			bx = (int) (x - (play.xx - (w1 / 2) + 16));
		}
		if (!yTop) {
			by = (int) (y - (play.yy - (h1 / 2) + 16));
		}
		for (int a = 0; a < butts.length; a++) {
			int tempButtonX = butts[a][0];
			int tempButtonY = butts[a][1];
			if (butts[a][4] == 1) {
				tempButtonX += w1;
			}
			if (butts[a][4] == 2) {
				tempButtonY += h1;
			}
			if (y > tempButtonY) {
				if (y < tempButtonY + butts[a][3]) {
					if (x > tempButtonX) {
						if (x < tempButtonX + butts[a][2]) {
							buttonP(butts[a][5]);
						}
					}
				}
			}
		}
		for (int a = 0; a < tempButts.length; a++) {
			int tempButtonX = tempButts[a][0];
			int tempButtonY = tempButts[a][1];
			if (y > tempButtonY) {
				if (y < tempButtonY + tempButts[a][3]) {
					if (x > tempButtonX) {
						if (x < tempButtonX + tempButts[a][2]) {
							buttonP(tempButts[a][5]);
						}
					}
				}
			}
		}
	}

	public void buttonP(int a) {
		System.out.println("MouseClicked: (" + a + ")");
		if (a == 0) {
			selectedZone = 1;
		}
		if (a == 10) {
			play.tryWalkDraw(dropInts[0], dropInts[1]);
		}
		if (a == 11) {
			// get bx and by of dropints
			// make sure player is adj the door before they can effect it.
			int boxX = (dropInts[0] - (dropInts[0] % 32)) / 32;
			int boxY = (dropInts[1] - (dropInts[1] % 32)) / 32;
			System.out.println("Map: " + getMapVar(boxX, boxY));
			// change the map of the dropints to the corresponding door.
			if (getMapVar(boxX, boxY) == 17) {
				changeMap(boxY, boxX, 18);
			} else if (getMapVar(boxX, boxY) == 18) {
				changeMap(boxY, boxX, 17);
			} else if (getMapVar(boxX, boxY) == 19) {
				changeMap(boxY, boxX, 20);
			} else if (getMapVar(boxX, boxY) == 20) {
				changeMap(boxY, boxX, 19);
			} else if (getMapVar(boxX, boxY) == 21) {
				changeMap(boxY, boxX, 22);
			} else if (getMapVar(boxX, boxY) == 22) {
				changeMap(boxY, boxX, 21);
			} else if (getMapVar(boxX, boxY) == 23) {
				changeMap(boxY, boxX, 24);
			} else if (getMapVar(boxX, boxY) == 24) {
				changeMap(boxY, boxX, 23);
			}
		}
		if (a == 12) {
			play.tryWalkDraw(dropInts[0], dropInts[1]);
		}
	}

	/**
	 * Methods go above here.
	 * 
	 */

	public static long timer() {
		return System.currentTimeMillis() - startTime;

	}

	int i1x = 0, i1y = 0;

	public void drwGm() {
		Graphics gspec = this.getGraphics();

		// Centers the camera around the player
		i1x = (int) ((w1 / 2) - (play.xx + 16));
		i1y = (int) ((h1 / 2) - (play.yy + 16));
		// Adjusts the cameras for when the player at the edge of a map.
		if ((play.xx + 16) < (w1 / 2)) {
			i1x = 0;
			xTop = true;
		} else {
			xTop = false;
		}
		if ((play.xx + 16) > (horBlocks * imgH) - (w1 / 2)) {
			i1x = -(((horBlocks) * imgH) - w1);
			xBot = true;
		} else {
			xBot = false;
		}
		if ((play.yy + 16) < (h1 / 2)) {
			i1y = 0;
			yTop = true;
		} else {
			yTop = false;
		}
		if ((play.yy + 16) > (verBlocks * imgH) - (h1 / 2)) {
			i1y = -(((verBlocks) * imgH) - h1);
			yBot = true;
		} else {
			yBot = false;
		}
		gspec.drawImage(image1, (i1x), i1y, null);

		gspec.dispose();

		gspec = this.getGraphics();
		gspec.drawImage(image2, 0, h1, null);
		gspec.dispose();

		gspec = this.getGraphics();
		gspec.drawImage(image3, w1, 0, null);
		gspec.dispose();
	}

	public static int[] converter(String st) {
		int a = st.length();
		int[] nw = new int[a];

		for (int b = 0; b < a; b++) {
			if (st.charAt(b) == 'a') {
				nw[b] = 0;
			} else if (st.charAt(b) == 'A') {
				nw[b] = 0;
			} else if (st.charAt(b) == 'b') {
				nw[b] = 1;
			} else if (st.charAt(b) == 'B') {
				nw[b] = 1;
			} else if (st.charAt(b) == 'c') {
				nw[b] = 2;
			} else if (st.charAt(b) == 'C') {
				nw[b] = 2;
			} else if (st.charAt(b) == 'd') {
				nw[b] = 3;
			} else if (st.charAt(b) == 'D') {
				nw[b] = 3;
			} else if (st.charAt(b) == 'e') {
				nw[b] = 4;
			} else if (st.charAt(b) == 'E') {
				nw[b] = 4;
			} else if (st.charAt(b) == 'f') {
				nw[b] = 5;
			} else if (st.charAt(b) == 'F') {
				nw[b] = 5;
			} else if (st.charAt(b) == 'g') {
				nw[b] = 6;
			} else if (st.charAt(b) == 'G') {
				nw[b] = 6;
			} else if (st.charAt(b) == 'h') {
				nw[b] = 7;
			} else if (st.charAt(b) == 'H') {
				nw[b] = 7;
			} else if (st.charAt(b) == 'i') {
				nw[b] = 8;
			} else if (st.charAt(b) == 'I') {
				nw[b] = 8;
			} else if (st.charAt(b) == 'j') {
				nw[b] = 9;
			} else if (st.charAt(b) == 'J') {
				nw[b] = 9;
			} else if (st.charAt(b) == 'k') {
				nw[b] = 10;
			} else if (st.charAt(b) == 'K') {
				nw[b] = 10;
			} else if (st.charAt(b) == 'l') {
				nw[b] = 11;
			} else if (st.charAt(b) == 'L') {
				nw[b] = 11;
			} else if (st.charAt(b) == 'm') {
				nw[b] = 12;
			} else if (st.charAt(b) == 'M') {
				nw[b] = 12;
			} else if (st.charAt(b) == 'n') {
				nw[b] = 13;
			} else if (st.charAt(b) == 'N') {
				nw[b] = 13;
			} else if (st.charAt(b) == 'o') {
				nw[b] = 14;
			} else if (st.charAt(b) == 'O') {
				nw[b] = 14;
			} else if (st.charAt(b) == 'p') {
				nw[b] = 15;
			} else if (st.charAt(b) == 'P') {
				nw[b] = 15;
			} else if (st.charAt(b) == 'q') {
				nw[b] = 16;
			} else if (st.charAt(b) == 'Q') {
				nw[b] = 16;
			} else if (st.charAt(b) == 'r') {
				nw[b] = 17;
			} else if (st.charAt(b) == 'R') {
				nw[b] = 17;
			} else if (st.charAt(b) == 's') {
				nw[b] = 18;
			} else if (st.charAt(b) == 'S') {
				nw[b] = 18;
			} else if (st.charAt(b) == 't') {
				nw[b] = 19;
			} else if (st.charAt(b) == 'T') {
				nw[b] = 19;
			} else if (st.charAt(b) == 'u') {
				nw[b] = 20;
			} else if (st.charAt(b) == 'U') {
				nw[b] = 20;
			} else if (st.charAt(b) == 'v') {
				nw[b] = 21;
			} else if (st.charAt(b) == 'V') {
				nw[b] = 21;
			} else if (st.charAt(b) == 'w') {
				nw[b] = 22;
			} else if (st.charAt(b) == 'W') {
				nw[b] = 22;
			} else if (st.charAt(b) == 'x') {
				nw[b] = 23;
			} else if (st.charAt(b) == 'X') {
				nw[b] = 23;
			} else if (st.charAt(b) == 'y') {
				nw[b] = 24;
			} else if (st.charAt(b) == 'Y') {
				nw[b] = 24;
			} else if (st.charAt(b) == 'z') {
				nw[b] = 25;
			} else if (st.charAt(b) == 'Z') {
				nw[b] = 25;
			} else if (st.charAt(b) == ' ') {
				nw[b] = 26;
			} else if (st.charAt(b) == '0') {
				nw[b] = 27;
			} else if (st.charAt(b) == '1') {
				nw[b] = 28;
			} else if (st.charAt(b) == '2') {
				nw[b] = 29;
			} else if (st.charAt(b) == '3') {
				nw[b] = 30;
			} else if (st.charAt(b) == '4') {
				nw[b] = 31;
			} else if (st.charAt(b) == '5') {
				nw[b] = 32;
			} else if (st.charAt(b) == '6') {
				nw[b] = 33;
			} else if (st.charAt(b) == '7') {
				nw[b] = 34;
			} else if (st.charAt(b) == '8') {
				nw[b] = 35;
			} else if (st.charAt(b) == '9') {
				nw[b] = 36;
			} else if (st.charAt(b) == ',') {
				nw[b] = 37;
			} else if (st.charAt(b) == '?') {
				nw[b] = 38;
			} else if (st.charAt(b) == '¿') {
				nw[b] = 39;
			} else if (st.charAt(b) == '(') {
				nw[b] = 40;
			} else if (st.charAt(b) == ')') {
				nw[b] = 41;
			}
		}
		return nw;
	}

	/**
	 * Listeners
	 */

	@Override
	public void keyPressed(KeyEvent ke) {
		if (selectedZone != 1) {
			play.keyPressed(ke);
			if (ke.getKeyCode() == KeyEvent.VK_F) {
				if (display == 1) {
					display = 2;
				} else {
					display = 1;
				}
			}
		}
	}

	boolean sendMsg = false;

	@Override
	public void keyReleased(KeyEvent ke) {
		if (selectedZone == 1) {
			if (ke.getKeyCode() == KeyEvent.VK_A) {
				chatLine.add('a');
			}
			if (ke.getKeyCode() == KeyEvent.VK_B) {
				chatLine.add('b');
			}
			if (ke.getKeyCode() == KeyEvent.VK_C) {
				chatLine.add('c');
			}
			if (ke.getKeyCode() == KeyEvent.VK_D) {
				chatLine.add('d');
			}
			if (ke.getKeyCode() == KeyEvent.VK_E) {
				chatLine.add('e');
			}
			if (ke.getKeyCode() == KeyEvent.VK_F) {
				chatLine.add('f');
			}
			if (ke.getKeyCode() == KeyEvent.VK_G) {
				chatLine.add('g');
			}
			if (ke.getKeyCode() == KeyEvent.VK_H) {
				chatLine.add('h');
			}
			if (ke.getKeyCode() == KeyEvent.VK_I) {
				chatLine.add('i');
			}
			if (ke.getKeyCode() == KeyEvent.VK_J) {
				chatLine.add('j');
			}
			if (ke.getKeyCode() == KeyEvent.VK_K) {
				chatLine.add('k');
			}
			if (ke.getKeyCode() == KeyEvent.VK_L) {
				chatLine.add('l');
			}
			if (ke.getKeyCode() == KeyEvent.VK_M) {
				chatLine.add('m');
			}
			if (ke.getKeyCode() == KeyEvent.VK_N) {
				chatLine.add('n');
			}
			if (ke.getKeyCode() == KeyEvent.VK_O) {
				chatLine.add('o');
			}
			if (ke.getKeyCode() == KeyEvent.VK_P) {
				chatLine.add('p');
			}
			if (ke.getKeyCode() == KeyEvent.VK_Q) {
				chatLine.add('q');
			}
			if (ke.getKeyCode() == KeyEvent.VK_R) {
				chatLine.add('r');
			}
			if (ke.getKeyCode() == KeyEvent.VK_S) {
				chatLine.add('s');
			}
			if (ke.getKeyCode() == KeyEvent.VK_T) {
				chatLine.add('t');
			}
			if (ke.getKeyCode() == KeyEvent.VK_U) {
				chatLine.add('u');
			}
			if (ke.getKeyCode() == KeyEvent.VK_V) {
				chatLine.add('v');
			}
			if (ke.getKeyCode() == KeyEvent.VK_W) {
				chatLine.add('w');
			}
			if (ke.getKeyCode() == KeyEvent.VK_X) {
				chatLine.add('x');
			}
			if (ke.getKeyCode() == KeyEvent.VK_Y) {
				chatLine.add('y');
			}
			if (ke.getKeyCode() == KeyEvent.VK_Z) {
				chatLine.add('z');
			}
			if (ke.getKeyCode() == KeyEvent.VK_SPACE) {
				chatLine.add(' ');
			}
			// ADD BACKSPACE
			if (ke.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
				chatLine.remove(chatLine.size() - 1);
			}
			if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
				sendMsg = true;
			}

			for (int i = 0; i < chatLine.size(); i++) {
				System.out.print(chatLine.get(i));
			}
			System.out.println();
		} else {
			play.keyReleased(ke);
			// Unimportant
			if (ke.getKeyCode() == KeyEvent.VK_P) {
				textBox.add("follower");
			}
			if (ke.getKeyCode() == KeyEvent.VK_O) {
				textBox.add("i am not a");
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent ke) {

	}

	@Override
	public void mouseClicked(MouseEvent me) {

	}

	int dropDownHeight = 0;
	int dropDownWidth = 0;

	void dropDownWorks() {
		// walk button
		// cancle button
		if (drawDrop) {
			// at drop location draw a rectangle the appropriate height and also
			// draw buttons in the rect.
			// 1 button for the first time

			// while the drop down is being drawn if the players mouse leaves
			// the drop then remove the drop

			// need to figure out how many buttons there are and make the height
			// accordingly

			// button 1 walk. Button 2 toggle like door. Make door.

			int tempButtons = 0;
			if (haveMove) {
				tempButtons++;
			}
			if (isDoor) {
				tempButtons++;
			}
			if (isOre) {
				tempButtons++;
			}
			dropDownHeight = 0;
			dropDownWidth = 31;
			if (tempButtons > 0) {
				dropDownHeight = tempButtons * 12 + 2;
			} else {
				dropDownHeight = 0;
			}
			// Draw temp butts
			g1.setColor(Color.BLUE);
			g1.fillRect(dropInts[0], dropInts[1], dropDownWidth, dropDownHeight);

			/**
			 * If the block is enterable have first drop be walk. If the wall
			 * block is door have the option to toggle it.
			 */
			int drawnButts = 0;

			tempButts = new int[tempButtons][];
			// 1st button
			if (haveMove) {
				tempButts[drawnButts] = new int[] { dropInts[0] + 2,
						dropInts[1] + 2 + (drawnButts * 12), 27, 10, 1, 10 };
				drawnButts++;
				System.out.println("moveButt");
			}
			// 2nd button
			if (isDoor) {
				tempButts[drawnButts] = new int[] { dropInts[0] + 2,
						dropInts[1] + 2 + (drawnButts * 12), 27, 10, 1, 11 };
				drawnButts++;
				System.out.println("doorButt");
			}
			// ore button.
			if (isOre) {
				System.out.println("drawnB: " + drawnButts);
				tempButts[drawnButts] = new int[] { dropInts[0] + 2,
						dropInts[1] + 2 + (drawnButts * 12), 27, 10, 1, 12 };
				System.out.println("oreButt");
				drawnButts++;
			}
			// draws temp butts.
			for (int i = 0; i < tempButts.length; i++) {
				if (tempButts[i][4] == 1) {
					g1.setColor(Color.LIGHT_GRAY);
					g1.fillRect(tempButts[i][0], tempButts[i][1],
							tempButts[i][2], tempButts[i][3]);
				}
			}
			drawnButts = 0;
			// Lables the buttons
			if (haveMove) {
				// draw
				int[] b = converter("Walk");
				for (int c = 0; c < b.length; c++) {
					g1.drawImage(ImgLoad.txtMc[b[c]],
							dropInts[0] + 3 + (c * 6), dropInts[1] + 2
									+ (drawnButts * 12), null);
				}
				drawnButts++;
			}
			if (isDoor) {
				// draw
				int[] b = converter("Door");
				for (int c = 0; c < b.length; c++) {
					g1.drawImage(ImgLoad.txtMc[b[c]],
							dropInts[0] + 3 + (c * 6), dropInts[1] + 2
									+ (drawnButts * 12), null);
				}
				drawnButts++;
			}
			if (isOre) {
				int[] b = converter("Mine");
				for (int c = 0; c < b.length; c++) {
					g1.drawImage(ImgLoad.txtMc[b[c]],
							dropInts[0] + 3 + (c * 6), dropInts[1] + 2
									+ (drawnButts * 12), null);
				}
				drawnButts++;
			}
		}
	}

	boolean drawDrop = false;
	int[] dropInts = new int[2];

	boolean haveMove = false;
	boolean isDoor = false;
	boolean isOre = false;

	boolean tickClick = false;
	int[] clickInfo;

	@Override
	public void mousePressed(MouseEvent me) {

		tickClick = true;
		clickInfo = new int[] { me.getX(), me.getY(), me.getButton() };

	}

	@Override
	public void mouseReleased(MouseEvent me) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}
}
