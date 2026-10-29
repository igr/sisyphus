# Pacing of the current economy: first obol 180 work, then 720 (or the blessing's), blessing scales the obol in progress.
LINES = {
    'rock':   [('River stone', 2, 1.5), ('Pumice', 5, 2.0), ('Sphere', 9, 3.0)],
    'side':   [('Merope', 3, 1.25), ('Bellerophon', 6, 1.5), ('Heracles', 10, 2.0)],
    'gods':   [('Goat', 1, 6), ('Pan', 4, 12), ('Thanatos', 8, 24)],
    'bless':  [('Hermes', 2, 504), ('Tyche', 6, 360), ('Plutus', 10, 216)],
    'beauty': [('Dolphins', 3, None), ('Pegasus', 7, None), ('Pleiades', 11, None)],
}

class Shop:
    def __init__(s):
        s.tier = {k: 0 for k in LINES}; s.obols = 0.0; s.need = 180.0
    def eff(s, k, default):
        return LINES[k][s.tier[k]-1][2] if s.tier[k] else default
    def bonus(s): return s.eff('rock', 1.0) * s.eff('side', 1.0)
    def per_hour(s): return s.eff('gods', 0)
    def wpo(s): return s.eff('bless', 720.0)
    def add(s, w):
        while w > 0:
            whole = int(s.obols) + 1
            left = (whole - s.obols) * s.need
            if w < left: s.obols += w / s.need; return
            w -= left; s.obols = whole; s.need = s.wpo()
    def offers(s): return [(LINES[k][s.tier[k]][1], k) for k in LINES if s.tier[k] < 3]
    def buy(s, k):
        old = s.wpo(); s.obols -= LINES[k][s.tier[k]][1]; s.tier[k] += 1; s.need *= s.wpo() / old
        return LINES[k][s.tier[k]-1][0]

def pick(s, policy):
    offers = s.offers()
    if not offers: return None
    if policy == 'cheapest': cost, k = min(offers)
    elif policy == 'power':  # rock, side, bless, gods first, beauty last
        rank = {'bless': 0, 'rock': 1, 'side': 2, 'gods': 3, 'beauty': 9}
        cost, k = min(offers, key=lambda o: (rank[o[1]], o[0]))
    elif policy == 'beauty':
        rank = {'beauty': 0, 'gods': 1, 'rock': 2, 'side': 3, 'bless': 4}
        cost, k = min(offers, key=lambda o: (rank[o[1]], o[0]))
    return k if s.obols >= cost - 1e-9 else None

def continuous(policy='cheapest', tps=1.0):
    s = Shop(); t = 0; out = []
    while len(out) < 15:
        t += 1
        s.add(tps * s.bonus() + s.per_hour() / 3600)
        while (k := pick(s, policy)):
            out.append((t, s.buy(k), s.bonus(), s.wpo()))
    return out

def daily(minutes, policy='cheapest', tpm=60):
    s = Shop(); out = []
    for day in range(1, 1000):
        for _ in range(int(minutes * tpm)):
            s.add(s.bonus())
            while (k := pick(s, policy)): out.append((day, s.buy(k)))
        s.add(s.per_hour() * 24)
        while (k := pick(s, policy)): out.append((day, s.buy(k)))
        if len(out) == 15: return out

print("Continuous play, 1 turn a second (turning minutes):")
prev = 0
for t, name, b, w in continuous():
    print(f"  {name:12s} at {t/60:6.1f} min  (+{(t-prev)/60:5.1f})   after it: {w/b/60:5.1f} min per obol")
    prev = t
for p in ('cheapest', 'power', 'beauty'):
    print(f"  policy {p:9s}: all 15 at {continuous(p)[-1][0]/60:6.1f} min")
print("5 min a day:")
d = daily(5)
print("  ", ", ".join(f"{n} d{day}" for day, n in d))
for p in ('power', 'beauty'):
    print(f"  policy {p}: all 15 on day {daily(5, p)[-1][0]}")
print("First session at 1 turn a second: items in the first 5/10/15/30 min:",
      [sum(1 for t, *_ in continuous() if t <= m*60) for m in (5, 10, 15, 30)])
