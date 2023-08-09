#!/usr/bin/env bash

git fetch --prune --unshallow --tags
sbt "writeVersion target/version.txt" -Dsbt.log.noformat=true
export VERSION=$(cat target/version.txt)

sbt publish
