package jakarta.json;

public interface JsonPatch {
   <T extends JsonStructure> T apply(T var1);

   JsonArray toJsonArray();

   public static enum Operation {
      ADD("add"),
      REMOVE("remove"),
      REPLACE("replace"),
      MOVE("move"),
      COPY("copy"),
      TEST("test");

      private final String operationName;

      private Operation(String operationName) {
         this.operationName = operationName;
      }

      public String operationName() {
         return this.operationName;
      }

      public static JsonPatch.Operation fromOperationName(String operationName) {
         for (JsonPatch.Operation op : values()) {
            if (op.operationName().equalsIgnoreCase(operationName)) {
               return op;
            }
         }

         throw new JsonException("Illegal value for the operationName of the JSON patch operation: " + operationName);
      }
   }
}
