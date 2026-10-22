package org.controlsfx.dialog;

import impl.org.controlsfx.ImplUtils;
import impl.org.controlsfx.i18n.Localization;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.function.BooleanSupplier;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Window;
import org.controlsfx.tools.ValueExtractor;

public class Wizard {
   private Dialog<ButtonType> dialog;
   private final ObservableMap<String, Object> settings = FXCollections.observableHashMap();
   private final Stack<WizardPane> pageHistory = new Stack<>();
   private Optional<WizardPane> currentPage = Optional.empty();
   private final BooleanProperty invalidProperty = new SimpleBooleanProperty(false);
   private final BooleanProperty readSettingsProperty = new SimpleBooleanProperty(true);
   private final ButtonType BUTTON_PREVIOUS = new ButtonType(Localization.localize(Localization.asKey("wizard.previous.button")), ButtonData.BACK_PREVIOUS);
   private final EventHandler<ActionEvent> BUTTON_PREVIOUS_ACTION_HANDLER = actionEvent -> {
      actionEvent.consume();
      Optional<WizardPane> nextPage = Optional.ofNullable(this.pageHistory.isEmpty() ? null : this.pageHistory.pop());
      this.updatePage(this.dialog, nextPage, false);
   };
   private final ButtonType BUTTON_NEXT = new ButtonType(Localization.localize(Localization.asKey("wizard.next.button")), ButtonData.NEXT_FORWARD);
   private final EventHandler<ActionEvent> BUTTON_NEXT_ACTION_HANDLER = actionEvent -> {
      actionEvent.consume();
      this.currentPage.ifPresent(page -> this.pageHistory.push(page));
      Optional<WizardPane> nextPage = this.getFlow().advance(this.currentPage.orElse(null));
      this.updatePage(this.dialog, nextPage, true);
   };
   private final EventHandler<ActionEvent> BUTTON_FINISH_ACTION_HANDLER = actionEvent -> this.updatePage(this.dialog, Optional.empty(), true);
   private final EventHandler<ActionEvent> BUTTON_CANCEL_ACTION_HANDLER = actionEvent -> this.updatePage(this.dialog, Optional.empty(), false);
   private final StringProperty titleProperty = new SimpleStringProperty();
   private ObjectProperty<Wizard.Flow> flow = new SimpleObjectProperty<Wizard.Flow>(new Wizard.LinearFlow()) {
      protected void invalidated() {
         Wizard.this.updatePage(Wizard.this.dialog, Optional.empty(), false);
      }

      public void set(Wizard.Flow flow) {
         super.set(flow);
         Wizard.this.pageHistory.clear();
         if (flow != null) {
            Optional<WizardPane> nextPage = flow.advance(Wizard.this.currentPage.orElse(null));
            Wizard.this.updatePage(Wizard.this.dialog, nextPage, true);
         }
      }
   };
   private static final Object USER_DATA_KEY = new Object();
   private ObservableMap<Object, Object> properties;
   private int settingCounter;

   public Wizard() {
      this(null);
   }

   public Wizard(Object owner) {
      this(owner, "");
   }

   public Wizard(Object owner, String title) {
      this.invalidProperty.addListener((o, ov, nv) -> this.validateActionState());
      this.dialog = new Dialog();
      this.dialog.titleProperty().bind(this.titleProperty);
      this.setTitle(title);
      Window window = null;
      if (owner instanceof Window) {
         window = (Window)owner;
      } else if (owner instanceof Node) {
         window = ((Node)owner).getScene().getWindow();
      }

      this.dialog.initOwner(window);
   }

   public final Optional<ButtonType> showAndWait() {
      return this.dialog.showAndWait();
   }

   public final ObjectProperty<ButtonType> resultProperty() {
      return this.dialog.resultProperty();
   }

   public final ObservableMap<String, Object> getSettings() {
      return this.settings;
   }

   public final StringProperty titleProperty() {
      return this.titleProperty;
   }

