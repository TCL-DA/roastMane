package com.sun.javafx.binding;

import java.lang.ref.WeakReference;
import java.text.Format;
import java.text.ParseException;
import java.util.Objects;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import javafx.util.StringConverter;

public abstract class BidirectionalBinding implements InvalidationListener, WeakListener {
   private final int cachedHashCode;

   private static void checkParameters(Object var0, Object var1) {
      Objects.requireNonNull(var0, "Both properties must be specified.");
      Objects.requireNonNull(var1, "Both properties must be specified.");
      if (var0 == var1) {
         throw new IllegalArgumentException("Cannot bind property to itself");
      }
   }

   public static <T> BidirectionalBinding bind(Property<T> var0, Property<T> var1) {
      checkParameters(var0, var1);
      Object var2 = var0 instanceof DoubleProperty && var1 instanceof DoubleProperty
         ? new BidirectionalBinding.BidirectionalDoubleBinding((DoubleProperty)var0, (DoubleProperty)var1)
         : (
            var0 instanceof FloatProperty && var1 instanceof FloatProperty
               ? new BidirectionalBinding.BidirectionalFloatBinding((FloatProperty)var0, (FloatProperty)var1)
               : (
                  var0 instanceof IntegerProperty && var1 instanceof IntegerProperty
                     ? new BidirectionalBinding.BidirectionalIntegerBinding((IntegerProperty)var0, (IntegerProperty)var1)
                     : (
                        var0 instanceof LongProperty && var1 instanceof LongProperty
                           ? new BidirectionalBinding.BidirectionalLongBinding((LongProperty)var0, (LongProperty)var1)
                           : (
                              var0 instanceof BooleanProperty && var1 instanceof BooleanProperty
                                 ? new BidirectionalBinding.BidirectionalBooleanBinding((BooleanProperty)var0, (BooleanProperty)var1)
                                 : new BidirectionalBinding.TypedGenericBidirectionalBinding(var0, var1)
                           )
                     )
               )
         );
      var0.setValue(var1.getValue());
      var0.getValue();
      var0.addListener((InvalidationListener)var2);
      var1.addListener((InvalidationListener)var2);
      return (BidirectionalBinding)var2;
   }

   public static Object bind(Property<String> var0, Property<?> var1, Format var2) {
      checkParameters(var0, var1);
      Objects.requireNonNull(var2, "Format cannot be null");
      BidirectionalBinding.StringFormatBidirectionalBinding var3 = new BidirectionalBinding.StringFormatBidirectionalBinding(var0, var1, var2);
      var0.setValue(var2.format(var1.getValue()));
      var0.getValue();
      var0.addListener(var3);
      var1.addListener(var3);
      return var3;
   }

   public static <T> Object bind(Property<String> var0, Property<T> var1, StringConverter<T> var2) {
      checkParameters(var0, var1);
      Objects.requireNonNull(var2, "Converter cannot be null");
      BidirectionalBinding.StringConverterBidirectionalBinding var3 = new BidirectionalBinding.StringConverterBidirectionalBinding(var0, var1, var2);
      var0.setValue(var2.toString(var1.getValue()));
      var0.getValue();
      var0.addListener(var3);
      var1.addListener(var3);
      return var3;
   }

   public static <T> void unbind(Property<T> var0, Property<T> var1) {
      checkParameters(var0, var1);
      BidirectionalBinding.UntypedGenericBidirectionalBinding var2 = new BidirectionalBinding.UntypedGenericBidirectionalBinding(var0, var1);
      var0.removeListener(var2);
      var1.removeListener(var2);
   }

   public static void unbind(Object var0, Object var1) {
      checkParameters(var0, var1);
      BidirectionalBinding.UntypedGenericBidirectionalBinding var2 = new BidirectionalBinding.UntypedGenericBidirectionalBinding(var0, var1);
      if (var0 instanceof ObservableValue) {
         ((ObservableValue)var0).removeListener(var2);
      }

      if (var1 instanceof ObservableValue) {
         ((ObservableValue)var1).removeListener(var2);
      }
   }

   public static BidirectionalBinding bindNumber(Property<Integer> var0, IntegerProperty var1) {
      return bindNumber(var0, (Property<Number>)var1);
   }

   public static BidirectionalBinding bindNumber(Property<Long> var0, LongProperty var1) {
      return bindNumber(var0, (Property<Number>)var1);
   }

