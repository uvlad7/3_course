#include <thread>
#include <vector>

class ThreadClass
{
private: 
	int n;
	int p;
	double h;
public:
	ThreadClass(int _n, int _p) : n(_n), p(_p) {
		h = 1. / n;
	}
	void operator()(int start, double *pi)
	{
		double sum = 0.;
		double x;
		for (int i = start; i < n; i += p) {
			x = h * i;
			sum += 4. / (1. + x * x);
		}
		pi[start] = h * sum;
	}
};

class Counter
{
public:
	Counter() {};
	double countPi(int n, int p) {
		double* pi = new double[p];
		std::vector<std::thread> threads(p);
		ThreadClass t(n, p);
		for (int i = 0; i < p; i++) {
			threads[i] = std::thread(t, i, pi);
		}
		double sum = 0.;
		for (int i = 0; i < p; i++) {
			threads[i].join();
		}
		for (int i = 0; i < p; i++) {
			sum += pi[i];
		}
		delete[] pi;
		return sum;
	}
};

int main()
{
	auto start = std::chrono::high_resolution_clock::now();
	double pi = Counter().countPi(100000000, 4);
	auto stop = std::chrono::high_resolution_clock::now();
	std::chrono::duration<double> diff = stop - start;
	printf("PI = %.16f\n", pi);
	printf("Time = %lld\n", std::chrono::duration_cast<std::chrono::milliseconds>(diff).count());
	return 0;
}