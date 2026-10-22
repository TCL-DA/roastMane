package com.cropster.roastingintelligence.ui.components;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javafx.beans.DefaultProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@DefaultProperty("buttons")
public class DragAndDropLabelEditor extends BorderPane {
   private static final DataFormat DATA_FORMAT = new DataFormat(new String[]{"dragableLabel"});
   private static final String[] OPTIONAL_DRAG_TARGETS = new String[]{"C", "D.1", "D.2", "D.3", "E.1", "E.2", "E.3"};
   private final Logger logger = LoggerFactory.getLogger(DragAndDropLabelEditor.class);
   @FXML
   private VBox dragSourcePanel;
   @FXML
   private GridPane dragTargetPanel;
   @FXML
   private GridPane optionalPanel;
   @FXML
   private ImageView optionalPanelSnapshot;
   @FXML
   private ImageView qrCodeTop;
   @FXML
   private ImageView qrCodeBottom;
   @FXML
   private HBox buttonBar;
   private final ObjectProperty<RoastLabel.QrCodePosition> qrCodePosition = new SimpleObjectProperty();
   private final BooleanProperty reducedStyle = new SimpleBooleanProperty();
   private final Map<String, DragSourceLabel> dragSources = new HashMap<>();
   private final Map<String, DragTargetLabel> dragTargets = new HashMap<>();
   private final Map<String, String> dragTargetToSourceMapping = new HashMap<>();
   private final ObservableList<Button> buttons = FXCollections.observableArrayList();
   private WritableImage snapshot;

   public DragAndDropLabelEditor() {
      JavaFxUtil.loadComponent(this);
      this.buttons.addListener(c -> this.buttonBar.getChildren().setAll(this.buttons));
      this.qrCodeTop.setImage(QrCodeRenderer.renderImage(100, 100, "https://c-sar.cropster.com"));
      this.qrCodeTop.visibleProperty().bind(this.qrCodePosition.isEqualTo(RoastLabel.QrCodePosition.TOP));
      this.qrCodeBottom.setImage(QrCodeRenderer.renderImage(100, 100, "https://c-sar.cropster.com"));
      this.qrCodeBottom.visibleProperty().bind(this.qrCodePosition.isEqualTo(RoastLabel.QrCodePosition.BOTTOM));
      this.optionalPanel.managedProperty().bind(this.optionalPanel.visibleProperty());
      this.optionalPanelSnapshot.managedProperty().bind(this.optionalPanelSnapshot.visibleProperty());
      this.optionalPanelSnapshot.setPreserveRatio(false);
      this.reducedStyle.addListener((s, o, n) -> {
         this.optionalPanel.setVisible(!n);
         this.optionalPanelSnapshot.setVisible(false);
         if (n) {
            for (String targetName : OPTIONAL_DRAG_TARGETS) {
               this.unlinkTarget(targetName);
            }

            this.dragTargets.get("F").setAlias("C");
         } else {
            this.dragTargets.get("F").setAlias(null);
         }
      });
      this.dragSourcePanel.getChildren().clear();

      for (RoastLabel.RoastLabelComponent component : RoastLabel.getComponents()) {
         DragSourceLabel dragSource = new DragSourceLabel(component);
         dragSource.setId(component.getId());
         this.dragSourcePanel.getChildren().add(dragSource);
         this.dragSources.put(dragSource.getId(), dragSource);
      }

      this.dragTargetPanel.lookupAll(".dragtarget-label").stream().forEach(n -> this.dragTargets.put(((DragTargetLabel)n).getName(), (DragTargetLabel)n));
      this.dragTargets.values().forEach(t -> t.getStyleClass().add("dragtarget-label--empty"));

      for (DragSourceLabel dragableSource : this.dragSources.values()) {
         dragableSource.setOnDragDetected(e -> {
            ClipboardContent content = new ClipboardContent();
            content.put(DATA_FORMAT, dragableSource.getId());
            SnapshotParameters params = new SnapshotParameters();
            WritableImage dragImage = new WritableImage((int)dragableSource.getWidth(), (int)dragableSource.getHeight());
            Dragboard dragBoard = dragableSource.startDragAndDrop(new TransferMode[]{TransferMode.LINK});
            dragBoard.setContent(content);
            dragBoard.setDragView(dragableSource.snapshot(params, dragImage));
         });
         dragableSource.setOnDragDone(e -> {
            if (e.getTransferMode() == TransferMode.LINK) {
               Dragboard dragBoard = e.getDragboard();
               String dragSourceId = (String)dragBoard.getContent(DATA_FORMAT);
               Label dragSourcex = this.dragSources.get(dragSourceId);
               dragSourcex.setDisable(true);
            }
         });
      }

      for (DragTargetLabel dragTarget : this.dragTargets.values()) {
         dragTarget.setOnCloseAction(e -> {
            dragTarget.reset();
            String dragSourceId = this.dragTargetToSourceMapping.remove(dragTarget.getName());
            this.dragSources.get(dragSourceId).setDisable(false);
         });
         dragTarget.setOnDragOver(e -> {
            if (e.getGestureSource() != dragTarget && e.getDragboard().hasContent(DATA_FORMAT)) {
               e.acceptTransferModes(new TransferMode[]{TransferMode.LINK});
            }
         });
         dragTarget.setOnDragEntered(e -> {
            if (e.getDragboard().hasContent(DATA_FORMAT)) {
               dragTarget.getStyleClass().add("dragtarget-label--hover");
            }
         });
         dragTarget.setOnDragExited(e -> dragTarget.getStyleClass().remove("dragtarget-label--hover"));
         dragTarget.setOnDragDropped(e -> {
            boolean success = false;
            Dragboard dragBoard = e.getDragboard();
            if (dragBoard.hasContent(DATA_FORMAT)) {
               String dragSourceId = (String)dragBoard.getContent(DATA_FORMAT);
               success = this.linkDragSourceToTarget(dragSourceId, dragTarget.getName());
            }

            e.setDropCompleted(success);
         });
      }
   }

