package javafx.css;

public final class StyleClass {
   private final String styleClassName;
   private final int index;

   public StyleClass(String var1, int var2) {
      this.styleClassName = var1;
      this.index = var2;
   }

   public String getStyleClassName() {
      return this.styleClassName;
   }

   @Override
   public String toString() {
      return this.styleClassName;
   }

   public int getIndex() {
      return this.index;
   }
}
