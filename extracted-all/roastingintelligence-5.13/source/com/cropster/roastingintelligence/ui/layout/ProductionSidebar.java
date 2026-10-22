package com.cropster.roastingintelligence.ui.layout;

import com.cropster.roastingintelligence.ui.JavaFxApplicationState;
import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.components.BlendLabel;
import com.cropster.roastingintelligence.ui.components.KeyCodeCombinationLabel;
import com.cropster.roastingintelligence.ui.components.ProductionEditor;
import com.cropster.roastingintelligence.ui.components.ProductionErrorLabel;
import com.cropster.roastingintelligence.ui.components.ProductionLabel;
import com.cropster.roastingintelligence.ui.components.RandomShortcutPane;
import com.cropster.roastingintelligence.ui.list.EmptyStateList;
import com.cropster.roastingintelligence.ui.util.AbstractListCell;
import com.cropster.roastingintelligence.ui.util.ActionBuilder;
import com.cropster.roastingintelligence.ui.util.Icon;
import com.cropster.roastingintelligence.ui.util.KeyboardShortcut;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import javafx.application.Platform;
import javafx.beans.DefaultProperty;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination.Modifier;
import javafx.scene.input.KeyCombination.ModifierValue;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import org.controlsfx.control.action.Action;
import org.cropster.roastingintelligence.AppModel;
import org.cropster.roastingintelligence.model.Lot;
import org.cropster.roastingintelligence.model.LotConsistencyCheck;
import org.cropster.roastingintelligence.model.Roast;
import org.cropster.roastingintelligence.model.Scheduled;
import org.cropster.roastingintelligence.remote.CsarConnection;

@DefaultProperty("toggleButtons")
public class ProductionSidebar extends DetachableSidebar implements Initializable {
   private static final String EMPTY_STATE_MESSAGE = org.cropster.roastingintelligence.util.Messages.m_("No roasts to display");
   private static final String SELECT_ALL_TEXT = org.cropster.roastingintelligence.util.Messages.m_("Select all");
   private static final String UNSELECT_ALL_TEXT = org.cropster.roastingintelligence.util.Messages.m_("Unselect all");
   private static final String SELECTED_TEXT = "{0} selected";
   private static final int SINGLE_SELECTION_SIZE = 1;
   @FXML
   private Region separatorSpacer;
   @FXML
   private HBox toggleButtonBar;
   @FXML
   private KeyCodeCombinationLabel doubleClickKeyCodeCombinationLabel;
   @FXML
   private KeyCodeCombinationLabel clickKeyCodeCombinationLabel;
   @FXML
   private EmptyStateList<Object> productionEmptyStateList;
   @FXML
   private Pane bulkActionBar;
   @FXML
   private Button batchSelectButton;
   @FXML
   private Button batchMoreButton;
   @FXML
   private Button blendSelectButton;
   @FXML
   private Button blendMoreButton;
   @FXML
   private ToggleButton filterButtonAll;
   @FXML
   private ToggleButton filterButtonRoasted;
   @FXML
   private ToggleButton filterButtonScheduled;
   @FXML
   private ToggleButton toggleButtonBatches;
   @FXML
   private ToggleButton toggleButtonBlends;
   @FXML
   private VBox productionListBox;
   @FXML
   private EmptyStateList<Lot> blendEmptyStateList;
   @FXML
   private Circle customizedIndicator;
   private final BooleanProperty blendsSelected = new SimpleBooleanProperty(false);
   private final StringProperty numBatchSelected = new SimpleStringProperty(org.cropster.roastingintelligence.util.Messages.m_("{0} selected", 0));
   private final StringProperty numBlendSelected = new SimpleStringProperty(org.cropster.roastingintelligence.util.Messages.m_("{0} selected", 0));
   private final ProductionEditor editor = new ProductionEditor();
   private FilteredList<Object> filteredProductionList;
   private FilteredList<Object> roastList;
   private ListView<Object> productionView;
   private ListView<Lot> blendView;
   private AppModel appModel;
   private JavaFxApplicationState state;
   private CsarConnection connection;
   private Consumer<Roast> finalValuesUpdater;
   private Consumer<Roast> roastInfoUpdater;
   private Action editRoastAction;
   private Action blendManuallyAction;
   private Action blendWithProfileAction;
   private Action markRoastAsReferenceAction;
   private Action printRoastLabelAction;
   private Action printSelectedRoastLabelsAction;
   private Action printSelectedBlendLabelsAction;
   private Action openRoastOnlineAction;
   private Action showRoastInfoAction;
   private Action openBlendOnlineAction;
   private Action printBlendLabelAction;
   private Runnable printSelectedRoastLabelsConsumer;
   private Runnable printSelectedBlendLabelsConsumer;

