from pathlib import Path
import json
import re
import xml.etree.ElementTree as ET

root = Path(__file__).resolve().parents[1]
catalog = json.loads((root / 'tools/catalog.json').read_text())
keys = set(catalog['en'])
tokens = lambda text: sorted(re.findall(r'%\d+\$[.\d]*[dfs]', text))
assert len(catalog) == 18
for language, strings in catalog.items():
    assert set(strings) == keys, language
    folder = 'values' if language == 'en' else 'values-' + language
    actual = {node.attrib['name']: node.text or '' for node in ET.parse(root / 'res' / folder / 'strings.xml').getroot()}
    assert set(actual) == keys, language
    for key, text in strings.items():
        assert tokens(text) == tokens(catalog['en'][key]), (language, key)
        escaped = text.replace("'", "\\'").replace('"', '\\"')
        assert actual[key] == escaped, (language, key)
print(f'PASS: {len(catalog)} languages, {len(keys) * len(catalog)} translated strings, matching XML and format tokens')