   public ObjectProperty<RoastLabel.QrCodePosition> qrCodePositionProperty() {
      return this.qrCodePosition;
   }

   public RoastLabel.QrCodePosition getQrCodePosition() {
      return (RoastLabel.QrCodePosition)this.qrCodePosition.get();
   }

   public void setQrCodePosition(RoastLabel.QrCodePosition qrCodePosition) {
      this.qrCodePosition.set(qrCodePosition);
   }

   public BooleanProperty reducedStyleProperty() {
      return this.reducedStyle;
   }

   public boolean getReducedStyle() {
      return this.reducedStyle.get();
   }

   public void setReducedStyle(boolean reducedStyle) {
      this.reducedStyle.set(reducedStyle);
   }

   public ObservableList<Button> getButtons() {
      return this.buttons;
   }

   private boolean linkDragSourceToTarget(String sourceId, String targetName) {
      DragTargetLabel dragTarget = this.dragTargets.get(targetName);
      if (dragTarget != null) {
         DragSourceLabel dragableLabel = this.dragSources.get(sourceId);
         if (dragableLabel != null) {
            dragTarget.setText(dragableLabel.getText());
            if (this.dragTargetToSourceMapping.containsKey(targetName)) {
               String previousSourceId = this.dragTargetToSourceMapping.get(targetName);
               Label previousLabel = this.dragSources.get(previousSourceId);
               previousLabel.setDisable(false);
            }

            dragableLabel.setDisable(true);
            dragTarget.getStyleClass().removeIf(s -> s.equals("dragtarget-label--empty"));
            this.dragTargetToSourceMapping.put(targetName, sourceId);
            return true;
         }

         this.logger.warn("Invalid drag source: {}", sourceId);
      } else {
         this.logger.warn("Invalid drag target: {}", targetName);
      }

      return false;
   }

   private void unlinkTarget(String targetName) {
      if (this.dragTargetToSourceMapping.containsKey(targetName)) {
         String sourceId = this.dragTargetToSourceMapping.get(targetName);
         DragSourceLabel dragSource = this.dragSources.get(sourceId);
         dragSource.setDisable(false);
         DragTargetLabel dragTarget = this.dragTargets.get(targetName);
         dragTarget.reset();
         this.dragTargetToSourceMapping.remove(targetName);
      }
   }

   private void clearLabelMapping() {
      this.dragTargetToSourceMapping.clear();
      this.dragSources.values().forEach(s -> s.setDisable(false));
      this.dragTargets.values().forEach(t -> t.reset());
   }

   public Map<String, String> getLabelMapping() {
      return Collections.unmodifiableMap(this.dragTargetToSourceMapping);
   }

   public void setLabelMapping(Map<String, String> labelMapping) {
      this.clearLabelMapping();

      for (Entry<String, String> e : labelMapping.entrySet()) {
         this.linkDragSourceToTarget(e.getValue(), e.getKey());
      }
   }
}
