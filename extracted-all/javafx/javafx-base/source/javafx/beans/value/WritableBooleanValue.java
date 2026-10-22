package javafx.beans.value;

public interface WritableBooleanValue extends WritableValue<Boolean> {
   boolean get();

   void set(boolean var1);

   void setValue(Boolean var1);
}
