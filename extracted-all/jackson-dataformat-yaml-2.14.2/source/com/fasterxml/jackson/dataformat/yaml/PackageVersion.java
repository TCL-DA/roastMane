package com.fasterxml.jackson.dataformat.yaml;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.Versioned;
import com.fasterxml.jackson.core.util.VersionUtil;

public final class PackageVersion implements Versioned {
   public static final Version VERSION = VersionUtil.parseVersion("2.14.2", "com.fasterxml.jackson.dataformat", "jackson-dataformat-yaml");

   public Version version() {
      return VERSION;
   }
}
