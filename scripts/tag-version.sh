#!/usr/bin/env bash

git fetch --prune --unshallow --tags
sbt "writeVersion target/version.txt" -Drelease=minor -Dsbt.log.noformat=true
export VERSION=$(cat target/version.txt)

git tag "v${VERSION}"
git push origin "v${VERSION}"
