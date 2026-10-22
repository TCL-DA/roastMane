package com.sun.javafx.scene.control.behavior;

import javafx.scene.control.PasswordField;
import javafx.scene.text.HitInfo;

public class PasswordFieldBehavior extends TextFieldBehavior {
   public PasswordFieldBehavior(PasswordField var1) {
      super(var1);
   }

   @Override
   protected void deletePreviousWord() {
   }

   @Override
   protected void deleteNextWord() {
   }

   @Override
   protected void selectPreviousWord() {
   }

   @Override
   public void selectNextWord() {
   }

   @Override
   protected void previousWord() {
   }

   @Override
   protected void nextWord() {
   }

   @Override
   protected void selectWord() {
      this.selectAll();
   }

   @Override
   protected void mouseDoubleClick(HitInfo var1) {
      this.getNode().selectAll();
   }
}
