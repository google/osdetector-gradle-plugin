# OS Detector Plugin for Gradle
A Gradle plugin that detects the OS name and architecture, providing a uniform
classifier to be used in the names of native artifacts.

It uses [os-maven-plugin](https://github.com/trustin/os-maven-plugin) under the
hood thus produces the same result.

## Latest version
The latest version ``1.4.0`` is available on Maven Central.
Its output is identical to ``os-maven-plugin:1.4.0.Final``.

## Usage
To use this plugin, include in your build script:
```groovy
apply plugin: 'com.google.osdetector'

buildcript {
  repositories {
    mavenCentral()
  }
  dependencies {
    classpath 'com.google.gradle:osdetector-gradle-plugin:1.4.0'
  }
}
```

The plugin creates ``osdetector`` extension in your project, through which you
can access the following attributes:
- ``osdetector.os``: normalized OS name
- ``osdetector.arch``: architecture
- ``osdetector.classifier``: classifier, which is explained in the following
  subsection
- ``osdetector.release``: only vailable if ``osdetector.os`` is ``linux``.
  ``null`` on non-linux systems. It provides additional information about the
  linux release:
 - ``id``: the ID for the linux release
 - ``version``: the version ID for this linux release
 - ``isLike(baseRelease)``: ``true`` if this release is a variant of the given
   base release. For example, ubuntu is a variant of debian, so on a debian or
   ubuntu system ``isLike('debian`)`` returns ``true``.

Before you access any of those attributes, you can configure
``osdetector.classifierWithLikes`` to add additional suffix to ``classifier`` to
reflect the linux distribution. See the following subsection for details.

### Classifier
The classifier is a string that ``osdetector.classifier`` gives you. By default
it is ``osdetector.os + '-' + osdetector.arch``, e.g., ``linux-x86_64``.

Optionally, you can give a list of linux distributions to
``osdetector.classifierWithLikes`` (note this must be done before you access any
of those attributes). The plugin will test ``isLike()`` with each distribution
on the list, and it will append the first one that returns ``true`` to the
classifier. If none returns ``true``, the classifier stays unchanged. The
following table illustrates what the classifier may look like in various cases:

classifierWithLikes      | distribution | classifier
------------------------ | ------------ | -----------------------
``['debian', 'fedora']`` | Ubuntu       | ``linux-x86_64-debian``
``['debian', 'fedora']`` | RHEL         | ``linux-x86_64-fedora``
``['debian', 'fedora']`` | Gentoo       | ``linux-x86_64``
unset or empty           | any          | ``linux-x86_64``

### Typical usage example

```groovy
apply plugin: 'com.google.osdetector'
osdetector.classifierWithLikes = ['debian', 'fedora']

artifacts {
  archives(artifactFile) {
    classifier osdetector.classifier
    type "exe"
    extension "exe"
    builtBy buildArtifact
  }
}
```

## To build and install locally
```
$ git clone git@github.com:google/osdetector-gradle-plugin.git
$ cd osdetector-gradle-plugin
$ ./gradlew install
```
