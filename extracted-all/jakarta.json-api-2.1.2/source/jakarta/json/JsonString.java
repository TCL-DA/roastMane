package jakarta.json;

public interface JsonString extends JsonValue {
   String getString();

   CharSequence getChars();

   @Override
   boolean equals(Object var1);

   @Override
   int hashCode();
}
