package planningoptimization115657k62.KieuMinhHieu.project;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;

public class MiniProject_Choco {

	int K; // so nhan vien
	int N;// so khach hang
	int[] d; // thoi gian bao tri

	int[][] t; // thoi gian di chuyen

	public MiniProject_Choco(String file) {
		read_Data(
				"C:\\Users\\Admin\\Documents\\CourseCPCBLS\\src\\planningoptimization115657k62\\KieuMinhHieu\\project\\data\\"
						+ file);
	}

	public void read_Data(String file) {
		try {
			File fi = new File(file);
			Scanner s = new Scanner(fi);

			N = s.nextInt();
			K = s.nextInt();

			d = new int[N + 1];
			d[0] = 0;
			for (int i = 1; i <= N; i++) {
				d[i] = s.nextInt();
			}

			t = new int[N + 1][N + 1];
			for (int i = 0; i <= N; i++) {
				for (int j = 0; j <= N; j++) {
					t[i][j] = s.nextInt();
				}
			}
			s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public int distance(int i, int j) {
		if (j > N + K)
			return 0;
		if (i > N)
			i = 0;
		if (j > N)
			j = 0;
		return t[i][j] + d[j];
	}

	public void solve() {
		long start, end;
		start = System.currentTimeMillis();

		Model model;
		model = new Model("MiniProject");
		IntVar[] X;
		IntVar[] T;
		IntVar[] R;

		X = model.intVarArray(N + K + 1, 1, N + 2 * K);
		T = model.intVarArray(N + 2 * K + 1, 0, 1000);
		R = model.intVarArray(N + 2 * K + 1, 1, K);

		for (int i = 1; i <= N + K; i++) {
			model.arithm(X[i], "!=", i).post();
			for (int j = i + 1; j <= N + K; j++) {
				model.arithm(X[i], "!=", X[j]).post();
			}
		}

		//Phai di it nhat mot nhan vien
		for (int i = 1; i <= K; i++) {
			model.arithm(X[N + i], "!=", N + K + i).post();
		}

		for (int i = 1; i <= K; i++) {
			model.arithm(R[N + i], "=", i).post();
			model.arithm(R[N + K + i], "=", i).post();
		}

		for (int i = N + 1; i <= N + K; i++) {
			model.arithm(T[i], "=", 0).post();
		}

		for (int i = 1; i <= N + K; i++) {
			for (int j = 1; j <= N + 2 * K; j++) {
				model.ifThen(model.arithm(X[i], "=", j), model.arithm(R[i], "=", R[j]));
				model.ifThen(model.arithm(X[i], "=", j),
						model.arithm(T[j], "=", model.intOffsetView(T[i], distance(i, j))));
			}
		}

		IntVar y = model.intVar(0, 1000);
		for (int i = 1; i <= K; i++) {
			model.arithm(T[N + K + i], "<=", y).post();
		}

		model.setObjective(Model.MINIMIZE, y);

		Solver s = model.getSolver();

		while (s.solve()) {
			System.out.println("Obj = " + y.getValue());
			for (int i = 1; i <= K; i++) {
				System.out.print("Thoi gian " + i + ": " + T[N + K + i].getValue() + "\t");
				System.out.print("Nhan vien " + i + ": 0 ");
				int next = X[N + i].getValue();
				while (next <= N) {
					System.out.print(" -> " + next);
					next = X[next].getValue();
				}
				System.out.println();
			}
			System.out.println("---------------oOo--------------");
		}

		end = System.currentTimeMillis();
		System.out.print("Thoi gian chay: " + (end - start) / 1000.0 + "s");
	}	

	public static void main(String[] args) {
		MiniProject_Choco app = new MiniProject_Choco("7x4.txt");
		app.solve();
	}
}
