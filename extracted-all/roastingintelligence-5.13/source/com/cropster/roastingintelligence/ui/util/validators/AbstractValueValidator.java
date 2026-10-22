package com.cropster.roastingintelligence.ui.util.validators;

import com.cropster.roastingintelligence.ui.util.Validators;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;

public abstract class AbstractValueValidator<T> extends AbstractValidator {
   protected ChangeListener<T> valueChangeListener;

   protected AbstractValueValidator(TextField textField, Node container) {
      super(textField, container);
   }

   protected void initListeners(
      ChangeListener<Boolean> focusChangeListener, ChangeListener<Validators.Status> statusChangeListener, ChangeListener<T> valueChangeListener
   ) {
      this.initListeners(focusChangeListener, statusChangeListener);
      this.valueChangeListener = valueChangeListener;
   }

   @Override
   protected void restoreValidStatus(TextInputControl textInputControl, Node container) {
      super.restoreValidStatus(textInputControl, container);
      TextField textField = (TextField)textInputControl;
      Pos alignment = (Pos)textField.getProperties().get(Pos.class);
      if (alignment != null) {
         textField.setAlignment(alignment);
      }
   }

   @Override
   protected void updateChangedStatus(TextInputControl textInputControl, Node container, Validators.Status status) {
      super.updateChangedStatus(textInputControl, container, status);
      TextField textField = (TextField)textInputControl;
      if (status == Validators.Status.ERROR || status == Validators.Status.WARNING) {
         textField.getProperties().put(Pos.class, textField.getAlignment());
         textField.setAlignment(Pos.CENTER_LEFT);
      }
   }

   protected abstract ChangeListener<T> createValueChangeListener();
}
