package javafx.scene.control;

import com.sun.javafx.scene.ParentHelper;
import com.sun.javafx.scene.traversal.ParentTraversalEngine;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.css.PseudoClass;
import javafx.css.StyleableProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.control.skin.ToggleButtonSkin;

public class ToggleButton extends ButtonBase implements Toggle {
   private BooleanProperty selected;
   private ObjectProperty<ToggleGroup> toggleGroup;
   private static final String DEFAULT_STYLE_CLASS = "toggle-button";
   private static final PseudoClass PSEUDO_CLASS_SELECTED = PseudoClass.getPseudoClass("selected");

   public ToggleButton() {
      this.initialize();
   }

   public ToggleButton(String var1) {
      this.setText(var1);
      this.initialize();
   }

   public ToggleButton(String var1, Node var2) {
      this.setText(var1);
      this.setGraphic(var2);
      this.initialize();
   }

   private void initialize() {
      this.getStyleClass().setAll(new String[]{"toggle-button"});
      this.setAccessibleRole(AccessibleRole.TOGGLE_BUTTON);
      ((StyleableProperty)this.alignmentProperty()).applyStyle(null, Pos.CENTER);
      this.setMnemonicParsing(true);
   }

   @Override
   public final void setSelected(boolean var1) {
      this.selectedProperty().set(var1);
   }

   @Override
   public final boolean isSelected() {
      return this.selected == null ? false : this.selected.get();
   }

   @Override
   public final BooleanProperty selectedProperty() {
      if (this.selected == null) {
         this.selected = new BooleanPropertyBase() {
            protected void invalidated() {
               boolean var1 = this.get();
               ToggleGroup var2 = ToggleButton.this.getToggleGroup();
               ToggleButton.this.pseudoClassStateChanged(ToggleButton.PSEUDO_CLASS_SELECTED, var1);
               ToggleButton.this.notifyAccessibleAttributeChanged(AccessibleAttribute.SELECTED);
               if (var2 != null) {
                  if (var1) {
                     var2.selectToggle(ToggleButton.this);
                  } else if (var2.getSelectedToggle() == ToggleButton.this) {
                     var2.clearSelectedToggle();
                  }
               }
            }

            public Object getBean() {
               return ToggleButton.this;
            }

            public String getName() {
               return "selected";
            }
         };
      }

      return this.selected;
   }

   @Override
   public final void setToggleGroup(ToggleGroup var1) {
      this.toggleGroupProperty().set(var1);
   }

   @Override
   public final ToggleGroup getToggleGroup() {
      return this.toggleGroup == null ? null : (ToggleGroup)this.toggleGroup.get();
   }

   @Override
   public final ObjectProperty<ToggleGroup> toggleGroupProperty() {
      if (this.toggleGroup == null) {
         this.toggleGroup = new ObjectPropertyBase<ToggleGroup>() {
            private ToggleGroup old;
            private ChangeListener<Toggle> listener = (var1x, var2, var3) -> ParentHelper.getTraversalEngine(ToggleButton.this)
                  .setOverriddenFocusTraversability(var3 != null ? ToggleButton.this.isSelected() : null);

            protected void invalidated() {
               ToggleGroup var1 = (ToggleGroup)this.get();
               if (var1 != null && !var1.getToggles().contains(ToggleButton.this)) {
                  if (this.old != null) {
                     this.old.getToggles().remove(ToggleButton.this);
                  }

                  var1.getToggles().add(ToggleButton.this);
                  ParentTraversalEngine var2 = new ParentTraversalEngine(ToggleButton.this);
                  ParentHelper.setTraversalEngine(ToggleButton.this, var2);
                  var2.setOverriddenFocusTraversability(var1.getSelectedToggle() != null ? ToggleButton.this.isSelected() : null);
                  var1.selectedToggleProperty().addListener(this.listener);
               } else if (var1 == null) {
                  this.old.selectedToggleProperty().removeListener(this.listener);
                  this.old.getToggles().remove(ToggleButton.this);
                  ParentHelper.setTraversalEngine(ToggleButton.this, null);
               }

               this.old = var1;
            }

            public Object getBean() {
               return ToggleButton.this;
            }

            public String getName() {
               return "toggleGroup";
            }
         };
      }

      return this.toggleGroup;
   }

   @Override
   public void fire() {
      if (!this.isDisabled()) {
         this.setSelected(!this.isSelected());
         this.fireEvent(new ActionEvent());
      }
   }

   @Override
   protected Skin<?> createDefaultSkin() {
      return new ToggleButtonSkin(this);
   }

   @Override
   protected Pos getInitialAlignment() {
      return Pos.CENTER;
   }

   @Override
   public Object queryAccessibleAttribute(AccessibleAttribute var1, Object... var2) {
      switch (var1) {
         case SELECTED:
            return this.isSelected();
         default:
            return super.queryAccessibleAttribute(var1, var2);
      }
   }
}
