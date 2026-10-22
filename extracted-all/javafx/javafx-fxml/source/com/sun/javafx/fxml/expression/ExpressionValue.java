package com.sun.javafx.fxml.expression;

import com.sun.javafx.fxml.BeanAdapter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ObservableValueBase;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ListChangeListener.Change;

public class ExpressionValue extends ObservableValueBase<Object> {
   private Object namespace;
   private Expression expression;
   private Class<?> type;
   private ArrayList<ExpressionValue.KeyPathMonitor> argumentMonitors;
   private int listenerCount = 0;

   public ExpressionValue(Object var1, Expression var2, Class<?> var3) {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (var2 == null) {
         throw new NullPointerException();
      } else if (var3 == null) {
         throw new NullPointerException();
      } else {
         this.namespace = var1;
         this.expression = var2;
         this.type = var3;
         List var4 = var2.getArguments();
         this.argumentMonitors = new ArrayList<>(var4.size());

         for (KeyPath var6 : var4) {
            this.argumentMonitors.add(new ExpressionValue.KeyPathMonitor(var6.iterator()));
         }
      }
   }

   public Object getValue() {
      return BeanAdapter.coerce(this.expression.evaluate(this.namespace), this.type);
   }

   public void addListener(InvalidationListener var1) {
      if (this.listenerCount == 0) {
         this.monitorArguments();
      }

      super.addListener(var1);
      this.listenerCount++;
   }

   public void removeListener(InvalidationListener var1) {
      super.removeListener(var1);
      this.listenerCount--;
      if (this.listenerCount == 0) {
         this.unmonitorArguments();
      }
   }

   public void addListener(ChangeListener<? super Object> var1) {
      if (this.listenerCount == 0) {
         this.monitorArguments();
      }

      super.addListener(var1);
      this.listenerCount++;
   }

   public void removeListener(ChangeListener<? super Object> var1) {
      super.removeListener(var1);
      this.listenerCount--;
      if (this.listenerCount == 0) {
         this.unmonitorArguments();
      }
   }

   private void monitorArguments() {
      for (ExpressionValue.KeyPathMonitor var2 : this.argumentMonitors) {
         var2.monitor(this.namespace);
      }
   }

   private void unmonitorArguments() {
      for (ExpressionValue.KeyPathMonitor var2 : this.argumentMonitors) {
         var2.unmonitor();
      }
   }

   private class KeyPathMonitor {
      private String key;
      private ExpressionValue.KeyPathMonitor next;
      private Object namespace = null;
      private ListChangeListener<Object> listChangeListener = new ListChangeListener<Object>() {
         public void onChanged(Change<? extends Object> var1) {
            while (var1.next()) {
               int var2 = Integer.parseInt(KeyPathMonitor.this.key);
               if (var2 >= var1.getFrom() && var2 < var1.getTo()) {
                  ExpressionValue.this.fireValueChangedEvent();
                  KeyPathMonitor.this.remonitor();
               }
            }
         }
      };
      private MapChangeListener<String, Object> mapChangeListener = new MapChangeListener<String, Object>() {
         public void onChanged(javafx.collections.MapChangeListener.Change<? extends String, ? extends Object> var1) {
            if (KeyPathMonitor.this.key.equals(var1.getKey())) {
               ExpressionValue.this.fireValueChangedEvent();
               KeyPathMonitor.this.remonitor();
            }
         }
      };
      private ChangeListener<Object> propertyChangeListener = new ChangeListener<Object>() {
         public void changed(ObservableValue<? extends Object> var1, Object var2, Object var3) {
            ExpressionValue.this.fireValueChangedEvent();
            KeyPathMonitor.this.remonitor();
         }
      };

      public KeyPathMonitor(Iterator<String> var2) {
         this.key = (String)var2.next();
         if (var2.hasNext()) {
            this.next = ExpressionValue.this.new KeyPathMonitor(var2);
         } else {
            this.next = null;
         }
      }

      public void monitor(Object var1) {
         if (var1 instanceof ObservableList) {
            ((ObservableList)var1).addListener(this.listChangeListener);
         } else if (var1 instanceof ObservableMap) {
            ((ObservableMap)var1).addListener(this.mapChangeListener);
         } else {
            BeanAdapter var2 = new BeanAdapter(var1);
            ObservableValue var3 = var2.getPropertyModel(this.key);
            if (var3 != null) {
               var3.addListener(this.propertyChangeListener);
            }

            var1 = var2;
         }

         this.namespace = var1;
         if (this.next != null) {
            Object var4 = Expression.get(var1, this.key);
            if (var4 != null) {
               this.next.monitor(var4);
            }
         }
      }

      public void unmonitor() {
         if (this.namespace instanceof ObservableList) {
            ((ObservableList)this.namespace).removeListener(this.listChangeListener);
         } else if (this.namespace instanceof ObservableMap) {
            ((ObservableMap)this.namespace).removeListener(this.mapChangeListener);
         } else if (this.namespace != null) {
            BeanAdapter var1 = (BeanAdapter)this.namespace;
            ObservableValue var2 = var1.getPropertyModel(this.key);
            if (var2 != null) {
               var2.removeListener(this.propertyChangeListener);
            }
         }

         this.namespace = null;
         if (this.next != null) {
            this.next.unmonitor();
         }
      }

      public void remonitor() {
         if (this.next != null) {
            this.next.unmonitor();
            Object var1 = Expression.get(this.namespace, this.key);
            if (var1 != null) {
               this.next.monitor(var1);
            }
         }
      }
   }
}
