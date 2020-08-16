package planningoptimization115657k62.KieuMinhHieu.project;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.constraints.basic.Implicate;
import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.NotEqual;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import localsearch.selectors.MinMaxSelector;

public class MiniProject_OpenCBLS {

	int N; // so khach hang
	int K; // so nhan vien

	int[] _d, d; // thoi gian bao tri
	int[][] _t, t; // thoi gian di chuyen

	LocalSearchManager mgr;
	VarIntLS[] X;
	VarIntLS[] Route;
	VarIntLS[] T;
	VarIntLS y;
	ConstraintSystem S;

	public int distance(int i, int j) {
		i = i + 1;
		j = j + 1;
		if (j > N + K)
			return 0;
		if (i > N)
			i = 0;
		if (j > N)
			j = 0;
		return t[i][j] + d[j];
	}

	public MiniProject_OpenCBLS(String file) {
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



	public void createModel() {
		mgr = new LocalSearchManager();
		S = new ConstraintSystem(mgr);

		X = new VarIntLS[N + K];
		for (int i = 0; i < N + K; i++) {
			X[i] = new VarIntLS(mgr, 0, N + 2 * K - 1);
		}

		int total = 0;
		for (int i = 0; i <= N; i++) {
			total += d[i];
			for (int j = 0; j <= N; j++) {
				total += t[i][j];
			}
		}

		Route = new VarIntLS[N + 2 * K];
		T = new VarIntLS[N + 2 * K];
		for (int i = 0; i < N + 2 * K; i++) {
			Route[i] = new VarIntLS(mgr, 0, K - 1);
			T[i] = new VarIntLS(mgr, 0, total);
		}

		y = new VarIntLS(mgr, 0, total);
	}

	public void createConstraint() {
		S.post(new AllDifferent(X));

		for (int i = 0; i < N + K; i++) {
			S.post(new NotEqual(X[i], i));
			for (int j = N; j < N + K; j++) {
				S.post(new NotEqual(X[i], j));
			}
		}

		for (int i = 0; i < K; i++) {
			S.post(new IsEqual(Route[N + i], i));
			S.post(new IsEqual(Route[N + K + i], i));
		}

		for (int i = 0; i < K; i++) {
			S.post(new IsEqual(T[N + i], 0));
		}

		for (int i = 0; i < N + K; i++) {
			for (int j = 0; j < N + 2 * K; j++) {
				IConstraint c1 = new IsEqual(X[i], j);
				IConstraint c2 = new IsEqual(Route[i], Route[j]);
				IConstraint c3 = new IsEqual(new FuncPlus(T[i], distance(i, j)), T[j]);

				S.post(new Implicate(c1, c2));
				S.post(new Implicate(c1, c3));
			}
		}

		for (int i = 0; i < K; i++) {
			S.post(new LessOrEqual(T[N + K + i], y));
		}
		mgr.close();
	}

	public void search(int maxIter) {
		System.out.println("Init S = " + S.violations());
		MinMaxSelector mms = new MinMaxSelector(S);

		int it = 0;
		while (it < maxIter && S.violations() > 0) {
			VarIntLS sel_x = mms.selectMostViolatingVariable();
			int sel_v = mms.selectMostPromissingValue(sel_x);

			sel_x.setValuePropagate(sel_v);
			System.out.println("Step " + it + ", S = " + S.violations());

			it++;
		}

		System.out.println("Thoi lam lam viec lon nhat cua 1 nhan vien la: " + S.violations());
	}

	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		MiniProject_OpenCBLS sol = new MiniProject_OpenCBLS("7x4.txt");
		sol.createModel();
		sol.createConstraint();
		sol.search(1000);
		long end = System.currentTimeMillis();
		System.out.println("Total time: " + (end - start) / 1000.0 + " s");
	}

}