package javafx.scene.control;

import com.sun.javafx.event.EventHandlerManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener.Change;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.scene.Node;

public class TreeItem<T> implements EventTarget {
   private static final EventType<?> TREE_NOTIFICATION_EVENT = new EventType(Event.ANY, "TreeNotificationEvent");
   private static final EventType<?> EXPANDED_ITEM_COUNT_CHANGE_EVENT = new EventType(treeNotificationEvent(), "ExpandedItemCountChangeEvent");
   private static final EventType<?> BRANCH_EXPANDED_EVENT = new EventType(expandedItemCountChangeEvent(), "BranchExpandedEvent");
   private static final EventType<?> BRANCH_COLLAPSED_EVENT = new EventType(expandedItemCountChangeEvent(), "BranchCollapsedEvent");
   private static final EventType<?> CHILDREN_MODIFICATION_EVENT = new EventType(expandedItemCountChangeEvent(), "ChildrenModificationEvent");
   private static final EventType<?> VALUE_CHANGED_EVENT = new EventType(treeNotificationEvent(), "ValueChangedEvent");
   private static final EventType<?> GRAPHIC_CHANGED_EVENT = new EventType(treeNotificationEvent(), "GraphicChangedEvent");
   private final EventHandler<TreeItem.TreeModificationEvent<Object>> itemListener = new EventHandler<TreeItem.TreeModificationEvent<Object>>() {
      public void handle(TreeItem.TreeModificationEvent<Object> var1) {
         TreeItem.this.expandedDescendentCountDirty = true;
      }
   };
   private boolean ignoreSortUpdate = false;
   private boolean expandedDescendentCountDirty = true;
   ObservableList<TreeItem<T>> children;
   private final EventHandlerManager eventHandlerManager = new EventHandlerManager(this);
   private int expandedDescendentCount = 1;
   int previousExpandedDescendentCount = 1;
   Comparator<TreeItem<T>> lastComparator = null;
   TreeSortMode lastSortMode = null;
   private int parentLinkCount = 0;
   private ListChangeListener<TreeItem<T>> childrenListener = var1x -> {
      this.expandedDescendentCountDirty = true;
      this.updateChildren(var1x);
   };
   private ObjectProperty<T> value;
   private ObjectProperty<Node> graphic;
   private BooleanProperty expanded;
   private ReadOnlyBooleanWrapper leaf;
   private ReadOnlyObjectWrapper<TreeItem<T>> parent = new ReadOnlyObjectWrapper(this, "parent");

   public static <T> EventType<TreeItem.TreeModificationEvent<T>> treeNotificationEvent() {
      return (EventType<TreeItem.TreeModificationEvent<T>>)TREE_NOTIFICATION_EVENT;
   }

   public static <T> EventType<TreeItem.TreeModificationEvent<T>> expandedItemCountChangeEvent() {
      return (EventType<TreeItem.TreeModificationEvent<T>>)EXPANDED_ITEM_COUNT_CHANGE_EVENT;
   }

   public static <T> EventType<TreeItem.TreeModificationEvent<T>> branchExpandedEvent() {
      return (EventType<TreeItem.TreeModificationEvent<T>>)BRANCH_EXPANDED_EVENT;
   }

   public static <T> EventType<TreeItem.TreeModificationEvent<T>> branchCollapsedEvent() {
      return (EventType<TreeItem.TreeModificationEvent<T>>)BRANCH_COLLAPSED_EVENT;
   }

   public static <T> EventType<TreeItem.TreeModificationEvent<T>> childrenModificationEvent() {
      return (EventType<TreeItem.TreeModificationEvent<T>>)CHILDREN_MODIFICATION_EVENT;
   }

   public static <T> EventType<TreeItem.TreeModificationEvent<T>> valueChangedEvent() {
      return (EventType<TreeItem.TreeModificationEvent<T>>)VALUE_CHANGED_EVENT;
   }

   public static <T> EventType<TreeItem.TreeModificationEvent<T>> graphicChangedEvent() {
      return (EventType<TreeItem.TreeModificationEvent<T>>)GRAPHIC_CHANGED_EVENT;
   }

   public TreeItem() {
      this(null);
   }

   public TreeItem(T var1) {
      this((T)var1, (Node)null);
   }

   public TreeItem(T var1, Node var2) {
      this.setValue((T)var1);
      this.setGraphic(var2);
      this.addEventHandler(expandedItemCountChangeEvent(), this.itemListener);
   }

