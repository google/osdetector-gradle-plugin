// Normalization logic ported from
// https://github.com/trustin/os-maven-plugin/blob/master/src/main/java/kr/motd/maven/os/Detector.java

package com.google.gradle.plugins.osdetector

import org.gradle.api.Plugin
import org.gradle.api.Project

class OsDetectorPlugin implements Plugin<Project> {
  void apply(final Project project) {
    project.extensions.create('osdetector', OsDetectorExtension)
  }
}

class OsDetectorExtension {
  def String os = OsDetector.os()
  def String arch = OsDetector.arch()
  def String classifier = OsDetector.classifier()
}
