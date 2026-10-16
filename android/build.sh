#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
mode="${1:-debug}"
case "$mode" in debug|release) ;; *) echo 'Usage: bash android/build.sh [debug|release]' >&2; exit 1;; esac
: "${ANDROID_SDK_ROOT:?Set ANDROID_SDK_ROOT to your Android SDK directory}"
bt="$ANDROID_SDK_ROOT/build-tools/34.0.0"
platform="$ANDROID_SDK_ROOT/platforms/android-34/android.jar"
for tool in javac jar keytool python3; do command -v "$tool" >/dev/null || { echo "Missing tool: $tool" >&2; exit 1; }; done
for tool in aapt d8 zipalign apksigner; do test -x "$bt/$tool" || { echo "Missing SDK tool: $bt/$tool" >&2; exit 1; }; done
test -f "$platform" || { echo 'Missing Android SDK platform 34' >&2; exit 1; }
if test "$mode" = release; then
 : "${FREELIGHT_AAPT2:?Set FREELIGHT_AAPT2 to Google Maven AAPT2 executable}"
 : "${FREELIGHT_BUNDLETOOL:?Set FREELIGHT_BUNDLETOOL to the official bundletool JAR}"
 test -x "$FREELIGHT_AAPT2"
 test -f "$FREELIGHT_BUNDLETOOL"
fi
build="build/$mode"
rm -rf "$build/classes" "$build/dex"
mkdir -p "$build/classes" "$build/dex"
python3 - "$mode" "$build/AndroidManifest.xml" <<'PY'
import sys,xml.etree.ElementTree as E
E.register_namespace('android','http://schemas.android.com/apk/res/android')
tree=E.parse('AndroidManifest.xml');tree.getroot().find('application').set('{http://schemas.android.com/apk/res/android}debuggable','true' if sys.argv[1]=='debug' else 'false');tree.write(sys.argv[2],encoding='utf-8',xml_declaration=True)
PY
"$bt/aapt" package -f -M "$build/AndroidManifest.xml" -S res -I "$platform" -F "$build/unsigned.apk"
javac -source 8 -target 8 -classpath "$platform" -d "$build/classes" src/local/ambilight/reader/*.java
jar cf "$build/classes.jar" -C "$build/classes" .
"$bt/d8" --lib "$platform" --output "$build/dex" "$build/classes.jar"
python3 - "$build" <<'PY'
import sys,zipfile,pathlib
root=pathlib.Path(sys.argv[1])
with zipfile.ZipFile(root/'unsigned.apk','a') as z:
 for dex in (root/'dex').glob('classes*.dex'):z.write(dex,dex.name)
PY
"$bt/zipalign" -f 4 "$build/unsigned.apk" "$build/Freelight-unsigned.apk"
if test "$mode" = debug; then
 keystore='../private/debug.jks'
 if ! test -f "$keystore"; then
  mkdir -p ../private
  (umask 077; keytool -genkeypair -keystore "$keystore" -storepass android -keypass android -alias androiddebugkey -keyalg RSA -keysize 2048 -validity 10000 -dname 'CN=Freelight Development' >/dev/null)
 fi
 "$bt/apksigner" sign --ks "$keystore" --ks-pass pass:android --out "$build/Freelight-debug.apk" "$build/Freelight-unsigned.apk"
 "$bt/apksigner" verify "$build/Freelight-debug.apk"
 echo "Built and verified: android/$build/Freelight-debug.apk"
else
 # Compile resources with Google Maven AAPT2, then build the base module.
 "$FREELIGHT_AAPT2" compile --dir res -o "$build/compiled-res.zip"
 "$FREELIGHT_AAPT2" link --proto-format -o "$build/proto.apk" -I "$platform" --manifest "$build/AndroidManifest.xml" -R "$build/compiled-res.zip" --auto-add-overlay
 python3 - "$build" <<'PY'
import pathlib,sys,zipfile,json
root=pathlib.Path(sys.argv[1])
with zipfile.ZipFile(root/'proto.apk') as proto,zipfile.ZipFile(root/'base.zip','w',zipfile.ZIP_DEFLATED) as base:
 for name in proto.namelist():
  if name=='AndroidManifest.xml':base.writestr('manifest/AndroidManifest.xml',proto.read(name))
  elif name=='resources.pb' or name.startswith('res/'):base.writestr(name,proto.read(name))
 for dex in (root/'dex').glob('classes*.dex'):base.write(dex,'dex/'+dex.name)
(root/'BundleConfig.json').write_text(json.dumps({'optimizations':{'splitsConfig':{'splitDimension':[{'value':'LANGUAGE','negate':True}]}}}))
PY
 java -jar "$FREELIGHT_BUNDLETOOL" build-bundle --modules="$build/base.zip" --config="$build/BundleConfig.json" --output="$build/Freelight-unsigned.aab" --overwrite
 java -jar "$FREELIGHT_BUNDLETOOL" validate --bundle="$build/Freelight-unsigned.aab"
 if test -n "${FREELIGHT_RELEASE_KEYSTORE:-}"; then
  : "${FREELIGHT_RELEASE_ALIAS:?Set release key alias}"
  : "${FREELIGHT_STORE_PASSWORD:?Set release keystore password}"
  : "${FREELIGHT_KEY_PASSWORD:?Set release key password}"
  "$bt/apksigner" sign --ks "$FREELIGHT_RELEASE_KEYSTORE" --ks-key-alias "$FREELIGHT_RELEASE_ALIAS" --ks-pass env:FREELIGHT_STORE_PASSWORD --key-pass env:FREELIGHT_KEY_PASSWORD --out "$build/Freelight-release.apk" "$build/Freelight-unsigned.apk"
  "$bt/apksigner" verify "$build/Freelight-release.apk"
  jarsigner -keystore "$FREELIGHT_RELEASE_KEYSTORE" -storepass:env FREELIGHT_STORE_PASSWORD -keypass:env FREELIGHT_KEY_PASSWORD -signedjar "$build/Freelight-release.aab" "$build/Freelight-unsigned.aab" "$FREELIGHT_RELEASE_ALIAS"
  jarsigner -verify "$build/Freelight-release.aab"
  echo "Built signed release APK/AAB under android/$build"
 else
  echo "Validated unsigned release AAB: android/$build/Freelight-unsigned.aab"
  echo 'Not ready for Play upload: configure your own upload-signing key. No development key was used.'
 fi
fi
