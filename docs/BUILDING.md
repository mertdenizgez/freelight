# Build and test

Use Java 17, Python 3, Bash, Android SDK `platforms;android-34`, `build-tools;34.0.0` and platform-tools for device testing. No Gradle wrapper or third-party Android runtime dependency is required. macOS/Linux-style environments are supported; native Windows execution is untested.

```sh
export ANDROID_SDK_ROOT="/absolute/path/to/android-sdk"
bash android/build.sh debug
bash android/tests/run.sh
```

Debug output: `android/build/debug/Freelight-debug.apk`. The first debug build creates an ignored local `private/debug.jks` if absent, using the standard development password `android`. Preserve the original key for updates to an existing installation. Different keys cannot update one another’s installations. The source manifest is non-debuggable; only the generated debug variant enables debugging.

## Release APK and AAB

Install the official [bundletool](https://github.com/google/bundletool/releases) JAR and the macOS/Linux AAPT2 executable from [Google Maven](https://maven.google.com/web/index.html#com.android.tools.build:aapt2). Tested tool versions: bundletool 1.18.3 and Google Maven AAPT2 9.4.1-15978811. These are local development tools and are not bundled with the app or committed to source.

```sh
export FREELIGHT_AAPT2="/absolute/path/to/aapt2"
export FREELIGHT_BUNDLETOOL="/absolute/path/to/bundletool-all.jar"
bash android/build.sh release
```

This creates `android/build/release/Freelight-unsigned.apk` and `Freelight-unsigned.aab`. The script validates the bundle with bundletool. These unsigned artifacts are **not ready for Play upload**. No development signing key is used for a release build.

For signing, provide your own keystore and configure these environment variables through a private local environment or CI secret store:

- `FREELIGHT_RELEASE_KEYSTORE`: absolute keystore path.
- `FREELIGHT_RELEASE_ALIAS`: upload-key alias.
- `FREELIGHT_STORE_PASSWORD`: keystore password.
- `FREELIGHT_KEY_PASSWORD`: key password.

Then rerun the release build. It produces signed `Freelight-release.apk` and `Freelight-release.aab`, verifies their signatures, and retains the unsigned intermediates locally. Passwords are read from environment variables by signing tools; do not commit them or echo them into logs. Create/configure production signing and Play App Signing separately before publication.

The AAB disables language splits so all 18 languages remain available when the user changes language inside the app. There are no native `.so` libraries; confirm generated APK/device compatibility as part of release testing.

## Host-side tests

`bash android/tests/run.sh` requires only the JDK and Python 3. It covers RGB conversion and 4,096 samples, five positions and 4,352 brightness cases, locale policy and geometry, simulated bulb-fleet isolation, matching translation resources, provisioning validation, and the new TV pairing protocol/state tests. AES-GCM envelope tests cover round trips, independent IVs, tampering, wrong keys and malformed storage.

The pairing suite uses small Android/JSON/API test doubles to test the **real TvPairing transaction code**. It does not test the Android Keystore implementation, real TLS sockets, Philips firmware PIN UI or physical BLE radio concurrency. `TestHarness.java` remains an older, unregistered Bridge instrumentation helper.

## Translations

Edit `android/tools/generate-translations.py` for original UI strings and `android/tools/setup-translations.py` for new setup/privacy strings. Regenerate XML and JSON, then run tests:

```sh
python3 android/tools/generate-translations.py
bash android/tests/run.sh
```

Keep every language complete and preserve positional formatting tokens. Native-speaker review is welcome.

## Optional local TLS/HTTP regression test

`bash android/tests/transport/run.sh` runs the real production transport against a temporary TLS server bound only to 127.0.0.1:1926. It needs permission to open that local port, which must be free. A temporary self-signed test key is generated and removed automatically. Set `FREELIGHT_TRANSPORT_TESTS=1` when running the host suite to include it.

This covers GET Digest keep-alive, fresh-connection POST Digest retries with unchanged bodies, oversized response rejection, certificate-pin mismatch and the endpoint allowlist. Its JSON double carries raw bodies; JSON parsing and real TV firmware behavior are not simulated by this test.
