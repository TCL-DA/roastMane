package org.eclipse.jetty.http.pathmap;

import java.util.Objects;

public interface PathSpec extends Comparable<PathSpec> {
   static PathSpec from(String pathSpecString) {
      Objects.requireNonNull(pathSpecString, "null PathSpec not supported");
      if (pathSpecString.length() == 0) {
         return new ServletPathSpec("");
      } else {
         return (PathSpec)(pathSpecString.charAt(0) == '^' ? new RegexPathSpec(pathSpecString) : new ServletPathSpec(pathSpecString));
      }
   }

   int getSpecLength();

   PathSpecGroup getGroup();

   int getPathDepth();

   @Deprecated
   String getPathInfo(String var1);

   @Deprecated
   String getPathMatch(String var1);

   String getDeclaration();

   String getPrefix();

   String getSuffix();

   default boolean matches(String path) {
      return this.matched(path) != null;
   }

   MatchedPath matched(String var1);
}
