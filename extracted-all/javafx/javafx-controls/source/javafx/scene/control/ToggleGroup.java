package javafx.scene.control;

import com.sun.javafx.collections.TrackableObservableList;
import com.sun.javafx.collections.VetoableListDecorator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ListChangeListener.Change;

public class ToggleGroup {
   private final ObservableList<Toggle> toggles = new VetoableListDecorator<Toggle>(new TrackableObservableList<Toggle>() {
      protected void onChanged(Change<Toggle> var1) {
         label47:
         while (var1.next()) {
            List var2 = var1.getAddedSubList();

            for (Toggle var4 : var1.getRemoved()) {
               if (var4.isSelected()) {
                  ToggleGroup.this.selectToggle(null);
               }

               if (!var2.contains(var4)) {
                  var4.setToggleGroup(null);
               }
            }

            for (Toggle var7 : var2) {
               if (!ToggleGroup.this.equals(var7.getToggleGroup())) {
                  if (var7.getToggleGroup() != null) {
                     var7.getToggleGroup().getToggles().remove(var7);
                  }

                  var7.setToggleGroup(ToggleGroup.this);
               }
            }

            Iterator var6 = var2.iterator();

            Toggle var8;
            do {
               if (!var6.hasNext()) {
                  continue label47;
               }

               var8 = (Toggle)var6.next();
            } while (!var8.isSelected());

            ToggleGroup.this.selectToggle(var8);
         }
      }
   }) {
      protected void onProposedChange(List<Toggle> var1, int... var2) {
         for (Toggle var4 : var1) {
            if (var2[0] == 0 && var2[1] == this.size()) {
               break;
            }

            if (ToggleGroup.this.toggles.contains(var4)) {
               throw new IllegalArgumentException("Duplicate toggles are not allow in a ToggleGroup.");
            }
         }
      }
   };
   private final ReadOnlyObjectWrapper<Toggle> selectedToggle = new ReadOnlyObjectWrapper<Toggle>() {
      public void set(Toggle var1) {
         if (this.isBound()) {
            throw new RuntimeException("A bound value cannot be set.");
         } else {
            Toggle var2 = (Toggle)this.get();
            if (var2 != var1) {
               if (ToggleGroup.this.setSelected(var1, true) || var1 != null && var1.getToggleGroup() == ToggleGroup.this || var1 == null) {
                  if (var2 == null || var2.getToggleGroup() == ToggleGroup.this || !var2.isSelected()) {
                     ToggleGroup.this.setSelected(var2, false);
                  }

                  super.set(var1);
               }
            }
         }
      }
   };
   private static final Object USER_DATA_KEY = new Object();
   private ObservableMap<Object, Object> properties;

   public final ObservableList<Toggle> getToggles() {
      return this.toggles;
   }

   public final void selectToggle(Toggle var1) {
      this.selectedToggle.set(var1);
   }

   public final Toggle getSelectedToggle() {
      return (Toggle)this.selectedToggle.get();
   }

   public final ReadOnlyObjectProperty<Toggle> selectedToggleProperty() {
      return this.selectedToggle.getReadOnlyProperty();
   }

   private boolean setSelected(Toggle var1, boolean var2) {
      if (var1 != null && var1.getToggleGroup() == this && !var1.selectedProperty().isBound()) {
         var1.setSelected(var2);
         return true;
      } else {
         return false;
      }
   }

   final void clearSelectedToggle() {
      if (!((Toggle)this.selectedToggle.getValue()).isSelected()) {
         for (Toggle var2 : this.getToggles()) {
            if (var2.isSelected()) {
               return;
            }
         }
      }

      this.selectedToggle.set(null);
   }

   public final ObservableMap<Object, Object> getProperties() {
      if (this.properties == null) {
         this.properties = FXCollections.observableMap(new HashMap());
      }

      return this.properties;
   }

   public boolean hasProperties() {
      return this.properties != null && !this.properties.isEmpty();
   }

   public void setUserData(Object var1) {
      this.getProperties().put(USER_DATA_KEY, var1);
   }

   public Object getUserData() {
      return this.getProperties().get(USER_DATA_KEY);
   }
}
