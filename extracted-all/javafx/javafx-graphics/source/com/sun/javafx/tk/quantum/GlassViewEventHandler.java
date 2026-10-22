package com.sun.javafx.tk.quantum;

import com.sun.glass.events.ViewEvent;
import com.sun.glass.ui.Accessible;
import com.sun.glass.ui.ClipboardAssistance;
import com.sun.glass.ui.Screen;
import com.sun.glass.ui.View;
import com.sun.glass.ui.Window;
import com.sun.javafx.PlatformUtil;
import com.sun.javafx.collections.TrackableObservableList;
import com.sun.javafx.logging.PulseLogger;
import com.sun.javafx.scene.input.KeyCodeMap;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener.Change;
import javafx.event.EventType;
import javafx.geometry.Point2D;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.InputMethodHighlight;
import javafx.scene.input.InputMethodTextRun;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.RotateEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.SwipeEvent;
import javafx.scene.input.TouchPoint;
import javafx.scene.input.TransferMode;
import javafx.scene.input.ZoomEvent;

class GlassViewEventHandler extends View.EventHandler {
   static boolean zoomGestureEnabled;
   static boolean rotateGestureEnabled;
   static boolean scrollGestureEnabled;
   private ViewScene scene;
   private final GlassSceneDnDEventHandler dndHandler;
   private final GestureRecognizers gestures;
   private final PaintCollector collector = PaintCollector.getInstance();
   private final GlassViewEventHandler.KeyEventNotification keyNotification = new GlassViewEventHandler.KeyEventNotification();
   private int mouseButtonPressedMask = 0;
   private final GlassViewEventHandler.MouseEventNotification mouseNotification = new GlassViewEventHandler.MouseEventNotification();
   private ClipboardAssistance dropSourceAssistant;
   private final GlassViewEventHandler.ViewEventNotification viewNotification = new GlassViewEventHandler.ViewEventNotification();

   public GlassViewEventHandler(ViewScene var1) {
      this.scene = var1;
      this.dndHandler = new GlassSceneDnDEventHandler(var1);
      this.gestures = new GestureRecognizers();
      if (PlatformUtil.isWindows() || PlatformUtil.isIOS() || PlatformUtil.isEmbedded()) {
         this.gestures.add(new SwipeGestureRecognizer(var1));
      }

      if (zoomGestureEnabled) {
         this.gestures.add(new ZoomGestureRecognizer(var1));
      }

      if (rotateGestureEnabled) {
         this.gestures.add(new RotateGestureRecognizer(var1));
      }

      if (scrollGestureEnabled) {
         this.gestures.add(new ScrollGestureRecognizer(var1));
      }
   }

   private static boolean allowableFullScreenKeys(int var0) {
      switch (var0) {
         case 9:
         case 10:
         case 32:
         case 33:
         case 34:
         case 35:
         case 36:
         case 37:
         case 38:
         case 39:
         case 40:
            return true;
         case 11:
         case 12:
         case 13:
         case 14:
         case 15:
         case 16:
         case 17:
         case 18:
         case 19:
         case 20:
         case 21:
         case 22:
         case 23:
         case 24:
         case 25:
         case 26:
         case 27:
         case 28:
         case 29:
         case 30:
         case 31:
         default:
            return false;
      }
   }

   private boolean checkFullScreenKeyEvent(int var1, int var2, char[] var3, int var4) {
      return this.scene.getWindowStage().isTrustedFullScreen() || allowableFullScreenKeys(var2);
   }

   private static EventType<KeyEvent> keyEventType(int var0) {
      switch (var0) {
         case 111:
            return KeyEvent.KEY_PRESSED;
         case 112:
            return KeyEvent.KEY_RELEASED;
         case 113:
            return KeyEvent.KEY_TYPED;
         default:
            if (QuantumToolkit.verbose) {
               System.err.println("Unknown Glass key event type: " + var0);
            }

            return null;
      }
   }

   @Override
   public void handleKeyEvent(View var1, long var2, int var4, int var5, char[] var6, int var7) {
      this.keyNotification.view = var1;
      this.keyNotification.time = var2;
      this.keyNotification.type = var4;
      this.keyNotification.key = var5;
      this.keyNotification.chars = var6;
      this.keyNotification.modifiers = var7;
      QuantumToolkit.runWithoutRenderLock(() -> AccessController.doPrivileged(this.keyNotification, this.scene.getAccessControlContext()));
   }

   private static EventType<MouseEvent> mouseEventType(int var0) {
      switch (var0) {
         case 221:
            return MouseEvent.MOUSE_PRESSED;
         case 222:
            return MouseEvent.MOUSE_RELEASED;
         case 223:
            return MouseEvent.MOUSE_DRAGGED;
         case 224:
            return MouseEvent.MOUSE_MOVED;
         case 225:
            return MouseEvent.MOUSE_ENTERED;
         case 226:
            return MouseEvent.MOUSE_EXITED;
         case 227:
         default:
            if (QuantumToolkit.verbose) {
               System.err.println("Unknown Glass mouse event type: " + var0);
            }

            return null;
         case 228:
            throw new IllegalArgumentException("WHEEL event cannot be translated to MouseEvent, must be translated to ScrollEvent");
      }
   }

   private static MouseButton mouseEventButton(int var0) {
      switch (var0) {
         case 212:
            return MouseButton.PRIMARY;
         case 213:
            return MouseButton.SECONDARY;
         case 214:
            return MouseButton.MIDDLE;
         case 215:
            return MouseButton.BACK;
         case 216:
            return MouseButton.FORWARD;
         default:
            return MouseButton.NONE;
      }
   }

