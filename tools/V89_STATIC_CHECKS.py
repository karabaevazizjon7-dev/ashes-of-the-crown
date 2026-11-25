from pathlib import Path
from PIL import Image
import hashlib
import re
import xml.etree.ElementTree as ET
root = Path(__file__).resolve().parents[1]
main = (root / 'app/src/main/java/com/ashes/crown/MainActivity.kt').read_text(encoding='utf-8')
res = root / 'app/src/main/res/drawable-nodpi'
checks = {}
refs=[]
for i in range(21):
    name=f'story_v89_{i:02d}'
    f=res/f'{name}.jpg'
    checks[f'{name} resource exists']=f.is_file()
    if f.is_file():
        with Image.open(f) as im:
            checks[f'{name} is 960x540']=im.size==(960,540)
            checks[f'{name} is RGB JPEG']=im.format=='JPEG' and im.mode in ('RGB','L')
        refs.append(hashlib.sha256(f.read_bytes()).hexdigest())
    checks[f'scene index {i} maps to {name}']=f'{i} -> R.drawable.{name}' in main
checks['story resolver has explicit scene mapping']='"story" -> when (sceneIndex)' in main
checks['story artwork fills illustration panel']='contentScale = ContentScale.Crop' in main
checks['no old generic story art mapping remains']='0 -> R.drawable.scene_art_prologue' not in main
checks['catalog documents all 21 scenes']=(root/'docs/STORY_ARTWORK_V89.md').exists() and len(re.findall(r'\|\s*\d+\s*\|', (root/'docs/STORY_ARTWORK_V89.md').read_text(encoding='utf-8')))==21
checks['all story assets are distinct']=len(set(refs))==21
ET.parse(root/'app/src/main/AndroidManifest.xml')
for name,ok in checks.items(): print(('PASS ' if ok else 'FAIL ')+name)
failed=[k for k,v in checks.items() if not v]
if failed: raise SystemExit('V89 checks failed: '+', '.join(failed))
print('PASS AndroidManifest XML')
print(f'V89_STATIC_CHECKS: {len(checks)+1} passed')
