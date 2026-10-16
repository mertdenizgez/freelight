# Installation and setup

## Before you begin

You need a Philips Android/Google TV that exposes the authenticated JointSpace v6 Ambilight API, a compatible Hue Bluetooth RGB bulb, ADB access to the TV, and a valid pairing response for **your own TV**. The tested combination is 65OLED810/12 + Hue LCA012.

TV pairing is not implemented in this app or its helper scripts. Obtain the pairing response through a compatible JointSpace pairing client that completes the TV’s on-screen PIN confirmation. Keep the full response locally; the fields Freelight reads are:

```json
{
  "device": { "id": "YOUR_PAIRED_DEVICE_ID" },
  "response": { "auth_key": "YOUR_TV_AUTH_KEY" }
}
```

These are placeholders, not working credentials. A Hue Bridge key or Bluetooth PIN cannot replace this TV pairing response. Do not reuse credentials from another TV.

## Install the development APK

Build the APK using [BUILDING.md](BUILDING.md). Enable developer options and debugging on the TV, then use the IP and port shown by the TV’s debugging screen:

```sh
# When wireless debugging requires pairing, use its separate pairing port:
adb pair TV_IP:PAIRING_PORT

# Use the connection port, which may differ from the pairing port:
adb connect TV_IP:CONNECTION_PORT
adb -s TV_IP:CONNECTION_PORT install -r android/build/Freelight-TV.apk
```

Approve the TV’s authorization dialog. Do not assume the port is 5555. Replace each placeholder with your device’s current address. ADB connectivity is only needed for setup and development, not normal synchronization.

Updates must use the same signing key as the installed app. A separately generated key cannot update an existing installation. Keep your original development key locally; do not uninstall a working installation just to resolve a signature mismatch, as uninstalling erases its stored settings and credentials.

## Provision the TV credentials

Keep the real pairing response outside the repository or under the ignored `private/` directory. From the repository root:

```sh
python3 android/tools/provision-tv.py \
  --serial TV_IP:CONNECTION_PORT \
  --credentials /absolute/path/to/your-tv-pairing.json
```

The helper validates the two required fields and writes a minimal pairing file directly into the app’s private files directory using `adb run-as`. It does not print the credentials or leave a copy in shared TV storage. It requires the debuggable development build and an authorized ADB connection. It does not restart or reset the app.

Open Freelight after provisioning. If no reader service is running yet, opening the Bluetooth workflow and granting permissions starts it. If the reader was already waiting for credentials, its retry loop picks them up. Replacing credentials in a service that has already loaded a different pair requires restarting the app process; this is not a hot credential switch.

## Connect a bulb

1. Keep the bulb powered on and close enough for a reliable Bluetooth connection.
2. Close other apps that may be actively connected to it. Put the bulb into a discoverable state using the controls available for your bulb/firmware. Do not factory-reset a working bulb just to try this app.
3. Open **Menu → Bluetooth bulbs → Scan for bulbs** and grant the requested Bluetooth permissions.
4. Select the bulb, choose **Add selected bulb**, enter an optional name, and explicitly choose its position. New bulbs have no position preselected.
5. Set brightness and choose **Save and connect**. The tested default is 2×.

Only the verified LCA012 model is currently accepted by the BLE controller. Successful Bluetooth discovery alone does not prove protocol compatibility.

## Everyday controls

On Home, use the room scene or a bulb’s **Settings** button to change its name, position or brightness. With a remote, focus the scene, use Left/Right to select a bulb, then OK to open its settings. The bulb cards are below the room scene; scroll down to reach them.

| Control | Behavior |
| --- | --- |
| Start | Enables automatic connection and starts/resumes this bulb |
| Stop | Pauses this session and restores its saved previous light state; wake may resume it |
| Disconnect (Bluetooth page) | Disables automatic connection for this bulb until Start is used |
| Start all / Stop all | Applies the corresponding action to every registered bulb |
| Home on the remote | Leaves the UI; the foreground service continues |
| Notification Stop action | Stops the reader service and disables its boot-start preference |

Use **Menu → App settings → App language → Apply** to override the system language.

## Troubleshooting

| Symptom | Check |
| --- | --- |
| No TV colors | Own-TV credentials are provisioned; TV API is available; allow startup retries after a cold boot |
| Authentication error | Pairing belongs to this TV and has not been revoked; placeholder values will not work |
| Bulb is found but cannot connect | Model is LCA012, Bluetooth permission is granted, bulb is available and another controller is not occupying it |
| Bulb does not follow video | Home shows connected/streaming; position and multiplier are set; TV’s Ambilight API is providing fresh data |
| Slow cold-boot connection | TV firmware may start its local color API later than the app service |
| Hue Bridge connects but no light sync | Bridge streaming is not implemented; use the tested BLE workflow |
| IKEA bulb is not listed/controllable | Matter/Thread support is not implemented |
| APK update signature mismatch | Use the same local signing key as the existing installation |

Developer diagnostics:

```sh
adb -s TV_IP:CONNECTION_PORT shell dumpsys activity service local.ambilight.reader/.ReaderService
```

The `bulbs` array contains per-bulb state. Raw output may contain local addresses, Bluetooth identifiers and custom names. Share a redacted excerpt rather than the whole dump. It does not contain the TV authentication key, but a full bug report can still contain personal data.
