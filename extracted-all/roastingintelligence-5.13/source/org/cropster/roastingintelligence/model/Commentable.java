package org.cropster.roastingintelligence.model;

import java.util.Collection;

public interface Commentable {
   Long getDuration();

   Measure getTempAtTime(long var1);

   Long getTimeForTemp(double var1);

   Long getTimeForTemp(long var1, double var3);

   Collection<RoastingComment> getComments();

   RoastingComment addComment(RoastingComment var1);

   void removeComment(RoastingComment var1);

   void fireCommentUpdated(RoastingComment var1);

   void addCommentListener(CommentListener var1);

   void removeCommentListener(CommentListener var1);
}
