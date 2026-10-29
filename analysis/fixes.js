// Worst and best order under a rule: [allowed(tiers, line)] says which next items the Agora sells.
const o = require("./order.js");
const ids = ["rock", "sidekicks", "gods", "blessings", "beauty"];
function dp(p, play, allowed) {
  const price = (li, t) => p[`${ids[li]}.${t}.price`], eff = (li, t, d) => (t ? p[`${ids[li]}.${t}.effect`] : d);
  const idx = (t) => t.reduce((a, x, i) => a + x * 4 ** i, 0), un = (s) => [0, 1, 2, 3, 4].map((i) => Math.floor(s / 4 ** i) % 4);
  const best = {}, worst = {}, bA = {}, wA = {}, full = idx([3, 3, 3, 3, 3]);
  best[full] = worst[full] = 0;
  const all = [...Array(1024).keys()].sort((a, b) => un(b).reduce((x, y) => x + y) - un(a).reduce((x, y) => x + y));
  for (const s of all) {
    if (s === full) continue;
    const t = un(s);
    const spent = t.reduce((a, x, li) => a + [1, 2, 3].filter((k) => k <= x).reduce((c, k) => c + price(li, k), 0), 0);
    const rate = play * p.turnsPerMinute * eff(0, t[0], 1) * eff(1, t[1], 1) + 24 * eff(2, t[2], 0);
    best[s] = Infinity; worst[s] = -Infinity;
    for (let li = 0; li < 5; li++) {
      if (t[li] === 3 || !allowed(t, li)) continue;
      const n = [...t]; n[li]++; const ns = idx(n);
      if (!(ns in best) || best[ns] === Infinity) continue;
      let days = 0;
      for (let k = spent + 1; k <= spent + price(li, t[li] + 1); k++) days += (k <= p.fastObols ? p.fastPart : 1) * p.workPerObol * eff(3, t[3], 1) / rate;
      if (days + best[ns] < best[s]) { best[s] = days + best[ns]; bA[s] = li; }
      if (days + worst[ns] > worst[s]) { worst[s] = days + worst[ns]; wA[s] = li; }
    }
  }
  const walk = (A) => { const t = [0, 0, 0, 0, 0], out = []; while (out.length < 15) { const li = A[idx(t)]; out.push(li); t[li]++; } return out; };
  return { best: o.simulateOrder(p, play, walk(bA)).top, worst: o.simulateOrder(p, play, walk(wA)).top, cheap: o.simulateOrder(p, play, o.cheapest()).top };
}
const free = () => true;
const beautyAfterTier = (t, li) => li !== 4 || [0, 1, 2, 3].every((j) => t[j] > t[4]);
const cheapBeauty = { ...o.P, "beauty.1.price": 1, "beauty.2.price": 2, "beauty.3.price": 3 };
for (const [label, p, rule] of [["today", o.P, free], ["beauty 1/2/3", cheapBeauty, free], ["beauty after its tier", o.P, beautyAfterTier]]) {
  console.log(label.padEnd(22), [2, 5, 10, 30].map((m) => { const r = dp(p, m, rule); return `${m}m best ${r.best} cheap ${r.cheap} worst ${r.worst}`; }).join(" | "));
}
