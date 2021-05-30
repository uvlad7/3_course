#include <windows.h> 
#include <process.h>
#include <stdio.h> 
#include <time.h> 
#include <iostream>

using namespace std;

#define n 15000 //размерность матрицы

int A[n][n];
int B[n];
int C[n];

int main() {
	for (int i = 0; i < n; i++) {
		for (int j = 0; j < n; j++)
			A[i][j] = rand() % 10;
	}
	for (int i = 0; i < n; i++) {
		B[i] = rand() % 10;
	}

	LARGE_INTEGER liFrequency, liStartTime, liFinishTime;
	// получаем частоту
	QueryPerformanceFrequency(&liFrequency);
	// получаем стартовое время
	QueryPerformanceCounter(&liStartTime);

	for (int i = 0; i < n; i++) {
		C[i] = 0;
		for (int j = 0; j < n; j++) {
			C[i] += A[i][j] * B[j];
		}
	}
	QueryPerformanceCounter(&liFinishTime);
	// вычисляет время в миллисекундах
	double dElapsedTimeDOUBle = 1000. * (liFinishTime.QuadPart - liStartTime.QuadPart) / liFrequency.QuadPart;
	printf("Time = %.16f\n", dElapsedTimeDOUBle);
	return 0;
}