   public ProductionSidebar() {
      JavaFxUtil.loadComponent(this);
   }

   @FXML
   public void filterAll() {
      this.updateProductionListFilter(null, org.cropster.roastingintelligence.util.Messages.m_("Your production is up to date"));
   }

   @FXML
   public void filterScheduled() {
      this.updateProductionListFilter(Scheduled.class::isInstance, EMPTY_STATE_MESSAGE);
   }

   @FXML
   public void filterRoasted() {
      this.updateProductionListFilter(Roast.class::isInstance, EMPTY_STATE_MESSAGE);
   }

   @FXML
   public void showBatches() {
      this.setBlendsSelected(false);
   }

   @FXML
   public void showBlends() {
      this.setBlendsSelected(true);
   }

   @FXML
   public void showBatchMoreMenu(ActionEvent ae) {
      Node owner = (Node)ae.getSource();
      ContextMenu contextMenu = this.createBatchContextMenu(owner);
      contextMenu.show(owner, Side.BOTTOM, 0.0, 0.0);
   }

   private ContextMenu createBatchContextMenu(Node owner) {
      ContextMenu contextMenu = JavaFxUtil.createContextMenu(owner);
      contextMenu.getItems().add(ActionBuilder.menuItemFrom(this.printSelectedRoastLabelsAction));
      contextMenu.getItems().add(ActionBuilder.menuItemFrom(this.blendManuallyAction));
      contextMenu.getItems().add(ActionBuilder.menuItemFrom(this.blendWithProfileAction));
      return contextMenu;
   }

   private ContextMenu createProductionListCellContextMenu(Node owner) {
      ContextMenu contextMenu = JavaFxUtil.createContextMenu(owner);
      contextMenu.getItems().add(ActionBuilder.menuItemFrom(this.markRoastAsReferenceAction));
      contextMenu.getItems().add(ActionBuilder.menuItemFrom(this.printRoastLabelAction));
      contextMenu.getItems().add(ActionBuilder.menuItemFrom(this.openRoastOnlineAction));
      contextMenu.getItems().add(ActionBuilder.menuItemFrom(this.showRoastInfoAction));
      contextMenu.getItems().add(ActionBuilder.menuItemFrom(this.editRoastAction));
      return contextMenu;
   }

   @FXML
   public void showBlendMoreMenu(ActionEvent ae) {
      Node owner = (Node)ae.getSource();
      ContextMenu contextMenu = JavaFxUtil.createContextMenu(owner);
      contextMenu.getItems().add(ActionBuilder.menuItemFrom(this.printSelectedBlendLabelsAction));
      contextMenu.show(owner, Side.BOTTOM, 0.0, 0.0);
   }

   @FXML
   public void showCustomizeInformation(ActionEvent ae) {
      ProductionLabel.customizeInformation(this.productionView, (Button)ae.getSource());
   }

