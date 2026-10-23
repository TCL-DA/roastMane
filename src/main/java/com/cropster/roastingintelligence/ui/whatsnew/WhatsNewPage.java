package com.cropster.roastingintelligence.ui.whatsnew;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import java.net.URL;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javafx.scene.Node;
import org.controlsfx.control.action.Action;

public interface WhatsNewPage {
   Pattern PAGE_INDEX_PATTERN = Pattern.compile("N(\\d{2,})_\\w+");

   static WhatsNewPage build(final int index, final String title, final String... content) {
      Objects.requireNonNull(title, "Null 'title' parameter");
      return new WhatsNewPage() {
         @Override
         public String[] content() {
            return content;
         }

         @Override
         public int identifier() {
            return index;
         }

         @Override
         public String title() {
            return title;
         }
      };
   }

   static WhatsNewPage build(final int index, final String title, final URL image, final Action action, final boolean needsAttention, final String... content) {
      Objects.requireNonNull(title, "Null 'title' parameter");
      return new WhatsNewPage() {
         @Override
         public Action action() {
            return action;
         }

         @Override
         public String[] content() {
            return content;
         }

         @Override
         public URL image() {
            return image;
         }

         @Override
         public int identifier() {
            return index;
         }

         @Override
         public boolean needsAttention() {
            return needsAttention;
         }

         @Override
         public String title() {
            return title;
         }
      };
   }

   String[] content();

   String title();

   default Action action() {
      return null;
   }

   default Node[] contentNodes() {
      String[] content = this.content();
      return content != null && content.length != 0
         ? Stream.of(content).flatMap(t -> JavaFxUtil.parseHtmlToTextNodes(t).stream()).toArray(Node[]::new)
         : new Node[0];
   }

   default boolean disabled() {
      return false;
   }

   default URL image() {
      return null;
   }

   default int identifier() {
      Matcher matcher = PAGE_INDEX_PATTERN.matcher(this.getClass().getSimpleName());
      if (matcher.matches()) {
         return Integer.parseInt(matcher.group(1));
      } else {
         throw new IllegalStateException("Page index not parsable from " + this.getClass().getSimpleName());
      }
   }

   default boolean needsAttention() {
      return false;
   }
}
