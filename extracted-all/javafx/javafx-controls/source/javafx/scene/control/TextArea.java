package javafx.scene.control;

import com.sun.javafx.binding.ExpressionHelper;
import com.sun.javafx.collections.ListListenerHelper;
import com.sun.javafx.collections.NonIterableChange;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableIntegerProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.SizeConverter;
import javafx.scene.AccessibleRole;
import javafx.scene.control.skin.TextAreaSkin;

public class TextArea extends TextInputControl {
   public static final int DEFAULT_PREF_COLUMN_COUNT = 40;
   public static final int DEFAULT_PREF_ROW_COUNT = 10;
   private static final int DEFAULT_PARAGRAPH_CAPACITY = 32;
   private BooleanProperty wrapText = new StyleableBooleanProperty(false) {
      public Object getBean() {
         return TextArea.this;
      }

      public String getName() {
         return "wrapText";
      }

      public CssMetaData<TextArea, Boolean> getCssMetaData() {
         return TextArea.StyleableProperties.WRAP_TEXT;
      }
   };
   private IntegerProperty prefColumnCount = new StyleableIntegerProperty(40) {
      private int oldValue = this.get();

      protected void invalidated() {
         int var1 = this.get();
         if (var1 < 0) {
            if (this.isBound()) {
               this.unbind();
            }

            this.set(this.oldValue);
            throw new IllegalArgumentException("value cannot be negative.");
         } else {
            this.oldValue = var1;
         }
      }

      public CssMetaData<TextArea, Number> getCssMetaData() {
         return TextArea.StyleableProperties.PREF_COLUMN_COUNT;
      }

      public Object getBean() {
         return TextArea.this;
      }

      public String getName() {
         return "prefColumnCount";
      }
   };
   private IntegerProperty prefRowCount = new StyleableIntegerProperty(10) {
      private int oldValue = this.get();

      protected void invalidated() {
         int var1 = this.get();
         if (var1 < 0) {
            if (this.isBound()) {
               this.unbind();
            }

            this.set(this.oldValue);
            throw new IllegalArgumentException("value cannot be negative.");
         } else {
            this.oldValue = var1;
         }
      }

      public CssMetaData<TextArea, Number> getCssMetaData() {
         return TextArea.StyleableProperties.PREF_ROW_COUNT;
      }

      public Object getBean() {
         return TextArea.this;
      }

      public String getName() {
         return "prefRowCount";
      }
   };
   private DoubleProperty scrollTop = new SimpleDoubleProperty(this, "scrollTop", 0.0);
   private DoubleProperty scrollLeft = new SimpleDoubleProperty(this, "scrollLeft", 0.0);

   public TextArea() {
      this("");
   }

   public TextArea(String var1) {
      super(new TextArea.TextAreaContent());
      this.getStyleClass().add("text-area");
      this.setAccessibleRole(AccessibleRole.TEXT_AREA);
      this.setText(var1);
   }

   @Override
   final void textUpdated() {
      this.setScrollTop(0.0);
      this.setScrollLeft(0.0);
   }

   public ObservableList<CharSequence> getParagraphs() {
      return ((TextArea.TextAreaContent)this.getContent()).paragraphList;
   }

   public final BooleanProperty wrapTextProperty() {
      return this.wrapText;
   }

   public final boolean isWrapText() {
      return this.wrapText.getValue();
   }

   public final void setWrapText(boolean var1) {
      this.wrapText.setValue(var1);
   }

   public final IntegerProperty prefColumnCountProperty() {
      return this.prefColumnCount;
   }

   public final int getPrefColumnCount() {
      return this.prefColumnCount.getValue();
   }

   public final void setPrefColumnCount(int var1) {
      this.prefColumnCount.setValue(var1);
   }

   public final IntegerProperty prefRowCountProperty() {
      return this.prefRowCount;
   }

   public final int getPrefRowCount() {
      return this.prefRowCount.getValue();
   }

   public final void setPrefRowCount(int var1) {
      this.prefRowCount.setValue(var1);
   }

   public final DoubleProperty scrollTopProperty() {
      return this.scrollTop;
   }

   public final double getScrollTop() {
      return this.scrollTop.getValue();
   }

   public final void setScrollTop(double var1) {
      this.scrollTop.setValue(var1);
   }

   public final DoubleProperty scrollLeftProperty() {
      return this.scrollLeft;
   }

   public final double getScrollLeft() {
      return this.scrollLeft.getValue();
   }

   public final void setScrollLeft(double var1) {
      this.scrollLeft.setValue(var1);
   }

   @Override
   protected Skin<?> createDefaultSkin() {
      return new TextAreaSkin(this);
   }

   @Override
   String filterInput(String var1) {
      return TextInputControl.filterInput(var1, false, false);
   }

