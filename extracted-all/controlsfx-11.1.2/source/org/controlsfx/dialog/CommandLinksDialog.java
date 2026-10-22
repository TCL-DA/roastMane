package org.controlsfx.dialog;

import impl.org.controlsfx.i18n.Localization;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.beans.Observable;
import javafx.beans.binding.DoubleBinding;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

public class CommandLinksDialog extends Dialog<ButtonType> {
   private static final int gapSize = 10;
   private final Map<ButtonType, CommandLinksDialog.CommandLinksButtonType> typeMap;
   private Label contentTextLabel;
   private GridPane grid = new GridPane() {
      protected double computePrefWidth(double height) {
         boolean isDefault = true;
         double pw = 0.0;

         for (ButtonType buttonType : CommandLinksDialog.this.getDialogPane().getButtonTypes()) {
            Button button = (Button)CommandLinksDialog.this.getDialogPane().lookupButton(buttonType);
            double buttonPrefWidth = button.getGraphic().prefWidth(-1.0);
            if (isDefault) {
               pw = buttonPrefWidth;
               isDefault = false;
            } else {
               pw = Math.min(pw, buttonPrefWidth);
            }
         }

         return pw + 10.0;
      }

      protected double computePrefHeight(double width) {
         double ph = CommandLinksDialog.this.getDialogPane().getHeader() == null ? 0.0 : 10.0;

         for (ButtonType buttonType : CommandLinksDialog.this.getDialogPane().getButtonTypes()) {
            Button button = (Button)CommandLinksDialog.this.getDialogPane().lookupButton(buttonType);
            ph += button.prefHeight(width) + 10.0;
         }

         return ph * 1.2;
      }
   };

   public CommandLinksDialog(CommandLinksDialog.CommandLinksButtonType... links) {
      this(Arrays.asList(links));
   }

   public CommandLinksDialog(List<CommandLinksDialog.CommandLinksButtonType> links) {
      this.grid.setHgap(10.0);
      this.grid.setVgap(10.0);
      this.grid.getStyleClass().add("container");
      DialogPane dialogPane = new DialogPane() {
         protected Node createButtonBar() {
            return null;
         }

         protected Node createButton(ButtonType buttonType) {
            return CommandLinksDialog.this.createCommandLinksButton(buttonType);
         }
      };
      this.setDialogPane(dialogPane);
      this.setTitle(Localization.getString("Dialog.info.title"));
      dialogPane.getStyleClass().add("command-links-dialog");
      dialogPane.getStylesheets().add(this.getClass().getResource("dialogs.css").toExternalForm());
      dialogPane.getStylesheets().add(this.getClass().getResource("commandlink.css").toExternalForm());
      this.typeMap = new HashMap<>();

      for (CommandLinksDialog.CommandLinksButtonType link : links) {
         this.addLinkToDialog(dialogPane, link);
      }

      this.addLinkToDialog(dialogPane, CommandLinksDialog.CommandLinksButtonType.buildHiddenCancelLink());
      this.updateGrid();
      dialogPane.getButtonTypes().addListener(c -> this.updateGrid());
      this.contentTextProperty().addListener(o -> this.updateContentText());
   }

   private void addLinkToDialog(DialogPane dialogPane, CommandLinksDialog.CommandLinksButtonType link) {
      this.typeMap.put(link.getButtonType(), link);
      dialogPane.getButtonTypes().add(link.getButtonType());
   }

   private void updateContentText() {
      String contentText = this.getDialogPane().getContentText();
      this.grid.getChildren().remove(this.contentTextLabel);
      if (contentText != null && !contentText.isEmpty()) {
         if (this.contentTextLabel != null) {
            this.contentTextLabel.setText(contentText);
         } else {
            this.contentTextLabel = new Label(this.getDialogPane().getContentText());
            this.contentTextLabel.getStyleClass().add("command-link-message");
         }

         this.grid.add(this.contentTextLabel, 0, 0);
      }
   }

   private void updateGrid() {
      this.grid.getChildren().clear();
      this.updateContentText();
      int row = 1;

      for (ButtonType buttonType : this.getDialogPane().getButtonTypes()) {
         if (buttonType != null) {
            Button button = (Button)this.getDialogPane().lookupButton(buttonType);
            GridPane.setHgrow(button, Priority.ALWAYS);
            GridPane.setVgrow(button, Priority.ALWAYS);
            this.grid.add(button, 0, row++);
         }
      }

      this.getDialogPane().setContent(this.grid);
      this.getDialogPane().requestLayout();
   }

