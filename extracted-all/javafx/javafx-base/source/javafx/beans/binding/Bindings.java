package javafx.beans.binding;

import com.sun.javafx.binding.BidirectionalBinding;
import com.sun.javafx.binding.BidirectionalContentBinding;
import com.sun.javafx.binding.ContentBinding;
import com.sun.javafx.binding.DoubleConstant;
import com.sun.javafx.binding.FloatConstant;
import com.sun.javafx.binding.IntegerConstant;
import com.sun.javafx.binding.Logging;
import com.sun.javafx.binding.LongConstant;
import com.sun.javafx.binding.ObjectConstant;
import com.sun.javafx.binding.SelectBinding;
import com.sun.javafx.binding.StringConstant;
import com.sun.javafx.binding.StringFormatter;
import com.sun.javafx.collections.ImmutableObservableList;
import java.lang.ref.WeakReference;
import java.text.Format;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableDoubleValue;
import javafx.beans.value.ObservableFloatValue;
import javafx.beans.value.ObservableIntegerValue;
import javafx.beans.value.ObservableLongValue;
import javafx.beans.value.ObservableNumberValue;
import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.ObservableStringValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableFloatArray;
import javafx.collections.ObservableIntegerArray;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;
import javafx.util.StringConverter;

public final class Bindings {
   private Bindings() {
   }

   public static BooleanBinding createBooleanBinding(final Callable<Boolean> var0, final Observable... var1) {
      return new BooleanBinding() {
         {
            this.bind(var1);
         }

         @Override
         protected boolean computeValue() {
            try {
               return (Boolean)var0.call();
            } catch (Exception var2) {
               Logging.getLogger().warning("Exception while evaluating binding", var2);
               return false;
            }
         }

         @Override
         public void dispose() {
            super.unbind(var1);
         }

         @Override
         public ObservableList<?> getDependencies() {
            return (ObservableList<?>)(var1 != null && var1.length != 0
               ? (var1.length == 1 ? FXCollections.singletonObservableList(var1[0]) : new ImmutableObservableList<>(var1))
               : FXCollections.emptyObservableList());
         }
      };
   }

   public static DoubleBinding createDoubleBinding(final Callable<Double> var0, final Observable... var1) {
      return new DoubleBinding() {
         {
            this.bind(var1);
         }

         @Override
         protected double computeValue() {
            try {
               return (Double)var0.call();
            } catch (Exception var2) {
               Logging.getLogger().warning("Exception while evaluating binding", var2);
               return 0.0;
            }
         }

         @Override
         public void dispose() {
            super.unbind(var1);
         }

         @Override
         public ObservableList<?> getDependencies() {
            return (ObservableList<?>)(var1 != null && var1.length != 0
               ? (var1.length == 1 ? FXCollections.singletonObservableList(var1[0]) : new ImmutableObservableList<>(var1))
               : FXCollections.emptyObservableList());
         }
      };
   }

   public static FloatBinding createFloatBinding(final Callable<Float> var0, final Observable... var1) {
      return new FloatBinding() {
         {
            this.bind(var1);
         }

         @Override
         protected float computeValue() {
            try {
               return (Float)var0.call();
            } catch (Exception var2) {
               Logging.getLogger().warning("Exception while evaluating binding", var2);
               return 0.0F;
            }
         }

         @Override
         public void dispose() {
            super.unbind(var1);
         }

         @Override
         public ObservableList<?> getDependencies() {
            return (ObservableList<?>)(var1 != null && var1.length != 0
               ? (var1.length == 1 ? FXCollections.singletonObservableList(var1[0]) : new ImmutableObservableList<>(var1))
               : FXCollections.emptyObservableList());
         }
      };
   }

   public static IntegerBinding createIntegerBinding(final Callable<Integer> var0, final Observable... var1) {
      return new IntegerBinding() {
         {
            this.bind(var1);
         }

         @Override
         protected int computeValue() {
            try {
               return (Integer)var0.call();
            } catch (Exception var2) {
               Logging.getLogger().warning("Exception while evaluating binding", var2);
               return 0;
            }
         }

         @Override
         public void dispose() {
            super.unbind(var1);
         }

         @Override
         public ObservableList<?> getDependencies() {
            return (ObservableList<?>)(var1 != null && var1.length != 0
               ? (var1.length == 1 ? FXCollections.singletonObservableList(var1[0]) : new ImmutableObservableList<>(var1))
               : FXCollections.emptyObservableList());
         }
      };
   }

   public static LongBinding createLongBinding(final Callable<Long> var0, final Observable... var1) {
      return new LongBinding() {
         {
            this.bind(var1);
         }

         @Override
         protected long computeValue() {
            try {
               return (Long)var0.call();
            } catch (Exception var2) {
               Logging.getLogger().warning("Exception while evaluating binding", var2);
               return 0L;
            }
         }

         @Override
         public void dispose() {
            super.unbind(var1);
         }

         @Override
         public ObservableList<?> getDependencies() {
            return (ObservableList<?>)(var1 != null && var1.length != 0
               ? (var1.length == 1 ? FXCollections.singletonObservableList(var1[0]) : new ImmutableObservableList<>(var1))
               : FXCollections.emptyObservableList());
         }
      };
   }

   public static <T> ObjectBinding<T> createObjectBinding(final Callable<T> var0, final Observable... var1) {
      return new ObjectBinding<T>() {
         {
            this.bind(var1);
         }

         @Override
         protected T computeValue() {
            try {
               return (T)var0.call();
            } catch (Exception var2) {
               Logging.getLogger().warning("Exception while evaluating binding", var2);
               return null;
            }
         }

         @Override
         public void dispose() {
            super.unbind(var1);
         }

         @Override
         public ObservableList<?> getDependencies() {
            return (ObservableList<?>)(var1 != null && var1.length != 0
               ? (var1.length == 1 ? FXCollections.singletonObservableList(var1[0]) : new ImmutableObservableList<>(var1))
               : FXCollections.emptyObservableList());
         }
      };
   }

   public static StringBinding createStringBinding(final Callable<String> var0, final Observable... var1) {
      return new StringBinding() {
         {
            this.bind(var1);
         }

         @Override
         protected String computeValue() {
            try {
               return (String)var0.call();
            } catch (Exception var2) {
               Logging.getLogger().warning("Exception while evaluating binding", var2);
               return "";
            }
         }

         @Override
         public void dispose() {
            super.unbind(var1);
         }

         @Override
         public ObservableList<?> getDependencies() {
            return (ObservableList<?>)(var1 != null && var1.length != 0
               ? (var1.length == 1 ? FXCollections.singletonObservableList(var1[0]) : new ImmutableObservableList<>(var1))
               : FXCollections.emptyObservableList());
         }
      };
   }

   public static <T> ObjectBinding<T> select(ObservableValue<?> var0, String... var1) {
      return new SelectBinding.AsObject<>(var0, var1);
   }

   public static DoubleBinding selectDouble(ObservableValue<?> var0, String... var1) {
      return new SelectBinding.AsDouble(var0, var1);
   }

   public static FloatBinding selectFloat(ObservableValue<?> var0, String... var1) {
      return new SelectBinding.AsFloat(var0, var1);
   }

   public static IntegerBinding selectInteger(ObservableValue<?> var0, String... var1) {
      return new SelectBinding.AsInteger(var0, var1);
   }

   public static LongBinding selectLong(ObservableValue<?> var0, String... var1) {
      return new SelectBinding.AsLong(var0, var1);
   }

   public static BooleanBinding selectBoolean(ObservableValue<?> var0, String... var1) {
      return new SelectBinding.AsBoolean(var0, var1);
   }

   public static StringBinding selectString(ObservableValue<?> var0, String... var1) {
      return new SelectBinding.AsString(var0, var1);
   }

   public static <T> ObjectBinding<T> select(Object var0, String... var1) {
      return new SelectBinding.AsObject<>(var0, var1);
   }

   public static DoubleBinding selectDouble(Object var0, String... var1) {
      return new SelectBinding.AsDouble(var0, var1);
   }

   public static FloatBinding selectFloat(Object var0, String... var1) {
      return new SelectBinding.AsFloat(var0, var1);
   }

   public static IntegerBinding selectInteger(Object var0, String... var1) {
      return new SelectBinding.AsInteger(var0, var1);
   }

   public static LongBinding selectLong(Object var0, String... var1) {
      return new SelectBinding.AsLong(var0, var1);
   }

   public static BooleanBinding selectBoolean(Object var0, String... var1) {
      return new SelectBinding.AsBoolean(var0, var1);
   }

   public static StringBinding selectString(Object var0, String... var1) {
      return new SelectBinding.AsString(var0, var1);
   }

   public static When when(ObservableBooleanValue var0) {
      return new When(var0);
   }

   public static <T> void bindBidirectional(Property<T> var0, Property<T> var1) {
      BidirectionalBinding.bind(var0, var1);
   }

   public static <T> void unbindBidirectional(Property<T> var0, Property<T> var1) {
      BidirectionalBinding.unbind(var0, var1);
   }

   public static void unbindBidirectional(Object var0, Object var1) {
      BidirectionalBinding.unbind(var0, var1);
   }

   public static void bindBidirectional(Property<String> var0, Property<?> var1, Format var2) {
      BidirectionalBinding.bind(var0, var1, var2);
   }

   public static <T> void bindBidirectional(Property<String> var0, Property<T> var1, StringConverter<T> var2) {
      BidirectionalBinding.bind(var0, var1, var2);
   }

   public static <E> void bindContentBidirectional(ObservableList<E> var0, ObservableList<E> var1) {
      BidirectionalContentBinding.bind(var0, var1);
   }

   public static <E> void bindContentBidirectional(ObservableSet<E> var0, ObservableSet<E> var1) {
      BidirectionalContentBinding.bind(var0, var1);
   }

   public static <K, V> void bindContentBidirectional(ObservableMap<K, V> var0, ObservableMap<K, V> var1) {
      BidirectionalContentBinding.bind(var0, var1);
   }

   public static void unbindContentBidirectional(Object var0, Object var1) {
      BidirectionalContentBinding.unbind(var0, var1);
   }

   public static <E> void bindContent(List<E> var0, ObservableList<? extends E> var1) {
      ContentBinding.bind(var0, var1);
   }

   public static <E> void bindContent(Set<E> var0, ObservableSet<? extends E> var1) {
      ContentBinding.bind(var0, var1);
   }

   public static <K, V> void bindContent(Map<K, V> var0, ObservableMap<? extends K, ? extends V> var1) {
      ContentBinding.bind(var0, var1);
   }

   public static void unbindContent(Object var0, Object var1) {
      ContentBinding.unbind(var0, var1);
   }

   public static NumberBinding negate(final ObservableNumberValue var0) {
      if (var0 == null) {
         throw new NullPointerException("Operand cannot be null.");
      } else if (var0 instanceof ObservableDoubleValue) {
         return new DoubleBinding() {
            {
               super.bind(var0);
            }

            @Override
            public void dispose() {
               super.unbind(var0);
            }

            @Override
            protected double computeValue() {
               return -var0.doubleValue();
            }

            @Override
            public ObservableList<?> getDependencies() {
               return FXCollections.singletonObservableList(var0);
            }
         };
      } else if (var0 instanceof ObservableFloatValue) {
         return new FloatBinding() {
            {
               super.bind(var0);
            }

            @Override
            public void dispose() {
               super.unbind(var0);
            }

            @Override
            protected float computeValue() {
               return -var0.floatValue();
            }

            @Override
            public ObservableList<?> getDependencies() {
               return FXCollections.singletonObservableList(var0);
            }
         };
      } else {
         return (NumberBinding)(var0 instanceof ObservableLongValue ? new LongBinding() {
            {
               super.bind(var0);
            }

            @Override
            public void dispose() {
               super.unbind(var0);
            }

            @Override
            protected long computeValue() {
               return -var0.longValue();
            }

            @Override
            public ObservableList<?> getDependencies() {
               return FXCollections.singletonObservableList(var0);
            }
         } : new IntegerBinding() {
            {
               super.bind(var0);
            }

            @Override
            public void dispose() {
               super.unbind(var0);
            }

            @Override
            protected int computeValue() {
               return -var0.intValue();
            }

            @Override
            public ObservableList<?> getDependencies() {
               return FXCollections.singletonObservableList(var0);
            }
         });
      }
   }

   private static NumberBinding add(final ObservableNumberValue var0, final ObservableNumberValue var1, final Observable... var2) {
      if (var0 != null && var1 != null) {
         assert var2 != null && var2.length > 0;

         if (var0 instanceof ObservableDoubleValue || var1 instanceof ObservableDoubleValue) {
            return new DoubleBinding() {
               {
                  super.bind(var2);
               }

               @Override
               public void dispose() {
                  super.unbind(var2);
               }

               @Override
               protected double computeValue() {
                  return var0.doubleValue() + var1.doubleValue();
               }

               @Override
               public ObservableList<?> getDependencies() {
                  return (ObservableList<?>)(var2.length == 1 ? FXCollections.singletonObservableList(var2[0]) : new ImmutableObservableList<>(var2));
               }
            };
         } else if (var0 instanceof ObservableFloatValue || var1 instanceof ObservableFloatValue) {
            return new FloatBinding() {
               {
                  super.bind(var2);
               }

               @Override
               public void dispose() {
                  super.unbind(var2);
               }

               @Override
               protected float computeValue() {
                  return var0.floatValue() + var1.floatValue();
               }

               @Override
               public ObservableList<?> getDependencies() {
                  return (ObservableList<?>)(var2.length == 1 ? FXCollections.singletonObservableList(var2[0]) : new ImmutableObservableList<>(var2));
               }
            };
         } else {
            return (NumberBinding)(!(var0 instanceof ObservableLongValue) && !(var1 instanceof ObservableLongValue) ? new IntegerBinding() {
               {
                  super.bind(var2);
               }

               @Override
               public void dispose() {
                  super.unbind(var2);
               }

               @Override
               protected int computeValue() {
                  return var0.intValue() + var1.intValue();
               }

               @Override
               public ObservableList<?> getDependencies() {
                  return (ObservableList<?>)(var2.length == 1 ? FXCollections.singletonObservableList(var2[0]) : new ImmutableObservableList<>(var2));
               }
            } : new LongBinding() {
               {
                  super.bind(var2);
               }

               @Override
               public void dispose() {
                  super.unbind(var2);
               }

               @Override
               protected long computeValue() {
                  return var0.longValue() + var1.longValue();
               }

               @Override
               public ObservableList<?> getDependencies() {
                  return (ObservableList<?>)(var2.length == 1 ? FXCollections.singletonObservableList(var2[0]) : new ImmutableObservableList<>(var2));
               }
            });
         }
      } else {
         throw new NullPointerException("Operands cannot be null.");
      }
   }