   @Override
   public void initialize(URL location, ResourceBundle resources) {
      super.initialize(location, resources);
      this.doubleClickKeyCodeCombinationLabel.setCustomKeyCode("Double-Click", new KeyCodeCombination(KeyCode.ALL_CANDIDATES, new Modifier[0]));
      this.clickKeyCodeCombinationLabel
         .setCustomKeyCode(
            "Click", new KeyCodeCombination(KeyCode.ALL_CANDIDATES, ModifierValue.UP, ModifierValue.UP, ModifierValue.UP, ModifierValue.UP, ModifierValue.DOWN)
         );
      this.separatorSpacer.visibleProperty().bind(this.toggleButtonBar.visibleProperty().not());
      this.productionView = this.productionEmptyStateList.getListView();
      this.productionView.setCellFactory(lv -> new ProductionSidebar.ProductionListCell());
      this.productionView.setEditable(true);
      this.productionView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
      this.productionView.setOnKeyReleased(e -> {
         if (e.getCode() == KeyCode.ESCAPE && !e.isAltDown() && !e.isControlDown() && !e.isMetaDown() && !e.isShiftDown() && !e.isShortcutDown()) {
            this.unselectAllRoasts();
            e.consume();
         }
      });
      this.batchSelectButton.setText(SELECT_ALL_TEXT);
      this.productionView.getSelectionModel().getSelectedItems().addListener(this::handleRoastSelectionChanged);
      this.batchSelectButton.setOnAction(e -> {
         if (SELECT_ALL_TEXT.equals(this.batchSelectButton.getText())) {
            this.extendSelectionToAllRoasts();
         } else {
            this.unselectAllRoasts();
         }
      });
      this.blendView = this.blendEmptyStateList.getListView();
      this.blendView.setCellFactory(lv -> new ProductionSidebar.BlendListCell());
      this.blendView.setEditable(true);
      this.blendView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
      this.blendSelectButton.setText(SELECT_ALL_TEXT);
      ListChangeListener<Lot> blendSelectionChanged = c -> {
         int selectionSize = this.blendView.getSelectionModel().getSelectedIndices().size();
         this.numBlendSelected.set(org.cropster.roastingintelligence.util.Messages.m_("{0} selected", selectionSize));
         this.blendSelectButton.setText(selectionSize != this.blendView.getItems().size() ? SELECT_ALL_TEXT : UNSELECT_ALL_TEXT);
         this.printSelectedBlendLabelsAction.setDisabled(selectionSize == 0);
      };
      this.blendView.getSelectionModel().getSelectedItems().addListener(blendSelectionChanged);
      this.blendSelectButton.setOnAction(e -> {
         MultipleSelectionModel<Lot> selectionModel = this.blendView.getSelectionModel();
         if (selectionModel.getSelectedIndices().size() != this.blendView.getItems().size()) {
            selectionModel.selectAll();
         } else {
            selectionModel.clearSelection();
         }
      });
      this.bulkActionBar.visibleProperty().bind(this.blendsSelected.not().and(this.filterButtonScheduled.selectedProperty().not()).or(this.blendsSelected));
      this.customizedIndicator
         .visibleProperty()
         .bind(
            ProductionLabel.showRoastNotesProperty()
               .not()
               .or(ProductionLabel.showGreenInventoryProperty().not())
               .or(ProductionLabel.showBlendContributionsProperty().not())
               .or(ProductionLabel.showRoastGoalsProperty().not())
               .or(ProductionLabel.showRoastTechnicianProperty().not())
         );
      this.blendSelectButton.visibleProperty().unbind();
      this.blendSelectButton.setVisible(false);
      this.blendMoreButton.visibleProperty().unbind();
      this.blendMoreButton.setVisible(false);
   }

