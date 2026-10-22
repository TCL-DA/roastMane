package org.eclipse.yasson.internal.model.customization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import org.eclipse.yasson.internal.model.ClassModel;
import org.eclipse.yasson.internal.model.PropertyModel;

public class PropertyOrdering {
   private final Consumer<List<PropertyModel>> propertyOrderStrategy;

   public PropertyOrdering(Consumer<List<PropertyModel>> propertyOrderStrategy) {
      this.propertyOrderStrategy = Objects.requireNonNull(propertyOrderStrategy);
   }

   public List<PropertyModel> orderProperties(List<PropertyModel> properties, ClassModel classModel) {
      Map<String, PropertyModel> byReadName = new HashMap<>();
      properties.forEach(propertyModel -> byReadName.put(propertyModel.getPropertyName(), propertyModel));
      String[] order = classModel.getClassCustomization().getPropertyOrder();
      List<PropertyModel> sortedProperties = new ArrayList<>();
      if (order != null) {
         for (String propName : order) {
            PropertyModel remove = byReadName.remove(propName);
            if (remove != null) {
               sortedProperties.add(remove);
            }
         }
      }

      List<PropertyModel> readNamesToSort = new ArrayList<>(byReadName.values());
      this.propertyOrderStrategy.accept(readNamesToSort);
      sortedProperties.addAll(readNamesToSort);
      return sortedProperties;
   }
}
