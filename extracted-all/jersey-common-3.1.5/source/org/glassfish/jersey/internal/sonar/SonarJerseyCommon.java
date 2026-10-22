package org.glassfish.jersey.internal.sonar;

public class SonarJerseyCommon {
   public String common() {
      return "common";
   }

   public String unitTest() {
      return this.common() + " unit test";
   }

   public String e2e() {
      return this.common() + " e2e";
   }

   public String integrationTestJvm() {
      return this.common() + " test jvm";
   }

   public String integrationServerJvm() {
      return this.common() + " server jvm";
   }
}