   private void handleRoastSelectionChanged(Change<?> change) {
      MultipleSelectionModel<?> selectionModel = this.productionView.getSelectionModel();
      int selectionSize = selectionModel.getSelectedIndices().size();
      List<?> selectedRoasts = selectionModel.getSelectedItems().stream().filter(Roast.class::isInstance).toList();
      int selectedRoastsSize = selectedRoasts.size();
      if (selectionSize == 1) {
         Object selectedItem = selectionModel.getSelectedItem();
         if (selectedItem instanceof Roast) {
            this.productionView.setContextMenu(this.createProductionListCellContextMenu(this.productionView));
         } else {
            this.productionView.setContextMenu(null);
         }
      } else if (selectionSize > 1) {
         if (selectedRoastsSize > 0 && selectedRoastsSize < selectionSize) {
            ObservableList<Object> viewItems = this.productionView.getItems();
            int[] roastIndices = selectedRoasts.stream().mapToInt(viewItems::indexOf).toArray();
            int[] remainingIndices = new int[roastIndices.length - 1];
            System.arraycopy(roastIndices, 1, remainingIndices, 0, remainingIndices.length);
            Platform.runLater(() -> {
               selectionModel.clearSelection();
               selectionModel.selectIndices(roastIndices[0], remainingIndices);
            });
            return;
         }

         if (selectedRoastsSize == 0) {
            this.unselectAllRoasts();
            Platform.runLater(this::unselectUnnecessaryScheduled);
            return;
         }

         this.productionView.setContextMenu(this.createBatchContextMenu(this.productionView));
      }

      this.numBatchSelected.set(org.cropster.roastingintelligence.util.Messages.m_("{0} selected", selectionSize));
      long totalRoastsCount = this.productionView.getItems().stream().filter(Roast.class::isInstance).count();
      this.batchSelectButton.setText((long)selectedRoastsSize != totalRoastsCount ? SELECT_ALL_TEXT : UNSELECT_ALL_TEXT);
      long printableSize = selectionModel.getSelectedItems().stream().filter(Roast.class::isInstance).map(Roast.class::cast).filter(Roast::isSynced).count();
      this.printSelectedRoastLabelsAction.setDisabled(printableSize == 0L);
   }

   private void extendSelectionToAllRoasts() {
      MultipleSelectionModel<Object> selectionModel = this.productionView.getSelectionModel();
      ObservableList<Object> items = this.productionView.getItems();
      int[] toBeSelected = IntStream.range(0, items.size()).filter(i -> items.get(i) instanceof Roast).filter(i -> !selectionModel.isSelected(i)).toArray();
      if (toBeSelected.length != 0) {
         int[] tbsResidual = new int[toBeSelected.length - 1];
         System.arraycopy(toBeSelected, 1, tbsResidual, 0, tbsResidual.length);
         selectionModel.selectIndices(toBeSelected[0], tbsResidual);
      }
   }

   private void unselectAllRoasts() {
      MultipleSelectionModel<Object> selectionModel = this.productionView.getSelectionModel();
      ObservableList<Object> items = this.productionView.getItems();
      IntStream.range(0, items.size()).filter(i -> items.get(i) instanceof Roast).filter(selectionModel::isSelected).forEach(selectionModel::clearSelection);
   }

   private void unselectUnnecessaryScheduled() {
      MultipleSelectionModel<Object> selectionModel = this.productionView.getSelectionModel();
      ObservableList<Object> items = this.productionView.getItems();
      IntStream.range(0, items.size())
         .filter(i -> items.get(i) instanceof Scheduled)
         .filter(selectionModel::isSelected)
         .filter(i -> !Objects.equals(this.state.getScheduled(), ((Scheduled)items.get(i)).getId()))
         .forEach(selectionModel::clearSelection);
   }

   public HBox getToggleButtonBar() {
      return this.toggleButtonBar;
   }

   public Action getBlendManuallyAction() {
      return this.blendManuallyAction;
   }

   public Action getBlendWithProfileAction() {
      return this.blendWithProfileAction;
   }

   public Action getMarkRoastAsReferenceAction() {
      return this.markRoastAsReferenceAction;
   }

   public BooleanProperty blendsSelectedProperty() {
      return this.blendsSelected;
   }

   public boolean isBlendsSelected() {
      return this.blendsSelected.get();
   }

   public void setBlendsSelected(boolean blendsSelected) {
      this.blendsSelected.set(blendsSelected);
   }

   public EmptyStateList<Object> getProductionEmptyStateList() {
      return this.productionEmptyStateList;
   }

   public ListView<Object> getProductionView() {
      return this.productionView;
   }

