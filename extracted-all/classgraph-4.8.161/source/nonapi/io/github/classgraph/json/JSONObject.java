package nonapi.io.github.classgraph.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

class JSONObject {
   List<Entry<String, Object>> items;
   CharSequence objectId;

   public JSONObject(int sizeHint) {
      this.items = new ArrayList<>(sizeHint);
   }

   public JSONObject(List<Entry<String, Object>> items) {
      this.items = items;
   }

   void toJSONString(
      Map<ReferenceEqualityKey<JSONReference>, CharSequence> jsonReferenceToId, boolean includeNullValuedFields, int depth, int indentWidth, StringBuilder buf
   ) {
      boolean prettyPrint = indentWidth > 0;
      int n = this.items.size();
      int numDisplayedFields;
      if (includeNullValuedFields) {
         numDisplayedFields = n;
      } else {
         numDisplayedFields = 0;

         for (Entry<String, Object> item : this.items) {
            if (item.getValue() != null) {
               numDisplayedFields++;
            }
         }
      }

      if (this.objectId == null && numDisplayedFields == 0) {
         buf.append("{}");
      } else {
         buf.append(prettyPrint ? "{\n" : "{");
         if (this.objectId != null) {
            if (prettyPrint) {
               JSONUtils.indent(depth + 1, indentWidth, buf);
            }

            buf.append('"');
            buf.append("__ID");
            buf.append(prettyPrint ? "\": " : "\":");
            JSONSerializer.jsonValToJSONString(this.objectId, jsonReferenceToId, includeNullValuedFields, depth + 1, indentWidth, buf);
            if (numDisplayedFields > 0) {
               buf.append(',');
            }

            if (prettyPrint) {
               buf.append('\n');
            }
         }

         int i = 0;

         for (int j = 0; i < n; i++) {
            Entry<String, Object> itemx = this.items.get(i);
            Object val = itemx.getValue();
            if (val != null || includeNullValuedFields) {
               String key = itemx.getKey();
               if (key == null) {
                  throw new IllegalArgumentException("Cannot serialize JSON object with null key");
               }

               if (prettyPrint) {
                  JSONUtils.indent(depth + 1, indentWidth, buf);
               }

               buf.append('"');
               JSONUtils.escapeJSONString(key, buf);
               buf.append(prettyPrint ? "\": " : "\":");
               JSONSerializer.jsonValToJSONString(val, jsonReferenceToId, includeNullValuedFields, depth + 1, indentWidth, buf);
               if (++j < numDisplayedFields) {
                  buf.append(',');
               }

               if (prettyPrint) {
                  buf.append('\n');
               }
            }
         }

         if (prettyPrint) {
            JSONUtils.indent(depth, indentWidth, buf);
         }

         buf.append('}');
      }
   }
}
