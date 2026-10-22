package org.eclipse.jetty.util.component;

import java.io.FileWriter;
import java.io.Writer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class FileNoticeLifeCycleListener implements LifeCycle.Listener {
   private static final Logger LOG = LoggerFactory.getLogger(FileNoticeLifeCycleListener.class);
   private final String _filename;

   public FileNoticeLifeCycleListener(String filename) {
      this._filename = filename;
   }

   private void writeState(String action, LifeCycle lifecycle) {
      try (Writer out = new FileWriter(this._filename, true)) {
         out.append(action).append(" ").append(lifecycle.toString()).append("\n");
      } catch (Exception var8) {
         LOG.warn("Unable to write state", var8);
      }
   }

   @Override
   public void lifeCycleStarting(LifeCycle event) {
      this.writeState("STARTING", event);
   }

   @Override
   public void lifeCycleStarted(LifeCycle event) {
      this.writeState("STARTED", event);
   }

   @Override
   public void lifeCycleFailure(LifeCycle event, Throwable cause) {
      this.writeState("FAILED", event);
   }

   @Override
   public void lifeCycleStopping(LifeCycle event) {
      this.writeState("STOPPING", event);
   }

   @Override
   public void lifeCycleStopped(LifeCycle event) {
      this.writeState("STOPPED", event);
   }
}
