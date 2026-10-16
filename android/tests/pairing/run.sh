#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/../.."
tmp=$(mktemp -d)
trap 'rm -rf "$tmp"' EXIT
javac -d "$tmp" tests/pairing/android/content/*.java tests/pairing/android/os/*.java tests/pairing/org/json/*.java tests/pairing/local/ambilight/reader/*.java src/local/ambilight/reader/TvPairing.java src/local/ambilight/reader/TvProtocol.java src/local/ambilight/reader/TvFrame.java
java -cp "$tmp" local.ambilight.reader.PairingTest
