#!/usr/bin/env bash
# Fail if any commands fails
set -e
 
# Step 1: get the upload URL
step1_response=$(curl -X POST -H "Authorization: APIKey ${DT_UPLOAD_API_KEY}"  --data ""  https://api.securetheorem.com/uploadapi/v1/upload_init)
upload_url=$(echo ${step1_response} | cut -f 3 -d" "  | tr -d '"')
 
# Step 2: upload the APK
step2_response=$(curl -F file=@${SIGNED_BINARY_PATH} ${upload_url} --retry 3)

