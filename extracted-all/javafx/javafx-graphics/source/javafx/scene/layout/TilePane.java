package javafx.scene.layout;

import com.sun.javafx.binding.ExpressionHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.beans.InvalidationListener;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableIntegerProperty;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.EnumConverter;
import javafx.css.converter.SizeConverter;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.util.Callback;

public class TilePane extends Pane {
   private static final String MARGIN_CONSTRAINT = "tilepane-margin";
   private static final String ALIGNMENT_CONSTRAINT = "tilepane-alignment";
   private static final Callback<Node, Insets> marginAccessor = var0 -> getMargin(var0);
   private double _tileWidth = -1.0;
   private double _tileHeight = -1.0;
   private ObjectProperty<Orientation> orientation;
   private IntegerProperty prefRows;
   private IntegerProperty prefColumns;
   private DoubleProperty prefTileWidth;
   private DoubleProperty prefTileHeight;
   private TilePane.TileSizeProperty tileWidth;
   private TilePane.TileSizeProperty tileHeight;
   private DoubleProperty hgap;
   private DoubleProperty vgap;
   private ObjectProperty<Pos> alignment;
   private ObjectProperty<Pos> tileAlignment;
   private int actualRows = 0;
   private int actualColumns = 0;

   public static void setAlignment(Node var0, Pos var1) {
      setConstraint(var0, "tilepane-alignment", var1);
   }

   public static Pos getAlignment(Node var0) {
      return (Pos)getConstraint(var0, "tilepane-alignment");
   }

   public static void setMargin(Node var0, Insets var1) {
      setConstraint(var0, "tilepane-margin", var1);
   }

   public static Insets getMargin(Node var0) {
      return (Insets)getConstraint(var0, "tilepane-margin");
   }

   public static void clearConstraints(Node var0) {
      setAlignment(var0, null);
      setMargin(var0, null);
   }

   public TilePane() {
   }

   public TilePane(Orientation var1) {
      this.setOrientation(var1);
   }

   public TilePane(double var1, double var3) {
      this.setHgap(var1);
      this.setVgap(var3);
   }

   public TilePane(Orientation var1, double var2, double var4) {
      this();
      this.setOrientation(var1);
      this.setHgap(var2);
      this.setVgap(var4);
   }

   public TilePane(Node... var1) {
      this.getChildren().addAll(var1);
   }

   public TilePane(Orientation var1, Node... var2) {
      this.setOrientation(var1);
      this.getChildren().addAll(var2);
   }

   public TilePane(double var1, double var3, Node... var5) {
      this.setHgap(var1);
      this.setVgap(var3);
      this.getChildren().addAll(var5);
   }

   public TilePane(Orientation var1, double var2, double var4, Node... var6) {
      this();
      this.setOrientation(var1);
      this.setHgap(var2);
      this.setVgap(var4);
      this.getChildren().addAll(var6);
   }

   public final ObjectProperty<Orientation> orientationProperty() {
      if (this.orientation == null) {
         this.orientation = new StyleableObjectProperty(Orientation.HORIZONTAL) {
            public void invalidated() {
               TilePane.this.requestLayout();
            }

            @Override
            public CssMetaData<TilePane, Orientation> getCssMetaData() {
               return TilePane.StyleableProperties.ORIENTATION;
            }

            public Object getBean() {
               return TilePane.this;
            }

            public String getName() {
               return "orientation";
            }
         };
      }

      return this.orientation;
   }

   public final void setOrientation(Orientation var1) {
      this.orientationProperty().set(var1);
   }

   public final Orientation getOrientation() {
      return this.orientation == null ? Orientation.HORIZONTAL : (Orientation)this.orientation.get();
   }

   public final IntegerProperty prefRowsProperty() {
      if (this.prefRows == null) {
         this.prefRows = new StyleableIntegerProperty(5) {
            public void invalidated() {
               TilePane.this.requestLayout();
            }

            @Override
            public CssMetaData<TilePane, Number> getCssMetaData() {
               return TilePane.StyleableProperties.PREF_ROWS;
            }

            public Object getBean() {
               return TilePane.this;
            }

            public String getName() {
               return "prefRows";
            }
         };
      }

      return this.prefRows;
   }

   public final void setPrefRows(int var1) {
      this.prefRowsProperty().set(var1);
   }

   public final int getPrefRows() {
      return this.prefRows == null ? 5 : this.prefRows.get();
   }

