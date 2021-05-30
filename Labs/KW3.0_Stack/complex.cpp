#define _CRT_SECURE_NO_WARNINGS
#include "stdafx.h"

#include "complex.h"
#include <assert.h>
#include <ctime>

Complex::Complex(int r, int i)
{
	re = r;
	im = i;
}

Complex::Complex()
{
	re = 0;
	im = 0;
}

bool Complex::operator>(const Complex& c) const
{
	return (re * re * im * im > c.re * c.re * c.im * c.im) ? true : false;
}

bool Complex::operator<(const Complex& c) const
{
	return (re * re * im * im < c.re * c.re * c.im * c.im) ? true : false;
}