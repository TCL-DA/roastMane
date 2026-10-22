package com.cropster.roastingintelligence.ui.util.validators;

import com.cropster.roastingintelligence.ui.WindowManager;
import com.cropster.roastingintelligence.ui.components.PopOverBubble;
import com.cropster.roastingintelligence.ui.util.Validators;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.control.PopupControl;
import javafx.scene.control.TextInputControl;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Window;
import org.apache.commons.lang3.StringUtils;

public abstract class AbstractValidator implements Validators.Validator {
   protected static final Supplier<String> DEFAULT_MESSAGE_PROVIDER = () -> org.cropster.roastingintelligence.util.Messages.m_("Invalid value");
   protected final Map<Validators.Status, Supplier<String>> messageProviders = new EnumMap<>(Validators.Status.class);
   protected final ObjectProperty<Validators.Status> highlight = new SimpleObjectProperty(Validators.Status.NONE);
   protected final TextInputControl textInputControl;
   protected Observable[] dependencies = new Observable[0];
   protected Function<TextInputControl, Validators.Status> statusProvider;
   protected ChangeListener<Boolean> focusChangeListener;
   protected ChangeListener<Validators.Status> statusChangeListener;

   protected AbstractValidator(TextInputControl textInputControl, Node container) {
      this.textInputControl = textInputControl;
      this.highlight.addListener((s, o, n) -> {
         this.restoreValidStatus(textInputControl, container);
         this.updateChangedStatus(textInputControl, container, n);
      });
      this.initStatusProvider(tf -> Validators.Status.NONE);
      this.initMessages();
   }

   protected void restoreValidStatus(TextInputControl ignoredTextInputControl, Node container) {
      container.getStyleClass().removeAll(new String[]{"validation-error", "validation-warning"});
   }

   protected void updateChangedStatus(TextInputControl ignoredTextInputControl, Node container, Validators.Status status) {
      switch (status) {
         case ERROR:
            container.getStyleClass().add("validation-error");
            break;
         case WARNING:
            container.getStyleClass().add("validation-warning");
      }
   }

   protected final void initMessages() {
      this.setWarningMessageProvider(null);
      this.setErrorMessageProvider(null);
   }

   protected Supplier<String> getDefaultMessageProvider() {
      return DEFAULT_MESSAGE_PROVIDER;
   }

   protected Function<TextInputControl, Validators.Status> getStatusProvider() {
      return this.statusProvider;
   }

   protected void initStatusProvider(Function<TextInputControl, Validators.Status> statusProvider) {
      this.statusProvider = statusProvider;
   }

   @Override
   public void setStatusProvider(Function<TextInputControl, Validators.Status> statusProvider, Observable... dependencies) {
      Objects.requireNonNull(statusProvider, "Null 'statusProvider' parameter");
      this.uninstall();
      this.statusProvider = statusProvider;
      this.dependencies = dependencies != null ? dependencies : new Observable[0];
      this.install();
   }

   protected void initListeners(ChangeListener<Boolean> focusChangeListener, ChangeListener<Validators.Status> statusChangeListener) {
      this.focusChangeListener = focusChangeListener;
      this.statusChangeListener = statusChangeListener;
   }

   protected abstract ChangeListener<Boolean> createFocusChangeListener();

   protected ChangeListener<Validators.Status> createStatusChangeListener() {
      return (s, o, n) -> {
         WindowManager.get().getWindow("VALIDATOR_POPOVER_WINDOW_ID").ifPresent(Window::hide);
         switch (n) {
            case ERROR:
               showPopOverMessage(this.textInputControl, this.messageProviders.get(n).get(), "text--error");
               break;
            case WARNING:
               showPopOverMessage(this.textInputControl, this.messageProviders.get(n).get(), "text--warning");
               break;
            default:
               WindowManager.get().getWindow("VALIDATOR_POPOVER_WINDOW_ID").ifPresent(w -> {
                  if (w instanceof PopupControl popup && Validators.class.getSimpleName().equals(popup.getId())) {
                     w.hide();
                  }
               });
         }
      };
   }

   protected Observable[] getDependencies() {
      return this.dependencies;
   }

   @Override
   public ObjectProperty<Validators.Status> highlightProperty() {
      return this.highlight;
   }

   @Override
   public void setErrorMessageProvider(Supplier<String> messageProvider) {
      this.messageProviders.put(Validators.Status.ERROR, messageProvider == null ? this.getDefaultMessageProvider() : messageProvider);
   }

   @Override
   public void setWarningMessageProvider(Supplier<String> messageProvider) {
      this.messageProviders.put(Validators.Status.WARNING, messageProvider == null ? this.getDefaultMessageProvider() : messageProvider);
   }

   @Override
   public void install() {
      List<Observable> dependenciesList = new ArrayList<>(1 + this.getDependencies().length);
      dependenciesList.add(this.textInputControl.textProperty());
      dependenciesList.addAll(List.of(this.getDependencies()));
      this.highlightProperty()
         .bind(Bindings.createObjectBinding(() -> this.getStatusProvider().apply(this.textInputControl), dependenciesList.toArray(Observable[]::new)));
      this.highlightProperty().addListener(this.statusChangeListener);
      this.textInputControl.focusedProperty().addListener(this.focusChangeListener);
   }

   @Override
   public void uninstall() {
      this.textInputControl.focusedProperty().removeListener(this.focusChangeListener);
      this.highlightProperty().removeListener(this.statusChangeListener);
      this.highlightProperty().unbind();
   }

   private static void showPopOverMessage(Node anchorNode, String message, String messageStyleClass) {
      if (anchorNode != null && anchorNode.getScene() != null && anchorNode.getScene().getWindow() != null) {
         if (!StringUtils.isBlank(message)) {
            Text messageText = new Text(message);
            messageText.setTextAlignment(TextAlignment.CENTER);
            if (StringUtils.isNotBlank(messageStyleClass)) {
               messageText.getStyleClass().add(messageStyleClass);
            }

            PopOverBubble bubble = PopOverBubble.builder()
               .withoutDetachable()
               .withId(Validators.class.getSimpleName())
               .withSmallTextPadding(true)
               .windowId("VALIDATOR_POPOVER_WINDOW_ID")
               .withContent(List.of(messageText))
               .build();
            bubble.setAutoHide(false);
            bubble.setArrowIndent(4.0);
            bubble.setArrowSize(8.0);
            bubble.setCornerRadius(4.0);
            bubble.showAt(anchorNode);
         }
      }
   }
}