   public static NumberBinding add(ObservableNumberValue var0, ObservableNumberValue var1) {
      return add(var0, var1, var0, var1);
   }

   public static DoubleBinding add(ObservableNumberValue var0, double var1) {
      return (DoubleBinding)add(var0, DoubleConstant.valueOf(var1), var0);
   }

   public static DoubleBinding add(double var0, ObservableNumberValue var2) {
      return (DoubleBinding)add(DoubleConstant.valueOf(var0), var2, var2);
   }

   public static NumberBinding add(ObservableNumberValue var0, float var1) {
      return add(var0, FloatConstant.valueOf(var1), var0);
   }

   public static NumberBinding add(float var0, ObservableNumberValue var1) {
      return add(FloatConstant.valueOf(var0), var1, var1);
   }

   public static NumberBinding add(ObservableNumberValue var0, long var1) {
      return add(var0, LongConstant.valueOf(var1), var0);
   }

   public static NumberBinding add(long var0, ObservableNumberValue var2) {
      return add(LongConstant.valueOf(var0), var2, var2);
   }

   public static NumberBinding add(ObservableNumberValue var0, int var1) {
      return add(var0, IntegerConstant.valueOf(var1), var0);
   }

   public static NumberBinding add(int var0, ObservableNumberValue var1) {
      return add(IntegerConstant.valueOf(var0), var1, var1);
   }

   private static NumberBinding subtract(final ObservableNumberValue var0, final ObservableNumberValue var1, final Observable... var2) {
      if (var0 != null && var1 != null) {
         assert var2 != null && var2.length > 0;

         if (var0 instanceof ObservableDoubleValue || var1 instanceof ObservableDoubleValue) {
            return new DoubleBinding() {
               {
                  super.bind(var2);
               }

               @Override
               public void dispose() {
                  super.unbind(var2);
               }

               @Override
               protected double computeValue() {
                  return var0.doubleValue() - var1.doubleValue();
               }

               @Override
               public ObservableList<?> getDependencies() {
                  return (ObservableList<?>)(var2.length == 1 ? FXCollections.singletonObservableList(var2[0]) : new ImmutableObservableList<>(var2));
               }
            };
         } else if (var0 instanceof ObservableFloatValue || var1 instanceof ObservableFloatValue) {
            return new FloatBinding() {
               {
                  super.bind(var2);
               }

               @Override
               public void dispose() {
                  super.unbind(var2);
               }

               @Override
               protected float computeValue() {
                  return var0.floatValue() - var1.floatValue();
               }

               @Override
               public ObservableList<?> getDependencies() {
                  return (ObservableList<?>)(var2.length == 1 ? FXCollections.singletonObservableList(var2[0]) : new ImmutableObservableList<>(var2));
               }
            };
         } else {
            return (NumberBinding)(!(var0 instanceof ObservableLongValue) && !(var1 instanceof ObservableLongValue) ? new IntegerBinding() {
               {
                  super.bind(var2);
               }

               @Override
               public void dispose() {
                  super.unbind(var2);
               }

               @Override
               protected int computeValue() {
                  return var0.intValue() - var1.intValue();
               }

               @Override
               public ObservableList<?> getDependencies() {
                  return (ObservableList<?>)(var2.length == 1 ? FXCollections.singletonObservableList(var2[0]) : new ImmutableObservableList<>(var2));
               }
            } : new LongBinding() {
               {
                  super.bind(var2);
               }

               @Override
               public void dispose() {
                  super.unbind(var2);
               }

               @Override
               protected long computeValue() {
                  return var0.longValue() - var1.longValue();
               }

               @Override
               public ObservableList<?> getDependencies() {
                  return (ObservableList<?>)(var2.length == 1 ? FXCollections.singletonObservableList(var2[0]) : new ImmutableObservableList<>(var2));
               }
            });
         }
      } else {
         throw new NullPointerException("Operands cannot be null.");
      }
   }

   public static NumberBinding subtract(ObservableNumberValue var0, ObservableNumberValue var1) {
      return subtract(var0, var1, var0, var1);
   }

   public static DoubleBinding subtract(ObservableNumberValue var0, double var1) {
      return (DoubleBinding)subtract(var0, DoubleConstant.valueOf(var1), var0);
   }

   public static DoubleBinding subtract(double var0, ObservableNumberValue var2) {
      return (DoubleBinding)subtract(DoubleConstant.valueOf(var0), var2, var2);
   }

   public static NumberBinding subtract(ObservableNumberValue var0, float var1) {
      return subtract(var0, FloatConstant.valueOf(var1), var0);
   }

   public static NumberBinding subtract(float var0, ObservableNumberValue var1) {
      return subtract(FloatConstant.valueOf(var0), var1, var1);
   }

   public static NumberBinding subtract(ObservableNumberValue var0, long var1) {
      return subtract(var0, LongConstant.valueOf(var1), var0);
   }

   public static NumberBinding subtract(long var0, ObservableNumberValue var2) {
      return subtract(LongConstant.valueOf(var0), var2, var2);
   }

   public static NumberBinding subtract(ObservableNumberValue var0, int var1) {
      return subtract(var0, IntegerConstant.valueOf(var1), var0);
   }

   public static NumberBinding subtract(int var0, ObservableNumberValue var1) {
      return subtract(IntegerConstant.valueOf(var0), var1, var1);
   }

   private static NumberBinding multiply(final ObservableNumberValue var0, final ObservableNumberValue var1, final Observable... var2) {
      if (var0 != null && var1 != null) {
         assert var2 != null && var2.length > 0;

         if (var0 instanceof ObservableDoubleValue || var1 instanceof ObservableDoubleValue) {
            return new DoubleBinding() {
               {
                  super.bind(var2);
               }

               @Override
               public void dispose() {
                  super.unbind(var2);
               }

               @Override
               protected double computeValue() {
                  return var0.doubleValue() * var1.doubleValue();
               }

               @Override
               public ObservableList<?> getDependencies() {
                  return (ObservableList<?>)(var2.length == 1 ? FXCollections.singletonObservableList(var2[0]) : new ImmutableObservableList<>(var2));
               }
            };
         } else if (var0 instanceof ObservableFloatValue || var1 instanceof ObservableFloatValue) {
            return new FloatBinding() {
               {
                  super.bind(var2);
               }

               @Override
               public void dispose() {
                  super.unbind(var2);
               }

               @Override
               protected float computeValue() {
                  return var0.floatValue() * var1.floatValue();
               }

               @Override
               public ObservableList<?> getDependencies() {
                  return (ObservableList<?>)(var2.length == 1 ? FXCollections.singletonObservableList(var2[0]) : new ImmutableObservableList<>(var2));
               }
            };
         } else {
            return (NumberBinding)(!(var0 instanceof ObservableLongValue) && !(var1 instanceof ObservableLongValue) ? new IntegerBinding() {
               {
                  super.bind(var2);
               }

               @Override
               public void dispose() {
                  super.unbind(var2);
               }

               @Override
               protected int computeValue() {
                  return var0.intValue() * var1.intValue();
               }

               @Override
               public ObservableList<?> getDependencies() {
                  return (ObservableList<?>)(var2.length == 1 ? FXCollections.singletonObservableList(var2[0]) : new ImmutableObservableList<>(var2));
               }
            } : new LongBinding() {
               {
                  super.bind(var2);
               }

               @Override
               public void dispose() {
                  super.unbind(var2);
               }

               @Override
               protected long computeValue() {
                  return var0.longValue() * var1.longValue();
               }

               @Override
               public ObservableList<?> getDependencies() {
                  return (ObservableList<?>)(var2.length == 1 ? FXCollections.singletonObservableList(var2[0]) : new ImmutableObservableList<>(var2));
               }
            });
         }
      } else {
         throw new NullPointerException("Operands cannot be null.");
      }
   }

   public static NumberBinding multiply(ObservableNumberValue var0, ObservableNumberValue var1) {
      return multiply(var0, var1, var0, var1);
   }

   public static DoubleBinding multiply(ObservableNumberValue var0, double var1) {
      return (DoubleBinding)multiply(var0, DoubleConstant.valueOf(var1), var0);
   }

   public static DoubleBinding multiply(double var0, ObservableNumberValue var2) {
      return (DoubleBinding)multiply(DoubleConstant.valueOf(var0), var2, var2);
   }

   public static NumberBinding multiply(ObservableNumberValue var0, float var1) {
      return multiply(var0, FloatConstant.valueOf(var1), var0);
   }

   public static NumberBinding multiply(float var0, ObservableNumberValue var1) {
      return multiply(FloatConstant.valueOf(var0), var1, var1);
   }

   public static NumberBinding multiply(ObservableNumberValue var0, long var1) {
      return multiply(var0, LongConstant.valueOf(var1), var0);
   }

   public static NumberBinding multiply(long var0, ObservableNumberValue var2) {
      return multiply(LongConstant.valueOf(var0), var2, var2);
   }

   public static NumberBinding multiply(ObservableNumberValue var0, int var1) {
      return multiply(var0, IntegerConstant.valueOf(var1), var0);
   }

   public static NumberBinding multiply(int var0, ObservableNumberValue var1) {
      return multiply(IntegerConstant.valueOf(var0), var1, var1);
   }

   private static NumberBinding divide(final ObservableNumberValue var0, final ObservableNumberValue var1, final Observable... var2) {
      if (var0 != null && var1 != null) {
         assert var2 != null && var2.length > 0;

         if (var0 instanceof ObservableDoubleValue || var1 instanceof ObservableDoubleValue) {
            return new DoubleBinding() {
               {
                  super.bind(var2);
               }

               @Override
               public void dispose() {
                  super.unbind(var2);
               }

               @Override
               protected double computeValue() {
                  return var0.doubleValue() / var1.doubleValue();
               }

               @Override
               public ObservableList<?> getDependencies() {
                  return (ObservableList<?>)(var2.length == 1 ? FXCollections.singletonObservableList(var2[0]) : new ImmutableObservableList<>(var2));
               }
            };
         } else if (var0 instanceof ObservableFloatValue || var1 instanceof ObservableFloatValue) {
            return new FloatBinding() {
               {
                  super.bind(var2);
               }

               @Override
               public void dispose() {
                  super.unbind(var2);
               }

               @Override
               protected float computeValue() {
                  return var0.floatValue() / var1.floatValue();
               }

               @Override
               public ObservableList<?> getDependencies() {
                  return (ObservableList<?>)(var2.length == 1 ? FXCollections.singletonObservableList(var2[0]) : new ImmutableObservableList<>(var2));
               }
            };
         } else {
            return (NumberBinding)(!(var0 instanceof ObservableLongValue) && !(var1 instanceof ObservableLongValue) ? new IntegerBinding() {
               {
                  super.bind(var2);
               }

               @Override
               public void dispose() {
                  super.unbind(var2);
               }

               @Override
               protected int computeValue() {
                  return var0.intValue() / var1.intValue();
               }

               @Override
               public ObservableList<?> getDependencies() {
                  return (ObservableList<?>)(var2.length == 1 ? FXCollections.singletonObservableList(var2[0]) : new ImmutableObservableList<>(var2));
               }
            } : new LongBinding() {
               {
                  super.bind(var2);
               }

               @Override
               public void dispose() {
                  super.unbind(var2);
               }

               @Override
               protected long computeValue() {
                  return var0.longValue() / var1.longValue();
               }

               @Override
               public ObservableList<?> getDependencies() {
                  return (ObservableList<?>)(var2.length == 1 ? FXCollections.singletonObservableList(var2[0]) : new ImmutableObservableList<>(var2));
               }
            });
         }
      } else {
         throw new NullPointerException("Operands cannot be null.");
      }
   }

   public static NumberBinding divide(ObservableNumberValue var0, ObservableNumberValue var1) {
      return divide(var0, var1, var0, var1);
   }

   public static DoubleBinding divide(ObservableNumberValue var0, double var1) {
      return (DoubleBinding)divide(var0, DoubleConstant.valueOf(var1), var0);
   }

   public static DoubleBinding divide(double var0, ObservableNumberValue var2) {
      return (DoubleBinding)divide(DoubleConstant.valueOf(var0), var2, var2);
   }

   public static NumberBinding divide(ObservableNumberValue var0, float var1) {
      return divide(var0, FloatConstant.valueOf(var1), var0);
   }

   public static NumberBinding divide(float var0, ObservableNumberValue var1) {
      return divide(FloatConstant.valueOf(var0), var1, var1);
   }

   public static NumberBinding divide(ObservableNumberValue var0, long var1) {
      return divide(var0, LongConstant.valueOf(var1), var0);
   }

   public static NumberBinding divide(long var0, ObservableNumberValue var2) {
      return divide(LongConstant.valueOf(var0), var2, var2);
   }

   public static NumberBinding divide(ObservableNumberValue var0, int var1) {
      return divide(var0, IntegerConstant.valueOf(var1), var0);
   }

   public static NumberBinding divide(int var0, ObservableNumberValue var1) {
      return divide(IntegerConstant.valueOf(var0), var1, var1);
   }

