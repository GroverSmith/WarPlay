# MFM Raw Text Parser Usage Guide

This document explains how to use the new Java-based MFM parser that can directly parse raw text files and convert them to database rows.

## Overview

The new parser consists of three main components:

1. **MfmRawTextParserService** - Parses raw MFM text files and stores data in database
2. **MfmValidationService** - Validates parsed data by regenerating files and comparing with originals
3. **MfmRawParserController** - REST API endpoints for parsing and validation

## Features

- Direct parsing of raw MFM text files (no need to convert to JavaScript first)
- Automatic faction, detachment, unit, and enhancement extraction
- Database storage with proper relationships
- Validation system that regenerates files and compares with originals
- Graceful handling of formatting differences (dots, spacing, etc.)

## API Endpoints

### Parse MFM File (Upload)
```
POST /api/mfm/raw-parser/parse
Content-Type: multipart/form-data

Parameters:
- file: The raw MFM text file to parse
```

### Parse MFM File (File Path)
```
POST /api/mfm/raw-parser/parse-file
Content-Type: application/x-www-form-urlencoded

Parameters:
- filePath: Path to the raw MFM text file on the server
```

### Validate Parsed Data
```
POST /api/mfm/raw-parser/validate
Content-Type: application/x-www-form-urlencoded

Parameters:
- version: MFM version to validate (e.g., "3.2")
- originalFilePath: Path to the original raw MFM file
```

### Regenerate MFM File
```
GET /api/mfm/raw-parser/regenerate/{version}
```

### Generate Validation Report
```
POST /api/mfm/raw-parser/validation-report
Content-Type: application/x-www-form-urlencoded

Parameters:
- version: MFM version to validate
- originalFilePath: Path to the original raw MFM file
- outputPath: Path where to save the validation report
```

## Usage Examples

### 1. Parse a Raw MFM File

```bash
# Using curl to upload a file
curl -X POST http://localhost:8080/api/mfm/raw-parser/parse \
  -F "file=@RAW_MFM_3_2_Aug25.txt"

# Using curl with file path (if file is on server)
curl -X POST http://localhost:8080/api/mfm/raw-parser/parse-file \
  -d "filePath=/path/to/RAW_MFM_3_2_Aug25.txt"
```

### 2. Validate Parsed Data

```bash
curl -X POST http://localhost:8080/api/mfm/raw-parser/validate \
  -d "version=3.2" \
  -d "originalFilePath=/path/to/RAW_MFM_3_2_Aug25.txt"
```

### 3. Generate Validation Report

```bash
curl -X POST http://localhost:8080/api/mfm/raw-parser/validation-report \
  -d "version=3.2" \
  -d "originalFilePath=/path/to/RAW_MFM_3_2_Aug25.txt" \
  -d "outputPath=/path/to/validation_report.txt"
```

## Response Format

### Successful Parse Response
```json
{
  "success": true,
  "version": "3.2",
  "unitsCount": 1250,
  "enhancementsCount": 180,
  "factionsCount": 25,
  "detachmentsCount": 45,
  "message": "MFM file parsed and stored successfully"
}
```

### Validation Response
```json
{
  "success": true,
  "version": "3.2",
  "matches": 1420,
  "differencesCount": 5,
  "isPerfectMatch": false,
  "matchPercentage": 99.6,
  "differences": [
    {
      "line": "Some Unit Name 5 models 100 pts",
      "originalCount": 1,
      "regeneratedCount": 0,
      "issue": "Missing in regenerated"
    }
  ],
  "message": "Validation completed"
}
```

## Database Schema

The parser creates the following database structure:

- **mfm_versions** - MFM version information
- **mfm_factions** - Faction data with supergroup and ally relationships
- **mfm_units** - Unit information
- **mfm_unit_variants** - Unit variants with model counts and points
- **mfm_detachments** - Detachment information
- **mfm_enhancements** - Enhancement data with points

## Validation Process

The validation system works by:

1. Reading the original raw MFM file
2. Regenerating the file from database data
3. Normalizing both files (removing dots, normalizing spacing, etc.)
4. Comparing line-by-line to find differences
5. Generating a detailed report of any discrepancies

## Error Handling

The parser includes comprehensive error handling for:

- Invalid file formats
- Missing version information
- Malformed unit entries
- Database constraint violations
- File I/O errors

## Future Enhancements

Potential improvements for the parser:

1. **Batch Processing** - Parse multiple files at once
2. **Incremental Updates** - Update existing data instead of replacing
3. **Advanced Validation** - More sophisticated diff algorithms
4. **Export Formats** - Export to different formats (JSON, CSV, etc.)
5. **Web UI** - Browser-based interface for parsing and validation

## Troubleshooting

### Common Issues

1. **Version Not Found** - Ensure the MFM version is properly extracted from the file
2. **Parse Errors** - Check that the file format matches expected MFM structure
3. **Validation Differences** - Minor formatting differences are expected and handled gracefully
4. **Database Errors** - Ensure database is properly configured and accessible

### Debug Mode

Enable debug logging to see detailed parsing information:

```properties
logging.level.com.warplay.service.MfmRawTextParserService=DEBUG
logging.level.com.warplay.service.MfmValidationService=DEBUG
```
