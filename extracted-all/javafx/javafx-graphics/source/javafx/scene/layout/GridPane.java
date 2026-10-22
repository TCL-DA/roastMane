package javafx.scene.layout;

import com.sun.javafx.collections.TrackableObservableList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener.Change;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.BooleanConverter;
import javafx.css.converter.EnumConverter;
import javafx.css.converter.SizeConverter;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.util.Callback;

public class GridPane extends Pane {
   public static final int REMAINING = Integer.MAX_VALUE;
   private static final String MARGIN_CONSTRAINT = "gridpane-margin";
   private static final String HALIGNMENT_CONSTRAINT = "gridpane-halignment";
   private static final String VALIGNMENT_CONSTRAINT = "gridpane-valignment";
   private static final String HGROW_CONSTRAINT = "gridpane-hgrow";
   private static final String VGROW_CONSTRAINT = "gridpane-vgrow";
   private static final String ROW_INDEX_CONSTRAINT = "gridpane-row";
   private static final String COLUMN_INDEX_CONSTRAINT = "gridpane-column";
   private static final String ROW_SPAN_CONSTRAINT = "gridpane-row-span";
   private static final String COLUMN_SPAN_CONSTRAINT = "gridpane-column-span";
   private static final String FILL_WIDTH_CONSTRAINT = "gridpane-fill-width";
   private static final String FILL_HEIGHT_CONSTRAINT = "gridpane-fill-height";
   private static final Callback<Node, Insets> marginAccessor = var0 -> getMargin(var0);
   private static final Color GRID_LINE_COLOR = Color.rgb(30, 30, 30);
   private static final double GRID_LINE_DASH = 3.0;
   private DoubleProperty hgap;
   private DoubleProperty vgap;
   private ObjectProperty<Pos> alignment;
   private BooleanProperty gridLinesVisible;
   private final ObservableList<RowConstraints> rowConstraints = new TrackableObservableList<RowConstraints>() {
      protected void onChanged(Change<RowConstraints> var1) {
         while (var1.next()) {
            for (RowConstraints var3 : var1.getRemoved()) {
               if (var3 != null && !GridPane.this.rowConstraints.contains(var3)) {
                  var3.remove(GridPane.this);
               }
            }

            for (RowConstraints var5 : var1.getAddedSubList()) {
               if (var5 != null) {
                  var5.add(GridPane.this);
               }
            }
         }

         GridPane.this.requestLayout();
      }
   };
   private final ObservableList<ColumnConstraints> columnConstraints = new TrackableObservableList<ColumnConstraints>() {
      protected void onChanged(Change<ColumnConstraints> var1) {
         while (var1.next()) {
            for (ColumnConstraints var3 : var1.getRemoved()) {
               if (var3 != null && !GridPane.this.columnConstraints.contains(var3)) {
                  var3.remove(GridPane.this);
               }
            }

            for (ColumnConstraints var5 : var1.getAddedSubList()) {
               if (var5 != null) {
                  var5.add(GridPane.this);
               }
            }
         }

         GridPane.this.requestLayout();
      }
   };
   private Group gridLines;
   private Orientation bias;
   private double[] rowPercentHeight;
   private double rowPercentTotal = 0.0;
   private GridPane.CompositeSize rowMinHeight;
   private GridPane.CompositeSize rowPrefHeight;
   private GridPane.CompositeSize rowMaxHeight;
   private List<Node>[] rowBaseline;
   private double[] rowMinBaselineComplement;
   private double[] rowPrefBaselineComplement;
   private double[] rowMaxBaselineComplement;
   private Priority[] rowGrow;
   private double[] columnPercentWidth;
   private double columnPercentTotal = 0.0;
   private GridPane.CompositeSize columnMinWidth;
   private GridPane.CompositeSize columnPrefWidth;
   private GridPane.CompositeSize columnMaxWidth;
   private Priority[] columnGrow;
   private boolean metricsDirty = true;
   private boolean performingLayout = false;
   private int numRows;
   private int numColumns;
   private GridPane.CompositeSize currentHeights;
   private GridPane.CompositeSize currentWidths;

   public static void setRowIndex(Node var0, Integer var1) {
      if (var1 != null && var1 < 0) {
         throw new IllegalArgumentException("rowIndex must be greater or equal to 0, but was " + var1);
      } else {
         setConstraint(var0, "gridpane-row", var1);
      }
   }

   public static Integer getRowIndex(Node var0) {
      return (Integer)getConstraint(var0, "gridpane-row");
   }

   public static void setColumnIndex(Node var0, Integer var1) {
      if (var1 != null && var1 < 0) {
         throw new IllegalArgumentException("columnIndex must be greater or equal to 0, but was " + var1);
      } else {
         setConstraint(var0, "gridpane-column", var1);
      }
   }

   public static Integer getColumnIndex(Node var0) {
      return (Integer)getConstraint(var0, "gridpane-column");
   }

   public static void setRowSpan(Node var0, Integer var1) {
      if (var1 != null && var1 < 1) {
         throw new IllegalArgumentException("rowSpan must be greater or equal to 1, but was " + var1);
      } else {
         setConstraint(var0, "gridpane-row-span", var1);
      }
   }

   public static Integer getRowSpan(Node var0) {
      return (Integer)getConstraint(var0, "gridpane-row-span");
   }

   public static void setColumnSpan(Node var0, Integer var1) {
      if (var1 != null && var1 < 1) {
         throw new IllegalArgumentException("columnSpan must be greater or equal to 1, but was " + var1);
      } else {
         setConstraint(var0, "gridpane-column-span", var1);
      }
   }

   public static Integer getColumnSpan(Node var0) {
      return (Integer)getConstraint(var0, "gridpane-column-span");
   }

   public static void setMargin(Node var0, Insets var1) {
      setConstraint(var0, "gridpane-margin", var1);
   }

   public static Insets getMargin(Node var0) {
      return (Insets)getConstraint(var0, "gridpane-margin");
   }

   private double getBaselineComplementForChild(Node var1) {
      return this.isNodePositionedByBaseline(var1) ? this.rowMinBaselineComplement[getNodeRowIndex(var1)] : -1.0;
   }

   public static void setHalignment(Node var0, HPos var1) {
      setConstraint(var0, "gridpane-halignment", var1);
   }

   public static HPos getHalignment(Node var0) {
      return (HPos)getConstraint(var0, "gridpane-halignment");
   }

   public static void setValignment(Node var0, VPos var1) {
      setConstraint(var0, "gridpane-valignment", var1);
   }

   public static VPos getValignment(Node var0) {
      return (VPos)getConstraint(var0, "gridpane-valignment");
   }

   public static void setHgrow(Node var0, Priority var1) {
      setConstraint(var0, "gridpane-hgrow", var1);
   }

   public static Priority getHgrow(Node var0) {
      return (Priority)getConstraint(var0, "gridpane-hgrow");
   }

   public static void setVgrow(Node var0, Priority var1) {
      setConstraint(var0, "gridpane-vgrow", var1);
   }

   public static Priority getVgrow(Node var0) {
      return (Priority)getConstraint(var0, "gridpane-vgrow");
   }

   public static void setFillWidth(Node var0, Boolean var1) {
      setConstraint(var0, "gridpane-fill-width", var1);
   }

   public static Boolean isFillWidth(Node var0) {
      return (Boolean)getConstraint(var0, "gridpane-fill-width");
   }

   public static void setFillHeight(Node var0, Boolean var1) {
      setConstraint(var0, "gridpane-fill-height", var1);
   }

   public static Boolean isFillHeight(Node var0) {
      return (Boolean)getConstraint(var0, "gridpane-fill-height");
   }

   public static void setConstraints(Node var0, int var1, int var2) {
      setRowIndex(var0, var2);
      setColumnIndex(var0, var1);
   }

   public static void setConstraints(Node var0, int var1, int var2, int var3, int var4) {
      setRowIndex(var0, var2);
      setColumnIndex(var0, var1);
      setRowSpan(var0, var4);
      setColumnSpan(var0, var3);
   }

   public static void setConstraints(Node var0, int var1, int var2, int var3, int var4, HPos var5, VPos var6) {
      setRowIndex(var0, var2);
      setColumnIndex(var0, var1);
      setRowSpan(var0, var4);
      setColumnSpan(var0, var3);
      setHalignment(var0, var5);
      setValignment(var0, var6);
   }

   public static void setConstraints(Node var0, int var1, int var2, int var3, int var4, HPos var5, VPos var6, Priority var7, Priority var8) {
      setRowIndex(var0, var2);
      setColumnIndex(var0, var1);
      setRowSpan(var0, var4);
      setColumnSpan(var0, var3);
      setHalignment(var0, var5);
      setValignment(var0, var6);
      setHgrow(var0, var7);
      setVgrow(var0, var8);
   }

   public static void setConstraints(Node var0, int var1, int var2, int var3, int var4, HPos var5, VPos var6, Priority var7, Priority var8, Insets var9) {
      setRowIndex(var0, var2);
      setColumnIndex(var0, var1);
      setRowSpan(var0, var4);
      setColumnSpan(var0, var3);
      setHalignment(var0, var5);
      setValignment(var0, var6);
      setHgrow(var0, var7);
      setVgrow(var0, var8);
      setMargin(var0, var9);
   }

   public static void clearConstraints(Node var0) {
      setRowIndex(var0, null);
      setColumnIndex(var0, null);
      setRowSpan(var0, null);
      setColumnSpan(var0, null);
      setHalignment(var0, null);
      setValignment(var0, null);
      setHgrow(var0, null);
      setVgrow(var0, null);
      setMargin(var0, null);
   }

