package org.controlsfx.property;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.function.Predicate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.controlsfx.control.PropertySheet;

public final class BeanPropertyUtils {
   private BeanPropertyUtils() {
   }

   public static ObservableList<PropertySheet.Item> getProperties(Object bean) {
      return getProperties(bean, p -> true);
   }

   public static ObservableList<PropertySheet.Item> getProperties(Object bean, Predicate<PropertyDescriptor> test) {
      ObservableList<PropertySheet.Item> list = FXCollections.observableArrayList();

      try {
         BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass(), Object.class);

         for (PropertyDescriptor p : beanInfo.getPropertyDescriptors()) {
            if (test.test(p)) {
               list.add(new BeanProperty(bean, p));
            }
         }
      } catch (IntrospectionException var8) {
         var8.printStackTrace();
      }

      return list;
   }
}
