// How the order of purchases changes the climb. Uses the page's own numbers and its minute-by-minute model,
// but buys the items in a given order: the next item of the order, as soon as it is affordable.
const fs = require("fs");
const file = process.argv[2];
const html = fs.readFileSync(file, "utf8");
const js = html.split("<script>")[1].split("</script>")[0];
const head = js.split("/* ---------- the numbers ---------- */")[0].replace("(() => {", "");
const sim = js.split("/* ---------- the game, minute by minute ---------- */")[1].split("/* ---------- what the numbers do ---------- */")[0];
const page = new Function(head + sim + "\nreturn { GAME, items, simulate, LINES };")();
const P = page.GAME;
const PX = 12;

/** The page's simulate(), with the cheapest-first choice replaced by a fixed order of line indexes. */
function simulateOrder(p, play, order) {
  const shop = page.items(p);
  const tier = [0, 0, 0, 0, 0];
  let obols = 0, pos = 0, summit = null, top = null, next = 0, all = null;
  let earned = 0;
  const eff = (li, def) => tier[li] ? shop[li][tier[li] - 1].effect : def;
  const part = () => eff(3, 1);
  const needNext = () => (earned < p.fastObols ? p.fastPart : 1) * p.workPerObol * part();
  let need = needNext();
  const earn = (work) => {
    while (summit === null && work > 0) {
      const whole = Math.floor(obols) + 1, left = (whole - obols) * need;
      if (work < left - 1e-9) { obols += work / need; return; }
      work = Math.max(0, work - left); obols = whole; earned++; need = needNext();
    }
  };
  const away = 1440 - play;
  for (let d = 1; d <= 36500 && top === null; d++) {
    for (let m = 0; m < play && top === null; m++) {
      const bonus = eff(0, 1) * eff(1, 1), rate = eff(2, 0);
      const t = p.turnsPerMinute;
      let moved = t * Math.min(bonus, p.sunCap) * PX;
      if (summit !== null && pos + moved >= summit) moved = summit - pos;
      pos += moved;
      earn(t * bonus);
      earn(rate / 60);
      if (summit !== null && pos >= summit - 1e-6) { top = d; break; }
      while (next < 15) {
        const li = order[next], it = shop[li][tier[li]];
        if (obols < it.price) break;
        const before = part();
        obols -= it.price; tier[li]++; next++;
        need *= part() / before;
        if (next === 15) { summit = pos + p.climbTurns * p.sunCap * PX; all = d; }
      }
    }
    if (top !== null) break;
    earn(eff(2, 0) * away / 60);
  }
  return { all, top };
}

const price = (li, t) => P[`${page.LINES[li].id}.${t}.price`];
const effect = (li, t, def) => t ? P[`${page.LINES[li].id}.${t}.effect`] : def;

/** The order the page assumes: the cheapest next item, ties to the line shown first. */
function cheapest() {
  const tier = [0, 0, 0, 0, 0], out = [];
  while (out.length < 15) {
    let best = -1;
    for (let li = 0; li < 5; li++) if (tier[li] < 3 && (best < 0 || price(li, tier[li] + 1) < price(best, tier[best] + 1))) best = li;
    out.push(best); tier[best]++;
  }
  return out;
}