   public final String getTitle() {
      return (String)this.titleProperty.get();
   }

   public final void setTitle(String title) {
      this.titleProperty.set(title);
   }

   public final ObjectProperty<Wizard.Flow> flowProperty() {
      return this.flow;
   }

   public final Wizard.Flow getFlow() {
      return (Wizard.Flow)this.flow.get();
   }

   public final void setFlow(Wizard.Flow flow) {
      this.flow.set(flow);
   }

   public final ObservableMap<Object, Object> getProperties() {
      if (this.properties == null) {
         this.properties = FXCollections.observableMap(new HashMap());
      }

      return this.properties;
   }

   public boolean hasProperties() {
      return this.properties != null && !this.properties.isEmpty();
   }

   public void setUserData(Object value) {
      this.getProperties().put(USER_DATA_KEY, value);
   }

   public Object getUserData() {
      return this.getProperties().get(USER_DATA_KEY);
   }

   public final void setInvalid(boolean invalid) {
      this.invalidProperty.set(invalid);
   }

   public final boolean isInvalid() {
      return this.invalidProperty.get();
   }

   public final BooleanProperty invalidProperty() {
      return this.invalidProperty;
   }

   public final void setReadSettings(boolean readSettings) {
      this.readSettingsProperty.set(readSettings);
   }

   public final boolean isReadSettings() {
      return this.readSettingsProperty.get();
   }

   public final BooleanProperty readSettingsProperty() {
      return this.readSettingsProperty;
   }

   private void updatePage(Dialog<ButtonType> dialog, Optional<WizardPane> nextPage, boolean advancing) {
      Wizard.Flow flow = this.getFlow();
      if (flow != null) {
         Optional<WizardPane> prevPage = this.currentPage;
         prevPage.ifPresent(page -> {
            if (advancing && this.isReadSettings()) {
               this.readSettings(page);
            }

            page.onExitingPage(this);
         });
         this.currentPage = nextPage;
         this.currentPage.ifPresent(currentPage -> {
            addButtonIfMissing(currentPage, this.BUTTON_PREVIOUS, this.BUTTON_PREVIOUS_ACTION_HANDLER);
            addButtonIfMissing(currentPage, this.BUTTON_NEXT, this.BUTTON_NEXT_ACTION_HANDLER);
            addButtonIfMissing(currentPage, ButtonType.FINISH, this.BUTTON_FINISH_ACTION_HANDLER);
            addButtonIfMissing(currentPage, ButtonType.CANCEL, this.BUTTON_CANCEL_ACTION_HANDLER);
            currentPage.onEnteringPage(this);
            if (currentPage.getParent() != null && currentPage.getParent() instanceof Pane) {
               Pane parentOfCurrentPage = (Pane)currentPage.getParent();
               parentOfCurrentPage.getChildren().remove(currentPage);
            }

            double previousX = dialog.getX();
            double previousY = dialog.getY();
            double previousWidth = dialog.getWidth();
            double previousHeight = dialog.getHeight();
            dialog.setDialogPane(currentPage);
            Window wizard = currentPage.getScene().getWindow();
            wizard.sizeToScene();
            if (!Double.isNaN(previousX) && !Double.isNaN(previousY)) {
               double newWidth = dialog.getWidth();
               double newHeight = dialog.getHeight();
               int newX = (int)(previousX + previousWidth / 2.0 - newWidth / 2.0);
               int newY = (int)(previousY + previousHeight / 2.0 - newHeight / 2.0);
               ObservableList<Screen> screens = Screen.getScreensForRectangle(previousX, previousY, 1.0, 1.0);
               Screen screen = screens.isEmpty() ? Screen.getPrimary() : (Screen)screens.get(0);
               Rectangle2D scrBounds = screen.getBounds();
               int minX = (int)Math.round(scrBounds.getMinX());
               int maxX = (int)Math.round(scrBounds.getMaxX());
               int minY = (int)Math.round(scrBounds.getMinY());
               int maxY = (int)Math.round(scrBounds.getMaxY());
               if ((double)newX + newWidth > (double)maxX) {
                  newX = maxX - (int)Math.round(newWidth);
               }

               if ((double)newY + newHeight > (double)maxY) {
                  newY = maxY - (int)Math.round(newHeight);
               }

               if (newX < minX) {
                  newX = minX;
               }

               if (newY < minY) {
                  newY = minY;
               }

               dialog.setX((double)newX);
               dialog.setY((double)newY);
            }
         });
         this.validateActionState();
      }
   }

