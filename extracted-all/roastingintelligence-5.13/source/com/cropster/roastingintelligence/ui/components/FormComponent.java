package com.cropster.roastingintelligence.ui.components;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.DefaultProperty;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.apache.commons.lang3.StringUtils;

@DefaultProperty("nodes")
public class FormComponent extends BorderPane implements Initializable {
   private static final String ORIGINAL_MAXIMUM_WIDTH_PROPERTY_KEY = "originalMaximumWidthProperty";
   @FXML
   private HBox contentHBox;
   @FXML
   private TextFlow descriptionTextFlow;
   @FXML
   private TextFlow noteTextFlow;
   @FXML
   private Label titleLabel;
   @FXML
   private HBox decorations;
   private final BooleanProperty required = new SimpleBooleanProperty(false);

   public FormComponent() {
      this(null, null, null, 0.0);
   }

   public FormComponent(Node... nodes) {
      this(null, null, null, 0.0, nodes);
   }

   public FormComponent(String title, Node... nodes) {
      this(title, null, null, 0.0, nodes);
   }

   public FormComponent(String title, String note, Node... nodes) {
      this(title, note, null, 0.0, nodes);
   }

   public FormComponent(String title, String note, String description, Node... nodes) {
      this(title, note, description, 0.0, nodes);
   }

   public FormComponent(double spacing) {
      this(null, null, null, spacing);
   }

   public FormComponent(String title, double spacing, Node... nodes) {
      this(title, null, null, spacing, nodes);
   }

   public FormComponent(String title, String note, double spacing, Node... nodes) {
      this(title, note, null, spacing, nodes);
   }

   public FormComponent(String title, String note, String description, double spacing, Node... nodes) {
      JavaFxUtil.loadComponent(this);
      this.titleLabel.setText(title);
      this.setNote(note);
      this.contentHBox.setSpacing(spacing);
      if (description != null) {
         this.descriptionTextFlow.getChildren().add(new Text(description));
      }

      if (nodes != null && nodes.length > 0) {
         this.contentHBox.getChildren().addAll(nodes);
      }
   }

   public void initialize(URL location, ResourceBundle resources) {
      this.contentHBox.getChildren().addListener(e -> {
         ObservableList<? extends Node> children = e.getList();
         if (children.size() == 1) {
            Node node = (Node)children.get(0);
            HBox.setHgrow(node, Priority.ALWAYS);
            if (node instanceof Region region) {
               node.getProperties().put("originalMaximumWidthProperty", region.getMaxWidth());
               region.setMaxWidth(Double.MAX_VALUE);
            }
         } else {
            children.forEach(n -> {
               if (n instanceof Region regionx && regionx.getProperties().containsKey("originalMaximumWidthProperty")) {
                  regionx.setMaxWidth((Double)regionx.getProperties().get("originalMaximumWidthProperty"));
                  regionx.getProperties().remove("originalMaximumWidthProperty");
               }

               HBox.setHgrow(n, Priority.NEVER);
            });
         }
      });
      this.titleLabel.visibleProperty().bind(Bindings.isNotEmpty(this.titleLabel.textProperty()));
      this.noteTextFlow.visibleProperty().bind(Bindings.createBooleanBinding(() -> {
         ObservableList<Node> notes = this.getNotes();
         if (notes.isEmpty()) {
            return false;
         } else {
            if (notes.size() == 1) {
               Node child = (Node)notes.get(0);
               if (child instanceof Text) {
                  return StringUtils.isNotEmpty(((Text)child).getText());
               }

               if (child instanceof Labeled) {
                  return StringUtils.isNotEmpty(((Labeled)child).getText());
               }
            }

            return true;
         }
      }, new Observable[]{this.noteTextFlow.getChildren()}));
      this.descriptionTextFlow.visibleProperty().bind(Bindings.isNotEmpty(this.descriptionTextFlow.getChildren()));
      JavaFxUtil.setDefaultLineSpacing(this);
   }

   public ObservableList<Node> getNodes() {
      return this.contentHBox.getChildren();
   }

   public ObservableList<Node> getDecorations() {
      return this.decorations.getChildren();
   }

   public ObservableList<Node> getDescription() {
      return this.descriptionTextFlow.getChildren();
   }

   public TextFlow getDescriptionTextFlow() {
      return this.descriptionTextFlow;
   }

   public HBox getNodesContainer() {
      return this.contentHBox;
   }

   public Label getTitleLabel() {
      return this.titleLabel;
   }

   public TextFlow getNoteTextFlow() {
      return this.noteTextFlow;
   }

   public ObservableList<Node> getNotes() {
      return this.noteTextFlow.getChildren();
   }

   public StringProperty noteProperty() {
      if (this.noteTextFlow.getChildren().isEmpty()) {
         throw new IllegalStateException("Empty 'noteTextFlow'.");
      } else {
         Node firstChild = (Node)this.noteTextFlow.getChildren().get(0);
         if (firstChild instanceof Text) {
            return ((Text)firstChild).textProperty();
         } else if (firstChild instanceof Labeled) {
            return ((Labeled)firstChild).textProperty();
         } else {
            throw new IllegalStateException("First child of 'noteTextFlow' is not a Text or Labeled element.");
         }
      }
   }

   public String getNote() {
      if (this.noteTextFlow.getChildren().isEmpty()) {
         throw new IllegalStateException("Empty 'noteTextFlow'.");
      } else {
         Node firstChild = (Node)this.noteTextFlow.getChildren().get(0);
         if (firstChild instanceof Text) {
            return ((Text)firstChild).getText();
         } else if (firstChild instanceof Labeled) {
            return ((Labeled)firstChild).getText();
         } else {
            throw new IllegalStateException("First child of 'noteTextFlow' is not a Text or Labeled element.");
         }
      }
   }

   public void setNote(String note) {
      Text noteText = new Text(note);
      noteText.getStyleClass().add("hint");
      ObservableList<Node> notes = this.getNotes();
      notes.clear();
      notes.add(noteText);
   }

   public DoubleProperty spacingProperty() {
      return this.contentHBox.spacingProperty();
   }

   public double getSpacing() {
      return this.contentHBox.getSpacing();
   }

   public void setSpacing(double spacing) {
      this.contentHBox.setSpacing(spacing);
   }

   public StringProperty titleProperty() {
      return this.titleLabel.textProperty();
   }

   public String getTitle() {
      return this.titleLabel.getText();
   }

   public void setTitle(String title) {
      this.titleLabel.setText(title);
   }

   public BooleanProperty requiredProperty() {
      return this.required;
   }

   public boolean isRequired() {
      return this.required.get();
   }

   public void setRequired(boolean required) {
      this.required.set(required);
   }
}
