#include <windows.h> 
#include <iostream>
#include <process.h>
#include <stdio.h> 
#include <time.h>
#include <queue>

using namespace std;
#define  p 4   // количество дочерних потоков
#define n 15000 //размерность матрицы
int A[n][n];
int B[n];
int C[n];
int numberOfTasks[p];
queue<int> tasks[p];
bool run[p];

CRITICAL_SECTION cs[p];
int threadAvilability() {
	int min = n + 1, pos;
	for (int i = 0; i < p; i++) {
		if (numberOfTasks[i] < min) {
			min = numberOfTasks[i];
			pos = i;
		}
	}
	return pos;
}
int popTask(int numberOfThread) { 
	EnterCriticalSection(&cs[numberOfThread]);
	int i = tasks[numberOfThread].front();
	tasks[numberOfThread].pop();
	numberOfTasks[numberOfThread]--;
	LeaveCriticalSection(&cs[numberOfThread]);
	return i;
}
void pushTask(int numberOfThread, int i) {
	EnterCriticalSection(&cs[numberOfThread]);
	tasks[numberOfThread].push(i);
	numberOfTasks[numberOfThread]++;
	LeaveCriticalSection(&cs[numberOfThread]);
}
//умножаем строку на вектор
unsigned int __stdcall ThreadFunction(LPVOID pvParam) {
	int numberOfThread = (int)pvParam;
	while (numberOfTasks[numberOfThread] == 0 && run[numberOfThread]) {

	}
	while (numberOfTasks[numberOfThread] != 0) {
		int i = popTask(numberOfThread);
		C[i] = 0;
		for (int j = 0; j < n; j++) {
			C[i] += A[i][j] * B[j];
		}
	}
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

	for (int i = 0; i < p; i++) {
		numberOfTasks[i] = 0;
		InitializeCriticalSection(&cs[i]);
		run[i] = true;
	}
	//создаем пул потоков
	HANDLE hThreads[p];
	for (int i = 0; i < p; i++) {
		hThreads[i] = (HANDLE)_beginthreadex(NULL, 0, ThreadFunction, (LPVOID)i, 0, NULL);
	}
	for (int i = 0; i < n; i++) {
		int numberOfThread = threadAvilability();
		pushTask(numberOfThread, i);
	}
	for (int i = 0; i < p; i++) {
		run[i] = false;
		WaitForSingleObject(hThreads[i], INFINITE);
	}
	for (int i = 0; i < p; i++) {
		DeleteCriticalSection(&cs[i]);
	}
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