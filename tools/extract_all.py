"""Trích xuất mã Java và tài nguyên của mọi JAR, giữ bản kiểm kê đầu vào."""
import argparse
import concurrent.futures
import hashlib
import json
import subprocess
import zipfile
from pathlib import Path


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('--app', required=True)
    parser.add_argument('--java', required=True)
    parser.add_argument('--decompiler', required=True)
    parser.add_argument('--output', required=True)
    args = parser.parse_args()
    app, output = Path(args.app), Path(args.output)
    output.mkdir(parents=True, exist_ok=True)
    jars = sorted(app.rglob('*.jar'))

    def extract(jar):
        relative = jar.relative_to(app).as_posix()
        target = output / relative.removesuffix('.jar')
        target.mkdir(parents=True, exist_ok=True)
        inventory = target / 'inventory.json'
        if inventory.exists():
            previous = json.loads(inventory.read_text(encoding='utf-8'))
            if previous['sha256'] == hashlib.sha256(jar.read_bytes()).hexdigest() and previous['decompiler_exit'] == 0:
                print(f'{relative}: cached', flush=True)
                return {key: value for key, value in previous.items() if key not in {'classes', 'resources'}}
        log = target / 'decompiler.log'
        with log.open('w', encoding='utf-8') as stream:
            result = subprocess.run([
                args.java, '-Xmx768m', '-jar', args.decompiler,
                '--folder', '--thread-count=1', '--log-level=WARN',
                str(jar), str(target / 'source'),
            ], stdout=stream, stderr=subprocess.STDOUT)
        classes, resources, omitted = [], [], []
        with zipfile.ZipFile(jar) as archive:
            for entry in archive.infolist():
                if entry.is_dir():
                    continue
                name = entry.filename
                if name.endswith('.class'):
                    classes.append(name)
                    continue
                # Tệp nhị phân được kiểm kê, không giả nhận là mã nguồn.
                resources.append({'path': name, 'bytes': entry.file_size,
                                  'sha256': hashlib.sha256(archive.read(entry)).hexdigest()})
        java_files = sorted(p for p in (target / 'source').rglob('*.java') if p.is_file())
        errors = [p.relative_to(target).as_posix() for p in java_files
                  if 'couldn\'t be decompiled' in p.read_text(encoding='utf-8', errors='replace').lower()
                  or '$VF: Could not decompile' in p.read_text(encoding='utf-8', errors='replace')]
        # Giữ tài nguyên văn bản và asset nhỏ; bỏ binary native khỏi Git.
        for path in (target / 'source').rglob('*'):
            if path.is_file() and (path.suffix.lower() in {'.dll', '.so', '.dylib', '.jnilib', '.exe', '.class', '.a', '.lib', '.o', '.obj'} or path.stat().st_size > 10_000_000):
                omitted.append(path.relative_to(target / 'source').as_posix())
                path.unlink()
        record = {'jar': relative, 'sha256': hashlib.sha256(jar.read_bytes()).hexdigest(),
                  'class_entries': len(classes), 'classes': classes, 'java_files': len(java_files),
                  'decompiler_exit': result.returncode, 'error_files': errors,
                  'resources': resources, 'omitted_binary_resources': omitted,
                  'full_source_build_verified': False}
        (target / 'inventory.json').write_text(json.dumps(record, ensure_ascii=False, indent=2), encoding='utf-8')
        print(f'{relative}: exit={result.returncode} classes={len(classes)} java={len(java_files)} errors={len(errors)}', flush=True)
        return {key: value for key, value in record.items() if key not in {'classes', 'resources'}}

    with concurrent.futures.ThreadPoolExecutor(max_workers=3) as pool:
        records = list(pool.map(extract, jars))
    (output / 'inventory.json').write_text(json.dumps(records, ensure_ascii=False, indent=2), encoding='utf-8')
    if any(record['decompiler_exit'] for record in records):
        raise SystemExit('Some decompilations failed; inspect inventories and logs')


if __name__ == '__main__':
    main()
