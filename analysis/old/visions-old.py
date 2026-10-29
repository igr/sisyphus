"""The table of docs/shop.md, from the rules of Shop.kt and Game.kt, with the visions of Voices.kt counted."""
import math, random, statistics, sys
LINES = ['rock', 'side', 'gods', 'bless', 'beauty']
ITEMS = {
    'rock': [('River stone', 2, 1.5), ('Pumice', 5, 2.0), ('Sphere', 9, 3.0)],
    'side': [('Merope', 3, 1.25), ('Bellerophon', 6, 1.5), ('Heracles', 10, 2.0)],
    'gods': [('Goat', 1, 6.0), ('Pan', 4, 12.0), ('Thanatos', 8, 24.0)],
    'bless': [('Hermes', 2, 0.7), ('Tyche', 6, 0.5), ('Plutus', 10, 0.3)],
    'beauty': [('Dolphins', 3, 0), ('Pegasus', 7, 0), ('Pleiades', 11, 0)],
}
EPS = 1e-9
WORK = 720.0  # WORK_PER_OBOL
HOLD = 0      # seconds a vision stops the pushing

class Shop:
    def __init__(s):
        s.tier = {k: 0 for k in LINES}; s.obols = 0.0; s.need = s.fast()
    def eff(s, k, d): return ITEMS[k][s.tier[k] - 1][2] if s.tier[k] else d
    def bonus(s): return s.eff('rock', 1.0) * s.eff('side', 1.0)
    def per_hour(s): return s.eff('gods', 0.0)
    def wpo(s): return WORK * s.eff('bless', 1.0)
    def fast(s): return WORK / 4 * s.eff('bless', 1.0)
    def earned(s): return math.floor(s.obols) + sum(ITEMS[k][i][1] for k in LINES for i in range(s.tier[k]))
    def work(s, w):
        while w > 0:
            whole = math.floor(s.obols) + 1
            left = (whole - s.obols) * s.need
            if w < left - EPS: s.obols += w / s.need; return
            w -= left; s.obols = whole
            s.need = s.fast() if s.earned() < 4 else s.wpo()
    def buy_cheapest(s):
        offers = [(ITEMS[k][s.tier[k]][1], i, k) for i, k in enumerate(LINES) if s.tier[k] < 3]
        if not offers: return None
        cost, _, k = min(offers)
        if s.obols < cost - EPS: return None
        old = s.wpo(); s.obols -= cost; s.tier[k] += 1; s.need *= s.wpo() / old
        return ITEMS[k][s.tier[k] - 1][0]
    def done(s): return all(s.tier[k] == 3 for k in LINES)

def play(minutes, tpm=60, right=False, seed=None, night=False):
    """One climb at [minutes] a day and [tpm] turns a minute. With [right], every vision gives an obol."""
    rnd = random.Random(seed)
    s = Shop(); days = []; names = []; visions = 0; own = 0.0; climb = None; hold = 0
    nxt = rnd.uniform(300, 600) if seed is not None else 450.0
    def buy(day):
        nonlocal climb
        while climb is None and (n := s.buy_cheapest()):
            days.append(day); names.append(n)
            if s.done(): climb = 900.0
    for day in range(1, 5000):
        buy(day)
        for _ in range(int(minutes * 60)):
            t = tpm / 60
            if hold > 0:
                hold -= 1; continue
            if climb is None:
                s.work(t * s.bonus() + (0 if night else s.per_hour() / 3600))
                own += t; nxt -= t
                if nxt <= 0:
                    visions += 1; hold = HOLD
                    nxt = (rnd.uniform(600, 1800) if seed is not None else 1200.0)
                    if right: s.obols += 1
                buy(day)
            else:
                climb -= t
                if climb <= 0:
                    wait = max(b - a for a, b in zip(days, days[1:]))
                    return dict(first=days[0], all15=days[-1], wait=wait, top=day, visions=visions, own=own, names=names)
        if climb is None: s.work(s.per_hour() * (24 if night else 24 - minutes / 60))

def nonstop(right=False, seed=None):
    rnd = random.Random(seed); s = Shop(); t = 0; visions = 0; bought = 0
    nxt = rnd.uniform(300, 600) if seed is not None else 450.0
    while not s.done():
        t += 1
        s.work(s.bonus() + s.per_hour() / 3600); nxt -= 1
        if nxt <= 0:
            visions += 1; nxt = rnd.uniform(600, 1800) if seed is not None else 1200.0
            if right: s.obols += 1
        while s.buy_cheapest(): pass
    return t / 60, visions

print("Check against the table (no right answers, visions not counted toward obols):")
for m in (2, 5, 10, 30):
    r = play(m)
    print(f"  {m:2d} min: first day {r['first']}, all 15 day {r['all15']}, longest wait {r['wait']} days, top day {r['top']}, own turns before the final climb {r['own']:.0f}")
print("  order at 5 min:", ", ".join(play(5)['names']))
print(f"  non-stop: all 15 after {nonstop()[0]:.0f} min of turning")
print(f"  5 min a day at 30 and 90 turns a minute: all 15 on day {play(5, 30)['all15']} and {play(5, 90)['all15']}")
N = int(sys.argv[1]) if len(sys.argv) > 1 else 2000
print(f"\nVisions in a climb, {N} random climbs each:")
for m in (2, 5, 10, 30):
    w = [play(m, seed=i)['visions'] for i in range(N)]
    rr = [play(m, right=True, seed=i) for i in range(N)]
    q = statistics.quantiles(w, n=10)
    print(f"  {m:2d} min: wrong answers {statistics.mean(w):4.1f} (min {min(w)}, max {max(w)}, 10-90% {q[0]:.0f}-{q[-1]:.0f});"
          f" all right {statistics.mean(r['visions'] for r in rr):4.1f}, all 15 day {statistics.median(r['all15'] for r in rr)}, top day {statistics.median(r['top'] for r in rr)}")
ns = [nonstop(seed=i) for i in range(N)]; nr = [nonstop(right=True, seed=i) for i in range(N)]
print(f"  non-stop: wrong {statistics.mean(v for _, v in ns):4.1f}; all right {statistics.mean(v for _, v in nr):4.1f}, all 15 after {statistics.mean(t for t, _ in nr):.0f} min")
