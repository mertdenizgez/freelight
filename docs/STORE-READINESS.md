# Store preparation — 0.12

The application code is being prepared for Google Play on Android TV. Nothing in this document claims a production review or store listing has been completed.

## Implemented in this version

- Native on-TV PIN pairing, without ADB, a manual IP address or a credential-file upload for new users.
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

## Still required before store submission

- Test new-user PIN pairing, incorrect PIN, cancellation, existing-install migration, bulb tracking, standby/wake and reboot on the physical Philips TV. Broader firmware/model coverage remains unverified.
- Choose the final package identity before the first Play upload. Current identity remains `local.ambilight.reader` to preserve development installations.
- Create and securely retain an upload key; configure Play App Signing. No production signing key is generated or committed by this change.
- Publish the privacy policy at a stable public URL and complete the Data safety, content rating, app access and foreground-service declarations.
- Create/review the store description, TV banner, icon and unaltered TV screenshots. State the supported hardware explicitly.
- Supply reviewer instructions for the hardware-dependent setup. No developer-owned TV credentials should be supplied as a substitute for pairing the reviewer’s hardware.
- Complete the required testing track/account verification and Google’s TV review. GPLv3 source corresponding to distributed binaries must remain available with required notices.

## Reference documentation

- [TV distribution](https://developer.android.com/training/tv/publishing/distribute)
- [TV quality](https://developer.android.com/docs/quality-guidelines/tv-app-quality)
- [Connected-device foreground service](https://developer.android.com/develop/background-work/services/fgs/service-types#connected-device)
- [Command-line AAB builds](https://developer.android.com/build/building-cmdline)
- [JointSpace protocol reference implementation, reviewed for wire fields](https://github.com/eslavnov/pylips/blob/master/pylips.py)

The Philips PIN signature uses a public protocol HMAC seed; it is not a developer/user TV authentication key. The implementation is written for this project. Keep actual per-TV credentials out of source and bug reports.
