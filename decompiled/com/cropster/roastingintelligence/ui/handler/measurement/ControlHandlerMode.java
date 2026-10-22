package com.cropster.roastingintelligence.ui.handler.measurement;

import com.cropster.roastingintelligence.ui.components.ControlSlider;
import java.math.BigDecimal;
import javafx.animation.PauseTransition;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.model.Measure;

public abstract class ControlHandlerMode extends AbstractControlHandler {
   protected boolean isAutoModeMeasurement;
   protected SimpleBooleanProperty autoModeWriteIsPending = new SimpleBooleanProperty(false);
   protected final SimpleBooleanProperty autoModePendingValueToWrite = new SimpleBooleanProperty(false);
   protected SimpleBooleanProperty autoModeIsUserInput = new SimpleBooleanProperty(true);
   protected final PauseTransition autoToggleDebouncePause = new PauseTransition(Duration.seconds(1.0));

   public ControlHandlerMode(ControlSlider controlSlider) {
      super(controlSlider);
      this.isAutoModeMeasurement = controlSlider.getMeasurement()
         .equals(this.measurementReaderWriter.getFactory().getAutoModeMeasurement(controlSlider.getMeasurement()));
      controlSlider.getAutoModeToggle().setVisible(true);
      EventHandler<? super MouseEvent> clickHandler = e -> {
         boolean newAutoMode = !controlSlider.getAutoModeToggle().isOn();
         this.autoModePendingValueToWrite.set(newAutoMode);
         this.autoModeWriteIsPending.set(true);
         this.autoToggleDebouncePause.setOnFinished(actionEvent -> this.measurementReaderWriter.setAutoMode(newAutoMode, this.controlMeasurement));
         this.autoToggleDebouncePause.playFromStart();
      };
      controlSlider.getAutoModeToggle().getSwitcher().addEventHandler(MouseEvent.MOUSE_CLICKED, clickHandler);
      controlSlider.getToggle().getAutoModeToggle().getSwitcher().addEventHandler(MouseEvent.MOUSE_CLICKED, clickHandler);
   }

   public boolean isInAutoMode() {
      return this.controlSlider.getAutoModeToggle().isOn();
   }

   public Boolean getAutoMode() {
      return this.measurementReaderWriter.getAutoMode(this.controlMeasurement);
   }

   public boolean process(Channel channel, Measure measure, Boolean autoMode) {
      if (this.autoModeWriteIsPending.get() && (autoMode == null || this.autoModePendingValueToWrite.getValue().equals(autoMode))) {
         this.autoModeWriteIsPending.set(false);
      }

      if (!this.autoModeWriteIsPending.get()) {
         if (autoMode != null) {
            this.controlSlider.setDisable(false);
            if (autoMode != this.controlSlider.getAutoModeToggle().onProperty().get()) {
               this.autoModeIsUserInput.set(false);
               this.controlSlider.getAutoModeToggle().setOn(autoMode);
               this.autoModeIsUserInput.set(true);
            }
         } else if (autoMode == null) {
            this.controlSlider.setDisable(true);
            return false;
         }
      }

      if (measure == null) {
         this.controlSlider.getControlPanel().setDisable(true);
      } else if (this.isAutoModeMeasurement != this.isInAutoMode()) {
         this.controlSlider.getControlPanel().setDisable(true);
      } else if (this.controlSlider.getControlPanel().isDisable()) {
         this.controlSlider.getControlPanel().setDisable(false);
      }

      if (this.controlSlider.getSlider().isValueChanging()) {
         return false;
      } else if (this.controlSlider.getWriteToDeviceIsPending().get() && measure != null) {
         BigDecimal receivedValue = ControlSlider.round(this.sourceUnit.from(BigDecimal.valueOf(measure.getAmount()), this.unit));
         if (receivedValue.equals(this.controlSlider.getPendingValueToWrite())) {
            this.controlSlider.getWriteToDeviceIsPending().set(false);
         }

         return false;
      } else {
         return true;
      }
   }

   public void bind(ControlHandlerMode controlHandler) {
      if (controlHandler.getControlSlider().visibleProperty().isBound()) {
         controlHandler.getControlSlider().visibleProperty().unbind();
      }

      if (controlHandler.getControlSlider().getToggle().visibleProperty().isBound()) {
         controlHandler.getControlSlider().getToggle().visibleProperty().unbind();
      }

      BooleanBinding configuredBinding = controlHandler.getControlSlider()
         .configuredProperty()
         .and(controlHandler.getControlSlider().getAutoModeToggle().onProperty().not().or(this.getControlSlider().configuredProperty().not()));
      controlHandler.getControlSlider().visibleProperty().bind(configuredBinding.and(controlHandler.getControlSlider().displayableProperty()));
      controlHandler.getControlSlider()
         .getToggle()
         .visibleProperty()
         .bind(configuredBinding.and(controlHandler.getControlSlider().getToggle().displayableProperty()));
      controlHandler.getControlSlider().getToggle().visibleProperty().addListener((s, o, n) -> {
         if (n) {
            controlHandler.getControlSlider().getToggle().getToggleButton().fire();
         }
      });
      if (this.getControlSlider().visibleProperty().isBound()) {
         this.getControlSlider().visibleProperty().unbind();
      }

      if (this.getControlSlider().getToggle().visibleProperty().isBound()) {
         this.getControlSlider().getToggle().visibleProperty().unbind();
      }

      configuredBinding = this.getControlSlider()
         .configuredProperty()
         .and(this.getControlSlider().getAutoModeToggle().onProperty().or(controlHandler.getControlSlider().configuredProperty().not()));
      this.getControlSlider().visibleProperty().bind(configuredBinding.and(this.getControlSlider().displayableProperty()));
      this.getControlSlider().getToggle().visibleProperty().bind(configuredBinding.and(this.getControlSlider().getToggle().displayableProperty()));
      this.getControlSlider().getToggle().visibleProperty().addListener((s, o, n) -> {
         if (n) {
            this.getControlSlider().getToggle().getToggleButton().fire();
         }
      });
      controlHandler.getControlSlider().getAutoModeToggle().onProperty().bindBidirectional(this.getControlSlider().getAutoModeToggle().onProperty());
      controlHandler.autoModePendingValueToWrite.bindBidirectional(this.autoModePendingValueToWrite);
      controlHandler.autoModeWriteIsPending.bindBidirectional(this.autoModeWriteIsPending);
      controlHandler.autoModeIsUserInput.bindBidirectional(this.autoModeIsUserInput);
   }
}
