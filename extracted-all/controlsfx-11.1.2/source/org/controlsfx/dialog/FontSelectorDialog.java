package org.controlsfx.dialog;

import impl.org.controlsfx.i18n.Localization;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Callback;

public class FontSelectorDialog extends Dialog<Font> {
   private FontSelectorDialog.FontPanel fontPanel = new FontSelectorDialog.FontPanel();

   public FontSelectorDialog(Font defaultFont) {
      this.fontPanel.setFont(defaultFont);
      this.setResultConverter(dialogButton -> dialogButton == ButtonType.OK ? this.fontPanel.getFont() : null);
      DialogPane dialogPane = this.getDialogPane();
      this.setTitle(Localization.localize(Localization.asKey("font.dlg.title")));
      dialogPane.setHeaderText(Localization.localize(Localization.asKey("font.dlg.header")));
      dialogPane.getStyleClass().add("font-selector-dialog");
      dialogPane.getStylesheets().add(FontSelectorDialog.class.getResource("dialogs.css").toExternalForm());
      dialogPane.getButtonTypes().addAll(new ButtonType[]{ButtonType.OK, ButtonType.CANCEL});
      dialogPane.setContent(this.fontPanel);
   }

   private static class FontPanel extends GridPane {
      private static final double HGAP = 10.0;
      private static final double VGAP = 5.0;
      private static final Predicate<Object> MATCH_ALL = new Predicate<Object>() {
         @Override
         public boolean test(Object t) {
            return true;
         }
      };
      private static final Double[] fontSizes = new Double[]{8.0, 9.0, 11.0, 12.0, 14.0, 16.0, 18.0, 20.0, 22.0, 24.0, 26.0, 28.0, 36.0, 48.0, 72.0};
      private final FilteredList<String> filteredFontList = new FilteredList(FXCollections.observableArrayList(Font.getFamilies()), MATCH_ALL);
      private final FilteredList<FontSelectorDialog.FontStyle> filteredStyleList = new FilteredList(FXCollections.observableArrayList(), MATCH_ALL);
      private final FilteredList<Double> filteredSizeList = new FilteredList(FXCollections.observableArrayList(fontSizes), MATCH_ALL);
      private final ListView<String> fontListView = new ListView(this.filteredFontList);
      private final ListView<FontSelectorDialog.FontStyle> styleListView = new ListView(this.filteredStyleList);
      private final ListView<Double> sizeListView = new ListView(this.filteredSizeList);
      private final Text sample = new Text(Localization.localize(Localization.asKey("font.dlg.sample.text")));

      private static List<FontSelectorDialog.FontStyle> getFontStyles(String fontFamily) {
         Set<FontSelectorDialog.FontStyle> set = new HashSet<>();

         for (String f : Font.getFontNames(fontFamily)) {
            set.add(new FontSelectorDialog.FontStyle(f.replace(fontFamily, "")));
         }

         List<FontSelectorDialog.FontStyle> result = new ArrayList<>(set);
         Collections.sort(result);
         return result;
      }

