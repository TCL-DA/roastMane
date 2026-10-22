package com.sun.javafx.scene.control.behavior;

import com.sun.javafx.scene.control.inputmap.InputMap;
import com.sun.javafx.scene.control.inputmap.KeyBinding;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Accordion;
import javafx.scene.control.FocusModel;
import javafx.scene.control.TitledPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class AccordionBehavior extends BehaviorBase<Accordion> {
   private final InputMap<Accordion> inputMap;
   private AccordionBehavior.AccordionFocusModel focusModel;

   public AccordionBehavior(Accordion var1) {
      super(var1);
      this.focusModel = new AccordionBehavior.AccordionFocusModel(var1);
      this.inputMap = this.createInputMap();
      this.addDefaultMapping(
         this.inputMap,
         new InputMap.Mapping[]{
            new InputMap.KeyMapping(KeyCode.UP, var1x -> this.pageUp(false)),
            new InputMap.KeyMapping(KeyCode.DOWN, var1x -> this.pageDown(false)),
            new InputMap.KeyMapping(KeyCode.LEFT, var2 -> {
               if (this.isRTL(var1)) {
                  this.pageDown(false);
               } else {
                  this.pageUp(false);
               }
            }),
            new InputMap.KeyMapping(KeyCode.RIGHT, var2 -> {
               if (this.isRTL(var1)) {
                  this.pageUp(false);
               } else {
                  this.pageDown(false);
               }
            }),
            new InputMap.KeyMapping(KeyCode.HOME, this::home),
            new InputMap.KeyMapping(KeyCode.END, this::end),
            new InputMap.KeyMapping(KeyCode.PAGE_UP, var1x -> this.pageUp(true)),
            new InputMap.KeyMapping(KeyCode.PAGE_DOWN, var1x -> this.pageDown(true)),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.PAGE_UP).ctrl(), this::moveBackward),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.PAGE_DOWN).ctrl(), this::moveForward),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.TAB).ctrl(), this::moveForward),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.TAB).ctrl().shift(), this::moveBackward),
            new InputMap.MouseMapping(MouseEvent.MOUSE_PRESSED, this::mousePressed)
         }
      );
   }

   @Override
   public void dispose() {
      this.focusModel.dispose();
      super.dispose();
   }

   @Override
   public InputMap<Accordion> getInputMap() {
      return this.inputMap;
   }

   private void pageUp(boolean var1) {
      Accordion var2 = this.getNode();
      if (this.focusModel.getFocusedIndex() != -1 && ((TitledPane)var2.getPanes().get(this.focusModel.getFocusedIndex())).isFocused()) {
         this.focusModel.focusPrevious();
         int var3 = this.focusModel.getFocusedIndex();
         ((TitledPane)var2.getPanes().get(var3)).requestFocus();
         if (var1) {
            ((TitledPane)var2.getPanes().get(var3)).setExpanded(true);
         }
      }
   }

   private void pageDown(boolean var1) {
      Accordion var2 = this.getNode();
      if (this.focusModel.getFocusedIndex() != -1 && ((TitledPane)var2.getPanes().get(this.focusModel.getFocusedIndex())).isFocused()) {
         this.focusModel.focusNext();
         int var3 = this.focusModel.getFocusedIndex();
         ((TitledPane)var2.getPanes().get(var3)).requestFocus();
         if (var1) {
            ((TitledPane)var2.getPanes().get(var3)).setExpanded(true);
         }
      }
   }

   private void moveBackward(KeyEvent var1) {
      Accordion var2 = this.getNode();
      this.focusModel.focusPrevious();
      if (this.focusModel.getFocusedIndex() != -1) {
         int var3 = this.focusModel.getFocusedIndex();
         ((TitledPane)var2.getPanes().get(var3)).requestFocus();
         ((TitledPane)var2.getPanes().get(var3)).setExpanded(true);
      }
   }

   private void moveForward(KeyEvent var1) {
      Accordion var2 = this.getNode();
      this.focusModel.focusNext();
      if (this.focusModel.getFocusedIndex() != -1) {
         int var3 = this.focusModel.getFocusedIndex();
         ((TitledPane)var2.getPanes().get(var3)).requestFocus();
         ((TitledPane)var2.getPanes().get(var3)).setExpanded(true);
      }
   }

   private void home(KeyEvent var1) {
      Accordion var2 = this.getNode();
      if (this.focusModel.getFocusedIndex() != -1 && ((TitledPane)var2.getPanes().get(this.focusModel.getFocusedIndex())).isFocused()) {
         TitledPane var3 = (TitledPane)var2.getPanes().get(0);
         var3.requestFocus();
         var3.setExpanded(!var3.isExpanded());
      }
   }

   private void end(KeyEvent var1) {
      Accordion var2 = this.getNode();
      if (this.focusModel.getFocusedIndex() != -1 && ((TitledPane)var2.getPanes().get(this.focusModel.getFocusedIndex())).isFocused()) {
         TitledPane var3 = (TitledPane)var2.getPanes().get(var2.getPanes().size() - 1);
         var3.requestFocus();
         var3.setExpanded(!var3.isExpanded());
      }
   }

   public void mousePressed(MouseEvent var1) {
      Accordion var2 = this.getNode();
      if (var2.getPanes().size() > 0) {
         TitledPane var3 = (TitledPane)var2.getPanes().get(var2.getPanes().size() - 1);
         var3.requestFocus();
      } else {
         var2.requestFocus();
      }
   }

   static class AccordionFocusModel extends FocusModel<TitledPane> {
      private final Accordion accordion;
      private final ChangeListener<Boolean> focusListener = new ChangeListener<Boolean>() {
         public void changed(ObservableValue<? extends Boolean> var1, Boolean var2, Boolean var3) {
            if (var3) {
               if (AccordionFocusModel.this.accordion.getExpandedPane() != null) {
                  AccordionFocusModel.this.accordion.getExpandedPane().requestFocus();
               } else if (!AccordionFocusModel.this.accordion.getPanes().isEmpty()) {
                  ((TitledPane)AccordionFocusModel.this.accordion.getPanes().get(0)).requestFocus();
               }
            }
         }
      };
      private final ChangeListener<Boolean> paneFocusListener = new ChangeListener<Boolean>() {
         public void changed(ObservableValue<? extends Boolean> var1, Boolean var2, Boolean var3) {
            if (var3) {
               ReadOnlyBooleanProperty var4 = (ReadOnlyBooleanProperty)var1;
               TitledPane var5 = (TitledPane)var4.getBean();
               AccordionFocusModel.this.focus(AccordionFocusModel.this.accordion.getPanes().indexOf(var5));
            }
         }
      };
      private final ListChangeListener<TitledPane> panesListener = var1x -> {
         while (var1x.next()) {
            if (var1x.wasAdded()) {
               for (TitledPane var5 : var1x.getAddedSubList()) {
                  var5.focusedProperty().addListener(this.paneFocusListener);
               }
            } else if (var1x.wasRemoved()) {
               for (TitledPane var3x : var1x.getAddedSubList()) {
                  var3x.focusedProperty().removeListener(this.paneFocusListener);
               }
            }
         }
      };

      public AccordionFocusModel(Accordion var1) {
         if (var1 == null) {
            throw new IllegalArgumentException("Accordion can not be null");
         } else {
            this.accordion = var1;
            this.accordion.focusedProperty().addListener(this.focusListener);
            this.accordion.getPanes().addListener(this.panesListener);

            for (TitledPane var3 : this.accordion.getPanes()) {
               var3.focusedProperty().addListener(this.paneFocusListener);
            }
         }
      }

      void dispose() {
         this.accordion.focusedProperty().removeListener(this.focusListener);
         this.accordion.getPanes().removeListener(this.panesListener);

         for (TitledPane var2 : this.accordion.getPanes()) {
            var2.focusedProperty().removeListener(this.paneFocusListener);
         }
      }

      @Override
      protected int getItemCount() {
         ObservableList var1 = this.accordion.getPanes();
         return var1 == null ? 0 : var1.size();
      }

      protected TitledPane getModelItem(int var1) {
         ObservableList var2 = this.accordion.getPanes();
         if (var2 == null) {
            return null;
         } else {
            return var1 < 0 ? null : (TitledPane)var2.get(var1 % var2.size());
         }
      }

      @Override
      public void focusPrevious() {
         if (this.getFocusedIndex() <= 0) {
            this.focus(this.accordion.getPanes().size() - 1);
         } else {
            this.focus((this.getFocusedIndex() - 1) % this.accordion.getPanes().size());
         }
      }

      @Override
      public void focusNext() {
         if (this.getFocusedIndex() == -1) {
            this.focus(0);
         } else {
            this.focus((this.getFocusedIndex() + 1) % this.accordion.getPanes().size());
         }
      }
   }
}