   @Override
   public void handleMouseEvent(View var1, long var2, int var4, int var5, int var6, int var7, int var8, int var9, int var10, boolean var11, boolean var12) {
      this.mouseNotification.view = var1;
      this.mouseNotification.time = var2;
      this.mouseNotification.type = var4;
      this.mouseNotification.button = var5;
      this.mouseNotification.x = var6;
      this.mouseNotification.y = var7;
      this.mouseNotification.xAbs = var8;
      this.mouseNotification.yAbs = var9;
      this.mouseNotification.modifiers = var10;
      this.mouseNotification.isPopupTrigger = var11;
      this.mouseNotification.isSynthesized = var12;
      QuantumToolkit.runWithoutRenderLock(() -> AccessController.doPrivileged(this.mouseNotification, this.scene.getAccessControlContext()));
   }

   @Override
   public void handleMenuEvent(View var1, int var2, int var3, int var4, int var5, boolean var6) {
      if (PulseLogger.PULSE_LOGGING_ENABLED) {
         PulseLogger.newInput("MENU_EVENT");
      }

      WindowStage var7 = this.scene.getWindowStage();

      try {
         if (var7 != null) {
            var7.setInAllowedEventHandler(true);
         }

         QuantumToolkit.runWithoutRenderLock(
            () -> AccessController.doPrivileged(
                  (PrivilegedAction<Void>)(() -> {
                     if (this.scene.sceneListener != null) {
                        Window var19 = var1.getWindow();
                        double var7x;
                        double var9;
                        double var11;
                        double var13;
                        double var15;
                        double var17;
                        if (var19 != null) {
                           var7x = (double)var19.getPlatformScaleX();
                           var9 = (double)var19.getPlatformScaleY();
                           Screen var20 = var19.getScreen();
                           if (var20 != null) {
                              var11 = (double)var20.getPlatformX();
                              var13 = (double)var20.getPlatformY();
                              var15 = (double)var20.getX();
                              var17 = (double)var20.getY();
                           } else {
                              var17 = 0.0;
                              var15 = 0.0;
                              var13 = 0.0;
                              var11 = 0.0;
                           }
                        } else {
                           var9 = 1.0;
                           var7x = 1.0;
                           var17 = 0.0;
                           var15 = 0.0;
                           var13 = 0.0;
                           var11 = 0.0;
                        }

                        this.scene
                           .sceneListener
                           .menuEvent(
                              (double)var2 / var7x, (double)var3 / var9, var15 + ((double)var4 - var11) / var7x, var17 + ((double)var5 - var13) / var9, var6
                           );
                     }

                     return null;
                  }),
                  this.scene.getAccessControlContext()
               )
         );
      } finally {
         if (var7 != null) {
            var7.setInAllowedEventHandler(false);
         }

         if (PulseLogger.PULSE_LOGGING_ENABLED) {
            PulseLogger.newInput(null);
         }
      }
   }

   @Override
   public void handleScrollEvent(
      View var1,
      long var2,
      int var4,
      int var5,
      int var6,
      int var7,
      double var8,
      double var10,
      int var12,
      int var13,
      int var14,
      int var15,
      int var16,
      double var17,
      double var19
   ) {
      if (PulseLogger.PULSE_LOGGING_ENABLED) {
         PulseLogger.newInput("SCROLL_EVENT");
      }

      WindowStage var21 = this.scene.getWindowStage();

      try {
         if (var21 != null) {
            var21.setInAllowedEventHandler(false);
         }

         QuantumToolkit.runWithoutRenderLock(
            () -> AccessController.doPrivileged(
                  (PrivilegedAction<Void>)(() -> {
                     if (this.scene.sceneListener != null) {
                        Window var19x = var1.getWindow();
                        double var20;
                        double var22;
                        double var24x;
                        double var26;
                        double var28;
                        double var30;
                        if (var19x != null) {
                           var20 = (double)var19x.getPlatformScaleX();
                           var22 = (double)var19x.getPlatformScaleY();
                           Screen var32 = var19x.getScreen();
                           if (var32 != null) {
                              var24x = (double)var32.getPlatformX();
                              var26 = (double)var32.getPlatformY();
                              var28 = (double)var32.getX();
                              var30 = (double)var32.getY();
                           } else {
                              var30 = 0.0;
                              var28 = 0.0;
                              var26 = 0.0;
                              var24x = 0.0;
                           }
                        } else {
                           var22 = 1.0;
                           var20 = 1.0;
                           var30 = 0.0;
                           var28 = 0.0;
                           var26 = 0.0;
                           var24x = 0.0;
                        }

                        this.scene
                           .sceneListener
                           .scrollEvent(
                              ScrollEvent.SCROLL,
                              var8 / var20,
                              var10 / var22,
                              0.0,
                              0.0,
                              var17,
                              var19,
                              0,
                              var14,
                              var13,
                              var16,
                              var15,
                              (double)var4 / var20,
                              (double)var5 / var22,
                              var28 + ((double)var6 - var24x) / var20,
                              var30 + ((double)var7 - var26) / var22,
                              (var12 & 1) != 0,
                              (var12 & 4) != 0,
                              (var12 & 8) != 0,
                              (var12 & 16) != 0,
                              false,
                              false
                           );
                     }

                     return null;
                  }),
                  this.scene.getAccessControlContext()
               )
         );
      } finally {
         if (var21 != null) {
            var21.setInAllowedEventHandler(false);
         }

         if (PulseLogger.PULSE_LOGGING_ENABLED) {
            PulseLogger.newInput(null);
         }
      }
   }

   private static byte inputMethodEventAttrValue(int var0, int[] var1, byte[] var2) {
      if (var1 != null) {
         for (int var3 = 0; var3 < var1.length - 1; var3++) {
            if (var0 >= var1[var3] && var0 < var1[var3 + 1]) {
               return var2[var3];
            }
         }
      }

      return 4;
   }

