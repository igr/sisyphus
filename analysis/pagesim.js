// Runs the page's own simulate() from an HTML file, without the page around it.
const fs = require("fs");
function load(file) {
  const html = fs.readFileSync(file, "utf8");
  const js = html.split("<script>")[1].split("</script>")[0];
  const head = js.split("/* ---------- the numbers ---------- */")[0].replace("(() => {", "");
  const sim = js.split("/* ---------- the game, minute by minute ---------- */")[1].split("/* ---------- what the numbers do ---------- */")[0];
  return new Function(head + sim + "\nreturn { GAME, items, simulate, migrate: typeof migrate === 'function' ? migrate : null, CORE };")();
}
const [oldF, newF] = process.argv.slice(2);
const a = load(oldF), b = load(newF);
const show = (r) => `first ${r.first}, all 15 ${r.all}, wait ${r.wait}, top ${r.top}, gods ${(r.gods * 100).toFixed(1)}%` + (r.visions != null ? `, visions ${r.visions}` : "");
for (const m of [2, 5, 10, 30]) {
  const x = a.simulate(a.GAME, m), y = b.simulate(b.GAME, m), z = b.simulate({ ...b.GAME, rightAnswers: 100 }, m);
  const same = JSON.stringify(x.bought.map((i) => [i.name, i.day])) === JSON.stringify(y.bought.map((i) => [i.name, i.day])) && x.top === y.top;
  console.log(`${m} min | old: ${show(x)}\n       new: ${show(y)}  ${same ? "SAME" : "DIFFERENT"}\n  all right: ${show(z)}`);
}
const half = b.simulate({ ...b.GAME, workPerObol: 360 }, 5), dbl = b.simulate({ ...b.GAME, workPerObol: 1440 }, 5);
console.log(`5 min with 360 work: top ${half.top}; with 1440: top ${dbl.top}`);
const old = { ...a.GAME };
const m = b.migrate(old);
console.log("old saved numbers convert to:", m.fastPart, m["blessings.1.effect"], m["blessings.2.effect"], m["blessings.3.effect"], "firstObol" in m);
console.log("read-only fields:", b.CORE.flatMap((g) => g.fields).filter((f) => f.ro).map((f) => `${f.label} = ${f.value(b.GAME)}`).join("; "));
