package com.yoctopuce.YoctoAPI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

class YJSONObject extends YJSONContent {
   private HashMap<String, YJSONContent> _parsed = new HashMap<>();
   private ArrayList<String> _keys = new ArrayList<>(16);

   YJSONObject(String data) {
      super(data, 0, data.length(), YJSONContent.YJSONType.OBJECT);
   }

   YJSONObject(String data, int start, int len) {
      super(data, start, len, YJSONContent.YJSONType.OBJECT);
   }

   @Override
   int parse() throws Exception {
      String current_name = "";
      int name_start = this._data_start;
      int cur_pos = SkipGarbage(this._data, this._data_start, this._data_boundary);
      if (this._data.length() > cur_pos && cur_pos < this._data_boundary && this._data.charAt(cur_pos) == '{') {
         cur_pos++;
         YJSONContent.Tjstate state = YJSONContent.Tjstate.JWAITFORNAME;

         while (cur_pos < this._data_boundary) {
            char sti = this._data.charAt(cur_pos);
            switch (state) {
               case JWAITFORNAME:
                  if (sti == '"') {
                     state = YJSONContent.Tjstate.JWAITFORENDOFNAME;
                     name_start = cur_pos + 1;
                  } else {
                     if (sti == '}') {
                        this._data_len = cur_pos + 1 - this._data_start;
                        return this._data_len;
                     }

                     if (sti != ' ' && sti != '\n' && sti != '\r') {
                        throw new Exception(this.formatError("invalid char: was expecting \"", cur_pos));
                     }
                  }
                  break;
               case JWAITFORENDOFNAME:
                  if (sti == '"') {
                     current_name = this._data.substring(name_start, cur_pos);
                     state = YJSONContent.Tjstate.JWAITFORCOLON;
                  } else if (sti < ' ') {
                     throw new Exception(this.formatError("invalid char: was expecting an identifier compliant char", cur_pos));
                  }
                  break;
               case JWAITFORCOLON:
                  if (sti == ':') {
                     state = YJSONContent.Tjstate.JWAITFORDATA;
                  } else if (sti != ' ' && sti != '\n' && sti != '\r') {
                     throw new Exception(this.formatError("invalid char: was expecting \"", cur_pos));
                  }
                  break;
               case JWAITFORDATA:
                  if (sti == '{') {
                     YJSONObject jobj = new YJSONObject(this._data, cur_pos, this._data_boundary);
                     int len = jobj.parse();
                     cur_pos += len;
                     this._parsed.put(current_name, jobj);
                     this._keys.add(current_name);
                     state = YJSONContent.Tjstate.JWAITFORNEXTSTRUCTMEMBER;
                     continue;
                  }

                  if (sti == '[') {
                     YJSONArray jobj = new YJSONArray(this._data, cur_pos, this._data_boundary);
                     int len = jobj.parse();
                     cur_pos += len;
                     this._parsed.put(current_name, jobj);
                     this._keys.add(current_name);
                     state = YJSONContent.Tjstate.JWAITFORNEXTSTRUCTMEMBER;
                     continue;
                  }

                  if (sti == '"') {
                     YJSONString jobj = new YJSONString(this._data, cur_pos, this._data_boundary);
                     int len = jobj.parse();
                     cur_pos += len;
                     this._parsed.put(current_name, jobj);
                     this._keys.add(current_name);
                     state = YJSONContent.Tjstate.JWAITFORNEXTSTRUCTMEMBER;
                     continue;
                  }

                  if (sti == '-' || sti >= '0' && sti <= '9') {
                     YJSONNumber jobj = new YJSONNumber(this._data, cur_pos, this._data_boundary);
                     int len = jobj.parse();
                     cur_pos += len;
                     this._parsed.put(current_name, jobj);
                     this._keys.add(current_name);
                     state = YJSONContent.Tjstate.JWAITFORNEXTSTRUCTMEMBER;
                     continue;
                  }

                  if (sti != ' ' && sti != '\n' && sti != '\r') {
                     throw new Exception(this.formatError("invalid char: was expecting  \",0..9,t or f", cur_pos));
                  }
                  break;
               case JWAITFORNEXTSTRUCTMEMBER:
                  if (sti == ',') {
                     state = YJSONContent.Tjstate.JWAITFORNAME;
                     name_start = cur_pos + 1;
                  } else {
                     if (sti == '}') {
                        this._data_len = cur_pos + 1 - this._data_start;
                        return this._data_len;
                     }

                     if (sti != ' ' && sti != '\n' && sti != '\r') {
                        throw new Exception(this.formatError("invalid char: was expecting ,", cur_pos));
                     }
                  }
                  break;
               case JWAITFORNEXTARRAYITEM:
               case JWAITFORSTRINGVALUE:
               case JWAITFORINTVALUE:
               case JWAITFORBOOLVALUE:
                  throw new Exception(this.formatError("invalid state for YJSONObject", cur_pos));
            }

            cur_pos++;
         }

         throw new Exception(this.formatError("unexpected end of data", cur_pos));
      } else {
         throw new Exception(this.formatError("Opening braces was expected", cur_pos));
      }
   }

