package com.cropster.roastingintelligence.ui.components;

import com.cropster.roastingintelligence.ui.layout.DetachableSidebar;
import com.cropster.roastingintelligence.ui.layout.SlidingPanel;
import com.cropster.roastingintelligence.ui.util.PlatformScheduler;
import java.util.concurrent.TimeUnit;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class SidebarButton extends Button {
   private final ObjectProperty<Pane> targetPane = new SimpleObjectProperty();
   private SlidingPanel slidingPanel;
   private final ChangeListener<Boolean> detachListener = (s, o, n) -> {
      if (((Pane)this.targetPane.get()).isVisible() && !n) {
         this.getStyleClass().add("button--selected");
      } else {
         this.getStyleClass().removeAll(new String[]{"button--selected"});
      }
   };
   private final ChangeListener<Boolean> visibleListener = (s, o, n) -> {
      if (n && !this.isDetached()) {
         this.getStyleClass().add("button--selected");
      } else {
         this.getStyleClass().removeAll(new String[]{"button--selected"});
      }
   };

   public SidebarButton() {
      this.targetPane.addListener((s, o, n) -> {
         if (o != null) {
            o.visibleProperty().removeListener(this.visibleListener);
         }

         if (n != null) {
            this.slidingPanel = (SlidingPanel)n.getParent();
            n.visibleProperty().addListener(this.visibleListener);
         } else {
            this.slidingPanel = null;
         }

         if (o instanceof DetachableSidebar) {
            ((DetachableSidebar)o).detachedProperty().removeListener(this.detachListener);
         }

         if (n instanceof DetachableSidebar) {
            ((DetachableSidebar)n).detachedProperty().addListener(this.detachListener);
         }
      });
      this.setOnAction(this::togglePanel);
   }

   private boolean isDetached() {
      boolean detached = false;
      if (this.targetPane.get() instanceof DetachableSidebar) {
         DetachableSidebar sidebar = (DetachableSidebar)this.targetPane.get();
         detached = sidebar.isDetached();
      }

      return detached;
   }

   public ObjectProperty<Pane> targetPaneProperty() {
      return this.targetPane;
   }

   public Pane getTargetPane() {
      return (Pane)this.targetPane.get();
   }

   public void setTargetPane(Pane targetPane) {
      this.targetPane.set(targetPane);
   }

   public void togglePanel(ActionEvent e) {
      if (this.isDetached()) {
         Stage window = (Stage)((Pane)this.targetPane.get()).getScene().getWindow();
         window.toFront();
      } else if (this.slidingPanel != null) {
         this.slidingPanel.toggleSlidingPanel((Parent)this.targetPane.get());
      }
   }

   public void closePanel() {
      if (this.isVisible()) {
         if (this.isDetached()) {
            DetachableSidebar sidebar = (DetachableSidebar)this.targetPane.get();
            sidebar.hideWindow();
         }

         if (this.targetPane.get() != null && ((Pane)this.targetPane.get()).isVisible()) {
            PlatformScheduler.schedule(() -> {
               if (this.slidingPanel != null) {
                  this.slidingPanel.slideIn();
               }

               this.getStyleClass().removeAll(new String[]{"button--selected"});
            }, 444, TimeUnit.MILLISECONDS);
         }
      }
   }
}
