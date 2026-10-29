// Runs the economy page's own code: its constants, items and simulation.
const fs = require("fs");
const html = fs.readFileSync(process.argv[2], "utf8");
const cut = (from, to) => html.slice(html.indexOf(from), html.indexOf(to));
const code = [
  cut("const PX = 12;", "const CORE = ["),
  cut("/* ---------- the game, minute by minute ---------- */", "/* ---------- what the numbers do ---------- */"),
  "module.exports = { GAME, simulate };",
].join("\n");
const m = { exports: {} };
new Function("module", code)(m);
const { GAME, simulate } = m.exports;
for (const play of [2, 5, 10, 30]) {
  const r = simulate(GAME, play);
  console.log(`${play} min: first day ${r.first}, all 15 day ${r.all}, longest wait ${r.wait} d, top day ${r.top}, gods ${Math.round(r.gods * 100)}%`);
}
for (const tpm of [30, 90]) console.log(`5 min at ${tpm} a minute: all 15 day ${simulate({ ...GAME, turnsPerMinute: tpm }, 5).all}`);
console.log("order at 5 min:", simulate(GAME, 5).bought.map((b) => `${b.name} d${b.day}`).join(", "));
const old = { ...GAME, fastObols: 1 };
for (const play of [2, 5, 10, 30]) {
  const r = simulate(old, play);
  console.log(`before, ${play} min: first day ${r.first}, all 15 day ${r.all}, longest wait ${r.wait} d, top day ${r.top}`);
}
