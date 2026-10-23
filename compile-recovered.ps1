param(
    [ValidateSet('Main', 'OfflineV06', 'All', 'Recovered')]
    [string]$Target = 'All',
    [string]$Bundle = 'C:\Tools\_soi\roastMane-Offline-OTL-20261022-03',
    [string]$JdkBin = 'C:\Program Files\Eclipse Adoptium\jdk-21.0.12.101-hotspot\bin',
    [string]$MainSource,
    [string]$OfflineSource,
    [string]$AsmJar
)

$ErrorActionPreference = 'Stop'
$recoveryRoot = [IO.Path]::GetFullPath($PSScriptRoot)
$appDir = Join-Path $Bundle 'app'
$javac = Join-Path $JdkBin 'javac.exe'
$reportsDir = Join-Path $recoveryRoot 'reports'

# Tự nhận dạng cả bố cục thư mục phục hồi và bố cục repository chính.
if ([string]::IsNullOrWhiteSpace($MainSource)) {
    $candidate = Join-Path $recoveryRoot 'src\main\java'
    $MainSource = if (Test-Path -LiteralPath $candidate) { $candidate } else { Join-Path $recoveryRoot 'decompiled' }
}
if ([string]::IsNullOrWhiteSpace($OfflineSource)) {
    $candidate = Join-Path $recoveryRoot 'offline-v06\patch-src\local'
    $OfflineSource = if (Test-Path -LiteralPath $candidate) { $candidate } else { Join-Path $recoveryRoot 'offline-v06\src\local' }
}
if ([string]::IsNullOrWhiteSpace($AsmJar)) {
    $AsmJar = Join-Path $recoveryRoot 'offline-v06\lib\asm.jar'
}

if (-not (Test-Path -LiteralPath $javac)) {
    throw "javac not found: $javac"
}
if (-not (Test-Path -LiteralPath (Join-Path $appDir 'roastingintelligence-5.13.jar'))) {
    throw "Invalid roastMane bundle: $Bundle"
}
New-Item -ItemType Directory -Path $reportsDir -Force | Out-Null

function Reset-OwnedDirectory {
    param([Parameter(Mandatory)][string]$Path)

    # Chỉ dọn thư mục build nằm trực tiếp trong vùng phục hồi này.
    $resolved = [IO.Path]::GetFullPath($Path)
    $ownedPrefix = $recoveryRoot.TrimEnd([IO.Path]::DirectorySeparatorChar) + [IO.Path]::DirectorySeparatorChar
    if (-not $resolved.StartsWith($ownedPrefix, [StringComparison]::OrdinalIgnoreCase)) {
        throw "Refusing to clean a directory outside the recovery root: $resolved"
    }
    if (Test-Path -LiteralPath $resolved) {
        [IO.Directory]::Delete($resolved, $true)
    }
    New-Item -ItemType Directory -Path $resolved | Out-Null
}

