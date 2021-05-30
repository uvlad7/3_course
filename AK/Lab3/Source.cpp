#include<mpi.h>

#define _CRT_SECURE_NO_WARNINGS
#include <fstream>
#include <iostream>

int main(int argc, char** argv)
{
	int procs_rank, procs_count;
	MPI_Status status;
	double t1, t2;
	const auto n = 1000;
	double* A = new double[n * n];

	MPI_Init(&argc, &argv);  //инициализация MPI
	MPI_Comm_size(MPI_COMM_WORLD, &procs_count); //определение кол-ва процессов, записывает в procs_count
	MPI_Comm_rank(MPI_COMM_WORLD, &procs_rank);	 //определение ранка вызывающего процесса, записывает в procs_rank

	//чтение матрицы из файла
	if (procs_rank == 0)
	{
		std::ifstream in("input.txt");
		for (auto i = 0; i < n; i++)
			for (auto j = 0; j < n; j++)
				in >> A[i * n + j];
	}

	MPI_Bcast(A, n * n, MPI_DOUBLE, 0, MPI_COMM_WORLD); //рассылка матрицы всем процессам

	t1 = MPI_Wtime(); //время старта 
	//LU декомпозиция
	for (int k = 0; k < n - 1; k++)
	{
		if (k % procs_count == procs_rank)
			for (int i = k + 1; i < n; i++)
				A[k * n + i] /= A[k * n + k];

		MPI_Bcast(&(A[k * n + k + 1]), n - k - 1, MPI_DOUBLE, k % procs_count, MPI_COMM_WORLD); //рассылка полученных значений матрицы во все процессы

		for (int i = k + 1; i < n; i++)
			if (i % procs_count == procs_rank)
				for (int j = k + 1; j < n; j++)
					A[i * n + j] -= A[i * n + k] * A[k * n + j];
	}

	t2 = MPI_Wtime();
	//вывод данных в файл
	if (procs_rank == 0)
	{
		std::ofstream print("output.txt");
		print << "L:" << std::endl;
		for (auto i = 0; i < n; i++)
		{
			if (i % procs_count != 0)
				MPI_Recv(&(A[i * n]), n, MPI_DOUBLE, i % procs_count, 100, MPI_COMM_WORLD, &status); //сбор данных с проццессов в корневом процессе
			for (auto j = 0; j <= i; j++)
				print << A[i * n + j] << " ";
			for (auto j = i + 1; j < n; j++)
				print << "0 ";
			print << std::endl;
		}
		print << std::endl << "U:" << std::endl;
		for (auto i = 0; i < n; i++)
		{
			for (auto j = 0; j < i; j++)
				print << "0 ";
			print << "1 ";
			for (auto j = i + 1; j < n; j++)
				print << A[i * n + j] << " ";
			print << std::endl;
		}
		print.close();
	}
	else
	{
		for (auto i = procs_rank; i < n; i += procs_count)
			MPI_Send(&(A[i * n]), n, MPI_DOUBLE, 0, 100, MPI_COMM_WORLD); //отправка данных текущего процесса корневому
	}


	if (procs_rank == 0)
		std::cout << "Working time: " << t2 - t1 << std::endl;

	MPI_Finalize(); //завершение работы MPI
	return 0;
}
