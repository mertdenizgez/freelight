# Installation and first-run setup

Freelight 0.12 supports TV-remote PIN entry through a temporary keypad. Physical testing on Philips 65OLED810/12 verified re-pairing with fresh credentials, encrypted legacy migration and live TV/Bluetooth operation. A clean-install first run and standby/reboot still require verification.

## Requirements

- A compatible Philips Android/Google TV exposing JointSpace v6 and Ambilight color data. Tested reader hardware: Philips 65OLED810/12.
- A compatible Hue Bluetooth RGB bulb. The controller currently accepts LCA012; other models, IKEA Matter/Thread and Bridge streaming are not supported.
- The app installed on the TV. Store distribution is not configured yet; use a development APK for testing.

## First run

1. Open Freelight. An unpaired installation opens **TV connection**.
2. Select **Pair this TV**. The app contacts only this device’s own local TV API; no manual IP is needed.
3. Approve the explanation for the temporary PIN keypad. In Android’s **Display over other apps** settings, allow Freelight, then return and select **Pair this TV** again. This is a one-time permission grant and can be revoked later. It is not used during normal synchronization.
4. Keep the Philips PIN window open. Use the remote’s arrows/OK on the Freelight keypad at the bottom of the screen, or its numeric keys, to enter the four-digit code. Select **Confirm PIN** before the TV’s countdown ends. **Do not press Close in the Philips window**: on the tested firmware, closing it before entering the PIN returned `TIMEOUT`.
5. The keypad is removed after successful pairing, cancellation or expiry.
6. The app verifies authorization and Ambilight color access before saving the pair. An incorrect PIN, expired session, cancelled attempt or unsupported color API does not replace existing credentials.
7. Choose **Add a Bluetooth bulb**, grant Bluetooth permissions, scan, select a compatible bulb, set its position/brightness, and choose **Save and connect**.

The physical TV’s PIN flow and firmware support must still be validated for each supported hardware/firmware combination. Do not claim universal Philips compatibility from successful compilation or simulated tests.

## TV connection controls

Open **Menu → TV connection** to see whether fresh colors are arriving, start/stop synchronization, pair again or forget this TV. Re-pairing preserves an existing pair until the new one is fully validated. **Forget TV** asks for confirmation, stops the service and removes only TV pairing data; bulb registrations remain.

The encrypted pairing file is private and excluded from Android backups. Updates retain the package ID and local data. Legacy private `files/pairing.json` is migrated only after encrypted storage has been successfully written. Keep your existing signing key for APK updates.

## Bulb controls

On Home, focus the room scene, use Left/Right to select a bulb and OK to open settings. Or scroll down to the bulb cards. Names, five positions and independent 0–4× brightness are supported.

- **Start** enables automatic connection for a bulb.
- **Stop** pauses the current session and restores its saved previous light state; wake may resume it.
- **Disconnect** on the Bluetooth page disables automatic connection until Start is used.
- Home leaves the UI while the foreground service continues.
- **Stop synchronization** on the TV connection page, or Stop in the notification, disables the reader’s boot-start preference and stops the service.

Use **Menu → App settings** to choose language, read privacy information or view the license/About screen.

## Development installation

```sh
adb pair TV_IP:PAIRING_PORT
adb connect TV_IP:CONNECTION_PORT
adb -s TV_IP:CONNECTION_PORT install -r android/build/debug/Freelight-debug.apk
```

Use the actual ports shown by wireless debugging; the pairing and connection ports can differ. Accept the TV’s authorization prompt. This ADB connection is for development installation only, not normal pairing or synchronization.

The old `tools/provision-tv.py` is retained only for legacy/debug experiments. It is not part of the new-user workflow and cannot overwrite an encrypted pairing that the running app has already loaded. Use the in-app pairing controls instead.

## Troubleshooting

- Keypad permission unavailable: open Android settings → Apps → Special app access → Display over other apps. If Freelight is unavailable there, report the model/firmware; remote-only onboarding is not verified for that firmware. Do not close the Philips PIN window to reach an obscured input.
- No PIN/service: verify Philips model/firmware compatibility and local API availability. Other Android TVs do not automatically expose Philips Ambilight.
- Wrong/expired PIN: cancel and pair again. Your previous working pairing is preserved.
- Authorization rejected or TV certificate changed: re-pair through the TV connection page.
- Bulb cannot connect: check model, power, range, Bluetooth permissions and competing controllers; do not reset a working bulb just for discovery.
- Cold-boot delay: the TV’s API may start later than the app; the reader retries automatically.
- APK signature mismatch: use the original signing key. Do not uninstall a configured app merely to resolve a signing mismatch.

Diagnostics: `adb shell dumpsys activity service local.ambilight.reader/.ReaderService`. Redact IP/MAC addresses and custom names before sharing. Do not share TV keys, encrypted credential files or signing material.
