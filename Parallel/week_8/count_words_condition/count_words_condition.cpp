#define _CRT_SECURE_NO_WARNINGS
#define HAVE_STRUCT_TIMESPEC
#include <string>
#include <queue>
#include <fstream>
#include <regex>
#include <pthread.h>
#include <windows.h>

//���������� �������� �������-������������
#define k 3
pthread_mutex_t mutex_queue;
pthread_cond_t count_threshold_cv;
pthread_t threads[k + 1];
// �������� �� ����?
bool eof = false;
int wordCount = 0;
std::string word;
std::queue<std::string> strings;

void* producer(void* t)
{
	std::fstream fin("..\\..\\week_6_1\\the_goblet_of_fire.txt");
	if (fin.is_open()) {   //checking whether the file is open
		std::string tp;
		while (getline(fin, tp)) { //read data from file object and put it into string.
			pthread_mutex_lock(&mutex_queue);
			strings.push(tp);
			pthread_cond_signal(&count_threshold_cv);
			pthread_mutex_unlock(&mutex_queue);
		}
		fin.close(); //close the file object.
	}
	pthread_mutex_lock(&mutex_queue);
	eof = true;
	pthread_cond_signal(&count_threshold_cv);
	pthread_mutex_unlock(&mutex_queue);
	return 0;
}
void* consumer(void* t)
{
	std::string tp;
	std::regex e("\\W+");
	int count;
	pthread_mutex_lock(&mutex_queue);
	do {
		while (strings.empty() && !eof) {
			pthread_cond_wait(&count_threshold_cv, &mutex_queue);
		}
		if (!strings.empty())
		{
			do {
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
				pthread_mutex_lock(&mutex_queue);
				wordCount += count;
			} while (!strings.empty());
		}
	} while (!eof);
	pthread_mutex_unlock(&mutex_queue);
	return 0;
}

int main(int argc, char* argv[])
{
	word = "Lord";
	//time_t startTime, finishTime;
	//startTime = time(NULL); // �������� ��������� �����
	LARGE_INTEGER liFrequency, liStartTime, liFinishTime;
	// �������� �������
	QueryPerformanceFrequency(&liFrequency);
	// �������� ��������� �����
	QueryPerformanceCounter(&liStartTime);

	pthread_attr_t attr;
	pthread_mutex_init(&mutex_queue, NULL);
	pthread_cond_init(&count_threshold_cv, NULL);
	pthread_attr_init(&attr);
	pthread_attr_setdetachstate(&attr, PTHREAD_CREATE_JOINABLE);
	int rc;
	rc = pthread_create(&threads[0], &attr, producer, NULL);
	if (rc) // ��������� ������
	{
		printf("ERROR: return code from pthread_create() %d\n", rc); return -1;
	}
	for (int i = 1; i < k + 1; i++) {
		rc = pthread_create(&threads[i], &attr, consumer, NULL);
		if (rc) // ��������� ������
		{
			printf("ERROR: return code from pthread_create() %d\n", rc); return -1;
		}
	}
	for (int i = 0; i < k + 1; i++) {
		pthread_join(threads[i], NULL);
	}
	pthread_attr_destroy(&attr);
	pthread_mutex_destroy(&mutex_queue);
	pthread_cond_destroy(&count_threshold_cv);

	//finishTime = time(NULL); // �������� �������� �����
	//// ��������� ����� � �������������
	//double dElapsedTimeDOUBle = difftime(finishTime, startTime);
	QueryPerformanceCounter(&liFinishTime);
	// ��������� ����� � �������������
	double dElapsedTimeDOUBle = 1000. * (liFinishTime.QuadPart - liStartTime.QuadPart) / liFrequency.QuadPart;
	printf("Time = %.16f\n", dElapsedTimeDOUBle);
	printf("Number of words = %i\n", wordCount);
	return 0;
}