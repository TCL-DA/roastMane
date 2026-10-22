package impl.org.controlsfx.skin;

import impl.org.controlsfx.i18n.Localization;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SkinBase;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.control.SegmentedButton;
import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionUtils;
import org.controlsfx.control.textfield.TextFields;
import org.controlsfx.property.editor.AbstractPropertyEditor;
import org.controlsfx.property.editor.PropertyEditor;

public class PropertySheetSkin extends SkinBase<PropertySheet> {
   private static final int MIN_COLUMN_WIDTH = 100;
   private final BorderPane content;
   private final ScrollPane scroller;
   private final ToolBar toolbar;
   private final SegmentedButton modeButton = ActionUtils.createSegmentedButton(
      new PropertySheetSkin.ActionChangeMode(PropertySheet.Mode.NAME), new PropertySheetSkin.ActionChangeMode(PropertySheet.Mode.CATEGORY)
   );
   private final TextField searchField = TextFields.createClearableTextField();

   public PropertySheetSkin(PropertySheet control) {
      super(control);
      this.scroller = new ScrollPane();
      this.scroller.setFitToWidth(true);
      this.toolbar = new ToolBar();
      this.toolbar.managedProperty().bind(this.toolbar.visibleProperty());
      this.toolbar.setFocusTraversable(true);
      this.modeButton.managedProperty().bind(this.modeButton.visibleProperty());
      ((ToggleButton)this.modeButton.getButtons().get(((PropertySheet.Mode)((PropertySheet)this.getSkinnable()).modeProperty().get()).ordinal()))
         .setSelected(true);
      this.toolbar.getItems().add(this.modeButton);
      this.searchField.setPromptText(Localization.localize(Localization.asKey("property.sheet.search.field.prompt")));
      this.searchField.setMinWidth(0.0);
      HBox.setHgrow(this.searchField, Priority.SOMETIMES);
      this.searchField.managedProperty().bind(this.searchField.visibleProperty());
      this.toolbar.getItems().add(this.searchField);
      this.content = new BorderPane();
      this.content.setTop(this.toolbar);
      this.content.setCenter(this.scroller);
      this.getChildren().add(this.content);
      this.registerChangeListener(control.modeProperty(), e -> this.refreshProperties());
      this.registerChangeListener(control.propertyEditorFactory(), e -> this.refreshProperties());
      this.registerChangeListener(control.titleFilter(), e -> this.refreshProperties());
      this.registerChangeListener(this.searchField.textProperty(), e -> ((PropertySheet)this.getSkinnable()).setTitleFilter(this.searchField.getText()));
      this.registerChangeListener(control.modeSwitcherVisibleProperty(), e -> this.updateToolbar());
      this.registerChangeListener(control.searchBoxVisibleProperty(), e -> this.updateToolbar());
      this.registerChangeListener(control.categoryComparatorProperty(), e -> this.refreshProperties());
      control.getItems().addListener(change -> this.refreshProperties());
      this.refreshProperties();
      this.updateToolbar();
   }

   protected void layoutChildren(double x, double y, double w, double h) {
      this.content.resizeRelocate(x, y, w, h);
   }

   private void updateToolbar() {
      this.modeButton.setVisible(((PropertySheet)this.getSkinnable()).isModeSwitcherVisible());
      this.searchField.setVisible(((PropertySheet)this.getSkinnable()).isSearchBoxVisible());
      this.toolbar.setVisible(this.modeButton.isVisible() || this.searchField.isVisible());
   }

   private void refreshProperties() {
      this.scroller.setContent(this.buildPropertySheetContainer());
   }

   private Node buildPropertySheetContainer() {
      switch ((PropertySheet.Mode)((PropertySheet)this.getSkinnable()).modeProperty().get()) {
         case CATEGORY:
            Map<String, List<PropertySheet.Item>> categoryMap = new TreeMap<>(((PropertySheet)this.getSkinnable()).getCategoryComparator());

            for (PropertySheet.Item p : ((PropertySheet)this.getSkinnable()).getItems()) {
               String category = p.getCategory();
               List<PropertySheet.Item> list = categoryMap.get(category);
               if (list == null) {
                  list = new ArrayList<>();
                  categoryMap.put(category, list);
               }

               list.add(p);
            }

            Accordion accordion = new Accordion();

            for (String category : categoryMap.keySet()) {
               PropertySheetSkin.PropertyPane props = new PropertySheetSkin.PropertyPane(categoryMap.get(category));
               if (props.getChildrenUnmodifiable().size() > 0) {
                  TitledPane pane = new TitledPane(category, props);
                  pane.setExpanded(true);
                  accordion.getPanes().add(pane);
               }
            }

            if (accordion.getPanes().size() > 0) {
               accordion.setExpandedPane((TitledPane)accordion.getPanes().get(0));
            }

            return accordion;
         default:
            return new PropertySheetSkin.PropertyPane(((PropertySheet)this.getSkinnable()).getItems());
      }
   }

