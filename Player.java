package clickMove;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Random;

public class Player {
	int tar;

	// # of ticks per 1 attack.
	int atkSpd = 20;

	int atk = 0;

	int[] loc;
	float xx = 0;
	float yy = 0;

	// Used for drawing the circle for attacking
	long qpTim = 0;

	Image curPlay = ImgLoad.play[0];

	// Blocks the player can not walk through
	int[] impBs = { -1, 0, 3, 5, 6 };

	// Players invintory
	// inv[1] = 2, you have 2 of the item one.
	int[] inv = { 0, 0 };

	// mining stat
	int statMine = 0;

	// Keys booleans
	boolean qP = false;
	boolean wP = false;
	boolean eP = false;
	boolean dP = false;
	boolean sP = false;
	boolean aP = false;
	boolean shiftP = false;
	boolean spaceP = false;

	boolean attacking;

	// Counts down for mining.
	static int mineNum = -1;
	static boolean mining;

	int miningEp = 0;
	int miningLvl = 1;

	int level1 = 100;

	public Player() {
		invInit();
		itemInit();
		// used for path finding
		paths = new ArrayList<PathFind>();
		temppaths = new ArrayList<PathFind>();
	}

	int thisOreId = -1;

	public void updatePlayer() {
		// moves for pathfinding
		if (paths.size() != 0) {
			// necessary?
			if (paths.size() > 1) {
				paths.remove(0);
			}
			for (int i = 0; i < paths.size(); i++) {
				paths.get(i).drawPath();
				paths.get(i).conCheck();
			}
			// stops mining while moving
			mining = false;
		}
		/**
		 * This is the shit for mining
		 */
		if (mining) {
			Panel.playDoing = "Mining";
			// find the id of the node play is trying to mine.
			if (!wP && !aP && !sP && !dP) {
				// if the node has a resource then try to mine.
				if (Panel.nodeArray[thisOreId][2] > 0) {
					// stops mining while moving, but does this in another place
					// if (paths.size() == 0) {
					// System.out.println("pass");
					// System.out.println("thisOreId: " + thisOreId);
					// deals with chance of getting the ore
					Random rand = new Random();
					int get = rand.nextInt(200);
					if (get > 200 - 4 - miningLvl) {
						addInv(1, 1);
						Panel.nodeArray[thisOreId][2] -= 1;
						miningEp += 5;
						if (miningLvl == 1) {
							System.out.println("EP: " + miningEp + " lvl: "
									+ level1);
							if (miningEp >= level1) {
								miningLvl += 1;
								System.out.println("add");
							}
						}
					}
				} else {
					mining = false;
				}
				// }
			}
		} else {
			Panel.playDoing = null;
		}
		if (mineNum > -1) {
			// If player moves it cancels the channel
			if (!wP && !aP && !sP && !dP) {
				if (mineNum == 0) {
					// give item
					System.out.println("GIVE ITEM");
					// inv[0] += 1;
					statMine += 1;
					addInv(1, 1);
				}
			} else {
				mineNum = -1;
			}
			mineNum -= 1;
		}
		if (!shiftP) {
			if (wP && !dP && !sP && !aP || wP && dP && aP && !sP) {
				// Stupidly simple if loop to stop player from walking north off
				// screen.
				if (yy < 8) {
					yy = 0;
				} else {
					if (wallDet(0)) {
						if (Npc.overlap(xx, yy, 31, 15, 8, 0) == 1) {
							yy -= 8;
							curPlay = ImgLoad.play[1];
						} else if (Npc.overlap(xx, yy, 31, 15, 8, 0) == 0) {
							if (Npc.getdy() > 0) {
								if (Npc.getdy() < 8) {
									yy -= (Npc.getdy());
									curPlay = ImgLoad.play[1];
								}
							}
						}
					} else {
						if (yy % 32 != 0) {
							yy -= yy % 32;
							curPlay = ImgLoad.play[1];
						}
					}
				}
			}
			if (aP && !wP && !dP && !sP || aP && wP && sP && !dP) {
				if (xx < 8) {
					xx = 0;
				} else {
					if (wallDet(3)) {
						if (Npc.overlap(xx, yy, 31, 31, 8, 3) == 1) {
							xx -= 8;
							curPlay = ImgLoad.play[4];
						} else if (Npc.overlap(xx, yy, 31, 31, 8, 3) == 0) {
							if (Npc.getdx() > 0) {
								if (Npc.getdx() < 8) {
									xx -= Npc.getdx();
									curPlay = ImgLoad.play[4];
								}
							}
						}
					} else {
						if (xx % 32 <= 8) {
							if (xx % 32 != 0) {
								xx -= xx % 32;
								curPlay = ImgLoad.play[4];
							}
						}
					}
				}
			}
			if (sP && !aP && !wP && !dP || sP && aP && dP && !wP) {
				if (wallDet(2)) {
					if (Npc.overlap(xx, yy, 31, 31, 8, 2) == 1) {
						yy += 8;
						curPlay = ImgLoad.play[3];
					} else if (Npc.overlap(xx, yy, 31, 31, 8, 2) == 0) {
						if (Npc.getdy() > -8) {
							if (Npc.getdy() < 0) {
								yy -= Npc.getdy();
								curPlay = ImgLoad.play[3];
							}
						}
					}
				} else {
					if (32 - (yy % 32) <= 8) {
						yy += 32 - (yy % 32);
						curPlay = ImgLoad.play[3];
					}
				}
			}
			if (dP && !sP && !aP && !wP || dP && sP && wP && !aP) {
				if (wallDet(1)) {
					if (Npc.overlap(xx, yy, 31, 31, 8, 1) == 1) {
						xx += 8;
						curPlay = ImgLoad.play[2];
					} else if (Npc.overlap(xx, yy, 31, 31, 8, 1) == 0) {
						if (Npc.getdx() > -8) {
							if (Npc.getdx() < 0) {
								xx -= Npc.getdx();
								curPlay = ImgLoad.play[2];
							}
						}
					}
				} else {
					if (32 - (xx % 32) <= 8) {
						xx += 32 - (xx % 32);
						curPlay = ImgLoad.play[2];
					}
				}
			}
			// NW
			if (wP && aP && !sP && !dP) {
				if (wallDet(4)) {
					if (Npc.overlap(xx, yy, 31, 31, 5.6, 0) == 1
							&& Npc.overlap(xx, yy, 31, 31, 5.6, 3) == 1) {
						// Needs to do a corner check.
						if (Npc.cornDet(xx, yy, 31, 31, 5.6, 4) == 1) {
							// If this == 0 then it should havea random chance
							// of
							// going north or west
							yy -= 5.6;
							xx -= 5.6;
							curPlay = ImgLoad.play[5];
						}

					} else if (Npc.overlap(xx, yy, 31, 31, 5.6, 0) == 0) {
						if (Npc.overlap(xx, yy, 31, 31, 8, 3) == 1) {
							xx -= 8;
							curPlay = ImgLoad.play[4];
						}
						if (Npc.getdy() > 0) {
							if (Npc.getdy() <= 8) {
								yy -= (Npc.getdy());
								curPlay = ImgLoad.play[1];
							}
						}
					} else if (Npc.overlap(xx, yy, 31, 31, 5.6, 3) == 0) {
						if (Npc.overlap(xx, yy, 31, 31, 8, 0) == 1) {
							// If this returns 0 b/c another being it should get
							// closer.
							yy -= 8;
							curPlay = ImgLoad.play[1];
						}
						if (Npc.getdx() > 0) {
							if (Npc.getdx() <= 8) {
								xx -= Npc.getdx();
								curPlay = ImgLoad.play[4];
							}
						}
					}
				} else {
					if (wallDet(0)) {
						yy -= 8;
						curPlay = ImgLoad.play[1];
					} else {
						if (yy % 32 <= 8 && yy % 32 > 0) {
							yy -= yy % 32;
							curPlay = ImgLoad.play[1];
						}
					}
					if (wallDet(3)) {
						xx -= 8;
						curPlay = ImgLoad.play[4];
					} else {
						if (xx % 32 <= 8 && xx % 32 > 0) {
							xx -= xx % 32;
							curPlay = ImgLoad.play[4];
						}
					}
					// System.out.println(yy % 32);
				}
			}
			if (wP && dP && !sP && !aP) {
				if (wallDet(5)) {
					if (Npc.overlap(xx, yy, 31, 31, 5.6, 0) == 1
							&& Npc.overlap(xx, yy, 31, 31, 5.6, 1) == 1) {
						if (Npc.cornDet(xx, yy, 31, 31, 5.6, 5) == 1) {
							// Needs to do a corner check.
							yy -= 5.6;
							xx += 5.6;
							curPlay = ImgLoad.play[6];
						}
					} else if (Npc.overlap(xx, yy, 31, 31, 5.6, 0) == 0) {
						if (Npc.overlap(xx, yy, 31, 31, 8, 1) == 1) {
							xx += 8;
							curPlay = ImgLoad.play[2];
						}
						if (Npc.getdy() > 0) {
							if (Npc.getdy() < 8) {
								yy -= (Npc.getdy());
								curPlay = ImgLoad.play[1];
							}
						}
					} else if (Npc.overlap(xx, yy, 31, 31, 5.6, 1) == 0) {
						if (Npc.overlap(xx, yy, 31, 31, 8, 0) == 1) {
							yy -= 8;
							curPlay = ImgLoad.play[1];
						}

						if (Npc.getdx() > -8) {
							if (Npc.getdx() < 0) {
								xx -= Npc.getdx();
								curPlay = ImgLoad.play[2];
							}
						}
					}
				} else {
					if (wallDet(0)) {
						yy -= 8;
						curPlay = ImgLoad.play[1];
					} else {
						if (yy % 32 > 0) {
							if (yy % 32 <= 8) {
								yy -= yy % 32;
								curPlay = ImgLoad.play[1];
							}
						}
					}
					if (wallDet(1)) {
						xx += 8;
						curPlay = ImgLoad.play[2];
					} else {
						// System.out.println("xx dit: " + (32 - (xx % 32)));
						if (32 - (xx % 32) <= 8) {
							xx += 32 - (xx % 32);
							curPlay = ImgLoad.play[2];
						}
					}
				}
			}
			if (sP && dP && !wP && !aP) {
				if (wallDet(6)) {
					if (Npc.overlap(xx, yy, 31, 31, 5.6, 2) == 1
							&& Npc.overlap(xx, yy, 31, 31, 5.6, 1) == 1) {
						if (Npc.cornDet(xx, yy, 31, 31, 5.6, 6) == 1) {
							xx += 5.6;
							yy += 5.6;
							curPlay = ImgLoad.play[7];
						}
					} else if (Npc.overlap(xx, yy, 31, 31, 5.6, 2) == 0) {
						if (Npc.overlap(xx, yy, 31, 31, 8, 1) == 1) {
							xx += 8;
							curPlay = ImgLoad.play[2];
						}
						if (Npc.getdy() > -8) {
							if (Npc.getdy() < 0) {
								yy -= Npc.getdy();
								curPlay = ImgLoad.play[3];
							}
						}
					} else if (Npc.overlap(xx, yy, 31, 31, 5.6, 1) == 0) {
						if (Npc.overlap(xx, yy, 31, 31, 8, 2) == 1) {
							yy += 8;
							curPlay = ImgLoad.play[3];
						}

						if (Npc.getdx() > -8) {
							if (Npc.getdx() < 0) {
								xx -= Npc.getdx();
								curPlay = ImgLoad.play[2];
							}
						}
					}
				} else {
					if (wallDet(2)) {
						yy += 8;
						curPlay = ImgLoad.play[3];
					} else {
						if (32 - (yy % 32) <= 8) {
							yy += 32 - (yy % 32);
							curPlay = ImgLoad.play[3];
						}
					}
					if (wallDet(1)) {
						xx += 8;
						curPlay = ImgLoad.play[2];
					} else {
						if (32 - (xx % 32) <= 8) {
							xx += 32 - (xx % 32);
							curPlay = ImgLoad.play[2];
						}
					}
				}
			}
			if (sP && aP && !wP && !dP) {
				if (wallDet(7)) {
					if (Npc.overlap(xx, yy, 31, 31, 5.6, 2) == 1
							&& Npc.overlap(xx, yy, 31, 31, 5.6, 3) == 1) {
						if (Npc.cornDet(xx, yy, 31, 31, 5.6, 7) == 1) {
							xx -= 5.6;
							yy += 5.6;
							curPlay = ImgLoad.play[8];
						}
					} else if (Npc.overlap(xx, yy, 31, 31, 5.6, 2) == 0) {
						if (Npc.overlap(xx, yy, 31, 31, 8, 3) == 1) {
							xx -= 8;
							curPlay = ImgLoad.play[4];
						}
						if (Npc.getdy() > -8) {
							if (Npc.getdy() < 0) {
								yy -= Npc.getdy();
								curPlay = ImgLoad.play[3];
							}
						}
					} else if (Npc.overlap(xx, yy, 31, 31, 5.6, 3) == 0) {
						if (Npc.overlap(xx, yy, 31, 31, 8, 2) == 1) {
							// If this returns 0 b/c another being it should get
							// closer.
							yy += 8;
							curPlay = ImgLoad.play[3];
						}
						if (Npc.getdx() > 0) {
							if (Npc.getdx() <= 8) {
								xx -= Npc.getdx();
								curPlay = ImgLoad.play[4];
							}
						}
					}
				} else {
					if (wallDet(2)) {
						yy += 8;
						curPlay = ImgLoad.play[3];
					} else {
						if (32 - (yy % 32) <= 8 && 32 - (yy % 32) > 0) {
							yy += 32 - (yy % 32);
							curPlay = ImgLoad.play[3];
						}
					}
					if (wallDet(3)) {
						xx -= 8;
						curPlay = ImgLoad.play[4];
					} else {
						if (xx % 32 <= 8 && xx % 32 > 0) {
							xx -= xx % 32;
							curPlay = ImgLoad.play[4];
						}
					}
				}
			}

			if (eP) {
				System.out.println(Npc.dir(xx + 16, yy + 16));
				System.out.println("playLoc: (" + xx + "," + yy + ")");
				System.out.println("len.s " + Panel.mapWall.get(1).length);
			}
			if (spaceP) {
				if (tar == 26) {
					// Need to find the location of the node trying to be mined.

					// if char doesnt move and 20 ticks is passed an item is
					// given to the player.

					// Have a health bar fill up to show progress. should be 12
					// ticks to make it easy then.
					/**
					 * double d = Math.hypot(Math.abs(xx - MineNode.xx),
					 * Math.abs(yy - MineNode.yy));
					 */
					double d = Math
							.hypot(Math.abs(xx
									- (Panel.nodeArray[thisOreId][0] * 32)),
									Math.abs(yy
											- (Panel.nodeArray[thisOreId][1] * 32)));
					if (d < 64) {
						mining = true;
						/*
						 * mineNum = 24 - statMine; if (mineNum < 1) { mineNum =
						 * 1; } // make bar beneath nodes that fills.
						 */
					}
				}

				spaceP = false;
			}
		} else {
			if (wP && !dP && !sP && !aP) {
				curPlay = ImgLoad.play[1];
			}
			if (aP && !wP && !dP && !sP) {
				curPlay = ImgLoad.play[4];
			}
			if (sP && !aP && !wP && !dP) {
				curPlay = ImgLoad.play[3];
			}
			if (dP && !sP && !aP && !wP) {
				curPlay = ImgLoad.play[2];
			}
			if (wP && aP && !sP && !dP) {
				curPlay = ImgLoad.play[5];
			}
			if (wP && dP && !sP && !aP) {
				curPlay = ImgLoad.play[6];
			}
			if (sP && dP && !wP && !aP) {
				curPlay = ImgLoad.play[7];
			}
			if (sP && aP && !wP && !dP) {
				curPlay = ImgLoad.play[8];
			}
		}
		if (attacking) {
			Panel.npCircle();
			attack();
		}
		// De targeting system.

		if (tar != 0) {
			// I do the for loop b/c n the future the 1 would be replaces with
			// the array list of entities.size.
			for (int i = 0; i < 1; i++) {
				if (tar == Npc.num) {
					if (Math.hypot(Math.abs(xx - Npc.xx), Math.abs(yy - Npc.yy)) > 112) {
						tar = 0;
						attacking = false;
					}
				}
			}
		}
		// if (tar != 0) {
		// for (int i = 0; i < 1; i++) {
		// if (Npc.num == tar) {
		// if (Math.abs((xx + 16) - (Npc.xx + 16)) > Panel.width / 2 + 16) {
		// tar = 0;
		// }
		// if (Math.abs((yy + 16) - (Npc.yy + 16)) > Panel.height / 2 + 16) {
		// tar = 0;
		// } } } }
	}

