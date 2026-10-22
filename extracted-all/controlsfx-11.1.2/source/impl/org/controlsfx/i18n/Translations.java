package impl.org.controlsfx.i18n;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public class Translations {
   private static final List<Translation> translations = new ArrayList<>();

   private static void loadFrom(Path rootPath) {
      try (DirectoryStream<Path> stream = Files.newDirectoryStream(rootPath)) {
         for (Path path : stream) {
            String filename = path.getFileName().toString();
            if (filename.startsWith("controlsfx") || filename.endsWith(".properties")) {
               if ("controlsfx.properties".equals(filename)) {
                  translations.add(new Translation("en", path));
               } else {
                  if (!filename.contains("_")) {
                     throw new IllegalStateException("Unknown translation file '" + path + "'.");
                  }

                  String locale = filename.substring(11, filename.indexOf(".properties"));
                  translations.add(new Translation(locale, path));
               }
            }
         }
      } catch (DirectoryIteratorException | IOException var8) {
      }
   }

   private Translations() {
   }

   public static Optional<Translation> getTranslation(String localeString) {
      for (Translation t : translations) {
         if (localeString.equals(t.getLocaleString())) {
            return Optional.of(t);
         }
      }

      return Optional.empty();
   }

   public static List<Translation> getAllTranslations() {
      return translations;
   }

   public static List<Locale> getAllTranslationLocales() {
      return translations.stream().map(t -> t.getLocale()).collect(Collectors.toList());
   }

   static {
      File file = new File(Translations.class.getProtectionDomain().getCodeSource().getLocation().getPath());
      if (file.getName().endsWith(".jar")) {
         Path jarFile = file.toPath();

         try (FileSystem fs = FileSystems.newFileSystem(jarFile, (ClassLoader)null)) {
            fs.getRootDirectories().forEach(path -> loadFrom(path));
         } catch (IOException var7) {
            var7.printStackTrace();
         }
      }

      if (translations.isEmpty()) {
         Path srcDir = new File("src/main/resources").toPath();
         loadFrom(srcDir);
      }

      if (translations.isEmpty()) {
         Path binDir = new File("bin").toPath();
         loadFrom(binDir);
      }

      if (translations.isEmpty() && file.getAbsolutePath().endsWith("controlsfx" + File.separator + "bin")) {
         loadFrom(file.toPath());
      }

      Collections.sort(translations);
   }
}
