package org.glassfish.jersey.server.internal.scanning;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Stack;
import org.glassfish.jersey.server.internal.AbstractResourceFinderAdapter;

final class FileSchemeResourceFinderFactory implements UriSchemeResourceFinderFactory {
   private static final Set<String> SCHEMES = Collections.singleton("file");

   @Override
   public Set<String> getSchemes() {
      return SCHEMES;
   }

   public FileSchemeResourceFinderFactory.FileSchemeScanner create(URI uri, boolean recursive) {
      return new FileSchemeResourceFinderFactory.FileSchemeScanner(uri, recursive);
   }

   private class FileSchemeScanner extends AbstractResourceFinderAdapter {
      private final CompositeResourceFinder compositeResourceFinder = new CompositeResourceFinder();
      private final boolean recursive;

      private FileSchemeScanner(URI uri, boolean recursive) {
         this.recursive = recursive;
         this.processFile(new File(uri.getPath()));
      }

      @Override
      public boolean hasNext() {
         return this.compositeResourceFinder.hasNext();
      }

      public String next() {
         return this.compositeResourceFinder.next();
      }

      @Override
      public InputStream open() {
         return this.compositeResourceFinder.open();
      }

      @Override
      public void close() {
         this.compositeResourceFinder.close();
      }

      @Override
      public void reset() {
         throw new UnsupportedOperationException();
      }

      private void processFile(final File f) {
         this.compositeResourceFinder.push(new AbstractResourceFinderAdapter() {
            Stack<File> files = new Stack<File>() {
               {
                  if (f.isDirectory()) {
                     File[] subDirFiles = f.listFiles();
                     if (subDirFiles != null) {
                        for (File file : subDirFiles) {
                           this.push(file);
                        }
                     }
                  } else {
                     this.push(f);
                  }
               }
            };
            private File current;
            private File next;

            @Override
            public boolean hasNext() {
               while (this.next == null && !this.files.empty()) {
                  this.next = this.files.pop();
                  if (this.next.isDirectory()) {
                     if (FileSchemeScanner.this.recursive) {
                        FileSchemeScanner.this.processFile(this.next);
                     }

                     this.next = null;
                  }
               }

               return this.next != null;
            }

            public String next() {
               if (this.next == null && !this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  this.current = this.next;
                  this.next = null;
                  return this.current.getName();
               }
            }

            @Override
            public InputStream open() {
               try {
                  return Files.newInputStream(this.current.toPath());
               } catch (IOException var2) {
                  throw new ResourceFinderException(var2);
               }
            }

            @Override
            public void reset() {
               throw new UnsupportedOperationException();
            }
         });
      }
   }
}
