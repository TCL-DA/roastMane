package com.cropster.roastingintelligence.ui.layout;

import com.cropster.roastingintelligence.ui.JavaFxController;
import com.cropster.roastingintelligence.ui.WindowManager;
import com.cropster.roastingintelligence.ui.components.MeasureEditor;
import com.cropster.roastingintelligence.ui.components.MessageBox;
import com.cropster.roastingintelligence.ui.util.DirtyListener;
import com.cropster.roastingintelligence.ui.util.Icon;
import com.cropster.roastingintelligence.ui.util.KeyboardShortcut;
import com.cropster.roastingintelligence.ui.util.PlatformScheduler;
import com.cropster.roastingintelligence.ui.util.Validators;
import com.cropster.roastingintelligence.ui.util.WindowUtil;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import org.cropster.roastingintelligence.AppModel;
import org.cropster.roastingintelligence.model.Lot;
import org.cropster.roastingintelligence.model.Profile;
import org.cropster.roastingintelligence.model.Roast;
import org.cropster.roastingintelligence.remote.CsarConnection;
import org.cropster.roastingintelligence.util.MeasureFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractBlendPanel extends BorderPane implements Initializable {
   protected static final String ORIGINAL_OWNER_KEY = "original-owner";
   protected static final String NAME_FORMAT = "[%s] %s";
   protected static final int MAX_COMMENT_LENGTH = 200;
   protected static final MeasureFormat FORMAT = MeasureFormat.getInstance();
   protected static final NumberFormat PERCENTAGE_FORMAT = NumberFormat.getPercentInstance();
   @FXML
   protected VBox headerPanel;
   @FXML
   protected MessageBox infoMessageBox;
   @FXML
   protected MessageBox warningMessageBox;
   @FXML
   protected VBox contentPanel;
   @FXML
   protected GridPane blendTable;
   @FXML
   protected Button createButton;
   private final Logger logger = LoggerFactory.getLogger(AbstractBlendPanel.class);
   protected final BooleanProperty dirty = new SimpleBooleanProperty();
   protected final Consumer<Lot> showBlendListView;
   protected final List<AbstractBlendPanel.RoastComponent> roastComponents = new ArrayList<>();
   protected AppModel appModel;
   protected CsarConnection connection;
   protected List<Roast> roastsToBlend;
   protected Task<Map<Roast, Lot>> loadLotsTask;
   private final String windowId;

   protected static void showWindow(String title, Node owner, AbstractBlendPanel blendPanel, EventHandler<WindowEvent> onCloseHandler) {
      Objects.requireNonNull(title, "Null 'title' parameter");
      Objects.requireNonNull(blendPanel, "Null 'blendPanel' parameter");
      Objects.requireNonNull(onCloseHandler, "Null 'onCloseHandler' parameter");
      Stage blendWindow = new Stage();
      blendWindow.initOwner(owner.getScene().getWindow());
      blendWindow.initModality(Modality.APPLICATION_MODAL);
      blendWindow.setTitle(title);
      blendWindow.setScene(new Scene(blendPanel));
      blendWindow.getScene().getStylesheets().add(JavaFxController.STYLE_SHEET);
      blendWindow.getScene().getAccelerators().put(KeyboardShortcut.ESC.getKeyCodeCombination(), blendWindow::hide);
      blendWindow.setOnCloseRequest(onCloseHandler);
      blendWindow.getProperties().put("original-owner", owner);
      Platform.runLater(() -> {
         sizeWindowToScene(blendWindow);
         if (WindowManager.get().register(blendWindow, blendPanel.windowId)) {
            WindowUtil.showSafeStage(blendWindow, 560.0, 0.0, 560.0, -1.0, 32.0);
            blendWindow.requestFocus();
         }
      });
   }

   protected static void hideWindow(String windowId) {
      Objects.requireNonNull(windowId, "Null 'windowId' parameter");
      WindowManager.get().getWindow(windowId).ifPresent(Window::hide);
   }

   protected static void sizeWindowToScene(Stage window) {
      Objects.requireNonNull(window, "Null 'window' parameter");
      window.sizeToScene();
      window.setResizable(false);
      double fixedWidth = window.getWidth();
      if (Double.isFinite(fixedWidth)) {
         window.setMinWidth(fixedWidth);
         window.setMaxWidth(fixedWidth);
         window.setWidth(fixedWidth);
      }

      double fixedHeight = Math.min(window.getHeight(), 600.0);
      if (Double.isFinite(fixedHeight)) {
         double safeHeight = WindowUtil.getSafeHeight(window.getHeight(), window, 32.0);
         boolean verticallyResizable = window.getHeight() > 600.0;
         window.setResizable(verticallyResizable);
         window.setMinHeight(fixedHeight);
         window.setMaxHeight(verticallyResizable ? safeHeight : fixedHeight);
         window.setHeight(Math.max(fixedHeight, safeHeight));
      }

      WindowUtil.center(window);
   }

   protected AbstractBlendPanel(Consumer<Lot> showBlendListView, String windowId) {
      this.showBlendListView = showBlendListView;
      this.windowId = windowId;
   }

   public void initialize(URL location, ResourceBundle resources) {
      this.headerPanel.visibleProperty().bind(Bindings.or(this.infoMessageBox.visibleProperty(), this.warningMessageBox.visibleProperty()));
      this.contentPanel.visibleProperty().bind(Bindings.and(this.infoMessageBox.visibleProperty().not(), this.warningMessageBox.visibleProperty().not()));
   }

   @FXML
   public void onCancel(ActionEvent ignoredEvent) {
      this.cancelLoadAndCloseWindow();
   }

   @FXML
   public void onCreate(ActionEvent ignoredEvent) {
      this.createButton.requestFocus();
      this.createBlend();
      Platform.runLater(() -> hideWindow(this.windowId));
   }

   @FXML
   public void onTryAgain(ActionEvent ignoredEvent) {
      Node owner = (Node)this.getScene().getWindow().getProperties().get("original-owner");
      this.cancelLoadAndCloseWindow();
      this.showWindowAgain(owner);
   }

   protected abstract void showWindowAgain(Node var1);

   protected abstract void createBlend();

   protected void cancelLoadAndCloseWindow() {
      this.cancelLoadAndCloseWindow(null);
   }

   protected void cancelLoadAndCloseWindow(WorkerStateEvent e) {
      this.cancelLoadTask();
      hideWindow(this.windowId);
   }

   protected void cancelLoadTask() {
      if (this.loadLotsTask.isRunning()) {
         this.loadLotsTask.cancel();
      }
   }

   protected void setRoastsToBlend(CsarConnection connection, List<Roast> roastsToBlend, AppModel appModel) {
      this.appModel = appModel;
      this.connection = connection;
      this.roastsToBlend = roastsToBlend;
   }

   protected void showWarningMessageBox(WorkerStateEvent e) {
      this.logger.warn("Problems loading lots for roasts to blend", e.getSource().getException());
      this.warningMessageBox.setVisible(true);
      this.infoMessageBox.setVisible(false);
      this.sizeWindowToScene();
   }

   protected void sizeWindowToScene() {
      sizeWindowToScene((Stage)this.getScene().getWindow());
   }

   protected void loadLotsAsynchronously(final CsarConnection connection, EventHandler<WorkerStateEvent> onLoadSucceeded) {
      this.loadLotsTask = new Task<Map<Roast, Lot>>() {
         protected Map<Roast, Lot> call() {
            Map<Roast, Lot> roastLotMap = new LinkedHashMap<>(AbstractBlendPanel.this.roastsToBlend.size());

            try {
               for (Roast roast : AbstractBlendPanel.this.roastsToBlend) {
                  Lot lot = connection.getLot(roast.getLot().getId());
                  if (lot != null) {
                     roastLotMap.put(roast, lot);
                  }
               }

               return roastLotMap;
            } catch (Exception var5) {
               if (this.isCancelled()) {
                  return roastLotMap;
               } else {
                  throw var5;
               }
            }
         }
      };
      this.loadLotsTask.setOnCancelled(this::cancelLoadAndCloseWindow);
      this.loadLotsTask.setOnFailed(this::showWarningMessageBox);
      this.loadLotsTask.setOnSucceeded(onLoadSucceeded);
      PlatformScheduler.executeBackgroundTask(this.loadLotsTask);
   }

   protected AbstractBlendPanel.RoastComponent populateRow(int row, Roast roast, Lot lot, DoubleProperty totalWeight) {
      RowConstraints rowConstraints = new RowConstraints();
      rowConstraints.setValignment(VPos.CENTER);
      rowConstraints.setVgrow(Priority.NEVER);
      this.blendTable.getRowConstraints().add(row, rowConstraints);
      Profile profile = this.appModel.getData().getProfiles().get(roast.getProfile());
      Label roastProfileLabel = new Label(profile != null ? profile.getName() : org.cropster.roastingintelligence.util.Messages.m_("No profile"));
      roastProfileLabel.getStyleClass().addAll(new String[]{"text-muted", "label--small"});
      Label roastNameLabel = new Label(String.format("[%s] %s", lot.getIdTag(), lot.getName()));
      VBox roastBox = new VBox();
      roastBox.setSpacing(4.0);
      roastBox.getChildren().addAll(new Node[]{roastProfileLabel, roastNameLabel});
      GridPane.setColumnIndex(roastBox, 0);
      GridPane.setRowIndex(roastBox, row);
      this.blendTable.getChildren().add(roastBox);
      Label ratioLabel = new Label("–");
      GridPane.setColumnIndex(ratioLabel, 1);
      GridPane.setRowIndex(ratioLabel, row);
      this.blendTable.getChildren().add(ratioLabel);
      Label availableWeightLabel = new Label(FORMAT.format(lot.getWeight()));
      GridPane.setColumnIndex(availableWeightLabel, 2);
      GridPane.setRowIndex(availableWeightLabel, row);
      this.blendTable.getChildren().add(availableWeightLabel);
      Button addAvailableWeightButton = new Button();
      addAvailableWeightButton.getStyleClass().add("button-icon");
      addAvailableWeightButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
      addAvailableWeightButton.setGraphic(Icon.LOGIN.load("icon--muted", 0.75));
      GridPane.setColumnIndex(addAvailableWeightButton, 3);
      GridPane.setRowIndex(addAvailableWeightButton, row);
      this.blendTable.getChildren().add(addAvailableWeightButton);
      MeasureEditor usedWeightEditor = new MeasureEditor(lot.getWeight().getUnit());
      usedWeightEditor.setConsumeEnterKeyEvent(true);
      usedWeightEditor.setValue(0);
      usedWeightEditor.setDisable(lot.getWeight().getAmount() <= 0.0);
      usedWeightEditor.setPadding(new Insets(0.0, 0.5, 0.0, 0.0));
      GridPane.setColumnIndex(usedWeightEditor, 4);
      GridPane.setRowIndex(usedWeightEditor, row);
      this.blendTable.getChildren().add(usedWeightEditor);
      ratioLabel.textProperty().bind(Bindings.createStringBinding(() -> {
         double tw = totalWeight.get();
         double usedWeight = usedWeightEditor.getValue() != null ? usedWeightEditor.getValue().doubleValue() : 0.0;
         return tw > 0.0 && lot.getWeight().getAmount() > 0.0 && usedWeight > 0.0 ? PERCENTAGE_FORMAT.format(usedWeight / tw) : "–";
      }, new Observable[]{totalWeight}));
      DirtyListener<String> weightEditorDirty = new DirtyListener<>(null);
      weightEditorDirty.setOriginalValue(usedWeightEditor.getInput().getText());
      usedWeightEditor.getInput().textProperty().addListener(weightEditorDirty);
      Validators.Validator validator = Validators.setValidator(usedWeightEditor, lot.getWeight().getAmount());
      AbstractBlendPanel.RoastComponent roastComponent = new AbstractBlendPanel.RoastComponent(
         roast, lot, addAvailableWeightButton, usedWeightEditor, weightEditorDirty, validator
      );
      this.roastComponents.add(roastComponent);
      addAvailableWeightButton.setOnAction(e -> usedWeightEditor.setValue(lot.getWeight().getAmount()));
      return roastComponent;
   }

   static {
      PERCENTAGE_FORMAT.setMinimumFractionDigits(0);
      PERCENTAGE_FORMAT.setMaximumFractionDigits(0);
   }

   protected static record RoastComponent(
      Roast roast, Lot lot, Button addAvailabeButton, MeasureEditor editor, DirtyListener<String> dirtyListener, Validators.Validator validator
   ) {
   }
}
