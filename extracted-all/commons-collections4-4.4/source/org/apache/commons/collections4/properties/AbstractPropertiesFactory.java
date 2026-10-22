package org.apache.commons.collections4.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public abstract class AbstractPropertiesFactory<T extends Properties> {
   protected AbstractPropertiesFactory() {
   }

   protected abstract T createProperties();

   public T load(ClassLoader classLoader, String name) throws IOException {
      Properties var5;
      try (InputStream inputStream = classLoader.getResourceAsStream(name)) {
         var5 = this.load(inputStream);
      }

      return (T)var5;
   }

   public T load(File file) throws FileNotFoundException, IOException {
      Properties var4;
      try (FileInputStream inputStream = new FileInputStream(file)) {
         var4 = this.load(inputStream);
      }

      return (T)var4;
   }

   public T load(InputStream inputStream) throws IOException {
      if (inputStream == null) {
         return null;
      } else {
         T properties = this.createProperties();
         properties.load(inputStream);
         return properties;
      }
   }

   public T load(Path path) throws IOException {
      Properties var4;
      try (InputStream inputStream = Files.newInputStream(path)) {
         var4 = this.load(inputStream);
      }

      return (T)var4;
   }

   public T load(Reader reader) throws IOException {
      T properties = this.createProperties();
      properties.load(reader);
      return properties;
   }

   public T load(String name) throws IOException {
      Properties var4;
      try (FileInputStream inputStream = new FileInputStream(name)) {
         var4 = this.load(inputStream);
      }

      return (T)var4;
   }

   public T load(URI uri) throws IOException {
      return this.load(Paths.get(uri));
   }

   public T load(URL url) throws IOException {
      Properties var4;
      try (InputStream inputStream = url.openStream()) {
         var4 = this.load(inputStream);
      }

      return (T)var4;
   }
}
