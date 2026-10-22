package org.eclipse.jetty.util.resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.eclipse.jetty.util.FileID;
import org.eclipse.jetty.util.IO;

public class MemoryResource extends Resource {
   private final URI _uri;
   private final Instant _created = Instant.now();
   private final byte[] _bytes;

   MemoryResource(URL url) {
      try {
         this._uri = Objects.requireNonNull(url).toURI();

         try (InputStream in = url.openStream()) {
            this._bytes = IO.readBytes(in);
         }
      } catch (URISyntaxException | IOException var7) {
         throw new RuntimeException(var7);
      }
   }

   @Override
   public Path getPath() {
      return null;
   }

   @Override
   public boolean isDirectory() {
      return false;
   }

   @Override
   public boolean isReadable() {
      return true;
   }

   @Override
   public boolean isContainedIn(Resource container) {
      return false;
   }

   @Override
   public URI getURI() {
      return this._uri;
   }

   @Override
   public String getName() {
      return this._uri.toASCIIString();
   }

   @Override
   public String getFileName() {
      return FileID.getFileName(this._uri);
   }

   @Override
   public Instant lastModified() {
      return this._created;
   }

   @Override
   public long length() {
      return (long)this._bytes.length;
   }

   @Override
   public Resource resolve(String subUriPath) {
      return null;
   }

   @Override
   public InputStream newInputStream() throws IOException {
      return new ByteArrayInputStream(this._bytes);
   }

   @Override
   public ReadableByteChannel newReadableByteChannel() throws IOException {
      return Channels.newChannel(this.newInputStream());
   }

   @Override
   public boolean exists() {
      return true;
   }

   @Override
   public List<Resource> list() {
      return List.of();
   }

   @Override
   public Collection<Resource> getAllResources() {
      return List.of();
   }

   @Override
   public String toString() {
      return this.getName();
   }
}
