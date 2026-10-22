package javafx.scene.chart;

import com.sun.javafx.charts.ChartLayoutAnimator;
import java.lang.invoke.StringConcatFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.BooleanConverter;
import javafx.css.converter.SizeConverter;
import javafx.geometry.Dimension2D;
import javafx.geometry.Side;
import javafx.util.Duration;

public final class CategoryAxis extends Axis<String> {
   private List<String> allDataCategories = new ArrayList<>();
   private boolean changeIsLocal = false;
   private final DoubleProperty firstCategoryPos = new SimpleDoubleProperty(this, "firstCategoryPos", 0.0);
   private Object currentAnimationID;
   private final ChartLayoutAnimator animator = new ChartLayoutAnimator(this);
   private ListChangeListener<String> itemsListener = var1 -> {
      while (var1.next()) {
         if (!var1.getAddedSubList().isEmpty()) {
            for (String var3 : var1.getAddedSubList()) {
               this.checkAndRemoveDuplicates(var3);
            }
         }

         if (!this.isAutoRanging()) {
            this.allDataCategories.clear();
            this.allDataCategories.addAll(this.getCategories());
            this.rangeValid = false;
         }

         this.requestAxisLayout();
      }
   };
   private DoubleProperty startMargin = new StyleableDoubleProperty(5.0) {
      protected void invalidated() {
         CategoryAxis.this.requestAxisLayout();
      }

      public CssMetaData<CategoryAxis, Number> getCssMetaData() {
         return CategoryAxis.StyleableProperties.START_MARGIN;
      }

      public Object getBean() {
         return CategoryAxis.this;
      }

      public String getName() {
         return "startMargin";
      }
   };
   private DoubleProperty endMargin = new StyleableDoubleProperty(5.0) {
      protected void invalidated() {
         CategoryAxis.this.requestAxisLayout();
      }

      public CssMetaData<CategoryAxis, Number> getCssMetaData() {
         return CategoryAxis.StyleableProperties.END_MARGIN;
      }

      public Object getBean() {
         return CategoryAxis.this;
      }

      public String getName() {
         return "endMargin";
      }
   };
   private BooleanProperty gapStartAndEnd = new StyleableBooleanProperty(true) {
      protected void invalidated() {
         CategoryAxis.this.requestAxisLayout();
      }

      public CssMetaData<CategoryAxis, Boolean> getCssMetaData() {
         return CategoryAxis.StyleableProperties.GAP_START_AND_END;
      }

      public Object getBean() {
         return CategoryAxis.this;
      }

      public String getName() {
         return "gapStartAndEnd";
      }
   };
   private ObjectProperty<ObservableList<String>> categories = new ObjectPropertyBase<ObservableList<String>>() {
      ObservableList<String> old;

      protected void invalidated() {
         if (CategoryAxis.this.getDuplicate() != null) {
            throw new IllegalArgumentException("Duplicate category added; " + CategoryAxis.this.getDuplicate() + " already present");
         } else {
            ObservableList var1 = (ObservableList)this.get();
            if (this.old != var1) {
               if (this.old != null) {
                  this.old.removeListener(CategoryAxis.this.itemsListener);
               }

               if (var1 != null) {
                  var1.addListener(CategoryAxis.this.itemsListener);
               }

               this.old = var1;
            }
         }
      }

      public Object getBean() {
         return CategoryAxis.this;
      }

      public String getName() {
         return "categories";
      }
   };
   private final ReadOnlyDoubleWrapper categorySpacing = new ReadOnlyDoubleWrapper(this, "categorySpacing", 1.0);

   public final double getStartMargin() {
      return this.startMargin.getValue();
   }

   public final void setStartMargin(double var1) {
      this.startMargin.setValue(var1);
   }

   public final DoubleProperty startMarginProperty() {
      return this.startMargin;
   }

   public final double getEndMargin() {
      return this.endMargin.getValue();
   }

   public final void setEndMargin(double var1) {
      this.endMargin.setValue(var1);
   }

   public final DoubleProperty endMarginProperty() {
      return this.endMargin;
   }

   public final boolean isGapStartAndEnd() {
      return this.gapStartAndEnd.getValue();
   }

   public final void setGapStartAndEnd(boolean var1) {
      this.gapStartAndEnd.setValue(var1);
   }

   public final BooleanProperty gapStartAndEndProperty() {
      return this.gapStartAndEnd;
   }

   public final void setCategories(ObservableList<String> var1) {
      this.categories.set(var1);
      if (!this.changeIsLocal) {
         this.setAutoRanging(false);
         this.allDataCategories.clear();
         this.allDataCategories.addAll(this.getCategories());
      }

      this.requestAxisLayout();
   }

   private void checkAndRemoveDuplicates(String var1) {
      if (this.getDuplicate() != null) {
         this.getCategories().remove(var1);
         throw new IllegalArgumentException("Duplicate category ; " + var1 + " already present");
      }
   }

