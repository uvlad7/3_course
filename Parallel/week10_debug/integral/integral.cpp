#include <thread>
#include <vector>
#include <algorithm>
#include <numeric>
#include <functional>

struct integrate_block
{
	void operator()(double a, double b, std::function<double(double)>& f, double& result, long const n, const int p, const int start)
	{
		double h = (b - a) / n;
		double sum = 0.;
		double x = a + p * h;
		for (long i = start; i < n; i += p) {
			sum += f(x);
			x += p * h;
		}
		result = h * sum;
	}
};

double parallel_integrate(double a, double b, std::function<double(double)>& f, long const n, int const p)
{
	if (a == b)
		return 0;
	if (a > b)
		std::swap(a, b);
	std::vector<double> results(p);
	std::vector<std::thread> threads(p - 1);
	for (int i = 0; i < (p - 1); ++i)
	{
		threads[i] = std::thread(integrate_block(), a, b, std::ref(f), std::ref(results[i]), n, p, i);
	}
	integrate_block()(a, b, f, results[p - 1], n, p, (p - 1));
	std::for_each(threads.begin(), threads.end(), std::mem_fn(&std::thread::join));
	return std::accumulate(results.begin(), results.end(), 0.);
}

double single_integrate(double a, double b, std::function<double(double)>& f, long const n)
{
	if (a == b)
		return 0;
	if (a > b)
		std::swap(a, b);
	double h = (b - a) / n;
	double sum = 0.;
	double x = a + h;
	for (long i = 0; i < n; i++) {
		sum += f(x);
		x += h;
	}
	return h * sum;
}

int main()
{
	std::function<double(double)> functions[] = {
		[](double x) -> double { return sin(x * x); },
		[](double x) -> double { return sin(x) / x; },
		[](double x) -> double { return exp(x * x); }
	};
	long dimension[] = { 100000, 1000000, 10000000, 100000000 };
	int ps[] = { 1, 2, 4 };
	std::chrono::high_resolution_clock::time_point start, stop;
	for (auto f: functions)
	{
		for (auto p : ps)
		{
			for (auto n : dimension)
			{
				start = std::chrono::high_resolution_clock::now();
				double sum = (p == 1 ? single_integrate(0, 1, f, n) : parallel_integrate(0, 1, f, n, p));
				stop = std::chrono::high_resolution_clock::now();
				printf("I = %.16f\n", sum);
				printf("Time = %lld\n", std::chrono::duration_cast<std::chrono::milliseconds>(stop - start).count());
			}
			printf("\n");
		}
		printf("\n");
	}
	return 0;
}