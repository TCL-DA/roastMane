package javafx.collections;

public interface ArrayChangeListener<T extends ObservableArray<T>> {
   void onChanged(T var1, boolean var2, int var3, int var4);
}
