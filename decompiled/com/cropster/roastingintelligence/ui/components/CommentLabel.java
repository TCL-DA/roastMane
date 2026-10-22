package com.cropster.roastingintelligence.ui.components;

import com.cropster.roastingintelligence.ui.JavaFxApplicationState;
import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.layout.CommentsSidebar;
import com.cropster.roastingintelligence.ui.util.Icon;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.apache.commons.lang3.StringUtils;
import org.controlsfx.control.HiddenSidesPane;
import org.cropster.roastingintelligence.RoastingModel;
import org.cropster.roastingintelligence.model.Measure;
import org.cropster.roastingintelligence.model.RoastingComment;
import org.girod.javafx.svgimage.SVGImage;

public class CommentLabel extends HiddenSidesPane {
   private final DoubleProperty cornerRadius = new SimpleDoubleProperty();
   private final Map<Icon, SVGImage> iconsCache = new HashMap<>(3);
   private final BooleanProperty sliderVisibleProperty = new SimpleBooleanProperty();
   @FXML
   private HBox commentLabel;
   @FXML
   private Label icon;
   @FXML
   private TimeLabel timeLabel;
   @FXML
   private MeasureLabel tempLabel;
   @FXML
   private Label titleLabel;
   @FXML
   private HBox sliderRightSide;
   @FXML
   private Button leftButton;
   @FXML
   private Button centerButton;
   @FXML
   private Button rightButton;
   private RoastingComment comment;

   public static Node iconForEvent(String event) {
      if (event != null) {
         if ("gas".equals(event)) {
            return Icon.GAS.load("icon--muted");
         } else {
            return "airflow".equals(event) ? Icon.AIRFLOW.load("icon--muted") : Icon.COFFEE_BEAN.load("icon--muted");
         }
      } else {
         return Icon.USER.load("icon--muted");
      }
   }

   public CommentLabel() {
      JavaFxUtil.loadComponent(this);
      MessageFormat commentLabelFormat = new MessageFormat("-fx-border-radius: {0,number,#0.0#}; -fx-background-radius: {0,number,#0.0#};", Locale.US);
      MessageFormat sliderRightSideFormat = new MessageFormat(
         "-fx-border-radius: 0 {0,number,#0.0#} {0,number,#0.0#} 0; -fx-background-radius: 0 {0,number,#0.0#} {0,number,#0.0#} 0;", Locale.US
      );
      this.cornerRadius.addListener((s, o, n) -> {
         double newRadius = n.doubleValue();
         this.commentLabel.setStyle(commentLabelFormat.format(new Object[]{newRadius}));
         this.sliderRightSide.setStyle(sliderRightSideFormat.format(new Object[]{newRadius}));
      });
      this.cornerRadius.set(8.0);
      this.tempLabel.managedProperty().bind(this.tempLabel.visibleProperty());
      this.leftButton.managedProperty().bind(this.leftButton.visibleProperty());
      this.centerButton.managedProperty().bind(this.centerButton.visibleProperty());
      this.rightButton.managedProperty().bind(this.rightButton.visibleProperty());
      this.sliderVisibleProperty.bind(this.leftButton.visibleProperty().or(this.centerButton.visibleProperty()).or(this.rightButton.visibleProperty()));
      this.sliderVisibleProperty.addListener((s, o, n) -> this.setRight(n ? this.sliderRightSide : null));
      this.setTriggerDistance(-1.0);
      this.addEventFilter(MouseEvent.MOUSE_ENTERED, e -> this.show(Side.RIGHT));
      this.addEventFilter(MouseEvent.MOUSE_EXITED, e -> this.hide());
      this.setAnimationDelay(Duration.ZERO);
      this.setAnimationDuration(Duration.millis(0.5));
      this.leftButton.setVisible(false);
      this.centerButton.setVisible(false);
      this.rightButton.setVisible(false);
   }

