#include <bits/stdc++.h>
using namespace std;

#define int long long
#define form2(i, a, b) for (int i = (a); i < (b); ++i)
#define ford2(i, a, b) for (int i = (a-1); i >= (b); --i)
#define form(i, n) form2(i, 0, n)
#define ford(i, n) ford2(i, n, 0)

#define chmax(x, v) x = max(x, (v))
#define chmin(x, v) x = min(x, (v))
#define fi first
#define se second

const long long BIG = 1000000000000000000LL;

typedef long long ll;
typedef long double ld;
typedef pair<int, int> pii;

void solve();
signed main()
{
	ios::sync_with_stdio(false);
	cin.tie(0);
	solve();
	return 0;
}

void solve()
{
	int q; cin >> q;
	form(i, q) {
		int n; cin >> n;
		int res = 0;
		while (n % 3 == 0) { n /= 3; n *= 2; ++res; }
		while (n % 5 == 0) { n /= 5; n *= 4; ++res; }
		while (n % 2 == 0) { n /= 2; ++res; }
		if (n != 1) res = -1;
		cout << res << "\n";
	}
}