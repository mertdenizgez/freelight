# Freelight privacy policy

Effective date: October 16, 2026. This policy describes Freelight 0.12.

Freelight is an independent application maintained through [mertdenizgez/freelight](https://github.com/mertdenizgez/freelight). Privacy questions can be submitted through [the project issue tracker](https://github.com/mertdenizgez/freelight/issues). Do not post TV authentication keys or other secrets in an issue.

## What the application processes

Freelight reads Ambilight RGB zones from the local API of the TV on which it is installed. It sends matching color, power and brightness commands to compatible Bluetooth bulbs. These operations do not capture screen images, video or audio. The current RGB frame and bounded troubleshooting state are held in memory, without continuous frame history or screenshot recording.

The application has no developer-operated server, analytics SDK, advertising SDK or cloud account. It does not automatically upload your RGB data, credentials, bulb identifiers or settings to the developer.

## On-device storage

TV pairing credentials are encrypted with AES-GCM using a device-bound key in Android Keystore. The encrypted file is stored in the app’s private, no-backup directory. The TV certificate fingerprint is saved with the pairing. Legacy installations migrate their old private pairing file after successfully writing encrypted storage; the original plaintext file is then removed.

Other local settings include Bluetooth bulb addresses and names, optional custom names, positions, brightness multipliers, automatic connection preferences, previous light state and interface language. These preferences are stored in private Android application storage. App backup is disabled.

TV PIN pairing requests and pending credentials are held in memory until verified. Failed or cancelled attempts do not replace a previously saved working pairing.

## Local connections and permissions

TV communication is restricted to the device’s own loopback and current local IPv4 interface addresses. During pairing, Freelight observes the TV’s local self-signed certificate and saves its fingerprint after successful authorization and color-access verification. Subsequent connections require that fingerprint. Legacy credentials without a fingerprint are pinned after their first successful local authenticated read. This is local trust establishment, not public certificate-authority validation.

Bluetooth permissions are used to discover, connect to and control compatible bulbs. On older Android versions, the platform requires location permission for Bluetooth discovery; Freelight does not use that permission to determine your physical location. Foreground-service and boot permissions support user-enabled synchronization and reconnection. There is no camera, microphone, accessibility-service or screen-capture permission.

The optional display-over-other-apps permission is requested through Android settings for remote-only PIN entry. It displays a temporary Freelight keypad below the Philips PIN window; it does not read the TV window, capture its contents or fill the code automatically. The user enters the PIN with the remote. The keypad is removed on completion, cancellation or expiry and is not used during normal synchronization. The permission can be revoked in Android settings.

## Retention and deletion

Preferences remain on the device until you change them, clear application data or uninstall. **TV connection → Forget TV** deletes the TV pairing and stops synchronization, while preserving bulb registrations. **Disconnect** disables automatic connection for that bulb but keeps its registration/settings. Clear Freelight’s application data in Android settings to remove all stored application data. No developer-held cloud copy exists to delete.

## External services and voluntary reports

Following a GitHub support/source link uses GitHub, which has its own privacy policy. If you voluntarily submit screenshots or diagnostics, redact local IP/MAC addresses, bulb names, TV keys and any other personal data. The app does not submit these reports automatically.

## Updates

This policy is updated when application behavior changes. The effective date identifies the current policy version.
