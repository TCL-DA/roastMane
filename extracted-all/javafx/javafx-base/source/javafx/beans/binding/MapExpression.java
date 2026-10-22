package javafx.beans.binding;

import com.sun.javafx.binding.StringFormatter;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.value.ObservableMapValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

public abstract class MapExpression<K, V> implements ObservableMapValue<K, V> {
   private static final ObservableMap EMPTY_MAP = new MapExpression.EmptyObservableMap();

   public ObservableMap<K, V> getValue() {
      return this.get();
   }

   public static <K, V> MapExpression<K, V> mapExpression(final ObservableMapValue<K, V> var0) {
      if (var0 == null) {
         throw new NullPointerException("Map must be specified.");
      } else {
         return (MapExpression<K, V>)(var0 instanceof MapExpression ? (MapExpression)var0 : new MapBinding<K, V>() {
            {
               super.bind(var0);
            }

            @Override
            public void dispose() {
               super.unbind(var0);
            }

            @Override
            protected ObservableMap<K, V> computeValue() {
               return var0.get();
            }

            @Override
            public ObservableList<?> getDependencies() {
               return FXCollections.singletonObservableList(var0);
            }
         });
      }
   }

   public int getSize() {
      return this.size();
   }

   public abstract ReadOnlyIntegerProperty sizeProperty();

   public abstract ReadOnlyBooleanProperty emptyProperty();

   public ObjectBinding<V> valueAt(K var1) {
      return Bindings.valueAt(this, (K)var1);
   }

   public ObjectBinding<V> valueAt(ObservableValue<K> var1) {
      return Bindings.valueAt(this, var1);
   }

   public BooleanBinding isEqualTo(ObservableMap<?, ?> var1) {
      return Bindings.equal(this, var1);
   }

   public BooleanBinding isNotEqualTo(ObservableMap<?, ?> var1) {
      return Bindings.notEqual(this, var1);
   }

   public BooleanBinding isNull() {
      return Bindings.isNull(this);
   }

   public BooleanBinding isNotNull() {
      return Bindings.isNotNull(this);
   }

   public StringBinding asString() {
      return (StringBinding)StringFormatter.convert(this);
   }

   @Override
   public int size() {
      ObservableMap var1 = this.get();
      return var1 == null ? EMPTY_MAP.size() : var1.size();
   }

   @Override
   public boolean isEmpty() {
      ObservableMap var1 = this.get();
      return var1 == null ? EMPTY_MAP.isEmpty() : var1.isEmpty();
   }

   @Override
   public boolean containsKey(Object var1) {
      ObservableMap var2 = this.get();
      return var2 == null ? EMPTY_MAP.containsKey(var1) : var2.containsKey(var1);
   }

   @Override
   public boolean containsValue(Object var1) {
      ObservableMap var2 = this.get();
      return var2 == null ? EMPTY_MAP.containsValue(var1) : var2.containsValue(var1);
   }

   @Override
   public V put(K var1, V var2) {
      ObservableMap var3 = this.get();
      return (V)(var3 == null ? EMPTY_MAP.put(var1, var2) : var3.put(var1, var2));
   }

   @Override
   public V remove(Object var1) {
      ObservableMap var2 = this.get();
      return (V)(var2 == null ? EMPTY_MAP.remove(var1) : var2.remove(var1));
   }

   @Override
   public void putAll(Map<? extends K, ? extends V> var1) {
      ObservableMap var2 = this.get();
      if (var2 == null) {
         EMPTY_MAP.putAll(var1);
      } else {
         var2.putAll(var1);
      }
   }

   @Override
   public void clear() {
      ObservableMap var1 = this.get();
      if (var1 == null) {
         EMPTY_MAP.clear();
      } else {
         var1.clear();
      }
   }

   @Override
   public Set<K> keySet() {
      ObservableMap var1 = this.get();
      return var1 == null ? EMPTY_MAP.keySet() : var1.keySet();
   }

   @Override
   public Collection<V> values() {
      ObservableMap var1 = this.get();
      return var1 == null ? EMPTY_MAP.values() : var1.values();
   }

   @Override
   public Set<Entry<K, V>> entrySet() {
      ObservableMap var1 = this.get();
      return var1 == null ? EMPTY_MAP.entrySet() : var1.entrySet();
   }

   @Override
   public V get(Object var1) {
      ObservableMap var2 = this.get();
      return (V)(var2 == null ? EMPTY_MAP.get(var1) : var2.get(var1));
   }

   private static class EmptyObservableMap<K, V> extends AbstractMap<K, V> implements ObservableMap<K, V> {
      @Override
      public Set<Entry<K, V>> entrySet() {
         return Collections.emptySet();
      }

      @Override
      public void addListener(MapChangeListener<? super K, ? super V> var1) {
      }

      @Override
      public void removeListener(MapChangeListener<? super K, ? super V> var1) {
      }

      @Override
      public void addListener(InvalidationListener var1) {
      }

      @Override
      public void removeListener(InvalidationListener var1) {
      }
   }
}