   public final void setValue(T var1) {
      this.valueProperty().setValue(var1);
   }

   public final T getValue() {
      return (T)(this.value == null ? null : this.value.getValue());
   }

   public final ObjectProperty<T> valueProperty() {
      if (this.value == null) {
         this.value = new ObjectPropertyBase<T>() {
            protected void invalidated() {
               TreeItem.this.fireEvent(
                  new TreeItem.TreeModificationEvent<>((EventType<? extends Event>)TreeItem.VALUE_CHANGED_EVENT, TreeItem.this, this.get())
               );
            }

            public Object getBean() {
               return TreeItem.this;
            }

            public String getName() {
               return "value";
            }
         };
      }

      return this.value;
   }

   public final void setGraphic(Node var1) {
      this.graphicProperty().setValue(var1);
   }

   public final Node getGraphic() {
      return this.graphic == null ? null : (Node)this.graphic.getValue();
   }

   public final ObjectProperty<Node> graphicProperty() {
      if (this.graphic == null) {
         this.graphic = new ObjectPropertyBase<Node>() {
            protected void invalidated() {
               TreeItem.this.fireEvent(new TreeItem.TreeModificationEvent<>((EventType<? extends Event>)TreeItem.GRAPHIC_CHANGED_EVENT, TreeItem.this));
            }

            public Object getBean() {
               return TreeItem.this;
            }

            public String getName() {
               return "graphic";
            }
         };
      }

      return this.graphic;
   }

   public final void setExpanded(boolean var1) {
      if (var1 || this.expanded != null) {
         this.expandedProperty().setValue(var1);
      }
   }

   public final boolean isExpanded() {
      return this.expanded == null ? false : this.expanded.getValue();
   }

   public final BooleanProperty expandedProperty() {
      if (this.expanded == null) {
         this.expanded = new BooleanPropertyBase() {
            protected void invalidated() {
               if (!TreeItem.this.isLeaf()) {
                  EventType var1 = TreeItem.this.isExpanded() ? TreeItem.BRANCH_EXPANDED_EVENT : TreeItem.BRANCH_COLLAPSED_EVENT;
                  TreeItem.this.fireEvent(new TreeItem.TreeModificationEvent<>(var1, TreeItem.this, TreeItem.this.isExpanded()));
               }
            }

            public Object getBean() {
               return TreeItem.this;
            }

            public String getName() {
               return "expanded";
            }
         };
      }

      return this.expanded;
   }

   private void setLeaf(boolean var1) {
      if (!var1 || this.leaf != null) {
         if (this.leaf == null) {
            this.leaf = new ReadOnlyBooleanWrapper(this, "leaf", true);
         }

         this.leaf.setValue(var1);
      }
   }

   public boolean isLeaf() {
      return this.leaf == null ? true : this.leaf.getValue();
   }

   public final ReadOnlyBooleanProperty leafProperty() {
      if (this.leaf == null) {
         this.leaf = new ReadOnlyBooleanWrapper(this, "leaf", true);
      }

      return this.leaf.getReadOnlyProperty();
   }

   private void setParent(TreeItem<T> var1) {
      this.parent.setValue(var1);
   }

   public final TreeItem<T> getParent() {
      return this.parent == null ? null : (TreeItem)this.parent.getValue();
   }

   public final ReadOnlyObjectProperty<TreeItem<T>> parentProperty() {
      return this.parent.getReadOnlyProperty();
   }

   public ObservableList<TreeItem<T>> getChildren() {
      if (this.children == null) {
         this.children = FXCollections.observableArrayList();
         this.children.addListener(this.childrenListener);
      }

      if (this.children.isEmpty()) {
         return this.children;
      } else {
         if (!this.ignoreSortUpdate) {
            this.checkSortState();
         }

         return this.children;
      }
   }

   public TreeItem<T> previousSibling() {
      return this.previousSibling(this);
   }

