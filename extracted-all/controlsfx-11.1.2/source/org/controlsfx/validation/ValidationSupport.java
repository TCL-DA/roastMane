package org.controlsfx.validation;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;
import javafx.collections.MapChangeListener.Change;
import javafx.scene.control.Control;
import javafx.util.Callback;
import org.controlsfx.tools.ValueExtractor;
import org.controlsfx.validation.decoration.GraphicValidationDecoration;
import org.controlsfx.validation.decoration.ValidationDecoration;

public class ValidationSupport {
   private static final String CTRL_REQUIRED_FLAG = "$org.controlsfx.validation.required$";
   private final ObservableSet<Control> controls = FXCollections.observableSet(ConcurrentHashMap.newKeySet());
   private final ObservableMap<Control, ValidationResult> validationResults = FXCollections.synchronizedObservableMap(
      FXCollections.observableMap(new WeakHashMap())
   );
   private final AtomicBoolean dataChanged = new AtomicBoolean(false);
   private BooleanProperty errorDecorationEnabledProperty = new SimpleBooleanProperty(true) {
      protected void invalidated() {
         ValidationSupport.this.redecorate();
      }
   };
   private ReadOnlyObjectWrapper<ValidationResult> validationResultProperty = new ReadOnlyObjectWrapper();
   private BooleanProperty invalidProperty = new SimpleBooleanProperty();
   private ObjectProperty<ValidationDecoration> validationDecoratorProperty = new SimpleObjectProperty<ValidationDecoration>(
      this, "validationDecorator", new GraphicValidationDecoration()
   ) {
      protected void invalidated() {
         ValidationSupport.this.redecorate();
      }
   };

   public static void setRequired(Control c, boolean required) {
      c.getProperties().put("$org.controlsfx.validation.required$", required);
   }

   public static boolean isRequired(Control c) {
      Object value = c.getProperties().get("$org.controlsfx.validation.required$");
      return value instanceof Boolean ? (Boolean)value : false;
   }

   public ValidationSupport() {
      this.validationResultProperty().addListener((o, oldValue, validationResult) -> {
         this.invalidProperty.set(!validationResult.getErrors().isEmpty());
         this.redecorate();
      });
      this.validationResults.addListener(change -> this.validationResultProperty.set(ValidationResult.fromResults(this.validationResults.values())));
   }

   public void initInitialDecoration() {
      this.dataChanged.set(true);
      this.redecorate();
   }

   public void redecorate() {
      Optional<ValidationDecoration> odecorator = Optional.ofNullable(this.getValidationDecorator());

      for (Control target : this.getRegisteredControls()) {
         odecorator.ifPresent(decorator -> {
            decorator.removeDecorations(target);
            decorator.applyRequiredDecoration(target);
            if (this.dataChanged.get() && this.isErrorDecorationEnabled()) {
               this.getHighestMessage(target).ifPresent(msg -> decorator.applyValidationDecoration(msg));
            }
         });
      }
   }

   public void revalidate() {
      for (Control target : this.getRegisteredControls()) {
         target.fireEvent(new ValidateEvent());
      }
   }

   public void revalidate(Control c) {
      c.fireEvent(new ValidateEvent());
   }

   public BooleanProperty errorDecorationEnabledProperty() {
      return this.errorDecorationEnabledProperty;
   }

   public void setErrorDecorationEnabled(boolean enabled) {
      this.errorDecorationEnabledProperty.set(enabled);
   }

   private boolean isErrorDecorationEnabled() {
      return this.errorDecorationEnabledProperty.get();
   }

   public ValidationResult getValidationResult() {
      return (ValidationResult)this.validationResultProperty.get();
   }

   public ReadOnlyObjectProperty<ValidationResult> validationResultProperty() {
      return this.validationResultProperty.getReadOnlyProperty();
   }

   public Boolean isInvalid() {
      return this.invalidProperty.get();
   }

   public ReadOnlyBooleanProperty invalidProperty() {
      return this.invalidProperty;
   }

   public ObjectProperty<ValidationDecoration> validationDecoratorProperty() {
      return this.validationDecoratorProperty;
   }

   public ValidationDecoration getValidationDecorator() {
      return (ValidationDecoration)this.validationDecoratorProperty.get();
   }

   public void setValidationDecorator(ValidationDecoration decorator) {
      this.validationDecoratorProperty.set(decorator);
   }

   public <T> boolean registerValidator(Control c, boolean required, Validator<T> validator) {
      Optional.ofNullable(c).ifPresent(ctrl -> ctrl.getProperties().addListener(new MapChangeListener<Object, Object>() {
            public void onChanged(Change<? extends Object, ? extends Object> change) {
               if ("$org.controlsfx.validation.required$".equals(change.getKey())) {
                  ValidationSupport.this.redecorate();
               }
            }
         }));
      setRequired(c, required);
      return ValueExtractor.getObservableValueExtractor(c).map(e -> {
         ObservableValue<T> observable = (ObservableValue<T>)e.call(c);
         Consumer<T> updateResults = value -> Platform.runLater(() -> this.validationResults.put(c, validator.apply(c, value)));
         this.controls.add(c);
         observable.addListener((o, oldValue, newValue) -> {
            this.dataChanged.set(true);
            updateResults.accept((T)newValue);
         });
         c.addEventHandler(ValidateEvent.EVENT_TYPE, event -> updateResults.accept((T)observable.getValue()));
         updateResults.accept((T)observable.getValue());
         return (Callback)e;
      }).isPresent();
   }

   public <T> boolean registerValidator(Control c, Validator<T> validator) {
      return this.registerValidator(c, true, validator);
   }

   public Set<Control> getRegisteredControls() {
      return Collections.unmodifiableSet(this.controls);
   }

   public Optional<ValidationMessage> getHighestMessage(Control target) {
      return Optional.ofNullable((ValidationResult)this.validationResults.get(target))
         .flatMap(result -> ((ValidationResult)result).getMessages().stream().max(ValidationMessage.COMPARATOR));
   }
}
