#define _CRT_SECURE_NO_WARNINGS
#define HAVE_STRUCT_TIMESPEC
#include <string>
#include <queue>
#include <fstream>
#include <regex>
#include <pthread.h>
#include <windows.h>

//количество дочерних потоков-потребителей
#define k 1
pthread_mutex_t mutex_queue;
pthread_mutex_t mutex_count;
pthread_t threads[k + 1];
// прочитан ли файл?
bool eof = false;
int wordCount = 0;
std::string word;
std::queue<std::string> strings;

void* producer(void* t)
{
	std::fstream fin("..\\..\\week_6_1\\hamlet.txt");
	if (fin.is_open()) {   //checking whether the file is open
		std::string tp;
		while (getline(fin, tp)) { //read data from file object and put it into string.
			pthread_mutex_lock(&mutex_queue);
			strings.push(tp);
			pthread_mutex_unlock(&mutex_queue);
		}
		fin.close(); //close the file object.
	}
	eof = true;
	return 0;
}
void* consumer(void* t)
{
	std::string tp;
	std::regex e("\\W+");
	int count;
	do {
		while (!strings.empty()) {
			pthread_mutex_lock(&mutex_queue);
			if (strings.empty()) { // двойная проверка т. к. состояние очереди могло быть изменено
				pthread_mutex_unlock(&mutex_queue);
			}
			else {
				tp = strings.front();
				strings.pop();
				pthread_mutex_unlock(&mutex_queue);
				std::regex_token_iterator<std::string::iterator> i(tp.begin(), tp.end(), e, -1);
				std::regex_token_iterator<std::string::iterator> end;
				count = 0;
				while (i != end) {
					if (word == *i++) {
						count++;
					}
				}
				pthread_mutex_lock(&mutex_count);
				wordCount += count;
				pthread_mutex_unlock(&mutex_count);
			}
		}
	} while (!eof);
	return 0;
}


int main(int argc, char* argv[])
{
	word = "lord";
	//time_t startTime, finishTime;
	//startTime = time(NULL); // получаем стартовое время
	LARGE_INTEGER liFrequency, liStartTime, liFinishTime;
	// получаем частоту
	QueryPerformanceFrequency(&liFrequency);
	// получаем стартовое время
	QueryPerformanceCounter(&liStartTime);

	pthread_attr_t attr;
	pthread_mutex_init(&mutex_queue, NULL);
	pthread_mutex_init(&mutex_count, NULL);
	pthread_attr_init(&attr);
	pthread_attr_setdetachstate(&attr, PTHREAD_CREATE_JOINABLE);
	int rc;
	rc = pthread_create(&threads[0], &attr, producer, NULL);
	if (rc) // обработка ошибки
	{
		printf("ERROR: return code from pthread_create() %d\n", rc); return -1;
	}
	for (int i = 1; i < k + 1; i++) {
		rc = pthread_create(&threads[i], &attr, consumer, NULL);
		if (rc) // обработка ошибки
		{
			printf("ERROR: return code from pthread_create() %d\n", rc); return -1;
		}
	}
	for (int i = 0; i < k + 1; i++) {
		pthread_join(threads[i], NULL);
	}
	pthread_attr_destroy(&attr);
	pthread_mutex_destroy(&mutex_queue);
	pthread_mutex_destroy(&mutex_count);

	//finishTime = time(NULL); // получаем финишное время
	//// вычисляет время в миллисекундах
	//double dElapsedTimeDOUBle = difftime(finishTime, startTime);
	QueryPerformanceCounter(&liFinishTime);
	// вычисляет время в миллисекундах
	double dElapsedTimeDOUBle = 1000. * (liFinishTime.QuadPart - liStartTime.QuadPart) / liFrequency.QuadPart;
	printf("Time = %.16f\n", dElapsedTimeDOUBle);
	printf("Number of words = %i\n", wordCount);
	return 0;
}