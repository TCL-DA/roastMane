package impl.org.controlsfx.autocompletion;

import java.util.Collection;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.AutoCompletionBinding;

public class AutoCompletionTextFieldBinding<T> extends AutoCompletionBinding<T> {
   private StringConverter<T> converter;
   private final ChangeListener<String> textChangeListener = new ChangeListener<String>() {
      public void changed(ObservableValue<? extends String> obs, String oldText, String newText) {
         if (AutoCompletionTextFieldBinding.this.getCompletionTarget().isFocused()) {
            AutoCompletionTextFieldBinding.this.setUserInput(newText);
         }
      }
   };
   private final ChangeListener<Boolean> focusChangedListener = new ChangeListener<Boolean>() {
      public void changed(ObservableValue<? extends Boolean> obs, Boolean oldFocused, Boolean newFocused) {
         if (!newFocused) {
            AutoCompletionTextFieldBinding.this.hidePopup();
         }
      }
   };

   private static <T> StringConverter<T> defaultStringConverter() {
      return new StringConverter<T>() {
         public String toString(T t) {
            return t == null ? null : t.toString();
         }

         public T fromString(String string) {
            return (T)string;
         }
      };
   }

   public AutoCompletionTextFieldBinding(TextField textField, Callback<AutoCompletionBinding.ISuggestionRequest, Collection<T>> suggestionProvider) {
      this(textField, suggestionProvider, defaultStringConverter());
   }

   public AutoCompletionTextFieldBinding(
      TextField textField, Callback<AutoCompletionBinding.ISuggestionRequest, Collection<T>> suggestionProvider, StringConverter<T> converter
   ) {
      super(textField, suggestionProvider, converter);
      this.converter = converter;
      this.getCompletionTarget().textProperty().addListener(this.textChangeListener);
      this.getCompletionTarget().focusedProperty().addListener(this.focusChangedListener);
   }

   public TextField getCompletionTarget() {
      return (TextField)super.getCompletionTarget();
   }

   @Override
   public void dispose() {
      this.getCompletionTarget().textProperty().removeListener(this.textChangeListener);
      this.getCompletionTarget().focusedProperty().removeListener(this.focusChangedListener);
   }

   @Override
   protected void completeUserInput(T completion) {
      String newText = this.converter.toString(completion);
      this.getCompletionTarget().setText(newText);
      this.getCompletionTarget().positionCaret(newText.length());
   }
}
