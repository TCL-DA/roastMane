package com.sun.javafx.sg.prism;

import java.util.ArrayList;
import java.util.List;

public class NodePath {
   private List<NGNode> path = new ArrayList<>();
   private int position;

   public NGNode last() {
      return this.path.isEmpty() ? null : this.path.get(this.path.size() - 1);
   }

   public NGNode getCurrentNode() {
      return this.path.get(this.position);
   }

   public boolean hasNext() {
      return this.position < this.path.size() - 1 && !this.isEmpty();
   }

   public void next() {
      if (!this.hasNext()) {
         throw new IllegalStateException();
      } else {
         this.position++;
      }
   }

   public void reset() {
      this.position = this.path.isEmpty() ? -1 : 0;
   }

   public void clear() {
      this.position = -1;
      this.path.clear();
   }

   public void add(NGNode var1) {
      this.path.add(0, var1);
      if (this.position == -1) {
         this.position = 0;
      }
   }

   public int size() {
      return this.path.size();
   }

   public boolean isEmpty() {
      return this.path.isEmpty();
   }

   @Override
   public String toString() {
      return this.path.toString();
   }
}
