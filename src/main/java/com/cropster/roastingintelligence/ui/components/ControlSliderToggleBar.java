package com.cropster.roastingintelligence.ui.components;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.util.KeyboardShortcut;
import javafx.beans.DefaultProperty;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;

@DefaultProperty("sliders")
public class ControlSliderToggleBar extends VBox {
   private static final String TOGGLE_TOP_STYLE = "toggle-button-group__top";
   private static final String TOGGLE_MIDDLE_STYLE = "toggle-button-group__middle";
   private static final String TOGGLE_BOTTOM_STYLE = "toggle-button-group__bottom";
   private final ToggleGroup slidersToggleGroup = new ToggleGroup();
   private final VBox togglesBox = new VBox();
   private final GridPane gridPane = new GridPane();
   private final ObservableList<ControlSlider> sliders = FXCollections.observableArrayList();
   private final ObservableList<ControlSliderToggleButton> toggles = FXCollections.observableArrayList();
   private final ObservableList<ControlSliderToggleButton> visibleToggles = FXCollections.observableArrayList();
   private final ObservableList<ControlSliderToggleButton> readOnlyVisibleToggles = FXCollections.unmodifiableObservableList(this.visibleToggles);
   private final ChangeListener<Boolean> toggleVisibilityChangeListener = (s, o, n) -> this.updateToggles();
   private final ChangeListener<Boolean> sliderVisibilityChangeListener = (s, o, n) -> this.updateGridPaneLayout();
   private final BooleanProperty singleColumn = new SimpleBooleanProperty(true);

