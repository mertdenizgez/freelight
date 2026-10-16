#!/usr/bin/env python3
"""Provision an own-TV pairing response into a debuggable Freelight installation."""
import argparse
import json
import pathlib
import subprocess
import sys

PACKAGE = 'local.ambilight.reader'


def read_pairing(path):
    data = json.loads(pathlib.Path(path).read_text(encoding='utf-8'))
    device_id = data['device']['id']
    auth_key = data['response']['auth_key']
    for value in (device_id, auth_key):
        if not isinstance(value, str) or not value.strip() or any(ord(c) < 32 for c in value):
            raise ValueError('Invalid required pairing field')
        if value.startswith('YOUR_'):
            raise ValueError('Replace placeholder credentials with your own TV pairing response')
    # Do not transfer unrelated metadata or a full pairing transcript.
    return json.dumps({'device': {'id': device_id}, 'response': {'auth_key': auth_key}}).encode()


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--serial', required=True, help='Authorized ADB device serial or IP:port')
    parser.add_argument('--credentials', required=True, type=pathlib.Path, help='Own-TV pairing JSON file')
    args = parser.parse_args()
    try:
        payload = read_pairing(args.credentials)
    except (OSError, ValueError, KeyError, TypeError):
        print('Cannot read a valid pairing response. Expected nonempty device.id and response.auth_key; credentials were not sent.', file=sys.stderr)
        return 1
    adb = ['adb', '-s', args.serial]
    try:
        subprocess.run(adb + ['get-state'], check=True, stdout=subprocess.DEVNULL, stderr=subprocess.PIPE, timeout=15)
        # The remote command is constant. Credentials travel only through stdin,
        # never through shell interpolation, command-line arguments or stdout.
        command = f"run-as {PACKAGE} sh -c 'umask 077; mkdir -p files && cat > files/pairing.json.new && mv files/pairing.json.new files/pairing.json'"
        subprocess.run(adb + ['shell', command], input=payload, check=True, stdout=subprocess.DEVNULL, stderr=subprocess.PIPE, timeout=30)
    except (OSError, subprocess.SubprocessError):
        print('Provisioning failed. Check ADB authorization, selected device and the installed debuggable app. Credentials were not printed.', file=sys.stderr)
        return 1
    print('TV credentials provisioned into Freelight private storage. Open the app and connect a compatible bulb.')
    return 0


if __name__ == '__main__':
    sys.exit(main())
