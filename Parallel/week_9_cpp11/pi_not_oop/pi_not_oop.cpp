#include <thread>
#include <vector>


#define p 3 // количество дочерних потоков
double pi[p];
int n = 100000000;
void ThreadFunction(int nParam) {
	int i, start;
	double h, sum, x;
	h = 1. / n;  sum = 0.;
	start = nParam;
	for (i = start; i < n; i += p) {
		x = h * i;
		sum += 4. / (1. + x * x);
	}
	pi[nParam] = h * sum;
}

int main()
{
	auto start = std::chrono::high_resolution_clock::now();
	std::vector<std::thread> threads(p);
	int k;
	for (k = 0; k < p; ++k) // создание дочерних потоков
		threads[k] = std::thread(ThreadFunction, k);
	for (k = 0; k < p; ++k) // ожидание завершения дочерних потоков
		threads[k].join();
	double sum = 0.;
	for (k = 0; k < p; ++k) sum += pi[k];
	auto stop = std::chrono::high_resolution_clock::now();
	std::chrono::duration<double> diff = stop - start;
	printf("PI = %.16f\n", sum);
	printf("Time = %lld\n", std::chrono::duration_cast<std::chrono::milliseconds>(diff).count());
	return 0;
}