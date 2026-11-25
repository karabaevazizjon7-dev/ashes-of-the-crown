from pathlib import Path
import xml.etree.ElementTree as ET
root = Path(__file__).resolve().parents[1]
res = root / 'app/src/main/res'
raw = res / 'raw'
audio = sorted(p for p in raw.iterdir() if p.is_file() and p.suffix.lower() in {'.ogg','.mp3','.wav','.m4a'})
assert len(audio) == 7, f'Expected the 7 known bundled audio assets, found {len(audio)}'
assert all(p.stat().st_size > 0 for p in audio), 'Empty audio resource detected'
manifest = root / 'app/src/main/AndroidManifest.xml'
ET.parse(manifest)
refs = set()
for source in (root / 'app/src/main/java').rglob('*.kt'):
    refs.update(__import__('re').findall(r'R\.drawable\.([A-Za-z0-9_]+)', source.read_text()))
drawable_names = {p.stem for folder in [res/'drawable', res/'drawable-nodpi'] for p in folder.iterdir() if p.is_file()}
missing = sorted(refs - drawable_names)
assert not missing, f'Missing drawable references: {missing}'
print(f'PASS: {len(audio)} bundled audio files are present and non-empty')
print('PASS: AndroidManifest.xml parses')
print(f'PASS: all {len(refs)} drawable references resolve')