   public final IntegerProperty prefColumnsProperty() {
      if (this.prefColumns == null) {
         this.prefColumns = new StyleableIntegerProperty(5) {
            public void invalidated() {
               TilePane.this.requestLayout();
            }

            @Override
            public CssMetaData<TilePane, Number> getCssMetaData() {
               return TilePane.StyleableProperties.PREF_COLUMNS;
            }

            public Object getBean() {
               return TilePane.this;
            }

            public String getName() {
               return "prefColumns";
            }
         };
      }

      return this.prefColumns;
   }

   public final void setPrefColumns(int var1) {
      this.prefColumnsProperty().set(var1);
   }

   public final int getPrefColumns() {
      return this.prefColumns == null ? 5 : this.prefColumns.get();
   }

   public final DoubleProperty prefTileWidthProperty() {
      if (this.prefTileWidth == null) {
         this.prefTileWidth = new StyleableDoubleProperty(-1.0) {
            public void invalidated() {
               TilePane.this.requestLayout();
            }

            @Override
            public CssMetaData<TilePane, Number> getCssMetaData() {
               return TilePane.StyleableProperties.PREF_TILE_WIDTH;
            }

            public Object getBean() {
               return TilePane.this;
            }

            public String getName() {
               return "prefTileWidth";
            }
         };
      }

      return this.prefTileWidth;
   }

   public final void setPrefTileWidth(double var1) {
      this.prefTileWidthProperty().set(var1);
   }

   public final double getPrefTileWidth() {
      return this.prefTileWidth == null ? -1.0 : this.prefTileWidth.get();
   }

   public final DoubleProperty prefTileHeightProperty() {
      if (this.prefTileHeight == null) {
         this.prefTileHeight = new StyleableDoubleProperty(-1.0) {
            public void invalidated() {
               TilePane.this.requestLayout();
            }

            @Override
            public CssMetaData<TilePane, Number> getCssMetaData() {
               return TilePane.StyleableProperties.PREF_TILE_HEIGHT;
            }

            public Object getBean() {
               return TilePane.this;
            }

            public String getName() {
               return "prefTileHeight";
            }
         };
      }

      return this.prefTileHeight;
   }

   public final void setPrefTileHeight(double var1) {
      this.prefTileHeightProperty().set(var1);
   }

   public final double getPrefTileHeight() {
      return this.prefTileHeight == null ? -1.0 : this.prefTileHeight.get();
   }

   public final ReadOnlyDoubleProperty tileWidthProperty() {
      if (this.tileWidth == null) {
         this.tileWidth = new TilePane.TileSizeProperty("tileWidth", this._tileWidth) {
            @Override
            public double compute() {
               return TilePane.this.computeTileWidth();
            }
         };
      }

      return this.tileWidth;
   }

   private void invalidateTileWidth() {
      if (this.tileWidth != null) {
         this.tileWidth.invalidate();
      } else {
         this._tileWidth = -1.0;
      }
   }

   public final double getTileWidth() {
      if (this.tileWidth != null) {
         return this.tileWidth.get();
      } else {
         if (this._tileWidth == -1.0) {
            this._tileWidth = this.computeTileWidth();
         }

         return this._tileWidth;
      }
   }

   public final ReadOnlyDoubleProperty tileHeightProperty() {
      if (this.tileHeight == null) {
         this.tileHeight = new TilePane.TileSizeProperty("tileHeight", this._tileHeight) {
            @Override
            public double compute() {
               return TilePane.this.computeTileHeight();
            }
         };
      }

      return this.tileHeight;
   }

   private void invalidateTileHeight() {
      if (this.tileHeight != null) {
         this.tileHeight.invalidate();
      } else {
         this._tileHeight = -1.0;
      }
   }

   public final double getTileHeight() {
      if (this.tileHeight != null) {
         return this.tileHeight.get();
      } else {
         if (this._tileHeight == -1.0) {
            this._tileHeight = this.computeTileHeight();
         }

         return this._tileHeight;
      }
   }

   public final DoubleProperty hgapProperty() {
      if (this.hgap == null) {
         this.hgap = new StyleableDoubleProperty() {
            public void invalidated() {
               TilePane.this.requestLayout();
            }

            @Override
            public CssMetaData<TilePane, Number> getCssMetaData() {
               return TilePane.StyleableProperties.HGAP;
            }

            public Object getBean() {
               return TilePane.this;
            }

            public String getName() {
               return "hgap";
            }
         };
      }

      return this.hgap;
   }

