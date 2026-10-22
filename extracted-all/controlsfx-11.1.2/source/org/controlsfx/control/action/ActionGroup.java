package org.controlsfx.control.action;

import java.util.Arrays;
import java.util.Collection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;

public class ActionGroup extends Action {
   private final ObservableList<Action> actions = FXCollections.observableArrayList();

   public ActionGroup(String text, Action... actions) {
      this(text, Arrays.asList(actions));
   }

   public ActionGroup(String text, Collection<Action> actions) {
      super(text);
      this.getActions().addAll(actions);
   }

   public ActionGroup(String text, Node icon, Action... actions) {
      this(text, icon, Arrays.asList(actions));
   }

   public ActionGroup(String text, Node icon, Collection<Action> actions) {
      super(text);
      this.setGraphic(icon);
      this.getActions().addAll(actions);
   }

   public final ObservableList<Action> getActions() {
      return this.actions;
   }

   @Override
   public String toString() {
      return this.getText();
   }
}
