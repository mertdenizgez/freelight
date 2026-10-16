#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
test_build=$(mktemp -d)
trap 'rm -rf "$test_build"' EXIT
src=src/local/ambilight/reader
javac -d "$test_build" "$src/HueBleColor.java" "$src/BulbPosition.java" \
  "$src/CredentialEnvelope.java" "$src/TvProtocol.java" "$src/LanguagePolicy.java" "$src/RoomGeometry.java" \
  tests/ColorTest.java tests/PositionBrightnessTest.java tests/LocaleGeometryTest.java tests/TvProtocolTest.java tests/CredentialEnvelopeTest.java
for suite in ColorTest PositionBrightnessTest LocaleGeometryTest TvProtocolTest CredentialEnvelopeTest; do
  java -cp "$test_build" "local.ambilight.reader.$suite"
done
bash tests/fleet/run.sh
bash tests/pairing/run.sh
python3 tests/check-translations.py
python3 tests/test-provisioning.py

if test "${FREELIGHT_TRANSPORT_TESTS:-0}" = 1; then bash tests/transport/run.sh; fi
