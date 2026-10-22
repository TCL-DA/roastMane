package org.controlsfx.control.textfield;

import com.sun.javafx.event.EventHandlerManager;
import impl.org.controlsfx.skin.AutoCompletePopup;
import impl.org.controlsfx.skin.AutoCompletePopupSkin;
import java.util.Collection;
import java.util.UUID;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.Skin;
import javafx.util.Callback;
import javafx.util.StringConverter;

public abstract class AutoCompletionBinding<T> implements EventTarget {
   private final Node completionTarget;
   private final AutoCompletePopup<T> autoCompletionPopup;
   private final Object suggestionsTaskLock = new Object();
   private AutoCompletionBinding<T>.FetchSuggestionsTask suggestionsTask = null;
   private Callback<AutoCompletionBinding.ISuggestionRequest, Collection<T>> suggestionProvider = null;
   private boolean ignoreInputChanges = false;
   private long delay = 250L;
   private ObjectProperty<EventHandler<AutoCompletionBinding.AutoCompletionEvent<T>>> onAutoCompleted;
   final EventHandlerManager eventHandlerManager = new EventHandlerManager(this);

   protected AutoCompletionBinding(
      Node completionTarget, Callback<AutoCompletionBinding.ISuggestionRequest, Collection<T>> suggestionProvider, StringConverter<T> converter
   ) {
      this.completionTarget = completionTarget;
      this.suggestionProvider = suggestionProvider;
      this.autoCompletionPopup = new AutoCompletePopup<>();
      this.autoCompletionPopup.setConverter(converter);
      this.autoCompletionPopup.setOnSuggestion(sce -> {
         try {
            this.setIgnoreInputChanges(true);
            this.completeUserInput((T)sce.getSuggestion());
            this.fireAutoCompletion((T)sce.getSuggestion());
            this.hidePopup();
         } finally {
            this.setIgnoreInputChanges(false);
         }
      });
   }

   public void setHideOnEscape(boolean value) {
      this.autoCompletionPopup.setHideOnEscape(value);
   }

   public final void setUserInput(String userText) {
      if (!this.isIgnoreInputChanges()) {
         this.onUserInputChanged(userText);
      }
   }

   public final void setDelay(long delay) {
      this.delay = delay;
   }

   public Node getCompletionTarget() {
      return this.completionTarget;
   }

   public abstract void dispose();

   public final void setVisibleRowCount(int value) {
      this.autoCompletionPopup.setVisibleRowCount(value);
   }

   public final int getVisibleRowCount() {
      return this.autoCompletionPopup.getVisibleRowCount();
   }

   public final IntegerProperty visibleRowCountProperty() {
      return this.autoCompletionPopup.visibleRowCountProperty();
   }

   public final void setPrefWidth(double value) {
      this.autoCompletionPopup.setPrefWidth(value);
   }

   public final double getPrefWidth() {
      return this.autoCompletionPopup.getPrefWidth();
   }

   public final DoubleProperty prefWidthProperty() {
      return this.autoCompletionPopup.prefWidthProperty();
   }

   public final void setMinWidth(double value) {
      this.autoCompletionPopup.setMinWidth(value);
   }

   public final double getMinWidth() {
      return this.autoCompletionPopup.getMinWidth();
   }

   public final DoubleProperty minWidthProperty() {
      return this.autoCompletionPopup.minWidthProperty();
   }

   public final void setMaxWidth(double value) {
      this.autoCompletionPopup.setMaxWidth(value);
   }

   public final double getMaxWidth() {
      return this.autoCompletionPopup.getMaxWidth();
   }

   public final DoubleProperty maxWidthProperty() {
      return this.autoCompletionPopup.maxWidthProperty();
   }

   public AutoCompletePopup<T> getAutoCompletionPopup() {
      return this.autoCompletionPopup;
   }

   protected abstract void completeUserInput(T var1);

   protected void showPopup() {
      this.autoCompletionPopup.show(this.completionTarget);
      this.selectFirstSuggestion(this.autoCompletionPopup);
   }

   protected void hidePopup() {
      this.autoCompletionPopup.hide();
   }

   protected void fireAutoCompletion(T completion) {
      Event.fireEvent(this, new AutoCompletionBinding.AutoCompletionEvent<>(completion));
   }

   private void selectFirstSuggestion(AutoCompletePopup<?> autoCompletionPopup) {
      Skin<?> skin = autoCompletionPopup.getSkin();
      if (skin instanceof AutoCompletePopupSkin) {
         AutoCompletePopupSkin<?> au = (AutoCompletePopupSkin<?>)skin;
         ListView<?> li = (ListView<?>)au.getNode();
         if (li.getItems() != null && !li.getItems().isEmpty()) {
            li.getSelectionModel().select(0);
         }
      }
   }

