package com.yoctopuce.YoctoAPI;

class YJSONString extends YJSONContent {
   private String _stringValue;

   YJSONString(String data, int start, int stop) {
      super(data, start, stop, YJSONContent.YJSONType.STRING);
   }

   YJSONString(String data) {
      super(data, 0, data.length(), YJSONContent.YJSONType.STRING);
   }

   YJSONString() {
      super(YJSONContent.YJSONType.STRING);
   }

   @Override
   int parse() throws Exception {
      StringBuilder value = new StringBuilder();
      int cur_pos = SkipGarbage(this._data, this._data_start, this._data_boundary);
      if (cur_pos < this._data_boundary && this._data.charAt(cur_pos) == '"') {
         int str_start = ++cur_pos;

         for (YJSONContent.Tjstate state = YJSONContent.Tjstate.JWAITFORSTRINGVALUE; cur_pos < this._data_boundary; cur_pos++) {
            char sti = this._data.charAt(cur_pos);
            switch (state) {
               case JWAITFORSTRINGVALUE:
                  if (sti == '\\') {
                     value.append(this._data.substring(str_start, cur_pos));
                     str_start = cur_pos;
                     state = YJSONContent.Tjstate.JWAITFORSTRINGVALUE_ESC;
                  } else {
                     if (sti == '"') {
                        value.append(this._data.substring(str_start, cur_pos));
                        this._stringValue = value.toString();
                        this._data_len = cur_pos + 1 - this._data_start;
                        return this._data_len;
                     }

                     if (sti < ' ') {
                        throw new Exception(this.formatError("invalid char: was expecting string value", cur_pos));
                     }
                  }
                  break;
               case JWAITFORSTRINGVALUE_ESC:
                  value.append(sti);
                  state = YJSONContent.Tjstate.JWAITFORSTRINGVALUE;
                  str_start = cur_pos + 1;
                  break;
               default:
                  throw new Exception(this.formatError("invalid state for YJSONObject", cur_pos));
            }
         }

         throw new Exception(this.formatError("unexpected end of data", cur_pos));
      } else {
         throw new Exception(this.formatError("double quote was expected", cur_pos));
      }
   }

   @Override
   String toJSON() {
      StringBuilder res = new StringBuilder(this._stringValue.length() * 2);
      res.append('"');
      int len = this._stringValue.length();

      for (int i = 0; i < len; i++) {
         char c = this._stringValue.charAt(i);
         switch (c) {
            case '\b':
               res.append("\\b");
               break;
            case '\t':
               res.append("\\t");
               break;
            case '\n':
               res.append("\\n");
               break;
            case '\f':
               res.append("\\f");
               break;
            case '\r':
               res.append("\\r");
               break;
            case '"':
               res.append("\\\"");
               break;
            case '/':
               res.append("\\/");
               break;
            case '\\':
               res.append("\\\\");
               break;
            default:
               res.append(c);
         }
      }

      res.append('"');
      return res.toString();
   }

   String getString() {
      return this._stringValue;
   }

   @Override
   public String toString() {
      return this._stringValue;
   }

   void setContent(String value) {
      this._stringValue = value;
   }
}