   private static BooleanBinding equal(final ObservableNumberValue var0, final ObservableNumberValue var1, final double var2, final Observable... var4) {
      if (var0 != null && var1 != null) {
         assert var4 != null && var4.length > 0;

         if (var0 instanceof ObservableDoubleValue || var1 instanceof ObservableDoubleValue) {
            return new BooleanBinding() {
               {
                  super.bind(var4);
               }

               @Override
               public void dispose() {
                  super.unbind(var4);
               }

               @Override
               protected boolean computeValue() {
                  return Math.abs(var0.doubleValue() - var1.doubleValue()) <= var2;
               }

               @Override
               public ObservableList<?> getDependencies() {
                  return (ObservableList<?>)(var4.length == 1 ? FXCollections.singletonObservableList(var4[0]) : new ImmutableObservableList<>(var4));
               }
            };
         } else if (var0 instanceof ObservableFloatValue || var1 instanceof ObservableFloatValue) {
            return new BooleanBinding() {
               {
                  super.bind(var4);
               }

               @Override
               public void dispose() {
                  super.unbind(var4);
               }

               @Override
               protected boolean computeValue() {
                  return (double)Math.abs(var0.floatValue() - var1.floatValue()) <= var2;
               }

               @Override
               public ObservableList<?> getDependencies() {
                  return (ObservableList<?>)(var4.length == 1 ? FXCollections.singletonObservableList(var4[0]) : new ImmutableObservableList<>(var4));
               }
            };
         } else {
            return !(var0 instanceof ObservableLongValue) && !(var1 instanceof ObservableLongValue) ? new BooleanBinding() {
               {
                  super.bind(var4);
               }

               @Override
               public void dispose() {
                  super.unbind(var4);
               }

               @Override
               protected boolean computeValue() {
                  return (double)Math.abs(var0.intValue() - var1.intValue()) <= var2;
               }

               @Override
               public ObservableList<?> getDependencies() {
                  return (ObservableList<?>)(var4.length == 1 ? FXCollections.singletonObservableList(var4[0]) : new ImmutableObservableList<>(var4));
               }
            } : new BooleanBinding() {
               {
                  super.bind(var4);
               }

               @Override
               public void dispose() {
                  super.unbind(var4);
               }

               @Override
               protected boolean computeValue() {
                  return (double)Math.abs(var0.longValue() - var1.longValue()) <= var2;
               }

               @Override
               public ObservableList<?> getDependencies() {
                  return (ObservableList<?>)(var4.length == 1 ? FXCollections.singletonObservableList(var4[0]) : new ImmutableObservableList<>(var4));
               }
            };
         }
      } else {
         throw new NullPointerException("Operands cannot be null.");
      }
   }

   public static BooleanBinding equal(ObservableNumberValue var0, ObservableNumberValue var1, double var2) {
      return equal(var0, var1, var2, var0, var1);
   }

   public static BooleanBinding equal(ObservableNumberValue var0, ObservableNumberValue var1) {
      return equal(var0, var1, 0.0, var0, var1);
   }

   public static BooleanBinding equal(ObservableNumberValue var0, double var1, double var3) {
      return equal(var0, DoubleConstant.valueOf(var1), var3, var0);
   }

   public static BooleanBinding equal(double var0, ObservableNumberValue var2, double var3) {
      return equal(DoubleConstant.valueOf(var0), var2, var3, var2);
   }

   public static BooleanBinding equal(ObservableNumberValue var0, float var1, double var2) {
      return equal(var0, FloatConstant.valueOf(var1), var2, var0);
   }

   public static BooleanBinding equal(float var0, ObservableNumberValue var1, double var2) {
      return equal(FloatConstant.valueOf(var0), var1, var2, var1);
   }

   public static BooleanBinding equal(ObservableNumberValue var0, long var1, double var3) {
      return equal(var0, LongConstant.valueOf(var1), var3, var0);
   }

   public static BooleanBinding equal(ObservableNumberValue var0, long var1) {
      return equal(var0, LongConstant.valueOf(var1), 0.0, var0);
   }

   public static BooleanBinding equal(long var0, ObservableNumberValue var2, double var3) {
      return equal(LongConstant.valueOf(var0), var2, var3, var2);
   }

   public static BooleanBinding equal(long var0, ObservableNumberValue var2) {
      return equal(LongConstant.valueOf(var0), var2, 0.0, var2);
   }

   public static BooleanBinding equal(ObservableNumberValue var0, int var1, double var2) {
      return equal(var0, IntegerConstant.valueOf(var1), var2, var0);
   }

   public static BooleanBinding equal(ObservableNumberValue var0, int var1) {
      return equal(var0, IntegerConstant.valueOf(var1), 0.0, var0);
   }

   public static BooleanBinding equal(int var0, ObservableNumberValue var1, double var2) {
      return equal(IntegerConstant.valueOf(var0), var1, var2, var1);
   }

   public static BooleanBinding equal(int var0, ObservableNumberValue var1) {
      return equal(IntegerConstant.valueOf(var0), var1, 0.0, var1);
   }

   private static BooleanBinding notEqual(final ObservableNumberValue var0, final ObservableNumberValue var1, final double var2, final Observable... var4) {
      if (var0 != null && var1 != null) {
         assert var4 != null && var4.length > 0;

         if (var0 instanceof ObservableDoubleValue || var1 instanceof ObservableDoubleValue) {
            return new BooleanBinding() {
               {
                  super.bind(var4);
               }

               @Override
               public void dispose() {
                  super.unbind(var4);
               }

               @Override
               protected boolean computeValue() {
                  return Math.abs(var0.doubleValue() - var1.doubleValue()) > var2;
               }

               @Override
               public ObservableList<?> getDependencies() {
                  return (ObservableList<?>)(var4.length == 1 ? FXCollections.singletonObservableList(var4[0]) : new ImmutableObservableList<>(var4));
               }
            };
         } else if (var0 instanceof ObservableFloatValue || var1 instanceof ObservableFloatValue) {
            return new BooleanBinding() {
               {
                  super.bind(var4);
               }

               @Override
               public void dispose() {
                  super.unbind(var4);
               }

               @Override
               protected boolean computeValue() {
                  return (double)Math.abs(var0.floatValue() - var1.floatValue()) > var2;
               }

               @Override
               public ObservableList<?> getDependencies() {
                  return (ObservableList<?>)(var4.length == 1 ? FXCollections.singletonObservableList(var4[0]) : new ImmutableObservableList<>(var4));
               }
            };
         } else {
            return !(var0 instanceof ObservableLongValue) && !(var1 instanceof ObservableLongValue) ? new BooleanBinding() {
               {
                  super.bind(var4);
               }

               @Override
               public void dispose() {
                  super.unbind(var4);
               }

               @Override
               protected boolean computeValue() {
                  return (double)Math.abs(var0.intValue() - var1.intValue()) > var2;
               }

               @Override
               public ObservableList<?> getDependencies() {
                  return (ObservableList<?>)(var4.length == 1 ? FXCollections.singletonObservableList(var4[0]) : new ImmutableObservableList<>(var4));
               }
            } : new BooleanBinding() {
               {
                  super.bind(var4);
               }

               @Override
               public void dispose() {
                  super.unbind(var4);
               }

               @Override
               protected boolean computeValue() {
                  return (double)Math.abs(var0.longValue() - var1.longValue()) > var2;
               }

               @Override
               public ObservableList<?> getDependencies() {
                  return (ObservableList<?>)(var4.length == 1 ? FXCollections.singletonObservableList(var4[0]) : new ImmutableObservableList<>(var4));
               }
            };
         }
      } else {
         throw new NullPointerException("Operands cannot be null.");
      }
   }

   public static BooleanBinding notEqual(ObservableNumberValue var0, ObservableNumberValue var1, double var2) {
      return notEqual(var0, var1, var2, var0, var1);
   }

   public static BooleanBinding notEqual(ObservableNumberValue var0, ObservableNumberValue var1) {
      return notEqual(var0, var1, 0.0, var0, var1);
   }

   public static BooleanBinding notEqual(ObservableNumberValue var0, double var1, double var3) {
      return notEqual(var0, DoubleConstant.valueOf(var1), var3, var0);
   }

   public static BooleanBinding notEqual(double var0, ObservableNumberValue var2, double var3) {
      return notEqual(DoubleConstant.valueOf(var0), var2, var3, var2);
   }

   public static BooleanBinding notEqual(ObservableNumberValue var0, float var1, double var2) {
      return notEqual(var0, FloatConstant.valueOf(var1), var2, var0);
   }

   public static BooleanBinding notEqual(float var0, ObservableNumberValue var1, double var2) {
      return notEqual(FloatConstant.valueOf(var0), var1, var2, var1);
   }

   public static BooleanBinding notEqual(ObservableNumberValue var0, long var1, double var3) {
      return notEqual(var0, LongConstant.valueOf(var1), var3, var0);
   }

   public static BooleanBinding notEqual(ObservableNumberValue var0, long var1) {
      return notEqual(var0, LongConstant.valueOf(var1), 0.0, var0);
   }

   public static BooleanBinding notEqual(long var0, ObservableNumberValue var2, double var3) {
      return notEqual(LongConstant.valueOf(var0), var2, var3, var2);
   }

   public static BooleanBinding notEqual(long var0, ObservableNumberValue var2) {
      return notEqual(LongConstant.valueOf(var0), var2, 0.0, var2);
   }

   public static BooleanBinding notEqual(ObservableNumberValue var0, int var1, double var2) {
      return notEqual(var0, IntegerConstant.valueOf(var1), var2, var0);
   }

   public static BooleanBinding notEqual(ObservableNumberValue var0, int var1) {
      return notEqual(var0, IntegerConstant.valueOf(var1), 0.0, var0);
   }

   public static BooleanBinding notEqual(int var0, ObservableNumberValue var1, double var2) {
      return notEqual(IntegerConstant.valueOf(var0), var1, var2, var1);
   }

   public static BooleanBinding notEqual(int var0, ObservableNumberValue var1) {
      return notEqual(IntegerConstant.valueOf(var0), var1, 0.0, var1);
   }

   private static BooleanBinding greaterThan(final ObservableNumberValue var0, final ObservableNumberValue var1, final Observable... var2) {
      if (var0 != null && var1 != null) {
         assert var2 != null && var2.length > 0;

         if (var0 instanceof ObservableDoubleValue || var1 instanceof ObservableDoubleValue) {
            return new BooleanBinding() {
               {
                  super.bind(var2);
               }

               @Override
               public void dispose() {
                  super.unbind(var2);
               }

               @Override
               protected boolean computeValue() {
                  return var0.doubleValue() > var1.doubleValue();
               }

               @Override
               public ObservableList<?> getDependencies() {
                  return (ObservableList<?>)(var2.length == 1 ? FXCollections.singletonObservableList(var2[0]) : new ImmutableObservableList<>(var2));
               }
            };
         } else if (var0 instanceof ObservableFloatValue || var1 instanceof ObservableFloatValue) {
            return new BooleanBinding() {
               {
                  super.bind(var2);
               }

               @Override
               public void dispose() {
                  super.unbind(var2);
               }

               @Override
               protected boolean computeValue() {
                  return var0.floatValue() > var1.floatValue();
               }

               @Override
               public ObservableList<?> getDependencies() {
                  return (ObservableList<?>)(var2.length == 1 ? FXCollections.singletonObservableList(var2[0]) : new ImmutableObservableList<>(var2));
               }
            };
         } else {
            return !(var0 instanceof ObservableLongValue) && !(var1 instanceof ObservableLongValue) ? new BooleanBinding() {
               {
                  super.bind(var2);
               }

               @Override
               public void dispose() {
                  super.unbind(var2);
               }

               @Override
               protected boolean computeValue() {
                  return var0.intValue() > var1.intValue();
               }

               @Override
               public ObservableList<?> getDependencies() {
                  return (ObservableList<?>)(var2.length == 1 ? FXCollections.singletonObservableList(var2[0]) : new ImmutableObservableList<>(var2));
               }
            } : new BooleanBinding() {
               {
                  super.bind(var2);
               }

               @Override
               public void dispose() {
                  super.unbind(var2);
               }

               @Override
               protected boolean computeValue() {
                  return var0.longValue() > var1.longValue();
               }

               @Override
               public ObservableList<?> getDependencies() {
                  return (ObservableList<?>)(var2.length == 1 ? FXCollections.singletonObservableList(var2[0]) : new ImmutableObservableList<>(var2));
               }
            };
         }
      } else {
         throw new NullPointerException("Operands cannot be null.");
      }
   }

   public static BooleanBinding greaterThan(ObservableNumberValue var0, ObservableNumberValue var1) {
      return greaterThan(var0, var1, var0, var1);
   }

   public static BooleanBinding greaterThan(ObservableNumberValue var0, double var1) {
      return greaterThan(var0, DoubleConstant.valueOf(var1), var0);
   }

   public static BooleanBinding greaterThan(double var0, ObservableNumberValue var2) {
      return greaterThan(DoubleConstant.valueOf(var0), var2, var2);
   }

   public static BooleanBinding greaterThan(ObservableNumberValue var0, float var1) {
      return greaterThan(var0, FloatConstant.valueOf(var1), var0);
   }

   public static BooleanBinding greaterThan(float var0, ObservableNumberValue var1) {
      return greaterThan(FloatConstant.valueOf(var0), var1, var1);
   }

   public static BooleanBinding greaterThan(ObservableNumberValue var0, long var1) {
      return greaterThan(var0, LongConstant.valueOf(var1), var0);
   }

   public static BooleanBinding greaterThan(long var0, ObservableNumberValue var2) {
      return greaterThan(LongConstant.valueOf(var0), var2, var2);
   }

   public static BooleanBinding greaterThan(ObservableNumberValue var0, int var1) {
      return greaterThan(var0, IntegerConstant.valueOf(var1), var0);
   }

   public static BooleanBinding greaterThan(int var0, ObservableNumberValue var1) {
      return greaterThan(IntegerConstant.valueOf(var0), var1, var1);
   }

   private static BooleanBinding lessThan(ObservableNumberValue var0, ObservableNumberValue var1, Observable... var2) {
      return greaterThan(var1, var0, var2);
   }

   public static BooleanBinding lessThan(ObservableNumberValue var0, ObservableNumberValue var1) {
      return lessThan(var0, var1, var0, var1);
   }

   public static BooleanBinding lessThan(ObservableNumberValue var0, double var1) {
      return lessThan(var0, DoubleConstant.valueOf(var1), var0);
   }

   public static BooleanBinding lessThan(double var0, ObservableNumberValue var2) {
      return lessThan(DoubleConstant.valueOf(var0), var2, var2);
   }

   public static BooleanBinding lessThan(ObservableNumberValue var0, float var1) {
      return lessThan(var0, FloatConstant.valueOf(var1), var0);
   }

   public static BooleanBinding lessThan(float var0, ObservableNumberValue var1) {
      return lessThan(FloatConstant.valueOf(var0), var1, var1);
   }

   public static BooleanBinding lessThan(ObservableNumberValue var0, long var1) {
      return lessThan(var0, LongConstant.valueOf(var1), var0);
   }

