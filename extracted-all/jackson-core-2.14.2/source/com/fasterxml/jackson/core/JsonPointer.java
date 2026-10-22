package com.fasterxml.jackson.core;

import com.fasterxml.jackson.core.io.NumberInput;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;
import java.io.Serializable;

public class JsonPointer implements Serializable {
   private static final long serialVersionUID = 1L;
   public static final char SEPARATOR = '/';
   protected static final JsonPointer EMPTY = new JsonPointer();
   protected final JsonPointer _nextSegment;
   protected volatile JsonPointer _head;
   protected final String _asString;
   protected final int _asStringOffset;
   protected final String _matchingPropertyName;
   protected final int _matchingElementIndex;
   protected int _hashCode;

   protected JsonPointer() {
      this._nextSegment = null;
      this._matchingPropertyName = null;
      this._matchingElementIndex = -1;
      this._asString = "";
      this._asStringOffset = 0;
   }

   protected JsonPointer(String fullString, int fullStringOffset, String segment, JsonPointer next) {
      this._asString = fullString;
      this._asStringOffset = fullStringOffset;
      this._nextSegment = next;
      this._matchingPropertyName = segment;
      this._matchingElementIndex = _parseIndex(segment);
   }

   protected JsonPointer(String fullString, int fullStringOffset, String segment, int matchIndex, JsonPointer next) {
      this._asString = fullString;
      this._asStringOffset = fullStringOffset;
      this._nextSegment = next;
      this._matchingPropertyName = segment;
      this._matchingElementIndex = matchIndex;
   }

   public static JsonPointer compile(String expr) throws IllegalArgumentException {
      if (expr != null && expr.length() != 0) {
         if (expr.charAt(0) != '/') {
            throw new IllegalArgumentException("Invalid input: JSON Pointer expression must start with '/': \"" + expr + "\"");
         } else {
            return _parseTail(expr);
         }
      } else {
         return EMPTY;
      }
   }

   public static JsonPointer valueOf(String expr) {
      return compile(expr);
   }

   public static JsonPointer empty() {
      return EMPTY;
   }

   public static JsonPointer forPath(JsonStreamContext context, boolean includeRoot) {
      if (context == null) {
         return EMPTY;
      } else {
         if (!context.hasPathSegment() && (!includeRoot || !context.inRoot() || !context.hasCurrentIndex())) {
            context = context.getParent();
         }

         JsonPointer.PointerSegment next = null;

         int approxLength;
         for (approxLength = 0; context != null; context = context.getParent()) {
            if (context.inObject()) {
               String propName = context.getCurrentName();
               if (propName == null) {
                  propName = "";
               }

               approxLength += 2 + propName.length();
               next = new JsonPointer.PointerSegment(next, propName, -1);
            } else if (context.inArray() || includeRoot) {
               int ix = context.getCurrentIndex();
               approxLength += 6;
               next = new JsonPointer.PointerSegment(next, null, ix);
            }
         }

         if (next == null) {
            return EMPTY;
         } else {
            StringBuilder pathBuilder = new StringBuilder(approxLength);

            JsonPointer.PointerSegment last;
            for (last = null; next != null; next = next.next) {
               last = next;
               next.pathOffset = pathBuilder.length();
               pathBuilder.append('/');
               if (next.property != null) {
                  _appendEscaped(pathBuilder, next.property);
               } else {
                  pathBuilder.append(next.index);
               }
            }

            String fullPath = pathBuilder.toString();
            JsonPointer.PointerSegment currSegment = last;

            JsonPointer currPtr;
            for (currPtr = EMPTY; currSegment != null; currSegment = currSegment.prev) {
               if (currSegment.property != null) {
                  currPtr = new JsonPointer(fullPath, currSegment.pathOffset, currSegment.property, currPtr);
               } else {
                  int index = currSegment.index;
                  currPtr = new JsonPointer(fullPath, currSegment.pathOffset, String.valueOf(index), index, currPtr);
               }
            }

            return currPtr;
         }
      }
   }

   private static void _appendEscaped(StringBuilder sb, String segment) {
      int i = 0;

      for (int end = segment.length(); i < end; i++) {
         char c = segment.charAt(i);
         if (c == '/') {
            sb.append("~1");
         } else if (c == '~') {
            sb.append("~0");
         } else {
            sb.append(c);
         }
      }
   }

   public int length() {
      return this._asString.length() - this._asStringOffset;
   }

