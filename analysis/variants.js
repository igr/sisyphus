// What-ifs: cheaper beauty, and a player who saves every obol until all 15 are affordable.
process.argv[2] = process.argv[2];
const o = require("./order.js");
const { P, simulateOrder, cheapest } = o;
const beautyFirst = (c) => [4, 4, 4, ...c.filter((li) => li !== 4)];
function saver(p, play) {
  // Buys nothing until the obols pay for everything: every obol at the bare rate.
  const total = [1, 2, 3].reduce((a, t) => a + ["rock", "sidekicks", "gods", "blessings", "beauty"].reduce((b, id) => b + p[`${id}.${t}.price`], 0), 0);
  const work = Math.min(total, p.fastObols) * p.fastPart * p.workPerObol + Math.max(0, total - p.fastObols) * p.workPerObol;
  return Math.ceil(work / (play * p.turnsPerMinute)) + Math.ceil(p.climbTurns / (play * p.turnsPerMinute));
}
for (const [label, p] of [["today (beauty 3/7/11)", P], ["beauty 1/2/3", { ...P, "beauty.1.price": 1, "beauty.2.price": 2, "beauty.3.price": 3 }]]) {
  const row = [2, 5, 10, 30].map((m) => {
    const d = o.dpDays.length ? null : null;
    const c = simulateOrder(p, m, cheapest()).top, bf = simulateOrder(p, m, beautyFirst(cheapest())).top;
    return `${m}m: cheap ${c}, beauty first ${bf}`;
  });
  console.log(label.padEnd(22), row.join(" | "));
}
console.log("saver (buys all at the end):", [2, 5, 10, 30].map((m) => `${m}m day ~${saver(P, m)}`).join(", "));
