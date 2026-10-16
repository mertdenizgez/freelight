# Changelog

## 0.12

- Added experimental native on-TV PIN pairing and explicit TV connection controls. A temporary, user-authorized overlay keypad supports remote-only PIN entry without closing the Philips activity; physical-remote re-pairing passed on Philips 65OLED810/12.
- Added encrypted TV credential storage, legacy migration and local certificate pinning.
- Preserved working credentials during failed, cancelled or expired pairing attempts.
- Added privacy/About screens and 27 new strings in each of the 18 languages.
- Switched the foreground service to the connected-device type with permission gating.
- Hid the unfinished Bridge workflow and added separate debug/release APK/AAB builds.
- Added protocol vectors, pairing transaction tests, a privacy policy and store-readiness documentation.
- Verified existing-install encrypted migration and live TV/BLE resumption on the Philips TV. Fresh-identity PIN re-pairing passed with the temporary keypad; clean-install onboarding and standby/reboot verification remain pending.
- Retry POST Digest authentication over a fresh connection; add real local TLS/HTTP regression coverage and respect server-reported PIN timeout.

## 0.11

- Added 18 interface languages, system language detection, English fallback and a manual language selector.
- Added right-to-left Arabic layouts while keeping physical bulb positions unchanged.
- Increased button spacing and room-scene height; reserved space above the TV for upper bulbs and labels.
- Added a subtle live-color tint to the bulb illustration.

## Earlier development

- Introduced the Freelight name, logo, warm light theme and room-based Home screen.
- Added per-bulb names, five positions and independent 0–4× brightness controls.
- Added separate bulb sessions, group controls and persistent connection preferences.
- Implemented direct LCA012 Bluetooth color output, saved-light restoration and wake/restart recovery.
- Moved color reading onto the TV and removed continuous Android frame logging.

## Source-sharing preparation

- Licensed the project under GNU GPL v3.0 only (`GPL-3.0-only`).

- Added English setup, architecture, build and contribution documentation.
- Added an own-TV credential provisioning helper and a single host-side test entry point.
- Made the development build generate a local signing key on first use.
- Excluded personal session files, credentials, signing material and generated APKs from the sharing snapshot.

Source-sharing preparation does not change the TV app’s version or color-control behavior.
