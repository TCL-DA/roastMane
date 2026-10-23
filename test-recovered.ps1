param(
    [string]$Bundle = 'C:\Tools\_soi\roastMane-SourceBuild-20261023-01',
    [string]$JdkBin = 'C:\Program Files\Eclipse Adoptium\jdk-21.0.12.101-hotspot\bin'
)
$ErrorActionPreference = 'Stop'
$appDirectory = Join-Path $Bundle 'app'
$testRoot = Join-Path $env:TEMP ('roastmane-source-tests-' + [Guid]::NewGuid().ToString('N'))
$testClasses = Join-Path $testRoot 'classes'
New-Item -ItemType Directory -Path $testClasses -Force | Out-Null
New-Item -ItemType Directory -Path (Join-Path $testRoot 'home'), (Join-Path $testRoot 'logs') -Force | Out-Null
$classPath = "$testClasses;$appDirectory\*;$appDirectory\javafx\*"
$testSources = @(Get-ChildItem -LiteralPath (Join-Path $PSScriptRoot 'offline-v06\tests\local') -Filter '*.java' -File) + @(Get-ChildItem -LiteralPath (Join-Path $PSScriptRoot 'tests\local') -Filter '*.java' -File)
& (Join-Path $JdkBin 'javac.exe') -encoding UTF-8 -proc:none -cp $classPath -d $testClasses ($testSources | ForEach-Object FullName)
if ($LASTEXITCODE -ne 0) { throw 'Test source compilation failed.' }
$javaOptions = Get-Content -LiteralPath (Join-Path $appDirectory 'roastMane.cfg') | Where-Object { $_.StartsWith('java-options=') } | ForEach-Object { $_.Substring(13).Replace('$APPDIR', $appDirectory) }
$results = @()
foreach ($testClass in @('local.PatchedFilePersisterIntegrationTest', 'local.BrandRenderProbe', 'local.RecoveredUiTest', 'local.GuiSmokeTest')) {
    $testLog = Join-Path $testRoot ($testClass + '.txt')
    $stdout = Join-Path $testRoot ($testClass + '.stdout.txt')
    $stderr = Join-Path $testRoot ($testClass + '.stderr.txt')
    $arguments = @($javaOptions) + @("-Doffline.data=$testRoot\data", "-Duser.home=$testRoot\home", "-Dorg.cropster.roastingintelligence.logging.path=$testRoot\logs/", '-cp', $classPath, $testClass)
    if ($testClass -eq 'local.RecoveredUiTest') { $arguments += (Join-Path $appDirectory 'roastingintelligence-5.13.jar') }
    # Gọi tiến trình trực tiếp và đọc hai luồng bất đồng bộ để tránh đầy bộ đệm log.
    $start = New-Object System.Diagnostics.ProcessStartInfo
    $start.FileName = Join-Path $JdkBin 'java.exe'
    $start.UseShellExecute = $false
    $start.CreateNoWindow = $true
    $start.RedirectStandardOutput = $true
    $start.RedirectStandardError = $true
    # Windows PowerShell 5 chưa có ArgumentList; response file tránh lỗi quoting.
    $response = Join-Path $testRoot ($testClass + '.args')
    $escaped = $arguments | ForEach-Object { '"' + $_.Replace('\', '/').Replace('"', '\"') + '"' }
    [IO.File]::WriteAllLines($response, $escaped, [Text.UTF8Encoding]::new($false))
    $start.Arguments = '@"' + $response + '"'
    $process = New-Object System.Diagnostics.Process
    $process.StartInfo = $start
    [void]$process.Start()
    $outputTask = $process.StandardOutput.ReadToEndAsync()
    $errorTask = $process.StandardError.ReadToEndAsync()
    if (-not $process.WaitForExit(60000)) {
        $process.Kill()
        throw "Test timeout: $testClass"
    }
    $outputText = $outputTask.GetAwaiter().GetResult()
    $errorText = $errorTask.GetAwaiter().GetResult()
    [IO.File]::WriteAllText($stdout, $outputText, [Text.UTF8Encoding]::new($false))
    [IO.File]::WriteAllText($stderr, $errorText, [Text.UTF8Encoding]::new($false))
    [IO.File]::WriteAllText($testLog, $outputText + $errorText, [Text.UTF8Encoding]::new($false))
    $results += [ordered]@{ test = $testClass; exit = $process.ExitCode; log = $testLog }
    Write-Host "$testClass exit=$($process.ExitCode)"
    if ($process.ExitCode -ne 0) { Get-Content -LiteralPath $testLog -Tail 30; throw "Test failed: $testClass" }
}
$results | ConvertTo-Json | Set-Content -LiteralPath (Join-Path $testRoot 'results.json') -Encoding UTF8
Write-Host "PASS test results: $testRoot"
