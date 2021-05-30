#include <fstream>
#include <queue>
#include <thread>
#include <regex>

using namespace std;
using namespace chrono_literals;

queue<string> lines;

static void producer(string input) {
	fstream fin(input);
	if (fin.is_open()) {   //checking whether the file is open
		string line;
		while (getline(fin, line)) { //read data from file object and put it into string.		
			lines.push(line);
		}
		fin.close(); //close the file object.
	}
};

static void consumer(string output) {
	ofstream fout(output);
	if (fout.is_open()) {
		string line;
		string token;
		regex e("([^\\w]+)|(\\w+)");   // matches delimiters or consecutive non-delimiters
		regex w("\\w+");
		while (!lines.empty()) {
			line = lines.front();
			lines.pop();
			regex_token_iterator<string::iterator> i(line.begin(), line.end(), e, 0);
			regex_token_iterator<string::iterator> end;
			while (i != end) {
				token = *i++;
				if (regex_match(token, w)) {
					reverse(token.begin(), token.end());
				}
				fout << token;
			}
			fout << "\n";
		}
	}
}

int main(int argc, char* argv[])
{
	auto start = chrono::high_resolution_clock::now();
	producer("..\\..\\week_6_1\\the_goblet_of_fire.txt");
	consumer("..\\output.txt");
	auto stop = chrono::high_resolution_clock::now();
	chrono::duration<double> diff = stop - start;
	printf("Time = %lld\n", chrono::duration_cast<chrono::milliseconds>(diff).count());
	return 0;
}