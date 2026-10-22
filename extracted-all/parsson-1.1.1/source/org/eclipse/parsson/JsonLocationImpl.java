package org.eclipse.parsson;

import jakarta.json.stream.JsonLocation;

class JsonLocationImpl implements JsonLocation {
   static final JsonLocation UNKNOWN = new JsonLocationImpl(-1L, -1L, -1L);
   private final long columnNo;
   private final long lineNo;
   private final long offset;

   JsonLocationImpl(long lineNo, long columnNo, long streamOffset) {
      this.lineNo = lineNo;
      this.columnNo = columnNo;
      this.offset = streamOffset;
   }

   public long getLineNumber() {
      return this.lineNo;
   }

   public long getColumnNumber() {
      return this.columnNo;
   }

   public long getStreamOffset() {
      return this.offset;
   }

   @Override
   public String toString() {
      return "(line no=" + this.lineNo + ", column no=" + this.columnNo + ", offset=" + this.offset + ")";
   }
}
