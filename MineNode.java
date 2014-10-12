package clickMove;

import java.awt.Graphics;

public class MineNode {
	static int num = 002;

	static int fullHp = 24;
	static int health = fullHp;

	static float xx = 288;
	static float yy = 0;

	// Its truely 32 pixels high and wide.
	static int width = 31;
	static int height = 31;

	public static void updateNpc(Graphics g) {
		drawNpc(g);
		drawHealth(g);
		// Panel.drwNum(num);
	}

	public static void drawNpc(Graphics g) {
		g.drawImage(ImgLoad.play[0], (int) xx, (int) yy, null);
	}

	static int de;

	public static void drawHealth(Graphics g) {

		if (Player.mineNum == -1 || Player.mineNum == -2) {
			health = 24;
		} else {
			health = Player.mineNum;
		}
		if (health > 0) {
			if (health >= fullHp) {
				de = 24;
			} else if (health >= fullHp * 23 / 24) {
				de = 23;
			} else if (health >= fullHp * 22 / 24) {
				de = 22;
			} else if (health >= fullHp * 21 / 24) {
				de = 21;
			} else if (health >= fullHp * 20 / 24) {
				de = 20;
			} else if (health >= fullHp * 19 / 24) {
				de = 19;
			} else if (health >= fullHp * 18 / 24) {
				de = 18;
			} else if (health >= fullHp * 17 / 24) {
				de = 17;
			} else if (health >= fullHp * 16 / 24) {
				de = 16;
			} else if (health >= fullHp * 15 / 24) {
				de = 15;
			} else if (health >= fullHp * 14 / 24) {
				de = 14;
			} else if (health >= fullHp * 13 / 24) {
				de = 13;
			} else if (health >= fullHp * 12 / 24) {
				de = 12;
			} else if (health >= fullHp * 11 / 24) {
				de = 11;
			} else if (health >= fullHp * 10 / 24) {
				de = 10;
			} else if (health >= fullHp * 9 / 24) {
				de = 9;
			} else if (health >= fullHp * 8 / 24) {
				de = 8;
			} else if (health >= fullHp * 7 / 24) {
				de = 7;
			} else if (health >= fullHp * 6 / 24) {
				de = 6;
			} else if (health >= fullHp * 5 / 24) {
				de = 5;
			} else if (health >= fullHp * 4 / 24) {
				de = 4;
			} else if (health >= fullHp * 3 / 24) {
				de = 3;
			} else if (health >= fullHp * 2 / 24) {
				de = 2;
			} else {
				de = 1;
			}
			g.drawImage(ImgLoad.health[de], (int) xx, (int) yy + 32, null);

			// de =playerHp], xx, yy + 32, null);
		} else {
			de = 0;
		}

	}

	public static boolean clickCheck(int x, int y) {
		if (x >= xx && x <= xx + width) {
			if (y >= yy && y <= yy + height) {
				return true;
			}
		}
		return false;
	}

	static float dx = 0;
	static float dy = 0;

	public static int overlap(float x, float y, int w, int h, double s, int d) {
		// For the four directions.
		if (d == 0) {
			// Checks to if it will collide on the axis it's not moving on.
			if (x > xx + width || x + w < xx) {
				// wont clash
				return 1;
			} else {
				dy = y - (yy + height + 1);
				// Does the mover have enough space to move its speed.
				if (y - s > yy + height) {
					// (y - s) North face of where the mover is about to be.
					// (yy + 32) South face of the non-mover.
					return 1;
				}
				// Is the movers completely past the non-mover.
				if (y + height < yy) {
					// (y + 31) South face of mover.
					// (yy) North face of non-mover.
					return 1;
				}
			}
		}
		if (d == 1) {
			if (y > yy + height || y + h < yy) {
				return 1;
			} else {
				dx = (x + width + 1) - xx;
				if (x + width + s < xx) {
					return 1;
				}
				if (x > xx + width) {
					return 1;
				}
			}
		}
		if (d == 2) {
			if (x > xx + width || x + w < xx) {
				return 1;
			} else {
				dy = (y + height + 1) - yy;
				if (y + height + s < yy) {
					return 1;
				}
				if (y > yy + height) {
					return 1;
				}
			}
		}
		if (d == 3) {
			if (y > yy + height || y + h < yy) {
				return 1;
			} else {
				dx = x - (xx + width + 1);
				if (x - s > xx + width) {
					return 1;
				}
				if (x < xx - width) {
					return 1;
				}
			}
		}
		return 0;
	}

