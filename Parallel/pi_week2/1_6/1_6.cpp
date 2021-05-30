#include <windows.h> 
#include <process.h>
#include <stdio.h> 
#include <time.h> 
#include <chrono>

#define  p 4
// количество дочерних потоков 
int n = 100000000;
double pi = 0.;
// требуется взаимоисключающий доступ
CRITICAL_SECTION cs;


unsigned int __stdcall ThreadFunction(LPVOID pvParam) {
	int nParam = (int)pvParam;
	int i, start;
	double h, sum, x;
	h = 1. / n;
	sum = 0.;
	start = nParam;
	for (i = start; i < n; i += p) {
		x = h * i;   sum += 4. / (1. + x * x);
	}
	// монопольный доступ  
	EnterCriticalSection(&cs);
	pi += h * sum;
	LeaveCriticalSection(&cs);
	return 0;
}

int main() {
	//LARGE_INTEGER liFrequency, liStartTime, liFinishTime;
	//// получаем частоту
	//QueryPerformanceFrequency(&liFrequency);
	//// получаем стартовое время
	//QueryPerformanceCounter(&liStartTime);

	// получаем стартовое время
	// DWORD dwStartTime = GetTickCount();

	/*clock_t time_start;
	time_start = clock();*/

	/*std::chrono::system_clock::time_point start = std::chrono::system_clock::now();*/

	HANDLE hThreads[p];
	int k;
	InitializeCriticalSection(&cs);
	// создание дочерних потоков 
	for (k = 0; k < p; ++k) {
		hThreads[k] = (HANDLE)_beginthreadex(NULL, 0, ThreadFunction, (LPVOID)k, 0, NULL);
		if (hThreads[k] == NULL) {// обработка ошибки   
			printf("Create Thread %d Error=%d\n", k, GetLastError());    return -1;
		}
	}
	// ожидание завершения дочерних потоков
	WaitForMultipleObjects(p, hThreads, TRUE, INFINITE);
	for (k = 0; k < p; ++k)
		CloseHandle(hThreads[k]);
	// освобождение ресурсов, занятых критической секцией 
	DeleteCriticalSection(&cs);

	//QueryPerformanceCounter(&liFinishTime);
	//// вычисляет время в миллисекундах
	//double dElapsedTimeDOUBle = 1000. * (liFinishTime.QuadPart - liStartTime.QuadPart) / liFrequency.QuadPart;
	//printf("Time = %.16f\n", dElapsedTimeDOUBle);

	//DWORD dwElapsedTime = GetTickCount() - dwStartTime;
	//printf("Time = %ld\n", dwElapsedTime);

	/*clock_t time_end;
	time_end = clock() - time_start;
	double dElapsedTimeDOUBle = 1000. * (double)time_end / CLOCKS_PER_SEC;
	printf("Time = %.16f\n", dElapsedTimeDOUBle);*/

	/*std::chrono::system_clock::time_point end = std::chrono::system_clock::now();
	std::chrono::duration<double> elapsed = end - start;
	printf("Time = %.16f\n", elapsed.count() * 1000.);*/
	printf("PI = %.16f\n", pi);
	return 0;
}