function Quote-ArgFileValue {
    param([Parameter(Mandatory)][string]$Value)
    return '"' + $Value.Replace('\', '/') + '"'
}

function Invoke-JavacResponse {
    param(
        [Parameter(Mandatory)][string]$Name,
        [Parameter(Mandatory)][string[]]$Arguments,
        [Parameter(Mandatory)][string]$ClassesDir
    )

    Reset-OwnedDirectory -Path $ClassesDir
    $argFile = Join-Path $reportsDir "javac-$Name.args"
    $stdout = Join-Path $reportsDir "javac-$Name.stdout.log"
    $stderr = Join-Path $reportsDir "javac-$Name.log"
    [IO.File]::WriteAllLines($argFile, $Arguments, [Text.UTF8Encoding]::new($false))

    # Dùng đường dẫn response file tương đối để script vẫn chạy khi thư mục cha có dấu cách.
    $responseArgument = '@reports/' + [IO.Path]::GetFileName($argFile)
    $process = Start-Process -FilePath $javac -ArgumentList $responseArgument `
        -WorkingDirectory $recoveryRoot -NoNewWindow -Wait -PassThru `
        -RedirectStandardOutput $stdout -RedirectStandardError $stderr
    Set-Content -LiteralPath (Join-Path $reportsDir "javac-$Name.exitcode.txt") `
        -Value $process.ExitCode -Encoding ASCII

    $errors = (Select-String -LiteralPath $stderr -Pattern ': error:' | Measure-Object).Count
    $warnings = (Select-String -LiteralPath $stderr -Pattern ': warning:' | Measure-Object).Count
    Write-Host "javac target=$Name exit=$($process.ExitCode) errors=$errors warnings=$warnings"
    return $process.ExitCode
}

function Compile-Main {
    param([switch]$IncludeOverlay)
    $sourceRoot = [IO.Path]::GetFullPath($MainSource)
    $targetName = if ($IncludeOverlay) { 'recovered-current' } else { 'main-current' }
    $classesDir = Join-Path $recoveryRoot $(if ($IncludeOverlay) { 'build\classes-recovered' } else { 'build\classes-main' })
    $embeddedPatch = [IO.Path]::GetFullPath((Join-Path $sourceRoot 'offline-patch'))
    $sources = Get-ChildItem -LiteralPath $sourceRoot -Recurse -Filter '*.java' -File |
        Where-Object { -not $_.FullName.StartsWith($embeddedPatch, [StringComparison]::OrdinalIgnoreCase) } |
        Sort-Object FullName
    if ($IncludeOverlay) {
        $sources = @($sources) + @(Get-ChildItem -LiteralPath $OfflineSource -Filter '*.java' -File | Sort-Object FullName)
    }

    # Main JAR bị loại khỏi classpath để javac không lấy class cũ che lỗi source.
    $dependencies = Get-ChildItem -LiteralPath $appDir -Filter '*.jar' -File |
        Where-Object { $_.Name -ne 'roastingintelligence-5.13.jar' -and (-not $IncludeOverlay -or $_.Name -ne 'offline-local.jar') } |
        ForEach-Object FullName
    if ($IncludeOverlay) { $dependencies += [IO.Path]::GetFullPath($AsmJar) }
    $classpath = Quote-ArgFileValue -Value ($dependencies -join ';')
    $modulePath = Quote-ArgFileValue -Value (Join-Path $appDir 'javafx')

    $arguments = @(
        '-encoding', 'UTF-8', '-proc:none', '-Xmaxerrs', '5000', '-Xmaxwarns', '500',
        '--module-path', $modulePath,
        '--add-modules', 'javafx.controls,javafx.fxml,javafx.swing,javafx.web,javafx.media',
        '--add-exports', 'javafx.base/com.sun.javafx=ALL-UNNAMED',
        '--add-exports', 'javafx.controls/com.sun.javafx.scene.control.behavior=ALL-UNNAMED',
        '--add-exports', 'javafx.controls/com.sun.javafx.scene.control.inputmap=ALL-UNNAMED',
        '--add-exports', 'javafx.controls/com.sun.javafx.scene.control.skin=ALL-UNNAMED',
        '--add-exports', 'javafx.controls/com.sun.javafx.scene.control=ALL-UNNAMED',
        '--add-exports', 'javafx.graphics/com.sun.javafx.print=ALL-UNNAMED',
        '--add-exports', 'javafx.graphics/com.sun.javafx.scene=ALL-UNNAMED',
        '--add-exports', 'javafx.graphics/com.sun.prism.impl=ALL-UNNAMED',
        '--add-exports', 'javafx.graphics/com.sun.prism=ALL-UNNAMED',
        '-cp', $classpath, '-d', (Quote-ArgFileValue -Value $classesDir)
    )
    $arguments += $sources | ForEach-Object { Quote-ArgFileValue -Value $_.FullName }
    Write-Host "Compiling recovered main sources: $($sources.Count) files"
    return Invoke-JavacResponse -Name $targetName -Arguments $arguments -ClassesDir $classesDir
}

function Compile-OfflineV06 {
    $sourceRoot = [IO.Path]::GetFullPath($OfflineSource)
    $classesDir = Join-Path $recoveryRoot 'build\classes-offline-v06'
    $sources = Get-ChildItem -LiteralPath $sourceRoot -Filter '*.java' -File | Sort-Object Name
    $dependencies = @()
    $dependencies += Get-ChildItem -LiteralPath $appDir -Filter '*.jar' -File | ForEach-Object FullName
    $dependencies += Get-ChildItem -LiteralPath (Join-Path $appDir 'javafx') -Filter '*.jar' -File | ForEach-Object FullName
    $dependencies += [IO.Path]::GetFullPath($AsmJar)
    $classpath = Quote-ArgFileValue -Value ($dependencies -join ';')

    $arguments = @(
        '-encoding', 'UTF-8', '-proc:none', '-cp', $classpath,
        '-d', (Quote-ArgFileValue -Value $classesDir)
    )
    $arguments += $sources | ForEach-Object { Quote-ArgFileValue -Value $_.FullName }
    Write-Host "Compiling current v06 offline overlay: $($sources.Count) files"
    return Invoke-JavacResponse -Name 'offline-v06-current' -Arguments $arguments -ClassesDir $classesDir
}

$exitCodes = @()
if ($Target -eq 'Recovered') {
    $exitCodes += Compile-Main -IncludeOverlay
}
if ($Target -in @('Main', 'All')) {
    $exitCodes += Compile-Main
}
if ($Target -in @('OfflineV06', 'All')) {
    $exitCodes += Compile-OfflineV06
}
if ($exitCodes -contains 1 -or ($exitCodes | Where-Object { $_ -ne 0 }).Count -gt 0) {
    exit 1
}
exit 0
