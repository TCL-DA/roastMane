package com.sun.javafx.scene.control.skin;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.Animation.Status;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.SkinBase;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.skin.TextAreaSkin;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Window;
import javafx.util.Duration;

public class FXVKSkin extends SkinBase<FXVK> {
   private static final int GAP = 6;
   private List<List<FXVKSkin.Key>> currentBoard;
   private static HashMap<String, List<List<FXVKSkin.Key>>> boardMap = new HashMap<>();
   private int numCols;
   private boolean capsDown = false;
   private boolean shiftDown = false;
   private boolean isSymbol = false;
   long lastTime = -1L;
   private static Popup vkPopup;
   private static Popup secondaryPopup;
   private static FXVK primaryVK;
   private static Timeline slideInTimeline = new Timeline();
   private static Timeline slideOutTimeline = new Timeline();
   private static boolean hideAfterSlideOut = false;
   private static FXVK secondaryVK;
   private static Timeline secondaryVKDelay;
   private static FXVKSkin.CharKey secondaryVKKey;
   private static FXVKSkin.TextInputKey repeatKey;
   private static Timeline repeatInitialDelay;
   private static Timeline repeatSubsequentDelay;
   private static double KEY_REPEAT_DELAY = 400.0;
   private static double KEY_REPEAT_DELAY_MIN = 100.0;
   private static double KEY_REPEAT_DELAY_MAX = 1000.0;
   private static double KEY_REPEAT_RATE = 25.0;
   private static double KEY_REPEAT_RATE_MIN = 2.0;
   private static double KEY_REPEAT_RATE_MAX = 50.0;
   private Node attachedNode;
   private String vkType = null;
   FXVK fxvk;
   static final double VK_HEIGHT = 243.0;
   static final double VK_SLIDE_MILLIS = 250.0;
   static final double PREF_PORTRAIT_KEY_WIDTH = 40.0;
   static final double PREF_KEY_HEIGHT = 56.0;
   static boolean vkAdjustWindow = false;
   static boolean vkLookup = false;
   private static DoubleProperty winY = new SimpleDoubleProperty();
   EventHandler<InputEvent> unHideEventHandler;
   private boolean isVKHidden = false;
   private Double origWindowYPos = null;

   void clearShift() {
      if (this.shiftDown && !this.capsDown) {
         this.shiftDown = false;
         this.updateKeys();
      }

      this.lastTime = -1L;
   }

   void pressShift() {
      long var1 = System.currentTimeMillis();
      if (this.shiftDown && !this.capsDown) {
         if (this.lastTime > 0L && var1 - this.lastTime < 400L) {
            this.shiftDown = false;
            this.capsDown = true;
         } else {
            this.shiftDown = false;
            this.capsDown = false;
         }
      } else if (!this.shiftDown && !this.capsDown) {
         this.shiftDown = true;
      } else {
         this.shiftDown = false;
         this.capsDown = false;
      }

      this.updateKeys();
      this.lastTime = var1;
   }

   void clearSymbolABC() {
      this.isSymbol = false;
      this.updateKeys();
   }

   void pressSymbolABC() {
      this.isSymbol = !this.isSymbol;
      this.updateKeys();
   }

   void clearStateKeys() {
      this.capsDown = false;
      this.shiftDown = false;
      this.isSymbol = false;
      this.lastTime = -1L;
      this.updateKeys();
   }

   private void updateKeys() {
      for (List var2 : this.currentBoard) {
         for (FXVKSkin.Key var4 : var2) {
            var4.update(this.capsDown, this.shiftDown, this.isSymbol);
         }
      }
   }

   private static void startSlideIn() {
      slideOutTimeline.stop();
      slideInTimeline.playFromStart();
   }

   private static void startSlideOut(boolean var0) {
      hideAfterSlideOut = var0;
      slideInTimeline.stop();
      slideOutTimeline.playFromStart();
   }

   private void adjustWindowPosition(Node var1) {
      if (var1 instanceof TextInputControl) {
         double var2 = var1.localToScene(0.0, 0.0).getY() + var1.getScene().getY();
         double var4 = ((TextInputControl)var1).getHeight();
         double var6 = var2 + var4;
         double var8 = com.sun.javafx.util.Utils.getScreen(var1).getBounds().getHeight();
         double var10 = var8 - 243.0;
         double var12 = 0.0;
         double var14 = 0.0;
         double var16 = 0.0;
         double var18 = 10.0;
         if (var1 instanceof TextField) {
            var12 = var2 + var4 / 2.0;
            var14 = var6;
            Parent var20 = this.attachedNode.getParent();
            if (var20 instanceof ComboBoxBase) {
               var16 = Math.min(var18 - var2, 0.0);
            } else {
               var16 = Math.min(var10 / 2.0 - var12, 0.0);
            }
         } else if (var1 instanceof TextArea) {
            TextAreaSkin var32 = (TextAreaSkin)((TextArea)var1).getSkin();
            Bounds var21 = var32.getCaretBounds();
            double var22 = var21.getMinY();
            double var24 = var21.getMaxY();
            var12 = var2 + (var22 + var24) / 2.0;
            var14 = var2 + var24;
            if (var4 < var10) {
               var16 = var10 / 2.0 - (var2 + var4 / 2.0);
            } else {
               var16 = var10 / 2.0 - var12;
            }

            var16 = Math.min(var16, 0.0);
         } else {
            var12 = var2 + var4 / 2.0;
            var14 = var6;
            var16 = Math.min(var10 / 2.0 - var12, 0.0);
         }

         Window var33 = var1.getScene().getWindow();
         if (this.origWindowYPos + var14 > var10) {
            var33.setY(var16);
         } else {
            var33.setY(this.origWindowYPos);
         }
      }
   }