   public CommentLabel(CommentsSidebar.CommentListCell listCell) {
      JavaFxUtil.loadComponent(this);
      MessageFormat commentLabelFormat = new MessageFormat("-fx-border-radius: {0,number,#0.0#}; -fx-background-radius: {0,number,#0.0#};", Locale.US);
      MessageFormat sliderRightSideFormat = new MessageFormat(
         "-fx-border-radius: 0 {0,number,#0.0#} {0,number,#0.0#} 0; -fx-background-radius: 0 {0,number,#0.0#} {0,number,#0.0#} 0;", Locale.US
      );
      this.cornerRadius.addListener((s, o, n) -> {
         double newRadius = n.doubleValue();
         this.commentLabel.setStyle(commentLabelFormat.format(new Object[]{newRadius}));
         this.sliderRightSide.setStyle(sliderRightSideFormat.format(new Object[]{newRadius}));
      });
      this.cornerRadius.set(8.0);
      this.tempLabel.managedProperty().bind(this.tempLabel.visibleProperty());
      this.leftButton.managedProperty().bind(this.leftButton.visibleProperty());
      this.centerButton.managedProperty().bind(this.centerButton.visibleProperty());
      this.rightButton.managedProperty().bind(this.rightButton.visibleProperty());
      this.sliderVisibleProperty.bind(this.leftButton.visibleProperty().or(this.centerButton.visibleProperty()).or(this.rightButton.visibleProperty()));
      this.sliderVisibleProperty.addListener((s, o, n) -> this.setRight(n ? this.sliderRightSide : null));
      this.setTriggerDistance(-1.0);
      this.addEventFilter(MouseEvent.MOUSE_ENTERED, e -> this.show(Side.RIGHT));
      this.addEventFilter(MouseEvent.MOUSE_EXITED, e -> this.hide());
      this.setAnimationDelay(Duration.ZERO);
      this.setAnimationDuration(Duration.millis(0.5));
      this.leftButton.setOnAction(e -> listCell.startEdit());
      this.leftButton.setGraphic(Icon.EDIT_INLINE.load());
      this.leftButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
      updateTooltip(this.leftButton, org.cropster.roastingintelligence.util.Messages.m_("Edit"));
      this.centerButton.setOnAction(e -> listCell.delete());
      this.centerButton.setGraphic(Icon.DELETE.load());
      this.centerButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
      updateTooltip(this.centerButton, org.cropster.roastingintelligence.util.Messages.m_("Delete"));
      this.rightButton.setVisible(false);
   }

   public void init(RoastingComment comment) {
      this.comment = comment;
      this.timeLabel.setTime(comment.getTime());
      this.tempLabel.setValue(null);
      this.tempLabel.setVisible(false);
      String text = "";
      if (comment.getEvent() != null && !comment.getEvent().matches(".+[cC]ontrol")) {
         text = org.cropster.roastingintelligence.util.Messages.m_(comment.getEvent());
      }

      if (StringUtils.isNotBlank(comment.getText())) {
         if (StringUtils.isNotBlank(text)) {
            text = text + " - ";
         }

         text = text + comment.getText();
      }

      this.titleLabel.setText(text);
      this.icon.setGraphic(iconForEvent(comment.getEvent()));
   }

   public void init(RoastingComment comment, Measure temperature) {
      this.init(comment);
      this.tempLabel.setValue(temperature);
      this.tempLabel.setVisible(true);
   }

   public void init(RoastingComment comment, Measure commentTemperature, RoastingModel roastingModel, long duration, JavaFxApplicationState state) {
      this.init(comment, commentTemperature);
      String text = comment.getText();
      String event = comment.getEvent();
      if ("gasControl".equals(event)) {
         this.graphicProperty()
            .bind(
               Bindings.createObjectBinding(
                  () -> state.isReplaying() ? Icon.REPLAY.load("icon--success") : Icon.REPLAY.load("icon--link"), new Observable[]{state.replayingProperty()}
               )
            );
      } else {
         this.graphicProperty().unbind();
         this.setGraphic(null);
      }

      Measure currentTemperature = roastingModel.getTempAtTime(duration);
      boolean buttonsDisabled = roastingModel.getComment(comment.getTime()) != null
         || roastingModel.getTurningPoint() != null
            && commentTemperature != null
            && currentTemperature != null
            && commentTemperature.getAmount() > currentTemperature.getAmount();
      boolean buttonsVisible = event == null
         || !"start".equals(event) && !"end".equals(event) && !"turningPoint".equals(event) && !event.matches(".+[cC]ontrol");
      Button markNowButton = this.getMarkNowButton();
      markNowButton.setDisable(false);
      markNowButton.setVisible(buttonsVisible);
      markNowButton.setGraphic((Node)this.iconsCache.computeIfAbsent(Icon.CHECK_MARK, i -> i.load()));
      markNowButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
      markNowButton.setOnAction(a -> {
         markNowButton.setDisable(true);
         roastingModel.addComment(new RoastingComment(duration, event, text));
      });
      updateTooltip(markNowButton, org.cropster.roastingintelligence.util.Messages.m_("Mark now"));
      Button markOnTimeButton = this.getMarkOnTimeButton();
      markOnTimeButton.setDisable(buttonsDisabled);
      markOnTimeButton.setVisible(buttonsVisible);
      markOnTimeButton.setGraphic((Node)this.iconsCache.computeIfAbsent(Icon.TIME, i -> i.load()));
      markOnTimeButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
      markOnTimeButton.setOnAction(a -> {
         markOnTimeButton.setDisable(true);
         roastingModel.addComment(comment.clone());
      });
      updateTooltip(markOnTimeButton, org.cropster.roastingintelligence.util.Messages.m_("Mark on reference's time"));
      boolean enabled = roastingModel.getTurningPoint() != null && commentTemperature != null;
      Long timeForTemperature = enabled ? roastingModel.getTimeForTemp(comment.getTime(), commentTemperature.getAmount()) : null;
      enabled = enabled && timeForTemperature != null;
      Button markOnTemperatureButton = this.getMarkOnTemperatureButton();
      markOnTemperatureButton.setDisable(buttonsDisabled || !enabled);
      markOnTemperatureButton.setVisible(buttonsVisible);
      markOnTemperatureButton.setGraphic((Node)this.iconsCache.computeIfAbsent(Icon.TEMPERATURE, i -> i.load()));
      markOnTemperatureButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
      markOnTemperatureButton.setOnAction(a -> {
         markOnTemperatureButton.setDisable(true);
         roastingModel.addComment(new RoastingComment(timeForTemperature, event, text));
      });
      updateTooltip(markOnTemperatureButton, org.cropster.roastingintelligence.util.Messages.m_("Mark on reference's temp."));
      this.setRight(buttonsVisible ? this.sliderRightSide : null);
   }

