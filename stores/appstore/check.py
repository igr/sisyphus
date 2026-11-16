"""Stops an upload to Apple when the app in the archive holds the dev screen, or code to buy something.

Apple rejects an app with hidden features (guideline 5.6 and 2.5.19), so App Store and TestFlight builds leave out the dev screen.
The game is free: no build holds RevenueCat or a question to buy a world.
Kotlin/Native keeps the texts of the code in UTF-16, and the names that Swift can call in UTF-8.
Usage: python3 -I stores/appstore/check.py build/Sisyphus.xcarchive
"""
import sys
from pathlib import Path

DEV_SCREEN = ["Voices and labors", "devOpen", "nearTheTop"]
BUYING = ["RevenueCat", "is yours forever"]

app = Path(sys.argv[1]) / "Products/Applications/Sisyphus.app/Sisyphus"
data = app.read_bytes()
found = [f'"{text}"' for text in DEV_SCREEN + BUYING if text.encode() in data or text.encode("utf-16-le") in data]
if found:
    sys.exit(f"The app holds the dev screen or code to buy something, so it cannot go to Apple: {', '.join(found)}.")
print("The app holds no dev screen and no code to buy something.")