   private void saveWindowPosition(Node var1) {
      Window var2 = var1.getScene().getWindow();
      this.origWindowYPos = var2.getY();
   }

   private void restoreWindowPosition(Node var1) {
      if (var1 != null) {
         Scene var2 = var1.getScene();
         if (var2 != null) {
            Window var3 = var2.getWindow();
            if (var3 != null) {
               var3.setY(this.origWindowYPos);
            }
         }
      }
   }

   private void registerUnhideHandler(Node var1) {
      if (this.unHideEventHandler == null) {
         this.unHideEventHandler = var1x -> {
            if (this.attachedNode != null && this.isVKHidden) {
               double var2 = com.sun.javafx.util.Utils.getScreen(this.attachedNode).getBounds().getHeight();
               if (this.fxvk.getHeight() > 0.0 && vkPopup.getY() > var2 - this.fxvk.getHeight() && slideInTimeline.getStatus() != Status.RUNNING) {
                  startSlideIn();
                  if (vkAdjustWindow) {
                     this.adjustWindowPosition(this.attachedNode);
                  }
               }
            }

            this.isVKHidden = false;
         };
      }

      var1.addEventHandler(TouchEvent.TOUCH_PRESSED, this.unHideEventHandler);
      var1.addEventHandler(MouseEvent.MOUSE_PRESSED, this.unHideEventHandler);
   }

   private void unRegisterUnhideHandler(Node var1) {
      if (this.unHideEventHandler != null) {
         var1.removeEventHandler(TouchEvent.TOUCH_PRESSED, this.unHideEventHandler);
         var1.removeEventHandler(MouseEvent.MOUSE_PRESSED, this.unHideEventHandler);
      }
   }

   private String getNodeVKType(Node var1) {
      Integer var2 = (Integer)var1.getProperties().get("vkType");
      String var3 = null;
      if (var2 != null) {
         String var4 = FXVK.VK_TYPE_NAMES[var2];
         if (var4 instanceof String) {
            var3 = var4.toLowerCase(Locale.ROOT);
         }
      }

      return var3 != null ? var3 : "text";
   }

   private void updateKeyboardType(Node var1) {
      String var2 = this.vkType;
      this.vkType = this.getNodeVKType(var1);
      if (var2 == null || !this.vkType.equals(var2)) {
         this.rebuildPrimaryVK(this.vkType);
      }
   }

   private void closeSecondaryVK() {
      if (secondaryVK != null) {
         secondaryVK.setAttachedNode(null);
         secondaryPopup.hide();
      }
   }

   private void setupPrimaryVK() {
      this.fxvk.setFocusTraversable(false);
      this.fxvk.setVisible(true);
      if (vkPopup == null) {
         vkPopup = new Popup();
         vkPopup.setAutoFix(false);
      }

      vkPopup.getContent().setAll(new Node[]{this.fxvk});
      double var1 = com.sun.javafx.util.Utils.getScreen(this.fxvk).getBounds().getHeight();
      double var3 = com.sun.javafx.util.Utils.getScreen(this.fxvk).getBounds().getWidth();
      slideInTimeline.getKeyFrames()
         .setAll(new KeyFrame[]{new KeyFrame(Duration.millis(250.0), new KeyValue[]{new KeyValue(winY, var1 - 243.0, Interpolator.EASE_BOTH)})});
      slideOutTimeline.getKeyFrames().setAll(new KeyFrame[]{new KeyFrame(Duration.millis(250.0), var0 -> {
         if (hideAfterSlideOut && vkPopup.isShowing()) {
            vkPopup.hide();
         }
      }, new KeyValue[]{new KeyValue(winY, var1, Interpolator.EASE_BOTH)})});
      this.fxvk.setPrefWidth(var3);
      this.fxvk.setMinWidth(Double.NEGATIVE_INFINITY);
      this.fxvk.setMaxWidth(Double.NEGATIVE_INFINITY);
      this.fxvk.setPrefHeight(243.0);
      this.fxvk.setMinHeight(Double.NEGATIVE_INFINITY);
      if (secondaryVKDelay == null) {
         secondaryVKDelay = new Timeline();
      }

      KeyFrame var5 = new KeyFrame(Duration.millis(500.0), var1x -> {
         if (secondaryVKKey != null) {
            this.showSecondaryVK(secondaryVKKey);
         }
      }, new KeyValue[0]);
      secondaryVKDelay.getKeyFrames().setAll(new KeyFrame[]{var5});
      if (KEY_REPEAT_RATE > 0.0) {
         repeatInitialDelay = new Timeline(new KeyFrame[]{new KeyFrame(Duration.millis(KEY_REPEAT_DELAY), var0 -> {
            repeatKey.sendKeyEvents();
            repeatSubsequentDelay.playFromStart();
         }, new KeyValue[0])});
         repeatSubsequentDelay = new Timeline(
            new KeyFrame[]{new KeyFrame(Duration.millis(1000.0 / KEY_REPEAT_RATE), var0 -> repeatKey.sendKeyEvents(), new KeyValue[0])}
         );
         repeatSubsequentDelay.setCycleCount(-1);
      }
   }

