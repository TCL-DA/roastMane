package com.cropster.roastingintelligence.ui.components;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.layout.MachineControlSidebar;
import com.cropster.roastingintelligence.ui.layout.SlidingPanel;
import com.cropster.roastingintelligence.ui.util.Icon;
import com.cropster.roastingintelligence.ui.util.PlatformScheduler;
import com.cropster.roastingintelligence.ui.util.SlidingAnimation;
import java.awt.Toolkit;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ControlToggleBar extends VBox implements Initializable {
   @FXML
   private ToggleGroup controlToggleGroup;
   @FXML
   private ToggleButton slidersToggleButton;
   @FXML
   private ToggleButton buttonsToggleButton;
   @FXML
   private ToggleButton controlsToggleButton;
   @FXML
   private HBox toggleContainer;
   @FXML
   private Button slidersButton;
   @FXML
   private Button buttonsButton;
   @FXML
   private Button controlsButton;
   private final IntegerProperty currentVisibleColumn = new SimpleIntegerProperty(-1);
   private SlidingPanel slidingPanel;
   private MachineControlSidebar detachableSidebar;
   private int preDetachedColumn;
   private ToggleButton preDetachedButton;
   private ControlSliderToggleBar slidersContainer;
   private VBox buttonsContainer;
   private boolean slidersVisible;
   private boolean buttonsVisible;
   private Runnable onPreSliding;
   private Runnable onPostSliding;

   public ControlToggleBar() {
      JavaFxUtil.loadComponent(this);
   }

   @FXML
   public void bringDetachedWindowOnTop() {
      this.detachableSidebar.getDetachedWindow().toFront();
   }

   public void initialize(URL location, ResourceBundle resources) {
      this.slidersToggleButton.setGraphic(Icon.CONTROL_SLIDER.load());
      this.slidersToggleButton.setTooltip(new Tooltip(org.cropster.roastingintelligence.util.Messages.m_("Show control sliders")));
      this.buttonsToggleButton.setGraphic(Icon.CONTROL_SWITCH.load());
      this.buttonsToggleButton.setTooltip(new Tooltip(org.cropster.roastingintelligence.util.Messages.m_("Show control buttons")));
      this.controlsToggleButton.setGraphic(Icon.CONTROL_SLIDER_AND_SWITCH.load());
      this.controlsToggleButton.setTooltip(new Tooltip(org.cropster.roastingintelligence.util.Messages.m_("Show machine controls")));
      this.slidersButton.setGraphic(Icon.CONTROL_SLIDER.load());
      this.slidersButton.setTooltip(new Tooltip(org.cropster.roastingintelligence.util.Messages.m_("Bring machine control window to front")));
      this.buttonsButton.setGraphic(Icon.CONTROL_SWITCH.load());
      this.buttonsButton.setTooltip(new Tooltip(org.cropster.roastingintelligence.util.Messages.m_("Bring machine control window to front")));
      this.controlsButton.setGraphic(Icon.CONTROL_SLIDER_AND_SWITCH.load());
      this.controlsButton.setTooltip(new Tooltip(org.cropster.roastingintelligence.util.Messages.m_("Bring machine control window to front")));
      this.visibleProperty().bind(this.slidersToggleButton.visibleProperty().or(this.buttonsToggleButton.visibleProperty()));
      JavaFxUtil.configureTooltips(this);
   }

   public void setEnabled(boolean enabled) {
      this.slidersToggleButton.setDisable(!enabled);
      this.buttonsToggleButton.setDisable(!enabled);
      this.controlsToggleButton.setDisable(!enabled);
   }

   public void init(SlidingPanel slidingPanel, MachineControlSidebar detachableSidebar, ControlSliderToggleBar slidersContainer, VBox buttonsContainer) {
      this.slidingPanel = slidingPanel;
      this.detachableSidebar = detachableSidebar;
      this.slidersContainer = slidersContainer;
      this.buttonsContainer = buttonsContainer;
      slidingPanel.switchPanel(detachableSidebar);
      this.controlToggleGroup.selectedToggleProperty().addListener((s, o, n) -> this.selectionChanged(n));
      this.toggleContainer.visibleProperty().bind(detachableSidebar.detachedProperty().not());
      detachableSidebar.detachedProperty().addListener((s, o, n) -> Platform.runLater(() -> slidingPanel.switchPanel(detachableSidebar)));
      detachableSidebar.parentProperty().addListener((s, o, n) -> {
         if (n == null) {
            this.parentChanged();
         }
      });
      detachableSidebar.getLayoutButton().setOnAction(e -> {
         if (detachableSidebar.isDetached()) {
            Stage w = detachableSidebar.getDetachedWindow();
            w.setMinWidth(0.0);
            w.setMaxWidth(Double.MAX_VALUE);
            slidersContainer.switchLayout();
            w.sizeToScene();
            w.setMinWidth(w.getWidth());
            w.setMaxWidth(w.getWidth());
         } else {
            SlidingAnimation.SlidingStatus status = slidingPanel.getSlidingStatus();
            if (status == SlidingAnimation.SlidingStatus.OUT || status == SlidingAnimation.SlidingStatus.SLIDING_OUT) {
               this.slideStarting();
               slidingPanel.slideIn(ev -> {
                  this.performOnPostSliding();
                  slidersContainer.switchLayout();
                  this.performSlidingOut(this.controlToggleGroup.getSelectedToggle());
               });
            }
         }
      });
      detachableSidebar.getLayoutButton()
         .visibleProperty()
         .bind(
            Bindings.size(slidersContainer.getVisibleToggles())
               .greaterThan(1)
               .and(detachableSidebar.detachedProperty().or(this.currentVisibleColumn.isEqualTo(0)).or(this.currentVisibleColumn.isEqualTo(2)))
         );
   }

   private void parentChanged() {
      if (this.detachableSidebar.isDetached()) {
         this.detachedParentChanged();
      } else {
         this.embeddedParentChanged();
      }
   }

   private void embeddedParentChanged() {
      this.preDetachedButton = this.getSelectedButton();
      this.preDetachedColumn = this.slidersToggleButton.isSelected() ? 0 : 1;
      this.updateControlsVisibility(this.getVisibleColumn(this.slidersVisible, this.buttonsVisible));
      this.controlToggleGroup.selectToggle(null);
   }

   private int getVisibleColumn(boolean sv, boolean bv) {
      return sv && bv ? 2 : (bv ? 1 : 0);
   }

   private void detachedParentChanged() {
      if (this.slidersVisible && this.preDetachedColumn == 0) {
         this.updateControlsVisibility(0);
      } else if (this.buttonsVisible && this.preDetachedColumn == 1) {
         this.updateControlsVisibility(1);
      }

      if (this.preDetachedButton != null) {
         this.preDetachedButton.setSelected(true);
      }
   }

   private void selectionChanged(Toggle toggle) {
      this.slideStarting();
      SlidingAnimation.SlidingStatus status = this.slidingPanel.getSlidingStatus();
      if (status == SlidingAnimation.SlidingStatus.IN) {
         this.performSlidingOut(toggle);
      } else if (status == SlidingAnimation.SlidingStatus.OUT && toggle == null) {
         this.slidingPanel.slideIn(e -> {
            this.currentVisibleColumn.set(-1);
            this.slideComplete();
         });
      } else {
         boolean singleColumnSliders = this.slidersContainer.isSingleColumn();
         int visibleColumn = this.currentVisibleColumn.get();
         if (!singleColumnSliders || (visibleColumn != 0 || toggle != this.buttonsToggleButton) && (visibleColumn != 1 || toggle != this.slidersToggleButton)) {
            this.slidingPanel.slideIn(e -> {
               this.performOnPostSliding();
               this.performSlidingOut(toggle);
            });
         } else {
            this.updateControlsVisibility(1 - visibleColumn);
            this.slideComplete();
         }
      }
   }

   private void slideStarting() {
      this.setEnabled(false);
      this.performOnPreSliding();
   }

   private void slideComplete() {
      this.setEnabled(true);
      this.performOnPostSliding();
   }

   private void performSlidingOut(Toggle toggle) {
      if (toggle == this.slidersToggleButton) {
         this.slideOut(0);
      } else if (toggle == this.buttonsToggleButton) {
         this.slideOut(1);
      } else if (toggle == this.controlsToggleButton) {
         this.slideOut(2);
      } else {
         this.slideComplete();
      }
   }

   private void slideOut(int visibleColumn) {
      this.updateControlsVisibility(visibleColumn);
      PlatformScheduler.schedule(() -> this.slidingPanel.slideOut(e -> this.slideComplete()), 111, TimeUnit.MILLISECONDS);
   }

   private void updateControlsVisibility(int visibleColumn) {
      boolean allVisible = this.slidersVisible && this.buttonsVisible && visibleColumn == 2;
      this.slidersContainer.setVisible(allVisible || this.slidersVisible && visibleColumn == 0);
      this.buttonsContainer.setVisible(allVisible || this.buttonsVisible && visibleColumn == 1);
      this.currentVisibleColumn.set(visibleColumn);
   }

   public ToggleButton getSlidersToggleButton() {
      return this.slidersToggleButton;
   }

   public ToggleButton getButtonsToggleButton() {
      return this.buttonsToggleButton;
   }

   public ToggleButton getControlsToggleButton() {
      return this.controlsToggleButton;
   }

   public void toggleNext() {
      if (this.isDisabled()) {
         Toolkit.getDefaultToolkit().beep();
      } else if (this.slidersVisible && this.buttonsVisible) {
         if (this.slidersToggleButton.isSelected()) {
            this.buttonsToggleButton.fire();
         } else if (!this.buttonsToggleButton.isSelected() && !this.controlsToggleButton.isSelected()) {
            this.slidersToggleButton.fire();
         } else {
            this.controlsToggleButton.fire();
         }
      } else if (this.slidersVisible) {
         this.slidersToggleButton.fire();
      } else if (this.buttonsVisible) {
         this.buttonsToggleButton.fire();
      }
   }

   public void togglePrevious() {
      if (this.isDisabled()) {
         Toolkit.getDefaultToolkit().beep();
      } else if (this.slidersVisible && this.buttonsVisible) {
         if (this.controlsToggleButton.isSelected()) {
            this.buttonsToggleButton.fire();
         } else if (!this.buttonsToggleButton.isSelected() && !this.slidersToggleButton.isSelected()) {
            this.controlsToggleButton.fire();
         } else {
            this.slidersToggleButton.fire();
         }
      } else if (this.slidersVisible) {
         this.slidersToggleButton.fire();
      } else if (this.buttonsVisible) {
         this.buttonsToggleButton.fire();
      }
   }

   public void toggleNone() {
      if (this.isDisabled()) {
         Toolkit.getDefaultToolkit().beep();
      } else if (this.buttonsToggleButton.isSelected()) {
         this.buttonsToggleButton.fire();
      } else if (this.slidersToggleButton.isSelected()) {
         this.slidersToggleButton.fire();
      } else if (this.controlsToggleButton.isSelected()) {
         this.controlsToggleButton.fire();
      }
   }

   public void closeSidebar() {
      if (this.detachableSidebar.isDetached()) {
         this.detachableSidebar.hideWindow();
      }

      PlatformScheduler.schedule(this::toggleNone, 888, TimeUnit.MILLISECONDS);
   }

   public ToggleButton getSelectedButton() {
      return (ToggleButton)this.controlToggleGroup.getSelectedToggle();
   }

   public boolean isSlidingPanelVisible() {
      return this.slidingPanel.getSlidingStatus() != SlidingAnimation.SlidingStatus.IN;
   }

   public void controlsChanged(boolean slidersVisible, boolean buttonsVisible) {
      boolean osv = this.slidersVisible;
      boolean obv = this.buttonsVisible;
      this.slidersVisible = slidersVisible;
      this.buttonsVisible = buttonsVisible;
      this.slidersToggleButton.setVisible(slidersVisible);
      this.buttonsToggleButton.setVisible(buttonsVisible);
      this.controlsToggleButton.setVisible(slidersVisible && buttonsVisible);
      if (this.detachableSidebar.isDetached()) {
         this.detachedControlsChanged(slidersVisible, buttonsVisible, osv, obv);
      } else {
         this.embeddedControlsChanged(slidersVisible, buttonsVisible, osv, obv);
      }
   }

   private void embeddedControlsChanged(boolean slidersVisible, boolean buttonsVisible, boolean oldSlidersVisible, boolean oldButtonsVisible) {
      SlidingAnimation.SlidingStatus status = this.slidingPanel.getSlidingStatus();
      if (status == SlidingAnimation.SlidingStatus.OUT || status == SlidingAnimation.SlidingStatus.SLIDING_OUT) {
         if (oldButtonsVisible && !buttonsVisible) {
            if (this.buttonsToggleButton.isSelected()) {
               this.buttonsToggleButton.fire();
            } else if (this.controlsToggleButton.isSelected()) {
               this.controlsToggleButton.fire();
            }
         } else if (oldSlidersVisible && !slidersVisible && this.slidersToggleButton.isSelected() || slidersVisible && !this.slidersContainer.isSingleColumn()) {
            if (this.slidersToggleButton.isSelected()) {
               this.slidersToggleButton.fire();
            } else if (this.controlsToggleButton.isSelected()) {
               this.controlsToggleButton.fire();
            }
         }
      }
   }

   private void detachedControlsChanged(boolean slidersVisible, boolean buttonsVisible, boolean oldSlidersVisible, boolean oldButtonsVisible) {
      if (oldButtonsVisible == buttonsVisible && oldSlidersVisible == slidersVisible && (!slidersVisible || this.slidersContainer.isSingleColumn())) {
         int visibleColumn = slidersVisible ? 0 : -1;
         if (buttonsVisible) {
            visibleColumn = slidersVisible ? 2 : 1;
         }

         this.updateControlsVisibility(visibleColumn);
      } else {
         this.preDetachedButton = null;
         this.detachableSidebar.hideWindow();
         PlatformScheduler.schedule(() -> {
            this.controlToggleGroup.selectToggle(null);
            this.setEnabled(true);
         }, 222, TimeUnit.MILLISECONDS);
      }
   }

   public void setOnPreSliding(Runnable onPreSliding) {
      this.onPreSliding = onPreSliding;
   }

   private void performOnPreSliding() {
      if (this.onPreSliding != null) {
         this.onPreSliding.run();
      }
   }

   public void setOnPostSliding(Runnable onPostSliding) {
      this.onPostSliding = onPostSliding;
   }

   private void performOnPostSliding() {
      if (this.onPostSliding != null) {
         this.onPostSliding.run();
      }
   }
}
