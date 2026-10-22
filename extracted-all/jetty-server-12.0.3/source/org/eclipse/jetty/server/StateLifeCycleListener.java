package org.eclipse.jetty.server;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.util.component.LifeCycle.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StateLifeCycleListener implements Listener {
   private static final Logger LOG = LoggerFactory.getLogger(StateLifeCycleListener.class);
   private final Path stateFile;

   public StateLifeCycleListener(String filename) throws IOException {
      this.stateFile = Paths.get(filename).toAbsolutePath();
      if (LOG.isDebugEnabled()) {
         LOG.debug("State File: {}", this.stateFile);
      }

      Files.deleteIfExists(this.stateFile);
      Files.writeString(this.stateFile, "INIT " + this + "\n", StandardCharsets.UTF_8, StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW);
   }

   private void appendStateChange(String action, Object obj) {
      try (Writer out = Files.newBufferedWriter(this.stateFile, StandardCharsets.UTF_8, StandardOpenOption.WRITE, StandardOpenOption.APPEND)) {
         String entry = String.format("%s %s\n", action, obj);
         if (LOG.isDebugEnabled()) {
            LOG.debug("appendEntry to {}: {}", this.stateFile, entry);
         }

         out.append(entry);
      } catch (IOException var8) {
         LOG.warn("Unable to append to state file: " + this.stateFile, var8);
      }
   }

   public void lifeCycleStarting(LifeCycle event) {
      this.appendStateChange("STARTING", event);
   }

   public void lifeCycleStarted(LifeCycle event) {
      this.appendStateChange("STARTED", event);
   }

   public void lifeCycleFailure(LifeCycle event, Throwable cause) {
      this.appendStateChange("FAILED", event);
   }

   public void lifeCycleStopping(LifeCycle event) {
      this.appendStateChange("STOPPING", event);
   }

   public void lifeCycleStopped(LifeCycle event) {
      this.appendStateChange("STOPPED", event);
   }

   @Override
   public String toString() {
      return String.format("%s@%h", this.getClass().getSimpleName(), this);
   }
}