	public void drawPlayer(Graphics g) {
		g.drawImage(curPlay, (int) xx, (int) yy, null);
	}

	/**
	 * Methods
	 */

	String[] itemNames;
	int[] itemMax;

	// items[x] = # of items in a stack

	public void itemInit() {
		itemNames = new String[4];
		itemNames[0] = "No Item";
		itemNames[1] = "wood Sword";
		itemNames[2] = "item2";
		itemNames[3] = "item3";

		// Maximum number of times that item can stack
		itemMax = new int[4];
		itemMax[0] = 0;
		itemMax[1] = 3;
	}

	int bagSlots = 20;
	int[][] inv1;

	public void invInit() {
		inv1 = new int[bagSlots][2];
	}

	public void addInv(int itemNum, int numItems) {
		// d allows me to distribue the correct number of items.
		int d = numItems;

		if (itemMax[itemNum] > 1) {
			for (int a = 0; a < bagSlots; a++) {
				if (inv1[a][0] == itemNum) {
					// same item
					// check to see is max stack.
					if (inv1[a][1] < itemMax[itemNum]) {
						// Im only adding 1 so it works. But for when it is
						// adding more than 1 item this will be buggy.
						d -= 1;
						inv1[a][1] += 1;
					}
				}
			}
		}
		// Should first search through bags for unmaxed stack of same item.
		// Makes a new item stack.
		for (int i = 0; i < bagSlots; i++) {
			// Checks to open slots
			if (inv1[i][0] == 0) {
				// System.out.println("Slot " + i + " open");
				while (d > 0) {
					d -= 1;
					// System.out.println("add Item: "+i);
					// System.out.println("inv: "+inv1[i][0]);
					inv1[i][0] = itemNum;
					inv1[i][1] = 1;
				}
			}
		}
		if (d > 0) {
			System.out.println("Full Invintory");
		}
	}

