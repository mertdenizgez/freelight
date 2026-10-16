# Architecture

## Local color reader

`ReaderService` is a foreground service with a worker thread targeting roughly five successful reads per second. It uses the TV’s authenticated HTTPS `/6/ambilight/measured` endpoint on port 1926, with a persistent transport and HTTP Digest authentication. It tries loopback and active local IPv4 interfaces, retries failed reads, and rejects malformed RGB data.

The latest frame is held in RAM. The Android app does not record frame history. It stores only credentials, bulb/session preferences and saved previous-light values. Old frame-log files from earlier prototypes are cleaned up on service creation.

The TLS implementation trusts the TV’s self-signed certificate without verification. This is a known development limitation, not certificate pinning or an authenticated trust-store setup.

## Per-bulb control

`HueFleet` owns registered controllers and a separate discovery controller. Each `HueBle` has its own GATT session, serialized operation queue, saved light state, reconnect state, position and brightness settings. A failed bulb session does not intentionally close another bulb’s session. Hardware radio capacity still limits real concurrency.

`BulbPosition` selects zone averages. Corners blend the upper portion of a side with the corresponding top portion. `HueBleColor` converts sampled RGB into the controller’s color representation and clamps brightness. Data older than two seconds is not forwarded.

Confirmed power/brightness state drives the UI’s on/off appearance. The bulb illustration is an approximation of the commanded color, not a measurement of the physical lamp’s output. Previous-state restoration is best effort and depends on successful Bluetooth reads/writes and the bulb remaining reachable.

## Native TV interface

`MainActivity` builds native Android views, a left drawer, room preview and per-bulb cards. `RoomScene` draws bundled room/bulb images and live glows on a Canvas. `RoomGeometry` keeps upper bulbs and labels clear of the TV. The UI loads local images rather than downloading assets at runtime.

`LanguagePolicy` resolves system/manual language choices. `I18n` creates a localized context without changing the TV’s system language. Arabic layout direction does not mirror physical bulb coordinates.

`BootReceiver` attempts to restart an enabled service after boot or package replacement. TV wake events are handled by the BLE controllers. Boot timing and Android background restrictions vary by firmware; they remain part of device testing.

## Bridge boundary

`HueClient` supports local Bridge discovery/pairing-related configuration and Entertainment area retrieval. There is no DTLS Entertainment stream connecting the reader to that Bridge configuration. The UI is preparation for future work, not a second working light-output backend.

## Useful source files

| File | Responsibility |
| --- | --- |
| `ReaderService.java` | Local API transport, authentication, latest RGB state, foreground lifecycle |
| `HueFleet.java` | Registry, independent sessions and group controls |
| `HueBle.java` | Discovery, model check, queued BLE I/O, restore and reconnect |
| `HueBleColor.java` / `BulbPosition.java` | Color conversion, brightness and region sampling |
| `MainActivity.java` | UI, permissions, settings and navigation |
| `RoomScene.java` / `RoomGeometry.java` | Room rendering and bulb placement |
| `LanguagePolicy.java` / `I18n.java` | Locale policy and localized resources |
| `HueClient.java` | Incomplete Bridge backend configuration |
