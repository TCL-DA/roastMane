package org.eclipse.jetty.http.pathmap;

public interface MatchedPath {
   MatchedPath EMPTY = new MatchedPath() {
      @Override
      public String getPathMatch() {
         return null;
      }

      @Override
      public String getPathInfo() {
         return null;
      }

      @Override
      public String toString() {
         return MatchedPath.class.getSimpleName() + ".EMPTY";
      }
   };

   static MatchedPath from(final String pathMatch, final String pathInfo) {
      return new MatchedPath() {
         @Override
         public String getPathMatch() {
            return pathMatch;
         }

         @Override
         public String getPathInfo() {
            return pathInfo;
         }

         @Override
         public String toString() {
            return MatchedPath.class.getSimpleName() + "[pathMatch=" + pathMatch + ", pathInfo=" + pathInfo + "]";
         }
      };
   }

   String getPathMatch();

   String getPathInfo();
}
