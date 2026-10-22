package io.github.classgraph;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;
import nonapi.io.github.classgraph.fileslice.reader.ClassfileReader;
import nonapi.io.github.classgraph.utils.LogNode;
import nonapi.io.github.classgraph.utils.URLPathEncoder;

public abstract class Resource implements Closeable, Comparable<Resource> {
   private final ClasspathElement classpathElement;
   protected InputStream inputStream;
   protected ByteBuffer byteBuffer;
   protected long length;
   private String toString;
   LogNode scanLog;

   public Resource(ClasspathElement classpathElement, long length) {
      this.classpathElement = classpathElement;
      this.length = length;
   }

   private static URL uriToURL(URI uri) {
      try {
         return uri.toURL();
      } catch (MalformedURLException | IllegalArgumentException var2) {
         if (uri.getScheme().equals("jrt")) {
            throw new IllegalArgumentException(
               "Could not create URL from URI with \"jrt:\" scheme (\"jrt:\" is not supported by the URL class without a custom URL protocol handler): " + uri
            );
         } else {
            throw new IllegalArgumentException("Could not create URL from URI: " + uri + " -- " + var2);
         }
      }
   }

   public URI getURI() {
      URI locationURI = this.getClasspathElementURI();
      String locationURIStr = locationURI.toString();
      String resourcePath = this.getPathRelativeToClasspathElement();
      boolean isDir = locationURIStr.endsWith("/");

      try {
         return new URI(
            (!isDir && !locationURIStr.startsWith("jar:") && !locationURIStr.startsWith("jrt:") ? "jar:" : "")
               + locationURIStr
               + (isDir ? "" : (locationURIStr.startsWith("jrt:") ? "/" : "!/"))
               + URLPathEncoder.encodePath(resourcePath)
         );
      } catch (URISyntaxException var6) {
         throw new IllegalArgumentException("Could not form URL for classpath element: " + locationURIStr + " ; path: " + resourcePath + " : " + var6);
      }
   }

   public URL getURL() {
      return uriToURL(this.getURI());
   }

   public URI getClasspathElementURI() {
      return this.classpathElement.getURI();
   }

   public URL getClasspathElementURL() {
      return uriToURL(this.getClasspathElementURI());
   }

   public File getClasspathElementFile() {
      return this.classpathElement.getFile();
   }

   public ModuleRef getModuleRef() {
      return this.classpathElement instanceof ClasspathElementModule ? ((ClasspathElementModule)this.classpathElement).moduleRef : null;
   }

   public String getContentAsString() throws IOException {
      String content = new String(this.load(), StandardCharsets.UTF_8);
      this.close();
      return content;
   }

   public abstract String getPath();

   public String getPathRelativeToClasspathElement() {
      return this.getPath();
   }

   public abstract InputStream open() throws IOException;

   public abstract ByteBuffer read() throws IOException;

   public CloseableByteBuffer readCloseable() throws IOException {
      return new CloseableByteBuffer(this.read(), new Runnable() {
         @Override
         public void run() {
            Resource.this.close();
         }
      });
   }

   public abstract byte[] load() throws IOException;

   abstract ClassfileReader openClassfile() throws IOException;

   public long getLength() {
      return this.length;
   }

   public abstract long getLastModified();

   public abstract Set<PosixFilePermission> getPosixFilePermissions();

   @Override
   public String toString() {
      return this.toString != null ? this.toString : (this.toString = this.getURI().toString());
   }

   @Override
   public int hashCode() {
      return this.toString().hashCode();
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else {
         return !(obj instanceof Resource) ? false : this.toString().equals(obj.toString());
      }
   }

   public int compareTo(Resource o) {
      return this.toString().compareTo(o.toString());
   }

   @Override
   public void close() {
      if (this.inputStream != null) {
         try {
            this.inputStream.close();
         } catch (IOException var2) {
         }

         this.inputStream = null;
      }
   }
}
