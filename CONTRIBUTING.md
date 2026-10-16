# Contributing

Freelight is currently a small hardware-specific experiment. Useful contributions include native-speaker translation review, reproducible compatibility reports, BLE recovery improvements, and a first-run TV pairing flow.

Before changing behavior, read [the architecture](docs/ARCHITECTURE.md) and run the [host-side tests](docs/BUILDING.md#run-the-host-side-tests). Keep color history in RAM, keep each bulb’s state independent, and do not reset the TV Bluetooth radio to recover one bulb connection.

For a device report, include TV model, Android/firmware version, bulb model, app version, the action that failed, and what you expected. State whether the problem appears after a cold boot, standby/wake or while streaming. Redact credentials, IP/MAC addresses, custom bulb names and other personal identifiers from logs/screenshots.

Do not claim support for a new bulb solely because it advertises over BLE. Provide protocol/model checks and a real hardware test, including restore behavior. Multiple simulated controllers are not evidence of simultaneous physical connections.

Contributions to Freelight are provided under the project’s GNU GPL v3.0-only license. Submit only work you have the right to contribute under these terms, and preserve any applicable third-party notices. See [LICENSE](LICENSE).