   public static BooleanBinding lessThan(long var0, ObservableNumberValue var2) {
      return lessThan(LongConstant.valueOf(var0), var2, var2);
   }

   public static BooleanBinding lessThan(ObservableNumberValue var0, int var1) {
      return lessThan(var0, IntegerConstant.valueOf(var1), var0);
   }

   public static BooleanBinding lessThan(int var0, ObservableNumberValue var1) {
      return lessThan(IntegerConstant.valueOf(var0), var1, var1);
   }

   private static BooleanBinding greaterThanOrEqual(final ObservableNumberValue var0, final ObservableNumberValue var1, final Observable... var2) {
      if (var0 != null && var1 != null) {
         assert var2 != null && var2.length > 0;

         if (var0 instanceof ObservableDoubleValue || var1 instanceof ObservableDoubleValue) {
            return new BooleanBinding() {
               {
                  super.bind(var2);
               }

               @Override
               public void dispose() {
                  super.unbind(var2);
               }

               @Override
               protected boolean computeValue() {
                  return var0.doubleValue() >= var1.doubleValue();
               }

               @Override
               public ObservableList<?> getDependencies() {
                  return (ObservableList<?>)(var2.length == 1 ? FXCollections.singletonObservableList(var2[0]) : new ImmutableObservableList<>(var2));
               }
            };
         } else if (var0 instanceof ObservableFloatValue || var1 instanceof ObservableFloatValue) {
            return new BooleanBinding() {
               {
                  super.bind(var2);
               }

               @Override
               public void dispose() {
                  super.unbind(var2);
               }

               @Override
               protected boolean computeValue() {
                  return var0.floatValue() >= var1.floatValue();
               }

               @Override
               public ObservableList<?> getDependencies() {
                  return (ObservableList<?>)(var2.length == 1 ? FXCollections.singletonObservableList(var2[0]) : new ImmutableObservableList<>(var2));
               }
            };
         } else {
            return !(var0 instanceof ObservableLongValue) && !(var1 instanceof ObservableLongValue) ? new BooleanBinding() {
               {
                  super.bind(var2);
               }

               @Override
               public void dispose() {
                  super.unbind(var2);
               }

               @Override
               protected boolean computeValue() {
                  return var0.intValue() >= var1.intValue();
               }

               @Override
               public ObservableList<?> getDependencies() {
                  return (ObservableList<?>)(var2.length == 1 ? FXCollections.singletonObservableList(var2[0]) : new ImmutableObservableList<>(var2));
               }
            } : new BooleanBinding() {
               {
                  super.bind(var2);
               }

               @Override
               public void dispose() {
                  super.unbind(var2);
               }

               @Override
               protected boolean computeValue() {
                  return var0.longValue() >= var1.longValue();
               }

               @Override
               public ObservableList<?> getDependencies() {
                  return (ObservableList<?>)(var2.length == 1 ? FXCollections.singletonObservableList(var2[0]) : new ImmutableObservableList<>(var2));
               }
            };
         }
      } else {
         throw new NullPointerException("Operands cannot be null.");
      }
   }

   public static BooleanBinding greaterThanOrEqual(ObservableNumberValue var0, ObservableNumberValue var1) {
      return greaterThanOrEqual(var0, var1, var0, var1);
   }

   public static BooleanBinding greaterThanOrEqual(ObservableNumberValue var0, double var1) {
      return greaterThanOrEqual(var0, DoubleConstant.valueOf(var1), var0);
   }

   public static BooleanBinding greaterThanOrEqual(double var0, ObservableNumberValue var2) {
      return greaterThanOrEqual(DoubleConstant.valueOf(var0), var2, var2);
   }

   public static BooleanBinding greaterThanOrEqual(ObservableNumberValue var0, float var1) {
      return greaterThanOrEqual(var0, FloatConstant.valueOf(var1), var0);
   }

   public static BooleanBinding greaterThanOrEqual(float var0, ObservableNumberValue var1) {
      return greaterThanOrEqual(FloatConstant.valueOf(var0), var1, var1);
   }

   public static BooleanBinding greaterThanOrEqual(ObservableNumberValue var0, long var1) {
      return greaterThanOrEqual(var0, LongConstant.valueOf(var1), var0);
   }

   public static BooleanBinding greaterThanOrEqual(long var0, ObservableNumberValue var2) {
      return greaterThanOrEqual(LongConstant.valueOf(var0), var2, var2);
   }

   public static BooleanBinding greaterThanOrEqual(ObservableNumberValue var0, int var1) {
      return greaterThanOrEqual(var0, IntegerConstant.valueOf(var1), var0);
   }

   public static BooleanBinding greaterThanOrEqual(int var0, ObservableNumberValue var1) {
      return greaterThanOrEqual(IntegerConstant.valueOf(var0), var1, var1);
   }

   private static BooleanBinding lessThanOrEqual(ObservableNumberValue var0, ObservableNumberValue var1, Observable... var2) {
      return greaterThanOrEqual(var1, var0, var2);
   }

   public static BooleanBinding lessThanOrEqual(ObservableNumberValue var0, ObservableNumberValue var1) {
      return lessThanOrEqual(var0, var1, var0, var1);
   }

   public static BooleanBinding lessThanOrEqual(ObservableNumberValue var0, double var1) {
      return lessThanOrEqual(var0, DoubleConstant.valueOf(var1), var0);
   }

   public static BooleanBinding lessThanOrEqual(double var0, ObservableNumberValue var2) {
      return lessThanOrEqual(DoubleConstant.valueOf(var0), var2, var2);
   }

   public static BooleanBinding lessThanOrEqual(ObservableNumberValue var0, float var1) {
      return lessThanOrEqual(var0, FloatConstant.valueOf(var1), var0);
   }

   public static BooleanBinding lessThanOrEqual(float var0, ObservableNumberValue var1) {
      return lessThanOrEqual(FloatConstant.valueOf(var0), var1, var1);
   }

   public static BooleanBinding lessThanOrEqual(ObservableNumberValue var0, long var1) {
      return lessThanOrEqual(var0, LongConstant.valueOf(var1), var0);
   }

   public static BooleanBinding lessThanOrEqual(long var0, ObservableNumberValue var2) {
      return lessThanOrEqual(LongConstant.valueOf(var0), var2, var2);
   }

   public static BooleanBinding lessThanOrEqual(ObservableNumberValue var0, int var1) {
      return lessThanOrEqual(var0, IntegerConstant.valueOf(var1), var0);
   }

   public static BooleanBinding lessThanOrEqual(int var0, ObservableNumberValue var1) {
      return lessThanOrEqual(IntegerConstant.valueOf(var0), var1, var1);
   }

   private static NumberBinding min(final ObservableNumberValue var0, final ObservableNumberValue var1, final Observable... var2) {
      if (var0 != null && var1 != null) {
         assert var2 != null && var2.length > 0;

         if (var0 instanceof ObservableDoubleValue || var1 instanceof ObservableDoubleValue) {
            return new DoubleBinding() {
               {
                  super.bind(var2);
               }

               @Override
               public void dispose() {
                  super.unbind(var2);
               }

               @Override
               protected double computeValue() {
                  return Math.min(var0.doubleValue(), var1.doubleValue());
               }

               @Override
               public ObservableList<?> getDependencies() {
                  return (ObservableList<?>)(var2.length == 1 ? FXCollections.singletonObservableList(var2[0]) : new ImmutableObservableList<>(var2));
               }
            };
         } else if (var0 instanceof ObservableFloatValue || var1 instanceof ObservableFloatValue) {
            return new FloatBinding() {
               {
                  super.bind(var2);
               }

               @Override
               public void dispose() {
                  super.unbind(var2);
               }

               @Override
               protected float computeValue() {
                  return Math.min(var0.floatValue(), var1.floatValue());
               }

               @Override
               public ObservableList<?> getDependencies() {
                  return (ObservableList<?>)(var2.length == 1 ? FXCollections.singletonObservableList(var2[0]) : new ImmutableObservableList<>(var2));
               }
            };
         } else {
            return (NumberBinding)(!(var0 instanceof ObservableLongValue) && !(var1 instanceof ObservableLongValue) ? new IntegerBinding() {
               {
                  super.bind(var2);
               }

               @Override
               public void dispose() {
                  super.unbind(var2);
               }

               @Override
               protected int computeValue() {
                  return Math.min(var0.intValue(), var1.intValue());
               }

               @Override
               public ObservableList<?> getDependencies() {
                  return (ObservableList<?>)(var2.length == 1 ? FXCollections.singletonObservableList(var2[0]) : new ImmutableObservableList<>(var2));
               }
            } : new LongBinding() {
               {
                  super.bind(var2);
               }

               @Override
               public void dispose() {
                  super.unbind(var2);
               }

               @Override
               protected long computeValue() {
                  return Math.min(var0.longValue(), var1.longValue());
               }

               @Override
               public ObservableList<?> getDependencies() {
                  return (ObservableList<?>)(var2.length == 1 ? FXCollections.singletonObservableList(var2[0]) : new ImmutableObservableList<>(var2));
               }
            });
         }
      } else {
         throw new NullPointerException("Operands cannot be null.");
      }
   }

   public static NumberBinding min(ObservableNumberValue var0, ObservableNumberValue var1) {
      return min(var0, var1, var0, var1);
   }

   public static DoubleBinding min(ObservableNumberValue var0, double var1) {
      return (DoubleBinding)min(var0, DoubleConstant.valueOf(var1), var0);
   }

   public static DoubleBinding min(double var0, ObservableNumberValue var2) {
      return (DoubleBinding)min(DoubleConstant.valueOf(var0), var2, var2);
   }

   public static NumberBinding min(ObservableNumberValue var0, float var1) {
      return min(var0, FloatConstant.valueOf(var1), var0);
   }

   public static NumberBinding min(float var0, ObservableNumberValue var1) {
      return min(FloatConstant.valueOf(var0), var1, var1);
   }

   public static NumberBinding min(ObservableNumberValue var0, long var1) {
      return min(var0, LongConstant.valueOf(var1), var0);
   }

   public static NumberBinding min(long var0, ObservableNumberValue var2) {
      return min(LongConstant.valueOf(var0), var2, var2);
   }

   public static NumberBinding min(ObservableNumberValue var0, int var1) {
      return min(var0, IntegerConstant.valueOf(var1), var0);
   }

   public static NumberBinding min(int var0, ObservableNumberValue var1) {
      return min(IntegerConstant.valueOf(var0), var1, var1);
   }

   private static NumberBinding max(final ObservableNumberValue var0, final ObservableNumberValue var1, final Observable... var2) {
      if (var0 != null && var1 != null) {
         assert var2 != null && var2.length > 0;

         if (var0 instanceof ObservableDoubleValue || var1 instanceof ObservableDoubleValue) {
            return new DoubleBinding() {
               {
                  super.bind(var2);
               }

               @Override
               public void dispose() {
                  super.unbind(var2);
               }

               @Override
               protected double computeValue() {
                  return Math.max(var0.doubleValue(), var1.doubleValue());
               }

               @Override
               public ObservableList<?> getDependencies() {
                  return (ObservableList<?>)(var2.length == 1 ? FXCollections.singletonObservableList(var2[0]) : new ImmutableObservableList<>(var2));
               }
            };
         } else if (var0 instanceof ObservableFloatValue || var1 instanceof ObservableFloatValue) {
            return new FloatBinding() {
               {
                  super.bind(var2);
               }

               @Override
               public void dispose() {
                  super.unbind(var2);
               }

               @Override
               protected float computeValue() {
                  return Math.max(var0.floatValue(), var1.floatValue());
               }

               @Override
               public ObservableList<?> getDependencies() {
                  return (ObservableList<?>)(var2.length == 1 ? FXCollections.singletonObservableList(var2[0]) : new ImmutableObservableList<>(var2));
               }
            };
         } else {
            return (NumberBinding)(!(var0 instanceof ObservableLongValue) && !(var1 instanceof ObservableLongValue) ? new IntegerBinding() {
               {
                  super.bind(var2);
               }

               @Override
               public void dispose() {
                  super.unbind(var2);
               }

               @Override
               protected int computeValue() {
                  return Math.max(var0.intValue(), var1.intValue());
               }

               @Override
               public ObservableList<?> getDependencies() {
                  return (ObservableList<?>)(var2.length == 1 ? FXCollections.singletonObservableList(var2[0]) : new ImmutableObservableList<>(var2));
               }
            } : new LongBinding() {
               {
                  super.bind(var2);
               }

               @Override
               public void dispose() {
                  super.unbind(var2);
               }

               @Override
               protected long computeValue() {
                  return Math.max(var0.longValue(), var1.longValue());
               }

               @Override
               public ObservableList<?> getDependencies() {
                  return (ObservableList<?>)(var2.length == 1 ? FXCollections.singletonObservableList(var2[0]) : new ImmutableObservableList<>(var2));
               }
            });
         }
      } else {
         throw new NullPointerException("Operands cannot be null.");
      }
   }

   public static NumberBinding max(ObservableNumberValue var0, ObservableNumberValue var1) {
      return max(var0, var1, var0, var1);
   }

   public static DoubleBinding max(ObservableNumberValue var0, double var1) {
      return (DoubleBinding)max(var0, DoubleConstant.valueOf(var1), var0);
   }

   public static DoubleBinding max(double var0, ObservableNumberValue var2) {
      return (DoubleBinding)max(DoubleConstant.valueOf(var0), var2, var2);
   }

   public static NumberBinding max(ObservableNumberValue var0, float var1) {
      return max(var0, FloatConstant.valueOf(var1), var0);
   }

   public static NumberBinding max(float var0, ObservableNumberValue var1) {
      return max(FloatConstant.valueOf(var0), var1, var1);
   }

   public static NumberBinding max(ObservableNumberValue var0, long var1) {
      return max(var0, LongConstant.valueOf(var1), var0);
   }

   public static NumberBinding max(long var0, ObservableNumberValue var2) {
      return max(LongConstant.valueOf(var0), var2, var2);
   }

   public static NumberBinding max(ObservableNumberValue var0, int var1) {
      return max(var0, IntegerConstant.valueOf(var1), var0);
   }

   public static NumberBinding max(int var0, ObservableNumberValue var1) {
      return max(IntegerConstant.valueOf(var0), var1, var1);
   }

