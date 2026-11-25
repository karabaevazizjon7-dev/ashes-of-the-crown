from pathlib import Path
import json, re
root = Path(__file__).resolve().parents[1]
res = root / 'app/src/main/res'
drawables = res / 'drawable-nodpi'
catalog_path = root / 'docs/visual_assets/alpha4/ASSET_CATALOG.json'
catalog = json.loads(catalog_path.read_text(encoding='utf-8'))
assert len(catalog) == 146, f'Expected 146 catalog entries, got {len(catalog)}'
missing_catalog_assets = [item['file'] for item in catalog if not (drawables / item['file']).is_file()]
assert not missing_catalog_assets, f'Catalog images missing from Android drawables: {missing_catalog_assets[:8]}'
assert not any(path.is_dir() for path in drawables.iterdir()), 'Nested directory remains under drawable-nodpi'
assert not (drawables / 'game_art').exists(), 'Duplicate game_art staging directory remains in Android resources'
references = set()
for source in (root / 'app/src/main/java').rglob('*.kt'):
    references.update(re.findall(r'R\.drawable\.([A-Za-z0-9_]+)', source.read_text(encoding='utf-8')))
available = {path.stem for path in drawables.iterdir() if path.is_file()}
missing_references = sorted(references - available)
assert not missing_references, f'Kotlin references missing drawable resources: {missing_references}'
readme = root / 'docs/visual_assets/alpha4/README_ASSETS.txt'
assert readme.is_file() and 'byte-identical duplicate copies' in readme.read_text(encoding='utf-8')
print(f'PASS: {len(catalog)} visual catalog entries resolve to drawable files')
print(f'PASS: {len(references)} Kotlin drawable references resolve')
print('PASS: drawable-nodpi is flat; duplicate staging folder removed')
print('V83 static checks: PASS')
