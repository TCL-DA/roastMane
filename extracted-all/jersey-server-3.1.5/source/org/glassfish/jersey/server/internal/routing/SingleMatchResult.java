package org.glassfish.jersey.server.internal.routing;

import java.util.regex.MatchResult;

final class SingleMatchResult implements MatchResult {
   private final String path;

   public SingleMatchResult(String path) {
      this.path = stripMatrixParams(path);
   }

   private static String stripMatrixParams(String path) {
      int e = path.indexOf(59);
      if (e == -1) {
         return path;
      } else {
         int s = 0;
         StringBuilder sb = new StringBuilder();

         do {
            sb.append(path, s, e);
            s = path.indexOf(47, e + 1);
            if (s == -1) {
               break;
            }

            e = path.indexOf(59, s);
         } while (e != -1);

         if (s != -1) {
            sb.append(path, s, path.length());
         }

         return sb.toString();
      }
   }

   @Override
   public int start() {
      return 0;
   }

   @Override
   public int start(int group) {
      if (group == 0) {
         return this.start();
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   @Override
   public int end() {
      return this.path.length();
   }

   @Override
   public int end(int group) {
      if (group == 0) {
         return this.end();
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   @Override
   public String group() {
      return this.path;
   }

   @Override
   public String group(int group) {
      if (group == 0) {
         return this.group();
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   @Override
   public int groupCount() {
      return 0;
   }
}
