package planningoptimization115657k62.KieuMinhHieu.project;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;

import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPSolver.ResultStatus;
import com.google.ortools.linearsolver.MPVariable;

public class MiniProject_Ortool {

	static {
		System.loadLibrary("jniortools");
	}

	int K; // so nhan vien
	int N; // so khach hang
	int[] d; // thoi gian bao tri cua khacsh hang
	int[][] t;// thoi gian di chuyen

	public MiniProject_Ortool(String file) {
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
			System.out.println("Xay ra loi: ");
			e.printStackTrace();
		}

	}

	public void solve() {

		long start, end;
		start = System.currentTimeMillis();

		MPSolver solver = new MPSolver("Project", MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING);
		MPVariable[][][] x = new MPVariable[N + 1][N + 1][N + 1];

		for (int i = 0; i <= N; i++)
			for (int j = 1; j <= N; j++)
				for (int u = 1; u <= K; u++)
					x[i][j][u] = solver.makeIntVar(0, 1, "x[" + i + "," + j + "," + u + "]");

		// Moi khach hanh chi 1 nhan vien den bao tri
		for (int i = 1; i <= N; i++) {
			MPConstraint c = solver.makeConstraint(1, 1);
			for (int j = 0; j <= N; j++) {
				for (int u = 1; u <= K; u++)
					if (i != j)
						c.setCoefficient(x[j][i][u], 1);
			}
		}

		// Moi khach hang chi di ra <=1
		for (int i = 1; i <= N; i++) {
			MPConstraint c = solver.makeConstraint(0, 1);
			for (int j = 1; j <= N; j++) {
				for (int u = 1; u <= K; u++)
					if (i != j)
						c.setCoefficient(x[i][j][u], 1);
			}
		}

		// Moi nhan vien chi xuat phat tu cty
		for (int u = 1; u <= K; u++) {
			MPConstraint c = solver.makeConstraint(1, 1);
			for (int j = 1; j <= N; j++) {
				c.setCoefficient(x[0][j][u], 1);
			}
		}

		// Khong tao chu trinh con
		for (int u = 1; u <= K; u++) {
			SubSetGenerator generator = new SubSetGenerator(N + 1);
			HashSet<Integer> S = generator.first();
			while (S != null) {
				if (S.size() > 1 && S.size() <= N) {
					MPConstraint s = solver.makeConstraint(0, S.size() - 1);
					for (int i : S) {
						for (int j : S)
							if (i != j) {
								s.setCoefficient(x[i][j][u], 1);
							}
					}
				}
				S = generator.next();
			}
		}

		// Moi nhan vien,
		for (int u = 1; u <= K; u++)
			for (int i = 1; i <= N; i++)
				for (int j = 1; j <= N; j++)
					if (i != j) {
						MPConstraint c = solver.makeConstraint(-1, 0);
						for (int v = 0; v <= N; v++)
							c.setCoefficient(x[v][i][u], -1);

						c.setCoefficient(x[i][j][u], 1);
					}

		int totalTimes = 0;

		for (int i = 0; i <= N; i++)
			for (int j = 0; j <= N; j++)
				totalTimes += t[i][j];

		totalTimes /= 2;

		for (int i = 1; i <= N; i++)
			totalTimes += d[i];

		MPVariable[] y = new MPVariable[K + 1];
		for (int i = 0; i <= K; i++)
			y[i] = solver.makeIntVar(0, totalTimes, "y[" + i + "]");

		MPVariable Y = solver.makeIntVar(0, totalTimes, "Y");

		for (int u = 1; u <= K; u++) {
			MPConstraint c = solver.makeConstraint(0, 0);
			for (int i = 0; i <= N; i++)
				for (int j = 1; j <= N; j++) {
					c.setCoefficient(x[i][j][u], (t[i][j] + d[j]));

				}
			c.setCoefficient(y[u], -1);
		}

		for (int i = 1; i <= K; i++) {
			MPConstraint c = solver.makeConstraint(0, totalTimes);
			c.setCoefficient(y[i], -1);
			c.setCoefficient(Y, 1);
		}

		MPObjective obj = solver.objective();
		obj.setCoefficient(Y, 1);
		obj.setMinimization();
		ResultStatus rs = solver.solve();

		for (int u = 1; u <= K; u++) {
			System.out.print("Thoi gian " + y[u].solutionValue() + "\t" + "Nhan vien " + u + "\t");
			int i = 1;
			while (x[0][i][u].solutionValue() == 0 && i <= N)
				i++;
			if (i > N)
				break;
			int dem = 0;
			System.out.print(" 0 -> ( " + (dem + t[0][i]) + " ) " + i + " ( " + (dem + t[0][i] + d[i]) + " )");
			dem += t[0][i] + d[i];
			int j = 1;
			while (true) {
				while (j <= N && x[i][j][u].solutionValue() == 0)
					j++;
				if (j > N)
					break;
				System.out.print(" -> ( " + (dem + t[i][j]) + " ) "+ j + " ( " + (dem + t[i][j] + d[j]) + " )");
				dem += t[i][j] + d[j];
				i = j;
				j = 1;
			}
			System.out.println();
		}
		System.out.println("Thoi gian nhan vien lam viec nhieu nhat: " + Y.solutionValue());

		end = System.currentTimeMillis();
		System.out.print("Thoi gian chay: " + (end - start) / 1000.0 + "s");
	}

	public static void main(String[] args) {
		MiniProject_Ortool app = new MiniProject_Ortool("7x5.txt");
		app.solve();
	}
}
