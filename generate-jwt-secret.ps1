# Generate a secure JWT secret for production
# This script generates a cryptographically secure random string suitable for JWT signing
# JWT tokens will expire after 30 days (2592000000 milliseconds)

Write-Host "Generating JWT secret for production..." -ForegroundColor Green
Write-Host ""

# Generate a 64-character random string
$bytes = New-Object byte[] 48
$rng = [System.Security.Cryptography.RNGCryptoServiceProvider]::Create()
$rng.GetBytes($bytes)
$JWT_SECRET = [System.Convert]::ToBase64String($bytes) -replace '[+/=]', '' | Select-Object -First 64

Write-Host "JWT_SECRET=$JWT_SECRET" -ForegroundColor Yellow
Write-Host ""
Write-Host "Add this to your production environment variables:" -ForegroundColor Cyan
Write-Host "`$env:JWT_SECRET = `"$JWT_SECRET`"" -ForegroundColor White
Write-Host ""
Write-Host "Or for Railway deployment, add it to your environment variables in the Railway dashboard." -ForegroundColor Cyan
Write-Host ""
Write-Host "⚠️  IMPORTANT: Keep this secret secure and never commit it to version control!" -ForegroundColor Red
