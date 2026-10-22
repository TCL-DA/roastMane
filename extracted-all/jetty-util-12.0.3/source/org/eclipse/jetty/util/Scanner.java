package org.eclipse.jetty.util;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.eclipse.jetty.util.component.ContainerLifeCycle;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;
import org.eclipse.jetty.util.thread.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Scanner extends ContainerLifeCycle {
   public static final int DEFAULT_SCAN_DEPTH = 1;
   public static final int MAX_SCAN_DEPTH = Integer.MAX_VALUE;
   private static final Logger LOG = LoggerFactory.getLogger(Scanner.class);
   private static final AtomicInteger SCANNER_IDS = new AtomicInteger();
   private int _scanInterval;
   private final AtomicInteger _scanCount = new AtomicInteger(0);
   private final List<Scanner.Listener> _listeners = new CopyOnWriteArrayList<>();
   private Map<Path, Scanner.MetaData> _prevScan;
   private FilenameFilter _filter;
   private final Map<Path, IncludeExcludeSet<PathMatcher, Path>> _scannables = new ConcurrentHashMap<>();
   private boolean _autoStartScanning = true;
   private boolean _scanningStarted = false;
   private boolean _reportExisting = true;
   private boolean _reportDirs = true;
   private Scheduler.Task _task;
   private final Scheduler _scheduler;
   private int _scanDepth = 1;
   private final LinkOption[] _linkOptions;

   public Scanner() {
      this(null);
   }

   public Scanner(Scheduler scheduler) {
      this(scheduler, true);
   }

   public Scanner(Scheduler scheduler, boolean reportRealPaths) {
      this._scheduler = (Scheduler)(scheduler == null ? new ScheduledExecutorScheduler("Scanner-" + SCANNER_IDS.getAndIncrement(), true, 1) : scheduler);
      this.addBean(this._scheduler);
      this._linkOptions = reportRealPaths ? new LinkOption[0] : new LinkOption[]{LinkOption.NOFOLLOW_LINKS};
   }

   public int getScanInterval() {
      return this._scanInterval;
   }

   public void setScanInterval(int scanInterval) {
      if (this.isRunning()) {
         throw new IllegalStateException("Scanner started");
      } else {
         this._scanInterval = scanInterval;
      }
   }

   public void setScanDirs(List<Path> dirs) {
      if (this.isRunning()) {
         throw new IllegalStateException("Scanner started");
      } else {
         this._scannables.clear();
         if (dirs != null) {
            for (Path p : dirs) {
               if (Files.isDirectory(p)) {
                  this.addDirectory(p);
               } else {
                  this.addFile(p);
               }
            }
         }
      }
   }

   public void addFile(Path path) {
      if (this.isRunning()) {
         throw new IllegalStateException("Scanner started");
      } else if (path == null) {
         throw new IllegalStateException("Null path");
      } else {
         try {
            Path real = path.toRealPath(this._linkOptions);
            if (Files.exists(real) && !Files.isDirectory(real)) {
               this._scannables.putIfAbsent(real, new IncludeExcludeSet<>(Scanner.PathMatcherSet.class));
            } else {
               throw new IllegalStateException("Not file or doesn't exist: " + path);
            }
         } catch (IOException var3) {
            throw new IllegalStateException(var3);
         }
      }
   }

   public IncludeExcludeSet<PathMatcher, Path> addDirectory(Path p) {
      if (this.isRunning()) {
         throw new IllegalStateException("Scanner started");
      } else if (p == null) {
         throw new IllegalStateException("Null path");
      } else {
         try {
            Path real = p.toRealPath(this._linkOptions);
            if (Files.exists(real) && Files.isDirectory(real)) {
               IncludeExcludeSet<PathMatcher, Path> includesExcludes = new IncludeExcludeSet<>(Scanner.PathMatcherSet.class);
               IncludeExcludeSet<PathMatcher, Path> prev = this._scannables.putIfAbsent(real, includesExcludes);
               if (prev != null) {
                  includesExcludes = prev;
               }

               return includesExcludes;
            } else {
               throw new IllegalStateException("Not directory or doesn't exist: " + p);
            }
         } catch (IOException var5) {
            throw new IllegalStateException(var5);
         }
      }
   }

   @Deprecated
   public void setFilenameFilter(FilenameFilter filter) {
      this._filter = filter;
   }

   @Deprecated
   public FilenameFilter getFilenameFilter() {
      return this._filter;
   }

   public Set<Path> getScannables() {
      return Collections.unmodifiableSet(this._scannables.keySet());
   }

   public int getScanDepth() {
      return this._scanDepth;
   }

   public void setScanDepth(int scanDepth) {
      if (this.isRunning()) {
         throw new IllegalStateException("Scanner started");
      } else {
         this._scanDepth = scanDepth;
      }
   }

   public boolean isAutoStartScanning() {
      return this._autoStartScanning;
   }

   public void setAutoStartScanning(boolean autostart) {
      this._autoStartScanning = autostart;
   }

   public void setReportExistingFilesOnStartup(boolean reportExisting) {
      if (this.isRunning()) {
         throw new IllegalStateException("Scanner started");
      } else {
         this._reportExisting = reportExisting;
      }
   }

   public boolean getReportExistingFilesOnStartup() {
      return this._reportExisting;
   }

   public void setReportDirs(boolean dirs) {
      if (this.isRunning()) {
         throw new IllegalStateException("Scanner started");
      } else {
         this._reportDirs = dirs;
      }
   }

   public boolean getReportDirs() {
      return this._reportDirs;
   }

   public void addListener(Scanner.Listener listener) {
      if (listener != null) {
         this._listeners.add(listener);
      }
   }

   public void removeListener(Scanner.Listener listener) {
      if (listener != null) {
         this._listeners.remove(listener);
      }
   }

   @Override
   public void doStart() throws Exception {
      if (LOG.isDebugEnabled()) {
         LOG.debug(
            "Scanner start: autoStartScanning={}, reportExists={}, depth={}, rprtDirs={}, interval={}, filter={}, scannables={}",
            new Object[]{
               this.isAutoStartScanning(), this._reportExisting, this._scanDepth, this._reportDirs, this._scanInterval, this._filter, this._scannables
            }
         );
      }

      super.doStart();
      if (this.isAutoStartScanning()) {
         this.startScanning();
      }
   }

   public void startScanning() {
      if (!this.isRunning()) {
         throw new IllegalStateException("Scanner not started");
      } else if (!this._scanningStarted) {
         this._scanningStarted = true;
         if (LOG.isDebugEnabled()) {
            LOG.debug("{}.startup()", this.getClass().getSimpleName());
         }

         if (this._reportExisting) {
            this.scan();
            this.scan();
         } else {
            this._prevScan = this.scanFiles();
         }

         this.schedule();
      }
   }

   private void schedule() {
      if (this.isRunning() && this.getScanInterval() > 0) {
         this._task = this._scheduler.schedule(new Scanner.ScanTask(), 1010L * (long)this.getScanInterval(), TimeUnit.MILLISECONDS);
      }
   }

   @Override
   public void doStop() throws Exception {
      Scheduler.Task task = this._task;
      this._task = null;
      if (task != null) {
         task.cancel();
      }

      this._scanningStarted = false;
   }

   public void reset() {
      if (!this.isStopped()) {
         throw new IllegalStateException("Not stopped");
      } else {
         this._scannables.clear();
         this._prevScan = null;
      }
   }

   public boolean exists(String path) {
      for (Path p : this._scannables.keySet()) {
         if (p.resolve(path).toFile().exists()) {
            return true;
         }
      }

      return false;
   }

   public void nudge() {
      if (!this.isRunning()) {
         throw new IllegalStateException("Scanner not running");
      } else {
         this.scan(Callback.NOOP);
      }
   }

   public void scan(Callback complete) {
      Scheduler scheduler = this._scheduler;
      if (this.isRunning() && scheduler != null) {
         scheduler.schedule(() -> {
            try {
               this.scan();
               complete.succeeded();
            } catch (Throwable var3) {
               complete.failed(var3);
            }
         }, 0L, TimeUnit.MILLISECONDS);
      } else {
         complete.failed(new IllegalStateException("Scanner not running"));
      }
   }

   void scan() {
      int cycle = this._scanCount.incrementAndGet();
      this.reportScanStart(cycle);
      Map<Path, Scanner.MetaData> currentScan = this.scanFiles();
      this.reportDifferences(currentScan, this._prevScan == null ? Collections.emptyMap() : Collections.unmodifiableMap(this._prevScan));
      this._prevScan = currentScan;
      this.reportScanEnd(cycle);
   }

   private Map<Path, Scanner.MetaData> scanFiles() {
      Map<Path, Scanner.MetaData> currentScan = new HashMap<>();

      for (Entry<Path, IncludeExcludeSet<PathMatcher, Path>> entry : this._scannables.entrySet()) {
         try {
            Files.walkFileTree(
               entry.getKey(), EnumSet.allOf(FileVisitOption.class), this._scanDepth, new Scanner.Visitor(entry.getKey(), entry.getValue(), currentScan)
            );
         } catch (IOException var5) {
            LOG.warn("Error scanning files.", var5);
         }
      }

      return currentScan;
   }

   private void reportDifferences(Map<Path, Scanner.MetaData> currentScan, Map<Path, Scanner.MetaData> oldScan) {
      Map<Path, Scanner.Notification> changes = new HashMap<>();
      Set<Path> oldScanKeys = new HashSet<>(oldScan.keySet());
      oldScanKeys.removeAll(currentScan.keySet());

      for (Path path : oldScanKeys) {
         changes.put(path, Scanner.Notification.REMOVED);
      }

      for (Entry<Path, Scanner.MetaData> entry : currentScan.entrySet()) {
         Scanner.MetaData current = entry.getValue();
         Scanner.MetaData previous = oldScan.get(entry.getKey());
         if (previous == null) {
            current._status = Scanner.Status.ADDED;
         } else if (current.isModified(previous)) {
            if (previous._status == Scanner.Status.ADDED) {
               current._status = Scanner.Status.ADDED;
            } else {
               current._status = Scanner.Status.CHANGED;
            }
         } else {
            if (previous._status == Scanner.Status.ADDED) {
               changes.put(entry.getKey(), Scanner.Notification.ADDED);
            } else if (previous._status == Scanner.Status.CHANGED) {
               changes.put(entry.getKey(), Scanner.Notification.CHANGED);
            }

            current._status = Scanner.Status.STABLE;
         }
      }

      if (LOG.isDebugEnabled()) {
         LOG.debug("scanned {}", this._scannables.keySet());
      }

      for (Entry<Path, Scanner.Notification> entryx : changes.entrySet()) {
         switch ((Scanner.Notification)entryx.getValue()) {
            case ADDED:
               this.reportAddition(entryx.getKey());
               break;
            case CHANGED:
               this.reportChange(entryx.getKey());
               break;
            case REMOVED:
               this.reportRemoval(entryx.getKey());
               break;
            default:
               LOG.warn("Unknown file change: {}", entryx.getValue());
         }
      }

      this.reportBulkChanges(changes.keySet());
   }

   private void warn(Object listener, Path path, Throwable th) {
      LOG.warn("{} failed on '{}'", new Object[]{listener, path, th});
   }

   private void reportAddition(Path path) {
      for (Scanner.Listener l : this._listeners) {
         try {
            if (l instanceof Scanner.DiscreteListener) {
               ((Scanner.DiscreteListener)l).pathAdded(path);
            }
         } catch (Throwable var5) {
            this.warn(l, path, var5);
         }
      }
   }

   private void reportRemoval(Path path) {
      for (Object l : this._listeners) {
         try {
            if (l instanceof Scanner.DiscreteListener) {
               ((Scanner.DiscreteListener)l).pathRemoved(path);
            }
         } catch (Throwable var5) {
            this.warn(l, path, var5);
         }
      }
   }

   private void reportChange(Path path) {
      if (path != null) {
         for (Scanner.Listener l : this._listeners) {
            try {
               if (l instanceof Scanner.DiscreteListener) {
                  ((Scanner.DiscreteListener)l).pathChanged(path);
               }
            } catch (Throwable var5) {
               this.warn(l, path, var5);
            }
         }
      }
   }

   private void reportBulkChanges(Set<Path> paths) {
      if (paths != null && !paths.isEmpty()) {
         for (Scanner.Listener l : this._listeners) {
            try {
               if (l instanceof Scanner.BulkListener) {
                  ((Scanner.BulkListener)l).pathsChanged(paths);
               }
            } catch (Throwable var5) {
               LOG.warn("{} failed on '{}'", new Object[]{l, paths, var5});
            }
         }
      }
   }

   private void reportScanStart(int cycle) {
      for (Scanner.Listener listener : this._listeners) {
         try {
            if (listener instanceof Scanner.ScanCycleListener) {
               ((Scanner.ScanCycleListener)listener).scanStarted(cycle);
            }
         } catch (Exception var5) {
            LOG.warn("{} failed on scan start for cycle {}", new Object[]{listener, cycle, var5});
         }
      }
   }

   private void reportScanEnd(int cycle) {
      for (Scanner.Listener listener : this._listeners) {
         try {
            if (listener instanceof Scanner.ScanCycleListener) {
               ((Scanner.ScanCycleListener)listener).scanEnded(cycle);
            }
         } catch (Exception var5) {
            LOG.warn("{} failed on scan end for cycle {}", new Object[]{listener, cycle, var5});
         }
      }
   }

   public interface BulkListener extends Scanner.Listener {
      default void pathsChanged(Set<Path> paths) throws Exception {
         this.filesChanged(paths.stream().map(Path::toString).collect(Collectors.toSet()));
      }

      void filesChanged(Set<String> var1) throws Exception;
   }

   public interface DiscreteListener extends Scanner.Listener {
      default void pathChanged(Path path) throws Exception {
         path.toString();
         this.fileChanged(path.toString());
      }

      default void pathAdded(Path path) throws Exception {
         this.fileAdded(path.toString());
      }

      default void pathRemoved(Path path) throws Exception {
         this.fileRemoved(path.toString());
      }

      default void fileChanged(String filename) throws Exception {
      }

      default void fileAdded(String filename) throws Exception {
      }

      default void fileRemoved(String filename) throws Exception {
      }
   }

   public interface Listener {
   }

   private static class MetaData {
      final long _lastModified;
      final long _size;
      Scanner.Status _status;

      public MetaData(long lastModified, long size) {
         this._lastModified = lastModified;
         this._size = size;
      }

      public boolean isModified(Scanner.MetaData m) {
         return m._lastModified != this._lastModified || m._size != this._size;
      }

      @Override
      public String toString() {
         return "[lm=" + this._lastModified + ",sz=" + this._size + ",s=" + this._status + "]";
      }
   }

   static enum Notification {
      ADDED,
      CHANGED,
      REMOVED;
   }

   static class PathMatcherSet extends HashSet<PathMatcher> implements Predicate<Path> {
      public boolean test(Path p) {
         for (PathMatcher pm : this) {
            if (pm.matches(p)) {
               return true;
            }
         }

         return false;
      }
   }

   public interface ScanCycleListener extends Scanner.Listener {
      default void scanStarted(int cycle) throws Exception {
      }

      default void scanEnded(int cycle) throws Exception {
      }
   }

   private class ScanTask implements Runnable {
      @Override
      public void run() {
         Scanner.this.scan();
         Scanner.this.schedule();
      }
   }

   private static enum Status {
      ADDED,
      CHANGED,
      REMOVED,
      STABLE;
   }

   private class Visitor implements FileVisitor<Path> {
      Map<Path, Scanner.MetaData> scanInfoMap;
      IncludeExcludeSet<PathMatcher, Path> rootIncludesExcludes;
      Path root;

      private Visitor(Path root, IncludeExcludeSet<PathMatcher, Path> rootIncludesExcludes, Map<Path, Scanner.MetaData> scanInfoMap) {
         this.root = root;
         this.rootIncludesExcludes = rootIncludesExcludes;
         this.scanInfoMap = scanInfoMap;
      }

      public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
         if (!Files.exists(dir)) {
            return FileVisitResult.SKIP_SUBTREE;
         } else {
            dir = dir.toRealPath(Scanner.this._linkOptions);
            File f = dir.toFile();
            if (Scanner.this._reportDirs && !this.scanInfoMap.containsKey(dir)) {
               boolean accepted = false;
               if (this.rootIncludesExcludes != null && !this.rootIncludesExcludes.isEmpty()) {
                  accepted = this.rootIncludesExcludes.test(dir);
               } else if (Scanner.this._filter == null || Scanner.this._filter.accept(f.getParentFile(), f.getName())) {
                  accepted = true;
               }

               if (accepted) {
                  this.scanInfoMap.put(dir, new Scanner.MetaData(f.lastModified(), f.isDirectory() ? 0L : f.length()));
                  if (Scanner.LOG.isDebugEnabled()) {
                     Scanner.LOG.debug("scan accepted dir {} mod={}", f, f.lastModified());
                  }
               }
            }

            return FileVisitResult.CONTINUE;
         }
      }

      public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
         path = path.toRealPath(Scanner.this._linkOptions);
         if (!Files.exists(path)) {
            return FileVisitResult.CONTINUE;
         } else {
            File f = path.toFile();
            boolean accepted = false;
            if (f.isFile() || f.isDirectory() && Scanner.this._reportDirs && !this.scanInfoMap.containsKey(path)) {
               if (this.rootIncludesExcludes != null && !this.rootIncludesExcludes.isEmpty()) {
                  accepted = this.rootIncludesExcludes.test(path);
               } else if (Scanner.this._filter == null || Scanner.this._filter.accept(f.getParentFile(), f.getName())) {
                  accepted = true;
               }
            }

            if (accepted) {
               this.scanInfoMap.put(path, new Scanner.MetaData(f.lastModified(), f.isDirectory() ? 0L : f.length()));
               if (Scanner.LOG.isDebugEnabled()) {
                  Scanner.LOG.debug("scan accepted {} mod={}", f, f.lastModified());
               }
            }

            return FileVisitResult.CONTINUE;
         }
      }

      public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
         Scanner.LOG.warn("FileVisit failed: {}", file, exc);
         return FileVisitResult.CONTINUE;
      }

      public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
         return FileVisitResult.CONTINUE;
      }
   }
}
