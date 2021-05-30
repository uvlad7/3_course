#include <string>
#include <queue>
#include <process.h>
#include <windows.h>
#include <fstream>
#include <regex>
//количество дочерних потоков-потребителей
#define k 1
CRITICAL_SECTION cs;
HANDLE hThreads[k + 1];
// прочитан ли файл?
bool eof = false;
int wordCount = 0;
std::string word;
std::queue<std::string> strings;

unsigned int __stdcall Producer(LPVOID pvParam) {
	std::fstream fin("..\\hamlet.txt");
	if (fin.is_open()) {   //checking whether the file is open
		std::string tp;
		while (getline(fin, tp)) { //read data from file object and put it into string.
			EnterCriticalSection(&cs);
			strings.push(tp);
			LeaveCriticalSection(&cs);
		}
		fin.close(); //close the file object.
	}
	eof = true;
	return 0;
}


unsigned int __stdcall Consumer(LPVOID pvParam) {
	std::string tp;
	std::regex e("\\W+");
	int count;
	do {
		while (!strings.empty()) {
			EnterCriticalSection(&cs);
			if (strings.empty()) { // двойная проверка т. к. состояние очереди могло быть изменено
				LeaveCriticalSection(&cs);
			}
			else {
				tp = strings.front();
				strings.pop();
				LeaveCriticalSection(&cs);
				std::regex_token_iterator<std::string::iterator> i(tp.begin(), tp.end(), e, -1);
				std::regex_token_iterator<std::string::iterator> end;
				count = 0;
				while (i != end) {
					if (word == *i++) {
						count++;
					}
				}
				EnterCriticalSection(&cs);
				wordCount += count;
				LeaveCriticalSection(&cs);
			}
		}
	} while (!eof);
	return 0;
}
int main()
{
	word = "lord";
	LARGE_INTEGER liFrequency, liStartTime, liFinishTime;
	// получаем частоту
	QueryPerformanceFrequency(&liFrequency);
	// получаем стартовое время
	QueryPerformanceCounter(&liStartTime);

	InitializeCriticalSection(&cs);
	hThreads[0] = (HANDLE)_beginthreadex(NULL, 0, Producer, 0, 0, NULL);
	if (hThreads[0] == NULL) {  
		printf("Create Thread %d Error=%d\n", k, GetLastError());
		return -1;
	}
	for (int i = 1; i < k + 1; i++) {
		hThreads[i] = (HANDLE)_beginthreadex(NULL, 0, Consumer, 0, 0, NULL);
		if (hThreads[i] == NULL) {
			printf("Create Thread %d Error=%d\n", k, GetLastError());
			return -1;
		}
	}
	WaitForMultipleObjects(k + 1, hThreads, TRUE, INFINITE);
	for (int i = 0; i < k + 1; i++)
		CloseHandle(hThreads[i]);
	DeleteCriticalSection(&cs);
	QueryPerformanceCounter(&liFinishTime);
	// вычисляет время в миллисекундах
	double dElapsedTimeDOUBle = 1000. * (liFinishTime.QuadPart - liStartTime.QuadPart) / liFrequency.QuadPart;
	printf("Time = %.16f\n", dElapsedTimeDOUBle);
	printf("Number of words = %i\n", wordCount);
	return 0;
}