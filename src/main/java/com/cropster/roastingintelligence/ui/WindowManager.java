package com.cropster.roastingintelligence.ui;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.WeakHashMap;
import javafx.event.EventHandler;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import org.apache.commons.lang3.StringUtils;

public final class WindowManager {
   private static final String DEFAULT_ID = "managed-window-default-id";
   private static final String MANAGED_ID_PROPERTY = "managed-window-id";
   private final Map<Window, String> showingWindowToIdMap = Collections.synchronizedMap(new WeakHashMap<>());
   private final EventHandler<WindowEvent> onHiddenHandler = e -> this.unregister((Window)e.getSource());
   private final EventHandler<WindowEvent> onShownHandler = e -> {
      Window window = (Window)e.getSource();
      this.showingWindowToIdMap.put(window, (String)window.getProperties().getOrDefault("managed-window-id", "managed-window-id"));
   };
   private volatile boolean locked = false;

   public static WindowManager get() {
      return WindowManager.WindowsManageraHolder.INSTANCE;
   }

   private WindowManager() {
   }

   public Optional<Window> getWindow(String id) {
      Objects.requireNonNull(id, "Null 'id' parameter.");
      return this.showingWindowToIdMap.entrySet().stream().filter(e -> id.equals(e.getValue())).map(e -> e.getKey()).findFirst();
   }

   public synchronized boolean isLocked() {
      return this.locked;
   }

   public boolean isShowing(String id) {
      return this.showingWindowToIdMap.containsValue(Objects.requireNonNull(id, "Null 'id' parameter."));
   }

   public synchronized void lock() {
      this.locked = true;
   }

   public boolean register(Window window, String id) {
      if (this.isLocked()) {
         return false;
      } else {
         Objects.requireNonNull(window, "Null 'window' parameter.");
         window.getProperties().put("managed-window-id", StringUtils.isBlank(id) ? "managed-window-default-id" : id);
         window.addEventFilter(WindowEvent.WINDOW_HIDDEN, this.onHiddenHandler);
         if (window.isShowing()) {
            this.showingWindowToIdMap.put(window, (String)window.getProperties().getOrDefault("managed-window-id", "managed-window-id"));
         } else {
            window.addEventFilter(WindowEvent.WINDOW_SHOWN, this.onShownHandler);
         }

         return true;
      }
   }

   public Set<Window> showingWindows() {
      return Set.copyOf(this.showingWindowToIdMap.keySet());
   }

   public synchronized void unlock() {
      this.locked = false;
   }

   public Window unregister(Window window) {
      Objects.requireNonNull(window, "Null 'window' parameter.");
      window.removeEventFilter(WindowEvent.WINDOW_HIDDEN, this.onHiddenHandler);
      window.removeEventFilter(WindowEvent.WINDOW_SHOWN, this.onShownHandler);
      this.showingWindowToIdMap.remove(window);
      return window;
   }

   private static final class WindowsManageraHolder {
      private static final WindowManager INSTANCE = new WindowManager();
   }
}
