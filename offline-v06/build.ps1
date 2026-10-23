param(
  [string]$BaseBundle = 'C:\Tools\_soi\Cropster-Offline-OTL-20261021-01',
  [string]$Output = 'C:\Tools\_soi\roastMane-Offline-OTL-20261022-03',
  [switch]$Force,
  [string]$JdkBin = 'C:\Program Files\Eclipse Adoptium\jdk-21.0.12.101-hotspot\bin'
)

$ErrorActionPreference = 'Stop'
$jdk = $JdkBin
$sourceRoot = $PSScriptRoot
$work = Join-Path $env:TEMP 'roastmane-offline-build-v06-layoutfix'
$brandKit = Join-Path $sourceRoot 'brand-kit'

if (-not (Test-Path -LiteralPath (Join-Path $BaseBundle 'app\roastingintelligence-5.13.jar'))) { throw "Invalid input bundle: $BaseBundle" }
$approvedSvg = Join-Path $brandKit 'exports\monogram_xanh.svg'
$approvedAvatar = Join-Path $brandKit 'exports\avatar_xanh_1024.png'
$runtimeMark = Join-Path $brandKit 'runtime\mark-16.svg'
$runtimeWordmark = Join-Path $brandKit 'runtime\wordmark-298.svg'
if ((Get-FileHash -Algorithm SHA256 $approvedSvg).Hash -ne 'DE59BEEC61D8EBBFE8BDC6A01925CF12B724699393351846055A387A1E8917AF') { throw 'Approved v06 SVG hash mismatch.' }
if ((Get-FileHash -Algorithm SHA256 $approvedAvatar).Hash -ne 'E2288BBFBECDFBD1ADD6EB030D7F12E3A143D47DC6F9E75E2FE208828CCA5D52') { throw 'Approved v06 avatar hash mismatch.' }
if ((Get-FileHash -Algorithm SHA256 $runtimeMark).Hash -ne '2C4E436ED3AA34390B43E520C711BA5EAEBD850ED3341F58689B489715031D6C') { throw 'Flattened 16x16 runtime mark hash mismatch.' }
if ((Get-FileHash -Algorithm SHA256 $runtimeWordmark).Hash -ne '01E3F5750A3BDA7DF8DC581E512636C5146846C59B6B8A608F6A4015D0FF6B05') { throw 'Flattened 298x50 runtime wordmark hash mismatch.' }
if (Test-Path -LiteralPath $Output) {
  if (-not $Force) { throw "Output already exists. Use -Force after verifying the exact path: $Output" }
  $resolvedOutput = [IO.Path]::GetFullPath($Output)
  if (-not $resolvedOutput.StartsWith('C:\Tools\_soi\roastMane-', [StringComparison]::OrdinalIgnoreCase)) { throw "Refusing to remove a path outside the roastMane area: $resolvedOutput" }
  Remove-Item -LiteralPath $resolvedOutput -Recurse -Force
}
$resolvedWork = [IO.Path]::GetFullPath($work)
$expectedWork = [IO.Path]::GetFullPath((Join-Path $env:TEMP 'roastmane-offline-build-v06-layoutfix'))
if (-not $resolvedWork.Equals($expectedWork, [StringComparison]::OrdinalIgnoreCase)) { throw "Unexpected work path: $resolvedWork" }
if (Test-Path -LiteralPath $resolvedWork) { Remove-Item -LiteralPath $resolvedWork -Recurse -Force }
New-Item -ItemType Directory -Path $work, (Join-Path $work 'input'), (Join-Path $work 'classes'), (Join-Path $work 'image') | Out-Null
Copy-Item -Path (Join-Path $BaseBundle 'app\*') -Destination (Join-Path $work 'input') -Recurse -Force
Remove-Item -LiteralPath (Join-Path $work 'input\Cropster-Offline-OTL.cfg') -Force -ErrorAction SilentlyContinue
Remove-Item -LiteralPath (Join-Path $work 'input\roastMane.cfg') -Force -ErrorAction SilentlyContinue

$classpath = (Join-Path $BaseBundle 'app\*') + ';' + (Join-Path $BaseBundle 'app\javafx\*') + ';' + (Join-Path $sourceRoot 'lib\asm.jar')
& (Join-Path $jdk 'javac.exe') -proc:none -encoding UTF-8 -cp $classpath -d (Join-Path $work 'classes') (Get-ChildItem -LiteralPath (Join-Path $sourceRoot 'patch-src\local') -Filter '*.java' | ForEach-Object FullName)
if ($LASTEXITCODE -ne 0) { throw 'Patch compilation failed.' }