   public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
      return TextArea.StyleableProperties.STYLEABLES;
   }

   @Override
   public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
      return getClassCssMetaData();
   }

   private static final class ParagraphList extends AbstractList<CharSequence> implements ObservableList<CharSequence> {
      private TextArea.TextAreaContent content;

      public CharSequence get(int var1) {
         return this.content.paragraphs.get(var1);
      }

      @Override
      public boolean addAll(Collection<? extends CharSequence> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(CharSequence... var1) {
         throw new UnsupportedOperationException();
      }

      public boolean setAll(Collection<? extends CharSequence> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean setAll(CharSequence... var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public int size() {
         return this.content.paragraphs.size();
      }

      public void addListener(ListChangeListener<? super CharSequence> var1) {
         this.content.listenerHelper = ListListenerHelper.addListener(this.content.listenerHelper, var1);
      }

      public void removeListener(ListChangeListener<? super CharSequence> var1) {
         this.content.listenerHelper = ListListenerHelper.removeListener(this.content.listenerHelper, var1);
      }

      public boolean removeAll(CharSequence... var1) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(CharSequence... var1) {
         throw new UnsupportedOperationException();
      }

      public void remove(int var1, int var2) {
         throw new UnsupportedOperationException();
      }

      public void addListener(InvalidationListener var1) {
         this.content.listenerHelper = ListListenerHelper.addListener(this.content.listenerHelper, var1);
      }

      public void removeListener(InvalidationListener var1) {
         this.content.listenerHelper = ListListenerHelper.removeListener(this.content.listenerHelper, var1);
      }
   }

   private static final class ParagraphListChange extends NonIterableChange<CharSequence> {
      private List<CharSequence> removed;

      protected ParagraphListChange(ObservableList<CharSequence> var1, int var2, int var3, List<CharSequence> var4) {
         super(var2, var3, var1);
         this.removed = var4;
      }

      public List<CharSequence> getRemoved() {
         return this.removed;
      }

      protected int[] getPermutation() {
         return new int[0];
      }
   }

   private static class StyleableProperties {
      private static final CssMetaData<TextArea, Number> PREF_COLUMN_COUNT = new CssMetaData<TextArea, Number>(
         "-fx-pref-column-count", SizeConverter.getInstance(), 40
      ) {
         public boolean isSettable(TextArea var1) {
            return !var1.prefColumnCount.isBound();
         }

         public StyleableProperty<Number> getStyleableProperty(TextArea var1) {
            return (StyleableProperty<Number>)var1.prefColumnCountProperty();
         }
      };
      private static final CssMetaData<TextArea, Number> PREF_ROW_COUNT = new CssMetaData<TextArea, Number>(
         "-fx-pref-row-count", SizeConverter.getInstance(), 10
      ) {
         public boolean isSettable(TextArea var1) {
            return !var1.prefRowCount.isBound();
         }

         public StyleableProperty<Number> getStyleableProperty(TextArea var1) {
            return (StyleableProperty<Number>)var1.prefRowCountProperty();
         }
      };
      private static final CssMetaData<TextArea, Boolean> WRAP_TEXT = new CssMetaData<TextArea, Boolean>(
         "-fx-wrap-text", StyleConverter.getBooleanConverter(), false
      ) {
         public boolean isSettable(TextArea var1) {
            return !var1.wrapText.isBound();
         }

         public StyleableProperty<Boolean> getStyleableProperty(TextArea var1) {
            return (StyleableProperty<Boolean>)var1.wrapTextProperty();
         }
      };
      private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

      static {
         ArrayList var0 = new ArrayList<>(TextInputControl.getClassCssMetaData());
         var0.add(PREF_COLUMN_COUNT);
         var0.add(PREF_ROW_COUNT);
         var0.add(WRAP_TEXT);
         STYLEABLES = Collections.unmodifiableList(var0);
      }
   }

   private static final class TextAreaContent implements TextInputControl.Content {
      private ExpressionHelper<String> helper = null;
      private ArrayList<StringBuilder> paragraphs = new ArrayList<>();
      private int contentLength = 0;
      private TextArea.ParagraphList paragraphList = new TextArea.ParagraphList();
      private ListListenerHelper<CharSequence> listenerHelper;

      private TextAreaContent() {
         this.paragraphs.add(new StringBuilder(32));
         this.paragraphList.content = this;
      }

      @Override
      public String get(int var1, int var2) {
         int var3 = var2 - var1;
         StringBuilder var4 = new StringBuilder(var3);
         int var5 = this.paragraphs.size();
         int var6 = 0;

         int var7;
         for (var7 = var1; var6 < var5; var6++) {
            StringBuilder var8 = this.paragraphs.get(var6);
            int var9 = var8.length() + 1;
            if (var7 < var9) {
               break;
            }

            var7 -= var9;
         }

         StringBuilder var10 = this.paragraphs.get(var6);

         for (int var11 = 0; var11 < var3; var11++) {
            if (var7 == var10.length() && var11 < this.contentLength) {
               var4.append('\n');
               var10 = this.paragraphs.get(++var6);
               var7 = 0;
            } else {
               var4.append(var10.charAt(var7++));
            }
         }

         return var4.toString();
      }

      @Override
      public void insert(int var1, String var2, boolean var3) {
         if (var1 < 0 || var1 > this.contentLength) {
            throw new IndexOutOfBoundsException();
         } else if (var2 == null) {
            throw new IllegalArgumentException();
         } else {
            var2 = TextInputControl.filterInput(var2, false, false);
            int var4 = var2.length();
            if (var4 > 0) {
               ArrayList var5 = new ArrayList();
               StringBuilder var6 = new StringBuilder(32);

               for (int var7 = 0; var7 < var4; var7++) {
                  char var8 = var2.charAt(var7);
                  if (var8 == '\n') {
                     var5.add(var6);
                     var6 = new StringBuilder(32);
                  } else {
                     var6.append(var8);
                  }
               }

               var5.add(var6);
               int var16 = this.paragraphs.size();
               int var17 = this.contentLength + 1;
               StringBuilder var9 = null;

               do {
                  var9 = this.paragraphs.get(--var16);
                  var17 -= var9.length() + 1;
               } while (var1 < var17);

               int var10 = var1 - var17;
               int var11 = var5.size();
               if (var11 == 1) {
                  var9.insert(var10, (CharSequence)var6);
                  this.fireParagraphListChangeEvent(var16, var16 + 1, Collections.singletonList(var9));
               } else {
                  int var12 = var9.length();
                  CharSequence var13 = var9.subSequence(var10, var12);
                  var9.delete(var10, var12);
                  StringBuilder var14 = (StringBuilder)var5.get(0);
                  var9.insert(var10, (CharSequence)var14);
                  var6.append(var13);
                  this.fireParagraphListChangeEvent(var16, var16 + 1, Collections.singletonList(var9));
                  this.paragraphs.addAll(var16 + 1, var5.subList(1, var11));
                  this.fireParagraphListChangeEvent(var16 + 1, var16 + var11, Collections.EMPTY_LIST);
               }

               this.contentLength += var4;
               if (var3) {
                  ExpressionHelper.fireValueChangedEvent(this.helper);
               }
            }
         }
      }

      @Override
      public void delete(int var1, int var2, boolean var3) {
         if (var1 > var2) {
            throw new IllegalArgumentException();
         } else if (var1 >= 0 && var2 <= this.contentLength) {
            int var4 = var2 - var1;
            if (var4 > 0) {
               int var5 = this.paragraphs.size();
               int var6 = this.contentLength + 1;
               StringBuilder var7 = null;

               do {
                  var7 = this.paragraphs.get(--var5);
                  var6 -= var7.length() + 1;
               } while (var2 < var6);

               int var8 = var5++;
               var6 += var7.length() + 1;

               do {
                  var7 = this.paragraphs.get(--var5);
                  var6 -= var7.length() + 1;
               } while (var1 < var6);

               if (var5 == var8) {
                  var7.delete(var1 - var6, var2 - var6);
                  this.fireParagraphListChangeEvent(var5, var5 + 1, Collections.singletonList(var7));
               } else {
                  CharSequence var14 = var7.subSequence(0, var1 - var6);
                  int var15 = var1 + var4 - var6;
                  var7.delete(0, var15);
                  this.fireParagraphListChangeEvent(var8, var8 + 1, Collections.singletonList(var7));
                  if (var8 - var5 > 0) {
                     ArrayList var16 = new ArrayList<>(this.paragraphs.subList(var5, var8));
                     this.paragraphs.subList(var5, var8).clear();
                     this.fireParagraphListChangeEvent(var5, var5, var16);
                  }

                  var7.insert(0, var14);
                  this.fireParagraphListChangeEvent(var5, var5 + 1, Collections.singletonList(var7));
               }

               this.contentLength -= var4;
               if (var3) {
                  ExpressionHelper.fireValueChangedEvent(this.helper);
               }
            }
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      @Override
      public int length() {
         return this.contentLength;
      }

      public String get() {
         return this.get(0, this.length());
      }

      public void addListener(ChangeListener<? super String> var1) {
         this.helper = ExpressionHelper.addListener(this.helper, this, var1);
      }

      public void removeListener(ChangeListener<? super String> var1) {
         this.helper = ExpressionHelper.removeListener(this.helper, var1);
      }

      public String getValue() {
         return this.get();
      }

      public void addListener(InvalidationListener var1) {
         this.helper = ExpressionHelper.addListener(this.helper, this, var1);
      }

      public void removeListener(InvalidationListener var1) {
         this.helper = ExpressionHelper.removeListener(this.helper, var1);
      }

      private void fireParagraphListChangeEvent(int var1, int var2, List<CharSequence> var3) {
         TextArea.ParagraphListChange var4 = new TextArea.ParagraphListChange(this.paragraphList, var1, var2, var3);
         ListListenerHelper.fireValueChangedEvent(this.listenerHelper, var4);
      }
   }
}