   private static ObservableList<InputMethodTextRun> inputMethodEventComposed(String var0, int var1, int[] var2, int[] var3, byte[] var4) {
      TrackableObservableList var5 = new TrackableObservableList<InputMethodTextRun>() {
         protected void onChanged(Change<InputMethodTextRun> var1) {
         }
      };
      if (var1 < var0.length()) {
         if (var2 == null) {
            var5.add(new InputMethodTextRun(var0.substring(var1), InputMethodHighlight.UNSELECTED_RAW));
         } else {
            for (int var6 = 0; var6 < var2.length - 1; var6++) {
               if (var2[var6] >= var1) {
                  InputMethodHighlight var7;
                  switch (inputMethodEventAttrValue(var2[var6], var3, var4)) {
                     case 0:
                     case 4:
                     default:
                        var7 = InputMethodHighlight.UNSELECTED_RAW;
                        break;
                     case 1:
                        var7 = InputMethodHighlight.SELECTED_CONVERTED;
                        break;
                     case 2:
                        var7 = InputMethodHighlight.UNSELECTED_CONVERTED;
                        break;
                     case 3:
                        var7 = InputMethodHighlight.SELECTED_RAW;
                  }

                  var5.add(new InputMethodTextRun(var0.substring(var2[var6], var2[var6 + 1]), var7));
               }
            }
         }
      }

      return var5;
   }

   @Override
   public void handleInputMethodEvent(long var1, String var3, int[] var4, int[] var5, byte[] var6, int var7, int var8) {
      if (PulseLogger.PULSE_LOGGING_ENABLED) {
         PulseLogger.newInput("INPUT_METHOD_EVENT");
      }

      WindowStage var9 = this.scene.getWindowStage();

      try {
         if (var9 != null) {
            var9.setInAllowedEventHandler(true);
         }

         QuantumToolkit.runWithoutRenderLock(() -> AccessController.doPrivileged((PrivilegedAction<Void>)(() -> {
               if (this.scene.sceneListener != null) {
                  String var7x = var3 != null ? var3 : "";
                  EventType var8x = InputMethodEvent.INPUT_METHOD_TEXT_CHANGED;
                  ObservableList var9x = inputMethodEventComposed(var7x, var7, var4, var5, var6);
                  String var10 = var7x.substring(0, var7);
                  this.scene.sceneListener.inputMethodEvent(var8x, var9x, var10, var8);
               }

               return null;
            }), this.scene.getAccessControlContext()));
      } finally {
         if (var9 != null) {
            var9.setInAllowedEventHandler(false);
         }

         if (PulseLogger.PULSE_LOGGING_ENABLED) {
            PulseLogger.newInput(null);
         }
      }
   }

   @Override
   public double[] getInputMethodCandidatePos(int var1) {
      Point2D var2 = this.scene.inputMethodRequests.getTextLocation(var1);
      return new double[]{var2.getX(), var2.getY()};
   }

   private static TransferMode actionToTransferMode(int var0) {
      if (var0 == 0) {
         return null;
      } else if (var0 == 1 || var0 == 1073741825) {
         return TransferMode.COPY;
      } else if (var0 == 2 || var0 == 1073741826) {
         return TransferMode.MOVE;
      } else if (var0 == 1073741824) {
         return TransferMode.LINK;
      } else {
         if (var0 == 3) {
            if (QuantumToolkit.verbose) {
               System.err.println("Ambiguous drop action: " + Integer.toHexString(var0));
            }
         } else if (QuantumToolkit.verbose) {
            System.err.println("Unknown drop action: " + Integer.toHexString(var0));
         }

         return null;
      }
   }

   private static int transferModeToAction(TransferMode var0) {
      if (var0 == null) {
         return 0;
      } else {
         switch (var0) {
            case COPY:
               return 1;
            case MOVE:
               return 2;
            case LINK:
               return 1073741824;
            default:
               return 0;
         }
      }
   }

   @Override
   public int handleDragEnter(View var1, int var2, int var3, int var4, int var5, int var6, ClipboardAssistance var7) {
      if (PulseLogger.PULSE_LOGGING_ENABLED) {
         PulseLogger.newInput("DRAG_ENTER");
      }

      TransferMode var8;
      try {
         var8 = QuantumToolkit.runWithoutRenderLock(() -> this.dndHandler.handleDragEnter(var2, var3, var4, var5, actionToTransferMode(var6), var7));
      } finally {
         if (PulseLogger.PULSE_LOGGING_ENABLED) {
            PulseLogger.newInput(null);
         }
      }

      return transferModeToAction(var8);
   }

   @Override
   public void handleDragLeave(View var1, ClipboardAssistance var2) {
      if (PulseLogger.PULSE_LOGGING_ENABLED) {
         PulseLogger.newInput("DRAG_LEAVE");
      }

      try {
         QuantumToolkit.runWithoutRenderLock(() -> {
            this.dndHandler.handleDragLeave(var2);
            return null;
         });
      } finally {
         if (PulseLogger.PULSE_LOGGING_ENABLED) {
            PulseLogger.newInput(null);
         }
      }
   }

   @Override
   public int handleDragDrop(View var1, int var2, int var3, int var4, int var5, int var6, ClipboardAssistance var7) {
      if (PulseLogger.PULSE_LOGGING_ENABLED) {
         PulseLogger.newInput("DRAG_DROP");
      }

      TransferMode var8;
      try {
         var8 = QuantumToolkit.runWithoutRenderLock(() -> this.dndHandler.handleDragDrop(var2, var3, var4, var5, actionToTransferMode(var6), var7));
      } finally {
         if (PulseLogger.PULSE_LOGGING_ENABLED) {
            PulseLogger.newInput(null);
         }
      }

      return transferModeToAction(var8);
   }

   @Override
   public int handleDragOver(View var1, int var2, int var3, int var4, int var5, int var6, ClipboardAssistance var7) {
      if (PulseLogger.PULSE_LOGGING_ENABLED) {
         PulseLogger.newInput("DRAG_OVER");
      }

      TransferMode var8;
      try {
         var8 = QuantumToolkit.runWithoutRenderLock(() -> this.dndHandler.handleDragOver(var2, var3, var4, var5, actionToTransferMode(var6), var7));
      } finally {
         if (PulseLogger.PULSE_LOGGING_ENABLED) {
            PulseLogger.newInput(null);
         }
      }

      return transferModeToAction(var8);
   }

