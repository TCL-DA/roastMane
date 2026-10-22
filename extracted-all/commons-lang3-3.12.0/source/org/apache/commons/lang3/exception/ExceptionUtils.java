package org.apache.commons.lang3.exception;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;

public class ExceptionUtils {
   private static final int NOT_FOUND = -1;
   private static final String[] CAUSE_METHOD_NAMES = new String[]{
      "getCause",
      "getNextException",
      "getTargetException",
      "getException",
      "getSourceException",
      "getRootCause",
      "getCausedByException",
      "getNested",
      "getLinkedException",
      "getNestedException",
      "getLinkedCause",
      "getThrowable"
   };
   static final String WRAPPED_MARKER = " [wrapped] ";

   @Deprecated
   public static Throwable getCause(Throwable throwable) {
      return getCause(throwable, null);
   }

   @Deprecated
   public static Throwable getCause(Throwable throwable, String[] methodNames) {
      if (throwable == null) {
         return null;
      } else {
         if (methodNames == null) {
            Throwable cause = throwable.getCause();
            if (cause != null) {
               return cause;
            }

            methodNames = CAUSE_METHOD_NAMES;
         }

         for (String methodName : methodNames) {
            if (methodName != null) {
               Throwable legacyCause = getCauseUsingMethodName(throwable, methodName);
               if (legacyCause != null) {
                  return legacyCause;
               }
            }
         }

         return null;
      }
   }

   private static Throwable getCauseUsingMethodName(Throwable throwable, String methodName) {
      Method method = null;

      try {
         method = throwable.getClass().getMethod(methodName);
      } catch (SecurityException | NoSuchMethodException var4) {
      }

      if (method != null && Throwable.class.isAssignableFrom(method.getReturnType())) {
         try {
            return (Throwable)method.invoke(throwable);
         } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var5) {
         }
      }

