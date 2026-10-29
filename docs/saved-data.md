# Saved data

The game saves all of its data on the device, and there is no account. Only the purchases of worlds go over the network: the store keeps each purchase, and RevenueCat keeps the worlds of each player under an anonymous id. The save holds no purchases.

Each saved value is one number under a short key. The code that reads and writes the keys is in `Game.kt`.

## Where the data is

| Platform | Storage | Key names |
|---|---|---|
| iOS | `NSUserDefaults`, the standard defaults of the app | Each key starts with `sisyphus.`, for example `sisyphus.obols` |
| Android | The `SharedPreferences` file `sisyphus` | The key itself. The value is saved as text. |
| Desktop | Java `Preferences`, node `dev/oblac/sisyphus` | The key itself |

## The keys

The last two columns show what happens to each value when Sisyphus reaches the top, and when the player goes to another world.

| Key | What it holds | At the top | In another world |
|---|---|---|---|
| `effort` | The work that moves the sun, in pixels. The day and the time of day come from it. | Back to 0 | Back to 0 |
| `position` | How far Sisyphus walked up the hill, in pixels. | Back to 0 | Back to 0 |
| `knob` | The angle of the knob, in radians. | Kept | Kept |
| `obols` | The obols, with the part of the next obol. | Back to 0 | Back to 0 |
| `nextObol` | The work that the next obol needs, in turns. It is set when the obol before it comes. | Back to 36 | Back to 36 |
| `shop.rock` | The tier bought in the Rock line: 0 to 3. | Back to 0 | Back to 0 |
| `shop.beside` | The tier bought in the Sidekicks line: 0 to 3. | Back to 0 | Back to 0 |
| `shop.alone` | The tier bought in the Gods line: 0 to 3. | Back to 0 | Back to 0 |
| `shop.blessing` | The tier bought in the Blessings line: 0 to 3. | Back to 0 | Back to 0 |
| `shop.beauty` | The tier bought in the Beauty line: 0 to 3. | Back to 0 | Back to 0 |
| `summit` | Where the hill ends, in pixels walked. -1 means that the final climb has not started. | Back to -1 | Back to -1 |
| `climbs` | How many times Sisyphus reached the top. | Plus 1 | Kept |
| `place` | The id of the world: 0 is Corinth, 1 is Mount Etna, 2 is The Underworld, 3 is Cairo. | Kept | The new world |
| `quoteSeed` | The number that sets the order of the wise words. | Kept | Kept |
| `quotesDrawn` | How many wise words came so far. | Back to 0 | Back to 0 |
| `nextVision` | The player's own turns until the next vision. Set when a vision comes. | 70 to 100 | 70 to 100 |
| `visionSeed` | The number that sets the order of the visions. | Kept | Kept |
| `visionsDrawn` | How many visions came so far. | Kept | Kept |
| `rightAnswers` | How many questions of the visions the player answered right. | Kept | Kept |
| `turnsPushed` | The player's own turns that pushed, in all climbs and worlds. The gods' pushes do not count. | Kept | Kept |
| `daysBefore` | The days pushed through in all the climbs before this one. | Plus the days of the climb | Plus the days of the climb |
| `laborsSeen` | How many labors were done when the Labors tab was last open. A dot stands by the day while more are done. | Kept | Kept |
| `stelesTold` | How many parts of the tale the stones told in this climb. | Back to 0 | Back to 0 |
| `nextStele` | Where the next stone of the tale stands, in pixels walked, until its part is told. -1 means that no stone stands ahead. | Back to -1 | Back to -1 |
| `tale` | The id of the tale that the stones of this climb tell. | The next tale | The next tale |
| `talesTold` | The tales told, one bit for each id: 1 for Sisyphus, 2 for Typhon, 4 for Orpheus, 8 for Isis and Osiris. | Kept | Kept |
| `nextJoker` | Where the next joker stands by the path, in pixels walked. -1 means that none stands ahead yet. | A new joker ahead | A new joker ahead |
| `joker` | The id of the next joker. | The new joker | The new joker |
| `met.0` to `met.11` | How many times the player met each joker, by the id of the joker. A joker met once or more has the card face up, and the count tells which line comes next. | Kept | Kept |
| `jokersSeen` | The cards seen face up on the Jokers tab, one bit for each id: 1 for Diogenes, 2 for Humpty Dumpty, up to 2048 for Scheherazade. | Kept | Kept |
| `sound` | 1 when the sound is on, 0 when it is off. | Kept | Kept |
| `savedAt` | The time of the last save, in seconds since 1970. | Saved again | Saved again |

