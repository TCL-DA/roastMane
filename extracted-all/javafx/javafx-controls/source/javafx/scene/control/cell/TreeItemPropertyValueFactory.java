package javafx.scene.control.cell;

import com.sun.javafx.logging.PlatformLogger;
import com.sun.javafx.logging.PlatformLogger.Level;
import com.sun.javafx.property.PropertyReference;
import com.sun.javafx.scene.control.Logging;
import javafx.beans.NamedArg;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;

public class TreeItemPropertyValueFactory<S, T> implements Callback<TreeTableColumn.CellDataFeatures<S, T>, ObservableValue<T>> {
   private final String property;
   private Class<?> columnClass;
   private String previousProperty;
   private PropertyReference<T> propertyRef;

   public TreeItemPropertyValueFactory(@NamedArg("property") String var1) {
      this.property = var1;
   }

   public ObservableValue<T> call(TreeTableColumn.CellDataFeatures<S, T> var1) {
      TreeItem var2 = var1.getValue();
      return this.getCellDataReflectively((S)var2.getValue());
   }

   public final String getProperty() {
      return this.property;
   }

   private ObservableValue<T> getCellDataReflectively(S var1) {
      if (this.getProperty() != null && !this.getProperty().isEmpty() && var1 != null) {
         try {
            if (this.columnClass == null
               || this.previousProperty == null
               || !this.columnClass.equals(var1.getClass())
               || !this.previousProperty.equals(this.getProperty())) {
               this.columnClass = var1.getClass();
               this.previousProperty = this.getProperty();
               this.propertyRef = new PropertyReference(var1.getClass(), this.getProperty());
            }

            if (this.propertyRef != null) {
               return this.propertyRef.getProperty(var1);
            }
         } catch (RuntimeException var5) {
            try {
               Object var6 = this.propertyRef.get(var1);
               return new ReadOnlyObjectWrapper(var6);
            } catch (RuntimeException var4) {
               PlatformLogger var3 = Logging.getControlsLogger();
               if (var3.isLoggable(Level.WARNING)) {
                  var3.warning(
                     "Can not retrieve property '"
                        + this.getProperty()
                        + "' in TreeItemPropertyValueFactory: "
                        + this
                        + " with provided class type: "
                        + var1.getClass(),
                     var5
                  );
               }

               this.propertyRef = null;
            }
         }

         return null;
      } else {
         return null;
      }
   }
}
