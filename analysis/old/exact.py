# Continuous play at 1 turn a second, cheapest item first (ties: the Agora's order), with exact obol completion.
LINES = [  # in the Agora's order
    ('rock',   [('River stone', 2, 1.5), ('Pumice', 5, 2.0), ('Sphere', 9, 3.0)]),
    ('side',   [('Merope', 3, 1.25), ('Bellerophon', 6, 1.5), ('Heracles', 10, 2.0)]),
    ('gods',   [('Goat', 1, 6), ('Pan', 4, 12), ('Thanatos', 8, 24)]),
    ('bless',  [('Hermes', 2, 504), ('Tyche', 6, 360), ('Plutus', 10, 216)]),
    ('beauty', [('Dolphins', 3, None), ('Pegasus', 7, None), ('Pleiades', 11, None)]),
]
def run(fast):
    tier = {k: 0 for k, _ in LINES}; items = dict(LINES)
    eff = lambda k, d: items[k][tier[k]-1][2] if tier[k] else d
    wpo = lambda: eff('bless', 720.0)
    obols, earned, t, out = 0.0, 0, 0, []
    need = 180.0 if fast > 0 else 720.0
    def add(w):
        nonlocal obols, earned, need
        while w > 0:
            whole = int(obols) + 1
            left = (whole - obols) * need
            if w < left - 1e-9:
                obols += w / need
                return
            w = max(0.0, w - left)
            obols = whole; earned += 1
            need = 180.0 * wpo() / 720 if earned < fast else wpo()
    while len(out) < 15:
        t += 1
        add(eff('rock', 1.0) * eff('side', 1.0) + eff('gods', 0) / 3600)
        while True:
            offers = [(items[k][tier[k]][1], i, k) for i, (k, _) in enumerate(LINES) if tier[k] < 3]
            if not offers: break
            price, _, k = min(offers)
            if obols < price: break
            old = wpo(); obols -= price; tier[k] += 1; need *= wpo() / old
            out.append((t, items[k][tier[k]-1][0]))
    return out
for name, fast in (('before (1 fast obol)', 1), ('now (4 fast obols)', 4)):
    o = run(fast)
    print(f"{name}: items by 10/15/20/30/60 min {[sum(1 for t, _ in o if t <= m*60) for m in (10, 15, 20, 30, 60)]}, all 15 at {o[-1][0]/60:.0f} min")
    print("   ", ", ".join(f"{n} {t/60:.1f}" for t, n in o[:6]))
