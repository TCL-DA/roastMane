package com.cropster.roastingintelligence.ui.components;

import java.awt.Desktop;
import java.net.URI;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.css.PseudoClass;
import javafx.css.StyleableProperty;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.scene.Cursor;
import javafx.scene.text.Text;
import org.apache.commons.lang3.StringUtils;
import org.cropster.roastingintelligence.RoastingIntelligenceApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HyperlinkText extends Text {
   private static final String DEFAULT_STYLE_CLASS = "hyperlink-text";
   private static final PseudoClass PSEUDO_CLASS_VISITED = PseudoClass.getPseudoClass("visited");
   private static final Logger LOGGER = LoggerFactory.getLogger(HyperlinkText.class);
   private final StringProperty url = new SimpleStringProperty(this, "url");
   private BooleanProperty visited;

   public HyperlinkText() {
      this.initialize();
   }

   public HyperlinkText(String text) {
      super(text);
      this.initialize();
   }

   public HyperlinkText(String text, String url) {
      this(text);
      this.setUrl(url);
   }

   public StringProperty urlProperty() {
      return this.url;
   }

   public String getUrl() {
      return (String)this.url.get();
   }

   public void setUrl(String url) {
      this.url.set(url);
   }

   private void initialize() {
      this.getStyleClass().setAll(new String[]{"hyperlink-text"});
      this.setAccessibleRole(AccessibleRole.HYPERLINK);
      ((StyleableProperty)this.cursorProperty()).applyStyle(null, Cursor.HAND);
      this.setOnMouseClicked(e -> this.handleClicked(this.getUrl()));
   }

   private void handleClicked(String url) {
      if (!this.isDisabled() && !StringUtils.isBlank(url)) {
         if (this.visited == null || !this.visited.isBound()) {
            this.setVisited(true);
         }

         performUrlNavigation(url);
      }
   }

   private static void performUrlNavigation(String url) {
      if (url.startsWith("mailto:")) {
         Platform.runLater(() -> {
            try {
               Desktop.getDesktop().mail(new URI(url));
            } catch (Exception var2) {
               LOGGER.error("Error opening mail client: {}", url, var2);
            }
         });
      } else {
         Platform.runLater(() -> {
            try {
               RoastingIntelligenceApp.showDocument(url);
            } catch (Exception var4) {
               try {
                  Desktop.getDesktop().browse(URI.create(url));
               } catch (Exception var3) {
                  LOGGER.error("Error opening url: {}", url, var3);
               }
            }
         });
      }
   }

   public final BooleanProperty visitedProperty() {
      if (this.visited == null) {
         this.visited = new BooleanPropertyBase() {
            protected void invalidated() {
               HyperlinkText.this.pseudoClassStateChanged(HyperlinkText.PSEUDO_CLASS_VISITED, this.get());
            }

            public Object getBean() {
               return HyperlinkText.this;
            }

            public String getName() {
               return "visited";
            }
         };
      }

      return this.visited;
   }

   public final void setVisited(boolean value) {
      this.visitedProperty().set(value);
   }

   public final boolean isVisited() {
      return this.visited == null ? false : this.visited.get();
   }

   protected Cursor getInitialCursor() {
      return Cursor.HAND;
   }

   public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
      switch (attribute) {
         case VISITED:
            return this.isVisited();
         default:
            return super.queryAccessibleAttribute(attribute, parameters);
      }
   }
}