   private final void onUserInputChanged(String userText) {
      synchronized (this.suggestionsTaskLock) {
         if (this.suggestionsTask != null && this.suggestionsTask.isRunning()) {
            this.suggestionsTask.cancel();
         }

         this.suggestionsTask = new AutoCompletionBinding.FetchSuggestionsTask(userText, this.delay);
         new Thread(this.suggestionsTask).start();
      }
   }

   private boolean isIgnoreInputChanges() {
      return this.ignoreInputChanges;
   }

   private void setIgnoreInputChanges(boolean state) {
      this.ignoreInputChanges = state;
   }

   public final void setOnAutoCompleted(EventHandler<AutoCompletionBinding.AutoCompletionEvent<T>> value) {
      this.onAutoCompletedProperty().set(value);
   }

   public final EventHandler<AutoCompletionBinding.AutoCompletionEvent<T>> getOnAutoCompleted() {
      return this.onAutoCompleted == null ? null : (EventHandler)this.onAutoCompleted.get();
   }

   public final ObjectProperty<EventHandler<AutoCompletionBinding.AutoCompletionEvent<T>>> onAutoCompletedProperty() {
      if (this.onAutoCompleted == null) {
         this.onAutoCompleted = new ObjectPropertyBase<EventHandler<AutoCompletionBinding.AutoCompletionEvent<T>>>() {
            protected void invalidated() {
               AutoCompletionBinding.this.eventHandlerManager
                  .setEventHandler(AutoCompletionBinding.AutoCompletionEvent.AUTO_COMPLETED, (EventHandler)this.get());
            }

            public Object getBean() {
               return AutoCompletionBinding.this;
            }

            public String getName() {
               return "onAutoCompleted";
            }
         };
      }

      return this.onAutoCompleted;
   }

   public <E extends Event> void addEventHandler(EventType<E> eventType, EventHandler<E> eventHandler) {
      this.eventHandlerManager.addEventHandler(eventType, eventHandler);
   }

   public <E extends Event> void removeEventHandler(EventType<E> eventType, EventHandler<E> eventHandler) {
      this.eventHandlerManager.removeEventHandler(eventType, eventHandler);
   }

   public EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
      return tail.prepend(this.eventHandlerManager);
   }

   public static class AutoCompletionEvent<TE> extends Event {
      public static final EventType<AutoCompletionBinding.AutoCompletionEvent<?>> AUTO_COMPLETED = new EventType(
         "AUTO_COMPLETED" + UUID.randomUUID().toString()
      );
      private final TE completion;

      public AutoCompletionEvent(TE completion) {
         super(AUTO_COMPLETED);
         this.completion = completion;
      }

      public TE getCompletion() {
         return this.completion;
      }
   }

   private class FetchSuggestionsTask extends Task<Void> implements AutoCompletionBinding.ISuggestionRequest {
      private final String userText;
      private final long delay;

      public FetchSuggestionsTask(String userText, long delay) {
         this.userText = userText;
         this.delay = delay;
      }

      protected Void call() throws Exception {
         Callback<AutoCompletionBinding.ISuggestionRequest, Collection<T>> provider = AutoCompletionBinding.this.suggestionProvider;
         if (provider != null) {
            long startTime = System.currentTimeMillis();
            long sleepTime = startTime + this.delay - System.currentTimeMillis();
            if (sleepTime > 0L && !this.isCancelled()) {
               Thread.sleep(sleepTime);
            }

            if (!this.isCancelled()) {
               Collection<T> fetchedSuggestions = (Collection<T>)provider.call(this);
               Platform.runLater(
                  () -> {
                     boolean validNode = AutoCompletionBinding.this.completionTarget.getScene() != null
                        && AutoCompletionBinding.this.completionTarget.getScene().getWindow() != null;
                     if (fetchedSuggestions != null && !fetchedSuggestions.isEmpty() && validNode) {
                        AutoCompletionBinding.this.autoCompletionPopup.getSuggestions().setAll(fetchedSuggestions);
                        AutoCompletionBinding.this.showPopup();
                     } else {
                        AutoCompletionBinding.this.hidePopup();
                     }
                  }
               );
            }
         } else {
            AutoCompletionBinding.this.hidePopup();
         }

         return null;
      }

      @Override
      public String getUserText() {
         return this.userText;
      }
   }

   public interface ISuggestionRequest {
      boolean isCancelled();

      String getUserText();
   }
}
