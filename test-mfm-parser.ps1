# Test script for MFM Raw Text Parser
# This script demonstrates how to use the new Java-based MFM parser

$BaseUrl = "http://localhost:8080/api/mfm/raw-parser"
$MfmFilePath = "../gameSystems/40K/mfm/RAW_MFM_3_2_Aug25.txt"

Write-Host "=== MFM Raw Text Parser Test Script ===" -ForegroundColor Green
Write-Host "Base URL: $BaseUrl"
Write-Host "MFM File: $MfmFilePath"
Write-Host ""

# Check if the MFM file exists
if (-not (Test-Path $MfmFilePath)) {
    Write-Host "Error: MFM file not found at $MfmFilePath" -ForegroundColor Red
    Write-Host "Please ensure the file exists and the path is correct."
    exit 1
}

Write-Host "1. Testing MFM file parsing..." -ForegroundColor Yellow
Write-Host "   Parsing file: $MfmFilePath"
Write-Host ""

try {
    # Parse the MFM file
    $ParseResponse = Invoke-RestMethod -Uri "$BaseUrl/parse-file" -Method Post -Body @{
        filePath = $MfmFilePath
    } -ContentType "application/x-www-form-urlencoded"

    Write-Host "Parse Response:" -ForegroundColor Cyan
    $ParseResponse | ConvertTo-Json -Depth 3
    Write-Host ""

    # Extract version from response
    $Version = $ParseResponse.version

    if (-not $Version) {
        Write-Host "Error: Could not extract version from parse response" -ForegroundColor Red
        exit 1
    }

    Write-Host "2. Testing validation..." -ForegroundColor Yellow
    Write-Host "   Validating version: $Version"
    Write-Host ""

    # Validate the parsed data
    $ValidationResponse = Invoke-RestMethod -Uri "$BaseUrl/validate" -Method Post -Body @{
        version = $Version
        originalFilePath = $MfmFilePath
    } -ContentType "application/x-www-form-urlencoded"

    Write-Host "Validation Response:" -ForegroundColor Cyan
    $ValidationResponse | ConvertTo-Json -Depth 3
    Write-Host ""

    # Extract validation results
    $Matches = $ValidationResponse.matches
    $Differences = $ValidationResponse.differencesCount
    $PerfectMatch = $ValidationResponse.isPerfectMatch
    $MatchPercentage = $ValidationResponse.matchPercentage

    Write-Host "3. Validation Summary:" -ForegroundColor Yellow
    Write-Host "   Matches: $Matches"
    Write-Host "   Differences: $Differences"
    Write-Host "   Perfect Match: $PerfectMatch"
    Write-Host "   Match Percentage: $MatchPercentage%"
    Write-Host ""

    Write-Host "4. Testing file regeneration..." -ForegroundColor Yellow
    Write-Host "   Regenerating file for version: $Version"
    Write-Host ""

    # Regenerate the file
    $RegenerateResponse = Invoke-RestMethod -Uri "$BaseUrl/regenerate/$Version" -Method Get

    Write-Host "Regenerate Response (first 500 characters):" -ForegroundColor Cyan
    $Content = $RegenerateResponse.content
    if ($Content) {
        $Content.Substring(0, [Math]::Min(500, $Content.Length))
        Write-Host "..."
    } else {
        Write-Host "Could not extract content"
    }
    Write-Host ""

    Write-Host "5. Generating validation report..." -ForegroundColor Yellow
    Write-Host "   Creating report for version: $Version"
    Write-Host ""

    # Generate validation report
    $ReportPath = "$env:TEMP\mfm_validation_report_$Version.txt"
    $ReportResponse = Invoke-RestMethod -Uri "$BaseUrl/validation-report" -Method Post -Body @{
        version = $Version
        originalFilePath = $MfmFilePath
        outputPath = $ReportPath
    } -ContentType "application/x-www-form-urlencoded"

    Write-Host "Report Generation Response:" -ForegroundColor Cyan
    $ReportResponse | ConvertTo-Json -Depth 3
    Write-Host ""

    # Check if report was created
    if (Test-Path $ReportPath) {
        Write-Host "6. Validation Report Contents:" -ForegroundColor Yellow
        Write-Host "   Report saved to: $ReportPath"
        Write-Host ""
        Write-Host "Report Preview (first 20 lines):" -ForegroundColor Cyan
        Get-Content $ReportPath | Select-Object -First 20
        Write-Host ""
        $LineCount = (Get-Content $ReportPath).Count
        Write-Host "Report size: $LineCount lines"
    } else {
        Write-Host "6. Validation Report:" -ForegroundColor Yellow
        Write-Host "   Report file not found at: $ReportPath"
    }

    Write-Host ""
    Write-Host "=== Test Complete ===" -ForegroundColor Green
    Write-Host ""
    Write-Host "Summary:" -ForegroundColor Yellow
    Write-Host "- Parsed MFM file: $MfmFilePath"
    Write-Host "- Version: $Version"
    Write-Host "- Matches: $Matches"
    Write-Host "- Differences: $Differences"
    Write-Host "- Match Percentage: $MatchPercentage%"

    if ($PerfectMatch -eq $true) {
        Write-Host "- Status: ✓ Perfect match!" -ForegroundColor Green
    } else {
        Write-Host "- Status: ⚠ Some differences found (this may be normal)" -ForegroundColor Yellow
    }

    Write-Host ""
    Write-Host "To view the full validation report:" -ForegroundColor Cyan
    Write-Host "Get-Content $ReportPath"

} catch {
    Write-Host "Error occurred during testing:" -ForegroundColor Red
    Write-Host $_.Exception.Message
    Write-Host ""
    Write-Host "Make sure the backend server is running on localhost:8080" -ForegroundColor Yellow
}
