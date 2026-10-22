package javafx.collections;

import com.sun.javafx.collections.ListListenerHelper;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javafx.beans.InvalidationListener;

public abstract class ObservableListBase<E> extends AbstractList<E> implements ObservableList<E> {
   private ListListenerHelper<E> listenerHelper;
   private final ListChangeBuilder<E> changeBuilder = new ListChangeBuilder<>(this);

   protected final void nextUpdate(int var1) {
      this.changeBuilder.nextUpdate(var1);
   }

   protected final void nextSet(int var1, E var2) {
      this.changeBuilder.nextSet(var1, (E)var2);
   }

   protected final void nextReplace(int var1, int var2, List<? extends E> var3) {
      this.changeBuilder.nextReplace(var1, var2, var3);
   }

   protected final void nextRemove(int var1, List<? extends E> var2) {
      this.changeBuilder.nextRemove(var1, var2);
   }

   protected final void nextRemove(int var1, E var2) {
      this.changeBuilder.nextRemove(var1, (E)var2);
   }

   protected final void nextPermutation(int var1, int var2, int[] var3) {
      this.changeBuilder.nextPermutation(var1, var2, var3);
   }

   protected final void nextAdd(int var1, int var2) {
      this.changeBuilder.nextAdd(var1, var2);
   }

   protected final void beginChange() {
      this.changeBuilder.beginChange();
   }

   protected final void endChange() {
      this.changeBuilder.endChange();
   }

   @Override
   public final void addListener(InvalidationListener var1) {
      this.listenerHelper = ListListenerHelper.addListener(this.listenerHelper, var1);
   }

   @Override
   public final void removeListener(InvalidationListener var1) {
      this.listenerHelper = ListListenerHelper.removeListener(this.listenerHelper, var1);
   }

   @Override
   public final void addListener(ListChangeListener<? super E> var1) {
      this.listenerHelper = ListListenerHelper.addListener(this.listenerHelper, var1);
   }

   @Override
   public final void removeListener(ListChangeListener<? super E> var1) {
      this.listenerHelper = ListListenerHelper.removeListener(this.listenerHelper, var1);
   }

   protected final void fireChange(ListChangeListener.Change<? extends E> var1) {
      ListListenerHelper.fireValueChangedEvent(this.listenerHelper, var1);
   }

   protected final boolean hasListeners() {
      return ListListenerHelper.hasListeners(this.listenerHelper);
   }

   @Override
   public boolean addAll(E... var1) {
      return this.addAll(Arrays.asList((E[])var1));
   }

   @Override
   public boolean setAll(E... var1) {
      return this.setAll(Arrays.asList((E[])var1));
   }

   @Override
   public boolean setAll(Collection<? extends E> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean removeAll(E... var1) {
      return this.removeAll(Arrays.asList(var1));
   }

   @Override
   public boolean retainAll(E... var1) {
      return this.retainAll(Arrays.asList(var1));
   }

   @Override
   public void remove(int var1, int var2) {
      this.removeRange(var1, var2);
   }
}
