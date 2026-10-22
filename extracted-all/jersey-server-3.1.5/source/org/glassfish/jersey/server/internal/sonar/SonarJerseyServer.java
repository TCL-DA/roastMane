package org.glassfish.jersey.server.internal.sonar;

public class SonarJerseyServer {
   private String server() {
      return "server";
   }

   public String unitTest() {
      return this.server() + " unit test";
   }

   public String e2e() {
      return this.server() + " e2e";
   }

   public String integrationTestJvm() {
      return this.server() + " test jvm";
   }

   public String integrationServerJvm() {
      return this.server() + " server jvm";
   }
}