   private static void addButtonIfMissing(WizardPane page, ButtonType buttonType, EventHandler<ActionEvent> actionHandler) {
      List<ButtonType> buttons = page.getButtonTypes();
      if (!buttons.contains(buttonType)) {
         buttons.add(buttonType);
         Button button = (Button)page.lookupButton(buttonType);
         button.addEventFilter(ActionEvent.ACTION, actionHandler);
      }
   }

   private void validateActionState() {
      List<ButtonType> currentPaneButtons = this.dialog.getDialogPane().getButtonTypes();
      if (this.getFlow().canAdvance(this.currentPage.orElse(null))) {
         currentPaneButtons.remove(ButtonType.FINISH);
      } else {
         currentPaneButtons.remove(this.BUTTON_NEXT);
      }

      this.validateButton(this.BUTTON_PREVIOUS, () -> this.pageHistory.isEmpty());
      this.validateButton(this.BUTTON_NEXT, () -> this.invalidProperty.get());
      this.validateButton(ButtonType.FINISH, () -> this.invalidProperty.get());
   }

   private void validateButton(ButtonType buttonType, BooleanSupplier condition) {
      Button btn = (Button)this.dialog.getDialogPane().lookupButton(buttonType);
      if (btn != null) {
         Node focusOwner = btn.getScene() != null ? btn.getScene().getFocusOwner() : null;
         btn.setDisable(condition.getAsBoolean());
         if (focusOwner != null) {
            focusOwner.requestFocus();
         }
      }
   }

   private void readSettings(WizardPane page) {
      this.settingCounter = 0;
      this.checkNode(page.getContent());
   }

   private boolean checkNode(Node n) {
      boolean success = this.readSetting(n);
      if (success) {
         return true;
      } else {
         List<Node> children = ImplUtils.getChildren(n, true);
         boolean childSuccess = false;

         for (Node child : children) {
            childSuccess |= this.checkNode(child);
         }

         return childSuccess;
      }
   }

   private boolean readSetting(Node n) {
      if (n == null) {
         return false;
      } else {
         Object setting = ValueExtractor.getValue(n);
         if (setting != null) {
            String settingName = n.getId();
            if (settingName == null || settingName.isEmpty()) {
               settingName = "page_.setting_" + this.settingCounter;
            }

            this.getSettings().put(settingName, setting);
            this.settingCounter++;
         }

         return setting != null;
      }
   }

   Dialog<ButtonType> getDialog() {
      return this.dialog;
   }

   public interface Flow {
      Optional<WizardPane> advance(WizardPane var1);

      boolean canAdvance(WizardPane var1);
   }

   public static class LinearFlow implements Wizard.Flow {
      private final List<WizardPane> pages;

      public LinearFlow(Collection<WizardPane> pages) {
         this.pages = new ArrayList<>(pages);
      }

      public LinearFlow(WizardPane... pages) {
         this(Arrays.asList(pages));
      }

      @Override
      public Optional<WizardPane> advance(WizardPane currentPage) {
         int pageIndex = this.pages.indexOf(currentPage);
         return Optional.ofNullable(this.pages.get(++pageIndex));
      }

      @Override
      public boolean canAdvance(WizardPane currentPage) {
         int pageIndex = this.pages.indexOf(currentPage);
         return this.pages.size() - 1 > pageIndex;
      }
   }
}
