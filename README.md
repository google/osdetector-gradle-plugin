# OS Detector Plugin for Gradle
A Gradle plugin that detects the OS name and architecture, providing a uniform
classifier to be used in the names of native artifacts.

It uses [os-maven-plugin](https://github.com/trustin/os-maven-plugin) under the
hood thus produces the same result.

## Latest version
The latest version ``1.6.0`` is available on Maven Central.
Its output is identical to ``os-maven-plugin:1.6.0``.

## Usage
To use this plugin, include in your build script

### For Gradle 2.1 and higher:
```groovy
plugins {
  id "com.google.osdetector" version "1.6.0"
}
```

### For Gradle 1.x and 2.0:
```groovy
apply plugin: 'com.google.osdetector'

buildscript {
  repositories {
    mavenCentral()
  }
  dependencies {
    classpath 'com.google.gradle:osdetector-gradle-plugin:1.6.0'
  }
}
```

The plugin creates ``osdetector`` extension in your project, through which you
can access the following attributes:
- ``osdetector.os``: normalized OS name
- ``osdetector.arch``: architecture
- ``osdetector.classifier``: classifier, which is ``osdetector.os + '-' +
  osdetector.arch``, e.g., ``linux-x86_64``
- ``osdetector.release``: only vailable if ``osdetector.os`` is ``linux``.
  ``null`` on non-linux systems. It provides additional information about the
  linux release:
 - ``id``: the ID for the linux release
 - ``version``: the version ID for this linux release
 - ``isLike(baseRelease)``: ``true`` if this release is a variant of the given
   base release. For example, ubuntu is a variant of debian, so on a debian or
   ubuntu system ``isLike('debian`)`` returns ``true``.

**WARNING:** DO NOT USE ``osdetector.classifierWithLikes`` because it has a
known [issue](https://github.com/google/osdetector-gradle-plugin/issues/4). It
will be either removed or changed to a different form in the next version.

### Typical usage example
### To have separate artifacts for different operating systems

```groovy
artifacts {
  archives(artifactFile) {
    classifier osdetector.classifier
    type "exe"
    extension "exe"
    builtBy buildArtifact
  }
}
```

### To differentiate between debian-like, redhat-like and
other linux systems
```groovy
def getLinuxReleaseSuffix() {
  if (osdetector.release.isLike('debian')) {
    return 'debian'
  } else if (osdetector.release.isLike('redhat')) {
    return 'redhat'
  } else {
    return 'other'
  }
}

artifacts {
  archives(artifactFile) {
    classifier osdetector.classifier + '-' + getLinuxReleaseSuffix()
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