   void prerender(Node var1) {
      if (this.fxvk == primaryVK) {
         this.loadBoard("text");
         this.loadBoard("numeric");
         this.loadBoard("url");
         this.loadBoard("email");
         this.updateKeyboardType(var1);
         this.fxvk.setVisible(true);
         if (!vkPopup.isShowing()) {
            Rectangle2D var2 = com.sun.javafx.util.Utils.getScreen(var1).getBounds();
            vkPopup.setX((var2.getWidth() - this.fxvk.prefWidth(-1.0)) / 2.0);
            winY.set(var2.getHeight());
            vkPopup.show(var1.getScene().getWindow());
         }
      }
   }

   public FXVKSkin(final FXVK var1) {
      super(var1);
      this.fxvk = var1;
      if (var1 == FXVK.vk) {
         primaryVK = var1;
      }

      if (var1 == primaryVK) {
         this.setupPrimaryVK();
      }

      var1.attachedNodeProperty()
         .addListener(
            new InvalidationListener() {
               public void invalidated(Observable var1x) {
                  Node var2 = FXVKSkin.this.attachedNode;
                  FXVKSkin.this.attachedNode = var1.getAttachedNode();
                  if (var1 == FXVKSkin.primaryVK) {
                     FXVKSkin.this.closeSecondaryVK();
                     if (FXVKSkin.this.attachedNode != null) {
                        if (var2 != null) {
                           FXVKSkin.this.unRegisterUnhideHandler(var2);
                        }

                        FXVKSkin.this.registerUnhideHandler(FXVKSkin.this.attachedNode);
                        FXVKSkin.this.updateKeyboardType(FXVKSkin.this.attachedNode);
                        if ((var2 == null || var2.getScene() == null || var2.getScene().getWindow() != FXVKSkin.this.attachedNode.getScene().getWindow())
                           && FXVKSkin.vkPopup.isShowing()) {
                           FXVKSkin.vkPopup.hide();
                        }

                        if (!FXVKSkin.vkPopup.isShowing()) {
                           Rectangle2D var3 = com.sun.javafx.util.Utils.getScreen(FXVKSkin.this.attachedNode).getBounds();
                           FXVKSkin.vkPopup.setX((var3.getWidth() - var1.prefWidth(-1.0)) / 2.0);
                           if (var2 != null && !FXVKSkin.this.isVKHidden) {
                              FXVKSkin.winY.set(var3.getHeight() - 243.0);
                           } else {
                              FXVKSkin.winY.set(var3.getHeight());
                           }

                           FXVKSkin.vkPopup.show(FXVKSkin.this.attachedNode.getScene().getWindow());
                        }

                        if (var2 == null || FXVKSkin.this.isVKHidden) {
                           FXVKSkin.startSlideIn();
                        }

                        if (FXVKSkin.vkAdjustWindow) {
                           if (var2 == null || var2.getScene() == null || var2.getScene().getWindow() != FXVKSkin.this.attachedNode.getScene().getWindow()) {
                              FXVKSkin.this.saveWindowPosition(FXVKSkin.this.attachedNode);
                           }

                           FXVKSkin.this.adjustWindowPosition(FXVKSkin.this.attachedNode);
                        }
                     } else {
                        if (var2 != null) {
                           FXVKSkin.this.unRegisterUnhideHandler(var2);
                        }

                        FXVKSkin.startSlideOut(true);
                        if (FXVKSkin.vkAdjustWindow) {
                           FXVKSkin.this.restoreWindowPosition(var2);
                        }
                     }

                     FXVKSkin.this.isVKHidden = false;
                  }
               }
            }
         );
   }

