package clickMove;

import java.util.ArrayList;

public class PathFindOld {

	// Need to make exceptions for when the player is going northwest and he
	// can walk north because that is the door, and the wall allows him to walk
	// east but then not north because there is a wall.

	// Player loc
	int px = 0;
	int py = 0;

	int startX = 0;
	int startY = 0;

	// Target loc
	int tx = 12;
	int ty = 9;

	int id;
	ArrayList<int[]> open;
	ArrayList<int[]> closed;
	ArrayList<int[]> temp;

	Player play;

	// Blocks that cant be enterd from (with the character going in) the
	// direction.
	int[] eN = { -1, 5, 9, 10, 11, 12, 13, 14 };
	int[] eE = { -1, 6, 8, 10, 11, 12, 13, 14 };
	int[] eS = { -1, 3, 7, 8, 11, 12, 13, 14 };
	int[] eW = { -1, 4, 7, 9, 11, 12, 13, 14 };

	int[] xN = { 3, 7, 8 };
	int[] xE = { 4, 7, 9 };
	int[] xS = { 5, 9, 10 };
	int[] xW = { 6, 8, 10 };

	int[] eNW = { 4, 5, 7, 9, 10, 11, 12, 13, 14 };
	int[] eNE = { 5, 6, 8, 9, 10, 11, 12, 13, 14 };
	int[] eSE = { 3, 6, 7, 8, 10, 11, 12, 13, 14 };
	int[] eSW = { 3, 4, 7, 8, 9, 11, 12, 13, 14 };

	int[] xNW = { 3, 6, 7, 8, 10, 11, 12, 13, 14, 18 };
	int[] xNE = { 3, 4, 7, 8, 9, 11, 12, 13, 14, 15 };
	int[] xSE = { 4, 5, 7, 9, 10, 11, 12, 13, 14, 16 };
	int[] xSW = { 5, 6, 8, 9, 10, 11, 12, 13, 14, 17 };

	int[] impBlocks = { 20, 23 };

	public PathFindOld(int tx, int ty, int id, int px, int py, Player play) {
		// System.out.println("called");
		this.tx = tx;
		this.ty = ty;
		this.id = id;
		this.px = px;
		this.py = py;
		this.play = play;
		startX = tx;
		startY = ty;

		setImps();

		closed = new ArrayList<int[]>();
		open = new ArrayList<int[]>();
		temp = new ArrayList<int[]>();
		closed.add(new int[] { px, py, -1, 0, -1, -1 });

		beginSearch();
	}

	void setImps() {
		int[] temp = null;
		for (int i = 0; i < impBlocks.length; i++) {
			temp = new int[(eN.length + impBlocks.length)];
			for (int ig = 0; ig < eN.length; ig++) {
				temp[ig] = eN[ig];
				for (int ih = 0; ih < impBlocks.length; ih++) {
					temp[ih + eN.length] = impBlocks[ih];
				}
			}
		}
		eN = new int[eN.length + impBlocks.length];
		eN = temp;

		for (int i = 0; i < impBlocks.length; i++) {
			temp = new int[(eE.length + impBlocks.length)];
			for (int ig = 0; ig < eE.length; ig++) {
				temp[ig] = eE[ig];
				for (int ih = 0; ih < impBlocks.length; ih++) {
					temp[ih + eE.length] = impBlocks[ih];
				}
			}
		}
		eE = new int[eE.length + impBlocks.length];
		eE = temp;

		for (int i = 0; i < impBlocks.length; i++) {
			temp = new int[(eS.length + impBlocks.length)];
			for (int ig = 0; ig < eS.length; ig++) {
				temp[ig] = eS[ig];
				for (int ih = 0; ih < impBlocks.length; ih++) {
					temp[ih + eS.length] = impBlocks[ih];
				}
			}
		}
		eS = new int[eS.length + impBlocks.length];
		eS = temp;

		for (int i = 0; i < impBlocks.length; i++) {
			temp = new int[(eW.length + impBlocks.length)];
			for (int ig = 0; ig < eW.length; ig++) {
				temp[ig] = eW[ig];
				for (int ih = 0; ih < impBlocks.length; ih++) {
					temp[ih + eW.length] = impBlocks[ih];
				}
			}
		}
		eW = new int[eW.length + impBlocks.length];
		eW = temp;

		for (int i = 0; i < impBlocks.length; i++) {
			temp = new int[(xNW.length + impBlocks.length)];
			for (int ig = 0; ig < xNW.length; ig++) {
				temp[ig] = xNW[ig];
				for (int ih = 0; ih < impBlocks.length; ih++) {
					temp[ih + xNW.length] = impBlocks[ih];
				}
			}
		}
		eNW = new int[eNW.length + impBlocks.length];
		eNW = temp;

		for (int i = 0; i < impBlocks.length; i++) {
			temp = new int[(eNE.length + impBlocks.length)];
			for (int ig = 0; ig < eNE.length; ig++) {
				temp[ig] = eNE[ig];
				for (int ih = 0; ih < impBlocks.length; ih++) {
					temp[ih + eNE.length] = impBlocks[ih];
				}
			}
		}
		eNE = new int[eNE.length + impBlocks.length];
		eNE = temp;

		for (int i = 0; i < impBlocks.length; i++) {
			temp = new int[(eSE.length + impBlocks.length)];
			for (int ig = 0; ig < eSE.length; ig++) {
				temp[ig] = eSE[ig];
				for (int ih = 0; ih < impBlocks.length; ih++) {
					temp[ih + eSE.length] = impBlocks[ih];
				}
			}
		}
		eSE = new int[eSE.length + impBlocks.length];
		eSE = temp;

		for (int i = 0; i < impBlocks.length; i++) {
			temp = new int[(eSW.length + impBlocks.length)];
			for (int ig = 0; ig < eSW.length; ig++) {
				temp[ig] = eSW[ig];
				for (int ih = 0; ih < impBlocks.length; ih++) {
					temp[ih + eSW.length] = impBlocks[ih];
				}
			}
		}
		eSW = new int[eSW.length + impBlocks.length];
		eSW = temp;
	}