	public void attack() {
		if (tar == Npc.num) {
			if (Npc.distance(xx + 16, yy + 16) < 112) {
				if (Npc.dir(xx, yy) == 0) {
					if (chaDir() == 2 || chaDir() == 6 || chaDir() == 7) {
						hit();
					}
				}
				if (Npc.dir(xx, yy) == 1) {
					if (chaDir() == 3 || chaDir() == 4 || chaDir() == 7) {
						hit();
					}
				}
				if (Npc.dir(xx, yy) == 2) {
					if (chaDir() == 0 || chaDir() == 4 || chaDir() == 5) {
						hit();
					}
				}
				if (Npc.dir(xx, yy) == 3) {
					if (chaDir() == 1 || chaDir() == 5 || chaDir() == 6) {
						hit();
					}
				}
				if (Npc.dir(xx, yy) == 4) {
					if (chaDir() == 1 || chaDir() == 2 || chaDir() == 6) {
						hit();
					}
				}
				if (Npc.dir(xx, yy) == 5) {
					if (chaDir() == 2 || chaDir() == 3 || chaDir() == 7) {
						hit();
					}
				}
				if (Npc.dir(xx, yy) == 6) {
					if (chaDir() == 0 || chaDir() == 3 || chaDir() == 4) {
						hit();
					}
				}
				if (Npc.dir(xx, yy) == 7) {
					if (chaDir() == 0 || chaDir() == 1 || chaDir() == 5) {
						hit();
					}
				}
			}
		}
	}

