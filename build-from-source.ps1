param(
    [string]$BaseBundle = 'C:\Tools\_soi\roastMane-Offline-OTL-20261022-03',
    [string]$Output = 'C:\Tools\_soi\roastMane-SourceBuild-20261023-01',
    [string]$JdkBin = 'C:\Program Files\Eclipse Adoptium\jdk-21.0.12.101-hotspot\bin'
)

$ErrorActionPreference = 'Stop'
$repoRoot = $PSScriptRoot
if (Test-Path -LiteralPath $Output) { throw "Output already exists: $Output" }
if (-not (Test-Path -LiteralPath (Join-Path $BaseBundle 'app'))) { throw 'Base dependency bundle is missing.' }
& powershell.exe -NoProfile -ExecutionPolicy Bypass -File (Join-Path $repoRoot 'compile-recovered.ps1') -Target Recovered -Bundle $BaseBundle -JdkBin $JdkBin
if ($LASTEXITCODE -ne 0) { throw 'Recovered application source compilation failed.' }

# Thư mục duy nhất cho mỗi lượt dựng; không xóa hoặc ghi đè bundle nền.
$sourceWork = Join-Path $env:TEMP ('roastmane-source-package-' + [Guid]::NewGuid().ToString('N'))
New-Item -ItemType Directory -Path (Join-Path $sourceWork 'base\app') -Force | Out-Null
$sourceMain = Join-Path $sourceWork 'roastingintelligence-5.13.jar'
$sourceLocal = Join-Path $sourceWork 'offline-local.jar'
& python (Join-Path $repoRoot 'tools\package_recovered.py') --classes (Join-Path $repoRoot 'build\classes-recovered') --resources (Join-Path $repoRoot 'src\main\resources') --main $sourceMain --local $sourceLocal
if ($LASTEXITCODE -ne 0) { throw 'Recovered JAR packaging failed.' }
Copy-Item -Path (Join-Path $BaseBundle 'app\*') -Destination (Join-Path $sourceWork 'base\app') -Recurse
Copy-Item -LiteralPath $sourceMain -Destination (Join-Path $sourceWork 'base\app\roastingintelligence-5.13.jar') -Force
Copy-Item -LiteralPath $sourceLocal -Destination (Join-Path $sourceWork 'base\app\offline-local.jar') -Force

# Dùng pipeline nhận diện và jpackage hiện có trên main JAR vừa dựng từ source.
& powershell.exe -NoProfile -ExecutionPolicy Bypass -File (Join-Path $repoRoot 'offline-v06\build.ps1') -BaseBundle (Join-Path $sourceWork 'base') -Output $Output -JdkBin $JdkBin
if ($LASTEXITCODE -ne 0) { throw 'Source-built application packaging failed.' }

# Bàn giao đủ cây source ứng dụng để có thể dựng lại, không kèm kho dịch ngược thư viện.
$developmentRoot = Join-Path $Output 'development-source'
New-Item -ItemType Directory -Path $developmentRoot | Out-Null
foreach ($sourceItem in @('src', 'offline-v06', 'tools', 'tests', 'compile-recovered.ps1', 'build-from-source.ps1', 'test-recovered.ps1', 'README.md')) {
    Copy-Item -LiteralPath (Join-Path $repoRoot $sourceItem) -Destination $developmentRoot -Recurse
}
$buildInfo = [ordered]@{
    mode = 'application-source-build'
    sourceFiles = (Get-ChildItem -LiteralPath (Join-Path $repoRoot 'src\main\java') -Recurse -Filter '*.java' -File).Count + (Get-ChildItem -LiteralPath (Join-Path $repoRoot 'offline-v06\patch-src\local') -Filter '*.java' -File).Count
    originalMainClassesUsed = $false
    dependencyBundle = $BaseBundle
    applicationJarSha256 = (Get-FileHash -LiteralPath (Join-Path $Output 'app\roastingintelligence-5.13.jar') -Algorithm SHA256).Hash
}
$buildInfo | ConvertTo-Json | Set-Content -LiteralPath (Join-Path $Output 'BUILD-INFO.json') -Encoding UTF8
Get-ChildItem -LiteralPath $Output -Recurse -File | Where-Object { $_.FullName -ne (Join-Path $Output 'SHA256SUMS.txt') } | Get-FileHash -Algorithm SHA256 | ForEach-Object { "$($_.Hash)  $($_.Path.Substring($Output.Length + 1))" } | Set-Content -LiteralPath (Join-Path $Output 'SHA256SUMS.txt') -Encoding UTF8
Write-Host "Source build created: $Output"
