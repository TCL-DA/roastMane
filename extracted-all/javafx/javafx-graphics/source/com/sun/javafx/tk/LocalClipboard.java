package com.sun.javafx.tk;

import java.security.AccessControlContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javafx.scene.image.Image;
import javafx.scene.input.DataFormat;
import javafx.scene.input.TransferMode;
import javafx.util.Pair;

final class LocalClipboard implements TKClipboard {
   private final Map<DataFormat, Object> values = new HashMap<>();

   public LocalClipboard() {
   }

   @Override
   public void setSecurityContext(AccessControlContext var1) {
   }

   @Override
   public Set<DataFormat> getContentTypes() {
      return Collections.unmodifiableSet(new HashSet<>(this.values.keySet()));
   }

   @Override
   public boolean putContent(Pair<DataFormat, Object>... var1) {
      for (Pair var5 : var1) {
         if (var5.getKey() == null) {
            throw new NullPointerException("Clipboard.putContent: null data format");
         }

         if (var5.getValue() == null) {
            throw new NullPointerException("Clipboard.putContent: null data");
         }
      }

      this.values.clear();

      for (Pair var9 : var1) {
         this.values.put((DataFormat)var9.getKey(), var9.getValue());
      }

      return true;
   }

   @Override
   public Object getContent(DataFormat var1) {
      return this.values.get(var1);
   }

   @Override
   public boolean hasContent(DataFormat var1) {
      return this.values.containsKey(var1);
   }

   @Override
   public Set<TransferMode> getTransferModes() {
      throw new IllegalStateException();
   }

   @Override
   public void setDragView(Image var1) {
      throw new IllegalStateException();
   }

   @Override
   public void setDragViewOffsetX(double var1) {
      throw new IllegalStateException();
   }

   @Override
   public void setDragViewOffsetY(double var1) {
      throw new IllegalStateException();
   }

   @Override
   public Image getDragView() {
      throw new IllegalStateException();
   }

   @Override
   public double getDragViewOffsetX() {
      throw new IllegalStateException();
   }

   @Override
   public double getDragViewOffsetY() {
      throw new IllegalStateException();
   }
}
