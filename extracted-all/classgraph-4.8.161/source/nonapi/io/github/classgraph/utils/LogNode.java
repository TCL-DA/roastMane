package nonapi.io.github.classgraph.utils;

import io.github.classgraph.ClassGraph;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import nonapi.io.github.classgraph.classpath.SystemJarFinder;

public final class LogNode {
   private static final Logger log = Logger.getLogger(ClassGraph.class.getName());
   private final long timeStampNano = System.nanoTime();
   private final long timeStampMillis = System.currentTimeMillis();
   private final String msg;
   private String stackTrace;
   private long elapsedTimeNanos;
   private LogNode parent;
   private final Map<String, LogNode> children = new ConcurrentSkipListMap<>();
   private final String sortKeyPrefix;
   private static AtomicInteger sortKeyUniqueSuffix = new AtomicInteger(0);
   private static final SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZ", Locale.US);
   private static final DecimalFormat nanoFormatter = new DecimalFormat("0.000000");
   private static boolean logInRealtime;

   public static void logInRealtime(boolean logInRealtime) {
      LogNode.logInRealtime = logInRealtime;
   }

   private LogNode(String sortKey, String msg, long elapsedTimeNanos, Throwable exception) {
      this.sortKeyPrefix = sortKey;
      this.msg = msg;
      this.elapsedTimeNanos = elapsedTimeNanos;
      if (exception != null) {
         StringWriter writer = new StringWriter();
         exception.printStackTrace(new PrintWriter(writer));
         this.stackTrace = writer.toString();
      } else {
         this.stackTrace = null;
      }

      if (logInRealtime) {
         log.info(this.toString());
      }
   }

   public LogNode() {
      this("", "", -1L, null);
      this.log("ClassGraph version " + VersionFinder.getVersion());
      this.logJavaInfo();
   }

   private void logJavaInfo() {
      this.log(
         "Operating system: "
            + VersionFinder.getProperty("os.name")
            + " "
            + VersionFinder.getProperty("os.version")
            + " "
            + VersionFinder.getProperty("os.arch")
      );
      this.log(
         "Java version: "
            + VersionFinder.getProperty("java.version")
            + " / "
            + VersionFinder.getProperty("java.runtime.version")
            + " ("
            + VersionFinder.getProperty("java.vendor")
            + ")"
      );
      this.log("Java home: " + VersionFinder.getProperty("java.home"));
      String jreRtJarPath = SystemJarFinder.getJreRtJarPath();
      if (jreRtJarPath != null) {
         this.log("JRE rt.jar:").log(jreRtJarPath);
      }
   }

   private void appendLine(String timeStampStr, int indentLevel, String line, StringBuilder buf) {
      buf.append(timeStampStr);
      buf.append('\t');
      buf.append(ClassGraph.class.getSimpleName());
      buf.append('\t');
      int numDashes = 2 * (indentLevel - 1);

      for (int i = 0; i < numDashes; i++) {
         buf.append('-');
      }

      if (numDashes > 0) {
         buf.append(' ');
      }

      buf.append(line);
      buf.append('\n');
   }

   private void toString(int indentLevel, StringBuilder buf) {
      Calendar cal = Calendar.getInstance();
      cal.setTimeInMillis(this.timeStampMillis);
      String timeStampStr;
      synchronized (dateTimeFormatter) {
         timeStampStr = dateTimeFormatter.format(cal.getTime());
      }

      if (this.msg != null && !this.msg.isEmpty()) {
         this.appendLine(
            timeStampStr,
            indentLevel,
            this.elapsedTimeNanos > 0L ? this.msg + " (took " + nanoFormatter.format((double)this.elapsedTimeNanos * 1.0E-9) + " sec)" : this.msg,
            buf
         );
      }

      if (this.stackTrace != null && !this.stackTrace.isEmpty()) {
         String[] parts = this.stackTrace.split("\n");

         for (String part : parts) {
            this.appendLine(timeStampStr, indentLevel, part, buf);
         }
      }

      for (Entry<String, LogNode> ent : this.children.entrySet()) {
         LogNode child = ent.getValue();
         child.toString(indentLevel + 1, buf);
      }
   }

   @Override
   public String toString() {
      synchronized (dateTimeFormatter) {
         StringBuilder buf = new StringBuilder();
         this.toString(0, buf);
         return buf.toString();
      }
   }

   public void addElapsedTime() {
      this.elapsedTimeNanos = System.nanoTime() - this.timeStampNano;
   }

   private LogNode addChild(String sortKey, String msg, long elapsedTimeNanos, Throwable exception) {
      String newSortKey = this.sortKeyPrefix + "\t" + (sortKey == null ? "" : sortKey) + "\t" + String.format("%09d", sortKeyUniqueSuffix.getAndIncrement());
      LogNode newChild = new LogNode(newSortKey, msg, elapsedTimeNanos, exception);
      newChild.parent = this;
      this.children.put(newSortKey, newChild);
      return newChild;
   }

   private LogNode addChild(String sortKey, String msg, long elapsedTimeNanos) {
      return this.addChild(sortKey, msg, elapsedTimeNanos, null);
   }

   private LogNode addChild(Throwable exception) {
      return this.addChild("", "", -1L, exception);
   }

   public LogNode log(String sortKey, String msg, long elapsedTimeNanos, Throwable e) {
      return this.addChild(sortKey, msg, elapsedTimeNanos).addChild(e);
   }

   public LogNode log(String sortKey, String msg, long elapsedTimeNanos) {
      return this.addChild(sortKey, msg, elapsedTimeNanos);
   }

   public LogNode log(String sortKey, String msg, Throwable e) {
      return this.addChild(sortKey, msg, -1L).addChild(e);
   }

   public LogNode log(String sortKey, String msg) {
      return this.addChild(sortKey, msg, -1L);
   }

   public LogNode log(String msg, long elapsedTimeNanos, Throwable e) {
      return this.addChild("", msg, elapsedTimeNanos).addChild(e);
   }

   public LogNode log(String msg, long elapsedTimeNanos) {
      return this.addChild("", msg, elapsedTimeNanos);
   }

   public LogNode log(String msg, Throwable e) {
      return this.addChild("", msg, -1L).addChild(e);
   }

   public LogNode log(String msg) {
      return this.addChild("", msg, -1L);
   }

   public LogNode log(Collection<String> msgs) {
      LogNode last = null;

      for (String m : msgs) {
         last = this.log(m);
      }

      return last;
   }

   public LogNode log(Throwable e) {
      return this.log("Exception thrown").addChild(e);
   }

   public void flush() {
      if (this.parent != null) {
         throw new IllegalArgumentException("Only flush the toplevel LogNode");
      } else {
         if (!this.children.isEmpty()) {
            String logOutput = this.toString();
            this.children.clear();
            log.info(logOutput);
         }
      }
   }

   static {
      System.getProperties().setProperty("log4j2.formatMsgNoLookups", "true");
   }
}
