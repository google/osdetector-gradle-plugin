# OS Detector Plugin for Gradle
A Gradle plugin that detects the OS name and architecture, providing a uniform
classifier to be used in the names of native artifacts.

It uses [os-maven-plugin](https://github.com/trustin/os-maven-plugin) under the
hood thus produces the same result.

## Latest version
The latest version ``1.3.0`` is available on Maven Central.
Its output is identical to ``os-maven-plugin:1.3.0.Final``.

## Usage
To use this plugin, include in your build script:
```groovy
apply plugin: 'osdetector'

buildcript {
  repositories {
    mavenCentral()
  }
  dependencies {
    classpath 'com.google.gradle:osdetector-gradle-plugin:1.2.1'
  }
}
```

The plugin creates ``osdetector`` extension in your project, through which you
can access the following attributes:
- ``osdetector.os``: normalized OS name
- ``osdetector.arch``: architecture
- ``osdetector.classifier``: classifier, which is ``osdetector.os + '-' +
  osdetector.arch``
- ``osdetector.release``: only vailable if ``osdetector.os`` is ``linux``.
  ``null`` on non-linux systems. It provides additional information about the
  linux release:
 - ``id``: the ID for the linux release
 - ``version``: the version ID for this linux release
 - ``isLike(baseRelease)``: ``true`` if this release is a variant of the given
   base release. For example, ubuntu is a variant of debian, so on a debian or
   ubuntu system ``isLike('debian`)`` returns ``true``.

## Examples

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

### To differentiate between debian-like, redhat-like and other linux systems
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