   public void setProductionViewItems(ObservableList<Object> items) {
      this.roastList = new FilteredList(items, Roast.class::isInstance);
      this.filteredProductionList = new FilteredList(items);
      this.productionEmptyStateList.setListViewItems(this.filteredProductionList);
      this.filterButtonAll
         .textProperty()
         .bind(Bindings.createStringBinding(() -> items.size() + " " + org.cropster.roastingintelligence.util.Messages.m_("Total"), new Observable[]{items}));
      this.filterButtonRoasted
         .textProperty()
         .bind(
            Bindings.createStringBinding(
               () -> items.stream().filter(Roast.class::isInstance).count() + " " + org.cropster.roastingintelligence.util.Messages.m_("Roasted"),
               new Observable[]{items}
            )
         );
      this.filterButtonScheduled
         .textProperty()
         .bind(
            Bindings.createStringBinding(
               () -> items.stream().filter(Scheduled.class::isInstance).count() + " " + org.cropster.roastingintelligence.util.Messages.m_("Remaining"),
               new Observable[]{items}
            )
         );
      this.toggleButtonBatches
         .textProperty()
         .bind(
            Bindings.createStringBinding(
               () -> items.size() + " " + org.cropster.roastingintelligence.util.Messages.p_("batch", "batches", items.size()), new Observable[]{items}
            )
         );
      this.roastList.addListener(c -> this.updateBatchSelectButton());
      this.filterButtonScheduled.selectedProperty().addListener((s, o, n) -> this.updateBatchSelectButton());
   }

   private void updateBatchSelectButton() {
      this.batchSelectButton.setDisable(this.roastList.isEmpty() || this.filterButtonScheduled.isSelected());
   }

   public EmptyStateList<Lot> getBlendEmptyStateList() {
      return this.blendEmptyStateList;
   }

   public ListView<Lot> getBlendView() {
      return this.blendView;
   }

   public void setBlendViewItems(ObservableList<Lot> items) {
      this.blendEmptyStateList.setListViewItems(items);
      this.blendSelectButton.disableProperty().bind(Bindings.size(this.blendView.getItems()).isEqualTo(0));
      this.toggleButtonBlends
         .textProperty()
         .bind(
            Bindings.createStringBinding(
               () -> items.size() + " " + org.cropster.roastingintelligence.util.Messages.p_("blend", "blends", items.size()), new Observable[]{items}
            )
         );
      items.addListener(c -> this.productionView.refresh());
   }

   public ToggleButton getToggleButtonBlends() {
      return this.toggleButtonBlends;
   }

   public RandomShortcutPane getRandomShortcutPane() {
      return this.productionEmptyStateList.getRandomShortcutPane();
   }

   public ReadOnlyStringProperty numBatchSelectedProperty() {
      return this.numBatchSelected;
   }

   public String getNumBatchSelected() {
      return (String)this.numBatchSelected.get();
   }

   public ReadOnlyStringProperty numBlendSelectedProperty() {
      return this.numBlendSelected;
   }

   public String getNumBlendSelected() {
      return (String)this.numBlendSelected.get();
   }

   public Action getPrintSelectedRoastLabelsAction() {
      return this.printSelectedRoastLabelsAction;
   }

   public void setPrintSelectedRoastLabelsConsumer(Runnable printSelectedRoastLabelsConsumer) {
      this.printSelectedRoastLabelsConsumer = printSelectedRoastLabelsConsumer;
   }

   public Action getPrintSelectedBlendLabelsAction() {
      return this.printSelectedBlendLabelsAction;
   }

   public void setPrintSelectedBlendLabelsConsumer(Runnable printSelectedBlendLabelsAction) {
      this.printSelectedBlendLabelsConsumer = printSelectedBlendLabelsAction;
   }

   private void updateProductionListFilter(Predicate<Object> filter, String emptyMessage) {
      this.cancelEdit();
      this.productionView.setDisable(true);
      this.filteredProductionList.setPredicate(filter);
      this.productionEmptyStateList.setEmptyStateMessage(emptyMessage);
      this.productionView.setDisable(false);
   }

   private void cancelEdit() {
      if (this.productionView.getEditingIndex() != -1) {
         this.productionView.edit(-1);
      }
   }

