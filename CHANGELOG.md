# Changelog

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