   public static BooleanBinding and(ObservableBooleanValue var0, ObservableBooleanValue var1) {
      if (var0 != null && var1 != null) {
         return new Bindings.BooleanAndBinding(var0, var1);
      } else {
         throw new NullPointerException("Operands cannot be null.");
      }
   }

   public static BooleanBinding or(ObservableBooleanValue var0, ObservableBooleanValue var1) {
      if (var0 != null && var1 != null) {
         return new Bindings.BooleanOrBinding(var0, var1);
      } else {
         throw new NullPointerException("Operands cannot be null.");
      }
   }

   public static BooleanBinding not(final ObservableBooleanValue var0) {
      if (var0 == null) {
         throw new NullPointerException("Operand cannot be null.");
      } else {
         return new BooleanBinding() {
            {
               super.bind(var0);
            }

            @Override
            public void dispose() {
               super.unbind(var0);
            }

            @Override
            protected boolean computeValue() {
               return !var0.get();
            }

            @Override
            public ObservableList<?> getDependencies() {
               return FXCollections.singletonObservableList(var0);
            }
         };
      }
   }

   public static BooleanBinding equal(final ObservableBooleanValue var0, final ObservableBooleanValue var1) {
      if (var0 != null && var1 != null) {
         return new BooleanBinding() {
            {
               super.bind(var0, var1);
            }

            @Override
            public void dispose() {
               super.unbind(var0, var1);
            }

            @Override
            protected boolean computeValue() {
               return var0.get() == var1.get();
            }

            @Override
            public ObservableList<?> getDependencies() {
               return new ImmutableObservableList<>(var0, var1);
            }
         };
      } else {
         throw new NullPointerException("Operands cannot be null.");
      }
   }

   public static BooleanBinding notEqual(final ObservableBooleanValue var0, final ObservableBooleanValue var1) {
      if (var0 != null && var1 != null) {
         return new BooleanBinding() {
            {
               super.bind(var0, var1);
            }

            @Override
            public void dispose() {
               super.unbind(var0, var1);
            }

            @Override
            protected boolean computeValue() {
               return var0.get() != var1.get();
            }

            @Override
            public ObservableList<?> getDependencies() {
               return new ImmutableObservableList<>(var0, var1);
            }
         };
      } else {
         throw new NullPointerException("Operands cannot be null.");
      }
   }

   public static StringExpression convert(ObservableValue<?> var0) {
      return StringFormatter.convert(var0);
   }

   public static StringExpression concat(Object... var0) {
      return StringFormatter.concat(var0);
   }

   public static StringExpression format(String var0, Object... var1) {
      return StringFormatter.format(var0, var1);
   }

   public static StringExpression format(Locale var0, String var1, Object... var2) {
      return StringFormatter.format(var0, var1, var2);
   }

   private static String getStringSafe(String var0) {
      return var0 == null ? "" : var0;
   }

   private static BooleanBinding equal(final ObservableStringValue var0, final ObservableStringValue var1, final Observable... var2) {
      if (var0 != null && var1 != null) {
         assert var2 != null && var2.length > 0;

         return new BooleanBinding() {
            {
               super.bind(var2);
            }

            @Override
            public void dispose() {
               super.unbind(var2);
            }

            @Override
            protected boolean computeValue() {
               String var1x = Bindings.getStringSafe(var0.get());
               String var2x = Bindings.getStringSafe(var1.get());
               return var1x.equals(var2x);
            }

            @Override
            public ObservableList<?> getDependencies() {
               return (ObservableList<?>)(var2.length == 1 ? FXCollections.singletonObservableList(var2[0]) : new ImmutableObservableList<>(var2));
            }
         };
      } else {
         throw new NullPointerException("Operands cannot be null.");
      }
   }

   public static BooleanBinding equal(ObservableStringValue var0, ObservableStringValue var1) {
      return equal(var0, var1, var0, var1);
   }

   public static BooleanBinding equal(ObservableStringValue var0, String var1) {
      return equal(var0, StringConstant.valueOf(var1), var0);
   }

   public static BooleanBinding equal(String var0, ObservableStringValue var1) {
      return equal(StringConstant.valueOf(var0), var1, var1);
   }

   private static BooleanBinding notEqual(final ObservableStringValue var0, final ObservableStringValue var1, final Observable... var2) {
      if (var0 != null && var1 != null) {
         assert var2 != null && var2.length > 0;

         return new BooleanBinding() {
            {
               super.bind(var2);
            }

            @Override
            public void dispose() {
               super.unbind(var2);
            }

            @Override
            protected boolean computeValue() {
               String var1x = Bindings.getStringSafe(var0.get());
               String var2x = Bindings.getStringSafe(var1.get());
               return !var1x.equals(var2x);
            }

            @Override
            public ObservableList<?> getDependencies() {
               return (ObservableList<?>)(var2.length == 1 ? FXCollections.singletonObservableList(var2[0]) : new ImmutableObservableList<>(var2));
            }
         };
      } else {
         throw new NullPointerException("Operands cannot be null.");
      }
   }

   public static BooleanBinding notEqual(ObservableStringValue var0, ObservableStringValue var1) {
      return notEqual(var0, var1, var0, var1);
   }

   public static BooleanBinding notEqual(ObservableStringValue var0, String var1) {
      return notEqual(var0, StringConstant.valueOf(var1), var0);
   }

   public static BooleanBinding notEqual(String var0, ObservableStringValue var1) {
      return notEqual(StringConstant.valueOf(var0), var1, var1);
   }

   private static BooleanBinding equalIgnoreCase(final ObservableStringValue var0, final ObservableStringValue var1, final Observable... var2) {
      if (var0 != null && var1 != null) {
         assert var2 != null && var2.length > 0;

         return new BooleanBinding() {
            {
               super.bind(var2);
            }

            @Override
            public void dispose() {
               super.unbind(var2);
            }

            @Override
            protected boolean computeValue() {
               String var1x = Bindings.getStringSafe(var0.get());
               String var2x = Bindings.getStringSafe(var1.get());
               return var1x.equalsIgnoreCase(var2x);
            }

            @Override
            public ObservableList<?> getDependencies() {
               return (ObservableList<?>)(var2.length == 1 ? FXCollections.singletonObservableList(var2[0]) : new ImmutableObservableList<>(var2));
            }
         };
      } else {
         throw new NullPointerException("Operands cannot be null.");
      }
   }

   public static BooleanBinding equalIgnoreCase(ObservableStringValue var0, ObservableStringValue var1) {
      return equalIgnoreCase(var0, var1, var0, var1);
   }

   public static BooleanBinding equalIgnoreCase(ObservableStringValue var0, String var1) {
      return equalIgnoreCase(var0, StringConstant.valueOf(var1), var0);
   }

   public static BooleanBinding equalIgnoreCase(String var0, ObservableStringValue var1) {
      return equalIgnoreCase(StringConstant.valueOf(var0), var1, var1);
   }

   private static BooleanBinding notEqualIgnoreCase(final ObservableStringValue var0, final ObservableStringValue var1, final Observable... var2) {
      if (var0 != null && var1 != null) {
         assert var2 != null && var2.length > 0;

         return new BooleanBinding() {
            {
               super.bind(var2);
            }

            @Override
            public void dispose() {
               super.unbind(var2);
            }

            @Override
            protected boolean computeValue() {
               String var1x = Bindings.getStringSafe(var0.get());
               String var2x = Bindings.getStringSafe(var1.get());
               return !var1x.equalsIgnoreCase(var2x);
            }

            @Override
            public ObservableList<?> getDependencies() {
               return (ObservableList<?>)(var2.length == 1 ? FXCollections.singletonObservableList(var2[0]) : new ImmutableObservableList<>(var2));
            }
         };
      } else {
         throw new NullPointerException("Operands cannot be null.");
      }
   }

   public static BooleanBinding notEqualIgnoreCase(ObservableStringValue var0, ObservableStringValue var1) {
      return notEqualIgnoreCase(var0, var1, var0, var1);
   }

   public static BooleanBinding notEqualIgnoreCase(ObservableStringValue var0, String var1) {
      return notEqualIgnoreCase(var0, StringConstant.valueOf(var1), var0);
   }

   public static BooleanBinding notEqualIgnoreCase(String var0, ObservableStringValue var1) {
      return notEqualIgnoreCase(StringConstant.valueOf(var0), var1, var1);
   }

   private static BooleanBinding greaterThan(final ObservableStringValue var0, final ObservableStringValue var1, final Observable... var2) {
      if (var0 != null && var1 != null) {
         assert var2 != null && var2.length > 0;

         return new BooleanBinding() {
            {
               super.bind(var2);
            }

            @Override
            public void dispose() {
               super.unbind(var2);
            }

            @Override
            protected boolean computeValue() {
               String var1x = Bindings.getStringSafe(var0.get());
               String var2x = Bindings.getStringSafe(var1.get());
               return var1x.compareTo(var2x) > 0;
            }

            @Override
            public ObservableList<?> getDependencies() {
               return (ObservableList<?>)(var2.length == 1 ? FXCollections.singletonObservableList(var2[0]) : new ImmutableObservableList<>(var2));
            }
         };
      } else {
         throw new NullPointerException("Operands cannot be null.");
      }
   }

   public static BooleanBinding greaterThan(ObservableStringValue var0, ObservableStringValue var1) {
      return greaterThan(var0, var1, var0, var1);
   }

   public static BooleanBinding greaterThan(ObservableStringValue var0, String var1) {
      return greaterThan(var0, StringConstant.valueOf(var1), var0);
   }

   public static BooleanBinding greaterThan(String var0, ObservableStringValue var1) {
      return greaterThan(StringConstant.valueOf(var0), var1, var1);
   }

   private static BooleanBinding lessThan(ObservableStringValue var0, ObservableStringValue var1, Observable... var2) {
      return greaterThan(var1, var0, var2);
   }

   public static BooleanBinding lessThan(ObservableStringValue var0, ObservableStringValue var1) {
      return lessThan(var0, var1, var0, var1);
   }

   public static BooleanBinding lessThan(ObservableStringValue var0, String var1) {
      return lessThan(var0, StringConstant.valueOf(var1), var0);
   }

   public static BooleanBinding lessThan(String var0, ObservableStringValue var1) {
      return lessThan(StringConstant.valueOf(var0), var1, var1);
   }

   private static BooleanBinding greaterThanOrEqual(final ObservableStringValue var0, final ObservableStringValue var1, final Observable... var2) {
      if (var0 != null && var1 != null) {
         assert var2 != null && var2.length > 0;

         return new BooleanBinding() {
            {
               super.bind(var2);
            }

            @Override
            public void dispose() {
               super.unbind(var2);
            }

            @Override
            protected boolean computeValue() {
               String var1x = Bindings.getStringSafe(var0.get());
               String var2x = Bindings.getStringSafe(var1.get());
               return var1x.compareTo(var2x) >= 0;
            }

            @Override
            public ObservableList<?> getDependencies() {
               return (ObservableList<?>)(var2.length == 1 ? FXCollections.singletonObservableList(var2[0]) : new ImmutableObservableList<>(var2));
            }
         };
      } else {
         throw new NullPointerException("Operands cannot be null.");
      }
   }

   public static BooleanBinding greaterThanOrEqual(ObservableStringValue var0, ObservableStringValue var1) {
      return greaterThanOrEqual(var0, var1, var0, var1);
   }

   public static BooleanBinding greaterThanOrEqual(ObservableStringValue var0, String var1) {
      return greaterThanOrEqual(var0, StringConstant.valueOf(var1), var0);
   }

   public static BooleanBinding greaterThanOrEqual(String var0, ObservableStringValue var1) {
      return greaterThanOrEqual(StringConstant.valueOf(var0), var1, var1);
   }

   private static BooleanBinding lessThanOrEqual(ObservableStringValue var0, ObservableStringValue var1, Observable... var2) {
      return greaterThanOrEqual(var1, var0, var2);
   }

   public static BooleanBinding lessThanOrEqual(ObservableStringValue var0, ObservableStringValue var1) {
      return lessThanOrEqual(var0, var1, var0, var1);
   }

   public static BooleanBinding lessThanOrEqual(ObservableStringValue var0, String var1) {
      return lessThanOrEqual(var0, StringConstant.valueOf(var1), var0);
   }

   public static BooleanBinding lessThanOrEqual(String var0, ObservableStringValue var1) {
      return lessThanOrEqual(StringConstant.valueOf(var0), var1, var1);
   }

   public static IntegerBinding length(final ObservableStringValue var0) {
      if (var0 == null) {
         throw new NullPointerException("Operand cannot be null");
      } else {
         return new IntegerBinding() {
            {
               super.bind(var0);
            }

            @Override
            public void dispose() {
               super.unbind(var0);
            }

            @Override
            protected int computeValue() {
               return Bindings.getStringSafe(var0.get()).length();
            }

            @Override
            public ObservableList<?> getDependencies() {
               return FXCollections.singletonObservableList(var0);
            }
         };
      }
   }

   public static BooleanBinding isEmpty(final ObservableStringValue var0) {
      if (var0 == null) {
         throw new NullPointerException("Operand cannot be null");
      } else {
         return new BooleanBinding() {
            {
               super.bind(var0);
            }

            @Override
            public void dispose() {
               super.unbind(var0);
            }

            @Override
            protected boolean computeValue() {
               return Bindings.getStringSafe(var0.get()).isEmpty();
            }

            @Override
            public ObservableList<?> getDependencies() {
               return FXCollections.singletonObservableList(var0);
            }
         };
      }
   }

   public static BooleanBinding isNotEmpty(final ObservableStringValue var0) {
      if (var0 == null) {
         throw new NullPointerException("Operand cannot be null");
      } else {
         return new BooleanBinding() {
            {
               super.bind(var0);
            }

            @Override
            public void dispose() {
               super.unbind(var0);
            }

            @Override
            protected boolean computeValue() {
               return !Bindings.getStringSafe(var0.get()).isEmpty();
            }

            @Override
            public ObservableList<?> getDependencies() {
               return FXCollections.singletonObservableList(var0);
            }
         };
      }
   }

