"""Đóng gói class ứng dụng đã biên dịch và tài nguyên trong repository."""
import argparse
import hashlib
import json
from pathlib import Path
import zipfile

p = argparse.ArgumentParser()
p.add_argument('--classes', required=True)
p.add_argument('--resources', required=True)
p.add_argument('--main', required=True)
p.add_argument('--local', required=True)
a = p.parse_args()
classes, resources = Path(a.classes), Path(a.resources)
main_files, local_files = {}, {}
for file in resources.rglob('*'):
    if file.is_file():
        main_files[file.relative_to(resources).as_posix()] = file
for file in classes.rglob('*.class'):
    relative = file.relative_to(classes).as_posix()
    if relative.startswith('local/'):
        if file.name.startswith('PatchJar') or file.name == 'BuildIcon.class':
            continue
        local_files[relative] = file
    else:
        main_files[relative] = file
assert any(name.endswith('RoastingIntelligenceApp.class') for name in main_files)
assert 'local/Launcher.class' in local_files
for target, files in [(a.main, main_files), (a.local, local_files)]:
    with zipfile.ZipFile(target, 'w', compression=zipfile.ZIP_DEFLATED) as archive:
        for name, file in sorted(files.items()):
            info = zipfile.ZipInfo(name, date_time=(2026, 10, 23, 0, 0, 0))
            info.compress_type = zipfile.ZIP_DEFLATED
            archive.writestr(info, file.read_bytes())
    print(json.dumps({'jar': target, 'entries': len(files),
                      'sha256': hashlib.sha256(Path(target).read_bytes()).hexdigest()}))
