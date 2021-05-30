#include <thread>
#include <vector>
#include <algorithm>
#include <numeric>
#include <functional>

template <typename Iterator, typename T>
struct inner_product_block
{
	void operator()(Iterator lfirst, Iterator llast, Iterator rfirst, T& result)
	{
		result = std::inner_product(lfirst, llast, rfirst, result);
	}
};

template <typename Iterator, typename T>
T parallel_inner_product(Iterator lfirst, Iterator llast, Iterator rfirst, T init)
{
	unsigned long const length = std::distance(lfirst, llast);
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
	Iterator lblock_start = lfirst, rblock_start = rfirst;
	for (unsigned long i = 0; i < (num_threads - 1); ++i)
	{
		Iterator lblock_end = lblock_start, rblock_end = rblock_start;
		std::advance(lblock_end, block_size);
		std::advance(rblock_end, block_size);
		threads[i] = std::thread(inner_product_block<Iterator, T>(), lblock_start, lblock_end, rblock_start, std::ref(results[i]));
		lblock_start = lblock_end;
		rblock_start = rblock_end;
	}
	inner_product_block<Iterator, T>()(lblock_start, llast, rblock_start, results[num_threads - 1]);
	std::for_each(threads.begin(), threads.end(), std::mem_fn(&std::thread::join));
	return std::accumulate(results.begin(), results.end(), init);
}
int main()
{
	int n = 1000000;
	std::vector<int> lst(n);
	std::vector<int> rst(n);
	for (int i = 0; i < n; i++)
	{
		lst[i] = rand() % 10;
		rst[i] = rand() % 10;
	}
	auto start = std::chrono::high_resolution_clock::now();
	int sum = parallel_inner_product(lst.begin(), lst.end(), rst.begin(), 0);
	// int sum = std::inner_product(lst.begin(), lst.end(), rst.begin(), 0);
	auto stop = std::chrono::high_resolution_clock::now();
	std::chrono::duration<double> diff = stop - start;
	printf("sum = %i\n", sum);
	printf("Time = %lld\n", std::chrono::duration_cast<std::chrono::milliseconds>(diff).count());
	return 0;
}