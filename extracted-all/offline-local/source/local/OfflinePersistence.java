package local;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.nio.channels.FileChannel;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;
import org.cropster.roastingintelligence.model.Roast;
import org.cropster.roastingintelligence.remote.Version;
import org.cropster.roastingintelligence.service.impl.FilePersister;
import org.cropster.roastingintelligence.service.impl.JsonWriter;

public final class OfflinePersistence {
   private static final Map<Roast, Path> saved = Collections.synchronizedMap(new WeakHashMap<>());

   private static Object field(Object var0, String var1) throws ReflectiveOperationException {
      Field var2 = FilePersister.class.getDeclaredField(var1);
      var2.setAccessible(true);
      return var2.get(var0);
   }

   public static File save(FilePersister var0, Roast var1, boolean var2) throws IOException {
      synchronized (var0) {
         File var10000;
         try {
            Map var4 = (Map)field(var0, "_filesMap");
            if (var2 && var4.containsKey(var1)) {
               throw new IllegalArgumentException("Existing roast");
            }

            if (!var2 && !var4.containsKey(var1)) {
               throw new IllegalArgumentException("New roast");
            }

            Path var5 = Files.createTempFile(((File)field(var0, "_outboxDir")).toPath(), "write-check-", ".tmp");
            Files.delete(var5);
            OfflineConnection var6 = new OfflineConnection(new Version("5.13"), URI.create("https://offline.invalid"));
            if (var1.getLot().getId() == null) {
               var6.startRoast(var1);
            }

            File var7 = var2
               ? new File(
                  (File)field(var0, "_outboxDir"),
                  String.format(Locale.ROOT, "%1$tY%1$tm%1$td_%1$tH%1$tM%1$tS_%1$tL.json", var1.getStartDate(), var1.getLot().getId())
               )
               : (File)var4.get(var1);
            if (var2 && Files.exists(var7.toPath())) {
               throw new IOException("A batch with the same timestamp already exists; refusing to overwrite it");
            }

            Path var8 = Files.createTempFile(var7.toPath().getParent(), "local-roast-", ".tmp");

            try {
               try (BufferedWriter var9 = Files.newBufferedWriter(var8)) {
                  ((JsonWriter)field(var0, "_jsonWriter")).write(var9, var1);
               }

               try (FileChannel var30 = FileChannel.open(var8, StandardOpenOption.WRITE)) {
                  var30.force(true);
               }

               try {
                  Files.move(var8, var7.toPath(), StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
               } catch (AtomicMoveNotSupportedException var24) {
                  Files.move(var8, var7.toPath(), StandardCopyOption.REPLACE_EXISTING);
               }
            } finally {
               Files.deleteIfExists(var8);
            }

            var4.put(var1, var7);
            saved.put(var1, var7.toPath());
            if (var1.getEndDate() != null) {
               var6.finishRoast(var1);
            } else {
               var6.update(var1);
            }

            var10000 = var7;
         } catch (ReflectiveOperationException var28) {
            throw new IOException("Unable to access local persister", var28);
         }

         return var10000;
      }
   }

   public static void verifySaved(Roast var0) {
      Path var1 = saved.get(var0);

      try {
         if (var1 == null || !Files.isRegularFile(var1) || Files.size(var1) == 0L) {
            throw new IllegalStateException("Batch was not saved locally. Preserve the displayed data before closing.");
         }
      } catch (IOException var3) {
         throw new IllegalStateException("Cannot verify saved batch", var3);
      }
   }
}
