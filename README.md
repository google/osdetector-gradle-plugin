# OS Detector Plugin for Gradle
A Gradle plugin that detects the OS name and architecture, providing a uniform
classifier to be used in the names of native artifacts.

It uses [os-maven-plugin](https://github.com/trustin/os-maven-plugin) under the
hood thus produces the same result.

## Latest version
Not available on any repository yet. You have to download the source, build and
install it locally:

```
$ git clone git@github.com:google/osdetector-gradle-plugin.git
$ cd osdetector-gradle-plugin
$ ./gradlew install
```

## Usage
To use this plugin, include in your build script:
```groovy
apply plugin: 'osdetector'

buildcript {
  repositories {
    mavenCentral()
    mavenLocal()
  }
  dependencies {
    classpath 'com.google.gradle:osdetector-gradle-plugin:0.1.0-SNAPSHOT'
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
