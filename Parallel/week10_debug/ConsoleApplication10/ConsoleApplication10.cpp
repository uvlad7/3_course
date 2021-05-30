// ConsoleApplication10.cpp : This file contains the 'main' function. Program execution begins and ends there.
//

#include <list>
#include <string>
#include <iostream>
#include <thread>
#include <mutex>
#include <sstream>
#include <vector>
using namespace std;

std::list<int> some_list = { 7, 5, 16, 8 };
std::mutex m;


void add_to_list(int new_value) { 
    std::lock_guard<std::mutex> lk(m);
    some_list.push_back(new_value);
}

bool list_contains(int value_to_find) {
    std::lock_guard<std::mutex> lk(m);
    return (std::find(some_list.begin(), some_list.end(), value_to_find) != some_list.end());
}

struct pcout : public stringstream {
    static inline mutex cout_mutex;
    ~pcout() {
        lock_guard<mutex> l{ cout_mutex };
        cout << rdbuf();
        cout.flush();
    }
};

static void my_own_cout(int id)
{
    pcout{} << "Hello world " << id << '\n';
}

static void main_cout()
{
    pcout{} << "Hello world\n";
}

int main()
{
    std::thread th1(my_own_cout, 1);
    std::thread th2(my_own_cout, 2);
    std::thread th3(my_own_cout, 3);
    main_cout();
    th1.join();
    th2.join();
    th3.join();
    return 0;
}
