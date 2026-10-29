const fs = require("fs");
const html = fs.readFileSync(process.argv[2], "utf8");
const js = html.split("<script>")[1].split("</script>")[0];
const head = js.split("/* ---------- the numbers ---------- */")[0].replace("(() => {", "");
const sim = js.split("/* ---------- the game, minute by minute ---------- */")[1].split("/* ---------- what the numbers do ---------- */")[0];
const pg = new Function(head + sim + "\nreturn { GAME, simulate };")();
for (const [label, extra] of [["old", { "beauty.1.price": 3, "beauty.2.price": 7, "beauty.3.price": 11 }], ["new", {}]]) {
  const p = { ...pg.GAME, ...extra };
  console.log(label);
  for (const m of [2, 5, 10, 30]) { const r = pg.simulate(p, m); console.log(`  ${m} min: first ${r.first}, all ${r.all}, wait ${r.wait}, top ${r.top}, visions ${r.visions}`); }
  const right = pg.simulate({ ...p, rightAnswers: 100 }, 5), slow = pg.simulate({ ...p, turnsPerMinute: 30 }, 5), fast = pg.simulate({ ...p, turnsPerMinute: 90 }, 5);
  console.log(`  all right at 5: all ${right.all}, top ${right.top}; 30 tpm all ${slow.all}; 90 tpm all ${fast.all}`);
}
