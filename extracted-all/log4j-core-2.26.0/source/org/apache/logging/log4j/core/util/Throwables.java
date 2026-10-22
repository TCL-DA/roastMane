package org.apache.logging.log4j.core.util;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.apache.logging.log4j.core.internal.annotation.SuppressFBWarnings;

public final class Throwables {
   private Throwables() {
   }

   public static Throwable getRootCause(final Throwable throwable) {
      Objects.requireNonNull(throwable, "throwable");
      Set<Throwable> visitedThrowables = new HashSet<>();
      Throwable prevCause = throwable;
      visitedThrowables.add(throwable);

      Throwable nextCause;
      while ((nextCause = prevCause.getCause()) != null && visitedThrowables.add(nextCause)) {
         prevCause = nextCause;
      }

      return prevCause;
   }

   @SuppressFBWarnings(
      value = {"INFORMATION_EXPOSURE_THROUGH_AN_ERROR_MESSAGE"},
      justification = "Log4j prints stacktraces only to logs, which should be private."
   )
   public static List<String> toStringList(final Throwable throwable) {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);

      try {
         throwable.printStackTrace(pw);
      } catch (RuntimeException var10) {
      }

      pw.flush();
      List<String> lines = new ArrayList<>();
      LineNumberReader reader = new LineNumberReader(new StringReader(sw.toString()));

      try {
         for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            lines.add(line);
         }
      } catch (IOException var11) {
         if (var11 instanceof InterruptedIOException) {
            Thread.currentThread().interrupt();
         }

         lines.add(var11.toString());
      } finally {
         Closer.closeSilently(reader);
      }

      return lines;
   }

   public static void rethrow(final Throwable t) {
      rethrow0(t);
   }

   private static <T extends Throwable> void rethrow0(final Throwable t) throws T {
      throw t;
   }
}
