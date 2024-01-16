#!/usr/bin/bash

# If the APK-extracted directory doesn't exist, create it
if [ -d "APK-extracted" ]; then
    rm -rf "APK-extracted"
fi
mkdir "APK-extracted"
cd "APK-extracted" || exit
cp ../"$1" "$1"
../dex-tools/d2j-dex2jar.sh "$1"
# Get every .jar file in the current directory and rename it to .zip
for f in *.jar; do
    mv -- "$f" "${f%.jar}.zip"
done
# Unzip every .zip file in the current directory
for f in *.zip; do
    unzip "$f"
done