   public static BidirectionalBinding bindNumber(Property<Float> var0, FloatProperty var1) {
      return bindNumber(var0, (Property<Number>)var1);
   }

   public static BidirectionalBinding bindNumber(Property<Double> var0, DoubleProperty var1) {
      return bindNumber(var0, (Property<Number>)var1);
   }

   public static BidirectionalBinding bindNumber(IntegerProperty var0, Property<Integer> var1) {
      return bindNumberObject(var0, var1);
   }

   public static BidirectionalBinding bindNumber(LongProperty var0, Property<Long> var1) {
      return bindNumberObject(var0, var1);
   }

   public static BidirectionalBinding bindNumber(FloatProperty var0, Property<Float> var1) {
      return bindNumberObject(var0, var1);
   }

   public static BidirectionalBinding bindNumber(DoubleProperty var0, Property<Double> var1) {
      return bindNumberObject(var0, var1);
   }

   private static <T extends Number> BidirectionalBinding bindNumberObject(Property<Number> var0, Property<T> var1) {
      checkParameters(var0, var1);
      BidirectionalBinding.TypedNumberBidirectionalBinding var2 = new BidirectionalBinding.TypedNumberBidirectionalBinding(var1, var0);
      var0.setValue((Number)var1.getValue());
      var0.getValue();
      var0.addListener(var2);
      var1.addListener(var2);
      return var2;
   }

   private static <T extends Number> BidirectionalBinding bindNumber(Property<T> var0, Property<Number> var1) {
      checkParameters(var0, var1);
      BidirectionalBinding.TypedNumberBidirectionalBinding var2 = new BidirectionalBinding.TypedNumberBidirectionalBinding(var0, var1);
      var0.setValue((Number)var1.getValue());
      var0.getValue();
      var0.addListener(var2);
      var1.addListener(var2);
      return var2;
   }

   private BidirectionalBinding(Object var1, Object var2) {
      this.cachedHashCode = var1.hashCode() * var2.hashCode();
   }

   protected abstract Object getProperty1();

   protected abstract Object getProperty2();

   @Override
   public int hashCode() {
      return this.cachedHashCode;
   }

