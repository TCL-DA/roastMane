package javafx.scene.control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.beans.DefaultProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableIntegerProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.SizeConverter;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.control.skin.PaginationSkin;
import javafx.util.Callback;

@DefaultProperty("pages")
public class Pagination extends Control {
   private static final int DEFAULT_MAX_PAGE_INDICATOR_COUNT = 10;
   public static final String STYLE_CLASS_BULLET = "bullet";
   public static final int INDETERMINATE = Integer.MAX_VALUE;
   private int oldMaxPageIndicatorCount = 10;
   private IntegerProperty maxPageIndicatorCount;
   private int oldPageCount = Integer.MAX_VALUE;
   private IntegerProperty pageCount = new SimpleIntegerProperty(this, "pageCount", Integer.MAX_VALUE) {
      protected void invalidated() {
         if (!Pagination.this.pageCount.isBound()) {
            if (Pagination.this.getPageCount() < 1) {
               Pagination.this.setPageCount(Pagination.this.oldPageCount);
            }

            Pagination.this.oldPageCount = Pagination.this.getPageCount();
         }
      }
   };
   private final IntegerProperty currentPageIndex = new SimpleIntegerProperty(this, "currentPageIndex", 0) {
      protected void invalidated() {
         if (!Pagination.this.currentPageIndex.isBound()) {
            if (Pagination.this.getCurrentPageIndex() < 0) {
               Pagination.this.setCurrentPageIndex(0);
            } else if (Pagination.this.getCurrentPageIndex() > Pagination.this.getPageCount() - 1) {
               Pagination.this.setCurrentPageIndex(Pagination.this.getPageCount() - 1);
            }
         }
      }

      public void bind(ObservableValue<? extends Number> var1) {
         throw new UnsupportedOperationException("currentPageIndex supports only bidirectional binding");
      }
   };
   private ObjectProperty<Callback<Integer, Node>> pageFactory = new SimpleObjectProperty(this, "pageFactory");
   private static final String DEFAULT_STYLE_CLASS = "pagination";

   public Pagination(int var1, int var2) {
      this.getStyleClass().setAll(new String[]{"pagination"});
      this.setAccessibleRole(AccessibleRole.PAGINATION);
      this.setPageCount(var1);
      this.setCurrentPageIndex(var2);
   }

   public Pagination(int var1) {
      this(var1, 0);
   }

   public Pagination() {
      this(Integer.MAX_VALUE, 0);
   }

   public final void setMaxPageIndicatorCount(int var1) {
      this.maxPageIndicatorCountProperty().set(var1);
   }

   public final int getMaxPageIndicatorCount() {
      return this.maxPageIndicatorCount == null ? 10 : this.maxPageIndicatorCount.get();
   }

   public final IntegerProperty maxPageIndicatorCountProperty() {
      if (this.maxPageIndicatorCount == null) {
         this.maxPageIndicatorCount = new StyleableIntegerProperty(10) {
            protected void invalidated() {
               if (!Pagination.this.maxPageIndicatorCount.isBound()) {
                  if (Pagination.this.getMaxPageIndicatorCount() < 1 || Pagination.this.getMaxPageIndicatorCount() > Pagination.this.getPageCount()) {
                     Pagination.this.setMaxPageIndicatorCount(Pagination.this.oldMaxPageIndicatorCount);
                  }

                  Pagination.this.oldMaxPageIndicatorCount = Pagination.this.getMaxPageIndicatorCount();
               }
            }

            public CssMetaData<Pagination, Number> getCssMetaData() {
               return Pagination.StyleableProperties.MAX_PAGE_INDICATOR_COUNT;
            }

            public Object getBean() {
               return Pagination.this;
            }

            public String getName() {
               return "maxPageIndicatorCount";
            }
         };
      }

      return this.maxPageIndicatorCount;
   }

   public final void setPageCount(int var1) {
      this.pageCount.set(var1);
   }

   public final int getPageCount() {
      return this.pageCount.get();
   }

   public final IntegerProperty pageCountProperty() {
      return this.pageCount;
   }

   public final void setCurrentPageIndex(int var1) {
      this.currentPageIndex.set(var1);
   }

   public final int getCurrentPageIndex() {
      return this.currentPageIndex.get();
   }

   public final IntegerProperty currentPageIndexProperty() {
      return this.currentPageIndex;
   }

   public final void setPageFactory(Callback<Integer, Node> var1) {
      this.pageFactory.set(var1);
   }

   public final Callback<Integer, Node> getPageFactory() {
      return (Callback<Integer, Node>)this.pageFactory.get();
   }

   public final ObjectProperty<Callback<Integer, Node>> pageFactoryProperty() {
      return this.pageFactory;
   }

   @Override
   protected Skin<?> createDefaultSkin() {
      return new PaginationSkin(this);
   }

   public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
      return Pagination.StyleableProperties.STYLEABLES;
   }

   @Override
   public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
      return getClassCssMetaData();
   }

   private static class StyleableProperties {
      private static final CssMetaData<Pagination, Number> MAX_PAGE_INDICATOR_COUNT = new CssMetaData<Pagination, Number>(
         "-fx-max-page-indicator-count", SizeConverter.getInstance(), 10
      ) {
         public boolean isSettable(Pagination var1) {
            return var1.maxPageIndicatorCount == null || !var1.maxPageIndicatorCount.isBound();
         }

         public StyleableProperty<Number> getStyleableProperty(Pagination var1) {
            return (StyleableProperty<Number>)var1.maxPageIndicatorCountProperty();
         }
      };
      private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

      static {
         ArrayList var0 = new ArrayList<>(Control.getClassCssMetaData());
         var0.add(MAX_PAGE_INDICATOR_COUNT);
         STYLEABLES = Collections.unmodifiableList(var0);
      }
   }
}
