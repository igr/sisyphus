const fs = require("fs");
const html = fs.readFileSync(process.argv[2], "utf8");
const js = html.split("<script>")[1].split("</script>")[0];
const head = js.split("/* ---------- the numbers ---------- */")[0].replace("(() => {", "");
let sim = js.split("/* ---------- the game, minute by minute ---------- */")[1].split("/* ---------- what the numbers do ---------- */")[0];
sim = sim.replace("turns, visions };", "turns, visions, gameDays: START + effort / day };");
const { GAME, simulate } = new Function(head + sim + "\nreturn { GAME, simulate };")();
for (const m of [2, 5, 10, 30]) {
  const r = simulate({ ...GAME, rightAnswers: 75 }, m);
  console.log(`${m} min a day: top on day ${r.top} of play, game day ${Math.floor(r.gameDays) + 1} at the top, ${Math.round(r.turns)} own turns, ${r.visions} visions`);
}
