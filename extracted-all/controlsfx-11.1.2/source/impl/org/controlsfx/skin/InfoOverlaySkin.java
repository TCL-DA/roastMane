package impl.org.controlsfx.skin;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.Animation.Status;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import org.controlsfx.control.InfoOverlay;

public class InfoOverlaySkin extends SkinBase<InfoOverlay> {
   private final ImageView EXPAND_IMAGE = new ImageView(new Image(InfoOverlay.class.getResource("expand.png").toExternalForm()));
   private final ImageView COLLAPSE_IMAGE = new ImageView(new Image(InfoOverlay.class.getResource("collapse.png").toExternalForm()));
   private static final Duration TRANSITION_DURATION = new Duration(350.0);
   private Node content;
   private Label infoLabel;
   private HBox infoPanel;
   private ToggleButton expandCollapseButton;
   private Timeline timeline;
   private DoubleProperty transition = new SimpleDoubleProperty(this, "transition", 0.0) {
      protected void invalidated() {
         ((InfoOverlay)InfoOverlaySkin.this.getSkinnable()).requestLayout();
      }
   };

   public InfoOverlaySkin(final InfoOverlay control) {
      super(control);
      this.content = control.getContent();
      control.hoverProperty().addListener(new ChangeListener<Boolean>() {
         public void changed(ObservableValue<? extends Boolean> o, Boolean wasHover, Boolean isHover) {
            if (control.isShowOnHover() && (isHover && !InfoOverlaySkin.this.isExpanded() || !isHover && InfoOverlaySkin.this.isExpanded())) {
               InfoOverlaySkin.this.doToggle();
            }
         }
      });
      this.infoLabel = new Label();
      this.infoLabel.setWrapText(true);
      this.infoLabel.setAlignment(Pos.TOP_LEFT);
      this.infoLabel.getStyleClass().add("info");
      this.infoLabel.textProperty().bind(control.textProperty());
      this.expandCollapseButton = new ToggleButton();
      this.expandCollapseButton.setMouseTransparent(true);
      this.expandCollapseButton.visibleProperty().bind(Bindings.not(control.showOnHoverProperty()));
      this.expandCollapseButton.managedProperty().bind(Bindings.not(control.showOnHoverProperty()));
      this.updateToggleButton();
      this.infoPanel = new HBox(new Node[]{this.infoLabel, this.expandCollapseButton});
      this.infoPanel.setAlignment(Pos.TOP_LEFT);
      this.infoPanel.setFillHeight(true);
      this.infoPanel.getStyleClass().add("info-panel");
      this.infoPanel.setCursor(Cursor.HAND);
      this.infoPanel.setOnMouseClicked(new EventHandler<MouseEvent>() {
         public void handle(MouseEvent e) {
            if (!control.isShowOnHover()) {
               InfoOverlaySkin.this.doToggle();
            }
         }
      });
      this.getChildren().addAll(new Node[]{this.content, this.infoPanel});
      this.registerChangeListener(control.contentProperty(), e -> {
         this.getChildren().remove(0);
         this.getChildren().add(0, ((InfoOverlay)this.getSkinnable()).getContent());
         ((InfoOverlay)this.getSkinnable()).requestLayout();
      });
   }

   private void doToggle() {
      this.expandCollapseButton.setSelected(!this.expandCollapseButton.isSelected());
      this.toggleInfoPanel();
      this.updateToggleButton();
   }

   private boolean isExpanded() {
      return this.expandCollapseButton.isSelected();
   }

   protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
      double contentPrefHeight = this.content.prefHeight(contentWidth);
      double toggleButtonPrefWidth = this.expandCollapseButton.prefWidth(-1.0);
      this.expandCollapseButton.setMinWidth(toggleButtonPrefWidth);
      Insets infoPanelPadding = this.infoPanel.getPadding();
      double infoLabelWidth = this.snapSize(contentWidth - toggleButtonPrefWidth - infoPanelPadding.getLeft() - infoPanelPadding.getRight());
      double prefInfoPanelHeight = (
            this.snapSize(this.infoLabel.prefHeight(infoLabelWidth))
               + this.snapSpace(this.infoPanel.getPadding().getTop())
               + this.snapSpace(this.infoPanel.getPadding().getBottom())
         )
         * this.transition.get();
      this.infoLabel.setMaxWidth(infoLabelWidth);
      this.infoLabel.setMaxHeight(prefInfoPanelHeight);
      this.layoutInArea(this.content, contentX, contentY, contentWidth, contentHeight, -1.0, HPos.CENTER, VPos.TOP);
      this.layoutInArea(
         this.infoPanel, contentX, this.snapPosition(contentPrefHeight - prefInfoPanelHeight), contentWidth, prefInfoPanelHeight, 0.0, HPos.CENTER, VPos.BOTTOM
      );
   }

   private void updateToggleButton() {
      if (this.expandCollapseButton.isSelected()) {
         this.expandCollapseButton.getStyleClass().setAll(new String[]{"collapse-button"});
         this.expandCollapseButton.setGraphic(this.COLLAPSE_IMAGE);
      } else {
         this.expandCollapseButton.getStyleClass().setAll(new String[]{"expand-button"});
         this.expandCollapseButton.setGraphic(this.EXPAND_IMAGE);
      }
   }

   protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
      double insets = topInset + bottomInset;
      return insets + (this.content == null ? 0.0 : this.content.prefHeight(width));
   }

   protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
      double insets = leftInset + rightInset;
      return insets + (this.content == null ? 0.0 : this.content.prefWidth(height));
   }

   protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
      return this.computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);
   }

   protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
      return this.computePrefWidth(height, topInset, rightInset, bottomInset, leftInset);
   }

   private void toggleInfoPanel() {
      if (this.content != null) {
         Duration duration;
         if (this.timeline != null && this.timeline.getStatus() != Status.STOPPED) {
            duration = this.timeline.getCurrentTime();
            this.timeline.stop();
         } else {
            duration = TRANSITION_DURATION;
         }

         this.timeline = new Timeline();
         this.timeline.setCycleCount(1);
         KeyFrame k1;
         KeyFrame k2;
         if (this.isExpanded()) {
            k1 = new KeyFrame(Duration.ZERO, new KeyValue[]{new KeyValue(this.transition, 0)});
            k2 = new KeyFrame(duration, new KeyValue[]{new KeyValue(this.transition, 1, Interpolator.LINEAR)});
         } else {
            k1 = new KeyFrame(Duration.ZERO, new KeyValue[]{new KeyValue(this.transition, 1)});
            k2 = new KeyFrame(duration, new KeyValue[]{new KeyValue(this.transition, 0, Interpolator.LINEAR)});
         }

         this.timeline.getKeyFrames().setAll(new KeyFrame[]{k1, k2});
         this.timeline.play();
      }
   }
}
