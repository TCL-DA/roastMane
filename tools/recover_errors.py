"""Dùng CFR tạo bản đối chiếu cho những file Vineflower báo lỗi."""
import argparse
import json
import re
import subprocess
from pathlib import Path

p = argparse.ArgumentParser()
p.add_argument('--app', required=True)
p.add_argument('--java', required=True)
p.add_argument('--cfr', required=True)
p.add_argument('--output', required=True)
a = p.parse_args()
results = []
for inventory in sorted(Path(a.output).rglob('inventory.json')):
    record = json.loads(inventory.read_text(encoding='utf-8'))
    if not isinstance(record, dict):
        continue
    for source in record['error_files']:
        name = Path(source).stem
        destination = inventory.parent / 'fallback-cfr'
        destination.mkdir(exist_ok=True)
        with (destination / f'{name}.log').open('w', encoding='utf-8') as log:
            run = subprocess.run([a.java, '-Xmx768m', '-jar', a.cfr,
                                  str(Path(a.app) / record['jar']), '--jarfilter', re.escape(name),
                                  '--outputdir', str(destination), '--silent', 'true'],
                                 stdout=log, stderr=subprocess.STDOUT)
        candidates = [file for file in destination.rglob(f'{name}.java') if file.is_file()]
        remaining = []
        for file in candidates:
            text = file.read_text(encoding='utf-8', errors='replace')
            if 'could not decompile' in text.lower() or 'decompilation failed' in text.lower():
                remaining.append(file.relative_to(destination).as_posix())
        result = {'jar': record['jar'], 'vineflower_source': source,
                  'cfr_exit': run.returncode, 'cfr_java_files': len(candidates),
                  'remaining_error_files': remaining, 'compilation_verified': False}
        results.append(result)
        print(json.dumps(result), flush=True)
Path(a.output, 'fallback-report.json').write_text(json.dumps(results, indent=2), encoding='utf-8')
