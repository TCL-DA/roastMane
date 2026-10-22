package org.controlsfx.tools;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Slider;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeView;
import javafx.util.Callback;

public class ValueExtractor {
   private static List<ValueExtractor.ObservableValueExtractor> extractors = FXCollections.observableArrayList();
   private static final List<ValueExtractor.NodeValueExtractor> valueExtractors = FXCollections.observableArrayList();

   public static void addObservableValueExtractor(Predicate<Control> test, Callback<Control, ObservableValue<?>> extract) {
      extractors.add(0, new ValueExtractor.ObservableValueExtractor(test, extract));
   }

   public static final Optional<Callback<Control, ObservableValue<?>>> getObservableValueExtractor(Control c) {
      for (ValueExtractor.ObservableValueExtractor e : extractors) {
         if (e.applicability.test(c)) {
            return Optional.of(e.extraction);
         }
      }

      return Optional.empty();
   }

   private ValueExtractor() {
   }

   public static void addValueExtractor(Predicate<Node> test, Callback<Node, Object> extractor) {
      valueExtractors.add(0, new ValueExtractor.NodeValueExtractor(test, extractor));
   }

   public static Object getValue(Node n) {
      for (ValueExtractor.NodeValueExtractor nve : valueExtractors) {
         if (nve.applicability.test(n)) {
            return nve.extraction.call(n);
         }
      }

      return null;
   }

   static {
      addObservableValueExtractor(c -> c instanceof TextInputControl, c -> ((TextInputControl)c).textProperty());
      addObservableValueExtractor(c -> c instanceof ComboBox, c -> ((ComboBox)c).valueProperty());
      addObservableValueExtractor(c -> c instanceof ChoiceBox, c -> ((ChoiceBox)c).valueProperty());
      addObservableValueExtractor(c -> c instanceof CheckBox, c -> ((CheckBox)c).selectedProperty());
      addObservableValueExtractor(c -> c instanceof Slider, c -> ((Slider)c).valueProperty());
      addObservableValueExtractor(c -> c instanceof ColorPicker, c -> ((ColorPicker)c).valueProperty());
      addObservableValueExtractor(c -> c instanceof DatePicker, c -> ((DatePicker)c).valueProperty());
      addObservableValueExtractor(c -> c instanceof ListView, c -> ((ListView)c).itemsProperty());
      addObservableValueExtractor(c -> c instanceof TableView, c -> ((TableView)c).itemsProperty());
      addValueExtractor(n -> n instanceof CheckBox, cb -> ((CheckBox)cb).isSelected());
      addValueExtractor(n -> n instanceof ChoiceBox, cb -> ((ChoiceBox)cb).getValue());
      addValueExtractor(n -> n instanceof ComboBox, cb -> ((ComboBox)cb).getValue());
      addValueExtractor(n -> n instanceof DatePicker, dp -> ((DatePicker)dp).getValue());
      addValueExtractor(n -> n instanceof RadioButton, rb -> ((RadioButton)rb).isSelected());
      addValueExtractor(n -> n instanceof Slider, sl -> ((Slider)sl).getValue());
      addValueExtractor(n -> n instanceof TextInputControl, ta -> ((TextInputControl)ta).getText());
      addValueExtractor(n -> n instanceof ListView, lv -> {
         MultipleSelectionModel<?> sm = ((ListView)lv).getSelectionModel();
         return sm.getSelectionMode() == SelectionMode.MULTIPLE ? sm.getSelectedItems() : sm.getSelectedItem();
      });
      addValueExtractor(n -> n instanceof TreeView, tv -> {
         MultipleSelectionModel<?> sm = ((TreeView)tv).getSelectionModel();
         return sm.getSelectionMode() == SelectionMode.MULTIPLE ? sm.getSelectedItems() : sm.getSelectedItem();
      });
      addValueExtractor(n -> n instanceof TableView, tv -> {
         MultipleSelectionModel<?> sm = ((TableView)tv).getSelectionModel();
         return sm.getSelectionMode() == SelectionMode.MULTIPLE ? sm.getSelectedItems() : sm.getSelectedItem();
      });
      addValueExtractor(n -> n instanceof TreeTableView, tv -> {
         MultipleSelectionModel<?> sm = ((TreeTableView)tv).getSelectionModel();
         return sm.getSelectionMode() == SelectionMode.MULTIPLE ? sm.getSelectedItems() : sm.getSelectedItem();
      });
   }

   private static class NodeValueExtractor {
      public final Predicate<Node> applicability;
      public final Callback<Node, Object> extraction;

      public NodeValueExtractor(Predicate<Node> applicability, Callback<Node, Object> extraction) {
         this.applicability = Objects.requireNonNull(applicability);
         this.extraction = Objects.requireNonNull(extraction);
      }
   }

   private static class ObservableValueExtractor {
      public final Predicate<Control> applicability;
      public final Callback<Control, ObservableValue<?>> extraction;

      public ObservableValueExtractor(Predicate<Control> applicability, Callback<Control, ObservableValue<?>> extraction) {
         this.applicability = Objects.requireNonNull(applicability);
         this.extraction = Objects.requireNonNull(extraction);
      }
   }
}