	public void hit() {
		if (atk == 0) {
			Npc.health -= 10;
			Panel.cmbt((int) Npc.xx, (int) Npc.yy, 32, 32, 10, 0);
			atk = atkSpd;
		} else {
			atk -= 1;
		}
	}

	public int chaDir() {
		if (curPlay == ImgLoad.play[1]) {
			return 0;
		} else if (curPlay == ImgLoad.play[2]) {
			return 1;
		} else if (curPlay == ImgLoad.play[3]) {
			return 2;
		} else if (curPlay == ImgLoad.play[4]) {
			return 3;
		} else if (curPlay == ImgLoad.play[5]) {
			return 4;
		} else if (curPlay == ImgLoad.play[6]) {
			return 5;
		} else if (curPlay == ImgLoad.play[7]) {
			return 6;
		} else if (curPlay == ImgLoad.play[8]) {
			return 7;
		} else {
			return 0;
		}
	}

	float ben;
	float bes;

	float baes;

	public boolean wallDet(int d) {
		// Block Adjacent Var
		float baw = ((xx - 1) - ((xx - 1) % 32)) / 32;
		float bae = ((xx + 32) - ((xx + 32) % 32)) / 32;

		float ban = ((yy - 1) - ((yy - 1) % 32)) / 32;
		float bas = ((yy + 32) - ((yy + 32) % 32)) / 32;

		// Block Edge Var
		float bee = ((xx + 31) - ((xx + 31) % 32)) / 32;
		float bew = ((xx) - ((xx) % 32)) / 32;

		ben = ((yy) - ((yy) % 32)) / 32;
		bes = ((yy + 31) - ((yy + 31) % 32)) / 32;

		// pixel adjacent + speed
		float bans = ((yy - 1 - 8) - ((yy - 1 - 8) % 32)) / 32;
		baes = ((xx + 32 + 8) - ((xx + 32 + 8) % 32)) / 32;
		float bass = ((yy + 32 + 8) - ((yy + 32 + 8) % 32)) / 32;
		float baws = ((xx - 1 - 8) - ((xx - 1 - 8) % 32)) / 32;

		if (d == 0) {
			for (int i = 0; i < impBs.length; i++) {
				if (Panel.getMapVar((int) bee, (int) bans) == impBs[i]) {
					return false;
				}
				if (Panel.getMapVar((int) bew, (int) bans) == impBs[i]) {
					return false;
				}
			}

		}
		if (d == 1) {
			for (int i = 0; i < impBs.length; i++) {
				// System.out.println("baes: " + baes);
				if (Panel.getMapVar((int) baes, (int) ben) == impBs[i]) {
					return false;
				}
				if (Panel.getMapVar((int) baes, (int) bes) == impBs[i]) {
					return false;
				}
			}
			// return eastS();
		}
		if (d == 2) {
			for (int i = 0; i < impBs.length; i++) {
				// System.out.println("baes: " + baes);
				if (Panel.getMapVar((int) bee, (int) bass) == impBs[i]) {
					return false;
				}
				if (Panel.getMapVar((int) bew, (int) bass) == impBs[i]) {
					return false;
				}
			}
		}
		if (d == 3) {
			int exception = 0;
			for (int i = 0; i < impBs.length; i++) {
				if ((xx - 8) % 32 > 0 && (xx - 8) % 32 < .5) {
					exception = 1;
				}
				// System.out.println("int: " + (baws - 8) % 32);

				if (Panel.getMapVar((int) baws, (int) ben) == impBs[i]) {
					if (exception == 1) {
						return true;
					}
					return false;
				}
				if (Panel.getMapVar((int) baws, (int) bes) == impBs[i]) {
					if (exception == 1) {
						return true;
					}
					return false;
				}
			}
		}
		/**
		 * DIAGONAL DETECIONT
		 */

		if (d == 4) {
			for (int i = 0; i < impBs.length; i++) {
				if (Panel.getMapVar((int) bee, (int) bans) == impBs[i]) {
					return false;
				}
				if (Panel.getMapVar((int) bew, (int) bans) == impBs[i]) {
					return false;
				}
				if (Panel.getMapVar((int) baws, (int) ben) == impBs[i]) {
					return false;
				}
				if (Panel.getMapVar((int) baws, (int) bes) == impBs[i]) {
					return false;
				}

			}
		}
		if (d == 5) {
			for (int i = 0; i < impBs.length; i++) {
				if (Panel.getMapVar((int) bee, (int) bans) == impBs[i]) {
					return false;
				}
				if (Panel.getMapVar((int) bew, (int) bans) == impBs[i]) {
					return false;
				}
				if (Panel.getMapVar((int) baes, (int) ben) == impBs[i]) {
					return false;
				}
				if (Panel.getMapVar((int) baes, (int) bes) == impBs[i]) {
					return false;
				}
			}
		}
		if (d == 6) {
			for (int i = 0; i < impBs.length; i++) {
				if (Panel.getMapVar((int) bee, (int) bass) == impBs[i]) {
					return false;
				}
				if (Panel.getMapVar((int) bew, (int) bass) == impBs[i]) {
					return false;
				}
				if (Panel.getMapVar((int) baes, (int) ben) == impBs[i]) {
					return false;
				}
				if (Panel.getMapVar((int) baes, (int) bes) == impBs[i]) {
					return false;
				}
			}
		}
		if (d == 7) {
			for (int i = 0; i < impBs.length; i++) {
				if (Panel.getMapVar((int) bee, (int) bass) == impBs[i]) {
					return false;
				}
				if (Panel.getMapVar((int) bew, (int) bass) == impBs[i]) {
					return false;
				}
				if (Panel.getMapVar((int) baws, (int) ben) == impBs[i]) {
					return false;
				}
				if (Panel.getMapVar((int) baws, (int) bes) == impBs[i]) {
					return false;
				}
			}
		}
		return true;
	}

