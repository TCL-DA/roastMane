package javafx.scene.control;

import com.sun.javafx.collections.TrackableObservableList;
import java.util.Iterator;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener.Change;
import javafx.css.StyleableProperty;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.skin.AccordionSkin;

public class Accordion extends Control {
   private boolean biasDirty = true;
   private Orientation bias;
   private final ObservableList<TitledPane> panes = new TrackableObservableList<TitledPane>() {
      protected void onChanged(Change<TitledPane> var1) {
         label27:
         while (var1.next()) {
            if (var1.wasRemoved() && !Accordion.this.expandedPane.isBound()) {
               Iterator var2 = var1.getRemoved().iterator();

               TitledPane var3;
               do {
                  if (!var2.hasNext()) {
                     continue label27;
                  }

                  var3 = (TitledPane)var2.next();
               } while (var1.getAddedSubList().contains(var3) || Accordion.this.getExpandedPane() != var3);

               Accordion.this.setExpandedPane(null);
            }
         }
      }
   };
   private ObjectProperty<TitledPane> expandedPane = new ObjectPropertyBase<TitledPane>() {
      private TitledPane oldValue;

      protected void invalidated() {
         TitledPane var1 = (TitledPane)this.get();
         if (var1 != null) {
            var1.setExpanded(true);
         } else if (this.oldValue != null) {
            this.oldValue.setExpanded(false);
         }

         this.oldValue = var1;
      }

      public String getName() {
         return "expandedPane";
      }

      public Object getBean() {
         return Accordion.this;
      }
   };
   private static final String DEFAULT_STYLE_CLASS = "accordion";

   public Accordion() {
      this((TitledPane[])null);
   }

   public Accordion(TitledPane... var1) {
      this.getStyleClass().setAll(new String[]{"accordion"});
      if (var1 != null) {
         this.getPanes().addAll(var1);
      }

      ((StyleableProperty)this.focusTraversableProperty()).applyStyle(null, Boolean.FALSE);
   }

   public final void setExpandedPane(TitledPane var1) {
      this.expandedPaneProperty().set(var1);
   }

   public final TitledPane getExpandedPane() {
      return (TitledPane)this.expandedPane.get();
   }

   public final ObjectProperty<TitledPane> expandedPaneProperty() {
      return this.expandedPane;
   }

   public final ObservableList<TitledPane> getPanes() {
      return this.panes;
   }

   @Override
   protected Skin<?> createDefaultSkin() {
      return new AccordionSkin(this);
   }

   public void requestLayout() {
      this.biasDirty = true;
      this.bias = null;
      super.requestLayout();
   }

   public Orientation getContentBias() {
      if (this.biasDirty) {
         this.bias = null;

         for (Node var3 : this.getManagedChildren()) {
            Orientation var4 = var3.getContentBias();
            if (var4 != null) {
               this.bias = var4;
               if (var4 == Orientation.HORIZONTAL) {
                  break;
               }
            }
         }

         this.biasDirty = false;
      }

      return this.bias;
   }

   @Override
   protected Boolean getInitialFocusTraversable() {
      return Boolean.FALSE;
   }
}