   public void init(
      AppModel appModel, JavaFxApplicationState state, CsarConnection connection, Consumer<Roast> finalValuesUpdater, Consumer<Roast> roastInfoUpdater
   ) {
      this.appModel = appModel;
      this.state = state;
      this.connection = connection;
      this.finalValuesUpdater = finalValuesUpdater;
      this.roastInfoUpdater = roastInfoUpdater;
   }

   public void initActions(
      Consumer<Roast> markAsReferenceConsumer,
      Consumer<Roast> printRoastLabelConsumer,
      Consumer<Roast> openRoastOnlineConsumer,
      Consumer<Roast> showRoastInfoConsumer,
      Consumer<Lot> printBlendLabelConsumer,
      Consumer<Lot> openBlendOnlineConsumer
   ) {
      this.markRoastAsReferenceAction = new ActionBuilder(
            org.cropster.roastingintelligence.util.Messages.m_("Mark as reference"),
            e -> markAsReferenceConsumer.accept((Roast)this.productionView.getSelectionModel().getSelectedItem())
         )
         .graphics(Icon.FAVOURITE.load())
         .disabled(true)
         .build();
      this.printRoastLabelAction = new ActionBuilder(
            org.cropster.roastingintelligence.util.Messages.m_("Print roast label"),
            e -> printRoastLabelConsumer.accept((Roast)this.productionView.getSelectionModel().getSelectedItem())
         )
         .accelerator(KeyboardShortcut.CTRL_P.getKeyCodeCombination())
         .graphics(Icon.PRINT.load())
         .build();
      this.openRoastOnlineAction = new ActionBuilder(
            org.cropster.roastingintelligence.util.Messages.m_("Open in browser"),
            e -> openRoastOnlineConsumer.accept((Roast)this.productionView.getSelectionModel().getSelectedItem())
         )
         .graphics(Icon.OPEN_OUT.load())
         .build();
      this.showRoastInfoAction = new ActionBuilder(
            org.cropster.roastingintelligence.util.Messages.m_("Expand batch information"),
            e -> showRoastInfoConsumer.accept((Roast)this.productionView.getSelectionModel().getSelectedItem())
         )
         .graphics(Icon.EXPAND.load())
         .build();
      this.editRoastAction = new ActionBuilder(
            org.cropster.roastingintelligence.util.Messages.m_("Edit roast"),
            () -> this.productionView.edit(this.productionView.getSelectionModel().getSelectedIndex())
         )
         .graphics(Icon.EDIT.load())
         .build();
      this.editRoastAction
         .disabledProperty()
         .bind(
            Bindings.size(this.productionView.getSelectionModel().getSelectedItems())
               .greaterThan(1)
               .or(this.productionView.editingIndexProperty().greaterThanOrEqualTo(0))
         );
      this.printSelectedRoastLabelsAction = new ActionBuilder(org.cropster.roastingintelligence.util.Messages.m_("Print roast label"), () -> {
            if (this.printSelectedRoastLabelsConsumer != null) {
               this.printSelectedRoastLabelsConsumer.run();
            }
         })
         .longText(org.cropster.roastingintelligence.util.Messages.m_("Print selected roast label"))
         .accelerator(KeyboardShortcut.CTRL_P.getKeyCodeCombination())
         .graphics(Icon.PRINT.load())
         .disabled(true)
         .build();
      this.blendManuallyAction = new ActionBuilder(
            org.cropster.roastingintelligence.util.Messages.m_("Blend manually"),
            e -> ManualBlendPanel.showWindow(
                  this,
                  this.connection,
                  this.productionView.getSelectionModel().getSelectedItems().stream().filter(Roast.class::isInstance).map(Roast.class::cast).toList(),
                  this.appModel,
                  blend -> {
                     this.toggleButtonBlends.fire();
                     this.blendView.getSelectionModel().select(blend);
                     this.blendView.scrollTo(blend);
                  }
               )
         )
         .graphics(Icon.MERGE.load())
         .disabled(true)
         .build();
      this.blendWithProfileAction = new ActionBuilder(
            org.cropster.roastingintelligence.util.Messages.m_("Blend with profile"),
            e -> BlendWithProfilePanel.showWindow(
                  this,
                  this.connection,
                  this.productionView.getSelectionModel().getSelectedItems().stream().filter(Roast.class::isInstance).map(Roast.class::cast).toList(),
                  this.appModel,
                  blend -> {
                     this.toggleButtonBlends.fire();
                     this.blendView.getSelectionModel().select(blend);
                     this.blendView.scrollTo(blend);
                  }
               )
         )
         .graphics(Icon.MERGE.load())
         .disabled(true)
         .build();
      this.printBlendLabelAction = new ActionBuilder(
            org.cropster.roastingintelligence.util.Messages.m_("Print blend label"),
            e -> printBlendLabelConsumer.accept((Lot)this.blendView.getSelectionModel().getSelectedItem())
         )
         .accelerator(KeyboardShortcut.CTRL_P.getKeyCodeCombination())
         .graphics(Icon.PRINT.load())
         .build();
      this.openBlendOnlineAction = new ActionBuilder(
            org.cropster.roastingintelligence.util.Messages.m_("Open in browser"),
            e -> openBlendOnlineConsumer.accept((Lot)this.blendView.getSelectionModel().getSelectedItem())
         )
         .graphics(Icon.OPEN_OUT.load())
         .build();
      this.printSelectedBlendLabelsAction = new ActionBuilder(org.cropster.roastingintelligence.util.Messages.m_("Print blend label"), () -> {
            if (this.printSelectedBlendLabelsConsumer != null) {
               this.printSelectedBlendLabelsConsumer.run();
            }
         })
         .longText(org.cropster.roastingintelligence.util.Messages.m_("Print selected blend label"))
         .accelerator(KeyboardShortcut.CTRL_P.getKeyCodeCombination())
         .graphics(Icon.PRINT.load())
         .build();
   }