   private String getDuplicate() {
      if (this.getCategories() != null) {
         for (int var1 = 0; var1 < this.getCategories().size(); var1++) {
            for (int var2 = 0; var2 < this.getCategories().size(); var2++) {
               if (((String)this.getCategories().get(var1)).equals(this.getCategories().get(var2)) && var1 != var2) {
                  return (String)this.getCategories().get(var1);
               }
            }
         }
      }

      return null;
   }

   public final ObservableList<String> getCategories() {
      return (ObservableList<String>)this.categories.get();
   }

   public final double getCategorySpacing() {
      return this.categorySpacing.get();
   }

   public final ReadOnlyDoubleProperty categorySpacingProperty() {
      return this.categorySpacing.getReadOnlyProperty();
   }

   public CategoryAxis() {
      this.changeIsLocal = true;
      this.setCategories(FXCollections.observableArrayList());
      this.changeIsLocal = false;
   }

   public CategoryAxis(ObservableList<String> var1) {
      this.setCategories(var1);
   }

   private double calculateNewSpacing(double var1, List<String> var3) {
      Side var4 = this.getEffectiveSide();
      double var5 = 1.0;
      if (var3 != null) {
         double var7 = (double)(this.isGapStartAndEnd() ? var3.size() : var3.size() - 1);
         var5 = var7 == 0.0 ? 1.0 : (var1 - this.getStartMargin() - this.getEndMargin()) / var7;
      }

      if (!this.isAutoRanging()) {
         this.categorySpacing.set(var5);
      }

      return var5;
   }

   private double calculateNewFirstPos(double var1, double var3) {
      Side var5 = this.getEffectiveSide();
      double var6 = 1.0;
      double var8 = this.isGapStartAndEnd() ? var3 / 2.0 : 0.0;
      if (var5.isHorizontal()) {
         var6 = 0.0 + this.getStartMargin() + var8;
      } else {
         var6 = var1 - this.getStartMargin() - var8;
      }

      if (!this.isAutoRanging()) {
         this.firstCategoryPos.set(var6);
      }

      return var6;
   }

   @Override
   protected Object getRange() {
      return new Object[]{this.getCategories(), this.categorySpacing.get(), this.firstCategoryPos.get(), this.getEffectiveTickLabelRotation()};
   }

   @Override
   protected void setRange(Object var1, boolean var2) {
      Object[] var3 = (Object[])var1;
      List var4 = (List)var3[0];
      double var5 = (Double)var3[1];
      double var7 = (Double)var3[2];
      this.setEffectiveTickLabelRotation((Double)var3[3]);
      this.changeIsLocal = true;
      this.setCategories(FXCollections.observableArrayList(var4));
      this.changeIsLocal = false;
      if (var2) {
         this.animator.stop(this.currentAnimationID);
         this.currentAnimationID = this.animator
            .animate(
               new KeyFrame(
                  Duration.ZERO,
                  new KeyValue[]{
                     new KeyValue(this.firstCategoryPos, this.firstCategoryPos.get()), new KeyValue(this.categorySpacing, this.categorySpacing.get())
                  }
               ),
               new KeyFrame(Duration.millis(1000.0), new KeyValue[]{new KeyValue(this.firstCategoryPos, var7), new KeyValue(this.categorySpacing, var5)})
            );
      } else {
         this.categorySpacing.set(var5);
         this.firstCategoryPos.set(var7);
      }
   }

   @Override
   protected Object autoRange(double var1) {
      Side var3 = this.getEffectiveSide();
      double var4 = this.calculateNewSpacing(var1, this.allDataCategories);
      double var6 = this.calculateNewFirstPos(var1, var4);
      double var8 = this.getTickLabelRotation();
      if (var1 >= 0.0) {
         double var10 = this.calculateRequiredSize(var3.isVertical(), var8);
         if (var10 > var1) {
            if (var3.isHorizontal() && var8 != 90.0) {
               var8 = 90.0;
            }

            if (var3.isVertical() && var8 != 0.0) {
               var8 = 0.0;
            }
         }
      }

      return new Object[]{this.allDataCategories, var4, var6, var8};
   }

   private double calculateRequiredSize(boolean var1, double var2) {
      double var4 = 0.0;
      double var6 = 0.0;
      boolean var8 = true;

      for (String var10 : this.allDataCategories) {
         Dimension2D var11 = this.measureTickMarkSize(var10, var2);
         double var12 = !var1 && var2 == 0.0 ? var11.getWidth() : var11.getHeight();
         if (var8) {
            var8 = false;
            var6 = var12 / 2.0;
         } else {
            var4 = Math.max(var4, var6 + 6.0 + var12 / 2.0);
         }
      }

      return this.getStartMargin() + var4 * (double)this.allDataCategories.size() + this.getEndMargin();
   }

