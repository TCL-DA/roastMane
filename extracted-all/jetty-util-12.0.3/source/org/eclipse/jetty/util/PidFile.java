package org.eclipse.jetty.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.jetty.util.annotation.Name;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PidFile extends Thread {
   private static final Logger LOG = LoggerFactory.getLogger(PidFile.class);
   private static final Set<Path> activeFiles = ConcurrentHashMap.newKeySet();
   private final Path pidFile;

   public static void create(@Name("file") String filename) throws IOException {
      Path pidFile = Paths.get(filename).toAbsolutePath();
      if (activeFiles.add(pidFile)) {
         Runtime.getRuntime().addShutdownHook(new PidFile(pidFile));
         if (Files.exists(pidFile)) {
            LOG.info("Overwriting existing PID file: {}", pidFile);
         }

         long pid = ProcessHandle.current().pid();
         Files.writeString(
            pidFile, Long.toString(pid), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING
         );
         if (LOG.isDebugEnabled()) {
            LOG.debug("PID file: {}", pidFile);
         }
      }
   }

   private PidFile(Path pidFile) {
      this.pidFile = pidFile;
   }

   @Override
   public void run() {
      try {
         Files.deleteIfExists(this.pidFile);
      } catch (Throwable var2) {
         LOG.info("Unable to remove PID file: {}", this.pidFile, var2);
      }
   }
}
