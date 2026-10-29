// What each lever does to the climb, with the page's own model.
const fs = require("fs");
const html = fs.readFileSync(process.argv[2], "utf8");
const js = html.split("<script>")[1].split("</script>")[0];
const head = js.split("/* ---------- the numbers ---------- */")[0].replace("(() => {", "");
const sim = js.split("/* ---------- the game, minute by minute ---------- */")[1].split("/* ---------- what the numbers do ---------- */")[0];
const pg = new Function(head + sim + "\nreturn { GAME, simulate };")();
const G = pg.GAME;
const real = { rightAnswers: 75 };   // a player who answers 3 visions in 4 right
const scen = [
  ["today, no right answers", {}],
  ["today, 75% right", real],
  ["A: work per obol 480", { ...real, workPerObol: 480 }],
  ["A: work per obol 360", { ...real, workPerObol: 360 }],
  ["A: work per obol 240", { ...real, workPerObol: 240 }],
  ["B: visions 2x as often", { ...real, firstVisionMin: 150, firstVisionMax: 300, nextVisionMin: 300, nextVisionMax: 900 }],
  ["B: visions 2x, 2 obols each", { ...real, firstVisionMin: 150, firstVisionMax: 300, nextVisionMin: 300, nextVisionMax: 900, visionObols: 2 }],
  ["final climb 450 turns", { ...real, climbTurns: 450 }],
  ["gods 2x (12/24/48 an hour)", { ...real, "gods.1.effect": 12, "gods.2.effect": 24, "gods.3.effect": 48 }],
  ["tier III prices -3 (6/7/5/7)", { ...real, "rock.3.price": 6, "sidekicks.3.price": 7, "gods.3.price": 5, "blessings.3.price": 7 }],
  ["mix: 360 work, visions 2x, climb 600", { ...real, workPerObol: 360, firstVisionMin: 150, firstVisionMax: 300, nextVisionMin: 300, nextVisionMax: 900, climbTurns: 600 }],
  ["mix: 240 work, visions 2x, climb 600", { ...real, workPerObol: 240, firstVisionMin: 150, firstVisionMax: 300, nextVisionMin: 300, nextVisionMax: 900, climbTurns: 600 }],
];
console.log("scenario".padEnd(38), "top day @2  @5  @10  @30 | turning to the top | gods' share @5 | visions @5");
for (const [label, o] of scen) {
  const p = { ...G, ...o };
  const r = [2, 5, 10, 30].map((m) => pg.simulate(p, m));
  const five = r[1];
  console.log(label.padEnd(38), r.map((x) => String(x.top).padStart(4)).join(" "), "|", `${(five.turns / p.turnsPerMinute / 60).toFixed(1)} h`.padStart(8), "         |", `${(five.gods * 100).toFixed(0)}%`.padStart(5), "          |", five.visions);
}
