package org.glassfish.jersey.jackson.internal.jackson.jaxrs.json;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.Versioned;
import com.fasterxml.jackson.core.util.VersionUtil;

public final class PackageVersion implements Versioned {
   public static final Version VERSION = VersionUtil.parseVersion("2.15.3", "com.fasterxml.jackson.jaxrs", "jackson-jaxrs-json-provider");

   public Version version() {
      return VERSION;
   }
}
