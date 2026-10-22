package com.sun.javafx.scene.control.behavior;

import com.sun.javafx.PlatformUtil;
import com.sun.javafx.scene.control.Properties;
import com.sun.javafx.scene.control.inputmap.InputMap;
import com.sun.javafx.scene.control.inputmap.KeyBinding;
import com.sun.javafx.scene.control.skin.Utils;
import java.util.function.Predicate;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.skin.TextAreaSkin;
import javafx.scene.control.skin.TextInputControlSkin;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.HitInfo;
import javafx.stage.Screen;
import javafx.stage.Window;

public class TextAreaBehavior extends TextInputControlBehavior<TextArea> {
   private TextAreaSkin skin;
   private TwoLevelFocusBehavior tlFocus;
   private boolean focusGainedByMouseClick = false;
   private boolean shiftDown = false;
   private boolean deferClick = false;

   public TextAreaBehavior(TextArea var1) {
      super(var1);
      if (Properties.IS_TOUCH_SUPPORTED) {
         this.contextMenu.getStyleClass().add("text-input-context-menu");
      }

      Predicate var2 = var1x -> !var1.isEditable();
      InputMap var3 = new InputMap(var1);
      var3.getMappings()
         .addAll(
            new InputMap.Mapping[]{
               this.keyMapping(KeyCode.HOME, var1x -> this.lineStart(false)),
               this.keyMapping(KeyCode.END, var1x -> this.lineEnd(false)),
               this.keyMapping(KeyCode.UP, var1x -> this.skin.moveCaret(TextInputControlSkin.TextUnit.LINE, TextInputControlSkin.Direction.UP, false)),
               this.keyMapping(KeyCode.DOWN, var1x -> this.skin.moveCaret(TextInputControlSkin.TextUnit.LINE, TextInputControlSkin.Direction.DOWN, false)),
               this.keyMapping(KeyCode.PAGE_UP, var1x -> this.skin.moveCaret(TextInputControlSkin.TextUnit.PAGE, TextInputControlSkin.Direction.UP, false)),
               this.keyMapping(KeyCode.PAGE_DOWN, var1x -> this.skin.moveCaret(TextInputControlSkin.TextUnit.PAGE, TextInputControlSkin.Direction.DOWN, false)),
               this.keyMapping(new KeyBinding(KeyCode.HOME).shift(), var1x -> this.lineStart(true)),
               this.keyMapping(new KeyBinding(KeyCode.END).shift(), var1x -> this.lineEnd(true)),
               this.keyMapping(
                  new KeyBinding(KeyCode.UP).shift(), var1x -> this.skin.moveCaret(TextInputControlSkin.TextUnit.LINE, TextInputControlSkin.Direction.UP, true)
               ),
               this.keyMapping(
                  new KeyBinding(KeyCode.DOWN).shift(),
                  var1x -> this.skin.moveCaret(TextInputControlSkin.TextUnit.LINE, TextInputControlSkin.Direction.DOWN, true)
               ),
               this.keyMapping(
                  new KeyBinding(KeyCode.PAGE_UP).shift(),
                  var1x -> this.skin.moveCaret(TextInputControlSkin.TextUnit.PAGE, TextInputControlSkin.Direction.UP, true)
               ),
               this.keyMapping(
                  new KeyBinding(KeyCode.PAGE_DOWN).shift(),
                  var1x -> this.skin.moveCaret(TextInputControlSkin.TextUnit.PAGE, TextInputControlSkin.Direction.DOWN, true)
               ),
               this.keyMapping(new KeyBinding(KeyCode.ENTER), var1x -> this.insertNewLine(), var2),
               this.keyMapping(new KeyBinding(KeyCode.TAB), var1x -> this.insertTab(), var2)
            }
         );
      this.addDefaultChildMap(this.getInputMap(), var3);
      InputMap var4 = new InputMap(var1);
      var4.setInterceptor(var0 -> !PlatformUtil.isMac());
      var4.getMappings()
         .addAll(
            new InputMap.Mapping[]{
               this.keyMapping(new KeyBinding(KeyCode.LEFT).shortcut(), var1x -> this.lineStart(false)),
               this.keyMapping(new KeyBinding(KeyCode.RIGHT).shortcut(), var1x -> this.lineEnd(false)),
               this.keyMapping(new KeyBinding(KeyCode.UP).shortcut(), var1x -> var1.home()),
               this.keyMapping(new KeyBinding(KeyCode.DOWN).shortcut(), var1x -> var1.end()),
               this.keyMapping(new KeyBinding(KeyCode.LEFT).shortcut().shift(), var1x -> this.lineStart(true)),
               this.keyMapping(new KeyBinding(KeyCode.RIGHT).shortcut().shift(), var1x -> this.lineEnd(true)),
               this.keyMapping(new KeyBinding(KeyCode.UP).shortcut().shift(), var1x -> this.selectHomeExtend()),
               this.keyMapping(new KeyBinding(KeyCode.DOWN).shortcut().shift(), var1x -> this.selectEndExtend()),
               this.keyMapping(
                  new KeyBinding(KeyCode.UP).alt(),
                  var1x -> this.skin.moveCaret(TextInputControlSkin.TextUnit.PARAGRAPH, TextInputControlSkin.Direction.UP, false)
               ),
               this.keyMapping(
                  new KeyBinding(KeyCode.DOWN).alt(),
                  var1x -> this.skin.moveCaret(TextInputControlSkin.TextUnit.PARAGRAPH, TextInputControlSkin.Direction.DOWN, false)
               ),
               this.keyMapping(
                  new KeyBinding(KeyCode.UP).alt().shift(),
                  var1x -> this.skin.moveCaret(TextInputControlSkin.TextUnit.PARAGRAPH, TextInputControlSkin.Direction.UP, true)
               ),
               this.keyMapping(
                  new KeyBinding(KeyCode.DOWN).alt().shift(),
                  var1x -> this.skin.moveCaret(TextInputControlSkin.TextUnit.PARAGRAPH, TextInputControlSkin.Direction.DOWN, true)
               )
            }
         );
      this.addDefaultChildMap(var3, var4);
      InputMap var5 = new InputMap(var1);
      var5.setInterceptor(var0 -> PlatformUtil.isMac());
      var5.getMappings()
         .addAll(
            new InputMap.Mapping[]{
               this.keyMapping(
                  new KeyBinding(KeyCode.UP).ctrl(),
                  var1x -> this.skin.moveCaret(TextInputControlSkin.TextUnit.PARAGRAPH, TextInputControlSkin.Direction.UP, false)
               ),
               this.keyMapping(
                  new KeyBinding(KeyCode.DOWN).ctrl(),
                  var1x -> this.skin.moveCaret(TextInputControlSkin.TextUnit.PARAGRAPH, TextInputControlSkin.Direction.DOWN, false)
               ),
               this.keyMapping(
                  new KeyBinding(KeyCode.UP).ctrl().shift(),
                  var1x -> this.skin.moveCaret(TextInputControlSkin.TextUnit.PARAGRAPH, TextInputControlSkin.Direction.UP, true)
               ),
               this.keyMapping(
                  new KeyBinding(KeyCode.DOWN).ctrl().shift(),
                  var1x -> this.skin.moveCaret(TextInputControlSkin.TextUnit.PARAGRAPH, TextInputControlSkin.Direction.DOWN, true)
               )
            }
         );
      this.addDefaultChildMap(var3, var5);
      this.addKeyPadMappings(var3);
      var1.focusedProperty().addListener(new ChangeListener<Boolean>() {
         public void changed(ObservableValue<? extends Boolean> var1, Boolean var2, Boolean var3) {
            TextArea var4 = TextAreaBehavior.this.getNode();
            if (var4.isFocused()) {
               if (!TextAreaBehavior.this.focusGainedByMouseClick) {
                  TextAreaBehavior.this.setCaretAnimating(true);
               }
            } else {
               TextAreaBehavior.this.focusGainedByMouseClick = false;
               TextAreaBehavior.this.setCaretAnimating(false);
            }
         }
      });
      if (Utils.isTwoLevelFocus()) {
         this.tlFocus = new TwoLevelFocusBehavior(var1);
      }
   }

