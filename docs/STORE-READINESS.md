# Store preparation — 0.12

The application code is being prepared for Google Play on Android TV. Nothing in this document claims a production review or store listing has been completed.

## Implemented in this version

- Experimental native PIN pairing with a temporary, user-authorized overlay keypad for remote-only entry. Physical-TV re-pairing and permission access were verified on the tested Philips firmware.
- A TV connection page, retry/re-pair controls, first-run routing and explicit Forget TV confirmation.
- Candidate credentials committed only after the grant and a valid Ambilight frame; cancellation/expiry/errors preserve an existing pair.
- Device-bound AES-GCM credential storage and migration from legacy private pairing.json.
- Local-only API transport with a certificate fingerprint saved at pairing (or first successful legacy authenticated read).
- A `connectedDevice` foreground service gated by Bluetooth runtime permissions, with explicit Start/Stop controls.
- The incomplete Hue Bridge workflow is hidden from the production UI; Bridge client code remains for future development.
- In-app privacy and About/license screens, with all new UI strings in the existing 18 languages.
- Separate debuggable development and non-debuggable release manifests, an unsigned validated AAB output, optional upload-key signing, and language-split disabling so manual language selection remains complete.

## Verification boundary

Automated tests cover protocol vectors, expiry and failure/cancellation transaction behavior using simulated TV responses. A clean release bundle is validated with bundletool. These tests do not prove that every Philips firmware displays/accepts the PIN or that Android Keystore migration works on every physical device. Hardware onboarding and upgrade tests must be recorded before production submission.

## Physical-TV validation so far

On Philips 65OLED810/12, the 0.11 → 0.12 development update retained bulb name, position, brightness and automatic connection preferences. Legacy credentials migrated to an encrypted no-backup envelope and the old plaintext file was removed. Subsequent app updates read the encrypted pair successfully. Live TV reading and Bluetooth synchronization resumed with no new reader/BLE errors during checks.

The original modal PIN input was covered by the Philips activity. Waiting for that activity to expire, closing it with the physical remote, and reordering Freelight to the foreground returned the TV’s explicit TIMEOUT response. The replacement `TYPE_APPLICATION_OVERLAY` keypad keeps the Philips activity visible. The user enabled its permission in Android settings, entered the PIN with the physical remote without closing the Philips window, and confirmed success. The app reported `paired`, replaced the encrypted credential envelope and continued reading RGB and controlling the bulb. The temporary keypad was removed. After a process stop/relaunch, the encrypted new pair loaded successfully and RGB/Bluetooth synchronization resumed automatically with no reader/BLE errors observed. Bulb registration, custom name, left position, 2× brightness and automatic preference remained unchanged. This verifies a fresh authenticated identity through re-pairing on an existing installation; a clean-install first run is still unverified.

A POST Digest retry originally encountered EOF; the transport now retries POST authentication on a fresh connection, covered by a real local TLS regression test. Working old pairing was preserved throughout unsuccessful PIN attempts. No TV authentication keys or PIN screenshots are included in the repository or release artifacts.

## Still required before store submission

- Test a clean-install first run, incorrect-PIN/cancellation behavior through the new keypad, standby/wake and full TV reboot on physical hardware. Existing-install migration, fresh-identity re-pairing and live single-bulb tracking have passed. Broader firmware/model coverage and simultaneous physical bulbs remain unverified.
- Choose the final package identity before the first Play upload. Current identity remains `local.ambilight.reader` to preserve development installations.
- Create and securely retain an upload key; configure Play App Signing. No production signing key is generated or committed by this change.
- Review the special overlay permission and its disclosed, setup-only use against current Play policy; verify the system permission screen is reachable using a TV remote.
- Publish the privacy policy at a stable public URL and complete the Data safety, content rating, app access and foreground-service declarations.
- Create/review the store description, TV banner, icon and unaltered TV screenshots. State the supported hardware explicitly.
- Supply reviewer instructions for the hardware-dependent setup. No developer-owned TV credentials should be supplied as a substitute for pairing the reviewer’s hardware.
- Complete the required testing track/account verification and Google’s TV review. GPLv3 source corresponding to distributed binaries must remain available with required notices.

## Reference documentation

- [Overlay permission and user approval](https://developer.android.com/reference/android/provider/Settings#canDrawOverlays(android.content.Context))
- [Play permission policy](https://support.google.com/googleplay/android-developer/answer/16558241)
- [TV distribution](https://developer.android.com/training/tv/publishing/distribute)
- [TV quality](https://developer.android.com/docs/quality-guidelines/tv-app-quality)
- [Connected-device foreground service](https://developer.android.com/develop/background-work/services/fgs/service-types#connected-device)
- [Command-line AAB builds](https://developer.android.com/build/building-cmdline)
- [JointSpace protocol reference implementation, reviewed for wire fields](https://github.com/eslavnov/pylips/blob/master/pylips.py)

The Philips PIN signature uses a public protocol HMAC seed; it is not a developer/user TV authentication key. The implementation is written for this project. Keep actual per-TV credentials out of source and bug reports.