   public void addLotToBlendList(Lot blendLot) {
      Objects.requireNonNull(blendLot, "Null 'blendLot' parameter");
      this.blendView.getItems().add(blendLot);
   }

   public void setLotsToBlendList(Collection<Lot> blendLots) {
      Objects.requireNonNull(blendLots, "Null 'blendLots' parameter");
      this.blendView.getItems().setAll(blendLots);
   }

   public class BlendListCell extends AbstractListCell<Lot> {
      private final BlendLabel viewer = new BlendLabel(this::printSelectedBlendLabelsAction, this::openInBrowserAction);

      public BlendListCell() {
         this.setEditable(false);
         this.setPrefWidth(0.0);
      }

      protected void initEditor(Lot item) {
      }

      protected void initViewer(Lot item) {
         if (item != null) {
            this.viewer.init(item, ProductionSidebar.this.roastList);
            this.setGraphic(this.viewer);
         } else {
            this.viewer.resetLabels();
            this.setGraphic(null);
         }
      }

      private void openInBrowserAction(ActionEvent ae) {
         JavaFxUtil.selectItemExclusively(ProductionSidebar.this.blendView, (Lot)this.getItem());
         ProductionSidebar.this.openBlendOnlineAction.handle(ae);
      }

      private void printSelectedBlendLabelsAction(ActionEvent ae) {
         JavaFxUtil.selectItemExclusively(ProductionSidebar.this.blendView, (Lot)this.getItem());
         ProductionSidebar.this.printBlendLabelAction.handle(ae);
      }
   }

   public class ProductionListCell extends AbstractListCell<Object> {
      private final ProductionLabel viewer = new ProductionLabel(this::showMenu, this::edit);

      ProductionListCell() {
         this.viewer.getEditButton().disableProperty().bind(ProductionSidebar.this.editRoastAction.disabledProperty());
         this.viewer.getMenuButton().disableProperty().bind(ProductionSidebar.this.productionView.editingIndexProperty().greaterThanOrEqualTo(0));
         this.setPrefWidth(0.0);
      }

