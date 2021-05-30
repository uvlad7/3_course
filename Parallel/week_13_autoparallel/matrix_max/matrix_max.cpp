#include <thread>
#include <vector>
#include <algorithm>
#include <numeric>
#include <functional>
#include <future>

template <typename IteratorM, typename IteratorV, typename T>
struct max_block
{
	void operator()(IteratorM first, IteratorM last, IteratorV result)
	{
		std::transform(first, last, result, [](const std::vector<T>& row) -> T
			{
				return *(std::max_element(row.begin(), row.end()));
			});
	}
};

template <typename Iterator, typename T>
T parallel_max(Iterator first, Iterator last, T def)
{
	unsigned long const length = std::distance(first, last);
	if (!length)
		return def;
	unsigned long const min_per_thread = 25;
	unsigned long const max_threads = (length + min_per_thread - 1) / min_per_thread;
	unsigned long const hardware_threads = std::thread::hardware_concurrency();
	unsigned long const num_threads = std::min(hardware_threads != 0 ? hardware_threads : 2, max_threads);
	// printf("num_threads = %i\n", num_threads);
	unsigned long const block_size = length / num_threads;
	std::vector<T> results(length);
	std::vector<std::thread> threads(num_threads - 1);
	Iterator block_start = first;
	auto result_start = results.begin();
	for (unsigned long i = 0; i < (num_threads - 1); ++i)
	{
		Iterator block_end = block_start;
		std::advance(block_end, block_size);
		threads[i] = std::thread(max_block<Iterator, std::vector<T>::iterator, T>(), block_start, block_end, result_start);
		block_start = block_end;
		std::advance(result_start, block_size);
	}
	max_block<Iterator, std::vector<T>::iterator, T>()(block_start, last, result_start);
	std::for_each(threads.begin(), threads.end(), std::mem_fn(&std::thread::join));
	return *(std::max_element(results.begin(), results.end()));
}


template <typename Iterator, typename Func, typename T>
T div_conq_transform(Iterator first, Iterator last, T def, Func f)
{
	unsigned long const length = std::distance(first, last);
	if (!length)
		return def;
	if (length <= 100) {
		std::vector<T> result(length);
		std::transform(first, last, result.begin(), f);
		return f(result);
	}
	Iterator mid = first;
	std::advance(mid, length / 2);
	std::future<T> bgtask = std::async(&div_conq_transform<Iterator, Func, T>, first, mid, def, f);
	try
	{
		T result = div_conq_transform(mid, last, def, f);
		return f({ result, bgtask.get() });
	}
	catch (...)
	{
		bgtask.wait();
		throw;
	}
}

template <typename Iterator, typename T>
T div_conq_max(Iterator first, Iterator last, T def)
{
	std::function<T(const std::vector<T>&)> f = [](const std::vector<T>& row) -> T
	{
		return *(std::max_element(row.begin(), row.end()));
	};
	T res = div_conq_transform(first, last, def, f);
	return res;
}
int main()
{
	std::chrono::high_resolution_clock::time_point start, stop;
	int sum;
	std::chrono::duration<double> diff;
	for (int n : {5'000, 10'000, 15'000})
	{
		printf("Start init\n");
		std::vector<std::vector<int>> matrix(n, std::vector<int>(n));
		for (int i = 0; i < n; i++)
		{
			for (int j = 0; j < n; j++)
			{
				matrix[i][j] = (rand() % 1'000) * (rand() % 1'000);
			}
		}
		printf("End init\n");
		start = std::chrono::high_resolution_clock::now();
		std::vector<int> result(n);
		max_block<std::vector<std::vector<int>>::iterator, std::vector<int>::iterator, int>()(matrix.begin(), matrix.end(), result.begin());
		sum = *(std::max_element(result.begin(), result.end()));
		stop = std::chrono::high_resolution_clock::now();
		printf("max = %i\n", sum);
		diff = stop - start;
		printf("Time = %lld, n = %d\n", std::chrono::duration_cast<std::chrono::milliseconds>(diff).count(), n);
		start = std::chrono::high_resolution_clock::now();
		sum = parallel_max<std::vector<std::vector<int>>::iterator, int>(matrix.begin(), matrix.end(), 0);
		stop = std::chrono::high_resolution_clock::now();
		printf("max = %i\n", sum);
		diff = stop - start;
		printf("Time = %lld, n = %d\n", std::chrono::duration_cast<std::chrono::milliseconds>(diff).count(), n);
		start = std::chrono::high_resolution_clock::now();
		sum = div_conq_max<std::vector<std::vector<int>>::iterator, int>(matrix.begin(), matrix.end(), 0);
		stop = std::chrono::high_resolution_clock::now();
		printf("max = %i\n", sum);
		diff = stop - start;
		printf("Time = %lld, n = %d\n", std::chrono::duration_cast<std::chrono::milliseconds>(diff).count(), n);
		printf("\n\n");
	}
	return 0;
}