	void beginSearch() {
		getAdj(px, py, 0);
		while (!pathDone) {
			findLow();
			// System.out.println("frem");
		}
	}

	public void findLow() {
		// if there is no way to get there it just ends
		if (open.size() == 0) {
			pathDone = true;
		}
		int g = 0;
		// System.out.println("openSize: " + open.size());
		for (int i = 1; i < open.size(); i++) {
			// i = 1 because it automatically assumes that 0 is the lowest 2+ 3.
			// g is lowest of temp.
			if (open.get(i)[2] + open.get(i)[3] < open.get(g)[2]
					+ open.get(g)[3]) {
				g = i;
			}
		}
		// System.out.println("c: " + closed.size());
		// When this crashes it is normaly because it searched 1000 places and
		// didnt find its way to the block.
		// g is lowest block
		// System.out.println("Open Size: " + open.size());
		try {
			// System.out.println("deed");
			// System.out.println("open: " + open.size() + "\tg: " + g);
			addClosed(open.get(g));
			// System.out.println("step 1");
			open.remove(open.get(g));
			// System.out.println("stemp 2");
			getAdj(closed.get(closed.size() - 1)[0],
					closed.get(closed.size() - 1)[1],
					closed.get(closed.size() - 1)[3]);
			// System.out.println("cont");
		} catch (Exception ex) {
		}
	}

