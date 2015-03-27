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
package com.google.gradle.plugins.osdetector;

import kr.motd.maven.os.Detector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class OsDetector {
  private static final Logger logger = LoggerFactory.getLogger(OsDetector.class.getName());

  private static final Impl impl = new Impl();

  static String os() {
    return (String) impl.detectedProperties.get(Detector.DETECTED_NAME);
  }

  static String arch() {
    return (String) impl.detectedProperties.get(Detector.DETECTED_ARCH);
  }

  static String classifier() {
    return (String) impl.detectedProperties.get(Detector.DETECTED_CLASSIFIER);
  }

  private static class Impl extends Detector {
    final Properties detectedProperties = System.getProperties();

    @Override
    protected void log(String message) {
      logger.info(message);
    }

    @Override
    protected void logProperty(String name, String value) {
      logger.info(name + "=" + value);
    }

    Impl() {
      detect(detectedProperties);
    }
  }
}