   @Override
   public void handleDragStart(View var1, int var2, int var3, int var4, int var5, int var6, ClipboardAssistance var7) {
      if (PulseLogger.PULSE_LOGGING_ENABLED) {
         PulseLogger.newInput("DRAG_START");
      }

      this.dropSourceAssistant = var7;

      try {
         QuantumToolkit.runWithoutRenderLock(() -> {
            this.dndHandler.handleDragStart(var2, var3, var4, var5, var6, var7);
            return null;
         });
      } finally {
         if (PulseLogger.PULSE_LOGGING_ENABLED) {
            PulseLogger.newInput(null);
         }
      }
   }

   @Override
   public void handleDragEnd(View var1, int var2) {
      if (PulseLogger.PULSE_LOGGING_ENABLED) {
         PulseLogger.newInput("DRAG_END");
      }

      try {
         QuantumToolkit.runWithoutRenderLock(() -> {
            this.dndHandler.handleDragEnd(actionToTransferMode(var2), this.dropSourceAssistant);
            return null;
         });
      } finally {
         if (PulseLogger.PULSE_LOGGING_ENABLED) {
            PulseLogger.newInput(null);
         }
      }
   }

   @Override
   public void handleViewEvent(View var1, long var2, int var4) {
      if (PulseLogger.PULSE_LOGGING_ENABLED) {
         PulseLogger.newInput("VIEW_EVENT: " + ViewEvent.getTypeString(var4));
      }

      this.viewNotification.view = var1;
      this.viewNotification.time = var2;
      this.viewNotification.type = var4;

      try {
         QuantumToolkit.runWithoutRenderLock(() -> AccessController.doPrivileged(this.viewNotification, this.scene.getAccessControlContext()));
      } finally {
         if (PulseLogger.PULSE_LOGGING_ENABLED) {
            PulseLogger.newInput(null);
         }
      }
   }

   @Override
   public void handleScrollGestureEvent(
      View var1,
      long var2,
      int var4,
      int var5,
      boolean var6,
      boolean var7,
      int var8,
      int var9,
      int var10,
      int var11,
      int var12,
      double var13,
      double var15,
      double var17,
      double var19,
      double var21,
      double var23
   ) {
      if (PulseLogger.PULSE_LOGGING_ENABLED) {
         PulseLogger.newInput("SCROLL_GESTURE_EVENT");
      }

      WindowStage var25 = this.scene.getWindowStage();

      try {
         if (var25 != null) {
            var25.setInAllowedEventHandler(false);
         }

         QuantumToolkit.runWithoutRenderLock(
            () -> AccessController.doPrivileged(
                  (PrivilegedAction<Void>)(() -> {
                     if (this.scene.sceneListener != null) {
                        EventType var23x;
                        switch (var4) {
                           case 1:
                              var23x = ScrollEvent.SCROLL_STARTED;
                              break;
                           case 2:
                              var23x = ScrollEvent.SCROLL;
                              break;
                           case 3:
                              var23x = ScrollEvent.SCROLL_FINISHED;
                              break;
                           default:
                              throw new RuntimeException("Unknown scroll event type: " + var4);
                        }

                        Window var24 = var1.getWindow();
                        double var25x;
                        double var27;
                        double var29;
                        double var31;
                        double var33;
                        double var35;
                        if (var24 != null) {
                           var25x = (double)var24.getPlatformScaleX();
                           var27 = (double)var24.getPlatformScaleY();
                           Screen var37 = var24.getScreen();
                           if (var37 != null) {
                              var29 = (double)var37.getPlatformX();
                              var31 = (double)var37.getPlatformY();
                              var33 = (double)var37.getX();
                              var35 = (double)var37.getY();
                           } else {
                              var35 = 0.0;
                              var33 = 0.0;
                              var31 = 0.0;
                              var29 = 0.0;
                           }
                        } else {
                           var27 = 1.0;
                           var25x = 1.0;
                           var35 = 0.0;
                           var33 = 0.0;
                           var31 = 0.0;
                           var29 = 0.0;
                        }

                        this.scene
                           .sceneListener
                           .scrollEvent(
                              var23x,
                              var13 / var25x,
                              var15 / var27,
                              var17 / var25x,
                              var19 / var27,
                              var21,
                              var23,
                              var8,
                              0,
                              0,
                              0,
                              0,
                              var9 == Integer.MAX_VALUE ? Double.NaN : (double)var9 / var25x,
                              var10 == Integer.MAX_VALUE ? Double.NaN : (double)var10 / var27,
                              var11 == Integer.MAX_VALUE ? Double.NaN : var33 + ((double)var11 - var29) / var25x,
                              var12 == Integer.MAX_VALUE ? Double.NaN : var35 + ((double)var12 - var31) / var27,
                              (var5 & 1) != 0,
                              (var5 & 4) != 0,
                              (var5 & 8) != 0,
                              (var5 & 16) != 0,
                              var6,
                              var7
                           );
                     }

                     return null;
                  }),
                  this.scene.getAccessControlContext()
               )
         );
      } finally {
         if (var25 != null) {
            var25.setInAllowedEventHandler(false);
         }

         if (PulseLogger.PULSE_LOGGING_ENABLED) {
            PulseLogger.newInput(null);
         }
      }
   }

