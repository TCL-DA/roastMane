package org.cropster.roastingintelligence.util;

import com.cropster.util.model.Unit;
import com.cropster.util.text.DurationFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParsePosition;
import org.cropster.roastingintelligence.model.Measure;

public final class MeasureFormat extends Format {
   private static final long serialVersionUID = -5404184860949765542L;
   protected final NumberFormat _numberFormat;
   protected DurationFormat _durationFormat;
   private final int _maxUnitLength;

   public static MeasureFormat getInstance() {
      return new MeasureFormat();
   }

   public static MeasureFormat getInstance(int maxUnitLength) {
      return new MeasureFormat(maxUnitLength);
   }

   private MeasureFormat() {
      this(-1);
   }

   private MeasureFormat(int maxUnitLength) {
      this._maxUnitLength = maxUnitLength;
      this._numberFormat = NumberFormat.getInstance();
      this._numberFormat.setMaximumFractionDigits(2);
   }

   public NumberFormat getNumberFormat() {
      return this._numberFormat;
   }

   @Override
   public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
      Measure measure = (Measure)obj;
      if (measure == null) {
         return toAppendTo;
      } else {
         String value = this.formatValue(measure);
         return measure.getUnit().equals(Unit.DURATION) ? toAppendTo.append(value) : toAppendTo.append(String.format("%s %s", value, this.formatUnit(measure)));
      }
   }

   public String formatValue(Measure measure) {
      if (measure.getUnit().equals(Unit.DURATION)) {
         if (this._durationFormat == null) {
            this._durationFormat = DurationFormat.getDurationInstance(true);
         }

         return this._durationFormat.format(measure.getAmount() * 1000.0);
      } else {
         double amount = measure.getAmount();
         return this._numberFormat.format(amount == 0.0 ? 0.0 : amount);
      }
   }

   public String formatUnit(Measure measure) {
      if (measure != null && !measure.getUnit().equals(Unit.DURATION)) {
         String unitText = org.cropster.roastingintelligence.util.Messages.m_(measure.getUnit());
         if (this._maxUnitLength > 0 && unitText.length() > this._maxUnitLength) {
            unitText = unitText.substring(0, this._maxUnitLength) + ".";
         }

         return unitText;
      } else {
         return "";
      }
   }

   @Override
   public Object parseObject(String source, ParsePosition pos) {
      throw new UnsupportedOperationException();
   }
}