   private static BooleanBinding equal(final ObservableObjectValue<?> var0, final ObservableObjectValue<?> var1, final Observable... var2) {
      if (var0 != null && var1 != null) {
         assert var2 != null && var2.length > 0;

         return new BooleanBinding() {
            {
               super.bind(var2);
            }

            @Override
            public void dispose() {
               super.unbind(var2);
            }

            @Override
            protected boolean computeValue() {
               Object var1x = var0.get();
               Object var2x = var1.get();
               return var1x == null ? var2x == null : var1x.equals(var2x);
            }

            @Override
            public ObservableList<?> getDependencies() {
               return (ObservableList<?>)(var2.length == 1 ? FXCollections.singletonObservableList(var2[0]) : new ImmutableObservableList<>(var2));
            }
         };
      } else {
         throw new NullPointerException("Operands cannot be null.");
      }
   }

   public static BooleanBinding equal(ObservableObjectValue<?> var0, ObservableObjectValue<?> var1) {
      return equal(var0, var1, var0, var1);
   }

   public static BooleanBinding equal(ObservableObjectValue<?> var0, Object var1) {
      return equal(var0, ObjectConstant.valueOf(var1), var0);
   }

   public static BooleanBinding equal(Object var0, ObservableObjectValue<?> var1) {
      return equal(ObjectConstant.valueOf(var0), var1, var1);
   }

   private static BooleanBinding notEqual(final ObservableObjectValue<?> var0, final ObservableObjectValue<?> var1, final Observable... var2) {
      if (var0 != null && var1 != null) {
         assert var2 != null && var2.length > 0;

         return new BooleanBinding() {
            {
               super.bind(var2);
            }

            @Override
            public void dispose() {
               super.unbind(var2);
            }

            @Override
            protected boolean computeValue() {
               Object var1x = var0.get();
               Object var2x = var1.get();
               return var1x == null ? var2x != null : !var1x.equals(var2x);
            }

            @Override
            public ObservableList<?> getDependencies() {
               return (ObservableList<?>)(var2.length == 1 ? FXCollections.singletonObservableList(var2[0]) : new ImmutableObservableList<>(var2));
            }
         };
      } else {
         throw new NullPointerException("Operands cannot be null.");
      }
   }

   public static BooleanBinding notEqual(ObservableObjectValue<?> var0, ObservableObjectValue<?> var1) {
      return notEqual(var0, var1, var0, var1);
   }

   public static BooleanBinding notEqual(ObservableObjectValue<?> var0, Object var1) {
      return notEqual(var0, ObjectConstant.valueOf(var1), var0);
   }

   public static BooleanBinding notEqual(Object var0, ObservableObjectValue<?> var1) {
      return notEqual(ObjectConstant.valueOf(var0), var1, var1);
   }

   public static BooleanBinding isNull(final ObservableObjectValue<?> var0) {
      if (var0 == null) {
         throw new NullPointerException("Operand cannot be null.");
      } else {
         return new BooleanBinding() {
            {
               super.bind(var0);
            }

            @Override
            public void dispose() {
               super.unbind(var0);
            }

            @Override
            protected boolean computeValue() {
               return var0.get() == null;
            }

            @Override
            public ObservableList<?> getDependencies() {
               return FXCollections.singletonObservableList(var0);
            }
         };
      }
   }

   public static BooleanBinding isNotNull(final ObservableObjectValue<?> var0) {
      if (var0 == null) {
         throw new NullPointerException("Operand cannot be null.");
      } else {
         return new BooleanBinding() {
            {
               super.bind(var0);
            }

            @Override
            public void dispose() {
               super.unbind(var0);
            }

            @Override
            protected boolean computeValue() {
               return var0.get() != null;
            }

            @Override
            public ObservableList<?> getDependencies() {
               return FXCollections.singletonObservableList(var0);
            }
         };
      }
   }

   public static <E> IntegerBinding size(final ObservableList<E> var0) {
      if (var0 == null) {
         throw new NullPointerException("List cannot be null.");
      } else {
         return new IntegerBinding() {
            {
               super.bind(var0);
            }

            @Override
            public void dispose() {
               super.unbind(var0);
            }

            @Override
            protected int computeValue() {
               return var0.size();
            }

            @Override
            public ObservableList<?> getDependencies() {
               return FXCollections.singletonObservableList(var0);
            }
         };
      }
   }

   public static <E> BooleanBinding isEmpty(final ObservableList<E> var0) {
      if (var0 == null) {
         throw new NullPointerException("List cannot be null.");
      } else {
         return new BooleanBinding() {
            {
               super.bind(var0);
            }

            @Override
            public void dispose() {
               super.unbind(var0);
            }

            @Override
            protected boolean computeValue() {
               return var0.isEmpty();
            }

            @Override
            public ObservableList<?> getDependencies() {
               return FXCollections.singletonObservableList(var0);
            }
         };
      }
   }

   public static <E> BooleanBinding isNotEmpty(final ObservableList<E> var0) {
      if (var0 == null) {
         throw new NullPointerException("List cannot be null.");
      } else {
         return new BooleanBinding() {
            {
               super.bind(var0);
            }

            @Override
            public void dispose() {
               super.unbind(var0);
            }

            @Override
            protected boolean computeValue() {
               return !var0.isEmpty();
            }

            @Override
            public ObservableList<?> getDependencies() {
               return FXCollections.singletonObservableList(var0);
            }
         };
      }
   }

   public static <E> ObjectBinding<E> valueAt(final ObservableList<E> var0, final int var1) {
      if (var0 == null) {
         throw new NullPointerException("List cannot be null.");
      } else if (var1 < 0) {
         throw new IllegalArgumentException("Index cannot be negative");
      } else {
         return new ObjectBinding<E>() {
            {
               super.bind(var0);
            }

            @Override
            public void dispose() {
               super.unbind(var0);
            }

            @Override
            protected E computeValue() {
               try {
                  return (E)var0.get(var1);
               } catch (IndexOutOfBoundsException var2) {
                  Logging.getLogger().fine("Exception while evaluating binding", var2);
                  return null;
               }
            }

            @Override
            public ObservableList<?> getDependencies() {
               return FXCollections.singletonObservableList(var0);
            }
         };
      }
   }

   public static <E> ObjectBinding<E> valueAt(ObservableList<E> var0, ObservableIntegerValue var1) {
      return valueAt(var0, (ObservableNumberValue)var1);
   }

   public static <E> ObjectBinding<E> valueAt(final ObservableList<E> var0, final ObservableNumberValue var1) {
      if (var0 != null && var1 != null) {
         return new ObjectBinding<E>() {
            {
               super.bind(var0, var1);
            }

            @Override
            public void dispose() {
               super.unbind(var0, var1);
            }

            @Override
            protected E computeValue() {
               try {
                  return (E)var0.get(var1.intValue());
               } catch (IndexOutOfBoundsException var2) {
                  Logging.getLogger().fine("Exception while evaluating binding", var2);
                  return null;
               }
            }

            @Override
            public ObservableList<?> getDependencies() {
               return new ImmutableObservableList<>(var0, var1);
            }
         };
      } else {
         throw new NullPointerException("Operands cannot be null.");
      }
   }

   public static BooleanBinding booleanValueAt(final ObservableList<Boolean> var0, final int var1) {
      if (var0 == null) {
         throw new NullPointerException("List cannot be null.");
      } else if (var1 < 0) {
         throw new IllegalArgumentException("Index cannot be negative");
      } else {
         return new BooleanBinding() {
            {
               super.bind(var0);
            }

            @Override
            public void dispose() {
               super.unbind(var0);
            }

            @Override
            protected boolean computeValue() {
               try {
                  Boolean var1x = (Boolean)var0.get(var1);
                  if (var1x != null) {
                     return var1x;
                  }

                  Logging.getLogger().fine("List element is null, returning default value instead.", new NullPointerException());
               } catch (IndexOutOfBoundsException var2) {
                  Logging.getLogger().fine("Exception while evaluating binding", var2);
               }

               return false;
            }

            @Override
            public ObservableList<?> getDependencies() {
               return FXCollections.singletonObservableList(var0);
            }
         };
      }
   }

   public static BooleanBinding booleanValueAt(ObservableList<Boolean> var0, ObservableIntegerValue var1) {
      return booleanValueAt(var0, (ObservableNumberValue)var1);
   }

   public static BooleanBinding booleanValueAt(final ObservableList<Boolean> var0, final ObservableNumberValue var1) {
      if (var0 != null && var1 != null) {
         return new BooleanBinding() {
            {
               super.bind(var0, var1);
            }

            @Override
            public void dispose() {
               super.unbind(var0, var1);
            }

            @Override
            protected boolean computeValue() {
               try {
                  Boolean var1x = (Boolean)var0.get(var1.intValue());
                  if (var1x != null) {
                     return var1x;
                  }

                  Logging.getLogger().fine("List element is null, returning default value instead.", new NullPointerException());
               } catch (IndexOutOfBoundsException var2) {
                  Logging.getLogger().fine("Exception while evaluating binding", var2);
               }

               return false;
            }

            @Override
            public ObservableList<?> getDependencies() {
               return new ImmutableObservableList<>(var0, var1);
            }
         };
      } else {
         throw new NullPointerException("Operands cannot be null.");
      }
   }

   public static DoubleBinding doubleValueAt(final ObservableList<? extends Number> var0, final int var1) {
      if (var0 == null) {
         throw new NullPointerException("List cannot be null.");
      } else if (var1 < 0) {
         throw new IllegalArgumentException("Index cannot be negative");
      } else {
         return new DoubleBinding() {
            {
               super.bind(var0);
            }

            @Override
            public void dispose() {
               super.unbind(var0);
            }

            @Override
            protected double computeValue() {
               try {
                  Number var1x = (Number)var0.get(var1);
                  if (var1x != null) {
                     return var1x.doubleValue();
                  }

                  Logging.getLogger().fine("List element is null, returning default value instead.", new NullPointerException());
               } catch (IndexOutOfBoundsException var2) {
                  Logging.getLogger().fine("Exception while evaluating binding", var2);
               }

               return 0.0;
            }

            @Override
            public ObservableList<?> getDependencies() {
               return FXCollections.singletonObservableList(var0);
            }
         };
      }
   }

   public static DoubleBinding doubleValueAt(ObservableList<? extends Number> var0, ObservableIntegerValue var1) {
      return doubleValueAt(var0, (ObservableNumberValue)var1);
   }

   public static DoubleBinding doubleValueAt(final ObservableList<? extends Number> var0, final ObservableNumberValue var1) {
      if (var0 != null && var1 != null) {
         return new DoubleBinding() {
            {
               super.bind(var0, var1);
            }

            @Override
            public void dispose() {
               super.unbind(var0, var1);
            }

            @Override
            protected double computeValue() {
               try {
                  Number var1x = (Number)var0.get(var1.intValue());
                  if (var1x != null) {
                     return var1x.doubleValue();
                  }

                  Logging.getLogger().fine("List element is null, returning default value instead.", new NullPointerException());
               } catch (IndexOutOfBoundsException var2) {
                  Logging.getLogger().fine("Exception while evaluating binding", var2);
               }

               return 0.0;
            }

            @Override
            public ObservableList<?> getDependencies() {
               return new ImmutableObservableList<>(var0, var1);
            }
         };
      } else {
         throw new NullPointerException("Operands cannot be null.");
      }
   }

   public static FloatBinding floatValueAt(final ObservableList<? extends Number> var0, final int var1) {
      if (var0 == null) {
         throw new NullPointerException("List cannot be null.");
      } else if (var1 < 0) {
         throw new IllegalArgumentException("Index cannot be negative");
      } else {
         return new FloatBinding() {
            {
               super.bind(var0);
            }

            @Override
            public void dispose() {
               super.unbind(var0);
            }

            @Override
            protected float computeValue() {
               try {
                  Number var1x = (Number)var0.get(var1);
                  if (var1x != null) {
                     return var1x.floatValue();
                  }

                  Logging.getLogger().fine("List element is null, returning default value instead.", new NullPointerException());
               } catch (IndexOutOfBoundsException var2) {
                  Logging.getLogger().fine("Exception while evaluating binding", var2);
               }

               return 0.0F;
            }

            @Override
            public ObservableList<?> getDependencies() {
               return FXCollections.singletonObservableList(var0);
            }
         };
      }
   }

   public static FloatBinding floatValueAt(ObservableList<? extends Number> var0, ObservableIntegerValue var1) {
      return floatValueAt(var0, (ObservableNumberValue)var1);
   }

   public static FloatBinding floatValueAt(final ObservableList<? extends Number> var0, final ObservableNumberValue var1) {
      if (var0 != null && var1 != null) {
         return new FloatBinding() {
            {
               super.bind(var0, var1);
            }

            @Override
            public void dispose() {
               super.unbind(var0, var1);
            }

            @Override
            protected float computeValue() {
               try {
                  Number var1x = (Number)var0.get(var1.intValue());
                  if (var1x != null) {
                     return var1x.floatValue();
                  }

                  Logging.getLogger().fine("List element is null, returning default value instead.", new NullPointerException());
               } catch (IndexOutOfBoundsException var2) {
                  Logging.getLogger().fine("Exception while evaluating binding", var2);
               }

               return 0.0F;
            }

            @Override
            public ObservableList<?> getDependencies() {
               return new ImmutableObservableList<>(var0, var1);
            }
         };
      } else {
         throw new NullPointerException("Operands cannot be null.");
      }
   }

   public static IntegerBinding integerValueAt(final ObservableList<? extends Number> var0, final int var1) {
      if (var0 == null) {
         throw new NullPointerException("List cannot be null.");
      } else if (var1 < 0) {
         throw new IllegalArgumentException("Index cannot be negative");
      } else {
         return new IntegerBinding() {
            {
               super.bind(var0);
            }

            @Override
            public void dispose() {
               super.unbind(var0);
            }

            @Override
            protected int computeValue() {
               try {
                  Number var1x = (Number)var0.get(var1);
                  if (var1x != null) {
                     return var1x.intValue();
                  }

                  Logging.getLogger().fine("List element is null, returning default value instead.", new NullPointerException());
               } catch (IndexOutOfBoundsException var2) {
                  Logging.getLogger().fine("Exception while evaluating binding", var2);
               }

               return 0;
            }

            @Override
            public ObservableList<?> getDependencies() {
               return FXCollections.singletonObservableList(var0);
            }
         };
      }
   }

