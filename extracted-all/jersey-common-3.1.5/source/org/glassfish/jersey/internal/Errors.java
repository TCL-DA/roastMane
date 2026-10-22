package org.glassfish.jersey.internal;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Logger;
import org.glassfish.jersey.Severity;
import org.glassfish.jersey.internal.util.Producer;

public class Errors {
   private static final Logger LOGGER = Logger.getLogger(Errors.class.getName());
   private static final ThreadLocal<Errors> errors = new ThreadLocal<>();
   private final ArrayList<Errors.ErrorMessage> issues = new ArrayList<>(0);
   private Deque<Integer> mark = new ArrayDeque<>(4);
   private int stack = 0;

   public static void error(String message, Severity severity) {
      error(null, message, severity);
   }

   public static void error(Object source, String message, Severity severity) {
      getInstance().issues.add(new Errors.ErrorMessage(source, message, severity));
   }

   public static void fatal(Object source, String message) {
      error(source, message, Severity.FATAL);
   }

   public static void warning(Object source, String message) {
      error(source, message, Severity.WARNING);
   }

   public static void hint(Object source, String message) {
      getInstance().issues.add(new Errors.ErrorMessage(source, message, Severity.HINT));
   }

   private static void processErrors(boolean throwException) {
      List<Errors.ErrorMessage> errors = new ArrayList<>(Errors.errors.get().issues);
      boolean isFatal = logErrors(errors);
      if (throwException && isFatal) {
         throw new Errors.ErrorMessagesException(errors);
      }
   }

   public static boolean logErrors(boolean afterMark) {
      return logErrors(getInstance()._getErrorMessages(afterMark));
   }

   private static boolean logErrors(Collection<Errors.ErrorMessage> errors) {
      boolean isFatal = false;
      if (!errors.isEmpty()) {
         StringBuilder fatals = new StringBuilder("\n");
         StringBuilder warnings = new StringBuilder();
         StringBuilder hints = new StringBuilder();

         for (Errors.ErrorMessage error : errors) {
            switch (error.getSeverity()) {
               case FATAL:
                  isFatal = true;
                  fatals.append(LocalizationMessages.ERROR_MSG(error.getMessage())).append('\n');
                  break;
               case WARNING:
                  warnings.append(LocalizationMessages.WARNING_MSG(error.getMessage())).append('\n');
                  break;
               case HINT:
                  hints.append(LocalizationMessages.HINT_MSG(error.getMessage())).append('\n');
            }
         }

         if (isFatal) {
            LOGGER.severe(LocalizationMessages.ERRORS_AND_WARNINGS_DETECTED(fatals.append((CharSequence)warnings).append((CharSequence)hints).toString()));
         } else {
            if (warnings.length() > 0) {
               LOGGER.warning(LocalizationMessages.WARNINGS_DETECTED(warnings.toString()));
            }

            if (hints.length() > 0) {
               LOGGER.config(LocalizationMessages.HINTS_DETECTED(hints.toString()));
            }
         }
      }

      return isFatal;
   }

   public static boolean fatalIssuesFound() {
      for (Errors.ErrorMessage message : getInstance().issues) {
         if (message.getSeverity() == Severity.FATAL) {
            return true;
         }
      }

      return false;
   }

   public static <T> T process(Producer<T> producer) {
      return process(producer, false);
   }

   public static <T> T process(Callable<T> task) throws Exception {
      return process(task, true);
   }

   public static <T> T processWithException(Producer<T> producer) {
      return process(producer, true);
   }

   public static void process(final Runnable task) {
      process(new Producer<Void>() {
         public Void call() {
            task.run();
            return null;
         }
      }, false);
   }

   public static void processWithException(final Runnable task) {
      process(new Producer<Void>() {
         public Void call() {
            task.run();
            return null;
         }
      }, true);
   }

