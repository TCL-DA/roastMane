package impl.org.controlsfx.tools;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import javafx.event.EventHandler;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.ListView;
import javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.controlsfx.control.PrefixSelectionComboBox;

public class PrefixSelectionCustomizer {
   public static final int DEFAULT_TYPING_DELAY = 500;
   private static final String SELECTION_PREFIX_STRING = "selectionPrefixString";
   private static final Object SELECTION_PREFIX_TASK = "selectionPrefixTask";
   public static final BiFunction<ComboBox, String, Optional> DEFAULT_LOOKUP_COMBOBOX = (comboBox, selection) -> comboBox != null
            && selection != null
            && !selection.isEmpty()
         ? comboBox.getItems().stream().filter(Objects::nonNull).filter(item -> {
            String s = comboBox.getConverter() == null ? item.toString() : comboBox.getConverter().toString(item);
            return s != null && !s.isEmpty() ? s.toUpperCase(Locale.ROOT).startsWith(selection.toUpperCase(Locale.ROOT)) : false;
         }).findFirst()
         : Optional.empty();
   public static final BiFunction<ChoiceBox, String, Optional> DEFAULT_LOOKUP_CHOICEBOX = (choiceBox, selection) -> choiceBox != null
            && selection != null
            && !selection.isEmpty()
         ? choiceBox.getItems().stream().filter(Objects::nonNull).filter(item -> {
            String s = choiceBox.getConverter() == null ? item.toString() : choiceBox.getConverter().toString(item);
            return s != null && !s.isEmpty() ? s.toUpperCase(Locale.ROOT).startsWith(selection.toUpperCase(Locale.ROOT)) : false;
         }).findFirst()
         : Optional.empty();
   private static EventHandler<KeyEvent> handler = new EventHandler<KeyEvent>() {
      private ScheduledExecutorService executorService = null;
      private PrefixSelectionComboBox prefixSelectionComboBox;
      private int typingDelay;
      private Object result;

      public void handle(KeyEvent event) {
         this.keyPressed(event);
      }

      private <T> void keyPressed(KeyEvent event) {
         KeyCode code = event.getCode();
         if (code.isLetterKey() || code.isDigitKey() || code == KeyCode.SPACE || code == KeyCode.BACK_SPACE) {
            if (event.getSource() instanceof PrefixSelectionComboBox
               && code == KeyCode.BACK_SPACE
               && !((PrefixSelectionComboBox)event.getSource()).isBackSpaceAllowed()) {
               return;
            }

            String letter = event.getText();
            if (event.getSource() instanceof ComboBox) {
               ComboBox<T> comboBox = (ComboBox<T>)event.getSource();
               T item = this.getEntryWithKey(letter, comboBox);
               if (item != null) {
                  comboBox.setValue(item);
                  ComboBoxListViewSkin<?> skin = (ComboBoxListViewSkin<?>)comboBox.getSkin();
                  ((ListView)skin.getPopupContent()).scrollTo(item);
               }
            } else if (event.getSource() instanceof ChoiceBox) {
               ChoiceBox<T> choiceBox = (ChoiceBox<T>)event.getSource();
               T item = this.getEntryWithKey(letter, choiceBox);
               if (item != null) {
                  choiceBox.setValue(item);
               }
            }
         }
      }

      private <T> T getEntryWithKey(String letter, Control control) {
         this.result = null;
         this.typingDelay = 500;
         this.prefixSelectionComboBox = control instanceof PrefixSelectionComboBox ? (PrefixSelectionComboBox)control : null;
         String selectionPrefixString = this.processInput((String)control.getProperties().get("selectionPrefixString"), letter);
         control.getProperties().put("selectionPrefixString", selectionPrefixString);
         if (this.prefixSelectionComboBox != null) {
            this.typingDelay = this.prefixSelectionComboBox.getTypingDelay();
            BiFunction<ComboBox, String, Optional> lookup = this.prefixSelectionComboBox.getLookup();
            if (lookup != null) {
               lookup.apply(this.prefixSelectionComboBox, selectionPrefixString).ifPresent(t -> this.result = t);
            }
         } else if (control instanceof ComboBox) {
            PrefixSelectionCustomizer.DEFAULT_LOOKUP_COMBOBOX.apply((ComboBox)control, selectionPrefixString).ifPresent(t -> this.result = t);
         } else if (control instanceof ChoiceBox) {
            PrefixSelectionCustomizer.DEFAULT_LOOKUP_CHOICEBOX.apply((ChoiceBox)control, selectionPrefixString).ifPresent(t -> this.result = t);
         }

         ScheduledFuture<?> task = (ScheduledFuture<?>)control.getProperties().get(PrefixSelectionCustomizer.SELECTION_PREFIX_TASK);
         if (task != null) {
            task.cancel(false);
         }

         task = this.getExecutorService()
            .schedule(() -> control.getProperties().put("selectionPrefixString", ""), (long)this.typingDelay, TimeUnit.MILLISECONDS);
         control.getProperties().put(PrefixSelectionCustomizer.SELECTION_PREFIX_TASK, task);
         return (T)this.result;
      }

      private ScheduledExecutorService getExecutorService() {
         if (this.executorService == null) {
            this.executorService = Executors.newScheduledThreadPool(1, runnabble -> {
               Thread result = new Thread(runnabble);
               result.setDaemon(true);
               return result;
            });
         }

         return this.executorService;
      }

      private String processInput(String initialText, String letter) {
         if (initialText == null) {
            initialText = "";
         }

         StringBuilder sb = new StringBuilder();

         for (char c : initialText.concat(letter).toCharArray()) {
            if (c == '\b') {
               if (sb.length() > 0) {
                  sb.delete(0, sb.length());
                  break;
               }
            } else {
               sb.append(c);
            }
         }

         return sb.toString();
      }
   };

   public static void customize(ComboBox<?> comboBox) {
      if (!comboBox.isEditable()) {
         comboBox.addEventHandler(KeyEvent.KEY_PRESSED, handler);
      }

      comboBox.editableProperty().addListener((o, oV, nV) -> {
         if (!nV) {
            comboBox.addEventHandler(KeyEvent.KEY_PRESSED, handler);
         } else {
            comboBox.removeEventHandler(KeyEvent.KEY_PRESSED, handler);
         }
      });
   }

   public static void customize(ChoiceBox<?> choiceBox) {
      choiceBox.addEventHandler(KeyEvent.KEY_PRESSED, handler);
   }
}