   static void createRow(int var0, int var1, Node... var2) {
      for (int var3 = 0; var3 < var2.length; var3++) {
         setConstraints(var2[var3], var1 + var3, var0);
      }
   }

   static void createColumn(int var0, int var1, Node... var2) {
      for (int var3 = 0; var3 < var2.length; var3++) {
         setConstraints(var2[var3], var0, var1 + var3);
      }
   }

   static int getNodeRowIndex(Node var0) {
      Integer var1 = getRowIndex(var0);
      return var1 != null ? var1 : 0;
   }

   private static int getNodeRowSpan(Node var0) {
      Integer var1 = getRowSpan(var0);
      return var1 != null ? var1 : 1;
   }

   static int getNodeRowEnd(Node var0) {
      int var1 = getNodeRowSpan(var0);
      return var1 != Integer.MAX_VALUE ? getNodeRowIndex(var0) + var1 - 1 : Integer.MAX_VALUE;
   }

   static int getNodeColumnIndex(Node var0) {
      Integer var1 = getColumnIndex(var0);
      return var1 != null ? var1 : 0;
   }

   private static int getNodeColumnSpan(Node var0) {
      Integer var1 = getColumnSpan(var0);
      return var1 != null ? var1 : 1;
   }

   static int getNodeColumnEnd(Node var0) {
      int var1 = getNodeColumnSpan(var0);
      return var1 != Integer.MAX_VALUE ? getNodeColumnIndex(var0) + var1 - 1 : Integer.MAX_VALUE;
   }

   private static Priority getNodeHgrow(Node var0) {
      Priority var1 = getHgrow(var0);
      return var1 != null ? var1 : Priority.NEVER;
   }

   private static Priority getNodeVgrow(Node var0) {
      Priority var1 = getVgrow(var0);
      return var1 != null ? var1 : Priority.NEVER;
   }

   private static Priority[] createPriorityArray(int var0, Priority var1) {
      Priority[] var2 = new Priority[var0];
      Arrays.fill(var2, var1);
      return var2;
   }

   public GridPane() {
      this.getChildren().addListener(var1 -> this.requestLayout());
   }

