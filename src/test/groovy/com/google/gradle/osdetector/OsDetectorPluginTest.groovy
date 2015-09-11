/*
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
package com.google.gradle.osdetector

import org.gradle.testfixtures.ProjectBuilder
import org.gradle.api.Project
import org.junit.Test
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.fail

class OsDetectorPluginTest {
  @Test
  public void pluginAddsExtensionToProject() {
    Project project = ProjectBuilder.builder().build()
    project.apply plugin: 'com.google.osdetector'
    assertNotNull(project.osdetector)
    assertNotNull(project.osdetector.os)
    assertNotNull(project.osdetector.arch)
    assertEquals(project.osdetector.os + '-' + project.osdetector.arch,
        project.osdetector.classifier)
    System.err.println('classifier=' + project.osdetector.classifier)
    if (project.osdetector.os == 'linux') {
      assertNotNull(project.osdetector.release.id)
      assertNotNull(project.osdetector.release.version)
      System.err.println('release.id=' + project.osdetector.release.id)
      System.err.println('release.version=' + project.osdetector.release.version)
      System.err.println('release.isLike(debian)=' + project.osdetector.release.isLike('debian'))
      System.err.println('release.isLike(redhat)=' + project.osdetector.release.isLike('redhat'))
    } else if (project.osdetector.release) {
      fail("Should be null")
    }
  }

  @Test
  public void setClassifierWithLikes() {
    Project project = ProjectBuilder.builder().build()
    project.apply plugin: 'com.google.osdetector'
    project.osdetector.classifierWithLikes = ['debian', 'fedora']
    assertNotNull(project.osdetector.os)
    assertNotNull(project.osdetector.arch)
    System.err.println('classifier=' + project.osdetector.classifier)
    try {
      project.osdetector.classifierWithLikes = ['debian']
      fail("Should throw IllegalStateException")
    } catch (IllegalStateException expected) {
    }
  }
}