   public static IntegerBinding integerValueAt(ObservableList<? extends Number> var0, ObservableIntegerValue var1) {
      return integerValueAt(var0, (ObservableNumberValue)var1);
   }

   public static IntegerBinding integerValueAt(final ObservableList<? extends Number> var0, final ObservableNumberValue var1) {
      if (var0 != null && var1 != null) {
         return new IntegerBinding() {
            {
               super.bind(var0, var1);
            }

            @Override
            public void dispose() {
               super.unbind(var0, var1);
            }

            @Override
            protected int computeValue() {
               try {
                  Number var1x = (Number)var0.get(var1.intValue());
                  if (var1x != null) {
                     return var1x.intValue();
                  }

                  Logging.getLogger().fine("List element is null, returning default value instead.", new NullPointerException());
               } catch (IndexOutOfBoundsException var2) {
                  Logging.getLogger().fine("Exception while evaluating binding", var2);
               }

               return 0;
            }

            @Override
            public ObservableList<?> getDependencies() {
               return new ImmutableObservableList<>(var0, var1);
            }
         };
      } else {
         throw new NullPointerException("Operands cannot be null.");
      }
   }

   public static LongBinding longValueAt(final ObservableList<? extends Number> var0, final int var1) {
      if (var0 == null) {
         throw new NullPointerException("List cannot be null.");
      } else if (var1 < 0) {
         throw new IllegalArgumentException("Index cannot be negative");
      } else {
         return new LongBinding() {
            {
               super.bind(var0);
            }

            @Override
            public void dispose() {
               super.unbind(var0);
            }

            @Override
            protected long computeValue() {
               try {
                  Number var1x = (Number)var0.get(var1);
                  if (var1x != null) {
                     return var1x.longValue();
                  }

                  Logging.getLogger().fine("List element is null, returning default value instead.", new NullPointerException());
               } catch (IndexOutOfBoundsException var2) {
                  Logging.getLogger().fine("Exception while evaluating binding", var2);
               }

               return 0L;
            }

            @Override
            public ObservableList<?> getDependencies() {
               return FXCollections.singletonObservableList(var0);
            }
         };
      }
   }

   public static LongBinding longValueAt(ObservableList<? extends Number> var0, ObservableIntegerValue var1) {
      return longValueAt(var0, (ObservableNumberValue)var1);
   }

   public static LongBinding longValueAt(final ObservableList<? extends Number> var0, final ObservableNumberValue var1) {
      if (var0 != null && var1 != null) {
         return new LongBinding() {
            {
               super.bind(var0, var1);
            }

            @Override
            public void dispose() {
               super.unbind(var0, var1);
            }

            @Override
            protected long computeValue() {
               try {
                  Number var1x = (Number)var0.get(var1.intValue());
                  if (var1x != null) {
                     return var1x.longValue();
                  }

                  Logging.getLogger().fine("List element is null, returning default value instead.", new NullPointerException());
               } catch (IndexOutOfBoundsException var2) {
                  Logging.getLogger().fine("Exception while evaluating binding", var2);
               }

               return 0L;
            }

            @Override
            public ObservableList<?> getDependencies() {
               return new ImmutableObservableList<>(var0, var1);
            }
         };
      } else {
         throw new NullPointerException("Operands cannot be null.");
      }
   }

   public static StringBinding stringValueAt(final ObservableList<String> var0, final int var1) {
      if (var0 == null) {
         throw new NullPointerException("List cannot be null.");
      } else if (var1 < 0) {
         throw new IllegalArgumentException("Index cannot be negative");
      } else {
         return new StringBinding() {
            {
               super.bind(var0);
            }

            @Override
            public void dispose() {
               super.unbind(var0);
            }

            @Override
            protected String computeValue() {
               try {
                  return (String)var0.get(var1);
               } catch (IndexOutOfBoundsException var2) {
                  Logging.getLogger().fine("Exception while evaluating binding", var2);
                  return null;
               }
            }

            @Override
            public ObservableList<?> getDependencies() {
               return FXCollections.singletonObservableList(var0);
            }
         };
      }
   }

   public static StringBinding stringValueAt(ObservableList<String> var0, ObservableIntegerValue var1) {
      return stringValueAt(var0, (ObservableNumberValue)var1);
   }

   public static StringBinding stringValueAt(final ObservableList<String> var0, final ObservableNumberValue var1) {
      if (var0 != null && var1 != null) {
         return new StringBinding() {
            {
               super.bind(var0, var1);
            }

            @Override
            public void dispose() {
               super.unbind(var0, var1);
            }

            @Override
            protected String computeValue() {
               try {
                  return (String)var0.get(var1.intValue());
               } catch (IndexOutOfBoundsException var2) {
                  Logging.getLogger().fine("Exception while evaluating binding", var2);
                  return null;
               }
            }

            @Override
            public ObservableList<?> getDependencies() {
               return new ImmutableObservableList<>(var0, var1);
            }
         };
      } else {
         throw new NullPointerException("Operands cannot be null.");
      }
   }

   public static <E> IntegerBinding size(final ObservableSet<E> var0) {
      if (var0 == null) {
         throw new NullPointerException("Set cannot be null.");
      } else {
         return new IntegerBinding() {
            {
               super.bind(var0);
            }

            @Override
            public void dispose() {
               super.unbind(var0);
            }

            @Override
            protected int computeValue() {
               return var0.size();
            }

            @Override
            public ObservableList<?> getDependencies() {
               return FXCollections.singletonObservableList(var0);
            }
         };
      }
   }

   public static <E> BooleanBinding isEmpty(final ObservableSet<E> var0) {
      if (var0 == null) {
         throw new NullPointerException("Set cannot be null.");
      } else {
         return new BooleanBinding() {
            {
               super.bind(var0);
            }

            @Override
            public void dispose() {
               super.unbind(var0);
            }

            @Override
            protected boolean computeValue() {
               return var0.isEmpty();
            }

            @Override
            public ObservableList<?> getDependencies() {
               return FXCollections.singletonObservableList(var0);
            }
         };
      }
   }

   public static <E> BooleanBinding isNotEmpty(final ObservableSet<E> var0) {
      if (var0 == null) {
         throw new NullPointerException("List cannot be null.");
      } else {
         return new BooleanBinding() {
            {
               super.bind(var0);
            }

            @Override
            public void dispose() {
               super.unbind(var0);
            }

            @Override
            protected boolean computeValue() {
               return !var0.isEmpty();
            }

            @Override
            public ObservableList<?> getDependencies() {
               return FXCollections.singletonObservableList(var0);
            }
         };
      }
   }

   public static IntegerBinding size(final ObservableArray var0) {
      if (var0 == null) {
         throw new NullPointerException("Array cannot be null.");
      } else {
         return new IntegerBinding() {
            {
               super.bind(var0);
            }

            @Override
            public void dispose() {
               super.unbind(var0);
            }

            @Override
            protected int computeValue() {
               return var0.size();
            }

            @Override
            public ObservableList<?> getDependencies() {
               return FXCollections.singletonObservableList(var0);
            }
         };
      }
   }

   public static FloatBinding floatValueAt(final ObservableFloatArray var0, final int var1) {
      if (var0 == null) {
         throw new NullPointerException("Array cannot be null.");
      } else if (var1 < 0) {
         throw new IllegalArgumentException("Index cannot be negative");
      } else {
         return new FloatBinding() {
            {
               super.bind(var0);
            }

            @Override
            public void dispose() {
               super.unbind(var0);
            }

            @Override
            protected float computeValue() {
               try {
                  return var0.get(var1);
               } catch (IndexOutOfBoundsException var2) {
                  Logging.getLogger().fine("Exception while evaluating binding", var2);
                  return 0.0F;
               }
            }

            @Override
            public ObservableList<?> getDependencies() {
               return FXCollections.singletonObservableList(var0);
            }
         };
      }
   }

   public static FloatBinding floatValueAt(ObservableFloatArray var0, ObservableIntegerValue var1) {
      return floatValueAt(var0, (ObservableNumberValue)var1);
   }

   public static FloatBinding floatValueAt(final ObservableFloatArray var0, final ObservableNumberValue var1) {
      if (var0 != null && var1 != null) {
         return new FloatBinding() {
            {
               super.bind(var0, var1);
            }

            @Override
            public void dispose() {
               super.unbind(var0, var1);
            }

            @Override
            protected float computeValue() {
               try {
                  return var0.get(var1.intValue());
               } catch (IndexOutOfBoundsException var2) {
                  Logging.getLogger().fine("Exception while evaluating binding", var2);
                  return 0.0F;
               }
            }

            @Override
            public ObservableList<?> getDependencies() {
               return new ImmutableObservableList<>(var0, var1);
            }
         };
      } else {
         throw new NullPointerException("Operands cannot be null.");
      }
   }

   public static IntegerBinding integerValueAt(final ObservableIntegerArray var0, final int var1) {
      if (var0 == null) {
         throw new NullPointerException("Array cannot be null.");
      } else if (var1 < 0) {
         throw new IllegalArgumentException("Index cannot be negative");
      } else {
         return new IntegerBinding() {
            {
               super.bind(var0);
            }

            @Override
            public void dispose() {
               super.unbind(var0);
            }

            @Override
            protected int computeValue() {
               try {
                  return var0.get(var1);
               } catch (IndexOutOfBoundsException var2) {
                  Logging.getLogger().fine("Exception while evaluating binding", var2);
                  return 0;
               }
            }

            @Override
            public ObservableList<?> getDependencies() {
               return FXCollections.singletonObservableList(var0);
            }
         };
      }
   }

   public static IntegerBinding integerValueAt(ObservableIntegerArray var0, ObservableIntegerValue var1) {
      return integerValueAt(var0, (ObservableNumberValue)var1);
   }

   public static IntegerBinding integerValueAt(final ObservableIntegerArray var0, final ObservableNumberValue var1) {
      if (var0 != null && var1 != null) {
         return new IntegerBinding() {
            {
               super.bind(var0, var1);
            }

            @Override
            public void dispose() {
               super.unbind(var0, var1);
            }

            @Override
            protected int computeValue() {
               try {
                  return var0.get(var1.intValue());
               } catch (IndexOutOfBoundsException var2) {
                  Logging.getLogger().fine("Exception while evaluating binding", var2);
                  return 0;
               }
            }

            @Override
            public ObservableList<?> getDependencies() {
               return new ImmutableObservableList<>(var0, var1);
            }
         };
      } else {
         throw new NullPointerException("Operands cannot be null.");
      }
   }

   public static <K, V> IntegerBinding size(final ObservableMap<K, V> var0) {
      if (var0 == null) {
         throw new NullPointerException("Map cannot be null.");
      } else {
         return new IntegerBinding() {
            {
               super.bind(var0);
            }

            @Override
            public void dispose() {
               super.unbind(var0);
            }

            @Override
            protected int computeValue() {
               return var0.size();
            }

            @Override
            public ObservableList<?> getDependencies() {
               return FXCollections.singletonObservableList(var0);
            }
         };
      }
   }

   public static <K, V> BooleanBinding isEmpty(final ObservableMap<K, V> var0) {
      if (var0 == null) {
         throw new NullPointerException("Map cannot be null.");
      } else {
         return new BooleanBinding() {
            {
               super.bind(var0);
            }

            @Override
            public void dispose() {
               super.unbind(var0);
            }

            @Override
            protected boolean computeValue() {
               return var0.isEmpty();
            }

            @Override
            public ObservableList<?> getDependencies() {
               return FXCollections.singletonObservableList(var0);
            }
         };
      }
   }

   public static <K, V> BooleanBinding isNotEmpty(final ObservableMap<K, V> var0) {
      if (var0 == null) {
         throw new NullPointerException("List cannot be null.");
      } else {
         return new BooleanBinding() {
            {
               super.bind(var0);
            }

            @Override
            public void dispose() {
               super.unbind(var0);
            }

            @Override
            protected boolean computeValue() {
               return !var0.isEmpty();
            }

            @Override
            public ObservableList<?> getDependencies() {
               return FXCollections.singletonObservableList(var0);
            }
         };
      }
   }

   public static <K, V> ObjectBinding<V> valueAt(final ObservableMap<K, V> var0, final K var1) {
      if (var0 == null) {
         throw new NullPointerException("Map cannot be null.");
      } else {
         return new ObjectBinding<V>() {
            {
               super.bind(var0);
            }

            @Override
            public void dispose() {
               super.unbind(var0);
            }

            @Override
            protected V computeValue() {
               try {
                  return (V)var0.get(var1);
               } catch (ClassCastException var2) {
                  Logging.getLogger().warning("Exception while evaluating binding", var2);
               } catch (NullPointerException var3) {
                  Logging.getLogger().warning("Exception while evaluating binding", var3);
               }

               return null;
            }

            @Override
            public ObservableList<?> getDependencies() {
               return FXCollections.singletonObservableList(var0);
            }
         };
      }
   }

   public static <K, V> ObjectBinding<V> valueAt(final ObservableMap<K, V> var0, final ObservableValue<? extends K> var1) {
      if (var0 != null && var1 != null) {
         return new ObjectBinding<V>() {
            {
               super.bind(var0, var1);
            }

            @Override
            public void dispose() {
               super.unbind(var0);
            }

            @Override
            protected V computeValue() {
               try {
                  return (V)var0.get(var1.getValue());
               } catch (ClassCastException var2) {
                  Logging.getLogger().warning("Exception while evaluating binding", var2);
               } catch (NullPointerException var3) {
                  Logging.getLogger().warning("Exception while evaluating binding", var3);
               }

               return null;
            }

            @Override
            public ObservableList<?> getDependencies() {
               return new ImmutableObservableList<>(var0, var1);
            }
         };
      } else {
         throw new NullPointerException("Operands cannot be null.");
      }
   }

   public static <K> BooleanBinding booleanValueAt(final ObservableMap<K, Boolean> var0, final K var1) {
      if (var0 == null) {
         throw new NullPointerException("Map cannot be null.");
      } else {
         return new BooleanBinding() {
            {
               super.bind(var0);
            }

            @Override
            public void dispose() {
               super.unbind(var0);
            }

            @Override
            protected boolean computeValue() {
               try {
                  Boolean var1x = (Boolean)var0.get(var1);
                  if (var1x != null) {
                     return var1x;
                  }

                  Logging.getLogger().fine("Element not found in map, returning default value instead.", new NullPointerException());
               } catch (ClassCastException var2) {
                  Logging.getLogger().warning("Exception while evaluating binding", var2);
               } catch (NullPointerException var3) {
                  Logging.getLogger().warning("Exception while evaluating binding", var3);
               }

               return false;
            }

            @Override
            public ObservableList<?> getDependencies() {
               return FXCollections.singletonObservableList(var0);
            }
         };
      }
   }