	public boolean eastS() {
		if (Panel.getMapVar((int) ben, (int) baes) == 7) {
			return false;
		}
		if (Panel.getMapVar((int) bes, (int) baes) == 7) {
			return false;
		}
		return true;
	}

	static ArrayList<PathFind> temppaths;
	int tid = 0;

	public void makeOrePathFinding(int x, int y) {
		temppaths.add(new PathFind(x, y, tid,
				(int) ((xx + 16) - ((xx + 16) % 32)) / 32,
				(int) ((yy + 16) - ((yy + 16) % 32)) / 32, this));
		tid++;
	}

	void findShortTemp() {

	}

	void conCheck() {

	}

	boolean checkOre(int x, int y) {
		// Check map array to see if you clicked on an ore
		if (Panel.getMapVar(x, y) == 100) {
			return false;
		}
		// If so then it will check the 8 blocks adj to the ore and go to the
		// closest one that is open and has an avaliable path
		return true;
	}

	/**
	 * START OF PATHFINDING
	 */

	// Constructors gets called then conCheck() gets called every tick to
	// completion
	static ArrayList<PathFind> paths;
	int id = 0;

	public void makePathFinding(int x, int y) {
		if (paths.size() != 0) {
			// Panel.setPlayLoc(paths.get(0).px, paths.get(0).py);
			deletePathFinding(paths.get(0).id);
		}

		// (tarx, tary, id, centPlayX, centPlayY)
		paths.add(new PathFind(x, y, id,
				(int) ((xx + 16) - ((xx + 16) % 32)) / 32,
				(int) ((yy + 16) - ((yy + 16) % 32)) / 32, this));
		id++;
	}

