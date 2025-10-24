#!/bin/bash

# Test script for MFM Raw Text Parser
# This script demonstrates how to use the new Java-based MFM parser

BASE_URL="http://localhost:8080/api/mfm/raw-parser"
MFM_FILE_PATH="../gameSystems/40K/mfm/RAW_MFM_3_2_Aug25.txt"

echo "=== MFM Raw Text Parser Test Script ==="
echo "Base URL: $BASE_URL"
echo "MFM File: $MFM_FILE_PATH"
echo ""

# Check if the MFM file exists
if [ ! -f "$MFM_FILE_PATH" ]; then
    echo "Error: MFM file not found at $MFM_FILE_PATH"
    echo "Please ensure the file exists and the path is correct."
    exit 1
fi

echo "1. Testing MFM file parsing..."
echo "   Parsing file: $MFM_FILE_PATH"
echo ""

# Parse the MFM file
PARSE_RESPONSE=$(curl -s -X POST "$BASE_URL/parse-file" \
    -d "filePath=$MFM_FILE_PATH")

echo "Parse Response:"
echo "$PARSE_RESPONSE" | jq '.' 2>/dev/null || echo "$PARSE_RESPONSE"
echo ""

# Extract version from response
VERSION=$(echo "$PARSE_RESPONSE" | jq -r '.version' 2>/dev/null)

if [ "$VERSION" = "null" ] || [ -z "$VERSION" ]; then
    echo "Error: Could not extract version from parse response"
    exit 1
fi

echo "2. Testing validation..."
echo "   Validating version: $VERSION"
echo ""

# Validate the parsed data
VALIDATION_RESPONSE=$(curl -s -X POST "$BASE_URL/validate" \
    -d "version=$VERSION" \
    -d "originalFilePath=$MFM_FILE_PATH")

echo "Validation Response:"
echo "$VALIDATION_RESPONSE" | jq '.' 2>/dev/null || echo "$VALIDATION_RESPONSE"
echo ""

# Extract validation results
MATCHES=$(echo "$VALIDATION_RESPONSE" | jq -r '.matches' 2>/dev/null)
DIFFERENCES=$(echo "$VALIDATION_RESPONSE" | jq -r '.differencesCount' 2>/dev/null)
PERFECT_MATCH=$(echo "$VALIDATION_RESPONSE" | jq -r '.isPerfectMatch' 2>/dev/null)
MATCH_PERCENTAGE=$(echo "$VALIDATION_RESPONSE" | jq -r '.matchPercentage' 2>/dev/null)

echo "3. Validation Summary:"
echo "   Matches: $MATCHES"
echo "   Differences: $DIFFERENCES"
echo "   Perfect Match: $PERFECT_MATCH"
echo "   Match Percentage: $MATCH_PERCENTAGE%"
echo ""

echo "4. Testing file regeneration..."
echo "   Regenerating file for version: $VERSION"
echo ""

# Regenerate the file
REGENERATE_RESPONSE=$(curl -s -X GET "$BASE_URL/regenerate/$VERSION")

echo "Regenerate Response (first 500 characters):"
echo "$REGENERATE_RESPONSE" | jq -r '.content' 2>/dev/null | head -c 500 || echo "Could not extract content"
echo "..."
echo ""

echo "5. Generating validation report..."
echo "   Creating report for version: $VERSION"
echo ""

# Generate validation report
REPORT_PATH="/tmp/mfm_validation_report_$VERSION.txt"
REPORT_RESPONSE=$(curl -s -X POST "$BASE_URL/validation-report" \
    -d "version=$VERSION" \
    -d "originalFilePath=$MFM_FILE_PATH" \
    -d "outputPath=$REPORT_PATH")

echo "Report Generation Response:"
echo "$REPORT_RESPONSE" | jq '.' 2>/dev/null || echo "$REPORT_RESPONSE"
echo ""

# Check if report was created
if [ -f "$REPORT_PATH" ]; then
    echo "6. Validation Report Contents:"
    echo "   Report saved to: $REPORT_PATH"
    echo ""
    echo "Report Preview (first 20 lines):"
    head -20 "$REPORT_PATH"
    echo ""
    echo "Report size: $(wc -l < "$REPORT_PATH") lines"
else
    echo "6. Validation Report:"
    echo "   Report file not found at: $REPORT_PATH"
fi

echo ""
echo "=== Test Complete ==="
echo ""
echo "Summary:"
echo "- Parsed MFM file: $MFM_FILE_PATH"
echo "- Version: $VERSION"
echo "- Matches: $MATCHES"
echo "- Differences: $DIFFERENCES"
echo "- Match Percentage: $MATCH_PERCENTAGE%"

if [ "$PERFECT_MATCH" = "true" ]; then
    echo "- Status: ✓ Perfect match!"
else
    echo "- Status: ⚠ Some differences found (this may be normal)"
fi

echo ""
echo "To view the full validation report:"
echo "cat $REPORT_PATH"