   public TreeItem<T> previousSibling(TreeItem<T> var1) {
      if (this.getParent() != null && var1 != null) {
         ObservableList var2 = this.getParent().getChildren();
         int var3 = var2.size();
         int var4 = -1;

         for (int var5 = 0; var5 < var3; var5++) {
            if (var1.equals(var2.get(var5))) {
               var4 = var5 - 1;
               return var4 < 0 ? null : (TreeItem)var2.get(var4);
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public TreeItem<T> nextSibling() {
      return this.nextSibling(this);
   }

   public TreeItem<T> nextSibling(TreeItem<T> var1) {
      if (this.getParent() != null && var1 != null) {
         ObservableList var2 = this.getParent().getChildren();
         int var3 = var2.size();
         int var4 = -1;

         for (int var5 = 0; var5 < var3; var5++) {
            if (var1.equals(var2.get(var5))) {
               var4 = var5 + 1;
               return var4 >= var3 ? null : (TreeItem)var2.get(var4);
            }
         }

         return null;
      } else {
         return null;
      }
   }

   @Override
   public String toString() {
      return "TreeItem [ value: " + this.getValue() + " ]";
   }

   private void fireEvent(TreeItem.TreeModificationEvent<T> var1) {
      Event.fireEvent(this, var1);
   }

   public EventDispatchChain buildEventDispatchChain(EventDispatchChain var1) {
      if (this.getParent() != null) {
         this.getParent().buildEventDispatchChain(var1);
      }

      return var1.append(this.eventHandlerManager);
   }

   public <E extends Event> void addEventHandler(EventType<E> var1, EventHandler<E> var2) {
      this.eventHandlerManager.addEventHandler(var1, var2);
   }

   public <E extends Event> void removeEventHandler(EventType<E> var1, EventHandler<E> var2) {
      this.eventHandlerManager.removeEventHandler(var1, var2);
   }

   void sort() {
      this.sort(this.children, this.lastComparator, this.lastSortMode);
   }

   private void sort(ObservableList<TreeItem<T>> var1, Comparator<TreeItem<T>> var2, TreeSortMode var3) {
      if (var2 != null) {
         this.runSort(var1, var2, var3);
         if (this.getParent() == null) {
            TreeItem.TreeModificationEvent var4 = new TreeItem.TreeModificationEvent<>(childrenModificationEvent(), this);
            var4.wasPermutated = true;
            this.fireEvent(var4);
         }
      }
   }

   private void checkSortState() {
      TreeItem var1 = this.getRoot();
      TreeSortMode var2 = var1.lastSortMode;
      Comparator var3 = var1.lastComparator;
      if (var3 != null && var3 != this.lastComparator) {
         this.lastComparator = var3;
         this.runSort(this.children, var3, var2);
      }
   }

   private void runSort(ObservableList<TreeItem<T>> var1, Comparator<TreeItem<T>> var2, TreeSortMode var3) {
      if (var3 == TreeSortMode.ALL_DESCENDANTS) {
         this.doSort(var1, var2);
      } else if (var3 == TreeSortMode.ONLY_FIRST_LEVEL && this.getParent() == null) {
         this.doSort(var1, var2);
      }
   }

   private TreeItem<T> getRoot() {
      TreeItem var1 = this.getParent();
      if (var1 == null) {
         return this;
      } else {
         while (true) {
            TreeItem var2 = var1.getParent();
            if (var2 == null) {
               return var1;
            }

            var1 = var2;
         }
      }
   }

   private void doSort(ObservableList<TreeItem<T>> var1, Comparator<TreeItem<T>> var2) {
      if (!this.isLeaf() && this.isExpanded()) {
         FXCollections.sort(var1, var2);
      }
   }

   int getExpandedDescendentCount(boolean var1) {
      if (var1 || this.expandedDescendentCountDirty) {
         this.updateExpandedDescendentCount(var1);
         this.expandedDescendentCountDirty = false;
      }

      return this.expandedDescendentCount;
   }

   private void updateExpandedDescendentCount(boolean var1) {
      this.previousExpandedDescendentCount = this.expandedDescendentCount;
      this.expandedDescendentCount = 1;
      this.ignoreSortUpdate = true;
      if (!this.isLeaf() && this.isExpanded()) {
         for (TreeItem var3 : this.getChildren()) {
            if (var3 != null) {
               this.expandedDescendentCount = this.expandedDescendentCount + (var3.isExpanded() ? var3.getExpandedDescendentCount(var1) : 1);
            }
         }
      }

      this.ignoreSortUpdate = false;
   }

   private void updateChildren(Change<? extends TreeItem<T>> var1) {
      this.setLeaf(this.children.isEmpty());
      ArrayList var2 = new ArrayList();
      ArrayList var3 = new ArrayList();

      while (var1.next()) {
         var2.addAll(var1.getAddedSubList());
         var3.addAll(var1.getRemoved());
      }

      updateChildrenParent(var3, null);
      updateChildrenParent(var2, this);
      var1.reset();
      this.fireEvent(new TreeItem.TreeModificationEvent<>((EventType<? extends Event>)CHILDREN_MODIFICATION_EVENT, this, var2, var3, var1));
   }

   private static <T> void updateChildrenParent(List<? extends TreeItem<T>> var0, TreeItem<T> var1) {
      if (var0 != null) {
         for (TreeItem var3 : var0) {
            if (var3 != null) {
               TreeItem var4 = var3.getParent();
               if (var3.parentLinkCount == 0) {
                  var3.setParent(var1);
               }

               boolean var5 = var4 != null && var4.equals(var1);
               if (var5) {
                  if (var1 == null) {
                     var3.parentLinkCount--;
                  } else {
                     var3.parentLinkCount++;
                  }
               }
            }
         }
      }
   }

   public static class TreeModificationEvent<T> extends Event {
      private static final long serialVersionUID = 4741889985221719579L;
      public static final EventType<?> ANY = TreeItem.TREE_NOTIFICATION_EVENT;
      private final transient TreeItem<T> treeItem;
      private final T newValue;
      private final List<? extends TreeItem<T>> added;
      private final List<? extends TreeItem<T>> removed;
      private final Change<? extends TreeItem<T>> change;
      private final boolean wasExpanded;
      private final boolean wasCollapsed;
      private boolean wasPermutated;

      public TreeModificationEvent(EventType<? extends Event> var1, TreeItem<T> var2) {
         this(var1, var2, null);
      }

      public TreeModificationEvent(EventType<? extends Event> var1, TreeItem<T> var2, T var3) {
         super(var1);
         this.treeItem = var2;
         this.newValue = (T)var3;
         this.added = null;
         this.removed = null;
         this.change = null;
         this.wasExpanded = false;
         this.wasCollapsed = false;
      }

      public TreeModificationEvent(EventType<? extends Event> var1, TreeItem<T> var2, boolean var3) {
         super(var1);
         this.treeItem = var2;
         this.newValue = null;
         this.added = null;
         this.removed = null;
         this.change = null;
         this.wasExpanded = var3;
         this.wasCollapsed = !var3;
      }

      public TreeModificationEvent(EventType<? extends Event> var1, TreeItem<T> var2, List<? extends TreeItem<T>> var3, List<? extends TreeItem<T>> var4) {
         this(var1, var2, var3, var4, null);
      }

      private TreeModificationEvent(
         EventType<? extends Event> var1,
         TreeItem<T> var2,
         List<? extends TreeItem<T>> var3,
         List<? extends TreeItem<T>> var4,
         Change<? extends TreeItem<T>> var5
      ) {
         super(var1);
         this.treeItem = var2;
         this.newValue = null;
         this.added = var3;
         this.removed = var4;
         this.change = var5;
         this.wasExpanded = false;
         this.wasCollapsed = false;
         this.wasPermutated = var3 != null && var4 != null && var3.size() == var4.size() && var3.containsAll(var4);
      }

      public TreeItem<T> getSource() {
         return this.treeItem;
      }

      public TreeItem<T> getTreeItem() {
         return this.treeItem;
      }

      public T getNewValue() {
         return this.newValue;
      }

      public List<? extends TreeItem<T>> getAddedChildren() {
         return this.added == null ? Collections.emptyList() : this.added;
      }

      public List<? extends TreeItem<T>> getRemovedChildren() {
         return this.removed == null ? Collections.emptyList() : this.removed;
      }

      public int getRemovedSize() {
         return this.getRemovedChildren().size();
      }

      public int getAddedSize() {
         return this.getAddedChildren().size();
      }

      public boolean wasExpanded() {
         return this.wasExpanded;
      }

      public boolean wasCollapsed() {
         return this.wasCollapsed;
      }

      public boolean wasAdded() {
         return this.getAddedSize() > 0;
      }

      public boolean wasRemoved() {
         return this.getRemovedSize() > 0;
      }

      public boolean wasPermutated() {
         return this.wasPermutated;
      }

      int getFrom() {
         return this.change == null ? -1 : this.change.getFrom();
      }

      int getTo() {
         return this.change == null ? -1 : this.change.getTo();
      }

      Change<? extends TreeItem<T>> getChange() {
         return this.change;
      }
   }
}
