package org.controlsfx.property;

import impl.org.controlsfx.i18n.Localization;
import java.beans.PropertyDescriptor;
import java.beans.PropertyVetoException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.editor.PropertyEditor;

public class BeanProperty implements PropertySheet.Item {
   public static final String CATEGORY_LABEL_KEY = "propertysheet.item.category.label";
   private final Object bean;
   private final PropertyDescriptor beanPropertyDescriptor;
   private final Method readMethod;
   private boolean editable = true;
   private Optional<ObservableValue<? extends Object>> observableValue = Optional.empty();

   public BeanProperty(Object bean, PropertyDescriptor propertyDescriptor) {
      this.bean = bean;
      this.beanPropertyDescriptor = propertyDescriptor;
      this.readMethod = propertyDescriptor.getReadMethod();
      if (this.beanPropertyDescriptor.getWriteMethod() == null) {
         this.setEditable(false);
      }

      this.findObservableValue();
   }

   @Override
   public String getName() {
      return this.beanPropertyDescriptor.getDisplayName();
   }

   @Override
   public String getDescription() {
      return this.beanPropertyDescriptor.getShortDescription();
   }

   @Override
   public Class<?> getType() {
      return this.beanPropertyDescriptor.getPropertyType();
   }

   @Override
   public Object getValue() {
      try {
         return this.readMethod.invoke(this.bean);
      } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var2) {
         var2.printStackTrace();
         return null;
      }
   }

   @Override
   public void setValue(Object value) {
      Method writeMethod = this.beanPropertyDescriptor.getWriteMethod();
      if (writeMethod != null) {
         try {
            writeMethod.invoke(this.bean, value);
         } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var5) {
            var5.printStackTrace();
         } catch (Throwable var6) {
            if (!(var6 instanceof PropertyVetoException)) {
               throw var6;
            }

            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle(Localization.localize(Localization.asKey("bean.property.change.error.title")));
            alert.setHeaderText(Localization.localize(Localization.asKey("bean.property.change.error.masthead")));
            alert.setContentText(var6.getLocalizedMessage());
            alert.showAndWait();
         }
      }
   }

   @Override
   public String getCategory() {
      String category = (String)this.beanPropertyDescriptor.getValue("propertysheet.item.category.label");
      if (category == null) {
         category = Localization.localize(
            Localization.asKey(this.beanPropertyDescriptor.isExpert() ? "bean.property.category.expert" : "bean.property.category.basic")
         );
      }

      return category;
   }

   public Object getBean() {
      return this.bean;
   }

   public PropertyDescriptor getPropertyDescriptor() {
      return this.beanPropertyDescriptor;
   }

   @Override
   public Optional<Class<? extends PropertyEditor<?>>> getPropertyEditorClass() {
      return this.beanPropertyDescriptor.getPropertyEditorClass() != null
            && PropertyEditor.class.isAssignableFrom(this.beanPropertyDescriptor.getPropertyEditorClass())
         ? Optional.of((Class<? extends PropertyEditor<?>>)this.beanPropertyDescriptor.getPropertyEditorClass())
         : PropertySheet.Item.super.getPropertyEditorClass();
   }

   @Override
   public boolean isEditable() {
      return this.editable;
   }

   public void setEditable(boolean editable) {
      this.editable = editable;
   }

   @Override
   public Optional<ObservableValue<? extends Object>> getObservableValue() {
      return this.observableValue;
   }

   private void findObservableValue() {
      try {
         String propName = this.beanPropertyDescriptor.getName() + "Property";
         Method m = this.getBean().getClass().getMethod(propName);
         Object val = m.invoke(this.getBean());
         if (val != null && val instanceof ObservableValue) {
            this.observableValue = Optional.of((ObservableValue<? extends Object>)val);
         }
      } catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException var4) {
      }
   }
}
