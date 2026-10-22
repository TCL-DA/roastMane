package javafx.scene.control.skin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.css.CssMetaData;
import javafx.css.StyleOrigin;
import javafx.css.Styleable;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.SizeConverter;
import javafx.scene.control.Cell;
import javafx.scene.control.SkinBase;

public class CellSkinBase<C extends Cell> extends LabeledSkinBase<C> {
   private DoubleProperty cellSize;
   static final double DEFAULT_CELL_SIZE = 24.0;

   public CellSkinBase(C var1) {
      super((C)var1);
      this.consumeMouseEvents(false);
   }

   public final double getCellSize() {
      return this.cellSize == null ? 24.0 : this.cellSize.get();
   }

   public final ReadOnlyDoubleProperty cellSizeProperty() {
      return this.cellSizePropertyImpl();
   }

   private DoubleProperty cellSizePropertyImpl() {
      if (this.cellSize == null) {
         this.cellSize = new StyleableDoubleProperty(24.0) {
            public void applyStyle(StyleOrigin var1, Number var2) {
               double var3 = var2 == null ? 24.0 : var2.doubleValue();
               super.applyStyle(var1, var3 <= 0.0 ? 24.0 : var3);
            }

            public void set(double var1) {
               super.set(var1);
               ((Cell)CellSkinBase.this.getSkinnable()).requestLayout();
            }

            public Object getBean() {
               return CellSkinBase.this;
            }

            public String getName() {
               return "cellSize";
            }

            public CssMetaData<Cell<?>, Number> getCssMetaData() {
               return CellSkinBase.StyleableProperties.CELL_SIZE;
            }
         };
      }

      return this.cellSize;
   }

   public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
      return CellSkinBase.StyleableProperties.STYLEABLES;
   }

   @Override
   public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
      return getClassCssMetaData();
   }

   private static class StyleableProperties {
      private static final CssMetaData<Cell<?>, Number> CELL_SIZE = new CssMetaData<Cell<?>, Number>("-fx-cell-size", SizeConverter.getInstance(), 24.0) {
         public boolean isSettable(Cell<?> var1) {
            CellSkinBase var2 = (CellSkinBase)var1.getSkin();
            return var2.cellSize == null || !var2.cellSize.isBound();
         }

         public StyleableProperty<Number> getStyleableProperty(Cell<?> var1) {
            CellSkinBase var2 = (CellSkinBase)var1.getSkin();
            return (StyleableProperty<Number>)var2.cellSizePropertyImpl();
         }
      };
      private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

      static {
         ArrayList var0 = new ArrayList<>(SkinBase.getClassCssMetaData());
         var0.add(CELL_SIZE);
         STYLEABLES = Collections.unmodifiableList(var0);
      }
   }
}