   public final void setHgap(double var1) {
      this.hgapProperty().set(var1);
   }

   public final double getHgap() {
      return this.hgap == null ? 0.0 : this.hgap.get();
   }

   public final DoubleProperty vgapProperty() {
      if (this.vgap == null) {
         this.vgap = new StyleableDoubleProperty() {
            public void invalidated() {
               TilePane.this.requestLayout();
            }

            @Override
            public CssMetaData<TilePane, Number> getCssMetaData() {
               return TilePane.StyleableProperties.VGAP;
            }

            public Object getBean() {
               return TilePane.this;
            }

            public String getName() {
               return "vgap";
            }
         };
      }

      return this.vgap;
   }

   public final void setVgap(double var1) {
      this.vgapProperty().set(var1);
   }

   public final double getVgap() {
      return this.vgap == null ? 0.0 : this.vgap.get();
   }

   public final ObjectProperty<Pos> alignmentProperty() {
      if (this.alignment == null) {
         this.alignment = new StyleableObjectProperty<Pos>(Pos.TOP_LEFT) {
            public void invalidated() {
               TilePane.this.requestLayout();
            }

            @Override
            public CssMetaData<TilePane, Pos> getCssMetaData() {
               return TilePane.StyleableProperties.ALIGNMENT;
            }

            public Object getBean() {
               return TilePane.this;
            }

            public String getName() {
               return "alignment";
            }
         };
      }

      return this.alignment;
   }

   public final void setAlignment(Pos var1) {
      this.alignmentProperty().set(var1);
   }

   public final Pos getAlignment() {
      return this.alignment == null ? Pos.TOP_LEFT : (Pos)this.alignment.get();
   }

   private Pos getAlignmentInternal() {
      Pos var1 = this.getAlignment();
      return var1 == null ? Pos.TOP_LEFT : var1;
   }

   public final ObjectProperty<Pos> tileAlignmentProperty() {
      if (this.tileAlignment == null) {
         this.tileAlignment = new StyleableObjectProperty<Pos>(Pos.CENTER) {
            public void invalidated() {
               TilePane.this.requestLayout();
            }

            @Override
            public CssMetaData<TilePane, Pos> getCssMetaData() {
               return TilePane.StyleableProperties.TILE_ALIGNMENT;
            }

            public Object getBean() {
               return TilePane.this;
            }

            public String getName() {
               return "tileAlignment";
            }
         };
      }

      return this.tileAlignment;
   }

   public final void setTileAlignment(Pos var1) {
      this.tileAlignmentProperty().set(var1);
   }

   public final Pos getTileAlignment() {
      return this.tileAlignment == null ? Pos.CENTER : (Pos)this.tileAlignment.get();
   }

   private Pos getTileAlignmentInternal() {
      Pos var1 = this.getTileAlignment();
      return var1 == null ? Pos.CENTER : var1;
   }

   @Override
   public Orientation getContentBias() {
      return this.getOrientation();
   }

   @Override
   public void requestLayout() {
      this.invalidateTileWidth();
      this.invalidateTileHeight();
      super.requestLayout();
   }

   @Override
   protected double computeMinWidth(double var1) {
      return this.getContentBias() == Orientation.HORIZONTAL
         ? this.getInsets().getLeft() + this.getTileWidth() + this.getInsets().getRight()
         : this.computePrefWidth(var1);
   }

   @Override
   protected double computeMinHeight(double var1) {
      return this.getContentBias() == Orientation.VERTICAL
         ? this.getInsets().getTop() + this.getTileHeight() + this.getInsets().getBottom()
         : this.computePrefHeight(var1);
   }

   @Override
   protected double computePrefWidth(double var1) {
      List var3 = this.getManagedChildren();
      Insets var4 = this.getInsets();
      int var5 = 0;
      if (var1 != -1.0) {
         int var6 = this.computeRows(var1 - this.snapSpaceY(var4.getTop()) - this.snapSpaceY(var4.getBottom()), this.getTileHeight());
         var5 = this.computeOther(var3.size(), var6);
      } else {
         var5 = this.getOrientation() == Orientation.HORIZONTAL ? this.getPrefColumns() : this.computeOther(var3.size(), this.getPrefRows());
      }

      return this.snapSpaceX(var4.getLeft()) + this.computeContentWidth(var5, this.getTileWidth()) + this.snapSpaceX(var4.getRight());
   }