   @Override
   public void dispose() {
      if (this.tlFocus != null) {
         this.tlFocus.dispose();
      }

      super.dispose();
   }

   public void setTextAreaSkin(TextAreaSkin var1) {
      this.skin = var1;
   }

   private void insertNewLine() {
      this.setEditing(true);
      this.getNode().replaceSelection("\n");
      this.setEditing(false);
   }

   private void insertTab() {
      this.setEditing(true);
      this.getNode().replaceSelection("\t");
      this.setEditing(false);
   }

   @Override
   protected void deleteChar(boolean var1) {
      if (var1) {
         this.getNode().deletePreviousChar();
      } else {
         this.getNode().deleteNextChar();
      }
   }

   @Override
   protected void deleteFromLineStart() {
      TextArea var1 = this.getNode();
      int var2 = var1.getCaretPosition();
      if (var2 > 0) {
         this.lineStart(false);
         int var3 = var1.getCaretPosition();
         if (var2 > var3) {
            this.replaceText(var3, var2, "");
         }
      }
   }

   private void lineStart(boolean var1) {
      this.skin.moveCaret(TextInputControlSkin.TextUnit.LINE, TextInputControlSkin.Direction.BEGINNING, var1);
   }

   private void lineEnd(boolean var1) {
      this.skin.moveCaret(TextInputControlSkin.TextUnit.LINE, TextInputControlSkin.Direction.END, var1);
   }

