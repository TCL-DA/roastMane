package org.eclipse.jetty.server;

import java.io.IOException;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedObject("Slf4j RequestLog Writer")
public class Slf4jRequestLogWriter extends AbstractLifeCycle implements RequestLog.Writer {
   private Logger logger;
   private String loggerName = "org.eclipse.jetty.server.RequestLog";

   public void setLoggerName(String loggerName) {
      this.loggerName = loggerName;
   }

   @ManagedAttribute("logger name")
   public String getLoggerName() {
      return this.loggerName;
   }

   protected boolean isEnabled() {
      return this.logger != null;
   }

   @Override
   public void write(String requestEntry) throws IOException {
      this.logger.info(requestEntry);
   }

   protected void doStart() throws Exception {
      this.logger = LoggerFactory.getLogger(this.loggerName);
      super.doStart();
   }
}
