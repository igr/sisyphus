// Which numbers make every climb about a week at 5 minutes a day.
const fs = require("fs");
const html = fs.readFileSync(process.argv[2], "utf8");
const js = html.split("<script>")[1].split("</script>")[0];
const head = js.split("/* ---------- the numbers ---------- */")[0].replace("(() => {", "");
const sim = js.split("/* ---------- the game, minute by minute ---------- */")[1].split("/* ---------- what the numbers do ---------- */")[0];
const pg = new Function(head + sim + "\nreturn { GAME, simulate };")();
const twice = { firstVisionMin: 150, firstVisionMax: 300, nextVisionMin: 300, nextVisionMax: 900 };
const rows = [];
for (const work of [144, 180, 216, 240])
  for (const climb of [300, 450])
    for (const [vn, v] of [["1x", {}], ["2x", twice]])
      for (const right of [0, 75]) {
        const p = { ...pg.GAME, ...v, workPerObol: work, climbTurns: climb, rightAnswers: right };
        const r = [2, 5, 10, 30].map((m) => pg.simulate(p, m));
        rows.push(`${String(work).padStart(4)} ${String(climb).padStart(4)} ${vn} ${String(right).padStart(3)}% | top ${r.map((x) => String(x.top).padStart(3)).join(" ")} | wait@5 ${r[1].wait} | turning ${(r[1].turns / 60).toFixed(0).padStart(3)} min | gods ${(r[1].gods * 100).toFixed(0)}% | visions ${r[1].visions} | first item day ${r[1].first}, all 15 day ${r[1].all}`);
      }
console.log("work climb vis right | top day @2 @5 @10 @30");
console.log(rows.join("\n"));
