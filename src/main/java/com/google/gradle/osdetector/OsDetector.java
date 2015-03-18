package com.google.gradle.plugins.osdetector;

import kr.motd.maven.os.Detector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

class OsDetector {
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