	// x, y, h, g, parentx, parenty
	public void getAdj(int x, int y, int f) {
		// Temp int array
		int[] d = { x, y, getMd(x, y), f, -1, -1 };

		// That "cardinal?" direction is available.
		boolean Na = false;
		boolean Ea = false;
		boolean Sa = false;
		boolean Wa = false;

		// f should be taken from parent

		// add all to temp
		int xx;
		int yy;
		// This allows the player to only walk into block 7 at certin
		// directions. now i need it to only walk out of block 7 at certin
		// directions.

		// north
		xx = d[0];
		yy = d[1] - 1;
		if (comeOut(0, Panel.getMapVar(x, y))) {
			if (compare(0, Panel.getMapVar(xx, yy))) {
				Na = true;
				temp.add(new int[] { xx, yy, getMd(xx, yy), d[3] + 9, x, y });
			}
		}
		// east
		xx = d[0] + 1;
		yy = d[1];
		if (comeOut(1, Panel.getMapVar(x, y))) {
			if (compare(1, Panel.getMapVar(xx, yy))) {
				Ea = true;
				temp.add(new int[] { xx, yy, getMd(xx, yy), d[3] + 9, x, y });
			}
		}
		// south
		xx = d[0];
		yy = d[1] + 1;
		if (comeOut(2, Panel.getMapVar(x, y))) {
			if (compare(2, Panel.getMapVar(xx, yy))) {
				Sa = true;
				temp.add(new int[] { xx, yy, getMd(xx, yy), d[3] + 9, x, y });
			}
		}
		// west
		xx = d[0] - 1;
		yy = d[1];
		if (comeOut(3, Panel.getMapVar(x, y))) {
			if (compare(3, Panel.getMapVar(xx, yy))) {
				Wa = true;
				temp.add(new int[] { xx, yy, getMd(xx, yy), d[3] + 9, x, y });
			}
		}
		// play moving nw
		// if w or n is imp then dont add.
		if (Wa && Na) {

			xx = d[0] - 1;
			yy = d[1] - 1;
			if (comeOut(4, Panel.getMapVar(x, y))) {
				if (compare(4, Panel.getMapVar(xx, yy))) {
					temp.add(new int[] { xx, yy, getMd(xx, yy), d[3] + 14, x, y });

				}
			}
		}
		// ne
		if (Na && Ea) {
			xx = d[0] + 1;
			yy = d[1] - 1;
			if (comeOut(5, Panel.getMapVar(x, y))) {
				if (compare(5, Panel.getMapVar(xx, yy))) {
					temp.add(new int[] { xx, yy, getMd(xx, yy), d[3] + 14, x, y });
				}
			}
		}
		// sw
		if (Sa && Wa) {
			xx = d[0] - 1;
			yy = d[1] + 1;
			if (comeOut(7, Panel.getMapVar(x, y))) {
				if (compare(7, Panel.getMapVar(xx, yy))) {
					temp.add(new int[] { xx, yy, getMd(xx, yy), d[3] + 14, x, y });
				}
			}
		}
		// se
		if (Sa && Ea) {
			xx = d[0] + 1;
			yy = d[1] + 1;
			if (comeOut(6, Panel.getMapVar(x, y))) {
				if (compare(6, Panel.getMapVar(xx, yy))) {
					temp.add(new int[] { xx, yy, getMd(xx, yy), d[3] + 14, x, y });
				}
			}
		}
		for (int i = 0; i < temp.size(); i++) {
			addOpen(temp.get(i));
		}
		temp.clear();
	}

	boolean compare(int dir, int get) {
		if (dir == 0) {
			// System.out.println("compare");
			for (int i = 0; i < eN.length; i++) {
				if (get == eN[i]) {
					return false;

				}
			}
		} else if (dir == 1) {
			for (int i = 0; i < eE.length; i++) {
				if (get == eE[i]) {
					return false;
				}
			}
		} else if (dir == 2) {
			for (int i = 0; i < eS.length; i++) {
				if (get == eS[i]) {
					return false;
				}
			}
		} else if (dir == 3) {
			for (int i = 0; i < eW.length; i++) {
				if (get == eW[i]) {
					return false;
				}
			}
		} else if (dir == 4) {
			for (int i = 0; i < eNW.length; i++) {
				if (get == eNW[i]) {
					return false;
				}
			}
		} else if (dir == 5) {
			for (int i = 0; i < eNE.length; i++) {
				if (get == eNE[i]) {
					return false;
				}
			}
		} else if (dir == 6) {
			for (int i = 0; i < eSW.length; i++) {
				if (get == eSW[i]) {
					return false;
				}
			}
		} else if (dir == 7) {
			for (int i = 0; i < eSW.length; i++) {
				if (get == eSW[i]) {
					return false;
				}
			}
		}
		return true;
	}

	boolean comeOut(int dir, int get) {
		if (dir == 0) {
			for (int i = 0; i < xN.length; i++) {
				if (get == xN[i]) {
					return false;
				}
			}
		} else if (dir == 1) {
			for (int i = 0; i < xE.length; i++) {
				if (get == xE[i]) {
					return false;
				}
			}
		} else if (dir == 2) {
			for (int i = 0; i < xS.length; i++) {
				if (get == xS[i]) {
					return false;
				}
			}
		} else if (dir == 3) {
			for (int i = 0; i < xW.length; i++) {
				if (get == xW[i]) {
					return false;
				}
			}
		} else if (dir == 4) {
			for (int i = 0; i < xNW.length; i++) {
				if (get == xNW[i]) {
					return false;
				}
			}
		} else if (dir == 5) {
			for (int i = 0; i < xNE.length; i++) {
				if (get == xNE[i]) {
					return false;
				}
			}
		} else if (dir == 6) {
			for (int i = 0; i < xSE.length; i++) {
				if (get == xSE[i]) {
					return false;
				}
			}
		} else if (dir == 7) {
			for (int i = 0; i < xSW.length; i++) {
				if (get == xSW[i]) {
					return false;
				}
			}
		}
		return true;
	}

