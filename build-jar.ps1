# Alternative: Create JAR with proper directory structure
# This version ensures the temp directory structure is correct

Write-Host "Creating assembler.jar..."

# Ensure classes are compiled
if (-not (Test-Path "target\classes\com")) {
    Write-Host "[ERROR] Compiled classes not found. Running compilation..."
    javac -d target/classes src/main/java/com/csci6461/assembler/*.java
    if ($LASTEXITCODE -ne 0) {
        Write-Host "[ERROR] Compilation failed!"
        exit 1
    }
}

# Load assembly
Add-Type -AssemblyName System.IO.Compression.FileSystem

# Remove existing JAR
if (Test-Path "assembler.jar") {
    Remove-Item "assembler.jar"
}

# Create JAR directly from target/classes with manifest injection
$tempJar = "temp.jar"
$manifestDir = "temp_manifest"

# Create manifest directory
if (Test-Path $manifestDir) {
    Remove-Item -Recurse -Force $manifestDir
}
New-Item -ItemType Directory -Path "$manifestDir\META-INF" | Out-Null

# Write manifest
$manifest = @"
Manifest-Version: 1.0
Main-Class: com.csci6461.assembler.Assembler

"@
[System.IO.File]::WriteAllText("$manifestDir\META-INF\MANIFEST.MF", $manifest, [System.Text.Encoding]::ASCII)

# Create JAR using Java's jar command workaround
# Since jar is not available, manually create ZIP
try {
    # Create empty ZIP
    [System.IO.Compression.ZipFile]::Open((Join-Path (Get-Location) "assembler.jar"), 'Create').Dispose()
    
    # Open for update
    $jar = [System.IO.Compression.ZipFile]::Open((Join-Path (Get-Location) "assembler.jar"), 'Update')
    
    # Add manifest
    $manifestEntry = $jar.CreateEntry("META-INF/MANIFEST.MF")
    $writer = New-Object System.IO.StreamWriter($manifestEntry.Open())
    $writer.Write($manifest)
    $writer.Close()
    
    # Add all .class files
    Get-ChildItem -Path "target\classes" -Recurse -File | ForEach-Object {
        $relativePath = $_.FullName.Substring((Resolve-Path "target\classes").Path.Length + 1).Replace('\', '/')
        Write-Host "Adding: $relativePath"
        [System.IO.Compression.ZipFileExtensions]::CreateEntryFromFile($jar, $_.FullName, $relativePath) | Out-Null
    }
    
    $jar.Dispose()
    
    Write-Host "[SUCCESS] JAR created successfully: assembler.jar"
    Write-Host ""
    Write-Host "Usage: java -jar assembler.jar `<input_file.asm`>"
    
} catch {
    Write-Host "[ERROR] JAR creation failed: $_"
    exit 1
} finally {
    if (Test-Path $manifestDir) {
        Remove-Item -Recurse -Force $manifestDir
    }
}
