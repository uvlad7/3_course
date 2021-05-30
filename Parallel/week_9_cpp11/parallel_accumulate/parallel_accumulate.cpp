#include <thread>
#include <vector>
#include <algorithm>
#include <numeric>
#include <functional>

template <typename Iterator, typename T>
struct accumulate_block
{
	void operator()(Iterator first, Iterator last, T& result)
	{
		result = std::accumulate(first, last, result);
	}
};

template <typename Iterator, typename T>
T parallel_accumulate(Iterator first, Iterator last, T init)
{
	unsigned long const length = std::distance(first, last);
	if (!length)
		return init;
	unsigned long const min_per_thread = 25;
	unsigned long const max_threads = (length + min_per_thread - 1) / min_per_thread;
	unsigned long const hardware_threads = std::thread::hardware_concurrency();
	unsigned long const num_threads = std::min(hardware_threads != 0 ? hardware_threads : 2, max_threads);
	// printf("num_threads = %i\n", num_threads);
	unsigned long const block_size = length / num_threads;
	std::vector<T> results(num_threads);
	std::vector<std::thread> threads(num_threads - 1);
	Iterator block_start = first;
	for (unsigned long i = 0; i < (num_threads - 1); ++i)
	{
		Iterator block_end = block_start;
		std::advance(block_end, block_size);
		threads[i] = std::thread(accumulate_block<Iterator, T>(), block_start, block_end, std::ref(results[i]));
		block_start = block_end;
	}
	accumulate_block<Iterator, T>()(block_start, last, results[num_threads - 1]);
	std::for_each(threads.begin(), threads.end(), std::mem_fn(&std::thread::join));
	return std::accumulate(results.begin(), results.end(), init);
}
int main()
{
	int n = 1000000;
	std::vector<int> lst(n);
	for (int i = 0; i < n; i++)
	{
		lst[i] = rand() % 10;
	}
	auto start = std::chrono::high_resolution_clock::now();
	int sum = parallel_accumulate(lst.begin(), lst.end(), 0);
	// int sum = std::accumulate(lst.begin(), lst.end(), 0);
	auto stop = std::chrono::high_resolution_clock::now();
	std::chrono::duration<double> diff = stop - start;
	printf("sum = %i\n", sum);
	printf("Time = %lld\n", std::chrono::duration_cast<std::chrono::milliseconds>(diff).count());
	return 0;
}