const fs = require("fs");
const html = fs.readFileSync(process.argv[2], "utf8");
const js = html.split("<script>")[1].split("</script>")[0];
const head = js.split("/* ---------- the numbers ---------- */")[0].replace("(() => {", "");
const sim = js.split("/* ---------- the game, minute by minute ---------- */")[1].split("/* ---------- what the numbers do ---------- */")[0];
const pg = new Function(head + sim + "\nreturn { GAME, simulate };")();
const real = { rightAnswers: 75 };
for (const [label, o] of [
  ["stronger items: rock 2/3/4, sidekicks 1.5/2/2.5", { ...real, "rock.1.effect": 2, "rock.2.effect": 3, "rock.3.effect": 4, "sidekicks.1.effect": 1.5, "sidekicks.2.effect": 2, "sidekicks.3.effect": 2.5 }],
  ["gods 1.5x (9/18/36)", { ...real, "gods.1.effect": 9, "gods.2.effect": 18, "gods.3.effect": 36 }],
  ["first hill: tier I only, then 300 turns (approx)", null],
]) {
  if (!o) continue;
  const p = { ...pg.GAME, ...o };
  const r = [2, 5, 10, 30].map((m) => pg.simulate(p, m));
  console.log(label.padEnd(50), r.map((x) => x.top).join(" / "), `| ${(r[1].turns / 3600).toFixed(1)} h | gods ${(r[1].gods * 100).toFixed(0)}% | visions ${r[1].visions}`);
}
// A first hill: only the five tier I items, then a 300-turn climb, at 5 minutes a day and 75% right.
const p = { ...pg.GAME, ...real, climbTurns: 300 };
for (const t of ["rock", "sidekicks", "gods", "blessings", "beauty"]) for (const k of [2, 3]) p[`${t}.${k}.price`] = 0;
for (const work of [720, 360]) {
  const r = [2, 5, 10, 30].map((m) => pg.simulate({ ...p, workPerObol: work }, m));
  console.log(`first hill (tier I only, climb 300), work ${work}`.padEnd(50), r.map((x) => x.top).join(" / "), `| ${(r[1].turns / 3600).toFixed(2)} h`);
}