   @Override
   public void handleZoomGestureEvent(
      View var1,
      long var2,
      int var4,
      int var5,
      boolean var6,
      boolean var7,
      int var8,
      int var9,
      int var10,
      int var11,
      double var12,
      double var14,
      double var16,
      double var18
   ) {
      if (PulseLogger.PULSE_LOGGING_ENABLED) {
         PulseLogger.newInput("ZOOM_GESTURE_EVENT");
      }

      WindowStage var20 = this.scene.getWindowStage();

      try {
         if (var20 != null) {
            var20.setInAllowedEventHandler(false);
         }

         QuantumToolkit.runWithoutRenderLock(
            () -> AccessController.doPrivileged(
                  (PrivilegedAction<Void>)(() -> {
                     if (this.scene.sceneListener != null) {
                        EventType var14x;
                        switch (var4) {
                           case 1:
                              var14x = ZoomEvent.ZOOM_STARTED;
                              break;
                           case 2:
                              var14x = ZoomEvent.ZOOM;
                              break;
                           case 3:
                              var14x = ZoomEvent.ZOOM_FINISHED;
                              break;
                           default:
                              throw new RuntimeException("Unknown scroll event type: " + var4);
                        }

                        Window var15 = var1.getWindow();
                        double var16x;
                        double var18x;
                        double var20x;
                        double var22;
                        double var24;
                        double var26;
                        if (var15 != null) {
                           var16x = (double)var15.getPlatformScaleX();
                           var18x = (double)var15.getPlatformScaleY();
                           Screen var28 = var15.getScreen();
                           if (var28 != null) {
                              var20x = (double)var28.getPlatformX();
                              var22 = (double)var28.getPlatformY();
                              var24 = (double)var28.getX();
                              var26 = (double)var28.getY();
                           } else {
                              var26 = 0.0;
                              var24 = 0.0;
                              var22 = 0.0;
                              var20x = 0.0;
                           }
                        } else {
                           var18x = 1.0;
                           var16x = 1.0;
                           var26 = 0.0;
                           var24 = 0.0;
                           var22 = 0.0;
                           var20x = 0.0;
                        }

                        this.scene
                           .sceneListener
                           .zoomEvent(
                              var14x,
                              var12,
                              var16,
                              var8 == Integer.MAX_VALUE ? Double.NaN : (double)var8 / var16x,
                              var9 == Integer.MAX_VALUE ? Double.NaN : (double)var9 / var18x,
                              var10 == Integer.MAX_VALUE ? Double.NaN : var24 + ((double)var10 - var20x) / var16x,
                              var11 == Integer.MAX_VALUE ? Double.NaN : var26 + ((double)var11 - var22) / var18x,
                              (var5 & 1) != 0,
                              (var5 & 4) != 0,
                              (var5 & 8) != 0,
                              (var5 & 16) != 0,
                              var6,
                              var7
                           );
                     }

                     return null;
                  }),
                  this.scene.getAccessControlContext()
               )
         );
      } finally {
         if (var20 != null) {
            var20.setInAllowedEventHandler(false);
         }

         if (PulseLogger.PULSE_LOGGING_ENABLED) {
            PulseLogger.newInput(null);
         }
      }
   }

   @Override
   public void handleRotateGestureEvent(
      View var1, long var2, int var4, int var5, boolean var6, boolean var7, int var8, int var9, int var10, int var11, double var12, double var14
   ) {
      if (PulseLogger.PULSE_LOGGING_ENABLED) {
         PulseLogger.newInput("ROTATE_GESTURE_EVENT");
      }

      WindowStage var16 = this.scene.getWindowStage();

      try {
         if (var16 != null) {
            var16.setInAllowedEventHandler(false);
         }

         QuantumToolkit.runWithoutRenderLock(
            () -> AccessController.doPrivileged(
                  (PrivilegedAction<Void>)(() -> {
                     if (this.scene.sceneListener != null) {
                        EventType var14x;
                        switch (var4) {
                           case 1:
                              var14x = RotateEvent.ROTATION_STARTED;
                              break;
                           case 2:
                              var14x = RotateEvent.ROTATE;
                              break;
                           case 3:
                              var14x = RotateEvent.ROTATION_FINISHED;
                              break;
                           default:
                              throw new RuntimeException("Unknown scroll event type: " + var4);
                        }

                        Window var15 = var1.getWindow();
                        double var16x;
                        double var18;
                        double var20;
                        double var22;
                        double var24;
                        double var26;
                        if (var15 != null) {
                           var16x = (double)var15.getPlatformScaleX();
                           var18 = (double)var15.getPlatformScaleY();
                           Screen var28 = var15.getScreen();
                           if (var28 != null) {
                              var20 = (double)var28.getPlatformX();
                              var22 = (double)var28.getPlatformY();
                              var24 = (double)var28.getX();
                              var26 = (double)var28.getY();
                           } else {
                              var26 = 0.0;
                              var24 = 0.0;
                              var22 = 0.0;
                              var20 = 0.0;
                           }
                        } else {
                           var18 = 1.0;
                           var16x = 1.0;
                           var26 = 0.0;
                           var24 = 0.0;
                           var22 = 0.0;
                           var20 = 0.0;
                        }

                        this.scene
                           .sceneListener
                           .rotateEvent(
                              var14x,
                              var12,
                              var14,
                              var8 == Integer.MAX_VALUE ? Double.NaN : (double)var8 / var16x,
                              var9 == Integer.MAX_VALUE ? Double.NaN : (double)var9 / var18,
                              var10 == Integer.MAX_VALUE ? Double.NaN : var24 + ((double)var10 - var20) / var16x,
                              var11 == Integer.MAX_VALUE ? Double.NaN : var26 + ((double)var11 - var22) / var18,
                              (var5 & 1) != 0,
                              (var5 & 4) != 0,
                              (var5 & 8) != 0,
                              (var5 & 16) != 0,
                              var6,
                              var7
                           );
                     }

                     return null;
                  }),
                  this.scene.getAccessControlContext()
               )
         );
      } finally {
         if (var16 != null) {
            var16.setInAllowedEventHandler(false);
         }

         if (PulseLogger.PULSE_LOGGING_ENABLED) {
            PulseLogger.newInput(null);
         }
      }
   }

