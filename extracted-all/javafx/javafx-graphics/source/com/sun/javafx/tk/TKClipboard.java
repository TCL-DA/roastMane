package com.sun.javafx.tk;

import java.security.AccessControlContext;
import java.util.Set;
import javafx.scene.image.Image;
import javafx.scene.input.DataFormat;
import javafx.scene.input.TransferMode;
import javafx.util.Pair;

public interface TKClipboard {
   void setSecurityContext(AccessControlContext var1);

   Set<DataFormat> getContentTypes();

   boolean putContent(Pair<DataFormat, Object>... var1);

   Object getContent(DataFormat var1);

   boolean hasContent(DataFormat var1);

   Set<TransferMode> getTransferModes();

   void setDragView(Image var1);

   void setDragViewOffsetX(double var1);

   void setDragViewOffsetY(double var1);

   Image getDragView();

   double getDragViewOffsetX();

   double getDragViewOffsetY();
}
