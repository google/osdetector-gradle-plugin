# OS Detector Plugin for Gradle
A Gradle plugin that detects the OS name and architecture, providing a uniform
classifier to be used in the names of native artifacts.

It uses [os-maven-plugin](https://github.com/trustin/os-maven-plugin) under the
hood thus produces the same result.

## Latest version
The latest version ``1.2.0`` is available on Maven Central.
Its output is identical to ``os-maven-plugin:1.2.3.Final``.

## Usage
To use this plugin, include in your build script:
```groovy
apply plugin: 'osdetector'

buildcript {
  repositories {
    mavenCentral()
  }
  dependencies {
    classpath 'com.google.gradle:osdetector-gradle-plugin:1.2.0'
  }
}
```

The plugin creates ``osdetector`` extension in your project, through which you
can access the normalized OS name (``osdetector.os``), architecture
(``osdetector.arch``) and classifier (``osdetector.classifier``). For example:
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

## To build and install locally
```
$ git clone git@github.com:google/osdetector-gradle-plugin.git
$ cd osdetector-gradle-plugin
$ ./gradlew install
```
