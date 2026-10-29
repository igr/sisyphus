const fs = require("fs");
const html = fs.readFileSync(process.argv[2], "utf8");
const js = html.split("<script>")[1].split("</script>")[0];
const head = js.split("/* ---------- the numbers ---------- */")[0].replace("(() => {", "");
const sim = js.split("/* ---------- the game, minute by minute ---------- */")[1].split("/* ---------- what the numbers do ---------- */")[0];
const pg = new Function(head + sim + "\nreturn { GAME, simulate };")();
const pkg = { workPerObol: 144, climbTurns: 300, "blessings.1.effect": 0.75, "blessings.2.effect": 0.5, "blessings.3.effect": 0.25 };
for (const [label, v] of [["visions as today", {}], ["visions 150-300, then 200-600", { firstVisionMin: 150, firstVisionMax: 300, nextVisionMin: 200, nextVisionMax: 600 }]])
  for (const right of [0, 75]) {
    const p = { ...pg.GAME, ...pkg, ...v, rightAnswers: right };
    const r = [2, 5, 10, 30].map((m) => pg.simulate(p, m));
    console.log(`${label.padEnd(30)} ${String(right).padStart(2)}% right | top ${r.map((x) => x.top).join("/")} | turning ${(r[1].turns / 60).toFixed(0)} min | gods ${(r[1].gods * 100).toFixed(0)}% | visions ${r.map((x) => x.visions).join("/")} | all 15 day ${r[1].all}`);
    if (right === 75 && v.nextVisionMin) console.log("  buys at 5 min:", r[1].bought.map((b) => `${b.name} d${b.day}`).join(", "));
  }