   private void rebuildSecondaryVK() {
      if (secondaryVK.chars != null) {
         int var1 = secondaryVK.chars.length;
         int var2 = (int)Math.floor(Math.sqrt((double)Math.max(1, var1 - 2)));
         int var3 = (int)Math.ceil((double)var1 / (double)var2);
         ArrayList var5 = new ArrayList(2);

         for (int var6 = 0; var6 < var2; var6++) {
            int var7 = var6 * var3;
            int var8 = Math.min(var7 + var3, var1);
            if (var7 >= var8) {
               break;
            }

            ArrayList var9 = new ArrayList(var3);

            for (int var10 = var7; var10 < var8; var10++) {
               FXVKSkin.CharKey var4 = new FXVKSkin.CharKey(secondaryVK.chars[var10], null, null);
               var4.col = (var10 - var7) * 2;
               var4.colSpan = 2;

               for (String var12 : var4.getStyleClass()) {
                  var4.text.getStyleClass().add(var12 + "-text");
                  var4.altText.getStyleClass().add(var12 + "-alttext");
                  var4.icon.getStyleClass().add(var12 + "-icon");
               }

               if (secondaryVK.chars[var10] != null && secondaryVK.chars[var10].length() > 1) {
                  var4.text.getStyleClass().add("multi-char-text");
               }

               var9.add(var4);
            }

            var5.add(var9);
         }

         this.currentBoard = var5;
         this.getChildren().clear();
         this.numCols = 0;

         for (List var14 : this.currentBoard) {
            for (FXVKSkin.Key var16 : var14) {
               this.numCols = Math.max(this.numCols, var16.col + var16.colSpan);
            }

            this.getChildren().addAll(var14);
         }
      }
   }

   private void rebuildPrimaryVK(String var1) {
      this.currentBoard = this.loadBoard(var1);
      this.clearStateKeys();
      this.getChildren().clear();
      this.numCols = 0;

      for (List var3 : this.currentBoard) {
         for (FXVKSkin.Key var5 : var3) {
            this.numCols = Math.max(this.numCols, var5.col + var5.colSpan);
         }

         this.getChildren().addAll(var3);
      }
   }

   @Override
   protected double computePrefWidth(double var1, double var3, double var5, double var7, double var9) {
      return var9 + (double)(56 * this.numCols) + var5;
   }

   @Override
   protected double computePrefHeight(double var1, double var3, double var5, double var7, double var9) {
      return var3 + 400.0 + var7;
   }

   @Override
   protected void layoutChildren(double var1, double var3, double var5, double var7) {
      int var9 = this.currentBoard.size();
      double var10 = (var5 - (double)((this.numCols - 1) * 6)) / (double)this.numCols;
      double var12 = (var7 - (double)((var9 - 1) * 6)) / (double)var9;
      double var14 = var3;

      for (List var17 : this.currentBoard) {
         for (FXVKSkin.Key var19 : var17) {
            double var20 = var1 + (double)var19.col * (var10 + 6.0);
            double var22 = (double)var19.colSpan * (var10 + 6.0) - 6.0;
            var19.resizeRelocate((double)((int)(var20 + 0.5)), (double)((int)(var14 + 0.5)), var22, var12);
         }

         var14 += var12 + 6.0;
      }
   }