	// make a method to check for overlap on open list.
	// make an adding method for closed like i did for open

	void addOpen(int[] d) {
		// Check open (and closed) list to see if the x, y value being added is
		// already on
		int a = 0;
		for (int i = 0; i < open.size(); i++) {
			if (d[0] == open.get(i)[0]) {
				if (d[1] == open.get(i)[1]) {
					// if what your adding's g is lower than what is there. then
					// change parent.
					if (d[3] < open.get(i)[3]) {
						open.set(i, d);
					}
					a++;
				}
			}
		}
		// If no collisions have happened so far.
		if (a == 0) {
			// Checks closed list now
			for (int p = 0; p < closed.size(); p++) {
				if (d[0] == closed.get(p)[0]) {
					if (d[1] == closed.get(p)[1]) {
						a++;
					}
				}
			}
		}
		if (a == 0) {
			open.add(d);
		}
	}

	ArrayList<int[]> path;

	public void addClosed(int[] d) {
		int a = 0;
		for (int p = 0; p < closed.size(); p++) {
			if (d[0] == closed.get(p)[0]) {
				if (d[1] == closed.get(p)[1]) {
					a++;
				}

			}
		}
		if (a == 0) {
			// sees if what is being added to closed is the target.
			closed.add(d);
			if (d[0] == tx) {
				if (d[1] == ty) {
					// If so it draws path and starts moving.
					// find the last added to closed list, should be on target
					path = new ArrayList<int[]>();
					path.add(closed.get(closed.size() - 1));
					while (!pathDone) {
						doStuff();
					}
				}
			}
		}
	}

	boolean pathDone = false;

	void doStuff() {
		int d = -1;
		// Searches all of closed for the parent of the node most recently
		// added to path.
		for (int i = 0; i < closed.size(); i++) {
			if (path.get(path.size() - 1)[4] == closed.get(i)[0]) {
				if (path.get(path.size() - 1)[5] == closed.get(i)[1]) {
					d = i;
				}
			}
		}
		if (d != -1) {
			path.add(closed.get(d));
		} else {
			// Cant find parent.
			// sice player loc's parent is -1, -1 (unacessable location) it
			// means that the path has reached the players block
			pathDone = true;
		}
	}

	int getMd(int x, int y) {
		return (Math.abs(x - tx) * 10) + (Math.abs(y - ty) * 10);
	}

	boolean pathComplete = false;

	// used for moving player through path.
	int ga = -1;
	int gi = 0;

	boolean runOnce = true;

	void giInit() {
		if (runOnce)
			gi = path.size() - 2;
		runOnce = false;
	}

	int dx;
	int dy;

	int dirX;
	int dirY;

	int cx;
	int cy;