   @Override
   protected double computePrefHeight(double var1) {
      List var3 = this.getManagedChildren();
      Insets var4 = this.getInsets();
      int var5 = 0;
      if (var1 != -1.0) {
         int var6 = this.computeColumns(var1 - this.snapSpaceX(var4.getLeft()) - this.snapSpaceX(var4.getRight()), this.getTileWidth());
         var5 = this.computeOther(var3.size(), var6);
      } else {
         var5 = this.getOrientation() == Orientation.HORIZONTAL ? this.computeOther(var3.size(), this.getPrefColumns()) : this.getPrefRows();
      }

      return this.snapSpaceY(var4.getTop()) + this.computeContentHeight(var5, this.getTileHeight()) + this.snapSpaceY(var4.getBottom());
   }

   private double computeTileWidth() {
      List var1 = this.getManagedChildren();
      double var2 = this.getPrefTileWidth();
      if (var2 != -1.0) {
         return this.snapSizeX(var2);
      } else {
         double var4 = -1.0;
         boolean var6 = false;
         int var7 = 0;

         for (int var8 = var1.size(); var7 < var8; var7++) {
            Node var9 = (Node)var1.get(var7);
            if (var9.getContentBias() == Orientation.VERTICAL) {
               var6 = true;
               break;
            }
         }

         if (var6) {
            var4 = this.computeMaxPrefAreaHeight(var1, marginAccessor, -1.0, this.getTileAlignmentInternal().getVpos());
         }

         return this.snapSizeX(this.computeMaxPrefAreaWidth(var1, marginAccessor, var4, true));
      }
   }

   private double computeTileHeight() {
      List var1 = this.getManagedChildren();
      double var2 = this.getPrefTileHeight();
      if (var2 != -1.0) {
         return this.snapSizeY(var2);
      } else {
         double var4 = -1.0;
         boolean var6 = false;
         int var7 = 0;

         for (int var8 = var1.size(); var7 < var8; var7++) {
            Node var9 = (Node)var1.get(var7);
            if (var9.getContentBias() == Orientation.HORIZONTAL) {
               var6 = true;
               break;
            }
         }

         if (var6) {
            var4 = this.computeMaxPrefAreaWidth(var1, marginAccessor);
         }

         return this.snapSizeY(this.computeMaxPrefAreaHeight(var1, marginAccessor, var4, this.getTileAlignmentInternal().getVpos()));
      }
   }

   private int computeOther(int var1, int var2) {
      double var3 = (double)var1 / (double)Math.max(1, var2);
      return (int)Math.ceil(var3);
   }

   private int computeColumns(double var1, double var3) {
      double var5 = this.snapSpaceX(this.getHgap());
      return Math.max(1, (int)((var1 + var5) / (var3 + var5)));
   }

   private int computeRows(double var1, double var3) {
      double var5 = this.snapSpaceY(this.getVgap());
      return Math.max(1, (int)((var1 + var5) / (var3 + var5)));
   }

   private double computeContentWidth(int var1, double var2) {
      return var1 == 0 ? 0.0 : (double)var1 * var2 + (double)(var1 - 1) * this.snapSpaceX(this.getHgap());
   }

   private double computeContentHeight(int var1, double var2) {
      return var1 == 0 ? 0.0 : (double)var1 * var2 + (double)(var1 - 1) * this.snapSpaceY(this.getVgap());
   }

