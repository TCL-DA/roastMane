package org.controlsfx.control;

import impl.org.controlsfx.skin.SegmentedBarSkin;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.EnumConverter;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.Skin;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;

public class SegmentedBar<T extends SegmentedBar.Segment> extends ControlsFXControl {
   private static final String DEFAULT_STYLE = "segmented-bar";
   private static final PseudoClass VERTICAL_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("vertical");
   private static final PseudoClass HORIZONTAL_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("horizontal");
   private final ObjectProperty<Callback<T, Node>> infoNodeFactory = new SimpleObjectProperty(this, "infoNodeFactory");
   private ObjectProperty<Orientation> orientation = new StyleableObjectProperty<Orientation>(Orientation.VERTICAL) {
      protected void invalidated() {
         boolean vertical = this.get() == Orientation.VERTICAL;
         SegmentedBar.this.pseudoClassStateChanged(SegmentedBar.VERTICAL_PSEUDOCLASS_STATE, vertical);
         SegmentedBar.this.pseudoClassStateChanged(SegmentedBar.HORIZONTAL_PSEUDOCLASS_STATE, !vertical);
      }

      public CssMetaData<SegmentedBar, Orientation> getCssMetaData() {
         return SegmentedBar.StyleableProperties.ORIENTATION;
      }

      public Object getBean() {
         return SegmentedBar.this;
      }

      public String getName() {
         return "orientation";
      }
   };
   private final ObjectProperty<Callback<T, Node>> segmentViewFactory = new SimpleObjectProperty(this, "segmentViewFactory");
   private final ListProperty<T> segments = new SimpleListProperty(this, "segments", FXCollections.observableArrayList());
   private final ReadOnlyDoubleWrapper total = new ReadOnlyDoubleWrapper(this, "total");
   private final InvalidationListener sumListener = it -> this.total
         .set(this.segments.stream().collect(Collectors.summingDouble(segment -> segment.getValue())));
   private final WeakInvalidationListener weakSumListener = new WeakInvalidationListener(this.sumListener);

   public SegmentedBar() {
      this.segments.addListener(it -> this.listenToValues());
      this.listenToValues();
      this.getStyleClass().add("segmented-bar");
      this.setSegmentViewFactory(segment -> new SegmentedBar.SegmentView(segment));
      this.setInfoNodeFactory(segment -> {
         Label label = new Label("Value: " + segment.getValue());
         label.setPadding(new Insets(4.0));
         return label;
      });
   }

   protected Skin<?> createDefaultSkin() {
      return new SegmentedBarSkin<T>(this);
   }

   public String getUserAgentStylesheet() {
      return this.getUserAgentStylesheet(SegmentedBar.class, "segmentedbar.css");
   }

   public final ObjectProperty<Callback<T, Node>> infoNodeFactoryProperty() {
      return this.infoNodeFactory;
   }

   public final Callback<T, Node> getInfoNodeFactory() {
      return (Callback<T, Node>)this.infoNodeFactory.get();
   }

   public void setInfoNodeFactory(Callback<T, Node> factory) {
      this.infoNodeFactory.set(factory);
   }

   public final void setOrientation(Orientation value) {
      this.orientationProperty().set(value);
   }

   public final Orientation getOrientation() {
      return this.orientation == null ? Orientation.HORIZONTAL : (Orientation)this.orientation.get();
   }

   public final ObjectProperty<Orientation> orientationProperty() {
      return this.orientation;
   }

   public final ObjectProperty<Callback<T, Node>> segmentViewFactoryProperty() {
      return this.segmentViewFactory;
   }

   public final Callback<T, Node> getSegmentViewFactory() {
      return (Callback<T, Node>)this.segmentViewFactory.get();
   }

   public final void setSegmentViewFactory(Callback<T, Node> factory) {
      this.segmentViewFactory.set(factory);
   }

   public final ListProperty<T> segmentsProperty() {
      return this.segments;
   }

   public final ObservableList<T> getSegments() {
      return (ObservableList<T>)this.segments.get();
   }

   public void setSegments(ObservableList<T> segments) {
      this.segments.set(segments);
   }

   public final ReadOnlyDoubleProperty totalProperty() {
      return this.total.getReadOnlyProperty();
   }

   public final double getTotal() {
      return this.total.get();
   }

   private void listenToValues() {
      ((ObservableList)this.segments.get()).addListener(this.weakSumListener);
      this.getSegments().forEach(segment -> {
         segment.valueProperty().removeListener(this.weakSumListener);
         segment.valueProperty().addListener(this.weakSumListener);
      });
   }

   public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
      return SegmentedBar.StyleableProperties.STYLEABLES;
   }

   public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
      return getClassCssMetaData();
   }

   public static class Segment {
      private final StringProperty text = new SimpleStringProperty(this, "text");
      private final DoubleProperty value = new SimpleDoubleProperty(this, "value") {
         public void set(double newValue) {
            if (newValue < 0.0) {
               throw new IllegalArgumentException("segment value must be >= 0 but was " + newValue);
            } else {
               super.set(newValue);
            }
         }
      };

      public Segment(double value) {
         if (value < 0.0) {
            throw new IllegalArgumentException("value must be larger or equal to 0 but was " + value);
         } else {
            this.setValue(value);
         }
      }

      public Segment(double value, String text) {
         this(value);
         this.setText(text);
      }

      public final StringProperty textProperty() {
         return this.text;
      }

      public final void setText(String text) {
         this.text.set(text);
      }

      public final String getText() {
         return (String)this.text.get();
      }

      public final DoubleProperty valueProperty() {
         return this.value;
      }

      public final void setValue(double value) {
         this.value.set(value);
      }

      public final double getValue() {
         return this.value.get();
      }
   }

   public class SegmentView extends StackPane {
      private Label label;

      public SegmentView(T segment) {
         this.getStyleClass().add("segment-view");
         this.label = new Label();
         this.label.textProperty().bind(segment.textProperty());
         this.label.setTextOverrun(OverrunStyle.CLIP);
         StackPane.setAlignment(this.label, Pos.CENTER_LEFT);
         this.getChildren().add(this.label);
      }

      protected void layoutChildren() {
         super.layoutChildren();
         this.label.setVisible(this.label.prefWidth(-1.0) < this.getWidth() - this.getPadding().getLeft() - this.getPadding().getRight());
      }
   }

   private static class StyleableProperties {
      private static final CssMetaData<SegmentedBar, Orientation> ORIENTATION = new CssMetaData<SegmentedBar, Orientation>(
         "-fx-orientation", new EnumConverter(Orientation.class), Orientation.VERTICAL
      ) {
         public Orientation getInitialValue(SegmentedBar node) {
            return node.getOrientation();
         }

         public boolean isSettable(SegmentedBar n) {
            return n.orientation == null || !n.orientation.isBound();
         }

         public StyleableProperty<Orientation> getStyleableProperty(SegmentedBar n) {
            return (StyleableProperty<Orientation>)n.orientationProperty();
         }
      };
      private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

      static {
         List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<>(Control.getClassCssMetaData());
         styleables.add(ORIENTATION);
         STYLEABLES = Collections.unmodifiableList(styleables);
      }
   }
}
