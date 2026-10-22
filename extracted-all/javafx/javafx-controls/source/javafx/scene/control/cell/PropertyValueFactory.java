package javafx.scene.control.cell;

import com.sun.javafx.logging.PlatformLogger;
import com.sun.javafx.logging.PlatformLogger.Level;
import com.sun.javafx.property.PropertyReference;
import com.sun.javafx.scene.control.Logging;
import javafx.beans.NamedArg;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class PropertyValueFactory<S, T> implements Callback<TableColumn.CellDataFeatures<S, T>, ObservableValue<T>> {
   private final String property;
   private Class<?> columnClass;
   private String previousProperty;
   private PropertyReference<T> propertyRef;

   public PropertyValueFactory(@NamedArg("property") String var1) {
      this.property = var1;
   }

   public ObservableValue<T> call(TableColumn.CellDataFeatures<S, T> var1) {
      return this.getCellDataReflectively((S)var1.getValue());
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
               if (this.propertyRef.hasProperty()) {
                  return this.propertyRef.getProperty(var1);
               }

               Object var2 = this.propertyRef.get(var1);
               return new ReadOnlyObjectWrapper(var2);
            }
         } catch (RuntimeException var4) {
            PlatformLogger var3 = Logging.getControlsLogger();
            if (var3.isLoggable(Level.WARNING)) {
               var3.warning(
                  "Can not retrieve property '" + this.getProperty() + "' in PropertyValueFactory: " + this + " with provided class type: " + var1.getClass(),
                  var4
               );
            }

            this.propertyRef = null;
         }

         return null;
      } else {
         return null;
      }
   }
}
