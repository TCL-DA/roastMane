package org.apache.commons.math3.geometry.partitioning;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.math3.geometry.Space;

public class NodesSet<S extends Space> implements Iterable<BSPTree<S>> {
   private List<BSPTree<S>> list = new ArrayList<>();

   public void add(BSPTree<S> node) {
      for (BSPTree<S> existing : this.list) {
         if (node == existing) {
            return;
         }
      }

      this.list.add(node);
   }

   public void addAll(Iterable<BSPTree<S>> iterator) {
      for (BSPTree<S> node : iterator) {
         this.add(node);
      }
   }

   @Override
   public Iterator<BSPTree<S>> iterator() {
      return this.list.iterator();
   }
}
