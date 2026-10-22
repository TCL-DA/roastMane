package javafx.beans.value;

@FunctionalInterface
public interface ChangeListener<T> {
   void changed(ObservableValue<? extends T> var1, T var2, T var3);
}
