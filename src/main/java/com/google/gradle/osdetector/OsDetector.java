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
package com.google.gradle.osdetector;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import javax.inject.Inject;
import kr.motd.maven.os.Detector;
import kr.motd.maven.os.FileOperationProvider;
import kr.motd.maven.os.SystemPropertyOperationProvider;
import org.gradle.api.Project;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.file.RegularFile;
import org.gradle.api.provider.Provider;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.util.GradleVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public abstract class OsDetector {
  private static final Logger logger = LoggerFactory.getLogger(OsDetector.class.getName());

  @Inject
  public abstract ProviderFactory getProviderFactory();
  @Inject
  public abstract ProjectLayout getProjectLayout();
  private final Project project;
  private final List<String> classifierWithLikes = new ArrayList<String>();
  private Impl impl;

  public OsDetector(Project project) {
    this.project = project;
  }

  public String getOs() {
    return (String) getImpl().detectedProperties.get(Detector.DETECTED_NAME);
  }

  public String getArch() {
    return (String) getImpl().detectedProperties.get(Detector.DETECTED_ARCH);
  }

  public String getClassifier() {
    return (String) getImpl().detectedProperties.get(Detector.DETECTED_CLASSIFIER);
  }

  public Release getRelease() {
    Impl impl = getImpl();
    Object releaseId = impl.detectedProperties.get(Detector.DETECTED_RELEASE);
    if (releaseId == null) {
      return null;
    }
    return new Release(impl);
  }

  public synchronized void setClassifierWithLikes(List<String> classifierWithLikes) {
    if (impl != null) {
      throw new IllegalStateException("classifierWithLikes must be set before osdetector is read.");
    }
    this.classifierWithLikes.clear();
    this.classifierWithLikes.addAll(classifierWithLikes);
  }

  private synchronized Impl getImpl() {
    if (impl == null) {
      if (GradleVersion.current().compareTo(GradleVersion.version("6.5")) >= 0) {
        impl = new Impl(classifierWithLikes, new ConfigurationTimeSafeSystemPropertyOperations(),
            new ConfigurationTimeSafeFileOperations());
      } else {
        impl = new Impl(classifierWithLikes);
      }
    }
    return impl;
  }

  /**
   * Accessor to information about the current OS release.
   */
  public static class Release {
    private final Impl impl;

    private Release(Impl impl) {
      this.impl = impl;
    }

    /**
     * Returns the release ID.
     */
    public String getId() {
      return (String) impl.detectedProperties.get(Detector.DETECTED_RELEASE);
    }

    /**
     * Returns the version ID.
     */
    public String getVersion() {
      return (String) impl.detectedProperties.get(Detector.DETECTED_RELEASE_VERSION);
    }

    /**
     * Returns {@code true} if this release is a variant of the given base release (for example,
     * ubuntu is "like" debian).
     */
    public boolean isLike(String baseRelease) {
      return impl.detectedProperties.containsKey(
          Detector.DETECTED_RELEASE_LIKE_PREFIX + baseRelease);
    }
  }

  private static class Impl extends Detector {
    final Properties detectedProperties = new Properties();

    Impl(List<String> classifierWithLikes, SystemPropertyOperationProvider sysPropOps,
        FileOperationProvider fsOps) {
      super(sysPropOps, fsOps);
      detect(detectedProperties, classifierWithLikes);
    }

    Impl(List<String> classifierWithLikes) {
      detect(detectedProperties, classifierWithLikes);
    }

    @Override
    protected void log(String message) {
      logger.info(message);
    }

    @Override
    protected void logProperty(String name, String value) {
      logger.info(name + "=" + value);
    }
  }

  private static <T> Provider<T> forUseAtConfigurationTime(Provider<T> provider) {
    // Deprecated and a noop starting in 7.4
    if (GradleVersion.current().compareTo(GradleVersion.version("7.4")) < 0) {
      return provider.forUseAtConfigurationTime();
    } else {
      return provider;
    }
  }

  /** Provides system property operations compatible with Gradle configuration cache. */
  private final class ConfigurationTimeSafeSystemPropertyOperations
      implements SystemPropertyOperationProvider {
    @Override
    public String getSystemProperty(String name) {
      return forUseAtConfigurationTime(getProviderFactory().systemProperty(name)).getOrNull();
    }

    @Override
    public String getSystemProperty(String name, String def) {
      return forUseAtConfigurationTime(getProviderFactory().systemProperty(name)).getOrElse(def);
    }

    @Override
    public String setSystemProperty(String name, String value) {
      // no-op
      return null;
    }
  }

  /** Provides filesystem operations compatible with Gradle configuration cache. */
  private final class ConfigurationTimeSafeFileOperations implements FileOperationProvider {
    @Override
    public InputStream readFile(String fileName) throws IOException {
      RegularFile file = getProjectLayout().getProjectDirectory().file(fileName);
      byte[] bytes = forUseAtConfigurationTime(getProviderFactory().fileContents(file).getAsBytes())
          .getOrNull();
      if (bytes == null) {
        throw new FileNotFoundException(fileName + " not exist");
      }
      return new ByteArrayInputStream(bytes);
    }
  }
}
