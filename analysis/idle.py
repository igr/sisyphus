"""A player who only lets the gods push: 36 own turns for the first obol, then the app closed, buying as soon as possible."""
import itertools, math
WORK = 144.0
ITEMS = {
    'rock': [(2, 1.5), (5, 2.0), (9, 3.0)], 'side': [(3, 1.25), (6, 1.5), (10, 2.0)],
    'gods': [(1, 6.0), (4, 12.0), (8, 24.0)], 'bless': [(2, 0.75), (6, 0.5), (10, 0.25)],
    'beauty': [(1, 0), (2, 0), (3, 0)],
}

class Shop:
    def __init__(s):
        s.tier = {k: 0 for k in ITEMS}; s.obols = 0.0; s.earned = 0; s.need = s.fast()
    def part(s): return ITEMS['bless'][s.tier['bless'] - 1][1] if s.tier['bless'] else 1.0
    def fast(s): return WORK / 4 * s.part()
    def wpo(s): return WORK * s.part()
    def rate(s): return ITEMS['gods'][s.tier['gods'] - 1][1] if s.tier['gods'] else 0.0
    def work(s, w):
        while w > 0:
            whole = math.floor(s.obols) + 1
            left = (whole - s.obols) * s.need
            if w < left - 1e-9: s.obols += w / s.need; return
            w -= left; s.obols = whole; s.earned += 1
            s.need = s.fast() if s.earned < 4 else s.wpo()
    def price(s, k): return ITEMS[k][s.tier[k]][0]
    def buy(s, k):
        old = s.wpo(); s.obols -= s.price(k); s.tier[k] += 1; s.need *= s.wpo() / old

def run(order, check_every=None, dt=1 / 60):
    """Hours until all 15 are bought. [order] lists the lines to buy; with [check_every] hours, the app opens only then."""
    s = Shop(); s.work(36.0)                     # the player's own 36 turns: the first fast obol
    t = 0.0; i = 0
    while i < len(order):
        can_buy = check_every is None or abs(t / check_every - round(t / check_every)) < dt / 2 / check_every
        while can_buy and i < len(order) and s.obols >= s.price(order[i]) - 1e-9:
            s.buy(order[i]); i += 1
        if i == len(order): break
        s.work(s.rate() * dt); t += dt
    return t

useless = ['rock'] * 3 + ['side'] * 3 + ['beauty'] * 3
best = None
for perm in set(itertools.permutations(['gods', 'gods', 'bless', 'bless', 'bless'])):
    order = ['gods'] + list(perm) + useless
    h = run(order)
    if best is None or h < best[0]: best = (h, order)
h, order = best
print(f"best order: {order[:6]} then rock, sidekicks, beauty")
print(f"all 15 bought, buying the moment it is affordable: {h:.1f} h = {h / 24:.1f} days")
print(f"opening the app once a day: {run(order, check_every=24) / 24:.1f} days")
print(f"only the three gods: {run(['gods'] * 3):.1f} h")
gods_only = ['gods'] * 3 + ['bless'] * 3 + useless
print(f"gods first, then blessings: {run(gods_only) / 24:.1f} days")
