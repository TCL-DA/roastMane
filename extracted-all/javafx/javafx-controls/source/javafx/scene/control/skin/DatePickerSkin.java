package javafx.scene.control.skin;

import com.sun.javafx.scene.control.DatePickerContent;
import com.sun.javafx.scene.control.DatePickerHijrahContent;
import com.sun.javafx.scene.control.behavior.ComboBoxBaseBehavior;
import com.sun.javafx.scene.control.behavior.DatePickerBehavior;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.chrono.HijrahChronology;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

public class DatePickerSkin extends ComboBoxPopupControl<LocalDate> {
   private final DatePicker datePicker;
   private TextField displayNode;
   private DatePickerContent datePickerContent;
   private final DatePickerBehavior behavior;

   public DatePickerSkin(DatePicker var1) {
      super(var1);
      this.datePicker = var1;
      this.behavior = new DatePickerBehavior(var1);
      this.arrow
         .paddingProperty()
         .addListener(
            new InvalidationListener() {
               private boolean rounding = false;

               public void invalidated(Observable var1) {
                  if (!this.rounding) {
                     Insets var2 = DatePickerSkin.this.arrow.getPadding();
                     Insets var3 = new Insets(
                        (double)Math.round(var2.getTop()),
                        (double)Math.round(var2.getRight()),
                        (double)Math.round(var2.getBottom()),
                        (double)Math.round(var2.getLeft())
                     );
                     if (!var3.equals(var2)) {
                        this.rounding = true;
                        DatePickerSkin.this.arrow.setPadding(var3);
                        this.rounding = false;
                     }
                  }
               }
            }
         );
      this.registerChangeListener(var1.chronologyProperty(), var1x -> {
         this.updateDisplayNode();
         this.datePickerContent = null;
         this.popup = null;
      });
      this.registerChangeListener(var1.converterProperty(), var1x -> this.updateDisplayNode());
      this.registerChangeListener(var1.dayCellFactoryProperty(), var1x -> {
         this.updateDisplayNode();
         this.datePickerContent = null;
         this.popup = null;
      });
      this.registerChangeListener(var1.showWeekNumbersProperty(), var1x -> {
         if (this.datePickerContent != null) {
            this.datePickerContent.updateGrid();
            this.datePickerContent.updateWeeknumberDateCells();
         }
      });
      this.registerChangeListener(var1.valueProperty(), var2 -> {
         this.updateDisplayNode();
         if (this.datePickerContent != null) {
            LocalDate var3 = var1.getValue();
            this.datePickerContent.displayedYearMonthProperty().set(var3 != null ? YearMonth.from(var3) : YearMonth.now());
            this.datePickerContent.updateValues();
         }

         var1.fireEvent(new ActionEvent());
      });
      this.registerChangeListener(var1.showingProperty(), var2 -> {
         if (var1.isShowing()) {
            if (this.datePickerContent != null) {
               LocalDate var3 = var1.getValue();
               this.datePickerContent.displayedYearMonthProperty().set(var3 != null ? YearMonth.from(var3) : YearMonth.now());
               this.datePickerContent.updateValues();
            }

            this.show();
         } else {
            this.hide();
         }
      });
      if (var1.isShowing()) {
         this.show();
      }
   }

   @Override
   public void dispose() {
      super.dispose();
      if (this.behavior != null) {
         this.behavior.dispose();
      }
   }

   @Override
   public Node getPopupContent() {
      if (this.datePickerContent == null) {
         if (this.datePicker.getChronology() instanceof HijrahChronology) {
            this.datePickerContent = new DatePickerHijrahContent(this.datePicker);
         } else {
            this.datePickerContent = new DatePickerContent(this.datePicker);
         }
      }

      return this.datePickerContent;
   }

   @Override
   protected double computeMinWidth(double var1, double var3, double var5, double var7, double var9) {
      return 50.0;
   }

   @Override
   public void show() {
      super.show();
      this.datePickerContent.clearFocus();
   }

   @Override
   protected TextField getEditor() {
      return ((DatePicker)this.getSkinnable()).getEditor();
   }

   @Override
   protected StringConverter<LocalDate> getConverter() {
      return ((DatePicker)this.getSkinnable()).getConverter();
   }

   @Override
   public Node getDisplayNode() {
      if (this.displayNode == null) {
         this.displayNode = this.getEditableInputNode();
         this.displayNode.getStyleClass().add("date-picker-display-node");
         this.updateDisplayNode();
      }

      this.displayNode.setEditable(this.datePicker.isEditable());
      return this.displayNode;
   }

   @Override
   void focusLost() {
   }

   @Override
   ComboBoxBaseBehavior getBehavior() {
      return this.behavior;
   }
}
