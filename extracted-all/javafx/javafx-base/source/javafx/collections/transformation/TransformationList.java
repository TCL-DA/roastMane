package javafx.collections.transformation;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.collections.WeakListChangeListener;

public abstract class TransformationList<E, F> extends ObservableListBase<E> implements ObservableList<E> {
   private ObservableList<? extends F> source;
   private ListChangeListener<F> sourceListener;

   protected TransformationList(ObservableList<? extends F> var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.source = var1;
         var1.addListener(new WeakListChangeListener<>(this.getListener()));
      }
   }

   public final ObservableList<? extends F> getSource() {
      return this.source;
   }

   public final boolean isInTransformationChain(ObservableList<?> var1) {
      if (this.source == var1) {
         return true;
      } else {
         ObservableList var2 = this.source;

         while (var2 instanceof TransformationList) {
            var2 = ((TransformationList)var2).source;
            if (var2 == var1) {
               return true;
            }
         }

         return false;
      }
   }

   private ListChangeListener<F> getListener() {
      if (this.sourceListener == null) {
         this.sourceListener = var1 -> this.sourceChanged(var1);
      }

      return this.sourceListener;
   }

   protected abstract void sourceChanged(ListChangeListener.Change<? extends F> var1);

   public abstract int getSourceIndex(int var1);

   public final int getSourceIndexFor(ObservableList<?> var1, int var2) {
      if (!this.isInTransformationChain(var1)) {
         throw new IllegalArgumentException("Provided list is not in the transformation chain of thistransformation list");
      } else {
         ObservableList var3 = this.source;
         int var4 = this.getSourceIndex(var2);

         while (var3 != var1 && var3 instanceof TransformationList) {
            TransformationList var5 = (TransformationList)var3;
            var4 = var5.getSourceIndex(var4);
            var3 = var5.source;
         }

         return var4;
      }
   }

   public abstract int getViewIndex(int var1);
}
