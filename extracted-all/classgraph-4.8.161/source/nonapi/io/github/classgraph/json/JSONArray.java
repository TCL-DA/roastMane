package nonapi.io.github.classgraph.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class JSONArray {
   List<Object> items;

   public JSONArray() {
      this.items = new ArrayList<>();
   }

   public JSONArray(List<Object> items) {
      this.items = items;
   }

   void toJSONString(
      Map<ReferenceEqualityKey<JSONReference>, CharSequence> jsonReferenceToId, boolean includeNullValuedFields, int depth, int indentWidth, StringBuilder buf
   ) {
      boolean prettyPrint = indentWidth > 0;
      int n = this.items.size();
      if (n == 0) {
         buf.append("[]");
      } else {
         buf.append('[');
         if (prettyPrint) {
            buf.append('\n');
         }

         for (int i = 0; i < n; i++) {
            Object item = this.items.get(i);
            if (prettyPrint) {
               JSONUtils.indent(depth + 1, indentWidth, buf);
            }

            JSONSerializer.jsonValToJSONString(item, jsonReferenceToId, includeNullValuedFields, depth + 1, indentWidth, buf);
            if (i < n - 1) {
               buf.append(',');
            }

            if (prettyPrint) {
               buf.append('\n');
            }
         }

         if (prettyPrint) {
            JSONUtils.indent(depth, indentWidth, buf);
         }

         buf.append(']');
      }
   }
}