   private Button createCommandLinksButton(ButtonType buttonType) {
      CommandLinksDialog.CommandLinksButtonType commandLink = this.typeMap.getOrDefault(buttonType, new CommandLinksDialog.CommandLinksButtonType(buttonType));
      Button button = new Button();
      button.getStyleClass().addAll(new String[]{"command-link-button"});
      button.setMaxHeight(Double.MAX_VALUE);
      button.setMaxWidth(Double.MAX_VALUE);
      button.setAlignment(Pos.CENTER_LEFT);
      ButtonData buttonData = buttonType.getButtonData();
      button.setDefaultButton(buttonData != null && buttonData.isDefaultButton());
      button.setOnAction(ae -> this.setResult(buttonType));
      final Label titleLabel = new Label(commandLink.getButtonType().getText());
      titleLabel.minWidthProperty().bind(new DoubleBinding() {
         {
            this.bind(new Observable[]{titleLabel.prefWidthProperty()});
         }

         protected double computeValue() {
            return titleLabel.getPrefWidth() + 400.0;
         }
      });
      titleLabel.getStyleClass().addAll(new String[]{"line-1"});
      titleLabel.setWrapText(true);
      titleLabel.setAlignment(Pos.TOP_LEFT);
      GridPane.setVgrow(titleLabel, Priority.NEVER);
      Label messageLabel = new Label(commandLink.getLongText());
      messageLabel.getStyleClass().addAll(new String[]{"line-2"});
      messageLabel.setWrapText(true);
      messageLabel.setAlignment(Pos.TOP_LEFT);
      messageLabel.setMaxHeight(Double.MAX_VALUE);
      GridPane.setVgrow(messageLabel, Priority.SOMETIMES);
      Node commandLinkImage = commandLink.getGraphic();
      Node view = (Node)(commandLinkImage == null
         ? new ImageView(CommandLinksDialog.class.getResource("arrow-green-right.png").toExternalForm())
         : commandLinkImage);
      Pane graphicContainer = new Pane(new Node[]{view});
      graphicContainer.getStyleClass().add("graphic-container");
      GridPane.setValignment(graphicContainer, VPos.TOP);
      GridPane.setMargin(graphicContainer, new Insets(0.0, 10.0, 0.0, 0.0));
      GridPane grid = new GridPane();
      grid.minWidthProperty().bind(titleLabel.prefWidthProperty());
      grid.setMaxHeight(Double.MAX_VALUE);
      grid.setMaxWidth(Double.MAX_VALUE);
      grid.getStyleClass().add("container");
      grid.add(graphicContainer, 0, 0, 1, 2);
      grid.add(titleLabel, 1, 0);
      grid.add(messageLabel, 1, 1);
      button.setGraphic(grid);
      button.minWidthProperty().bind(titleLabel.prefWidthProperty());
      if (commandLink.isHidden) {
         button.setVisible(false);
         button.setPrefHeight(1.0);
      }

      return button;
   }

   public static class CommandLinksButtonType {
      private final ButtonType buttonType;
      private final String longText;
      private final Node graphic;
      private boolean isHidden = false;

      public CommandLinksButtonType(String text, boolean isDefault) {
         this(new ButtonType(text, buildButtonData(isDefault)), null);
      }

      public CommandLinksButtonType(String text, String longText, boolean isDefault) {
         this(new ButtonType(text, buildButtonData(isDefault)), longText, null);
      }

      public CommandLinksButtonType(String text, String longText, Node graphic, boolean isDefault) {
         this(new ButtonType(text, buildButtonData(isDefault)), longText, graphic);
      }

      private CommandLinksButtonType(ButtonType buttonType) {
         this(buttonType, null);
      }

      private CommandLinksButtonType(ButtonType buttonType, String longText) {
         this(buttonType, longText, null);
      }

      private CommandLinksButtonType(ButtonType buttonType, String longText, Node graphic) {
         this.buttonType = buttonType;
         this.longText = longText;
         this.graphic = graphic;
      }

      private static ButtonData buildButtonData(boolean isDeafault) {
         return isDeafault ? ButtonData.OK_DONE : ButtonData.OTHER;
      }

      private static CommandLinksDialog.CommandLinksButtonType buildHiddenCancelLink() {
         CommandLinksDialog.CommandLinksButtonType link = new CommandLinksDialog.CommandLinksButtonType(new ButtonType("", ButtonData.CANCEL_CLOSE));
         link.isHidden = true;
         return link;
      }

      public ButtonType getButtonType() {
         return this.buttonType;
      }

      public Node getGraphic() {
         return this.graphic;
      }

      public String getLongText() {
         return this.longText;
      }
   }
}
