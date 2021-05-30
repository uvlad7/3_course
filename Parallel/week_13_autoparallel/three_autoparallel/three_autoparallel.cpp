#include <algorithm>
#include <vector>
#include <execution>
#include <iostream>
#include <thread>
#include <functional>


//// very very BIG algorithm
//std::vector<double> calculate(std::vector<std::pair<int, int>>& points, std::function<bool(int, int)>& has_road)
//{
//	int n = points.size();
//	std::vector<std::vector<double>> matrix(n, std::vector<double>(n));
//	for (int i = 0; i < n; i++) // распараллелим внутренний цикл для лучшей локальности алгоритма
//	{
//		std::for_each(std::execution::seq, matrix[i].begin(), matrix[i].end(),
//			[&matrix, &points, &i, &has_road](double& elem) -> void {
//				int j = &elem - &matrix[i][0];
//				if (i == j)
//					elem = 0.;
//				else
//				{
//					if (has_road(i, j)) {
//						int x = points[i].first - points[j].first;
//						int y = points[i].second - points[j].second;
//						elem = sqrt(x * x + y * y);
//					}
//					else {
//						elem = INT_MAX;
//					}
//				}
//			});
//	}
//	std::vector<double> ansver(n, INT_MAX);
//	ansver[0] = 0.;
//	bool relaxed;
//	do {
//		relaxed = false;
//		for (int w = 0; w < n; w++) {
//			for (int v = 0; v < n; v++) {
//				std::vector<double> distances(n);
//				std::transform(std::execution::seq, ansver.begin(), ansver.end(), distances.begin(),
//					[&ansver, &matrix, &v](const double& ans) -> double {
//						int f = &ans - &ansver[0];
//						return ansver[f] + matrix[f][v];
//					});
//				double min = *(std::min_element(std::execution::seq, distances.begin(), distances.end()));
//				if (min < ansver[v])
//				{
//					ansver[v] = min;
//					relaxed = true;
//				}
//			}
//		}
//	} while (relaxed);
//	return ansver;
//}
//
//std::vector<double> parallel_calculate(std::vector<std::pair<int, int>>& points, std::function<bool(int, int)>& has_road)
//{
//	int n = points.size();
//	std::vector<std::vector<double>> matrix(n, std::vector<double>(n));
//	for (int i = 0; i < n; i++) // распараллелим внутренний цикл для лучшей локальности алгоритма
//	{
//		std::for_each(std::execution::par, matrix[i].begin(), matrix[i].end(),
//			[&matrix, &points, &i, &has_road](double& elem) -> void {
//				int j = &elem - &matrix[i][0];
//				if (i == j)
//					elem = 0.;
//				else
//				{
//					if (has_road(i, j)) {
//						int x = points[i].first - points[j].first;
//						int y = points[i].second - points[j].second;
//						elem = sqrt(x * x + y * y);
//					}
//					else {
//						elem = INT_MAX;
//					}
//				}
//			});
//	}
//	std::vector<double> ansver(n, INT_MAX);
//	ansver[0] = 0.;
//	bool relaxed;
//	do {
//		relaxed = false; for (int w = 0; w < n; w++) {
//			for (int v = 0; v < n; v++) {
//				std::vector<double> distances(n);
//				std::transform(std::execution::par, ansver.begin(), ansver.end(), distances.begin(),
//					[&ansver, &matrix, &v](const double& ans) -> double {
//						int f = &ans - &ansver[0];
//						return ansver[f] + matrix[f][v];
//					});
//				double min = *(std::min_element(std::execution::par, distances.begin(), distances.end()));
//				if (min < ansver[v])
//				{
//					ansver[v] = min;
//					relaxed = true;
//				}
//			}
//		}
//	} while (relaxed);
//	return ansver;
//}

int main()
{
	printf("%u concurrent threads are supported.\n", std::thread::hardware_concurrency());
	std::chrono::high_resolution_clock::time_point start, stop;
	int max;
	// std::function<bool(int, int)> has_road = [](int i, int j) -> bool { return j == i + 1; };
	for (int n : {10'000, 100'000, 1'000'000}) {
		// std::vector<std::pair<int, int>> points(n);
		std::vector<int> random(n), sorted(n);
		for (int i = 0; i < n; i++)
		{
			random[i] = rand() % n;
			// points[i] = std::make_pair(rand() % 10000, rand() % 10000);
		}
		start = std::chrono::high_resolution_clock::now();
		max = *(std::max_element(std::execution::seq, random.begin(), random.end()));
		std::transform(std::execution::seq, random.begin(), random.end(), sorted.begin(), [](const int& el) -> int {return el * el; });
		std::sort(std::execution::seq, sorted.begin(), sorted.end());
		// calculate(points, has_road);
		stop = std::chrono::high_resolution_clock::now();
		printf("Max1 = %d\n", max);
		printf("Max2 = %d\n", sorted[n - 1]);
		printf("Seq time = %lld, n = %d\n", std::chrono::duration_cast<std::chrono::milliseconds>(stop - start).count(), n);
		start = std::chrono::high_resolution_clock::now();
		max = *(std::max_element(std::execution::par, random.begin(), random.end()));
		std::transform(std::execution::par, random.begin(), random.end(), sorted.begin(), [](const int& el) -> int {return el * el; });
		std::sort(std::execution::par, sorted.begin(), sorted.end());
		// parallel_calculate(points, has_road);
		stop = std::chrono::high_resolution_clock::now();
		printf("Max1 = %d\n", max);
		printf("Max2 = %d\n", sorted[n - 1]);
		printf("Par time = %lld, n = %d\n", std::chrono::duration_cast<std::chrono::milliseconds>(stop - start).count(), n);
		printf("\n\n");
	}


}
