<#
check_jar_and_manifest.ps1

Localiza JARs em target/ e em mods/, extrai plugin.json e manifest.json (se existirem) para uma pasta temporária e valida o JSON.

Uso:
  powershell -ExecutionPolicy Bypass -File tools\check_jar_and_manifest.ps1 -JarPath "C:\path\to\mods"

Se nenhum JarPath for passado, procura em target/ e .\mods
#>
param(
    [string]$JarPath = ""
)

function Validate-JsonContent([string]$jsonPath) {
    try {
        $text = Get-Content -Raw -Encoding UTF8 $jsonPath
        $obj = $text | ConvertFrom-Json -ErrorAction Stop
        Write-Host "  VALID JSON: $jsonPath" -ForegroundColor Green
        return $true
    } catch {
        Write-Host "  INVALID JSON: $jsonPath -> $($_.Exception.Message)" -ForegroundColor Red
        return $false
    }
}

$searchLocations = @()
if ($JarPath -and (Test-Path $JarPath)) { $searchLocations += $JarPath } else { $searchLocations += "target"; $searchLocations += ".\mods" }

$temp = Join-Path $env:TEMP "chestinspect"
if (Test-Path $temp) { Remove-Item -Recurse -Force $temp }
New-Item -ItemType Directory -Path $temp | Out-Null

foreach ($loc in $searchLocations) {
    Write-Host "Searching in: $loc" -ForegroundColor Cyan
    if (-not (Test-Path $loc)) { Write-Host "  Path not found: $loc"; continue }
    Get-ChildItem -Path $loc -Filter *.jar -Recurse | ForEach-Object {
        $jar = $_.FullName
        Write-Host "Found JAR: $jar"
        # try to extract plugin.json and manifest.json
        try {
            Push-Location $temp
            jar xf $jar plugin.json 2>$null | Out-Null
            jar xf $jar manifest.json 2>$null | Out-Null
            Pop-Location
            $p = Join-Path $temp "plugin.json"
            $m = Join-Path $temp "manifest.json"
            if (Test-Path $p) { Write-Host "Extracted plugin.json from $jar"; Validate-JsonContent $p } else { Write-Host "plugin.json not found in $jar" }
            if (Test-Path $m) { Write-Host "Extracted manifest.json from $jar"; Validate-JsonContent $m } else { Write-Host "manifest.json not found in $jar" }
            # cleanup extracted
            Remove-Item -Force (Join-Path $temp "plugin.json") -ErrorAction SilentlyContinue
            Remove-Item -Force (Join-Path $temp "manifest.json") -ErrorAction SilentlyContinue
        } catch {
            Write-Host "  Error extracting from jar: $_" -ForegroundColor Red
        }
    }
}

Write-Host "Done. Temporary files (if any) are in: $temp"
