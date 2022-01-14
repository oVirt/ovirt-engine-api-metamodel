#!/bin/bash -ex

# git hash of current commit should be passed as the 1st paraameter
if [ "${GITHUB_SHA}" == "" ]; then
  GIT_HASH=$(git rev-list HEAD | wc -l)
else
  GIT_HASH=$(git rev-parse --short $GITHUB_SHA)
fi

# Directory, where build artifacts will be stored, should be passed as the 1st parameter
ARTIFACTS_DIR=${1:-exported-artifacts}

# Prepare the version string (with support for SNAPSHOT versioning)
VERSION=$(mvn help:evaluate  -q -DforceStdout -Dexpression=project.version)
VERSION=${VERSION/-SNAPSHOT/-0.${GIT_HASH}.$(date +%04Y%02m%02d%02H%02M)}
IFS='-' read -ra VERSION <<< "$VERSION"
RELEASE=${VERSION[1]-1}


# We will need the name of the package in several places:
name="ovirt-engine-api-metamodel"


# Generate the tarball:
git archive \
  --prefix="${name}-${version}/" \
  --output="${ARTIFACTS_DIR}/ovirt-engine-api-metamodel-${VERSION}-${RELEASE}.tar.gz" \
  HEAD


# Build the artifacts:
mvn package -DskipTests

# Deploy the artifacts to the artifacts directory:
mvn deploy -DaltDeploymentRepository="local::default::file://${ARTIFACTS_DIR}"