   private void showSecondaryVK(FXVKSkin.CharKey var1) {
      if (var1 != null) {
         Node var2 = primaryVK.getAttachedNode();
         if (secondaryVK == null) {
            secondaryVK = new FXVK();
            secondaryVK.setSkin(new FXVKSkin(secondaryVK));
            secondaryVK.getStyleClass().setAll(new String[]{"fxvk-secondary"});
            secondaryPopup = new Popup();
            secondaryPopup.setAutoHide(true);
            secondaryPopup.getContent().add(secondaryVK);
         }

         secondaryVK.chars = null;
         ArrayList var3 = new ArrayList();
         if (!this.isSymbol && var1.letterChars != null && var1.letterChars.length() > 0) {
            if (!this.shiftDown && !this.capsDown) {
               var3.add(var1.letterChars);
            } else {
               var3.add(var1.letterChars.toUpperCase());
            }
         }

         if (var1.altChars != null && var1.altChars.length() > 0) {
            if (!this.shiftDown && !this.capsDown) {
               var3.add(var1.altChars);
            } else {
               var3.add(var1.altChars.toUpperCase());
            }
         }

         if (var1.moreChars != null && var1.moreChars.length > 0) {
            if (this.isSymbol) {
               for (String var22 : var1.moreChars) {
                  if (!Character.isLetter(var22.charAt(0))) {
                     var3.add(var22);
                  }
               }
            } else {
               for (String var7 : var1.moreChars) {
                  if (Character.isLetter(var7.charAt(0))) {
                     if (!this.shiftDown && !this.capsDown) {
                        var3.add(var7);
                     } else {
                        var3.add(var7.toUpperCase());
                     }
                  }
               }
            }
         }

         boolean var15 = false;

         for (String var20 : var3) {
            if (var20.length() > 1) {
               var15 = true;
            }
         }

         secondaryVK.chars = var3.toArray(new String[var3.size()]);
         if (secondaryVK.chars.length > 1) {
            if (secondaryVK.getSkin() != null) {
               ((FXVKSkin)secondaryVK.getSkin()).rebuildSecondaryVK();
            }

            secondaryVK.setAttachedNode(var2);
            FXVKSkin var18 = (FXVKSkin)primaryVK.getSkin();
            FXVKSkin var21 = (FXVKSkin)secondaryVK.getSkin();
            int var23 = secondaryVK.chars.length;
            int var8 = (int)Math.floor(Math.sqrt((double)Math.max(1, var23 - 2)));
            int var9 = (int)Math.ceil((double)var23 / (double)var8);
            double var10 = this.snappedLeftInset() + this.snappedRightInset() + (double)var9 * 40.0 * (double)(var15 ? 2 : 1) + (double)((var9 - 1) * 6);
            double var12 = this.snappedTopInset() + this.snappedBottomInset() + (double)var8 * 56.0 + (double)((var8 - 1) * 6);
            secondaryVK.setPrefWidth(var10);
            secondaryVK.setMinWidth(Double.NEGATIVE_INFINITY);
            secondaryVK.setPrefHeight(var12);
            secondaryVK.setMinHeight(Double.NEGATIVE_INFINITY);
            Platform.runLater(() -> {
               Point2D var5 = com.sun.javafx.util.Utils.pointRelativeTo(var1, var10, var12, HPos.CENTER, VPos.TOP, 5.0, -3.0, true);
               double var6 = var5.getX();
               double var8x = var5.getY();
               Scene var10x = var1.getScene();
               var6 = Math.min(var6, var10x.getWindow().getX() + var10x.getWidth() - var10);
               secondaryPopup.show(var1.getScene().getWindow(), var6, var8x);
            });
         }
      } else {
         this.closeSecondaryVK();
      }
   }

