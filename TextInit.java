package clickMove;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class TextInit {
	static ArrayList<int[]> map1;
	static ArrayList<int[]> map2;

	public static void readMap() throws IOException {

		map1 = new ArrayList<int[]>();
		map2 = new ArrayList<int[]>();

		BufferedReader inputStream = null;

		try {
			InputStream is = TextInit.class.getResourceAsStream("res/MapW.txt");
			inputStream = new BufferedReader(new InputStreamReader(is));

			String l;
			while ((l = inputStream.readLine()) != null) {
				String delims = "[ ]+";
				String[] tokens = l.split(delims);
				int[] e = new int[tokens.length];
				for (int i = 0; i < tokens.length; i++) {
					e[i] = Integer.parseInt(tokens[i]);
				}
				map1.add(e);
			}
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}

		// reads the second map.
		inputStream = null;

		try {
			InputStream is = TextInit.class
					.getResourceAsStream("res/MapGround.txt");
			inputStream = new BufferedReader(new InputStreamReader(is));

			String l;
			while ((l = inputStream.readLine()) != null) {
				String delims = "[ ]+";
				String[] tokens = l.split(delims);
				int[] e = new int[tokens.length];
				for (int i = 0; i < tokens.length; i++) {
					e[i] = Integer.parseInt(tokens[i]);
				}
				map2.add(e);
			}
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
	}

	// Doest get used
	public static void readUsingFileReader(String fileName) throws IOException {
		File file = new File(fileName);
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		// String line;

		String l;
		while ((l = br.readLine()) != null) {
			// process the line
			String delims = "[ ]+";
			String[] tokens = l.split(delims);
			int[] e = new int[tokens.length];
			for (int i = 0; i < tokens.length; i++) {
				e[i] = Integer.parseInt(tokens[i]);
			}
			map1.add(e);
		}
		br.close();
		fr.close();

	}

	public static ArrayList<int[]> getmap1() {
		return map1;
	}

	public static ArrayList<int[]> getmap2() {
		return map2;
	}

}
