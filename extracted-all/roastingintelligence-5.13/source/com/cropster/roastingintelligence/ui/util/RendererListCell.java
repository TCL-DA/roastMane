package com.cropster.roastingintelligence.ui.util;

import java.util.function.BiFunction;
import java.util.function.Function;
import javafx.scene.control.ListCell;
import org.apache.commons.lang3.StringUtils;

public class RendererListCell<T> extends ListCell<T> {
   private final Function<T, String> renderFunction;
   private String nullValue;
   private String nullValueStyle;
   private BiFunction<RendererListCell<T>, T, String> styleProvider;

   public RendererListCell(Function<T, String> renderFunction) {
      this.renderFunction = renderFunction;
   }

   public String getNullValue() {
      return this.nullValue;
   }

   public void setNullValue(String nullValue) {
      this.nullValue = nullValue;
   }

   public RendererListCell<T> withNullValue(String nullValue) {
      this.setNullValue(nullValue);
      return this;
   }

   public RendererListCell<T> withNullValueStyle(String style) {
      this.nullValueStyle = style;
      return this;
   }

   public RendererListCell<T> withStyleProvider(BiFunction<RendererListCell<T>, T, String> styleProvider) {
      this.styleProvider = styleProvider;
      return this;
   }

   protected void updateItem(T item, boolean empty) {
      super.updateItem(item, empty);
      this.setGraphic(null);
      this.setStyle(null);
      if (!empty && item != null) {
         this.setText(this.renderFunction.apply(item));
         if (this.styleProvider != null) {
            this.setStyle(this.styleProvider.apply(this, item));
         }
      } else {
         this.setText(this.nullValue);
         if (StringUtils.isNotBlank(this.nullValueStyle)) {
            this.setStyle(this.nullValueStyle);
         }
      }
   }
}
