import pace
def run(ramp):
    s = pace.Shop(); s.count = 0; t = 0; out = []
    def add(w):
        while w > 0:
            whole = int(s.obols) + 1
            left = (whole - s.obols) * s.need
            if w < left: s.obols += w / s.need; return
            w -= left; s.obols = whole; s.count += 1
            s.need = ramp[s.count] * s.wpo() / 720 if s.count < len(ramp) else s.wpo()
    while len(out) < 15:
        t += 1
        add(s.bonus() + s.per_hour() / 3600)
        while (k := pace.pick(s, 'cheapest')): out.append((t, s.buy(k)))
    return out
for name, ramp in [('now', [180]), ('180 x4', [180]*4), ('180,240,360,480', [180, 240, 360, 480]), ('180 x6', [180]*6)]:
    o = run(ramp)
    print(f"{name:18s} items by 10/15/30/60 min: {[sum(1 for t, _ in o if t <= m*60) for m in (10, 15, 30, 60)]}  first 4 at {[round(t/60,1) for t, _ in o[:4]]}  all at {o[-1][0]/60:.0f} min")