   @Override
   public void handleSwipeGestureEvent(
      View var1, long var2, int var4, int var5, boolean var6, boolean var7, int var8, int var9, int var10, int var11, int var12, int var13
   ) {
      if (PulseLogger.PULSE_LOGGING_ENABLED) {
         PulseLogger.newInput("SWIPE_EVENT");
      }

      WindowStage var14 = this.scene.getWindowStage();

      try {
         if (var14 != null) {
            var14.setInAllowedEventHandler(false);
         }

         QuantumToolkit.runWithoutRenderLock(
            () -> AccessController.doPrivileged(
                  (PrivilegedAction<Void>)(() -> {
                     if (this.scene.sceneListener != null) {
                        EventType var10x;
                        switch (var9) {
                           case 1:
                              var10x = SwipeEvent.SWIPE_UP;
                              break;
                           case 2:
                              var10x = SwipeEvent.SWIPE_DOWN;
                              break;
                           case 3:
                              var10x = SwipeEvent.SWIPE_LEFT;
                              break;
                           case 4:
                              var10x = SwipeEvent.SWIPE_RIGHT;
                              break;
                           default:
                              throw new RuntimeException("Unknown swipe event direction: " + var9);
                        }

                        Window var11x = var1.getWindow();
                        double var12x;
                        double var14x;
                        double var16;
                        double var18;
                        double var20;
                        double var22;
                        if (var11x != null) {
                           var12x = (double)var11x.getPlatformScaleX();
                           var14x = (double)var11x.getPlatformScaleY();
                           Screen var24 = var11x.getScreen();
                           if (var24 != null) {
                              var16 = (double)var24.getPlatformX();
                              var18 = (double)var24.getPlatformY();
                              var20 = (double)var24.getX();
                              var22 = (double)var24.getY();
                           } else {
                              var22 = 0.0;
                              var20 = 0.0;
                              var18 = 0.0;
                              var16 = 0.0;
                           }
                        } else {
                           var14x = 1.0;
                           var12x = 1.0;
                           var22 = 0.0;
                           var20 = 0.0;
                           var18 = 0.0;
                           var16 = 0.0;
                        }

                        this.scene
                           .sceneListener
                           .swipeEvent(
                              var10x,
                              var8,
                              var10 == Integer.MAX_VALUE ? Double.NaN : (double)var10 / var12x,
                              var11 == Integer.MAX_VALUE ? Double.NaN : (double)var11 / var14x,
                              var12 == Integer.MAX_VALUE ? Double.NaN : var20 + ((double)var12 - var16) / var12x,
                              var13 == Integer.MAX_VALUE ? Double.NaN : var22 + ((double)var13 - var18) / var14x,
                              (var5 & 1) != 0,
                              (var5 & 4) != 0,
                              (var5 & 8) != 0,
                              (var5 & 16) != 0,
                              var6
                           );
                     }

                     return null;
                  }),
                  this.scene.getAccessControlContext()
               )
         );
      } finally {
         if (var14 != null) {
            var14.setInAllowedEventHandler(false);
         }

         if (PulseLogger.PULSE_LOGGING_ENABLED) {
            PulseLogger.newInput(null);
         }
      }
   }

   @Override
   public void handleBeginTouchEvent(View var1, long var2, int var4, boolean var5, int var6) {
      if (PulseLogger.PULSE_LOGGING_ENABLED) {
         PulseLogger.newInput("BEGIN_TOUCH_EVENT");
      }

      WindowStage var7 = this.scene.getWindowStage();

      try {
         if (var7 != null) {
            var7.setInAllowedEventHandler(true);
         }

         QuantumToolkit.runWithoutRenderLock(() -> AccessController.doPrivileged((PrivilegedAction<Void>)(() -> {
               if (this.scene.sceneListener != null) {
                  this.scene.sceneListener.touchEventBegin(var2, var6, var5, (var4 & 1) != 0, (var4 & 4) != 0, (var4 & 8) != 0, (var4 & 16) != 0);
               }

               return null;
            }), this.scene.getAccessControlContext()));
      } finally {
         if (var7 != null) {
            var7.setInAllowedEventHandler(false);
         }

         if (PulseLogger.PULSE_LOGGING_ENABLED) {
            PulseLogger.newInput(null);
         }
      }

      this.gestures.notifyBeginTouchEvent(var2, var4, var5, var6);
   }

   @Override
   public void handleNextTouchEvent(View var1, long var2, int var4, long var5, int var7, int var8, int var9, int var10) {
      if (PulseLogger.PULSE_LOGGING_ENABLED) {
         PulseLogger.newInput("NEXT_TOUCH_EVENT");
      }

      WindowStage var11 = this.scene.getWindowStage();

      try {
         if (var11 != null) {
            var11.setInAllowedEventHandler(true);
         }

         QuantumToolkit.runWithoutRenderLock(
            () -> AccessController.doPrivileged(
                  (PrivilegedAction<Void>)(() -> {
                     if (this.scene.sceneListener != null) {
                        TouchPoint.State var9x;
                        switch (var4) {
                           case 811:
                              var9x = TouchPoint.State.PRESSED;
                              break;
                           case 812:
                              var9x = TouchPoint.State.MOVED;
                              break;
                           case 813:
                              var9x = TouchPoint.State.RELEASED;
                              break;
                           case 814:
                              var9x = TouchPoint.State.STATIONARY;
                              break;
                           default:
                              throw new RuntimeException("Unknown touch state: " + var4);
                        }

                        Window var10x = var1.getWindow();
                        double var11x;
                        double var13;
                        double var15;
                        double var17;
                        double var19;
                        double var21;
                        if (var10x != null) {
                           var11x = (double)var10x.getPlatformScaleX();
                           var13 = (double)var10x.getPlatformScaleY();
                           Screen var23 = var10x.getScreen();
                           if (var23 != null) {
                              var15 = (double)var23.getPlatformX();
                              var17 = (double)var23.getPlatformY();
                              var19 = (double)var23.getX();
                              var21 = (double)var23.getY();
                           } else {
                              var21 = 0.0;
                              var19 = 0.0;
                              var17 = 0.0;
                              var15 = 0.0;
                           }
                        } else {
                           var13 = 1.0;
                           var11x = 1.0;
                           var21 = 0.0;
                           var19 = 0.0;
                           var17 = 0.0;
                           var15 = 0.0;
                        }

                        this.scene
                           .sceneListener
                           .touchEventNext(
                              var9x,
                              var5,
                              (double)var7 / var11x,
                              (double)var8 / var13,
                              var19 + ((double)var9 - var15) / var11x,
                              var21 + ((double)var10 - var17) / var13
                           );
                     }

                     return null;
                  }),
                  this.scene.getAccessControlContext()
               )
         );
      } finally {
         if (var11 != null) {
            var11.setInAllowedEventHandler(false);
         }

         if (PulseLogger.PULSE_LOGGING_ENABLED) {
            PulseLogger.newInput(null);
         }
      }

      this.gestures.notifyNextTouchEvent(var2, var4, var5, var7, var8, var9, var10);
   }

