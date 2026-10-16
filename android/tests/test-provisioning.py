"""Validate provisioning without contacting a real TV or reading real credentials."""
import contextlib
import importlib.util
import io
import json
from pathlib import Path
import sys
import tempfile
from unittest.mock import patch

spec = importlib.util.spec_from_file_location('provision', Path(__file__).resolve().parents[1] / 'tools/provision-tv.py')
module = importlib.util.module_from_spec(spec)
spec.loader.exec_module(module)
with tempfile.TemporaryDirectory() as temp:
    pairing = Path(temp) / 'pairing.json'
    data = {'device': {'id': 'test-device'}, 'response': {'auth_key': 'test-only-value'}, 'extra': 'not transferred'}
    pairing.write_text(json.dumps(data))
    args = ['provision-tv.py', '--serial', 'test-device', '--credentials', str(pairing)]
    output = io.StringIO()
    with patch.object(sys, 'argv', args), patch.object(module.subprocess, 'run') as adb, contextlib.redirect_stdout(output):
        assert module.main() == 0
        assert adb.call_count == 2
        sent = adb.call_args.kwargs['input']
        assert json.loads(sent) == {'device': data['device'], 'response': data['response']}
        assert 'test-only-value' not in str(adb.call_args.args)
        assert 'test-only-value' not in output.getvalue()
        assert 'run-as local.ambilight.reader' in adb.call_args.args[0][-1]
    for invalid in [{}, {'device': {'id': ''}, 'response': {'auth_key': 'x'}}, {'device': {'id': 'YOUR_PAIRED_DEVICE_ID'}, 'response': {'auth_key': 'YOUR_TV_AUTH_KEY'}}]:
        pairing.write_text(json.dumps(invalid))
        with patch.object(sys, 'argv', args), patch.object(module.subprocess, 'run') as adb, contextlib.redirect_stderr(io.StringIO()):
            assert module.main() == 1
            adb.assert_not_called()
print('PASS: provisioning strips metadata, keeps credentials out of commands/output, rejects invalid input before ADB')
