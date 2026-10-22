package impl.org.controlsfx.tableview2.filter.popupfilter;

import impl.org.controlsfx.i18n.Localization;
import impl.org.controlsfx.tableview2.filter.parser.aggregate.AggregatorsParser;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javafx.beans.binding.Bindings;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.controlsfx.control.tableview2.FilteredTableColumn;
import org.controlsfx.control.tableview2.filter.popupfilter.PopupFilter;
import org.controlsfx.control.tableview2.filter.popupfilter.PopupStringFilter;
import org.controlsfx.control.textfield.TextFields;

public class PopupFilterSkin<S, T> implements Skin<PopupFilter<S, T>> {
   private static final PseudoClass PSEUDO_CLASS_ERROR = PseudoClass.getPseudoClass("error");
   private final PopupFilter<S, T> popupFilter;
   private final TextField filterTextField;
   private final VBox container;
   private final HBox filterContainer;

   public PopupFilterSkin(PopupFilter<S, T> popupFilter) {
      this.popupFilter = popupFilter;
      this.filterTextField = TextFields.createClearableTextField();
      this.filterTextField.textProperty().bindBidirectional(popupFilter.textProperty());
      this.filterTextField.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
         if (e.getCode() == KeyCode.ESCAPE) {
            popupFilter.hide();
            e.consume();
         }
      });
      this.updateFilterTextField();
      FlowPane availableFilters = new FlowPane(5.0, 5.0);
      availableFilters.getChildren().addAll(this.getOperations(popupFilter.getOperations()));
      this.container = new VBox();
      this.filterContainer = new HBox(new Node[]{this.filterTextField});
      HBox.setHgrow(this.filterTextField, Priority.ALWAYS);
      Bindings.bindContent(this.container.getStyleClass(), popupFilter.getStyleClass());
      this.container.getStylesheets().add(PopupFilter.class.getResource("/org/controlsfx/control/tableview2/popupfilter.css").toExternalForm());
      this.container.getChildren().addAll(new Node[]{this.filterContainer, availableFilters});
      this.addRemoveCaseButton();
      this.initializeListeners();
   }

   private void initializeListeners() {
      this.filterTextField.textProperty().addListener((o, ov, nv) -> this.updateFilterTextField());
   }

   private void updateFilterTextField() {
      String text = this.filterTextField.getText();
      if (text != null && !text.isEmpty()) {
         if (this.popupFilter.getParser().isValid(text)) {
            this.filterTextField.setTooltip(null);
            this.filterTextField.pseudoClassStateChanged(PSEUDO_CLASS_ERROR, false);
         } else {
            String validateMessage = this.popupFilter.getParser().getErrorMessage();
            this.filterTextField.setTooltip(new Tooltip(validateMessage));
            this.filterTextField.pseudoClassStateChanged(PSEUDO_CLASS_ERROR, true);
         }
      } else {
         this.filterTextField.setTooltip(null);
         this.filterTextField.pseudoClassStateChanged(PSEUDO_CLASS_ERROR, false);
      }
   }

   private void addRemoveCaseButton() {
      if (this.popupFilter instanceof PopupStringFilter) {
         this.filterContainer.getChildren().add(this.createCaseButton());
      }
   }

   private ToggleButton createCaseButton() {
      ToggleButton toggleButton = new ToggleButton("Aa");
      toggleButton.setToggleGroup(new ToggleGroup());
      Tooltip enableCaseSensitive = new Tooltip(Localization.localize(Localization.asKey("popup.filter.case.sensitive.enable")));
      Tooltip disableCaseSensitive = new Tooltip(Localization.localize(Localization.asKey("popup.filter.case.sensitive.disable")));
      toggleButton.setTooltip(disableCaseSensitive);
      ((PopupStringFilter)this.popupFilter).caseSensitiveProperty().bind(toggleButton.selectedProperty().not());
      toggleButton.selectedProperty().addListener((o, ov, nv) -> {
         this.updatePredicate();
         this.updateFilterTextField();
         if (nv) {
            toggleButton.setTooltip(enableCaseSensitive);
         } else {
            toggleButton.setTooltip(disableCaseSensitive);
         }
      });
      return toggleButton;
   }

   private void updatePredicate() {
      FilteredTableColumn<S, T> tableColumn = this.popupFilter.getTableColumn();
      String text = this.filterTextField.getText();
      if (text != null && !text.isEmpty()) {
         tableColumn.setPredicate(this.getSkinnable().getParser().parse(text));
      } else {
         tableColumn.setPredicate(null);
      }
   }

   public PopupFilter<S, T> getSkinnable() {
      return this.popupFilter;
   }

   public Node getNode() {
      return this.container;
   }

   public void dispose() {
   }

   private Collection<? extends Node> getOperations(List<String> operations) {
      return operations.stream().map(this::createLeafNode).collect(Collectors.toList());
   }

   private Label createLeafNode(String text) {
      Label label = new Label(text);
      label.setOnMousePressed(e -> {
         this.filterTextField.insertText(this.filterTextField.getCaretPosition(), text);
         if (AggregatorsParser.getStrings().noneMatch(s -> s.equals(text))) {
            this.positionCaret();
         }
      });
      label.getStyleClass().add("operation");
      return label;
   }

   private void positionCaret() {
      if (this.getSkinnable() instanceof PopupStringFilter) {
         this.filterTextField.insertText(this.filterTextField.getCaretPosition(), " \"\"");
         this.filterTextField.positionCaret(this.filterTextField.getCaretPosition() - 1);
      }
   }
}
