#pragma once
#include <string>
#include "stdafx.h"

using namespace std;

class Complex
{
private:
	int re;
	int im;
public:
	Complex(int re, int im);
	Complex();
	bool operator > (const Complex& right)const;
	bool operator < (const Complex& right)const;
	friend string to_string(const Complex& right) { return (to_string(right.re) + (right.im < 0 ? "" : "+") + to_string(right.im) + "i"); }
	~Complex() {};
};
