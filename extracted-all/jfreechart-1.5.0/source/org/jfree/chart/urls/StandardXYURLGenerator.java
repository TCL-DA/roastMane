package org.jfree.chart.urls;

import java.io.Serializable;
import org.jfree.chart.util.Args;
import org.jfree.chart.util.ObjectUtils;
import org.jfree.data.xy.XYDataset;

public class StandardXYURLGenerator implements XYURLGenerator, Serializable {
   private static final long serialVersionUID = -1771624523496595382L;
   public static final String DEFAULT_PREFIX = "index.html";
   public static final String DEFAULT_SERIES_PARAMETER = "series";
   public static final String DEFAULT_ITEM_PARAMETER = "item";
   private String prefix;
   private String seriesParameterName;
   private String itemParameterName;

   public StandardXYURLGenerator() {
      this("index.html", "series", "item");
   }

   public StandardXYURLGenerator(String prefix) {
      this(prefix, "series", "item");
   }

   public StandardXYURLGenerator(String prefix, String seriesParameterName, String itemParameterName) {
      Args.nullNotPermitted(prefix, "prefix");
      Args.nullNotPermitted(seriesParameterName, "seriesParameterName");
      Args.nullNotPermitted(itemParameterName, "itemParameterName");
      this.prefix = prefix;
      this.seriesParameterName = seriesParameterName;
      this.itemParameterName = itemParameterName;
   }

   @Override
   public String generateURL(XYDataset dataset, int series, int item) {
      String url = this.prefix;
      boolean firstParameter = url.indexOf("?") == -1;
      url = url + (firstParameter ? "?" : "&amp;");
      return url + this.seriesParameterName + "=" + series + "&amp;" + this.itemParameterName + "=" + item;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof StandardXYURLGenerator)) {
         return false;
      } else {
         StandardXYURLGenerator that = (StandardXYURLGenerator)obj;
         if (!ObjectUtils.equal(that.prefix, this.prefix)) {
            return false;
         } else {
            return !ObjectUtils.equal(that.seriesParameterName, this.seriesParameterName)
               ? false
               : ObjectUtils.equal(that.itemParameterName, this.itemParameterName);
         }
      }
   }
}
