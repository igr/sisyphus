const fs = require("fs");
const html = fs.readFileSync(process.argv[2], "utf8");
const js = html.split("<script>")[1].split("</script>")[0];
const head = js.split("/* ---------- the numbers ---------- */")[0].replace("(() => {", "");
const sim = js.split("/* ---------- the game, minute by minute ---------- */")[1].split("/* ---------- what the numbers do ---------- */")[0];
const pg = new Function(head + sim + "\nreturn { GAME, simulate };")();
// A first hill: the five tier I items only (tiers II and III free, with the effect of tier I), then a short climb.
for (const [work, climb] of [[720, 300], [360, 300]]) {
  const p = { ...pg.GAME, rightAnswers: 75, climbTurns: climb, workPerObol: work };
  for (const t of ["rock", "sidekicks", "gods", "blessings", "beauty"]) for (const k of [2, 3]) { p[`${t}.${k}.price`] = 0; if (t !== "beauty") p[`${t}.${k}.effect`] = p[`${t}.1.effect`]; }
  const r = [2, 5, 10, 30].map((m) => pg.simulate(p, m));
  console.log(`first hill: tier I only, climb ${climb}, work ${work}:`, r.map((x) => x.top).join(" / "), `| ${(r[1].turns / 60).toFixed(0)} min of turning`);
}
