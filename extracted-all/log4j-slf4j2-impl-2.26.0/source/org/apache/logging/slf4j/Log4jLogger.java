package org.apache.logging.slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.apache.logging.log4j.message.SimpleMessage;
import org.apache.logging.log4j.spi.ExtendedLogger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.spi.LocationAwareLogger;
import org.slf4j.spi.LoggingEventBuilder;

public class Log4jLogger implements LocationAwareLogger, Serializable {
   public static final String FQCN = Log4jLogger.class.getName();
   private static final long serialVersionUID = 7869000638091304316L;
   private transient ExtendedLogger logger;
   private final String name;
   private transient Log4jMarkerFactory markerFactory;

   public Log4jLogger(final Log4jMarkerFactory markerFactory, final ExtendedLogger logger, final String name) {
      this.markerFactory = markerFactory;
      this.logger = logger;
      this.name = name;
   }

   public void trace(final String format) {
      this.logger.logIfEnabled(FQCN, Level.TRACE, null, format);
   }

   public void trace(final String format, final Object o) {
      this.logger.logIfEnabled(FQCN, Level.TRACE, null, format, o);
   }

   public void trace(final String format, final Object arg1, final Object arg2) {
      this.logger.logIfEnabled(FQCN, Level.TRACE, null, format, arg1, arg2);
   }

   public void trace(final String format, final Object... args) {
      this.logger.logIfEnabled(FQCN, Level.TRACE, null, format, args);
   }

   public void trace(final String format, final Throwable t) {
      this.logger.logIfEnabled(FQCN, Level.TRACE, null, format, t);
   }

   public boolean isTraceEnabled() {
      return this.logger.isEnabled(Level.TRACE, null, null);
   }

   public boolean isTraceEnabled(final Marker marker) {
      return this.logger.isEnabled(Level.TRACE, this.markerFactory.getLog4jMarker(marker), null);
   }

   public void trace(final Marker marker, final String s) {
      this.logger.logIfEnabled(FQCN, Level.TRACE, this.markerFactory.getLog4jMarker(marker), s);
   }

   public void trace(final Marker marker, final String s, final Object o) {
      this.logger.logIfEnabled(FQCN, Level.TRACE, this.markerFactory.getLog4jMarker(marker), s, o);
   }

   public void trace(final Marker marker, final String s, final Object o, final Object o1) {
      this.logger.logIfEnabled(FQCN, Level.TRACE, this.markerFactory.getLog4jMarker(marker), s, o, o1);
   }

   public void trace(final Marker marker, final String s, final Object... objects) {
      this.logger.logIfEnabled(FQCN, Level.TRACE, this.markerFactory.getLog4jMarker(marker), s, objects);
   }

   public void trace(final Marker marker, final String s, final Throwable throwable) {
      this.logger.logIfEnabled(FQCN, Level.TRACE, this.markerFactory.getLog4jMarker(marker), s, throwable);
   }

   public void debug(final String format) {
      this.logger.logIfEnabled(FQCN, Level.DEBUG, null, format);
   }

   public void debug(final String format, final Object o) {
      this.logger.logIfEnabled(FQCN, Level.DEBUG, null, format, o);
   }

   public void debug(final String format, final Object arg1, final Object arg2) {
      this.logger.logIfEnabled(FQCN, Level.DEBUG, null, format, arg1, arg2);
   }

   public void debug(final String format, final Object... args) {
      this.logger.logIfEnabled(FQCN, Level.DEBUG, null, format, args);
   }

   public void debug(final String format, final Throwable t) {
      this.logger.logIfEnabled(FQCN, Level.DEBUG, null, format, t);
   }

   public boolean isDebugEnabled() {
      return this.logger.isEnabled(Level.DEBUG, null, null);
   }

   public boolean isDebugEnabled(final Marker marker) {
      return this.logger.isEnabled(Level.DEBUG, this.markerFactory.getLog4jMarker(marker), null);
   }

   public void debug(final Marker marker, final String s) {
      this.logger.logIfEnabled(FQCN, Level.DEBUG, this.markerFactory.getLog4jMarker(marker), s);
   }

   public void debug(final Marker marker, final String s, final Object o) {
      this.logger.logIfEnabled(FQCN, Level.DEBUG, this.markerFactory.getLog4jMarker(marker), s, o);
   }