   private class ActionChangeMode extends Action {
      private final Image CATEGORY_IMAGE = new Image(PropertySheetSkin.class.getResource("/org/controlsfx/control/format-indent-more.png").toExternalForm());
      private final Image NAME_IMAGE = new Image(PropertySheetSkin.class.getResource("/org/controlsfx/control/format-line-spacing-triple.png").toExternalForm());

      public ActionChangeMode(PropertySheet.Mode mode) {
         super("");
         this.setEventHandler(ae -> ((PropertySheet)PropertySheetSkin.this.getSkinnable()).modeProperty().set(mode));
         if (mode == PropertySheet.Mode.CATEGORY) {
            this.setGraphic(new ImageView(this.CATEGORY_IMAGE));
            this.setLongText(Localization.localize(Localization.asKey("property.sheet.group.mode.bycategory")));
         } else if (mode == PropertySheet.Mode.NAME) {
            this.setGraphic(new ImageView(this.NAME_IMAGE));
            this.setLongText(Localization.localize(Localization.asKey("property.sheet.group.mode.byname")));
         } else {
            this.setText("???");
         }
      }
   }

   private class PropertyPane extends GridPane {
      public PropertyPane(List<PropertySheet.Item> properties) {
         this(properties, 0);
      }

      public PropertyPane(List<PropertySheet.Item> properties, int nestingLevel) {
         this.setVgap(5.0);
         this.setHgap(5.0);
         this.setPadding(new Insets(5.0, 15.0, 5.0, (double)(15 + nestingLevel * 10)));
         this.getStyleClass().add("property-pane");
         this.setItems(properties);
      }

      public void setItems(List<PropertySheet.Item> properties) {
         this.getChildren().clear();
         String filter = ((PropertySheet)PropertySheetSkin.this.getSkinnable()).titleFilter().get();
         filter = filter == null ? "" : filter.trim().toLowerCase();
         int row = 0;

         for (PropertySheet.Item item : properties) {
            String title = item.getName();
            if (filter.isEmpty() || title.toLowerCase().indexOf(filter) >= 0) {
               Label label = new Label(title);
               label.setMinWidth(100.0);
               String description = item.getDescription();
               if (description != null && !description.trim().isEmpty()) {
                  label.setTooltip(new Tooltip(description));
               }

               this.add(label, 0, row);
               Node editor = this.getEditor(item);
               if (editor instanceof Region) {
                  ((Region)editor).setMinWidth(100.0);
                  ((Region)editor).setMaxWidth(Double.MAX_VALUE);
               }

               label.setLabelFor(editor);
               this.add(editor, 1, row);
               GridPane.setHgrow(editor, Priority.ALWAYS);
               row++;
            }
         }
      }

      private Node getEditor(PropertySheet.Item item) {
         PropertyEditor editor = (PropertyEditor)((PropertySheet)PropertySheetSkin.this.getSkinnable()).getPropertyEditorFactory().call(item);
         if (editor == null) {
            editor = new AbstractPropertyEditor<Object, TextField>(item, new TextField(), true) {
               {
                  this.getEditor().setEditable(false);
                  this.getEditor().setDisable(true);
               }

               @Override
               protected ObservableValue<Object> getObservableValue() {
                  return (ObservableValue<Object>)this.getEditor().textProperty();
               }

               @Override
               public void setValue(Object value) {
                  this.getEditor().setText(value == null ? "" : value.toString());
               }
            };
         } else if (!item.isEditable()) {
            editor.getEditor().setDisable(true);
         }

         editor.setValue(item.getValue());
         return editor.getEditor();
      }
   }
}
