package org.controlsfx.control;

import impl.org.controlsfx.skin.PropertySheetSkin;
import java.util.Comparator;
import java.util.Optional;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Skin;
import javafx.util.Callback;
import org.controlsfx.property.editor.DefaultPropertyEditorFactory;
import org.controlsfx.property.editor.PropertyEditor;

public class PropertySheet extends ControlsFXControl {
   private final ObservableList<PropertySheet.Item> items;
   private final SimpleObjectProperty<PropertySheet.Mode> modeProperty = new SimpleObjectProperty(this, "mode", PropertySheet.Mode.NAME);
   private final SimpleObjectProperty<Callback<PropertySheet.Item, PropertyEditor<?>>> propertyEditorFactory = new SimpleObjectProperty(
      this, "propertyEditor", new DefaultPropertyEditorFactory()
   );
   private final SimpleBooleanProperty modeSwitcherVisible = new SimpleBooleanProperty(this, "modeSwitcherVisible", true);
   private final SimpleBooleanProperty searchBoxVisible = new SimpleBooleanProperty(this, "searchBoxVisible", true);
   private final SimpleStringProperty titleFilterProperty = new SimpleStringProperty(this, "titleFilter", "");
   private final SimpleObjectProperty<Comparator<String>> categoryComparatorProperty = new SimpleObjectProperty(this, "categoryComparator", null);
   private static final String DEFAULT_STYLE_CLASS = "property-sheet";

   public PropertySheet() {
      this(null);
   }

   public PropertySheet(ObservableList<PropertySheet.Item> items) {
      this.getStyleClass().add("property-sheet");
      this.items = items == null ? FXCollections.observableArrayList() : items;
   }

   public ObservableList<PropertySheet.Item> getItems() {
      return this.items;
   }

   protected Skin<?> createDefaultSkin() {
      return new PropertySheetSkin(this);
   }

   public String getUserAgentStylesheet() {
      return this.getUserAgentStylesheet(PropertySheet.class, "propertysheet.css");
   }

   public final SimpleObjectProperty<PropertySheet.Mode> modeProperty() {
      return this.modeProperty;
   }

   public final PropertySheet.Mode getMode() {
      return (PropertySheet.Mode)this.modeProperty.get();
   }

   public final void setMode(PropertySheet.Mode mode) {
      this.modeProperty.set(mode);
   }

   public final SimpleObjectProperty<Callback<PropertySheet.Item, PropertyEditor<?>>> propertyEditorFactory() {
      return this.propertyEditorFactory;
   }

   public final Callback<PropertySheet.Item, PropertyEditor<?>> getPropertyEditorFactory() {
      return (Callback<PropertySheet.Item, PropertyEditor<?>>)this.propertyEditorFactory.get();
   }

   public final void setPropertyEditorFactory(Callback<PropertySheet.Item, PropertyEditor<?>> factory) {
      this.propertyEditorFactory.set(factory == null ? new DefaultPropertyEditorFactory() : factory);
   }

   public final SimpleBooleanProperty modeSwitcherVisibleProperty() {
      return this.modeSwitcherVisible;
   }

   public final boolean isModeSwitcherVisible() {
      return this.modeSwitcherVisible.get();
   }

   public final void setModeSwitcherVisible(boolean visible) {
      this.modeSwitcherVisible.set(visible);
   }

   public final SimpleBooleanProperty searchBoxVisibleProperty() {
      return this.searchBoxVisible;
   }

   public final boolean isSearchBoxVisible() {
      return this.searchBoxVisible.get();
   }

   public final void setSearchBoxVisible(boolean visible) {
      this.searchBoxVisible.set(visible);
   }

   public final SimpleStringProperty titleFilter() {
      return this.titleFilterProperty;
   }

   public final String getTitleFilter() {
      return this.titleFilterProperty.get();
   }

   public final void setTitleFilter(String filter) {
      this.titleFilterProperty.set(filter);
   }

   public final SimpleObjectProperty<Comparator<String>> categoryComparatorProperty() {
      return this.categoryComparatorProperty;
   }

   public final Comparator<String> getCategoryComparator() {
      return (Comparator<String>)this.categoryComparatorProperty.get();
   }

   public final void setCategoryComparator(Comparator<String> categoryComparator) {
      this.categoryComparatorProperty.set(categoryComparator);
   }

   public interface Item {
      Class<?> getType();

      String getCategory();

      String getName();

      String getDescription();

      Object getValue();

      void setValue(Object var1);

      Optional<ObservableValue<? extends Object>> getObservableValue();

      default Optional<Class<? extends PropertyEditor<?>>> getPropertyEditorClass() {
         return Optional.empty();
      }

      default boolean isEditable() {
         return true;
      }
   }

   public static enum Mode {
      NAME,
      CATEGORY;
   }
}
