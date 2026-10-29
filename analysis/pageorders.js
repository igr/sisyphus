// Runs the page's own buy-order code, without the page around it.
const fs = require("fs");
const html = fs.readFileSync(process.argv[2], "utf8");
const js = html.split("<script>")[1].split("</script>")[0];
const head = js.split("/* ---------- the numbers ---------- */")[0].replace("(() => {", "");
const sim = js.split("/* ---------- the game, minute by minute ---------- */")[1].split("/* ---------- what the numbers do ---------- */")[0];
const pg = new Function(head + sim + "\nreturn { GAME, PLAYERS, simulate, cheapest, inOrder, beautyFirst, saver, ROWS_DOWN, RANDOM, extremes, orderNames };")();
const p = { ...pg.GAME, ...JSON.parse(process.argv[3] || "{}") };
const t0 = Date.now();
const labels = ["fastest", "cheapest first", "random median", "rows top down", "beauty first", "rows bottom up", "slowest", "saver"];
const cols = pg.PLAYERS.map((play) => {
  const x = pg.extremes(p, play);
  const top = (pick) => pg.simulate(p, play, pick).top ?? Infinity;
  const tops = pg.RANDOM.map((o) => top(pg.inOrder(o))).sort((a, b) => a - b);
  const days = [top(pg.inOrder(x.fastest)), top(pg.cheapest), tops[Math.floor((tops.length - 1) / 2)], top(pg.inOrder(pg.ROWS_DOWN)), top(pg.beautyFirst),
    top(pg.inOrder(pg.ROWS_DOWN.slice().reverse())), top(pg.inOrder(x.slowest)), top(pg.saver)];
  return { play, x, days, minRandom: tops[0], maxRandom: tops.at(-1) };
});
console.log(`computed in ${Date.now() - t0} ms`);
labels.forEach((l, i) => console.log(l.padEnd(16), cols.map((c) => String(c.days[i]).padStart(6)).join("")));
for (const c of cols) console.log(`${c.play} min: random min ${c.minRandom}, max ${c.maxRandom}; fastest beats random min: ${c.days[0] <= c.minRandom}; slowest above random max: ${c.days[6] >= c.maxRandom}`);
const five = cols.find((c) => c.play === 5);
console.log("fastest at 5:", pg.orderNames(five.x.fastest));
console.log("slowest at 5:", pg.orderNames(five.x.slowest));
const r5 = pg.simulate(p, 5);
console.log("cheapest at 5:", r5.bought.map((b) => b.name).join(", "), "| all", r5.all, "top", r5.top);