	public void deletePathFinding(int id) {
		if (wantToMine) {
			spaceP = true;
			System.out.println("wantToMine off");
			wantToMine = false;
		}
		for (int i = 0; i < paths.size(); i++) {
			if (paths.get(i).id == id) {
				paths.remove(i);
			}
		}
	}

	/**
	 * End of path finding
	 */

	/**
	 * Listeners
	 */

	public void keyPressed(KeyEvent ke) {
		int e = ke.getKeyCode();

		if (e == KeyEvent.VK_SHIFT) {
			shiftP = true;
		}
		if (e == KeyEvent.VK_W) {
			wP = true;
		}
		if (e == KeyEvent.VK_D) {
			dP = true;
		}
		if (e == KeyEvent.VK_S) {
			sP = true;
		}
		if (e == KeyEvent.VK_A) {
			aP = true;
		}
		if (e == KeyEvent.VK_Q) {
			if (!qP) {
				if (qpTim + 20 < Panel.timer()) {
					qpTim = Panel.timer();
					qP = true;
				}
			} else {
				if (qpTim + 20 < Panel.timer()) {
					qpTim = Panel.timer();
					qP = false;
				}
			}
		}
		if (e == KeyEvent.VK_E) {
			eP = true;
		}
		if (e == KeyEvent.VK_SPACE) {
			spaceP = true;
		}
	}

	public void keyReleased(KeyEvent ke) {
		int e = ke.getKeyCode();

		if (e == KeyEvent.VK_SHIFT) {
			shiftP = false;
		}
		if (e == KeyEvent.VK_W) {
			wP = false;
		}
		if (e == KeyEvent.VK_D) {
			dP = false;
		}
		if (e == KeyEvent.VK_S) {
			sP = false;
		}
		if (e == KeyEvent.VK_A) {
			aP = false;
		}
		if (e == KeyEvent.VK_Q) {
			// qP = false;
		}
		if (e == KeyEvent.VK_E) {
			eP = false;
		}
	}

	// when the player walks to an ore this is true, once their path gets
	// deleted if this is still true then start mining, if another path is made
	// then this is set to false.
	boolean wantToMine = false;

	public void tryWalkClick(int clickX, int clickY) {
		int xmouse = (int) (clickX + xx - (Panel.w1 / 2) + 16);
		int ymouse = (int) (clickY + yy - (Panel.h1 / 2) + 16);
		if (Panel.yTop) {
			ymouse = clickY;
		} else if (Panel.yBot) {
			ymouse = clickY + ((Panel.verBlocks * Panel.imgH) - Panel.h1);
		}
		if (Panel.xTop) {
			xmouse = clickX;
		} else if (Panel.xBot) {
			xmouse = clickX + ((Panel.horBlocks * Panel.imgH) - Panel.w1);
		}

		// Find block that i click on
		int x = (xmouse - (xmouse % 32)) / 32;
		int y = (ymouse - (ymouse % 32)) / 32;

		int bestTemp = 0;
		// if not an ore.
		if (checkOre(x, y)) {
			// if you dont click on the box the palyer is currently in
			if ((((xx - (xx % 32)) / 32) != x)
					|| (((yy - (yy % 32)) / 32) != y)) {
				makePathFinding(x, y);
			}
			wantToMine = false;
		} else {
			// if it is an ore
			wantToMine = true;
			// find adj.
			// if the block is not already adjascent
			// System.out.println("findN: (" + x * 32 + ", " + y * 32 +
			// ")");
			// System.out.println("findP: (" + xx + ", " + yy + ")");
			if ((Math.abs(xx - x * 32) > 32) || (Math.abs(yy - y * 32) > 32)) {
				int pathLength = 999;
				// don't actually make a path to each one, just check the
				// path
				// length and then pick the shortest one.
				if (Panel.getMapVar(x - 1, y - 1) == 1) {
					try {
						makeOrePathFinding(x - 1, y - 1);
					} catch (Exception ex) {
					}
				}
				if (Panel.getMapVar(x, y - 1) == 1) {
					try {
						makeOrePathFinding(x, y - 1);
					} catch (Exception ex) {
					}
				}
				if (Panel.getMapVar(x + 1, y - 1) == 1) {
					try {
						makeOrePathFinding(x + 1, y - 1);
					} catch (Exception ex) {
					}
				}
				if (Panel.getMapVar(x - 1, y) == 1) {
					try {
						makeOrePathFinding(x - 1, y);
					} catch (Exception ex) {
					}
				}
				if (Panel.getMapVar(x + 1, y) == 1) {
					try {
						makeOrePathFinding(x + 1, y);
					} catch (Exception ex) {
					}
				}
				if (Panel.getMapVar(x - 1, y + 1) == 1) {
					try {
						makeOrePathFinding(x - 1, y + 1);
					} catch (Exception ex) {
					}
				}
				if (Panel.getMapVar(x, y + 1) == 1) {
					try {
						makeOrePathFinding(x, y + 1);
					} catch (Exception ex) {
					}
				}
				if (Panel.getMapVar(x + 1, y + 1) == 1) {
					try {
						makeOrePathFinding(x + 1, y + 1);
					} catch (Exception ex) {
					}
				}
				for (int i = 0; i < temppaths.size(); i++) {
					// System.out.println("size of I: " +
					// temppaths.get(i).path.size());
					if (pathLength > temppaths.get(i).path.size()) {
						pathLength = temppaths.get(i).path.size();
						bestTemp = i;
					}
				}
				int bestX = temppaths.get(bestTemp).tarX;
				int bestY = temppaths.get(bestTemp).tarY;
				// not start but end
				// System.out.println("bestLoc: (" + bestX + ", " + bestY +
				// ")");
				// when path is done set it to do what the buttons planed to
				// do
				makePathFinding(bestX, bestY);
				temppaths.clear();
				tar = 26;
			}
		}
	}