$patchedJar = Join-Path $work 'input\roastingintelligence-5.13.jar'
$originalJar = Join-Path $BaseBundle 'app\roastingintelligence-5.13.jar'
& (Join-Path $jdk 'java.exe') -cp ((Join-Path $work 'classes') + ';' + (Join-Path $sourceRoot 'lib\asm.jar')) local.PatchJar $originalJar $patchedJar $brandKit
if ($LASTEXITCODE -ne 0) { throw 'JAR patch and rebrand failed.' }

$icon = Join-Path $work 'roastMane.ico'
& (Join-Path $jdk 'java.exe') -cp (Join-Path $work 'classes') local.BuildIcon $patchedJar $icon
Get-ChildItem -LiteralPath (Join-Path $work 'classes\local') -Filter 'PatchJar*.class' | Remove-Item -Force
Remove-Item -LiteralPath (Join-Path $work 'classes\local\BuildIcon.class') -Force
& (Join-Path $jdk 'jar.exe') --create --file (Join-Path $work 'input\offline-local.jar') -C (Join-Path $work 'classes') .
if ($LASTEXITCODE -ne 0) { throw 'offline-local.jar packaging failed.' }

$options = @(
  '--type','app-image','--name','roastMane','--app-version','0.3.0',
  '--vendor','O Tesla','--description','roastMane local offline roast logging',
  '--input',(Join-Path $work 'input'),'--dest',(Join-Path $work 'image'),
  '--main-jar','offline-local.jar','--main-class','local.Launcher','--icon',$icon,
  '--java-options','-Djava.security.manager=allow',
  '--java-options','--module-path=$APPDIR/javafx',
  '--java-options','--add-modules=javafx.controls,javafx.fxml,javafx.swing,javafx.web,javafx.media',
  '--java-options','--add-exports=javafx.base/com.sun.javafx=ALL-UNNAMED',
  '--java-options','--add-exports=javafx.controls/com.sun.javafx.scene.control.behavior=ALL-UNNAMED',
  '--java-options','--add-exports=javafx.controls/com.sun.javafx.scene.control.inputmap=ALL-UNNAMED',
  '--java-options','--add-exports=javafx.controls/com.sun.javafx.scene.control.skin=ALL-UNNAMED',
  '--java-options','--add-exports=javafx.controls/com.sun.javafx.scene.control=ALL-UNNAMED',
  '--java-options','--add-exports=javafx.graphics/com.sun.javafx.print=ALL-UNNAMED',
  '--java-options','--add-exports=javafx.graphics/com.sun.javafx.scene=ALL-UNNAMED',
  '--java-options','--add-exports=javafx.graphics/com.sun.prism.impl=ALL-UNNAMED',
  '--java-options','--add-exports=javafx.graphics/com.sun.prism=ALL-UNNAMED',
  '--java-options','--add-opens=javafx.controls/javafx.scene.control.skin=ALL-UNNAMED'
)
& (Join-Path $jdk 'jpackage.exe') @options
if ($LASTEXITCODE -ne 0) { throw 'app-image packaging failed.' }

Move-Item -LiteralPath (Join-Path $work 'image\roastMane') -Destination $Output
New-Item -ItemType Directory -Path (Join-Path $Output 'source') | Out-Null
Copy-Item -LiteralPath (Join-Path $sourceRoot 'patch-src') -Destination (Join-Path $Output 'source\patch-src') -Recurse
Copy-Item -LiteralPath (Join-Path $sourceRoot 'lib') -Destination (Join-Path $Output 'source\lib') -Recurse
Copy-Item -LiteralPath (Join-Path $sourceRoot 'tests') -Destination (Join-Path $Output 'source\tests') -Recurse
Copy-Item -LiteralPath (Join-Path $sourceRoot 'brand-kit') -Destination (Join-Path $Output 'source\brand-kit') -Recurse
Copy-Item -LiteralPath (Join-Path $sourceRoot 'build.ps1') -Destination (Join-Path $Output 'source\build.ps1')
Copy-Item -LiteralPath (Join-Path $sourceRoot 'README.md') -Destination (Join-Path $Output 'README.md')
Copy-Item -LiteralPath (Join-Path $sourceRoot 'KIEM-THU.md') -Destination (Join-Path $Output 'KIEM-THU.md')
Get-ChildItem -LiteralPath (Join-Path $Output 'source\brand-kit') -Recurse -File | Get-FileHash -Algorithm SHA256 | ForEach-Object { "$($_.Hash)  $($_.Path.Substring((Join-Path $Output 'source\brand-kit').Length + 1))" } | Set-Content -LiteralPath (Join-Path $Output 'source\brand-kit\SHA256SUMS.txt') -Encoding UTF8
Get-ChildItem -LiteralPath $Output -Recurse -File | Get-FileHash -Algorithm SHA256 | ForEach-Object { "$($_.Hash)  $($_.Path.Substring($Output.Length + 1))" } | Set-Content -LiteralPath (Join-Path $Output 'SHA256SUMS.txt') -Encoding UTF8
Write-Host "Created: $Output"