   @Override
   protected void replaceText(int var1, int var2, String var3) {
      this.getNode().replaceText(var1, var2, var3);
   }

   @Override
   public void mousePressed(MouseEvent var1) {
      TextArea var2 = this.getNode();
      if (!var2.isDisabled()) {
         if (!var2.isFocused()) {
            this.focusGainedByMouseClick = true;
            var2.requestFocus();
         }

         this.setCaretAnimating(false);
         if (var1.getButton() == MouseButton.PRIMARY && !var1.isMiddleButtonDown() && !var1.isSecondaryButtonDown()) {
            HitInfo var3 = this.skin.getIndex(var1.getX(), var1.getY());
            int var4 = var3.getInsertionIndex();
            int var5 = var2.getAnchor();
            int var6 = var2.getCaretPosition();
            if (var1.getClickCount() >= 2 || !var1.isSynthesized() && (var5 == var6 || (var4 <= var5 || var4 >= var6) && (var4 >= var5 || var4 <= var6))) {
               if (!var1.isControlDown() && !var1.isAltDown() && !var1.isShiftDown() && !var1.isMetaDown() && !var1.isShortcutDown()) {
                  switch (var1.getClickCount()) {
                     case 1:
                        this.skin.positionCaret(var3, false);
                        break;
                     case 2:
                        this.mouseDoubleClick(var3);
                        break;
                     case 3:
                        this.mouseTripleClick(var3);
                  }
               } else if (var1.isShiftDown()
                  && !var1.isControlDown()
                  && !var1.isAltDown()
                  && !var1.isMetaDown()
                  && !var1.isShortcutDown()
                  && var1.getClickCount() == 1) {
                  this.shiftDown = true;
                  if (PlatformUtil.isMac()) {
                     var2.extendSelection(var4);
                  } else {
                     this.skin.positionCaret(var3, true);
                  }
               }
            } else {
               this.deferClick = true;
            }
         }

         if (this.contextMenu.isShowing()) {
            this.contextMenu.hide();
         }
      }
   }

   @Override
   public void mouseDragged(MouseEvent var1) {
      TextArea var2 = this.getNode();
      if (!var2.isDisabled()
         && !var1.isSynthesized()
         && var1.getButton() == MouseButton.PRIMARY
         && !var1.isMiddleButtonDown()
         && !var1.isSecondaryButtonDown()
         && !var1.isControlDown()
         && !var1.isAltDown()
         && !var1.isShiftDown()
         && !var1.isMetaDown()) {
         this.skin.positionCaret(this.skin.getIndex(var1.getX(), var1.getY()), true);
      }

      this.deferClick = false;
   }