When a key is not there, the game uses a start value:

- `sound`: on.
- `summit`: no final climb.
- `quoteSeed`: a random number.
- `nextVision`: from 70 to 100 turns, at random, in a new game, at the start of each climb, and in a game saved before the visions. A count saved with other numbers is never more than the longest wait between visions now: 600 turns.
- `visionSeed`: a random number.
- `savedAt`: no time away.
- `nextObol`: 36 in a new game. A game saved before there was this key counts the obols of its climb, the spent ones too: each of the first four needs 36, made smaller by a blessing. After them, an obol needs the work per obol of the blessing. An obol saved with other numbers needs no more work than an obol can need now, and what was earned of it is kept.
- `summit`: a top saved with other numbers is no farther than the final climb is now.
- `stelesTold`: in a climb saved before the stones, the items bought in it, so that no stone tells an old part.
- `nextStele`: -1, no stone ahead.
- `tale`: 0, the tale of Sisyphus. An id that the pool does not have also tells the tale of Sisyphus.
- `nextJoker`: -1, so a joker is put by the path at once, 20 to 40 turns ahead.
- `joker`: 0, Diogenes. An id that the game does not have is Diogenes too.
- All other keys: 0.

## Ids that never change

Saved games know each line and each world by an id, not by a name in the game or in the code. An id never changes, so that saved games keep their purchases and their world.

- The lines have the ids `rock`, `beside`, `alone`, `blessing` and `beauty`. The Sidekicks and the Gods keep `beside` and `alone`, which they had before they got their names.
- The worlds have the ids 0 (Corinth), 1 (Mount Etna), 2 (The Underworld) and 3 (Cairo).
- The tales have the ids 0 (Sisyphus), 1 (Typhon), 2 (Orpheus) and 3 (Isis and Osiris).
- The jokers have the ids 0 (Diogenes), 1 (Humpty Dumpty), 2 (Red Riding Hood), 3 (Don Quixote), 4 (Pinocchio), 5 (Cleopatra), 6 (Sherlock Holmes), 7 (Hamlet), 8 (Count Dracula), 9 (Cthulhu), 10 (Isaac Newton) and 11 (Scheherazade).
- The game never saves the name of an item. The world and the tier of a line tell which item it is.
- The worlds that cost money are the entitlements `world_etna`, `world_underworld` and `world_cairo` in RevenueCat. A changed entitlement locks the worlds that players bought. The store products have the same names, except on Google Play, where The Underworld is `world_underworld2`.

A test fails when an id or a product changes. New lines and new worlds get new ids, and new worlds get new products.

## When the game saves

The game saves all keys at these times:

- Every second while something changes: the knob turns, an obol comes, or new wise words come.
- When the app goes to the background, and when the desktop window closes.
- After a purchase, after a dev screen button, at the top, after a move to another world, and when the Labors tab or the Jokers tab opens.

The sound setting saves only the `sound` key, at the moment that the player changes it.

## Time away

When the game starts, or comes back from the background, it reads `savedAt`. The god that the player owns then pushes for all of the time away. This adds obols and days. Hermes tells the player how much the god pushed.

Sisyphus does not climb while the app is closed: `position` changes only with the turns of the player. When the clock of the device goes back, the game adds nothing.