      @Override
      public void startEdit() {
         if (ProductionSidebar.this.editor.isDirty()) {
            ProductionSidebar.this.productionView.scrollTo(ProductionSidebar.this.editor.getRoast());
            ProductionSidebar.this.editor.reset();
         }

         super.startEdit();
      }

      @Override
      public void cancelEdit() {
         ProductionSidebar.this.editor.reset();
         super.cancelEdit();
      }

      @Override
      protected void initEditor(Object item) {
         if (item instanceof Roast roast && roast.isSynced()) {
            ProductionSidebar.this.editor
               .startEditing(
                  roast, ProductionSidebar.this.appModel, this::updateFinalValues, () -> this.onSuccessfulEdit(roast), this.blendsFor(roast).isEmpty()
               );
            ProductionSidebar.this.editor.getCancelButton().setOnAction(e -> this.cancelEdit());
            this.setGraphic(ProductionSidebar.this.editor);
            return;
         }

         this.initViewer(item);
      }

      @Override
      protected void initViewer(Object item) {
         if (this.isEditing()) {
            this.cancelEdit();
         }

         this.setEditable(false);
         if (item instanceof Roast roast) {
            this.setEditable(roast.isSynced());
            if (roast.getStartWeight() == null) {
               this.setGraphic(
                  new ProductionErrorLabel(
                     org.cropster.roastingintelligence.util.Messages.m_(
                        "Error loading file {0}", String.format("%1$tY%1$tm%1$td_%1$tH%1$tM%1$tS_%1$tL.json", roast.getStartDate())
                     )
                  )
               );
            } else {
               LotConsistencyCheck check = ProductionSidebar.this.appModel.getData().getChecks().get(roast.getLot().getId());
               int goalCount = -1;
               int errorCount = -1;
               if (check != null) {
                  goalCount = check.getCheckCount();
                  errorCount = check.getAlerts().size();
               }

               this.viewer.init(roast, goalCount, errorCount, ProductionSidebar.this.appModel, this.blendsFor(roast));
               this.setGraphic(this.viewer);
            }
         } else if (item instanceof Scheduled scheduled) {
            this.viewer.init(scheduled, ProductionSidebar.this.appModel);
            this.viewer
               .selectedProperty()
               .bind(
                  Bindings.createBooleanBinding(
                     () -> Objects.equals(ProductionSidebar.this.state.getScheduled(), scheduled.getId()),
                     new Observable[]{ProductionSidebar.this.state.scheduledProperty()}
                  )
               );
            this.setGraphic(this.viewer);
         } else {
            this.viewer.resetLabels();
            this.setGraphic(null);
         }
      }

      private List<Lot> blendsFor(Roast roast) {
         Long id = roast.getLot().getId();
         return ProductionSidebar.this.blendView
            .getItems()
            .stream()
            .filter(l -> l.getSources().stream().anyMatch(t -> Objects.equals(t.getLot(), id)))
            .toList();
      }

      private void showMenu(ActionEvent ae) {
         JavaFxUtil.selectItemExclusively(ProductionSidebar.this.productionView, this.getItem());
         Node owner = (Node)ae.getSource();
         ContextMenu contextMenu = ProductionSidebar.this.createProductionListCellContextMenu(owner);
         contextMenu.show(owner, Side.BOTTOM, 0.0, 0.0);
      }

      private void edit(ActionEvent ae) {
         JavaFxUtil.selectItemExclusively(ProductionSidebar.this.productionView, this.getItem());
         ProductionSidebar.this.editRoastAction.handle(ae);
      }

      private void onSuccessfulEdit(Roast roast) {
         ProductionSidebar.this.editor.getCancelButton().fire();
         this.viewer.updateRoastData(roast);
         ProductionSidebar.this.productionView.refresh();
         this.updateRoastInfo(roast);
      }

      private void updateFinalValues(Roast roast) {
         ProductionSidebar.this.finalValuesUpdater.accept(roast);
      }

      private void updateRoastInfo(Roast roast) {
         ProductionSidebar.this.roastInfoUpdater.accept(roast);
      }
   }
}
