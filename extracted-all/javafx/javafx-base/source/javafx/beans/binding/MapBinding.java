package javafx.beans.binding;

import com.sun.javafx.binding.BindingHelperObserver;
import com.sun.javafx.binding.MapExpressionHelper;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanPropertyBase;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

public abstract class MapBinding<K, V> extends MapExpression<K, V> implements Binding<ObservableMap<K, V>> {
   private final MapChangeListener<K, V> mapChangeListener = new MapChangeListener<K, V>() {
      @Override
      public void onChanged(MapChangeListener.Change<? extends K, ? extends V> var1) {
         MapBinding.this.invalidateProperties();
         MapBinding.this.onInvalidating();
         MapExpressionHelper.fireValueChangedEvent(MapBinding.this.helper, var1);
      }
   };
   private ObservableMap<K, V> value;
   private boolean valid = false;
   private BindingHelperObserver observer;
   private MapExpressionHelper<K, V> helper = null;
   private MapBinding<K, V>.SizeProperty size0;
   private MapBinding<K, V>.EmptyProperty empty0;

   @Override
   public ReadOnlyIntegerProperty sizeProperty() {
      if (this.size0 == null) {
         this.size0 = new MapBinding.SizeProperty();
      }

      return this.size0;
   }

   @Override
   public ReadOnlyBooleanProperty emptyProperty() {
      if (this.empty0 == null) {
         this.empty0 = new MapBinding.EmptyProperty();
      }

      return this.empty0;
   }

   @Override
   public void addListener(InvalidationListener var1) {
      this.helper = MapExpressionHelper.addListener(this.helper, this, var1);
   }

   @Override
   public void removeListener(InvalidationListener var1) {
      this.helper = MapExpressionHelper.removeListener(this.helper, var1);
   }

   @Override
   public void addListener(ChangeListener<? super ObservableMap<K, V>> var1) {
      this.helper = MapExpressionHelper.addListener(this.helper, this, var1);
   }

   @Override
   public void removeListener(ChangeListener<? super ObservableMap<K, V>> var1) {
      this.helper = MapExpressionHelper.removeListener(this.helper, var1);
   }

   @Override
   public void addListener(MapChangeListener<? super K, ? super V> var1) {
      this.helper = MapExpressionHelper.addListener(this.helper, this, var1);
   }

   @Override
   public void removeListener(MapChangeListener<? super K, ? super V> var1) {
      this.helper = MapExpressionHelper.removeListener(this.helper, var1);
   }

   protected final void bind(Observable... var1) {
      if (var1 != null && var1.length > 0) {
         if (this.observer == null) {
            this.observer = new BindingHelperObserver(this);
         }

         for (Observable var5 : var1) {
            if (var5 != null) {
               var5.addListener(this.observer);
            }
         }
      }
   }

   protected final void unbind(Observable... var1) {
      if (this.observer != null) {
         for (Observable var5 : var1) {
            if (var5 != null) {
               var5.removeListener(this.observer);
            }
         }

         this.observer = null;
      }
   }

   @Override
   public void dispose() {
   }

   @Override
   public ObservableList<?> getDependencies() {
      return FXCollections.emptyObservableList();
   }

   public final ObservableMap<K, V> get() {
      if (!this.valid) {
         this.value = this.computeValue();
         this.valid = true;
         if (this.value != null) {
            this.value.addListener(this.mapChangeListener);
         }
      }

      return this.value;
   }

   protected void onInvalidating() {
   }

   private void invalidateProperties() {
      if (this.size0 != null) {
         this.size0.fireValueChangedEvent();
      }

      if (this.empty0 != null) {
         this.empty0.fireValueChangedEvent();
      }
   }

   @Override
   public final void invalidate() {
      if (this.valid) {
         if (this.value != null) {
            this.value.removeListener(this.mapChangeListener);
         }

         this.valid = false;
         this.invalidateProperties();
         this.onInvalidating();
         MapExpressionHelper.fireValueChangedEvent(this.helper);
      }
   }

   @Override
   public final boolean isValid() {
      return this.valid;
   }

   protected abstract ObservableMap<K, V> computeValue();

   @Override
   public String toString() {
      return this.valid ? "MapBinding [value: " + this.get() + "]" : "MapBinding [invalid]";
   }

   private class EmptyProperty extends ReadOnlyBooleanPropertyBase {
      @Override
      public boolean get() {
         return MapBinding.this.isEmpty();
      }

      @Override
      public Object getBean() {
         return MapBinding.this;
      }

      @Override
      public String getName() {
         return "empty";
      }

      @Override
      protected void fireValueChangedEvent() {
         super.fireValueChangedEvent();
      }
   }

   private class SizeProperty extends ReadOnlyIntegerPropertyBase {
      @Override
      public int get() {
         return MapBinding.this.size();
      }

      @Override
      public Object getBean() {
         return MapBinding.this;
      }

      @Override
      public String getName() {
         return "size";
      }

      @Override
      protected void fireValueChangedEvent() {
         super.fireValueChangedEvent();
      }
   }
}
