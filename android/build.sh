#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
: "${ANDROID_SDK_ROOT:?Set ANDROID_SDK_ROOT to your Android SDK directory}"
bt="$ANDROID_SDK_ROOT/build-tools/34.0.0"
platform="$ANDROID_SDK_ROOT/platforms/android-34/android.jar"
for tool in javac jar keytool python3; do
  command -v "$tool" >/dev/null || { echo "Missing tool: $tool" >&2; exit 1; }
done
for tool in aapt d8 zipalign apksigner; do
  test -x "$bt/$tool" || { echo "Missing SDK tool: $bt/$tool" >&2; exit 1; }
done
test -f "$platform" || { echo "Missing Android SDK platform 34" >&2; exit 1; }

# Reuse the original local key when updating an existing installation.
keystore="../private/debug.jks"
if ! test -f "$keystore"; then
  mkdir -p ../private
  (umask 077; keytool -genkeypair -keystore "$keystore" -storepass android \
    -keypass android -alias androiddebugkey -keyalg RSA -keysize 2048 \
    -validity 10000 -dname "CN=Freelight Development" >/dev/null)
  echo "Created a local development signing key. Keep it for future updates."
fi
rm -rf build/classes build/dex
mkdir -p build/classes build/dex
"$bt/aapt" package -f -M AndroidManifest.xml -S res -I "$platform" -F build/unsigned.apk
javac -source 8 -target 8 -classpath "$platform" -d build/classes src/local/ambilight/reader/*.java
jar cf build/classes.jar -C build/classes .
"$bt/d8" --lib "$platform" --output build/dex build/classes.jar
python3 - <<'PY'
import zipfile
with zipfile.ZipFile('build/unsigned.apk', 'a') as package:
    package.write('build/dex/classes.dex', 'classes.dex')
PY
"$bt/zipalign" -f 4 build/unsigned.apk build/aligned.apk
"$bt/apksigner" sign --ks "$keystore" --ks-pass pass:android --out build/Freelight-TV.apk build/aligned.apk
"$bt/apksigner" verify build/Freelight-TV.apk
echo "Built and verified: android/build/Freelight-TV.apk (development build)"