   private List<List<FXVKSkin.Key>> loadBoard(String var1) {
      List var2 = boardMap.get(var1);
      if (var2 != null) {
         return var2;
      } else {
         String var3 = var1.substring(0, 1).toUpperCase() + var1.substring(1).toLowerCase() + "Board.txt";

         try {
            ArrayList var23 = new ArrayList(5);
            ArrayList var4 = new ArrayList(20);
            InputStream var5 = FXVKSkin.class.getResourceAsStream(var3);
            BufferedReader var6 = new BufferedReader(new InputStreamReader(var5, "UTF-8"));
            int var8 = 0;
            int var9 = 0;
            int var10 = 1;
            boolean var11 = false;
            ArrayList var12 = new ArrayList(10);

            String var7;
            while ((var7 = var6.readLine()) != null) {
               if (var7.length() != 0 && var7.charAt(0) != '#') {
                  for (int var13 = 0; var13 < var7.length(); var13++) {
                     char var14 = var7.charAt(var13);
                     if (var14 == ' ') {
                        var8++;
                     } else if (var14 == '[') {
                        var9 = var8;
                        var12 = new ArrayList(10);
                        var11 = false;
                     } else if (var14 == ']') {
                        String var25 = "";
                        String var26 = null;
                        String[] var27 = null;

                        for (int var18 = 0; var18 < var12.size(); var18++) {
                           var12.set(var18, FXVKCharEntities.get((String)var12.get(var18)));
                        }

                        int var28 = var12.size();
                        if (var28 > 0) {
                           var25 = (String)var12.get(0);
                           if (var28 > 1) {
                              var26 = (String)var12.get(1);
                              if (var28 > 2) {
                                 var27 = var12.subList(2, var28).toArray(new String[var28 - 2]);
                              }
                           }
                        }

                        var10 = var8 - var9;
                        Object var19;
                        if (var11) {
                           if ("$shift".equals(var25)) {
                              var19 = new FXVKSkin.KeyboardStateKey("", null, "shift") {
                                 @Override
                                 protected void release() {
                                    FXVKSkin.this.pressShift();
                                 }

                                 @Override
                                 public void update(boolean var1, boolean var2, boolean var3) {
                                    if (var3) {
                                       this.setDisable(true);
                                       this.setVisible(false);
                                    } else {
                                       if (var1) {
                                          this.icon.getStyleClass().remove("shift-icon");
                                          this.icon.getStyleClass().add("capslock-icon");
                                       } else {
                                          this.icon.getStyleClass().remove("capslock-icon");
                                          this.icon.getStyleClass().add("shift-icon");
                                       }

                                       this.setDisable(false);
                                       this.setVisible(true);
                                    }
                                 }
                              };
                              ((FXVKSkin.Key)var19).getStyleClass().add("shift");
                           } else if ("$SymbolABC".equals(var25)) {
                              var19 = new FXVKSkin.KeyboardStateKey("!#123", "ABC", "symbol") {
                                 @Override
                                 protected void release() {
                                    FXVKSkin.this.pressSymbolABC();
                                 }
                              };
                           } else if ("$backspace".equals(var25)) {
                              var19 = new FXVKSkin.KeyCodeKey("backspace", "\b", KeyCode.BACK_SPACE) {
                                 @Override
                                 protected void press() {
                                    if (FXVKSkin.KEY_REPEAT_RATE > 0.0) {
                                       FXVKSkin.this.clearShift();
                                       this.sendKeyEvents();
                                       FXVKSkin.repeatKey = this;
                                       FXVKSkin.repeatInitialDelay.playFromStart();
                                    } else {
                                       super.press();
                                    }
                                 }

                                 @Override
                                 protected void release() {
                                    if (FXVKSkin.KEY_REPEAT_RATE > 0.0) {
                                       FXVKSkin.repeatInitialDelay.stop();
                                       FXVKSkin.repeatSubsequentDelay.stop();
                                    } else {
                                       super.release();
                                    }
                                 }
                              };
                              ((FXVKSkin.Key)var19).getStyleClass().add("backspace");
                           } else if ("$enter".equals(var25)) {
                              var19 = new FXVKSkin.KeyCodeKey("enter", "\n", KeyCode.ENTER);
                              ((FXVKSkin.Key)var19).getStyleClass().add("enter");
                           } else if ("$tab".equals(var25)) {
                              var19 = new FXVKSkin.KeyCodeKey("tab", "\t", KeyCode.TAB);
                           } else if ("$space".equals(var25)) {
                              var19 = new FXVKSkin.CharKey(" ", " ", null, "space");
                           } else if ("$clear".equals(var25)) {
                              var19 = new FXVKSkin.SuperKey("clear", "");
                           } else if ("$.org".equals(var25)) {
                              var19 = new FXVKSkin.SuperKey(".org", ".org");
                           } else if ("$.com".equals(var25)) {
                              var19 = new FXVKSkin.SuperKey(".com", ".com");
                           } else if ("$.net".equals(var25)) {
                              var19 = new FXVKSkin.SuperKey(".net", ".net");
                           } else if ("$oracle.com".equals(var25)) {
                              var19 = new FXVKSkin.SuperKey("oracle.com", "oracle.com");
                           } else if ("$gmail.com".equals(var25)) {
                              var19 = new FXVKSkin.SuperKey("gmail.com", "gmail.com");
                           } else if ("$hide".equals(var25)) {
                              var19 = new FXVKSkin.KeyboardStateKey("hide", null, "hide") {
                                 @Override
                                 protected void release() {
                                    FXVKSkin.this.isVKHidden = true;
                                    FXVKSkin.startSlideOut(false);
                                    if (FXVKSkin.vkAdjustWindow) {
                                       FXVKSkin.this.restoreWindowPosition(FXVKSkin.this.attachedNode);
                                    }
                                 }
                              };
                              ((FXVKSkin.Key)var19).getStyleClass().add("hide");
                           } else if ("$undo".equals(var25)) {
                              var19 = new FXVKSkin.SuperKey("undo", "");
                           } else if ("$redo".equals(var25)) {
                              var19 = new FXVKSkin.SuperKey("redo", "");
                           } else {
                              var19 = null;
                           }
                        } else {
                           var19 = new FXVKSkin.CharKey(var25, var26, var27);
                        }

                        if (var19 != null) {
                           ((FXVKSkin.Key)var19).col = var9;
                           ((FXVKSkin.Key)var19).colSpan = var10;

                           for (String var21 : ((FXVKSkin.Key)var19).getStyleClass()) {
                              ((FXVKSkin.Key)var19).text.getStyleClass().add(var21 + "-text");
                              ((FXVKSkin.Key)var19).altText.getStyleClass().add(var21 + "-alttext");
                              ((FXVKSkin.Key)var19).icon.getStyleClass().add(var21 + "-icon");
                           }

                           if (var25 != null && var25.length() > 1) {
                              ((FXVKSkin.Key)var19).text.getStyleClass().add("multi-char-text");
                           }

                           if (var26 != null && var26.length() > 1) {
                              ((FXVKSkin.Key)var19).altText.getStyleClass().add("multi-char-text");
                           }

                           var4.add(var19);
                        }
                     } else {
                        for (int var15 = var13; var15 < var7.length(); var15++) {
                           char var16 = var7.charAt(var15);
                           boolean var17 = false;
                           if (var16 == '\\') {
                              var15++;
                              var13++;
                              var17 = true;
                              var16 = var7.charAt(var15);
                           }

                           if (var16 == '$' && !var17) {
                              var11 = true;
                           }

                           if (var16 == '|' && !var17) {
                              var12.add(var7.substring(var13, var15));
                              var13 = var15 + 1;
                           } else if ((var16 == ']' || var16 == ' ') && !var17) {
                              var12.add(var7.substring(var13, var15));
                              var13 = var15 - 1;
                              break;
                           }
                        }

                        var8++;
                     }
                  }

                  var8 = 0;
                  var9 = 0;
                  var23.add(var4);
                  var4 = new ArrayList(20);
               }
            }

            var6.close();
            boardMap.put(var1, var23);
            return var23;
         } catch (Exception var22) {
            var22.printStackTrace();
            return Collections.emptyList();
         }
      }
   }

