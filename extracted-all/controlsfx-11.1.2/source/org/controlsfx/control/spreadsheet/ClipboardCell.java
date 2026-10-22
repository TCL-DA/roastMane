package org.controlsfx.control.spreadsheet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class ClipboardCell implements Serializable {
   private final int row;
   private final int column;
   private Object value;
   private final String htmlVersion;

   public ClipboardCell(int row, int column, SpreadsheetCell spc) {
      this.row = row;
      this.column = column;
      Object value = spc.getItem();
      if (spc.isCellGraphic()) {
         this.htmlVersion = value == null ? null : value.toString();
         this.value = spc.getCellType().toString(spc.getItem());
      } else {
         try {
            new ObjectOutputStream(new ByteArrayOutputStream()).writeObject(value);
            this.value = value;
         } catch (IOException var6) {
            this.value = value == null ? null : value.toString();
         }

         this.htmlVersion = null;
      }
   }

   public int getRow() {
      return this.row;
   }

   public int getColumn() {
      return this.column;
   }

   public Object getValue() {
      return this.value;
   }

   public String getHtmlVersion() {
      return this.htmlVersion;
   }
}
