#include <windows.h>
#include "resource.h"
#include <iostream>
#include <fstream>
#include <list>
#include <iterator>
#include <string>
#include "Complex.h"

using namespace std;

class KList;

class Iterator
{
public:
	virtual ~Iterator() {}
	virtual void first() = 0;
	virtual void next() = 0;
	virtual bool isDone() const = 0;
	virtual Complex& currentItem() const = 0;
};

class KListIterator;

class Visitor
{
public:
	virtual ~Visitor() {}
	virtual void visitList(KList*) = 0;
};

class ToStrVisitor;

class MinVisitor;

class Observer
{
public:
	virtual ~Observer() {}
	virtual void update(KList* theChangedSubject) = 0;
};

class Viewer;

class Controller;

class KList
{
private:
	Complex *items = nullptr;
	int first;
	int size;
	int capacity;
	list<Observer*> *observers;
public:
	KList(int length);
	KList(const KList &cList);
	~KList() { delete[] items; }
	bool empty() const { return (size == 0) ? true : false; }
	int length() const { return size; }
	Complex popFront();
	Complex popBack();
	Complex& front() const { return items[first]; }
	Complex& back() const { return items[(first + size) % capacity]; }
	bool pushFront(const Complex&newElement);
	bool pushBack(const Complex&newElement);

	friend class KListIterator;
	KListIterator createIterator() const;

	void accept(Visitor& v);

	void attach(Observer *o);
	void detach(Observer *o);
	void notify();
};


class KListIterator : public Iterator
{
private:
	const KList *list;
	int index;
public:
	KListIterator(const KList* aList) { list = aList; }
	void first() override { index = 0; }
	void next() override { index++; }
	bool isDone() const override { return (index == list->size) ? true : false; }
	Complex& currentItem() const override;
};

class ToStrVisitor : public Visitor
{
private:
	string str;
public:
	string toStr() const { return str; }
	void visitList(KList *list) override;
};

class MinVisitor : public Visitor
{
private:
	string str;
public:
	string minEl() const { return str; }
	void visitList(KList *list) override;
};

class MaxVisitor : public Visitor
{
private:
	string str;
public:
	string maxEl() const { return str; }
	void visitList(KList *list) override;
};

class Viewer : public Observer
{
private:
	KList *model;
	Controller *controller;
	HWND inEdit1;
	HWND inEdit2;
	HWND outEdit;
	HWND popFrontButton;
	HWND popBackButton;
	HWND pushButton;
	void show();
public:
	Viewer(KList *m, Controller *c, HWND ie1, HWND ie2, HWND oe, HWND pfb, HWND pbb, HWND pb);
	~Viewer() { model->detach(this); }
	void update(KList* theChangedSubject) override;
	void enable();
	void disable();
	void push();
	void popFront();
	void popBack();
};


class Controller
{
private:
	KList *model;
	Viewer *view;
public:
	Controller(KList *m, HWND ie1, HWND ie2, HWND oe, HWND pfb, HWND pbb, HWND pb);
	~Controller() { delete view; }
	void popFront();
	void popBack();
	void push(Complex el);
	void translate(short param);
};