      return null;
   }

   @Deprecated
   public static String[] getDefaultCauseMethodNames() {
      return ArrayUtils.clone((String[])CAUSE_METHOD_NAMES);
   }

   public static String getMessage(Throwable th) {
      if (th == null) {
         return "";
      } else {
         String clsName = ClassUtils.getShortClassName(th, null);
         String msg = th.getMessage();
         return clsName + ": " + StringUtils.defaultString(msg);
      }
   }

   public static Throwable getRootCause(Throwable throwable) {
      List<Throwable> list = getThrowableList(throwable);
      return list.isEmpty() ? null : list.get(list.size() - 1);
   }

   public static String getRootCauseMessage(Throwable th) {
      Throwable root = getRootCause(th);
      root = root == null ? th : root;
      return getMessage(root);
   }

   public static String[] getRootCauseStackTrace(Throwable throwable) {
      if (throwable == null) {
         return ArrayUtils.EMPTY_STRING_ARRAY;
      } else {
         Throwable[] throwables = getThrowables(throwable);
         int count = throwables.length;
         List<String> frames = new ArrayList<>();
         List<String> nextTrace = getStackFrameList(throwables[count - 1]);
         int i = count;

         while (--i >= 0) {
            List<String> trace = nextTrace;
            if (i != 0) {
               nextTrace = getStackFrameList(throwables[i - 1]);
               removeCommonFrames(trace, nextTrace);
            }

            if (i == count - 1) {
               frames.add(throwables[i].toString());
            } else {
               frames.add(" [wrapped] " + throwables[i].toString());
            }

            frames.addAll(trace);
         }

         return frames.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
      }
   }

   static List<String> getStackFrameList(Throwable t) {
      String stackTrace = getStackTrace(t);
      String linebreak = System.lineSeparator();
      StringTokenizer frames = new StringTokenizer(stackTrace, linebreak);
      List<String> list = new ArrayList<>();
      boolean traceStarted = false;

      while (frames.hasMoreTokens()) {
         String token = frames.nextToken();
         int at = token.indexOf("at");
         if (at != -1 && token.substring(0, at).trim().isEmpty()) {
            traceStarted = true;
            list.add(token);
         } else if (traceStarted) {
            break;
         }
      }

      return list;
   }

   static String[] getStackFrames(String stackTrace) {
      String linebreak = System.lineSeparator();
      StringTokenizer frames = new StringTokenizer(stackTrace, linebreak);
      List<String> list = new ArrayList<>();

      while (frames.hasMoreTokens()) {
         list.add(frames.nextToken());
      }

      return list.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
   }

   public static String[] getStackFrames(Throwable throwable) {
      return throwable == null ? ArrayUtils.EMPTY_STRING_ARRAY : getStackFrames(getStackTrace(throwable));
   }

   public static String getStackTrace(Throwable throwable) {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw, true);
      throwable.printStackTrace(pw);
      return sw.getBuffer().toString();
   }

   public static int getThrowableCount(Throwable throwable) {
      return getThrowableList(throwable).size();
   }

   public static List<Throwable> getThrowableList(Throwable throwable) {
      List<Throwable> list = new ArrayList<>();

      while (throwable != null && !list.contains(throwable)) {
         list.add(throwable);
         throwable = throwable.getCause();
      }

      return list;
   }

   public static Throwable[] getThrowables(Throwable throwable) {
      List<Throwable> list = getThrowableList(throwable);
      return list.toArray(ArrayUtils.EMPTY_THROWABLE_ARRAY);
   }

   public static boolean hasCause(Throwable chain, Class<? extends Throwable> type) {
      if (chain instanceof UndeclaredThrowableException) {
         chain = chain.getCause();
      }

      return type.isInstance(chain);
   }

   private static int indexOf(Throwable throwable, Class<? extends Throwable> type, int fromIndex, boolean subclass) {
      if (throwable != null && type != null) {
         if (fromIndex < 0) {
            fromIndex = 0;
         }

         Throwable[] throwables = getThrowables(throwable);
         if (fromIndex >= throwables.length) {
            return -1;
         } else {
            if (subclass) {
               for (int i = fromIndex; i < throwables.length; i++) {
                  if (type.isAssignableFrom(throwables[i].getClass())) {
                     return i;
                  }
               }
            } else {
               for (int ix = fromIndex; ix < throwables.length; ix++) {
                  if (type.equals(throwables[ix].getClass())) {
                     return ix;
                  }
               }
            }

            return -1;
         }
      } else {
         return -1;
      }
   }

   public static int indexOfThrowable(Throwable throwable, Class<? extends Throwable> clazz) {
      return indexOf(throwable, clazz, 0, false);
   }

   public static int indexOfThrowable(Throwable throwable, Class<? extends Throwable> clazz, int fromIndex) {
      return indexOf(throwable, clazz, fromIndex, false);
   }

   public static int indexOfType(Throwable throwable, Class<? extends Throwable> type) {
      return indexOf(throwable, type, 0, true);
   }

   public static int indexOfType(Throwable throwable, Class<? extends Throwable> type, int fromIndex) {
      return indexOf(throwable, type, fromIndex, true);
   }

   public static void printRootCauseStackTrace(Throwable throwable) {
      printRootCauseStackTrace(throwable, System.err);
   }

   public static void printRootCauseStackTrace(Throwable throwable, PrintStream printStream) {
      if (throwable != null) {
         Objects.requireNonNull(printStream, "printStream");
         String[] trace = getRootCauseStackTrace(throwable);

         for (String element : trace) {
            printStream.println(element);
         }

         printStream.flush();
      }
   }

   public static void printRootCauseStackTrace(Throwable throwable, PrintWriter printWriter) {
      if (throwable != null) {
         Objects.requireNonNull(printWriter, "printWriter");
         String[] trace = getRootCauseStackTrace(throwable);

         for (String element : trace) {
            printWriter.println(element);
         }

         printWriter.flush();
      }
   }

   public static void removeCommonFrames(List<String> causeFrames, List<String> wrapperFrames) {
      if (causeFrames != null && wrapperFrames != null) {
         int causeFrameIndex = causeFrames.size() - 1;

         for (int wrapperFrameIndex = wrapperFrames.size() - 1; causeFrameIndex >= 0 && wrapperFrameIndex >= 0; wrapperFrameIndex--) {
            String causeFrame = causeFrames.get(causeFrameIndex);
            String wrapperFrame = wrapperFrames.get(wrapperFrameIndex);
            if (causeFrame.equals(wrapperFrame)) {
               causeFrames.remove(causeFrameIndex);
            }

            causeFrameIndex--;
         }
      } else {
         throw new IllegalArgumentException("The List must not be null");
      }
   }

   public static <R> R rethrow(Throwable throwable) {
      return typeErasure(throwable);
   }

   private static <T extends Throwable> T throwableOf(Throwable throwable, Class<T> type, int fromIndex, boolean subclass) {
      if (throwable != null && type != null) {
         if (fromIndex < 0) {
            fromIndex = 0;
         }

         Throwable[] throwables = getThrowables(throwable);
         if (fromIndex >= throwables.length) {
            return null;
         } else {
            if (subclass) {
               for (int i = fromIndex; i < throwables.length; i++) {
                  if (type.isAssignableFrom(throwables[i].getClass())) {
                     return type.cast(throwables[i]);
                  }
               }
            } else {
               for (int ix = fromIndex; ix < throwables.length; ix++) {
                  if (type.equals(throwables[ix].getClass())) {
                     return type.cast(throwables[ix]);
                  }
               }
            }

            return null;
         }
      } else {
         return null;
      }
   }

   public static <T extends Throwable> T throwableOfThrowable(Throwable throwable, Class<T> clazz) {
      return throwableOf(throwable, clazz, 0, false);
   }

   public static <T extends Throwable> T throwableOfThrowable(Throwable throwable, Class<T> clazz, int fromIndex) {
      return throwableOf(throwable, clazz, fromIndex, false);
   }

   public static <T extends Throwable> T throwableOfType(Throwable throwable, Class<T> type) {
      return throwableOf(throwable, type, 0, true);
   }

   public static <T extends Throwable> T throwableOfType(Throwable throwable, Class<T> type, int fromIndex) {
      return throwableOf(throwable, type, fromIndex, true);
   }

   private static <R, T extends Throwable> R typeErasure(Throwable throwable) throws T {
      throw throwable;
   }

   public static <R> R wrapAndThrow(Throwable throwable) {
      if (throwable instanceof RuntimeException) {
         throw (RuntimeException)throwable;
      } else if (throwable instanceof Error) {
         throw (Error)throwable;
      } else {
         throw new UndeclaredThrowableException(throwable);
      }
   }
}
