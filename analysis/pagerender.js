// Runs the whole page script against a stub DOM, then prints the buy-order section.
const fs = require("fs");
const html = fs.readFileSync(process.argv[2], "utf8");
const js = html.split("<script>")[1].split("</script>")[0];
const els = {};
const el = (id) => els[id] ??= { id, innerHTML: "", textContent: "", value: "", hidden: false, disabled: false, title: "", dataset: {},
  classList: { toggle() {}, remove() {}, add() {} }, addEventListener() {}, querySelector: () => el("q-" + id), closest: () => null };
global.document = { getElementById: el, querySelectorAll: () => [], documentElement: {}, activeElement: null };
global.window = {};
global.getComputedStyle = () => ({ getPropertyValue: () => "#000" });
global.matchMedia = () => ({ addEventListener() {} });
global.MutationObserver = class { observe() {} };
const errors = [];
process.on("uncaughtException", (e) => { errors.push(e); });
new Function(js)();
setTimeout(() => {
  const strip = (h) => h.replace(/<\/(td|th)>/g, " | ").replace(/<\/tr>|<\/li>/g, "\n").replace(/<[^>]+>/g, "");
  console.log(strip(els.ordersHead.innerHTML));
  console.log(strip(els.orders.innerHTML));
  console.log(strip(els.ordersNotes.innerHTML));
  console.log("key:", strip(els.key.innerHTML));
  console.log("errors:", errors.map(String));
}, 200);
