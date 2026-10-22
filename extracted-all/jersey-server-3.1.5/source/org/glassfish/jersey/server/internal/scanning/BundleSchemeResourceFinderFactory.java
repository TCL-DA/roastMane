package org.glassfish.jersey.server.internal.scanning;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Set;
import org.glassfish.jersey.server.internal.AbstractResourceFinderAdapter;

final class BundleSchemeResourceFinderFactory implements UriSchemeResourceFinderFactory {
   private static final Set<String> SCHEMES = Collections.singleton("bundle");

   @Override
   public Set<String> getSchemes() {
      return SCHEMES;
   }

   public BundleSchemeResourceFinderFactory.BundleSchemeScanner create(URI uri, boolean recursive) {
      return new BundleSchemeResourceFinderFactory.BundleSchemeScanner(uri);
   }

   private class BundleSchemeScanner extends AbstractResourceFinderAdapter {
      private final URI uri;
      private boolean accessed = false;
      private boolean iterated = false;

      private BundleSchemeScanner(URI uri) {
         this.uri = uri;
      }

      @Override
      public boolean hasNext() {
         return !this.accessed && !this.iterated;
      }

      public String next() {
         if (this.hasNext()) {
            this.iterated = true;
            return this.uri.getPath();
         } else {
            throw new NoSuchElementException();
         }
      }

      @Override
      public InputStream open() {
         if (!this.accessed) {
            try {
               this.accessed = true;
               return this.uri.toURL().openStream();
            } catch (IOException var2) {
               throw new ResourceFinderException(var2);
            }
         } else {
            return null;
         }
      }

      @Override
      public void reset() {
         throw new UnsupportedOperationException();
      }
   }
}
