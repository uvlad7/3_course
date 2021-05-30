#include <windows.h> 
#include <process.h> 
#include <stdio.h> 

#define p 4 // ���������� �������� �������
int n = 100000000; // ��� ���������, ������������� ������� ������
struct SThreadParam { int k;  double sum; };


unsigned int __stdcall ThreadFunction(LPVOID pvParam) {
	SThreadParam* param = (SThreadParam*)pvParam;
	int i, start;
	double h, sum, x;  h = 1. / n;
	sum = 0.;
	start = param->k;
	for (i = start; i < n; i += p) {
		x = h * i;
		sum += 4. / (1. + x * x);
	}
	// � ���������� ���������� �� ������� ����� ���������� 
	param->sum = h * sum;
	return 0;
}

int main() {
	LARGE_INTEGER liFrequency, liStartTime, liFinishTime;
	// �������� �������
	QueryPerformanceFrequency(&liFrequency);
	// �������� ��������� �����
	QueryPerformanceCounter(&liStartTime);

	HANDLE hThreads[p]; // ������ ������������ �������  
	// ������ ���������� ��������� ������� 
	SThreadParam params[p];
	int k;
	double sum;
	// �������� �������� �������  
	for (k = 0; k < p; ++k) {
		params[k].k = k;
		hThreads[k] = (HANDLE)_beginthreadex(NULL, 0, ThreadFunction, (LPVOID) & (params[k]), 0, NULL);
		if (hThreads[k] == NULL) {// ��������� ������   
			printf("Create Thread %d Error=%d\n", k, GetLastError());
			return -1;
		}
	}
	// �������� ���������� �������� ������� 
	WaitForMultipleObjects(p, hThreads, TRUE, INFINITE);
	for (k = 0; k < p; ++k)
		CloseHandle(hThreads[k]);

	sum = 0.;
	for (k = 0; k < p; ++k)
		sum += params[k].sum;
	QueryPerformanceCounter(&liFinishTime);
	// ��������� ����� � �������������
	double dElapsedTimeDOUBle = 1000. * (liFinishTime.QuadPart - liStartTime.QuadPart) / liFrequency.QuadPart;
	printf("PI = %.16f\n", sum);
	printf("Time = %.16f\n", dElapsedTimeDOUBle);
	return 0;
}