   @Override
   protected void layoutChildren() {
      List var1 = this.getManagedChildren();
      HPos var2 = this.getAlignmentInternal().getHpos();
      VPos var3 = this.getAlignmentInternal().getVpos();
      double var4 = this.getWidth();
      double var6 = this.getHeight();
      double var8 = this.snapSpaceY(this.getInsets().getTop());
      double var10 = this.snapSpaceX(this.getInsets().getLeft());
      double var12 = this.snapSpaceY(this.getInsets().getBottom());
      double var14 = this.snapSpaceX(this.getInsets().getRight());
      double var16 = this.snapSpaceY(this.getVgap());
      double var18 = this.snapSpaceX(this.getHgap());
      double var20 = var4 - var10 - var14;
      double var22 = var6 - var8 - var12;
      double var24 = this.getTileWidth() > var20 ? var20 : this.getTileWidth();
      double var26 = this.getTileHeight() > var22 ? var22 : this.getTileHeight();
      int var28 = 0;
      int var29 = 0;
      if (this.getOrientation() == Orientation.HORIZONTAL) {
         this.actualColumns = this.computeColumns(var20, var24);
         this.actualRows = this.computeOther(var1.size(), this.actualColumns);
         var28 = var2 != HPos.LEFT ? this.actualColumns - (this.actualColumns * this.actualRows - var1.size()) : 0;
      } else {
         this.actualRows = this.computeRows(var22, var26);
         this.actualColumns = this.computeOther(var1.size(), this.actualRows);
         var29 = var3 != VPos.TOP ? this.actualRows - (this.actualColumns * this.actualRows - var1.size()) : 0;
      }

      double var30 = var10 + computeXOffset(var20, this.computeContentWidth(this.actualColumns, var24), var2);
      double var32 = var8 + computeYOffset(var22, this.computeContentHeight(this.actualRows, var26), var3);
      double var34 = var28 > 0 ? var10 + computeXOffset(var20, this.computeContentWidth(var28, var24), var2) : var30;
      double var36 = var29 > 0 ? var8 + computeYOffset(var22, this.computeContentHeight(var29, var26), var3) : var32;
      double var38 = this.getTileAlignmentInternal().getVpos() == VPos.BASELINE
         ? this.getAreaBaselineOffset(var1, marginAccessor, var2x -> var24, var26, false)
         : -1.0;
      int var40 = 0;
      int var41 = 0;
      int var42 = 0;

      for (int var43 = var1.size(); var42 < var43; var42++) {
         Node var44 = (Node)var1.get(var42);
         double var45 = var40 == this.actualRows - 1 ? var34 : var30;
         double var47 = var41 == this.actualColumns - 1 ? var36 : var32;
         double var49 = var45 + (double)var41 * (var24 + var18);
         double var51 = var47 + (double)var40 * (var26 + var16);
         Pos var53 = getAlignment(var44);
         this.layoutInArea(
            var44,
            var49,
            var51,
            var24,
            var26,
            var38,
            getMargin(var44),
            var53 != null ? var53.getHpos() : this.getTileAlignmentInternal().getHpos(),
            var53 != null ? var53.getVpos() : this.getTileAlignmentInternal().getVpos()
         );
         if (this.getOrientation() == Orientation.HORIZONTAL) {
            if (++var41 == this.actualColumns) {
               var41 = 0;
               var40++;
            }
         } else if (++var40 == this.actualRows) {
            var40 = 0;
            var41++;
         }
      }
   }