   public void debug(final Marker marker, final String s, final Object o, final Object o1) {
      this.logger.logIfEnabled(FQCN, Level.DEBUG, this.markerFactory.getLog4jMarker(marker), s, o, o1);
   }

   public void debug(final Marker marker, final String s, final Object... objects) {
      this.logger.logIfEnabled(FQCN, Level.DEBUG, this.markerFactory.getLog4jMarker(marker), s, objects);
   }

   public void debug(final Marker marker, final String s, final Throwable throwable) {
      this.logger.logIfEnabled(FQCN, Level.DEBUG, this.markerFactory.getLog4jMarker(marker), s, throwable);
   }

   public void info(final String format) {
      this.logger.logIfEnabled(FQCN, Level.INFO, null, format);
   }

   public void info(final String format, final Object o) {
      this.logger.logIfEnabled(FQCN, Level.INFO, null, format, o);
   }

   public void info(final String format, final Object arg1, final Object arg2) {
      this.logger.logIfEnabled(FQCN, Level.INFO, null, format, arg1, arg2);
   }

   public void info(final String format, final Object... args) {
      this.logger.logIfEnabled(FQCN, Level.INFO, null, format, args);
   }

   public void info(final String format, final Throwable t) {
      this.logger.logIfEnabled(FQCN, Level.INFO, null, format, t);
   }

   public boolean isInfoEnabled() {
      return this.logger.isEnabled(Level.INFO, null, null);
   }

   public boolean isInfoEnabled(final Marker marker) {
      return this.logger.isEnabled(Level.INFO, this.markerFactory.getLog4jMarker(marker), null);
   }

   public void info(final Marker marker, final String s) {
      this.logger.logIfEnabled(FQCN, Level.INFO, this.markerFactory.getLog4jMarker(marker), s);
   }

   public void info(final Marker marker, final String s, final Object o) {
      this.logger.logIfEnabled(FQCN, Level.INFO, this.markerFactory.getLog4jMarker(marker), s, o);
   }

   public void info(final Marker marker, final String s, final Object o, final Object o1) {
      this.logger.logIfEnabled(FQCN, Level.INFO, this.markerFactory.getLog4jMarker(marker), s, o, o1);
   }

   public void info(final Marker marker, final String s, final Object... objects) {
      this.logger.logIfEnabled(FQCN, Level.INFO, this.markerFactory.getLog4jMarker(marker), s, objects);
   }

   public void info(final Marker marker, final String s, final Throwable throwable) {
      this.logger.logIfEnabled(FQCN, Level.INFO, this.markerFactory.getLog4jMarker(marker), s, throwable);
   }

   public void warn(final String format) {
      this.logger.logIfEnabled(FQCN, Level.WARN, null, format);
   }

   public void warn(final String format, final Object o) {
      this.logger.logIfEnabled(FQCN, Level.WARN, null, format, o);
   }

   public void warn(final String format, final Object arg1, final Object arg2) {
      this.logger.logIfEnabled(FQCN, Level.WARN, null, format, arg1, arg2);
   }

   public void warn(final String format, final Object... args) {
      this.logger.logIfEnabled(FQCN, Level.WARN, null, format, args);
   }

   public void warn(final String format, final Throwable t) {
      this.logger.logIfEnabled(FQCN, Level.WARN, null, format, t);
   }

   public boolean isWarnEnabled() {
      return this.logger.isEnabled(Level.WARN, null, null);
   }

   public boolean isWarnEnabled(final Marker marker) {
      return this.logger.isEnabled(Level.WARN, this.markerFactory.getLog4jMarker(marker), null);
   }

   public void warn(final Marker marker, final String s) {
      this.logger.logIfEnabled(FQCN, Level.WARN, this.markerFactory.getLog4jMarker(marker), s);
   }

   public void warn(final Marker marker, final String s, final Object o) {
      this.logger.logIfEnabled(FQCN, Level.WARN, this.markerFactory.getLog4jMarker(marker), s, o);
   }

   public void warn(final Marker marker, final String s, final Object o, final Object o1) {
      this.logger.logIfEnabled(FQCN, Level.WARN, this.markerFactory.getLog4jMarker(marker), s, o, o1);
   }

   public void warn(final Marker marker, final String s, final Object... objects) {
      this.logger.logIfEnabled(FQCN, Level.WARN, this.markerFactory.getLog4jMarker(marker), s, objects);
   }

