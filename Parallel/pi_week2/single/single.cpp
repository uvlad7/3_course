#include <windows.h> 
#include <time.h>
#include <process.h> 
#include <stdio.h> 

double pi;
int n = 100000;
unsigned int __stdcall ThreadFunction() {
	int i, start;
	double h, sum, x;
	h = 1. / n;  sum = 0.;
	for (i = 0; i < n; i++) {
		x = h * i;
		sum += 4. / (1. + x * x);
	}
	pi = h * sum;
	return 0;
}

int main() {
	LARGE_INTEGER liFrequency, liStartTime, liFinishTime;
	// частота
	QueryPerformanceFrequency(&liFrequency);
	// стартовое время
	QueryPerformanceCounter(&liStartTime);
	ThreadFunction();
	QueryPerformanceCounter(&liFinishTime);
	// вычисляет время в миллисекундах
	double dElapsedTimeDOUBle = 1000. * (liFinishTime.QuadPart - liStartTime.QuadPart) / liFrequency.QuadPart;
	printf("PI = %.16f\n", pi);
	printf("Time = %.16f\n", dElapsedTimeDOUBle);
	return 0;
}