   public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
      return TilePane.StyleableProperties.STYLEABLES;
   }

   @Override
   public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
      return getClassCssMetaData();
   }

   private static class StyleableProperties {
      private static final CssMetaData<TilePane, Pos> ALIGNMENT = new CssMetaData<TilePane, Pos>("-fx-alignment", new EnumConverter<>(Pos.class), Pos.TOP_LEFT) {
         public boolean isSettable(TilePane var1) {
            return var1.alignment == null || !var1.alignment.isBound();
         }

         public StyleableProperty<Pos> getStyleableProperty(TilePane var1) {
            return (StyleableProperty<Pos>)var1.alignmentProperty();
         }
      };
      private static final CssMetaData<TilePane, Number> PREF_COLUMNS = new CssMetaData<TilePane, Number>("-fx-pref-columns", SizeConverter.getInstance(), 5.0) {
         public boolean isSettable(TilePane var1) {
            return var1.prefColumns == null || !var1.prefColumns.isBound();
         }

         public StyleableProperty<Number> getStyleableProperty(TilePane var1) {
            return (StyleableProperty<Number>)var1.prefColumnsProperty();
         }
      };
      private static final CssMetaData<TilePane, Number> HGAP = new CssMetaData<TilePane, Number>("-fx-hgap", SizeConverter.getInstance(), 0.0) {
         public boolean isSettable(TilePane var1) {
            return var1.hgap == null || !var1.hgap.isBound();
         }

         public StyleableProperty<Number> getStyleableProperty(TilePane var1) {
            return (StyleableProperty<Number>)var1.hgapProperty();
         }
      };
      private static final CssMetaData<TilePane, Number> PREF_ROWS = new CssMetaData<TilePane, Number>("-fx-pref-rows", SizeConverter.getInstance(), 5.0) {
         public boolean isSettable(TilePane var1) {
            return var1.prefRows == null || !var1.prefRows.isBound();
         }

         public StyleableProperty<Number> getStyleableProperty(TilePane var1) {
            return (StyleableProperty<Number>)var1.prefRowsProperty();
         }
      };
      private static final CssMetaData<TilePane, Pos> TILE_ALIGNMENT = new CssMetaData<TilePane, Pos>(
         "-fx-tile-alignment", new EnumConverter<>(Pos.class), Pos.CENTER
      ) {
         public boolean isSettable(TilePane var1) {
            return var1.tileAlignment == null || !var1.tileAlignment.isBound();
         }

         public StyleableProperty<Pos> getStyleableProperty(TilePane var1) {
            return (StyleableProperty<Pos>)var1.tileAlignmentProperty();
         }
      };
      private static final CssMetaData<TilePane, Number> PREF_TILE_WIDTH = new CssMetaData<TilePane, Number>(
         "-fx-pref-tile-width", SizeConverter.getInstance(), -1.0
      ) {
         public boolean isSettable(TilePane var1) {
            return var1.prefTileWidth == null || !var1.prefTileWidth.isBound();
         }

         public StyleableProperty<Number> getStyleableProperty(TilePane var1) {
            return (StyleableProperty<Number>)var1.prefTileWidthProperty();
         }
      };
      private static final CssMetaData<TilePane, Number> PREF_TILE_HEIGHT = new CssMetaData<TilePane, Number>(
         "-fx-pref-tile-height", SizeConverter.getInstance(), -1.0
      ) {
         public boolean isSettable(TilePane var1) {
            return var1.prefTileHeight == null || !var1.prefTileHeight.isBound();
         }

         public StyleableProperty<Number> getStyleableProperty(TilePane var1) {
            return (StyleableProperty<Number>)var1.prefTileHeightProperty();
         }
      };
      private static final CssMetaData<TilePane, Orientation> ORIENTATION = new CssMetaData<TilePane, Orientation>(
         "-fx-orientation", new EnumConverter<>(Orientation.class), Orientation.HORIZONTAL
      ) {
         public Orientation getInitialValue(TilePane var1) {
            return var1.getOrientation();
         }

         public boolean isSettable(TilePane var1) {
            return var1.orientation == null || !var1.orientation.isBound();
         }

         public StyleableProperty<Orientation> getStyleableProperty(TilePane var1) {
            return (StyleableProperty<Orientation>)var1.orientationProperty();
         }
      };
      private static final CssMetaData<TilePane, Number> VGAP = new CssMetaData<TilePane, Number>("-fx-vgap", SizeConverter.getInstance(), 0.0) {
         public boolean isSettable(TilePane var1) {
            return var1.vgap == null || !var1.vgap.isBound();
         }

         public StyleableProperty<Number> getStyleableProperty(TilePane var1) {
            return (StyleableProperty<Number>)var1.vgapProperty();
         }
      };
      private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

      static {
         ArrayList var0 = new ArrayList<>(Region.getClassCssMetaData());
         var0.add(ALIGNMENT);
         var0.add(HGAP);
         var0.add(ORIENTATION);
         var0.add(PREF_COLUMNS);
         var0.add(PREF_ROWS);
         var0.add(PREF_TILE_WIDTH);
         var0.add(PREF_TILE_HEIGHT);
         var0.add(TILE_ALIGNMENT);
         var0.add(VGAP);
         STYLEABLES = Collections.unmodifiableList(var0);
      }
   }

   private abstract class TileSizeProperty extends ReadOnlyDoubleProperty {
      private final String name;
      private ExpressionHelper<Number> helper;
      private double value;
      private boolean valid;

      TileSizeProperty(String var2, double var3) {
         this.name = var2;
         this.value = var3;
         this.valid = var3 != -1.0;
      }

      public Object getBean() {
         return TilePane.this;
      }

      public String getName() {
         return this.name;
      }

      public void addListener(InvalidationListener var1) {
         this.helper = ExpressionHelper.addListener(this.helper, this, var1);
      }

      public void removeListener(InvalidationListener var1) {
         this.helper = ExpressionHelper.removeListener(this.helper, var1);
      }

      public void addListener(ChangeListener<? super Number> var1) {
         this.helper = ExpressionHelper.addListener(this.helper, this, var1);
      }

      public void removeListener(ChangeListener<? super Number> var1) {
         this.helper = ExpressionHelper.removeListener(this.helper, var1);
      }

      public double get() {
         if (!this.valid) {
            this.value = this.compute();
            this.valid = true;
         }

         return this.value;
      }

      public void invalidate() {
         if (this.valid) {
            this.valid = false;
            ExpressionHelper.fireValueChangedEvent(this.helper);
         }
      }

      public abstract double compute();
   }
}
