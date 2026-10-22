package impl.org.controlsfx.tableview2.filter.filtermenubutton;

import impl.org.controlsfx.i18n.Localization;
import impl.org.controlsfx.tableview2.filter.parser.aggregate.AggregatorsParser;
import impl.org.controlsfx.tableview2.filter.parser.number.NumberParser;
import impl.org.controlsfx.tableview2.filter.parser.string.StringParser;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.util.StringConverter;
import org.controlsfx.control.tableview2.filter.filtermenubutton.FilterMenuButton;

public class DefaultFilterMenuButtonFactory {
   private static final List<Class<?>> PRIMITIVE_NUMERIC_TYPES = Arrays.asList(byte.class, short.class, int.class, long.class, float.class, double.class);

   public static FilterMenuButton forClass(Class<?> clazz) {
      return (FilterMenuButton)(isNumber(clazz)
         ? new DefaultFilterMenuButtonFactory.FilterNumberMenuButton()
         : new DefaultFilterMenuButtonFactory.FilterStringMenuButton());
   }

   private static boolean isNumber(Class<?> type) {
      return type == null ? false : Number.class.isAssignableFrom(type) || PRIMITIVE_NUMERIC_TYPES.stream().anyMatch(cls -> type == cls);
   }

   private static class FilterNumberMenuButton extends FilterMenuButton {
      private final List<MenuItem> items;
      private NumberParser<? extends Number> operator = new NumberParser<>();
      private String operatorString = "";

      public FilterNumberMenuButton() {
         this.getStyleClass().add("filter-menu-button");
         ToggleGroup group = new ToggleGroup();
         this.items = this.operator.operators().stream().filter(opr -> AggregatorsParser.getStrings().noneMatch(s -> s.equals(opr))).map(op -> {
            RadioButton radioButton = new RadioButton(op);
            radioButton.setToggleGroup(group);
            return new CustomMenuItem(radioButton);
         }).collect(Collectors.toList());
         this.getItems().addAll(this.items);
         group.selectedToggleProperty().addListener((obs, ov, nv) -> {
            RadioButton radioButton = (RadioButton)nv;
            this.operatorString = radioButton.getText();
            this.buttonText.set(this.operator.getSymbol(this.operatorString));
         });
         ((Toggle)group.getToggles().get(0)).setSelected(true);
      }

      @Override
      public Predicate<? extends Number> parse(String text) {
         return this.operator.parse(this.operatorString + " " + text);
      }

      @Override
      public Predicate<?> parse(String text, StringConverter<?> converter) {
         return this.parse(text);
      }

      @Override
      public String getErrorMessage() {
         return this.operator.getErrorMessage();
      }
   }

   private static class FilterStringMenuButton extends FilterMenuButton {
      private final List<MenuItem> items;
      private StringParser<?> operator = new StringParser();
      private String operatorString = "";
      private final CheckBox caseSensitive;

      public FilterStringMenuButton() {
         this.getStyleClass().add("filter-menu-button");
         this.caseSensitive = new CheckBox(Localization.localize(Localization.asKey("southfilter.menubutton.checkbox.sensitive")));
         CustomMenuItem customMenuItem = new CustomMenuItem(this.caseSensitive);
         customMenuItem.setHideOnClick(false);
         this.getItems().add(customMenuItem);
         this.getItems().add(new SeparatorMenuItem());
         ToggleGroup group = new ToggleGroup();
         this.items = this.operator.operators().stream().filter(opr -> AggregatorsParser.getStrings().noneMatch(s -> s.equals(opr))).map(op -> {
            RadioButton radioButton = new RadioButton(op);
            radioButton.setToggleGroup(group);
            return new CustomMenuItem(radioButton);
         }).collect(Collectors.toList());
         this.getItems().addAll(this.items);
         group.selectedToggleProperty().addListener((obs, ov, nv) -> this.updateSymbol((RadioButton)nv));
         ((Toggle)group.getToggles().get(0)).setSelected(true);
         this.caseSensitive.selectedProperty().addListener((obs, ov, nv) -> this.updateSymbol((RadioButton)group.getSelectedToggle()));
      }

      private void updateSymbol(RadioButton radioButton) {
         this.operator.setCaseSensitive(this.caseSensitive.isSelected());
         this.operatorString = radioButton.getText();
         this.buttonText.set(this.operator.getSymbol(radioButton.getText()));
         this.setTooltip(
            new Tooltip(
               this.operatorString
                  + " ("
                  + Localization.localize(Localization.asKey("southfilter.menubutton.sensitive." + (this.caseSensitive.isSelected() ? "enabled" : "disabled")))
                  + ")"
            )
         );
      }

      @Override
      public Predicate parse(String text) {
         return this.parse(text, null);
      }

      @Override
      public Predicate<?> parse(String text, StringConverter converter) {
         this.operator.setCaseSensitive(this.caseSensitive.isSelected());
         this.operator.setConverter(converter);
         return this.operator.parse(this.operatorString + " \"" + text + "\"");
      }

      @Override
      public String getErrorMessage() {
         return this.operator.getErrorMessage();
      }
   }
}
