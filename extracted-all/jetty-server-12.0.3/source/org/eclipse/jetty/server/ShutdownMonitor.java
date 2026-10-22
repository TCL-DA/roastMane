package org.eclipse.jetty.server;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.component.Destroyable;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.util.thread.AutoLock;
import org.eclipse.jetty.util.thread.ShutdownThread;
import org.eclipse.jetty.util.thread.AutoLock.WithCondition;

public class ShutdownMonitor {
   private final WithCondition _lock = new WithCondition();
   private final Set<LifeCycle> _lifeCycles = new LinkedHashSet<>();
   private boolean debug;
   private final String host;
   private int port;
   private String key;
   private boolean exitVm = true;
   private boolean alive;

   public static ShutdownMonitor getInstance() {
      return ShutdownMonitor.Holder.instance;
   }

   protected static void reset() {
      ShutdownMonitor.Holder.instance = new ShutdownMonitor();
   }

   public static void register(LifeCycle... lifeCycles) {
      getInstance().addLifeCycles(lifeCycles);
   }

   public static void deregister(LifeCycle lifeCycle) {
      getInstance().removeLifeCycle(lifeCycle);
   }

   public static boolean isRegistered(LifeCycle lifeCycle) {
      return getInstance().containsLifeCycle(lifeCycle);
   }

   private ShutdownMonitor() {
      this.debug = System.getProperty("DEBUG") != null;
      this.host = System.getProperty("STOP.HOST", "127.0.0.1");
      this.port = Integer.getInteger("STOP.PORT", -1);
      this.key = System.getProperty("STOP.KEY", null);
      this.exitVm = Boolean.valueOf(System.getProperty("STOP.EXIT", "true"));
   }

