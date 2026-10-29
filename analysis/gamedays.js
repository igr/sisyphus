const fs = require("fs");
const html = fs.readFileSync(process.argv[2], "utf8");
const js = html.split("<script>")[1].split("</script>")[0];
const head = js.split("/* ---------- the numbers ---------- */")[0].replace("(() => {", "");
let sim = js.split("/* ---------- the game, minute by minute ---------- */")[1].split("/* ---------- what the numbers do ---------- */")[0];
sim = sim.replace("return { play, bought,", "return { effort, play, bought,");
const pg = new Function(head + sim + "\nreturn { GAME, simulate };")();
for (const [label, extra] of [["old", { "beauty.1.price": 3, "beauty.2.price": 7, "beauty.3.price": 11 }], ["new", {}]]) {
  const r = pg.simulate({ ...pg.GAME, ...extra }, 5), r30 = pg.simulate({ ...pg.GAME, ...extra }, 30);
  console.log(label, "5 min: top", r.top, "turns", r.turns, "game day at top", Math.floor(0.27 + r.effort / (12 * 72)) + 1, "| 30 min top", r30.top);
}
