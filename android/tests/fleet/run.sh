#!/bin/sh
set -eu
suite_dir=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
project_android=$(CDPATH= cd -- "$suite_dir/../.." && pwd)
test_build=$(mktemp -d)
trap 'rm -rf "$test_build"' EXIT
javac -d "$test_build" "$suite_dir"/android/content/*.java "$suite_dir"/local/ambilight/reader/*.java "$project_android"/src/local/ambilight/reader/HueFleet.java "$project_android"/src/local/ambilight/reader/BulbPosition.java
java -cp "$test_build" local.ambilight.reader.FleetTest
