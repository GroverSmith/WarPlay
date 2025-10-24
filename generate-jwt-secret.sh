#!/bin/bash

# Generate a secure JWT secret for production
# This script generates a cryptographically secure random string suitable for JWT signing
# JWT tokens will expire after 30 days (2592000000 milliseconds)

echo "Generating JWT secret for production..."
echo ""

# Generate a 64-character random string using OpenSSL
JWT_SECRET=$(openssl rand -base64 48 | tr -d "=+/" | cut -c1-64)

echo "JWT_SECRET=$JWT_SECRET"
echo ""
echo "Add this to your production environment variables:"
echo "export JWT_SECRET=\"$JWT_SECRET\""
echo ""
echo "Or for Railway deployment, add it to your environment variables in the Railway dashboard."
echo ""
echo "⚠️  IMPORTANT: Keep this secret secure and never commit it to version control!"
