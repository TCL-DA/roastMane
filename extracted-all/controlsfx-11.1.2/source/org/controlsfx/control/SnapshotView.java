package org.controlsfx.control;

import impl.org.controlsfx.skin.SnapshotViewSkin;
import impl.org.controlsfx.tools.rectangle.Rectangles2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class SnapshotView extends ControlsFXControl {
   public static final double MAX_SELECTION_RATIO_DIVERGENCE = 1.0E-6;
   public static final String SELECTION_CHANGING_PROPERTY_KEY = SnapshotView.class.getCanonicalName() + ".selection_changing";
   private final ObjectProperty<Node> node;
   private final ObjectProperty<Rectangle2D> selection;
   private final BooleanProperty hasSelection;
   private final BooleanProperty selectionActive;
   private final BooleanProperty selectionChanging;
   private final BooleanProperty selectionRatioFixed;
   private final DoubleProperty fixedSelectionRatio;
   private final ObjectProperty<SnapshotView.Boundary> selectionAreaBoundary;
   private final BooleanProperty selectionActivityManaged;
   private final BooleanProperty selectionMouseTransparent;
   private final ObjectProperty<SnapshotView.Boundary> unselectedAreaBoundary;
   private final ObjectProperty<Paint> selectionBorderPaint;
   private final DoubleProperty selectionBorderWidth;
   private final ObjectProperty<Paint> selectionAreaFill;
   private final ObjectProperty<Paint> unselectedAreaFill;
   private static final String DEFAULT_STYLE_CLASS = "snapshot-view";

   public SnapshotView() {
      this.getStyleClass().setAll(new String[]{"snapshot-view"});
      this.node = new SimpleObjectProperty(this, "node");
      this.selection = new SimpleObjectProperty<Rectangle2D>(this, "selection") {
         public void set(Rectangle2D selection) {
            if (!SnapshotView.this.isSelectionValid(selection)) {
               throw new IllegalArgumentException(
                  "The selection \""
                     + selection
                     + "\" is invalid. Check the comment on 'SnapshotView.selectionProperty()' for all criteria a selection must fulfill."
               );
            } else {
               super.set(selection);
            }
         }
      };
      this.hasSelection = new SimpleBooleanProperty(this, "hasSelection", false);
      this.hasSelection.bind(Bindings.and(Bindings.isNotNull(this.selection), Bindings.notEqual(Rectangle2D.EMPTY, this.selection)));
      this.selectionActive = new SimpleBooleanProperty(this, "selectionActive", false);
      this.selectionChanging = new SimpleBooleanProperty(this, "selectionChanging", false);
      this.selectionRatioFixed = new SimpleBooleanProperty(this, "selectionRatioFixed", false);
      this.fixedSelectionRatio = new SimpleDoubleProperty(this, "fixedSelectionRatio", 1.0) {
         public void set(double newValue) {
            if (newValue <= 0.0) {
               throw new IllegalArgumentException("The fixed selection ratio must be positive.");
            } else {
               super.set(newValue);
            }
         }
      };
      this.selectionAreaBoundary = createStylableObjectProperty(
         this, "selectionAreaBoundary", SnapshotView.Boundary.CONTROL, SnapshotView.Css.SELECTION_AREA_BOUNDARY
      );
      this.selectionActivityManaged = new SimpleBooleanProperty(this, "selectionActivityManaged", true);
      this.selectionMouseTransparent = new SimpleBooleanProperty(this, "selectionMouseTransparent", false);
      this.unselectedAreaBoundary = createStylableObjectProperty(
         this, "unselectedAreaBoundary", SnapshotView.Boundary.CONTROL, SnapshotView.Css.UNSELECTED_AREA_BOUNDARY
      );
      this.selectionBorderPaint = createStylableObjectProperty(this, "selectionBorderPaint", Color.WHITESMOKE, SnapshotView.Css.SELECTION_BORDER_PAINT);
      this.selectionBorderWidth = createStylableDoubleProperty(this, "selectionBorderWidth", 2.5, SnapshotView.Css.SELECTION_BORDER_WIDTH);
      this.selectionAreaFill = createStylableObjectProperty(this, "selectionAreaFill", Color.TRANSPARENT, SnapshotView.Css.SELECTION_AREA_FILL);
      this.unselectedAreaFill = createStylableObjectProperty(this, "unselectedAreaFill", new Color(0.0, 0.0, 0.0, 0.5), SnapshotView.Css.UNSELECTED_AREA_FILL);
      this.addStateUpdatingListeners();
      new SnapshotView.SelectionSizeUpdater().enableResizing();
   }

   private void addStateUpdatingListeners() {
      this.selection.addListener((o, oldValue, newValue) -> this.updateSelectionActivityState());
      this.selectionRatioFixed.addListener((o, oldValue, newValue) -> {
         boolean valueChangedToTrue = !oldValue && newValue;
         if (valueChangedToTrue) {
            this.fixSelectionRatio();
         }
      });
      this.fixedSelectionRatio.addListener((o, oldValue, newValue) -> {
         if (this.isSelectionRatioFixed()) {
            this.fixSelectionRatio();
         }
      });
      listenToProperty(this.getProperties(), SELECTION_CHANGING_PROPERTY_KEY, value -> this.selectionChanging.set(value));
   }

   private static <T> void listenToProperty(ObservableMap<Object, Object> properties, Object key, Consumer<T> processValue) {
      Objects.requireNonNull(properties, "The argument 'properties' must not be null.");
      Objects.requireNonNull(key, "The argument 'key' must not be null.");
      Objects.requireNonNull(processValue, "The argument 'processValue' must not be null.");
      MapChangeListener<Object, Object> listener = change -> {
         boolean addedForKey = change.wasAdded() && Objects.equals(key, change.getKey());
         if (addedForKey) {
            try {
               T newValue = (T)change.getValueAdded();
               processValue.accept(newValue);
            } catch (ClassCastException var6) {
            }

            properties.remove(key);
         }
      };
      properties.addListener(listener);
   }

   public SnapshotView(Node node) {
      this();
      this.setNode(node);
   }

   public Rectangle2D transformSelectionToNodeCoordinates() {
      if (!this.hasSelection()) {
         throw new IllegalStateException("The selection can not be transformed if it does not exist (check 'hasSelection()').");
      } else {
         return this.transformToNodeCoordinates(this.getSelection());
      }
   }

   public Rectangle2D transformToNodeCoordinates(Rectangle2D area) throws IllegalStateException {
      Objects.requireNonNull(area, "The argument 'area' must not be null.");
      if (this.getNode() == null) {
         throw new IllegalStateException("The selection can not be transformed if the node is null (check 'getNode()').");
      } else {
         Bounds nodeBounds = this.getNode().getBoundsInParent();
         double xOffset = nodeBounds.getMinX();
         double yOffset = nodeBounds.getMinY();
         double minX = area.getMinX() - xOffset;
         double minY = area.getMinY() - yOffset;
         return new Rectangle2D(minX, minY, area.getWidth(), area.getHeight());
      }
   }

   public WritableImage createSnapshot() throws IllegalStateException {
      if (this.getNode() == null) {
         throw new IllegalStateException("No snapshot can be created if the node is null (check 'getNode()').");
      } else if (!this.hasSelection()) {
         throw new IllegalStateException("No snapshot can be created if there is no selection (check 'hasSelection()').");
      } else {
         SnapshotParameters parameters = new SnapshotParameters();
         parameters.setViewport(this.getSelection());
         return this.createSnapshot(parameters);
      }
   }

   public WritableImage createSnapshot(SnapshotParameters parameters) throws IllegalStateException {
      Objects.requireNonNull(parameters, "The argument 'parameters' must not be null.");
      if (this.getNode() == null) {
         throw new IllegalStateException("No snapshot can be created if the node is null (check 'getNode()').");
      } else {
         return this.getNode().snapshot(parameters, null);
      }
   }

   private void updateSelectionActivityState() {
      boolean userManaged = !this.isSelectionActivityManaged();
      if (!userManaged) {
         boolean selectionActive = this.getSelection() != null && this.getSelection() != Rectangle2D.EMPTY;
         this.setSelectionActive(selectionActive);
      }
   }

   private void fixSelectionRatio() {
      boolean noSelectionToFix = this.getNode() == null || !this.hasSelection();
      if (!noSelectionToFix) {
         Rectangle2D selectionBounds = this.getSelectionBounds();
         Rectangle2D resizedSelection = Rectangles2D.fixRatioWithinBounds(this.getSelection(), this.getFixedSelectionRatio(), selectionBounds);
         this.selection.set(resizedSelection);
      }
   }

   private Rectangle2D getSelectionBounds() {
      SnapshotView.Boundary boundary = this.getSelectionAreaBoundary();
      switch (boundary) {
         case CONTROL:
            return new Rectangle2D(0.0, 0.0, this.getWidth(), this.getHeight());
         case NODE:
            return Rectangles2D.fromBounds(this.getNode().getBoundsInParent());
         default:
            throw new IllegalArgumentException("The boundary '" + boundary + "' is not fully implemented yet.");
      }
   }

   private boolean isSelectionValid(Rectangle2D selection) {
      boolean emptySelection = selection == null || selection == Rectangle2D.EMPTY;
      if (emptySelection) {
         return true;
      } else if (!valuesFinite(selection)) {
         return false;
      } else {
         return !this.inBounds(selection) ? false : this.hasCorrectRatio(selection);
      }
   }

   private static boolean valuesFinite(Rectangle2D selection) {
      return Double.isFinite(selection.getMinX())
         && Double.isFinite(selection.getMinY())
         && Double.isFinite(selection.getWidth())
         && Double.isFinite(selection.getHeight());
   }

   private boolean inBounds(Rectangle2D selection) {
      SnapshotView.Boundary boundary = this.getSelectionAreaBoundary();
      switch (boundary) {
         case CONTROL:
            return inBounds(selection, this.getBoundsInLocal());
         case NODE:
            if (this.getNode() == null) {
               return false;
            }

            return inBounds(selection, this.getNode().getBoundsInParent());
         default:
            throw new IllegalArgumentException("The boundary '" + boundary + "' is not fully implemented yet.");
      }
   }

   private static boolean inBounds(Rectangle2D selection, Bounds bounds) {
      return bounds.getMinX() <= selection.getMinX()
         && bounds.getMinY() <= selection.getMinY()
         && selection.getMaxX() <= bounds.getMaxX()
         && selection.getMaxY() <= bounds.getMaxY();
   }

   private boolean hasCorrectRatio(Rectangle2D selection) {
      if (!this.isSelectionRatioFixed()) {
         return true;
      } else {
         double ratio = selection.getWidth() / selection.getHeight();
         double ratioDivergence = Math.abs(1.0 - ratio / this.getFixedSelectionRatio());
         return ratioDivergence <= 1.0E-6;
      }
   }

   public String getUserAgentStylesheet() {
      return this.getUserAgentStylesheet(SnapshotView.class, "snapshot-view.css");
   }

   private static StyleableDoubleProperty createStylableDoubleProperty(
      final Object bean, final String name, double initialValue, final CssMetaData<? extends Styleable, Number> cssMetaData
   ) {
      return new StyleableDoubleProperty(initialValue) {
         public Object getBean() {
            return bean;
         }

         public String getName() {
            return name;
         }

         public CssMetaData<? extends Styleable, Number> getCssMetaData() {
            return cssMetaData;
         }
      };
   }

   private static <T> StyleableObjectProperty<T> createStylableObjectProperty(
      final Object bean, final String name, T initialValue, final CssMetaData<? extends Styleable, T> cssMetaData
   ) {
      return new StyleableObjectProperty<T>(initialValue) {
         public Object getBean() {
            return bean;
         }

         public String getName() {
            return name;
         }

         public CssMetaData<? extends Styleable, T> getCssMetaData() {
            return cssMetaData;
         }
      };
   }

   private static <S extends Styleable, T> CssMetaData<S, T> createCssMetaData(
      final Function<S, Property<T>> getProperty, String cssPropertyName, StyleConverter<?, T> styleConverter
   ) {
      return new CssMetaData<S, T>(cssPropertyName, styleConverter) {
         public boolean isSettable(S styleable) {
            Property<T> property = getProperty.apply(styleable);
            return property != null && !property.isBound();
         }

         public StyleableProperty<T> getStyleableProperty(S styleable) {
            return (StyleableProperty<T>)getProperty.apply(styleable);
         }
      };
   }

   public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
      return SnapshotView.Css.CSS_META_DATA;
   }

   public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
      return getClassCssMetaData();
   }

   protected Skin<?> createDefaultSkin() {
      return new SnapshotViewSkin(this);
   }

   public final ObjectProperty<Node> nodeProperty() {
      return this.node;
   }

   public final Node getNode() {
      return (Node)this.nodeProperty().get();
   }

   public final void setNode(Node node) {
      this.nodeProperty().set(node);
   }

   public final ObjectProperty<Rectangle2D> selectionProperty() {
      return this.selection;
   }

   public final Rectangle2D getSelection() {
      return (Rectangle2D)this.selectionProperty().get();
   }

   public final void setSelection(Rectangle2D selection) {
      this.selectionProperty().set(selection);
   }

   public final void setSelection(double upperLeftX, double upperLeftY, double width, double height) {
      this.selectionProperty().set(new Rectangle2D(upperLeftX, upperLeftY, width, height));
   }

   public final ReadOnlyBooleanProperty hasSelectionProperty() {
      return this.hasSelection;
   }

   public final boolean hasSelection() {
      return this.hasSelectionProperty().get();
   }

   public final BooleanProperty selectionActiveProperty() {
      return this.selectionActive;
   }

   public final boolean isSelectionActive() {
      return this.selectionActiveProperty().get();
   }

   public final void setSelectionActive(boolean selectionActive) {
      this.selectionActiveProperty().set(selectionActive);
   }

   public final ReadOnlyBooleanProperty selectionChangingProperty() {
      return this.selectionChanging;
   }

   public final boolean isSelectionChanging() {
      return this.selectionChangingProperty().get();
   }

   public final BooleanProperty selectionRatioFixedProperty() {
      return this.selectionRatioFixed;
   }

   public final boolean isSelectionRatioFixed() {
      return this.selectionRatioFixedProperty().get();
   }

   public final void setSelectionRatioFixed(boolean selectionRatioFixed) {
      this.selectionRatioFixedProperty().set(selectionRatioFixed);
   }

   public final DoubleProperty fixedSelectionRatioProperty() {
      return this.fixedSelectionRatio;
   }

   public final double getFixedSelectionRatio() {
      return this.fixedSelectionRatioProperty().get();
   }

   public final void setFixedSelectionRatio(double fixedSelectionRatio) {
      this.fixedSelectionRatioProperty().set(fixedSelectionRatio);
   }

   public final ObjectProperty<SnapshotView.Boundary> selectionAreaBoundaryProperty() {
      return this.selectionAreaBoundary;
   }

   public final SnapshotView.Boundary getSelectionAreaBoundary() {
      return (SnapshotView.Boundary)this.selectionAreaBoundaryProperty().get();
   }

   public final void setSelectionAreaBoundary(SnapshotView.Boundary selectionAreaBoundary) {
      this.selectionAreaBoundaryProperty().set(selectionAreaBoundary);
   }

   public final BooleanProperty selectionActivityManagedProperty() {
      return this.selectionActivityManaged;
   }

   public final boolean isSelectionActivityManaged() {
      return this.selectionActivityManagedProperty().get();
   }

   public final void setSelectionActivityManaged(boolean selectionActivityManaged) {
      this.selectionActivityManagedProperty().set(selectionActivityManaged);
   }

   public final BooleanProperty selectionMouseTransparentProperty() {
      return this.selectionMouseTransparent;
   }

   public final boolean isSelectionMouseTransparent() {
      return this.selectionMouseTransparentProperty().get();
   }

   public final void setSelectionMouseTransparent(boolean selectionMouseTransparent) {
      this.selectionMouseTransparentProperty().set(selectionMouseTransparent);
   }

   public final ObjectProperty<SnapshotView.Boundary> unselectedAreaBoundaryProperty() {
      return this.unselectedAreaBoundary;
   }

   public final SnapshotView.Boundary getUnselectedAreaBoundary() {
      return (SnapshotView.Boundary)this.unselectedAreaBoundaryProperty().get();
   }

   public final void setUnselectedAreaBoundary(SnapshotView.Boundary unselectedAreaBoundary) {
      this.unselectedAreaBoundaryProperty().set(unselectedAreaBoundary);
   }

   public final ObjectProperty<Paint> selectionBorderPaintProperty() {
      return this.selectionBorderPaint;
   }

   public final Paint getSelectionBorderPaint() {
      return (Paint)this.selectionBorderPaintProperty().get();
   }

   public final void setSelectionBorderPaint(Paint selectionBorderPaint) {
      this.selectionBorderPaintProperty().set(selectionBorderPaint);
   }

   public final DoubleProperty selectionBorderWidthProperty() {
      return this.selectionBorderWidth;
   }

   public final double getSelectionBorderWidth() {
      return this.selectionBorderWidthProperty().get();
   }

   public final void setSelectionBorderWidth(double selectionBorderWidth) {
      this.selectionBorderWidthProperty().set(selectionBorderWidth);
   }

   public final ObjectProperty<Paint> selectionAreaFillProperty() {
      return this.selectionAreaFill;
   }

   public final Paint getSelectionAreaFill() {
      return (Paint)this.selectionAreaFillProperty().get();
   }

   public final void setSelectionAreaFill(Paint selectionAreaFill) {
      this.selectionAreaFillProperty().set(selectionAreaFill);
   }

   public final ObjectProperty<Paint> unselectedAreaFillProperty() {
      return this.unselectedAreaFill;
   }

   public final Paint getUnselectedAreaFill() {
      return (Paint)this.unselectedAreaFillProperty().get();
   }

   public final void setUnselectedAreaFill(Paint unselectedAreaFill) {
      this.unselectedAreaFillProperty().set(unselectedAreaFill);
   }

   public static enum Boundary {
      CONTROL,
      NODE;
   }

   private static class Css {
      public static final CssMetaData<SnapshotView, SnapshotView.Boundary> SELECTION_AREA_BOUNDARY = SnapshotView.createCssMetaData(
         snapshotView -> snapshotView.selectionAreaBoundary, "-fx-selection-area-boundary", StyleConverter.getEnumConverter(SnapshotView.Boundary.class)
      );
      public static final CssMetaData<SnapshotView, SnapshotView.Boundary> UNSELECTED_AREA_BOUNDARY = SnapshotView.createCssMetaData(
         snapshotView -> snapshotView.unselectedAreaBoundary, "-fx-unselected-area-boundary", StyleConverter.getEnumConverter(SnapshotView.Boundary.class)
      );
      public static final CssMetaData<SnapshotView, Paint> SELECTION_BORDER_PAINT = SnapshotView.createCssMetaData(
         snapshotView -> snapshotView.selectionBorderPaint, "-fx-selection-border-paint", StyleConverter.getPaintConverter()
      );
      public static final CssMetaData<SnapshotView, Number> SELECTION_BORDER_WIDTH = SnapshotView.createCssMetaData(
         snapshotView -> snapshotView.selectionBorderWidth, "-fx-selection-border-width", StyleConverter.getSizeConverter()
      );
      public static final CssMetaData<SnapshotView, Paint> SELECTION_AREA_FILL = SnapshotView.createCssMetaData(
         snapshotView -> snapshotView.selectionAreaFill, "-fx-selection-area-fill", StyleConverter.getPaintConverter()
      );
      public static final CssMetaData<SnapshotView, Paint> UNSELECTED_AREA_FILL = SnapshotView.createCssMetaData(
         snapshotView -> snapshotView.unselectedAreaFill, "-fx-unselected-area-fill", StyleConverter.getPaintConverter()
      );
      public static final List<CssMetaData<? extends Styleable, ?>> CSS_META_DATA;

      static {
         List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<>(Control.getClassCssMetaData());
         styleables.add(SELECTION_AREA_BOUNDARY);
         styleables.add(UNSELECTED_AREA_BOUNDARY);
         styleables.add(SELECTION_BORDER_PAINT);
         styleables.add(SELECTION_BORDER_WIDTH);
         styleables.add(SELECTION_AREA_FILL);
         styleables.add(UNSELECTED_AREA_FILL);
         CSS_META_DATA = Collections.unmodifiableList(styleables);
      }
   }

   private class SelectionSizeUpdater {
      private final ChangeListener<Number> resizeSelectionToNewControlWidthListener = this::resizeSelectionToNewControlWidth;
      private final ChangeListener<Number> resizeSelectionToNewControlHeightListener = this::resizeSelectionToNewControlHeight;
      private final ChangeListener<Node> updateSelectionToNodeListener = this::updateSelectionToNewNode;
      private final ChangeListener<Bounds> resizeSelectionToNewNodeBoundsListener = this::resizeSelectionToNewNodeBounds;

      public SelectionSizeUpdater() {
      }

      public void enableResizing() {
         this.enableResizingForBoundary(SnapshotView.this.getSelectionAreaBoundary());
         SnapshotView.this.selectionAreaBoundary.addListener((o, oldBoundary, newBoundary) -> this.enableResizingForBoundary(newBoundary));
      }

      private void enableResizingForBoundary(SnapshotView.Boundary boundary) {
         switch (boundary) {
            case CONTROL:
               this.enableResizingForControl();
               break;
            case NODE:
               this.enableResizingForNode();
               break;
            default:
               throw new IllegalArgumentException("The boundary '" + boundary + "' is not fully implemented yet.");
         }
      }

      private void enableResizingForControl() {
         SnapshotView.this.node.removeListener(this.updateSelectionToNodeListener);
         if (SnapshotView.this.getNode() != null) {
            SnapshotView.this.getNode().boundsInParentProperty().removeListener(this.resizeSelectionToNewNodeBoundsListener);
         }

         SnapshotView.this.widthProperty().addListener(this.resizeSelectionToNewControlWidthListener);
         SnapshotView.this.heightProperty().addListener(this.resizeSelectionToNewControlHeightListener);
         this.resizeSelectionFromNodeToControl();
      }

      private void enableResizingForNode() {
         SnapshotView.this.widthProperty().removeListener(this.resizeSelectionToNewControlWidthListener);
         SnapshotView.this.heightProperty().removeListener(this.resizeSelectionToNewControlHeightListener);
         if (SnapshotView.this.getNode() != null) {
            SnapshotView.this.getNode().boundsInParentProperty().addListener(this.resizeSelectionToNewNodeBoundsListener);
         }

         SnapshotView.this.node.addListener(this.updateSelectionToNodeListener);
         this.resizeSelectionFromControlToNode();
      }

      private void resizeSelectionFromNodeToControl() {
         if (SnapshotView.this.getNode() == null) {
            SnapshotView.this.setSelection(null);
         } else {
            Rectangle2D controlBounds = new Rectangle2D(0.0, 0.0, SnapshotView.this.getWidth(), SnapshotView.this.getHeight());
            Rectangle2D nodeBounds = Rectangles2D.fromBounds(SnapshotView.this.getNode().getBoundsInParent());
            this.resizeSelectionToNewBounds(nodeBounds, controlBounds);
         }
      }

      private void resizeSelectionToNewControlWidth(ObservableValue<? extends Number> o, Number oldWidth, Number newWidth) {
         Rectangle2D oldBounds = new Rectangle2D(0.0, 0.0, oldWidth.doubleValue(), SnapshotView.this.getHeight());
         Rectangle2D newBounds = new Rectangle2D(0.0, 0.0, newWidth.doubleValue(), SnapshotView.this.getHeight());
         this.resizeSelectionToNewBounds(oldBounds, newBounds);
      }

      private void resizeSelectionToNewControlHeight(ObservableValue<? extends Number> o, Number oldHeight, Number newHeight) {
         Rectangle2D oldBounds = new Rectangle2D(0.0, 0.0, SnapshotView.this.getWidth(), oldHeight.doubleValue());
         Rectangle2D newBounds = new Rectangle2D(0.0, 0.0, SnapshotView.this.getWidth(), newHeight.doubleValue());
         this.resizeSelectionToNewBounds(oldBounds, newBounds);
      }

      private void resizeSelectionFromControlToNode() {
         if (SnapshotView.this.getNode() == null) {
            SnapshotView.this.setSelection(null);
         } else {
            Rectangle2D controlBounds = new Rectangle2D(0.0, 0.0, SnapshotView.this.getWidth(), SnapshotView.this.getHeight());
            Rectangle2D nodeBounds = Rectangles2D.fromBounds(SnapshotView.this.getNode().getBoundsInParent());
            this.resizeSelectionToNewBounds(controlBounds, nodeBounds);
         }
      }

      private void updateSelectionToNewNode(ObservableValue<? extends Node> o, Node oldNode, Node newNode) {
         if (oldNode != null) {
            oldNode.boundsInParentProperty().removeListener(this.resizeSelectionToNewNodeBoundsListener);
         }

         if (newNode != null) {
            newNode.boundsInParentProperty().addListener(this.resizeSelectionToNewNodeBoundsListener);
         }

         if (oldNode != null && newNode != null) {
            this.resizeSelectionToNewNodeBounds(null, oldNode.getBoundsInParent(), newNode.getBoundsInParent());
         } else {
            SnapshotView.this.setSelection(null);
         }
      }

      private void resizeSelectionToNewNodeBounds(ObservableValue<? extends Bounds> o, Bounds oldBounds, Bounds newBounds) {
         this.resizeSelectionToNewBounds(Rectangles2D.fromBounds(oldBounds), Rectangles2D.fromBounds(newBounds));
      }

      private void resizeSelectionToNewBounds(Rectangle2D oldBounds, Rectangle2D newBounds) {
         if (SnapshotView.this.hasSelection()) {
            Rectangle2D newSelection = this.transformSelectionToNewBounds(SnapshotView.this.getSelection(), oldBounds, newBounds);
            if (SnapshotView.this.isSelectionValid(newSelection)) {
               SnapshotView.this.setSelection(newSelection);
            } else {
               SnapshotView.this.setSelection(null);
            }
         }
      }

      private Rectangle2D transformSelectionToNewBounds(Rectangle2D oldSelection, Rectangle2D oldBounds, Rectangle2D newBounds) {
         Point2D newSelectionCenter = this.computeNewSelectionCenter(oldSelection, oldBounds, newBounds);
         double widthRatio = newBounds.getWidth() / oldBounds.getWidth();
         double heightRatio = newBounds.getHeight() / oldBounds.getHeight();
         if (SnapshotView.this.isSelectionRatioFixed()) {
            double newArea = oldSelection.getWidth() * widthRatio * oldSelection.getHeight() * heightRatio;
            double ratio = SnapshotView.this.getFixedSelectionRatio();
            return Rectangles2D.forCenterAndAreaAndRatioWithinBounds(newSelectionCenter, newArea, ratio, newBounds);
         } else {
            double newWidth = oldSelection.getWidth() * widthRatio;
            double newHeight = oldSelection.getHeight() * heightRatio;
            return Rectangles2D.forCenterAndSize(newSelectionCenter, newWidth, newHeight);
         }
      }

      private Point2D computeNewSelectionCenter(Rectangle2D oldSelection, Rectangle2D oldBounds, Rectangle2D newBounds) {
         Point2D oldSelectionCenter = Rectangles2D.getCenterPoint(oldSelection);
         Point2D oldBoundsCenter = Rectangles2D.getCenterPoint(oldBounds);
         Point2D oldSelectionCenterOffset = oldSelectionCenter.subtract(oldBoundsCenter);
         double widthRatio = newBounds.getWidth() / oldBounds.getWidth();
         double heightRatio = newBounds.getHeight() / oldBounds.getHeight();
         Point2D newSelectionCenterOffset = new Point2D(oldSelectionCenterOffset.getX() * widthRatio, oldSelectionCenterOffset.getY() * heightRatio);
         Point2D newBoundsCenter = Rectangles2D.getCenterPoint(newBounds);
         return newBoundsCenter.add(newSelectionCenterOffset);
      }
   }
}
