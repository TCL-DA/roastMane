package com.cropster.roastingintelligence.ui.components;

import com.cropster.roastingintelligence.model.ControlStateConfiguration;
import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.util.AnimatedBorders;
import com.cropster.roastingintelligence.ui.util.PlatformScheduler;
import com.cropster.util.text.DurationFormat;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.util.Duration;
import org.apache.commons.lang3.StringUtils;
import org.cropster.roastingintelligence.measurement.ControlStateSource;
import org.cropster.roastingintelligence.measurement.impl.simulation.DeviceSimulatorSourceFactory;
import org.cropster.roastingintelligence.model.ControlState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ControlSwitch extends BorderPane implements Initializable {
   private final Logger logger = LoggerFactory.getLogger(ControlSwitch.class);
   @FXML
   private ToggleButton stateSwitch;
   @FXML
   private Label stateCaption;
   @FXML
   private Label demoFlag;
   @FXML
   private Label countdownLabel;
   private TimerTask turnOffTask;
   private long countdown = 0L;
   private final AtomicBoolean isUserInput = new AtomicBoolean(true);
   private final PauseTransition waitingReadback = new PauseTransition(Duration.seconds(5.0));
   private final AtomicBoolean isWaitingReadback = new AtomicBoolean(false);
   private final ControlStateSource source;
   private final ControlStateConfiguration stateConfiguration;
   private final BooleanProperty showNoConnectionProperty = new SimpleBooleanProperty(false);

   public ControlSwitch(ControlStateConfiguration stateConfiguration, ControlStateSource source) {
      Objects.requireNonNull(stateConfiguration, "Null `stateConfiguration` parameter");
      Objects.requireNonNull(source, "Null `source` parameter");
      JavaFxUtil.loadComponent(this);
      this.stateConfiguration = stateConfiguration;
      this.source = source;
      this.updateSwitch();
   }

   public void initialize(URL location, ResourceBundle resources) {
      this.managedProperty().bind(this.visibleProperty());
      this.waitingReadback.setOnFinished(e -> {
         this.stateSwitch.setDisable(false);
         if (this.source == null) {
            this.logger.error("Cannot read as source is not set for {}", this.getStateName());
         } else {
            this.setStateWithoutDeviceWrite(this.source.getState(this.getControlState()));
            this.isWaitingReadback.set(false);
         }
      });
      this.stateSwitch
         .disabledProperty()
         .addListener(
            (s, o, n) -> {
               if (!this.showNoConnectionProperty.get()) {
                  if (n) {
                     AnimatedBorders.setAnimatedBorder(
                        this.stateSwitch, AnimatedBorders.WAIT_SEGMENT_COLOR, new CornerRadii(20.0), AnimatedBorders.DEFAULT_BORDER_WIDTHS
                     );
                  } else {
                     AnimatedBorders.stopAnRestoreOriginalBorder(this.stateSwitch);
                  }
               }
            }
         );
      this.stateSwitch.selectedProperty().addListener((s, o, n) -> this.writeToDevice(n));
      this.showNoConnectionProperty.addListener((s, o, n) -> {
         if (n) {
            if (AnimatedBorders.isAnimatedBorderSet(this.stateSwitch)) {
               AnimatedBorders.stopAnRestoreOriginalBorder(this.stateSwitch);
            }

            this.stateSwitch.setDisable(true);
         } else {
            this.stateSwitch.setDisable(false);
         }
      });
      this.setOnMouseClicked(e -> {
         if (!this.stateSwitch.isDisabled()) {
            this.stateSwitch.setSelected(!this.stateSwitch.isSelected());
         }
      });
      this.setOnMouseEntered(e -> {
         if (!this.stateSwitch.isDisabled()) {
            this.getStyleClass().add("control-switch--hovered");
            this.stateSwitch.getStyleClass().add("button-push--hovered");
         }
      });
      this.setOnMouseExited(e -> {
         this.getStyleClass().remove("control-switch--hovered");
         this.stateSwitch.getStyleClass().remove("button-push--hovered");
      });
   }

   public ControlState getControlState() {
      return this.stateConfiguration.getState();
   }

   public ControlStateConfiguration getStateConfiguration() {
      return this.stateConfiguration;
   }

   public String getStateName() {
      return this.getControlState().getName();
   }

   public void writeToDevice(boolean switchOn) {
      if (this.turnOffTask != null && !switchOn) {
         this.stopSelfTurnOffTimer();
      }

      if (this.isUserInput.getAndSet(true)) {
         if (this.source == null) {
            this.logger.error("Cannot write as source is not set for {}", this.getStateName());
         } else {
            boolean oldState = this.stateSwitch.isSelected();
            this.stateSwitch.setDisable(true);
            if (switchOn != oldState) {
               this.setStateWithoutDeviceWrite(switchOn);
            }

            this.isWaitingReadback.set(true);
            PlatformScheduler.executeBackgroundTask(() -> {
               this.source.setState(this.getControlState(), switchOn);
               this.waitingReadback.playFromStart();
            });
         }
      }
   }

   public void setStateWithoutDeviceWrite(boolean value) {
      if (this.isWaitingReadback.get()) {
         if (value == this.stateSwitch.isSelected()) {
            this.waitingReadback.stop();
            this.stateSwitch.setDisable(false);
            this.isWaitingReadback.set(false);
            if (value) {
               this.startSelfTurnOffTimer();
            }
         }
      } else if (this.stateSwitch.isSelected() != value) {
         this.isUserInput.set(false);
         this.stateSwitch.setSelected(value);
         this.isUserInput.set(true);
      }
   }

   public BooleanProperty showNoConnectionPropertyProperty() {
      return this.showNoConnectionProperty;
   }

   public boolean isShowNoConnection() {
      return this.showNoConnectionProperty.get();
   }

   public void setShowNoConnection(boolean show) {
      this.showNoConnectionProperty.set(show);
   }

   public void updateSwitch() {
      this.stateCaption
         .setText(
            StringUtils.isNotBlank(this.stateConfiguration.getCustomName())
               ? this.stateConfiguration.getCustomName()
               : org.cropster.roastingintelligence.util.Messages.m_(this.stateConfiguration.getState().getName())
         );
      String demoSource = this.getDemoSourceName();
      this.demoFlag.setVisible(demoSource != null);
   }

   private String getDemoSourceName() {
      String demoSource = null;
      if (this.stateConfiguration != null && DeviceSimulatorSourceFactory.class.equals(this.stateConfiguration.getState().getSourceFactory())) {
         demoSource = org.cropster.roastingintelligence.util.Messages.m_(DeviceSimulatorSourceFactory.class.getSimpleName());
         demoSource = demoSource.replaceAll("\\((.*?)\\)", "");
         demoSource = demoSource.replace("  ", " ");
      }

      return demoSource != null ? demoSource.trim() : null;
   }

   private void startSelfTurnOffTimer() {
      this.stopSelfTurnOffTimer();
      if (this.stateConfiguration.getOffTimerInterval() > 0L) {
         this.turnOffTask = this.createTurnOffTask();
         Timer selfTurnOffTimer = getSelfTurnOffTimer();
         selfTurnOffTimer.scheduleAtFixedRate(this.turnOffTask, 11L, 1000L);
         selfTurnOffTimer.purge();
      }
   }

   private void stopSelfTurnOffTimer() {
      if (this.turnOffTask != null) {
         this.turnOffTask.cancel();
         this.turnOffTask = null;
         this.countdownLabel.setVisible(false);
         this.countdownLabel.setText(null);
         AnimatedBorders.stopAnRestoreOriginalBorder(this.stateSwitch);
      }
   }

   private TimerTask createTurnOffTask() {
      final long totalTime = this.stateConfiguration.getOffTimerInterval();
      this.countdown = totalTime;
      AnimatedBorders.setWaitRingBorder(this.stateSwitch, (double)this.countdown / (double)totalTime);
      return new TimerTask() {
         @Override
         public void run() {
            if (ControlSwitch.this.countdown <= 0L) {
               Platform.runLater(() -> {
                  ControlSwitch.this.stateSwitch.setSelected(false);
                  AnimatedBorders.stopAnRestoreOriginalBorder(ControlSwitch.this.stateSwitch);
               });
            } else {
               Platform.runLater(
                  () -> {
                     ControlSwitch.this.countdownLabel
                        .setText(
                           ControlSwitch.this.countdown < 60000L
                              ? ControlSwitch.this.countdown / 1000L + "s"
                              : ControlSwitch.SelfTurnOffTimerHolder.FORMAT.format(ControlSwitch.this.countdown)
                        );
                     ControlSwitch.this.countdownLabel.setVisible(true);
                     ControlSwitch.this.countdown -= 1000L;
                     AnimatedBorders.setWaitRingBorder(ControlSwitch.this.stateSwitch, (double)ControlSwitch.this.countdown / (double)totalTime);
                  }
               );
            }
         }
      };
   }

   private static Timer getSelfTurnOffTimer() {
      return ControlSwitch.SelfTurnOffTimerHolder.SELF_TURN_OFF_TIMER;
   }

   private static final class SelfTurnOffTimerHolder {
      private static final Timer SELF_TURN_OFF_TIMER = new Timer("ControlSwitch-SelfTurnOffTimer", true);
      private static final DurationFormat FORMAT = DurationFormat.getDurationInstance();
   }
}
