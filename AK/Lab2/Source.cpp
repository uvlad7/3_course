#include <mpi.h>
#include <stdio.h>
#include <sys\timeb.h>
#include <iostream>
#include <cmath>
#include <fstream>
double* A, * X, * AB;
int* first_array, * elements_number_array;
int myrank, ranksize;
void Iter_Jacoby(double* X_old, int rows_number, int n, int first) {
	for (int i = 0; i < rows_number; ++i) {
		X[i + first] = A[i * (n + 1) + n];
		for (int j = 0; j < i + first; ++j) {
			X[i + first] -= A[i * (n + 1) + j] * X_old[j];
		}
		for (int j = i + 1 + first; j < n; ++j) {
			X[i + first] -= A[i * (n + 1) + j] * X_old[j];
		}
		X[i + first] /= A[i * (n + 1) + i + first];
	}
}
void SolveSLAE(int n, int rows_number, double Error) {
	double* X_old;
	int first, Result;
	first = first_array[myrank];
	X_old = new double[n];
	do
	{
		for (int i = 0; i < n; ++i) {
			X_old[i] = X[i];
		}
		Iter_Jacoby(X_old, rows_number, n, first);
		MPI_Barrier(MPI_COMM_WORLD);
		MPI_Allgatherv(&X[first], rows_number, MPI_DOUBLE, X, elements_number_array, first_array, MPI_DOUBLE, MPI_COMM_WORLD);
		if (myrank == 0) {
			double norm = fabs(X[0] - X_old[0]), bufer;
			for (int i = 1; i < n; ++i) {
				bufer = fabs(X[i] - X_old[i]);
				if (norm < bufer) {
					norm = bufer;
				}
			}
			Result = Error < norm;
		}
		MPI_Bcast(&Result, 1, MPI_INT, 0, MPI_COMM_WORLD);
	} while (Result);
	delete[] X_old;
	return;
}
int main(int argc, char* argv[])
{
	int rows_number, n, elements_number;
	double Error = 0.0001;
	double t1, t2;
	MPI_Init(&argc, &argv);

	MPI_Comm_size(MPI_COMM_WORLD, &ranksize);
	MPI_Comm_rank(MPI_COMM_WORLD, &myrank);
	t1 = MPI_Wtime();
	if (myrank == 0) {
		std::fstream in("input.txt");
		in >> n;
		AB = new double[n * (n + 1)];
		for (int i = 0; i < n * (n + 1); ++i) {
			in >> AB[i];
		}
	}
	MPI_Bcast(&n, 1, MPI_INT, 0, MPI_COMM_WORLD);
	X = new double[n];
	if (myrank == 0) {
		for (int i = 0; i < n; ++i) {
			X[i] = AB[i * (n + 1) + n] / AB[i * (n + 1) + i];
		}
	}
	MPI_Bcast(X, n, MPI_DOUBLE, 0, MPI_COMM_WORLD);
	rows_number = (n / ranksize) + ((n % ranksize) > myrank ? 1 : 0);
	A = new double[(n + 1) * rows_number];
	first_array = new int[ranksize];
	elements_number_array = new int[ranksize];
	elements_number = (n + 1) * rows_number;
	MPI_Gather(&elements_number, 1, MPI_INT, elements_number_array, 1, MPI_INT, 0, MPI_COMM_WORLD);
	if (myrank == 0) {
		first_array[0] = 0;
		for (int i = 1; i < ranksize; ++i) {
			first_array[i] = first_array[i - 1] + elements_number_array[i - 1];
		}
	}
	MPI_Scatterv(AB, elements_number_array, first_array, MPI_DOUBLE, A, (n + 1) * rows_number, MPI_DOUBLE, 0, MPI_COMM_WORLD);
	if (myrank == 0) {
		for (int i = 0; i < ranksize; ++i) {
			elements_number_array[i] /= n;
			first_array[i] /= n;
		}
	}
	MPI_Bcast(first_array, ranksize, MPI_INT, 0, MPI_COMM_WORLD);
	MPI_Bcast(elements_number_array, ranksize, MPI_INT, 0, MPI_COMM_WORLD);
	SolveSLAE(n, rows_number, Error);
	t2 = MPI_Wtime();
	if (myrank == 0) {
		std::cout << "total time = " << t2 - t1 << "\n";
		std::cout << "X" << "\n";
		for (int i = 0; i < n; ++i) {
			std::cout << X[i] << "\n";
		}
		std::cout << "\n";
	}
	MPI_Finalize();
	std::cin >> rows_number;
	return 0;
}

