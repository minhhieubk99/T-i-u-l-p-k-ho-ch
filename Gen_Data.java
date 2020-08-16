package planningoptimization115657k62.KieuMinhHieu.project;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Gen_Data {
	public void generate() {
		Random r = new Random();
		r.setSeed(10);
		int maxN = 20, maxT = 10;
		int N = 3 + r.nextInt(maxN);
		int K;
		do {
			K = 3 + r.nextInt(maxN);
		} while (K >= N);

		N = 7;
		K = 4;

		String folder = "C:\\Users\\Admin\\Documents\\CourseCPCBLS\\src\\planningoptimization115657k62\\KieuMinhHieu\\project\\data\\";
		String filename = folder + String.valueOf(N) + "x" + String.valueOf(K) + ".txt";

		System.out.print(filename);

		File file = new File(filename);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException ex) {
				Logger.getLogger(Gen_Data.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(filename);
		} catch (FileNotFoundException ex) {
			Logger.getLogger(Gen_Data.class.getName()).log(Level.SEVERE, null, ex);
		}

		PrintWriter pw = new PrintWriter(fos);

		pw.println(N + " " + K);
		
		for (int i = 0; i < N; i++)
			pw.print(3 + r.nextInt(maxT) + " ");
		pw.println();

		int t[][] = new int[N + 1][N + 1];

		for (int i = 0; i <= N; i++) {
			for (int j = 0; j <= N; j++) {
				if (i == j) {
					t[i][j] = 0;
				} else {
					if (i < j)
						t[i][j] = 3 + r.nextInt(maxT);
					else
						t[i][j] = t[j][i];
				}
				pw.print(t[i][j] + " ");
			}
			pw.println();
		}

		pw.close();
	}

	public static void main(String[] arg) {
		Gen_Data app = new Gen_Data();
		app.generate();
	}
}