   public static <K> BooleanBinding booleanValueAt(final ObservableMap<K, Boolean> var0, final ObservableValue<? extends K> var1) {
      if (var0 != null && var1 != null) {
         return new BooleanBinding() {
            {
               super.bind(var0, var1);
            }

            @Override
            public void dispose() {
               super.unbind(var0, var1);
            }

            @Override
            protected boolean computeValue() {
               try {
                  Boolean var1x = (Boolean)var0.get(var1.getValue());
                  if (var1x != null) {
                     return var1x;
                  }

                  Logging.getLogger().fine("Element not found in map, returning default value instead.", new NullPointerException());
               } catch (ClassCastException var2) {
                  Logging.getLogger().warning("Exception while evaluating binding", var2);
               } catch (NullPointerException var3) {
                  Logging.getLogger().warning("Exception while evaluating binding", var3);
               }

               return false;
            }

            @Override
            public ObservableList<?> getDependencies() {
               return new ImmutableObservableList<>(var0, var1);
            }
         };
      } else {
         throw new NullPointerException("Operands cannot be null.");
      }
   }

   public static <K> DoubleBinding doubleValueAt(final ObservableMap<K, ? extends Number> var0, final K var1) {
      if (var0 == null) {
         throw new NullPointerException("Map cannot be null.");
      } else {
         return new DoubleBinding() {
            {
               super.bind(var0);
            }

            @Override
            public void dispose() {
               super.unbind(var0);
            }

            @Override
            protected double computeValue() {
               try {
                  Number var1x = (Number)var0.get(var1);
                  if (var1x != null) {
                     return var1x.doubleValue();
                  }

                  Logging.getLogger().fine("Element not found in map, returning default value instead.", new NullPointerException());
               } catch (ClassCastException var2) {
                  Logging.getLogger().warning("Exception while evaluating binding", var2);
               } catch (NullPointerException var3) {
                  Logging.getLogger().warning("Exception while evaluating binding", var3);
               }

               return 0.0;
            }

            @Override
            public ObservableList<?> getDependencies() {
               return FXCollections.singletonObservableList(var0);
            }
         };
      }
   }

   public static <K> DoubleBinding doubleValueAt(final ObservableMap<K, ? extends Number> var0, final ObservableValue<? extends K> var1) {
      if (var0 != null && var1 != null) {
         return new DoubleBinding() {
            {
               super.bind(var0, var1);
            }

            @Override
            public void dispose() {
               super.unbind(var0, var1);
            }

            @Override
            protected double computeValue() {
               try {
                  Number var1x = (Number)var0.get(var1.getValue());
                  if (var1x != null) {
                     return var1x.doubleValue();
                  }

                  Logging.getLogger().fine("Element not found in map, returning default value instead.", new NullPointerException());
               } catch (ClassCastException var2) {
                  Logging.getLogger().warning("Exception while evaluating binding", var2);
               } catch (NullPointerException var3) {
                  Logging.getLogger().warning("Exception while evaluating binding", var3);
               }

               return 0.0;
            }

            @Override
            public ObservableList<?> getDependencies() {
               return new ImmutableObservableList<>(var0, var1);
            }
         };
      } else {
         throw new NullPointerException("Operands cannot be null.");
      }
   }

   public static <K> FloatBinding floatValueAt(final ObservableMap<K, ? extends Number> var0, final K var1) {
      if (var0 == null) {
         throw new NullPointerException("Map cannot be null.");
      } else {
         return new FloatBinding() {
            {
               super.bind(var0);
            }

            @Override
            public void dispose() {
               super.unbind(var0);
            }

            @Override
            protected float computeValue() {
               try {
                  Number var1x = (Number)var0.get(var1);
                  if (var1x != null) {
                     return var1x.floatValue();
                  }

                  Logging.getLogger().fine("Element not found in map, returning default value instead.", new NullPointerException());
               } catch (ClassCastException var2) {
                  Logging.getLogger().warning("Exception while evaluating binding", var2);
               } catch (NullPointerException var3) {
                  Logging.getLogger().warning("Exception while evaluating binding", var3);
               }

               return 0.0F;
            }

            @Override
            public ObservableList<?> getDependencies() {
               return FXCollections.singletonObservableList(var0);
            }
         };
      }
   }

   public static <K> FloatBinding floatValueAt(final ObservableMap<K, ? extends Number> var0, final ObservableValue<? extends K> var1) {
      if (var0 != null && var1 != null) {
         return new FloatBinding() {
            {
               super.bind(var0, var1);
            }

            @Override
            public void dispose() {
               super.unbind(var0, var1);
            }

            @Override
            protected float computeValue() {
               try {
                  Number var1x = (Number)var0.get(var1.getValue());
                  if (var1x != null) {
                     return var1x.floatValue();
                  }

                  Logging.getLogger().fine("Element not found in map, returning default value instead.", new NullPointerException());
               } catch (ClassCastException var2) {
                  Logging.getLogger().warning("Exception while evaluating binding", var2);
               } catch (NullPointerException var3) {
                  Logging.getLogger().warning("Exception while evaluating binding", var3);
               }

               return 0.0F;
            }

            @Override
            public ObservableList<?> getDependencies() {
               return new ImmutableObservableList<>(var0, var1);
            }
         };
      } else {
         throw new NullPointerException("Operands cannot be null.");
      }
   }

   public static <K> IntegerBinding integerValueAt(final ObservableMap<K, ? extends Number> var0, final K var1) {
      if (var0 == null) {
         throw new NullPointerException("Map cannot be null.");
      } else {
         return new IntegerBinding() {
            {
               super.bind(var0);
            }

            @Override
            public void dispose() {
               super.unbind(var0);
            }

            @Override
            protected int computeValue() {
               try {
                  Number var1x = (Number)var0.get(var1);
                  if (var1x != null) {
                     return var1x.intValue();
                  }

                  Logging.getLogger().fine("Element not found in map, returning default value instead.", new NullPointerException());
               } catch (ClassCastException var2) {
                  Logging.getLogger().warning("Exception while evaluating binding", var2);
               } catch (NullPointerException var3) {
                  Logging.getLogger().warning("Exception while evaluating binding", var3);
               }

               return 0;
            }

            @Override
            public ObservableList<?> getDependencies() {
               return FXCollections.singletonObservableList(var0);
            }
         };
      }
   }

   public static <K> IntegerBinding integerValueAt(final ObservableMap<K, ? extends Number> var0, final ObservableValue<? extends K> var1) {
      if (var0 != null && var1 != null) {
         return new IntegerBinding() {
            {
               super.bind(var0, var1);
            }

            @Override
            public void dispose() {
               super.unbind(var0, var1);
            }

            @Override
            protected int computeValue() {
               try {
                  Number var1x = (Number)var0.get(var1.getValue());
                  if (var1x != null) {
                     return var1x.intValue();
                  }

                  Logging.getLogger().fine("Element not found in map, returning default value instead.", new NullPointerException());
               } catch (ClassCastException var2) {
                  Logging.getLogger().warning("Exception while evaluating binding", var2);
               } catch (NullPointerException var3) {
                  Logging.getLogger().warning("Exception while evaluating binding", var3);
               }

               return 0;
            }

            @Override
            public ObservableList<?> getDependencies() {
               return new ImmutableObservableList<>(var0, var1);
            }
         };
      } else {
         throw new NullPointerException("Operands cannot be null.");
      }
   }

   public static <K> LongBinding longValueAt(final ObservableMap<K, ? extends Number> var0, final K var1) {
      if (var0 == null) {
         throw new NullPointerException("Map cannot be null.");
      } else {
         return new LongBinding() {
            {
               super.bind(var0);
            }

            @Override
            public void dispose() {
               super.unbind(var0);
            }

            @Override
            protected long computeValue() {
               try {
                  Number var1x = (Number)var0.get(var1);
                  if (var1x != null) {
                     return var1x.longValue();
                  }

                  Logging.getLogger().fine("Element not found in map, returning default value instead.", new NullPointerException());
               } catch (ClassCastException var2) {
                  Logging.getLogger().warning("Exception while evaluating binding", var2);
               } catch (NullPointerException var3) {
                  Logging.getLogger().warning("Exception while evaluating binding", var3);
               }

               return 0L;
            }

            @Override
            public ObservableList<?> getDependencies() {
               return FXCollections.singletonObservableList(var0);
            }
         };
      }
   }

   public static <K> LongBinding longValueAt(final ObservableMap<K, ? extends Number> var0, final ObservableValue<? extends K> var1) {
      if (var0 != null && var1 != null) {
         return new LongBinding() {
            {
               super.bind(var0, var1);
            }

            @Override
            public void dispose() {
               super.unbind(var0, var1);
            }

            @Override
            protected long computeValue() {
               try {
                  Number var1x = (Number)var0.get(var1.getValue());
                  if (var1x != null) {
                     return var1x.longValue();
                  }

                  Logging.getLogger().fine("Element not found in map, returning default value instead.", new NullPointerException());
               } catch (ClassCastException var2) {
                  Logging.getLogger().warning("Exception while evaluating binding", var2);
               } catch (NullPointerException var3) {
                  Logging.getLogger().warning("Exception while evaluating binding", var3);
               }

               return 0L;
            }

            @Override
            public ObservableList<?> getDependencies() {
               return new ImmutableObservableList<>(var0, var1);
            }
         };
      } else {
         throw new NullPointerException("Operands cannot be null.");
      }
   }

   public static <K> StringBinding stringValueAt(final ObservableMap<K, String> var0, final K var1) {
      if (var0 == null) {
         throw new NullPointerException("Map cannot be null.");
      } else {
         return new StringBinding() {
            {
               super.bind(var0);
            }

            @Override
            public void dispose() {
               super.unbind(var0);
            }

            @Override
            protected String computeValue() {
               try {
                  return (String)var0.get(var1);
               } catch (ClassCastException var2) {
                  Logging.getLogger().warning("Exception while evaluating binding", var2);
               } catch (NullPointerException var3) {
                  Logging.getLogger().warning("Exception while evaluating binding", var3);
               }

               return null;
            }

            @Override
            public ObservableList<?> getDependencies() {
               return FXCollections.singletonObservableList(var0);
            }
         };
      }
   }

   public static <K> StringBinding stringValueAt(final ObservableMap<K, String> var0, final ObservableValue<? extends K> var1) {
      if (var0 != null && var1 != null) {
         return new StringBinding() {
            {
               super.bind(var0, var1);
            }

            @Override
            public void dispose() {
               super.unbind(var0, var1);
            }

            @Override
            protected String computeValue() {
               try {
                  return (String)var0.get(var1.getValue());
               } catch (ClassCastException var2) {
                  Logging.getLogger().warning("Exception while evaluating binding", var2);
               } catch (NullPointerException var3) {
                  Logging.getLogger().warning("Exception while evaluating binding", var3);
               }

               return null;
            }

            @Override
            public ObservableList<?> getDependencies() {
               return new ImmutableObservableList<>(var0, var1);
            }
         };
      } else {
         throw new NullPointerException("Operands cannot be null.");
      }
   }

   private static class BooleanAndBinding extends BooleanBinding {
      private final ObservableBooleanValue op1;
      private final ObservableBooleanValue op2;
      private final InvalidationListener observer;

      public BooleanAndBinding(ObservableBooleanValue var1, ObservableBooleanValue var2) {
         this.op1 = var1;
         this.op2 = var2;
         this.observer = new Bindings.ShortCircuitAndInvalidator(this);
         var1.addListener(this.observer);
         var2.addListener(this.observer);
      }

      @Override
      public void dispose() {
         this.op1.removeListener(this.observer);
         this.op2.removeListener(this.observer);
      }

      @Override
      protected boolean computeValue() {
         return this.op1.get() && this.op2.get();
      }

      @Override
      public ObservableList<?> getDependencies() {
         return new ImmutableObservableList<>(this.op1, this.op2);
      }
   }

   private static class BooleanOrBinding extends BooleanBinding {
      private final ObservableBooleanValue op1;
      private final ObservableBooleanValue op2;
      private final InvalidationListener observer;

      public BooleanOrBinding(ObservableBooleanValue var1, ObservableBooleanValue var2) {
         this.op1 = var1;
         this.op2 = var2;
         this.observer = new Bindings.ShortCircuitOrInvalidator(this);
         var1.addListener(this.observer);
         var2.addListener(this.observer);
      }

      @Override
      public void dispose() {
         this.op1.removeListener(this.observer);
         this.op2.removeListener(this.observer);
      }

      @Override
      protected boolean computeValue() {
         return this.op1.get() || this.op2.get();
      }

      @Override
      public ObservableList<?> getDependencies() {
         return new ImmutableObservableList<>(this.op1, this.op2);
      }
   }

   private static class ShortCircuitAndInvalidator implements InvalidationListener {
      private final WeakReference<Bindings.BooleanAndBinding> ref;

      private ShortCircuitAndInvalidator(Bindings.BooleanAndBinding var1) {
         assert var1 != null;

         this.ref = new WeakReference<>(var1);
      }

      @Override
      public void invalidated(Observable var1) {
         Bindings.BooleanAndBinding var2 = this.ref.get();
         if (var2 == null) {
            var1.removeListener(this);
         } else if (var2.op1.equals(var1) || var2.isValid() && var2.op1.get()) {
            var2.invalidate();
         }
      }
   }

   private static class ShortCircuitOrInvalidator implements InvalidationListener {
      private final WeakReference<Bindings.BooleanOrBinding> ref;

      private ShortCircuitOrInvalidator(Bindings.BooleanOrBinding var1) {
         assert var1 != null;

         this.ref = new WeakReference<>(var1);
      }

      @Override
      public void invalidated(Observable var1) {
         Bindings.BooleanOrBinding var2 = this.ref.get();
         if (var2 == null) {
            var1.removeListener(this);
         } else if (var2.op1.equals(var1) || var2.isValid() && !var2.op1.get()) {
            var2.invalidate();
         }
      }
   }
}