	// Checks to see if players location is the last square of the path
	void conCheck() {
		// constant check that gets ran every tick.
		// when this happens, once every 20 ticks (once a second) the player
		// moves through the path until it hits the target at which it deletes
		// the ArrayList
		if (!pathComplete) {
			// use this to stop the loop from running once it doesnt needto.
			if (path != null) {
				// System.out.println("not null");
				for (int i = 0; i < path.size(); i++) {
					// System.out.println("pathSize: "+path.size());
					// if players spot is in path
					if (path.get(i)[0] == px && path.get(i)[1] == py) {
						// now what, path al has everything the player needs to
						// move to. Start moving player every second
						pathComplete = true;
					}
				}
			}
		} else {
			// System.out.println("ga: " + ga);
			// if pathComplete == true;
			giInit();
			// Make smooth moving transitions.
			// Needs to read the next plot and find the direction to it.
			if (ga == -1) {
				try {
					cx = px - path.get(gi)[0];
					cy = py - path.get(gi)[1];
				} catch (Exception ex) {
					// Throws an error when at the end of its path because
					// gi = -1;
					cx = 0;
					cy = 0;
				}
				dx = 0;
				dy = 0;
				if (cy == 0 || cx == 0) {
					// if he is not walking diagonally.
					ga = 4;
				} else {
					ga = 6;
				}
			} else if (ga == 0) {
				if (gi >= 0) {
					px = path.get(gi)[0];
					py = path.get(gi)[1];
					dx = 0;
					dy = 0;
					gi -= 1;
					// ga = 0;
					play.xx = px * 32;
					play.yy = py * 32;

					// Delta to tar.
					// neg if going east or south
					try {
						cx = px - path.get(gi)[0];
						cy = py - path.get(gi)[1];
					} catch (Exception ex) {
						// Throws an error when at the end of its path because
						// gi = -1;
						cx = 0;
						cy = 0;
					}
					// Finds the direction of the next block.
					// 0 for no chance, 1 for positive chance, 2 for negative
					// change.
					if (cy == 0 || cx == 0) {
						// if he is not walking diagonally.
						ga = 4;
					} else {
						ga = 6;
					}
				}
			}
			if (ga > 0) {
				// System.out.println("ga is > zero");
				// System.out.println("cx: " + cx);
				// System.out.println("cy: " + cy);
				ga -= 1;

				if (cx == 0) {
					if (cy < 0) {
						dy += 8;
						// System.out.println("1");
					}
					if (cy > 0) {
						dy -= 8;
						// System.out.println("2");
					}
				} else if (cy == 0) {
					if (cx < 0) {
						dx += 8;
						// System.out.println("3");
					}
					if (cx > 0) {
						dx -= 8;
						// System.out.println("4");
					}
				}

				else if (cy < 0) {
					dy += 6;
					if (cx < 0) {
						// SE
						dx += 6;
						// System.out.println("5");
					} else {
						// sw
						dx -= 6;
						// System.out.println("6");
					}
				} else if (cy > 0) {
					dy -= 6;
					if (cx < 0) {
						// NE
						dx += 6;
						// System.out.println("7");
					} else {
						// Nw
						dx -= 6;
						// System.out.println("8");
					}
				}

				play.xx = (px * 32) + dx;
				play.yy = (py * 32) + dy;
				// System.out.println("Loc: (" + ((py * 32) + dy) + ", "
				// + ((px * 32) + dx) + ")");
				/*
				 * if (dirX == 0) { if (dirY > 0) { dy += 8; } if (dirY < 0) {
				 * dy+=8; } }
				 */
				/**
				 * // if the difference from play to tar is only on one axis
				 * then // add 8 each time. int x = path.get(gi)[0]; int y =
				 * path.get(gi)[1]; int hx = px - x; int hy = py - y; if (hx ==
				 * 0) { if (hy < 0) { dirX = 0; dy += 8; } if (hy > 0) { dy -=
				 * 8; } } if (hy == 0) { if (hx < 0) { dx += 8; } if (hx > 0) {
				 * dx -= 8; } } play.xx = (px * 32) + dx; play.yy = (py * 32) +
				 * dy; ga++;
				 */
			}
			if (px == tx && py == ty) {
				play.deletePathFinding(id);
			}
		}
	}

	/**
	 * Optional labeling methods
	 */

	void drawPath() {
		try {
			for (int i = 0; i < path.size(); i++) {
				Panel.g1.drawImage(ImgLoad.small[0], path.get(i)[0] * 32,
						path.get(i)[1] * 32, null);
			}
		} catch (Exception ex) {

		}
	}

	// Draw player wont be necessary but i would still like do draw an
	// indication to where the player is going.

	public void printOut() {
		for (int i = 0; i < closed.size(); i++) {
			System.out.println("Closed " + i + " (" + closed.get(i)[0] + ", "
					+ closed.get(i)[1] + ")");
		}
	}

	// lable boxes
	void lableBoxes() {
		for (int i = 0; i < closed.size(); i++) {
			int[] b = Panel.converter(Integer.toString(closed.get(i)[3]));
			for (int c = 0; c < b.length; c++) {
				Panel.g1.drawImage(ImgLoad.txtMc[b[c]], (closed.get(i)[0] * 32)
						+ 7 + (c * 6), (closed.get(i)[1] * 32) + 16, null);
			}
		}
		// Draws for open list
		for (int i = 0; i < open.size(); i++) {
			int[] b = Panel.converter(Integer.toString(open.get(i)[3]));
			for (int c = 0; c < b.length; c++) {
				Panel.g1.drawImage(ImgLoad.txtMc[b[c]], (open.get(i)[0] * 32)
						+ 7 + (c * 6), (open.get(i)[1] * 32) + 16, null);
			}
		}
	}

}
