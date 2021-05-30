#pragma once

#include <iterator>
#include <stdexcept>
#include <cassert>
#include <algorithm>
#include <tuple>
#include <sstream>
#include <vector>

template<typename T>
class Stack;

template<typename T>
class Visitor {
public:
	virtual T visit(const Stack<T>* model) const = 0;
};

template<typename T>
class Observer {
public:
	virtual ~Observer() {}
	virtual void update(Stack<T>* theChangedSubject) = 0;
};

template<typename T>
class GetFirstVisitor : public Visitor<T> {
public:
	T visit(const Stack<T>* model) const {
		if (model->IsEmpty()) {
			throw std::out_of_range("Stack is empty!");
		}
		return model->Get(0);
	}
};

template<typename T>
class GetLastVisitor : public Visitor<T> {
public:
	T visit(const Stack<T>* model) const {
		if (model->IsEmpty()) {
			throw std::out_of_range("Stack is empty!");
		}

		T result = model->Get(model->Size() - 1);

		return result;
	}
};


template <typename T>
class MaxVisitor : public Visitor<T> {
public:
	T visit(const Stack<T>* q) const {
		if (q->Size() == 0) throw std::out_of_range("Stack is empty!");
		auto iter = q->begin();
		T max = *iter;
		for (; iter != q->end(); ++iter) {
			if (*iter > max) {
				max = *iter;
			}
		}

		return max;
	}
};

template <typename T>
class MinVisitor : public Visitor<T> {
public:
	T visit(const Stack<T>* q) const {
		if (q->Size() == 0) throw std::out_of_range("Stack is empty!");
		auto iter = q->begin();
		T min = *iter;
		for (; iter != q->end(); ++iter) {
			if (*iter < min) {
				min = *iter;
			}
		}
		return min;
	}
};

template<typename T>
class StackIterator : public std::iterator<std::bidirectional_iterator_tag, T> {
public:
	const T operator*() const {
		if (index_ >= model_->Size()) {
			throw std::out_of_range("Can't dereference end iterator!");
		}
		return model_->Get(index_);
	}

	const T* operator->() const {
		if (index_ >= model_->Size()) {
			throw std::out_of_range("Can't access end iterator!");
		}
		return &model_->Get(index_);
	}

	StackIterator& operator++() {
		if (index_ >= model_->Size()) {
			throw std::out_of_range("Can't move further than end iterator!");
		}
		++index_;
		return *this;
	}

	const StackIterator operator++(int) {
		StackIterator result = *this;
		++(*this);
		return result;
	}

	StackIterator& operator--() {
		if (index_ == 0) {
			throw std::out_of_range("Can't move earlier begin iterator!");
		}
		StackIterator result = *this;
		--(*this);
		return result;
	}

	const StackIterator operator--(int) {
		StackIterator result;
		--(*this);
		return result;
	}

	bool operator==(const StackIterator& other) const {
		return std::tie(model_, index_) == std::tie(other.model_, other.index_);
	}

	bool operator!=(const StackIterator& other) const {
		return !(*this == other);
	}

private:
	friend class Stack<T>;

	const Stack<T>* const model_;
	size_t index_;

	StackIterator(const Stack<T>* const model, size_t index)
		: model_(model), index_(index) {}
};

template<typename T>
class Stack {
public:
	Stack() : size_(0), allocated_size_(1) {
		elements_ = new T[1];
		observers = new std::vector<Observer<T>*>;
	}

	Stack(const Stack<T>& other) : size_(other.size_), allocated_size_(other.allocated_size_) {
		elements_ = new T[allocated_size_];
		for (int i = 0; i < size_; ++i) {
			elements_[i] = other.elements_[i];
		}
		observers = new std::vector<Observer<T>*>;
	}

	Stack(Stack<T>&& other) : size_(other.size_), allocated_size_(other.allocated_size_) {
		elements_ = other.elements_;
		observers = other.observers;
	}

	Stack(const std::initializer_list<T>& elements) : size_(elements.size()), allocated_size_(elements.size()) {
		elements_ = new T[allocated_size_];
		for (int i = 0; i < size_; ++i) {
			elements_[i] = *(elements.begin() + i);
		}
		observers = new std::vector<Observer<T>*>;
	}

	void Push(const T& element) {
		if (size_ >= allocated_size_) {
			allocated_size_ *= 2;
		}

		T* new_array = new T[allocated_size_];
		new_array[0] = element;

		for (int i = 0; i < size_; ++i) {
			new_array[i + 1] = elements_[i];
		}

		++size_;
		delete[] elements_;
		elements_ = new_array;
		notify();
	}

	T Get(size_t index) const {
		if (index >= size_) {
			throw std::out_of_range("Index out of bounds!");
		}
		return elements_[index];
	}

	void Pop() {
		T* new_array = new T[allocated_size_];
		--size_;
		for (int i = 0; i < size_; ++i) {
			new_array[i] = elements_[i + 1];
		}
		delete[] elements_;
		elements_ = new_array;
		notify();
	}

	StackIterator<T> begin() const {
		return StackIterator<T>(this, 0);
	}

	StackIterator<T> end() const {
		return StackIterator<T>(this, size_);
	}

	size_t Size() const {
		return size_;
	}

	bool IsEmpty() const {
		return (size_ == 0);
	}

	void Clear() {
		delete[] elements_;
		elements_ = new T[1];
		size_ = 0;
		allocated_size_ = 1;
		notify();
	}

	T Accept(const Visitor<T>& visitor) {
		return visitor.visit(this);
	}

	T Top() const {
		if (IsEmpty()) {
			throw std::out_of_range("Stack is empty!");
		}
		return elements_[0];
	}

	T Bottom() const {
		if (IsEmpty()) {
			throw std::out_of_range("Stack is empty!");
		}
		return elements_[size_ - 1];
	}

	void attach(Observer<T>* o)
	{
		observers->push_back(o);
	}

	void detach(Observer<T>* o)
	{
		observers->erase(std::remove(observers->begin(), observers->end(), o), observers->end());
	}

private:
	size_t size_;
	size_t allocated_size_;
	T* elements_;
	std::vector<Observer<T>*>* observers;

	void notify() {
		if (!observers->empty()) {
			for (auto i = observers->begin(); i != observers->end(); advance(i, 1)) {
				(*i)->update(this);
			}
		}
	}
};