   @Override
   public boolean wasGarbageCollected() {
      return this.getProperty1() == null || this.getProperty2() == null;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         Object var2 = this.getProperty1();
         Object var3 = this.getProperty2();
         if (var2 != null && var3 != null) {
            if (var1 instanceof BidirectionalBinding) {
               BidirectionalBinding var4 = (BidirectionalBinding)var1;
               Object var5 = var4.getProperty1();
               Object var6 = var4.getProperty2();
               if (var5 == null || var6 == null) {
                  return false;
               }

               if (var2 == var5 && var3 == var6) {
                  return true;
               }

               if (var2 == var6 && var3 == var5) {
                  return true;
               }
            }

            return false;
         } else {
            return false;
         }
      }
   }

   private static class BidirectionalBooleanBinding extends BidirectionalBinding {
      private final WeakReference<BooleanProperty> propertyRef1;
      private final WeakReference<BooleanProperty> propertyRef2;
      private boolean oldValue;
      private boolean updating;

      private BidirectionalBooleanBinding(BooleanProperty var1, BooleanProperty var2) {
         super(var1, var2);
         this.oldValue = var1.get();
         this.propertyRef1 = new WeakReference<>(var1);
         this.propertyRef2 = new WeakReference<>(var2);
      }

      protected Property<Boolean> getProperty1() {
         return this.propertyRef1.get();
      }

      protected Property<Boolean> getProperty2() {
         return this.propertyRef2.get();
      }

      @Override
      public void invalidated(Observable var1) {
         if (!this.updating) {
            BooleanProperty var2 = this.propertyRef1.get();
            BooleanProperty var3 = this.propertyRef2.get();
            if (var2 != null && var3 != null) {
               try {
                  this.updating = true;
                  if (var2 == var1) {
                     boolean var4 = var2.get();
                     var3.set(var4);
                     var3.get();
                     this.oldValue = var4;
                  } else {
                     boolean var13 = var3.get();
                     var2.set(var13);
                     var2.get();
                     this.oldValue = var13;
                  }
               } catch (RuntimeException var11) {
                  try {
                     if (var2 == var1) {
                        var2.set(this.oldValue);
                        var2.get();
                     } else {
                        var3.set(this.oldValue);
                        var3.get();
                     }
                  } catch (Exception var10) {
                     var10.addSuppressed(var11);
                     unbind(var2, var3);
                     throw new RuntimeException(
                        "Bidirectional binding failed together with an attempt to restore the source property to the previous value. Removing the bidirectional binding from properties "
                           + var2
                           + " and "
                           + var3,
                        var10
                     );
                  }

                  throw new RuntimeException("Bidirectional binding failed, setting to the previous value", var11);
               } finally {
                  this.updating = false;
               }
            } else {
               if (var2 != null) {
                  var2.removeListener(this);
               }

               if (var3 != null) {
                  var3.removeListener(this);
               }
            }
         }
      }
   }

   private static class BidirectionalDoubleBinding extends BidirectionalBinding {
      private final WeakReference<DoubleProperty> propertyRef1;
      private final WeakReference<DoubleProperty> propertyRef2;
      private double oldValue;
      private boolean updating = false;

      private BidirectionalDoubleBinding(DoubleProperty var1, DoubleProperty var2) {
         super(var1, var2);
         this.oldValue = var1.get();
         this.propertyRef1 = new WeakReference<>(var1);
         this.propertyRef2 = new WeakReference<>(var2);
      }

      protected Property<Number> getProperty1() {
         return this.propertyRef1.get();
      }

      protected Property<Number> getProperty2() {
         return this.propertyRef2.get();
      }

      @Override
      public void invalidated(Observable var1) {
         if (!this.updating) {
            DoubleProperty var2 = this.propertyRef1.get();
            DoubleProperty var3 = this.propertyRef2.get();
            if (var2 != null && var3 != null) {
               try {
                  this.updating = true;
                  if (var2 == var1) {
                     double var4 = var2.get();
                     var3.set(var4);
                     var3.get();
                     this.oldValue = var4;
                  } else {
                     double var13 = var3.get();
                     var2.set(var13);
                     var2.get();
                     this.oldValue = var13;
                  }
               } catch (RuntimeException var11) {
                  try {
                     if (var2 == var1) {
                        var2.set(this.oldValue);
                        var2.get();
                     } else {
                        var3.set(this.oldValue);
                        var3.get();
                     }
                  } catch (Exception var10) {
                     var10.addSuppressed(var11);
                     unbind(var2, var3);
                     throw new RuntimeException(
                        "Bidirectional binding failed together with an attempt to restore the source property to the previous value. Removing the bidirectional binding from properties "
                           + var2
                           + " and "
                           + var3,
                        var10
                     );
                  }

                  throw new RuntimeException("Bidirectional binding failed, setting to the previous value", var11);
               } finally {
                  this.updating = false;
               }
            } else {
               if (var2 != null) {
                  var2.removeListener(this);
               }

               if (var3 != null) {
                  var3.removeListener(this);
               }
            }
         }
      }
   }

   private static class BidirectionalFloatBinding extends BidirectionalBinding {
      private final WeakReference<FloatProperty> propertyRef1;
      private final WeakReference<FloatProperty> propertyRef2;
      private float oldValue;
      private boolean updating = false;

      private BidirectionalFloatBinding(FloatProperty var1, FloatProperty var2) {
         super(var1, var2);
         this.oldValue = var1.get();
         this.propertyRef1 = new WeakReference<>(var1);
         this.propertyRef2 = new WeakReference<>(var2);
      }

      protected Property<Number> getProperty1() {
         return this.propertyRef1.get();
      }

      protected Property<Number> getProperty2() {
         return this.propertyRef2.get();
      }

      @Override
      public void invalidated(Observable var1) {
         if (!this.updating) {
            FloatProperty var2 = this.propertyRef1.get();
            FloatProperty var3 = this.propertyRef2.get();
            if (var2 != null && var3 != null) {
               try {
                  this.updating = true;
                  if (var2 == var1) {
                     float var4 = var2.get();
                     var3.set(var4);
                     var3.get();
                     this.oldValue = var4;
                  } else {
                     float var13 = var3.get();
                     var2.set(var13);
                     var2.get();
                     this.oldValue = var13;
                  }
               } catch (RuntimeException var11) {
                  try {
                     if (var2 == var1) {
                        var2.set(this.oldValue);
                        var2.get();
                     } else {
                        var3.set(this.oldValue);
                        var3.get();
                     }
                  } catch (Exception var10) {
                     var10.addSuppressed(var11);
                     unbind(var2, var3);
                     throw new RuntimeException(
                        "Bidirectional binding failed together with an attempt to restore the source property to the previous value. Removing the bidirectional binding from properties "
                           + var2
                           + " and "
                           + var3,
                        var10
                     );
                  }

                  throw new RuntimeException("Bidirectional binding failed, setting to the previous value", var11);
               } finally {
                  this.updating = false;
               }
            } else {
               if (var2 != null) {
                  var2.removeListener(this);
               }

               if (var3 != null) {
                  var3.removeListener(this);
               }
            }
         }
      }
   }

   private static class BidirectionalIntegerBinding extends BidirectionalBinding {
      private final WeakReference<IntegerProperty> propertyRef1;
      private final WeakReference<IntegerProperty> propertyRef2;
      private int oldValue;
      private boolean updating = false;

      private BidirectionalIntegerBinding(IntegerProperty var1, IntegerProperty var2) {
         super(var1, var2);
         this.oldValue = var1.get();
         this.propertyRef1 = new WeakReference<>(var1);
         this.propertyRef2 = new WeakReference<>(var2);
      }

      protected Property<Number> getProperty1() {
         return this.propertyRef1.get();
      }

      protected Property<Number> getProperty2() {
         return this.propertyRef2.get();
      }

      @Override
      public void invalidated(Observable var1) {
         if (!this.updating) {
            IntegerProperty var2 = this.propertyRef1.get();
            IntegerProperty var3 = this.propertyRef2.get();
            if (var2 != null && var3 != null) {
               try {
                  this.updating = true;
                  if (var2 == var1) {
                     int var4 = var2.get();
                     var3.set(var4);
                     var3.get();
                     this.oldValue = var4;
                  } else {
                     int var13 = var3.get();
                     var2.set(var13);
                     var2.get();
                     this.oldValue = var13;
                  }
               } catch (RuntimeException var11) {
                  try {
                     if (var2 == var1) {
                        var2.set(this.oldValue);
                        var2.get();
                     } else {
                        var3.set(this.oldValue);
                        var3.get();
                     }
                  } catch (Exception var10) {
                     var10.addSuppressed(var11);
                     unbind(var2, var3);
                     throw new RuntimeException(
                        "Bidirectional binding failed together with an attempt to restore the source property to the previous value. Removing the bidirectional binding from properties "
                           + var2
                           + " and "
                           + var3,
                        var10
                     );
                  }

                  throw new RuntimeException("Bidirectional binding failed, setting to the previous value", var11);
               } finally {
                  this.updating = false;
               }
            } else {
               if (var2 != null) {
                  var2.removeListener(this);
               }

               if (var3 != null) {
                  var3.removeListener(this);
               }
            }
         }
      }
   }

   private static class BidirectionalLongBinding extends BidirectionalBinding {
      private final WeakReference<LongProperty> propertyRef1;
      private final WeakReference<LongProperty> propertyRef2;
      private long oldValue;
      private boolean updating = false;

      private BidirectionalLongBinding(LongProperty var1, LongProperty var2) {
         super(var1, var2);
         this.oldValue = var1.get();
         this.propertyRef1 = new WeakReference<>(var1);
         this.propertyRef2 = new WeakReference<>(var2);
      }

      protected Property<Number> getProperty1() {
         return this.propertyRef1.get();
      }

      protected Property<Number> getProperty2() {
         return this.propertyRef2.get();
      }

      @Override
      public void invalidated(Observable var1) {
         if (!this.updating) {
            LongProperty var2 = this.propertyRef1.get();
            LongProperty var3 = this.propertyRef2.get();
            if (var2 != null && var3 != null) {
               try {
                  this.updating = true;
                  if (var2 == var1) {
                     long var4 = var2.get();
                     var3.set(var4);
                     var3.get();
                     this.oldValue = var4;
                  } else {
                     long var13 = var3.get();
                     var2.set(var13);
                     var2.get();
                     this.oldValue = var13;
                  }
               } catch (RuntimeException var11) {
                  try {
                     if (var2 == var1) {
                        var2.set(this.oldValue);
                        var2.get();
                     } else {
                        var3.set(this.oldValue);
                        var3.get();
                     }
                  } catch (Exception var10) {
                     var10.addSuppressed(var11);
                     unbind(var2, var3);
                     throw new RuntimeException(
                        "Bidirectional binding failed together with an attempt to restore the source property to the previous value. Removing the bidirectional binding from properties "
                           + var2
                           + " and "
                           + var3,
                        var10
                     );
                  }

                  throw new RuntimeException("Bidirectional binding failed, setting to the previous value", var11);
               } finally {
                  this.updating = false;
               }
            } else {
               if (var2 != null) {
                  var2.removeListener(this);
               }

               if (var3 != null) {
                  var3.removeListener(this);
               }
            }
         }
      }
   }

   public abstract static class StringConversionBidirectionalBinding<T> extends BidirectionalBinding {
      private final WeakReference<Property<String>> stringPropertyRef;
      private final WeakReference<Property<T>> otherPropertyRef;
      private boolean updating;

      public StringConversionBidirectionalBinding(Property<String> var1, Property<T> var2) {
         super(var1, var2);
         this.stringPropertyRef = new WeakReference<>(var1);
         this.otherPropertyRef = new WeakReference<>(var2);
      }

      protected abstract String toString(T var1);

      protected abstract T fromString(String var1) throws ParseException;

      @Override
      protected Object getProperty1() {
         return this.stringPropertyRef.get();
      }

      @Override
      protected Object getProperty2() {
         return this.otherPropertyRef.get();
      }

      @Override
      public void invalidated(Observable var1) {
         if (!this.updating) {
            Property var2 = this.stringPropertyRef.get();
            Property var3 = this.otherPropertyRef.get();
            if (var2 != null && var3 != null) {
               try {
                  this.updating = true;
                  if (var2 == var1) {
                     try {
                        var3.setValue(this.fromString((String)var2.getValue()));
                        var3.getValue();
                     } catch (Exception var10) {
                        Logging.getLogger().warning("Exception while parsing String in bidirectional binding", var10);
                        var3.setValue(null);
                        var3.getValue();
                     }
                  } else {
                     try {
                        var2.setValue(this.toString((T)var3.getValue()));
                        var2.getValue();
                     } catch (Exception var9) {
                        Logging.getLogger().warning("Exception while converting Object to String in bidirectional binding", var9);
                        var2.setValue("");
                        var2.getValue();
                     }
                  }
               } finally {
                  this.updating = false;
               }
            } else {
               if (var2 != null) {
                  var2.removeListener(this);
               }

               if (var3 != null) {
                  var3.removeListener(this);
               }
            }
         }
      }
   }

   private static class StringConverterBidirectionalBinding<T> extends BidirectionalBinding.StringConversionBidirectionalBinding<T> {
      private final StringConverter<T> converter;

      public StringConverterBidirectionalBinding(Property<String> var1, Property<T> var2, StringConverter<T> var3) {
         super(var1, var2);
         this.converter = var3;
      }

      @Override
      protected String toString(T var1) {
         return this.converter.toString((T)var1);
      }

      @Override
      protected T fromString(String var1) throws ParseException {
         return this.converter.fromString(var1);
      }
   }

   private static class StringFormatBidirectionalBinding extends BidirectionalBinding.StringConversionBidirectionalBinding {
      private final Format format;

      public StringFormatBidirectionalBinding(Property<String> var1, Property<?> var2, Format var3) {
         super(var1, var2);
         this.format = var3;
      }

      @Override
      protected String toString(Object var1) {
         return this.format.format(var1);
      }

      @Override
      protected Object fromString(String var1) throws ParseException {
         return this.format.parseObject(var1);
      }
   }

   private static class TypedGenericBidirectionalBinding<T> extends BidirectionalBinding {
      private final WeakReference<Property<T>> propertyRef1;
      private final WeakReference<Property<T>> propertyRef2;
      private T oldValue;
      private boolean updating = false;

      private TypedGenericBidirectionalBinding(Property<T> var1, Property<T> var2) {
         super(var1, var2);
         this.oldValue = (T)var1.getValue();
         this.propertyRef1 = new WeakReference<>(var1);
         this.propertyRef2 = new WeakReference<>(var2);
      }

      protected Property<T> getProperty1() {
         return this.propertyRef1.get();
      }

      protected Property<T> getProperty2() {
         return this.propertyRef2.get();
      }

      @Override
      public void invalidated(Observable var1) {
         if (!this.updating) {
            Property var2 = this.propertyRef1.get();
            Property var3 = this.propertyRef2.get();
            if (var2 != null && var3 != null) {
               try {
                  this.updating = true;
                  if (var2 == var1) {
                     Object var4 = var2.getValue();
                     var3.setValue(var4);
                     var3.getValue();
                     this.oldValue = (T)var4;
                  } else {
                     Object var13 = var3.getValue();
                     var2.setValue(var13);
                     var2.getValue();
                     this.oldValue = (T)var13;
                  }
               } catch (RuntimeException var11) {
                  try {
                     if (var2 == var1) {
                        var2.setValue(this.oldValue);
                        var2.getValue();
                     } else {
                        var3.setValue(this.oldValue);
                        var3.getValue();
                     }
                  } catch (Exception var10) {
                     var10.addSuppressed(var11);
                     unbind(var2, var3);
                     throw new RuntimeException(
                        "Bidirectional binding failed together with an attempt to restore the source property to the previous value. Removing the bidirectional binding from properties "
                           + var2
                           + " and "
                           + var3,
                        var10
                     );
                  }

                  throw new RuntimeException("Bidirectional binding failed, setting to the previous value", var11);
               } finally {
                  this.updating = false;
               }
            } else {
               if (var2 != null) {
                  var2.removeListener(this);
               }

               if (var3 != null) {
                  var3.removeListener(this);
               }
            }
         }
      }
   }

   private static class TypedNumberBidirectionalBinding<T extends Number> extends BidirectionalBinding {
      private final WeakReference<Property<T>> propertyRef1;
      private final WeakReference<Property<Number>> propertyRef2;
      private T oldValue;
      private boolean updating = false;

      private TypedNumberBidirectionalBinding(Property<T> var1, Property<Number> var2) {
         super(var1, var2);
         this.oldValue = (T)var1.getValue();
         this.propertyRef1 = new WeakReference<>(var1);
         this.propertyRef2 = new WeakReference<>(var2);
      }

      protected Property<T> getProperty1() {
         return this.propertyRef1.get();
      }

      protected Property<Number> getProperty2() {
         return this.propertyRef2.get();
      }

      @Override
      public void invalidated(Observable var1) {
         if (!this.updating) {
            Property var2 = this.propertyRef1.get();
            Property var3 = this.propertyRef2.get();
            if (var2 != null && var3 != null) {
               try {
                  this.updating = true;
                  if (var2 == var1) {
                     Number var4 = (Number)var2.getValue();
                     var3.setValue(var4);
                     var3.getValue();
                     this.oldValue = (T)var4;
                  } else {
                     Number var13 = (Number)var3.getValue();
                     var2.setValue(var13);
                     var2.getValue();
                     this.oldValue = (T)var13;
                  }
               } catch (RuntimeException var11) {
                  try {
                     if (var2 == var1) {
                        var2.setValue(this.oldValue);
                        var2.getValue();
                     } else {
                        var3.setValue(this.oldValue);
                        var3.getValue();
                     }
                  } catch (Exception var10) {
                     var10.addSuppressed(var11);
                     unbind(var2, var3);
                     throw new RuntimeException(
                        "Bidirectional binding failed together with an attempt to restore the source property to the previous value. Removing the bidirectional binding from properties "
                           + var2
                           + " and "
                           + var3,
                        var10
                     );
                  }

                  throw new RuntimeException("Bidirectional binding failed, setting to the previous value", var11);
               } finally {
                  this.updating = false;
               }
            } else {
               if (var2 != null) {
                  var2.removeListener(this);
               }

               if (var3 != null) {
                  var3.removeListener(this);
               }
            }
         }
      }
   }

   private static class UntypedGenericBidirectionalBinding extends BidirectionalBinding {
      private final Object property1;
      private final Object property2;

      public UntypedGenericBidirectionalBinding(Object var1, Object var2) {
         super(var1, var2);
         this.property1 = var1;
         this.property2 = var2;
      }

      @Override
      protected Object getProperty1() {
         return this.property1;
      }

      @Override
      protected Object getProperty2() {
         return this.property2;
      }

      @Override
      public void invalidated(Observable var1) {
         throw new RuntimeException("Should not reach here");
      }
   }
}