   static {
      Void var0 = AccessController.doPrivileged((PrivilegedAction<Void>)(() -> {
         String var0x = System.getProperty("com.sun.javafx.vk.adjustwindow");
         if (var0x != null) {
            vkAdjustWindow = Boolean.valueOf(var0x);
         }

         var0x = System.getProperty("com.sun.javafx.sqe.vk.lookup");
         if (var0x != null) {
            vkLookup = Boolean.valueOf(var0x);
         }

         var0x = System.getProperty("com.sun.javafx.virtualKeyboard.backspaceRepeatDelay");
         if (var0x != null) {
            Double var1 = Double.valueOf(var0x);
            KEY_REPEAT_DELAY = Math.min(Math.max(var1, KEY_REPEAT_DELAY_MIN), KEY_REPEAT_DELAY_MAX);
         }

         var0x = System.getProperty("com.sun.javafx.virtualKeyboard.backspaceRepeatRate");
         if (var0x != null) {
            Double var5 = Double.valueOf(var0x);
            if (var5 <= 0.0) {
               KEY_REPEAT_RATE = 0.0;
            } else {
               KEY_REPEAT_RATE = Math.min(Math.max(var5, KEY_REPEAT_RATE_MIN), KEY_REPEAT_RATE_MAX);
            }
         }

         return null;
      }));
      winY.addListener(var0x -> {
         if (vkPopup != null) {
            vkPopup.setY(winY.get());
         }
      });
   }

   private class CharKey extends FXVKSkin.TextInputKey {
      private final String letterChars;
      private final String altChars;
      private final String[] moreChars;

      private CharKey(String var2, String var3, String[] var4, String var5) {
         this.letterChars = var2;
         this.altChars = var3;
         this.moreChars = var4;
         this.chars = this.letterChars;
         this.text.setText(this.chars);
         this.altText.setText(this.altChars);
         if (FXVKSkin.vkLookup) {
            this.setId((var5 != null ? var5 : this.chars).replaceAll("\\.", ""));
         }
      }

      private CharKey(String var2, String var3, String[] var4) {
         this(var2, var3, var4, null);
      }

      @Override
      protected void press() {
         super.press();
         if (!this.letterChars.equals(this.altChars) || this.moreChars != null) {
            if (FXVKSkin.this.fxvk == FXVKSkin.primaryVK) {
               FXVKSkin.this.showSecondaryVK(null);
               FXVKSkin.secondaryVKKey = this;
               FXVKSkin.secondaryVKDelay.playFromStart();
            }
         }
      }

      @Override
      protected void release() {
         super.release();
         if (!this.letterChars.equals(this.altChars) || this.moreChars != null) {
            if (FXVKSkin.this.fxvk == FXVKSkin.primaryVK) {
               FXVKSkin.secondaryVKDelay.stop();
            }
         }
      }

      @Override
      public void update(boolean var1, boolean var2, boolean var3) {
         if (var3) {
            this.chars = this.altChars;
            this.text.setText(this.chars);
            if (this.moreChars != null && this.moreChars.length > 0 && !Character.isLetter(this.moreChars[0].charAt(0))) {
               this.altText.setText(this.moreChars[0]);
            } else {
               this.altText.setText(null);
            }
         } else {
            this.chars = !var1 && !var2 ? this.letterChars.toLowerCase() : this.letterChars.toUpperCase();
            this.text.setText(this.chars);
            this.altText.setText(this.altChars);
         }
      }
   }

   private class Key extends Region {
      int col = 0;
      int colSpan = 1;
      protected final Text text;
      protected final Text altText;
      protected final Region icon = new Region();

