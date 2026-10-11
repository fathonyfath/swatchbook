#!/usr/bin/env bash
# Builds, shrinks, signs and verifies the release APK.
#
# Usage: ./release.sh <keystore> [key-alias]
#        ./release.sh --version
#
# The version is the base version in android/build.gradle.kts followed by the number of
# commits, such as 1.0.57. That number is the version code too: it only ever grows on a
# branch that is never rewritten, and a commit always gets the same one. --version prints it
# without building anything.
#
# The keystore and alias can also be given as RELEASE_KEYSTORE and RELEASE_KEY_ALIAS.
# apksigner asks for the passwords, unless RELEASE_KS_PASS (and RELEASE_KEY_PASS when the
# key has its own password) are set.
set -euo pipefail

cd "$(dirname "$0")"

if [ "$(git rev-parse --is-shallow-repository)" = true ]; then
    echo "A shallow clone cannot count the commits the version is made from." >&2
    exit 1
fi
BASE_VERSION="$(sed -n 's/^val baseVersion = "\(.*\)"$/\1/p' android/build.gradle.kts)"
COMMIT_COUNT="$(git rev-list --count HEAD)"
COMMIT="$(git rev-parse --short=7 HEAD)"
VERSION="$BASE_VERSION.$COMMIT_COUNT"

if [ "${1:-}" = "--version" ]; then
    echo "$VERSION"
    exit
fi

# The version names a commit, so the build has to be of exactly that commit.
if ! git diff --quiet HEAD; then
    echo "There are uncommitted changes: commit them before releasing." >&2
    exit 1
fi

KEYSTORE="${1:-${RELEASE_KEYSTORE:-}}"
KEY_ALIAS="${2:-${RELEASE_KEY_ALIAS:-}}"

if [ -z "$KEYSTORE" ] || [ ! -f "$KEYSTORE" ]; then
    echo "Usage: $0 <keystore> [key-alias]" >&2
    exit 1
fi

SDK_DIR="${ANDROID_HOME:-${ANDROID_SDK_ROOT:-}}"
if [ -z "$SDK_DIR" ] && [ -f local.properties ]; then
    SDK_DIR="$(sed -n 's/^sdk\.dir=//p' local.properties)"
fi

BUILD_TOOLS="$(ls -d "$SDK_DIR"/build-tools/*/ 2>/dev/null | sort -V | tail -n 1)"
if [ -z "$BUILD_TOOLS" ]; then
    echo "Build-tools not found, set ANDROID_HOME or install the Android SDK build-tools." >&2
    exit 1
fi
APKSIGNER="${BUILD_TOOLS}apksigner"
ZIPALIGN="${BUILD_TOOLS}zipalign"

UNSIGNED="android/build/outputs/apk/release/android-release-unsigned.apk"
OUTPUT="release/swatchbook-v$VERSION.apk"
# Needed to read stack traces of the obfuscated build.
MAPPING="release/swatchbook-v$VERSION-mapping.txt"

./gradlew clean lint assembleRelease \
    -PreleaseVersionCode="$COMMIT_COUNT" \
    -PreleaseVersionName="$VERSION+$COMMIT"

WORK="$(mktemp -d)"
trap 'rm -rf "$WORK"' EXIT

# Gradle deflates quickly rather than tightly. Repacking the APK with 7-Zip's slowest
# deflate makes it a few percent smaller, at no cost to the app. Entries that Gradle left
# uncompressed stay that way: Android reads those, such as resources.arsc, in place.
if command -v 7z >/dev/null; then
    mkdir "$WORK/apk"
    7z x -y -o"$WORK/apk" "$UNSIGNED" >/dev/null
    unzip -Z1 "$UNSIGNED" | grep -v '/$' >"$WORK/all"
    unzip -v "$UNSIGNED" | awk '$2 == "Stored" { print $8 }' >"$WORK/stored"
    grep -vxFf "$WORK/stored" "$WORK/all" >"$WORK/deflated" || true
    (
        cd "$WORK/apk"
        [ ! -s "$WORK/deflated" ] ||
            7z a -tzip -mx=9 -mfb=258 -mpass=15 "$WORK/repacked.zip" @"$WORK/deflated" >/dev/null
        [ ! -s "$WORK/stored" ] ||
            7z a -tzip -mx=0 "$WORK/repacked.zip" @"$WORK/stored" >/dev/null
    )
    SHRUNK="$WORK/repacked.zip"
else
    echo "7z not found: skipping the repack step, the APK will be slightly larger." >&2
    SHRUNK="$UNSIGNED"
fi

# Must come before signing: aligning afterwards would invalidate the signature.
"$ZIPALIGN" -f -P 16 4 "$SHRUNK" "$WORK/aligned.apk"

SIGN_ARGS=(--ks "$KEYSTORE")
if [ -n "$KEY_ALIAS" ]; then
    SIGN_ARGS+=(--ks-key-alias "$KEY_ALIAS")
fi
if [ -n "${RELEASE_KS_PASS:-}" ]; then
    SIGN_ARGS+=(--ks-pass env:RELEASE_KS_PASS)
fi
if [ -n "${RELEASE_KEY_PASS:-}" ]; then
    SIGN_ARGS+=(--key-pass env:RELEASE_KEY_PASS)
fi

mkdir -p release
"$APKSIGNER" sign "${SIGN_ARGS[@]}" --out "$OUTPUT" "$WORK/aligned.apk"
rm -f "$OUTPUT.idsig"
cp android/build/outputs/mapping/release/mapping.txt "$MAPPING"

"$APKSIGNER" verify --print-certs "$OUTPUT"
echo
echo "Unsigned from Gradle: $(stat -c %s "$UNSIGNED") bytes"
echo "Signed release:       $(stat -c %s "$OUTPUT") bytes  ->  $OUTPUT"
