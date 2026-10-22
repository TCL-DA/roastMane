package org.glassfish.jersey.media.multipart;

import java.util.ArrayList;
import java.util.Collection;

class BodyPartsList extends ArrayList<BodyPart> {
   MultiPart parent = null;

   BodyPartsList(MultiPart parent) {
      this.parent = parent;
   }

   public boolean add(BodyPart bp) {
      super.add(bp);
      bp.setParent(this.parent);
      return true;
   }

   public void add(int index, BodyPart bp) {
      super.add(index, bp);
      bp.setParent(this.parent);
   }

   @Override
   public boolean addAll(Collection<? extends BodyPart> bps) {
      if (!super.addAll(bps)) {
         return false;
      } else {
         for (BodyPart bp : bps) {
            bp.setParent(this.parent);
         }

         return true;
      }
   }

   @Override
   public boolean addAll(int index, Collection<? extends BodyPart> bps) {
      if (!super.addAll(index, bps)) {
         return false;
      } else {
         for (BodyPart bp : bps) {
            bp.setParent(this.parent);
         }

         return true;
      }
   }

   @Override
   public void clear() {
      BodyPart[] bps = super.toArray(new BodyPart[super.size()]);
      super.clear();

      for (BodyPart bp : bps) {
         bp.setParent(null);
      }
   }

   public BodyPart remove(int index) {
      BodyPart bp = (BodyPart)super.remove(index);
      if (bp != null) {
         bp.setParent(null);
      }

      return bp;
   }

   @Override
   public boolean remove(Object bp) {
      if (super.remove(bp)) {
         ((BodyPart)bp).setParent(null);
         return true;
      } else {
         return false;
      }
   }
}