   public Button getEditButton() {
      return this.leftButton;
   }

   public Button getDeleteButton() {
      return this.centerButton;
   }

   public Button getMarkNowButton() {
      return this.leftButton;
   }

   public Button getMarkOnTimeButton() {
      return this.centerButton;
   }

   public Button getMarkOnTemperatureButton() {
      return this.rightButton;
   }

   public ObjectProperty<Node> graphicProperty() {
      return this.titleLabel.graphicProperty();
   }

   public Node getGraphic() {
      return this.titleLabel.getGraphic();
   }

   public void setGraphic(Node node) {
      this.titleLabel.setGraphic(node);
   }

   public RoastingComment getComment() {
      return this.comment;
   }

   public void reset() {
      this.comment = null;
      this.timeLabel.setTime((Long)null);
      this.tempLabel.setValue(null);
      this.titleLabel.setText("");
      this.leftButton.setOnAction(null);
      this.leftButton.setVisible(false);
      updateTooltip(this.leftButton, null);
      this.centerButton.setOnAction(null);
      this.centerButton.setVisible(false);
      updateTooltip(this.centerButton, null);
      this.rightButton.setOnAction(null);
      this.rightButton.setVisible(false);
      updateTooltip(this.rightButton, null);
      this.graphicProperty().unbind();
      this.setGraphic(null);
   }

   public void addContentStyleClass(String styleClass) {
      this.getContent().getStyleClass().add(styleClass);
      this.sliderRightSide.getStyleClass().add(styleClass);
   }

   public void removeContentStyleClass(String styleClass) {
      this.getContent().getStyleClass().removeIf(s -> styleClass.equals(s));
      this.sliderRightSide.getStyleClass().removeIf(s -> styleClass.equals(s));
   }

   public DoubleProperty cornerRadiusProperty() {
      return this.cornerRadius;
   }

   public double getCornerRadius() {
      return this.cornerRadiusProperty().get();
   }

   public void setCornerRadius(double cornerRadius) {
      this.cornerRadiusProperty().set(cornerRadius);
   }

   protected Skin<?> createDefaultSkin() {
      Skin<?> skin = super.createDefaultSkin();
      Rectangle clip = (Rectangle)this.getClip();
      clip.arcWidthProperty().bind(this.cornerRadiusProperty().multiply(2.0));
      clip.arcHeightProperty().bind(this.cornerRadiusProperty().multiply(2.0));
      return skin;
   }

   private static void updateTooltip(Button button, String tooltipText) {
      if (StringUtils.isBlank(tooltipText)) {
         button.setTooltip(null);
      } else {
         javafx.scene.control.Tooltip currentTooltip = button.getTooltip();
         if (currentTooltip == null || !tooltipText.equals(currentTooltip.getText())) {
            button.setTooltip(new Tooltip(tooltipText));
         }
      }
   }
}