   public void warn(final Marker marker, final String s, final Throwable throwable) {
      this.logger.logIfEnabled(FQCN, Level.WARN, this.markerFactory.getLog4jMarker(marker), s, throwable);
   }

   public void error(final String format) {
      this.logger.logIfEnabled(FQCN, Level.ERROR, null, format);
   }

   public void error(final String format, final Object o) {
      this.logger.logIfEnabled(FQCN, Level.ERROR, null, format, o);
   }

   public void error(final String format, final Object arg1, final Object arg2) {
      this.logger.logIfEnabled(FQCN, Level.ERROR, null, format, arg1, arg2);
   }

   public void error(final String format, final Object... args) {
      this.logger.logIfEnabled(FQCN, Level.ERROR, null, format, args);
   }

   public void error(final String format, final Throwable t) {
      this.logger.logIfEnabled(FQCN, Level.ERROR, null, format, t);
   }

   public boolean isErrorEnabled() {
      return this.logger.isEnabled(Level.ERROR, null, null);
   }

   public boolean isErrorEnabled(final Marker marker) {
      return this.logger.isEnabled(Level.ERROR, this.markerFactory.getLog4jMarker(marker), null);
   }

   public void error(final Marker marker, final String s) {
      this.logger.logIfEnabled(FQCN, Level.ERROR, this.markerFactory.getLog4jMarker(marker), s);
   }

   public void error(final Marker marker, final String s, final Object o) {
      this.logger.logIfEnabled(FQCN, Level.ERROR, this.markerFactory.getLog4jMarker(marker), s, o);
   }

   public void error(final Marker marker, final String s, final Object o, final Object o1) {
      this.logger.logIfEnabled(FQCN, Level.ERROR, this.markerFactory.getLog4jMarker(marker), s, o, o1);
   }

   public void error(final Marker marker, final String s, final Object... objects) {
      this.logger.logIfEnabled(FQCN, Level.ERROR, this.markerFactory.getLog4jMarker(marker), s, objects);
   }

   public void error(final Marker marker, final String s, final Throwable throwable) {
      this.logger.logIfEnabled(FQCN, Level.ERROR, this.markerFactory.getLog4jMarker(marker), s, throwable);
   }

   public void log(final Marker marker, final String fqcn, final int level, final String message, final Object[] params, final Throwable throwable) {
      Level log4jLevel = getLevel(level);
      org.apache.logging.log4j.Marker log4jMarker = this.markerFactory.getLog4jMarker(marker);
      if (this.logger.isEnabled(log4jLevel, log4jMarker, message, params)) {
         Message msg;
         Throwable actualThrowable;
         if (params == null) {
            msg = new SimpleMessage(message);
            actualThrowable = throwable;
         } else {
            msg = new ParameterizedMessage(message, params, throwable);
            actualThrowable = throwable != null ? throwable : msg.getThrowable();
         }

         this.logger.logMessage(fqcn, log4jLevel, log4jMarker, msg, actualThrowable);
      }
   }

   public String getName() {
      return this.name;
   }

   private void readObject(final ObjectInputStream aInputStream) throws ClassNotFoundException, IOException {
      aInputStream.defaultReadObject();
      this.logger = LogManager.getContext().getLogger(this.name);
      this.markerFactory = ((Log4jLoggerFactory)LoggerFactory.getILoggerFactory()).getMarkerFactory();
   }

   private void writeObject(final ObjectOutputStream aOutputStream) throws IOException {
      aOutputStream.defaultWriteObject();
   }

   private static Level getLevel(final int i) {
      switch (i) {
         case 0:
            return Level.TRACE;
         case 10:
            return Level.DEBUG;
         case 20:
            return Level.INFO;
         case 30:
            return Level.WARN;
         case 40:
            return Level.ERROR;
         default:
            return Level.ERROR;
      }
   }

   public LoggingEventBuilder makeLoggingEventBuilder(final org.slf4j.event.Level level) {
      Level log4jLevel = getLevel(level.toInt());
      return new Log4jEventBuilder(this.markerFactory, this.logger, log4jLevel);
   }

   public boolean isEnabledForLevel(final org.slf4j.event.Level level) {
      return this.logger.isEnabled(getLevel(level.toInt()));
   }
}
