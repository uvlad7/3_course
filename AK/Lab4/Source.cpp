#include <mpi.h>
#include <stdio.h>
#include <sys\timeb.h>
#include <iostream>
#include <cmath>
#include <fstream>
#include <string>

double f(double x, double y) {
	return x * y;
}

double f1(double y) {  // u(0, y), f1(0) = f3(0)
	return y * y * y;
}

double f2(double y) {  // u(a, y), f2(b) = f4(a)
	return 1 + y;
}

double f3(double x) { // u(x, 0), f3(a) = f2(0)
	return x * x * x;
}

double f4(double x) { // u(x, b), f4(0) = f1(b)
	return x + 1;
}

int main(int args, char** argv) {
	const int n = 249, m = 499;
	const double a = 1, b = 1;
	const double h = a / (n + 1), t = b / (m + 1);
	const double epsilon = 0.0001;
	int size, rank;
	double** u;
	MPI_Init(&args, &argv);
	double time1 = MPI_Wtime();
	MPI_Comm_size(MPI_COMM_WORLD, &size);
	MPI_Comm_rank(MPI_COMM_WORLD, &rank);
	int len = (rank == 0 ? m - (m / size)*(size - 1) : m / size) + 2;
	u = new double* [len];
	for (int i = 0; i < len; i++) {
		u[i] = new double[n + 2];
		for (int j = 0; j < n + 2; j++) {
			u[i][j] = 0;
		}
	}
	if (rank == 0) {
		for (int i = 0; i < n + 2; i++) {
			u[0][i] = f3(h * i);
		}
	}
	if (rank == size - 1) {
		for (int i = 0; i < n + 2; i++) {
			u[len - 1][i] = f4(h * i);
		}
	}
	for (int i = 0; i < len; i++) {
		u[i][0] = f1(((rank == 0 ? 0 : m - (m / size) * (size - 1) + 1 + (rank - 1) * (m / size + 1)) + i) * t);
		u[i][n + 1] = f2(((rank == 0 ? 0 : m - (m / size) * (size - 1) + 1 + (rank - 1) * (m / size + 1)) + i) * t);
	}
	double dmax_all;
	do {
		if (rank != size - 1) {
			MPI_Send(u[len - 2], n + 2, MPI_DOUBLE, rank + 1, 0, MPI_COMM_WORLD);
		}
		if (rank != 0) {
			MPI_Recv(u[0], n + 2, MPI_DOUBLE, rank - 1, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
		}
		if (rank != 0) {
			MPI_Send(u[1], n + 2, MPI_DOUBLE, rank - 1, 0, MPI_COMM_WORLD);
		}
		if (rank != size - 1) {
			MPI_Recv(u[len - 1], n + 2, MPI_DOUBLE, rank + 1, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
		}
		double dmax = 0;
		for (int i = 1; i <= len - 2; i++) {
			for (int j = 1; j <= n; j++) {
				double temp = u[i][j];
				u[i][j] = (0.5 / (h * h + t * t)) *
					(((u[i][j - 1] + u[i][j + 1]) * t * t) + ((u[i - 1][j] + u[i + 1][j]) * h * h) -
						h * h * t * t * f(h * j, t * ((rank == 0 ? 0 : m - (m / size) * (size - 1) + 1 + (rank - 1) * (m / size + 1)) + i)));
				double dm = abs(temp - u[i][j]);
				if (dmax < dm) {
					dmax = dm;
				}
			}
		}
		MPI_Reduce(&dmax, &dmax_all, 1, MPI_DOUBLE, MPI_MAX, 0, MPI_COMM_WORLD);
		MPI_Bcast(&dmax_all, 1, MPI_DOUBLE, 0, MPI_COMM_WORLD);
	} while (dmax_all > epsilon);

	MPI_Barrier(MPI_COMM_WORLD);
	double time2 = MPI_Wtime();
	if (rank == 0) {
		std::cout << "time = " << time2 - time1 << "\n" <<std::flush;
	}

	if (rank == 0) {
		std::ofstream fout("output" + std::to_string(size) + ".txt");
		for (int i = 0; i < len - 1; i++) {
			for (int j = 0; j < n + 1; j++) {
				fout << u[i][j] << " ";
			}
			fout << u[i][n + 1] << "\n";
		}
		if (size == 1) {
			for (int j = 0; j < n + 1; j++) {
				fout << u[len - 1][j] << " ";
			}
			fout << u[len - 1][n + 1] << "\n";
			MPI_Finalize();
			return 0;
		}
		double* V = new double[n + 2];
		for (int k = 1; k < size; k++) {
			for (int i = 1; i < ((k == size - 1) ? m / size + 2 : m / size + 1); i++) {
				MPI_Recv(V, n + 2, MPI_DOUBLE, k, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
				for (int j = 0; j < n + 1; j++) {
					fout << V[j] << " ";
				}
				fout << V[n + 1] << "\n";
			}
		}

	}
	else {
		for (int i = 1; i < ((rank == size - 1) ? m / size + 2 : m / size + 1); i++) {
			MPI_Send(u[i], n + 2, MPI_DOUBLE, 0, 0, MPI_COMM_WORLD);
		}
	}
	MPI_Finalize();
	return 0;
}