   private void addLifeCycles(LifeCycle... lifeCycles) {
      AutoLock l = this._lock.lock();

      try {
         this._lifeCycles.addAll(Arrays.asList(lifeCycles));
      } catch (Throwable var6) {
         if (l != null) {
            try {
               l.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (l != null) {
         l.close();
      }
   }

   private void removeLifeCycle(LifeCycle lifeCycle) {
      AutoLock l = this._lock.lock();

      try {
         this._lifeCycles.remove(lifeCycle);
      } catch (Throwable var6) {
         if (l != null) {
            try {
               l.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (l != null) {
         l.close();
      }
   }

   private boolean containsLifeCycle(LifeCycle lifeCycle) {
      AutoLock l = this._lock.lock();

      boolean var3;
      try {
         var3 = this._lifeCycles.contains(lifeCycle);
      } catch (Throwable var6) {
         if (l != null) {
            try {
               l.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (l != null) {
         l.close();
      }

      return var3;
   }

   private void debug(String format, Object... args) {
      if (this.debug) {
         System.err.printf("[ShutdownMonitor] " + format + "%n", args);
      }
   }

   private void debug(Throwable t) {
      if (this.debug) {
         t.printStackTrace(System.err);
      }
   }

   public String getKey() {
      AutoLock l = this._lock.lock();

      String var2;
      try {
         var2 = this.key;
      } catch (Throwable var5) {
         if (l != null) {
            try {
               l.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (l != null) {
         l.close();
      }

      return var2;
   }

   public int getPort() {
      AutoLock l = this._lock.lock();

      int var2;
      try {
         var2 = this.port;
      } catch (Throwable var5) {
         if (l != null) {
            try {
               l.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (l != null) {
         l.close();
      }

      return var2;
   }

   public boolean isExitVm() {
      AutoLock l = this._lock.lock();

      boolean var2;
      try {
         var2 = this.exitVm;
      } catch (Throwable var5) {
         if (l != null) {
            try {
               l.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (l != null) {
         l.close();
      }

      return var2;
   }

   public void setDebug(boolean flag) {
      this.debug = flag;
   }

   public void setExitVm(boolean exitVm) {
      AutoLock l = this._lock.lock();

      try {
         if (this.alive) {
            throw new IllegalStateException("ShutdownMonitor already started");
         }

         this.exitVm = exitVm;
      } catch (Throwable var6) {
         if (l != null) {
            try {
               l.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (l != null) {
         l.close();
      }
   }

   public void setKey(String key) {
      AutoLock l = this._lock.lock();

      try {
         if (this.alive) {
            throw new IllegalStateException("ShutdownMonitor already started");
         }

         this.key = key;
      } catch (Throwable var6) {
         if (l != null) {
            try {
               l.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (l != null) {
         l.close();
      }
   }

   public void setPort(int port) {
      AutoLock l = this._lock.lock();

      try {
         if (this.alive) {
            throw new IllegalStateException("ShutdownMonitor already started");
         }

         this.port = port;
      } catch (Throwable var6) {
         if (l != null) {
            try {
               l.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (l != null) {
         l.close();
      }
   }

   public void start() throws Exception {
      AutoLock l = this._lock.lock();

      label45: {
         try {
            if (this.alive) {
               this.debug("Already started");
               break label45;
            }

            ServerSocket serverSocket = this.listen();
            if (serverSocket != null) {
               this.alive = true;
               Thread thread = new Thread(new ShutdownMonitor.ShutdownMonitorRunnable(serverSocket));
               thread.setDaemon(true);
               thread.setName("ShutdownMonitor");
               thread.start();
            }
         } catch (Throwable var5) {
            if (l != null) {
               try {
                  l.close();
               } catch (Throwable var4) {
                  var5.addSuppressed(var4);
               }
            }

            throw var5;
         }

         if (l != null) {
            l.close();
         }

         return;
      }

      if (l != null) {
         l.close();
      }
   }

   private void stop() {
      WithCondition l = this._lock.lock();

      try {
         this.alive = false;
         l.signalAll();
      } catch (Throwable var5) {
         if (l != null) {
            try {
               l.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (l != null) {
         l.close();
      }
   }

   public void await() throws InterruptedException {
      WithCondition l = this._lock.lock();

      try {
         while (this.alive) {
            l.await();
         }
      } catch (Throwable var5) {
         if (l != null) {
            try {
               l.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (l != null) {
         l.close();
      }
   }

   protected boolean isAlive() {
      AutoLock l = this._lock.lock();

      boolean var2;
      try {
         var2 = this.alive;
      } catch (Throwable var5) {
         if (l != null) {
            try {
               l.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (l != null) {
         l.close();
      }

      return var2;
   }

   private ServerSocket listen() {
      int port = this.getPort();
      if (port < 0) {
         this.debug("Not enabled (port < 0): %d", port);
         return null;
      } else {
         String key = this.getKey();

         Object e;
         try {
            ServerSocket serverSocket = new ServerSocket();

            try {
               serverSocket.setReuseAddress(true);
               serverSocket.bind(new InetSocketAddress(InetAddress.getByName(this.host), port));
            } catch (Throwable var9) {
               IO.close(serverSocket);
               throw var9;
            }

            if (port == 0) {
               port = serverSocket.getLocalPort();
               System.out.printf("STOP.PORT=%d%n", port);
               this.setPort(port);
            }

            if (key == null) {
               key = Long.toString((long)(9.223372E18F * Math.random() + (double)this.hashCode() + (double)System.currentTimeMillis()), 36);
               System.out.printf("STOP.KEY=%s%n", key);
               this.setKey(key);
            }

            return serverSocket;
         } catch (Throwable var10) {
            this.debug(var10);
            System.err.println("Error binding ShutdownMonitor to port " + port + ": " + var10.toString());
            e = null;
         } finally {
            this.debug("STOP.PORT=%d", port);
            this.debug("STOP.KEY=%s", key);
            this.debug("STOP.EXIT=%b", this.exitVm);
         }

         return (ServerSocket)e;
      }
   }

   @Override
   public String toString() {
      return String.format("%s[port=%d,alive=%b]", this.getClass().getName(), this.getPort(), this.isAlive());
   }

   private static class Holder {
      static ShutdownMonitor instance = new ShutdownMonitor();
   }

   private class ShutdownMonitorRunnable implements Runnable {
      private final ServerSocket serverSocket;

      private ShutdownMonitorRunnable(ServerSocket serverSocket) {
         this.serverSocket = serverSocket;
      }

      @Override
      public void run() {
         ShutdownMonitor.this.debug("Started");

         try {
            String key = ShutdownMonitor.this.getKey();

            while (true) {
               try (Socket socket = this.serverSocket.accept()) {
                  LineNumberReader reader = new LineNumberReader(new InputStreamReader(socket.getInputStream()));
                  String receivedKey = reader.readLine();
                  if (key.equals(receivedKey)) {
                     String cmd = reader.readLine();
                     ShutdownMonitor.this.debug("command=%s", cmd);
                     OutputStream out = socket.getOutputStream();
                     boolean exitVm = ShutdownMonitor.this.isExitVm();
                     if ("stop".equalsIgnoreCase(cmd)) {
                        ShutdownMonitor.this.debug("Performing stop command");
                        this.stopLifeCycles(ShutdownThread::isRegistered, exitVm);
                        ShutdownMonitor.this.debug("Informing client that we are stopped");
                        this.informClient(out, "Stopped\r\n");
                        if (exitVm) {
                           ShutdownMonitor.this.debug("Killing JVM");
                           System.exit(0);
                           continue;
                        }
                        break;
                     } else if ("forcestop".equalsIgnoreCase(cmd)) {
                        ShutdownMonitor.this.debug("Performing forced stop command");
                        this.stopLifeCycles(l -> true, exitVm);
                        ShutdownMonitor.this.debug("Informing client that we are stopped");
                        this.informClient(out, "Stopped\r\n");
                        if (!exitVm) {
                           break;
                        }

                        ShutdownMonitor.this.debug("Killing JVM");
                        System.exit(0);
                     } else if ("stopexit".equalsIgnoreCase(cmd)) {
                        ShutdownMonitor.this.debug("Performing stop and exit commands");
                        this.stopLifeCycles(ShutdownThread::isRegistered, true);
                        ShutdownMonitor.this.debug("Informing client that we are stopped");
                        this.informClient(out, "Stopped\r\n");
                        ShutdownMonitor.this.debug("Killing JVM");
                        System.exit(0);
                     } else if ("exit".equalsIgnoreCase(cmd)) {
                        ShutdownMonitor.this.debug("Killing JVM");
                        System.exit(0);
                     } else if ("status".equalsIgnoreCase(cmd)) {
                        this.informClient(out, "OK\r\n");
                     } else if ("pid".equalsIgnoreCase(cmd)) {
                        this.informClient(out, Long.toString(ProcessHandle.current().pid()));
                     }
                  } else {
                     ShutdownMonitor.this.debug("Ignoring command with incorrect key: %s", receivedKey);
                  }
               } catch (Throwable var16) {
                  ShutdownMonitor.this.debug(var16);
               }
            }
         } catch (Throwable var17) {
            ShutdownMonitor.this.debug(var17);
         } finally {
            IO.close(this.serverSocket);
            ShutdownMonitor.this.stop();
            ShutdownMonitor.this.debug("Stopped");
         }
      }

      private void informClient(OutputStream out, String message) throws IOException {
         out.write(message.getBytes(StandardCharsets.UTF_8));
         out.flush();
      }

      private void stopLifeCycles(Predicate<LifeCycle> predicate, boolean destroy) {
         AutoLock l = ShutdownMonitor.this._lock.lock();

         List<LifeCycle> lifeCycles;
         try {
            lifeCycles = new ArrayList<>(ShutdownMonitor.this._lifeCycles);
         } catch (Throwable var9) {
            if (l != null) {
               try {
                  l.close();
               } catch (Throwable var7) {
                  var9.addSuppressed(var7);
               }
            }

            throw var9;
         }

         if (l != null) {
            l.close();
         }

         for (LifeCycle lx : lifeCycles) {
            try {
               if (lx.isStarted() && predicate.test(lx)) {
                  lx.stop();
               }

               if (lx instanceof Destroyable && destroy) {
                  ((Destroyable)lx).destroy();
               }
            } catch (Throwable var8) {
               ShutdownMonitor.this.debug(var8);
            }
         }
      }
   }
}