   boolean has(String key) {
      return this._parsed.containsKey(key);
   }

   YJSONObject getYJSONObject(String key) {
      return (YJSONObject)this._parsed.get(key);
   }

   YJSONString getYJSONString(String key) {
      return (YJSONString)this._parsed.get(key);
   }

   YJSONArray getYJSONArray(String key) {
      return (YJSONArray)this._parsed.get(key);
   }

   Set<String> getKeys() {
      return this._parsed.keySet();
   }

   YJSONNumber getYJSONNumber(String key) {
      return (YJSONNumber)this._parsed.get(key);
   }

   void remove(String key) {
      this._parsed.remove(key);
   }

   String getString(String key) {
      YJSONString ystr = (YJSONString)this._parsed.get(key);
      return ystr.getString();
   }

   int getInt(String key) {
      YJSONNumber yint = (YJSONNumber)this._parsed.get(key);
      return yint.getInt();
   }

   YJSONContent get(String key) {
      return this._parsed.get(key);
   }

   long getLong(String key) {
      YJSONNumber yint = (YJSONNumber)this._parsed.get(key);
      return yint.getLong();
   }

   double getDouble(String key) {
      YJSONNumber yint = (YJSONNumber)this._parsed.get(key);
      return yint.getDouble();
   }

   @Override
   String toJSON() {
      StringBuilder res = new StringBuilder();
      res.append('{');
      String sep = "";

      for (String key : this._parsed.keySet()) {
         YJSONContent subContent = this._parsed.get(key);
         String subres = subContent.toJSON();
         res.append(sep);
         res.append('"');
         res.append(key);
         res.append("\":");
         res.append(subres);
         sep = ",";
      }

      res.append('}');
      return res.toString();
   }

   @Override
   public String toString() {
      StringBuilder res = new StringBuilder();
      res.append('{');
      String sep = "";

      for (String key : this._parsed.keySet()) {
         YJSONContent subContent = this._parsed.get(key);
         String subres = subContent.toString();
         res.append(sep);
         res.append(key);
         res.append("=>");
         res.append(subres);
         sep = ",";
      }

      res.append('}');
      return res.toString();
   }

   public void parseWithRef(YJSONObject reference) throws Exception {
      if (reference != null) {
         try {
            YJSONArray yzon = new YJSONArray(this._data, this._data_start, this._data_boundary);
            yzon.parse();
            this.convert(reference, yzon);
            return;
         } catch (Exception var3) {
         }
      }

      this.parse();
   }

   private void convert(YJSONObject reference, YJSONArray newArray) throws Exception {
      int length = newArray.length();

      for (int i = 0; i < length; i++) {
         String key = reference.getKeyFromIdx(i);
         YJSONContent new_item = newArray.get(i);
         YJSONContent reference_item = reference.get(key);
         if (new_item.getJSONType() == reference_item.getJSONType()) {
            this._parsed.put(key, new_item);
            this._keys.add(key);
         } else {
            if (new_item.getJSONType() != YJSONContent.YJSONType.ARRAY || reference_item.getJSONType() != YJSONContent.YJSONType.OBJECT) {
               throw new Exception(String.format("Unable to convert %s to %s", new_item.getJSONType().toString(), reference.getJSONType().toString()));
            }

            YJSONObject jobj = new YJSONObject(new_item._data, new_item._data_start, reference_item._data_boundary);
            jobj.convert((YJSONObject)reference_item, (YJSONArray)new_item);
            this._parsed.put(key, jobj);
            this._keys.add(key);
         }
      }
   }

   private String getKeyFromIdx(int i) {
      return this._keys.get(i);
   }
}
