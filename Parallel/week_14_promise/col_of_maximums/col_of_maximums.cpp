#include <thread>
#include <vector>
#include <algorithm>
#include <numeric>
#include <functional>
#include <future>

template <typename Iterator, typename T>
struct max_count_block
{
	void operator()(Iterator first, Iterator last, std::pair<T, int>& result)
	{
		T max= (*first)[0];
		int count = 0;
		std::for_each(first, last, [&max, &count](const std::vector<T>& row) -> void {
			std::for_each(row.begin(), row.end(), [&max, &count](const T& elem) -> void {
				if (elem > max)
				{
					max = elem;
					count = 1;
				}
				else if (elem == max)
				{
					count++;
				}
			});
		});
		result = std::make_pair(max, count);
	}
};

template <typename Iterator, typename T>
std::pair<T, int> max_count_future(Iterator first, Iterator last)
{
	T max = (*first)[0];
	int count = 0;
	std::for_each(first, last, [&max, &count](const std::vector<T>& row) -> void {
		std::for_each(row.begin(), row.end(), [&max, &count](const T& elem) -> void {
			if (elem > max)
			{
				max = elem;
				count = 1;
			}
			else if (elem == max)
			{
				count++;
			}
			});
		});
	return std::make_pair(max, count);
}

template <typename Iterator, typename T>
int parallel_max_count(Iterator first, Iterator last)
{
	unsigned long const length = std::distance(first, last);
	if (!length)
		return 0;
	unsigned long const min_per_thread = 25;
	unsigned long const max_threads = (length + min_per_thread - 1) / min_per_thread;
	unsigned long const hardware_threads = std::thread::hardware_concurrency();
	unsigned long const num_threads = std::min(hardware_threads != 0 ? hardware_threads : 2, max_threads);
	unsigned long const block_size = length / num_threads;
	std::vector<std::pair<T, int>> results(num_threads);
	std::vector<std::thread> threads(num_threads - 1);
	Iterator block_start = first;
	for (unsigned long i = 0; i < (num_threads - 1); ++i)
	{
		Iterator block_end = block_start;
		std::advance(block_end, block_size);
		threads[i] = std::thread(max_count_block<Iterator, T>(), block_start, block_end, std::ref(results[i]));
		block_start = block_end;
	}
	max_count_block<Iterator, T>()(block_start, last, results[num_threads - 1]);
	std::for_each(threads.begin(), threads.end(), std::mem_fn(&std::thread::join));
	T max = results[0].first;
	int count = 0;
	std::for_each(results.begin(), results.end(), [&max, &count](const std::pair<T, int>& elem) -> void {
		if (elem.first > max)
		{
			max = elem.first;
			count = elem.second;
		}
		else if (elem.first == max)
		{
			count += elem.second;
		}
	});
	return count;
}

template <typename Iterator, typename T>
int promise_max_count(Iterator first, Iterator last)
{
	using Task_type = std::pair<T, int>(Iterator, Iterator);
	unsigned long const length = std::distance(first, last);
	if (!length)
		return 0;
	unsigned long const min_per_thread = 25;
	unsigned long const max_threads = (length + min_per_thread - 1) / min_per_thread;
	unsigned long const hardware_threads = std::thread::hardware_concurrency();
	unsigned long const num_threads = std::min(hardware_threads != 0 ? hardware_threads : 2, max_threads);
	unsigned long const block_size = length / num_threads;
	std::vector<std::future<std::pair<T, int>>> results(num_threads);
	std::vector<std::thread> threads(num_threads);
	Iterator block_start = first;
	for (unsigned long i = 0; i < (num_threads - 1); ++i)
	{
		std::packaged_task<Task_type> pt{ max_count_future<Iterator, T> };
		results[i] = pt.get_future();
		Iterator block_end = block_start;
		std::advance(block_end, block_size);
		threads[i] = std::thread(move(pt), block_start, block_end);
		block_start = block_end;
	}
	std::packaged_task<Task_type> pt{ max_count_future<Iterator, T> };
	results[num_threads - 1] = pt.get_future();
	threads[num_threads - 1] = std::thread(move(pt), block_start, last);
	std::for_each(threads.begin(), threads.end(), std::mem_fn(&std::thread::join));
	std::pair<T, int> res = results[0].get();
	T max = res.first;
	int count = res.second;
	std::for_each(std::next(results.begin()), results.end(), [&max, &count](std::future<std::pair<T, int>>& elem) -> void {
		std::pair<T, int> el = elem.get();
		if (el.first > max)
		{
			max = el.first;
			count = el.second;
		}
		else if (el.first == max)
		{
			count += el.second;
		}
		});
	return count;
}

template <typename Iterator, typename T>
int max_count(Iterator first, Iterator last)
{
	unsigned long const length = std::distance(first, last);
	if (!length)
		return 0;
	std::pair<T, int> result;
	max_count_block<Iterator, T>()(first, last, result);
	return result.second;
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
		sum = max_count<std::vector<std::vector<int>>::iterator, int>(matrix.begin(), matrix.end());
		stop = std::chrono::high_resolution_clock::now();
		diff = stop - start;
		printf("max count = %i\n", sum);
		printf("Time single = %lld, n = %d\n", std::chrono::duration_cast<std::chrono::milliseconds>(diff).count(), n);
		start = std:: chrono::high_resolution_clock::now();
		sum = parallel_max_count<std::vector<std::vector<int>>::iterator, int>(matrix.begin(), matrix.end());
		stop = std::chrono::high_resolution_clock::now();
		diff = stop - start;
		printf("max count = %i\n", sum);
		printf("Time parallel = %lld, n = %d\n", std::chrono::duration_cast<std::chrono::milliseconds>(diff).count(), n);
		start = std::chrono::high_resolution_clock::now();
		sum = promise_max_count<std::vector<std::vector<int>>::iterator, int>(matrix.begin(), matrix.end());
		stop = std::chrono::high_resolution_clock::now();
		diff = stop - start;
		printf("max count = %i\n", sum);
		printf("Time promise = %lld, n = %d\n", std::chrono::duration_cast<std::chrono::milliseconds>(diff).count(), n);
		printf("\n\n");
	}
	return 0;
}