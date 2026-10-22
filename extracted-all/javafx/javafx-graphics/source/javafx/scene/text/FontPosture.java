package javafx.scene.text;

public enum FontPosture {
   REGULAR("", "regular"),
   ITALIC("italic");

   private final String[] names;

   private FontPosture(String... var3) {
      this.names = var3;
   }

   public static FontPosture findByName(String var0) {
      if (var0 == null) {
         return null;
      } else {
         for (FontPosture var4 : values()) {
            for (String var8 : var4.names) {
               if (var8.equalsIgnoreCase(var0)) {
                  return var4;
               }
            }
         }

         return null;
      }
   }
}
