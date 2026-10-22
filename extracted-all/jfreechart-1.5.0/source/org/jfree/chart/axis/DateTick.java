package org.jfree.chart.axis;

import java.util.Date;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.chart.util.Args;
import org.jfree.chart.util.ObjectUtils;

public class DateTick extends ValueTick {
   private Date date;

   public DateTick(Date date, String label, TextAnchor textAnchor, TextAnchor rotationAnchor, double angle) {
      this(TickType.MAJOR, date, label, textAnchor, rotationAnchor, angle);
   }

   public DateTick(TickType tickType, Date date, String label, TextAnchor textAnchor, TextAnchor rotationAnchor, double angle) {
      super(tickType, (double)date.getTime(), label, textAnchor, rotationAnchor, angle);
      Args.nullNotPermitted(tickType, "tickType");
      this.date = date;
   }

   public Date getDate() {
      return this.date;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof DateTick)) {
         return false;
      } else {
         DateTick that = (DateTick)obj;
         return !ObjectUtils.equal(this.date, that.date) ? false : super.equals(obj);
      }
   }

   @Override
   public int hashCode() {
      return this.date.hashCode();
   }
}
