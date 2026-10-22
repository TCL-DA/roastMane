package org.eclipse.jetty.server;

import java.io.File;
import java.util.List;
import java.util.concurrent.Executor;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.util.Attributes;
import org.eclipse.jetty.util.Decorator;
import org.eclipse.jetty.util.resource.Resource;

public interface Context extends Attributes, Decorator, Executor {
   String getContextPath();

   ClassLoader getClassLoader();

   Resource getBaseResource();

   Request.Handler getErrorHandler();

   List<String> getVirtualHosts();

   MimeTypes getMimeTypes();

   @Override
   void execute(Runnable var1);

   void run(Runnable var1);

   void run(Runnable var1, Request var2);

   default String getPathInContext(String canonicallyEncodedPath) {
      return getPathInContext(this.getContextPath(), canonicallyEncodedPath);
   }

   File getTempDirectory();

   static String getPathInContext(String encodedContextPath, String encodedPath) {
      if (encodedContextPath.length() != 0 && !"/".equals(encodedContextPath)) {
         if (encodedContextPath.length() <= encodedPath.length() && encodedPath.startsWith(encodedContextPath)) {
            if (encodedPath.length() == encodedContextPath.length()) {
               return "";
            } else {
               return encodedPath.charAt(encodedContextPath.length()) != '/' ? null : encodedPath.substring(encodedContextPath.length());
            }
         } else {
            return null;
         }
      } else {
         return encodedPath;
      }
   }
}