	// DIAGGAGADIC
	public static int cornDet(float x, float y, int w, int h, double s, int d) {
		if (d == 4) {
			x -= s;
			if (x > xx + width || x + w < xx) {
				return 1;
			} else {
				dy = y - (yy + height + 1);
				if (y - s > yy + height) {
					return 1;
				}
				if (y + height < yy) {
					return 1;
				}
			}
			// DIFFERENCIAL
			y -= s;
			if (y > yy + height || y + h < yy) {
				return 1;
			} else {
				dx = x - (xx + width + 1);
				if (x - s > xx + width) {
					return 1;
				}
				if (x < xx - width) {
					return 1;
				}
			}
		}
		if (d == 5) {
			// NE
			// This is north
			x += s;
			if (x > xx + width || x + w < xx) {
				return 1;
			} else {
				dy = y - (yy + height + 1);
				if (y - s > yy + height) {
					return 1;
				}
				if (y + height < yy) {
					return 1;
				}
			}
			// This is east
			y -= s;
			if (y > yy + height || y + h < yy) {
				return 1;
			} else {
				dx = (x + width + 1) - xx;
				if (x + width + s < xx) {
					return 1;
				}
				if (x > xx + width) {
					return 1;
				}
			}
		}
		if (d == 6) {
			// South east
			// South
			x += s;
			if (x > xx + width || x + w < xx) {
				return 1;
			} else {
				dy = (y + height + 1) - yy;
				if (y + height + s < yy) {
					return 1;
				}
				if (y > yy + height) {
					return 1;
				}
			}
			// East
			y += s;
			if (y > yy + height || y + h < yy) {
				return 1;
			} else {
				dx = (x + width + 1) - xx;
				if (x + width + s < xx) {
					return 1;
				}
				if (x > xx + width) {
					return 1;
				}
			}
		}
		if (d == 7) {
			// SW
			// South
			x -= s;
			if (x > xx + width || x + w < xx) {
				return 1;
			} else {
				dy = (y + height + 1) - yy;
				if (y + height + s < yy) {
					return 1;
				}
				if (y > yy + height) {
					return 1;
				}
			}
			// West
			y += s;
			if (y > yy + height || y + h < yy) {
				return 1;
			} else {
				dx = x - (xx + width + 1);
				if (x - s > xx + width) {
					return 1;
				}
				if (x < xx - width) {
					return 1;
				}
			}
		}
		return 0;
	}

	// Distance

	public static float distance(float x, float y) {
		float a = Math.abs(x - (xx + 16));
		float b = Math.abs(y - (yy + 16));
		float c = (float) Math.hypot(a, b);

		return c;
	}

	public static int dir(float x, float y) {
		float xm = xx + 16;
		float ym = yy + 16;
		if (xm < x) {
			if (ym < y) {
				if (Math.abs(x - xm) / Math.abs(y - ym) > .5) {
					if (Math.abs(x - xm) / Math.abs(y - ym) < 2) {
						return 6; // SOUTH EAST
					} else {
						return 1; // EAST
					}
				} else {
					return 2; // SOUTH
				}
			} else {
				if (Math.abs(x - xm) / Math.abs(y - ym) > .5) {
					if (Math.abs(x - xm) / Math.abs(y - ym) < 2) {
						return 5; // NORTH EAST
					} else {
						return 1; // EAST
					}
				} else {
					return 0; // North
				}
			}
		} else {
			if (yy < y) {
				if (Math.abs(x - xm) / Math.abs(y - ym) > .5) {
					if (Math.abs(x - xm) / Math.abs(y - ym) < 2) {
						return 7; // SOUTH WEST
					} else {
						return 3; // WEST
					}
				} else {
					return 2; // SOUTH
				}
			} else {
				if (Math.abs(x - xm) / Math.abs(y - ym) > .5) {
					if (Math.abs(x - xm) / Math.abs(y - ym) < 2) {
						return 4; // NORTH WEST
					} else {
						return 3; // WEST
					}
				} else {
					return 0;
				}
			}
		}
	}

	// Dmg text

	public static void cmbt(int x, int y, int w, int h, int dmg, int deviation) {
		Panel.cmbt(x, y, w, h, dmg, deviation);
	}

	// Converter
	public static int[] converter(String st) {
		int a = st.length();
		int[] nw = new int[a];
		for (int b = 0; b < a; b++) {
			if (st.charAt(b) == 'a') {
				nw[b] = 0;
			} else if (st.charAt(b) == 'b') {
				nw[b] = 1;
			} else if (st.charAt(b) == 'c') {
				nw[b] = 2;
			} else if (st.charAt(b) == 'd') {
				nw[b] = 3;
			} else if (st.charAt(b) == 'e') {
				nw[b] = 4;
			} else if (st.charAt(b) == 'f') {
				nw[b] = 5;
			} else if (st.charAt(b) == 'g') {
				nw[b] = 6;
			} else if (st.charAt(b) == 'h') {
				nw[b] = 7;
			} else if (st.charAt(b) == 'i') {
				nw[b] = 8;
			} else if (st.charAt(b) == 'j') {
				nw[b] = 9;
			} else if (st.charAt(b) == 'k') {
				nw[b] = 10;
			} else if (st.charAt(b) == 'l') {
				nw[b] = 11;
			} else if (st.charAt(b) == 'm') {
				nw[b] = 12;
			} else if (st.charAt(b) == 'n') {
				nw[b] = 13;
			} else if (st.charAt(b) == 'o') {
				nw[b] = 14;
			} else if (st.charAt(b) == 'p') {
				nw[b] = 15;
			} else if (st.charAt(b) == 'q') {
				nw[b] = 16;
			} else if (st.charAt(b) == 'r') {
				nw[b] = 17;
			} else if (st.charAt(b) == 's') {
				nw[b] = 18;
			} else if (st.charAt(b) == 't') {
				nw[b] = 19;
			} else if (st.charAt(b) == 'u') {
				nw[b] = 20;
			} else if (st.charAt(b) == 'v') {
				nw[b] = 21;
			} else if (st.charAt(b) == 'w') {
				nw[b] = 22;
			} else if (st.charAt(b) == 'x') {
				nw[b] = 23;
			} else if (st.charAt(b) == 'y') {
				nw[b] = 24;
			} else if (st.charAt(b) == 'z') {
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
			} else if (st.charAt(b) == '.') {
				nw[b] = 37;
			}
		}
		return nw;
	}

	// Getters & Setters
	public static float getdx() {
		return dx;
	}

	public static float getdy() {
		return dy;
	}

	public static int getnum() {
		return num;
	}
}
