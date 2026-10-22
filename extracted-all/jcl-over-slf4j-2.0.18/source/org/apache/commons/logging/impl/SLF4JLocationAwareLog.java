package org.apache.commons.logging.impl;

import java.io.ObjectStreamException;
import java.io.Serializable;
import org.apache.commons.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LocationAwareLogger;

public class SLF4JLocationAwareLog implements Log, Serializable {
   private static final long serialVersionUID = -2379157579039314822L;
   protected String name;
   private final transient LocationAwareLogger logger;
   private static final String FQCN = SLF4JLocationAwareLog.class.getName();

   public SLF4JLocationAwareLog(LocationAwareLogger logger) {
      this.logger = logger;
      this.name = logger.getName();
   }

   @Override
   public boolean isTraceEnabled() {
      return this.logger.isTraceEnabled();
   }

   @Override
   public boolean isDebugEnabled() {
      return this.logger.isDebugEnabled();
   }

   @Override
   public boolean isInfoEnabled() {
      return this.logger.isInfoEnabled();
   }

   @Override
   public boolean isWarnEnabled() {
      return this.logger.isWarnEnabled();
   }

   @Override
   public boolean isErrorEnabled() {
      return this.logger.isErrorEnabled();
   }

   @Override
   public boolean isFatalEnabled() {
      return this.logger.isErrorEnabled();
   }

   @Override
   public void trace(Object message) {
      if (this.isTraceEnabled()) {
         this.logger.log(null, FQCN, 0, String.valueOf(message), null, null);
      }
   }

   @Override
   public void trace(Object message, Throwable t) {
      if (this.isTraceEnabled()) {
         this.logger.log(null, FQCN, 0, String.valueOf(message), null, t);
      }
   }

   @Override
   public void debug(Object message) {
      if (this.isDebugEnabled()) {
         this.logger.log(null, FQCN, 10, String.valueOf(message), null, null);
      }
   }

   @Override
   public void debug(Object message, Throwable t) {
      if (this.isDebugEnabled()) {
         this.logger.log(null, FQCN, 10, String.valueOf(message), null, t);
      }
   }

   @Override
   public void info(Object message) {
      if (this.isInfoEnabled()) {
         this.logger.log(null, FQCN, 20, String.valueOf(message), null, null);
      }
   }

   @Override
   public void info(Object message, Throwable t) {
      if (this.isInfoEnabled()) {
         this.logger.log(null, FQCN, 20, String.valueOf(message), null, t);
      }
   }

   @Override
   public void warn(Object message) {
      if (this.isWarnEnabled()) {
         this.logger.log(null, FQCN, 30, String.valueOf(message), null, null);
      }
   }

   @Override
   public void warn(Object message, Throwable t) {
      if (this.isWarnEnabled()) {
         this.logger.log(null, FQCN, 30, String.valueOf(message), null, t);
      }
   }

   @Override
   public void error(Object message) {
      if (this.isErrorEnabled()) {
         this.logger.log(null, FQCN, 40, String.valueOf(message), null, null);
      }
   }

   @Override
   public void error(Object message, Throwable t) {
      if (this.isErrorEnabled()) {
         this.logger.log(null, FQCN, 40, String.valueOf(message), null, t);
      }
   }

   @Override
   public void fatal(Object message) {
      if (this.isErrorEnabled()) {
         this.logger.log(null, FQCN, 40, String.valueOf(message), null, null);
      }
   }

   @Override
   public void fatal(Object message, Throwable t) {
      if (this.isErrorEnabled()) {
         this.logger.log(null, FQCN, 40, String.valueOf(message), null, t);
      }
   }

   protected Object readResolve() throws ObjectStreamException {
      Logger logger = LoggerFactory.getLogger(this.name);
      return new SLF4JLocationAwareLog((LocationAwareLogger)logger);
   }
}
