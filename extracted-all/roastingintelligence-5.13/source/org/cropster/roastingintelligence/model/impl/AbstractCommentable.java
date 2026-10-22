package org.cropster.roastingintelligence.model.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.cropster.roastingintelligence.model.CommentListener;
import org.cropster.roastingintelligence.model.Commentable;
import org.cropster.roastingintelligence.model.CurveModel;
import org.cropster.roastingintelligence.model.Measure;
import org.cropster.roastingintelligence.model.RoastingComment;
import org.cropster.roastingintelligence.util.CollectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractCommentable implements Commentable {
   private static final Logger logger = LoggerFactory.getLogger(AbstractCommentable.class);
   protected final TreeMap<Long, RoastingComment> _comments = new TreeMap<>();
   private final List<CommentListener> listeners = new CopyOnWriteArrayList<>();

   protected abstract CurveModel getPrimaryCurve();

   @Override
   public Measure getTempAtTime(long time) {
      CurveModel beanCurve = this.getPrimaryCurve();
      Long key = CollectionUtil.getProximateKey(beanCurve.getValues(), time, 500L);
      return key != null ? new Measure(beanCurve.getValue(key), beanCurve.getUnit()) : null;
   }

   @Override
   public Long getTimeForTemp(double temp) {
      CurveModel beanCurve = this.getPrimaryCurve();
      if (beanCurve != null && !beanCurve.getValues().isEmpty()) {
         if (beanCurve.getValues() instanceof NavigableMap) {
            NavigableMap<Long, Double> navMap = (NavigableMap<Long, Double>)beanCurve.getValues();
            Long lastKey = navMap.lastKey();

            for (Long key : navMap.descendingKeySet()) {
               Double value = beanCurve.getValue(key);
               if (value <= temp) {
                  return lastKey;
               }

               lastKey = key;
            }

            return navMap.lastKey();
         } else {
            List<Long> keys = new ArrayList<>(beanCurve.getValues().keySet());
            Collections.reverse(keys);
            Long lastKey = keys.get(0);

            for (Long key : keys) {
               Double value = beanCurve.getValue(key);
               if (value <= temp) {
                  return lastKey;
               }

               lastKey = key;
            }

            return keys.get(0);
         }
      } else {
         return null;
      }
   }

   @Override
   public Long getTimeForTemp(long time, double temp) {
      CurveModel beanCurve = this.getPrimaryCurve();
      if (beanCurve == null) {
         return null;
      } else {
         Measure currentTemp = this.getTempAtTime(time);
         List<Long> keys = new ArrayList<>();
         Comparator<Double> c;
         if (currentTemp != null && !(temp < currentTemp.getAmount())) {
            keys.addAll(beanCurve.getValues().tailMap(time).keySet());
            c = (t1, t2) -> t2.compareTo(t1);
         } else {
            keys.addAll(beanCurve.getValues().headMap(time).keySet());
            Collections.reverse(keys);
            c = (t1, t2) -> t1.compareTo(t2);
         }

         for (Long key : keys) {
            Double t = beanCurve.getValue(key);
            if (c.compare(t, temp) <= 0) {
               return key;
            }
         }

         return null;
      }
   }

   @Override
   public Collection<RoastingComment> getComments() {
      return this._comments.values();
   }

   @Override
   public RoastingComment addComment(RoastingComment comment) {
      RoastingComment replacedComment = this._comments.put(comment.getTime(), comment);
      if (replacedComment != null) {
         this.fireCommentRemoved(replacedComment);
      }

      this.fireCommentAdded(comment);
      return replacedComment;
   }

   @Override
   public void removeComment(RoastingComment comment) {
      this._comments.remove(comment.getTime());
      this.fireCommentRemoved(comment);
   }

   @Override
   public void addCommentListener(CommentListener listener) {
      this.listeners.add(listener);
   }

   @Override
   public void removeCommentListener(CommentListener listener) {
      this.listeners.remove(listener);
   }

   protected void fireCommentAdded(RoastingComment comment) {
      try {
         for (CommentListener listener : this.listeners) {
            listener.commentAdded(comment);
         }
      } catch (Exception var4) {
         logger.error("Error dispatching commentAdded to listeners: {}", var4.getMessage());
      }
   }

   @Override
   public void fireCommentUpdated(RoastingComment comment) {
      try {
         for (CommentListener listener : this.listeners) {
            listener.commentUpdated(comment);
         }
      } catch (Exception var4) {
         logger.error("Error dispatching commentUpdated to listeners: {}", var4.getMessage());
      }
   }

   protected void fireCommentRemoved(RoastingComment comment) {
      try {
         for (CommentListener listener : this.listeners) {
            listener.commentRemoved(comment);
         }
      } catch (Exception var4) {
         logger.error("Error dispatching commentRemoved to listeners: {}", var4.getMessage());
      }
   }
}
