"""Bung riêng các class đa phiên bản mà lượt dịch ngược JAR thông thường bỏ qua."""
import argparse
import concurrent.futures
import json
import re
import subprocess
import tempfile
import zipfile
from pathlib import Path

p = argparse.ArgumentParser()
p.add_argument('--app', required=True)
p.add_argument('--java', required=True)
p.add_argument('--decompiler', required=True)
p.add_argument('--output', required=True)
a = p.parse_args()
jobs = []
for jar in Path(a.app).rglob('*.jar'):
    with zipfile.ZipFile(jar) as archive:
        versions = sorted({m.group(1) for name in archive.namelist()
                           if (m := re.match(r'META-INF/versions/(\d+)/.*\.class$', name))})
        jobs.extend((jar, version) for version in versions)

def run(job):
    jar, version = job
    relative = jar.relative_to(a.app).as_posix()
    target = Path(a.output) / relative.removesuffix('.jar') / 'source/META-INF/versions' / version
    target.mkdir(parents=True, exist_ok=True)
    prefix = f'META-INF/versions/{version}/'
    with tempfile.TemporaryDirectory(prefix='roastmane-version-') as temporary:
        stripped = Path(temporary) / 'variant.jar'
        with zipfile.ZipFile(jar) as source, zipfile.ZipFile(stripped, 'w') as dest:
            for name in source.namelist():
                if name.startswith(prefix) and name.endswith('.class'):
                    dest.writestr(name[len(prefix):], source.read(name))
        with (target / 'decompiler-output.txt').open('w', encoding='utf-8') as log:
            result = subprocess.run([a.java, '-Xmx512m', '-jar', a.decompiler, '--folder',
                                     '--thread-count=1', '--log-level=WARN', str(stripped), str(target)],
                                    stdout=log, stderr=subprocess.STDOUT)
    record = {'jar': relative, 'version': version, 'exit': result.returncode,
              'java_files': len([f for f in target.rglob('*.java') if f.is_file()])}
    print(json.dumps(record), flush=True)
    return record

with concurrent.futures.ThreadPoolExecutor(max_workers=3) as pool:
    records = list(pool.map(run, jobs))
Path(a.output, 'versions-report.json').write_text(json.dumps(records, indent=2), encoding='utf-8')
