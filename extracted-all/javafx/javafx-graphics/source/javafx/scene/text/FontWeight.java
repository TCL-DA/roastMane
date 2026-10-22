package javafx.scene.text;

public enum FontWeight {
   THIN(100, "Thin"),
   EXTRA_LIGHT(200, "Extra Light", "Ultra Light"),
   LIGHT(300, "Light"),
   NORMAL(400, "Normal", "Regular"),
   MEDIUM(500, "Medium"),
   SEMI_BOLD(600, "Semi Bold", "Demi Bold"),
   BOLD(700, "Bold"),
   EXTRA_BOLD(800, "Extra Bold", "Ultra Bold"),
   BLACK(900, "Black", "Heavy");

   private final int weight;
   private final String[] names;

   private FontWeight(int var3, String... var4) {
      this.weight = var3;
      this.names = var4;
   }

   public int getWeight() {
      return this.weight;
   }

   public static FontWeight findByName(String var0) {
      if (var0 == null) {
         return null;
      } else {
         for (FontWeight var4 : values()) {
            for (String var8 : var4.names) {
               if (var8.equalsIgnoreCase(var0)) {
                  return var4;
               }
            }
         }

         return null;
      }
   }

   public static FontWeight findByWeight(int var0) {
      if (var0 <= 150) {
         return THIN;
      } else if (var0 <= 250) {
         return EXTRA_LIGHT;
      } else if (var0 < 350) {
         return LIGHT;
      } else if (var0 <= 450) {
         return NORMAL;
      } else if (var0 <= 550) {
         return MEDIUM;
      } else if (var0 < 650) {
         return SEMI_BOLD;
      } else if (var0 <= 750) {
         return BOLD;
      } else {
         return var0 <= 850 ? EXTRA_BOLD : BLACK;
      }
   }
}
