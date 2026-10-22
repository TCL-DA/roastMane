package org.controlsfx.validation.decoration;

import java.util.Arrays;
import java.util.Collection;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.controlsfx.control.decoration.Decoration;
import org.controlsfx.control.decoration.GraphicDecoration;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationMessage;

public class GraphicValidationDecoration extends AbstractValidationDecoration {
   private static final Image ERROR_IMAGE = new Image(
      GraphicValidationDecoration.class.getResource("/impl/org/controlsfx/control/validation/decoration-error.png").toExternalForm()
   );
   private static final Image WARNING_IMAGE = new Image(
      GraphicValidationDecoration.class.getResource("/impl/org/controlsfx/control/validation/decoration-warning.png").toExternalForm()
   );
   private static final Image INFO_IMAGE = new Image(
      GraphicValidationDecoration.class.getResource("/impl/org/controlsfx/control/validation/decoration-info.png").toExternalForm()
   );
   private static final Image REQUIRED_IMAGE = new Image(
      GraphicValidationDecoration.class.getResource("/impl/org/controlsfx/control/validation/required-indicator.png").toExternalForm()
   );
   private static final String SHADOW_EFFECT = "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);";
   private static final String POPUP_SHADOW_EFFECT = "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 5, 0, 0, 5);";
   private static final String TOOLTIP_COMMON_EFFECTS = "-fx-font-weight: bold; -fx-padding: 5; -fx-border-width:1;";
   private static final String ERROR_TOOLTIP_EFFECT = "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 5, 0, 0, 5);-fx-font-weight: bold; -fx-padding: 5; -fx-border-width:1;-fx-background-color: FBEFEF; -fx-text-fill: cc0033; -fx-border-color:cc0033;";
   private static final String WARNING_TOOLTIP_EFFECT = "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 5, 0, 0, 5);-fx-font-weight: bold; -fx-padding: 5; -fx-border-width:1;-fx-background-color: FFFFCC; -fx-text-fill: CC9900; -fx-border-color: CC9900;";
   private static final String INFO_TOOLTIP_EFFECT = "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 5, 0, 0, 5);-fx-font-weight: bold; -fx-padding: 5; -fx-border-width:1;-fx-background-color: c4d0ef; -fx-text-fill: FFFFFF; -fx-border-color: a8c8ff;";

   @Deprecated
   protected Node createErrorNode() {
      return new ImageView(ERROR_IMAGE);
   }

   @Deprecated
   protected Node createWarningNode() {
      return new ImageView(WARNING_IMAGE);
   }

   protected Node createDecorationNode(ValidationMessage message) {
      Node graphic = this.getGraphicBySeverity(message.getSeverity());
      graphic.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);");
      Label label = new Label();
      label.setGraphic(graphic);
      label.setTooltip(this.createTooltip(message));
      label.setAlignment(Pos.CENTER);
      return label;
   }

   protected Node getGraphicBySeverity(Severity severity) {
      switch (severity) {
         case ERROR:
            return this.createErrorNode();
         case WARNING:
            return this.createWarningNode();
         default:
            return new ImageView(INFO_IMAGE);
      }
   }

   protected Tooltip createTooltip(ValidationMessage message) {
      Tooltip tooltip = new Tooltip(message.getText());
      tooltip.setOpacity(0.9);
      tooltip.setAutoFix(true);
      tooltip.setStyle(this.getStyleBySeverity(message.getSeverity()));
      return tooltip;
   }

   protected String getStyleBySeverity(Severity severity) {
      switch (severity) {
         case ERROR:
            return "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 5, 0, 0, 5);-fx-font-weight: bold; -fx-padding: 5; -fx-border-width:1;-fx-background-color: FBEFEF; -fx-text-fill: cc0033; -fx-border-color:cc0033;";
         case WARNING:
            return "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 5, 0, 0, 5);-fx-font-weight: bold; -fx-padding: 5; -fx-border-width:1;-fx-background-color: FFFFCC; -fx-text-fill: CC9900; -fx-border-color: CC9900;";
         default:
            return "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 5, 0, 0, 5);-fx-font-weight: bold; -fx-padding: 5; -fx-border-width:1;-fx-background-color: c4d0ef; -fx-text-fill: FFFFFF; -fx-border-color: a8c8ff;";
      }
   }

   @Override
   protected Collection<Decoration> createValidationDecorations(ValidationMessage message) {
      return Arrays.asList(new GraphicDecoration(this.createDecorationNode(message), Pos.BOTTOM_LEFT));
   }

   @Override
   protected Collection<Decoration> createRequiredDecorations(Control target) {
      return Arrays.asList(
         new GraphicDecoration(new ImageView(REQUIRED_IMAGE), Pos.TOP_LEFT, REQUIRED_IMAGE.getWidth() / 2.0, REQUIRED_IMAGE.getHeight() / 2.0)
      );
   }
}
