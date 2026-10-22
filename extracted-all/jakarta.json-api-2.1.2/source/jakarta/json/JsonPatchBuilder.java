package jakarta.json;

public interface JsonPatchBuilder {
   JsonPatchBuilder add(String var1, JsonValue var2);

   JsonPatchBuilder add(String var1, String var2);

   JsonPatchBuilder add(String var1, int var2);

   JsonPatchBuilder add(String var1, boolean var2);

   JsonPatchBuilder remove(String var1);

   JsonPatchBuilder replace(String var1, JsonValue var2);

   JsonPatchBuilder replace(String var1, String var2);

   JsonPatchBuilder replace(String var1, int var2);

   JsonPatchBuilder replace(String var1, boolean var2);

   JsonPatchBuilder move(String var1, String var2);

   JsonPatchBuilder copy(String var1, String var2);

   JsonPatchBuilder test(String var1, JsonValue var2);

   JsonPatchBuilder test(String var1, String var2);

   JsonPatchBuilder test(String var1, int var2);

   JsonPatchBuilder test(String var1, boolean var2);

   JsonPatch build();
}
