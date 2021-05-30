#include <windows.h> 
#include <process.h>
#include <stdio.h> 
#include <time.h> 
#include <iostream>

using namespace std;
#define  p 4   // количество дочерних потоков

#define n 15000 //размерность матрицы

int AvailableThreads = p;
int A[n][n];
int B[n];
int C[n];

CRITICAL_SECTION cs;

bool threadAvilability() {
	return (AvailableThreads > 0);
}
void incrementThreadAvailbility() {
	EnterCriticalSection(&cs);
	AvailableThreads++;
	LeaveCriticalSection(&cs);
}
void decrementThreadAvailbility() {
	EnterCriticalSection(&cs);
	AvailableThreads--;
	LeaveCriticalSection(&cs);
}

//умножаем строку на вектор
unsigned int __stdcall ThreadFunction(LPVOID pvParam) {
	int i = (int)pvParam;
	C[i] = 0;
	for (int j = 0; j < n; j++) {
		C[i] += A[i][j] * B[j];
	}
	incrementThreadAvailbility();
	return 0;
}

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

	InitializeCriticalSection(&cs);
	int i = 0;
	while (i < n) {
		if (threadAvilability()) {
			decrementThreadAvailbility();
			(HANDLE)_beginthreadex(NULL, 0, ThreadFunction, (LPVOID)i, 0, NULL);
			i++;
		}
	}

	while (p > AvailableThreads) {

	}
	DeleteCriticalSection(&cs);
	/*for (int i = 0; i < n; i++) {
		for (int j = 0; j < n; j++) {
			std::cout << A[i][j] << " ";
		}
		std::cout << endl;
	}
	std::cout << endl;
	for (int i = 0; i < n; i++) {
		std::cout << B[i] << " ";
	}
	std::cout << endl;
	for (int i = 0; i < n; i++) {
		std::cout << C[i] << " ";
	}
	std::cout << endl;*/
	QueryPerformanceCounter(&liFinishTime);
	// вычисляет время в миллисекундах
	double dElapsedTimeDOUBle = 1000. * (liFinishTime.QuadPart - liStartTime.QuadPart) / liFrequency.QuadPart;
	printf("Time = %.16f\n", dElapsedTimeDOUBle);
	return 0;
}