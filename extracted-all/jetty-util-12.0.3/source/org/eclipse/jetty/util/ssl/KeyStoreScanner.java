package org.eclipse.jetty.util.ssl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.Scanner;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedOperation;
import org.eclipse.jetty.util.component.ContainerLifeCycle;
import org.eclipse.jetty.util.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeyStoreScanner extends ContainerLifeCycle implements Scanner.DiscreteListener {
   private static final Logger LOG = LoggerFactory.getLogger(KeyStoreScanner.class);
   private final SslContextFactory sslContextFactory;
   private final Path keystoreFile;
   private final Scanner _scanner;

   public KeyStoreScanner(SslContextFactory sslContextFactory) {
      this.sslContextFactory = sslContextFactory;
      Resource keystoreResource = sslContextFactory.getKeyStoreResource();
      if (!keystoreResource.exists()) {
         throw new IllegalArgumentException("keystore file does not exist");
      } else if (keystoreResource.isDirectory()) {
         throw new IllegalArgumentException("expected keystore file not directory");
      } else {
         Path monitoredFile = keystoreResource.getPath();
         this.keystoreFile = monitoredFile;
         if (LOG.isDebugEnabled()) {
            LOG.debug("Monitored Keystore File: {}", monitoredFile);
         }

         Path parentFile = this.keystoreFile.getParent();
         if (Files.exists(parentFile) && Files.isDirectory(parentFile)) {
            this._scanner = new Scanner(null, false);
            this._scanner.addDirectory(parentFile);
            this._scanner.setScanInterval(1);
            this._scanner.setReportDirs(false);
            this._scanner.setReportExistingFilesOnStartup(false);
            this._scanner.setScanDepth(1);
            this._scanner.addListener(this);
            this.addBean(this._scanner);
         } else {
            throw new IllegalArgumentException("error obtaining keystore dir");
         }
      }
   }

   private Path getRealKeyStorePath() {
      try {
         return this.keystoreFile.toRealPath();
      } catch (IOException var2) {
         return this.keystoreFile;
      }
   }

   @Override
   public void fileAdded(String filename) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("fileAdded {} - keystoreFile.toReal {}", filename, this.getRealKeyStorePath());
      }

      if (this.keystoreFile.toString().equals(filename)) {
         this.reload();
      }
   }

   @Override
   public void fileChanged(String filename) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("fileChanged {} - keystoreFile.toReal {}", filename, this.getRealKeyStorePath());
      }

      if (this.keystoreFile.toString().equals(filename)) {
         this.reload();
      }
   }

   @Override
   public void fileRemoved(String filename) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("fileRemoved {} - keystoreFile.toReal {}", filename, this.getRealKeyStorePath());
      }

      if (this.keystoreFile.toString().equals(filename)) {
         this.reload();
      }
   }

   @ManagedOperation(
      value = "Scan for changes in the SSL Keystore",
      impact = "ACTION"
   )
   public boolean scan(long waitMillis) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("scanning");
      }

      CompletableFuture<Boolean> cf = new CompletableFuture<>();

      try {
         this._scanner
            .scan(Callback.from(() -> this._scanner.scan(Callback.from(() -> cf.complete(true), cf::completeExceptionally)), cf::completeExceptionally));
         return cf.get(waitMillis, TimeUnit.MILLISECONDS);
      } catch (Exception var5) {
         throw new RuntimeException(var5);
      }
   }

   @ManagedOperation(
      value = "Reload the SSL Keystore",
      impact = "ACTION"
   )
   public void reload() {
      if (LOG.isDebugEnabled()) {
         LOG.debug("reloading keystore file {}", this.keystoreFile);
      }

      try {
         this.sslContextFactory.reload(scf -> {
         });
      } catch (Throwable var2) {
         LOG.warn("Keystore Reload Failed", var2);
      }
   }

   @ManagedAttribute("scanning interval to detect changes which need reloaded")
   public int getScanInterval() {
      return this._scanner.getScanInterval();
   }

   public void setScanInterval(int scanInterval) {
      this._scanner.setScanInterval(scanInterval);
   }
}