	int rememberOreX = 0;
	int rememberOreY = 0;

	public void tryWalkDraw(int drawX, int drawY) {
		int xmouse = drawX;
		int ymouse = drawY;

		// Find block that i click on
		int x = (xmouse - (xmouse % 32)) / 32;
		int y = (ymouse - (ymouse % 32)) / 32;

		int bestTemp = 0;
		// if not an ore.
		if (checkOre(x, y)) {
			// if you dont click on the box the palyer is currently in
			if ((((xx - (xx % 32)) / 32) != x)
					|| (((yy - (yy % 32)) / 32) != y)) {
				makePathFinding(x, y);
			}
			wantToMine = false;
		} else {
			// It is an ore
			rememberOreX = x;
			rememberOreY = y;
			System.out.println("remember: (" + rememberOreX + ", "
					+ rememberOreY + ")");
			// if the difference between your x and y is greater than 1.
			int bx = (int) (((xx + 16) - ((xx + 16) % 32)) / 32);
			int by = (int) (((yy + 16) - ((yy + 16) % 32)) / 32);
			System.out.println("playBloc: (" + bx + ", " + by + ")");
			if (Math.abs(rememberOreX - bx) <= 1
					&& Math.abs(rememberOreY - by) <= 1) {
				System.out.println("in range");
				// play is in melee range of the ore.
				// so just straight up mine it yo.
				spaceP = true;
				for (int i = 0; i < Panel.nodeArray.length; i++) {
					if (Panel.nodeArray[i][0] == rememberOreX
							&& Panel.nodeArray[i][1] == rememberOreY) {
						System.out.println("SET IDENTIFICATION");
						System.out.println("id: " + i);
						thisOreId = i;
					}
				}
			} else {
				System.out.println("not close enough");
				// find adj.
				// if the block is not already adjascent
				// System.out.println("findN: (" + x * 32 + ", " + y * 32 +
				// ")");
				// System.out.println("findP: (" + xx + ", " + yy + ")");
				if ((Math.abs(xx - x * 32) > 32)
						|| (Math.abs(yy - y * 32) > 32)) {
					int pathLength = 999;
					// don't actually make a path to each one, just check the
					// path
					// length and then pick the shortest one.
					if (Panel.getMapVar(x - 1, y - 1) == 0) {
						try {
							makeOrePathFinding(x - 1, y - 1);
						} catch (Exception ex) {
						}
					}
					if (Panel.getMapVar(x, y - 1) == 0) {
						try {
							makeOrePathFinding(x, y - 1);
						} catch (Exception ex) {
						}
					}
					if (Panel.getMapVar(x + 1, y - 1) == 0) {
						try {
							makeOrePathFinding(x + 1, y - 1);
						} catch (Exception ex) {
						}
					}
					if (Panel.getMapVar(x - 1, y) == 0) {
						try {
							makeOrePathFinding(x - 1, y);
						} catch (Exception ex) {
						}
					}
					if (Panel.getMapVar(x + 1, y) == 0) {
						try {
							makeOrePathFinding(x + 1, y);
						} catch (Exception ex) {
						}
					}
					if (Panel.getMapVar(x - 1, y + 1) == 0) {
						try {
							makeOrePathFinding(x - 1, y + 1);
						} catch (Exception ex) {
						}
					}
					if (Panel.getMapVar(x, y + 1) == 0) {
						try {
							makeOrePathFinding(x, y + 1);
						} catch (Exception ex) {
						}
					}
					if (Panel.getMapVar(x + 1, y + 1) == 0) {
						try {
							makeOrePathFinding(x + 1, y + 1);
						} catch (Exception ex) {
						}
					}
					for (int i = 0; i < temppaths.size(); i++) {
						// System.out.println("size of I: " +
						// temppaths.get(i).path.size());
						if (pathLength > temppaths.get(i).path.size()) {
							pathLength = temppaths.get(i).path.size();
							bestTemp = i;
						}
					}
					int bestX = temppaths.get(bestTemp).tarX;
					int bestY = temppaths.get(bestTemp).tarY;
					System.out.println("Want to go to: (" + bestX + ", "
							+ bestY + ")");
					// not start but end
					// System.out.println("bestLoc: (" + bestX + ", " + bestY +
					// ")");
					// when path is done set it to do what the buttons planed to
					// do
					makePathFinding(bestX, bestY);
					temppaths.clear();
					tar = 26;
					wantToMine = true;
					// set thisOreId to the id of the ore player is trying to
					// mine
					for (int i = 0; i < Panel.nodeArray.length; i++) {
						if (Panel.nodeArray[i][0] == rememberOreX
								&& Panel.nodeArray[i][1] == rememberOreY) {
							System.out.println("SET IDENTIFICATION");
							System.out.println("id: " + i);
							thisOreId = i;
						}
					}
				}
			}
		}
	}

