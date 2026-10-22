package org.controlsfx.control.spreadsheet;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.Background;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

public interface CellGraphicFactory<T extends Node> {
   Node getNode(SpreadsheetCell var1);

   void load(T var1, SpreadsheetCell var2);

   void loadStyle(T var1, SpreadsheetCell var2, Font var3, Paint var4, Pos var5, Background var6);

   void setUnusedNode(T var1);

   Class getType();
}
