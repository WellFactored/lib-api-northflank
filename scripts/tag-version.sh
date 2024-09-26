#!/usr/bin/env bash

git fetch --prune --unshallow --tags

commit_message=$(git log -1 --pretty=%B)
if echo "$commit_message" | grep -q "(MAJOR)"; then
    RELEASE="major"
elif echo "$commit_message" | grep -q "(MINOR)"; then
    RELEASE="minor"
else
    RELEASE="patch"
fi

sbt "writeVersion target/version.txt" -Drelease=$RELEASE -Dsbt.log.noformat=true
VERSION=$(cat target/version.txt)

git tag "v${VERSION}"
git push origin "v${VERSION}"