	public void mousePressed(int clickX, int clickY, int button) {
		// button 1 = left click
		// button 3 = right click

		/*
		 * int xmouse = (int) (clickX+ xx - (Panel.w1 / 2) + 16); int ymouse =
		 * (int) (clickY+ yy - (Panel.h1 / 2) + 16); if (Panel.yTop) { ymouse =
		 * clickY; } else if (Panel.yBot) { ymouse = clickY+ ((Panel.verBlocks *
		 * Panel.imgH) - Panel.h1); } if (Panel.xTop) { xmouse = clickX; } else
		 * if (Panel.xBot) { xmouse = clickX+ ((Panel.horBlocks * Panel.imgH) -
		 * Panel.w1); } // Left click if (me.getButton() == MouseEvent.BUTTON1)
		 * { / if (Npc.clickCheck(xmouse, ymouse)) { tar = Npc.getnum(); } else
		 * { tar = 0; attacking = false; } if (MineNode.clickCheck(xmouse,
		 * ymouse)) { tar = MineNode.getnum(); attacking = true; } else { // tar
		 * = 0; } } // Right click if (me.getButton() == MouseEvent.BUTTON3) {
		 * if (Npc.clickCheck(xmouse, ymouse)) { tar = Npc.getnum(); attacking =
		 * true; } if (MineNode.clickCheck(xmouse, ymouse)) { tar =
		 * MineNode.getnum(); } }
		 */

		int xmouse = (int) (clickX + xx - (Panel.w1 / 2) + 16);
		int ymouse = (int) (clickY + yy - (Panel.h1 / 2) + 16);
		if (Panel.yTop) {
			ymouse = clickY;
		} else if (Panel.yBot) {
			ymouse = clickY + ((Panel.verBlocks * Panel.imgH) - Panel.h1);
		}
		if (Panel.xTop) {
			xmouse = clickX;
		} else if (Panel.xBot) {
			xmouse = clickX + ((Panel.horBlocks * Panel.imgH) - Panel.w1);
		}

		// Find block that i click on
		int x = (xmouse - (xmouse % 32)) / 32;
		int y = (ymouse - (ymouse % 32)) / 32;

		// System.out.println("mouseLocation: (" + x + ", " + y + ")");

		int bestTemp = 0;
		if (button == 1) {
			// if not an ore.
			if (checkOre(x, y)) {
				// if you dont click on the box the palyer is currently in
				if ((((xx - (xx % 32)) / 32) != x)
						|| (((yy - (yy % 32)) / 32) != y)) {
					makePathFinding(x, y);
				}
				wantToMine = false;
			} else {
				wantToMine = true;
				// find adj.
				// if the block is not already adjascent
				// System.out.println("findN: (" + x * 32 + ", " + y * 32 +
				// ")");
				// System.out.println("findP: (" + xx + ", " + yy + ")");
				if ((Math.abs(xx - x * 32) > 32)
						|| (Math.abs(yy - y * 32) > 32)) {
					int pathLength = 999;
					// don't actually make a path to each one, just check the
					// path
					// length and then pick the shortest one.
					if (Panel.getMapVar(x - 1, y - 1) == 1) {
						try {
							makeOrePathFinding(x - 1, y - 1);
						} catch (Exception ex) {
						}
					}
					if (Panel.getMapVar(x, y - 1) == 1) {
						try {
							makeOrePathFinding(x, y - 1);
						} catch (Exception ex) {
						}
					}
					if (Panel.getMapVar(x + 1, y - 1) == 1) {
						try {
							makeOrePathFinding(x + 1, y - 1);
						} catch (Exception ex) {
						}
					}
					if (Panel.getMapVar(x - 1, y) == 1) {
						try {
							makeOrePathFinding(x - 1, y);
						} catch (Exception ex) {
						}
					}
					if (Panel.getMapVar(x + 1, y) == 1) {
						try {
							makeOrePathFinding(x + 1, y);
						} catch (Exception ex) {
						}
					}
					if (Panel.getMapVar(x - 1, y + 1) == 1) {
						try {
							makeOrePathFinding(x - 1, y + 1);
						} catch (Exception ex) {
						}
					}
					if (Panel.getMapVar(x, y + 1) == 1) {
						try {
							makeOrePathFinding(x, y + 1);
						} catch (Exception ex) {
						}
					}
					if (Panel.getMapVar(x + 1, y + 1) == 1) {
						try {
							makeOrePathFinding(x + 1, y + 1);
						} catch (Exception ex) {
						}
					}
					for (int i = 0; i < temppaths.size(); i++) {
						// System.out.println("size of I: " +
						// temppaths.get(i).path.size());
						if (pathLength > temppaths.get(i).path.size()) {
							pathLength = temppaths.get(i).path.size();
							bestTemp = i;
						}
					}
					int bestX = temppaths.get(bestTemp).tarX;
					int bestY = temppaths.get(bestTemp).tarY;
					// not start but end
					// System.out.println("bestLoc: (" + bestX + ", " + bestY +
					// ")");
					// when path is done set it to do what the buttons planed to
					// do
					makePathFinding(bestX, bestY);
					temppaths.clear();
					tar = 26;
				}
			}

		}
		// Right click
		if (button == 3) {
			// check map to see if play can stand there. If display walk button.
			if (Panel.getMapVar(x, y) == 01) {
				// if it is an enterable block thenallow a walk here option.
				// drop down menue shows up at players click location
				System.out.println("Can Walk");
			}
			if (Npc.clickCheck(xmouse, ymouse)) {
				tar = Npc.getnum();
				attacking = true;
			}
			if (MineNode.clickCheck(xmouse, ymouse)) {
				tar = MineNode.getnum();
			}
		}
	}
}
