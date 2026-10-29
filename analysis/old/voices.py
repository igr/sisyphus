"""How much shorter a climb gets when voices give obols: every 10 minutes of turning, a right answer."""
import random
LINES = {
    'rock':   [2, 5, 9], 'side': [3, 6, 10], 'gods': [1, 4, 8], 'bless': [2, 6, 10], 'beauty': [3, 7, 11],
}
EFF = {'rock': [1.5, 2.0, 3.0], 'side': [1.25, 1.5, 2.0], 'gods': [6, 12, 24], 'bless': [504, 360, 216]}

def climb(reward, every=600, tps=1.0, seed=1):
    rnd = random.Random(seed)
    tier = {k: 0 for k in LINES}; obols = 0.0; earned = 0
    def eff(k, d): return EFF[k][tier[k] - 1] if tier[k] else d
    def wpo(): return eff('bless', 720.0)
    need = 180.0
    def add(w):
        nonlocal obols, need, earned
        while w > 0:
            whole = int(obols + 1e-9) + 1
            left = (whole - obols) * need
            if w < left - 1e-9: obols += w / need; return
            w -= left; obols = whole; earned += 1
            need = 180.0 * wpo() / 720.0 if earned < 4 else wpo()
    t = 0; bought = 0; next_voice = every * rnd.uniform(0.5, 1.5); saved = 0.0
    while bought < 15:
        t += 1
        rate = tps * eff('rock', 1) * eff('side', 1)
        add(rate + eff('gods', 0) / 3600)
        if reward and t >= next_voice:
            next_voice = t + every * rnd.uniform(0.5, 1.5)
            if reward == 'minute': add(60 * rate)
            else: add(need * (int(obols + 1e-9) + 1 - obols) if False else need * 1.0)
        while True:
            offers = [(LINES[k][tier[k]], k) for k in LINES if tier[k] < 3]
            cost, k = min(offers)
            if obols + 1e-9 < cost: break
            old = wpo(); obols -= cost; tier[k] += 1; earned += 0; need *= wpo() / old; bought += 1
            if bought == 15: break
    return t / 60

base = sum(climb(None, seed=s) for s in range(20)) / 20
for r in ('minute', 'obol'):
    v = sum(climb(r, seed=s) for s in range(20)) / 20
    print(f"{r:6s}: all 15 items at {v:5.1f} min of turning, without voices {base:5.1f} min: {100 * (1 - v / base):4.1f}% shorter")
