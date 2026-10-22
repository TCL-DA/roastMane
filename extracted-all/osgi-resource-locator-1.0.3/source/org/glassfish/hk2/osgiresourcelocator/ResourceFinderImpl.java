package org.glassfish.hk2.osgiresourcelocator;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleReference;

public class ResourceFinderImpl extends ResourceFinder {
   private BundleContext bundleContext;

   public ResourceFinderImpl() {
      ClassLoader cl = this.getClass().getClassLoader();
      if (cl instanceof BundleReference) {
         this.bundleContext = BundleReference.class.cast(cl).getBundle().getBundleContext();
      }

      if (this.bundleContext == null) {
         throw new RuntimeException("There is no bundle context available yet. Instatiate this class in STARTING or ACTIVE state only");
      }
   }

   @Override
   URL findEntry1(String path) {
      for (Bundle bundle : this.bundleContext.getBundles()) {
         URL url = bundle.getEntry(path);
         if (url != null) {
            return url;
         }
      }

      return null;
   }

   @Override
   List<URL> findEntries1(String path) {
      List<URL> urls = new ArrayList<>();

      for (Bundle bundle : this.bundleContext.getBundles()) {
         URL url = bundle.getEntry(path);
         if (url != null) {
            urls.add(url);
         }
      }

      return urls;
   }
}
