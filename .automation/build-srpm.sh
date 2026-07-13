#!/bin/bash -xe

# Mark current directory as safe for git to be able to parse git hash
git config --global --add safe.directory $(pwd)

# Directory, where build artifacts will be stored, should be passed as the 1st parameter
ARTIFACTS_DIR=${1:-exported-artifacts}

# Get version from Maven, strip SNAPSHOT suffix
VERSION=$(mvn help:evaluate -q -DforceStdout -Dexpression=project.version | sed 's/-SNAPSHOT//')

# Set the RPM release (default to 0.master for snapshot builds)
PACKAGE_RPM_RELEASE=${PACKAGE_RPM_RELEASE:-0.master}

# Prepare source archive
[[ -d rpmbuild/SOURCES ]] || mkdir -p rpmbuild/SOURCES
git archive --format=tar HEAD | gzip -9 > rpmbuild/SOURCES/ovirt-engine-api-metamodel-${VERSION}.tar.gz

# Set version and release
sed \
    -e "s|@VERSION@|${VERSION}|g" \
    -e "s|@PACKAGE_RPM_RELEASE@|${PACKAGE_RPM_RELEASE}|g" \
    < ovirt-engine-api-metamodel.spec.in \
    > ovirt-engine-api-metamodel.spec

# Build source package
rpmbuild \
    -D "_topdir $(pwd)/rpmbuild" \
    --define "release_suffix ${RELEASE_SUFFIX:-}" \
    -bs ovirt-engine-api-metamodel.spec
