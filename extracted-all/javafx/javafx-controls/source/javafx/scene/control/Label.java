package javafx.scene.control;

import com.sun.javafx.scene.NodeHelper;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.css.StyleableProperty;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.control.skin.LabelSkin;

public class Label extends Labeled {
   private ChangeListener<Boolean> mnemonicStateListener = (var1, var2, var3) -> NodeHelper.showMnemonicsProperty(this).setValue(var3);
   private ObjectProperty<Node> labelFor;

   public Label() {
      this.initialize();
   }

   public Label(String var1) {
      super(var1);
      this.initialize();
   }

   public Label(String var1, Node var2) {
      super(var1, var2);
      this.initialize();
   }

   private void initialize() {
      this.getStyleClass().setAll(new String[]{"label"});
      this.setAccessibleRole(AccessibleRole.TEXT);
      ((StyleableProperty)this.focusTraversableProperty()).applyStyle(null, Boolean.FALSE);
   }

   public ObjectProperty<Node> labelForProperty() {
      if (this.labelFor == null) {
         this.labelFor = new ObjectPropertyBase<Node>() {
            Node oldValue = null;

            protected void invalidated() {
               if (this.oldValue != null) {
                  NodeHelper.getNodeAccessor().setLabeledBy(this.oldValue, null);
                  NodeHelper.showMnemonicsProperty(this.oldValue).removeListener(Label.this.mnemonicStateListener);
               }

               Node var1 = (Node)this.get();
               if (var1 != null) {
                  NodeHelper.getNodeAccessor().setLabeledBy(var1, Label.this);
                  NodeHelper.showMnemonicsProperty(var1).addListener(Label.this.mnemonicStateListener);
                  NodeHelper.setShowMnemonics(Label.this, NodeHelper.isShowMnemonics(var1));
               } else {
                  NodeHelper.setShowMnemonics(Label.this, false);
               }

               this.oldValue = var1;
            }

            public Object getBean() {
               return Label.this;
            }

            public String getName() {
               return "labelFor";
            }
         };
      }

      return this.labelFor;
   }

   public final void setLabelFor(Node var1) {
      this.labelForProperty().setValue(var1);
   }

   public final Node getLabelFor() {
      return this.labelFor == null ? null : (Node)this.labelFor.getValue();
   }

   @Override
   protected Skin<?> createDefaultSkin() {
      return new LabelSkin(this);
   }

   @Override
   protected Boolean getInitialFocusTraversable() {
      return Boolean.FALSE;
   }
}