   public final DoubleProperty hgapProperty() {
      if (this.hgap == null) {
         this.hgap = new StyleableDoubleProperty(0.0) {
            public void invalidated() {
               GridPane.this.requestLayout();
            }

            @Override
            public CssMetaData<GridPane, Number> getCssMetaData() {
               return GridPane.StyleableProperties.HGAP;
            }

            public Object getBean() {
               return GridPane.this;
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
         this.vgap = new StyleableDoubleProperty(0.0) {
            public void invalidated() {
               GridPane.this.requestLayout();
            }

            @Override
            public CssMetaData<GridPane, Number> getCssMetaData() {
               return GridPane.StyleableProperties.VGAP;
            }

            public Object getBean() {
               return GridPane.this;
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
               GridPane.this.requestLayout();
            }

            @Override
            public CssMetaData<GridPane, Pos> getCssMetaData() {
               return GridPane.StyleableProperties.ALIGNMENT;
            }

            public Object getBean() {
               return GridPane.this;
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

   public final BooleanProperty gridLinesVisibleProperty() {
      if (this.gridLinesVisible == null) {
         this.gridLinesVisible = new StyleableBooleanProperty() {
            protected void invalidated() {
               if (this.get()) {
                  GridPane.this.gridLines = new Group();
                  GridPane.this.gridLines.setManaged(false);
                  GridPane.this.getChildren().add(GridPane.this.gridLines);
               } else {
                  GridPane.this.getChildren().remove(GridPane.this.gridLines);
                  GridPane.this.gridLines = null;
               }

               GridPane.this.requestLayout();
            }

            @Override
            public CssMetaData<GridPane, Boolean> getCssMetaData() {
               return GridPane.StyleableProperties.GRID_LINES_VISIBLE;
            }

            public Object getBean() {
               return GridPane.this;
            }

            public String getName() {
               return "gridLinesVisible";
            }
         };
      }

      return this.gridLinesVisible;
   }

   public final void setGridLinesVisible(boolean var1) {
      this.gridLinesVisibleProperty().set(var1);
   }

   public final boolean isGridLinesVisible() {
      return this.gridLinesVisible == null ? false : this.gridLinesVisible.get();
   }

   public final ObservableList<RowConstraints> getRowConstraints() {
      return this.rowConstraints;
   }

   public final ObservableList<ColumnConstraints> getColumnConstraints() {
      return this.columnConstraints;
   }

   public void add(Node var1, int var2, int var3) {
      setConstraints(var1, var2, var3);
      this.getChildren().add(var1);
   }

   public void add(Node var1, int var2, int var3, int var4, int var5) {
      setConstraints(var1, var2, var3, var4, var5);
      this.getChildren().add(var1);
   }

   public void addRow(int var1, Node... var2) {
      int var3 = 0;
      List var4 = this.getManagedChildren();
      int var5 = 0;

      for (int var6 = var4.size(); var5 < var6; var5++) {
         Node var7 = (Node)var4.get(var5);
         int var8 = getNodeRowIndex(var7);
         int var9 = getNodeRowEnd(var7);
         if (var1 >= var8 && (var1 <= var9 || var9 == Integer.MAX_VALUE)) {
            int var10 = getNodeColumnIndex(var7);
            int var11 = getNodeColumnEnd(var7);
            var3 = Math.max(var3, (var11 != Integer.MAX_VALUE ? var11 : var10) + 1);
         }
      }

      createRow(var1, var3, var2);
      this.getChildren().addAll(var2);
   }

   public void addColumn(int var1, Node... var2) {
      int var3 = 0;
      List var4 = this.getManagedChildren();
      int var5 = 0;

      for (int var6 = var4.size(); var5 < var6; var5++) {
         Node var7 = (Node)var4.get(var5);
         int var8 = getNodeColumnIndex(var7);
         int var9 = getNodeColumnEnd(var7);
         if (var1 >= var8 && (var1 <= var9 || var9 == Integer.MAX_VALUE)) {
            int var10 = getNodeRowIndex(var7);
            int var11 = getNodeRowEnd(var7);
            var3 = Math.max(var3, (var11 != Integer.MAX_VALUE ? var11 : var10) + 1);
         }
      }

      createColumn(var1, var3, var2);
      this.getChildren().addAll(var2);
   }

   private int getNumberOfRows() {
      this.computeGridMetrics();
      return this.numRows;
   }

   private int getNumberOfColumns() {
      this.computeGridMetrics();
      return this.numColumns;
   }

   private boolean isNodePositionedByBaseline(Node var1) {
      return this.getRowValignment(getNodeRowIndex(var1)) == VPos.BASELINE && getValignment(var1) == null || getValignment(var1) == VPos.BASELINE;
   }

   private void computeGridMetrics() {
      if (this.metricsDirty) {
         this.numRows = this.rowConstraints.size();
         this.numColumns = this.columnConstraints.size();
         List var1 = this.getManagedChildren();
         int var2 = 0;

         for (int var3 = var1.size(); var2 < var3; var2++) {
            Node var4 = (Node)var1.get(var2);
            int var5 = getNodeRowIndex(var4);
            int var6 = getNodeColumnIndex(var4);
            int var7 = getNodeRowEnd(var4);
            int var8 = getNodeColumnEnd(var4);
            this.numRows = Math.max(this.numRows, (var7 != Integer.MAX_VALUE ? var7 : var5) + 1);
            this.numColumns = Math.max(this.numColumns, (var8 != Integer.MAX_VALUE ? var8 : var6) + 1);
         }

         this.rowPercentHeight = createDoubleArray(this.numRows, -1.0);
         this.rowPercentTotal = 0.0;
         this.columnPercentWidth = createDoubleArray(this.numColumns, -1.0);
         this.columnPercentTotal = 0.0;
         this.columnGrow = createPriorityArray(this.numColumns, Priority.NEVER);
         this.rowGrow = createPriorityArray(this.numRows, Priority.NEVER);
         this.rowMinBaselineComplement = createDoubleArray(this.numRows, -1.0);
         this.rowPrefBaselineComplement = createDoubleArray(this.numRows, -1.0);
         this.rowMaxBaselineComplement = createDoubleArray(this.numRows, -1.0);
         this.rowBaseline = new List[this.numRows];
         var2 = 0;

         for (int var17 = this.numRows; var2 < var17; var2++) {
            if (var2 < this.rowConstraints.size()) {
               RowConstraints var21 = (RowConstraints)this.rowConstraints.get(var2);
               double var27 = var21.getPercentHeight();
               Priority var35 = var21.getVgrow();
               if (var27 >= 0.0) {
                  this.rowPercentHeight[var2] = var27;
               }

               if (var35 != null) {
                  this.rowGrow[var2] = var35;
               }
            }

            ArrayList var22 = new ArrayList(this.numColumns);
            int var28 = 0;

            for (int var32 = var1.size(); var28 < var32; var28++) {
               Node var36 = (Node)var1.get(var28);
               if (getNodeRowIndex(var36) == var2 && this.isNodePositionedByBaseline(var36)) {
                  var22.add(var36);
               }
            }

            this.rowMinBaselineComplement[var2] = getMinBaselineComplement(var22);
            this.rowPrefBaselineComplement[var2] = getPrefBaselineComplement(var22);
            this.rowMaxBaselineComplement[var2] = getMaxBaselineComplement(var22);
            this.rowBaseline[var2] = var22;
         }

         var2 = 0;

         for (int var18 = Math.min(this.numColumns, this.columnConstraints.size()); var2 < var18; var2++) {
            ColumnConstraints var23 = (ColumnConstraints)this.columnConstraints.get(var2);
            double var29 = var23.getPercentWidth();
            Priority var37 = var23.getHgrow();
            if (var29 >= 0.0) {
               this.columnPercentWidth[var2] = var29;
            }

            if (var37 != null) {
               this.columnGrow[var2] = var37;
            }
         }

         var2 = 0;

         for (int var19 = var1.size(); var2 < var19; var2++) {
            Node var24 = (Node)var1.get(var2);
            if (getNodeColumnSpan(var24) == 1) {
               Priority var30 = getNodeHgrow(var24);
               int var33 = getNodeColumnIndex(var24);
               this.columnGrow[var33] = Priority.max(this.columnGrow[var33], var30);
            }

            if (getNodeRowSpan(var24) == 1) {
               Priority var31 = getNodeVgrow(var24);
               int var34 = getNodeRowIndex(var24);
               this.rowGrow[var34] = Priority.max(this.rowGrow[var34], var31);
            }
         }

         for (int var12 = 0; var12 < this.rowPercentHeight.length; var12++) {
            if (this.rowPercentHeight[var12] > 0.0) {
               this.rowPercentTotal = this.rowPercentTotal + this.rowPercentHeight[var12];
            }
         }

         if (this.rowPercentTotal > 100.0) {
            double var13 = 100.0 / this.rowPercentTotal;

            for (int var25 = 0; var25 < this.rowPercentHeight.length; var25++) {
               if (this.rowPercentHeight[var25] > 0.0) {
                  this.rowPercentHeight[var25] = this.rowPercentHeight[var25] * var13;
               }
            }

            this.rowPercentTotal = 100.0;
         }

         for (int var14 = 0; var14 < this.columnPercentWidth.length; var14++) {
            if (this.columnPercentWidth[var14] > 0.0) {
               this.columnPercentTotal = this.columnPercentTotal + this.columnPercentWidth[var14];
            }
         }

         if (this.columnPercentTotal > 100.0) {
            double var15 = 100.0 / this.columnPercentTotal;

            for (int var26 = 0; var26 < this.columnPercentWidth.length; var26++) {
               if (this.columnPercentWidth[var26] > 0.0) {
                  this.columnPercentWidth[var26] = this.columnPercentWidth[var26] * var15;
               }
            }

            this.columnPercentTotal = 100.0;
         }

         this.bias = null;

         for (int var16 = 0; var16 < var1.size(); var16++) {
            Orientation var20 = ((Node)var1.get(var16)).getContentBias();
            if (var20 != null) {
               this.bias = var20;
               if (var20 == Orientation.HORIZONTAL) {
                  break;
               }
            }
         }

         this.metricsDirty = false;
      }
   }

   @Override
   protected double computeMinWidth(double var1) {
      this.computeGridMetrics();
      this.performingLayout = true;

      double var4;
      try {
         double[] var3 = var1 == -1.0 ? null : this.computeHeightsToFit(var1).asArray();
         var4 = this.snapSpaceX(this.getInsets().getLeft())
            + this.computeMinWidths(var3).computeTotalWithMultiSize()
            + this.snapSpaceX(this.getInsets().getRight());
      } finally {
         this.performingLayout = false;
      }

      return var4;
   }

   @Override
   protected double computeMinHeight(double var1) {
      this.computeGridMetrics();
      this.performingLayout = true;

      double var4;
      try {
         double[] var3 = var1 == -1.0 ? null : this.computeWidthsToFit(var1).asArray();
         var4 = this.snapSpaceY(this.getInsets().getTop())
            + this.computeMinHeights(var3).computeTotalWithMultiSize()
            + this.snapSpaceY(this.getInsets().getBottom());
      } finally {
         this.performingLayout = false;
      }

      return var4;
   }

   @Override
   protected double computePrefWidth(double var1) {
      this.computeGridMetrics();
      this.performingLayout = true;

      double var4;
      try {
         double[] var3 = var1 == -1.0 ? null : this.computeHeightsToFit(var1).asArray();
         var4 = this.snapSpaceX(this.getInsets().getLeft())
            + this.computePrefWidths(var3).computeTotalWithMultiSize()
            + this.snapSpaceX(this.getInsets().getRight());
      } finally {
         this.performingLayout = false;
      }

      return var4;
   }

   @Override
   protected double computePrefHeight(double var1) {
      this.computeGridMetrics();
      this.performingLayout = true;

      double var4;
      try {
         double[] var3 = var1 == -1.0 ? null : this.computeWidthsToFit(var1).asArray();
         var4 = this.snapSpaceY(this.getInsets().getTop())
            + this.computePrefHeights(var3).computeTotalWithMultiSize()
            + this.snapSpaceY(this.getInsets().getBottom());
      } finally {
         this.performingLayout = false;
      }

      return var4;
   }

   private VPos getRowValignment(int var1) {
      if (var1 < this.getRowConstraints().size()) {
         RowConstraints var2 = (RowConstraints)this.getRowConstraints().get(var1);
         if (var2.getValignment() != null) {
            return var2.getValignment();
         }
      }

      return VPos.CENTER;
   }

   private HPos getColumnHalignment(int var1) {
      if (var1 < this.getColumnConstraints().size()) {
         ColumnConstraints var2 = (ColumnConstraints)this.getColumnConstraints().get(var1);
         if (var2.getHalignment() != null) {
            return var2.getHalignment();
         }
      }

      return HPos.LEFT;
   }

   private double getColumnMinWidth(int var1) {
      if (var1 < this.getColumnConstraints().size()) {
         ColumnConstraints var2 = (ColumnConstraints)this.getColumnConstraints().get(var1);
         return var2.getMinWidth();
      } else {
         return -1.0;
      }
   }

   private double getRowMinHeight(int var1) {
      if (var1 < this.getRowConstraints().size()) {
         RowConstraints var2 = (RowConstraints)this.getRowConstraints().get(var1);
         return var2.getMinHeight();
      } else {
         return -1.0;
      }
   }

   private double getColumnMaxWidth(int var1) {
      if (var1 < this.getColumnConstraints().size()) {
         ColumnConstraints var2 = (ColumnConstraints)this.getColumnConstraints().get(var1);
         return var2.getMaxWidth();
      } else {
         return -1.0;
      }
   }

   private double getColumnPrefWidth(int var1) {
      if (var1 < this.getColumnConstraints().size()) {
         ColumnConstraints var2 = (ColumnConstraints)this.getColumnConstraints().get(var1);
         return var2.getPrefWidth();
      } else {
         return -1.0;
      }
   }

   private double getRowPrefHeight(int var1) {
      if (var1 < this.getRowConstraints().size()) {
         RowConstraints var2 = (RowConstraints)this.getRowConstraints().get(var1);
         return var2.getPrefHeight();
      } else {
         return -1.0;
      }
   }

   private double getRowMaxHeight(int var1) {
      if (var1 < this.getRowConstraints().size()) {
         RowConstraints var2 = (RowConstraints)this.getRowConstraints().get(var1);
         return var2.getMaxHeight();
      } else {
         return -1.0;
      }
   }

   private boolean shouldRowFillHeight(int var1) {
      return var1 < this.getRowConstraints().size() ? ((RowConstraints)this.getRowConstraints().get(var1)).isFillHeight() : true;
   }

   private boolean shouldColumnFillWidth(int var1) {
      return var1 < this.getColumnConstraints().size() ? ((ColumnConstraints)this.getColumnConstraints().get(var1)).isFillWidth() : true;
   }

   private double getTotalWidthOfNodeColumns(Node var1, double[] var2) {
      if (getNodeColumnSpan(var1) == 1) {
         return var2[getNodeColumnIndex(var1)];
      } else {
         double var3 = 0.0;
         int var5 = getNodeColumnIndex(var1);

         for (int var6 = this.getNodeColumnEndConvertRemaining(var1); var5 <= var6; var5++) {
            var3 += var2[var5];
         }

         return var3;
      }
   }

   private GridPane.CompositeSize computeMaxHeights() {
      if (this.rowMaxHeight == null) {
         this.rowMaxHeight = this.createCompositeRows(Double.MAX_VALUE);
         ObservableList var1 = this.getRowConstraints();
         GridPane.CompositeSize var2 = null;

         for (int var3 = 0; var3 < var1.size(); var3++) {
            RowConstraints var4 = (RowConstraints)var1.get(var3);
            double var5 = var4.getMaxHeight();
            if (var5 == Double.NEGATIVE_INFINITY) {
               if (var2 == null) {
                  var2 = this.computePrefHeights(null);
               }

               this.rowMaxHeight.setPresetSize(var3, var2.getSize(var3));
            } else if (var5 != -1.0) {
               double var7 = this.snapSizeY(var5);
               double var9 = var4.getMinHeight();
               if (var9 >= 0.0) {
                  double var11 = this.snapSizeY(var4.getMinHeight());
                  this.rowMaxHeight.setPresetSize(var3, boundedSize(var11, var7, var7));
               } else {
                  this.rowMaxHeight.setPresetSize(var3, var7);
               }
            }
         }
      }

      return this.rowMaxHeight;
   }

   private GridPane.CompositeSize computePrefHeights(double[] var1) {
      GridPane.CompositeSize var2;
      if (var1 == null) {
         if (this.rowPrefHeight != null) {
            return this.rowPrefHeight;
         }

         this.rowPrefHeight = this.createCompositeRows(0.0);
         var2 = this.rowPrefHeight;
      } else {
         var2 = this.createCompositeRows(0.0);
      }

      ObservableList var3 = this.getRowConstraints();

      for (int var4 = 0; var4 < var3.size(); var4++) {
         RowConstraints var5 = (RowConstraints)var3.get(var4);
         double var6 = var5.getMinHeight();
         double var8 = var5.getPrefHeight();
         if (var8 != -1.0) {
            double var10 = this.snapSizeY(var8);
            double var12 = var5.getMaxHeight();
            if (!(var6 >= 0.0) && !(var12 >= 0.0)) {
               var2.setPresetSize(var4, var10);
            } else {
               double var14 = var6 < 0.0 ? 0.0 : this.snapSizeY(var6);
               double var16 = var12 < 0.0 ? Double.POSITIVE_INFINITY : this.snapSizeY(var12);
               var2.setPresetSize(var4, boundedSize(var14, var10, var16));
            }
         } else if (var6 > 0.0) {
            var2.setSize(var4, this.snapSizeY(var6));
         }
      }

      List var18 = this.getManagedChildren();
      int var19 = 0;

      for (int var20 = var18.size(); var19 < var20; var19++) {
         Node var7 = (Node)var18.get(var19);
         int var21 = getNodeRowIndex(var7);
         int var9 = this.getNodeRowEndConvertRemaining(var7);
         double var22 = this.computeChildPrefAreaHeight(
            var7,
            this.isNodePositionedByBaseline(var7) ? this.rowPrefBaselineComplement[var21] : -1.0,
            getMargin(var7),
            var1 == null ? -1.0 : this.getTotalWidthOfNodeColumns(var7, var1)
         );
         if (var21 == var9 && !var2.isPreset(var21)) {
            double var23 = this.getRowMinHeight(var21);
            double var24 = this.getRowMaxHeight(var21);
            var2.setMaxSize(var21, boundedSize(var23 < 0.0 ? 0.0 : var23, var22, var24 < 0.0 ? Double.MAX_VALUE : var24));
         } else if (var21 != var9) {
            var2.setMaxMultiSize(var21, var9 + 1, var22);
         }
      }

      return var2;
   }

   private GridPane.CompositeSize computeMinHeights(double[] var1) {
      GridPane.CompositeSize var2;
      if (var1 == null) {
         if (this.rowMinHeight != null) {
            return this.rowMinHeight;
         }

         this.rowMinHeight = this.createCompositeRows(0.0);
         var2 = this.rowMinHeight;
      } else {
         var2 = this.createCompositeRows(0.0);
      }

      ObservableList var3 = this.getRowConstraints();
      GridPane.CompositeSize var4 = null;

      for (int var5 = 0; var5 < var3.size(); var5++) {
         double var6 = ((RowConstraints)var3.get(var5)).getMinHeight();
         if (var6 == Double.NEGATIVE_INFINITY) {
            if (var4 == null) {
               var4 = this.computePrefHeights(var1);
            }

            var2.setPresetSize(var5, var4.getSize(var5));
         } else if (var6 != -1.0) {
            var2.setPresetSize(var5, this.snapSizeY(var6));
         }
      }

      List var13 = this.getManagedChildren();
      int var14 = 0;

      for (int var7 = var13.size(); var14 < var7; var14++) {
         Node var8 = (Node)var13.get(var14);
         int var9 = getNodeRowIndex(var8);
         int var10 = this.getNodeRowEndConvertRemaining(var8);
         double var11 = this.computeChildMinAreaHeight(
            var8,
            this.isNodePositionedByBaseline(var8) ? this.rowMinBaselineComplement[var9] : -1.0,
            getMargin(var8),
            var1 == null ? -1.0 : this.getTotalWidthOfNodeColumns(var8, var1)
         );
         if (var9 == var10 && !var2.isPreset(var9)) {
            var2.setMaxSize(var9, var11);
         } else if (var9 != var10) {
            var2.setMaxMultiSize(var9, var10 + 1, var11);
         }
      }

      return var2;
   }

   private double getTotalHeightOfNodeRows(Node var1, double[] var2) {
      if (getNodeRowSpan(var1) == 1) {
         return var2[getNodeRowIndex(var1)];
      } else {
         double var3 = 0.0;
         int var5 = getNodeRowIndex(var1);

         for (int var6 = this.getNodeRowEndConvertRemaining(var1); var5 <= var6; var5++) {
            var3 += var2[var5];
         }

         return var3;
      }
   }

   private GridPane.CompositeSize computeMaxWidths() {
      if (this.columnMaxWidth == null) {
         this.columnMaxWidth = this.createCompositeColumns(Double.MAX_VALUE);
         ObservableList var1 = this.getColumnConstraints();
         GridPane.CompositeSize var2 = null;

         for (int var3 = 0; var3 < var1.size(); var3++) {
            ColumnConstraints var4 = (ColumnConstraints)var1.get(var3);
            double var5 = var4.getMaxWidth();
            if (var5 == Double.NEGATIVE_INFINITY) {
               if (var2 == null) {
                  var2 = this.computePrefWidths(null);
               }

               this.columnMaxWidth.setPresetSize(var3, var2.getSize(var3));
            } else if (var5 != -1.0) {
               double var7 = this.snapSizeX(var5);
               double var9 = var4.getMinWidth();
               if (var9 >= 0.0) {
                  double var11 = this.snapSizeX(var9);
                  this.columnMaxWidth.setPresetSize(var3, boundedSize(var11, var7, var7));
               } else {
                  this.columnMaxWidth.setPresetSize(var3, var7);
               }
            }
         }
      }

      return this.columnMaxWidth;
   }

   private GridPane.CompositeSize computePrefWidths(double[] var1) {
      GridPane.CompositeSize var2;
      if (var1 == null) {
         if (this.columnPrefWidth != null) {
            return this.columnPrefWidth;
         }

         this.columnPrefWidth = this.createCompositeColumns(0.0);
         var2 = this.columnPrefWidth;
      } else {
         var2 = this.createCompositeColumns(0.0);
      }

      ObservableList var3 = this.getColumnConstraints();

      for (int var4 = 0; var4 < var3.size(); var4++) {
         ColumnConstraints var5 = (ColumnConstraints)var3.get(var4);
         double var6 = var5.getPrefWidth();
         double var8 = var5.getMinWidth();
         if (var6 != -1.0) {
            double var10 = this.snapSizeX(var6);
            double var12 = var5.getMaxWidth();
            if (!(var8 >= 0.0) && !(var12 >= 0.0)) {
               var2.setPresetSize(var4, var10);
            } else {
               double var14 = var8 < 0.0 ? 0.0 : this.snapSizeX(var8);
               double var16 = var12 < 0.0 ? Double.POSITIVE_INFINITY : this.snapSizeX(var12);
               var2.setPresetSize(var4, boundedSize(var14 < 0.0 ? 0.0 : var14, var10, var16 < 0.0 ? Double.POSITIVE_INFINITY : var16));
            }
         } else if (var8 > 0.0) {
            var2.setSize(var4, this.snapSizeX(var8));
         }
      }

      List var18 = this.getManagedChildren();
      int var19 = 0;

      for (int var20 = var18.size(); var19 < var20; var19++) {
         Node var7 = (Node)var18.get(var19);
         int var21 = getNodeColumnIndex(var7);
         int var9 = this.getNodeColumnEndConvertRemaining(var7);
         if (var21 == var9 && !var2.isPreset(var21)) {
            double var22 = this.getColumnMinWidth(var21);
            double var23 = this.getColumnMaxWidth(var21);
            var2.setMaxSize(
               var21,
               boundedSize(
                  var22 < 0.0 ? 0.0 : var22,
                  this.computeChildPrefAreaWidth(
                     var7, this.getBaselineComplementForChild(var7), getMargin(var7), var1 == null ? -1.0 : this.getTotalHeightOfNodeRows(var7, var1), false
                  ),
                  var23 < 0.0 ? Double.MAX_VALUE : var23
               )
            );
         } else if (var21 != var9) {
            var2.setMaxMultiSize(
               var21,
               var9 + 1,
               this.computeChildPrefAreaWidth(
                  var7, this.getBaselineComplementForChild(var7), getMargin(var7), var1 == null ? -1.0 : this.getTotalHeightOfNodeRows(var7, var1), false
               )
            );
         }
      }

      return var2;
   }

   private GridPane.CompositeSize computeMinWidths(double[] var1) {
      GridPane.CompositeSize var2;
      if (var1 == null) {
         if (this.columnMinWidth != null) {
            return this.columnMinWidth;
         }

         this.columnMinWidth = this.createCompositeColumns(0.0);
         var2 = this.columnMinWidth;
      } else {
         var2 = this.createCompositeColumns(0.0);
      }

      ObservableList var3 = this.getColumnConstraints();
      GridPane.CompositeSize var4 = null;

      for (int var5 = 0; var5 < var3.size(); var5++) {
         double var6 = ((ColumnConstraints)var3.get(var5)).getMinWidth();
         if (var6 == Double.NEGATIVE_INFINITY) {
            if (var4 == null) {
               var4 = this.computePrefWidths(var1);
            }

            var2.setPresetSize(var5, var4.getSize(var5));
         } else if (var6 != -1.0) {
            var2.setPresetSize(var5, this.snapSizeX(var6));
         }
      }

      List var11 = this.getManagedChildren();
      int var12 = 0;

      for (int var7 = var11.size(); var12 < var7; var12++) {
         Node var8 = (Node)var11.get(var12);
         int var9 = getNodeColumnIndex(var8);
         int var10 = this.getNodeColumnEndConvertRemaining(var8);
         if (var9 == var10 && !var2.isPreset(var9)) {
            var2.setMaxSize(
               var9,
               this.computeChildMinAreaWidth(
                  var8, this.getBaselineComplementForChild(var8), getMargin(var8), var1 == null ? -1.0 : this.getTotalHeightOfNodeRows(var8, var1), false
               )
            );
         } else if (var9 != var10) {
            var2.setMaxMultiSize(
               var9,
               var10 + 1,
               this.computeChildMinAreaWidth(
                  var8, this.getBaselineComplementForChild(var8), getMargin(var8), var1 == null ? -1.0 : this.getTotalHeightOfNodeRows(var8, var1), false
               )
            );
         }
      }

      return var2;
   }

   private GridPane.CompositeSize computeHeightsToFit(double var1) {
      assert var1 != -1.0;

      GridPane.CompositeSize var3;
      if (this.rowPercentTotal == 100.0) {
         var3 = this.createCompositeRows(0.0);
      } else {
         var3 = (GridPane.CompositeSize)this.computePrefHeights(null).clone();
      }

      this.adjustRowHeights(var3, var1);
      return var3;
   }

   private GridPane.CompositeSize computeWidthsToFit(double var1) {
      assert var1 != -1.0;

      GridPane.CompositeSize var3;
      if (this.columnPercentTotal == 100.0) {
         var3 = this.createCompositeColumns(0.0);
      } else {
         var3 = (GridPane.CompositeSize)this.computePrefWidths(null).clone();
      }

      this.adjustColumnWidths(var3, var1);
      return var3;
   }

   @Override
   public Orientation getContentBias() {
      this.computeGridMetrics();
      return this.bias;
   }

   @Override
   public void requestLayout() {
      if (!this.performingLayout) {
         if (this.metricsDirty) {
            super.requestLayout();
         } else {
            this.metricsDirty = true;
            this.bias = null;
            this.rowGrow = null;
            this.rowMinHeight = this.rowPrefHeight = this.rowMaxHeight = null;
            this.columnGrow = null;
            this.columnMinWidth = this.columnPrefWidth = this.columnMaxWidth = null;
            this.rowMinBaselineComplement = this.rowPrefBaselineComplement = this.rowMaxBaselineComplement = null;
            super.requestLayout();
         }
      }
   }

   @Override
   protected void layoutChildren() {
      this.performingLayout = true;

      try {
         double var1 = this.snapSpaceX(this.getHgap());
         double var3 = this.snapSpaceY(this.getVgap());
         double var5 = this.snapSpaceY(this.getInsets().getTop());
         double var7 = this.snapSpaceY(this.getInsets().getBottom());
         double var9 = this.snapSpaceX(this.getInsets().getLeft());
         double var11 = this.snapSpaceX(this.getInsets().getRight());
         double var13 = this.getWidth();
         double var15 = this.getHeight();
         double var17 = var15 - var5 - var7;
         double var19 = var13 - var9 - var11;
         this.computeGridMetrics();
         Orientation var25 = this.getContentBias();
         double var21;
         double var23;
         GridPane.CompositeSize var26;
         GridPane.CompositeSize var27;
         if (var25 == null) {
            var26 = (GridPane.CompositeSize)this.computePrefHeights(null).clone();
            var27 = (GridPane.CompositeSize)this.computePrefWidths(null).clone();
            var23 = this.adjustRowHeights(var26, var15);
            var21 = this.adjustColumnWidths(var27, var13);
         } else if (var25 == Orientation.HORIZONTAL) {
            var27 = (GridPane.CompositeSize)this.computePrefWidths(null).clone();
            var21 = this.adjustColumnWidths(var27, var13);
            var26 = this.computePrefHeights(var27.asArray());
            var23 = this.adjustRowHeights(var26, var15);
         } else {
            var26 = (GridPane.CompositeSize)this.computePrefHeights(null).clone();
            var23 = this.adjustRowHeights(var26, var15);
            var27 = this.computePrefWidths(var26.asArray());
            var21 = this.adjustColumnWidths(var27, var13);
         }

         double var28 = var9 + computeXOffset(var19, var21, this.getAlignmentInternal().getHpos());
         double var30 = var5 + computeYOffset(var17, var23, this.getAlignmentInternal().getVpos());
         List var32 = this.getManagedChildren();
         double[] var33 = createDoubleArray(this.numRows, -1.0);
         int var34 = 0;

         for (int var35 = var32.size(); var34 < var35; var34++) {
            Node var36 = (Node)var32.get(var34);
            int var37 = getNodeRowIndex(var36);
            int var38 = getNodeColumnIndex(var36);
            int var39 = getNodeColumnSpan(var36);
            if (var39 == Integer.MAX_VALUE) {
               var39 = var27.getLength() - var38;
            }

            int var40 = getNodeRowSpan(var36);
            if (var40 == Integer.MAX_VALUE) {
               var40 = var26.getLength() - var37;
            }

            double var41 = var28;

            for (int var43 = 0; var43 < var38; var43++) {
               var41 += var27.getSize(var43) + var1;
            }

            double var59 = var30;

            for (int var45 = 0; var45 < var37; var45++) {
               var59 += var26.getSize(var45) + var3;
            }

            double var60 = var27.getSize(var38);

            for (int var47 = 2; var47 <= var39; var47++) {
               var60 += var27.getSize(var38 + var47 - 1) + var1;
            }

            double var61 = var26.getSize(var37);

            for (int var49 = 2; var49 <= var40; var49++) {
               var61 += var26.getSize(var37 + var49 - 1) + var3;
            }

            HPos var62 = getHalignment(var36);
            VPos var50 = getValignment(var36);
            Boolean var51 = isFillWidth(var36);
            Boolean var52 = isFillHeight(var36);
            if (var62 == null) {
               var62 = this.getColumnHalignment(var38);
            }

            if (var50 == null) {
               var50 = this.getRowValignment(var37);
            }

            if (var51 == null) {
               var51 = this.shouldColumnFillWidth(var38);
            }

            if (var52 == null) {
               var52 = this.shouldRowFillHeight(var37);
            }

            double var53 = 0.0;
            if (var50 == VPos.BASELINE) {
               if (var33[var37] == -1.0) {
                  var33[var37] = this.getAreaBaselineOffset(this.rowBaseline[var37], marginAccessor, var5x -> {
                     Node var6 = this.rowBaseline[var37].get(var5x);
                     int var7x = getNodeColumnIndex(var6);
                     int var8 = getNodeColumnSpan(var6);
                     if (var8 == Integer.MAX_VALUE) {
                        var8 = var27.getLength() - var7x;
                     }

                     double var9x = var27.getSize(var7x);

                     for (int var11x = 2; var11x <= var8; var11x++) {
                        var9x += var27.getSize(var7x + var11x - 1) + var1;
                     }

                     return var9x;
                  }, var61, var2 -> {
                     Boolean var3x = isFillHeight(var36);
                     return var3x != null ? var3x : this.shouldRowFillHeight(getNodeRowIndex(var36));
                  }, this.rowMinBaselineComplement[var37]);
               }

               var53 = var33[var37];
            }

            Insets var55 = getMargin(var36);
            this.layoutInArea(var36, var41, var59, var60, var61, var53, var55, var51, var52, var62, var50);
         }

         this.layoutGridLines(var27, var26, var28, var30, var23, var21);
         this.currentHeights = var26;
         this.currentWidths = var27;
      } finally {
         this.performingLayout = false;
      }
   }

   private double adjustRowHeights(GridPane.CompositeSize var1, double var2) {
      assert var2 != -1.0;

      double var4 = this.snapSpaceY(this.getVgap());
      double var6 = this.snapSpaceY(this.getInsets().getTop());
      double var8 = this.snapSpaceY(this.getInsets().getBottom());
      double var10 = var4 * (double)(this.getNumberOfRows() - 1);
      double var12 = var2 - var6 - var8;
      if (this.rowPercentTotal > 0.0) {
         double var14 = 0.0;

         for (int var16 = 0; var16 < this.rowPercentHeight.length; var16++) {
            if (this.rowPercentHeight[var16] >= 0.0) {
               double var17 = (var12 - var10) * (this.rowPercentHeight[var16] / 100.0);
               double var19 = Math.floor(var17);
               var14 += var17 - var19;
               var17 = var19;
               if (var14 >= 0.5) {
                  var17 = var19 + 1.0;
                  var14 += -1.0;
               }

               var1.setSize(var16, var17);
            }
         }
      }

      double var21 = var1.computeTotal();
      if (this.rowPercentTotal < 100.0) {
         double var22 = var2 - var6 - var8 - var21;
         if (var22 != 0.0) {
            double var18 = this.growToMultiSpanPreferredHeights(var1, var22);
            var18 = this.growOrShrinkRowHeights(var1, Priority.ALWAYS, var18);
            var18 = this.growOrShrinkRowHeights(var1, Priority.SOMETIMES, var18);
            var21 += var22 - var18;
         }
      }

      return var21;
   }

   private double growToMultiSpanPreferredHeights(GridPane.CompositeSize var1, double var2) {
      if (var2 <= 0.0) {
         return var2;
      } else {
         TreeSet var4 = new TreeSet();
         TreeSet var5 = new TreeSet();
         TreeSet var6 = new TreeSet();

         for (Entry var8 : var1.multiSizes()) {
            GridPane.Interval var9 = (GridPane.Interval)var8.getKey();

            for (int var10 = var9.begin; var10 < var9.end; var10++) {
               if (this.rowPercentHeight[var10] < 0.0) {
                  switch (this.rowGrow[var10]) {
                     case ALWAYS:
                        var4.add(var10);
                        break;
                     case SOMETIMES:
                        var5.add(var10);
                  }
               }
            }

            if (this.rowPercentHeight[var9.end - 1] < 0.0) {
               var6.add(var9.end - 1);
            }
         }

         double var25 = var2;

         while (var4.size() > 0 && var25 > (double)var4.size()) {
            double var26 = Math.floor(var25 / (double)var4.size());
            Iterator var11 = var4.iterator();

            while (var11.hasNext()) {
               int var12 = (Integer)var11.next();
               double var13 = this.getRowMaxHeight(var12);
               double var15 = this.getRowPrefHeight(var12);
               double var17 = var26;

               for (Entry var20 : var1.multiSizes()) {
                  GridPane.Interval var21 = (GridPane.Interval)var20.getKey();
                  if (var21.contains(var12)) {
                     int var22 = 0;

                     for (int var23 = var21.begin; var23 < var21.end; var23++) {
                        if (var4.contains(var23)) {
                           var22++;
                        }
                     }

                     double var53 = var1.computeTotal(var21.begin, var21.end);
                     var17 = Math.min(Math.floor(Math.max(0.0, ((Double)var20.getValue() - var53) / (double)var22)), var17);
                  }
               }

               double var39 = var1.getSize(var12);
               double var46 = var13 >= 0.0
                  ? boundedSize(0.0, var39 + var17, var13)
                  : (var13 == Double.NEGATIVE_INFINITY && var15 > 0.0 ? boundedSize(0.0, var39 + var17, var15) : var39 + var17);
               double var54 = var46 - var39;
               var25 -= var54;
               if (var54 != var17 || var54 == 0.0) {
                  var11.remove();
               }

               var1.setSize(var12, var46);
            }
         }

         while (var5.size() > 0 && var25 > (double)var5.size()) {
            double var27 = Math.floor(var25 / (double)var5.size());
            Iterator var29 = var5.iterator();

            while (var29.hasNext()) {
               int var31 = (Integer)var29.next();
               double var33 = this.getRowMaxHeight(var31);
               double var35 = this.getRowPrefHeight(var31);
               double var37 = var27;

               for (Entry var44 : var1.multiSizes()) {
                  GridPane.Interval var47 = (GridPane.Interval)var44.getKey();
                  if (var47.contains(var31)) {
                     int var51 = 0;

                     for (int var55 = var47.begin; var55 < var47.end; var55++) {
                        if (var5.contains(var55)) {
                           var51++;
                        }
                     }

                     double var56 = var1.computeTotal(var47.begin, var47.end);
                     var37 = Math.min(Math.floor(Math.max(0.0, ((Double)var44.getValue() - var56) / (double)var51)), var37);
                  }
               }

               double var41 = var1.getSize(var31);
               double var48 = var33 >= 0.0
                  ? boundedSize(0.0, var41 + var37, var33)
                  : (var33 == Double.NEGATIVE_INFINITY && var35 > 0.0 ? boundedSize(0.0, var41 + var37, var35) : var41 + var37);
               double var57 = var48 - var41;
               var25 -= var57;
               if (var57 != var37 || var57 == 0.0) {
                  var29.remove();
               }

               var1.setSize(var31, var48);
            }
         }

         while (var6.size() > 0 && var25 > (double)var6.size()) {
            double var28 = Math.floor(var25 / (double)var6.size());
            Iterator var30 = var6.iterator();

            while (var30.hasNext()) {
               int var32 = (Integer)var30.next();
               double var34 = this.getRowMaxHeight(var32);
               double var36 = this.getRowPrefHeight(var32);
               double var38 = var28;

               for (Entry var45 : var1.multiSizes()) {
                  GridPane.Interval var49 = (GridPane.Interval)var45.getKey();
                  if (var49.end - 1 == var32) {
                     double var52 = var1.computeTotal(var49.begin, var49.end);
                     var38 = Math.min(Math.max(0.0, (Double)var45.getValue() - var52), var38);
                  }
               }

               double var43 = var1.getSize(var32);
               double var50 = var34 >= 0.0
                  ? boundedSize(0.0, var43 + var38, var34)
                  : (var34 == Double.NEGATIVE_INFINITY && var36 > 0.0 ? boundedSize(0.0, var43 + var38, var36) : var43 + var38);
               double var58 = var50 - var43;
               var25 -= var58;
               if (var58 != var38 || var58 == 0.0) {
                  var30.remove();
               }

               var1.setSize(var32, var50);
            }
         }

         return var25;
      }
   }

   private double growOrShrinkRowHeights(GridPane.CompositeSize var1, Priority var2, double var3) {
      boolean var5 = var3 < 0.0;
      ArrayList var6 = new ArrayList();

      for (int var7 = 0; var7 < this.rowGrow.length; var7++) {
         if (this.rowPercentHeight[var7] < 0.0 && (var5 || this.rowGrow[var7] == var2)) {
            var6.add(var7);
         }
      }

      double var21 = var3;
      boolean var9 = false;
      double var10 = 0.0;
      boolean var12 = var3 >= 0.0;
      boolean var13 = var12;
      GridPane.CompositeSize var14 = var5 ? this.computeMinHeights(null) : this.computeMaxHeights();

      while (var21 != 0.0 && var12 == var13 && var6.size() > 0) {
         if (!var9) {
            var10 = var21 > 0.0 ? Math.floor(var21 / (double)var6.size()) : Math.ceil(var21 / (double)var6.size());
         }

         if (var10 == 0.0) {
            var10 = (double)((int)var21 % var6.size());
            if (var10 == 0.0) {
               break;
            }

            var10 = var5 ? -1.0 : 1.0;
            var9 = true;
         } else {
            Iterator var15 = var6.iterator();

            while (var15.hasNext()) {
               int var16 = (Integer)var15.next();
               double var17 = this.snapSpaceY(var14.getProportionalMinOrMaxSize(var16, var5)) - var1.getSize(var16);
               if (var5 && var17 > 0.0 || !var5 && var17 < 0.0) {
                  var17 = 0.0;
               }

               double var19 = Math.abs(var17) <= Math.abs(var10) ? var17 : var10;
               var1.addSize(var16, var19);
               var21 -= var19;
               var13 = var21 >= 0.0;
               if (Math.abs(var19) < Math.abs(var10)) {
                  var15.remove();
               }

               if (var21 == 0.0) {
                  break;
               }
            }
         }
      }

      return var21;
   }

   private double adjustColumnWidths(GridPane.CompositeSize var1, double var2) {
      assert var2 != -1.0;

      double var4 = this.snapSpaceX(this.getHgap());
      double var6 = this.snapSpaceX(this.getInsets().getLeft());
      double var8 = this.snapSpaceX(this.getInsets().getRight());
      double var10 = var4 * (double)(this.getNumberOfColumns() - 1);
      double var12 = var2 - var6 - var8;
      if (this.columnPercentTotal > 0.0) {
         double var14 = 0.0;

         for (int var16 = 0; var16 < this.columnPercentWidth.length; var16++) {
            if (this.columnPercentWidth[var16] >= 0.0) {
               double var17 = (var12 - var10) * (this.columnPercentWidth[var16] / 100.0);
               double var19 = Math.floor(var17);
               var14 += var17 - var19;
               var17 = var19;
               if (var14 >= 0.5) {
                  var17 = var19 + 1.0;
                  var14 += -1.0;
               }

               var1.setSize(var16, var17);
            }
         }
      }

      double var21 = var1.computeTotal();
      if (this.columnPercentTotal < 100.0) {
         double var22 = var2 - var6 - var8 - var21;
         if (var22 != 0.0) {
            double var18 = this.growToMultiSpanPreferredWidths(var1, var22);
            var18 = this.growOrShrinkColumnWidths(var1, Priority.ALWAYS, var18);
            var18 = this.growOrShrinkColumnWidths(var1, Priority.SOMETIMES, var18);
            var21 += var22 - var18;
         }
      }

      return var21;
   }

   private double growToMultiSpanPreferredWidths(GridPane.CompositeSize var1, double var2) {
      if (var2 <= 0.0) {
         return var2;
      } else {
         TreeSet var4 = new TreeSet();
         TreeSet var5 = new TreeSet();
         TreeSet var6 = new TreeSet();

         for (Entry var8 : var1.multiSizes()) {
            GridPane.Interval var9 = (GridPane.Interval)var8.getKey();

            for (int var10 = var9.begin; var10 < var9.end; var10++) {
               if (this.columnPercentWidth[var10] < 0.0) {
                  switch (this.columnGrow[var10]) {
                     case ALWAYS:
                        var4.add(var10);
                        break;
                     case SOMETIMES:
                        var5.add(var10);
                  }
               }
            }

            if (this.columnPercentWidth[var9.end - 1] < 0.0) {
               var6.add(var9.end - 1);
            }
         }

         double var25 = var2;

         while (var4.size() > 0 && var25 > (double)var4.size()) {
            double var26 = Math.floor(var25 / (double)var4.size());
            Iterator var11 = var4.iterator();

            while (var11.hasNext()) {
               int var12 = (Integer)var11.next();
               double var13 = this.getColumnMaxWidth(var12);
               double var15 = this.getColumnPrefWidth(var12);
               double var17 = var26;

               for (Entry var20 : var1.multiSizes()) {
                  GridPane.Interval var21 = (GridPane.Interval)var20.getKey();
                  if (var21.contains(var12)) {
                     int var22 = 0;

                     for (int var23 = var21.begin; var23 < var21.end; var23++) {
                        if (var4.contains(var23)) {
                           var22++;
                        }
                     }

                     double var53 = var1.computeTotal(var21.begin, var21.end);
                     var17 = Math.min(Math.floor(Math.max(0.0, ((Double)var20.getValue() - var53) / (double)var22)), var17);
                  }
               }

               double var39 = var1.getSize(var12);
               double var46 = var13 >= 0.0
                  ? boundedSize(0.0, var39 + var17, var13)
                  : (var13 == Double.NEGATIVE_INFINITY && var15 > 0.0 ? boundedSize(0.0, var39 + var17, var15) : var39 + var17);
               double var54 = var46 - var39;
               var25 -= var54;
               if (var54 != var17 || var54 == 0.0) {
                  var11.remove();
               }

               var1.setSize(var12, var46);
            }
         }

         while (var5.size() > 0 && var25 > (double)var5.size()) {
            double var27 = Math.floor(var25 / (double)var5.size());
            Iterator var29 = var5.iterator();

            while (var29.hasNext()) {
               int var31 = (Integer)var29.next();
               double var33 = this.getColumnMaxWidth(var31);
               double var35 = this.getColumnPrefWidth(var31);
               double var37 = var27;

               for (Entry var44 : var1.multiSizes()) {
                  GridPane.Interval var47 = (GridPane.Interval)var44.getKey();
                  if (var47.contains(var31)) {
                     int var51 = 0;

                     for (int var55 = var47.begin; var55 < var47.end; var55++) {
                        if (var5.contains(var55)) {
                           var51++;
                        }
                     }

                     double var56 = var1.computeTotal(var47.begin, var47.end);
                     var37 = Math.min(Math.floor(Math.max(0.0, ((Double)var44.getValue() - var56) / (double)var51)), var37);
                  }
               }

               double var41 = var1.getSize(var31);
               double var48 = var33 >= 0.0
                  ? boundedSize(0.0, var41 + var37, var33)
                  : (var33 == Double.NEGATIVE_INFINITY && var35 > 0.0 ? boundedSize(0.0, var41 + var37, var35) : var41 + var37);
               double var57 = var48 - var41;
               var25 -= var57;
               if (var57 != var37 || var57 == 0.0) {
                  var29.remove();
               }

               var1.setSize(var31, var48);
            }
         }

         while (var6.size() > 0 && var25 > (double)var6.size()) {
            double var28 = Math.floor(var25 / (double)var6.size());
            Iterator var30 = var6.iterator();

            while (var30.hasNext()) {
               int var32 = (Integer)var30.next();
               double var34 = this.getColumnMaxWidth(var32);
               double var36 = this.getColumnPrefWidth(var32);
               double var38 = var28;

               for (Entry var45 : var1.multiSizes()) {
                  GridPane.Interval var49 = (GridPane.Interval)var45.getKey();
                  if (var49.end - 1 == var32) {
                     double var52 = var1.computeTotal(var49.begin, var49.end);
                     var38 = Math.min(Math.max(0.0, (Double)var45.getValue() - var52), var38);
                  }
               }

               double var43 = var1.getSize(var32);
               double var50 = var34 >= 0.0
                  ? boundedSize(0.0, var43 + var38, var34)
                  : (var34 == Double.NEGATIVE_INFINITY && var36 > 0.0 ? boundedSize(0.0, var43 + var38, var36) : var43 + var38);
               double var58 = var50 - var43;
               var25 -= var58;
               if (var58 != var38 || var58 == 0.0) {
                  var30.remove();
               }

               var1.setSize(var32, var50);
            }
         }

         return var25;
      }
   }

   private double growOrShrinkColumnWidths(GridPane.CompositeSize var1, Priority var2, double var3) {
      if (var3 == 0.0) {
         return 0.0;
      } else {
         boolean var5 = var3 < 0.0;
         ArrayList var6 = new ArrayList();

         for (int var7 = 0; var7 < this.columnGrow.length; var7++) {
            if (this.columnPercentWidth[var7] < 0.0 && (var5 || this.columnGrow[var7] == var2)) {
               var6.add(var7);
            }
         }

         double var21 = var3;
         boolean var9 = false;
         double var10 = 0.0;
         boolean var12 = var3 >= 0.0;
         boolean var13 = var12;
         GridPane.CompositeSize var14 = var5 ? this.computeMinWidths(null) : this.computeMaxWidths();

         while (var21 != 0.0 && var12 == var13 && var6.size() > 0) {
            if (!var9) {
               var10 = var21 > 0.0 ? Math.floor(var21 / (double)var6.size()) : Math.ceil(var21 / (double)var6.size());
            }

            if (var10 == 0.0) {
               var10 = (double)((int)var21 % var6.size());
               if (var10 == 0.0) {
                  break;
               }

               var10 = var5 ? -1.0 : 1.0;
               var9 = true;
            } else {
               Iterator var15 = var6.iterator();

               while (var15.hasNext()) {
                  int var16 = (Integer)var15.next();
                  double var17 = this.snapSpaceX(var14.getProportionalMinOrMaxSize(var16, var5)) - var1.getSize(var16);
                  if (var5 && var17 > 0.0 || !var5 && var17 < 0.0) {
                     var17 = 0.0;
                  }

                  double var19 = Math.abs(var17) <= Math.abs(var10) ? var17 : var10;
                  var1.addSize(var16, var19);
                  var21 -= var19;
                  var13 = var21 >= 0.0;
                  if (Math.abs(var19) < Math.abs(var10)) {
                     var15.remove();
                  }

                  if (var21 == 0.0) {
                     break;
                  }
               }
            }
         }

         return var21;
      }
   }

   private void layoutGridLines(GridPane.CompositeSize var1, GridPane.CompositeSize var2, double var3, double var5, double var7, double var9) {
      if (this.isGridLinesVisible()) {
         if (!this.gridLines.getChildren().isEmpty()) {
            this.gridLines.getChildren().clear();
         }

         double var11 = this.snapSpaceX(this.getHgap());
         double var13 = this.snapSpaceY(this.getVgap());
         double var15 = var3;
         double var17 = var5;

         for (int var19 = 0; var19 <= var1.getLength(); var19++) {
            this.gridLines.getChildren().add(this.createGridLine(var15, var17, var15, var17 + var7));
            if (var19 > 0 && var19 < var1.getLength() && var11 != 0.0) {
               var15 += var11;
               this.gridLines.getChildren().add(this.createGridLine(var15, var17, var15, var17 + var7));
            }

            if (var19 < var1.getLength()) {
               var15 += var1.getSize(var19);
            }
         }

         var15 = var3;

         for (int var21 = 0; var21 <= var2.getLength(); var21++) {
            this.gridLines.getChildren().add(this.createGridLine(var15, var17, var15 + var9, var17));
            if (var21 > 0 && var21 < var2.getLength() && var13 != 0.0) {
               var17 += var13;
               this.gridLines.getChildren().add(this.createGridLine(var15, var17, var15 + var9, var17));
            }

            if (var21 < var2.getLength()) {
               var17 += var2.getSize(var21);
            }
         }
      }
   }

   private Line createGridLine(double var1, double var3, double var5, double var7) {
      Line var9 = new Line();
      var9.setStartX(var1);
      var9.setStartY(var3);
      var9.setEndX(var5);
      var9.setEndY(var7);
      var9.setStroke(GRID_LINE_COLOR);
      var9.setStrokeDashOffset(3.0);
      return var9;
   }

   @Override
   public String toString() {
      return "Grid hgap=" + this.getHgap() + ", vgap=" + this.getVgap() + ", alignment=" + this.getAlignment();
   }

   private GridPane.CompositeSize createCompositeRows(double var1) {
      return new GridPane.CompositeSize(this.getNumberOfRows(), this.rowPercentHeight, this.rowPercentTotal, this.snapSpaceY(this.getVgap()), var1);
   }

   private GridPane.CompositeSize createCompositeColumns(double var1) {
      return new GridPane.CompositeSize(this.getNumberOfColumns(), this.columnPercentWidth, this.columnPercentTotal, this.snapSpaceX(this.getHgap()), var1);
   }

   private int getNodeRowEndConvertRemaining(Node var1) {
      int var2 = getNodeRowSpan(var1);
      return var2 != Integer.MAX_VALUE ? getNodeRowIndex(var1) + var2 - 1 : this.getNumberOfRows() - 1;
   }

   private int getNodeColumnEndConvertRemaining(Node var1) {
      int var2 = getNodeColumnSpan(var1);
      return var2 != Integer.MAX_VALUE ? getNodeColumnIndex(var1) + var2 - 1 : this.getNumberOfColumns() - 1;
   }

   double[][] getGrid() {
      return this.currentHeights != null && this.currentWidths != null ? new double[][]{this.currentWidths.asArray(), this.currentHeights.asArray()} : null;
   }

   public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
      return GridPane.StyleableProperties.STYLEABLES;
   }

   @Override
   public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
      return getClassCssMetaData();
   }

   public final int getRowCount() {
      int var1 = this.getRowConstraints().size();

      for (int var2 = 0; var2 < this.getChildren().size(); var2++) {
         Node var3 = (Node)this.getChildren().get(var2);
         if (var3.isManaged()) {
            int var4 = getNodeRowIndex(var3);
            int var5 = getNodeRowEnd(var3);
            var1 = Math.max(var1, (var5 != Integer.MAX_VALUE ? var5 : var4) + 1);
         }
      }

      return var1;
   }

   public final int getColumnCount() {
      int var1 = this.getColumnConstraints().size();

      for (int var2 = 0; var2 < this.getChildren().size(); var2++) {
         Node var3 = (Node)this.getChildren().get(var2);
         if (var3.isManaged()) {
            int var4 = getNodeColumnIndex(var3);
            int var5 = getNodeColumnEnd(var3);
            var1 = Math.max(var1, (var5 != Integer.MAX_VALUE ? var5 : var4) + 1);
         }
      }

      return var1;
   }

   public final Bounds getCellBounds(int var1, int var2) {
      double var3 = this.snapSpaceX(this.getHgap());
      double var5 = this.snapSpaceY(this.getVgap());
      double var7 = this.snapSpaceY(this.getInsets().getTop());
      double var9 = this.snapSpaceX(this.getInsets().getRight());
      double var11 = this.snapSpaceY(this.getInsets().getBottom());
      double var13 = this.snapSpaceX(this.getInsets().getLeft());
      double var15 = this.snapSizeY(this.getHeight()) - (var7 + var11);
      double var17 = this.snapSizeX(this.getWidth()) - (var13 + var9);
      double[][] var21 = this.getGrid();
      double[] var19;
      double[] var20;
      if (var21 == null) {
         var20 = new double[]{0.0};
         var2 = 0;
         var19 = new double[]{0.0};
         var1 = 0;
      } else {
         var19 = var21[0];
         var20 = var21[1];
      }

      double var22 = 0.0;

      for (int var24 = 0; var24 < var20.length; var24++) {
         var22 += var20[var24];
      }

      var22 += (double)(var20.length - 1) * var5;
      double var36 = var7 + Region.computeYOffset(var15, var22, this.getAlignment().getVpos());
      double var26 = var20[var2];

      for (int var28 = 0; var28 < var2; var28++) {
         var36 += var20[var28] + var5;
      }

      double var37 = 0.0;

      for (int var30 = 0; var30 < var19.length; var30++) {
         var37 += var19[var30];
      }

      var37 += (double)(var19.length - 1) * var3;
      double var39 = var13 + Region.computeXOffset(var17, var37, this.getAlignment().getHpos());
      double var32 = var19[var1];

      for (int var34 = 0; var34 < var1; var34++) {
         var39 += var19[var34] + var3;
      }

      return new BoundingBox(var39, var36, var32, var26);
   }

   private static final class CompositeSize implements Cloneable {
      double[] singleSizes;
      private SortedMap<GridPane.Interval, Double> multiSizes;
      private BitSet preset;
      private final double[] fixedPercent;
      private final double totalFixedPercent;
      private final double gap;

      public CompositeSize(int var1, double[] var2, double var3, double var5, double var7) {
         this.singleSizes = new double[var1];
         Arrays.fill(this.singleSizes, var7);
         this.fixedPercent = var2;
         this.totalFixedPercent = var3;
         this.gap = var5;
      }

      private void setSize(int var1, double var2) {
         this.singleSizes[var1] = var2;
      }

      private void setPresetSize(int var1, double var2) {
         this.setSize(var1, var2);
         if (this.preset == null) {
            this.preset = new BitSet(this.singleSizes.length);
         }

         this.preset.set(var1);
      }

      private boolean isPreset(int var1) {
         return this.preset == null ? false : this.preset.get(var1);
      }

      private void addSize(int var1, double var2) {
         this.singleSizes[var1] = this.singleSizes[var1] + var2;
      }

      private double getSize(int var1) {
         return this.singleSizes[var1];
      }

      private void setMaxSize(int var1, double var2) {
         this.singleSizes[var1] = Math.max(this.singleSizes[var1], var2);
      }

      private void setMultiSize(int var1, int var2, double var3) {
         if (this.multiSizes == null) {
            this.multiSizes = new TreeMap<>();
         }

         GridPane.Interval var5 = new GridPane.Interval(var1, var2);
         this.multiSizes.put(var5, Double.valueOf(var3));
      }

      private Iterable<Entry<GridPane.Interval, Double>> multiSizes() {
         return (Iterable<Entry<GridPane.Interval, Double>>)(this.multiSizes == null ? Collections.EMPTY_LIST : this.multiSizes.entrySet());
      }

      private void setMaxMultiSize(int var1, int var2, double var3) {
         if (this.multiSizes == null) {
            this.multiSizes = new TreeMap<>();
         }

         GridPane.Interval var5 = new GridPane.Interval(var1, var2);
         Double var6 = this.multiSizes.get(var5);
         if (var6 == null) {
            this.multiSizes.put(var5, Double.valueOf(var3));
         } else {
            this.multiSizes.put(var5, Double.valueOf(Math.max(var3, var6)));
         }
      }

      private double getProportionalMinOrMaxSize(int var1, boolean var2) {
         double var3 = this.singleSizes[var1];
         if (!this.isPreset(var1) && this.multiSizes != null) {
            for (GridPane.Interval var6 : this.multiSizes.keySet()) {
               if (var6.contains(var1)) {
                  double var7 = this.multiSizes.get(var6) / (double)var6.size();
                  double var9 = var7;

                  for (int var11 = var6.begin; var11 < var6.end; var11++) {
                     if (var11 != var1 && (var2 ? this.singleSizes[var11] > var7 : this.singleSizes[var11] < var7)) {
                        var9 += var7 - this.singleSizes[var11];
                     }
                  }

                  var3 = var2 ? Math.max(var3, var9) : Math.min(var3, var9);
               }
            }
         }

         return var3;
      }

      private double computeTotal(int var1, int var2) {
         double var3 = this.gap * (double)(var2 - var1 - 1);

         for (int var5 = var1; var5 < var2; var5++) {
            var3 += this.singleSizes[var5];
         }

         return var3;
      }

      private double computeTotal() {
         return this.computeTotal(0, this.singleSizes.length);
      }

      private boolean allPreset(int var1, int var2) {
         if (this.preset == null) {
            return false;
         } else {
            for (int var3 = var1; var3 < var2; var3++) {
               if (!this.preset.get(var3)) {
                  return false;
               }
            }

            return true;
         }
      }

      private double computeTotalWithMultiSize() {
         double var1 = this.computeTotal();
         if (this.multiSizes != null) {
            for (Entry var4 : this.multiSizes.entrySet()) {
               GridPane.Interval var5 = (GridPane.Interval)var4.getKey();
               if (!this.allPreset(var5.begin, var5.end)) {
                  double var6 = this.computeTotal(var5.begin, var5.end);
                  if ((Double)var4.getValue() > var6) {
                     var1 += var4.getValue() - var6;
                  }
               }
            }
         }

         if (this.totalFixedPercent > 0.0) {
            double var8 = 0.0;

            for (int var9 = 0; var9 < this.fixedPercent.length; var9++) {
               if (this.fixedPercent[var9] == 0.0) {
                  var1 -= this.singleSizes[var9];
               }
            }

            for (int var10 = 0; var10 < this.fixedPercent.length; var10++) {
               if (this.fixedPercent[var10] > 0.0) {
                  var1 = Math.max(var1, this.singleSizes[var10] * (100.0 / this.fixedPercent[var10]));
               } else if (this.fixedPercent[var10] < 0.0) {
                  var8 += this.singleSizes[var10];
               }
            }

            if (this.totalFixedPercent < 100.0) {
               var1 = Math.max(var1, var8 * 100.0 / (100.0 - this.totalFixedPercent));
            }
         }

         return var1;
      }

      private int getLength() {
         return this.singleSizes.length;
      }

      @Override
      protected Object clone() {
         try {
            GridPane.CompositeSize var1 = (GridPane.CompositeSize)super.clone();
            var1.singleSizes = (double[])var1.singleSizes.clone();
            if (this.multiSizes != null) {
               var1.multiSizes = new TreeMap<>(var1.multiSizes);
            }

            return var1;
         } catch (CloneNotSupportedException var2) {
            throw new RuntimeException(var2);
         }
      }

      private double[] asArray() {
         return this.singleSizes;
      }
   }

   private static final class Interval implements Comparable<GridPane.Interval> {
      public final int begin;
      public final int end;

      public Interval(int var1, int var2) {
         this.begin = var1;
         this.end = var2;
      }

      public int compareTo(GridPane.Interval var1) {
         return this.begin != var1.begin ? this.begin - var1.begin : this.end - var1.end;
      }

      private boolean contains(int var1) {
         return this.begin <= var1 && var1 < this.end;
      }

      private int size() {
         return this.end - this.begin;
      }
   }

   private static class StyleableProperties {
      private static final CssMetaData<GridPane, Boolean> GRID_LINES_VISIBLE = new CssMetaData<GridPane, Boolean>(
         "-fx-grid-lines-visible", BooleanConverter.getInstance(), Boolean.FALSE
      ) {
         public boolean isSettable(GridPane var1) {
            return var1.gridLinesVisible == null || !var1.gridLinesVisible.isBound();
         }

         public StyleableProperty<Boolean> getStyleableProperty(GridPane var1) {
            return (StyleableProperty<Boolean>)var1.gridLinesVisibleProperty();
         }
      };
      private static final CssMetaData<GridPane, Number> HGAP = new CssMetaData<GridPane, Number>("-fx-hgap", SizeConverter.getInstance(), 0.0) {
         public boolean isSettable(GridPane var1) {
            return var1.hgap == null || !var1.hgap.isBound();
         }

         public StyleableProperty<Number> getStyleableProperty(GridPane var1) {
            return (StyleableProperty<Number>)var1.hgapProperty();
         }
      };
      private static final CssMetaData<GridPane, Pos> ALIGNMENT = new CssMetaData<GridPane, Pos>("-fx-alignment", new EnumConverter<>(Pos.class), Pos.TOP_LEFT) {
         public boolean isSettable(GridPane var1) {
            return var1.alignment == null || !var1.alignment.isBound();
         }

         public StyleableProperty<Pos> getStyleableProperty(GridPane var1) {
            return (StyleableProperty<Pos>)var1.alignmentProperty();
         }
      };
      private static final CssMetaData<GridPane, Number> VGAP = new CssMetaData<GridPane, Number>("-fx-vgap", SizeConverter.getInstance(), 0.0) {
         public boolean isSettable(GridPane var1) {
            return var1.vgap == null || !var1.vgap.isBound();
         }

         public StyleableProperty<Number> getStyleableProperty(GridPane var1) {
            return (StyleableProperty<Number>)var1.vgapProperty();
         }
      };
      private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

      static {
         ArrayList var0 = new ArrayList<>(Region.getClassCssMetaData());
         var0.add(GRID_LINES_VISIBLE);
         var0.add(HGAP);
         var0.add(ALIGNMENT);
         var0.add(VGAP);
         STYLEABLES = Collections.unmodifiableList(var0);
      }
   }
}
