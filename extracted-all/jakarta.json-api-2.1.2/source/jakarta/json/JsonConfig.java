package jakarta.json;

public final class JsonConfig {
   public static final String KEY_STRATEGY = "jakarta.json.JsonConfig.keyStrategy";

   private JsonConfig() {
   }

   public static enum KeyStrategy {
      FIRST,
      LAST,
      NONE;
   }
}
