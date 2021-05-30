// ConsoleApplication10.cpp : This file contains the 'main' function. Program execution begins and ends there.
//

#include <iostream>
#include <list>
#include <mutex>
#include <string>

std::list<int> some_list = { 7, 5, 16, 8 };
std::mutex m;
std::mutex m2;


void add_to_list(int new_value) { 
    std::lock_guard<std::mutex> lk(m);
    some_list.push_back(new_value);
}

bool list_contains(int value_to_find) {
    std::lock_guard<std::mutex> lk(m);
    return (std::find(some_list.begin(), some_list.end(), value_to_find) != some_list.end());
}

template<typename T>
void my_own_cout(const T& out) {
    std::lock_guard<std::mutex> lk(m2);
    std::cout << out;
}

int main()
{
    std::thread th1(my_own_cout<std::string>, "Hello world 1\n");
    my_own_cout<std::string>("Hello world\n");
    std::thread th2(my_own_cout<std::string>, "Hello world 2\n");
    std::thread th3(my_own_cout<std::string>, "Hello world 3\n");
    th1.join();
    th2.join();
    th3.join();
    getchar();
    return 0;
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