   @Override
   public void handleEndTouchEvent(View var1, long var2) {
      if (PulseLogger.PULSE_LOGGING_ENABLED) {
         PulseLogger.newInput("END_TOUCH_EVENT");
      }

      WindowStage var4 = this.scene.getWindowStage();

      try {
         if (var4 != null) {
            var4.setInAllowedEventHandler(true);
         }

         QuantumToolkit.runWithoutRenderLock(() -> AccessController.doPrivileged((PrivilegedAction<Void>)(() -> {
               if (this.scene.sceneListener != null) {
                  this.scene.sceneListener.touchEventEnd();
               }

               return null;
            }), this.scene.getAccessControlContext()));
      } finally {
         if (var4 != null) {
            var4.setInAllowedEventHandler(false);
         }

         if (PulseLogger.PULSE_LOGGING_ENABLED) {
            PulseLogger.newInput(null);
         }
      }

      this.gestures.notifyEndTouchEvent(var2);
   }

   @Override
   public Accessible getSceneAccessible() {
      return this.scene != null && this.scene.sceneListener != null ? this.scene.sceneListener.getSceneAccessible() : null;
   }

   static {
      Void var0 = AccessController.doPrivileged((PrivilegedAction<Void>)(() -> {
         zoomGestureEnabled = Boolean.valueOf(System.getProperty("com.sun.javafx.gestures.zoom", "false"));
         rotateGestureEnabled = Boolean.valueOf(System.getProperty("com.sun.javafx.gestures.rotate", "false"));
         scrollGestureEnabled = Boolean.valueOf(System.getProperty("com.sun.javafx.gestures.scroll", "false"));
         return null;
      }));
   }

   private class KeyEventNotification implements PrivilegedAction<Void> {
      View view;
      long time;
      int type;
      int key;
      char[] chars;
      int modifiers;
      private KeyCode lastKeyCode;

      public Void run() {
         if (PulseLogger.PULSE_LOGGING_ENABLED) {
            PulseLogger.newInput(GlassViewEventHandler.keyEventType(this.type).toString());
         }

         WindowStage var1 = GlassViewEventHandler.this.scene.getWindowStage();

         try {
            boolean var2 = (this.modifiers & 1) != 0;
            boolean var3 = (this.modifiers & 4) != 0;
            boolean var4 = (this.modifiers & 8) != 0;
            boolean var5 = (this.modifiers & 16) != 0;
            String var6 = new String(this.chars);
            KeyEvent var8 = new KeyEvent(GlassViewEventHandler.keyEventType(this.type), var6, var6, KeyCodeMap.valueOf(this.key), var2, var3, var4, var5);
            KeyCode var9 = KeyCodeMap.valueOf(this.key);
            switch (this.type) {
               case 111:
               case 112:
                  this.lastKeyCode = var9;
                  break;
               case 113:
                  var9 = this.lastKeyCode;
            }

            if (var1 != null) {
               if (var9 == KeyCode.ESCAPE) {
                  var1.setInAllowedEventHandler(false);
               } else {
                  var1.setInAllowedEventHandler(true);
               }
            }

            switch (this.type) {
               case 111:
                  if (this.view.isInFullscreen() && var1 != null && var1.getSavedFullScreenExitKey() != null && var1.getSavedFullScreenExitKey().match(var8)) {
                     var1.exitFullScreen();
                  }
               case 112:
               case 113:
                  if ((!this.view.isInFullscreen() || GlassViewEventHandler.this.checkFullScreenKeyEvent(this.type, this.key, this.chars, this.modifiers))
                     && GlassViewEventHandler.this.scene.sceneListener != null) {
                     GlassViewEventHandler.this.scene.sceneListener.keyEvent(var8);
                  }
                  break;
               default:
                  if (QuantumToolkit.verbose) {
                     System.out.println("handleKeyEvent: unhandled type: " + this.type);
                  }
            }
         } finally {
            if (var1 != null) {
               var1.setInAllowedEventHandler(false);
            }

            if (PulseLogger.PULSE_LOGGING_ENABLED) {
               PulseLogger.newInput(null);
            }
         }

         return null;
      }
   }

   private class MouseEventNotification implements PrivilegedAction<Void> {
      View view;
      long time;
      int type;
      int button;
      int x;
      int y;
      int xAbs;
      int yAbs;
      int modifiers;
      boolean isPopupTrigger;
      boolean isSynthesized;

