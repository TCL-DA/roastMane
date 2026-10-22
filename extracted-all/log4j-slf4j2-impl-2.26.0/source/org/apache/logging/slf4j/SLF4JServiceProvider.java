package org.apache.logging.slf4j;

import aQute.bnd.annotation.spi.ServiceProvider;
import org.slf4j.ILoggerFactory;
import org.slf4j.IMarkerFactory;
import org.slf4j.spi.MDCAdapter;

@ServiceProvider(
   value = org.slf4j.spi.SLF4JServiceProvider.class,
   resolution = "mandatory"
)
public class SLF4JServiceProvider implements org.slf4j.spi.SLF4JServiceProvider {
   public static final String REQUESTED_API_VERSION = "2.0.99";
   private ILoggerFactory loggerFactory;
   private Log4jMarkerFactory markerFactory;
   private MDCAdapter mdcAdapter;

   public ILoggerFactory getLoggerFactory() {
      return this.loggerFactory;
   }

   public IMarkerFactory getMarkerFactory() {
      return this.markerFactory;
   }

   public MDCAdapter getMDCAdapter() {
      return this.mdcAdapter;
   }

   public String getRequestedApiVersion() {
      return "2.0.99";
   }

   public void initialize() {
      this.markerFactory = new Log4jMarkerFactory();
      this.loggerFactory = new Log4jLoggerFactory(this.markerFactory);
      this.mdcAdapter = new Log4jMDCAdapter();
   }
}
