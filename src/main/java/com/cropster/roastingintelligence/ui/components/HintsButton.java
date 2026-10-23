package com.cropster.roastingintelligence.ui.components;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.util.Icon;
import java.util.stream.Stream;
import javafx.beans.DefaultProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.controlsfx.control.PopOver.ArrowLocation;

@DefaultProperty("content")
public class HintsButton extends Button {
   private final ObjectProperty<ArrowLocation> arrowLocation = new SimpleObjectProperty(ArrowLocation.TOP_CENTER);
   private final ObservableList<Node> content = FXCollections.observableArrayList();
   private final DoubleProperty contentExtraHorizontalPadding = new SimpleDoubleProperty(0.0);
   private final DoubleProperty contentExtraVerticalPadding = new SimpleDoubleProperty(0.0);
   private final VBox footer = new VBox();
   private final BooleanProperty small = new SimpleBooleanProperty(false) {
      protected void invalidated() {
         if (this.get()) {
            HintsButton.this.getStyleClass().add("button--small-transparent");
         } else {
            HintsButton.this.getStyleClass().remove("button--small-transparent");
         }
      }
   };
   private final StringProperty title = new SimpleStringProperty(org.cropster.roastingintelligence.util.Messages.m_("About this feature"));

   public HintsButton() {
      this.setGraphic(Icon.INFO.load("icon--link"));
      this.getStyleClass().add("hints-button");
      if (this.small.get()) {
         this.getStyleClass().add("button--small-transparent");
      }

      this.setText(org.cropster.roastingintelligence.util.Messages.m_("Hints"));
      this.textProperty().addListener(e -> {
         throw new IllegalStateException("Hints text cannot be changed.");
      });
      this.footer.setAlignment(Pos.TOP_LEFT);
      this.footer.setFillWidth(true);
      this.footer.setSpacing(8.0);
      this.setOnAction(this::showHintsPopOver);
      this.onActionProperty().addListener(e -> {
         throw new IllegalStateException("On action callback cannot be changed.");
      });
   }

   public HintsButton(String title, ArrowLocation arrowLocation) {
      this();
      this.title.set(title);
      this.arrowLocation.set(arrowLocation);
   }

   public ObjectProperty<ArrowLocation> arrowLocationProperty() {
      return this.arrowLocation;
   }

   public DoubleProperty contentExtraHorizontalPaddingProperty() {
      return this.contentExtraHorizontalPadding;
   }

   public DoubleProperty contentExtraVerticalPaddingProperty() {
      return this.contentExtraVerticalPadding;
   }

   public ArrowLocation getArrowLocation() {
      return (ArrowLocation)this.arrowLocation.get();
   }

   public ObservableList<Node> getContent() {
      return this.content;
   }

   public double getContentExtraHorizontalPadding() {
      return this.contentExtraHorizontalPadding.get();
   }

   public double getContentExtraVerticalPadding() {
      return this.contentExtraVerticalPadding.get();
   }

   public ObservableList<Node> getFooter() {
      return this.footer.getChildren();
   }

   public String getTitle() {
      return (String)this.title.get();
   }

   public boolean isSmall() {
      return this.small.get();
   }

   public void setArrowLocation(ArrowLocation arrowLocation) {
      this.arrowLocation.set(arrowLocation);
   }

   public void setContentExtraHorizontalPadding(double contentExtraHorizontalPadding) {
      this.contentExtraHorizontalPadding.set(contentExtraHorizontalPadding);
   }

   public void setContentExtraVerticalPadding(double contentExtraVerticalPadding) {
      this.contentExtraVerticalPadding.set(contentExtraVerticalPadding);
   }

   public void setSmall(boolean small) {
      this.small.set(small);
   }

   public void setTitle(String title) {
      this.title.set(title);
   }

   public BooleanProperty smallProperty() {
      return this.small;
   }

   public StringProperty titleProperty() {
      return this.title;
   }

   private void showHintsPopOver(ActionEvent event) {
      PopOverBubble.Builder builder = PopOverBubble.builder()
         .withoutDetachable()
         .minimumWidth(192.0)
         .maximumWidth(360.0)
         .withHeader(this.getTitle(), Icon.INFO.load("icon--muted"));
      if (!this.getContent().isEmpty()) {
         builder.withContent(
            this.getContent()
               .stream()
               .flatMap(n -> n instanceof Text t ? JavaFxUtil.parseHtmlToTextNodes(t.getText()).stream() : Stream.of((Text)n))
               .toArray(Node[]::new)
         );
         builder.withContentExtraPadding(this.getContentExtraHorizontalPadding(), this.getContentExtraVerticalPadding());
      }

      if (!this.footer.getChildren().isEmpty()) {
         this.footer.getChildren().forEach(n -> VBox.setVgrow(n, Priority.ALWAYS));
         builder.withFooter(this.footer);
      }

      PopOverBubble popover = builder.build();
      popover.setOnHiding(e -> this.setDisable(false));
      popover.showAt(this, this.getArrowLocation());
      this.setDisable(true);
   }
}
