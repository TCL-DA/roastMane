"""Đối chiếu mọi JAR với bản kiểm kê và file Java đã trích xuất."""
import argparse
import hashlib
import json
from pathlib import Path

p = argparse.ArgumentParser()
p.add_argument('--app', required=True)
p.add_argument('--output', required=True)
a = p.parse_args()
app, output = Path(a.app), Path(a.output)
records, missing, source_files = [], [], set()
for jar in sorted(app.rglob('*.jar')):
    relative = jar.relative_to(app).as_posix()
    directory = output / relative.removesuffix('.jar')
    source_files.update(f for f in (directory / 'source').rglob('*.java') if f.is_file())
    record = json.loads((directory / 'inventory.json').read_text(encoding='utf-8'))
    assert record['sha256'] == hashlib.sha256(jar.read_bytes()).hexdigest(), relative
    assert record['decompiler_exit'] == 0, relative
    # Class lồng nhau được gộp vào file Java của class bao ngoài.
    absent = []
    for name in record['classes']:
        candidate = name.removesuffix('.class')
        while not (directory / 'source' / (candidate + '.java')).is_file():
            parent, _, leaf = candidate.rpartition('/')
            if '$' not in leaf or leaf.rfind('$') == 0:
                absent.append(name)
                break
            candidate = (parent + '/' if parent else '') + leaf.rsplit('$', 1)[0]
    absent = sorted(set(absent))
    if absent:
        missing.append({'jar': relative, 'unmapped_outer_sources': absent})
    records.append(record)
report = {'jar_count': len(records), 'class_entries': sum(r['class_entries'] for r in records),
          'java_files': len(source_files),
          'vineflower_error_files': sum(len(r['error_files']) for r in records),
          'unmapped_outer_sources': missing, 'all_jar_hashes_match': True,
          'full_source_build_verified': False}
(output / 'audit.json').write_text(json.dumps(report, indent=2), encoding='utf-8')
print(json.dumps(report, indent=2))
