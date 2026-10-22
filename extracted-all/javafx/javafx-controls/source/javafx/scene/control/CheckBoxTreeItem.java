package javafx.scene.control;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.Node;

public class CheckBoxTreeItem<T> extends TreeItem<T> {
   private static final EventType<? extends Event> CHECK_BOX_SELECTION_CHANGED_EVENT = new EventType(
      CheckBoxTreeItem.TreeModificationEvent.ANY, "checkBoxSelectionChangedEvent"
   );
   private final ChangeListener<Boolean> stateChangeListener = (var1x, var2x, var3x) -> this.updateState();
   private final BooleanProperty selected = new SimpleBooleanProperty(this, "selected", false) {
      protected void invalidated() {
         super.invalidated();
         CheckBoxTreeItem.this.fireEvent(CheckBoxTreeItem.this, true);
      }
   };
   private final BooleanProperty indeterminate = new SimpleBooleanProperty(this, "indeterminate", false) {
      protected void invalidated() {
         super.invalidated();
         CheckBoxTreeItem.this.fireEvent(CheckBoxTreeItem.this, false);
      }
   };
   private final BooleanProperty independent = new SimpleBooleanProperty(this, "independent", false);
   private static boolean updateLock = false;

   public static <T> EventType<CheckBoxTreeItem.TreeModificationEvent<T>> checkBoxSelectionChangedEvent() {
      return (EventType<CheckBoxTreeItem.TreeModificationEvent<T>>)CHECK_BOX_SELECTION_CHANGED_EVENT;
   }

   public CheckBoxTreeItem() {
      this(null);
   }

   public CheckBoxTreeItem(T var1) {
      this((T)var1, null, false);
   }

   public CheckBoxTreeItem(T var1, Node var2) {
      this((T)var1, var2, false);
   }

   public CheckBoxTreeItem(T var1, Node var2, boolean var3) {
      this((T)var1, var2, var3, false);
   }

   public CheckBoxTreeItem(T var1, Node var2, boolean var3, boolean var4) {
      super((T)var1, var2);
      this.setSelected(var3);
      this.setIndependent(var4);
      this.selectedProperty().addListener(this.stateChangeListener);
      this.indeterminateProperty().addListener(this.stateChangeListener);
   }

   public final void setSelected(boolean var1) {
      this.selectedProperty().setValue(var1);
   }

   public final boolean isSelected() {
      return this.selected.getValue();
   }

   public final BooleanProperty selectedProperty() {
      return this.selected;
   }

   public final void setIndeterminate(boolean var1) {
      this.indeterminateProperty().setValue(var1);
   }

   public final boolean isIndeterminate() {
      return this.indeterminate.getValue();
   }

   public final BooleanProperty indeterminateProperty() {
      return this.indeterminate;
   }

   public final BooleanProperty independentProperty() {
      return this.independent;
   }

   public final void setIndependent(boolean var1) {
      this.independentProperty().setValue(var1);
   }

   public final boolean isIndependent() {
      return this.independent.getValue();
   }

   private void updateState() {
      if (!this.isIndependent()) {
         boolean var1 = !updateLock;
         updateLock = true;
         this.updateUpwards();
         if (var1) {
            updateLock = false;
         }

         if (!updateLock) {
            this.updateDownwards();
         }
      }
   }

   private void updateUpwards() {
      if (this.getParent() instanceof CheckBoxTreeItem) {
         CheckBoxTreeItem var1 = (CheckBoxTreeItem)this.getParent();
         int var2 = 0;
         int var3 = 0;

         for (TreeItem var5 : var1.getChildren()) {
            if (var5 instanceof CheckBoxTreeItem) {
               CheckBoxTreeItem var6 = (CheckBoxTreeItem)var5;
               var2 += var6.isSelected() && !var6.isIndeterminate() ? 1 : 0;
               var3 += var6.isIndeterminate() ? 1 : 0;
            }
         }

         if (var2 == var1.getChildren().size()) {
            var1.setSelected(true);
            var1.setIndeterminate(false);
         } else if (var2 == 0 && var3 == 0) {
            var1.setSelected(false);
            var1.setIndeterminate(false);
         } else {
            var1.setIndeterminate(true);
         }
      }
   }

   private void updateDownwards() {
      if (!this.isLeaf()) {
         for (TreeItem var2 : this.getChildren()) {
            if (var2 instanceof CheckBoxTreeItem) {
               CheckBoxTreeItem var3 = (CheckBoxTreeItem)var2;
               var3.setSelected(this.isSelected());
            }
         }
      }
   }

   private void fireEvent(CheckBoxTreeItem<T> var1, boolean var2) {
      CheckBoxTreeItem.TreeModificationEvent var3 = new CheckBoxTreeItem.TreeModificationEvent(CHECK_BOX_SELECTION_CHANGED_EVENT, var1, var2);
      Event.fireEvent(this, var3);
   }

   public static class TreeModificationEvent<T> extends Event {
      private static final long serialVersionUID = -8445355590698862999L;
      private final transient CheckBoxTreeItem<T> treeItem;
      private final boolean selectionChanged;
      public static final EventType<Event> ANY = new EventType(Event.ANY, "TREE_MODIFICATION");

      public TreeModificationEvent(EventType<? extends Event> var1, CheckBoxTreeItem<T> var2, boolean var3) {
         super(var1);
         this.treeItem = var2;
         this.selectionChanged = var3;
      }

      public CheckBoxTreeItem<T> getTreeItem() {
         return this.treeItem;
      }

      public boolean wasSelectionChanged() {
         return this.selectionChanged;
      }

      public boolean wasIndeterminateChanged() {
         return !this.selectionChanged;
      }
   }
}
