# Saved data

The game saves all of its data on the device. It sends nothing over the network, and there is no account.

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
| `shop.rock` | The tier bought in the Rock line: 0 to 3. | Back to 0 | Back to 0 |
| `shop.beside` | The tier bought in the Sidekicks line: 0 to 3. | Back to 0 | Back to 0 |
| `shop.alone` | The tier bought in the Gods line: 0 to 3. | Back to 0 | Back to 0 |
| `shop.blessing` | The tier bought in the Blessings line: 0 to 3. | Back to 0 | Back to 0 |
| `shop.beauty` | The tier bought in the Beauty line: 0 to 3. | Back to 0 | Back to 0 |
| `summit` | Where the hill ends, in pixels walked. -1 means that the final climb has not started. | Back to -1 | Back to -1 |
| `climbs` | How many times Sisyphus reached the top. | Plus 1 | Kept |
| `place` | The id of the world: 0 is Corinth, 1 is Mount Etna, 2 is The Underworld. | Kept | The new world |
| `quoteSeed` | The number that sets the order of the wise words. | Kept | Kept |
| `quotesDrawn` | How many wise words came so far. | Back to 0 | Back to 0 |
| `sound` | 1 when the sound is on, 0 when it is off. | Kept | Kept |
| `savedAt` | The time of the last save, in seconds since 1970. | Saved again | Saved again |

When a key is not there, the game uses a start value:

- `sound`: on.
- `summit`: no final climb.
- `quoteSeed`: a random number.
- `savedAt`: no time away.
- All other keys: 0.

## Ids that never change

Saved games know each line and each world by an id, not by a name in the game or in the code. An id never changes, so that saved games keep their purchases and their world.

- The lines have the ids `rock`, `beside`, `alone`, `blessing` and `beauty`. The Sidekicks and the Gods keep `beside` and `alone`, which they had before they got their names.
- The worlds have the ids 0 (Corinth), 1 (Mount Etna) and 2 (The Underworld).
- The game never saves the name of an item. The tier of a line tells which item it is.

A test fails when an id changes. New lines and new worlds get new ids.

## When the game saves

The game saves all keys at these times:

- Every second while something changes: the knob turns, or new wise words come.
- When the app goes to the background, and when the desktop window closes.
- After a purchase, after a dev screen button, at the top, and after a move to another world.

The sound setting saves only the `sound` key, at the moment that the player changes it.

## Time away

When the game starts, or comes back from the background, it reads `savedAt`. The god that the player owns then pushes for all of the time away. This adds obols and days. Hermes tells the player how much the god pushed.

Sisyphus does not climb while the app is closed: `position` changes only with the turns of the player. When the clock of the device goes back, the game adds nothing.
