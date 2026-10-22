package com.sun.javafx.scene.control.behavior;

import com.sun.javafx.PlatformUtil;
import com.sun.javafx.application.PlatformImpl;
import com.sun.javafx.scene.control.Properties;
import com.sun.javafx.scene.control.inputmap.InputMap;
import com.sun.javafx.scene.control.inputmap.KeyBinding;
import com.sun.javafx.scene.control.skin.FXVK;
import com.sun.javafx.scene.control.skin.resources.ControlResources;
import java.text.Bidi;
import java.util.function.Predicate;
import javafx.application.ConditionalFeature;
import javafx.beans.InvalidationListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.IndexRange;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.skin.TextInputControlSkin;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public abstract class TextInputControlBehavior<T extends TextInputControl> extends BehaviorBase<T> {
   static final boolean SHOW_HANDLES = Properties.IS_TOUCH_SUPPORTED;
   public static final String DISABLE_FORWARD_TO_PARENT = "TextInputControlBehavior.disableForwardToParent";
   final T textInputControl;
   protected ContextMenu contextMenu;
   private InvalidationListener textListener = var1x -> this.invalidateBidi();
   private final InputMap<T> inputMap;
   private Bidi bidi = null;
   private Boolean mixed = null;
   private Boolean rtlText = null;
   private boolean editing = false;
   private final MenuItem undoMI = new TextInputControlBehavior.ContextMenuItem("Undo", var1x -> this.undo());
   private final MenuItem redoMI = new TextInputControlBehavior.ContextMenuItem("Redo", var1x -> this.redo());
   private final MenuItem cutMI = new TextInputControlBehavior.ContextMenuItem("Cut", var1x -> this.cut());
   private final MenuItem copyMI = new TextInputControlBehavior.ContextMenuItem("Copy", var1x -> this.getNode().copy());
   private final MenuItem pasteMI = new TextInputControlBehavior.ContextMenuItem("Paste", var1x -> this.paste());
   private final MenuItem deleteMI = new TextInputControlBehavior.ContextMenuItem("DeleteSelection", var1x -> this.deleteSelection());
   private final MenuItem selectWordMI = new TextInputControlBehavior.ContextMenuItem("SelectWord", var1x -> this.selectWord());
   private final MenuItem selectAllMI = new TextInputControlBehavior.ContextMenuItem("SelectAll", var1x -> this.selectAll());
   private final MenuItem separatorMI = new SeparatorMenuItem();

   public TextInputControlBehavior(T var1) {
      super((T)var1);
      this.textInputControl = (T)var1;
      this.textInputControl.textProperty().addListener(this.textListener);
      this.inputMap = this.createInputMap();
      Predicate var2 = var1x -> !var1.isEditable();
      Predicate var3 = var0 -> !PlatformUtil.isWindows();
      Predicate var4 = var0 -> !PlatformUtil.isLinux();
      InputMap.KeyMapping var5;
      InputMap.KeyMapping var6;
      InputMap.KeyMapping var7;
      this.addDefaultMapping(
         this.inputMap,
         new InputMap.Mapping[]{
            this.keyMapping(KeyCode.RIGHT, var1x -> this.nextCharacterVisually(true)),
            this.keyMapping(KeyCode.LEFT, var1x -> this.nextCharacterVisually(false)),
            this.keyMapping(KeyCode.UP, var1x -> var1.home()),
            this.keyMapping(KeyCode.HOME, var1x -> var1.home()),
            this.keyMapping(KeyCode.DOWN, var1x -> var1.end()),
            this.keyMapping(KeyCode.END, var1x -> var1.end()),
            var6 = this.keyMapping(KeyCode.ENTER, this::fire),
            this.keyMapping(new KeyBinding(KeyCode.HOME).shortcut(), var1x -> var1.home()),
            this.keyMapping(new KeyBinding(KeyCode.END).shortcut(), var1x -> var1.end()),
            this.keyMapping(new KeyBinding(KeyCode.BACK_SPACE), var1x -> this.deletePreviousChar(), var2),
            this.keyMapping(new KeyBinding(KeyCode.BACK_SPACE).shift(), var1x -> this.deletePreviousChar(), var2),
            this.keyMapping(new KeyBinding(KeyCode.DELETE), var1x -> this.deleteNextChar(), var2),
            this.keyMapping(new KeyBinding(KeyCode.X).shortcut(), var1x -> this.cut(), var2),
            this.keyMapping(new KeyBinding(KeyCode.CUT), var1x -> this.cut(), var2),
            this.keyMapping(new KeyBinding(KeyCode.C).shortcut(), var1x -> var1.copy()),
            this.keyMapping(new KeyBinding(KeyCode.INSERT).shortcut(), var1x -> var1.copy()),
            this.keyMapping(KeyCode.COPY, var1x -> var1.copy()),
            this.keyMapping(new KeyBinding(KeyCode.V).shortcut(), var1x -> this.paste(), var2),
            this.keyMapping(new KeyBinding(KeyCode.PASTE), var1x -> this.paste(), var2),
            this.keyMapping(new KeyBinding(KeyCode.INSERT).shift(), var1x -> this.paste(), var2),
            this.keyMapping(new KeyBinding(KeyCode.RIGHT).shift(), var1x -> this.selectRight()),
            this.keyMapping(new KeyBinding(KeyCode.LEFT).shift(), var1x -> this.selectLeft()),
            this.keyMapping(new KeyBinding(KeyCode.UP).shift(), var1x -> this.selectHome()),
            this.keyMapping(new KeyBinding(KeyCode.DOWN).shift(), var1x -> this.selectEnd()),
            this.keyMapping(new KeyBinding(KeyCode.HOME).shortcut().shift(), var1x -> this.selectHome()),
            this.keyMapping(new KeyBinding(KeyCode.END).shortcut().shift(), var1x -> this.selectEnd()),
            this.keyMapping(new KeyBinding(KeyCode.A).shortcut(), var1x -> var1.selectAll()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.TAB), FocusTraversalInputMap::traverseNext),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.TAB).shift(), FocusTraversalInputMap::traversePrevious),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.TAB).ctrl(), FocusTraversalInputMap::traverseNext),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.TAB).ctrl().shift(), FocusTraversalInputMap::traversePrevious),
            var5 = new InputMap.KeyMapping(KeyCode.ESCAPE, this::cancelEdit),
            this.keyMapping(new KeyBinding(KeyCode.Z).shortcut(), var1x -> this.undo()),
            this.keyMapping(
               new KeyBinding(null, KeyEvent.KEY_TYPED)
                  .alt(KeyBinding.OptionalBoolean.ANY)
                  .shift(KeyBinding.OptionalBoolean.ANY)
                  .ctrl(KeyBinding.OptionalBoolean.ANY)
                  .meta(KeyBinding.OptionalBoolean.ANY),
               this::defaultKeyTyped
            ),
            var7 = this.keyMapping(new KeyBinding(null, KeyEvent.KEY_PRESSED).shift(KeyBinding.OptionalBoolean.ANY), var0 -> {
               if (!var0.getCode().isFunctionKey()) {
                  var0.consume();
               }
            }),
            new InputMap.KeyMapping(
               new KeyBinding(KeyCode.DIGIT9).ctrl().shift(),
               var1x -> FXVK.toggleUseVK(this.textInputControl),
               var0 -> !PlatformImpl.isSupported(ConditionalFeature.VIRTUAL_KEYBOARD)
            ),
            new InputMap.MouseMapping(MouseEvent.MOUSE_PRESSED, this::mousePressed),
            new InputMap.MouseMapping(MouseEvent.MOUSE_DRAGGED, this::mouseDragged),
            new InputMap.MouseMapping(MouseEvent.MOUSE_RELEASED, this::mouseReleased),
            new InputMap.Mapping<ContextMenuEvent>(ContextMenuEvent.CONTEXT_MENU_REQUESTED, this::contextMenuRequested) {
               @Override
               public int getSpecificity(Event var1) {
                  return 1;
               }
            }
         }
      );
      var5.setAutoConsume(false);
      var6.setAutoConsume(false);
      var7.setAutoConsume(false);
      InputMap var8 = new InputMap(var1);
      var8.setInterceptor(var0 -> !PlatformUtil.isMac());
      var8.getMappings()
         .addAll(
            new InputMap.Mapping[]{
               this.keyMapping(new KeyBinding(KeyCode.HOME).shift(), var1x -> this.selectHomeExtend()),
               this.keyMapping(new KeyBinding(KeyCode.END).shift(), var1x -> this.selectEndExtend()),
               this.keyMapping(new KeyBinding(KeyCode.LEFT).shortcut(), var1x -> var1.home()),
               this.keyMapping(new KeyBinding(KeyCode.RIGHT).shortcut(), var1x -> var1.end()),
               this.keyMapping(new KeyBinding(KeyCode.LEFT).alt(), var1x -> this.leftWord()),
               this.keyMapping(new KeyBinding(KeyCode.RIGHT).alt(), var1x -> this.rightWord()),
               this.keyMapping(new KeyBinding(KeyCode.DELETE).alt(), var1x -> this.deleteNextWord()),
               this.keyMapping(new KeyBinding(KeyCode.BACK_SPACE).alt(), var1x -> this.deletePreviousWord()),
               this.keyMapping(new KeyBinding(KeyCode.BACK_SPACE).shortcut(), var1x -> this.deleteFromLineStart()),
               this.keyMapping(new KeyBinding(KeyCode.Z).shortcut().shift(), var1x -> this.redo()),
               this.keyMapping(new KeyBinding(KeyCode.LEFT).shortcut().shift(), var1x -> this.selectHomeExtend()),
               this.keyMapping(new KeyBinding(KeyCode.RIGHT).shortcut().shift(), var1x -> this.selectEndExtend()),
               this.keyMapping(new KeyBinding(KeyCode.LEFT).shift().alt(), var1x -> this.selectLeftWord()),
               this.keyMapping(new KeyBinding(KeyCode.RIGHT).shift().alt(), var1x -> this.selectRightWord())
            }
         );
      this.addDefaultChildMap(this.inputMap, var8);
      InputMap var9 = new InputMap(var1);
      var9.setInterceptor(var0 -> PlatformUtil.isMac());
      var9.getMappings()
         .addAll(
            new InputMap.Mapping[]{
               this.keyMapping(new KeyBinding(KeyCode.HOME).shift(), var1x -> this.selectHome()),
               this.keyMapping(new KeyBinding(KeyCode.END).shift(), var1x -> this.selectEnd()),
               this.keyMapping(new KeyBinding(KeyCode.LEFT).ctrl(), var1x -> this.leftWord()),
               this.keyMapping(new KeyBinding(KeyCode.RIGHT).ctrl(), var1x -> this.rightWord()),
               this.keyMapping(new KeyBinding(KeyCode.H).ctrl(), var1x -> this.deletePreviousChar()),
               this.keyMapping(new KeyBinding(KeyCode.DELETE).ctrl(), var1x -> this.deleteNextWord()),
               this.keyMapping(new KeyBinding(KeyCode.BACK_SPACE).ctrl(), var1x -> this.deletePreviousWord()),
               this.keyMapping(new KeyBinding(KeyCode.BACK_SLASH).ctrl(), var1x -> var1.deselect()),
               this.keyMapping(new KeyBinding(KeyCode.Y).ctrl(), var1x -> this.redo(), var3),
               this.keyMapping(new KeyBinding(KeyCode.Z).ctrl().shift(), var1x -> this.redo(), var4),
               this.keyMapping(new KeyBinding(KeyCode.LEFT).ctrl().shift(), var1x -> this.selectLeftWord()),
               this.keyMapping(new KeyBinding(KeyCode.RIGHT).ctrl().shift(), var1x -> this.selectRightWord())
            }
         );
      this.addDefaultChildMap(this.inputMap, var9);
      this.addKeyPadMappings(this.inputMap);
      this.textInputControl.textProperty().addListener(this.textListener);
      this.contextMenu = new ContextMenu();
   }

   @Override
   public InputMap<T> getInputMap() {
      return this.inputMap;
   }

   protected void addKeyPadMappings(InputMap<T> var1) {
      InputMap var2 = new InputMap(this.getNode());

      for (Object var4 : var1.getMappings()) {
         if (var4 instanceof InputMap.KeyMapping) {
            InputMap.KeyMapping var5 = (InputMap.KeyMapping)var4;
            KeyBinding var6 = (KeyBinding)var5.getMappingKey();
            if (var6.getCode() != null) {
               KeyCode var7 = null;
               switch (var6.getCode()) {
                  case LEFT:
                     var7 = KeyCode.KP_LEFT;
                     break;
                  case RIGHT:
                     var7 = KeyCode.KP_RIGHT;
                     break;
                  case UP:
                     var7 = KeyCode.KP_UP;
                     break;
                  case DOWN:
                     var7 = KeyCode.KP_DOWN;
               }

               if (var7 != null) {
                  KeyBinding var8 = new KeyBinding(var7).shift(var6.getShift()).ctrl(var6.getCtrl()).alt(var6.getAlt()).meta(var6.getMeta());
                  var2.getMappings().add(new InputMap.KeyMapping(var8, var5.getEventHandler()));
               }
            }
         }
      }

      for (Object var11 : var2.getMappings()) {
         var1.getMappings().add((InputMap.KeyMapping)var11);
      }

      for (Object var12 : var1.getChildInputMaps()) {
         this.addKeyPadMappings((InputMap<T>)var12);
      }
   }

   protected InputMap.KeyMapping keyMapping(KeyCode var1, EventHandler<KeyEvent> var2) {
      return this.keyMapping(new KeyBinding(var1), var2);
   }

   protected InputMap.KeyMapping keyMapping(KeyBinding var1, EventHandler<KeyEvent> var2) {
      return this.keyMapping(var1, var2, null);
   }

   protected InputMap.KeyMapping keyMapping(KeyBinding var1, EventHandler<KeyEvent> var2, Predicate<KeyEvent> var3) {
      return new InputMap.KeyMapping(var1, var2x -> {
         this.setCaretAnimating(false);
         var2.handle(var2x);
         this.setCaretAnimating(true);
      }, var3);
   }

   @Override
   public void dispose() {
      this.textInputControl.textProperty().removeListener(this.textListener);
      super.dispose();
   }

   protected abstract void deleteChar(boolean var1);

   protected abstract void replaceText(int var1, int var2, String var3);

   protected abstract void setCaretAnimating(boolean var1);

   protected abstract void deleteFromLineStart();

   protected abstract void mousePressed(MouseEvent var1);

   protected abstract void mouseDragged(MouseEvent var1);

   protected abstract void mouseReleased(MouseEvent var1);

   protected abstract void contextMenuRequested(ContextMenuEvent var1);

   private void defaultKeyTyped(KeyEvent var1) {
      TextInputControl var2 = this.getNode();
      if (var2.isEditable() && !var2.isDisabled()) {
         String var3 = var1.getCharacter();
         if (var3.length() != 0) {
            if (!var1.isControlDown() && !var1.isAltDown() && (!PlatformUtil.isMac() || !var1.isMetaDown())
               || (var1.isControlDown() || PlatformUtil.isMac()) && var1.isAltDown()) {
               this.setEditing(true);
               if (var3.charAt(0) > 31 && var3.charAt(0) != 127 && !var1.isMetaDown()) {
                  IndexRange var4 = var2.getSelection();
                  int var5 = var4.getStart();
                  int var6 = var4.getEnd();
                  this.replaceText(var5, var6, var3);
               }

               this.setEditing(false);
            }
         }
      }
   }

   private void invalidateBidi() {
      this.bidi = null;
      this.mixed = null;
      this.rtlText = null;
   }

   private Bidi getBidi() {
      if (this.bidi == null) {
         this.bidi = new Bidi(
            this.textInputControl.textProperty().getValueSafe(), this.textInputControl.getEffectiveNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT ? 1 : 0
         );
      }

      return this.bidi;
   }

   protected boolean isMixed() {
      if (this.mixed == null) {
         this.mixed = this.getBidi().isMixed();
      }

      return this.mixed;
   }

   protected boolean isRTLText() {
      if (this.rtlText == null) {
         Bidi var1 = this.getBidi();
         this.rtlText = var1.isRightToLeft() || this.isMixed() && this.textInputControl.getEffectiveNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
      }

      return this.rtlText;
   }

   private void nextCharacterVisually(boolean var1) {
      if (this.isMixed()) {
         TextInputControlSkin var2 = (TextInputControlSkin)this.textInputControl.getSkin();
         var2.moveCaret(TextInputControlSkin.TextUnit.CHARACTER, var1 ? TextInputControlSkin.Direction.RIGHT : TextInputControlSkin.Direction.LEFT, false);
      } else if (var1 != this.isRTLText()) {
         this.textInputControl.forward();
      } else {
         this.textInputControl.backward();
      }
   }

   private void selectLeft() {
      if (this.isRTLText()) {
         this.textInputControl.selectForward();
      } else {
         this.textInputControl.selectBackward();
      }
   }

   private void selectRight() {
      if (this.isRTLText()) {
         this.textInputControl.selectBackward();
      } else {
         this.textInputControl.selectForward();
      }
   }

   private void deletePreviousChar() {
      this.setEditing(true);
      this.deleteChar(true);
      this.setEditing(false);
   }

   private void deleteNextChar() {
      this.setEditing(true);
      this.deleteChar(false);
      this.setEditing(false);
   }

   protected void deletePreviousWord() {
      this.setEditing(true);
      TextInputControl var1 = this.getNode();
      int var2 = var1.getCaretPosition();
      if (var2 > 0) {
         var1.previousWord();
         int var3 = var1.getCaretPosition();
         this.replaceText(var3, var2, "");
      }

      this.setEditing(false);
   }

   protected void deleteNextWord() {
      this.setEditing(true);
      TextInputControl var1 = this.getNode();
      int var2 = var1.getCaretPosition();
      if (var2 < var1.getLength()) {
         this.nextWord();
         int var3 = var1.getCaretPosition();
         this.replaceText(var2, var3, "");
      }

      this.setEditing(false);
   }

   public void deleteSelection() {
      this.setEditing(true);
      TextInputControl var1 = this.getNode();
      IndexRange var2 = var1.getSelection();
      if (var2.getLength() > 0) {
         this.deleteChar(false);
      }

      this.setEditing(false);
   }

   public void cut() {
      this.setEditing(true);
      this.getNode().cut();
      this.setEditing(false);
   }

   public void paste() {
      this.setEditing(true);
      this.getNode().paste();
      this.setEditing(false);
   }

   public void undo() {
      this.setEditing(true);
      this.getNode().undo();
      this.setEditing(false);
   }

   public void redo() {
      this.setEditing(true);
      this.getNode().redo();
      this.setEditing(false);
   }

   protected void selectPreviousWord() {
      this.getNode().selectPreviousWord();
   }

   public void selectNextWord() {
      TextInputControl var1 = this.getNode();
      if (!PlatformUtil.isMac() && !PlatformUtil.isLinux()) {
         var1.selectNextWord();
      } else {
         var1.selectEndOfNextWord();
      }
   }

   private void selectLeftWord() {
      if (this.isRTLText()) {
         this.selectNextWord();
      } else {
         this.selectPreviousWord();
      }
   }

   private void selectRightWord() {
      if (this.isRTLText()) {
         this.selectPreviousWord();
      } else {
         this.selectNextWord();
      }
   }

   protected void selectWord() {
      TextInputControl var1 = this.getNode();
      var1.previousWord();
      if (PlatformUtil.isWindows()) {
         var1.selectNextWord();
      } else {
         var1.selectEndOfNextWord();
      }

      if (SHOW_HANDLES && this.contextMenu.isShowing()) {
         this.populateContextMenu();
      }
   }

   protected void selectAll() {
      this.getNode().selectAll();
      if (SHOW_HANDLES && this.contextMenu.isShowing()) {
         this.populateContextMenu();
      }
   }

   protected void previousWord() {
      this.getNode().previousWord();
   }

   protected void nextWord() {
      TextInputControl var1 = this.getNode();
      if (!PlatformUtil.isMac() && !PlatformUtil.isLinux()) {
         var1.nextWord();
      } else {
         var1.endOfNextWord();
      }
   }

   private void leftWord() {
      if (this.isRTLText()) {
         this.nextWord();
      } else {
         this.previousWord();
      }
   }

   private void rightWord() {
      if (this.isRTLText()) {
         this.previousWord();
      } else {
         this.nextWord();
      }
   }

   protected void fire(KeyEvent var1) {
   }

   protected void cancelEdit(KeyEvent var1) {
   }

   protected void selectHome() {
      this.getNode().selectHome();
   }

   protected void selectEnd() {
      this.getNode().selectEnd();
   }

   protected void selectHomeExtend() {
      this.getNode().extendSelection(0);
   }

   protected void selectEndExtend() {
      TextInputControl var1 = this.getNode();
      var1.extendSelection(var1.getLength());
   }

   protected void setEditing(boolean var1) {
      this.editing = var1;
   }

   public boolean isEditing() {
      return this.editing;
   }

   protected void populateContextMenu() {
      TextInputControl var1 = this.getNode();
      boolean var2 = var1.isEditable();
      boolean var3 = var1.getLength() > 0;
      boolean var4 = var1.getSelection().getLength() > 0;
      boolean var5 = var1.getSelection().getLength() == var1.getLength();
      boolean var6 = var1 instanceof PasswordField;
      ObservableList var7 = this.contextMenu.getItems();
      if (SHOW_HANDLES) {
         var7.clear();
         if (!var6 && var4) {
            if (var2) {
               var7.add(this.cutMI);
            }

            var7.add(this.copyMI);
         }

         if (var2 && Clipboard.getSystemClipboard().hasString()) {
            var7.add(this.pasteMI);
         }

         if (var3 && !var5) {
            if (!var4 && !(var1 instanceof PasswordField)) {
               var7.add(this.selectWordMI);
            }

            var7.add(this.selectAllMI);
         }

         this.selectWordMI.getProperties().put("refreshMenu", Boolean.TRUE);
         this.selectAllMI.getProperties().put("refreshMenu", Boolean.TRUE);
      } else {
         if (var2) {
            var7.setAll(new MenuItem[]{this.undoMI, this.redoMI, this.cutMI, this.copyMI, this.pasteMI, this.deleteMI, this.separatorMI, this.selectAllMI});
         } else {
            var7.setAll(new MenuItem[]{this.copyMI, this.separatorMI, this.selectAllMI});
         }

         this.undoMI.setDisable(!this.getNode().isUndoable());
         this.redoMI.setDisable(!this.getNode().isRedoable());
         this.cutMI.setDisable(var6 || !var4);
         this.copyMI.setDisable(var6 || !var4);
         this.pasteMI.setDisable(!Clipboard.getSystemClipboard().hasString());
         this.deleteMI.setDisable(!var4);
      }
   }

   private static class ContextMenuItem extends MenuItem {
      ContextMenuItem(String var1, EventHandler<ActionEvent> var2) {
         super(ControlResources.getString("TextInputControl.menu." + var1));
         this.setOnAction(var2);
      }
   }
}
