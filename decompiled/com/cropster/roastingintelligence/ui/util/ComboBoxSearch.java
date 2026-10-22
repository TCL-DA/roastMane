package com.cropster.roastingintelligence.ui.util;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.Collection;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.StringConverter;

public class ComboBoxSearch<T> {
   private final ComboBox<T> comboBox;
   private ObservableList<T> originalItems;
   private FilteredList<T> filteredItems;
   private int currPos;
   private boolean searching = false;
   private final ObjectProperty<T> selectedItem = new SimpleObjectProperty();
   private T lastSelection;

   public ObjectProperty<T> selectedItemProperty() {
      return this.selectedItem;
   }

   public ComboBoxSearch(final ComboBox<T> comboBox) {
      assert Platform.isFxApplicationThread();

      this.comboBox = comboBox;
      this.originalItems = FXCollections.observableArrayList(comboBox.getItems());
      this.filteredItems = new FilteredList(this.originalItems);
      this.comboBox.setItems(this.filteredItems);
      comboBox.setEditable(true);
      comboBox.setConverter(new StringConverter<T>() {
         public String toString(T object) {
            return object != null ? object.toString() : null;
         }

         public T fromString(String string) {
            return (T)comboBox.getValue();
         }
      });
      TextField editor = comboBox.getEditor();
      comboBox.getSelectionModel().selectedItemProperty().addListener((v, o, n) -> {
         if (!this.searching) {
            if (n != null) {
               this.lastSelection = (T)n;
            }

            this.selectedItem.set(n);
         }
      });
      comboBox.getEditor().addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
         this.filteredItems.setPredicate(x -> true);
         this.setEditorTextFromSelection(editor);
         editor.selectAll();
         comboBox.show();
      });
      comboBox.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
         if (!this.isEventFromEditor(e, editor)) {
            this.filteredItems.setPredicate(x -> true);
            this.setEditorTextFromSelection(editor);
            editor.selectAll();
            comboBox.show();
         }
      });
      comboBox.getEditor().focusedProperty().addListener((v, o, n) -> {
         this.searching = true;
         this.filteredItems.setPredicate(e -> true);
         if (n) {
            T selected = (T)comboBox.getSelectionModel().getSelectedItem();
            if (selected != null) {
               this.lastSelection = selected;
            }

            comboBox.getSelectionModel().clearSelection();
            this.setEditorTextFromSelection(editor);
            editor.selectAll();
            comboBox.show();
         } else if (comboBox.getSelectionModel().getSelectedIndex() < 0) {
            comboBox.getSelectionModel().select(this.lastSelection);
         }

         this.searching = false;
      });
      comboBox.getEditor().addEventFilter(KeyEvent.KEY_RELEASED, e -> {
         this.currPos = editor.getCaretPosition();
         if (e.getCode() == KeyCode.ENTER) {
            comboBox.setValue(comboBox.getValue());
         } else if (e.getCode() == KeyCode.ESCAPE) {
            this.filteredItems.setPredicate(x -> true);
            comboBox.getSelectionModel().select(this.lastSelection);
         } else if (e.getCode() == KeyCode.DOWN) {
            if (!comboBox.getItems().isEmpty()) {
               comboBox.show();
            }
         } else if (e.getCode() == KeyCode.BEGIN) {
            editor.positionCaret(0);
         } else if (e.getCode() == KeyCode.END) {
            editor.positionCaret(editor.getText().length());
         } else if (!e.getCode().isNavigationKey() && !e.getCode().isArrowKey() && !e.getCode().isFunctionKey() && !e.getCode().isMediaKey()) {
            comboBox.hide();
            String searchTerm = editor.getText();
            if (e.getCode() == KeyCode.BACK_SPACE || e.getCode() == KeyCode.DELETE || searchTerm == null || searchTerm.trim().isEmpty()) {
               this.searching = true;
               this.filteredItems.setPredicate(t -> this.matches((T)t, searchTerm));
               this.searching = false;
               if (editor.getSelection() != null) {
                  editor.replaceSelection("");
               }
            }

            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
               this.searching = true;
               this.filteredItems.setPredicate(t -> this.matches((T)t, searchTerm));
               this.searching = false;
            }

            editor.selectPositionCaret(this.currPos);
            if (!comboBox.getItems().isEmpty()) {
               comboBox.show();
            }
         }
      });
      ComboBoxListViewSkin<T> comboBoxListViewSkin = new ComboBoxListViewSkin(comboBox);
      comboBoxListViewSkin.getPopupContent().addEventFilter(KeyEvent.ANY, event -> {
         if (event.getCode() == KeyCode.SPACE) {
            event.consume();
         }
      });
      comboBox.setSkin(comboBoxListViewSkin);
   }

   public void reset(Collection<T> items) {
      this.originalItems.setAll(items);
      this.filteredItems.setPredicate(e -> true);
   }

   protected String normalize(String text) {
      return Normalizer.normalize(text, Form.NFD).replaceAll("\\p{M}", "");
   }

   protected boolean matches(T obj, String searchTerm) {
      if (obj == null) {
         return false;
      } else if (searchTerm != null && !searchTerm.trim().isEmpty()) {
         String normalizedSearchTerm = this.normalize(searchTerm.toLowerCase());
         String normalizedText = this.normalize(obj.toString().toLowerCase());
         return normalizedText.contains(normalizedSearchTerm);
      } else {
         return true;
      }
   }

   private boolean isEventFromEditor(MouseEvent event, TextField editor) {
      Object target = event.getTarget();
      if (target instanceof Node) {
         for (Node node = (Node)target; node != null; node = node.getParent()) {
            if (node == editor) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private void setEditorTextFromSelection(TextField editor) {
      T selected = (T)this.comboBox.getSelectionModel().getSelectedItem();
      T selectedOrLast = selected != null ? selected : this.lastSelection;
      editor.setText(selectedOrLast != null ? this.comboBox.getConverter().toString(selectedOrLast) : null);
   }
}
