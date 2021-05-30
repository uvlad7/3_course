// Cp3.cpp : This file contains the 'main' function. Program execution begins and ends there.
//

#define _USE_MATH_DEFINES

#include <iostream>
#include <cmath>
#include <random>
#include <cassert>

using namespace std;

int main()
{
	random_device rd;
	mt19937_64 gen(rd());
	uniform_real_distribution<double> r(-M_PI, M_PI);
	double x = r(gen), e;
	cout << "epsilon: ";
	cin >> e;
	assert(e > 0);
	cout << "x: " << x << endl;
	double elem = x, ans = 0;
	for (int i = 1; abs(elem) > e; i++)
	{
		cout << elem << endl;
		ans += elem;
		elem *= x * x / (-2 * i * (2 * i + 1));
	}
	cout << "sin(x): " << ans << endl;
	cout << "stdlib sin(x): " << sin(x) << endl;
}

// Run program: Ctrl + F5 or Debug > Start Without Debugging menu
// Debug program: F5 or Debug > Start Debugging menu

// Tips for Getting Started: 
//   1. Use the Solution Explorer window to add/manage files
//   2. Use the Team Explorer window to connect to source control
//   3. Use the Output window to see build output and other messages
//   4. Use the Error List window to view errors
//   5. Go to Project > Add New Item to create new code files, or Project > Add Existing Item to add existing code files to the project
//   6. In the future, to open this project again, go to File > Open > Project and select the .sln file