   @Override
   protected List<String> calculateTickValues(double var1, Object var3) {
      Object[] var4 = (Object[])var3;
      return (List<String>)var4[0];
   }

   protected String getTickMarkLabel(String var1) {
      return var1;
   }

   protected Dimension2D measureTickMarkSize(String var1, Object var2) {
      Object[] var3 = (Object[])var2;
      double var4 = (Double)var3[3];
      return this.measureTickMarkSize(var1, var4);
   }

   @Override
   public void invalidateRange(List<String> var1) {
      super.invalidateRange(var1);
      ArrayList var2 = new ArrayList();
      var2.addAll(this.allDataCategories);

      for (String var4 : this.allDataCategories) {
         if (!var1.contains(var4)) {
            var2.remove(var4);
         }
      }

      for (int var5 = 0; var5 < var1.size(); var5++) {
         int var6 = var2.size();
         if (!var2.contains(var1.get(var5))) {
            var2.add(var5 > var6 ? var6 : var5, (String)var1.get(var5));
         }
      }

      this.allDataCategories.clear();
      this.allDataCategories.addAll(var2);
   }

   final List<String> getAllDataCategories() {
      return this.allDataCategories;
   }

   public double getDisplayPosition(String var1) {
      ObservableList var2 = this.getCategories();
      if (!var2.contains(var1)) {
         return Double.NaN;
      } else {
         return this.getEffectiveSide().isHorizontal()
            ? this.firstCategoryPos.get() + (double)var2.indexOf(var1) * this.categorySpacing.get()
            : this.firstCategoryPos.get() + (double)var2.indexOf(var1) * this.categorySpacing.get() * -1.0;
      }
   }

   public String getValueForDisplay(double var1) {
      if (this.getEffectiveSide().isHorizontal()) {
         if (!(var1 < 0.0) && !(var1 > this.getWidth())) {
            double var5 = (var1 - this.firstCategoryPos.get()) / this.categorySpacing.get();
            return this.toRealValue(var5);
         } else {
            return null;
         }
      } else if (!(var1 < 0.0) && !(var1 > this.getHeight())) {
         double var3 = (var1 - this.firstCategoryPos.get()) / (this.categorySpacing.get() * -1.0);
         return this.toRealValue(var3);
      } else {
         return null;
      }
   }

   public boolean isValueOnAxis(String var1) {
      return this.getCategories().indexOf(StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0001">(var1)) != -1;
   }

   public double toNumericValue(String var1) {
      return (double)this.getCategories().indexOf(var1);
   }

   public String toRealValue(double var1) {
      int var3 = (int)Math.round(var1);
      ObservableList var4 = this.getCategories();
      return var3 >= 0 && var3 < var4.size() ? (String)this.getCategories().get(var3) : null;
   }

   @Override
   public double getZeroPosition() {
      return Double.NaN;
   }

   public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
      return CategoryAxis.StyleableProperties.STYLEABLES;
   }

   @Override
   public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
      return getClassCssMetaData();
   }

   private static class StyleableProperties {
      private static final CssMetaData<CategoryAxis, Number> START_MARGIN = new CssMetaData<CategoryAxis, Number>(
         "-fx-start-margin", SizeConverter.getInstance(), 5.0
      ) {
         public boolean isSettable(CategoryAxis var1) {
            return var1.startMargin == null || !var1.startMargin.isBound();
         }

         public StyleableProperty<Number> getStyleableProperty(CategoryAxis var1) {
            return (StyleableProperty<Number>)var1.startMarginProperty();
         }
      };
      private static final CssMetaData<CategoryAxis, Number> END_MARGIN = new CssMetaData<CategoryAxis, Number>(
         "-fx-end-margin", SizeConverter.getInstance(), 5.0
      ) {
         public boolean isSettable(CategoryAxis var1) {
            return var1.endMargin == null || !var1.endMargin.isBound();
         }

         public StyleableProperty<Number> getStyleableProperty(CategoryAxis var1) {
            return (StyleableProperty<Number>)var1.endMarginProperty();
         }
      };
      private static final CssMetaData<CategoryAxis, Boolean> GAP_START_AND_END = new CssMetaData<CategoryAxis, Boolean>(
         "-fx-gap-start-and-end", BooleanConverter.getInstance(), Boolean.TRUE
      ) {
         public boolean isSettable(CategoryAxis var1) {
            return var1.gapStartAndEnd == null || !var1.gapStartAndEnd.isBound();
         }

         public StyleableProperty<Boolean> getStyleableProperty(CategoryAxis var1) {
            return (StyleableProperty<Boolean>)var1.gapStartAndEndProperty();
         }
      };
      private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

      static {
         ArrayList var0 = new ArrayList<>(Axis.getClassCssMetaData());
         var0.add(START_MARGIN);
         var0.add(END_MARGIN);
         var0.add(GAP_START_AND_END);
         STYLEABLES = Collections.unmodifiableList(var0);
      }
   }
}
