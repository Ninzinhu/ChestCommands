# PowerShell script to extract class list and run javap on selected HytaleServer.jar classes
# Usage: .\tools\extract_hytale_api.ps1

$jar = "libs\HytaleServer.jar"
$entries = "libs\hytale_entries.txt"
$docsDir = "docs"
$apiIndex = "$docsDir\hytale-api-index.txt"

if (-not (Test-Path $jar)) {
    Write-Error "HytaleServer.jar not found at $jar. Place the server JAR into the libs folder."
    exit 1
}

if (-not (Get-Command jar -ErrorAction SilentlyContinue)) {
    Write-Error "'jar' command not found. Ensure JDK is installed and 'jar' is on PATH."
    exit 1
}

if (-not (Get-Command javap -ErrorAction SilentlyContinue)) {
    Write-Warning "'javap' not found on PATH. The script will still list classes but cannot show method signatures."
}

New-Item -ItemType Directory -Force -Path $docsDir | Out-Null

Write-Host "Listing JAR entries to $entries (this can be large)..."
jar tf $jar > $entries

# Patterns to look for
$patterns = @(
    'EntityStore',
    'ItemContainerWindow',
    'ItemContainer',
    'ItemStack',
    'openUI',
    'openWindow',
    'openInventory',
    'sendMessage',
    'sendChatMessage',
    'sendSystemMessage',
    'CommandManager',
    'JavaPlugin',
    'HytaleServer',
    'Window',
    'Inventory',
    'Player'
)

Write-Host "Searching for API-relevant entries..."
$matches = @()
foreach ($p in $patterns) {
    $found = Select-String -Path $entries -Pattern $p -SimpleMatch -ErrorAction SilentlyContinue | ForEach-Object { $_.Line }
    if ($found) { $matches += $found }
}
$matches = $matches | Sort-Object -Unique

if (-not $matches) {
    Write-Warning "No matching entries found in JAR for the given patterns. The JAR may be huge; check the file manually."
} else {
    Write-Host "Found $($matches.Count) matching entries. Writing to $apiIndex"
    $matches | Out-File -FilePath $apiIndex -Encoding UTF8
}

# Try to run javap for each matched .class path and save signature outputs
if (Get-Command javap -ErrorAction SilentlyContinue) {
    $signaturesFile = "$docsDir\hytale-api-signatures.txt"
    "Javap signatures generated on $(Get-Date)`n" | Out-File $signaturesFile -Encoding UTF8
    foreach ($line in $matches) {
        # Only class files
        if ($line -like "*.class") {
            $classPath = $line.Trim()
            # Convert to fully qualified class name
            $className = $classPath -replace '/', '.' -replace '\\', '.' -replace '\.class$',''
            Write-Host "javap -> $className"
            try {
                $out = javap -classpath $jar -public $className 2>&1
                "--- $className ---" | Out-File -FilePath $signaturesFile -Append -Encoding UTF8
                $out | Out-File -FilePath $signaturesFile -Append -Encoding UTF8
            } catch {
                "Failed javap for $className: $($_)" | Out-File -FilePath $signaturesFile -Append -Encoding UTF8
            }
        }
    }
    Write-Host "Signatures saved to $signaturesFile"
} else {
    Write-Warning "Skipping javap signatures because 'javap' not found."
}

Write-Host "Done. Check the $docsDir folder for outputs: hytale-api-index.txt and hytale-api-signatures.txt (if javap available)."