   public ControlSliderToggleBar() {
      this.initializeToggleBox();
      this.getChildren().add(this.togglesBox);
      this.initializeGridPane();
      this.getChildren().add(this.gridPane);
      this.singleColumn.addListener((s, o, n) -> {
         if (n) {
            for (int i = 0; i < this.sliders.size(); i++) {
               ControlSlider slider = (ControlSlider)this.sliders.get(i);
               this.gridPane.getChildren().remove(slider);
               GridPane.clearConstraints(slider);
               if (!this.getChildren().contains(slider)) {
                  this.getChildren().add(i, slider);
               }

               if (!slider.displayableProperty().isBound()) {
                  slider.displayableProperty().bind(slider.getToggle().selectedProperty());
               }
            }

            this.updateSliderButtons();
         } else {
            for (int i = 0; i < this.sliders.size(); i++) {
               ControlSlider sliderx = (ControlSlider)this.sliders.get(i);
               this.getChildren().remove(sliderx);
               sliderx.displayableProperty().unbind();
               sliderx.setDisplayable(true);
            }

            this.updateGridPaneLayout();
         }
      });
      ListChangeListener<ControlSlider> slidersChangeListener = change -> {
         while (change.next()) {
            if (change.wasAdded()) {
               change.getAddedSubList().forEach(this::addSlider);
            } else if (change.wasRemoved()) {
               change.getRemoved().forEach(this::removeSlider);
            }
         }
      };
      this.sliders.addListener(slidersChangeListener);
      ListChangeListener<ControlSliderToggleButton> togglesChangeListener = change -> {
         while (change.next()) {
            if (change.wasAdded()) {
               change.getAddedSubList().forEach(this::addToggle);
            } else if (change.wasRemoved()) {
               change.getRemoved().forEach(this::removeToggle);
            }
         }
      };
      this.toggles.addListener(togglesChangeListener);
      JavaFxUtil.preventToggleGroupBeingUnselected(this.slidersToggleGroup);
      this.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
         if (KeyboardShortcut.ARROW_UP.match(e) || KeyboardShortcut.ARROW_DOWN.match(e)) {
            e.consume();
         }
      });
   }

   private void initializeToggleBox() {
      this.togglesBox.getStyleClass().add("control-slider-toggle-bar__toggle-box");
      this.togglesBox.setId("toggleBox");
      this.togglesBox.managedProperty().bind(this.togglesBox.visibleProperty());
      this.togglesBox
         .visibleProperty()
         .bind(
            Bindings.createBooleanBinding(
               () -> this.isSingleColumn() && this.visibleToggles.size() > 1, new Observable[]{this.singleColumn, this.visibleToggles}
            )
         );
      VBox.setVgrow(this.togglesBox, Priority.NEVER);
      VBox.setMargin(this.togglesBox, new Insets(8.0, 0.0, 0.0, 0.0));
   }

   private void initializeGridPane() {
      this.gridPane.getStyleClass().add("control-slider-toggle-bar__grid-pane");
      this.gridPane.setId("gridPane");
      this.gridPane.managedProperty().bind(this.gridPane.visibleProperty());
      this.gridPane.visibleProperty().bind(this.singleColumn.not());
      VBox.setVgrow(this.gridPane, Priority.ALWAYS);
      RowConstraints rc0 = new RowConstraints();
      rc0.setVgrow(Priority.ALWAYS);
      RowConstraints rc1 = new RowConstraints();
      rc1.setVgrow(Priority.ALWAYS);
      this.gridPane.getRowConstraints().addAll(new RowConstraints[]{rc0, rc1});
      this.gridPane.getColumnConstraints().add(this.createColumnConstraints());
   }

   private ColumnConstraints createColumnConstraints() {
      ColumnConstraints cc = new ColumnConstraints(160.0);
      cc.setHgrow(Priority.ALWAYS);
      cc.setHalignment(HPos.CENTER);
      return cc;
   }

   private void addSlider(ControlSlider slider) {
      if (!slider.visibleProperty().isBound()) {
         slider.setVisible(false);
      }

      slider.visibleProperty().addListener(this.sliderVisibilityChangeListener);
      if (this.isSingleColumn()) {
         this.getChildren().add(this.getChildren().size() - 2, slider);
         slider.initButtons();
      } else {
         this.updateGridPaneLayout();
      }
   }

   private void removeSlider(ControlSlider slider) {
      slider.visibleProperty().removeListener(this.sliderVisibilityChangeListener);
      if (this.isSingleColumn()) {
         this.getChildren().remove(slider);
      } else {
         this.gridPane.getChildren().remove(slider);
         GridPane.clearConstraints(slider);
         this.updateGridPaneLayout();
      }
   }

   private void updateGridPaneLayout() {
      if (!this.isSingleColumn()) {
         this.gridPane.getChildren().clear();
         this.sliders.stream().filter(Node::isVisible).forEach(slider -> this.gridPane.getChildren().add(slider));
         int childrenCount = this.gridPane.getChildren().size();
         if (childrenCount == 0) {
            while (this.gridPane.getColumnConstraints().size() > 1) {
               this.gridPane.getColumnConstraints().remove(0);
            }
         } else {
            int columns = columnsForSliders(childrenCount);

            while (this.gridPane.getColumnConstraints().size() > columns) {
               this.gridPane.getColumnConstraints().remove(0);
            }

            while (this.gridPane.getColumnConstraints().size() < columns) {
               this.gridPane.getColumnConstraints().add(this.createColumnConstraints());
            }

            GridPane.setConstraints((Node)this.gridPane.getChildren().get(0), 0, 0, 1, 2);
            switch (childrenCount) {
               case 2:
               case 4:
                  GridPane.setConstraints((Node)this.gridPane.getChildren().get(1), 1, 0, 1, 2);
                  break;
               case 3:
               case 5:
                  GridPane.setConstraints((Node)this.gridPane.getChildren().get(1), 1, 0, 1, 1);
                  GridPane.setConstraints((Node)this.gridPane.getChildren().get(2), 1, 1, 1, 1);
            }

            switch (childrenCount) {
               case 4:
                  GridPane.setConstraints((Node)this.gridPane.getChildren().get(2), 2, 0, 1, 1);
                  GridPane.setConstraints((Node)this.gridPane.getChildren().get(3), 2, 1, 1, 1);
                  break;
               case 5:
                  GridPane.setConstraints((Node)this.gridPane.getChildren().get(3), 2, 0, 1, 1);
                  GridPane.setConstraints((Node)this.gridPane.getChildren().get(4), 2, 1, 1, 1);
            }

            this.updateSliderButtons();
         }
      }
   }

   private void updateSliderButtons() {
      this.sliders.forEach(slider -> {
         if (slider.isVisible()) {
            slider.initButtons();
         }
      });
   }

   private static int columnsForSliders(int sliders) {
      return 1 + (int)Math.ceil(((double)sliders - 1.0) / 2.0);
   }

   private void addToggle(ControlSliderToggleButton toggle) {
      if (!toggle.visibleProperty().isBound()) {
         toggle.setVisible(false);
      }

      toggle.setToggleGroup(this.slidersToggleGroup);
      toggle.visibleProperty().addListener(this.toggleVisibilityChangeListener);
      this.togglesBox.getChildren().add(toggle);
      this.updateToggles();
   }

   private void removeToggle(ControlSliderToggleButton toggle) {
      toggle.visibleProperty().removeListener(this.toggleVisibilityChangeListener);
      toggle.setToggleGroup(null);
      this.togglesBox.getChildren().remove(toggle);
      this.updateToggles();
   }

   private void updateToggles() {
      this.visibleToggles.clear();
      this.toggles.forEach(t -> {
         t.getStyleClass().remove("toggle-button-group__top");
         t.getStyleClass().remove("toggle-button-group__middle");
         t.getStyleClass().remove("toggle-button-group__bottom");
         if (t.isVisible()) {
            this.visibleToggles.add(t);
         }
      });
      if (this.visibleToggles.size() > 1) {
         ((ControlSliderToggleButton)this.visibleToggles.get(0)).getStyleClass().add("toggle-button-group__top");
         ((ControlSliderToggleButton)this.visibleToggles.get(this.visibleToggles.size() - 1)).getStyleClass().add("toggle-button-group__bottom");

         for (int i = 1; i < this.visibleToggles.size() - 1; i++) {
            ((ControlSliderToggleButton)this.visibleToggles.get(i)).getStyleClass().add("toggle-button-group__middle");
         }
      }

      ToggleButton selected = (ToggleButton)this.slidersToggleGroup.getSelectedToggle();
      if (selected != null && !this.visibleToggles.isEmpty() && !this.visibleToggles.contains(selected.getParent())) {
         ((ControlSliderToggleButton)this.visibleToggles.get(0)).setSelected(true);
      }
   }

   public ObservableList<ControlSlider> getSliders() {
      return this.sliders;
   }

   public ObservableList<ControlSliderToggleButton> getToggles() {
      return this.toggles;
   }

   public ObservableList<ControlSliderToggleButton> getVisibleToggles() {
      return this.readOnlyVisibleToggles;
   }

   public BooleanProperty singleColumnProperty() {
      return this.singleColumn;
   }

   public boolean isSingleColumn() {
      return this.singleColumn.get();
   }

   private void setSingleColumn(boolean singleColumn) {
      this.singleColumn.set(singleColumn);
   }

   public void switchLayout() {
      this.setSingleColumn(!this.isSingleColumn());
   }

   protected double computePrefWidth(double height) {
      return this.computeMinWidth(height);
   }

   protected double computeMinWidth(double height) {
      if (this.singleColumn.get()) {
         return 172.0;
      } else {
         long visibleSliders = this.sliders.stream().filter(Node::isVisible).count();
         int columns = columnsForSliders((int)visibleSliders);
         return 160.0 * (double)columns + (double)(columns - 1) * this.gridPane.getHgap() + 12.0;
      }
   }
}
