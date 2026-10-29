# Analysis

Models of the economy of the game: how long a climb takes, how many visions come, and what a change of the numbers does. They copy the rules of `game/Config.kt`, `game/Shop.kt`, `game/Game.kt` and `game/Voices.kt`. When those rules change, change the models too, or their numbers are wrong.

## The economy page

`economy.html` is the Sisyphus Economy page, published as a Claude artifact. Open it in a browser. Change a number on the left, and the results on the right follow. `GAME` in its script holds the numbers of `game/Config.kt`. Saving for everyone works only on the published page.

## Scripts for the rules of today

The JavaScript scripts run the page's own code: they read `economy.html`, so they follow its numbers. Give them the page as the first argument, for example `node analysis/order.js analysis/economy.html`.

| Script | What it tells |
|---|---|
| `visions.py` | The table "How long it takes" in `docs/shop.md`: the day of the first item, of all 15 and of the top, and the visions in a climb, for 2, 5, 10 and 30 minutes a day. The argument is the number of random climbs: `python3 analysis/visions.py 2000`. |
| `idle.py` | A player who lets only the gods push: the real days until all 15 items. `python3 analysis/idle.py` |
| `page-sim.js` | The results of the page for each daily play. |
| `daysim.js` | The day of the top, the game day at the top, the player's own turns and the visions, for each daily play. |
| `order.js` | The order of purchases: the best, the worst and the cheapest first, for each daily play. `fixes.js` and `variants.js` use it. |
| `pageorders.js` | The "Buy order" table of the page. |
| `pagerender.js` | Runs the whole page script without a browser, and prints the "Buy order" section. A quick check that the page works. |
| `pagesim.js` | Compares two versions of the page: `node analysis/pagesim.js old.html new.html`. |
| `pagecards.js`, `gamedays.js` | The cards of the page, and the game day at the top, for the numbers of today and for one change. |
| `levers.js`, `levers2.js` | What each lever does to a climb: the work for an obol, stronger items, faster gods, the visions. |
| `week.js`, `week2.js` | Which numbers make every climb about a week at 5 minutes a day. |
| `fixes.js` | The best and the worst order when the Agora sells only some items. |
| `variants.js` | Cheaper Beauty, and a player who saves every obol until all 15 are paid for. |
| `firsthill.js`, `firsthill2.js` | A short first hill: only the tier I items and a short final climb. An idea for later. `firsthill2.js` gives no result with the page of today. |

## Old rules

`old/` keeps the scripts of earlier economies, such as 720 work for an obol, and the first vision after 150 to 300 turns. They give the numbers of those rules, not of today's. Run them from anywhere, for example `python3 analysis/old/pace.py`.

| Script | What it tells |
|---|---|
| `pace.py`, `ramp.py`, `exact.py` | The pace of the items before every climb took about a week. |
| `voices.py` | How much shorter a climb gets with the visions, from the plan of the voices. |
| `visions-old.py`, `visions.py`, `visions2.py` | The table of `docs/shop.md` with the old work for an obol. |
| `visions3.py` | The table of `docs/shop.md` with the first vision after 150 to 300 turns. |