   public boolean matches() {
      return this._nextSegment == null;
   }

   public String getMatchingProperty() {
      return this._matchingPropertyName;
   }

   public int getMatchingIndex() {
      return this._matchingElementIndex;
   }

   public boolean mayMatchProperty() {
      return this._matchingPropertyName != null;
   }

   public boolean mayMatchElement() {
      return this._matchingElementIndex >= 0;
   }

   public JsonPointer last() {
      JsonPointer current = this;
      if (this == EMPTY) {
         return null;
      } else {
         while (true) {
            JsonPointer next = current._nextSegment;
            if (current._nextSegment == EMPTY) {
               return current;
            }

            current = next;
         }
      }
   }

   public JsonPointer append(JsonPointer tail) {
      if (this == EMPTY) {
         return tail;
      } else if (tail == EMPTY) {
         return this;
      } else {
         String currentJsonPointer = this._asString;
         if (currentJsonPointer.endsWith("/")) {
            currentJsonPointer = currentJsonPointer.substring(0, currentJsonPointer.length() - 1);
         }

         return compile(currentJsonPointer + tail._asString);
      }
   }

   public JsonPointer appendProperty(String property) {
      if (property != null && !property.isEmpty()) {
         if (property.charAt(0) != '/') {
            property = '/' + property;
         }

         String currentJsonPointer = this._asString;
         if (currentJsonPointer.endsWith("/")) {
            currentJsonPointer = currentJsonPointer.substring(0, currentJsonPointer.length() - 1);
         }

         return compile(currentJsonPointer + property);
      } else {
         return this;
      }
   }

   public JsonPointer appendIndex(int index) {
      if (index < 0) {
         throw new IllegalArgumentException("Negative index cannot be appended");
      } else {
         String currentJsonPointer = this._asString;
         if (currentJsonPointer.endsWith("/")) {
            currentJsonPointer = currentJsonPointer.substring(0, currentJsonPointer.length() - 1);
         }

         return compile(currentJsonPointer + '/' + index);
      }
   }

   public boolean matchesProperty(String name) {
      return this._nextSegment != null && this._matchingPropertyName.equals(name);
   }

   public JsonPointer matchProperty(String name) {
      return this._nextSegment != null && this._matchingPropertyName.equals(name) ? this._nextSegment : null;
   }

   public boolean matchesElement(int index) {
      return index == this._matchingElementIndex && index >= 0;
   }

   public JsonPointer matchElement(int index) {
      return index == this._matchingElementIndex && index >= 0 ? this._nextSegment : null;
   }

   public JsonPointer tail() {
      return this._nextSegment;
   }

   public JsonPointer head() {
      JsonPointer h = this._head;
      if (h == null) {
         if (this != EMPTY) {
            h = this._constructHead();
         }

         this._head = h;
      }

      return h;
   }

   @Override
   public String toString() {
      return this._asStringOffset <= 0 ? this._asString : this._asString.substring(this._asStringOffset);
   }

   @Override
   public int hashCode() {
      int h = this._hashCode;
      if (h == 0) {
         h = this.toString().hashCode();
         if (h == 0) {
            h = -1;
         }

         this._hashCode = h;
      }

      return h;
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (o == null) {
         return false;
      } else if (!(o instanceof JsonPointer)) {
         return false;
      } else {
         JsonPointer other = (JsonPointer)o;
         return this._compare(this._asString, this._asStringOffset, other._asString, other._asStringOffset);
      }
   }

   private final boolean _compare(String str1, int offset1, String str2, int offset2) {
      int end1 = str1.length();
      if (end1 - offset1 != str2.length() - offset2) {
         return false;
      } else {
         while (offset1 < end1) {
            if (str1.charAt(offset1++) != str2.charAt(offset2++)) {
               return false;
            }
         }

         return true;
      }
   }

   private static final int _parseIndex(String str) {
      int len = str.length();
      if (len != 0 && len <= 10) {
         char c = str.charAt(0);
         if (c <= '0') {
            return len == 1 && c == 48 ? 0 : -1;
         } else if (c > '9') {
            return -1;
         } else {
            for (int i = 1; i < len; i++) {
               c = str.charAt(i);
               if (c > '9' || c < '0') {
                  return -1;
               }
            }

            if (len == 10) {
               long l = NumberInput.parseLong(str);
               if (l > 2147483647L) {
                  return -1;
               }
            }

            return NumberInput.parseInt(str);
         }
      } else {
         return -1;
      }
   }

