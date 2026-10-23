package org.cropster.roastingintelligence.model;

public interface CommentListener {
   void commentAdded(RoastingComment var1);

   void commentUpdated(RoastingComment var1);

   void commentRemoved(RoastingComment var1);
}
