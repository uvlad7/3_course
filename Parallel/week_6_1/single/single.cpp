#include <string>
#include <windows.h>
#include <fstream>
#include <regex>


int main()
{
	int wordCount = 0;
	std::string word = "Lord";
	LARGE_INTEGER liFrequency, liStartTime, liFinishTime;
	// получаем частоту
	QueryPerformanceFrequency(&liFrequency);
	// получаем стартовое время
	QueryPerformanceCounter(&liStartTime);

	std::regex e("\\W+");
	int count;
	std::fstream fin("..\\the_goblet_of_fire.txt");
	if (fin.is_open()) {   //checking whether the file is open
		std::string tp;
		while (getline(fin, tp)) { //read data from file object and put it into string.
			std::regex_token_iterator<std::string::iterator> i(tp.begin(), tp.end(), e, -1);
			std::regex_token_iterator<std::string::iterator> end;
			count = 0;
			while (i != end) {
				if (word == *i++)
					count++;
			}
			wordCount += count;
		}
		fin.close(); //close the file object.
	}

	QueryPerformanceCounter(&liFinishTime);
	// вычисляет время в миллисекундах
	double dElapsedTimeDOUBle = 1000. * (liFinishTime.QuadPart - liStartTime.QuadPart) / liFrequency.QuadPart;
	printf("Time = %.16f\n", dElapsedTimeDOUBle);
	printf("Number of words = %i\n", wordCount);
	return 0;
}