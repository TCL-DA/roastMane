package impl.org.controlsfx.skin;

import impl.org.controlsfx.i18n.Localization;
import java.util.Arrays;
import java.util.function.Predicate;
import javafx.beans.binding.Bindings;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SkinBase;
import javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.CustomTextField;
import org.controlsfx.control.textfield.TextFields;

public class SearchableComboBoxSkin<T> extends SkinBase<ComboBox<T>> {
   private static final Image filterIcon = new Image(SearchableComboBoxSkin.class.getResource("/impl/org/controlsfx/table/filter.png").toExternalForm());
   private final ComboBox<T> filteredComboBox = this.createFilteredComboBox();
   private final CustomTextField searchField;
   private T previousValue;

   public SearchableComboBoxSkin(ComboBox<T> comboBox) {
      super(comboBox);
      this.getChildren().add(this.filteredComboBox);
      this.searchField = this.createSearchField();
      this.getChildren().add(this.searchField);
      this.bindSearchFieldAndFilteredComboBox();
      this.preventDefaultComboBoxKeyListener();
      comboBox.addEventHandler(KeyEvent.KEY_PRESSED, this::checkOpenPopup);
   }

   protected void layoutChildren(double x, double y, double w, double h) {
      this.filteredComboBox.resizeRelocate(x, y, w, h);
      this.searchField.resizeRelocate(x, y, w, h);
   }

   private CustomTextField createSearchField() {
      CustomTextField field = (CustomTextField)TextFields.createClearableTextField();
      field.setPromptText(Localization.getString("filterpanel.search.field"));
      field.setId("search");
      field.getStyleClass().add("combo-box-search");
      ImageView imageView = new ImageView(filterIcon);
      imageView.setFitHeight(15.0);
      imageView.setPreserveRatio(true);
      field.setLeft(imageView);
      return field;
   }

   private ComboBox<T> createFilteredComboBox() {
      ComboBox<T> box = new ComboBox();
      box.setId("filtered");
      box.getStyleClass().add("combo-box-filtered");
      box.setFocusTraversable(false);
      Bindings.bindContent(box.getStyleClass(), ((ComboBox)this.getSkinnable()).getStyleClass());
      box.buttonCellProperty().bind(((ComboBox)this.getSkinnable()).buttonCellProperty());
      box.cellFactoryProperty().bind(((ComboBox)this.getSkinnable()).cellFactoryProperty());
      box.converterProperty().bind(((ComboBox)this.getSkinnable()).converterProperty());
      box.placeholderProperty().bind(((ComboBox)this.getSkinnable()).placeholderProperty());
      box.disableProperty().bind(((ComboBox)this.getSkinnable()).disableProperty());
      box.visibleRowCountProperty().bind(((ComboBox)this.getSkinnable()).visibleRowCountProperty());
      box.promptTextProperty().bind(((ComboBox)this.getSkinnable()).promptTextProperty());
      ((ComboBox)this.getSkinnable()).showingProperty().addListener((obs, oldVal, newVal) -> {
         if (newVal) {
            box.show();
         } else {
            box.hide();
         }
      });
      box.valueProperty().bindBidirectional(((ComboBox)this.getSkinnable()).valueProperty());
      return box;
   }

   private void bindSearchFieldAndFilteredComboBox() {
      this.filteredComboBox.setItems(this.createFilteredList());
      ((ComboBox)this.getSkinnable()).itemsProperty().addListener((obs, oldVal, newVal) -> this.filteredComboBox.setItems(this.createFilteredList()));
      this.searchField.textProperty().addListener(o -> this.updateFilter());
      this.searchField.visibleProperty().bind(this.filteredComboBox.showingProperty());
      this.filteredComboBox.showingProperty().addListener((obs, oldVal, newVal) -> {
         if (newVal) {
            ((ComboBox)this.getSkinnable()).show();
            this.previousValue = (T)((ComboBox)this.getSkinnable()).getValue();
            this.searchField.requestFocus();
         } else {
            ((ComboBox)this.getSkinnable()).hide();
            this.searchField.setText("");
         }
      });
      this.searchField.focusedProperty().addListener((obs, oldVal, newVal) -> {
         if (newVal) {
            this.filteredComboBox.show();
         } else {
            this.filteredComboBox.hide();
         }
      });
   }

   private FilteredList<T> createFilteredList() {
      return new FilteredList(((ComboBox)this.getSkinnable()).getItems(), this.predicate());
   }

   private void updateFilter() {
      this.filteredComboBox.setItems(this.createFilteredList());
   }

   private Predicate<T> predicate() {
      String searchText = this.searchField.getText().trim();
      return searchText.isEmpty() ? null : this.predicate(searchText);
   }

   private Predicate<T> predicate(String searchText) {
      String[] lowerCaseSearchWords = searchText.toLowerCase().split(" ");
      return value -> {
         String lowerCaseDisplayText = this.getDisplayText(value).toLowerCase();
         return Arrays.stream(lowerCaseSearchWords).allMatch(word -> lowerCaseDisplayText.contains(word));
      };
   }

   private String getDisplayText(T value) {
      StringConverter<T> converter = this.filteredComboBox.getConverter();
      return value == null ? "" : (converter != null ? converter.toString(value) : value.toString());
   }

   private void preventDefaultComboBoxKeyListener() {
      this.filteredComboBox.skinProperty().addListener((obs, oldVal, newVal) -> {
         if (newVal instanceof ComboBoxListViewSkin) {
            ComboBoxListViewSkin cblwSkin = (ComboBoxListViewSkin)newVal;
            if (cblwSkin.getPopupContent() instanceof ListView) {
               ListView<T> listView = (ListView<T>)cblwSkin.getPopupContent();
               if (listView != null) {
                  listView.setOnKeyPressed(this::checkApplyAndCancel);
               }
            }
         }
      });
   }

   private void checkApplyAndCancel(KeyEvent e) {
      KeyCode code = e.getCode();
      if (code != KeyCode.ENTER && code != KeyCode.TAB) {
         if (code == KeyCode.ESCAPE) {
            ((ComboBox)this.getSkinnable()).setValue(this.previousValue);
            ((ComboBox)this.getSkinnable()).hide();
            ((ComboBox)this.getSkinnable()).requestFocus();
         }
      } else {
         if (this.filteredComboBox.getSelectionModel().isEmpty()) {
            this.filteredComboBox.getSelectionModel().selectFirst();
         }

         ((ComboBox)this.getSkinnable()).hide();
         if (code == KeyCode.ENTER) {
            ((ComboBox)this.getSkinnable()).requestFocus();
         }
      }
   }

   private void checkOpenPopup(KeyEvent e) {
      KeyCode code = e.getCode();
      if (code == KeyCode.UP || code == KeyCode.DOWN) {
         this.filteredComboBox.show();
         e.consume();
      } else if (code.isLetterKey() || code.isDigitKey() || code == KeyCode.SPACE) {
         this.filteredComboBox.show();
      }
   }
}
