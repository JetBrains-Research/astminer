#include <algorithm>
#include <cmath>
#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <iomanip>
#include <iostream>
#include <map>
#include <queue>
#include <set>
#include <sstream>
#include <string>
#include <vector>
#include <list>
#include <cassert>
#include <queue>

using namespace std;

#define clr(x) memset((x), 0, sizeof(x))
#define all(x) (x).begin(), (x).end()
#define pb push_back
#define mp make_pair
#define sz size()
#define For(i, st, en) for(int i=(st); i<=(int)(en); i++)
#define Ford(i, st, en) for(int i=(st); i>=(int)(en); i--)
#define forn(i, n) for(int i=0; i<(int)(n); i++)
#define ford(i, n) for(int i=(n)-1; i>=0; i--)
#define fori(it, x) for (__typeof((x).begin()) it = (x).begin(); it != (x).end(); it++)

template <class _T> inline _T sqr(const _T& x) { return x * x; }
template <class _T> inline _T ABS(const _T& x) { return (x<0)?-x:x;}
template <class _T> inline string tostr(const _T& a) { ostringstream os(""); os << a; return os.str(); }
template <class _T> inline istream& operator << (istream& is, const _T& a) { is.putback(a); return is; }
template <class _T> inline _T gcd(const _T &a, const _T &b) {
    _T t;

    while (!(b == 0)) {
        t = a % b;
        a = b;
        b = t;
    }

    return a;
}

typedef long double ld;

// Constants
const ld PI = 3.1415926535897932384626433832795;
const ld EPS = 1e-11;

// Types
typedef unsigned uns;
typedef signed   long long i64;
typedef unsigned long long u64;
typedef set < int > SI;
typedef vector < ld > VD;
typedef vector < int > VI;
typedef vector < bool > VB;
typedef vector < string > VS;
typedef pair < int, int > PII;
typedef map < string, int > MSI;
typedef map < string, void * > MSV;

// DEBUG
//#define DEBUG
#ifdef DEBUG
static bool const _debug_ = true;
#else
static bool const _debug_ = false;
#endif
#define DOUT(MSG) (_debug_ && cerr << (MSG))
#define DLOUT(MSG) (_debug_ && cerr << (MSG) << endl)


// ########## UTILITIES ##########//
inline uns getUnsigned() {
    uns curr;
    scanf("%u", &curr);
    return curr;
}

inline void getUnsigned(uns &one, uns &two) {
    scanf("%u%u", &one, &two);
}

inline void getUnsigned(uns &one, uns &two, uns &three) {
    scanf("%u%u%u", &one, &two, &three);
}

inline int getInt() {
    int curr;
    scanf("%d", &curr);
    return curr;
}

inline void getInt(int &one, int &two) {
    scanf("%d%d", &one, &two);
}

inline void getInt(int &one, int &two, int &three) {
    scanf("%d%d%d", &one, &two, &three);
}

inline double getDouble() {
    double curr;
    scanf("%lf", &curr);
    return curr;
}

inline void getDouble(double &one, double &two) {
    scanf("%lf%lf", &one, &two);
}

inline void getDouble(double &one, double &two, double &three) {
    scanf("%lf%lf%lf", &one, &two, &three);
}

inline void FLUSH() {
    string dummy;
    getline(cin, dummy);
}

inline string getString() {
    string curr;
    cin >> curr;
    return curr;
}

inline string getLine() {
    string curr;
    getline(cin, curr);
    return curr;
}

inline void split(string const &in, VS &out, char delim = ' ') {
    size_t start = 0; size_t len = 0;
    size_t end = in.sz -1;
    size_t foundAt = in.find_first_of(delim, start);
    while (foundAt != string::npos) {
        len = (foundAt - start);
        out.pb(in.substr(start, len));
        start = foundAt+1;
        foundAt = in.find_first_of(delim, start);
    }
    if (foundAt != end) {
        out.pb(in.substr(start));
    }
}

// ########## UTILITIES ##########//

// 0. VARIABLES

inline void foreachTest(uns testNum) {
    // 1. READ inputs

    // 2. SOLVE test
    
    // 3.  WRITE outputs
    cout << "Case #" << testNum << ": ";

    // 4. CLEANUP for next test
    cout << endl;
}

int main() {
    //freopen("input.txt", "rt", stdin);
    //freopen("output.txt", "wt", stdout);
    //freopen("log.txt", "wt", stderr);
    cout << setiosflags(ios::fixed) << setprecision(10);

    uns T = getUnsigned();
    for (uns tt=1U; tt<=T; ++tt) {
        //DOUT("At test: "); DLOUT(tt);
        foreachTest(tt);
    }
    return 0;
}

