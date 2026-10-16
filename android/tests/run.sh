#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
test_build=$(mktemp -d)
trap 'rm -rf "$test_build"' EXIT
src=src/local/ambilight/reader
javac -d "$test_build" "$src/HueBleColor.java" "$src/BulbPosition.java" \
  "$src/LanguagePolicy.java" "$src/RoomGeometry.java" \
  tests/ColorTest.java tests/PositionBrightnessTest.java tests/LocaleGeometryTest.java
for suite in ColorTest PositionBrightnessTest LocaleGeometryTest; do
  java -cp "$test_build" "local.ambilight.reader.$suite"
done
bash tests/fleet/run.sh
python3 tests/check-translations.py
python3 tests/test-provisioning.py
