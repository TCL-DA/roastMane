package com.fasterxml.jackson.module.jakarta.xmlbind;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.Versioned;
import com.fasterxml.jackson.core.util.VersionUtil;

public final class PackageVersion implements Versioned {
   public static final Version VERSION = VersionUtil.parseVersion("2.14.2", "com.fasterxml.jackson.module", "jackson-module-jakarta-xmlbind-annotations");

   public Version version() {
      return VERSION;
   }
}