/** Days to all 15 with the work spread evenly over each day: exact enough to rank orders. */
function dpDays(play) {
  const states = 1024, idx = (t) => t.reduce((a, x, i) => a + x * 4 ** i, 0), un = (s) => [0, 1, 2, 3, 4].map((i) => Math.floor(s / 4 ** i) % 4);
  const best = new Array(states).fill(Infinity), worst = new Array(states).fill(-Infinity), bArg = [], wArg = [];
  const full = idx([3, 3, 3, 3, 3]);
  best[full] = worst[full] = 0;
  const order = [...Array(states).keys()].sort((a, b) => un(b).reduce((x, y) => x + y) - un(a).reduce((x, y) => x + y));
  for (const s of order) {
    if (s === full) continue;
    const t = un(s);
    const spent = t.reduce((a, x, li) => a + [1, 2, 3].filter((k) => k <= x).reduce((c, k) => c + price(li, k), 0), 0);
    const rate = play * P.turnsPerMinute * effect(0, t[0], 1) * effect(1, t[1], 1) + 24 * effect(2, t[2], 0);
    for (let li = 0; li < 5; li++) {
      if (t[li] === 3) continue;
      let days = 0;
      for (let k = spent + 1; k <= spent + price(li, t[li] + 1); k++) days += (k <= P.fastObols ? P.fastPart : 1) * P.workPerObol * effect(3, t[3], 1) / rate;
      const n = [...t]; n[li]++;
      const ns = idx(n);
      if (days + best[ns] < best[s]) { best[s] = days + best[ns]; bArg[s] = li; }
      if (days + worst[ns] > worst[s]) { worst[s] = days + worst[ns]; wArg[s] = li; }
    }
  }
  const walk = (arg) => { const t = [0, 0, 0, 0, 0], out = []; while (out.length < 15) { const li = arg[idx(t)]; out.push(li); t[li]++; } return out; };
  return { best: walk(bArg), worst: walk(wArg) };
}

function shuffle(a, rnd) { for (let i = a.length - 1; i > 0; i--) { const j = Math.floor(rnd() * (i + 1)); [a[i], a[j]] = [a[j], a[i]]; } return a; }
function rng(seed) { return () => { seed = (seed * 1664525 + 1013904223) >>> 0; return seed / 2 ** 32; }; }

const names = (o) => { const t = [0, 0, 0, 0, 0]; return o.map((li) => page.LINES[li].names[t[li]++]).join(", "); };
const lineBy = (lines) => lines.flatMap((li) => [li, li, li]);
const beautyFirst = [4, 4, 4, ...cheapest().filter((li) => li !== 4)];
const beautyLast = [...cheapest().filter((li) => li !== 4), 4, 4, 4];

module.exports = { P, page, simulateOrder, cheapest, dpDays, lineBy, names };
if (require.main === module) {
// Sanity: the fixed cheapest order gives the page's own results.
for (const m of [2, 5, 10, 30]) {
  const a = page.simulate(P, m), b = simulateOrder(P, m, cheapest());
  if (a.all !== b.all || a.top !== b.top) console.log(`MISMATCH at ${m} min: page ${a.all}/${a.top}, here ${b.all}/${b.top}`);
}

console.log("cheapest first:", names(cheapest()));
for (const m of [2, 5, 10, 30]) {
  const dp = dpDays(m);
  const rnd = rng(42), tops = [];
  for (let i = 0; i < 3000; i++) tops.push(simulateOrder(P, m, shuffle(lineBy([0, 1, 2, 3, 4]), rnd)).top);
  tops.sort((a, b) => a - b);
  const q = (f) => tops[Math.floor(f * (tops.length - 1))];
  const row = (label, o) => { const r = simulateOrder(P, m, o); return `  ${label.padEnd(26)} all 15 day ${String(r.all).padStart(5)}, top day ${String(r.top).padStart(5)}`; };
  console.log(`\n${m} min a day`);
  console.log(row("best order", dp.best));
  console.log(row("cheapest first (page)", cheapest()));
  console.log(row("beauty last, else cheap", beautyLast));
  console.log(row("Agora rows, top to bottom", lineBy([0, 1, 2, 3, 4])));
  console.log(row("beauty first, else cheap", beautyFirst));
  console.log(row("Agora rows, bottom to top", lineBy([4, 3, 2, 1, 0])));
  console.log(row("worst order", dp.worst));
  console.log(`  random orders: top day median ${q(0.5)}, 10% ${q(0.1)}, 90% ${q(0.9)}, worst seen ${tops[tops.length - 1]}`);
  if (m === 5) { console.log("  best: " + names(dp.best)); console.log("  worst: " + names(dp.worst)); }
}
}
