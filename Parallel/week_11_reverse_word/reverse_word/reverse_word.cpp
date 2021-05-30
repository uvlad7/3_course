#include <fstream>
#include <queue>
#include <tuple>
#include <condition_variable>
#include <thread>
#include <regex>

using namespace std;
using namespace chrono_literals;

queue<string> tokens;
mutex mut;
condition_variable cv;
bool eof{ false };

static void producer(string input) {
	fstream fin(input);
	if (fin.is_open()) {   //checking whether the file is open
		string line;
		queue<string> buffer;
		regex e("([^\\w]+)|(\\w+)");   // matches delimiters or consecutive non-delimiters
		while (getline(fin, line)) { //read data from file object and put it into string.		
			regex_token_iterator<string::iterator> i(line.begin(), line.end(), e, 0);
			regex_token_iterator<string::iterator> end;
			while (i != end) {
				buffer.push(*i++);
			}
			{
				lock_guard<mutex> lk{ mut };
				while (!buffer.empty())
				{
					tokens.push(buffer.front());
					buffer.pop();
				}
				tokens.push("\n");
			}
			cv.notify_all();
		}
		fin.close(); //close the file object.
	}
	{
		lock_guard<mutex> lk{ mut };
		eof = true;
	}
	cv.notify_all();
};

static void consumer(string output) {
	ofstream fout(output);
	if (fout.is_open()) {
		string token;
		regex w("\\w+");
		while (!eof || !tokens.empty()) {
			unique_lock<mutex> l{ mut };
			cv.wait(l, [] { return !tokens.empty() || eof; });
			while (!tokens.empty()) {
				token = tokens.front();
				tokens.pop();
				l.unlock();
				if (regex_match(token, w)) {
					reverse(token.begin(), token.end());
				}
				fout << token;
				l.lock();
			}
		}
	}
}

int main(int argc, char* argv[])
{
	auto start = chrono::high_resolution_clock::now();
	thread prod{ producer, "..\\..\\week_6_1\\hamlet.txt" };
	thread cons{ consumer, "..\\output.txt" };
	prod.join();
	cons.join();
	auto stop = chrono::high_resolution_clock::now();
	chrono::duration<double> diff = stop - start;
	printf("Time = %lld\n", chrono::duration_cast<chrono::milliseconds>(diff).count());
	return 0;
}