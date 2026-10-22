package org.controlsfx.control.textfield;

import impl.org.controlsfx.autocompletion.AutoCompletionTextFieldBinding;
import impl.org.controlsfx.autocompletion.SuggestionProvider;
import java.util.Arrays;
import java.util.Collection;
import javafx.animation.FadeTransition;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.util.StringConverter;

public class TextFields {
   private static final Duration FADE_DURATION = Duration.millis(350.0);

   private TextFields() {
   }

   public static TextField createClearableTextField() {
      CustomTextField inputField = new CustomTextField();
      setupClearButtonField(inputField, inputField.rightProperty());
      return inputField;
   }

   public static PasswordField createClearablePasswordField() {
      CustomPasswordField inputField = new CustomPasswordField();
      setupClearButtonField(inputField, inputField.rightProperty());
      return inputField;
   }

   private static void setupClearButtonField(final TextField inputField, ObjectProperty<Node> rightProperty) {
      inputField.getStyleClass().add("clearable-field");
      Region clearButton = new Region();
      clearButton.getStyleClass().addAll(new String[]{"graphic"});
      StackPane clearButtonPane = new StackPane(new Node[]{clearButton});
      clearButtonPane.getStyleClass().addAll(new String[]{"clear-button"});
      clearButtonPane.setOpacity(0.0);
      clearButtonPane.setCursor(Cursor.DEFAULT);
      clearButtonPane.setOnMouseReleased(e -> inputField.clear());
      clearButtonPane.managedProperty().bind(inputField.editableProperty());
      clearButtonPane.visibleProperty().bind(inputField.editableProperty());
      rightProperty.set(clearButtonPane);
      final FadeTransition fader = new FadeTransition(FADE_DURATION, clearButtonPane);
      fader.setCycleCount(1);
      inputField.textProperty().addListener(new InvalidationListener() {
         private boolean isButtonVisible = false;

         public void invalidated(Observable arg0) {
            String text = inputField.getText();
            boolean isTextEmpty = text == null || text.isEmpty();
            if (isTextEmpty == this.isButtonVisible) {
               this.isButtonVisible = !isTextEmpty;
               this.fadeTo(this.isButtonVisible);
            }
         }

         private void fadeTo(boolean visible) {
            fader.stop();
            fader.setFromValue(visible ? 0.0 : 1.0);
            fader.setToValue(visible ? 1.0 : 0.0);
            fader.play();
         }
      });
   }

   public static <T> AutoCompletionBinding<T> bindAutoCompletion(
      TextField textField, Callback<AutoCompletionBinding.ISuggestionRequest, Collection<T>> suggestionProvider, StringConverter<T> converter
   ) {
      return new AutoCompletionTextFieldBinding<>(textField, suggestionProvider, converter);
   }

   public static <T> AutoCompletionBinding<T> bindAutoCompletion(
      TextField textField, Callback<AutoCompletionBinding.ISuggestionRequest, Collection<T>> suggestionProvider
   ) {
      return new AutoCompletionTextFieldBinding<>(textField, suggestionProvider);
   }

   public static <T> AutoCompletionBinding<T> bindAutoCompletion(TextField textField, T... possibleSuggestions) {
      return bindAutoCompletion(textField, Arrays.asList(possibleSuggestions));
   }

   public static <T> AutoCompletionBinding<T> bindAutoCompletion(TextField textField, Collection<T> possibleSuggestions) {
      return new AutoCompletionTextFieldBinding<>(textField, SuggestionProvider.create(possibleSuggestions));
   }
}