   private static <T> T process(Producer<T> task, boolean throwException) {
      try {
         return process(task, throwException);
      } catch (RuntimeException var3) {
         throw var3;
      } catch (Exception var4) {
         throw new RuntimeException(var4);
      }
   }

   private static <T> T process(Callable<T> task, boolean throwException) throws Exception {
      Errors instance = errors.get();
      if (instance == null) {
         instance = new Errors();
         errors.set(instance);
      }

      instance.preProcess();
      Exception caught = null;

      try {
         return task.call();
      } catch (Exception var8) {
         caught = var8;
      } finally {
         instance.postProcess(throwException && caught == null);
      }

      throw caught;
   }

   private static Errors getInstance() {
      Errors instance = errors.get();
      if (instance == null) {
         throw new IllegalStateException(LocalizationMessages.NO_ERROR_PROCESSING_IN_SCOPE());
      } else if (instance.stack == 0) {
         errors.remove();
         throw new IllegalStateException(LocalizationMessages.NO_ERROR_PROCESSING_IN_SCOPE());
      } else {
         return instance;
      }
   }

   public static List<Errors.ErrorMessage> getErrorMessages() {
      return getErrorMessages(false);
   }

   public static List<Errors.ErrorMessage> getErrorMessages(boolean afterMark) {
      return getInstance()._getErrorMessages(afterMark);
   }

   public static void mark() {
      getInstance()._mark();
   }

   public static void unmark() {
      getInstance()._unmark();
   }

   public static void reset() {
      getInstance()._reset();
   }

   private Errors() {
   }

   private void _mark() {
      this.mark.addLast(this.issues.size());
   }

   private void _unmark() {
      this.mark.pollLast();
   }

   private void _reset() {
      Integer _pos = this.mark.pollLast();
      int markedPos = _pos == null ? -1 : _pos;
      if (markedPos >= 0 && markedPos < this.issues.size()) {
         this.issues.subList(markedPos, this.issues.size()).clear();
      }
   }

   private void preProcess() {
      this.stack++;
   }

   private void postProcess(boolean throwException) {
      this.stack--;
      if (this.stack == 0) {
         try {
            if (!this.issues.isEmpty()) {
               processErrors(throwException);
            }
         } finally {
            errors.remove();
         }
      }
   }

   private List<Errors.ErrorMessage> _getErrorMessages(boolean afterMark) {
      if (afterMark) {
         Integer _pos = this.mark.peekLast();
         int markedPos = _pos == null ? -1 : _pos;
         if (markedPos >= 0 && markedPos < this.issues.size()) {
            return Collections.unmodifiableList(new ArrayList<>(this.issues.subList(markedPos, this.issues.size())));
         }
      }

      return Collections.unmodifiableList(new ArrayList<>(this.issues));
   }

   public static class ErrorMessage {
      private final Object source;
      private final String message;
      private final Severity severity;

      private ErrorMessage(Object source, String message, Severity severity) {
         this.source = source;
         this.message = message;
         this.severity = severity;
      }

      public Severity getSeverity() {
         return this.severity;
      }

      public String getMessage() {
         return this.message;
      }

      public Object getSource() {
         return this.source;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            Errors.ErrorMessage that = (Errors.ErrorMessage)o;
            if (this.message != null ? this.message.equals(that.message) : that.message == null) {
               if (this.severity != that.severity) {
                  return false;
               } else {
                  return this.source != null ? this.source.equals(that.source) : that.source == null;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         int result = this.source != null ? this.source.hashCode() : 0;
         result = 31 * result + (this.message != null ? this.message.hashCode() : 0);
         return 31 * result + (this.severity != null ? this.severity.hashCode() : 0);
      }
   }

   public static class ErrorMessagesException extends RuntimeException {
      private final List<Errors.ErrorMessage> messages;

      private ErrorMessagesException(List<Errors.ErrorMessage> messages) {
         this.messages = messages;
      }

      public List<Errors.ErrorMessage> getMessages() {
         return this.messages;
      }
   }
}