      public Void run() {
         if (PulseLogger.PULSE_LOGGING_ENABLED) {
            PulseLogger.newInput(GlassViewEventHandler.mouseEventType(this.type).toString());
         }

         short var1;
         switch (this.button) {
            case 212:
               var1 = 32;
               break;
            case 213:
               var1 = 64;
               break;
            case 214:
               var1 = 128;
               break;
            case 215:
               var1 = 256;
               break;
            case 216:
               var1 = 512;
               break;
            default:
               var1 = 0;
         }

         switch (this.type) {
            case 221:
               GlassViewEventHandler.this.mouseButtonPressedMask |= var1;
               break;
            case 222:
               if ((GlassViewEventHandler.this.mouseButtonPressedMask & var1) == 0) {
                  return null;
               }

               GlassViewEventHandler.this.mouseButtonPressedMask &= ~var1;
               break;
            case 223:
            default:
               if (QuantumToolkit.verbose) {
                  System.out.println("handleMouseEvent: unhandled type: " + this.type);
               }
               break;
            case 224:
               if (this.button != 211) {
                  return null;
               }
            case 225:
            case 226:
               break;
            case 227:
               return null;
         }

         WindowStage var2 = GlassViewEventHandler.this.scene.getWindowStage();

         try {
            if (var2 != null) {
               switch (this.type) {
                  case 221:
                  case 222:
                     var2.setInAllowedEventHandler(true);
                     break;
                  default:
                     var2.setInAllowedEventHandler(false);
               }
            }

            if (GlassViewEventHandler.this.scene.sceneListener != null) {
               boolean var3 = (this.modifiers & 1) != 0;
               boolean var4 = (this.modifiers & 4) != 0;
               boolean var5 = (this.modifiers & 8) != 0;
               boolean var6 = (this.modifiers & 16) != 0;
               boolean var7 = (this.modifiers & 32) != 0;
               boolean var8 = (this.modifiers & 128) != 0;
               boolean var9 = (this.modifiers & 64) != 0;
               boolean var10 = (this.modifiers & 256) != 0;
               boolean var11 = (this.modifiers & 512) != 0;
               Window var12 = this.view.getWindow();
               double var13;
               double var15;
               double var17;
               double var19;
               double var21;
               double var23;
               if (var12 != null) {
                  var13 = (double)var12.getPlatformScaleX();
                  var15 = (double)var12.getPlatformScaleY();
                  Screen var25 = var12.getScreen();
                  if (var25 != null) {
                     var17 = (double)var25.getPlatformX();
                     var19 = (double)var25.getPlatformY();
                     var21 = (double)var25.getX();
                     var23 = (double)var25.getY();
                  } else {
                     var23 = 0.0;
                     var21 = 0.0;
                     var19 = 0.0;
                     var17 = 0.0;
                  }
               } else {
                  var15 = 1.0;
                  var13 = 1.0;
                  var23 = 0.0;
                  var21 = 0.0;
                  var19 = 0.0;
                  var17 = 0.0;
               }

               GlassViewEventHandler.this.scene
                  .sceneListener
                  .mouseEvent(
                     GlassViewEventHandler.mouseEventType(this.type),
                     (double)this.x / var13,
                     (double)this.y / var15,
                     var21 + ((double)this.xAbs - var17) / var13,
                     var23 + ((double)this.yAbs - var19) / var15,
                     GlassViewEventHandler.mouseEventButton(this.button),
                     this.isPopupTrigger,
                     this.isSynthesized,
                     var3,
                     var4,
                     var5,
                     var6,
                     var7,
                     var8,
                     var9,
                     var10,
                     var11
                  );
            }
         } finally {
            if (var2 != null) {
               var2.setInAllowedEventHandler(false);
            }

            if (PulseLogger.PULSE_LOGGING_ENABLED) {
               PulseLogger.newInput(null);
            }
         }

         return null;
      }
   }

   private class ViewEventNotification implements PrivilegedAction<Void> {
      View view;
      long time;
      int type;

      public Void run() {
         if (GlassViewEventHandler.this.scene.sceneListener == null) {
            return null;
         } else {
            switch (this.type) {
               case 411:
               case 412:
                  break;
               case 413:
               case 414:
               case 415:
               case 416:
               case 417:
               case 418:
               case 419:
               case 420:
               case 424:
               case 425:
               case 426:
               case 427:
               case 428:
               case 429:
               case 430:
               default:
                  throw new RuntimeException("handleViewEvent: unhandled type: " + this.type);
               case 421:
                  Window var6 = this.view.getWindow();
                  if (var6 == null || var6.getMinimumWidth() != this.view.getWidth() || var6.isVisible()) {
                     if (QuantumToolkit.drawInPaint && var6 != null && var6.isVisible()) {
                        WindowStage var8 = GlassViewEventHandler.this.scene.getWindowStage();
                        if (var8 != null && !var8.isApplet()) {
                           GlassViewEventHandler.this.collector.liveRepaintRenderJob(GlassViewEventHandler.this.scene);
                        }
                     }

                     GlassViewEventHandler.this.scene.entireSceneNeedsRepaint();
                  }
                  break;
               case 422:
                  Window var5 = this.view.getWindow();
                  float var7 = var5 == null ? 1.0F : var5.getPlatformScaleX();
                  float var9 = var5 == null ? 1.0F : var5.getPlatformScaleY();
                  GlassViewEventHandler.this.scene.sceneListener.changedSize((float)this.view.getWidth() / var7, (float)this.view.getHeight() / var9);
                  GlassViewEventHandler.this.scene.entireSceneNeedsRepaint();
                  QuantumToolkit.runWithRenderLock(() -> {
                     GlassViewEventHandler.this.scene.updateSceneState();
                     return null;
                  });
                  if (QuantumToolkit.liveResize && var5 != null && var5.isVisible()) {
                     WindowStage var4 = GlassViewEventHandler.this.scene.getWindowStage();
                     if (var4 != null && !var4.isApplet()) {
                        GlassViewEventHandler.this.collector.liveRepaintRenderJob(GlassViewEventHandler.this.scene);
                     }
                  }
                  break;
               case 423:
                  Window var1 = this.view.getWindow();
                  float var2 = var1 == null ? 1.0F : var1.getPlatformScaleX();
                  float var3 = var1 == null ? 1.0F : var1.getPlatformScaleY();
                  GlassViewEventHandler.this.scene.sceneListener.changedLocation((float)this.view.getX() / var2, (float)this.view.getY() / var3);
                  break;
               case 431:
               case 432:
                  if (GlassViewEventHandler.this.scene.getWindowStage() != null) {
                     GlassViewEventHandler.this.scene.getWindowStage().fullscreenChanged(this.type == 431);
                  }
            }

            return null;
         }
      }
   }
}
