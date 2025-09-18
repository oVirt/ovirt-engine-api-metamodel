# oVirt Engine API Metamodel

[![Copr build status](https://copr.fedorainfracloud.org/coprs/ovirt/ovirt-master-snapshot/package/ovirt-engine-api-metamodel/status_image/last_build.png)](https://copr.fedorainfracloud.org/coprs/ovirt/ovirt-master-snapshot/package/ovirt-engine-api-metamodel/)

Welcome to the oVirt Engine API Metamodel source repository. This repository is
hosted on [GitHub:ovirt-engine-api-metamodel](https://github.com/oVirt/ovirt-engine-api-metamodel).

This project contains the oVirt Engine API Metamodel. It is a set of tools that
read, analyze and generate code from the API model.

## Building

To build this project use the usual Maven command line:

  $ mvn clean install

## Releasing

The project is released to Maven Central using the Central Publishing Maven
Plugin.

To perform a release you will need to do the following actions, most of
them automated by the Maven release plugin:

### Prepare the release

This is automated using the Maven release plugin:

  $ mvn release:prepare

This will ask you the version numbers to use for the released artifacts
and the version numbers to use after the release. The release version
numbers will be something like 1.0.4, and the version numbers after the
release will be something like 1.0.5-SNAPSHOT. You should use the
defaults unless there is a very good reason to change them.

The result will be two new patches, and a tag added to the local
repository. These patches and tag will *not* be pushed automatically to
the remote repository, so you need to do it manually, first the patches:

  $ git push origin HEAD:refs/for/master

This will send the patches for review to https://github.com/oVirt[github].
Go there, review and merge them. Once the patches are merged the tag can
be pushed:

  $ git push origin 1.0.4

This tag is important so that the release tools notice that a new version has
been released.

### Perform the release

This is also automated using the Maven release plugin. But in this case
it is necessary to sign the artifacts, as Maven Central requires signed
artifacts. To sign artifacts the `sign` profile needs to be activated:

  $ mvn release:perform -Psign

NOTE: The artifacts will be signed using your default GPG key, so make
sure you have a valid GPG key available.

This will build the code from the repository where it is triggered,
run the tests and, finally, if everything succeeds, it will upload
the signed artifacts to the Maven Central repository.

The rest of the process is manual, using the Central Sonatype web interface
available https://central.sonatype.com[here]. Information about this publishing
can be found https://central.sonatype.org/publish/publish-portal-guide/[here].

## How to contribute

All contributions are welcome - patches, bug reports, and documentation issues.

### Submitting patches

Please submit patches to [GitHub:ovirt-engine-api-metamodel](https://github.com/oVirt/ovirt-engine-api-metamodel).
If you are not familiar with the process, you can read about
[collaborating with pull requests](https://docs.github.com/en/pull-requests/collaborating-with-pull-requests/proposing-changes-to-your-work-with-pull-requests)
on the GitHub website.

### Found a bug or documentation issue?

To submit a bug or suggest an enhancement for oVirt Engine API Metamodel please
use [Github Issues](https://github.com/oVirt/ovirt-engine-api-metamodel/issues).
If you find a documentation issue on the oVirt website, please navigate to the
page footer and click "Report an issue on GitHub".

## Still need help?

If you have any other questions or suggestions, you can join and contact us on
the [oVirt Users forum / mailing list](https://lists.ovirt.org/hyperkitty/list/users@ovirt.org/).
