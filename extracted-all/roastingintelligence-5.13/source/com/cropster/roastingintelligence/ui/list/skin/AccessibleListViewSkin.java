package com.cropster.roastingintelligence.ui.list.skin;

import javafx.scene.control.IndexedCell;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.skin.ListViewSkin;
import javafx.scene.control.skin.VirtualFlow;

public class AccessibleListViewSkin<T> extends ListViewSkin<T> {
   public AccessibleListViewSkin(ListView<T> control) {
      super(control);
   }

   public final ScrollBar accessHbar() {
      return this.accessVirtualFlow().accessHbar();
   }

   public final ScrollBar accessVbar() {
      return this.accessVirtualFlow().accessVbar();
   }

   public final AccessibleListViewSkin.AccessibleVirtualFlow<ListCell<T>> accessVirtualFlow() {
      return (AccessibleListViewSkin.AccessibleVirtualFlow<ListCell<T>>)this.getVirtualFlow();
   }

   protected VirtualFlow<ListCell<T>> createVirtualFlow() {
      return new AccessibleListViewSkin.AccessibleVirtualFlow();
   }

   public static class AccessibleVirtualFlow<C extends IndexedCell> extends VirtualFlow<C> {
      public final ScrollBar accessHbar() {
         return this.getHbar();
      }

      public final ScrollBar accessVbar() {
         return this.getVbar();
      }
   }
}