   @Override
   public void mouseReleased(MouseEvent var1) {
      TextArea var2 = this.getNode();
      if (!var2.isDisabled()) {
         this.setCaretAnimating(false);
         if (this.deferClick) {
            this.deferClick = false;
            this.skin.positionCaret(this.skin.getIndex(var1.getX(), var1.getY()), this.shiftDown);
            this.shiftDown = false;
         }

         this.setCaretAnimating(true);
      }
   }

   @Override
   public void contextMenuRequested(ContextMenuEvent var1) {
      TextArea var2 = this.getNode();
      if (this.contextMenu.isShowing()) {
         this.contextMenu.hide();
      } else if (var2.getContextMenu() == null && var2.getOnContextMenuRequested() == null) {
         double var3 = var1.getScreenX();
         double var5 = var1.getScreenY();
         double var7 = var1.getSceneX();
         if (Properties.IS_TOUCH_SUPPORTED) {
            Point2D var9;
            if (var2.getSelection().getLength() == 0) {
               this.skin.positionCaret(this.skin.getIndex(var1.getX(), var1.getY()), false);
               var9 = this.skin.getMenuPosition();
            } else {
               var9 = this.skin.getMenuPosition();
               if (var9 != null && (var9.getX() <= 0.0 || var9.getY() <= 0.0)) {
                  this.skin.positionCaret(this.skin.getIndex(var1.getX(), var1.getY()), false);
                  var9 = this.skin.getMenuPosition();
               }
            }

            if (var9 != null) {
               Point2D var10 = this.getNode().localToScene(var9);
               Scene var11 = this.getNode().getScene();
               Window var12 = var11.getWindow();
               Point2D var13 = new Point2D(var12.getX() + var11.getX() + var10.getX(), var12.getY() + var11.getY() + var10.getY());
               var3 = var13.getX();
               var7 = var10.getX();
               var5 = var13.getY();
            }
         }

         this.populateContextMenu();
         double var17 = this.contextMenu.prefWidth(-1.0);
         double var18 = var3 - (Properties.IS_TOUCH_SUPPORTED ? var17 / 2.0 : 0.0);
         Screen var19 = com.sun.javafx.util.Utils.getScreenForPoint(var3, 0.0);
         Rectangle2D var14 = var19.getBounds();
         if (var18 < var14.getMinX()) {
            this.getNode().getProperties().put("CONTEXT_MENU_SCREEN_X", var3);
            this.getNode().getProperties().put("CONTEXT_MENU_SCENE_X", var7);
            this.contextMenu.show(this.getNode(), var14.getMinX(), var5);
         } else if (var3 + var17 > var14.getMaxX()) {
            double var15 = var17 - (var14.getMaxX() - var3);
            this.getNode().getProperties().put("CONTEXT_MENU_SCREEN_X", var3);
            this.getNode().getProperties().put("CONTEXT_MENU_SCENE_X", var7);
            this.contextMenu.show(this.getNode(), var3 - var15, var5);
         } else {
            this.getNode().getProperties().put("CONTEXT_MENU_SCREEN_X", 0);
            this.getNode().getProperties().put("CONTEXT_MENU_SCENE_X", 0);
            this.contextMenu.show(this.getNode(), var18, var5);
         }
      }

      var1.consume();
   }

   @Override
   protected void setCaretAnimating(boolean var1) {
      this.skin.setCaretAnimating(var1);
   }

   protected void mouseDoubleClick(HitInfo var1) {
      TextArea var2 = this.getNode();
      var2.previousWord();
      if (PlatformUtil.isWindows()) {
         var2.selectNextWord();
      } else {
         var2.selectEndOfNextWord();
      }
   }

   protected void mouseTripleClick(HitInfo var1) {
      this.skin.moveCaret(TextInputControlSkin.TextUnit.PARAGRAPH, TextInputControlSkin.Direction.BEGINNING, false);
      this.skin.moveCaret(TextInputControlSkin.TextUnit.PARAGRAPH, TextInputControlSkin.Direction.END, true);
   }
}
