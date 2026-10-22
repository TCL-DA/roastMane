package com.cropster.roastingintelligence.ui.components;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.util.Icon;
import com.cropster.roastingintelligence.ui.util.PlatformScheduler;
import com.cropster.roastingintelligence.ui.util.Styles;
import com.cropster.roastingintelligence.ui.util.Validators;
import com.cropster.util.model.Unit;
import java.awt.Color;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.cropster.roastingintelligence.AppModel;
import org.cropster.roastingintelligence.measurement.MeasurementReaderWriter;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.model.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ControlSlider extends VBox implements Initializable {
   private static final Logger LOGGER = LoggerFactory.getLogger(ControlSlider.class);
   private String pattern = "%d%%";
   @FXML
   private Slider slider;
   @FXML
   private VBox sliderButtonWrapperLeft;
   @FXML
   private VBox sliderButtonWrapperRight;
   @FXML
   private HBox buttonContainer;
   @FXML
   private Button plusButton;
   @FXML
   private Button minusButton;
   @FXML
   private Label titleLabel;
   @FXML
   private VBox controlPanel;
   @FXML
   private ToggleSwitch autoModeToggle;
   @FXML
   private MeasureEditor valueEditor;
   @FXML
   private Label icon;
   @FXML
   private StackPane stackPanel;
   @FXML
   private BorderPane sliderContainer;
   @FXML
   private Label thumbValue;
   private final BooleanProperty configured = new SimpleBooleanProperty(false);
   private final BooleanProperty displayable = new SimpleBooleanProperty(false);
   private MeasurementReaderWriter measurementReaderWriter;
   private final StringProperty measurement = new SimpleStringProperty();
   private final AtomicBoolean writeToDeviceIsPending = new AtomicBoolean(false);
   private final AtomicBoolean isUserInput = new AtomicBoolean(true);
   private BigDecimal pendingValueToWrite = null;
   private final StringProperty valueAsString = new SimpleStringProperty();
   private final PauseTransition userInputDebouncePause = new PauseTransition(Duration.seconds(1.0));
   private final PauseTransition resetPause = new PauseTransition(Duration.millis(500.0));
   private final List<SliderButton> sliderButtons = new ArrayList<>();
   private final BooleanProperty mousePressedOnThumb = new SimpleBooleanProperty(false);
   private final BooleanProperty mouseInsideThumb = new SimpleBooleanProperty(false);
   private final BooleanProperty mouseScrollingSlider = new SimpleBooleanProperty(false);
   private Unit sourceUnit;
   private ControlSliderToggleButton toggle;
   private final EventHandler<? super ScrollEvent> scrollHandler = e -> {
      if (e.isShortcutDown()) {
         this.slider.setValue(this.slider.getValue() - e.getDeltaY() / e.getMultiplierY());
         if (!this.slider.isFocused()) {
            this.slider.requestFocus();
         }
      }
   };

   private static Region createElasticSpacer() {
      Region spacer = new Region();
      VBox.setVgrow(spacer, Priority.ALWAYS);
      spacer.setPadding(Styles.INSETS_EMPTY);
      spacer.setMinHeight(0.0);
      spacer.setMaxHeight(Double.MAX_VALUE);
      spacer.setSnapToPixel(false);
      return spacer;
   }

   private static Region createRigidSpacer(SliderButton sliderButton) {
      Region spacer = new Region();
      VBox.setVgrow(spacer, Priority.NEVER);
      spacer.setPadding(Styles.INSETS_EMPTY);
      spacer.setSnapToPixel(false);
      spacer.minHeightProperty().bind(sliderButton.heightProperty().divide(2));
      spacer.maxHeightProperty().bind(sliderButton.heightProperty().divide(2));
      return spacer;
   }

   public ControlSlider() {
      JavaFxUtil.loadComponent(this);
   }

   public MeasurementReaderWriter getSource() {
      return this.measurementReaderWriter;
   }

   public void setSource(MeasurementReaderWriter source, String measurement, Unit unit) {
      if (this.measurementReaderWriter != null) {
         throw new IllegalStateException("Control already initialized!");
      } else {
         this.measurementReaderWriter = source;
         this.measurement.set(measurement);
         this.pattern = "%d" + (Unit.PERCENT.equals(unit) ? "" : " ") + org.cropster.roastingintelligence.util.Messages.m_(unit).replace("%", "%%");
         Double minValue = this.measurementReaderWriter.getMinValue(this.getMeasurement());
         Double maxValue = this.measurementReaderWriter.getMaxValue(this.getMeasurement());
         this.setUnit(unit);
         this.sourceUnit = this.measurementReaderWriter.getFactory().getUnit(this.getMeasurement());
         if (unit.isCompatible(Unit.CELSIUS) && !unit.equals(this.sourceUnit)) {
            minValue = unit.from(BigDecimal.valueOf(minValue), this.sourceUnit).doubleValue();
            maxValue = unit.from(BigDecimal.valueOf(maxValue), this.sourceUnit).doubleValue();
         }

         this.initSliderAndEditor((int)Math.round(minValue), (int)Math.round(maxValue));
         this.slider.valueProperty().addListener((s, o, n) -> {
            this.writeToDevice(n);
            this.highlightButtons(n);
         });
         String var7 = this.getMeasurement();

         String titleText = switch (var7) {
            case "setTemperatureControl" -> org.cropster.roastingintelligence.util.Messages.m_("setTemperatureControl");
            case "customControl" -> {
               MeasurementSourceFactory sourceFactory = this.measurementReaderWriter.getFactory();
               yield sourceFactory != null
                  ? org.cropster.roastingintelligence.util.Messages.m_("custom", (Class<? extends MeasurementSourceFactory>)sourceFactory.getClass())
                  : org.cropster.roastingintelligence.util.Messages.m_("custom");
            }
            default -> org.cropster.roastingintelligence.util.Messages.m_(this.getMeasurement().replace("Control", ""));
         };
         this.titleLabel.setText(titleText);
         this.titleLabel.setTooltip(new Tooltip(titleText));
         this.titleLabel.disableProperty().bind(this.controlPanel.disabledProperty());
         this.icon.setGraphic(this.createIconGraphics("icon--muted"));
         this.initButtons();
      }
   }

   public Node createIconGraphics(String styleClass) {
      String m = this.getMeasurement();
      if (m != null) {
         if (m.startsWith("gas") || "customControl".equals(m)) {
            return Icon.GAS.load(styleClass);
         } else if ("setTemperatureControl".equals(m)) {
            return Icon.TEMPERATURE.load(styleClass);
         } else {
            return m.startsWith("drumSpeed") ? Icon.DRUM_SPEED.load(styleClass) : Icon.AIRFLOW.load(styleClass);
         }
      } else {
         return null;
      }
   }

   public String getMeasurement() {
      return (String)this.measurement.get();
   }

   public StringProperty measurementProperty() {
      return this.measurement;
   }

   public void initialize(URL location, ResourceBundle resources) {
      this.managedProperty().bind(this.visibleProperty());
      this.plusButton.setGraphic(Icon.ADD.load());
      this.minusButton.setGraphic(Icon.REMOVE.load());
      this.valueEditor.getInput().getStyleClass().add("control-slider__input");
      this.valueEditor.getInput().setAlignment(Pos.CENTER);
      this.valueEditor.getInput().setOnAction(e -> this.slider.requestFocus());
      this.valueEditor.valueProperty().bindBidirectional(this.slider.valueProperty());
      this.icon.getStyleClass().add("control-slider__icon");
      this.valueAsString
         .bind(Bindings.createStringBinding(() -> String.format(this.pattern, (int)this.slider.getValue()), new Observable[]{this.slider.valueProperty()}));
      this.slider.valueProperty().addListener((s, o, n) -> {
         if (n != null) {
            double v = n.doubleValue();
            this.plusButton.setDisable(v == this.slider.getMax());
            this.minusButton.setDisable(v == this.slider.getMin());
         }
      });
      this.thumbValue.textProperty().bind(this.valueEditor.getInput().textProperty());
      this.thumbValue.visibleProperty().bind(this.mouseInsideThumb.or(this.mousePressedOnThumb).or(this.mouseScrollingSlider));
      this.slider.addEventFilter(ScrollEvent.SCROLL_STARTED, e -> this.mouseScrollingSlider.set(e.isShortcutDown()));
      this.slider.addEventFilter(ScrollEvent.SCROLL_FINISHED, e -> this.mouseScrollingSlider.set(false));
      this.slider.skinProperty().addListener((s, o, n) -> {
         if (n != null) {
            Region thumb = (Region)this.slider.getSkin().getNode().lookup(".thumb");
            thumb.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> this.mousePressedOnThumb.set(true));
            thumb.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> this.mousePressedOnThumb.set(false));
            thumb.addEventFilter(MouseEvent.MOUSE_ENTERED, e -> this.mouseInsideThumb.set(true));
            thumb.addEventFilter(MouseEvent.MOUSE_EXITED, e -> this.mouseInsideThumb.set(false));
            this.thumbValue.translateXProperty().bind(Bindings.createDoubleBinding(() -> {
               Bounds sBounds = this.stackPanel.sceneToLocal(thumb.localToScene(thumb.getBoundsInLocal()));
               return sBounds.getCenterX() - this.thumbValue.getPrefWidth() / 2.0 - 4.0;
            }, new Observable[]{thumb.layoutXProperty(), thumb.layoutYProperty()}));
            this.thumbValue.translateYProperty().bind(Bindings.createDoubleBinding(() -> {
               Bounds sBounds = this.stackPanel.sceneToLocal(thumb.localToScene(thumb.getBoundsInLocal()));
               return sBounds.getMinY() - this.thumbValue.getPrefHeight() - 8.0;
            }, new Observable[]{thumb.layoutXProperty(), thumb.layoutYProperty()}));
         }
      });
   }

   private SliderButton createSliderButton(int value) {
      String buttonLabel = String.valueOf(value);
      SliderButton sliderButton = new SliderButton(value);
      sliderButton.getButton().setText(buttonLabel);
      sliderButton.getButton().setOnAction(mouseEvent -> this.slider.setValue((double)value));
      if (LOGGER.isDebugEnabled()) {
         sliderButton.addEventFilter(
            ActionEvent.ACTION, e -> LOGGER.debug("Fired {} button {}", this.measurement.get(), ((SliderButton)e.getSource()).getValue())
         );
      }

      sliderButton.setSnapToPixel(false);
      return sliderButton;
   }

   public boolean isFullSized() {
      Integer rowSpan = GridPane.getRowSpan(this);
      return rowSpan == null || Integer.valueOf(2).equals(rowSpan);
   }

   Set<Pair<Integer, Integer>> findDivisors(int range) {
      Set<Pair<Integer, Integer>> divisors = new HashSet<>();

      for (int i = 1; (double)i <= Math.sqrt((double)range); i++) {
         if (range % i == 0) {
            Pair<Integer, Integer> divisor = new ImmutablePair(i, range / i);
            divisors.add(divisor);
            Pair<Integer, Integer> divisorReversed = new ImmutablePair((Integer)divisor.getRight(), (Integer)divisor.getLeft());
            divisors.add(divisorReversed);
         }
      }

      return divisors;
   }

   public Integer findStepSize(int range, int minButtons, int maxButtons, int bestButtons) {
      Set<Pair<Integer, Integer>> divisors = this.findDivisors(range);
      Comparator<Pair<Integer, Integer>> comparator = (p1, p2) -> {
         boolean p1DivisibleBy5 = (Integer)p1.getRight() % 5 == 0;
         boolean p2DivisibleBy5 = (Integer)p2.getRight() % 5 == 0;
         if (p1DivisibleBy5 && !p2DivisibleBy5) {
            return -1;
         } else if (!p1DivisibleBy5 && p2DivisibleBy5) {
            return 1;
         } else {
            boolean p1DivisibleBy2 = (Integer)p1.getLeft() % 2 == 0;
            boolean p2DivisibleBy2 = (Integer)p2.getLeft() % 2 == 0;
            if (p1DivisibleBy2 && !p2DivisibleBy2) {
               return -1;
            } else if (!p1DivisibleBy2 && p2DivisibleBy2) {
               return 1;
            } else {
               int p1Distance = Math.abs((Integer)p1.getLeft() - bestButtons);
               int p2Distance = Math.abs((Integer)p2.getLeft() - bestButtons);
               return p1Distance - p2Distance;
            }
         }
      };
      SortedSet<Pair<Integer, Integer>> sortedDivisors = new TreeSet<>(comparator);
      sortedDivisors.addAll(divisors);
      return sortedDivisors.stream()
         .filter(p -> (Integer)p.getLeft() >= minButtons && (Integer)p.getLeft() <= maxButtons)
         .<Integer>map(Pair::getRight)
         .findFirst()
         .orElse(null);
   }

   private void initSliderAndEditor(int min, int max) {
      this.slider.setDisable(false);
      this.slider.setMin((double)min);
      this.slider.setMax((double)max);
      this.slider.setMajorTickUnit(1.0);
      this.slider.setMinorTickCount(0);
      this.slider.setBlockIncrement(1.0);
      this.slider.setSnapToTicks(true);
      this.slider.setOnScroll(this.scrollHandler);
      if (LOGGER.isDebugEnabled()) {
         this.slider.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> LOGGER.debug("Clicked {} slider", this.measurement.get()));
         this.slider.addEventFilter(KeyEvent.KEY_PRESSED, e -> LOGGER.debug("Pressed key {} on {} slider", e.getCode(), this.measurement.get()));
      }

      Validators.setValidator(this.valueEditor, min, Integer.valueOf(max));
      this.valueEditor.setValue(this.slider.getValue());
      this.setOnScroll(this.scrollHandler);
   }

   public void initButtons() {
      this.sliderButtonWrapperLeft.getChildren().clear();
      this.sliderButtonWrapperRight.getChildren().clear();
      this.sliderButtons.clear();
      int min = (int)this.slider.getMin();
      int max = (int)this.slider.getMax();
      int range = max - min;
      int maxButtons = this.isFullSized() ? 26 : 12;
      int bestButtons = this.isFullSized() ? 20 : 10;
      int minButtons = this.isFullSized() ? 5 : 4;
      Integer stepSize = this.findStepSize(range, minButtons, maxButtons, bestButtons);
      double diff = stepSize != null ? (double)stepSize.intValue() : (double)range / (double)bestButtons;
      boolean buttonsLeftAndRight = (double)range / diff % 2.0 == 0.0;
      Map<Integer, NodeOrientation> sliderButtonValues = new LinkedHashMap<>();

      for (int i = 0; i <= (int)((double)(max - min) / diff); i++) {
         int value = (int)Math.round((double)max - (double)i * diff);
         sliderButtonValues.put(value, buttonsLeftAndRight && i % 2 != 0 ? NodeOrientation.RIGHT_TO_LEFT : NodeOrientation.LEFT_TO_RIGHT);
      }

      int i = 0;

      for (Entry<Integer, NodeOrientation> e : sliderButtonValues.entrySet()) {
         int value = e.getKey();
         SliderButton sliderButton = this.createSliderButton(value);
         VBox.setVgrow(sliderButton, Priority.NEVER);
         if (NodeOrientation.RIGHT_TO_LEFT.equals(e.getValue())) {
            sliderButton.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
            sliderButton.getStyleClass().add("nodeorientation-rtl");
         }

         this.sliderButtons.add(sliderButton);
         if (NodeOrientation.RIGHT_TO_LEFT.equals(e.getValue())) {
            this.sliderButtonWrapperRight.getChildren().add(sliderButton);
         } else {
            this.sliderButtonWrapperLeft.getChildren().add(sliderButton);
         }

         if (i == 0) {
            this.sliderButtonWrapperRight.getChildren().add(createRigidSpacer(sliderButton));
         }

         if (i < sliderButtonValues.size() - 1) {
            this.sliderButtonWrapperLeft.getChildren().add(createElasticSpacer());
            this.sliderButtonWrapperRight.getChildren().add(createElasticSpacer());
         }

         if (i == sliderButtonValues.size() - 1) {
            this.sliderButtonWrapperRight.getChildren().add(createRigidSpacer(sliderButton));
         }

         i++;
      }

      this.sliderButtonWrapperRight.prefWidthProperty().bind(this.sliderButtonWrapperLeft.widthProperty());
      this.setMinHeight(-1.0);
      this.plusButton.setOnAction(mouseEvent -> this.inc());
      this.minusButton.setOnAction(mouseEvent -> this.dec());
      if (LOGGER.isDebugEnabled()) {
         this.plusButton.addEventFilter(ActionEvent.ACTION, ex -> LOGGER.debug("Fired {} button {}", this.measurement.get(), this.plusButton.getText()));
         this.minusButton.addEventFilter(ActionEvent.ACTION, ex -> LOGGER.debug("Fired {} button {}", this.measurement.get(), this.minusButton.getText()));
      }
   }

   private void highlightButtons(Number newValue) {
      String selectedClass = "control-slider__btn-wrapper--selected";

      for (SliderButton sliderButton : this.sliderButtons) {
         if (sliderButton.getValue() == newValue.intValue()) {
            if (!sliderButton.getStyleClass().contains(selectedClass)) {
               sliderButton.getStyleClass().add(selectedClass);
            }
         } else {
            sliderButton.getStyleClass().remove(selectedClass);
         }
      }
   }

   public void inc() {
      this.slider.setValue(this.slider.getValue() + 1.0);
   }

   public void dec() {
      this.slider.setValue(this.slider.getValue() - 1.0);
   }

   public void writeToDevice(Number newValue) {
      if (this.isUserInput.getAndSet(true)) {
         double newDoubleValue = newValue.doubleValue();
         Double newValidValue = this.measurementReaderWriter.getValidControlValue(this.getMeasurement(), newDoubleValue);
         if (newValidValue == null) {
            throw new IllegalStateException("null is not a valid value");
         } else {
            this.resetPause.stop();
            if (newValidValue != newDoubleValue) {
               this.resetPause.setOnFinished(e -> this.setSliderWithoutDeviceWrite(newValidValue));
               this.resetPause.playFromStart();
            }

            this.writeToDeviceIsPending.set(true);
            this.pendingValueToWrite = round(this.sourceUnit.from(BigDecimal.valueOf(newValidValue), this.getUnit()));
            this.userInputDebouncePause.setOnFinished(actionEvent -> {
               if (this.measurementReaderWriter == null) {
                  LOGGER.error("Cannot write as source is not set for {}", this.measurement.get());
               } else {
                  double value = this.pendingValueToWrite.doubleValue();
                  PlatformScheduler.executeBackgroundTask(() -> {
                     LOGGER.info("Writing {}: {}", this.measurement.get(), value);
                     this.measurementReaderWriter.setValue((String)this.measurement.get(), value);
                  });
               }
            });
            this.userInputDebouncePause.playFromStart();
         }
      }
   }

   public static BigDecimal toBigDecimal(double value) {
      return new BigDecimal(value).setScale(0, RoundingMode.HALF_UP);
   }

   public static BigDecimal round(BigDecimal value) {
      return value.setScale(0, RoundingMode.HALF_UP);
   }

   public void setSliderWithoutDeviceWrite(double value) {
      this.isUserInput.set(false);
      this.slider.setValue(value);
      this.isUserInput.set(true);
   }

   private boolean isVisible(AppModel appModel) {
      boolean visible = appModel.getActiveChannels().stream().anyMatch(c -> c.getName().equals(this.measurement.get()));
      return this.getSource() != null && visible ? visible & this.getSource().getConfiguredChannels().contains(this.measurement.get()) : false;
   }

   public final ReadOnlyBooleanProperty configuredProperty() {
      return this.configured;
   }

   public boolean isConfigured() {
      return this.configured.get();
   }

   public final BooleanProperty displayableProperty() {
      return this.displayable;
   }

   public boolean isDisplayable() {
      return this.displayable.get();
   }

   public void setDisplayable(boolean displayable) {
      this.displayable.set(displayable);
   }

   private String getChannelColorStyle(Color color) {
      return "-fx-color-channel: #" + JavaFxUtil.colorToHexString(JavaFxUtil.colorFromAwt(color));
   }

   public ControlSliderToggleButton getToggle() {
      return this.toggle;
   }

   void setToggle(ControlSliderToggleButton toggle) {
      this.toggle = toggle;
   }

   public void init(AppModel appModel) {
      Channel channel = appModel.getChannel((String)this.measurement.get());
      boolean visible = this.isVisible(appModel);
      Platform.runLater(() -> {
         this.configured.setValue(visible);
         if (channel != null) {
            this.setStyle(this.getChannelColorStyle(channel.getColor()));
         }
      });
      if (this.toggle != null) {
         this.toggle.init(appModel);
      }
   }

   public StringProperty valueAsStringProperty() {
      return this.valueAsString;
   }

   public String getValueAsString() {
      return (String)this.valueAsString.get();
   }

   public void setValueAsString(String valueAsString) {
      this.valueAsString.set(valueAsString);
   }

   public VBox getControlPanel() {
      return this.controlPanel;
   }

   public ToggleSwitch getAutoModeToggle() {
      return this.autoModeToggle;
   }

   public AtomicBoolean getIsUserInput() {
      return this.isUserInput;
   }

   public Slider getSlider() {
      return this.slider;
   }

   public AtomicBoolean getWriteToDeviceIsPending() {
      return this.writeToDeviceIsPending;
   }

   public BigDecimal getPendingValueToWrite() {
      return this.pendingValueToWrite;
   }

   public ObjectProperty<Unit> unitProperty() {
      return this.valueEditor.unitProperty();
   }

   public Unit getUnit() {
      return this.valueEditor.getUnit();
   }

   public void setUnit(Unit unit) {
      this.valueEditor.setUnit(unit);
   }

   public Unit getSourceUnit() {
      return this.sourceUnit;
   }

   public void setSourceUnit(Unit sourceUnit) {
      this.sourceUnit = sourceUnit;
   }

   public MeasureEditor getValueEditor() {
      return this.valueEditor;
   }

   public Label getTitleLabel() {
      return this.titleLabel;
   }

   public void setButtonsOnTop(boolean buttonsOnTop) {
      this.controlPanel.getChildren().remove(this.buttonContainer);
      int position = this.controlPanel.getChildren().indexOf(this.stackPanel);
      if (!buttonsOnTop) {
         position++;
      }

      this.controlPanel.getChildren().add(position, this.buttonContainer);
   }
}
