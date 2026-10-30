param([string]$StudioRoot = 'C:\Program Files\Android\Android Studio')
$ErrorActionPreference='Stop'
$projectRoot=Split-Path -Parent $PSScriptRoot
$javaRuntime=if($env:JAVA_HOME){$env:JAVA_HOME}else{Join-Path $StudioRoot 'jbr'}
$layoutRoot=Join-Path $StudioRoot 'plugins\design-tools'
$compiled=Join-Path $projectRoot 'app\build\intermediates\javac\debug\compileDebugJavaWithJavac\classes'
$output=Join-Path $projectRoot 'build\visual-reference\render-classes'
New-Item -ItemType Directory -Force $output | Out-Null
$classpath="$layoutRoot\lib\layoutlib.jar;$StudioRoot\plugins\android\lib\layoutlib-api.jar;$StudioRoot\plugins\android\lib\*;$layoutRoot\lib\*;$StudioRoot\lib\*;$compiled"
Push-Location $projectRoot
try {
    & (Join-Path $javaRuntime 'bin\javac.exe') -proc:none -encoding UTF-8 -classpath $classpath -d $output (Join-Path $PSScriptRoot 'NativeRender.java')
    if($LASTEXITCODE -ne 0){throw 'Compile the Android app first, then retry the native preview.'}
    & (Join-Path $javaRuntime 'bin\java.exe') --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.util=ALL-UNNAMED -classpath "$classpath;$output" com.example.projetomarvel.NativeRender "$layoutRoot\resources\layoutlib\data"
    if($LASTEXITCODE -ne 0){throw 'Native rendering failed.'}
} finally {Pop-Location}
