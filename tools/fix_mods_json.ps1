<#
fix_mods_json.ps1

Valida todos os arquivos .json dentro da pasta mods e tenta corrigir problemas simples:
- Remove BOM (reescreve em UTF-8 sem BOM)
- Revalida JSON com ConvertFrom-Json

Uso:
  powershell -ExecutionPolicy Bypass -File tools\fix_mods_json.ps1 -ModsPath "C:\path\to\server\mods"

Se nenhum argumento for passado, usa .\mods
#>
param(
    [string]$ModsPath = ".\mods"
)

if (-not (Test-Path $ModsPath)) {
    Write-Host "Mods path not found: $ModsPath" -ForegroundColor Yellow
    exit 1
}

Write-Host "Scanning JSON files in: $ModsPath" -ForegroundColor Cyan

$badFiles = @()
Get-ChildItem -Path $ModsPath -Filter *.json -Recurse | ForEach-Object {
    $path = $_.FullName
    Write-Host "Checking: $path"
    try {
        $text = Get-Content -Raw -Encoding UTF8 $path
        $obj = $null
        try { $obj = $text | ConvertFrom-Json -ErrorAction Stop } catch { $obj = $null }
        if ($null -ne $obj) {
            Write-Host "  OK" -ForegroundColor Green
            return
        }
        Write-Host "  INVALID JSON, trying to rewrite without BOM and re-validate..." -ForegroundColor Yellow
        # rewrite as UTF8 without BOM
        $enc = New-Object System.Text.UTF8Encoding($false)
        [System.IO.File]::WriteAllText($path, $text, $enc)
        # re-read and validate
        $text2 = Get-Content -Raw -Encoding UTF8 $path
        try { $obj2 = $text2 | ConvertFrom-Json -ErrorAction Stop; Write-Host "  Fixed: OK" -ForegroundColor Green; return } catch { $obj2 = $null }

        Write-Host "  Still INVALID JSON after BOM removal" -ForegroundColor Red
        $badFiles += $path
    } catch {
        Write-Host "  ERROR reading $path : $_" -ForegroundColor Red
        $badFiles += $path
    }
}

if ($badFiles.Count -gt 0) {
    Write-Host "\nFiles still invalid or with issues:" -ForegroundColor Red
    $badFiles | ForEach-Object { Write-Host "  $_" }
    Write-Host "\nOpen these files in an editor and fix JSON syntax (quotes, commas, brackets)." -ForegroundColor Yellow
    exit 2
} else {
    Write-Host "\nAll .json files validated (or fixed) successfully." -ForegroundColor Green
    exit 0
}
