# Build and test

## Requirements

- Java 17 (including `javac`, `jar`, and `keytool`).
- Android SDK **platforms;android-34** and **build-tools;34.0.0**.
- Python 3 and Bash.
- Android SDK platform-tools (`adb`) for provisioning and device testing.

The build script supports macOS/Linux-style development environments. Native Windows execution is not tested. There is no Gradle wrapper or Android Studio project configuration.

Install the SDK components using your existing SDK manager, then set its directory:

```sh
sdkmanager "platforms;android-34" "build-tools;34.0.0" "platform-tools"
export ANDROID_SDK_ROOT="/absolute/path/to/your/android-sdk"
export PATH="$ANDROID_SDK_ROOT/platform-tools:$PATH"
bash android/build.sh
```

The script compiles Java, creates DEX bytecode, packages resources, aligns the APK, signs it, and verifies the signature. Output: **android/build/Freelight-TV.apk**.

## Development signing

The first build creates `private/debug.jks` with a local development key if one does not already exist. The password is the standard development value `android`; this is not release signing. The key is ignored by Git. Subsequent builds reuse it, so APK updates keep the same identity.

For an existing installation, copy **your original key** into this local path before building. Never commit or share it. New clones generate different keys and therefore cannot update each other’s installations.

This app is currently debuggable to support ADB credential provisioning. A production release needs a first-run credential workflow and a deliberate release-signing/debuggable policy. This build script does not pretend to provide that release process.

## Run the host-side tests

```sh
bash android/tests/run.sh
```

Tests require only a JDK and Python 3, not a TV or Android SDK. They cover:

- RGB-to-xy conversion, primary colors, byte order and 4,096 color samples.
- Five positions, corner sampling, sparse layouts and 4,352 brightness cases.
- Language policy, regional tags, English fallback and upper-bulb/label clearance.
- Independent fleet behavior using simulated controllers and in-memory preferences.
- Completeness and formatting consistency across 18 translation catalogs.

Simulated fleet tests do not verify real simultaneous radio connections. `TestHarness.java` is an older Android instrumentation helper for the Bridge client; it is not registered or run by this host-side suite.

## Edit translations

The translation source is `android/tools/generate-translations.py`. Its embedded dictionaries generate both `android/tools/catalog.json` and the Android `res/values*/strings.xml` files:

```sh
python3 android/tools/generate-translations.py
bash android/tests/run.sh
```

Edit the generator rather than only the generated JSON/XML. Keep all language catalogs complete and preserve positional format tokens such as `%1$d` and `%1$.2f`. Native-speaker review is welcome.