      public FontPanel() {
         this.setHgap(10.0);
         this.setVgap(5.0);
         this.setPrefSize(500.0, 300.0);
         this.setMinSize(500.0, 300.0);
         ColumnConstraints c0 = new ColumnConstraints();
         c0.setPercentWidth(60.0);
         ColumnConstraints c1 = new ColumnConstraints();
         c1.setPercentWidth(25.0);
         ColumnConstraints c2 = new ColumnConstraints();
         c2.setPercentWidth(15.0);
         this.getColumnConstraints().addAll(new ColumnConstraints[]{c0, c1, c2});
         RowConstraints r0 = new RowConstraints();
         r0.setVgrow(Priority.NEVER);
         RowConstraints r1 = new RowConstraints();
         r1.setVgrow(Priority.NEVER);
         RowConstraints r2 = new RowConstraints();
         r2.setFillHeight(true);
         r2.setVgrow(Priority.NEVER);
         RowConstraints r3 = new RowConstraints();
         r3.setPrefHeight(250.0);
         r3.setVgrow(Priority.NEVER);
         this.getRowConstraints().addAll(new RowConstraints[]{r0, r1, r2, r3});
         this.add(new Label(Localization.localize(Localization.asKey("font.dlg.font.label"))), 0, 0);
         this.add(this.fontListView, 0, 1);
         this.fontListView.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            public ListCell<String> call(ListView<String> listview) {
               return new ListCell<String>() {
                  protected void updateItem(String family, boolean empty) {
                     super.updateItem(family, empty);
                     if (!empty) {
                        this.setFont(Font.font(family));
                        this.setText(family);
                     } else {
                        this.setText(null);
                     }
                  }
               };
            }
         });
         ChangeListener<Object> sampleRefreshListener = new ChangeListener<Object>() {
            public void changed(ObservableValue<? extends Object> arg0, Object arg1, Object arg2) {
               FontPanel.this.refreshSample();
            }
         };
         ((MultipleSelectionModel)this.fontListView.selectionModelProperty().get()).selectedItemProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
               String fontFamily = FontPanel.this.listSelection(FontPanel.this.fontListView);
               FontPanel.this.styleListView.setItems(FXCollections.observableArrayList(FontSelectorDialog.FontPanel.getFontStyles(fontFamily)));
               FontPanel.this.refreshSample();
            }
         });
         this.add(new Label(Localization.localize(Localization.asKey("font.dlg.style.label"))), 1, 0);
         this.add(this.styleListView, 1, 1);
         ((MultipleSelectionModel)this.styleListView.selectionModelProperty().get()).selectedItemProperty().addListener(sampleRefreshListener);
         this.add(new Label(Localization.localize(Localization.asKey("font.dlg.size.label"))), 2, 0);
         this.add(this.sizeListView, 2, 1);
         ((MultipleSelectionModel)this.sizeListView.selectionModelProperty().get()).selectedItemProperty().addListener(sampleRefreshListener);
         double height = 45.0;
         DoubleBinding sampleWidth = new DoubleBinding() {
            {
               this.bind(
                  new Observable[]{
                     FontPanel.this.fontListView.widthProperty(), FontPanel.this.styleListView.widthProperty(), FontPanel.this.sizeListView.widthProperty()
                  }
               );
            }

            protected double computeValue() {
               return FontPanel.this.fontListView.getWidth() + FontPanel.this.styleListView.getWidth() + FontPanel.this.sizeListView.getWidth() + 30.0;
            }
         };
         StackPane sampleStack = new StackPane(new Node[]{this.sample});
         sampleStack.setAlignment(Pos.CENTER_LEFT);
         sampleStack.setMinHeight(45.0);
         sampleStack.setPrefHeight(45.0);
         sampleStack.setMaxHeight(45.0);
         sampleStack.prefWidthProperty().bind(sampleWidth);
         Rectangle clip = new Rectangle(0.0, 45.0);
         clip.widthProperty().bind(sampleWidth);
         sampleStack.setClip(clip);
         this.add(sampleStack, 0, 3, 1, 3);
      }

      public void setFont(Font font) {
         Font _font = font == null ? Font.getDefault() : font;
         if (_font != null) {
            this.selectInList(this.fontListView, _font.getFamily());
            this.selectInList(this.styleListView, new FontSelectorDialog.FontStyle(_font));
            this.selectInList(this.sizeListView, _font.getSize());
         }
      }

      public Font getFont() {
         try {
            FontSelectorDialog.FontStyle style = this.listSelection(this.styleListView);
            return style == null
               ? Font.font(this.listSelection(this.fontListView), this.<Double>listSelection(this.sizeListView))
               : Font.font(this.listSelection(this.fontListView), style.getWeight(), style.getPosture(), this.<Double>listSelection(this.sizeListView));
         } catch (Throwable var2) {
            return null;
         }
      }

      private void refreshSample() {
         this.sample.setFont(this.getFont());
      }

      private <T> void selectInList(final ListView<T> listView, final T selection) {
         Platform.runLater(new Runnable() {
            @Override
            public void run() {
               listView.scrollTo(selection);
               listView.getSelectionModel().select(selection);
            }
         });
      }

      private <T> T listSelection(ListView<T> listView) {
         return (T)((MultipleSelectionModel)listView.selectionModelProperty().get()).getSelectedItem();
      }
   }

   private static class FontStyle implements Comparable<FontSelectorDialog.FontStyle> {
      private FontPosture posture;
      private FontWeight weight;

      public FontStyle(FontWeight weight, FontPosture posture) {
         this.posture = posture == null ? FontPosture.REGULAR : posture;
         this.weight = weight;
      }

      public FontStyle() {
         this(null, null);
      }

      public FontStyle(String styles) {
         this();
         String[] fontStyles = (styles == null ? "" : styles.trim().toUpperCase()).split(" ");

         for (String style : fontStyles) {
            FontWeight w = FontWeight.findByName(style);
            if (w != null) {
               this.weight = w;
            } else {
               FontPosture p = FontPosture.findByName(style);
               if (p != null) {
                  this.posture = p;
               }
            }
         }
      }

      public FontStyle(Font font) {
         this(font.getStyle());
      }

      public FontPosture getPosture() {
         return this.posture;
      }

      public FontWeight getWeight() {
         return this.weight;
      }

      @Override
      public int hashCode() {
         int prime = 31;
         int result = 1;
         result = 31 * result + (this.posture == null ? 0 : this.posture.hashCode());
         return 31 * result + (this.weight == null ? 0 : this.weight.hashCode());
      }

      @Override
      public boolean equals(Object that) {
         if (this == that) {
            return true;
         } else if (that == null) {
            return false;
         } else if (this.getClass() != that.getClass()) {
            return false;
         } else {
            FontSelectorDialog.FontStyle other = (FontSelectorDialog.FontStyle)that;
            return this.posture != other.posture ? false : this.weight == other.weight;
         }
      }

      private static String makePretty(Object o) {
         String s = o == null ? "" : o.toString();
         if (!s.isEmpty()) {
            s = s.replace("_", " ");
            s = s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
         }

         return s;
      }

      @Override
      public String toString() {
         return String.format("%s %s", makePretty(this.weight), makePretty(this.posture)).trim();
      }

      private <T extends Enum<T>> int compareEnums(T e1, T e2) {
         if (e1 == e2) {
            return 0;
         } else if (e1 == null) {
            return -1;
         } else {
            return e2 == null ? 1 : e1.compareTo(e2);
         }
      }

      public int compareTo(FontSelectorDialog.FontStyle fs) {
         int result = this.compareEnums(this.weight, fs.weight);
         return result != 0 ? result : this.compareEnums(this.posture, fs.posture);
      }
   }
}