      protected Key() {
         this.text = new Text();
         this.text.setTextOrigin(VPos.TOP);
         this.altText = new Text();
         this.altText.setTextOrigin(VPos.TOP);
         this.getChildren().setAll(new Node[]{this.text, this.altText, this.icon});
         this.getStyleClass().setAll(new String[]{"key"});
         this.addEventHandler(MouseEvent.MOUSE_PRESSED, var1x -> {
            if (var1x.getButton() == MouseButton.PRIMARY) {
               this.press();
            }
         });
         this.addEventHandler(MouseEvent.MOUSE_RELEASED, var1x -> {
            if (var1x.getButton() == MouseButton.PRIMARY) {
               this.release();
            }
         });
      }

      protected void press() {
      }

      protected void release() {
         FXVKSkin.this.clearShift();
      }

      public void update(boolean var1, boolean var2, boolean var3) {
      }

      protected void layoutChildren() {
         double var1 = this.snappedLeftInset();
         double var3 = this.snappedTopInset();
         double var5 = this.getWidth() - var1 - this.snappedRightInset();
         double var7 = this.getHeight() - var3 - this.snappedBottomInset();
         this.text.setVisible(this.icon.getBackground() == null);
         double var9 = this.text.prefWidth(-1.0);
         double var11 = this.text.prefHeight(-1.0);
         this.text
            .resizeRelocate(
               (double)((int)(var1 + (var5 - var9) / 2.0 + 0.5)), (double)((int)(var3 + (var7 - var11) / 2.0 + 0.5)), (double)((int)var9), (double)((int)var11)
            );
         this.altText.setVisible(this.icon.getBackground() == null && this.altText.getText().length() > 0);
         var9 = this.altText.prefWidth(-1.0);
         var11 = this.altText.prefHeight(-1.0);
         this.altText
            .resizeRelocate(
               (double)((int)var1) + (var5 - var9) + 0.5,
               (double)((int)(var3 + (var7 - var11) / 2.0 + 0.5 - var7 / 2.0)),
               (double)((int)var9),
               (double)((int)var11)
            );
         this.icon.resizeRelocate(var1 - 8.0, var3 - 8.0, var5 + 16.0, var7 + 16.0);
      }
   }

   private class KeyCodeKey extends FXVKSkin.SuperKey {
      private KeyCode code;

      private KeyCodeKey(String var2, String var3, KeyCode var4) {
         super(var2, var3);
         this.code = var4;
         if (FXVKSkin.vkLookup) {
            this.setId(var2);
         }
      }

      @Override
      protected void sendKeyEvents() {
         Node var1 = FXVKSkin.this.fxvk.getAttachedNode();
         if (var1 instanceof EventTarget) {
            var1.fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.CHAR_UNDEFINED, this.chars, this.code, FXVKSkin.this.shiftDown, false, false, false));
            var1.fireEvent(new KeyEvent(KeyEvent.KEY_TYPED, this.chars, "", KeyCode.UNDEFINED, FXVKSkin.this.shiftDown, false, false, false));
            var1.fireEvent(new KeyEvent(KeyEvent.KEY_RELEASED, KeyEvent.CHAR_UNDEFINED, this.chars, this.code, FXVKSkin.this.shiftDown, false, false, false));
         }
      }
   }

   private class KeyboardStateKey extends FXVKSkin.Key {
      private final String defaultText;
      private final String toggledText;

      private KeyboardStateKey(String var2, String var3, String var4) {
         this.defaultText = var2;
         this.toggledText = var3;
         this.text.setText(this.defaultText);
         if (FXVKSkin.vkLookup && var4 != null) {
            this.setId(var4);
         }

         this.getStyleClass().add("special");
      }

      @Override
      public void update(boolean var1, boolean var2, boolean var3) {
         if (var3) {
            this.text.setText(this.toggledText);
         } else {
            this.text.setText(this.defaultText);
         }
      }
   }

   private class SuperKey extends FXVKSkin.TextInputKey {
      private SuperKey(String var2, String var3) {
         this.chars = var3;
         this.text.setText(var2);
         this.getStyleClass().add("special");
         if (FXVKSkin.vkLookup) {
            this.setId(var2);
         }
      }
   }

   private class TextInputKey extends FXVKSkin.Key {
      String chars = "";

      @Override
      protected void press() {
      }

      @Override
      protected void release() {
         if (FXVKSkin.this.fxvk == FXVKSkin.secondaryVK || FXVKSkin.secondaryPopup == null || !FXVKSkin.secondaryPopup.isShowing()) {
            this.sendKeyEvents();
            if (FXVKSkin.this.fxvk == FXVKSkin.secondaryVK) {
               FXVKSkin.this.showSecondaryVK(null);
            }

            super.release();
         }
      }

      protected void sendKeyEvents() {
         Node var1 = FXVKSkin.this.fxvk.getAttachedNode();
         if (var1 instanceof EventTarget && this.chars != null) {
            var1.fireEvent(new KeyEvent(KeyEvent.KEY_TYPED, this.chars, "", KeyCode.UNDEFINED, FXVKSkin.this.shiftDown, false, false, false));
         }
      }
   }
}
