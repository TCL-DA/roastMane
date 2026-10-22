package com.cropster.roastingintelligence.ui.util;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.apache.commons.lang3.StringUtils;

public class SearchHighlightingListCell<T> extends TranslatedListCell<T> {
   private final boolean ignoreCase;
   private final ReadOnlyStringProperty searchTextProperty;

   public static List<Text> getHighlightedTextComponents(String text, String searchText, boolean ignoreCase, String highlightStyle) {
      return getHighlightedTextComponents(text, searchText, ignoreCase, highlightStyle, null);
   }

   public static List<Text> getHighlightedTextComponents(String text, String searchText, boolean ignoreCase, String highlightStyle, String defaultStyle) {
      List<Text> segments = new ArrayList<>();
      if (StringUtils.isNotEmpty(searchText) && StringUtils.isNotEmpty(text)) {
         int index = 0;
         String actualText = ignoreCase ? text.toLowerCase() : text;
         String actualSearchText = ignoreCase ? searchText.toLowerCase() : searchText;
         String[] tokens = StringUtils.splitByWholeSeparatorPreserveAllTokens(actualText, actualSearchText);
         if (StringUtils.isNotEmpty(tokens[0])) {
            segments.add(style(new Text(text.substring(index, index + tokens[0].length())), defaultStyle));
            index += tokens[0].length();
         }

         for (int i = 1; i < tokens.length; i++) {
            segments.add(style(new Text(text.substring(index, index + searchText.length())), highlightStyle));
            index += searchText.length();
            if (StringUtils.isNotEmpty(tokens[i])) {
               segments.add(style(new Text(text.substring(index, index + tokens[i].length())), defaultStyle));
               index += tokens[i].length();
            }
         }
      } else {
         segments.add(style(new Text(text), defaultStyle));
      }

      return segments;
   }

   private static Text style(Text text, String style) {
      if (StringUtils.isNotBlank(style)) {
         text.setStyle(style);
      }

      return text;
   }

   public SearchHighlightingListCell(ReadOnlyStringProperty searchTextProperty, boolean ignoreCase) {
      this(null, searchTextProperty, ignoreCase);
   }

   public SearchHighlightingListCell(String nullText, ReadOnlyStringProperty searchTextProperty, boolean ignoreCase) {
      super(nullText);
      this.searchTextProperty = searchTextProperty;
      this.ignoreCase = ignoreCase;
   }

   @Override
   protected void updateItem(T item, boolean empty) {
      super.updateItem(item, empty);
      if (!this.isDisable()) {
         String text = this.getText();
         String searchText = (String)this.searchTextProperty.get();
         List<Text> segments = getHighlightedTextComponents(text, searchText, this.ignoreCase, "-fx-font-weight: bold;");
         if (!segments.isEmpty()) {
            this.setText(null);
            this.setGraphic(new TextFlow(segments.toArray(new Text[0])));
         }
      }
   }
}
