#!/usr/bin/env python3
"""
find_hytale_class.py

Simple helper to search classes and packages listed in hytale_entries.txt
Place this file in the repo and run:

    python tools/find_hytale_class.py query

It will print matching entries (path form and dot form), and can output JSON if requested.
"""
import sys
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
ENTRIES = ROOT / 'hytale_entries.txt'


def load_entries():
    if not ENTRIES.exists():
        print(f"Entries file not found: {ENTRIES}")
        return []
    with ENTRIES.open('r', encoding='utf-8', errors='ignore') as f:
        lines = [l.strip() for l in f if l.strip()]
    # keep only paths under com/hypixel/hytale
    filtered = [l for l in lines if l.startswith('com/hypixel/hytale/')]
    # normalize: remove trailing slashes, remove .class suffix
    normalized = []
    for p in filtered:
        if p.endswith('/'):
            normalized.append(p.rstrip('/'))
        elif p.endswith('.class'):
            normalized.append(p[:-6])
        else:
            normalized.append(p)
    # dedupe preserving order
    seen = set()
    out = []
    for p in normalized:
        if p not in seen:
            seen.add(p)
            out.append(p)
    return out


def to_dot(path):
    return path.replace('/', '.')


def search(entries, query):
    q = query.lower()
    results = []
    for p in entries:
        if q in p.lower() or q in to_dot(p).lower():
            results.append(p)
    return results


def main(argv):
    if len(argv) < 2:
        print('Usage: python tools/find_hytale_class.py <query> [--json]')
        return 2
    query = argv[1]
    out_json = '--json' in argv
    entries = load_entries()
    results = search(entries, query)
    if out_json:
        out = [{'path': r, 'dot': to_dot(r)} for r in results]
        print(json.dumps(out, indent=2, ensure_ascii=False))
    else:
        for r in results:
            print(r)
            print('  ->', to_dot(r))
    print(f"\nFound {len(results)} matches")
    return 0


if __name__ == '__main__':
    sys.exit(main(sys.argv))
