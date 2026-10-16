#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/../.."
tmp=$(mktemp -d)
trap 'rm -rf "$tmp"' EXIT
keytool -genkeypair -keystore "$tmp/test.p12" -storetype PKCS12 -storepass test-only-password -keypass test-only-password -alias test -keyalg RSA -keysize 2048 -validity 1 -dname 'CN=Local transport test' >/dev/null 2>&1
javac -d "$tmp/classes" tests/pairing/android/content/Context.java tests/pairing/org/json/*.java tests/pairing/local/ambilight/reader/TvCredentials.java src/local/ambilight/reader/TvProtocol.java src/local/ambilight/reader/LocalTvApi.java tests/transport/TransportTest.java
java -cp "$tmp/classes" local.ambilight.reader.TransportTest "$tmp/test.p12"