   protected static JsonPointer _parseTail(String fullPath) {
      JsonPointer.PointerParent parent = null;
      int i = 1;
      int end = fullPath.length();
      int startOffset = 0;

      while (i < end) {
         char c = fullPath.charAt(i);
         if (c == '/') {
            parent = new JsonPointer.PointerParent(parent, startOffset, fullPath.substring(startOffset + 1, i));
            startOffset = i++;
         } else if (c == '~' && ++i < end) {
            StringBuilder sb = new StringBuilder(32);
            i = _extractEscapedSegment(fullPath, startOffset + 1, i, sb);
            String segment = sb.toString();
            if (i < 0) {
               return _buildPath(fullPath, startOffset, segment, parent);
            }

            parent = new JsonPointer.PointerParent(parent, startOffset, segment);
            startOffset = i++;
         }
      }

      return _buildPath(fullPath, startOffset, fullPath.substring(startOffset + 1), parent);
   }

   private static JsonPointer _buildPath(String fullPath, int fullPathOffset, String segment, JsonPointer.PointerParent parent) {
      JsonPointer curr = new JsonPointer(fullPath, fullPathOffset, segment, EMPTY);

      while (parent != null) {
         curr = new JsonPointer(fullPath, parent.fullPathOffset, parent.segment, curr);
         parent = parent.parent;
      }

      return curr;
   }

   protected static int _extractEscapedSegment(String input, int firstCharOffset, int i, StringBuilder sb) {
      int end = input.length();
      int toCopy = i - 1 - firstCharOffset;
      if (toCopy > 0) {
         sb.append(input, firstCharOffset, i - 1);
      }

      _appendEscape(sb, input.charAt(i++));

      while (i < end) {
         char c = input.charAt(i);
         if (c == '/') {
            return i;
         }

         if (c == '~' && ++i < end) {
            _appendEscape(sb, input.charAt(i++));
         } else {
            sb.append(c);
         }
      }

      return -1;
   }

   private static void _appendEscape(StringBuilder sb, char c) {
      if (c == '0') {
         c = '~';
      } else if (c == '1') {
         c = '/';
      } else {
         sb.append('~');
      }

      sb.append(c);
   }

   protected JsonPointer _constructHead() {
      JsonPointer last = this.last();
      if (last == this) {
         return EMPTY;
      } else {
         int suffixLength = last.length();
         JsonPointer next = this._nextSegment;
         String fullString = this.toString();
         return new JsonPointer(
            fullString.substring(0, fullString.length() - suffixLength),
            0,
            this._matchingPropertyName,
            this._matchingElementIndex,
            next._constructHead(suffixLength, last)
         );
      }
   }

   protected JsonPointer _constructHead(int suffixLength, JsonPointer last) {
      if (this == last) {
         return EMPTY;
      } else {
         JsonPointer next = this._nextSegment;
         String str = this.toString();
         return new JsonPointer(
            str.substring(0, str.length() - suffixLength), 0, this._matchingPropertyName, this._matchingElementIndex, next._constructHead(suffixLength, last)
         );
      }
   }

   private Object writeReplace() {
      return new JsonPointer.Serialization(this.toString());
   }

   private static class PointerParent {
      public final JsonPointer.PointerParent parent;
      public final int fullPathOffset;
      public final String segment;

      PointerParent(JsonPointer.PointerParent pp, int fpo, String sgm) {
         this.parent = pp;
         this.fullPathOffset = fpo;
         this.segment = sgm;
      }
   }

   private static class PointerSegment {
      public final JsonPointer.PointerSegment next;
      public final String property;
      public final int index;
      public int pathOffset;
      public JsonPointer.PointerSegment prev;

      public PointerSegment(JsonPointer.PointerSegment next, String pn, int ix) {
         this.next = next;
         this.property = pn;
         this.index = ix;
         if (next != null) {
            next.prev = this;
         }
      }
   }

   static class Serialization implements Externalizable {
      private String _fullPath;

      public Serialization() {
      }

      Serialization(String fullPath) {
         this._fullPath = fullPath;
      }

      @Override
      public void writeExternal(ObjectOutput out) throws IOException {
         out.writeUTF(this._fullPath);
      }

      @Override
      public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
         this._fullPath = in.readUTF();
      }

      private Object readResolve() throws ObjectStreamException {
         return JsonPointer.compile(this._fullPath);
      }
   }
}
