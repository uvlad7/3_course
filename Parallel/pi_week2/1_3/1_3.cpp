#include <windows.h> 
#include <time.h>
#include <process.h> 
#include <stdio.h> 

#define p 4 // ���������� �������� �������
double pi[p];
int n = 100000;
unsigned int __stdcall ThreadFunction(LPVOID pvParam) {
	int nParam = (int)pvParam;
	int i, start;
	double h, sum, x;
	h = 1. / n;  sum = 0.;
	start = nParam;
	for (i = start; i < n; i += p) {
		x = h * i;
		sum += 4. / (1. + x * x);
	}
	pi[nParam] = h * sum;
	return 0;
}

int main() {
	LARGE_INTEGER liFrequency, liStartTime, liFinishTime;
	// �������� �������
	QueryPerformanceFrequency(&liFrequency);
	// �������� ��������� �����
	QueryPerformanceCounter(&liStartTime);

	HANDLE hThreads[p];
	int k;

	double sum;  // �������� �������� �������
	// �������� ��������� �����
	// DWORD dwStartTime = GetTickCount();
	//LARGE_INTEGER litFrequency, litStartTime, litFinishTime;
	//// �������� �������
	//QueryPerformanceFrequency(&litFrequency);
	//// �������� ��������� �����
	//QueryPerformanceCounter(&litStartTime);
	for (k = 0; k < p; ++k) {
		hThreads[k] = (HANDLE)_beginthreadex(NULL, 0, ThreadFunction, (LPVOID)k, 0, NULL);
		if (hThreads[k] == NULL) { // ��������� ������     
			printf("Create Thread %d Error=%d\n", k, GetLastError());
			return -1;
		}
	}  // �������� ���������� �������� ������� 
	// ���������� ����������
	// DWORD dwElapsedTime = GetTickCount() - dwStartTime;
	// printf("Time for creating threads = %lu\n", (double) dwElapsedTime);
	//QueryPerformanceCounter(&litFinishTime);
	//// ��������� ����� � �������������
	//double dTElapsedTimeDOUBle = 1000. * (litFinishTime.QuadPart - litStartTime.QuadPart) / litFrequency.QuadPart;
	//printf("Time for creating threads = %.16f\n", dTElapsedTimeDOUBle);
	WaitForMultipleObjects(p, hThreads, TRUE, INFINITE);
	for (k = 0; k < p; ++k)
		CloseHandle(hThreads[k]);
	sum = 0.;
	for (k = 0; k < p; ++k)
		sum += pi[k];
	QueryPerformanceCounter(&liFinishTime);
	// ��������� ����� � �������������
	double dElapsedTimeDOUBle = 1000. * (liFinishTime.QuadPart - liStartTime.QuadPart) / liFrequency.QuadPart;
	printf("PI = %.16f\n", sum);
	printf("Time = %.16f\n", dElapsedTimeDOUBle);
	return 0;
}